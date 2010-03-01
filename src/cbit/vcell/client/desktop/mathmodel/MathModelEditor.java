package cbit.vcell.client.desktop.mathmodel;

import cbit.gui.*;
import java.awt.Container;
import javax.swing.*;

import org.vcell.util.gui.JInternalFrameEnhanced;
import org.vcell.util.gui.JToolBarToggleButton;

import cbit.vcell.client.*;
/**
 * Insert the type's description here.
 * Creation date: (5/11/2004 4:48:35 PM)
 * @author: Ion Moraru
 */
public class MathModelEditor extends JPanel {
	private MathModelWindowManager mathModelWindowManager = null;
	private cbit.vcell.math.MathDescription fieldMathDescription = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JToolBar ivjMathModelToolBar = null;
	private JToolBarToggleButton ivjEquationsViewerToggleButton = null;
	private JToolBarToggleButton ivjGeometryToggleButton = null;
	private JToolBarToggleButton ivjSimulationsToggleButton = null;
	private JToolBarToggleButton ivjvcmlToggleButton = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == MathModelEditor.this.getEquationsViewerToggleButton()) 
				connEtoC2(e);
			if (e.getSource() == MathModelEditor.this.getGeometryToggleButton()) 
				connEtoC3(e);
			if (e.getSource() == MathModelEditor.this.getSimulationsToggleButton()) 
				connEtoC4(e);
			if (e.getSource() == MathModelEditor.this.getvcmlToggleButton()) 
				connEtoC1(e);
		};
	};
public MathModelEditor() {
	super();
	initialize();
}
/**
 * connEtoC1:  (vcmlToggleButton.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelEditor.showVCMLEditor(Z)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showVCMLEditor(this.getVCMLButtonSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (EqunToggleButton.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelEditor.showEquationsViewer(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showEquationsViewer(this.getEqunButtonSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (GeometryToggleButton.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelEditor.showGeometryViewer(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showGeometryViewer(this.getGeoButtonSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (SimsToggleButton.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelEditor.showSimultionsList(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showSimulations(this.getSimsButtonSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (MathModelEditor.initialize() --> vcmlToggleButton.selected)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getvcmlToggleButton().setSelected(true);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the EquationsViewerToggleButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JToolBarToggleButton getEquationsViewerToggleButton() {
	if (ivjEquationsViewerToggleButton == null) {
		try {
			ivjEquationsViewerToggleButton = new org.vcell.util.gui.JToolBarToggleButton();
			ivjEquationsViewerToggleButton.setName("EquationsViewerToggleButton");
			ivjEquationsViewerToggleButton.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjEquationsViewerToggleButton.setText("Equations Viewer");
			ivjEquationsViewerToggleButton.setMaximumSize(new java.awt.Dimension(120, 25));
			ivjEquationsViewerToggleButton.setMinimumSize(new java.awt.Dimension(120, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEquationsViewerToggleButton;
}
/**
 * Comment
 */
private boolean getEqunButtonSelected() {
	return getEquationsViewerToggleButton().isSelected();
}
/**
 * Comment
 */
private boolean getGeoButtonSelected() {
	return getGeometryToggleButton().isSelected();
}
/**
 * Return the GeometryToggleButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JToolBarToggleButton getGeometryToggleButton() {
	if (ivjGeometryToggleButton == null) {
		try {
			ivjGeometryToggleButton = new org.vcell.util.gui.JToolBarToggleButton();
			ivjGeometryToggleButton.setName("GeometryToggleButton");
			ivjGeometryToggleButton.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjGeometryToggleButton.setText("Geometry Viewer");
			ivjGeometryToggleButton.setMaximumSize(new java.awt.Dimension(120, 25));
			ivjGeometryToggleButton.setMinimumSize(new java.awt.Dimension(120, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometryToggleButton;
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
 * Return the MathModelToolBar property value.
 * @return javax.swing.JToolBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToolBar getMathModelToolBar() {
	if (ivjMathModelToolBar == null) {
		try {
			ivjMathModelToolBar = new javax.swing.JToolBar();
			ivjMathModelToolBar.setName("MathModelToolBar");
			ivjMathModelToolBar.setFloatable(false);
			getMathModelToolBar().add(getvcmlToggleButton(), getvcmlToggleButton().getName());
			ivjMathModelToolBar.addSeparator();
			getMathModelToolBar().add(getEquationsViewerToggleButton(), getEquationsViewerToggleButton().getName());
			ivjMathModelToolBar.addSeparator();
			getMathModelToolBar().add(getGeometryToggleButton(), getGeometryToggleButton().getName());
			ivjMathModelToolBar.addSeparator();
			getMathModelToolBar().add(getSimulationsToggleButton(), getSimulationsToggleButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathModelToolBar;
}
/**
 * Gets the mathModelWindowManager property (cbit.vcell.client.desktop.MathModelWindowManager) value.
 * @return The mathModelWindowManager property value.
 * @see #setMathModelWindowManager
 */
