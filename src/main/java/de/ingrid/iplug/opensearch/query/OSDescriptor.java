/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
import java.util.Map;

/**
 * This class stores the values of the received Opensearch descriptor.
 * 
 * @author André Wallat
 *
 */
public class OSDescriptor {
	// remember which response-type we want to use
	private String usedType = "";

	// remember the url to each response-type
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
