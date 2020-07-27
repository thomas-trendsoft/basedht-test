package org.p2pc.base.test.net.con;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.p2pc.base.test.net.ClientException;
import org.p2pc.base.test.net.con.protocol.Message;
import org.p2pc.base.test.net.con.protocol.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * host info
	 */
	private Host host;
	
	/**
	 * logging interface
	 */
	private Logger log;
	
	/**
	 * default constructor 
	 * 
	 * @param channel
	 */
	public ClientConnection(Host host,ChannelFuture channel,ClientDHTHandler handler) {
		this.host    = host;
		this.channel = channel;
		this.handler = handler;
		this.log     = LoggerFactory.getLogger("ClientCon");
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
	 * handshake the client connection
	 * 
	 * @return
	 * @throws IOException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws ClientException 
	 */
	public void handshake() throws IOException, InterruptedException, ExecutionException, ClientException {
		CompletableFuture<Message> cf = new CompletableFuture<Message>();
		
		System.out.println("make handshake");
		Message hello = new Message(Message.HELLO);
		handler.register(hello.getRequestId(), cf);
		ByteBuf buf = Unpooled.copiedBuffer(hello.serializeMsg());
		channel.channel().writeAndFlush(buf);
		log.debug("handshake send...");
		Message answer = cf.get();
	
		if (!Arrays.equals(Message.WELCOME, answer.getMsg())) {
			throw new ClientException("unknown handshake answer: " + host.getHostname() + ":" + new String(answer.getMsg()));
		}
		
		// check and extract params
		if (answer.getParams().size() < 2) {
			throw new ClientException("insufficient handshake answer (expected 2 params): " + host.getHostname() + ":" + answer.getParams().size());			
		}
		
		Parameter pv = answer.getParams().get(0);
		Parameter pk = answer.getParams().get(1);
		
		
	}

}
