package org.p2pc.base.test.net.con.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

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
	 * local key
	 */
	private Key key;

	/**
	 * local version
	 */
	private BaseParameter version;
	
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
	
	public void setKey(Key own) {
		this.key = own;
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
		m.addParam(key);
		
		return m;
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
		} while (r != 0 && c < 15);
		
		String scmd = new String(buf.toByteArray(),CharsetUtil.UTF_8);
		Commands cmd = cmdLookup.get(scmd);
		
		// check command
		if (cmd == null) {
			throw new ClientException("unknown command: " + scmd);
		} 
		
		buf.close();
		
		Message m = new Message(rid,cmd);
		
		return m;
		
	}

}
