package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
import cbit.vcell.model.Model;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import cbit.vcell.mapping.*;
/**
 * This type was created in VisualAge.
 */
public class StructureMappingCartoonPanel extends JPanel implements java.beans.PropertyChangeListener {
	private cbit.gui.graph.GraphPane ivjStructureGraphPane = null;
	private StructureMappingPanel ivjStructureMappingPanel = null;
	private cbit.gui.ButtonGroupCivilized ivjButtonGroupCivilized = null;
	private JToolBar ivjJToolBar1 = null;
	private cbit.gui.JToolBarToggleButton ivjLineButton = null;
	private cbit.gui.JToolBarToggleButton ivjSelectButton = null;
	private boolean ivjConnPtoP1Aligning = false;
	private ButtonModel ivjSelection = null;
	private JScrollPane ivjJScrollPane1 = null;
	private SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP3Aligning = false;
	private SimulationContext ivjsimulationContext1 = null;
	private JPanel ivjJPanel2 = null;
	private StructureMappingCartoonTool ivjStructureMappingCartoonTool1 = null;
	private StructureMappingCartoon ivjStructureMappingCartoon1 = null;
	private GeometryContext ivjGeometryContext1 = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public StructureMappingCartoonPanel() {
	super();
	initialize();
}


/**
 * connEtoM1:  (StructureMappingCartoonPanel.initialize() --> ButtonGroupCivilized.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized().add(getSelectButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM10:  (simulationContext1.this --> GeometryContext1.this)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(cbit.vcell.mapping.SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getsimulationContext1() != null)) {
			setGeometryContext1(getsimulationContext1().getGeometryContext());
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
 * connEtoM11:  (simulationContext1.geometryContext --> GeometryContext1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setGeometryContext1(getsimulationContext1().getGeometryContext());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM12:  (GeometryContext1.geometry --> LineButton.enabled)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getLineButton().setEnabled(this.hasMappableGeometry());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM15:  (simulationContext1.this --> StructureMappingPanel.geometryContext)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM15(cbit.vcell.mapping.SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		getStructureMappingPanel().setGeometryContext(this.getGeoContext(getsimulationContext1()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (StructureMappingCartoonPanel.initialize() --> ButtonGroupCivilized.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized().add(getLineButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (simulationContext1.this --> StructureMappingCartoon1.simulationContext)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.mapping.SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		getStructureMappingCartoon1().setSimulationContext(getsimulationContext1());
		connEtoM5();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM4:  (StructureMappingCartoonPanel.initialize() --> StructureMappingCartoonTool1.structureMappingCartoon)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4() {
	try {
		// user code begin {1}
		// user code end
		getStructureMappingCartoonTool1().setStructureMappingCartoon(getStructureMappingCartoon1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  ( (simulationContext1,this --> StructureMappingCartoon1,simulationContext).normalResult --> LineButton.enabled)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5() {
	try {
		// user code begin {1}
		// user code end
		getLineButton().setEnabled(this.hasMappableGeometry());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM6:  (StructureMappingCartoonPanel.initialize() --> StructureGraphPane.graphModel)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6() {
	try {
		// user code begin {1}
		// user code end
		getStructureGraphPane().setGraphModel(getStructureMappingCartoon1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM7:  (simulationContext1.geometryContext --> StructureMappingPanel.geometryContext)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getStructureMappingPanel().setGeometryContext(getsimulationContext1().getGeometryContext());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM8:  (StructureMappingCartoonPanel.initialize() --> StructureMappingCartoonTool1.buttonGroup)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8() {
	try {
		// user code begin {1}
		// user code end
		getStructureMappingCartoonTool1().setButtonGroup(getButtonGroupCivilized());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM9:  (StructureMappingCartoonPanel.initialize() --> StructureMappingCartoonTool1.graphPane)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9() {
	try {
		// user code begin {1}
		// user code end
		getStructureMappingCartoonTool1().setGraphPane(getStructureGraphPane());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (ButtonGroupCivilized.selection <--> Selection.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getSelection() != null)) {
				getButtonGroupCivilized().setSelection(getSelection());
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
 * connPtoP1SetTarget:  (ButtonGroupCivilized.selection <--> Selection.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setSelection(getButtonGroupCivilized().getSelection());
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
 * connPtoP2SetTarget:  (Selection.actionCommand <--> StructureMappingCartoonTool1.modeString)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if ((getSelection() != null)) {
			getStructureMappingCartoonTool1().setModeString(getSelection().getActionCommand());
		}
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (StructureMappingCartoonPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getsimulationContext1() != null)) {
				this.setSimulationContext(getsimulationContext1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (StructureMappingCartoonPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setsimulationContext1(this.getSimulationContext());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Return the ButtonGroupCivilized property value.
 * @return cbit.gui.ButtonGroupCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.ButtonGroupCivilized getButtonGroupCivilized() {
	if (ivjButtonGroupCivilized == null) {
		try {
			ivjButtonGroupCivilized = new cbit.gui.ButtonGroupCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupCivilized;
}


/**
 * Comment
 */
public cbit.vcell.mapping.GeometryContext getGeoContext(SimulationContext argSimulationContext) {
	if (argSimulationContext!=null){
		return argSimulationContext.getGeometryContext();
	}else{
		return null;
	}
}


/**
 * Return the GeometryContext1 property value.
 * @return cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.GeometryContext getGeometryContext1() {
	// user code begin {1}
	// user code end
	return ivjGeometryContext1;
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
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsStructureMappingPanel = new java.awt.GridBagConstraints();
			constraintsStructureMappingPanel.gridx = 0; constraintsStructureMappingPanel.gridy = 1;
			constraintsStructureMappingPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsStructureMappingPanel.weightx = 1.0;
			constraintsStructureMappingPanel.weighty = 0.5;
			getJPanel2().add(getStructureMappingPanel(), constraintsStructureMappingPanel);

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 0;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 5.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJScrollPane1(), constraintsJScrollPane1);
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
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			getJScrollPane1().setViewportView(getStructureGraphPane());
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
 * Return the JToolBar1 property value.
 * @return javax.swing.JToolBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToolBar getJToolBar1() {
	if (ivjJToolBar1 == null) {
		try {
			ivjJToolBar1 = new javax.swing.JToolBar();
			ivjJToolBar1.setName("JToolBar1");
			ivjJToolBar1.setFloatable(false);
			ivjJToolBar1.setBorder(new javax.swing.border.EtchedBorder());
			ivjJToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
			getJToolBar1().add(getSelectButton(), getSelectButton().getName());
			getJToolBar1().add(getLineButton(), getLineButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBar1;
}

/**
 * Return the LineButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.JToolBarToggleButton getLineButton() {
	if (ivjLineButton == null) {
		try {
			ivjLineButton = new cbit.gui.JToolBarToggleButton();
			ivjLineButton.setName("LineButton");
			ivjLineButton.setText("");
			ivjLineButton.setMaximumSize(new java.awt.Dimension(28, 28));
			ivjLineButton.setActionCommand("line");
			ivjLineButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/line.gif")));
			ivjLineButton.setPreferredSize(new java.awt.Dimension(28, 28));
			ivjLineButton.setMinimumSize(new java.awt.Dimension(28, 28));
			ivjLineButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLineButton;
}

/**
 * Return the SelectButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.JToolBarToggleButton getSelectButton() {
	if (ivjSelectButton == null) {
		try {
			ivjSelectButton = new cbit.gui.JToolBarToggleButton();
			ivjSelectButton.setName("SelectButton");
			ivjSelectButton.setText("");
			ivjSelectButton.setMaximumSize(new java.awt.Dimension(28, 28));
			ivjSelectButton.setActionCommand("select");
			ivjSelectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/select.gif")));
			ivjSelectButton.setSelected(true);
			ivjSelectButton.setPreferredSize(new java.awt.Dimension(28, 28));
			ivjSelectButton.setMinimumSize(new java.awt.Dimension(28, 28));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectButton;
}

/**
 * Return the Selection property value.
 * @return javax.swing.ButtonModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonModel getSelection() {
	// user code begin {1}
	// user code end
	return ivjSelection;
}


/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}


/**
 * Return the simulationContext1 property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.SimulationContext getsimulationContext1() {
	// user code begin {1}
	// user code end
	return ivjsimulationContext1;
}

/**
 * Return the StructureGraphPane property value.
 * @return cbit.vcell.graph.GraphPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.graph.GraphPane getStructureGraphPane() {
	if (ivjStructureGraphPane == null) {
		try {
			ivjStructureGraphPane = new cbit.gui.graph.GraphPane();
			ivjStructureGraphPane.setName("StructureGraphPane");
			ivjStructureGraphPane.setBounds(0, 0, 290, 237);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStructureGraphPane;
}

/**
 * Return the StructureMappingCartoon1 property value.
 * @return cbit.vcell.mapping.gui.StructureMappingCartoon
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private StructureMappingCartoon getStructureMappingCartoon1() {
	if (ivjStructureMappingCartoon1 == null) {
		try {
			ivjStructureMappingCartoon1 = new cbit.vcell.mapping.gui.StructureMappingCartoon();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStructureMappingCartoon1;
}


/**
 * Return the StructureMappingCartoonTool1 property value.
 * @return cbit.vcell.mapping.gui.StructureMappingCartoonTool
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private StructureMappingCartoonTool getStructureMappingCartoonTool1() {
	if (ivjStructureMappingCartoonTool1 == null) {
		try {
			ivjStructureMappingCartoonTool1 = new cbit.vcell.mapping.gui.StructureMappingCartoonTool();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStructureMappingCartoonTool1;
}


/**
 * Return the StructureMappingPanel property value.
 * @return cbit.vcell.mapping.StructureMappingPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private StructureMappingPanel getStructureMappingPanel() {
	if (ivjStructureMappingPanel == null) {
		try {
			ivjStructureMappingPanel = new cbit.vcell.mapping.gui.StructureMappingPanel();
			ivjStructureMappingPanel.setName("StructureMappingPanel");
			ivjStructureMappingPanel.setPreferredSize(new java.awt.Dimension(100, 200));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStructureMappingPanel;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in CartoonPanel");
	exception.printStackTrace(System.out);
}

/**
 * Comment
 */
public boolean hasMappableGeometry() {
	if (getSimulationContext() != null
		&& getSimulationContext().getGeometryContext() != null
		&& getSimulationContext().getGeometryContext().getGeometry() != null
		&& getSimulationContext().getGeometryContext().getGeometry().getDimension() > 0) {
		return true;
	} else {
		return false;
	}
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getButtonGroupCivilized().addPropertyChangeListener(this);
	this.addPropertyChangeListener(this);
	connPtoP1SetTarget();
	connPtoP3SetTarget();
	connPtoP2SetTarget();
}

/**
 * Initialize class
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("StaticCartoonPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(522, 463);
		add(getJToolBar1(), "West");
		add(getJPanel2(), "Center");
		initConnections();
		connEtoM1();
		connEtoM2();
		connEtoM4();
		connEtoM6();
		connEtoM8();
		connEtoM9();
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
		StructureMappingCartoonPanel aStructureMappingCartoonPanel;
		aStructureMappingCartoonPanel = new StructureMappingCartoonPanel();
		frame.setContentPane(aStructureMappingCartoonPanel);
		frame.setSize(aStructureMappingCartoonPanel.getSize());
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
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == getButtonGroupCivilized() && (evt.getPropertyName().equals("selection"))) 
		connPtoP1SetTarget();
	if (evt.getSource() == this && (evt.getPropertyName().equals("simulationContext"))) 
		connPtoP3SetTarget();
	if (evt.getSource() == getsimulationContext1() && (evt.getPropertyName().equals("geometryContext"))) 
		connEtoM7(evt);
	if (evt.getSource() == getsimulationContext1() && (evt.getPropertyName().equals("geometryContext"))) 
		connEtoM11(evt);
	if (evt.getSource() == getGeometryContext1() && (evt.getPropertyName().equals("geometry"))) 
		connEtoM12(evt);
	// user code begin {2}
	// user code end
}

/**
 * Set the GeometryContext1 to a new value.
 * @param newValue cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setGeometryContext1(cbit.vcell.mapping.GeometryContext newValue) {
	if (ivjGeometryContext1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjGeometryContext1 != null) {
				ivjGeometryContext1.removePropertyChangeListener(this);
			}
			ivjGeometryContext1 = newValue;

			/* Listen for events from the new object */
			if (ivjGeometryContext1 != null) {
				ivjGeometryContext1.addPropertyChangeListener(this);
			}
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
 * Set the Selection to a new value.
 * @param newValue javax.swing.ButtonModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setSelection(javax.swing.ButtonModel newValue) {
	if (ivjSelection != newValue) {
		try {
			ivjSelection = newValue;
			connPtoP1SetSource();
			connPtoP2SetTarget();
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
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
}


/**
 * Set the simulationContext1 to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationContext1(cbit.vcell.mapping.SimulationContext newValue) {
	if (ivjsimulationContext1 != newValue) {
		try {
			cbit.vcell.mapping.SimulationContext oldValue = getsimulationContext1();
			/* Stop listening for events from the current object */
			if (ivjsimulationContext1 != null) {
				ivjsimulationContext1.removePropertyChangeListener(this);
			}
			ivjsimulationContext1 = newValue;

			/* Listen for events from the new object */
			if (ivjsimulationContext1 != null) {
				ivjsimulationContext1.addPropertyChangeListener(this);
			}
			connPtoP3SetSource();
			connEtoM15(ivjsimulationContext1);
			connEtoM3(ivjsimulationContext1);
			connEtoM10(ivjsimulationContext1);
			firePropertyChange("simulationContext", oldValue, newValue);
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
	D0CB838494G88G88GC1FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBD8DD89C5535C45124163E26ED2C28F9FDF91AC6D454340DAD3571B5AA3651261F08E85396DB2A29AF7625ED1ECDDB2C548E8B84084420E2CDDBD4EAE28503849212A041041F4504C8E043CFC29284165DE1F7426CCFF6E7E189A4F9677E1C59595DD996293E77783E1319B9775CF34EBD675CFB4F3DF3E7A3143C3EA8AF5BD0A28859F9827F5B17AD884BAC0270ECE76C55082BFBAB35D0507C7D9D60
	8561EF1BF261FA984ACA2EC8ADAC968C2E82F86E076795F3D38BDF017B1704FCD75F905CC410D38DE5DD36782E6965741EC0B97974F96F51A238BE8FB084B8F2CDFC0B486FB83A1563E3B99EA15BA0888B4A890DF43414635683CD8160CCG56F2C6BF85D72640E575F51C2E7FF38942067BBD064D040F06A7C2BA2FABD430EB1970076C048AA26B73D4CFD2278E1E5DG2471B942D3B56F423541B4EABBE09916142AB10BA84BD58E335BADB9EDD5B61FD4DF5F2DF8FC964567910FB2EC0B592338DCCE1359A94A
	D0F77B5B047F0F0E96D0FE02308EF8AF253885E708CCBB5C47G443F0D78EFB988FE835C976DC8AD5C3E835B795EDB8D3C9DFD135F9052F6B9F237C4E8E7438E56BE6B0EC0BBE39C0F9A7EC2FDF2E59F317DA2A8CB86D88D1085D0DA19DA58G70B79A8B8BCF3C8D5726E1254565F0381CCDEEF733EC759CF5D9C5996E6B6B2104633AC40B4BE39584666F782315BA7ACC872C5E120FDC0F59A4E1B77ABE316BD6A17DEBDFB4646B58A4BDE15250D349ECA1D786EC42E47FB4226C384A60588C16FDC2504A4E5D7C
	C8CE1E0E6C32BFBC1C330873D926111DD5C9FC3DF51478BA8D6EC9BD62CF06AF277846F782F89627575018A14FF6A8DB738E46C65F7328CBFB6EF5C2F14DD53AF6083FBB4EB0D445F470D7056A3253C5F8AF2EE2B277293AF4D3FC694EG1E69F2970DD3723CG4ACCB52905AC3E3EF40A745B225D290565G0DGFDGFE0085G893BB146FEFEE2678CE34C84DD2A59A749D651A3303ADF6A7BB3DCC53F62B137B93D0A59E9910F1BBD12F9D8963D822739210F58E0B828DEA20DFD8B48B8A4BAC50FD911C8CF75
	189D22A2FA3C0C66763E97880DC72425BD12C384818E3700FC6F6CFB094411592B743A2DE6C58CD03072FB7B08CF464C4701A3B000FBE6176F1DA676DA896D4FGD82F5A2171A4617701680181233E3E5D659A7539FBC8CB0446DAE2675F5131A3976ECBEB51465F6DA738D8F8DED80BFC46CF3E2861E314ACC4FD33E7827B58EB2344AFCB01BE85E0BDC0419E70910089C0590331742BBE0CA56366DB044A072F540D2585905773773098CA58935A272A28AC22EF8A142D87A85E1BDAD88DE082F06F4579E15199
	B767D74075A03AA9FB747BDDA810B6144F101634BBF84C7936AA1B341B4C99F93CEF4DF45CFAEE0634B91AFB663FBD83A49666039D9281D66DC59FFE75E4996B934DE62F083D20538381ACDA839EE57A6FF1495CAFB9DC77FC5E16B7815AC8ACG78GDCGAF833C426593D92F823CAE30F9FB9BG19675E8228G5881708EG691FB5G3B817628F6BBC9652E811D0B762596568274834482A4822481E481EC2FCBADEC87D0G9682A4832C83A8F9976699GABC09C40B200F4005C7A5442CA009E00A90045G2B
	GB654E32CFC5731B4DB3F3DFAB60F565EB7E7505E6739DD2673AD5A79234CDD1140253977FDCCBC7F2F016701CE623BD03FC56ABF517C36B90A5F76F1BEEFC551ED8B556D63E9674B1A7B177E9F587D63GB6A6A51CA2F69247C427E2945D22538AB7DEDEF65BA962C7BE8F0F3854BA37D33C9B666D9618A83DAA7EDB27083D4D0A621106FD0A58ADAA8A244C0172FB28AC2FEF589D1872D1567D27C86C3846E15ED6699FB8C566BC0B4B6994696028D661658F52B9D714C587A85D6272B995067F41E932CE9995
	A7781C7F63539B85B2E74BBE12BF305CFF291944275BA3B9E048A5A908A06FA77371660E2D9A9B15820E2CE19A615A339F675071EDB807EEBFF72330E250ED504B8D633F7CD943327DECDEDA39BFB007B21DD6756B6B1ECA67FE0E77B2DD5FEDA5F668D6A0E53184AFC0F2E81D3F9BE1C1A7B47B944565BC64F1795CAD5218A4CB67C4ABABAF2BA0FAF783D3EAC6816BF5EDA1FC3D1243A753EC284565D4405BB5D87E9B31144A55DD827538DCF28D23DB193BB5B29D275976CAB1DC8F09AEC846BC93A8094D174E
	C1E20B35E023AC0012861CAFBBBEA465187370093245A5875A515FC0FCB6BEDA2231FBE583739939DFC54B2FD74B974373723072C3B216173C877354FB01F2467F173216FFC3D879DF9C5A721877C279372A65AB20EC35261C35F37FG310DD7BC6B93A155A5496187EA6CB68F3C98D2F61C47B1ABBFB2C062D85664157281A2F3318B5757C587183C6D87D83E56F0002F15A85D009B6912A250159FA4F4AF1EC53A9440137CE0ADDCB38136C2F915CA172152A987D98EC168628E866852A95D928F52A59EE47212
	8F32DC12503D265249DE24ABEF64FD3111651BC30D0C0E35E32F02F4EEDEBE8F2E71G4B9BF9FB1B88DD3E8F69D6B732FCE6BD671B5B24ED6F0DAADDD2934BF9D21A82FC2BD4395EB1244B606519CDA1F2A9DD63B85215B4B33A4AE616AFB7B4EB69DE74A3DDFFB353CBE9E639F5DC33D6BF478524DB5A4CF2B0528E93679B506F6F9711EEE5B34B57B21A19BFB21B35F67911CAD75442F2B9344B76962DFED382529DEE8968B78477B1ADDA7D8EE7A3DDFC8B4B89C375E37E4DD2691636B07F2FECE16DDD532275
	6F8386244BEA8936CB09D1AB775A9C242BB4327C1348A57195EC174EDC24B39A03797587717BDB1E9A4FC6164792FE243D417C9ED069620C4C3F0B0DECAD34B8085F559B11EE191165436BF93C2EB2EA797564A35DDAE320DC4B0F517D35G6904D6D63E3515793732D56B5FDF28F40D2DAC778E75AF1BEBFED3C146F23AA7F646BCE676D7F947E1AC2F2FBFC2E79C365FB55ED140697BDAF1DF6B7AE3F44D834F89GABDAF12EFCF0B36EEBAD98CD90B2B6BF1C93E97FEEB557B72BB5B0D7265FB61E43660824A1
	A23361FDD8F73C0F73433D835A35F513576B32C8F4A263E323B2281DCB99EEE2F093DCC7A40FD7E903E4470F636845C122AFD977F3B4374712ED5B846D9EA15D95541BEA793E517977356DDD3E653C6139886D9DFA1F35B3667DC0FB131F7CDD8E5BFF391F6E911139F38514E782D49F226317CCF29BB2D6B2F9165DA8EF7E39AF8BC3D7DE93D11EF20849D9F0A8A0AF711DBDDC1ED3C6F92B20BC77B04A3B194AA3E3AE33C77CEBF5426C76CCC94240F87C2938F616BC182E328377F39BC0CF65B06AF654DA9E19
	EFFDFDC87241F8FD5F056DCE84BEEBGD83ED26BA6F849A9A9121978373C024928F63E81D73355DA5E2E699FD5CD43DE4812AD8A0B0C7D9547F19F7116579A397DD75DFA1DD0E03E4C20372F9B77244DD05A467CE2EC5344FF5A230675GCC5739EE54B58EE8128198FE7FD944E7812E768836613A3344F7E60F2D0665A9F30EEB7394996896822CD0696F27740ADD72B25D27CE226EB12B1689DBDF7A0C2E6E84520E703EFBC433777E582306650F216E059E54317D8398F7G18CE49A6BD1D16FF00EDFBC02D17
	823802F65475D5196868556434CC67271A3476CE4A0F535559FEC33C21311D69EAEF8F68BC7478238665F15479CAAF4ACE821A752A6C3BE4324E894B1B854C1B4B9434A363B328A01F7223B0F79CC5BE5FD169585A63539B35EB0FF820DB8D1085D05C01F5FE2510765802C3196F59D67B0A7838D58E74C39D58AFCA8E10ABDD7F88B82EBDB4F01272D120D7F0AE656DBA3D31BB34157BAB7448C27A56FE559F4F3C0A7E58EEDCAC54ED980D98C3719D4C8FABBA827E283CF3CFCE631DB8C6257A500E451D290507
	BB51A66F0EE11CB33A05E3E8079820C9D269F246A35BCE9A47BA6B013E5C04F5D24651C76CB9F3BC126FF9DF74230EC340A3DE65D3FEA1F4BC61EB308BE4BF442BBF5CF11EDF3C20768160D7F08C7B46110B08EF841C7F987A7BG1D0B1DAE0F43ACF709DE1F2CB0D977DCA4FBB32213AC932C1CF70E006DDE8A75572ABC72DEA1FB70F63317E8C4AA20074BABCEE1DB3FAC207C322E54423EAE6C53391236CF4F877CF2000CAE34C5F5B61903ED9E335BCEDF88A03F335948EF8350D6F6E3BB5FB4A05E8A38055D
	48A70E3E8BB4DB189F9C8E33532A7641EF98088C8730072984AC0AB2B2D5DEE940272887F9DD1AC3664D6140444E6913F3105E8834D3BD2813BF97714BG37CE651313D708EDA399139A9746BC245F5E8BF66AC5BE19AAFE8160D675A21F410D41E3485D9B11AE77B864534711EEFFFE6418BE1F0F3ED500BE71B84AECA9C0DEEBGD7FA82F95DD4D0206145F08F9504474CCB054837876A461D40BD5C290E50F7ED49A7827B722BB577886C9D54ABDD045F3A936C9DD41E0AFF0362053EG1E493F71984AAF01B2
	FF9F3EB7F958C2464FF4F84E84A8BE897D9860B0007DA4660C79C3E49FF15A77D2EDE0C30F53ACF787EDD4617B21A1E217D1F102BF5FB5249DDB199EADA2097FD8103988A089A085E03D2A43E1F3D49DDA7D3ABAB0BB270C92BB971F1A593B91822C5ECFE83D720FD00F0D619527346F60F3DFB8E2A0B6509B437BB8EF7B2940984EEC32EE0458A4817049G69G9BGCACE23CDBEEF2E0CE693FC87480D415A2448333345A572C735852B67F14C6E9DF84CAC6BBD640AD62F76F4F09BD83D1FC52D5797D20F7929
	59CE5609EEA80B83D88C3082E08D00508FEB45FE7457CF4C7B2346B059D46EF1B57171B9E497176B7999776C5AF7ED54FA46FE3DFACDE7E7A72FB3EA3D7E90F9EC9EFDFA2B4E7A7188D98BB51BBDBDAE1B0D44B319F4787E7329AEED8E28743377428B613A14581F2E35130E97886C5D464AFE9C7B6E16085FAABF04718EE088E08AE09140F2000C8F51EF55434D517C263EB2875F61DEFE387B38DD36F8E6E74FD723564B7CB0389EEB7BB3A6ECFB71G4EBB8F4FD207472356D386747A48BE7A7EBB8E4A92GD6
	G2483880360AB0046C134F5ECF4DB2B861E092D2F754E2E1DB7C62D57B728D7CFD4E6A74F9B351EFBD02F2F1C17F6899FF1DF003F87E1FDE73D907A6E06741DF9A0EB61A0761DFBB6625E5532C15C3B5ABDC2738FF84E9D02BAC36A79B6DFE8BBEA07027556EF3FB12CDED54833E9C8AF2EEE17C9DC5921AC96E0A1C092C0BAC0A6514B0CF135FA38A9DADCE97AF08B608F3BDC23BAE14575DD9D266F4C7C7CC454FA8DE63DFA570D4D4E2ECB436A3D96725C23AB4FB5BEBBF967436A054A13F565B579E7B947C4
	2D972BABEF610559493BA9EA3D053A72CC97E7A7EFA0EA3DE53A728A0559796F1530FAD5A14F2BF4655D9CD6EFE66D7BE654FAEB4CFA63DEF1F31990BE6E51B5E4FD7DA1DCCA32B16E09916D9817E5E6FBC4654330A69C46B1EE3F0467D1FB06F1AC9B4F1EDD1BAE0BDAAFEED82F5E8E436C643D9B355EE2DDF94F4ED2DED1CE34FA4BF5655D9F351E3E3C55D16B25694A3B10BBBBF9F1D16B2D5315F7A4EFF6727A22564B35687AEF16720AB6C62BD72AABEFE554FA7A729E09DECFD75E2579339B43BE97D6AFF4
	8EEA571557D6B03B761D08DA4F2AAB6FAF0533EB5FDBE1752AC21E27C26431F333DF206FA097C0D982G4339695AE095BC2F31603877EA974195D8D38B4B2C08AB27B8721E010C1D8CF75B313EA86DC6A0FC26ABAF50A967B1B7E399F9D14AF3F4F0FCDDEE09FC1E6B67967D73DC7FE551BF4F755F1697989EF8E8CFE9E3795316694FFBE5DBA31F777AC00C2CE717282FE71F282F6700B83D1EA3E2A4BDDF2536FC046E89FBFCCEC5F20814B51B17C5BFB9178859F4174F49D2ED425D9B3A9FA5E07E7EDB875933
	2D35E21E7EC7F117901E2737DAA54565C1A6BD225953B9B222AE7E3A5D2285755EB09259C64F0C685BA85B26EF23FC5B74B65AE21B5EC67F2910B310E1E771BAE015A1601E7BA6F7A127E9D5DCC7EBD45E6D76486D68366B3763241D6C6DFBDDBE0FC58C55F5483ED5AD8B2CEA5A9451C154E1B4922D2F18BDB6B12C2DEEDA1F15C52ABFE10FE48F56566420366ED2717E334FF13CE94463F218EAF80EB7F4712F026E398A3E95047B9B1F27E39361ED32627A747EDE921F7360F991C012BA0E5DF9DCEFBCC96163
	4F9AFE4D540CC7C5A20C03G35E27098F5981E7BC3F07C1C229459F78FC97A3E7B219459F799D2F45FBDA9C5765DFAA93A6F1E16683B9C49AAF9785BE1661B77BD413E594547E5C1F7DCF6F3FB4472EB3C46BE09F03FC28C9F6753G3796A0CBD346E497F9B1DECC0A4B94689BCD23116DFBF8D45F3E9D23116D5BBB9A5D3E8323116DAB0EC6372FF3D45F3E29C1ED5C2D36FD2132C9BBAE6C41D8AF4F463D1862910C75B47ABE6CB0BC8F8178C7B05683795A7C11F03BAF564131F7C8B7503D1D2491768EA9DB45
	AF2578E48D1E3DDBFAF7844F1624C0D92DAD35100DFF777D1978498DC32A68259B5BC633E2666DF37802622AE5B737653765483E7D0E2C6F5BD5F2E45FBEA4C777ED3A9C59370F4B51FD3BCE1EFE5C5BC25F2DEB4637DA067F6118F6BC5C2752AF99576283B16177EB7135AA7E218B41FDF4B7474F3D28255F2352B78A41362F61784784AD7DDE81575A36D662CF1DF1385B62F149B2FFE74B6273B59B2E2363ED981F1DF6527E94F8DE8BA058B1BE9B256972662D767078E4FA5E1A9D5C5EDA0E9FB6E87BD2E0FE
	591323EDD71D0A4F4E8D66330763EF4F55523F2B524F498B265F0B7CB7867555EA247F5346E07AFD9CFFC7FE903DFAF6BC26A0883F8B712642E0FF6D62781BBA4899FD59BC6172A9E0EFBB7D86D1BD0BDF894FC6G99A0464E76F519BF3FB2CE780DF84C8EF0A3CD1E94091E15B2E15D8C205FGD08278DA009E895FF77F4ECAEC03B3F00DEA1BF77208CF590947DD186BBAB6EB7B854FFB186F2F6AA23EDFAEB19F27C8413EDE93721C894FEC8CBAF01C44D2511950775EED94DFF6A6743DF7CAAF3E772E053278
	B3386E79A9FD9F14854FC52308B31EA0382D70DC2D626E1BA0B8A3BC8F29382BA689CE0667A9956726F1BF9F1E972B38EFD2DC92BC2729B8FF872E37562A389FD3DC111CDA38DDC65C9B47D03FBA95F769B1544F2862D69EC07D7AD4DC45C1544F2A626A9AD0973F0ADBD0013A4429385FD720AECB83F48241A54373EA997B6EBBE1FBDC6B64004F84C7243965B0BD13D1628875DFB74517BBC27D7723BC74DF8314ADF2287E236FA4F21D101BB99157E4437654BAD14FDBC684B5663E2E39BFB8B253F565EC006F
	6B7CAF4858E61BEE8C0DF5C67243BDF4CC0EF7B2FB6728786FD3FC02864F7C50EBD74F75C1D9298BEDFEC2C21B4738702C4C139D188FAC86DC3A8B471DA72A4838204EA3D566F1253E3E59C53ED1E9E703981BC3EABA8BF87D92B74EA1031D04E7BBBCDB81265CF816577F8FBC4B1B3864BAA12DD07F2CF4C64FF94382B733C512BBF04EE20163D18313F90B89E5EE00724A3334DF861D33A4ED7F379C3657424AB79F5316139C6003ECED794120F2226F4B395A7233C16544C6F385D64E5A763DB731EDD95FDBAA
	483747C67C9D8765AC4F3779F57E1A2B33637FF4AE077968D63ABECA013235E751EF37F55371056861C1DCD52F5AFFD45C88458D41335F03FD4AC8474EB8F8DE2C62BAA9AE13FF3742777DFAE79A7F040F9EBEAE82BE907F3F1E310CC87BC3FA7BC25AB218730E53F3E510F8AE77105F78F80FFC0A4A4BCE1FA0F329436C179CBEC737F4CE946A3C749B97BA1788F0DF644539B3E3C26F6C9D63290C85F85EBAC964C9B061B3DE49F43E19027BF9AA2F39F42CE3E7F9C5AB237B121AFB252BF4A9B6A2536D91C7C4
	0FC734D28D852E631314FE9D5CE78254A98477D9B5FF582EC44A3F1E6A20ED161CA1ED5EC47BED8C540B83E06D3AEADCD065A55279AB894A125572BB7C0172AEBAEF2D0732ACG06B353F94B640335368FF53C3982F51C678B571155ABFB1B6ED7C2F9028FEDF13320BD1747686E96305F277954BC002E315642F3160A7BD49E760D32B10C6F8EBA87354333DB451D24B87AADD39762B272BE0EF90048407BF64DBD13717447A2C35BC7F2C26E219FEC0C9CCBDB72C93B6701DE8BGB60C91DC1F4DB80DAC1CDF90
	D6C01DB4001779735A3140BA137920DE1D5F9B4651367759C9DF09D31F9F224F64DBA376FC5A2E358B217BE7ED94BE86A9C1B2269BCFE683ACB7F7CA987309500695G99G029F62510FED7DF558D95219697F47D9565B5B79D16B319C2346C46672C6FF687EC2BD45377BC3779744CE4CA77A20EC399F4FEF7D6B8431C959844483C08F008C908710B0017B49F31C309C1B7E5CC70F4B5DAE0E0932E60B17356F2B260F5ABEB6EF5F150F737632BBEE9212AEB7C67C3EE879840B47F4FEADFF7C6AECF2E56D2B1E
	A46DABBA17DAD88E508EE08570834CBF076D3B4F494F0012AD72A6C5B1DB6C744BADF543DD924783E83258E3FD795EAE226B086CB2ABFCBF1E7563C73BF0BCDCFCCE7D66221B2CB307E922066B4CC73BB19E1300AE85A0F7B235F06BA47B9625F11250347616CDE32B6AC956E6A663FCAF31114BA301166CFBB4264F153DB8274D1BC4FDFEDFCE6201FD1B4D8E827239EE8FD9BF37CBCEBC9E487D7140E96DD95C62EBE48346D728CEC9DC276449C00E4866295873022A6BFFCC107709CEF13C8C5358AA238466A5
	BFEED33E2B2E32ECD7DD854ADEB42E15DD3769925D6F5E89E4F219C5D37C3B20172F6132CF88815927ED07B43271D32AB60759A636C142D0CE362948CB0C720AAA386D92594ED7799BBBA5C2625D49B206AA1BB472C6C5856EF758E8FE637109CC5E396C00BCC747CC5A4AF2685B55EF9C0DD3181F2F7CD01B43166D68544DE1B3BFBCE718626DDEB42579366FECD98E5369B1C3C0279BCCB35109554B4B8D543B73404C6BAD4F8B545B56507ACF59DC0608C40BA37F1FEE8C707FD4454CED7CE0855AB8ED8AF3A8
	C3010617B093DEFC7F117299B14B5E407B28535961FBCA1EEC6C6BC567D38B378334838C1DE76F02D9DFDF14B7DDDF771F577675E70E11B9BE4231C45C83255F30E9F698EBD8DB0FFC98E86BDEDBD9643656D4D7C3E32B2BABDE2734824D73585F7A0361380C030C3FE300BC31587DD99146EE694D578B469F988D5BA2745DB85E3E74730158DD8D314B7220E3EA9ED4F2A1BC7F4031887320F220E938904803CC978273A1EB777CE3AFEA6DF7A870618C0B11859246C87F85DC4B2E12C25732BF8EDA2BBEAB052E
	E5093CC0F975195035EC2BC1DBBEFCA6F4AD0B7559DC3A4FA428770D1A7B514ECFFE0FA4769315416661A593115FDB5CB2217F5E62EB937A6F1D3FB9B17D99013BA6A21F91D8B9217DCD9816C7448E925FAC00D8D88A10863096A06B826690474F116FF359496A507C2155EAA36FCD48AF4AA9A87F5D4948EDED1C0C7EBBF4ED13E41E9EF6419C6DA07C915FB9C97C25385C9449F0BD1344C69E49E6D798A9471FE2FB306288C5737D70EE6DF846F2473C9E5C375AFAB13550F4D17DD62F0C7CEED268BB0DAA1664
	545737AA963BE8ED66A98F6FD7C7FBB0B71AF7917725A6A97F95703C9AE07DC54CD106FB4944F3C274AA7C77EFC46DD8D27AFA23817BD8E8BC954430B122B6A6B01684464E44DEB286E9D56EA67F220B3CD547DB3BFA5ADA1A5A396F1F9892D41B7DE212347938790C6E290AA34033F3788CE487BDE61B9ABBA9E652E6BB68929B23661C67F7C46283C813476596BD4A844352DFDE617D700E20F546E2601794B37DFA0C6910B9CC661FD5C03B9620E88E4CC1GF5G26B92853FFE417CF23935BDE24C67218A527
	427727AA347AD80117FF4E747A705F1C4C4675C89C50A74F41182A99AA954276B78FF9A4EB335946B4E0F174D8B646D1569C0C23C76837113517405C8AE03D8463684EBCB29F3708647BBA34C73F154CA7D3C0938F309CA08DE0AD00F0E9EAE189C0B9C083C08F008C908330GA081A089E095C046256AEF6C8DD4CCE3BB5E000071547EF0098D67198D40EBE673ECC9A31BE76BB4FB71D7873D872C0C051CA2F6FABFB03D6386A30FC3DF980CBE8EADE43FF58163C74F041B3F4B7D77C1E2EF574808D7D4473BA5
	0345AA2E4DEBF2F9A532C0E13ABF5793ED0D58935296FE76AFEA3D50B30B2CDED754FA214056563F266314BBB6F44D1DC971D33121EB6EB9BD98637320ECDDAC2E39DF25B91769B208AB0079G89G4B8152G56828817E3DC558DCD57A759609A88ABD667E1B351D91D7B5870417D10EEA673187ABDB8DDA632AF15597E5003F47DD18A720F8938E774884555F29CBF6FB54DF93F82CB7438F94122BFFF3DE2891F3FCAAD7A73579B966D7C45F47DE5C5387ECFD5E875673F919A75FC39FE9EF46BAC6B5D9275BC
	34FE4C9D216F6ECC170746DCB74577DF9E9AF3693998F3F6A8DB2546115B9A59DF6356687E0A316A7B6BF2EB383FBEED557757672CDAFFB15C972DE46C334178582752DD2B62CE2A38C556C20EBBA1D9953B0ADFE25D447107C5A2907109D42FF10A6B63382FE8F0A7B96E6BD6B2D6C1136DAE0F7AFB02D612F70CC8324C4F53D09A4989C317EA0FE42A2364B6EB743ECF451DD47DBEB7685DE2755C54C2635C6947E59633CBA50C63DBF290F73B04F1FCEFB61E0FDC7152E62A9BD99F3730DFA0D6E57F36C7D06F
	1F555C4F6D7964578B0BBED1994C5E653648B13D5396BD266BEC7AB1FD40969E53ADB67D18EE330547F487676BD18F2A72BC1C3EC7E27B82C74DCE330D6645AC6FB17F23C8D047E59A628CDF6A5F9C40E3086377D0F9811F1E545CBF167B497B7719CFD4860BF776AE2E140FF18CF73D8D7BG41317FE360F041E1D8C7A77ED248835BA842C636BFC76FF3C93F70FA049743F7B71A7E6507BFC89CB4169884C31B3051CECD5BA6640D935B7B490FB8539F1B063A5BACAEC715B46C28720DB92A4C43CA1588F3F095
	F12CD55D3FBAF54FD0127D3E175FB088F9ED42F3A312A8DB3B698ED63BD0CC1F0C225762115CE43AD5049769EFB86D144DCE5BCEB6796EEF97DE20F43873C5581F95AE88796D42CB9AF66FCB32688C94BD2FA96A6DEAD7046799E727286C04677D349D590A70572A2AFE7AF5D7BF5BCE6B8FECE102B6617B1A047DA63717DCF2D5CBC4FB5A48E4ED8D1505DBF70AD09C3A1BC72A954FCC4ECAGABF8E30B6A49B6BAA0E70C6D2E4FF97C7A9C03108BEEE41D398D7C8EBD923C69E1DE4D014ECCED9174C7F913AD22
	DCAD705F1E38285DF34E3C4C92F14F396F8AB61E4BD7047E76449D83A4D693G1FFC854E431B8648EF8BC7DABFF6BB4C322C186551A627152CA51BA794B130E7716D81425F0BB43C101F6B9CA0FA1F5154799FD0CB87889BB57F76E99FGG5CE3GGD0CB818294G94G88G88GC1FBB0B69BB57F76E99FGG5CE3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG239FGGGG
**end of data**/
}
}