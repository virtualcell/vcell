/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.simulation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;

import org.vcell.util.BeanUtils;
import org.vcell.util.NumberUtils;
import org.vcell.util.document.User;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;
import org.vcell.util.gui.SimpleUserMessage;
import org.vcell.util.gui.VCellIcons;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.ClientSimManager.ViewerType;
import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.biomodel.BioModelEditor;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.SimulationConsolePanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.GeometryOwner;
import cbit.vcell.graph.gui.ReactionCartoonEditorPanel;
import cbit.vcell.graph.gui.VisItShapeIcon;
import cbit.vcell.graph.gui.VisItShapeIcon.State;
import cbit.vcell.mapping.MathMappingCallbackTaskAdapter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.MathDescription;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SimulationOwner.UnitInfo;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverDescription.SolverFeature;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.SolverUtilities;
import thredds.wcs.GetCoverageRequest;
/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 3:41:07 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class SimulationListPanel extends DocumentEditorSubPanel {
	private static final Date FINITEVOLUME_CUTTOFF = getFiniteVolumeMissingDataRegenerateDate();

//	private static final String QUICK_RUN_PYTHON_TOOL_TIP = "Python Quick Run";
	private static final String QUICK_RUN_NATIVE_TOOL_TIP = "Native Quick Run";
	private static final String SERVER_RUN_TOOL_TIP = "Run and Save Simulation";
	
	private OutputFunctionsPanel outputFunctionsPanel;
	private JToolBar toolBar = null;
	private JButton ivjEditButton = null;
	private JButton copyButton = null;
	private JButton ivjNewButton = null;
	private JButton ivjNativeResultsButton = null;
	private JButton batchButton = null;

//	private JButton ivjPythonResultsButton = null;
	private JButton ivjRunButton = null;
	private JButton ivjDeleteButton = null;
//	private JButton particleViewButton = null;
	private JButton quickNativeRunButton = null;
//	private JButton quickPythonRunButton = null;
	private JSortTable ivjScrollPaneTable = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimulationListTableModel ivjSimulationListTableModel1 = null;
	private SimulationWorkspace fieldSimulationWorkspace = null;
	private JButton moreActionsButton = null;
	private JButton stopButton;
	private JButton statusDetailsButton;
	
	private JPanel massConservationModelReductionPanel = null;
	private JCheckBox massConservationModelReductionCheckBox = null;
	private JButton massConservationModelReductionHelpButton = null;

	private class IvjEventHandler implements java.awt.event.ActionListener,
		java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener, MouseListener {
		
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getNewButton()) {
				newSimulation(NetworkGenerationRequirements.AllowTruncatedStandardTimeout);
			} else if (e.getSource() == SimulationListPanel.this.getEditButton()) {
				editSimulation();
			} else if (e.getSource() == copyButton) {
				copySimulations();
			} else if (e.getSource() == getDeleteButton()) {
				deleteSimulations();
			} else if (e.getSource() == getRunButton()) {
				runSimulations();
			} else if (e.getSource() == stopButton) {
				stopSimulations();
			} else if (e.getSource() == batchButton) {
				DialogUtils.showInfoDialog(SimulationListPanel.this, "Under Construction");
				batchSimulations();
			} else if (e.getSource() == getNativeResultsButton()) {
				showSimulationResults(ViewerType.NativeViewer_only);
//			} else if (e.getSource() == getPythonResultsButton()) {
//				showSimulationResults(ViewerType.PythonViewer_only);
			} else if (e.getSource() == statusDetailsButton) {
				showSimulationStatusDetails();
//			} else if (e.getSource() == particleViewButton) {
//				particleView();
//			} else if (e.getSource() == quickPythonRunButton) {
//				quickRun(ViewerType.PythonViewer_only);
			} else if (e.getSource() == quickNativeRunButton) {
				quickRun(ViewerType.NativeViewer_only);
			} else if(e.getSource() == getMassConservationModelReductionCheckBox()) {
				if (fieldSimulationWorkspace != null && fieldSimulationWorkspace.getSimulationOwner() instanceof SimulationContext) {
					SimulationContext simulationContext = (SimulationContext)fieldSimulationWorkspace.getSimulationOwner();
					simulationContext.setUsingMassConservationModelReduction(getMassConservationModelReductionCheckBox().isSelected());
				}
			} else if(e.getSource() == getMassConservationModelReductionHelpButton()) {
				showHelp();
			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == fieldSimulationWorkspace && evt.getPropertyName().equals(SimulationWorkspace.PROPERTY_NAME_SIMULATION_STATUS)) {
				refreshButtonsLax();
			}
			if (evt.getSource() instanceof SimulationContext && evt.getPropertyName().equals(GeometryOwner.PROPERTY_NAME_GEOMETRY)) {
				if(fieldSimulationWorkspace != null && fieldSimulationWorkspace.getSimulationOwner() instanceof SimulationContext) {
					if(evt.getSource() == fieldSimulationWorkspace.getSimulationOwner()) {
						massConservationButtonAdjust();
					}
				}
//				massConservationButtonAdjust();
			}
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == getScrollPaneTable().getSelectionModel())
				tableSelectionChanged(e);

		}
		public void mouseClicked(MouseEvent e) {
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
	};

public SimulationListPanel() {
	super();
	initialize();
}

private static Date getFiniteVolumeMissingDataRegenerateDate(){
	try{
		return new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2015");
	}catch(Exception e){
		//never happen, but ignore if it does
		e.printStackTrace();
	}
	return null;
}

