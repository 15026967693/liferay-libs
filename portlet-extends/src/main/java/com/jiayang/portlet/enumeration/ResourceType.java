package com.jiayang.portlet.enumeration;


public enum ResourceType {
JSON("application/json;"),FILE("application/octet-stream;");
	private String MIMEType;
 ResourceType(String MIMEType) {
	this.MIMEType=MIMEType;
}
 public String getMIMEType() {
	 return this.MIMEType;
 }
}
