package com.bupt.pkj.SecondKillTest.config;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;



@Configuration
public class RabbitmqCallBackConfig {
   
	private static final Logger logger = LoggerFactory.getLogger(RabbitmqCallBackConfig.class);
    @Value("${spring.rabbitmq.host}")
    private String address;

    @Value("${spring.rabbitmq.port}")
    private String port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Value("${spring.rabbitmq.publisher-confirms}")
    private boolean publisherConfirms;

    @Value("${rabbitmq.config.exchangeName}")
    private String exchangeName;

    @Value("${rabbitmq.config.queueName}")
    private String queueName;

    @Value("${rabbitmq.config.routingKey}")
    private String routingKey;

    @Bean
    public ConnectionFactory initConnectionFactory(){
    	logger.info("ConnectionFactory注入开始:");
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setAddresses(address+":"+port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        factory.setPublisherConfirms(publisherConfirms);
        return factory;
    }

    //必须是prototype类型
    
    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate initRabbitTemplate(@Qualifier("initConnectionFactory") ConnectionFactory connectionFactory){
    	logger.info("RabbitTemplate初始化开始:");
    	return new RabbitTemplate(connectionFactory);
    }

    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
         FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
         HeadersExchange ：通过添加属性key-value匹配
         DirectExchange:按照routingkey分发到指定队列
         TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(exchangeName);
    }
    @Bean
    public Queue queue() {
        return new Queue(queueName);
    }
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(defaultExchange()).with(routingKey);
    }
    
    @Bean 
    public RabbitAdmin admin(@Qualifier("initConnectionFactory") ConnectionFactory connectionFactory){
    	RabbitAdmin admin = new RabbitAdmin(connectionFactory);
    	admin.declareQueue(queue());
    	admin.declareExchange(defaultExchange());
    	admin.declareBinding(binding() );
		return admin;
    } 
    
   
}
