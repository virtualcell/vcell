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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPDataAnalysis;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.gui.defineROIwizard.DefineROI_RoiForErrorPanel;
import cbit.vcell.microscopy.server.FrapDataUtils;
import cbit.vcell.modelopt.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESolverResultSet;

@SuppressWarnings("serial")
public class EstParams_ReactionOffRatePanel extends JPanel 
{
	private JPanel paramPanel; //exclusively display pure diffusion panel and reaction diffusion panel

	private FRAPReacOffRateParametersPanel offRateParamPanel;
	private FRAPSingleWorkspace frapWorkspace;
	
	private MultisourcePlotPane multisourcePlotPane;
	private DefineROI_RoiForErrorPanel roiPanel;
	private double[][] currentEstimationResults = null; //a data structure used to store results according to the current params. 
	
	public EstParams_ReactionOffRatePanel() {
		super();
		setLayout(new GridBagLayout());
		
		new JPanel(new FlowLayout());
		//set up parameters panel.
		paramPanel=new JPanel(new GridBagLayout());
		paramPanel.setForeground(new Color(0,0,244));
		paramPanel.setBorder(new EtchedBorder(Color.gray, Color.lightGray));
		
		JLabel interactiveAnalysisLabel = new JLabel();
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.gridy = 0;
		gridBagConstraints_8.gridx = 0;
		paramPanel.add(interactiveAnalysisLabel, gridBagConstraints_8);
		interactiveAnalysisLabel.setFont(new Font("", Font.PLAIN, 14));
		interactiveAnalysisLabel.setText("Interactive Analysis on 'Reaction Dominant Off Rate' Model using Analytic Equations");

		offRateParamPanel = new FRAPReacOffRateParametersPanel();
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints_10.fill = GridBagConstraints.BOTH;
		gridBagConstraints_10.gridy = 1;
		gridBagConstraints_10.gridx = 0;
		gridBagConstraints_10.weightx = 1.5;
		gridBagConstraints_10.weighty = 2;
		paramPanel.add(offRateParamPanel, gridBagConstraints_10);
		offRateParamPanel.addPropertyChangeListener(
				new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent evt) {
						if(evt.getSource() == offRateParamPanel){
							if((evt.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_OPTIMIZER_VALUE)))
							{
								if(frapWorkspace != null && frapWorkspace.getWorkingFrapStudy() != null && 
								   frapWorkspace.getWorkingFrapStudy().getFrapOptFunc() != null)
								{
									FRAPStudy fStudy = frapWorkspace.getWorkingFrapStudy();
									//updating values here.
//									Parameter[] params = (Parameter[])evt.getNewValue();
									try {
										displayResults(fStudy.getFrapData(), fStudy.getStartingIndexForRecovery());
									} catch (ExpressionException e) {
										e.printStackTrace(System.out);
										DialogUtils.showErrorDialog(EstParams_ReactionOffRatePanel.this, e.getMessage());
									}
								}
							}
						}
					}
				}
		);

		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_9.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_9.gridy = 0;
		gridBagConstraints_9.gridx = 0;
		add(paramPanel, gridBagConstraints_9);
		
		
		final JPanel panel_3 = new JPanel();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[] {0, 0, 0, 0};
		panel_3.setLayout(gridBagLayout_1);
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.gridy = 1;
		gridBagConstraints_11.gridx = 0;
		add(panel_3, gridBagConstraints_11);

		final JLabel standardErrorRoiLabel = new JLabel();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.insets = new Insets(2, 2, 2, 5);
		panel_3.add(standardErrorRoiLabel, gridBagConstraints_4);
		standardErrorRoiLabel.setFont(new Font("", Font.BOLD, 12));
		standardErrorRoiLabel.setText("Plot -  ROI Average Normalized (using Pre-Bleach Average) vs. Time          ");

		final JButton showRoisButton = new JButton();
		showRoisButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if(frapWorkspace != null && frapWorkspace.getWorkingFrapStudy() != null &&
				   frapWorkspace.getWorkingFrapStudy().getSelectedROIsForErrorCalculation() != null)
				{
					getROIPanel().setFrapWorkspace(frapWorkspace);
					getROIPanel().setCheckboxesForDisplay(FRAPStudy.createSelectedROIsForReactionOffRateModel());
					getROIPanel().refreshROIImageForDisplay();
				}
				JOptionPane.showMessageDialog(EstParams_ReactionOffRatePanel.this, getROIPanel());
			}
		});
		showRoisButton.setFont(new Font("", Font.PLAIN, 11));
		showRoisButton.setMargin(new Insets(0, 8, 0, 8));
		showRoisButton.setText("Show ROIs");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 0, 5);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 1;
		panel_3.add(showRoisButton, gridBagConstraints);

		final JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.black, 1, false));
		panel.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridwidth = 0;
		gridBagConstraints_1.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.weighty = 1;
		gridBagConstraints_1.weightx = 1;
		gridBagConstraints_1.gridy = 2;
		gridBagConstraints_1.gridx = 0;
		add(panel, gridBagConstraints_1);

		multisourcePlotPane = new MultisourcePlotPane();
		
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 0;
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.weighty = 1;
		gridBagConstraints_2.weightx = 1;
		panel.add(multisourcePlotPane, gridBagConstraints_2);
	}

	private DefineROI_RoiForErrorPanel getROIPanel()
	{
		if(roiPanel == null)
		{
			roiPanel = new DefineROI_RoiForErrorPanel();
		}
		return roiPanel;
	}
	
	public void clearSelectedPlotIndices()
	{
		multisourcePlotPane.clearSelection();
	}
	
	public void setData(Parameter[] bestParams, FRAPData fData, int startIndexRecovery) throws Exception
	{
		offRateParamPanel.setData(bestParams);
		displayResults(fData, startIndexRecovery);
	}
	
	private void displayResults(FRAPData frapData, int startIndexRecovery) throws ExpressionException, DivideByZeroException
	{
		Parameter[] currentParams = offRateParamPanel.getCurrentParameters();
		if (frapData == null || currentParams == null)
		{
			multisourcePlotPane.setDataSources(null);
		}
		else
		{
			double[] frapDataTimeStamps = frapData.getImageDataset().getImageTimeStamps();
			//Experiment - Cell ROI Average
			double[] temp_background = frapData.getAvgBackGroundIntensity();
			double[] preBleachAvgXYZ = FrapDataUtils.calculatePreBleachAverageXYZ(frapData, startIndexRecovery);
			/*double[] cellRegionData = FRAPDataAnalysis.getAverageROIIntensity(frapData, frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()),preBleachAvgXYZ,temp_background);
			ReferenceData expCellAvgData = new SimpleReferenceData(new String[] { ReservedSymbol.TIME.getName(), "CellROIAvg" }, new double[] { 1.0, 1.0 }, new double[][] {frapDataTimeStamps, cellRegionData });
			DataSource expCellAvgDataSource = new DataSource.DataSourceReferenceData("exp", expCellAvgData);
			//Analytic - Cell ROI Average with Bleach while monitor
			ODESolverResultSet bleachWhileMonitorOdeSolverResultSet = new ODESolverResultSet();
			bleachWhileMonitorOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(ReservedSymbol.TIME.getName()));
			Expression cellAvgExp = new Expression(FRAPOptFunctions.FUNC_CELL_INTENSITY);
			// substitute parameter values 
			cellAvgExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_I_inicell), new Expression(cellRegionData[startIndexRecovery]));
			cellAvgExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_BWM_RATE), new Expression(currentParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess()));
			// time shift
			cellAvgExp.substituteInPlace(new Expression(ReservedSymbol.TIME.getName()), new Expression(ReservedSymbol.TIME.getName()+"-"+frapDataTimeStamps[startIndexRecovery]));
			try {
				bleachWhileMonitorOdeSolverResultSet.addFunctionColumn(
					new FunctionColumnDescription(
						cellAvgExp,
						"CellROIAvg",
						null,"bleachWhileMonitorFit",true));
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
			for (int i = startIndexRecovery; i < frapDataTimeStamps.length; i++) 
			{
				bleachWhileMonitorOdeSolverResultSet.addRow(new double[] { frapDataTimeStamps[i] });
			}
			DataSource bleachWhileMonitorDataSource = new DataSource.DataSourceOdeSolverResultSet("fit", bleachWhileMonitorOdeSolverResultSet);*/

			//experimental bleach region average intensity curve
			double[] bleachRegionData = FRAPDataAnalysis.getAverageROIIntensity(frapData, frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()),preBleachAvgXYZ,temp_background);;
			ReferenceData expRefData = new SimpleReferenceData(new String[] { ReservedVariable.TIME.getName(), "BleachROIAvg" }, new double[] { 1.0, 1.0 }, new double[][] { frapDataTimeStamps, bleachRegionData });
			DataSource expBleachDataSource = new DataSource.DataSourceReferenceData("exp", expRefData);
			//Analytic - bleach region average intensity with bleach while monitoring rate
			ODESolverResultSet koffFitOdeSolverResultSet = new ODESolverResultSet();
			koffFitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(ReservedVariable.TIME.getName()));
			Expression bleachedAvgExp = frapWorkspace.getWorkingFrapStudy().getFrapOptFunc().getRecoveryExpressionWithCurrentParameters(currentParams);
			try {
				koffFitOdeSolverResultSet.addFunctionColumn(
					new FunctionColumnDescription(
						bleachedAvgExp,
						"BleachROIAvg",
						null,"recoveryFit",true));
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
						double[] truncatedTimes = new double[frapDataTimeStamps.length - startIndexRecovery];
			for (int i = startIndexRecovery; i < frapDataTimeStamps.length; i++) 
			{
				koffFitOdeSolverResultSet.addRow(new double[] { frapDataTimeStamps[i] });
				truncatedTimes[i-startIndexRecovery] = frapDataTimeStamps[i];
			}
			setCurrentEstimationResults(frapWorkspace.getWorkingFrapStudy().getFrapOptFunc().createData(bleachedAvgExp.flatten(), truncatedTimes));
			DataSource koffFitDataSource = new DataSource.DataSourceRowColumnResultSet("fit", koffFitOdeSolverResultSet);
			multisourcePlotPane.setDataSources(new DataSource[] {expBleachDataSource, koffFitDataSource /*, expCellAvgDataSource , bleachWhileMonitorDataSource*/} );
			multisourcePlotPane.selectAll();		
		}
	}
	
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
	{
		this.frapWorkspace = frapWorkspace;
		getReacOffRatePanel().setFrapWorkspace(frapWorkspace);
	}
	
	private FRAPReacOffRateParametersPanel getReacOffRatePanel() {
		return offRateParamPanel;
	}
	
	public Parameter[] getCurrentParameters()
	{
		return getReacOffRatePanel().getCurrentParameters();
	}
	
	public double[][] getCurrentEstimationResults() {
		return currentEstimationResults;
	}

	public void setCurrentEstimationResults(double[][] currentEstimationResults) {
		this.currentEstimationResults = currentEstimationResults;
	}

	public static void main(java.lang.String[] args) {
		try {
			JFrame frame = new JFrame();
			EstParams_ReactionOffRatePanel aPanel = new EstParams_ReactionOffRatePanel();
			frame.setContentPane(aPanel);
			frame.setSize(1000,700);
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
