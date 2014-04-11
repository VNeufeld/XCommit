package com.bpcs.basic.servletcontroller;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
public class JsonEndPointController {
	
	protected final Logger logger = Logger.getLogger(this.getClass());
	
	
	@RequestMapping(value = "/appSetCheckout", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	public @ResponseBody
	JsonTestResponse setCheckoutForApp(
			@RequestBody @Validated JsonTestRequest appRequest,
			HttpSession session) {
		
		JsonTestResponse appResult = new JsonTestResponse();
		appResult.setPaypalUrl("test url ");
		appResult.setToken(" test token ");
		
		return appResult;

	}


}