package cbit.vcell.math.gui;
import org.vcell.util.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (1/9/01 8:56:10 AM)
 * @author: Jim Schaff
 */
public class MeshSpecificationPanel extends javax.swing.JPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextField ivjGeometrySizeTextField = null;
	private javax.swing.JLabel ivjXLabel = null;
	private javax.swing.JLabel ivjYLabel = null;
	private javax.swing.JLabel ivjZLabel = null;
	private javax.swing.JTextField ivjXTextField = null;
	private javax.swing.JTextField ivjYTextField = null;
	private javax.swing.JTextField ivjZTextField = null;
	private javax.swing.JLabel ivjGeometrySizeLabel = null;
	private javax.swing.JLabel ivjMeshSizeLabel = null;
	private cbit.vcell.simulation.MeshSpecification fieldMeshSpecification = null;
	private javax.swing.JLabel ivjJLabelTitle = null;

class IvjEventHandler implements java.awt.event.FocusListener, java.beans.PropertyChangeListener {
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == MeshSpecificationPanel.this.getXTextField()) 
				connEtoC2(e);
			if (e.getSource() == MeshSpecificationPanel.this.getYTextField()) 
				connEtoC3(e);
			if (e.getSource() == MeshSpecificationPanel.this.getZTextField()) 
				connEtoC4(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MeshSpecificationPanel.this && (evt.getPropertyName().equals("meshSpecification"))) 
				connEtoC1(evt);
		};
	};

/**
 * MeshSpecificationPanel constructor comment.
 */
public MeshSpecificationPanel() {
	super();
	initialize();
}

/**
 * MeshSpecificationPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public MeshSpecificationPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * MeshSpecificationPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public MeshSpecificationPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * MeshSpecificationPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public MeshSpecificationPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoC1:  (MeshSpecificationPanel.meshSpecification --> MeshSpecificationPanel.updateDisplay()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateDisplay();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (XTextField.focus.focusLost(java.awt.event.FocusEvent) --> MeshSpecificationPanel.updateSize()V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateSize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (YTextField.focus.focusLost(java.awt.event.FocusEvent) --> MeshSpecificationPanel.updateSize()V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateSize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (ZTextField.focus.focusLost(java.awt.event.FocusEvent) --> MeshSpecificationPanel.updateSize()V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateSize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (MeshSpecificationPanel.initialize() --> MeshSpecificationPanel.makeBoldTitle()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5() {
	try {
		// user code begin {1}
		// user code end
		this.makeBoldTitle();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (MeshSpecificationPanel.initialize() --> MeshSpecificationPanel.updateDisplay()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6() {
	try {
		// user code begin {1}
		// user code end
		this.updateDisplay();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Return the GeometrySizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGeometrySizeLabel() {
	if (ivjGeometrySizeLabel == null) {
		try {
			ivjGeometrySizeLabel = new javax.swing.JLabel();
			ivjGeometrySizeLabel.setName("GeometrySizeLabel");
			ivjGeometrySizeLabel.setText("Geometry Size (µm)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometrySizeLabel;
}

/**
 * Return the GeometrySizeLabel property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getGeometrySizeTextField() {
	if (ivjGeometrySizeTextField == null) {
		try {
			ivjGeometrySizeTextField = new javax.swing.JTextField();
			ivjGeometrySizeTextField.setName("GeometrySizeTextField");
			ivjGeometrySizeTextField.setText(" ");
			ivjGeometrySizeTextField.setForeground(java.awt.Color.black);
			ivjGeometrySizeTextField.setFont(new java.awt.Font("dialog", 0, 12));
			ivjGeometrySizeTextField.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometrySizeTextField;
}

/**
 * Return the JLabelTitle property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTitle() {
	if (ivjJLabelTitle == null) {
		try {
			org.vcell.util.gui.EmptyBorderBean ivjLocalBorder;
			ivjLocalBorder = new org.vcell.util.gui.EmptyBorderBean();
			ivjLocalBorder.setInsets(new java.awt.Insets(10, 0, 10, 0));
			ivjJLabelTitle = new javax.swing.JLabel();
			ivjJLabelTitle.setName("JLabelTitle");
			ivjJLabelTitle.setBorder(ivjLocalBorder);
			ivjJLabelTitle.setText("Specify geometry size and mesh resolution to use:");
			ivjJLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTitle;
}

/**
 * Return the MeshSizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMeshSizeLabel() {
	if (ivjMeshSizeLabel == null) {
		try {
			ivjMeshSizeLabel = new javax.swing.JLabel();
			ivjMeshSizeLabel.setName("MeshSizeLabel");
			ivjMeshSizeLabel.setText("Mesh Size (elements)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMeshSizeLabel;
}

/**
 * Gets the meshSpecification property (cbit.vcell.mesh.MeshSpecification) value.
 * @return The meshSpecification property value.
 * @see #setMeshSpecification
 */
