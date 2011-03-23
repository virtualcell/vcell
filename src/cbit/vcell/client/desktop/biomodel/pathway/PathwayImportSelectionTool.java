package cbit.vcell.client.desktop.biomodel.pathway;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import org.vcell.pathway.BioPaxObject;
import org.vcell.util.gui.ActionSpecs;

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
		
		public BringItInAction(ActionSpecs specs, PathwayEditor editor, PathwayImportSelectionTool tool) { 
			specs.set(this);
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
