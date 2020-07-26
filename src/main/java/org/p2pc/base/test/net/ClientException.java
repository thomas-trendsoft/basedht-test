package org.p2pc.base.test.net;

/**
 * client side exceptions
 * 
 * @author tkrieger
 *
 */
public class ClientException extends Exception {

	/**
	 * serialization id
	 */
	private static final long serialVersionUID = 2464084220851095797L;

	/**
	 * default constructor 
	 * 
	 * @param msg
	 */
	public ClientException(String msg) {
		super(msg);
	}
	
}
