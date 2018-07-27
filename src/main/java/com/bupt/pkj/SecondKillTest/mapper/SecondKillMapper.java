package com.bupt.pkj.SecondKillTest.mapper;

import java.util.List;


import org.springframework.stereotype.Repository;

import com.bupt.pkj.SecondKillTest.entity.Product;
import com.bupt.pkj.SecondKillTest.entity.Record;
import com.bupt.pkj.SecondKillTest.entity.User;

@Repository
public interface SecondKillMapper {
    
	List<User> getAllUser();
	
	List<Product> getAllProduct();
	
	List<Record> getAllRecord();
	
	User getUserById(Integer id);
	
	Product getProductById(Integer id);
	
	boolean updatePessLockInMySQL(Product product);
	
	boolean updatePosiLockInMySQL(Product product);
	
	boolean insertRecord(Record record);
	
	boolean updateByAsynPattern(Integer id);

	

	
	

}
