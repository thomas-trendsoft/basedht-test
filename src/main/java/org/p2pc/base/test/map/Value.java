package org.p2pc.base.test.map;

/**
 * base map value
 * 
 * @author tkrieger
 *
 */
public class Value {
	
	public Value(byte[] bytes) {
		this.data = bytes;
	}

	/**
	 * memory data
	 */
	public byte[] data;

}
