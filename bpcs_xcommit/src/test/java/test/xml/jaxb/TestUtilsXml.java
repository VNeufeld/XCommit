package test.xml.jaxb;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

public class TestUtilsXml {
	static Logger log = Logger.getLogger(TestUtilsXml.class);

	@Test
	public void testUtilsXml() throws SecurityException, IOException {
		
		

		String xml = FileUtils.readFileToString(new File("C:/Temp/GetStationsRequest.txt"));
		log.info("xml = "+xml);
		
		try {
			@SuppressWarnings("unused")
			int demandedId = getDemandedObjectId(xml);
			
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	int getDemandedObjectId(String xml) throws JDOMException, IOException {
		log.info("getDemandedObjectId entry ");
		long startTime = System.currentTimeMillis();
		
		final SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		
		doc = builder.build(new StringReader(xml));

		Element rootElement = doc.getRootElement();
		Element requestElement = rootElement.getChild("RequestorRequestContent").getChild("Request");
		Element demandedObjectElement = requestElement.getChild("DemandedObjects").getChild("DemandedObject");

		int demObject = Integer.valueOf(demandedObjectElement.getTextTrim());

		log.info("getDemandedObjectId "+demObject +" exit " + ( System.currentTimeMillis() - startTime) + " ms.");
		return demObject;
		
	}
	
	
}
