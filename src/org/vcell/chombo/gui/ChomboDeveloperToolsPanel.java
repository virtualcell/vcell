package org.vcell.chombo.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.solver.SolverTaskDescription;

@SuppressWarnings("serial")
public class ChomboDeveloperToolsPanel extends CollapsiblePanel {

	private class EventHandler implements ActionListener, FocusListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			if (e.getSource() == includeNewFeatureCheckBox)
			{
				solverTaskDescription.getChomboSolverSpec().setActivateFeatureUnderDevelopment(includeNewFeatureCheckBox.isSelected());
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (e.getSource() == smallVolFracTextField)
			{
				String text = smallVolFracTextField.getText();
				try
				{
					double d = new Double(text);
					solverTaskDescription.getChomboSolverSpec().setSmallVolfracThreshold(d);
				}
				catch (NumberFormatException ex)
				{
					DialogUtils.showErrorDialog(ChomboDeveloperToolsPanel.this, "Small Volfrac Threshold must be a number.", ex);
					smallVolFracTextField.requestFocus();
				}
			}
		}
	}
	
	private EventHandler eventHandler = new EventHandler();
	private SolverTaskDescription solverTaskDescription;	
	private JCheckBox includeNewFeatureCheckBox;
	private JTextField smallVolFracTextField; 

	public ChomboDeveloperToolsPanel() {
		super("Developer Tools");
		
		getContentPanel().setLayout(new GridBagLayout());
		
		includeNewFeatureCheckBox = new JCheckBox("Activate Feature Under Development");
		includeNewFeatureCheckBox.addActionListener(eventHandler);
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 2;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		getContentPanel().add(includeNewFeatureCheckBox, gbc);
		
		smallVolFracTextField = new JTextField();
		smallVolFracTextField.addFocusListener(eventHandler);
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 1;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		getContentPanel().add(new JLabel("Small Volfrac Threshold"), gbc);
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = 1;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		getContentPanel().add(smallVolFracTextField, gbc);
	}

	public final void setSolverTaskDescription(SolverTaskDescription newValue) {
		this.solverTaskDescription = newValue;
		if(solverTaskDescription.getChomboSolverSpec() != null){
			smallVolFracTextField.setText(solverTaskDescription.getChomboSolverSpec().getSmallVolfracThreshold() + "");
			includeNewFeatureCheckBox.setSelected(solverTaskDescription.getChomboSolverSpec().isActivateFeatureUnderDevelopment());
		}else{
			smallVolFracTextField.setText("");
			includeNewFeatureCheckBox.setSelected(false);
		}
	}

}
