package com.example.demo.controllers;

public class Heartbeat {
	
	private String gw_uuid = null;
	
	private long timestamp;
	
	private String status = null;

	public String getGw_uuid() {
		return gw_uuid;
	}

	public void setGw_uuid(String gw_uuid) {
		this.gw_uuid = gw_uuid;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	

}
