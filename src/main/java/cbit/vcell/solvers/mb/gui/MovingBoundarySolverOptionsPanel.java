package cbit.vcell.solvers.mb.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

import org.vcell.util.gui.CollapsiblePanel;

import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solvers.mb.MovingBoundarySolverOptions;
import cbit.vcell.solvers.mb.MovingBoundarySolverOptions.ExtrapolationMethod;
import cbit.vcell.solvers.mb.MovingBoundarySolverOptions.RedistributionMode;
import cbit.vcell.solvers.mb.MovingBoundarySolverOptions.RedistributionVersion;

@SuppressWarnings("serial")
public class MovingBoundarySolverOptionsPanel extends CollapsiblePanel {

	private class EventHandler implements ActionListener, FocusListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if (e.getSource() == comboBoxRedistMode)
			{
				RedistributionMode selectedItem = (RedistributionMode) comboBoxRedistMode.getSelectedItem();
				solverTaskDescription.getMovingBoundarySolverOptions().setRedistributionMode(selectedItem);
				if (selectedItem == RedistributionMode.FULL_REDIST)
				{
					comboBoxRedistVersion.setEnabled(true);
					comboBoxRedistVersion.setSelectedItem(solverTaskDescription.getMovingBoundarySolverOptions()
							.getRedistributionVersion() == RedistributionVersion.NOT_APPLICABLE
									? RedistributionVersion.EQUI_BOND_REDISTRIBUTE
									: solverTaskDescription.getMovingBoundarySolverOptions()
											.getRedistributionVersion());
				}
				else
				{
					comboBoxRedistVersion.setSelectedItem(RedistributionVersion.NOT_APPLICABLE);
					comboBoxRedistVersion.setEnabled(false);
				}
				if (selectedItem == RedistributionMode.NO_REDIST)
				{
					textFieldRedistributionFrequency.setEnabled(false);
					textFieldRedistributionFrequency.setValue(null);
				}
				else
				{
					textFieldRedistributionFrequency.setEnabled(true);
					textFieldRedistributionFrequency.setValue(solverTaskDescription.getMovingBoundarySolverOptions().getRedistributionFrequency());
				}
			}
			else if (e.getSource() == comboBoxRedistVersion)
			{
				RedistributionVersion selectedItem = (RedistributionVersion) comboBoxRedistVersion.getSelectedItem();
				solverTaskDescription.getMovingBoundarySolverOptions().setRedistributionVersion(selectedItem);
			}
			else if (e.getSource() == comboBoxExtrapolationMethod)
			{
				ExtrapolationMethod selectedItem = (ExtrapolationMethod) comboBoxExtrapolationMethod.getSelectedItem();
				solverTaskDescription.getMovingBoundarySolverOptions().setExtrapolationMethod(selectedItem);
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (e.getSource() == textFieldFrontToNodeRatio)
			{
				Object text = textFieldFrontToNodeRatio.getValue();
				if (text != null)
				{
					solverTaskDescription.getMovingBoundarySolverOptions().setFrontToNodeRatio((Double)text);
				}
				else
				{
					textFieldFrontToNodeRatio.setValue(solverTaskDescription.getMovingBoundarySolverOptions().getFrontToNodeRatio());
				}
			}
			else if (e.getSource() == textFieldRedistributionFrequency)
			{
				Object text = textFieldRedistributionFrequency.getValue();
				if (text != null)
				{
					solverTaskDescription.getMovingBoundarySolverOptions().setRedistributionFrequency((Integer)text);
				}
				else
				{
					textFieldRedistributionFrequency.setValue(solverTaskDescription.getMovingBoundarySolverOptions().getRedistributionFrequency());
				}
			}
		}
	}
	
	private EventHandler eventHandler = new EventHandler();
	private SolverTaskDescription solverTaskDescription;	
	private JFormattedTextField textFieldFrontToNodeRatio;
	private JFormattedTextField textFieldRedistributionFrequency;
	private JComboBox<RedistributionMode> comboBoxRedistMode = new JComboBox<>(RedistributionMode.values()); 
	private JComboBox<RedistributionVersion> comboBoxRedistVersion = new JComboBox<>(RedistributionVersion.values()); 
	private JComboBox<ExtrapolationMethod> comboBoxExtrapolationMethod = new JComboBox<>(ExtrapolationMethod.values()); 

	private JPanel getFrontTrackingPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(GuiConstants.TAB_PANEL_BORDER, "Front Tracking"));
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		panel.add(new JLabel("Front To Volume Nodes Ratio"), gbc);
			
		textFieldFrontToNodeRatio = new JFormattedTextField(DecimalFormat.getInstance());
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		panel.add(textFieldFrontToNodeRatio, gbc);
		
		++ gridy;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		panel.add(new JLabel("Front Redistribution Method"), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		panel.add(comboBoxRedistMode, gbc);
		
		++ gridy;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		panel.add(new JLabel("Full Redistribution Mode"), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		panel.add(comboBoxRedistVersion, gbc);
		
		++ gridy;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		panel.add(new JLabel("Redistribution Frequency"), gbc);
		
	    NumberFormatter integerFormatter = new NumberFormatter(NumberFormat.getInstance());
	    integerFormatter.setValueClass(Integer.class);
	    integerFormatter.setMinimum(1);
	    integerFormatter.setMaximum(Integer.MAX_VALUE);
		textFieldRedistributionFrequency = new JFormattedTextField(integerFormatter);
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		textFieldRedistributionFrequency.setToolTipText("integer only");
		panel.add(textFieldRedistributionFrequency, gbc);
		
		
		comboBoxRedistMode.setRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				// TODO Auto-generated method stub
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (c instanceof JLabel)
				{
					JLabel lbl = (JLabel)c;
					lbl.setText(((RedistributionMode)value).getLabel());
				}
				return c;
			}
			
		});
		
		comboBoxRedistVersion.setRenderer(new DefaultListCellRenderer() {
			
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				// TODO Auto-generated method stub
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (c instanceof JLabel)
				{
					JLabel lbl = (JLabel)c;
					lbl.setText(((RedistributionVersion)value).getLabel());
				}
				return c;
			}
			
		});
		
		comboBoxExtrapolationMethod.setRenderer(new DefaultListCellRenderer() {
			
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				// TODO Auto-generated method stub
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (c instanceof JLabel)
				{
					JLabel lbl = (JLabel)c;
					lbl.setText(((ExtrapolationMethod)value).getLabel());
				}
				return c;
			}
			
		});
		return panel;
	}
	
	public JPanel getVolumeFrontCouplingPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(GuiConstants.TAB_PANEL_BORDER, "Front and Volume Coupling"));
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;
		panel.add(new JLabel("Volume To Front Extrapolation Method"), gbc);
			
		++ gridy;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		panel.add(comboBoxExtrapolationMethod, gbc);

		++ gridy;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		panel.add(Box.createVerticalGlue(), gbc);
		
		return panel;
	}
	
	public MovingBoundarySolverOptionsPanel() {
		super("Advanced Solver Options");
		
		getContentPanel().setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		getContentPanel().add(getFrontTrackingPanel(), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = 0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		gbc.weightx = 0.2;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		getContentPanel().add(getVolumeFrontCouplingPanel(), gbc);
		
		comboBoxRedistMode.addActionListener(eventHandler);
		comboBoxRedistVersion.addActionListener(eventHandler);
		comboBoxExtrapolationMethod.addActionListener(eventHandler);
		textFieldFrontToNodeRatio.addFocusListener(eventHandler);
		textFieldRedistributionFrequency.addFocusListener(eventHandler);
	}

	public final void setSolverTaskDescription(SolverTaskDescription newValue) {
		this.solverTaskDescription = newValue;
		updateDisplay();
	}
	
	private void updateDisplay()
	{
		if (solverTaskDescription != null && solverTaskDescription.getMovingBoundarySolverOptions() != null)
		{
			setVisible(true);
			textFieldFrontToNodeRatio.setValue(solverTaskDescription.getMovingBoundarySolverOptions().getFrontToNodeRatio());
			RedistributionMode redistributionMode = solverTaskDescription.getMovingBoundarySolverOptions().getRedistributionMode();
			comboBoxRedistMode.setSelectedItem(redistributionMode);
			comboBoxRedistVersion.setSelectedItem(solverTaskDescription.getMovingBoundarySolverOptions().getRedistributionVersion());
			comboBoxExtrapolationMethod.setSelectedItem(solverTaskDescription.getMovingBoundarySolverOptions().getExtrapolationMethod());
			if (redistributionMode == RedistributionMode.NO_REDIST)
			{
				textFieldRedistributionFrequency.setEnabled(false);
			}
			else
			{
				textFieldRedistributionFrequency.setValue(solverTaskDescription.getMovingBoundarySolverOptions().getRedistributionFrequency());
			}
		}
		else
		{
			setVisible(false);
		}
	}
}
