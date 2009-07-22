package cbit.vcell.mapping.gui;

import org.vcell.util.Coordinate;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.TableCellEditorAutoCompletion;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ScopedExpression;
import cbit.vcell.mapping.CurrentClampStimulus;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.Electrode;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.VoltageClampStimulus;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Feature;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
/**
 * Insert the type's description here.
 * Creation date: (10/26/2004 9:58:14 PM)
 * @author: Jim Schaff
 */
public class ElectricalStimulusPanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjparameterTable = null;
	private JSortTable ivjScrollPaneTable = null;
	private SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP1Aligning = false;
	private ElectricalStimulus ivjelectricalStimulus = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimulationContext ivjsimulationContext1 = null;
	private ElectricalStimulusParameterTableModel ivjelectricalStimulusParameterTableModel = null;
	private javax.swing.JRadioButton ivjCurrentClampRadioButton = null;
	private GeometryContext ivjgeometryContext = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JRadioButton ivjNoClampRadioButton = null;
	private javax.swing.JRadioButton ivjVoltageClampRadioButton = null;
	private javax.swing.ButtonGroup ivjbuttonGroup = null;
	private javax.swing.JLabel ivjgroundElectrodeLabel = null;
	private ElectrodePanel ivjgroundElectrodePanel = null;
	private javax.swing.JLabel ivjpatchElectrodeLabel = null;
	private ElectrodePanel ivjpatchElectrodePanel = null;
	private javax.swing.JPanel ivjJPanel2 = null;

class IvjEventHandler implements java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == ElectricalStimulusPanel.this.getNoClampRadioButton()) 
				connEtoC4(e);
			if (e.getSource() == ElectricalStimulusPanel.this.getVoltageClampRadioButton()) 
				connEtoC5(e);
			if (e.getSource() == ElectricalStimulusPanel.this.getCurrentClampRadioButton()) 
				connEtoC6(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ElectricalStimulusPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == ElectricalStimulusPanel.this.getsimulationContext1() && (evt.getPropertyName().equals("groundElectrode"))) 
				connEtoM2(evt);
			if (evt.getSource() == ElectricalStimulusPanel.this.getsimulationContext1() && (evt.getPropertyName().equals("electricalStimuli"))) 
				connEtoM4(evt);
			if (evt.getSource() == ElectricalStimulusPanel.this.getelectricalStimulus() && (evt.getPropertyName().equals("electrode"))) 
				connEtoM5(evt);
			if (evt.getSource() == ElectricalStimulusPanel.this.getsimulationContext1() && (evt.getPropertyName().equals("electricalStimuli"))) 
				connEtoM9(evt);
			if (evt.getSource() == ElectricalStimulusPanel.this.getgeometryContext() && (evt.getPropertyName().equals("geometry"))) 
				connEtoM12(evt);
			if (evt.getSource() == ElectricalStimulusPanel.this.getgeometryContext() && (evt.getPropertyName().equals("geometry"))) 
				connEtoM14(evt);
			if (evt.getSource() == ElectricalStimulusPanel.this.getsimulationContext1() && (evt.getPropertyName().equals("electricalStimuli"))) 
				connEtoC2(evt);
			if (evt.getSource() == ElectricalStimulusPanel.this.getsimulationContext1() && (evt.getPropertyName().equals("electricalStimuli"))) 
				connEtoC8(evt);
			if (evt.getSource() == ElectricalStimulusPanel.this.getsimulationContext1() && (evt.getPropertyName().equals("model"))) 
				connEtoM19(evt);
			if (evt.getSource() == ElectricalStimulusPanel.this.getsimulationContext1() && (evt.getPropertyName().equals("model"))) 
				connEtoM21(evt);
		};
	};

/**
 * CurrentClampStimulusPanel constructor comment.
 */
public ElectricalStimulusPanel() {
	super();
	initialize();
}

