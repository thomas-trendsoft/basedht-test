package org.p2pc.base.test.net.con;

import org.p2pc.base.test.map.Key;

public class Host {
	
	private Key key;
	
	private String hostname;
	
	private int port;
	
	private int version;
	
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return hostname + ":"  + port;
	}

}
