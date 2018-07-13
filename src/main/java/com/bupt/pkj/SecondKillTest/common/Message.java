package com.bupt.pkj.SecondKillTest.common;


public class Message<T> {
	
	private Head head;
	
	
	private T body;
	
	public Message() {
		super();
	}
	public Message(Head head, T body) {
		super();
		this.head = head;
		this.body = body;
	}
	public Message(SecondKillEnum resultEnum,T body){
		this.head = new Head();
		this.head.setStatusCode(resultEnum.getCode());
		this.head.setStatusMessage(resultEnum.getMessage());
		this.body = body;
	}
	public Head getHead() {
		return head;
	}
	public void setHead(Head head) {
		this.head = head;
	}
	public T getBody() {
		return body;
	}
	public void setBody(T body) {
		this.body = body;
	}
	public Message(SecondKillEnum resultEnum){
		this.head = new Head();
		this.head.setStatusCode(resultEnum.getCode());
		this.head.setStatusMessage(resultEnum.getMessage());
	}
}
