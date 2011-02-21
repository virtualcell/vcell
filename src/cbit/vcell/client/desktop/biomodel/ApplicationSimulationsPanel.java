package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.GuiConstants;
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
	
	private enum SimulationsPanelTabID {
		simulations("Simulations"),
		output_functions("Output Functions"),
		generated_math("Generated Math");
		
		String title = null;
		SimulationsPanelTabID(String name) {
			this.title = name;
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

	private void initialize(){
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
	public ActiveViewID getActiveViewID() {
		Component selectedComponent = tabbedPane.getSelectedComponent();
		if (selectedComponent == simulationListPanel) {
			return ActiveViewID.simulations;
		}
		if (selectedComponent == outputFunctionsPanel) {
			return ActiveViewID.output_functions;
		}
		if (selectedComponent == mathematicsPanel) {
			return ActiveViewID.generated_math;
		}
		return null;
	}
}
