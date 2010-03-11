package org.vcell.sybil.util.table;

/*   StatementsTableSPModel  --- by Oliver Ruebenacker, UCHC --- February 2008 to March 2009
 *   TableModel for a Table displaying the selected Statements of a graph
 */

import java.util.Collection;
import java.util.Comparator;

import org.vcell.sybil.rdf.RDFToken;
import org.vcell.sybil.rdf.RDFToken.PredicateToken;
import org.vcell.sybil.rdf.RDFToken.SubjectToken;

import com.hp.hpl.jena.rdf.model.Statement;

public class StatementsSPTableModel extends StatementsTableModel {

	private static final long serialVersionUID = 5025542954275851530L;

	public static final int iColSubject = 0;
	public static final int iColPredicate = 1;
	
	public StatementsSPTableModel(Comparator<Statement> comparatorNew) { super(comparatorNew); }
	
	public StatementsSPTableModel(Comparator<Statement> comparatorNew, 
			Collection<? extends Statement> statementsNew) { 
		super(comparatorNew, statementsNew); 
	}
	
	public int getColumnCount() { return 2; }

	public Class<? extends RDFToken> getColumnClass(int iCol) { 
		switch(iCol) {
		case iColSubject : return SubjectToken.class;
		case iColPredicate : return PredicateToken.class;
		}
		return RDFToken.class; 
	}
	
	public String getColumnName(int iCol) { 
		switch(iCol) {
		case iColSubject: return "Subject";
		case iColPredicate: return "Predicate";
		}
		return "Statements"; 
	}
	
	public RDFToken getValueAt(int iRow, int iCol) { 
		switch(iCol) {
		case iColSubject: return rows.get(iRow).subject();
		case iColPredicate: return rows.get(iRow).predicate();
		}
		return null;
	}

}
