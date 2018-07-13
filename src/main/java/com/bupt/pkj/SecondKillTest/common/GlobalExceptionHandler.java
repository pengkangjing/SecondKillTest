package com.bupt.pkj.SecondKillTest.common;

import com.bupt.pkj.SecondKillTest.common.Message;
import com.bupt.pkj.SecondKillTest.exception.SecondKillException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class GlobalExceptionHandler {
			
	private static final Logger logger =  LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(value = SecondKillException.class)
	@ResponseBody
	public Message<Object> handleSecondKillException(SecondKillException secondKillException){
		
		logger.info(secondKillException.getSecondKillEnum().getMessage());
		return new Message<Object>(secondKillException.getSecondKillEnum());
		
	}
	
}
