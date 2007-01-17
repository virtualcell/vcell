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
	D0CB838494G88G88GB4FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E165BC8BF4D46519514296AB6D42EEDA5152CA91EB6CC625EE2A38F236292516AE34E20AC237584D8AEDF11B36EC4DF6EDCB5D1B1909E4A4E81428388618D654GB18698B4892F0484C8CC00C8C28890A0B7B3F7E66EE4DE4C5C10498B767B6F7FFF7F3D1339B393F36AB9BDE7B9676366FE6F6FFFFC7F730ED03AF5D6E10129D4908A8A85766F0382C1483AA88839F764C791D77DCE0ED5507D7B9340CB02
	55E201E79350CAA767D8CB841B29885E3B61FD647A9C6BEB7077C6E11B3774BC7C1140CE5EB9C1F83A76A79FA5373373B45AD9273EAF5FDC8C4F97GFAGC71E196F907BEBB6EFE278C98C0FD0E0920474F24233FECB99432D811EA2403581B84BA97F88BC298313EB2B99DF77748CA17FDF42268DC40FCEA7428AA6ABEC27CF1B707B020C6D4416FDCBB11329067790G895EAC149E2805677E36BE77C1C72F2CD4DEF1C8BEDF25BF6814FC156EFE393676D8D34B25B6A96C1744DE59A7AB038D224F516F9315E0
	38CD8CC8BEC1583603689F59D2246A03F2845DF3D55C77BD4426877ECE831831837139DE024F073F0BF766D8376D44B88F3C331F4551B5FC3F30F4375F72E202B8776F2471B9F7EAF1B6861EB03D2E56C9DFA7A97BD9C0DBG309CE0ADC0D9C50EF5BF409BEADB881F5E814F7ADE25A168778783752150E11FB3FC1C448DFF5756820561CEC90EE058A988343EEB37D49878138C28DC53471623E51231876BBE73545D420AFF7C02E91DC1192C4898B6F5D4503270D5E8E5C2EDDF175076540A583699EB7B0C2037
	9D8DBCEEAEB4301DFE7EF173AC26A7CBE7FBCD85296BFA195475D2783B0069257893AA5E7E2E06276D7407EA1BA16FEDC04BFA975BC6578B68CB2BE536D0326BA643F2187150D45326CA6AC3D565D8DF2AFCC4F7F7A535390B7B72110ADF2843D3DFFE2636D372EEFFAF471ADD15E3256DCB69A27D36F5F70E55831086B083E09E40D200553B310DBDF87A3DF13631B6B12C9C6E17FDCEA9ACD0D92573D5F8CAD1A5ACB687A20A98F0C81DE2D896FBFDD2C4E0BC231D248CFAE3642ED75B3E83EC9C1382D2D8D464
	E0G340BFEC91142914AF3E367CB04A7AC29548E59AF0181FFC8C03DFF5F391134A3B1221C8EB9C5C552B8A87D0B1D24CE2E081D209194405F34DC3EF21614D7511E9C2B8D20EA8F16432BE7083E96A98C79223636B5986C6B8FF510C80434BD241CE7283923917E6E5E03E57C25F38437G5ED7F0BD474F3C2C5353A8BB087BE2F8907B58B1390454A9701759F32C0DG5DF6ECBF3F6A4276533861AE2162314F9B361F226E474D0A1D361B2976317D28C355CFFC4C825AD200FCBB0E83BF72F4997423EE26234C
	EE5C4F9281711D44911BC3FEE3E8434A1E059FC3BF6AB46B5EE9392F6FA6F5D83A976A8F20EDAF167D14B3B65A168F0B9189DB6F49B0B4BC492955847573FDDF8FABDBB373EB9D73F3BDG993BDED47908DEC1F88560B5E61F585A8C30D520636D1BGEF813C83309DE0A7403BG6F71B2B9235A72001F53G66816482943D8F7E83F4812481E4GAC81F8864076C10E3589A084B083A09BE0B5C0C9F50E359A2087E08A40DC0065GC28D56F11EB72D60130F6B6354617FC720F5F1320F5445A7D98F3B7E8AE27D
	EB871A9B2EF61172162EC88125D18AC981A77C91E13449DD24DE58B8668AF2199BD5FC88463D8698E8A29CBF3D0B541B28A8E13937DF115AA5C51183EE0DFE33EAAB525F9B5206CC34B52B0B340560G0CFB1C7F76AEB2E6B802010024A6A9AE4268F728E31664137C60F4C330BF20D07CDDE749BC3FCF9AE4E366574FBEA710B14F57CF46DFBAF71EAB1276968A4BFEC8FDE4889722B5E4BC3BE74BA6DD9915830E2C81DA6159D3036351401BB89EED9B3AD3483E94B2994D2DE65C7E37266C9ABA8EAC2D5146A3
	6A536CF3463EE728E3A743C7282F7E4B244D271A4C1BD5794DAD7F1B14D7E0FCA7CE907BEECFDF0B9C1094599159C571BFFD13600F7A7A2327A4D1ADF5BA36B4DED0677D90C7D1AD6461DA9CDB361DA7F49C57592052907479E81581ED2416146BADF2292EEC2AEBE939123F73D47A431C3EG5ED744513DDE245B77C1FD6C5368540F173A49789E11AE77CBB05DA2931496DE7F1B3ADF9EC36BE4ED0152ED5D24AD78F4F31B797B084DDBFD38469B59C7678D697B685CA1931EDBGCF79E4BF72956D27FCB6F856
	81746C57736D8BA0DF0871CD01E7C69C1FB508FC8B99DFAEBC0583E37916F1BE5B814AD78D4FB69D5F1483046F369037FBC073BF1FBD89FE53C1B50E4B483774G9D0373983FG74CA2E6F4DB072B98FEAFAC68ED23B1A3E27B85F4C03F4ACA77A88FFD60C3E5BA35C6EC10D9E2F2FCFC13E3C03F4CEE02CEFEBBF72D98E51FAA8BBC44B277B10DE5F2706F97D9E22F38B228F6BCD5377418872259F2275057AB2E374950EA25F62C3F40EC274E54469FB00732DBAC46B95752D0D5177692B48D7D6C767BAC49F36
	83CD5F3EEB48E72F237E375699457B2200FC136A681C49B85EEFF33E19F554FF54979B6F8D854817D5C767DE46719EB071F229237E233E58F84DE6DEAEF5F48EB7B65E9975046FFE4E371669A92D27792002BC6BF1FF64696DA4172B7BA3DE710A982D0C8C40F8D4DB7B211AB0695E476BDBC93EA5F9FDD5BD6EF1AC0A12DCD7F6B847EA87E8BD0CF97F518D3847115E17A1F4849FB3A75A4BF19E26F1CDB92C65FD4B6281B34D251F3BC8ECAE845A724318C73F582DDFE755C7A2C1072CAE71D80E7A7002FEDC20
	3813971E0727CB8EC714E6983823184FCEDFA07E12B5A0C333B1EF41013D4284770B845CD798BD2AAF0379AF0E1A56A7A803BC96FBF103D6862DAD7FED266B73DF2AFB88E44CE9857AA400798D843F43CB46EE12E729BD479E34B7ED68F621677337A434A7B4B2BB0D1A3D254DFB193D69DE34578574E90D58C6F6363C1928CCEAEB7543A05BD7D868C3BD8BC107F0A447CA474DBAAF71CBB71E32F1365DCF62D1BCF20446731B7ABA6C8BF74F96662D1BEAB25AA7735CB143D4FD044651F3C40BE75B2AA7CC55G
	54173F7323AFF301E7B57765F7FE5216E05E646CF7283B8F754A89E8D891050D4545C37A3158F6142E5D1B0E227CDB8155E71878D1BB4EG5A69861EF44E37A8C06C4801086C149A02E0A72050CD8BE667FFF69859D98CCF2AF7F790756682EE53B154FBF31048857B15B845D42FCBB2525BF68C758AA1546B845CCC2E572D62C33A86CE753D61B65237146BDB4C752D861C2D895B66DDDB74F5D9A4CEB62C4B29CF39CDDDCD348E531A343A1C755893262C47F8DDDE666D8AF804E63E47FB10BC55392B00F33775
	E1429BE68D33DD11C2541F113368CF5A62ACE153464F9A7A234E3F1AD95BEA567C712DFC421436927DE98D733685BC2B81E8D9F5380C4A4A76A116554B911433834E77A1167D0D91A2677008E13754A00643329406C998190FD1399FF039B9A033026BCBD790DF5A8239388571BF98C27CA4404DEFC1BBFB07F7431316CCA5FDC0C6F385214E1D65E49428948714CAF53DD3D92F120F103F37E3594A43097A8E351BB90CF67341E6C5AB7A53B3425B9C60E63622BF3F98C954C728DC65A84AAD85196263286FC90E
	EF845C48F154673F1A28EFD03939D7D1AE8BE47238BE67B544EFBB81F5F6027761EBE4AF5D95F474C7AA557FDB0391E5ECC19DA584ACA72A2FDAC0FD6920EB7189341347711613B9567DA791FFEF81629540E5F23C4B0478D500ABEBC3FC0599719D001B4A7139AA1E74098500DB5B06F19C330C6D5B347FD8DADB84F50C145A42C1A04320669183EE293CD50D5187FFD7364302B5605E27431C6C75427A45G333D934FCCACE4BCA6FB97760FB09F4C5E284F877B2FDDB46CFFD3DE7418FC9F51FE177E11567F2A
	A197348250BD12ADDBF15FBF177164FF246DA9961D0232BA953BAFC977436F99A465D0F50A6E07EF65786F2A78288ECF4B73D1F52EC35E3301B63F9D77F06F94495CE9DE8764C100BCG4B699C6BB6004653B8E7F962429EA1459EF9F3C0015EA37A5AE3967D38D7FD11549D2C53597BCD9775390E7A711A0334779E30B982108E108930187B3065D2CA9F0EC48DFD20657C34CC4AB96F747877F829DC011748D9BA47AFC75B4876F37A73C04BCB9F1A4C86EDC41D4BB35D0D1DDA9B21E5126DA4E5B2897033G66
	81AC81D85309E56239D4112ACC70BC0295860D69A66F444A62760FDD96D46E873ED467EAA5E7E2F5D23995A9656CE364E8393D6AD24FFE006681C8831889108D3082C06842723B6F5261D465D70F1BDC9DC17EE743580DAA74F706FFE2F1FEB5255C262ED8B91A735EAAA7F19A2CFBBAE0CAF7D416FCCE1A439A06705C372A8B77493D0109799ACDA9679963AB75617D1D68C39A7721BAB8B19F0E26141BE7684317A474E1897761395044FCA8CDA9D7F4564807D36EA2D6FF363368C34E84FD48CDA957F55628FF
	5C68A66DA6843429GF3G3281D6818867A0771F437ED15433CFC815F349E4817A065A9D62BB866B9717A758AFD24AB51EB30AEF3DCC626B86DA94E09AC0A6409200BC001273985F391E147D1F9F4B0E277F1F8ACFAC4E1E14F265670D5A520DAEECCBF56731ADED0ACC4C0737D34AC54F9B49AD1D203DA7D34ACDB334F70BB2B1FB99A965B20C639B1AE0FCA965328D6D5DBBBCB1FB59A965169B5AF30DCC4C1E12D2EE0D21BD7B68446C9DCEA9D754EDA437FA0276FE1BD24EE6E86F3E2B933337A025DC3521BD
	6F35890E63A9655A8C6D9D94A6E62FB5251C47505EEFA6E8CFA8C8A517E6E8EFDECAB9E3FB8F27141BE1E84FE71A183D2114F2F38D6D6DB6CF4CDEDDCA39050676FE96A717BB064F58DEFE1C5C16B16FEB8D6DDDE7A1F22597A82D7CC272354A7E8B388ECE7312B9C39B3C7BGA6814C82D8G108BE03908F3063BFA8E88631B93B5G2EA9986CB318AE30BBD5FB55B309ED203B8E00624E8C92DC873CFBB84EBECCF0D361FD8E471D9ED14F9660BD77A20E7D5F98A4FB687970DEFCA947EA833083B481F483A8G
	D3GE682E481AC8448854887A86A49311675201E6F2BFA48F9119DF09DG8AC0FA8FBD73490267529EFADE135FC34F6FCAC5608568906939A45533AC3A0615B371A3D9BD90DF097DE4BCEA5BC921C78F79AC66A696AB091114CDB753D14C4A43C8B6EB1CF6E69A705158DF9D44582DA9F4907E2D6330757AB863A6C05AAA5913A2E79564DE8D39CFC356BD9B983DD020F7EF0CE4DF99270D0D896CEE946E95935FB5F9C0B43EEB72CD51782E49F74417A89EF468EFE0A27D7BE2723BA80F09096F22ECF5A47673ED
	07311F958EE3BFF7BB127BD96BC864A72D1B47557D51F0FFC0117D122A1A2E7125A8398385D965D4FF00269C22BDB2F6FFC74035CFF61FFA26A3E23EFA5431DB084FD7C71C32928C23128EC98C1FF439788227BDA4B9506F054E44E5740853380C9EF39A17510F1D494BE825B3F9997DDCA1EDB36E1E5089D82189382FDF6837B21EB6A558360B6B36399247710E4BB80EF7DD243FC4027DE107B456D73BEB9327E9993ED9117C449D4AF3C015D748D1C8DC2C0D2ABC25A512BF6EA27D37D7F64AE1F64B064678C8
	CC0C24FE597D7524EB0EE9E2FC2E227A3E92506B5B43756D5B19CC5F1C047A02C13DBEBB57B7DBCA26EFDEC2FD87C2FAFDFB393EB36EE47A96033E5CB1BA29BE6BE554F7C4899E575A48675D095B48CC37F19B196DCE5CC66EF427EEA3773A933711875C295B48C36EC4FD0635154B6B316CDC61209FFA8415FBC5CE9CEB19EC9C6B1F654431EE17D3473ADBCE9CEB0D1CBA564332F1FF38A12C2FCF2DBFCCF3A5EB9F7950B60AFA0D5A4749C8EC196D4673D8C5EFE78F67FFD409655F037C43FAFEBB67FFEAB816
	5F4E70DF9A51736F657CFD23317CFB99FE6BD5BD7F7B1C7F17D7E3795F47FEFECD4F7F81676F97E279BFE0786D85FA7EEA4E7F6B02D87EEA063F4D24672F617C32B9163F0665736F3DCA5AE0880688091EBAB60A0A08779BADBADD8D240F326F90547338B228370ADE1C8B7FD145D543FB9347B59592DC973CFBFAF1AC7C0CB7F11B1F6EB5EE73B7F9933779AFFBD33779BB3C095B7C5D5E54ED7EFEEF727E5D3A15688F05A517948ECB4EF6F9080C05F3FBC999C86A7AA28AFFCF6A45735340A039A36B9387037D
	0AA074B8885F4AC13C8B1B8FFC45003383B4B96819BF1D07FCFD005421ABAC7A413CBAC1586FA432AD43A81B817C7381D6G2C85A8F322CD5F881185C1595F6FD7BD247E550E927FA6815FB44EDBFB1670EAA7983BD81C4F1711BE7DE12B58AB7990F75FBA8D07FD743E839A8E7B612EF7B49C7635730531F6D07EBBC10D977B592DAD9A8E7B52FFDE0C15C7DB8B439AAF7623272BE3F951076D059AAF7621F517B4DC8D437D4F8BC5DA6CD594774106D81DFB59DD1CCBB1F7DC66BBE99D66BA71DEC7D93B91BD1F
	53CB8E99510BA424D75598514BB87DEE13913D0A539FB6742F11530F9A523BB87D1C47086E6374EF3A0C68E91CFE2721FC3A04B9E2C15F86FDFFE3F3CB1AD3AE8D61BABCC342BCF3EAA7621E613887A444B5399097F0A3EE8647DD71905CEAF8DF4BF1F738102F440D38E75D48576346FEF20F7AADAD240EDD0A1BFC83FB83C9AB2C3D1496B25A1E65EE752E1D7ABE0F4BDEC27AEECA6FD75F4BBC30DED6F57DA22269EA9D3441B3A08D5878DD11C6D8218448C7E565762A2A8A5B6E2A2A6D744CFB56007E4C3B7A
	796B8D6F4B93586F2163EE9BFB2EFD6596768D6C7AA17C86F68A5032BD18FFF675441CF592C7EAEB0F05E567E1515D2AE6B3FAA6522E569111FF460377BC2FD3F5D649D09FGBDB25E63083E0DF78EB33FBADBE86AB33E67195BB1EA0A4A544F1932F6C65F95F842C4EDDE9EC61B4B01DE62D56F148F6B5B99A94B6940D089CFCAFFB406CEF2BC7936C5231BC774F4626F1F4CFA7A5E98FA21CE1E463668AD0CED6D2239026F9B1392FE0F5C65E55FB2336734D5B797E43FB1EC26ED61E77DDADB38BBBA1E36406E
	643A925C49D53398AD37AF703350AC304D6A2F9DF1F99CF76C905E5D5D5607B8F78D5E4B6966381F1A90174EF1FF64B696F35C289B79ACBE44CD77A0EEBF477D5605B80563F2B9DFA660AA7CEABF9EEAE4E5394AE73C06416F5188BC83BC45A0778A734B6627F7F588D0FD72CE5427780D7569E10ABFD65F4CB8FDCBA4541711C0DF21CE5F7CB17A1644691B6CC6FDAB9268336A742D99234F92507451F6750ADA2612D3D648D6861D9B5C6A41BED59240F27EE7975A5D96B0363BCEE737BA90EB37D5E7175A2860
	B63A8218939EB92F4FE1A53778CCA8B3561E0F79908DE8B9015D91AEA45F21105CDC89B33B9352007A99CA79F66C1B87A43D0D2A615604F703B2196E7981ADCF32FB16B1B6CE7A1C5C865D87FAE300441687F2C5C19C5B2A94726DDC27680D5F8582A45E1B23D8D59DDBC73D1472ACF3ACD0392D93146B7A58F2746E5BFF29F93EBC2855B345BF2B626DBABC9DEBBE4D471ABA204D8962D9C0EB14642E229074F9007DG9DG8A403490166520D2A524B887688806DAE1316153EDF551782E24BC2B9D8B345DFCEB
	9D66742CFF7A1AB06FEF9A935E111F93226DE581FB162F3C3900BCE9FC7F264E777301D6F299667AG2DGBDG514B985F3794F6370CB407FAC5919D9E750B843E71A8CB839A1AAC88E8ECDFB45C1319FA197AB16BF2227D722EF6A339AC463FB021DC6BA1A339950CFF8DFBDA42B956CDE17AB7BBF72BB1122BE0BCF56C59919E7BFD77FF981E3BF9985F88FBCE0B13B3F90C6466B03EF961C471B5F90D6496B37EDC764C0F337785C3FBC5914AD7461ED51131F2FFB05CB3E9E4FCDD6C690B137B29213DB44617
	4E1E99BAB9BA1E5EB1046373FC20ED956839C211007F569C0A474138284ECD9E99A6FF533E716F4558B74A7E61B621F1D92309FC6FE154B772198F0D0A1637D7C5ECE66A4FADBB511F9145589FEA3BE6D460B19470FBB8D9C9E53A2FEAB27771F9C9DED21974EB640D2DEDBD045FA57AA2925773EBEEFBDBFFB2BD778B1A1E0698BDB4A7B8C9D131FD099DD7487756066384747C8FE1BAC7EFC9F708EEAEFF42C456905D7D18BF82D77E1CC887A04959A83D0D4D776787C8DD70F5979DFEF0FFFE004CF565002C48
	22CF9E92695790C41A3559A10137C34B307677E7070CFE0B61AF856C0E5AA7EA03FD6795CD3CEF559DB55E376A0D9A1F41F822494F4BC2514467E583D17D375B345EC5B3291B90547BD40039G8BG1673F6D0B8C85A813DC9BBF68CB962F413FDB3724BA98A5ADFBD18B856DF8F267E3D15E7864937F23DC1C5897A09FE44BF2B7E6E05928C29C80AB38F11B28A4BEE0FC2D9995E2A66A41F64D251F48DFE2F5ADF73A1AE4B95DC173F6661EBF50E8B36A3DF8F47ADE9C7BE9F47B98EA15FFC0EDBF0887996F25C
	C0B541ED9A00F948G62962938B6F88FF15C27EAD05FA40EDBD5037A26F15C6C9DA81B4BF185E6144D6738DEB34A8AD1445D680E377B3A87E55BB85F90AF17EE0E3B64C53ECC0E4B76A25FBC0EFB5D95EF63AEEE23E890F1AF48A8DB4AF113DC71313D4C6B4D4979A2FCAFA644F181CF3C5D355CEEB6673B406DAE66385C983BB4AFE4464CE14B06F22CD5C3496F94507E74CD8BD963B482EF8F40A40019GF38196822CGD883E0994E31168158811AG3A81C2GE9G69G9943587FCAC7483EC022BC4CB6D054
	CE9896E5528DD9DE3BB5666CEFBE68DBB21C5CFFF61FBAE67E308AE41E99175C03B1734662119C2BEDE4BCF279B1766CA05314C20E164BA99359870F440CC132B9F16E8919D3671EC1B3DD473A250E41905B3F3FEAA6F602AED7C462B96ED366920EEB0E3485A3B2995168587CBDB7FE13C9476D9192A6DBF7D7FAF07D5BED9023FECDCD20D8376EA6785008367E213AEFAD52F47F3C9DF50F9868AECC21FBDA1C6E072C1A6E9F9FC25DB39378EDCE201B581DBBB2F63F60F35524AEBC4130BC948CA8220FEC02E3
	A9625A7E4EEA5CB31ABF027B1ECFD6EF0C112B77496EG9911184CDAAE33FA8477C23B4EE9F17C218663A89DB5AE23E4F1140F0E5DFFD8348367EE76D134B7FCDE33B7E8C6FBBD86768A46589CEBCF999DDBA7DF6E56F4CF67EDA9ADC1AC49F4274769AE3C2869DE66C55D3392783DAE096E2CB85D6517B45DEF4928BBBB015F49F4AF0D535D5223697E8C5F574ACD6037B5096E7CB85D81D1532DF03F052B46FEA753DDFAD553CD471CEF2BF3949B43D35CC295D74DF0B4577C5012B8277D2BA5F5CEDBE9B11ECF
	BDE5091FCF7D4AE2BC1F4A376867D39477BB8B094B8D63CF9767FB16634EF21C39504AF0E7E4276261F8EB61738C5FA4910308FFD1BDD39BD0F1DD8CD72A431DE538578BC9DE0610BD64C78F58B7066AB908CB767958FD8715C78EC01A666571CEA171D18E09BA3FABB86EAC2F0B7BF646574F03BB7575C3F11BDD71FCFFF645736538637916396379569B7015447051F563A58F661E0E2B18FB26778A1C3E298369D3B9BD4B21510B6B103E005317B98581F3F4F6358D1E3A2D7F4AF672BFCE77A315271A0FB5F5
	D05E0DB5636125F6ABEA516EDAEE777712E677499DDB136BEABDF234034570B5B34F3F57D057E6379663C33C0CFC1CBED95668C5FDC81F436999DE0D5EA9A3FD8527AF6A53688F72BD0052B49E0BCF235F4E65DBB9FDF3C090B0B70BEF978BFCDD2CF68F0AAF0BEE507058BD58FC499933CFB389744EC89B4FBCEBF307DEAE93E43247A577D21DDEEEA9482C9E175C2EDA3D5CDA10A939EEBCF277445C9B5F86B25563127BEECC393402CC4F3864B6746965941019B2AE397332DEEEA6484C9D17DCF64CBCF98148
	AC9F175C57E26C65010CF0FDF2B91ABB06BD7179A44D931FF76C9D71FCB59D71FC65F571FC6F564573B55546739D2F0D67FB589C4F37489C4F779303B8D69A44312DAF1EEFFBDFBCDFD80E679B106379169964639586F97BDB867A9E0E51C7FF9F7BAD538DA668FF3F948A9521D87FB388113070F27C31EC7D67FE34AC73D2E329C9B0B58B4FF954DC50AC948E10E491A53FA63B90A69553A217052B6A5C023E0505973926E6F5744B3D3227563C72B633C930009ABA9BE896ACE4C887EDE12A558C3381D527C58F
	2AEE4BDF9E2B88FFEA50FE3D43A6AA36D3B04B89C7C41F2DAD2840AC87F21FA2BC97E8FF2F5F3FDB91CA1B551F6630457C1406A2187A0FAB42ABA7694FEC989237505FB231DD213FE2E2ABA7D758EC7823AC5BA13302F27D0FA959DC41306DD8D38B98F7785F0BC80ECF2A847E9A30827F3D03EB7AB34DE7A6BB925E7D681A4C4E90A6EB7B6FF46FC3D06F6CE5GFE41E45C3FE833105F7ACC34975D6E97FDBED8497555871CE4DF7A7020A2E9FB190505C4FF84F99811520A55F925D7A773FFD0CB8788C5B2D5FD
	369CGG5CDEGGD0CB818294G94G88G88GB4FBB0B6C5B2D5FD369CGG5CDEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG709CGGGG
**end of data**/
}
}
