package com.bupt.pkj.SecondKillTest.entity;

import java.util.Date;


import org.springframework.stereotype.Component;



public class Record {
	/*
	 *  记录id
	 */
	private Integer id;
	
	/*
	 *  用户 userId
	 */
	private Integer userId;
	/*
	 *  用户 userName
	 */
	private String userName;
	/*
	 * 产品productId
	 */
	private Integer productId;
	/*
	 * 产品productName
	 */
	private String productName;
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
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
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
	@Override
	public String toString() {
		return "Record [id=" + id + ", userId=" + userId + ", userName=" + userName + ", productId=" + productId
				+ ", productName=" + productName + ", state=" + state + ", stateInfo=" + stateInfo + ", createTime="
				+ createTime + "]";
	}
	public Record(Integer id, Integer userId, String userName, Integer productId, String productName, String state,
			String stateInfo, Date createTime) {
		super();
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.productId = productId;
		this.productName = productName;
		this.state = state;
		this.stateInfo = stateInfo;
		this.createTime = createTime;
	}
	public Record() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
}
