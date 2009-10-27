package de.ingrid.iplug.opensearch;

import org.springframework.stereotype.Service;

import de.ingrid.iplug.IPlugdescriptionFieldFilter;

@Service
public class MappingFilter implements IPlugdescriptionFieldFilter {

	public boolean filter(Object object) {
		String key = object.toString();
		if ("rankingMul".equals(key) ||
			"rankingAdd".equals(key)) {
			return true;
		}
		return false;
	}
}