/**
 * connEtoC9:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> SimulationListPanel.refreshButtons()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
private void tableSelectionChanged(javax.swing.event.ListSelectionEvent arg1) {
	try {
		refreshButtonsLax();
		setSelectedObjectsFromTable(getScrollPaneTable(), getSimulationListTableModel1());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void showHelp() {
	 String message = "In earlier VCell versions, math generation in ODE compartmental Applications automatically ";
	 message += "reduced the system, using mass conservation, to solve the minimal number of ODEs for a minimal set of variables; ";
	 message += "the rest of the variables (i.e. species) were determined from algebraic mass conservation relations. \rAt the ";
	 message += "expense of slightly longer simulation times, users may now turn off variable reduction. This makes the generated ";
	 message += "math easier to understand and can avoid occasional numerical simulation issues.";
//	message = "<html>" + message + "</html>";
	PopupGenerator.showInfoDialog(this, "Variable reduction by mass conservation", message);
}

/**
 * Comment
 */
private void batchSimulations() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	if(selections.length != 1) {
		throw new RuntimeException("Exactly one template Simulation is required for Batch Creation");
	}
	
	Vector<Simulation> v = new Vector<Simulation>();
	v.add((Simulation)(ivjSimulationListTableModel1.getValueAt(selections[0])));
	Simulation[] toCopy = (Simulation[])BeanUtils.getArray(v, Simulation.class);
	int index = -1;
	
	Map<Integer, Map<String, String>> batchInputDataMap = new LinkedHashMap<>();
	parseBatchInputFile(batchInputDataMap);
	if(batchInputDataMap.isEmpty()) {
		throw new RuntimeException("Failed to read batch input data file");
	}
	
	try {
		index = getSimulationWorkspace().batchSimulations(toCopy, batchInputDataMap, this);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, exc.getMessage(), exc);
	}
	// set selection to the last copied one
	getScrollPaneTable().getSelectionModel().setSelectionInterval(index, index);
	getScrollPaneTable().scrollRectToVisible(getScrollPaneTable().getCellRect(index, 0, true));
}

private static void parseBatchInputFile(Map<Integer, Map<String, String>> batchInputDataMap) {
	
	StringBuffer stringBuffer = new StringBuffer();
	long batchInputFileLength = 0;
	
	try {
		File batchInputFile = new java.io.File("C:\\TEMP\\ddd\\batchSimulations.dat");
		if (!batchInputFile.exists()) {
			throw new java.io.FileNotFoundException("Batch input file " + batchInputFile.getPath() + " not found");
		}
		batchInputFileLength = batchInputFile.length();
	
		BufferedReader br = new BufferedReader(new FileReader(batchInputFile));
		char charArray[] = new char[100000];
		while (true) {
			int numRead = br.read(charArray, 0, charArray.length);
			if (numRead > 0) {
				stringBuffer.append(charArray,0,numRead);
			} else if (numRead == -1) {
				break;
			}
		}
		br.close();
	} catch (java.io.FileNotFoundException e1) {
		throw new RuntimeException("could not read batch simulation input .dat file : "+e1.getMessage());
	} catch (java.io.IOException e2) {
		throw new RuntimeException("could not read batch simulation input .dat file : "+e2.getMessage());
	}
		
	if (stringBuffer.length() != batchInputFileLength){
		System.err.println("SimulationListPanel, read "+stringBuffer.length()+" of "+batchInputFileLength+" bytes of input file");
	}
	String inputString = stringBuffer.toString();
	
	String newLineDelimiters = "\n\r";
	StringTokenizer lineTokenizer = new StringTokenizer(inputString, newLineDelimiters);
	String line = new String("");
	String entry = new String("");
	final String EntitiesDelimiter = " ,";
	final String EntityValueDelimiter = "=";
	Integer lineIndex = 0;

	while (lineTokenizer.hasMoreTokens()) {
		boolean badTokenFound = false;
		line = lineTokenizer.nextToken();

		Map<String, String> simOverridesMap = new LinkedHashMap<>();
		StringTokenizer nextLine = new StringTokenizer(line, EntitiesDelimiter);	// overrides for this simulation
		while (nextLine.hasMoreTokens()) {
			entry = nextLine.nextToken();
		
			StringTokenizer entryTokenizer = new StringTokenizer(entry, EntityValueDelimiter);	// one pair entity=value, ex: s1_init=21.17
			try {
				String entity = entryTokenizer.nextToken();
				String value = entryTokenizer.nextToken();
				simOverridesMap.put(entity, value);
			} catch(NoSuchElementException e) {
				badTokenFound = true;
				break;
			}
		}
		if(badTokenFound == false) {
			// we skip the simulations that have errors
			batchInputDataMap.put(lineIndex, simOverridesMap);
		}
		lineIndex++;
	}
}


private void copySimulations() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector<Simulation> v = new Vector<Simulation>();
	for (int i = 0; i < selections.length; i++){
		v.add((Simulation)(ivjSimulationListTableModel1.getValueAt(selections[i])));
	}
	Simulation[] toCopy = (Simulation[])BeanUtils.getArray(v, Simulation.class);
	int index = -1;
	try {
		index = getSimulationWorkspace().copySimulations(toCopy, this);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Could not copy all simulations\n"+exc.getMessage(), exc);
	}
	// set selection to the last copied one
	getScrollPaneTable().getSelectionModel().setSelectionInterval(index, index);
	getScrollPaneTable().scrollRectToVisible(getScrollPaneTable().getCellRect(index, 0, true));
}

