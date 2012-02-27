package util;

import java.util.LinkedList;
import java.util.List;

public class Table<T> {

	public List<Row<T>> rows = new LinkedList<Table.Row<T>>();
	
	public void addRow(Row<T> row) {
		this.rows.add(row);
	}
	
	public static class Row<T> {
		public List<Column<T>> columns = new LinkedList<Table.Column<T>>();
		
		public void addColumn(Column<T> column) {
			this.columns.add(column);
		}
		
		
	}
	
	public static class Column<T> {

		public T value;
		
		public Column(T value) {
			this.value = value;
		}
		
	}
}
