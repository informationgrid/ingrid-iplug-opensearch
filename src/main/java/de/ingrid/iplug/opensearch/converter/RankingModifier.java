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
package de.ingrid.iplug.opensearch.converter;

import de.ingrid.utils.IngridHit;

/**
 * The interface of how the modifiers for score of a hit shall be created.
 * @author André Wallat
 *
 */
public interface RankingModifier {

	/** Pass the result of the query already processed by former ranking modifiers.
	 * So the RankingModifier can analyze the current state if needed for normalization !
	 * @param inHits the result of the OpenSearch query already processed by former ranking modifiers
	 * 		(or original result if no former modifiers)
	 */
	void initialize(IngridHit[] inHits);

	/**
	 * This function computes and returns the new score !
	 * NOTICE: does NOT set the new score in the hit !
	 * @param hit hit delivering original score or score as it was processed by former ranking modifiers
	 * @return new score after processing of this modifier (NOT set in hit yet !)
	 */
	float getNormalizedRanking(IngridHit hit);
}
