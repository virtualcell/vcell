package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.geometry.gui.GeometryViewer;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.gui.StructureMappingCartoonPanel;

@SuppressWarnings("serial")
public class ApplicationGeometryPanel extends ApplicationSubPanel {
	private GeometryViewer geometryViewer;
	private StructureMappingCartoonPanel structureMappingCartoonPanel;
	
		
	public ApplicationGeometryPanel() {
		super();
		initialize();
	}

	private void initialize(){			
		geometryViewer = new GeometryViewer();
		structureMappingCartoonPanel = new StructureMappingCartoonPanel();
		
		geometryViewer.setBorder(GuiConstants.TAB_PANEL_BORDER);
		structureMappingCartoonPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		tabbedPane.addTab("Structure Mapping", structureMappingCartoonPanel);
		tabbedPane.addTab("Geometry Settings", geometryViewer);
	}
	
	@Override
	public void setSimulationContext(SimulationContext newValue) {
		super.setSimulationContext(newValue);
		structureMappingCartoonPanel.setSimulationContext(simulationContext);		
		geometryViewer.setGeometryOwner(simulationContext);
	}

	@Override
	public void setBioModelWindowManager(BioModelWindowManager newValue) {
		super.setBioModelWindowManager(newValue);
		geometryViewer.addActionListener(bioModelWindowManager);
	}	

	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		structureMappingCartoonPanel.setSelectionManager(selectionManager);
	}

	@Override
	protected void onActiveViewChange(ActiveView activeView) {
		super.onActiveViewChange(activeView);
		if (DocumentEditorTreeFolderClass.GEOMETRY_NODE.equals(activeView.getDocumentEditorTreeFolderClass())) {
			if (activeView.getActiveViewID() != null) {
				if (activeView.getActiveViewID().equals(ActiveViewID.geometry_settings)) {
					tabbedPane.setSelectedIndex(0);
				} else if (activeView.getActiveViewID().equals(ActiveViewID.structure_mapping)) {
					tabbedPane.setSelectedIndex(1);
				}
			}
		}
	}

	@Override
	public ActiveViewID getActiveViewID() {
		Component selectedComponent = tabbedPane.getSelectedComponent();
		if (selectedComponent == structureMappingCartoonPanel) {
			return ActiveViewID.structure_mapping;
		}
		if (selectedComponent == geometryViewer) {
			return ActiveViewID.geometry_settings;
		}
		return null;
	}
	
}
