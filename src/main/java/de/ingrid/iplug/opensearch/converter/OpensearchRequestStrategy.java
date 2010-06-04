package de.ingrid.iplug.opensearch.converter;

import java.util.List;

/**
 * This class holds a list of strategies, which conversion, and so which kind of
 * response shall be prefered.
 *  
 * @author André Wallat
 *
 */
public class OpensearchRequestStrategy {
	private List<String> strategy;

	public List<String> getStrategy() {
		return strategy;
	}

	public void setStrategy(List<String> strategy) {
		this.strategy = strategy;
	}
	
	
}
