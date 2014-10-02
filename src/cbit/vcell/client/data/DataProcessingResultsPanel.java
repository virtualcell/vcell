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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
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
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.UserCancelException;
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
import javax.swing.border.LineBorder;
import java.awt.Color;

@SuppressWarnings("serial")
public class DataProcessingResultsPanel extends JPanel/* implements PropertyChangeListener*/ {

//	private PDEDataContext pdeDataContext;
//	private NetcdfFile ncfile;
	private JList varJList;
	private PlotPane plotPane = null;
	private double[] timeArray;
	private int[] lastSelectedIdxArray = null;
	private DataProcessingOutput dpo;
	private JScrollPane graphScrollPane;
	private ImagePlaneManagerPanel imagePlaneManagerPanel;
	private JScrollPane imageScrollPane;
	private JList imageList;
	private JSlider spatialTimeSlider;
	private JPanel timePanel;
	private JLabel minTimeLabel;
	private JLabel maxTimeLabel;
	private JPanel cardLayoutPanel;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	
	public DataProcessingResultsPanel() {
		super();
		initialize();
	}
	private void initialize() {		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0, 0.0, 0.0, 0.0, 0.0};
		gridBagLayout.columnWeights = new double[]{0, 0};
		setLayout(gridBagLayout);
		varJList = new JList();
		varJList.setVisibleRowCount(5);
		varJList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				if(varJList.getSelectedIndex() != -1){
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
		gbc_graphScrollPane.weighty = 0.3;
		gbc_graphScrollPane.gridx = 0;
		gbc_graphScrollPane.gridy = 1;
		gbc_graphScrollPane.insets = new Insets(4, 4, 4, 4);
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
		gbc_imageScrollPane.weighty = 0.3;
		gbc_imageScrollPane.insets = new Insets(4, 4, 4, 4);
		gbc_imageScrollPane.fill = GridBagConstraints.BOTH;
		gbc_imageScrollPane.gridx = 0;
		gbc_imageScrollPane.gridy = 3;
		add(imageScrollPane, gbc_imageScrollPane);
		
		imageList = new JList();
		imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		imageList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(!imageList.getValueIsAdjusting()){
					if(imageList.getSelectedIndex() != -1){
						varJList.clearSelection();
						((CardLayout)cardLayoutPanel.getLayout()).show(cardLayoutPanel, "imagePlaneManagerPanel1");
						updateImageDisplay();
					}
				}
			}
		});
		imageList.setVisibleRowCount(5);
		imageScrollPane.setViewportView(imageList);
		
		lblNewLabel_2 = new JLabel("Post Process Image Time");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.insets = new Insets(4, 4, 0, 4);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 4;
		add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		timePanel = new JPanel();
		timePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_timePanel = new GridBagConstraints();
		gbc_timePanel.insets = new Insets(4, 4, 4, 4);
		gbc_timePanel.gridx = 0;
		gbc_timePanel.gridy = 5;
		add(timePanel, gbc_timePanel);
		
		GridBagLayout gbl_timePanel = new GridBagLayout();
		gbl_timePanel.columnWidths = new int[]{150, 0};
		gbl_timePanel.rowHeights = new int[]{241, 0};
		gbl_timePanel.columnWeights = new double[]{0.0, 0.0};
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
		spatialTimeSlider.setInverted(true);
		spatialTimeSlider.setMaximum(10);
		spatialTimeSlider.setMajorTickSpacing(1);
		spatialTimeSlider.setOrientation(SwingConstants.VERTICAL);
		GridBagConstraints gbc_spatialTimeSlider = new GridBagConstraints();
		gbc_spatialTimeSlider.gridheight = 3;
		gbc_spatialTimeSlider.weighty = 1.0;
		gbc_spatialTimeSlider.fill = GridBagConstraints.VERTICAL;
		gbc_spatialTimeSlider.insets = new Insets(0, 0, 5, 5);
		gbc_spatialTimeSlider.gridx = 0;
		gbc_spatialTimeSlider.gridy = 0;
		timePanel.add(spatialTimeSlider, gbc_spatialTimeSlider);
		
		minTimeLabel = new JLabel("New label");
		GridBagConstraints gbc_minTimeLabel = new GridBagConstraints();
		gbc_minTimeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_minTimeLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_minTimeLabel.gridx = 1;
		gbc_minTimeLabel.gridy = 0;
		timePanel.add(minTimeLabel, gbc_minTimeLabel);
		
		maxTimeLabel = new JLabel("New label");
		GridBagConstraints gbc_maxTimeLabel = new GridBagConstraints();
		gbc_maxTimeLabel.anchor = GridBagConstraints.SOUTHWEST;
		gbc_maxTimeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_maxTimeLabel.gridx = 1;
		gbc_maxTimeLabel.gridy = 1;
		timePanel.add(maxTimeLabel, gbc_maxTimeLabel);
	}
	
