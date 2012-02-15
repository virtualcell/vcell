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

import javax.swing.Icon;
import javax.swing.JComponent;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel;
import cbit.vcell.mapping.gui.MicroscopeMeasurementPanel;

@SuppressWarnings("serial")
public class ApplicationProtocolsPanel extends ApplicationSubPanel {
	private EventsDisplayPanel eventsDisplayPanel;
	private ElectricalMembraneMappingPanel electricalMembraneMappingPanel;
	private MicroscopeMeasurementPanel microscopeMeasurementPanel;
	
	private enum ProtocolsPanelTabID {
		events("Events"),
		electrical("Electrical"),
		microscope_measurements("Microscope Measurements");
		
		String title = null;
		ProtocolsPanelTabID(String name) {
			this.title = name;
		}
	}
	
	private class ProtocolsPanelTab {
		ProtocolsPanelTabID id;
		JComponent component = null;
		Icon icon = null;
		ProtocolsPanelTab(ProtocolsPanelTabID id, JComponent component, Icon icon) {
			this.id = id;
			this.component = component;
			this.icon = icon;
		}		
	}
	
	private ProtocolsPanelTab protocolPanelTabs[] = new ProtocolsPanelTab[ProtocolsPanelTabID.values().length];
	
	public ApplicationProtocolsPanel() {
		super();
		initialize();
	}

	@Override
	protected void initialize(){
		super.initialize();
		eventsDisplayPanel = new EventsDisplayPanel();
		electricalMembraneMappingPanel = new ElectricalMembraneMappingPanel();
		microscopeMeasurementPanel = new MicroscopeMeasurementPanel();
		
		protocolPanelTabs = new ProtocolsPanelTab[ProtocolsPanelTabID.values().length]; 
		protocolPanelTabs[ProtocolsPanelTabID.events.ordinal()] = new ProtocolsPanelTab(ProtocolsPanelTabID.events, eventsDisplayPanel, null);
		protocolPanelTabs[ProtocolsPanelTabID.electrical.ordinal()] = new ProtocolsPanelTab(ProtocolsPanelTabID.electrical, electricalMembraneMappingPanel, null);
		protocolPanelTabs[ProtocolsPanelTabID.microscope_measurements.ordinal()] = new ProtocolsPanelTab(ProtocolsPanelTabID.microscope_measurements, microscopeMeasurementPanel, null);
		
		for (ProtocolsPanelTab tab : protocolPanelTabs) {
			tab.component.setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
		}		
	}	
	
	@Override
	public void setSimulationContext(SimulationContext newValue) {
		super.setSimulationContext(newValue);
		eventsDisplayPanel.setSimulationContext(simulationContext);
		electricalMembraneMappingPanel.setSimulationContext(simulationContext);
		showOrHideMicroscopeMeasurementPanel();
	}
	
	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		eventsDisplayPanel.setSelectionManager(selectionManager);
	}

	private void showOrHideMicroscopeMeasurementPanel() {
		ProtocolsPanelTab tab = protocolPanelTabs[ProtocolsPanelTabID.microscope_measurements.ordinal()];
		int index = tabbedPane.indexOfComponent(tab.component);
		if (simulationContext.getGeometry().getDimension() > 0 && !simulationContext.isStoch()) {
			if (index < 0) {
				tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
			}
			microscopeMeasurementPanel.setSimulationContext(simulationContext);
		} else {
			if (index >= 0) {
				Component selectedComponent = tabbedPane.getSelectedComponent();
				tabbedPane.remove(tab.component);
				if (selectedComponent == tab.component) {
					tabbedPane.setSelectedIndex(0);
				}
			}
		}
	}

	@Override
	public ActiveView getActiveView() {
		Component selectedComponent = tabbedPane.getSelectedComponent();
		ActiveViewID activeViewID = null;
		if (selectedComponent == eventsDisplayPanel) {
			activeViewID = ActiveViewID.events;
		} else if (selectedComponent == electricalMembraneMappingPanel) {
			activeViewID =  ActiveViewID.electrical;
		} else if (selectedComponent == microscopeMeasurementPanel) {
			activeViewID = ActiveViewID.microscope_measuremments;
		}
		return new ActiveView(simulationContext, DocumentEditorTreeFolderClass.PROTOCOLS_NODE, activeViewID);
	}
	
}
