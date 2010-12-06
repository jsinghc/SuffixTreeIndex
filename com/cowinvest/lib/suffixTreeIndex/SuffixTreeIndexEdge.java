/**
 * 
 */
package com.cowinvest.lib.suffixTreeIndex;

/**
 * @author Jagjit 'Jay' Choudhary
 *
 */
public class SuffixTreeIndexEdge {

	// constructor to build an index edge
	SuffixTreeIndexEdge (String edgeText, SuffixTreeIndexNode childNode) {
		this.text       = edgeText;
		this.childNode  = childNode;
	}
	
	SuffixTreeIndexEdge () {
		this.text       = null;
		this.childNode  = null;		
	}
	
	SuffixTreeIndexNode getChild () {
		return childNode;
	}
	
	void setChild (SuffixTreeIndexNode childNode, String text) {
		this.childNode  = childNode;		
		this.text       = text;
	}
	
	char charAt (int index) {
		return text.charAt(index);
	}

	int length () {
		return text.length();
	}
	
	void setText (String text) {
		this.text = text;
	}
	
	String getText () {
		return this.text;
	}
	
	// check if the edge is a new leaf
	// the new leaves are only in existence while a text is being added
	boolean isNewLeaf () {
		return (childNode == null);
	}
	
	
	// private
	private String text;
	private SuffixTreeIndexNode childNode;

} //SuffixTreeIndexEdge
