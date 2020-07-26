package org.p2pc.base.test.net.con;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.p2pc.base.test.net.ServerException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * node server for handling the p2p connection 
 * 
 * @author tkrieger
 *
 */
public class NodeServer {
	
	/**
	 * port range start
	 */
	private int pstart;
	
	/**
	 * port range end
	 */
	private int pend;
	
	/**
	 * given local port
	 */
	private int localPort;
	
	/**
	 * channel future
	 */
	private ChannelFuture future;
	
	/**
	 * server
	 */
	private ServerBootstrap server;
	
	/**
	 * server thread pool
	 */
	private EventLoopGroup threadPool;
	
	
	/**
	 * default constructor 
	 */
	public NodeServer() {
		pstart = 40000;
		pend   = 65300;
	}
		
	/**
	 * start the server process
	 * 
	 * @throws InterruptedException
	 * @throws ServerException 
	 */
	public void start() throws InterruptedException, ServerException {

		// check available port
		localPort = -1;
		for (int i=pstart;i<pend;i++) {
			if (available(i)) {
				localPort = i;
				break;
			}
		}
		
		if (localPort == -1) {
			throw new ServerException("no available server port found.");
		}
		
		try{
			threadPool = new NioEventLoopGroup();

			server = new ServerBootstrap();
		    server.group(threadPool);
		    server.channel(NioServerSocketChannel.class);
		    server.localAddress(new InetSocketAddress("localhost", localPort));

		    server.childHandler(new ChannelInitializer<SocketChannel>() {
		        protected void initChannel(SocketChannel socketChannel) throws Exception {
		            socketChannel.pipeline().addLast(new BaseDHTHandler(true));
		        }
		    });
		    
		    future = server.bind().sync();
		    future.channel().closeFuture().sync();
		} catch(Exception e){
		    e.printStackTrace();
		} finally {
		    threadPool.shutdownGracefully().sync();
		}		
	}
	
	/**
	 * util method for open port check
	 * 
	 * @param port
	 * @return
	 */
	private boolean available(int port) {
	    try (Socket ignored = new Socket("localhost", port)) {
	        return false;
	    } catch (IOException ignored) {
	        return true;
	    }
	}	
	

}
