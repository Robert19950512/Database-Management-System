package hw2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hw1.Field;
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
	Map<Type,Integer> numMap;
	Map<Type, Tuple> groupMap;
	Tuple theTuple;
	public Aggregator(AggregateOperator o, boolean groupBy, TupleDesc td) {
		//feed one tuple everytime
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
		}
		

	}

	/**
	 * Merges the given tuple into the current aggregation
	 * @param t the tuple to be aggregated
	 */
	public void merge(Tuple t) {
		//your code here
		Tuple cur = null;
		Field curVal = null;
		if (isGroupBy == true) {
			Field temp = t.getField(0);
			if (groupMap.containsKey(temp)) {
				cur = groupMap.get(t.getField(0));
			}else {
				cur = new Tuple(t.getDesc());
			}
		} else {
			cur = this.theTuple;
		}
		//to be implement
		switch (operator) {
        case MAX:
            
        	break;
        case MIN:
            
        	break;
        case AVG:
            
        	break;
        case COUNT:
            
        	break;
        case SUM:
            
        	break;
        
        }
		
		
	}
	
	/**
	 * Returns the result of the aggregation
	 * @return a list containing the tuples after aggregation
	 */
	public ArrayList<Tuple> getResults() {
		//your code here
		return null;
	}

}
