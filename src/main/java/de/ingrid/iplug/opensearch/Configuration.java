/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.iplug.opensearch;

import java.util.Properties;

import com.thoughtworks.xstream.XStream;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFiles;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocations;
import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;

import de.ingrid.admin.IConfig;
import de.ingrid.admin.command.PlugdescriptionCommandObject;
import de.ingrid.utils.tool.PlugDescriptionUtil;
import de.ingrid.utils.tool.QueryUtil;

@PropertiesFiles( {"config"} )
@PropertyLocations(directories = {"conf"}, fromClassLoader = true)
public class Configuration implements IConfig {
    
    // private static Log log = LogFactory.getLog(Configuration.class);
    
    @PropertyValue("plugdescription.mapping")
    @DefaultValue("")
    public String mapping;
    
    @PropertyValue("plugdescription.useDescriptor")
    @DefaultValue("false")
    public boolean useDescriptor;
    
    @PropertyValue("plugdescription.domainGroupingSupport")
    @DefaultValue("false")
    public boolean domainGroupingSupport;

    @PropertyValue("plugdescription.rankingMul")
    public String rankingMul;
    
    @PropertyValue("plugdescription.rankingAdd")
    public String rankingAdd;
    
    @PropertyValue("plugdescription.mappingSupport")
    @DefaultValue("false")
    public boolean mappingSupport;
    
    @PropertyValue("plugdescription.serviceUrl")
    public String serviceUrl;
    
    private XStream xstream;
    
    @Override
    public void initialize() {
    }

    @Override
    public void addPlugdescriptionValues( PlugdescriptionCommandObject pdObject ) {
    	pdObject.put( "iPlugClass", "de.ingrid.iplug.opensearch.OpenSearchPlug");
        
        pdObject.put("rankingMul", rankingMul);
        pdObject.put("rankingAdd", rankingAdd);
        pdObject.putBoolean("useDescriptor", useDescriptor);
        pdObject.putBoolean("domainGroupingSupport", domainGroupingSupport);
        pdObject.put("serviceUrl", serviceUrl);
        
        // add necessary fields so iBus actually will query us
        // remove field first to prevent multiple equal entries
        // add field indicating query of SNS metadata, no impact on query
        PlugDescriptionUtil.addFieldToPlugDescription(pdObject, "incl_meta");
        
        // we also can process metainfo field ! so indicate this !
        // add "metainfo" field, so plug won't be filtered when field is part of query !
        PlugDescriptionUtil.addFieldToPlugDescription(pdObject, QueryUtil.FIELDNAME_METAINFO);

        // add datatype opensearch to PD
        pdObject.addDataType("opensearch");
        
        // FIXME: use latest basewebapp to remove this line (wrong mapping of ranking:off, was 'notRanked')
        if (pdObject.getRankingTypes().length == 0) pdObject.setRankinTypes( false, false, true );
    }

    @Override
    public void setPropertiesFromPlugdescription( Properties props, PlugdescriptionCommandObject pd ) {
    	if(pd.get("mapping") != null){
    		xstream = new XStream();
        	props.setProperty("plugdescription.mapping", mapping);
    	}
    	props.setProperty("plugdescription.rankingMul", rankingMul);
        props.setProperty("plugdescription.rankingAdd", rankingAdd);
        props.setProperty("plugdescription.useDescriptor", String.valueOf(useDescriptor));
        props.setProperty("plugdescription.mappingSupport", String.valueOf(mappingSupport));
        props.setProperty("plugdescription.domainGroupingSupport", String.valueOf(domainGroupingSupport));
        props.setProperty("plugdescription.serviceUrl", serviceUrl);
    }
}
