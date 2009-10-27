package de.ingrid.iplug.webapp.validation;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import de.ingrid.admin.validation.AbstractValidator;
import de.ingrid.iplug.opensearch.webapp.object.OpensearchConfig;

@Service
public class OSValidator extends AbstractValidator<OpensearchConfig> {

	public final Errors validateOSParams(final BindingResult errors) {
		System.out.println("Validate OSParams");
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
        System.out.println("Validated");
        return errors;
    }
}
