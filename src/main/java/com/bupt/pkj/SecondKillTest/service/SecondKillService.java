package com.bupt.pkj.SecondKillTest.service;

import java.util.Date;


import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.bupt.pkj.SecondKillTest.cache.RedisCacheHandle;
import com.bupt.pkj.SecondKillTest.common.SecondKillEnum;
import com.bupt.pkj.SecondKillTest.concurrent.AtomicStock;
import com.bupt.pkj.SecondKillTest.entity.Product;
import com.bupt.pkj.SecondKillTest.entity.Record;
import com.bupt.pkj.SecondKillTest.entity.User;
import com.bupt.pkj.SecondKillTest.exception.SecondKillException;
import com.bupt.pkj.SecondKillTest.mapper.SecondKillMapper;
import com.bupt.pkj.SecondKillTest.mq.RabbitMQSender;
import com.bupt.pkj.SecondKillTest.utils.SecondKillUtils;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;


@Service
public class SecondKillService {
	
	private static final Logger logger = LoggerFactory.getLogger(SecondKillService.class);
	
	@Autowired
	private RedisCacheHandle redisCacheHandle;
	@Autowired
	private SecondKillMapper secondKillMapper;
	@Autowired
	private RabbitMQSender rabbitMQSender;
	@Autowired
	private AtomicStock atomicStock;
	
	/**
     * 利用MySQL的update行锁实现悲观锁
     * @param paramMap
     * @return
     */
	
	@Transactional
	public SecondKillEnum handleByPessLockInMySQL(Map<String,Object> paramMap){
		Jedis jedis = redisCacheHandle.getJedis();
		Record record;
		
		Integer userId = (Integer) paramMap.get("userId");
		Integer productId = (Integer) paramMap.get("productId");
		User user = secondKillMapper.getUserById(userId);
		Product product = secondKillMapper.getProductById(productId);
		
		String hasBoughtSetKey = SecondKillUtils.getRedisBoughtSetKey(product.getProductName());
		
		boolean isBuy = jedis.sismember(hasBoughtSetKey, userId.toString());
		if(isBuy){
			logger.error("用户:"+user.getUserName()+"重复购买商品:"+product.getProductName());
			throw new SecondKillException(SecondKillEnum.REPEAT);
		}
		
		boolean secondKillSuccess = secondKillMapper.updatePessLockInMySQL(product);
		if(!secondKillSuccess){
			
			logger.error("商品:"+product.getProductName()+"库存不足！");
			throw new SecondKillException(SecondKillEnum.LOW_STOCKS);
			
		}
		long result = jedis.sadd(hasBoughtSetKey, user.getId().toString());
		if(result>0){
			record = new Record(null,user,product,SecondKillEnum.SUCCESS.getCode(),SecondKillEnum.SUCCESS.getMessage(),new Date());
			logger.info(record.toString());
			boolean insertFlag = secondKillMapper.insertRecord(record);
			if(insertFlag){
				
				logger.info("用户"+user.getUserName()+"秒杀商品:"+product.getProductName()+"成功");
				return SecondKillEnum.SUCCESS;
			}
			else {
				
				logger.error("系统错误");
				throw new SecondKillException(SecondKillEnum.SYSTEM_EXCEPTION);
			}
		}
		else {

			logger.error("用户:"+user.getUserName()+"重复秒杀商品:"+product.getProductName());
			throw new SecondKillException(SecondKillEnum.REPEAT);
		}
	}
		 /**
	     * MySQL加字段version实现乐观锁
	     * @param paramMap
	     * @return
	     */
		@Transactional
		public List<User> getAllUser(){
			                                          
			List<User> user = secondKillMapper.getAllUser();
	      
			return user;
			
		}
		
		@Transactional
		public List<Product> getAllProduct(){
			                                          
			List<Product> product = secondKillMapper.getAllProduct();
	      
			return product;
			
		}
		
		@Transactional
		public User getUserById(Integer id){
			                                          
			User user = secondKillMapper.getUserById(id);
	      
			return user;
			
		}
		
