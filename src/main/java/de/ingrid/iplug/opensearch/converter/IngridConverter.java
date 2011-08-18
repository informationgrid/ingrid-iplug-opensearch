package de.ingrid.iplug.opensearch.converter;

import java.io.InputStream;
import java.util.List;

import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;

/**
 * The interface of how a converter has to look-a-like.
 * @author André Wallat
 *
 */
public interface IngridConverter {
	public IngridHits processResult(String plugId, InputStream result, String groupedBy);
	
	public void setRankingModifiers(List<RankingModifier> normalizers);
	
	public IngridHitDetail getHitDetailFromCache(int docId);
}