public cbit.vcell.simulation.MeshSpecification getMeshSpecification() {
	return fieldMeshSpecification;
}


/**
 * Return the XLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getXLabel() {
	if (ivjXLabel == null) {
		try {
			ivjXLabel = new javax.swing.JLabel();
			ivjXLabel.setName("XLabel");
			ivjXLabel.setText("X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXLabel;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getXTextField() {
	if (ivjXTextField == null) {
		try {
			ivjXTextField = new javax.swing.JTextField();
			ivjXTextField.setName("XTextField");
			ivjXTextField.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXTextField;
}

/**
 * Return the YLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getYLabel() {
	if (ivjYLabel == null) {
		try {
			ivjYLabel = new javax.swing.JLabel();
			ivjYLabel.setName("YLabel");
			ivjYLabel.setText("Y");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYLabel;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getYTextField() {
	if (ivjYTextField == null) {
		try {
			ivjYTextField = new javax.swing.JTextField();
			ivjYTextField.setName("YTextField");
			ivjYTextField.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYTextField;
}

/**
 * Return the ZLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getZLabel() {
	if (ivjZLabel == null) {
		try {
			ivjZLabel = new javax.swing.JLabel();
			ivjZLabel.setName("ZLabel");
			ivjZLabel.setText("Z");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZLabel;
}

/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getZTextField() {
	if (ivjZTextField == null) {
		try {
			ivjZTextField = new javax.swing.JTextField();
			ivjZTextField.setName("ZTextField");
			ivjZTextField.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZTextField;
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getXTextField().addFocusListener(ivjEventHandler);
	getYTextField().addFocusListener(ivjEventHandler);
	getZTextField().addFocusListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MeshSpecificationPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(324, 173);
		setEnabled(false);

		java.awt.GridBagConstraints constraintsGeometrySizeLabel = new java.awt.GridBagConstraints();
		constraintsGeometrySizeLabel.gridx = 0; constraintsGeometrySizeLabel.gridy = 1;
		constraintsGeometrySizeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsGeometrySizeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getGeometrySizeLabel(), constraintsGeometrySizeLabel);

		java.awt.GridBagConstraints constraintsMeshSizeLabel = new java.awt.GridBagConstraints();
		constraintsMeshSizeLabel.gridx = 0; constraintsMeshSizeLabel.gridy = 2;
		constraintsMeshSizeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsMeshSizeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMeshSizeLabel(), constraintsMeshSizeLabel);

		java.awt.GridBagConstraints constraintsXLabel = new java.awt.GridBagConstraints();
		constraintsXLabel.gridx = 1; constraintsXLabel.gridy = 2;
		constraintsXLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getXLabel(), constraintsXLabel);

		java.awt.GridBagConstraints constraintsYLabel = new java.awt.GridBagConstraints();
		constraintsYLabel.gridx = 1; constraintsYLabel.gridy = 3;
		constraintsYLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getYLabel(), constraintsYLabel);

		java.awt.GridBagConstraints constraintsZLabel = new java.awt.GridBagConstraints();
		constraintsZLabel.gridx = 1; constraintsZLabel.gridy = 4;
		constraintsZLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getZLabel(), constraintsZLabel);

		java.awt.GridBagConstraints constraintsGeometrySizeTextField = new java.awt.GridBagConstraints();
		constraintsGeometrySizeTextField.gridx = 2; constraintsGeometrySizeTextField.gridy = 1;
		constraintsGeometrySizeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsGeometrySizeTextField.weightx = 1.0;
		constraintsGeometrySizeTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getGeometrySizeTextField(), constraintsGeometrySizeTextField);

		java.awt.GridBagConstraints constraintsXTextField = new java.awt.GridBagConstraints();
		constraintsXTextField.gridx = 2; constraintsXTextField.gridy = 2;
		constraintsXTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsXTextField.weightx = 1.0;
		constraintsXTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getXTextField(), constraintsXTextField);

		java.awt.GridBagConstraints constraintsYTextField = new java.awt.GridBagConstraints();
		constraintsYTextField.gridx = 2; constraintsYTextField.gridy = 3;
		constraintsYTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsYTextField.weightx = 1.0;
		constraintsYTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getYTextField(), constraintsYTextField);

		java.awt.GridBagConstraints constraintsZTextField = new java.awt.GridBagConstraints();
		constraintsZTextField.gridx = 2; constraintsZTextField.gridy = 4;
		constraintsZTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsZTextField.weightx = 1.0;
		constraintsZTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getZTextField(), constraintsZTextField);

		java.awt.GridBagConstraints constraintsJLabelTitle = new java.awt.GridBagConstraints();
		constraintsJLabelTitle.gridx = 0; constraintsJLabelTitle.gridy = 0;
		constraintsJLabelTitle.gridwidth = 3;
		constraintsJLabelTitle.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelTitle.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelTitle(), constraintsJLabelTitle);
		initConnections();
		connEtoC6();
		connEtoC5();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		MeshSpecificationPanel aMeshSpecificationPanel = new MeshSpecificationPanel();
		frame.setContentPane(aMeshSpecificationPanel);
		frame.setSize(aMeshSpecificationPanel.getSize());
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
 * Comment
 */
