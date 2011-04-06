package org.vcell.sybil.util.table;

/*   StatementsSPOTableModel  --- by Oliver Ruebenacker, UCHC --- February 2008 to March 2009
 *   TableModel for a Table displaying statements
 */

import java.util.Collection;
import java.util.Comparator;

import org.vcell.sybil.rdf.RDFToken;
import org.vcell.sybil.rdf.RDFToken.ObjectToken;
import org.vcell.sybil.rdf.RDFToken.PredicateToken;
import org.vcell.sybil.rdf.RDFToken.SubjectToken;

import com.hp.hpl.jena.rdf.model.Statement;

public class StatementsSPOTableModel extends StatementsTableModel {

	private static final long serialVersionUID = 5025542954275851530L;

	public static final int iColSubject = 0;
	public static final int iColPredicate = 1;
	public static final int iColObject = 2;
	
	public StatementsSPOTableModel(Comparator<Statement> comparatorNew) { super(comparatorNew); }
	
	public StatementsSPOTableModel(Comparator<Statement> comparatorNew, 
			Collection<? extends Statement> statementsNew) { 
		super(comparatorNew, statementsNew); 
	}
	
	public int getColumnCount() { return 3; }

	@Override
	public Class<? extends RDFToken> getColumnClass(int iCol) { 
		switch(iCol) {
		case iColSubject : return SubjectToken.class;
		case iColPredicate : return PredicateToken.class;
		case iColObject : return ObjectToken.class;
		}
		return RDFToken.class; 
	}
	
	@Override
	public String getColumnName(int iCol) { 
		switch(iCol) {
		case iColSubject: return "Subject";
		case iColPredicate: return "Predicate";
		case iColObject: return "Object";
		}
		return "Statements"; 
	}
	
	public RDFToken getValueAt(int iRow, int iCol) { 
		switch(iCol) {
		case iColSubject: return rows.get(iRow).subject();
		case iColPredicate: return rows.get(iRow).predicate();
		case iColObject: return rows.get(iRow).object();
		}
		return null;
	}

}