/**
 * CurrentClampStimulusPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ElectricalStimulusPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * CurrentClampStimulusPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ElectricalStimulusPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * CurrentClampStimulusPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ElectricalStimulusPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoC1:  (simulationContext1.this --> ElectricalStimulusPanel.setRadioButtons(Lcbit.vcell.mapping.SimulationContext;)V)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		this.setRadioButtons(getsimulationContext1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (simulationContext1.electricalStimuli --> ElectricalStimulusPanel.setRadioButtons(Lcbit.vcell.mapping.SimulationContext;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getsimulationContext1() != null)) {
			this.setRadioButtons(getsimulationContext1());
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
 * connEtoC3:  (ElectricalStimulusPanel.initialize() --> ElectricalStimulusPanel.setRadioButtons(Lcbit.vcell.mapping.SimulationContext;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
	try {
		// user code begin {1}
		// user code end
		if ((getsimulationContext1() != null)) {
			this.setRadioButtons(getsimulationContext1());
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
 * connEtoC4:  (NoClampRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ElectricalStimulusPanel.newStimulus(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.newStimulus(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (VoltageClampRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ElectricalStimulusPanel.newStimulus(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.newStimulus(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (CurrentClampRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ElectricalStimulusPanel.newStimulus(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.newStimulus(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (simulationContext1.this --> ElectricalStimulusPanel.setPanelsVisible()V)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		this.setPanelsVisible();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (simulationContext1.electricalStimuli --> ElectricalStimulusPanel.setPanelsVisible()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setPanelsVisible();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM1:  (simulationContext1.this --> ElectrodePanel.electrode)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getsimulationContext1() != null)) {
			getgroundElectrodePanel().setElectrode(getsimulationContext1().getGroundElectrode());
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
 * connEtoM10:  (geometryContext.this --> electrodePanel.geometry)
 * @param value cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(GeometryContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getgeometryContext() != null)) {
			getpatchElectrodePanel().setGeometry(getgeometryContext().getGeometry());
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
 * connEtoM11:  (simulationContext1.this --> geometryContext.this)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getsimulationContext1() != null)) {
			setgeometryContext(getsimulationContext1().getGeometryContext());
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
 * connEtoM12:  (geometryContext.geometry --> electrodePanel.geometry)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getpatchElectrodePanel().setGeometry(getgeometryContext().getGeometry());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM13:  (geometryContext.this --> ElectrodePanel.geometry)
 * @param value cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM13(GeometryContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getgeometryContext() != null)) {
			getgroundElectrodePanel().setGeometry(getgeometryContext().getGeometry());
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
 * connEtoM14:  (geometryContext.geometry --> ElectrodePanel.geometry)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM14(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getgroundElectrodePanel().setGeometry(getgeometryContext().getGeometry());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM15:  (ElectricalStimulusPanel.initialize() --> buttonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM15() {
	try {
		// user code begin {1}
		// user code end
		getbuttonGroup().add(getVoltageClampRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM16:  (ElectricalStimulusPanel.initialize() --> buttonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM16() {
	try {
		// user code begin {1}
		// user code end
		getbuttonGroup().add(getNoClampRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM17:  (ElectricalStimulusPanel.initialize() --> buttonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM17() {
	try {
		// user code begin {1}
		// user code end
		getbuttonGroup().add(getCurrentClampRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM18:  (simulationContext1.this --> patchElectrodePanel.model)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM18(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getsimulationContext1() != null)) {
			getpatchElectrodePanel().setModel(getsimulationContext1().getModel());
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
 * connEtoM19:  (simulationContext1.model --> patchElectrodePanel.model)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM19(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getpatchElectrodePanel().setModel(getsimulationContext1().getModel());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (simulationContext1.groundElectrode --> ElectrodePanel.electrode)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getgroundElectrodePanel().setElectrode(getsimulationContext1().getGroundElectrode());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM20:  (simulationContext1.this --> groundElectrodePanel.model)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM20(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getsimulationContext1() != null)) {
			getgroundElectrodePanel().setModel(getsimulationContext1().getModel());
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
 * connEtoM21:  (simulationContext1.model --> groundElectrodePanel.model)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM21(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getgroundElectrodePanel().setModel(getsimulationContext1().getModel());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM3:  (simulationContext1.this --> electricalStimulus.this)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		setelectricalStimulus(this.getElectricalStimulus(getsimulationContext1()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (simulationContext1.electricalStimuli --> electricalStimulus.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setelectricalStimulus(this.getElectricalStimulus(getsimulationContext1()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (electricalStimulus.electrode --> electrodePanel.electrode)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getpatchElectrodePanel().setElectrode(getelectricalStimulus().getElectrode());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (electricalStimulus.this --> electrodePanel.electrode)
 * @param value cbit.vcell.mapping.ElectricalStimulus
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(ElectricalStimulus value) {
	try {
		// user code begin {1}
		// user code end
		if ((getelectricalStimulus() != null)) {
			getpatchElectrodePanel().setElectrode(getelectricalStimulus().getElectrode());
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
 * connEtoM7:  (CurrentClampStimulusPanel.initialize() --> ScrollPaneTable.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7() {
	try {
		// user code begin {1}
		// user code end
		getScrollPaneTable().setModel(getelectricalStimulusParameterTableModel());
		getScrollPaneTable().createDefaultColumnsFromModel();
		getScrollPaneTable().setDefaultEditor(ScopedExpression.class, new TableCellEditorAutoCompletion(getScrollPaneTable(), false));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM8:  (simulationContext1.this --> electricalStimulusParameterTableModel.electricalStimulus)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		getelectricalStimulusParameterTableModel().setElectricalStimulus(this.getElectricalStimulus(getsimulationContext1()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM9:  (simulationContext1.electricalStimuli --> electricalStimulusParameterTableModel.electricalStimulus)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getelectricalStimulusParameterTableModel().setElectricalStimulus(this.getElectricalStimulus(getsimulationContext1()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (ElectricalStimulusPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getsimulationContext1() != null)) {
				this.setSimulationContext(getsimulationContext1());
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
 * connPtoP1SetTarget:  (ElectricalStimulusPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setsimulationContext1(this.getSimulationContext());
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
 * Return the buttonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getbuttonGroup() {
	if (ivjbuttonGroup == null) {
		try {
			ivjbuttonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjbuttonGroup;
}


/**
 * Return the CurrentClampRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getCurrentClampRadioButton() {
	if (ivjCurrentClampRadioButton == null) {
		try {
			ivjCurrentClampRadioButton = new javax.swing.JRadioButton();
			ivjCurrentClampRadioButton.setName("CurrentClampRadioButton");
			ivjCurrentClampRadioButton.setText("Current Clamp");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurrentClampRadioButton;
}


/**
 * Return the electricalStimulus property value.
 * @return cbit.vcell.mapping.ElectricalStimulus
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ElectricalStimulus getelectricalStimulus() {
	// user code begin {1}
	// user code end
	return ivjelectricalStimulus;
}


/**
 * Comment
 */
