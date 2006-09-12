package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
import cbit.vcell.model.Model;
import cbit.vcell.model.render.StructureMappingCartoon;

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
			ivjStructureMappingCartoon1 = new cbit.vcell.model.render.StructureMappingCartoon();
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
	D0CB838494G88G88G4B0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBD8DF8945535298664A7A0C25008D4C083069AB434C123C60D481F028A95ABEDE99F5558E29FDA2A3445A77D1CEC02C48292A0EA349123D08DB58602D1E224B6C082898409A40484C2486C6EE43330FF4C4EC2FE611D7BF3E6E7F7E733F12BFDAF5FF71839671EFB4F39671CFB6F39BF3388B96F0DDF1DE64891043455827F5B1DA68809A6C1F8E36E651FA3EE0423A1CB507C3D8F30D1D0B2B260B99E
	72EC8B9A323205D356CCC8A7C2BA7731062C3760FD3350EAEDEC0517A0FCF6C25EFD17C76C6E1B4F9F3EC1BEEBE8FACB45BAF83E8E1084B8724CF0927E1FD764F27CE20EC7C8B38842B2CA73CDC59E47D5C13A95A08EA0594968974273B54AF9C0D9A927EB3EA6CED87130ECD8CF6A515409D062E2E59B79B3DF781FB461BC6155C565A4E5C6C05E9CG5278F4610562AF603927761C65AB13512A94DDB0C9B6DB11DDF4392C8ECB1145E3ADAB2BD3E40FC971485291065DAF4A0A536928959D128D4A76507A27D7
	E4527A8421825219B2412D15884F94C8AF82D82E620B5B893E865E4DGBDB236731F9F6D616DE86C3ED3C8354CFCEDC310F6C64B2CFD4932371D117FB0263FCDED32230E68FEA56465399B32F6825483A8G51GFF233E707931FF40F32FD1596F345B1D0E3DAE57BE1BD9BE62B4CBB6F8AFAB039C0EBBA6191C32D990183DA3AA8AF564698BD839615F3A9C53C95C27E87B78E337890BFFF22DE10D0ECE9647F59B625DCC97B35CDE1DB05E330372CEF57B7A26AF6F93021637706C2EB52BF5F8677DEC571A153C
	1EF50A17F73DC2ECBD1C7AC0A13C13F2441E8CFFA34547E8704CCF33A91E24D3A0EF0F87FD2371F51425A663A6A13B78BADDBD44CC0FB2A4F9188C73BD7E32CC34123AD7F9984F5D2AAC8FD13CF2410BE7326C26FECA524BA0AF2E23A10B79D769F152EF4DDD8DD991GB1G89G73811681885D68E3170F6D68270F55C2175A67315A4C12AC303225F5EF42D36AD0E4315A61D6C407C9EA90E52BE834C9EE01536C2DA3BAB07A14BBC0FD5F84BC8ECA8EC99695AB692932E81794C9F6B31A23F59B890DAC515CFA
	2BDD8286F61700751E285B8CCF1B68D60E3B4C22A2F9A9D87E19BAE2138BE28354889540BB530B54C074158F6DAF8528D275708BDA5FE1C90681232C2C4669BC67F155131688B15DC44F3F26E3C7B33CAB5D28E3718441AD06748A351E0D751BB475D4D8CDC4FCD16E44BE96AD913BE476B4E495GD4813482C4G44814C6BC1DFFA3E91FD29E27D94217009513A3EB4827CFAE98F73A121573FCF45D2DEC45E82482B82E88798849087B027976707B7A5972F2F154BD140E5694F98D045656F8FED26060E717ECC
	65CD5ACD660C553CEF75F75C4A6AA7ED3A661D596F31C6628BCBC18F99179A3236DDC29B56546733BE31CFF4CB588B0E4A60401259EBD1A67F2DD69B37CBBA17FD8DEF4B5F812A09AFGF8G1CGEFG3C457993DE6F823CA730F9FBABG1967BE82588E70B140A7G24FF9683FC8A70192A3747A85FBDA03399A08AA091E0B1C04665062CC200FA009E00D800F9GABG72A29A32AAGECG51G49G29G6BFE5010D582508C9089908F3090E0954036AB9A32EA2EC0DF79E7FBC21AFEFB75F49E2A3DFF6FC7FB
	DF67FA694B362867EFB3F7858327665D739D5579FF8D4CF67154F67EF68B56FFC259EDFD883B6D6675FC94C2368DD4366F261DEFE85EB77FBF507BF781ECCC5AF71C68C93AA0B9148A49A5B94C706266F95547099D79BC5C66D44B9C25F8974C5B7BE122F42B786347093EC5C5112DC60FA255C90A82A133B77FB465657698DD5EA99FF9190F935FF1DE04F9DB25379EA7F31E4969F0C8F4F0D40B707CF3F44E15EC129D045E6F74B89406BF5FC056A96724CEBE67FB9ADE95481CED73107801457E89A271CF17EC
	354310CBC290E14695E4BE7E133AF6A120G0E2CE1E26099FF854E219737629C3A2DEB3210F246E5500B8DE3A6DEEDD8F7A51FBB2F744E21CC2636937A325B685C4F71EEA66B9715C48FF58A04ACA65F85C8BAAD73FE85AC6804FD9EC5F1BA8E4ACE0FEB3F750255E66D124CACBFFFBB113B8EAA25EA94305C318D24DE37556E3151E8E83F532100350BB17FC5BA4F87D98255BB1D36E2C6B7A7A3B7B89D27597AD6B6BC8FCACE884664CE6444664B4C13C497652023FAG5B15B8DFAEBDC172B166619365FE274D
	5B0E44AB094D1E13F2B4FA0F3C12590C5AA712643F5D06792BA0AFA752BF7FDEB55F8669C8CDBE2B5FE451569F9D10FF35CF7E22007A6FD173F387401AEC00B71F357309C6229B37F45EA3C128CB0243432A6FAEEE5C641757407D186527B692BF36E9624A9983884FF74E627A5A4C79758CE071DA74G3ED622F48BD53A6C017AF49183895D20F3C8D7GE9929F144333964085D02452B55810EE5EC096C3903AD48DDD49D504AE4F0EF499D7B1FEF9D731D81250EDD1691ED269A22E62FD71AA96EFA6DD4568D8
	BB629DC8B70767AF0167F2001CC13C3D0388DD2F9369F68EE271CC55A0D6EF73A0EDFB5FF329FA994CE21E02415EFA0BD43E4F2AF425BC3FF6309FDFCAF747F92433F13A4841ACDE0E9E2C259BA4A3DD62E0A6573C41AC36CE9D2C156F698E244B9C42E2B0520EB8DE2FD73E6BBA11AEFF880B57CA07B0FB548E516A2536CB7517A1AC16C33D748C514A7702CAB7FE08D73EA4F8DFB4C4AB5FFD5DC837FC880B897D65E37615FB54F68CE5764F9F4A5A3BE728563E153DC8D7BF54D7AF3621DA3E9BAFA1DD64D096
	FF923E8581FA1929520D752BAF51273EEBAE2B7EBC14453124BE52DE5F7AAA8524CB9D4A6C3BEAA8DB8B2D7229EFC39A522D0BE271B0290F50EF0B52567730CAD79E654D57564768C698102E11673B2218FDA3233476EDCDC73A18A896FB7B5B174DB5AFEEA7E3B95D93BBABDE90BB0A5C97E1ACAFABBBC4E79C365FF526B21353A7C4613E5650AA3A660134B02CA1ABF7984E158B56633ED67439B8E1713345EB026D5F6D9C4664289F661DABC7AC5F7BAA1BA30E1DA2BC23A1AFF6984E8FE71A34EB6B3DEE3753
	E42593999F9BDFBF291DCB99EE73493570EC334AEE259A021D8E9CC75FBEC964A56BFE0E667A1834F52B205DA3243B02FAD3AD5FB76ABD20EDEF7206DEC3D61076A671F6AE5234375CD93F066D3F14372D9358BC1CBB9C7AAA402061F47CEAA3318D99AB99BF532748EFC457C421F57418207C668DE7FC168D7772AB3C34F2B563172A725BF6B5787955486FFD8B61C746DC260F18AD25C2F8FB26A4E0E0F53C12DD92E69DCC5627AC381F9B8DF24E3B9AE5BBE2AE88DEEFD919DF70416A5AFF965B1DB182469CG
	962F7C08622D8E2BE29509FD8B36B39EBB9D9F40D3B41B8BEAB47D23E82F518DD132C9E11E716576865CC71C32259C75FF5BCDC226B84020372F9B752C45200CE0F699BB42EB1F1CD1467482GA66BDFD5D9D301A66F9A68EB94BF759C626B8197F58D36A157C6ECA74A16E29627D80FE8631499C03782A0F7A45217D3FA255D6AE63277B4216C917392055C4D43F4E5A7D0B812775D11DE59170CB7262F980F32DF36210C31C03398004934D55013A9E794362D520E658AG57BA8AE59D55C6E4F4EBE2DAA6738B
	FB357ACEDA9325ABF37BADB10618D1CC5614D1DE19E3A6985363A7204CD98E641D9D8D63E5B4725EEDA16B1C0038D9403879DA876A11557304DACF8454112456334709F4EC6D51EB522EBD16835D4E5190FBG340F46B2E6A7E90F454F15991FC94EF5FC5C0AG7A245158AFF23EA2CF3A7E91F0DCFB3C25095623C02F60359454687446BA7FD6FE39DD0F4C2FEFFD295A6365F7519E5BAAE205529567027A5072514C8E3957FA6D913F7E2555A9G2CFDD5AE54E3BB500C3F96F5F23B8B7D1C51EDBA0FFAD884B4
	5957A1DD6779603A7BAF35CC9550C728E58E48E8A316F64841EC4F7B220CB2A6C19D4B55FAEE6C709FCF785A29036C0738038CF72C4EF51DEA9F0801BE9003FDA3C54547GEEFE8C5A7B2BEF885E61146D226D1864765894466B0BAE32B7A3B948B2414C6B6E6C42F6E7DE8F317A75D8476A37489EFC3B68A6921182E861026DA7312DFF6BC67E9DD0AE61FA6C53DDA6ED1FDE8A781CB190F70EC1DDCC6AA6F330C596DD6D74C0G6B7B1DDADF8D50C60E41F65E500378E4402DD06BB17412D8C2B4B1BB586D2243
	2C7641CFFA898FBBD48FD389E894F9B8FA312E429BE0FDF2835675D2AF19B70D5E091D5317DDC27A31C03B70861469F9953FEEAC444BE3D59F3B7C9A360DC4CC2ADF5CFD9969FB00B6F1AC566390903F8CF05BFE0875BCA1780EA125E9C8578CB451AA5D543460BE7DE29A5AF69E50E75C08BC6FB2E0DDFBG275C08F56527E7EA6AE2386A74C01F991C0E7546C359549BF18F57DC69FF5616B74E3BAF3FD3730E404E20DE2131E345B8F68635DA452F2DA678C68D1E713F268A795BA0EF7EB8BCB7F136107133E4
	BC44CBG6DG91G6381D246E34CB861A459C76C73DC2A9AF4A8BBC4DB1D4FC6951E8F1DA2FAB9A7F5727429D35A311549712BB3447F97834F15G5993E07D81D0B581E5F86AD4C899AABBF4E5E0FABE50C6745CBE21FFE7A384D839FADAAE62DB14E3E37876135AB3780C0D078CC487FAE3F8822FBBE502F78CE7BA99A8921D88B7C15C82D082D083E03B89F5D258DC98CAA7F8864815415A747BB0F53172DB6B0215DBEA896F8C7C4FE1162BBE9B2ADC54CD3EEDE065CE04AC1760D70E59E931112C9367C0DEAA
	40AA005C1BA1AE86E8846839996D55517CE5C89FA61B4A754E3DFCFC765B4565F2261F8B2FFD9BC3169BFB33DE39BB4364B7B3E439C4BFFEEC9EFDA9D7E77DF8082C057609F23D53E2A17ECCA69D3E7F3C2FDA9B834E3B191D8B2F00E7E6AC681F0EFF630FE48A6CECA3BF96473EE6B331DBA4244783A481AC84D88910B391665E09E837071B7705321BFAE48E36433D7CC073F13D8C3005274FD1A14B55CE74AD475AFED2403637CF44F96730BDBC991AC2161BB7D12F0FAC207B622910A7CC027E81D082508820
	G44CCC2DDEFB99DD2572A027B236B0D0E705A79DE48F28913744A7DB2CCFE4F06ACB7E712DEDF69356E923E653EGBF87E1FD6729537EE7B7246FAC81DEABA6E15F19E6423D2BF537605ED536C8638FC8B783A83720DF45BB7D5B91F50B2F5C7A6D3FBB20DC11DFBA6E96BD3FBAEAA1FE1582F90B81D6GE44701EF815482A8F168D772693D217CCA530777833E4A69BC2763D6DC5E2FDD615939B9E43968B83DF2396743536B07816536782563F579BD93A63F97C3721B214B6FCEB94CB9A6E439453A7CB6F506
	47AFBFE439953A7CE60549EFE148F26BA66B153B5C951E7D068514AB72CBEF536577FEF7F86D5B91325C1E49FA63DE763EFCA1F05C23EB483232033814E4E35C0322F60C2B1F4C7608A26099BD99473829A63C0F9ABF9947326742EC534BA14B256A6AF0C2CFF87CEE8FD9EE15AEBF25B7BCFED704AC1773A33DF26517426377F548F2053A7CD604494FF0B9D4398ADDFEA9A14B6973FBB4E439E6DDFE0A90267DC216D3F479FD15969E3F1A5065F4793D94D02EFFE3589AC328B9A8D6175F7D81657A573EC7C216
	CB5665F7F5FAF86D7BE1C039A23F74C2BFFE6C5EECB7BD03DC86F9422D90F3D3DC63C1025B8669BD376238373A1A60DAA15D216232A90E1CB3103113612E2BEA8C51EE84D2CFDF792DBA790CC7C23FF92C8E111F2E03636B7216607739AE37685F679A5022FF1FEBE84BC60607BA343734B1FFD4CB5F773DEEE889FE5F6B19B34165DCF6C6DF4E974E684B3972CC5FF28A2D4164FC176A72E7F4BFD476B8942BDD22D533F9D96AA07792A11ABE66F130D01B546E765F0F92B0FE3F3A1D6C59C65D0AF17AF52D3B04
	40B83D52ECD51CB2D6D2AF09725136B6F571D76712CCA877F0B1380EC60B7ABA3AC15457518431EF9DCD967B56516F94F287B260AE5E57304A90F04FBD626CEB1C26D2F19EA9D66B7E2DB1F8BB1EB36A3763C5A3595BF7BBBD32C9721775FF0C39EA1EF7D5D32DC8F6A28E23C9B711720AA8DB2400362E2567ECACAFD8794D26E07AE0EDADBB27ED6BAE957F7C39AC0E2FED131D765AE29E6335DEFEC7503DD7413702F0FF230B0ECD246E38DBF1FD7A639A621FCBA03D92A0BB9E472C09C774461302F8B67E6C61
	4F5AF86FF8E406778E0028F85FB1EABC24937DF07C1E22B9386D6CE6FD5B394D41ED57E58EED3BAB4D41EDB7449C5AF6234C74AC47EA364A7CF41859A65966EB1BDDFCDCBEA36869F18E5747E27EDC2E51CF4614062C5CA90163FCA1604A816AB5F90C7795F67417DA45D96B6D9B3F14026B77A9C9DF3FCB25607A7D0394DA3FAB25607AFDC58A2D5F4CB6FD7DD6B934ED7CD4ED7BA667DA6D3870997A7ABBAD389753BE85FDFD1611787AF8C8A7814C1F023EFE3789630425BAFADF2503E3E7C811F4EFA77BB6F6
	0614266207D1FC1E864F4E165E9471EEC98164C58130716F21B7091DDCB024CAEE3A31DDA1AAA2EF5F53CE9F3F5A7FA957E513A538EDDBAD7A36ED3384372D5D925A3697AC41ED5BEB89ED5B01163E473D81748CDEB33E15B07C51735A71F037CA1FA7EB71DE1FF85E87DF22622BBBFD7B68279C7F57CEAD7DE7AA7D9DDD3E3AAF667846EEAD7D67822E35AD15441EBA63F01DC9F65AEC7C4C1679674BA22E23175F067EB9137AE7416D504F819AEFC77F3C4354D75C6C3ABD50BF191C5BFAFC5BDB42710BFA34FD
	49BB3F5C5A2BEDD7290A3FF849371E4FB87E634BDA7AAFD47A3FDC76257F9C6B97FC7A6ACE24772479526F66781D869FFA756E781FFD713B90BFAB5D57DE3BB8FEE4A53923EF93BB1D9E8574FDBB517564CA5C6B080474D80099G0BEEE77B3A4C1ED3AF127A5AE4518EE62441533C84D256F9904B16C23A8640869085901F00675DFF2673974E40452AEEBED9CDEC4AEEBC6E42D837FD3D36DF7038076FCFD5935B67CCE5B6AE186AEB6BBDFE69DAC833B16843A344174CD37D4F3D0BA93EE32A7F39F7D28D1EFB
	C7C15E7229386E19CD4F036AFF5C10E57EB1628A3FA6B89724872938595D84B79652C9AAEEF4AF414D0074C29557AA935CD2C82FD2F113A9AE7BA78DD905BFC15C0ED6DCEF152B381194E706F40F0A7B0100720DD0F1CF89A85FD895372E9765CBD0F1F6D53EE495F75209324CD7F1573423AC29AAEEC5BB4A12190838012789AE8F52BB93316F3E57625FF7AB923DB6EBCC8CB637141908BDEC097E762B247808C4FF7B4DB3227D22A1EF25AA53823ADF57BC8DE233E908CB9631BDD153D04EA122207A5CA84DFB
	1E585FF5E5B8403F277C8F70900CFD0D210B2785334397D45F4B27B1FD272B787D94AF5C6145B3BB4CB52A777AA0CF3983F57E0889F53E688E3CAB3320926301D500AB3993471D5F9611F1C11DC70A440BCAD959BEA779C625068DE2EC8EF97BC0A6AFEF3B9367100387C81D3110CE86D8F8A75E656D78906F7246CF3AC9187F0C7EDD69A5753D06E5F7B2DDE4A7E96EAE7E43184EF80EBE08BCEBA0BFB2096E9B9F545E49A2ED9F174E76DAD87E1FFC72C98CF0B8CD1B7FD115B61F487BC606B63F49A71F6868AA
	016533364D7DBB36ED555C8441B6ADB2686FB84C63EDDA0EEDFB747A34E5B1352FB29BDDCB57C785F74118F3975A6D3AC3F4FC01F43B0AFB33C66DBFAA6EEB0ACB02747C3B30CF955231B395522BD45CEE0A2B655F2DB05CFDB57D75FFD20F9EBEB5885E6B7FCF761BC7307DA13DFDA1ED5EE66157C7683DB288BCF36EA63F7171A489BDF95E7E2F49DCEA97BB2CF60F3D4E5AA589A36EA6FABBC56712C6F8B75F0DF3672F3B756E5E31BADFBF6F2DB33E17703342044F6A1ACE477305703EC42D6BA21DDB58DDDE
	494C68064B98FB155C03F4A9B261691225B6C916A5B315D060B27E1C52D7GEDAD4008FB886EC0AB46GBD77840B3FA63752B6DB9DFEEDFE004EE30B20DCAAGEBD7B18925F9FED69A1D03129B327212B13F5E6D4D3F1636338A726A81986ECABAEF45C1BAB199E50CEBC79917A4874A484A3DCBFF4FE585648B7722AEFEF8DAFBAF0F51A51E46FEDFF8AF7A7C67F40DD58E69FA9557ED443E51F1AF7AF7B99D7BE2A1BDC7459D26B87AAD5398447D5478DD4C8304873E47EA5E990F7F7ECEF8E87BC83A5FBB2C21
	CD41FDA94DCC5A3D8464DA86D0F39F41BDAED674418B679704DCA8D3887086CF175F67DDE7B29B3C255AA07ABE546D3FCC242F242A69EA1AA65F9A3174829F3D903AFFD7C701E3506FFDF874B51E0483ACB6CFD56F39E75C5F10158BD08A508850F1BF367549003B247D137F19B04BEDB970ED4B3198636D03E4AE0F395FFFFF61830A0F3D5FFFFF6148810CA792A0AFA7856FEF6DD608CEBAA09D8D908FB083A095C0F8G77137FE60565D85F77BE6A1D2E9A6902E453EC7132760DB978ED5B47666D8756603C1D
	F8770F04240195C13FAF4AF90079E389FFA64D3BBE0DBCD97B9E76107619A1AF82A096A099E0BE40D235FDA6AB3F83CA364877AA0AE8EA27DFEE299B6ED6692297CD96FB2CAFCF29A63236591C22427763D9BF1ED90D63612A87F05C3C6D90D9E79AE920066B4C1907509F3327B7E495GB483382633EFD1E226931ACFEA727B505520692C4D0C07D4C3F464142DA0A57B9E0D4953DB03F35A1269A84FDF8A08BF30EF3359C5C0061F7F99D9BF57D89DF8BD105B6331D35A3B3859E3EC8674AFFF19329FE4B265BD
	28791E084ED3DB4867ED38A75ACD4E939D5245FC8CE30BAA2C20DE7263B6853BCA8B73F715EEC75E63AFEAF9172E3DC2773BF782351C2719BF23DE391E737EE3171777E1792006B7FECAD5ED97ADD2B5848C85E41B0A9CE294ECAF0257E3925B79AA78E007151038F730082148E2ED7BE07BF6DE67C4763D066C11983F1C9EAF3FED2D7DE9AB0B21ED2D38B7B976C10C4F671C564630799F9F550DE1D71E6EB2AC646DDE2951FDC5DB49EBCC261EDE2FCC2F8A7D1109159BF749DBAE2D377F659A84EF39CAE7653F
	25F39BF8A4EA9C6B6FCC7356BF303D7FBAFE349DF5DCB883E3283186EFDD5D277BD397231DFB1A5453A65A5C5E7328A3AD01FBCA66966C6BE6605983908B10B4031D85333EBE5258D7DF1FBFC35B57DF7E064C71C12EA5F21AC9741BA04D8EE3B1EB6B7D695E36CE9573033735F867CEE86C4E1D5B5F2334825DBBE1FF4B7F9508DB72AFD6BFBD9E62FD77B76B50F773EE3DD928F8324230B5C85FCD656DAB1969755DC670DD96879599B18E324D8C0CBFF0AC42B8A882E822E7FA632038195E791035FB44B71B34
	7ABB687DF0067948DAB37AC862CCDC4B6EB37B2FE5FF6933D6D54C7EEBD9424F1BBF496C3F162DB4E87397197D5732D81E6F9711E10B3F97E85E6B8EFC7FFBA4366F15871B074B1560679687957DF30BA30A7E39F31D52779D01D3CA70BB82C6C57B1BB0AC0EB0B7915BAC83DF481C85B1B6C0B9C07DAC0CA116AB647BDCF6335ABFFE28B4DB48398979C5B98579BF6E895E56DFF8C27F8E5D539EB2CF9B1DB0C75BC97D08FF56C36C25B8DD9449F04FFB080EE42B25DDE1249C3F42C3FC5CA635D1B45F8FBF249D
	4FD86C785CA15C37F2C1FB63E6295F6A65135FCD72BF53A8E2C1CED9D925E2EA174C7BF848437B55E707B1B6DAB28B7725CC07C97D3933E1AC84281A0DB14A219AB2271E105C8A7F7D9BC9BB16643DD7EE40BE666FCF2D33797CBF5BBB16F8474E689AB286E9C52EA37F22095CC58D15476A2B776F2D61361FC53EBC64BA0B3CC05A5CA01E553DD5F1886ABCEABC8B51C13DE8D1FDA7017E06C08A4832F8369AF3F6FC9C2C9EC0564ACE17A4AB1D8CCBFFF9057743732D5AF8FA955417BD276F759813E1D1B319FF
	3681EDB900992087E084C05C9C14293127208F195876A2D512ACDA9D8A136771F62DBC49D0577C9072B01F0AEA4175C8AA5067BD04BED55C1AA7846CEF9E142D66FD2205C9407CE8E18B7AD17DC368C7F36899EA9424E381129FD23F53B51279F83FC43E2FC3FD6CE9A17349C220D98E1073B074E900F200C6G9BC084C0B4C0BC408C00C5G4BG0439408BE09BC069DC545D48466DFD680EB7402BBC35BF1CB162BCD383F575EF1E4DA9E773ECEEABC9B1BBECB0EB6D9089F5454C6D5B8E7CF71F1A020FC36E26
	5063D0CF937D2D8B98BF6ABBDD7CAC7787CDC45F4E36B637240EF7831B32D5DC353B56693612858A3F6FFEA8549AB15E2FAD2CDC5A61D0657C6FAC32F2E5A14B7983DBDBBFC94729B9F37D575CCBA8FE61DC7FB53775B07A7892482B1807EB6E3F366643B38E52B3G1682880F008F8294821483B4BE02FE15FB322FBE4986D72FDB31B253CE911955390F8D9F5C8E492748BC26FE8FCE17096CCBE536BFB4082EBF94607F0D00FBC643A8AE0A63D8BD3FE98E6EB7CB1BC37B4D9F1B75672F3FB4874EDF2B1A7567
	AF43E96D7C45E47DFDFB207CFFE8574A4FFCE0E44059DB7F622071E1169BA70667F3FB6819D15CA37EBED7C971090F787B5C236ABD1294485B76A87AD1ED9F77B3CF340436D7EB0B3E3D5ADA826DF52EC55FDE67DB34766277C6DA4858E7017131D1256BD6F1CDAAAE2265B50EBBE1B5AB6DAAFEE04BDA0E2F1288C344C7D139AED2DCA347DD2341B5F15C35F42D86CDEEF74A5E7B23A46EE8335AECACBD0E52D89DB0F4297A0825B2DADD22C66E49AA2EC9353B5967ECF19058E36C23FD0F4B4CE7BFB4239FCFC9
	C75C47E6746319E9F8BFB2E573FAAA9BD99F6FE73FC02C727E957989D17E7EDB4D3B7370773FDE98743D726077417A38273AC18C6D5339223ECFEF9183FDFA2B286F535B45C01FAE62754A6AC5D506AF91092D583E409151A1DAE8DC4C629E7143F502BAAED397E7783C0E75DEBC3AB85771BE23572607B56F8B7F83E7664FFD2FBC3467C58A9F6398AE4208FD0060587FB1B041D0856B6878670455209BC5F8156D4F51778C52AF5C3230A9F0F7E36F701FBF99FF26A247A0982A05D75B29EA2B055597096EBB48
	0FB813C0B48F4AEEB5B96DC5D6233D48F341DEA49A15A2896660A2E2D8333AFFF57241562476075E7840A02C2E96325A2C124DDCC7F730EA04EC1A2A105CA6596AA2532DA2EC223F613443A6BAACBB58647BE50D301152614E97297E3CF0C9D8D3A3EC56D4F740EA139C5E2C57B5D9470F55A8426B2CE607246C007417349DE90A70CED1D1B37D3A2B19ED27B5FB37B0C11A40FDCDD27DDA171BBCB2D4CD04CA3D4AF8657A73422DBBC548765F4DA345327B47E78E00992C31C135E4B59D1017DC7834AC7D691B53
	8DC2861811F566EA30BB74C830264C2C1A8E1D196A42670F56CD3608B23440FFFB6232F64FF965GD350BD670485ECBC1F31407F37A7465058D878A92CEFFE0A7370BD99643705032D9F6B6C224D260836F3FB9DE632165C5729C85EBD0BF1CD24FEB75270CC16B7290948FDD6D366FF81D0CB8788FBAD7DE8079FGG5CE3GGD0CB818294G94G88G88G4B0171B4FBAD7DE8079FGG5CE3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBA
	GGG419FGGGG
**end of data**/
}
}