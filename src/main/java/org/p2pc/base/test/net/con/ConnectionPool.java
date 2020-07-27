package org.p2pc.base.test.net.con;

import java.util.concurrent.ConcurrentHashMap;

import org.p2pc.base.test.map.Key;
import org.p2pc.base.test.net.ClientException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
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
	private ConcurrentHashMap<Key, Connection> active;
	
	/**
	 * client thread pool
	 */
	private NioEventLoopGroup clientThreads;
	
	/**
	 * client protocol handler
	 */
	private ClientDHTHandler clientHandler;
		
	/**
	 * Request ID generator
	 */
	private static int reqid;
	
	/**
	 * default constructor 
	 */
	private ConnectionPool() {
		reqid  = (int) (Math.random() * Integer.MAX_VALUE);
		active = new ConcurrentHashMap<>();
		
		// prepare client connections
		clientThreads   = new NioEventLoopGroup();
		clientHandler   = new ClientDHTHandler(true);
		
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
	public void registerConnection(Key host,Connection con) {
		active.put(host, con);
	}
	
	/**
	 * create or get a available host connection
	 * 
	 * @param host
	 * @throws ClientException 
	 */
	public Connection getConnection(Host host) throws ClientException {
		
		if (host.getKey() != null && active.contains(host.getKey())) {
			return active.get(host.getKey());
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
		    
		    ChannelFuture cf;
			cf = clientBootstrap.connect().sync();
		    ClientConnection con = new ClientConnection(host,cf,clientHandler);

		    // handshake .. updates key and protocol version
		    con.handshake();
		    
		    // register connection
		    active.put(host.getKey(), con);
		    
		    return con;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClientException("unable to connecto to host: " + host.getHostname());
		}
		
	}
	
}
