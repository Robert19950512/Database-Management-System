package hw4;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import hw1.Catalog;
import hw1.Database;
import hw1.HeapPage;
import hw1.Tuple;
// something
/**
 * BufferPool manages the reading and writing of pages into memory from
 * disk. Access methods call into it to retrieve pages, and it fetches
 * pages from the appropriate location.
 * <p>
 * The BufferPool is also responsible for locking;  when a transaction fetches
 * a page, BufferPool which check that the transaction has the appropriate
 * locks to read/write the page.
 */
public class BufferPool {
	// key: tableId & pageID; value: list of transactionId
	HashMap<IdPair, List<Integer>> readLocks;
	// key: tableId & pageID; value: transactionId
	HashMap<IdPair, Integer> writeLocks;
	// key: tableId & pageID; value: whether the page is "dirty"
	HashMap<IdPair, Boolean> isDirty;
	// key: tableId & pageID; value: heapPage
	HashMap<IdPair, HeapPage> cache;
	int maxPages;
	
	
    /** Bytes per page, including header. */
    public static final int PAGE_SIZE = 4096;

    /** Default number of pages passed to the constructor. This is used by
    other classes. BufferPool should use the numPages argument to the
    constructor instead. */
    public static final int DEFAULT_PAGES = 50;

    /**
     * Creates a BufferPool that caches up to numPages pages.
     *
     * @param numPages maximum number of pages in this buffer pool.
     */
    public BufferPool(int numPages) {
    	this.maxPages = numPages;
        this.readLocks = new HashMap<IdPair, List<Integer>> ();
        this.writeLocks = new HashMap<IdPair, Integer>();
        this.isDirty = new HashMap<IdPair, Boolean>();
        this.cache = new HashMap<IdPair, HeapPage>();
    }

    /**
     * Retrieve the specified page with the associated permissions.
     * Will acquire a lock and may block if that lock is held by another
     * transaction.
     * <p>
     * The retrieved page should be looked up in the buffer pool.  If it
     * is present, it should be returned.  If it is not present, it should
     * be added to the buffer pool and returned.  If there is insufficient
     * space in the buffer pool, an page should be evicted and the new page
     * should be added in its place.
     *
     * @param tid the ID of the transaction requesting the page
     * @param tableId the ID of the table with the requested page
     * @param pid the ID of the requested page
     * @param perm the requested permissions on the page
     */
    public HeapPage getPage(int tid, int tableId, int pid, Permissions perm)
        throws Exception {
        // your code here
    	
    	IdPair idPair = new IdPair (tableId, pid);
    	HeapPage thePage;
    	//if already in cache
		if (this.cache.containsKey(idPair)) {
    		thePage = this.cache.get(idPair);
    	} else {
    	//get Heappage
    		
    		Catalog catalog = Database.getCatalog();
    		thePage = catalog.getDbFile(tableId).readPage(pid);
    		if (this.cache.size() == this.maxPages) {
    			evictPage();
    		}
    		this.cache.put(idPair, thePage);
    		this.isDirty.put(idPair, false);
    	}
    	
		if(perm.permLevel == 1) {//write
			//not hold by wirte lock
			if (!this.writeLocks.containsKey(idPair)){
				//not hold by readLock
				if (!this.readLocks.containsKey(idPair)) {
					this.writeLocks.put(idPair, tid);
					return thePage; 
				//already hold by readLock, upgrade this.readLocks.size() == 1
				} else if(this.readLocks.containsKey(idPair)) {
					for(int i = 0; i < this.readLocks.get(idPair).size(); i++) {
						if (this.readLocks.get(idPair).get(i).equals(tid)) {
							this.writeLocks.put(idPair, tid);
						}
					}
					return thePage;
				} else {
					System.out.println("this page is held by another transaction");
					throw new Exception();
				}
			// hold by wirteLock but itself
			} else if (this.writeLocks.containsKey(idPair) && this.writeLocks.get(idPair) == tid) {
				return thePage;
			} else {
				System.out.println("this page is held by another transaction");
				throw new Exception();
			}
		}else if (perm.permLevel == 0) {//read
			if (this.writeLocks.containsKey(idPair) && this.writeLocks.get(idPair).equals(tid)
					|| !this.writeLocks.containsKey(idPair)) {
				
				if (this.readLocks.containsKey(idPair)) {
					//check whether already exist
					for(int i = 0; i < this.readLocks.get(idPair).size(); i++) {
						if (this.readLocks.get(idPair).get(i).equals(tid)) {
							return thePage;
						}
						this.readLocks.get(idPair).add(tid);
					}
					//this.cache.put(idPair, thePage);
				} else {
					ArrayList<Integer> listOfTrans = new ArrayList<Integer>();
					listOfTrans.add(tid);
					this.readLocks.put(idPair, listOfTrans);
					//this.cache.put(idPair, thePage);
				}	
				return thePage;  
			} else {
				System.out.println("this page is held by another write%read lock");
				throw new Exception();
			} 	
		} else {
			System.out.println("Unknown transaction");
			throw new Exception();
		}
			
    }

