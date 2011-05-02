package de.ingrid.iplug.opensearch.webapp.object;

import org.springframework.stereotype.Service;

import de.ingrid.admin.object.AbstractDataType;

@Service
public class opensearchDataType extends AbstractDataType {

    public opensearchDataType() {
        super("opensearch");
        setForceActive(true);
    }

}