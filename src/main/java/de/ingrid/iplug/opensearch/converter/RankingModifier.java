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
	 * This function normalizes the ranking (score)
	 * @param score old score
	 * @return new score
	 */
	float normalizeRanking(float score);
}
