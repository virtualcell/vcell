package cbit.vcell.client.desktop.biomodel.pathway;

import java.util.List;

import org.vcell.pathway.BioPaxObject;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.SelectionManager;

public interface PathwayEditor {
	
	public SelectionManager getSelectionManager();
	public BioModel getBioModel();
	public List<BioPaxObject> getSelectedBioPaxObjects();

}
