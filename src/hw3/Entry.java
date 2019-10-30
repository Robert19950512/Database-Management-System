package hw3;
/*
 * Student 1 name: Fa Long (id:462512)
 * Student 2 name: Zhuo Wei (id: 462473)
 * Date: Oct 29th, 2019
 */
import hw1.Field;

public class Entry {

	private Field f;
	private int page;
	
	public Entry(Field f, int page) {
		this.f = f;
		this.page = page;
	}
	
	public Field getField() {
		return this.f;
	}
	
	public int getPage() {
		return this.page;
	}
	
}
