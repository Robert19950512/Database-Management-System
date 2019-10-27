package hw3;

import java.util.ArrayList;

public class LeafNode implements Node {
	private int degree;
	private int minEntry;
	private LeafNode prev;
	private LeafNode next;
	ArrayList<Entry> entries;
	
	private InnerNode parent;
	
	public LeafNode(int degree) {
		this.degree = degree;
		this.minEntry = (int) Math.ceil((double) degree / 2);
		entries = new ArrayList<>();
	}
	
	public ArrayList<Entry> getEntries() {
		//your code here
		return this.entries;
	}

	public int getDegree() {
		//your code here
		return degree;
	}
	
	public boolean isLeafNode() {
		return true;
	}
	public int getMiniEntry() {
		return this.minEntry;
	}
	public void setPrev (LeafNode prev) {
		this.prev = prev;
	}
	public void setNext(LeafNode next) {
		this.next = next;
	}
	public LeafNode getPrev() {
		return this.prev;
	}
	public LeafNode getNext() {
		return this.next;
	}
	
	public InnerNode getParent() {
		return this.parent;
	}
	public void setParent(InnerNode parent) {
		this.parent = parent;
	}

}