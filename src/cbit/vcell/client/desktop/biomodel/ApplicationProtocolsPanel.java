package cbit.vcell.client.desktop.biomodel;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel;
import cbit.vcell.mapping.gui.MicroscopeMeasurementPanel;

@SuppressWarnings("serial")
public class ApplicationProtocolsPanel extends ApplicationSubPanel {
	private EventsDisplayPanel eventsDisplayPanel;
	private ElectricalMembraneMappingPanel electricalMembraneMappingPanel;
	private MicroscopeMeasurementPanel microscopeMeasurementPanel;
	
	public ApplicationProtocolsPanel() {
		super();
		initialize();
	}

	private void initialize(){
		eventsDisplayPanel = new EventsDisplayPanel();
		electricalMembraneMappingPanel = new ElectricalMembraneMappingPanel();
		microscopeMeasurementPanel = new MicroscopeMeasurementPanel();
		
		eventsDisplayPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		electricalMembraneMappingPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		microscopeMeasurementPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		tabbedPane.addTab("Events", eventsDisplayPanel);
		tabbedPane.addTab("Electrical", electricalMembraneMappingPanel);
		tabbedPane.addTab("Microscope Measurements", microscopeMeasurementPanel);		
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
	
}
