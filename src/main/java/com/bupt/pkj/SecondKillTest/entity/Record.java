package com.bupt.pkj.SecondKillTest.entity;

import java.util.Date;

import org.springframework.stereotype.Component;



public class Record {
	/*
	 *  记录id
	 */
	private Integer id;
	
	/*
	 *  用户 user
	 */
	private User user;
	/*
	 * 产品
	 */
	private Product product;
	/*
	 *  状态 state 1 秒杀成功  0 秒杀失败   -1 重复秒杀  -2 系统异常
	 */
	private String state;
	/*
	 * 状态的明文标识
	 */
	private String stateInfo;
	/*
	 * 创建时间
	 */
	private Date createTime;
	
	@Override
	public String toString() {
		return "Record [id=" + id + ", user=" + user + ", product=" + product + ", state=" + state + ", stateInfo="
				+ stateInfo + ", createTime=" + createTime + "]";
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStateInfo() {
		return stateInfo;
	}
	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Record(Integer id, User user, Product product, String state, String stateInfo, Date createTime) {
		super();
		this.id = id;
		this.user = user;
		this.product = product;
		this.state = state;
		this.stateInfo = stateInfo;
		this.createTime = createTime;
	}
	public Record() {
		super();
	}
	
	
	
}
