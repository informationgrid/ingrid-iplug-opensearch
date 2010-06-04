package de.ingrid.iplug.opensearch.query;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class holds all parameters needed and understood by an Opensearch-
 * Interface.
 * @author André Wallat
 *
 */
public class OSQuery extends HashMap<String,String> {

    private static final long serialVersionUID = 7633577508030661464L;

    /* The logging object
	 */
	private static Log log = LogFactory.getLog(OSQuery.class);
	
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
		
	public OSQuery() {}
	
}
