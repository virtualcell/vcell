/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import cbit.vcell.client.desktop.biomodel.annotations.AnnotationsPanel;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Entity;
import org.vcell.relationship.RelationshipEvent;
import org.vcell.relationship.RelationshipListener;
import org.vcell.relationship.RelationshipObject;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.util.BeanUtils;
import org.vcell.util.document.Identifiable;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.GeneralKinetics;
import cbit.vcell.model.GeneralLumpedKinetics;
import cbit.vcell.model.HMM_IRRKinetics;
import cbit.vcell.model.HMM_REVKinetics;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Macroscopic_IRRKinetics;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Microscopic_IRRKinetics;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.gui.ParameterTableModel;
import cbit.vcell.model.gui.ReactionElectricalPropertiesPanel;
import cbit.vcell.parser.Expression;

@SuppressWarnings("serial")
public class ReactionPropertiesPanel extends DocumentEditorSubPanel {
	private ReactionStep reactionStep = null;
	private javax.swing.JComboBox kineticsTypeComboBox = null;
	private JButton jToggleButton = null;
	private ParameterTableModel ivjParameterTableModel = null;
	private ScrollTable ivjScrollPaneTable = null;
	private EventHandler eventHandler = new EventHandler();
	private ReactionElectricalPropertiesPanel reactionElectricalPropertiesPanel;
	private JTextField nameTextField = null;
	private JTextField sbmlNameTextField = null;
	
	private JLabel annotationIconLabel = null;
	private JLabel electricalPropertiesLabel;
	private JCheckBox isReversibleCheckBox;
	private JLabel reversibleLabel;


	// wei's code
	private BioModel bioModel = null;
	private JScrollPane linkedPOScrollPane;
	// done
	
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
	
	private class EventHandler extends MouseAdapter implements RelationshipListener, ActionListener, FocusListener, PropertyChangeListener {
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getKineticsTypeComboBox()) { 
				updateKineticChoice();
			} else if (e.getSource() == nameTextField) {
				changeName();
			} else if(e.getSource() == sbmlNameTextField) {
				changeSbmlName();
			} else if (e.getSource() == isReversibleCheckBox) {
				setReversible(isReversibleCheckBox.isSelected());
			}
		}
		@Override
		public void focusGained(FocusEvent e) {
		}
		@Override
		public void focusLost(FocusEvent e) {
			if (e.getSource() == nameTextField) {
				changeName();
			} else if (e.getSource() == sbmlNameTextField) {
				changeSbmlName();
			}
		}
		@Override
		public void mouseExited(MouseEvent e) {
			super.mouseExited(e);
		}
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == reactionStep) {
				updateInterface();
			}			
		}
		@Override
		public void relationshipChanged(RelationshipEvent event) {
			if(reactionStep != null && event.getRelationshipObject() != null && event.getRelationshipObject().getBioModelEntityObject() == reactionStep) {
				listLinkedPathwayObjects();
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.getSource() == ReactionPropertiesPanel.this.annotationIconLabel) {
				selectionManager.firePropertyChange(SelectionManager.PROPERTY_NAME_SELECTED_PANEL, null, selectionManager.getAnnotationNavigator(reactionStep));
			}
		}
	}
	
public ReactionPropertiesPanel() {
	super();
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 10:10:52 PM)
 */
public void cleanupOnClose() {
	getParameterTableModel().setReactionStep(null);
}

private ScrollTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new ScrollTable();
			ivjScrollPaneTable.setModel(getParameterTableModel());
			ivjScrollPaneTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}

