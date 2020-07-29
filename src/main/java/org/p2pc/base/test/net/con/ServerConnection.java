package org.p2pc.base.test.net.con;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.p2pc.base.test.net.con.protocol.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
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
	private Channel context;
	
	/**
	 * protocol handler
	 */
	private BaseDHTHandler handler;
	
	/**
	 * open falg
	 */
	private boolean open;
	
	/**
	 * default constructor 
	 * 
	 * @param h
	 * @param ctx
	 */
	public ServerConnection(Host h,Channel ctx,BaseDHTHandler handle) {
		host    = h;
		context = ctx;
		handler = handle;
		
		context.closeFuture().addListener(con -> {
			System.out.println("close a server connection");
			open = false;
		});
	}
	
	@Override
	public Host getHost() {
		return host;
	}

	@Override
	public CompletableFuture<Message> sendMsg(Message msg) throws IOException {
		CompletableFuture<Message> cf = new CompletableFuture<>();
		
		handler.register(msg.getRequestId(), cf);
		ByteBuf data = Unpooled.copiedBuffer(msg.serializeMsg());
		context.writeAndFlush(data);

		return cf;
	}

	@Override
	public boolean isAlive() {
		System.out.println("isactive: " + context.isActive());
		return open && context.isActive();
	}

	@Override
	public void destroy() {
		try {
			context.close().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
