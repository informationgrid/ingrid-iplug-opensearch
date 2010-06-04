package de.ingrid.iplug.opensearch.converter;

/**
 * The interface of how the modifiers for score of a hit shall be created.
 * It has to be two values, one for multiplying the score and one for adding
 * a value. The implemented class has to receive or create those values for
 * later use.
 *  
 * @author André Wallat
 *
 */
public interface RankingModifier {
    /**
     * Get the value the score shall be multiplied with.
     * 
     * @return
     */
	public float getMultiplier();
	
	/**
	 * Get the value that shall be added to the score.
	 * @return
	 */
	public float getAdditional();
}
