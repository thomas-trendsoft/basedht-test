package org.p2pc.base.test.net.con.protocol;

import io.netty.buffer.ByteBuf;

public class BaseParameter implements Parameter {

	private byte[] data;
	
	public BaseParameter(byte[] data) {
		this.data = data;
	}
	
	@Override
	public byte[] getByteData() {
		return data;
	}


}
