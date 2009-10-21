package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.BorderLayout;
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
import java.util.Arrays;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import org.vcell.util.Range;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.SpatialAnalysisResults;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.ROIImagePanel;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;

public class EstParams_TwoDiffComponentPanel extends JPanel {
	
	private static final int IDX_REACTION_DIFFUSION = 1;
	private static final int IDX_PURE_DIFFUSION = 0;	
	
	private SpatialAnalysisResults spatialAnalysisResults; //will be initialized in setData
	private final JPanel paramPanel; //exclusively display pure diffusion panel and reaction diffusion panel
	private JLabel simulationParametersLabel;
	private final JLabel interactiveAnalysisUsingLabel_1;
	

	private ROIImagePanel roiImagePanel;
	private FRAPDiffTwoParamPanel pureDiffusionPanel;
		
	private FRAPOptData frapOptData;
	private FRAPWorkspace frapWorkspace;
	
	private MultisourcePlotPane multisourcePlotPane;
	private Hashtable<FRAPStudy.AnalysisParameters, DataSource[]> allDataHash;
	private double[][] currentEstimationResults = null; //a data structure used to store results according to the current params. 
	
	private boolean do_once = true;
	
	private static String[] summaryReportColumnNames = SpatialAnalysisResults.getSummaryReportColumnNames();
	
	
	
	public EstParams_TwoDiffComponentPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {7,7,7,0,7};
		gridBagLayout.columnWidths = new int[] {7};
		setLayout(gridBagLayout);
			
		//set up tabbed pane for two kinds of models.
		paramPanel=new JPanel(new GridBagLayout());
		paramPanel.setForeground(new Color(0,0,244));
		paramPanel.setBorder(new EtchedBorder(Color.gray, Color.lightGray));
		paramPanel.setLayout(new GridBagLayout());
		
		//pure diffusion panel
		interactiveAnalysisUsingLabel_1 = new JLabel();
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.gridy = 0;
		gridBagConstraints_8.gridx = 0;
		paramPanel.add(interactiveAnalysisUsingLabel_1, gridBagConstraints_8);
		interactiveAnalysisUsingLabel_1.setFont(new Font("", Font.PLAIN, 14));
		interactiveAnalysisUsingLabel_1.setText("Interactive Analysis on 'Diffusion with Two Diffusing Components' Model using FRAP Simulation Results");

		pureDiffusionPanel = new FRAPDiffTwoParamPanel();
//		pureDiffusionPanel.setSecondDiffComponentEnabled(true);
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.fill = GridBagConstraints.BOTH;
		gridBagConstraints_10.gridy = 1;
		gridBagConstraints_10.gridx = 0;
		gridBagConstraints_10.weightx = 1.5;
		paramPanel.add(pureDiffusionPanel, gridBagConstraints_10);
		pureDiffusionPanel.addPropertyChangeListener(
				new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent evt) {
						if(evt.getSource() == pureDiffusionPanel){
							if((evt.getPropertyName().equals(FRAPDiffTwoParamPanel.PROPERTY_CHANGE_OPTIMIZER_VALUE)))
							{
								plotDerivedSimulationResults(spatialAnalysisResults.getAnalysisParameters());
							}
//							}
						}
					}
				}
		);

		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.weightx = 1;
		gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_9.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_9.gridy = 0;
		gridBagConstraints_9.gridx = 0;
		add(paramPanel, gridBagConstraints_9);
		
		
		final JPanel panel_3 = new JPanel();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[] {0,7};
		panel_3.setLayout(gridBagLayout_1);
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.weightx = 1;
		gridBagConstraints_11.gridy = 1;
		gridBagConstraints_11.gridx = 0;
		add(panel_3, gridBagConstraints_11);

		final JLabel standardErrorRoiLabel = new JLabel();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.insets = new Insets(2, 2, 2, 2);
		panel_3.add(standardErrorRoiLabel, gridBagConstraints_4);
		standardErrorRoiLabel.setFont(new Font("", Font.BOLD, 12));
		standardErrorRoiLabel.setText("Plot -  ROI Average Normalized (using Pre-Bleach Average) vs. Time");

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
		
