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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.Coordinate;
import org.vcell.util.DataJobListenerHolder;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataJobID;

import cbit.plot.Plot2D;
import cbit.plot.SingleXPlot2D;
import cbit.plot.gui.PlotPane;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.VariableType;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.SpatialSelectionMembrane;
import cbit.vcell.simdata.SpatialSelectionVolume;
import cbit.vcell.solver.Simulation;

public class PdeTimePlotMultipleVariablesPanel extends JPanel {
	private TSJobResultsNoStats tsJobResultsNoStats = null;
	private PlotPane plotPane = null;
	private Vector<SpatialSelection> pointVector = null;
	private Vector<SpatialSelection> pointVector2 = null;
	private JList<DataIdentifier> variableJList = new JList<DataIdentifier>();
	private EventHandler eventHandler = new EventHandler();
	private JList<String> pointJList = new JList<String>();
	MultiTimePlotHelper multiTimePlotHelper;
	
	private class EventHandler implements ListSelectionListener,PropertyChangeListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				showTimePlot();
			}
		}
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getSource() == multiTimePlotHelper && evt.getPropertyName().equals(PDEDataContext.PROPERTY_NAME_DATAIDENTIFIERS)){
				try{
					DataIdentifier selected = variableJList.getSelectedValue();
					variableJList.removeListSelectionListener(eventHandler);
					DataIdentifier[] newData = multiTimePlotHelper.getCopyOfDisplayedDataIdentifiers();
					variableJList.setListData(newData);
					initVariableListSelected(variableJList, selected);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					variableJList.addListSelectionListener(eventHandler);
				}
				showTimePlot();
			}
		}	
	}
	
	public interface MultiTimePlotHelper extends DataJobListenerHolder{
		DataIdentifier[] getCopyOfDisplayedDataIdentifiers();
		PDEDataContext getPdeDatacontext();
		User getUser();
		ListCellRenderer getListCellRenderer();
		Simulation getsimulation();
		void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);
		void removeallPropertyChangeListeners();
		VariableType getVariableType();
	}
	public PdeTimePlotMultipleVariablesPanel(MultiTimePlotHelper multiTimePlotHelper, Vector<SpatialSelection> pv, Vector<SpatialSelection> pv2, TSJobResultsNoStats tsjr) {
		this.multiTimePlotHelper=multiTimePlotHelper;
		pointVector = pv;
		pointVector2 = pv2;
		tsJobResultsNoStats = tsjr;

		initialize();
	}

	private static void initVariableListSelected(JList myVariableJList,DataIdentifier selected){
		boolean bHasdataIdentifier = false;
		for (int i = 0; i < myVariableJList.getModel().getSize(); i++) {
			if(myVariableJList.getModel().getElementAt(i).equals(selected)){
				bHasdataIdentifier = true;
				break;
			}
		}
		if(selected != null && bHasdataIdentifier){
			myVariableJList.setSelectedValue(selected, true);
		}
		if(myVariableJList.getSelectedIndex() == -1){
			myVariableJList.setSelectedIndex(0);
		}
	}
	public void showTimePlot() {
		VariableType varType = multiTimePlotHelper.getVariableType();
		Object[] selectedValues = variableJList.getSelectedValues();
		DataIdentifier[] selectedDataIdentifiers = new DataIdentifier[selectedValues.length];
		System.arraycopy(selectedValues, 0, selectedDataIdentifiers, 0, selectedValues.length);
		if (selectedDataIdentifiers.length > 1) {
			for (DataIdentifier selectedDataIdentifier : selectedDataIdentifiers) {
				if (!selectedDataIdentifier.getVariableType().getVariableDomain().equals(varType.getVariableDomain())) {
					PopupGenerator.showErrorDialog(this, "Please choose VOLUME variables or MEMBRANE variables only");
					variableJList.clearSelection();
					variableJList.setSelectedValue(multiTimePlotHelper.getPdeDatacontext().getVariableName(), true);
					return;
				}
			}
		}		
		
		try {		
			final int numSelectedVariables = selectedDataIdentifiers.length;
			final int numSelectedSpatialPoints = pointVector.size();
			int[][] indices = new int[numSelectedVariables][numSelectedSpatialPoints];
			//
			for (int i = 0; i < numSelectedSpatialPoints; i++){
				for (int v = 0; v < numSelectedVariables; v ++) {
					if (selectedDataIdentifiers[v].getVariableType().equals(varType)) {
						if (varType.equals(VariableType.VOLUME) || varType.equals(VariableType.VOLUME_REGION)){
							SpatialSelectionVolume ssv = (SpatialSelectionVolume)pointVector.get(i);
							indices[v][i] = ssv.getIndex(0);
						}else if(varType.equals(VariableType.MEMBRANE) || varType.equals(VariableType.MEMBRANE_REGION)){
							SpatialSelectionMembrane ssm = (SpatialSelectionMembrane)pointVector.get(i);
							indices[v][i] = ssm.getIndex(0);
						}
					} else {
						if (varType.equals(VariableType.VOLUME) || varType.equals(VariableType.VOLUME_REGION)){
							SpatialSelectionVolume ssv = (SpatialSelectionVolume)pointVector2.get(i);
							indices[v][i] = ssv.getIndex(0);
						}else if(varType.equals(VariableType.MEMBRANE) || varType.equals(VariableType.MEMBRANE_REGION)){
							SpatialSelectionMembrane ssm = (SpatialSelectionMembrane)pointVector2.get(i);
							indices[v][i] = ssm.getIndex(0);
						}
					}
				}
			}

			final String[] selectedVarNames = new String[numSelectedVariables];
			for (int i = 0; i < selectedVarNames.length; i++) {
				selectedVarNames[i] = selectedDataIdentifiers[i].getName();
			}
			final double[] timePoints = multiTimePlotHelper.getPdeDatacontext().getTimePoints();
			TimeSeriesJobSpec tsjs = new TimeSeriesJobSpec(selectedVarNames,
					indices, null, timePoints[0], 1, timePoints[timePoints.length-1],
					VCDataJobID.createVCDataJobID(multiTimePlotHelper.getUser(), true));

			if (!tsjs.getVcDataJobID().isBackgroundTask()){
				throw new RuntimeException("Use getTimeSeries(...) if not a background job");
			}
			
			Hashtable<String, Object> hash = new Hashtable<String, Object>();
			hash.put(PDEDataViewer.StringKey_timeSeriesJobSpec, tsjs);
			
			AsynchClientTask task1 = new PDEDataViewer.TimeSeriesDataRetrievalTask("Retrieving Data",multiTimePlotHelper,multiTimePlotHelper.getPdeDatacontext());	
			AsynchClientTask task2 = new AsynchClientTask("showing time plot", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					TSJobResultsNoStats tsJobResultsNoStats = (TSJobResultsNoStats)hashTable.get(PDEDataViewer.StringKey_timeSeriesJobResults);
					int plotCount = numSelectedVariables * numSelectedSpatialPoints;
					SymbolTableEntry[] symbolTableEntries = new SymbolTableEntry[plotCount];
					String[] plotNames = new String[plotCount];
					double[][] plotDatas = new double[1 + plotCount][];
					plotDatas[0] = timePoints;
					int plotIndex = 0;
					for (int v = 0; v < numSelectedVariables; v ++) {
						String varName = selectedVarNames[v];
						double[][] data = tsJobResultsNoStats.getTimesAndValuesForVariable(varName);
						for (int i = 1; i < data.length; i++) {
							symbolTableEntries[plotIndex] = multiTimePlotHelper.getsimulation().getMathDescription().getEntry(varName);
							plotNames[plotIndex] = varName + " at P[" + (i-1) + "]";
							plotDatas[plotIndex + 1] = data[i];
							plotIndex ++;
						}
					}
					Plot2D plot2D = new SingleXPlot2D(symbolTableEntries, ReservedVariable.TIME.getName(), plotNames, plotDatas, 
							new String[] {"Time Plot", ReservedVariable.TIME.getName(), ""});				
					plotPane.setPlot2D(plot2D);
				}						
			};		
			
			ClientTaskDispatcher.dispatch(this, hash, new AsynchClientTask[] { task1, task2 },false, true, true, null,false);

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
	}

	private void initialize() {		
		VariableType varType = multiTimePlotHelper.getPdeDatacontext().getDataIdentifier().getVariableType();
		String varName = multiTimePlotHelper.getPdeDatacontext().getVariableName();
		String[] plotNames = new String[pointVector.size()];
		final SymbolTableEntry[] symbolTableEntries = new SymbolTableEntry[plotNames.length];
		DefaultListModel<String> pointListModel = new DefaultListModel<String>();
		for (int i = 0; i < pointVector.size(); i++){
			Coordinate tp = null;
			if (varType.equals(VariableType.VOLUME) || varType.equals(VariableType.VOLUME_REGION) || varType.equals(VariableType.POSTPROCESSING)){
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
			if (multiTimePlotHelper.getsimulation()!=null){
				symbolTableEntries[0] = multiTimePlotHelper.getsimulation().getMathDescription().getEntry(varName);
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
		double[][] plotDatas = tsJobResultsNoStats.getTimesAndValuesForVariable(varName);
		Plot2D plot2D = new SingleXPlot2D(symbolTableEntries, ReservedVariable.TIME.getName(), plotNames, plotDatas, 
				new String[] {"Time Plot", ReservedVariable.TIME.getName(), ""});				
		plotPane.setPlot2D(plot2D);
		
		DataIdentifier[] dis = (multiTimePlotHelper.getCopyOfDisplayedDataIdentifiers()!=null?multiTimePlotHelper.getCopyOfDisplayedDataIdentifiers():DataIdentifier.collectSortedSimilarDataTypes(multiTimePlotHelper.getVariableType(), multiTimePlotHelper.getPdeDatacontext().getDataIdentifiers()));
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
		initVariableListSelected(variableJList, multiTimePlotHelper.getPdeDatacontext().getDataIdentifier());
		variableJList.setCellRenderer(multiTimePlotHelper.getListCellRenderer());
		
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
		
		variableJList.addListSelectionListener(eventHandler);
		multiTimePlotHelper.addPropertyChangeListener(eventHandler);

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

		int dim = multiTimePlotHelper.getPdeDatacontext().getCartesianMesh().getGeometryDimension();
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
