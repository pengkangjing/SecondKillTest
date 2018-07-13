package com.bupt.pkj.SecondKillTest.config;

import java.io.IOException;




import java.sql.SQLException;


import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.alibaba.druid.pool.DruidDataSource;



@Configuration
public class MybatisConfig {
	
		private static final Logger logger =  LoggerFactory.getLogger(MybatisConfig.class);
		
		@Value("${spring.datasource.url}")
		private String dbUrl;
	
		@Value("${spring.datasource.username}")
		private String username;
	
		@Value("${spring.datasource.password}")
		private String password;
	
		@Value("${spring.datasource.driver-class-name}")
		private String driverClassName;
	
		@Value("${spring.datasource.initialSize}")
		private int initialSize;
	
		@Value("${spring.datasource.minIdle}")
		private int minIdle;
	
		@Value("${spring.datasource.maxActive}")
		private int maxActive;
	
		@Value("${spring.datasource.maxWait}")
		private int maxWait;
	
	 	@Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
	    private int timeBetweenEvictionRunsMillis;

	    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
	    private int minEvictableIdleTimeMillis;

	    @Value("${spring.datasource.validationQuery}")
	    private String validationQuery;

	    @Value("${spring.datasource.testWhileIdle}")
	    private boolean testWhileIdle;

	    @Value("${spring.datasource.testOnBorrow}")
	    private boolean testOnBorrow;

	    @Value("${spring.datasource.testOnReturn}")
	    private boolean testOnReturn;

	    @Value("${spring.datasource.poolPreparedStatements}")
	    private boolean poolPreparedStatements;

	    @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")
	    private int maxPoolPreparedStatementPerConnectionSize;

	    @Value("${spring.datasource.filters}")
	    private String filters;

	    @Value("${spring.datasource.connectionProperties}")
	    private String connectionProperties;
	    
	    /*
	     * MyBatis
	     */
	    
	    @Value("${mybatis.type-aliases-package}")
	    private String typeAliasesPackage;
	    
	    @Value("${mybatis.mapper-locations}")
	    private String mapperLocations;
	    
	    @Bean(name = "druidDataSource")
	    public DruidDataSource datasource(){
	    	logger.info("druidDataSource注入开始:");
	    	DruidDataSource dataSource = new DruidDataSource();
	    	
	    	dataSource.setUrl(dbUrl);
	        dataSource.setUsername(username);
	        dataSource.setPassword(password);
	        dataSource.setDriverClassName(driverClassName);

	        //configuration
	        dataSource.setInitialSize(initialSize);
	        dataSource.setMinIdle(minIdle);
	        dataSource.setMaxActive(maxActive);
	        dataSource.setMaxWait(maxWait);
	        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
	        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
	        dataSource.setValidationQuery(validationQuery);
	        dataSource.setTestWhileIdle(testWhileIdle);
	        dataSource.setTestOnBorrow(testOnBorrow);
	        dataSource.setTestOnReturn(testOnReturn);
	        dataSource.setPoolPreparedStatements(poolPreparedStatements);
	        dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
	    	
	        try{
	        	dataSource.setFilters(filters);
	        	
	        }catch(SQLException e){
	        	logger.error("druid configuration initialization filter",e);
	        }
	        
	        dataSource.setConnectionProperties(connectionProperties);
	        return dataSource;
	    }
	    @Bean
	    public SqlSessionFactoryBean initSqlSessionFactoryBean(@Qualifier("druidDataSource") DruidDataSource dataSource) throws IOException{
	    	logger.info("SqlSessionFactoryBean注入开始:");
	    	SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
	    	sessionFactoryBean.setDataSource(dataSource);
	    	sessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);
	    	ResourcePatternResolver  resolver = new PathMatchingResourcePatternResolver();
	    	
	    	sessionFactoryBean.setMapperLocations(resolver.getResources(mapperLocations));
	    	
	        return sessionFactoryBean;
	    }
	   
	    
	    
	
}
