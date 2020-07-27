package org.p2pc.base.test.net.con;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.net.con.protocol.Message;
import org.p2pc.base.test.net.con.protocol.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * server base dht protocol handler
 * 
 * @author tkrieger
 *
 */
public class BaseDHTHandler extends ChannelInboundHandlerAdapter {
    
	/**
	 * debuging protocol available
	 */
	private boolean debug;
	
	/**
	 * msg parser
	 */
	private MessageFactory parser;
	
	/**
	 * logging interface
	 */
	private Logger log;
	
	/**
	 * open requests
	 */
	private ConcurrentHashMap<Integer, CompletableFuture<Message>> open;	
	
	/**
	 * default constructor 
	 * 
	 * TODO check key init
	 * 
	 * @param debug
	 */
	public BaseDHTHandler(boolean debug) {
		this.debug  = debug;
		this.parser = MessageFactory.singleton;
		this.open   = new ConcurrentHashMap<>();
		this.log    = LoggerFactory.getLogger("DHTServer");
	}
	
	/**
	 * register request
	 * 
	 * @param rid
	 * @param con
	 */
	public void register(Integer rid, CompletableFuture<Message> con) {
		open.put(rid, con);
	}
	
	private void sendMsg(ChannelHandlerContext ctx,Message m) {
		ByteBuf buf;
		try {
			buf = Unpooled.wrappedBuffer(m.serializeMsg());
			ctx.channel().writeAndFlush(buf);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("failed to send message: " + e.getMessage());
		}
	}
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf inBuffer = (ByteBuf) msg;

        try {
            Message m = parser.parseMessage(inBuffer);
            
            switch (m.getMsg()) {
            case PING:
            	break;
            case HELLO:
            	sendMsg(ctx, parser.welcome(m.getRequestId()));
            	return;
            case FINDSUCCESSOR:
            	break;
            }
            
            CompletableFuture<Message> cf = open.get(m.getRequestId());
            if (cf == null) {
            	log.warn("unexpected message: " + m.getRequestId() + " / " + m.getMsg());
            } else {
            	log.info("got request: " + m.getRequestId());
            	cf.complete(m);
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
}
