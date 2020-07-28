package org.p2pc.base.test.net.con.protocol;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.p2pc.base.test.Version;
import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.net.ClientException;
import org.p2pc.base.test.net.con.ClientConnection;
import org.p2pc.base.test.net.con.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * chord protocol implementation 
 * 
 * @author tkrieger
 *
 */
public class BaseDHTProtocol {
	
	/**
	 * message parser and creator
	 */
	private MessageFactory factory;
	
	/**
	 * logging interface
	 */
	private Logger log;
	
	/**
	 * default constructor 
	 */
	public BaseDHTProtocol() {
		factory = MessageFactory.singleton;
		log     = LoggerFactory.getLogger("DHTProtocol");
	}

	/**
	 * handle hello message
	 * 
	 * @param welcome
	 * @return
	 */
	public Message hello(Message m) {
		return factory.welcome(m.getRequestId());
	}

	/**
	 * handle ping message
	 * 
	 * @param m
	 * @return
	 */
	public Message ping(Message m) {
		return null;
	}
	
	/**
	 * handshake the client connection
	 * 
	 * @return
	 * @throws IOException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws ClientException 
	 */
	public void handshake(Connection con) throws IOException, InterruptedException, ExecutionException, ClientException {
		CompletableFuture<Message> cf = new CompletableFuture<Message>();
		
		System.out.println("make handshake");
		
		Message hello = MessageFactory.singleton.hello();

		cf = con.sendMsg(hello);
		log.debug("handshake send...");
		
		Message answer;
		try {
			answer = cf.get(2,TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			e.printStackTrace();
			throw new ClientException("handshake timeout: " + con.getHost());
		}
	
		if (Commands.WELCOME != answer.getMsg()) {
			throw new ClientException("unknown handshake answer: " + con.getHost());
		}
		
		// check and extract params
		if (answer.getParams().size() < 2) {
			throw new ClientException("insufficient handshake answer (expected 2 params): " + con.getHost().getHostname() + ":" + answer.getParams().size());			
		}
		
		Parameter pv = answer.getParams().get(0);
		Parameter pk = answer.getParams().get(1);
		
		con.getHost().setVersion(new Version(pv.getByteData()));
		con.getHost().setKey((new Key(pk.getByteData(), con.getHost().getHostname())));
		
	}	

}
