package org.vcell.model.rbm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.vcell.util.ProgressDialogListener;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.CreateBNGOutputSpec;
import cbit.vcell.client.task.ReturnBNGOutput;
import cbit.vcell.client.task.RunBioNetGen;
import cbit.vcell.graph.ReactionCartoonEditorPanel;
import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.NetworkTransformer;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.server.bionetgen.BNGExecutorService;
import cbit.vcell.server.bionetgen.BNGInput;
import cbit.vcell.solvers.ApplicationMessage;

// we should use WindowBuilder Plugin (add it to Eclipse IDE) to speed up panel design
// can choose absolute layout and place everything exactly as we see fit
@SuppressWarnings("serial")
public class NetworkConstraintsPanel extends JPanel implements BioNetGenUpdaterCallback, ApplicationSpecificationsPanel.Specifier {
	BNGOutputSpec outputSpec = null;
	
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
	
	private JTextPane netGenConsoleText;
//	private EditorScrollTable molecularTypeTable = null;
//	private ApplicationMolecularTypeTableModel molecularTypeTableModel = null;
	private JButton refreshMathButton;
//	private JButton viewNetworkButton;
	
	private int currentIterationSpecies = 0;
	private int previousIterationSpecies = 0;
	
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
//			} else if(e.getSource() == getViewNetworkButton()) {
//				viewNetwork();
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
			refreshMathButton = new javax.swing.JButton(" Run BioNetGen ");
			refreshMathButton.setName("RefreshMathButton");
		}
		return refreshMathButton;
	}
