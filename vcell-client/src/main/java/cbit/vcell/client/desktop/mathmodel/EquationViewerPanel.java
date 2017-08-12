/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.mathmodel;
/**
 * Insert the type's description here.
 * Creation date: (5/20/2004 3:41:12 PM)
 * @author: Anuradha Lakshminarayana
 */
@SuppressWarnings("serial")
public class EquationViewerPanel extends javax.swing.JPanel {
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.math.gui.MathDescPanel ivjmathDescPanel = null;
	private cbit.vcell.mathmodel.MathModel fieldMathModel = new cbit.vcell.mathmodel.MathModel(null);

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == EquationViewerPanel.this && (evt.getPropertyName().equals("mathModel"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == EquationViewerPanel.this.getmathModel1() && (evt.getPropertyName().equals("mathDescription"))) 
				connEtoM2(evt);
		};
	};
	private cbit.vcell.mathmodel.MathModel ivjmathModel1 = null;

/**
 * EquationViewerPanel constructor comment.
 */
public EquationViewerPanel() {
	super();
	initialize();
}

/**
 * EquationViewerPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public EquationViewerPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * EquationViewerPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public EquationViewerPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * EquationViewerPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public EquationViewerPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoM1:  (mathModel1.this --> mathDescPanel.mathDescription)
 * @param value cbit.vcell.mathmodel.MathModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.mathmodel.MathModel value) {
	try {
		// user code begin {1}
		// user code end
		if ((getmathModel1() != null)) {
			getmathDescPanel().setMathDescription(getmathModel1().getMathDescription());
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
 * connEtoM2:  (mathModel1.mathDescription --> mathDescPanel.mathDescription)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmathDescPanel().setMathDescription(getmathModel1().getMathDescription());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (EquationViewerPanel.mathDescription <--> mathDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getmathModel1() != null)) {
				this.setMathModel(getmathModel1());
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
 * connPtoP1SetTarget:  (EquationViewerPanel.mathDescription <--> mathDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setmathModel1(this.getMathModel());
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
 * Return the mathDescPanel property value.
 * @return cbit.vcell.math.gui.MathDescPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.math.gui.MathDescPanel getmathDescPanel() {
	if (ivjmathDescPanel == null) {
		try {
			ivjmathDescPanel = new cbit.vcell.math.gui.MathDescPanel();
			ivjmathDescPanel.setName("mathDescPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmathDescPanel;
}


/**
 * Gets the mathModel property (cbit.vcell.mathmodel.MathModel) value.
 * @return The mathModel property value.
 * @see #setMathModel
 */
public cbit.vcell.mathmodel.MathModel getMathModel() {
	return fieldMathModel;
}


/**
 * Return the mathModel1 property value.
 * @return cbit.vcell.mathmodel.MathModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mathmodel.MathModel getmathModel1() {
	// user code begin {1}
	// user code end
	return ivjmathModel1;
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
	this.addPropertyChangeListener(ivjEventHandler);
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
		setName("EquationViewerPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(303, 285);
		add(getmathDescPanel(), "Center");
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
		EquationViewerPanel aEquationViewerPanel;
		aEquationViewerPanel = new EquationViewerPanel();
		frame.setContentPane(aEquationViewerPanel);
		frame.setSize(aEquationViewerPanel.getSize());
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
 * Sets the mathModel property (cbit.vcell.mathmodel.MathModel) value.
 * @param mathModel The new value for the property.
 * @see #getMathModel
 */
public void setMathModel(cbit.vcell.mathmodel.MathModel mathModel) {
	cbit.vcell.mathmodel.MathModel oldValue = fieldMathModel;
	fieldMathModel = mathModel;
	firePropertyChange("mathModel", oldValue, mathModel);
}


/**
 * Set the mathModel1 to a new value.
 * @param newValue cbit.vcell.mathmodel.MathModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmathModel1(cbit.vcell.mathmodel.MathModel newValue) {
	if (ivjmathModel1 != newValue) {
		try {
			cbit.vcell.mathmodel.MathModel oldValue = getmathModel1();
			/* Stop listening for events from the current object */
			if (ivjmathModel1 != null) {
				ivjmathModel1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjmathModel1 = newValue;

			/* Listen for events from the new object */
			if (ivjmathModel1 != null) {
				ivjmathModel1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connEtoM1(ivjmathModel1);
			firePropertyChange("mathModel", oldValue, newValue);
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
