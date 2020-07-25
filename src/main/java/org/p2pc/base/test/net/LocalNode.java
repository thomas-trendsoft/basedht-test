package org.p2pc.base.test.net;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.p2pc.base.test.CryptoUtil;
import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.map.Value;
import org.p2pc.base.test.net.con.NodeServer;

/**
 * base peer network node
 * 
 * @author tkrieger
 *
 */
public class LocalNode extends Node {

	/**
	 * local memory map
	 */
	private ConcurrentHashMap<Key, Value> localMap;
	
	/**
	 * chord finger map
	 */
	private ArrayList<Node> fingers;
	
	/**
	 * node server interface
	 */
	private NodeServer server;
	
	/**
	 * ring links
	 */
	private Node predecessor;
	
	/**
	 * ring links
	 */
	private Node successor;
	
	/**
	 * default constructor 
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	public LocalNode(String name) throws NoSuchAlgorithmException {
		localMap = new ConcurrentHashMap<>();
		key      = CryptoUtil.createRandomKey(name);
		fingers  = new ArrayList<Node>(Key.size);
	}
	
	/**
	 * bootstrap a new network
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	public void bootstrap() throws NoSuchAlgorithmException {
		predecessor = null;
		successor   = this;
	}
	
	/**
	 * join a p2p network
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	public void join(Node hub) throws NoSuchAlgorithmException {
		predecessor = null;
		successor   = hub.findSuccessor(this.key);
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
	public Value get(Key key) {
		return null;
	}
	
	/**
	 * set a map value
	 * 
	 * @param key
	 * @param data
	 */
	public void set(Key key,Value data) {
		
	}
	
	/**
	 * find Successor node of a given key
	 */
	public Node findSuccessor(Key key) {
		if (key.inside(this.key,successor.key)) {
			return successor;
		} else {
			Node p = closestPrecedingNode(key);
			if (p == this) return null; // no cyclic call
			return p.findSuccessor(key);
		}
	}

	/**
	 * look up closest node of key inside the finger links
	 * 
	 * @param key
	 * @return
	 */
	private Node closestPrecedingNode(Key key) {
		for (int i=Key.size;i>=1;i--) {
			Node n = fingers.get(i);
			if (n != null && n.key.inside(this.key,key)) {
				return n;
			}
		}
		return this;
	}
	
}
