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
package de.ingrid.iplug.opensearch.webapp.validation;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import de.ingrid.admin.validation.AbstractValidator;
import de.ingrid.iplug.opensearch.webapp.object.OpensearchConfig;

@Service
public class OSValidator extends AbstractValidator<OpensearchConfig> {

	public final Errors validateOSParams(final BindingResult errors) {
		rejectIfNull(errors, "osDescriptor");
		String useDescriptor = getString(errors, "osDescriptor");
		if (useDescriptor != null && useDescriptor.equals("descriptor")) {
			rejectIfEmptyOrWhitespace(errors, "opensearchDescriptorUrl");
		} else if (useDescriptor != null && useDescriptor.equals("url")) {
			rejectIfEmptyOrWhitespace(errors, "opensearchUrl");
		}
		
		if (getBoolean(errors, "rankSupport")) {
			rejectIfEmptyOrWhitespace(errors, "rankMultiplier");
			rejectIfEmptyOrWhitespace(errors, "rankAddition");
		}
        return errors;
    }
}
