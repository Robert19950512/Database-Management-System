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
	
	public void updatekey (Field newkey) {
		// when a new key is pushed from its children
		for(int i = 0; i < keys.size(); i++) {
			if(newkey.compare(RelationalOperator.LTE, keys.get(i)) == true) {
				
			}
		}
	}

	public void addNewChild (Node oldNode, Node newNode) {
		if(this.children.size() == this.degree) {
			
		}
	}

}