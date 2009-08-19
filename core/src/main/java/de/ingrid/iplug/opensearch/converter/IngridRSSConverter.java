package de.ingrid.iplug.opensearch.converter;

import java.io.IOException;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;

public class IngridRSSConverter implements IngridConverter {
	/**
	 * The logging object
	 */
	private static Log log = LogFactory.getLog(IngridRSSConverter.class);


	public static final String TYPE = "application/rss+xml";
	
	// for results not having an InGrid-DocumentId
	private static int customDocId = 0;
	
	public IngridRSSConverter() {
		
	}
	
	@Override
	public IngridHits processResult(String plugId, InputStream result) {
		IngridHits hits = null;
		Document doc 	= null;
		
		try {
			doc = getDocumentFromStream(result);
				
			int totalResults = getTotalResults(doc);
			
			boolean isRanked = getIsRanked(doc);
			
			IngridHit[] hitArray = getHits(doc, plugId, totalResults);

			hits = new IngridHits(plugId, totalResults, hitArray, isRanked);
			
		} catch (ParserConfigurationException e) {
			log.error("Error while parsing the InputStream!");
			e.printStackTrace();
		} catch (SAXException e) {
			log.error("Error while parsing the InputStream!");
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Error while parsing the InputStream!");
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			log.error("Error while performing xpath.evaluate on a document!");
			e.printStackTrace();
		}
		
		return hits;
	}

	private IngridHit[] getHits(Document doc, String plugId, int totalResults) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xpath.evaluate("/rss/channel/item", doc, XPathConstants.NODESET);
		IngridHit[] hits = new IngridHit[nodes.getLength()];
		
		for (int i=0; i<nodes.getLength(); i++) {
			IngridHit hit = new IngridHit(plugId, 0, 0,(float) 1.0);
			Node node = nodes.item(i);
			hit.put("title", getTitle(node));
			hit.put("url", getLink(node));
			hit.put("abstract", getAbstract(node));
			hit.put("no_of_hits", String.valueOf(totalResults));
			hit.setDocumentId(getDocumentId(node));
			hit.setScore(getScore(node));
			hits[i] = hit;
		}
		return hits;
	}

	private float getScore(Node item) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("score", item, XPathConstants.NODE);
		if (node == null) {
			return (float) 1.0;
		} else {
			return Float.valueOf(node.getTextContent());
		}
	}

	private int getDocumentId(Node item) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("docid", item, XPathConstants.NODE);
		if (node == null) {
			return customDocId++;
		} else {
			return Integer.valueOf(node.getTextContent());
		}
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
		return true;
	}

	private int getTotalResults(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("/rss/channel/totalResults", doc, XPathConstants.NODE);
		if (node.getTextContent() == "") {
			return 0;
		} else {
			return Integer.valueOf(node.getTextContent());
		}
	}

	private Document getDocumentFromStream(InputStream result)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document descriptorDoc = builder.parse(result);
		return descriptorDoc;
	}
}