public ElectricalStimulus getElectricalStimulus(SimulationContext simContext) {
	if (simContext==null){
		return null;
	}
	ElectricalStimulus electricalStimuli[] = simContext.getElectricalStimuli();
	if (electricalStimuli!=null && electricalStimuli.length>0){
		return electricalStimuli[0];
	}
	return null;
}


/**
 * Return the electricalStimulusParameterTableModel property value.
 * @return cbit.vcell.mapping.gui.ElectricalStimulusParameterTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ElectricalStimulusParameterTableModel getelectricalStimulusParameterTableModel() {
	if (ivjelectricalStimulusParameterTableModel == null) {
		try {
			ivjelectricalStimulusParameterTableModel = new ElectricalStimulusParameterTableModel(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjelectricalStimulusParameterTableModel;
}


/**
 * Return the geometryContext property value.
 * @return cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometryContext getgeometryContext() {
	// user code begin {1}
	// user code end
	return ivjgeometryContext;
}


/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getgroundElectrodeLabel() {
	if (ivjgroundElectrodeLabel == null) {
		try {
			ivjgroundElectrodeLabel = new javax.swing.JLabel();
			ivjgroundElectrodeLabel.setName("groundElectrodeLabel");
			ivjgroundElectrodeLabel.setText("ground electrode");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjgroundElectrodeLabel;
}

/**
 * Return the ElectrodePanel property value.
 * @return cbit.vcell.mapping.gui.ElectrodePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ElectrodePanel getgroundElectrodePanel() {
	if (ivjgroundElectrodePanel == null) {
		try {
			ivjgroundElectrodePanel = new ElectrodePanel();
			ivjgroundElectrodePanel.setName("groundElectrodePanel");
			ivjgroundElectrodePanel.setPreferredSize(new java.awt.Dimension(545, 20));
			ivjgroundElectrodePanel.setMinimumSize(new java.awt.Dimension(287, 20));
			ivjgroundElectrodePanel.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjgroundElectrodePanel;
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
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNoClampRadioButton = new java.awt.GridBagConstraints();
			constraintsNoClampRadioButton.gridx = 1; constraintsNoClampRadioButton.gridy = 1;
			getJPanel1().add(getNoClampRadioButton(), constraintsNoClampRadioButton);

			java.awt.GridBagConstraints constraintsVoltageClampRadioButton = new java.awt.GridBagConstraints();
			constraintsVoltageClampRadioButton.gridx = 2; constraintsVoltageClampRadioButton.gridy = 1;
			getJPanel1().add(getVoltageClampRadioButton(), constraintsVoltageClampRadioButton);

			java.awt.GridBagConstraints constraintsCurrentClampRadioButton = new java.awt.GridBagConstraints();
			constraintsCurrentClampRadioButton.gridx = 3; constraintsCurrentClampRadioButton.gridy = 1;
			constraintsCurrentClampRadioButton.insets = new java.awt.Insets(0, 0, 0, 2);
			getJPanel1().add(getCurrentClampRadioButton(), constraintsCurrentClampRadioButton);
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
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintspatchElectrodeLabel = new java.awt.GridBagConstraints();
			constraintspatchElectrodeLabel.gridx = 0; constraintspatchElectrodeLabel.gridy = 0;
			getJPanel2().add(getpatchElectrodeLabel(), constraintspatchElectrodeLabel);

			java.awt.GridBagConstraints constraintspatchElectrodePanel = new java.awt.GridBagConstraints();
			constraintspatchElectrodePanel.gridx = 0; constraintspatchElectrodePanel.gridy = 1;
			constraintspatchElectrodePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintspatchElectrodePanel.weightx = 1.0;
			constraintspatchElectrodePanel.weighty = 1.0;
			constraintspatchElectrodePanel.insets = new java.awt.Insets(0, 4, 4, 4);
			getJPanel2().add(getpatchElectrodePanel(), constraintspatchElectrodePanel);

			java.awt.GridBagConstraints constraintsgroundElectrodeLabel = new java.awt.GridBagConstraints();
			constraintsgroundElectrodeLabel.gridx = 0; constraintsgroundElectrodeLabel.gridy = 2;
			constraintsgroundElectrodeLabel.insets = new java.awt.Insets(4, 4, 0, 4);
			getJPanel2().add(getgroundElectrodeLabel(), constraintsgroundElectrodeLabel);

			java.awt.GridBagConstraints constraintsgroundElectrodePanel = new java.awt.GridBagConstraints();
			constraintsgroundElectrodePanel.gridx = 0; constraintsgroundElectrodePanel.gridy = 3;
			constraintsgroundElectrodePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsgroundElectrodePanel.weightx = 1.0;
			constraintsgroundElectrodePanel.weighty = 1.0;
			constraintsgroundElectrodePanel.insets = new java.awt.Insets(0, 4, 4, 4);
			getJPanel2().add(getgroundElectrodePanel(), constraintsgroundElectrodePanel);

			java.awt.GridBagConstraints constraintsparameterTable = new java.awt.GridBagConstraints();
			constraintsparameterTable.gridx = 0; constraintsparameterTable.gridy = 4;
			constraintsparameterTable.fill = java.awt.GridBagConstraints.BOTH;
			constraintsparameterTable.weightx = 1.0;
			constraintsparameterTable.weighty = 1.0;
			constraintsparameterTable.insets = new java.awt.Insets(0, 4, 4, 4);
			getJPanel2().add(getparameterTable(), constraintsparameterTable);
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
 * Return the NoClampRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getNoClampRadioButton() {
	if (ivjNoClampRadioButton == null) {
		try {
			ivjNoClampRadioButton = new javax.swing.JRadioButton();
			ivjNoClampRadioButton.setName("NoClampRadioButton");
			ivjNoClampRadioButton.setText("No Clamp");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNoClampRadioButton;
}


/**
 * Return the parameterTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getparameterTable() {
	if (ivjparameterTable == null) {
		try {
			ivjparameterTable = new javax.swing.JScrollPane();
			ivjparameterTable.setName("parameterTable");
			ivjparameterTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjparameterTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getparameterTable().setViewportView(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjparameterTable;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getpatchElectrodeLabel() {
	if (ivjpatchElectrodeLabel == null) {
		try {
			ivjpatchElectrodeLabel = new javax.swing.JLabel();
			ivjpatchElectrodeLabel.setName("patchElectrodeLabel");
			ivjpatchElectrodeLabel.setText("patch electrode");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjpatchElectrodeLabel;
}

/**
 * Return the electrodePanel property value.
 * @return cbit.vcell.mapping.gui.ElectrodePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ElectrodePanel getpatchElectrodePanel() {
	if (ivjpatchElectrodePanel == null) {
		try {
			ivjpatchElectrodePanel = new ElectrodePanel();
			ivjpatchElectrodePanel.setName("patchElectrodePanel");
			ivjpatchElectrodePanel.setPreferredSize(new java.awt.Dimension(545, 20));
			ivjpatchElectrodePanel.setMinimumSize(new java.awt.Dimension(287, 20));
			ivjpatchElectrodePanel.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjpatchElectrodePanel;
}

/**
 * Return the ScrollPaneTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getparameterTable().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			ivjScrollPaneTable.setRowHeight(ivjScrollPaneTable.getRowHeight() + 2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
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
private SimulationContext getsimulationContext1() {
	// user code begin {1}
	// user code end
	return ivjsimulationContext1;
}


/**
 * Return the VoltageClampRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getVoltageClampRadioButton() {
	if (ivjVoltageClampRadioButton == null) {
		try {
			ivjVoltageClampRadioButton = new javax.swing.JRadioButton();
			ivjVoltageClampRadioButton.setName("VoltageClampRadioButton");
			ivjVoltageClampRadioButton.setText("Voltage Clamp");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVoltageClampRadioButton;
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
	getNoClampRadioButton().addItemListener(ivjEventHandler);
	getVoltageClampRadioButton().addItemListener(ivjEventHandler);
	getCurrentClampRadioButton().addItemListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
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
		setName("CurrentClampStimulusPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(567, 393);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 8, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 1;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.weightx = 1.0;
		constraintsJPanel2.weighty = 1.0;
		constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel2(), constraintsJPanel2);
		initConnections();
		connEtoM7();
		connEtoM16();
		connEtoM15();
		connEtoM17();
		connEtoC3();
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
		ElectricalStimulusPanel aElectricalStimulusPanel;
		aElectricalStimulusPanel = new ElectricalStimulusPanel();
		frame.setContentPane(aElectricalStimulusPanel);
		frame.setSize(aElectricalStimulusPanel.getSize());
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
 * Comment
 */
