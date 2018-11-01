/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
package de.ingrid.iplug.opensearch.webapp.controller;

import de.ingrid.admin.command.PlugdescriptionCommandObject;
import de.ingrid.admin.controller.AbstractController;
import de.ingrid.iplug.opensearch.Configuration;
import de.ingrid.iplug.opensearch.webapp.object.OpensearchConfig;
import de.ingrid.iplug.opensearch.webapp.validation.OSValidator;
import de.ingrid.utils.query.IngridQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Control the page of Opensearch-specific parameters of the webapp.
 * @author André Wallat
 *
 */
@Controller
@SessionAttributes("plugDescription")
public class OSParametersController extends AbstractController {
	private final OSValidator _validator;

	@Autowired
	private Configuration opensearchConfig;

	@Autowired
	public OSParametersController(OSValidator validator) {
		_validator = validator;
	}
	
	@RequestMapping(value = {"/iplug-pages/welcome.html", "/iplug-pages/osParams.html"}, method = RequestMethod.GET)
    public String getParameters(final ModelMap modelMap,
    		@ModelAttribute("plugDescription") final PlugdescriptionCommandObject commandObject) {
		OpensearchConfig osConfig = new OpensearchConfig();
		
		// put values from plugdescription into object being used by the web-form
		mapParamsFromPD(osConfig, commandObject);		
		
		// write object into session
		modelMap.addAttribute("osConfig", osConfig);
        return AdminViews.OS_PARAMS;
    }

	@RequestMapping(value = "/iplug-pages/osParams.html", method = RequestMethod.POST)
    public String post(@ModelAttribute("osConfig") final OpensearchConfig commandObject,
    		final BindingResult errors,
    		@ModelAttribute("plugDescription") final PlugdescriptionCommandObject pdCommandObject) {
    	
		// check if page contains any errors
    	if (_validator.validateOSParams(errors).hasErrors()) {
            return AdminViews.OS_PARAMS;
        }
    	
    	// put values into plugdescription
    	mapParamsToPD(commandObject, pdCommandObject);
    	
    	return redirect(AdminViews.OS_MAPPING + ".html");
    }
	
	
    private void mapParamsToPD(OpensearchConfig commandObject,
			PlugdescriptionCommandObject pdCommandObject) {
    	
        boolean isOff  = pdCommandObject.containsKey("forceAddRankingOff") ? pdCommandObject.getBoolean("forceAddRankingOff") : false;
        boolean isDate = pdCommandObject.containsRankingType("date");
        
        // write Ranking-information after list got emptied
        if (pdCommandObject.getArrayList(IngridQuery.RANKED) != null )
            pdCommandObject.getArrayList(IngridQuery.RANKED).clear();
    	
    	if (commandObject.getRankSupport()) {
    		pdCommandObject.setRankinTypes(true,  isDate, isOff);
    		opensearchConfig.rankingMul = commandObject.getRankMultiplier();
            opensearchConfig.rankingAdd = commandObject.getRankAddition();
    	} else if (!isDate && !isOff) {
    	    // there must be at least one "true" value!
    	    pdCommandObject.setRankinTypes(false, false, true);
        } else {
            pdCommandObject.setRankinTypes(false, isDate, isOff);
    	}
    	
    	// write information if Descriptor is used
    	if (commandObject.getOsDescriptor() != null && commandObject.getOsDescriptor().equals("descriptor")) { //useDescriptor.equals("descriptor")) {
    	    opensearchConfig.useDescriptor = true;
    	    opensearchConfig.serviceUrl = commandObject.getOpensearchDescriptorUrl();
    	} else {
    	    opensearchConfig.useDescriptor = false;
    	    opensearchConfig.serviceUrl = commandObject.getOpensearchUrl();
    	}
    	
	}

	private void mapParamsFromPD(OpensearchConfig osConfig, PlugdescriptionCommandObject pdObject) {
    	osConfig.setRankSupport(rankSupported("score", pdObject.getRankingTypes()));
		
		if (opensearchConfig.useDescriptor) {
			osConfig.setOsDescriptor("descriptor");
			osConfig.setOpensearchDescriptorUrl(opensearchConfig.serviceUrl);
		} else {
			osConfig.setOsDescriptor("url");
			osConfig.setOpensearchUrl(opensearchConfig.serviceUrl);
		}
		
		osConfig.setRankAddition(opensearchConfig.rankingAdd);
		osConfig.setRankMultiplier(opensearchConfig.rankingMul);
	}
    
	public boolean rankSupported(String rankType, String[] types) {
		for (String type : types) {
			if (type.contains(rankType))
				return true;
		}
		return false;
	}

}
