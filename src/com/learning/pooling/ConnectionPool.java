package com.learning.pooling;

public interface ConnectionPool {

	Connection getConnection();
	
	void releaseConnection(Connection connection);
	
	void shutDownPool();
	
	Integer numberOfOpenConnection();
	
	Integer numberOfActiveConnection();
	
}
