/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vmicro.workflow.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.vcell.util.ColorUtil;
import org.vcell.util.gui.DialogUtils;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.OptContextSolver;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;
import org.vcell.vmicro.workflow.task.DisplayProfileLikelihoodPlots;
import org.vcell.vmicro.workflow.task.RunProfileLikelihoodGeneral;
import org.vcell.workflow.MemoryRepository;
import org.vcell.workflow.Repository;
import org.vcell.workflow.TaskContext;
import org.vcell.workflow.Workflow;
import org.vcell.workflow.WorkflowParameter;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.modelopt.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.opt.Parameter;

@SuppressWarnings("serial")
public class OptModelParamPanel extends JPanel
{
	private static final int ARRAY_INDEX_EXPDATASOURCE = 0;
	private static final int ARRAY_INDEX_SIMDATASOURCE = 1;
	
	private final ArrayList<JSlider> parameterValueSliders;
	private final ArrayList<JTextField> parameterValueTextFields;
	private final ArrayList<JButton> parameterValueSetButtons;
	private OptContext optContext;
	private NormalizedSampleFunction[] fittedROIs;
	private LocalWorkspace localWorkspace;
	private final JPanel parameterPanel = new JPanel();
	private final MultisourcePlotPane multisourcePlotPane = new MultisourcePlotPane();

	private boolean B_HOLD_FIRE = false;
	
