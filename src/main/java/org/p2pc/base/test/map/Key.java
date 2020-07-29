package org.p2pc.base.test.map;

import org.p2pc.base.test.net.ClientException;
import org.p2pc.base.test.net.con.protocol.Parameter;

/**
 * hash map key value representation
 * 
 * @author tkrieger
 *
 */
public class Key implements Parameter {
	
	public static final int size = 256;
	
	/**
	 * byte value
	 */
	public byte[] hash;
	
	/**
	 * internal key name
	 */
	public String name;
	
	/**
	 * default constructor 
	 * 
	 * @param hash
	 * @param name
	 */
	public Key(byte[] hash,String name) {
		this.hash = hash;
		this.name = name;
	}
	
	public boolean stabilizeInside(Key from, Key to) throws ClientException {
		
		// check if border gets passed
		if (from.compareTo(to) < 0) {
			return from.compareTo(this) > 0 && to.compareTo(this) < 0;
		}
		
		return this.compareTo(from) > 0 || this.compareTo(to) < 0;
		
	}	

	public boolean inside(Key from, Key to) throws ClientException {
		
		// bootstrap case (or conflict?)
		if (from.equals(to)) {
			return !this.equals(from);
		}
		
		// check if border gets passed
		if (from.compareTo(to) < 0) {
			return from.compareTo(this) > 0 && to.compareTo(this) <= 0;
		}
		
		return this.compareTo(from) > 0 || this.compareTo(to) <= 0;
		
	}

	@Override
	public byte[] getByteData() {
		return hash;
	}
	
	/**
	 * compare to keys
	 * 
	 * @param other
	 * @return
	 * @throws ClientException
	 */
	public boolean equals(Key other) throws ClientException {
		if (other.hash.length != this.hash.length) {
			throw new ClientException("uncomparable key length: " + other.hash.length);
		}
		
		for (int i=0;i<this.hash.length;i++) { 
			if (this.hash[i] != other.hash[i]) 
				return false; 
		}
		
		return true;		
	}
	
	/**
	 * create finger key for routing table
	 * 
	 * (copy from ref implementation
	 * 
	 * @param power
	 * @return
	 */
	public final Key addPower(int power) {

		if (power < 0 || power >= (hash.length * 8)) {
			throw new IllegalArgumentException(
					"The power of two is out of range! It must be in the interval "
							+ "[0, length-1]");
		}

		// copy ID
		byte[] copy = new byte[hash.length];
		System.arraycopy(hash, 0, copy, 0, hash.length);

		// determine index of byte and the value to be added
		int indexOfByte = hash.length - 1 - (power / 8);
		byte[] toAdd = { 1, 2, 4, 8, 16, 32, 64, -128 };
		byte valueToAdd = toAdd[power % 8];
		byte oldValue;

		do {
			// add value
			oldValue = copy[indexOfByte];
			copy[indexOfByte] += valueToAdd;

			// reset value to 1 for possible overflow situation
			valueToAdd = 1;
		}
		while (oldValue < 0 && copy[indexOfByte] >= 0 && indexOfByte-- > 0);

		return new Key(copy,this.name);
	}	
	
	/**
	 * compare key positions
	 * 
	 * @param other
	 * @return
	 * @throws ClientException
	 */
	public int compareTo(Key other) throws ClientException {
	
		if (other.hash.length != this.hash.length) {
			throw new ClientException("uncomparable key length: " + other.hash.length + "/" + this.hash.length);
		}
		
		for (int i=0;i<this.hash.length;i++) { 
			if ((byte) (this.hash[i] - 128) < (byte) (other.hash[i] - 128)) {
				return -1; 
			} else if ((byte) (this.hash[i] - 128) > (byte) (other.hash[i] - 128)) {
				return 1; // this ID is greater
			}
		}
		
		return 0;
	}
	
	@Override
	public String toString() {
	   StringBuilder sb = new StringBuilder(hash.length * 2);
	   for(byte b: hash)
	      sb.append(String.format("%02x", b));
	   return sb.toString();
	}
	
}
