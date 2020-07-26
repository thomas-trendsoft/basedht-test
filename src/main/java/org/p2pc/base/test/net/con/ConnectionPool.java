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
	 * Request ID generator
	 */
	private static int reqid;
	
	/**
	 * default constructor 
	 */
	private ConnectionPool() {
		reqid  = (int) (Math.random() * Integer.MAX_VALUE);
		active = new ConcurrentHashMap<>();
	}
	
	/**
	 * local node request id sequence
	 * 
	 * @return
	 */
	public synchronized static int getRequestId() {
		int id = reqid++;
		if (reqid == Integer.MAX_VALUE) reqid = 0;
		return id;
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
	public Connection getConnection(Host host) {
		if (active.contains(host.getKey())) {
			return active.get(host.getKey());
		}
		
		// try to create a new connection
		return null;
	}
	
}
