package org.p2pc.base.test.net.con;

import java.util.concurrent.CompletableFuture;

import org.p2pc.base.test.net.con.protocol.Message;

/**
 * base connection interface to cumulate server and client connections 
 * 
 * @author tkrieger
 *
 */
public interface Connection {
	
	/**
	 * sends data and 
	 * @param data
	 * @return
	 */
	CompletableFuture<Message> sendMsg(Message msg);

}
