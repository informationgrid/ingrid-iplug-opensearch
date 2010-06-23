package de.ingrid.iplug.opensearch.converter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

/**
 * This class converts the response of an OpenSearch-Interface which is
 * an RSS-Stream, into the IngridHits format.
 * @author André Wallat
 *
 */
public class IngridRSSConverter extends IngridDefaultConverter {
	/**
	 * The logging object
	 */
	private static Log log = LogFactory.getLog(IngridRSSConverter.class);

	public static final String TYPE = "application/rss+xml";
	
	// for results not having an InGrid-DocumentId
	private static int customDocId = 0;
	
	/* (non-Javadoc)
     * @see de.ingrid.iplug.opensearch.converter.IngridDefaultConverter#processResult(String, InputStream)
     */
	@Override
	public IngridHits processResult(String plugId, InputStream result, String groupedBy) {
		IngridHits hits = null;
		Document doc 	= null;
		
		try {
			doc = getDocumentFromStream(result);
				
			int totalResults = getTotalResults(doc);
			
			boolean isRanked = getIsRanked(doc);
			
			IngridHit[] hitArray = getHits(doc, plugId, totalResults, groupedBy);

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
		} finally {
			if (hits == null) {
				hits = new IngridHits(plugId, 0, new IngridHit[0], true);
			}
		}
		
		return hits;
	}

