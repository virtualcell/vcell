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

import org.vcell.util.VCAssert;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Structure;


/**
 * Insert the type's description here.
 * Creation date: (11/13/2006 12:51:07 PM)
 * @author: Anuradha Lakshminarayana
 */
@SuppressWarnings("serial")
public class ApplnSelectionAndStructureSizeInputPanel extends javax.swing.JPanel {
	private javax.swing.JPanel ivjApplnListPanel = null;
	private javax.swing.JPanel ivjStructSizePanel = null;
	private javax.swing.JScrollPane ivjApplnNamesScrollPane = null;
	private javax.swing.JList<String> ivjApplnNamesList = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.DefaultListModel<String> ivjApplnNamesListModel = null;
	private boolean ivjConnPtoP3Aligning = false;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JLabel ivjSizeInfoLabel = null;
	private javax.swing.JLabel ivjSizeLabel = null;
	private javax.swing.JTextField ivjSizeTextField = null;
	private javax.swing.JList<String> ivjStructuresList = null;
	private javax.swing.DefaultListModel<String> ivjStructuresListModel = null;
	private javax.swing.ListSelectionModel ivjstructuresListSelectionModel = null;
	private javax.swing.JLabel ivjUnitsLabel = null;
	private Structure[] fieldStructures = null;
	private java.lang.String fieldSelectedStructureName = null;
	private double fieldStructureSize = 0;
	private SimulationContext fieldSimContext = null;
	private String sizeInfoLabelText = "<html>Compartments in the Virtual Cell are specified in terms of relative " +
			"measurements (i.e., surface_to_volume ratio and volumeFraction). SBML requires absolute sizes for " +
			"compartments. Using the relative sizes of the compartments and the size of one compartment, all " +
			"compartment sizes will be automatically computed. <br><br>Please specify size for one of the " +
			"following comparments: </html>";
	/**
	 * does user need to set structure sizes?
	 */
	private boolean needStructureSizes;

class IvjEventHandler implements java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ApplnSelectionAndStructureSizeInputPanel.this.getStructuresList() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == ApplnSelectionAndStructureSizeInputPanel.this && (evt.getPropertyName().equals("structures"))) 
				connEtoC4(evt);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == ApplnSelectionAndStructureSizeInputPanel.this.getstructuresListSelectionModel()) 
				connEtoC3(e);
			if (e.getSource() == ApplnSelectionAndStructureSizeInputPanel.this.getApplnNamesList()) 
				connEtoC5(e);
		};
	};

/**
 * ApplnSelectionAndStructureSizeInputPanel constructor comment.
 */
public ApplnSelectionAndStructureSizeInputPanel() {
	super();
	initialize();
}

/**
 * ApplnSelectionAndStructureSizeInputPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ApplnSelectionAndStructureSizeInputPanel(java.awt.LayoutManager layout) {
	super(layout);
}

public boolean isNeedStructureSizes() {
	return needStructureSizes;
}

/**
 * Comment
 */
public void applyStructureNameAndSizeValues() {
	int index = getStructuresList().getSelectedIndex();
	setSelectedStructureName(getStructures(index).getName());
	setStructureSize(Double.parseDouble(getSizeTextField().getText()));
}


