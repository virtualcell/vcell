package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;

import cbit.vcell.client.GuiConstants;
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
		electrical("Electrical");
//		microscope_measurements("Microscope Measurements");
		
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
	
	public ApplicationProtocolsPanel() {
		super();
		initialize();
	}

	private void initialize(){
		eventsDisplayPanel = new EventsDisplayPanel();
		electricalMembraneMappingPanel = new ElectricalMembraneMappingPanel();
		microscopeMeasurementPanel = new MicroscopeMeasurementPanel();
		
		ProtocolsPanelTab simsPanelTabs[] = new ProtocolsPanelTab[ProtocolsPanelTabID.values().length]; 
		simsPanelTabs[ProtocolsPanelTabID.events.ordinal()] = new ProtocolsPanelTab(ProtocolsPanelTabID.events, eventsDisplayPanel, null);
		simsPanelTabs[ProtocolsPanelTabID.electrical.ordinal()] = new ProtocolsPanelTab(ProtocolsPanelTabID.electrical, electricalMembraneMappingPanel, null);
//		simsPanelTabs[ProtocolsPanelTabID.microscope_measurements.ordinal()] = new ProtocolsPanelTab(ProtocolsPanelTabID.microscope_measurements, microscopeMeasurementPanel, null);
		
		for (ProtocolsPanelTab tab : simsPanelTabs) {
			tab.component.setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
		}		
	}	
	
	@Override
	public void setSimulationContext(SimulationContext newValue) {
		super.setSimulationContext(newValue);
		eventsDisplayPanel.setSimulationContext(simulationContext);
		electricalMembraneMappingPanel.setSimulationContext(simulationContext);
		microscopeMeasurementPanel.setSimulationContext(simulationContext);
	}
	
	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		eventsDisplayPanel.setSelectionManager(selectionManager);
	}

	@Override
	public ActiveViewID getActiveViewID() {
		Component selectedComponent = tabbedPane.getSelectedComponent();
		if (selectedComponent == eventsDisplayPanel) {
			return ActiveViewID.events;
		}
		if (selectedComponent == electricalMembraneMappingPanel) {
			return ActiveViewID.electrical;
		}
		if (selectedComponent == microscopeMeasurementPanel) {
			return ActiveViewID.microscope_measuremments;
		}
		return null;
	}
	
}
