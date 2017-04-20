/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math.gui;


/**
 * Insert the type's description here.
 * Creation date: (2/5/2001 12:58:17 PM)
 * @author: Ion Moraru
 */
public class MathViewerPanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;
	private MathDescPanel ivjMathDescPanel1 = null;
	private javax.swing.JPanel ivjVCMLPage = null;
	private cbit.vcell.math.MathDescription fieldMathDescription = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.math.MathDescription ivjmathDescription1 = null;
	private javax.swing.JEditorPane ivjJEditorPane1 = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MathViewerPanel.this && (evt.getPropertyName().equals("mathDescription"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == MathViewerPanel.this.getMathDescPanel1() && (evt.getPropertyName().equals("mathDescription"))) 
				connPtoP2SetSource();
		};
	};

public MathViewerPanel() {
	super();
	initialize();
}

/**
 * connEtoM2:  (mathDescription1.this --> JEditorPane1.text)
 * @param value cbit.vcell.math.MathDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.math.MathDescription value) {
	try {
		getJEditorPane1().setText(getVCMDL(getmathDescription1()));
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (MathViewerPanel.mathDescription <--> mathDescription1.this)
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
 * connPtoP1SetTarget:  (MathViewerPanel.mathDescription <--> mathDescription1.this)
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
 * connPtoP2SetSource:  (mathDescription1.this <--> MathDescPanel1.mathDescription)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setmathDescription1(getMathDescPanel1().getMathDescription());
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
 * connPtoP2SetTarget:  (mathDescription1.this <--> MathDescPanel1.mathDescription)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getmathDescription1() != null)) {
				getMathDescPanel1().setMathDescription(getmathDescription1());
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
 * Return the JEditorPane1 property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getJEditorPane1() {
	if (ivjJEditorPane1 == null) {
		try {
			ivjJEditorPane1 = new javax.swing.JEditorPane();
			ivjJEditorPane1.setName("JEditorPane1");
			ivjJEditorPane1.setAutoscrolls(false);
			ivjJEditorPane1.setBounds(0, 0, 160, 120);
			ivjJEditorPane1.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJEditorPane1;
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
			getJScrollPane1().setViewportView(getJEditorPane1());
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
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.insertTab("Equations", null, getMathDescPanel1(), null, 0);
			ivjJTabbedPane1.insertTab("VCMDL", null, getVCMLPage(), null, 1);
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
 * Return the MathDescPanel1 property value.
 * @return cbit.vcell.math.gui.MathDescPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathDescPanel getMathDescPanel1() {
	if (ivjMathDescPanel1 == null) {
		try {
			ivjMathDescPanel1 = new cbit.vcell.math.gui.MathDescPanel();
			ivjMathDescPanel1.setName("MathDescPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathDescPanel1;
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
 * Comment
 */
public java.lang.String getVCMDL(cbit.vcell.math.MathDescription mathDescription) throws cbit.vcell.math.MathException {
	return mathDescription == null ? "" : mathDescription.getVCML();
}


/**
 * Return the VCMLPage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getVCMLPage() {
	if (ivjVCMLPage == null) {
		try {
			ivjVCMLPage = new javax.swing.JPanel();
			ivjVCMLPage.setName("VCMLPage");
			ivjVCMLPage.setLayout(new java.awt.BorderLayout());
			getVCMLPage().add(getJScrollPane1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVCMLPage;
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
	getMathDescPanel1().addPropertyChangeListener(ivjEventHandler);
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
		setName("MathViewerPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(433, 362);
		add(getJTabbedPane1(), "Center");
		initConnections();
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
		MathViewerPanel aMathViewerPanel;
		aMathViewerPanel = new MathViewerPanel();
		frame.setContentPane(aMathViewerPanel);
		frame.setSize(aMathViewerPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Sets the mathDescription property (cbit.vcell.math.MathDescription) value.
 * @param mathDescription The new value for the property.
 * @see #getMathDescription
 */
public void setMathDescription(cbit.vcell.math.MathDescription mathDescription) {
	cbit.vcell.math.MathDescription oldValue = fieldMathDescription;
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
			ivjmathDescription1 = newValue;
			connPtoP1SetSource();
			connPtoP2SetTarget();
			connEtoM2(ivjmathDescription1);
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

}
