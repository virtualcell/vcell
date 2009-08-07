package cbit.vcell.model.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.UtilCancelException;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
/**
 * Insert the type's description here.
 * Creation date: (7/20/00 5:01:27 PM)
 * @author: 
 */
public class FluxReaction_Dialog extends JDialog {
	private JPanel ivjJDialogContentPane = null;
	private JLabel ivjJLabel1 = null;
	private JLabel ivjJLabel2 = null;
	private JLabel ivjJLabel3 = null;
	private JTextField fluxReactionNameTextField = null;
	private Model ivjModel1 = null;
	private ReactionCanvas ivjReactionCanvas1 = null;
	private JScrollPane ivjJScrollPane1 = null;
	private Species ivjfluxCarrier1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JComboBox ivjJComboBox1 = null;
	private JLabel ivjJLabel8 = null;
	private KineticsTypeTemplatePanel ivjKineticsTypeTemplatePanel = null;
	private FluxReaction ivjFluxReaction1 = null;
	private boolean ivjConnPtoP1Aligning = false;
	private ReactionElectricalPropertiesPanel ivjReactionElectricalPropertiesPanel1 = null;
	private Kinetics ivjKinetics = null;
	private JButton jToggleButton = null;  //  @jve:decl-index=0:visual-constraint="251,111"
	private JButton closeButton = new JButton("Close");
	
	private KineticsDescription[] kineticTypes = {
		KineticsDescription.General,
		KineticsDescription.GeneralLumped,
		KineticsDescription.GeneralCurrent,
		KineticsDescription.GeneralCurrentLumped,
		KineticsDescription.GHK,
		KineticsDescription.Nernst
	};
	private JTextArea annotationTextArea = null;
	private JComboBox carrierComboBox = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, FocusListener, ItemListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == FluxReaction_Dialog.this.getJComboBox1()) 
				connEtoC3();
			if (e.getSource() == closeButton) {
				FluxReaction_Dialog.this.dispose();
			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == FluxReaction_Dialog.this.getFluxReaction1() && (evt.getPropertyName().equals("fluxCarrier"))) 
				connEtoM7(evt);
			if (evt.getSource() == FluxReaction_Dialog.this.getFluxReaction1() && (evt.getPropertyName().equals("name"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == FluxReaction_Dialog.this.getFluxReactionNameTextField() && (evt.getPropertyName().equals("text"))) 
				connPtoP1SetSource();
			if (evt.getSource() == FluxReaction_Dialog.this.getFluxReaction1() && (evt.getPropertyName().equals("kinetics"))) 
				connEtoM10(evt);
		};
		
		public void focusGained(FocusEvent e) {
			
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				getFluxReaction1().getModel().getVcMetaData().setFreeTextAnnotation(getFluxReaction1(), annotationTextArea.getText());
			} else if (e.getSource() == fluxReactionNameTextField) {
				try {
					getFluxReaction1().setName(fluxReactionNameTextField.getText());
				} catch (PropertyVetoException e1) {
					PopupGenerator.showErrorDialog(FluxReaction_Dialog.this,"Error changing name:\n"+e1.getMessage());
				}
			}
		}
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() == carrierComboBox) {
				changeFluxCarrier((String)carrierComboBox.getSelectedItem());
			}
			
		}
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

