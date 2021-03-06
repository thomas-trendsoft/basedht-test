package org.p2pc.base.test.net;

import org.p2pc.base.test.map.Key;
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
	 * current finger check pos
	 */
	private int fingerpos;
	
	/**
	 * default constructor
	 *  
	 * @param routes
	 */
	public StabilizeTask(LocalNode n,Routing routes) {
		this.routes = routes;
		this.node   = n;
		this.log    = LoggerFactory.getLogger("stabilize");
		this.fingerpos = 0;
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
	
	private void fixFingers() {
		if (fingerpos++ > Key.size) {
			fingerpos = 1;
		}
		Key  fkey   = node.getHost().getKey().addPower(fingerpos);
		try {
			Node update = node.findSuccessor(fkey);
			// if new node and not myself
			if (update != null && update != node) {
				routes.setFinger(fingerpos, update);
			}
		} catch (ClientException e) {
			log.info("failed to update finger table");
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

			try {
				// call stabilize
				stabilize();
				
				// check predecessor 
				checkPredecessor();
				
				// update finger entry
				fixFingers();				
			} catch (Exception e) {
				log.error("failed to stabilize: " + e.getMessage());
				e.printStackTrace();
			}
			
		}
	}

}
