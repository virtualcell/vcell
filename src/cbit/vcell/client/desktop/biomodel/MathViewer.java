package cbit.vcell.client.desktop.biomodel;
import cbit.vcell.client.*;
import cbit.vcell.mapping.*;
import javax.swing.*;

import org.vcell.util.Issue;
/**
 * Insert the type's description here.
 * Creation date: (6/3/2004 10:10:46 PM)
 * @author: Ion Moraru
 */
public class MathViewer extends JPanel {
	private JButton ivjJButton1 = null;
	private cbit.vcell.math.gui.MathViewerPanel ivjMathViewerPanel1 = null;
	private cbit.vcell.mapping.SimulationContext fieldSimContext = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimulationContext ivjsimContext1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == MathViewer.this.getJButton1()) 
				connEtoC1(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MathViewer.this && (evt.getPropertyName().equals("simContext"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == MathViewer.this.getsimContext1() && (evt.getPropertyName().equals("mathDescription"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == MathViewer.this.getMathViewerPanel1() && (evt.getPropertyName().equals("mathDescription"))) 
				connPtoP2SetSource();
		};
	};

public MathViewer() {
	super();
	initialize();
}

/**
 * connEtoC1:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --> MathViewer.updateMath()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateMath();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (simContext1.this --> MathViewerPanel1.mathDescription)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.mapping.SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getsimContext1() != null)) {
			getMathViewerPanel1().setMathDescription(getsimContext1().getMathDescription());
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
 * connPtoP1SetSource:  (MathViewer.simContext <--> simContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getsimContext1() != null)) {
				this.setSimContext(getsimContext1());
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
 * connPtoP1SetTarget:  (MathViewer.simContext <--> simContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setsimContext1(this.getSimContext());
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
 * connPtoP2SetSource:  (simContext1.mathDescription <--> MathViewerPanel1.mathDescription)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getsimContext1() != null)) {
				getsimContext1().setMathDescription(getMathViewerPanel1().getMathDescription());
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
 * connPtoP2SetTarget:  (simContext1.mathDescription <--> MathViewerPanel1.mathDescription)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getsimContext1() != null)) {
				getMathViewerPanel1().setMathDescription(getsimContext1().getMathDescription());
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
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton1() {
	if (ivjJButton1 == null) {
		try {
			ivjJButton1 = new javax.swing.JButton();
			ivjJButton1.setName("JButton1");
			ivjJButton1.setText("Update Math");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton1;
}


/**
 * Return the MathViewerPanel1 property value.
 * @return cbit.vcell.math.gui.MathViewerPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.math.gui.MathViewerPanel getMathViewerPanel1() {
	if (ivjMathViewerPanel1 == null) {
		try {
			ivjMathViewerPanel1 = new cbit.vcell.math.gui.MathViewerPanel();
			ivjMathViewerPanel1.setName("MathViewerPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathViewerPanel1;
}


/**
 * Gets the simContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simContext property value.
 * @see #setSimContext
 */
public cbit.vcell.mapping.SimulationContext getSimContext() {
	return fieldSimContext;
}


/**
 * Return the simContext1 property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.SimulationContext getsimContext1() {
	// user code begin {1}
	// user code end
	return ivjsimContext1;
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
	getMathViewerPanel1().addPropertyChangeListener(ivjEventHandler);
	getJButton1().addActionListener(ivjEventHandler);
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
		setName("MathViewer");
		setLayout(new java.awt.GridBagLayout());
		setSize(622, 485);

		java.awt.GridBagConstraints constraintsMathViewerPanel1 = new java.awt.GridBagConstraints();
		constraintsMathViewerPanel1.gridx = 0; constraintsMathViewerPanel1.gridy = 0;
		constraintsMathViewerPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMathViewerPanel1.weightx = 1.0;
		constraintsMathViewerPanel1.weighty = 1.0;
		constraintsMathViewerPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMathViewerPanel1(), constraintsMathViewerPanel1);

		java.awt.GridBagConstraints constraintsJButton1 = new java.awt.GridBagConstraints();
		constraintsJButton1.gridx = 0; constraintsJButton1.gridy = 1;
		constraintsJButton1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJButton1(), constraintsJButton1);
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
		JFrame frame = new javax.swing.JFrame();
		MathViewer aMathViewer;
		aMathViewer = new MathViewer();
		frame.setContentPane(aMathViewer);
		frame.setSize(aMathViewer.getSize());
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
 * Sets the simContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simContext The new value for the property.
 * @see #getSimContext
 */
public void setSimContext(cbit.vcell.mapping.SimulationContext simContext) {
	cbit.vcell.mapping.SimulationContext oldValue = fieldSimContext;
	fieldSimContext = simContext;
	firePropertyChange("simContext", oldValue, simContext);
}


/**
 * Set the simContext1 to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimContext1(cbit.vcell.mapping.SimulationContext newValue) {
	if (ivjsimContext1 != newValue) {
		try {
			cbit.vcell.mapping.SimulationContext oldValue = getsimContext1();
			/* Stop listening for events from the current object */
			if (ivjsimContext1 != null) {
				ivjsimContext1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjsimContext1 = newValue;

			/* Listen for events from the new object */
			if (ivjsimContext1 != null) {
				ivjsimContext1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connPtoP2SetTarget();
			connEtoM1(ivjsimContext1);
			firePropertyChange("simContext", oldValue, newValue);
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
public void updateMath() {
	try {
		SimulationContext simContext = getSimContext();
		MathMapping mathMapping = new MathMapping(simContext);
		cbit.vcell.math.MathDescription mathDesc = mathMapping.getMathDescription();
		simContext.setMathDescription(mathDesc);
		//
		// inform user if any issues
		//
		org.vcell.util.Issue issues[] = mathMapping.getIssues();
		if (issues!=null && issues.length>0){
			StringBuffer messageBuffer = new StringBuffer("Issues encountered during Math Generation:\n");
			int issueCount = 0;
			for (int i = 0; i < issues.length; i++){
				if (issues[i].getSeverity()==Issue.SEVERITY_ERROR || issues[i].getSeverity()==Issue.SEVERITY_WARNING){
					messageBuffer.append(issues[i].toString()+"\n");
					issueCount++;
				}
			}
			if (issueCount>0){
				cbit.vcell.client.PopupGenerator.showWarningDialog(this,messageBuffer.toString(),new String[] { "Ok" }, "Ok");
			}
		}
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Failed to generate new Math:\n"+exc.getMessage(), exc);
	}
}


}