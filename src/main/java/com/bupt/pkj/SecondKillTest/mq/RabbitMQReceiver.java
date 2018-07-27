package com.bupt.pkj.SecondKillTest.mq;

import javax.annotation.PostConstruct;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bupt.pkj.SecondKillTest.entity.Record;
import com.bupt.pkj.SecondKillTest.mapper.SecondKillMapper;
import com.rabbitmq.client.Channel;

@Component
public class RabbitMQReceiver implements ChannelAwareMessageListener,RabbitTemplate.ReturnCallback {

	@Autowired
	private SecondKillMapper secondKillMapper;
	
	@Autowired
	private ConnectionFactory initConnectionFactory;
	
	@PostConstruct
	public void initReciver(){
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(initConnectionFactory);
	   
	    MessageListenerAdapter adapter = new MessageListenerAdapter(this);
	    container.setMessageListener(adapter);
	    container.setQueueNames("secondKillQueue");
	    container.start();
	    
	}
	
	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
		// TODO Auto-generated method stub
		//channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); 
	}

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		// TODO Auto-generated method stub
		
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); 
        System.out.println(" [pkj] Received '" + message.toString() + "'");
		
		Record record = (Record) JSON.parseObject(message.toString(),new TypeReference<Record>(){});    //将消息json字符串 解析成record
		System.out.println(" [PKJ] Received '" + message.toString() + "'");
		//竞争全部在redis  秒杀成功之后才会将秒杀成的记录   更新到数据库    数据库没有任何压力   
		
		secondKillMapper.insertRecord(record);     //向数据库中插入记录                                   
			
		secondKillMapper.updateByAsynPattern(record.getProductId());   //异步更新库存
		
		
	}
	
	
}
