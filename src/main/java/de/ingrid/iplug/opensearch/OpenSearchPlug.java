/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.opensearch;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import de.ingrid.iplug.HeartBeatPlug;
import de.ingrid.iplug.communication.OSCommunication;
import de.ingrid.iplug.opensearch.converter.ConverterFactory;
import de.ingrid.iplug.opensearch.converter.IngridConverter;
import de.ingrid.iplug.opensearch.query.OSDescriptor;
import de.ingrid.iplug.opensearch.query.OSDescriptorBuilder;
import de.ingrid.iplug.opensearch.query.OSQuery;
import de.ingrid.iplug.opensearch.query.OSQueryBuilder;
import de.ingrid.iplug.opensearch.query.OSRequest;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.IRecordLoader;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.tool.SpringUtil;

/**
 * iPlug for connecting CSW services to Ingrid 1.0
 * 
 * @author rschaefer
 */
@Service
public class OpenSearchPlug extends HeartBeatPlug implements IPlug, IRecordLoader {

	/**
	 * The logging object
	 */
	private static Log log 				= LogFactory.getLog(OpenSearchPlug.class);

	/**
	 * The <code>PlugDescription</code> object passed at startup
	 */
	private PlugDescription fPlugDesc 	= null;

	/**
	 * Workingdirectory of the iPlug instance as absolute path
	 */
	private String fWorkingDir 			= ".";

	/**
	 * Does this iPlug return ranked hits?
	 */
	private boolean fIsRanked 			= false;

	/**
	 * Unique Plug-iD
	 */
	private String fPlugID 				= null;

	/**
	 * SOAP-Service URL
	 */
	private String fServiceURL 			= null;

	/**
	 * SOAP-Service Version
	 */
//	private int fSOAPVersion 			= 0;

	/**
	 * the initial timout
	 */
	private static final int INITIALTIMEOUT = 10000;

	/**
	 * Time out for request
	 */
	private int fTimeOut 				= INITIALTIMEOUT;
	
	private IngridConverter ingridConverter;
	
	private OSDescriptor osDescriptor 	= null;
	
	
	public OpenSearchPlug() {
		super(0, null);
	}
	
