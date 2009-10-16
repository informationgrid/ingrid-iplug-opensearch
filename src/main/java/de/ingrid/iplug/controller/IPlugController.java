package de.ingrid.iplug.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ingrid.admin.command.PlugdescriptionCommandObject;

@Controller
@SessionAttributes("plugDescription")
public class IPlugController {
	@RequestMapping(value = {"/iplug/welcome.html", "/iplug/nothing.html"}, method = RequestMethod.GET)
    public String get(@ModelAttribute("plugDescription") final PlugdescriptionCommandObject plugDescription) {
        // do nearly nothing
        plugDescription.put("nothing", 0);

        return "/iplug/nothing";
    }

    @RequestMapping(value = "/iplug/nothing.html", method = RequestMethod.POST)
    public String post() {
        return "redirect:/base/save.html";
    }

}
