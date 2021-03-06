package hw3;
/*
 * Student 1 name: Fa Long (id:462512)
 * Student 2 name: Zhuo Wei (id: 462473)
 * Date: Oct 29th, 2019
 */
import java.util.ArrayList;
import hw1.RelationalOperator;

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
	
	public void setEntries(ArrayList<Entry> newEntries) {
		this.entries = newEntries;
	}
	
	public void addToEntries(Entry e) {
		if (entries.size() == 0) {
			entries.add(e);
			return;
		}
		boolean added = false;
		for (int i = 0 ; i < entries.size() ; i ++) {
			if (e.getField().compare(RelationalOperator.LT, entries.get(i).getField())){
				entries.add(i, e);
				added = true;
				break;
			}
		}
		if (added == false) {
			entries.add(e);
		}	
	}
	
	public LeafNode merge(LeafNode other) {
		//merge another leafNode with current leafNode,
		
		ArrayList<Entry> newEntries = new ArrayList<Entry>();
		if (this.getNext() == other) {
			newEntries.addAll(this.getEntries());
			newEntries.addAll(other.getEntries());
			if (other.getNext() != null) {
				other.getNext().setPrev(this);
			}
		} else {
			newEntries.addAll(other.getEntries());
			newEntries.addAll(this.getEntries());
			if (other.getPrev() != null) {
				other.getPrev().setNext(this);
			}
		}
		this.setEntries(newEntries);
		return this;
	}
	
	public LeafNode split() {
		// split current node to 2 nodes, the current node will become the left one
		ArrayList<Entry> newLeft = new ArrayList<>();
		ArrayList<Entry> newRight = new ArrayList<>();
		for (int i = 0 ; i < Math.ceil(((double)this.getEntries().size())/2) ; i ++) {
			newLeft.add(this.getEntries().get(i));
		}
		for (int i = newLeft.size(); i < this.getEntries().size() ; i ++) {
			newRight.add(this.getEntries().get(i));
		}
		LeafNode rightNode = new LeafNode(this.getDegree());
		if (this.next != null) {
			this.next.prev = rightNode;
		}
		this.setEntries(newLeft);
		rightNode.setEntries(newRight);
		rightNode.setNext(this.next);
		this.setNext(rightNode);
		rightNode.setPrev(this);
		return rightNode;
	}
	
	public void removeEntry(Entry e) {
		for (Entry en : this.entries) {
			if (en.getField().compare(RelationalOperator.EQ, e.getField())) {
				entries.remove(en);
				return;
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry en : this.entries) {
			sb.append(en.getField().toString());
		}
		return sb.toString();
	}

}