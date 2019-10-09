package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import hw1.Field;
import hw1.IntField;
import hw1.RelationalOperator;
import hw3.BPlusTree;
import hw3.Entry;
import hw3.InnerNode;
import hw3.Node;

public class YourHW3Tests {

	@Test
	public void testSplit() {
		BPlusTree bt = new BPlusTree(3, 2);
		bt.insert(new Entry(new IntField(9), 0));
		bt.insert(new Entry(new IntField(4), 0));
		bt.insert(new Entry(new IntField(12),0));
		bt.insert(new Entry(new IntField(7), 0));
		Node root = bt.getRoot();
		assertTrue(root.isLeafNode() == false);

		InnerNode in = (InnerNode)root;
		//check whether the root was updated
		ArrayList<Field> keys = in.getKeys();
		assertTrue(keys.get(0).compare(RelationalOperator.EQ, new IntField(7)));
		assertTrue(keys.get(1).compare(RelationalOperator.EQ, new IntField(9)));
		//check splitting
		ArrayList<Node> children = in.getChildren();
		InnerNode middle = (InnerNode)children.get(1);
		InnerNode right = (InnerNode)children.get(2);
		assertTrue(middle.getKeys().get(0).compare(RelationalOperator.EQ, new IntField(9)));
		assertTrue(right.getKeys().get(0).compare(RelationalOperator.EQ, new IntField(12)));
		
		
		bt.insert(new Entry(new IntField(2), 0));
		root = bt.getRoot();
		in = (InnerNode)root;
		//the root should be updated
		keys = in.getKeys();
		assertTrue(keys.get(0).compare(RelationalOperator.EQ, new IntField(7)));
		//the key 7 should be pushed up
		children = in.getChildren();
		InnerNode left = (InnerNode)children.get(0);
		assertFalse(left.getKeys().get(1).compare(RelationalOperator.EQ, new IntField(7)));
		// testing key7 and key9 split
		middle = (InnerNode)children.get(1);
		assertTrue(keys.get(0).compare(RelationalOperator.EQ, new IntField(9)));
		
	}
	
	@Test
	public void testDeleteThenSearch() {
		BPlusTree bt = new BPlusTree(3, 2);
		bt.insert(new Entry(new IntField(9), 0));
		bt.insert(new Entry(new IntField(4), 0));
		bt.insert(new Entry(new IntField(12), 0));
		bt.insert(new Entry(new IntField(7), 0));
		bt.insert(new Entry(new IntField(2), 0));
		bt.insert(new Entry(new IntField(6), 0));
		bt.insert(new Entry(new IntField(3), 0));

		bt.delete(new Entry(new IntField(7), 0));
		bt.delete(new Entry(new IntField(3), 0));
		bt.delete(new Entry(new IntField(4), 0));
		
		
		//deleted ones should not exist
				assertTrue(bt.search(new IntField(7)) == null);
				assertTrue(bt.search(new IntField(3)) == null);
				assertTrue(bt.search(new IntField(4)) == null);
	}
	
	@Test
	public void testDeleteNoExist() {
		BPlusTree bt = new BPlusTree(3, 2);
		bt.insert(new Entry(new IntField(9), 0));
		bt.delete(new Entry(new IntField(7), 0));
		//nothing should changed
		Node root = bt.getRoot();
		InnerNode in = (InnerNode)root;
		//check whether the root was updated
		ArrayList<Field> keys = in.getKeys();
		assertTrue(keys.get(0).compare(RelationalOperator.EQ, new IntField(9)));
		
	}

}
