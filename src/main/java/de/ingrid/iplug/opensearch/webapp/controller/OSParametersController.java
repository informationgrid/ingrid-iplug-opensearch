package de.ingrid.iplug.opensearch.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ingrid.admin.command.PlugdescriptionCommandObject;
import de.ingrid.admin.controller.AbstractController;
import de.ingrid.iplug.opensearch.webapp.object.OpensearchConfig;
import de.ingrid.iplug.opensearch.webapp.validation.OSValidator;
import de.ingrid.utils.query.IngridQuery;

@Controller
@SessionAttributes("plugDescription")
public class OSParametersController extends AbstractController {
	private final OSValidator _validator;
	
	@Autowired
	public OSParametersController(OSValidator validator) {
		_validator = validator;
	}
	
	@RequestMapping(value = {"/iplug/welcome.html", "/iplug/osParams.html"}, method = RequestMethod.GET)
    public String getParameters(final ModelMap modelMap,
    		@ModelAttribute("plugDescription") final PlugdescriptionCommandObject commandObject) {
		
		OpensearchConfig osConfig = new OpensearchConfig();
		
		// put values from plugdescription into object being used by the web-form
		mapParamsFromPD(osConfig, commandObject);		
		
		// write object into session
		modelMap.addAttribute("osConfig", osConfig);
        return AdminViews.OS_PARAMS;
    }

	@RequestMapping(value = "/iplug/osParams.html", method = RequestMethod.POST)
    public String post(@ModelAttribute("osConfig") final OpensearchConfig commandObject,
    		final BindingResult errors,
    		@ModelAttribute("plugDescription") final PlugdescriptionCommandObject pdCommandObject) {
    	
		// check if page contains any errors
    	if (_validator.validateOSParams(errors).hasErrors()) {
            return AdminViews.OS_PARAMS;
        }
    	
    	// put values into plugdescription
    	mapParamsToPD(commandObject, pdCommandObject);
    	
    	return redirect(AdminViews.SAVE + ".html");
    }
	
	
    private void mapParamsToPD(OpensearchConfig commandObject,
			PlugdescriptionCommandObject pdCommandObject) {
    	// write Ranking-information after list got emptied
    	if (pdCommandObject.getArrayList(IngridQuery.RANKED) != null )
    		pdCommandObject.getArrayList(IngridQuery.RANKED).clear();
    	
    	if (commandObject.getRankSupport()) {//enableRanking != null && enableRanking.equals("on")) {
    		pdCommandObject.setRankinTypes(true, false, false);
    	} else {
    		pdCommandObject.setRankinTypes(false, false, true);
    	}
    	
    	pdCommandObject.put("rankingMul", commandObject.getRankMultiplier());
    	pdCommandObject.put("rankingAdd", commandObject.getRankAddition());
    	    	
    	// write information if Descriptor is used
    	if (commandObject.getOsDescriptor() != null && commandObject.getOsDescriptor().equals("descriptor")) { //useDescriptor.equals("descriptor")) {
    		pdCommandObject.putBoolean("useDescriptor", true);
    		pdCommandObject.put("serviceUrl", commandObject.getOpensearchDescriptorUrl());
    	} else {
    		pdCommandObject.putBoolean("useDescriptor", false);
    		pdCommandObject.put("serviceUrl", commandObject.getOpensearchUrl());
    	}		
	}

	private void mapParamsFromPD(OpensearchConfig osConfig, PlugdescriptionCommandObject pdObject) {
    	osConfig.setRankSupport(rankSupported(pdObject.getRankingTypes()));
		
		if (pdObject.containsKey("useDescriptor") && pdObject.getBoolean("useDescriptor")) {
			osConfig.setOsDescriptor("descriptor");
			osConfig.setOpensearchDescriptorUrl((String)pdObject.get("serviceUrl"));
		} else if (pdObject.containsKey("useDescriptor")) {
			osConfig.setOsDescriptor("url");
			osConfig.setOpensearchUrl((String)pdObject.get("serviceUrl"));
		}
		
		osConfig.setRankAddition((String)pdObject.get("rankingAdd"));
		osConfig.setRankMultiplier((String)pdObject.get("rankingMul"));
	}
    
	public boolean rankSupported(String[] types) {
		System.out.println("types: " + types.toString());
		for (String type : types) {
			if (type.contains("score"))
				return true;
		}
		return false;
	}

}
