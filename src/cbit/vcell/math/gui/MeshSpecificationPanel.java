package cbit.vcell.math.gui;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.gui.DialogUtils;

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
	private cbit.vcell.solver.MeshSpecification fieldMeshSpecification = null;
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
public cbit.vcell.solver.MeshSpecification getMeshSpecification() {
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
public void setMeshSpecification(cbit.vcell.solver.MeshSpecification meshSpecification) {
	cbit.vcell.solver.MeshSpecification oldValue = fieldMeshSpecification;
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
	DialogUtils.showErrorDialog(this, "Error in mesh value - " + error);
}

}