		@Transactional
		public Product getProductById(Integer id){
			                                          
			Product product = secondKillMapper.getProductById(id);
	      
			return product;
			
		}
		
		@Transactional
		public SecondKillEnum handleByPosiLockInMySQL(Map<String,Object> paramMap){
			                                          
			Jedis jedis = redisCacheHandle.getJedis();
			Record record = null;
			
			Integer userId = (Integer) paramMap.get("userId");
			Integer productId = (Integer) paramMap.get("productId");
			User user = secondKillMapper.getUserById(userId);
			Product product = secondKillMapper.getProductById(productId);
			
			String hasBoughtSetKey = SecondKillUtils.getRedisBoughtSetKey(product.getProductName());
			boolean isBuy = jedis.sismember(hasBoughtSetKey, user.getId().toString());
	        if (isBuy){
	        	logger.error("用户:"+user.getUserName()+"重复购买商品"+product.getProductName());
	            throw new SecondKillException(SecondKillEnum.REPEAT);
	        }
	      //库存手动减一
	        int lastStock = product.getStock()-1;
	        
	        if (lastStock >= 0){
	            product.setStock(lastStock);
	            boolean secondKillSuccess = secondKillMapper.updatePosiLockInMySQL(product);
	            if (!secondKillSuccess){                    
	            	logger.error("用户:"+user.getUserName()+"秒杀商品"+product.getProductName()+"失败!");
	                throw new SecondKillException(SecondKillEnum.FAIL);
	            }
	        } else {
	        	logger.error("商品:"+product.getProductName()+"库存不足!");
	            throw new SecondKillException(SecondKillEnum.LOW_STOCKS);
	        }
	        
	        long addResult = jedis.sadd(hasBoughtSetKey, user.getId().toString());
	        if(addResult>0){
	        	record = new Record(null,user,product,SecondKillEnum.SUCCESS.getCode(),SecondKillEnum.SUCCESS.getMessage(),new Date());
	        	logger.info(record.toString());
	        	boolean insertFlag = secondKillMapper.insertRecord(record);
	        	if(insertFlag){
	        		logger.info("用户:"+user.getUserName()+"秒杀商品:"+product.getProductName()+"成功");
	        		return SecondKillEnum.SUCCESS;
	        	}else{
	        		throw new SecondKillException(SecondKillEnum.SYSTEM_EXCEPTION);
	        	}
	        }else{
	        	 logger.error("用户:"+user.getUserName()+"重复秒杀商品:"+product.getProductName());
	             throw new SecondKillException(SecondKillEnum.REPEAT);
	        }
	        
			
		}
		
