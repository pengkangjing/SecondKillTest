package com.bupt.pkj.SecondKillTest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

//import org.dom4j.Document;
//import org.dom4j.Element;
//import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.bupt.pkj.SecondKillTest.entity.Product;
import com.bupt.pkj.SecondKillTest.entity.User;
import com.bupt.pkj.SecondKillTest.mapper.SecondKillMapper;
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
        
       logger.info( Arrays.toString(ctx.getBeanDefinitionNames()));
		SecondKillService ss = (SecondKillService) ctx.getBean("secondKillService");
		List<Product> product = ss.getAllProduct();
		for(int i=0;i<product.size();i++){
			logger.info(product.get(i).getProductName().toString());
			logger.info(String.valueOf(product.get(i).getStock()));
		}
		sm.updatePessLockInMySQL(product.get(0));
		//sm.updatePosiLockInMySQL(product.get(0));
		sm.updateByAsynPattern(product.get(0));
		//logger.info(product.get(0).getPrice().toString());
		
	}
	
}
