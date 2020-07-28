package org.p2pc.base.test.net;

import java.util.ArrayList;

import org.p2pc.base.test.map.Key;

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
	 * default constructor 
	 */
	public Routing() {
		fingers     = new ArrayList<>();
		predecessor = null;
		successor   = null;
	}

	/**
	 * look up closest node of key inside the finger links
	 * 
	 * @param key
	 * @return
	 * @throws ClientException 
	 */
	public Node closestPrecedingNode(Key key,Node start) throws ClientException {
		for (int i=fingers.size()-1;i>=0;i--) {
			Node n = fingers.get(i);
			if (n != null && n.key.inside(start.key,key)) {
				return n;
			}
		}
		return start;
	}

	public Node getPredecessor() {
		return predecessor;
	}


	public void setPredecessor(Node predecessor) {
		this.predecessor = predecessor;
	}


	public Node getSuccessor() {
		return successor;
	}

	public void setSuccessor(Node successor) {
		this.successor = successor;
	}
	
	

}
