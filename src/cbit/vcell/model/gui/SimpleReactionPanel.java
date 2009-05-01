package cbit.vcell.model.gui;
import cbit.gui.DialogUtils;
import cbit.gui.UtilCancelException;
import cbit.vcell.parser.Expression;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import java.util.*;

import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.FieldDataWindowManager.FDSimBioModelInfo;
import cbit.vcell.client.FieldDataWindowManager.FDSimMathModelInfo;
import cbit.vcell.client.FieldDataWindowManager.SimInfoHolder;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.model.*;
import cbit.vcell.model.Model.ModelParameter;

import javax.swing.JButton;

import java.awt.Component;
import java.awt.GridBagConstraints;
/**
 * Insert the type's description here.
 * Creation date: (7/24/2002 2:30:19 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SimpleReactionPanel extends javax.swing.JPanel {
	private cbit.vcell.model.SimpleReaction fieldSimpleReaction = null;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimpleReaction ivjTornOffSimpleReaction = null;
	private KineticsTypeTemplatePanel ivjKineticsTypeTemplatePanel = null;
	private ReactionCanvas ivjReactionCanvas = null;
	private javax.swing.JLabel ivjKineticTypeTitleLabel = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JButton ivjRenameButton = null;
	private javax.swing.JLabel ivjSimpleReactionNameLabel = null;
	private javax.swing.JLabel ivjStoichiometryLabel = null;
	private javax.swing.JScrollPane ivjReactionScrollPane = null;
	private ReactionElectricalPropertiesPanel ivjReactionElectricalPropertiesPanel1 = null;
	private javax.swing.JComboBox ivjJComboBox1 = null;
	private Kinetics ivjKinetics = null;
	private boolean ivjConnPtoP2Aligning = false;
	private JButton jToggleButton = null;
	private JButton ivjShowGlobalParamsButton = null;
	
	private final static KineticsDescription[] kineticTypes = {
		KineticsDescription.MassAction,
		KineticsDescription.General,
		KineticsDescription.GeneralLumped,
		KineticsDescription.HMM_irreversible,
		KineticsDescription.HMM_reversible
	};

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SimpleReactionPanel.this.getRenameButton()) 
				connEtoC2(e);
			if (e.getSource() == SimpleReactionPanel.this.getRenameButton()) 
				connEtoM14(e);
			if (e.getSource() == SimpleReactionPanel.this.getShowGlobalParamsButton()) 
				showGlobalParameters();
			if (e.getSource() == SimpleReactionPanel.this.getJComboBox1()) 
				connEtoC3();
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SimpleReactionPanel.this && (evt.getPropertyName().equals("simpleReaction"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == SimpleReactionPanel.this.getTornOffSimpleReaction() && (evt.getPropertyName().equals("name"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == SimpleReactionPanel.this.getSimpleReactionNameLabel() && (evt.getPropertyName().equals("text"))) 
				connPtoP2SetSource();
			if (evt.getSource() == SimpleReactionPanel.this.getTornOffSimpleReaction() && (evt.getPropertyName().equals("kinetics"))) 
				connEtoM6(evt);
		};
	};

/**
 * SimpleReactionPanel constructor comment.
 */
public SimpleReactionPanel() {
	super();
	initialize();
}

/**
 * SimpleReactionPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public SimpleReactionPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * SimpleReactionPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public SimpleReactionPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * SimpleReactionPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public SimpleReactionPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 10:09:40 PM)
 */
public void cleanupOnClose() {
	getKineticsTypeTemplatePanel().cleanupOnClose();
}


