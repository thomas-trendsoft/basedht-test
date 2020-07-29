package org.p2pc.base.test.net.con;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.p2pc.base.test.net.con.protocol.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

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
	private Channel channel;
	
	/**
	 * protocol handler
	 */
	private BaseDHTHandler handler;
	
	/**
	 * host info
	 */
	private Host host;
	
	/**
	 * open flag
	 */
	private boolean open;
	
	/**
	 * default constructor 
	 * 
	 * @param channel
	 */
	public ClientConnection(Host host,Channel channel,BaseDHTHandler handler) {
		this.host    = host;
		this.channel = channel;
		this.handler = handler;
		this.open    = true;
		
		channel.closeFuture().addListener(con -> {
			System.out.println("close client con: " + con);
			open = false;
		});
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
		try {
			channel.writeAndFlush(buf).sync();
		} catch (InterruptedException e) {
			System.out.println("Error on send");
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		
		return cf;
	}


	@Override
	public Host getHost() {
		return host;
	}

	@Override
	public boolean isAlive() {
		return open && channel != null && channel.isOpen() && channel.isWritable();
	}

	@Override
	public void destroy() {
		try {
			channel.close().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
