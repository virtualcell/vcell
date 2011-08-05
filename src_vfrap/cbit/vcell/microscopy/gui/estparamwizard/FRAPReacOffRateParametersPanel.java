/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.vcell.optimization.ProfileData;
import org.vcell.optimization.gui.ConfidenceIntervalPlotPanel;
import org.vcell.optimization.gui.ProfileDataPanel;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.math.gui.ExpressionCanvas;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptFunctions;
import cbit.vcell.microscopy.FRAPOptimizationUtils;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.opt.Parameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

@SuppressWarnings("serial")
public class FRAPReacOffRateParametersPanel extends JPanel
{
	private final JLabel fittingParamValueLabel;
	private final JSlider offRateSlider;
	private final JSlider bleachWhileMonitorSlider;
	private final JTextField offRateTextField;
	
	private final JTextField bleachWhileMonitorRateTextField;
	private final JButton offRateSetButton;
	private final JButton bleachWhileMonitorSetButton;
	
	private ExpressionCanvas expressionCanvas1;
	private ExpressionCanvas expressionCanvas2;
	
	private FRAPWorkspace frapWorkspace;
	private boolean B_HOLD_FIRE = false;
	private final ActionListener OPTIMIZER_VALUE_ACTION_LISTENER =
		new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if(e.getSource() == offRateTextField){
					SET_ACTION_LISTENER.actionPerformed(new ActionEvent(offRateSetButton,e.getID(),offRateSetButton.getActionCommand()));
				}else if(e.getSource() == bleachWhileMonitorRateTextField){
					SET_ACTION_LISTENER.actionPerformed(new ActionEvent(bleachWhileMonitorSetButton,e.getID(),bleachWhileMonitorSetButton.getActionCommand()));
				}
			}
		};
		
	private final ChangeListener OPTIMIZER_SLIDER_CHANGE_LISTENER =
		new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				if(e.getSource() == offRateSlider){
					double value =
						FRAPModel.REF_REACTION_OFF_RATE.getLowerBound()+
						(FRAPModel.REF_REACTION_OFF_RATE.getUpperBound()-FRAPModel.REF_REACTION_OFF_RATE.getLowerBound())*
						((double)offRateSlider.getValue()/(double)offRateSlider.getMaximum());
					offRateTextField.setText(value+"");
					offRateTextField.setCaretPosition(0);
					offRateSetButton.setEnabled(false);
				}else if(e.getSource() == bleachWhileMonitorSlider){
					double value = FRAPModel.REF_BWM_LOG_VAL_MIN + (FRAPModel.REF_BWM_LOG_VAL_MAX - FRAPModel.REF_BWM_LOG_VAL_MIN)* 
					               ((double)bleachWhileMonitorSlider.getValue()/(double)bleachWhileMonitorSlider.getMaximum());
					double realVal = Math.pow(10,value);
					if(realVal > (Math.pow(10, FRAPModel.REF_BWM_LOG_VAL_MIN)-FRAPOptimizationUtils.epsilon) && realVal <(Math.pow(10, FRAPModel.REF_BWM_LOG_VAL_MIN)+FRAPOptimizationUtils.epsilon))
					{
						realVal = 0;
					}
					bleachWhileMonitorRateTextField.setText(realVal+"");
					bleachWhileMonitorRateTextField.setCaretPosition(0);
					bleachWhileMonitorSetButton.setEnabled(false);
				}
							
				if(!((JSlider)e.getSource()).getValueIsAdjusting()){
					firePropertyChange(FRAPSingleWorkspace.PROPERTY_CHANGE_OPTIMIZER_VALUE, null, null);
				}
			}
		};
		
	private ActionListener SET_ACTION_LISTENER =
		new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try{
					offRateSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					bleachWhileMonitorSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					
					//get mobile fractions
//					if(e.getSource() == diffusionRateSetButton){
						double value = Double.parseDouble(offRateTextField.getText());
						if(value < FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound()){
							value = FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound();
						}
						if(value > FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound()){
							value = FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound();
						}
						offRateTextField.setText(value+"");
						offRateTextField.setCaretPosition(0);
						int sliderValue = (int)
							(((value-FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound())*offRateSlider.getMaximum())/
							(FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound()-FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound()));
						if(sliderValue < offRateSlider.getMinimum()){
							sliderValue = offRateSlider.getMinimum();
						}
						if(sliderValue > offRateSlider.getMaximum()){
							sliderValue = offRateSlider.getMaximum();
						}
						offRateSlider.setValue(sliderValue);
						offRateSetButton.setEnabled(false);
//					}else if(e.getSource() == bleachWhileMonitorSetButton){
						value = Double.parseDouble(bleachWhileMonitorRateTextField.getText());
						
						if(value <= (Math.pow(10, FRAPModel.REF_BWM_LOG_VAL_MIN)+FRAPOptimizationUtils.epsilon)){
							value = FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound();
						}
						if(value > FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound()){
							value = FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound();
						}
						bleachWhileMonitorRateTextField.setText(value+"");
						bleachWhileMonitorRateTextField.setCaretPosition(0);
						if(value == FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound())
						{
							sliderValue = 0;
						}
						else
						{
							double tempVal = Math.log10(value);
							sliderValue = (int)
								(((tempVal-FRAPModel.REF_BWM_LOG_VAL_MIN)*bleachWhileMonitorSlider.getMaximum())/
								(FRAPModel.REF_BWM_LOG_VAL_MAX-FRAPModel.REF_BWM_LOG_VAL_MIN));
							if(sliderValue < bleachWhileMonitorSlider.getMinimum()){
								sliderValue = bleachWhileMonitorSlider.getMinimum();
							}
							if(sliderValue > bleachWhileMonitorSlider.getMaximum()){
								sliderValue = bleachWhileMonitorSlider.getMaximum();
							}
						}
						bleachWhileMonitorSlider.setValue(sliderValue);
						bleachWhileMonitorSetButton.setEnabled(false);
						//set fitting parameter label
						value = Double.parseDouble(fittingParamValueLabel.getText());
						if(value < FRAPModel.REF_BS_CONCENTRATION_OR_A.getLowerBound()){
							value = FRAPModel.REF_BS_CONCENTRATION_OR_A.getLowerBound();
						}
						if(value > FRAPModel.REF_BS_CONCENTRATION_OR_A.getUpperBound()){
							value = FRAPModel.REF_BS_CONCENTRATION_OR_A.getUpperBound();
						}
						fittingParamValueLabel.setText(value+"");
						//set all text fields positions to 0
						setAllTextFieldsPosition(0);
//					}
					if(!B_HOLD_FIRE){
						firePropertyChange(FRAPSingleWorkspace.PROPERTY_CHANGE_OPTIMIZER_VALUE, null, null);
					}
				}catch (Exception e2){
					e2.printStackTrace();
					DialogUtils.showErrorDialog(FRAPReacOffRateParametersPanel.this, "Error setting parameter value for "+
							(e.getSource() == offRateSetButton?"diffusionRate":"")+
							(e.getSource() == bleachWhileMonitorSetButton?"bleachWhileMonitor":"")+
							"\n"+e2.getMessage());
				}finally{
					offRateSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					bleachWhileMonitorSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
				}
			}
		};
	private UndoableEditListener EDIT_LISTENER =
		new UndoableEditListener() {
			public void undoableEditHappened(final UndoableEditEvent e) {
				if(e.getSource() == offRateTextField.getDocument()){
					offRateSetButton.setEnabled(true);
				}else if(e.getSource() == bleachWhileMonitorRateTextField.getDocument()){
					bleachWhileMonitorSetButton.setEnabled(true);
				}
			}
		};
	
		
	public FRAPReacOffRateParametersPanel() {
		super();
		setLayout(new /*GridBagLayout()*/ BorderLayout());
		
		JPanel panel_left = new JPanel();
		panel_left.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints_0 = new GridBagConstraints();
		gridBagConstraints_0.fill = GridBagConstraints.BOTH;
		gridBagConstraints_0.gridy = 0;
		gridBagConstraints_0.gridx = 0;
		gridBagConstraints_0.gridwidth = 2;
		gridBagConstraints_0.gridheight = 1;
		add(panel_left, /*gridBagConstraints_0*/BorderLayout.CENTER);
		

		JLabel offRateLabel = new JLabel();
		offRateLabel.setText("Reaction off rate (1/s):");
		GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_9.anchor = GridBagConstraints.EAST;
		gridBagConstraints_9.gridy = 0;
		gridBagConstraints_9.gridx = 0;
		panel_left.add(offRateLabel, gridBagConstraints_9);

		offRateTextField = new JTextField();
		offRateTextField.getDocument().addUndoableEditListener(EDIT_LISTENER);
		offRateTextField.setPreferredSize(new Dimension(125, 20));
		offRateTextField.addActionListener(OPTIMIZER_VALUE_ACTION_LISTENER);
		offRateTextField.setMinimumSize(new Dimension(125, 20));
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 1;
		panel_left.add(offRateTextField, gridBagConstraints);

		offRateSetButton = new JButton();
		offRateSetButton.setMargin(new Insets(2, 1, 2, 1));
		offRateSetButton.addActionListener(SET_ACTION_LISTENER);
		offRateSetButton.setText("Set");
		GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(2, 0, 2, 2);
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 2;
		panel_left.add(offRateSetButton, gridBagConstraints_1);

		offRateSlider = new JSlider();
		offRateSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		offRateSlider.setPaintLabels(true);
		GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.weighty = 1;
		gridBagConstraints_4.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.weightx = 1;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.gridx = 3;
		panel_left.add(offRateSlider, gridBagConstraints_4);

		JLabel bleachWhileMonitorLabel = new JLabel();
		bleachWhileMonitorLabel.setText("Bleach Monitor Rate(1/s):");
		GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_12.anchor = GridBagConstraints.EAST;
		gridBagConstraints_12.gridy = 1;
		gridBagConstraints_12.gridx = 0;
		panel_left.add(bleachWhileMonitorLabel, gridBagConstraints_12);

		bleachWhileMonitorRateTextField = new JTextField();
		bleachWhileMonitorRateTextField.getDocument().addUndoableEditListener(EDIT_LISTENER);
		bleachWhileMonitorRateTextField.setPreferredSize(new Dimension(125, 20));
		bleachWhileMonitorRateTextField.setMinimumSize(new Dimension(125, 20));
		bleachWhileMonitorRateTextField.addActionListener(OPTIMIZER_VALUE_ACTION_LISTENER);
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 1;
		panel_left.add(bleachWhileMonitorRateTextField, gridBagConstraints_3);

		bleachWhileMonitorSetButton = new JButton();
		bleachWhileMonitorSetButton.addActionListener(SET_ACTION_LISTENER);
		bleachWhileMonitorSetButton.setMargin(new Insets(2, 1, 2, 1));
		bleachWhileMonitorSetButton.setText("Set");
		GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.insets = new Insets(2, 0, 2, 2);
		gridBagConstraints_10.gridy = 1;
		gridBagConstraints_10.gridx = 2;
		panel_left.add(bleachWhileMonitorSetButton, gridBagConstraints_10);

		bleachWhileMonitorSlider = new JSlider();
		bleachWhileMonitorSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		bleachWhileMonitorSlider.setPaintLabels(true);
		GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_8.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_8.weightx = 1;
		gridBagConstraints_8.weighty = 1.5;
		gridBagConstraints_8.gridy = 1;
		gridBagConstraints_8.gridx = 3;
		panel_left.add(bleachWhileMonitorSlider, gridBagConstraints_8);		

		JLabel fittingParamLabel = new JLabel();
		fittingParamLabel.setText("Fitting parameter (A):");
		GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.anchor = GridBagConstraints.EAST;
		gridBagConstraints_16.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_16.gridy = 2;
		gridBagConstraints_16.gridx = 0;
		panel_left.add(fittingParamLabel, gridBagConstraints_16);

		fittingParamValueLabel = new JLabel();
		fittingParamValueLabel.setIconTextGap(0);
		fittingParamValueLabel.setText("       ");
		GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.anchor = GridBagConstraints.WEST;
		gridBagConstraints_19.gridy = 2;
		gridBagConstraints_19.gridx = 1;
		panel_left.add(fittingParamValueLabel, gridBagConstraints_19);
		
		JPanel buttonPanel = new JPanel();
		JButton optimalButton = new JButton();
		optimalButton.setText("Estimate Parameters");
		optimalButton.setFont(new Font("", Font.PLAIN, 11));
		optimalButton.setToolTipText("Set best parameters through optimization with experimental data");
		optimalButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) 
			{
				try {
					runAndSetBestParameters();
				} catch (Exception ex) 
				{
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(FRAPReacOffRateParametersPanel.this, "Error in getting best estimates: " + ex.getMessage());
				}
			}
		});
		JButton evaluationButton = new JButton();
		evaluationButton.setText("Show Confidence Intervals");
		evaluationButton.setFont(new Font("", Font.PLAIN, 11));
		evaluationButton.setToolTipText("Get confidence intervals for each parameter based on confidence level");
		evaluationButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				showParameterEvaluation();
			}
		});
		buttonPanel.add(optimalButton);
		buttonPanel.add(new JLabel("   "));
		buttonPanel.add(evaluationButton);
		GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.fill = GridBagConstraints.BOTH;
		gridBagConstraints_20.gridy = 2;
		gridBagConstraints_20.gridx = 3;
		panel_left.add(buttonPanel, gridBagConstraints_20);
		
		JPanel panel_right = new JPanel( new GridBagLayout());
		GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 0;
		gridBagConstraints_2.gridwidth = 1;
		gridBagConstraints_2.gridheight = 1;
		panel_right.setBorder(new EtchedBorder());
		add(panel_right, /*gridBagConstraints_2*/BorderLayout.SOUTH);
		expressionCanvas1 = new ExpressionCanvas();
		GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.fill = GridBagConstraints.BOTH;
		gridBagConstraints_5.gridy = 0;
		gridBagConstraints_5.gridx = 0;
		expressionCanvas2 = new ExpressionCanvas();
		GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.fill = GridBagConstraints.BOTH;
		gridBagConstraints_6.gridy = 0;
		gridBagConstraints_6.gridx = 1;
