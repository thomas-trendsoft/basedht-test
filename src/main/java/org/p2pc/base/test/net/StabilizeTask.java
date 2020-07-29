package org.p2pc.base.test.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * stabilize algorithm implementation
 * 
 * @author tkrieger
 *
 */
public class StabilizeTask implements Runnable {

	/**
	 * shutdown flag
	 */
	private boolean stop;
	
	/**
	 * routing information
	 */
	private Routing routes;
	
	/**
	 * local node
	 */
	private LocalNode node;
	
	/**
	 * logging interface
	 */
	private Logger log;
	
	/**
	 * default constructor
	 *  
	 * @param routes
	 */
	public StabilizeTask(LocalNode n,Routing routes) {
		this.routes = routes;
		this.node   = n;
		this.log    = LoggerFactory.getLogger("stabilize");
	}
	
	/**
	 * start stabilize thread
	 */
	public void start() {
		stop = false;
		Thread t = new Thread(this);
		t.start();
	}
	
	/**
	 * stops stabilize thread if running
	 */
	public void stop() {
		stop = true;
	}
	
	/**
	 * stabilize steps
	 */
	public void stabilize() {
		// already possible?
		Node s = routes.getSuccessor();
		if (s == null) return;
		
		log.info("stabilize");

		try {
			Node x = s.getPredecessor();
			
			if (x != null && x.key.stabilizeInside(node.key, s.key)) {
				log.info("update successor: " + x.getHost());
				routes.setSuccessor(x);
			}
			
			s.notify(node);
			log.info("notify done");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * check predecessor of current routing
	 */
	private void checkPredecessor() {
		log.info("check_predecessor");
		Node        n = routes.getPredecessor();
		boolean check = false;
		
		if (n != null)
			try {
				check = n.ping();
			} catch (ClientException e) {
				e.printStackTrace();
				check = false;
			}
		
		if (!check) {
			log.info("predecessor lost");
			routes.setPredecessor(null);
		}
	}

	/**
	 * thread start point
	 */
	@Override
	public void run() {
		while (!stop) {
			
			// wait a short moment
			try { Thread.sleep(10000); } catch (Exception e) { e.printStackTrace(); }

			// call stabilize
			stabilize();
			
			// check predecessor 
			checkPredecessor();
			
		}
	}

}
