package cbit.vcell.model.gui;
import javax.swing.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.*;
/**
 * Insert the type's description here.
 * Creation date: (7/20/00 5:01:27 PM)
 * @author: 
 */
public class FluxReaction_Dialog extends JDialog {
	private final static int KINETIC_GENERAL = 0;
	private final static int KINETIC_CURRENT = 1;
	private final static int KINETIC_GHK = 2;
	private final static int KINETIC_NERNST = 3;
	private final static String MU = "\u03BC";
	private final static String MICROMOLAR = MU+"M";
	private final static String SQUARED = "\u00B2";
	private final static String CUBED = "\u00B3";
	private final static String MICRON = MU+"m";
	private final static String SQUAREMICRON = MU+"m"+SQUARED;
	private final static String KINETIC_STRINGS[] = {
		"General mass flux ("+MICROMOLAR+"-"+MICRON+"/s)", 
		"General Current Density (pA/"+SQUAREMICRON+")",
		"Goldman-Hodgkin-Katz permeability ("+MICRON+"/s)", 
		"Nernst conductance (nS/"+SQUAREMICRON+")" 
	};
	private JPanel ivjJDialogContentPane = null;
	private JLabel ivjJLabel1 = null;
	private JLabel ivjJLabel2 = null;
	private JLabel ivjJLabel3 = null;
	private JLabel ivjJLabel4 = null;
	private JLabel ivjJLabel5 = null;
	private Model ivjModel1 = null;
	private ReactionCanvas ivjReactionCanvas1 = null;
	private JButton ivjChangeButton = null;
	private JButton ivjRenameButton = null;
	private JScrollPane ivjJScrollPane1 = null;
	private Species ivjfluxCarrier1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JComboBox ivjJComboBox1 = null;
	private JLabel ivjJLabel8 = null;
	private KineticsTypeTemplatePanel ivjKineticsTypeTemplatePanel = null;
	private FluxReaction ivjFluxReaction1 = null;
	private boolean ivjConnPtoP1Aligning = false;
	private ReactionElectricalPropertiesPanel ivjReactionElectricalPropertiesPanel1 = null;
	private boolean ivjConnPtoP4Aligning = false;
	private Kinetics ivjKinetics = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == FluxReaction_Dialog.this.getRenameButton()) 
				connEtoC1(e);
			if (e.getSource() == FluxReaction_Dialog.this.getChangeButton()) 
				connEtoC2(e);
			if (e.getSource() == FluxReaction_Dialog.this.getJComboBox1()) 
				connEtoC3();
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == FluxReaction_Dialog.this.getfluxCarrier1() && (evt.getPropertyName().equals("commonName"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == FluxReaction_Dialog.this.getJLabel5() && (evt.getPropertyName().equals("text"))) 
				connPtoP4SetSource();
			if (evt.getSource() == FluxReaction_Dialog.this.getFluxReaction1() && (evt.getPropertyName().equals("fluxCarrier"))) 
				connEtoM7(evt);
			if (evt.getSource() == FluxReaction_Dialog.this.getFluxReaction1() && (evt.getPropertyName().equals("name"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == FluxReaction_Dialog.this.getJLabel4() && (evt.getPropertyName().equals("text"))) 
				connPtoP1SetSource();
			if (evt.getSource() == FluxReaction_Dialog.this.getFluxReaction1() && (evt.getPropertyName().equals("kinetics"))) 
				connEtoM10(evt);
		};
	};

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public FluxReaction_Dialog() {
	super();
	initialize();
}


public FluxReaction_Dialog(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
	initialize();
}


public FluxReaction_Dialog(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
	initialize();
}


/**
 * Comment
 */
private void changeFluxCarrier() {
	String speciesNames[] = getModel1().getSpeciesNames();
	if (speciesNames == null || speciesNames.length == 0) {
		cbit.vcell.client.PopupGenerator.showErrorDialog(this, "No defined species present !");
		return;
	}
	String selection = (String)cbit.vcell.client.PopupGenerator.showListDialog(this,speciesNames,"Select new flux carrier species:");
	if (selection!=null){
		try {
			Species species = getModel1().getSpecies(selection);
			//
			// assure that there are the appropriate speciesContexts
			//
			Membrane membrane = (Membrane)getFluxReaction1().getStructure();
			Feature feature = membrane.getOutsideFeature();
			SpeciesContext sc = getModel1().getSpeciesContext(species,feature);
			if (sc==null){
				getModel1().addSpeciesContext(species,feature);
			}
			feature = membrane.getInsideFeature();
			sc = getModel1().getSpeciesContext(species,feature);
			if (sc==null){
				getModel1().addSpeciesContext(species,feature);
			}	

			//
			// set flux carrier
			//
			getFluxReaction1().setFluxCarrier(species,getModel1());
		}catch (Exception e){
			cbit.vcell.client.PopupGenerator.showErrorDialog(this,"Error changing flux carrier:\n"+e.getMessage());
		}
	}
}


/**
 * connEtoC1:  (RenameButton.action.actionPerformed(java.awt.event.ActionEvent) --> FluxReactionDialog.renameFluxReaction()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.renameFluxReaction();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (ChangeButton.action.actionPerformed(java.awt.event.ActionEvent) --> FluxReactionDialog.changeFluxCarrier()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.changeFluxCarrier();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JComboBox1.action. --> FluxReactionDialog.updateKineticChoice(Ljava.lang.String;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
	try {
		// user code begin {1}
		// user code end
		this.updateKineticChoice((String)getJComboBox1().getSelectedItem());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (FluxReactionDialog.initialize() --> FluxReactionDialog.initKineticChoices()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5() {
	try {
		// user code begin {1}
		// user code end
		this.initKineticChoices();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (FluxReaction1.this --> ReactionCanvas1.init(Lcbit.vcell.model.ReactionStep;)V)
 * @param value cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.model.FluxReaction value) {
	try {
		// user code begin {1}
		// user code end
		if ((getFluxReaction1() != null)) {
			getReactionCanvas1().setReactionStep(getFluxReaction1());
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
 * connEtoM10:  (FluxReaction1.kinetics --> Kinetics1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getFluxReaction1() != null)) {
			setKinetics(getFluxReaction1().getKinetics());
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
 * connEtoM2:  (KineticsTemplate1.this --> ReactionElectricalPropertiesPanel1.kinetics)
 * @param value cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.model.Kinetics value) {
	try {
		// user code begin {1}
		// user code end
		getReactionElectricalPropertiesPanel1().setKinetics(getKinetics());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM3:  (Kinetics.this --> KineticsTypeTemplatePanel.kinetics)
 * @param value cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.model.Kinetics value) {
	try {
		// user code begin {1}
		// user code end
		getKineticsTypeTemplatePanel().setKinetics(getKinetics());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM6:  (FluxReaction1.this --> fluxCarrier1.this)
 * @param value cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(cbit.vcell.model.FluxReaction value) {
	try {
		// user code begin {1}
		// user code end
		if ((getFluxReaction1() != null)) {
			setfluxCarrier1(getFluxReaction1().getFluxCarrier());
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
 * connEtoM7:  (FluxReaction1.fluxCarrier --> fluxCarrier1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setfluxCarrier1(getFluxReaction1().getFluxCarrier());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM8:  (FluxReaction1.this --> Kinetics1.this)
 * @param value cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(cbit.vcell.model.FluxReaction value) {
	try {
		// user code begin {1}
		// user code end
		if ((getFluxReaction1() != null)) {
			setKinetics(getFluxReaction1().getKinetics());
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
 * connEtoM9:  (KineticsTemplate1.this --> JComboBox1.setSelectedItem(Ljava.lang.Object;)V)
 * @param value cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(cbit.vcell.model.Kinetics value) {
	try {
		// user code begin {1}
		// user code end
		getJComboBox1().setSelectedItem(this.getKineticTypeName(getKinetics()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetSource:  (FluxReaction1.name <--> JLabel4.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getFluxReaction1() != null)) {
				getFluxReaction1().setName(getJLabel4().getText());
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
 * connPtoP1SetTarget:  (FluxReaction1.name <--> JLabel4.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getFluxReaction1() != null)) {
				getJLabel4().setText(getFluxReaction1().getName());
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
 * connPtoP4SetSource:  (fluxCarrier1.commonName <--> JLabel5.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getfluxCarrier1() != null)) {
				getfluxCarrier1().setCommonName(getJLabel5().getText());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetTarget:  (fluxCarrier1.name <--> JLabel5.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getfluxCarrier1() != null)) {
				getJLabel5().setText(getfluxCarrier1().getCommonName());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getChangeButton() {
	if (ivjChangeButton == null) {
		try {
			ivjChangeButton = new javax.swing.JButton();
			ivjChangeButton.setName("ChangeButton");
			ivjChangeButton.setText("Change");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChangeButton;
}


/**
 * Return the fluxCarrier1 property value.
 * @return cbit.vcell.model.Species
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.Species getfluxCarrier1() {
	// user code begin {1}
	// user code end
	return ivjfluxCarrier1;
}


/**
 * Return the FluxReaction1 property value.
 * @return cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.FluxReaction getFluxReaction1() {
	// user code begin {1}
	// user code end
	return ivjFluxReaction1;
}


/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBox1() {
	if (ivjJComboBox1 == null) {
		try {
			ivjJComboBox1 = new javax.swing.JComboBox();
			ivjJComboBox1.setName("JComboBox1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBox1;
}


/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabel1.insets = new java.awt.Insets(4, 10, 4, 0);
			getJDialogContentPane().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 1;
			constraintsJLabel2.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabel2.insets = new java.awt.Insets(4, 10, 4, 0);
			getJDialogContentPane().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 2;
			constraintsJLabel3.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabel3.insets = new java.awt.Insets(4, 10, 4, 0);
			getJDialogContentPane().add(getJLabel3(), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
			constraintsJLabel4.gridx = 1; constraintsJLabel4.gridy = 1;
			constraintsJLabel4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabel4.weightx = 1.0;
			constraintsJLabel4.insets = new java.awt.Insets(4, 5, 4, 5);
			getJDialogContentPane().add(getJLabel4(), constraintsJLabel4);

			java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
			constraintsJLabel5.gridx = 1; constraintsJLabel5.gridy = 2;
			constraintsJLabel5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabel5.insets = new java.awt.Insets(0, 5, 0, 5);
			getJDialogContentPane().add(getJLabel5(), constraintsJLabel5);

			java.awt.GridBagConstraints constraintsRenameButton = new java.awt.GridBagConstraints();
			constraintsRenameButton.gridx = 2; constraintsRenameButton.gridy = 1;
			constraintsRenameButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRenameButton.insets = new java.awt.Insets(5, 5, 5, 10);
			getJDialogContentPane().add(getRenameButton(), constraintsRenameButton);

			java.awt.GridBagConstraints constraintsChangeButton = new java.awt.GridBagConstraints();
			constraintsChangeButton.gridx = 2; constraintsChangeButton.gridy = 2;
			constraintsChangeButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsChangeButton.insets = new java.awt.Insets(5, 5, 5, 10);
			getJDialogContentPane().add(getChangeButton(), constraintsChangeButton);

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 0;
			constraintsJScrollPane1.gridwidth = 2;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 0.8;
			constraintsJScrollPane1.insets = new java.awt.Insets(10, 5, 10, 10);
			getJDialogContentPane().add(getJScrollPane1(), constraintsJScrollPane1);

			java.awt.GridBagConstraints constraintsJLabel8 = new java.awt.GridBagConstraints();
			constraintsJLabel8.gridx = 0; constraintsJLabel8.gridy = 4;
			constraintsJLabel8.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabel8.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJLabel8(), constraintsJLabel8);

			java.awt.GridBagConstraints constraintsJComboBox1 = new java.awt.GridBagConstraints();
			constraintsJComboBox1.gridx = 1; constraintsJComboBox1.gridy = 4;
			constraintsJComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBox1.insets = new java.awt.Insets(0, 5, 0, 5);
			getJDialogContentPane().add(getJComboBox1(), constraintsJComboBox1);

			java.awt.GridBagConstraints constraintsKineticsTypeTemplatePanel = new java.awt.GridBagConstraints();
			constraintsKineticsTypeTemplatePanel.gridx = 0; constraintsKineticsTypeTemplatePanel.gridy = 5;
			constraintsKineticsTypeTemplatePanel.gridwidth = 3;
			constraintsKineticsTypeTemplatePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsKineticsTypeTemplatePanel.weightx = 1.0;
			constraintsKineticsTypeTemplatePanel.weighty = 1.0;
			constraintsKineticsTypeTemplatePanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getKineticsTypeTemplatePanel(), constraintsKineticsTypeTemplatePanel);

			java.awt.GridBagConstraints constraintsReactionElectricalPropertiesPanel1 = new java.awt.GridBagConstraints();
			constraintsReactionElectricalPropertiesPanel1.gridx = 0; constraintsReactionElectricalPropertiesPanel1.gridy = 3;
			constraintsReactionElectricalPropertiesPanel1.gridwidth = 3;
			constraintsReactionElectricalPropertiesPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsReactionElectricalPropertiesPanel1.weightx = 1.0;
			constraintsReactionElectricalPropertiesPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getReactionElectricalPropertiesPanel1(), constraintsReactionElectricalPropertiesPanel1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
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
			ivjJLabel1.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabel1.setText("Stoichiometry");
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
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabel2.setText("Name:");
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
			ivjJLabel3.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabel3.setText("Flux carrier:");
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
			ivjJLabel4.setText(" ");
			ivjJLabel4.setForeground(java.awt.Color.black);
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
			ivjJLabel5.setText(" ");
			ivjJLabel5.setForeground(java.awt.Color.black);
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
 * Return the JLabel8 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel8() {
	if (ivjJLabel8 == null) {
		try {
			ivjJLabel8 = new javax.swing.JLabel();
			ivjJLabel8.setName("JLabel8");
			ivjJLabel8.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			ivjJLabel8.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabel8.setText("Kinetic type:");
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
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			org.vcell.util.gui.BevelBorderBean ivjLocalBorder;
			ivjLocalBorder = new org.vcell.util.gui.BevelBorderBean();
			ivjLocalBorder.setColor(new java.awt.Color(160,160,255));
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setBorder(ivjLocalBorder);

			java.awt.GridBagConstraints constraintsReactionCanvas1 = new java.awt.GridBagConstraints();
			constraintsReactionCanvas1.gridx = 1; constraintsReactionCanvas1.gridy = 1;
			constraintsReactionCanvas1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsReactionCanvas1.weightx = 1.0;
			constraintsReactionCanvas1.weighty = 1.0;
			constraintsReactionCanvas1.insets = new java.awt.Insets(2, 2, 2, 2);
			getJScrollPane1().setViewportView(getReactionCanvas1());
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
 * Return the KineticsTemplate1 property value.
 * @return cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.Kinetics getKinetics() {
	// user code begin {1}
	// user code end
	return ivjKinetics;
}

/**
 * Return the KineticsTypeTemplatePanel property value.
 * @return cbit.vcell.model.gui.KineticsTypeTemplatePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private KineticsTypeTemplatePanel getKineticsTypeTemplatePanel() {
	if (ivjKineticsTypeTemplatePanel == null) {
		try {
			ivjKineticsTypeTemplatePanel = new cbit.vcell.model.gui.KineticsTypeTemplatePanel();
			ivjKineticsTypeTemplatePanel.setName("KineticsTypeTemplatePanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKineticsTypeTemplatePanel;
}


/**
 * Comment
 */
private int getKineticTypeFromName(String kineticName) {
	if (kineticName.equals(KINETIC_STRINGS[KINETIC_GENERAL])){
		return KINETIC_GENERAL;
	}else if (kineticName.equals(KINETIC_STRINGS[KINETIC_GHK])){
		return KINETIC_GHK;
	}else if (kineticName.equals(KINETIC_STRINGS[KINETIC_NERNST])){
		return KINETIC_NERNST;
	}else if (kineticName.equals(KINETIC_STRINGS[KINETIC_CURRENT])){
		return KINETIC_CURRENT;
	}else{
		throw new IllegalArgumentException("unknown kinetic type "+kineticName);
	}
}


/**
 * Comment
 */
private String getKineticTypeName(Kinetics kinetics) {
	if (kinetics instanceof GeneralKinetics){
		return KINETIC_STRINGS[KINETIC_GENERAL];
	}else if (kinetics instanceof GHKKinetics){
		return KINETIC_STRINGS[KINETIC_GHK];
	}else if (kinetics instanceof NernstKinetics){
		return KINETIC_STRINGS[KINETIC_NERNST];
	}else if (kinetics instanceof GeneralCurrentKinetics){
		return KINETIC_STRINGS[KINETIC_CURRENT];
	}else{
		return null;
	}
}


/**
 * Return the Model1 property value.
 * @return cbit.vcell.model.Model
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.Model getModel1() {
	// user code begin {1}
	// user code end
	return ivjModel1;
}


/**
 * Return the ReactionCanvas1 property value.
 * @return cbit.vcell.model.ReactionCanvas
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReactionCanvas getReactionCanvas1() {
	if (ivjReactionCanvas1 == null) {
		try {
			ivjReactionCanvas1 = new cbit.vcell.model.gui.ReactionCanvas();
			ivjReactionCanvas1.setName("ReactionCanvas1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionCanvas1;
}


/**
 * Return the ReactionElectricalPropertiesPanel1 property value.
 * @return cbit.vcell.model.gui.ReactionElectricalPropertiesPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReactionElectricalPropertiesPanel getReactionElectricalPropertiesPanel1() {
	if (ivjReactionElectricalPropertiesPanel1 == null) {
		try {
			ivjReactionElectricalPropertiesPanel1 = new cbit.vcell.model.gui.ReactionElectricalPropertiesPanel();
			ivjReactionElectricalPropertiesPanel1.setName("ReactionElectricalPropertiesPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionElectricalPropertiesPanel1;
}


/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRenameButton() {
	if (ivjRenameButton == null) {
		try {
			ivjRenameButton = new javax.swing.JButton();
			ivjRenameButton.setName("RenameButton");
			ivjRenameButton.setText("Rename");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRenameButton;
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
 * Insert the method's description here.
 * Creation date: (7/20/00 5:43:16 PM)
 * @param fluxReaction cbit.vcell.model.FluxReaction
 * @param model cbit.vcell.model.Model
 */
public void init(FluxReaction fluxReaction, Model model) {
	setFluxReaction1(fluxReaction);
	setModel1(model);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getRenameButton().addActionListener(ivjEventHandler);
	getChangeButton().addActionListener(ivjEventHandler);
	getJComboBox1().addActionListener(ivjEventHandler);
	getJLabel5().addPropertyChangeListener(ivjEventHandler);
	getJLabel4().addPropertyChangeListener(ivjEventHandler);
	connPtoP4SetTarget();
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
		setName("FluxReactionDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(531, 691);
		setModal(true);
		setResizable(true);
		setContentPane(getJDialogContentPane());
		initConnections();
		connEtoC5();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}


/**
 * Comment
 */
private void initKineticChoices() {
	javax.swing.DefaultComboBoxModel model = new DefaultComboBoxModel();

	for (int i=0;i<KINETIC_STRINGS.length;i++){
		model.addElement(KINETIC_STRINGS[i]);
	}

	getJComboBox1().setModel(model);
	
	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		FluxReaction_Dialog aFluxReaction_Dialog;
		aFluxReaction_Dialog = new FluxReaction_Dialog();
		aFluxReaction_Dialog.setModal(true);
		aFluxReaction_Dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aFluxReaction_Dialog.show();
		java.awt.Insets insets = aFluxReaction_Dialog.getInsets();
		aFluxReaction_Dialog.setSize(aFluxReaction_Dialog.getWidth() + insets.left + insets.right, aFluxReaction_Dialog.getHeight() + insets.top + insets.bottom);
		aFluxReaction_Dialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private void renameFluxReaction() {
	try {
		String newName = null;
		try{
			newName = cbit.vcell.client.PopupGenerator.showInputDialog(this,"reaction name:",getFluxReaction1().getName());
		}catch(org.vcell.util.UserCancelException e){
			return;
		}
		if (newName != null) {
			getFluxReaction1().setName(newName);
		}
	}catch (java.beans.PropertyVetoException e){
		cbit.vcell.client.PopupGenerator.showErrorDialog(this,"Error changing name:\n"+e.getMessage());
	}
}


/**
 * Set the fluxCarrier1 to a new value.
 * @param newValue cbit.vcell.model.Species
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setfluxCarrier1(cbit.vcell.model.Species newValue) {
	if (ivjfluxCarrier1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjfluxCarrier1 != null) {
				ivjfluxCarrier1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjfluxCarrier1 = newValue;

			/* Listen for events from the new object */
			if (ivjfluxCarrier1 != null) {
				ivjfluxCarrier1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP4SetTarget();
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
 * Set the FluxReaction1 to a new value.
 * @param newValue cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setFluxReaction1(cbit.vcell.model.FluxReaction newValue) {
	if (ivjFluxReaction1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjFluxReaction1 != null) {
				ivjFluxReaction1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjFluxReaction1 = newValue;

			/* Listen for events from the new object */
			if (ivjFluxReaction1 != null) {
				ivjFluxReaction1.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoM1(ivjFluxReaction1);
			connEtoM6(ivjFluxReaction1);
			connPtoP1SetTarget();
			connEtoM8(ivjFluxReaction1);
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
 * Set the kinetics1 to a new value.
 * @param newValue cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setKinetics(cbit.vcell.model.Kinetics newValue) {
	if (ivjKinetics != newValue) {
		try {
			ivjKinetics = newValue;
			connEtoM9(ivjKinetics);
			connEtoM2(ivjKinetics);
			connEtoM3(ivjKinetics);
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
 * Set the Model1 to a new value.
 * @param newValue cbit.vcell.model.Model
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setModel1(cbit.vcell.model.Model newValue) {
	if (ivjModel1 != newValue) {
		try {
			ivjModel1 = newValue;
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
private void updateKineticChoice(String newKineticChoice) {
	int newKineticType = -1;
	for (int i=0;i<KINETIC_STRINGS.length;i++){
		if (KINETIC_STRINGS[i].equals(newKineticChoice)){
			newKineticType = i;
		}
	}
	if (newKineticType==-1){
		return;
	}
	//
	// if same as current kinetics, don't create new one
	//
	if (getKinetics()!=null && getKinetics().getName().equals(newKineticChoice)){
		return;
	}
	if (! getJComboBox1().getSelectedItem().equals(KINETIC_STRINGS[newKineticType])) {
		getJComboBox1().setSelectedItem(KINETIC_STRINGS[newKineticType]);
	}
	if (getFluxReaction1() != null) {
		try {
			
			switch (newKineticType){
				case KINETIC_GHK:{
					if (getKinetics() instanceof GHKKinetics){
						return;
					}
					setKinetics(new GHKKinetics(getFluxReaction1()));
					break;
				}
					
				case KINETIC_GENERAL:{
					if (getKinetics() instanceof GeneralKinetics){
						return;
					}
					setKinetics(new GeneralKinetics(getFluxReaction1()));
					break;
				}
				case KINETIC_NERNST:{
					if (getKinetics() instanceof NernstKinetics){
						return;
					}
					setKinetics(new NernstKinetics(getFluxReaction1()));
					break;
				}
				case KINETIC_CURRENT:{
					if (getKinetics() instanceof GeneralCurrentKinetics){
						return;
					}
					setKinetics(new GeneralCurrentKinetics(getFluxReaction1()));
					break;
				}
				default: {
					return;
				}
			}
		} catch (Exception exc) {
			handleException(exc);
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
	D0CB838494G88G88G6D0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8DD8D5D556B095A995CD66E8E1B1C5EA8E9AB5E40C11D911DAEAB2C673BA0DEFE38D5F77520C3DE35F6BB7E36F6B4C505361C7C541E452A69BCD19A4C43D02A8882215A0AA94AAA3C828145E1F83DC39F06F5CFB100B22FDEB7F2CFD4E3D779CAE723CFE4F737234BCF72F3D565AEB2F3D765A7B6C1F1334FECB5472347475121436DC62FF65E912546F1024DC5B11BFA3EEC4F7F33624797BBB40DF
	A42BB5931ED110D77ADAF3F62E74CCCD962463A02D7C3CB97BA3783DCE1ADE93E7059F866504C35E376F0CCC98381C6A2DD84ECA1A6EA94D01677BG7100A34FCCA7A97F4E528D9C3F1063915252A5A91952C417EE6238DAC8DFG088408F5B27A5970DCC5CB3E35320253355E9DADADFD491D3E06485148C4A8F4B15EFA7E4C137E143654C54AFA056AC9F8C2A1BD8D00D4BEC3FA53D4844F038D1D6DDFD8ADF6256812D5F6B80A3A1CB659D1545EE32F2CBC6A68711E124DD6456E6CBEDFE3B7BB1C6D12647AA7
	1179F1D99615A1C99510CE253897ED241CB8781D8810A270DD6D84FF485D1C5D8260F2E35DBE5FF6106B5EF265C9A9257D05D5EB8D6A9666E6F50AF52BF54B5BE149781836434D79445E0BA1AF57531C1D8FD08B508190863011367FEDDBF740334A229CF6F6F5B93B2BDC2EEA074DFD1C5495FED7D6C28E471D122DCE37CD12D89B7F325424234FC040785E3CEEBEE613683D585EB1279E1396BEF1CF7ACA9D1BAC0C3E129E65E136087728B6E1E5EFAEB3AAFB0147579FFD4B7EC65216AD3D35E765F21D32B73D
	3AE765E2AEC7D254328FA9242DABE55256F9701B7011CE447035949F2A41B35FCC24BEC35263A1AF2B87FD2365FD5425BEF32C14DBF22F2E9DA2260727BBFA188E21BD7E3A0C69A032E3FBD819BB05AECFD2FC6EA5954FF471D0BFA5E99764A576B6E7B37F3A23007455423E666C9A00D6G85E094C0AC404CBE74311FEFAD9E240FB5185DCAF50F5DE1135D92633DA37F2F70143D0A5BDC576DD14C5DD639596C361BAD8E59A3F11AD1794486969F3EB154772DD046B139DBF61BC9D7856966AED9115D9EC6B3B6
	7FAF0446AD535CA6FB178C85F439A414BBA9FF9DBC9DE60FF25AE5B3AB32CA4172A76713B639E4EE8609A0GFEB33B44EFA376CA017A278264DCC6BB6C7A0C487BDAF6F74B0E4A4AFA2733334755C4EAA235DEA6F6AE2331A38FFED7DCC69B479592DC942463059C5BE79FE8645458E99CB23B7B300F9D12A110CB09C03F98C03A521C3D8140F485FD687F34208F552CF9D4B27DE2342E8F0DGFF2E3D42FC6742957F3EF40216C1748C033CE80059G0B2E609870F3DB8BBCC979040F14AFF1990917757BDAB0B0
	DDD179D8993F23E5AC577455157CF74690643F4F7B9B7EE6767FF487E94B2CFE081FGB57D58868FFE1647FC3A5A6C11510BCF3A4181E51B5AA2CC4F87BB1A6079A1G99F32BGBA816CG67G3682FC86F0AF4037G65929BD77F81F08600741D7DG87G76821481FC89F0DC586199AA3F95F48B850881188F10CAF43E5A1CDD8F20GC4824C84D88C307EDAF376C1G9BC0B8C09C40C2004C6F41CFG1AG7A3F47366CEBEFC89B3C8D311E7E783FEB6A6A1FD74C1FD83FF2CD1D5F67752DB2A86FB8FF7E438F
	FF065B77DB83BEBBEF072178C9B038F790B44C36C957E5DB62574BF57044D757D2F901F9DB781358FD9BEF9BEC8B025FAE297D469FF67267C6BF7CA7GEBF8DB9B7523D5B748366B86C1437A686882E29379125C2D5448AE395B86BFBCBCAF320068474715B6276099CB71AE98078EC3607788FCF481312DD9D15CF6CB0FA2B74A0AE26FEED773E3E9D91E9E0BCB9D42302CA78BC81BB9FBE19C9274538AC88C37BA3B3BE59AAC848B4FFF160EA132C36E82258FBBFB3A95061F390D4C35BB65BEBE064D5E36C2A2
	E31023070C07EC7E9AEBA676F7396DDD90024810AAAD7B1E0CAF75E59BB4B69A8FB8B28FCF04E77277B8B674EE4531A17F72C4A9613CABDDEF2E937170DD6907C2D8BCEE8AD147862653D3057A3ABFC747B20E77B0DD1FBEC46C201DD063FCBA03723C76A9299F1DAB14A02EAA0D48EF831E43E6375BAE3BCB98FECEBB117F353DDBD66CD68F43B93EDB63A73FC44258FEEEA71123007EE1GD1A1985B8FECA779B83E7220FE5869F03026023CE5A1441E236998C86A9E8F692490E64F77C5FEF69B669F3A896678
	B779675FA472C3A1AFB2A07F3FC47EEC48CB5664B3BD8BF764921F137F59A3433408CCA43E96FED13C63833F3CE66EA3AC3FFC87719107E68E92F3B3A9B35D0E6FDF3910AE84283D19159BF6B3AB1B513DA868161174ADAA5D6EDB34F417AFA2DD8360BBG42EEE1F4111CAEB714506D6DC43A24DB5898360853AF83A89272B29CC8579A4A64A87C999E4A655DCA5FF585DDECA81F1F40B399C0027C4D00CF20F4F7F689F93732790B824FD00068DB357A7D1B8769F20631B1B5FF18CADFA4645D2BA0DD05A6BF1A
	BFD5F95F76883B8CE3E3333E3C3FDCC23A8E1E9FB24CCF9E25FBDD50C5F03AD8F8A68C732F6F0FFB11AE1953AD9B4666EEB94319BCC6F7490BF41B06337C52612CBDEAFC680A7A10CE61F461700C84C87021FBD7504D9F4E4A4D996EDBAE334B0C4BC837F8B81B1764EB74A4F6698FA3F43DD710AEB30C4DE3B605B1BFB005E9659577A35D6CB0B63FC98ED36BD3A4687EA8681604313911FF392C9E0FDEC53A2C5B181CBCF86E86E83A0D154B625A29FF1298C25790AE1AAF193DC51EDE98AFAAAB3F427046578B
	2650B9BF093D9D37613A40718A921722A09D8F10F49B4666D756603A40284EE8E961DBA5AB0D56BC96F1DDF2EED7E3F35CFFD42DE0F16FE193A9339E72DAEF4718B7F907765D244A63F1DA6D743508478B73CEED6CE638361D2BC99C363BBDCA9D8C2EDE0CAD0E1DC4DF725E44517CBDF21A0C6BADAEA8B77AF6DCEFF9DB5EA4992C37D4D652504F6AEF5D0B759FF179E16942687B52338D6A1F4C6BAD5D21565FF4ED59F226471E369C092F51C03E6B8E54431D3F59D80FC617EC35137849E4C437E1DD2201BFD9
	4818FAF10B318C9C1A380C4D6D28470670666C06F01431736B2D46B234C3991733531E437DAE8CE4A4G345CC9709673FA0DCD1681BE8F70B5GECEC9CE95F83CFA62A08BD9AE4F71B535DA55B8A09579619FB15A2BAFFAA2A2259C7486F6D6863193B487C4FADF743DB35D62D426D4D922E1D0CDDF708171F7858D86960775F256B2DE5052D6DC80F3A132F715D2936D96DC8CBC6ABG2B67FF08FA2E7F8144609FE0BDFB4F6B55B37487E84F39BDA45F5AE16EEE27FA72D9832849640E3B08F2A300A7C948FD67
	B32D5C2C3B185C5DF7217DD6F72ED46D47ED32711BC2F8765075825E5C07BB1CF62BDCD8CFAD6AGA50A9AE196595DFEG4BEF6DC4BF2881597DC27E0CCEB257B33B5B797C66F7144EA313F908EC2BD364AEE6637EB3E8631044B8E9433ABBF5EDCCA06EAEE65B05F729B66E0F32E40C08C29B1B9CE88B5388082BA3D0179FD23C3D5B2EC07C32DF168B37337ABEDB76A1477B545643FC00497C0D836B978972668B19473BC87C3052C5219360C312446C3E452A35FB4E5D5C6EF7A3DFEF97310B8213F7D67F5FD7
	E9FDACFEE538EE7DBBFE9C11DEF3B72B3763EE357E258FD9B2EA9F427A9F74202E5300E629A8F31C071659E177303974DFED5A3934E9A4588AA08CA0F2A4724C25325C3CD7B4AA320B697B68E62D3ED966DBF5758DFF2BBDBDEEA4EF2F112A3E31509FE60ABE1123203E79BF04F87EC3747975DF10A7FDAF906D10A0E8C781DDA2G438F6BC1FC66A8987BC6A13E72927A02823818D1D82F07AE11382B19FFF379ABFAD1CE8A50EE9A0D74DD3D44F6A4CA30767D5824573ECD23315C24DEAC3783F0D1C2CE39170C
	C11DBE737B8BDEEC97467B2097F5D8GFC3977A06F0ABE24E376B9F9AC1F76A525D1531D34FD742465A260C59F7D268FF52A8519A1C26E33FD3EFE51E6557AC57CBD2CEE523D4D596B6FC51E5F74E96B416497DCC63DCDC0E73B97ED31D860A381B7DF60232E20BEB9919053A3D0764AFE0C992C1EE30E11F513F6993BE7D31FCBA6FD2D30BEE0EB83C727835B83C31504717C4F1F206F66570C17AA16F69A461A7E88663363A3D45F0DD973476589G2C8EAD7DD83725C0E39A03F5F8CE540151FDFA9569ECC093
	B986ED706AD53441FC40655C077C1FDF73ED135B2FEA5B2466BEBEBE40B3C270485734ED4256E61E267B853177692FD74E6478857C39981EEC9D3DF7BF51CB3A1F2D2393798CFF934567EA702C9E0D95D83FBC488B3D9F57FEE797933D97C1BAB3927A93C0A9C093009209733625BB491A5900EB6BF55D8ACC6B4C0EC61F4589DC63B611F2BB65BE1EFE4C240D9B4C8E5365216D75A4DDB79F2B772E92929FC6C09DE3GE682AC84C895F55E3B4B942C4E38C740AB4B7429E8A3FC1BFEB47875E746F7147215DEB7
	5FE3EDC3335B34A17265378F2DFEFB5A07D63F923B3F1EB6BF99A1FEE94677E5GDF11DFFA3C9F9F0BE5EB9B8D5E292AFB94052D4B507E9E3FDF5B5F277D086D27A443F3A9GFB6F9AE34E62F2F39E40772B2BC7356FD75356DEB5FC3F28F90069E7FBC00DED79DDCDAB197F3E3C073C4F0C073C840085GCB81B6BC887D972065C1746310DD1F8773E3BA61EEF29E86DC2D5359F958FF3DD058741DA136C54645E0ED61FD50170F596F3173E83F918F62BE4144GD90353614920BA4CFED04F9F5A9D9B757D816C
	E9F1D6BB3DDCD7D3816E49A78BDDAD1D417A183E2E2E20FC9B22FC7918CF1C24679AF6C3DEBD008DA084E0BCC082C0CA947A443F6C2A8E6693E29B3349D9056B51BA0E417B36E3E8EDF2A8004F3FCDD22374789687708D4E2EFF8A4A377EA1BD9F78C35B86FD9F28B7DBE487EB7FE6EB96AF4774900E2DB34BC8FB58A09D829081908730GE0A9400631589E5B0A023607E8044134C735E7E876E9884A37FB2C1EFDECE736CB831EE541177643666ECBE6321F41E268C90BB606560EE5F3A6851EE1E3B1061E30E1
	3F0A9A0BEBD72D96B217D9G69A5GD963505E7B4B093D73A0FD9020894085B08AA096E07EB834F7DF5101E07666B1112DC8849A1A596B83E5E8F64E8B4A37F41C9E5F0BC3ACEFFED03E1C71FAFC61BDC3AB6F3E20FC793A659D3AB434B8F2A200EF33DF7A20EEF9B93DC32B5F5F0272B50D579B4F9ACC98A39C63F10CF8B5C056606A1C9240D764179E2FDB67073DC32B7393C17926691677ED5F506A579940675F2673476B4522A93DE4FEB508D8F4043C123B6DD63323416DF449EE45AEFB9A4C5D984F9F30E2
	5CD9BC9E634EBAFADEAA7FE108AFGCD8FE39B1AAE8F4D2615C1799C8F6B717DFE0865C9D70271056A16B7A100EFF0ED989740676F2311FE6531B87ED4850963F11017843088A0F3C2F3F6A1C08DC047840C63F30B483A43E067B14167375F758F4D2E0E20FCE19374788A2F8E2D3CFDC17922F44BDB9840B738F67C77GBE7F3E98BFC12FAF4E5ADEAA8D5897F1B508AC98B549DDAE07D911B5FDF07AD56C03C993308FBEC1477E2C9F43DC812082001DEB7BB95D63EA0034A2F05B7791DC98242384AE3A1C60E6
	C2FA01407DDF0ADB8C694CE86C53D77713751D8DA2BD1C26497C04254FD43484E9036B85D2162F5D5D7F6DE5E84F2C31BE74CA3171791585457A67D75EA856BF3F12D27C970687995AD316183F38F86073ADCB0B0D4F371415986BF930C4DF4F6A92FDBDEBCB8656332144C84FCF288DDFCD27EB6EBD5D0A3DCB2622193F4BDEF26689224E291EEE961208F40F7FFA1110B3672FEB299E9E0F6C21934E9A33E266F93FD5481EC740599D3605414F28F458D7F11AA30A73F00968F7FF58EE703E2269DBCB76633BF5
	C5B4762DE78E10FA39A09D8690A5FC7F8133DE3C09677E1A986D5B7755B8FE28122C31A59379G6BA7B6E71BG6AA7E27C7EEDC950F545A35E8156957FF50F31CFA46F5177095F6CA17B599EE70F5BAA7B377B5BFBB608BCF514A00B76446C0C66BFA93FE2F6374B81FE73AE65E7F9C67CAB769A7996EBD353C5ED1B6E9131600973FAED601848EC9FB2B1308D183CE49FF9FB053C1202014610D1067246F4E6F3F98DEDEEE7D7C3B92F7B19D2633638D02A5F96ED2546ED51D59A3CADAE159A3745555260EDB12C
	145445E237595D7C54952B235D2135D919287B56AE44B7A84E06D2E14BF3D6BDDBC6F39BCE03E7121FBDD979734A0CED760BB2FD1B7D2A4C58E67F3BAC384DFED7E6EC335F178537591F4B74ED566751586C389A0B5A0FFC229F0B9A2DEE2743C1A212046FC743AD9813D2A6E2CCB2DBC9CC5AB429B93B9420FE924624C65BC07EEB1B94683FECFFA4094E9F3C13587EC81A407FAB4507EA70EC5F44E241B398A3A0EF69A40CDD074B0D5B6E2BF27D36EBA8B7EE3BEF4A03375D05F26336EBAF8F5EF62E7201E3CF
	1D625B37CBB87EDDC55B367B847D0C9EDF7AB20E6F3D2425AF977407FB357D27C4741F53A63D36ECF9046F2343B364915F76E47256F8D7EBFDEEB76297FA35656F9774237A34F8B5360E536D3FA296BEE264CB43697BE074A37E3EB4025D45FA445F17BE3621AFA5C0DEFE8C6E8BDAAFEB752A907AEE3F62EB5FFD9C7F3AAF9EC689563EB3AA0CFD72258AFD1FCC2AB07649DFD6847749648AE31F7CEDC5F01FDCD2B130CF0E6E773541FE0EBFCF77E5B53E4A4F2F2C3B263525BA5E4D9A701DA9AC46280D375178
	9399635F463BA9BEBA463F0D1F9271A28E72B6BC0AED7C96BD43E2135B4CBD8E180EBABD72C9973FB2C4E89FA53C197BB07E75BF0AE7AC9E6F2567A060AD964CC24E07C9610F915AC37BF06DF4995066G4E84D08F60F8CC6CF1179399703EE5F608B2DEAD47F72A7902AE0B7A06DB7658AF7379A9234D2BC05A94C1FB7B17644CAFBFE1880D2A50B9B48962FC0FE83F3F1D17BE265AB66BA7C6F63EFA1046791FB0FBFEAC70A3A93EC503E7F61EC1672AA4BD8A72AE44623B5CCF691E1B6971666C030FA36E2F96
	FC8F2CFD9C63F7ED25F1DFB9D9295FD7CED76A3FCF1C2B9C78FD42DAE96463CC3F174E234E91C2671D97D0677182D7DB003825826736A2EF4DE4447D0B95699A846EBF293CA8C847CF46B1B45C22ED2B44493EED13BC59282DFEC1FDFF69E47F33873F26784CA77C4F9E34DB716C41FA4873BE01BADD34211E8B1F906B2196C97459DFEBFEF7D87E3B5F0535404A38690616416A6CB0E1FB1962508EBFB3219DAA84CE4A44768A93382922DDA36350DEF75AD49B5D23793D45F6636DD5F1C34BE0F5FE63AA56F931
	2873AE2BDA4FFD1A5F4F7DFF701185B7348C96E3B76EA37DD2B3DDAE7A465EEDF376C238A537D395CFD1CDDDE3434946A367CF1EB8FF389EFE303385D13D5A7B6A952BEF563DD7C36060CFD95FEE604F7077462431B9F89B9D0388CF9864A57C1460FE3FEF3D1F0E6D4B59DAAE4B4FAF576613D8C77683557CE61FFC22AB39E02466077964AF577033FA4D7D8C4FCC244E0D159CBF8DB53C071FB5055547441FF973462465C6B42CE0F6FD3B1C1E59F277487C2CDF8526192F65D4123A5B0057BB857B5C9D87C41F
	1B027EF74B415DC4F7C84F87D884108A107BA44C69G8E82B481B8GC2GA2G921EC45E884A3B845239714D593B63597D89F2E724105FF1A1E7BC9B6259BD88F2661F5CF3E03CF38EA4E86A1AB0C50D4F248C72A4BA24CE890C5BC44710A707F6A7360973E15D9C86F2928610BFFF0A7F5AE62ACFDD346B12F82F5368FE9F61B73A93988C706EA2711F3554877469BE9EC00676EEA473235F2661D9E1D7BC79F64553E4DBC2DA94CF7CE7172506534645875AE7B91701B09BE888FC487396462B73CBA67B0DEAD5
	F67585143D7BA9FD594B8710DD7314AA1B49BBDF00729AF464E57076C0F9B62071BE254A8BFF4ADF57349AD557FF37226C88835983699A23114DEC7ED1A63D9762F44B6DEEE7CF378D672CDB687CAF9E68E7BE05FDF89C1D7F25C0FA19407D41097DFA53D46CDF3F225FB2F108741BB4CD6EE931F41775FAE3BF11B5D8DAD646306BAE63FA6E2133363849D693C41E9642208E11D3D9DB10F46CD47FF64EBF2236F30A89E5CF53293B3F6C24293E6D1C22114D5A33B8935BF3896431B2DE913E147BF4E099E95C16
	ABF99979C0D371342AFF7D537EFE7F4ED51457E2A0AFDBA34F81B4A19AF923B472583A77ABC7685C8D70531E46770D7F2AA4771C1B4D9779AA77911BDDF13A69B2B7A07996F49F4352DB145C9F666C3B5EF31AEC4CFCFEA50E19F1D3A7C97143EA8C4F99AE64F5CE654F7844B1E96F0B3A9CB8C65EB3CD53605D894081908A908190BB8D6B764581FED61628DF25A8E6EB873D41A5B6BE6CF22F0AA6AF31FCBD35125C3BF5187B1CBD8A4430E768FD00CAFC2F1D89729782E482FE43B36CEC1017520CBD30E1007A
	B359DEAF319FBB21056BD8A9C7D55C9E0E9BF6D6456D6538128E95D74AF193DAD49C2EF3B51B48FB4DA9FAB7099D8FE57850BA02BFCC6F82E971B72DA3F84D4AA7DF3358E8D6E5D7F059DF651378A6CE990AFB49273D745E3051462928FF1873D32ADB10438EA5F89FE67EC13D7B92894F60BD2564FA3D7C79A27F7CE53D7CC5A2FF2D6EFD1B54E770DC6C4B6734F3575C7B9C6958277C5BB777D966271B1ED54F4532396BF7E27D38817236C8ECFC4F1254BF962FC657C9221F7C2F9C6CA71B9E99A7557C32A6FD
	2BC139E13C3C851AF2C3DBCBD7313277DB316C5C671A33D9B96FFDC54B2479A38AB03F76B9FD5D0C757D34DE15335106F2C2C4B9E70F2B34FFBAA9899F2825F77344A53B43CE0753DDE4F1182D1DACFF2A0574BF0B53CD97ABD87FFBC51C490B8779C9G29G3989ECDE4A6265EA97464B42847D981FA6296379C120E9C8D06325AD41FFACEFBE33D6632B6C10E0090EBD16FC2907E3757E649F2A0DBE5C05B6F2A568599AF9C2CF2936DAEEB532951319AE6C9EEDA053387DFE5C286AD6F08D65A48FDA8E6A7B92
	79848BDFD3FA75883135D8D3E2C3864FFB7D88290B38C7E7E6B743489D9B96536FBBCD72B7BC5F1CDD8A508260G88F99E633979A8099D6C5429FFACBFE2EB1771BD944B2BB9E63C16767531605FD6EABA361A7AA1C44AAEA29F71E70F113828B8DD9449F0670F5135CAFBFB0742C8B9DEBEC66C65105BA81A6F5DD462DAECA454ED7E733826DAF2C15C8546862F2CBC66365B2A4D6D75F4B4E2EDB529925F05F32663FE5482AA3386522DG4AF40CDF5E820CA3B19346CAC9EF6A5F1BCEEE3A1A9EBE1D75195869
	1A7B01BBAC99CC4E45CEEDBCA2F9C66F3A0BB9FF6E8CD54E7B424F32BF4733068DB370AC6DC7479197A6F077DCC6DC02409D3562FD71E5B370ECED7DB72A6F3DF30CFC67C5F7AE839E72951814DDECEBB237CBB82F357A2CB56E1E591CDDB3F36039B1AB373705441A9620D5GA2GE281E682AC84D8821079824C8FGF6835482348238GC281C681C483CCFB81FD7B353A6DC63A8312BB81F5F03759CEDC1C6B7F054F9ED19248CBF9E1E07D995FE81FFD76E540137362E07872FC76B7730027B4889F7379BFDA
	F02D359668C3DEC41F1F75B75FFB67747CD5B574D9C7B50D7F5532399BFD27CA1CFD0CFF917D7E101348DD82692CD9B0F64FC2DFB9609267C8E6850FE7770DA1FFEF71F856F8D692757D5BAEEDFDDDA0ABF456E06C345247CE914093B3A83E1D5734FC8940B3BF889F7FF64FE963D8B771F470D8B779B47D468144A8B2F7635F6EB9CD46E1E7DB1BC796B1F55A69DC012B73B4B8BDF6924579DAF865506EEEDD9AA25FCF2E1B0F6D875453BD1AC5337C774ECED37C52D97EFBE7EF08F8AD4D067946EC0CBD7B4E11
	B949ECC827G24822C1F837D8120E18E766BFFB413F535814F92B5B9DD754461B5DBB4CC7E08D6A25F8632C2G4683CC83C882D8A4645F5FCC76920D6246A927136E02118DB1889B32DB93C1188DF3BF575AB0F58E0F5DAF7135351782FD0F715DFCC24BD741696A797302A15F7DBEE70495CE97461F1106FCDF7B4439D8201B89300053AF5661E36BA917695CDB1A4B5A14613E27388D9C473F3558E25CFFC636846FBF7737684F959EEA891CABCCE8511FAB4434E8678A8C77F88B09E16D907FDB845D9401BBA3
	F0CFD31984770D5D26F4887C73D4AF022F15C901081FC5756A2538960ECB54604E60B94C969283204A9DCE37186335107527B63B434147AECAE36F0610A06C71EB2A235DE556681DA2F0E7C4DB1C3686364FD92B36FD98AE26A0106E7102C03AF2DBA05DC11B160E7953FB16E03123F42E5E1963B5831E49A060FF86195F235565EB18EB9C5BD9CC0A23631BED2EFF2CFA0662DDF37DE355D1A7462AFE48CB1E0BEB0407DC5A72EB92E1FE1DB8703842ECD732AB501E7BF6855AFD33307B3769085BAA6C7E1E202B
	3E06F42FE7A06E58B524ABCFC7FB1DA2D721709C1B66F7CA650D5FCBFB770616417A4217670C634D51F37A7B7C7567747779CF1D9B787C7259F34667174D67823F4FB72655D83728566031F0E22BFEAC7CC9EBE0AC0CEB550F05D3DB3531107949B67A6E60G3F6DCF44757224B45C379EA1F0E569E2EFCC60461CC05E45829759023C524F9077EE8B7266885C644B485BAAF021D5486B9238B72B1037DF60ACB416A5C2BAC560622CA8EF1940BDDAC4F0B9731A33B74DC35C8745A82FF09E4E174D8524FF73F7E8
	9A6A99FE13F70D0A47D04F4FECA72CE3F98EBB1989502F820B7E78AD55458B724367E12C186AD47B5B8C4D6FFA670D6FFB728DAD0355799BD7E0CCEBF5E9E39ADBFB7962F3BA4E415C092D79B7D17F0B82BB454C43B52E6D5F91D1EC7D29BE9D571F964E53DF2FC7595A3D240D9C3FE41E7F9E43F46A3329004FFA994B2BBF2F16377E8C16D77132FEF9FAFBF8D8DE7D4B7E657D0A7AF98B60ED22BC6785353C54AAACEF14C1F9FAFBE5D8DE744B7E6BEBFDD692A74C8EFBFBB7D94BE9C1BD6E26BE998774338198
	6EF50ADB86694CA4BC73B53348F7FFF324D955F5FBB16ADA1A242F2BF64F504B778CD1575AA4FFDD95AB291B15AC3F89FB5DC1F5EA825A8BC9E82FB1ED12601919C66EDDF44B3DF9E27D6EA8B8D36127D590B09D1F1A72CA603FBD643D1F3D33DECD47F756F0A1EF9AD92A606BF5260B2A6C91AD57A77B489914BDD3482E6ED6E5273AD4595E2A6B133D2B9AE5278A59630919F457CAD99951D6321627D9576CF2B93B494756CE559D2BEDBADFD5DFF76C4471A3A71AD0BF29C7556F21226B536FECB16AD77A8A6A
	377512DA7751DED576D66796FF59F44D3530E45FEE9301BDE5E55B719DA8DDBB27F2006C10D786730E7F5599AD5FA860099E945F1EEAAD5FB460C99A945F79E2ADDF8A70AC8B4247474C7440B879C0FA605C4FFCA6102E6DCCA0DDDDF5A05D096AC03ACB4501F4D70A35F46C7F5FD1F6E1B2D4B066EDE939A22DE05BDD70036DEFA9D2DA9E810FDBDAABDA320E3E22A4DF5ADB1971EF63B252254CBAE9851B2654C919E42ED1A7AD27D311BAA98326A9DE17249ED7A021EA39671F52B5A95390ABE9F2A4832CDBFA
	5F5733529E7E68165C3B767624CBD9C72595EDE6072B438CFA504F9055CB59962A90F9F3F7A8D2AE7DBAD5B17096D35E8375D23A5523C89BD5BC8DD1AC3751E5364AC0324AE6F6F7D289C425556724D5ED6EF68BADC011524F9DBD87455294494E2917D6AB9DF445G822BA2FD240AEEA06B86BC63GB50EC4A676AE7AA72A1B2E8501559AAD5387ABA088F63546642B95E9CD236234DBBB6CCEE8F4F71FA2ADA7E737AB81CF3EF218E7E59FE7A3E93EA71927743964CAB07F9A1F207142C3E53FC9FC235913A12D
	2813BEA6E3FD236CE61F793311EBEF7C7FEFF0D4DA65F2C26D9B818F3E62210F659E254F81ED343E6D94795630C711ED62A39850A89FB24B75A8F6C7F12D59532110C9B2B4C7A6F3548FD8B67952D8B17F529835E9DA55A16AB274BD8E74DDC775A5D20A0414F5CF2F7D6C6FDF2544E7C869C725558E2759F654ECD51C6EBAE915526116BD9DCE074D6B7A12894A6AA5DDA71B9A53ADED9455A715ADEA023694CAF7FAFF943EE6511A2350F4DE37D4CCA9C9E397898A9B74AEB794A53B6B94F2CC5C694E97F9B7ED
	D68AFEF66C2356F4A90DD712B45D5F3CAE0599CCF4C57AF135232035725A5BF3467E6A60124540F2143BBC941C2BD21D686932486EE1B123BF499E15FC87514BC54CC31DE233C23ACB175D812D3F029D7C96F83608C1131F9E12D406D5705E526DA11F7984ED727CCC01D922C6C5BF1C33FB7444BF1D25B5D2D995E90B5AA95AE5E7D7F1D5DB9BF81990G8D8962BF916289CD119F4D875E3BBF7C6A976FD94071EA240C2E5D3B693FA5745FBD645F920AA9211812BD501ACB24817F7C733DDED267EDDE1FEE351A
	E732EE35A1405D88F6D3205F90F49E33A6A1A7BFBE93C6A729C2724F8E724FCECA360B7E3BFBE8BA6F7B9F2873677F83F52E718E90F269365FFBD1D16FE9D2A9A95A947C39B4FF1A9C17EB32CF7A4409FEADA51EF1587E3D76EC4532DB2D0667023D2F7173402F29FB0AEC0D7CACDDE31A8D781457F0CDFC12F517E43C8F5858E5F6B8943323332A5BC676842B7B94D9DDABB9EFA372BDC843B3793DD09B517B2206677F81D0CB8788CEAC6D355BA2GG24ECGGD0CB818294G94G88G88G6D0171B4CEAC6D
	355BA2GG24ECGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG95A3GGGG
**end of data**/
}
}