package de.ingrid.iplug.opensearch.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ingrid.admin.controller.ExtrasController;

@Controller
@SessionAttributes("plugDescription")
public class OSExtrasController {
	
	@Autowired
	public OSExtrasController(ExtrasController controller) {
	    controller.show_ShowInUnranked();
	}
	
}
