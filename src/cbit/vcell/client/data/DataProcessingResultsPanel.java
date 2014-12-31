/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.data;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.BeanUtils;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.gui.DialogUtils;

import cbit.image.gui.DisplayAdapterService;
import cbit.image.gui.ImagePaneModel;
import cbit.image.gui.ImagePlaneManagerPanel;
import cbit.image.gui.SourceDataInfo;
import cbit.plot.Plot2D;
import cbit.plot.PlotPane;
import cbit.plot.SingleXPlot2D;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.solver.DataProcessingOutput;

@SuppressWarnings("serial")
public class DataProcessingResultsPanel extends JPanel/* implements PropertyChangeListener*/ {

	private JList<String> varJList;
	private PlotPane plotPane = null;
	private double[] timeArray;
	private DataProcessingOutput dpo;
	private JScrollPane graphScrollPane;
	private ImagePlaneManagerPanel imagePlaneManagerPanel;
	private JScrollPane imageScrollPane;
	private JList<String> imageList;
	private JSlider spatialTimeSlider;
	private JPanel timePanel;
	private JLabel minTimeLabel;
	private JLabel maxTimeLabel;
	private JPanel cardLayoutPanel;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel postProcessImageTimeLabel;
	
	public DataProcessingResultsPanel() {
		super();
		initialize();
	}
	private void initialize() {		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0, 0.0, 0.0, 0.0, 0.0};
		gridBagLayout.columnWeights = new double[]{0, 0};
		setLayout(gridBagLayout);
		varJList = new JList<String>();
		varJList.setVisibleRowCount(5);
		varJList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				if(varJList.getSelectedIndex() != -1){
					enableTimePanel(false);
					imageList.clearSelection();
					((CardLayout)cardLayoutPanel.getLayout()).show(cardLayoutPanel, "plotPane1");
				}
				onVariablesChange();
			}
		});
		
		lblNewLabel = new JLabel("Post Process Variable Statistics");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(4, 4, 0, 4);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);
		GridBagConstraints gbc_graphScrollPane = new GridBagConstraints();
		gbc_graphScrollPane.weighty = 0.5;
		gbc_graphScrollPane.gridx = 0;
		gbc_graphScrollPane.gridy = 1;
		gbc_graphScrollPane.insets = new Insets(2, 4, 4, 4);
		gbc_graphScrollPane.fill = GridBagConstraints.BOTH;
		graphScrollPane = new JScrollPane(varJList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(graphScrollPane, gbc_graphScrollPane);
		
		cardLayoutPanel = new JPanel();
		GridBagConstraints gbc_cardLayoutPanel = new GridBagConstraints();
		gbc_cardLayoutPanel.weightx = 1.0;
		gbc_cardLayoutPanel.weighty = 1.0;
		gbc_cardLayoutPanel.gridheight = 6;
		gbc_cardLayoutPanel.insets = new Insets(4, 4, 4, 4);
		gbc_cardLayoutPanel.fill = GridBagConstraints.BOTH;
		gbc_cardLayoutPanel.gridx = 1;
		gbc_cardLayoutPanel.gridy = 0;
		add(cardLayoutPanel, gbc_cardLayoutPanel);
		cardLayoutPanel.setLayout(new CardLayout(0, 0));
		
		plotPane = new PlotPane();
		cardLayoutPanel.add(plotPane,"plotPane1");
		
		imagePlaneManagerPanel = new ImagePlaneManagerPanel();
		imagePlaneManagerPanel.setMode(ImagePaneModel.MESH_MODE);
		imagePlaneManagerPanel.setDisplayAdapterServicePanelVisible(true);
		imagePlaneManagerPanel.getDisplayAdapterServicePanel().getDisplayAdapterService().setAutoScale(true);
		DisplayAdapterService das = imagePlaneManagerPanel.getDisplayAdapterServicePanel().getDisplayAdapterService();
		das.setValueDomain(null);
		das.addColorModelForValues(
			DisplayAdapterService.createGrayColorModel(), 
			DisplayAdapterService.createGraySpecialColors(),
			DisplayAdapterService.GRAY);
		das.addColorModelForValues(
			DisplayAdapterService.createBlueRedColorModel(),
			DisplayAdapterService.createBlueRedSpecialColors(),
			DisplayAdapterService.BLUERED);
		das.setActiveColorModelID(DisplayAdapterService.BLUERED);
		
		cardLayoutPanel.add(imagePlaneManagerPanel, "imagePlaneManagerPanel1");
		
		lblNewLabel_1 = new JLabel("Post Process Image Data");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(4, 4, 0, 4);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		imageScrollPane = new JScrollPane();
		imageScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		imageScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_imageScrollPane = new GridBagConstraints();
		gbc_imageScrollPane.weighty = 0.5;
		gbc_imageScrollPane.insets = new Insets(2, 4, 4, 4);
		gbc_imageScrollPane.fill = GridBagConstraints.BOTH;
		gbc_imageScrollPane.gridx = 0;
		gbc_imageScrollPane.gridy = 3;
		add(imageScrollPane, gbc_imageScrollPane);
		
		imageList = new JList<String>();
		imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		imageList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(!imageList.getValueIsAdjusting()){
					if(imageList.getSelectedIndex() != -1){
						varJList.clearSelection();
						((CardLayout)cardLayoutPanel.getLayout()).show(cardLayoutPanel, "imagePlaneManagerPanel1");
					}
					updateImageDisplay();
				}
			}
		});
		imageList.setVisibleRowCount(5);
		imageScrollPane.setViewportView(imageList);
		
		postProcessImageTimeLabel = new JLabel("Post Process Image Time");
		GridBagConstraints gbc_postProcessImageTimeLabel = new GridBagConstraints();
		gbc_postProcessImageTimeLabel.insets = new Insets(4, 4, 0, 4);
		gbc_postProcessImageTimeLabel.gridx = 0;
		gbc_postProcessImageTimeLabel.gridy = 4;
		add(postProcessImageTimeLabel, gbc_postProcessImageTimeLabel);
		
		timePanel = new JPanel();
		timePanel.setMinimumSize(new Dimension(250, 60));
		timePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_timePanel = new GridBagConstraints();
		gbc_timePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_timePanel.insets = new Insets(2, 4, 4, 4);
		gbc_timePanel.gridx = 0;
		gbc_timePanel.gridy = 5;
		add(timePanel, gbc_timePanel);
		
		GridBagLayout gbl_timePanel = new GridBagLayout();
		gbl_timePanel.columnWidths = new int[]{0, 0, 0};
		gbl_timePanel.rowHeights = new int[]{0, 0};
		gbl_timePanel.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_timePanel.rowWeights = new double[]{0.0, 0.0};
		timePanel.setLayout(gbl_timePanel);
		
		spatialTimeSlider = new JSlider();
		spatialTimeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
			        updateImageDisplay();
			    }
}
		});
		spatialTimeSlider.setPaintTicks(true);
		spatialTimeSlider.setValue(5);
		spatialTimeSlider.setMaximum(10);
		spatialTimeSlider.setMajorTickSpacing(1);
		GridBagConstraints gbc_spatialTimeSlider = new GridBagConstraints();
		gbc_spatialTimeSlider.weightx = 1.0;
		gbc_spatialTimeSlider.gridwidth = 3;
		gbc_spatialTimeSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_spatialTimeSlider.insets = new Insets(4, 4, 4, 4);
		gbc_spatialTimeSlider.gridx = 0;
		gbc_spatialTimeSlider.gridy = 0;
		timePanel.add(spatialTimeSlider, gbc_spatialTimeSlider);
		
		minTimeLabel = new JLabel("Min Time");
		GridBagConstraints gbc_minTimeLabel = new GridBagConstraints();
		gbc_minTimeLabel.anchor = GridBagConstraints.WEST;
		gbc_minTimeLabel.insets = new Insets(4, 4, 4, 4);
		gbc_minTimeLabel.gridx = 0;
		gbc_minTimeLabel.gridy = 1;
		timePanel.add(minTimeLabel, gbc_minTimeLabel);
		
		maxTimeLabel = new JLabel("Max Time");
		GridBagConstraints gbc_maxTimeLabel = new GridBagConstraints();
		gbc_maxTimeLabel.anchor = GridBagConstraints.EAST;
		gbc_maxTimeLabel.insets = new Insets(4, 4, 4, 4);
		gbc_maxTimeLabel.gridx = 2;
		gbc_maxTimeLabel.gridy = 1;
		timePanel.add(maxTimeLabel, gbc_maxTimeLabel);
		
		enableTimePanel(false);
	}

	private void updateImageDisplay(){
		String selectedVarName = (String)imageList.getSelectedValue();
		if(selectedVarName == null){
			imagePlaneManagerPanel.getDisplayAdapterServicePanel().getDisplayAdapterService().setValueDomain(null);
			imagePlaneManagerPanel.setSourceDataInfo(null);
			enableTimePanel(false);
		}else{
			enableTimePanel(true);
			Vector<SourceDataInfo> dataV = dpo.getDataGenerators().get(selectedVarName);
			int timeIndex = spatialTimeSlider.getValue();
			SourceDataInfo sdi = dataV.get(timeIndex);
			BeanUtils.enableComponents(imagePlaneManagerPanel.getDisplayAdapterServicePanel(), true);
			imagePlaneManagerPanel.getDisplayAdapterServicePanel().getDisplayAdapterService().setValueDomain(sdi.getMinMax());
			BeanUtils.enableComponents(imagePlaneManagerPanel.getDisplayAdapterServicePanel(), false);
			imagePlaneManagerPanel.setSourceDataInfo(sdi);
		}
	}
	
	private void enableTimePanel(boolean bEnable){
		postProcessImageTimeLabel.setEnabled(bEnable);
		BeanUtils.enableComponents(timePanel, bEnable);
	}

	private VCDataIdentifier lastVCDataIdentifier;
	private void read(PDEDataContext pdeDataContext) throws Exception {
		
		if(dpo != null && timeArray!= null && (timeArray.length == pdeDataContext.getTimePoints().length) && pdeDataContext.getVCDataIdentifier().equals(lastVCDataIdentifier)){
			System.out.println("--------------------"+timeArray.length+"=="+pdeDataContext.getTimePoints().length);
			//we already read this
			throw UserCancelException.CANCEL_GENERIC;
		}
		dpo = null;
		timeArray = null;
		lastVCDataIdentifier = pdeDataContext.getVCDataIdentifier();// parameter scan check
		
		try {
			dpo = pdeDataContext.getDataProcessingOutput();
			if (dpo != null) {
				timeArray = dpo.getTimes();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Data Processing Output Error - '"+e.getMessage()+"'  (Note: Data Processing Output is generated automatically when running VCell 5.2 or later simulations)");
		}
	}
	public void update(final PDEDataContext pdeDataContext) {
		
		final String SELECTED_LIST = "SELECTED_LIST";
		final String SELECTED_ITEMS = "SELECTED_ITEMS";
		final String SELECTED_TIME = "SELECTED_TIME";
		AsynchClientTask task0 = new AsynchClientTask("save user choices", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				hashTable.put(SELECTED_TIME, spatialTimeSlider.getValue());
				if(imageList.getSelectedValue() != null){
					hashTable.put(SELECTED_LIST, imageList);
					hashTable.put(SELECTED_ITEMS, imageList.getSelectedIndices());
				}else if(varJList.getSelectedValue() != null){
					hashTable.put(SELECTED_LIST, varJList);
					hashTable.put(SELECTED_ITEMS, varJList.getSelectedIndices());
				}
			}
		};

		AsynchClientTask task1 = new AsynchClientTask("retrieving data", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {		
				read(pdeDataContext);
			}
		};
		
		AsynchClientTask task2 = new AsynchClientTask("showing data", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {		
				varJList.removeAll();
				if (/*ncfile*/ dpo == null) {
					plotPane.setPlot2D(null);
				} else {
					spatialTimeSlider.setMinimum(0);
					spatialTimeSlider.setMaximum(timeArray.length-1);
					spatialTimeSlider.setValue(0);
					
					minTimeLabel.setText(timeArray[0]+"");
					maxTimeLabel.setText(timeArray[timeArray.length-1]+"");
					
					DefaultListModel<String> imageListModel = new DefaultListModel<String>();
					 HashMap<String, Vector<SourceDataInfo>> imgHashMap = dpo.getDataGenerators();
					 if (imgHashMap != null) {
						Set<String> imgNamesset = imgHashMap.keySet();
						Iterator<String> imgNameIter = imgNamesset.iterator();
						while (imgNameIter.hasNext()) {
							imageListModel.addElement(imgNameIter.next());
						}
						imageList.setModel(imageListModel);
						((DefaultListSelectionModel)imageList.getSelectionModel()).clearSelection();
					}
					 
					 
					 
					//					List<Variable> varList = ncfile.getVariables();
					DefaultListModel<String> dlm = new DefaultListModel<String>();
					for (int i = 0; i < dpo.getVariableStatNames().length; i++) {
						if(dpo.getVariableUnits() != null && dpo.getVariableUnits()[i] != null && dpo.getVariableUnits()[i].length() > 0){
							dlm.addElement(dpo.getVariableStatNames()[i] + "_(" + dpo.getVariableUnits()[i] + ")");
						}else{
							dlm.addElement(dpo.getVariableStatNames()[i]);
						}
					}
					varJList.setModel(dlm);
					
					//set user previous choices if possible
					if(hashTable.get(SELECTED_ITEMS) != null && hashTable.get(SELECTED_LIST) != null){
						((JList<JList<String>>)hashTable.get(SELECTED_LIST)).setSelectedIndices((int[])hashTable.get(SELECTED_ITEMS));
						spatialTimeSlider.setValue((Integer)hashTable.get(SELECTED_TIME));
					}else{
						varJList.setSelectedIndex(0);
					}
				}
			}
		};
		
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task0,task1, task2});
	}
	
	private void onVariablesChange() {
		try {
			List<String> selectedObjects = varJList.getSelectedValuesList();
			int numSelectedVars = selectedObjects.size();
			int totalColumns = 1;
			for (int v = 0; v < numSelectedVars; v ++) {
				totalColumns++;
			}
			int numTimes = timeArray.length;
			double[][] plotDatas = new double[totalColumns][numTimes];
			plotDatas[0] = timeArray;
			String[] plotNames = new String[totalColumns - 1];
			int columnCount = 0;
			for (int v = 0; v < numSelectedVars; v ++) {
				String varName = ((String)selectedObjects.get(v));
				//remove the unit from name if exist
				if(varName.indexOf("_(") > 0) //"_(" doesn't suppose to be the first char
				{
					varName = varName.substring(0, varName.indexOf("_("));
				}
				for (int i = 0; i < /*numColumns*/ 1; i++) {
					String plotName = varName ;
					if (i > 0) {
						plotName += ": region " + (i-1);
					}
					plotNames[columnCount] = plotName;
					for (int j = 0; j < numTimes; j++) {
						plotDatas[columnCount + 1][j] = dpo.getVariableStatValues(varName)[j];//data.get(j, i);
					}
					columnCount ++;
				}
			}
			Plot2D plot2D = new SingleXPlot2D(null, ReservedVariable.TIME.getName(), plotNames, plotDatas, 
					new String[] {"Time Plot", ReservedVariable.TIME.getName(), ""});				
			plotPane.setPlot2D(plot2D);
		} catch (Exception e1) {
			DialogUtils.showErrorDialog(this, e1.getMessage(), e1);
			e1.printStackTrace();
		}
	}
}
