package cbit.vcell.model.gui;

import cbit.vcell.parser.Expression;
import javax.swing.DefaultComboBoxModel;
import java.util.*;
import cbit.vcell.model.*;
/**
 * Insert the type's description here.
 * Creation date: (7/24/2002 2:30:19 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SimpleReactionPanel extends javax.swing.JPanel {
	private final static int KINETIC_MASSACTION = 0;
	private final static int KINETIC_GENERAL = 1;
	private final static int KINETIC_HMM_IRR_KINETICS = 2;
	private final static int KINETIC_HMM_REV_KINETICS = 3;
	private final static String KINETIC_STRINGS[] = {"Mass Action","General", "Henri-Michaelis-Menten (Irreversible)", "Henri-Michaelis-Menten (Reversible)"};
	private final static String MU = "\u03BC";
	private final static String MICROMOLAR = MU+"M";
	private final static String SQUARED = "\u00B2";
	private final static String CUBED = "\u00B3";
	private final static String MICRON = MU+"m";
	private final static String SQUAREMICRON = MU+"m"+SQUARED;
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

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SimpleReactionPanel.this.getRenameButton()) 
				connEtoC2(e);
			if (e.getSource() == SimpleReactionPanel.this.getRenameButton()) 
				connEtoM14(e);
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
private String getKineticTypeName(Kinetics kinetics) {
	if (kinetics instanceof GeneralKinetics){
		return KINETIC_STRINGS[KINETIC_GENERAL];
	}else if (kinetics instanceof MassActionKinetics){
		return KINETIC_STRINGS[KINETIC_MASSACTION];
	}else if (kinetics instanceof HMM_IRRKinetics){
		return KINETIC_STRINGS[KINETIC_HMM_IRR_KINETICS];
	}else if (kinetics instanceof HMM_REVKinetics){
		return KINETIC_STRINGS[KINETIC_HMM_REV_KINETICS];
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

		java.awt.GridBagConstraints constraintsKineticsTypeTemplatePanel = new java.awt.GridBagConstraints();
		constraintsKineticsTypeTemplatePanel.gridx = 0; constraintsKineticsTypeTemplatePanel.gridy = 4;
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
		}catch(cbit.util.UserCancelException e){
			return;
		}

		if (newName != null) {
			getSimpleReaction().setName(newName);
		}
	}catch (java.beans.PropertyVetoException e){
		cbit.vcell.client.PopupGenerator.showErrorDialog("Error changing name:\n"+e.getMessage());
	}
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
	if (getTornOffSimpleReaction() != null) {
		try {
			
			switch (newKineticType){
				case KINETIC_MASSACTION:{
					if (getKinetics() instanceof MassActionKinetics){
						return;
					}
					setKinetics(new MassActionKinetics(getTornOffSimpleReaction()));
					break;
				}
					
				case KINETIC_GENERAL:{
					if (getKinetics() instanceof GeneralKinetics){
						return;
					}
					setKinetics(new GeneralKinetics(getTornOffSimpleReaction()));
					break;
				}
				case KINETIC_HMM_IRR_KINETICS:{
					if (getKinetics() instanceof HMM_IRRKinetics){
						return;
					}
					setKinetics(new HMM_IRRKinetics(getTornOffSimpleReaction()));
					break;
				}
				case KINETIC_HMM_REV_KINETICS:{
					if (getKinetics() instanceof HMM_REVKinetics){
						return;
					}
					setKinetics(new HMM_REVKinetics(getTornOffSimpleReaction()));
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
	D0CB838494G88G88G710171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BF89465352088D123860D955F9151C688B6E8540820D1030025ADEDE9AFAA570B3D54A73629251536347E598D908CC80428E9C1090AB531E9C8GAB862C8142A398B5A64B921E496C6EE4F7124D6E32BBA11B87F04F7FB833333B335918DE7B150F1319B97FF97F67FF4E3FC30AB624AC4DB59591123B1408FF9F659212E6A3E4F2DB468B08CB72D88B086E5F5BG2F127D66BC3826C019E3363520
	10B81D79701C814FCF4F34963C8177EB095BD9FA94EEE268A1D0B6772D69F5836BF1FC05FA1633671263AB617A8AC0A66068B54FCB75EFBBDEAC7073849EA157C448FCC653F4FC3D406D0767D600EBG523D1CFE86DCD7B04D67D5EF93F447C62712C58F7BCD2B289C1DCC04D29F672D975792727B5CC5BE2A2B1F59C9F9C6407394G6A3C197C41D481579D8D9D4EFF5AED0ADAF14AAE3B5D951DDE076C2EF0F6A95555CDCA274FADB74A12DDD53C1E8649A33B89711D24B2330F67B39904EC0367A58C77E3BB55
	13817733G96E8783BE50A2F71DB8B1A81FCFE746D1377F7885B1B7B6EA28B1C53D62C0E61DB021F7B146E8F79B6264CE67EB32B07F10DB45E8B212CB0E0AD5888309F408510G300E557F042FBE006BCE1B3A5B5B5969756C7479F6399D7E4F292FF0DFDD8DA58257A85B3DFE87A13C0E4FB9DEEEE04FC04079123EB69F0FC96A962C6F3446DB493C5B3FE5DAEE9013F929FD2614G0FC5E6A094932EFBE6CC5DF3836179982E7B90516BA64FECDE3E54C0777A1FEEDE3ED048A1EAC877B6155675BE872D6B9238
	4F95316078A6066F5761F9EEBE42F206BEA7C359E295F32379953425BEEFACA92C3A42B08EE39EC8B49D6B62B628DD11362CF0D259A9DDDCE715E64B878C3F72D4884FED390F65A9FD8EC2D9DE50DA4073EB22053655249EEBC19AC0B640BC00A5G453D560272DE4C31C5DFED9AE40EB5C8FEF5D71762F648FE42F9A7B63E8ED7B9287A25BACFC015BCF659AA799549661683C4505C59C8E3E08B633B07653E9DF46C13BD32DF628D55AFF54A2A6C8FF01A6C46D7A90DDFE6259625D386851DBE02F2E7B52E052B
	DB8A2887FD8EC915C3943CFCCEA32D13D31295A402G3867F1F974A00DD7BD786FGD035B8FC59C465FDA67B213F282E2E77FABB3AFC966A89496E25F11E4F7A0E8438CF6945987F54CAF179FD560212BE14B332E90DCECE2D42FAA1495F03ED6CF316D3DB015E82906C435C79C5B366CE6D2A09247CA7179B66CE9264F1E29F4F19F1FD11ED68B013CD6D1B85E58B7B304F7F9FFB33CEC630AF3CBDD97A0C5BD7A4EC0DC147F5AC8E5331BC02E669A064D3B0474073F857B633B650EFADA885585F0FB13F3F2904
	6770AEA9A0E356FE690704139D219A60F62EF6360A381A05DD2B047CCFGBE855883308F008E2C9F83EC87788AE0B7C0996163AA9D165F83F89F60B840C7G6F827C8D20C50BC9A553558AF6A6G24834C85A02741FE008600A0C08A408C009C0062B356029AG97C092C0A640FC007C335602CA00E600E1G29G3381961F45BA1EA68F4B8D77CB9F3FD58344D57F7C367083FDAB93BE9971FE240B8F466E9591A7FD8C75BCDF9948B9AAE258B2489C79CF86DE9725ED34AE309EA25B420A983C8586F571F7DDDD
	ECG5894031756519B22BEE9DD2F9678B719BD340D9072DA844FBA83B96F0ABA2F7A8F08653F8A3CEF384BC2E3A11F12BDEA2D6C13BD8E38890832FBAD34AE4478556655F832995E87635DEE98E0829A7EE18B0D25242AFE4556254ACD322AAA9EE7287CC7CCD7204B668B8D15286B918B4D85EFB70CF79A7D6396BAD6583D9E0F4CBAA90DC5144FE7E3156C16BB41685D5EAE0F4A71CF9E24F37A8E39C70C154F9EDCC668D8676E2263AE1FA727CBB41FFCFE25933ABEBAF4935FB0BA0E3D284D73A914G0E4E77
	EB615ABC8C47226EF7F1AC5A58FB8B49EA71190C66D4E3EE3A4414B90C0F8933060546A2EE53E3D6E35B1FE0E3264087382D4FF6507CB41A381B99CF1509662E456B77FC595696CEC60820B9D6C265FE26F8E4D5318778F8F17AB035E1A158C606C35FBA9C470B6307E8B90E51E220586DF53BE51459BC1C466ABAC7117035921E778F6731FAC5AB5FADE3F98ABCE76A4A391E834D05B49F6413DDB2CC0D68E462B32D4E3EEADE93D1E69575474B8FB7537AF36B66A12567D01DC1A72E419688FDCB0673713E984A
	5F827C8CC6374B05F46AB92172C40074F3380DB567D23AD79424ABBA170FD365670668ABG4F75BE2051A50A72F1F04DG18F3AE57BBEC8425CBEA576C837C920075A3381D47C608F5A213E769C03AD9A3B85D0291DC6E46737476D53811EE6BF9FC5CAC97FA6B4F6376F1F9CFEAF4474E63F2D4380EGC89572B60E24F4F7F4A25D14737878CB65D17B660469BD64C1BA5FC8BEC6A70C8C492D507CFD5B0BF4E320BC9DE076C8AEEF41C83D3CC7B53A62D1FC2C2F9C4569EBC669650D77A1DD73A8AE47A72E8923
	7471F31CC43A34D1FC2E17BD0A7BBBA70C6E6EEE24FBDA50950BFCA089FA3A91C1AD8F92C265C89F22BB5003F465223CB6017BE18913E76AC53A918921FC524B63F1192151DD134067B918FFD4FF05D6EF7DFDC817114067C43392F8DC66A56865ED6D576CBB1F4F17EA4F8F59D921517DC923331C4F67D911FAB95DED27114EFDBE1F13D1BAAAEF44797A7CF31F517C907AB2601A8D7074795CBE5E577D3A0C763FEC7F22DDBAA585AB825DB006D4D79FE09D9E5F2710CE648B7AA56763FE44C592DBB7DCGF98F
	6038G7B6A9F2E427D08640ED4B26F192A653176DA7AAF60F624DC906A2B49AFF7AE63FD6574A3D467FCA84B39G7B49B94D7AB5514EC040EBD758F2CC74D1679E5677659C17F078E53836A97E00DA8703ED907B330B8FD3FB69FACD20457AF59ADB3350BEBC6FC2EB418EG75C216BFB29D63E85F467D34EFC1BF13FAEFA22D17DFE5AA0861E766055C3F3997067CACBF33F8A95757AE633ED2E1A2746909382FB439FD8309312FD4DD0D430A307986A75ABC8C78B3817898D4ACD3FD0108810A17153B28BF2ACB
	89F0FFDE58D9037D5F2DE3C96672C41351BE126B5BE3CCB9095C0F620BC27E2425584CD9G5C1E8E57CA11AFAE20C98160E3FD26CDBF564F85FC614556022DG7BAFC61BFF4BF87D425AA6D576F17BA63E2537AFDFBA4F503E44E71C26560B39DD8997076CAB3D54E65EF1A95A771102711A8DB47917E04C0B7EC92FEC5EC2F0BF277FB06A9DB6AB03942F3D48D0AFEB6F17F0FD2ECBF4FB07575B4C79DADC1E5374E682CD4EA568F3E3BB2D8B0F54A937A6D13A1939C5BA3925C9FC0DDF1F04743D0CDE0509A217
	FBF7BB46BB95E86683F0B953DCFAB94523391C1A51A82724E3B3DCF924AB44B4C77637F97D1D322314768695D237DA41662A95BBD9719EFADF06FD47F3EC0E6F17296561532052B22B085F58357AFA5BF17604E17C92D63BCC1651BCEE7D23C3714B9AEDB34F990D71BB5D01711B81B464D274E341107D906D440D7167FA8E39D14FB6506153744CF60747FDF5A75225824DFC0D6ED4A736AD1E77CB9C7A3CAF3E0C57C3ABDC554B10673ACE3AAEBB258494BA3762729F6F44FACD843A6C4BB0CF07ED5A85D7272C
	6E21B3C43FE2175C8DFE2FCF762B0A9C30F2912575D15B5161D5343D6C8866789F5E44BA5AD8BB0EECDB5491B34717DCA666AE493A363F6AF7CB33G384D43BC989395E85212513F27BCB4A6125FD945698EF8513785C0337EF24C59179C7A1C35DC4EE355FFB94A1964DD9E2AEB11076B8E1542350B6D3B092EF1374B2B58A1882C765D1247D951840172B83717D939FE338F6DCC8259B3B4796DBE3413BFDF4A6882B20D346C28D365CE4E7FE39F5ACF3EE5AD5876AD642FBEC9710AC7D195492D744A25E55C46
	FBBFFCCD6043AC8C501602E34FC5A7D1268F6425EAB2FB3BB11F787358EEBA4EF508C100737E2C9B7D198FFC45D7A0EFE7B01AF6E290E99B00AE81006383BD084F825CE2CD4623BD281F7B72653E0DACA6EA13AEAC7AD8FFE9EB873C966B3F75225C4AB1568257985477AB8D1F8238399A7E26BE0CC36115B0B6DC09F62C6C473A61F6DC39EFA7EF87A224169E1F7C8534E2035C4791F3FBD9F4CAE871BF5E2F0D2320B3D3531B5D9F1E3B6F1FC63A9C20A93D8A6DFECC433782EE4CD548EFB993B926F17A4B4F20
	1F3301B67FEA24DFF2A63C7FD8C8747DC74D553CCD8C03EB324633712C3E1E793ECF5B0948F798E9D70776CC33F47788FCEF3F4CC66D1AF3B55F5BDF2B6137B3FC5EB5A1BC7763B2B697244FDB21EC76B5389F7DACDB5B3B2F059C83C8814884188B3078DA1C533DFEE48B09335FDF67D1E14AA7391B42B6B2F05F7D88753FC36E914F330F6847F09E076FBAE89C0A2E9B5C3E72AB9A5FA30C2F7CEB70717E346C087EFDE35E2B87CC54C6237E7418106D3EAE541F7258DD5DC27B41B4404FG188F30982078FA0C
	5B4123657162066FBEC44038CFBEF9E86F624E8C116F5B4EF8FC5B2E8F0FAF673BA3AE5F3188BE9E37DF3552F50F8AE5C9G29G59GCF836425C0DBCD41785DF8F4D73C786D440DB50BD73B5D9D39B906761EF4C55ADB1392EE1F311FE72378AAA27D0C10437BBE47E13AAFBA40FBE79C67F7CB1ED3925D9364FD49A4C95F1728A93CAFC901EB86GDF6F6558703D742C94DC6F5531F5D15E8DB0D683EC3D815BF81B0D463B9E1EDDG43G2E8148821887C046E23CFFF3F4FB3CF833C999441A1B9D95E89E2FCF
	14216553E7F179CA469A655313F66A5FB6A833G388192G52GE6GAC547C5BF7ACEEBEE92F948713CF3F9A221FAF45654B3B51A81FFE5D96E39F20DE3261BEGB1658BBD6BEF44F570693D7A757E145527E32E03ADC22FFA23EEFE6F36AC67F65F57BE3476733DA83E487613E1686F6A26BF9B7B3B2BCBD5591EAB77F9098B77BEE65D08B97FED1966C45EB8EBC18940B6G8BC090A099A0F39C6644D547BE99E44E6F865CFE2F3743A09D041F67F68CAD9FC647651BBB4E086F38FBE87ABA627265986AFB2DF3
	E87A5E0D4B17FF13D17D5F593D038C2A7FB4DAE3697646364A58D7165C04FDE5BD6BAB1B61598710F0933E93DD90E56B60F27C05A83E489CCF3949286FFA1F6D4DE7C2599C0005G79B7DB8B4A81768338EF46BCFD7E584EF8F92A6B3B62676AC84F506AB2B9AEDF624DC6FC8D5E214575C494DFE4DC475DEC14BBCE77BAE258F7C07CEC5EDD5E2068BB7E216546141BB187D67806961BB76272ADB20C4DA3D1FC030B4DD3D1FC1131D979EDA33E4BCE8ECD5F8DD1FCEFC5BCEFB45477F4775062795B38FCBB8C75
	5D9E9C1A3E7B6372D98C7575748CCD5F2838FCEEC3FDD53DC35377E9DC3E91067ADE9C223E25FD71782EB154B7A10AEFF079F9F794DFC544F30621BEFB7F507CBB9917EF16213E3F1F9E1A3E6DF17996986AFBE2087AFE9917EF09213E2B4F8C2D7EEE09620B6CDF0AD20D7A7A69E5DB490073845CD9221BCF9639536716D4D9B7BF58180BE3C0F9AA4E8F7E54CA67878EF86E87C88660E77ACE3377ECE9701C25610E34D25C824A1B0AE348A69B5DFFAB3945DAD083508C508F108C1089B09F000C3796D40EC77A
	1D0CFEBFBC3B4673F755747DF642F87EBE103E03216FE969BBD3CEFF2514240B834A5247A6D96899287E1A87A82F91E5A591B4194297A39EEEDBD618EDF816C8CFC7579F2B87308BE545AA4333EA11E77F68FB4CD8E72578BC6A5747E31F6D797DF16333BD640471591EE5A7DE65F81021BF690A652F1C98786C4F6BA70C4E7E3C497C7A29096DC7F6F9D425D3E6EAF92E4BC1FA468BE6FB0DDD9EBE95241A83117BC9846717CE768EB1F5BC4EA353CEECA6517348BD8EC5757AD108C5167CDF3635E98B60A61FEC
	C71BEFEC099D3F71AD4671CBEFB10E5F1DAD83476F5E16D87163B1FACA257B75D1E7216876B24177C60FBBD7881A86555BD025497EC3EBECBFCC12319F79924D2F00374BEF17A3EDDD4D76ECF8D9E856CDB75D29B91C2618712B125FA9C7793A01717332D87C256CFC16CDF1A8FEF16A0847E1666B2BD99C8381B940962B3512AA897FD3DD687F9E557BF995516647FBA82F417C38496E773A5D34F7A4383EFEC6427EB1FBBC760FE6364F3C841E0BA640FAE58276CB3F318DB4E654CE08351FBD152F47A670FD6B
	DC8D7F9043BBF4F83E1F7D25835F497B20EC4E844CEF1FADF63DF65B0C6B7534ADF63D0E30452F570BEC316BB559963FDE2F35454AF3DE072AD200B95C6677F6C296F33E9CFBECDFDF349B7B7AC7FBECDF739C71FDDD65086DEB11A33E2FEFBAA2F3187BD85A2EEF27340F92F3C813D1BEEDCB933F8102EBEB1A513ABAC521FB73AA14A5812482E4814CCF43BC394F91771D461E2061BB8DF18622DD572EF6076C4DB55C5BA393390DC59323731F5BB3C90EDD171932F1DD5EAB472E4BE9F27C3A7C3E9C3BAEFFA2
	472F4B47650173F6F2474B7A3EE7AB6213593BFBDDBE0B7738751DE1747F4018FEBC60FB0DD20318723EA30B7591BBA6C676A9B399FE7F4448BE25D16BD3ACD016B9915F5B2D7304353F5D587E2A1C316BEC1B53384E7E690CDDE7FB1C716B6C8BE76CBABB680CDFE7A71C835759D4CFC45DEC6178D1DEFD3EEF5172FD7B00F5337E56683A6172EAFC61FD5A9601AFBC29577331C67F7049305C7890719B027A71ED1BC67FEC987E9F9ABE3DC70F5F2C61DD3DFA7CC79A3E22AF5C4EFF887C4F42715083F07CD57D
	617408F71C8E47FF0867DA4E0463BF92789F1D515B930AF7D96EC0712EB408B74FF9BFFB7FDBFBEBE4DB68E37806DBA35BC21E04ED619814E55D0AEDA17984BDB76E16FA3CDDAAA1C95F213493CE607B2A0D703C834081907C8EFFEF45679B133A291FEDFE2993520DCDA85D691477E4AB724E857A05GAB81DF8ED01F0EBAF389BD775C24FA953BCB71F64A2A3F073D7140BADAF308460A6638C08BFC1DCC4FBE059F2989D1F11A8FB7D0BF9B5979A87EDAC16467E9BA37549DDA30A82ADB134FFD1AD6796790CD
	7462D233630DF756B09D62EC01EE02454B0F6CFDC7D79EF3B33B8A691718E9DB50F6B2814F4F65745A0C4E24E52463391D5FB90C4AE7E86525ADC6657352F14C6CF02D52779DE21E4D73729752C0F919139EABAF3B79F905C9BC7F4C9AFEB843976970BCAFD758707C46C6A8CB1804EB7015F6DC03AF1C04F351879DC433EF166E7E2023F9GDB7FD560BABC5F288E6E73FA762E2F70B6EBC149ED980761A60CC3F98857CEF17D701CFC9B56E52A92FB6C7A0EE2BCF65D21982F636ED186DE47BD28449AF338FD45
	8A35AF95EC4B54ECBE2D209F599A2EC1C6DC516DD8478F696A75873A7B66FFC39D1F7CB754F123663363F60CC3CDAE62B234B88425106F43F577AFCB5FFC9C5E7DC6F50833FCA7F0BC4A4B00B1A6834715AAF616C4DB4752D32455557B7C0AE317642CE72394DF432EEB49977CEA06F6DE22054ACC0367EC00F999783EBC78D7FCDF1EF673D8B27B6746676367DBCE1B96E508F356F7684EDA16594CDC67952D2853826509F730733E2D7ABE1676E3137278BEB5AFFFB12C1C369BFA06A4D4DEA6694B293DD71B75
	65C7434A176A7839EF5FFD8FFDDB725DF4623EE3C44C6FAC4C91BEADC65F3EFFE56E31AB9A16F15FA659701CC1651DF8A6251265AA9D67DD9A2E2585F1A9F7E27EAE35E17E4E3D937377340D6ED3155E054F2358B37D6D11788D2EADB247A86D7FEF9E17C66908755B6221G1F6F3CEB4779CEAD585F8C9084988310F9974EF73618708C735C3B6877B9DEBFC9EB4E11C9794718EA857F7ACC6339359EAA01269660B5118B4D1921F99E57B5261D7D76426B171DFEEF1787FA5039CCCFBA9BB35260BEB3936D5A24
	045B7539BFE4D7322259F5F734DDCBA36D829A8AE8D76D5DA13B38BC270C729A8C646512506FE429BC87508405BC7A1C98A5CF51640DB110E75649221086B4D9BA7B66C449BB140B72664730EF05CE66A220491F4C47DE7A3CFEF2C81EF8879CF58EF4F0B97B549079CADA3EAE9F1F172DE36FC5CAA7C72EA3B6B27C5649D17BF4AD58EF57C0593849F8BE27B2C05BC17EBD56020DGBBGACGAAC052BDB8C7B95CDEC9626C51D83C3EFA184438F5538E6E5FB52DDF57BF5EBF5E3F9C7B470C4963C966485A18E7
	25465D436BBDDBDCB3E7DD19CB2F5C3F87D56A5FA2A8AB1CE2AD588AD08F508A901C027E5D50AE4E07526D771D2AAA59DD6CD7D15AE63EA2F70750F45144FD3BB3EC0FB5F10A780D1638E6CC094EC74E77E158DAFD06201BA72EB9621AB755DAD0BC155FF3BEC7D82E140B329AF135082B8B2E7D621E371175B6ECA3C9D30D5B9C36E3FABF8EE8B227065A702C2921DC62B4CA1072C59E1A6463CC1D9D4F004D1D0AE36DF0FB64D87B9C510F15375BA3475A5C30721F5BA3475A9F187465EFB8A247DA6467397403
	237AB34605D739CD68CFE4AC8A6F6571D9FFEFE89E417D1AE9C23F9A20EC83616F5272897E4B79B49A87B1E07D7372EE22597244CA4C6B7593EEA4357FD5EBFAB706AD89420639BADB76DB362E60B227779002E34784765BA56D47BB3B3DEE2F3F4266166C9DDCE7CA375E7FEDAF1FE3781BF0667FFD62B7AB621A785215427FE98A7AFF6CBEA37F5157FDFD741E730C1111A75910E7201835770764DC254919753565DCF4BAA4A7A1976564E58DD6CE4E27DCCE7A997A445B615B2D217B8FF47787DB7E8D6B59EF
	D4876F8FBF8E44DE1356840C5724FB8346EB527A40406FDEAD01585FDDB89A50FFF7017767434E52DC5F8175578C508F1084B0AE8B7B72E782F48F071FC80F6C4777B81CF4BF17FE6DC8553EC52446767547EA7CEFA44DD3693C4D66D5D5EFA7150F781F29343ED42F0FA1B96EA91546482FB8DDAAA7957867D51AEFEE390D21795C7CFBECEE1E85FE4D4E4279FA5F9AB6DF0767C59AEE7E9A1C43137B91D7AFA3EF0306ABE46D57814F2A066B52567643B45CF8765B1859701C23617E48F0F98FD88BCA9F50ECE9
	C19DBBB45CF5259457G4F2D9A4EDC06BA5C9AEE8BEBDFD360F91E069B49F0B970DC74A02ED956EA6D3E74412F5BDE5766065A6597F67D38598A32548787DE3370DC4BE2B2443B49AE7AAD972B54EE78267EG743D7C07DC964929697DFE58BB338450F94D2074BE47569669C0BB8BA087A0BF5BDA309EE0AB40FE00E3GBE00840094008C0099G73G968210E99047E958A66A3B4BE2598EC831C8E78D43AFA934E988BB3E4A23F1AD85B9351A2C153DFF9BC01658A96D61D876D58C910B07435E47B9C0DEF05A
	403160FC57064D159201A7E5D0FC570771E5G4F0CC171DD94362FBF8FF8F26270095F4E1F0D5D0FDCF2B6FEBFB20635B1995ABF5D3F966F234F52BDFEEFDBDBC0567A2B31E78BB5DCDD20419BD0689CCC7C36172D555B3C9E152F9DFF4F7A103C07607EA1DCBBDE3AA6FC6D3879904DF1BECFFCF18D4E93EB9EB2DECB61FA150285E8A82C93F857C321F9A2575F455A2F8A78E11A7E77A3562ED7B60774A7EAEB5774987A75EB57EC2049967A59FC732148B5757DE7E95F60727A15DE0809645E69D61C9EBAA288
	7B9EE3B127FD72824D3EA272FA980F85AC45A823DF1B887295CE3796F05CB8569F6E00677D53D1569D25613ECEB99C7275AFE568EBF03A312F7AF5F0A250D0C0DFD326C77AFA008C6C2BC333B9CD336F5AC1781A29714D1D0EFBC045E1EDABEF862CD3E68C5CC6F89FAF3335C1B9505EEFC2DCBB4355826EA90D6E67A6244B55F04F1A10AED7EBF3AF19E23739E56678EDEE3559F86CFE4D9CBDF67F45ECBCF6171875E3B7473DE726F56404FE35D9237BC0439D56F01B18CC0ABB24B8D417067F1059C5717BE52A
	90715B19DD5D8C570C73B19D6EB03E3FB653FE83DC062A553EE7443ED95B26385D627DB523D1BC500DE871E8E2B6AABEC9E7F733068B59ED4D23F52A4D69F8B7AF4A0E6795915DEF3AA57EABDE7A5BE1DE0FCFA851F57B24222FDB0E4B10112E490C383BE5243BC3233BD30E16F70F9CADEFDCAE527D37A66F16DC247B48046B058BCF902DCDF95750B3FE9E393BC4DBDB6D0506D27A4ECEC8A477BB65A5D570FFB39DDB79FC25F4AD4ED7DAE78C34C6DAE78B5DF7BBC27A72142F27EF2A36A69BB3203E2B64100E
	974E16903ED694C74DAA1A68DF1C9687AA9A6B766D37F05A345C4150F29DAED7C8472525DF4F0F4F4A500F59837AF121925251C7866F478F86C54BF3E213C1BEED8D4BA73E87A27DD5776D8256FCB9FEFDF0D5880F4D974F1B2F55771B79B3E1CEB7F3B0F312874266CE1540D3BBA83E06B2BDDFB370386370F1FFEB57C64720EEEDF41BFA4420ADBFEE50161DE551F46EB2BD9D7FE6F6876904CEA96DF932D4A539A5AAD9460F83847CE43516C8F52C471AFFEACB3579FFEFB41BC8DE9DD946FB70BA12C73B61BA
	32147552F5448CBDF810FEF5B6CF549ADB790334D74237CAF2EFFA635C42CB36F419C87EDE322CCDF27BDC92C8E13F0B2FA785B6A6CE76832DCA8A59A791B6816FA6463B3D1E186C81152C8B616D944FCB1BFC12DD8612958E495F41A41CA4E7485223E4C51B5FE9E38AD4E2BA3A77A828E5CF34F8F9BDF9D9F55105AA1DB928640D106886BABD97855B19EB64A55A0A583FGE5B669DC250348D28D1B2707FF9D2B12D5E187C1D432141EC328863CB808D12242C4369A423DAA2C9718F64307CF4EFA5C9AB013E5
	F5644FF4325AA47B7977BF9C743C2C78686FDE32426785EF1BG8FB59BE017258135478DF5D25456C8BF3E97D0E507F6B89AAA61B59E29AED5F1EF5AAF85DCAA9D6DA17CF9B40BD4320697534FD9EC921F33E0B14C5DD943BA9936C18E762EE576D2A9951A1435772CFE6F6D838BB24D4434173C6C76CA0E3D129D16B8F5E4056A724B8117576D887ABE6502723B71DD9B4D36F51A7B54598ABA49520C6E88DE1B386A69D5FBA13CC1BF5944A8E965D6E8940E5DAE498F2AE4FF1DCABF376C75EF544A063F257E75
	FB7B5EB8E6A23942C9DAD5C5C11F4A8326B59C76850F3DE0357A6873B347BE36A3E7A1306C95A98E0A8BC3D4DFF4F55AE47F48344B5FACC81EFFA1354BC743431220D8254D23D3F1C3EDAF2B05BA73BAB5BC1F01304775B48AD83082A6E01EG7D3E90D8D392918AAC52BC2A38ECE66565377C7E8873A8442A128D21C660143D1D1BF63635C116E98220A2C17C1B1AF8CAD391C133A6B87A3583BFF9498609D7CB4C1D15156CEF957B3B197E2DE218AA06295A8C3519C3867C97D99E8CD21F5F8E06B51817C5A14D
	048D7AA14867165AD803B8D857A8733306393B32C75D7F4E057BE854290CB5D149C9312FC7E799C57F05471E12531B8DDA955127F7681F0D744FFB0C6CFD76770321F9D8957210C4F5947F21B6EF8B9AF7E3C47B8E49D97D9E7962736CB17748D3E773B1F44A6C486F10F49D255EAD86FC518FF13F6492B37D8EF02CFD2326CE496D0615D747CE0F036EA16D6AD165505EE86FD1AABF00B42210178DBFCA6DEE57717C9FD0CB87882AAFE86A3A9FGG54E2GGD0CB818294G94G88G88G710171B42AAFE86A
	3A9FGG54E2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG749FGGGG
**end of data**/
}
}
