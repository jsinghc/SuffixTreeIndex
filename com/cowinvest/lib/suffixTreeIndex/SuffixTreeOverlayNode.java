/**
 * 
 */
package com.cowinvest.lib.suffixTreeIndex;

import java.util.HashMap;

/**
 * @author Jagjit 'Jay' Choudhary
 *
 */
class SuffixTreeOverlayNode {
	SuffixTreeOverlayNode(SuffixTreeIndexNode   stIndexNode) {
		childNodes = new HashMap<Character, SuffixTreeOverlayEdge>();
		suffixLink = null;
		this.stIndexNode = stIndexNode;
	}
		
	// add a new edge (the edge has startIndex, endIndex and next node)
	// adding an initialized edge is equivalent to adding the transition g'(s, (k,x)) = r
	void addChild (char childChar, SuffixTreeOverlayEdge edge) {
		childNodes.put(childChar, edge);
	}
	
	SuffixTreeOverlayEdge getChild (char childChar) {
		return childNodes.get(childChar);
	}
	
	// Set the suffix link ()
	void setSuffixLink (SuffixTreeOverlayNode suffixLink) {
		this.suffixLink = suffixLink;
	}
	
	SuffixTreeOverlayNode getSuffixLink () {
		return suffixLink;
	}
	
	SuffixTreeIndexNode getStIndexNode () {
		return stIndexNode;
	}
	
	boolean isLeaf() {
	    return childNodes.isEmpty();
	}
	
	// private
	private HashMap<Character, SuffixTreeOverlayEdge> childNodes;
	
	private SuffixTreeOverlayNode suffixLink;
	
	private SuffixTreeIndexNode   stIndexNode;
	

} // class SuffixTreeNode
