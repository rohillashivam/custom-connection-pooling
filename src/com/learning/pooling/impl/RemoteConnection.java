package com.learning.pooling.impl;

import java.util.UUID;

import com.learning.pooling.Connection;
import com.learning.pooling.enums.ConnectionState;
import com.learning.pooling.factory.ConnectionPoolFactory;
import com.learning.service.ConnectionService;

public class RemoteConnection implements Connection {

	private UUID id;
	
	private ConnectionHost host;
	
	private ConnectionState state;
	
	private ConnectionService connectionService;
	
	public RemoteConnection(ConnectionHost host) {
		this.host = host;
		this.id = UUID.randomUUID();
		state = ConnectionState.OPEN;
	}
	
	@Override
	public void run() {
		while(true);
	}
	
	public Object execute(String str) {
		return connectionService.execute(str);
	}
	
	@Override
	public void release() {
		this.state = ConnectionState.OPEN;
		ConnectionPoolFactory.release(host, this);
	}
	
	@Override
	public void openConnection() {
		this.state = ConnectionState.ACTIVE;
	}
	
	public void invalidateConnection() {
		this.state = ConnectionState.INVALID;
	}

	@Override
	public ConnectionState getState() {
		return state;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RemoteConnection other = (RemoteConnection) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (state != other.state)
			return false;
		return true;
	}

}
