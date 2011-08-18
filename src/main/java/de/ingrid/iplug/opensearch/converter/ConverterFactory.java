package de.ingrid.iplug.opensearch.converter;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.iplug.opensearch.query.OSDescriptor;

/**
 * A factory returning an individual converter for the output format.
 * 
 * @author André Wallat
 *
 */
public class ConverterFactory {
	private static Log log = LogFactory.getLog(ConverterFactory.class);
	
	// injected by Spring
	private OpensearchRequestStrategy opensearchRequestStrategy;
	
	// injected by Spring
	private List<RankingModifier> rankingModifiers;

	// injected by Spring
	private Map<String, String> converterMap;

	/**
	 * According to the descriptor and the strategy, which conversion should be
	 * prefered, a certain converter is returned.
	 * 
	 * @param descriptor is the descriptor returned from the OS-interface
	 * @return a converter if supported
	 * @throws Exception if no converter is available that is listed in the descriptor
	 */
	public IngridConverter getConverter(OSDescriptor descriptor) throws Exception {
		List<String> strategies = opensearchRequestStrategy.getStrategy();
		for (String type : strategies) {
			if (descriptor.hasType(type)) {
				// remember in Descriptor which type will be used 
				descriptor.setUsedType(type);
				
				// return the class that is used for the conversion
				IngridConverter iConverter = (IngridConverter)Class.forName(converterMap.get(type)).newInstance();
				iConverter.setRankingModifiers(rankingModifiers);
				if (log.isDebugEnabled()) {
					for (RankingModifier rmf : rankingModifiers) {
						log.debug("Use RankingModifier: " + rmf);
					}
				}
				return iConverter;
			}
		}
		log.error("There's no supported type within the received descriptor!");
		throw new Exception();
	}
	
	/**
	 * Get the strategies which converter should be prefered over the other.
	 * @return
	 */
	public OpensearchRequestStrategy getOpensearchRequestStrategy() {
		return opensearchRequestStrategy;
	}

	/**
	 * Set the strategies which converter should be prefered over the other.
	 * @param opensearchRequestStrategy
	 */
	public void setOpensearchRequestStrategy(
			OpensearchRequestStrategy opensearchRequestStrategy) {
		this.opensearchRequestStrategy = opensearchRequestStrategy;
	}
	
	/**
	 * Get the map which holds a converter for each strategy (like rss, atom, ...).
	 * @return
	 */
	public Map<String, String> getConverterMap() {
		return converterMap;
	}

	/**
	 * Set the map which holds a converter for each strategy.
	 * @param converterMap
	 */
	public void setConverterMap(Map<String, String> converterMap) {
		this.converterMap = converterMap;
	}
	
	/**
	 * Set the modifier who influences the ranking, before it is returned
	 * to the requesting client.
	 * @param rankingModifier
	 */
	public void setRankingModifiers(List<RankingModifier> rankingModifiers) {
		this.rankingModifiers = rankingModifiers;
	}
}
