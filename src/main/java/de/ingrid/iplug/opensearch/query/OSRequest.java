package de.ingrid.iplug.opensearch.query;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.iplug.opensearch.tools.StringUtils;

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
	public static String getOSQueryString(OSQuery osQuery, OSDescriptor osDescriptor) {
		String mergedResult 	= osDescriptor.getUrl();
		Set<String> paramKeys 	= osQuery.keySet();
		
		for (String param : paramKeys) {
			mergedResult = StringUtils.replaceParameter(mergedResult, param, osQuery.get(param));
		}
		
		// set ingrid specific parameters if they exist
		mergedResult = StringUtils.replaceParameter(mergedResult, "ingridsearch:georss", "1");
		mergedResult = StringUtils.replaceParameter(mergedResult, "ingridsearch:xml", "1");
		
		String finalQueryString = StringUtils.removeUnusedParameter(mergedResult);
		log.debug("OpenSearch Query: " + finalQueryString);
		return finalQueryString;
	}
}
