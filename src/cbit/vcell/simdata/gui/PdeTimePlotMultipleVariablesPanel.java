/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.Coordinate;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.VCDataJobID;

import cbit.plot.Plot2D;
import cbit.plot.SingleXPlot2D;
import cbit.plot.gui.PlotPane;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.data.PDEDataViewer.DataInfoProvider;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.VariableType;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.SpatialSelectionMembrane;
import cbit.vcell.simdata.SpatialSelectionVolume;
import cbit.vcell.solver.Simulation;

public class PdeTimePlotMultipleVariablesPanel extends JPanel implements PropertyChangeListener{
	private PDEDataContext pdeDataContext = null;
	private PlotPane plotPane = null;
	private Vector<SpatialSelection> pointVector = null;
	private JList<DataIdentifier> variableJList = new JList<DataIdentifier>();
	private EventHandler eventHandler = new EventHandler();
	private Simulation simulation = null;
	private PDEDataViewer pdeDataViewer = null;
	private JList<String> pointJList = new JList<String>();
	private ListCellRenderer<DataIdentifier> listCellRenderer = null;
	private DataInfoProvider dataInfoProvider;
	private DataIdentifier initialDataIdentifier;
	
	private class EventHandler implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				showTimePlot();
			}
		}
		
	}
	public PdeTimePlotMultipleVariablesPanel(PDEDataViewer pdv, ListCellRenderer<DataIdentifier> lcr, Simulation sim, Vector<SpatialSelection> pv/*, Vector<SpatialSelection> pv2, TSJobResultsNoStats tsjr*/,DataInfoProvider dataInfoProvider) {
		pdeDataViewer = pdv;
		listCellRenderer = lcr;
		simulation = sim;
		pdeDataContext = pdeDataViewer.getPdeDataContext();
		this.initialDataIdentifier = pdeDataContext.getDataIdentifier();
		pointVector = pv;
		this.dataInfoProvider = dataInfoProvider;
		
		initialize();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getSource() == pdeDataContext &&
			(/*evt.getPropertyName().equals(SimDataConstants.PROPERTY_NAME_DATAIDENTIFIERS) || */evt.getPropertyName().equals(SimDataConstants.PDE_DATA_MANAGER_CHANGED))){
			initVariableJList();
			showTimePlot();
		}
	}

	public void showTimePlot() {
		List<DataIdentifier> selectedDataIdentifiers = variableJList.getSelectedValuesList();
		if (selectedDataIdentifiers.size() > 1) {
			for (DataIdentifier selectedDataIdentifier : selectedDataIdentifiers) {
				if (!selectedDataIdentifier.getVariableType().getVariableDomain().equals(initialDataIdentifier.getVariableType().getVariableDomain())) {
					PopupGenerator.showErrorDialog(this, "Please choose VOLUME variables or MEMBRANE variables only");
					variableJList.clearSelection();
					variableJList.setSelectedValue(pdeDataContext.getVariableName(), true);
					return;
				}
			}
		}		
		
		try {		
			HashMap<DataIdentifier,ArrayList<SpatialSelection>> mapDataIdentifierToMarkers = new HashMap<>();
			for (int v = 0; v < selectedDataIdentifiers.size(); v ++) {
				ArrayList<SpatialSelection> currentSpatialSelections = new ArrayList<SpatialSelection>();
				VariableType selectedVariableType = selectedDataIdentifiers.get(v).getVariableType();
				for(SpatialSelection spatialSelection:pointVector){
					if((selectedVariableType.equals(VariableType.VOLUME) || selectedVariableType.equals(VariableType.VOLUME_REGION)) &&
							spatialSelection instanceof SpatialSelectionVolume &&
							(dataInfoProvider == null || dataInfoProvider.isDefined(selectedDataIdentifiers.get(v),((SpatialSelectionVolume)spatialSelection).getIndex(0)))){
						currentSpatialSelections.add(spatialSelection);
					}else if((selectedVariableType.equals(VariableType.MEMBRANE) || selectedVariableType.equals(VariableType.MEMBRANE_REGION)) &&
							spatialSelection instanceof SpatialSelectionMembrane &&
							(dataInfoProvider == null || dataInfoProvider.isDefined(selectedDataIdentifiers.get(v),((SpatialSelectionMembrane)spatialSelection).getIndex(0)))){
						currentSpatialSelections.add(spatialSelection);
					}else if(selectedVariableType.equals(VariableType.POSTPROCESSING) && spatialSelection instanceof SpatialSelectionVolume){
						currentSpatialSelections.add(spatialSelection);						
					}
				}
				if(currentSpatialSelections.size() > 0){
					mapDataIdentifierToMarkers.put(selectedDataIdentifiers.get(v), currentSpatialSelections);
				}
			}
			
			final String[] selectedVarNames = new String[mapDataIdentifierToMarkers.size()];
			final int[] plotCountHolder = new int[] {0};
			final HashMap<String, DataIdentifier> mapVarNameToDataIdentifier = new HashMap<>();
			int dataIdentifierCount = 0;
			int[][] indices = new int[mapDataIdentifierToMarkers.size()][];
			for (DataIdentifier dataIdentifier:mapDataIdentifierToMarkers.keySet()) {
				selectedVarNames[dataIdentifierCount] = dataIdentifier.getName();
				mapVarNameToDataIdentifier.put(dataIdentifier.getName(), dataIdentifier);
				indices[dataIdentifierCount] = new int[mapDataIdentifierToMarkers.get(dataIdentifier).size()];
				int spatialSelectionCount = 0;
				for (SpatialSelection spatialSelection:mapDataIdentifierToMarkers.get(dataIdentifier)) {
					indices[dataIdentifierCount][spatialSelectionCount] = spatialSelection.getIndex(0);
					plotCountHolder[0]++;
					spatialSelectionCount++;
				}
				dataIdentifierCount++;
			}

			final double[] timePoints = pdeDataContext.getTimePoints();
			TimeSeriesJobSpec tsjs = new TimeSeriesJobSpec(selectedVarNames,
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
					SymbolTableEntry[] symbolTableEntries = new SymbolTableEntry[plotCountHolder[0]];
					String[] plotNames = new String[plotCountHolder[0]];
					double[][] plotDatas = new double[1 + plotCountHolder[0]][];
					plotDatas[0] = timePoints;
					int plotIndex = 0;
					for (int v = 0; v < selectedVarNames.length; v ++) {
						String varName = selectedVarNames[v];
						double[][] data = tsJobResultsNoStats.getTimesAndValuesForVariable(varName);
						for (int i = 1; i < data.length; i++) {
							symbolTableEntries[plotIndex] = simulation.getMathDescription().getEntry(varName);
							plotNames[plotIndex] = "("+getDescription(mapDataIdentifierToMarkers.get(mapVarNameToDataIdentifier.get(varName)).get(i-1),null)+") "+varName;
							plotDatas[plotIndex + 1] = data[i];
							plotIndex ++;
						}
					}
					Plot2D plot2D = new SingleXPlot2D(symbolTableEntries, null, ReservedVariable.TIME.getName(), plotNames, plotDatas, 
							new String[] {"Time Plot", ReservedVariable.TIME.getName(), ""});				
					plotPane.setPlot2D(plot2D);
				}						
			};		
			
			if(plotCountHolder[0] > 0){
				ClientTaskDispatcher.dispatch(this, hash, new AsynchClientTask[] { task1, task2 }, true, true, null);
			}else{
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						plotPane.setPlot2D(null);
					}
				});
			}

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
	}

	private static String getDescription(SpatialSelection spatialSelection,Integer geometryDimension){
		String description = null;
		Coordinate tp = null;
		if(spatialSelection instanceof SpatialSelectionVolume){
			SpatialSelectionVolume ssv = (SpatialSelectionVolume)spatialSelection;
			tp = ssv.getCurveSelectionInfo().getCurve().getBeginningCoordinate();
			description = ssv.getCurveSelectionInfo().getCurve().getDescription();
		}else if(spatialSelection instanceof SpatialSelectionMembrane){
			SpatialSelectionMembrane ssm = (SpatialSelectionMembrane)spatialSelection;
			double midU = ssm.getCurveSelectionInfo().getCurveUfromSelectionU(.5);
			tp = ((SampledCurve)ssm.getCurveSelectionInfo().getCurve()).coordinateFromNormalizedU(midU);
			description = ssm.getSelectionSource().getDescription();
		}
		return description +(geometryDimension!=null?(tp==null?"":" (" + niceCoordinateString(tp,geometryDimension)+")"):"");
	}
	
	private void initVariableJList(){
		List<DataIdentifier> currentlySelectedList = variableJList.getSelectedValuesList();
		try{
			variableJList.removeListSelectionListener(eventHandler);
			DataIdentifier[] dis = DataIdentifier.collectSimilarDataTypes(initialDataIdentifier, pdeDataContext.getDataIdentifiers());		
			Arrays.sort(dis, new Comparator<DataIdentifier>(){
				public int compare(DataIdentifier o1, DataIdentifier o2) {
					int bEqualIgnoreCase = o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
					if (bEqualIgnoreCase == 0){
						return o1.getDisplayName().compareTo(o2.getDisplayName());
					}
					return bEqualIgnoreCase;
				}
			});
			variableJList.setListData(dis);
			if(currentlySelectedList != null){
				ArrayList<Integer> selectedIntegers = new ArrayList<>();
				for(int i=0;i<variableJList.getModel().getSize();i++){
					if(currentlySelectedList.contains(variableJList.getModel().getElementAt(i))){
						selectedIntegers.add(i);
					}
				}
				if(selectedIntegers.size() > 0){
					int[] selectedIndices = new int[selectedIntegers.size()];
					int selectCount = 0;
					for(Integer selection:selectedIntegers){
						selectedIndices[selectCount] = selection;
						selectCount++;
					}
					variableJList.setSelectedIndices(selectedIndices);
				}
			}
		}finally{
			variableJList.addListSelectionListener(eventHandler);
		}

	}
	private void initialize() {		
		VariableType varType = initialDataIdentifier.getVariableType();
		String varName = pdeDataContext.getVariableName();
		final SymbolTableEntry[] symbolTableEntries = new SymbolTableEntry[pointVector.size()];
		DefaultListModel<String> pointListModel = new DefaultListModel<String>();
		for (int i = 0; i < pointVector.size(); i++){
			if (varType.equals(VariableType.VOLUME) || varType.equals(VariableType.VOLUME_REGION) || varType.equals(VariableType.POSTPROCESSING)){
				pointListModel.addElement(getDescription(pointVector.get(i), pdeDataContext.getCartesianMesh().getGeometryDimension()));
			}else if(varType.equals(VariableType.MEMBRANE) || varType.equals(VariableType.MEMBRANE_REGION)){
				pointListModel.addElement(getDescription(pointVector.get(i), pdeDataContext.getCartesianMesh().getGeometryDimension()));
			}			
			if (simulation!=null){
				symbolTableEntries[0] = simulation.getMathDescription().getEntry(varName);
			}else{
				System.out.println("PdeTimePlotMultipleVariablesPanel.initialize() adding artificial symbol table entries for field data");
				SimpleSymbolTable simpleSymbolTable = new SimpleSymbolTable(new String[] { varName });
				symbolTableEntries[0] = simpleSymbolTable.getEntry(varName);			
			}
		}
		pointJList.setModel(pointListModel);
		pointJList.setForeground(variableJList.getForeground());
		pointJList.setVisibleRowCount(3);
		pointJList.setBackground(getBackground());
		pointJList.setSelectionBackground(getBackground());
		pointJList.setSelectionForeground(Color.black);
		
		plotPane = new PlotPane();
		initVariableJList();
		variableJList.setCellRenderer(listCellRenderer);
		
		setLayout(new GridBagLayout());		
		
		JLabel label = new JLabel("Selected Points");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new java.awt.Insets(15, 10, 4, 4);
		add(label, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = 0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.gridheight = 4;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new java.awt.Insets(0, 4, 0, 0);
		add(plotPane, gbc);

		JScrollPane sp = new JScrollPane(pointJList);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		gbc = new GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weighty = 0.5;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new java.awt.Insets(4, 10, 4, 4);
		add(sp, gbc);
		
		label = new JLabel("Y Axis");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		gbc = new GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new java.awt.Insets(4, 10, 4, 4);
		add(label, gbc);
			
		sp = new JScrollPane(variableJList);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		gbc = new GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 3;
		gbc.weightx = 0.2;
		gbc.weighty = 1;
		gbc.insets = new java.awt.Insets(4, 10, 50, 4);
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		add(sp, gbc);
		
		variableJList.setSelectedValue(initialDataIdentifier, true);
		if(variableJList.getSelectedIndex() == -1 && variableJList.getModel().getSize() > 0){
			variableJList.setSelectedIndex(0);
		}
	}
	

	/**
	 * Insert the method's description here.
	 * Creation date: (10/8/2004 7:09:31 AM)
	 * @return java.lang.String
	 * @param coord cbit.vcell.geometry.Coordinate
	 */
	private static String niceCoordinateString(Coordinate coord,int geometryDimension) {

		//reduce fraction digits of the form XX.xxxxxxxxxxxxxxy
		//to something more reasonable

//		int dim = pdeDataContext.getCartesianMesh().getGeometryDimension();
		final int MAX_CHARS = 3;
		String result = "";
		for(int i=0;i<geometryDimension;i+= 1){
			double xyz = -1;
			if(i==0){xyz = coord.getX();}
			else if(i==1){xyz = coord.getY();}
			else if(i==2){xyz = coord.getZ();}
			
			result+= (i!=0?",":"")+(double)((int)(xyz * Math.pow(10,MAX_CHARS)))/Math.pow(10,MAX_CHARS);//NumberUtils.formatNumber(xyz,MAX_CHARS);
		}

		return result;
	}
}
