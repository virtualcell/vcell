package org.vcell.sybil.models.bpimport.table.groups;

/*   ByResource  --- by Oliver Ruebenacker, UCHC --- July 2008 to November 2009
 *   Assigns cells into groups by value
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.Column;
import org.vcell.sybil.models.bpimport.table.ColumnCellIter;
import org.vcell.sybil.models.bpimport.table.ProcessTableModel;
import org.vcell.sybil.models.bpimport.table.options.CellOption;
import org.vcell.sybil.models.bpimport.table.options.CellThingOption;
import org.vcell.sybil.util.iterators.FilterIter;

import com.hp.hpl.jena.rdf.model.Resource;

public class GroupByResource extends GroupManager {

	public static class ValueCellGroup extends CellGroup implements FilterIter.Tester<Cell> {

		protected Resource value;
		protected Column column;
		
		public ValueCellGroup(ProcessTableModel tableNew, Column columnNew, 
				Collection<? extends CellOption> defaultOptions, Resource valueNew) {
			super(tableNew, defaultOptions);
			column = columnNew;
			value = valueNew;
		}

		public boolean accepts(Cell cell) {
			CellOption.Selection selected = cell.selected();
			if (selected instanceof CellThingOption<?>) {
				Resource node = ((CellThingOption<?>) selected).thing().resource();
				if(node != null) { return node.equals(value); }
			}
			return false;
		}

		@Override
		public Iterator<Cell> cellIter() {
			return new FilterIter<Cell>(new ColumnCellIter(table, column), this);
		}
		
	}
	
	protected Map<Resource, CellGroup> nodeToGroup = new HashMap<Resource, CellGroup>();
	
	public GroupByResource(Collection<? extends CellOption> defaultOptions) { super(defaultOptions); }
	
	@Override
	public CellGroup determineGroup(Cell cell) {
		CellOption.Selection selected = cell.selected();
		if(selected instanceof CellThingOption<?>) {
			Resource node = ((CellThingOption<?>) selected).thing().resource();
			CellGroup group = nodeToGroup.get(node);
			if(group == null) {
				group = new ValueCellGroup(cell.table(), cell.column(), defaultOptions, node);
				nodeToGroup.put(node, group);
			}
			return group;			
		}
		return defaultGroup(cell);
	}
	
	@Override
	public CellGroup defaultGroup(Cell cell) { 
		return new GroupIndividual.SingleCellGroup(cell, defaultOptions); 
	}
	
}