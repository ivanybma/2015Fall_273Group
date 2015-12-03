package com.spring.Entity;

import java.util.List;

public class GroupMsgRtv {

	private static final long serialVersionUID = -7780018177799873712L;
	
	private String groupname;
	private List<Msg> msglist;
	
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public List<Msg> getMsglist() {
		return msglist;
	}
	public void setMsglist(List<Msg> msglist) {
		this.msglist = msglist;
	}

	
}
