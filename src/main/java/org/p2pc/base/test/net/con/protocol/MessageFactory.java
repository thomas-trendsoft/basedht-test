package org.p2pc.base.test.net.con.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.p2pc.base.test.NodeConfig;
import org.p2pc.base.test.Version;
import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.net.ClientException;

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
	private NodeConfig config;
	
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
		
		version = new BaseParameter(Message.VERSION);
	}
	
	public void setConfig(NodeConfig cfg) {
		this.config = cfg;
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
		while ((r = buf.readByte()) != -1 && c < len) {
			b[0] = r;
			c++;
		}
		
		if (len != c) throw new ClientException("unexpoected eof on msg read");
		
		return b;
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
		int c;
		int rid = data.readInt();
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		System.out.println("rid : " + rid);
		
		byte   r;
		
		c=0;
		do {
			r = data.readByte();
			buf.write(r);
			c++;
		} while (r != 0 && c < 15);
		buf.close();
		
		System.out.println("c = " + c);
		String scmd = new String(buf.toByteArray(),CharsetUtil.UTF_8);
		Commands cmd = cmdLookup.get(scmd);
		
		// check command
		if (cmd == null) {
			throw new ClientException("unknown command: " + scmd);
		} 

		Message m = new Message(rid,cmd);
		switch (cmd) {
		case HELLO:
			m.addParam(new Version(readArray(4, data)));
			m.addParam(new Key(readArray(4, data), "key"));
			break;
		case WELCOME:
			m.addParam(new Version(readArray(4, data)));
			m.addParam(new Key(readArray(4, data), "key"));
			break;
		default:
			throw new ClientException("missing implement message command: " + scmd);
		}
		
		
		
		return m;
		
	}

	/**
	 * generate a hello msg
	 * 
	 * @return
	 */
	public Message hello() {
		Message hello = new Message(Commands.HELLO);
		
		hello.addParam(version);
		hello.addParam(config.key);
		
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
		m.addParam(config.key);
		
		return m;
	}
	
	

}
