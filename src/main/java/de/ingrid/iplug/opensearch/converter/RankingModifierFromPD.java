/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.iplug.opensearch.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.utils.IConfigurable;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.PlugDescription;

/**
 * This class is used to normalize a score received from search results.
 * It has to be two values, one for multiplying the score and one for adding
 * a value.
 * The values for the manipulation are received from a provided PlugDescription
 * with the keys 'rankingMul' and 'rankingAdd'.
 * 
 * normalizedScore = multiplier*score + additional
 * @author André Wallat
 */
public class RankingModifierFromPD implements RankingModifier, IConfigurable {
	private static Log log = LogFactory.getLog(RankingModifierFromPD.class);
	
	/** value for multiplying the score */
	private float multiplier;
	
	/** value for adding to the score */
	private float additional;
	
	/** The standard initialisation will not alter the score at all. */
	public RankingModifierFromPD() {
		this.multiplier = 1;
		this.additional = 0;
	}

	@Override
	public void initialize(IngridHit[] inHits) {
		// NOT NEEDED because our normalization is independent from other hits !
	}

	private float getMultiplier() {
		return multiplier;
	}

	private void setMultiplier(String multiplier) {
		if (multiplier != null) {
			this.multiplier = Float.parseFloat(multiplier);
		}
	}
	
	private float getAdditional() {
		return additional;
	}

	private void setAdditional(String additional) {
		if (additional != null) {
			this.additional = Float.parseFloat(additional);
		}
	}

	/**
	 * The configure method will always be called when PlugDescription
	 * has changed. This happens to all classes that implement the IConfigurable
	 * Interface. 
	 * 
	 * Here the values for adding and multiplying is received from the 
	 * PlugDescription.
	 * 
	 * @param plugDescription holds the values used for the manipulation
	 */
	@Override
	public void configure(PlugDescription plugDescription) {
		// get values from plugDescription if any
		if (!plugDescription.getRankingTypes()[0].equals("off")) {
			setMultiplier((String)plugDescription.get("rankingMul"));
			setAdditional((String)plugDescription.get("rankingAdd"));
		}
		log.info("RankingModifier was correctly configured from PlugDescription!");
	}

	@Override
	public float getNormalizedRanking(IngridHit hit) {
		return getMultiplier() * hit.getScore() + getAdditional();
	}
}
