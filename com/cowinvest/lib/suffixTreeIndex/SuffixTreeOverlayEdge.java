/**
 * 
 */
package com.cowinvest.lib.suffixTreeIndex;

/**
 * @author Jagjit 'Jay' Choudhary
 *
 */
class SuffixTreeOverlayEdge {
	// constructor to build a leaf edge (which will eventually point to a leafnode)
	SuffixTreeOverlayEdge (int startIndex, SuffixTreeIndexEdge stIndexEdge) {
		this.startIndex = startIndex; 
		this.endIndex   = -1;
		this.childNode  = null;
		this.stIndexEdge  = stIndexEdge;
	}
	
	// constructor to build a branch edge (which points to a branch node)
	SuffixTreeOverlayEdge (int                   startIndex, 
			               int                   endIndex, 
			               SuffixTreeOverlayNode childNode, 
			               SuffixTreeIndexEdge   stIndexEdge) {
		this.startIndex = startIndex;
		this.endIndex   = endIndex;
		this.childNode  = childNode;
		this.stIndexEdge  = stIndexEdge;
	}
	
	boolean isLeaf () {
		return (endIndex < 0);
	}
	
	boolean isNewLeaf () {
		return (this.isLeaf() && (stIndexEdge.isNewLeaf()));
	}
	
	boolean isDiscoveryLeaf () {
		return (this.isLeaf() && (!stIndexEdge.isNewLeaf()));
	}
	
	int getStartIndex() {
		return startIndex;
	}
	
	void setStartIndex( int startIndex) {
		this.startIndex = startIndex;
	}

	int getEndIndex() {
		return endIndex;
	}
	
	void setEndIndex (int endIndex) {
		this.endIndex = endIndex;
	}

	SuffixTreeOverlayNode getChild () {
		return childNode;
	}
	
	void setChild (SuffixTreeOverlayNode childNode, int endIndex) {
		this.childNode = childNode;
		this.endIndex  = endIndex;		
	}
	
	SuffixTreeIndexEdge getStIndexEdge() {
		return stIndexEdge;
	}
	
	// private
	private int startIndex;
	private int endIndex;
	private SuffixTreeOverlayNode childNode;
	private SuffixTreeIndexEdge stIndexEdge;

} // class SuffixTreeOverlayEdge
