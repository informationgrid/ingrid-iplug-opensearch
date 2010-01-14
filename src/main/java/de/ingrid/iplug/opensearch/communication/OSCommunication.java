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

public class OSCommunication {
	 /* The logging object
	 */
	private static Log log = LogFactory.getLog(OSCommunication.class);
	 
	private HttpMethod method = null;
	
	public OSCommunication() {
		
	}
	
	public InputStream sendRequest(String url) {
		try {
		    HttpClientParams httpClientParams = new HttpClientParams();
	        HttpConnectionManager httpConnectionManager = new SimpleHttpConnectionManager();
	        httpClientParams.setSoTimeout(30 * 1000);
	        httpConnectionManager.getParams().setConnectionTimeout(30 * 1000);
	        httpConnectionManager.getParams().setSoTimeout(30 * 1000);

			HttpClient client = new HttpClient(httpClientParams, httpConnectionManager);
			method = new GetMethod(url);
		
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
	
	public void releaseConnection() {
		method.releaseConnection();
	}
}