//	private JSlider getJSliderTime(){
//		return spatialTimeSlider;
//	}
//	private void newTimePoints(double[] newTimes) {
//		//
//		getJSliderTime().setSnapToTicks(true);//So arrow keys work correctly with no minor tick marks
//		//
//		if (newTimes == null || newTimes.length == 1) {
//			getJSliderTime().setMinimum(0);
//			getJSliderTime().setMaximum(0);
//			getJLabelMin().setText((newTimes == null?"":newTimes[0]+""));
//			getJLabelMax().setText((newTimes == null?"":newTimes[0]+""));
//			getJTextField1().setText((newTimes == null?"":newTimes[0]+""));
//			if(getJSliderTime().isEnabled()){
//				BeanUtils.enableComponents(getTimeSliderJPanel(),false);
//			}
//		} else {
//			if(!getJSliderTime().isEnabled()){
//				BeanUtils.enableComponents(getTimeSliderJPanel(),true);
//			}
//			int sValue = getJSliderTime().getValue();
//			getJSliderTime().setMinimum(0);
//			//getJSliderTime().setExtent(1);//Can't do this because of bug in JSlider won't set last value
//			getJSliderTime().setMaximum(newTimes.length - 1);
//			if(sValue >= 0 && sValue < newTimes.length){
//				getJSliderTime().setValue(sValue);
//			}else{
//				getJSliderTime().setValue(0);
//			}
//			getJSliderTime().setMajorTickSpacing((newTimes.length < 10?1:newTimes.length/10));
////			getJSliderTime().setMinorTickSpacing(getJSliderTime().getMajorTickSpacing());//hides minor tick marks
//			getJSliderTime().setMinorTickSpacing(1);// testing....
//			//
//			getJLabelMin().setText(NumberUtils.formatNumber(newTimes[0],8));
//			getJLabelMax().setText(NumberUtils.formatNumber(newTimes[newTimes.length - 1],8));
//		}
//	}

	private void updateImageDisplay(){
		String selectedVarName = (String)imageList.getSelectedValue();
		if(selectedVarName == null){
			imagePlaneManagerPanel.getDisplayAdapterServicePanel().getDisplayAdapterService().setValueDomain(null);
			imagePlaneManagerPanel.setSourceDataInfo(null);
			
		}else{
			Vector<SourceDataInfo> dataV = dpo.getDataGenerators().get(selectedVarName);
			int timeIndex = spatialTimeSlider.getValue();
			SourceDataInfo sdi = dataV.get(timeIndex);
			imagePlaneManagerPanel.getDisplayAdapterServicePanel().getDisplayAdapterService().setValueDomain(sdi.getMinMax());
			imagePlaneManagerPanel.setSourceDataInfo(sdi);
		}
	}
	