//		init();
	}

	private void plotDerivedSimulationResults(FRAPStudy.AnalysisParameters[] anaParams)
	{
//		boolean isSimData = false;
		try{
			String description = null;
			int totalROIlen = FRAPData.VFRAP_ROI_ENUM.values().length;
			boolean[] wantsROITypes = new boolean[totalROIlen];
			System.arraycopy(frapWorkspace.getFrapStudy().getSelectedROIsForErrorCalculation(), 0, wantsROITypes, 0, totalROIlen);
			
			ODESolverResultSet fitOdeSolverResultSet = new ODESolverResultSet();
			fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
			for (int j = 0; j < totalROIlen; j++) {
				if(!wantsROITypes[j]){continue;}
				String currentROIName = FRAPData.VFRAP_ROI_ENUM.values()[j].name();
				String name = (description == null?/*"sim D="+diffusionRates[diffusionRateIndex]+"::"*/"":description)+currentROIName;
				fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(name));
			}
			
			int totalWantedROIlen = 0;
			for(int i=0; i<wantsROITypes.length; i++)
			{
				if(wantsROITypes[i])
				{
					totalWantedROIlen ++;
				}
			}
			//
			// populate time
			//
			double[] shiftedSimTimes = frapOptData.getReducedExpTimePoints();
			int startIndexRecovery = frapOptData.getExpFrapStudy().getStartingIndexForRecovery();
			for (int j = 0; j < shiftedSimTimes.length; j++) {
				double[] row = new double[totalWantedROIlen+1];
				row[0] = shiftedSimTimes[j]+
					frapOptData.getExpFrapStudy().getFrapData().getImageDataset().getImageTimeStamps()[startIndexRecovery];
				fitOdeSolverResultSet.addRow(row);
			}
			// populate values
			double[][] currentOptFitData = pureDiffusionPanel.getCurrentFitData();
			//store results
			setCurrentEstimationResults(currentOptFitData);
			if(allDataHash != null && currentOptFitData != null)
			{
				//populate optimization data---convert it to odeSoverResultSet and further put into datasource 
				int columncounter = 0;
				for (int j = 0; j < totalROIlen; j++) {
					if(!wantsROITypes[j]){continue;}
//					if(!isSimData) //opt data
//					{
						double[] values = currentOptFitData[j];
						for (int k = 0; k < values.length; k++) {
							fitOdeSolverResultSet.setValue(k, columncounter/*j*/+1, values[k]);
						}
//					}
					columncounter++;
				}
				boolean hasSimData = false;
				
				
				DataSource[] selectedRowDataSourceArr = allDataHash.get(anaParams[0]);//anaParams[0] is the key in allDataHash to get the dataSource[]:exp & sim
				if(selectedRowDataSourceArr != null)
				{   //referenceData is the exp data
					final DataSource expDataSource = /*new DataSource(referenceData,"exp")*/selectedRowDataSourceArr[SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE];
					DataSource optDataSource  = new DataSource.DataSourceOdeSolverResultSet( "opt", fitOdeSolverResultSet);
//					
					DataSource[] newDataSourceArr = new DataSource[2];
					newDataSourceArr[SpatialAnalysisResults.ARRAY_INDEX_EXPDATASOURCE] = expDataSource;
					newDataSourceArr[SpatialAnalysisResults.ARRAY_INDEX_SIMDATASOURCE] = optDataSource;
					if( currentOptFitData == null)
					{
						multisourcePlotPane.setDataSources(null);
					}
					else
					{
						multisourcePlotPane.setDataSources(newDataSourceArr);
						multisourcePlotPane.selectAll();
					}
				}
			}
		}catch(Exception e2){
			e2.printStackTrace();
			DialogUtils.showErrorDialog(this,"Error graphing Optimizer data "+e2.getMessage());
		}

	}

	public void setData(final FRAPOptData frapOptData, final FRAPData fData, Parameter[] modelParams,final double[] frapDataTimeStamps,int startIndexForRecovery, boolean[] selectedROIs) throws Exception
	{
		this.frapOptData = frapOptData;
		double[] prebleachAverage = FRAPStudy.calculatePreBleachAverageXYZ(fData, startIndexForRecovery);
		spatialAnalysisResults = FRAPStudy.spatialAnalysis(null, startIndexForRecovery, frapDataTimeStamps[startIndexForRecovery], modelParams, fData, prebleachAverage);
		//allDataHash use AnalysisParameters as key, the value is dataSource[] which should have length as 2: expDataSource & simDataSouce
		allDataHash = spatialAnalysisResults.createSummaryReportSourceData(frapDataTimeStamps, startIndexForRecovery, selectedROIs, false);
		final SpatialAnalysisResults finalSpatialAnalysisResults = spatialAnalysisResults;
		try{
			
			getPureDiffusionPanel().setData(frapOptData, modelParams);
			multisourcePlotPane.forceXYRange(new Range(frapDataTimeStamps[0],frapDataTimeStamps[frapDataTimeStamps.length-1]), new Range(0,1.5));
	
			plotDerivedSimulationResults(finalSpatialAnalysisResults.getAnalysisParameters());
			
		}catch(Exception e){
			throw new RuntimeException("Error setting data to result panel for diffusion with one diffusing component.");
		}
	}
	
//	private void setTableCellSelection(int selectedRow, int[] selectedColumns){
//		table.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
//		table.getColumnModel().getSelectionModel().setSelectionInterval(selectedColumns[0], selectedColumns[0]);
//		for (int i = 1; i < selectedColumns.length; i++) {
//			table.getColumnModel().getSelectionModel().addSelectionInterval(selectedColumns[i], selectedColumns[i]);
//		}
//	}
	
	public void clearData(){
		if(allDataHash != null)
		{
			allDataHash.clear();
		}
		allDataHash = null;
	}
	public boolean hasData(){
		return allDataHash != null;
	}
	
	public Parameter[] getCurrentParameters()
	{
		return getPureDiffusionPanel().getCurrentParameters();
	}
	
	public void setFrapWorkspace(FRAPWorkspace frapWorkspace)
	{
		this.frapWorkspace = frapWorkspace;
		getPureDiffusionPanel().setFrapWorkspace(frapWorkspace);
	}
	
	public FRAPDiffTwoParamPanel getPureDiffusionPanel() {
		return pureDiffusionPanel;
	}
	
	public double[][] getCurrentEstimationResults() {
		return currentEstimationResults;
	}

	public void setCurrentEstimationResults(double[][] currentEstimationResults) {
		this.currentEstimationResults = currentEstimationResults;
	}
	
	public void insertPureDiffusionParametersIntoFRAPStudy(FRAPStudy arg_FRAPStudy) throws Exception
	{
		getPureDiffusionPanel().insertPureDiffusionParametersIntoFRAPStudy(arg_FRAPStudy);
	}
	
	public static void main(java.lang.String[] args) {
		try {
			try{
		    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    }catch(Exception e){
		    	throw new RuntimeException(e.getMessage(),e);
		    }
			javax.swing.JFrame frame = new javax.swing.JFrame();
			EstParams_TwoDiffComponentPanel aParameterPanel;
			aParameterPanel = new EstParams_TwoDiffComponentPanel();
			frame.add(aParameterPanel);
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
	
	
}
