package hw3;


import hw1.Field;
import hw1.RelationalOperator;

public class BPlusTree {
    
    public BPlusTree(int pInner, int pLeaf) {
    	//your code here
    }
    
    public LeafNode search(Field f) {
    	//your code here
    	return null;
    }
    
    public void insert(Entry e) {
    	if (search(e.getField()) != null) {
    		return;
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
    	return null;
    }
    


	
}
