package hw1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
/*
 * Student 1 name: Fa Long (id:462512)
 * Student 2 name: Zhuo Wei (id: 462473)
 * Date: Sep 13th, 2019
 */
/**
 * A heap file stores a collection of tuples. It is also responsible for managing pages.
 * It needs to be able to manage page creation as well as correctly manipulating pages
 * when tuples are added or deleted.
 * @author Sam Madden modified by Doug Shook
 *
 */
public class HeapFile {
	
	
	public static final int PAGE_SIZE = 4096;
	private File file;
	private TupleDesc td;
	private int tableId;
	private int numOfPages;
	
	/**
	 * Creates a new heap file in the given location that can accept tuples of the given type
	 * @param f location of the heap file..
	 * @param types type of tuples contained in the file
	 */
	public HeapFile(File f, TupleDesc type) {
		//your code here
		this.file = f;
		this.td = type;
		this.tableId = this.hashCode();
		this.numOfPages = 1;
	}
	
	public File getFile() {
		//your code here
		return this.file;
	}
	
	public TupleDesc getTupleDesc() {
		//your code here
		return this.td;
	}
	
	/**
	 * Creates a HeapPage object representing the page at the given page number.
	 * Because it will be necessary to arbitrarily move around the file, a RandomAccessFile object
	 * should be used here.
	 * @param id the page number to be retrieved
	 * @return a HeapPage at the given page number
	 * @throws IOException 
	 */
	public HeapPage readPage(int id) {// id is page number
		//your code here
		RandomAccessFile raFile;
		byte[] data;
		try {
			raFile = new RandomAccessFile(this.file, "r");
			raFile.seek((long)id * PAGE_SIZE);
			data = new byte[PAGE_SIZE];
			raFile.read(data);
			raFile.close();
			return new HeapPage(id, data, tableId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
		
	}
	
	/**
	 * Returns a unique id number for this heap file. Consider using
	 * the hash of the File itself.
	 * @return
	 */
	public int getId() {
		//your code here
		return this.tableId;
	}
	
	/**
	 * Writes the given HeapPage to disk. Because of the need to seek through the file,
	 * a RandomAccessFile object should be used in this method.
	 * @param p the page to write to disk
	 * @throws IOException 
	 */
	public void writePage(HeapPage p) throws IOException {
		//your code here
		byte[] data = p.getPageData();
		RandomAccessFile raFile = new RandomAccessFile(this.file, "rw");
		raFile.seek((long)p.getId() * PAGE_SIZE);
		raFile.write(data);
		raFile.close();
		
		
	}
	
	/**
	 * Adds a tuple. This method must first find a page with an open slot, creating a new page
	 * if all others are full. It then passes the tuple to this page to be stored. It then writes
	 * the page to disk (see writePage)
	 * @param t The tuple to be stored
	 * @return The HeapPage that contains the tuple
	 */
	public HeapPage addTuple(Tuple t) {
		//your code here
		HeapPage emptyPage = findEmpty();
		if (emptyPage == null) {
			// need to creat a new page
			try {
				emptyPage = new HeapPage(numOfPages , new byte[PAGE_SIZE], tableId);
				numOfPages++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			try {
				emptyPage.addTuple(t);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("problem with add tuple");
				e.printStackTrace();
			}
			try {
				writePage(emptyPage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		return emptyPage;
	}
	//this helper methods find the first page that is not full, return null if all current
	//exit page is full.
	private HeapPage findEmpty(){
		for (int i = 0 ; i < numOfPages ; i++) {
			HeapPage cur = this.readPage(i);
			if (!cur.isFull()) {
				return cur;
			}
		}
		return null;
	}
	
	/**
	 * This method will examine the tuple to find out where it is stored, then delete it
	 * from the proper HeapPage. It then writes the modified page to disk.
	 * @param t the Tuple to be deleted
	 * @throws Exception 
	 */
	public void deleteTuple(Tuple t) throws Exception{
		//your code here
		int pageId = t.getPid();
		HeapPage hp = readPage(pageId);
		hp.deleteTuple(t);
		writePage(hp);
	}
	
	/**
	 * Returns an ArrayList containing all of the tuples in this HeapFile. It must
	 * access each HeapPage to do this (see iterator() in HeapPage)
	 * @return
	 * @throws IOException 
	 */
	public ArrayList<Tuple> getAllTuples() throws IOException {
		//your code here
		ArrayList<Tuple> allTuples = new ArrayList<Tuple>();
		for(int i = 0; i < numOfPages; i++) {
				HeapPage hp = readPage(i);
				Iterator<Tuple> iter = hp.iterator();
				while(iter.hasNext()) {
					allTuples.add(iter.next());
				}
		}
		return allTuples;
	}
	
	/**
	 * Computes and returns the total number of pages contained in this HeapFile
	 * @return the number of pages
	 */
	public int getNumPages() {
		//your code here
		return numOfPages;
	}
}
