package cbit.vcell.client.desktop.biomodel;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class ApplicationFittingPanel extends ApplicationSubPanel {
	private ParameterEstimationPanel parameterEstimationPanel = null;
	
	public ApplicationFittingPanel() {
		super();
		initialize();
	}

	private void initialize(){			
		parameterEstimationPanel = new ParameterEstimationPanel();
		
		parameterEstimationPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		tabbedPane.addTab("Parameter Estimation", parameterEstimationPanel);
	}
	
	@Override
	public void setSimulationContext(SimulationContext newValue) {
		super.setSimulationContext(newValue);
		parameterEstimationPanel.setSimulationContext(simulationContext);
	}
	
	@Override
	public void setBioModelWindowManager(BioModelWindowManager newValue) {
		super.setBioModelWindowManager(newValue);
		parameterEstimationPanel.setOptimizationService(bioModelWindowManager.getOptimizationService());
	}

	@Override
	public void setSelectionManager(SelectionManager selectionManager) {
		super.setSelectionManager(selectionManager);
		parameterEstimationPanel.setSelectionManager(selectionManager);
	}
}
