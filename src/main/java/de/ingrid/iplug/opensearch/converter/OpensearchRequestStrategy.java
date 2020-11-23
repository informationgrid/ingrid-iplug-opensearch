/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
package de.ingrid.iplug.opensearch.converter;

import java.util.List;

/**
 * This class holds a list of strategies, which conversion, and so which kind of
 * response shall be prefered.
 *  
 * @author André Wallat
 *
 */
public class OpensearchRequestStrategy {
	private List<String> strategy;

	public List<String> getStrategy() {
		return strategy;
	}

	public void setStrategy(List<String> strategy) {
		this.strategy = strategy;
	}
	
	
}
