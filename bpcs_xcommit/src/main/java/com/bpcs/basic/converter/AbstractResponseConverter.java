package com.bpcs.basic.converter;

import java.math.BigInteger;


public abstract class AbstractResponseConverter {

	final XmlMarshaller xmlMarshaller;
	
	private final  static int PAYPALSTATUS_VALIDATED  = 1;    //  'Completed'

	private final  static int PAYPALSTATUS_FAILED  = 2;       //  'Failed

	private final  static int PAYPALSTATUS_PENDING = 5;    //  'Pending'

	private final  static String PAYPALSTATUS_PENDING_TEXT = "Pending";
	private final  static String PAYPALSTATUS_COMPLETED_TEXT = "Completed";

	private final  static String PAYPALSTATUS_DELAYED_TEXT = "delayed";
	private final  static String PAYPALSTATUS_INSTANT_TEXT = "instant";


	public AbstractResponseConverter(String filterExclusion) {
		super();
		this.xmlMarshaller = new XmlMarshaller(filterExclusion);
	}

	protected String convertToXml(Object response) {
		
		return xmlMarshaller.marshallToXML(response);
	}
	
	static int createResultStatus(String paymentStatus) {
		if ( PAYPALSTATUS_COMPLETED_TEXT.equalsIgnoreCase(paymentStatus) || PAYPALSTATUS_INSTANT_TEXT.equalsIgnoreCase(paymentStatus) )
			return PAYPALSTATUS_VALIDATED;
		else if ( PAYPALSTATUS_PENDING_TEXT.equalsIgnoreCase(paymentStatus) || PAYPALSTATUS_DELAYED_TEXT.equalsIgnoreCase(paymentStatus) )
			return PAYPALSTATUS_PENDING;
		return PAYPALSTATUS_FAILED;
	}

	static BigInteger createIsPaidRefundFlag(String paymentStatus) {
		BigInteger isPaid = BigInteger.ZERO;
		if ( PAYPALSTATUS_INSTANT_TEXT.equalsIgnoreCase(paymentStatus))
			isPaid = BigInteger.ONE;
		
		return isPaid;
	}
	
	static BigInteger createIsPaidPaymentFlag(String paymentStatus) {
		BigInteger isPaid = BigInteger.ZERO;
		if ( PAYPALSTATUS_COMPLETED_TEXT.equalsIgnoreCase(paymentStatus))
			isPaid = BigInteger.ONE;
		
		return isPaid;
	}


}
