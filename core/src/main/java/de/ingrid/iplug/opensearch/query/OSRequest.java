package de.ingrid.iplug.opensearch.query;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.iplug.opensearch.tools.StringUtils;

public class OSRequest {
	/* The logging object
	 */
	private static final Log log = LogFactory.getLog(OSRequest.class);
	
	public static String getOSQueryString(OSQuery osQuery, OSDescriptor osDescriptor) {
		String mergedResult 	= osDescriptor.getUrl();
		Set<String> paramKeys 	= osQuery.keySet();
		
		for (String param : paramKeys) {
			mergedResult = StringUtils.replaceParameter(mergedResult, param, osQuery.get(param));
		}
		
		String finalQueryString = StringUtils.removeUnusedParameter(mergedResult);
		log.debug("OpenSearch Query: " + finalQueryString);
		return finalQueryString;
	}
}
