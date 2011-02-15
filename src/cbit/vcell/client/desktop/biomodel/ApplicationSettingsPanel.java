package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.gui.InitialConditionsPanel;
import cbit.vcell.mapping.gui.ReactionSpecsPanel;

@SuppressWarnings("serial")
public class ApplicationSettingsPanel extends ApplicationSubPanel {
	private InitialConditionsPanel initialConditionsPanel;
	private ReactionSpecsPanel reactionSpecsPanel;	
		
	public ApplicationSettingsPanel() {
		super();
		initialize();
	}

	private void initialize(){			
		initialConditionsPanel = new InitialConditionsPanel();
		reactionSpecsPanel = new ReactionSpecsPanel();
		
		initialConditionsPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		reactionSpecsPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		tabbedPane.addTab("Species Settings", initialConditionsPanel);
		tabbedPane.addTab("Reaction Settings", reactionSpecsPanel);
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
	public ActiveViewID getActiveViewID() {
		Component selectedComponent = tabbedPane.getSelectedComponent();
		if (selectedComponent == initialConditionsPanel) {
			return ActiveViewID.species_settings;
		}
		if (selectedComponent == reactionSpecsPanel) {
			return ActiveViewID.reaction_setting;
		}
		return null;
	}
}
