/**
 * 
 */
package com.cowinvest.lib.suffixTreeIndex;

import java.util.*;

/**
 * @author Jagjit 'Jay' Choudhary
 * 
 */
public class SuffixTreeIndex {

    public SuffixTreeIndex() {
        root = new SuffixTreeIndexNode();
    }

    // Update the tree with new text
    // it is done with the help of overlay suffix tree
    // which is like a suffix tree for a single text, but modified
    // to overlay over a suffix tree index and update the index as needed
    public void addText(String text, int matchIndex) {
        new SuffixTreeOverlay(text, matchIndex);
    }

    // method to search for a string in the text
    // the text is represented by the suffix tree
    // the method returns the list of matched ids which
    // have text matching the string
    public ArrayList<Integer> search(String searchText) {
        SuffixTreeIndexNode searchNode = root;
        int edgeIndex = 0;
        int length = searchText.length();
        int searchIndex = 0;
        SuffixTreeIndexEdge edge = null;
        boolean match = true;
        ArrayList<Integer> returnList = new ArrayList<Integer>();

        while (searchIndex < length) {

            // if edge is null check if the node has an edge starting with char
            // at searchIndex
            if (edge == null) {
                edge = searchNode.getChild(searchText.charAt(searchIndex));
                if (edge == null) {
                    match = false;
                    break;
                }
                edgeIndex = 0;
            } else {
                // check if the next character matches
                if (searchText.charAt(searchIndex) != edge.charAt(edgeIndex)) {
                    match = false;
                    break;
                }
            }
            // if the edge index has reached the end advance to the next branch
            // node
            if (edgeIndex == (edge.length() - 1)) {
                searchNode = edge.getChild();
                edge = null;
            }

            // control comes here if the char has matched
            searchIndex++;
            edgeIndex++;
        }

        if (match) {
            if (edge != null) {
                searchNode = edge.getChild();
            }
            searchNode.fillMatchIds(returnList, 100);
        }
        return returnList;
    }

    // private
    private SuffixTreeIndexNode root;

    // overlay suffix tree
    // it is like a suffix tree for a single text, but modified
    // to overlay over the suffix tree index and update the index when needed
    private class SuffixTreeOverlay {

        SuffixTreeOverlay(String text, int matchIndex) {
            root = new SuffixTreeOverlayNode(SuffixTreeIndex.this.root);
            this.text = text;
            this.matchIndex = matchIndex;
            newLeafEdges = new LinkedList<SuffixTreeOverlayEdge>();

            build();
        }

        public void build() {
            SKTuple sk = new SKTuple(root, 1);
            int length = text.length();

            for (int i = 1; i <= length; i++) {
                // update the overlay tree and the underlying suffix tree index
                // with the character at i-th pos
                // also update the s state and k index for the active point
                update(sk, i);
                // update s,k with the canonical reference point of s,k,i
                canonize(sk, i);
            }

            addMatchIds(sk, length);
            
            // Create the text for edges in the underlying index for all the new
            // leaf edges
            ListIterator<SuffixTreeOverlayEdge> leafIterator = newLeafEdges.listIterator(0);
            while (leafIterator.hasNext()) {
                SuffixTreeOverlayEdge leafEdge = leafIterator.next();

                // set the text for the sister stindexedge
                leafEdge.getStIndexEdge().setChild(new SuffixTreeIndexNode(matchIndex), 
                                                   text.substring(leafEdge.getStartIndex() - 1, length));
            }
        }

        private class SKTuple {
            int kIndex;
            SuffixTreeOverlayNode sState;

            SKTuple(SuffixTreeOverlayNode sState, int kIndex) {
                this.kIndex = kIndex;
                this.sState = sState;
            }
        }

        private class TestAndSplitReturn {
            boolean endPointReached;
            SuffixTreeOverlayNode rState;

            TestAndSplitReturn(boolean endPointReached, SuffixTreeOverlayNode rState) {
                this.endPointReached = endPointReached;
                this.rState = rState;
            }
        }

