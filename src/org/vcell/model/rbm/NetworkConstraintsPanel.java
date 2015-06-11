package org.vcell.model.rbm;

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
import java.beans.PropertyVetoException;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.model.rbm.common.NetworkConstraintsEntity;
import org.vcell.util.BeanUtils;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.BioModelEditor;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.RbmTableRenderer;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.desktop.biomodel.SimulationConsolePanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.CreateBNGOutputSpec;
import cbit.vcell.client.task.ReturnBNGOutput;
import cbit.vcell.client.task.RunBioNetGen;
import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.NetworkTransformer;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.mapping.gui.NetworkConstraintsTableModel;
import cbit.vcell.math.MathException;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.server.bionetgen.BNGExecutorService;
import cbit.vcell.server.bionetgen.BNGInput;
import cbit.vcell.solvers.ApplicationMessage;

// we should use WindowBuilder Plugin (add it to Eclipse IDE) to speed up panel design
// can choose absolute layout and place everything exactly as we see fit
@SuppressWarnings("serial")
public class NetworkConstraintsPanel extends JPanel implements BioNetGenUpdaterCallback, ApplicationSpecificationsPanel.Specifier {

	private JTextField maxIterationTextField;
	private JTextField maxMolTextField;
	private NetworkConstraints networkConstraints;
	private EventHandler eventHandler = new EventHandler();
	private SimulationContext fieldSimulationContext;
	private IssueManager fieldIssueManager;
	private SelectionManager fieldSelectionManager;
	
	private JLabel seedSpeciesLabel;
	private JLabel reactionRulesLabel;
	private JLabel generatedSpeciesLabel;
	private JLabel generatedReactionsLabel;
	private JButton viewGeneratedSpeciesButton;
	private JButton viewGeneratedReactionsButton;
	
	private EditorScrollTable networkConstraintsTable = null;
	private NetworkConstraintsTableModel networkConstraintsTableModel = null;
	private JButton refreshMathButton;
	private JButton createModelButton;
	
