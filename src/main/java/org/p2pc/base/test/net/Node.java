package org.p2pc.base.test.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.map.Value;
import org.p2pc.base.test.net.con.Host;
import org.p2pc.base.test.net.con.protocol.Parameter;

/**
 * base node interface 
 * 
 * @author tkrieger
 *
 */
public abstract class Node implements Parameter {
	
	/**
	 * host information
	 */
	protected Host host;
	
	/**
	 * host accessor 
	 * 
	 * @return
	 */
	public Host getHost() {
		return host;
	}

	/**
	 * host setter 
	 * 
	 * @param host
	 */
	public void setHost(Host host) {
		this.host = host;
	}


	/**
	 * lookup a map key
	 * 
	 * @param key
	 * @return
	 */
	public abstract Value get(Key key) throws ClientException;
	
	/**
	 * set a map value
	 * 
	 * @param key
	 * @param data
	 */
	public abstract void set(Key key,Value data) throws ClientException;
	
	/**
	 * find a success for the given node
	 * 
	 * @param n
	 * @return
	 */
	public abstract Node findSuccessor(Key key) throws ClientException;
	
	/**
	 * get current predecessor 
	 * 
	 * @return
	 * @throws ClientException 
	 */
	public abstract Node getPredecessor() throws ClientException;
	
	/**
	 * notify node
	 * 
	 * @param n
	 * @throws ClientException 
	 */
	public abstract void notify(Node n) throws ClientException;
	
	/**
	 * ping the node to check alive 
	 * 
	 * @return
	 * @throws ClientException 
	 */
	public abstract boolean ping() throws ClientException;
	
	/**
	 * create node parameter serialization
	 */
	@Override
	public byte[] getByteData() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream       out = new DataOutputStream(bout);
		
		out.write(this.getHost().getKey().hash);
		out.writeInt(this.host.getPort());
		
		out.writeBytes(host.getHostname());
		out.write(0);
		
		out.close();
		
		byte[] data = bout.toByteArray();
		
		return data;
	}
	
	@Override
	public String toString() {
		return host.toString();
	}

}
