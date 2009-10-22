package de.ingrid.iplug.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ingrid.admin.command.PlugdescriptionCommandObject;
import de.ingrid.utils.query.IngridQuery;

@Controller
@SessionAttributes("plugDescription")
public class OSParametersController {
	
	@ModelAttribute("opensearchUrl")
	public String getOpensearchUrl() {
		System.out.println("getOpensearchUrl");
		return "bla";
	}
	
	@ModelAttribute("rankSupport")
	public String rankSupported() {
		System.out.println("rankSupported");
		return "checked";
	}
	
	@RequestMapping(value = {"/iplug/welcome.html", "/iplug/osParams.html"}, method = RequestMethod.GET)
    public String getParameters(@ModelAttribute("plugDescription") final PlugdescriptionCommandObject plugDescription) {
		System.out.println("get OS Parameter");
        // do nearly nothing
        //plugDescription.put("proxyUrl", "http://www.portalu.de/opensearch");

        return "/iplug/osParams";
    }

    @RequestMapping(value = "/iplug/osParams.html", method = RequestMethod.POST)
    public String post(@ModelAttribute("plugDescription") final PlugdescriptionCommandObject commandObject,
    		@RequestParam(value = "rankSupport", required = false) final String enableRanking,
    		@RequestParam(value = "opensearchUrl", required = false) final String serviceUrl,
    		@RequestParam(value = "rankMultiplier", required = false) final String rankMultiplier,
    		@RequestParam(value = "rankAddition", required = false) final String rankAddition) {
    	
    	System.out.println("post OS Parameter, rankSupport: " + enableRanking);
    	commandObject.put("rankingMul", rankMultiplier);
    	commandObject.put("rankingAdd", rankAddition);
    	//commandObject.put("proxyUrl", proxyUrl);
    	commandObject.put("serviceUrl", serviceUrl);
    	
    	// empty list
    	if (commandObject.getArrayList(IngridQuery.RANKED) != null )
    		commandObject.getArrayList(IngridQuery.RANKED).clear();
    	
    	if (enableRanking != null && enableRanking.equals("on")) {
    		commandObject.setRankinTypes(true, false, false);
    	} else {
    		commandObject.setRankinTypes(false, false, true);
    	}
    	
        return "redirect:/base/save.html";
    }

}
