package org.p2pc.base.test.net;

import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.map.Value;
import org.p2pc.base.test.net.con.Connection;
import org.p2pc.base.test.net.con.ConnectionPool;
import org.p2pc.base.test.net.con.Host;
import org.p2pc.base.test.net.con.protocol.Message;

/**
 * basic remote interface for other nodes
 * 
 * @author tkrieger
 *
 */
public class RemoteNode extends Node {

	/**
	 * remote address and key id
	 */
	private Host host;
	
	/**
	 * default constructor 
	 * 
	 * @param host
	 */
	public RemoteNode(Host host) {
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
	 */
	@Override
	public Node findSuccessor(Key key) {
		return null;
	}

	@Override
	public void notify(Node n) {
		
	}

	
	
}
