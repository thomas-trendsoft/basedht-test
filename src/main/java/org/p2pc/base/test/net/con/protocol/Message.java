package org.p2pc.base.test.net.con.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
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
	
	public final static byte[] VERSION = {0x00,0x00,0x00,0x01};

	public final static String GET           = "GET\0";
	public final static String SET           = "SET\0";
	public final static String DONE          = "DONE\0";
	public final static String VALUE         = "VAL\0";
	public static final String HELLO         = "HELLOP2PC\0";
	public static final String WELCOME       = "CP2P\0";
	public static final String FINDSUCCESSOR = "FINDSUCC\0";
	public static final String SUCCESSORFIND = "SUCCESSF\0";
	public static final String PING         = "PI\0";
	public static final String PONG         = "PO\0";
	
	
	public static final byte[][] msgLookup = {GET.getBytes(CharsetUtil.UTF_8),
			SET.getBytes(CharsetUtil.UTF_8),DONE.getBytes(CharsetUtil.UTF_8),
			VALUE.getBytes(CharsetUtil.UTF_8),HELLO.getBytes(CharsetUtil.UTF_8),
			WELCOME.getBytes(CharsetUtil.UTF_8),FINDSUCCESSOR.getBytes(CharsetUtil.UTF_8),
			SUCCESSORFIND.getBytes(CharsetUtil.UTF_8),PING.getBytes(CharsetUtil.UTF_8),
			PONG.getBytes(CharsetUtil.UTF_8)};
	
	/**
	 * request id
	 */
	private int reqid;
	
	/**
	 * command or response msg
	 */
	private Commands cmd;
	
	/**
	 * message parameter values
	 */
	private List<Parameter> params;
	
	/**
	 * default constructor 
	 * 
	 * @param msg
	 */
	public Message(Commands cmd) {
		this.reqid  = ConnectionPool.getRequestId();
		this.cmd    = cmd;
		this.params = new LinkedList<>();
	}
	
	/**
	 * parse constructor 
	 * 
	 * @param rid
	 * @param msg
	 */
	public Message(int rid,Commands cmd) {
		this.reqid  = rid;
		this.cmd    = cmd;
		this.params = new LinkedList<>();		
	}
	
	public Commands getMsg() {
		return cmd;
	}

	public List<Parameter> getParams() {
		return params;
	}

	public void setParams(List<Parameter> params) {
		this.params = params;
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
		puf.write(msgLookup[cmd.ordinal()]);
		for (Parameter p : params) {
			System.out.println(p);
			puf.write(p.getByteData());			
		}
		
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
