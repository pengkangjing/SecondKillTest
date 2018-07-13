package com.bupt.pkj.SecondKillTest.mapper;

import java.util.List;


import org.springframework.stereotype.Repository;

import com.bupt.pkj.SecondKillTest.entity.Product;
import com.bupt.pkj.SecondKillTest.entity.Record;
import com.bupt.pkj.SecondKillTest.entity.User;

@Repository
public interface SecondKillMapper {
    
	List<User> getAllUser();
	
	User getUserById(Integer id);
	
	
	List<Product> getAllProduct();
	
	Product getProductById(Integer id);
	
	boolean updatePessLockInMySQL(Product product);
	
	boolean updatePosiLockInMySQL(Product product);
	
	boolean insertRecord(Record record);
	
	boolean updateByAsynPattern(Product product);

	
	

}
