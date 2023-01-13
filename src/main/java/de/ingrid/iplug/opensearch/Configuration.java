/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.iplug.opensearch;

import de.ingrid.admin.IConfig;
import de.ingrid.admin.command.PlugdescriptionCommandObject;
import de.ingrid.utils.tool.PlugDescriptionUtil;
import de.ingrid.utils.tool.QueryUtil;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Properties;

@org.springframework.context.annotation.Configuration
public class Configuration implements IConfig {
    
    // private static Log log = LogFactory.getLog(Configuration.class);
    
    @Value("${plugdescription.mapping:}")
    public String mapping;
    
    @Value("${plugdescription.useDescriptor:false}")
    public boolean useDescriptor;
    
    @Value("${plugdescription.domainGroupingSupport:false}")
    public boolean domainGroupingSupport;

    @Value("${plugdescription.ranking:score}")
    public List<String> ranking;

    @Value("${plugdescription.rankingMul:1}")
    public String rankingMul;
    
    @Value("${plugdescription.rankingAdd:0}")
    public String rankingAdd;
    
    @Value("${plugdescription.mappingSupport:false}")
    public boolean mappingSupport;
    
    @Value("${plugdescription.serviceUrl:}")
    public String serviceUrl;

    @Override
    public void initialize() {
    }

    @Override
    public void addPlugdescriptionValues( PlugdescriptionCommandObject pdObject ) {
    	pdObject.put( "iPlugClass", "de.ingrid.iplug.opensearch.OpenSearchPlug");
        
        pdObject.put("rankingMul", rankingMul);
        pdObject.put("rankingAdd", rankingAdd);
        pdObject.putBoolean("useDescriptor", useDescriptor);
        pdObject.putBoolean("domainGroupingSupport", domainGroupingSupport);
        pdObject.put("serviceUrl", serviceUrl);
        
        // add necessary fields so iBus actually will query us
        // remove field first to prevent multiple equal entries
        // add field indicating query of SNS metadata, no impact on query
        PlugDescriptionUtil.addFieldToPlugDescription(pdObject, "incl_meta");
        
        // we also can process metainfo field ! so indicate this !
        // add "metainfo" field, so plug won't be filtered when field is part of query !
        PlugDescriptionUtil.addFieldToPlugDescription(pdObject, QueryUtil.FIELDNAME_METAINFO);
        
        // the field "isfolder" is now always request, so we have to add it
        PlugDescriptionUtil.addFieldToPlugDescription(pdObject, "isfolder");

        // add datatype opensearch to PD
        pdObject.addDataType("opensearch");
        
        // FIXME: use latest basewebapp to remove this line (wrong mapping of ranking:off, was 'notRanked')
        if (pdObject.getRankingTypes().length == 0) {
            pdObject.setRankinTypes( false, false, true );
        } else {
            for (String type : this.ranking) {
                pdObject.addToList("ranking", type);
            }
        }
    }

    @Override
    public void setPropertiesFromPlugdescription( Properties props, PlugdescriptionCommandObject pd ) {
    	if(pd.get("mapping") != null){
        	props.setProperty("plugdescription.mapping", mapping);
    	}
    	props.setProperty("plugdescription.rankingMul", rankingMul);
        props.setProperty("plugdescription.rankingAdd", rankingAdd);
        props.setProperty("plugdescription.useDescriptor", String.valueOf(useDescriptor));
        props.setProperty("plugdescription.mappingSupport", String.valueOf(mappingSupport));
        props.setProperty("plugdescription.domainGroupingSupport", String.valueOf(domainGroupingSupport));
        props.setProperty("plugdescription.serviceUrl", serviceUrl);
    }
}
