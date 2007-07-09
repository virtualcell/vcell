/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.vcell.vcml.gui;
import org.vcell.util.gui.DialogUtils;

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
		DialogUtils.showErrorDialog("No structures in selected BioModel");
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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6C0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BF494D51600FC22C48D889A9998836A18D156419D76CC56E167B033FA96C756E1F61D1119E5950FF1453368F0C6F68E63627252891F1084DA7E4EA03602CE5050C9B0A2460C64577959E0564490B18E2924AB69066A2E262A1AF4C2C2765E77292EF42A93754C6664262A6EFB77736E3B773E7BDED5481E5752B6E7B97690123519701F773388D97A81A1D757F65788DCCA28AD0FD8FEDE87F819DC
	F06440B58D5A22F73565B9097352B6F8DE8C4FC58B5A7276437DEE32771272AE5CA410D38BED5F3E780729E34BF9710A103315BEBF151B8F579DG990043EB0E0A727FBB77G472F62F881D98EC2D653BE7B369F643886F86E84188B30C8E57D1741B517CA1ED2FE12776B1811CE567FD4F394A09F8BCF8105E1C66B65D797F9B1EBFD98E51D5A1E4FE9A6437392G9CFCB639236198DCAB1AAF76FE545D95B05C173BE5C5013FC1253C3C4550A25DC6C413DB8283F2E3A89CB11A25102CC07F70A564B9ED7BB64A
	031013703C11624E9EC3B90B61FEB9C02609FF410D78CA2DAD2F9DA02C09317D79CF95DC7776ABBFA0193D8F666ECCB036A40D0DE911969B1B6B6D2E6CD768BC148F213D57C21BD3EF4BBB8250G6087C88278A31D7F2A413761DA55E5542841209A2A8A072B951FF6DA75498A5C1717C38B47FDAAF72B1A0F90B6471E6DC5B67A0C850C2E65EB53B11B243FAB66FB61277712D55F3F59315546A62B522FB852F4E60B8CBDE693A6BBB9216C157AC8FF9CA97BAC314AA64F9E5F3A59C67641DF9E5F3A1673A1C6CC
	76C983677AE16A83AE384F6236E07847A8FE5002E73E79A5456373ACE85BE0885FE85FA1F47166ACA04E52DBEC6D107A13E4C7C70469E0C462F569ADC65EE991A6B35754452078A596BC53657754CF71F9A534D5DCEE4BE37ED5B30C311A864FCBG9E85D88B105F5716D788D051A7FC6C3A41622F68E34D12E6D4C7820ACF5688232D995A87D7B9EAE8D2E3C8B724D0375CA6E98129CB11F5427BF8075086DDA3687E177AFEB7482817C332A69981B5845C2520EC481A4E7A1C9BFA997BE8B2EDED8D84E590908C
	934137EBE8B7DC95C9B7BE8B7BA4C30E75E06D970650A61725B660888C601E59A5CC50DE9DB0FE83E032E90727A93F0FE58D92C6F939D7D5AFC6422DB89272E89F5A79F61ABBD261FED11F30311205388351363C122860F3706AAE8B1F5AC0B72AAFE97DA246FECEFD27967A7B81122242F71EEF973ED3DB70B724689733EDFDA7857C780EA87319A55178987AF74A9B754B04361CFE11733B0F35A7082D24E8AC367CD17BB85B41759DBBF78CA6143155F23FB9C19F046CB15A183DFF6D40719D01F1D582F4768B
	1B1F3D6AE2BEDCAD6932705ACFB4F0B859971B8126E7305849E56D677AECB547B0C05B2240B78DE09940BA53061B5C6D96BD855DD839F17FB86D23EDF82058AA83F13963500DE54FF8E036BABD0C6B15FCD98E9935F2D88E7960C667ED2D43A80F47F30FEA521C2378B044FF8D841CEE62BB07B1C6A5435082DD91C3EE118DA3906A0D357B29ACBD52950E258EA1CB9DC65D55BE08FF333FB10C31532D06C2B21DB4130437DF2631ABABF290142ED1A3A10361878956B89765FE1EBBA610AD84E3DF09E09EE2F543
	A2890B2330968802ABE0AAA37E810C6B7B379F305868G60307E2900EB7300084D3E43A2B60F8CFC17ACBD9FF6582DB129F75E68D8B44062640101D8ECB21DAEE5596B5EC7F388476BCC574D9750F792D5B359146E37BA5A3ED66E11A20A618D6886CD59255CF7F6138F5F2636874654143C1545516E9C54A593F45C00B638A2626805ACEC97390B87D00D2AF09E84EB9E34599B6EBD16F197DEE1B6437BE5347D0C591E864F9916F6A6E73543C9F53B9411E14940A47B31B9F74FB8F645353571F9E46D3FF160BC
	AA167C2C8C224CFB0BC5ED6A9CE4792F902EA7815A818E81BEFF887B8D1508FE9903CC2F957C3AE6106948783DDFAA7A358FB1BE61A116D313072C7C7E29CC741B4B5B978FB1FE64AA63476674052368E734EE3DA0DD1622EE3D8FE2253C3C09CEAA2BCF0F64ED637D9B2E0ABA34F9874E5584F8CE85D8F4D578644F8BC49DBA6BE2BAD975EC6956C4B576B22E475A2BB11FDC7C1B2AAD4C97261EC219C543607343428FAEE4DB5742AADDD73B83F49966F370CA0E55E7996ED04EF6387684B45DE80424929573F5
	A4877545F51A2339BD264BEF585AA35E23C51DB7D4E79D6F121DC3548F6C465BBC4C46999E0E0D77C830F5AB2B17A234DE4298D9846DEBGDC93907F37EE4CA1B847CCDE773BC2DE4A401D24F3761C0472FC93181C4993E2720A2EEE584C64BDD6AC64AD017675GACBEBFAAC1F906BF203318BAD3E40D29220910FFG0C0922FFCF895AAD48CC42ED725B2ACA5EFF793D8BC84656E4075DDE44FFD72AA3E9A25F0BCC0C69D8B63FAB3B810069791D12FCBEB7E492787BA4A137209467D59739484C27FFFAE7BF1D
	EFC541297A24474CD6FA6151B62E5F025DD67DAA067FE22BDF52CE3FA3BC096915BAA926DF434C2E6C0E19C23F4ED2E147CC68F3709A215FB2778E921F67C48E3C2FCC581871D8D7A646588A7449A60F336F211E8A0C23C570F153552346AF05FAE5DF2145C35DF469F4FB2D1DBD08BA4A6C327BF0F9BC2F4FC28123507BD5F8FC8826633540513C787D50835744EA3015570C2E4758BE61811AAF56DE437689F9A67EDFA8BEFFF28C4FEC52C67178DC84EDA913C5ED7BD295662B75706C1C82399420922093E042
	9411A7966660714418FB0746901474A1C9E999D184705CB07D945A9F56ED7EFC63A934A135BC91B55F459A0C0B39A0B383E085409A00EDD3058E7BAA4755419335550159790A9B6DDCB8B5F1FDEBDF2BDDCB69AA3F869D0B0923672CE7A3B9AFB7B932936415B067BDE1EAACA618CD76F850A6CB813F8AE0BDC07E34363C22E942A61D15C56359C46C5338B1787E21F83C7D3FF75A4871B23A7F9C17AE9CC747E265612318C3EC56C40C8CDE1F551E3056E7C953587EEBB1DC178130F5F3CA11B857D9BDCD786F05
	BADC178FA435651581F8817CG49G69G4B13042D6E2D2C9E4FD6D52240EDD54D5B1A78A2D558E2E1C93CADB213C60E5D5E06770F22F347BD13EBC75231F1BED1CF4789EDE5GDEGBFC0B2C0BA4072EB45B8332A7EBCEE1CE08AEAD5EBG57G75154D90392EC5E55F4CCF4A4725DBFB2D9D5D6FC651FDB51B663C97CFF7A86EB9673A11F4EC2FDFC96B300350D68240F02F96A02E951E156B441E7027BBB08ED360B99DE0A940AA0075E67BCA5A1EBF9DE29340BB1D2D694AF4515E31C36C77D2B8DDA23BAC0D
	EBCB37BC2FCAC047E4C4F6D87714F9E358BEFEBFEB5DDBA65A07B25F5AD21DF8CFD9D0ED3F275CDDED3F27FC257AE506879E5693A7517EFA7558FB4E235509771CFACDE2BD87EA6C751CD0E32F67541A31753C21A6111E2F523978A5BD4B50E09F98884A14B54B09F2944F6DA19ABF0D04D828A2F7BDBEEF93917F2FB5E20D13BCDD44791B35474968B8777882062A89A62D3224FD5253E3A63316305CAD74BE68C9EC23439EFB9B9D75585B2858B3360D5E770CED23E70C3D4466BC600C941409287176146472BE
	4D065ADCCA445A526BC19EB6EBCBCB3786BBDA5CF59171DEE07004D8BF52270BFD572D6F607C2C06678DG4EE4913357950D15438A13C747A2BF5B28CBEC57ABF576F61DD807FBBADD0DE85DF23C6D126A8E18ED31CC5DE848C1B48F6BB303529B1256AB0F32FDAA25E7ED09686757E13E688A78829ABF81E07372503E1DF4DEF5DD566922D1AB99921F0F274C797098EACD29A96F597A4463DFD7EFBF7E5F55A79E7FCB75630FFFCBC3627197B40CBF7EFD8D097C130D353B4C6AFB47CD7C31772CB608614F6661
	3914A2752B9183FCAD997DAC14A76A1B8AF8EE859884C8C9E6F58E73657B7AF09EFAB4A98862E930CE3E9EE94B8A846DBA681F8338A3G35G3E6B455E66DC9B6A02C7C8DE290B6E9D79FE126E57F1EB227386062F29A9607DDBE1C26A823262B3E9DEBA09B1E10920D2067F873ABE60548999ECCFDB30533A27DDFEBD9B53BA3832769F046C5AF3EE906DBF2937369F380135D71A6D0F9F3323EF3DC16442430D85567963F90345714493E345317F06C47B2B02A27ACE6886360F4AB6717BA9BE5902E77B2B5B5E
	917B563950C6EE9475447CA2D1CF78G477A5E1CA75EF9A681EE690DE27ECBBB8FDAF319DB6AB34A4B6B35002FDA6A75D2EFE2F96CF1DA37A07D069BC59E4B4FC71EA5A9EDF98DG1DA962FCA37A16B85FD8781D85E445537667C72BDB079C5194E60B39A9163303373B32194CF385C266CAE8F74EC0DCD201F5DEF06C1B32D8BD4B5A6F9F510EB156C82C6DEBC734233E4FBA2C6D07F6D85B51C61FF0FAB636075F94E35B7870A2227C5D64046F1F5BE730B18D722B6B11DB331C294DDB7819DDCF02B3BB6A66EC
	7C35BB45195D6299E2BF72FCE89B57E56D8CB107EF969E3467E50699237DFD3D60F7E4266037573D0D7BED47CC61BF29A7047F4C1AA956265DC5D88BE41A4F2F52E7BC3BE44FF73E13689D4DB78114B33A5E7C51DFD5C67CBBA11677B20AC45EA3B735656D81A883E885B0EE92E31DBB6A7CE4BCE0F4F7FFC33A273EB69D4B9B0F53781CF5934B9BD9A67E990ACF3360D90E5815AFF244C2E8F34E927B5D15CDF4BD03679400F40025G2B81B64E9279701E4692B24E9E30D58DFBE111D6AC659A9B5F7902EF76ED
	400F370AD8DCFC7F5DA4E3EAED42F3D467EC66CFA57C1A317C56AC3C72F3A0AF0E2F935A868152GB2G1683E44EB65FDFB472F38FACF7213A153A7D74045AAC1E83F2DF8C0D8BA99BDB63CE3B31ED60FA645FBCFAADE0F4AF046C68DC3CFF993FB64035035FB33A7F293752056378CFCEA86F8FEEABDDAA6F3710DF175A509D3C85FD66FBA7EA797323B6FD44FA0500F9F28D7459883017631C3744FC102D777FF58C69E2A73E74380657520F97097C653BC52C877B0E452F87570ED88F9A0E452F87A8AB56BEF0
	ACFEBD58B902FE61317875C050B33BE5E248707B67AC7787727F1A394AFE4ECA7FDFE5B03FFF32A9F1ED3D26493E36FE3E49FE4F363EE96C7D754626447B6B6C0F2D6FF4D95C5EF71A1E5F02AFE4G2C82D8871013AAE2F64647789E081D3245472B47570B35BDFED9E2187BA86F987BA86F785F23CC76E25D5B259A069AC47E82FF1D97674BD04394494F233DE8A3AD506BB7D8D70E1F6DC59FD7649E0AE66BF1A72D83DCB02E12D431C6DFF10AB53A4244EDD7C53FE493F7D1957DD2CD5CD6DA872C01677CDB85
	2E0362DC70DCE96276B6D0DAF8D6CCDCEF0360B7414415529AE285BC2FB6F1459E442D07671CB982B74BA3E81DF3C47D317A040869CE403DC6EDC08862584F3AD3F63854B978BD62D32D0408356049FC31969C3C67F6D27BD82D63B031DF8BD64DE1F92A68DB311AF3F2C7D9AEDFEBC76C41FD50A77A2D314F61593C7D7D69443E337474783E7350E97A5E93663C35BF4C77080F1C46BD1D5A53234B260F7E6BE92709EB541BD5BD00FBCB16BB7BF652F7D2A0085967F49BDA06656919CE112713ED46139D87E950
	E7B100306132EF4572B4639DBE9B63BDC9B557G9B5E1B09B8B7E130867AA06C657CB6427DD1E2DD838E55233D7C2A9698D0C30624E099ABC68AB54CDC1479C13D589BEC1BAB7623FA7D6E91F4D5CA20B704D14FE92EB7E986CD1A22860CD9C974EC977CEB291FAF047B25GEC1CD79B44B8574D353721F51CB97358B88FF37C01F971EBDD83FDAF5C833AB219FF21F1F4847A154C937A55F9C64EE95F97B13B5F63917AF44E33DFFB453705484B00BEC973E2EB6FDC0BBE4C771F9E31EFCD0776A5B6FC2D40E26F
	E7DFD85FBBB967A88E41B73E7F8A4EEF553C7877CEF7F760D81C5FEE4BAB82E88670838C824C82C887D8823082E08DG398DEAF300C2008AGAFC0A700F11B487D051F21CF35C997C61FD28212EFF0698220C981DC8278797C0B3446C882BED887B05CA60ACB839C5696ACEF7D23D3642DA5375965ADC26CF0EC8E271C47FB46677B2A6033BE819FC6135EC9CC3FE92E47F3DC6BCB5F967CAB16B35DDD63B9536A79541C1F34BE6B7C1C5CBE49769BA31A9353583CD472EB7226DB33184CBB3E32CC3BB1B09EDFB4
	889EF84C60061DC2D88D5137551E3A5673D55E467AB327010BE91B6FB5885BC45346324D2E2E583D5E9B13F7580372C2F21F4B14D98741D678C695A4DF650DA2D7A97C9EBFEA4ADB65917296A51017F8AD3A59CFCC9F5AF8C278D016094BFADF78906278F7348124E17E7A43AF5EA4897C952A15A6507F132E8BF23751AA751AB4BF6AC83C66BC58B17E1A73C807FD3D72E84768FA65D79D76754A939D56FA0561B2BB50063D90E36DE63FE7CD5C67A66EF94A93F1E783BE43EF6257D33D905FA023C001FF116A55
	C7F16D9C3749027B1C63F23E44F593068CCB01204D7D12665A002270B3DA5AA79002E55434472EAFD147C0D832683D5F44FDCEC4FE175E4A273430A0C98AD51D618FC68BE2F821BA4F2D4B1C565C3A867CEBE35A58391551E528D6BA677C363CA3734726E33EF56C824E4BC9685BBADF644467BA5FC964E300EC5654302C997D8CCB3F0C93FB43A62B8E8670CB9AC787C6772447CAB797E8960DC347E2658967687849F4DA630761FE260E6E7728EA6D4746FD7C8272B6779DECE83C6D038BFBE2F39698201F2241
	6E93D29B2FED1BC64BB851B4DA178D1E517DDE72D87B317F3B795570C760AC8B1FA31BF50D6CB473D2A3F54455175FAD4FFE6A76EC8749E9A4DBD8E0B6129C0C2EC6321986DFA34906400C62476AB9D6B008535CB43B70085DD5EE10AC1711B52D42EF84150FB059422E0A6E01DC01104B704B2E3600E6C4A445D5A3AB0ACB82BABDACF787FA82320F761035206ED2FBDC1A2C00D9AE4B2E20AC69401E71A88C38E5F7118B90BDD237FC5ED04FDFD615C8D0F651EF6EDDD24867E208BACD226FBF0E3ADDAD55273D
	406ED2A4GBBFB175425C3878344023A3A2BC755DC5D96AD5D2E4FF418862A2929826B8ACAA1565A1B8AC4A476408E3032D8BB9439A498248541D975C1C6F0F549AEA9823686995D502D1F76834DFCEED7C5177691C233820397966AE7D2D0E394435569D195C545C5110B21FA153BAA0A6984681748D5CC77A3276DEBE1B5D2B0E2A1FBF07EFB7F317C71B6BD1BECE9A42FE09D59A2EB540DE59F3EB6637F1FD0C7F243AA54B874DBDFE0C3AF1BF523DF113DE4CF4F2778BD2C8E03B55F596A8659CB3FA9AB0E98
	812538C1527D86AE9C9FFAC9CE887F13026CE24D0A946AADE65172A1F549AC7A55A0215FD2E7013E3B293E4845EDF2597D431DEF3E5E141911CD9CF5E43B22CA3EBAF090D5EBA439069F7C422FAA3EE87894E3342D8FC3A80F8F7F0F6670F130EE5C9718CADF0C4ECBAED8D3D06700EAC1A34534A7164DEE33072F46AF21AF4ADA23017F9922EAC74C3609070C377E39FEFF0703E471C16294EC0F068DE6B0B3D8F1486EBA505A7875F38FAD78370AF5EB01240EEC26DF1601E0E72C5719C830CB5626AE1C7DEA5E
	2C5553D12FB01A07CE6E8E03EC69110281259FEEEAE14E54DE934F96B37A388B2DA09AF2E1AD8F6960F4C1504695E78A51E40E48FD53C3A5333F7B62B9BA229829C1DEE3F38724453D329AAC2E6A698904E41381CCA430FF55E40FFD5CF1FDF6C5E76CED7A4526AEF03CDA129DACA921FFCB695F63783714E2CAA92674B84C66BAB266CFFCFBB40AE35E9F358F8473FB37E1EBDD3BE1CAF7423AF649DDEC5DD9F1D77C3EC34AC6874BCF073DC43AD8A76FF3607F81A62A45DA0210A2E0F7F931AA64433A2C3A5F10
	E3E7D8BD5948DF97FDF8A3EB8BE6235E97ACB47F87D0CB878885832E7A4B95GG68B9GGD0CB818294G94G88G88G6C0171B485832E7A4B95GG68B9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8596GGGG
**end of data**/
}
}