/**
 * Comment
 */
private void deleteSimulations() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	StringBuilder simulationNames = new StringBuilder();
	ArrayList<Simulation> simList = new ArrayList<Simulation>();
//	Simulation[] allSims = getSimulationWorkspace().getSimulations();
	for (int i = 0; i < selections.length; i++){
		Simulation sim = (Simulation)(ivjSimulationListTableModel1.getValueAt(selections[i]));
		SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(sim);
		if (!simStatus.isRunning()){
			simList.add(sim);
			simulationNames.append(sim.getName() + "\n");
		}
	}
	if (simList.size() == 0) {
		return;
	}

	String confirm = DialogUtils.showOKCancelWarningDialog(this, "Deleting", "You are going to delete the following simulation(s):\n\n" + simulationNames + "\n Continue?");
	if (confirm.equals(UserMessage.OPTION_CANCEL)) {
		return;
	}
	Simulation[] toDelete = (Simulation[])BeanUtils.getArray(simList, Simulation.class);
	try {
		getSimulationWorkspace().deleteSimulations(toDelete);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Could not delete all simulations\n"+exc.getMessage(), exc);
	}
	// unset selection - may not be needed...
	getScrollPaneTable().clearSelection();
}


/**
 * Comment
 */
private void editSimulation() {
	// this should not be possible to call unless exactly one row is selected, but check anyway
	int[] selectedRows = getScrollPaneTable().getSelectedRows();
	if (selectedRows.length > 0) { // make sure something is selected...
		Simulation sim = (Simulation)(ivjSimulationListTableModel1.getValueAt(selectedRows[0]));
		SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(sim);
		if (!simStatus.isRunning()){
			SimulationWorkspace.editSimulation(this, getSimulationWorkspace().getSimulationOwner(), sim); // just the first one if more than one selected...
			refreshButtonsLax(); //need to check if parallel option changed
		}
	}
}

/**
 * Return the ButtonPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToolBar getToolBar() {
	if (toolBar == null) {
		try {
			toolBar = new javax.swing.JToolBar();
			toolBar.setFloatable(false);

			copyButton = new JButton("", VCellIcons.copySimIcon);
			copyButton.setToolTipText("Copy Simulation");
			copyButton.addActionListener(ivjEventHandler);
			batchButton = new JButton("", VCellIcons.copySimIcon);
			batchButton.setToolTipText("Batch Simulation");
			batchButton.addActionListener(ivjEventHandler);
			stopButton = new JButton("", VCellIcons.stopSimIcon);
			stopButton.setToolTipText("Stop Simulation");
			stopButton.setEnabled(false);
			stopButton.addActionListener(ivjEventHandler);
			statusDetailsButton = new JButton("", VCellIcons.statusDetailscon);
			statusDetailsButton.setToolTipText("Simulation Status Details...");
			statusDetailsButton.addActionListener(ivjEventHandler);
			/*
			particleViewButton = new JButton("", VCellIcons.particleRunSimIcon);
			particleViewButton.setToolTipText("Real-Time Particle View");
			particleViewButton.addActionListener(ivjEventHandler);
			*/
//			quickPythonRunButton = new JButton("", VCellIcons.pythonQuickRunIcon);
//			quickPythonRunButton.setToolTipText(QUICK_RUN_PYTHON_TOOL_TIP);
//			quickPythonRunButton.addActionListener(ivjEventHandler);

			quickNativeRunButton = new JButton("", VCellIcons.odeQuickRunIcon);
			quickNativeRunButton.setToolTipText(QUICK_RUN_NATIVE_TOOL_TIP);
			quickNativeRunButton.addActionListener(ivjEventHandler);

			toolBar.addSeparator();
			toolBar.add(getNewButton());
			toolBar.add(copyButton);
			toolBar.add(getEditButton());
			toolBar.add(getDeleteButton());
			toolBar.addSeparator();
			toolBar.add(batchButton);
			toolBar.addSeparator();
			
			toolBar.add(getMassConservationModelReductionPanel());
			toolBar.add(Box.createHorizontalGlue());

			
			toolBar.add(getRunButton());
			toolBar.add(stopButton);
//			toolBar.add(getPythonResultsButton());
			toolBar.add(getNativeResultsButton());
			toolBar.add(statusDetailsButton);
			toolBar.addSeparator();
//			toolBar.add(quickPythonRunButton);
			toolBar.add(quickNativeRunButton);
//			toolBar.add(particleViewButton);

			ReactionCartoonEditorPanel.setToolBarButtonSizes(getNewButton());
			ReactionCartoonEditorPanel.setToolBarButtonSizes(batchButton);
			ReactionCartoonEditorPanel.setToolBarButtonSizes(copyButton);
			ReactionCartoonEditorPanel.setToolBarButtonSizes(getEditButton());
			ReactionCartoonEditorPanel.setToolBarButtonSizes(getDeleteButton());
			ReactionCartoonEditorPanel.setToolBarButtonSizes(getRunButton());
			ReactionCartoonEditorPanel.setToolBarButtonSizes(stopButton);
//			ReactionCartoonEditorPanel.setToolBarButtonSizes(getPythonResultsButton());
			ReactionCartoonEditorPanel.setToolBarButtonSizes(getNativeResultsButton());
			ReactionCartoonEditorPanel.setToolBarButtonSizes(statusDetailsButton);
