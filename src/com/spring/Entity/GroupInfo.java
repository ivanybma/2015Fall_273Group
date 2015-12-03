package com.spring.Entity;

import java.util.List;

public class GroupInfo {

	private static final long serialVersionUID = -7780098177799873712L;
	private String endpoint_client_name;
	private String groupname;
	private List<String> devices;
	private List<Msg> msglist;
	
	public String getEndpoint_client_name() {
		return endpoint_client_name;
	}
	public void setEndpoint_client_name(String endpoint_client_name) {
		this.endpoint_client_name = endpoint_client_name;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public List<String> getDevices() {
		return devices;
	}
	public void setDevices(List<String> devices) {
		this.devices = devices;
	}
	public List<Msg> getMsglist() {
		return msglist;
	}
	public void setMsglist(List<Msg> msglist) {
		this.msglist = msglist;
	}
	
	
}
