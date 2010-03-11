package org.vcell.sybil.util.table;

/*   SelectedStatementsTableModel  --- by Oliver Ruebenacker, UCHC --- February 2008 to March 2009
 *   TableModel for a Table displaying the selected Statements of a graph
 */

import java.util.Collection;
import java.util.Comparator;

import org.vcell.sybil.rdf.RDFToken;

import com.hp.hpl.jena.rdf.model.Statement;

public class StatementsPOTableModel extends StatementsTableModel {

	private static final long serialVersionUID = 5025542954275851530L;
	
	public static final int iColumnPredicate = 0;
	public static final int iColumnObject = 1;
	public static final int columnCount = 2;

	public StatementsPOTableModel(Comparator<Statement> comparatorNew) { super(comparatorNew); }
	
	public StatementsPOTableModel(Comparator<Statement> comparatorNew, 
			Collection<? extends Statement> statementsNew) { 
		super(comparatorNew, statementsNew); 
	}
	
	public int getColumnCount() { return columnCount; }

	public String getColumnName(int colInd) { 
		switch(colInd) {
		case iColumnPredicate: return "Property";
		case iColumnObject: return "Value";
		}
		return "Statements"; 
	}
	
	public RDFToken getValueAt(int rowInd, int colInd) { 
		switch(colInd) {
		case iColumnPredicate: return rows.get(rowInd).predicate();
		case iColumnObject: return rows.get(rowInd).object();
		}
		return null;
	}

}
