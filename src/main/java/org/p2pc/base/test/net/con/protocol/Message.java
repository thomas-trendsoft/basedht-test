package org.p2pc.base.test.net.con.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.p2pc.base.test.net.con.ConnectionPool;

import io.netty.util.CharsetUtil;

/**
 * protocol message 
 * 
 * @author tkrieger
 *
 */
public class Message {
	
	public final static String GET   = "GET";
	public final static String SET   = "SET";
	public final static String DONE  = "DONE";
	public final static String VALUE = "VAL";
	
	/**
	 * request id
	 */
	private int reqid;
	
	/**
	 * command or response msg
	 */
	private String msg;
	
	/**
	 * message parameter values
	 */
	private List<Parameter> params;
	
	/**
	 * default constructor 
	 * 
	 * @param msg
	 */
	public Message(String msg) {
		this.reqid = ConnectionPool.getRequestId();
		this.msg   = msg;
	}
	
	/**
	 * add a parameter value to the message
	 * 
	 * @param p
	 */
	public void addParam(Parameter p) {
		params.add(p);
	}
	
	/**
	 * help method to convert req id
	 * 
	 * @param i
	 * @return
	 */
	protected byte[] toBytes(int i)
	{
	  byte[] result = new byte[4];

	  result[0] = (byte) (i >> 24);
	  result[1] = (byte) (i >> 16);
	  result[2] = (byte) (i >> 8);
	  result[3] = (byte) (i /*>> 0*/);

	  return result;
	}
	
	/**
	 * create byte stream from msg
	 * 
	 * @return byte array representing the message 
	 * 
	 * @throws IOException 
	 */
	public byte[] serializeMsg() throws IOException {
		ByteArrayOutputStream puf = new ByteArrayOutputStream();
		
		puf.write(toBytes(reqid));
		puf.write(msg.getBytes(CharsetUtil.UTF_8));
		for (Parameter p : params) {
			puf.write(p.getByteData());			
		}
		puf.write(0x00);
		
		byte[] data = puf.toByteArray();
		puf.close();
		
		return data;
	}

	/**
	 * get request id
	 * 
	 * @return
	 */
	public Integer getRequestId() {
		return this.reqid;
	}

}
