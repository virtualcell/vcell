/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model.gui;

import java.awt.Color;
import java.awt.Insets;

import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;

/**
 * Insert the type's description here.
 * Creation date: (11/18/2002 2:01:41 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class ReactionElectricalPropertiesPanel extends javax.swing.JPanel {
	private Kinetics fieldKinetics = null;
	private boolean ivjConnPtoP2Aligning = false;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private Kinetics ivjkinetics1 = null;
	private ReactionStep ivjReactionStep1 = null;
	private javax.swing.JCheckBox ivjCurrentCheckbox = null;
	private javax.swing.JCheckBox ivjMolecularCheckbox = null;

private class IvjEventHandler implements java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == ReactionElectricalPropertiesPanel.this.getMolecularCheckbox()) 
				connEtoM11(e);
			if (e.getSource() == ReactionElectricalPropertiesPanel.this.getCurrentCheckbox()) 
				connEtoM12(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ReactionElectricalPropertiesPanel.this && (evt.getPropertyName().equals("kinetics"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == ReactionElectricalPropertiesPanel.this.getReactionStep1() && (evt.getPropertyName().equals(ReactionStep.PROPERTY_NAME_REACTION_PARTICIPANTS))){
				ivjCurrentCheckbox.repaint();
			}
			if (evt.getSource() == ReactionElectricalPropertiesPanel.this.getReactionStep1() && (evt.getPropertyName().equals("physicsOptions"))) 
				connEtoC4(evt);
			if (evt.getSource() == ReactionElectricalPropertiesPanel.this.getReactionStep1() && (evt.getPropertyName().equals("physicsOptions"))) 
				connEtoC6(evt);
		};
	};
/**
 * ReactionElectricalPropertiesPanel constructor comment.
 */
public ReactionElectricalPropertiesPanel() {
	super();
	initialize();
}

/**
 * connEtoC1:  ( (MolecularCheckbox,item.itemStateChanged(java.awt.event.ItemEvent) --> ReactionStep1,physicsOptions).exceptionOccurred --> ReactionElectricalPropertiesPanel.setSelectionsFromPhysicsOption(Lcbit.vcell.model.ReactionStep;)V)
 * @param exception java.lang.Throwable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.lang.Throwable exception) {
	try {
		// user code begin {1}
		// user code end
		if ((getReactionStep1() != null)) {
			this.setSelectionsFromPhysicsOption(getReactionStep1());
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
 * connEtoC2:  (kinetics1.this --> ReactionElectricalPropertiesPanel.kinetics1_This(Lcbit.vcell.model.Kinetics;)V)
 * @param value cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(Kinetics value) {
	try {
		// user code begin {1}
		// user code end
		this.enableControls(getkinetics1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (ReactionStep1.physicsOptions --> ReactionElectricalPropertiesPanel.setSelectionsFromPhysicsOption(Lcbit.vcell.model.ReactionStep;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getReactionStep1() != null)) {
			this.setSelectionsFromPhysicsOption(getReactionStep1());
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
 * connEtoC5:  ( (CurrentCheckbox,item.itemStateChanged(java.awt.event.ItemEvent) --> ReactionStep1,physicsOptions).exceptionOccurred --> ReactionElectricalPropertiesPanel.setSelectionsFromPhysicsOption(Lcbit.vcell.model.ReactionStep;)V)
 * @param exception java.lang.Throwable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.lang.Throwable exception) {
	try {
		// user code begin {1}
		// user code end
		if ((getReactionStep1() != null)) {
			this.setSelectionsFromPhysicsOption(getReactionStep1());
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
 * connEtoC6:  (ReactionStep1.physicsOptions --> ReactionElectricalPropertiesPanel.enableControls(Lcbit.vcell.model.Kinetics;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getkinetics1() != null)) {
			this.enableControls(getkinetics1());
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
 * connEtoM11:  (MolecularCheckbox.item.itemStateChanged(java.awt.event.ItemEvent) --> ReactionStep1.physicsOptions)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if (getReactionStep1()!=null){
			getReactionStep1().setPhysicsOptions(this.getPhysicsOptionFromSelection());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		connEtoC1(ivjExc);
	}
}
/**
 * connEtoM12:  (CurrentCheckbox.item.itemStateChanged(java.awt.event.ItemEvent) --> ReactionStep1.physicsOptions)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12(java.awt.event.ItemEvent arg1) {
	try {
		if (getReactionStep1()!=null){
			getReactionStep1().setPhysicsOptions(this.getPhysicsOptionFromSelection());
		}
	} catch (java.lang.Throwable ivjExc) {
		connEtoC5(ivjExc);
	}
}
/**
 * connEtoM7:  (kinetics1.this --> ReactionStep1.this)
 * @param value cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(Kinetics value) {
	try {
		// user code begin {1}
		// user code end
		if ((getkinetics1() != null)) {
			setReactionStep1(getkinetics1().getReactionStep());
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
 * connPtoP2SetSource:  (ReactionElectricalPropertiesPanel.kinetics <--> kinetics1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getkinetics1() != null)) {
				this.setKinetics(getkinetics1());
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
 * connPtoP2SetTarget:  (ReactionElectricalPropertiesPanel.kinetics <--> kinetics1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setkinetics1(this.getKinetics());
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
 * Comment
 */
