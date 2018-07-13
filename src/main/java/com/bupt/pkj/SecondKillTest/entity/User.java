package com.bupt.pkj.SecondKillTest.entity;

import java.util.Date;

import org.springframework.stereotype.Component;


public class User {
	/*
	 * 主键id
	 */
	private Integer id;
	/*
	 * 用户名 username
	 */
	private String userName;
	/*
	 * 手机号码 phone
	 */
	private String phone;
	/*
	 * 创建时间 creatTime
	 */
	private Date creatTime;
	
	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", phone=" + phone + ", creatTime=" + creatTime + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public User(Integer id, String userName, String phone, Date creatTime) {
		super();
		this.id = id;
		this.userName = userName;
		this.phone = phone;
		this.creatTime = creatTime;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public User() {
		super();
	}

}
