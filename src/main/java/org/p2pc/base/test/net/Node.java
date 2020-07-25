package org.p2pc.base.test.net;

import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.map.Value;
import org.p2pc.base.test.net.con.Host;

/**
 * base node interface 
 * 
 * @author tkrieger
 *
 */
public abstract class Node {
	
	/**
	 * host information
	 */
	protected Host host;
	
	/**
	 * key for map position
	 */
	protected Key key;
	
	/**
	 * lookup a map key
	 * 
	 * @param key
	 * @return
	 */
	public abstract Value get(Key key);
	
	/**
	 * set a map value
	 * 
	 * @param key
	 * @param data
	 */
	public abstract void set(Key key,Value data);
	
	/**
	 * find a success for the given node
	 * 
	 * @param n
	 * @return
	 */
	public abstract Node findSuccessor(Key key);

}
