package com.bpcs.basic.servletcontroller;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CommonExceptionHandler {

	protected final Logger logger = Logger.getLogger(this.getClass());

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody com.bpcs.basic.servletcontroller.Error handleOtherException (Exception ex) {
		
		String errName = ex.getClass().getName();
		logger.error(ex.getMessage(),ex);
		
		String causeMessage = null;
		if ( ex.getCause() != null) {
			causeMessage = ex.getCause().getMessage(); 
		}
		
		String errorMessage = ex.getMessage();
		if ( causeMessage != null )
			errorMessage += causeMessage;
		com.bpcs.basic.servletcontroller.Error err = new  com.bpcs.basic.servletcontroller.Error(errName, errorMessage, "1",ex);
		
		return err;
	}
}
