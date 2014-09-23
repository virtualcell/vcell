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
import cbit.vcell.mapping.gui.InitialConditionsPanel;
import cbit.vcell.mapping.gui.ReactionSpecsPanel;

@SuppressWarnings("serial")
public class ApplicationSpecificationsPanel extends ApplicationSubPanel {
	private InitialConditionsPanel initialConditionsPanel;
	private ReactionSpecsPanel reactionSpecsPanel;	
		
	private enum SettingsPanelTabID {
		species_settings("Species"),
		reaction_settings("Reactions");
		
		String title = null;
		SettingsPanelTabID(String name) {
			this.title = name;
		}
	}
	
	private class SettingsPanelTab {
		SettingsPanelTabID id;
		JComponent component = null;
		Icon icon = null;
		SettingsPanelTab(SettingsPanelTabID id, JComponent component, Icon icon) {
			this.id = id;
			this.component = component;
			this.icon = icon;
		}		
	}
	
	public ApplicationSpecificationsPanel() {
		super();
		initialize();
	}

	@Override
	protected void initialize(){
		super.initialize();	
		initialConditionsPanel = new InitialConditionsPanel();
		reactionSpecsPanel = new ReactionSpecsPanel();
		
		SettingsPanelTab settingsPanelTabs[] = new SettingsPanelTab[SettingsPanelTabID.values().length]; 
		settingsPanelTabs[SettingsPanelTabID.species_settings.ordinal()] = new SettingsPanelTab(SettingsPanelTabID.species_settings, initialConditionsPanel, null);
		settingsPanelTabs[SettingsPanelTabID.reaction_settings.ordinal()] = new SettingsPanelTab(SettingsPanelTabID.reaction_settings, reactionSpecsPanel, null);
		
		for (SettingsPanelTab tab : settingsPanelTabs) {
			tab.component.setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
		}	
	}
	
	@Override
	public void setSimulationContext(SimulationContext newValue) {
		super.setSimulationContext(newValue);
		initialConditionsPanel.setSimulationContext(simulationContext);
		reactionSpecsPanel.setSimulationContext(simulationContext);		
	}

	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		reactionSpecsPanel.setSelectionManager(selectionManager);
		initialConditionsPanel.setSelectionManager(selectionManager);
	}
	
	@Override
	public void setIssueManager(IssueManager issueManager) {
		super.setIssueManager(issueManager);
		reactionSpecsPanel.setIssueManager(issueManager);
		initialConditionsPanel.setIssueManager(issueManager);
	}
	
	@Override
	protected void onActiveViewChange(ActiveView activeView) {
		super.onActiveViewChange(activeView);
		if (DocumentEditorTreeFolderClass.SPECIFICATIONS_NODE.equals(activeView.getDocumentEditorTreeFolderClass())) {
			if (activeView.getActiveViewID() != null) {
				if (activeView.getActiveViewID().equals(ActiveViewID.species_settings)) {
					tabbedPane.setSelectedIndex(SettingsPanelTabID.species_settings.ordinal());
				} else if (activeView.getActiveViewID().equals(ActiveViewID.reaction_setting)) {
					tabbedPane.setSelectedIndex(SettingsPanelTabID.reaction_settings.ordinal());
				}
			}
		}
	}

	@Override
	public ActiveView getActiveView() {
		Component selectedComponent = tabbedPane.getSelectedComponent();
		ActiveViewID activeViewID = null;
		if (selectedComponent == initialConditionsPanel) {
			activeViewID = ActiveViewID.species_settings;
		} else if (selectedComponent == reactionSpecsPanel) {
			activeViewID = ActiveViewID.reaction_setting;
		}
		return new ActiveView(simulationContext, DocumentEditorTreeFolderClass.SPECIFICATIONS_NODE, activeViewID);
	}
}
