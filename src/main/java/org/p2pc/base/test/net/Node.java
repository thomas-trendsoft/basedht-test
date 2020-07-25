package org.p2pc.base.test.net;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.p2pc.base.test.CryptoUtil;
import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.map.Value;
import org.p2pc.base.test.net.con.Host;

/**
 * base peer network node
 * 
 * @author tkrieger
 *
 */
public class Node {

	/**
	 * local memory map
	 */
	private ConcurrentHashMap<Key, Value> localMap;
	
	/**
	 * DHT Node Key
	 */
	private Key nodeKey;
	
	/**
	 * default constructor 
	 */
	public Node() {
		localMap = new ConcurrentHashMap<>();
	}
	
	/**
	 * bootstrap a new network
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	public void bootstrap(String name) throws NoSuchAlgorithmException {
		nodeKey = CryptoUtil.createRandomKey(name);
	}
	
	/**
	 * join a p2p network
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	public void join(String name,List<Host> candidates) throws NoSuchAlgorithmException {
		// TODO check connection state
		
		nodeKey = CryptoUtil.createRandomKey(name); 
	}
	
	/**
	 * leave the active p2p network
	 */
	public void leave() {
		
	}
	
	/**
	 * lookup a map key
	 * 
	 * @param key
	 * @return
	 */
	public Value get(byte[] key) {
		return null;
	}
	
	/**
	 * set a map value
	 * 
	 * @param key
	 * @param data
	 */
	public void set(byte[] key,Value data) {
		
	}
	
}