/**
 * connEtoC3:  (structuresListSelectionModel.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> ApplnSelectionAndStructureSizeInputPanel.listSelectionUnit()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
private void connEtoC3(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.listSelectionUnit();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (ApplnSelectionAndStructureSizeInputPanel.structures --> ApplnSelectionAndStructureSizeInputPanel.fillListOfStructures()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
private void connEtoC4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fillListOfStructures();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (ApplnNamesList.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> ApplnSelectionAndStructureSizeInputPanel.setSelectedApplnNameAndEnableStructureSizePanel()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
private void connEtoC5(javax.swing.event.ListSelectionEvent arg1) {
	try {
		this.setSelectedSimContextAndEnableStructureSizePanel();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (defaultListModel1.this <--> JList1.model)
 */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getApplnNamesList().setModel(getApplnNamesListModel());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetTarget:  (StructuresListModel.this <--> StructuresList.model)
 */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getStructuresList().setModel(getStructuresListModel());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (StructuresList.selectionModel <--> structuresListSelectionModel.this)
 */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			ivjConnPtoP3Aligning = true;
			if ((getstructuresListSelectionModel() != null)) {
				getStructuresList().setSelectionModel(getstructuresListSelectionModel());
			}
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (StructuresList.selectionModel <--> structuresListSelectionModel.this)
 */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			ivjConnPtoP3Aligning = true;
			setstructuresListSelectionModel(getStructuresList().getSelectionModel());
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void fillListOfStructures() {
	if (getStructures() == null || getStructures().length == 0) {
		PopupGenerator.showErrorDialog(this, "No structures in selected BioModel");
	}

	// Fill the Jlist with the structure names
	for (int i = 0; i < getStructures().length; i++){
		getStructuresListModel().addElement(getStructures(i).getName());
	}

	// select the first structure as default selection
	getStructuresList().setSelectedIndex(0);
	
	// Initialize the size to have a value of 1.0 
	getSizeTextField().setText("1.0");
}


/**
 * Return the ApplnListPanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getApplnListPanel() {
	if (ivjApplnListPanel == null) {
		try {
			org.vcell.util.gui.LineBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new org.vcell.util.gui.LineBorderBean();
			ivjLocalBorder1.setThickness(2);
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new org.vcell.util.gui.TitledBorderBean();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setBorder(ivjLocalBorder1);
			ivjLocalBorder.setTitleColor(new java.awt.Color(0,0,0));
			ivjLocalBorder.setTitle("Select Application to Export");
			ivjApplnListPanel = new javax.swing.JPanel();
			ivjApplnListPanel.setName("ApplnListPanel");
			ivjApplnListPanel.setBorder(ivjLocalBorder);
			ivjApplnListPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsApplnNamesScrollPane = new java.awt.GridBagConstraints();
			constraintsApplnNamesScrollPane.gridx = 0; constraintsApplnNamesScrollPane.gridy = 0;
			constraintsApplnNamesScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsApplnNamesScrollPane.weightx = 1.0;
			constraintsApplnNamesScrollPane.weighty = 1.0;
			constraintsApplnNamesScrollPane.insets = new java.awt.Insets(4, 4, 4, 4);
			getApplnListPanel().add(getApplnNamesScrollPane(), constraintsApplnNamesScrollPane);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjApplnListPanel;
}

/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
private javax.swing.JList<String> getApplnNamesList() {
	if (ivjApplnNamesList == null) {
		try {
			ivjApplnNamesList = new javax.swing.JList<>();
			ivjApplnNamesList.setName("ApplnNamesList");
			ivjApplnNamesList.setBounds(0, 0, 160, 120);
			ivjApplnNamesList.setSelectedIndex(0);
			ivjApplnNamesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjApplnNamesList;
}

/**
 * Return the defaultListModel1 property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel<String> getApplnNamesListModel() {
	if (ivjApplnNamesListModel == null) {
		try {
			ivjApplnNamesListModel = new javax.swing.DefaultListModel<>();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjApplnNamesListModel;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getApplnNamesScrollPane() {
	if (ivjApplnNamesScrollPane == null) {
		try {
			ivjApplnNamesScrollPane = new javax.swing.JScrollPane();
			ivjApplnNamesScrollPane.setName("ApplnNamesScrollPane");
			getApplnNamesScrollPane().setViewportView(getApplnNamesList());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjApplnNamesScrollPane;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			getJScrollPane1().setViewportView(getStructuresList());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}


/**
 * Gets the selectedSimContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The selectedSimContext property value.
 * @see #setSelectedSimContext
 */
public cbit.vcell.mapping.SimulationContext getSelectedSimContext() {
	return fieldSimContext;
}


/**
 * Gets the selectedStructureName property (java.lang.String) value.
 * @return The selectedStructureName property value.
 * @see #setSelectedStructureName
 */
