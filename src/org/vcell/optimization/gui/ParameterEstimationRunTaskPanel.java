package org.vcell.optimization.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.vcell.optimization.CopasiOptimizationSolver.CopasiOptimizationMethod;
import org.vcell.optimization.CopasiOptimizationSolver.CopasiOptimizationMethodType;
import org.vcell.optimization.CopasiOptimizationSolver.CopasiOptimizationParameter;
import org.vcell.optimization.ProfileSummaryData;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;

@SuppressWarnings("serial")
public class ParameterEstimationRunTaskPanel extends JPanel {

	private javax.swing.JTextPane ivjOptimizeResultsTextPane = null;
	private javax.swing.JComboBox optimizationMethodComboBox = null;
	private javax.swing.JButton plotButton = null;
	private javax.swing.JButton saveSolutionAsNewSimButton = null;
	private javax.swing.JPanel solutionPanel = null;
	private javax.swing.JProgressBar ivjJProgressBar1 = null;
	private javax.swing.JButton solveButton = null;
	private javax.swing.JLabel ivjNumEvaluationsValueLabel = null;
	private javax.swing.JLabel ivjObjectiveFunctionValueLabel = null;
	private javax.swing.JButton ivjStopButton = null;
	private javax.swing.JPanel solverPanel = null;
	private ParameterEstimationTask fieldParameterEstimationTask = null;
	private JCheckBox computeProfileDistributionsCheckBox = null;
	private JButton evaluateConfidenceIntervalButton = null;
	
	private ScrollTable optimizationMethodParameterTable = null;
	private OptimizationMethodParameterTableModel optimizationMethodParameterTableModel;
	private ParameterEstimationController parameterEstimationController = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private static class OptimizationMethodParameterTableModel extends VCellSortTableModel<CopasiOptimizationParameter> {
		CopasiOptimizationMethod copasiOptimizationMethod;
		static final int COLUMN_Parameter = 0;
		static final int COLUMN_Value = 1;
		
		OptimizationMethodParameterTableModel(ScrollTable table) {
			super(table, new String[] {"Parameter", "Value"});
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			CopasiOptimizationParameter cop = getValueAt(rowIndex);
			return columnIndex == COLUMN_Parameter ? cop.getType().getDisplayName() : cop.getValue();
		}

		@Override
		protected Comparator<CopasiOptimizationParameter> getComparator(int col, boolean ascending) {
			return null;
		}
		
		private void refreshData() {
			List<CopasiOptimizationParameter> list = null;
			
			if (copasiOptimizationMethod.getParameters() != null) {
				list = Arrays.asList(copasiOptimizationMethod.getParameters());
			}
			setData(list);
		}
		
		public final void setCopasiOptimizationMethod(CopasiOptimizationMethod copasiOptimizationMethod) {
			this.copasiOptimizationMethod = copasiOptimizationMethod;
			refreshData();
			GuiUtils.flexResizeTableColumns(ownerTable);
		}

		@Override
		public boolean isSortable(int col) {
			return false;
		}
	}
	
