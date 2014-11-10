/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.xstream.XStream;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFiles;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocations;
import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;

import de.ingrid.admin.IConfig;
import de.ingrid.admin.command.PlugdescriptionCommandObject;

@PropertiesFiles( {"config"} )
@PropertyLocations(directories = {"conf"}, fromClassLoader = true)
public class Configuration implements IConfig {
    
    private static Log log = LogFactory.getLog(Configuration.class);
    
    @PropertyValue("plugdescription.fields")
    public String fields;
    
    @PropertyValue("plugdescription.mapping")
    @DefaultValue("")
    public String mapping;
    
    @PropertyValue("plugdescription.useDescriptor")
    @DefaultValue("false")
    public boolean useDescriptor;
    
    @PropertyValue("plugdescription.forceAddRankingOff")
    @DefaultValue("false")
    public boolean forceAddRankingOff;
    
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
        
    	if(pdObject.getFields().length == 0){
        	if(fields != null){
        		String[] fieldsList = fields.split(",");
        		for(String field : fieldsList){
        			pdObject.addField(field);
        		}
        	}
    	}
        
        if(pdObject.get("mapping") == null){
        	if(!mapping.equals("")){
        		xstream = new XStream();
            	pdObject.put("mapping", xstream.fromXML(mapping));
        	}
        }
        
        pdObject.put("rankingMul", rankingMul);
        pdObject.put("rankingAdd", rankingAdd);
        pdObject.putBoolean("useDescriptor", useDescriptor);
        pdObject.putBoolean("mappingSupport", mappingSupport);
        pdObject.putBoolean("forceAddRankingOff", forceAddRankingOff);
        pdObject.putBoolean("domainGroupingSupport", domainGroupingSupport);
        pdObject.put("serviceUrl", serviceUrl);
    }

    @Override
    public void setPropertiesFromPlugdescription( Properties props, PlugdescriptionCommandObject pd ) {
    	if(pd.get("mapping") != null){
    		xstream = new XStream();
        	props.setProperty("plugdescription.mapping", xstream.toXML(pd.get("mapping")));
    	}
    }
}
