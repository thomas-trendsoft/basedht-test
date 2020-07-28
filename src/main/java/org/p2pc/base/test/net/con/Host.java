package org.p2pc.base.test.net.con;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.p2pc.base.test.Version;
import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.net.con.protocol.Parameter;

import io.netty.buffer.ByteBuf;

/**
 * host info object
 * 
 * @author tkrieger
 *
 */
public class Host implements Parameter {
	
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
	public Host(String hostname,int port,Key key) {
		this.hostname = hostname;
		this.port     = port;
		this.key      = key;
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

	@Override
	public byte[] getByteData() throws IOException {
		ByteArrayOutputStream  out = new ByteArrayOutputStream();
		DataOutputStream      dout = new DataOutputStream(out);
		
		dout.writeInt(port);
		dout.writeBytes(hostname);
		
		
		return null;
	}
	

}
