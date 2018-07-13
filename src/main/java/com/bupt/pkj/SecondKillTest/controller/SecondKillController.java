package com.bupt.pkj.SecondKillTest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bupt.pkj.SecondKillTest.common.Message;
import com.bupt.pkj.SecondKillTest.common.SecondKillEnum;
import com.bupt.pkj.SecondKillTest.entity.Person;
import com.bupt.pkj.SecondKillTest.entity.Product;
import com.bupt.pkj.SecondKillTest.entity.User;
import com.bupt.pkj.SecondKillTest.service.SecondKillService;
import com.bupt.pkj.SecondKillTest.web.req.SecondKillRequest;
import com.bupt.pkj.SecondKillTest.web.vo.SecondKillResponse;

@RequestMapping("/SecondKill")
@RestController
public class SecondKillController {
	
	@Autowired
	private SecondKillService secondKillService;
	
	 /**
     * MySQL悲观锁
     * @param requestMessage
     * @return
     */
	@RequestMapping(value="/pessLockInMySQL",method = RequestMethod.POST)
	public Message<SecondKillResponse> pessLockInMySQL(@RequestBody Message<SecondKillRequest> requestMessage){
		Map<String,Object> paramMap= new HashMap<String,Object>();
		paramMap.put("userId", requestMessage.getBody().getUserId());
		paramMap.put("productId", requestMessage.getBody().getProductId());
		SecondKillEnum secondKillEnum = secondKillService.handleByPessLockInMySQL(paramMap);
		Message<SecondKillResponse> responseMessage = new Message<SecondKillResponse>(secondKillEnum,null);
		return responseMessage;
	}
	
	
	/**
     * MySQL乐观锁
     * @param requestMessage
     * @return
     */
	@RequestMapping(value="/posiLockInMySQL",method = RequestMethod.POST)
	@ResponseBody
	public Message<SecondKillResponse> posiLockInMySQL(@RequestBody Message<SecondKillRequest> requestMessage){
		Map<String,Object> paramMap= new HashMap<String,Object>();
		paramMap.put("userId", requestMessage.getBody().getUserId());
		paramMap.put("productId", requestMessage.getBody().getProductId());
		SecondKillEnum secondKillEnum = secondKillService.handleByPosiLockInMySQL(paramMap);
		Message<SecondKillResponse> responseMessage = new Message<SecondKillResponse>(secondKillEnum,null);
		return responseMessage;
	}
	
	/**
     * 利用redis的watch监控的特性
     * @throws InterruptedException
     */
	@RequestMapping(value ="/baseOnRedisWatch",method = RequestMethod.POST)
	@ResponseBody
	public Message<SecondKillResponse> baseOnRedisWatch(@RequestBody Message<SecondKillRequest> requestMessage){
		Map<String,Object> paramMap= new HashMap<String,Object>();
		paramMap.put("userId", requestMessage.getBody().getUserId());
		paramMap.put("productId", requestMessage.getBody().getProductId());
		SecondKillEnum secondKillEnum = secondKillService.handleByRedisWatch(paramMap);
		Message<SecondKillResponse> responseMessage = new Message<SecondKillResponse>(secondKillEnum,null);
		return responseMessage;
	} 
	
	/**
     * 利用AtomicInteger的CAS机制特性
     * @param requestMessage
     * @return
     */
	
	@RequestMapping(value = "/baseOnAtomicInteger",method = RequestMethod.POST)
	@ResponseBody
	public Message<SecondKillResponse> baseOnAtomicInteger(@RequestBody Message<SecondKillRequest> requestMessage){
		Map<String,Object> paramMap= new HashMap<String,Object>();
		paramMap.put("userId", requestMessage.getBody().getUserId());
		paramMap.put("productId", requestMessage.getBody().getProductId());
		SecondKillEnum secondKillEnum = secondKillService.handleByAtomicInteger(paramMap);
		Message<SecondKillResponse> responseMessage = new Message<SecondKillResponse>(secondKillEnum,null);
		return responseMessage;
	} 
	
	
	@RequestMapping(value = "/hello",method = RequestMethod.POST)
	@ResponseBody
	public Message<SecondKillResponse> hello(@RequestBody Message<SecondKillRequest> requestMessage){
		Map<String,Object> paramMap= new HashMap<String,Object>();
		paramMap.put("userId", requestMessage.getBody().getUserId());
		paramMap.put("productId", requestMessage.getBody().getProductId());
		SecondKillEnum secondKillEnum = SecondKillEnum.FAIL;
		Message<SecondKillResponse> responseMessage = new Message<SecondKillResponse>(secondKillEnum,null);
		return responseMessage;
	} 
	
	@RequestMapping(value = "/getUserById",method = RequestMethod.GET)
	@ResponseBody
	public User getUserById(@RequestParam(value="id") Integer id){
		
		User user = secondKillService.getUserById(id);
		
		return user;
	} 
	@RequestMapping(value = "/getProductById",method = RequestMethod.POST)
	@ResponseBody
	public Product getProductById(@RequestBody Integer id){
		
		Product product = secondKillService.getProductById(id);
		
		return product;
	} 
	
	@RequestMapping(value = "/getAllUser",method = RequestMethod.GET)
	@ResponseBody
	public List<Product> getAllUser(){
		
		//List<User> user = secondKillService.getAllUser();
		List<Product> product = secondKillService.getAllProduct();
		return product;
	} 
	
	@RequestMapping(value = "/getAllProduct",method = RequestMethod.GET)
	@ResponseBody
	public List<Product> getAllProduct(){
		
		List<Product> product = secondKillService.getAllProduct();
		
		return product;
	} 
	
	@RequestMapping(value = "/getWelcome",method = RequestMethod.GET)
	public String getWelcome(){
		
		return "welcome";
	} 
	
	@RequestMapping(value="/test",method = RequestMethod.POST)
	@ResponseBody
	public String test(@RequestBody String str_message){

		return str_message;
	}
	
	@RequestMapping(value="/Object",method = RequestMethod.POST)
	@ResponseBody
	public Person testObjectt(@RequestBody Person person){
        person.setName("彭康晶");
		return person;
	}
}
