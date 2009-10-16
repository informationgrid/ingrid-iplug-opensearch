package de.ingrid.iplug.opensearch.converter;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class IngridRSSConverterTest extends TestCase {

	private final String XML_INPUT_FILE = "response_example.xml";
	
	public final void testProcessResult() {
		IngridRSSConverter converter = new IngridRSSConverter();
		
		try {
			Resource resource = new ClassPathResource(XML_INPUT_FILE);
			converter.processResult("plugId", resource.getInputStream());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//fail("Not yet implemented"); // TODO
	}

}
