package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class BioModelEditorApplicationPanel extends DocumentEditorSubPanel {
	private ApplicationGeometryPanel applicationGeometryPanel = null;
	private ApplicationSettingsPanel applicationSettingsPanel;
	private ApplicationProtocolsPanel applicationProtocolsPanel;
	private ApplicationSimulationsPanel applicationSimulationsPanel;
	private ApplicationFittingPanel applicationAnalysisPanel;
	private JTabbedPane tabbedPane = null;
	private int selectedIndex = -1;
	private String selectedTabTitle = null;
	private SimulationContext simulationContext;
	private BioModelWindowManager bioModelWindowManager;

	public enum ApplicationPanelTabID {
		geometry("Geometry"),
		settings("Settings"),
		protocols("Protocols"),
		simulations("Simulations"),
		fitting("Fitting");
		
		String title = null;
		ApplicationPanelTabID(String name) {
			this.title = name;
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
	
	private ApplicationPanelTab modelPanelTabs[] = new ApplicationPanelTab[ApplicationPanelTabID.values().length]; 
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private class InternalEventHandler implements ActionListener, ChangeListener {		
				
		public void actionPerformed(ActionEvent e) {
			
		}

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == tabbedPane) {
				tabbedPaneSelectionChanged();
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
		applicationSettingsPanel = new ApplicationSettingsPanel();
		applicationSimulationsPanel = new ApplicationSimulationsPanel();
		applicationAnalysisPanel = new ApplicationFittingPanel();
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		modelPanelTabs[ApplicationPanelTabID.geometry.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.geometry, applicationGeometryPanel, VCellIcons.geometryIcon);
		modelPanelTabs[ApplicationPanelTabID.settings.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.settings, applicationSettingsPanel, VCellIcons.settingsIcon);
		modelPanelTabs[ApplicationPanelTabID.protocols.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.protocols, applicationProtocolsPanel, VCellIcons.protocolsIcon);
		modelPanelTabs[ApplicationPanelTabID.simulations.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.simulations, applicationSimulationsPanel, VCellIcons.simulationIcon);
		modelPanelTabs[ApplicationPanelTabID.fitting.ordinal()] = new ApplicationPanelTab(ApplicationPanelTabID.fitting, applicationAnalysisPanel, VCellIcons.fittingIcon);
		tabbedPane.addChangeListener(eventHandler);
		
		for (ApplicationPanelTab tab : modelPanelTabs) {
			tab.component.setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
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
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
	}
	
	public void setSimulationContext(SimulationContext newValue) {
		if (simulationContext == newValue) {
			return;
		}
		simulationContext = newValue;
		applicationGeometryPanel.setSimulationContext(newValue);
		applicationSettingsPanel.setSimulationContext(newValue);
		applicationProtocolsPanel.setSimulationContext(newValue);
		applicationSimulationsPanel.setSimulationContext(newValue);
		applicationAnalysisPanel.setSimulationContext(newValue);
	}

	public void setBioModelWindowManager(BioModelWindowManager newValue) {
		if (bioModelWindowManager == newValue) {
			return;
		}
		bioModelWindowManager = newValue;
		applicationProtocolsPanel.setBioModelWindowManager(newValue);
		applicationSimulationsPanel.setBioModelWindowManager(newValue);
		applicationAnalysisPanel.setBioModelWindowManager(bioModelWindowManager);
	}
	
	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		applicationGeometryPanel.setSelectionManager(selectionManager);
		applicationSettingsPanel.setSelectionManager(selectionManager);
		applicationProtocolsPanel.setSelectionManager(selectionManager);
		applicationSimulationsPanel.setSelectionManager(selectionManager);
		applicationAnalysisPanel.setSelectionManager(selectionManager);
	}
	
	public void selectTab(ApplicationPanelTabID tabid) {		
		tabbedPane.setSelectedIndex(tabid.ordinal());
	}	
}
