package org.p2pc.base.test.net.con;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.p2pc.base.test.net.con.protocol.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

/**
 * server opened connection 
 * 
 * @author tkrieger
 *
 */
public class ServerConnection implements Connection {

	/**
	 * remote host
	 */
	private Host host;
	
	/**
	 * server connection context
	 */
	private ChannelHandlerContext context;
	
	/**
	 * protocol handler
	 */
	private BaseDHTHandler handler;
	
	/**
	 * default constructor 
	 * 
	 * @param h
	 * @param ctx
	 */
	public ServerConnection(Host h,ChannelHandlerContext ctx,BaseDHTHandler handle) {
		host    = h;
		context = ctx;
		handler = handle;
	}
	
	@Override
	public Host getHost() {
		return host;
	}

	@Override
	public CompletableFuture<Message> sendMsg(Message msg) throws IOException {
		CompletableFuture<Message> cf = new CompletableFuture<>();
		
		handler.register(msg.getRequestId(), cf);
		ByteBuf data = Unpooled.wrappedBuffer(msg.serializeMsg());
		context.writeAndFlush(data);
		
		return cf;
	}

}