//	private JButton getViewNetworkButton() {
//		if (viewNetworkButton == null) {
//			viewNetworkButton = new javax.swing.JButton(" View Network ");
//			viewNetworkButton.setName("ViewNetworkButton");
//		}
//		return viewNetworkButton;
//	}

	private void initialize() {
		netGenConsoleText = new JTextPane();
		maxIterationTextField = new JTextField();
		maxMolTextField = new JTextField();
		seedSpeciesLabel = new JLabel();
		reactionRulesLabel = new JLabel();
		generatedSpeciesLabel = new JLabel();
		generatedReactionsLabel = new JLabel();
//		molecularTypeTable = new EditorScrollTable();
//		molecularTypeTableModel = new ApplicationMolecularTypeTableModel(molecularTypeTable);
		
		maxIterationTextField.addActionListener(eventHandler);
		maxMolTextField.addActionListener(eventHandler);
		
		getViewGeneratedSpeciesButton().addActionListener(eventHandler);
		getViewGeneratedReactionsButton().addActionListener(eventHandler);
		getRefreshMathButton().addActionListener(eventHandler);
//		getViewNetworkButton().addActionListener(eventHandler);
		
		netGenConsoleText.addFocusListener(eventHandler);
		maxIterationTextField.addFocusListener(eventHandler);
		maxMolTextField.addFocusListener(eventHandler);
		
		// ------------------------------------------- The 2 group boxes --------------------------
		JPanel leftPanel = new JPanel();
		JPanel rightPanel = new JPanel();

		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();

		TitledBorder titleLeft = BorderFactory.createTitledBorder(loweredEtchedBorder, " Rules ");
		titleLeft.setTitleJustification(TitledBorder.LEFT);
		titleLeft.setTitlePosition(TitledBorder.TOP);

		TitledBorder titleRight = BorderFactory.createTitledBorder(loweredEtchedBorder, " Networking ");
		titleRight.setTitleJustification(TitledBorder.LEFT);
		titleRight.setTitlePosition(TitledBorder.TOP);
		
		leftPanel.setBorder(titleLeft);
		rightPanel.setBorder(titleRight);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 3, 2, 1);
		add(leftPanel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 1, 2, 3);	//  top, left, bottom, right 
		add(rightPanel, gbc);

		// ------------------------------------------- Populating the left group box ---------------
		JPanel top = new JPanel();
		JPanel bottom = new JPanel();
		
		leftPanel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(top, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		leftPanel.add(bottom, gbc);
		
		top.setLayout(new GridBagLayout());		// --- top
		int gridy = 0;
		gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(15, 10, 4, 4);
		top.add(seedSpeciesLabel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.insets = new Insets(15, 4, 4, 10);
		top.add(reactionRulesLabel, gbc);
		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(12, 10, 4, 4);
		top.add(new JLabel("Max Iteration"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(12, 4, 4, 10);
		top.add(maxIterationTextField, gbc);
		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(5, 10, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;
		top.add(new JLabel("Max Molecules/Species"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(5, 4, 4, 10);
		top.add(maxMolTextField, gbc);

		// we may want to use a scroll pane whose viewing area is the JTable to provide similar look with NetGen Console
//		bottom.setLayout(new GridBagLayout());		// --- bottom
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.weightx = gbc.weighty = 1.0;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.anchor = GridBagConstraints.NORTHWEST;
//		gbc.insets = new Insets(20, 4, 4, 10);
//		bottom.add(molecularTypeTable, gbc);
		
		// ------------------------------------------- Populating the right group box ------------
		top = new JPanel();
		bottom = new JPanel();
		
		// we use a scroll pane whose viewing area is the JTextArea - to provide the vertical scroll bar
		JScrollPane netGenConsoleScrollPane = new JScrollPane(netGenConsoleText);
		netGenConsoleScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		TitledBorder titleConsole = BorderFactory.createTitledBorder(loweredBevelBorder, " BioNetGen Console ");
		titleConsole.setTitleJustification(TitledBorder.LEFT);
		titleConsole.setTitlePosition(TitledBorder.ABOVE_TOP);
		netGenConsoleScrollPane.setBorder(titleConsole);
		
		rightPanel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		rightPanel.add(top, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		rightPanel.add(bottom, gbc);
		
		top.setLayout(new GridBagLayout());		// --- top
		gridy = 0;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 4, 4, 10);
		top.add(generatedSpeciesLabel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 4, 4, 10);
		top.add(getViewGeneratedSpeciesButton(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 4, 4, 10);
		top.add(getRefreshMathButton(), gbc);

		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 4, 4, 10);
		top.add(generatedReactionsLabel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 4, 4, 10);
		top.add(getViewGeneratedReactionsButton(), gbc);
		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 2;
//		gbc.gridy = gridy;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 4, 4, 10);
//		top.add(getViewNetworkButton(), gbc);

		bottom.setLayout(new GridBagLayout());		// --- bottom
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = gbc.weighty = 1.0;			// get all the available space
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(20, 4, 4, 10);
		bottom.add(netGenConsoleScrollPane, gbc);
		
		netGenConsoleText.setFont(new Font("monospaced", Font.PLAIN, 11));
		netGenConsoleText.setEditable(false);
	}
	
	/*
	public void setNetworkConstraints(NetworkConstraints newValue) {
		if (networkConstraints == newValue) {
			return;
		}
		networkConstraints = newValue;
		refreshInterface();
	}
	*/
	
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
		this.outputSpec = outputSpec;
		refreshInterface();
		if(outputSpec == null || outputSpec.getBNGSpecies() == null || outputSpec.getBNGReactions() == null) {
			return;
		}
		if(outputSpec.getBNGSpecies().length > NetworkTransformer.speciesLimit) {
			String message = NetworkTransformer.getSpeciesLimitExceededMessage(outputSpec);
			appendToConsole(message);
		}
		if(outputSpec.getBNGReactions().length > NetworkTransformer.reactionsLimit) {
			String message = NetworkTransformer.getReactionsLimitExceededMessage(outputSpec);
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
		StyledDocument doc = netGenConsoleText.getStyledDocument();
		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, Color.RED);
		try {
		doc.insertString(doc.getLength(), string + "\n", keyWord);
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	private void appendToConsole(TaskCallbackMessage newCallbackMessage) {
		TaskCallbackStatus status = newCallbackMessage.getStatus();
		String string = newCallbackMessage.getText();
		StyledDocument doc = netGenConsoleText.getStyledDocument();
		try {
		switch(status) {
		case TaskStart:
//			netGenConsoleText.append(string + "\n");
			doc.insertString(doc.getLength(), string + "\n", null);
			break;
		case TaskEnd:
//			netGenConsoleText.append(string + "\n");
			doc.insertString(doc.getLength(), string + "\n", null);
			if(previousIterationSpecies>0 && currentIterationSpecies>0 && currentIterationSpecies!=previousIterationSpecies) {
				String s = "Warning: Max Iterations number may be insufficient.";
//				netGenConsoleText.append(s + "\n");
				SimpleAttributeSet keyWord = new SimpleAttributeSet();
				StyleConstants.setForeground(keyWord, Color.RED);
				doc.insertString(doc.getLength(), s + "\n", keyWord);
			}
			break;
		case TaskStopped:
//			netGenConsoleText.append("  " + string + "\n");
			SimpleAttributeSet keyWord = new SimpleAttributeSet();
			StyleConstants.setForeground(keyWord, Color.RED);
//			StyleConstants.setBackground(keyWord, Color.YELLOW);
			StyleConstants.setBold(keyWord, true);
			doc.insertString(doc.getLength(), "  " + string + "\n", keyWord);
			
			previousIterationSpecies = 0;	// we can't evaluate the max iterations anymore
			currentIterationSpecies = 0;
			break;
		case Detail:
			String split[];
			split = string.split("\\n");
			for(String s : split) {
				if(s.startsWith("CPU TIME: total"))  {
//					netGenConsoleText.append("  " + s + "\n");
					doc.insertString(doc.getLength(), "  " + s + "\n", null);
				} else if (s.startsWith("Iteration")) {
					String species = "species";
					s = "    " + s.substring(0, s.indexOf("species") + species.length());
//					netGenConsoleText.append(s + "\n");
					doc.insertString(doc.getLength(), s + "\n", null);
					checkMaxIterationConsistency(s);
				}
			}
			break;
		default:
			break;
		}
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	private void checkMaxIterationConsistency(String s) {
		Pattern pattern = Pattern.compile("\\w+");
		Matcher matcher = pattern.matcher(s);
		try {
		for(int i=0; matcher.find(); i++) {
			if(i==2) {
				previousIterationSpecies = currentIterationSpecies;
				currentIterationSpecies = Integer.parseInt(matcher.group());
			}
		}
		} catch(NumberFormatException nfe) {
			
		}

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
		if(outputSpec != null) {
			text3 = fieldSimulationContext.getModel().getNumSpeciesContexts() + "";
			int numReactions = fieldSimulationContext.getModel().getNumReactions();
			numReactions += fieldSimulationContext.getModel().getRbmModelContainer().getReactionRuleList().size();
			text5 = numReactions + "";
			if(fieldSimulationContext.getModel().getRbmModelContainer().isEmpty()) {
				text4 = "N/A (rule based model needed)";
				text6 = "N/A (rule based model needed)";
			} else {
				text4 = outputSpec.getBNGSpecies().length + "";
				text6 = outputSpec.getBNGReactions().length + "";
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
//			viewNetworkButton.setEnabled(false);
			netGenConsoleText.setText("");
		} else {
			if(outputSpec!= null && outputSpec.getBNGSpecies().length > 0) {
				viewGeneratedSpeciesButton.setEnabled(true);
			} else {
				viewGeneratedSpeciesButton.setEnabled(false);
			}
			if(outputSpec!= null && outputSpec.getBNGReactions().length > 0) {
				viewGeneratedReactionsButton.setEnabled(true);
			} else {
				viewGeneratedReactionsButton.setEnabled(false);
			}
			refreshMathButton.setEnabled(true);
//			viewNetworkButton.setEnabled(true);
		}
	}
	
	private void runBioNetGen() {
		currentIterationSpecies = 0;
		previousIterationSpecies = 0;
		outputSpec = null;
		refreshInterface();
		netGenConsoleText.setText("");

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
		panel.setSpecies(outputSpec.getBNGSpecies());
		panel.setPreferredSize(new Dimension(800,550));
		DialogUtils.showComponentCloseDialog(this, panel, "View Generated Species");
	}
	private void viewGeneratedReactions() {
		System.out.println("viewGeneratedReactions button pressed");
		ViewGeneratedReactionsPanel panel = new ViewGeneratedReactionsPanel(this);
		panel.setReactions(outputSpec.getBNGReactions());
		panel.setPreferredSize(new Dimension(800,550));
		DialogUtils.showComponentCloseDialog(this, panel, "View Generated Reactions");
	}
	private void viewNetwork() {
		try {
			ReactionCartoonEditorPanel reactionCartoonEditorPanel;
			reactionCartoonEditorPanel = new ReactionCartoonEditorPanel();
			reactionCartoonEditorPanel.setSize(300, 100);
			
			MathMapping mm = fieldSimulationContext.getMostRecentlyCreatedMathMapping();
			reactionCartoonEditorPanel.setModel(mm.getSimulationContext().getModel());
			
			DialogUtils.showComponentCloseDialog(this, reactionCartoonEditorPanel, "Flattened reaction diagram.");
		} catch (Throwable exception) {
			System.err.println("Exception occurred viewing Network");
			exception.printStackTrace(System.out);
		}
	}
	@Override
	public boolean isInterrupted() {
		// TODO Auto-generated method stub
		return false;
	}

}
