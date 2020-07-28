package org.p2pc.base.test;

import java.security.NoSuchAlgorithmException;

import org.p2pc.base.test.net.LocalNode;
import org.p2pc.base.test.net.RemoteNode;
import org.p2pc.base.test.net.con.Host;
import org.p2pc.base.test.net.con.NodeServer;
import org.p2pc.base.test.net.con.protocol.BaseDHTProtocol;
import org.p2pc.base.test.net.con.protocol.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * p2p listening server
	 */
	private NodeServer server;
	
	/**
	 * logging interface
	 */
	private Logger log;
	
	/**
	 * default constructor 
	 * 
	 * @param config
	 * @throws NoSuchAlgorithmException 
	 */
	public BaseDHTApp(NodeConfig config) throws NoSuchAlgorithmException {
		this.log    = LoggerFactory.getLogger("DHTApp");
		this.config = config;
		
		// create local key first random
		this.config.key = CryptoUtil.createRandomKey("localhost");
		
		this.node   = new LocalNode(this.config.key);
		this.server = new NodeServer();	
		
		MessageFactory.singleton.setNode(this.node);
		BaseDHTProtocol.singleton.setNode(this.node);
		
		log.info("basedht app start / " + config);
	}
	
	
	/**
	 * start the server node and try to join network
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	public void startNode() throws NoSuchAlgorithmException {
		
		// setup local server point
		try {
			Thread t = new Thread(() -> {
					try { 
						server.start(); 
					} catch (Exception e) { 
						e.printStackTrace(); 
					} 
				});
			t.start();
			Thread.sleep(200);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("error on startup server: " + e.getMessage());
		}
		
		// put host info to local node
		// TODO check eth or ip name for listening for hash name and real hostname
		node.setHost(new Host("localhost", server.getPort(),this.config.key));	
		
		// start chord algorithm
		try {
			if (config.bootstrap) {
				node.bootstrap();
			} else {
				node.join(new RemoteNode(config.hubs.get(0)));
			}			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("failed to join or start network: " + e.getMessage());
		}
	}
	
	/**
	 * start p2p node application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		NodeConfig cfg = new NodeConfig(args);
		
		try {
			BaseDHTApp app = new BaseDHTApp(cfg);			
			app.startNode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
