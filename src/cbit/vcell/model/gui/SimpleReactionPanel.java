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
	private final static int KINETIC_GEN_TOTAL_KINETICS = 4;
	private final static String KINETIC_STRINGS[] = {"Mass Action","General", "Henri-Michaelis-Menten (Irreversible)", "Henri-Michaelis-Menten (Reversible)", "General Total Kinetics"};
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
	}else if (kinetics instanceof GeneralTotalKinetics){
		return KINETIC_STRINGS[KINETIC_GEN_TOTAL_KINETICS];
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
		}catch(cbit.vcell.client.task.UserCancelException e){
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
				case KINETIC_GEN_TOTAL_KINETICS:{
					if (getKinetics() instanceof GeneralTotalKinetics){
						return;
					}
					setKinetics(new GeneralTotalKinetics(getTornOffSimpleReaction()));
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
	D0CB838494G88G88GF6FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8946719B4B1B9F3B1CFECEB9A12FABDEBCDCBAFDCEBFAE94F34DECB2E3635975B9A0EDC3D1EDE6D4527312D69512B3DFAADF77D5885DC920DA49245C8A2C9CCA309D9C1C9C4449FFEC40818A0EC9581A34B6E6C6E406C4EBABB8BABC13D777B3EF9E7E6F7E7D92417FB6A634B4C3C5F7B7F3D5F6FFCBB24FC573C62C2DBB9A10545C47BF73010106CA1C2CEEF7B4B8A44553F1ADBCACC7FDE84F8
	1A14DFA902EBBB14ED3AA537340C9CF0BB60F9G1EE75F10DB3A9D6E3712FA771EFE38C92107C0D9499D352E497510F328E7A3FBEE5E36892ECF818C820EDE33DF257A7B36D5E878999A9E2150C6484CBD14A63A2DD24365830D83F02DG3DFBB8FD982E5C001B9A6AB53A018FE711757F2058B6D3B9A619884BB55E755A350A7C26F0EEB5557589E6A765598B4FDEG6A3C1D7C566604EBE357087F28E7C8D41D239EC1121CC159ABC8CEFFD4ECE8689103E1C968965C9ED514C3DD6E10A091D2719A157963ED8E
	A6031055A0EF8143FDB7C0758C403D8AB06BB5445FBFCC71EB611E3C1EDBDA71BA7AF66455C64D765E777F16646D8F96EDC961DB4D6B5C275E578D5F3C432BECBBD8BD9C6F25711E83E50BG16816483146D4DAD2D8158496A7F643957605AB424364841209CEA8A071BA52FF21A7A8A778D8DD022613A850F2CF88961F53CE05BDE8BFBA6834E77056B6663B149BA00751D5D7DB93272EFBEE65BE8911315D96F5B5A7772D88C6EB5E242F57FAC256E0C3D7179982F7B9CB16B1E714EA376E28B5D738EBFE21F23
	4949B269DE3D1756F533C86BFAA95C97EA316078F798FE779B861E67E69E4B997A5C88E5735E405C68FD8AED69AC1ACF4AEA6F340CC3665F4F3295B9398D154EC4DB0AA4AA3B5D49F5566A363C4270771B705C16AF33BC254FD57BF2CB33EBF2CBF9FEF5745136DA3FBF375485A0814CGD8G108310379FF3EC7639FDD34C31AE3722B6C7C549ABA80473F674EE032B90D395F7C7A8223AC39E612CDB915DC31290A19ACDF7AF0D41D09C5FB9167B9E50F1D288890A1BB7D4459D94D4C109F01A415E27A90DA230
	D2179894C0C1B0CCD0EE20F7ABDCA5F7C47DC3586BD68503021707FBE91D0C3A4F02C490G77BCAE237DB4DE6B417FCD87F2CBAB8FE09CFE46641D929468AF9A9ABAE5F9A49AF6D1CF08F4004659361D769DB5F05FFEGE32C8CD05CC2F8DE2A4BF9697CB3A6B92DA26B055C4AE5ECE36DAC27D682FD81C0D59D664E4FFBB1F7DAB77FB5597B489D1639D35E78083D2E0E67CCE7DDE29BF2B15954BE954A6654E11F7F03C02FC9C6D5DDFCFB22F6CC252D2FCCC147F57CB2CE4746841A62A94827E0CF016771DEFD
	01358130FFB9C0FE9D467C4279AA1E434D6E0800D97B2E8289A7F80D9A60F6BEA18DEAF135EBF6ED56649F81B886F08260A4C08740A10043GE7GDAG2A899FD769307C8A402BG97G8E823C8C70BA40C5BDA62FB3DD0EB721CF87688548G4882D88E3081206AAD48A700B0C0A6C08E40DA003203392575G03GB38196822CGA0753925FBG3AGE2G736A310EF306B38A637DB247EF73A4F1B5BF3F2879013ED5EBBED9719EB44587E3771496A7F38C4DBCE7AC6474EBB13CB8459C79D386DE97AF0F503A
	40FAC8EC8B25A9F81F34280BB7CCF5318BE0DF8ADEDAC75B357A24F53DC543BF4F6C21ED0410E793F8F6DA48F9D92B735ABF01D87E31407B061EBE9A8BE1D488292DC2D888F961A622157575513A5046AF1F2C738CB2FC98463B9698E0A2BA5E57C7E369D6D5C59C0A2AC20F202AE248EF1407182EC8F4A8EC8C1528AB5AC7F3C19E0371CE279F6F23E305C78E058456C969ACDA79849B2B84C9880251ADF2B424F27C8D7DF4CEBFA2DC56464A1B7BCB889D6B24A89DF779BCF9211B66D3D89103507551211BD49C
	2263580E6D952698AD057E034E775741159C4231E86CE59C0BF60FFF1664DC8C5B2C66D419775CEE9BBC4447847510B196F11B46862CED3F4246CC8D9F61367EF404662755445D4EF8EAEDB4F7DD3292FA57670BA7A3C423992822F2CF09A1C195BD91BEDE6C38C8ED188336E581649C4271623F86E8B90E515AC051A2CB1200B2C9830D55DDE239666B8A604DBF44E37514DEFEF4984B5B61F93041A867FAFEFC210C6603F0A9AA405408CEA6CE69F5767805E7924A4EEA75474BFFF501561FE41A07ACE7BAA392
	2E41E6E97A96B47071BE872EAF50BABFCC695A03C8D7F958A82F037B5E435C46350C2EBC04F40B8F73F1AA4FC46F84BC573BD4272BEB64651DF09DG18E864FA779C21F49F13F57BG3FGE0C9A33733680836CEE4FA7B42C8279E61F4330EF0394B0E186D2B3B04F4AB0F70F1135AC7752EBF426D63725669F4C5C7391CCA386E8568BA4A65ADBBCA693E22A01D77A89FFF29BCEA5F44D1335E8B91242BB84647681AE306DC276E6F2BAA529D0772DE0058B1AEEF56B1333C55BADD4EB1BE562F5068579EB34BDB
	98C5BAF21C4B29502EB5474D719B9EC5BA57F1BE57130EF3FFA762683EB601F433B53A9CADDF3262687E7C0A1E87470DF224B7683A2EA2DD1ED63E6EB87723A0CE1E639A526DEDB27249AC0F47653BBADDEB931F67E07ED17DCE3D5EEEA0C8B7504467C4EA930F4B0CA6333C06C25D3EA6BEDFDA57E4586954690AECC8D7504467D909FAB95DA21D2E3C194F49A89D153737591CFF0ADD772319178F40D582185D4C6D63FD5DAF2BE97F4B76AF065D236E18B3B286E3C8C3435B2C43637B94B732358625DF500C7B
	911FFF0E2D9B60F9BD4026966C2B9F5E0C7B91F3C63248603BF976D4FBAD3BDB389D6DADC6DF1DF9EE4C4E7BCA4FA055B9934AB2DB301F8C3FE7DE93B5C5A232C7E44BB12D0F5AF5515C17F35C6E0BCF4055A7AA913583865B9876E72FDD247652751A0656562FB9EC4DC27B70EC503B86203215627FDB24E39C6D5B381F1E8368676C71FB48609DF75B1ECC6167E0AB77AF235570F37DA3F5F62ECF9846FD25C5D01E5F0A7BCADF9E5EC5D26CABB5B460302259BCD7C21B771C005C8160E35056E12AAF12B0C071
	327DC16A0F9A90A35C1F7FE8BA2E593B6CF37349220D33ECD67BC8014FE45AB2CFF0BFF2CE987E342EDEE56B856076DC8AEE5272252C8D7AAAGBE56FF53EF9E6BB3G3F88E0A5C0FE9B5ADC40F8954D5A9ED588F37B7E7A8533FD8E77CD16764D7A095F66B8496D2AB9E958177370AADB5E43E85F119046AB86B48BCFE24C4B0F52AB1B37905C4F19B80FFAB3165DCFAA365EE6291735F7CDDFD93B21F7568FD759667D90757EC257BB88B4196D68F32FCC6BA2648E8A0E8E561712F2135C656DFC0D3FDE274FE0
	74AACC94395C2F49986FAE601F894065BC241A6564F4F0B9EBBBD04E6F437B614AA36D5426B90262131520605DC3FB8327FBCCF532392A3309951F20775558F77C0C4D719501DA9EBF8D5AD3FDD60B5F7C2D66FAEB3C761EE57CE6EE89588ABAF85CF63FED442F976ACC526B6D46B046AF8CB4D9EF239FBF1D369F1C7F05CB98FF2E6742A554339AF4D41CC2BD7FF4A9BE6E4FAAC85785B4B3F53A89855B964F7BA7064DF91FF30A570323937A16CE64D92050F559289891695C0A4BDF23E03D56811D5409F91A31
	2F992EFEC1BDC1E7080A68F1CBDD0A9C9694D594A2E73908BD1DC95B5171D5F4383A8FF37C374FE39D6DEEDDC06A570F244C71851D5A5C255328A3D5F853BE93005BFCF384E3D2F9BA3754F59A7DFBBCC2E362D67C351C2ECBC55FE6814D12531833FFBEEC4E590253BCD63B5FC1B9FF2BEEB46ADA4B431D677640B54A765D342E31A5A00B9E88822BFD499D72BBFBA0D0A17F616A33DCFFE9946D2C8759E1DDFEB80AF67267CCC69791E82485EF07AA84B97FBFC7517EAC60DD2D739F9F25F8B1A42A22DB924705
	BD555C462F3E792C060F33B0C2DB8A0EBD9F9DC519956F42BC71DD3D3DCFE0BE71672C89BA4E0DE803G67DDBB017E4C843E9C1DB7F2A51976EF2EA06D86202B694AAD65784BD7919FG5C3CAE14317AAA6A673E3CFBF2B70B095AE38A0BB9566F8E8D83DE0FF56DB5143B82E4161DC1FD3F56716D004B507177920C43A240651FC1BB1EA9443A61F65CF532093783AD242E4BE1619DE84596390FA366616A641450636FA9544751EE98C73BD16FBF9446676E9BB6244B841A655DE8770FF43C23A73774F88F7277
	599347B4CE7FF1BB7A99835A05BA3D2DA83E7FF832445CFF2C6D61EDE20FAB37345105BC2F95196B196F7B4C5C1E788E4365B276CC8326FB843E37FFEAA735EB42457776376A788B8C1FED42F3BF3E48660274F9A514457E007B51B7FB285D65E7A147G5A818681B2G661D45B95D97AE9EA0E9767BBBC2AACC795CD2CF5CC6866E3B8FD27FC7044B5A33B4E89E43F99C3EA952B8ACBEBB35FD6527F43EBC4617F79DFC3CBF2D6EB33FEFACFA7AED9B35512ABFADBA47E5171FB37AD39E3B03826D87DD008F834C
	84188710F38E63760B413D6962066FBE3400F11FC6062777AE6E72B47966CB6978D61F0B0FAF67DB18162F28B71E0F476DB69FDD77D4C2D9BDC0970084B09BA09BE0F9AF466F183BB9DD7C1AF0E34DA56B37AD091BE3E86FD8B0515E358976D97BF9D3921FB3514F7371FC3C6F731E277B22133CF746F13E459D9AF553BDC15E179C0D1B03D71E67FDC9BBDC87G78FA6FFF7C78DEDABD0F6B3DF3ECDD148D4FCBGD61E47B6EE8B50F82F076732BE6813GDA8182GB3G327AB05E9FF69FCE97EFB6A903D8F333
	13824D6355921ADEBE1DC94B37344FAA1FEE9029FF2B212CG203C9F7AB2G97C098E0CEBF7A77E87AFC52DFA9CEA51FFEBDCDBF0B65F4FC597DD67974CBDF0AFD00CE77906E83FC12B8B4BDCB7AF19DFC254D3C5EDF3C65CA4AF5F0012637F240686BE63F761FF6EE77B7126C1EDA7B7947A43E4476B3B0E0656F161E9D567EB6C7D5156D39F21FEDD15C7BD087B067DBC6E8CEE44373D20055G85GD597E0BC86983C00B971565011A966FC8B605AE5F944A29DB4BFEF89CFAF9F6ECC4B17F1410A4FF3E9FA7A
	AE25654B345437D3191E3E6A34FC8BAFD8557F17469A49147ACF2BB516E9EF2CF9987B4A2597302F7CBE6BAB49FB501F82543C076FC4FF1AE46B54F2FCC392DFE20E373FE755F7FD1D6D4D8FC25984409C0005GF9G79G6597B1CFEF99EACA1727263EABFD2E4E0ACC2FAE6FCE4BD7F7510A6F2CBA3D38FA137892635AF951AAF77C52CEE259F7C07C0664E6B926759D8F6A39613D08B930A5BA3D58BC1F16EF2EE5ECFE1844B73558BC1E4417981B7BAD755DB5BABDFD1FC962FBA161F919253E1FCFCCAF1E85
	E97956D86AFB604A7474FDABADDF01253EEBD727276F3634FC6503D6FC872FCDCF5F0934FCFBAD757D77B475BDC9527135DA6A3BAF09EFEA797935A4BEE742730025BE31F0FA7E0D2565D3AD759D30CDCFDFD3DA3ED9167A9E1B263EDF2565DBE0296F137669555F671378927B174503D6FD7D372A6B482473845CD9221BCFAEA198165C2AE01A9F3414609810B7087303DBFDF4FE30491DDB3A9B2091001F697BCB761E4D854F819DB7310362E6417382B70EA1E7F6527D3725703C96008C019C00C600C10019G
	D9GAB06105E43687361394C435FD553775BB59E7EBE103E03216FE969BBD3CE7F1567E61B6200324C31E95474CCD6FFE412F2EA77D31A8FE67C20660B958F376D5F62EC4333C4E6BA3A7E5812C22FD9D62AB2BC2B16F8760F3E47CCF5D60A4F23EE7724BE5B73B10F75591E399E6B33BD73BDCFF3BC48B01FF445723F72CCFE76678B9E2B33BF4FB33F7E096DD3AA5110AA86852616673A9023E73CE036579D8D7129A0559CC95CCFA2B83F340768BEE5178767116D5E7DA4F99EF942AB2A3202C2DC02DBF95767
	539740BDE14103B69F93D2472FCD300EDF27E09D3FEEE172787589296247E37418CA776B134EC2516DE5026F0D7ED9AA55E83AD4392BD617FD27AF359F1F70D97B3140C773ABA2C7950F10E86B3DECED4E4B0CD9B75DF42766F01A7B993F6AD67CC2122FDFE17C3CAC957F929FEDB7C322D7D434D3C7BC8E5F5E360545B19291A2EC315A6AD65D1A7F1F8B227FA7D479F4AD516747A7A82F457C3847234812C4FBC7026B6BD1FD2FC972E07F78341F666782F8DE8C106741FE499E18EC4CD867C9351F7D90DF0FF9
	793EF5210E4FE378CDA63C767ECF44F77295D0B661457C5E94C8DD2FDB8356753AA3103ADE2B82696B35BA103ADEEB82696B75D0A0D51E73BA9C8FBD09B96CD364A0E4B1671BA52676752322352FF70929FD1DA726777533E2EADF970A69FDFDC0CC4CE16E23D3B637D35AC771FC7A34659CF235D77B8D90DC9D02553A3AD126FB7315D0D68F508590G18A9E01E748F27FD27F1A2E679CEC3BB83A11B5AD50BE16FC64BB5E016E663E2A1B97F39BD27C7D25725EB443AAE7BC6D25725FBA4FDDD8E0F242E4BCBA3
	696BF2FCE4723C4D89BFE16EFB6A90FFB7FBF7EF4AE76DBDEE3792C77F96467464246FB516DB44D4FBC7464E6D2C91927B949743678B09FD4AAA3DCFA90032C19F3E37FBB192577EDA307DFDC7CADDE7F912F51D7DC0CADDE7BF12525759CF2554F5768BA9FD1D7DC61A3C4E3E9EC9281B839CFF1BEA4E7783FA3EFFF3523AD962CB2E9BAE2FAD9A5F279D50705BC64DFA8E69744BC763F263CD443FF245BC3E556B7479F17837F47C172E1A717BF5FC681A99FFD0475711F8BB5F5270EB63715083F07C3C42F8FA
	448F5B6271EF623996FBBC7E2006DFE1B75BE3443B3DE432F82F3008B74F795F31754CBADFE2DBF030F1FC03AF31ADFC5807ED21488FFD279F5B42173653F363127B329CD589298FD05A6F68674317815D9A00CD002F8A7077D6FC3EF15F98755327380310EEECC2D9AE327394BB10B7836867G5C8F108730BE00BA8B89BD775C234A22A7A04AC1C1D5AE33B79ED8C74F1C233122B92E21B5FC07C04FBE459F29B128B84D1B3B281F5D6CFC94FF2D201D9139C2671626C38BAED115F4795C27EF546CB0E8129717
	3A9D5BDFFA0669504E9618A6D85A59033617CC65A9B733EB113E40CE5B023E1389F8FEAEE76ECE2BB3E98322FEEE670CD5F9D8AF5FA6D9154F90F14C5C985AEC6EBB34F9B64F4B777D1365E52618AAAFEFE27917AD727C336B785B99FE31894F73F23B9F4FEFAC03321AE1DC03BF9740B5781CE11C0BBEAC925D3E9F186EBDE26FA4367E3140F5CCFC20BA384FFF475E75AD82FF176AF1F8299063102763EEE3F43BC7F2CB9BC730AEDB6554E357A959FA6CBAA3DB2F634E49132F63AE4A29469CEE5F8B6C7DDD97
	58B6B802B65F98C5BFA49D775EB062960F687BAC43C63D76196E9F7DFF2863027F07BADE257B3CC942B8DCACC15C0004F1A8769B3EBFE53A3F53774147E161872A03377BDFED4771A89BFCDEA36138D2BBC85F1F696BD8FA0A34216124A2FA1B5D7ECEB6CA71B56C118A0746DF9944B56CC885156902E789E0C6905F1747FE0F6F4B33BFBD1F38CED91F0FDF6D3AE21B9B544ED9870D77653161D5B62E73019D2833G4A6BC29477588EF39FCB7B317B0A78BEB5AF5F711C391C369BFA0644A8BF95D7CE6D7D385D
	DCFEA52E3C58444FFDFB6895742D602105C47A620C145FD91888F11F66E95759EB6EAAF4FDE65C4EFD7B471DF84EE0C5884F24BCF8066268B8DFA6A36E340438F6997377A3FE4C5F8C997377D6BF5D27DA2EBF5F411E69EF0F34F7EB7E449C23347F37F97CD6120ED43FAD1E8E7079CEEDG67BB6B40FE9206F99240F10041B04EF772881EE14E88536FF3FCE114565C26CB147FD0E12B463FA4ECBD37B6438A20D987702C168B6412B14F534EB493765B8BD9917C0A9C8DF9E19E4674ACE6E3068B6E87AF21DDEF
	C6636D7A4918E1575DD15D2ECB49F695A75A85B494502EF5A63B383C68B04A5BE0A12F10983F132572B6A990C7054B234FF5CA223C753A3C630AF55CB61A6CF381CDC0B16C1BC812B7DA0272E6DA4823F425A619F301E6214247DE7A3C44A40F675EEF134E01CEADE7CBAB264757FC5DFCFCDEF6E4876DB317AB096B08B606DF29A42EA3AED5E03F3D964ABAA3F8BEE7D90C360305703C8CE08DC081C0251ADBDA2F629C65D7F28DC933C76312431DB00911CC538E6E5FD7F7DC2FFF3CFFFCF0A3760F77FF792F48
	221BDBD31E156AD4F93DCB5AF54F23F79552AB776F62B875EFAE14AD82D889309E409105BC0D22FFC7425A79D03A7D5E242AEECF007DAACA5F4C9705B183CD97CD5C3795BB4D3E55C5355FE8E95701E8F2BEEAEB75B3E63E30C6B7C33BE6EA57EC3866E8771C2FBB6E1CE71ED636D63B96E85732515C525D237C1E37114F7A310D540FDA37B9EC47743E93E886C70DB62C0E9A3944E9449865CBBCB4494719F381BC8316B10AE36D6D01443176E744BCD6BE98C89CEB8B634AFF99C89CEB3FE7B3173FA2A60E3548
	4FF369FB7D66B346E5F7CBB674A7B1960B3478AC99B566915C2F61C274EB83146DA27CDD1A03603F7CE349B808816B1FBFB5C1F4DBFE3489733A725ECF11566F375ADECEE1CB4D1876DB38B1431672E0151D4B74DCA384470E7B58EF17749F6F3448122CB807A437E7046B1CB7E6763F7E099BACFF934E7C57F4AD532E854F5E2D797F10FE362FA8E665BF7A7AA83B67BC1F9EC61EC6CB1E49E2D6E2B3643CB2221FAB3CEEB955F6C34EE7CAD0CE761465649F63F2BA69100B67EAF798771D267B310A8FFE0EBF7B
	8355417B43C7E2295724ABE356EB52FF0BD92FC95746A6FF771A9FCB7D5D05DF474C5FDD607DF9E5B14D75B5D0FF64B274F3G75G1D1731AF3F69B25D4361A752937B7193DEBF5D4F25DFBBD275EF910D2776D59ECF7F0D2458B81D378D492AAA8729FC44DF9D2775254AE10664389B47E90C94519FD0B929063FE51C661BA478981A4F4D7F1D4D6B8360D76CB24E57E7ECE473F5F81E2B63FE32916770D9BAEE5599645D2063727BA8EE53B80C6D63087BC3BF726E51F1272ED2DC8C1EB3F55CA798AE9B1E176B
	B8D1C29DEBF45C375F20388D706CF89FF1CEA76AA857F15D459467056799BA6E9E064B0467456F631AE51E5EFFACFF7FFA5B6B67CB0CF6B99077AE54B181B11818FC4D42F3ED0032633B49776937DC4E3A07AD5F543F8DFDAF7FA1174B6D57754AF16712EAC0E76B14747E19030E573DC02B82E482AC84D8823092A09F20688A44882086209DE0GA08CB083E08EC0964062AB58A656CED427329D105AA21DB58C45AD5226215971F8A90D6BF21033CE1715F56D75C9E4E9BB2517B916FD95C30B45723877F11B2E
	427C6B6A643160FC77463D5728831E76A9713DB6E2669BG1E70147832637675E7GCFE69ABE6D377345297B11570B5377A3F5AC7F85E87FF47FDAFB9FDDCC13D87679A2025EDF9DADAE53F19D11AEB9A252B9981F8714D452C14AA707D43EF62C2CE46D966CDFF495570E9F5F983FF66C9E20B94E6709440173443596BE1B572B948A000642CE8DDFF64D18A7F27DA55DD4FFA56077DCC37D9B7B6375BFF04150AF75237E5EEB567A4DEBD789E8A8D07DEC3EF9ADF1CDBDF415768D81D99147A1A6EE29C9927DA1
	BAA2E876CD30FE0D764933F47B3EF8F5DB9C0F8BAC45A823DF4BF53EC54047F19FE17D619AF84E57E52DFEA35E57156F993E1EF0222FD599563E1A5741F5C0C381FDED4FC874D5AC1E5C579B0B51E6D7865A3738B83D2F5F5779B2B2F08F681E38B6198D789C8B9F4C407B7888DB9B64816D03B6444598EE9D609E5369EEAEC43AC29DF7EBA152956AED6E630E54EDEE3EA3FD1B3B57E1BDF65F67C89E3B3F64309E3B97BB4CE3B747BD6820F56407FE35D7277B260EBB2F639EE2B2A96E1C68D583BA7EE1E69745
	378BD4A1623F4F6C9AE338DE1C0F19F067717D3503769B60B2D42D7EBDA3876B9BC4C9525EDFB39AB1845D089E0FFFE7B60AE137496E75BA4E307BBFD8DD6BF3BA5E4DEBE57FD5DACECC3F69F673DF71525F8E73FAFCAC1ADC376B22663A65383F9FC63A9EBB623EB50CF4DF54691AC61265350DA44B7BFA89522D5265ADADC13A03B6DCAF4C73903DCD45B652B3FEA1E12CCADFDB35C1C35973D293A41274525E2ADA783F1F0E2DFC3ED26B40790A63C3132D11EA7C467DAC515037266F7A747DC5BF6ABBBE293E
	4F8F9BBABEFA350A703522F6544C59C37F6234B8626C6EB8596E62342749D4E8390EABC1C3475F3DF1FDFE0CBA510F5824FE4C8B99BA6EAE1E3A9FBF1F92AD4F0945434979746030B91F789E087B7726EF9730664B7115314D869E1BAF6EEBBB4C7D66C270F5490726B2A7195FEF66DB81BC6B2644773E534CC7EE00B9788DD36927CFBB12E3506DC8EED38F74A753FD35BF196EA34EE43ABB1DE6BA7E4D6C0363C16814329FA745AAA92CD2E17E400E83C49432C5CF248E56E32D9EBD50E0FF74D3F69BA96AA0A5
	3C876FA0C5349B6EA0452C176EA0F66841E3742B33C5DA2D315D8B1076D47CD6C961BD5BEFAC3B7DC054C69CED244467164281B7C8E13F0B6FA4CF8EB1F1028234AAA9E31FC458873C7B986F61CEE273C4D4325340FBA81E1776045D9E81C8CA3DEEE504C938C42E1262FED26AD37CC3CC01CAEC7DED7D2016BD51620D1D6489B5C0972AF46620126D0668AEBABD578A8EB357486FG426CDF04B25BCC2E52C124D8479619610F472AE4F35CC190159453F3888D00578EE2D429B011ED00F0EF0E6B853E7149B7FF
	3C6CDF4FC66C2424036C2013559EC1615F7F705273325AC7FF5BC8E9D886EFFBG8FB59BE1176208FAD902BAA977F5530F6FC5D4412B9F0E06CAF816C7AA2A0A523EF6F7A42052519E42DFC433C8A54F70E27AB90BFD5A67ACD88C8B1B0E33CE06ED10033DDB193DD40AD3173275ABDBDEF9716DB50B6C4456C61E10E437374D6D01A5CE87A9D5830A90894812B796BE4685B94670DD9B4D361D3A7B54D9A71DE469C60F447EE25666351B5BA03CB1056CE31434F21DBA0537A560D6C01520F42874F34B3232DBAF
	7B508B6A6F3FF3F27B000D94EACE522AAA0F05D51A46C1D102DAA9E905584AFED609957048E78AFA13E25F76E883FF549FBC7E6D797F52183F8E0435E9498FA61599D46FC403C302F2F3769D4FBFB9E7752D5462B0F597A51742CCAB94219F928261D589BEE311EE3A73235F2E3963333F69E3269B2CAA59E5E43BDF10037B1AFCBEC8A7DDG549808FFDE97CFE91C89B44F44BE7C6C5B0F7CEE88B22C155803B5B56CEFAD7B3B1F7E2DE518DA06295D8F5516CFA67D17D89EC31FD9DA3207DDB407E2742EB29657
	E01E50F8E8A66CB28FC1E1496DE38D628F9D5D82BFEBD8583C644F9EFC695613B6CD54B3C949C9315B12330C221F536B1ABEBDCF7F3CC87F3C4418F6333F2FDE3727142BBA41439A43C31254D17C095AFCB0E65D0D917DBBA4574CFB649BEE72245CA36F3A050F215EDB923FC372A4DB974E837C62DB74734DD768F700D36D9B75845D1284AB2F112610176EA1B5DFD685E3EF346CAA159FC19A2D1017EDE767A707CDBC7F8BD0CB878824F0E1B83C9FGG54E2GGD0CB818294G94G88G88GF6FBB0B624F0
	E1B83C9FGG54E2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG769FGGGG
**end of data**/
}
}