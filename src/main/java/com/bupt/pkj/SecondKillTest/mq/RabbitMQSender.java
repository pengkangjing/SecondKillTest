package com.bupt.pkj.SecondKillTest.mq;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * 可靠确认模式
 */

@Component
public class RabbitMQSender implements RabbitTemplate.ConfirmCallback{   //实现确认回调接口
	
	private static final Logger logger = LoggerFactory.getLogger(RabbitMQSender.class);
	
	
	
	@Autowired
	public RabbitTemplate rabbitTemplate;
	
	
	
	public void send(String message){
		rabbitTemplate.setConfirmCallback(this);    //设置回调接口
		CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());  //版本号
		rabbitTemplate.convertAndSend("secondKillExchange","secondKillRoutingKey",message,correlationData);   //发送消息
		                                    
	}
	@Override
	public void confirm(CorrelationData correlationData, boolean ack,String cause){ //实现接口
		
		logger.info("callback confirm :"+correlationData.getId());
		
		if(ack){
			
			logger.info("插入Record成功，更改库存成功");
			
		}
		else{
			
			logger.info("cause:"+cause);
		}
	}
	
	
	

}
