package org.vcell.sbml.gui;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Structure;
/**
 * Insert the type's description here.
 * Creation date: (5/12/2006 3:23:42 PM)
 * @author: Anuradha Lakshminarayana
 */
public class StructureSizeInputPanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JLabel ivjSizeLabel = null;
	private javax.swing.JTextField ivjSizeTextField = null;
	private javax.swing.JList ivjStructuresList = null;
	private javax.swing.JLabel ivjUnitsLabel = null;
	private cbit.vcell.model.Structure[] fieldStructures = null;
	private javax.swing.DefaultListModel ivjDefaultListModel1 = null;
	private double fieldStructureSize = 0;
	private java.lang.String fieldSelectedStructureName = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean ivjConnPtoP2Aligning = false;
	private javax.swing.ListSelectionModel ivjselectionModel1 = null;
	private javax.swing.JLabel ivjInfoLabel = null;

class IvjEventHandler implements java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == StructureSizeInputPanel.this && (evt.getPropertyName().equals("structures"))) 
				connEtoC1(evt);
			if (evt.getSource() == StructureSizeInputPanel.this.getStructuresList() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP2SetTarget();
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == StructureSizeInputPanel.this.getselectionModel1()) 
				connEtoC2(e);
		};
	};

/**
 * StructureSizeInputPanel constructor comment.
 */
public StructureSizeInputPanel() {
	super();
	initialize();
}

/**
 * StructureSizeInputPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public StructureSizeInputPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * StructureSizeInputPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public StructureSizeInputPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * StructureSizeInputPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public StructureSizeInputPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
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
 * connEtoC1:  (StructureSizeInputPanel.structures --> StructureSizeInputPanel.fillListOfStructures()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
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
 * connEtoC2:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> StructureSizeInputPanel.listSelectionUnit(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.listSelectionUnit(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (dafaultListModel1.this <--> StructuresList.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getStructuresList().setModel(getDefaultListModel1());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetSource:  (StructuresList.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getselectionModel1() != null)) {
				getStructuresList().setSelectionModel(getselectionModel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (StructuresList.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setselectionModel1(getStructuresList().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
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
		getDefaultListModel1().addElement(getStructures(i).getName());
	}

	// select the first structure as default selection
	getStructuresList().setSelectedIndex(0);
	
	// Initialize the size to have a value of 1.0 
	getSizeTextField().setText("1.0");
}


/**
 * Return the dafaultListModel1 property value.
 * @return javax.swing.DefaultListModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.DefaultListModel getDefaultListModel1() {
	if (ivjDefaultListModel1 == null) {
		try {
			ivjDefaultListModel1 = new javax.swing.DefaultListModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultListModel1;
}

/**
 * Return the InfoLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getInfoLabel() {
	if (ivjInfoLabel == null) {
		try {
			ivjInfoLabel = new javax.swing.JLabel();
			ivjInfoLabel.setName("InfoLabel");
			ivjInfoLabel.setFont(new java.awt.Font("Arial", 1, 12));
			ivjInfoLabel.setText("<html>Compartments in the Virtual Cell are specified in terms of relative measurements (i.e., surface_to_volume ratio and volumeFraction). SBML requires absolute sizes for compartments. Using the relative sizes of the compartments and the size of one compartment, all compartment sizes will be automatically computed. <br><br>Please specify size for one of the following comparments: </html>");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInfoLabel;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			getJScrollPane1().setViewportView(getStructuresList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
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
 * Return the selectionModel1 property value.
 * @return javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListSelectionModel getselectionModel1() {
	// user code begin {1}
	// user code end
	return ivjselectionModel1;
}


/**
 * Return the SizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSizeLabel() {
	if (ivjSizeLabel == null) {
		try {
			ivjSizeLabel = new javax.swing.JLabel();
			ivjSizeLabel.setName("SizeLabel");
			ivjSizeLabel.setText("Structure Size :");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeLabel;
}


/**
 * Return the SizeTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSizeTextField() {
	if (ivjSizeTextField == null) {
		try {
			ivjSizeTextField = new javax.swing.JTextField();
			ivjSizeTextField.setName("SizeTextField");
			ivjSizeTextField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
 * Gets the structures property (cbit.vcell.model.Structure[]) value.
 * @return The structures property value.
 * @see #setStructures
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getStructuresList() {
	if (ivjStructuresList == null) {
		try {
			ivjStructuresList = new javax.swing.JList();
			ivjStructuresList.setName("StructuresList");
			ivjStructuresList.setBounds(0, 0, 135, 196);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStructuresList;
}


/**
 * Return the UnitsLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUnitsLabel() {
	if (ivjUnitsLabel == null) {
		try {
			ivjUnitsLabel = new javax.swing.JLabel();
			ivjUnitsLabel.setName("UnitsLabel");
			ivjUnitsLabel.setText(" ");
			ivjUnitsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjUnitsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getStructuresList().addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("StructureSizeInputPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(372, 255);

		java.awt.GridBagConstraints constraintsSizeLabel = new java.awt.GridBagConstraints();
		constraintsSizeLabel.gridx = 1; constraintsSizeLabel.gridy = 1;
		constraintsSizeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSizeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSizeLabel(), constraintsSizeLabel);

		java.awt.GridBagConstraints constraintsUnitsLabel = new java.awt.GridBagConstraints();
		constraintsUnitsLabel.gridx = 2; constraintsUnitsLabel.gridy = 2;
		constraintsUnitsLabel.ipadx = 20;
		constraintsUnitsLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getUnitsLabel(), constraintsUnitsLabel);

		java.awt.GridBagConstraints constraintsSizeTextField = new java.awt.GridBagConstraints();
		constraintsSizeTextField.gridx = 1; constraintsSizeTextField.gridy = 2;
		constraintsSizeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSizeTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSizeTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSizeTextField(), constraintsSizeTextField);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
constraintsJScrollPane1.gridheight = 6;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane1(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsInfoLabel = new java.awt.GridBagConstraints();
		constraintsInfoLabel.gridx = 0; constraintsInfoLabel.gridy = 0;
		constraintsInfoLabel.gridwidth = 3;
		constraintsInfoLabel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsInfoLabel.weighty = 1.0;
		constraintsInfoLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getInfoLabel(), constraintsInfoLabel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void listSelectionUnit(javax.swing.event.ListSelectionEvent listSelectionEvent) {
	int index = getStructuresList().getSelectedIndex();
	Structure selectedStructure = getStructures(index);
	if (selectedStructure instanceof Feature) {
		getUnitsLabel().setText("[um3]");
	} else if (selectedStructure instanceof Membrane) {
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
		StructureSizeInputPanel aStructureSizeInputPanel;
		aStructureSizeInputPanel = new StructureSizeInputPanel();
		frame.setContentPane(aStructureSizeInputPanel);
		frame.setSize(aStructureSizeInputPanel.getSize());
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
 * Set the selectionModel1 to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectionModel1(javax.swing.ListSelectionModel newValue) {
	if (ivjselectionModel1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.removeListSelectionListener(ivjEventHandler);
			}
			ivjselectionModel1 = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.addListSelectionListener(ivjEventHandler);
			}
			connPtoP2SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}


/**
 * Sets the structures property (cbit.vcell.model.Structure[]) value.
 * @param structures The new value for the property.
 * @see #getStructures
 */
public void setStructures(cbit.vcell.model.Structure[] structures) {
	cbit.vcell.model.Structure[] oldValue = fieldStructures;
	fieldStructures = structures;
	firePropertyChange("structures", oldValue, structures);
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

}