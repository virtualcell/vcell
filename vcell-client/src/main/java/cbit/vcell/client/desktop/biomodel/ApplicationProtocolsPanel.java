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
import javax.swing.SwingUtilities;

import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel;
import cbit.vcell.mapping.gui.MicroscopeMeasurementPanel;

@SuppressWarnings("serial")
public class ApplicationProtocolsPanel extends ApplicationSubPanel {
	private ElectricalMembraneMappingPanel electricalMembraneMappingPanel;
	private EventsDisplayPanel eventsDisplayPanel;
	private RateRulesDisplayPanel rateRulesDisplayPanel;
	private AssignmentRulesDisplayPanel assignmentRulesDisplayPanel;
	private MicroscopeMeasurementPanel microscopeMeasurementPanel;
	
	private enum ProtocolsPanelTabID {
		electrical("Electrical"),
		events("Events"),
		rate_rules("Rate Rules"),
		assignment_rules("Assignment Rules"),
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
		electricalMembraneMappingPanel = new ElectricalMembraneMappingPanel();
		eventsDisplayPanel = new EventsDisplayPanel();
		rateRulesDisplayPanel = new RateRulesDisplayPanel();
		assignmentRulesDisplayPanel = new AssignmentRulesDisplayPanel();
		microscopeMeasurementPanel = new MicroscopeMeasurementPanel();
		
		protocolPanelTabs = new ProtocolsPanelTab[ProtocolsPanelTabID.values().length]; 
		protocolPanelTabs[ProtocolsPanelTabID.electrical.ordinal()] = new ProtocolsPanelTab(ProtocolsPanelTabID.electrical, electricalMembraneMappingPanel, null);
		protocolPanelTabs[ProtocolsPanelTabID.events.ordinal()] = new ProtocolsPanelTab(ProtocolsPanelTabID.events, eventsDisplayPanel, null);
		protocolPanelTabs[ProtocolsPanelTabID.rate_rules.ordinal()] = new ProtocolsPanelTab(ProtocolsPanelTabID.rate_rules, rateRulesDisplayPanel, VCellIcons.ruleRateIcon);
		protocolPanelTabs[ProtocolsPanelTabID.assignment_rules.ordinal()] = new ProtocolsPanelTab(ProtocolsPanelTabID.assignment_rules, assignmentRulesDisplayPanel, VCellIcons.ruleAssignIcon);
		protocolPanelTabs[ProtocolsPanelTabID.microscope_measurements.ordinal()] = new ProtocolsPanelTab(ProtocolsPanelTabID.microscope_measurements, microscopeMeasurementPanel, null);
		
		for (ProtocolsPanelTab tab : protocolPanelTabs) {
			tab.component.setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.insertTab(tab.id.title, tab.icon, tab.component, null, tab.id.ordinal());
		}		
	}	
	
	@Override
	public void setIssueManager(IssueManager issueManager) {
		super.setIssueManager(issueManager);
		eventsDisplayPanel.setIssueManager(issueManager);
		rateRulesDisplayPanel.setIssueManager(issueManager);
		assignmentRulesDisplayPanel.setIssueManager(issueManager);
	}

	@Override
	public void setSimulationContext(SimulationContext newValue) {
		super.setSimulationContext(newValue);
		showOrHideSubpanels();
	}
	
	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		eventsDisplayPanel.setSelectionManager(selectionManager);
		rateRulesDisplayPanel.setSelectionManager(selectionManager);
		assignmentRulesDisplayPanel.setSelectionManager(selectionManager);
	}

	private void showOrHidePanel(ProtocolsPanelTabID tabID, boolean bShow) {
		ProtocolsPanelTab tab = protocolPanelTabs[tabID.ordinal()];
		int index = tabbedPane.indexOfComponent(tab.component);
		// we are always actually showing all tabs
		// but they are only usable if compatible
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				BioModelEditorApplicationPanel.enableSubComponents(tab.component, bShow);
				tabbedPane.setEnabledAt(tabID.ordinal(), bShow);
			}				
		});
	}

	public void geometryChanged() {
		showOrHideSubpanels();
	}
	
	private void showOrHideSubpanels() {
		showOrHideEventsPanel();
		showOrHideelectricalMembraneMappingPanel();
		showOrHideMicroscopeMeasurementPanel();
		showOrHideRateRulesPanel();
		showOrHideAssignmentRulesPanel();
	}
	
	private void showOrHideelectricalMembraneMappingPanel() {
		boolean bShow = simulationContext.getApplicationType() == Application.NETWORK_DETERMINISTIC;
		showOrHidePanel(ProtocolsPanelTabID.electrical, bShow);
		if (bShow) {
			electricalMembraneMappingPanel.setSimulationContext(simulationContext);
		}
	}

	private void showOrHideMicroscopeMeasurementPanel() {
		boolean bShow = simulationContext.getGeometry().getDimension() > 0 && simulationContext.getApplicationType() == Application.NETWORK_DETERMINISTIC;
		showOrHidePanel(ProtocolsPanelTabID.microscope_measurements, bShow);
		if (bShow) {
			microscopeMeasurementPanel.setSimulationContext(simulationContext);
		}
	}
	
	private void showOrHideEventsPanel() {
		boolean bShow = simulationContext.getGeometry().getDimension() == 0 && simulationContext.getApplicationType() == Application.NETWORK_DETERMINISTIC;
		showOrHidePanel(ProtocolsPanelTabID.events, bShow);
		if (bShow) {
			eventsDisplayPanel.setSimulationContext(simulationContext);
		}
	}

	private void showOrHideRateRulesPanel() {
		boolean bShow = simulationContext.getGeometry().getDimension() == 0 && simulationContext.getApplicationType() == Application.NETWORK_DETERMINISTIC;
		showOrHidePanel(ProtocolsPanelTabID.rate_rules, bShow);
		if (bShow) {
			rateRulesDisplayPanel.setSimulationContext(simulationContext);
		}
	}
	private void showOrHideAssignmentRulesPanel() {
		boolean bShow = simulationContext.getGeometry().getDimension() == 0 && simulationContext.getApplicationType() == Application.NETWORK_DETERMINISTIC;
		showOrHidePanel(ProtocolsPanelTabID.assignment_rules, bShow);
		if (bShow) {
			assignmentRulesDisplayPanel.setSimulationContext(simulationContext);
		}
	}
	
	public void selectTab(ActiveViewID activeViewID) {
		if(activeViewID == null) {
			return;
		}
		switch (activeViewID) {
		case events:
			tabbedPane.setSelectedComponent(eventsDisplayPanel);
			break;
		case electrical:
			tabbedPane.setSelectedComponent(electricalMembraneMappingPanel);
			break;
		case microscope_measuremments:
			tabbedPane.setSelectedComponent(microscopeMeasurementPanel);
			break;
		case rateRules:
			tabbedPane.setSelectedComponent(rateRulesDisplayPanel);
			break;
		case assignmentRules:
			tabbedPane.setSelectedComponent(assignmentRulesDisplayPanel);
			break;
		default:
			break;
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
		} else if (selectedComponent == rateRulesDisplayPanel) {
			activeViewID =  ActiveViewID.rateRules;
		} else if (selectedComponent == assignmentRulesDisplayPanel) {
			activeViewID =  ActiveViewID.assignmentRules;
		}
		return new ActiveView(simulationContext, DocumentEditorTreeFolderClass.PROTOCOLS_NODE, activeViewID);
	}
	
}
