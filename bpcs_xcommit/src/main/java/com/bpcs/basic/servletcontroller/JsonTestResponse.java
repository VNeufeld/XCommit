package com.bpcs.basic.servletcontroller;

public class JsonTestResponse {
	private String token;
	private String paypalUrl;
	
	private String error;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPaypalUrl() {
		return paypalUrl;
	}
	public void setPaypalUrl(String paypalUrl) {
		this.paypalUrl = paypalUrl;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

}
