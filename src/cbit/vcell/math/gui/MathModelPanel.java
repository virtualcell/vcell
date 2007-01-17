package cbit.vcell.math.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (4/9/01 8:03:34 AM)
 * @author: Jim Schaff
 */
public class MathModelPanel extends javax.swing.JPanel {
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;
	private javax.swing.JPanel ivjPage = null;
	private cbit.vcell.geometry.gui.GeometrySummaryPanel ivjGeometrySummaryPanel = null;
	private javax.swing.JSplitPane ivjJSplitPane1 = null;
	private MathDescEditor ivjMathDescEditor = null;
	private MathDescPanel ivjMathDescPanel = null;
	private cbit.vcell.math.MathDescription fieldMathDescription = null;
	private boolean ivjConnPtoP1Aligning = false;
	private cbit.vcell.math.MathDescription ivjmathDescription1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JTextArea ivjStatusTextArea = null;
	private boolean fieldEnableTestFramework = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MathModelPanel.this && (evt.getPropertyName().equals("mathDescription"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == MathModelPanel.this.getmathDescription1() && (evt.getPropertyName().equals("warning"))) 
				connEtoM5(evt);
			if (evt.getSource() == MathModelPanel.this.getMathDescEditor() && (evt.getPropertyName().equals("mathDescription"))) 
				connEtoM6(evt);
			if (evt.getSource() == MathModelPanel.this.getmathDescription1() && (evt.getPropertyName().equals("geometry"))) 
				connEtoM7(evt);
			if (evt.getSource() == MathModelPanel.this.getmathDescription1() && (evt.getPropertyName().equals("geometry"))) 
				connEtoM8(evt);
			if (evt.getSource() == MathModelPanel.this && (evt.getPropertyName().equals("enableTestFramework"))) 
				connEtoM10(evt);
			if (evt.getSource() == MathModelPanel.this && (evt.getPropertyName().equals("enableTestFramework"))) 
				connEtoM12(evt);
		};
	};

/**
 * MathModelPanel constructor comment.
 */
public MathModelPanel() {
	super();
	initialize();
}

/**
 * MathModelPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public MathModelPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * MathModelPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public MathModelPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * MathModelPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public MathModelPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoM1:  (mathDescription1.this --> MathDescEditor.mathDescription)
 * @param value cbit.vcell.math.MathDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.math.MathDescription value) {
	try {
		// user code begin {1}
		// user code end
		if ((getmathDescription1() != null)) {
			getMathDescEditor().setMathDescription(getmathDescription1());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM10:  (MathModelPanel.enableTestFramework --> MathDescEditor.constructedSolnButtonVisibility)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getMathDescEditor().setConstructedSolnButtonVisibility(this.getEnableTestFramework());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM11:  (MathModelPanel.initialize() --> MathDescEditor.constructedSolnButtonVisibility)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11() {
	try {
		// user code begin {1}
		// user code end
		getMathDescEditor().setConstructedSolnButtonVisibility(this.getEnableTestFramework());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM12:  (MathModelPanel.enableTestFramework --> MathDescEditor.approxSensSolnButtonVisibility)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getMathDescEditor().setApproxSensSolnButtonVisibility(this.getEnableTestFramework());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM13:  (MathModelPanel.initialize() --> MathDescEditor.approxSensSolnButtonVisibility)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM13() {
	try {
		// user code begin {1}
		// user code end
		getMathDescEditor().setApproxSensSolnButtonVisibility(this.getEnableTestFramework());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (mathDescription1.this --> GeometrySummaryPanel.geometry)
 * @param value cbit.vcell.math.MathDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.math.MathDescription value) {
	try {
		// user code begin {1}
		// user code end
		if ((getmathDescription1() != null)) {
			getGeometrySummaryPanel().setGeometry(getmathDescription1().getGeometry());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (mathDescription1.this --> MathDescPanel.mathDescription)
 * @param value cbit.vcell.math.MathDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.math.MathDescription value) {
	try {
		// user code begin {1}
		// user code end
		if ((getmathDescription1() != null)) {
			getMathDescPanel().setMathDescription(getmathDescription1());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (mathDescription1.this --> JTextArea1.text)
 * @param value cbit.vcell.math.MathDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(cbit.vcell.math.MathDescription value) {
	try {
		// user code begin {1}
		// user code end
		if ((getmathDescription1() != null)) {
			getStatusTextArea().setText(getmathDescription1().getWarning());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM5:  (mathDescription1.warning --> JTextArea1.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getmathDescription1() != null)) {
			getStatusTextArea().setText(getmathDescription1().getWarning());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (MathDescEditor.mathDescription --> mathDescription1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setmathDescription1(getMathDescEditor().getMathDescription());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM7:  (mathDescription1.geometry --> GeometrySummaryPanel.geometry)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getmathDescription1() != null)) {
			getGeometrySummaryPanel().setGeometry(getmathDescription1().getGeometry());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM8:  (mathDescription1.geometry --> JTextArea1.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getmathDescription1() != null)) {
			getStatusTextArea().setText(getmathDescription1().getWarning());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM9:  (mathDescription1.this --> mathDescription1.isValid()Z)
 * @return boolean
 * @param value cbit.vcell.math.MathDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private boolean connEtoM9(cbit.vcell.math.MathDescription value) {
	boolean connEtoM9Result = false;
	try {
		// user code begin {1}
		// user code end
		connEtoM9Result = getmathDescription1().isValid();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	return connEtoM9Result;
}


/**
 * connPtoP1SetSource:  (MathModelPanel.mathDescription <--> mathDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getmathDescription1() != null)) {
				this.setMathDescription(getmathDescription1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (MathModelPanel.mathDescription <--> mathDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setmathDescription1(this.getMathDescription());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Gets the enableTestFramework property (boolean) value.
 * @return The enableTestFramework property value.
 * @see #setEnableTestFramework
 */
