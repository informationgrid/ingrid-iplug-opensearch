/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.opensearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.iplug.HeartBeatPlug;
import de.ingrid.iplug.IPlugdescriptionFieldFilter;
import de.ingrid.iplug.PlugDescriptionFieldFilters;
import de.ingrid.iplug.opensearch.communication.OSCommunication;
import de.ingrid.iplug.opensearch.converter.ConverterFactory;
import de.ingrid.iplug.opensearch.converter.IngridConverter;
import de.ingrid.iplug.opensearch.model.OSMapping;
import de.ingrid.iplug.opensearch.query.OSDescriptor;
import de.ingrid.iplug.opensearch.query.OSDescriptorBuilder;
import de.ingrid.iplug.opensearch.query.OSQuery;
import de.ingrid.iplug.opensearch.query.OSQueryBuilder;
import de.ingrid.iplug.opensearch.query.OSRequest;
import de.ingrid.iplug.opensearch.tools.QueryUtils;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.metadata.IMetadataInjector;
import de.ingrid.utils.processor.IPostProcessor;
import de.ingrid.utils.processor.IPreProcessor;
import de.ingrid.utils.query.IngridQuery;

/**
 * This iPlug connects to the iBus and offers requests to a defined Opensearch-
 * Interface. Its results will be converted into a format that the portal can
 * understand.
 * @author André Wallat
 *
 */
@Service
public class OpenSearchPlug extends HeartBeatPlug {

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

	private IngridConverter ingridConverter;
	
	private OSDescriptor osDescriptor 	= null;

	private boolean fUseDescriptor = true;
	
	private ConverterFactory converterFactory;

    private List<OSMapping> mapping;

	@Autowired
	public OpenSearchPlug(IPlugdescriptionFieldFilter[] fieldFilters, 
			IMetadataInjector[] injector,
			IPreProcessor[] preProcessors,
			IPostProcessor[] postProcessors) throws IOException {
		super(30000, new PlugDescriptionFieldFilters(fieldFilters), injector, preProcessors, postProcessors);
	}
	
	/**
	 * This methode is called by the iBus to initialize the iPlug. If it was
	 * impossible to initialize the iPlug correctly, it will return no hits in
	 * case of an incomming search.
	 * 
	 * @param plugDescription
	 *            Descriptionfile for initialization
	 * @throws IOException 
	 * @see de.ingrid.iplug.IPlug#configure(PlugDescription)
	 */
	@SuppressWarnings("unchecked")
    @Override
	public final void configure(final PlugDescription plugDescription) {
		super.configure(plugDescription);
		log.info("Configuring OpenSearch-iPlug...");		
		this.fPlugDesc = plugDescription;
		
		try {
		
			if (fPlugDesc.getRankingTypes()[0].equals("off")) {
				this.fIsRanked = false;
			} else {
				this.fIsRanked = true;
			}
				
			this.fPlugID = fPlugDesc.getPlugId();
			this.fWorkingDir = fPlugDesc.getWorkinDirectory().getCanonicalPath();
			this.fUseDescriptor  = (boolean) fPlugDesc.getBoolean("useDescriptor");
			this.fServiceURL = (String) fPlugDesc.get("serviceUrl");
			
			mapping = (List<OSMapping>) fPlugDesc.get("mapping");
			
			if (mapping == null)
			    mapping = new ArrayList<OSMapping>();
			
			// TODO Disconnect iPlug from iBus if configuration wasn't succesfull
			// Throw Exception for disconnect iPlug
			// Write logging information...
	
			log.info("  - Plug-ID: " + fPlugID);
			log.info("  - Working directory: " + fWorkingDir);
			log.info("  - SOAP-Service URL: " + fServiceURL);
				
			log.info("Receiving OpenSearch-Descriptor ... using one: " + fUseDescriptor);
			OSDescriptorBuilder descrBuilder = new OSDescriptorBuilder();
			osDescriptor = descrBuilder.createDescriptor(fServiceURL, fUseDescriptor);
			log.info("OpenSearch-Descriptor received");
			
			ingridConverter = converterFactory.getConverter(osDescriptor);
			
			log.info("iPlug initialized; waiting for incoming queries.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Error reading PlugDescription: " + e.getMessage());
			//e.printStackTrace();
		}
	}
	

