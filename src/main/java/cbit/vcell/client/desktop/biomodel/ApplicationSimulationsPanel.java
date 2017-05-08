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
import java.awt.Window;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.desktop.simulation.OutputFunctionsPanel;
import cbit.vcell.client.desktop.simulation.SimulationListPanel;
import cbit.vcell.client.desktop.simulation.SimulationWorkspace;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class ApplicationSimulationsPanel extends ApplicationSubPanel {
	private SimulationListPanel simulationListPanel;
	private OutputFunctionsPanel outputFunctionsPanel;
	private MathematicsPanel mathematicsPanel;
	
	public enum SimulationsPanelTabID {
		simulations("Simulations"),
		output_functions("Output Functions"),
		generated_math("Generated Math");
		
		String title = null;
		SimulationsPanelTabID(String name) {
			this.title = name;
		}
		public final String getTitle() {
			return title;
		}
	}
	
	private class SimulationsPanelTab {
		SimulationsPanelTabID id;
		JComponent component = null;
		Icon icon = null;
		SimulationsPanelTab(SimulationsPanelTabID id, JComponent component, Icon icon) {
			this.id = id;
			this.component = component;
			this.icon = icon;
		}		
	}
	
	public ApplicationSimulationsPanel() {
		super();
		initialize();
	}

	@Override
	protected void initialize(){
		super.initialize();
		simulationListPanel = new SimulationListPanel();
		outputFunctionsPanel = new OutputFunctionsPanel();
		mathematicsPanel = new MathematicsPanel();
		
		SimulationsPanelTab simsPanelTabs[] = new SimulationsPanelTab[SimulationsPanelTabID.values().length]; 
		simsPanelTabs[SimulationsPanelTabID.simulations.ordinal()] = new SimulationsPanelTab(SimulationsPanelTabID.simulations, simulationListPanel, null);
		simsPanelTabs[SimulationsPanelTabID.output_functions.ordinal()] = new SimulationsPanelTab(SimulationsPanelTabID.output_functions, outputFunctionsPanel, null);
		simsPanelTabs[SimulationsPanelTabID.generated_math.ordinal()] = new SimulationsPanelTab(SimulationsPanelTabID.generated_math, mathematicsPanel, null);
		
		for (SimulationsPanelTab tab : simsPanelTabs) {
			tab.component.setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
		}
	}	
	
	@Override
	public void setSimulationContext(SimulationContext newValue) {
		super.setSimulationContext(newValue);
		if (simulationContext == null) {
			return;
		}
		ApplicationComponents applicationComponents = bioModelWindowManager.getApplicationComponents(simulationContext);
		SimulationWorkspace simulationWorkspace = applicationComponents.getSimulationWorkspace();		
		simulationListPanel.setSimulationWorkspace(simulationWorkspace);
		outputFunctionsPanel.setSimulationWorkspace(simulationWorkspace);
		mathematicsPanel.setSimulationContext(simulationContext);
	}
	
	@Override
	public void setBioModelWindowManager(BioModelWindowManager newValue) {
		super.setBioModelWindowManager(newValue);
		BioModelWindowManager oldValue = bioModelWindowManager;
		if (oldValue != null) {
			mathematicsPanel.removeActionListener(bioModelWindowManager);
		}
		bioModelWindowManager = newValue;
		if (newValue != null) {
			mathematicsPanel.addActionListener(newValue);
		}
	}

	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		simulationListPanel.setSelectionManager(selectionManager);
		outputFunctionsPanel.setSelectionManager(selectionManager);
	}

	@Override
	public ActiveView getActiveView() {
		Component selectedComponent = tabbedPane.getSelectedComponent();
		ActiveViewID activeViewID = null;
		if (selectedComponent == simulationListPanel) {
			activeViewID = ActiveViewID.simulations;
		} else if (selectedComponent == outputFunctionsPanel) {
			activeViewID = ActiveViewID.output_functions;
		} else if (selectedComponent == mathematicsPanel) {
			activeViewID = ActiveViewID.generated_math;
		}
		return new ActiveView(simulationContext, DocumentEditorTreeFolderClass.SIMULATIONS_NODE, activeViewID);
	}
	
	@Override
	protected void onActiveViewChange(ActiveView activeView) {
		super.onActiveViewChange(activeView);
		if (DocumentEditorTreeFolderClass.SIMULATIONS_NODE.equals(activeView.getDocumentEditorTreeFolderClass())) {
			if (activeView.getActiveViewID() != null) {
				boolean set = false;
				if (activeView.getActiveViewID().equals(ActiveViewID.simulations)) {
					tabbedPane.setSelectedIndex(SimulationsPanelTabID.simulations.ordinal());
					set = true;
				} else if (activeView.getActiveViewID().equals(ActiveViewID.output_functions)) {
					tabbedPane.setSelectedIndex(SimulationsPanelTabID.output_functions.ordinal());
					set = true;
				} else if (activeView.getActiveViewID().equals(ActiveViewID.generated_math)) {
					tabbedPane.setSelectedIndex(SimulationsPanelTabID.generated_math.ordinal());
					set = true;
				}
				if (set) {
					Window w = (Window) SwingUtilities.getAncestorOfClass(Window.class, this);
					activeView.setActivated(w);
				}
			}
		}
	}

	@Override
	public void setIssueManager(IssueManager newValue) {		
		super.setIssueManager(newValue);
		simulationListPanel.setIssueManager(newValue);
		outputFunctionsPanel.setIssueManager(newValue);
//		mathematicsPanel.setIssueManager(newValue);		// can't, not a table
	}
	
}
