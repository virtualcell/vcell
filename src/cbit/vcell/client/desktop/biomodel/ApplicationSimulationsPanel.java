package cbit.vcell.client.desktop.biomodel;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.simulation.OutputFunctionsPanel;
import cbit.vcell.client.desktop.simulation.SimulationListPanel;
import cbit.vcell.client.desktop.simulation.SimulationWorkspace;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class ApplicationSimulationsPanel extends ApplicationSubPanel {
	private SimulationListPanel simulationListPanel;
	private OutputFunctionsPanel outputFunctionsPanel;
	private MathematicsPanel mathematicsPanel;
	
	public ApplicationSimulationsPanel() {
		super();
		initialize();
	}

	private void initialize(){
		simulationListPanel = new SimulationListPanel();
		outputFunctionsPanel = new OutputFunctionsPanel();
		mathematicsPanel = new MathematicsPanel();
		
		simulationListPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		outputFunctionsPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		mathematicsPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		tabbedPane.addTab("Simulations", simulationListPanel);
		tabbedPane.addTab("Output Functions", outputFunctionsPanel);
		tabbedPane.addTab("Generated Math", mathematicsPanel);
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
}