	private class EventHandler implements FocusListener, ActionListener, PropertyChangeListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == maxIterationTextField) {
				changeMaxIteration();
			} else if (e.getSource() == maxMolTextField) {
				changeMaxMolPerSpecies();
			} else if(e.getSource() == getViewGeneratedSpeciesButton()) {
				viewGeneratedSpecies();
			} else if(e.getSource() == getViewGeneratedReactionsButton()) {
				viewGeneratedReactions();
			} else if(e.getSource() == getRefreshMathButton()) {
				runBioNetGen();
			} else if(e.getSource() == getCreateModelButton()) {
				createModel();
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
	private JButton getRefreshMathButton() {
		if (refreshMathButton == null) {
			refreshMathButton = new javax.swing.JButton(" Test Run BioNetGen ");
			refreshMathButton.setName("RefreshMathButton");
		}
		return refreshMathButton;
	}
	private JButton getCreateModelButton() {
		if (createModelButton == null) {
			createModelButton = new javax.swing.JButton(" Create Model from Network ");
			createModelButton.setName("CreateModelButton");
		}
		return createModelButton;
	}

	private void initialize() {
		maxIterationTextField = new JTextField();
		maxMolTextField = new JTextField();
		seedSpeciesLabel = new JLabel();
		reactionRulesLabel = new JLabel();
		generatedSpeciesLabel = new JLabel();
		generatedReactionsLabel = new JLabel();
		
		networkConstraintsTable = new EditorScrollTable();
		networkConstraintsTableModel = new NetworkConstraintsTableModel(networkConstraintsTable);
		networkConstraintsTable.setModel(networkConstraintsTableModel);
		
		maxIterationTextField.addActionListener(eventHandler);
		maxMolTextField.addActionListener(eventHandler);
		
		getViewGeneratedSpeciesButton().addActionListener(eventHandler);
		getViewGeneratedReactionsButton().addActionListener(eventHandler);
		getRefreshMathButton().addActionListener(eventHandler);
		getCreateModelButton().addActionListener(eventHandler);
		
		maxIterationTextField.addFocusListener(eventHandler);
		maxMolTextField.addFocusListener(eventHandler);
		
		// ------------------------------------------- The 2 group boxes --------------------------
		JPanel leftPanel = new JPanel();
//		JPanel rightPanel = new JPanel();

		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();

		TitledBorder titleTop = BorderFactory.createTitledBorder(loweredEtchedBorder, " Network Constraints ");
		titleTop.setTitleJustification(TitledBorder.LEFT);
		titleTop.setTitlePosition(TitledBorder.TOP);

		TitledBorder titleBottom = BorderFactory.createTitledBorder(loweredEtchedBorder, " Generated Network ");
		titleBottom.setTitleJustification(TitledBorder.LEFT);
		titleBottom.setTitlePosition(TitledBorder.TOP);
		
//		leftPanel.setBorder(titleLeft);
//		rightPanel.setBorder(titleRight);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 3, 2, 1);
		add(leftPanel, gbc);
		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.weightx = 1;
//		gbc.weighty = 1;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(5, 1, 2, 3);	//  top, left, bottom, right 
//		add(rightPanel, gbc);

		// ------------------------------------------- Populating the left group box ---------------
		JPanel top = new JPanel();
		JPanel bottom = new JPanel();
		
		top.setBorder(titleTop);
		bottom.setBorder(titleBottom);
		
		leftPanel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
		leftPanel.add(top, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		leftPanel.add(bottom, gbc);
		
//		top.setLayout(new GridBagLayout());		// --- top
//		int gridy = 0;
//		gbc = new GridBagConstraints();
//		
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.anchor = GridBagConstraints.NORTHWEST;
//		gbc.insets = new Insets(15, 10, 4, 4);
//		top.add(seedSpeciesLabel, gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.anchor = GridBagConstraints.NORTHEAST;
//		gbc.insets = new Insets(15, 4, 4, 10);
//		top.add(reactionRulesLabel, gbc);
//		
//		gridy++;
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = gridy;
//		gbc.anchor = GridBagConstraints.LINE_START;
//		gbc.insets = new Insets(12, 10, 4, 4);
//		top.add(new JLabel("Max Iteration"), gbc);
//		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.anchor = GridBagConstraints.LINE_END;
//		gbc.insets = new Insets(12, 4, 4, 10);
//		top.add(maxIterationTextField, gbc);
//		
//		gridy++;
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = gridy;
//		gbc.insets = new Insets(5, 10, 4, 4);
//		gbc.anchor = GridBagConstraints.LINE_START;
//		top.add(new JLabel("Max Molecules/Species"), gbc);
//		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.anchor = GridBagConstraints.LINE_END;
//		gbc.insets = new Insets(5, 4, 4, 10);
//		top.add(maxMolTextField, gbc);

		// we may want to use a scroll pane whose viewing area is the JTable to provide similar look with NetGen Console
		JScrollPane p = new JScrollPane(networkConstraintsTable);
		p.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//		TitledBorder ttt = BorderFactory.createTitledBorder(loweredBevelBorder, " Network Constraints ");
//		ttt.setTitleJustification(TitledBorder.LEFT);
//		ttt.setTitlePosition(TitledBorder.TOP);
//		p.setBorder(ttt);
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
//		top = new JPanel();
//		bottom = new JPanel();
//		
//		rightPanel.setLayout(new GridBagLayout());
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		rightPanel.add(top, gbc);
//		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 1;
//		gbc.weightx = 1;
//		gbc.weighty = 1;
//		gbc.fill = GridBagConstraints.BOTH;
//		rightPanel.add(bottom, gbc);
		
		bottom.setLayout(new GridBagLayout());		// --- top
		int gridy = 0;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 4, 4, 10);
		bottom.add(generatedSpeciesLabel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 4, 4, 10);
		bottom.add(getViewGeneratedSpeciesButton(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 4, 4, 10);
		bottom.add(getRefreshMathButton(), gbc);

		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 10);
		bottom.add(generatedReactionsLabel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 10);
		bottom.add(getViewGeneratedReactionsButton(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 10);
		bottom.add(getCreateModelButton(), gbc);
		
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
		networkConstraints = simulationContext.getModel().getRbmModelContainer().getNetworkConstraints();
		networkConstraintsTableModel.setSimulationContext(simulationContext);
		refreshInterface();
	}
	public SimulationContext getSimulationContext() {
		return fieldSimulationContext;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		fieldSelectionManager = selectionManager;
	}
	public void setIssueManager(IssueManager issueManager) {
		fieldIssueManager = issueManager;
	}
	
	@Override
	public void updateBioNetGenOutput(BNGOutputSpec outputSpec) {
		synchronized (this) {
			fieldSimulationContext.setMd5hash(null);
			fieldSimulationContext.setMostRecentlyCreatedOutputSpec(outputSpec);
		}
//		refreshInterface();
		if(outputSpec == null || outputSpec.getBNGSpecies() == null || outputSpec.getBNGReactions() == null) {
			return;
		}
		if(outputSpec.getBNGSpecies().length > SimulationConsolePanel.speciesLimit) {
			String message = SimulationConsolePanel.getSpeciesLimitExceededMessage(outputSpec);
			appendToConsole(message);
		}
		if(outputSpec.getBNGReactions().length > SimulationConsolePanel.reactionsLimit) {
			String message = SimulationConsolePanel.getReactionsLimitExceededMessage(outputSpec);
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
	
	private void refreshInterface() {
		String text1 = null;
		String text2 = null;
		if (networkConstraints != null) {
			text1 = networkConstraints.getMaxIteration() + "";
			text2 = networkConstraints.getMaxMoleculesPerSpecies() + "";
		}
		maxIterationTextField.setText(text1);
		maxMolTextField.setText(text2);
		
		String text3 = null;
		String text4 = null;
		String text5 = null;
		String text6 = null;
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
		} else {
			text3 = fieldSimulationContext.getModel().getNumSpeciesContexts() + "";
			int numReactions = fieldSimulationContext.getModel().getNumReactions();
			numReactions += fieldSimulationContext.getModel().getRbmModelContainer().getReactionRuleList().size();
			text5 = numReactions + "";
			if(fieldSimulationContext.getModel().getRbmModelContainer().isEmpty()) {
				text4 = "N/A (rule based model needed)";
				text6 = "N/A (rule based model needed)";
			} else {
//				text4 = "unavailable (no network)";
//				text6 = "unavailable (no network)";
				text4 = "unavailable";
				text6 = "unavailable";
			}
		}
		seedSpeciesLabel.setText("Seed Species: " + text3);
		generatedSpeciesLabel.setText("Generated Species: " + text4);
		reactionRulesLabel.setText("Reaction Rules: " + text5);
		generatedReactionsLabel.setText("Generated Reactions: " + text6);
		
		if(fieldSimulationContext.getModel().getRbmModelContainer().isEmpty()) {
			viewGeneratedSpeciesButton.setEnabled(false);
			viewGeneratedReactionsButton.setEnabled(false);
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
			refreshMathButton.setEnabled(true);
			createModelButton.setEnabled(true);
		}
	}
	
	private void runBioNetGen() {
		
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

		NetworkTransformer transformer = new NetworkTransformer();
		MathMappingCallback dummyCallback = new MathMappingCallback() {
			public void setProgressFraction(float percentDone) {}
			public void setMessage(String message) {}
			public boolean isInterrupted() { return false; }
		};
		String input = transformer.convertToBngl(fieldSimulationContext, true, dummyCallback, NetworkGenerationRequirements.ComputeFullNetwork);
		BNGInput bngInput = new BNGInput(input);
		final BNGExecutorService bngService = new BNGExecutorService(bngInput);
		bngService.registerBngUpdaterCallback(this);
		Hashtable<String, Object> hash = new Hashtable<String, Object>();

		AsynchClientTask[] tasksArray = new AsynchClientTask[3];
		TaskCallbackMessage message = new TaskCallbackMessage(TaskCallbackStatus.Clean, "");
		fieldSimulationContext.appendToConsole(message);
		tasksArray[0] = new RunBioNetGen(bngService);
		tasksArray[1] = new CreateBNGOutputSpec(bngService);
		tasksArray[2] = new ReturnBNGOutput(bngService);
		ClientTaskDispatcher.dispatch(this, hash, tasksArray, false, true, new ProgressDialogListener() {
			
			public void cancelButton_actionPerformed(EventObject newEvent) {
				try {
					bngService.stopBNG();
					String s = "...user cancelled.";
					TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.TaskStopped, s);
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
		DialogUtils.showComponentCloseDialog(this, panel, "View Generated Species");
	}
	private void viewGeneratedReactions() {
		System.out.println("viewGeneratedReactions button pressed");
		ViewGeneratedReactionsPanel panel = new ViewGeneratedReactionsPanel(this);
		panel.setReactions(fieldSimulationContext.getMostRecentlyCreatedOutputSpec().getBNGReactions());
		panel.setPreferredSize(new Dimension(800,550));
		DialogUtils.showComponentCloseDialog(this, panel, "View Generated Reactions");
	}
	private void createModel() {

		DocumentWindow dw = (DocumentWindow)BeanUtils.findTypeParentOfComponent(this, DocumentWindow.class);
		BioModelWindowManager bmwm = (BioModelWindowManager)(dw.getTopLevelWindowManager());
		RequestManager rm = dw.getTopLevelWindowManager().getRequestManager();
			
		rm.createBioModelFromApplication(bmwm, "Test", fieldSimulationContext);
	}
	@Override
	public boolean isInterrupted() {
		// TODO Auto-generated method stub
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


}