public boolean getEnableTestFramework() {
	return fieldEnableTestFramework;
}


/**
 * Return the GeometrySummaryPanel property value.
 * @return cbit.vcell.geometry.gui.GeometrySummaryPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.gui.GeometrySummaryPanel getGeometrySummaryPanel() {
	if (ivjGeometrySummaryPanel == null) {
		try {
			ivjGeometrySummaryPanel = new cbit.vcell.geometry.gui.GeometrySummaryPanel();
			ivjGeometrySummaryPanel.setName("GeometrySummaryPanel");
			ivjGeometrySummaryPanel.setMinimumSize(new java.awt.Dimension(50, 150));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometrySummaryPanel;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.BorderLayout());
			getJPanel1().add(getMathDescPanel(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.BorderLayout());
			getJPanel2().add(getMathDescEditor(), "Center");
			getJPanel2().add(getStatusTextArea(), "South");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}

/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getJSplitPane1() {
	if (ivjJSplitPane1 == null) {
		try {
			ivjJSplitPane1 = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			ivjJSplitPane1.setName("JSplitPane1");
			ivjJSplitPane1.setOneTouchExpandable(true);
			getJSplitPane1().add(getGeometrySummaryPanel(), "top");
			getJSplitPane1().add(getJPanel2(), "bottom");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSplitPane1;
}

/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.insertTab("Math Editor", null, getPage(), null, 0);
			ivjJTabbedPane1.insertTab("Equation Viewer", null, getJPanel1(), null, 1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane1;
}


/**
 * Return the MathDescEditor property value.
 * @return cbit.vcell.math.gui.MathDescEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathDescEditor getMathDescEditor() {
	if (ivjMathDescEditor == null) {
		try {
			ivjMathDescEditor = new cbit.vcell.math.gui.MathDescEditor();
			ivjMathDescEditor.setName("MathDescEditor");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathDescEditor;
}


/**
 * Return the MathDescPanel property value.
 * @return cbit.vcell.math.gui.MathDescPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathDescPanel getMathDescPanel() {
	if (ivjMathDescPanel == null) {
		try {
			ivjMathDescPanel = new cbit.vcell.math.gui.MathDescPanel();
			ivjMathDescPanel.setName("MathDescPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathDescPanel;
}


/**
 * Gets the mathDescription property (cbit.vcell.math.MathDescription) value.
 * @return The mathDescription property value.
 * @see #setMathDescription
 */
public cbit.vcell.math.MathDescription getMathDescription() {
	return fieldMathDescription;
}


/**
 * Return the mathDescription1 property value.
 * @return cbit.vcell.math.MathDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.math.MathDescription getmathDescription1() {
	// user code begin {1}
	// user code end
	return ivjmathDescription1;
}


/**
 * Return the Page property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getPage() {
	if (ivjPage == null) {
		try {
			ivjPage = new javax.swing.JPanel();
			ivjPage.setName("Page");
			ivjPage.setLayout(new java.awt.BorderLayout());
			getPage().add(getJSplitPane1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPage;
}

/**
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getStatusTextArea() {
	if (ivjStatusTextArea == null) {
		try {
			ivjStatusTextArea = new javax.swing.JTextArea();
			ivjStatusTextArea.setName("StatusTextArea");
			ivjStatusTextArea.setWrapStyleWord(true);
			ivjStatusTextArea.setForeground(java.awt.Color.red);
			ivjStatusTextArea.setRows(3);
			ivjStatusTextArea.setMinimumSize(new java.awt.Dimension(0, 16));
			ivjStatusTextArea.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusTextArea;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION in MathModelPanel ---------");
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
	this.addPropertyChangeListener(ivjEventHandler);
	getMathDescEditor().addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MathModelPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(495, 608);
		add(getJTabbedPane1(), "Center");
		initConnections();
		connEtoM11();
		connEtoM13();
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
		MathModelPanel aMathModelPanel;
		aMathModelPanel = new MathModelPanel();
		frame.setContentPane(aMathModelPanel);
		frame.setSize(aMathModelPanel.getSize());
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
 * Sets the enableTestFramework property (boolean) value.
 * @param enableTestFramework The new value for the property.
 * @see #getEnableTestFramework
 */
