/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vcell.util.gui.JTabbedPaneEnhanced;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public abstract class ApplicationSubPanel extends DocumentEditorSubPanel {
	protected JTabbedPane tabbedPane = null;
	protected BioModelWindowManager bioModelWindowManager;
	protected SimulationContext simulationContext;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private class InternalEventHandler implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == tabbedPane) {
				tabbedPaneSelectionChanged();
			}			
		}
	}
	
	protected void tabbedPaneSelectionChanged() {		
		ActiveView activeView = getActiveView();
		if (activeView != null && isShowing()) {
			setActiveView(activeView);
		}
		
	}
	
	public ApplicationSubPanel() {
		super();
	}

	protected void initialize(){		
		tabbedPane = new JTabbedPaneEnhanced();
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
	
	public abstract ActiveView getActiveView();
}
