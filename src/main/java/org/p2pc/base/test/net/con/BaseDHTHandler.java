package org.p2pc.base.test.net.con;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.p2pc.base.test.net.Node;
import org.p2pc.base.test.net.con.protocol.BaseDHTProtocol;
import org.p2pc.base.test.net.con.protocol.Message;
import org.p2pc.base.test.net.con.protocol.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * server base dht protocol handler
 * 
 * @author tkrieger
 *
 */
@Sharable
public class BaseDHTHandler extends ChannelInboundHandlerAdapter {
    
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
	 * protocol implementation
	 */
	private BaseDHTProtocol protocol;
	
	/**
	 * default constructor 
	 * 
	 * TODO check key init
	 * 
	 * @param debug
	 */
	public BaseDHTHandler(boolean debug) {
		this.parser   = MessageFactory.singleton;
		this.open     = new ConcurrentHashMap<>();
		this.log      = LoggerFactory.getLogger("DHTServer");
		this.protocol = BaseDHTProtocol.singleton;
	}
	
	/**
	 * protocol handler 
	 * 
	 * @return
	 */
	public BaseDHTProtocol protocol() {
		return protocol;
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
	
	/**
	 * send a p2p message 
	 * 
	 * @param ctx
	 * @param m
	 */
	private void sendMsg(ChannelHandlerContext ctx,Message m) {
		ByteBuf buf;
		try {
			buf = Unpooled.wrappedBuffer(m.serializeMsg());
			//System.out.println("send: " + m.getMsg() + ":" + buf.readableBytes());
			ctx.channel().writeAndFlush(buf);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("failed to send message: " + e.getMessage());
		}
	}
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf inBuffer = (ByteBuf) msg;

        // TODO add connection as server connection to pool (how to host)
        
        //System.out.println("recv: " + inBuffer.readableBytes());
        try {
            Message m = parser.parseMessage(inBuffer);
            
            switch (m.getMsg()) {
            case PING:
            	sendMsg(ctx, protocol.ping(m));
            	break;
            case HELLO:
            	Node ocn = (Node) m.getParams().get(1);
            	ConnectionPool.singleton.registerConnection(ocn.getHost().toString(), new ServerConnection(ocn.getHost(), ctx.channel(), this));
            	sendMsg(ctx, protocol.hello(m));
            	return;
            case FINDSUCCESSOR: 
            	sendMsg(ctx, protocol.findSuccessor(m));
            	return;
            case PREDECESSOR:
            	sendMsg(ctx, protocol.predecessor(m));
            	return;
            case NOTIFY:
            	protocol.notifyNode(m);
            	return;
            default:
            	// check if expected request
                CompletableFuture<Message> cf = open.get(m.getRequestId());
                if (cf == null) {
                	log.warn("unexpected message: " + m.getRequestId() + " / " + m.getMsg());
                } else {
                	cf.complete(m);
                }
            	break;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	System.out.println("con exception");
        cause.printStackTrace();
        ctx.close();
    }
    
}