	/**
	 * Return the hits coming from the response of an OS-Interface.
	 * @param doc is the converted response into a document structure 
	 * @param plugId
	 * @param totalResults
	 * @param groupedBy 
	 * @return
	 * @throws XPathExpressionException
	 */
	private IngridHit[] getHits(Document doc, String plugId, int totalResults, String groupedBy) throws XPathExpressionException {
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
			
			setScore(hit, getScore(node));
			
			// ingrid specific data
			setIngridHitDetail(hit, node, groupedBy);
			
			hits[i] = hit;
		}
		return hits;
	}

    private void setPartnerAndProvider(IngridHitDetail hitDetail, Node item) throws XPathExpressionException {
	    XPath xpath = XPathFactory.newInstance().newXPath();
        Node node = (Node) xpath.evaluate("provider", item, XPathConstants.NODE);
        if (node != null) {
            hitDetail.put("provider", new String[]{node.getTextContent()});
        }
        node = (Node) xpath.evaluate("partner", item, XPathConstants.NODE);
        if (node != null) {
            hitDetail.put("partner", new String[]{node.getTextContent()});
        }
    }
	

    /**
	 *  Set an IngridHitDetail with data that is needed by default.
	 *  
	 * @param hit
	 * @param item
     * @param groupedBy 
	 * @throws XPathExpressionException
	 */
	private void setIngridHitDetail(IngridHit hit, Node item, String groupedBy) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		// get plug id
		Node node = (Node) xpath.evaluate("plugid", item, XPathConstants.NODE);
		if (node != null) {
			hit.setPlugId(node.getTextContent());
		}
		
		// get doc id
		hit.setDocumentId(getDocumentId(item));
		
		//=====================================================================
		// the following must be put into the HitDetail
		// this detail will be put into a cache and returned when getDetail()
		// is called
		//=====================================================================
		IngridHitDetail hitDetail = new IngridHitDetail(hit, (String)hit.get("title"),(String)hit.get("abstract"));
		node = (Node) xpath.evaluate("timeReference/start", item, XPathConstants.NODE);		
		if (node != null) {
			hitDetail.put("t1", node.getTextContent());
		}
		node = (Node) xpath.evaluate("timeReference/stop", item, XPathConstants.NODE);		
		if (node != null) {
			hitDetail.put("t2", node.getTextContent());
		}
		
		// set partner and provider
        setPartnerAndProvider(hitDetail, item);
		
		// get box
		node = (Node) xpath.evaluate("box", item, XPathConstants.NODE);
		if (node != null) {
			String[] box = node.getTextContent().split(" ");
			hitDetail.put("x1", box[0]);
			hitDetail.put("y1", box[0]);
			hitDetail.put("x2", box[0]);
			hitDetail.put("y2", box[0]);
		}
		
		// add grouping information
		if (groupedBy != null) {
            String groupInfos = null;
            
            if (IngridQuery.GROUPED_BY_PARTNER.equalsIgnoreCase(groupedBy)) {
                String[] partner = (String[]) hitDetail.get("partner");
                if (partner != null && partner.length > 0)
                    groupInfos = partner[0];
            } else if (IngridQuery.GROUPED_BY_ORGANISATION.equalsIgnoreCase(groupedBy)) {
                String[] provider = (String[]) hitDetail.get("provider");
                if (provider != null && provider.length > 0)
                groupInfos = provider[0];
            } else if (IngridQuery.GROUPED_BY_DATASOURCE.equalsIgnoreCase(groupedBy)) {
                groupInfos = (String) hit.get("url");
                try {
                    groupInfos = new URL(groupInfos).getHost();
                } catch (MalformedURLException e) {
                  log.warn("can not group url: " + groupInfos, e);
                }
            }
            if (groupInfos != null) {
                hit.addGroupedField(groupInfos);
            }
		}
		
		// add some important default values
		hitDetail.setDocumentId(hit.getDocumentId());
		hitDetail.setPlugId(hit.getPlugId());
		hitDetail.setDataSourceId(hit.getDataSourceId());
		hitDetail.put("url", hit.get("url"));
		
		// put detail into cache
		cache.put(new Element(hit.getDocumentId(), hitDetail));
	}

	/**
	 * Extract the score value from a given node and return it as a float value.
	 * In case no score node could be found 0.0f is returned. This score will not
	 * be put into an IngridHit then.
	 * 
	 * @param item is the node to look for the score entry
	 * @return the score but 0.0f if no ranking is supported
	 * @throws XPathExpressionException
	 */
	private float getScore(Node item) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("score", item, XPathConstants.NODE);
		if (node != null) {
			return Float.valueOf(node.getTextContent());
		}
		return 0.0f;
	}

	/**
	 * Calculate a document id so that it can be identified later when looking 
	 * for the detail.
	 * 
	 * @param item
	 * @return
	 * @throws XPathExpressionException
	 */
	private int getDocumentId(Node item) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("docid", item, XPathConstants.NODE);
		if (node == null) {
			return customDocId++;
		} else {
			return Integer.valueOf(node.getTextContent());
		}
	}

	/**
	 * Get the description of the entry.
	 * 
	 * @param item
	 * @return
	 * @throws XPathExpressionException
	 */
	private Object getAbstract(Node item) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("description", item, XPathConstants.NODE);
		return node.getTextContent();
	}

	/**
	 * Get the link of the entry.
	 * 
	 * @param item
	 * @return
	 * @throws XPathExpressionException
	 */
	private Object getLink(Node item) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("link", item, XPathConstants.NODE);
		return node.getTextContent();
	}

	/**
	 * Get the title of the entry.
	 * 
	 * @param item
	 * @return
	 * @throws XPathExpressionException
	 */
	private Object getTitle(Node item) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("title", item, XPathConstants.NODE);
		return node.getTextContent();
	}

	/**
	 * Is the entry ranked?
	 * 
	 * @param doc
	 * @return
	 */
	private boolean getIsRanked(Document doc) {
		// TODO: !!!
		return true;
	}

	/**
	 * Get the total number of hits.
	 * 
	 * @param doc
	 * @return
	 * @throws XPathExpressionException
	 */
	private int getTotalResults(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("/rss/channel/totalResults", doc, XPathConstants.NODE);
		if (node.getTextContent() == "") {
			return 0;
		} else {
			return Integer.valueOf(node.getTextContent());
		}
	}

	/**
	 * Create a parseable DOM-document of the InputStream, which should be XML/HTML.
	 *  
	 * @param result
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document getDocumentFromStream(InputStream result)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		//Document descriptorDoc = builder.parse(new InputSource(new InputStreamReader(result, "UTF8")));
		Document descriptorDoc = builder.parse(result);
		return descriptorDoc;
	}
}
