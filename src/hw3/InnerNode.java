package hw3;

import java.util.ArrayList;
import java.util.List;

import hw1.Field;
import hw1.RelationalOperator;

public class InnerNode implements Node {
	int degree;
	int minPointer;
	ArrayList<Node> children;
	ArrayList<Field> keys;
	InnerNode parent;
	public InnerNode(int degree) {
		//your code here
		this.degree = degree;
		this.minPointer = (int) Math.ceil((double) degree / 2);
		this.children = new ArrayList<Node> ();
		this.keys = new ArrayList<Field> ();
	}
	
	public ArrayList<Field> getKeys() {
		//your code here
		return this.keys;
	}
	
	public ArrayList<Node> getChildren() {
		//your code here
		return this.children;
	}

	public int getDegree() {
		//your code here
		return this.degree;
	}
	
	public boolean isLeafNode() {
		return false;
	}
	

	public void updateKey (Field newKey) { // when a new key is pushed from its children
		// add in
		
		ArrayList<Field> newKeys = new ArrayList<>();
		for(int i = 0; i < keys.size(); i++) {
			if(newKey.compare(RelationalOperator.LTE, keys.get(i)) == true) {
				newKeys.add(newKey);
			}
			newKeys.add(keys.get(i));
		}
		setKeys(newKeys);
		//check whether need to split
		if(newKeys.size() <= degree - 1) {
			return;
		} else {
			InnerNode newNode = split();
			Field updateKey = this.keys.get(this.keys.size() - 1);
			if (this.parent != null) {
				this.parent.updateKey(updateKey);
			} else {
				InnerNode newRoot = new InnerNode(this.degree);
				newRoot.keys.add(updateKey);
				this.root = newRoot;
			}
		}
		
	}

	public void addNewChild (Node oldNode, Node newNode) {
		if(this.children.size() == this.degree) {
			
		}
	}
	
	public InnerNode split() {
		// split current node to 2 nodes, the current node will become the left one
		ArrayList<Field> newLeft = new ArrayList<>();
		ArrayList<Field> newRight = new ArrayList<>();
		for (int i = 0 ; i < Math.ceil(((double)this.getKeys().size())/2); i ++) {
			newLeft.add(this.getKeys().get(i));
		}
		for (int i = newLeft.size(); i < this.getKeys().size(); i ++) {
			newRight.add(this.getKeys().get(i));
		}
		InnerNode rightNode = new InnerNode(this.getDegree());
		this.setKeys(newLeft);
		rightNode.setKeys(newRight);
		return rightNode;
	}
	public void setKeys (ArrayList<Field> newKeys) {
		this.keys = newKeys;
	}
	public void setChildren (ArrayList<Node> newChildren) {
		this.children = newChildren;
	}

		

}