        // the method implementing the modified version of
        // update procedure of Ukkonen's SuffixTree algorithm
        private void update(SKTuple sk, int i) {
            SuffixTreeOverlayNode oldr = root;
            TestAndSplitReturn testAndSplitReturn;

            testAndSplitReturn = testAndSplit(sk, i - 1, text.charAt(i - 1));

            while (!testAndSplitReturn.endPointReached) {
                // create new transition from the state r
                SuffixTreeIndexEdge newIndexEdge = new SuffixTreeIndexEdge();
                SuffixTreeOverlayEdge newOverlayEdge = new SuffixTreeOverlayEdge(i, newIndexEdge);

                newLeafEdges.add(newOverlayEdge);
                testAndSplitReturn.rState.getStIndexNode().addChild(text.charAt(i - 1), newIndexEdge);
                testAndSplitReturn.rState.addChild(text.charAt(i - 1), newOverlayEdge);

                // oldr is not root add a suffixlink to the state r
                if (oldr != root) {
                    oldr.setSuffixLink(testAndSplitReturn.rState);
                }

                oldr = testAndSplitReturn.rState;

                // in this implementation, we do not have a separate node for
                // state _|_
                // the check for root state tells when to stop following the
                // suffix links.
                if (sk.sState == root) {
                    // if k <= i-1, then canonize of the suffixlink of
                    // root,k,i-1 results
                    // in s <-- root and k <-- k+1
                    if (sk.kIndex <= i - 1) {
                        sk.kIndex++;
                    }
                    // if k > i-1, then canonize leads to _|_ and susequent
                    // canonize of _|_,k,i leads to
                    // s <-- root, k <-- k+1 for next i <-- i+1
                    // so we increment k and break
                    else {
                        sk.kIndex++;
                        break;
                    }
                } else {
                    sk.sState = sk.sState.getSuffixLink();
                    canonize(sk, i - 1);
                }
                testAndSplitReturn = testAndSplit(sk, i - 1, text.charAt(i - 1));
            }

            // oldr is not root add a suffixlink to the state s
            if (oldr != root) {
                oldr.setSuffixLink(sk.sState);
            }

            return;
        }

        // the method implementing the modified version of the testAndSplit
        // procedure
        // of Ukkonen's SuffixTree algorithm
        // the method tests if the state represented by s,k,p is an end point
        // for tVal
        // If it is not an endpoint, then it makes the state s,k,p explicit
        private TestAndSplitReturn testAndSplit(SKTuple sk, int pIndex, char tVal) {
            SuffixTreeOverlayEdge tOverlayEdge = sk.sState.getChild(text.charAt(sk.kIndex - 1));
            SuffixTreeIndexEdge tIndexEdge = sk.sState.getStIndexNode().getChild(text.charAt(sk.kIndex - 1));

            if (sk.kIndex <= pIndex) {

                // check if the next character on the edge matches the next
                // character in update tVal
                char nextChar = (tOverlayEdge.isDiscoveryLeaf()) ? 
                                 tIndexEdge.charAt(pIndex - sk.kIndex + 1) :
                                 text.charAt(tOverlayEdge.getStartIndex() + pIndex - sk.kIndex); 

                if (tVal == nextChar) {
                    // if the character matches, we have reached the end point
                    return new TestAndSplitReturn(true, sk.sState);
                } else {
                    // if the character does not match, create the state r - for
                    // both overlay edge and index edge
                    SuffixTreeIndexNode rIndexState = new SuffixTreeIndexNode();
                    SuffixTreeOverlayNode rOverlayState = new SuffixTreeOverlayNode(rIndexState);

                    SuffixTreeIndexEdge newIndexEdge = new SuffixTreeIndexEdge(text.substring(sk.kIndex - 1, pIndex), rIndexState);
                    if (!tOverlayEdge.isDiscoveryLeaf()) {
                        SuffixTreeOverlayEdge newOverlayEdge = new SuffixTreeOverlayEdge(tOverlayEdge.getStartIndex(),
                                                                                         tOverlayEdge.getStartIndex() + pIndex - sk.kIndex,
                                                                                         rOverlayState, newIndexEdge);                        
                        sk.sState.addChild(text.charAt(sk.kIndex - 1), newOverlayEdge);
                        tOverlayEdge.setStartIndex(tOverlayEdge.getStartIndex() + pIndex - sk.kIndex + 1);
                        rOverlayState.addChild(nextChar, tOverlayEdge);
                    } else {
                        tOverlayEdge.setChild(rOverlayState, tOverlayEdge.getStartIndex() + pIndex - sk.kIndex);
                    }

                    // add the transition from s,k,p to r
                    sk.sState.getStIndexNode().addChild(text.charAt(sk.kIndex - 1), newIndexEdge);

                    // add the transition from r to the rest of the tree ahead
                    if (!tOverlayEdge.isNewLeaf()) {
                        tIndexEdge.setText(tIndexEdge.getText().substring(pIndex - sk.kIndex + 1));
                    }

                    rIndexState.addChild(nextChar, tIndexEdge);

                    return new TestAndSplitReturn(false, rOverlayState);
                }
            } else {
                // For k > p, we return s state.
                // if tEdge is present we have reached end point

                if (tOverlayEdge != null) {
                    return new TestAndSplitReturn(true, sk.sState);
                } else if (tIndexEdge != null) {
                    // if index has edge for the char, then create a new
                    // discovery leaf for overlay
                    SuffixTreeOverlayEdge newOverlayEdge = new SuffixTreeOverlayEdge(sk.kIndex, tIndexEdge);
                    sk.sState.addChild(text.charAt(sk.kIndex - 1), newOverlayEdge);
                    return new TestAndSplitReturn(true, sk.sState);
                } else {
                    return new TestAndSplitReturn(false, sk.sState);
                }
            }

        }

