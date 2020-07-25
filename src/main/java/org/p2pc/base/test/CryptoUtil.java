package org.p2pc.base.test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.p2pc.base.test.map.Key;

/**
 * basic hash, crypt and sign util
 * 
 * @author tkrieger
 *
 */
public class CryptoUtil {

	/***
	 * create a random key (for node init)
	 * 
	 * @param name
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static Key createRandomKey(String name) throws NoSuchAlgorithmException {
		String iv = UUID.randomUUID().toString();
		
		byte[] kv = hashData((iv + name).getBytes());
		
		return new Key(kv,name);
	}
	
	/**
	 * simple hash a small byte amount
	 * 
	 * @param data
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] hashData(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest hash = MessageDigest.getInstance("SHA-256");
		
		hash.update(data);
		
		return hash.digest();
	}
	
}
