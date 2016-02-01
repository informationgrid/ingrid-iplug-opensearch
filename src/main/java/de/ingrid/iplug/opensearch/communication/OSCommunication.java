/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.iplug.opensearch.communication;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class handles the communication to an OpenSearch-Interface and is used to
 * send requests and receive the responses.
 * 
 * @author André Wallat
 *
 */
public class OSCommunication {
	 /* The logging object
	 */
	private static Log log = LogFactory.getLog(OSCommunication.class);
	 
	private HttpMethod method = null;
	
	public OSCommunication() {}
	
	/**
	 * Send a request with all parameters within the "url"-String.
	 * 
	 * @param url is the URL to request including the parameters
	 * @return the response of the request
	 */
	public InputStream sendRequest(String url) {
		try {
		    HttpClientParams httpClientParams = new HttpClientParams();
	        HttpConnectionManager httpConnectionManager = new SimpleHttpConnectionManager();
	        httpClientParams.setSoTimeout(30 * 1000);
	        httpConnectionManager.getParams().setConnectionTimeout(30 * 1000);
	        httpConnectionManager.getParams().setSoTimeout(30 * 1000);

			HttpClient client = new HttpClient(httpClientParams, httpConnectionManager);
			method = new GetMethod(url);
			
			// set a request header
			// this can change in the result of the response since it might be 
			// interpreted differently
			//method.addRequestHeader("Accept-Language", language); //"de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4");
			
			int status = client.executeMethod(method);
			if (status == 200) {
				log.debug("Successfully received: " + url);
				return method.getResponseBodyAsStream();
			} else {
				log.error("Response code for '" + url + "' was: " + status);
				return null;
			}
		} catch (HttpException e) {
			log.error("An HTTP-Exception occured when calling: " + url);
			e.printStackTrace();
		} catch (IOException e) {
			log.error("An IO-Exception occured when calling: " + url);
			e.printStackTrace();
		}
		return null;		
	}
	
	/**
	 * Close the connection.
	 */
	public void releaseConnection() {
		method.releaseConnection();
	}
}
