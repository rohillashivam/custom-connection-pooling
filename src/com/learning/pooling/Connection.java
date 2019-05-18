package com.learning.pooling;

import com.learning.pooling.enums.ConnectionState;

public interface Connection extends Runnable {

	public void release();
	
	public void openConnection();
	
	public void invalidateConnection();
	
	public ConnectionState getState();
}
