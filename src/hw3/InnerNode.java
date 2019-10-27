package hw3;

import java.util.ArrayList;
import java.util.List;

import hw1.Field;

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
		this.children = new ArrayList<Node> (degree);
		this.keys = new ArrayList<Field> (degree - 1);
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

}