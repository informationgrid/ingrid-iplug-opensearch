/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
package de.ingrid.iplug.opensearch.query;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.ingrid.iplug.opensearch.communication.OSCommunication;

/**
 * This class takes care of receiving the Opensearch-Descriptor
 * @author André Wallat
 *
 */
public class OSDescriptorBuilder {
	private static Log log = LogFactory.getLog(OSDescriptorBuilder.class);
	
	/**
	 * Create a descriptor object that holds all the information necessary. If
	 * a descriptor was given, the OS-Interface is requested. If not then the
	 * template-url (according to Opensearch-standard) provided will be used
	 * along with the type 'application/rss+xml'.
	 * 
	 * @param serviceUrl
	 * @param isDescriptor
	 * @return
	 * @throws Exception
	 */
	public OSDescriptor createDescriptor(String serviceUrl, boolean isDescriptor) throws Exception {
		if (isDescriptor) {
			return receiveDescriptor(serviceUrl);
		} else {
			OSDescriptor osDesciptor = new OSDescriptor();
			osDesciptor.setTypeAndUrl("application/rss+xml", serviceUrl);
			return osDesciptor;
		}
	}
	
	/**
	 * Request the descriptorAddress and return an OSDescriptor object.
	 * 
	 * @param descriptorAddress
	 * @return
	 * @throws Exception
	 */
	public OSDescriptor receiveDescriptor(String descriptorAddress) throws Exception {
		OSCommunication comm = new OSCommunication();
		OSDescriptor osDesciptor = new OSDescriptor();
		
		try {
			InputStream response = comm.sendRequest(descriptorAddress);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document descriptorDoc = builder.parse(response);
			
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList typeList = (NodeList) xpath.evaluate("/OpenSearchDescription/Url/@type", descriptorDoc, XPathConstants.NODESET);
			NodeList templateList= (NodeList) xpath.evaluate("/OpenSearchDescription/Url/@template", descriptorDoc, XPathConstants.NODESET);
			
			for (int i=0; i<typeList.getLength(); i++) {
				osDesciptor.setTypeAndUrl(typeList.item(i).getTextContent(), templateList.item(i).getTextContent());
			}
		} catch (ParserConfigurationException e) {
			log.error("Error while parsing DescriptorFile from: " + descriptorAddress, e);
		} catch (SAXException e) {
			log.error("Error while parsing DescriptorFile from: " + descriptorAddress, e);
		} catch (XPathExpressionException e) {
			log.error("Error while parsing DescriptorFile from: " + descriptorAddress, e);
		} finally {
			comm.releaseConnection();
		}
		return osDesciptor;
	}
	
	/**
	 * Check for a certain type inside a document.
	 * 
	 * @param nodeList
	 * @param type
	 * @return
	 */
	/*private boolean nodeListContainsType(NodeList nodeList, String type) {
		
		for (int i=0; i<nodeList.getLength(); i++) {
			if (nodeList.item(i).getTextContent().equals(type)) {
				return true;
			}
		}
		return false;
	}*/
	
	/**
	 * Print all the types offered in this document.
	 * @param nodeList
	 * @return
	 */
	/*private String printTypes(NodeList nodeList) {
		String result = "";
		for (int i=0; i<nodeList.getLength(); i++) {
			result += nodeList.item(i).getTextContent() +"\n";
		}
		return result;
	}*/
}
