package org.vcell.model.rbm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.MathematicsPanel;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;

public class NetworkConstraintsPanel extends JPanel {

	private JTextField maxIterationTextField;
	private JTextField maxMolTextField;
	private NetworkConstraints networkConstraints;
	private EventHandler eventHandler = new EventHandler();
	private SimulationContext fieldSimulationContext;
	private IssueManager fieldIssueManager;
	private SelectionManager fieldSelectionManager;
	
	private JTextField speciesBeforeTextField;
	private JTextField speciesAfterTextField;
	private JTextField reactionsBeforeTextField;
	private JTextField reactionsAfterTextField;
	
	private JButton refreshMath;
	private JButton viewNetwork;
	
	private class EventHandler implements FocusListener, ActionListener, PropertyChangeListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == maxIterationTextField) {
				changeMaxIteration();
			} else if (e.getSource() == maxMolTextField) {
				changeMaxMolPerSpecies();
			} else if(e.getSource() == getRefreshMathButton()) {
				refreshMath();
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
		
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() instanceof SimulationContext && evt.getPropertyName().equals("mathDescription") && evt.getNewValue()!=null){
				refreshInterface();
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
	
	private JButton getRefreshMathButton() {
		if (refreshMath == null) {
			refreshMath = new javax.swing.JButton("Refresh Math");
			refreshMath.setName("RefreshMathButton");
		}
		return refreshMath;
	}
	private JButton getViewNetworkButton() {
		if (viewNetwork == null) {
			viewNetwork = new javax.swing.JButton("View Network");
			viewNetwork.setName("ViewNetworkButton");
		}
		return viewNetwork;
	}


	private void initialize() {		
		maxIterationTextField = new JTextField();
		maxMolTextField = new JTextField();
		speciesBeforeTextField = new JTextField();
		speciesAfterTextField = new JTextField();
		reactionsBeforeTextField = new JTextField();
		reactionsAfterTextField = new JTextField();

		maxIterationTextField.addActionListener(eventHandler);
		maxMolTextField.addActionListener(eventHandler);
		speciesBeforeTextField.addActionListener(eventHandler);
		speciesAfterTextField.addActionListener(eventHandler);
		reactionsBeforeTextField.addActionListener(eventHandler);
		reactionsAfterTextField.addActionListener(eventHandler);
		getRefreshMathButton().addActionListener(eventHandler);
		getViewNetworkButton().addActionListener(eventHandler);
		
		maxIterationTextField.addFocusListener(eventHandler);
		maxMolTextField.addFocusListener(eventHandler);
		
		JPanel upperPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel lowerPanel = new JPanel();
		
		upperPanel.setLayout(new GridBagLayout());
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(20, 10, 4, 4);
		upperPanel.add(new JLabel("Max Iteration"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20, 4, 4, 10);
		upperPanel.add(maxIterationTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.insets = new Insets(20, 10, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		upperPanel.add(new JLabel("Max Molecules/Species"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20, 4, 4, 10);
		upperPanel.add(maxMolTextField, gbc);
		
		// ----------------------------------------------------
		
		gridy = 0;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(4, 4, 4, 10);
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		centerPanel.add(Box.createVerticalGlue(), gbc);
		
		// ----------------------------------------------------------
		lowerPanel.setLayout(new GridBagLayout());
		gridy = 0;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		gbc.insets = new Insets(20, 10, 4, 4);
		lowerPanel.add(new JLabel("Species (before flattening)"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20, 4, 4, 10);
		lowerPanel.add(speciesBeforeTextField, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.insets = new Insets(20, 10, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		lowerPanel.add(new JLabel("Species (flattened)"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20, 4, 4, 10);
		lowerPanel.add(speciesAfterTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20, 4, 4, 10);
		lowerPanel.add(getRefreshMathButton(), gbc);

		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		gbc.insets = new Insets(20, 10, 4, 4);
		lowerPanel.add(new JLabel("Reactions (before flattening)"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20, 4, 4, 10);
		lowerPanel.add(reactionsBeforeTextField, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.insets = new Insets(20, 10, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		lowerPanel.add(new JLabel("Reactions (flattened)"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20, 4, 4, 10);
		lowerPanel.add(reactionsAfterTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20, 4, 4, 10);
		lowerPanel.add(getViewNetworkButton(), gbc);

		setLayout(new GridLayout(3,1));
		add(upperPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(lowerPanel, BorderLayout.SOUTH);
		
		speciesBeforeTextField.setEditable(false);
		speciesAfterTextField.setEditable(false);
		reactionsBeforeTextField.setEditable(false);
		reactionsAfterTextField.setEditable(false);
	}
	
	public void setNetworkConstraints(NetworkConstraints newValue) {
		if (networkConstraints == newValue) {
			return;
		}
		networkConstraints = newValue;
		refreshInterface();
	}
	
	public void setSimulationContext(SimulationContext simulationContext) {
		if(simulationContext == null) {
			return;
		}
		if(fieldSimulationContext != null) {
			fieldSimulationContext.removePropertyChangeListener(eventHandler);
		}
		fieldSimulationContext = simulationContext;
		fieldSimulationContext.addPropertyChangeListener(eventHandler);
		refreshInterface();
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		fieldSelectionManager = selectionManager;
	}

	public void setIssueManager(IssueManager issueManager) {
		fieldIssueManager = issueManager;
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
		
		MathMapping mm = fieldSimulationContext.getMostRecentlyCreatedMathMapping();
		String text3 = null;
		String text4 = null;
		String text5 = null;
		String text6 = null;
		if(mm != null) {
			text3 = fieldSimulationContext.getModel().getNumSpeciesContexts() + "";
			text5 = fieldSimulationContext.getModel().getNumReactions() + "";
			if(fieldSimulationContext.getModel().getRbmModelContainer().isEmpty()) {
				text4 = "rule based model needed";
				text6 = "rule based model needed";
			} else {
				text4 = mm.getSimulationContext().getModel().getNumSpeciesContexts() + "";
				text6 = mm.getSimulationContext().getModel().getNumReactions() + "";
			}
		} else {
			text3 = fieldSimulationContext.getModel().getNumSpeciesContexts() + "";
			text5 = fieldSimulationContext.getModel().getNumReactions() + "";
			if(fieldSimulationContext.getModel().getRbmModelContainer().isEmpty()) {
				text4 = "rule based model needed";
				text6 = "rule based model needed";
			} else {
				text4 = "no recent math mapping.";
				text6 = "no recent math mapping.";
			}
		}
		speciesBeforeTextField.setText(text3);
		speciesAfterTextField.setText(text4);
		reactionsBeforeTextField.setText(text5);
		reactionsAfterTextField.setText(text6);
	}
	
	private void refreshMath() {
		ClientTaskDispatcher.dispatch(NetworkConstraintsPanel.this, new Hashtable<String, Object>(), ClientRequestManager.updateMath(this, fieldSimulationContext), false);
	}


}
