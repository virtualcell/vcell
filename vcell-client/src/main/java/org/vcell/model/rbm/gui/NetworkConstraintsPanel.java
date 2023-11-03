package org.vcell.model.rbm.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.model.rbm.RbmNetworkGenerator;
import org.vcell.model.rbm.common.MaxStoichiometryEntity;
import org.vcell.model.rbm.gui.EditConstraintsPanel.ActionButtons;
import org.vcell.util.BeanUtils;
import org.vcell.util.GeneralGuiUtils;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.bionetgen.ObservableGroup;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.BioModelEditor;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.desktop.biomodel.SimulationConsolePanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.CreateBNGOutputSpec;
import cbit.vcell.client.task.ReturnBNGOutput;
import cbit.vcell.client.task.RunBioNetGen;
import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.mapping.NetworkTransformer;
import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.mapping.gui.NetworkConstraintsTableModel;
import cbit.vcell.mapping.gui.StoichiometryTableModel;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.server.bionetgen.BNGExecutorService;
import cbit.vcell.server.bionetgen.BNGInput;
import cbit.vcell.solvers.ApplicationMessage;

// we should use WindowBuilder Plugin (add it to Eclipse IDE) to speed up panel design
// can choose absolute layout and place everything exactly as we see fit
@SuppressWarnings("serial")
public class NetworkConstraintsPanel extends DocumentEditorSubPanel implements BioNetGenUpdaterCallback, ApplicationSpecificationsPanel.Specifier {

	private EventHandler eventHandler = new EventHandler();
	private SimulationContext fieldSimulationContext;
	private IssueManager fieldIssueManager;
	private SelectionManager fieldSelectionManager;
	
	private JDialog viewSpeciesDialog = null;
	private JDialog viewReactionsDialog = null;
	private JDialog viewObservablesMapDialog = null;
	
	private JLabel seedSpeciesLabel;
	private JLabel reactionRulesLabel;
	private JLabel generatedSpeciesLabel;
	private JLabel generatedReactionsLabel;
	private JLabel observablesMapLabel;
	private JButton viewGeneratedSpeciesButton;
	private JButton viewGeneratedReactionsButton;
	private JButton viewObservablesMapButton;
	private JLabel somethingInsufficientLabel;

	private EditorScrollTable networkConstraintsTable = null;
	private NetworkConstraintsTableModel networkConstraintsTableModel = null;
	private JButton refreshMathButton;
	private JButton createModelButton;
	
