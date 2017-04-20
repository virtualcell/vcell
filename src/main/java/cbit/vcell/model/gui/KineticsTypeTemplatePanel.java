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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
/**
 * Insert the type's description here.
 * Creation date: (6/24/2002 11:34:37 AM)
 * @author: Anuradha Lakshminarayana
 */
@SuppressWarnings("serial")
public class KineticsTypeTemplatePanel extends DocumentEditorSubPanel {
	private ReactionStep reactionStep = null;
	private javax.swing.JComboBox kineticsTypeComboBox = null;
	private JButton jToggleButton = null;
	private ParameterTableModel ivjParameterTableModel = null;
	private ScrollTable ivjScrollPaneTable = null;

	private final static KineticsDescription[] Simple_Reaction_Kinetic_Types = {
		KineticsDescription.MassAction,
		KineticsDescription.General,
		KineticsDescription.GeneralLumped,
		KineticsDescription.HMM_irreversible,
		KineticsDescription.HMM_reversible,
		KineticsDescription.Macroscopic_irreversible,
		KineticsDescription.Microscopic_irreversible
	};
	
	private final static KineticsDescription[] Flux_Reaction_KineticTypes = {
			KineticsDescription.General,
			KineticsDescription.GeneralLumped,
			KineticsDescription.GeneralCurrent,
			KineticsDescription.GeneralCurrentLumped,
			KineticsDescription.GHK,
			KineticsDescription.Nernst,
			KineticsDescription.GeneralPermeability
	};
	
class IvjEventHandler implements ActionListener, ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getKineticsTypeComboBox()) 
				updateKineticChoice((KineticsDescription)getKineticsTypeComboBox().getSelectedItem());
		}
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (bEditable) {
				int[] rows = getScrollPaneTable().getSelectedRows();
				List<Object> selectedObjects = new ArrayList<Object>();
				if (rows != null) {
					for (int i = 0; i < rows.length; i++) {
						Parameter object = getParameterTableModel().getValueAt(rows[i]);
						if (!(object instanceof Kinetics.KineticsProxyParameter)) {
							selectedObjects.add(object);
						}
					}
					if (selectedObjects.size() > 0 || rows.length == 0) {
						setSelectedObjects(selectedObjects.toArray());
					}
				}
			}
		}
	}
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
/**
 * KineticsTypeTemplatePanel constructor comment.
 */
	private boolean bEditable = true;
	
	public KineticsTypeTemplatePanel() {
		this(true);
	}
	
public KineticsTypeTemplatePanel(boolean editable) {
	super();
	this.bEditable = editable;
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 10:10:52 PM)
 */
public void cleanupOnClose() {
	getParameterTableModel().setReactionStep(null);
}


/**
 * Gets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @return The kinetics property value.
 * @see #setKinetics
 */
public Kinetics getKinetics() {
	return reactionStep == null ? null : reactionStep.getKinetics();
}

private ScrollTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new ScrollTable();
			ivjScrollPaneTable.setModel(getParameterTableModel());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}

