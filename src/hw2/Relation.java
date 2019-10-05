package hw2;

import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;

/**
 * This class provides methods to perform relational algebra operations. It will be used
 * to implement SQL queries.
 * @author Doug Shook
 *
 */
public class Relation {

	private ArrayList<Tuple> tuples;
	private TupleDesc td;
	
	public Relation(ArrayList<Tuple> l, TupleDesc td) {
		//your code here
		this.tuples = l;
		this.td = td;
	}
	
	/**
	 * This method performs a select operation on a relation
	 * @param field number (refer to TupleDesc) of the field to be compared, left side of comparison
	 * @param op the comparison operator
	 * @param operand a constant to be compared against the given column
	 * @return
	 */
	public Relation select(int field, RelationalOperator op, Field operand) {
		//your code here
		ArrayList<Tuple> newTupleList = new ArrayList<>();
		for (Tuple tp : this.tuples) {
			if (tp.getField(field).compare(op, operand)) {
				newTupleList.add(tp);
			}
		}
		this.tuples = newTupleList;
		return this;
	}
	
	/**
	 * This method performs a rename operation on a relation
	 * @param fields the field numbers (refer to TupleDesc) of the fields to be renamed
	 * @param names a list of new names. The order of these names is the same as the order of field numbers in the field list
	 * @return
	 */
	public Relation rename(ArrayList<Integer> fields, ArrayList<String> names) {
		//your code here
		int maxIndex = this.td.numFields() - 1;
		for (Integer num : fields) {
			if (num.intValue() > maxIndex) {
				return null;
			}
		}
		Type[] newType = new Type[this.td.numFields()];
		String[] newFieldAr = new String[newType.length];
		for (int i = 0 ; i < newType.length ; i ++) {
			newType[i] = this.td.getType(i);
			newFieldAr[i] = this.td.getFieldName(i);
		}
		for (Integer num : fields) {
			newFieldAr[num.intValue()] = names.get(0);
			names.remove(0);
		}
		
		TupleDesc newDesc = new TupleDesc(newType,newFieldAr);
		this.td = newDesc;
		for (Tuple tp : this.tuples) {
			tp.setDesc(newDesc);
		}
		return this;
	}
	
	/**
	 * This method performs a project operation on a relation
	 * @param fields a list of field numbers (refer to TupleDesc) that should be in the result
	 * @return
	 */
	public Relation project(ArrayList<Integer> fields) {
		//your code here
		int maxIndex = this.td.numFields() - 1;
		for (Integer num : fields) {
			if (num.intValue() > maxIndex || num.intValue() < 0) {
				return null;
			}
		}
		Type[] newType = new Type[fields.size()];
		String[] newFieldAr = new String[newType.length];
		for (int i = 0 ; i < fields.size() ; i ++) {
			newType[i] = this.td.getType(fields.get(i));
			newFieldAr[i] = this.td.getFieldName(fields.get(i));
		}
		TupleDesc newDesc = new TupleDesc(newType,newFieldAr);
		ArrayList<Tuple> newTupleList = new ArrayList<>();
		for (Tuple oldTp : this.getTuples()) {
			Tuple newTp = new Tuple(newDesc);
			for (int i = 0 ; i < oldTp.getDesc().numFields() ; i ++) {
				// means current column is preserved
				if (fields.contains(Integer.valueOf(i))) {
					newTp.setField(newDesc.nameToId(oldTp.getDesc().getFieldName(i)), oldTp.getField(i));
				}
			}
			newTupleList.add(newTp);
		}
		
		
		return new Relation(newTupleList, newDesc);
	}
	
	/**
	 * This method performs a join between this relation and a second relation.
	 * The resulting relation will contain all of the columns from both of the given relations,
	 * joined using the equality operator (=)
	 * @param other the relation to be joined
	 * @param field1 the field number (refer to TupleDesc) from this relation to be used in the join condition
	 * @param field2 the field number (refer to TupleDesc) from other to be used in the join condition
	 * @return
	 */
	public Relation join(Relation other, int field1, int field2) {
		//your code here
		//create the desc for new relation
		int newLength = this.td.numFields() + other.td.numFields();
		Type[] newType = new Type[newLength];
		String[] newFieldAr = new String[newLength];
		for (int i = 0 ; i < this.td.numFields() ; i ++) {
			newType[i] = this.getDesc().getType(i);
			newFieldAr[i] = this.getDesc().getFieldName(i);
		}
		for (int i = 0 ; i < other.getDesc().numFields() ; i ++) {
				newType[this.getDesc().numFields() + i  ] = other.getDesc().getType(i);
				newFieldAr[this.getDesc().numFields() + i ] = other.getDesc().getFieldName(i);
		}
		//finish creating new tuple desc
		TupleDesc newDesc = new TupleDesc(newType, newFieldAr);
		ArrayList<Tuple> newTupleList = new ArrayList<>(); 
		for (Tuple tup1 : this.getTuples()) {
			for (Tuple tup2 : other.getTuples()) {
				if (tup1.getField(field1).equals(tup2.getField(field2))) {
					Tuple temp = new Tuple(newDesc);
					for (int i = 0 ; i < tup1.getDesc().numFields();i++) {
						temp.setField(i, tup1.getField(i));
					}
					for (int i = 0 ; i < tup2.getDesc().numFields(); i++) {
						
						temp.setField(i , tup2.getField(i));;
						
					}
					newTupleList.add(temp);
				}
			}
		}
			
		return new Relation(newTupleList, newDesc);
	}
	
	/**
	 * Performs an aggregation operation on a relation. See the lab write up for details.
	 * @param op the aggregation operation to be performed
	 * @param groupBy whether or not a grouping should be performed
	 * @return
	 */
	public Relation aggregate(AggregateOperator op, boolean groupBy) {
		//your code here
		if (!groupBy) {
			if (this.getDesc().numFields() != 1) {
				return null;
			}
			if (this.getDesc().getType(0) == Type.STRING) {
				if (op == AggregateOperator.AVG || op == AggregateOperator.SUM) {
					return null;
				}
			}
		}else {
			if (this.getDesc().numFields() != 2) {
				return null;
			}
			if (this.getDesc().getType(1) == Type.STRING) {
				if (op == AggregateOperator.AVG || op == AggregateOperator.SUM) {
					return null;
				}
			}
		}
		Aggregator agg = new Aggregator (op, groupBy, this.getDesc());
		for (Tuple tuple : this.getTuples()) {
			agg.merge(tuple);
		}
		ArrayList<Tuple> newTupleList = agg.getResults();
		return new Relation(newTupleList, td);
	}
	
	public TupleDesc getDesc() {
		//your code here
		return this.td;
	}
	
	public ArrayList<Tuple> getTuples() {
		//your code here
		return this.tuples;
	}
	
	/**
	 * Returns a string representation of this relation. The string representation should
	 * first contain the TupleDesc, followed by each of the tuples in this relation
	 */
	public String toString() {
		//your code here
		StringBuilder sb = new StringBuilder();
		String desc = this.getDesc().toString();
		sb.append(desc);
		sb.append("\n");
		for (Tuple tp : this.getTuples()) {
			sb.append(tp.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
