package org.vcell.sybil.util.table;

/*   StatementsTableModel  --- by Oliver Ruebenacker, UCHC --- February 2008 to March 2009
 *   TableModel for a Table displaying the selected Statements of a graph
 */

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.vcell.sybil.rdf.RDFToken;
import org.vcell.sybil.rdf.RDFToken.ObjectToken;
import org.vcell.sybil.rdf.RDFToken.PredicateToken;
import org.vcell.sybil.rdf.RDFToken.SubjectToken;

import com.hp.hpl.jena.rdf.model.Statement;

public abstract class StatementsTableModel extends AbstractTableModel implements TableModel {

	private static final long serialVersionUID = -286488757217458964L;

	public static class Row {
		protected Statement statement;
		protected SubjectToken subjectToken;
		protected PredicateToken predicateToken;
		protected ObjectToken objectToken;
		
		public Row(Statement statementNew) {
			statement = statementNew;
			subjectToken = new SubjectToken(statementNew);
			predicateToken = new PredicateToken(statementNew);
			objectToken = new ObjectToken(statementNew);
		}
		
		public Statement statement() { return statement; }
		public SubjectToken subject() { return subjectToken; }
		public PredicateToken predicate() { return predicateToken; }
		public ObjectToken object() { return objectToken; }
	}
	
	public static class RowComparator implements Comparator<Row> {
		protected Comparator<Statement> stateComp;
		public RowComparator(Comparator<Statement> stateCompNew) { stateComp = stateCompNew; }
		public int compare(Row row1, Row row2) { 
			return stateComp.compare(row1.statement(), row2.statement());
		}
		
	}
	
	protected Vector<Row> rows = new Vector<Row>();
	protected Comparator<Row> comparator;
	
	public StatementsTableModel(Comparator<Statement> comparatorNew) { 
		comparator = new RowComparator(comparatorNew); 
	}
	
	public StatementsTableModel(Comparator<Statement> comparatorNew,
			Collection<? extends Statement> statementsNew) {
		comparator = new RowComparator(comparatorNew);
		rows = new Vector<Row>();
		setStatements(statementsNew);
	}
	
	public void setStatements(Collection<? extends Statement> statementsNew) {
		rows = new Vector<Row>();
		for(Statement statement : statementsNew) {
			rows.add(new Row(statement));
		}
		Collections.sort(rows, comparator);
		fireTableDataChanged();
	}
	
	public int getRowCount() { return rows.size(); }
	
	public Class<? extends RDFToken> getColumnClass(int iCol) { return RDFToken.class; }
	
	public void setValueAt(Object valueNew, int rowInd, int colInd) { }

}
