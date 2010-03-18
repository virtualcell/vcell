package org.vcell.sybil.models.bpimport.table.groups;

/*   ByLiteral  --- by Oliver Ruebenacker, UCHC --- July 2008 to July 2009
 *   Assigns cells into groups by (Literal) value
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.Column;
import org.vcell.sybil.models.bpimport.table.ColumnCellIter;
import org.vcell.sybil.models.bpimport.table.ProcessTableModel;
import org.vcell.sybil.models.bpimport.table.options.CellLiteralOption;
import org.vcell.sybil.models.bpimport.table.options.CellOption;
import org.vcell.sybil.util.iterators.FilterIter;
import com.hp.hpl.jena.rdf.model.Literal;

public class GroupByLiteral extends GroupManager {

	public static class ValueCellGroup extends CellGroup implements FilterIter.Tester<Cell> {

		protected Literal value;
		protected Column column;
		
		public ValueCellGroup(ProcessTableModel tableNew, Column columnNew, 
				Collection<? extends CellOption> defaultOptions, Literal valueNew) {
			super(tableNew, defaultOptions);
			column = columnNew;
			value = valueNew;
		}

		public boolean accepts(Cell cell) {
			CellOption.Selection selected = cell.selected();
			if (selected instanceof CellLiteralOption) {
				Literal node = ((CellLiteralOption) selected).node();
				if(node != null) { return node.equals(value); }
			} 
			return false;
		}

		public Iterator<Cell> cellIter() {
			return new FilterIter<Cell>(new ColumnCellIter(table, column), this);
		}
		
	}
	
	public GroupByLiteral(Collection<? extends CellOption> defaultOptions) { super(defaultOptions); }
	
	protected Map<Literal, CellGroup> nodeToGroup = new HashMap<Literal, CellGroup>();
	
	public CellGroup determineGroup(Cell cell) {
		CellOption.Selection selected = cell.selected();
		if(selected instanceof CellLiteralOption) {
			Literal node = ((CellLiteralOption) selected).node();
			CellGroup group = nodeToGroup.get(node);
			if(group == null) {
				group = new ValueCellGroup(cell.table(), cell.column(), defaultOptions, node);
				nodeToGroup.put(node, group);
			}
			return group;			
		}
		return defaultGroup(cell);
	}
	
	public CellGroup defaultGroup(Cell cell) { 
		return new GroupIndividual.SingleCellGroup(cell, defaultOptions); 
	}
	
}