package cbit.vcell.model.gui;

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
			getJTextField4().setText(String.valueOf(getFluxReaction().getChargeCarrierValence()));
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
			getJTextField4().setText(String.valueOf(getFluxReaction().getChargeCarrierValence()));
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
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G430171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E165DC8DD4D4E55E377203920B8ADB5C45C2232222228D0DB6B3AA5C25224D5D57DA36175DE33B565A9EAA5FE277755D6CF465C3B0302828E5B7CADA353065A585DC11348D2594A5A5458993F36E4CDCE0F43E3CF387C6BE647DBF77F97E4F3DB3F307A14EF64E1E73F24E1FBB77777FFC3EBFE7040A7589C52B73AB84E1F511407E362F960454EF84611A0326DB910BF119CA855D5FFBC02F894EF30570CC
	G1EA34BD4DAAE88A3A5701E8A6FCB6EB7157E99BE3FA1C459AA0E4307B0FEF23F960436D797BEB6361FCB8E211FE275DDF21741739520B440483350CD7C0F38AB991E43F024557960CB1509F3D7B1EC9F3C1B0112009638297C92F82ED5BDCFE9EEE2F25D17A689F98F48796B089D1DCD24C68F556DE24FEA61CF2B05B34457BDEA1CC4A786F819C0A47185C2452EE6F86E6CB85D7B0955E2D76A7A2D1243D167F45BA4C7DD2F4F5E5C3CF75F01139D126C14C40B5DE1D74E6E919DD61FC3D45CF20768129C02B0
	2C5ADF6BA9D16D89C2AB3C1748847B1B1578CC0777EC20959C5FA6913C9DBE5B0006E5CC672E8FF632F4F48D5DAEAC6BDD347655B0690C15E97A9648DABA53BE34947CC5AD1307BBC85E2F84DE1557D43A852893C8810A85FADB2D8B0F9E7A901EAD96E50F5B69F43BDABC1E5D8E1B7C85C9B7FCEEEE868E438ECBD637EC9384DA5EBFF1579A44B396D13DC73E359E4D132486AC7B6443B78BB937DD11DFEC10A7B9C9C37949DE1A9799DEADCF286FD7BC61FCAF7386564DC05FDF89FA5F42D37545C5863E2B9E2D
	AFDE496C14A91A6FFD8AA96BAB54BAD08B1FD7B33B943FDA4523F5B82D27B5AACE5E1300D766433A51758A4652DEB8D7A85F7ABD43FC083BB7BA5F6323B1C47902E31952C3EC277A284F2DBC16EBD53C3ADF43E9AC9F2B75143CC7832F52EFAA2575AB7BB0E93709E7CD2569C059C0AB004A87CD2535C01F8EE29DDB5F7151B86BD807A8AB3BFDF607CD12852A1B5D7196BCA53FA20BEDAE2FA23A2C12C9146D2245A1F985A673F8874983CB005E13EA5D37020F3D12CB12C5456EF601F551A9A9126C25B24FF63C
	C6E4E4C965F65A1D92B8F0FA84347B5F9DEF4053A1FA15239E1B28C81A84653F54C14A24DFB401C5B0G1FE93E94F5127CB2C37A7DC0D1BC9F863FA4768EC8B274974D4D6DEE77E91F271324C4489EA4793CC96DBB6261F36AA066716A23842B9AB215B68E211DDB3FFCDDE72755EEA5610B72D9ECE37156F2D226A02FG4583258EE17DF9268B6BCF6B3A1B045AC7E69A561F98284BD9C3345EAC9B8AEEC7572B76C90CE543D056C06D43B88E54D93B8C5AD1AA33218C993733F0C4E25F275321BE769A7AD84B1E
	C55F42BE5AAC503D53FCF71BC899BAA0DDD1C0C943187739DFD6533A3CDB74CAD8FB8F49D071A41BD692B44E69BDE6163785AC2EE296A799D04958752AAAC76C8A421BC0FFE67E092FF700568BF43C5DG34896883204DC0FF877ADF200FF81EA42B3E52A14E65C065A32652A6A0B3D094D08AD0B610F04ED4DA8354816487CAG4A84DA81D4B98AED96288FA886A88DE8A9D049A498BB003AA6E19937C813D6FF77697AB6E5787F11E8D9A42AE571DD164356FF03347E3B936D9BFEF318643754AF3914D649A339
	EC70414BF8CF9DA6654246319EB757F9D645BDB06E6D0101464B71BF9CA665A6AA0AEC377894690824A8F6D72F46FFD1756575D9BC5A10093E8A0F103A609E00F10F4B2FBDC2468C2B5B651254CE0A2BB0FE29BAE6C98E4989C16FF17BDC8A45DF6BA4737C5352D9B6E63E5939C6A0E31E43C746DFBAF7CE91C9FD7348F6A7F4FDE4889796CFA26359CECF25AE0FA681C656G0970CC1B04635140869C0FB68EDEAF241F74649B4D2D622E39A43F72BCBA8E541E270DC7B426B5C70DE3AFD347CE06FBE92C4EB324
	4EC71A4C97287A6F9C786B18328213BBF810786F6DBBFD406E12943B553B1562CFECA078678E1F7F30A42A39CE4716242F54F9BF24438C64BF8F47162823040F63BA9BD476389D8EDAE440DBF29E4957E72D953A3C09B90F66AB795CFABE613FEDC3FE953CB71E9F4C3F1B732361BDD14727F178CCE4FC77CAE7FC92CC37488465GAF7FE1536BC1BC932B8B14BFD9DDACBAF4F31B0A8B084F3C9EDC63ADB91F4E9BF24F27F307C2603F8BB815DB580BF2668B281C9F1EF1C0E981F2B1FDA8174964164213388F14
	7B460EF2551329DC93BC3BC264EACF211C1F494540B3C9A737F4B211DB4965B2A7EB713733A741BDD354F41CC6395AA9F48CEE1DC2653B60D9476D799DA837E00AE6E749946AD73377099365722650313C154917CD555B7B93172B1D2A71C36D253BD02EF5AA1D93985BD3785A3CFBAAAD87E5AA4D1F5429FAFB59BEDE3ED3695C025843F2536CC57723DC6ED4DADEE82FF01A5E5E5783A837F19A1D2390FBD83E1A3DD77CA837F39AADD7345799E0AF074BA95368DC07586B8A3197F39665222751789327992557
	B608F2B953681C49B83D15C33C5C26517851DEE0FAFF4D654A22685C4BB83D178D73FC092271233D4074F60D70FC0922F33860742E08A2F2E567D02EB30A5AF1C4517EE0B2F9C6617E48731BC9DF2E6E0F1C927BC5FF1DF7G462366667DEA07C977BE2ED557AF24DF5FF9A16EF15CFF0674F58A3CC783A5DE087D7E4F56619EC76C69A4A167292D456176F296DCC8533574C22D5F4FDA51320676254FF5911F5597419C75A26CC7DFB26957D9ADDE2F5BEAD717F82C0F3A79AB7D38C0315B3EFA991EBDF6592B34
	41406D477E6C0E2FC83CE48D48E0B666E56C5AA6CCF03FC840FD05114F75F9306055117C52B0F950FA91CD3B6DA25DBE0F3B33182E4FF75B4A84BA96A582BF8728E2BA41C7EDE46CA67DAC75E7EDC0FFB103578866195F8F6B2FEBBA75631F2E792BBD3732087AFB127BCB817E7269D8C77EFEE0C338BCE9EE568F03ECDF611DDE34D39D8DF593080E1BB3EDA4AE5DF84A465924DE129E254F6E2569F92E65D396EF564DF305346268FC23FD323E6B6272E322E9BA5222F5F5E80E25E0A9900D65F7BC161299308E
	1A01319C6CA3F58966CDB61FD55DFDE8D18EC24572AAECACFE592B9F0B7DB3685ABD016B8F7629B143440F7A39238F7D2402CCAE172B3693BFF61757EE137638410FCB211B964C4F071B0C7CEC3C987A88556EB4BB5AED824CF3B15AFD46CE745CBEA544B0355BE6B6321B446D9EBD05F697G16476DAED0F10F2E02D3FBEBACC676EAAFC1FB6F1DC6FB1F82663F846B66CD6F6A4B32C41CE2D81651CF756627DCC24BB07B925D3E566516026E4BF9D9F29F55B1D02FE2789E6FA764294EDD851C3B199DC4D6E695
	7308A2F9E8BC4347B01EC9D929C2659BB38C63D15BD28C2BDBB1DABC95D02FEAF85D1A6360F56BD228DBC0B42F360BC6F9653F94732A5709FA5100E5DC0AF93F54C9742CFD225CAB6D91E559AE49B089A373B12A374105FA2BC02771B2347794479D00A5F2FC038F719C40AAAEC7BF97796A61C9F3260EBE20C76BF1C3195BEA48A8D0A78EA8F56AFA272EC5E5FFC6BEEF463CDD688B57F6285F557D68379DFCCE3E9C6319477185006571F8BE9D8857C6285E858328D79BEBAA3545223D6DFE446381DB920B766E
	7507EB9BD42F68AC6A154D0471F3A65A3B03634300254E64EDF8106C2577382DBEEF1D7A3F5D6DD502B36AF34240FC2276E68C223DDC3035718A7453B204F8B7E0319CAF9EC6BC8B30426FA1FEA747F78226F07C3C91441381DB4671ED6788CE5AC4D59C4C9F62B09D7167025BB6EDBF056D8784F50C14BAE4B730E1D06B93DD3DD2CD3B1AC687FC2EBB828BD6D76F47BA641065942CDF8C104DA6F6E67233F3E4BCA6FB9751F1589F4CFDC35F9F6C9C7D46307DC53D5A171F91C75BDDEE1C567E16DEE6A9488322
	FBA4B772FD7F26D9D426FD163627E8064F4A2C40FDC93A9FFE13CC72A1EA965D8FDF4F71C5AA3ED807537CFCD81D6B10774A6FC3BB7A816E612E6BA6F32772F8688701DA013A0106016263F14E525255A0C458A3EFF3A950FAC4471100C5BF6ED5F7113203F5BAFBFF21CB5F5751B806CE107A1E86BE17G6582954E067AB69BE3B87FD84498BE739B46C073F91F0D64F36B6C716F71D2BD132A577DAD74E89D597C357EBC3070357D798586F5C49DB7186D7859DA9D21F93271A449139C40D78215DF89EBA120FD
	D7E21EE49D2B0D14A7F89E41B20326A9F702F97127EF1D97D4EFC3EF24F3353EAB83EDD23D2D9175220374E83E8D1A55339F602583E5836581D5DE85638DD057D518FFFF7DFAF7247CEB41CD2ECEB77F38A7F8238A63FDFA0269FCA1221E672AC0BD5A67ED2AA169B4D877F442146EF33B643051BE2C450B673ED1D761BE795DFD930B752108FA69C131529844CD98C3B60FE106FDE2B17CA022DEF902D18C134C98C3CD8246F07C5444E230C754B39B46304BD2424A4F4FE378607444E25896D1AFA54128FDBCAB
	12FA13893CE5C02B00AA6718CAF782F5818D4F417661787AE3A1D21FCBA68B50B65466905AB0D83B18E07ADE082897BF47A8FDEEABC9DFAA709683AD87AA1C8BFDA7D0ABD05FDCCC5F42486D1F9F4B0E277DDF6718D8BA67C5541BB45728AE39C42CCBF1F331AEB91D930BE1A8225E6239C6FA1BDD937337BD225EF2C3FF4FCE505F0B9175042B8D53671BE07AA26AD59A7AAB6E1F183FB7A36AEDB174F767847D5D9FD1EF1F213F8B87A6666FFB91754C067EF679A7666FC0C4BD3F213F52339373771708FAB106
	7E6E1E203F07A26AA5997A3BFCF0E27E66C454CBB77437F7E8E27E0EC6544BB67437EEF8E27E5E0E283742505FBD937477F0C43D12C4A33DE9A39373B7B322DE35213F0F4F856BB58549997BEB8B51FBB7683D53505F232A1E0371A6854984D3ECA22E031759481CA1895EB300F200D682D5DDE3AAED826A3E86678C2F9D5FA50CEFCE348730FDEE77E90369823BD3F52FFAA6B18C366300A8F6D4A6D8B23C27F32CDEA158B2F8DF4531A3BE75EC61DA086FDA9C7BEF13499EFABB3C5B007CC051C089C029C0D9C0
	CB0172004A2EB315D683B58135831901146B504E032A9DF2DE948DD8B2D096D06EF5744C27AC49D4DA1BC44FEB5A1368791D831E51C049C974DC125D27BC330265B309E32CF2A031927FE34928F5E79CB2C8A4DDD9AC7D55AC0D6DACAF885F467243C837EC1CFE728C74E95AABE4CC7B5A88B6087C7AF1787A4BB853CD0854D532A7C54EAA483D9AF21F062CFB56B1FE11C06F5E98693EB9CE9FEF04717B0670D6F7783BA66FF49B5FB55950EDFC57E4F377EB94879B7A9B18485F5ABD76DD149D5D616F22DCF0A2
	FC1C971DB00EB36604F11CD71C98BB4EABCF040B13164D23796ADE3F4F25581D12EA1A2E71A5BF3983853D4AE11F0BF6B9443AB7F8FFC740354F469EF2261315047D5536EF6A05507E6AB31BDDF14BE824D39265C3BDBDFC01F344A3D9B16E5A136173286124F19E6DB8E91CC77FBCB9F69E6DBDB9F69E7DCEA1F5B3641E50C1D82189382F7FF56FDAA6532138BB36F25B134D6153B15DEC1C0ECB4D243DF85DBE59AA85479AE72E64BC2D07EFD3A4A789074A4CD17595F294921256A4D51F7242695FEAA66D57E2
	3759E5F64B0626F1E31FBE0D24FCE91F7339F72CB1FAF9D2E8DFC56D7DB140DE83375733E9ACFB2B425A4B306B6DB5F2FBD34CE359AB3FBE1C3D4BCE696DED6376F6DB463237916CB5855924763EB90D76BED35CDFE8F564FF4470F5C430985711B51670F5E41DA5F29DF94B923E0E3CE309DCC75E3704EBB32C2EB8CAB16FFAE43793DA8455BBEB8D1F56C9D66334CE3506CF6B8CEB6434DEE18D1F56595648E93D56EA5C9EFE6D501727569E3C62D875239D6A065930FEA4BA83732C9E4FE3DDFABF8DDC7EFDF7
	20FC834A7B74720DDC7E137EC079C606BF572F175F46655387826537B1DC716B657F4165F71F8D147F87367333FA796DDC7E0141C0796D8CBFB72417EF62722D430172CD8CDFB92C175F4165970E844A6FE07D797DEF11BA6801A1C222270E2D22A2B29D791C4E569E52C6693C77E975BCCE01F21BFCBD4E058F0F92AC865E93B8B6DB45D260BD7DFA9C8B1F31052F73FF3099577997EC616BFC3E94394E17CA616B7C6BD264BA7F36B4F67B7E2507587748D20FA44B126D08FD100C05A5B710BCF8CEDDDFAC0674
	67DC0F67278E195C11F508E75DBEC59052D43947E53C8B5B8E6FB620E820049B6819BF1D075C2A56419ED9F402FBF50290FB23FAC7C3C1DD815EAB009A01BA01149B5167A99F5185C53B5367D4A32471B5751378F2C0EEB917EDBEC6E435930C2DAC1DAFD711B63D3FDD34C88E446697EB983651793BB48C5B61568FB48C5B5A7122C0BF283F482D49E2BB3B6A0006E1DB7A2FEF827551D7262C49E2BBFA3EA9D096E358DC2449E29BAABE29E1BB987652ABA5DA5A37D0EC7B3AC01B5B8423BBB39549348C1B1271
	DE47DDEDC67CF64EBFEF07915F4C79FFEDB062AB1CFF5B3091BF0A7357DB0C78711CFF03E844CF617CE4C37D8C4E1FE3281F4D79AFDB0D78394958C754742C5337B7B63724FD4A89AF2E43051B301F995904D88747624D08A5F06C188531959CFBD045BE3D995A4B4D08DDA422DC9F47BA38DC5A4D58CE8AC749BC9F3A0E2DD9B713EB4DCFBB845EA7581157306596752E1D7ADEFE8B6A26A23F1E729FD05F9578CCED9DF2E936764B5570F4C983557C2EC8AB2CD0DC64CBE5B50DDB3654D657EF5932191EF9A78C
	684F3C1BDEBE5F703EBC2158DB68381B441E51AF4DE25F01DD6B456F40AE85DEE58A76BFDB4D81E71DA41066663D325D36DB6CEDD7FBB3FAA67225DAC6C43FA3856FF90E2AB62360BD81A8AD856FF1785F47BB0749574E9596BFE1FC4FF3E967C87E62949AE7DE0A6E0E5E0716826A532D204FAD37C2395D2A0EBF0A3E1E113C3C94846A60C9798F8770C99FCF2E5CE87C55BEBD1F447BDE011E5F9040AF52695334BD388953366A419441B1EFF2586FA3275C4A3E4B4C1E55BF1D353AE6D6479ADA9776FB343A10
	FCE6BCF5015D496D89F3A7D76D45746731C43E6C0726D20A6535A1564A31349DF8F7F718E3DFB6603D1CD40E7DFE9831DC0E1978F96B465B90FBDCC439EE0E7D5602D8AC473239DC9647DA2C089526429CA4D53D076EEDE5F939B355F88D035FC7A35481B2B620B7D9DC7ED4FAD7079035973B896DE50531272725C17672C26C5DE6C6FB42BCE3FBC5BAFB9573826D554C8B366731203D46B076566A6C6D8B32572D33C76BD52F1A77124D2E102D8CBAB74833E01B6A1B0779BCC7C43F43E17C966B7C468479CD54
	7925BE4EF19FA97330CFF878383E8FAB7F3EA39FF5027DE53098964F537A84F6C7F814FC8F05744DF5B033BBA88D28DFC329590CEDF317247731E528BD6C5D20427954F645FC2D1FE477AC83FC9CF258388F3A8FB4FA06242D95744C73F1EC0BF5116F4E1944D3213BC0G623DB90A2A6658BA6A55B177950C086A3DB3C1BD53375623F75F1ED77B79C97335F22678AA950F5661F42C1942471AB8602D1A0FE781A96AFC58FCBB34F920D820E420AC206537E3DE7E4A35C508F08E5069763443E243215B6A2269D3
	14097D4E40BD455827277E6886A1EDEAEB58BB722BEE277525AA0D5D7F4D1A351ABCE97A7628F376F6607581C583A5822581ADCE4374EDF2333BE524BA34A80AE86DD33F1140B79E6D520086138541D8FBBC4BD89CAB53426D177FCF1B11DE599D2CED5D91CE6F2E9DC6FA0DCCFE9FFBF6435343BE33BA56E024B719494431E7324EAF3BFFE0F86E164E6416306772903D6306FBD52B18DC790F4225AF59E624371149B731E77B0F027DADB774E7E6F28AFBC605687D4AF04FA41E492530E7C6085E67D6A33DECA6
	174B1E421D1A9E9DCF2F716278DC813C7582BDD7A89170EF45BFC3B1F26F164CCD9A7A49E75AB69EAF4336D1F56355C26BAFDA734977BD0C5AC6BB0BA17ECE2D5F2E31B72E2571AC5A0471AC89938F75BDFBC060E9D8264DE3960C25136E57F46670F9C96B18BAAB4F12B75A6717DA09FC0F6870CA5C4E4F799D2061B16D3CBD245919E9515B21FDC2264A27ED7FE28F793E3561B881ADFFBFCC6768AD69CE31176B7FF0182CA1D297E07F71F34F5F4259G101C0D525B58FCFF7E8CA98B3E6E22438F6E4F1FA1F3
	DD3B4B2E58C507FDD0245F06A05A2C6FA05BA12C9E66AB5A6769DE235FE278D7917B0E7BF76A03FD4FCB8E3FEF15AE9B6FDB7DD8B6BE03491247BEAFDBA207BFAF7BCFD97F5DEDDA6E0BC6C859E4C239AF83AA394BD4DA853465AE2C87F6AF298774A6ED709872192D176C1B11DFCED150FF3B92BE2DC714483F37522D106F4AD95C0A62F6927B08DB94D2DE0A5B2302946BD5C89E49765EBE050AB25C292E679CD20F8A6B57746D102E6E3BF0DD3EDADB2BF32CFCBF4A25F16C4CFE144B6058129DA8D7319031ED
	4DA8D74DB1311EE09EF80F6558BC95CB0277CC0EB99B50DE8E47FE5A007616F36C210D285BF4B7E2EBC6D0371DE33DA3285B4531E5E228DF59023AC9DC6EBF78DEC7AA476E3321DCE1BAE29BA514AB6758AAF3280F17388FB317B3DBD15741319EB1B4EDF9A26AAE60F2C7F9FC199CFB58926A770495F5AB6FC1ECA1773B11E38F8678257D428D81F3D885E4236E997BCE81EDCF9BC6499AA79EE45300F2007200CA6E05BA815488348F289BC8810A86CAGCA854A844A864A85926E4376676FA77B82617AE13601
	22B6C2D93413E6487A3587824E7EAA40DE4DFDE347CF751E8A187F6D841D0EF169DD9BB0DF31010EFFDCFA3F88182FC403CEC284BD1AAF730749BE38B7E08CFAE8A4FC5F33E4A4F25F732B913A0E6D15BA4FFA587E7DEFC6089FF7CF0FD762FD5C6FC64AB956666DF0FB6DE4C423E373EDE67CCEA69D37172A4B953A7E2D646B5FD403B46A575404EC3AFD8102E75E272DFF286D02824DF654FE343D4440F6D1845B4BC3EC3FDF2859EEEFC65BF9E162AE88E31B78AD49885EAF7057133268F34B76C137CB919DE4
	939CF39157761757631ED1C5866EFBE6553F9120576230773A480844F41E64BA1FE660DEE853D1AD9DCFB4E0BA9C9946F9B4D6BAA6E5846FBF7CF1A34E5D22393F63A64D5F39917417E660EFF5104FE0FFD99941E5F26EAB4D766F799ECEF618340CE5BBB7447693DD1A6D0FA5343DB2CC5C45E358AEDB94EC3B64EB4D760095EDD7AEB20EFBAC5B35A1367FFEDC336D60F952E4E09BFF83AF1C6D76905B873BB55B0F70383B4244BD16ED074EB69DF36ED157CDFE06D36C47AA9643B05A57EC9F8D5F27FDB29A39
	CF5BBBEABC1F7AE2B4F4BEF5F854F8BE55B52A1FCFD16C44A8C9D7AF0CBFDDDC4E4231E39C6BD5ED926CAB3BCD696338D30D0B607BA46290F12F9A57000AF5B14C2F430EB1ECE414744B1064BE7223876CBB066AB9C80F5D61E0779DD4993B8B3AE91E9F5355986D9ED197F78C470E7132F8FCD3E879BC39C9DFBE949344D039A2B1D46EAECB285C3D16D039E78C6CBD9FE00F2E9B5B783AB1F99176BDE7CE881CFF47FE64AF637C58EFB47EC8B3722BFE0C7C5BCF8A827651B754D743D33775DFF7047C47693E37
	6EF05B5EFD1DD47645F149D23FEFB5225FCE6E77B13366F75D2675E35BEA7F6C73CE1606DBC6F87F4BED3DA8EAE9FC5B0A7C8C4E17EC9A5FA1A1FF9567C7C99ABF096BB77E8479B776E87C59FC8F4041790F75EA7CB5B664A7F27E0B2489303ED9FC3FCC606BE235F9D03C4A3FCE4331F9307952410079D28E58DD7113714C33966E576B95E6421CB8F3BCFA67B76B75EAC16753F1693D5D28576B841D3EF16925865CD39F861D18F169D5876CDFA502CE5A38744EC8FA3DAC50D9BAAE3D1B827C6501CE497D6351
	1B9BB0CF2E821DAD6352AB8D58C7EC851D2E88FA346F108C7A13D3867D53A37BC3659E5B9FAAB72BB9D46E4A66D0397A46D0396D0D21F28F0E044AAD9E89155BEE8D155BE98D150B12C265E6C821F29986766EB730F707C13F3D50205F7E1BADD46E831BDE0E7EBE7605439766C31F7D34D02488E57A1FC1704A426B214732AD977F729749A7DBAB72057CB6E1CD1F5A973489C58324337013235DCC18D444F84F8867689AC1FD1305D73925B6F574DB5A5F50DC705B2B8B7205C2B0C3E783EDC2A19952411ACC2D
	9640ECC035D928A755F6613F9ED5047776E83F5ED1ADAA5507E116A3FBC5C7F507DB01D98E74FD0A3046F564A31F33DE91AA5A541F66288E78A98DC54877FD2188EF9E22BF33E148FC177E16C9F5BF7D95136A9AF2052D9AFF14E5B3742CE0DC7FE3CA55BDEE39FA6F3E83605C6A7C48ABD93F2B9C78F7C0857EFB87237AB34D15D32CE16FFE24E431B304ACED7F1D6EFD5854F922701029346AA15C3FC8BDC7FE6BB35CDE74912768F040CA6EF40B4BC67625F71FD5A4EDAF53BECA6CFBD106B1A94F234EABCF69
	F47E8FD0CB8788FFB82ADF419CGG5CDEGGD0CB818294G94G88G88G430171B4FFB82ADF419CGG5CDEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7B9CGGGG
**end of data**/
}
}
