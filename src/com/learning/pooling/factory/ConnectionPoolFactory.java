package com.learning.pooling.factory;

import java.util.concurrent.ConcurrentHashMap;

import com.learning.pooling.Connection;
import com.learning.pooling.ConnectionPool;
import com.learning.pooling.impl.ConnectionHost;
import com.learning.pooling.impl.ConnectionPoolImpl.ConnectionPoolBuilder;

public class ConnectionPoolFactory {

	private static ConcurrentHashMap<ConnectionHost, ConnectionPool> connectionPoolMap = 
			new ConcurrentHashMap<ConnectionHost, ConnectionPool>();

	public static void buildConnectionPoolFactory(ConnectionHost host,
			Integer initialPoolSize, Integer maxPoolSize, 
			Integer incrementalPoolSize) {
		synchronized (ConnectionPoolFactory.class) {
			ConnectionPoolBuilder poolBuilder = new ConnectionPoolBuilder();
			poolBuilder.setHost(host);
			poolBuilder.setInitialPoolSize(initialPoolSize);
			poolBuilder.setMaxPoolSize(maxPoolSize);
			poolBuilder.setIncrementalPoolSize(incrementalPoolSize);
			ConnectionPool connectionPool = poolBuilder.build();
			connectionPoolMap.put(host, connectionPool);
		}
	}
	
	private static ConnectionPool getConnectionPool(ConnectionHost host) {
		if(host != null)
			return connectionPoolMap.get(host);
		else
			throw new NullPointerException("no connection for null host");
	}
	
	public static Connection getConnection(ConnectionHost host) {
		ConnectionPool pool = getConnectionPool(host);
		if(pool != null) {
			//synchronized (ConnectionPoolFactory.class) {
				Connection connection = pool.getConnection();
				if(connection == null) {
					//throw new RuntimeException("COnnection pool exhausted");
					while(true) {
						try {
							Thread.sleep(1000l);
							connection = pool.getConnection();
							if(connection != null) {
								break;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				connection.openConnection();
				return connection;
			}
		//}
		return null;
	}
	
	public static void release(ConnectionHost host, Connection connection) {
		ConnectionPool pool = getConnectionPool(host);
		if(pool != null) {
			//synchronized (ConnectionPoolFactory.class) {
				pool.releaseConnection(connection);
			//}
		}
	}
	
	public static Integer numberOfOpenConnection(ConnectionHost host) {
		ConnectionPool pool = getConnectionPool(host);
		if(pool != null) {
			synchronized (ConnectionPoolFactory.class) {
				return pool.numberOfOpenConnection();
			}
		}
		return null;
	}

	public static Integer numberOfActiveConnection(ConnectionHost host) {
		ConnectionPool pool = getConnectionPool(host);
		if(pool != null) {
			synchronized (ConnectionPoolFactory.class) {
				return pool.numberOfActiveConnection();
			}
		}
		return null;
	}
	
	public static void shutDownPool(ConnectionHost host) {
		ConnectionPool pool = getConnectionPool(host);
		if(pool != null) {
			synchronized (ConnectionPoolFactory.class) {
				pool.shutDownPool();
			}
		}
	}

	
}
