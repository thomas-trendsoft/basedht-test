package org.p2pc.base.test.net.con.protocol;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.p2pc.base.test.Version;
import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.map.Value;
import org.p2pc.base.test.net.ClientException;
import org.p2pc.base.test.net.LocalNode;
import org.p2pc.base.test.net.Node;
import org.p2pc.base.test.net.RemoteNode;
import org.p2pc.base.test.net.con.Connection;
import org.p2pc.base.test.net.con.Host;
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
	 * null value node
	 */
	private Node nullNode;
	
	/**
	 * default constructor 
	 */
	private BaseDHTProtocol() {
		byte[] ekey = new byte[32];
		nullNode = new RemoteNode(new Host("null", -1,new Key(ekey,"")));
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
		return factory.welcome(m.getRequestId());
	}

	/**
	 * handle ping message
	 * 
	 * @param m
	 * @return
	 */
	public Message ping(Message m) {
		return new Message(m.getRequestId(),Commands.PONG);
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
		
		Key hkey = new Key(pk.getByteData(), con.getHost().getHostname());
		con.getHost().setVersion(new Version(pv.getByteData()));
		con.getHost().setKey(hkey);
		
	}

	/**
	 * call find successor from remote 
	 * 
	 * @param m
	 * @return
	 */
	public Message findSuccessor(Message m) {
		//log.info("find successor: " + m.getRequestId());
		try {
			Key fk = new Key(m.getParams().get(0).getByteData(),"fs");
			Node n = node.findSuccessor(fk);
			
			Message ret = new Message(m.getRequestId(),Commands.SUCCESSORFIND);
			if (n != null) {
				ret.addParam(n);
			} else {
				ret.addParam(nullNode);
			}
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("FAIL FIND");
		
		return null;
	}

	/**
	 * call get predecessor from remote 
	 * 
	 * @param m
	 * @return
	 */
	public Message predecessor(Message m) {
		Message answer = new Message(m.getRequestId(), Commands.PREDANSWER);
		
		Node pnode = node.getPredecessor();
		if (pnode == null) {
			log.info("null predecessor return");
			answer.addParam(nullNode);
		} else {
			answer.addParam(pnode);			
		}
		
		return answer;
	}

	/**
	 * call notify from remote 
	 * 
	 * @param m
	 */
	public void notifyNode(Message m) {
		Node n = (Node) m.getParams().get(0);
		
		try {
			node.notify(n);
		} catch (ClientException e) {
			e.printStackTrace();
			log.error("failed to notify local: " + e.getMessage());
		}
	}

	/**
	 * execute get command
	 * 
	 * @param m
	 * @return
	 */
	public Message get(Message m) {
		Message answer = new Message(m.getRequestId(),Commands.VALUE);
		
		Value v = node.get((Key)m.getParams().get(0));
		if (v == null) {
			v = new Value(new byte[0]);
		} 
		
		answer.addParam(new BaseParameter(MessageFactory.singleton.intToBytes(v.data.length)));
		answer.addParam(v);
		
		return answer;
	}	
	
	/**
	 * call set method
	 * 
	 * @param m
	 * @return
	 */
	public Message set(Message m) {
		Key   k = (Key)   m.getParams().get(0);
		Value v = (Value) m.getParams().get(1);
		
		System.out.println("set received");
		node.set(k,v);
		
		Message answer = new Message(m.getRequestId(),Commands.DONE);
		
		return answer;
	}

}
