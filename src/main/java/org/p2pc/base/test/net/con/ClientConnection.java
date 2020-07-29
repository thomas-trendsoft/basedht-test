package org.p2pc.base.test.net.con;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

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
	private BaseDHTHandler handler;
	
	/**
	 * host info
	 */
	private Host host;
	
	/**
	 * default constructor 
	 * 
	 * @param channel
	 */
	public ClientConnection(Host host,ChannelFuture channel,BaseDHTHandler handler) {
		this.host    = host;
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
		
		handler.register(msg.getRequestId(), cf);
		ByteBuf buf = Unpooled.copiedBuffer(msg.serializeMsg());
		//System.out.println("client send: " + buf.readableBytes());
		channel.channel().writeAndFlush(buf);
		
		return cf;
	}


	@Override
	public Host getHost() {
		return host;
	}

	@Override
	public boolean isAlive() {
		return channel != null && !channel.isCancelled();
	}


}
