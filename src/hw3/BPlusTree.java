package hw3;


import hw1.Field;

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
    	for (Field key : ((InnerNode)root).getKeys()) {
    		if (k.compare(LTE, f))
    	}
    	
    }
    
    
    public void insert(Entry e) {
    	//your code here
    }
    
    public void delete(Entry e) {
    	//your code here
    }
    
    public Node getRoot() {
    	//your code here
    	return null;
    }
    


	
}