public java.lang.String getSelectedStructureName() {
	return fieldSelectedStructureName;
}

/**
 * Return the SizeInfoLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getSizeInfoLabel() {
	if (ivjSizeInfoLabel == null) {
		try {
			ivjSizeInfoLabel = new javax.swing.JLabel();
			ivjSizeInfoLabel.setName("SizeInfoLabel");
			ivjSizeInfoLabel.setPreferredSize(new java.awt.Dimension(2339, 152));
			ivjSizeInfoLabel.setText("<html>Compartments in the Virtual Cell are specified in terms of relative measurements (i.e., surface_to_volume ratio and volumeFraction). SBML requires absolute sizes for compartments. Using the relative sizes of the compartments and the size of one compartment, all compartment sizes will be automatically computed. <br><br>Please specify size for one of the following comparments: </html>");
			ivjSizeInfoLabel.setMinimumSize(new java.awt.Dimension(97, 152));
			ivjSizeInfoLabel.setMaximumSize(new java.awt.Dimension(2147483647, 152));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSizeInfoLabel;
}


/**
 * Return the SizeLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getSizeLabel() {
	if (ivjSizeLabel == null) {
		try {
			ivjSizeLabel = new javax.swing.JLabel();
			ivjSizeLabel.setName("SizeLabel");
			ivjSizeLabel.setText("Structure Size:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSizeLabel;
}


/**
 * Return the SizeTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getSizeTextField() {
	if (ivjSizeTextField == null) {
		try {
			ivjSizeTextField = new javax.swing.JTextField();
			ivjSizeTextField.setName("SizeTextField");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSizeTextField;
}


/**
 * Comment
 */
public int getStructSelectionIndex() {
	return getStructuresList().getSelectedIndex();
}


/**
 * Return the StructSizePanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getStructSizePanel() {
	if (ivjStructSizePanel == null) {
		try {
			org.vcell.util.gui.LineBorderBean ivjLocalBorder3;
			ivjLocalBorder3 = new org.vcell.util.gui.LineBorderBean();
			ivjLocalBorder3.setThickness(2);
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder2;
			ivjLocalBorder2 = new org.vcell.util.gui.TitledBorderBean();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder2.setBorder(ivjLocalBorder3);
			ivjLocalBorder2.setTitleColor(new java.awt.Color(0,0,0));
			ivjLocalBorder2.setTitle("Select Structure and Set Size");
			ivjStructSizePanel = new javax.swing.JPanel();
			ivjStructSizePanel.setName("StructSizePanel");
			ivjStructSizePanel.setBorder(ivjLocalBorder2);
			ivjStructSizePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSizeInfoLabel = new java.awt.GridBagConstraints();
			constraintsSizeInfoLabel.gridx = 0; constraintsSizeInfoLabel.gridy = 0;
			constraintsSizeInfoLabel.gridwidth = 3;
			constraintsSizeInfoLabel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsSizeInfoLabel.weighty = 1.0;
			constraintsSizeInfoLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getStructSizePanel().add(getSizeInfoLabel(), constraintsSizeInfoLabel);

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
			constraintsJScrollPane1.gridheight = 6;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
			getStructSizePanel().add(getJScrollPane1(), constraintsJScrollPane1);

			java.awt.GridBagConstraints constraintsSizeLabel = new java.awt.GridBagConstraints();
			constraintsSizeLabel.gridx = 1; constraintsSizeLabel.gridy = 1;
			constraintsSizeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSizeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getStructSizePanel().add(getSizeLabel(), constraintsSizeLabel);

			java.awt.GridBagConstraints constraintsSizeTextField = new java.awt.GridBagConstraints();
			constraintsSizeTextField.gridx = 1; constraintsSizeTextField.gridy = 2;
			constraintsSizeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSizeTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getStructSizePanel().add(getSizeTextField(), constraintsSizeTextField);

			java.awt.GridBagConstraints constraintsUnitsLabel = new java.awt.GridBagConstraints();
			constraintsUnitsLabel.gridx = 2; constraintsUnitsLabel.gridy = 2;
			constraintsUnitsLabel.ipadx = 20;
			constraintsUnitsLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getStructSizePanel().add(getUnitsLabel(), constraintsUnitsLabel);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStructSizePanel;
}

/**
 * Insert the method's description here.
 * Creation date: (11/14/2006 3:33:51 PM)
 * @param newStructures cbit.vcell.model.Structure[]
 */
