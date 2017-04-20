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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.vcell.util.Compare;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Membrane.MembraneVoltage;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ElectricalTopology;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Structure.StructureSize;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 2:07:01 PM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class StructurePropertiesPanel extends DocumentEditorSubPanel {
	private Structure structure = null;
	private EventHandler eventHandler = new EventHandler();
	private JTextArea annotationTextArea;
	private JTextField nameTextField = null;
	private Model fieldModel = null;
	private JTextField sizeTextField;
	private JLabel voltageLabel;
	private JTextField voltageTextField;
	
	// for electrophysiology
	private JLabel electrophysiologyLabel;
	private JLabel electrophysiologyExplanationLabel;
	private JLabel positiveFeatureLabel;
	private JComboBox<String> positiveFeatureComboBox;
	private JLabel negativeFeatureLabel;
	private JComboBox<String> negativeFeatureComboBox;
	
	private class EventHandler extends MouseAdapter implements ActionListener, FocusListener, PropertyChangeListener {
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				changeAnnotation();
			} else if (e.getSource() == nameTextField) {
				changeName();
			} else if (e.getSource() == positiveFeatureComboBox) {
				changePositiveFeature();
			} else if (e.getSource() == negativeFeatureComboBox) {
				changeNegativeFeature();
			}
		}
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mouseExited(e);
			if(e.getSource() == annotationTextArea){
				changeAnnotation();
			}
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == structure || evt.getSource() == structure.getStructureSize()
					|| structure instanceof Membrane && evt.getSource() == ((Membrane)structure).getMembraneVoltage()) {
				updateInterface();
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == nameTextField) {
				changeName();
			}
			if (e.getSource() == positiveFeatureComboBox) {
				changePositiveFeature();
			}		
			if (e.getSource() == negativeFeatureComboBox) {
				changeNegativeFeature();
			}		
		}
	}

/**
 * EditSpeciesDialog constructor comment.
 */
public StructurePropertiesPanel() {
	super();
	initialize();
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
	annotationTextArea.addFocusListener(eventHandler);
	annotationTextArea.addMouseListener(eventHandler);
	nameTextField.addFocusListener(eventHandler);
	positiveFeatureComboBox.addFocusListener(eventHandler);
	positiveFeatureComboBox.addActionListener(eventHandler);
	negativeFeatureComboBox.addFocusListener(eventHandler);
	negativeFeatureComboBox.addActionListener(eventHandler);
}

