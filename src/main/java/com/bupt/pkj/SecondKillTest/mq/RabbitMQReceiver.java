package com.bupt.pkj.SecondKillTest.mq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bupt.pkj.SecondKillTest.entity.Record;
import com.bupt.pkj.SecondKillTest.mapper.SecondKillMapper;

@Component
@RabbitListener(queues = "SecondKillQueue")
public class RabbitMQReceiver {

	@Autowired
	private SecondKillMapper secondKillMapper;
	
	@RabbitHandler
	public void process(String message) throws Exception{
		Record record = JSON.parseObject(message,new TypeReference<Record>(){});
		
		secondKillMapper.insertRecord(record);
			
		secondKillMapper.updateByAsynPattern(record.getProduct());
	}
	
}
