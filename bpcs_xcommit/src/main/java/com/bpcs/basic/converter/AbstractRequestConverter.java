package com.bpcs.basic.converter;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.bpcs.framework.exception.MarshallingException;

public abstract class AbstractRequestConverter {
	private static Logger logger = Logger.getLogger(AbstractRequestConverter.class);
	final XmlMarshaller xmlMarshaller;

	public AbstractRequestConverter(String filterExclusion) {
		super();
		this.xmlMarshaller = new XmlMarshaller(filterExclusion);
	}
	
	protected Object convertFromXml(String xmlString,Class<?> clazz) {
		
		try {
			return xmlMarshaller.unmarshallFromXML(xmlString, clazz) ;
		} catch (JAXBException e) {
			logger.error("",e);
			throw new MarshallingException("converter error", e);
		}
	}


}
