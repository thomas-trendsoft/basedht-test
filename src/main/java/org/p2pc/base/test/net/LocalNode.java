package org.p2pc.base.test.net;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.map.Value;
import org.p2pc.base.test.net.con.NodeServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * ring links
	 */
	private Node predecessor;
	
	/**
	 * ring links
	 */
	private Node successor;
	
	/**
	 * logger interface
	 */
	private Logger log;
	
	/**
	 * default constructor 
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	public LocalNode(Key k) throws NoSuchAlgorithmException {
		log      = LoggerFactory.getLogger("LocalNode");
		localMap = new ConcurrentHashMap<>();
		key      = k;
		fingers  = new ArrayList<Node>(Key.size);
	}
	
	/**
	 * bootstrap a new network
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	public void bootstrap() throws NoSuchAlgorithmException {
		log.info("bootstrap node");
		predecessor = null;
		successor   = this;
	}
	
	/**
	 * join a p2p network
	 * 
	 * @throws NoSuchAlgorithmException 
	 * @throws ClientException 
	 */
	public void join(Node hub) throws NoSuchAlgorithmException, ClientException {
		log.info("join network: " + hub.host + ":" + this.key);
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
	 * 
	 * @throws ClientException 
	 */
	public Node findSuccessor(Key key) throws ClientException {
		System.out.println("ckey: " + key);
		if (key.inside(this.key,successor.key)) {
			return successor;
		} else {
			Node p = closestPrecedingNode(key);
			return p.findSuccessor(key);
		}
	}

	/**
	 * look up closest node of key inside the finger links
	 * 
	 * @param key
	 * @return
	 * @throws ClientException 
	 */
	private Node closestPrecedingNode(Key key) throws ClientException {
		for (int i=fingers.size()-1;i>=0;i--) {
			Node n = fingers.get(i);
			if (n != null && n.key.inside(this.key,key)) {
				return n;
			}
		}
		return this;
	}

	@Override
	public void notify(Node n) {
		// TODO Auto-generated method stub
		
	}
	
}
