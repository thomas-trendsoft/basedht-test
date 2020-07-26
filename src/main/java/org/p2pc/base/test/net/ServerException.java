package org.p2pc.base.test.net;

/**
 * base server side exception 
 * 
 * @author tkrieger
 *
 */
public class ServerException extends Exception {

	/**
	 * serialization id
	 */
	private static final long serialVersionUID = 4737656621829727630L;

	/**
	 * default constructor 
	 * 
	 * @param msg
	 */
	public ServerException(String msg) {
		super(msg);
	}
}
