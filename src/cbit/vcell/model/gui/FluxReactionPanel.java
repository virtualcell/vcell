package cbit.vcell.model.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.UtilCancelException;

import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Model;
/**
 * Insert the type's description here.
 * Creation date: (7/20/00 5:01:27 PM)
 * @author: 
 */
public class FluxReactionPanel extends javax.swing.JPanel {
	private JLabel ivjJLabel1 = null;
	private JLabel ivjJLabel2 = null;
	private JLabel ivjJLabel3 = null;
	private JTextField fluxReactionNameTextField = null;
	private Model ivjModel1 = null;
	private ReactionCanvas ivjReactionCanvas1 = null;
	private JScrollPane ivjJScrollPane1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JLabel ivjJLabel8 = null;
	private KineticsTypeTemplatePanel ivjKineticsTypeTemplatePanel = null;
	private FluxReaction ivjFluxReaction1 = null;
	private boolean ivjConnPtoP1Aligning = false;
	private ReactionElectricalPropertiesPanel ivjReactionElectricalPropertiesPanel1 = null;
	private Kinetics ivjKinetics = null;
	private JButton jToggleButton = null;  //  @jve:decl-index=0:visual-constraint="251,111"
	private JTextArea annotationTextArea = null;

	private class IvjEventHandler implements java.beans.PropertyChangeListener {
		
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == FluxReactionPanel.this.getFluxReaction() && (evt.getPropertyName().equals("name"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == FluxReactionPanel.this.getFluxReactionNameTextField() && (evt.getPropertyName().equals("text"))) 
				connPtoP1SetSource();
			if (evt.getSource() == FluxReactionPanel.this.getFluxReaction() && (evt.getPropertyName().equals("kinetics"))) 
				connEtoM10(evt);
		};
		
/*		public void focusGained(FocusEvent e) {
			
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				getFluxReaction1().getModel().getVcMetaData().setFreeTextAnnotation(getFluxReaction1(), annotationTextArea.getText());
			} else if (e.getSource() == fluxReactionNameTextField) {
				try {
					getFluxReaction1().setName(fluxReactionNameTextField.getText());
				} catch (PropertyVetoException e1) {
					PopupGenerator.showErrorDialog(FluxReactionPanel.this,"Error changing name:\n"+e1.getMessage());
				}
			}
		}*/
	};


public FluxReactionPanel() {
	super();
	initialize();		// only draw a subset of the panel
}

/**
 * connEtoM1:  (FluxReaction1.this --> ReactionCanvas1.init(Lcbit.vcell.model.ReactionStep;)V)
 * @param value cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(FluxReaction value) {
	try {
		getReactionCanvas1().setReactionStep(getFluxReaction());
	} catch (java.lang.Throwable ivjExc) {
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
		if ((getFluxReaction() != null)) {
			setKinetics(getFluxReaction().getKinetics());
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
 * connEtoM8:  (FluxReaction1.this --> Kinetics1.this)
 * @param value cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(FluxReaction value) {
	try {
		// user code begin {1}
		// user code end
		if ((getFluxReaction() != null)) {
			setKinetics(getFluxReaction().getKinetics());
		} else {
			setKinetics(null);
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
			if ((getFluxReaction() != null)) {
				getFluxReaction().setName(getFluxReactionNameTextField().getText());
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
			if ((getFluxReaction() != null)) {
				getFluxReactionNameTextField().setText(getFluxReaction().getName());
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
 * Return the FluxReaction1 property value.
 * @return cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private FluxReaction getFluxReaction() {
	// user code begin {1}
	// user code end
	return ivjFluxReaction1;
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
			ivjJLabel1.setFont(ivjJLabel1.getFont().deriveFont(Font.BOLD));
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
			ivjJLabel2.setFont(ivjJLabel2.getFont().deriveFont(Font.BOLD));
			ivjJLabel2.setText("Name");
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
			ivjJLabel3.setFont(ivjJLabel3.getFont().deriveFont(Font.BOLD));
			ivjJLabel3.setText("Flux carrier");
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
private javax.swing.JTextField getFluxReactionNameTextField() {
	if (fluxReactionNameTextField == null) {
		try {
			fluxReactionNameTextField = new javax.swing.JTextField();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return fluxReactionNameTextField;
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
			ivjJLabel8.setFont(ivjJLabel8.getFont().deriveFont(Font.BOLD));
			ivjJLabel8.setText("Kinetic type");
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
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
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
private Kinetics getKinetics() {
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
			ivjKineticsTypeTemplatePanel = new KineticsTypeTemplatePanel();
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
 * Return the ReactionCanvas1 property value.
 * @return cbit.vcell.model.ReactionCanvas
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReactionCanvas getReactionCanvas1() {
	if (ivjReactionCanvas1 == null) {
		try {
			ivjReactionCanvas1 = new ReactionCanvas();
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
			ivjReactionElectricalPropertiesPanel1 = new ReactionElectricalPropertiesPanel();
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
	setFluxReaction(fluxReaction);
	setModel1(model);
	refreshAnnotationTextField();
	refreshNameTextField();
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getFluxReactionNameTextField().addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName("FluxReactionDialog");
		setLayout(new java.awt.GridBagLayout());

		// stoichiometry
		int gridy = 0;
		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = gridy;
		constraintsJLabel1.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel1.insets = new java.awt.Insets(4, 10, 4, 4);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = gridy;
		constraintsJScrollPane1.gridwidth = 2;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 0.5;
		constraintsJScrollPane1.insets = new java.awt.Insets(10, 5, 10, 10);
		add(getJScrollPane1(), constraintsJScrollPane1);

		// Name
		gridy++;
		java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
		constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = gridy;
		constraintsJLabel2.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel2.insets = new java.awt.Insets(4, 10, 4, 4);
		add(getJLabel2(), constraintsJLabel2);

		java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
		constraintsJLabel4.gridx = 1; constraintsJLabel4.gridy = gridy;
		constraintsJLabel4.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel4.weightx = 1.0;
		constraintsJLabel4.insets = new java.awt.Insets(4, 5, 4, 5);
		add(getFluxReactionNameTextField(), constraintsJLabel4);

		// Flux carrier
		gridy++;
		java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
		constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = gridy;
		constraintsJLabel3.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel3.insets = new java.awt.Insets(4, 10, 4, 4);
		add(getJLabel3(), constraintsJLabel3);

		// Electrical Properties
		gridy++;
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = gridy;
		gbc.anchor = java.awt.GridBagConstraints.EAST;
		gbc.insets = new java.awt.Insets(4, 10, 4, 4);
		JLabel label1 = new JLabel("<html>&nbsp;&nbsp;Electrical<br>properties</html>");
		label1.setFont(label1.getFont().deriveFont(Font.BOLD));
		add(label1, gbc);
			
		java.awt.GridBagConstraints constraintsReactionElectricalPropertiesPanel1 = new java.awt.GridBagConstraints();
		constraintsReactionElectricalPropertiesPanel1.gridx = 1; constraintsReactionElectricalPropertiesPanel1.gridy = gridy;
		constraintsReactionElectricalPropertiesPanel1.gridwidth = 2;
		constraintsReactionElectricalPropertiesPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsReactionElectricalPropertiesPanel1.weightx = 1.0;
		constraintsReactionElectricalPropertiesPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getReactionElectricalPropertiesPanel1(), constraintsReactionElectricalPropertiesPanel1);

		// Annotation
		gridy++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 10, 4, 4);
		gbc.anchor = GridBagConstraints.NORTHEAST;
		JLabel label = new JLabel("Annotation");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		add(label, gbc);
			
		annotationTextArea = new javax.swing.JTextArea();
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setWrapStyleWord(true);
		javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 0.1;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new java.awt.Insets(5, 5, 5, 10);
		add(jsp, gbc);
		
		// Kinetics Parameters
		gridy++;
		java.awt.GridBagConstraints constraintsKineticsTypeTemplatePanel = new java.awt.GridBagConstraints();
		constraintsKineticsTypeTemplatePanel.gridx = 0; constraintsKineticsTypeTemplatePanel.gridy = gridy;
		constraintsKineticsTypeTemplatePanel.gridwidth = 3;
		constraintsKineticsTypeTemplatePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsKineticsTypeTemplatePanel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsKineticsTypeTemplatePanel.weightx = 1.0;
		constraintsKineticsTypeTemplatePanel.weighty = 1.0;
		constraintsKineticsTypeTemplatePanel.insets = new java.awt.Insets(4, 10, 4, 10);
		add(getKineticsTypeTemplatePanel(), constraintsKineticsTypeTemplatePanel);
			
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Set the FluxReaction1 to a new value.
 * @param newValue cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void setFluxReaction(FluxReaction newValue) {
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
private void setKinetics(Kinetics newValue) {
	if (ivjKinetics != newValue) {
		try {
			ivjKinetics = newValue;
			getReactionElectricalPropertiesPanel1().setKinetics(getKinetics());
			getKineticsTypeTemplatePanel().setReactionStep(getFluxReaction());
			updateToggleButtonLabel();
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}

/**
 * Set the Model1 to a new value.
 * @param newValue cbit.vcell.model.Model
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setModel1(Model newValue) {
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

private void updateToggleButtonLabel(){
	final String MU = "\u03BC";
	final String MICROMOLAR = MU+"M";
	final String SQUARED = "\u00B2";
	final String SQUAREMICRON = MU+"m"+SQUARED;
	final String MICRON = MU+"m";
	if (getKinetics() instanceof DistributedKinetics){
		if (getKinetics().getKineticsDescription().isElectrical()){
			getToggleButton().setText("Convert to [pA]");
			getToggleButton().setToolTipText("convert kinetics to be in terms of total membrane current rather than current density");
		}else{
			getToggleButton().setText("Convert to [molecules/s]");
			getToggleButton().setToolTipText("convert kinetics to be in terms of molecules rather than concentration");
		}
	}else if (getKinetics() instanceof LumpedKinetics){
		if (getKinetics().getKineticsDescription().isElectrical()){
			getToggleButton().setText("Convert to [pA/"+SQUAREMICRON+"]");
			getToggleButton().setToolTipText("convert kinetics to be in terms of current density rather than total membrane current");
		}else{
			getToggleButton().setText("Convert to ["+MICROMOLAR+"-"+MICRON+"/s]");
			getToggleButton().setToolTipText("convert kinetics to be in terms of concentration rather than molecules");
		}
	}
}
/**
 * This method initializes jToggleButton	
 * 	
 * @return javax.swing.JButton	
 */
private JButton getToggleButton() {
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
						String response = DialogUtils.showInputDialog0(FluxReactionPanel.this, "enter compartment size ["+sizeUnits+"]", "1.0");
						double size = Double.parseDouble(response);
						setKinetics(LumpedKinetics.toLumpedKinetics((DistributedKinetics)getKinetics(), size));
					} catch (UtilCancelException e1) {
					} catch (Exception e2){
						if (getKinetics().getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(FluxReactionPanel.this,"failed to translate into General Current Kinetics [pA]: "+e2.getMessage(), e2);
						}else{
							DialogUtils.showErrorDialog(FluxReactionPanel.this,"failed to translate into General Lumped Kinetics [molecules/s]: "+e2.getMessage(), e2);
						}
					}
				}else if (getKinetics() instanceof LumpedKinetics){
					try {
						String response = DialogUtils.showInputDialog0(FluxReactionPanel.this, "enter compartment size ["+sizeUnits+"]", "1.0");
						double size = Double.parseDouble(response);
						setKinetics(DistributedKinetics.toDistributedKinetics((LumpedKinetics)getKinetics(), size));
					} catch (UtilCancelException e1) {
					} catch (Exception e2){
						if (getKinetics().getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(FluxReactionPanel.this,"failed to translate into General Current Density Kinetics [pA/um2]: "+e2.getMessage(), e2);
						}else{
							DialogUtils.showErrorDialog(FluxReactionPanel.this,"failed to translate into General Kinetics [molecules/um2.s]: "+e2.getMessage(), e2);
						}
					}
				}
			}
		});
	}
	return jToggleButton;
}

private void refreshAnnotationTextField() {
	annotationTextArea.setText(getFluxReaction().getModel().getVcMetaData().getFreeTextAnnotation(getFluxReaction()));
	annotationTextArea.setCaretPosition(0);
}

private void refreshNameTextField() {
	fluxReactionNameTextField.setText(getFluxReaction().getName());	
}

}