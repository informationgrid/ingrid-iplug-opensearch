package de.ingrid.iplug.opensearch.converter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import de.ingrid.iplug.opensearch.communication.OSCommunication;
import de.ingrid.iplug.opensearch.query.OSDescriptor;
import de.ingrid.iplug.opensearch.query.OSDescriptorBuilder;

public class IngridRSSConverterTest extends TestCase {

	private final String XML_INPUT_FILE = "response_example2.xml";
	
	public final void testProcessResult() {
		IngridRSSConverter converter = new IngridRSSConverter();
		
		try {
			Resource resource = new ClassPathResource(XML_INPUT_FILE);
			//converter.processResult("plugId", resource.getInputStream());

		} catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		//fail("Not yet implemented"); // TODO
	}

	public final void testProcessResultLive() {
        IngridRSSConverter converter = new IngridRSSConverter();
        
        try {

            OSDescriptorBuilder descrBuilder = new OSDescriptorBuilder();
            OSDescriptor osDescriptor = descrBuilder.createDescriptor("http://harrison.its-technidata.de/opensearchserver/descriptor", true);
            
            //ConverterFactory converterFactory = new ConverterFactory();
            IngridRSSConverter ingridConverter = new IngridRSSConverter();
            
            OSCommunication comm = new OSCommunication();
            InputStream result = comm.sendRequest("http://harrison.its-technidata.de:80/opensearchserver/query?q=Wasser&p=0&h=10&georss=1&format=rss");
            ingridConverter.processResult("bla", result);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //fail("Not yet implemented"); // TODO
    }
	
}
