package cbit.vcell.client.desktop.mathmodel;

import javax.swing.JMenu;

import cbit.vcell.math.MathDescription;
import cbit.vcell.math.gui.MathDescEditor;
import cbit.vcell.mathmodel.MathModel;

/**
 * Insert the type's description here.
 * Creation date: (5/20/2004 3:35:42 PM)
 * @author: Anuradha Lakshminarayana
 */
public class VCMLEditorPanel extends javax.swing.JPanel {
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private MathDescEditor ivjmathDescEditor = null;
	private MathModel fieldMathModel = new MathModel(null);
	private MathModel ivjmathModel1 = null;
	private javax.swing.JTextPane ivjMathWarningTextPane = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == VCMLEditorPanel.this && (evt.getPropertyName().equals("mathModel"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == VCMLEditorPanel.this.getmathModel1() && (evt.getPropertyName().equals("mathDescription"))) 
				connEtoM2(evt);
			if (evt.getSource() == VCMLEditorPanel.this.getmathDescEditor() && (evt.getPropertyName().equals("mathDescription"))) 
				connEtoM3(evt);
			if (evt.getSource() == VCMLEditorPanel.this.getmathModel1() && (evt.getPropertyName().equals("mathDescription"))) 
				connEtoC2(evt);
		};
	};

/**
 * VCMLEditorPanel constructor comment.
 */
public VCMLEditorPanel() {
	super();
	initialize();
}

/**
 * connEtoC1:  (mathModel1.this --> VCMLEditorPanel.updateWarningText(Lcbit.vcell.math.MathDescription;)V)
 * @param value cbit.vcell.mathmodel.MathModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(MathModel value) {
	try {
		// user code begin {1}
		// user code end
		if ((getmathModel1() != null)) {
			this.updateWarningText(getmathModel1().getMathDescription());
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
 * connEtoC2:  (mathModel1.mathDescription --> VCMLEditorPanel.updateWarningText(Lcbit.vcell.math.MathDescription;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateWarningText(getmathModel1().getMathDescription());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (mathModel1.this --> mathDescEditor.mathDescription)
 * @param value cbit.vcell.mathmodel.MathModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(MathModel value) {
	try {
		// user code begin {1}
		// user code end
		if ((getmathModel1() != null)) {
			getmathDescEditor().setMathDescription(getmathModel1().getMathDescription());
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
 * connEtoM2:  (mathModel1.mathDescription --> mathDescEditor.mathDescription)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmathDescEditor().setMathDescription(getmathModel1().getMathDescription());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (mathDescEditor.mathDescription --> mathModel1.mathDescription)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmathModel1().setMathDescription(getmathDescEditor().getMathDescription());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (VCMLEditorPanel.mathDescription <--> mathDescription1.this)
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
 * connPtoP1SetTarget:  (VCMLEditorPanel.mathDescription <--> mathDescription1.this)
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
 * Return the mathDescEditor property value.
 * @return cbit.vcell.math.gui.MathDescEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathDescEditor getmathDescEditor() {
	if (ivjmathDescEditor == null) {
		try {
			ivjmathDescEditor = new MathDescEditor();
			ivjmathDescEditor.setName("mathDescEditor");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmathDescEditor;
}


/**
 * Gets the mathModel property (cbit.vcell.mathmodel.MathModel) value.
 * @return The mathModel property value.
 * @see #setMathModel
 */
public MathModel getMathModel() {
	return fieldMathModel;
}


/**
 * Return the mathModel1 property value.
 * @return cbit.vcell.mathmodel.MathModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathModel getmathModel1() {
	// user code begin {1}
	// user code end
	return ivjmathModel1;
}


/**
 * Return the MathWarningTextPane property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getMathWarningTextPane() {
	if (ivjMathWarningTextPane == null) {
		try {
			ivjMathWarningTextPane = new javax.swing.JTextPane();
			ivjMathWarningTextPane.setName("MathWarningTextPane");
			ivjMathWarningTextPane.setForeground(java.awt.Color.red);
			ivjMathWarningTextPane.setPreferredSize(new java.awt.Dimension(11, 50));
			ivjMathWarningTextPane.setFont(new java.awt.Font("Arial", 3, 14));
			ivjMathWarningTextPane.setMinimumSize(new java.awt.Dimension(11, 50));
			ivjMathWarningTextPane.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathWarningTextPane;
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
	getmathDescEditor().addPropertyChangeListener(ivjEventHandler);
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
		setName("VCMLEditorPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(354, 336);
		add(getmathDescEditor(), "Center");
		add(getMathWarningTextPane(), "South");
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
		VCMLEditorPanel aVCMLEditorPanel;
		aVCMLEditorPanel = new VCMLEditorPanel();
		frame.setContentPane(aVCMLEditorPanel);
		frame.setSize(aVCMLEditorPanel.getSize());
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
public void setMathModel(MathModel mathModel) {
	MathModel oldValue = fieldMathModel;
	fieldMathModel = mathModel;
	firePropertyChange("mathModel", oldValue, mathModel);
}


/**
 * Set the mathModel1 to a new value.
 * @param newValue cbit.vcell.mathmodel.MathModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmathModel1(MathModel newValue) {
	if (ivjmathModel1 != newValue) {
		try {
			MathModel oldValue = getmathModel1();
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
			connEtoC1(ivjmathModel1);
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

/**
 * Comment
 */
public void updateWarningText(MathDescription argMathDescription) {
	if (argMathDescription.isValid()){
		getMathWarningTextPane().setText("");
	}else{
		getMathWarningTextPane().setText(argMathDescription.getWarning());
	}
}

public boolean hasUnappliedChanges() {
	return getmathDescEditor().hasUnappliedChanges();
}

public JMenu getEditMenu() {
	return getmathDescEditor().getEditMenu();
}
}