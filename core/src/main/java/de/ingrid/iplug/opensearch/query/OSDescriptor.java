package de.ingrid.iplug.opensearch.query;

import java.util.HashMap;
import java.util.Map;

public class OSDescriptor {
	
	private final String DEFAULT_TYPE = "application/rss+xml";
	
	private Map<String, String> templateUrls = new HashMap<String, String>();
	
	public void setTypeAndUrl(String type, String url) {
		templateUrls.put(type, url);
	}
	
	public String getUrl(String type) {
		return templateUrls.get(type);
	}
	
	public String getUrl() {
		return getUrl(DEFAULT_TYPE);
	}
}
