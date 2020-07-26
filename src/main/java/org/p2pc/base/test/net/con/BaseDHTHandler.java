package org.p2pc.base.test.net.con;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

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
	 * logging interface
	 */
	private Logger log;
	
	/**
	 * default constructor 
	 * 
	 * @param debug
	 */
	public BaseDHTHandler(boolean debug) {
		this.debug = debug;
		this.log   = LoggerFactory.getLogger("DHTProtocol");
	}
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf inBuffer = (ByteBuf) msg;

        String received = inBuffer.toString(CharsetUtil.UTF_8);
        System.out.println("Server received: " + received);

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
