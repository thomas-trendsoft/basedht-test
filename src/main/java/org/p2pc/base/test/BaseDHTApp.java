package org.p2pc.base.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;

import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.map.Value;
import org.p2pc.base.test.net.LocalNode;
import org.p2pc.base.test.net.Node;
import org.p2pc.base.test.net.RemoteNode;
import org.p2pc.base.test.net.con.ConnectionPool;
import org.p2pc.base.test.net.con.Host;
import org.p2pc.base.test.net.con.NodeServer;
import org.p2pc.base.test.net.con.protocol.BaseDHTProtocol;
import org.p2pc.base.test.net.con.protocol.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.CharsetUtil;

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
	
	protected boolean putEntry(String key,String value) {
		try {
			Key       k = new Key(CryptoUtil.hashData(key.getBytes(CharsetUtil.UTF_8)),"set");
			Node target = node.findSuccessor(k);
			
			if (target == null) {
				System.out.println("no successor found");
				return false;
			}
			
			return target.set(k, new Value(value.getBytes(CharsetUtil.UTF_8)));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected String getEntry(String key) {
		try {
			Key       k = new Key(CryptoUtil.hashData(key.getBytes(CharsetUtil.UTF_8)),"set");
			Node target = node.findSuccessor(k);
			
			System.out.println(target);
			if (target == null) {
				System.out.println("no successor found");
				return null;
			}
			
			Value v = target.get(k);
			if (v == null) {
				System.out.println("got null value");
				return null;
			} else
				return new String(target.get(k).data,CharsetUtil.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
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
		
		// execute command line to test
		String cmd = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		do {
			try {
				boolean fit = false;
				cmd = reader.readLine();
				System.out.println("cmd: " + cmd);
				String[] args = cmd.split(" ");
				if (args != null && args.length > 1) {
					if ("put".compareTo(args[0])==0) {
						if (args.length == 3) {
							fit = true;
							System.out.println("set key: " + putEntry(args[1], args[2]));
						}
					} else if ("get".compareTo(args[0])==0) {
						System.out.println(getEntry(args[1]));
					}
				} 
				if (!fit) {
					System.out.println("usage: \nput key value\nget key\n\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while ("exit".compareTo(cmd)!=0);
		
	}
	
	/**
	 * start p2p node application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		NodeConfig cfg = new NodeConfig(args);
		
		// add shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			ConnectionPool.singleton.closeAll();
		}));
		
		try {
			BaseDHTApp app = new BaseDHTApp(cfg);			
			app.startNode();
			System.out.println("app started");
		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}
	
}
