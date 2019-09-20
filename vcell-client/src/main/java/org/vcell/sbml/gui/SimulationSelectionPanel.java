/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sbml.gui;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import cbit.vcell.solver.Simulation;

/**
 * Insert the type's description here.
 * Creation date: (11/15/2006 5:05:26 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SimulationSelectionPanel extends JPanel {
	private cbit.vcell.solver.Simulation[] fieldSimulations = null;
	private boolean ivjConnPtoP2Aligning = false;
	private javax.swing.DefaultListModel ivjdefaultListModel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JList ivjSimList = null;
	private javax.swing.JScrollPane ivjSimListScrollPane = null;
	private javax.swing.ListSelectionModel ivjsimListSelectionModel = null;
	private javax.swing.JPanel ivjSimulationsListPanel = null;
	private javax.swing.JPanel ivjSimulationSummaryPanel = null;
	private cbit.vcell.solver.ode.gui.SimulationSummaryPanel ivjsimSummaryPanel = null;
	private cbit.vcell.solver.Simulation fieldSelectedSimulation = null;

class IvjEventHandler implements java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SimulationSelectionPanel.this.getSimList() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == SimulationSelectionPanel.this && (evt.getPropertyName().equals("simulations"))) 
				connEtoC1(evt);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == SimulationSelectionPanel.this.getsimListSelectionModel()) 
				connEtoC2(e);
		};
	};

/**
 * SimulationSelectionPanel constructor comment.
 */
public SimulationSelectionPanel() {
	super();
	initialize();
}

