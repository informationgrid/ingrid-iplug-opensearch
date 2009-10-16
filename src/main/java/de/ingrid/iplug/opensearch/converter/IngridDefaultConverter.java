package de.ingrid.iplug.opensearch.converter;

import java.io.InputStream;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;

/**
 * This class shall be used as a parent class for all other converters.
 * It already offers the functionality to normalize the ranking by modifying
 * the score to adapt it to the own ranking system.
 * 
 * @author André Wallat
 *
 */
public abstract class IngridDefaultConverter implements IngridConverter{
	private static Log log = LogFactory.getLog(IngridDefaultConverter.class);
	
	private RankingModifier rankingModifier;
	
	protected Cache cache;
	
	public IngridDefaultConverter() {
		// init ehcache
		CacheManager cacheManager = CacheManager.getInstance();
		cache = cacheManager.getCache("detail-cache"); 
		if (cache == null) {
			log.warn("Cache settings 'detail-cache' not found! Using default values.");
			if (!cacheManager.cacheExists("default")) {
                cache = new Cache("default", 1000, false, false, 600, 600);
                cacheManager.addCache(cache);
            } else {
                cache = cacheManager.getCache("default");
            }
		}
	}
	
	@Override
	// this must be implemented with a concrete class!
	public abstract IngridHits processResult(String plugId, InputStream result);
	
	public void setRankingModifier(RankingModifier normalizer) {
		this.rankingModifier = normalizer;
	}
	
	public void setScore(IngridHit hit, float score) {
		if (score != 0.0f) {
			hit.setScore(normalizeRanking(score));
		}
	}
	
	private float normalizeRanking(float score) {
		float normalizedScore = 0.0f;
		
		if (rankingModifier == null) {
			log.warn("RankingModifier is not initialized");
			return score;
		}
		
		normalizedScore = rankingModifier.getMultiplier()*score + rankingModifier.getAdditional();
		
		return normalizedScore;
	}
	
	public IngridHitDetail getHitDetailFromCache(int docId) {
		Element element = cache.get(docId);
		if (element != null) {
			return (IngridHitDetail)element.getValue();	
		}
		return null;
		
	}
}
