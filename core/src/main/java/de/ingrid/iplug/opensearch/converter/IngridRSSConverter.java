package de.ingrid.iplug.opensearch.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;

public class IngridRSSConverter implements IngridConverter {

	public static final String TYPE = "application/rss+xml";
	
	@Override
	public IngridHits processResult(String plugId, InputStream result) {
		IngridHits hits = null;
		Document doc 	= null;
		
		try {
			doc = getDocumentFromStream(result);
				
			int totalResults = getTotalResults(doc);
			
			boolean isRanked = getIsRanked(doc);
			
			IngridHit[] hitArray = getHits(doc, plugId);

			hits = new IngridHits(plugId, totalResults, hitArray, isRanked);
			//hits = new IngridHits(totalResults, hitArray);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(convertStreamToString(result));

		return hits;
	}

	private IngridHit[] getHits(Document doc, String plugId) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xpath.evaluate("/rss/channel/item", doc, XPathConstants.NODESET);
		IngridHit[] hits = new IngridHit[nodes.getLength()];
		
		for (int i=0; i<nodes.getLength(); i++) {
			IngridHit hit = new IngridHit(plugId, 0, 0,(float) 1.0);
			hit.put("title", getTitle(nodes.item(i)));
			hit.put("url", getLink(nodes.item(i)));
			hit.put("abstract", getAbstract(nodes.item(i)));
			hit.put("no_of_hits", "999");
			hit.setDocumentId(getDocumentId(nodes.item(i)));
			
			hits[i] = hit;
		}
		return hits;
	}

	private int getDocumentId(Node item) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("docid", item, XPathConstants.NODE);
		return Integer.valueOf(node.getTextContent());
	}

	private Object getAbstract(Node item) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("description", item, XPathConstants.NODE);
		return node.getTextContent();
	}

	private Object getLink(Node item) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("link", item, XPathConstants.NODE);
		return node.getTextContent();
	}

	private Object getTitle(Node item) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("title", item, XPathConstants.NODE);
		return node.getTextContent();
	}

	private boolean getIsRanked(Document doc) {
		// TODO: !!!
		return false;
	}

	private int getTotalResults(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("/rss/channel/totalResults", doc, XPathConstants.NODE);
		return Integer.valueOf(node.getTextContent());
	}

	private Document getDocumentFromStream(InputStream result)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document descriptorDoc = builder.parse(result);
		return descriptorDoc;
	}
	
	public String convertStreamToString(InputStream is) {
		        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		        StringBuilder sb = new StringBuilder();
		 
		        String line = null;
		       try {
		            while ((line = reader.readLine()) != null) {
		                sb.append(line + "\n");
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
		            try {
		                is.close();
		            } catch (IOException e) {
	                e.printStackTrace();
		            }
		        }
		 
		        return sb.toString();
		    }

}