private void makeBoldTitle() {
	getJLabelTitle().setFont(getJLabelTitle().getFont().deriveFont(java.awt.Font.BOLD));
}


/**
 * Sets the meshSpecification property (cbit.vcell.mesh.MeshSpecification) value.
 * @param meshSpecification The new value for the property.
 * @see #getMeshSpecification
 */
public void setMeshSpecification(cbit.vcell.simulation.MeshSpecification meshSpecification) {
	cbit.vcell.simulation.MeshSpecification oldValue = fieldMeshSpecification;
	fieldMeshSpecification = meshSpecification;
	firePropertyChange("meshSpecification", oldValue, meshSpecification);
}


/**
 * Comment
 */
private void updateDisplay() {
	int dimension = 0;
	if (getMeshSpecification() != null) {
		// cbit.util.Assertion.assertNotNull(getMeshSpecification().getGeometry());
		dimension = getMeshSpecification().getGeometry().getDimension();
	}
	if (getMeshSpecification() != null && getMeshSpecification().getGeometry() != null && getMeshSpecification().getGeometry().getExtent() != null) {
		Extent extent = getMeshSpecification().getGeometry().getExtent();
		int dim = getMeshSpecification().getGeometry().getDimension();
		if (dim==0){
			getGeometrySizeTextField().setText("");
		}else if (dim==1){
			getGeometrySizeTextField().setText(""+extent.getX());
		}else if (dim==2){
			getGeometrySizeTextField().setText("("+extent.getX()+","+extent.getY()+")");
		}else if (dim==3){
			getGeometrySizeTextField().setText("("+extent.getX()+","+extent.getY()+","+extent.getZ()+")");
		}
	} else {
		getGeometrySizeTextField().setText("");
	}
	if (dimension >= 1 && getMeshSpecification().getSamplingSize() != null) {
		getXTextField().setText(String.valueOf(getMeshSpecification().getSamplingSize().getX()));
	} else {
		getXTextField().setText("");
	}
	if (dimension >= 2 && getMeshSpecification().getSamplingSize() != null) {
		getYTextField().setText(String.valueOf(getMeshSpecification().getSamplingSize().getY()));
	} else {
		getYTextField().setText("");
	}
	if (dimension >= 3 && getMeshSpecification().getSamplingSize() != null) {
		getZTextField().setText(String.valueOf(getMeshSpecification().getSamplingSize().getZ()));
	} else {
		getZTextField().setText("");
	}
	//
	setEnabled(dimension > 0);
	getXLabel().setEnabled(dimension >= 1);
	getXTextField().setEnabled(dimension >= 1);
	getYLabel().setEnabled(dimension >= 2);
	getYTextField().setEnabled(dimension >= 2);
	getZLabel().setEnabled(dimension >= 3);
	getZTextField().setEnabled(dimension >= 3);
}


