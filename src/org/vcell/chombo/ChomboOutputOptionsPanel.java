package org.vcell.chombo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vcell.util.gui.CollapsiblePanel;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.solver.SolverTaskDescription;

@SuppressWarnings("serial")
public class ChomboOutputOptionsPanel extends CollapsiblePanel {

	private class IvjEventHandler implements ActionListener, PropertyChangeListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			if (e.getSource() == chomboOutputCheckBox || e.getSource() == vcellOutputCheckBox) 
			{
				setChomboOutputOptions();
			} 
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == solverTaskDescription)
			{
				updateDisplay();
			}
		}
	}
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JCheckBox vcellOutputCheckBox;
	private JCheckBox chomboOutputCheckBox;
	private SolverTaskDescription solverTaskDescription;	

	public ChomboOutputOptionsPanel() {
		super("EBChombo Options");
		initialize();
	}

	private void initialize() 
	{
		
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder(GuiConstants.TAB_PANEL_BORDER, "Save Output Files"));
		
		vcellOutputCheckBox = new JCheckBox("VCell");
		chomboOutputCheckBox = new JCheckBox("Chombo");
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		add(vcellOutputCheckBox, gbc);
		
		++ gridy;
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		add(chomboOutputCheckBox, gbc);
		
		vcellOutputCheckBox.addActionListener(ivjEventHandler);
		chomboOutputCheckBox.addActionListener(ivjEventHandler);
	}

	private void setChomboOutputOptions() {
		solverTaskDescription.getChomboSolverSpec().setSaveVCellOutput(vcellOutputCheckBox.isSelected());
		solverTaskDescription.getChomboSolverSpec().setSaveChomboOutput(chomboOutputCheckBox.isSelected());
	}

	private void updateDisplay() {
		if (!solverTaskDescription.getSolverDescription().isChomboSolver()) {
			setVisible(false);
			return;
		}
		setVisible(true);
		vcellOutputCheckBox.setSelected(solverTaskDescription.getChomboSolverSpec().isSaveVCellOutput());
		chomboOutputCheckBox.setSelected(solverTaskDescription.getChomboSolverSpec().isSaveChomboOutput());
	}

	public final void setSolverTaskDescription(SolverTaskDescription newValue) {
		SolverTaskDescription oldValue = this.solverTaskDescription;
		if (oldValue == newValue)
		{
			return;
		}
		if (oldValue != null)
		{
			oldValue.removePropertyChangeListener(ivjEventHandler);
		}
		this.solverTaskDescription = newValue;
		if (solverTaskDescription != null)
		{
			solverTaskDescription.addPropertyChangeListener(ivjEventHandler);
		}
		updateDisplay();		
	}
}
