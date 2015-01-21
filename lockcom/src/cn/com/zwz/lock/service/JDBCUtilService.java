package cn.com.zwz.lock.service;

import java.sql.Connection;

public interface JDBCUtilService {
	Connection getJDBCConnection();
	
	Connection getJDBCConnection(String dataSource);
}
