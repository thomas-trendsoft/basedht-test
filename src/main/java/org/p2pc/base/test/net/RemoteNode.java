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
	
	@Override
	public Value get(Key key) {
		Message    msg = new Message(Message.GET);
		Connection con = ConnectionPool.singleton.getConnection(host);
		
		con.sendMsg(msg);
		
		return null;
	}

	@Override
	public void set(Key key, Value data) {
		Message    msg = new Message(Message.SET);
		Connection con = ConnectionPool.singleton.getConnection(host);
		
		con.sendMsg(msg);

	}

	@Override
	public Node findSuccessor(Key key) {
		return null;
	}

	@Override
	public void notify(Node n) {
		// TODO Auto-generated method stub
		
	}

	
	
}
