package org.p2pc.base.test;

import java.util.LinkedList;
import java.util.List;

import org.p2pc.base.test.net.con.Host;

/**
 * basic node config
 * 
 * @author tkrieger
 *
 */
public class NodeConfig {
	
	/**
	 * bootstrap mode
	 */
	public boolean bootstrap;
	
	/**
	 * local port (-1 = auto)
	 */
	public int localPort;
	
	/**
	 * available hub hostnames
	 */
	public List<Host> hubs;
	
	/**
	 * default constructor 
	 */
	public NodeConfig() {
		bootstrap = false;
		localPort = -1;
		hubs      = new LinkedList<>();
	}

}