	public OpenSearchPlug(int period) {
		super(period, null);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * This methode is called by the iBus to initialize the iPlug. If it was
	 * impossible to initialize the iPlug correctly, it will return no hits in
	 * case of an incomming search.
	 * 
	 * @param plugDescription
	 *            Descriptionfile for initialization
	 * @see de.ingrid.iplug.IPlug#configure(PlugDescription)
	 */
	public final void configure(final PlugDescription plugDescription) {
		log.info("Configuring OpenSearch-iPlug...");

		this.fPlugDesc = plugDescription;
		
		try {
		
			if (/*fPlugDesc.getRankingTypes().length > 0 && */fPlugDesc.getRankingTypes()[0].equals("off")) {
				this.fIsRanked = false;
			} else {
				this.fIsRanked = true;
			}
				
			this.fPlugID = fPlugDesc.getPlugId();
			this.fWorkingDir = fPlugDesc.getWorkinDirectory().getCanonicalPath();
		
			this.fServiceURL = (String) fPlugDesc.get("serviceUrl");
			//this.fSOAPVersion = Integer.parseInt((String) fPlugDesc.get("soapVersion"));
			//this.fTimeOut = Integer.parseInt((String) fPlugDesc.get("timeOut"));
			
			// TODO Disconnect iPlug from iBus if configuration wasn't succesfull
			// Throw Exception for disconnect iPlug
			// Write logging information...
	
			log.info("  - Plug-ID: " + fPlugID);
			//log.info("  - Plug-Time out: " + fTimeOut);
			log.info("  - Working directory: " + fWorkingDir);
			log.info("  - SOAP-Service URL: " + fServiceURL);
			//log.info("  - SOAP-Version: " + fSOAPVersion);
			// log.info(" - Language: " + fLanguage);
				
			log.info("Receiving OpenSearch-Descriptor ...");
			OSDescriptorBuilder descrBuilder = new OSDescriptorBuilder();
			osDescriptor = descrBuilder.receiveDescriptor(fServiceURL);
			log.info("OpenSearch-Descriptor received");
			
			ConverterFactory converterFactory = (new SpringUtil("spring/spring.xml")).getBean("opensearchConverterFactory", ConverterFactory.class);
			ingridConverter = converterFactory.getConverter(osDescriptor);
			
			// set the normalizer for the ranking
			//ingridConverter.setRankingModifier(rankingModifier);
			
			log.info("iPlug initialized; waiting for incoming queries.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * This method is called by the iBus to invoke a CSW search. If no hits can
	 * be found, an empty <code>IngridHits</code> object will be returned.
	 * 
	 * @param query
	 *            The search query. Holds search terms, clauses and fields
	 * @param start
	 *            Index of the first returned hit
	 * @param length
	 *            Number of hits to return, beginning at <code>start</code>
	 * @return Hits in the specified range
	 * @throws Exception e
	 * 
	 * @see de.ingrid.iplug.IPlug#search(de.ingrid.utils.query.IngridQuery, int, int)
	 */
	public final IngridHits search(final IngridQuery query, final int start, final int length)
	throws Exception {
		IngridHits hits = null;
		if (log.isDebugEnabled()) {
		    log.debug("Incoming query: " + query.toString() + ", start=" + start + ", length=" + length);
        }
		
		try {
			OSQueryBuilder queryBuilder = new OSQueryBuilder();
			OSQuery osQuery = queryBuilder.createQuery(query, start, length);
			
			OSCommunication comm = new OSCommunication();
			InputStream result = comm.sendRequest(OSRequest.getOSQueryString(osQuery, osDescriptor));
			hits = ingridConverter.processResult(fPlugID, result);
			
			// set the ranking received from the plugdescription
			hits.setRanked(fIsRanked);
			
			comm.releaseConnection();
		
			return hits;
		} catch (Exception se) {
			log.warn("An error has occured! Returning no hits! Exception is: " + se.getMessage());
			se.printStackTrace();
			return new IngridHits(fPlugID, 0, new IngridHit[0], fIsRanked);
		}
	}


	/**
	 * Return all metadata information for a given hit.
	 * The hit object needs to have a document id for which the informations are requested
	 * from the CSW server.
	 * 
	 * @param hit
	 * @return a record object for a given hit.
	 * @throws Exception
	 * @throws IOException
	 */
	public Record getRecord(IngridHit hit) throws Exception {
		log.debug("getRecord");
		return null;
	}

	/**
	 * Inherited by iDetailer. Should not be filled with code.
	 * 
	 * @throws Exception e
	 */
	public void close() throws Exception {
		// nothing to do.
	}

	@Override
	public IngridHitDetail getDetail(IngridHit hit, IngridQuery query,
			String[] arg2) throws Exception {
		log.debug("getOpenSearch-Detail");
		IngridHitDetail hitDetail = ingridConverter.getHitDetailFromCache(hit.getDocumentId());
		
		if (hitDetail == null) { // this shouldn't happen
			log.warn("IngridHitDetail not found in cache! Creating one with fewer information.");
			hitDetail = new IngridHitDetail(hit, hit.getString("title"), hit.getString("abstract"));
			hitDetail.setDocumentId(hit.getDocumentId());
			hitDetail.setPlugId(hit.getPlugId());
			hitDetail.setDataSourceId(hit.getDataSourceId());
			hitDetail.put("url", hit.get("url"));
		}
		
		return hitDetail;
	}

	@Override
	public IngridHitDetail[] getDetails(IngridHit[] hits, IngridQuery query,
			String[] arg2) throws Exception {
		log.debug("getOpenSearch-Details");
		IngridHitDetail[] hitDetails = new IngridHitDetail[hits.length];
		int c = 0;
		for (IngridHit singleHit : hits) {
			hitDetails[c] = getDetail(singleHit, query, arg2);
			c++;
		}
		return hitDetails;
	}
}