	private class EventHandler implements FocusListener, ActionListener, PropertyChangeListener {

		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == getViewGeneratedSpeciesButton()) {
				viewGeneratedSpecies();
			} else if(e.getSource() == getViewGeneratedReactionsButton()) {
				viewGeneratedReactions();
			} else if(e.getSource() == getViewObservablesMapButton()) {
				viewObservablesMap();
			} else if(e.getSource() == getRefreshMathButton()) {
				runBioNetGen();
			} else if(e.getSource() == getCreateModelButton()) {
				createModel();
			}
		}
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
		}
		public void propertyChange(java.beans.PropertyChangeEvent event) {
			if(event.getSource() instanceof Model && event.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST)) {
				refreshInterface();
			} else if(event.getSource() instanceof SimulationContext && event.getPropertyName().equals("bngOutputChanged")) {
				refreshInterface();
			}
		}
	}
	ApplicationMessage getApplicationMessage() {
		return new ApplicationMessage(ApplicationMessage.PROGRESS_MESSAGE, 2, 1, "some progress error", "some progress message");
	}
	public NetworkConstraintsPanel() {
		super();
		initialize();
	}
	
	@Override
	public ActiveViewID getActiveView() {
		return ActiveViewID.network_setting;
	}
	/**
	 * no-op 
	 */
	@Override
	public void setSearchText(String s) {
		
	}
	
	private JButton getViewGeneratedSpeciesButton() {
		if (viewGeneratedSpeciesButton == null) {
			viewGeneratedSpeciesButton = new javax.swing.JButton("View");
			viewGeneratedSpeciesButton.setName("ViewGeneratedSpeciesButton");
		}
		return viewGeneratedSpeciesButton;
	}
	private JButton getViewGeneratedReactionsButton() {
		if (viewGeneratedReactionsButton == null) {
			viewGeneratedReactionsButton = new javax.swing.JButton("View");
			viewGeneratedReactionsButton.setName("ViewGeneratedReactionsButton");
		}
		return viewGeneratedReactionsButton;
	}
	private JButton getViewObservablesMapButton() {
		if (viewObservablesMapButton == null) {
			viewObservablesMapButton = new javax.swing.JButton("View");
			viewObservablesMapButton.setName("ViewObservablesMapButton");
		}
		return viewObservablesMapButton;
	}
	private JButton getRefreshMathButton() {
		if (refreshMathButton == null) {
			refreshMathButton = new javax.swing.JButton(" Edit / Test Constraints ");
			refreshMathButton.setName("RefreshMathButton");
		}
		return refreshMathButton;
	}
	private JButton getCreateModelButton() {
		if (createModelButton == null) {
			createModelButton = new javax.swing.JButton(" Create new VCell BioModel from Network ");
			createModelButton.setName("CreateModelButton");
		}
		return createModelButton;
	}

	private void initialize() {
		seedSpeciesLabel = new JLabel();
		reactionRulesLabel = new JLabel();
		generatedSpeciesLabel = new JLabel();
		generatedReactionsLabel = new JLabel();
		observablesMapLabel = new JLabel("Observables Map");
		somethingInsufficientLabel = new JLabel();
		
		networkConstraintsTable = new EditorScrollTable();
		networkConstraintsTableModel = new NetworkConstraintsTableModel(networkConstraintsTable);
		networkConstraintsTable.setModel(networkConstraintsTableModel);
		
		getViewGeneratedSpeciesButton().addActionListener(eventHandler);
		getViewGeneratedReactionsButton().addActionListener(eventHandler);
		getViewObservablesMapButton().addActionListener(eventHandler);
		getRefreshMathButton().addActionListener(eventHandler);
		getCreateModelButton().addActionListener(eventHandler);
		
		// ------------------------------------------- The 2 group boxes --------------------------
		JPanel thePanel = new JPanel();

		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();

		TitledBorder titleTop = BorderFactory.createTitledBorder(loweredEtchedBorder, " Network Constraints ");
		titleTop.setTitleJustification(TitledBorder.LEFT);
		titleTop.setTitlePosition(TitledBorder.TOP);

		TitledBorder titleBottom = BorderFactory.createTitledBorder(loweredEtchedBorder, " Generated Network ");
		titleBottom.setTitleJustification(TitledBorder.LEFT);
		titleBottom.setTitlePosition(TitledBorder.TOP);
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(1, 1, 1, 1);
		add(thePanel, gbc);
		
		// ------------------------------------------- Populating the top group box ---------------
		JPanel top = new JPanel();
		JPanel bottom = new JPanel();
		
		top.setBorder(titleTop);
		bottom.setBorder(titleBottom);
		
		thePanel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
		thePanel.add(top, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		thePanel.add(bottom, gbc);
		
		// we may want to use a scroll pane whose viewing area is the JTable to provide similar look with NetGen Console
		JScrollPane p = new JScrollPane(networkConstraintsTable);
		p.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		top.setLayout(new GridBagLayout());		// --- bottom
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(5, 4, 4, 10);
		top.add(p, gbc);
		
		// ------------------------------------------- Populating the right group box ------------
		bottom.setLayout(new GridBagLayout());		// --- top
		int gridy = 0;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 11, 1, 10);
		bottom.add(generatedSpeciesLabel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 1, 10);
		bottom.add(getViewGeneratedSpeciesButton(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 1, 10);
		bottom.add(getRefreshMathButton(), gbc);

		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(3, 11, 1, 10);
		bottom.add(generatedReactionsLabel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(3, 4, 1, 10);
		bottom.add(getViewGeneratedReactionsButton(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(3, 4, 1, 10);
		bottom.add(getCreateModelButton(), gbc);

		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(3, 11, 1, 10);
		bottom.add(observablesMapLabel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(3, 4, 1, 10);
		bottom.add(getViewObservablesMapButton(), gbc);
		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(3, 11, 4, 10);
		bottom.add(somethingInsufficientLabel, gbc);

		networkConstraintsTable.setDefaultRenderer(String.class, new DefaultScrollTableCellRenderer());
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
		
		Model m = fieldSimulationContext.getModel();
		if(m != null) {
			m.removePropertyChangeListener(eventHandler);
			m.addPropertyChangeListener(eventHandler);
		}
		networkConstraintsTableModel.setSimulationContext(simulationContext);
		refreshInterface();
	}
	public SimulationContext getSimulationContext() {
		return fieldSimulationContext;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		fieldSelectionManager = selectionManager;
	}
	public IssueManager getIssueManager() {
		return fieldIssueManager;
	}
	public void setIssueManager(IssueManager issueManager) {
		fieldIssueManager = issueManager;
	}
	
	
	public void updateOutputSpecToSimulationContext(BNGOutputSpec outputSpec) {
		synchronized (this) {
			// Warning: the results from "Edit / Test Constraints" are never cached because we want to repeatedly run it 
			// even if nothing changed - hence we set the hash to null
			fieldSimulationContext.setMd5hash(null);
			fieldSimulationContext.setMostRecentlyCreatedOutputSpec(outputSpec);
		}
	}
	public void updateLimitExceededWarnings(BNGOutputSpec outputSpec) {
		if(outputSpec == null || outputSpec.getBNGSpecies() == null || outputSpec.getBNGReactions() == null) {
			return;
		}
		if(outputSpec.getBNGSpecies().length > NetworkTransformer.getSpeciesLimit(fieldSimulationContext)) {
			String message = NetworkTransformer.getSpeciesLimitExceededMessage(outputSpec, fieldSimulationContext);
			appendToConsole(message);
		}
		if(outputSpec.getBNGReactions().length > NetworkTransformer.getReactionsLimit(fieldSimulationContext)) {
			String message = NetworkTransformer.getReactionsLimitExceededMessage(outputSpec, fieldSimulationContext);
			appendToConsole(message);
		}
	}

	
	@Override
	public void updateBioNetGenOutput(BNGOutputSpec outputSpec) {
		synchronized (this) {
			// Warning: the results from "Edit / Test Constraints" are never cached because we want to repeatedly run it 
			// even if nothing changed - hence we set the hash to null
			fieldSimulationContext.setMd5hash(null);
			fieldSimulationContext.setMostRecentlyCreatedOutputSpec(outputSpec);
		}
//		refreshInterface();
		if(outputSpec == null || outputSpec.getBNGSpecies() == null || outputSpec.getBNGReactions() == null) {
			return;
		}
		if(outputSpec.getBNGSpecies().length > NetworkTransformer.getSpeciesLimit(fieldSimulationContext)) {
			String message = NetworkTransformer.getSpeciesLimitExceededMessage(outputSpec, fieldSimulationContext);
			appendToConsole(message);
		}
		if(outputSpec.getBNGReactions().length > NetworkTransformer.getReactionsLimit(fieldSimulationContext)) {
			String message = NetworkTransformer.getReactionsLimitExceededMessage(outputSpec, fieldSimulationContext);
			appendToConsole(message);
		}
	}
	@Override
	public void setNewCallbackMessage(final TaskCallbackMessage newCallbackMessage) {
		if (!SwingUtilities.isEventDispatchThread()){
			SwingUtilities.invokeLater(new Runnable(){
	
				@Override
				public void run() {
					appendToConsole(newCallbackMessage);
				}
				
			});
		}else{
			appendToConsole(newCallbackMessage);
		}
	}
	
	private void appendToConsole(String string) {
		TaskCallbackMessage newCallbackMessage = new TaskCallbackMessage(TaskCallbackStatus.Error, string);
		fieldSimulationContext.appendToConsole(newCallbackMessage);
	}
	private void appendToConsole(TaskCallbackMessage newCallbackMessage) {
		fieldSimulationContext.appendToConsole(newCallbackMessage);
	}
	
	public void refreshInterface() {
		networkConstraintsTableModel.refreshData();
		String text1 = null;
		String text2 = null;
		if (getSimulationContext().getNetworkConstraints() != null) {
			text1 = getSimulationContext().getNetworkConstraints().getMaxIteration() + "";
			text2 = getSimulationContext().getNetworkConstraints().getMaxMoleculesPerSpecies() + "";
		}
		String text3 = null;
		String text4 = null;
		String text5 = null;
		String text6 = null;
		String text7 = "none";
		if(fieldSimulationContext.getMostRecentlyCreatedOutputSpec() != null) {
			text3 = fieldSimulationContext.getModel().getNumSpeciesContexts() + "";
			int numReactions = fieldSimulationContext.getModel().getNumReactions();
			numReactions += fieldSimulationContext.getModel().getRbmModelContainer().getReactionRuleList().size();
			text5 = numReactions + "";
			if(fieldSimulationContext.getModel().getRbmModelContainer().isEmpty()) {
				text4 = "N/A (rule based model needed)";
				text6 = "N/A (rule based model needed)";
			} else {
				text4 = fieldSimulationContext.getMostRecentlyCreatedOutputSpec().getBNGSpecies().length + "";
				text6 = fieldSimulationContext.getMostRecentlyCreatedOutputSpec().getBNGReactions().length + "";
			}
			if(fieldSimulationContext.isInsufficientIterations()) {
				text7 = "<font color=#8C001A>" + SimulationContext.IssueInsufficientIterations + "</font>";
			} else if(fieldSimulationContext.isInsufficientMaxMolecules()) {
				text7 = "<font color=#8C001A>" + SimulationContext.IssueInsufficientMolecules + "</font>";
			}

		} else {
			text3 = fieldSimulationContext.getModel().getNumSpeciesContexts() + "";
			int numReactions = fieldSimulationContext.getModel().getNumReactions();
			numReactions += fieldSimulationContext.getModel().getRbmModelContainer().getReactionRuleList().size();
			text5 = numReactions + "";
			if(fieldSimulationContext.getModel().getRbmModelContainer().isEmpty()) {
				text4 = "N/A (rule based model needed)";
				text6 = "N/A (rule based model needed)";
			} else {
				text4 = "unavailable";
				text6 = "unavailable";
			}
		}
		seedSpeciesLabel.setText(SpeciesContext.typeName + ": " + text3);
		generatedSpeciesLabel.setText("Species: " + text4);
		reactionRulesLabel.setText("Reaction Rules: " + text5);
		generatedReactionsLabel.setText("Reactions: " + text6);
		somethingInsufficientLabel.setText("<html>Warning:  " + text7 + "</html>");
		
		if(fieldSimulationContext.getModel().getRbmModelContainer().isEmpty()) {
			viewGeneratedSpeciesButton.setEnabled(false);
			viewGeneratedReactionsButton.setEnabled(false);
			viewObservablesMapButton.setEnabled(false);
			refreshMathButton.setEnabled(false);
			createModelButton.setEnabled(false);
			TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.Clean, "");
			fieldSimulationContext.appendToConsole(tcm);
		} else {
			if(fieldSimulationContext.getMostRecentlyCreatedOutputSpec()!= null && fieldSimulationContext.getMostRecentlyCreatedOutputSpec().getBNGSpecies().length > 0) {
				viewGeneratedSpeciesButton.setEnabled(true);
			} else {
				viewGeneratedSpeciesButton.setEnabled(false);
			}
			if(fieldSimulationContext.getMostRecentlyCreatedOutputSpec()!= null && fieldSimulationContext.getMostRecentlyCreatedOutputSpec().getBNGReactions().length > 0) {
				viewGeneratedReactionsButton.setEnabled(true);
			} else {
				viewGeneratedReactionsButton.setEnabled(false);
			}
			if(fieldSimulationContext.getMostRecentlyCreatedOutputSpec()!= null && fieldSimulationContext.getMostRecentlyCreatedOutputSpec().getObservableGroups().length > 0) {
				viewObservablesMapButton.setEnabled(true);
			} else {
				viewObservablesMapButton.setEnabled(false);
			}
			if(fieldSimulationContext.getMostRecentlyCreatedOutputSpec()!= null) {
				createModelButton.setEnabled(true);
			} else {
				createModelButton.setEnabled(false);
			}
			refreshMathButton.setEnabled(true);
		}
	}
	
	private void runBioNetGen() {
		EditConstraintsPanel panel = new EditConstraintsPanel(this);
		ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(this);
		ChildWindow childWindow = childWindowManager.addChildWindow(panel, panel, "Edit / Test Constraints");
		Dimension dim = new Dimension(320, 330);
		childWindow.pack();
		panel.setChildWindow(childWindow);
		childWindow.setPreferredSize(dim);
		childWindow.showModal();
		int maxIterations;
		int maxMolecules;
		int speciesLimit;
		int reactionsLimit;
		Map<MolecularType, Integer> testMaxStoichiometryMap = new LinkedHashMap<>();
		StoichiometryTableModel stoichiometryTableModel = panel.getStoichiometryTableModel();
		for(int row = 0; row < stoichiometryTableModel.getRowCount(); row++) {
			MaxStoichiometryEntity nce = stoichiometryTableModel.getValueAt(row);
			MolecularType key = nce.getMolecularType();
			Integer value = nce.getValue();
			testMaxStoichiometryMap.put(key, value);
		}
		if (panel.getButtonPushed() == ActionButtons.Run) {
			maxIterations = Integer.parseInt(panel.maxIterationTextField.getText());
			maxMolecules = Integer.parseInt(panel.maxMolTextField.getText());
			speciesLimit = Integer.parseInt(panel.speciesLimitTextField.getText());
			reactionsLimit = Integer.parseInt(panel.reactionsLimitTextField.getText());
			fieldSimulationContext.getNetworkConstraints().setTestConstraints(maxIterations, maxMolecules, 
					speciesLimit, reactionsLimit, testMaxStoichiometryMap);
		} else if(panel.getButtonPushed() == ActionButtons.Apply) {
			activateConsole();
			maxIterations = Integer.parseInt(panel.maxIterationTextField.getText());
			maxMolecules = Integer.parseInt(panel.maxMolTextField.getText());
			speciesLimit = Integer.parseInt(panel.speciesLimitTextField.getText());
			reactionsLimit = Integer.parseInt(panel.reactionsLimitTextField.getText());
			fieldSimulationContext.getNetworkConstraints().setTestConstraints(maxIterations, maxMolecules, 
					speciesLimit, reactionsLimit, testMaxStoichiometryMap);
			fieldSimulationContext.getNetworkConstraints().updateConstraintsFromTest();
			// apply will invalidate everything: generated species, reactions, console, cache, etc
			updateBioNetGenOutput(null);
			refreshInterface();
			TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.Clean, "");
			fieldSimulationContext.appendToConsole(tcm);
			String message = "Warning: The current Constraints are not tested / validated.";
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Warning, message);
			fieldSimulationContext.appendToConsole(tcm);
			message = "The Network generation may take a very long time or the generated network may be incomplete. "
					+ "We recommend testing this set of constraints.";
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Notification, message);
			fieldSimulationContext.appendToConsole(tcm);
			return;
		} else {
			// when cancel we put back in sync the test values
			maxIterations = fieldSimulationContext.getNetworkConstraints().getMaxIteration();
			maxMolecules = fieldSimulationContext.getNetworkConstraints().getMaxMoleculesPerSpecies();
			speciesLimit = fieldSimulationContext.getNetworkConstraints().getSpeciesLimit();
			reactionsLimit = fieldSimulationContext.getNetworkConstraints().getReactionsLimit();
			fieldSimulationContext.getNetworkConstraints().setTestConstraints(maxIterations, maxMolecules, 
					speciesLimit, reactionsLimit, testMaxStoichiometryMap);
			return;
		}
		
		// TODO: do not delete the commented code below
		// uncomment the next 6 lines to keep the data in the dialogs synchronized with the most recent reaction network
//		if(viewSpeciesDialog != null) {
//		viewSpeciesDialog.dispose();
//	}
//		if(viewReactionsDialog != null) {
//		viewReactionsDialog.dispose();
//	}

		activateConsole();
//		currentIterationSpecies = 0;
//		previousIterationSpecies = 0;
		synchronized (this) {
			fieldSimulationContext.setMd5hash(null);
			fieldSimulationContext.setMostRecentlyCreatedOutputSpec(null);
		}
		refreshInterface();
//		netGenConsoleText.setText("");
// no need to do this here because we do it below   			
//		TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.Clean, "");
//		fieldSimulationContext.appendToConsole(tcm);

		if(!checkBnglRequirements()) {
			return;
		}
		
		NetworkTransformer transformer = new NetworkTransformer();
		MathMappingCallback dummyCallback = new MathMappingCallback() {
			public void setProgressFraction(float percentDone) {}
			public void setMessage(String message) {}
			public boolean isInterrupted() { return false; }
		};
		String input = transformer.convertToBngl(fieldSimulationContext, true, dummyCallback, NetworkGenerationRequirements.ComputeFullNoTimeout);
		// we alter the input string to use the test values for speciesLimit and reactionsLimit
		BufferedReader br = new BufferedReader(new StringReader(input));
		StringBuilder sb = new StringBuilder();
		int lineNumber = 0;
		while (true) {
			String line = null;
			try {
				line = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line == null) {
				break;				// end of document
			}
			if(line.isEmpty()) {
				sb.append("\n");
				lineNumber++;
				continue;
			}
			if(line.contains(NetworkConstraints.SPECIES_LIMIT_PARAMETER)) {
				sb.append(NetworkConstraints.SPECIES_LIMIT_PARAMETER + "\t\t" + speciesLimit + "\n");
			} else if(line.contains(NetworkConstraints.REACTIONS_LIMIT_PARAMETER)) {
				sb.append(NetworkConstraints.REACTIONS_LIMIT_PARAMETER + "\t\t" + reactionsLimit + "\n");
			} else {
				sb.append(line + "\n");
			}
		}
		input = sb.toString();
		
		// we alter the input string to use the test values for max iterations and max molecules per species
		// get rid of the default generate network command...
		input = input.substring(0, input.indexOf("generate_network({"));
		// ... and replace it with the "fake" one
		StringWriter bnglStringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(bnglStringWriter);
		// testMaxStoichiometryMap
		RbmNetworkGenerator.generateNetworkEx(maxIterations, maxMolecules, true, pw, fieldSimulationContext.getModel().getRbmModelContainer(), fieldSimulationContext, NetworkGenerationRequirements.ComputeFullNoTimeout);
		String genNetStr = bnglStringWriter.toString();
		pw.close();
		input += genNetStr;
		
		BNGInput bngInput = new BNGInput(input);
		final BNGExecutorService bngService = BNGExecutorService.getInstance(bngInput, NetworkGenerationRequirements.NoTimeoutMS);
		bngService.registerBngUpdaterCallback(this);
		Hashtable<String, Object> hash = new Hashtable<String, Object>();

		AsynchClientTask[] tasksArray = new AsynchClientTask[3];
		TaskCallbackMessage message = new TaskCallbackMessage(TaskCallbackStatus.Clean, "");
		fieldSimulationContext.appendToConsole(message);
		tasksArray[0] = new RunBioNetGen(bngService);
		tasksArray[1] = new CreateBNGOutputSpec(bngService);
		tasksArray[2] = new ReturnBNGOutput(bngService, fieldSimulationContext, this);
		ClientTaskDispatcher.dispatch(this, hash, tasksArray, false, true, new ProgressDialogListener() {
			@Override
			public void cancelButton_actionPerformed(EventObject newEvent) {
				try {
					bngService.stopBNG();
					String s = "...user cancelled.";
					TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.TaskStopped, s);
					// message will be processed in TaskCallbackProcessor::case TaskStopped
					setNewCallbackMessage(tcm);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void viewGeneratedSpecies() {
		System.out.println("viewGeneratedSpecies button pressed");
		ViewGeneratedSpeciesPanel panel = new ViewGeneratedSpeciesPanel(this);
		panel.setSpecies(fieldSimulationContext.getMostRecentlyCreatedOutputSpec().getBNGSpecies());
		panel.setPreferredSize(new Dimension(800,550));

//		if(viewSpeciesDialog != null) {		// uncomment these 3 lines to allow only one instance of the dialog
//			viewSpeciesDialog.dispose();
//		}
		JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Close"});
		viewSpeciesDialog = pane.createDialog(this, "View Generated Species");
		viewSpeciesDialog.setModal(false);
		viewSpeciesDialog.setResizable(true);
		viewSpeciesDialog.setVisible(true);
	}
	private void viewGeneratedReactions() {
		System.out.println("viewGeneratedReactions button pressed");
		
// TODO: use this to verify instance consistency before and after a save (when the old sim context and other stuff gets replaced with the new one
//			System.out.println("NetworkConstraintsPanel: " + "simContext: " + System.identityHashCode(fieldSimulationContext));
//			System.out.println("NetworkConstraintsPanel: " + "simContext: " + "TaskCallbackProcessor: "+ System.identityHashCode(fieldSimulationContext.getTaskCallbackProcessor()));
//			System.out.println("NetworkConstraintsPanel: " + "simContext: " + "TaskCallbackProcessor: "+ "simContext: " + System.identityHashCode(fieldSimulationContext.getTaskCallbackProcessor().getSimulationContext()));
//			System.out.println("NetworkConstraintsPanel: " + "simContext: " + "NetworkConstraints: " + System.identityHashCode(fieldSimulationContext.getNetworkConstraints()));
//			System.out.println("NetworkConstraintsPanel: " + "NC Table: " + System.identityHashCode(networkConstraintsTable));
//			System.out.println("NetworkConstraintsPanel: " + "NCTableModel: " + System.identityHashCode(networkConstraintsTableModel));
//			System.out.println("NetworkConstraintsPanel: " + "NCTableModel: " + "simContext: " + System.identityHashCode(networkConstraintsTableModel.getSimulationContext()));
		
		ViewGeneratedReactionsPanel panel = new ViewGeneratedReactionsPanel(this);
		final BNGOutputSpec mostRecentlyCreatedOutputSpec = fieldSimulationContext.getMostRecentlyCreatedOutputSpec();
		panel.setReactions(mostRecentlyCreatedOutputSpec.getBNGReactions());
		panel.setPreferredSize(new Dimension(800,550));
		
//		if(viewReactionsDialog != null) {
//			viewReactionsDialog.dispose();
//		}
		JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Close"});
		viewReactionsDialog = pane.createDialog(this, "View Generated Reactions");
		viewReactionsDialog.setModal(false);
		viewReactionsDialog.setResizable(true);
		viewReactionsDialog.setVisible(true);
	}
	private void viewObservablesMap() {
		System.out.println("viewGeneratedSpecies button pressed");
		ViewObservablesMapPanel panel = new ViewObservablesMapPanel(this);
		BNGSpecies[] bngSpecies = fieldSimulationContext.getMostRecentlyCreatedOutputSpec().getBNGSpecies();
		ObservableGroup[] observables = fieldSimulationContext.getMostRecentlyCreatedOutputSpec().getObservableGroups();
		panel.setData(bngSpecies, observables);
		panel.setPreferredSize(new Dimension(1000,600));

//		if(viewSpeciesDialog != null) {		// uncomment these 3 lines to allow only one instance of the dialog
//			viewSpeciesDialog.dispose();
//		}
		JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Close"});
		viewObservablesMapDialog = pane.createDialog(this, "View Observables Map");
		viewObservablesMapDialog.setModal(false);
		viewObservablesMapDialog.setResizable(true);
		viewObservablesMapDialog.setVisible(true);
	}

	
	private void createModel() {
		if(!checkBnglRequirements()) {
			return;
		}

		DocumentWindow dw = (DocumentWindow) GeneralGuiUtils.findTypeParentOfComponent(this, DocumentWindow.class);
		BioModelWindowManager bmwm = (BioModelWindowManager)(dw.getTopLevelWindowManager());
		RequestManager rm = dw.getTopLevelWindowManager().getRequestManager();
			
		rm.createBioModelFromApplication(bmwm, "Test", fieldSimulationContext);
	}

	// before invoking bngl we check if there's at least one rule to flatten
	public boolean checkBnglRequirements() {
		if(!fieldSimulationContext.getGeometryContext().getModel().getRbmModelContainer().hasRules()) {
			DialogUtils.showInfoDialog(this, "Unable to Create Reaction Network", "At least one reaction rule is required.");
			return false;
		}
		
		boolean foundEnabledRule = false;
		for (ReactionRuleSpec rrs : fieldSimulationContext.getReactionContext().getReactionRuleSpecs()){
			if (!rrs.isExcluded()){
				foundEnabledRule = true;
				break;
			}
		}
		if(!foundEnabledRule) {
			DialogUtils.showInfoDialog(this, "Unable to Create Reaction Network", "At least one reaction rule enabled for this application is required.");
			return false;
		}
		return true;
	}

	@Override
	public boolean isInterrupted() {
		return false;
	}
	@Override		// for testing/debugging purposes
	public void setVisible(boolean bVisible) {
		super.setVisible(bVisible);
	}
	
	public void activateConsole() {
		boolean found = false;
		Container parent = getParent();
		while(parent != null) {
			parent = parent.getParent();
			if(parent instanceof BioModelEditor) {
				found = true;
				break;
			}
		}
		if(found) {
			System.out.println("Parent Found");
			BioModelEditor e = (BioModelEditor)parent;
//			e.getRightBottomTabbedPane().setSelectedComponent(e.getSimulationConsolePanel());
			Component[] cList = e.getRightBottomTabbedPane().getComponents();
			for(Component c : cList) {
				if(c instanceof SimulationConsolePanel) {
					e.getRightBottomTabbedPane().setSelectedComponent(c);
					break;
				}
			}
		}
	}
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {

	}

}
/*
if (!SwingUtilities.isEventDispatchThread()){
	SwingUtilities.invokeLater(new Runnable(){

		@Override
		public void run() {
			appendToConsole(newCallbackMessage);
		}
		
	});
}else{
	appendToConsole(newCallbackMessage);
}
*/