private ParameterTableModel getParameterTableModel() {
	if (ivjParameterTableModel == null) {
		try {
			ivjParameterTableModel = new ParameterTableModel(getScrollPaneTable(), true);
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

private void initConnections() throws java.lang.Exception {
	nameTextField.addFocusListener(eventHandler);
	nameTextField.addActionListener(eventHandler);
	sbmlNameTextField.addActionListener(eventHandler);
	sbmlNameTextField.addFocusListener(eventHandler);
	
	isReversibleCheckBox.addActionListener(eventHandler);
	getKineticsTypeComboBox().addActionListener(eventHandler);
	annotationIconLabel.addMouseListener(eventHandler);
}

private void initialize() {
	try {
		setName("KineticsTypeTemplatePanel");
		setLayout(new java.awt.GridBagLayout());
		
		nameTextField = new JTextField();
		nameTextField.setEditable(false);
		sbmlNameTextField = new JTextField();
		sbmlNameTextField.setEditable(false);
		sbmlNameTextField.setEnabled(false);

		isReversibleCheckBox = new JCheckBox("");
		isReversibleCheckBox.setBackground(Color.white);
//		isReversibleCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
		
		electricalPropertiesLabel = new JLabel("Electrical Properties");
		electricalPropertiesLabel.setVisible(false);
		reactionElectricalPropertiesPanel = new ReactionElectricalPropertiesPanel();
		reactionElectricalPropertiesPanel.setVisible(false);
		annotationIconLabel = new JLabel("");
		annotationIconLabel.setToolTipText("Annotations");
		annotationIconLabel.setVisible(true);
		
		reversibleLabel = new JLabel("Reversible");
		reversibleLabel.setVisible(false);
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(2, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Reaction Name"), gbc);
		
		JPanel jp = new JPanel();
		jp.setLayout(new java.awt.GridBagLayout());
		jp.setBackground(Color.white);
		
		GridBagConstraints gb = new java.awt.GridBagConstraints();
		gb.gridx = 0; 
		gb.gridy = 0;
		gb.insets = new java.awt.Insets(0, 0, 0, 4);
		gb.weightx = 1;
		gb.fill = GridBagConstraints.HORIZONTAL;
		jp.add(nameTextField, gb);
		
		gb = new GridBagConstraints();
		gb.gridx = 1;
		gb.gridy = 0;
		gb.weightx = 0.0;
		gb.insets = new java.awt.Insets(0, 4, 0, 4);
		gb.anchor = GridBagConstraints.LINE_END;
		jp.add(annotationIconLabel, gb);		// actually it's an icon
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(2, 4, 4, 4);
		gbc.weightx = 0.7;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(jp, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 3; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(2, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(new JLabel("Sbml Name"), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 4; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(2, 4, 4, 4);
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.anchor = GridBagConstraints.LINE_START;		
		add(sbmlNameTextField, gbc);
		
		gridy ++;		// -------------------------------- optional row ---------------------
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.anchor = java.awt.GridBagConstraints.LINE_END;
		gbc.insets = new java.awt.Insets(0, 4, 4, 4);		
		add(electricalPropertiesLabel, gbc);
			
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(0, 4, 0, 4);
		add(reactionElectricalPropertiesPanel, gbc);
		
		gridy ++;	// ----------------------------- next row (usually second) -----------------
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		p.setBackground(Color.white);
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		p.add(reversibleLabel, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		p.add(isReversibleCheckBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		add(p, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new java.awt.Insets(0, 4, 4, 4);
		add(new JLabel("Kinetic Type"), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 2; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(0, 4, 4, 4);
		add(getKineticsTypeComboBox(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.REMAINDER;
		gbc.gridwidth = 2;
		gbc.insets = new java.awt.Insets(0, 4, 4, 2);
		add(getJToggleButton(), gbc);

		gridy ++;	// ------------------------------------ the table -------------------
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 6;
		gbc.insets = new java.awt.Insets(0, 4, 0, 4);
		add(getScrollPaneTable().getEnclosingScrollPane(), gbc);
		
//		CollapsiblePanel collapsiblePanel = new CollapsiblePanel("Pathway Links", true);
//		collapsiblePanel.getContentPanel().setLayout(new GridBagLayout());

		JPanel jp1 = new JPanel();
		jp1.setLayout(new GridBagLayout());
		jp1.setBackground(Color.white);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		JLabel pathwayLink = new JLabel("Linked Pathway Object(s): ");
		jp1.add(pathwayLink, gbc);
		
		linkedPOScrollPane = new JScrollPane();
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		jp1.add(linkedPOScrollPane, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
//		gbc.weighty = 0.5;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(jp1, gbc);
				
		setBackground(Color.white);
		
		getKineticsTypeComboBox().setEnabled(false);
		initKineticChoices();
		initConnections();
		
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
		ReactionPropertiesPanel aKineticsTypeTemplatePanel;
		aKineticsTypeTemplatePanel = new ReactionPropertiesPanel();
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

private void setReversible(boolean bReversible) {
	reactionStep.setReversible(bReversible);
	if(reactionStep.getKinetics() instanceof MassActionKinetics) {
		KineticsParameter kp = reactionStep.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KReverse);
		kp.setExpression(new Expression(0.0d));
	}
	getParameterTableModel().refreshData();
}

/**
 * Sets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @param kinetics The new value for the property.
 * @see #getKinetics
 */
private void setReactionStep(ReactionStep newValue) {
	if (reactionStep == newValue) {
		return;
	}
	ReactionStep oldValue = reactionStep;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(eventHandler);
	}
	// commit the changes before switch to another reaction step
	changeName();
	changeSbmlName();
	
	reactionStep = newValue;
	if (newValue != null) {
		newValue.addPropertyChangeListener(eventHandler);
	}
	getParameterTableModel().setReactionStep(reactionStep);
	updateInterface();
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
				private final static String MICRON = MU+"m";

				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					java.awt.Component component = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
					
					if (value instanceof KineticsDescription) {
						KineticsDescription kineticsDescription = (KineticsDescription)value;
						setText(kineticsDescription.getDescription());
						if (reactionStep != null){
							if (reactionStep instanceof SimpleReaction) {
								if (reactionStep.getStructure() instanceof Feature){
									if (kineticsDescription.equals(KineticsDescription.General)){
										setText("General ["+MICROMOLAR+"/s]");
									} else if (kineticsDescription.equals(KineticsDescription.MassAction)){
										setText("Mass Action ["+MICROMOLAR+"/s] (recommended for stochastic application)");
									} else if (kineticsDescription.equals(KineticsDescription.GeneralLumped)){
										setText("General [molecules/s]");
									} else if (kineticsDescription.equals(KineticsDescription.HMM_irreversible)){
										setText("Henri-Michaelis-Menten (Irreversible) ["+MICROMOLAR+"/s]");
									} else if (kineticsDescription.equals(KineticsDescription.HMM_reversible)){
										setText("Henri-Michaelis-Menten (Reversible) ["+MICROMOLAR+"/s]");
									} else{
										setText(kineticsDescription.getDescription());
									}
								}else if (reactionStep.getStructure() instanceof Membrane){
									if (kineticsDescription.equals(KineticsDescription.General)){
										setText("General [molecules/("+SQUAREMICRON+" s)]");
									} else if (kineticsDescription.equals(KineticsDescription.MassAction)){
										setText("Mass Action [molecules/("+SQUAREMICRON+" s)]");
									} else if (kineticsDescription.equals(KineticsDescription.GeneralLumped)){
										setText("General [molecules/s)]");
									} else if (kineticsDescription.equals(KineticsDescription.HMM_irreversible)){
										setText("Henri-Michaelis-Menten (Irreversible) [molecules/("+SQUAREMICRON+" s)]");
									} else if (kineticsDescription.equals(KineticsDescription.HMM_reversible)){
										setText("Henri-Michaelis-Menten (Reversible) [molecules/("+SQUAREMICRON+" s)]");
									} else if (kineticsDescription.equals(KineticsDescription.Macroscopic_irreversible)){
										setText("Macroscopic (Irreversible) [molecules/("+SQUAREMICRON+" s)]");
									}  else if (kineticsDescription.equals(KineticsDescription.Microscopic_irreversible)){
										setText("Microscopic (Irreversible) [molecules/("+SQUAREMICRON+" s)]");
									}
								}
							} else if (reactionStep instanceof FluxReaction) {
								if (kineticsDescription.equals(KineticsDescription.General)){
									setText("General Flux Density ("+MICROMOLAR+"-"+MICRON+"/s)");
								} else if (kineticsDescription.equals(KineticsDescription.GeneralLumped)){
									setText("General Flux (molecules/s)");
								} else if (kineticsDescription.equals(KineticsDescription.GeneralCurrent)){
									setText("General Current Density (pA/"+SQUAREMICRON+")");
								} else if (kineticsDescription.equals(KineticsDescription.GeneralCurrentLumped)){
									setText("General Current (pA)");
								} else if (kineticsDescription.equals(KineticsDescription.GHK)){
									setText("Goldman-Hodgkin-Katz Current Density (pA/"+SQUAREMICRON+") - permeability in "+MICRON+"/s");
								} else if (kineticsDescription.equals(KineticsDescription.Nernst)){
									setText("Nernst Current Density (pA/"+SQUAREMICRON+") - conductance in nS/"+SQUAREMICRON);
								} else if (kineticsDescription.equals(KineticsDescription.GeneralPermeability)){
									setText("General Permeability ("+MICROMOLAR+"-"+MICRON+"/s) - permeability in "+MICRON+"/s");
								}
							}
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

private void updateKineticChoice() {
	KineticsDescription newKineticChoice = (KineticsDescription)getKineticsTypeComboBox().getSelectedItem();
	//
	// if same as current kinetics, don't create new one
	//
	if (reactionStep == null || reactionStep.getKinetics().getKineticsDescription().equals(newKineticChoice)){
		return;
	}
	
	boolean bLumpedToGeneral = false;
	boolean bGeneralToLumped = false;
	if(newKineticChoice.equals(KineticsDescription.General) && reactionStep.getKinetics().getKineticsDescription().equals(KineticsDescription.GeneralLumped)) {
		bLumpedToGeneral = true;
	} else if (newKineticChoice.equals(KineticsDescription.GeneralLumped) && reactionStep.getKinetics().getKineticsDescription().equals(KineticsDescription.General)) {
		bGeneralToLumped = true;
	}
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
	
	try {
		Kinetics newKinetics = null;
		if (bLumpedToGeneral) {
			newKinetics = DistributedKinetics.toDistributedKinetics((LumpedKinetics)reactionStep.getKinetics(), true);
		} else if (bGeneralToLumped) {
			newKinetics = LumpedKinetics.toLumpedKinetics((DistributedKinetics)reactionStep.getKinetics());
		} else {
			newKinetics = newKineticChoice.createKinetics(reactionStep);
		}
		reactionStep.setKinetics(newKinetics);
	} catch (Exception exc) {
		handleException(exc);
	}
}

private void updateToggleButtonLabel(){
	if(reactionStep.getModel() == null){
		return;
	}
	if (reactionStep.getKinetics() instanceof DistributedKinetics){
		getJToggleButton().setText("Convert to ["+reactionStep.getModel().getUnitSystem().getLumpedReactionRateUnit().getSymbolUnicode()+"]");
		getJToggleButton().setToolTipText("convert kinetics to be in terms of molecules rather than concentration");
	}else if (reactionStep.getKinetics() instanceof LumpedKinetics){
		if (reactionStep.getStructure() instanceof Feature){
			getJToggleButton().setText("Convert to ["+reactionStep.getModel().getUnitSystem().getVolumeReactionRateUnit().getSymbolUnicode()+"]");
		}else{
			getJToggleButton().setText("Convert to ["+reactionStep.getModel().getUnitSystem().getMembraneReactionRateUnit().getSymbolUnicode()+"]");
		}
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

private String listLinkedPathwayObjects(){
//	Kinetics kinetics = reactionStep.getKinetics();
	if (reactionStep == null) {
		return "no selected reaction";
	}
	if(bioModel == null || bioModel.getModel() == null){
		return "no biomodel";
	}
	JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	String linkedPOlist = "";
	for(RelationshipObject relObject : bioModel.getRelationshipModel().getRelationshipObjects(reactionStep)){
		if(relObject == null) {
			continue;
		}
		final BioPaxObject bpObject = relObject.getBioPaxObject();
		if(bpObject == null) {
			continue;
		}
		if(bpObject instanceof Entity){
			String name = new String();
			if(((Entity)bpObject).getName().isEmpty()) {
				name = ((Entity)bpObject).getID();
			} else {
				name = ((Entity)bpObject).getName().get(0);
			}
			if(name.contains("#")) {
				name = name.substring(name.indexOf("#")+1);
			}
			JLabel label = new JLabel("<html><u>" + name + "</u></html>");
			label.setForeground(Color.blue);
			label.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						selectionManager.followHyperlink(new ActiveView(null,DocumentEditorTreeFolderClass.PATHWAY_DIAGRAM_NODE, ActiveViewID.pathway_diagram),new Object[]{bpObject});
					}
				}
			});
			panel.add(label);
		}
	}
	Dimension dim = new Dimension(200, 20);
	panel.setMinimumSize(dim);
	panel.setPreferredSize(dim);
	linkedPOScrollPane.setViewportView(panel);
	return linkedPOlist;
}

private JButton getJToggleButton() {
	if (jToggleButton == null) {
		jToggleButton = new JButton("Convert");
		jToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				ModelUnitSystem modelUnitSystem = reactionStep.getModel().getUnitSystem();
				Kinetics kinetics = reactionStep.getKinetics();

				if (kinetics instanceof DistributedKinetics){
					try {
						reactionStep.setKinetics(LumpedKinetics.toLumpedKinetics((DistributedKinetics)kinetics));
					} catch (Exception e2){
						e2.printStackTrace(System.out);
						if (kinetics.getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(ReactionPropertiesPanel.this,"failed to translate into General Current Kinetics ["+modelUnitSystem.getCurrentUnit().getSymbolUnicode()+"]: "+e2.getMessage(), e2);
						}else{
							DialogUtils.showErrorDialog(ReactionPropertiesPanel.this,"failed to translate into General Lumped Kinetics ["+modelUnitSystem.getLumpedReactionRateUnit().getSymbolUnicode()+"]: "+e2.getMessage(), e2);
						}
					}
 				}else if (kinetics instanceof LumpedKinetics){
					try {
						reactionStep.setKinetics(DistributedKinetics.toDistributedKinetics((LumpedKinetics)kinetics, true));
					} catch (Exception e2){
						e2.printStackTrace(System.out);
						if (kinetics.getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(ReactionPropertiesPanel.this,"failed to translate into General Current Density Kinetics ["+modelUnitSystem.getCurrentDensityUnit().getSymbolUnicode()+"]: "+e2.getMessage(), e2);
						}else{
							if (kinetics.getReactionStep().getStructure() instanceof Feature){
								DialogUtils.showErrorDialog(ReactionPropertiesPanel.this,"failed to translate into General Kinetics ["+modelUnitSystem.getVolumeReactionRateUnit().getSymbolUnicode()+"]: "+e2.getMessage(), e2);
							}else{
								DialogUtils.showErrorDialog(ReactionPropertiesPanel.this,"failed to translate into General Kinetics ["+modelUnitSystem.getMembraneReactionRateUnit().getSymbolUnicode()+"]: "+e2.getMessage(), e2);
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
		
		if(!(kineticTypes[i].equals(KineticsDescription.Macroscopic_irreversible) || kineticTypes[i].equals(KineticsDescription.Microscopic_irreversible)))
		{
			model.addElement(kineticTypes[i]);
		}
		else // macroscopic/microscopic irreversible
		{
			// reactions on membrane in a 3D geometry
			if(reactionStep != null && reactionStep.getStructure() != null && reactionStep.getStructure() instanceof Membrane)
			{
				//check if reactants are all on membrane and calculate sum of reactants' stoichiometry
				ReactionParticipant[] rps = reactionStep.getReactionParticipants();
				int order = 0;
				boolean bAllMembraneReactants = true;
				for(ReactionParticipant rp : rps)
				{
					if(rp instanceof Reactant)
					{
						if(! (rp.getStructure() instanceof Membrane))
						{
							bAllMembraneReactants = false;
							break;
						}
						order += rp.getStoichiometry();
					}
				}
				//add only if 2nd order membrane reaction
				if(order == 2 && bAllMembraneReactants && !reactionStep.hasCatalyst())
				{
					model.addElement(kineticTypes[i]);
				}
			}
		}
	}
	getKineticsTypeComboBox().setModel(model);
	
	return;
}

protected void updateInterface() {
	boolean bNonNullReactionStep = reactionStep != null;
	nameTextField.setEditable(bNonNullReactionStep);
	sbmlNameTextField.setEditable(bNonNullReactionStep);
	getParameterTableModel().setEditable(bNonNullReactionStep);
	kineticsTypeComboBox.setEnabled(bNonNullReactionStep);
	BeanUtils.enableComponents(reactionElectricalPropertiesPanel, bNonNullReactionStep);
	jToggleButton.setEnabled(bNonNullReactionStep);
	reversibleLabel.setVisible(true);
	isReversibleCheckBox.setVisible(true);
	if (bNonNullReactionStep) {
		initKineticChoices();
		boolean bMembrane = reactionStep.getStructure() instanceof Membrane;
		electricalPropertiesLabel.setVisible(bMembrane);
		reactionElectricalPropertiesPanel.setVisible(bMembrane);		
		reactionElectricalPropertiesPanel.setKinetics(reactionStep.getKinetics());
		getKineticsTypeComboBox().setSelectedItem(getKineticType(reactionStep.getKinetics()));
		updateToggleButtonLabel();
		nameTextField.setText(reactionStep.getName());
		sbmlNameTextField.setText(reactionStep.getSbmlName());
		sbmlNameTextField.setToolTipText(reactionStep.getSbmlName());
		sbmlNameTextField.setCaretPosition(0);
		final boolean reversible = reactionStep.isReversible();
		if(reactionStep.getKinetics() instanceof HMM_IRRKinetics) {
			isReversibleCheckBox.setSelected(false);	// the flag says true for MM irreversible
			isReversibleCheckBox.setEnabled(false);
		} else if (reactionStep.getKinetics() instanceof HMM_REVKinetics) {
			isReversibleCheckBox.setSelected(true);
			isReversibleCheckBox.setEnabled(false);
		} else if(reactionStep.getKinetics() instanceof Microscopic_IRRKinetics) {
			isReversibleCheckBox.setSelected(false);
			isReversibleCheckBox.setEnabled(false);
		} else if(reactionStep.getKinetics() instanceof Macroscopic_IRRKinetics) {
			isReversibleCheckBox.setSelected(false);
			isReversibleCheckBox.setEnabled(false);
		} else if(reactionStep.getKinetics() instanceof GeneralKinetics || reactionStep.getKinetics() instanceof GeneralLumpedKinetics) {
			reversibleLabel.setVisible(false);
			isReversibleCheckBox.setVisible(false);
		} else {
			isReversibleCheckBox.setSelected(reversible);
			isReversibleCheckBox.setEnabled(true);
		}
		updateAnnotationIconLabel();
	} else {
		nameTextField.setText(null);
		sbmlNameTextField.setText(null);
		sbmlNameTextField.setToolTipText(null);
		electricalPropertiesLabel.setVisible(false);
		reactionElectricalPropertiesPanel.setVisible(false);
		isReversibleCheckBox.setSelected(false);
		isReversibleCheckBox.setEnabled(true);
		annotationIconLabel.setIcon(null);
	}
	listLinkedPathwayObjects();
}

private void updateAnnotationIconLabel() {
	Identifiable identifiable = AnnotationsPanel.getIdentifiable(reactionStep);
	String freeText = bioModel.getVCMetaData().getFreeTextAnnotation(identifiable);
	MiriamManager miriamManager = bioModel.getVCMetaData().getMiriamManager();
	TreeMap<Identifiable, Map<MiriamRefGroup, MIRIAMQualifier>> miriamDescrHeir = miriamManager.getMiriamTreeMap();
	Map<MiriamRefGroup, MIRIAMQualifier> refGroupMap = miriamDescrHeir.get(identifiable);
	Icon icon1 = null;
	Icon icon2 = null;
	Icon icon = null;
	if(freeText != null && !freeText.isEmpty()) {
		icon2 = VCellIcons.noteIcon;
	} 
	if(refGroupMap != null && !refGroupMap.isEmpty()) {
		icon1 = VCellIcons.linkIcon;
	}
	if(icon1 != null && icon2 != null) {
		icon = VCellIcons.addIcon(icon1, icon2);
	} else if(icon1 == null) {
		icon = icon2;			// icon2 mai be also null, no prob
	} else if(icon2 == null) {
		icon = icon1;
	}
	annotationIconLabel.setIcon(icon);
}

private void changeSbmlName() {
	if (reactionStep == null) {
		return;
	}
	String newName = sbmlNameTextField.getText();
	if (newName.equals(reactionStep.getSbmlName())) {
		return;
	}
	try {
		reactionStep.setSbmlName(newName);
		sbmlNameTextField.setToolTipText(newName);
	} catch (PropertyVetoException e) {
		e.printStackTrace();
		DialogUtils.showErrorDialog(ReactionPropertiesPanel.this, e.getMessage());
	}
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if(getScrollPaneTable() != null && getScrollPaneTable().getCellEditor() != null && getScrollPaneTable().isEditing()){
		getScrollPaneTable().getCellEditor().stopCellEditing();		
	}

	if (selectedObjects == null || selectedObjects.length != 1) {
		return;
	}
	if (selectedObjects[0] instanceof ReactionStep) {
		setReactionStep((ReactionStep) selectedObjects[0]);
	} else {
		setReactionStep(null);
	}
}

private void changeName() {
	if (reactionStep == null) {
		return;
	}
	String newName = nameTextField.getText();
	if (newName == null || newName.length() == 0) {
		nameTextField.setText(reactionStep.getName());
		return;
	}
	if (newName.equals(reactionStep.getName())) {
		return;
	}
	try {
		reactionStep.setName(newName);
	} catch (PropertyVetoException e1) {
		e1.printStackTrace();
		DialogUtils.showErrorDialog(this, e1.getMessage());
	}
}

public void setBioModel(BioModel newValue) {
	if (bioModel == newValue) {
		return;
	}
	BioModel oldValue = bioModel;
	if (oldValue != null) {
		oldValue.getRelationshipModel().removeRelationShipListener(eventHandler);
	}
	bioModel = newValue;
	if (newValue != null) {
		newValue.getRelationshipModel().addRelationShipListener(eventHandler);
	}
}

@Override
public void setSelectionManager(SelectionManager selectionManager) {
	super.setSelectionManager(selectionManager);
}


}
