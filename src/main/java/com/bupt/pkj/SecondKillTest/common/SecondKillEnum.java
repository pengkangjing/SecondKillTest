package com.bupt.pkj.SecondKillTest.common;

import com.bupt.pkj.SecondKillTest.constant.secondKillStateConstant;

public enum SecondKillEnum {
	/*
	 * 服务级错误
	 */
	SUCCESS(secondKillStateConstant.SUCCESS,"秒杀成功"), 
	LOW_STOCKS(secondKillStateConstant.FAIL,"库存不足"),          
	FAIL(secondKillStateConstant.FAIL,"秒杀失败"),        
	REPEAT(secondKillStateConstant.REPEAT,"重复秒杀"),  
	SYSTEM_EXCEPTION(secondKillStateConstant.SYSTEM_EXCEPTION,"系统错误");  
	
	private String code;
	
	private String message;
	
	SecondKillEnum(String code,String message){
		this.code = code;
		this.message = message;
	}
	public String getCode(){
		return this.code;
	}
	public String getMessage(){
		return this.message;
	}
}
