package com.bupt.pkj.SecondKillTest.common;

import com.bupt.pkj.SecondKillTest.common.Message;
import com.bupt.pkj.SecondKillTest.exception.SecondKillException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice                            //集中处理异常
public class GlobalExceptionHandler {
			
	private static final Logger logger =  LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(value = SecondKillException.class)    //处理SecondKillException
	@ResponseBody
	public Message<Object> handleSecondKillException(SecondKillException secondKillException){
		
		logger.info(secondKillException.getSecondKillEnum().getMessage());    //记录日志
		return new Message<Object>(secondKillException.getSecondKillEnum());   //返回json字符串响应给用户
		
	}
	
}
