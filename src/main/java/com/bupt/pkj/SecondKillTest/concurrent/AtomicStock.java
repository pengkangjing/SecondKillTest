package com.bupt.pkj.SecondKillTest.concurrent;

import com.bupt.pkj.SecondKillTest.entity.Product;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bupt.pkj.SecondKillTest.mapper.SecondKillMapper;

@Component
public class AtomicStock {                                                 //原子库存工厂
	private AtomicInteger samsungInteger = new AtomicInteger();
	private AtomicInteger huaweiInteger = new AtomicInteger();
	private AtomicInteger xiaomiInteger = new AtomicInteger();
	private AtomicInteger iphoneInteger = new AtomicInteger();
	
	@Autowired
	private SecondKillMapper secondKillMapper; 
	
	@PostConstruct
	public void initAtomicInteger(){                                         //初始化所有商品的原子类 库存
		List<Product> productList = secondKillMapper.getAllProduct();
		for(Product product : productList){
			getAtomicInteger(product.getProductName()).set(product.getStock());     
		}
		
	}
	public AtomicInteger getAtomicInteger(String productName){              //得到 对应商品名称 的商品库存原子类
		AtomicInteger ai = null;
		if(productName!=null&&!productName.isEmpty()){
			switch (productName){
				case "iphone" :
					ai = iphoneInteger;
					break;
				case "huawei":
					ai = huaweiInteger;
					break;
				case "xiaomi":
					ai = xiaomiInteger;
					break;
				case "samsung":
					ai = samsungInteger;
					break;
	
			}
		}
		return ai;
		
	}
}
