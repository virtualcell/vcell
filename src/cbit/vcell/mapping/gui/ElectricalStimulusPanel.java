package cbit.vcell.mapping.gui;
import org.vcell.util.Coordinate;

import cbit.vcell.parser.Expression;
import cbit.vcell.mapping.*;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Feature;
import cbit.vcell.client.UserMessage;
/**
 * Insert the type's description here.
 * Creation date: (10/26/2004 9:58:14 PM)
 * @author: Jim Schaff
 */
public class ElectricalStimulusPanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjparameterTable = null;
	private cbit.vcell.messaging.admin.sorttable.JSortTable ivjScrollPaneTable = null;
	private cbit.vcell.mapping.SimulationContext fieldSimulationContext = null;
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
private void connEtoC1(cbit.vcell.mapping.SimulationContext value) {
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
private void connEtoC7(cbit.vcell.mapping.SimulationContext value) {
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
private void connEtoM1(cbit.vcell.mapping.SimulationContext value) {
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
private void connEtoM10(cbit.vcell.mapping.GeometryContext value) {
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
private void connEtoM11(cbit.vcell.mapping.SimulationContext value) {
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
private void connEtoM13(cbit.vcell.mapping.GeometryContext value) {
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
private void connEtoM18(cbit.vcell.mapping.SimulationContext value) {
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
private void connEtoM20(cbit.vcell.mapping.SimulationContext value) {
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
private void connEtoM3(cbit.vcell.mapping.SimulationContext value) {
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
private void connEtoM6(cbit.vcell.mapping.ElectricalStimulus value) {
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
private void connEtoM8(cbit.vcell.mapping.SimulationContext value) {
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
private cbit.vcell.mapping.ElectricalStimulus getelectricalStimulus() {
	// user code begin {1}
	// user code end
	return ivjelectricalStimulus;
}


/**
 * Comment
 */
public cbit.vcell.mapping.ElectricalStimulus getElectricalStimulus(cbit.vcell.mapping.SimulationContext simContext) {
	if (simContext==null){
		return null;
	}
	cbit.vcell.mapping.ElectricalStimulus electricalStimuli[] = simContext.getElectricalStimuli();
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
private cbit.vcell.mapping.GeometryContext getgeometryContext() {
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
public cbit.vcell.mapping.SimulationContext getSimulationContext() {
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
		cbit.vcell.mapping.SimulationContext simContext = getSimulationContext();
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
				CurrentClampStimulus ccStimulus = new CurrentClampStimulus(probeElectrode,"ccElectrode",new Expression(0.0),simContext);
		System.out.println(" Geo's dim = "+simContext.getGeometry().getDimension());
				simContext.setElectricalStimuli(new ElectricalStimulus[] { ccStimulus });
				simContext.setGroundElectrode(new cbit.vcell.mapping.Electrode(topFeature,new Coordinate(0,0,0)));
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
				simContext.setGroundElectrode(new cbit.vcell.mapping.Electrode(topFeature,new Coordinate(0,0,0)));
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
private void setelectricalStimulus(cbit.vcell.mapping.ElectricalStimulus newValue) {
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
private void setgeometryContext(cbit.vcell.mapping.GeometryContext newValue) {
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
public void setRadioButtons(cbit.vcell.mapping.SimulationContext arg1) {
	if (arg1==null || arg1.getElectricalStimuli()==null || arg1.getElectricalStimuli().length==0){
		getNoClampRadioButton().setSelected(true);
	}else if (arg1.getElectricalStimuli()[0] instanceof cbit.vcell.mapping.CurrentClampStimulus){
		getCurrentClampRadioButton().setSelected(true);
	}else if (arg1.getElectricalStimuli()[0] instanceof cbit.vcell.mapping.VoltageClampStimulus){
		getVoltageClampRadioButton().setSelected(true);
	}
}


/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(cbit.vcell.mapping.SimulationContext simulationContext) {
	cbit.vcell.mapping.SimulationContext oldValue = fieldSimulationContext;
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
	D0CB838494G88G88GDAFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8DF894D516A8BA91D16390B1C044A89103C60DA32EF107F5599D4641F938E6C7A6C6876741186770E698B74E32CEBEA76B60DA69A4109ED122C44DE804A081820418C01090028AC9A4C00010B490A07CB45D1DF4A71D6E263B12B4795DF36F2DF32B3A3A3ABB749B5E775E77CD3E6F242A4EBDFF775CF37FEFD58B856B62F233F485021015ABC8FF3B3384A1E73CA0447E700BBC44D5EFCE518B0A3F
	75G9F88D147F3605A88E9664494FD3E3052CB985A61597EF00A7E9338DFA33C33AD67FBF093C04F229B8561493D37BE98DC0F3593752C244FBA43AA383E8FF089F0641A3819687F54D0A861A3A4BCC216CE90A2CB894DAEC31104DB86B4F900EB84E8AFE574A2DC1981B755D4CBF46DF7A488997F62563DC764A8E4A224CA3C195235D8F8ABABEE8B51F5015AC9F8AA60598AC0B21FADCCBCDD85575A666E4E2FCD17EDE2F91F49E237177798DDAE1B23333C3357D6D353E03718C4374DE434370A361EDEFB2F27
	596830580B3692398F9872289CC1D882B293A84EE8A33AAE413D97A0EAAB62CBFB88BE9D6EF33625680B36E17E76ED2E156CEF9B7C21103A23A7E7F5007CD5EDE379EA5FA667AF5D3ED87797DA9689D6627398C81B8330GA0832020ACC5DF8570990D0147BA3742F57FE5314E595363F46CF7398E584D6E23CE33458E77B5B510A261DAACA6275BAC882C1C4D06B28DFB028163F3DFB39F73C942CEAC7344169F880B1E1822DB296113C589033A26B2668BF3196C93267B11003AA34AFCE352D777E9C1293B6C65
	9733F3B5F4B77E74456C98C9CE22C2F7E699A96B7F20B1108C77D9549608C90A57F591FC6DF6994F6233135213E7AB24E5EC4758E8FB9FEDE94A19A164D7CE5574C34C53113AF1654C0618F235AD69CEA2FBDEB953696436143988BE71CB994FECA927F1CA1E23AB6039AAC54F626B71AED2DFA3F7C3BC834C85D8881089D0D01D22AF2D46984B6C7CF20CB156ECF40B87FAEDF63345ADB05E472D9F435562955D46C38E0FE8F418AC270CEE1B71325D6291A41A272C448717FD78FEC4E35F84BA8EDA9C9637D134
	B99DA05D58E391ADEE8F2319EFA57EB539ADB455E06B3100029E1700F297D8574055EE7408A7DDE623E811A9D87AC2ABA913BE63A9108882601E7965D75D44DF0D107FCBGAE6E07D69B11F74462065622262649696C6EF599C8CE043955444FCF5336A3826EE32B51473F3493DC4E9EE89F76201CDBED9FAA64545BCC44FC237BAA5631BF5A0A61DA8174EDGDE009800E40025GC2CD0A3E3886636977ED98CF756FBDAA143DB8D9B31E66C0ECD75730B8EA2ED1572B3CAE220F586C0534980059G8B81B2818A
	7726682B77E27F50E39B97208E929B715EAB59CE6CA5368AB55A759321E2CF6074E40DB4E6CBD4C0DB74AADAD25F5F136A636AA0F690488E12762E860E15F3230344CC9B78C984085E0BE57D8CADCB28A1870C9E8B561663EE88F40BD9AEF9161FB71C0B24324B16ECDEA96565DD497E55D29EC89CFE847009241F687A9CE01D407A758DG9B81B683EC81A0753792208E2081E0A740AE005DGFBG76826C8378C6A02D25A01C816881B888F00844AA40D10063GA7GDA81CE82F4835CGF08E60BC40C5GA300
	8940825089E0839885184C4BED070B64B391FC148A309C2024B6C55F84A08244824C83C887A85817222F85B083C482A481AC824879BAC5DF86E0G98820887C88648GA87AA6C5DF8FE087088618833084E0553750EF823483C4GA4822482AC87A8598F7A81C400D800F9G69G8587C0BF0099A092A089E091C0CE9D6887B0G8C814483A483E4G9455E3DD296AF1E5DF1F72445843F28DD61E6FAB4ABDD4F9E25907AA4FEEA9BEC2757D23D2FC0CE51CB0F90CF4FF033FC1B0E0F5EF0D03543D3FF63D5B2650
	33DDE075AFD03D5BAF51E17D8CD56F300E062AF724FE9E9C039FC87DBCB7C61FB97F9FA8373F417F7F4046BC7F58C56A17254F62906BADAE0B438CB79EA96D69AED28F2579C00713734C27F8974C9F6AE0406E61781FF511FAEA94E142FE39D73434DAC491A672F27ACBD417277732CB1EFA202E45DD246EBB7BE17E4069DF6DA2E3EF13536130504197E711521750313F45EE6981236B1C3D8E1161DF6BA66BA45D162B525C63775DAB84B2F7307712F98CDBFB18E5A46D0B4BED6B01A19D198A895175E4DE70B2
	DFBBA1D08FB83206E2056BD0BD0E637BB760B83EE460A1E16EC517CEEB0E9AB3F322EED1BD9B8FAF2B1747714C26D76C5A367FEFBA8711709EE6EB46FE525E55753A5D04568E13A21F459001351DEB04C7CE93FFF9C8921DE555B99DA214DEA526BFFA78C3A6DB35240269B7D6EF54CC67F3B9BA0122F3C92E73FF2EA3E55AE9F182017B2A24D1CA2BD9C3FC8C21A0BA9D875D4EDE1704DFB9C46C2837F498FB6DE21D45EEEFB05BC4271B5BF94717FFA679F576381C8E48F1A50E5BEF20F3697C8698838254B660
	385D58C352F10EA68D586B1CF6BB8B9BC89BEAA0E55B52D5A0150D811E6D8D2CEC5F676919BCFD9EBCAFD2241B3FA3690EEECCA7D0701D1AFF9ACFCF0434F95F2979736DCA7ECCBF7EDA1EDEF29072F7D01D7EF7BC5DCE5294694C8F479C79247CADD7FAADB0F5A7E5F5045701130E8FD5E927247A40524FBBC8D959957364E587094E3BDC38CE18A0691BFB104DBD53603A9670D10D044EF48569EA9B65F48339EFE4B6169CA2F4DF3A11EED1A31BD3E4C874F910DE4E653D4669DC0764F425BCC6B74B03F4938E
	49FAE324AB41179EA6F4FDA252459DE27397221764E776A125FD053DC8A79EE6F31C484332DC593ED7B9DD2C141EF4D8CB6F2CBE24CBBD4C66CA5AFADB7A11AE7DB01BCF659C5152DB64C53A02A3CCDF99DC6B0F287D6298C1BA6B9159AEA59D53DBB50AF4630E30399B318F654AFA17F23A18A3EC4DE496DC6783249FD14A7B87816904A6B687A472E67B6523A78B698A1B64FC923A0AA6257F766A102E39094DA5357D371853D91B58FCF3DC13167F9E4CC63A28A6395C7C7576647038EFE273D6ED3D2BF311EE
	EE931B5BAE54547BB227DBAA258BC7593A8D394A726EDF01F479C7599C996BC769D1253C8BAB112EDEA10714EF3B0F3CADF93C9E9DE5F3EDA20F50CF7011377BF39E4FC765F425BCC677B627CBD268A57552D76FCF56A2DD72D1B62F57565B3F8E6916A874AA65359DA3F47B0A11EE79D159FF24BC8A0EA9697E0353459FE3F4F30E7952B17B9EDD4F5B21E3EC1DA16318ACD7E15F97C817F31C49A9BA4E565A2A0EAB65D5EEC0BA73F136DEB184574863EAF9FF62F409C165BDD602F4730E33F50FC5F0CD7713F7
	F5A3522DEA8EA6EF7BA624ABE9E66BA735645AAC4BE37E7BA32733AB64797B6F114DC897514C56E1E21BFD7D47647516727CB6B3FF4C13648AA71465D63D85692ACE30FCEA5325F33A26934CFEED3A382D3CBCCE78464B108F1DE19B0F7B933E7102F44C4FF9E5BC6EA53A64932CFEACBA21744BAFB85D3293EC8D0A77EFAD4AF20B5A0EF4C5ADF23AD29E23BBD54E7D52A26BA57543D7EF51175CAFADEC3DCBDB6F82CEE7D668D54AE3F4D3AA10EE2805357351ADECFDB931C5C957D109F4F39469558ABAE6DF59
	8E5E8F36C86BEF0D0CBE3DC599AF3F61F4B92DCCCED1ABAB8FC3AB13474644FF58C24669F46F304B58E77416FB7AE12ED1D3F3D831BFD8F7B2CF2297DBF19F5059C677DD60F99E4022D69C537F7CBD5C870C6ECE9072FE191A9DE81FB3C3322350A00F69132674E733B16AF3AE22338D522C869C1FAEF4A877A276FBBCCE130D8E502531574DCE65181F61A21DE48C5CE1F3FB44C3B0A97362B8ED121358CB76C9A43434EFF4156EED1171739068CD82C8B7907C5FF7113990993331FC1AF6E2BE2386E68A17A6CF
	533D9BA01F95A725BD0813F2BEA38AF6E5B3FD6B3AF1CFF782244FBD09FB3A532A36898177F43576F325727C4F0F8F84668B3C972CB1B1E1799C7ECED91EF3D68F874CE726143F42D30AFD5DA75F147279E31E4FB6C80FBA0579BCEAAE8D164F033E93A24673F679671AB1FBC0B1C34272B459D16FFC50A91CC63D5B8EEC501461B71B12643C4C65D4038C9717735BA11F7AD3EE6C97EBEA78140B71BE6D4218CAG3EB4GB6BFCB2673FC3FF92600733723D7C84C09D61B0715459B7B3F15FC1D7C0399426C1511
	BA2DBDF66B03B13A42B6D6865F3649E5117C6AE2DD9AG33A9728A5A9485B47381583C3D27C3B9EF974EC05B81508E60BA03F6FF62568BB83F05D891B0D69EDD2B34AF4FF81326FD113F6B54CDB8436C1AF5C6362FB1F5312EBD956DEBF323FD19C0D3F1967DDD70B53952B5886E271F3B695C994A5DE1EE60D6B11B064E20CD6312130442B537EB5AC47DF6D6EA0F4F4AB695003FCA394F56F808CDC4FF9A50941ECB51CB3161C1DB1B81B70163270BC8BF97F01967B08F8F7418093D96513FA616B6E9545AD6F5
	046C5D22C566AFEDAF73411FBEC79F1454478B55995D81FDD051AE75696D328FF262F7E59746E3FB353F1747B2502435E3CCA47412D8B53ABBAB995D3BFDC8D7F8BEC55FFC9E695CFD9F887EEBA3B6DEDFDF6EC31FCDG1E39675117037DBC9EGD7F181653D5A6F2B37410B7CD6200963F4BBC654B14B62BD26C6AB5E9782DF7A8554BDF584F567DC04715EC5441F9FC53C8838C40E7FC0C07C924095DDC29B3C94EFF358C41B51EE9B3014EEE1B6EC6ED45AE0G7A9800B9G8BB86F8C1A279EE94D0869F9156B
	D98AF445C654BFA58B71ED000BB2220C753AC06BD60C4F2CC35F4D879E61B24ADB110D722A81673A0C72FA32FDFD3F0053A5GCD9A671F1203F28BCD908BA644D767A27D84404DB5215C69393EF2F5ABF85983CD059969ECAB30FDE4F4491C4E8AB4F11C2EE125565A9A6339F9A572AC847AFC8B72FC18172760E134ADF9C85B88F463B86DF39A3417BE47BC4F863AE5964C732635A82334A3C5FF298371699C9F8338859C9F378E6564F526683F6DC4FCC7B1623D001B5509EDCB56DE32266A3108ADC6334D49FA
	44B137AA27D8FBB1E38D3697249D2F9D3D2059DECCD8ED55AD6DE46DC43ED5EEAF52DED82C4BF8815B4CCF57231D6DC093EDC51F5D30BEF8BB7052FA74C7B270645810EF5F973E319177855255820D4869D6EED057B5661F8A4797045FECAEEDD2747B657BAFFBC43751A4B20701A7500E339BD0FEA248DE42657FF8835A4156A45774A957A40B3AD2740DG76AEAC2F129214B38EF033B9FEA147AF83DCE9B74A7FE6232F7CAC2B167CCB5DA8E746C65E9681AE1163CFED62ED91600A6CA87F1FB6794AFF2DD5CB
	3E410EF20AB773F61560B8BEF5B32FBF004B6FC179FBCBFD4B695ED264EF841AF11CAEF78B59CF3409161EF2722FD5B40A16BA2B5151E9B11762002A1CEE2B149F027486F237C59A6B7ED7A599BBBAAC7DF26F990885CA1459E15C426BA558302C87737151D64417BA402F8E44BF4B71B100DB6040FAF62AEA8D2BE7F41E64B9E57358E068DC8A014378063771FA6B04FA6BC4F915E568AFAF60E6B9518FB1E55816316739E57E6D3D301D377740D76CC25E2B5B7DE97FE1BB6AEF833AA8976A9FADC7997381A7DC
	C1FC43173C0D871C0B637F73CB1413G38B40E7FD185EF535D50263B5116CD15E43EE512074F52982D3952372D1ED8495BFE601D6BC639A7F770369EF0959E147B339D24EDB261401A67F375CE9E17C09B47697BF612B3229DCED32F271C7EEFF2FAC4F5C4FDC792241012E2EA7CD13A3EAFBADDB885006601942B34475EF8D2FD9EF321C7BE2B152E386773A2FACE7157F43EAC086C1CE2AE477F1E628B94F8162F5EB69E0F1096A962593A9A110C951641732A5E94FD89C0ADC0BB40D0AF4E13BFF5113D7720E7
	978FC10B6FF6C0336B330908E78849B182326FA7BD273A14E3EE660751AE6207185E60E75C144078EE27FC332E010F75C9DB4EAA4FCF67FCF0D8C7EC549A4326C93217754AFD92735D3F7512B62E22AFC55F84E0869882086DC33F1D39D2964AEFF80ED3F2984B53466E704E956F3CE6BE160F937DA49FF3406E05G4BG727A216F6847FC5CFFA5E479B7F8B54B1F5975BF424C4F0BE172596C617175074977CA18FC2F0749B75F950A2F315F37BEB03E17C27259D5FCACBE9E24FBDB91109687B09BA095A09320
	408B739BAF46499F2E9C8895A77B7190024149EF6B548789505EC9D7424BE7DCC83EE62FAF9FEB2B4F7787D9A3D32EF5C10E588EBF1BEF3D552EBE1BC076612C20E388002DF94E6CA0E3FA5276C4FBF14D73718EFA9E9E1E17GAC77E21BDCB4C07CDDF0957AAB00C6GB3408400F80079D7515F8D6E3D217CCDBBA37035C99A172B9C4D7CF5419D1E1F6DA17916DE550A2758C112BFE1GFC83D085508CA082C481A48DE07E263AC346133C52B806F87A0BC7EDEF72002FFD5A795C6C4737D61D4F81AD3ED77C78
	46665757C3728903DAFCB345707277049FDF396A39F0D02B9CBF9DA665D881E9CDGE600F1G71GF381168CE2B97E1BFB7FB514E39D609B1D4EEE0DE2146CBD589B1EDFCF06644B9952627B20AFBCFD6BC27295E96AFB450FEFEC65783A9F1F3A9C2BB4755D509FDE7EA60664EB5254F7449BDE7E4E7871296B21D9250F353D8BEA306D9D9A4236F70909343DC9701C8C30E4884F1B5FB3921EAF9E894937DC539726517074DD8949D7B02C457709901E3E0DA1794A06357A549FB610333DFEFD2A4563B1F612DE
	55E86E31B94ABDCE37A812F1F0CD4D61D638E7A71DF0CD556C331ED9BF4C56D4DDF00D98467E75E2A73EF398BB0CE57C83ABA963D4F8CE87489941B2DE9A268F5E8C49D7B42245971B951E3E07C372D5E96A6B5205D73F3CFEFC652A67A6CDFD2B32434B5F27A1794C1A7ADE8AD35F1210FCC35A6517931EBF9F7663D37BB3DAD35F573961652FA9A4DF22263E3C9561697BB8A45FBCCDFDA9E16ACB8B4937C8D35F6DAB4353F7F7C83E8CCDFDEDF961696B884937EAD4136F737062D37463D37727A51A7A4A5606
	173F1A10FC351A7A5E88D31F30AE941FC1D35F83FEFCE373E7129F1F5A1FF6CDFD766270748D78712975C5287431F17B9D748CEAAC244D86D8G108E10BFAEC5DF8650BC8E476DDFFB48FBB8E319DF069E33972F8FAF0F65FEFC652AE757B8AD3EC5FEFCE30B1957C272CD50547740976169CB8A499727296F52067074B9C2724DD669E363380F8F105852B85725589744715A26B3DA6BA18BC06E12F1B8DEBB5C07E740160F43F15956127072D49D122F6A86AD3EDF0529AFBDA4DF13CA9F4BF3359573EC3E8173
	1C30B1BC9B7EBEA4DF2CE61E5BB705272FABA4DF12E61E579BB04F49BC4F1BB6075796D47971152B1EF3EE54627B259F5F58723CB4A4DF11263E7852707277F7FEFC6A763CCAD3DF67167074757A712975B5E96A7BECEBF87A367871794D5FB575BD9F263E45FEFC6AF8995254773DED61693B430FCF2DAFDAD35F6E32706233A1A4DF22263E955B4353D79012EF1E263E9F79710D4D1F7F6A47270E17C51A7AEEAA8FAFFFF706644B5054F7644B70741D8949376AFBDAFC6F78710D4D1F6FD5049C6FEB6AFB460F
	EFEC7ADE88292FD6D35F644A707CF9FFC8BE03263EF3BB4253E78B49E7575477514E707C39410F2FDC759C2152473E29F3111E370F0534D9G8CD7FB1A60D261B97DFB58AF7F2E0D6072A3D274A591084B20B8F2F6072C39B15CE0DBDB10BC93FE9ECF913E760C85180E6820BA145F17493DC67984587C26CE8C7C0E75E1D17B9D6BE6D17B9D6BD362878C8FB214DFF042748BE270F730BBC42DF730BF27F9FD091E23F477BAC4DB0F052AE5E3F20B17FCDF81E6E1AD3D8EB6C5A31ABD6A3385824E7BFEBECA4E4C
	D4C7607CEE3938C3701F5F31F3B6A844E0B13A0FF7F4704D35D6174504B67F21B7307F5E6A55761F50276D3F95FD417D77FEDFF07F1D698FEC4B25FEEDDBBA7A35ED31778737C56C8FE48BAB2F5F0A64FB98FE6F479FB37648E74C5FF169A51AE65159DC49E5BF6E8D1C0F59DE6DFC7C33173C9363F1763ACD96352D4FF88BF91ABCBBA7675808B9527781A83FE8F4F7DA7C727AA265E7E90178FF65256F135B4CB63774E6B74B6333D7B00F8D2273A849A31B936CB80FF382C3842E55FB2BC90CC742F3BC409C5E
	7EBC5C01EDDE2AC6DB3294F04B8172EF125358B9280D1DC4DE49CD6C9CD4964717D3FC05824F4EC77D106E451367DAC80B3B896B482601406531FDC03BBCF68E84AE0F1A015065D1B79038BC8E8F04AE0F5601C071494AE5327BDD0C3D8E373387220F71BDB098B82F0F8EEA6775094140F9FDEAB0F4DEFFB298B82F7FB298BA2FAF8C2AE30F5541E7BFDECD6B3047E3E1A7726A0D22914F8EFA14F139037B65E3D1095F49710BC49FFF55213FCA06837BEB5B30363FAA07837B2BFAB8343F768F8776D763F0E8FF
	1D988E9E9BA33D3E3131C342B776A9FDD3A57B2C5F57B7C8BF5B975F3C933FE561D54AA1FB0352D92F9A2C67B9637D6BF9A1604AG3E9D2F2E67EDE6D2EF8D6355755CC87117462B6B79EBA62C67F6C8DBB09E4FF7260EA86D7A0A67EF5A28EFBE2AA47CB1C1C93FDB40F5324E866DF7498E371A5CCE3B1D9C9B626DE0AC5D33A4792F3A995BC1B7ED076C709C8190FBB33603664EE0E34D241B7DFD467C70857D06667C1B557E59C67129B72B7D13E4C57F24C1DA7D84744F3AACDFBF6C127C70C216528F555CEF
	7769FC63824B3FAE5BD74ED7927E3D9C251CBDDC4E8BB93EF2507F3D393EF2F6CB78EFD67CD9A12712FF3BC43F5217FE0F041F3752073E8269A7667952D7CB78FBBE77215F01742635BE781D085FC25F37D03435958C3FF81D8FFD95525FDF2C70C31DBCAE38FBA4F0DBB3E3C43B2DC998895C56BCB6923A2D19BD92382D7951C86836E67EC870366674FAE5DE65F6F84D97AA1FD5CABE7B4247E7DFA17D548DAA7A9D2CFEDDB803630C0489D83F4E52774D5260B993A07F962CDF47B54F6A145E426AD56DAD7E75
	0B69B6167858348B719FEDD45934136959E7555253AC49B78754734AC69FBD3B91FF67A6151EAAE90DD573BB3FDEC9FEE4C0BD67B7AB4BC46E83BFA8D56959A5FDE728D4C9AF778B77EED162F7F1FC4BD6A5DEEEE7D7ECD362F7F37CD3BEF839FDF1147978239A717B362B6C7C0A615F5C6EC33F87696714AB71BBF87D3F71CB151C5D8C7FD5058FBDEFDF167B62F93B10DC6903676D42249DAA79558C5F34D319DF399D6CBBC93E15E5B7DEF5760A02306AD6924B371F44FD16B8A84F59GE9G9937307D96569F
	BDCE7B618E3731872A9E1D3CAD253C2D271157G4FD60089G89G73EF45B67F0DEC92CB6C4D7DCA490ECD43B2EE874E91C65FD356F3E91EA32DED1ED747E145EDF26C555F96285FCA277301365BD87F74AE473FC571DE851E75DBA394CF1EE3A0ED79ED384E71E3FA6E76D2E40AFEA892F1BF37605AC794473DC57B62B4F84E64385FD13AD5372768CBEE47F6E2DB274073D625387F270EE06B957F27A01DFB392EBAD81E53EB50DF5537239FB6EFC5FFB5F15C59AC74D7AC47AD647E1A457DF541AC7B28D3F13F
	4CF47D7D25B7DFFF9D7771F159BC4873425BFD63B97D76C0717D9BBA4E933E4F62B81B6317D1FC3E824F625BCB7164399852A23E0FBE9F6C4432D97AFD74F929CD76F325627EA96B7577478B7F97F4184E285B14D593E5BF97CF8C64731F513E3FEAA2736DCA0E7FA545B7A97052BB3794CF1E4D10B6FFA27A3C036EDD17C52568EB239077DC9F16C3A347992CC89743F1CFDA11AE1E63769910AE03636EB5A01DF0873E4B7664C99C1BB783EE429D5846D7DEA267B9D45F0CB86836198F98BB1BE80F4146C3EF53
	F5E042BF778E9C8FEDBBC5E4E642F37E2494FD69A47C9E09F793FE27A3710199427297353F3B12E69856FDBBC99A3BC8D772FE145033D847F4DEBD0DBA63A0FD61A402FB7034725DD05A2FE43335EC167E32CFBA99C3BEA3A8535FEDD32693FB1DB94A74FDBE6944C77BA4FE163767B6E25E16BFB7CB30BF99917037B30A6FE4F92A172ED1CB6E4AF27DE6A01B1551B13A0EB584E9D1F7E2394D3F006516F4A75647B72A09BDB951500FG54831881A222B1DD58C352632259F70666C6336F7E10F776D9FAC4F5B8
	F52AC452C36D0F166BC404F41FA3DD25F34FE1694895646F442FD460719B6F590A6B9AA1FBAF71334BED6930385D96F32BED000C938AA753F9B96D27534026F400F80ADBEA2917785BA77B57E92C2B59923800A68260A369B9E632BC37E57A2F50B9AC8C04ECBD3DBDD4BB533D015649F4384F1C0CE31FFF37D0DA23D7C57BA13533E4CA0A3E8200610A76921C991EDDD350762F37226D31D334ED4FD640AC2019B7C536FD619459F62663965A5FAE817CB22EF7A53525E8AA545B2998FF33E9FF508E4F63B86E83
	1A3FE8F81E43F15D3C9DDAB09563AC3D036C8F944760731B7419FCE30ABD0F780CBF885DDF3FED1F6A23A37B2FA81FG9B7BCE626749EBA18F4DGAE00A800D9B1681F01B3D83797F05CE36750E7995CC729340F283F8B6A9A40043B901FC6711152376148775024331A3D216A991193AA9F7512AEED9F0E0654B116BD33EC4137CEAB71EB0461BE7A7E3545ABA65EC5FE93E9A3A195D86C3F5AD79FC22E921681BF010F24F84F19A647BE135D18A74BCE3686131D9BC2F63E1F6CF1FAD9761306E032D70610DD24
	104D4A619F7D5E6D9EDB9DF8B6CCBE5D29EB65E3631F37699820F41AFA0DF045E9BADE1A26DEA32CBC056310EFA1ADFE9A3E37E87AB329D7F9F7C3BF87D08BE0G9081226E467D147382CC31031FA9B5B8DDCD16BE0BDD3175A635BF272FB5FF2C3F7F71CA6C6F131EFAD818BD3EBE60B747626FE6F5EA1EF4AD7D75DDD9644A7277CEB749DF8624654726682BG1AGAE81F8E3B1FFB369BBA264E42C59C65EF6B51A2C746BF1FCD356E6691751E4C24E6A539DE77C67777D67311D0A0CE5737BF8F20DE5FB71AC
	E6DF3900B13BA0B6F4BF3384E8B2E365FEE655BD6ABE5255C33EC361B20AA6ABFF573549F859E2175277FE661F4E5E05E5696BEB48D8C6756DACAA00250FB7E824D381AC7D48E76FD3794A8F4861F71AF76DD10E6B8A6FE1FE2805ABCB1FEA56CAEF06AB2B6B4B48677CA5BBFFEAA13FCF6030749773EFB8545B20C8480F98156E2A5AD5D63C23E20B94BFF17D4AEF85D47F79C64D6F0A53367B9ED686DE693A7C23E9D9EC9C7C178EDC0B1C85E96B288DD0D7847CDB760D960E59BD1DAC69CA367C2FD5984BC50F
	5CAF547F22DE37A100ADCBA59B2A6F1547654B1D4559CC267E0A6C0BDF8573450E6A5D4C970C2F41AD73CD6A5894042F32EAF7D5754E2F2AA4F6095F6A11790B360E258C187F1CDB51FF2EFB2F55FF77114FFBC87A8F75497A7F759A726D6A17791EB1DDCB3E994F731D6477B17AACEE11FE1D08EE869DB03A1B1DF61B69AA0E0169580D7CA6C6423DB88EAD351236476AF45B861C8E511F93470775F4BCC75E33DDFAAF368DB7723E32F43AF65B209764362196E81A274BED03F93A3AEDF81E6E054B9BD92CF232
	72E163CEC26B1A0E733C5FF42A67F9F7E4A967E905D675BC0F704B69072D6AF91EAF7F10D5BD4FC3FE56FEA670F3688953D97BB9972E2953593708180FFEFA8EFD149E40C74AB6D408CB51974449BEAA0DD37B28E095795E4BB1A77DB9G1F337152BE1A034441A927DDB4F6DA7C0898CD3F0950A8FFD7C0C9435A01674FA95B247CE9F69D5A214EC3D59C4B5737F1F2BB60DFD730FEAC744A77FF3AAA5F2F9DA4772C9F4EBEAD5F2FD25C6FBEF57D57FE1A2F2B8E56077786796D0D21G3F3DF1E3005F5E189062
	37B722027C7646D41F5F5EE0E3105F8F12189721AC23G12G6683AC0A43714746EE72BDB276258D755823415CC9767A48AF880A28BF4F9EB82F2B6D21FFF73050CE76B7AEBBA1B4FB08FC442F351372921DAE0AE4388DF662A3372D53AAB2D2893F057E36065D52C1515237AE4F6398B39372D6F19F3EB7725B73584ED86F43B52C0FAE905AB8F81E8D30005366DF44BEE369FD575AE7BCB3AC887C1B469D813EE9CC47B5CC17FD8F6AAA1E017AFFE6C6DC5B8C9CA33BC788AE8A1E93E7E0196DF2106F0E1CB2F6
	791F4083E4335B6902EE63AA4352DF07106A425A8E65F8B89964254D88BEF7E4B67CE01434F999C01BFFBF0CE3811AGAE81F881A28162G12G128116GE4822C0ACF519783D4G54833445237DCB1CDB0258AFADC45240F39BED74DBDB527B07BE67736DA0EFDCFCF07BD97B18E5A57B624A4F6DA9CED7141FEAE8B19C2A5B5FF431352E65F8D353017DAD9777B71D5EFF2615731F336E8C425FF83C65505963478CDA92D839BE415F63090E3F56383A1504AE6E9985BD5321852CEE0CBF0BF5A39974674C6471
	F6966BC66DCC2C9B4BA92D884F11G711C760F67B08FF3E60EB58F5897BC2A93F8AC1D255DA20B03247E0D0162GDA1F43606F63173BE048EFB0F6F21EB7031CB9FBFBB0F47B33E208FE239B5A8D43D517F46E605DA1B217F2F6F4F8AC3C1D4B9F4A6738C31EE62747C64EAB30F2186937C7B9363E6809B079522F190F4D47EF216DDC46CC75BCFD92458B8F2867691FDD40B8D985E92E87F08D6A230BCA7D2900DF7AC070FA47782E6C5132FB39441B7FE0207528974CDAFCA592FD35869FAB337E91F24F7BCE56
	74C9E9A3A3249F645F63245F9DED85C2C7A7DB2B5BC567DBCDA0FBF5B66EED56D01C99F039D9A86769514071776CE86878CB99556E7F960E7A77FF0BC7357B3FDF0FAA7BBF06DBBACA6AE6A7341DED1C6EF50EBB43F1EFD01984F75AE6962D9C1FC96DA278C68BD108787F22F675D3DC1B044B96E45C999C9788248EC1162D641B0B8C771EC0567CBAECF6BBFB7E0052589CD02538BFBE26ED0A4DE5D4587D9947913BD9D95C36553FFC2236AA4B07454EEC1F73B263A0ADDAA3DE7CE3AE72ECF8F5F3DA18FCAFDC
	B39F2B3B2EF3240EA6BC282E53D7A9BE69C1F51D561D43BABD9752CA9230CEAFBF2754BF81703189E37153448B823F0FD15C2F3FF07D4772B557D5870B29320B98E7D99CD7F99163CC46CD206337D960333989385E7F7A9EDC6FCF6638574CC8D77490620E7073A4258F611E2C111E4B125AA7DAB5993E081E351770D8B5A59B1E377A5B1AEAD55A4A7C56F7CEAE27D145FD5E396BDFE66B2F2B8E1667B56D7EFEA8ECD77A010D63266F715D03F80ABCCAEB79B9B54156725573FA75DA3E77A175BE4162DCD976E5
	3EAF997110761AC1EE9059B1FE3229EB2531651D969C1745BF046B169B7264B20E31A86B79A220D926E1033FCF9F33787BB449227469A7542729C7DF0307445705DCD148BB66AC264B8F22701E34E2411F255589766CF18B9F7AAF056D7F7E4B3FC83CD8DF2093F4070495D69A6B07045CFED2993C6497B52087572D875E551C77906DCB527AF656E47F7A7EEC1D10830C2CEFBDA464108E92785DCCCEB6742DD4CA0E82844F95E104FA9DE4E72D131FC65FF9679DE59A68A5CF39574CCF43C079B7C62C4E872B54
	199344927A3DE7E50AA82C26EB6F457C1787C4A11F2D264B28B11824D0A5FD8FF7D4391E14F913A9609AF7C2A20B37B90972FA921B8B5C5CCCEC4D84FC4123B8B77C45F1321E99E8CE505AE334C3DC593B77BB4CE4FEF0602AE81157C6A21B09FC8F52C809AC6D4EE6E2F71702673F81D0CB8788F8BFCE6746A5GGAC06GGD0CB818294G94G88G88GDAFBB0B6F8BFCE6746A5GGAC06GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAG
	GGGA6GGGG
**end of data**/
}
}