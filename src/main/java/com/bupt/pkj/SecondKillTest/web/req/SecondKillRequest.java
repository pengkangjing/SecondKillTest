package com.bupt.pkj.SecondKillTest.web.req;


public class SecondKillRequest {

    public Integer getUserId() {
		return userId;
	}


	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	private Integer userId;

    private Integer productId;


    @Override
	public String toString() {
		return "SecondKillRequest [userId=" + userId + ", productId=" + productId + "]";
	}


	public SecondKillRequest(Integer userId, Integer productId) {
		super();
		this.userId = userId;
		this.productId = productId;
	}


	public SecondKillRequest() {
		super();
	}
    
}