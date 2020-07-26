package org.p2pc.base.test.net.con;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.net.con.protocol.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;

/**
 * Client connection implementation
 * 
 * @author tkrieger
 *
 */
public class ClientConnection implements Connection {

	/**
	 * netty connection channel
	 */
	private ChannelFuture channel;
	
	/**
	 * protocol handler
	 */
	private ClientDHTHandler handler;
	
	/**
	 * default constructor 
	 * 
	 * @param channel
	 */
	public ClientConnection(ChannelFuture channel,ClientDHTHandler handler) {
		this.channel = channel;
		this.handler = handler;
	}
	
	/**
	 * send a new message to the channel
	 * @throws IOException 
	 */
	@Override
	public CompletableFuture<Message> sendMsg(Message msg) throws IOException {
		CompletableFuture<Message> cf = new CompletableFuture<Message>();
		
		handler.register(msg.getRequestId(), this);
		ByteBuf buf = Unpooled.copiedBuffer(msg.serializeMsg());
		channel.channel().write(buf);
		
		return cf;
	}
	
	/**
	 * async message receiver 
	 * 
	 * @param msg
	 */
	public void receiveMsg(Message msg) {
		
	}

	/**
	 * 
	 * @return
	 */
	public Key handshake() {
		return null;
	}

}
