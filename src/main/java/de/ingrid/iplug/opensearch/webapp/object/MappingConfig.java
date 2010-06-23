package de.ingrid.iplug.opensearch.webapp.object;

public class MappingConfig {
    
    private boolean _useMapping = false;
    
    private boolean _forDomain;
    private boolean _forProvider;
    private boolean _forPartner;
    
    private String _mapDomain;
    private String _mapProvider;
    private String _mapPartner;
    
    private boolean _domainAsParam;
    private boolean _providerAsParam;
    private boolean _partnerAsParam;
    
    public void setForDomain(boolean _grpDomain) {
        this._forDomain = _grpDomain;
    }
    public boolean isForDomain() {
        return _forDomain;
    }
    public void setForProvider(boolean _grpProvider) {
        this._forProvider = _grpProvider;
    }
    public boolean isForProvider() {
        return _forProvider;
    }
    public void setForPartner(boolean _grpPartner) {
        this._forPartner = _grpPartner;
    }
    public boolean isForPartner() {
        return _forPartner;
    }
    public void setMapDomain(String _mapDomain) {
        this._mapDomain = _mapDomain;
    }
    public String getMapDomain() {
        return _mapDomain;
    }
    public void setMapProvider(String _mapProvider) {
        this._mapProvider = _mapProvider;
    }
    public String getMapProvider() {
        return _mapProvider;
    }
    public void setMapPartner(String _mapPartner) {
        this._mapPartner = _mapPartner;
    }
    public String getMapPartner() {
        return _mapPartner;
    }
    public void setDomainAsParam(boolean _domainAsParam) {
        this._domainAsParam = _domainAsParam;
    }
    public boolean isDomainAsParam() {
        return _domainAsParam;
    }
    public void setProviderAsParam(boolean _providerAsParam) {
        this._providerAsParam = _providerAsParam;
    }
    public boolean isProviderAsParam() {
        return _providerAsParam;
    }
    public void setPartnerAsParam(boolean _partnerAsParam) {
        this._partnerAsParam = _partnerAsParam;
    }
    public boolean isPartnerAsParam() {
        return _partnerAsParam;
    }
    public void setUseMapping(boolean _useMapping) {
        this._useMapping = _useMapping;
    }
    public boolean isUseMapping() {
        return _useMapping;
    }

}
