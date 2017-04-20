/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.plot.gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.vcell.util.gui.DialogUtils;

import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SimpleSymbolTable;

public class TimeFunctionPanel extends JPanel {
	private PlotPane plotPane;
	private JTextField funcTextField;
	private JTextField timeBegTextField;
	private JTextField timeStepTextField;
	private JTextField endTimeTextField;
	
	public TimeFunctionPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		
		plotPane = new PlotPane();
		plotPane.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		GridBagConstraints gbc_plotPane = new GridBagConstraints();
		gbc_plotPane.weighty = 1.0;
		gbc_plotPane.weightx = 1.0;
		gbc_plotPane.insets = new Insets(4, 4, 5, 4);
		gbc_plotPane.fill = GridBagConstraints.BOTH;
		gbc_plotPane.gridx = 0;
		gbc_plotPane.gridy = 0;
		add(plotPane, gbc_plotPane);
		
		JPanel funcPanel = new JPanel();
		GridBagConstraints gbc_funcPanel = new GridBagConstraints();
		gbc_funcPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_funcPanel.insets = new Insets(4, 4, 4, 4);
		gbc_funcPanel.gridx = 0;
		gbc_funcPanel.gridy = 1;
		add(funcPanel, gbc_funcPanel);
		GridBagLayout gbl_funcPanel = new GridBagLayout();
		gbl_funcPanel.columnWidths = new int[]{0, 0, 0};
		gbl_funcPanel.rowHeights = new int[]{0, 0};
		gbl_funcPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_funcPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		funcPanel.setLayout(gbl_funcPanel);
		
		JLabel lblTimeFunction = new JLabel("Time Function");
		GridBagConstraints gbc_lblTimeFunction = new GridBagConstraints();
		gbc_lblTimeFunction.insets = new Insets(0, 0, 0, 4);
		gbc_lblTimeFunction.anchor = GridBagConstraints.EAST;
		gbc_lblTimeFunction.gridx = 0;
		gbc_lblTimeFunction.gridy = 0;
		funcPanel.add(lblTimeFunction, gbc_lblTimeFunction);
		
		funcTextField = new JTextField();
		GridBagConstraints gbc_funcTextField = new GridBagConstraints();
		gbc_funcTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_funcTextField.gridx = 1;
		gbc_funcTextField.gridy = 0;
		funcPanel.add(funcTextField, gbc_funcTextField);
		funcTextField.setColumns(10);
		
		JPanel timePanel = new JPanel();
		GridBagConstraints gbc_timePanel = new GridBagConstraints();
		gbc_timePanel.insets = new Insets(4, 4, 5, 4);
		gbc_timePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_timePanel.gridx = 0;
		gbc_timePanel.gridy = 2;
		add(timePanel, gbc_timePanel);
		timePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblTimeBegin = new JLabel("Time Begin");
		timePanel.add(lblTimeBegin);
		
		timeBegTextField = new JTextField();
		timePanel.add(timeBegTextField);
		timeBegTextField.setColumns(10);
		
		JLabel lblStep = new JLabel("Time Step");
		timePanel.add(lblStep);
		
		timeStepTextField = new JTextField();
		timePanel.add(timeStepTextField);
		timeStepTextField.setColumns(10);
		
		JLabel lblSteps = new JLabel("End Time");
		timePanel.add(lblSteps);
		
		endTimeTextField = new JTextField();
		timePanel.add(endTimeTextField);
		endTimeTextField.setColumns(10);
		
		JButton btnPlot = new JButton("Refresh Plot");
		btnPlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					refreshPlot();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					DialogUtils.showErrorDialog(TimeFunctionPanel.this, "Error refreshing Plot:\n"+e1.getMessage(), e1);
					plotPane.setPlot2D(	null);
				}
			}
		});
		timePanel.add(btnPlot);
		
		init();
	}
	
	private void init(){
		funcTextField.setText("t");
		timeBegTextField.setText("0");
		timeStepTextField.setText(".1");
		endTimeTextField.setText("10.0");
		try {
			refreshPlot();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DialogUtils.showErrorDialog(this, "Error refreshing Plot:\n"+e.getMessage(), e);
			plotPane.setPlot2D(	null);
		}
	}
	
	private void refreshPlot() throws Exception{
//		try {
			Expression exp = new Expression(funcTextField.getText());
			exp.flatten();
			SimpleSymbolTable symbolTable = new SimpleSymbolTable(new String[] {"t", "pi"/*, "x", "y", "z"*/});
			exp.bindExpression(symbolTable);
			
			double valueArray[] = new double[2/*4*/];
			valueArray[1] = Math.PI;
//			valueArray[ReservedVariable.Z.getIndex()] = 0 ;
//			valueArray[ReservedVariable.Y.getIndex()] = 0 ;
//			valueArray[ReservedVariable.X.getIndex()] = 0 ;
			
			int totalTimePoints = 0;
			double beginTime = 0;
			double timeStep = 0;
			double parseEndTime = 0;
			try{
				beginTime = Double.parseDouble(timeBegTextField.getText());
			}catch (Exception e){
				throw new Exception("Couldn't evaluate 'Time Begin' as number\n"+e.getMessage());
			}
			try{
				timeStep = Double.parseDouble(timeStepTextField.getText());
				if(timeStep <= 0){
					throw new Exception("timestep value = "+timeStep);
				}
			}catch (Exception e){
				throw new Exception("'Time Step' must be > 0\n"+e.getMessage());
			}
			try{
				parseEndTime = Double.parseDouble(endTimeTextField.getText());
			}catch (Exception e){
				throw new Exception("Couldn't evaluate 'Time End' as number\n"+e.getMessage());
			}
			try{
				if(beginTime > parseEndTime || beginTime == parseEndTime){
					throw new Exception("endTime must be greater than beginTime");
				}
				totalTimePoints = (int)Math.ceil((parseEndTime-beginTime)/timeStep);
//				if(totalTimePoints <= 0){
//					throw new Exception("calculated number of time points ="+totalTimePoints);
//				}
			}catch(Exception e){
				throw new Exception("check beginTime, endTime and timeStep: ((endTime-beginTime)/timeStep) must be greater than 0"+"\n"+e.getMessage());
			}

			double[] timePoints = new double[totalTimePoints];
			double[] funcVals = new double[totalTimePoints];

			for (int i = 0; i < totalTimePoints; i++) {
				valueArray[ReservedVariable.TIME.getIndex()] = beginTime+i*timeStep;
				timePoints[i] = valueArray[ReservedVariable.TIME.getIndex()];
				funcVals[i] = exp.evaluateVector(valueArray);
			}

			PlotData plotData = new PlotData(timePoints, funcVals);
			Plot2D plot2D = new Plot2D(null, null, new String[] { "Time Function" },new PlotData[] { plotData },
					new String[] {"Time Function Value", "Time", "[" + "Time Function" + "]"});
			plotPane.setPlot2D(	plot2D);
//		} catch (Exception e) {
//			e.printStackTrace();
//			DialogUtils.showErrorDialog(this, "Error refreshing Plot:\n"+e.getMessage(), e);
//			plotPane.setPlot2D(	null);
//		}

	}
	public void setTimeFunction(String timeFunction) throws Exception{
		funcTextField.setText(timeFunction);
		refreshPlot();
	}
}
