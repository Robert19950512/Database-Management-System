package hw3;


import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class BPlusTree {
	int pInner;
	int pLeaf;
	Node root;
	
    
    public BPlusTree(int pInner, int pLeaf) {
    	//your code here
    	this.pInner = pInner;
    	this.pLeaf = pLeaf;
    	this.root = null;
    }
    
    public LeafNode search(Field f) {
    	//your code here
    	//corner case
    	if (root == null) {
    		return null;
    	}
    	return searchHelper(f, this.root);
    }
    private LeafNode searchHelper(Field f, Node root) {
    	//base case
    	if (root.isLeafNode() == true) {
    		//iterate all entries in this leafNode to check whether f exist
    		for (Entry entry: ((LeafNode)root).getEntries()) {
    			if (entry.getField().equals(f)) {
    				return (LeafNode)root;
    			}
    		}
    		return null;
    	}
    	ArrayList<Field> keys = ((InnerNode)root).getKeys();
    	ArrayList<Node> children = ((InnerNode)root).getChildren();
    	for (int i = 0; i < keys.size(); i++) {	
    		if (keys.get(i).compare(RelationalOperator.GTE, f) == true) {
    			return searchHelper(f, children.get(i));
    		}	
    	}
    	return searchHelper(f, children.get(keys.size()));
    	
    }
    
    
    public void insert(Entry e) {
    	if (search(e.getField()) != null) {
    		return;
    	}
    	if (this.getRoot() == null) {
    		// first insert
    		LeafNode theLeaf = new LeafNode(this.pLeaf);
    		theLeaf.getEntries().add(e);
    		this.root = theLeaf;
    		return;
    	}
    	LeafNode theLeaf = findleaf(e,this.getRoot());
    	theLeaf.addToEntries(e);
		
		if (theLeaf.getEntries().size() <= theLeaf.getDegree()) {
			// simple case, if there is empty spot, directly insert;
			return;
    	} else {
    		// need to split;
    		LeafNode rightNode = theLeaf.split();
    		if (theLeaf.getParent() != null) {
    			theLeaf.getParent().addNewChild(theLeaf, rightNode);
    			InnerNode parent = theLeaf.getParent();
    			while (parent.getChildren().size() > parent.getDegree()) {
    				// parent need to split
    				InnerNode newInnerRight = parent.split();
    				if (parent.parent != null) {
    					parent.parent.addNewChild(parent, newInnerRight);
    					parent = parent.parent;
    				} else {
    					// new parent need to create
    					InnerNode newRoot = new InnerNode(this.pInner);
    					newRoot.addNewChild(parent, newInnerRight);
    					this.root = newRoot;
    					parent = newRoot;
    				}
    			}
    		} else {
    			InnerNode newInner = new InnerNode(this.pInner);
    			newInner.addNewChild(theLeaf, rightNode);
    			theLeaf.setParent(newInner);
    			rightNode.setParent(newInner);
    			this.root = newInner;
    		}	
    	}  	
    	//your code here
    }
    
    // find the leafNode that e needs to get insert into, this is a helper function
    // for insert(Entry e) and delete
    public LeafNode findleaf(Entry e, Node cur) {
    	if (cur.isLeafNode()) {
    		return (LeafNode) cur;
    	}
    	InnerNode curInner = (InnerNode) cur;
    	for (int i = 0 ; i < curInner.getKeys().size() ; i ++) {
    		if (e.getField().compare(RelationalOperator.LTE, curInner.keys.get(i))) {
    			return findleaf(e,curInner.getChildren().get(i));
    		}
    	}
    	return findleaf(e,curInner.getChildren().get(curInner.getChildren().size() - 1));
    }
    
    public void delete(Entry e) {
    	//your code here
    	if (search(e.getField()) == null) {
    		return;
    	}
    	LeafNode theLeaf = findleaf(e,this.getRoot());
    	theLeaf.removeEntry(e);
    	if (theLeaf.getEntries().size() < theLeaf.getMiniEntry() && theLeaf != root) {
    		// need to adjust the structure
    		// try to borrow
    		if (theLeaf.getPrev() != null && theLeaf.getPrev().getEntries().size() > theLeaf.getPrev().getMiniEntry()
    				&& theLeaf.getPrev().getParent() == theLeaf.getParent()) {
				// borrow from left
				LeafNode prev = theLeaf.getPrev();
				Entry toMove = prev.getEntries().get(prev.getEntries().size() - 1);
				prev.getEntries().remove(prev.getEntries().size() - 1);
				theLeaf.addToEntries(toMove);
				// update parent's key
				InnerNode theParent = prev.getParent();
				for (int i = 0 ; i < theParent.getChildren().size() ; i++) {
					if (theParent.getChildren().get(i) == prev) {
						theParent.getKeys().set(i, prev.getEntries().get(prev.getEntries().size() - 1).getField());
					}
				}
    		} else if (theLeaf.getNext() != null && theLeaf.getNext().getEntries().size() > theLeaf.getNext().getMiniEntry()
    				&& theLeaf.getNext().getParent() == theLeaf.getParent()) {
    			// borrow from right
    			LeafNode next = theLeaf.getNext();
				Entry toMove = next.getEntries().get(0);
				next.getEntries().remove(0);
				theLeaf.addToEntries(toMove);
				// update parent's key
				InnerNode theParent = theLeaf.getParent();
				for (int i = 0 ; i < theParent.getChildren().size() ; i++) {
					if (theParent.getChildren().get(i) == theLeaf) {
						theParent.getKeys().set(i, theLeaf.getEntries().get(theLeaf.getEntries().size() - 1).getField());
					}
				}
    			
    		} else {
    			// borrow failed, need to merge
    			// try merge with left
    			LeafNode merged = null;
    			InnerNode parent = null;
    			if (theLeaf.getPrev() != null && theLeaf.getPrev().getParent() == theLeaf.getParent()) {
    				merged = theLeaf.getPrev().merge(theLeaf);
    				parent = merged.getParent();
    				for (int i = 0 ; i < parent.getChildren().size();i++) {
    					if (parent.getChildren().get(i) == theLeaf) {
    						parent.getChildren().remove(i);
							// remove the key before it
							parent.getKeys().remove(i - 1);
    						
    					}
    				}
//    				for (int i = 0 ; i < parent.getChildren().size(); i ++) {
//    					if (parent.getChildren().get(i) == theLeaf) {
//    						// delete the leaf and corresponding key
//    						parent.getKeys().remove(i);
//    						parent.getChildren().remove(theLeaf);
//    						break;
//    					}
//    				}
    			} else if (theLeaf.getNext() != null && theLeaf.getNext().getParent() == theLeaf.getParent()) {
    				merged = theLeaf.getNext().merge(theLeaf);
    				parent = merged.getParent();
    				for (int i = 0 ; i < parent.getChildren().size(); i ++) {
    					if (parent.getChildren().get(i) == theLeaf) {
    						// delete the leaf and corresponding key
    						parent.getKeys().remove(i);
    						parent.getChildren().remove(theLeaf);
    						break;
    					}
    				}
    			}
    			// after merge, check the parent node to see if parent node is still valid
				if (parent.getChildren().size() < parent.minPointer) {
					// parent don't have enough children to survive
					if (parent == root) {
						if (parent.getChildren().size() == 1) {
							// parent no longer needed
							merged.setParent(null);
							this.root = merged;
							return;
						}
					} else {
						// parent try to borrow from its siblings first
    					InnerNode parentLeft = null;
    					InnerNode parentRight = null;
    					int keyIndex = 0;
    					for (int i = 0 ; i < parent.parent.getChildren().size() ; i++) {
    						if (parent.parent.getChildren().get(i) == parent) {
    							if (i - 1 >= 0) {
    								parentLeft = (InnerNode) parent.parent.getChildren().get(i - 1);
    								
    							}
    							if (i + 1 < parent.parent.getChildren().size()) {
    								parentRight = (InnerNode) parent.parent.getChildren().get(i + 1);
    							}
    							// suppose we try to borrow from left first
    							keyIndex = i - 1;
//    							if (keyIndex < parent.getParent().getKeys().size()) {
//    								keyIndex = i;
//    							} else {
//    								keyIndex = i - 1;
//    							}
    							
    							break;
    						}
    					}
    					//try borrow from left
    					if (parentLeft != null && parentLeft.getChildren().size() > parentLeft.minPointer) {
    						Node toMove = parentLeft.getChildren().remove(parentLeft.getChildren().size() - 1);
    						Field passKey = parentLeft.getKeys().remove(parentLeft.getKeys().size() - 1);
    						Field parentKey = parent.parent.getKeys().get(keyIndex);
    						parent.parent.getKeys().set(keyIndex, passKey);
    						parent.children.add(0, toMove);
    						parent.getKeys().add(0, parentKey);
    						
    					} else if (parentRight != null && parentRight.getChildren().size() > parentRight.minPointer) {
    						//identify the key that needs to push through in parent
    						keyIndex++;
    						// if can't borrow from left, try to borrow from right
    						Node toMove = parentRight.getChildren().remove(0);
    						Field passKey = parentRight.getKeys().remove(0);
    						Field parentKey = parent.parent.getKeys().get(keyIndex);
    						parent.parent.getKeys().set(keyIndex, passKey);
    						parent.children.add(toMove);
    						parent.getKeys().add(parentKey);
    					} else {
    						// no place to borrow, collapse the entire level
    						InnerNode grandParent = parent.parent;
    						ArrayList<Field> newKeys = new ArrayList<>();
    						ArrayList<Node> newChildren = new ArrayList<>();
    						for (int i = 0 ; i < grandParent.getChildren().size() ; i ++) {
    							InnerNode curChildren = (InnerNode) grandParent.getChildren().get(i);
    							newKeys.addAll(curChildren.getKeys());
    							if (i < grandParent.getKeys().size()) {
    								newKeys.add(grandParent.getKeys().get(i));
    							}
    							newChildren.addAll(curChildren.getChildren());
    						}
    						grandParent.setChildren(newChildren);
    						grandParent.setKeys(newKeys);
    					}
					}
					
				}
    		}
    	} else {
    		if (theLeaf == root && theLeaf.getEntries().size() == 0) {
    			root = null;
    		}
    	}
    	
    	
    }
    
    public Node getRoot() {
    	//your code here
    	return this.root;
    }
    


	
}
