package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.gui.InitialConditionsPanel;
import cbit.vcell.mapping.gui.ReactionSpecsPanel;

@SuppressWarnings("serial")
public class ApplicationSpecificationsPanel extends ApplicationSubPanel {
	private InitialConditionsPanel initialConditionsPanel;
	private ReactionSpecsPanel reactionSpecsPanel;	
		
	private enum SettingsPanelTabID {
		species_settings("Species"),
		reaction_settings("Reactions");
		
		String title = null;
		SettingsPanelTabID(String name) {
			this.title = name;
		}
	}
	
	private class SettingsPanelTab {
		SettingsPanelTabID id;
		JComponent component = null;
		Icon icon = null;
		SettingsPanelTab(SettingsPanelTabID id, JComponent component, Icon icon) {
			this.id = id;
			this.component = component;
			this.icon = icon;
		}		
	}
	
	public ApplicationSpecificationsPanel() {
		super();
		initialize();
	}

	private void initialize(){			
		initialConditionsPanel = new InitialConditionsPanel();
		reactionSpecsPanel = new ReactionSpecsPanel();
		
		SettingsPanelTab settingsPanelTabs[] = new SettingsPanelTab[SettingsPanelTabID.values().length]; 
		settingsPanelTabs[SettingsPanelTabID.species_settings.ordinal()] = new SettingsPanelTab(SettingsPanelTabID.species_settings, initialConditionsPanel, null);
		settingsPanelTabs[SettingsPanelTabID.reaction_settings.ordinal()] = new SettingsPanelTab(SettingsPanelTabID.reaction_settings, reactionSpecsPanel, null);
		
		for (SettingsPanelTab tab : settingsPanelTabs) {
			tab.component.setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
		}	
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
