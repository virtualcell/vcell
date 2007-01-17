package cbit.vcell.model.gui;
import cbit.vcell.parser.Expression;
import javax.swing.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.*;
import java.util.*;
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
			cbit.gui.BevelBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.BevelBorderBean();
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
		}catch(cbit.vcell.client.task.UserCancelException e){
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
	D0CB838494G88G88GF0FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BDFDDCD45715B6B189ED6856A424B1D97737B431DD3BCB3BEE175DE53336252DED4D063689CBDA3745545D5056EDC817EE5C54ED6C66B1038A0A9F0D38A515A4A4B195E59CBE84C5C084C184C5C5A18A0248B073189998E646B78F9990F54F7DB86F3D19F90FC1FE6B9F4BAF47B76F5CF34EBD771CF34F3D6F5E7BDE0402F763B7E41B8A84A1FB03407F8EE78B4234C790DCAFFD3C81F1B5FBD373844D
	5FFBG3F955EF06540358D4A529FCE4D5BA1741E4A057BFE38AFFAB0B56FF770FB27B0F0AA518EBF8C6A09053272FB6263E72E6745922CE7933D9F5C1F8F57EDG0300A357043D247E607EC20E1F4F718859A6C108A9A1B40F6CAF6238AC204985DC8BC0CF8923774315A9F0DFED8D276BFFE80930769FA553D6A2C7A39361B94E3B16DF0B05DFE7AF5AC76ADAC675A4BC9638F7G10461B051FD82CF02D6F9CF5B658075C32753ADD74F82CE3BE076831BA475D35352D1E71608551E6175DBE6F5596374D63F38A
	C261FEA2B327B4174A900455A0EFB145F50F10FA7A61378C903B9F712FFB88FE8D7C96CAD3738ACB31EDC77756F35DFBEE7C1D10D6B916335D20ED65252CCDBD25EA5BD2C6D6195E22FEF8075ABB8E4A12GD2G32GF6D8D2734A81F6D37FFF38EFBFDC1B0664E65F58184F5B64779F77B824B3242D703B3696CAB86E02E877C98EC1E0BE7E7AFE0B0EBEB38163FB6E0E7918CD169CC4FFA7DC7822107E571FB6ED5231C97A129B26B68B334520C535892BBB2F54286EF91650F88C2D7B12202DFB4535974C9BF4
	6A4E98F8419C4765AC51543D46C2FC3D0246408A78CD7836903FD37C77A95EF2C04533583CCC71643E834A169E40586859063AF464BCA96C28F8CC578E8B3F9EEBAA30B29DAC56F0DD76FA096C9EAB2B33D451658445A7E970CC176C31FCFEDFD886B1DC1E1A4762EB2F05745567AAD273B281F2AAA12FG548374GF8AAB1463C7B4AE699E31DB6C9BEBE6E76B8C4C9E03CFB777F8FDC4520AC595A3D815966350B97ED125BB66491838227A95FCFECB09442D7CDE35F8EF51C943D22E4A3DD95245B46C4D91482
	0C26E17FEF890DA45252EE771888950C7985145B3AFFA7DCBD36007C115FE113C51502151F5DCFFCF25DF691A402G784D6C52FD0058AB965A3F98A0D13143B3D45EE9D1720A1E5A5A8E1FEFF45C5FCDDAA2649CA4F6AE2639E385502F2EC49BF7DA892E8D4A878F229C2174CD0D1C96B74DC3B6E9927B58F7C7A0118BB2504781AC81C886C8BB08B1740B9E0C21162DDF90ACAFBC2A9BC3BDD7DFB0E79DE43113DB955E1752E99DC44FF2A86B847083AC28D24600119E3846F17EE42E47B62E571D74FF04B48D9F
	2BE3125631C153D7B7715F66B9485F467B9B7EE6767FCA997165D2E8D78AC0E6957A202E34184574F1DBC044A8BEA7C1G0A8E55A3CC4F835EEE386E82A0E3EE9340A8009B208F6083008F819E833882F0D8E0636AF900DEG52F70EG54819C842886B886F0C63143E9AABF27BAB54F82508DB08D3098A085E0ADC051A198C781BCGF1G4BGB2G728F2766D58174834C87C8GC883D88F302786FD39E92CB3FB76B644F606635F5334B53C2C0CDF31FD07B5ED5E465B5BE4D05F99FEBD9F066F65763DE24067
	66FE18CB1CC40347E6C143ECBBF0C736A5F13DC187CFE2F5BB1597D976AE3F923B6F653EC1DF907CBEC16DB761D04A2F3B4370EF83EC653EB66AC7F9F74936BBE7C1437AE8050558C43CAEFA659651AFFA9D70A3404BEAACC4BFBE2E8C7B941E860A7743B8548C09BF2060DBAC4436B6D9165CC3633258A54A325B6BD44BBBE8DD0171A13FBA04E1DDDD9662B35F840CC38AFD0F0564F03B4F6B95E932D0D8F879E5BA06089EF18C14EE760DFBE5069FBCC0665A2362A49F439C87B68AE48C720C1371104DDF175A
	087D7D12FB8CD29099D205789AB23E9CAEAD54582883F0E49EAE43B526864706098FF0EC58B375F9A1792A5F24B757D97859CF19565430FC3C3EC69D9B18CE9FD975F56F23E3994787182E7F50C86C201DD063FC5ACCF9FE78BEE99F1DABD4A02EA91B489F861EE61BA439C52902611FF19279275DDED1F65B838C6799589AA63FC2405C7E2BF2A22768880CE1GEDC7B0377F40CA4AF1FC65C93D596771B0D7C1D97C91E24F8FC68B38BD8661BEF80459F31BD27E38D23E866E055A70726D9EAC37C0D9CBC479FD
	CA399F4AE2B465CC4F65E5BBC84C095746C51896110944E9A5AE1EADFBB32C6CA20F91D67ECFE5A4C6BC1AB9C8F79D29F301971F3F12203E67G32F83D65F52CEEC6576DC33AF838CF2AD369D60650657A11EE9D392F87B9750C2E251E51A555933A2FA9F441FAB606AD28E77471GD6C55EC35710AE67A813D3442FD5C7393C23046E3404F4BDC7797C842EB1GCBG5E813C2B01506D8EA8729A587C25882E96004E862DFE57A611AE31010D29A99AFA2BA26F43A924DB2DA96F64D7D55E4B8A5D3A86B6B66B4B
	7B539BC8376398ABAFB996A60F36F7F49A699AB9DD8FDCDD47425BDBFD9369E2B8DD7CB1B6F7CBBC4664B13A1CDBC8371C17279FE37E488C21DB265095B5B23AAA3836G389A35F48F5EC63A69C6D6EFE2D83D4CAE6785240BEBE47392348B511358E5CF93215B100DF4890DEC9E333C1145C1DA23D65E1795BAFF931B5F44B4296D31AAF48F1810EEE1931B9B05574B5AD1E7C63A25DC4E8A382E84D85F446AE5F96D42B512C3689A42086D3AADE88DCC40F8D1DBFB8A539BDFAF788C1D73135C3B63B82E8B5450
	7C59867703G4163181B1F5F0A6B82F123CB0445D21A59E84DE341F126CB62F1B5B7DB86A74CAC6FB5D512BA57C2D9CEB3663C33655AE71326C040E7F75347A21EAFFED32E4D5D8CE72A58CC7230DB8A486DB03886B13764D690FD49F393C7736748D323385ED28875F6B663FA4BABE211E0305ED2DBCBD3BFEB3F7DA036FF4154E70541C71FB0EDB1E8FFCCB3FF8EEFD65B3F6345AAB353A3196AC157E8203C30857510763CE32CC717DF343BC97EE4B28EF930AD1D409F2348F8FA64DDE399B8B4F199899E54A3
	997857A9B2CACFFFE0ACC3BB14F1B9FFCF1F5B89EF798968F7GC2AB4197BA8BB4B609877C8A00CCGB6B6967BAA614AC4D95925D31406FD521868A8A1D1EB35CD48D6BAFF32B65162936477BE0C711C83E47EA709DEF82A562AD5326F2200EBA7CF6EEC647527FC71C9217E7600C9EFADABE63B4B54564A5778DAD51FA5BF3F4A1486405AF953076DDC86B4EBDB311DCFB8745AE9B90976FCF61C145BDDB62F136A49E78D20A613DB61C7390D4093BC09F29FAA554ADDFA1249DDF9926D37503FC9359F37496ECB
	A5F09D276B855C5D4DAE1F5BAE16F4D00BFAC089EB974CA23D4EBA2C7F67FE0C03CC103D278D651F3FC666FAB6494967B7135788DDC0A473905151AE0BE34C46533DE863F9A909C2614ECF6A5A18C0FF9B336D7CB65546EBFF3C4A143B9AED7C74B534C59A50643723AE6FC9846F763AE548DF6EA931E49FEB6FD72AF7F1FCC8EB83AC8618CCD9427635003CE9C5660FA412BF6CF4D168AC44905CD03BEF5D2435FBE2BB373B4237A1C06CA243641D357FBF1A34B11634A9D6377D2EBFDBE84AECE76DAEB82536BF
	815A1E2C347F9F03282B83E8961D42BAABA6E91DAEF7004D25635C5A39F49A50651F06398DC04BE964691D4C277D03C5C817AC7A193EDFF8C72BEF2E6DBEDDFDE37F4DE96ABF4D7DF5DA5537A4F51529BE9575FDE29275CD811A1C8E0C790286F2254F850A9F4ECEA1EDBD5049G8CDFF0837189004BD070ABEEE0AC941D01B95999ECD759B4493B1A79B7179FB70DF2E201F639C27F7AB4319D49924C3F976B757C3B7E8C56BBF0936B5DF1966665E7D14EB7EF12B1E8B4E4FE7F0B1B689746FB60966AB08F7812
	945E38DBC8476CF36E649E5A1764AECDF7527651F3C3A300D77A68CFEE21CED9A033649C4A6D389D9A97DFF0E963E2709CEB5B9238AED3F8645B5AF6B0790937D16FB4204B6FC4DBCC89CA5F815C340237E623BE09004B6CC4590FE6E34EE06DFC7CA4D9B7F10A58BD3BA77DA269EBA59D91DB9BB8BA556D0BCCD5826673575F46585D5332D828D9BBEA18EB761CE7B15BF1DE0D5D85E3556645G2C8DBF4D4636AD821A34735806B3A6EC8323DBEAC23A7C8B503FAE208D9CE63441B46092AFA07F3366D01F5418
	35BE493C4047072E543C46AE64F9552C7589DB1B799C5DAF686952DF2F74F07CBCFE0D03ABDBC79F22EB06CB3A58BABA114F709E0ACF5260D9BB9A2C583E95D0E6694635DFC7B551FB815CA7GAC87C887D88FD074914E5B96D410B5339957565B3DB2CC6BEC1E2E104589DC632EA4750E0A137C7ED425B6EFB0BBF40E4EED2FE7600E79D83B1FB9C472C38D34319B4083B09FE03152666FD5DA2235997788F8E319BE5F227AAC7FE8766B4F0CAF0D72255FB1DF3DE7EEF6BBB5C73EAFF966563EE44F5C5A77D5EF
	381E7997C3E514045DB33E6FC470D9436EBB4278D8AE5B5EE570CCF5FCDC16593A8C6D6FCF15E97B3B63A25BCF09016BA2G765CF50E2E95517CF9911F2FEE36EA1F2F16ED3FE978FC1149754B3F24F93EB87B9AFF3E9A2DA14FB39DD0668218873088A099A09DC06841B8AEBAF8B4DA9C5389F7372F99F0EDBE5FE8F378FA21E253FBFDF37345C391FC613EA86E8965E376FB5E01762B6941FD02437E3969F0A202AFDC87FF0FDEBCB8BD3B7563816CB964BB6E8BF2DDD7A8FB72B10A2ED991F506033E2E7F9D15
	AFB9CCD79693AF9FA1B131924A56826477429C8220834085905B0BB161BBF8BCDACCA85B185D3EA6DC0F56898C5E372F4D4DA75F0D608B774962DEBD3EA9E9EEF60D094A372CD7AF86DE9DAE540F018E5B1068E17EEF59144B6BC96B4531F5300E78A37FB278812091209FE09E40A200644B680FEFC47707620459786339605C6C73E2D43E1517756C63685DA74CF816859F5A1BED5E6BB6321F41F2E83AD31BC333AE33B9D3D19FBC6374E18ECDF1E33FEA6B4335AB2F134CE566417DC20025FDE86F976A093DD7
	40FD8640FAA223BFB52F9E2087E03A9F6D3D2B2AAE1A3DF9CEE4AB921106E6760A1F1C1B1D3F9815EFD13F9EDF77545C6A9B0A4A1728DB5F6FEF4C2D3E1228FCA93A75FDB702EFF6F9A4BD026F1D307B8C5D7A1E181EDB7B16C465DB5F2FB71EFD329EF3C441959CA39CB76756E6A9024F9AF65FF1C50F6F602D393539A9AA1FC3373E17A378E6573E7F0A608B776974953DDC743793E4FEB50BDCF402BC12CBEE3B4D53A9797C22A43B45C0274D0B797CA7AE4CBBF1D7B06FFC46CD72CE8A5CE7G2CD7FC78746D
	3959F4C5D43E0281BD3EF9425C6A7B2328FC965D7A2A326766436391FC61B15A92D69F4B639FB510BC5E8FE5C10085G89G4F81E4826C380AF95CD9C5569DE6BB0F09BE3F4D0C50F7F6F67DF5D43E722BFAFCFFEB1ADBFD4B2372356956B7E21E1B9F0391FC61FDF1702ADEDF7C763EAAE1463E082BC1E44128DB9C73FBEC32286903FEB3764160D56C033F26E37FD238DF81309A001DEB7BB21DFF2F037B22C14495D8882E9C6E5B945CA387884E83777394DCBA454541FD42A076E9D799D95FC9D66EA5FACF66
	A76C3E455A934587F78A242ED03BDF79BF2FC3FBE60D752131EA6373ABF2357E791529EA7D73AB732AFF4B70A0C3FB4A924BE32AE7BE5F32205A78FC4B73070C75DCF9C8DF4F978F696B19F1E8E6BDB38F996979B635617769DA21B46E155DE3A2954D62DD8C12B3CF10F5AE0CFBD9CAA25283616BC50A1CE77EE7BB55A3909083F44259E213ED3C6CE7B2597308B83B4337B058FA62EB5EBCCEF3C276B5D7A87D6E55FD864FAB1A3E7523B2FC36DEBD08FDEBC985E9D7218D665FGEDB60C75EE27DE3E99347133
	163648B59D1647530F11B53698A8DF8C308CA08DE02D8D73773D07232EAB1E884E302EB8DEE39C9353B57AB1F1CF8D594F8E7846A53B986E770F5794AAE56AA8C1966D0959994D4314DF36C9CEB1A2EE9E277C2C4C087F499A2358E2BEFD5A2775E925128B3E6F5073C1419033FD49D0248F18BC2FDFAB6F20A26FBB06675489549B4A5BFBED8B1757B9AC7946BA8F73367F6791E3DF2CBF226F8BD32D31AF72EA237B624DDAE3DF3CD59B5D976F5712368C399DEE091F3AE2ED7C55B52D4D2A15367F150478CE59
	57D9255832FD131EADBB398D9DF08D06591355BFD2E7EC33EBF57AB61B28B336592D3A68B6FB204E58E6FFD4975DE60F55695BEC532446E6E754DC64BC7136FEAE6A32CBBE0F07E4A4811F0F8ADC1813E207B0A77D58C5F252F238CF87D8BB04B969F977CC711BEF0F0CDF36BF722FF47ED0ECE77BA3598A7EE50A37E870FCDF5909E7B0EA20EC119DF3570F6B0DFD7753FAFD5FE5569B7B6E557A683EDBDFEF6CBB7351683E4BBFBAF36E7941E4E85F2E60788727343EBD24501F3F91CADF4D719B26357407957A
	6727357D27C269BF8F576B79D2F070FDF43816B8C27D49647D714D4D5A18ABC77C48ADED7DC7947A77EFEB71EAEE7D39EB46DC68B00A25DD747D19CEC7F8AC3D435E45F20447521F39B116DCD0166240FD41DF0668D523687BF7C228FD8FF17CE8F6889EC689665F2B8D46B1B95C209F131E8663188CB4C40F49698663183C37A1FACC46B64C9C139F18C2EDF00463DF21F3B0CD2C7273AB1FB1EBED290EF763914FB0DAA8970DFC6C257927C58C77718445F70A61BE96DC6863FEA8CB965147E925E4BE679007ED
	639E180E7A8262B9BFFFE508508E935EFFAF457C37E7984FD87C5584BD8781CF31E096F2BECC28F2925AD28B2E1D4683EDA2C09A40DA0082A756796787088CF85E32F914BA3EF6G1F2926953AD78F901BC9E240BD4567270CF60B95E9E3955ACF9CA3E7FE7989C3F02ACC6750A409B3BB2FAE8B3773A227EA5B25CEA3BB3BAAE91EF7B2FB3E2560EFD23C60D2714C4EDF24F3D53AD787E539EEFC169B25FBEEE9F01F2160D21C789C1865467C1DF14C382F3CFCCC3F2F7C72187E73442F0E4D7CBC7146B12398E7
	7A3D62C01D9BC7D06733A26A5C2160B2CA9137C8418DEEC25ECC857735CDC837CE41A58C53F73CC6D37386C7F18CFDABA46F4923213E0999B572D5974DE30BC6434F9EDC21780451703387BFF1615903E5D0D66CC11D5638D14F799E54E98DB9364AE369171A5FD34E7F6BE7E1AD30BA9E392BF530B6FF3E9E7D152658E173D13443EA85F75D027EAA9FC3DC36E22F16B134576E9155C6FBB43F3F603E7B767A56DD2D0335B9560CED0ED35AAC3B54F65E547C5E683A7BEDFE772E5641F26CC68B69171A69327512
	5B6B70CDC03AA5EF274A81EBCBFBD767392E93D74F1D3D5A5C81BF58590278896D7B6AB51B6F51FD2F06C046986B5B6B78F57D2EA732599C7CBC0DBB42D36ECD4DF3F989EE1525A0CCC7678636164B4A379F501613DCC7768355722610F2226B5E9CEDF9A024FC03061F356B598F714C447AE7178A1E3F19EF789E7ED2AFEBCF9A3FAE48F8BCFB4DDFCE1919DDD752771AE4E9DC6473C1AB5E33D87B95DD934977C10E72A97D30C269F3BE0CBFEFE5B9519D6E278196GAC86C882C88148GD88FD06007F9ABC0A3
	004B0F3C77D25E05F01F8430524F5E1FA06F0C1077E048BBAE640C67BABFFB8F021C79A76FB9B05E440AE4CDDBC97D781B54C12EC9DC17F03B909DCB7CF3FBA7F6BD1707EDABB010631AC17E34AFFCED73E7A1ED512ECB62FB1DC66F7791FE23F78223813E3BC862E7BB0D81FD3A37E61021FDB71245516ADCBCABDCF80DFC3B6252C992358B2E1178F9495942E97B2FC55AE783170160879A823BF8597CEB6A7C12491EEAD3E50BA24ADEE9A0FB438C32B3B532193C57CBD15EBA9DF9E66E8F1417AFC1BF11D4F9
	D5D2382EFF522E6A3AF2934AEE1474E54F24EB37C6B63379A7AD743D901FA4BAA55F385701F3569768BCE79068BD927661F23A2E908B77718AEE67B8766B6592762FDEB771E3C1G6F6D741E3C274577B26FB87793D93325E5F5645C7118FAA76F21B3DF3C61EE09A2CF8B65500616G7385396F89047BB979146A67770E22EC07CE5B43E5878321FE0E5548E67E7C2C857D39904AD89D050A7EC9BAF5E4F3DBEE62F524G4DEA0D7EEB8361F17F14996589323E3CAD9AF985C0D3A22B726AE5D59EDB779EBBCD67
	EE00F748783C71C8A3F94F7922ED042FF21FF038E51FC4173981493720A799163EC54963A151F22767B459187935CDB8E6A6BE7D67C2527DAD06678C6773B6AF665712179E4F5E263465879D64B9B38D4A32G8A46D373ACG0DGBD6358369FB6723332C47DA6D93659DD748DAEE56343ADCE28E872904B7A7036F2725E2D47B6699B17A1070D53F79B4B7139568377738192G1247595920626B749CC5C5618C6DE73203C1E2BFF6C28B5731B2DAD5DCA5475DFFD9459D64388A170A2B62384F7528B8DC673AE8
	A14FB597683BC96CF8A8434FEFA778E67AAE10967F311D842FD97964EB963BED2A6C9AAE7B549E125F14D3064AFB499F8569FB43C69B27CA7BE3FC6FD35D229CF6A00BF074FD092FD66A3DAF613A0E6FA9A5DE52AB1FD64AD719744A97A8656F39744A97DF47F3315F6B534EDDF7BC61B1E11F8A77EF52F596274B2F2B67E243570F57C1593B829B5FF385750F652B0AF30252CF7EA59F7BC951DFBCA5347C20457401C13D65937CAC7E04DAEF4AD83119555D2F541D84652C1EBF6E24F552F20B854B33A674F5B3
	5677AB67D5B9F939A827A408753CF6C1257D74C70292837FBDCCEC2E3CE4576C7378A46B1047E69FE565A7DD247F8D79A43AD8417A5F0072EE49A0488F82AC86C88A32F9A94B177B47B1DFBE97544F715902BA1EE7G4D3A201AAF73A74347720B3D5BB5314A8E89D66858A36B189E0E357B6FAF2AB63AF094EDD4B829E7EB64A96ED1ED756AC6A3DBB1196DEE1459E9A05358FF4D3D2AEEE9E61493B3EBB9286F70E5F247F22EF41A58DAD9D3E2C386AF1BBCCD5A223CC7E7E3EF0611F7ECD8CEBF54C74A13217E
	F4GE18A72BAC0499466735F1CA639031DBA8D4F65A79CCE911FC3313E559D46EBE9BF6B087EED252C0E4DB48EA1D30E917908FF2D0364C5596727C806FB3D032ED53A1DAE1911F2FC7699FA2EC39C26E8366EF4269C57E2DB20ED53D33826DAB1283C8B0C8E2F2DBDA9399D47ED4E8EBA9AB1DFBDDA0E4F4209D3389F758DAAB3936EF3EE2466955D407C957C836611044FBDA9AC746A3FB73D3A7B26296A866BB3BDB754BC32FAE415094999995566A3D2E6742C9B477913B4F236A9F176DAA51EB5DCF7834F52
	3EFC89F1655308ABB0A34E2560FADC783EF87CB41E2DDD532F465EC3E748F7DEF467B290A12740246C4536EE1BD340F9ED68DA63CA101BB9BD735C18553BF300649A61A6589A209120874083B09FE0A1C082C0B240CA00AC001CDB508EG8BC0BDC0A7006396467675F37B0CF487A48F829A60124DCDC21C6BEF8B51BF8872E2EF4D2CBF632B88193F4583CF622C783665EA79D6GCFFA94BE967377ABEB2DD9C0DFF29BE37E5B3F8FFD6F1C1E3FBA8EFD56F31C667F63224D0B3173DD656C6360ED0C7BC11948DD
	8877CB81D65C46D8699BD74E115C0E1E4F1EF81C7C7D9B4FE77FFED5D05AFB6928363D057340FF73E6E32733A17B400D4053BDAB3EFF8E598FF2814FF494BE7E6D1EBE63DCF722AFFA2EBB5BC73FF1GB90A4C5D7837FB7A48B86C9B9E8E08CACE6D695B21605A831D3E001BE4F126FBD7795C5E5D324D116F09BB66E37B8107699E4D02F961FBE70D943FE8DE785E59B3CA3EDE82E5799F435C13BAC866A4FE380F85D88C308CA09DE05D4730DF3F52C75655E6BCCB546D73F7100057EC51B07916A1A2BF7F9E68
	AFG9DG8E00A04002FBD07E61FE3217E814B7AE78FCF4970CEC08C15A90A5CD86E1B6FC3DD2EB434577705C4D2FA977C4469E633B582B65DB4D695672EB6E3DC6FCEF0544F79127AB675796C33EABA1F92E8768BCG73B8FD1C8E1FF60DE5493D4C278CD7CDF1499C473F35B8E05CFF2C83517B4F61817D3942510148394271817D39C25B00F62E40F0E786C88EF3C27E6FD168AEA838DE85F7094AA438CBEE875D08E078ABD4AF02EF93C9050837D33DA6A82E0763DC9ADCAF1E439CA0B9G1A6C72C94A9CEF00
	2CBF8D3BBD9EBEF6D19A3797D202E20F9BD4C7375F2651FB1E026BD5FC9154714F4D907FB0DC1BA5126E0CA5126E435CC83A7D39DABA96CF31B31E2DA0109E96D3BC67C4658BBF034C787EC917EF1DCE5C06662AB4BA3E654F8F4FD52BA83EF0FEF82E9A16B1D76D013218794AB7A9473575E782DE38EF66F105594EFBB4521E012311F6975DC8F745043891B7523D2150E51911EE1599F17FE9C63A43A63457D1328240FF37EAFEFF2F7C6E6F252D392BF5303E7023C163FC732F037A7B7CEB86757779FFB1B873
	796557860D4FAF7FE6B0727BFC55C3463A558FC54F05A70674F361692148DC58B9241F8BAF8EE9F3A10B13B78F92DC81446D1E7BF03D7C19DC5C372ED1F056BCE5EFCC410D75A2EF1C027B3D9DF917A8389BF664CDD4F065E6644D399FF1873B1137D04145F5A36F9E85A753DCA643FD2C027B168B6545AB38DDD5841788774B955CD4954AFB6EFE1CAF5B7EC07AB7FF0626291E610B02DBD5BC26FAFEE6BBF9A7AB733849CC00FE95D8690FBF273A94BF101AD775G660A9F4AEAFFFBC9735BA35F7D3EF75FDD2D
	0335F9E2BCB227CD0FEBF39ADBFB59F7100EF3B0F7E2EB7E47E87C35011D3A9F40B52EEF5A09FD587A5309BCDCFF1A7F007EFABD4A566EA56D6678058F046FB1FC1A466CE240AFD56ADB63D06BFB580E752DB628CFEF8F8F6BDB9BD1DFAF0DF3A1866638B1D85F16E1353E3F68467A6AE3746B535BAB437ABAE34257579EF411BCE173381DDE32165303FABCC5E3329F687DG7C8C9A454543FDC28C1E7932D4056EEFDA1D2A2E8FD5212E69863AEA778C0B791EA16A1A9521EB361B344DCE165F94FB955271EDBD
	5066FE9C6DD5CD163AB94F17F349FB97DEF122D8D93FEB05E0AAF93F89922667FDCBF1857CD7C91E7B59B36B75BCFCE62DD264750C91F96C778A1FAAFB1B7D4EE4FF430E32BD0A6C97832A6C0557D559FB3B6FCCF6FAB74ADE2C48EEA41F3E53DDAB656F933848DA1EE6DDF34C6F73120F2DDDE8BF5956FD3529237D6459B3A74EF623FE0B26D47D722A6ECC3F2FD721FE690AFEDF3D21363D6216AA3BD9FEB7DCB6DDF3AD29B8D4EEA1D0D9DD3D0F672A9BF95AB9D541A760B96D9333F946DFE157725583CF672C
	787E25DB4B67G1E602C783EDD25650B851E78A8FCACA7BE1C9719A79F4B0B1C7B7D589EC917E10F24DB579DC9771B6EC83A972BA269DE2A5252317FFF477362172001892F889BE4E1A35B6E029FECFFCB96320B8984A4E13B6249F67A08327A7A41DA73CBCF19CDC2CE3B3011CDD35A059CB257E897B65029C83BE006E9CA50AF284795C82A5A9038A65C92F28C310226C4B040CA423650484A7E6C6F6E5D71290363A6A137D558B8EC7378DDB65003FE0628C358B2C495A2CF6E9ED958C13FCED5863CE514372E
	C3B059833230DB45539445CA3B7CB63B88A4F98E1BB4CAA59015B67789F94312F308D6A08B263E56BE28165E11627C8EE1336C22AB8610D8E561F72A68CE32EE408B6A28F184B2317753BF2539A6ADA8D82D51F2C2308AC441EE5618FC33ACEC6D12FDEE3B4B6D8327CB133230011C5D2E85BC794AE1311DFD1C0D5C73BD49E2F952AF5602793706A40DEFFE267A27A9AFDE8C18050D6D42DBE42C6F92A57619BF87F96D0D7F7F8DDA05BC3F8FDA5F85F80815G3DEC8848139E70D1417085722D6100ACBA140FE0
	00D3F6B14B0D4BEECFD91BAD601249A4995C11438275CDD6CC3EB4D6463FB4C6ED1A5D54C8C306BE47013EBB293EC40AD51132739F36FF785E290CA433E0EA95B6FBFCB6C72B4DAE7B24F6A1CFF6C9E2406573B8027EE3CCD06E8469BADB28B1A5E13752FC52D8EBB778D2D1FAB478A731DB57ECED855785A5210CD292E7DB958A8774AE892A9225F6199C9377C9FB14320F3DA37F61BBA7FF57EF9232F9A3096BFE9F744BA42C475C9E7052C6F6C01BBA759D38E50B8DCAA7251FDDEB057648BFFC6519A7FFD41F
	158942DAF9E7G15F628D4E7474706C4697E04C75F5E92377AC122313F91FB9DC84E038794EF00FC4F9304970735990B94552D0FBCD37E6867FFFD192A2E324A423BEA74BBC55FD8D95370B00413A2GBC86625FD644939AEB984D1B4107F61DFA610DA108309641BCD6DECE7F2D207FD612FFABA82602E2AAAA41EDD9420CFF6165C1ECB38DCBFA73A10921A07975FEB024DBED66BC2CDB95C6049B4196C5468DC197AB5EA3F79F10FF7612FFCAA853FE7A6F01BB561DF0150569DC7D7FD0677A7F07BAB787E7C8
	39F45B6F0D7878B7B4F799995ABB7873EB7EB4A5FE7F17C26E4F1E1D56D2629907FD3735E7AB565DE7B7BC97DC7C881B2FD4BD226EA932B57257689C5C8F7858C7F0CD3C55FDC0B05E876C9A33F9BC324DB35A64F510BD41631332282E153C6EA67283C8438B79FB21A3C46F918D4F7F82D0CB8788784C919227A2GG24ECGGD0CB818294G94G88G88GF0FBB0B6784C919227A2GG24ECGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBA
	GGG61A2GGGG
**end of data**/
}
}