//	public void setPdeDataContext(PDEDataContext newValue) {
//		if (this.pdeDataContext == newValue) {
//			return;
//		}
//		PDEDataContext oldValue = pdeDataContext;
//		if (oldValue != null) {
//			oldValue.removePropertyChangeListener(this);
//		}
//		this.pdeDataContext = newValue;	
//		if (newValue != null) {
//			newValue.addPropertyChangeListener(this);
//		}
//		update();
//	}

	private void read(PDEDataContext pdeDataContext) throws Exception {
		
		if(dpo != null && timeArray!= null && timeArray.length == pdeDataContext.getTimePoints().length){
			System.out.println("--------------------"+timeArray.length+"=="+pdeDataContext.getTimePoints().length);
			//we already read this
			throw UserCancelException.CANCEL_GENERIC;
		}
		dpo = null;
		timeArray = null;
		
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
					
					DefaultListModel imageListModel = new DefaultListModel();
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
					DefaultListModel dlm = new DefaultListModel();
					for (int i = 0; i < dpo.getVariableStatNames().length; i++) {
						if(dpo.getVariableUnits() != null && dpo.getVariableUnits()[i] != null && dpo.getVariableUnits()[i].length() > 0){
							dlm.addElement(dpo.getVariableStatNames()[i] + "_(" + dpo.getVariableUnits()[i] + ")");
						}else{
							dlm.addElement(dpo.getVariableStatNames()[i]);
						}
					}
//					for (Variable var : varList) {
//						if (!var.getName().equals(ReservedSymbol.TIME.getName())) {
//							dlm.addElement(var.getName());
//						}
//					}
					int[] lastSelectedIdxArray0 = varJList.getSelectedIndices(); 
					varJList.setModel(dlm);
					lastSelectedIdxArray = lastSelectedIdxArray0;
					if(lastSelectedIdxArray == null || lastSelectedIdxArray.length == 0)
					{
						varJList.setSelectedIndex(0);
					}
					else
					{
						varJList.setSelectedIndices(lastSelectedIdxArray);
					}
				}
			}
		};
		
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});
	}
	
	private void onVariablesChange() {
		try {
			//keep old indices
			lastSelectedIdxArray = varJList.getSelectedIndices();
			Object[] selectedObjects = varJList.getSelectedValues();
			int numSelectedVars = selectedObjects.length;
			int totalColumns = 1;
			for (int v = 0; v < numSelectedVars; v ++) {
				totalColumns++;
//				String varName = (String)selectedObjects[v];
//				ucar.nc2.Variable volVar = ncfile.findVariable(varName);
//				int[] shape = volVar.getShape();
//				int numColumns = shape[1];
//				
//				totalColumns += numColumns;
			}
			int numTimes = timeArray.length;
			double[][] plotDatas = new double[totalColumns][numTimes];
			plotDatas[0] = timeArray;
			String[] plotNames = new String[totalColumns - 1];
			int columnCount = 0;
			for (int v = 0; v < numSelectedVars; v ++) {
				String varName = ((String)selectedObjects[v]);
				//remove the unit from name if exist
				if(varName.indexOf("_(") > 0) //"_(" doesn't suppose to be the first char
				{
					varName = varName.substring(0, varName.indexOf("_("));
				}
//				ucar.nc2.Variable volVar = ncfile.findVariable(varName);
//				int[] shape = volVar.getShape();
//				int numColumns = shape[1];
//				int[] origin = new int[2];
//				ArrayDouble.D2 data = null;
//				try {
//					data = (ArrayDouble.D2) volVar.read(origin, shape);
//				} catch (Exception e) {
//					e.printStackTrace(System.err);
//					throw new IOException("Can not read volVar data.");
//				}
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
//	public void propertyChange(PropertyChangeEvent evt) {
//		if (evt.getSource() == pdeDataContext && evt.getPropertyName().equals(PDEDataContext.PROPERTY_NAME_TIME_POINTS)) {
//			update();
//		}
//		if (evt.getSource() == pdeDataContext && evt.getPropertyName().equals(PDEDataContext.PROPERTY_NAME_VCDATA_IDENTIFIER)) {
//			update();
//		}
//	}
}
