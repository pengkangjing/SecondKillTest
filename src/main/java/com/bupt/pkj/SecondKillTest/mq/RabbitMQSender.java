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
public class RabbitMQSender implements RabbitTemplate.ConfirmCallback{
	
	private static final Logger logger = LoggerFactory.getLogger(RabbitMQSender.class);
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public void send(String message){
		rabbitTemplate.setConfirmCallback(this);
		CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
		rabbitTemplate.convertAndSend("seckillExchange","seckillRoutingKey",message,correlationData);
		
	}
	@Override
	public void confirm(CorrelationData correlationData, boolean ack,String cause){
		
		logger.info("callback confirm :"+correlationData.getId());
		
		if(ack){
			
			logger.info("插入Record成功，更改库存成功");
			
		}
		else{
			
			logger.info("cause:"+cause);
		}
		
	}
	
	

}