	private class InternalEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getOptimizationMethodComboBox()) 
				solverTypeComboBox_ActionPerformed();
			if (e.getSource() == getPlotButton()) 
				parameterEstimationController.plot();
			if (e.getSource() == getSaveSolutionAsNewSimButton()) 
				parameterEstimationController.saveSolutionAsNewSimulation();
			if (e.getSource() == getSolveButton()) 
				solve();
			if (e.getSource() == getStopButton()) 
				parameterEstimationController.stop();
			else if (e.getSource() == getEvaluateConfidenceIntervalButton()) { 
				evaluateConfidenceInterval(); 
			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == fieldParameterEstimationTask && (evt.getPropertyName().equals("optimizationResultSet"))) 
				optimizationResultSet_This(fieldParameterEstimationTask.getOptimizationResultSet());
			if (evt.getSource() == fieldParameterEstimationTask && (evt.getPropertyName().equals("solverMessageText"))) 
				getOptimizeResultsTextPane().setText(String.valueOf(fieldParameterEstimationTask.getSolverMessageText()));
		};
	}
	
	public ParameterEstimationRunTaskPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSolverPanel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = 0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSolutionPanel(), gbc);
		
		parameterEstimationController = new ParameterEstimationController(this);
		
		javax.swing.DefaultComboBoxModel model = new DefaultComboBoxModel();
		for (CopasiOptimizationMethodType com : CopasiOptimizationMethodType.values()){
			model.addElement(com);
		}
		getOptimizationMethodComboBox().setModel(model);
		getOptimizationMethodComboBox().setRenderer(new DefaultListCellRenderer() {
			
			@Override
			public Component getListCellRendererComponent(JList list, Object value,	int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof CopasiOptimizationMethodType) {
					setText(((CopasiOptimizationMethodType) value).getDisplayName());
				}
				return this;
			}
		});
		
		getSolveButton().addActionListener(eventHandler);
		getStopButton().addActionListener(eventHandler);
		getOptimizationMethodComboBox().addActionListener(eventHandler);
		getPlotButton().addActionListener(eventHandler);
	}
	
	/**
	 * connEtoM19:  (SolveButton.action.actionPerformed(java.awt.event.ActionEvent) --> OptimizationController.solve()V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	private void solve() {
		try {
			fieldParameterEstimationTask.getModelOptimizationSpec().setComputeProfileDistributions(computeProfileDistributionsCheckBox.isSelected());
			parameterEstimationController.solve();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	/**
	 * Comment
	 */
	private java.lang.String displayResults(OptimizationResultSet optResultSet) {
		if (optResultSet==null){
			return "no results";
		}
		StringBuffer buffer = new StringBuffer();

		buffer.append("\n-------------Optimizer Output-----------------\n");
		buffer.append(optResultSet.getOptSolverResultSet().getOptimizationStatus() + "\n");
		buffer.append("best objective function :"+optResultSet.getOptSolverResultSet().getLeastObjectiveFunctionValue()+"\n");
		buffer.append("num function evaluations :"+optResultSet.getOptSolverResultSet().getObjFunctionEvaluations()+"\n");
		if (optResultSet.getOptSolverResultSet().getOptimizationStatus().isNormal()){
			buffer.append("status: complete\n");
		}else{
			buffer.append("status: aborted\n");
		}
		for (int i = 0; optResultSet.getOptSolverResultSet().getParameterNames()!=null && i < optResultSet.getOptSolverResultSet().getParameterNames().length; i++){
			buffer.append(optResultSet.getOptSolverResultSet().getParameterNames()[i]+" = "+optResultSet.getOptSolverResultSet().getBestEstimates()[i]+"\n");
		}

		return buffer.toString();
	}


	/**
	 * Return the JPanel10 property value.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getSolverPanel() {
		if (solverPanel == null) {
			try {
				solverPanel = new javax.swing.JPanel();
				solverPanel.setBorder(new TitledBorder("COPASI Optimization Method"));
				solverPanel.setLayout(new java.awt.GridBagLayout());

				optimizationMethodParameterTable = new ScrollTable();
				optimizationMethodParameterTableModel = new OptimizationMethodParameterTableModel(optimizationMethodParameterTable);
				optimizationMethodParameterTable.setModel(optimizationMethodParameterTableModel);
				
				computeProfileDistributionsCheckBox = new JCheckBox("Compute Profile Distributions");
				java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; 
				gbc.gridy = 0;
				gbc.weightx = 1.0;
				gbc.gridwidth = 2;
				gbc.insets = new java.awt.Insets(4, 4, 4, 0);
				gbc.anchor = GridBagConstraints.LINE_START;
				solverPanel.add(computeProfileDistributionsCheckBox, gbc);

				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; 
				gbc.gridy = 1;
				gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
				gbc.weightx = 1.0;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.gridwidth = 2;
				solverPanel.add(getOptimizationMethodComboBox(), gbc);

				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; 
				gbc.gridy = 2;
				gbc.fill = java.awt.GridBagConstraints.BOTH;
				gbc.weightx = 1.0;
				gbc.weighty = 1.0;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.gridwidth = 2;
				solverPanel.add(new JScrollPane(optimizationMethodParameterTable), gbc);
				
				
				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; 
				gbc.gridy = 3;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.weightx = 1.0;
				gbc.anchor = GridBagConstraints.PAGE_START;
				solverPanel.add(getSolveButton(), gbc);

				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 1; 
				gbc.gridy = 3;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.weightx = 1.0;
//				gbc.weighty = 0.5;
				gbc.anchor = GridBagConstraints.PAGE_START;
				solverPanel.add(getStopButton(), gbc);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return solverPanel;
	}

	/**
	 * Return the JPanel7 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getSolutionPanel() {
		if (solutionPanel == null) {
			try {
				solutionPanel = new javax.swing.JPanel();
				solutionPanel.setBorder(new TitledBorder("Solution"));
				solutionPanel.setLayout(new java.awt.GridBagLayout());
				
				int gridy = 0;		
				java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; gbc.gridy = gridy;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.anchor = GridBagConstraints.LINE_END;
				solutionPanel.add(new javax.swing.JLabel("Best Objective Function Value: "), gbc);

				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 1; 
				gbc.gridy = gridy;
				gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
				gbc.weightx = 1.0;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				solutionPanel.add(getObjectiveFunctionValueLabel(), gbc);

				gridy ++;
				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; 
				gbc.gridy = gridy;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.anchor = GridBagConstraints.LINE_END;
				solutionPanel.add(new javax.swing.JLabel("Number of Evaluations: "), gbc);

				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 1; 
				gbc.gridy = gridy;
				gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
				gbc.weightx = 1.0;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				solutionPanel.add(getNumEvaluationsValueLabel(), gbc);

				gridy ++;
				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; 
				gbc.gridy = gridy;
				gbc.gridwidth = 2;
				gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				solutionPanel.add(getJProgressBar1(), gbc);

				gridy ++;
				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; gbc.gridy = gridy;
				gbc.gridwidth = 2;
				gbc.fill = java.awt.GridBagConstraints.BOTH;
				gbc.weightx = 1.0;
				gbc.weighty = 1.0;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				solutionPanel.add(new javax.swing.JScrollPane(getOptimizeResultsTextPane()), gbc);
				
				JPanel panel = new javax.swing.JPanel();
				panel.setLayout(new java.awt.FlowLayout());
				panel.add(getPlotButton());
				panel.add(getSaveSolutionAsNewSimButton());
				panel.add(getEvaluateConfidenceIntervalButton());

				gridy ++;
				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; gbc.gridy = gridy;
				gbc.gridwidth = 2;
				gbc.fill = java.awt.GridBagConstraints.BOTH;
				gbc.weightx = 1.0;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				solutionPanel.add(panel, gbc);

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return solutionPanel;
	}

	/**
	 * Return the JProgressBar1 property value.
	 * @return javax.swing.JProgressBar
	 */
	private javax.swing.JProgressBar getJProgressBar1() {
		if (ivjJProgressBar1 == null) {
			try {
				ivjJProgressBar1 = new javax.swing.JProgressBar();
				ivjJProgressBar1.setName("JProgressBar1");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJProgressBar1;
	}

	/**
	 * Return the JLabel4 property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getNumEvaluationsValueLabel() {
		if (ivjNumEvaluationsValueLabel == null) {
			try {
				ivjNumEvaluationsValueLabel = new javax.swing.JLabel();
				ivjNumEvaluationsValueLabel.setName("NumEvaluationsValueLabel");
				ivjNumEvaluationsValueLabel.setText(" ");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjNumEvaluationsValueLabel;
	}

	/**
	 * Return the JLabel7 property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getObjectiveFunctionValueLabel() {
		if (ivjObjectiveFunctionValueLabel == null) {
			try {
				ivjObjectiveFunctionValueLabel = new javax.swing.JLabel();
				ivjObjectiveFunctionValueLabel.setName("ObjectiveFunctionValueLabel");
				ivjObjectiveFunctionValueLabel.setText(" ");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjObjectiveFunctionValueLabel;
	}

	/**
	 * Return the JTextPane1 property value.
	 * @return javax.swing.JTextPane
	 */
	private javax.swing.JTextPane getOptimizeResultsTextPane() {
		if (ivjOptimizeResultsTextPane == null) {
			try {
				ivjOptimizeResultsTextPane = new javax.swing.JTextPane();
				ivjOptimizeResultsTextPane.setName("OptimizeResultsTextPane");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjOptimizeResultsTextPane;
	}

	/**
	 * Return the PlotButton property value.
	 * @return javax.swing.JButton
	 */
	private JButton getPlotButton() {
		if (plotButton == null) {
			try {
				plotButton = new JButton("Plot");
				plotButton.setEnabled(false);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return plotButton;
	}

	private JButton getEvaluateConfidenceIntervalButton() {
		if ( evaluateConfidenceIntervalButton == null) {
			try {
				evaluateConfidenceIntervalButton = new javax.swing.JButton("Confidence Interval");
				evaluateConfidenceIntervalButton.setEnabled(false);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return evaluateConfidenceIntervalButton;
	}

	/**
	 * Return the SaveAsNewSimulationButton property value.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getSaveSolutionAsNewSimButton() {
		if (saveSolutionAsNewSimButton == null) {
			try {
				saveSolutionAsNewSimButton = new javax.swing.JButton();
				saveSolutionAsNewSimButton.setName("SaveSolutionAsNewSimButton");
				saveSolutionAsNewSimButton.setText("Save Solution as New Simulation...");
				saveSolutionAsNewSimButton.setEnabled(false);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return saveSolutionAsNewSimButton;
	}
	
	/**
	 * Return the JButton2 property value.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getSolveButton() {
		if (solveButton == null) {
			try {
				solveButton = new javax.swing.JButton("Start");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return solveButton;
	}

	/**
	 * Comment
	 */
	private java.lang.String getSolverMessageText() {
		if (fieldParameterEstimationTask!=null){
			return fieldParameterEstimationTask.getSolverMessageText();
		}else{
			return "";
		}
	}


	/**
	 * Return the SolverTypeComboBox property value.
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getOptimizationMethodComboBox() {
		if (optimizationMethodComboBox == null) {
			try {
				optimizationMethodComboBox = new javax.swing.JComboBox();
				optimizationMethodComboBox.setName("SolverTypeComboBox");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return optimizationMethodComboBox;
	}

	/**
	 * Return the JButton1 property value.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getStopButton() {
		if (ivjStopButton == null) {
			try {
				ivjStopButton = new javax.swing.JButton();
				ivjStopButton.setName("StopButton");
				ivjStopButton.setText("Stop");
				ivjStopButton.setEnabled(false);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjStopButton;
	}

	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}


	/**
	 * Comment
	 */
	private void optimizationResultSet_This(OptimizationResultSet optResultSet) {
		String message = displayResults(optResultSet);
		fieldParameterEstimationTask.appendSolverMessageText("\n"+message);
		if (optResultSet!=null){
			getSaveSolutionAsNewSimButton().setEnabled(true);
		}else{
			getSaveSolutionAsNewSimButton().setEnabled(false);
		}
	}


	/**
	 * Sets the parameterEstimationTask property (cbit.vcell.modelopt.ParameterEstimationTask) value.
	 * @param newValue The new value for the property.
	 * @see #getParameterEstimationTask
	 */
	public void setParameterEstimationTask(ParameterEstimationTask newValue) {
		ParameterEstimationTask oldValue = fieldParameterEstimationTask;
		fieldParameterEstimationTask = newValue;
		/* Stop listening for events from the current object */
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(eventHandler);
		}

		/* Listen for events from the new object */
		if (newValue != null) {
			newValue.addPropertyChangeListener(eventHandler);
		}
		getOptimizeResultsTextPane().setText(this.getSolverMessageText());
		parameterEstimationController.setParameterEstimationTask(fieldParameterEstimationTask);
	}
	
	/**
	 * Comment
	 */
	private void solverTypeComboBox_ActionPerformed() {
		if (fieldParameterEstimationTask==null){
			return;
		}
		//
		// if running, shouldn't change solver type.
		//
		if (parameterEstimationController.isRunning()){
			throw new RuntimeException("can't change solver types while running");
		}

		CopasiOptimizationMethodType methodType = (CopasiOptimizationMethodType)getOptimizationMethodComboBox().getSelectedItem();
		CopasiOptimizationMethod com = new CopasiOptimizationMethod(methodType);
		OptimizationSolverSpec optSolverSpec = new OptimizationSolverSpec(com);
		fieldParameterEstimationTask.setOptimizationSolverSpec(optSolverSpec);
		optimizationMethodParameterTableModel.setCopasiOptimizationMethod(com);
	}

	public void updateInterface(boolean bSolving) {
		getPlotButton().setEnabled(!bSolving);
		getSaveSolutionAsNewSimButton().setEnabled(!bSolving);
		getEvaluateConfidenceIntervalButton().setEnabled(!bSolving);
		getSolveButton().setEnabled(!bSolving);
		getStopButton().setEnabled(bSolving);

		getJProgressBar1().setValue(bSolving ? 0 : 100);
		getOptimizationMethodComboBox().setEnabled(!bSolving);
	}

	public void setProgress(int p) {
		getJProgressBar1().setValue(p);
	}

	public void setNumEvaluations(String num) {
		getNumEvaluationsValueLabel().setText(num);
	}

	public void setObjectFunctionValue(String d) {
		getObjectiveFunctionValueLabel().setText(d);
	}

	public void evaluateConfidenceInterval() 
	{
		ProfileSummaryData[] summaryData = null;
		try {
			summaryData = parameterEstimationController.evaluateConfidenceInterval();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			DialogUtils.showErrorDialog(this, e.getMessage());
			e.printStackTrace();
		}
		//put plotpanes of different parameters' profile likelihoods into a base panel
		JPanel basePanel= new JPanel();
		basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
		for(ProfileSummaryData aSumData : summaryData)
		{
			ConfidenceIntervalPlotPanel plotPanel = new ConfidenceIntervalPlotPanel();
			plotPanel.setProfileSummaryData(aSumData);
			plotPanel.setBorder(new EtchedBorder());
			
			ProfileDataPanel profileDataPanel = new ProfileDataPanel(plotPanel, aSumData.getParamName());
			basePanel.add(profileDataPanel);
		}
		JScrollPane scrollPane = new JScrollPane(basePanel);
		scrollPane.setAutoscrolls(true);
		scrollPane.setPreferredSize(new Dimension(620, 600));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//show plots in a dialog
		DialogUtils.showComponentCloseDialog(this, scrollPane, "Profile Likelihood of Parameters");
	}

}
