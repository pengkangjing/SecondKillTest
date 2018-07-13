package com.bupt.pkj.SecondKillTest.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class RedisCacheHandle {
	@Autowired
	JedisPool jedisPool = new JedisPool();
	
	public Jedis getJedis(){
		return jedisPool.getResource();
	}
}
