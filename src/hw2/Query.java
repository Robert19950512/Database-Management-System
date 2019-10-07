package hw2;
/*
 * Student 1 name: Fa Long (id:462512)
 * Student 2 name: Zhuo Wei (id: 462473)
 * Date: Oct 5th, 2019
 */
import java.util.ArrayList;
import java.util.List;
import hw1.Catalog;
import hw1.Database;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

public class Query {

	private String q;
	
	public Query(String q) {
		this.q = q;
	}
	
	public Relation execute()  {
		Statement statement = null;
		try {
			statement = CCJSqlParserUtil.parse(q);
		} catch (JSQLParserException e) {
			System.out.println("Unable to parse query");
			e.printStackTrace();
		}
		Select selectStatement = (Select) statement;
		PlainSelect sb = (PlainSelect)selectStatement.getSelectBody();
		//your code here
		//execute from (join), then where, then select
		List<String> tableNames = new ArrayList<>();
		TablesNamesFinder tf = new TablesNamesFinder();
		tableNames = tf.getTableList(selectStatement);
		Relation cur = getRelationFromName(tableNames.get(0));
		// join
		List<Join> joins = sb.getJoins();
		if (joins != null) {
			for (Join jo : joins) {
				Table right = (Table) jo.getRightItem();
				Relation toJoin = getRelationFromTable(right);
				EqualsTo joinExpression = (EqualsTo) jo.getOnExpression();		
				Column leftEx = (Column) joinExpression.getLeftExpression();
				Column rightEx = (Column) joinExpression.getRightExpression();
				if (leftEx.getTable().getName().equals(right.getName())) {
					// left and right's column are reversed, reverse it back
					Column temp = leftEx;
					leftEx = rightEx;
					rightEx = temp;
				}
				int leftId = cur.getDesc().nameToId(leftEx.getColumnName());
				int rightId = toJoin.getDesc().nameToId(rightEx.getColumnName());
				cur = cur.join(toJoin, leftId, rightId);
				
			}
		}
		// where query
		Expression whereExp = sb.getWhere();
		if (whereExp != null) {
			WhereExpressionVisitor wv = new WhereExpressionVisitor();
			whereExp.accept(wv);
			cur = cur.select(cur.getDesc().nameToId(wv.getLeft()), wv.getOp(), wv.getRight());
			
		}
		// project
		List<SelectItem> items = sb.getSelectItems();
		if (items != null && items.size() != 0 && items.get(0).toString() != "*") {
			SelectExpressionItem curExp = (SelectExpressionItem)items.get(0);
			if (curExp.getExpression().getClass() == Function.class) {
				// Aggregate without group by
				//relation should have only one column
				Function agFunction = (Function)curExp.getExpression();
				AggregateExpressionVisitor aEV = new AggregateExpressionVisitor();
				agFunction.accept(aEV);
				ArrayList<Integer> newField = new ArrayList<>();
				if (aEV.getColumn().equals("*")) {
					// if it's *, just select the first column
					newField.add(0);
				}else {
					newField.add(cur.getDesc().nameToId(aEV.getColumn()));
				}
				cur = cur.project(newField);
				cur = cur.aggregate(aEV.getOp(), false);
			}else if (items.size() == 2) {
				curExp = (SelectExpressionItem)items.get(1);
				if (curExp.getExpression().getClass() == Function.class) {
					//aggregate with group by
					Function agFunction = (Function)curExp.getExpression();
					AggregateExpressionVisitor aEV = new AggregateExpressionVisitor();
					agFunction.accept(aEV);
					List<Expression> groupBy = sb.getGroupByColumnReferences();
					Column gbColumn = (Column)groupBy.get(0);
					ArrayList<Integer> newField = new ArrayList<>();
					newField.add(cur.getDesc().nameToId(gbColumn.getColumnName()));
					newField.add(cur.getDesc().nameToId(aEV.getColumn()));
					cur = cur.project(newField);
					cur = cur.aggregate(aEV.getOp(), true);
				}
			}else {
				ArrayList<Integer> newFields = new ArrayList<>();
				for (SelectItem item : items) {
					String itemName = item.toString();
					newFields.add(cur.getDesc().nameToId(itemName));
				}
				cur = cur.project(newFields);
			}
		}
		// rename
		
		
		return cur;
		
	}
	public Relation getRelationFromTable(Table tb) {
		Catalog c = Database.getCatalog();
		Relation cur = new Relation(c.getDbFile(c.getTableId(tb.getName())).getAllTuples(),c.getDbFile(c.getTableId(tb.getName())).getTupleDesc());
		return cur;
		
	}
	public Relation getRelationFromName(String name) {
		Catalog c = Database.getCatalog();
		Relation cur = new Relation(c.getDbFile(c.getTableId(name)).getAllTuples(),c.getDbFile(c.getTableId(name)).getTupleDesc());
		return cur;
		
	}
}
