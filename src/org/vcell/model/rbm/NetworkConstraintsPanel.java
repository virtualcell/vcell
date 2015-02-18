package org.vcell.model.rbm;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.mapping.SimulationContext;

public class NetworkConstraintsPanel extends JPanel {

	private JTextField maxIterationTextField;
	private JTextField maxMolTextField;
	private NetworkConstraints networkConstraints;
	private EventHandler eventHandler = new EventHandler();
	private SimulationContext fieldSimulationContext;
	private IssueManager fieldIssueManager;
	private SelectionManager fieldSelectionManager;
	
	private class EventHandler implements FocusListener, ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == maxIterationTextField) {
				changeMaxIteration();
			} else if (e.getSource() == maxMolTextField) {
				changeMaxMolPerSpecies();
			}			
		}

		public void focusGained(FocusEvent e) {
		}

		public void focusLost(FocusEvent e) {
			if (e.getSource() == maxIterationTextField) {
				changeMaxIteration();
			} else if (e.getSource() == maxMolTextField) {
				changeMaxMolPerSpecies();
			}
		}		
	}
	
	public NetworkConstraintsPanel() {
		super();
		initialize();
	}
	
	public void changeMaxMolPerSpecies() {
		String text = maxMolTextField.getText();
		if (text == null || text.trim().length() == 0) {
			return;
		}
		networkConstraints.setMaxMoleculesPerSpecies(Integer.valueOf(text));
	}

	public void changeMaxIteration() {
		String text = maxIterationTextField.getText();
		if (text == null || text.trim().length() == 0) {
			return;
		}
		networkConstraints.setMaxIteration(Integer.valueOf(text));
	}

	private void initialize() {		
		maxIterationTextField = new JTextField();
		maxMolTextField = new JTextField();
		maxIterationTextField.addActionListener(eventHandler);
		maxMolTextField.addActionListener(eventHandler);
		maxIterationTextField.addFocusListener(eventHandler);
		maxMolTextField.addFocusListener(eventHandler);
		
		setLayout(new GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(20, 10, 4, 4);
		add(new JLabel("Max Iteration"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20, 4, 4, 10);
		add(maxIterationTextField, gbc);
		
		++ gridy;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 10, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Max Molecules/Species"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 10);
		add(maxMolTextField, gbc);
		
		++ gridy;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(4, 4, 4, 10);
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(Box.createVerticalGlue(), gbc);
		
		
	}
	
	public void setNetworkConstraints(NetworkConstraints newValue) {
		if (networkConstraints == newValue) {
			return;
		}
		networkConstraints = newValue;
		refreshInterface();
	}
	
	private void refreshInterface() {
		String text1 = null;
		String text2 = null;
		if (networkConstraints != null) {
			text1 = networkConstraints.getMaxIteration() + "";
			text2 = networkConstraints.getMaxMoleculesPerSpecies() + "";
		}
		maxIterationTextField.setText(text1);
		maxMolTextField.setText(text2);
	}

	public void setSimulationContext(SimulationContext simulationContext) {
		fieldSimulationContext = simulationContext;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		fieldSelectionManager = selectionManager;
	}

	public void setIssueManager(IssueManager issueManager) {
		fieldIssueManager = issueManager;
	}
}
