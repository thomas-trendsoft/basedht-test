package org.p2pc.base.test.net.con;

import org.p2pc.base.test.Version;
import org.p2pc.base.test.map.Key;

/**
 * host info object
 * 
 * @author tkrieger
 *
 */
public class Host {
	
	/**
	 * host key value
	 */
	private Key key;
	
	/**
	 * protocol version
	 */
	private Version version;
	
	/**
	 * hostname or ip 
	 */
	private String hostname;
	
	/**
	 * listening port 
	 */
	private int port;
	
	/**
	 * default constructor 
	 * 
	 * @param hostname
	 * @param port
	 */
	public Host(String hostname,int port) {
		this.hostname = hostname;
		this.port = port;
	}

	public Key getKey() {
		return key;
	}
	
	public void setKey(Key k) {
		this.key = k;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return hostname + ":"  + port;
	}

}
