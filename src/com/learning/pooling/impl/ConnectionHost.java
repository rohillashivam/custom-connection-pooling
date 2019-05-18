package com.learning.pooling.impl;

public class ConnectionHost {

	private String host;
	
	private Integer port;
	
	public ConnectionHost(String host, Integer port) {
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ConnectionHost) {
			ConnectionHost connectionObj = (ConnectionHost) obj;
			if(connectionObj.getHost().equals(this.getHost())
					&& connectionObj.getPort().equals(this.getPort()))
				return true;
			else
				return false;
		}
		return false;
	}

	
}