/**
 * SimulationSelectionPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public SimulationSelectionPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * SimulationSelectionPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public SimulationSelectionPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * SimulationSelectionPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public SimulationSelectionPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoC1:  (SimulationSelectionPanel.simulations --> SimulationSelectionPanel.fillSimulationList()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		this.fillSimulationList();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (simListSelectionModel.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> SimulationSelectionPanel.displaySimSummary()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
private void connEtoC2(javax.swing.event.ListSelectionEvent arg1) {
	try {
		this.displaySelectedSimSummary();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetTarget:  (defaultListModel.this <--> SimList.model)
 */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getSimList().setModel(getdefaultListModel());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetSource:  (SimList.selectionModel <--> simListSelectionModel.this)
 */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			ivjConnPtoP2Aligning = true;
			if ((getsimListSelectionModel() != null)) {
				getSimList().setSelectionModel(getsimListSelectionModel());
			}
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (SimList.selectionModel <--> simListSelectionModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			ivjConnPtoP2Aligning = true;
			setsimListSelectionModel(getSimList().getSelectionModel());
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private void displaySelectedSimSummary() {
	int index = getSimList().getSelectedIndex();

	Simulation sim = null;
	if (index >= 0) {
		sim = getSimulations(index);
		getsimSummaryPanel().setSimulation(sim);
	} else {
		getsimSummaryPanel().setSimulation(null);
	}

	setSelectedSimulation(sim);
}


/**
 * Comment
 */
private void fillSimulationList() {
	// Fill the Jlist with the simulation names
	for (int i = 0; i < getSimulations().length; i++){
		getdefaultListModel().addElement(getSimulations(i).getName());
	}

	// select the first name as default selection
	// getSimList().setSelectedIndex(0);
	
	return;
}


/**
 * Return the defaultListModel property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getdefaultListModel() {
	if (ivjdefaultListModel == null) {
		try {
			ivjdefaultListModel = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjdefaultListModel;
}


/**
 * Gets the selectedSimulation property (cbit.vcell.solver.Simulation) value.
 * @return The selectedSimulation property value.
 * @see #setSelectedSimulation
 */
public cbit.vcell.solver.Simulation getSelectedSimulation() {
	return fieldSelectedSimulation;
}


/**
 * Return the SimList property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getSimList() {
	if (ivjSimList == null) {
		try {
			ivjSimList = new javax.swing.JList();
			ivjSimList.setName("SimList");
			ivjSimList.setPreferredSize(new java.awt.Dimension(250, 125));
			ivjSimList.setBounds(0, 0, 160, 120);
			ivjSimList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSimList;
}


/**
 * Return the SimListScrollPane property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getSimListScrollPane() {
	if (ivjSimListScrollPane == null) {
		try {
			ivjSimListScrollPane = new javax.swing.JScrollPane();
			ivjSimListScrollPane.setName("SimListScrollPane");
			getSimListScrollPane().setViewportView(getSimList());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSimListScrollPane;
}


/**
 * Return the simListSelectionModel property value.
 * @return javax.swing.ListSelectionModel
 */
private javax.swing.ListSelectionModel getsimListSelectionModel() {
	return ivjsimListSelectionModel;
}


/**
 * Return the simSummaryPanel property value.
 * @return cbit.vcell.solver.ode.gui.SimulationSummaryPanel
 */
private cbit.vcell.solver.ode.gui.SimulationSummaryPanel getsimSummaryPanel() {
	if (ivjsimSummaryPanel == null) {
		try {
			ivjsimSummaryPanel = new cbit.vcell.solver.ode.gui.SimulationSummaryPanel();
			ivjsimSummaryPanel.setName("simSummaryPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjsimSummaryPanel;
}


/**
 * Gets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @return The simulations property value.
 * @see #setSimulations
 */
public cbit.vcell.solver.Simulation[] getSimulations() {
	return fieldSimulations;
}


/**
 * Gets the simulations index property (cbit.vcell.solver.Simulation) value.
 * @return The simulations property value.
 * @param index The index value into the property array.
 * @see #setSimulations
 */
public cbit.vcell.solver.Simulation getSimulations(int index) {
	return getSimulations()[index];
}


/**
 * Return the SimulationsListPanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getSimulationsListPanel() {
	if (ivjSimulationsListPanel == null) {
		try {
			org.vcell.util.gui.LineBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new org.vcell.util.gui.LineBorderBean();
			ivjLocalBorder1.setThickness(2);
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new org.vcell.util.gui.TitledBorderBean();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setBorder(ivjLocalBorder1);
			ivjLocalBorder.setTitleColor(new java.awt.Color(0,0,0));
//			ivjLocalBorder.setTitle("Select Simulation to Export");
			ivjSimulationsListPanel = new javax.swing.JPanel();
			ivjSimulationsListPanel.setName("SimulationsListPanel");
			ivjSimulationsListPanel.setPreferredSize(new java.awt.Dimension(120, 400));
			ivjSimulationsListPanel.setMinimumSize(new java.awt.Dimension(120, 400));
//			ivjSimulationsListPanel.setBorder(ivjLocalBorder);
			ivjSimulationsListPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSimListScrollPane = new java.awt.GridBagConstraints();
			constraintsSimListScrollPane.gridx = 0;
			constraintsSimListScrollPane.gridy = 0;
			constraintsSimListScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsSimListScrollPane.weightx = 1.0;
			constraintsSimListScrollPane.weighty = 1.0;
			constraintsSimListScrollPane.insets = new java.awt.Insets(4, 4, 4, 2);
			getSimulationsListPanel().add(getSimListScrollPane(), constraintsSimListScrollPane);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSimulationsListPanel;
}

/**
 * Return the SimulationSummaryPanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getSimulationSummaryPanel() {
	if (ivjSimulationSummaryPanel == null) {
		try {
			org.vcell.util.gui.LineBorderBean ivjLocalBorder3;
			ivjLocalBorder3 = new org.vcell.util.gui.LineBorderBean();
			ivjLocalBorder3.setThickness(2);
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder2;
			ivjLocalBorder2 = new org.vcell.util.gui.TitledBorderBean();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder2.setBorder(ivjLocalBorder3);
			ivjLocalBorder2.setTitleColor(new java.awt.Color(0,0,0));
			ivjLocalBorder2.setTitle("");
			ivjSimulationSummaryPanel = new javax.swing.JPanel();
			ivjSimulationSummaryPanel.setName("SimulationSummaryPanel");
//			ivjSimulationSummaryPanel.setBorder(ivjLocalBorder2);
			ivjSimulationSummaryPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintssimSummaryPanel = new java.awt.GridBagConstraints();
			constraintssimSummaryPanel.gridx = 0;
			constraintssimSummaryPanel.gridy = 0;
			constraintssimSummaryPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintssimSummaryPanel.weightx = 1.0;
			constraintssimSummaryPanel.weighty = 1.0;
			constraintssimSummaryPanel.insets = new java.awt.Insets(4, 2, 0, 2);
			getSimulationSummaryPanel().add(getsimSummaryPanel(), constraintssimSummaryPanel);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSimulationSummaryPanel;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	getSimList().addPropertyChangeListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}


/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("SimulationSelectionPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(607, 433);

		JButton button = new JButton();
		Color initialBackground = button.getBackground();
		String message = "The simulations in the list below have some initial conditions overriden. The right panel shows the overriden parameters.\n";
		message += "  Select one of them to export the overriden initial conditions and press OK, or\n";
		message += "  Press OK without selection to export the original initial conditions.";
		JTextPane textPane = new JTextPane();
		textPane.setText(message);
		textPane.setEditable(false);
		textPane.setBackground(initialBackground);
		
		int gridy = 0;
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(textPane, gbc);

		gridy++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.ipadx = 100;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSimulationsListPanel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSimulationSummaryPanel(), gbc);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SimulationSelectionPanel aSimulationSelectionPanel;
		aSimulationSelectionPanel = new SimulationSelectionPanel();
		frame.setContentPane(aSimulationSelectionPanel);
		frame.setSize(aSimulationSelectionPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Sets the selectedSimulation property (cbit.vcell.solver.Simulation) value.
 * @param selectedSimulation The new value for the property.
 * @see #getSelectedSimulation
 */
public void setSelectedSimulation(cbit.vcell.solver.Simulation selectedSimulation) {
	cbit.vcell.solver.Simulation oldValue = fieldSelectedSimulation;
	fieldSelectedSimulation = selectedSimulation;
	firePropertyChange("selectedSimulation", oldValue, selectedSimulation);
}


/**
 * Set the simListSelectionModel to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
private void setsimListSelectionModel(javax.swing.ListSelectionModel newValue) {
	if (ivjsimListSelectionModel != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjsimListSelectionModel != null) {
				ivjsimListSelectionModel.removeListSelectionListener(ivjEventHandler);
			}
			ivjsimListSelectionModel = newValue;

			/* Listen for events from the new object */
			if (ivjsimListSelectionModel != null) {
				ivjsimListSelectionModel.addListSelectionListener(ivjEventHandler);
			}
			connPtoP2SetSource();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}

/**
 * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @param simulations The new value for the property.
 * @see #getSimulations
 */
public void setSimulations(cbit.vcell.solver.Simulation[] simulations) {
	cbit.vcell.solver.Simulation[] oldValue = fieldSimulations;
	fieldSimulations = simulations;
	firePropertyChange("simulations", oldValue, simulations);
}


/**
 * Sets the simulations index property (cbit.vcell.solver.Simulation[]) value.
 * @param index The index value into the property array.
 * @param simulations The new value for the property.
 * @see #getSimulations
 */
public void setSimulations(int index, cbit.vcell.solver.Simulation simulations) {
	cbit.vcell.solver.Simulation oldValue = fieldSimulations[index];
	fieldSimulations[index] = simulations;
	if (oldValue != null && !oldValue.equals(simulations)) {
		firePropertyChange("simulations", null, fieldSimulations);
	};
}
}
