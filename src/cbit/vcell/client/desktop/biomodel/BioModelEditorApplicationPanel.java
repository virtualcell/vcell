package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
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

import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.document.GeometryOwner;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class BioModelEditorApplicationPanel extends DocumentEditorSubPanel {
	private ApplicationGeometryPanel applicationGeometryPanel = null;
	private ApplicationSpecificationsPanel applicationSpecificationsPanel;
	private ApplicationProtocolsPanel applicationProtocolsPanel;
	private ApplicationSimulationsPanel applicationSimulationsPanel;
	private ApplicationFittingPanel applicationFittingPanel;
	private JTabbedPane tabbedPane = null;
	private int selectedIndex = -1;
	private String selectedTabTitle = null;
	private SimulationContext simulationContext;
	private BioModelWindowManager bioModelWindowManager;

	public enum ApplicationPanelTabID {
		geometry("Geometry"),
		settings("Specifications"),
		protocols("Protocols"),
		simulations("Simulations"),
		fitting("Fitting");
		
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
		applicationFittingPanel = new ApplicationFittingPanel();
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		appPanelTabs[ApplicationPanelTabID.geometry.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.geometry, applicationGeometryPanel, VCellIcons.geometryIcon);
		appPanelTabs[ApplicationPanelTabID.settings.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.settings, applicationSpecificationsPanel, VCellIcons.settingsIcon);
		appPanelTabs[ApplicationPanelTabID.protocols.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.protocols, applicationProtocolsPanel, VCellIcons.protocolsIcon);
		appPanelTabs[ApplicationPanelTabID.simulations.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.simulations, applicationSimulationsPanel, VCellIcons.simulationIcon);
		appPanelTabs[ApplicationPanelTabID.fitting.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.fitting, applicationFittingPanel, VCellIcons.fittingIcon);
		tabbedPane.addChangeListener(eventHandler);
		
		for (ApplicationPanelTab tab : appPanelTabs) {
			tab.component.setBorder(GuiConstants.TAB_PANEL_BORDER);
			if (tab.id != ApplicationPanelTabID.fitting) {
				tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
			}
		}
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
	}	

	public void tabbedPaneSelectionChanged() {
		int oldSelectedIndex = selectedIndex;
		selectedIndex = tabbedPane.getSelectedIndex();
		if (oldSelectedIndex == selectedIndex) {
			return;
		}
		if (oldSelectedIndex >= 0) {
			tabbedPane.setTitleAt(oldSelectedIndex, selectedTabTitle);
		}
		selectedTabTitle = tabbedPane.getTitleAt(selectedIndex);
		tabbedPane.setTitleAt(selectedIndex, "<html><b>" + selectedTabTitle + "</b></html>");
		
		ActiveView activeView = null;
		if (selectedIndex == ApplicationPanelTabID.geometry.ordinal()) {
			activeView = applicationGeometryPanel.getActiveView();
		} else if (selectedIndex == ApplicationPanelTabID.settings.ordinal()) {
			activeView = applicationSpecificationsPanel.getActiveView();
		} else if (selectedIndex == ApplicationPanelTabID.protocols.ordinal()) {
			activeView = applicationProtocolsPanel.getActiveView();
		} else if (selectedIndex == ApplicationPanelTabID.simulations.ordinal()) {
			activeView = applicationSimulationsPanel.getActiveView();
		} else if (selectedIndex == ApplicationPanelTabID.fitting.ordinal()) {
			activeView = applicationFittingPanel.getActiveView();
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
				simulationContext.getGeometry().precomputeAll();
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("showing application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				applicationGeometryPanel.setSimulationContext(simulationContext);
				applicationSpecificationsPanel.setSimulationContext(simulationContext);
				applicationProtocolsPanel.setSimulationContext(simulationContext);
				applicationSimulationsPanel.setSimulationContext(simulationContext);
				showOrHideFittingPanel();
			}
		};		
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2});
	}

	private void showOrHideFittingPanel() {
		ApplicationPanelTab tab = appPanelTabs[ApplicationPanelTabID.fitting.ordinal()];
		if (simulationContext.isValidForFitting()) {
			if (tabbedPane.indexOfComponent(tab.component) < 0) {
				tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
			}
			applicationFittingPanel.setSimulationContext(simulationContext);
		} else {
			if (tabbedPane.indexOfComponent(tab.component) >= 0) {
				tabbedPane.remove(tab.component);
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
		applicationFittingPanel.setBioModelWindowManager(bioModelWindowManager);
	}
	
	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		applicationGeometryPanel.setSelectionManager(selectionManager);
		applicationSpecificationsPanel.setSelectionManager(selectionManager);
		applicationProtocolsPanel.setSelectionManager(selectionManager);
		applicationSimulationsPanel.setSelectionManager(selectionManager);
		applicationFittingPanel.setSelectionManager(selectionManager);
	}
	
	private void selectTab(ApplicationPanelTabID tabid) {		
		tabbedPane.setSelectedIndex(tabid.ordinal());
	}

	@Override
	protected void onActiveViewChange(ActiveView activeView) {
		super.onActiveViewChange(activeView);
		SimulationContext selectedSimContext = activeView.getSimulationContext();
		DocumentEditorTreeFolderClass folderClass = activeView.getDocumentEditorTreeFolderClass();
		if (selectedSimContext != this.simulationContext || folderClass == null) {
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
		case FITTING_NODE:
			selectTab(ApplicationPanelTabID.fitting);
			break;
		}
	}
	
	@Override
	public void setIssueManager(IssueManager issueManager) {
		super.setIssueManager(issueManager);
		applicationGeometryPanel.setIssueManager(issueManager);
		applicationSpecificationsPanel.setIssueManager(issueManager);
		applicationSimulationsPanel.setIssueManager(issueManager);
		applicationFittingPanel.setIssueManager(issueManager);
	}
}
