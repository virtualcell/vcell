package cbit.vcell.math.gui;
import cbit.util.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.Extent;
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
			cbit.gui.EmptyBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.EmptyBorderBean();
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
	cbit.gui.DialogUtils.showErrorDialog("Error in mesh value - " + error);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB6FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DF4D45519F10BAEFB0C27F8C40BAE6ED2243B512516BD46EDEAB9DDD46AE21B1E32D9349E0BB6DD5162A955EC175DC617561BC900CC78C9A42120C6090A9AA406A48408A1C8B279E192828492C28281721277B2B3491B9F5E3CA4930DF53F6F5EF76F3C19F913A0C7F34E17775E77FF3FFB6FF73FFB5F9BD234EB5E7AACDB91A1D96B0971F7A80B101C2304BC306D42DE0E2B79A04DCECCFFEF833C
	CA0ADCB9F0F582CDD6526C8564D0A8971E7B60B927BF4D3E836E8BC9DDE8EE8D5C443123832D6D9F5502296D74EC64F6B65267FC7FE638EE8568879CDE97FE0076BF706FB470B38CBC07AC9BA1B3CB114761AFB6F09940138B38A6001ED2469F00ABF3607A5A9A032F6F66A43276A71AED8B6AB16964305C10DDEBDCCB484BD9F377202D916AA74A1441338C000D4FA633B7D640352EE34475496000C7AF9F9BD4D4355CAB696EF257282736760492F4F78614C14F10E7D052BDFEDF0764D3541D9F224E7B7C39D4
	87A16920EF8145BD62C6BBFDF02F83A4FE4871B7F9903F9A6E495EB47B4E3D3CEDC7BE28B3FC6F796C7BE4C515B7A7BFCE5BAA7632B6756C8D37ED1177A95BEB349F6664E23CE783AD85A095A0832020AC4DDE8170866D7F3F5F78A1DC9B8674C63F576B77B584828ED556CE78E5C5057B5ADA209838B34A20DF1389E1FDFC45DFE6614FD440643E7852F2ACA6C97BF9FFAFBCF3AFD9F95FED360D96B1D91974194DD946E251DF960E89331D9257F6C2D964F80C34FD0E18ED979FFBBCFB3D056D1A6A4733E79BFA
	12CC361FA8433E2E25FD3D8C6E330C714170CD94DF7CD1984F46E6AA9DB378DC8634D99F71315133157B521EB31F94D4FE4BB28EF39ECA342D7D08793039BC5A177543283B261C595CA2FCA92278BE931E79F28B75911F93003686000D2F0F1DB8D7FB76413886C884D8G3094A09DE06DBEBE46D6EF5CF715E32CC352F44728C715950DB059EF677E992ECAC85724D6DFD017FC03CA3724F92481D5899203671EDC0C41C0045C7768589F849B47941F229953D4133C0A2EE8C14613127BAA72E88A25F6F93C8A98
	708688573BA437902E2A94544F86E4C9D7429C0C3EAC977BE4CC6A860D20G6ED9DC06DAB1DEC595E976B2003A8A9E07DF36203E630A86592236365D6F9F998DF4E1CBC8E285467967B4F7F4403D3B0247F8208DF14B61F93550336D58B6131EA64FA03AAFE993FC0E55FBB09F2F2B60E366059EBEE61A36FC1714BDF62B6518D988633724120D151A4A683953C6F522DF6CB94D5D73A567F1BCC0BF2DF345EF3FB29B5996B8965F6547312F7A213D13GF3ABF90CCF9CABE1E356A18595BECACFE9B04094B99CF1
	666726611D060D75066FB8D7B0761BC4DBDE27BC4920FFA5C0CE950C8D00AE0049AA9E535BBD7DA6FF39AE2EE74BD7948BE6AB59529637E37F0AEC31D8B6BAF17DD246941F5E2484941F8CB7C1035666C4DB467C9E728B198E0A8FC0BEE80489989478EEA74ED9C957B54F40282EF4AA3A6E71394274CB54D6F0F4A090CEA55C166244F67A47A19F887EE1A74E25C13F4F2750CE95A286DD25F3D9D195AFB85D689F75698C2F3562FC98D1A60CDCB2563A01E0AED0C7B1AF31BAE21104718CE89EAF8C95CCEDE4ED
	954E7367C49D0430GF0D88FAD036B4AAABEE7475F65F3F67727F713A51783B62BB5E74EF73EE92B5B4F66CF477E701CE5BE395A2CFD77521CE260034C570777E11E352EEC78DC311FC0FD8130B383E06EFEBED71EEFC3BA4FC746A4E9742BAA8B290875634731108CAAD7C695C86318780E0B78BDF5FCDB942D5B08A523BFFB9CE3291AF2E6FDB54632E318570B4960CFAA401A7DAC1E3B2BD9BC985F36914E17884F8B2A6371BDA2781642734A38FC1F2A1CEFDDB523979DE0573283E63EEAAF67F39A74FECB3E
	8C4117E050679857C5869F6B277F5E03E307562643521894AA8F0E43782F2DEDB355209D01DCE3BE279E90B5E8907BAE67E01A3D98207AA09FE77F3E05571A33C712C8663147326355516D870D9A7CE0F81CCD7E6BF8B69B9B1DA75166A220ADB94847C5FF3BF93DEB88867D039EBA2A0CBE4DBFE19E078CD7F4A28F2EC39EAD2837C22288717EFF7D847A0BEB3101B6EA1389DA33E0ECD6005D4D07526C8907907F5D4AA2136FCD07581A2A42B51B6ACB9F463AF748BFB89AAC277F5B7DC13D94C3DAAE0D6B65B4
	D91537A02199EF7770785F130571992575C02767D325F4CFB761F5627C42FA43DE6A3D73C95D9717ECD6F57B4CFC37ED59A196430CC361D8AED87114AD8500356B7088EFD7DD8D54D7B53CDDFFEB37EA57C220B33997055C9240E58A39E7E4AB390A0F395CDDAA17EB825C6447BCCEF9DE44FBFC9E5DA329343DAC8EB7D79408B8B4F90281D51A00D0B0DD973DDCD7B268D9A3F4AD75E26E76C6679851172F79385C6E5AB4FBDFAD17CB70D97B70038E7441AB0DA88E3FAAF7F9F4953B032DEFE78351357E6C5AF0
	1D1184770BEBA36B8ED6837BF434353C1655405B857EBA0A4FB99C469BF5BA1DD778DC8334D507F95D761F27F01E4E2A033E81D88A3092E09DC07191BEBF661F58CF26290BDBFDD02E7AA435B3E2C1B366C44FC90C8F2CC146F35FC90C8BDD030858EF8DC7472166C8E43B3B221E195CCDB1F265D14F21A8B956A76F15E29E3348CFDD304637F894D5E6B9EAD5E52EA1B36B884FC51F3718F351627C4FED1B626422E5066D55C74273E7653BFFB0F2D162E974217413B4FB93C09FC088E0AEC08A404ACFF87C1FBB
	F9E43A7853495F65EF841C93561E4668BAC1442BE1645A62FCAAC6AEBA4E6BBE0914E371CB337378959D6575637FDC238F596AF4BE349F357261F5057BA08B9F6E0E51F5F5BEFCFFDA9F669E35129B74DE1B3DABB1F2517612AD6D6D72DD1B3D0FE264226D25C659E3FBC2152E652B00168940F02D9A620A6BE19D2F673861A062FA60D92D67FD216B25F0CD0067F9G0B8116832C824884A8EEGF900F6G39014B7CB51519046739G0B81D6822C83A8F6307AEA01D19755B944DEB0B80F0F1306583DDC26E117
	7B90CD37825950334A023F58428687ECEBF59CBA73750F4257C28B9E3E77617B212B71F53BF15DE4C14B1FC2AE1B7AE3CDE379693727626F1BFEFF4AFA5F14F94AFA5F7427D32FB2BC68B01F32F07A060E2977D5DBBB2C76D5EF52F6BC7A67FC2ABB98D4D83554A46912C17B0D0E710959DF1D143C0A9103C272AF43F60327D977B7D6093EFA3EF22A3342BE98FF81C7E4DF337CDF5809354E8C204D83C881D88E106160F9FFC147346BEEF3E80AF5773653717B654E53567DF257E99CDBC17F28B628C4477E1E53
	BB85AD3C42346A0A974344F86E23723A243914183EFB004AB3DABC7907CF476BDF56872191F39F6C97FD708B7BD4FDD050887B224648BEE07A4AD4333EEA216FD957D47AEAC0D73B253E7488FD87857E8E2F99DFA9705DBEB37E0040FFA3007BF428D67D23BA645CC64C1DF78478FE3C9F1EC3G73G129BD90D4C6A1AFF9E4779BD24C9DE889B162FA42989E57BB5AEDB844F25GCEGB7404CA6DE3FDE974436ABBE5C364AA42799656650FC0D35CD0660FBEBB37651B1456FD5F4ED82F7976D52GEE3F8D5FEF
	8C609820FB74985A3D75582E9E8349F0AF56A12EB78277F4A5624EC760FE737676A83B22D8937A47F6613E2C47C4E078FF9BC7FCEF8C7E65BE441F0F41F7FC00F1EC2376E945CE587E64C15D3CBFF1B69B7B35E63EFFF948121EA0684BAC6933857D690BD6742430FC0395FD3120CFBA2C76CF4B857DC517157C2AE63EBE13AA3EE6E78A5CD1DA43B9DB20C6ED6139690D4E7839651DCE6B5CD25AE91D734BBB274E7987BB636584665F8BF66EF320057B1CA8F31F17885CBA9767DBA1F0F3DC1CEF77B13E47FCA8
	405F3755GAEF00C4F2D4A7E08BD83EE40EBEB0FE99E59A1395A69CCE575E65E95BEE7120E71F30D1AAB28B39D1EB3818A1CFCAF91FA1F6FA5967E43FC3226517ADCA33D6BF3DB2913651BF6E7F8AF31547B140D593CC163B6E782FD0993F1491A392FF1DCDF48E2759C23BF97C147986FA1E67A4E20190E7E7E0D4DCCEF0B20E30CEEB664D95BFE7A9EEF5B3A1FAEA26A7DB3623E7B4CEDE5EDAAB32E33D65D1ED5137FD9B61B0B5F23E7FFC3129AD4046EF96366772AB5F9FFE5F96EC8EBB4C3E748382E5BFEC7
	16F1C6B594670C0A4EFA9657FBC7795EE19148B35CCBBA472D9638D7842E240D6336DF62389E01FB25C16CA7856EBE8762B05FAC9338775D39C6DB4B0E435E7CB862F6D4B571B69D0F2D9D57933E37E590821EC4006D86FE5EF1F6A6405F5F415AE30F4D77DEBB9FC7EB055D75F95CEEEE3B355D0DA63B456D11F6AB5A43F6190D7BE5EE232E1D5BA8F4F39B9DF1EC58CDB664A89B21989B8F3A380D996D3C7EDF96F356B29DB039472FD1AE7F4A1715E3E7B8F99A660AB926B6B1FCA145AFB061D93E7A648A4FD7
	0B00D6F4021F6D541F45719D026759G8B8116822C82A0A7F99ECF6D44EF88262CB13B7C01F6C8742AA9B533765D28DD5BBB7287B7723C107C40BDA46506263867A7C5A759D828B62E254F5C1E05D7563E7F68467649C0CBGD8G3098E0B940EA513E222EB2563EE65923B7683AB4682627383CF15D9EE5BC0C460209697E5FF3B8FE56011E1D27382E8BDD78A1CB37B4ECC489D57AB52A89109D1ABF20E87A844352378246B838EF54AACEB5270CFCFEAA5EFB45BF69D6F27D863F9ED7AE47525ECC03FFEEDC39
	57AFD949ADB278174637673012FB4260DF93D7AE45E1755E14F404790BBA2C774825D178BAF86E0842B19BFB5DE65FE403A7E4DC930D6BBC13AC4B3F4EAA1E271301360B30FDF8AE61FF99C7E3F150568E7C6EE843E53CE7E37D571B79D8AF7E273BC853AF1AEC6F12B85F481CE6BEA41C8E2F6F29DA0931864E301B57C0666341BC6EE34AE9EB9F19BF01FEFCE2FAD265D8BDCDEE2EA7E3CABD37C8E1BDCF384CFAD82C8B30E4B36EDFB35DB7DD792A5E0F474F35973FD69BEC7E371E0DDF73F61C352EF93B4FDA
	573CFDE727BE47104F46FFBFBCF2567CFE18652B41812C45CA4F24591BG5CG13G334E705C755259F7884D0396B930D9F661FE9E3FDA51397D155D715B7ACC77745F3A2C6946FD5500DF577DDE544F713F276FBDF4FF00A2996E8F5D98A34D63F26B0C55407F319B47202A8CD1B4437D5F392D663831B4EB705BFAB086627D8BFDE953890CBE972B75FFC2EB3905901345E7F87D3FEF0C6F8916895CF6413733136346C6B95FEE01FB0362F4F81EA1F08902EF1640D5DE62FC698217F919732D896BF3F03E3AAE
	0E1BF4F0BE2740BDC9F1F360B9C9600EB6A2AE991ED3856E614308CB07678C01CBF3F2FDB9E7B96EB4FD4751854FEE01F3501CB0894F3384EEB32D291261F92940DD91FB2A658257CFE5CB3A536C755D3C76BAD1E54E43DD00F7F7CFFD464A72674FAF186B7C02BBD49B2FFD2279A78DFDB33B4379134D0D8DB256160B817F8440BA0022F3E976D200FA00AEGB740A440EC0005GCBGD6G2C864884585C13E6AF6961F36978E55CA7452B830C8DA11DD01A6441A9E5642843F96698D403BEE74F54B1E0F22D91
	EBD89F48842611B37244C07CB92C8DCCBF07BF9B206F3DE16EF5CD840C7917B00067B27E212120A2F24575838582579A6C7087BDB87358BA716BD17C7EC487C3EC8D299E42DEE5B5FF4B982F79E7D834076FD3B8A49A7B993EFE4E69895741CC77B9D7D877CD23DC7702B83A57CF21BB25A7FA2FD495550ECF5DE1DB5F923616DA584A0EE3EB3B217F0918F65C69896B4E6F673AD745513DF18A5D19B13A9F9B8E6B3E4541F56774DA6B36CF21FBE7EFF40C7E4B9199234C91A2E85D0D74FB893F2638B47F28CF66
	7BB49F4DDB3BC1D7C5AFBFDFFC72F08E6575690CA70366318E207775727D6273CE6EFB22056F66F8A34C6B0D5CAFA647786E2EE4E35D236394E0F63F2864FB48A54237DEB70E7F813FC68F285839EC404D4FD6D7G5F9A001D67536CE56719DFCC6EDDD7E4EC2E77067BE1260B3725693C75D84DB275F38F70A070362867235BB26E467A4D6D57BC1FC28CA535C17538FC38629BB13F594D5B95BA4FF336CC6B5DA4C39743F9A8EE0901E339344A07FE33F93FE370BD92A78FC2D551A60DC927860615C13DCBF289
	19576578B969EDF97A1C7421ECDDD7D44831F54581593A2EB8AC4756957534AEF6C16E6E91FC4D8257ABF0EDD4A7624EF9E45DAD709D54AF44BB95B4A86ABF6A57B8457598388BA6DC2F016B17F1DCC113215B382CA253F960D1D5766C21BC9E9F24D9918F9F75519310CCFE87852ED77465193C587EBD17E76EDF735A9D4927384DFCEC4FB0BE46778CAB814F46748E7CA459207FC81C1B95DD60745AABE17AB2C1EF97740BDA18DE5F4F69B384BDA19826AFF5F07AE2C13F8B17DAA34F1D99A7E2BE7C70B05605
	621B29C63F6A574A87D4E9F044180B13E15EC71CD8A77B147192415F821328749D99EACA3F6B1D32120A32124A2ABD625B02D66E475A8BD36D1D12BE8F5B382D523CF7E2E7B8F3425F5177255965BEFE663CE96BDB447C9B8F7A430EE6EFC01FF0509C64D0A49F77A55D4DFF53B1370F1FBB0F8C215E95703C86A067A2BFDF7C010B7BDDFC514AEF9E47477F9276BB497D3691C76328343C937F732DC3303C3179E4D7739942732F743E69FBA8BADD983EB834A50C67534528F5A647A26AC4704DFD71EAEA2407A3
	4EBCA6C1E656D5497DACC2EE814824DC15DCF33FD9AE95E4522FCA6E5108331235A013FB69EA64F6351A650AC126622A647E4EBD5D99DD53A52BF31305534A3DE5E96FD9AA57BF0DEFE6E0E71B3F2263D43F94FD66391A6293AEC51FF9DE9962E71E09C04B38447799BF0AF8EF5BF599464F6529FDE1B96E1631583CB7E7ACB6BFBEB99A4B772B51D83ED596FC4FDB7075754772C97D31FC690ED83EE79D31FC87DBE3798E371A7958778ECB8A599CD4BDBE25117D7AC4702FE3FBBF085B2C4BBC2E4F523A1D5D3F
	E03A9F9F7A7A4FF6E6FD2DB6D81B9FF345466DC917B9EEAC8796FA4CEBB04B11AFFD0171E03FD37C0BFC86125C425F1175C10D640BB524158A240F6D2F4DFE662EEC9B49E9A59BD89153CAF23092E9A56BE92152CA3221088961CA1AE3066095C2B37155E0F5D248DF121760DB721252076E776E51C9913E12B7B07CEBE6C0E775G74821C87A851C9A57B6AF52244451584D1C47249A578CDEC0926847D6AA8EE71CAF4FF49E8D0297DB24EFD85D80DEC0DDC4C333E33639B855F5CBFEAA339ADE483AC2C813784
	8125DF183613CD83B4320A863CBAA9E8C759FDA03B0F4A9EEEA7364120CE5E886387914F281D81E9D081963BACE9A3D4833A746A85E29F52DC835400CEEC97DAAE00D97A046442F612273B71C88D6BFD1D6C882B6E40838103F01876B2F985A0C07F20E1DBA22A0E9FFF7B40F329CFF787334906D6729AEE08BA950DFDDBAC6337CB468F3ADA08BD60873D5833B01C027432BE28CF28507A2221B378E305202E488D7C548C1A3B1D79845D286EF3CAC1B75D76C0C3F3FC7853B3320D11D549675A470E501837D98D
	7534C3B079927037107A0BDA4A0516429F663F77F65B2A14ECE2EBA1F92ADF12DB24C1589834923B6E064163762BF2A8F014A94A9D676FBE33C15D9B22795858F27CAAC3B8BD923AB3F14B6AADADB65836E8E49F6544B0168B8E3951ADE9E0CA51DAF57CA91DDF5BADE8573D253F7F33E3BB7AECA44BE8A476544ED0C0478163752893F053C4EBBCBA6A0AF570D613FCC15832FA413312A84FB8C998A83F65510ADB6FFE79BCB5909655492EF06F63045A57B0B484B9D7A80038027AB705FA64A90F6259963AF9FB
	5BE32F8C40B8E8A2595E0A8A7A3F127E2F427F1594D3C9B115D5905C8CB265DFB4BD445BCC870F48DC587D65AD50037A133FFBF47EAF6BB256C06BDA0C09851DD09066BAB96A9DD0349B965E7A66265969B7E29F85F02804D0EFC9681AA6FD1E4941BCFE9E38678B73F9E0667503F15F7BA7A9EC5DDF2CC41F87EE226BDBA6600B06443BBD57C7A47EDE3653AB29AA6C24C79AFCB26EEB9D933A92BE2BAFF0237EA067B108469E590DFE8F1BE47E9FD0CB8788A44778C3E596GGACBFGGD0CB818294G94G88
	G88GB6FBB0B6A44778C3E596GGACBFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1F96GGGG
**end of data**/
}
}