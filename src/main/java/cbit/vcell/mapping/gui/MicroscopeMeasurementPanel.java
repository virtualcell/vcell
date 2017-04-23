/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.VCellLookAndFeel;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.DataSymbol.DataSymbolType;
import cbit.vcell.mapping.MicroscopeMeasurement;
import cbit.vcell.mapping.MicroscopeMeasurement.ConvolutionKernel;
import cbit.vcell.mapping.MicroscopeMeasurement.ExperimentalPSF;
import cbit.vcell.mapping.MicroscopeMeasurement.GaussianConvolutionKernel;
import cbit.vcell.mapping.MicroscopeMeasurement.ProjectionZKernel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;

@SuppressWarnings("serial")
public class MicroscopeMeasurementPanel extends javax.swing.JPanel {
	
	private static final Color ItemBackgroundColor = new Color(0xdee3e9);
	private JTextField nameTextField;
	private JRadioButton rdbtnZprojection = null;
	
	private JPanel gaussianPsfPanel;
	private JRadioButton radioButtonGaussian = null;
	private JTextField sigmaXYTextField;
	private JTextField sigmaZTextField;

	private JPanel experimentalPsfPanel;
	private JRadioButton rdbtnExperimental = null;
	private JComboBox pointSpreadFunctionsComboBox = null;
	private JButton importPsfButton;
	
	
	private SimulationContext simulationContext = null;
	private DefaultComboBoxModel pointSpreadFunctionsComboModel = new DefaultComboBoxModel();
	private DefaultListModel allSpeciesContextListModel = new DefaultListModel();
	private DefaultListModel fluorescenceSpeciesContextListModel = new DefaultListModel();
	private JButton removeButton;
	private JButton addButton;
	private JList allSpeciesContextList;
	private JList fluorescentSpeciesContextList;
	private ListCellRenderer speciesContextCellRenderer = new DefaultListCellRenderer(){
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof SpeciesContext && component instanceof JLabel){
				SpeciesContext sc = (SpeciesContext)value;
				((JLabel)component).setText(" " + sc.getName());
			}
			return component;
		}
	};
		
	private class InternalEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener, FocusListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == simulationContext.getMicroscopeMeasurement()) {
				refreshInterface();
			} else if (evt.getSource() == simulationContext.getModel() && evt.getPropertyName().equals(Model.PROPERTY_NAME_SPECIES_CONTEXTS)) {
				refreshAllSpeciesList();
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == removeButton) {
				removeButtonActionPerformed();
			} else if (e.getSource() == rdbtnZprojection
					|| e.getSource() == radioButtonGaussian
					|| e.getSource() == rdbtnExperimental) {
				setKernel();
			} else if (e.getSource() == pointSpreadFunctionsComboBox) {
				pointSpreadFunctionsComboBoxActionPerformed();
			} else if (e.getSource() == addButton) {
				addButtonActionPerformed();
			} else if (e.getSource() == importPsfButton) {
				importPSFButtonActionPerformed();
			}
		}
		
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == allSpeciesContextList) {
					addButton.setEnabled(allSpeciesContextList.getSelectedIndex() >= 0);
			} else if (e.getSource() == fluorescentSpeciesContextList) {
				removeButton.setEnabled(fluorescentSpeciesContextList.getSelectedIndex() >= 0);
			}
		}

		public void focusGained(FocusEvent e) {			
		}

		public void focusLost(FocusEvent e) {
			try {
				ConvolutionKernel ck = simulationContext.getMicroscopeMeasurement().getConvolutionKernel();
				if (ck instanceof GaussianConvolutionKernel) {
					GaussianConvolutionKernel gck = (GaussianConvolutionKernel)ck;
					if (e.getSource() == sigmaXYTextField) {
						String xyText = sigmaXYTextField.getText();
						Expression sigmaXY = gck.getSigmaXY_um();
						if (xyText != null && !xyText.equals(gck.getSigmaXY_um().infix())) {
							sigmaXY = new Expression(xyText);
							gck = new GaussianConvolutionKernel(sigmaXY, gck.getSigmaZ_um());
						}
					} else if (e.getSource() == sigmaZTextField) {
						String zText = sigmaZTextField.getText();							
						Expression sigmaZ = gck.getSigmaZ_um();
						if (zText != null && !zText.equals(gck.getSigmaZ_um().infix())) {
							sigmaZ = new Expression(zText);
							gck = new GaussianConvolutionKernel(gck.getSigmaXY_um(), sigmaZ);
						}
					}
					if (gck != ck) {
						simulationContext.getMicroscopeMeasurement().setConvolutionKernel(gck);
					}					
				}
			} catch (Exception e1) {
				DialogUtils.showErrorDialog(MicroscopeMeasurementPanel.this, e1.getMessage(), e1);
			}
		}
		
	}
	private InternalEventHandler internalEventHandler = new InternalEventHandler();
	
	public MicroscopeMeasurementPanel() {
		super();
		initialize();
	}
	
	public SimulationContext getSimulationContext() {
		return simulationContext;
	}

	public void setSimulationContext(SimulationContext newValue) {
		if (this.simulationContext == newValue) {
			return;
		}
		SimulationContext oldValue = newValue;
		if (oldValue != null) {
			oldValue.getModel().removePropertyChangeListener(internalEventHandler);
			oldValue.getMicroscopeMeasurement().removePropertyChangeListener(internalEventHandler);
		}
		this.simulationContext = newValue;
		if (newValue != null) {
			newValue.getModel().addPropertyChangeListener(internalEventHandler);
			newValue.getMicroscopeMeasurement().addPropertyChangeListener(internalEventHandler);
		}		
		refreshInterface();		
	}
	
	private JPanel createGaussianPsfPanel() {		
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.gridx = 0;
		gbc.insets = new Insets(0, 0, 0, 4);
		gbc.anchor = GridBagConstraints.LINE_START;
		panel.add(new JLabel("Sigma XY"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.gridx = 1;
		gbc.insets = new Insets(0, 4, 0, 15);
		panel.add(sigmaXYTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.gridx = 2;		
		gbc.insets = new Insets(0, 4, 0, 4);
		panel.add(new JLabel("Sigma Z"), gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.gridx = 3;		
		gbc.insets = new Insets(0, 4, 0, 4);
		panel.add(sigmaZTextField, gbc);

		// filler
		gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.gridx = 4;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 4, 0, 4);
		panel.add(Box.createHorizontalGlue(), gbc);
		
		return panel;
	}
	private JPanel createPSFPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		JLabel label = new JLabel("Point Spread Function");
		Font boldFont = label.getFont().deriveFont(Font.BOLD);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setFont(boldFont);
		JPanel p = new JPanel();
		p.setBackground(ItemBackgroundColor);
		p.add(label);
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		panel.add(p, gbc);		
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		panel.add(new JSeparator(), gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 2, 4);
		gbc.anchor = GridBagConstraints.LINE_START;
		panel.add(rdbtnZprojection, gbc);
														
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 2, 4);
		gbc.anchor = GridBagConstraints.LINE_START;
		panel.add(radioButtonGaussian, gbc);		

		gaussianPsfPanel = createGaussianPsfPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(0, 4, 2, 4);
		gbc.anchor = GridBagConstraints.LINE_START;
		panel.add(gaussianPsfPanel, gbc);		
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;
		panel.add(rdbtnExperimental, gbc);	
		
		experimentalPsfPanel = new JPanel(new GridLayout(1, 2, 10, 0));
		experimentalPsfPanel.add(pointSpreadFunctionsComboBox);
		experimentalPsfPanel.add(importPsfButton);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;
		panel.add(experimentalPsfPanel, gbc);		
		
		panel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		return panel;
	}
	private void initialize() {
		try {
			nameTextField = new JTextField(30);
			
			allSpeciesContextList = new JList(allSpeciesContextListModel);
			allSpeciesContextList.setCellRenderer(speciesContextCellRenderer);
			allSpeciesContextList.addListSelectionListener(internalEventHandler);
			
			fluorescentSpeciesContextList = new JList(fluorescenceSpeciesContextListModel);
			fluorescentSpeciesContextList.setCellRenderer(speciesContextCellRenderer);
			fluorescentSpeciesContextList.addListSelectionListener(internalEventHandler);
		
			addButton = new JButton(">>");
			addButton.setEnabled(false);
			addButton.addActionListener(internalEventHandler);

			removeButton = new JButton("<<");
			removeButton.addActionListener(internalEventHandler);
			removeButton.setEnabled(false);

			rdbtnExperimental = new JRadioButton("Experimental");
			rdbtnExperimental.addActionListener(internalEventHandler);
			rdbtnExperimental.setEnabled(false);
			
			pointSpreadFunctionsComboBox = new JComboBox(pointSpreadFunctionsComboModel);
			pointSpreadFunctionsComboBox.addActionListener(internalEventHandler);
			pointSpreadFunctionsComboBox.setEnabled(false);
			
			rdbtnZprojection = new JRadioButton("Z Projection");
			rdbtnZprojection.addActionListener(internalEventHandler);
			
			importPsfButton = new JButton("Import PSF");
			importPsfButton.addActionListener(internalEventHandler);

			radioButtonGaussian = new JRadioButton("Gaussian");
			radioButtonGaussian.addActionListener(internalEventHandler);
			
			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(rdbtnZprojection);
			buttonGroup.add(rdbtnExperimental);
			buttonGroup.add(radioButtonGaussian);

			sigmaXYTextField = new JTextField(15);
			sigmaXYTextField.addFocusListener(internalEventHandler);
			sigmaZTextField = new JTextField(15);
			sigmaZTextField.addFocusListener(internalEventHandler);
			
			setLayout(new GridBagLayout());
			
			JPanel panel = new JPanel(new GridBagLayout());
			panel.setBackground(Color.white);
			panel.setBorder(GuiConstants.TAB_PANEL_BORDER);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			JLabel label = new JLabel("Fluorescence Function Name");	
			Font boldFont = label.getFont().deriveFont(Font.BOLD);
			label.setFont(boldFont);
			JPanel p = new JPanel();
			p.setBackground(ItemBackgroundColor);
			p.add(label);
			panel.add(p, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.VERTICAL;
			gbc.weighty = 1.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			panel.add(new JSeparator(JSeparator.VERTICAL), gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.insets = new Insets(1, 4, 1, 1);
			gbc.fill = GridBagConstraints.BOTH;
			nameTextField.setBorder(null);
			gbc.anchor = GridBagConstraints.LINE_START;
			panel.add(nameTextField, gbc);
			
			int gridy = 0;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			add(panel, gbc);	
			
			gridy ++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			add(createPSFPanel(), gbc);	
									
			gridy ++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.insets = new Insets(4,4,4,4);
			gbc.weightx = 1;
			gbc.weighty = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.fill = GridBagConstraints.BOTH;
			add(createFluorescentSpeciesPanel(), gbc);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	private JPanel createFluorescentSpeciesPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		int gridy = 0;
		
		JLabel label = new JLabel("Choose Fluorescent Species");
		Font boldFont = label.getFont().deriveFont(Font.BOLD);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setFont(boldFont);
		JPanel p = new JPanel();
		p.setBackground(ItemBackgroundColor);
		p.add(label);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(p, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		panel.add(new JSeparator(), gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,10,4,4);
		gbc.weightx = 1;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		JScrollPane scrollPane1 = new JScrollPane(allSpeciesContextList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane1.setPreferredSize(new Dimension(200,200));
		panel.add(scrollPane1, gbc);
		
		JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		panel.add(buttonPanel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,10);
		gbc.weightx = 1;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		JScrollPane scrollPane2 = new JScrollPane(fluorescentSpeciesContextList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane2.setPreferredSize(scrollPane1.getPreferredSize());
		panel.add(scrollPane2, gbc);
		
		panel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		return panel;
	}
	
	// selection changed in combobox
	protected void pointSpreadFunctionsComboBoxActionPerformed() {
		if(pointSpreadFunctionsComboModel.getSize() == 0) {
			if(!rdbtnZprojection.isSelected()) {
				rdbtnZprojection.setSelected(true);
			}
			if(rdbtnExperimental.isEnabled()) {
				rdbtnExperimental.setEnabled(false);
			}
			if(pointSpreadFunctionsComboBox.isEnabled()) {
				pointSpreadFunctionsComboBox.setEnabled(false);
			}
		} else {
			if(!rdbtnExperimental.isEnabled()) {
				rdbtnExperimental.setEnabled(true);
			}
		}
	}
	
	protected void setKernel() {
		MicroscopeMeasurement microscopeMeasurement = simulationContext.getMicroscopeMeasurement();
		if (rdbtnZprojection.isSelected()) {
			microscopeMeasurement.setConvolutionKernel(new ProjectionZKernel());
			BeanUtils.enableComponents(gaussianPsfPanel, false);
			BeanUtils.enableComponents(experimentalPsfPanel, false);
		} else if (radioButtonGaussian.isSelected()) {
			microscopeMeasurement.setConvolutionKernel(new GaussianConvolutionKernel());
			BeanUtils.enableComponents(gaussianPsfPanel, true);
			BeanUtils.enableComponents(experimentalPsfPanel, false);
		} else if (rdbtnExperimental.isSelected()) {
			String psfName = (String)pointSpreadFunctionsComboBox.getSelectedItem();
			for (DataSymbol dataSymbol : simulationContext.getDataContext().getDataSymbols()){
				if (dataSymbol.getName().equals(psfName)){
					microscopeMeasurement.setConvolutionKernel(new ExperimentalPSF(dataSymbol));
					break;
				}
			}
			BeanUtils.enableComponents(gaussianPsfPanel, false);
			BeanUtils.enableComponents(experimentalPsfPanel, true);
		}
	}

	protected void refreshInterface() {
		if (simulationContext == null) {
			return;
		}
		MicroscopeMeasurement microscopeMeasurement = simulationContext.getMicroscopeMeasurement();
		nameTextField.setText(microscopeMeasurement.getName());
		
		refreshFluorescenceSpeciesList(microscopeMeasurement);		
		
		refreshAllSpeciesList();
		
		ConvolutionKernel ck = microscopeMeasurement.getConvolutionKernel();
		if (ck instanceof ProjectionZKernel) {
			rdbtnZprojection.setSelected(true);
			BeanUtils.enableComponents(gaussianPsfPanel, false);
			BeanUtils.enableComponents(experimentalPsfPanel, false);
		} else if (ck instanceof GaussianConvolutionKernel) {
			radioButtonGaussian.setSelected(true);
			BeanUtils.enableComponents(gaussianPsfPanel, true);
			BeanUtils.enableComponents(experimentalPsfPanel, false);
			sigmaXYTextField.setText(((GaussianConvolutionKernel) ck).getSigmaXY_um().infix());
			sigmaZTextField.setText(((GaussianConvolutionKernel) ck).getSigmaZ_um().infix());
		} else if (ck instanceof ExperimentalPSF) {
			rdbtnExperimental.setSelected(true);
			BeanUtils.enableComponents(gaussianPsfPanel, false);
			BeanUtils.enableComponents(experimentalPsfPanel, true);			
		}
		pointSpreadFunctionsComboModel.removeAllElements();
		if (simulationContext.getDataContext() != null){
			for (DataSymbol dataSymbol : simulationContext.getDataContext().getDataSymbols()){
				if (dataSymbol.getDataSymbolType().equals(DataSymbolType.POINT_SPREAD_FUNCTION)){
					pointSpreadFunctionsComboModel.addElement(dataSymbol.getName());
				}
			}
		}
		
	}

	private void refreshFluorescenceSpeciesList(
			MicroscopeMeasurement microscopeMeasurement) {
		fluorescenceSpeciesContextListModel.removeAllElements();
		for (SpeciesContext sc : microscopeMeasurement.getFluorescentSpecies()){
			fluorescenceSpeciesContextListModel.addElement(sc);
		}
	}

	private void refreshAllSpeciesList() {
		allSpeciesContextListModel.removeAllElements();
		for (SpeciesContext sc : simulationContext.getModel().getSpeciesContexts()){
			if (!simulationContext.getMicroscopeMeasurement().contains(sc)){
				allSpeciesContextListModel.addElement(sc);
			}
		}
	}

	protected void addButtonActionPerformed() {
		SpeciesContext selectedSpeciesContext = (SpeciesContext)allSpeciesContextList.getSelectedValue();
		if (selectedSpeciesContext != null && simulationContext != null && simulationContext.getMicroscopeMeasurement() != null){
			simulationContext.getMicroscopeMeasurement().addFluorescentSpecies(selectedSpeciesContext);
		}
	}

	protected void removeButtonActionPerformed() {
		SpeciesContext selectedSpeciesContext = (SpeciesContext)fluorescentSpeciesContextList.getSelectedValue();
		if (selectedSpeciesContext != null && simulationContext != null && simulationContext.getMicroscopeMeasurement() != null){
			simulationContext.getMicroscopeMeasurement().removeFluorescentSpecies(selectedSpeciesContext);
		}
	}
	
	protected void importPSFButtonActionPerformed() {
		PointSpreadFunctionManagement psfManager = new PointSpreadFunctionManagement(MicroscopeMeasurementPanel.this, 
				getSimulationContext());
		psfManager.importPointSpreadFunction();
	}


	private void handleException(Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION --------- in MicroscopeMeasurementPanel");
		exception.printStackTrace(System.out);
	}
	

	public static void main(java.lang.String[] args) {
		try {
			JFrame frame = new javax.swing.JFrame();
			MicroscopeMeasurementPanel aPanel = new MicroscopeMeasurementPanel();
			frame.setContentPane(aPanel);
			frame.setSize(aPanel.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.pack();
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}


	
}
