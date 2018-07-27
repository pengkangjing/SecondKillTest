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
	public void initJedis(){                                   //初始化redis数据
		Jedis jedis = new RedisCacheHandle().getJedis();
		jedis.flushDB();                                       //清空redis
		
		List<Product> productList = secondKillMapper.getAllProduct();
		for(Product product : productList){
			                                                                                   //用到是redis中的字符串数据结构
			jedis.set(product.getProductName()+"_stock", String.valueOf(product.getStock()));  //以每个商品的名称加_stock 作为键名的 字符串
		}                                                                                     //  将数据库中刻每个商品的库存转换成字符串写进去
		
		logger.info("redis缓存初始化完毕");
	}
	
	
	 
	

}
