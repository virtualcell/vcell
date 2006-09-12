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
	D0CB838494G88G88G560171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E165BC8BF4D45535D103D2449AB5E844889179C48C98A4EA1408A05122E2CB7B2C522E74D5EDDA71197AF81A3E15D6DAEFFEA4B0A88182848818AAAAA88D3F28988285898A9AEBB453184AAF13191BE448E46EF067CEE672033777B9F75F39B3391309232C5535DE565A6B4E59E71F334F7E1C3D4FB9775C88252FA5946666158AC2EE21207EFD10AB880BCF884228D317BDC43898E723C9507D7D95E025F0
	6ECC81BC9320AE6561C6D3095070E5B114D3205C7CD023E99D7CDEADB4FD19708D7C88414787F5A5164D950373E9B8C9FC0AD87929954B6179AAC0AA6070D9A0A17FBF2EA8D37199AA1EA0B7CF9096B01A5212F295F7944A4DG31G0992271F854F250C73306A3DAA5D092B92056C07653CD7309FDD1F8495AE5E36D6FDD688FF4A4DF2A12F7DA54B55B6FDD0CE81C0617305D74B374233263E236D0035452ED4F6D9C5072332532234D736F96C55551F432F4FA51B682837B8C5C77BD96CEBD8C9B1EBAB88BB21
	1C4DF09F38317FA4781D8E102161A7A8085FABB71A6A815AE512E97F5BB56A181BFA6F92B25B9ED83AA204CCD1B217A5D1764BD471CECB7EFA267FEBCE211EB3212E585DE8AA87B888508C9085301159FD7449F760F928C5B9ACF5F6CA4EC3AE57C78E1B4C24025F5555D023623E922D12EC9384EE5B43A55B8D46B39870F6DFFE6BF6DCA709FB484EC9DFCD95B226DF1BD7E420130C445E3CB8B757C5325B2F934EFBC4C85E735D01FE98487BEBC14FDBF8F6F7D121816F7247F797E52A7D6468F8EFD7505637B2
	9FA8035F588E5BF37C1D8C6F5261D51FE4F8ACC7C35DC205FC2369D59ACBDD41B821E457F506FA083BFFE4DE1D070F4166899E4B9FBC58F70C07735C250DE5ED9762976B70FCAC37B2BF45F2FB97782E3751447DAB3E9967E8D4F723A99EA085E09EC096C0F18F78DB8F7958EFCE6E98220F55DBE465A30F5DE193E501370DBF35961E22CF11AD471CEE4562340A0D9659EEE9F108EEC12519F08AF5509250EEB273FDAB7078C4F40A32C531CBCE685D52A9AA226C66B453CF2DC49AD9E435E6FB27888CBADD8275
	BB6354EAF8BAACEE651FAE1BC5917D943CFE4EA934C9172591FA048E60B7574BC39654D78D48EF8630E9FAB8F09A7B7BCC14A1DAD4D757C9D20747E5C6C904149E5473CDACF67860F7CC8F697801964465E0FDAF7563B93DCA57CF2D5D0A4337485DB4472639314D9620BF58CBFE73FC9379CD6DABD3046DBF9FE568B7B16043A7FA393FA83D4173A70D750BE30B6F2598FF585D94E2CE917FEFB3570DE3460910BC7255B972DDF8F0BD6F31214D52E03C8BGF2B4DDA71F2E603E7B1145AD1237FEA1032309B63F
	6679B8DF72B42BBAFDC3B7B62CC39BE042445C340251E25F023086E01DBA8664378960B5016753B7815E86588670AE405FGF6E8BAA96FC2DEA5FD0D263DG4DG4381128196G887D0D262DG75GBE00840039G0BG4A4E01DDG5A81E2GD2811E84A8BE5FE85A895084908510F81EECBCCD094A65F2FDD7195E98C42E6FE2437F8F40EDD16AC1DB64FF8FB6A88CE107DD7F86327E3B830F8D931BD167E21768D4EAC5176834418F37DAB72599ED23662DD6C9EBB31D61DD10678EC3E2F1EB783B1B51F696C5
	116DAD9EC5EC9095456EEC73574FE63C5C1E9617BFC5922F394DE8CB498BF9CE23FF3499F304D5F2BAC5962434A6EA7DCFD80E929DE2A78C7A3064F1AA9C7FF38B2E61BB44EEB5C77E4232C4409C6770E03E656B62E48B7A1CCB36F7C2684314ADD4C4E17ECAADA95369E8BE448FDC5FE74333B80A7210778D4AC3DBFAEE91E61DF665992D2562A6DC19579E45F3C7DF14BF8F71B1BD5CE2BC7679ACD72AF8B79F6B0C2558B6F0254E674FEA21777D92F862FA3ED6F4DBE53B8B95350B67830708BC6201F7B2405C
	A84A87F5D62C275C2BA60243124341558CF5459721AE8425D41565C9E81B9345F561771D8FED485BAD1E7508302441C540E71A4E8F5AD68555B52A7A6775C7ED28FF07EE7D904BF85A3AE84FD4FF916757FE914F5551705C0CBA3C9869AA3DC47724DA1F23BE0BAFD6F54D68FE2751B9AE7677938D3F93D53AC50CEE3A0F6852AF6639A8C3C7DF29755756CDF4B517786B4D705BF7093E3F5DBDC4B772921E53283F7117687BFBCE23CB5575B79FFEE78774175ACBF4A551BCB79A0F4F51C7F4DB23F97DC1F85683
	3822757DD57793DDD4B44F3154DFDC343E3F97B53A24E8FF7D407E6EBE2769AF1A67EA637EBA4F93DDC6B437C336EE1C3A71891A3C43F84E477E085E5F5F739A5D56E1EABF4338BD4C43747D4D4CA53A76E1FC6DC063C3FAFFFFF21E2697DDFFA8EFDCC0FFB579C417B80C2FC1283FD9817DCD2BA63A4706F1FF4E9A4665D8FE295ECFC76CA33A3D17F23A7ACB8369B85F469ACD0ECB79FA8779D23FFE3E4B76EBF2287D45297D2586707D0DC6172952AD8E2263FCEFBBC0F425177135957145FE11AF0FD5AFEE43
	78494E9B4ED83AAC3ECA3797F2C0F575273A730557D7962B745BAF23F304FF14E22C32C1398FA076B20A35BFFD054E91E2BB92050CE7F79505BA9BC93A0C0FF77EE53A3D6222C3CBF8AC3C3395F9168CEFB4D58C27B8B847265F4B9CF23BA52B1DED235498A50B7AD84CF1DEF199BCDB6D32DBB9864952C7712CDF447162BECBC52B7B4E37593E93E3F59570F5G0C9F4E621C02B98AE39B17533A07640C6919A0B40F9A1DE78AA1674261DC3E629F6864BC17D34879DD51C57C8EC23D8F0067099E85F3EFD01ED0
	F3C819974722345B5DFCACAF9CBA28721AB7F51C10DAB4B24F686C26FDD2DCDE32BA06853A31D45D5412FF94000F67EBAF0DE76B88082BA3E8BCB3B51E5CEF969EC13D5A5D10A56C362D5B3E21F30BA91B75E3A9368CB39C4B48E75B7262C62867EAA37CE3C93E3AA5BF7DEA9A4B8A9F0D2578F21857GBC67FF645567FC9F6013G6683E4DECE639D6DB3529F6FB7C36BF7B150D70D243337528378E46B8E0164697B974993B5AFC5A8DBFD0521BCACDF0D64F2440E744B531750129F13C07244F4935FCC20A93F
	026459D02E17478C78D800D400F9D710BC9BFB50375BC429D3D464EE5E5F719E2DBF20AB7FA151BEE6C0DB5ACB34E6209B79C362BD692C1EF7BA60F3G4A2F04FD5915545F585E503AFCCA6B77A850C781F07C28BE422782EE11565762BE72A3DB8C52457ACAF57C3746F0DB5647907D16FEC68F0BDA5E2F351F7ACD821ACC0DAE239F77A6DE0B6C0470250E2D7C9C5116DF857965AA9A5B539AFEA46052B57C3567B53FGDC555554771F4F07165FAAD09B87500F3F1A7ADA17CB788C4015DEC3FD1D4B555B0663
	A6658E3457B3DA7BBAE89BFD8D75FBC39E6153G172D757BD6FE201E44FC225B9E5BE83245EA7322FA201EFED9CD67FE71C07718C6FBE89F46DB51094BBD3368D6BE4623AC2FA4F728F128F39F6E6FE03D6FD6E40FD591ED8D1283967D0AA2B99B6DEEFB0B5DE1D73AD59E537751F8160FEAB4550EA29E9BEA90EFF75A95BBC4129EF16BB6CEFF3E06687B00B6E594493FE7BF0DB58BF0DB2E55645ABF78D89F5C0FEBD10B4BA5CB3E865169B69C2ABA56658708778968BFD66361BEE0BC56859A7DBC20AD380E4E
	B05EDE99FC76DBF31D7FEC2A7E3A0167D47CCC74FE1EDB2F63E722799AFE9E43ABBABC67EFAEA57E51D03770BABA475BE34739DD9787EBF200A800B800D40085F114D38BDB7788E14EC90FB995C83996C7C340C6104EABDB11AF6C5D5472FD2D7A984A7570B78575109DB774F3B95EEE9FEB377C7A213763B1FB5BB77A77B385ABBF4D43B19A456C9A356F3A6B7DB11B6BAE738C46251100CF84188530GA067FA52DBED5B76F0FA23B3E3D5E1DC26EB3A423D0BA89B9DA8AFEFF7D358F6B5C16D389C558EDC871A
	214E85B09CE0BC40DC00CC00629BC81E2B5BBE8AA74FA1BAA8B0CB5A4F43411BFD9A6FC9EFE4F2F604EDD7F103111C37F5221CFB214E8C60G988E1084B097A0CB13B333EDDFB8B90F60618A4848449AA8A09F679ADFE4723D91365D727840F66ABADD887FDE903353911BDD11E41E5B5FD1024FC6F0DD31857A5F8B405776BDDEFAF7D89FCFEB7B27589AF0B81493GD263A91EBD9D214C0B42361B9FEFE4539D925AF4A154955C88EB92003DGA7GFCG71B712CD5FEF8F6B3B5AEB1821786E355D1149B9BEEC
	3B149B0DEC3B7E78EE835B52FA005B77933554606964A7E06CCCC93573863D1D67810FEC0032B10D266DE3484E291BE88FD7BB066C6CE56F7B7A209C8B10B4066CEC6909CCFEA9EC3B74B1C66DB674C646EFEB58F699E30C743D21726DA14DA52EE42E632B1C41F3A99B7AAE9B8BBA9ECBBA7E75D91ACB35E3C94751B253B114E3811246120E1F09D066BF04ED17BE56285D8DFD11713BA5EC3B0C31C6BAFE34FF23E0F82EE006A56AA1D93488DCE70F78D0E7F8C610BD1676567D9F6B4F9052D674075CDBB7A5F0
	5E3E84FF7EDE50E9AE62E33777C7A633B7EC3B548423F62F1F0B0C5F4E306D669B72DB98A13F7F8D5BEE21A13F787311711B9C361DF013D1BB3B90217D4237B36437A9B7B2FE6F06ED3753105F6FA36477D258F6C78D795D1497993F5B4236EBB66457119F993F6E306DFC067C1E2C8EEE9793C4E74C6F79816DB68715138C795D3DAFB279FE9436DD3AA13FE1B511713BA6EC3B8CC3FE9F6E0FCC1F1F8CE8972C4FECC3FE2B8EC4A6DFC558F64B4799357B4F8879E505ED37A5089F3FEBF3001D2357C0DDBDG47
	EDDC0DB8854AA347515AE0EEA91E65A4EB651F31F23636BE7B26B478EECAF2903F6F821C07FBG0F7C6F29FF843EEE2F3107FE17FD58EE7CAE7B185D78DD769776159C8FFD68EFF4D17D5776415FF51F369B3D6B5E4464FE1C1D39499E27E26F9499DB3E4691FDF83781F697DFF91CFC6B011C5D417BFF0176B3F73B500EF347513E658F765D4240FD8B5F41D1A7E651A2FF515A2AEDDA9ADC2215461CF5A6347EDEBCE32C3FBF1EB1565F4BE78657DFD1C7A87DF19DBD23607D0F816F7E7F813BAE015EFDBF5C35
	D42529D7247ADDDA5F350E50F2FC66B01663CB872E995D12C7360A41E3EDF214E9F57ECD5EB1C56C4461F09A8BEB2FD864B6F1002C6D2CBD2F8B557E2C835724ADF61BDDD65F42F399E33DA46391C57A7CC32D4F3BBBC34BB82B53D84687BBC34B78E8E7F8999F6F8CAD632FBA434B783B4ED0B6673256797432323B1AECCD5F2250DEB3F39C6D03AEE76F394AEFEEB4D581545DCC316C7C2067BD361BC31D117E133D43715D4C4FC2F3B57CA9060F56615573F4AF5D370E013A6C1B69CCF7FD3709FC32D516BA41
	AB553B92D2E8FB9D140C6D7531945ADE1FCB616D55A8053657C9A93C3DDAA5E31FFC34DBEF275D826D51EE2844739503BDDA034BE1D7F0774B773EAB56996DFD1B46B71A9C63E96F5B3281ED0EB60D9ECFB637EEE4779B217C98C056F832793D1B8633F9418458D7CF8834BB376DF36C3CE27B04E01BE7B37C5E8941B66FF7134DEB21AEF18259BC2EA750663BD5BDFD592B57931E0BF37973576865DFBE115F155D8B4F3A09A47FC3CE125FB61164FF41456E73C0B985E05EC412BF736CE072E7CE8C6573B96CAC
	A1E7E2307C39CC4E02C941720F10C97E9228D3A6117C1B7B82655F234A7F33BE3D7CD5822D85667B0257828BA6850EB1EB12717AC3C88C4417C2F9ABC00D8E4FF3638BE7713C3D9E708E00E800F80034C44A15452E306FA90E788C5FD3F059927A83E52ED2717FBA271779BD01E25BEB6783695FD3717FF1DECF7F3EC61FA084523F2F624D39FA7A8FB47AADF901749F287885F9FA7A3D9A7D387CC07A3DEA5EFDE8AD46A99724F2514D8E2EEBAD0AC5ED33EE5FB2DDDF3BF6D2DF0B82713B881FD6934863C3956F
	5A9FC03F1B680F9D88406FA17C1A3FAF8F68E757CE0E7F67CA3C576730F4CB9E85762837307BCFAB356FF2405659G65005FF98B1FE3BCCECD736238DAE5CBA704A736G5AB2195D59DACDEDE300FEBCC0BAC0A6C041E472751433B816CF4D1616965106A1ED977727FBDD98EB355C262ED3383F37A9417353B1596F2FD113C34D4F17D90E0B1D9C7C3D45B216FB92A787FFEFF1D5973D4BCC033A0ADBE9CFF02D1776845137D24CB80ED7D4547159F43FFF6F8DF5CF7D7B5B8BAC99B07F3FCF60717313F594BF13
	EF257869D90F7AD988E5A189F2C2926922EDC328783993E8EA13826D5384E5EE07A7D8BEF2A4855B67E9060FCA8A360FFDA359A7966AF21248BE0FEDA27B1C18C238AFB6A3EEB814932650B87F476D37531FF43FAF8979ED4177E733788B4A034F4BF70BB186991D71EA33CD336F3EB5F4B61EB2056C5B3496751B85650229B06F27125EFE6C8C36EF55D43FCD0FCE8D346F0929E45FD6B657DDD3035F1B3B98BEEAEA70FB739C973DB70F013A6C29E44B174E12FD1BEE23B17DC5765B2FC8E74B9879425B72168B
	4A034B7C82BB070F86F993EEA3BD6C6FE65FECC2F91E06FB095DF7FE924A0BB45CE1AB692BA019F0FDD61A8FBBB51C540138DAA8B7A5D3DE7F5359506B6CDC59F81DDDA49B6FE1DF11875F432E11C36DE738BF442E2475128346B7BE19724F6FAA835EE5D4DA3CCAF575C7925E432E63490C7BF886BB5F4176994964634BD6E11F255340C781EA2751BB8E5FDB740EA3E962B8E17EEF0D6FC9BEE96E4FBBB10D7BFB54B45D1D3DF7DA72B94F265544B38D6A3327A16E52557ABBD5685F338B8421921E3CBEB5209E
	F5025FE3786B7FAB209E477B6BFCFDFDF9293E3ED0571E4B76481BA45B62C71285479D51A13F0F2D391D4B542CBEABFEFCFD6E70387AA55CDF4E31FDE49C54A55DCEBE74E78F795A7C5BE9EE9632FC39984AE55389D742F0F8AF966F82F35CF5DDC673E8716D7EF8026D3F5BDCCDB66421BFBB7BAE5F0E9D1D0EBA18622D980426CA15010F27EE5D00B3DC1D0C630364CD1DCE3174D916FB67CD8F0E25AFB2FC467460D85A3B1EE2E996545527103D4E31FDE18A149F53F08FEFA09BAE54F0FFE13852BB007F9DE4
	2FDFEDC45CC1A8B7EB38058C077745711E207A5EF963D063A37623AF9F3CA33C3DB90F61C36691EC5F211C1B465D4176371B482E0A4138062BB83E2EED5D0CEBE2D8905BBBBD1D8D769ED1A83B13691B2DC31E845AACCDE787578C365FBCF1E728756CBC161FDDF7862F171EE07868BB0357CB275652FAA98E6A965FC9367D2D134E3D1B6FA25CE917364655F06799AE914AE9F751589DE7591929D66EE2E57C5EC24D4B2EC8F3A376B9B45B2FBB9BA90F21581E4F69D5B259BE53E0DCB9F769ED3F349B6329D736
	389A14EE0778B57BC4DE357F6B5D344EDA1ECAFBA0F3B73B83AF49E21BACF91CB622F5B05AF2205B2A512E3222FEE5496B66B4DB2D34AEBF8AB4FC2C5FD8AB861127A9150F15371F405ABBG6753F8DC4A5681224D2E6071B8273B3103725878D4326D688177A707E62F44885BBD752D5B71791041F2FCEAEA70394CEF99BEBDB578DC26E4952DB366C3DD555DF4DFF3019B7733F1D0CE8118871089D0F08F6C8D6F2175522DB2EC6387BF87B1CB2EBAD8D0B9F4CBA075CE5E6AEFAB9F4F67330BA81F275CB3D9C8
	3D34B664FD0C2AFB386D4D6AB3F55E7539786472F5F8D03EE828CBGC88748GD884D0BC0364FBD1D66FD562EB10C30AE23136332FB93417AAF6516BC763E1G7729D14A403305A46D7EDB798CFE36D0854F23B3F86E60B137340BFC39F94640F9274C504F3B8FCE606F401BF6D4F74DA7D85720D8940FDB3B62C26FBD3CB83643A25E23A51DFAC552E9792DB78B652F9D4CDB99C2274355B1244C702F7FD81945CC6BBA0A2D8BE7D09C7DD599455665E95A3A290CE26B4EB41ADF7F3D9E67FA0DD67EA3AB639DC1
	DE3EE8FD24F18F7B9C5A1C9B9DB10F2144D655B7B660370D12D3B4CB9EEB7B911F4B62347197F3BC765C3601E226B90DE25CD39BAA8669B7B09E2F590873C1126DE0E17EBD926F77EF9BA9265952A876FD388C574EC63779F87D6707D0AF7C6B2B5D6A3EE275E6FA1F9227757359E6FCAA12CB7527B3A5FAFF4A68EE8C69CF73D55BACCE8B74272AFB717F6B2C6897B4393EF1637DFE27682D20EDCFE52D9D269D7E63092D6F6D5CDE316B3D1D5BD47E89DE7D1D6B3D4BAEB67C469641F7AF67BB121EAFDF1F4BFD
	746BB564376977EA6F3E57A2EE910EED2636AEE4B83C63085F2A28E7E2EB8773A3EC7B2A5ACFA43E46F9748F4A0356FD113EA767B16BD509E2D669CC9E3336437360CCAEEB53CC2469F2158D528F37DD69FA26CBCB0B592EB8C4EE5BB1677C369D7DA62EA182EFBA52D9CD35CC7E16B0137C6D72B57A98267AF2B77B66C476287D97667A7BFF3185671C66B707A507A4D74A220D6A7FEEC562BD9A3D570A7C9AA0FA37EB3E38B54F5F1F2DE3D3E8DFC4D7FCFF1BC65B63443EDAAD8E377FCC77E99C0E7A7BB95D6F
	352BAE7C794D0E8B4A03674D275C214FD1963A0D4FD11EF79B1F23E43B873FAB31589D7A7F8264AB7A7F8B4073FE8E7EF3AFE1817AB3C059FD6053G876F231CFF15723A40F23EC13EBFE2EB43772578DFF894622FF8C24B5A6B897F3FFB22BD389EEE1194C56A447E89BF42037602184A109CF72587F5A45B5B5A95CE2A62C731F38807584A50FCDD7718C26B3A93A0DB6CFD74EE305CC3F9E05EFDF4AE74A97BFFC885339ACD65GD5330836260B66DE5DAC1AFBCA5792ED3C245332428AF8C683CD82C0BAC086
	40A200625950A7C055EC526F1BDD786DC023654C409BA6006487D9CC4732456ED4F89C9A293D732D1F4D63D0BBBC7BE6733B65BC8EE5178D9607789CFEFC09FF8EDF738E66C97F5B65DD74ED56860AA33CDC3B917FD7086EC839525F26327148D766E3078F551DEE282FBBE65666D859E67D5AB6964619B8FB70D84F75B82388751886348BG9683145E8F7BA10003GE6G477D24470E2EF7875123B6C43FAA555C7813637A5CD8B25A11C77B0160B18D3FDF7D06747E606F11F6AFC53F188B78CCEDBC667E4146
	D3AFCBAED1D63AB9167DB787F52F69DCD3B108DE8257B667FCE81BFACB1BEA1BA75C7E7549AEF5BF733EC4FEDFB02751D4B307FC791CC4FE2F4CA13FCFE234C9D0CE874818C36721F3CF6A4F8CAB5E7D42704CB067E4CFDE769CF5FF196E57D1D2F355D26E87FF1775FED087B44D69037B815725109FBA269465070FA9A579FCCF5BA61A3BDD6AFD0CB579687CD2EB2BDB54E257067C928DF74CDDAF396D38E064E37F6500770EC34BAF8BA3EC77D7CFE47B5D19ACF6A969417B5D9FB1FCD4FA70FE375EC37EB192
	6A9625531A6DD040B7ED668740EF9E980A2D5EA88CED2BF78A435BEACF21F17C7F30F0E07CBFD0E89C7FEB8B7571DF3D1B2876ABEB975F543B0905684BFCEF7A394589F3C956F413320EF453873227BCC0F3E771BA1ABBD98F505CF9278CE937BC88F38C407CA06D6DF6307D01834ABF4D27FDE19543C5838EBF5CD76F139505565B23C5617576F811315EB20A866A6D29A2E33DBDD3B4D0EFD92CDFD9EC031920575B8BC5C6FA6371D046A6144F0BB1DF9846C1D8C1FC8AF9640B16B322D5B1DB5A34B69F307F51
	232DF1F8F8A41BAF45750A761DAF5B52B5G21330D7BED0D9459FC7BB442F617FC6BF6FCBED630771B898F864F53ED8C1F74E070BC3DDD0B6329D0D77EA3BA177A018973FC94146381E681ACG4882A818CB39A8FD69E0EB8C7E1A4C1F98390FB64A64377682725B63B2796DDD79A4FF3AC750F471286EF72367422F5D258B4A034B5C54B5F07E1E6CA2BD9C5374F04F00F7B9C313E1EE046DF24BA2733BB1ACCE154D8D763BC9BCFE4D8D763B1DE564F7BB21AEF6AE3DF7353C05F8F59EFEED37A96D9CDF6EFB45
	0FBFAAE20C51467C164B6F9FBBF43FD3258B6FABBF3D20BC546F34CD21E378752670B1FC1C49B806A71A864670A9A66398BE5D240F616A372FE5FEDDCF517DFE3F6C426B7D0B8B4A037F2F6A675ABF8187CD7A3DD0681605D5834FA08E7D7097CFA41D2EAD4D93720E89CB5A19D39E938A3D6835BE7443EB81F6AB420AA3E7BDAC21D460C9B66C1BE5615540D3125C896BAEA939F20FA7CFA87ED8D852EAF1385AAD50937B178AF50229055983B7EC8EC5A8294336BB206D8E56F6DF1D10E7F5AB42C6BF5E0AF8DE
	5B6032D8C5A0D9EA33489D2C073342B92160383034D5EEEBE18C94A16F78474701ADABE17512BAE119520E1B9A7C67A20A304E5FF5BD06F235E29F93D2F879E5C1F031BFB7B646BFDC6F8A0AE04233268A7550DC7DBF8D67757B221CE15610E706B98F73F56172C70277C59E765E68A860ED0FD0AEFA608C7EDF49D06B0106CE0B4321D89C9D071CB6DC9BFC542D087E330D1E8E6C5FCDB4EAA52F3B288347FDC657667FGD0CB8788E972B4771B9CGGA4DDGGD0CB818294G94G88G88G560171B4E972B4
	771B9CGGA4DDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG559CGGGG
**end of data**/
}
}