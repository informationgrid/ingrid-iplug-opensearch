/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
