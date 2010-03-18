package org.vcell.sybil.models.bpimport.table.options;

/*   CellLiteralOption  --- by Oliver Ruebenacker, UCHC --- July 2009 to November 2009
 *   An option to choose from in a cell, representing a Literal
 */

import com.hp.hpl.jena.rdf.model.Literal;

public class CellLiteralOption extends CellSelectableOption implements CellOption {

	protected Literal literal;
	protected String label;

	public CellLiteralOption(Literal literal) { 
		this.literal = literal;
		if(literal != null) { label = literal.getLexicalForm(); }
		else { label = "[none]"; }
	}
	
	public Literal node() { return literal; }

	public String label() { return label; }
	public String toString() { return label; }
	public boolean equals(Object o) {
		if(o instanceof CellLiteralOption) {
			return literal.equals(((CellLiteralOption) o).node());
		}
		return false;
	}
	
	public int hashCode() { return literal.hashCode(); }

	
}
