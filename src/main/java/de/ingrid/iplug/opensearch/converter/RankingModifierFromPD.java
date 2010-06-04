package de.ingrid.iplug.opensearch.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.utils.IConfigurable;
import de.ingrid.utils.PlugDescription;

/**
 * This class is used to normalize a score received from search results. The values
 * for the manipulation are received from a provided PlugDescription with the keys
 * 'rankingMul' and 'rankingAdd'.
 * 
 * normalizedScore = multiplier*score + additional
 * @author André Wallat
 *
 */
public class RankingModifierFromPD implements RankingModifier, IConfigurable {
	private static Log log = LogFactory.getLog(RankingModifierFromPD.class);
	
	// value for multiplying the score
	private float multiplier;
	
	// value for adding to the score
	private float additional;
	
	/**
	 * The standard initialisation will not alter the score at all.
	 */
	public RankingModifierFromPD() {
		this.multiplier = 1;
		this.additional = 0;
	}
	
	public float getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}
	
	public void setMultiplier(String multiplier) {
		if (multiplier != null) {
			this.multiplier = Float.parseFloat(multiplier);
		}
	}

	public float getAdditional() {
		
		return additional;
	}

	public void setAdditional(float additional) {
		this.additional = additional;
	}
	
	public void setAdditional(String additional) {
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
}
