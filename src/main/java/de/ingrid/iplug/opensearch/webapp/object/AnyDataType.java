package de.ingrid.iplug.opensearch.webapp.object;

import org.springframework.stereotype.Service;

import de.ingrid.admin.object.AbstractDataType;

@Service
public class AnyDataType extends AbstractDataType {

    public AnyDataType() {
        super("any");
        
        System.out.println("Test Datatype!");
    }

}