	private final ActionListener OPTIMIZER_VALUE_ACTION_LISTENER =
		new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (parameterValueTextFields.contains(e.getSource())){
					JTextField valueTextField = (JTextField)e.getSource();
					int parameterIndex = parameterValueTextFields.indexOf(valueTextField);
					JButton valueSetButton = parameterValueSetButtons.get(parameterIndex);
					SET_ACTION_LISTENER.actionPerformed(new ActionEvent(valueSetButton,e.getID(),valueSetButton.getActionCommand()));
				}
			}
		};
		
	private final ChangeListener OPTIMIZER_SLIDER_CHANGE_LISTENER =
		new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				if (parameterValueSliders.contains(e.getSource())){
					JSlider slider = (JSlider)e.getSource();
					int parameterIndex = parameterValueSliders.indexOf(slider);
					Parameter parameter = optContext.getParameters()[parameterIndex];
					double value = parameter.getLowerBound() + (parameter.getUpperBound()-parameter.getLowerBound())*(((double)slider.getValue())/((double)slider.getMaximum()));
					JTextField valueTextField = parameterValueTextFields.get(parameterIndex);
					valueTextField.setText(value+"");
					valueTextField.setCaretPosition(0);
					JButton setButton = parameterValueSetButtons.get(parameterIndex);
					setButton.setEnabled(false);
				}
												
				parameterValuesChanged();
			}

		};
		
	private ActionListener SET_ACTION_LISTENER =
		new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try{
					for (JSlider slider : parameterValueSliders){
						slider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					}
					
					Parameter[] parameters = optContext.getParameters();
					for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++){
						Parameter p = parameters[parameterIndex];
						JTextField textField = parameterValueTextFields.get(parameterIndex);
						JSlider slider = parameterValueSliders.get(parameterIndex);
						double value = Double.parseDouble(textField.getText());
						value = Math.max(value, p.getLowerBound());
						value = Math.min(value, p.getUpperBound());
						textField.setText(value+"");
						textField.setCaretPosition(0);
						int sliderValue = (int)(((value - p.getLowerBound()) * slider.getMaximum())/(p.getUpperBound() - p.getLowerBound()));
						sliderValue = Math.max(sliderValue, slider.getMinimum());
						sliderValue = Math.min(sliderValue, slider.getMaximum());
						slider.setValue(sliderValue);
						slider.setEnabled(true);
					}
					//set all text fields positions to 0
					setAllTextFieldsPosition(0);

					if(!B_HOLD_FIRE){
						parameterValuesChanged();
					}
				}catch (Exception e2){
					e2.printStackTrace();
					parameterValueSetButtons.contains(e.getSource());
					Parameter p = optContext.getParameters()[parameterValueSetButtons.indexOf(e.getSource())];
					DialogUtils.showErrorDialog(OptModelParamPanel.this, "Error setting parameter value for "+p.getName()+"\n"+e2.getMessage());
				}finally{
					for (JSlider slider : parameterValueSliders){
						slider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					}
				}
			}
		};
		
	private UndoableEditListener EDIT_LISTENER =
		new UndoableEditListener() {
			public void undoableEditHappened(final UndoableEditEvent e) {
				for (int i=0;i<parameterValueTextFields.size();i++){
					JTextField textField = parameterValueTextFields.get(i); 
					if (textField.getDocument() == e.getSource()){
						JButton setButton = parameterValueSetButtons.get(i);
						setButton.setEnabled(true);
					}
				}
			}
		};
		
	public OptModelParamPanel() {
		super();
		this.parameterValueSetButtons = new ArrayList<JButton>();
		this.parameterValueSliders = new ArrayList<JSlider>();
		this.parameterValueTextFields = new ArrayList<JTextField>();
		
		initialize();	
	}
	
	private void initialize(){
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {7,7,7,0};
		gridBagLayout.rowHeights = new int[] {7,7,7,7};
		setLayout(gridBagLayout);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton optimalButton = new JButton();
		optimalButton.setText("Estimate Parameters");
		optimalButton.setToolTipText("Set best parameters through optimization with experimental data");
		optimalButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					runAndSetBestParameters();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton evaluationButton = new JButton();
		evaluationButton.setText("Show Confidence Intervals");
		evaluationButton.setToolTipText("Get confidence intervals for each parameter based on confidence level");
		evaluationButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				computeProfileLikelihood();
			}
		});
		
		buttonPanel.add(optimalButton);
		buttonPanel.add(new JLabel("   "));
		buttonPanel.add(evaluationButton);
		buttonPanel.add(new JLabel("   "));
		
		final GridBagConstraints gridBagConstraints_0 = new GridBagConstraints();
		gridBagConstraints_0.weightx = 1;
		gridBagConstraints_0.weighty = 0;
		gridBagConstraints_0.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_0.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_0.gridy = 0;
		gridBagConstraints_0.gridx = 0;
		add(parameterPanel, gridBagConstraints_0);

		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.weightx = 1;
		gridBagConstraints_1.weighty = 0;
		gridBagConstraints_1.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 0;
		add(buttonPanel, gridBagConstraints_1);

		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.weightx = 1;
		gridBagConstraints_2.weighty = 1;
		gridBagConstraints_2.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.gridy = 2;
		gridBagConstraints_2.gridx = 0;
		add(multisourcePlotPane, gridBagConstraints_2);
	}
	
	
	
	public void init(OptContext optContext, NormalizedSampleFunction[] fittedROIs, LocalWorkspace localWorkspace){
		this.optContext = optContext;
		this.fittedROIs = fittedROIs;
		this.localWorkspace = localWorkspace;

		Parameter[] parameters = optContext.getParameters();

		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {7,7,7,0};
		gridBagLayout.rowHeights = new int[] {7,7,7,7};
		parameterPanel.setLayout(gridBagLayout);
		
		int gridLayout_Y = 0;
		for (int i=0;i<parameters.length; i++){

			final JLabel label = new JLabel();
			label.setText(parameters[i].getName());
			final GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
			gridBagConstraints0.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints0.anchor = GridBagConstraints.EAST;
			gridBagConstraints0.gridy = gridLayout_Y;
			gridBagConstraints0.gridx = 0;
			parameterPanel.add(label, gridBagConstraints0);

			JTextField textField = new JTextField();
			parameterValueTextFields.add(textField);
			textField.getDocument().addUndoableEditListener(EDIT_LISTENER);
			textField.setPreferredSize(new Dimension(125, 20));
			textField.addActionListener(OPTIMIZER_VALUE_ACTION_LISTENER);
			textField.setMinimumSize(new Dimension(125, 20));
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(2, 2, 2, 0);
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridy = gridLayout_Y;
			gridBagConstraints.gridx = 1;
			parameterPanel.add(textField, gridBagConstraints);

			JButton setButton = new JButton();
			parameterValueSetButtons.add(setButton);
			setButton.setMargin(new Insets(2, 1, 2, 1));
			setButton.addActionListener(SET_ACTION_LISTENER);
			setButton.setText("Set");
			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.insets = new Insets(2, 0, 2, 2);
			gridBagConstraints_1.gridy = gridLayout_Y;
			gridBagConstraints_1.gridx = 2;
			parameterPanel.add(setButton, gridBagConstraints_1);

			JSlider slider = new JSlider();
			parameterValueSliders.add(slider);
			slider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
			slider.setPaintLabels(true);
			final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
			gridBagConstraints_4.weighty = 1;
			gridBagConstraints_4.insets = new Insets(2, 2, 2, 0);
			gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints_4.weightx = 1;
			gridBagConstraints_4.gridy = gridLayout_Y;
			gridBagConstraints_4.gridx = 3;
			parameterPanel.add(slider, gridBagConstraints_4);
			
			gridLayout_Y++;
		}
		
		for (JSlider slider : parameterValueSliders){
			slider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		}
		try{
			for (int parameterIndex=0;parameterIndex<parameters.length;parameterIndex++){
				Parameter parameter = parameters[parameterIndex];
				JSlider slider = parameterValueSliders.get(parameterIndex);
				Hashtable<Integer, JComponent> sliderLabelTable = new Hashtable<Integer, JComponent>();
				slider.setMinimum(0);
				slider.setMaximum(2000);
				slider.setValue(0);
				sliderLabelTable.put(0, new JLabel(parameter.getLowerBound()+""));
				sliderLabelTable.put(2000,new JLabel(parameter.getUpperBound()+""));
				//
				// way of doing a log scale
				//
//				bleachWhileMonitorSliderLabelTable.put(new Integer(0), new JLabel(FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound()+""));
//				bleachWhileMonitorSliderLabelTable.put(new Integer(20), new JLabel("1e-4"));
//				bleachWhileMonitorSliderLabelTable.put(new Integer(40), new JLabel("1e-3"));
//				bleachWhileMonitorSliderLabelTable.put(new Integer(60), new JLabel("1e-2"));
//				bleachWhileMonitorSliderLabelTable.put(new Integer(80), new JLabel("1e-1"));
//				bleachWhileMonitorSliderLabelTable.put(new Integer(100),new JLabel(FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound()+""));
				
				slider.setLabelTable(null);//Kludge for WindowBuilder otherwise not display correctly
				slider.setLabelTable(sliderLabelTable);
			}
			
		}finally{
			for (JSlider slider : parameterValueSliders){
				slider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
			}
		}

		double[] parameterValues = new double[parameters.length];
		for (int i=0;i<parameters.length;i++){
			parameterValues[i] = parameters[i].getInitialGuess();
		}
		setParameterValues(parameterValues);

		plotDerivedSimulationResults();

	}

	private void parameterValuesChanged() {
		plotDerivedSimulationResults();
		
	}

	public void computeProfileLikelihood()
	{
		final Repository repository = new MemoryRepository();
		Workflow workflow = new Workflow("profileLikelihoodWorkflow");
		final TaskContext context = new TaskContext(workflow,repository,localWorkspace);
		System.err.println("OptModelParamPanel.showParameterEvaluation(): how do we pass in the initial guess to ProfileLikelihood code??? should be an independent input to ProfileLikelihood so that it is explicit ... and OptContext is immutable.????");
		final RunProfileLikelihoodGeneral runProfileLikelihoodGeneral = new RunProfileLikelihoodGeneral("internal");
		WorkflowParameter<OptContext> optContextParam = workflow.addParameter(OptContext.class, "optContext", repository, optContext);
		workflow.connectParameter(optContextParam, runProfileLikelihoodGeneral.optContext);
		workflow.addTask(runProfileLikelihoodGeneral);
		final DisplayProfileLikelihoodPlots displayProfileLikelihoodPlots = new DisplayProfileLikelihoodPlots("displayProfileLikihood");
		workflow.connect2(runProfileLikelihoodGeneral.profileData, displayProfileLikelihoodPlots.profileData);
		WorkflowParameter<String> titleParam = workflow.addParameter(String.class, "title", repository, "profile likelihood");
		workflow.connectParameter(titleParam, displayProfileLikelihoodPlots.title);
		workflow.addTask(displayProfileLikelihoodPlots);

		AsynchClientTask evaluateTask = new AsynchClientTask("Prepare to evaluate parameters ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				runProfileLikelihoodGeneral.compute(context, getClientTaskStatusSupport());
			}
		};
		
		AsynchClientTask showResultTask = new AsynchClientTask("Showing profile likelihood and confidence intervals ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				displayProfileLikelihoodPlots.compute(context, getClientTaskStatusSupport());
			}
		};
		//dispatch
		ClientTaskDispatcher.dispatch(OptModelParamPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[]{evaluateTask, showResultTask}, false, true, null, true); 
	}
	
	public void runAndSetBestParameters() throws Exception
	{
		Parameter[] parameters = optContext.getParameters();
		final Parameter[] inParameters = new Parameter[parameters.length];
		double[] values = getCurrentParameterValues();
		for (int i=0;i<inParameters.length;i++){
			Parameter p = parameters[i];
			inParameters[i] = new Parameter(p.getName(), p.getLowerBound(), p.getUpperBound(), p.getScale(), values[i]);
		}
		
		AsynchClientTask optTask = new AsynchClientTask("Running optimization ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				String[] outParaNames = new String[inParameters.length];
				double[] outParaValues = new double[inParameters.length];
				OptContextSolver.estimate(optContext, inParameters, outParaNames, outParaValues);
				hashTable.put("outParaValues",outParaValues);
			}
		};
		
		AsynchClientTask showResultTask = new AsynchClientTask("Running optimization ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				double[] outParaVales = (double[])hashTable.get("outParaValues");
				setParameterValues(outParaVales);
				plotDerivedSimulationResults();
			}
		};
		//dispatch
		ClientTaskDispatcher.dispatch(OptModelParamPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[]{optTask, showResultTask}, false, false, null, true); 
	}
	
	//set text fields, update slider values and plot.
	protected void setParameterValues(double[] parameterValues){
		for (int i=0; i<optContext.getParameters().length; i++){
			parameterValueTextFields.get(i).setText(parameterValues[i]+"");
		}
		try {
			B_HOLD_FIRE = true;
			//one click is enough, The set action listener goes through the whole textfields and sliders
			parameterValueSetButtons.get(0).doClick();
		} finally {
			B_HOLD_FIRE = false;
		}
		//set all text fields positions to 0
		setAllTextFieldsPosition(0);
	}
	
	private void setAllTextFieldsPosition(int pos)
	{
		for (JTextField textField : parameterValueTextFields){
			textField.setCaretPosition(0);
		}
	}
	
	public double[] getCurrentParameterValues(){
		
		double[] paramValues = new double[optContext.getParameters().length];
		for (int i=0;i<paramValues.length;i++){
			paramValues[i] = Double.parseDouble(parameterValueTextFields.get(i).getText());
		}
		return paramValues;
	}
	
	private void plotDerivedSimulationResults()
	{
		try{
			RowColumnResultSet simulatedData = optContext.computeSolution(getCurrentParameterValues(),fittedROIs,optContext.getExperimentalTimePoints());
			RowColumnResultSet experimentalData = optContext.getExperimentalData(fittedROIs);
			DataSource simDataSource  = new DataSource.DataSourceRowColumnResultSet("opt", simulatedData,false);
			DataSource expDataSource  = new DataSource.DataSourceRowColumnResultSet("exp", experimentalData,true);
			DataSource[] newDataSourceArr = new DataSource[2];
			newDataSourceArr[ARRAY_INDEX_EXPDATASOURCE] = expDataSource;
			newDataSourceArr[ARRAY_INDEX_SIMDATASOURCE] = simDataSource;
			
			int roiSize = fittedROIs.length;//double valid ROI colors (not include cell and background)
			Color[] uniqueColors = ColorUtil.generateAutoColor(roiSize, getBackground(), new Integer(0));
			Color[] colors = new Color[roiSize*2];
			for (int i=0;i<roiSize;i++){
				colors[i] = uniqueColors[i];
				colors[i+roiSize] = uniqueColors[i];
			}
			
			int[] selectedIndices = multisourcePlotPane.getUnsortedSelectedIndices();
			multisourcePlotPane.setDataSources(newDataSourceArr, colors);
			if(selectedIndices.length == 0){
				multisourcePlotPane.selectAll();
			}else{
				multisourcePlotPane.setUnsortedSelectedIndices(selectedIndices);
			}
			
		}catch(Exception e2){
			e2.printStackTrace();
			DialogUtils.showErrorDialog(this,"Error graphing Optimizer data "+e2.getMessage());
		}

	}	
}
