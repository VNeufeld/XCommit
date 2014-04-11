package com.bpcs.basic.servletcontroller;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.bpcs.basic.hsgwService.BpcsHttpClient;



@Controller
public class HsgwEndPointController {

	private final static String XML_REQUEST_ATTRIBUTE_MESSAGE = "Message";
	private final static String XML_REQUEST_ATTRIBUTE_REQUEST = "request";
	
	
	@Value("${application.version}")
	private String version;

	@Value("${application.name}")
	private String appname;

	@Value("${application.buildtime}")
	private String buildtime;
	
	@Autowired
	private BpcsHttpClient httpClient;

	
	protected final Logger logger = Logger.getLogger(this.getClass());
	
	
	@RequestMapping(value = "/payment", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody 
		String payment(@RequestBody String xmlStringRequestBody, WebRequest webRequest, HttpSession session) throws JAXBException, JDOMException, IOException {
		
		String xmlString = getXmlStringFromRequestParams(webRequest); 
		if ( StringUtils.isBlank(xmlString))
			xmlString = xmlStringRequestBody;
		
		int demandedObject = getDemandedObjectId(xmlString);
		
		return getResponse();
	}
	private static String getXmlStringFromRequestParams(WebRequest webRequest) {
		String message = webRequest.getParameter(XML_REQUEST_ATTRIBUTE_MESSAGE);
		
		if (StringUtils.isEmpty(message) )
			message = webRequest.getParameter(XML_REQUEST_ATTRIBUTE_REQUEST);

		return message;
	}
	
	@SuppressWarnings("unused")
	private String getResponse() throws IOException {
		return FileUtils.readFileToString(new File("C:/Temp/DoCCPaymentResponse.xml"));
	}

	int getDemandedObjectId(String  xmlString) throws JDOMException, IOException {
		
		logger.info("getDemandedObjectId entry ");
		
		final SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new StringReader(xmlString));
		
		long startTime = System.currentTimeMillis();
		

		Element rootElement = doc.getRootElement();
		Element requestElement = rootElement.getChild("RequestorRequestContent").getChild("Request");
		Element demandedObjectElement = requestElement.getChild("DemandedObjects").getChild("DemandedObject");

		int demObject = Integer.valueOf(demandedObjectElement.getTextTrim());

		logger.info("getDemandedObjectId "+demObject +" exit " + ( System.currentTimeMillis() - startTime) + " ms.");
		return demObject;
		
	}


}