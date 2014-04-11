/**
 * 
 */
package com.bpcs.basic.hsgwService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bpcs.basic.converter.XmlMarshaller;
import com.bpcs.framework.exception.InternalException;

/**
 * @author neu
 *
 */
@Service
class HsgwServiceImpl  implements HsgwService {

	protected final Logger logger = Logger.getLogger(this.getClass());

	@Value("${application.logging.filter}")
	private String filterExclusion;

	@Autowired
	private BpcsHttpClient httpClient;

	@Override
	public String getHsgwConnection() {
		try {
			return this.httpClient.getHsgwConnection();
			
		}catch(Exception err) {
			logger.error(" getHsgwConnection ",err);
			return err.getMessage();
		}
	}
	
	@Override
	public String executeToString( Object legacyRequest) {
		
		assert (legacyRequest != null);
		
		long startTime = System.currentTimeMillis();
		logger.info("hsgw request: " + legacyRequest.getClass().getName());
		
		XmlMarshaller xmlMarshaller = new XmlMarshaller(filterExclusion);
		
		String hsgwXmlRequest = xmlMarshaller.marshallToXML(legacyRequest);

		logger.info("hsgw request: " + hsgwXmlRequest);
	
		String hsgwXmlresponse = null;
		try {
			hsgwXmlresponse = httpClient.executeHsgwPostRequest(hsgwXmlRequest);
		} catch (Exception e) {
			logger.error("hsgw error",e);
			throw new InternalException("hsgw error : "+e.getMessage(), e);
		}

		logger.info(" end hsgwService. Response : " + hsgwXmlresponse + " time = " + (System.currentTimeMillis() - startTime) + " ms.");
		return hsgwXmlresponse;
	}
}