	/**
	 * This method is called by the iBus to invoke an Opensearch search. If no hits can
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
		IngridHits hits 	= null;
		InputStream result 	= null;
		String url 			= null;
		
		if (log.isDebugEnabled()) {
		    log.debug("Incoming query: " + query.toString() + ", start=" + start + ", length=" + length);
        }
		
		try {
		    // check if possibly fields are supported by this iPlug
		    if (!allFieldsSupported(query)) {
		        log.warn("Not all fields of this query are supported! Returning zero hits! (query: "+query.toString()+")");
		        return new IngridHits(fPlugID, 0, new IngridHit[0], fIsRanked);
		    }
		    
			OSQueryBuilder queryBuilder = new OSQueryBuilder();
			OSQuery osQuery = queryBuilder.createQuery(query, start, length, mapping);
			
			OSCommunication comm = new OSCommunication();
			url = OSRequest.getOSQueryString(osQuery, query, osDescriptor);
			result = comm.sendRequest(url);
			hits = ingridConverter.processResult(fPlugID, result, query.getGrouped());
			
			// set the ranking received from the plugdescription
			if (hits != null)
				hits.setRanked(fIsRanked);
			
			comm.releaseConnection();
		
			return hits;
		} catch (Exception se) {
			if (result == null) {
				log.error("Could not receive answer from: " + url);
			} else {
				log.warn("An error has occured! Returning no hits! Exception is: " + se.getMessage());
				se.printStackTrace();
			}
			return new IngridHits(fPlugID, 0, new IngridHit[0], fIsRanked);
		}
	}


	/**
	 * Checks for all not supported parameters, if it is in the query. So far the fields
	 * partner, provider and site(domain) are checked.
	 * 
	 * @param query
	 * @return true if all fields in query are supported 
	 */
	@SuppressWarnings("unchecked")
    private boolean allFieldsSupported(IngridQuery query) {
	    // mapping might not be set in PlugDescription! 
	    if (fPlugDesc.get("mapping") == null || fPlugDesc.get("mappingSupport") == null) {
	        log.error("Missing mapping-information in PlugDescription!" +
	        		" PlugDescription wasn't written completely!");
	        return false;
	    }
	    
	    boolean mappingSupported = fPlugDesc.getBoolean("mappingSupport");
	    
	    for (OSMapping map : (List<OSMapping>) fPlugDesc.get("mapping")) {
	        // if mapping is not supported then check if query contains any of
	        // the fields
	        if (!mappingSupported || !map.isActive()) {
	            switch (map.getType()) {
                case PROVIDER:
                    // see case PARTNER!
                    if (query.getPositiveProvider() != null && query.getPositiveProvider().length > 0)
                        return false;
                    break;
                case PARTNER:
                    // if no partner filtering is supported then this iPlug
                    // will return no results, since different partner can occur
                    // in the results from the opensearch interface
                    if (query.getPositivePartner() != null && query.getPositivePartner().length > 0 )
                        return false;
                    break;
                case DOMAIN:
                    if (QueryUtils.getFieldValue("site", query) != null)
                        return false;
                    break;
                default:
                    break;
                }
	        }
	    }
        return true;
    }
	

    /**
	 * Return all metadata information for a given hit.
	 * This is not supported by Opensearch!
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
		IngridHitDetail[] hitDetails = new IngridHitDetail[hits.length];
		int c = 0;
		for (IngridHit singleHit : hits) {
			hitDetails[c] = getDetail(singleHit, query, arg2);
			c++;
		}
		return hitDetails;
	}

	@Autowired
	public void setConverterFactory(ConverterFactory converterFactory) {
		this.converterFactory = converterFactory;
	}
	
	/**
	 * This method creates a readable String out of an InputStream. This is
	 * only used for testing purpose!
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public String convertStreamToString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {        
            return "";
        }
    }

}