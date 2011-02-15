package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public abstract class ApplicationSubPanel extends DocumentEditorSubPanel {
	protected JTabbedPane tabbedPane = null;
	protected BioModelWindowManager bioModelWindowManager;
	protected SimulationContext simulationContext;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	private int selectedIndex = -1;
	private String selectedTabTitle = null;
	
	private class InternalEventHandler implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == tabbedPane) {
				tabbedPaneSelectionChanged();
			}			
		}
	}
	
	protected void tabbedPaneSelectionChanged() {
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
		setSelectedObjects(new Object[0]);
	}
	
	public ApplicationSubPanel() {
		super();
		initialize();
	}

	private void initialize(){		
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);	
		tabbedPane.addChangeListener(eventHandler);
	}	
	
	public void setSimulationContext(SimulationContext newValue) {
		if (simulationContext == newValue) {
			return;
		}
		simulationContext = newValue;
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1 || !(selectedObjects[0] instanceof SimulationContext)) {
			return;
		}
		setSimulationContext((SimulationContext) selectedObjects[0]);
	}
	
	public void setBioModelWindowManager(BioModelWindowManager newValue) {
		if (bioModelWindowManager == newValue) {
			return;
		}
		bioModelWindowManager = newValue;
	}
	
	public abstract ActiveViewID getActiveViewID();
}
