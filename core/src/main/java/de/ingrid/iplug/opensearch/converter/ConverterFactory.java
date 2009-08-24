package de.ingrid.iplug.opensearch.converter;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.iplug.opensearch.query.OSDescriptor;

public class ConverterFactory {
	private static Log log = LogFactory.getLog(ConverterFactory.class);
	
	// injected by Spring
	private OpensearchRequestStrategy opensearchRequestStrategy;
	
	// injected by Spring
	private RankingModifier rankingModifier;

	// injected by Spring
	private Map<String, String> converterMap;

	public IngridConverter getConverter(OSDescriptor descriptor) throws Exception {
		List<String> strategies = opensearchRequestStrategy.getStrategy();
		for (String type : strategies) {
			if (descriptor.hasType(type)) {
				// remember in Descriptor which type will be used 
				descriptor.setUsedType(type);
				
				// return the class that is used for the conversion
				IngridConverter iConverter = (IngridConverter)Class.forName(converterMap.get(type)).newInstance();
				iConverter.setRankingModifier(rankingModifier);
				return iConverter;
			}
		}
		log.error("There's no supported type within the received descriptor!");
		throw new Exception();
	}
	
	public OpensearchRequestStrategy getOpensearchRequestStrategy() {
		return opensearchRequestStrategy;
	}

	public void setOpensearchRequestStrategy(
			OpensearchRequestStrategy opensearchRequestStrategy) {
		this.opensearchRequestStrategy = opensearchRequestStrategy;
	}
	
	public Map<String, String> getConverterMap() {
		return converterMap;
	}

	public void setConverterMap(Map<String, String> converterMap) {
		this.converterMap = converterMap;
	}
	
	public void setRankingModifier(RankingModifier rankingModifier) {
		this.rankingModifier = rankingModifier;
	}
}
