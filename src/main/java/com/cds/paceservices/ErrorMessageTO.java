package com.cds.paceservices;

public class ErrorMessageTO {
	private int MessageCode;
	private String MessageDescription;
	
	public String getMessageDescription() {
		return MessageDescription;
	}
	public void setMessageDescription(String messageDescription) {
		MessageDescription = messageDescription;
	}
	public int getMessageCode() {
		return MessageCode;
	}
	public void setMessageCode(int messageCode) {
		MessageCode = messageCode;
	}
}
