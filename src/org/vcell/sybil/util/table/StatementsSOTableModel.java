package org.vcell.sybil.util.table;

/*   SelectedStatementsTableModel  --- by Oliver Ruebenacker, UCHC --- February 2008 to March 2009
 *   TableModel for a Table displaying the selected Statements of a graph
 */

import java.util.Collection;
import java.util.Comparator;

import org.vcell.sybil.rdf.RDFToken;
import org.vcell.sybil.rdf.RDFToken.ObjectToken;
import org.vcell.sybil.rdf.RDFToken.SubjectToken;

import com.hp.hpl.jena.rdf.model.Statement;

public class StatementsSOTableModel extends StatementsTableModel {

	private static final long serialVersionUID = 5025542954275851530L;

	public static final int iColSubject = 0;
	public static final int iColObject = 1;
	
	public StatementsSOTableModel(Comparator<Statement> comparatorNew) { super(comparatorNew); }

	public StatementsSOTableModel(Comparator<Statement> comparatorNew, 
			Collection<? extends Statement> statementsNew) { 
		super(comparatorNew, statementsNew); 
	}
	
	public int getColumnCount() { return 2; }

	@Override
	public Class<? extends RDFToken> getColumnClass(int iCol) { 
		switch(iCol) {
		case iColSubject : return SubjectToken.class;
		case iColObject : return ObjectToken.class;
		}
		return RDFToken.class; 
	}
	
	@Override
	public String getColumnName(int iCol) { 
		switch(iCol) {
		case iColSubject: return "Subject";
		case iColObject: return "Object";
		default: return "Statements"; 

		}
	}
	
	public RDFToken getValueAt(int iRow, int iCol) { 
		switch(iCol) {
		case iColSubject: return rows.get(iRow).subject();
		case iColObject: return rows.get(iRow).object();
		default: return null;
		}
	}

}