//			ReactionCartoonEditorPanel.setToolBarButtonSizes(particleViewButton);
//			ReactionCartoonEditorPanel.setToolBarButtonSizes(quickPythonRunButton);
			ReactionCartoonEditorPanel.setToolBarButtonSizes(quickNativeRunButton);

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return toolBar;
}

private JCheckBox getMassConservationModelReductionCheckBox() {
	if(massConservationModelReductionCheckBox == null) {
		massConservationModelReductionCheckBox = new JCheckBox("Variable Reduction");
		massConservationModelReductionCheckBox.setToolTipText("Toggle ODE variable reduction by mass conservation.");
		if(fieldSimulationWorkspace != null && fieldSimulationWorkspace.getSimulationOwner() instanceof SimulationContext) {
			SimulationContext sc = (SimulationContext)fieldSimulationWorkspace.getSimulationOwner();
			massConservationModelReductionCheckBox.setSelected(sc.isUsingMassConservationModelReduction());
		} else {
			massConservationModelReductionCheckBox.setSelected(false);
		}
	}
	return massConservationModelReductionCheckBox;
}
private JButton getMassConservationModelReductionHelpButton() {
	if(massConservationModelReductionHelpButton == null) {
		massConservationModelReductionHelpButton = new JButton("  ?  ");
		massConservationModelReductionHelpButton.setVisible(true);
		Font font = massConservationModelReductionHelpButton.getFont().deriveFont(Font.BOLD);
		Border border = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		massConservationModelReductionHelpButton.setFont(font);
		massConservationModelReductionHelpButton.setBorder(border);
		massConservationModelReductionHelpButton.setFocusPainted(false);
		massConservationModelReductionHelpButton.setFocusable(false);
		massConservationModelReductionHelpButton.setToolTipText("Variable reduction by mass conservation help.");
	}
	return massConservationModelReductionHelpButton;
}

private JPanel getMassConservationModelReductionPanel() {
	if(massConservationModelReductionPanel == null) {
		massConservationModelReductionPanel = new JPanel();
		massConservationModelReductionPanel.setName("speciesOptionsPanel");
		massConservationModelReductionPanel.setLayout(new GridBagLayout());
		massConservationModelReductionPanel.setSize(60, ReactionCartoonEditorPanel.TOOL_BAR_BUTTON_SIZE.height);
//		massConservationModelReductionPanel.setBackground(Color.yellow);
		
		int gridx = 0;
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		massConservationModelReductionPanel.add(new JLabel(""), gbc);

		gridx++;
		gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 0, 0);
		gbc.weightx = 0;
		massConservationModelReductionPanel.add(getMassConservationModelReductionCheckBox(), gbc);
		
		gridx++;
		gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weightx = 0;
		massConservationModelReductionPanel.add(getMassConservationModelReductionHelpButton(), gbc);

		gridx++;
		gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		massConservationModelReductionPanel.add(new JLabel(""), gbc);
	}
	return massConservationModelReductionPanel;
}

/**
 * Return the EditButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getEditButton() {
	if (ivjEditButton == null) {
		try {
			ivjEditButton = new javax.swing.JButton("", VCellIcons.editSimIcon);
			ivjEditButton.setName("EditButton");
			ivjEditButton.setToolTipText("Edit Simulation");
			ivjEditButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditButton;
}

/**
 * Return the NewButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getNewButton() {
	if (ivjNewButton == null) {
		try {
			ivjNewButton = new javax.swing.JButton("", VCellIcons.newSimIcon);
			ivjNewButton.setName("NewButton");
			ivjNewButton.setToolTipText("New Simulation");
			ivjNewButton.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNewButton;
}


/**
 * Return the ResultsButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getNativeResultsButton() {
	if (ivjNativeResultsButton == null) {
		try {
			ivjNativeResultsButton = new javax.swing.JButton("", VCellIcons.resultsIcon);
			ivjNativeResultsButton.setName("NativeResultsButton");
			ivjNativeResultsButton.setToolTipText("Native Simulation Results");
			ivjNativeResultsButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNativeResultsButton;
}


//private javax.swing.JButton getPythonResultsButton() {
//	if (ivjPythonResultsButton == null) {
//		try {
//			Icon iconNormal = new VisItShapeIcon(State.enabled);
//			Icon iconDisabled = new VisItShapeIcon(State.disabled);
//			ivjPythonResultsButton = new javax.swing.JButton("");
//			ivjPythonResultsButton.setName("PythonResultsButton");
//			ivjPythonResultsButton.setIcon(iconNormal);
//			ivjPythonResultsButton.setSelectedIcon(iconNormal);
//			ivjPythonResultsButton.setDisabledIcon(iconDisabled);
//			ivjPythonResultsButton.setToolTipText("Python Simulation Results");
//			ivjPythonResultsButton.setEnabled(false);
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjPythonResultsButton;
//}


/**
 * Return the RunButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRunButton() {
	if (ivjRunButton == null) {
		try {
			ivjRunButton = new javax.swing.JButton("", VCellIcons.runSimIcon);
			ivjRunButton.setName("RunButton");
			ivjRunButton.setToolTipText(SERVER_RUN_TOOL_TIP);
			ivjRunButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunButton;
}

private javax.swing.JButton getDeleteButton() {
	if (ivjDeleteButton == null) {
		try {
			ivjDeleteButton = new javax.swing.JButton("", VCellIcons.delSimIcon);
			ivjDeleteButton.setName("DeleteButton");
			ivjDeleteButton.setToolTipText("Delete Simulation");
			ivjDeleteButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeleteButton;
}

/**
 * Return the ScrollPaneTable property value.
 * @return cbit.gui.JTableFixed
 */
private JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
//			ivjScrollPaneTable = new ScrollTable() {
//				@Override
//				public JToolTip createToolTip() {
//					MultiLineToolTip tip = new MultiLineToolTip();
//			        tip.setComponent(this);
//			        return tip;
//				}
//			};
			ivjScrollPaneTable = new JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			ivjScrollPaneTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjScrollPaneTable.setModel(getSimulationListTableModel1());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 2:00:46 PM)
 * @return int[]
 */
public int[] getSelectedRows() {
	return getScrollPaneTable().getSelectedRows();
}

/**
 * Return the SimulationListTableModel1 property value.
 * @return cbit.vcell.client.desktop.biomodel.SimulationListTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimulationListTableModel getSimulationListTableModel1() {
	if (ivjSimulationListTableModel1 == null) {
		try {
			ivjSimulationListTableModel1 = new SimulationListTableModel(ivjScrollPaneTable);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSimulationListTableModel1;
}

private OutputFunctionsPanel getOutputFunctionsPanel() {
	if (outputFunctionsPanel == null) {
		try {
			outputFunctionsPanel = new OutputFunctionsPanel();
			outputFunctionsPanel.setName("ObservablesPanel");
			addPropertyChangeListener(ivjEventHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return outputFunctionsPanel;
}

/**
 * Gets the simulationWorkspace property (cbit.vcell.client.desktop.simulation.SimulationWorkspace) value.
 * @return The simulationWorkspace property value.
 * @see #setSimulationWorkspace
 */
public SimulationWorkspace getSimulationWorkspace() {
	return fieldSimulationWorkspace;
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

private Object getSimulationStatusDisplay(int row) {
	Simulation simulation = getSimulationListTableModel1().getValueAt(row);
	SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(simulation);
	boolean displayProgress = (simStatus.isRunning() || (simStatus.isFailed() && simStatus.numberOfJobsDone() < simulation.getScanCount()))
							  && simStatus.getProgress() != null && simStatus.getProgress().doubleValue() >= 0;
	if (displayProgress){
		double progress = simStatus.getProgress().doubleValue() / simulation.getScanCount();
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setValue((int)(progress * 100));
		if (simStatus.isFailed()) {
			progressBar.setString("one or more jobs failed");
		} else {
			progressBar.setString(NumberUtils.formatNumber(progress * 100, 4) + "%");
		}
		return progressBar;
	} else if (simStatus.isFailed()) {
		return simStatus.getFailedMessage();
	} else {
		return simStatus.getDetails();
	}
}

/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getNewButton().addActionListener(ivjEventHandler);
	getEditButton().addActionListener(ivjEventHandler);
	getDeleteButton().addActionListener(ivjEventHandler);
	getRunButton().addActionListener(ivjEventHandler);
	getMassConservationModelReductionCheckBox().addActionListener(ivjEventHandler);
	getMassConservationModelReductionHelpButton().addActionListener(ivjEventHandler);

//	getPythonResultsButton().addActionListener(ivjEventHandler);
	getNativeResultsButton().addActionListener(ivjEventHandler);
	getMoreActionsButton().addActionListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);

	getOutputFunctionsPanel().addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
	DefaultScrollTableCellRenderer renderer = new DefaultScrollTableCellRenderer(){

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus,	row, column);
			if (value instanceof OutputTimeSpec) {
				SimulationWorkspace sw = getSimulationWorkspace();
				SimulationOwner so = sw.getSimulationOwner();
				UnitInfo unitInfo = so.getUnitInfo();
				Objects.requireNonNull(unitInfo);
				OutputTimeSpec ots = (OutputTimeSpec) value;
				String text = ots.describe(unitInfo);
				setText(text);
			} else if (value instanceof Double) {
				setText(value+"");
			}
			boolean bFinitVolumeRerun = false;
			if (value instanceof SolverDescription) {
				SolverDescription solverDescription = (SolverDescription) value;
				try{
					if(FINITEVOLUME_CUTTOFF != null && solverDescription.equals(SolverDescription.FiniteVolume)/*Compiled Solver*/){
						SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(getSimulationListTableModel1().getValueAt(row));
						if(simStatus.getHasData()){
							int statusIndex = 0;
							while(simStatus.getJobStatus(statusIndex) != null){
								if(simStatus.getJobStatus(statusIndex).getEndDate().compareTo(FINITEVOLUME_CUTTOFF) > 0){
									bFinitVolumeRerun = true;
									break;
								}
								statusIndex++;
							}
						}
					}
				}catch(Exception e){
					//ignore, let table cell render anyway
					e.printStackTrace();
				}
				setText(solverDescription.getShortDisplayLabel());
				setToolTipText(solverDescription.getDisplayLabel());
			} else {
				setToolTipText(getText());
			}
			if(bFinitVolumeRerun){
				setText(getText()+(bFinitVolumeRerun?"(*)":""));
				setToolTipText(getToolTipText()+(bFinitVolumeRerun?" (data regenerated using FiniteVolumeStandalone)":""));
			}
			return this;
		}

	};
	getScrollPaneTable().setDefaultRenderer(OutputTimeSpec.class, renderer);
	getScrollPaneTable().setDefaultRenderer(Double.class, renderer);
	getScrollPaneTable().setDefaultRenderer(String.class, renderer);
	getScrollPaneTable().setDefaultRenderer(SolverDescription.class, renderer);
	getScrollPaneTable().setDefaultEditor(OutputTimeSpec.class, new DefaultCellEditor(new JTextField()));
	getScrollPaneTable().setDefaultRenderer(SimulationStatus.class, new DefaultScrollTableCellRenderer() {
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Object obj = getSimulationStatusDisplay(row);
			if (obj instanceof JProgressBar) {
				return (JProgressBar)obj;
			}
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
			if (value instanceof SimulationStatus) {
				setText(obj.toString());
				String details = ((SimulationStatus) value).getDetails();
				setToolTipText(details);
			}
			return this;
		}
	});
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SimulationListPanel");
		setSize(750, 560);

		setLayout(new BorderLayout());
		add(getToolBar(), BorderLayout.NORTH);
		add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);

		initConnections();
		getScrollPaneTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		JFrame frame = new javax.swing.JFrame("SimulationListPanel");
		SimulationListPanel aSimulationListPanel;
		aSimulationListPanel = new SimulationListPanel();
		frame.setContentPane(aSimulationListPanel);
		frame.setSize(aSimulationListPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private void newSimulation(final NetworkGenerationRequirements networkGenerationRequirements) {
	AsynchClientTask task1 = new AsynchClientTask("new simulation", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			MathMappingCallback mathMappingCallback = new MathMappingCallbackTaskAdapter(getClientTaskStatusSupport());
			if (getSimulationWorkspace().getSimulationOwner() instanceof SimulationContext){
				SimulationContext simulationContext = (SimulationContext)getSimulationWorkspace().getSimulationOwner();
				simulationContext.refreshMathDescription(mathMappingCallback,networkGenerationRequirements);
			}
		}
	};
	AsynchClientTask task2 = new AsynchClientTask("new simulation", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			MathMappingCallback mathMappingCallback = new MathMappingCallbackTaskAdapter(getClientTaskStatusSupport());
			int newSimIndex = getSimulationWorkspace().newSimulation(SimulationListPanel.this,mathMappingCallback,networkGenerationRequirements);
			getScrollPaneTable().getSelectionModel().setSelectionInterval(newSimIndex, newSimIndex);
			getScrollPaneTable().scrollRectToVisible(getScrollPaneTable().getCellRect(newSimIndex, 0, true));
		}
	};
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});
}

