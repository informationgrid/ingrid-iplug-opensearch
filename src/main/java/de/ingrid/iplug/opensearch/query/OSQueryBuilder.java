package de.ingrid.iplug.opensearch.query;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.utils.IngridQueryTools;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;


public class OSQueryBuilder {
	/* The logging object
	 */
	private static Log log = LogFactory.getLog(OSQueryBuilder.class);
	
	//which parameter were offered by the descriptor 
	List<String> templateParameter = new ArrayList<String>();

	
	/**
	 * recognize all available parameters for an OpenSearch query 
	 */
	/*private void checkAvailableParameters() {
		String[] os_params = {
				OSQuery.OS_SEARCH_TERMS,OSQuery.OS_COUNT,OSQuery.OS_START_INDEX,
				OSQuery.OS_START_PAGE,OSQuery.OS_LANGUAGE,OSQuery.OS_INPUT_ENCODING,
				OSQuery.OS_OUTPUT_ENCODING
		};
		for (String param : os_params) {
			if (StringUtils.parameterExists(templateQuery, param)) {
				templateParameter.add(param);
			}
		}
	}*/

	public OSQuery createQuery(IngridQuery ingridQuery, int start, int length) {
		OSQuery osQuery = new OSQuery();
		
		// this is a must have parameter
		osQuery.put(OSQuery.OS_SEARCH_TERMS, getSearchTerms(ingridQuery));
		
		osQuery.put(OSQuery.OS_COUNT, String.valueOf(length));
		
		osQuery.put(OSQuery.OS_START_INDEX, String.valueOf(start));
		
		osQuery.put(OSQuery.OS_START_PAGE, String.valueOf((start/length)+1));
				
		osQuery.put(OSQuery.OS_LANGUAGE, getLanguage(ingridQuery));
		
		osQuery.put(OSQuery.OS_INPUT_ENCODING, getInputEncoding(ingridQuery));
		
		osQuery.put(OSQuery.OS_OUTPUT_ENCODING, getOutputEncoding(ingridQuery));
		
		osQuery.put(OSQuery.OS_GEO_BBOX, getBoundingBox(ingridQuery));
		
		return osQuery;
	}

	private String getBoundingBox(IngridQuery ingridQuery) {
		String bbox = "";
		IngridQueryTools qTools = new IngridQueryTools();
		try {
			bbox += qTools.getFieldValueByKey(ingridQuery, "x1")[0] + ",";
			bbox += qTools.getFieldValueByKey(ingridQuery, "y1")[0] + ",";
			bbox += qTools.getFieldValueByKey(ingridQuery, "x2")[0] + ",";
			bbox += qTools.getFieldValueByKey(ingridQuery, "y2")[0];
		} catch (Exception e) {
			// no bounding box given
			bbox = null;
		}
		
		return bbox;
	}

	private String getOutputEncoding(IngridQuery ingridQuery) {
		// not supported yet
		return null;
	}

	private String getInputEncoding(IngridQuery ingridQuery) {
	    // not supported yet
		return null;
	}

	private String getLanguage(IngridQuery ingridQuery) {
		
	    // not supported yet
		return null;
	}

	private String getSearchTerms(IngridQuery ingridQuery) {
		String terms = "";
		TermQuery[] ingridTerms = ingridQuery.getTerms();
		for (TermQuery term : ingridTerms) {
			terms += term.getContent() + " ";
		}
		// also include fields in search-term of OS-Query
		/*
		for (FieldQuery field : ingridQuery.getFields()) {
		    if (fieldNotExcluded(field)) {
		        terms += field.getContent() + " ";
		    }
        }
        */
		try {
			return URLEncoder.encode(terms.trim(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Problems during encoding search terms: " + terms);
			e.printStackTrace();
		}
		return null;
	}

    private boolean fieldNotExcluded(FieldQuery field) {
        String fieldName = field.getFieldName();
        if (fieldName.equals("incl_meta"))
            return false;
        
        // return true if field must not be excluded
        return true;
    }
}
