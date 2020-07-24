/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
package de.ingrid.iplug.opensearch.webapp.object;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class OpensearchConfig implements Externalizable {

	private String _opensearchUrl;
	
	private String _opensearchDescriptorUrl;
	
	private boolean _rankSupport;
	
	private String _rankMultiplier;
	
	private String _rankAddition;
	
	private String _osDescriptor;
	
	private boolean _mappingSupport = false;
	
	
	public String getOpensearchUrl() {
		return _opensearchUrl;
	}
	
	public void setOpensearchUrl(String url) {
		_opensearchUrl = url;
	}
	
	public boolean getRankSupport() {
		return _rankSupport;
	}
	
	public void setRankSupport(boolean value) {
		_rankSupport = value;;
	}	
	
	
	@Override
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Set the modifier for multiplication of the ranking value.
	 * Use "1" as default value.
	 * @param rankMultiplier
	 */
	public void setRankMultiplier(String rankMultiplier) {
	    if (rankMultiplier == null)
	        this._rankMultiplier = "1";
	    else
	        this._rankMultiplier = rankMultiplier;
	}

	public String getRankMultiplier() {
		return _rankMultiplier;
	}

	/**
	 * Set the modifier for addition of the ranking value.
	 * Use "0" as default value.
	 * @param rankAddition
	 */
	public void setRankAddition(String rankAddition) {
		if (rankAddition == null)
		    this._rankAddition = "0";
		else
		    this._rankAddition = rankAddition;
	}

	public String getRankAddition() {
		return _rankAddition;
	}
	
	public String getOsDescriptor() {
		return _osDescriptor;
	}

	public void setOsDescriptor(String osDescriptor) {
		_osDescriptor = osDescriptor;
	}
	
	public void setOpensearchDescriptorUrl(String opensearchDescriptorUrl) {
		this._opensearchDescriptorUrl = opensearchDescriptorUrl;
	}

	public String getOpensearchDescriptorUrl() {
		return _opensearchDescriptorUrl;
	}
	
	public void printMembers() {
		System.out.println("rankSupport: " + _rankSupport);
		System.out.println("rankMul: " + _rankMultiplier);
		System.out.println("rankAdd: " + _rankAddition);
		System.out.println("osDescriptor: " + _osDescriptor);
		System.out.println("opensearchUrl: " + _opensearchUrl);
		System.out.println("opensearchDescriptorUrl: " + _opensearchDescriptorUrl);
	}

    public void setMappingSupport(boolean mappingSupport) {
        this._mappingSupport = mappingSupport;
    }

    public boolean getMappingSupport() {
        return _mappingSupport;
    }
}
