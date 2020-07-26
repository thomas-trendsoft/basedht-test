package org.p2pc.base.test.net.con;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * client protocol handler
 * 
 * @author tkrieger
 *
 */
public class ClientDHTHandler extends ChannelInboundHandlerAdapter {

	/**
	 * debuging protocol available
	 */
	private boolean debug;
	
	/**
	 * logging interface
	 */
	private Logger log;
	
	/**
	 * open requests
	 */
	private ConcurrentHashMap<Integer, ClientConnection> open;
		
	/**
	 * default constructor 
	 * 
	 * @param debug
	 */
	public ClientDHTHandler(boolean debug) {
		this.debug = debug;
		this.log   = LoggerFactory.getLogger("DHTClient");
	}
	
	/**
	 * register request
	 * 
	 * @param rid
	 * @param con
	 */
	public void register(Integer rid, ClientConnection con) {
		open.put(rid, con);
	}
	
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf inBuffer = (ByteBuf) msg;

        String received = inBuffer.toString(CharsetUtil.UTF_8);
        System.out.println("Client received: " + received);

        ctx.write(Unpooled.copiedBuffer(received, CharsetUtil.UTF_8));
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
