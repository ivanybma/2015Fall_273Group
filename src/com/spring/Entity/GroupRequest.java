package com.spring.Entity;

import java.util.List;

public class GroupRequest {

	private static final long serialVersionUID = -7780098177699573722L;

	private String from;
	private String group;
	private String status;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