private void changeFluxCarrier(String selection) {
	if (selection!=null && (getfluxCarrier1() == null || !selection.equals(getfluxCarrier1().getCommonName()))) {
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
			PopupGenerator.showErrorDialog(this,"Error changing flux carrier:\n"+e.getMessage());
		}
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
private void connEtoM1(FluxReaction value) {
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
 * connEtoM6:  (FluxReaction1.this --> fluxCarrier1.this)
 * @param value cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(FluxReaction value) {
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
private void connEtoM8(FluxReaction value) {
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
				getFluxReaction1().setName(getFluxReactionNameTextField().getText());
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
				getFluxReactionNameTextField().setText(getFluxReaction1().getName());
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
 * Return the fluxCarrier1 property value.
 * @return cbit.vcell.model.Species
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Species getfluxCarrier1() {
	// user code begin {1}
	// user code end
	return ivjfluxCarrier1;
}


/**
 * Return the FluxReaction1 property value.
 * @return cbit.vcell.model.FluxReaction
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private FluxReaction getFluxReaction1() {
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
			ivjJComboBox1.setRenderer(new DefaultListCellRenderer() {

				private final static String MU = "\u03BC";
				private final static String MICROMOLAR = MU+"M";
				private final static String SQUARED = "\u00B2";
				private final static String SQUAREMICRON = MU+"m"+SQUARED;
				private final static String MICRON = MU+"m";

				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					java.awt.Component component = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
					
					if (value instanceof KineticsDescription) {
						KineticsDescription kineticsDescription = (KineticsDescription)value;
						if (kineticsDescription.equals(KineticsDescription.General)){
							setText("General Flux Density ("+MICROMOLAR+"-"+MICRON+"/s)");
						}else if (kineticsDescription.equals(KineticsDescription.GeneralLumped)){
							setText("General Flux (molecules/s)");
						}else if (kineticsDescription.equals(KineticsDescription.GeneralCurrent)){
							setText("General Current Density (pA/"+SQUAREMICRON+")");
						}else if (kineticsDescription.equals(KineticsDescription.GeneralCurrentLumped)){
							setText("General Current (pA)");
						}else if (kineticsDescription.equals(KineticsDescription.GHK)){
							setText("Goldman-Hodgkin-Katz Current Density (pA/"+SQUAREMICRON+") - permeability in "+MICRON+"/s");
						}else if (kineticsDescription.equals(KineticsDescription.Nernst)){
							setText("Nernst Current Density (pA/"+SQUAREMICRON+") - conductance in nS/"+SQUAREMICRON);
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

			// stoichiometry
			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabel1.insets = new java.awt.Insets(4, 10, 4, 4);
			getJDialogContentPane().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 0;
			constraintsJScrollPane1.gridwidth = 2;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 0.5;
			constraintsJScrollPane1.insets = new java.awt.Insets(10, 5, 10, 10);
			getJDialogContentPane().add(getJScrollPane1(), constraintsJScrollPane1);

			// Name
			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 1;
			constraintsJLabel2.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabel2.insets = new java.awt.Insets(4, 10, 4, 4);
			getJDialogContentPane().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
			constraintsJLabel4.gridx = 1; constraintsJLabel4.gridy = 1;
			constraintsJLabel4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabel4.weightx = 1.0;
			constraintsJLabel4.insets = new java.awt.Insets(4, 5, 4, 5);
			getJDialogContentPane().add(getFluxReactionNameTextField(), constraintsJLabel4);

			// Flux carrier
			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 2;
			constraintsJLabel3.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabel3.insets = new java.awt.Insets(4, 10, 4, 4);
			getJDialogContentPane().add(getJLabel3(), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
			constraintsJLabel5.gridx = 1; constraintsJLabel5.gridy = 2;
			constraintsJLabel5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabel5.insets = new java.awt.Insets(4, 5, 4, 5);
			getJDialogContentPane().add(getCarrierComboBox(), constraintsJLabel5);

			// Electrical Properties
			java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = 3;
			gbc.anchor = java.awt.GridBagConstraints.EAST;
			gbc.insets = new java.awt.Insets(4, 10, 4, 4);
			JLabel label1 = new JLabel("<html>&nbsp;&nbsp;Electrical<br>properties</html>");
			label1.setFont(label1.getFont().deriveFont(Font.BOLD));
			getJDialogContentPane().add(label1, gbc);
			
			java.awt.GridBagConstraints constraintsReactionElectricalPropertiesPanel1 = new java.awt.GridBagConstraints();
			constraintsReactionElectricalPropertiesPanel1.gridx = 1; constraintsReactionElectricalPropertiesPanel1.gridy = 3;
			constraintsReactionElectricalPropertiesPanel1.gridwidth = 2;
			constraintsReactionElectricalPropertiesPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsReactionElectricalPropertiesPanel1.weightx = 1.0;
			constraintsReactionElectricalPropertiesPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getReactionElectricalPropertiesPanel1(), constraintsReactionElectricalPropertiesPanel1);

			// Annotation
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = 4;
			gbc.insets = new java.awt.Insets(4, 10, 4, 4);
			gbc.anchor = GridBagConstraints.NORTHEAST;
			JLabel label = new JLabel("Annotatation");
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			getJDialogContentPane().add(label, gbc);
			
			annotationTextArea = new javax.swing.JTextArea();
			annotationTextArea.setLineWrap(true);
			annotationTextArea.setWrapStyleWord(true);
			javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 1; gbc.gridy = 4;
			gbc.gridwidth = 2;
			gbc.weightx = 1.0;
			gbc.weighty = 0.1;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.insets = new java.awt.Insets(5, 5, 5, 10);
			getJDialogContentPane().add(jsp, gbc);

			// Kinetic type
			java.awt.GridBagConstraints constraintsJLabel8 = new java.awt.GridBagConstraints();
			constraintsJLabel8.gridx = 0; constraintsJLabel8.gridy = 5;
			constraintsJLabel8.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabel8.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJLabel8(), constraintsJLabel8);

			java.awt.GridBagConstraints constraintsJComboBox1 = new java.awt.GridBagConstraints();
			constraintsJComboBox1.gridx = 1; constraintsJComboBox1.gridy = 5;
			constraintsJComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBox1.insets = new java.awt.Insets(0, 5, 0, 5);
			getJDialogContentPane().add(getJComboBox1(), constraintsJComboBox1);

			java.awt.GridBagConstraints constraintsToggleButton = new java.awt.GridBagConstraints();
			constraintsToggleButton.gridx = 2; constraintsToggleButton.gridy = 5;
			constraintsToggleButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsToggleButton.insets = new java.awt.Insets(5, 5, 5, 10);
			getJDialogContentPane().add(getToggleButton(), constraintsToggleButton);

			// Kinetics Parameters
			java.awt.GridBagConstraints constraintsKineticsTypeTemplatePanel = new java.awt.GridBagConstraints();
			constraintsKineticsTypeTemplatePanel.gridx = 0; constraintsKineticsTypeTemplatePanel.gridy = 6;
			constraintsKineticsTypeTemplatePanel.gridwidth = 3;
			constraintsKineticsTypeTemplatePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsKineticsTypeTemplatePanel.weightx = 1.0;
			constraintsKineticsTypeTemplatePanel.weighty = 1.0;
			constraintsKineticsTypeTemplatePanel.insets = new java.awt.Insets(4, 10, 4, 10);
			getJDialogContentPane().add(getKineticsTypeTemplatePanel(), constraintsKineticsTypeTemplatePanel);
			
			// close button
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = 7;
			gbc.gridwidth = 3;
			gbc.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(closeButton, gbc);
			
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

private javax.swing.JComboBox getCarrierComboBox() {
	if (carrierComboBox == null) {
		try {
			carrierComboBox = new javax.swing.JComboBox();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return carrierComboBox;
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
 * Return the Model1 property value.
 * @return cbit.vcell.model.Model
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Model getModel1() {
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
	setFluxReaction1(fluxReaction);
	setModel1(model);
	refreshAnnotationTextField();
	refreshNameTextField();
	refreshCarrierComboBox();
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJComboBox1().addActionListener(ivjEventHandler);
	getFluxReactionNameTextField().addPropertyChangeListener(ivjEventHandler);
	closeButton.addActionListener(ivjEventHandler);
	annotationTextArea.addFocusListener(ivjEventHandler);
	fluxReactionNameTextField.addFocusListener(ivjEventHandler);
	carrierComboBox.addItemListener(ivjEventHandler);
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
		setSize(576, 691);
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
		FluxReaction_Dialog aFluxReaction_Dialog;
		aFluxReaction_Dialog = new FluxReaction_Dialog();
		aFluxReaction_Dialog.setModal(true);
		aFluxReaction_Dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aFluxReaction_Dialog.setVisible(true);
		java.awt.Insets insets = aFluxReaction_Dialog.getInsets();
		aFluxReaction_Dialog.setSize(aFluxReaction_Dialog.getWidth() + insets.left + insets.right, aFluxReaction_Dialog.getHeight() + insets.top + insets.bottom);
		aFluxReaction_Dialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}

/**
 * Set the fluxCarrier1 to a new value.
 * @param newValue cbit.vcell.model.Species
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setfluxCarrier1(Species newValue) {
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
private void setFluxReaction1(FluxReaction newValue) {
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
private void setKinetics(Kinetics newValue) {
	if (ivjKinetics != newValue) {
		try {
			ivjKinetics = newValue;
			getJComboBox1().setSelectedItem(this.getKineticType(getKinetics()));
			getReactionElectricalPropertiesPanel1().setKinetics(getKinetics());
			getKineticsTypeTemplatePanel().setKinetics(getKinetics());
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
	if (getFluxReaction1() != null) {
		try {
			if (getKinetics()==null || !getKinetics().getKineticsDescription().equals(newKineticChoice)){
				setKinetics(newKineticChoice.createKinetics(getFluxReaction1()));
			}
		} catch (Exception exc) {
			handleException(exc);
		}
	}
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
						String response = DialogUtils.showInputDialog0(FluxReaction_Dialog.this, "enter compartment size ["+sizeUnits+"]", "1.0");
						double size = Double.parseDouble(response);
						setKinetics(LumpedKinetics.toLumpedKinetics((DistributedKinetics)getKinetics(), size));
					} catch (UtilCancelException e1) {
					} catch (Exception e2){
						if (getKinetics().getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(FluxReaction_Dialog.this,"failed to translate into General Current Kinetics [pA]: "+e2.getMessage());
						}else{
							DialogUtils.showErrorDialog(FluxReaction_Dialog.this,"failed to translate into General Lumped Kinetics [molecules/s]: "+e2.getMessage());
						}
					}
				}else if (getKinetics() instanceof LumpedKinetics){
					try {
						String response = DialogUtils.showInputDialog0(FluxReaction_Dialog.this, "enter compartment size ["+sizeUnits+"]", "1.0");
						double size = Double.parseDouble(response);
						setKinetics(DistributedKinetics.toDistributedKinetics((LumpedKinetics)getKinetics(), size));
					} catch (UtilCancelException e1) {
					} catch (Exception e2){
						if (getKinetics().getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(FluxReaction_Dialog.this,"failed to translate into General Current Density Kinetics [pA/um2]: "+e2.getMessage());
						}else{
							DialogUtils.showErrorDialog(FluxReaction_Dialog.this,"failed to translate into General Kinetics [molecules/um2.s]: "+e2.getMessage());
						}
					}
				}
			}
		});
	}
	return jToggleButton;
}

private void refreshAnnotationTextField() {
	annotationTextArea.setText(getFluxReaction1().getModel().getVcMetaData().getFreeTextAnnotation(getFluxReaction1()));
	annotationTextArea.setCaretPosition(0);
}

private void refreshNameTextField() {
	fluxReactionNameTextField.setText(getFluxReaction1().getName());	
}

private void refreshCarrierComboBox() {
	String speciesNames[] = getModel1().getSpeciesNames();
	DefaultComboBoxModel cbm = new DefaultComboBoxModel();
	for (String name : speciesNames) {
		cbm.addElement(name);
	}
	carrierComboBox.setModel(cbm);
	if (getfluxCarrier1() != null) {
		carrierComboBox.setSelectedItem(getfluxCarrier1().getCommonName());
	} else {
		carrierComboBox.setSelectedIndex(-1);
		carrierComboBox.setToolTipText("Please select flux carrier");
	}
}
}