package com.bpcs.basic.hsgwService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BpcsHttpClient {

	protected final Logger logger = Logger.getLogger(this.getClass());
	
	private final String hsgwParamKey="Message";

	@Value("${hsgw.timeout}")
	private int connectionTimeoutMillis;
	@Value("${hsgw.timeout}")
	private int socketTimeoutMillis ;
	
	@Value("${hsgw.port}")
	private int port ;

	@Value("${hsgw.host}")
	private String host ;

	@Value("${hsgw.path}")
	private String path ;

	@Value("${hsgw.encoding}")
	private String encoding;
	
	
	
	public String executeHsgwPostRequest(String xmlString) throws URISyntaxException, ClientProtocolException, IOException {

		// TODO check SSL
		URI uri = URI.create(host+":"+String.valueOf(port)+path);
		
		logger.info("url : " + uri.toString());
		return startPostBodyRequest(uri,xmlString);

	}

	public String getHsgwConnection() throws URISyntaxException {

		URI uri = URI.create(host+":"+String.valueOf(port)+path);
		return uri.toString();

	}

	

	private String startPostRequest(URI uri,	String xmlString) throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(uri);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(hsgwParamKey, xmlString));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8")); 

    	DefaultHttpClient httpClient = new DefaultHttpClient();
        
        // set timeout
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeoutMillis);
        HttpConnectionParams.setSoTimeout(params, socketTimeoutMillis);

        // localcontext can be used for hold sesion
    	// hsgw is stateless
    	BasicHttpContext localContext = new BasicHttpContext();
    	// Create a response handler
    	ResponseHandler<String> responseHandler = new BasicResponseHandler();

        String response = httpClient.execute(httpPost, responseHandler,localContext);
        
        if ( logger.isDebugEnabled()) {
        	logger.debug("hsgw response = "+response);
        }

    	logger.info("hsgw serverContext = "+localContext.toString());
    	
    	if (logger.isDebugEnabled() ) {
			CookieStore cookieStore = httpClient.getCookieStore();
			logger.debug("cookieStore " + cookieStore);
    	}

		return response;
	}
	
	private String startPostBodyRequest(URI uri,	String xmlString) throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(uri);

		StringEntity entity = new StringEntity(xmlString, ContentType.create("application/xml", "UTF-8"));

		httpPost.setEntity(entity);

    	DefaultHttpClient httpClient = new DefaultHttpClient();
        
        // set timeout
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeoutMillis);
        HttpConnectionParams.setSoTimeout(params, socketTimeoutMillis);

        // localcontext can be used for hold sesion
    	// hsgw is stateless
    	BasicHttpContext localContext = new BasicHttpContext();
    	// Create a response handler
    	ResponseHandler<String> responseHandler = new BasicResponseHandler();

        String response = httpClient.execute(httpPost, responseHandler,localContext);
        
        if ( logger.isDebugEnabled()) {
        	logger.debug("hsgw response = "+response);
        }

    	logger.info("hsgw serverContext = "+localContext.toString());
    	
    	if (logger.isDebugEnabled() ) {
			CookieStore cookieStore = httpClient.getCookieStore();
			logger.debug("cookieStore " + cookieStore);
    	}

		return response;
	}

}
