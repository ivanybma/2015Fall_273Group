package com.spring.Entity;

import java.util.List;

public class Msg {

	private static final long serialVersionUID = -7780098177699573722L;
	private String sender;
	private String content;
	private String datetime;
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
}
