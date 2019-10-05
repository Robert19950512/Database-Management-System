package hw2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hw1.Field;
import hw1.IntField;
import hw1.RelationalOperator;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;

/**
 * A class to perform various aggregations, by accepting one tuple at a time
 * @author Doug Shook
 *
 */
public class Aggregator {
	private AggregateOperator operator;
	private boolean isGroupBy;
	private TupleDesc td;
	
	private ArrayList<Tuple> results;
	Map<Field,Integer> numMap;
	Map<Field, Tuple> groupMap;
	Tuple theTuple;
	int theNum;
	public Aggregator(AggregateOperator o, boolean groupBy, TupleDesc td) {
		//your code here
		this.operator = o;
		this.isGroupBy = groupBy;
		this.td = td;
		results  = new ArrayList<>();
		if (groupBy == true) {
			numMap = new HashMap<>();
			groupMap = new HashMap<>();
		}else {
			theTuple = new Tuple(td);
			this.results.add(theTuple);
			theNum = 0;
		}
		

	}

	/**
	 * Merges the given tuple into the current aggregation
	 * @param t the tuple to be aggregated
	 */
	public void merge(Tuple t) {
		//your code here
		Tuple cur = null;
		int curIndex = 0;
		int curNum = 0;
		if (isGroupBy == true) {
			Field temp = t.getField(0);
			if (groupMap.containsKey(temp)) {
				cur = groupMap.get(t.getField(0));
			}else {
				cur = new Tuple(t.getDesc());
				cur.setField(0, t.getField(0));
				this.results.add(cur);
				groupMap.put(temp, cur);
			}
			if (numMap.containsKey(t.getField(0))) {
				numMap.put(t.getField(0), numMap.get(t.getField(0)) + 1);
			}else {
				numMap.put(t.getField(0), 1);
			}
			curIndex = 1;
			curNum = numMap.get(t.getField(0));
		} else {
			cur = this.theTuple;
			curNum = this.theNum;
			
		}
		//to be implement
		switch (operator) {
        case MAX:
            if (cur.getField(curIndex) == null) {
            	cur.setField(curIndex, t.getField(curIndex));
            	
            }else {
            	if (cur.getField(curIndex).compare(RelationalOperator.LT, t.getField(curIndex))) {
            		cur.setField(curIndex, t.getField(curIndex));
            	}
            }
        	break;
        case MIN:
        	if (cur.getField(curIndex) == null) {
            	cur.setField(curIndex, t.getField(curIndex));
            }else {
            	if (cur.getField(curIndex).compare(RelationalOperator.GT, t.getField(curIndex))) {
            		cur.setField(curIndex, t.getField(curIndex));
            	}
            }
        	break;
        case AVG:
        	if (cur.getField(curIndex) == null) {
            	cur.setField(curIndex, t.getField(curIndex));
            }else {
            	int total = cur.getField(curIndex).hashCode() * (curNum - 1) + t.getField(curIndex).hashCode();
            	cur.setField(curIndex, new IntField(total / curNum));
            }
        	break;
        case COUNT:
        	if (cur.getField(curIndex) == null) {
            	cur.setField(curIndex, new IntField(1));
            }else {
            	cur.setField(curIndex, new IntField(cur.getField(curIndex).hashCode() + 1));
            }
        	break;
        case SUM:
        	if (cur.getField(curIndex) == null) {
            	cur.setField(curIndex, t.getField(curIndex));
            }else {
            	cur.setField(curIndex, new IntField(cur.getField(curIndex).hashCode() + t.getField(curIndex).hashCode()));
            }
        break;
        
        }
		
		
		
	}
	
	/**
	 * Returns the result of the aggregation
	 * @return a list containing the tuples after aggregation
	 */
	public ArrayList<Tuple> getResults() {
		//your code here
		return this.results;
	}

}
