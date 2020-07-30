package org.p2pc.base.test.net.con;

import java.util.concurrent.ConcurrentHashMap;

import org.p2pc.base.test.net.ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * p2p connection pool 
 * 
 * @author tkrieger
 *
 */
public class ConnectionPool {
	
	/**
	 * singleton connection pool
	 */
	public final static ConnectionPool singleton = new ConnectionPool();
	
	/**
	 * active connection map
	 */
	private ConcurrentHashMap<String, Connection> active;
	
	/**
	 * client thread pool
	 */
	private NioEventLoopGroup clientThreads;
	
	/**
	 * client protocol handler
	 */
	private BaseDHTHandler clientHandler;
		
	/**
	 * Request ID generator
	 */
	private static int reqid;
	
	/**
	 * Logging interface
	 */
	private Logger log;
	
	/**
	 * default constructor 
	 */
	private ConnectionPool() {
		reqid  = (int) (Math.random() * Integer.MAX_VALUE);
		active = new ConcurrentHashMap<>();
		log    = LoggerFactory.getLogger("Connections");
		
		// prepare client connections
		clientThreads   = new NioEventLoopGroup();
		clientHandler   = new BaseDHTHandler(true);
		
	}
	
	/**
	 * local node request id sequence
	 * 
	 * @return
	 */
	public synchronized static int getRequestId() {
		int id = reqid++;
		if (reqid == Integer.MAX_VALUE) reqid = 0;
		return id;
	}
	
	/**
	 * add available connection
	 * 
	 * @param host host key
	 * @param con open connection
	 */
	public void registerConnection(String url,Connection con) {
		active.put(url, con);
	}
	
	/**
	 * create or get a available host connection
	 * 
	 * @param host
	 * @throws ClientException 
	 */
	public Connection getConnection(Host host) throws ClientException {
		String lup = host.toString();
		
		Connection con = active.get(lup);
		if (con != null) {
			if (con.isAlive()) {
				return con;				
			} else {
				this.removeConnection(con);
			}
		}
		
		try {
			// try to create a new connection
			Bootstrap clientBootstrap = new Bootstrap();
			clientBootstrap.channel(NioSocketChannel.class);
			clientBootstrap.group(clientThreads);
			clientBootstrap.remoteAddress(host.getHostname(), host.getPort());
		    clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
		        protected void initChannel(SocketChannel socketChannel) throws Exception {
		            socketChannel.pipeline().addLast(clientHandler);
		        }
		    });
		    clientBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		    clientBootstrap.option(ChannelOption.AUTO_CLOSE, true);
		    
		    ChannelFuture cf;
			cf = clientBootstrap.connect().sync();
		    con = new ClientConnection(host,cf.channel(),clientHandler);

		    // handshake .. updates key and protocol version
		    clientHandler.protocol().handshake(con);
		    
		    // register connection
		    active.put(lup, con);
		    
		    return con;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClientException("unable to connecto to host: " + host.getHostname());
		}
		
	}

	public void removeConnection(Connection con) {
		log.info("remove connection: " + con.getHost());
		active.remove(con.getHost().toString());
		con.destroy();
	}

	public void closeAll() {
		log.info("shutdown connections...");
		for (Connection con : active.values()) {
			con.destroy();
		}
		
	}
	
}
