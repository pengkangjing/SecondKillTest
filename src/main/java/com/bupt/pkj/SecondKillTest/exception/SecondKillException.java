package com.bupt.pkj.SecondKillTest.exception;

import com.bupt.pkj.SecondKillTest.common.SecondKillEnum;


@SuppressWarnings("serial")

public class SecondKillException extends RuntimeException {     //所有的异常全部为运行时异常不需捕获     秒杀出问题 全部抛出异常  由 异常通知处理
	
	private SecondKillEnum secondKillEnum;     
	
	public SecondKillException(SecondKillEnum secondKillEnum){
		this.secondKillEnum = secondKillEnum;
	}
	public SecondKillEnum getSecondKillEnum() {
		return secondKillEnum;
	}
	public void setSecondKillEnum(SecondKillEnum secondKillEnum) {
		this.secondKillEnum = secondKillEnum;
	}
   
}
