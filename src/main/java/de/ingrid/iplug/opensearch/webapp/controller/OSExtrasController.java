package de.ingrid.iplug.opensearch.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ingrid.admin.controller.ExtrasController;

/**
 * A controller to activate the ExtrasController with more settings.
 * @author André Wallat
 *
 */
@Controller
@SessionAttributes("plugDescription")
public class OSExtrasController {
	
    /**
     * The main purpose is to show the option, that the result shall be also shown
     * on the right side in the portal.
     * @param controller
     */
	@Autowired
	public OSExtrasController(ExtrasController controller) {
	    controller.show_ShowInUnranked();
	}
	
}
