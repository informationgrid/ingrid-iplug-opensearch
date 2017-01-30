/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
package de.ingrid.iplug.opensearch.query;

import java.util.HashMap;
import java.util.List;

import de.ingrid.iplug.opensearch.model.OSMapping;

/**
 * This class holds all parameters needed and understood by an Opensearch-
 * Interface.
 * @author André Wallat
 *
 */
public class OSQuery extends HashMap<String,String> {

    private static final long serialVersionUID = 7633577508030661464L;

	// official OpenSearch parameters for a query
	// the search terms
	public static final String OS_SEARCH_TERMS 		= "searchTerms";
	
	// how many hits shall be returned
	public static final String OS_COUNT 			= "count";
	
	// at which hit do we want to start
	public static final String OS_START_INDEX		= "startIndex";
	
	// at which page (=OS_START_INDEX/OS_COUNT) do we want to start
	public static final String OS_START_PAGE		= "startPage";
	
	// which language
	public static final String OS_LANGUAGE 			= "language";
	
	// what is the input encoding
	public static final String OS_INPUT_ENCODING 	= "inputEncoding";
	
	// what is the ouput encoding
	public static final String OS_OUTPUT_ENCODING	= "outputEncoding";
	
	// extended OpenSearch parameters
	// the bounding box
	public static final String OS_GEO_BBOX			= "geo:box";

	// 
    private List<OSMapping> mapping;
		
	public OSQuery() {}

	// set the mapping of special Ingrid fields to OS parameters
    public void setMapping(List<OSMapping> mapping) {
        this.mapping = mapping;
    }
    
    public List<OSMapping> getMapping() {
        return mapping;
    }
	
}
