package de.ingrid.iplug.opensearch.model;

public class OSMapping {
    public static enum IngridFieldType {DOMAIN, PARTNER, PROVIDER};
    
    private IngridFieldType type;
    
    private boolean active;
    
    private String mapping;
    
    private boolean asParam;
    

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public String getMapping() {
        return mapping;
    }

    public void setAsParam(boolean asParam) {
        this.asParam = asParam;
    }

    public boolean isAsParam() {
        return asParam;
    }

    public void setType(IngridFieldType type) {
        this.type = type;
    }

    public IngridFieldType getType() {
        return type;
    }
}