//		expressionCanvas1.setPreferredSize(expressionCanvas1.getMinimumSize());
//		expressionCanvas1.setBackground(Color.blue);
//		expressionCanvas2.setPreferredSize(expressionCanvas2.getMinimumSize());
//		expressionCanvas2.setBackground(Color.red);
		panel_right.add(expressionCanvas1,gridBagConstraints_5);
		panel_right.add(expressionCanvas2,gridBagConstraints_6);
		
		
		initialize();
	}

	private void initialize(){
		
		offRateSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		bleachWhileMonitorSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		try{
			Hashtable<Integer, JComponent> diffusionSliderLabelTable = new Hashtable<Integer, JComponent>();
			offRateSlider.setMinimum(0);
			offRateSlider.setMaximum(1000);
			offRateSlider.setValue(0);
			diffusionSliderLabelTable.put(0, new JLabel(FRAPModel.REF_REACTION_OFF_RATE.getLowerBound()+""));
			diffusionSliderLabelTable.put(1000,new JLabel(FRAPModel.REF_REACTION_OFF_RATE.getUpperBound()+""));
			offRateSlider.setLabelTable(null);//Kludge for WindowBuilder otherwise not display correctly
			offRateSlider.setLabelTable(diffusionSliderLabelTable);
			
			Hashtable<Integer, JComponent> bleachWhileMonitorSliderLabelTable = new Hashtable<Integer, JComponent>();
			bleachWhileMonitorSlider.setMinimum(0);
			bleachWhileMonitorSlider.setMaximum(100);
			bleachWhileMonitorSlider.setValue(0);
			bleachWhileMonitorSlider.setMajorTickSpacing(20);
			bleachWhileMonitorSlider.setPaintTicks(true);
			bleachWhileMonitorSliderLabelTable.put(new Integer(0), new JLabel(FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound()+""));
			bleachWhileMonitorSliderLabelTable.put(new Integer(20), new JLabel("1e-4"));
			bleachWhileMonitorSliderLabelTable.put(new Integer(40), new JLabel("1e-3"));
			bleachWhileMonitorSliderLabelTable.put(new Integer(60), new JLabel("1e-2"));
			bleachWhileMonitorSliderLabelTable.put(new Integer(80), new JLabel("1e-1"));
			bleachWhileMonitorSliderLabelTable.put(new Integer(100),new JLabel(FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound()+""));
			bleachWhileMonitorSlider.setLabelTable(null);//Kludge for WindowBuilder otherwise not display correctly
			bleachWhileMonitorSlider.setLabelTable(bleachWhileMonitorSliderLabelTable);
			//initialize expression canvas
			initializeExpression();
		}finally{
			offRateSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
			bleachWhileMonitorSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
	
		}
	}
	
	public void setData(Parameter[] modelParameters) throws Exception
	{
		setParameterValues(
				new Double(modelParameters[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess()),
				new Double(modelParameters[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION].getInitialGuess()),
				new Double(modelParameters[FRAPModel.INDEX_OFF_RATE].getInitialGuess()));
		
	}
	//set text fields, update slider values and plot.
	protected void setParameterValues(Double bwmRate,Double fittingParamA,Double offRate){
		bleachWhileMonitorRateTextField.setText((bwmRate != null? bwmRate.doubleValue()+"":"0"));
		offRateTextField.setText(offRate.doubleValue()+"");
		fittingParamValueLabel.setText(fittingParamA.doubleValue() + "");
		B_HOLD_FIRE = true;
		//one click is enough, The set action listener goes through the whole textfields and sliders
		offRateSetButton.doClick();

		B_HOLD_FIRE = false;
		//set all text fields positions to 0
		setAllTextFieldsPosition(0);
	}
	
	private void setAllTextFieldsPosition(int pos)
	{
		offRateTextField.setCaretPosition(pos);
		bleachWhileMonitorRateTextField.setCaretPosition(pos);
	}
	
	private static Parameter[] createParameterArray(double bwmRate, double a, double offRate )
	{
		Parameter[] params = null;
		
		Parameter bwmRateParam = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE],
										 FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
					                     FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
					                     FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(), bwmRate);
		Parameter AParam = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION],
						                 FRAPModel.REF_BS_CONCENTRATION_OR_A.getLowerBound(), 
						                 FRAPModel.REF_BS_CONCENTRATION_OR_A.getUpperBound(),
						                 FRAPModel.REF_BS_CONCENTRATION_OR_A.getScale(), a);
		Parameter offRateParam = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE],
						                 FRAPModel.REF_REACTION_OFF_RATE.getLowerBound(), 
						                 FRAPModel.REF_REACTION_OFF_RATE.getUpperBound(),
						                 FRAPModel.REF_REACTION_OFF_RATE.getScale(),offRate);
		params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_REACTION_OFF_RATE];
		params[FRAPModel.INDEX_BLEACH_MONITOR_RATE]= bwmRateParam;
		params[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION] = AParam;
		params[FRAPModel.INDEX_OFF_RATE] = offRateParam;
		
		return params;
	}
	
	public Parameter[] getCurrentParameters(){
		double offRate, bleachWhileMonitorRate, a;
		try
		{
			offRate = Double.parseDouble(offRateTextField.getText());
			bleachWhileMonitorRate = Double.parseDouble(bleachWhileMonitorRateTextField.getText());
			a = Double.parseDouble(fittingParamValueLabel.getText());
		}catch(NumberFormatException e)
		{
			return null;
		}
		
		return createParameterArray(bleachWhileMonitorRate, a, offRate);
	}
	
	private double[][] getFitData(Parameter[] userParams) throws Exception{
		if(userParams == null || userParams.length <= 0)
		{
			return null;
		}
//		double[][] fitData = frapOptData.getFitData(userParams); 

		return null;
	}
	
	public double[][] getCurrentFitData() throws Exception{
		return getFitData(getCurrentParameters());
	}
	
	public String getDiffusionRateString() {
		return offRateTextField.getText();
	}

	public String getImmoFracString() {
		return fittingParamValueLabel.getText();
	}
	
	public String getBleachWhileMonitorRateString() {
		return bleachWhileMonitorRateTextField.getText();
	}
	
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
	{
		this.frapWorkspace = frapWorkspace;
	}

	public  FRAPWorkspace getFrapWorkspace()
	{
		return frapWorkspace;
	}
	
	private void initializeExpression() 
	{
		try{
			String[] prefixes = new String[] { "I_bleached(t) = ", "I_cell(t)= " };
			Expression[] expressions = new Expression[] { new Expression(FRAPOptFunctions.FUNC_RECOVERY_BLEACH_REACTION_DOMINANT), new Expression(FRAPOptFunctions.FUNC_CELL_INTENSITY)};
			String[] suffixes = new String[] { "", "" };
			expressionCanvas1.setExpressions(expressions,prefixes,suffixes);
			expressionCanvas2.setStrings(new String[]{"k_off : Reaction off rate.  A : Fitting parameter.   ", /*"I_init : First post bleach average intensity.", */"beta : Bleach while monitoring rate."});
		}catch (ExpressionException e2){
			e2.printStackTrace(System.out);
		}		
	}

	private String checkParameters()
	{
		String errMsg = "";
		Parameter[] params = getCurrentParameters();//checks null and illegal formats
		if (params == null)
		{
			errMsg = "Some of the editable parameters are empty or in illegal forms!";
		}
		return errMsg;
	}
	
	public void runAndSetBestParameters() throws Exception
	{
		String errorStr = checkParameters();
		if(errorStr.equals(""))
		{
			Parameter[] bestParameters = frapWorkspace.getWorkingFrapStudy().getFrapOptFunc().getBestParamters(frapWorkspace.getWorkingFrapStudy().getFrapData(), null, true);
			setParameterValues(
					new Double(bestParameters[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess()),
					new Double(bestParameters[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION].getInitialGuess()),//binding site is used to store fitting parameter A
					new Double(bestParameters[FRAPModel.INDEX_OFF_RATE].getInitialGuess())
					);
			firePropertyChange(FRAPSingleWorkspace.PROPERTY_CHANGE_OPTIMIZER_VALUE, null, null);
		}
		else
		{
			throw new IllegalArgumentException(errorStr);
		}
	}
	
	public void showParameterEvaluation()
	{
		
		AsynchClientTask evaluateTask = new AsynchClientTask("Prepare to evaluate parameters ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				String errorStr = checkParameters();
				if(!errorStr.equals(""))
				{
					throw new IllegalArgumentException(errorStr);
				}
			}
		};
		
		AsynchClientTask showResultTask = new AsynchClientTask("Showing profile likelihood and confidence intervals ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				ProfileData[] profileData = frapWorkspace.getWorkingFrapStudy().getProfileData_reactionOffRate();
				if(profileData != null && profileData.length > 0)
				{
					//put plotpanes of different parameters' profile likelihoods into a base panel
					JPanel basePanel= new JPanel();
			    	basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
					for(int i=0; i<profileData.length; i++)
					{
						ConfidenceIntervalPlotPanel plotPanel = new ConfidenceIntervalPlotPanel();
						plotPanel.setProfileSummaryData(FRAPOptimizationUtils.getSummaryFromProfileData(profileData[i]));
						plotPanel.setBorder(new EtchedBorder());
						String paramName = "";
						if(profileData[i].getProfileDataElements().size() > 0)
						{
							paramName = profileData[i].getProfileDataElements().get(0).getParamName();
						}
						ProfileDataPanel profileDataPanel = new ProfileDataPanel(plotPanel, paramName);
						basePanel.add(profileDataPanel);
					}
					JScrollPane scrollPane = new JScrollPane(basePanel);
			    	scrollPane.setAutoscrolls(true);
			    	scrollPane.setPreferredSize(new Dimension(620, 600));
			    	scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			    	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			    	//show plots in a dialog
			    	DialogUtils.showComponentCloseDialog(FRAPReacOffRateParametersPanel.this, scrollPane, "Profile Likelihood of Parameters");
				}
			}
		};
		//dispatch
		ClientTaskDispatcher.dispatch(FRAPReacOffRateParametersPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[]{evaluateTask, showResultTask}, false, true, null, true); 
	}
	
	public static void main(String argv[])
	{
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			FRAPReacOffRateParametersPanel aPanel = new FRAPReacOffRateParametersPanel();
			frame.setContentPane(aPanel);
//			frame.pack();
			frame.setSize(800,500);
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
			
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
	
}
