/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.bpimport.table.options;

/*   CellNewOption  --- by Oliver Ruebenacker, UCHC --- July 2008 to March 2009
 *   An option to choose from in a cell to create a new resource
 */

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.TableUI.NoProperInputException;

import com.hp.hpl.jena.rdf.model.Literal;

public class CellNewLiteralOption extends CellActionOption {

	static protected ID id = new ID();
	
	public CellNewLiteralOption() { super(id, "new"); }
	
	public CellOption eventSelect(Cell cell, CellOption.Selector selector) { 
		CellLiteralOption literalOption;
		try { literalOption = 
			cell.table().ui().askForCellLiteralOption((Literal) cell.column().sampleNode()); } 
		catch (NoProperInputException e) { literalOption = null; }
		if(literalOption != null) {
			CellOption option = cell.add(literalOption, true, true);
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
