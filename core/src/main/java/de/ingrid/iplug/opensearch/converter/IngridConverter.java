package de.ingrid.iplug.opensearch.converter;

import java.io.InputStream;

import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;

public interface IngridConverter {
	public IngridHits processResult(String plugId, InputStream result);
	
	public void setRankingModifier(RankingModifier normalizer);
	
	public IngridHitDetail getHitDetailFromCache(int docId);
}