/**
 * connEtoC2:  (RenameButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimpleReactionPanel.renameSimpleReaction()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.renameSimpleReaction();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JComboBox1.action. --> SimpleReactionPanel.updateKineticChoice(Ljava.lang.String;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
	try {
		// user code begin {1}
		// user code end
		this.updateKineticChoice((KineticsDescription)getJComboBox1().getSelectedItem());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (SimpleReactionPanel.initialize() --> SimpleReactionPanel.initKineticChoices()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9() {
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
 * connEtoM1:  (TornOffSimpleReaction.this --> ReactionCanvas.reactionStep)
 * @param value cbit.vcell.model.SimpleReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.model.SimpleReaction value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffSimpleReaction() != null)) {
			getReactionCanvas().setReactionStep(getTornOffSimpleReaction());
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
 * connEtoM14:  (RenameButton.action.actionPerformed(java.awt.event.ActionEvent) --> TornOffSimpleReaction.name)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM14(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getTornOffSimpleReaction().setName(getSimpleReactionNameLabel().getText());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM15:  (Kinetics.this --> ReactionElectricalPropertiesPanel1.kinetics)
 * @param value cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM15(cbit.vcell.model.Kinetics value) {
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
 * connEtoM16:  (TornOffSimpleReaction.this --> ReactionElectricalPropertiesPanel1.visible)
 * @param value cbit.vcell.model.SimpleReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM16(cbit.vcell.model.SimpleReaction value) {
	try {
		// user code begin {1}
		// user code end
		getReactionElectricalPropertiesPanel1().setVisible(this.getElectricalPropertiesVisible(getTornOffSimpleReaction()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (Kinetics.this --> JComboBox1.setSelectedItem(Ljava.lang.Object;)V)
 * @param value cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.model.Kinetics value) {
	try {
		// user code begin {1}
		// user code end
		getJComboBox1().setSelectedItem(this.getKineticType(getKinetics()));
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
 * connEtoM6:  (TornOffSimpleReaction.kinetics --> Kinetics.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffSimpleReaction() != null)) {
			setKinetics(getTornOffSimpleReaction().getKinetics());
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
 * connEtoM7:  (TornOffSimpleReaction.this --> KineticsTemplate1.this)
 * @param value cbit.vcell.model.SimpleReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(cbit.vcell.model.SimpleReaction value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffSimpleReaction() != null)) {
			setKinetics(getTornOffSimpleReaction().getKinetics());
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
 * connEtoM8:  (Kinetics.this --> TornOffSimpleReaction.kinetics)
 * @param value cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(cbit.vcell.model.Kinetics value) {
	try {
		// user code begin {1}
		// user code end
		if ((getKinetics() != null)) {
			getTornOffSimpleReaction().setKinetics(getKinetics().getReactionStep().getKinetics());
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
 * connPtoP1SetSource:  (SimpleReactionPanel.simpleReaction <--> TornOffSimpleReaction.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getTornOffSimpleReaction() != null)) {
				this.setSimpleReaction(getTornOffSimpleReaction());
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
 * connPtoP1SetTarget:  (SimpleReactionPanel.simpleReaction <--> TornOffSimpleReaction.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setTornOffSimpleReaction(this.getSimpleReaction());
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
 * connPtoP2SetSource:  (TornOffSimpleReaction.name <--> SimpleReactionNameLabel.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getTornOffSimpleReaction() != null)) {
				getTornOffSimpleReaction().setName(getSimpleReactionNameLabel().getText());
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
 * connPtoP2SetTarget:  (TornOffSimpleReaction.name <--> SimpleReactionNameLabel.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getTornOffSimpleReaction() != null)) {
				getSimpleReactionNameLabel().setText(getTornOffSimpleReaction().getName());
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
 * Comment
 */
