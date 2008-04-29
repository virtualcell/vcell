package org.vcell.sbml.gui;
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
		cbit.vcell.client.PopupGenerator.showErrorDialog("No structures in selected BioModel");
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
	D0CB838494G88G88GEFFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BF494D516BA0BAEF34C59C197F69987D7C49C6398DD1DE50FF156594551B903AB6EE00CA3BB0B9EDC599D5623ABBA2C4BF1F0FD694ED7BE89BF4D2CC1DBC58990BA890900A1FCC2BA1F0604B469909AE4A555696A24C375A7D5955289A0FB6F7BD4D7BA5509FAE6F2F2D3D5773DF76FFD775DFB5F3D2FAAE473873373F2EC1B894949A37CE7DF8EA173BFA7A4786A3DEF885C7E6DD9C5447473A140
	862266654235955A9CBDD9C5A524D4AF0467F3701CFDB62B68DD385FC4366ACA8D5C24606302363467DE39FFE2BE732E8ABE857439B23E962E6B81FAG07570C6D483FA5DE4A71D3B8DEC00E0D10296558479FAF633895502790F0AEGDFB96B9F03AB93603A3A7D3C5F399B53494AFFD4ED6B100E092600C5FC6CCAFEF510B7F2E66DC0DEB7D0B9F1CC85BC87GF072F6B237F5B7DC6BBD977A8E757AC33A73E22F2CA870B72C5455F9F5F528D79FD2E5EFE8D4F6C7E2C33AC70A488A74AF5D09B41F0A97D29A04
	AC85FAF3A96E879548679C5C6BGE9BB85FE1F93714B611E6C4AAAAA5DA566F6F0FBBD175DF76981123DA71CDB1CE2EED53B581CFC3B92F3DBB8702C6D77F49DD6925477F4E84B84D88830822024A22B288A60BD3A7E3F3B3A932E8DFE3DB19A8EC7A38D3158D1A52036C783328277F5F55042F11DF2EFD48D9042567846F80505BC93819BF76BB79E47F412DEA356BB2373DE3264EF7E42D6E02113A56917EC2D95CC97BD95891DB05E0BD3723E26E22CBD0E65FD1A18F9AF98FC5A1EE741FB5917CF5B27F3BA69
	A65ECBABF02D0BF763DAAF007B9C2E8B06FF0F62376DCE6019ED5EC6ED861F6B21ED76EEE19B3E75C216365CB924247AAFAD75B073E7E936DCA713214C19AC4B5D546EDA1D0CE711A1CBA64587CCF8A64BC10A4767EB20ED99G332FB7ED682B2D1560BBG178126834C83D884302CD258180BD4FECD9B73C82AFEF4A82484E4153031EF12F760AA47F5D5F2C7B4DD0A744A5D129A127C0A2C915EA71F2E2FFF4C38B5B9E87B3D4023C50E482A2407229120AE05E5DDD6B556E7F34E866C234A3435AB941601C1B8
	C6845D321CCDF0D5A4CDBF958BC83A1C68415A376520CEAECA5DC0918840BD534B4EDC54D7EED5D6D199C0C51550432D145EF1D90500D1D757960DDE980AF561CC4865AA5473ABB4F69C01FBDF15507127F9081B8F4F0B8DBA3F4A59E822638A75227812BAA2FCEC8B35291710E6B578FA35301D7F74895BF12D3B07D4BCBD4352F6FC671F3637D5B31B89D4A77B500714B64A1786ED9955A2665FD961CB61DBD555895FAA2936763375DC5E09E3477C14BC8ACC77F9A97AA058A7E8E37A1E1E0F73DB88F2AE87A8
	5CA3F43E4166E0B6FCD452E4E1359DAA981C9CC82CG1373FEE7896775AE17274018435F53B68750ED8508814C5CA3F458687419649463A60A0D6FCE52BEDE07674670C0DC51A463A652E7B2B0DD9558F13F12AF4A915DA54764C8GEEB45E365E0E7C38BF872346184D949F837FEF8407538C7C7F5A51C7A5DDD7C37EA1DD764A3A9E0A74A55ABF223C34A1FFAC91BA842F9DB4CEC707417F0D7ECEBB7ACEEFB49211692299C3F8FB9575DDD91143A0F4E3F4A822B37C5EDC4CF1AE48A3BCF65465669374FDE588
	63904B9B6E1356609C54D098CC81C399A929C13F4E9FA9B569E8BE2CB766BF4B603AEA0F704D610F05EFEE9B3D0B4CBF9F33D96DB1B36F781E4DD7437CC429C978A613E9D71E356C55B406F03C46E44D9BC05BC9154D58693857B4543DCB8ECAC30A5E9652F49A322B396DECA287F6D25D83E12ACA5E4A7C68098214A58DE41C8D10D9A37C681EBCEC97310BBBD0E3D461B4D095352833606E4D26F9AF2AE1BA437B98ED3F49E8EF05671E5AC4BB63F3F3FE8915EDF0C806AD83036CF1E36DEE4B5F1854564D5711
	355F150F6B28186273664F1067EBCE111BE656327837882ECB8188343F8F78F9345F431522DF4FE7CC2EB83FCE7B0C494868DD2A927D48DEC627F4AF0B2935FB4D7456540AFEAE5EFEEEAF23173E1751E3EB7A5B9DE8E7B4EF9D10AECAF127B68C3ED2D7F70CAEAA4BCFE3A3053C7F0A3DA28F1DF599572AFCDFD65191G5FBEE113CF2E93F968748B69E42E1AEDCF15E34776B1B9266FCB58E4C54F301D5942C093724C0636E57B049DECABB06F058D1A966D8D51ED182F41D385E61BE5384585EF43B598D2B55D
	8DC1A5AE566B3982149777E90E667A38C17E48D29F4996AD723CAB4D6679BED4FC4536A645FCD771F9166ECF4C3764446BF616AFBDC877C874919F34CF83D8309F712F6F469802EB4C78755688FE53C66FA0BDB3EECE49EF6D670CCF4567A6FE4F55F2FE6FD58AFE81E81F85407C73BB1548CF6F8FE94C27DABA4DBE158D7DD6811455097EBF2AC43D0519CA38CEDEEBB8427BAF3CF78E49ACC833D955A27DF74E34D55571DA24AEA1E34652E7ED7381181CAFD2B911C7BA74D9EA70BDD9056B2A09D8E444536D3B
	5E256B2DA838D49DC1A3DAE965BB3A39FCF3B619652B3F7A2525FCD30B7BED258718DCC78EA4641B7F6433366CA705FCF32B049E53204F238704FCEB1D6BC9F21C93B1703FEA040E990D5ADAB14755B03E36DE5078C1AD4A2940BC3C020E136E9E0D7DD224CF8E141BAC54C937CEE71B39F39322F6B03DEC7A38AE195629C8C8AFEF7BBAB48E006AF8AEB0B412DC8FA975099C6C1A7A7179982B93CA2E609C275733BAE10D01FF1F62671970CCA7F3E8FC4167ECE85BFFD064364F37E03C1A854F19G0BG168394
	9E4AAAAABFA462C430900FA7A62C9D5C91C869A312629D1384705890EDC27D433E4D1F35A654A155BC91B9DF079B7D4285BCFBG62G53G6EB3E4F823E9D2991A6216B2B0BD7F43EE547322C32973DB6BDC6D89BAEE79B79847FCE247D97359C86E06E3B6FB0A38D2FA1851AEBF1C70892613D547D0A77D001F82B08BE09EC076E12326EB2E18CCA722CE634A6075C365E4757F4A43E3674B4F14A69DD7FAE46CB866AB0F6F4098E231A722E7707CACB2EC4E4F2A0E307A6B9CDCE3GEC5F7C2AD31C6BCCBDA26C
	77C49B6E4B7361F98940CA00128670F9G8F005EA0F4F5D3735149F455A0925C2E28F15B181C248ADD2C98270B340631F33756617F0C9B67CCFACECF22436679C8BB1DA734AD81D889D0F29466896081500F0AF97EDB7341C97D84C3D0D73491F02D10DFD9CC114B9A2B79F6F672552463269F359AD7D77B6DF45ABAEE5C7BC94F99C97CD82D1FC67330C721EDB1G4305AEA1EEB5BCEFEE94B561BBD7508F77433387209FE08A40AC23FD9BED1F874F59GAB9B591E3E59A55A53AF0BFAEFBF9F17CAAF7DC9ED9E
	5373149463980F07AF1BEB4AB59368BE391EB55716296AD0E6DBAF36242EA9DFED312EA97F3B453A26FC33E58343838D730913E84FEB1D3866DC571A3A66BC6DCEAD6717EEEBB983EEEBB9875C934B293AD34939152E452FEC386F2AD0870642B2A54DE2229C47F3FB70464E2188F3D5242EA547EDA27C5F5E01B9CE2DCB78F95131BDE43C1FB785C2FAD495C43AE4C96D88860DE0660D493DC2EEF2BC350E8A0EDB6BE85DF1EB9DEDBABE310E5EBBBE310EFE23EFA1966781A72430CCC40E672BAC62FDBCFA54D3
	CD445E525704B4AC7696EF2F8A95ADD6DDC43C9778613058BFBCAED1F75DF49257E7AABC4F8648B4FC76CFBAA70AE10BDC63FD114D555716DA2F675B2C75AA37E1CD27C50754DEB9D9F7CADB2951160854EEDD8E23FAD81FA1BADE1754BEF91C6EAF53712CAD55786B5AB0DE78C301104ACFG583ABC76CEB1DDD7CD13B53AE938A4DD626B313DCA2CC7139EED2CB678BD511EFA7E596D56737FF5FB6A79BF5FBE797CDFECCFBD7FD75BA71F7F9B6D296C134D754E9A336D6DB1700335E69DA470370F603914A20D
	C407F43035A634331F9867CF4B40F6D683EC837C7EA61667B0DB3EFF9857A128CAE1E0CF1D35229947DEF3C90C1D897DB3G96823C843036D954B6E73BD196BCC2EA137C34F6E478E0AE56F5D81AE83C01619B9B57717EDD30A04DA1D98998E35E5C0FBEE17220EA067FA95D9FF069848FD6531E39E22EE975E6B62719F0E56D33E2D66D99C67B46C1F37BFC5E3E5CE8FF46EBB5FEF5330805859D6B4C6B47638673633B07A772631216D475D5FEA7FDA754426AA83B01AF2178DA931E55D7B3CF0A3A5585ED69AD
	A21F3839D36493EBDBE16F27FD7FF5C43C732C82DCFF2BD87F6A1EB2F3ACF3CA43FADDDD0B9A8A9C157A5A28B531B836F334100F1F5DAA6218EF94E9AE066795G05EEF13E917FD41CEFE47CF08E199931BEBFDA5AF54566F0B3DD385C26F7D5834F5AF86C3CA4F8DE836D19EE44E5DFB22F8B4E7D2D9C164F32767C4B66F674B1B7B1376F9F530E727E074D5CBEB0269DF55441473339BD7E09185B6A476FA34A5FCEC9797E199CE3F35A462F5316FDBFE779DF0F5A7919DDB045199DB5F3B67FE9D744195D39E3
	229EF9AAD648E519FECC2C61A765E556348CB7E3E35D03025EC2035E7A2EC2EE373947057D7CECD858CF7DF131B72D6D44DCA04DF8FE07BE6359A5FB7E2B13295E51FC9BC0BE63734D877E20BC125F893138F7FF2708FB69A043C3GCBGD68314350939AE9FF7FEB29930F12FFF4BF115235FF49C0B9BBBAF21FF5637313811E360EBA83E5504E7B1624BD191A33A20AD33CD543B8FF9D0A7E56D30F7GF8G7A8126824CED977170FB9DD5E4129A30AB9AEB03CDDAB125EBBCDEDE7AF65F86BCDCA0FCF15ECFFE
	C4B22FF725BCC74DECE7763418DF4BDF78FE8EDE79B990DD73429330BF82348274G68GE9A7447C1E6960679E186EC2F6AB75765393EAA3F98E494389B4EE24ECEEB32ED84DED7689A6473C936377827E4DC24CEA5C825EFF893F2E00EBEE873BE763CE8C1A4715F60C25DF51110A5F26AE7338A33CDF973F76DB0CFB3483ED661F07DD7C7932C59F31DFA1E01C1C86FD6682EC61384C0E048D72F3555DB8AEF162CB0FEBF8AE7DCC2708DFEBBDE2BFA876A66F875F9D339FB4F81377836415E8573C497BC17118
	71F7F8137783B11E692D6A92B16EBFB75D87C67F10314AFA4D2E7CD1F9B03B7F39A7F5EE3D50E31DDBE7F92CEB36451E096B6B251E547575EFBC66F73A4CEFE75035F101AD74GCC811889106191BEBBF4925F8331D336E4FFED8A74E1EE0FDF1668C69D55B9C19D55B97977A87D1D18777A233A9E8DA3FD010FF462FA695198C5727368CE54119A6A6B57D9D70E9F21B62E48C10AE67BF1864D8396403C96FB449E7D0B2F449E3D4C403D92937DEACF8A1CE7D074BBE260CE53FE5360F91E014BE0E361F9390113
	59D8F85E5CA9F08F98744A8D5CCE1AC34461F92A014B2CC75CACF84EB0F0DB8F0A31191DA27FF8BBAEFC3A501BD5748155815C9371336230956E08973FC77C45D9C244DE707CDA3197145DFDBBF1BD6332FDCC2C7702A9DE9627323D26EF67428EBB7329A5E3EA7035DDD9C50E2E094F61593A5D62CDEDBBF33D135B4EBDDE7A5E93563CEBA446EB449FFB31260B86031AEC5868835E92836756BCD1AD0435A50B1D0FDC216F24009153CF75B9548C0B534FFDA562F42D45FC6CC9508AFD4E81889D463A92F11A51
	6E7A3F846D3EC1E38F30201DC744398903E95087E18B27B7976EF790739E9009213E7A23EAE8B49A51A58553D8B1D32C9B1067F7E322B6382FCB54230F44B60D995720047AA26875FC4C334618ED2744184B036833126869937427D3BB6F02FE7DGEC1E7FE44CF366A9EB9D1A6719F10A4D73E30E1FFFAAF92FEB6842758A022C0C67E96AC78B215FE2C33EDF560FDD53AF24045EEB8F8AF98A3B2D77DE71EDA152AA03BED55D093D57551D100759FE71186FA4BD509E3020EB8666FB3F7C427C5E2964E645A668
	A6770FF3FAD33A135FBB7D199F67128978EC00D5GA527A19F83288770G84G62G53G52819E82D884308CE0A5C0218FF2F41F087DEB3FC01B6A1686461FD282129738F483D02590EE817CFC7E37B47ED6819D4C8398EE954535828EF38B96370A2F0A389570D945ADC22CF0EC8DC3986EB91D19C6CDB6AB859DB6A6ADC88C3B199D43F3DC73CBDFAF7E955B1966ECECBA515554E92C4F6CE1737A6CFF7BBB165F98511868E36B321CDFD7EF39B90771DC7935F9DA4D015138FBD05040E386A7D48A31E8043E2D
	EEEA6EBA5F5066EEB9518ED48C5D3CE56846F1E6A25D3C5E1738BF9DCE704B28C7FE91F958E170EC86E7AB7F2881022F72D105239AFE776CB078498785BFDF8AFE2977225188B1EC68533830219C8337ABAEEC88F14C6637E34A44637A2D52A7A4053DC236F28C646F708F483DFA1754E70C1963CF3D675C651FFC4F79315FBADF49740F4FD7FE6A374ED79E711B739506DB60C79D76010F790CFECF9838B386AE0B52C45C69D0C06FB7700B29DC08EF151121713D8F15EB1862FC9C770289F70663167BF15F04A9
	43D6A0463E6C2731B624A87C0C167689C5E09BB57471BA15B19413CCF27F4E401DA1A23ECB1F2E25E3E1C31294AABA43174557A570C2F49EDBB7DFB54756E9E0DFF34FCC9CDB5938DB4654F319B0E661A46318EDED20FB5DD2683B7A0C0809CF86F72532B1C0FA54E8CCD675910625DF46F19986B4338CE5E7E19FB97BF5E49FB8E89E6702B13EC946F1DF39BA5EFF2A2E1A7D0761668D0E6F776020399F1B77BBB124ED549DECEA3CEDABBDA766EB969A251F22C17589210D513BD69B4F63FAED3CAC579E3468F7
	505C0F7D5F4D7336C3E0AC99AF13BCCDA545C6DCF2D3C3DCFA31264E7E426DF69B49F513FC6618EE120B5E65A6F954795C448E0E99470F55F34D20139223E8F660913B23CEA7B98EBD67CF6B7B7530F28803ADD4D53486F204A28E3DDFF6F407D4FDC8D29C0D3222B8A4982745645ED0B0A487E88FD98DEB0EE850214A8A2865226C884B128664990D7210D3F6D6B8G9114FA6573FA747C4528B294169D741BFB079489B89822D915687B0F9DCE0777E8FB9B109B9C8AC1E56F107C9AF45001AD082BB902D15551
	EB125269B82541B2D0C98D91D8D7908A31665E14A1A23187F6001D455C2142A141A4CD88CEEA98A20243AFBB24A150B5706805EEA3349FC896F0BA6A7D6AA1840F8213979A9AE1DCD0E2E4434589C695A50A1BA2E7C3652AF354D75285508649D7986E47AE5BB7422AE45D180D6C6737FD766F8B1F6B566CA45FCDFE0FF924D7D629994B81FCED467FBF2119944522106350EFFD018C3D64E97A08A237114D41CE7C9ED603499A6FECB51DEC215F14D58E69A12532D5527AF55CB88E34115C887EA78559481A95A9
	52D7493C65GB5499C7A55A054CDF88F72EE2272A29527C1E5535F95FF726131E519F6E2EBA6EFABD1A9508C8692D55D24C86F873B680FAA01F86CB0A3D4C80F225770693FE7CC9FA76B443A40907AC27C1634F54B57B55BA04BD4C9A56D09E933536891E86C175096E5552D63FF06C455EDC65B356F6B1FBE51726EB99B49611344A5F8BB9E53C9FED08A07149138F101EE23FDF4F15643A35BF48CB7C6E5B81BE1BE7A2FDFFEEC4E3F542FF88908B513BC7A4D9908D41268F5E2A86C17556BB3E6ECDDB3FD698D
	A8F18C27ABA89741269D51403A42C05C11B4E751E4086E7C73472AE65C7546D9AAFAE228CEBEE00B84C3AB7B64E83832A1988CC5E403G2C98105FEA1047BE4E24BE9B63B7EEB97674DBFE30B09731072B2A685FEA7AF78F7E2D2618EA0A295E834B3602CC78135C9E97F326E6C99F36208D4571EEE35C5A910C6F5D2E1A735AD55775264CEB83BDEC5F0977A4579D21FCB430F900DF54A376498773707FGD365E25EB0048828AEAFB4C48218179D9D51654499D6A49F69EB228FEF6467D179A87700E94C7F83D0
	CB8788247B86A33595GG68B9GGD0CB818294G94G88G88GEFFBB0B6247B86A33595GG68B9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6F95GGGG
**end of data**/
}
}