package de.ingrid.iplug.opensearch.webapp.validation;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import de.ingrid.admin.validation.AbstractValidator;
import de.ingrid.iplug.opensearch.webapp.object.MappingConfig;

@Service
public class OSMappingValidator extends AbstractValidator<MappingConfig> {
    
    public final Errors validateOSMapping(final BindingResult errors) {
        
        if (getBoolean(errors, "forDomain"))
            rejectIfEmptyOrWhitespace(errors, "mapDomain");
        if (getBoolean(errors, "forProvider"))
            rejectIfEmptyOrWhitespace(errors, "mapProvider");
        if (getBoolean(errors, "forPartner"))
            rejectIfEmptyOrWhitespace(errors, "mapPartner");
        return errors;
    }

}