public boolean getElectricalPropertiesVisible(SimpleReaction sr) {
	if (sr==null){
		return true;
	}else if (sr.getStructure() instanceof Membrane){
		return true;
	}
	return false;
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
			ivjJComboBox1.setRenderer(new DefaultListCellRenderer() {
				private final static String MU = "\u03BC";
				private final static String MICROMOLAR = MU+"M";
				private final static String SQUARED = "\u00B2";
				private final static String SQUAREMICRON = MU+"m"+SQUARED;

				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					java.awt.Component component = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
					
					if (value instanceof KineticsDescription) {
						KineticsDescription kineticsDescription = (KineticsDescription)value;
						if (getKinetics()!=null && getKinetics().getReactionStep()!=null){
							if (getKinetics().getReactionStep().getStructure() instanceof Feature){
								if (kineticsDescription.equals(KineticsDescription.General)){
									setText("General ["+MICROMOLAR+"/s]");
								}else if (kineticsDescription.equals(KineticsDescription.MassAction)){
									setText("Mass Action ["+MICROMOLAR+"/s] (recommended for stochastic application)");
								}else if (kineticsDescription.equals(KineticsDescription.GeneralLumped)){
									setText("General [molecules/s]");
								}else if (kineticsDescription.equals(KineticsDescription.HMM_irreversible)){
									setText("Henri-Michaelis-Menten (Irreversible) ["+MICROMOLAR+"/s]");
								}else if (kineticsDescription.equals(KineticsDescription.HMM_reversible)){
									setText("Henri-Michaelis-Menten (Reversible) ["+MICROMOLAR+"/s]");
								}else{
									setText(kineticsDescription.getDescription());
								}
							}else if (getKinetics().getReactionStep().getStructure() instanceof Membrane){
								if (kineticsDescription.equals(KineticsDescription.General)){
									setText("General [molecules/("+SQUAREMICRON+" s)]");
								}else if (kineticsDescription.equals(KineticsDescription.MassAction)){
									setText("Mass Action [molecules/("+SQUAREMICRON+" s)]");
								}else if (kineticsDescription.equals(KineticsDescription.GeneralLumped)){
									setText("General [molecules/s)]");
								}else if (kineticsDescription.equals(KineticsDescription.HMM_irreversible)){
									setText("Henri-Michaelis-Menten (Irreversible) [molecules/("+SQUAREMICRON+" s)]");
								}else if (kineticsDescription.equals(KineticsDescription.HMM_reversible)){
									setText("Henri-Michaelis-Menten (Reversible) [molecules/("+SQUAREMICRON+" s)]");
								}else{
									setText(kineticsDescription.getDescription());
								}
							}
						}else{
							setText(kineticsDescription.getDescription());
						}
					}

					return component;
				}
				
			});
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
 * Return the Kinetics property value.
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
private KineticsDescription getKineticType(Kinetics kinetics) {
	if (kinetics!=null){
		return kinetics.getKineticsDescription();
	}else{
		return null;
	}
}