private ParameterTableModel getParameterTableModel() {
	if (ivjParameterTableModel == null) {
		try {
			ivjParameterTableModel = new ParameterTableModel(getScrollPaneTable(), bEditable);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjParameterTableModel;
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
private void initConnections() throws java.lang.Exception {
	getKineticsTypeComboBox().addActionListener(ivjEventHandler);
	getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("KineticsTypeTemplatePanel");
		setLayout(new java.awt.GridBagLayout());
		setBackground(Color.white);

		int gridy = 0;
		
		if (!bEditable) {
			GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridy;
			gbc.gridwidth = 3;
			gbc.insets = new java.awt.Insets(0, 4, 0, 4);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			JLabel label = new JLabel("<html><u>View only. Edit properties in Physiology</u></html>");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			add(label, gbc);
		}
		
		gridy ++;
		java.awt.GridBagConstraints constraintsKineticTypeTitleLabel = new java.awt.GridBagConstraints();
		constraintsKineticTypeTitleLabel.gridx = 0; constraintsKineticTypeTitleLabel.gridy = gridy;
		constraintsKineticTypeTitleLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsKineticTypeTitleLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsKineticTypeTitleLabel.insets = new java.awt.Insets(0, 4, 4, 4);
		JLabel label = new JLabel("Kinetic type");
		add(label, constraintsKineticTypeTitleLabel);

		java.awt.GridBagConstraints constraintsJComboBox1 = new java.awt.GridBagConstraints();
		constraintsJComboBox1.gridx = 1; constraintsJComboBox1.gridy = gridy;
		constraintsJComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBox1.weightx = 1.0;
		constraintsJComboBox1.insets = new java.awt.Insets(0, 4, 4, 4);
		add(getKineticsTypeComboBox(), constraintsJComboBox1);
		getKineticsTypeComboBox().setEnabled(bEditable);
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
		this.add(getJToggleButton(), gridBagConstraints);
		getJToggleButton().setVisible(bEditable);
		
		gridy ++;
		java.awt.GridBagConstraints constraintsParameterPanel = new java.awt.GridBagConstraints();
		constraintsParameterPanel.gridx = 0; 
		constraintsParameterPanel.gridy = gridy;
		constraintsParameterPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsParameterPanel.weightx = 1.0;
		constraintsParameterPanel.weighty = 1.0;
		constraintsParameterPanel.gridwidth = 3;
		add(getScrollPaneTable().getEnclosingScrollPane(), constraintsParameterPanel);
				
		initConnections();
		initKineticChoices();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		KineticsTypeTemplatePanel aKineticsTypeTemplatePanel;
		aKineticsTypeTemplatePanel = new KineticsTypeTemplatePanel();
		frame.setContentPane(aKineticsTypeTemplatePanel);
		frame.setSize(aKineticsTypeTemplatePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Sets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @param kinetics The new value for the property.
 * @see #getKinetics
 */
public void setReactionStep(ReactionStep newValue) {
	if (reactionStep == newValue) {
		return;
	}
	reactionStep = newValue;
	getParameterTableModel().setReactionStep(newValue);
	getKineticsTypeComboBox().setSelectedItem(getKineticType(getKinetics()));
	updateToggleButtonLabel();	
}

private javax.swing.JComboBox getKineticsTypeComboBox() {
	if (kineticsTypeComboBox == null) {
		try {
			kineticsTypeComboBox = new javax.swing.JComboBox();
			kineticsTypeComboBox.setName("JComboBox1");
			kineticsTypeComboBox.setRenderer(new DefaultListCellRenderer() {
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
								}else if (kineticsDescription.equals(KineticsDescription.Macroscopic_irreversible)){
									setText("Macroscopic (Irreversible) [molecules/("+SQUAREMICRON+" s)]");
								}else if (kineticsDescription.equals(KineticsDescription.Microscopic_irreversible)){
									setText("Microscopic (Irreversible) [molecules/("+SQUAREMICRON+" s)]");
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
	return kineticsTypeComboBox;
}

private void updateKineticChoice(KineticsDescription newKineticChoice) {
	boolean bFoundKineticType = false;
	KineticsDescription[] kineticTypes = reactionStep instanceof SimpleReaction ? Simple_Reaction_Kinetic_Types : Flux_Reaction_KineticTypes;
	for (int i=0;i<kineticTypes.length;i++){
		if (kineticTypes[i].equals(newKineticChoice)){
			bFoundKineticType = true;
			break;
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
	if (!getKineticsTypeComboBox().getSelectedItem().equals(newKineticChoice)) {
		getKineticsTypeComboBox().setSelectedItem(newKineticChoice);
	}
	if (reactionStep != null) {
		try {
			if (getKinetics()==null || !getKinetics().getKineticsDescription().equals(newKineticChoice)){
				reactionStep.setKinetics(newKineticChoice.createKinetics(reactionStep));
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

private KineticsDescription getKineticType(Kinetics kinetics) {
	if (kinetics!=null){
		return kinetics.getKineticsDescription();
	}else{
		return null;
	}
}


private JButton getJToggleButton() {
	if (jToggleButton == null) {
		jToggleButton = new JButton("Convert");
		jToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				ModelUnitSystem modelUnitSystem = getKinetics().getReactionStep().getModel().getUnitSystem();
				if (getKinetics() instanceof DistributedKinetics){
					try {
						reactionStep.setKinetics(LumpedKinetics.toLumpedKinetics((DistributedKinetics)getKinetics()));
					} catch (Exception e2){
						e2.printStackTrace(System.out);
						if (getKinetics().getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(KineticsTypeTemplatePanel.this,"failed to translate into General Current Kinetics ["+modelUnitSystem.getCurrentUnit().getSymbolUnicode()+"]: "+e2.getMessage(), e2);
						}else{
							DialogUtils.showErrorDialog(KineticsTypeTemplatePanel.this,"failed to translate into General Lumped Kinetics ["+modelUnitSystem.getLumpedReactionRateUnit().getSymbolUnicode()+"]: "+e2.getMessage(), e2);
						}
					}
 				}else if (getKinetics() instanceof LumpedKinetics){
					try {
						reactionStep.setKinetics(DistributedKinetics.toDistributedKinetics((LumpedKinetics)getKinetics()));
					} catch (Exception e2){
						e2.printStackTrace(System.out);
						if (getKinetics().getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(KineticsTypeTemplatePanel.this,"failed to translate into General Current Density Kinetics ["+modelUnitSystem.getCurrentDensityUnit().getSymbolUnicode()+"]: "+e2.getMessage(), e2);
						}else{
							if (getKinetics().getReactionStep().getStructure() instanceof Feature){
								DialogUtils.showErrorDialog(KineticsTypeTemplatePanel.this,"failed to translate into General Kinetics ["+modelUnitSystem.getVolumeReactionRateUnit().getSymbolUnicode()+"]: "+e2.getMessage(), e2);
							}else{
								DialogUtils.showErrorDialog(KineticsTypeTemplatePanel.this,"failed to translate into General Kinetics ["+modelUnitSystem.getMembraneReactionRateUnit().getSymbolUnicode()+"]: "+e2.getMessage(), e2);
							}
						}
					}
 				}
			}
		});
	}
	return jToggleButton;
}

private void initKineticChoices() {
	KineticsDescription[] kineticTypes = reactionStep == null || reactionStep instanceof SimpleReaction ? Simple_Reaction_Kinetic_Types : Flux_Reaction_KineticTypes;
	javax.swing.DefaultComboBoxModel model = new DefaultComboBoxModel();
	for (int i=0;i<kineticTypes.length;i++){
		model.addElement(kineticTypes[i]);
	}
	getKineticsTypeComboBox().setModel(model);
	
	return;
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length != 1) {
		return;
	}
	if (selectedObjects[0] instanceof ReactionStep) {
		setReactionStep((ReactionStep) selectedObjects[0]);
	} else if (selectedObjects[0] instanceof ReactionSpec) {
		setReactionStep(((ReactionSpec) selectedObjects[0]).getReactionStep());
	} else if (selectedObjects[0] instanceof KineticsParameter) {
		KineticsParameter kineticsParameter = (KineticsParameter) selectedObjects[0];
		setReactionStep(kineticsParameter.getKinetics().getReactionStep());
		for (int i = 0; i < getParameterTableModel().getRowCount(); i ++) {
			if (kineticsParameter == getParameterTableModel().getValueAt(i)) {
				getScrollPaneTable().setRowSelectionInterval(i, i);
				break;
			}
		}
	} else {
		setReactionStep(null);
	}
}

}
