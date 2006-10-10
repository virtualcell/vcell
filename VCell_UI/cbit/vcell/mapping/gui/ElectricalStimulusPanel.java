package cbit.vcell.mapping.gui;

import org.vcell.expression.ExpressionFactory;

import cbit.util.Coordinate;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Feature;
import cbit.vcell.modelapp.CurrentClampStimulus;
import cbit.vcell.modelapp.ElectricalStimulus;
import cbit.vcell.modelapp.Electrode;
import cbit.vcell.modelapp.GeometryContext;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.VoltageClampStimulus;
import cbit.vcell.client.server.UserMessage;
/**
 * Insert the type's description here.
 * Creation date: (10/26/2004 9:58:14 PM)
 * @author: Jim Schaff
 */
public class ElectricalStimulusPanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjparameterTable = null;
	private cbit.vcell.messaging.admin.sorttable.JSortTable ivjScrollPaneTable = null;
	private cbit.vcell.modelapp.SimulationContext fieldSimulationContext = null;
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
	private java.awt.Component ivjComponent1 = null;
	private javax.swing.DefaultCellEditor ivjDefaultCellEditor1 = null;
	private long lastTimeRequestCellEditorFocus = 0l;

class IvjEventHandler implements java.awt.event.FocusListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == ElectricalStimulusPanel.this.getComponent1()) 
				connEtoC9(e);
		};
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
			if (evt.getSource() == ElectricalStimulusPanel.this.getScrollPaneTable() && (evt.getPropertyName().equals("cellEditor"))) 
				connEtoM22(evt);
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
private void connEtoC1(cbit.vcell.modelapp.SimulationContext value) {
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
private void connEtoC7(cbit.vcell.modelapp.SimulationContext value) {
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
 * connEtoC9:  (Component1.focus.focusLost(java.awt.event.FocusEvent) --> ElectricalStimulusPanel.stopCellEditing()V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.stopCellEditing();
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
private void connEtoM1(cbit.vcell.modelapp.SimulationContext value) {
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
private void connEtoM10(cbit.vcell.modelapp.GeometryContext value) {
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
private void connEtoM11(cbit.vcell.modelapp.SimulationContext value) {
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
private void connEtoM13(cbit.vcell.modelapp.GeometryContext value) {
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
private void connEtoM18(cbit.vcell.modelapp.SimulationContext value) {
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
private void connEtoM20(cbit.vcell.modelapp.SimulationContext value) {
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
 * connEtoM22:  (ScrollPaneTable.cellEditor --> DefaultCellEditor1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM22(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setDefaultCellEditor1((javax.swing.DefaultCellEditor)getScrollPaneTable().getCellEditor());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM23:  (DefaultCellEditor1.this --> Component1.this)
 * @param value javax.swing.DefaultCellEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM23(javax.swing.DefaultCellEditor value) {
	try {
		// user code begin {1}
		// user code end
		if ((getDefaultCellEditor1() != null)) {
			setComponent1(getDefaultCellEditor1().getComponent());
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
 * connEtoM3:  (simulationContext1.this --> electricalStimulus.this)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.modelapp.SimulationContext value) {
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
private void connEtoM6(cbit.vcell.modelapp.ElectricalStimulus value) {
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
private void connEtoM8(cbit.vcell.modelapp.SimulationContext value) {
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
 * Return the Component1 property value.
 * @return java.awt.Component
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Component getComponent1() {
	// user code begin {1}
	// user code end
	return ivjComponent1;
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
 * Return the DefaultCellEditor1 property value.
 * @return javax.swing.DefaultCellEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.DefaultCellEditor getDefaultCellEditor1() {
	// user code begin {1}
	// user code end
	return ivjDefaultCellEditor1;
}


/**
 * Return the electricalStimulus property value.
 * @return cbit.vcell.mapping.ElectricalStimulus
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.modelapp.ElectricalStimulus getelectricalStimulus() {
	// user code begin {1}
	// user code end
	return ivjelectricalStimulus;
}


/**
 * Comment
 */
public cbit.vcell.modelapp.ElectricalStimulus getElectricalStimulus(cbit.vcell.modelapp.SimulationContext simContext) {
	if (simContext==null){
		return null;
	}
	cbit.vcell.modelapp.ElectricalStimulus electricalStimuli[] = simContext.getElectricalStimuli();
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
			ivjelectricalStimulusParameterTableModel = new cbit.vcell.mapping.gui.ElectricalStimulusParameterTableModel();
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
private cbit.vcell.modelapp.GeometryContext getgeometryContext() {
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
			ivjgroundElectrodePanel = new cbit.vcell.mapping.gui.ElectrodePanel();
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
			ivjpatchElectrodePanel = new cbit.vcell.mapping.gui.ElectrodePanel();
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
private cbit.vcell.messaging.admin.sorttable.JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new cbit.vcell.messaging.admin.sorttable.JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getparameterTable().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getparameterTable().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
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
public cbit.vcell.modelapp.SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}


/**
 * Return the simulationContext1 property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.modelapp.SimulationContext getsimulationContext1() {
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
 * Comment
 */
private void newStimulus(java.awt.event.ItemEvent itemEvent) {
	try {
		cbit.vcell.modelapp.SimulationContext simContext = getSimulationContext();
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
					String response = cbit.vcell.client.PopupGenerator.showWarningDialog(this,"warning: the present voltage clamp settings will be lost", new String[] { UserMessage.OPTION_CONTINUE, UserMessage.OPTION_CANCEL }, UserMessage.OPTION_CONTINUE);
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
					String response = cbit.vcell.client.PopupGenerator.showWarningDialog(this,"warning: the present current clamp settings will be lost", new String[] { UserMessage.OPTION_CONTINUE, UserMessage.OPTION_CANCEL }, UserMessage.OPTION_CONTINUE);
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
				CurrentClampStimulus ccStimulus = new CurrentClampStimulus(probeElectrode,"ccElectrode",ExpressionFactory.createExpression(0.0),simContext);
		System.out.println(" Geo's dim = "+simContext.getGeometry().getDimension());
				simContext.setElectricalStimuli(new ElectricalStimulus[] { ccStimulus });
				simContext.setGroundElectrode(new cbit.vcell.modelapp.Electrode(topFeature,new Coordinate(0,0,0)));
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
				VoltageClampStimulus vcStimulus = new VoltageClampStimulus(probeElectrode,"vcElectrode",ExpressionFactory.createExpression(0.0), simContext);
		System.out.println(" Geo's dim = "+simContext.getGeometry().getDimension());
				simContext.setElectricalStimuli(new ElectricalStimulus[] { vcStimulus });
				simContext.setGroundElectrode(new cbit.vcell.modelapp.Electrode(topFeature,new Coordinate(0,0,0)));
			}
		}
	}catch (java.beans.PropertyVetoException e){
		cbit.vcell.client.PopupGenerator.showErrorDialog(this,"Error setting electrical stimulus: "+e.getMessage());
	}
}


/**
 * Set the Component1 to a new value.
 * @param newValue java.awt.Component
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setComponent1(java.awt.Component newValue) {
	if (ivjComponent1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjComponent1 != null) {
				ivjComponent1.removeFocusListener(ivjEventHandler);
			}
			ivjComponent1 = newValue;

			/* Listen for events from the new object */
			if (ivjComponent1 != null) {
				ivjComponent1.addFocusListener(ivjEventHandler);
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
 * Set the DefaultCellEditor1 to a new value.
 * @param newValue javax.swing.DefaultCellEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setDefaultCellEditor1(javax.swing.DefaultCellEditor newValue) {
	if (ivjDefaultCellEditor1 != newValue) {
		try {
			ivjDefaultCellEditor1 = newValue;
			connEtoM23(ivjDefaultCellEditor1);
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
 * Set the electricalStimulus to a new value.
 * @param newValue cbit.vcell.mapping.ElectricalStimulus
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setelectricalStimulus(cbit.vcell.modelapp.ElectricalStimulus newValue) {
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
private void setgeometryContext(cbit.vcell.modelapp.GeometryContext newValue) {
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
public void setRadioButtons(cbit.vcell.modelapp.SimulationContext arg1) {
	if (arg1==null || arg1.getElectricalStimuli()==null || arg1.getElectricalStimuli().length==0){
		getNoClampRadioButton().setSelected(true);
	}else if (arg1.getElectricalStimuli()[0] instanceof cbit.vcell.modelapp.CurrentClampStimulus){
		getCurrentClampRadioButton().setSelected(true);
	}else if (arg1.getElectricalStimuli()[0] instanceof cbit.vcell.modelapp.VoltageClampStimulus){
		getVoltageClampRadioButton().setSelected(true);
	}
}


/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(cbit.vcell.modelapp.SimulationContext simulationContext) {
	cbit.vcell.modelapp.SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
}


/**
 * Set the simulationContext1 to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationContext1(cbit.vcell.modelapp.SimulationContext newValue) {
	if (ivjsimulationContext1 != newValue) {
		try {
			cbit.vcell.modelapp.SimulationContext oldValue = getsimulationContext1();
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

/**
 * Comment
 */
private void stopCellEditing() {
	if(getDefaultCellEditor1() != null){
		if (!getDefaultCellEditor1().stopCellEditing()){
			if (System.currentTimeMillis() - lastTimeRequestCellEditorFocus > 250){
				if (getDefaultCellEditor1().getComponent() instanceof javax.swing.JTextField){
					javax.swing.JTextField textField = (javax.swing.JTextField)getDefaultCellEditor1().getComponent();
					String message = "invalid input: \""+textField.getText()+"\"";
					cbit.vcell.client.PopupGenerator.showErrorDialog(this,message);
					lastTimeRequestCellEditorFocus = System.currentTimeMillis();
					textField.requestFocus();
				}
			}
		}
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5D0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8DD8D5D556B015B5CCE1212222B10D1595CDD456D8D1C305C51FCCE2B18EB514D4B41161E493738E755A645461472440345422C225C405A472871350E9C431C4C9C9690A45A417FB8FF0657EF96F61D754F76D3D4F5A675CF34F3D976FB73E4F77BD4F70BC0BF34E5A6BEF2F3D767FBE678A4BD7CFDA18133BDC90F2968A725F369CC1483FC890469DBA3D95F161F6C321207A7B98609D4139AF9F2E
	13A0AD6E2F064225C2372BG1E2742F342DF8C056F437DB26129F57997400D8FBDE9678B42C7077E7820FFBDC9C6543308BEAF49AB026B5BG3100A357FC87513FBE2FD8462749F8041CDCC14820B43B72CAE45CDEF8EE831884B055416813607AA655FCE1ED0DCC57BAAADA48FE50153B1848D149C428F4B25EA679DAAA7CADE73E137A035AC9F8C261B99E00E4BECF386CC8B5DCF7B4F5F7FEE5EA37C8D53DA651EA2D32991DCE0B3D332A3347D2DB3B47AA1AA41745E4349E16AC369EEB0F3B49E8972D5289A2
	7757F985D48E4800E741C5F0874DC4D7ACBCA783E4F27CFBDD84DF8F772DG128B73770F75BBE47BDBCE5EA1E4F4BE7066929F798BF331FCCDF5A979AB7D34BD6F83DA96C5A66273ACC8DB6EB6941683B482D88142GBE22B1302C7DD3385635CB3B9DB61B43DE67F46E321ADD5FBA4C22956EEBEBA1C5469D92CD8E17D990D8B91F482B5431479FB03EB3E74D47FC923D854BBC6650CDC25A2F4765AE5271C9DA74495C49EE660BB837629326BB56276EB437E7CCFA6ABEA228F58BF3B7AFDA28233B64514D0B32
	E4B97912223BC9A2E57D940D01B2384F2136C0CCD2FC9645C72870ACBE3F27F8729C8FE90DBD989BADEF23AD0D79D789CBB70D5775C364FDE13929BD4C062C9E2DAD0F9FA732CBFA19CE87376543EE024F6FD3704C16929A2764F99E24F51DB4943278DAA1127A1AF19A62790C213098E0A3C0930095A0648C46D8EB7B67430C31A623CB5A55E3311AC51740F8D7185E032B58AF390C8DF637E4341BC40351E5B136DBC537A0532CB2919F34FB702D2131EF829D5F08F651E514AC8EBBC8B75AC4C9F43999CD2509
	78576492E9EA3345A602821BD3C0395526E5F0359A5D5277CE33D192958A163E43C44A2457E8G09A0G6E19DFF6F590FFCD027C4782A4F2BF5CC1656D97DD50DA5456B6BA9C5DBD4EE6129321B8047879E15AF62481FD76997471D71D84578A69D2884A793369DD151CFA0B0918EFF48DE09DFB56DC8A57D0201F82108C1085D0F61E21F0A7C08BC07FF998CFFFEE41F82ADFFC23D07948D85DF88A07588EBF0F45D174F95AFA75B255C7ECCE0634AC002567C3ACGB481B881424F477E61C7F3080FBAC8EC447B
	E459761D322DAD676957CF0450905F69E5BA3C4C1653BEEDF9D3C3CB6A7BE239BEAE71E38701BCBFE905BAB8D64EB1340ECD81BFA5814CBB9F4B3A027A96EA48AE23DB445AF2508501AE1A1512E779C9BF1EA617DD1EEC73A2B9AF05327DCB64BC10B8DC81703E2C1F68DA8930DAE07D7A9A0035G6B81AAGC87D5D84309BE08F4096GB2G598670A540F600FFG7CD3A02D25A09C853081FC8350C0E29560DB0003G5F819C86789E209B60BC009F817E85F08C4088E0829081BA81ACGE7G4672F2AB6DA679
	4C3F40D0D889508CB084B099A089A09B20E40421309E408A908190871081D0F42121309A2095E084C08CC08A408200320B201D8510G22G92G6681AC7F1921F0870099A08CE0AAC09AC0FEA86887E8869882188C1084108DD072F3508FE08508G088348GA83A98748334820CG0881C881D8GD0F689688710AE413A72C917B36F5C14A7469E162B3F72FCDBD56E014A934BBED0F9F64B7191286FBFA34747F04689E307C9779F788F7883D677F2AC246E7D3B6B5D86151E4F84D67FFC553BBA198E6BE720FA
	07F5B4D03DA375731BE17801544F9F0769B3477F8365769F787F9F5818E735C86A1758AB5A25FA51A95A4DF06316535609249E4A73018E87672924F8A74C9FF643005D4D711BC5D2CF0D92CC585BFBA47130A8C9B011D752EB29AEF7CF3BD319FA202EBA1154FDC79F4C9FB87D57A299FB1B9CF63BC887DF1CC5CE5FCB477E22D53401513B9DBDF609618FF410F512EEF1C01EFB9C6AF8C3A0F387EB8F1947303507A9C6523EB8DD969B8C6948D4C818F78919974C61EBA78422GC756D062611AF2890E637B5660
	B83EEC70FAA17E18B3D7EF0E9AF94DE53955E1ECBC3CB7CC9947B31BF6F76A5B5EC067A0B25E4DEC4D2EA36D5D6E9E170B50DAE1D2643198A23036F319F0439162AFB7C922332C5D8E3B8425378953EF5C77AE132DD9D2417473EB566A2673399C1DC05139A457B9FBB5A953CE5181842E81D9231CD63B0C7898C2C1F2583FF1B9FA1CB2FE51903123DE6CB076D8255D22553A47EC119CAEEE67284F5FA279F5581C8EBB64F8930E5B1F35901FF501AF07GA242F05C7EC0A7C947B91ABCE05F6D30DAD958C0DACA
	98A95B1D62F239ECE260B9A10C156D5BBCFD8ECFAF9989E34711CAFA5CC8125E5A0169842CA3357C21BCBD7FD29817DE2A65FF4D033F69D2AD7F271D18BE84E991DE6913F8FA82C9D325B3BF5C5A3514143FF822C70429BBA92B7D3C8E5C59752EA64DA0578716BE2D0B1415D5B5CF5EFB99517933EEDCA794AEE37A0AAFE3F34F9A382E82F4A6257B4E0AF4912A7498F2FF9933F1A81C502D34A1DDF5B81BD3B406B37AB638D6F1F9291CAED1152E1647682E30A3DDFA3822B7CB3E92FC48A8C2E7F6A05D7CF0B6
	FFA1FAC9FE160ED25B7706936912C631B9CE46A8C52EE2DFB2274B16530BC66B693D628452D50EE6F3A5FD3DBBDDC8D7BF1A4D27DAC76B69FD4B0DF4D6D95F8838C60D567A25F5G6962D5F62969185ECF8611AEF5B41B3B917BD02E2277D1CE17B51A2D19940CB1941682540FD14B3B6EA452350CE1F3C0A20F2CD1F8666347A124F30ED172C968C247287DD7F18A6922473039243E7FE6F03A78B1EC3E19BAC64FFFE3CFA3DD6698255C3C757EF80647FD841B376A6B7DBB27AB0EE0F35B0D91FAFA278BC837D3
	CEEF09E06BB6642A489B1503F4DD91EC0E0C75A3A4C2AD2FB1976922D4F2C879CE751077BEDF67CF0CE0F3EDA20F5027FB483B7CFD9E4F91CA3ADA9E23B396A3DD51D8C5AF29171EFA57FF00F4E5E3593CDEDF6FECCE37E32C122E16B7E5AC213B72C324EB9E2B780F1407F52C1A6E7B0F10EE01CC37FC1CA79D336F1D925E8E0DE36B8C0D639439AA7BB8DD2BACC79A47565A424629650DDD09F4F16358FAC58ADCB346E9659DDB05F479637D49DB369A69CA4633F50FEA38560F574AFB0253197D4A9BD50AF4C3
	63597AC984392A64B17F7D78B152A52864F97BEF49A7C817B61E2D43E40F77749F137798274B0FE47EA809E472DAA255659635866942A2D9BE75691A4A10EEF2A433DF1FAEFFADAF0FC84FF8C971200B65F459111E7102F44C4F56F5BC6EA7B03A32892CFED4CFD07BE57BFA3E2FB6012DC1717EED023A5C32B91DB4C1C9D74BE3F4F715F33F2874127A6129B724027BE582DB6F5257DB7DA9770BCA2FDA9E237B9B27CB19405A79F993587AF27EC4B55D039B10EE79C4A5BDDCC5476C8B2F647D60C4F97DAD1C51
	57CFD4474B3E4FF8FD1365C893D9F94448725818782F95E41CCE778E0F9BFB0D7DD56EBE18EB54566ED36D8FB665974874C993F19F70457DF45F65F2089B006A4BF1CC7F3B45388F98519DAD244D5D344857BEE763654C8E67654A183EB22B6E8DB6C67D3C1B681C82E971176378F407C53D97D167F6BBCC96BAC01747DE4FDB54E3FE064B3210B1F0074565169AE0D2560F633417AD44DE32CFA22365FD23CE31C8E063679450DB9485FE0DA27849A2198B11B19B4B27E98B66B3FC709A21ED6C445CC29F798C0D
	1277A022D479BCBDFFA15337388377F453A13D78973827BB31FA0360F3CFD7EFBFD7AE4FD75E5B651B4F77DE304E4404657354576A720CDBF24AE7BE1BFEA11767AF14FC265A1A9731FC5E4C73B98552B3F9BE3FB5177B4B67B71E93A24673772A153AB13BCBB5C342727C2E937516DE81ED579528F7432EB53AB23CE6D3321C69DCCEB848C864F21E9B7228BFD546BE293616CF39986F14EE0CA96117B01E81E073333B68BC5FEB1EA9607CED3B15441C54E5F13332782F3A1D322F13EE3ACA08DD94162B37475E
	F5DDE42E73172C8CA2FF2914C575156DF9FB8118CD03D634A993E8CAA7C13BC4676D69ED6AF9FB8B6043812682A4CEC23B975A5E94F0FE8B31A2E02C5C38CAEDDF0171C2DD7B4266F66626CFE2F695DC2958B7E5F4FBDE42E8346FEB9B5A5784B421D7223F17FFC52EF48D027B69DE9B1DBBC3395B4DFB38D54C262123E8D3C852D421F859C8DD1B68F8D736A5D3E5535024763C70C9E8D30E1D58C47457DC85EDAE009C9BF634B59AF0699CBF52017445D743D862EA4C4335B6B331D7143CEBE2F923CE2DBD2C0D
	106D95FAE45E5236B39F3C3A92FDD0D6BFD928496E766903502B65BE7DEA4587B10BDFD9988F40727439134772E40865499893631CA4D60D2E4ECD0C6E95CE67841AE8CE57F6629D41FBED44426B6B74936833F460A93E86FDE9F171F8GDC68B5A8AF596529F70B9B796389AF27AB9D5046AC0B778555FA713E71DAE8772FC55D978E206ED640C5F07C7EC144A781AE7FBA443F4E71BBGA7DD07B6F41CA4F80B5DA2D90CD64B20D8DE41ECD8512E36A1866833G16C79B8A4B2311B77CA44913CDDEB3E2FA12CF
	221E1DC0579F0D7AC78CA1FE8A60B2390CA527FC2DDBB13EC32750F72557C3DBF9BD4A4BBE0572428117F8BD4A7B7134276F6FB94DE363D7909B3F62761CC139CE40C5F37CBA81695381D7FC834A9DA9F84A4D62F4CDC0934A690E64E07B48686E4AC13AF820194F6936646A2D2DB11E1EDC645998E3A86C0AC19E011EEBC09EC63BB38FE9A781DDAA273DD307B661FD4C73529B8D05FBEF44BCDFDC0CB2C2G974B71CD9FA0BE8BF065B7A1BE7FC314538638C80EFF10631381D7F0B336ADB95B491A2ADB148E99
	4D96876B91075D2A98D8FBF155B2ECAFC8BB3E634CCF3A6DC568122E5C1DB733F6226BE6253D588BEDF8ABEF47CFFF04F6CE851AF9B7234F1E7D48FFBBD0DB027EA81BE2A8EC1D02FCD716F846C67ECA240BG1AA4CE573DD2DB5718FFB65ABFA17CE6F3F9232A5F2F2AEBF7CBAE23C9E28E83CF209D77F0797937988AF75C0272572EC2BB581A64F207FACDD2823AC9G8937E0F9052EC2B92900DBFAAB62F72CC67CDE40055C0A722FDE6DA9FF31CDCFFEAC1773C6A9EF0BG177FEB445F4571BBGA77D9A65FF
	7C31277C0603FA72E3381C919F73F61560B83E6693DEFF26C27D190A72FF7909E7B9652EC17EC9C0134A69ACEB48FE22C592EDD5645FE149A8093B3B0C76CE51DC0E832AAA3A2DD255G69FB48DD05BC56FDED9399BB5A45BE2577744582A54A6CF8005B3174B6281737E1BE864A909F8238D80E7FECAD6233GD7FEBB56B3C375B2D64F68BC49ED3038ADB0F4AE0740E1FCCFF33EB660093C9D650DDD077ECA86DC419D6807856B30ADE34FE56B3D5BFB73FA5E5E83DFBF67FDC207F6E5B96A1F82F419F7207EE7
	4AD1C6E9AC347131083F3E02377100CB6478FFFD0AF204BB21CD3F9371EBB670B69DF051F722AD97EFA073AD13B2FC1647E8D3B7F83655FF5D405BFE60AD3E8B655ED9495BFA40055E05F2B7FFC65AA6938E2CF9BE5D1F7138A4B2B87D631F11B3229D8ED30F3B0A7EEFF438A5EDC4FDCD92641012E36A22EF697A3E64F06294G1A87D22C729E7B77795A73189BFF231C552AD75D73F991BD27E8EAA4F6367C061DD3DC4871F60A372A70ACDFAF6D677188E9993F4133F5D75849D8E1EF1C21508CB0849081B095
	A0A58E6749270F133DF73FE7979B2045F75921597558C444B3045D44075D62007CDC532D9EF3B3BF58C462072CB87FE75C544078CED23E023B074F477A240A9F546727735F5917CBEC549B43564832775E2D74C94CF7871D240D8B857CE400B80094006C3B51EFF7F7D786729B1E63149D4672342CA338F3459F1FB59F4B47EDA7C8BE165F8363D4003DGEDGA177E0BE96D983167F1EFE5D72E7F65D96E4FE6E8F126FC8E7F0FCE210FCB303641B9DA45F6D5D0178A65D63D99F98DFC2C03E848D9F0B0FC23A37
	1586E97381165EEBA82C84E8823082045F0BF1F2443AABD01C5461A104E6873F5D2DBDC8007606D8034B67258179226F7564E3ED753F7A7C2C1129573AA0C7EC071F4D379EBF22BD1BC0766162C1C78AGDB73ACB612B1BDE9FB665D0BEB1E45C6FA9EBE9E4601G4D7158A607C8445FD6F88E8518849087108E30G20F49A7A7BFA6B76C07E261D91785AA40F4BB50EE67EEA348567679F82726D1C2697CFC2AF49DF8B24C9GE1G51GC9G19GC577E17EDE8D9CCF4ACA63B062E911DDEBEF59FD1E766967F3
	05975FAAEDBE6F53631B6945B7BC3F4E8E4857222BEF0CA3387CDD6945D725F9F65E27D70E277BC8B906C25AE400B800D40085G4577C3CC5F0F65F850D6F79665389B70FB9D0EEE1DE2146D5D6A8C4E2F3B8372355E2F4777FA107A961F8844A7696A1B6945B73CF21C6D4527AD47B0DDFD8ED7F079BB9510EF322E3EAF5D41656F9BAFBEEDBD0C5368E3EDEF75D6ECFBD36E4736F77ACF246DADCAB09416816CC8407366178C84670B8981791A9374783E9B8CCE5F3100FCD6DDFD8BCF8627EFD9C03E9189FAFD
	6A9DFB4859DE2FBED5F43B0D1D24D7B51AED96FB155B6112A4B28E2E2D5DF7986E59C9A7DCD31DE17468DF93581AEAA2DC539230FF7D369D5FB94CCE40B20EB211B22EFC40D0D88F507AG167123C17AA0A3A01F74009E5F45C341690B8C4897262B2F65D4F0752B430B2FCA73BCD9D75F5F3C7806173F0253017862F475A584645357B7ABA0DF0AFE791D894E1F11DEFCDAFF4E5355D7A984173F5A00FC797FC70F6F65A07565678462AB5155979F10CFDF5FC381792AF5751D4C8DCE5F4F8372B56A6A7BBA607B
	3D7A7A1A837219F575BD78FEF071196A45276DCF07F475059787173FAB8272C5686AB3FC901CBEF3C03E98DDFDEFFD981CBF0B3D78347ECC5055179C243EA7836ACB5368E36376177A4938BD9B5216CEB79416835483F4810CG081E0E63762BEC64BD1C614CAF830F59C7FC94DC9EC7FB71D5E91E93276B716DAC89AEE68E8464CB575577564A60749587641B2F2BAFA1C8FD338272ADCD549B473D370B44164E39AE553EA00E572EED54DB8FA98739BB92F13C365F01E7401A93F1DCF6692A6072949510AFACD1
	0FEF576A6074B585641B2C514772DCEF43BC4771BC971586E74307817932F5739C9F243E078272957DD6AF4FA187B14FE53F45BCDF7CF11063A9AF3EAA4DF36BEF75783E7AA438BCB7866413F475952C89AEFF6FF971E95B73B0DDFDB3025477D8C0FD13F5751DA98BCE5FC8AFBE2F793B2E3EADEB0353D76745270D1794DDFD4F85296F25007A66696A3BFCDDF071F9FDC03E7C8775780E2F8FCEDFFFC03E92DDFDEB4A037367A6AFBEED3CD46B6A1B6B45B73C7C7DF7C03EC6DDFDB7D784276F6E00FCE6DDFD26
	CF03736789AFBE2F713E2E3E0A8D41695B6645275597212B6F95AF3E6179F3D1E540713E2E3EFB8372696B1B9910AFC1D75F40E7417973E7DEFCD51A67B40DBE76CD9DABBDEF1F8DE985B38C058C97370F60AA6139FE86764B16C6026B0267A10E13A80E1C5DA1EBEE8C776A7E96BFF9A67CBC1EE6F85AB39CE0BA16B77A53217E3E4C423314CF004DEF2E337BFE477AA63B7EBB56D36D7A6FD85FE5FF0761C1067A8BCE18FE1F5D7FBB588F5A755E41DEC9737AA8BDD3686A31CB961BC85532B13958CF3E2FG33
	30C3BDF6B6C5A31A5D5A3385824E7B368E12B3B361B3F0FE576658ACF84F6F58B99B9452AC9ADD87BBBA78665AE127E8C21B0FB8FD7B6FA7273E7F4CCEFD7F9DF77A771F4B695FFFF71F70EDCB4289FDDB9EBC21EFCB72897F36241E70E58BAB2F67A472BD8C2F77638F98ED4A9973973A5F14E91AA4C753A6AEFB055BF7BECA5C7A797844CD5E09F1BBFADCA6D1EBEB053B1827A933F3F20E0D18A3FF9F0072CBC6D727681557AFA9BFCB73453F4BCD5FA73718ADAE794DEE1647BB2C1847BD1263DB12C7B6A7F8
	37856784B1B3F02D3EE58B09718CF8DEG30BC895B1A15C6EC73AA133C5B121D00EB8668D22531F3D0E1F4EFFDA8091D034A617831949F2A423373D12BA81EBCC7C05A7CA42CA397CB3E4BE31424DF9E63A55F65F1059438BC2E13FC1747CDD2607238C372951F2CDCAE3095E26CF5389CB608BE4677D62F6F3C3E532B1F5777FBFD67F5D5EF603C2E6F751D572A5E40F95D562B0DBDD687FF7B5E92DA075DEE111D482BB7CAC6B97FDF5955F139197BA55F2146EF6178879D9E7E5A0D7E8A6D736D2F4B7A747DB5
	364F373F227A827B6B1ABE5F7E0A698B6C2F5B7A7C47065569999B1BE57C97A7543E29D6FC6672748D52DF6B09EF5AA2631DEE359C32B7A86F89EF45FA5EBA533B1EBB81B782A0F226361EEFBFC66AED4CCCEDBD2F277858195AFABE73A72C678910D67E901E6F3CEFC0ED5797BCFF21031E79281671BBCE2A69378938CE5639C77FDD32FD07CDAE0755CA0E8D71F6B00F6ED9127C07BD046DE09BED079260B98DA07BA1EC878F357B9BEB96BD6C6DB3660725749B1A258FEB7D738145D7BE2C754FD5A674CF8D24
	C5BD0C7ED9BC6469072D329F268D297DD0437DF659A94F3840725FF45AD34E97B27E5553EAB9DFF2B9534EF84AC17F1B84CFB95BE47CE7B9EF2964EC625FAE796F9CCF7AAFE57C2D399E749B117ED41EA7FD0D0C4FA976205F0C7453BD71DB90BF123E0721EAEBB7B2FC5D079E7455C83F68A3159FF6AB6302577BFD37B5EF8C6837B5C5833E5B1AE583015B1A12815FED4D1A0140EDCD65007F3666B79EF9D55A613E920D4FB649BEDB696133AF107E55159A7A4D2CFEBD5908638CA1996B570F745D3A9AF8EE82
	68CA467A5564F5860AC0C8B22BD791495E750B69FEE01507CDDB91BF30DAE353962627C1774CDC34ACBF4E271E5D259EFA367138AE556829E6FAAEBC2827A7D9161F61D34F340F55E5227401BD1FE874ECE578FF2ED153AB7DC2EE199A3F1563EF7740AB6D6C7135EA7CB60EDF35CE0DD75A17D96BBC7CD10378AB57EB6C7C0261FFA877207F9269CBAB54784D3C7E4F29504859467093BF7520676DCB5B868FBCEF97B6D6FA60F93B70F225C6FE8D43CF79CC1DDF259D3CA01FFCAB4BEA9CF074C802E07E9D0965
	0979384FB29F4AF3A960EAG9AFF4776DBD8FFF4CB9FE947BADCC69BD4BDBAF95BB90B707EFE9F7246GFDBCC0BA00GE92533304D7F2FBC92CB6C4D7DCD329D6BCEA9384DB8C798DC2C2E6772BC0745617BAD5AB88C1D2544DE54ACDF7DD6921D8FCC19457A27C20EFF1C6213D5F856EF7DC47164B98B521AFF0F6B9C55745CEDACBC27F0DCFC9B2EFDE4F25CB15A975724C03B100238D4CAE706672194ECA7C613CF8A623A2D6A3E4C68EF3D627F96180EFF1CD39DAC4FCDDB51DF615C8FDF2DC7FFCD66381053
	682FEC0E7B127B2B609174D75DB145C77BD4773F7F695C7BEB5E7F020E82BEAEAB01BCEFFC44B31E6B9F71955FA9F41C5772880B63BC0EFF12623BD4F8965F9D94CF1E7BA1AD6D917439259D4BE667236873774C0A1FD72B6E23CD675E9F537E97F4BC5328EDD34C0FAAFE6EFF54174F9F26FDFF58234C370BB8FEB645CFD661194F0D94CF1E63A02D748F6873CE3AF7BD821EA3B8AE490165B009635AECC81745F153ECC8370063228EA2DDE3AA627EFE9069DAD271DD367B72F1EC9E8D3874D4EC63B73511739C
	1AEFC6FC633218F799BB9BE90F4146C3279A8AE47E6247F0BCF4C3831159844FDDGA10F6177C87A5761F7BAE22E3DCAC8FED67F3BAB694D27F2A39F1347AE7215FE9B625376BC267315FD28F3BE24EFFC1C60566FD73F9BCA7B15BC36164D520FFA2413B1648302BA7D920FF4E22FA3DF1DFE37C7BA7151BFE4FE163799EBB1EF8BE6CC912C370D70795B997D0F33BCC549575207A66404C6B63D414A280A2EE325C0DA6663D8EE51C7315C0AE6E3FD1C350558538A4FC3GD1GF1GE9BCBD1D264F1F4D3EB3D4
	1C463E7BC35E59174FB1ED8926CE8D49FA287D3315BA11A65F374AD779ACF7D0BA968A4AF762972970780D77BC55F51950391578596992BBC417CBB49F368C12F102B30D3E97CA7B699A382F87D8C0F1FFE82B17792726F957E92C2BF9B2A482CD9A408A79B9ABCD195BB27D63695C9386C296DB0F0DEAE73AC3E99BDA7F84747DCF605827290D529A7BB534B9544EA1208B85E038905AE74641F36293E87B776B51766CA774ED4FD3C14113064212A7955BB7BE29584EF45CCC7B5B9D005F7BA46A6D227D2D844F
	A1CFE27C2D26F9198A4F299C57CFF17360F979D3081B457B0D7227B04E9E273FE3534F1FB368B3794694FB36FA0CBF885D3F3FED3F50C3C75E3FD1BE81B6768D615F470800BCC483A482E482942423FF7E5608F53B1C63BE67EDF2E3BA7A64915AC7C441F39CC0BA47BFCD71643BEA7572775C983E539E281E91B9017291A56B5277E1C8C09D4359B34B93BC6B349A3FCCB86D206FDF0B7DD27E5364B711FECEC2D5E0317F0C23BE00DCB56C85FE82AB64F8EFFDDA09FDA6BBA6D7111DEA73A7FBE1G59DDDE32B3
	7294594B1B7C49DE94C03624124D4AE1FBC3207DE17D72EB88126F7C3366E3631FD37BC82C06BC2DDDA33C0862431E562E915E52006310C8C8DB70B43E37B81D4EC75B7E887D9CC084C08CC092C0669FF1BFE55AA0CC317D1FA9EDF6B89B45DE512A5AFAE379FBED5F59660F75775396E1FFBF752EDF893197557B7C665802BF32BAD5728C3B46A6CD48A1D7163F27BAC87E9AA12D8BA08CE0B2C0ACC072B3183FB769BBA264E42C59C25EF6B51A3A68576378262CC56CD350E4C24E6A53BB5FFA4F6F0F34E0BB15
	718C1B5FAFA0578C36974FE2765E2398336599017B199DC053142174B3668CED9F6934116FD0B80D12290B3F6B5AE8EC972DF27A760F3C53593B3072F7C0EA49D8C6736DACAA0025DF542C13CE8530747D9F3DCD652BBFA0075FE9DED12D9E57B9E5BFC44015258F9E53CB0F06AB2B6B49DD824F67EDED6477896CE2DFA97F06C33D850A047C08D179566A2D15251BB7D64871B329CF7D2D001A374E577D2EB8ED3B65B2C8162FE12FCF48E1636082A32EC596BCEBA8DCCDED00FB817F327E2907E3F61FB9AE70D8
	FE3A88E33964062B057ABF54672E71E14B4EE7198D614FAA637278366AB7194C395D0AAFE67A7345661AED4C970CEF2DCD61FB25ED1D9F3ECD555B2AEB36FCD1AD334B7CFB6D8A7F167543A983663F0B56237F924F5AFF6711A5F1D9FF4589C5FF55D9643B492570C57FF4B67916774F5A496FE3740AAE09FE1D08EE866DB23A1A9CD60BE9C09E63FDC747E464B7B104B9B88E3D2C1D34BDDD8E17E550E1173CB9F1FCF88365A56F596E1C03ED435CF6EC9BC26668378DEF8ACA5B9081B451F31436A1EE0E36EDF8
	F8106CC1A89BD92CF2327239369D67F909F3F01E17522E1D670D4AD14F535EB0E967F904DFC92FB1E967F91E7C47CD5AF99E723376337CDBECBF0567D87BD98C574A67583708180F3EBE08BE2AFFCE5FC76AB63485E82C4FA9BE8AF9CE6B2365C564FBAF879C7467GBC4E46337455F692878607D5B2F60ADEC4727E07095028FFD7C0CD435A0107FFD437C9CBA7DAF3518EED9E4264FCC5BE2734835EF5856BC72CDB391FADA9772F7510FB568FDF30CF398FD35D5F51F06E57FE124F298E5607FF6F6737B7DAFD
	7C76C63B0F5F5E688A705B9BCEBF3F3D5167715B9BEC8C72D0AF0979A4A84BCC0022CCC3E1A9C0F5A60EBF96F6106F1131AFEDE8479EFB4C1DE42F0F7C0220047AB3BBFC6775450E403FBBB83F036CEF34BBA0B4EDC4BE62DF6BA065A5B91C9449F0F91D44C7AECBE71744C8E57C1B7437B52CE287C533B16619969CE3B6C15EC2B3713D1169AD584E44E7629A56CB14F6BEBCAF1D8B733539C81BF5947B0C1DF34F364F3806FC42973FE95C61631B46F4DC43F4B5D6232EFE2E7F16E3081BB2974748078688AE93
	1E737F04E53642C23EBBE2B09E77BE018748A61743895D46G4352DF07406F8B98556361B210D773A77FF3C7E6432503244DEB845AAE00B00049G31G49G99G73810A1EB79416816CGE882B08374830482C481CCF99E6DCFB8DE6147FEF9A112861E4BE8215F5AE276DF62F11EBC816425BE6F5FFE56BEF63513FDF1756776D427AB2A8CFB8EB5B76C2EEBBCF6F87721030D0D3B6A8E9D2BEBBCD2F774B0673F2FFD0B9F7E3D878FB57CF070C0331E84D62E9F1A30DC67BDFF36F1F5FCC860793F416F198EBD
	103F4DF7G6BC659BCC3E16BBC0C37838730EEC44C433AB1C4E91360B983E081276D636F65ACFFE138F9403EE064A90147D26DE9F24F62A0423556D79CC06B338F7CFD307DB88C791B0D1D1C67D1BFE74E1E688D5C7E4C69255F6806F623F940A91FBBF83E174C259C9D9DEE1137F3D93DCBB92E415D64F0DB48F985F94D49EB0FF2F8FD5118A079124E1A0F4D47ADC7C9D9B63E201D273BA83E65856DBC7DD55E061AA1AD7185DC03FA6928DAFF651F8D05BB7F6C3F5EB13E6F2A756CEE16F93BF4E4B03E5B0F69
	718D497461BAFC72796281F24F7BCE567461796281528F726FF1526F0E9E86C2FBA7DB2BAB23732D49A0FBC99E6EEDD6D0DC9C609666201C2B86FD475F750301636F56C17D7EAFF6503B7F3BFBD03F7F3BFFD05D7FB1DC62A0291B1D50F636F03A199CF71463E6D11984F744E2163AB8BE155AC570FBC5229071CFD23B7AA82EC546BD23429D45F141A029C31065AE724DC506FB011E9F6F30D82D6C79AF1446E207AA457D71B2355162B42A6CFE1563085D2CAC2EDA6FDDBE572DD7170F3CFFE9D247CEAA244D9B
	D62C969D882EEEFE98A45F61336613FF7B1A364F420B5ABA7D8A45973D282D536773363F985206DE44BABD64F15EA79D7059AF8E47CF8ED2A478FE176ABE6768399F4BFFF8CEF53018AA7A8163AC07635E798163CC41F55171DBC1964C9133F03DFFE6B52E7717A938E3C8A7F1DC95BFCF921205FB32C67ADDD639FD22D51361CB7A97ABF82C1A328DDF183CED5DEED25B4A7C767281251CDED75D079EB877E5F665B9554172BC6620379FA28F2A7D4046F1E72ABD77A0A6121F4A155772B32A7D2D65EB67755A35
	7C64AC6DBE415D02A2FB8F5F17CC4B52DFB3D868C7F616176CF43AF74246E53DEDB8AEDB10056B16F9F9CA991FECD357736A3F006C3F782F674CFF235B3CFDBA3ECD6D537729CF276E7D93BC443CA0AC140402830ED23A7CA0890B65958B7EAC2FCE30E737CBF857FBA92C6E52477E90F32CFEF92E105BA03C51C5E33DC1D858C7AAC3BF79C58D6841F3BF865EA51C370176A5693DDBEA731E39BAAFD74887C65637B68879240384FE97131387FDAB15122F8241FDC2B8CD3D8E32F3D6ABCFE7DEFF7DF5F59A68A5
	CF8B4F1A1F1EA6D57F8D931B6B01D56B4C77E389DDAFD627C842923A76DE4AFFF9C092163255F485B58C13D42A646F611ED12FA74D3F5064F30DDB48E67136BCDBD9CFE2F3014E7D4456A640DB33F1EEF847BE321E69EBCEF058E634C2DCD93B6B6CE6B2BF58B5A0094A5A08F5BF116FC69AB911253977933B0F2BF87E87D0CB8788224FD3D954A5GGAC06GGD0CB818294G94G88G88G5D0171B4224FD3D954A5GGAC06GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1
	D0CB8586GGGG81G81GBAGGG8EA6GGGG
**end of data**/
}
}