/**
 * can this task run locally?
 * sets {@link #quickRunButton} tool tip
 * @param taskDesc
 * @return true if it can
 */
private boolean canQuickRun(SolverTaskDescription taskDesc) {
	if (taskDesc.isParallel())  {
		System.err.println("SimulationListPanel.canQuickRun(): parallel solver not supported for local simulation");
//		quickPythonRunButton.setToolTipText("Parallel solver not supported");
		quickNativeRunButton.setToolTipText("Parallel solver not supported");
		return false;
	}else if (taskDesc.getSolverDescription().supports(SolverFeature.Feature_ServerOnly)) {
		System.err.println("SimulationListPanel.canQuickRun(): server-only feature required, local solver not supported");
//		quickPythonRunButton.setToolTipText("Not supported by selected solver");
		quickNativeRunButton.setToolTipText("Not supported by selected solver");
		return false;
	}else if(taskDesc.getSimulation().getScanCount() > 1){
		System.err.println("SimulationListPanel.canQuickRun(): parameter scan, local solver not supported");
//		quickPythonRunButton.setToolTipText("Not supported for parameter scans");
		quickNativeRunButton.setToolTipText("Not supported for parameter scans");
		return false;
	}
	if (!taskDesc.getSolverDescription().isJavaSolver()){
		try {
			File[] exes = SolverUtilities.getExes(taskDesc.getSolverDescription());
			if (exes==null || exes.length==0){
				System.err.println("SimulationListPanel.canQuickRun(): SolverUtilities.getExes("+taskDesc.getSolverDescription()+" returned no executable paths");
//				quickPythonRunButton.setToolTipText("Not available on this operating system");
				quickNativeRunButton.setToolTipText("Not available on this operating system");
				return false;
			}
			for (File exe : exes){
				if (!exe.exists()){
					System.err.println("SimulationListPanel.canQuickRun(): "+exe.getAbsolutePath()+" not found");
//					quickPythonRunButton.setToolTipText("Not available on this operating system");
					quickNativeRunButton.setToolTipText("Not available on this operating system");
					return false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	System.out.println("SimulationListPanel.canQuickRun(): solver "+taskDesc.getSolverDescription()+" supported for local computation");
//	quickPythonRunButton.setToolTipText(QUICK_RUN_PYTHON_TOOL_TIP);
	quickNativeRunButton.setToolTipText(QUICK_RUN_NATIVE_TOOL_TIP);
	return true;
}
private boolean canServerRun(SolverTaskDescription taskDesc) {
	if(taskDesc.getSolverDescription().isComsolSolver()) {
		System.err.println("SimulationListPanel.canServerRun(): quick run solver only, server run not supported");
		getRunButton().setToolTipText("Not supported by selected solver");
		return false;
	}
	getRunButton().setToolTipText(SERVER_RUN_TOOL_TIP);
	return true;
}
/**
 * Comment
 */
private void refreshButtonsLax() {
	MathDescription mathDescription = fieldSimulationWorkspace.getSimulationOwner().getMathDescription();
	if (mathDescription != null) {
//		particleViewButton.setVisible(mathDescription.isSpatialStoch());
//		quickPythonRunButton.setVisible(true);
		quickNativeRunButton.setVisible(true);
	}

	int[] selections = getScrollPaneTable().getSelectedRows();

	boolean bBatch = false;
	boolean bCopy = false;
	boolean bEditable = false;
	boolean bDeletable = false;
	boolean bRunnable = false;
	boolean bStoppable = false;
	boolean bHasData = false;
	boolean bStatusDetails = false;
//	boolean bParticleView = false;
	boolean bQuickRun = false;
	
	getRunButton().setEnabled(bRunnable);

	Simulation firstSelection = null;
	if (selections != null && selections.length > 0) {
		bCopy = true;
		bStatusDetails = true;
		firstSelection = ivjSimulationListTableModel1.getValueAt(selections[0]);
		if (selections.length == 1){
			SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(firstSelection);
			if (!simStatus.isRunning()){
				bEditable = true;
				bBatch = true;
			}
			final boolean onlyOne = firstSelection.getScanCount() == 1;
//			bParticleView = onlyOne;
			bRunnable = onlyOne && simStatus.isRunnable() && canServerRun(firstSelection.getSolverTaskDescription());
			bQuickRun = onlyOne && canQuickRun( firstSelection.getSolverTaskDescription() );
		}

		// we make'em true if at least one sim satisfies criterion (lax policy)
		for (int i = 0; i < selections.length; i++){
			Simulation sim = ivjSimulationListTableModel1.getValueAt(selections[i]);
			SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(sim);
			bDeletable = bDeletable || !simStatus.isRunning();
			bRunnable = bRunnable || (simStatus.isRunnable() && canServerRun(sim.getSolverTaskDescription()));
			bStoppable = bStoppable || simStatus.isStoppable();
			bHasData = bHasData || simStatus.getHasData();
		}
	}
	batchButton.setEnabled(bBatch);
	copyButton.setEnabled(bCopy);
	getEditButton().setEnabled(bEditable);
	getDeleteButton().setEnabled(bDeletable);
	massConservationButtonAdjust();
	
	getRunButton().setEnabled(bRunnable);
	stopButton.setEnabled(bStoppable);
	if(selections != null && selections.length == 1) {
		if(bHasData && firstSelection != null && (firstSelection.getMathDescription().isSpatial3D() || firstSelection.getMathDescription().isSpatial2D())) {
//			getPythonResultsButton().setVisible(true);
//			getPythonResultsButton().setEnabled(true);
		} else {
//			getPythonResultsButton().setVisible(false);
		}
	} else {
//		getPythonResultsButton().setVisible(false);
	}
	getNativeResultsButton().setEnabled(bHasData);
	statusDetailsButton.setEnabled(bStatusDetails);
//	particleViewButton.setEnabled(bParticleView);
//	quickPythonRunButton.setEnabled(bQuickRun);
	quickNativeRunButton.setEnabled(bQuickRun);
}

private void massConservationButtonAdjust() {
	
	if(fieldSimulationWorkspace != null && fieldSimulationWorkspace.getSimulationOwner() instanceof SimulationContext) {
		SimulationContext simContext = (SimulationContext)fieldSimulationWorkspace.getSimulationOwner();
		if(simContext.getGeometry().getDimension() == 0 && simContext.getApplicationType() == SimulationContext.Application.NETWORK_DETERMINISTIC) {
			getMassConservationModelReductionCheckBox().setVisible(true);
			getMassConservationModelReductionCheckBox().setEnabled(true);
			getMassConservationModelReductionCheckBox().setSelected(simContext.isUsingMassConservationModelReduction());
			getMassConservationModelReductionHelpButton().setVisible(true);
			getMassConservationModelReductionHelpButton().setEnabled(true);
		} else {
			getMassConservationModelReductionCheckBox().setVisible(false);
			getMassConservationModelReductionHelpButton().setVisible(false);
		}
	} else {
		getMassConservationModelReductionCheckBox().setVisible(false);
		getMassConservationModelReductionHelpButton().setVisible(false);
	}
}


/**
 * Comment
 */
private void runSimulations() {
	if(User.isGuest(getSimulationWorkspace().getLoggedInUser().getName())) {
		DialogUtils.showErrorDialog(this, User.createGuestErrorMessage("runVCellServerSimulations"));
		return;
	}
	final ArrayList<Simulation> simList = new ArrayList<Simulation>();
	int[] selections = getScrollPaneTable().getSelectedRows();
	for (int i = 0; i < selections.length; i++){
		Simulation sim = (Simulation)(ivjSimulationListTableModel1.getValueAt(selections[i]));
		if (sim.getSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolume)) {
			if (getSimulationWorkspace().getSimulationOwner() instanceof SimulationContext) {
				String option = DialogUtils.showOKCancelWarningDialog(SimulationListPanel.this, "Deprecated Solver", VCellErrorMessages.getSemiFVSolverCompiledSolverDeprecated(sim));
				if (option.equals(SimpleUserMessage.OPTION_CANCEL)) {
					return;
				}
				try {
					sim.getSolverTaskDescription().setSolverDescription(SolverDescription.FiniteVolumeStandalone);
					sim.setIsDirty(true);
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
			}
		}
		simList.add(sim);
	}
	if (simList.size() > 0) {
		activateConsole();

		Simulation[] toRun = simList.toArray(new Simulation[0]);
		try {
			getSimulationWorkspace().runSimulations(toRun, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DialogUtils.showErrorDialog(this, e.getMessage());
		}
	}
}


/**
 * Comment
 */
public void scrollPaneTable_FocusLost(java.awt.event.FocusEvent focusEvent) {
	int row = getScrollPaneTable().getSelectedRow();
	int col = getScrollPaneTable().getSelectedColumn();
	TableCellEditor ce = getScrollPaneTable().getCellEditor(row, col);
	if (ce != null) {
		ce.stopCellEditing();
	}
}

/**
 * Sets the simulationWorkspace property (cbit.vcell.client.desktop.simulation.SimulationWorkspace) value.
 * @param simulationWorkspace The new value for the property.
 * @see #getSimulationWorkspace
 */
public void setSimulationWorkspace(SimulationWorkspace newValue) {
	if (fieldSimulationWorkspace == newValue) {
		return;
	}
	SimulationWorkspace oldValue = fieldSimulationWorkspace;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(ivjEventHandler);
		if(oldValue.getSimulationOwner() instanceof SimulationContext) {
			oldValue.getSimulationOwner().removePropertyChangeListener(ivjEventHandler);
		}
	}
	fieldSimulationWorkspace = newValue;
	if (fieldSimulationWorkspace != null) {
		fieldSimulationWorkspace.addPropertyChangeListener(ivjEventHandler);
		if(fieldSimulationWorkspace.getSimulationOwner() instanceof SimulationContext) {
			fieldSimulationWorkspace.getSimulationOwner().addPropertyChangeListener(ivjEventHandler);
		}
	}
	getSimulationListTableModel1().setSimulationWorkspace(fieldSimulationWorkspace);
	refreshButtonsLax();
}


/**
 * Comment
 */
private void showSimulationResults(ViewerType viewerType) {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector<Simulation> v = new Vector<Simulation>();
	for (int i = 0; i < selections.length; i++){
		v.add((Simulation)(ivjSimulationListTableModel1.getValueAt(selections[i])));
	}
	Simulation[] toShow = (Simulation[])BeanUtils.getArray(v, Simulation.class);
	getSimulationWorkspace().showSimulationResults(toShow, viewerType);
}


/**
 * Comment
 */
private void showSimulationStatusDetails() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector<Simulation> v = new Vector<Simulation>();
	for (int i = 0; i < selections.length; i++){
		v.add((Simulation)(ivjSimulationListTableModel1.getValueAt(selections[i])));
	}
	Simulation[] sims = (Simulation[])BeanUtils.getArray(v, Simulation.class);
	getSimulationWorkspace().showSimulationStatusDetails(sims);
}


/**
 * Comment
 */
private void stopSimulations() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector<Simulation> v = new Vector<Simulation>();
	for (int i = 0; i < selections.length; i++){
		v.add((Simulation)(ivjSimulationListTableModel1.getValueAt(selections[i])));
	}
	Simulation[] toStop = (Simulation[])BeanUtils.getArray(v, Simulation.class);
	getSimulationWorkspace().stopSimulations(toStop);
}

	private javax.swing.JButton getMoreActionsButton() {
		if (moreActionsButton == null) {
			try {
				moreActionsButton = new JButton("More Actions", new DownArrowIcon());
				moreActionsButton.setHorizontalTextPosition(SwingConstants.LEFT);
				moreActionsButton.setName("MoreActionsButton");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return moreActionsButton;
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		setTableSelections(selectedObjects, getScrollPaneTable(), getSimulationListTableModel1());
	}
	@Override
	public void setIssueManager(IssueManager newValue) {
		super.setIssueManager(newValue);
		ivjSimulationListTableModel1.setIssueManager(newValue);
	}

	/*
	private void particleView() {
		int row = getScrollPaneTable().getSelectedRow();
		if (row < 0) {
			return;
		}
		Simulation selectedSim = getSimulationListTableModel1().getValueAt(row);
		getSimulationWorkspace().getClientSimManager().runSmoldynParticleView(selectedSim);
	}
	*/
	private void quickRun(ViewerType viewerType) {
		int row = getScrollPaneTable().getSelectedRow();
		if (row < 0) {
			return;
		}
		activateConsole();

		Simulation selectedSim = getSimulationListTableModel1().getValueAt(row);
		SolverDescription solverDescription = selectedSim.getSolverTaskDescription().getSolverDescription();
		if (solverDescription.equals(SolverDescription.FiniteVolume)) {
			if (getSimulationWorkspace().getSimulationOwner() instanceof SimulationContext) {
				String option = DialogUtils.showOKCancelWarningDialog(SimulationListPanel.this, "Deprecated Solver", VCellErrorMessages.getSemiFVSolverCompiledSolverDeprecated(selectedSim));
				if (option.equals(SimpleUserMessage.OPTION_CANCEL)) {
					return;
				}
				try {
					selectedSim.getSolverTaskDescription().setSolverDescription(SolverDescription.FiniteVolumeStandalone);
					selectedSim.setIsDirty(true);
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
			}
		}
		getSimulationWorkspace().getClientSimManager().runQuickSimulation(selectedSim, viewerType);
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

}  //  @jve:decl-index=0:visual-constraint="10,10"
