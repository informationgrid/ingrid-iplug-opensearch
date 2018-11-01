/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.opensearch;

import com.thoughtworks.xstream.XStream;
import de.ingrid.admin.JettyStarter;
import de.ingrid.iplug.HeartBeatPlug;
import de.ingrid.iplug.IPlugdescriptionFieldFilter;
import de.ingrid.iplug.PlugDescriptionFieldFilters;
import de.ingrid.iplug.opensearch.communication.OSCommunication;
import de.ingrid.iplug.opensearch.converter.ConverterFactory;
import de.ingrid.iplug.opensearch.converter.IngridConverter;
import de.ingrid.iplug.opensearch.model.OSMapping;
import de.ingrid.iplug.opensearch.query.*;
import de.ingrid.search.utils.facet.FacetManager;
import de.ingrid.search.utils.facet.IFacetManager;
import de.ingrid.utils.*;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.metadata.IMetadataInjector;
import de.ingrid.utils.processor.IPostProcessor;
import de.ingrid.utils.processor.IPreProcessor;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.tool.QueryUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
	
	// injected by Spring!
	private ConverterFactory converterFactory;

    private List<OSMapping> mapping;

    // injected by Spring!
    private OSQueryBuilder queryBuilder;
    
    @Autowired
    private IFacetManager facetManager;

    @Autowired
	private Configuration opensearchConfig;

	@Autowired
	public OpenSearchPlug(IPlugdescriptionFieldFilter[] fieldFilters, 
			IMetadataInjector[] injector,
			IPreProcessor[] preProcessors,
			IPostProcessor[] postProcessors) {
		super(30000, new PlugDescriptionFieldFilters(fieldFilters), injector, preProcessors, postProcessors);
	}
	
	/**
	 * This methode is called by the iBus to initialize the iPlug. If it was
	 * impossible to initialize the iPlug correctly, it will return no hits in
	 * case of an incomming search.
	 * 
	 * @param plugDescription
	 *            Descriptionfile for initialization
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
			this.fUseDescriptor  = opensearchConfig.useDescriptor;
			this.fServiceURL = opensearchConfig.serviceUrl;
			
			XStream xstream = new XStream();
            //mapping = (List<OSMapping>) fPlugDesc.get("mapping");
			mapping = (List<OSMapping>)(List<?>) xstream.fromXML(opensearchConfig.mapping);
			
			if (mapping == null)
			    mapping = new ArrayList<OSMapping>();
			
			// TODO Disconnect iPlug from iBus if configuration wasn't succesfull
			// Throw Exception for disconnect iPlug
			// Write logging information...
	
			log.info("  - Plug-ID: " + fPlugID);
			log.info("  - SOAP-Service URL: " + fServiceURL);
				
			log.info("Receiving OpenSearch-Descriptor ... using one: " + fUseDescriptor);
			OSDescriptorBuilder descrBuilder = new OSDescriptorBuilder();
			osDescriptor = descrBuilder.createDescriptor(fServiceURL, fUseDescriptor);
			log.info("OpenSearch-Descriptor received");
			
			ingridConverter = converterFactory.getConverter(osDescriptor);
			
            if (facetManager != null) {
                facetManager.initialize();
            }
			
			log.info("iPlug initialized; waiting for incoming queries.");
		} catch (IOException e) {
			log.error("Error during configuration", e);
		} catch (Exception e) {
			log.error("Error reading Descriptor: ", e);
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
	 */
	public final IngridHits search(final IngridQuery query, final int start, final int length)
	throws Exception {
		IngridHits hits 	= null;
		InputStream result 	= null;
		String url 			= null;
		
		if (log.isDebugEnabled()) {
		    log.debug("Incoming query: " + query.toString() + ", start=" + start + ", length=" + length);
        }

		// HANDLE METAINFO !!! e.g. add "reject search" metainfo etc.
        preProcess(query);

        // check if query is rejected and return 0 hits instead of search within the iplug
        if (query.isRejected()) {
            return new IngridHits(fPlugID, 0, new IngridHit[] {}, fIsRanked);
        }
        
    	// remove "meta" field from query so search works !
    	QueryUtil.removeFieldFromQuery(query, QueryUtil.FIELDNAME_METAINFO);

		if (log.isDebugEnabled()) {
		    log.debug("After preprocessing -> query: " + query.toString() + ", start=" + start + ", length=" + length);
        }

		try {
		    // check if possibly fields are supported by this iPlug
			// DISABLED! See INGRID-2214
		    /*if (!allFieldsSupported(query)) {
		        log.warn("Not all fields of this query are supported! Returning zero hits! (query: "+query.toString()+")");
		        return new IngridHits(fPlugID, 0, new IngridHit[0], fIsRanked);
		    }*/
		    
			OSQuery osQuery = queryBuilder.createQuery(query, start, length, mapping);
			
			OSCommunication comm = new OSCommunication();
			url = OSRequest.getOSQueryString(osQuery, query, osDescriptor, opensearchConfig);
			result = comm.sendRequest(url);
			hits = ingridConverter.processResult(fPlugID, result, query.getGrouped());
			
			// set the ranking received from the plugdescription
			if (hits != null)
				hits.setRanked(fIsRanked);
			
			comm.releaseConnection();
		
	        facetManager.addFacets(hits, query);
			
			return hits;
		} catch (Exception se) {
			if (result == null) {
				log.error("Could not receive answer from: " + url, se);
			} else {
				log.warn("An error has occured! Returning no hits!", se);
			}
			return new IngridHits(fPlugID, 0, new IngridHit[0], fIsRanked);
		}
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
	
	@Autowired
    public void setQueryBuilder(OSQueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public void setFacetManager(FacetManager facetManager) {
        this.facetManager = facetManager;
    }
	
    public static void main(String[] args) throws Exception {
        new JettyStarter();
    }

    @Override
    public IngridDocument call(IngridCall targetInfo) {
        throw new RuntimeException( "call-function not implemented in OpenSearch-iPlug" );
    }
}
