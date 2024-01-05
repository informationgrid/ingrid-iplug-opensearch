/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.iplug.opensearch.query;

import de.ingrid.iplug.opensearch.Configuration;
import de.ingrid.iplug.opensearch.model.OSMapping;
import de.ingrid.iplug.opensearch.tools.QueryUtils;
import de.ingrid.iplug.opensearch.tools.StringUtils;
import de.ingrid.utils.query.IngridQuery;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Set;

/**
 * This class creates an URL for an Opensearch-Interface with all the parameters
 * coming from an IngridQuery.
 * @author André Wallat
 *
 */
public class OSRequest {
	/* The logging object
	 */
	private static final Log log = LogFactory.getLog(OSRequest.class);
	
	/**
	 * The template URL coming from the descriptor will be used to insert the
	 * parameters coming from the IngridQuery. The modified URL can be used to 
	 * be sent to an Opensearch-Interface.
	 * @param osQuery
	 * @param osDescriptor
	 * @return
	 */
	public static String getOSQueryString(OSQuery osQuery, IngridQuery ingridQuery, OSDescriptor osDescriptor, Configuration opensearchConfig) {
		String mergedResult 	= osDescriptor.getUrl();
		log.debug("OpenSearch Query before merging params: " + mergedResult);
		Set<String> paramKeys 	= osQuery.keySet();
		
		for (String param : paramKeys) {
			mergedResult = StringUtils.replaceParameter(mergedResult, param, osQuery.get(param));
		}
		
		// set ingrid specific parameters if they exist
		mergedResult = StringUtils.replaceParameter(mergedResult, "ingridsearch:georss", "1");
		mergedResult = StringUtils.replaceParameter(mergedResult, "ingridsearch:xml", "1");
		
		mergedResult = StringUtils.removeUnusedParameter(mergedResult);
		
		// add mapped parameters like domain, partner and provider
		String finalQueryString = addMappedParameters(mergedResult, ingridQuery, osQuery.getMapping(), true, opensearchConfig);
		
		log.debug("OpenSearch Query: " + finalQueryString);
		return finalQueryString;
	}

    public static String addMappedParameters(String term, IngridQuery ingridQuery,
                                             List<OSMapping> mapping, boolean asParam, Configuration opensearchConfig) {

        // do not map if it was deactivated
        if (!opensearchConfig.mappingSupport) return term;
        
        String connector  = "+";
        String definition = ":";
        if (asParam) {
            connector  = "&";
            definition = "=";
        }
        
        String parameter = "";
        
        for (OSMapping map : mapping) {
            String mappedParam = map.getMapping();
            // if mapping is active and shall be used INSIDE the query
            if (map.isActive() && (map.isAsParam() == asParam)) {
                switch (map.getType()) {
                case DOMAIN:
                    String site = QueryUtils.getFieldValue("site", ingridQuery);
                    if (site != null) {
                        if ("[]".equals(mappedParam))
                            parameter += site + connector;
                        else
                            parameter += mappedParam + definition + site + connector;
                    }
                    break;
                case PARTNER:
                    String[] partner = ingridQuery.getPositivePartner();
                    if (partner != null && partner.length > 0) {
                        if ("[]".equals(mappedParam))
                            parameter += partner[0] + connector;
                        else
                            parameter += mappedParam + definition + partner[0] + connector;
                    }
                    break;
                case PROVIDER:
                    String[] provider = ingridQuery.getPositiveProvider();
                    if (provider != null && provider.length > 0) {
                        if ("[]".equals(mappedParam))
                            parameter += provider[0] + connector;
                        else
                            parameter += mappedParam + definition + provider[0] + connector;
                    }
                    break;

                default:
                    // not supported ... shouldn't happen
                    log.error("Mapping-Type "+map.getType()+" not supported!");
                    break;
                }
            }
        }
        
        if (!parameter.isEmpty()) {            
            term += connector + parameter.substring(0, parameter.length()-connector.length());
        }
        return term;
    }
    
}
