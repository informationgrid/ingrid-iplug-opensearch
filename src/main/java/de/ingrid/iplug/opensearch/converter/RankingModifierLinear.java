/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.iplug.opensearch.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.utils.IngridHit;

/**
 * This class is used to normalize a score received from search results.
 * 
 * Formel:
 * G=FORMER Score (GSA-Score)
 * I=NEW Score (Ingrid-Score)
 * p=Position des Scores innerhalb der Scores mit der selben Höhe. Wert: 0 ... (n-1)
 * n=Anzahl der Scores mit der selben Höhe
 * 
 * I = Minimum(G) + [{(Median(G)-Minimum(G)) / n } * (n-p)]
 * 
 * @author Martin Maidhof
 */
public class RankingModifierLinear implements RankingModifier {
	private static Log log = LogFactory.getLog(RankingModifierLinear.class);
	
	/** Injected by spring.
	 * key = incoming Score
	 * value = transformation values as max, min in a list -> List[Median, Minimum]
	 * score : List[Median, Minimum]
	 */
	private Map<Float, List<Float>> configMap;
	private Float configMinScore;
	private Float configMaxScore;

	/** passed hits grouped by their score ! Hits with same score
	 * go into same list !
	 */
	private Map<Float, List<IngridHit>> hitsWithSameScoreLists;
	
	/** Standard initialisation: empty trafo ... */
	public RankingModifierLinear() {
		configMap = new HashMap<Float, List<Float>>();
		configMinScore = null;
		configMaxScore = null;
	}

	@Override
	public void initialize(IngridHit[] inHits) {
		// prepare data/structures for computation of new score
		// order all hits dependent from their score in groups
		hitsWithSameScoreLists = new HashMap<Float, List<IngridHit>>();

		for (IngridHit hit : inHits) {
			float scoreInConfigRange = normalizeScoreToConfigRange(hit.getScore());
			
			List<IngridHit> hitsWithSameScoreList = hitsWithSameScoreLists.get(scoreInConfigRange);
			if (hitsWithSameScoreList == null) {
				hitsWithSameScoreList = new ArrayList<IngridHit>();
				hitsWithSameScoreLists.put(scoreInConfigRange, hitsWithSameScoreList);
			}
			
			hitsWithSameScoreList.add(hit);
		}
	}

	/** Set the trafo map determining how score is transformed.
	 * @param configMap
	 */
	public void setConfigMap(Map<Float, List<Float>> configMap) {
		if (configMap != null) {
			this.configMap = configMap;
		}

		// remember min and max values of config scores
		for (Float configScore : configMap.keySet()) {
			if (configMinScore == null || configMinScore > configScore) {
				configMinScore = configScore;
			}			
			if (configMaxScore == null || configMaxScore < configScore) {
				configMaxScore = configScore;
			}			
		}
		
		if (log.isDebugEnabled()) {
			log.debug("config Score MIN: " + configMinScore + ", MAX:" + configMaxScore);
		}
	}

	@Override
	public float getNormalizedRanking(IngridHit hit) {
		float scoreInRange = normalizeScoreToConfigRange(hit.getScore());

		// Get position of passed hit in list of hits with same score.
		List<IngridHit> hitsWithSameScoreList = hitsWithSameScoreLists.get(scoreInRange);
		int hitPositionInList = hitsWithSameScoreList.indexOf(hit);
		int numHitsInList = hitsWithSameScoreList.size();
		
		// max and min value of score from configuration
		Float configMaxOfScore = 1.0f;
		Float configMinOfScore = 0.0f;
		List<Float> configMaxMinOfScore = configMap.get(scoreInRange);
		if (configMaxMinOfScore != null && configMaxMinOfScore.size() > 1) {
			configMaxOfScore = configMaxMinOfScore.get(0);
			configMinOfScore = configMaxMinOfScore.get(1);
		} else {
			log.warn("NO config MAX/MIN set for score: " + scoreInRange + ", WE USE 1.0/0.0");
		}
		
		// compute new score
		float newScore = configMinOfScore + ( ((configMaxOfScore - configMinOfScore) / numHitsInList) * (numHitsInList - hitPositionInList) );

		if (log.isDebugEnabled()) {
			log.debug("score of Hit: " + hit.getScore());
			log.debug("score of Hit mapped to config score range: " + scoreInRange);
			log.debug("score MEDIAN from config: " + configMaxOfScore);
			log.debug("score MIN from config: " + configMinOfScore);
			log.debug("list of hits same score, numHitsInList: " + numHitsInList);
			log.debug("list of hits same score, hitPositionInList: " + hitPositionInList);
			log.debug("MIN + ( ((MEDIAN - MIN) / numHitsInList) * (numHitsInList - hitPositionInList) ): " + newScore);
		}

		return newScore;
	}
	
	/** Normalize passed score to be inside range.
	 */
	private float normalizeScoreToConfigRange(float score) {
		if (score < configMinScore) {
			return configMinScore; 
		}
		if (score > configMaxScore) {
			return configMaxScore; 
		}
		
		return score;
	}
}