		 /**
	     * redis的watch监控
	     * @param paramMap
	     * @return
	     */
		public SecondKillEnum handleByRedisWatch(Map<String,Object> paramMap){
			Jedis jedis = redisCacheHandle.getJedis();
			Record record;
			
			Integer userId = (Integer) paramMap.get("userId");
	        Integer productId = (Integer)paramMap.get("productId");
	        User user = secondKillMapper.getUserById(userId);
	        Product product = secondKillMapper.getProductById(productId);
            
	        String productStockCacheKey = product.getProductName()+"_stock";
	        String hasBoughtSetKey = SecondKillUtils.getRedisBoughtSetKey(product.getProductName());
	        
	      //watch开启监控
	        jedis.watch(productStockCacheKey);
	        
	      //判断是否重复购买，注意这里高并发情形下并不安全
	        
	        boolean isBuy = jedis.sismember(hasBoughtSetKey, user.getUserName().toString());
	        if(isBuy){
	        	logger.error("用户:"+user.getUserName()+"重复购买商品"+product.getProductName());
	            throw new SecondKillException(SecondKillEnum.REPEAT);
	        }
	        
	        String stock = jedis.get(productStockCacheKey);
	        if(Integer.parseInt(stock)<=0){
	            logger.error("商品:"+product.getProductName()+"库存不足!");
	            throw new SecondKillException(SecondKillEnum.LOW_STOCKS);
	        }
	      //开启Redis事务
	        Transaction tx = jedis.multi();
	      //库存减一
	        tx.decrBy(productStockCacheKey, 1);
	      //执行事务
	        List<Object> resultList = tx.exec();
	       
	        if(resultList==null||resultList.isEmpty()){
	        	jedis.unwatch();
	        	//watch监控被更改过----物品抢购失败;
	            logger.error("商品:"+product.getProductName()+",watch监控被更改,物品抢购失败");
	            throw new SecondKillException(SecondKillEnum.FAIL);
	        }
	      //添加到已买队列
	        long addResult = jedis.sadd(hasBoughtSetKey,user.getId().toString());
	        if(addResult>0){
	        	//秒杀成功
	        	 record =  new Record(null,user,product,SecondKillEnum.SUCCESS.getCode(),SecondKillEnum.SUCCESS.getMessage(),new Date());
	        	//添加record到rabbitMQ消息队列
	        	 rabbitMQSender.send(JSON.toJSONString(record));
	        	
	        	//返回秒杀成功
			return SecondKillEnum.SUCCESS;
		 } else {
	            //重复秒杀
	            //这里抛出RuntimeException异常，redis的decr操作并不会回滚，所以需要手动incr回去
	            jedis.incrBy(productStockCacheKey,1);
	            throw new SecondKillException(SecondKillEnum.REPEAT);
	        }
		
	}
		/**
	     * AtomicInteger的CAS机制
	     * @param paramMap
	     * @return
	     */	
		@Transactional
		public SecondKillEnum handleByAtomicInteger(Map<String,Object> paramMap){
			
			Jedis jedis = redisCacheHandle.getJedis();
	        Record record;
	        
	        Integer userId = (Integer) paramMap.get("userId");
	        Integer productId = (Integer)paramMap.get("productId");
	        User user = secondKillMapper.getUserById(userId);
	        Product product = secondKillMapper.getProductById(productId);
	        
	        String hasBoughtSetKey = SecondKillUtils.getRedisBoughtSetKey(product.getProductName());
	      //判断是否重复购买
	        boolean isBuy = jedis.sismember(hasBoughtSetKey,user.getUserName().toString());
	        
	        if(isBuy){
	        	logger.error("用户:"+user.getUserName()+"重复购买商品"+product.getProductName());
	            throw new SecondKillException(SecondKillEnum.REPEAT);
	        	
	        }
	        
	        AtomicInteger atomicInteger = atomicStock.getAtomicInteger(product.getProductName());
	        
	        int stock = atomicInteger.decrementAndGet();
	        if(stock<0){
	        	logger.error("商品:"+product.getProductName()+"库存不足, 抢购失败!");
	            throw new SecondKillException(SecondKillEnum.LOW_STOCKS);
	        }
	        
	        long result = jedis.sadd(hasBoughtSetKey,user.getUserName());
	        if(result>0){
	        	
	        	record = new Record(null,user,product,SecondKillEnum.SUCCESS.getCode(),SecondKillEnum.SUCCESS.getMessage(),new  Date());
	        	logger.info(record.toString());
	        	boolean insertFlag =  secondKillMapper.insertRecord(record);
	        	if(insertFlag){
	        		//更改物品库存
	        		 secondKillMapper.updateByAsynPattern(record.getProduct());
	                 logger.info("用户:"+user.getUserName()+"秒杀商品"+product.getProductName()+"成功!");
	                 return SecondKillEnum.SUCCESS;
	        		
	        	}else{
	        		logger.error("系统错误!");
	                throw new SecondKillException(SecondKillEnum.SYSTEM_EXCEPTION);
	        	}
	        	
	        }else {
	            logger.error("用户:"+user.getUserName()+"重复秒杀商品"+product.getProductName());
	            atomicInteger.incrementAndGet();
	            throw new SecondKillException(SecondKillEnum.REPEAT);
	        }
		}
	
	
	
	
}
