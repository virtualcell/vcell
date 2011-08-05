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

import java.awt.Dimension;
import java.awt.FlowLayout;
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
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPOptimizationUtils;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.gui.DiffRateHelpPanel;
import cbit.vcell.opt.Parameter;

@SuppressWarnings("serial")
public class FRAPDiffOneParamPanel extends JPanel
{
	private final JLabel immoFracValueLabel;
	private final JSlider diffusionRateSlider;
	private final JSlider mobileFractionSlider;
	private final JSlider bleachWhileMonitorSlider;
	private final JTextField diffusionRateTextField;
	
	private final JTextField mobileFractionTextField;
	private final JTextField bleachWhileMonitorRateTextField;
	private final JButton diffusionRateSetButton;
	private final JButton mobileFractionSetButton;
	private final JButton bleachWhileMonitorSetButton;
	private DiffRateHelpPanel diffRateHelpPanel = null;
	private FRAPOptData frapOptData;
	private FRAPSingleWorkspace frapWorkspace;

	private boolean B_HOLD_FIRE = false;
	private boolean isExecuting = false;//for control whether a paragraph should execute in OPTIMIZER_SLIDER_CHANGE_LISTENER or not, when getValueIsAdjusting() is false.
	
	private final ActionListener OPTIMIZER_VALUE_ACTION_LISTENER =
		new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if(e.getSource() == diffusionRateTextField){
					SET_ACTION_LISTENER.actionPerformed(new ActionEvent(diffusionRateSetButton,e.getID(),diffusionRateSetButton.getActionCommand()));
				}else if(e.getSource() == mobileFractionTextField){
					SET_ACTION_LISTENER.actionPerformed(new ActionEvent(mobileFractionSetButton,e.getID(),mobileFractionSetButton.getActionCommand()));
				}else if(e.getSource() == bleachWhileMonitorRateTextField){
					SET_ACTION_LISTENER.actionPerformed(new ActionEvent(bleachWhileMonitorSetButton,e.getID(),bleachWhileMonitorSetButton.getActionCommand()));
				}
			}
		};
		
	private final ChangeListener OPTIMIZER_SLIDER_CHANGE_LISTENER =
		new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				if(e.getSource() == diffusionRateSlider){
					double value =
						FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound()+
						(FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound()-FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound())*
						((double)diffusionRateSlider.getValue()/(double)diffusionRateSlider.getMaximum());
					diffusionRateTextField.setText(value+"");
					diffusionRateTextField.setCaretPosition(0);
					diffusionRateSetButton.setEnabled(false);
				}else if(e.getSource() == mobileFractionSlider){
					double value =
						FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound()+
						(FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound()-FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound())*
						((double)mobileFractionSlider.getValue()/(double)mobileFractionSlider.getMaximum());
					mobileFractionTextField.setText(value+"");
					mobileFractionTextField.setCaretPosition(0);
					mobileFractionSetButton.setEnabled(false);
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
					if(!isExecuting)
					{
						isExecuting = true;
						try{
							double primaryFrac = Double.parseDouble(mobileFractionTextField.getText());
							double[] adjustedVals = adjustMobileFractions(primaryFrac);
							//primary				
							double value = adjustedVals[0];
							
							int sliderValue = (int)
								(((value-FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound())*mobileFractionSlider.getMaximum())/
								(FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound()-FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound()));
							if(sliderValue < mobileFractionSlider.getMinimum()){
								sliderValue = mobileFractionSlider.getMinimum();
							}
							if(sliderValue > mobileFractionSlider.getMaximum()){
								sliderValue = mobileFractionSlider.getMaximum();
							}
							mobileFractionSlider.setValue(sliderValue);
							mobileFractionTextField.setText(value+"");//this will trigger the undoableEditorListener to set the button on
							mobileFractionSetButton.setEnabled(false);//we set the button off again.
							mobileFractionTextField.setCaretPosition(0);
							//immobile
							immoFracValueLabel.setText(adjustedVals[1]+"");
						}finally{
							isExecuting = false;
						}
					}
					
					firePropertyChange(FRAPSingleWorkspace.PROPERTY_CHANGE_OPTIMIZER_VALUE, null, null);
				}
			}
		};
		
	private ActionListener SET_ACTION_LISTENER =
		new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try{
					diffusionRateSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					mobileFractionSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					bleachWhileMonitorSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					
					//get mobile fractions
					double primaryMFrac = Double.parseDouble(mobileFractionTextField.getText());
					double[] adjustedVals = adjustMobileFractions(primaryMFrac);
					
//					if(e.getSource() == diffusionRateSetButton){
						double value = Double.parseDouble(diffusionRateTextField.getText());
						if(value < FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound()){
							value = FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound();
						}
						if(value > FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound()){
							value = FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound();
						}
						diffusionRateTextField.setText(value+"");
						diffusionRateTextField.setCaretPosition(0);
						int sliderValue = (int)
							(((value-FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound())*diffusionRateSlider.getMaximum())/
							(FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound()-FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound()));
						if(sliderValue < diffusionRateSlider.getMinimum()){
							sliderValue = diffusionRateSlider.getMinimum();
						}
						if(sliderValue > diffusionRateSlider.getMaximum()){
							sliderValue = diffusionRateSlider.getMaximum();
						}
						diffusionRateSlider.setValue(sliderValue);
						diffusionRateSetButton.setEnabled(false);
//					}else if(e.getSource() == mobileFractionSetButton){
						value = adjustedVals[0];
						mobileFractionTextField.setText(value+"");
						mobileFractionTextField.setCaretPosition(0);
						sliderValue = (int)
							(((value-FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound())*mobileFractionSlider.getMaximum())/
							(FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound()-FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound()));
						if(sliderValue < mobileFractionSlider.getMinimum()){
							sliderValue = mobileFractionSlider.getMinimum();
						}
						if(sliderValue > mobileFractionSlider.getMaximum()){
							sliderValue = mobileFractionSlider.getMaximum();
						}
						mobileFractionSlider.setValue(sliderValue);
						mobileFractionSetButton.setEnabled(false);
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
						//set immobile label
						value = adjustedVals[1];
						immoFracValueLabel.setText(value+"");
						//set all text fields positions to 0
						setAllTextFieldsPosition(0);
//					}
					if(!B_HOLD_FIRE){
						firePropertyChange(FRAPSingleWorkspace.PROPERTY_CHANGE_OPTIMIZER_VALUE, null, null);
					}
				}catch (Exception e2){
					e2.printStackTrace();
					DialogUtils.showErrorDialog(FRAPDiffOneParamPanel.this, "Error setting parameter value for "+
							(e.getSource() == diffusionRateSetButton?"diffusionRate":"")+
							(e.getSource() == mobileFractionSetButton?"mobileFraction":"")+
							(e.getSource() == bleachWhileMonitorSetButton?"bleachWhileMonitor":"")+
							"\n"+e2.getMessage());
				}finally{
					diffusionRateSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					mobileFractionSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					bleachWhileMonitorSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
				}
			}
		};
	private UndoableEditListener EDIT_LISTENER =
		new UndoableEditListener() {
			public void undoableEditHappened(final UndoableEditEvent e) {
				if(e.getSource() == diffusionRateTextField.getDocument()){
					diffusionRateSetButton.setEnabled(true);
				}else if(e.getSource() == mobileFractionTextField.getDocument()){
					mobileFractionSetButton.setEnabled(true);
				}else if(e.getSource() == bleachWhileMonitorRateTextField.getDocument()){
					bleachWhileMonitorSetButton.setEnabled(true);
				}
			}
		};
	//array inFractions stores 0:primary mobile fraction 1:secondary M F. 2:immobile fraction
	private double[] adjustMobileFractions(double primaryMF)
	{
		double primaryMFrac = primaryMF;
		//constrain the upper and lower bound for primary and secondary mobile fractions
		primaryMFrac = Math.max(FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound(), primaryMFrac);
		primaryMFrac = Math.min(FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(), primaryMFrac);
		double immFrac = 0;
		//second diffusion is not applied
		immFrac = 1 - primaryMFrac;
		
		return new double[]{primaryMFrac, immFrac};
	}
		
	public FRAPDiffOneParamPanel() {
		super();
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
				runAndSetBestParameters();
			}
		});
		JButton evaluationButton = new JButton();
		evaluationButton.setText("Show Confidence Intervals");
		evaluationButton.setToolTipText("Get confidence intervals for each parameter based on confidence level");
		evaluationButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				showParameterEvaluation();
			}
		});
		
		JButton effDiffAnalysisButton = new JButton();
		effDiffAnalysisButton.setText("Analyze Effective Diffusion");
		effDiffAnalysisButton.setToolTipText("If the estimated diffusion rate is smaller than expected, please try effective diffusion analysis.");
		effDiffAnalysisButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				showEffectiveDiffusionAnalysis();
			}
		});
		
		buttonPanel.add(optimalButton);
		buttonPanel.add(new JLabel("   "));
		buttonPanel.add(evaluationButton);
		buttonPanel.add(new JLabel("   "));
		buttonPanel.add(effDiffAnalysisButton);
		
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.weightx = 1;
		gridBagConstraints_8.weighty = 1;
		gridBagConstraints_8.ipadx = -105;
		gridBagConstraints_8.anchor = GridBagConstraints.EAST;
		gridBagConstraints_8.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_8.gridy = 3;
		gridBagConstraints_8.gridx = 3;
		add(buttonPanel, gridBagConstraints_8);

		final JLabel diffusionRateLabel = new JLabel();
		diffusionRateLabel.setText("Primary  Diff.  Rate(um2/s):");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_9.anchor = GridBagConstraints.EAST;
		gridBagConstraints_9.gridy = 0;
		gridBagConstraints_9.gridx = 0;
		add(diffusionRateLabel, gridBagConstraints_9);

		diffusionRateTextField = new JTextField();
		diffusionRateTextField.getDocument().addUndoableEditListener(EDIT_LISTENER);
		diffusionRateTextField.setPreferredSize(new Dimension(125, 20));
		diffusionRateTextField.addActionListener(OPTIMIZER_VALUE_ACTION_LISTENER);
		diffusionRateTextField.setMinimumSize(new Dimension(125, 20));
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 1;
		add(diffusionRateTextField, gridBagConstraints);

		diffusionRateSetButton = new JButton();
		diffusionRateSetButton.setMargin(new Insets(2, 1, 2, 1));
		diffusionRateSetButton.addActionListener(SET_ACTION_LISTENER);
		diffusionRateSetButton.setText("Set");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(2, 0, 2, 2);
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 2;
		add(diffusionRateSetButton, gridBagConstraints_1);

		diffusionRateSlider = new JSlider();
		diffusionRateSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		diffusionRateSlider.setPaintLabels(true);
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.weighty = 1;
		gridBagConstraints_4.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.weightx = 1;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.gridx = 3;
		add(diffusionRateSlider, gridBagConstraints_4);

		final JLabel mobileFractionLabel = new JLabel();
		mobileFractionLabel.setText("Primary  Mobile Fraction:");
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_11.anchor = GridBagConstraints.EAST;
		gridBagConstraints_11.gridy = 1;
		gridBagConstraints_11.gridx = 0;
		add(mobileFractionLabel, gridBagConstraints_11);

		mobileFractionTextField = new JTextField();
		mobileFractionTextField.getDocument().addUndoableEditListener(EDIT_LISTENER);
		mobileFractionTextField.setPreferredSize(new Dimension(125, 20));
		mobileFractionTextField.setMinimumSize(new Dimension(125, 20));
		mobileFractionTextField.addActionListener(OPTIMIZER_VALUE_ACTION_LISTENER);
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 1;
		add(mobileFractionTextField, gridBagConstraints_2);

		mobileFractionSetButton = new JButton();
		mobileFractionSetButton.addActionListener(SET_ACTION_LISTENER);
		mobileFractionSetButton.setMargin(new Insets(2, 1, 2, 1));
		mobileFractionSetButton.setText("Set");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(2, 0, 2, 2);
		gridBagConstraints_5.gridy = 1;
		gridBagConstraints_5.gridx = 2;
		add(mobileFractionSetButton, gridBagConstraints_5);

		mobileFractionSlider = new JSlider();
		mobileFractionSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		mobileFractionSlider.setPaintLabels(true);
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.weighty = 1;
		gridBagConstraints_6.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.weightx = 1;
		gridBagConstraints_6.gridy = 1;
		gridBagConstraints_6.gridx = 3;
		add(mobileFractionSlider, gridBagConstraints_6);

		final JLabel bleachWhileMonitorLabel = new JLabel();
		bleachWhileMonitorLabel.setText("Bleach While Monitor Rate(1/s):");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_12.anchor = GridBagConstraints.EAST;
		gridBagConstraints_12.gridy = 2;
		gridBagConstraints_12.gridx = 0;
		add(bleachWhileMonitorLabel, gridBagConstraints_12);

		bleachWhileMonitorRateTextField = new JTextField();
		bleachWhileMonitorRateTextField.getDocument().addUndoableEditListener(EDIT_LISTENER);
		bleachWhileMonitorRateTextField.setPreferredSize(new Dimension(125, 20));
		bleachWhileMonitorRateTextField.setMinimumSize(new Dimension(125, 20));
		bleachWhileMonitorRateTextField.addActionListener(OPTIMIZER_VALUE_ACTION_LISTENER);
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridy = 2;
		gridBagConstraints_3.gridx = 1;
		add(bleachWhileMonitorRateTextField, gridBagConstraints_3);

		bleachWhileMonitorSetButton = new JButton();
		bleachWhileMonitorSetButton.addActionListener(SET_ACTION_LISTENER);
		bleachWhileMonitorSetButton.setMargin(new Insets(2, 1, 2, 1));
		bleachWhileMonitorSetButton.setText("Set");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.insets = new Insets(2, 0, 2, 2);
		gridBagConstraints_10.gridy = 2;
		gridBagConstraints_10.gridx = 2;
		add(bleachWhileMonitorSetButton, gridBagConstraints_10);

		bleachWhileMonitorSlider = new JSlider();
		bleachWhileMonitorSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		bleachWhileMonitorSlider.setPaintLabels(true);
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_7.weightx = 1;
		gridBagConstraints_7.weighty = 1.5;
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 3;
		add(bleachWhileMonitorSlider, gridBagConstraints_7);		

		final JLabel immboileFractionLabel = new JLabel();
		immboileFractionLabel.setText("Model    Immobile    Fraction:");
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.anchor = GridBagConstraints.WEST;
		gridBagConstraints_16.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_16.gridy = 3;
		gridBagConstraints_16.gridx = 0;
		add(immboileFractionLabel, gridBagConstraints_16);

		immoFracValueLabel = new JLabel();
		immoFracValueLabel.setIconTextGap(0);
		immoFracValueLabel.setText("       ");
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.anchor = GridBagConstraints.WEST;
		gridBagConstraints_19.gridy = 3;
		gridBagConstraints_19.gridx = 1;
		add(immoFracValueLabel, gridBagConstraints_19);
		
		initialize();
	}

	private void initialize(){
		
		diffusionRateSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		mobileFractionSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		bleachWhileMonitorSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		try{
			Hashtable<Integer, JComponent> diffusionSliderLabelTable = new Hashtable<Integer, JComponent>();
			diffusionRateSlider.setMinimum(0);
			diffusionRateSlider.setMaximum(2000);
			diffusionRateSlider.setValue(0);
			diffusionSliderLabelTable.put(0, new JLabel(FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound()+""));
			diffusionSliderLabelTable.put(2000,new JLabel(FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound()+""));
			diffusionRateSlider.setLabelTable(null);//Kludge for WindowBuilder otherwise not display correctly
			diffusionRateSlider.setLabelTable(diffusionSliderLabelTable);
			
			Hashtable<Integer, JComponent> secondDiffusionSliderLabelTable = new Hashtable<Integer, JComponent>();
			secondDiffusionSliderLabelTable.put(0, new JLabel(FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound()+""));
			secondDiffusionSliderLabelTable.put(100,new JLabel(FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound()+""));
			
			Hashtable<Integer, JComponent> mobileFractionSliderLabelTable = new Hashtable<Integer, JComponent>();
			mobileFractionSlider.setMinimum(0);
			mobileFractionSlider.setMaximum(100);
			mobileFractionSlider.setValue(0);
			mobileFractionSliderLabelTable.put(0, new JLabel(FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound()+""));
			mobileFractionSliderLabelTable.put(100,new JLabel(FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound()+""));
			mobileFractionSlider.setLabelTable(null);//Kludge for WindowBuilder otherwise not display correctly
			mobileFractionSlider.setLabelTable(mobileFractionSliderLabelTable);
			
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
		}finally{
			diffusionRateSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
			mobileFractionSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
			bleachWhileMonitorSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
	
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
				ProfileData[] profileData = frapOptData.getExpFrapStudy().getProfileData_oneDiffComponent();
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
			    	DialogUtils.showComponentCloseDialog(FRAPDiffOneParamPanel.this, scrollPane, "Profile Likelihood of Parameters");
				}
			}
		};
		//dispatch
		ClientTaskDispatcher.dispatch(FRAPDiffOneParamPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[]{evaluateTask, showResultTask}, false, true, null, true); 
	}
	
	public void runAndSetBestParameters()
	{
		
		AsynchClientTask optTask = new AsynchClientTask("Running optimization ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				String errorStr = checkParameters();
				if(errorStr.equals(""))
				{
					frapOptData.setNumEstimatedParams(getCurrentParameters().length);
					final Parameter[] bestParameters = frapOptData.getBestParamters(getCurrentParameters(), frapWorkspace.getWorkingFrapStudy().getSelectedROIsForErrorCalculation());
					hashTable.put("bestParameters", bestParameters);
				}
				else
				{
					throw new IllegalArgumentException(errorStr);
				}
			}
		};
		
		AsynchClientTask showResultTask = new AsynchClientTask("Running optimization ...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				final Parameter[] bestParameters = (Parameter[])hashTable.get("bestParameters");
				if(bestParameters.length == FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF)
				{
					setParameterValues(
							new Double(bestParameters[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess()),
							new Double(bestParameters[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess()),
							new Double(bestParameters[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess())
							);
					firePropertyChange(FRAPSingleWorkspace.PROPERTY_CHANGE_OPTIMIZER_VALUE, null,null);
				}
			}
		};
		//dispatch
		ClientTaskDispatcher.dispatch(FRAPDiffOneParamPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[]{optTask, showResultTask}, false, false, null, true); 
	}
	
	public DiffRateHelpPanel getDiffRateHelpPanel()
	{
		if(diffRateHelpPanel == null)
		{
			diffRateHelpPanel = new DiffRateHelpPanel();
		}
		return diffRateHelpPanel;
	}
	
	public void showEffectiveDiffusionAnalysis()
	{
		try{
			double effDiff = Double.parseDouble(diffusionRateTextField.getText());
			getDiffRateHelpPanel().setEffectiveDiffRate(effDiff);
			
			DialogUtils.showComponentCloseDialog(this, diffRateHelpPanel, "Effective Diffusion Analysis");
		}catch(NumberFormatException e)
		{
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, "Estimated diffusion rate is empty or in illegal form, effective diffusion analysis is unable to proceed." + e.getMessage());
		}
	}
	
	public void setData(FRAPOptData frapOptData, Parameter[] modelParameters) throws Exception
	{
		this.frapOptData = frapOptData;
		setParameterValues(
				new Double(modelParameters[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess()),
				new Double(modelParameters[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess()),
				new Double(modelParameters[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess()));
		
	}
	//set text fields, update slider values and plot.
	protected void setParameterValues(Double diffusionRate,Double mobileFraction,Double monitorBleachRate){
		diffusionRateTextField.setText(diffusionRate.doubleValue()+"");
		mobileFractionTextField.setText((mobileFraction != null?mobileFraction.doubleValue()+"":"1.0"));
		bleachWhileMonitorRateTextField.setText((monitorBleachRate != null?monitorBleachRate.doubleValue()+"":"0"));
		String immFrac = "";
		if(mobileFraction != null)
		{
			immFrac = (1 - mobileFraction.doubleValue())+"";
		}
		immoFracValueLabel.setText(immFrac);
		B_HOLD_FIRE = true;
		//one click is enough, The set action listener goes through the whole textfields and sliders
		diffusionRateSetButton.doClick();

		B_HOLD_FIRE = false;
		//set all text fields positions to 0
		setAllTextFieldsPosition(0);
	}
	
	private void setAllTextFieldsPosition(int pos)
	{
		diffusionRateTextField.setCaretPosition(pos);
		mobileFractionTextField.setCaretPosition(pos);
		bleachWhileMonitorRateTextField.setCaretPosition(pos);
	}
	
	private static Parameter[] createParameterArray(double diffusionRate, double mobileFraction, double monitorBleachRate)
	{
		Parameter[] params = null;
		Parameter diff = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE],
										FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound(), 
										FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
										FRAPModel.REF_DIFFUSION_RATE_PARAM.getScale(),diffusionRate);
		Parameter mobileFrac = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION],
										FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
										FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
										FRAPModel.REF_MOBILE_FRACTION_PARAM.getScale(),mobileFraction);
		Parameter bleachWhileMonitoringRate = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE],
										FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
										FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
										FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),monitorBleachRate);
		
		params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF];
		params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = diff;
		params[FRAPModel.INDEX_PRIMARY_FRACTION] = mobileFrac;
		params[FRAPModel.INDEX_BLEACH_MONITOR_RATE]= bleachWhileMonitoringRate;
		
		return params;
	}
	
	public Parameter[] getCurrentParameters(){
		if(diffusionRateTextField == null || diffusionRateTextField.getText().equals("")||
		   mobileFractionTextField == null || mobileFractionTextField.getText().equals("")||
		   bleachWhileMonitorRateTextField == null || bleachWhileMonitorRateTextField.equals(""))
		{
			return null;
		}
		double diffusionRate, mobileFraction, bleachWhileMonitorRate;
		try
		{
			diffusionRate = Double.parseDouble(diffusionRateTextField.getText());
			mobileFraction = Double.parseDouble(mobileFractionTextField.getText());
			bleachWhileMonitorRate = Double.parseDouble(bleachWhileMonitorRateTextField.getText());
		}catch(NumberFormatException e)
		{
			return null;
		}
		
		return createParameterArray(diffusionRate, mobileFraction, bleachWhileMonitorRate);
	}
	private double[][] getFitData(Parameter[] userParams) throws Exception{
		if(userParams == null || userParams.length <= 0)
		{
			return null;
		}
		frapOptData.setNumEstimatedParams(userParams.length);
		double[][] fitData = frapOptData.getFitData(userParams); 

		return fitData;
	}
	

	
	
	public double[][] getCurrentFitData() throws Exception{
		return getFitData(getCurrentParameters());
	}
	
	public String getDiffusionRateString() {
		return diffusionRateTextField.getText();
	}

	public String getMobileFractionString() {
		return mobileFractionTextField.getText();
	}

	public String getImmoFracString() {
		return immoFracValueLabel.getText();
	}
	
	public String getBleachWhileMonitorRateString() {
		return bleachWhileMonitorRateTextField.getText();
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			FRAPDiffOneParamPanel aPanel = new FRAPDiffOneParamPanel();
			frame.setContentPane(aPanel);
			frame.pack();
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
	
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
	{
		this.frapWorkspace = frapWorkspace;
	}
	
}
