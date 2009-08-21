package de.ingrid.iplug.opensearch.converter;

/**
 * This class is used to normalize a score received from search results.
 * normalizedScore = multiplier*score + additional
 * @author Andre
 *
 */
public class RankingModifier {
	// value for multiplying the score
	private float multiplier;
	
	// value for adding to the score
	private float additional;
	
	/**
	 * The standard initialisation will not alter the score at all.
	 */
	public RankingModifier() {
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
	public RankingModifier(float mul, float add) {
		this.multiplier = mul;
		this.additional = add;
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
