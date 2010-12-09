package cbit.vcell.model.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import cbit.vcell.model.SimpleReaction;

/**
 * Insert the type's description here.
 * Creation date: (7/25/2002 3:03:15 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SimpleReactionPanelDialog extends javax.swing.JDialog {
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private SimpleReactionPanel ivjSimpleReactionPanel = null;
	private SimpleReaction fieldSimpleReaction = null;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimpleReaction ivjsimpleReaction1 = null;
	private JButton closeButton = new JButton("Close");

class IvjEventHandler implements java.beans.PropertyChangeListener, ActionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SimpleReactionPanelDialog.this && (evt.getPropertyName().equals("simpleReaction"))) 
				connPtoP1SetTarget();
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == closeButton) {
				SimpleReactionPanelDialog.this.dispose();
			}
			
		};
	};
/**
 * SimpleReactionPanelDialog constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public SimpleReactionPanelDialog(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
	initialize();
}
/**
 * SimpleReactionPanelDialog constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public SimpleReactionPanelDialog(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 9:57:55 PM)
 */
public void cleanupOnClose() {
	getSimpleReactionPanel().cleanupOnClose();
}
/**
 * connEtoM1:  (simpleReaction1.this --> SimpleReactionPanel.simpleReaction)
 * @param value cbit.vcell.model.SimpleReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(SimpleReaction value) {
	try {
		// user code begin {1}
		// user code end
		if ((getsimpleReaction1() != null)) {
			getSimpleReactionPanel().setSimpleReaction(getsimpleReaction1());
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
 * connPtoP1SetSource:  (SimpleReactionPanelDialog.simpleReaction <--> simpleReaction1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getsimpleReaction1() != null)) {
				this.setSimpleReaction(getsimpleReaction1());
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
 * connPtoP1SetTarget:  (SimpleReactionPanelDialog.simpleReaction <--> simpleReaction1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setsimpleReaction1(this.getSimpleReaction());
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
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.BorderLayout());
			getJDialogContentPane().add(getSimpleReactionPanel(), "Center");
			JPanel panel = new JPanel();
			panel.add(closeButton);
			getJDialogContentPane().add(panel, "South");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}
/**
 * Gets the simpleReaction property (cbit.vcell.model.SimpleReaction) value.
 * @return The simpleReaction property value.
 * @see #setSimpleReaction
 */
public SimpleReaction getSimpleReaction() {
	return fieldSimpleReaction;
}
/**
 * Return the simpleReaction1 property value.
 * @return cbit.vcell.model.SimpleReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimpleReaction getsimpleReaction1() {
	// user code begin {1}
	// user code end
	return ivjsimpleReaction1;
}
/**
 * Return the SimpleReactionPanel property value.
 * @return cbit.vcell.model.gui.SimpleReactionPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimpleReactionPanel getSimpleReactionPanel() {
	if (ivjSimpleReactionPanel == null) {
		try {
			ivjSimpleReactionPanel = new SimpleReactionPanel();
			ivjSimpleReactionPanel.setName("SimpleReactionPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSimpleReactionPanel;
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
	closeButton.addActionListener(ivjEventHandler);
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
		setName("SimpleReactionPanelDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(576, 600);
		setContentPane(getJDialogContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Sets the simpleReaction property (cbit.vcell.model.SimpleReaction) value.
 * @param simpleReaction The new value for the property.
 * @see #getSimpleReaction
 */
public void setSimpleReaction(SimpleReaction simpleReaction) {
	SimpleReaction oldValue = fieldSimpleReaction;
	fieldSimpleReaction = simpleReaction;
	firePropertyChange("simpleReaction", oldValue, simpleReaction);
}
/**
 * Set the simpleReaction1 to a new value.
 * @param newValue cbit.vcell.model.SimpleReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimpleReaction1(SimpleReaction newValue) {
	if (ivjsimpleReaction1 != newValue) {
		try {
			SimpleReaction oldValue = getsimpleReaction1();
			ivjsimpleReaction1 = newValue;
			connPtoP1SetSource();
			connEtoM1(ivjsimpleReaction1);
			firePropertyChange("simpleReaction", oldValue, newValue);
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
