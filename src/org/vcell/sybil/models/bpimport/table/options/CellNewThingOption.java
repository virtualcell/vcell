package org.vcell.sybil.models.bpimport.table.options;

/*   CellNewSBViewOption  --- by Oliver Ruebenacker, UCHC --- July 2008 to July 2009
 *   An option to choose from in a cell to create a new resource
 */

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.ProcessTableModel;
import org.vcell.sybil.models.bpimport.table.TableUI.NoProperInputException;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.ThingFactory;

import com.hp.hpl.jena.rdf.model.Resource;

public class CellNewThingOption<T extends SBBox.NamedThing> extends CellActionOption {

	static protected ID id = new ID();
	
	protected ThingFactory<? extends T> factory;
	
	public CellNewThingOption(ThingFactory<? extends T> factory) { 
		super(id, "new"); 
		this.factory = factory;
	}
	
	public CellOption eventSelect(Cell cell, CellOption.Selector selector) { 
		CellThingOption<T> sbViewOption;
		try { 
			ProcessTableModel table = cell.table();
			sbViewOption = 
				table.ui().askForCellResourceOption(factory,
						factory.box(), (Resource) cell.column().sampleNode()); } 
		catch (NoProperInputException e) { sbViewOption = null; }
		if(sbViewOption != null) {
			CellOption option = cell.add(sbViewOption, true, true);
			if(option != null) { 
				selector.select(option); 
				selector.stop();
			} else {
				selector.returnToLastSelection();
				selector.cancel();
			}
		} else {
			selector.returnToLastSelection();
			selector.cancel();
		}
		return cell.selected(); 
	}

}
