package com.bupt.pkj.SecondKillTest.exception;

import com.bupt.pkj.SecondKillTest.common.SecondKillEnum;


@SuppressWarnings("serial")

public class SecondKillException extends RuntimeException {
	
	private SecondKillEnum secondKillEnum;
	
	public SecondKillException(SecondKillEnum secondKillEnum){
		this.secondKillEnum = secondKillEnum;
	}
	public SecondKillEnum getSecondKillEnum() {
		return secondKillEnum;
	}

}
