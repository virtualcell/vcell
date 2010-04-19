package org.vcell.sybil.models.bpimport.table;

/*   Row  --- by Oliver Ruebenacker, UCHC --- June 2008 to November 2009
 *   A row of a process table
 */

import java.util.HashMap;
import java.util.Map;

import org.vcell.sybil.models.bpimport.edges.ProcessEdge;
import org.vcell.sybil.models.bpimport.table.options.CellLiteralOption;
import org.vcell.sybil.models.bpimport.table.options.CellOption;
import org.vcell.sybil.models.bpimport.table.options.CellThingOption;
import org.vcell.sybil.models.sbbox.SBBox;
import com.hp.hpl.jena.rdf.model.Literal;

public class Row implements ProcessEdge {
	
	protected ProcessTableModel table;
	protected Map<Column, Cell> cells = new HashMap<Column, Cell>();
	
	protected SBBox.Species species;
	protected SBBox.Stoichiometry stoichiometry;
	
	public Row(ProcessTableModel tableNew) {
		table = tableNew;
		for(Column col : tableNew.cols()) { cells.put(col, new Cell(tableNew, this, col)); }
	}

	public Cell cell(Column col) { return cells.get(col); }
	public Cell cell(int colInd) { return cell(table.cols().get(colInd)); }
	public void setCell(Cell cellNew, int colInd) { cells.put(table.cols().get(colInd), cellNew); }

	public CellOption selected(Column col) {
		CellOption selected = null;
		Cell cell = cell(col);
		if(cell != null) { selected = cell.selected(); }
		return selected;
	}
	
	public void addEdge(ProcessEdge edge) {
		addSBOption(edge.process(), table.colManager().colProcess());
		addSBOption(edge.participant(), table.colManager().colParticipant());
		species = edge.species();
		addSBOption(edge.entity(), table.colManager().colEntity());
		addSBOption(edge.entityType(), table.colManager().colEntityType());
		stoichiometry = edge.stoichiometry();
		addFloatOption(edge.stoichiometricCoeff(), table.colManager().colStoichCoeff());
		addSBOption(edge.substance(), table.colManager().colSubstance());
		addSBOption(edge.location(), table.colManager().colLocation());
	}

	public void addFloatOption(float num, Column col) {
		Literal literal = table.box().getRdf().createTypedLiteral(num);
		cell(col).add(new CellLiteralOption(literal), false, true);
	}
	
	public <T extends SBBox.NamedThing> void addSBOption(T thing, Column col) {
		if(thing != null) {
			cell(col).add(new CellThingOption<T>(thing), false, true);
		}
	}
	
	public SBBox.NamedThing selectedThing(Column col) {
		SBBox.NamedThing sbView = null;
		CellOption selected = selected(col);
		if(selected instanceof CellThingOption<?>) { sbView = ((CellThingOption<?>) selected).thing(); }
		return sbView;
	}

	public SBBox.Process process() { 
		return (SBBox.Process) selectedThing(table.colManager().colProcess());
	}

	public SBBox.Participant participant() {
		return (SBBox.Participant) selectedThing(table.colManager().colParticipant());
	}

	public SBBox.Species species() { 
		if(species == null) { species = table.box().factories().speciesFactory().createAnonymous(); }
		return species; 
	}

	public SBBox.Substance entity() {
		return (SBBox.Substance) selectedThing(table.colManager().colEntity());
	}

	public SBBox.RDFType entityType() {
		return (SBBox.RDFType) selectedThing(table.colManager().colEntityType());
	}

	public SBBox.Substance substance() {
		return (SBBox.Substance) selectedThing(table.colManager().colSubstance());
	}
	
	public SBBox.Location location() {
		return (SBBox.Location) selectedThing(table.colManager().colLocation());
	}

	public SBBox.Stoichiometry stoichiometry() { 
		if(stoichiometry == null) { stoichiometry = table.box().factories().stoichiometryFactory().createAnonymous(); }
		return stoichiometry; 
	}

	public float stoichiometricCoeff() {
		float sc = (float) 1.0;
		CellOption selected = selected(table.colManager().colStoichCoeff());
		if(selected instanceof CellLiteralOption) {
			sc = ((CellLiteralOption) selected).node().getFloat();
		}
		return sc;
	}

}