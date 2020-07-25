package org.p2pc.base.test.net.con;

import java.util.concurrent.ConcurrentHashMap;

import org.p2pc.base.test.map.Key;

/**
 * p2p connection pool 
 * 
 * @author tkrieger
 *
 */
public class ConnectionPool {
	
	/**
	 * singleton connection pool
	 */
	public final static ConnectionPool singleton = new ConnectionPool();
	
	/**
	 * active connection map
	 */
	private ConcurrentHashMap<Key, Connection> active;
	
	/**
	 * default constructor 
	 */
	private ConnectionPool() {
		active = new ConcurrentHashMap<>();
	}
	
	/**
	 * add available connection
	 * 
	 * @param host host key
	 * @param con open connection
	 */
	public void registerConnection(Key host,Connection con) {
		active.put(host, con);
	}
	
	/**
	 * create or get a available host connection
	 * 
	 * @param host
	 */
	public void getConnection(Host host) {
		
	}
	
}