        // the method implementing the the modified version of the canonize
        // procedure
        // of Ukkonen's SuffixTree algorithm
        // find the closest explicit state node for the state represented by
        // s,k,p
        // the overlay discovers edges if this part of tree has not been visited
        // yet
        private void canonize(SKTuple sk, int pIndex) {
            if (pIndex < sk.kIndex) {
                return;
            } else {
                SuffixTreeOverlayEdge overlayEdge = sk.sState.getChild(text.charAt(sk.kIndex - 1));
                SuffixTreeIndexEdge indexEdge = sk.sState.getStIndexNode().getChild(text.charAt(sk.kIndex - 1));

                // indexEdge is new leaf then we have reached the end of the
                // tree and current s is the closest
                while (!indexEdge.isNewLeaf()) {

                    // when k < p, there has to be an index edge
                    // but the overlay edge may not yet have been discovered
                    if (overlayEdge == null) {
                        overlayEdge = new SuffixTreeOverlayEdge(sk.kIndex, indexEdge);
                    }

                    if (indexEdge.length() - 1 <= (pIndex - sk.kIndex)) {

                        // if the overlayEdge is discovery edge, create a new
                        // state in the overlay tree and point the overlay edge
                        // to it
                        if (overlayEdge.isDiscoveryLeaf()) {
                            SuffixTreeOverlayNode newState = new SuffixTreeOverlayNode(indexEdge.getChild());
                            overlayEdge.setChild(newState, sk.kIndex + indexEdge.length() - 1);

                            // find the suffix link for sState
                            // in this implementation, we do not have a separate
                            // node for state _|_
                            // the check for root state tells how to find the
                            // suffixlink for the root
                            SKTuple localSk = new SKTuple(sk.sState, sk.kIndex);
                            if (localSk.sState == root) {
                                localSk.kIndex++;
                            } else {
                                localSk.sState = localSk.sState.getSuffixLink();
                            }

                            // discover suffix link for the new state
                            canonize(localSk, overlayEdge.getEndIndex());

                            // set the suffixlink for the new state
                            newState.setSuffixLink(localSk.sState);

                        }

                        sk.kIndex = sk.kIndex + indexEdge.length();
                        sk.sState = overlayEdge.getChild();

                        if (pIndex < sk.kIndex) {
                            break;
                        }

                        overlayEdge = sk.sState.getChild(text.charAt(sk.kIndex - 1));
                        indexEdge = sk.sState.getStIndexNode().getChild(text.charAt(sk.kIndex - 1));
                    } else {
                        break;
                    }
                }
                return;
            }
        } // void canonize

        // the method to add match ids to all the end nodes
        // it starts at the current end node and follows all the suffix links
        
        private void addMatchIds(SKTuple sk, int pIndex) {

            if (pIndex < sk.kIndex) {
                // if we have reached a node in the overlay tree, 
                // which does not have children, then add the match ID
                if (sk.sState.isLeaf()) {
                    sk.sState.getStIndexNode().addMatchIndex(matchIndex);
                }
                
                // find the suffix link for sState
                // in this implementation, we do not have a separate
                // node for state _|_
                // the check for root state tells how to find the
                // suffixlink for the root
                if (sk.sState != root) {
                    sk.sState = sk.sState.getSuffixLink();
                    // add match id to the suffix
                    addMatchIds(sk, pIndex);                    

                }
                
            }
            else {
                SuffixTreeOverlayEdge overlayEdge = sk.sState.getChild(text.charAt(sk.kIndex - 1));
                SuffixTreeIndexEdge indexEdge = sk.sState.getStIndexNode().getChild(text.charAt(sk.kIndex - 1));

                // if the edge is a discovery node, create a new node
                // and add the match index
                if (overlayEdge.isDiscoveryLeaf()) {
                    SuffixTreeIndexNode newIndexState = new SuffixTreeIndexNode();
                    SuffixTreeIndexEdge newIndexEdge = new SuffixTreeIndexEdge(text.substring(sk.kIndex - 1, pIndex), newIndexState);

                    // add the transition for index from s,k,p to r
                    sk.sState.getStIndexNode().addChild(text.charAt(sk.kIndex - 1), newIndexEdge);

                    // add the transition to the rest of the tree ahead
                    indexEdge.setText(indexEdge.getText().substring(pIndex - sk.kIndex + 1));

                    newIndexState.addChild(indexEdge.getText().charAt(0), indexEdge);
                    
                    newIndexState.addMatchIndex(matchIndex);
                    
                    // find the suffix link for sState
                    // in this implementation, we do not have a separate
                    // node for state _|_
                    // the check for root state tells how to find the
                    // suffixlink for the root
                    if (sk.sState == root) {
                        sk.kIndex++;
                    } else {
                        sk.sState = sk.sState.getSuffixLink();
                    }

                    // discover suffix link for the new state
                    canonize(sk, pIndex);

                    // add match id to the suffix
                    addMatchIds(sk, pIndex);                    
                }
            }

        }
        

        // private
        private SuffixTreeOverlayNode root;
        private String text;
        private int matchIndex;
        private LinkedList<SuffixTreeOverlayEdge> newLeafEdges;

    } // class SuffixTreeOverlay

}
