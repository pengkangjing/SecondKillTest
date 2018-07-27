package com.bupt.pkj.SecondKillTest;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

//import org.dom4j.Document;
//import org.dom4j.Element;
//import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.alibaba.fastjson.JSON;
import com.bupt.pkj.SecondKillTest.common.SecondKillEnum;
import com.bupt.pkj.SecondKillTest.entity.Product;
import com.bupt.pkj.SecondKillTest.entity.Record;
import com.bupt.pkj.SecondKillTest.entity.User;
import com.bupt.pkj.SecondKillTest.mapper.SecondKillMapper;
import com.bupt.pkj.SecondKillTest.mq.RabbitMQReceiver;
import com.bupt.pkj.SecondKillTest.mq.RabbitMQSender;
import com.bupt.pkj.SecondKillTest.service.SecondKillService;



public class SK_Test {
 
	public static void main(String[] ars){
		 
		final Logger logger = LoggerFactory.getLogger(SK_Test.class);
    	//Logger log = Logger.getLogger(App.class);
    	logger.info("hey hey hey");
       
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        
        SecondKillMapper sm = (SecondKillMapper) ctx.getBean("secondKillMapper");
        
        User user = sm.getUserById(1);
        System.out.println( user);
        System.out.println( ctx.containsBean("product"));
        RabbitMQSender rm=(RabbitMQSender) ctx.getBean("rabbitMQSender");
        rm.send(JSON.toJSONString(new Record(null,1,"PKJ",3,"XIAOMI",SecondKillEnum.SUCCESS.getCode(),SecondKillEnum.SUCCESS.getMessage(),new Date())));
        
        ConnectionFactory cf = (ConnectionFactory) ctx.getBean("initConnectionFactory");
        System.out.println(cf.getVirtualHost());
        
        Queue q = (Queue) ctx.getBean("queue");
        System.out.println(q.getName());
        System.out.println(rm.rabbitTemplate.getRoutingKey());
        //System.out.println();
       
       System.out.println( Arrays.toString(ctx.getBeanDefinitionNames()));
		/*SecondKillService ss = (SecondKillService) ctx.getBean("secondKillService");
		List<Product> product = ss.getAllProduct();
		for(int i=0;i<product.size();i++){
			logger.info(product.get(i).getProductName().toString());
			logger.info(String.valueOf(product.get(i).getStock()));
		}*/
		//sm.updatePessLockInMySQL(product.get(0));
		//sm.updatePosiLockInMySQL(product.get(0));
		//sm.updateByAsynPattern(product.get(0).getId());
		//logger.info(product.get(0).getPrice().toString());
		
		
	}
	
}
