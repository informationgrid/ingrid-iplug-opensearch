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
}
