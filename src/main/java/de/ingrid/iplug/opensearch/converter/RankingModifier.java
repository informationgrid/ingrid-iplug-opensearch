package de.ingrid.iplug.opensearch.converter;

import de.ingrid.utils.IngridHit;

/**
 * The interface of how the modifiers for score of a hit shall be created.
 * @author André Wallat
 *
 */
public interface RankingModifier {

	/** Pass the full result of the query already processed as IngridHit[] BUT NOT NORMALIZED YET !
	 * So the RankingModifier can analyze the original result if needed for normalization !
	 * @param originalResult the result of the OpenSearch query already processed as IngridHit[]
	 */
	void initialize(IngridHit[] originalResult);

	/**
	 * This function computes and returns the new score !
	 * NOTICE: does NOT set the new score in the hit !
	 * @param hit hit delivering former score
	 * @return new score after processing of this modifier (NOT set in hit yet !)
	 */
	float getNormalizedRanking(IngridHit hit);
}