public MathModelWindowManager getMathModelWindowManager() {
	return mathModelWindowManager;
}
/**
 * Comment
 */
private boolean getSimsButtonSelected() {
	return getSimulationsToggleButton().isSelected();
}
/**
 * Return the SimulationsToggleButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JToolBarToggleButton getSimulationsToggleButton() {
	if (ivjSimulationsToggleButton == null) {
		try {
			ivjSimulationsToggleButton = new org.vcell.util.gui.JToolBarToggleButton();
			ivjSimulationsToggleButton.setName("SimulationsToggleButton");
			ivjSimulationsToggleButton.setPreferredSize(new java.awt.Dimension(100, 25));
			ivjSimulationsToggleButton.setText("Simulations");
			ivjSimulationsToggleButton.setMaximumSize(new java.awt.Dimension(100, 25));
			ivjSimulationsToggleButton.setMinimumSize(new java.awt.Dimension(100, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSimulationsToggleButton;
}
/**
 * Comment
 */
private boolean getVCMLButtonSelected() {
	return getvcmlToggleButton().isSelected();
}
/**
 * Return the vcmlToggleButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JToolBarToggleButton getvcmlToggleButton() {
	if (ivjvcmlToggleButton == null) {
		try {
			ivjvcmlToggleButton = new org.vcell.util.gui.JToolBarToggleButton();
			ivjvcmlToggleButton.setName("vcmlToggleButton");
			ivjvcmlToggleButton.setPreferredSize(new java.awt.Dimension(100, 25));
			ivjvcmlToggleButton.setText("VCML Editor");
			ivjvcmlToggleButton.setMinimumSize(new java.awt.Dimension(100, 25));
			ivjvcmlToggleButton.setMaximumSize(new java.awt.Dimension(100, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjvcmlToggleButton;
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
	getEquationsViewerToggleButton().addActionListener(ivjEventHandler);
	if((getMathDescription() != null) && (getMathDescription().isStoch()))
		getGeometryToggleButton().setEnabled(false);
	else
		getGeometryToggleButton().addActionListener(ivjEventHandler);
	getSimulationsToggleButton().addActionListener(ivjEventHandler);
	getvcmlToggleButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MathModelEditor");
		setPreferredSize(new java.awt.Dimension(208, 25));
		setLayout(new java.awt.BorderLayout());
		setSize(504, 49);
		setMaximumSize(new java.awt.Dimension(150, 150));
		setMinimumSize(new java.awt.Dimension(208, 25));
		add(getMathModelToolBar(), "Center");
		initConnections();
		connEtoM1();
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
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		JFrame frame = new javax.swing.JFrame();
		MathModelEditor aMathModelEditor;
		aMathModelEditor = new MathModelEditor();
		frame.setContentPane(aMathModelEditor);
		frame.setSize(aMathModelEditor.getSize());
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
 * Sets the mathModelWindowManager property (cbit.vcell.client.desktop.MathModelWindowManager) value.
 * @param mathModelWindowManager The new value for the property.
 * @see #getMathModelWindowManager
 */
public void setMathModelWindowManager(MathModelWindowManager newMathModelWindowManager) {
	MathModelWindowManager oldValue = mathModelWindowManager;
	mathModelWindowManager = newMathModelWindowManager;
	firePropertyChange("mathModelWindowManager", oldValue, newMathModelWindowManager);
}
/**
 * Comment
 */
public void setToggleButtonSelected(String whichButton, boolean bSelected) {
	if (whichButton.equals("VCML Editor")) {
		getvcmlToggleButton().setSelected(bSelected);
	} else if (whichButton.equals("Equations Viewer")) {
		getEquationsViewerToggleButton().setSelected(bSelected);
	} else if (whichButton.equals("Geometry Viewer")) {
		getGeometryToggleButton().setSelected(bSelected);
	} else if (whichButton.equals("Simulations")) {
		getSimulationsToggleButton().setSelected(bSelected);
	} 
}

/**
 * Comment
 */
private void showEquationsViewer(boolean bSelected)
{
	getMathModelWindowManager().equationsViewerButtonPressed(bSelected);
}
/**
 * Comment
 */
private void showGeometryViewer(boolean bSelected) {
	getMathModelWindowManager().geometryViewerButtonPressed(bSelected);
}
/**
 * Comment
 */
private void showSimulations(boolean bSelected) {
	getMathModelWindowManager().simulationsButtonPressed(bSelected);
}

/**
 * Comment
 */
private void showVCMLEditor(boolean bSelected) {
	if (getMathModelWindowManager() == null) {
		return;
	} else {
		getMathModelWindowManager().vcmlEditorButtonPressed(bSelected);
	}
}

}