private void newStimulus(java.awt.event.ItemEvent itemEvent) {
	try {
		SimulationContext simContext = getSimulationContext();
		if (simContext == null){
			return;
		}

		//
		// When the voltage and current clamp radio buttons is deselected within the same simulation context (application),
		// display a warning saying that the present clamp settings will be lost (not applicable when the 'no clamp'
		// radiobutton is deselected.
		//
		
		ElectricalStimulus currElectricalStimulus = null;
		if (simContext.getElectricalStimuli()!=null && simContext.getElectricalStimuli().length>0){
			currElectricalStimulus = simContext.getElectricalStimuli()[0];
		}

		//
		// ignore selection if already selected
		// warn upon deselect if about to loose edits
		//
		if (itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED){
			if (currElectricalStimulus instanceof VoltageClampStimulus){
				if (itemEvent.getSource()==getVoltageClampRadioButton()){
					return;
				}else{
					String response = PopupGenerator.showWarningDialog(this,"warning: the present voltage clamp settings will be lost", new String[] { UserMessage.OPTION_CONTINUE, UserMessage.OPTION_CANCEL }, UserMessage.OPTION_CONTINUE);
					if (response==null || response.equals(UserMessage.OPTION_CANCEL)){
						getVoltageClampRadioButton().setSelected(true); // revert back to Voltage Clamp
						return;
					}
				}
			}
			if (currElectricalStimulus instanceof CurrentClampStimulus){
				if (itemEvent.getSource()==getCurrentClampRadioButton()) {
					return;
				}else{
					String response = PopupGenerator.showWarningDialog(this,"warning: the present current clamp settings will be lost", new String[] { UserMessage.OPTION_CONTINUE, UserMessage.OPTION_CANCEL }, UserMessage.OPTION_CONTINUE);
					if (response==null || response.equals(UserMessage.OPTION_CANCEL)){
						getCurrentClampRadioButton().setSelected(true); // revert back to Current Clamp
						return;
					}
				}
			}
			if (currElectricalStimulus == null && itemEvent.getSource()==getNoClampRadioButton()){
				return;
			}
		}else{
			return;
		}

		Feature topFeature = simContext.getModel().getTopFeature();
		Feature innerFeature = topFeature; // start here, but look for inner feature
		//
		// ground initialized to topFeature ... e.g. extracellular
		// then (probe) placed in innerFeature who is direct child (cytosol?)
		//
		Structure structures[] = simContext.getModel().getStructures();
		for (int i = 0; i < structures.length; i++){
			if (structures[i] instanceof Feature){
				Feature feature = (Feature)structures[i];
				if (feature.getParentStructure() != null && feature.getParentStructure().getParentStructure() == topFeature){
					innerFeature = (Feature)structures[i];
				}
			}
		}
		//
		// selected "Current Clamp Stimulus"
		//
		if (itemEvent.getSource()==getCurrentClampRadioButton()){
			if (simContext.getElectricalStimuli().length==0 || !(simContext.getElectricalStimuli()[0] instanceof CurrentClampStimulus)){
				Electrode probeElectrode = new Electrode(innerFeature,new Coordinate(0,0,0));
				CurrentClampStimulus ccStimulus = new CurrentClampStimulus(probeElectrode,"ccElectrode",new Expression(0.0),simContext);
		System.out.println(" Geo's dim = "+simContext.getGeometry().getDimension());
				simContext.setElectricalStimuli(new ElectricalStimulus[] { ccStimulus });
				simContext.setGroundElectrode(new Electrode(topFeature,new Coordinate(0,0,0)));
			}
		}
		//
		// selected "NO Electrical Stimulus"
		//
		if (itemEvent.getSource()==getNoClampRadioButton()){
			if (simContext.getElectricalStimuli().length>0){
				simContext.setElectricalStimuli(new ElectricalStimulus[0]);
			}
		}
		//
		// selected "Voltage Clamp Stimulus"
		//
		if (itemEvent.getSource()==getVoltageClampRadioButton()){
			if (simContext.getElectricalStimuli().length==0 || !(simContext.getElectricalStimuli()[0] instanceof VoltageClampStimulus)){
				Electrode probeElectrode = new Electrode(innerFeature,new Coordinate(0,0,0));
				VoltageClampStimulus vcStimulus = new VoltageClampStimulus(probeElectrode,"vcElectrode",new Expression(0.0), simContext);
		System.out.println(" Geo's dim = "+simContext.getGeometry().getDimension());
				simContext.setElectricalStimuli(new ElectricalStimulus[] { vcStimulus });
				simContext.setGroundElectrode(new Electrode(topFeature,new Coordinate(0,0,0)));
			}
		}
	}catch (java.beans.PropertyVetoException e){
		PopupGenerator.showErrorDialog(this,"Error setting electrical stimulus: "+e.getMessage());
	}
}

