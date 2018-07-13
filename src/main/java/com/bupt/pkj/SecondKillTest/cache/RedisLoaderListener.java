package com.bupt.pkj.SecondKillTest.cache;

import java.util.List;



import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bupt.pkj.SecondKillTest.concurrent.AtomicStock;
import com.bupt.pkj.SecondKillTest.entity.Product;
import com.bupt.pkj.SecondKillTest.mapper.SecondKillMapper;


import redis.clients.jedis.Jedis;


@Component
public class RedisLoaderListener {
	@Autowired
	RedisCacheHandle redisCacheHandle;
	@Autowired
	SecondKillMapper secondKillMapper;
	@Autowired
	AtomicStock atomicStock;
	
	private static final Logger logger = LoggerFactory.getLogger(RedisLoaderListener.class); 
	
	@PostConstruct
	public void initJedis(){
		Jedis jedis = new RedisCacheHandle().getJedis();
		jedis.flushDB();
		
		List<Product> productList = secondKillMapper.getAllProduct();
		for(Product product : productList){
			
			jedis.set(product.getProductName()+"_stock", String.valueOf(product.getStock()));
		}
		
		logger.info("redis缓存初始化完毕");
	}
	
	
	 
	

}
