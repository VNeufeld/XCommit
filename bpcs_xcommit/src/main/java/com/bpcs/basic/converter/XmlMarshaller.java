package com.bpcs.basic.converter;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.bpcs.framework.exception.MarshallingException;
import com.bpcs.framework.filter.FilterUtils;

public class XmlMarshaller {
	private static Logger logger = Logger.getLogger(XmlMarshaller.class);
	
	private String filterExclusion = null;
	
	public XmlMarshaller(String filterExclusion) {
		this.filterExclusion = filterExclusion;
	}

	public XmlMarshaller() {
	}

	public String marshallToXML(Object requestorRequest) {
		
		assert (requestorRequest != null);
		
		
		Marshaller marshaller;
		try {
			marshaller = JAXBContext.newInstance(requestorRequest.getClass()).createMarshaller();
		} catch (JAXBException e) {
			throw new MarshallingException("could not create JAXBContext", e);
		}
	
		try {
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		} catch (PropertyException e) {
			throw new MarshallingException("could not setup formatted output for marshalling", e);
		}
	
		StringWriter writer = new StringWriter();
	
		try {
			marshaller.marshal(requestorRequest, writer);
		} catch (JAXBException e) {
			throw new MarshallingException("could not marshall", e);
		}
	
		String xmlRequest = writer.toString();
	
		if (logger.isDebugEnabled()) {
			String[] keys = new String[] { "account"};
			if ( filterExclusion != null )
				keys = filterExclusion.split(",");
			
			String xmlrequestClone = new String(xmlRequest);
			
			xmlrequestClone = FilterUtils.shadowKeyWordsXml(xmlrequestClone,keys);
			
			logger.debug("xmlRequest " + xmlrequestClone);
		}
		return xmlRequest;
	}

	public Object unmarshallFromXML(String hsgwXmlresponse, Class<?> responseClass) throws JAXBException {

		assert (responseClass != null);
		assert (hsgwXmlresponse != null);
		
		if (logger.isDebugEnabled()) {
			printXml(hsgwXmlresponse);
		}
		
		JAXBContext context = JAXBContext.newInstance(responseClass);
	
		javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
	
		StringReader reader = new StringReader(hsgwXmlresponse);
	
		return unmarshaller.unmarshal(reader);
	}


	private void printXml(String xmlString) {
		long startTime = System.currentTimeMillis();
		final SAXBuilder builder = new SAXBuilder();

		try {

			Document doc = builder.build(new StringReader(xmlString));
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			String xml = outputter.outputString(doc);

			
			String[] keys = new String[] { "account"};
			if ( filterExclusion != null )
				keys = filterExclusion.split(",");
			
			xml = FilterUtils.shadowKeyWordsXml(xml,keys);

			logger.debug("xml = " + xml);

			logger.debug("print time = "+ ( System.currentTimeMillis() - startTime) + " ms. ");

		} catch (Exception e) {
			logger.warn("error in response logging : ",e);
		}

	}
}
