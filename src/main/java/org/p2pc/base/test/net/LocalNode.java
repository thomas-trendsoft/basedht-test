package org.p2pc.base.test.net;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.map.Value;
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
	private ConcurrentHashMap<String, Value> localMap;
	
	/**
	 * routing information
	 */
	private Routing routes;
	
	/**
	 * logger interface
	 */
	private Logger log;
	
	/**
	 * stabilization task
	 */
	private StabilizeTask stabilizeTask;
	
	/**
	 * default constructor 
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	public LocalNode(Key k) throws NoSuchAlgorithmException {
		log         = LoggerFactory.getLogger("LocalNode");
		localMap    = new ConcurrentHashMap<>();
		
		routes        = new Routing();
		stabilizeTask = new StabilizeTask(this,routes);
	}
	
	/**
	 * get the current predecessor
	 */
	@Override
	public Node getPredecessor() {
		return routes.getPredecessor();
	}
	
	/**
	 * bootstrap a new network
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	public void bootstrap() throws NoSuchAlgorithmException {
		log.info("bootstrap node");
		routes.setPredecessor(null);
		routes.setSuccessor(this);
		
		stabilizeTask.start();
	}
	
	/**
	 * join a p2p network
	 * 
	 * @throws NoSuchAlgorithmException 
	 * @throws ClientException 
	 */
	public void join(Node hub) throws NoSuchAlgorithmException, ClientException {
		
		log.info("join network: " + hub.host + ":" + this.host.getKey());
		
		// set predecessor empty
		routes.setPredecessor(null);
		
		// check if we can init with a successor from p2p net
		Node succ = hub.findSuccessor(this.host.getKey());
		if (succ != null && succ.host.getPort() != -1)
			routes.setSuccessor(succ);
		else {
			log.error("failed to get successor");
			return;
		}
		
		log.info("joined success: " + routes.getSuccessor().getHost());

		stabilizeTask.start();
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
		System.out.println("get local map entry : " + key);
		return localMap.get(key.toString());
	}
	
	/**
	 * set a map value
	 * 
	 * @param key
	 * @param data
	 */
	public boolean set(Key key,Value data) {
		System.out.println("set local map entry : " + key);
		localMap.put(key.toString(), data);
		return true;
	}
	
	/**
	 * find Successor node of a given key
	 * 
	 * @throws ClientException 
	 */
	public Node findSuccessor(Key key) throws ClientException {
		if (key.inside(this.host.getKey(),routes.getSuccessor().getHost().getKey())) {
			return routes.getSuccessor();
		} else {
			Node p = routes.closestPrecedingNode(key,this);
			if (p != this)
				return p.findSuccessor(key);
			else {
				log.debug("how can this be if inside this should be conflict");
				return routes.getSuccessor();
			}
		}
	}


	@Override
	public void notify(Node n) throws ClientException {
		// check update predecessor
		if (routes.getPredecessor() == null || 
			n.getHost().getKey().stabilizeInside(routes.getPredecessor().getHost().getKey(), this.host.getKey())) {
			log.info(("update predecessor: " + n.getHost() + ":" + n.getClass().getSimpleName()));
			routes.setPredecessor(n);
		}
	}

	@Override
	public boolean ping() throws ClientException {
		return true;
	}

}
