/**
 * 
 */
package com.cowinvest.lib.suffixTreeIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Jagjit 'Jay' Choudhary
 *
 */

class SuffixTreeIndexNode {
	SuffixTreeIndexNode() {
	}

	SuffixTreeIndexNode(int matchIndex) {
		addMatchIndex(matchIndex);		
	}

	// add a new edge (the edge has the char array and next node)
	void addChild (char childChar, SuffixTreeIndexEdge edge) {
		childNodes.put(childChar, edge);
	}
	
	SuffixTreeIndexEdge getChild (char childChar) {
		return childNodes.get(childChar);
	}
	
	void addMatchIndex(int matchIndex) {
		matchIndices.add(matchIndex);	
	}
	
	Iterator<Integer> getmatchIndexIterator() {
		return matchIndices.iterator();
	}
	
	int fillMatchIds(ArrayList<Integer> returnList, int maxEntries) {
		int numAdded = 0;
		Iterator<Integer> matchIdIterator = matchIndices.iterator();
		
		
		while (numAdded < maxEntries) {
		  if (matchIdIterator.hasNext()) {
			  returnList.add(matchIdIterator.next());
			  numAdded++;
		  }
		  else {
			  // there are no more entries in this index node
			  // look in the child nodes
			  break;
		  }
		}
		
		if (numAdded == maxEntries) {
			return numAdded;
		}
		
		// look in the child nodes
		Iterator<SuffixTreeIndexEdge> childNodeIterator = childNodes.values().iterator();
		
		while (numAdded < maxEntries) {
			if (childNodeIterator.hasNext()) {
				numAdded += childNodeIterator.next().getChild().fillMatchIds(returnList, maxEntries - numAdded);
			}
			else {
				// reached the end of nodes
				break;
			}
		}

		return numAdded;				
		
	}
	
	// private
	private HashMap<Character, SuffixTreeIndexEdge> childNodes = new HashMap<Character, SuffixTreeIndexEdge>();
	private HashSet<Integer> matchIndices = new HashSet<Integer>();
	
	
	
} // SuffixTreeIndexNode
