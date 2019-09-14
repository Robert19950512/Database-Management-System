package hw1;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
/*
 * Student 1 name: Fa Long (id:462512)
 * Student 2 name: Zhuo Wei (id: 462473)
 * Date: Sep 13th, 2019
 */
/**
 * This class represents a tuple that will contain a single row's worth of information
 * from a table. It also includes information about where it is stored
 * @author Sam Madden modified by Doug Shook
 *
 */
public class Tuple {
	
	/**
	 * Creates a new tuple with the given description
	 * @param t the schema for this tuple
	 */
	private TupleDesc desc;
	private int pid;
	private int sid;
	HashMap<Integer, Field> fieldMap= new HashMap<>();
	public Tuple(TupleDesc t) {
		//your code here
		this.desc = t;
	}
	
	public TupleDesc getDesc() {
		//your code here, don't know what to do
		return this.desc;
	}
	
	/**
	 * retrieves the page id where this tuple is stored
	 * @return the page id of this tuple
	 */
	public int getPid() {
		//your code here
		return this.pid;
	}

	public void setPid(int pid) {
		//your code here
		this.pid = pid;
	}

	/**
	 * retrieves the tuple (slot) id of this tuple
	 * @return the slot where this tuple is stored
	 */
	public int getId() {
		//your code here
		return this.sid;
	}

	public void setId(int id) {
		this.sid = id;
		//your code here
	}
	
	public void setDesc(TupleDesc td) {
		//your code here;
		this.desc = td;
	}
	
	/**
	 * Stores the given data at the i-th field
	 * @param i the field number to store the data
	 * @param v the data
	 */
	public void setField(int i, Field v) {
		//your code here
		fieldMap.put(i,v);
	}
	public Field getField(int i) {
		//your code here
		return fieldMap.get(i);
	}
	
	/**
	 * Creates a string representation of this tuple that displays its contents.
	 * You should convert the binary data into a readable format (i.e. display the ints in base-10 and convert
	 * the String columns to readable text).
	 */
	public String toString() {
		//your code here
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Integer, Field> entry : fieldMap.entrySet()) {
			sb.append("id: " + entry.getKey() + "value:" + entry.getValue().toString() + " ");
		}
		return sb.toString();
	}
}
	