public cbit.vcell.model.Structure[] getStructures() {
	return fieldStructures;
}


/**
 * Gets the structures index property (cbit.vcell.model.Structure) value.
 * @return The structures property value.
 * @param index The index value into the property array.
 * @see #setStructures
 */
public cbit.vcell.model.Structure getStructures(int index) {
	return getStructures()[index];
}


/**
 * Gets the structureSize property (double) value.
 * @return The structureSize property value.
 * @see #setStructureSize
 */
public double getStructureSize() {
	return fieldStructureSize;
}


/**
 * Return the StructuresList property value.
 * @return javax.swing.JList
 */
private javax.swing.JList<String> getStructuresList() {
	if (ivjStructuresList == null) {
		try {
			ivjStructuresList = new javax.swing.JList<>();
			ivjStructuresList.setName("StructuresList");
			ivjStructuresList.setBounds(0, 0, 160, 120);
			ivjStructuresList.setSelectedIndex(0);
			ivjStructuresList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStructuresList;
}

/**
 * Return the StructuresListModel property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel<String> getStructuresListModel() {
	if (ivjStructuresListModel == null) {
		try {
			ivjStructuresListModel = new javax.swing.DefaultListModel<>();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStructuresListModel;
}


/**
 * Return the structuresListSelectionModel property value.
 * @return javax.swing.ListSelectionModel
 */
private javax.swing.ListSelectionModel getstructuresListSelectionModel() {
	return ivjstructuresListSelectionModel;
}


/**
 * Return the UnitsLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getUnitsLabel() {
	if (ivjUnitsLabel == null) {
		try {
			ivjUnitsLabel = new javax.swing.JLabel();
			ivjUnitsLabel.setName("UnitsLabel");
			ivjUnitsLabel.setText("    ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUnitsLabel;
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
	getStructuresList().addPropertyChangeListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getApplnNamesList().addListSelectionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ApplnSelectionAndStructureSizeInputPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(489, 379);

		java.awt.GridBagConstraints constraintsApplnListPanel = new java.awt.GridBagConstraints();
		constraintsApplnListPanel.gridx = 0; constraintsApplnListPanel.gridy = 0;
		constraintsApplnListPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsApplnListPanel.weightx = 1.0;
		constraintsApplnListPanel.weighty = 1.0;
		constraintsApplnListPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getApplnListPanel(), constraintsApplnListPanel);

		java.awt.GridBagConstraints constraintsStructSizePanel = new java.awt.GridBagConstraints();
		constraintsStructSizePanel.gridx = 0; constraintsStructSizePanel.gridy = 1;
		constraintsStructSizePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsStructSizePanel.weightx = 1.0;
		constraintsStructSizePanel.weighty = 1.0;
		constraintsStructSizePanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getStructSizePanel(), constraintsStructSizePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void listSelectionUnit() {
	int index = getStructuresList().getSelectedIndex();
	cbit.vcell.model.Structure selectedStructure = getStructures(index);
	if (selectedStructure instanceof cbit.vcell.model.Feature) {
		getUnitsLabel().setText("[um3]");
	} else if (selectedStructure instanceof cbit.vcell.model.Membrane) {
		getUnitsLabel().setText("[um2]");
	}
	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ApplnSelectionAndStructureSizeInputPanel aApplnSelectionAndStructureSizeInputPanel;
		aApplnSelectionAndStructureSizeInputPanel = new ApplnSelectionAndStructureSizeInputPanel();
		frame.setContentPane(aApplnSelectionAndStructureSizeInputPanel);
		frame.setSize(aApplnSelectionAndStructureSizeInputPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private void setSelectedSimContextAndEnableStructureSizePanel() {
	boolean nonSpatial = fieldSimContext.getGeometry().getDimension() == 0;
	if (nonSpatial) {
		// Check if selected application has its structure sizes set; if so, disable the structureSize panel
		needStructureSizes = !fieldSimContext.getGeometryContext().isAllSizeSpecifiedPositive();
		enableStructureSizePanel(needStructureSizes);
	} 
	else {
		needStructureSizes = false;
	}
}

private void enableStructureSizePanel(boolean bEnable) {
	getStructSizePanel().setEnabled(bEnable);
	
	if (bEnable) {
		getSizeInfoLabel().setText(sizeInfoLabelText);
	} else {
		getSizeInfoLabel().setText("<html>Structure sizes have ALREADY BEEN SET for selected Application.</html>");
	}

	getSizeInfoLabel().setEnabled(true);
	getJScrollPane1().setEnabled(bEnable);
	getStructuresList().setEnabled(bEnable);
	getSizeLabel().setEnabled(bEnable);
	getSizeTextField().setEnabled(bEnable);
	getUnitsLabel().setEnabled(bEnable);
}

/**
 * Sets the selectedSimContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param selectedSimContext The new value for the property.
 */
public void setSimContext(cbit.vcell.mapping.SimulationContext application) {
	fieldSimContext = application;
	VCAssert.assertValid(application);

	getApplnNamesListModel().addElement(application.getName());

	// select the first name as default selection
	getApplnNamesList().setSelectedIndex(0);
	// if this simContext  has its sizes set, disable the structureSizePanel
	boolean allSet = application.getGeometryContext().isAllSizeSpecifiedPositive();
	// disable structureSizePanel
	enableStructureSizePanel(!allSet);
}


/**
 * Sets the selectedStructureName property (java.lang.String) value.
 * @param selectedStructureName The new value for the property.
 * @see #getSelectedStructureName
 */
public void setSelectedStructureName(java.lang.String selectedStructureName) {
	String oldValue = fieldSelectedStructureName;
	fieldSelectedStructureName = selectedStructureName;
	firePropertyChange("selectedStructureName", oldValue, selectedStructureName);
}

/**
 * Insert the method's description here.
 * Creation date: (11/14/2006 3:33:51 PM)
 * @param newStructures cbit.vcell.model.Structure[]
 */
public void setStructures(cbit.vcell.model.Structure[] newStructures) {
	cbit.vcell.model.Structure[] oldValue = fieldStructures;
	fieldStructures = newStructures;
	firePropertyChange("structures", oldValue, newStructures);
}


/**
 * Sets the structures index property (cbit.vcell.model.Structure[]) value.
 * @param index The index value into the property array.
 * @param structures The new value for the property.
 * @see #getStructures
 */
public void setStructures(int index, cbit.vcell.model.Structure structures) {
	cbit.vcell.model.Structure oldValue = fieldStructures[index];
	fieldStructures[index] = structures;
	if (oldValue != null && !oldValue.equals(structures)) {
		firePropertyChange("structures", null, fieldStructures);
	};
}


/**
 * Sets the structureSize property (double) value.
 * @param structureSize The new value for the property.
 * @see #getStructureSize
 */
public void setStructureSize(double structureSize) {
	double oldValue = fieldStructureSize;
	fieldStructureSize = structureSize;
	firePropertyChange("structureSize", new Double(oldValue), new Double(structureSize));
}


/**
 * Set the structuresListSelectionModel to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
private void setstructuresListSelectionModel(javax.swing.ListSelectionModel newValue) {
	if (ivjstructuresListSelectionModel != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjstructuresListSelectionModel != null) {
				ivjstructuresListSelectionModel.removeListSelectionListener(ivjEventHandler);
			}
			ivjstructuresListSelectionModel = newValue;

			/* Listen for events from the new object */
			if (ivjstructuresListSelectionModel != null) {
				ivjstructuresListSelectionModel.addListSelectionListener(ivjEventHandler);
			}
			connPtoP3SetSource();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
}
