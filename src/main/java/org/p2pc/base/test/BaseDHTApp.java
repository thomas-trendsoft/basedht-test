package org.p2pc.base.test;

import java.security.NoSuchAlgorithmException;

import org.p2pc.base.test.net.LocalNode;
import org.p2pc.base.test.net.RemoteNode;

/**
 * node application starter
 * 
 * @author tkrieger
 *
 */
public class BaseDHTApp {
	
	/**
	 * base node configuration
	 */
	private NodeConfig config;
	
	/**
	 * local node implementation
	 */
	private LocalNode node;

	/**
	 * default constructor 
	 * 
	 * @param config
	 * @throws NoSuchAlgorithmException 
	 */
	public BaseDHTApp(NodeConfig config) throws NoSuchAlgorithmException {
		this.config = config;
		this.node   = new LocalNode("node" + Math.round(100*Math.random()));
	}
	
	/**
	 * start the server node and try to join network
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	public void startNode() throws NoSuchAlgorithmException {
		if (config.bootstrap) {
			node.bootstrap();
		} else {
			node.join(new RemoteNode(config.hubs.get(0)));
		}
	}
	
	/**
	 * start p2p node application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		NodeConfig cfg = new NodeConfig();
		
		try {
			BaseDHTApp app = new BaseDHTApp(cfg);			
			app.startNode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
