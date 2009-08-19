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

import de.ingrid.iplug.communication.OSCommunication;

public class OSDescriptorBuilder {
	private static Log log = LogFactory.getLog(OSDescriptorBuilder.class);
	
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
			log.error("Error while parsing DescriptorFile from: " + descriptorAddress);
			e.printStackTrace();
		} catch (SAXException e) {
			log.error("Error while parsing DescriptorFile from: " + descriptorAddress);
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			log.error("Error while parsing DescriptorFile from: " + descriptorAddress);
			e.printStackTrace();
		} finally {
			comm.releaseConnection();
		}
		return osDesciptor;
	}
	
	private boolean nodeListContainsType(NodeList nodeList, String type) {
		
		for (int i=0; i<nodeList.getLength(); i++) {
			if (nodeList.item(i).getTextContent().equals(type)) {
				return true;
			}
		}
		return false;
	}
	
	private String printTypes(NodeList nodeList) {
		String result = "";
		for (int i=0; i<nodeList.getLength(); i++) {
			result += nodeList.item(i).getTextContent() +"\n";
		}
		return result;
	}
}
