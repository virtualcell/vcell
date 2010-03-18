package org.vcell.sybil.models.bpimport.table;

/*   Cell  --- by Oliver Ruebenacker, UCHC --- June 2008 to November 2009
 *   A model for a cell of a process table
 */

import java.util.Vector;

import org.vcell.sybil.models.bpimport.table.groups.CellGroup;
import org.vcell.sybil.models.bpimport.table.options.CellLiteralOption;
import org.vcell.sybil.models.bpimport.table.options.CellOption;
import org.vcell.sybil.models.bpimport.table.options.CellThingOption;
import org.vcell.sybil.util.combobox.VectorBoxModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class Cell {

	public static interface Filter {
		public boolean preferFirst(CellOption option1, CellOption option2);
	}
	
	public static class DefaultFilter implements Filter {

		public boolean preferFirst(CellOption option1, CellOption option2) { 
			return rank(option1) > rank(option2); 
		}
		
		public int rank(CellOption option) {
			int rank = 0;
			if(option instanceof CellThingOption<?>) {
				Resource node = ((CellThingOption<?>) option).thing().resource();
				if(node.isURIResource()) { rank = 3; }
				else { rank = 2; }
			} else if (option instanceof CellLiteralOption) {
				rank = 1;
			}
			return rank;
		}
		
		static public final DefaultFilter INSTANCE = new DefaultFilter();
	}
	
	protected ProcessTableModel table;
	protected Row row;
	protected Column column;
	protected CellGroup group;
	protected VectorBoxModel<CellOption> boxModel = new VectorBoxModel<CellOption>();
	protected Filter filter = DefaultFilter.INSTANCE;
	
	public Cell(ProcessTableModel tableNew, Row rowNew, Column columnNew) {
		table = tableNew;
		row = rowNew;
		column = columnNew;
		group = table.defaultGroup(this);
		boxModel.setList(group.options());
		boxModel.setSelectedItem(group.defaultSelectedOption()); 
	}
	
	public Row row() { return row; }
	public Column column() { return column; }
	public Vector<CellOption> options() { return group.options(); }
	public CellGroup group() { return group; }
	public void setFilter(Filter filterNew) { filter = filterNew; }
	public Filter filter() { return filter; }
	public VectorBoxModel<CellOption> boxModel() { return boxModel; }
	
	public CellOption add(CellOption option, boolean mustSelect, boolean maySelect) {
		if(!group.options().contains(option)) { 
			group.options().add(option);
		}
		if(option instanceof CellOption.Selection) { 
			if(mustSelect || (maySelect && filter.preferFirst(option, selected()))) { 
				select((CellOption.Selection) option); 
			}
		}
		return option;
	}

	public void select(CellOption.Selection option) { 
		boxModel.setSelectedItem(option); 
		table.assignGroup(this);
	}

	public void setGroup(CellGroup groupNew) {
		group = groupNew;
		boxModel.setList(group.options());
	}
	
	public CellOption.Selection selected() {  
		Object selectedObject = boxModel.getSelectedItem();
		return selectedObject instanceof CellOption.Selection ? 
				(CellOption.Selection) selectedObject : null; 
	}

	public ProcessTableModel table() { return table; }
	public String toString() { return selected() != null ? selected().toString() : ""; }

}
