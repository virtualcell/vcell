/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;

import org.vcell.util.BeanUtils;
import org.vcell.util.Coordinate;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.plot.gui.TimeFunctionPanel;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.mapping.CurrentDensityClampStimulus;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.Electrode;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.TotalCurrentClampStimulus;
import cbit.vcell.mapping.VoltageClampStimulus;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (10/26/2004 9:58:14 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class ElectricalStimulusPanel extends javax.swing.JPanel {
	private JSortTable ivjScrollPaneTable = null;
	private SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP1Aligning = false;
	private ElectricalStimulus ivjelectricalStimulus = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimulationContext ivjsimulationContext1 = null;
	private ElectricalStimulusParameterTableModel ivjelectricalStimulusParameterTableModel = null;
	private GeometryContext ivjgeometryContext = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private ElectrodePanel ivjgroundElectrodePanel = null;
	private ElectrodePanel ivjpatchElectrodePanel = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private JButton btnGraphElectricalStimulus;
	private TimeFunctionPanel timeFunctionPanel = null;
	private JComboBox clampComboBox;
	
	private enum Clamp {
		No_Clamp("No Clamp"),
		Voltage_Clamp("Voltage Clamp"),
		Total_Current_Clamp("Total Current Clamp"),
		Current_Density_Clamp("Current Density Clamp");
		
		String title;
		Clamp(String t) {
			title = t;
		}
	}

	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
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
				setupElectrodeFeatureListProvider();
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == clampComboBox) {
				newStimulus();
			}
			
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
 * connEtoC1:  (simulationContext1.this --> ElectricalStimulusPanel.setRadioButtons(Lcbit.vcell.mapping.SimulationContext;)V)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		this.setClamp(getsimulationContext1());
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
			this.setClamp(getsimulationContext1());
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
			this.setClamp(getsimulationContext1());
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
		getScrollPaneTable().setModel(getelectricalStimulusParameterTableModel());	
	} catch (java.lang.Throwable ivjExc) {
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
 * Return the ElectrodePanel property value.
 * @return cbit.vcell.mapping.gui.ElectrodePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ElectrodePanel getgroundElectrodePanel() {
	if (ivjgroundElectrodePanel == null) {
		try {
			ivjgroundElectrodePanel = new ElectrodePanel();
			ivjgroundElectrodePanel.setName("groundElectrodePanel");
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
			ivjJPanel1.setLayout(new BoxLayout(ivjJPanel1, BoxLayout.X_AXIS));
			JLabel label = new javax.swing.JLabel();
			label.setText("Electrical Stimulus:");
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			
			ivjJPanel1.add(label);
			clampComboBox = new JComboBox();
			for (Clamp clamp : Clamp.values()) {
				clampComboBox.addItem(clamp);
			}
			clampComboBox.setRenderer(new DefaultListCellRenderer() {

				@Override
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					super.getListCellRendererComponent(list, value, index, isSelected,
							cellHasFocus);
					if (value instanceof Clamp) {
						setText(((Clamp) value).title);
					}
					return this;
				}
				
			});
			ivjJPanel1.add(Box.createHorizontalStrut(4));
			ivjJPanel1.add(clampComboBox);
			
			ivjJPanel1.add(Box.createHorizontalGlue());
			ivjJPanel1.add(getBtnGraphElectricalStimulus());

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}


/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			TitledBorder tb = BorderFactory.createTitledBorder(GuiConstants.TAB_PANEL_BORDER, "Patch Electrode", TitledBorder.CENTER, TitledBorder.TOP);
			getpatchElectrodePanel().setBorder(tb);

			tb = BorderFactory.createTitledBorder(GuiConstants.TAB_PANEL_BORDER, "Ground Electrode", TitledBorder.CENTER, TitledBorder.TOP);
			getgroundElectrodePanel().setBorder(tb);
			
			java.awt.GridBagConstraints constraintspatchElectrodeLabel = new java.awt.GridBagConstraints();
			constraintspatchElectrodeLabel.gridx = 0; constraintspatchElectrodeLabel.gridy = 0;
			constraintspatchElectrodeLabel.fill = GridBagConstraints.BOTH;
			constraintspatchElectrodeLabel.weightx = 1.0;
			constraintspatchElectrodeLabel.insets = new Insets(0, 0, 0, 2);
			ivjJPanel2.add(getpatchElectrodePanel(), constraintspatchElectrodeLabel);

			java.awt.GridBagConstraints constraintsgroundElectrodeLabel = new java.awt.GridBagConstraints();
			constraintsgroundElectrodeLabel.gridx = 1; constraintsgroundElectrodeLabel.gridy = 0;
			constraintsgroundElectrodeLabel.fill = GridBagConstraints.BOTH;
			constraintsgroundElectrodeLabel.insets = new Insets(0, 2, 0, 0);
			constraintsgroundElectrodeLabel.weightx = 1.0;
			ivjJPanel2.add(getgroundElectrodePanel(), constraintsgroundElectrodeLabel);
			 
			java.awt.GridBagConstraints constraintsparameterTable = new java.awt.GridBagConstraints();
			constraintsparameterTable.gridx = 0; constraintsparameterTable.gridy = 1;
			constraintsparameterTable.fill = java.awt.GridBagConstraints.BOTH;
			constraintsparameterTable.gridwidth = 2;
			constraintsparameterTable.weightx = 1.0;
			constraintsparameterTable.weighty = 1.0;
			constraintsparameterTable.insets = new java.awt.Insets(4, 2, 2, 2);
			ivjJPanel2.add(getScrollPaneTable().getEnclosingScrollPane(), constraintsparameterTable);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
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
		} catch (java.lang.Throwable ivjExc) {
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
	clampComboBox.addActionListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
}

private ElectrodePanel.ElectrodeFeatureListProvider electrodeFeatureListProvider = new ElectrodePanel.ElectrodeFeatureListProvider() {
	@Override
	public Feature[] getFeatures(ElectrodePanel electrodePanel) {
		ElectrodePanel otherElectrodePanel = (ElectricalStimulusPanel.this.getpatchElectrodePanel() == electrodePanel?ElectricalStimulusPanel.this.getgroundElectrodePanel():ElectricalStimulusPanel.this.getpatchElectrodePanel());
		ArrayList<Feature> features = new ArrayList<>();
		Structure[] structures = ElectricalStimulusPanel.this.getsimulationContext1().getModel().getStructures();
		for (int i=0;i<structures.length;i++){
			if (structures[i] instanceof Feature) {
				if(otherElectrodePanel != null && otherElectrodePanel.getElectrode() != null && otherElectrodePanel.getElectrode().getFeature() != null){
					//exclude from list the structure name set for 'otherelectrodepanel'
					if(!otherElectrodePanel.getElectrode().getFeature().getName().equals(structures[i].getName())){
						features.add((Feature)structures[i]);
					}
				}else{
					features.add((Feature)structures[i]);
				}
			}
		}
		return features.toArray(new Feature[0]);
	}
};

private void setupElectrodeFeatureListProvider(){
	getgroundElectrodePanel().setElectrodeFeatureListProvider(electrodeFeatureListProvider);
	getpatchElectrodePanel().setElectrodeFeatureListProvider(electrodeFeatureListProvider);
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
		setSize(567, 430);
		
		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 2;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.weightx = 1.0;
		constraintsJPanel2.weighty = 1.0;
		constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel2(), constraintsJPanel2);
		
		initConnections();
		connEtoM7();
		connEtoC3();
		setupElectrodeFeatureListProvider();

	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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
private void newStimulus() {
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
		Clamp selectedClamp = (Clamp) clampComboBox.getSelectedItem();
		if (currElectricalStimulus instanceof VoltageClampStimulus){
			if (selectedClamp == Clamp.Voltage_Clamp){
				return;
			} 
			
			String response = PopupGenerator.showWarningDialog(this,"warning: the present voltage clamp settings will be lost", new String[] { UserMessage.OPTION_CONTINUE, UserMessage.OPTION_CANCEL }, UserMessage.OPTION_CONTINUE);
			if (response==null || response.equals(UserMessage.OPTION_CANCEL)){
				clampComboBox.setSelectedItem(Clamp.Voltage_Clamp); // revert back to Voltage Clamp
				return;
			}
		}
		if (currElectricalStimulus instanceof TotalCurrentClampStimulus){
			if (selectedClamp == Clamp.Total_Current_Clamp) {
				return;
			}
			String response = PopupGenerator.showWarningDialog(this,"warning: the present current clamp settings will be lost", new String[] { UserMessage.OPTION_CONTINUE, UserMessage.OPTION_CANCEL }, UserMessage.OPTION_CONTINUE);
			if (response==null || response.equals(UserMessage.OPTION_CANCEL)){
				clampComboBox.setSelectedItem(Clamp.Total_Current_Clamp); // revert back to Current Clamp
				return;
			}			
		}
		
		if (currElectricalStimulus instanceof CurrentDensityClampStimulus){
			if (selectedClamp == Clamp.Current_Density_Clamp) {
				return;
			}
			String response = PopupGenerator.showWarningDialog(this,"warning: the present current clamp settings will be lost", new String[] { UserMessage.OPTION_CONTINUE, UserMessage.OPTION_CANCEL }, UserMessage.OPTION_CONTINUE);
			if (response==null || response.equals(UserMessage.OPTION_CANCEL)){
				clampComboBox.setSelectedItem(Clamp.Current_Density_Clamp); // revert back to Current Clamp
				return;
			}			
		}
		
		if (currElectricalStimulus == null && selectedClamp == Clamp.No_Clamp){
			return;
		}		

		StructureTopology structTopology = getSimulationContext().getModel().getStructureTopology();
		Structure[] structures = getSimulationContext().getModel().getStructures();
		ArrayList<Feature> features = new ArrayList<Feature>();
		for (Structure structure : structures){
			if (structure instanceof Feature){
				features.add((Feature)structure);
				}
			}
		if (features.size()<2){
			PopupGenerator.showErrorDialog(this,"error: electrodes must be placed in distinct volumetric structures, found "+features.size()+" volumetric structures in model");
			return;
		}
		Feature groundFeature = features.get(0);
		Feature clampedFeature = features.get(1);

		//
		// selected "Total Current Clamp Stimulus"
		//
		if (selectedClamp==Clamp.Total_Current_Clamp){
			if (simContext.getElectricalStimuli().length==0 || !(simContext.getElectricalStimuli()[0] instanceof TotalCurrentClampStimulus)){
				Electrode probeElectrode = new Electrode(clampedFeature,new Coordinate(0,0,0));
				TotalCurrentClampStimulus ccStimulus = new TotalCurrentClampStimulus(probeElectrode,"ccElectrode",new Expression(0.0),simContext);
		System.out.println(" Geo's dim = "+simContext.getGeometry().getDimension());
				simContext.setElectricalStimuli(new ElectricalStimulus[] { ccStimulus });
				simContext.setGroundElectrode(new Electrode(groundFeature,new Coordinate(0,0,0)));
			}
		}
		//
		// selected "Current Density Clamp Stimulus"
		//
		if (selectedClamp==Clamp.Current_Density_Clamp){
			if (simContext.getElectricalStimuli().length==0 || !(simContext.getElectricalStimuli()[0] instanceof CurrentDensityClampStimulus)){
				Electrode probeElectrode = new Electrode(clampedFeature,new Coordinate(0,0,0));
				CurrentDensityClampStimulus ccStimulus = new CurrentDensityClampStimulus(probeElectrode,"ccElectrode",new Expression(0.0),simContext);
		System.out.println(" Geo's dim = "+simContext.getGeometry().getDimension());
				simContext.setElectricalStimuli(new ElectricalStimulus[] { ccStimulus });
				simContext.setGroundElectrode(new Electrode(groundFeature,new Coordinate(0,0,0)));
			}
		}
		//
		// selected "NO Electrical Stimulus"
		//
		if (selectedClamp==Clamp.No_Clamp){
			if (simContext.getElectricalStimuli().length>0){
				simContext.setElectricalStimuli(new ElectricalStimulus[0]);
			}
		}
		//
		// selected "Voltage Clamp Stimulus"
		//
		if (selectedClamp==Clamp.Voltage_Clamp){
			if (simContext.getElectricalStimuli().length==0 || !(simContext.getElectricalStimuli()[0] instanceof VoltageClampStimulus)){
				Electrode probeElectrode = new Electrode(clampedFeature,new Coordinate(0,0,0));
				VoltageClampStimulus vcStimulus = new VoltageClampStimulus(probeElectrode,"vcElectrode",new Expression(0.0), simContext);
		System.out.println(" Geo's dim = "+simContext.getGeometry().getDimension());
				simContext.setElectricalStimuli(new ElectricalStimulus[] { vcStimulus });
				simContext.setGroundElectrode(new Electrode(groundFeature,new Coordinate(0,0,0)));
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
	getBtnGraphElectricalStimulus().setEnabled(bHasStimulus);
	getJPanel2().setEnabled(bHasStimulus);
	BeanUtils.enableComponents(getJPanel2(), bHasStimulus);
	return;
}


/**
 * Comment
 */
private void setClamp(SimulationContext arg1) {
	if (arg1==null || arg1.getElectricalStimuli()==null || arg1.getElectricalStimuli().length==0){
		clampComboBox.setSelectedItem(Clamp.No_Clamp);
	}else if (arg1.getElectricalStimuli()[0] instanceof TotalCurrentClampStimulus){
		clampComboBox.setSelectedItem(Clamp.Total_Current_Clamp);
	}else if (arg1.getElectricalStimuli()[0] instanceof CurrentDensityClampStimulus){
		clampComboBox.setSelectedItem(Clamp.Current_Density_Clamp);
	}else if (arg1.getElectricalStimuli()[0] instanceof VoltageClampStimulus){
		clampComboBox.setSelectedItem(Clamp.Voltage_Clamp);
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
			setupElectrodeFeatureListProvider();
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

	private JButton getBtnGraphElectricalStimulus() {
		if (btnGraphElectricalStimulus == null) {
			btnGraphElectricalStimulus = new JButton("Preview Electrical Stimulus");
			btnGraphElectricalStimulus.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					graphElectricalStimulusFunction();
				}
			});
		}
		return btnGraphElectricalStimulus;
	}
	
	private void graphElectricalStimulusFunction(){
		if(timeFunctionPanel != null){
			return;
		}
		if (getelectricalStimulus()==null){
			DialogUtils.showInfoDialog(this, "No electrical stimulus selected (e.g. \"Voltage Clamp\" or \"Total Current Clamp\")");
		}
		
		try {
			Expression protocolParameterExp = new Expression(getelectricalStimulus().getProtocolParameter().getExpression());
			protocolParameterExp = MathUtilities.substituteModelParameters(protocolParameterExp, getelectricalStimulus().getSymbolTable());
			String[] symbols = protocolParameterExp.getSymbols();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; symbols!=null && i < symbols.length; i++) {
				SymbolTableEntry ste = protocolParameterExp.getSymbolBinding(symbols[i]);
				if (!ste.equals(getSimulationContext().getModel().getTIME())){
					buffer.append(ste.getName()+" ");
				}
			}
			if (buffer.length()>0){
				throw new ExpressionException("Cannot preview electrical stimulus containing variables ("+buffer.toString()+").");
			}
			timeFunctionPanel = new TimeFunctionPanel();
			timeFunctionPanel.setTimeFunction(protocolParameterExp.flatten().infix());
		} catch (Exception e) {							
			DialogUtils.showErrorDialog(ElectricalStimulusPanel.this, e.getMessage(), e);
			return;
		}
    	
		JDialog jdialog = new JDialog(JOptionPane.getFrameForComponent(this),true);
		jdialog.setTitle(getBtnGraphElectricalStimulus().getText());
    	jdialog.addWindowListener(new WindowAdapter() {	
    		@Override
			public void windowClosing(WindowEvent e) {
				timeFunctionPanel = null;
			}
    		@Override
			public void windowClosed(WindowEvent e) {
				timeFunctionPanel = null;
			}
		});
    	jdialog.setContentPane(timeFunctionPanel);
    	jdialog.pack();
    	DialogUtils.showModalJDialogOnTop(jdialog, this);
    	

	}

}