public void setEnableTestFramework(boolean enableTestFramework) {
	boolean oldValue = fieldEnableTestFramework;
	fieldEnableTestFramework = enableTestFramework;
	firePropertyChange("enableTestFramework", new Boolean(oldValue), new Boolean(enableTestFramework));
}


/**
 * Sets the mathDescription property (cbit.vcell.math.MathDescription) value.
 * @param mathDescription The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getMathDescription
 */
public void setMathDescription(cbit.vcell.math.MathDescription mathDescription) throws java.beans.PropertyVetoException {
	cbit.vcell.math.MathDescription oldValue = fieldMathDescription;
	fireVetoableChange("mathDescription", oldValue, mathDescription);
	fieldMathDescription = mathDescription;
	firePropertyChange("mathDescription", oldValue, mathDescription);
}


/**
 * Set the mathDescription1 to a new value.
 * @param newValue cbit.vcell.math.MathDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmathDescription1(cbit.vcell.math.MathDescription newValue) {
	if (ivjmathDescription1 != newValue) {
		try {
			cbit.vcell.math.MathDescription oldValue = getmathDescription1();
			/* Stop listening for events from the current object */
			if (ivjmathDescription1 != null) {
				ivjmathDescription1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjmathDescription1 = newValue;

			/* Listen for events from the new object */
			if (ivjmathDescription1 != null) {
				ivjmathDescription1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connEtoM9(ivjmathDescription1);
			connEtoM1(ivjmathDescription1);
			connEtoM2(ivjmathDescription1);
			connEtoM3(ivjmathDescription1);
			connEtoM4(ivjmathDescription1);
			firePropertyChange("mathDescription", oldValue, newValue);
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD1FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E165BC8DD0D45755A4A17D128EE96804A644128CCDCCE29B5A1A56B4760B7D06642379ECE2DAC33121A9E97DDA26B5D31B58462634E5A60F9FA36BCF0211D4D454759FA3A1A82808E8C05020C1C1D991E5919E6C83D6760F775E324B1FFE675E7B4E5B374BDB96B7F126B39FB3E75E3B671EFB4FBDBF771CFB6F3B8BD730A5FED9D6F68147E5AD63143FC3D99C17E9663877EF7FC4C2DC656EE48327795B8A
	30064B9C4F05E7835419EFCBB664F36B467220EC06F25E2D4906F5703E167B05A57EB23C846163A5F551953BA667736DB348E7B9ADA72EDB854FF7813AGC71E893B897F54F5058ABED241A3E4E5F3DCC4B121F9F3DD1102DBC2468938EA0066E2C65F8FCFB6005B2BAA95BA73D7E7F0CB1F9733D713FEB4FDA24CD55AAED61EC66E9FD9B1FB882F2242D5CA1B9DD0B6839061F338F70B0A61D95DBC50F72C275BAA178E7588B6DB291D177BCB7B5C562A2AB370F646E991EC4D3CC33065FFC87A32946651369C17
	8A7D44D1DC59A0695F846FB600488F917F20C8708B61BD03147722CCC7F7D7ABE3EE9BFD0ACB59EF4FFDAF08CC45FB19AC4DFBFDB2AD363D1A3D1E6A7FCFE7091E2320EEA6C09240A2003C12E4C3B140C6EA7725E7BE04E7ED37DC67345B1D0EDA176B384DA2D229603D2A8AEA945CF92147A9DAB80E59F6DBE1094EF8A683562E6C065BB11D4CA8C7BBA71C7F8E17767DFB33176B68A4ED46E8F6CD8953C5DB09CFA70C37B5A8EFEF093F9F7A733E48E9F9177CF2C14EB29D5E8D4FAD4809D27A095770CEA9A136
	1ECEFDA0915EC9BB521E613FCB71059F79704CA78FD2BCA917C0DD74C7689BED6F62D89AF39F6272775D272B0758E7233217FE4446302AB4F0AC2FCB246F4AD246F31FBA1677E4021F2E413331CC27FECA4A799F036F16A59B18FF3D55CC66E8717EE4C3BD0099C0860881188910349FFD6C68590F2768E34D3CA89FF7DBED96C164D85B374E7E8B1E02D79679D38EC9669DBDC2ABAFDA79EE1BGD10F513CFD1668205B2FDDEE93717D9E6071296090C4DE36BA9D50BBEF97E4C114984D6A26B504C694E82D49EA
	9700015D45E13F9FB42D05270D17648BAE8BAF8BBE8AD63F290958E408EF059E2183F8E7FA59FE1E68AB9D644F84D8D50EFA08257DFDA60890AD2A2A9A1D4E81374BC4A4614C65C44F91B4F698613D329CF53C2505606221BCDB6D67E7CD6FEB7A29377610617362B04E31EF8D12B67300FED1B97A4D6BED68B7752B3F4D15AC781A2E5F4C819F4E2DE07ED2D491B8FF1E247D123155D7E00C2F9EEC8BB227107F0D4CF57D18F1A1A80F9CE50EFC9E9ECC4FF719084DACA0D784C0FC856AFAC53311796EF1DE9250
	DB1BC4F0B4416253BC9B67D229CB5169F64D58C89D3141CAG121B5E233424EF0E7BGE01DB28642EF9340960E65531DG3B8176G6C85788860E3D5A79F480457EC9867C2003C834906B200B600880099G7381B2G0C87138D8DGAE00D800C400C5G7907404FG3AG6EG1889108A40D5C2FEGE82EC49BFFCB0C48E2F2FDDE1936CFA2576731617F87E036D8AD91DB64FC81B6D896448E7B7E8DE47DF787969B38F3C467421060106B85176030400B2454ADBFC7EC23642DDE275AE6B5453BA04F55C1E211
	D47C5AF344F63CAC0B56EE37AC348832ECF574796AB7D2DE123B5B65CB1148EB5BB9E2CB2787721CCA3F7B9C4991BDCE07C320C1CAED22546F21B9CA3089F698F41D536D10997E6373E48DBFA08CABB9327C7CBB9C49F1B6B74937ECDDBC13A7BE67922DF688FDA4E5F3F3ABC97E3A2F30D023A3AF60487ABE8E1EB3ABB18FF936E39E5AB172CDAE3153152D37160AFD646E6C7C43ACF76CB86C4BC3ECCCBBDA74477EA14D158ADEE2E3FD1A3675DF29337933169BBD188FCF321E2F97249E516AA20A5A477241F3
	97880FFA605D8660BA0C79605196D20F39D7C984F5CE1B0D29996AE69EA63A787BE001A24B9DD00EBF4CF461731DFBCC0437A48C3A85D85210C540E72A4E27195E8F28EBD5744F6A9FB2917D5BB46B076AAA4233C346BDD30642AF3F0A656A92F8EE867CDCCA375B0DF4F7A875714AF3E6959BA3237B0DCAD7F04457CF893CB79FE1F431C788DD429052590E30DC942921AFD57B6B72A0DD3A26BE935E0D555A7E76FA112E221A65B46C2F31DA5BDF3ACA57D56D6B4F8B6FF1FE7DBDB10CF4332BD9EE549F1FB002
	F473157E9641B383207028363F72D124ABBE4AF2AC76D7F3D45B5FE2154EF454D7BF313F6F0D297ABB4AF235FEFF3D63C897F91459A16E28EF1C1A71DDD365BD4AF2BE698F69FD7D7DC1251B0F7D9CE57648749B5FD35711AE7F98DBBB6078967975B74029FAB9662BA772569C5376F7A08B691A0F31B5887657FFCC5B1F3C9C69460EB1FF0EB9466418F5CC6B27E77210EE21C21791C047782EB728F2FC4256BB04AF766B637B22CAD77C892B2F790475577509166F23AB10AECA211B9EC047780AAB11EE4EA7EC
	ED05FCC93F04AF0BD5EF6EA171131EB7DC6507F8EF2964019CD0D5F5DAF33E1035BECF21CF79844F91BEAAA231EAD5CD32E187C0F58D465A17D663B9C24C408CAE6F15141CE0E7A3269AB6DEEF0DAF564E3A571343E2E17EC542F3B6544D2D41B8386902F6AFD3ABC94E9EAB5DC6A9B1EA5E85EDACE63817AE2C00E72FD51464D310AC3D984FDE3EC046CB76D98ADA59F79A693E13446AB460DBD08B733F1646B91164A8925B181CBD65A8E77448A3DC575726E52F8CA2E7F4AD13EFE62DCF4E480283B90C5F17E4
	6437886A0D47138DACCFC8A2493D81F9C249A1EB5CE4ACF23FD5E2E379CBED0D42EB5EF79E62E6AF0F4A56BB3B69FFACB63B6DB89BC344F15FD8667D76556C05GECBC4DEE9C4FFC20492B4371BC2572E4FE336884512BD502ACE135946F390C67965F5E2C9DCB9EFF3B6ED822DE6B4B2E29D34E556AFCE3E9C8F9B55B1C02E3499B42314C841AC5GAC677F172C4D79467AE40389408B90D50F633DE7C8CFFF2C5F85EA3F5301BE2D9E4F5E8A0E11A7DDF7F0A84F58A514A7E25EAC2EF06DDD3A7250FCF50249D1
	FD42A7CF816835D8556D1D9E649B85B4C9A7D01E3FEF574A1389786A13B01F816413A8CF011778F61F60348B32B84C7AB3F9557E00AEC925FDD107F675B052E682DD452748BB5E2965ED83FCBCC092C06A2758DF6CF0F0DD3E22763B84680B9BA02ED17CD7C6905F853858866C6B2FA368C72BCE913A7C21828D7F798D4C1699AA7D26D1CA8F0BDA562FF9947BB5C17B28D3C837E0146CCDBC3C680070250C6D03B124CD823A4CD3B836DF29780A5349865BE9445FBD2E7A856052CEE35FEF0D87175FFC8D5B94FC
	863162B36CAB7FBA62A381B767B36C4BF3DDEB9B060B3FBE51DE8B5576CB21EDC9A3769B43A15E823838C66CF7CB163F1E2EE4A1DD8A502CBA03F49D4BA76A690165F86ED78FF4E3AAEDEE9E09370203AC77CC02A41FA4C7D99E27B820442132BC323F03753EA40B6E9ED93034B8ED306817E5272355AAD93B2DB62BBC2C70984A43714C027E972BBCDE25F82B43AADBA1120C8845FB987DF183526FB81BECB01FC5795FB260D8E3GB76F2CAA570A4947FA678A329665DDAE5169ED919C126ED015317EEC85724E
	ED02395804BC8E2E549F6B57D7A23D8C3489CDF806714E7A40335F74A65F59D4C65344F3AAF6A67A8D16DB1B5919E80E0AFF1C620BB4F8463F3C887917C0DDF4B31E635DF5094C6D25D04EBF87F3932086208BA0629C66544E56F2AE44B969A9078CA917373578ED84713C72A2618BFBB7253C7E22B606B2BD6C92099E624ECD7DDC0E352B206DE65DC0BB963377DC56FE1F49DDF3BA1B0CD1AFE627ABFDAFBD670B59CCF7475AC9DC2AB88F7B8E00FE0088007873283787AF1604529B1E99AB8AE3B2C549213EC5
	A41E771717351B9632DDFAC0BBA6C70C192C83B3212E30A559D08650886082088218590272AC3EF8BC14BC35F8D0E0F22A2FF5011BFD9CEF2BBBBCB9ADA15B4DED511353F0054839906AB2818ACCA0AB0089408590E3C2B90F359D89A56789F23882B2D231A68A4846796ED0F8729505ECB74B645FCED927F3213F8B126CF442E2151DA24B6D0F8E861E0D10F545BC68FFA1GDB5B3FA56337438C932E6DBB689A306C826C8B813AAEE0BC7BD518B23F96321D77021ECD6F6CA4B60D063A8400A40005G392D30CE
	8628EFC51BC6370574DD75B34CD4FC77EECFF8F2BE90321D39D54F366B5B776B589657834C3E1FAA2596371D1D0051B3A5E6E743B62D1DE5609187108810520AF63EEFA76E619637221D5DB4776E3888FE81E03A08F63E6C8DCF7E7E106DEC97755A2D9D8E0F5F56106DA2AF6A69FBC3696EA94DA526E42663AF5B8267D29C741D8810F291F51C6A44393478A26AF85CC5F55C86BA86B035210E9706A9736BA15B595A745A450C04476F21106DA25B74F47C6278C6CE77DC4184CB54DAD160B92633CF07084E4899
	C1DC9B6E2D47CFEA4F90663CB79EF4EF4DDDD27276A5DF7EDE7C64DBB9EC6C5D2361496C8A592E6B12DE3B0DE361715B93321DD7175F7F06496F0D106D22F579FDEDBCBCFE8F07ECB7C317DF7735B06D973A1DAE3FFFDD8F0F5F0E106DD2F5797DBECCFECBC336DB224BEF9A979E3F99A15B65DD56DD4BE50547CF8C594E284B6F6165016DAA83687479BDB1215D660032C9175FD8DEF872FDB9E4BB1BAE3F86C3F87CDAC3360B546517B9215D547439E2C5A8FD466972DBB0215D54647BDD48F633F479FDE3E5F8
	7C3E9B325D3CGFE6C2E4DA6FA0E1E8EF599G8C770F8D84D7541EEC28E8473541E3C564AC27CDADBFC14BF16A7A6CF0D1605D94C2BB59F8EE84980F7393F864FCC17D93E06B76FBAF857F167D40A57DEF598FDF527F167D4DCBEB989E7A505E68427AA7AECD7E2D7B0717743EF5EF22F27FA21B2C61C437C3365A854A162DF184AF395BG3B0B73EE875BFA904ED2607E1F437D4CDA1E5851550E7B16264B7B39097B96360343CECC82AFB675762A1B1696175003E3BE599EDCFFE75A757557522E2F3FCB6D136B
	0FEF8F26BF2623BF48647E4704EF7FE7E1574561376F7F168D8ACD336CEC5E27767D20B9389C0F187565F85CCC560C1253AD768801E37D1E39D02D73ED72CE49021D8C0751BCCD5B4B3C58A7CC1075D95A1E5585EB7F02192CC93B2D962B28FC05E7B25E65C699CF484EB30755BE57DE89AE6306AB7AB29A2F8417F1771550B216DD89AE6321AB21E52C39924C66CC5693C3DAD969DDCD3A267F75A06EB5235AF19F341CFE67CA02F29A40D2B5367DF352F32DD566E0E724AF51B38A23191D05E62978D7A83EC403
	E7E724BDEE3CEFDD89F5F1E6BC53FD5F3392FD32D7F45A41AB153B921D416DB52DD35FDE711D416D75D8E7E8FB4D6C8CEE2F27BAC35BAB31D35FA71F73E86D341F43BD5A57CB49790A4E9E2D45E5334AE4774B763E2FEF565B7BF29D4906028E5C7B56EEA3B6A7B6AD69C01B9F5FCE6FB7C2F98CA02683ED9E37F3B21BA7GDDE2073F5D19ED7B687D64140EC01B3BA8FEE1C7204DC77350660B212E798A5A7C2BDEFF1B6FD774F4FAD82BA7F2AE4E647FED11D67ED9D758DD590570DCFA8565FF5606722F6AC479
	7F682077F920EC8610BBD17ED71D13499F55994C677FCC4F9262BB83657F8745A7F4864AFF1B8B651F8DF5C5DDA87F3A91FF794B9579DF98514ADF4161DA20F1A8F0AD905165BF4618AE7D75470CG7C9CA84F87C85760D9EEEC6CA6676D9900AF60138DA5G75G969EF3E5F7D7486F94A73C3A5FA918EC770F7A4BDC21604F0FE9E5BE40E1ECAB9C7727BF2060670DEB698F2A7477DF7327BF28604FDC57529FD269B7F17E7407947CCB1C163ED2257FFA16BFFD2512F7FF72AF9227DC104A85099EDC5773B22F
	34F9E5798ACDDF7B4A302F6F67796177A17E3A411F47E185DF3E420FFEBF522FDA6907AFC7FCAA7D9E260929E58CDF311E5C6B337143CE378CFBD41E7868E76B555F65C0B98EA089A015E7F30C4529A7E80C6E95F9BB04A73AG1A57CD5A2E5908EDAB215C88E0830882C868C6DF1FC5E343E9935F5DADD8C8C85B477CE99DCFE22D1A5B14F50A321F988C1C1F85BDBEFFAD6E89B6BFC7E90E2B6E897C3D853B63515C9378FB0B55B2FE4B34C05D5C9E5C933C6F46BDC10985E3464BEECE9D5FC24D3B559D6C1E7A
	97379738752672E071F357960C1FED960C1F262DC4BF51D01E8110286A626436E071B395E896DB7C6D43894986E6079F50FCD4A0845A67990AAF96826D53309D6DD38DF571825A677BBB51BE393D08AB5DC5F0E5D0EE6845F15A87FDF69A523C5F12F773EDB66D2672E073F2EF9E09C1FAE73C6AECD36D3BE1931E0D1BFB513E07B6937D46C0B981A0C9555B7F58826D1B566B336912DEFF7B667621FDBB1D74F7CDFD015F4D2D94DF5C97785D7C4F8E7CEEDE89F5F1FDE84B25CE34AF570FE37A1B4BE7BFCEE34B
	BBDDB75F168F5DD49ECCE687BD07AF81F99B7AD18FBF24B8B314E59597CD5776F7C0B9D645258E223E92D45C9A9167C32A0A6B37925CE2A8F3D64C6B266E606B6C76EE7DF5B65F2D3F076D6B1EFC8F6B6C8E361FE17EA02D4775D2810C2F510A79677725FE5FB2CAF90FDCD5F55CC96EE1B732E446FC7CE9FA3EC35AC7DA5147DF23FD4E01F28A40E2ABFE63706E42EF9C890FBE44E5AE503FA73950B41E1DFB1579FB71D55FB78E4E7EEAB663F9E8A37234C0FD5CD5FA27F82376CE95716FE7F2B92E941E2C7EBE
	3FFA229372FB8CDF7DCF7C6A49F8FF13232D7F27DF7DB2CDFBA65B8BBBD1364C97E6F236A7A3037EBEB6DD11A9EF00BD23536F4FF27DFEA4C7B99B23FED583F526817421E189FD4DBB00F373967A8DFBBA1493D5DC944511FB3164AE30721DD1561BC75387FC71A4F1A07CF3B44623C41707766C6C737CF6EC49G51419E37F1921AB4C586B61E3DDBA61C615AFCB2B65A7C656D32E1AC1DCBF32FEC8B0C2549949FE98B0C251737E2AC0D013A8C9B5A2B036E8B4DF658835A91774DEDE843E89577AA454D01F20A
	9D6DB5E7BB41AD02F21EC34D8D94C76E0B13FB028CF7F67BD463A369C7DBA6FDCF4D2EFDD36691E85F291C1B56B8088E1E5F09F6ADF2CC9CD71902D34EDEF611B5B1AC082DF6373D45BAA2F009340F8E3A8E39835EE3D41DED59B459FEB35799ECBD7BB84D4F054E4075528FA83E449938DEBA3A9957CBB5D0B75D09367D358D4F3D73DC08EBF328EBDC95672538E6A8DBDCB8F641C94FCC553213163D6A1C7E1BA35C5CC87A1C1A6D731D6172180A6D591CDE69C25BC7690CAB5E25357D6DC3A41EFAC45E55A28F
	5B040B74A7720A7D9F9C42F556AC976E01668F51BB70CED168931DEE0785E997D15AA4201B2F52BEB6C874ABBABD92237951A02E4B17G8D9B6B9B0346C964618659D8D97BAA3A8EA8G1CF190F9DC276BG41E2154971B8233B570AF92CF190EDBBBC617E64546C759FE136CB3C61F6ECBEBC3D0178FD57E06039CC92455B86834FE55ED05799DE28CB9B447B1A7BADE4BFDBA342FCG108122G92G660A38DEDA56835B78494FC1CCCED7A3AC28EC1AA510723B690DB7AA9F4B674FAC47FCBE6BBF3F454D7ED2
	FD507B98E9A233FD2672AC7E5D7DD96449647B31C064AB11E00D89E0830884088518A921FCE7FA14FB1564B3C82DAC73BD7D74579C6AC7952B60712149E100721B88F16259C231086783C992BBDBC803679209658E96F323A57465BCF9623CAB1235736E10193C7B5F34433AFBBEA5F5ADB2AF3BA5750A0BD2F752CD46D6A710FB3428D30F00BAADFA7CE12EE8CBCD761AA0BAADD346E016FD6BBFDA26B17378960C2D51B24651B9C60C2D33D4DC1A91E3EB2A0C736B27DB49DCCFD74B2934CC6E8832F25756F063
	DE3A0EAE75675CF058BC2692DB197E65ED64370DCE07E0F23AFB7ACFF8DD3C4342BE4C31587315ED98B3B3E50CF1095B0C13746B9F0F17EEA37341A9DA41426C77C82C5FD55BB1262DF2E36CBB3C022C1D75EE73A9670535C4AF6C57D77B15FD45DF543BFBB5EABF9F6E22774D1DAE451F2E66EB7DA9ED38B528BFF95DEC6C535D7E7E1466A67FDF47EA67D4395A687DFE0760B1623627345E8A530E7C63096283E5A546FD874A76A87C63BD5ABB5715ABEE557D0DAD65B74478D6A84F4C8F26E5B19FBD38897D56
	B624FE7B5ECCF031D01E216266D09C3963C8FE2B42F0F51BA77323998A2F58217056770C07F9D29E386E8B77BBB90BD94B6D9833668C311815824FC5C3CCD64EC3E81C0E42C97AE136FB134EF7C830A62BEC9318EDB39CBE5BCE5BC956907EB79D712C265C057E5660C1FF3B3AC99B43986E49A17A1B93512D745FB9686B7F0EC1B267D43F29F35A1CE229A8D830BEE51078C639CA3FCAA47CDAA0FA772B3EF82B686B2F433AA938AF92D7BC38C72515EC242FDE5EA6794EF41F5940296F4FEB5E7F3A7E661F5F3C
	3B61E672E0F97308A578B94AF10B7EB9CA03C57F9C652CE5723B922D16607F972043227D3FGAC6F1F17C84C0BGDF0A83C884C881D864411C7FC74BB60E66FC1DFCFF4252C73E17127F42A3A37F1F8A41E5DDA0047E5FBD3F9648FA385BA94BCEBB699F716982319744D40AE438578422A3515A57AFB3D2857F06C06608CD6825E8653713A22E6BF23D4906EAAFFE9B0C95B18F48DEBC97FA0E52A6C0B989A0CD251DAD615CDB6A453937C4FAC79DAF6A74B159884F12E1D8CB8158GA281E281E682A481248D23
	FE2F77125F8E3472D7A75EB081A4BB4822BA96F92BC3E6F1E8E08B46180CE1960772C7128DBBC6585DF29607FEEE1CAC8E31B9AC8C796670BD9F12BC697B3A3CCF41BF328D6388ABEF5FCE7ED70866C839545726347544F953293A5A464E166646D3A6F50E7D4D6F1BF2B50C33F9E472D84F74F85AC374E8815A880069GF3G6683AC824884A898C5BD7E38F76FA4FAD40768D325129BFF562E4D0D7953EC59389F889CD35928729B5251405FA3BDC3FF536982FC14BA1E6F58A79BCF3368F48922BC4C30743FB9
	A8FB4DB31B0C136845FFEDF34DCBEC534C77A936F9D9722DCF76A97B190F6D687789B03674D174E50F9D7D3EE88C7D7E91CAEB02328DA0F28C4FC367F6E84F8C0DFB1BF44F8CB3BAC6326346147D6518CFC70B1CC6654EF725CB6B87CB01A6EFFCF2BFE03A3CAC850FA93C94BA26DC15581E36CFB08D3B147B98CEBAEF1D3D3D122046AED94AD7F12724E627E4A58BC6651E26985E3E5592E63B87EE389D5B57D6517F65D6B49E385F2D23786271407D6EBCB5AED6C0DD6CB82E59FEA4E979E782BE7F5AD4EC75BD
	B9382DFEA80736553332FE7C1FABCF0C7FAF4A7A717F6732B67EB3DC2A522F28DEFCD3720DCCFC196DCD4F708E18CB222A1BFB3620EEF6006C66EBB8F7FE358567CE4CB51CBB86A3211D8765F4004CEB3837FB171E7995DCCFB63C14037B423594D782B8CFAE0E6302A7385E4C1E50FA93BC7AFA9B70CC541B4B232F37A14FC43D0D52FEC5218FE602D6EF37F874744662E1568827763956CB7205EE9C049544E948A3CD5DD7059E59447729ED1E7392FFD257B8ACBC224D3DE43D227E4E17EE69DA005051477C76
	00BD3C79D697E63B319BEE476663FA7AFD3361FA60BC5DC671266B017374F1B50EF7C1DD52F5BC17AA9CA5F93EB8A259D08F508F908190831090013928E5F832B5867BCC66CB0C4CC71BDC683756DC74DB138B7D76299C143F067E2FD976FECA731EAC5E7C357B9FEFAA8FA6F30AB4F17E7ED2C2BD1CD275F07F04EFB9D3136131B05BAD3C61F64C3F46E91CCA0C88743BAF3178959168F76F99516FD2212E7A967C6E4A6FA2F8E59EDE34DA64FE06AF722E76619B8492E354B1EFF1787C23D8737E047D667B4A73
	B715871BE7DBC7034770625150B1FC5F28FE8CBFB4BAB106D70F6A477063235A984EE43F4D6853F514667D83634D57FB69CD65417ED735F56CD3F05004BFF14BA411FBFF6299C46DD7FE79F2C2E7FDC1B617FD0AFB271FBA65A9EE1907F82D175C9D3997E03F4C3DF7E250CD930A111CE4433ED9645E75BFA549FAE45DED79F7173B33393C135CBB3D3C4D554FC3CF74DFAAB4F2AB3B29BD48064DA6F3790D246D475076E35A76C8A3175DA3495CC69F3E0760D9ED0B0B6F910044E06145815A43A0F70D4BED678C
	3DE2DFB7E5A0F3596DA75B01ADAD116AF79A3995F2BF595410FFAEA2F36BFCDDB713D02ED49C21C2F2EF3FCDBE2C11BF09B4A6FFE43D4B495CCAF256E4D48E4D157F53F0DD3BAF4A383DA76819E17CEDECDDB86B3640FD511B5708AFAE817C2AC84CC5F557487F158C369EE83173B61B4C5B86EA9D9632B6B8BEAC8B3E330D0CEB24FF89E914CAD67776B5B26E2B1AB67F87D0CB87882EE2645B089CGGA4DDGGD0CB818294G94G88G88GD1FBB0B62EE2645B089CGGA4DDGG8CGGGGGGGGG
	GGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG429CGGGG
**end of data**/
}
}