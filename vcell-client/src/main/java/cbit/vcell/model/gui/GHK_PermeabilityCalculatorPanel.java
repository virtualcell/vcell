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

import cbit.vcell.model.Kinetics;

/**
 * Insert the type's description here.
 * Creation date: (4/4/2002 4:56:32 PM)
 * @author: Jim Schaff
 */
public class GHK_PermeabilityCalculatorPanel extends javax.swing.JPanel {
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.model.FluxReaction ivjFluxReaction = null;
	private GHK_PermeabilityCalculator ivjGHK_PermeabilityCalculator = null;
	private cbit.vcell.model.GHKKinetics ivjghkKinetics1 = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel10 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JLabel ivjJLabel4 = null;
	private javax.swing.JLabel ivjJLabel5 = null;
	private javax.swing.JLabel ivjJLabel6 = null;
	private javax.swing.JLabel ivjJLabel7 = null;
	private javax.swing.JLabel ivjJLabel8 = null;
	private javax.swing.JLabel ivjJLabel9 = null;
	private javax.swing.JTextField ivjJTextField1 = null;
	private javax.swing.JTextField ivjJTextField2 = null;
	private javax.swing.JTextField ivjJTextField3 = null;
	private javax.swing.JTextField ivjJTextField4 = null;
	private javax.swing.JTextField ivjJTextField5 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GHK_PermeabilityCalculatorPanel.this.getJTextField1()) 
				connEtoM4(e);
			if (e.getSource() == GHK_PermeabilityCalculatorPanel.this.getJTextField2()) 
				connEtoM5(e);
			if (e.getSource() == GHK_PermeabilityCalculatorPanel.this.getJTextField3()) 
				connEtoM6(e);
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == GHK_PermeabilityCalculatorPanel.this.getJTextField1()) 
				connEtoM7(e);
			if (e.getSource() == GHK_PermeabilityCalculatorPanel.this.getJTextField2()) 
				connEtoM8(e);
			if (e.getSource() == GHK_PermeabilityCalculatorPanel.this.getJTextField3()) 
				connEtoM9(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GHK_PermeabilityCalculatorPanel.this.getGHK_PermeabilityCalculator() && (evt.getPropertyName().equals("ghkKinetics"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == GHK_PermeabilityCalculatorPanel.this.getGHK_PermeabilityCalculator() && (evt.getPropertyName().equals("conductanceAtNernst"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == GHK_PermeabilityCalculatorPanel.this.getGHK_PermeabilityCalculator() && (evt.getPropertyName().equals("insideConcentration"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == GHK_PermeabilityCalculatorPanel.this.getGHK_PermeabilityCalculator() && (evt.getPropertyName().equals("outsideConcentration"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == GHK_PermeabilityCalculatorPanel.this.getGHK_PermeabilityCalculator() && (evt.getPropertyName().equals("permeability"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == GHK_PermeabilityCalculatorPanel.this.getFluxReaction() && (evt.getPropertyName().equals("chargeCarrierValence"))) 
				connEtoM3(evt);
			if (evt.getSource() == GHK_PermeabilityCalculatorPanel.this.getGHK_PermeabilityCalculator() && (evt.getPropertyName().equals("permeability"))) 
				connEtoC1(evt);
		};
	};
/**
 * GHK_PermeabilityCalculatorPanel constructor comment.
 */
public GHK_PermeabilityCalculatorPanel() {
	super();
	initialize();
}
/**
 * GHK_PermeabilityCalculatorPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public GHK_PermeabilityCalculatorPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * GHK_PermeabilityCalculatorPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public GHK_PermeabilityCalculatorPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * GHK_PermeabilityCalculatorPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public GHK_PermeabilityCalculatorPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoC1:  (GHK_PermeabilityCalculator.permeability --> GHK_PermeabilityCalculatorPanel.firePropertyChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.firePropertyChange("permeability", arg1.getOldValue(), arg1.getNewValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (ghkKinetics1.this --> FluxReaction.this)
 * @param value cbit.vcell.model.GHKKinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.model.GHKKinetics value) {
	try {
		// user code begin {1}
		// user code end
		if ((getghkKinetics1() != null)) {
			setFluxReaction((cbit.vcell.model.FluxReaction)getghkKinetics1().getReactionStep());
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
 * connEtoM2:  (FluxReaction.this --> JTextField4.text)
 * @param value cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.model.FluxReaction value) {
	try {
		// user code begin {1}
		// user code end
		if ((getFluxReaction() != null)) {
			getJTextField4().setText(String.valueOf(getFluxReaction().getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_CarrierChargeValence)));
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
 * connEtoM3:  (FluxReaction.chargeCarrierValence --> JTextField4.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getFluxReaction() != null)) {
			getJTextField4().setText(String.valueOf(getFluxReaction().getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_CarrierChargeValence)));
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
 * connEtoM4:  (JTextField1.action.actionPerformed(java.awt.event.ActionEvent) --> GHK_PermeabilityCalculator.conductanceAtNernst)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getGHK_PermeabilityCalculator().setConductanceAtNernst(new Double(getJTextField1().getText()).doubleValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM5:  (JTextField2.action.actionPerformed(java.awt.event.ActionEvent) --> GHK_PermeabilityCalculator.insideConcentration)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getGHK_PermeabilityCalculator().setInsideConcentration(new Double(getJTextField2().getText()).doubleValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM6:  (JTextField3.action.actionPerformed(java.awt.event.ActionEvent) --> GHK_PermeabilityCalculator.outsideConcentration)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getGHK_PermeabilityCalculator().setOutsideConcentration(new Double(getJTextField3().getText()).doubleValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM7:  (JTextField1.focus.focusLost(java.awt.event.FocusEvent) --> GHK_PermeabilityCalculator.conductanceAtNernst)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getGHK_PermeabilityCalculator().setConductanceAtNernst(new Double(getJTextField1().getText()).doubleValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM8:  (JTextField2.focus.focusLost(java.awt.event.FocusEvent) --> GHK_PermeabilityCalculator.insideConcentration)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getGHK_PermeabilityCalculator().setInsideConcentration(new Double(getJTextField2().getText()).doubleValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM9:  (JTextField3.focus.focusLost(java.awt.event.FocusEvent) --> GHK_PermeabilityCalculator.outsideConcentration)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getGHK_PermeabilityCalculator().setOutsideConcentration(new Double(getJTextField3().getText()).doubleValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetSource:  (GHK_PermeabilityCalculator.ghkKinetics <--> ghkKinetics1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getghkKinetics1() != null)) {
				getGHK_PermeabilityCalculator().setGhkKinetics(getghkKinetics1());
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
 * connPtoP1SetTarget:  (GHK_PermeabilityCalculator.ghkKinetics <--> ghkKinetics1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setghkKinetics1(getGHK_PermeabilityCalculator().getGhkKinetics());
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
 * connPtoP2SetTarget:  (GHK_PermeabilityCalculator.conductanceAtNernst <--> JTextField1.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getJTextField1().setText(String.valueOf(getGHK_PermeabilityCalculator().getConductanceAtNernst()));
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP3SetTarget:  (GHK_PermeabilityCalculator.insideConcentration <--> JTextField2.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		getJTextField2().setText(String.valueOf(getGHK_PermeabilityCalculator().getInsideConcentration()));
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP4SetTarget:  (GHK_PermeabilityCalculator.outsideConcentration <--> JTextField3.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		getJTextField3().setText(String.valueOf(getGHK_PermeabilityCalculator().getOutsideConcentration()));
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP5SetTarget:  (GHK_PermeabilityCalculator.permeability <--> JTextField5.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		getJTextField5().setText(String.valueOf(getGHK_PermeabilityCalculator().getPermeability()));
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the FluxReaction property value.
 * @return cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.FluxReaction getFluxReaction() {
	// user code begin {1}
	// user code end
	return ivjFluxReaction;
}
/**
 * Return the GHK_PermeabilityCalculator property value.
 * @return cbit.vcell.model.gui.GHK_PermeabilityCalculator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GHK_PermeabilityCalculator getGHK_PermeabilityCalculator() {
	if (ivjGHK_PermeabilityCalculator == null) {
		try {
			ivjGHK_PermeabilityCalculator = new cbit.vcell.model.gui.GHK_PermeabilityCalculator();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGHK_PermeabilityCalculator;
}
/**
 * Return the ghkKinetics1 property value.
 * @return cbit.vcell.model.GHKKinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public cbit.vcell.model.GHKKinetics getghkKinetics1() {
	// user code begin {1}
	// user code end
	return ivjghkKinetics1;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Conductance at Reversal Potential");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JLabel10 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel10() {
	if (ivjJLabel10 == null) {
		try {
			ivjJLabel10 = new javax.swing.JLabel();
			ivjJLabel10.setName("JLabel10");
			ivjJLabel10.setText("cm/sec");
			ivjJLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel10;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("nS/um2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("Inside Concentration");
			ivjJLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			ivjJLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setText("uM");
			ivjJLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}
/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel5() {
	if (ivjJLabel5 == null) {
		try {
			ivjJLabel5 = new javax.swing.JLabel();
			ivjJLabel5.setName("JLabel5");
			ivjJLabel5.setText("Outside Concentration");
			ivjJLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel5;
}
/**
 * Return the JLabel6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel6() {
	if (ivjJLabel6 == null) {
		try {
			ivjJLabel6 = new javax.swing.JLabel();
			ivjJLabel6.setName("JLabel6");
			ivjJLabel6.setText("uM");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel6;
}
/**
 * Return the JLabel7 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel7() {
	if (ivjJLabel7 == null) {
		try {
			ivjJLabel7 = new javax.swing.JLabel();
			ivjJLabel7.setName("JLabel7");
			ivjJLabel7.setText("charge valence (from reaction)");
			ivjJLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel7;
}
/**
 * Return the JLabel8 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel8() {
	if (ivjJLabel8 == null) {
		try {
			ivjJLabel8 = new javax.swing.JLabel();
			ivjJLabel8.setName("JLabel8");
			ivjJLabel8.setText("");
			ivjJLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel8;
}
/**
 * Return the JLabel9 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel9() {
	if (ivjJLabel9 == null) {
		try {
			ivjJLabel9 = new javax.swing.JLabel();
			ivjJLabel9.setName("JLabel9");
			ivjJLabel9.setText("Permeability for GHK");
			ivjJLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel9;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField1() {
	if (ivjJTextField1 == null) {
		try {
			ivjJTextField1 = new javax.swing.JTextField();
			ivjJTextField1.setName("JTextField1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField1;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField2() {
	if (ivjJTextField2 == null) {
		try {
			ivjJTextField2 = new javax.swing.JTextField();
			ivjJTextField2.setName("JTextField2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField2;
}
/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField3() {
	if (ivjJTextField3 == null) {
		try {
			ivjJTextField3 = new javax.swing.JTextField();
			ivjJTextField3.setName("JTextField3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField3;
}
/**
 * Return the JTextField4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField4() {
	if (ivjJTextField4 == null) {
		try {
			ivjJTextField4 = new javax.swing.JTextField();
			ivjJTextField4.setName("JTextField4");
			ivjJTextField4.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField4;
}
/**
 * Return the JTextField5 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField5() {
	if (ivjJTextField5 == null) {
		try {
			ivjJTextField5 = new javax.swing.JTextField();
			ivjJTextField5.setName("JTextField5");
			ivjJTextField5.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField5;
}
/**
 * Method generated to support the promotion of the permeability attribute.
 * @return double
 */
public double getPermeability() {
	return getGHK_PermeabilityCalculator().getPermeability();
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
	getGHK_PermeabilityCalculator().addPropertyChangeListener(ivjEventHandler);
	getJTextField1().addActionListener(ivjEventHandler);
	getJTextField2().addActionListener(ivjEventHandler);
	getJTextField3().addActionListener(ivjEventHandler);
	getJTextField1().addFocusListener(ivjEventHandler);
	getJTextField2().addFocusListener(ivjEventHandler);
	getJTextField3().addFocusListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GHK_PermeabilityCalculatorPanel");
		setPreferredSize(new java.awt.Dimension(400, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(400, 200);
		setMinimumSize(new java.awt.Dimension(400, 200));

		java.awt.GridBagConstraints constraintsJTextField1 = new java.awt.GridBagConstraints();
		constraintsJTextField1.gridx = 1; constraintsJTextField1.gridy = 0;
		constraintsJTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextField1.weightx = 1.0;
		constraintsJTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTextField1(), constraintsJTextField1);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
		constraintsJLabel2.gridx = 2; constraintsJLabel2.gridy = 0;
		constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel2(), constraintsJLabel2);

		java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
		constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 1;
		constraintsJLabel3.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel3(), constraintsJLabel3);

		java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
		constraintsJLabel4.gridx = 2; constraintsJLabel4.gridy = 1;
		constraintsJLabel4.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel4.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel4(), constraintsJLabel4);

		java.awt.GridBagConstraints constraintsJTextField2 = new java.awt.GridBagConstraints();
		constraintsJTextField2.gridx = 1; constraintsJTextField2.gridy = 1;
		constraintsJTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextField2.weightx = 1.0;
		constraintsJTextField2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTextField2(), constraintsJTextField2);

		java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
		constraintsJLabel5.gridx = 0; constraintsJLabel5.gridy = 2;
		constraintsJLabel5.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel5.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel5(), constraintsJLabel5);

		java.awt.GridBagConstraints constraintsJLabel6 = new java.awt.GridBagConstraints();
		constraintsJLabel6.gridx = 2; constraintsJLabel6.gridy = 2;
		constraintsJLabel6.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel6.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel6(), constraintsJLabel6);

		java.awt.GridBagConstraints constraintsJTextField3 = new java.awt.GridBagConstraints();
		constraintsJTextField3.gridx = 1; constraintsJTextField3.gridy = 2;
		constraintsJTextField3.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextField3.weightx = 1.0;
		constraintsJTextField3.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTextField3(), constraintsJTextField3);

		java.awt.GridBagConstraints constraintsJLabel7 = new java.awt.GridBagConstraints();
		constraintsJLabel7.gridx = 0; constraintsJLabel7.gridy = 3;
		constraintsJLabel7.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel7.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel7(), constraintsJLabel7);

		java.awt.GridBagConstraints constraintsJTextField4 = new java.awt.GridBagConstraints();
		constraintsJTextField4.gridx = 1; constraintsJTextField4.gridy = 3;
		constraintsJTextField4.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextField4.weightx = 1.0;
		constraintsJTextField4.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTextField4(), constraintsJTextField4);

		java.awt.GridBagConstraints constraintsJLabel8 = new java.awt.GridBagConstraints();
		constraintsJLabel8.gridx = 2; constraintsJLabel8.gridy = 3;
		constraintsJLabel8.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel8.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel8(), constraintsJLabel8);

		java.awt.GridBagConstraints constraintsJLabel9 = new java.awt.GridBagConstraints();
		constraintsJLabel9.gridx = 0; constraintsJLabel9.gridy = 4;
		constraintsJLabel9.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel9.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel9(), constraintsJLabel9);

		java.awt.GridBagConstraints constraintsJLabel10 = new java.awt.GridBagConstraints();
		constraintsJLabel10.gridx = 2; constraintsJLabel10.gridy = 4;
		constraintsJLabel10.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel10.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel10(), constraintsJLabel10);

		java.awt.GridBagConstraints constraintsJTextField5 = new java.awt.GridBagConstraints();
		constraintsJTextField5.gridx = 1; constraintsJTextField5.gridy = 4;
		constraintsJTextField5.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextField5.weightx = 1.0;
		constraintsJTextField5.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTextField5(), constraintsJTextField5);
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
		GHK_PermeabilityCalculatorPanel aGHK_PermeabilityCalculatorPanel;
		aGHK_PermeabilityCalculatorPanel = new GHK_PermeabilityCalculatorPanel();
		frame.setContentPane(aGHK_PermeabilityCalculatorPanel);
		frame.setSize(aGHK_PermeabilityCalculatorPanel.getSize());
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
 * Set the FluxReaction to a new value.
 * @param newValue cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setFluxReaction(cbit.vcell.model.FluxReaction newValue) {
	if (ivjFluxReaction != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjFluxReaction != null) {
				ivjFluxReaction.removePropertyChangeListener(ivjEventHandler);
			}
			ivjFluxReaction = newValue;

			/* Listen for events from the new object */
			if (ivjFluxReaction != null) {
				ivjFluxReaction.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoM2(ivjFluxReaction);
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
 * Set the ghkKinetics1 to a new value.
 * @param newValue cbit.vcell.model.GHKKinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void setghkKinetics1(cbit.vcell.model.GHKKinetics newValue) {
	if (ivjghkKinetics1 != newValue) {
		try {
			cbit.vcell.model.GHKKinetics oldValue = getghkKinetics1();
			ivjghkKinetics1 = newValue;
			connPtoP1SetSource();
			connEtoM1(ivjghkKinetics1);
			firePropertyChange("ghkKinetics1", oldValue, newValue);
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
