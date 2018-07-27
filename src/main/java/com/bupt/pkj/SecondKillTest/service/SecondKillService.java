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
		Jedis jedis = redisCacheHandle.getJedis();                     //得到jedis实例
		Record record;
		
		Integer userId = (Integer) paramMap.get("userId");              //从请求中取出用户id
		Integer productId = (Integer) paramMap.get("productId");        //从请求中取出产品id
		User user = secondKillMapper.getUserById(userId);               //找出用户
		Product product = secondKillMapper.getProductById(productId);   //找出商品
		
		String hasBoughtSetKey = SecondKillUtils.getRedisBoughtSetKey(product.getProductName());  //根据商品的名字 找到redis中保存该商品已秒杀用户的表名
		
		// 如果一个用户没有秒杀成功   同一个用户的多个请求会通过这里 
		boolean isBuy = jedis.sismember(hasBoughtSetKey, user.getId().toString());   //从上述表中 查询用户是否已经秒杀成功过    
		//同一个用户秒杀成功之后的所有请求在这里 结束
		if(isBuy){
			logger.error("用户:"+user.getUserName()+"重复购买商品:"+product.getProductName());  //如果之前秒杀成功过
			throw new SecondKillException(SecondKillEnum.REPEAT);                         //记录日志，抛出重复秒杀异常
		}
		
		//悲观锁 锁住了那一行   所有用户的所有请求 对同一个商品 秒杀时  都会在这里阻塞
		boolean secondKillSuccess = secondKillMapper.updatePessLockInMySQL(product);      //如果之前没有秒杀过，悲观锁更新
		//所有请求通过上面这个独木桥之后   
		if(!secondKillSuccess){                                                           //更新失败
			
			logger.error("商品:"+product.getProductName()+"库存不足！");                       //记录库存不足日志
			throw new SecondKillException(SecondKillEnum.LOW_STOCKS);                     //抛出异常
			
		}
		//同一个用户的多个请求可能减库存成功   到达这里
		//下面这行是原子操作 只有一个请求能成功通过
		long result = jedis.sadd(hasBoughtSetKey, user.getId().toString());               //如果更新成功 ，记录到该商品的已秒杀集合中
		
		if(result>0){                                                                     //如果 成插入
			//以当前时间 生成  秒杀订单 记录
			record = new Record(null,user.getId(),user.getUserName().toString(),product.getId(),product.getProductName().toString(),SecondKillEnum.SUCCESS.getCode(),SecondKillEnum.SUCCESS.getMessage(),new Date());
			//
			logger.info(record.toString());         //在日志中记录  秒杀记录
			boolean insertFlag = secondKillMapper.insertRecord(record);  //将订单更新到数据库
			if(insertFlag){                                              //如果更新成功
				
				logger.info("用户"+user.getUserName()+"秒杀商品:"+product.getProductName()+"成功"); //在日志中记录整个秒杀成功
				return SecondKillEnum.SUCCESS;                 //返回秒杀成功状态
			}
			else {                                            //如果订单插入不成功
				
				logger.error("系统错误");                       //日志记录系统错误
				throw new SecondKillException(SecondKillEnum.SYSTEM_EXCEPTION);  //抛出系统异常 异常   回滚
			}
		}
		else {                   //如果 redis 加入集合不成功 说明重复秒杀了  

			logger.error("用户:"+user.getUserName()+"重复秒杀商品:"+product.getProductName());  //记录日志 重复秒杀
			throw new SecondKillException(SecondKillEnum.REPEAT);                         //抛出重复秒杀异常   回滚
		}
	}
		 
		
		/**
	     * MySQL加字段version实现乐观锁
	     * @param paramMap
	     * @return
	     */
		@Transactional
		public SecondKillEnum handleByPosiLockInMySQL(Map<String,Object> paramMap){
			                                          
			Jedis jedis = redisCacheHandle.getJedis();                      //得到jedis实例     每个请求一个redis连接
			Record record ;
			
			Integer userId = (Integer) paramMap.get("userId");              //从请求中得到用户id
			Integer productId = (Integer) paramMap.get("productId");        //从请求中得到产品id
			User user = secondKillMapper.getUserById(userId);               //根据用户id 查找用户
			Product product = secondKillMapper.getProductById(productId);   //根据商品id查找商品
			
			String hasBoughtSetKey = SecondKillUtils.getRedisBoughtSetKey(product.getProductName());  //根据商品名查找redis中记录该商品的已秒杀用户集合
			
	    	boolean isBuy = jedis.sismember(hasBoughtSetKey, user.getId().toString());   //查找当前用户是否在该商品的已秒杀集合中
			//同一个用户的所有请求都会到达这里   如果已经秒杀成功了  该用户的秒杀请求到这就结束了
	        if (isBuy){                                                                 //如果是 
	        	logger.error("用户:"+user.getUserName()+"重复购买商品"+product.getProductName()); //记录日志重复秒杀
	            throw new SecondKillException(SecondKillEnum.REPEAT);                       //抛出重复秒杀异常
	        }
	      //库存手动减一        //上面的 jedis.sismember 非同步    同一个用户的未秒杀成功之前    所有的请求都会到达这里
	        int lastStock = product.getStock()-1;         //手动将库存减一
	        
	        //
	        if (lastStock >= 0){                          //如果库存足够 
	            product.setStock(lastStock);              // 则更新该商品 实例中的库存
	            //这里会有大量的失败   在写比较多的情况下 乐观锁 比悲观锁性能差
	            boolean secondKillSuccess = secondKillMapper.updatePosiLockInMySQL(product); //乐观锁更新 数据库
	            
	            if (!secondKillSuccess){                                                     //更新不成功
	            	logger.error("用户:"+user.getUserName()+"秒杀商品"+product.getProductName()+"失败!");//日志记录 秒杀失败
	                throw new SecondKillException(SecondKillEnum.FAIL);                      //抛出秒杀失败异常
	            }
	            
	        } else {                                    //如果库存不足
	        	logger.error("商品:"+product.getProductName()+"库存不足!");   //日志记录 库存不足
	            throw new SecondKillException(SecondKillEnum.LOW_STOCKS); // 抛出库存不足异常      回滚      不过数据库没有操作 不回滚
	        }
	        //同一个用户的多个请求都减了库存
	        long addResult = jedis.sadd(hasBoughtSetKey, user.getId().toString());  //如果更新成功 将 该用户id添加到redis中对应该商品的已秒杀用户记录中
	        //上面的操作  同一个用户的多个请求中只有一个能到达这里  
	        if(addResult>0){               //redis添加成功
	        	//生成 订单记录
	        	record = new Record(null,user.getId(),user.getUserName().toString(),product.getId(),product.getProductName().toString(),SecondKillEnum.SUCCESS.getCode(),SecondKillEnum.SUCCESS.getMessage(),new Date());
	        	logger.info(record.toString());  //将订单记录到日志
	        	boolean insertFlag = secondKillMapper.insertRecord(record); //将订单记录到数据库中
	        	if(insertFlag){     //如果记录成功
	        		logger.info("用户:"+user.getUserName()+"秒杀商品:"+product.getProductName()+"成功");//日志记录 成功秒杀 该订单成功生成
	        		return SecondKillEnum.SUCCESS;   //返回成功秒杀状态
	        	}else{
	        		throw new SecondKillException(SecondKillEnum.SYSTEM_EXCEPTION);     //订单记录不成功  抛出系统异常  异常      回滚减库存操作
	        	}
	        }else{                                     //redis添加不成功
	        	 logger.error("用户:"+user.getUserName()+"重复秒杀商品:"+product.getProductName());   //日志记录 重复秒杀
	             throw new SecondKillException(SecondKillEnum.REPEAT);  //抛出重复秒杀异常                           同一个用户的 多个请求 减了  库存   回滚数据库减库存操作
	        }	
		}
		
		 /**
	     * redis的watch监控
	     * @param paramMap
	     * @return
	     */
		public SecondKillEnum handleByRedisWatch(Map<String,Object> paramMap){
			Jedis jedis = redisCacheHandle.getJedis();         //得到redis实例
			Record record;
			
			Integer userId = (Integer) paramMap.get("userId");          //从请求中得到用户id
	        Integer productId = (Integer)paramMap.get("productId");     //从 请求中得到商品id
	        User user = secondKillMapper.getUserById(userId);           //根据用户id 查询用户
	        Product product = secondKillMapper.getProductById(productId); //根据商品ID  查询商品
            
	        String productStockCacheKey = product.getProductName()+"_stock";   //根据商品名称+得到 商品库存 的键名
	        String hasBoughtSetKey = SecondKillUtils.getRedisBoughtSetKey(product.getProductName()); //根据商品名称得到 该商品的已秒杀用户 集合的键名
	        
	      //watch开启监控
	        jedis.watch(productStockCacheKey);      //watch开始监控  该商品库存的变化
	        
	      //判断是否重复购买，注意这里高并发情形下并不安全
	        
	        boolean isBuy = jedis.sismember(hasBoughtSetKey, user.getId().toString()); //g根据已秒杀集合判断是否重复秒杀
	        if(isBuy){        //重复秒杀
	        	logger.error("用户:"+user.getUserName()+"重复购买商品"+product.getProductName()); //记录日志 重复秒杀
	            throw new SecondKillException(SecondKillEnum.REPEAT);    //抛出重复秒杀异常
	        }
	        
	        String stock = jedis.get(productStockCacheKey);            //得到该商品的库存
	        if(Integer.parseInt(stock)<=0){                   //判断库存是否充足
	            logger.error("商品:"+product.getProductName()+"库存不足!");   //记录日志 库存不足
	            throw new SecondKillException(SecondKillEnum.LOW_STOCKS);  //抛出库存不足异常
	        }
	      //开启Redis事务
	        Transaction tx = jedis.multi();        //开启redis 事务
	      //库存减一
	        tx.decrBy(productStockCacheKey, 1);   //将该redis中该商品库存减一
	      //执行事务
	        List<Object> resultList = tx.exec();   //提交事务
	       
	        if(resultList==null||resultList.isEmpty()){   //事务失败
	        	jedis.unwatch();                          //关闭监控
	        	//watch监控被更改过----物品抢购失败;
	            logger.error("商品:"+product.getProductName()+",watch监控被更改,物品抢购失败");  //记录已修改异常
	            throw new SecondKillException(SecondKillEnum.FAIL);     //抛出秒杀失败异常
	        }
	      //添加到已买队列
	        long addResult = jedis.sadd(hasBoughtSetKey,user.getId().toString());  //事务成功  将用户id添加到该商品已购买用户
	        if(addResult>0){              //添加成功
	        	//秒杀成功
	        	//生成 订单 
	        	 record =  new Record(null,user.getId(),user.getUserName().toString(),product.getId(),product.getProductName().toString(),SecondKillEnum.SUCCESS.getCode(),SecondKillEnum.SUCCESS.getMessage(),new Date());
	        	//添加record到rabbitMQ消息队列
	        	 rabbitMQSender.send(JSON.toJSONString(record));  //将订单转换成json字符串
	        	
	        	//返回秒杀成功
			return SecondKillEnum.SUCCESS;   //返回秒杀成功
		 } else {                            //  将用户id添加到该商品已购买用户失败
	            //重复秒杀
	            //这里抛出RuntimeException异常，redis的decr操作并不会回滚，所以需要手动incr回去
	            jedis.incrBy(productStockCacheKey,1);
	            throw new SecondKillException(SecondKillEnum.REPEAT);  //抛出重复秒杀异常
	        }
		
	}
		/**
	     * AtomicInteger的CAS机制
	     * @param paramMap
	     * @return
	     */	
		@Transactional
		public SecondKillEnum handleByAtomicInteger(Map<String,Object> paramMap){
			
			Jedis jedis = redisCacheHandle.getJedis();             //得到redis实例
	        Record record;
	        
	        Integer userId = (Integer) paramMap.get("userId");     //根据请求 得到用户id
	        Integer productId = (Integer)paramMap.get("productId"); //根据请求得到商品id
	        User user = secondKillMapper.getUserById(userId);       //根据用户id 得到用户
	        Product product = secondKillMapper.getProductById(productId);  //根据商品id 得到商品
	        
	        String hasBoughtSetKey = SecondKillUtils.getRedisBoughtSetKey(product.getProductName()); //根据商品名称得到redis中该商品的已秒杀用户集合键名
	      //判断是否重复购买
	        boolean isBuy = jedis.sismember(hasBoughtSetKey,user.getId().toString());   //判断是否重复秒杀    //如果未秒杀成功之前同一个用户的多个请求会通过这里
	        
	        if(isBuy){                                                                       //如果用户的其中一个请求已经秒杀成功了 该用户的其他请求到这就结束了
	        	logger.error("用户:"+user.getUserName()+"重复购买商品"+product.getProductName());  //记录日志 重复秒杀
	            throw new SecondKillException(SecondKillEnum.REPEAT);                        //抛出重复秒杀异常
	        	
	        }
	        
	        AtomicInteger atomicInteger = atomicStock.getAtomicInteger(product.getProductName());  //得到保存该商品库存的的原子数
	        //----------0------------
	        int stock = atomicInteger.decrementAndGet();    //cas 原子操作  库存减一 返回 原来的库存数                  //该用户未秒杀成功之前   同一个用户的多个请求这里在硬件层面阻塞 挨个减1
	        // ----1-----
	        if(stock<0){                             //如果  减一之后小于0 说明 库存不足                                                            //同一个用户的请求在这里 部分通过 部分失败 
	        	                                     //   其他用户的请求可能会因为这个用户的多个请求减了库存失败  但其实库存还是有的
	        	                                     //0-3之间的库存是不准确的       每个请求库存 不足  并不一定是真的库存不足
	        	logger.error("商品:"+product.getProductName()+"库存不足, 抢购失败!");  //日志记录库存不足
	            throw new SecondKillException(SecondKillEnum.LOW_STOCKS);      //抛出库存不足异常
	        }
	       //------2----- 
	        long result = jedis.sadd(hasBoughtSetKey,user.getId().toString());   //如果上面成功了   将用户id加入到该商品已购买用户集合中     这一步只有一个请求能成功
	       
	        //为什么这里可能添加不成功  因为 可能有同一个用户的其他请求 先 到了 已经被处理了     在上面1和2 之间 同一个用户的多个请求进来了  其中有另一个请求插队 先 把用户添加进去了
	        if(result>0){  //添加成功
	        	//---------4--------
	        	//生成订单
	        	record = new Record(null,user.getId(),user.getUserName().toString(),product.getId(),product.getProductName().toString(),SecondKillEnum.SUCCESS.getCode(),SecondKillEnum.SUCCESS.getMessage(),new Date());
	        	logger.info(record.toString());
	        	boolean insertFlag =  secondKillMapper.insertRecord(record);  //将订单更新到数据库
	        	if(insertFlag){   //更新成功
	        		//更改物品库存
	        		 secondKillMapper.updateByAsynPattern(record.getProductId());   //异步更新数据库   update本身会加写锁  这个更新没问题
	                 logger.info("用户:"+user.getUserName()+"秒杀商品"+product.getProductName()+"成功!");  //日志记录 秒杀成功
	                 return SecondKillEnum.SUCCESS;     //返回秒杀成功
	        		
	        	}else{
	        		logger.error("系统错误!");         //订单更新不成功
	                throw new SecondKillException(SecondKillEnum.SYSTEM_EXCEPTION);   //抛出系统异常错误    数据库中没有订单  但redis中保存了 该用户已经秒杀
	        	}
	        	//--------5---------     4-5之间的代码同一个用户的一个请求能执行到
	        }else {   //将用户id加入到该商品已购买用户集合中 不成功
	            logger.error("用户:"+user.getUserName()+"重复秒杀商品"+product.getProductName()); //日志记录重复秒杀
	            atomicInteger.incrementAndGet();   //恢复库存  库存手动加一       这个 内部通过轮询一定能成功
	            throw new SecondKillException(SecondKillEnum.REPEAT);  //抛出重复秒杀异常
	        }
	        //----------3----------
		}
	
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
		public List<Record> getAllRecord(){
			                                          
			List<Record> record = secondKillMapper.getAllRecord();
	      
			return record;
			
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
		
		public String flushDB(){
			
			
			Jedis jedis = new RedisCacheHandle().getJedis();
			jedis.flushDB();                                       //清空redis
			
			List<Product> productList = secondKillMapper.getAllProduct();
			for(Product product : productList){
				                                                                                   //用到是redis中的字符串数据结构
				jedis.set(product.getProductName()+"_stock", String.valueOf(product.getStock()));  //以每个商品的名称加_stock 作为键名的 字符串
			}     
			
			return "redis cache have been reset! ";
		}
	
	
}
