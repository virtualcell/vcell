package cbit.vcell.client.desktop.biomodel;

import javax.swing.Icon;
import javax.swing.JComponent;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class ApplicationFittingPanel extends ApplicationSubPanel {
	private ParameterEstimationPanel parameterEstimationPanel = null;
	
	private enum FittingsPanelTabID {
		parameter_estimation("Parameter Estimation");
		
		String title = null;
		FittingsPanelTabID(String name) {
			this.title = name;
		}
	}
	
	private class FittingsPanelTab {
		FittingsPanelTabID id;
		JComponent component = null;
		Icon icon = null;
		FittingsPanelTab(FittingsPanelTabID id, JComponent component, Icon icon) {
			this.id = id;
			this.component = component;
			this.icon = icon;
		}		
	}
	
	public ApplicationFittingPanel() {
		super();
		initialize();
	}

	private void initialize(){			
		parameterEstimationPanel = new ParameterEstimationPanel();		
		
		FittingsPanelTab fittingPanelTabs[] = new FittingsPanelTab[FittingsPanelTabID.values().length]; 
		fittingPanelTabs[FittingsPanelTabID.parameter_estimation.ordinal()] = new FittingsPanelTab(FittingsPanelTabID.parameter_estimation, parameterEstimationPanel, null);
		
		for (FittingsPanelTab tab : fittingPanelTabs) {
			tab.component.setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
		}		
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

	@Override
	public ActiveView getActiveView() {
		return new ActiveView(simulationContext, DocumentEditorTreeFolderClass.FITTING_NODE, ActiveViewID.parameter_estimation);
	}
}
