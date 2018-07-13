package com.bupt.pkj.SecondKillTest.utils;

import com.bupt.pkj.SecondKillTest.constant.RedisCacheConstant;

public class SecondKillUtils {

	public static String getRedisBoughtSetKey(String productName) {
		
		String hasBySet = "";
		if(productName!=null&&!productName.isEmpty()){
			switch(productName){
			case "iphone":
				hasBySet = RedisCacheConstant.IPHONE_HAS_BOUGHT_SET;
				break;
			case "huawei":
				hasBySet = RedisCacheConstant.HUAWEI_HAS_BOUGHT_SET;
				break;
			case "xiaomi":
				hasBySet = RedisCacheConstant.XIAOMI_HAS_BOUGHT_SET;
				break;
			case "samsung":
				hasBySet = RedisCacheConstant.SAMSUNG_HAS_BOUGHT_SET;
				break;
			}
		}
		return hasBySet;
		
	}

}
