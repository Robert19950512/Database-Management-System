package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import hw1.Catalog;
import hw1.Database;
import hw1.HeapFile;
import hw1.HeapPage;
import hw1.TupleDesc;
import hw2.Relation;

public class YourUnitTests {
	
	private HeapFile hf;
	private TupleDesc td;
	private Catalog c;
	private HeapPage hp;
	private HeapFile ahf;
	private TupleDesc atd;
	private HeapFile bhf;
	private TupleDesc btd;

	@Before
	public void setup() throws IOException {
		
		try {
			Files.copy(new File("testfiles/test.dat.bak").toPath(), new File("testfiles/test.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("unable to copy files");
			e.printStackTrace();
		}
		
		c = Database.getCatalog();
		c.loadSchema("testfiles/test.txt");
		
		int tableId = c.getTableId("test");
		td = c.getTupleDesc(tableId);
		hf = c.getDbFile(tableId);
		hp = hf.readPage(0);
		c = Database.getCatalog();
		c.loadSchema("testfiles/A.txt");
		
		tableId = c.getTableId("A");
		atd = c.getTupleDesc(tableId);
		ahf = c.getDbFile(tableId);
		
		c = Database.getCatalog();
		c.loadSchema("testfiles/B.txt");
		tableId = c.getTableId("B");
		btd = c.getTupleDesc(tableId);
		bhf = c.getDbFile(tableId);
	}
	
	@Test
	public void testProject() {
		//this method tests the reaction if the given c(column number) is invalid,
		// I assume that this way project will return null
		Relation ar = new Relation(ahf.getAllTuples(), atd);
		ArrayList<Integer> c = new ArrayList<Integer>();
		//out of bounds
		c.add(3);
		ar = ar.project(c);
		assertNull(ar);
		c.clear();
	}
	
	@Test
	public void testProject2() {
		//test what project do with multiply columns
		Relation ar = new Relation(ahf.getAllTuples(), atd);
		ArrayList<Integer> c = new ArrayList<Integer>();
		//out of bounds
		c.add(0);
		c.add(1);
		ar = ar.project(c);
		assertTrue("Projection should not remove anything for this test", ar.getDesc().getSize() == 8);
		assertTrue("Projection should not change the number of tuples", ar.getTuples().size() == 8);
		assertTrue("Projection should retain the column names", ar.getDesc().getFieldName(0).equals("a1") && ar.getDesc().getFieldName(1).equals("a2"));
	}
	
	@Test
	public void testRename() {
		//test what happened if rename to an already exist name,
		//I expect the method reject the operation and return original relation
		Relation ar = new Relation(ahf.getAllTuples(), atd);
		
		ArrayList<Integer> f = new ArrayList<Integer>();
		ArrayList<String> n = new ArrayList<String>();
		
		f.add(0);
		n.add("a2");
		
		try {
			ar = ar.rename(f, n);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue("Rename should change nothing", ar.getDesc().getFieldName(0).equals("a1"));
		
	}
	
	@Test
	public void testRename2() {
		//test what happened if we rename 2 column to a same name
		//I expect the method reject the operation and return original relation
		Relation ar = new Relation(ahf.getAllTuples(), atd);
		
		ArrayList<Integer> f = new ArrayList<Integer>();
		ArrayList<String> n = new ArrayList<String>();
		
		f.add(0);
		f.add(1);
		n.add("same");
		n.add("same");
		
		try {
			ar = ar.rename(f, n);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue("Rename should change nothing", ar.getDesc().getFieldName(0).equals("a1") && ar.getDesc().getFieldName(1).equals("a2"));
		
	}
	
	
	
	

}