/**
 * Comment
 */
public void updateSize() {
	String error = null;
	String sx = getXTextField().getText();
	String sy = getYTextField().getText();
	String sz = getZTextField().getText();
	sx = (sx == null || sx.equals("")) ? "1" : sx;
	sy = (sy == null || sy.equals("")) ? "1" : sy;
	sz = (sz == null || sz.equals("")) ? "1" : sz;
	try {
		ISize iSize = new ISize(sx, sy, sz);
		getMeshSpecification().setSamplingSize(iSize);
		updateDisplay();
		return;
	} catch (NumberFormatException nexc) {
		error = "NumberFormatException " + nexc.getMessage();
	} catch (java.beans.PropertyVetoException pexc) {
		error = "Problem with sampling size " +pexc.getMessage();
	}
	org.vcell.util.gui.DialogUtils.showErrorDialog("Error in mesh value - " + error);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G440171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BF494D51608917949B7A8A80C616760CC746008BB199D4EAE4E608EAA4EB03B68B24A51B8E6C75C03BB1CD956C1648CAF9FC8C2878990840E09C68589AE062FBBCC0CC2BE04845B10115893C882296E2EF41AD4F7B555152443AFFB6FFB75DED7F7D7A748E1F24ECD553B7FF75FFB376EFBD5CD8A5ECFDA171ED1C0C87ABAE27C7DB91D102CE3046C9C51748B0E9B63F75808696F83004DA433AD8B
	2EC9C01B1B6C3065133B02B9501E836D166FBBEC5B61FEAB99971CFC94EE62585101F64FFDEF3E563F1D15995CCEB6ED7FC949036B5BGA90043EB160A765F716DB070CB8DBC07748CC2D2A94FC1DF21012B05F69B40E40059AA63DFG57DCEA790E322386DF4B58D9E465D3DA46C654E3524921A440E46D4635182CCEDF99C0DB67FCF906CC82346782E067B3C9E276813816B7F4FA3EF4B93DFAE937CBD614D21F24F714FA3A3CE5E5DF4B410EB3815965ED773AA45D2B7A9BA43F2C84AE214EE93E9C2A031023
	50DEC3F1BB1DE8E78E5CAF84C89378D40ABF2EB9ECCDG810D776D0BBD65066FCDD7FECC52BCCF64EE0A5337219A6B53ECAD5C3714CF1C19BB69B89C4942F8AF87DAFE50E15B85D08B5081B09460DDBA7E1FE7FE82578A27DE257AFC2A3FA29028D45C5A572ADBD6603E2C8CA8866E9B5925EAEEC25898377976D978539FB0B97FF716E3B119F5180FF772B78F13250F5E1D11ED911325332EE4A485D9ACD2026118B05B5F0FEBFBC9B0F2BEC65A7E9BB15BA62F9D4ADEE7E13B7039C3594B8DBDC48F5BBE205335
	C7473A986E530D79417077D03CEE42333979BE45E3BB81E84BF4BEB71A5E663E583326117C0377D846E1624FC6E6583B188F6E2EE8DF1ADC28FBCC973339D178622178B9A6BC7365F75497ECAF81DAF537434666571FABF02D4E0676C200B400B5G05BD8E5BD1GFB8F1FE389197BEFF00EB5C81ADE5965D55C32C6186C97D96F40D58E691AD4678F6A125FA5BBA44DABB995B9C88C1E1AAC0C01B3C26EABBA77DDE063246C17B5E319EA12CF56E5AD48F89A33B6A30FA6D3EA2357A70381DF00F03DADD9DB612A
	C8C17D5B00DB5265B087233B33F0CC3AA587E88485F04F62622E44F8A9507F8400C491075F9CC3FDDF499AE40B32B23B2AF6F6859A31A7A42D87637CCFB4F74C027BF9BDBC46EDD508AB89B9EC47C3DCCF7E71ADA6BD55DE973AAFE93DFC0D0DF7E2BEEE8C71B973DFCDFC4ED4EFFC086CFBF602651C998373B794E2F3E5CCA8FA6DCC21BA51AF565E6DEC7A0E6BB89EB406E2579E337165AD3311E901E3717DE5B50ED58A783098E0250871D7470B591C2D1402B21F2527B518E032BB9CF16667F9578E4346BA43
	F7DCAB98FB1B680B0F726475BAECC7GDAG92G128196777218AEF736197C653A381E0D37A8964CD6364B4A963713FB0BEC31D8D6D46073CB6E167DFA359C107DEE38899A345A8A34E52C6FF6D54854D3FCG72C195AC402040B7D6601A15F4DD73BA3BF4790C2C6BDE3FA7CCEF213602DD4EC0B815F0DB2E8A6C275A8379C070F7D460DAF229FE3FCC87D50898740BF4AD4B0A6C83272B54AE3F4E7001CADC8F1DF22F11CB744A7584F3015205F909559133A50CE7C0737AE02AE0EAA3764B384E1F95F590429A
	98FF2C070A01F664B2DF33BD9F71B53B6B7203E45E05C00655B3E7624C51990917597A19F5B93CE619CFF215356F1DB4279878A07375677BB14FDAD7B6FC2DE457203E85E0E7A9404A4BFC2D3CDA05F41E0F0CC5D225AA8A8B290875626AFC9C9B79D2178CE99C935FD7A2FEAFD4EF0922B90CD8B27AAB5598CB4514B3A7DE41D8CEF771FAB18F5A3BG2A2F30F8DE3D42624178E4B767CB03761A38FCBBE54ED7F8954663EABC3EE784DF63D5C6D70CEB42D5B35F1D6D1CAF492027D872D5F9B85F92033E423866
	DCE3FCEC1C7EFBAF4E9DDA1BDE143A25D0E9308766FFD959A9D38DFA4C1FE32C67DD57F82D7958A59C3B96E86BGA32F71F9766B0D3C56CC6C1CC5163EF6B0BBDE9DBD4370E3413570BCDB3822E2BD1B9BFFBD09B6F32EBBECBB2E73F9F17E0479F9D7918C2AAEAF1DD54618EE2CB14FC3065BD233812E6DDEAD2857C1228871715FD1037E623358C09B3549C2DA33E0EC7601DDB740126BB486FD85A65FA7DFE74F5479F04D247A8E3B316EEDD7DDDD41D27A5F2E8675928CE92954231752E4D5DA030493F83B17
	477F8769981FAEDA8F1C71DE16CB76BA882F9327EDBDEE58DB7870B4D25EF7BE432AEE9F3A29A323380F453036AF9C4B54F14E4C158834DF235C3CDF094033300F776B738BD67D4A9A846B114AD54ADCEE87609A86F1399F36D9498D95F2EF88394900DBBC084729359D71DE3FD777CA8A6DAF0B43588379A28E555EE0C011FAA194CC576CF62EAB6FB6D8BF37F1DD5BBD183BFD51B9C60C256261F2D7C1E60E107BB70F358FBFE9C09FFCD227DC29AA6EC62F2E60F03067DB35BF3A56DFFEDB384EA0039D360241
	11F5872B01DBB534D5B21855405B84BEC071ADA6BC737927F4DDE1FB8C504AEF67F5DBEAAD2E53E5504ECA001A986088C0A3001E4057C7D24DE1B2C0DDDC6707F255AFA9E7A29EE8461AF01C447840B348E81FBB09F1214FA0421F737F6A0A0E43180448FEA7C73519DCEA0CDCE9D4FBD1149C9B135DA51847AC72D3A3BC63EB3C3262E6B92A3BAF4710D91640F351359AF3AE1A3B69DA064DE27D5075F28733FD7C8E533ED7ED4CE6319FFE8AFD98843449GF3G96812C84489F8263B00447BF6D649783451FAE
	7EC6358AF035706C290A2E93C43C925DB79767E9B1F251F1EE9C92A9476227DD6071D306707A31D63EB99F9CB1F251BE4C30746137ED5C07394207B5B76943067601FCD8B944CA6E9FE264EE4C5E7C816D655DE9A527F9EE4E5E209839E8FB3B226C31BDE1B3FD1617832D8100610E869027C3FB24401D3D043859501EFFA79F8B17D68257A550DE85D0B054E1AB81A887E8G5081C6824CG18BB144BF4D3194550DE89D0B08C568BC0A300BE0C55D7D8BBE1DD14B80C4BBCFEA9094F132131FB398643AE77A11A
	EE85F38D3EF28BFE3D9F9D585711F168469ECB783A5502076FFD78FE68C6FC5DE6DCED9634CD7D48E5D2FF2CE9ACBF3DD69BFF5F342256FA5F74C62D753EE9CD6DE606879D66D396CE4F2A6BFFDF15D7E7352FAA227DF8721DCDD4F7B0A833EA29DA52A503762A0E710959DF55CBBE590841D6126B4EB5F8CE68EA55A1B1D6D77A7ABBAB1C8373EF41304831E679FF339DEB1D25C0DB8510BF9C669FC06DF01E7727558D785CBD916A67391BF8AA7E385CF74AFADC261E423995D43BB4179C9D7BD927F688DA7889
	D3274BBE8C9363F9044A6B126611E346AE054AB3DABC7947CF459BDFB686CFC70C41E1B186BD977A9B038E0869556111E340747549E6FDC7043E07E26A0A08DC8A3AE6D86ABB5CEE5677FF827FFA847E2040BF68B163BF93F84D0F7BF428D655AE9DF26E884C1D437CFCBF1E823697812C82489B41EAE4D657BC52036B3BDD13FC90B6ACDF899909322783DCD6817EC1GC9A88710BA02572F01CB58F7590F5BD6B7E921F2F752FC0D35CD6DC84E77DE9E0E51C9D975493A560B3B8B3B64446D37617B08G4E813A
	C70F21BDFC9C7B55E4A0996E776508BB9B01FB71A0624EC560DE7DE0DB14DDD12C897D5D6F633E2C49C4E078DF74A07EEC8CFEF58B624F45609B76E09CCFD17B34E2A7ECFFB2DBB36FCF12C69A7B3511FC7FB25FE7C5DFA268B63F95FD3920AF7F560ACE6E62745905D67482C19F5BED35FFAA9174DC49CA3E7CAE7EFC9E57471F598D827752F944A5C1FB4EDDBCB76D34474FAD45F66B5C72315DBA676F33771F730F58636584665F288B5C6785426746B66E730ED19CB75F457976895C0F845F55D1FC0F79081F
	3FEF9B83388523785ABA589631E7408DF8D959C94D6B2E14BCF63AD2D93D79261F2F99B21A1FEB6CD1D167D1E8B7GF40C66FB095047FCAF117C40B43268BF2C4FB5D29B2FE58C9A4D724D0C5126F30DCF1C194CE6D7005BCC857A0EB1744CA9E09EEB1C574D692C1EE374E7A36898633D44CC4F3EE426233F43B24C743FC450B1C6E38DF9563727F7733E2DF9FAB6D19ECB087B6E33ED8C6BD302F1AD7E65247412C98D6B59DA7C5EA5545DAEA9C1D968CE6AB13FD7BD3AE1306539A32D518C1D0B0C6B48351352
	0DB32A76B8E7D4F4553338CE89723DC34ED8070D615E72F15CF1012B74F3DCC860BEF2F05CEC01FB35D06CA785EEC1B762B05F940F633863D20E51578440E50DC75C03FD553CCF63E2EB47F5046FED99AC821EB400ED86FE55B8F6A6405F5FC18DF6BEB65FEF3C406711FDBC37BB7382375BB65E5AEE36492EBEBE526E5071E13B4C4635B6EEA3D158F84145ED4C0AE3A357E4E3EE140DC5B1B6DE90B6160E67757F0FE34EDA8682A67744CD4A7D69BB4B31B31CB7E92ED8E16A9343E784903F4604E7796AD31567
	2B1CC407CDC964E7BBDF56637CDE846D65GD9939C36C200F2002689BC0FBFE547EF887A2DB19B55009D923DE2CA4D2CFF1783B7770E7C71EC1E97667C638FC84A106A3867A74A84E34D9A5714051352714A7A776B53583F39C0DB8230862060EEF856819C3F1B77EF4B577BD87FCE383DFA052ECB2E8EFA0A4BBB67704ABDE1B496CCCC774A861CBF0D20A7A0F41D7D7AFF616A10AE9AD1C2152AC6B58132C1D383322677B2ACFDAB404FCE02D6F19AF337114F6F0E77DE11D84E1F9403FFE1DC391316F229867F
	4A38F2A58EAB391CFB98FF61BD71641697DA499DB0782B634ABD53ED755E3449442FD848A28C0A42A7C2FBD6940E59B8A119FD1BEB70ACB22EE946F515C916655F674539DF5EC4076DFD427661B9047F2DB8960B033ECE446F0E3230DB46DCFFB90F4F75429FCEA7557FDE1D719109730D4CC4664312096167FB0977C8AE4B553BAE181F014C47674459DA7EA4EB9F19BF7E3358E2FA925BE2756439391E5AFE750CBD9756730788BDAC56EB3154B36EF382617B436A2DFABF9EBF575A7F2EB6587ABFD99F3F662D
	2F372EF99B6B2DEB5EF3757D1FE3B86B633F9F76561B5F8F33FC25B4E3ADB68846EFB240BC0045G4BA6715C75477A8F894D0396B970045B037BF97CEAC567761FBF9D3F2FAF1F9E78DB177FBC0D7BAA272A6B2A8F75F37C0A53B8DE3A9A20C8067B1F5398A34D6B6950992B01FF6BB44EC1C5EE27E806FB3361EDF35CD81AB57849DFB186627D8BFDE9F386987D9ED66B7F0456F2D97742F362DEDE7F3F2773BDC13140ED77F33E0040057C1C6F2A401D27380550DEAAF0BF97FC4B846E1803739D3D0F6356B7F1
	3EEA014BAC62FC098267AE62FCC98277FBDAAF2E00B619AC76BBBD084B0376AE017BD6C15CD1E8578A5CBF6F617ADA846E156B08CB0676BC01EB217567E2E8AF933875F47F14B7856A08A99C57A976D4A58277A8158DC1FB6294DEFB3D94F1C6168C78F9D37ABFE3E57973D74D66BABF7FDEA50357BE517C0B8DFD29D34279132D0DCD2DD8DB96FC4FE1BBG508820G8C821888108CB08FE0B140F2002C7BE17F84308F60B8C083009BA0F4BFDFCBF5CD38CF0AD787989BC23A20B4490BCB4A48D1AFC41CD10D84
	FDC977779F83A637B9625D4B9C10D9B0001C712DC7F37CB5FC25F960B51C50CC5FFB435AEB6C8D986BEFD8B31E4B286D6DC1D9640AD14D7982D797ECD003DEDCF96CB931A8085F1F68E008BDC33E3800234AEA7E4FF4DE73AF35688F5F27F0C8B376B37C7939627EF09D4CF4373505F50F76F35DEB6268DE570F6E7C24683DD217BF329F37CBE1DB4F8BDB05C93136B263585AE668BF10945D0F871CE15DC5CDDCF7F99C5D597D68EE08517D22AB2C7BCA91575D92C7F7EEBF3A83B1B1DA5F939923F4CC65862D13
	1E3F352B1A6C5154AE3F1B6F53EE27F96BAA689A1A444F9795A50B727AF546B3186631D9C01F1344770B4F6D613E27D978EE0EB7422A24487DE25E54E85FD7DFE7F35D2B6392E0F6B77771BD640E295C372004735F29EA74000A1D4B8E1778596ABE602B8688GA4CCE5FEB139A2D7E4ECC6350747E130587BCE1EEABDD753CD63BC9BF890F8DF66477465A9896B378ED573DE0698CACA056275787109EF44BCCD627DDAB415676CB7E8DF49B4260B615E22389D860E65524FBD48E61C73356CA6F172A0D4952724
	EE693473226C529BA50F107928B5FECE7A24F5601CF438553A2E78CBEBECDD71E52BF5DDD15D9ADBD75435E2ACBD103B1B84DF3D401D9538B3D4A7627E66F56B9D82FF167A0578DA998DF27C856AD78F45B5716FF1CD38333C26EB45F985DD06E163327ED63A8E3C0A425A9A65717AA14D0AF874D09F3D81496477D501BBAB46728F97E247770F974C634BF0A56ED83ECF5DE6BE36E7E857791E618860591CAE7692C11FA74E4394C12F3A98265BD4CE1FA1686D1DE1FAFD9327AF9574E1CA181EDC44698553B9BD
	5907F4F65FCC779FECBDFC2DE0DDA83E192AD295D5ABF5AA122B53D80BDDE15EC777E01D6C17FB0A85FF8DAC22128F5DD0D32A1E8F77959F58D7FC7050DE6EC7F6897743BE3D3F3D53A35DE19BA5FD663D93BB437911A43E2387BDF32773B3E75B5B6F9173EFBC688FBBCE7882FAEFA54DC1153264673E2C1678EFBAD6CE67674EB27DA6FE5F8C0891C04B8CFE3E78280B7B2D4F30721B47F1F1CF586F37248F0CB89AC72525E770BF5FBA84CB2BCE54B71E7806707CABFDEC7A9E0AAE9706AF8CED8C6379F2B1EA
	1DCEBD22CE845F664D38119AA9720CEEB148AC3BA139FC55AC3786E472E75E085C19A6335CAE10B9FAC3F20F9419656CA053F6C3F2F9A5E6B99DE4065E10DC0AB450995D6419D667A67FB2205CFB167656D2391481FCB383BB5B7C8D1D278BE7C61FF97E1662174C0CBE736CF472B34FB42055BE407799A9916FED1381BF6F017EFDE1B9EE309E1B776E54E37363D2FFAC5F4B7ED83EBDEAAC5FBEB5166FE2D3AC1F5A944B3720A8166F1922D83E0FCAE279CACA4CFC6CFB07F9DB599AD43CFE390A7D7AC4703F63
	24FBBF085BB2915717E8CD4A6E17196EAF5D325FBE441F43437F2EB6D81FFF650A0D5BE217B9EEAC87FE6AB4BF03D90E7CA3FE66CB586F94FD2D5FC012CBFE1D2C8BEAE413F80654D101546E43E5192FCC4F4CA0D9F5E4BDABE26AC896D6A2F5E49DADD46AC8A694B1A1FCFAE519A1F809DCC7ABB7025549D67E123C985F1297177C6CB15FDE1D9460ABF983433FE686F4FA9340D900F3G45BAB9483EFA6DAD76F0A5C194117C6EE27CA636D813032A5205DB3CE2DDAD6E8A4AE55F4539DB0055485B118F737419
	5BEF4F9FFD38AB036454107570E08DF4C890D07A05291D581CB43232863CBA4937236CFE105DCFE5BF37138CD7D0A76F0671AE44B36A1900641201A557ADE91DD4833A3439196436EB9EA7B520130C661AE6B0CBDBC85EEAA79B748EBCD243FADFA75B432A9B70C040A0FCCEC7192C858850BF685846082A6309291F7DEE610B0EE0A6D9DFC7F66206680C2C31EF0B5D786D1271032E9A129BD0C1AF0EACCC27A03D2C8B6A3D8A743E207D9B7C31C2D0175D957C548C3A3B0D798443286C2F15029DF45B839D4D72
	63CF4F4896C6D6A43FE7BFBBC2E35E26D79C2783024917003FDB293F2825D4E8597A53CD3BBFB8151612C9B2EA4886C5155CB5128BB686F5A4D76F00495321AA6ED06098D31453435FFDE6023AF7C57731332578D506F03AB3B4E56446E59BEBB28889E9E4BF6544B0168A8EF7D5072401A9D92B537127F42A36CB50EEFBCF7F7819135BDBB2C83A51C99C299D21004E82A6D6ABF63934863C561FFF7D49E9AF142FD88EA2B546E4824379E12E7AAE1FD35606A4CFA832A5260EC03F82989EBAEB8BF51C08BE2F52
	8BB75534F66478DC585F7803309576015162289EF312F03CF47C1387A6BC387A9CF5BCAC2A137743338A976A7E0A76F64865C2810C9728AF926A1127B40AE7CBE86C36D34F2EF5427C2AA6193E8387687F03747FA17CFF10E28ED24C41C3B0E8ABC83FFF5174D0887B3CBBF4D30BFE03C95186FE9E38374FFC9E386A8ED75C777E64A1765CAFF8A87ABCF0B5FDB75CGF865A1FE26F72A6DD392FFAFFB46A7A98A6C24BBAB7CEE5C57D6766AF2782CFEED9B6A8FF29E0348E86BA5747B22C9667F81D0CB878836EB
	72F3F896GGACBFGGD0CB818294G94G88G88G440171B436EB72F3F896GGACBFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3296GGGG
**end of data**/
}
}