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
		
		//log.info("stabilize");

		try {
			Node x = s.getPredecessor();
			
			if (x != null && x.getHost().getKey().stabilizeInside(node.getHost().getKey(), s.getHost().getKey())) {
				log.info("update successor: " + x.getHost());
				routes.setSuccessor(x);
			}
			
			s.notify(node);
			//log.info("notify done:" + node.getHost());
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * check predecessor of current routing
	 */
	private void checkPredecessor() {
		//log.info("check_predecessor");
		Node        n = routes.getPredecessor();
		boolean check = false;
		
		// check if present 
		if (n != null)
			
			// myself works ;)
			if (n == node) {
				check = true;
			} else {
				// ping remote node to check working
				try {
					log.info("ping: " + n + ":" + n.getClass().getSimpleName());
					check = n.ping();
				} catch (ClientException e) {
					e.printStackTrace();
					check = false;
				}				
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
			try { Thread.sleep(2500); } catch (Exception e) { e.printStackTrace(); }

			// call stabilize
			stabilize();
			
			// check predecessor 
			checkPredecessor();
			
		}
	}

}
