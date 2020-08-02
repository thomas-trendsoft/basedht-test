package org.p2pc.base.test.net;

import java.util.concurrent.TimeUnit;

import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.map.Value;
import org.p2pc.base.test.net.con.Connection;
import org.p2pc.base.test.net.con.ConnectionPool;
import org.p2pc.base.test.net.con.Host;
import org.p2pc.base.test.net.con.protocol.BaseParameter;
import org.p2pc.base.test.net.con.protocol.Commands;
import org.p2pc.base.test.net.con.protocol.Message;
import org.p2pc.base.test.net.con.protocol.MessageFactory;
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
		Message    msg = new Message(Commands.GET);
		Connection con = ConnectionPool.singleton.getConnection(host);
		Message    answer;
		
		try {
			answer = con.sendMsg(msg).get(2, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClientException("failed to send get key msg: " + host.getHostname() + ":" + e.getMessage());
		}
		
		if (answer.getParams().size() != 1) {
			throw new ClientException("wrong param values on get");
		}
		
		return (Value)answer.getParams().get(0);
	}

	/**
	 * send a set key signal
	 * 
	 * @throws ClientException 
	 */
	@Override
	public boolean set(Key key, Value data) throws ClientException {
		Message    msg = new Message(Commands.SET);
		Connection con = ConnectionPool.singleton.getConnection(host);
		Message    answer;
		
		System.out.println("send set command");
		try {
			msg.addParam(key);
			msg.addParam(new BaseParameter(MessageFactory.singleton.intToBytes(data.data.length)));
			msg.addParam(data);
			System.out.println("set with len: " + data.data.length);
			answer = con.sendMsg(msg).get(2, TimeUnit.SECONDS);
		} catch (Exception e) {
			ConnectionPool.singleton.removeConnection(con);
			e.printStackTrace();
			throw new ClientException("failed to send set key msg: " + host.getHostname() + ":" + e.getMessage());
		}
		
		return answer.getMsg() == Commands.DONE;

	}

	/**
	 * find next successor node
	 * 
	 * @throws ClientException 
	 */
	@Override
	public Node findSuccessor(Key key) throws ClientException {
		log.info(host + " findSuccessor");

		Message    msg = new Message(Commands.FINDSUCCESSOR);
		msg.addParam(key);
		
		Connection con = ConnectionPool.singleton.getConnection(host);
		Message    answer;
		
		try {
			answer = con.sendMsg(msg).get(2, TimeUnit.SECONDS);
		} catch (Exception e) {
			ConnectionPool.singleton.removeConnection(con);			
			e.printStackTrace();
			throw new ClientException("failed to send set key msg: " + host.getHostname() + ":" + e.getMessage());
		}
		
		if (answer.getParams().size() == 0) {
			throw new ClientException("no node as respond from find successor");
		}
		
		return (Node) answer.getParams().get(0);
	}

	/**
	 * notify on remote node
	 */
	@Override
	public void notify(Node n) throws ClientException {
		Message    msg = new Message(Commands.NOTIFY);
		Connection con = ConnectionPool.singleton.getConnection(host);
		
		try {
			msg.addParam(n);
			con.sendMsg(msg);
		} catch (Exception e) {
			ConnectionPool.singleton.removeConnection(con);			
			e.printStackTrace();
			throw new ClientException("failed to send set key msg: " + host.getHostname() + ":" + e.getMessage());
		}
		
	}

	/**
	 * query remote nodes predecessor
	 */
	@Override
	public Node getPredecessor() throws ClientException {
		Message    msg = new Message(Commands.PREDECESSOR);
		Connection con = ConnectionPool.singleton.getConnection(host);
		Message    answer;
		
		try {
			answer = con.sendMsg(msg).get(2, TimeUnit.SECONDS);
		} catch (Exception e) {
			ConnectionPool.singleton.removeConnection(con);			
			e.printStackTrace();
			throw new ClientException("failed to send set key msg: " + host.getHostname() + ":" + e.getMessage());
		}
		
		if (answer.getParams().size() == 0) {
			throw new ClientException("no node as respond from find successor");
		}
		
		Node n = (Node)answer.getParams().get(0);
		
		// check null node msg
		if (n.getHost().getPort() == -1)
			return null;
		
		return n;
	}

	@Override
	public boolean ping() throws ClientException {
		Message    msg = new Message(Commands.PING);
		Connection con = ConnectionPool.singleton.getConnection(host);
		
		try {
			con.sendMsg(msg).get(2, TimeUnit.SECONDS);
		} catch (Exception e) {
			ConnectionPool.singleton.removeConnection(con);			
			e.printStackTrace();
			return false;
		}

		return true;
	}

	
	
}
