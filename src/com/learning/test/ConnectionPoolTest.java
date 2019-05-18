package com.learning.test;

import java.util.ArrayList;
import java.util.List;

import com.learning.pooling.Connection;
import com.learning.pooling.factory.ConnectionPoolFactory;
import com.learning.pooling.impl.ConnectionHost;

public class ConnectionPoolTest {

	private static ConnectionHost host;
	static {
		host = new ConnectionHost("192.168.4.3", 9003);
		ConnectionPoolFactory.buildConnectionPoolFactory(host, 5, 10, 2);
	}
	
	public static void main(String[] args) {
		Connection connection = ConnectionPoolFactory.getConnection(host);
		System.out.println(connection.getState().toString());
		System.out.println("number of Open connection :: "+ConnectionPoolFactory.numberOfOpenConnection(host));
		connection.release();
		
		// incrementing the connection count check
		List<Connection> connectionList = new ArrayList<>();
		for(int i=0; i<9; i++) {
			connection = ConnectionPoolFactory.getConnection(host);
			System.out.println("number of Open connection :: "+ConnectionPoolFactory.numberOfOpenConnection(host));
			System.out.println("number of active connection :: "+ConnectionPoolFactory.numberOfActiveConnection(host));
			connectionList.add(connection);
		}
		// releasing the connections
		for(int i=0; i< connectionList.size(); i++) {
			connection = connectionList.get(i);
			connection.release();
			System.out.println("number of Open connection :: "+ConnectionPoolFactory.numberOfOpenConnection(host));
			System.out.println("number of active connection :: "+ConnectionPoolFactory.numberOfActiveConnection(host));
		}
		
		// shutdown the connection pool
		System.out.println("Calling shutdown");
		ConnectionPoolFactory.shutDownPool(host);
		
		// re-initializing the pool factory
		System.out.println("re initializing the factory");
		ConnectionPoolFactory.buildConnectionPoolFactory(host, 5, 10, 2);
		
		new Thread(() -> {
			for(int i=0; i< 10; i++) {
				Connection connectionObj = ConnectionPoolFactory.getConnection(host);
				System.out.println(connectionObj.getState().toString());
				System.out.println("number of Open connection :: "+ConnectionPoolFactory.numberOfOpenConnection(host));
			}
		}).start();;
		
		new Thread(() -> {
			for(int i=0; i< 10; i++) {
				Connection connectionObj = ConnectionPoolFactory.getConnection(host);
				System.out.println(connectionObj.getState().toString());
				System.out.println("number of Open connection :: "+ConnectionPoolFactory.numberOfOpenConnection(host));
			}
		}).start();;
	}

}
