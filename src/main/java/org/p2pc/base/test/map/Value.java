package org.p2pc.base.test.map;

import java.io.IOException;

import org.p2pc.base.test.net.con.protocol.Parameter;

/**
 * base map value
 * 
 * @author tkrieger
 *
 */
public class Value implements Parameter {
	
	/**
	 * memory data
	 */
	public byte[] data;

	/**
	 * default constructor 
	 * 
	 * @param bytes
	 */
	public Value(byte[] bytes) {
		this.data = bytes;
	}

	/**
	 * serialization 
	 */
	@Override
	public byte[] getByteData() throws IOException {
		return data;
	}

}
