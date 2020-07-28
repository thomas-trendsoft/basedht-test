package org.p2pc.base.test.net.con.protocol;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.p2pc.base.test.Version;
import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.net.ClientException;
import org.p2pc.base.test.net.LocalNode;
import org.p2pc.base.test.net.Node;
import org.p2pc.base.test.net.con.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * chord protocol implementation 
 * 
 * @author tkrieger
 *
 */
public class BaseDHTProtocol {
	
	/**
	 * singleton accessor 
	 */
	public static final BaseDHTProtocol singleton = new BaseDHTProtocol();
	
	/**
	 * message parser and creator
	 */
	private MessageFactory factory;
	
	/**
	 * logging interface
	 */
	private Logger log;
	
	/**
	 * Node interface
	 */
	private LocalNode node;
	
	/**
	 * default constructor 
	 */
	private BaseDHTProtocol() {
		factory = MessageFactory.singleton;
		log     = LoggerFactory.getLogger("DHTProtocol");
	}
	
	/**
	 * node interface 
	 * 
	 * @param node
	 */
	public void setNode(LocalNode node) {
		this.node = node;
	}

	/**
	 * handle hello message
	 * 
	 * @param welcome
	 * @return
	 */
	public Message hello(Message m) {
		log.info("respond welcome: " + m.getRequestId());
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

	public Message findSuccessor(Message m) {
		log.info("find successor: " + m.getRequestId());
		try {
			Key fk = new Key(m.getParams().get(0).getByteData(),"fs");
			Node n = node.findSuccessor(fk);
			
			if (node != null) {
				Message ret = new Message(m.getRequestId(),Commands.SUCCESSORFIND);
				ret.addParam(n);
				return ret;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public Message predecessor(Message m) {
		Message answer = new Message(m.getRequestId(), Commands.VALUE);
		
		answer.addParam(node.getPredecessor());
		
		return answer;
	}	

}
