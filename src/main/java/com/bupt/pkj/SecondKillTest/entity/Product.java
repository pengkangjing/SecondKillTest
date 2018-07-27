package com.bupt.pkj.SecondKillTest.entity;

import java.math.BigDecimal;



import java.util.Date;

import org.springframework.stereotype.Component;


public class Product {
    /*
     * 产品id
     */
	private Integer id;
	/*
	 * 产品名称
	 */
	private String productName;
	/*
	 * 产品价格
	 */
	private BigDecimal price;
	/*
	 *  产品库存
	 */
	private int stock;
	/*
	 *  版本号
	 */
	private int version;
	/*
	 * 创建时间
	 */
	private Date creatTime;

	public Product() {
		super();
	}

	public Product(Integer id, String productName, BigDecimal price, int stock, int version, Date creatTime) {
		super();
		this.id = id;
		this.productName = productName;
		this.price = price;
		this.stock = stock;
		this.version = version;
		this.creatTime = creatTime;
	}

	public Product(Integer id){
		this.id = id;
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", productName=" + productName + ", price=" + price + ", stock=" + stock
				+ ", version=" + version + ", creatTime=" + creatTime + "]";
	}
    
	
}
