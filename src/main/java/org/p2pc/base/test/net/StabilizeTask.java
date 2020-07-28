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
	 * thread start point
	 */
	@Override
	public void run() {
		while (!stop) {
			
			// wait a short moment
			try { Thread.sleep(2000); } catch (Exception e) { e.printStackTrace(); }

			// already possible?
			Node s = routes.getSuccessor();
			if (s == null) continue;
			
			log.info("stabilize");

			try {
				Node x = s.getPredecessor();
				
				if (x.key.stabilizeInside(node.key, s.key)) {
					routes.setSuccessor(x);
				}
				
				s.notify(node);
				
				System.out.println();				
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
		}
	}

}
