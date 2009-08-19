package de.ingrid.iplug.opensearch.query;

import java.util.HashMap;
import java.util.Map;

public class OSDescriptor {
	
	private String usedType = "";

	private Map<String, String> templateUrls = new HashMap<String, String>();
	
	public void setTypeAndUrl(String type, String url) {
		templateUrls.put(type, url);
	}
	
	public String getUrl(String type) {
		return templateUrls.get(type);
	}
	
	public String getUrl() {
		return getUrl(getUsedType());
	}
	
	public boolean hasType(String type) {
		return templateUrls.containsKey(type);
	}
	
	public String getUsedType() {
		return usedType;
	}

	public void setUsedType(String usedType) {
		this.usedType = usedType;
	}
}
