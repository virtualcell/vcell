/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel.pathway;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import org.vcell.pathway.BioPaxObject;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.SelectionManager;

public interface PathwayImportSelectionTool {
	
	public void setBioModel(BioModel bioModel);
	public void setSelectionManager(SelectionManager selectionManager);
	public void setBioPaxObjects(List<BioPaxObject> bpObjects);
	public void showSelectionDialog();

	@SuppressWarnings("serial")
	public static class BringItInAction extends AbstractAction {
		
		protected final PathwayEditor editor;
		protected final PathwayImportSelectionTool tool;
		
		public BringItInAction(PathwayEditor editor, PathwayImportSelectionTool tool) { 
			this.editor = editor;
			this.tool = tool; 
		}
		
		public PathwayImportSelectionTool getTool() { return tool; }
		
		public void actionPerformed(ActionEvent arg0) { 
			List<BioPaxObject> selectedBioPaxObjects = editor.getSelectedBioPaxObjects();
			if(!selectedBioPaxObjects.isEmpty()) {
				tool.setBioModel(editor.getBioModel());
				tool.setSelectionManager(editor.getSelectionManager());
				tool.setBioPaxObjects(selectedBioPaxObjects);
				tool.showSelectionDialog(); 
			}
		}
		
	}

}