/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKineticTypeTitleLabel() {
	if (ivjKineticTypeTitleLabel == null) {
		try {
			ivjKineticTypeTitleLabel = new javax.swing.JLabel();
			ivjKineticTypeTitleLabel.setName("KineticTypeTitleLabel");
			ivjKineticTypeTitleLabel.setFont(new java.awt.Font("Arial", 1, 12));
			ivjKineticTypeTitleLabel.setText("Kinetic type:");
			ivjKineticTypeTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			ivjKineticTypeTitleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKineticTypeTitleLabel;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setFont(new java.awt.Font("Arial", 1, 12));
			ivjNameLabel.setText("Name:");
			ivjNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			ivjNameLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}

/**
 * Return the ReactionCanvas property value.
 * @return cbit.vcell.model.gui.ReactionCanvas
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReactionCanvas getReactionCanvas() {
	if (ivjReactionCanvas == null) {
		try {
			ivjReactionCanvas = new cbit.vcell.model.gui.ReactionCanvas();
			ivjReactionCanvas.setName("ReactionCanvas");
			ivjReactionCanvas.setBounds(0, 0, 359, 117);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionCanvas;
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
 * Return the ReactionScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getReactionScrollPane() {
	if (ivjReactionScrollPane == null) {
		try {
			ivjReactionScrollPane = new javax.swing.JScrollPane();
			ivjReactionScrollPane.setName("ReactionScrollPane");
			getReactionScrollPane().setViewportView(getReactionCanvas());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionScrollPane;
}


/**
 * Return the JButton property value.
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

private javax.swing.JButton getShowGlobalParamsButton() {
	if (ivjShowGlobalParamsButton == null) {
		try {
			ivjShowGlobalParamsButton = new javax.swing.JButton();
			ivjShowGlobalParamsButton.setName("ShowGlobalParamsButton");
			ivjShowGlobalParamsButton.setText("List of All Global Parameters");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjShowGlobalParamsButton;
}

/**
 * Gets the simpleReaction property (cbit.vcell.model.SimpleReaction) value.
 * @return The simpleReaction property value.
 * @see #setSimpleReaction
 */
public cbit.vcell.model.SimpleReaction getSimpleReaction() {
	return fieldSimpleReaction;
}


/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSimpleReactionNameLabel() {
	if (ivjSimpleReactionNameLabel == null) {
		try {
			ivjSimpleReactionNameLabel = new javax.swing.JLabel();
			ivjSimpleReactionNameLabel.setName("SimpleReactionNameLabel");
			ivjSimpleReactionNameLabel.setText(" ");
			ivjSimpleReactionNameLabel.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSimpleReactionNameLabel;
}

/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStoichiometryLabel() {
	if (ivjStoichiometryLabel == null) {
		try {
			ivjStoichiometryLabel = new javax.swing.JLabel();
			ivjStoichiometryLabel.setName("StoichiometryLabel");
			ivjStoichiometryLabel.setFont(new java.awt.Font("Arial", 1, 12));
			ivjStoichiometryLabel.setText("Stoichiometry");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStoichiometryLabel;
}

/**
 * Return the TornOffSimpleReaction property value.
 * @return cbit.vcell.model.SimpleReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.SimpleReaction getTornOffSimpleReaction() {
	// user code begin {1}
	// user code end
	return ivjTornOffSimpleReaction;
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
	getSimpleReactionNameLabel().addPropertyChangeListener(ivjEventHandler);
	getRenameButton().addActionListener(ivjEventHandler);
	getShowGlobalParamsButton().addActionListener(ivjEventHandler);
	getJComboBox1().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
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
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 3;
		setName("SimpleReactionPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(461, 600);

		java.awt.GridBagConstraints constraintsStoichiometryLabel = new java.awt.GridBagConstraints();
		constraintsStoichiometryLabel.gridx = 0; constraintsStoichiometryLabel.gridy = 0;
		constraintsStoichiometryLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsStoichiometryLabel.insets = new java.awt.Insets(4, 10, 4, 0);
		add(getStoichiometryLabel(), constraintsStoichiometryLabel);

		java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
		constraintsNameLabel.gridx = 0; constraintsNameLabel.gridy = 1;
		constraintsNameLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsNameLabel.insets = new java.awt.Insets(4, 4, 0, 4);
		add(getNameLabel(), constraintsNameLabel);

		java.awt.GridBagConstraints constraintsSimpleReactionNameLabel = new java.awt.GridBagConstraints();
		constraintsSimpleReactionNameLabel.gridx = 1; constraintsSimpleReactionNameLabel.gridy = 1;
		constraintsSimpleReactionNameLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSimpleReactionNameLabel.weightx = 1.0;
		constraintsSimpleReactionNameLabel.insets = new java.awt.Insets(4, 5, 4, 5);
		add(getSimpleReactionNameLabel(), constraintsSimpleReactionNameLabel);

		java.awt.GridBagConstraints constraintsRenameButton = new java.awt.GridBagConstraints();
		constraintsRenameButton.gridx = 2; constraintsRenameButton.gridy = 1;
		constraintsRenameButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsRenameButton.insets = new java.awt.Insets(5, 5, 5, 10);
		add(getRenameButton(), constraintsRenameButton);

		java.awt.GridBagConstraints constraintsKineticTypeTitleLabel = new java.awt.GridBagConstraints();
		constraintsKineticTypeTitleLabel.gridx = 0; constraintsKineticTypeTitleLabel.gridy = 3;
		constraintsKineticTypeTitleLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsKineticTypeTitleLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsKineticTypeTitleLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getKineticTypeTitleLabel(), constraintsKineticTypeTitleLabel);

		java.awt.GridBagConstraints constraintsShowGlobalParamsButton = new java.awt.GridBagConstraints();
		constraintsShowGlobalParamsButton.gridx = 1; constraintsShowGlobalParamsButton.gridy = 4;
 		constraintsShowGlobalParamsButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsShowGlobalParamsButton.insets = new java.awt.Insets(5, 5, 5, 10);
		add(getShowGlobalParamsButton(), constraintsShowGlobalParamsButton);

		java.awt.GridBagConstraints constraintsKineticsTypeTemplatePanel = new java.awt.GridBagConstraints();
		constraintsKineticsTypeTemplatePanel.gridx = 0; constraintsKineticsTypeTemplatePanel.gridy = 5;
		constraintsKineticsTypeTemplatePanel.gridwidth = 3;
		constraintsKineticsTypeTemplatePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsKineticsTypeTemplatePanel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsKineticsTypeTemplatePanel.weightx = 1.0;
		constraintsKineticsTypeTemplatePanel.weighty = 1.0;
		constraintsKineticsTypeTemplatePanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getKineticsTypeTemplatePanel(), constraintsKineticsTypeTemplatePanel);

		java.awt.GridBagConstraints constraintsReactionScrollPane = new java.awt.GridBagConstraints();
		constraintsReactionScrollPane.gridx = 1; constraintsReactionScrollPane.gridy = 0;
		constraintsReactionScrollPane.gridwidth = 2;
		constraintsReactionScrollPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsReactionScrollPane.weightx = 1.0;
		constraintsReactionScrollPane.weighty = 1.0;
		constraintsReactionScrollPane.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getReactionScrollPane(), constraintsReactionScrollPane);

		java.awt.GridBagConstraints constraintsReactionElectricalPropertiesPanel1 = new java.awt.GridBagConstraints();
		constraintsReactionElectricalPropertiesPanel1.gridx = 0; constraintsReactionElectricalPropertiesPanel1.gridy = 2;
		constraintsReactionElectricalPropertiesPanel1.gridwidth = 3;
		constraintsReactionElectricalPropertiesPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsReactionElectricalPropertiesPanel1.weightx = 1.0;
		constraintsReactionElectricalPropertiesPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getReactionElectricalPropertiesPanel1(), constraintsReactionElectricalPropertiesPanel1);

		java.awt.GridBagConstraints constraintsJComboBox1 = new java.awt.GridBagConstraints();
		constraintsJComboBox1.gridx = 1; constraintsJComboBox1.gridy = 3;
		constraintsJComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBox1.weightx = 1.0;
		constraintsJComboBox1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJComboBox1(), constraintsJComboBox1);
		this.add(getJToggleButton(), gridBagConstraints);
		initConnections();
		connEtoC9();
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
	for (int i=0;i<kineticTypes.length;i++){
		model.addElement(kineticTypes[i]);
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SimpleReactionPanel aSimpleReactionPanel;
		aSimpleReactionPanel = new SimpleReactionPanel();
		frame.setContentPane(aSimpleReactionPanel);
		frame.setSize(aSimpleReactionPanel.getSize());
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
private void renameSimpleReaction() {
	try {
		String newName = null;
		try{
			newName = cbit.vcell.client.PopupGenerator.showInputDialog(this,"reaction name:",getSimpleReaction().getName());
		}catch(org.vcell.util.UserCancelException e){
			return;
		}

		if (newName != null) {
			getSimpleReaction().setName(newName);
		}
	}catch (java.beans.PropertyVetoException e){
		cbit.vcell.client.PopupGenerator.showErrorDialog("Error changing name:\n"+e.getMessage());
	}
}

private void showGlobalParameters() {
	// pop-up a list of global parameters for user to refer to while deciding kinetic parameters
	ModelParameter[] mps = getKinetics().getReactionStep().getModel().getModelParameters();
	// set it up as a 2-d array of data 
	String[] colNames = new String[] {"Name","Value","Units"};
	Vector<String[]> rowsV = new Vector<String[]>();
	for (int i = 0; i < mps.length; i++) {
		String[] rows = new String[colNames.length];
		rows[0] = mps[i].getName();
		rows[1] = mps[i].getExpression().infix();
		rows[2] = mps[i].getUnitDefinition().getSymbol();
		rowsV.add(rows);
	}
	String[][] rows = new String[rowsV.size()][];
	rowsV.copyInto(rows);
	
	int[] selectionIndexArr =  PopupGenerator.showComponentOKCancelTableList(this, "List of Global Parameters in Model",
			colNames, rows, ListSelectionModel.SINGLE_SELECTION);
}

/**
 * Comment
 */
public void setChargeCarrierValence_Exception(java.lang.Throwable e) {
	if (e instanceof NumberFormatException){
		javax.swing.JOptionPane.showMessageDialog(this, "Number format error '"+e.getMessage()+"'", "Error changing charge valence", javax.swing.JOptionPane.ERROR_MESSAGE);
	}else{
		javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(), "Error changing charge valence", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
}


/**
 * Set the Kinetics to a new value.
 * @param newValue cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setKinetics(cbit.vcell.model.Kinetics newValue) {
	if (ivjKinetics != newValue) {
		try {
			ivjKinetics = newValue;
			connEtoM15(ivjKinetics);
			connEtoM8(ivjKinetics);
			connEtoM2(ivjKinetics);
			connEtoM3(ivjKinetics);
			updateToggleButtonLabel();
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
 * Sets the simpleReaction property (cbit.vcell.model.SimpleReaction) value.
 * @param simpleReaction The new value for the property.
 * @see #getSimpleReaction
 */
public void setSimpleReaction(cbit.vcell.model.SimpleReaction simpleReaction) {
	cbit.vcell.model.SimpleReaction oldValue = fieldSimpleReaction;
	fieldSimpleReaction = simpleReaction;
	firePropertyChange("simpleReaction", oldValue, simpleReaction);
}


/**
 * Set the TornOffSimpleReaction to a new value.
 * @param newValue cbit.vcell.model.SimpleReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTornOffSimpleReaction(cbit.vcell.model.SimpleReaction newValue) {
	if (ivjTornOffSimpleReaction != newValue) {
		try {
			cbit.vcell.model.SimpleReaction oldValue = getTornOffSimpleReaction();
			/* Stop listening for events from the current object */
			if (ivjTornOffSimpleReaction != null) {
				ivjTornOffSimpleReaction.removePropertyChangeListener(ivjEventHandler);
			}
			ivjTornOffSimpleReaction = newValue;

			/* Listen for events from the new object */
			if (ivjTornOffSimpleReaction != null) {
				ivjTornOffSimpleReaction.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connEtoM1(ivjTornOffSimpleReaction);
			connPtoP2SetTarget();
			connEtoM16(ivjTornOffSimpleReaction);
			connEtoM7(ivjTornOffSimpleReaction);
			firePropertyChange("simpleReaction", oldValue, newValue);
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
private void updateKineticChoice(KineticsDescription newKineticChoice) {
	boolean bFoundKineticType = false;
	for (int i=0;i<kineticTypes.length;i++){
		if (kineticTypes[i].equals(newKineticChoice)){
			bFoundKineticType = true;
		}
	}
	if (!bFoundKineticType){
		return;
	}
	//
	// if same as current kinetics, don't create new one
	//
	if (getKinetics()!=null && getKinetics().getKineticsDescription().equals(newKineticChoice)){
		return;
	}
	if (!getJComboBox1().getSelectedItem().equals(newKineticChoice)) {
		getJComboBox1().setSelectedItem(newKineticChoice);
	}
	if (getTornOffSimpleReaction() != null) {
		try {
			if (getKinetics()==null || !getKinetics().getKineticsDescription().equals(newKineticChoice)){
				setKinetics(newKineticChoice.createKinetics(getTornOffSimpleReaction()));
			}
		} catch (Exception exc) {
			handleException(exc);
		}
	}
}

private void updateToggleButtonLabel(){
	final String MU = "\u03BC";
	final String MICROMOLAR = MU+"M";
	if (getKinetics() instanceof DistributedKinetics){
		getJToggleButton().setText("Convert to [molecules/s]");
		getJToggleButton().setToolTipText("convert kinetics to be in terms of molecules rather than concentration");
	}else if (getKinetics() instanceof LumpedKinetics){
		getJToggleButton().setText("Convert to ["+MICROMOLAR+"/s]");
		getJToggleButton().setToolTipText("convert kinetics to be in terms of concentration rather than molecules");
	}
}

/**
 * This method initializes jToggleButton	
 * 	
 * @return javax.swing.JButton	
 */
private JButton getJToggleButton() {
	if (jToggleButton == null) {
		jToggleButton = new JButton();
		jToggleButton.setText("Toggle");
		jToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				final String MU = "\u03BC";
				final String SQUARED = "\u00B2";
				final String CUBED = "\u00B3";
				String sizeUnits = MU+"m"+SQUARED;
				if (getKinetics()!=null && getKinetics().getReactionStep()!=null && getKinetics().getReactionStep().getStructure() instanceof Feature){
					sizeUnits = MU+"m"+CUBED;
				}

				if (getKinetics() instanceof DistributedKinetics){
					try {
						String response = DialogUtils.showInputDialog0(SimpleReactionPanel.this, "enter compartment size ["+sizeUnits+"]", "1.0");
						double size = Double.parseDouble(response);
						setKinetics(LumpedKinetics.toLumpedKinetics((DistributedKinetics)getKinetics(), size));
					} catch (UtilCancelException e1) {
					} catch (Exception e2){
						if (getKinetics().getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(SimpleReactionPanel.this,"failed to translate into General Current Kinetics [pA]: "+e2.getMessage());
						}else{
							DialogUtils.showErrorDialog(SimpleReactionPanel.this,"failed to translate into General Lumped Kinetics [molecules/s]: "+e2.getMessage());
						}
					}
 				}else if (getKinetics() instanceof LumpedKinetics){
					try {
						String response = DialogUtils.showInputDialog0(SimpleReactionPanel.this, "enter compartment size ["+sizeUnits+"]", "1.0");
						double size = Double.parseDouble(response);
						setKinetics(DistributedKinetics.toDistributedKinetics((LumpedKinetics)getKinetics(), size));
					} catch (UtilCancelException e1) {
					} catch (Exception e2){
						if (getKinetics().getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(SimpleReactionPanel.this,"failed to translate into General Current Density Kinetics [pA/"+MU+"m"+SQUARED+"]: "+e2.getMessage());
						}else{
							if (getKinetics().getReactionStep().getStructure() instanceof Feature){
								DialogUtils.showErrorDialog(SimpleReactionPanel.this,"failed to translate into General Kinetics ["+MU+"M/s]: "+e2.getMessage());
							}else{
								DialogUtils.showErrorDialog(SimpleReactionPanel.this,"failed to translate into General Kinetics [molecules/"+MU+"m"+SQUARED+".s]: "+e2.getMessage());
							}
						}
					}
 				}
			}
		});
	}
	return jToggleButton;
}
}