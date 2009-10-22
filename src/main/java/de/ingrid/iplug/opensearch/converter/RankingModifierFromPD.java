package de.ingrid.iplug.opensearch.converter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.admin.service.PlugDescriptionService;

/**
 * This class is used to normalize a score received from search results.
 * normalizedScore = multiplier*score + additional
 * @author Andre
 *
 */
@Service
public class RankingModifierFromPD implements RankingModifier {
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
	
	/**
	 * With a multiplier and another value that will be added to the score
	 * it's possible to normalize the ranking to the own system.
	 * 
	 * @param mul is the multiplier the score will be mulitplied with
	 * @param add is a value that will be added to the (already) multiplied score
	 */
	public RankingModifierFromPD(float mul, float add) {
		this.multiplier = mul;
		this.additional = add;
	}
	
	@Autowired
	public RankingModifierFromPD(PlugDescriptionService pdService) throws IOException {
		// default values
		setMultiplier(1.0f);
		setAdditional(0.0f);
		
		// get values from plugDescription if any
		setMultiplier((String)pdService.getPlugDescription().get("rankingMul"));
		setAdditional((String)pdService.getPlugDescription().get("rankingAdd"));
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
}
