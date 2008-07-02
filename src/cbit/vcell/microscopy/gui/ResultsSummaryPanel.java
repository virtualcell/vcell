package cbit.vcell.microscopy.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JLabel;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import cbit.gui.DialogUtils;
import cbit.util.NumberUtils;
import cbit.util.Range;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPStudy.CurveInfo;
import cbit.vcell.microscopy.FRAPStudy.SpatialAnalysisResults;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;

public class ResultsSummaryPanel extends JPanel {
	private JTable table;
	private MultisourcePlotPane multisourcePlotPane;
	private Hashtable<Double, DataSource[]> allDataHash;
	private Object[][] summaryData;
	
	private static final int COLUMN_INDEX_DIFFUSION_RATE = 0;
	private static final int COLUMN_INDEX_BLEACHROI = 1;
	
	private static final int ARRAY_INDEX_EXPDATASOURCE = 0;
	private static final int ARRAY_INDEX_SIMDATASOURCE = 1;

	
	public ResultsSummaryPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7};
		gridBagLayout.columnWidths = new int[] {0,7};
		setLayout(gridBagLayout);

		final JLabel diffusionRateAndLabel = new JLabel();
		diffusionRateAndLabel.setFont(new Font("", Font.BOLD, 14));
		diffusionRateAndLabel.setText("Standard Error (including all Times) of Normalized ROI Average  (Experimental vs. Simulation Data)");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 0;
		add(diffusionRateAndLabel, gridBagConstraints_3);

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(0, 250));
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 0;
		add(scrollPane, gridBagConstraints);

		table = new JTable();
		table.setCellSelectionEnabled(true);
		table.getTableHeader().addMouseListener(
				new MouseAdapter(){
					public void mouseClicked(MouseEvent e) {
						System.out.println(e);
						final int columnIndex = table.getTableHeader().columnAtPoint(e.getPoint());
						sortColumn(columnIndex,true);
					}
				}
		);
		table.getTableHeader().setReorderingAllowed(false);
		table.getColumnModel().getSelectionModel().addListSelectionListener(
				new ListSelectionListener(){
					public void valueChanged(ListSelectionEvent e) {
						processTableSelection();
					}
				}
		);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener(){
					public void valueChanged(ListSelectionEvent e) {
						if(!e.getValueIsAdjusting()){
							processTableSelection();
						}
					}
				}
		);
		scrollPane.setViewportView(table);
		
		table.setModel(getTableModel(getColumnNames(),new Object[][] {{"diffTest","summaryTest"}}));

		final JLabel standardErrorRoiLabel = new JLabel();
		standardErrorRoiLabel.setFont(new Font("", Font.BOLD, 14));
		standardErrorRoiLabel.setText("Plot -  ROI Average Normalized (by first non-zero ROI Avg. in time)  vs. Time");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_4.gridy = 2;
		gridBagConstraints_4.gridx = 0;
		add(standardErrorRoiLabel, gridBagConstraints_4);

		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.weighty = 1;
		gridBagConstraints_1.weightx = 1;
		gridBagConstraints_1.gridy = 3;
		gridBagConstraints_1.gridx = 0;
		add(panel, gridBagConstraints_1);

		multisourcePlotPane = new MultisourcePlotPane();
		multisourcePlotPane.setRefDataLabelPrefix("exp:");
		multisourcePlotPane.setModelDataLabelPrefix("sim:");
		
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 0;
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.weighty = 1;
		gridBagConstraints_2.weightx = 1;
		panel.add(multisourcePlotPane, gridBagConstraints_2);
	}

	private void sortColumn(final int columnIndex,boolean bAutoReverse){
		if(summaryData != null){
			int selectedRow = table.getSelectedRow();
			int selectedColumn = table.getSelectedColumn();
			Object[][] sortedObjects = new Object[summaryData.length][];
			for (int i = 0; i < sortedObjects.length; i++) {
				sortedObjects[i] = new Object[] {new Integer(i),(Double)summaryData[i][columnIndex]};
			}
			Arrays.sort(sortedObjects,
				new Comparator<Object[]> (){
					public int compare(Object[] o1,Object[] o2) {
						return (int)Math.signum((Double)o1[1] - (Double)o2[1]);
					}
				}
			);
			if(bAutoReverse){
				boolean bSortOrderChanged = false;
				for (int i = 0; i < sortedObjects.length; i++) {
					if((Integer)(((Object[])sortedObjects[i])[0]) != i){
						bSortOrderChanged = true;
						break;
					}
				}
				if(!bSortOrderChanged){
					Object[][] reverseSort = new Object[sortedObjects.length][];
					for (int i = 0; i < reverseSort.length; i++) {
						reverseSort[i] = sortedObjects[sortedObjects.length-1-i];
					}
					sortedObjects = reverseSort;
				}
			}
			Object[][] summaryCopy = new Object[summaryData.length][];
			for (int i = 0; i < sortedObjects.length; i++) {
				summaryCopy[i] = summaryData[(Integer)(((Object[])sortedObjects[i])[0])];
			}
			table.setModel(getTableModel(getColumnNames(),summaryCopy));
			if(selectedRow != -1){
				for (int i = 0; i < sortedObjects.length; i++) {
					if((Integer)(((Object[])sortedObjects[i])[0]) == selectedRow){
						selectedRow = i;//table.getSelectionModel().setSelectionInterval(i, i);	
						break;
					}
				}

			}
			setTableCellSelection(selectedRow, selectedColumn);
		}
	}
	private void processTableSelection(){
		if(allDataHash == null){
			return;
		}
		int selectedRow = table.getSelectedRow();
		int selectedColumn = table.getSelectedColumn();
		if(selectedRow != -1 && selectedColumn != -1){
			Double selectedDiffusionRate = (Double)table.getValueAt(selectedRow, COLUMN_INDEX_DIFFUSION_RATE);
			DataSource[] selectedRowDataSourceArr = allDataHash.get(selectedDiffusionRate);
			multisourcePlotPane.setDataSources(selectedRowDataSourceArr);
			multisourcePlotPane.clearSelection();
			if(selectedColumn == COLUMN_INDEX_DIFFUSION_RATE){
				multisourcePlotPane.selectAll();
			}else{
				String expColName =
					((ReferenceData)selectedRowDataSourceArr[ARRAY_INDEX_EXPDATASOURCE].getSource()).getColumnNames()[selectedColumn];
				String simColName =
					((ODESolverResultSet)selectedRowDataSourceArr[ARRAY_INDEX_SIMDATASOURCE].getSource()).getColumnDescriptions()[selectedColumn].getName();
				multisourcePlotPane.select(new String[] {expColName,simColName});
			}
		}else{
			multisourcePlotPane.setDataSources(null);
		}

	}
	private TableModel getTableModel(final String[] columnNames,final Object[][] rowData){

		summaryData = rowData;
		
		TableModel tableModel = 
			new AbstractTableModel() {
			    public String getColumnName(int col) {
			        return columnNames[col].toString();
			    }
			    public int getRowCount() {
			    	return rowData.length; }
			    public int getColumnCount() {
			    	return columnNames.length; }
			    public Object getValueAt(int row, int col) {
			        return rowData[row][col];
			    }
			    public boolean isCellEditable(int row, int col){
			    	return false;
			    }
			    public void setValueAt(Object value, int row, int col) {
			        rowData[row][col] = value;
			        fireTableCellUpdated(row, col);
			    }
			};
		return tableModel;
	}
	
	private static String[] getColumnNames(){
		String[] columnNames = new String[SpatialAnalysisResults.ORDERED_ROITYPES.length+1];
		for (int i = 0; i < columnNames.length; i++) {
			if(i==0){
				columnNames[i] = "diffusion Rate";
			}else{
				columnNames[i] = SpatialAnalysisResults.ORDERED_ROITYPES[i-1].name();
			}
		}
		return columnNames;
	}
	public void setData(FRAPStudy.SpatialAnalysisResults spatialAnalysisResults,
			final double[] frapDataTimeStamps,int startIndexForRecovery,final Double modelDiffusionRate) throws Exception{
		allDataHash = new Hashtable<Double, DataSource[]>();
		
		Double[] diffusionRates = spatialAnalysisResults.diffusionRates;
		Hashtable<CurveInfo, double[]> ROIInfoHash = spatialAnalysisResults.curveHash;
		Set<CurveInfo> roiInfoSet = ROIInfoHash.keySet();
		Iterator<CurveInfo> roiInfoIter = roiInfoSet.iterator();
		Hashtable<RoiType, double[]> expROIData = new Hashtable<RoiType, double[]>();
		Hashtable<Double, Hashtable<RoiType, double[]>> simROIData = new Hashtable<Double, Hashtable<RoiType,double[]>>();
		int roiCount = 0;
		int diffusionRateCount = 0;
		while(roiInfoIter.hasNext()){
			CurveInfo roiCurveInfo = roiInfoIter.next();
			if(roiCurveInfo.isExperimentInfo()){
				expROIData.put(roiCurveInfo.getROIType(), ROIInfoHash.get(roiCurveInfo));
				roiCount++;
			}else{
				Hashtable<RoiType,double[]> simROIDataHash = simROIData.get(roiCurveInfo.getDiffusionRate());
				if(simROIDataHash == null){
					simROIDataHash  = new Hashtable<RoiType, double[]>();
					simROIData.put(roiCurveInfo.getDiffusionRate(), simROIDataHash);
					diffusionRateCount++;
				}
				simROIDataHash.put(roiCurveInfo.getROIType(), ROIInfoHash.get(roiCurveInfo));
			}
		}
		
		final int DIFFUSION_COLUMN_COMPENSATE = 1;
		final String[] columnNames = getColumnNames();

		ReferenceData referenceData = 
			spatialAnalysisResults.createReferenceData(frapDataTimeStamps,null,startIndexForRecovery,"");
		
		final Object[][] tableData = new Object[diffusionRateCount][columnNames.length];
		for (int diffusionRow = 0; diffusionRow < tableData.length; diffusionRow++) {
			Double currentDiffusionRate = diffusionRates[diffusionRow];
			tableData[diffusionRow][COLUMN_INDEX_DIFFUSION_RATE] = currentDiffusionRate;
			
			ODESolverResultSet odeSolverResultSet =
				spatialAnalysisResults.createODESolverResultSet(currentDiffusionRate,null,"D="+NumberUtils.formatNumber(currentDiffusionRate, 3));
			final DataSource expDataSource = new DataSource(referenceData,"exp");
			final DataSource simDataSource = new DataSource(odeSolverResultSet, "sim");
			DataSource[] newDataSourceArr = new DataSource[2];
			newDataSourceArr[ARRAY_INDEX_EXPDATASOURCE] = expDataSource;
			newDataSourceArr[ARRAY_INDEX_SIMDATASOURCE] = simDataSource;
			allDataHash.put(currentDiffusionRate,newDataSourceArr);
			
			for (int roiColumn = 0; roiColumn < SpatialAnalysisResults.ORDERED_ROITYPES.length; roiColumn++) {
				RoiType currentROIType = SpatialAnalysisResults.ORDERED_ROITYPES[roiColumn];
				double standardError = 
					calulateStandardError(
						spatialAnalysisResults,currentDiffusionRate,
						frapDataTimeStamps,currentROIType,startIndexForRecovery);
				tableData[diffusionRow][roiColumn+DIFFUSION_COLUMN_COMPENSATE] =
					new Double(NumberUtils.formatNumber(standardError, 5));//NumberUtils.formatNumber(standardError, 5);
			}
		}

		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
			try{
				table.setModel(getTableModel(columnNames,tableData));
				sortColumn(COLUMN_INDEX_DIFFUSION_RATE,false);
				multisourcePlotPane.forceXYRange(new Range(frapDataTimeStamps[0],frapDataTimeStamps[frapDataTimeStamps.length-1]), new Range(0,1));
				if(modelDiffusionRate != null){
					int matchingRow = -1;
					for (int i = 0; i < summaryData.length; i++) {
						if(summaryData[i][COLUMN_INDEX_DIFFUSION_RATE].equals(modelDiffusionRate)){
							matchingRow = i;
							break;
						}
					}
					if(matchingRow != -1){
						setTableCellSelection(matchingRow, COLUMN_INDEX_BLEACHROI);
					}else{
						DialogUtils.showErrorDialog("Summary Table couldn't find model diffusion rate "+modelDiffusionRate);
					}
				}
			}catch(Exception e){
				throw new RuntimeException("Error setting tableModel. "+e.getMessage());
			}
		}});
	}
	
	private void setTableCellSelection(int selectedRow, int selectedColumn){
		table.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
		table.getColumnModel().getSelectionModel().setSelectionInterval(selectedColumn, selectedColumn);
	}
	
	private double calulateStandardError(FRAPStudy.SpatialAnalysisResults spatialAnalysisResults,
			Double diffusionRate,double[] frapDataTimeStamps,RoiType roiType,int startTimeIndex){
		ODESolverResultSet odeSolverResultSet =
			spatialAnalysisResults.createODESolverResultSet(diffusionRate,roiType,"");
		ReferenceData referenceData = spatialAnalysisResults.createReferenceData(frapDataTimeStamps,roiType,startTimeIndex,"");
		
		int numSamples = referenceData.getNumRows();
		double sumSquaredError = MathTestingUtilities.calcWeightedSquaredError(odeSolverResultSet, referenceData);
		return Math.sqrt(sumSquaredError)/(numSamples-1);//unbiased estimator is numsamples-1
	}
	public void clearData(){
		allDataHash = null;
	}
	public boolean hasData(){
		return allDataHash != null;
	}
}
