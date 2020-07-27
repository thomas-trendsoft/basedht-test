package org.p2pc.base.test.net;

import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.map.Value;
import org.p2pc.base.test.net.con.Connection;
import org.p2pc.base.test.net.con.ConnectionPool;
import org.p2pc.base.test.net.con.Host;
import org.p2pc.base.test.net.con.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * basic remote interface for other nodes
 * 
 * @author tkrieger
 *
 */
public class RemoteNode extends Node {

	/**
	 * Logging interface
	 */
	private Logger log;
	
	/**
	 * default constructor 
	 * 
	 * @param host
	 */
	public RemoteNode(Host host) {
		this.log  = LoggerFactory.getLogger("RemoteNode");
		this.host = host;
	}
	
	/**
	 * query a key from the map
	 * 
	 * @throws ClientException 
	 */
	@Override
	public Value get(Key key) throws ClientException {
		Message    msg = new Message(Message.GET);
		Connection con = ConnectionPool.singleton.getConnection(host);
		Message    answer;
		
		try {
			answer = con.sendMsg(msg).get();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClientException("failed to send get key msg: " + host.getHostname() + ":" + e.getMessage());
		}
		
		
		
		return null;
	}

	/**
	 * send a set key signal
	 * @throws ClientException 
	 */
	@Override
	public void set(Key key, Value data) throws ClientException {
		Message    msg = new Message(Message.SET);
		Connection con = ConnectionPool.singleton.getConnection(host);
		Message    answer;
		
		try {
			answer = con.sendMsg(msg).get();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClientException("failed to send set key msg: " + host.getHostname() + ":" + e.getMessage());
		}

	}

	/**
	 * find next successor node
	 * @throws ClientException 
	 */
	@Override
	public Node findSuccessor(Key key) throws ClientException {
		log.info(host + " findSuccessor");

		Message    msg = new Message(Message.FINDSUCCESSOR);
		Connection con = ConnectionPool.singleton.getConnection(host);
		Message    answer;
		
		try {
			answer = con.sendMsg(msg).get();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClientException("failed to send set key msg: " + host.getHostname() + ":" + e.getMessage());
		}
		
		return null;
	}

	@Override
	public void notify(Node n) {
		
	}

	
	
}
