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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vcell.util.gui.JTabbedPaneEnhanced;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.GeometryOwner;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class BioModelEditorApplicationPanel extends DocumentEditorSubPanel {
	private ApplicationGeometryPanel applicationGeometryPanel = null;
	private ApplicationSpecificationsPanel applicationSpecificationsPanel;
	private ApplicationProtocolsPanel applicationProtocolsPanel;
	private ApplicationSimulationsPanel applicationSimulationsPanel;
	private ParameterEstimationPanel parameterEstimationPanel;
	private JTabbedPane tabbedPane = null;
	private SimulationContext simulationContext;
	private BioModelWindowManager bioModelWindowManager;

	public enum ApplicationPanelTabID {
		geometry("Geometry"),
		settings("Specifications"),
		protocols("Protocols"),
		simulations("Simulations"),
		parameterEstimation("Parameter Estimation");
		
		String title = null;
		ApplicationPanelTabID(String name) {
			this.title = name;
		}
		public final String getTitle() {
			return title;
		}		
	}
	
	private class ApplicationPanelTab {
		ApplicationPanelTabID id;
		JComponent component = null;
		Icon icon = null;
		ApplicationPanelTab(ApplicationPanelTabID id, JComponent component, Icon icon) {
			this.id = id;
			this.component = component;
			this.icon = icon;
		}		
	}
	
	private ApplicationPanelTab appPanelTabs[] = new ApplicationPanelTab[ApplicationPanelTabID.values().length]; 
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private class InternalEventHandler implements ActionListener, ChangeListener, PropertyChangeListener {		
				
		public void actionPerformed(ActionEvent e) {
			
		}

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == tabbedPane) {
				tabbedPaneSelectionChanged();
			}			
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == simulationContext) {
				if (evt.getPropertyName().equals(GeometryOwner.PROPERTY_NAME_GEOMETRY)) {
					showOrHideFittingPanel();
					applicationProtocolsPanel.geometryChanged();
				}
			}			
		}
	}
	
	public BioModelEditorApplicationPanel() {
		super();
		initialize();
	}

	private void initialize(){
		applicationGeometryPanel = new ApplicationGeometryPanel();
		applicationProtocolsPanel = new ApplicationProtocolsPanel();
		applicationSpecificationsPanel = new ApplicationSpecificationsPanel();
		applicationSimulationsPanel = new ApplicationSimulationsPanel();
		parameterEstimationPanel = new ParameterEstimationPanel();
		tabbedPane = new JTabbedPaneEnhanced();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		appPanelTabs[ApplicationPanelTabID.geometry.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.geometry, applicationGeometryPanel, VCellIcons.geometryIcon);
		appPanelTabs[ApplicationPanelTabID.settings.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.settings, applicationSpecificationsPanel, VCellIcons.settingsIcon);
		appPanelTabs[ApplicationPanelTabID.protocols.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.protocols, applicationProtocolsPanel, VCellIcons.protocolsIcon);
		appPanelTabs[ApplicationPanelTabID.simulations.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.simulations, applicationSimulationsPanel, VCellIcons.simulationIcon);
		appPanelTabs[ApplicationPanelTabID.parameterEstimation.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.parameterEstimation, parameterEstimationPanel, VCellIcons.fittingIcon);
		tabbedPane.addChangeListener(eventHandler);
		
		for (ApplicationPanelTab tab : appPanelTabs) {
			tab.component.setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
		}
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
	}	

	public void tabbedPaneSelectionChanged() {
		int selectedIndex = tabbedPane.getSelectedIndex();
		ActiveView activeView = null;
		if (selectedIndex == ApplicationPanelTabID.geometry.ordinal()) {
			activeView = applicationGeometryPanel.getActiveView();
		} else if (selectedIndex == ApplicationPanelTabID.settings.ordinal()) {
			activeView = applicationSpecificationsPanel.getActiveView();
		} else if (selectedIndex == ApplicationPanelTabID.protocols.ordinal()) {
			activeView = applicationProtocolsPanel.getActiveView();
		} else if (selectedIndex == ApplicationPanelTabID.simulations.ordinal()) {
			activeView = applicationSimulationsPanel.getActiveView();
		} else if (selectedIndex == ApplicationPanelTabID.parameterEstimation.ordinal()) {
			activeView = parameterEstimationPanel.getActiveView();
		}
		if (activeView != null) {
			setActiveView(activeView);
		}
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
	}
	
	public void setSimulationContext(SimulationContext newValue) {
		if (simulationContext == newValue) {
			return;
		}
		final boolean respondingToSelectionManager = selectionManager.isBusy();
		final Object[] selectedObj = selectionManager.getSelectedObjects();
		SimulationContext oldValue = simulationContext;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(eventHandler);
		}
		if (newValue != null) {
			newValue.addPropertyChangeListener(eventHandler);
		}
		simulationContext = newValue;
		AsynchClientTask task1 = new AsynchClientTask("loading application", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				simulationContext.getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT());
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("showing application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				applicationGeometryPanel.setSimulationContext(simulationContext);
				applicationSpecificationsPanel.setSimulationContext(simulationContext);
				applicationProtocolsPanel.setSimulationContext(simulationContext);
				applicationSimulationsPanel.setSimulationContext(simulationContext);
				parameterEstimationPanel.setSelectionManager(null);
				showOrHideFittingPanel();
				parameterEstimationPanel.setSelectionManager(selectionManager);
//				showOrHideProtocolsPanel();
				if (respondingToSelectionManager){
					selectionManager.setSelectedObjects(new Object[0]);
					selectionManager.setSelectedObjects(selectedObj);
				}
			}
		};		
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2});
	}

	private void showOrHideProtocolsPanel() {
		boolean bShow = !simulationContext.isRuleBased();
		ApplicationPanelTab protocolsTab = appPanelTabs[ApplicationPanelTabID.protocols.ordinal()];
		int protocolsTabIndexInPane = tabbedPane.indexOfComponent(protocolsTab.component);
		if(bShow){
			if(protocolsTabIndexInPane == -1){
				tabbedPane.insertTab(protocolsTab.id.title, protocolsTab.icon, protocolsTab.component,"protocols",ApplicationPanelTabID.protocols.ordinal());
			}
		}else if(protocolsTabIndexInPane != -1){
			tabbedPane.remove(protocolsTabIndexInPane);
		}
	}
	
	private void showOrHideFittingPanel() {
		ApplicationPanelTab tab = appPanelTabs[ApplicationPanelTabID.parameterEstimation.ordinal()];
		int index = tabbedPane.indexOfComponent(tab.component);
		if (simulationContext.isValidForFitting()) {
			if (index < 0) {
				tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
			}
			parameterEstimationPanel.setSimulationContext(simulationContext);
		} else {
			if (index >= 0) {
				Component selectedComponent = tabbedPane.getSelectedComponent();
				tabbedPane.remove(tab.component);
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

	public void setBioModelWindowManager(BioModelWindowManager newValue) {
		if (bioModelWindowManager == newValue) {
			return;
		}
		bioModelWindowManager = newValue;
		applicationGeometryPanel.setBioModelWindowManager(bioModelWindowManager);
		applicationProtocolsPanel.setBioModelWindowManager(newValue);
		applicationSimulationsPanel.setBioModelWindowManager(newValue);
		parameterEstimationPanel.setBioModelWindowManager(bioModelWindowManager);
	}
	
	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		applicationGeometryPanel.setSelectionManager(selectionManager);
		applicationSpecificationsPanel.setSelectionManager(selectionManager);
		applicationProtocolsPanel.setSelectionManager(selectionManager);
		applicationSimulationsPanel.setSelectionManager(selectionManager);
		parameterEstimationPanel.setSelectionManager(selectionManager);
	}
	
	private void selectTab(ApplicationPanelTabID tabid) {
		if (tabid.ordinal() == ApplicationPanelTabID.parameterEstimation.ordinal()) {
			showOrHideFittingPanel();
		}
		tabbedPane.setSelectedIndex(tabid.ordinal());
	}

	@Override
	protected void onActiveViewChange(ActiveView activeView) {
		super.onActiveViewChange(activeView);
		SimulationContext selectedSimContext = activeView.getSimulationContext();
		DocumentEditorTreeFolderClass folderClass = activeView.getDocumentEditorTreeFolderClass();
		if (folderClass == null) {
			return;
		}		
		switch (folderClass) {
		case GEOMETRY_NODE:
			selectTab(ApplicationPanelTabID.geometry);
			break;
		case SPECIFICATIONS_NODE:
			selectTab(ApplicationPanelTabID.settings);
			break;
		case PROTOCOLS_NODE:
			selectTab(ApplicationPanelTabID.protocols);
			break;
		case SIMULATIONS_NODE:
			selectTab(ApplicationPanelTabID.simulations);
			break;
		case PARAMETER_ESTIMATION_NODE:
			selectTab(ApplicationPanelTabID.parameterEstimation);
			setSelectedObjects(new Object[] { parameterEstimationPanel.getParameterEstimationTask() });
			break;
		}
	}
	
	@Override
	public void setIssueManager(IssueManager issueManager) {
		super.setIssueManager(issueManager);
		applicationGeometryPanel.setIssueManager(issueManager);
		applicationSpecificationsPanel.setIssueManager(issueManager);
		applicationProtocolsPanel.setIssueManager(issueManager);
		applicationSimulationsPanel.setIssueManager(issueManager);
		parameterEstimationPanel.setIssueManager(issueManager);
	}
}
