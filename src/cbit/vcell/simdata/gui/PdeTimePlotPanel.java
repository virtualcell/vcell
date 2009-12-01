package cbit.vcell.simdata.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.Coordinate;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.VCDataJobID;

import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.plot.PlotPane;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.math.VolVariable;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.Simulation;

public class PdeTimePlotPanel extends JPanel {
	private PDEDataContext pdeDataContext = null;
	private TSJobResultsNoStats tsJobResultsNoStats = null;
	private PlotPane plotPane = null;
	private Vector<SpatialSelection> pointVector = null;
	private JList variableJList = new JList();
	private EventHandler eventHandler = new EventHandler();
	private Simulation simulation = null;
	private PDEDataViewer pdeDataViewer = null;
	private JList pointJList = new JList();
	
	private class EventHandler implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			showTimePlot();			
		}
		
	}
	public PdeTimePlotPanel(PDEDataViewer pdv, Simulation sim, Vector<SpatialSelection> pv, TSJobResultsNoStats tsjr) {
		pdeDataViewer = pdv;
		simulation = sim;
		pdeDataContext = pdeDataViewer.getPdeDataContext();
		pointVector = pv;
		tsJobResultsNoStats = tsjr;
		
		initialize();
	}

	public void showTimePlot() {
		VariableType varType = pdeDataContext.getDataIdentifier().getVariableType();
		Object[] selectedValues = variableJList.getSelectedValues();
		final String[] selectedVarNames = new String[selectedValues.length];
		System.arraycopy(selectedValues, 0, selectedVarNames, 0, selectedValues.length);
		if (selectedVarNames.length > 1) {
			DataIdentifier[] dataIdentifiers = pdeDataContext.getDataIdentifiers();		
			for (String name : selectedVarNames) {
				for (DataIdentifier dataIdentifier : dataIdentifiers) {
					if (dataIdentifier.getName().equals(name)) {
						if (!dataIdentifier.getVariableType().equals(varType)) {
							PopupGenerator.showErrorDialog(this, "Please choose VOLUME variables or MEMBRANE variables only");
							variableJList.clearSelection();
							variableJList.setSelectedValue(pdeDataContext.getVariableName(), true);
							return;
						}
					}
				}
			}
		}		
		
		try {		
			final int numSelectedVariables = selectedVarNames.length;
			final int numSelectedSpatialPoints = pointVector.size();
			int[][] indices = new int[numSelectedVariables][numSelectedSpatialPoints];
			//
			final String[] plotNames = new String[numSelectedVariables * numSelectedSpatialPoints];
			final SymbolTableEntry[] symbolTableEntries = new SymbolTableEntry[numSelectedVariables * numSelectedSpatialPoints];
			for (int i = 0; i < numSelectedSpatialPoints; i++){
				for (int v = 0; v < numSelectedVariables; v ++) {
					String varName = selectedVarNames[v];
					if (varType.equals(VariableType.VOLUME) || varType.equals(VariableType.VOLUME_REGION)){
						SpatialSelectionVolume ssv = (SpatialSelectionVolume)pointVector.get(i);
						indices[v][i] = ssv.getIndex(0);
					}else if(varType.equals(VariableType.MEMBRANE) || varType.equals(VariableType.MEMBRANE_REGION)){
						SpatialSelectionMembrane ssm = (SpatialSelectionMembrane)pointVector.get(i);
						indices[v][i] = ssm.getIndex(0);
					}
					int plotIndex = v * numSelectedSpatialPoints + i;
					plotNames[plotIndex] = varName + " at P["+i+"]";
					try{
						if(simulation != null && simulation.getMathDescription() != null){
							symbolTableEntries[plotIndex] = simulation.getMathDescription().getEntry(varName);
						}
						if(symbolTableEntries[plotIndex] == null){
							symbolTableEntries[plotIndex] = new VolVariable(varName);
						}
					}catch(ExpressionBindingException e){
						e.printStackTrace();
					}
				}
			}

			final double[] timePoints = pdeDataContext.getTimePoints();
			TimeSeriesJobSpec tsjs = new TimeSeriesJobSpec(	selectedVarNames,
					indices, null, timePoints[0], 1, timePoints[timePoints.length-1],
					VCDataJobID.createVCDataJobID(pdeDataViewer.getDataViewerManager().getUser(), true));

			if (!tsjs.getVcDataJobID().isBackgroundTask()){
				throw new RuntimeException("Use getTimeSeries(...) if not a background job");
			}
			
			Hashtable<String, Object> hash = new Hashtable<String, Object>();
			hash.put(PDEDataViewer.StringKey_timeSeriesJobSpec, tsjs);
			
			AsynchClientTask task1 = pdeDataViewer.new TimeSeriesDataRetrievalTask("Retrieving Data");	
			AsynchClientTask task2 = new AsynchClientTask("showing time plot", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					TSJobResultsNoStats tsJobResultsNoStats = (TSJobResultsNoStats)hashTable.get(PDEDataViewer.StringKey_timeSeriesJobResults);
					PlotData[] plotDatas = new PlotData[numSelectedVariables * numSelectedSpatialPoints];
					int plotCount = 0;
					for (int v = 0; v < numSelectedVariables; v ++) {
						String varName = selectedVarNames[v];
						double[][] data = tsJobResultsNoStats.getTimesAndValuesForVariable(varName);
						for (int i = 1; i < data.length; i++) {
							plotDatas[plotCount ++] = new PlotData(data[0], data[i]);
						}
					}
					Plot2D plot2D = new Plot2D(symbolTableEntries, plotNames, plotDatas, 
							new String[] {null, "t", ""});				
					plotPane.setPlot2D(plot2D);
				}						
			};		
			ClientTaskDispatcher.dispatch(this, hash, new AsynchClientTask[] { task1, task2 }, true, true, null);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
	}

	private void initialize() {		
		VariableType varType = pdeDataContext.getDataIdentifier().getVariableType();
		String varName = pdeDataContext.getVariableName();
		String[] plotNames = new String[pointVector.size()];
		final SymbolTableEntry[] symbolTableEntries = new SymbolTableEntry[plotNames.length];
		DefaultListModel pointListModel = new DefaultListModel();
		for (int i = 0; i < pointVector.size(); i++){
			Coordinate tp = null;
			if (varType.equals(VariableType.VOLUME) || varType.equals(VariableType.VOLUME_REGION)){
				SpatialSelectionVolume ssv = (SpatialSelectionVolume)pointVector.get(i);
				tp = ssv.getCurveSelectionInfo().getCurve().getBeginningCoordinate();
			}else if(varType.equals(VariableType.MEMBRANE) || varType.equals(VariableType.MEMBRANE_REGION)){
				SpatialSelectionMembrane ssm = (SpatialSelectionMembrane)pointVector.get(i);
				double midU = ssm.getCurveSelectionInfo().getCurveUfromSelectionU(.5);
				tp = ((SampledCurve)ssm.getCurveSelectionInfo().getCurve()).coordinateFromNormalizedU(midU);
			}			
			plotNames[i] = varName + " at P[" + i +"]";
			String point = "P[" + i +"]  (" + niceCoordinateString(tp)+")";
			pointListModel.addElement(point);
			try{				
				if(simulation != null && simulation.getMathDescription() != null){
					symbolTableEntries[0] = simulation.getMathDescription().getEntry(varName);
				}
				if(symbolTableEntries[0] == null){
					symbolTableEntries[0] = new VolVariable(varName);
				}
			}catch(ExpressionBindingException e){
				e.printStackTrace();
			}
		}
		pointJList.setModel(pointListModel);
		pointJList.setForeground(variableJList.getForeground());
		pointJList.setVisibleRowCount(3);
		pointJList.setBackground(getBackground());
		pointJList.setSelectionBackground(getBackground());
		pointJList.setSelectionForeground(Color.black);
		
		plotPane = new PlotPane();
		PlotData[] plotDatas = new PlotData[pointVector.size()];
		int plotCount = 0;
		double[][] data = tsJobResultsNoStats.getTimesAndValuesForVariable(varName);
		for (int i = 1; i < data.length; i++) {
			plotDatas[plotCount ++] = new PlotData(data[0], data[i]);
		}		
		Plot2D plot2D = new Plot2D(symbolTableEntries, plotNames, plotDatas, 
				new String[] {null, "t", ""});				
		plotPane.setPlot2D(plot2D);
		
		DefaultListModel dlm = new DefaultListModel();
		DataIdentifier[] dis = pdeDataContext.getDataIdentifiers();
		for (DataIdentifier di : dis) {
			if (di.getVariableType().equals(varType)) {
				dlm.addElement(di.getName());
			}
		}
		variableJList.setModel(dlm);
		variableJList.setSelectedValue(varName, true);
		
		setLayout(new GridBagLayout());		
		
		JLabel label = new JLabel("Selected Points");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new java.awt.Insets(15, 10, 4, 4);
		add(label, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1; gbc.gridy = 0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.gridwidth = 12;
		gbc.gridheight = 6;
		gbc.weightx = 6;
		gbc.weighty = 3;
		gbc.insets = new java.awt.Insets(0, 4, 0, 0);
		add(plotPane, gbc);

		JScrollPane sp = new JScrollPane(pointJList);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weighty = 0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new java.awt.Insets(4, 10, 4, 4);
		add(sp, gbc);
		
		label = new JLabel("Y Axis");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new java.awt.Insets(4, 10, 4, 4);
		add(label, gbc);
			
		sp = new JScrollPane(variableJList);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 3;
		gbc.weightx = 0.2;
		gbc.weighty = 0.3;
		gbc.insets = new java.awt.Insets(4, 10, 50, 4);
		gbc.gridheight = 3;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		add(sp, gbc);
		
		variableJList.addListSelectionListener(eventHandler);
		
	}
	

	/**
	 * Insert the method's description here.
	 * Creation date: (10/8/2004 7:09:31 AM)
	 * @return java.lang.String
	 * @param coord cbit.vcell.geometry.Coordinate
	 */
	private String niceCoordinateString(Coordinate coord) {

		//reduce fraction digits of the form XX.xxxxxxxxxxxxxxy
		//to something more reasonable

		int dim = pdeDataContext.getCartesianMesh().getGeometryDimension();
		final int MAX_CHARS = 3;
		String result = "";
		for(int i=0;i<dim;i+= 1){
			double xyz = -1;
			if(i==0){xyz = coord.getX();}
			else if(i==1){xyz = coord.getY();}
			else if(i==2){xyz = coord.getZ();}
			
			result+= (i!=0?",":"")+(double)((int)(xyz * Math.pow(10,MAX_CHARS)))/Math.pow(10,MAX_CHARS);//NumberUtils.formatNumber(xyz,MAX_CHARS);
		}

		return result;
	}

}
