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
	 * @param args 
	 */
	public NodeConfig(String[] args) {
		bootstrap = false;
		localPort = -1;
		hubs      = new LinkedList<>();
		
		if (args != null && args.length > 0)
			parseArgs(args);
	}

	/**
	 * parse command line arguments
	 * 
	 * @param args
	 */
	private void parseArgs(String[] args) {
		int dp;
		
		for (String s : args) {
			// bootstrap param
			if (s.compareTo("bootstrap")==0) {
				bootstrap = true;
			// hub address
			} else if ((dp = s.indexOf(":")) > 0){
				 String    hn = s.substring(0,dp);
				 Integer   hp = Integer.parseInt(s.substring(dp+1));
				 hubs.add(new Host(hn, hp));
			}
		}
	}
	
	/**
	 * simple presentation of config values
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("node config: ");
		if (bootstrap)
			buf.append("BOOTSTRAP ");
		buf.append(" / hubs: ");
		for (Host h : hubs) {
			buf.append(h.toString());
			buf.append(" ");
		}
		
		return buf.toString();
	}

}
