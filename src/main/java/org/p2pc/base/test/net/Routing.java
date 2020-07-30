package org.p2pc.base.test.net;

import java.util.ArrayList;

import org.p2pc.base.test.map.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Routing {
	
	/**
	 * chord finger map
	 */
	private ArrayList<Node> fingers;
	
	/**
	 * ring links
	 */
	private Node predecessor;
	
	/**
	 * ring links
	 */
	private Node successor;
	
	/**
	 * logging interface
	 */
	private Logger log;
	
	/**
	 * default constructor 
	 */
	public Routing() {
		fingers     = new ArrayList<>();
		predecessor = null;
		successor   = null;
		
		log         = LoggerFactory.getLogger("Routing");
		
		for (int i=0;i<Key.size;i++) fingers.add(null);
	}

	/**
	 * look up closest node of key inside the finger links
	 * 
	 * @param key
	 * @return
	 * @throws ClientException 
	 */
	public Node closestPrecedingNode(Key key,Node start) throws ClientException {
		for (int i=0;i<fingers.size();i++) {
			Node n = fingers.get(i);
			if (n != null && n.getHost().getKey().inside(start.getHost().getKey(),key)) {
				return n;
			}
		}
		return start;
	}
	
	public synchronized void setFinger(int i,Node n) {
		int idx = fingers.indexOf(n);
		
		if (idx != -1)
			fingers.set(idx, null);
		
		log.info("update finger table: " + i + " -> " + n.getHost());
		fingers.set(i, n);
	}

	public Node getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(Node predecessor) {
		this.predecessor = predecessor;
		System.out.println(this.predecessor + " --> " + this.successor);
	}


	public Node getSuccessor() {
		return successor;
	}

	public void setSuccessor(Node successor) {
		this.successor = successor;
		System.out.println(this.predecessor + " --> " + this.successor);
	}
	
	

}
