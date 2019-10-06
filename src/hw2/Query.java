package hw2;

import java.util.ArrayList;
import java.util.List;

import hw1.Catalog;
import hw1.Database;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
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
				String name = leftEx.getColumnName();
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
		if(items.get(0).toString() != "*") {
			ArrayList<Integer> newFields = new ArrayList<>();
			for (SelectItem item : items) {
				String itemName = item.toString();
				newFields.add(cur.getDesc().nameToId(itemName));
			}
			cur = cur.project(newFields);
		}
		
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