private void enableControls(Kinetics kinetics) {
	Kinetics kt = kinetics;
	boolean bEnableMolecular = true;
	boolean bEnableCurrent = true;

	if (kt != null){
		if (kt.getKineticsDescription().isElectrical()){
			bEnableCurrent = false;
		}else{
			bEnableMolecular = false;
		}	
	}else{
		bEnableCurrent = false;
		bEnableMolecular = false;
	}
	
	getMolecularCheckbox().setEnabled(bEnableMolecular);
	getCurrentCheckbox().setEnabled(bEnableCurrent);
}
/**
 * Return the CurrentOnlyRadioButton property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getCurrentCheckbox() {
	if (ivjCurrentCheckbox == null) {
		try {
			ivjCurrentCheckbox = new javax.swing.JCheckBox() {
				@Override
				public String getText(){
					if (getReactionStep1()!=null){
						Model model = getReactionStep1().getModel();
						Membrane membrane = (getReactionStep1().getStructure() instanceof Membrane)?((Membrane)getReactionStep1().getStructure()):null;
						if (model!=null && membrane!=null){
							Feature inwardCurrentFeature = model.getElectricalTopology().getPositiveFeature(membrane);
							if (inwardCurrentFeature!=null){
								return "include electric current (into inside compartment \""+inwardCurrentFeature.getName()+"\")";
							}
						}
					}
					return "include electric current (into inside structure \"undefined\")";
				}
			};
			ivjCurrentCheckbox.setName("CurrentCheckbox");
			ivjCurrentCheckbox.setText("include electric current");
			ivjCurrentCheckbox.setActionCommand("Current");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurrentCheckbox;
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
 * Return the MolecularOnlyRadioButton property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getMolecularCheckbox() {
	if (ivjMolecularCheckbox == null) {
		try {
			ivjMolecularCheckbox = new javax.swing.JCheckBox();
			ivjMolecularCheckbox.setName("MolecularCheckbox");
			ivjMolecularCheckbox.setText("include molecular flux");
			ivjMolecularCheckbox.setActionCommand("Molecular");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMolecularCheckbox;
}
/**
 * Comment
 */
private int getPhysicsOptionFromSelection() {
	boolean bMolecularSelected = getMolecularCheckbox().isSelected();
	boolean bElectricSelected = getCurrentCheckbox().isSelected();
	if (bMolecularSelected && !bElectricSelected){
		return ReactionStep.PHYSICS_MOLECULAR_ONLY;
	}else if (bMolecularSelected && bElectricSelected){
		return ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL;
	}else if (!bMolecularSelected && bElectricSelected){
		return ReactionStep.PHYSICS_ELECTRICAL_ONLY;
	}else{
		throw new RuntimeException("must select at least one mechanism");
	}
}
/**
 * Return the ReactionStep1 property value.
 * @return cbit.vcell.model.ReactionStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReactionStep getReactionStep1() {
	// user code begin {1}
	// user code end
	return ivjReactionStep1;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION in ReactionElectricalPropertiesPanel ---------");
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
	getMolecularCheckbox().addItemListener(ivjEventHandler);
	getCurrentCheckbox().addItemListener(ivjEventHandler);
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
		setName("ReactionElectricalPropertiesPanel");
		setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = 0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.anchor = java.awt.GridBagConstraints.LINE_START;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0,4,0,0);
		add(getMolecularCheckbox(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 2; 
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		add(getCurrentCheckbox(), gbc);
		
		setBackground(Color.white);
		getMolecularCheckbox().setBackground(Color.white);
		getCurrentCheckbox().setBackground(Color.white);
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
		ReactionElectricalPropertiesPanel aReactionElectricalPropertiesPanel;
		aReactionElectricalPropertiesPanel = new ReactionElectricalPropertiesPanel();
		frame.setContentPane(aReactionElectricalPropertiesPanel);
		frame.setSize(aReactionElectricalPropertiesPanel.getSize());
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
			connPtoP2SetSource();
			connEtoM7(ivjkinetics1);
			connEtoC2(ivjkinetics1);
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
/**
 * Set the ReactionStep1 to a new value.
 * @param newValue cbit.vcell.model.ReactionStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setReactionStep1(ReactionStep newValue) {
	if (ivjReactionStep1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjReactionStep1 != null) {
				ivjReactionStep1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjReactionStep1 = null;
			this.setSelectionsFromPhysicsOption(newValue);
			ivjReactionStep1 = newValue;
			
			/* Listen for events from the new object */
			if (ivjReactionStep1 != null) {
				ivjReactionStep1.addPropertyChangeListener(ivjEventHandler);
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
 * Comment
 */
private void setSelectionsFromPhysicsOption(ReactionStep rs) {
	if (rs.getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_ONLY){
		if (getMolecularCheckbox().isSelected()==false) {
			getMolecularCheckbox().setSelected(true);
		}
		if (getCurrentCheckbox().isSelected()==true) {
			getCurrentCheckbox().setSelected(false);
		}
	}else if (rs.getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL){
		if (getMolecularCheckbox().isSelected()==false) {
			getMolecularCheckbox().setSelected(true);
		}
		if (getCurrentCheckbox().isSelected()==false) {
			getCurrentCheckbox().setSelected(true);
		}
	}else if (rs.getPhysicsOptions() == ReactionStep.PHYSICS_ELECTRICAL_ONLY){
		if (getMolecularCheckbox().isSelected()==true) {
			getMolecularCheckbox().setSelected(false);
		}
		if (getCurrentCheckbox().isSelected()==false) {
			getCurrentCheckbox().setSelected(true);
		}
	}
}

}
