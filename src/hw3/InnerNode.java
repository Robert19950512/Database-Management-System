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
	public void updateKey (Field newKey) { 
		// when a new key is pushed from its children
		// add in
		if (keys.size() == 0) {
			keys.add(newKey);
		}else {
			boolean isSet = false;
			for(int i = 0; i < keys.size(); i++) {
				if(newKey.compare(RelationalOperator.LTE, keys.get(i)) == true) {	
					keys.add(i, newKey);
					isSet = true;
					break;
				}
			}
			if (isSet == false) {
				keys.add(newKey);
			}
			
		}
		
	}

	public void addNewChild (Node oldNode, Node newNode) {
		// add the node that is split out from an old node to its right
		for (int i = 0 ; i < children.size() ; i++) {
			if (children.get(i) == oldNode) {
				children.add(i+1, newNode);
				// find key that needs to update
				if (oldNode.getClass() == InnerNode.class) {
					// a innerNode with children being innerNode
					InnerNode temp = (InnerNode) oldNode;
					Field newKey = temp.keys.get(temp.keys.size() - 1);
					// when innerNode push keys up, they no longer possess the key
					temp.keys.remove(temp.keys.size() - 1);
					updateKey(newKey);
					
				} else {
					LeafNode temp = (LeafNode) oldNode;
					Field newKey = temp.entries.get(temp.entries.size() - 1).getField();
					updateKey(newKey);
					// a innerNode with children being leafNode
				}
				break;
			}
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
		ArrayList<Node> newLeftChildren = new ArrayList<>();
		ArrayList<Node> newRightChildren = new ArrayList<>();
		for (int i = 0 ; i <= newLeft.size(); i ++) {
			newLeftChildren.add(this.getChildren().get(i));
		}
		for (int i = newLeft.size() + 1; i < children.size() ;i++ ) {
			newRightChildren.add(this.getChildren().get(i));
		}
		this.children = newLeftChildren;
		this.keys = newLeft;
		InnerNode rightNode = new InnerNode(this.getDegree());
		rightNode.setKeys(newRight);
		rightNode.setChildren(newRightChildren);
		return rightNode;
	}
	public void setKeys (ArrayList<Field> newKeys) {
		this.keys = newKeys;
	}
	public void setChildren (ArrayList<Node> newChildren) {
		this.children = newChildren;
	}

		

}