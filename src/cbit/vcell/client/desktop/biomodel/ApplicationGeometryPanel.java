/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.constants.GuiConstants;
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
	private SpatialEntitiesPanel spatialEntitiesPanel;
	private SpatialObjectDisplayPanel spatialObjectDisplayPanel;
	private SpatialProcessDisplayPanel spatialProcessDisplayPanel;
		
	private enum GeometryPanelTabID {
		structure_mapping("Structure Mapping"),
		geometry_definition("Geometry Definition"),
		spatial_entities("Kinematics"); 

		String title = null;
		GeometryPanelTabID(String name) {
			this.title = name;
		}
	}
	
	private class SpatialEntitiesPanel extends JPanel {
		public SpatialEntitiesPanel() {
			super();
			initialize();
		}

		private void initialize() {
			setName("SpatialEntitiesPanel");
			setLayout(new GridBagLayout());

			Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
			TitledBorder titleTop = BorderFactory.createTitledBorder(loweredEtchedBorder, "<html><b> Spatial Objects </b></html>");
			titleTop.setTitleJustification(TitledBorder.LEFT);
			titleTop.setTitlePosition(TitledBorder.TOP);
			TitledBorder titleBottom = BorderFactory.createTitledBorder(loweredEtchedBorder, "<html><b> Spatial Process </b></html>");
			titleBottom.setTitleJustification(TitledBorder.LEFT);
			titleBottom.setTitlePosition(TitledBorder.TOP);
			
			spatialObjectDisplayPanel = new SpatialObjectDisplayPanel();
			spatialProcessDisplayPanel = new SpatialProcessDisplayPanel();
			spatialObjectDisplayPanel.setBorder(titleTop);
			spatialProcessDisplayPanel.setBorder(titleBottom);

			int gridy = 0;
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(8,4,4,4);
			add(spatialObjectDisplayPanel, gbc);
			
			gridy++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.anchor = GridBagConstraints.SOUTH;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(8,4,4,4);
			add(spatialProcessDisplayPanel, gbc);
		}
	}
	
	private class GeometryPanelTab {
		GeometryPanelTabID id;
		JComponent component = null;
		Icon icon = null;
		GeometryPanelTab(GeometryPanelTabID id, JComponent component, Icon icon) {
			this.id = id;
			this.component = component;
			this.icon = icon;
		}		
	}
	
	public ApplicationGeometryPanel() {
		super();
		initialize();
	}

	@Override
	protected void initialize(){
		super.initialize();
		geometryViewer = new GeometryViewer();
		structureMappingCartoonPanel = new StructureMappingCartoonPanel();
		spatialEntitiesPanel = new SpatialEntitiesPanel();
		
		GeometryPanelTab geometryPanelTabs[] = new GeometryPanelTab[GeometryPanelTabID.values().length]; 
		geometryPanelTabs[GeometryPanelTabID.structure_mapping.ordinal()] = new GeometryPanelTab(GeometryPanelTabID.structure_mapping, structureMappingCartoonPanel, null);
		geometryPanelTabs[GeometryPanelTabID.geometry_definition.ordinal()] = new GeometryPanelTab(GeometryPanelTabID.geometry_definition, geometryViewer, null);
		geometryPanelTabs[GeometryPanelTabID.spatial_entities.ordinal()] = new GeometryPanelTab(GeometryPanelTabID.spatial_entities, spatialEntitiesPanel, null);
		
		for (GeometryPanelTab tab : geometryPanelTabs) {
			tab.component.setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
		}
	}
	
	@Override
	public void setSimulationContext(SimulationContext newValue) {
		super.setSimulationContext(newValue);
		structureMappingCartoonPanel.setSimulationContext(simulationContext);	
		spatialObjectDisplayPanel.setSimulationContext(simulationContext);
		spatialProcessDisplayPanel.setSimulationContext(simulationContext);
		updateGeometryViewerOwner();
	}

	@Override
	public void setBioModelWindowManager(BioModelWindowManager newValue) {
		super.setBioModelWindowManager(newValue);
		geometryViewer.addActionListener(bioModelWindowManager);
		updateGeometryViewerOwner();
	}	

	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		structureMappingCartoonPanel.setSelectionManager(selectionManager);
		geometryViewer.setSelectionManager(selectionManager);
		spatialObjectDisplayPanel.setSelectionManager(selectionManager);
		spatialProcessDisplayPanel.setSelectionManager(selectionManager);
	}

	@Override
	protected void onActiveViewChange(ActiveView activeView) {
		super.onActiveViewChange(activeView);
		if (DocumentEditorTreeFolderClass.GEOMETRY_NODE.equals(activeView.getDocumentEditorTreeFolderClass())) {
			if (activeView.getActiveViewID() != null) {
				if (activeView.getActiveViewID().equals(ActiveViewID.geometry_definition)) {
					tabbedPane.setSelectedIndex(GeometryPanelTabID.geometry_definition.ordinal());
				} else if (activeView.getActiveViewID().equals(ActiveViewID.structure_mapping)) {
					tabbedPane.setSelectedIndex(GeometryPanelTabID.structure_mapping.ordinal());
				} else if (activeView.getActiveViewID().equals(ActiveViewID.spatial_objects)) {
					tabbedPane.setSelectedIndex(GeometryPanelTabID.spatial_entities.ordinal());
				} else if (activeView.getActiveViewID().equals(ActiveViewID.spatial_processes)) {
					tabbedPane.setSelectedIndex(GeometryPanelTabID.spatial_entities.ordinal());
				}
			}
		}
	}

	@Override
	public ActiveView getActiveView() {
		Component selectedComponent = tabbedPane.getSelectedComponent();
		ActiveViewID activeViewID = null;
		if (selectedComponent == structureMappingCartoonPanel) {
			activeViewID = ActiveViewID.structure_mapping;
		} else if (selectedComponent == geometryViewer) {
			activeViewID = ActiveViewID.geometry_definition;
		} else if (selectedComponent == spatialObjectDisplayPanel) {
			activeViewID = ActiveViewID.spatial_objects;
		} else if (selectedComponent == spatialProcessDisplayPanel) {
			activeViewID = ActiveViewID.spatial_processes;
		}
		return new ActiveView(simulationContext, DocumentEditorTreeFolderClass.GEOMETRY_NODE, activeViewID);
	}
	
	@Override
	public void setIssueManager(IssueManager issueManager) {
		super.setIssueManager(issueManager);
		structureMappingCartoonPanel.setIssueManager(issueManager);
		geometryViewer.setIssueManager(issueManager);
		spatialObjectDisplayPanel.setIssueManager(issueManager);
		spatialProcessDisplayPanel.setIssueManager(issueManager);
	}
	
	private void updateGeometryViewerOwner(){
		geometryViewer.setGeometryOwner(simulationContext,bioModelWindowManager);
	}
}
