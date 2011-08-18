package de.ingrid.iplug.opensearch.converter;

import java.io.InputStream;
import java.util.List;

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
	
	private List<RankingModifier> rankingModifiers;
	
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

	/**
	 * Process the result from an OS-Interface and return the converted IngridHits.
	 * This must be implemented with a concrete class!
     *
	 */
	@Override
	public abstract IngridHits processResult(String plugId, InputStream result, String groupedBy);
	
	public void setRankingModifiers(List<RankingModifier> normalizers) {
		this.rankingModifiers = normalizers;
	}
	
	/**
	 * Normalize the scores of the hits by injected modifiers !
	 * @param hits original result with unnormalized scores !
	 */
	public void normalizeRanking(IngridHit[] hits) {
		// first initialize all modifiers with original results, so they can extract infos needed !
		for (RankingModifier rankingModifier : rankingModifiers) {
			rankingModifier.initialize(hits);
		}

		// then normalize
		for (IngridHit hit : hits) {
			float score = hit.getScore();
			if (log.isDebugEnabled()) {
				log.debug("hit original score: " + score);
			}

			for (RankingModifier rankingModifier : rankingModifiers) {
				score = rankingModifier.normalizeRanking(score);
				if (log.isDebugEnabled()) {
					log.debug("normalized score with " + rankingModifier + " -> " + score);
				}
			}

			if (log.isDebugEnabled()) {
				log.debug("hit set normalized score: " + score);
			}
			hit.setScore(score);
		}
	}
	
	/**
	 * Return a IngridHitDetail by using the temporary docId, which was
	 * created during the last fetch of the results.
	 */
	public IngridHitDetail getHitDetailFromCache(int docId) {
		Element element = cache.get(docId);
		if (element != null) {
			return (IngridHitDetail)element.getValue();	
		}
		return null;
		
	}
}