private String getExplanationText(){
	if (structure instanceof Membrane){
		Membrane membrane = (Membrane)structure;
		String voltageName = membrane.getMembraneVoltage().getName();
		Feature posFeature = fieldModel.getElectricalTopology().getPositiveFeature(membrane);
		Feature negFeature = fieldModel.getElectricalTopology().getNegativeFeature(membrane);
		String posCompName = (posFeature!=null)?posFeature.getName():"inside (+) compartment";
		String negCompName = (negFeature!=null)?negFeature.getName():"outside (-) compartment";
		return "<html><b>membrane voltage</i>:</b> <font color=\"blue\">\""+voltageName+"\"</font> = voltage(<font color=\"blue\">"+posCompName+"</font>) - voltage(<font color=\"blue\">"+negCompName+"</font>)</i><br/>"
		+ "<b>inward currents:</b> from compartment <font color=\"blue\">\""+negCompName+"\"</font> into compartment <font color=\"blue\">\""+posCompName+"\"</font>"
		+ "<font color=\"red\"><i><br/>Note: VCell reactions and fluxes specify inward currents (- to +) rather than conventional currents (+ to -).</i></font>"
		+ "</html>";
	}else{
		return "";
	}
	
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName(this.getClass().getName());
		setLayout(new GridBagLayout());
		
		nameTextField = new JTextField();
		nameTextField.setEditable(false);
		nameTextField.addActionListener(eventHandler);
		sizeTextField = new JTextField();
		sizeTextField.setEditable(false);
		
		voltageLabel = new JLabel("Voltage Variable Name");
		voltageTextField = new JTextField();
		voltageTextField.setEditable(false);

		electrophysiologyLabel = new JLabel("<html><u>Electrophysiology</u></html>");
		electrophysiologyExplanationLabel = new JLabel();
		positiveFeatureLabel = new JLabel("Positive (inside feature)");
		positiveFeatureComboBox = new JComboBox<String>();

		negativeFeatureLabel = new JLabel("Negative (outside feature)");
		negativeFeatureComboBox = new JComboBox<String>();

		annotationTextArea = new javax.swing.JTextArea("", 1, 30);
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setWrapStyleWord(true);
		annotationTextArea.setFont(new Font("monospaced", Font.PLAIN, 11));
		annotationTextArea.setEditable(false);

		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.insets = new java.awt.Insets(0, 4, 0, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JLabel label = new JLabel("<html><u>Select only one structure to edit properties</u></html>");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		add(label, gbc);
		
		// name
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("Structure Name");
		add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(nameTextField, gbc);
		
		// size
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("Size Variable Name");
		add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(sizeTextField, gbc);
		
		// electrophysiology
		gbc = new java.awt.GridBagConstraints();
		gridy++;
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.insets = new java.awt.Insets(0, 4, 0, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		electrophysiologyLabel.setHorizontalAlignment(SwingConstants.CENTER);
		electrophysiologyLabel.setFont(label.getFont().deriveFont(Font.BOLD));
		add(electrophysiologyLabel, gbc);

		// voltage
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(voltageLabel, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		voltageTextField.setForeground(Color.blue);
		add(voltageTextField, gbc);
		
		// positive (feature) voltage
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(positiveFeatureLabel, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;	
		positiveFeatureComboBox.setForeground(Color.blue);
		add(positiveFeatureComboBox, gbc);
		
		// negative (feature) voltage
		gbc = new java.awt.GridBagConstraints();
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(negativeFeatureLabel, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;
		negativeFeatureComboBox.setForeground(Color.blue);
		add(negativeFeatureComboBox, gbc);
		
		// electrophysiology
		gbc = new java.awt.GridBagConstraints();
		gridy++;
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.insets = new java.awt.Insets(0, 4, 0, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		electrophysiologyExplanationLabel.setHorizontalAlignment(SwingConstants.CENTER);
		//electrophysiologyExplanationLabel.setFont(label.getFont().deriveFont(Font.BOLD));
		add(electrophysiologyExplanationLabel, gbc);

		// annotation
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(9, 8, 4, 6);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("Annotation "), gbc);

		javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 1;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		add(jsp, gbc);
		
		
		DefaultListCellRenderer comboBoxListCellRenderer = new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
				if (value instanceof Feature) {
					setText(((Structure) value).getName());
				}
				return this;
			}
		};
	
		positiveFeatureComboBox.setRenderer(comboBoxListCellRenderer);
		negativeFeatureComboBox.setRenderer(comboBoxListCellRenderer);
			
		setBackground(Color.white);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void changeAnnotation() {
	try{
		if (structure == null || fieldModel == null) {
			return;
		}
		VCMetaData vcMetaData = fieldModel.getVcMetaData();
		String textAreaStr = (annotationTextArea.getText() == null || annotationTextArea.getText().length()==0?null:annotationTextArea.getText());
		if(!Compare.isEqualOrNull(vcMetaData.getFreeTextAnnotation(structure),textAreaStr)){
			vcMetaData.setFreeTextAnnotation(structure, textAreaStr);	
		}
	} catch(Exception e){
		e.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, e.getMessage(), e);
	}
}

public void setModel(Model model) {
	fieldModel = model;

	DefaultComboBoxModel<String> dataModelPos = new DefaultComboBoxModel<String>();
	DefaultComboBoxModel<String> dataModelNeg = new DefaultComboBoxModel<String>();
	dataModelPos.addElement("");
	dataModelNeg.addElement("");
	for (Structure s : model.getStructures()) {
		if (s instanceof Feature) {
			dataModelPos.addElement(s.getName());
			dataModelNeg.addElement(s.getName());
		}
	}
	// fill the comboBoxes with feature names from the model.
	positiveFeatureComboBox.setModel(dataModelPos);
	// if selected structure is a membrane, if it has +ve/-ve feature set, set the comboBox with that selection. 
	if (structure instanceof Membrane) {
		Membrane membrane = (Membrane)structure;
		if (fieldModel.getElectricalTopology().getPositiveFeature(membrane) != null) {
			positiveFeatureComboBox.setSelectedItem(fieldModel.getElectricalTopology().getPositiveFeature(membrane).getName());
		}
	}
	negativeFeatureComboBox.setModel(dataModelNeg);
	if (structure instanceof Membrane) {
		Membrane membrane = (Membrane)structure;
		if (fieldModel.getElectricalTopology().getNegativeFeature(membrane) != null) {
			negativeFeatureComboBox.setSelectedItem(fieldModel.getElectricalTopology().getNegativeFeature(membrane).getName());
		}
	}

}

/**
 * Sets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @param speciesContext The new value for the property.
 * @see #getSpeciesContext
 */
void setStructure(Structure newValue) {
	if (newValue == structure) {
		return;
	}
	Structure oldValue = structure;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(eventHandler);
		oldValue.getStructureSize().removePropertyChangeListener(eventHandler);
		if (oldValue instanceof Membrane) {
			((Membrane) oldValue).getMembraneVoltage().removePropertyChangeListener(eventHandler);
		}
	}
	// commit the changes before switch to another structure
	changeName();
	changeAnnotation();
	structure = newValue;
	if (newValue != null) {
		newValue.addPropertyChangeListener(eventHandler);
		newValue.getStructureSize().addPropertyChangeListener(eventHandler);
		if (newValue instanceof Membrane) {
			((Membrane) newValue).getMembraneVoltage().addPropertyChangeListener(eventHandler);
		}		
	}
	updateInterface();
}

/**
 * Comment
 */
private void updateInterface() {
	boolean bNonNullStructure = structure != null && fieldModel != null;
	nameTextField.setEditable(bNonNullStructure);
	annotationTextArea.setEditable(bNonNullStructure);
	boolean bMembrane = bNonNullStructure && structure instanceof Membrane;
	voltageLabel.setVisible(bMembrane);
	voltageTextField.setVisible(bMembrane);

	electrophysiologyLabel.setVisible(bMembrane);
	positiveFeatureLabel.setVisible(bMembrane);
	positiveFeatureComboBox.setVisible(bMembrane);
	negativeFeatureLabel.setVisible(bMembrane);
	negativeFeatureComboBox.setVisible(bMembrane);
	electrophysiologyExplanationLabel.setVisible(bMembrane);

	if (bNonNullStructure) {
		nameTextField.setText(structure.getName());
		annotationTextArea.setText(fieldModel.getVcMetaData().getFreeTextAnnotation(structure));
		StructureSize structureSize = structure.getStructureSize();
		sizeTextField.setText(structureSize.getName() + " [" + structureSize.getUnitDefinition().getSymbolUnicode() + "]");
		if (bMembrane) {
			Membrane membrane = (Membrane)structure;
			MembraneVoltage memVoltage = membrane.getMembraneVoltage();
			voltageTextField.setText(memVoltage.getName() + " [" + memVoltage.getUnitDefinition().getSymbolUnicode() + "]");
			// if membrane has +ve/-ve feature set, set the comboBox with that selection. 
			ElectricalTopology electricalTopology = fieldModel.getElectricalTopology();
			Feature positiveFeature = electricalTopology.getPositiveFeature(membrane);
			if (positiveFeature != null) {
				positiveFeatureComboBox.setSelectedItem(positiveFeature.getName());
			}
			Feature negativeFeature = electricalTopology.getNegativeFeature(membrane);
			if (negativeFeature != null) {
				negativeFeatureComboBox.setSelectedItem(negativeFeature.getName());
			}
			this.electrophysiologyExplanationLabel.setText(getExplanationText());
		}
	} else {
		annotationTextArea.setText(null);
		nameTextField.setText(null);
		sizeTextField.setText(null);
		voltageTextField.setText(null);
	}
}

private void changeName() {
	if (structure == null) {
		return;
	}
	String newName = nameTextField.getText();
	if (newName == null || newName.length() == 0) {
		nameTextField.setText(structure.getName());
		return;
	}
	if (newName.equals(structure.getName())) {
		return;
	}
	try {
		structure.setName(newName,true);
	} catch (PropertyVetoException e1) {
		e1.printStackTrace();
		DialogUtils.showErrorDialog(StructurePropertiesPanel.this, e1.getMessage());
	}
}

private void changePositiveFeature() {
	if (structure == null) {
		return;
	}
	String positiveFeatureStr = (String)positiveFeatureComboBox.getSelectedItem();
	if (positiveFeatureStr == null || positiveFeatureStr.length() == 0) {
		return;
	}
	if (structure instanceof Membrane) {
		fieldModel.getElectricalTopology().setPositiveFeature((Membrane)structure, (Feature)fieldModel.getStructure(positiveFeatureStr));
		updateInterface();
	}
}

private void changeNegativeFeature() {
	if (structure == null) {
		return;
	}
	String negativeFeatureStr = (String)negativeFeatureComboBox.getSelectedItem();
	if (negativeFeatureStr == null || negativeFeatureStr.length() == 0) {
		return;
	}
	if (structure instanceof Membrane) {
		fieldModel.getElectricalTopology().setNegativeFeature((Membrane)structure, (Feature)fieldModel.getStructure(negativeFeatureStr));
		updateInterface();
	}
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length != 1) {
		return;
	}
	if (selectedObjects[0] instanceof Structure) {
		setStructure((Structure) selectedObjects[0]);
	} else {
		setStructure(null);
	}
	
}
}
