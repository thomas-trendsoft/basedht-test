package org.p2pc.base.test.net.con.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.p2pc.base.test.Version;
import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.net.ClientException;
import org.p2pc.base.test.net.Node;
import org.p2pc.base.test.net.RemoteNode;
import org.p2pc.base.test.net.con.Host;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

/**
 * basic message parser
 * 
 * @author tkrieger
 *
 */
public class MessageFactory {
	
	/**
	 * singleton
	 */
	public final static MessageFactory singleton = new MessageFactory();
	
	/**
	 * command lookup table
	 */
	private HashMap<String,Commands> cmdLookup;
	
	/**
	 * local version
	 */
	private BaseParameter version;
	
	/**
	 * local config
	 */
	private Node node;
	
	/**
	 * default constructor 
	 */
	private MessageFactory() {
		// lookup map
		cmdLookup = new HashMap<>();
		cmdLookup.put(Message.GET,           Commands.GET);
		cmdLookup.put(Message.SET,           Commands.SET);
		cmdLookup.put(Message.DONE,          Commands.DONE);
		cmdLookup.put(Message.VALUE,         Commands.VALUE);
		cmdLookup.put(Message.HELLO,         Commands.HELLO);
		cmdLookup.put(Message.WELCOME,       Commands.WELCOME);
		cmdLookup.put(Message.FINDSUCCESSOR, Commands.FINDSUCCESSOR);
		cmdLookup.put(Message.SUCCESSORFIND, Commands.SUCCESSORFIND);
		cmdLookup.put(Message.PING,          Commands.PING);
		cmdLookup.put(Message.PONG,          Commands.PONG);
		cmdLookup.put(Message.PREDECESSOR,   Commands.PREDECESSOR);
		cmdLookup.put(Message.PREDANSWER,    Commands.PREDANSWER);
		cmdLookup.put(Message.NOTIFY,        Commands.NOTIFY);
		
		version = new BaseParameter(Message.VERSION);
	}
	
	/**
	 * set local node ref
	 * 
	 * @param base
	 */
	public void setNode(Node base) {
		this.node = base;
	}
	
	/**
	 * read a fix amount of bytes
	 * 
	 * @param len
	 * @param buf
	 * @return
	 * @throws ClientException
	 */
	private byte[] readArray(int len,ByteBuf buf) throws ClientException {
		byte[] b = new byte[len];
		
		int  c = 0;
		byte r;
		while ((r = buf.readByte()) != -1 && c < (len-1)) {
			b[c] = r;
			c++;
		}
		
		c++;
		if (len != c) throw new ClientException("unexpoected eof on msg read");
		
		return b;
	}
	
	/**
	 * read a string from the data stream
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	private String readString(ByteBuf data) throws IOException {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		byte   r;
		
		int c=0;
		do {
			r = data.readByte();
			buf.write(r);
			c++;
		} while (r != 0 && c < 20);
		buf.close();
		
		return new String(buf.toByteArray(),CharsetUtil.UTF_8);		
	}
	
	/**
	 * parse a network message
	 * 
	 * @param data
	 * @return
	 * @throws ClientException
	 * @throws IOException
	 */
	public Message parseMessage(ByteBuf data) throws ClientException, IOException {
		int rid = data.readInt();
		
		String  scmd = readString(data);
		Commands cmd = cmdLookup.get(scmd);
		
		// check command
		if (cmd == null) {
			throw new ClientException("unknown command: " + scmd);
		} 

		Message m = new Message(rid,cmd);
		switch (cmd) {
		case PREDECESSOR:
		case PING:
		case PONG:
			// no params
			break;
		case HELLO:
			m.addParam(new Version(readArray(4, data)));
			m.addParam(parseNode(data));
			break;
		case WELCOME:
			m.addParam(new Version(readArray(4, data)));
			m.addParam(new Key(readArray(32, data), "key"));
			break;
		case FINDSUCCESSOR:
			m.addParam(new Key(readArray(32, data), "key"));
			break;
		case SUCCESSORFIND:
			m.addParam(parseNode(data));
			break;
		case PREDANSWER:
			m.addParam(parseNode(data));
			break;
		case NOTIFY:
			m.addParam(parseNode(data));
			break;
		default:
			throw new ClientException("missing implement message command: " + scmd);
		}
		
		return m;
		
	}

	/**
	 * parse a node parameter
	 * 
	 * @param data
	 * @return
	 * @throws ClientException
	 * @throws IOException
	 */
	private Node parseNode(ByteBuf data) throws ClientException, IOException {
		Key key     = new Key(readArray(32, data),"key");
		int port    = data.readInt();
		String host = readString(data);

		// check if its me 
		if (key.equals(node.getHost().getKey()) && port == node.getHost().getPort()) {
			System.out.println("got local node as response");
			return node;
		} 
		
		return new RemoteNode(new Host(host, port, key));
	}

	/**
	 * generate a hello msg
	 * 
	 * @return
	 */
	public Message hello() {
		Message hello = new Message(Commands.HELLO);
		
		hello.addParam(version);
		hello.addParam(node);
		
		return hello;
	}
	
	/**
	 * generate welcome answer 
	 * 
	 * @param rid
	 * @return
	 */
	public Message welcome(int rid) {
		Message m = new Message(rid,Commands.WELCOME);
		
		m.addParam(version);
		m.addParam(node.getHost().getKey());
		
		return m;
	}
	
	

}
