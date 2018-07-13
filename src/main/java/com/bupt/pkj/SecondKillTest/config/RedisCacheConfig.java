package com.bupt.pkj.SecondKillTest.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisCacheConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisCacheConfig.class);
	@Value("${spring.redis.host}")
	private String host;
	@Value("${spring.redis.port}")
	private int port;
	@Value("${spring.redis.pool.max-idle}")
	private int maxIdle;
	@Value("${spring.redis.pool.min-idle}")
	private int minIdle;
	@Value("${spring.redis.pool.max-wait}")
	private int maxWaitMillis;
	@Value("${spring.redis.pool.max-active}")
	private int maxActive;
	
	@Value("${spring.redis.timeout}")
	private int timeout;
	@Value("${spring.redis.database}")
	private int database;
	
	@Bean(name="poolConfig")
	public JedisPoolConfig initJedisPoolConfig(){
		logger.info("JedisPoolConfig注入开始:");
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(maxActive);
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMaxWaitMillis(maxWaitMillis);
		poolConfig.setMinIdle(minIdle);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setBlockWhenExhausted(true);
		return poolConfig;
		
	}
	@Bean(name="jedisPool")
	public JedisPool initJedisPool(@Qualifier("poolConfig") JedisPoolConfig poolConfig ){
		logger.info("JedisPool注入开始:");
		JedisPool jedisPool = new JedisPool(poolConfig,host,port,timeout);
		return jedisPool;
	}
	
	
}
