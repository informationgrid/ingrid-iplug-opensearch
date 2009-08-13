package de.ingrid.iplug.opensearch.query;

import java.util.Set;

import de.ingrid.iplug.opensearch.OSQuery;
import de.ingrid.iplug.opensearch.tools.StringUtils;

public class OSRequest {
	public static String getOSQueryString(OSQuery osQuery, OSDescriptor osDescriptor) {
		String mergedResult 	= osDescriptor.getUrl();
		Set<String> paramKeys 	= osQuery.keySet();
		
		for (String param : paramKeys) {
			mergedResult = StringUtils.replaceParameter(mergedResult, param, osQuery.get(param));
		}
		
		return StringUtils.removeUnusedParameter(mergedResult);
	}
}
