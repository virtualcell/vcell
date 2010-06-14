package cbit.vcell.model.gui;

import cbit.vcell.model.Kinetics;
/**
 * Insert the type's description here.
 * Creation date: (6/24/2002 11:34:37 AM)
 * @author: Anuradha Lakshminarayana
 */
public class KineticsTypeTemplatePanel extends javax.swing.JPanel {
	private ParameterPanel ivjParameterPanel = null;
	private Kinetics fieldKinetics = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == KineticsTypeTemplatePanel.this && (evt.getPropertyName().equals("kinetics"))) 
				connPtoP1SetTarget();
		};
	};
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private Kinetics ivjkinetics1 = null;
/**
 * KineticsTypeTemplatePanel constructor comment.
 */
public KineticsTypeTemplatePanel() {
	super();
	initialize();
}
/**
 * KineticsTypeTemplatePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public KineticsTypeTemplatePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * KineticsTypeTemplatePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public KineticsTypeTemplatePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * KineticsTypeTemplatePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public KineticsTypeTemplatePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 10:10:52 PM)
 */
public void cleanupOnClose() {
	getParameterPanel().cleanupOnClose();
}
/**
 * connEtoM1:  (kinetics1.this --> ParameterPanel.kinetics)
 * @param value cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(Kinetics value) {
	try {
		// user code begin {1}
		// user code end
		//if ((getkinetics1() != null)) {
			getParameterPanel().setKinetics(getkinetics1());
		//}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetSource:  (KineticsTypeTemplatePanel.kinetics <--> kinetics1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getkinetics1() != null)) {
				this.setKinetics(getkinetics1());
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
 * connPtoP1SetTarget:  (KineticsTypeTemplatePanel.kinetics <--> kinetics1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setkinetics1(this.getKinetics());
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
 * Gets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @return The kinetics property value.
 * @see #setKinetics
 */
public Kinetics getKinetics() {
	return fieldKinetics;
}
/**
 * Return the kinetics1 property value.
 * @return cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Kinetics getkinetics1() {
	// user code begin {1}
	// user code end
	return ivjkinetics1;
}
/**
 * Return the ParameterPanel property value.
 * @return cbit.vcell.model.gui.ParameterPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ParameterPanel getParameterPanel() {
	if (ivjParameterPanel == null) {
		try {
			ivjParameterPanel = new ParameterPanel();
			ivjParameterPanel.setName("ParameterPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjParameterPanel;
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
		setName("KineticsTypeTemplatePanel");
		setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints constraintsParameterPanel = new java.awt.GridBagConstraints();
		constraintsParameterPanel.gridx = 0; constraintsParameterPanel.gridy = 0;
		constraintsParameterPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsParameterPanel.weightx = 1.0;
		constraintsParameterPanel.weighty = 1.0;
		add(getParameterPanel(), constraintsParameterPanel);
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
		KineticsTypeTemplatePanel aKineticsTypeTemplatePanel;
		aKineticsTypeTemplatePanel = new KineticsTypeTemplatePanel();
		frame.setContentPane(aKineticsTypeTemplatePanel);
		frame.setSize(aKineticsTypeTemplatePanel.getSize());
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
 * Sets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @param kinetics The new value for the property.
 * @see #getKinetics
 */
public void setKinetics(Kinetics kinetics) {
	Kinetics oldValue = fieldKinetics;
	fieldKinetics = kinetics;
	firePropertyChange("kinetics", oldValue, kinetics);
}
/**
 * Set the kinetics1 to a new value.
 * @param newValue cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setkinetics1(Kinetics newValue) {
	if (ivjkinetics1 != newValue) {
		try {
			Kinetics oldValue = getkinetics1();
			ivjkinetics1 = newValue;
			connPtoP1SetSource();
			connEtoM1(ivjkinetics1);
			firePropertyChange("kinetics", oldValue, newValue);
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
