package de.ingrid.iplug.opensearch.query;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OSQuery extends HashMap<String,String> {

    private static final long serialVersionUID = 7633577508030661464L;

    /* The logging object
	 */
	private static Log log = LogFactory.getLog(OSQuery.class);
	
	// official OpenSearch parameters for a query
	public static final String OS_SEARCH_TERMS 		= "searchTerms";
	public static final String OS_COUNT 			= "count";
	public static final String OS_START_INDEX		= "startIndex";
	public static final String OS_START_PAGE		= "startPage";
	public static final String OS_LANGUAGE 			= "language";
	public static final String OS_INPUT_ENCODING 	= "inputEncoding";
	public static final String OS_OUTPUT_ENCODING	= "outputEncoding";
	
	// extended OpenSearch parameters
	public static final String OS_GEO_BBOX			= "geo:box";
		
	public OSQuery() {}
	

	/**
	 * recognize all available parameters for an OpenSearch query 
	 */
	/*private void checkAvailableParameters() {
		String[] os_params = {OS_SEARCH_TERMS,OS_COUNT,OS_START_INDEX,OS_START_PAGE,
							  OS_LANGUAGE,OS_INPUT_ENCODING,OS_OUTPUT_ENCODING};
		for (String param : os_params) {
			if (StringUtils.parameterExists(templateQuery, param)) {
				templateParameter.add(param);
			}
		}
	}*/
	
}