/**
 * Set the electricalStimulus to a new value.
 * @param newValue cbit.vcell.mapping.ElectricalStimulus
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setelectricalStimulus(ElectricalStimulus newValue) {
	if (ivjelectricalStimulus != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjelectricalStimulus != null) {
				ivjelectricalStimulus.removePropertyChangeListener(ivjEventHandler);
			}
			ivjelectricalStimulus = newValue;

			/* Listen for events from the new object */
			if (ivjelectricalStimulus != null) {
				ivjelectricalStimulus.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoM6(ivjelectricalStimulus);
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
 * Set the geometryContext to a new value.
 * @param newValue cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometryContext(GeometryContext newValue) {
	if (ivjgeometryContext != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjgeometryContext != null) {
				ivjgeometryContext.removePropertyChangeListener(ivjEventHandler);
			}
			ivjgeometryContext = newValue;

			/* Listen for events from the new object */
			if (ivjgeometryContext != null) {
				ivjgeometryContext.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoM10(ivjgeometryContext);
			connEtoM13(ivjgeometryContext);
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
private void setPanelsVisible() {
	boolean bHasStimulus = (getElectricalStimulus(getSimulationContext()) != null);
	getpatchElectrodePanel().setVisible(bHasStimulus);
	getgroundElectrodePanel().setVisible(bHasStimulus);
	getparameterTable().setVisible(bHasStimulus);
	getpatchElectrodeLabel().setVisible(bHasStimulus);
	getgroundElectrodeLabel().setVisible(bHasStimulus);
	return;
}


/**
 * Comment
 */
public void setRadioButtons(SimulationContext arg1) {
	if (arg1==null || arg1.getElectricalStimuli()==null || arg1.getElectricalStimuli().length==0){
		getNoClampRadioButton().setSelected(true);
	}else if (arg1.getElectricalStimuli()[0] instanceof CurrentClampStimulus){
		getCurrentClampRadioButton().setSelected(true);
	}else if (arg1.getElectricalStimuli()[0] instanceof VoltageClampStimulus){
		getVoltageClampRadioButton().setSelected(true);
	}
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
private void setsimulationContext1(SimulationContext newValue) {
	if (ivjsimulationContext1 != newValue) {
		try {
			SimulationContext oldValue = getsimulationContext1();
			/* Stop listening for events from the current object */
			if (ivjsimulationContext1 != null) {
				ivjsimulationContext1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjsimulationContext1 = newValue;

			/* Listen for events from the new object */
			if (ivjsimulationContext1 != null) {
				ivjsimulationContext1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connEtoM1(ivjsimulationContext1);
			connEtoM3(ivjsimulationContext1);
			connEtoM8(ivjsimulationContext1);
			connEtoM11(ivjsimulationContext1);
			connEtoC1(ivjsimulationContext1);
			connEtoC7(ivjsimulationContext1);
			connEtoM18(ivjsimulationContext1);
			connEtoM20(ivjsimulationContext1);
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

}