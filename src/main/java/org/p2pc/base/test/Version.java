package org.p2pc.base.test;

import org.p2pc.base.test.net.con.protocol.Parameter;

public class Version implements Parameter {
	
	private byte[] version;
	
	public Version(byte[] v) {
		this.version = v;
	}

	@Override
	public byte[] getByteData() {
		return version;
	}

}
