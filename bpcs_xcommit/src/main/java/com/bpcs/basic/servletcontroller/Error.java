package com.bpcs.basic.servletcontroller;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name="error")
public class Error extends BasicProtocol {

	/**
	 * 
	 */
	private static final long serialVersionUID = 753760186633604377L;

	private String errorType;

	private String errorText;

	private String errorNumber;
	
	private Throwable exception;
	
	public String getErrorNumber() {
		return errorNumber;
	}

	public String getErrorText() {
		return errorText;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorNumber(String errorNumber) {
		this.errorNumber = errorNumber;
	}

	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	
	public Error() {
		
	}
	
	public Error(String errorType, String errorText, String errorNumber, Throwable exc) {
		this.errorType = errorType;
		this.errorText = errorText;
		this.errorNumber = errorNumber;
		
		this.exception = exc;
		
		
		
	}

	public Throwable getException() {
		return exception;
	}

}
