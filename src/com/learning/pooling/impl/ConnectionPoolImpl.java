package com.learning.pooling.impl;

import java.util.HashSet;
import java.util.Set;

import com.learning.pooling.Connection;
import com.learning.pooling.ConnectionPool;

public class ConnectionPoolImpl implements ConnectionPool {

	private ConnectionHost host; 
	
	private Integer maxPoolSize;
	
	private Integer initialPoolSize;
	
	private Integer incrementalPoolSize; 
	
	private Set<Connection> openConnectionPoolList = null;

	private Set<Connection> activeConnectionPoolList = null;

	private Object MUTEX = new Object();

	
	
	public ConnectionPoolImpl(ConnectionPoolBuilder builder) {
		this.host = builder.host;
		this.maxPoolSize = builder.maxPoolSize;
		this.initialPoolSize = builder.initialPoolSize;
		this.incrementalPoolSize = builder.incrementalPoolSize;
		this.openConnectionPoolList = new HashSet<>(initialPoolSize);
		this.activeConnectionPoolList = new HashSet<>();
		for(int i=0; i<initialPoolSize; i++) {
			openConnectionPoolList.add(new RemoteConnection(host));
		}
	}

	@Override
	public Connection getConnection() {
		Connection connection = null;
		if (openConnectionPoolList != null && 
				!openConnectionPoolList.isEmpty()) {
			try {
				connection = openConnectionPoolList.iterator().next();
				openConnectionPoolList.remove(connection);
				synchronized (connection) {
					if(activeConnectionPoolList.contains(connection)) 
						connection = openConnectionPoolList.iterator().next();
					activeConnectionPoolList.add(connection);
					openConnectionPoolList.remove(connection);

					if(openConnectionPoolList.size() <= (0.75 * initialPoolSize)) {

						Integer totalConnections = openConnectionPoolList.size() + incrementalPoolSize + activeConnectionPoolList.size() ;
						if(totalConnections < maxPoolSize) {

							openNewConnections(totalConnections);
						}
					}
					connection.openConnection();
				}
			} catch (Exception e) {
				openConnectionPoolList.add(connection);
				if(activeConnectionPoolList.contains(connection))
					activeConnectionPoolList.remove(connection);
			}
		}
		return connection;
	}

	private void openNewConnections(Integer totalConnections) {
		Set<Connection> newOpenConnectionSet = new HashSet<>(totalConnections);
		newOpenConnectionSet.addAll(openConnectionPoolList);
		int increaseCount = 0;
		if(totalConnections - openConnectionPoolList.size() > 0)
			increaseCount = totalConnections - openConnectionPoolList.size();
		else
			increaseCount = totalConnections;
		for(int i=0; i< increaseCount; i++) {
			newOpenConnectionSet.add(new RemoteConnection(host));
		}
		openConnectionPoolList = newOpenConnectionSet;
	}

	@Override
	public void releaseConnection(Connection connection) {
		if(connection != null && activeConnectionPoolList != null && 
				!activeConnectionPoolList.isEmpty()) {
			synchronized (connection) {
				if(activeConnectionPoolList.contains(connection)) {
					activeConnectionPoolList.remove(connection);
					openConnectionPoolList.add(connection);
				}
			}
		} else if(connection == null) {
			throw new NullPointerException("null connection");
		} else {
			System.out.println("No active connection");
		}
	}

	@Override
	public void shutDownPool() {
		synchronized (MUTEX) {
			if(activeConnectionPoolList != null) {
				activeConnectionPoolList.clear();
				activeConnectionPoolList = null;
			}
			if(openConnectionPoolList != null) {
				openConnectionPoolList.clear();
				openConnectionPoolList = null;
			}
		}
	}
	
	public static class ConnectionPoolBuilder {
		private ConnectionHost host;
		
		private Integer maxPoolSize;
		
		private Integer initialPoolSize;
		
		private Integer incrementalPoolSize;
		
		public ConnectionPool build() {
			return new ConnectionPoolImpl(this);
		}

		public ConnectionHost getHost() {
			return host;
		}

		public void setHost(ConnectionHost host) {
			this.host = host;
		}

		public Integer getMaxPoolSize() {
			return maxPoolSize;
		}

		public void setMaxPoolSize(Integer maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
		}

		public Integer getInitialPoolSize() {
			return initialPoolSize;
		}

		public void setInitialPoolSize(Integer initialPoolSize) {
			this.initialPoolSize = initialPoolSize;
		}

		public Integer getIncrementalPoolSize() {
			return incrementalPoolSize;
		}

		public void setIncrementalPoolSize(Integer incrementalPoolSize) {
			this.incrementalPoolSize = incrementalPoolSize;
		}
		
	}
	
	@Override
	public Integer numberOfActiveConnection() {
		return this.activeConnectionPoolList.size();
	}
	
	@Override
	public Integer numberOfOpenConnection() {
		return this.openConnectionPoolList.size();
	}
	
}
