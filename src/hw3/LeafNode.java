package hw3;

import java.util.ArrayList;

public class LeafNode implements Node {
	private int degree;
	private int miniEntry;
	ArrayList<Entry> entries;
	
	public LeafNode(int degree) {
		this.degree = degree;
		this.miniEntry = (int) Math.ceil((double) degree / 2);
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
		return this.miniEntry;
	}

}