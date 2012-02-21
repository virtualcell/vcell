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
		electrical("Electrical"),
		events("Events"),
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
		electricalMembraneMappingPanel.setSimulationContext(simulationContext);
		showOrHideEventsPanel();
		showOrHideMicroscopeMeasurementPanel();
	}
	
	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		eventsDisplayPanel.setSelectionManager(selectionManager);
	}
	
	private void showOrHidePanel(ProtocolsPanelTabID tabID, boolean bShow) {
		ProtocolsPanelTab tab = protocolPanelTabs[tabID.ordinal()];
		int index = tabbedPane.indexOfComponent(tab.component);
		if (bShow) {
			tabbedPane.setEnabledAt(index, true);
		} else {
			if (index >= 0) {
				Component selectedComponent = tabbedPane.getSelectedComponent();
				tabbedPane.setEnabledAt(index, false);
				if (selectedComponent == tab.component) {
					for (int i = 0; i < tabbedPane.getTabCount(); ++i) {
						if (tabbedPane.isEnabledAt(i)) {
							tabbedPane.setSelectedIndex(i);
							break;
						}
					}
				}
			}
		}
	}

	private void showOrHideMicroscopeMeasurementPanel() {
		boolean bShow = simulationContext.getGeometry().getDimension() > 0 && !simulationContext.isStoch();
		showOrHidePanel(ProtocolsPanelTabID.microscope_measurements, bShow);
		if (bShow) {
			microscopeMeasurementPanel.setSimulationContext(simulationContext);
		}
	}
	
	private void showOrHideEventsPanel() {
		boolean bShow = simulationContext.getGeometry().getDimension() == 0 && !simulationContext.isStoch();
		showOrHidePanel(ProtocolsPanelTabID.events, bShow);
		if (bShow) {
			eventsDisplayPanel.setSimulationContext(simulationContext);
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
