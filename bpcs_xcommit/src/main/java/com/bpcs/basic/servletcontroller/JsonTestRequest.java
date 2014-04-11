package com.bpcs.basic.servletcontroller;

public class JsonTestRequest {
	
	private String puDate;
	private String doDate;
	private String amount;
	private String currency;
	private int    groupId;
	private int    stationId;
	private String groupName;
	
	public String getPuDate() {
		return puDate;
	}
	public void setPuDate(String puDate) {
		this.puDate = puDate;
	}
	public String getDoDate() {
		return doDate;
	}
	public void setDoDate(String doDate) {
		this.doDate = doDate;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getStationId() {
		return stationId;
	}
	public void setStationId(int stationId) {
		this.stationId = stationId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