    /**
     * Releases the lock on a page.
     * Calling this is very risky, and may result in wrong behavior. Think hard
     * about who needs to call this and why, and why they can run the risk of
     * calling it.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param tableID the ID of the table containing the page to unlock
     * @param pid the ID of the page to unlock
     */
    public  void releasePage(int tid, int tableId, int pid) {
        // your code here
    	IdPair index = new IdPair(tableId,pid);
    	if (readLocks.get(index) != null) {
    		readLocks.get(index).remove(Integer.valueOf(tid));
    	}	
    	writeLocks.remove(index);
    }

    /** Return true if the specified transaction has a lock on the specified page */
    public boolean holdsLock(int tid, int tableId, int pid) {
        // your code here
    	IdPair idPair = new IdPair(tableId, pid);
    	if (this.writeLocks.get(idPair) != null && this.writeLocks.get(idPair).equals(tid)) {
    		return true;
    	} 
    	if (this.readLocks.get(idPair) != null) {
    		for (int t : this.readLocks.get(idPair)) {
    			if (t == tid) {
    				return true;
    			}
    		}
    	}
        return false;
    }

    /**
     * Commit or abort a given transaction; release all locks associated to
     * the transaction. If the transaction wishes to commit, write
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param commit a flag indicating whether we should commit or abort
     */
    public   void transactionComplete(int tid, boolean commit)
        throws IOException {
        // your code here
    	HashSet<IdPair> pages = new HashSet<>();
    	for (Map.Entry<IdPair, List<Integer>> entry : readLocks.entrySet()) {
    		if (entry.getValue().contains(Integer.valueOf(tid))) {
    			entry.getValue().remove(Integer.valueOf(tid));
    		}
    	}
    	Iterator<Map.Entry<IdPair,Integer>> iter = writeLocks.entrySet().iterator();
    	while (iter.hasNext()) {
    		Map.Entry<IdPair, Integer> entry = iter.next();
    		if (entry.getValue().equals(Integer.valueOf(tid))) {
    			pages.add(entry.getKey());
    			iter.remove();
    		}
    	}
    	if (commit) {
    		for (IdPair index : pages) {
    			HeapPage thePage = cache.get(index);
    			Database.getCatalog().getDbFile(index.tid).writePage(thePage);
    			isDirty.put(index, false);
    		}
    	} else {
    		for (IdPair index : pages) {
    			flushPage(index.tid,index.pid);
    		}
    	}
    }

    /**
     * Add a tuple to the specified table behalf of transaction tid.  Will
     * acquire a write lock on the page the tuple is added to. May block if the lock cannot 
     * be acquired.
     * 
     * Marks any pages that were dirtied by the operation as dirty
     *
     * @param tid the transaction adding the tuple
     * @param tableId the table to add the tuple to
     * @param t the tuple to add
     */
    public  void insertTuple(int tid, int tableId, Tuple t)
        throws Exception {
        // your code here
    	int pid = t.getPid();
    	IdPair idPair = new IdPair(tableId, pid);
    	HeapPage thePage = this.cache.get(idPair);
    	if (this.writeLocks.get(idPair) == tid) {
    		this.isDirty.put(idPair, true);
        	thePage.addTuple(t);
    	} else {
    		throw new Exception();
    	}
    	
    }

    /**
     * Remove the specified tuple from the buffer pool.
     * Will acquire a write lock on the page the tuple is removed from. May block if
     * the lock cannot be acquired.
     *
     * Marks any pages that were dirtied by the operation as dirty.
     *
     * @param tid the transaction adding the tuple.
     * @param tableId the ID of the table that contains the tuple to be deleted
     * @param t the tuple to add
     */
    public  void deleteTuple(int tid, int tableId, Tuple t)
        throws Exception {
        // your code here
    	int pid = t.getPid();
    	IdPair idPair = new IdPair(tableId, pid);
    	HeapPage thePage = getPage(tid, tableId, pid, Permissions.READ_WRITE);//????
    	this.isDirty.put(idPair, true);
    	thePage.deleteTuple(t);
    }

    private synchronized  void flushPage(int tableId, int pid) throws IOException {
        // your code here
    	IdPair index = new IdPair(tableId, pid);
    	HeapPage newPage = Database.getCatalog().getDbFile(tableId).readPage(pid);
    	cache.put(index, newPage);
    	isDirty.put(index, false);
    }

    /**
     * Discards a page from the buffer pool.
     * Flushes the page to disk to ensure dirty pages are updated on disk.
     */
    private synchronized  void evictPage() throws Exception {
        // your code here
    	Iterator<Map.Entry<IdPair,Boolean>> iter = isDirty.entrySet().iterator();
    	while (iter.hasNext()) {
    		Map.Entry<IdPair,Boolean> entry = iter.next();
    		if (entry.getValue() == false) {
    			cache.remove(entry.getKey());
    			iter.remove();
    			return;
    		}
    	}
    	throw new Exception();
    }
    
    
    class IdPair {
    	int tid;
    	int pid;
    	public IdPair(int t, int p) {
    		this.tid = t;
    		this.pid = p;
    	}
    	
    	@Override
    	public boolean equals(Object obj) {
    		if (this == obj) {
    			return true;
    		}
    		if(!(obj instanceof IdPair)) {
    			return false;
    		}
    		IdPair another = (IdPair)obj;
    		return this.tid == another.tid && this.pid == another.pid;
    		
    	}
    	@Override
    	public int hashCode() {
    		return this.tid * 31 + this.pid;
    	}
    }

}
