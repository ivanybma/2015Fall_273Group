package com.spring.Entity;

import java.util.List;

public class DeviceGroupSegment {

		private static final long serialVersionUID = -7780018177799873712L;
		private List<String> list;
		private String active;
		private List<GroupRequest> gprst;
		
		public List<GroupRequest> getGprst() {
			return gprst;
		}
		public void setGprst(List<GroupRequest> gprst) {
			this.gprst = gprst;
		}
		public List<String> getList() {
			return list;
		}
		public void setList(List<String> list) {
			this.list = list;
		}
		public String getActive() {
			return active;
		}
		public void setActive(String active) {
			this.active = active;
		}





	
	
}
