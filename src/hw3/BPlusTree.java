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
    	
		if (theLeaf.getEntries().size() == 0) {
			theLeaf.getEntries().add(e);
		}
		boolean added = false;
		for (int i = 0 ; i < theLeaf.getEntries().size() ; i ++) {
			if (e.getField().compare(RelationalOperator.LT, theLeaf.getEntries().get(i).getField())){
				theLeaf.getEntries().add(i, e);
				added = true;
				break;
			}
		}
		if (added == false) {
			theLeaf.getEntries().add(e);
		}	
		
		if (theLeaf.getEntries().size() <= theLeaf.getDegree()) {
			// simple case, if there is empty spot, directly insert;
			return;
    	} else {
    		// need to split;
    		LeafNode rightNode = theLeaf.split();
    		if (theLeaf.getParent() != null) {
    			theLeaf.getParent().addNewChild(theLeaf, rightNode);
    		} else {
    			InnerNode newInner = new InnerNode(this.pInner);
    			newInner.addNewChild(theLeaf, rightNode);
    			this.root = newInner;
    		}
    		
    		
    		
    	}
    	
    	
    	
    	//your code here
    }
    // find the leafNode that e needs to get insert into, this is a helper function
    // for insert(Entry e)
    public LeafNode findleaf(Entry e, Node cur) {
    	if (cur.isLeafNode()) {
    		return (LeafNode) cur;
    	}
    	InnerNode curInner = (InnerNode) cur;
    	for (int i = 0 ; i < curInner.degree ; i ++) {
    		if (e.getField().compare(RelationalOperator.LTE, curInner.keys.get(i))) {
    			return findleaf(e,curInner.getChildren().get(i));
    		}
    	}
    	return findleaf(e,curInner.getChildren().get(curInner.getChildren().size() - 1));
    }
    
    public void delete(Entry e) {
    	//your code here
    }
    
    public Node getRoot() {
    	//your code here
    	return this.root;
    }
    


	
}
