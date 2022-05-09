package org.vcell.model.springsalad.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.model.Model;

// we should use WindowBuilder Plugin (add it to Eclipse IDE) to speed up panel design
// can choose absolute layout and place everything exactly as we see fit
@SuppressWarnings("serial")
public class MolecularStructuresPanel extends DocumentEditorSubPanel implements ApplicationSpecificationsPanel.Specifier {

	private EventHandler eventHandler = new EventHandler();
	private SimulationContext fieldSimulationContext;
	private IssueManager fieldIssueManager;
	private SelectionManager fieldSelectionManager;
	
	private class EventHandler implements FocusListener, ActionListener, PropertyChangeListener {

		public void actionPerformed(ActionEvent e) {

		}
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
		}
		public void propertyChange(java.beans.PropertyChangeEvent event) {

		}
	}

	public MolecularStructuresPanel() {
		super();
		initialize();
	}
	
	@Override
	public ActiveViewID getActiveView() {
		return ActiveViewID.network_setting;
	}
	/**
	 * no-op 
	 */
	@Override
	public void setSearchText(String s) {
		
	}
	

	private void initialize() {

		JPanel thePanel = new JPanel();

		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();

		TitledBorder titleLeft = BorderFactory.createTitledBorder(loweredEtchedBorder, " Molecule Types ");
		titleLeft.setTitleJustification(TitledBorder.LEFT);
		titleLeft.setTitlePosition(TitledBorder.TOP);

		TitledBorder titleCenter = BorderFactory.createTitledBorder(loweredEtchedBorder, " Sites ");
		titleCenter.setTitleJustification(TitledBorder.LEFT);
		titleCenter.setTitlePosition(TitledBorder.TOP);
		
		TitledBorder titleRight = BorderFactory.createTitledBorder(loweredEtchedBorder, " Links ");
		titleRight.setTitleJustification(TitledBorder.LEFT);
		titleRight.setTitlePosition(TitledBorder.TOP);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(1, 1, 1, 1);
		add(thePanel, gbc);
		
		// ------------------------------------------- Populating the top group box ---------------
		JPanel left = new JPanel();
		JPanel center = new JPanel();
		JPanel right = new JPanel();
		
		left.setBorder(titleLeft);
		center.setBorder(titleCenter);
		right.setBorder(titleRight);
		
		thePanel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
		thePanel.add(left, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		thePanel.add(center, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		thePanel.add(right, gbc);
		
	}
	
	public void setSimulationContext(SimulationContext simulationContext) {
		if(simulationContext == null) {
			return;
		}
		if(fieldSimulationContext != null) {
			fieldSimulationContext.removePropertyChangeListener(eventHandler);
		}
		fieldSimulationContext = simulationContext;
		fieldSimulationContext.addPropertyChangeListener(eventHandler);
		
		Model m = fieldSimulationContext.getModel();
		if(m != null) {
			m.removePropertyChangeListener(eventHandler);
			m.addPropertyChangeListener(eventHandler);
		}

		refreshInterface();
	}
	public SimulationContext getSimulationContext() {
		return fieldSimulationContext;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		fieldSelectionManager = selectionManager;
	}
	public IssueManager getIssueManager() {
		return fieldIssueManager;
	}
	public void setIssueManager(IssueManager issueManager) {
		fieldIssueManager = issueManager;
	}
	
	
	
	private void appendToConsole(String string) {
		TaskCallbackMessage newCallbackMessage = new TaskCallbackMessage(TaskCallbackStatus.Error, string);
		fieldSimulationContext.appendToConsole(newCallbackMessage);
	}
	private void appendToConsole(TaskCallbackMessage newCallbackMessage) {
		fieldSimulationContext.appendToConsole(newCallbackMessage);
	}
	
	public void refreshInterface() {


	}
	

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {

	}

}

