/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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

import java.util.List;

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
import de.ingrid.iplug.opensearch.model.OSMapping;
import de.ingrid.iplug.opensearch.model.OSMapping.IngridFieldType;
import de.ingrid.iplug.opensearch.webapp.object.MappingConfig;
import de.ingrid.iplug.opensearch.webapp.validation.OSMappingValidator;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.tool.PlugDescriptionUtil;

/**
 * Control the page of Opensearch-specific parameters of the webapp.
 * @author André Wallat
 *
 */
@Controller
@SessionAttributes("plugDescription")
public class OSMappingController extends AbstractController {
    
    private OSMappingValidator _validator;

    @Autowired
    public OSMappingController(OSMappingValidator validator) {
        _validator = validator;
    }
    
    @RequestMapping(value = {"/iplug-pages/osIngridMapping.html"}, method = RequestMethod.GET)
    public String getMapping(final ModelMap modelMap,
            @ModelAttribute("plugDescription") final PlugdescriptionCommandObject commandObject) {
        MappingConfig mapConfig = new MappingConfig();
        
        mapConfigFromPD(mapConfig, commandObject);
        
        // write object into session
        modelMap.addAttribute("mapConfig", mapConfig);
        
        return AdminViews.OS_MAPPING;
    }
    


    @RequestMapping(value = {"/iplug-pages/osIngridMapping.html"}, method = RequestMethod.POST)
    public String postMapping(@ModelAttribute("mapConfig") final MappingConfig commandObject,
            final BindingResult errors,
            @ModelAttribute("plugDescription") final PlugdescriptionCommandObject pdCommandObject) {
        
        if (_validator.validateOSMapping(errors).hasErrors()) {
            return AdminViews.OS_MAPPING;
        }
        
        mapConfigToPD(commandObject, pdCommandObject);
        
        return AdminViews.SAVE;
    }

    private void mapConfigToPD(MappingConfig commandObject,
            PlugdescriptionCommandObject pdCommandObject) {
        pdCommandObject.remove("mapping");
        
        // add mapping-support
        pdCommandObject.putBoolean("mappingSupport", commandObject.isUseMapping());
        
        OSMapping mapping = new OSMapping();
        mapping.setType(IngridFieldType.DOMAIN);
        mapping.setActive(commandObject.isForDomain());
        mapping.setMapping(commandObject.getMapDomain());
        mapping.setAsParam(commandObject.isDomainAsParam());        
        pdCommandObject.addToList("mapping", mapping);
        
        if (commandObject.isForDomain() && commandObject.isUseMapping()) {
            pdCommandObject.put("domainGroupingSupport", true);
            // remove and add site-field
            pdCommandObject.removeFromList(PlugDescription.FIELDS, "site");
            pdCommandObject.addField("site");
        } else {
            pdCommandObject.put("domainGroupingSupport", false);
            pdCommandObject.removeFromList(PlugDescription.FIELDS, "site");
        }
        
        mapping = new OSMapping();
        mapping.setType(IngridFieldType.PROVIDER);
        mapping.setActive(commandObject.isForProvider());
        mapping.setMapping(commandObject.getMapProvider());
        mapping.setAsParam(commandObject.isProviderAsParam());        
        pdCommandObject.addToList("mapping", mapping);
        if (commandObject.isForProvider() && commandObject.isUseMapping()) {
            PlugDescriptionUtil.addFieldToPlugDescription(pdCommandObject, "provider");
        } else {
            pdCommandObject.removeFromList(PlugDescription.FIELDS, "provider");
        }
        
        
        mapping = new OSMapping();
        mapping.setType(IngridFieldType.PARTNER);
        mapping.setActive(commandObject.isForPartner());
        mapping.setMapping(commandObject.getMapPartner());
        mapping.setAsParam(commandObject.isPartnerAsParam());        
        pdCommandObject.addToList("mapping", mapping);
        if (commandObject.isForPartner() && commandObject.isUseMapping()) {
            PlugDescriptionUtil.addFieldToPlugDescription(pdCommandObject, "partner");
        } else {
            pdCommandObject.removeFromList(PlugDescription.FIELDS, "partner");
        }
    }

    @SuppressWarnings("unchecked")
    private void mapConfigFromPD(MappingConfig mapConfig,
            PlugdescriptionCommandObject commandObject) {
        
        List<OSMapping> mappings = (List<OSMapping>)(List<?>)commandObject.getArrayList("mapping");
        
        if (mappings == null) return;
        
        for (OSMapping mapping : mappings) {
            if (mapping.getType() == IngridFieldType.DOMAIN) {
                mapConfig.setForDomain(mapping.isActive());
                mapConfig.setMapDomain(mapping.getMapping());
                mapConfig.setDomainAsParam(mapping.isAsParam());
            } else if (mapping.getType() == IngridFieldType.PROVIDER) {
                mapConfig.setForProvider(mapping.isActive());
                mapConfig.setMapProvider(mapping.getMapping());
                mapConfig.setProviderAsParam(mapping.isAsParam());
            } else {
                mapConfig.setForPartner(mapping.isActive());
                mapConfig.setMapPartner(mapping.getMapping());
                mapConfig.setPartnerAsParam(mapping.isAsParam());
            }
        }
        
        if (commandObject.containsKey("mappingSupport"))
            mapConfig.setUseMapping(commandObject.getBoolean("mappingSupport"));
        
    }
}
