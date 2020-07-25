package org.p2pc.base.test.map;

/**
 * hash map key value representation
 * 
 * @author tkrieger
 *
 */
public class Key {
	
	public static final int size = 256;
	
	/**
	 * byte value
	 */
	public byte[] hash;
	
	/**
	 * internal key name
	 */
	public String name;
	
	/**
	 * default constructor 
	 * 
	 * @param hash
	 * @param name
	 */
	public Key(byte[] hash,String name) {
		this.hash = hash;
		this.name = name;
	}

	public boolean inside(Key key, Key key2) {
		return false;
	}
	
}
