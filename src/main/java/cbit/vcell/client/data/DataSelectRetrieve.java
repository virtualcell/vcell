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

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import org.vcell.util.Range;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.parser.Expression;

/**
 * Insert the type's description here.
 * Creation date: (2/18/2006 2:30:55 PM)
 * @author: Frank Morgan
 */
public class DataSelectRetrieve extends javax.swing.JPanel implements java.awt.event.ActionListener {

	//
	//
	public class DSRState{
		public final int selectionMode;
		public final int selectionType;
		public final int selectAreaRadius;
		public final Range selectAreaRange;
		public final Expression selectAreaAnalytic;
		public final String selectAreaRegionName;
		public final boolean bRetrieveTimeData;
		public final boolean bTimeStats;
		public final boolean bSpatialStats;
		public final boolean bSpatialStatsGroup;

		public DSRState(){
			selectionMode =
				(getJRadioButtonAOIManual().isSelected()?SELECT_MODE_AREA_MANUAL:0) +
				(getJRadioButtonAOIRange().isSelected()?SELECT_MODE_AREA_RANGE:0) +
				(getJRadioButtonAOIRegion().isSelected()?SELECT_MODE_AREA_REGION:0) +
				(getJRadioButtonLine().isSelected()?SELECT_MODE_LINE:0)+
				(getJRadioButtonAOIAnalytic().isSelected()?SELECT_MODE_AREA_ANALYTIC:0);

			selectionType =
				(getJRadioButtonSelectAdd().isSelected()?SELECT_TYPE_ADD:0) +
				(getJRadioButtonSelectRemove().isSelected()?SELECT_TYPE_REMOVE:0);

			selectAreaRadius = getJComboBoxAreaRadius().getSelectedIndex();

			Range rangeTemp = null;
				try{
					rangeTemp = new Range(Double.parseDouble(getJTextFieldAreaMinRange().getText()),Double.parseDouble(getJTextFieldAreaMaxRange().getText()));
				}catch(Throwable e){
					//Do nothing
				}
			selectAreaRange = rangeTemp;

			Expression expTemp = null;
			try{
				expTemp = getAreaAnalyticExpression();
				
			}catch(Throwable e){
				expTemp = null;
			}
			selectAreaAnalytic = expTemp;
			
			selectAreaRegionName = (String)getJComboBoxAreaRegion().getSelectedItem();

			//spatialStats =
				//(getJCheckBoxRetrieveSpaceStatistics().isSelected()?1:SPATIAL_STATISTICS_NONE) *
				//(
					//(getJRadioButtonRetrieveSpaceStatisticsGroupAll().isSelected()?SPATIAL_STATISTICS_GROUPALL:0) +
					//(getJRadioButtonRetrieveSpaceStatisticsGroupIslands().isSelected()?SPATIAL_STATISTICS_ISLANDS:0)
				//);

			bRetrieveTimeData = getJCheckBoxRetrieveOverTime().isSelected();
			bTimeStats = getJCheckBoxRetrieveTimeStatistics().isSelected();

			bSpatialStats = getJCheckBoxRetrieveSpaceStatistics().isSelected();
			bSpatialStatsGroup = getJCheckBoxSpaceStatsMakeGroups().isSelected();
		}
	}
	//
	//
	private Hashtable<Object, ActionEvent> refireEvents = new Hashtable<Object, ActionEvent>();
	//
	//public static final int SPATIAL_STATISTICS_NONE = 0;
	//public static final int SPATIAL_STATISTICS_GROUPALL = 1;
	//public static final int SPATIAL_STATISTICS_ISLANDS = 2;
	//
	public static final int RETRIEVE_DATA = 0;
	public static final int SELECTION_APPLY_AREA_RANGE = 1;
	public static final int SELECTION_CLEAR = 2;
	public static final int SELECTION_APPLY_AREA_REGION = 3;
	public static final int SELECTION_TOAOI_LINE = 4;
	public static final int SELECTION_APPLY_AREA_ANALYTIC = 5;
	//
	public static final int SELECT_MODE_AREA_MANUAL = 0;
	public static final int SELECT_MODE_AREA_RANGE = 1;
	public static final int SELECT_MODE_AREA_REGION = 2;
	public static final int SELECT_MODE_LINE = 3;
	public static final int SELECT_MODE_AREA_ANALYTIC = 4;
	//
	public static final int SELECT_TYPE_ADD = 0;
	public static final int SELECT_TYPE_REMOVE = 1;
	//
	public static final int RETRIEVE_OPTION_OVER_ALL_TIMES = 0;
	public static final int RETRIEVE_OPTION_STATS_TIME = 1;
	public static final int RETRIEVE_OPTION_STATS_SPACE = 2;
	public static final int RETRIEVE_OPTION_ISLANDS = 3;
	private javax.swing.JButton ivjJButtonSelectClear = null;
	private javax.swing.JComboBox ivjJComboBoxAreaRadius = null;
	private javax.swing.JLabel ivjJLabel = null;
	private javax.swing.JLabel ivjJLabel8 = null;
	private javax.swing.JLabel ivjJLabelRange = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel4 = null;
	private javax.swing.JPanel ivjJPanel5 = null;
	private javax.swing.JPanel ivjJPanel6 = null;
	private javax.swing.JRadioButton ivjJRadioButtonAOIManual = null;
	private javax.swing.JRadioButton ivjJRadioButtonAOIRange = null;
	private javax.swing.JRadioButton ivjJRadioButtonLine = null;
	private javax.swing.JRadioButton ivjJRadioButtonSelectAdd = null;
	private javax.swing.JRadioButton ivjJRadioButtonSelectRemove = null;
	private javax.swing.JTextField ivjJTextFieldAreaMaxRange = null;
	private javax.swing.JTextField ivjJTextFieldAreaMinRange = null;
	private javax.swing.ButtonGroup ivjButtonGroupSelectAddRemove = null;
	private javax.swing.ButtonGroup ivjButtonGroupSelectFunc = null;
	protected transient java.awt.event.ActionListener aActionListener = null;
	private javax.swing.JButton ivjJButtonApplySelectAreaRange = null;
	private javax.swing.JButton ivjJButtonRetrieveData = null;
	private javax.swing.JCheckBox ivjJCheckBoxRetrieveSpaceStatistics = null;
	private javax.swing.JCheckBox ivjJCheckBoxRetrieveTimeStatistics = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JPanel ivjJPanel7 = null;
	private javax.swing.JButton ivjJButtonApplySelectAreaRegion = null;
	private javax.swing.JRadioButton ivjJRadioButtonAOIRegion = null;
	private javax.swing.JCheckBox ivjJCheckBoxRetrieveOverTime = null;
	private javax.swing.JPanel ivjJPanel8 = null;
	private javax.swing.JPanel ivjJPanel9 = null;
	private javax.swing.ButtonGroup ivjButtonGroupSpatialStatsGrouping = null;
	private javax.swing.JPanel ivjJPanel10 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox ivjJComboBoxAreaRegion = null;
	private javax.swing.JButton ivjJButtonLineToAOI = null;
	private java.lang.String fieldModeInstruction = new String();
	private javax.swing.JPanel ivjJPanelSelectionMode = null;
	private double fieldSelectMin = 0;
	private double fieldSelectMax = 0;
	private javax.swing.JPanel ivjJPanel11 = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	private javax.swing.JPanel ivjJPanel12 = null;
	private javax.swing.JRadioButton ivjJRadioButtonAOIAnalytic = null;
	private javax.swing.JButton ivjJButtonApplySelectAreaAnalytic = null;
	private javax.swing.JTextField ivjJTextFieldAreaAnalytic = null;
	private javax.swing.JCheckBox ivjJCheckBoxSpaceStatsMakeGroups = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, javax.swing.event.ChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DataSelectRetrieve.this.getJCheckBoxRetrieveOverTime()) 
				connEtoC2(e);
			if (e.getSource() == DataSelectRetrieve.this.getJButtonApplySelectAreaRange()) 
				connEtoC3(e);
			if (e.getSource() == DataSelectRetrieve.this.getJCheckBoxRetrieveSpaceStatistics()) 
				connEtoC4(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == DataSelectRetrieve.this.getJRadioButtonAOIRange()) 
				connEtoM4(e);
			if (e.getSource() == DataSelectRetrieve.this.getJRadioButtonAOIManual()) 
				connEtoM5(e);
			if (e.getSource() == DataSelectRetrieve.this.getJRadioButtonAOIRange()) 
				connEtoM6(e);
			if (e.getSource() == DataSelectRetrieve.this.getJRadioButtonAOIRange()) 
				connEtoM7(e);
			if (e.getSource() == DataSelectRetrieve.this.getJRadioButtonAOIRange()) 
				connEtoM8(e);
			if (e.getSource() == DataSelectRetrieve.this.getJRadioButtonAOIRange()) 
				connEtoM9(e);
			if (e.getSource() == DataSelectRetrieve.this.getJRadioButtonAOIRegion()) 
				connEtoM10(e);
			if (e.getSource() == DataSelectRetrieve.this.getJRadioButtonAOIRegion()) 
				connEtoM11(e);
			if (e.getSource() == DataSelectRetrieve.this.getJRadioButtonLine()) 
				connEtoM12(e);
			if (e.getSource() == DataSelectRetrieve.this.getJRadioButtonAOIAnalytic()) 
				connEtoM13(e);
			if (e.getSource() == DataSelectRetrieve.this.getJRadioButtonAOIAnalytic()) 
				connEtoM14(e);
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == DataSelectRetrieve.this.getJRadioButtonAOIManual()) 
				connEtoM3(e);
		};
	};

/**
 * DataSelectRetrieve constructor comment.
 */
public DataSelectRetrieve() {
	super();
	initialize();
}

/**
 * DataSelectRetrieve constructor comment.
 * @param layout java.awt.LayoutManager
 */
public DataSelectRetrieve(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * DataSelectRetrieve constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public DataSelectRetrieve(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * DataSelectRetrieve constructor comment.
 * @param isDoubleBuffered boolean
 */
public DataSelectRetrieve(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {

	if(e.getSource() == getJButtonApplySelectAreaAnalytic()){
		//Make sure x,y,z are lower case
		String analyticS = getJTextFieldAreaAnalytic().getText();
		if(analyticS != null && analyticS.length() > 0){
			if(analyticS.indexOf("X") != -1 ||
				analyticS.indexOf("Y") != -1 ||
				analyticS.indexOf("Z") != -1){
				PopupGenerator.showErrorDialog(this, "Any use of X,Y,Z in Analytic must be lower case (x,y,z)");
				return;
			}
		}
		DSRState dsrState = getDSRState();
		if(dsrState.selectAreaAnalytic == null){
			try{
				getAreaAnalyticExpression();
			}catch(Throwable exc){
				PopupGenerator.showErrorDialog(this, "Error parsing 'Select Area Analytic' expression\n"+exc.getMessage(), exc);
				return;
			}
		}
	}
	
	if(e.getSource() == getJButtonApplySelectAreaRange()){
		DSRState dsrState = getDSRState();
		if(dsrState.selectAreaRange != null){
			try{
				double min = Double.parseDouble(getJTextFieldAreaMinRange().getText());
				double max = Double.parseDouble(getJTextFieldAreaMaxRange().getText());
				if(min > max){
					getJTextFieldAreaMinRange().setText(dsrState.selectAreaRange.getMin()+"");
					getJTextFieldAreaMaxRange().setText(dsrState.selectAreaRange.getMax()+"");
					PopupGenerator.showInfoDialog(this, "Select Area Range min,max had to be swapped, 'Apply' again");
					return;
				}
			}catch(Exception exc){
				//Do Nothing
			}
		}else{
			PopupGenerator.showErrorDialog(this, "Error parsing 'Select Area Range' min,max");
			return;
		}
	}
	
	if(e.getSource() == getJButtonApplySelectAreaRange() ||
		e.getSource() == getJButtonRetrieveData() ||
		e.getSource() == getJButtonSelectClear() ||
		e.getSource() == getJButtonApplySelectAreaRegion() ||
		e.getSource() == getJButtonLineToAOI() ||
		e.getSource() == getJButtonApplySelectAreaAnalytic()){
			
		fireActionPerformed(refireEvents.get(e.getSource()));
	}


	//String retrievalFormat = "";
	//if(getJCheckBoxRetriveOverTime().isSelected() && !getJCheckBoxRetrieveStatistics().isSelected()){
		//if(getJRadioButtonPoints().isSelected()){
			//retrievalFormat = "(Time Plots) Data values at selected points for all times.";
		//}else if(getJRadioButtonLine().isSelected()){
			//retrievalFormat = "(Kymograph) Data values along selected line for all times.";
		//}else if (getJRadioButtonAOIManual().isSelected() || getJRadioButtonAOIRange().isSelected()){
			//retrievalFormat = "Error.";
		//}
	//}else if (getJCheckBoxRetrieveStatistics().isSelected() && !getJCheckBoxRetriveOverTime().isSelected()){
		//if(getJRadioButtonPoints().isSelected()){
			//retrievalFormat = "(Text) Summary statistics for all selected points (current time only).";
		//}else if(getJRadioButtonLine().isSelected()){
			//retrievalFormat = "(Text) Summary statistics for all data values along selected line (current time only).";
		//}else if (getJRadioButtonAOIManual().isSelected() || getJRadioButtonAOIRange().isSelected()){
			//retrievalFormat = "(Text) Summary statistics for all data values in selected area (current time only).";
		//}
	//}else if (getJCheckBoxRetrieveStatistics().isSelected() && getJCheckBoxRetriveOverTime().isSelected()){
		//if(getJRadioButtonPoints().isSelected()){
			//retrievalFormat = "(Text) Summary statistics(over time) for each selected POINT.";
		//}else if(getJRadioButtonLine().isSelected()){
			//retrievalFormat = "(Time Plots) Statistics for selected LINE over all times.";
		//}else if (getJRadioButtonAOIManual().isSelected() || getJRadioButtonAOIRange().isSelected()){
			//retrievalFormat = "(Time Plots) Statistics for selected AREA over all times.";
		//}
	//}else{
		//if(getJRadioButtonPoints().isSelected()){
			//retrievalFormat = "(Text Summary) Data values at selected points for the current time only.";
		//}else if(getJRadioButtonLine().isSelected()){
			//retrievalFormat = "(Line Plot) Data values along selected line for the current time only.";
		//}else if (getJRadioButtonAOIManual().isSelected() || getJRadioButtonAOIRange().isSelected()){
			//retrievalFormat = "Error.";
		//}
	//}

	//getJLabelRetrievalFormat().setText(retrievalFormat);
}


public void addActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.add(aActionListener, newListener);
	return;
}


/**
 * connEtoC1:  (DataSelectRetrieve.initialize() --> DataSelectRetrieve.dataSelectRetrieve_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.dataSelectRetrieve_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JCheckBoxRetrieveOverTime.action.actionPerformed(java.awt.event.ActionEvent) --> DataSelectRetrieve.jCheckBoxRetrieveOverTime_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxRetrieveOverTime_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JButtonApplySelectAreaRange.action.actionPerformed(java.awt.event.ActionEvent) --> DataSelectRetrieve.jButtonApplySelectAreaRange_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonApplySelectAreaRange_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JCheckBoxRetrieveSpaceStatistics.action.actionPerformed(java.awt.event.ActionEvent) --> DataSelectRetrieve.jCheckBoxRetrieveSpaceStatistics_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxRetrieveSpaceStatistics_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM10:  (JRadioButtonAOIRegion.item.itemStateChanged(java.awt.event.ItemEvent) --> JButtonApplySelectAreaRegion.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJButtonApplySelectAreaRegion().setEnabled(getJRadioButtonAOIRegion().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM11:  (JRadioButtonAOIRegion.item.itemStateChanged(java.awt.event.ItemEvent) --> JComboBoxAreaRegion.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJComboBoxAreaRegion().setEnabled(getJRadioButtonAOIRegion().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM12:  (JRadioButtonLine.item.itemStateChanged(java.awt.event.ItemEvent) --> JButtonLineStartNew.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJButtonLineToAOI().setEnabled(getJRadioButtonLine().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM13:  (JRadioButtonAOIAnalytic.item.itemStateChanged(java.awt.event.ItemEvent) --> JTextField1.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM13(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJTextFieldAreaAnalytic().setEnabled(getJRadioButtonAOIAnalytic().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM14:  (JRadioButtonAOIAnalytic.item.itemStateChanged(java.awt.event.ItemEvent) --> JButtonApplySelectAreaAnalytic.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM14(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJButtonApplySelectAreaAnalytic().setEnabled(getJRadioButtonAOIAnalytic().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (JRadioButtonAOIManual.change.stateChanged(javax.swing.event.ChangeEvent) --> JComboBoxAreaRadius.enabled)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJComboBoxAreaRadius().setEnabled(getJRadioButtonAOIManual().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (JRadioButtonAOIRange.item.itemStateChanged(java.awt.event.ItemEvent) --> JTextFieldAreaMinRange.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJTextFieldAreaMinRange().setEnabled(getJRadioButtonAOIRange().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (JRadioButtonAOIManual.item.itemStateChanged(java.awt.event.ItemEvent) --> JLabelRange.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJLabelRange().setEnabled(getJRadioButtonAOIManual().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM6:  (JRadioButtonAOIRange.item.itemStateChanged(java.awt.event.ItemEvent) --> JButtonApplySelectAreaRange.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJButtonApplySelectAreaRange().setEnabled(getJRadioButtonAOIRange().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM7:  (JRadioButtonAOIRange.item.itemStateChanged(java.awt.event.ItemEvent) --> JTextFieldAreaMaxRange.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJTextFieldAreaMaxRange().setEnabled(getJRadioButtonAOIRange().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM8:  (JRadioButtonAOIRange.item.itemStateChanged(java.awt.event.ItemEvent) --> JLabel8.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJLabel8().setEnabled(getJRadioButtonAOIRange().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM9:  (JRadioButtonAOIRange.item.itemStateChanged(java.awt.event.ItemEvent) --> JLabel.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJLabel().setEnabled(getJRadioButtonAOIRange().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private void dataSelectRetrieve_Initialize() {

	//Temporarily turn off some features
	getJPanel10().setVisible(false);
	getJPanel1().setVisible(false);
	//
	
	refireEvents.put(getJButtonRetrieveData(),new java.awt.event.ActionEvent(this,RETRIEVE_DATA,"RETRIEVE_DATA"));
	refireEvents.put(getJButtonApplySelectAreaRange(),new java.awt.event.ActionEvent(this,SELECTION_APPLY_AREA_RANGE,"APPLY_SELECT_AREA_RANGE"));
	refireEvents.put(getJButtonApplySelectAreaRegion(),new java.awt.event.ActionEvent(this,SELECTION_APPLY_AREA_REGION,"SELECTION_APPLY_AREA_REGION"));
	refireEvents.put(getJButtonApplySelectAreaAnalytic(),new java.awt.event.ActionEvent(this,SELECTION_APPLY_AREA_ANALYTIC,"SELECTION_APPLY_AREA_ANALYTIC"));
	refireEvents.put(getJButtonSelectClear(),new java.awt.event.ActionEvent(this,SELECTION_CLEAR,"SELECTION_CLEAR"));
	refireEvents.put(getJButtonLineToAOI(),new java.awt.event.ActionEvent(this,SELECTION_TOAOI_LINE,"SELECTION_TOAOI_LINE"));
	
	getJButtonApplySelectAreaRange().addActionListener(this);
	getJButtonApplySelectAreaRegion().addActionListener(this);
	getJButtonRetrieveData().addActionListener(this);
	getJButtonSelectClear().addActionListener(this);
	getJButtonLineToAOI().addActionListener(this);
	getJButtonApplySelectAreaAnalytic().addActionListener(this);
	
	//getJRadioButtonAOIManual().addActionListener(this);
	//getJRadioButtonAOIRange().addActionListener(this);
	//getJRadioButtonAOIRegion().addActionListener(this);
	//getJRadioButtonLine().addActionListener(this);
	//getJRadioButtonAOIAnalytic().addActionListener(this);
	
	//getJCheckBoxRetrieveOverTime().addActionListener(this);
	//getJCheckBoxRetrieveTimeStatistics().addActionListener(this);
	//getJCheckBoxRetrieveSpaceStatistics().addActionListener(this);
	
	for(int i=0;i<26;i+= 1){
		getJComboBoxAreaRadius().addItem(""+i);
	}
	
	getButtonGroupSelectAddRemove().add(getJRadioButtonSelectAdd());
	getButtonGroupSelectAddRemove().add(getJRadioButtonSelectRemove());

	getButtonGroupSelectFunc().add(getJRadioButtonAOIManual());
	getButtonGroupSelectFunc().add(getJRadioButtonAOIRange());
	getButtonGroupSelectFunc().add(getJRadioButtonAOIRegion());
	getButtonGroupSelectFunc().add(getJRadioButtonLine());
	getButtonGroupSelectFunc().add(getJRadioButtonAOIAnalytic());
}


/**
 * Method to support listener events.
 */
protected void fireActionPerformed(java.awt.event.ActionEvent e) {
	if (aActionListener == null) {
		return;
	};
	aActionListener.actionPerformed(e);
}


/**
 * Insert the method's description here.
 * Creation date: (2/22/2006 6:20:43 PM)
 */
public cbit.vcell.parser.Expression getAreaAnalyticExpression() throws cbit.vcell.parser.ExpressionException{
	
	cbit.vcell.parser.Expression expTemp = new cbit.vcell.parser.Expression(getJTextFieldAreaAnalytic().getText());
	String symbols[] =
		new String[]{
			cbit.vcell.math.ReservedVariable.X.getName(),
			cbit.vcell.math.ReservedVariable.Y.getName(),
			cbit.vcell.math.ReservedVariable.Z.getName()
		};
	expTemp.bindExpression(new cbit.vcell.parser.SimpleSymbolTable(symbols));
	
	return expTemp;
}


/**
 * Return the ButtonGroupSelectAddRemove property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroupSelectAddRemove() {
	if (ivjButtonGroupSelectAddRemove == null) {
		try {
			ivjButtonGroupSelectAddRemove = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupSelectAddRemove;
}


/**
 * Return the ButtonGroupSelectFunc property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroupSelectFunc() {
	if (ivjButtonGroupSelectFunc == null) {
		try {
			ivjButtonGroupSelectFunc = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupSelectFunc;
}


/**
 * Return the ButtonGroupSpatialStatsGrouping property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroupSpatialStatsGrouping() {
	if (ivjButtonGroupSpatialStatsGrouping == null) {
		try {
			ivjButtonGroupSpatialStatsGrouping = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupSpatialStatsGrouping;
}


/**
 * Insert the method's description here.
 * Creation date: (2/19/2006 1:06:37 PM)
 */
public DSRState getDSRState() {

	return new DSRState();
	
}


/**
 * Return the JButtonApplySelectAreaAnalytic property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonApplySelectAreaAnalytic() {
	if (ivjJButtonApplySelectAreaAnalytic == null) {
		try {
			ivjJButtonApplySelectAreaAnalytic = new javax.swing.JButton();
			ivjJButtonApplySelectAreaAnalytic.setName("JButtonApplySelectAreaAnalytic");
			ivjJButtonApplySelectAreaAnalytic.setText("Apply");
			ivjJButtonApplySelectAreaAnalytic.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjJButtonApplySelectAreaAnalytic.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonApplySelectAreaAnalytic;
}

/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonApplySelectAreaRange() {
	if (ivjJButtonApplySelectAreaRange == null) {
		try {
			ivjJButtonApplySelectAreaRange = new javax.swing.JButton();
			ivjJButtonApplySelectAreaRange.setName("JButtonApplySelectAreaRange");
			ivjJButtonApplySelectAreaRange.setText("Apply");
			ivjJButtonApplySelectAreaRange.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjJButtonApplySelectAreaRange.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonApplySelectAreaRange;
}

/**
 * Return the JButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonApplySelectAreaRegion() {
	if (ivjJButtonApplySelectAreaRegion == null) {
		try {
			ivjJButtonApplySelectAreaRegion = new javax.swing.JButton();
			ivjJButtonApplySelectAreaRegion.setName("JButtonApplySelectAreaRegion");
			ivjJButtonApplySelectAreaRegion.setText("Apply");
			ivjJButtonApplySelectAreaRegion.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjJButtonApplySelectAreaRegion.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonApplySelectAreaRegion;
}

/**
 * Return the JButtonLineStartNew property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonLineToAOI() {
	if (ivjJButtonLineToAOI == null) {
		try {
			ivjJButtonLineToAOI = new javax.swing.JButton();
			ivjJButtonLineToAOI.setName("JButtonLineToAOI");
			ivjJButtonLineToAOI.setText("To AOI");
			ivjJButtonLineToAOI.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjJButtonLineToAOI.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonLineToAOI;
}

/**
 * Return the JButtonRetrieve property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRetrieveData() {
	if (ivjJButtonRetrieveData == null) {
		try {
			ivjJButtonRetrieveData = new javax.swing.JButton();
			ivjJButtonRetrieveData.setName("JButtonRetrieveData");
			ivjJButtonRetrieveData.setText("Retrieve Data");
			ivjJButtonRetrieveData.setMargin(new java.awt.Insets(2, 2, 2, 2));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRetrieveData;
}

/**
 * Return the JButtonSelectClear property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonSelectClear() {
	if (ivjJButtonSelectClear == null) {
		try {
			ivjJButtonSelectClear = new javax.swing.JButton();
			ivjJButtonSelectClear.setName("JButtonSelectClear");
			ivjJButtonSelectClear.setText("Clear AOI");
			ivjJButtonSelectClear.setMargin(new java.awt.Insets(2, 2, 2, 2));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonSelectClear;
}

/**
 * Return the JCheckBoxRetriveOverTime property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRetrieveOverTime() {
	if (ivjJCheckBoxRetrieveOverTime == null) {
		try {
			ivjJCheckBoxRetrieveOverTime = new javax.swing.JCheckBox();
			ivjJCheckBoxRetrieveOverTime.setName("JCheckBoxRetrieveOverTime");
			ivjJCheckBoxRetrieveOverTime.setText("Data For All Times");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRetrieveOverTime;
}

/**
 * Return the JCheckBoxRetrieveStatistics property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRetrieveSpaceStatistics() {
	if (ivjJCheckBoxRetrieveSpaceStatistics == null) {
		try {
			ivjJCheckBoxRetrieveSpaceStatistics = new javax.swing.JCheckBox();
			ivjJCheckBoxRetrieveSpaceStatistics.setName("JCheckBoxRetrieveSpaceStatistics");
			ivjJCheckBoxRetrieveSpaceStatistics.setText("Calculate Spatial Statistics");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRetrieveSpaceStatistics;
}

/**
 * Return the JCheckBoxRetrieveTimeStatistics property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRetrieveTimeStatistics() {
	if (ivjJCheckBoxRetrieveTimeStatistics == null) {
		try {
			ivjJCheckBoxRetrieveTimeStatistics = new javax.swing.JCheckBox();
			ivjJCheckBoxRetrieveTimeStatistics.setName("JCheckBoxRetrieveTimeStatistics");
			ivjJCheckBoxRetrieveTimeStatistics.setText("Calculate Time Statistics");
			ivjJCheckBoxRetrieveTimeStatistics.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRetrieveTimeStatistics;
}

/**
 * Return the JCheckBoxSpaceStatsMakeGroups property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSpaceStatsMakeGroups() {
	if (ivjJCheckBoxSpaceStatsMakeGroups == null) {
		try {
			ivjJCheckBoxSpaceStatsMakeGroups = new javax.swing.JCheckBox();
			ivjJCheckBoxSpaceStatsMakeGroups.setName("JCheckBoxSpaceStatsMakeGroups");
			ivjJCheckBoxSpaceStatsMakeGroups.setText("Separate Groups");
			ivjJCheckBoxSpaceStatsMakeGroups.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSpaceStatsMakeGroups;
}


/**
 * Return the JComboBoxAreaRadius property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxAreaRadius() {
	if (ivjJComboBoxAreaRadius == null) {
		try {
			ivjJComboBoxAreaRadius = new javax.swing.JComboBox();
			ivjJComboBoxAreaRadius.setName("JComboBoxAreaRadius");
			ivjJComboBoxAreaRadius.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxAreaRadius;
}

/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxAreaRegion() {
	if (ivjJComboBoxAreaRegion == null) {
		try {
			ivjJComboBoxAreaRegion = new javax.swing.JComboBox();
			ivjJComboBoxAreaRegion.setName("JComboBoxAreaRegion");
			ivjJComboBoxAreaRegion.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxAreaRegion;
}

/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel() {
	if (ivjJLabel == null) {
		try {
			ivjJLabel = new javax.swing.JLabel();
			ivjJLabel.setName("JLabel");
			ivjJLabel.setText("Max");
			ivjJLabel.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel;
}

/**
 * Return the JLabel8 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel8() {
	if (ivjJLabel8 == null) {
		try {
			ivjJLabel8 = new javax.swing.JLabel();
			ivjJLabel8.setName("JLabel8");
			ivjJLabel8.setText("Min");
			ivjJLabel8.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel8;
}

/**
 * Return the JLabelRange property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRange() {
	if (ivjJLabelRange == null) {
		try {
			ivjJLabelRange = new javax.swing.JLabel();
			ivjJLabelRange.setName("JLabelRange");
			ivjJLabelRange.setText("Radius");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRange;
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder2;
			ivjLocalBorder2 = new org.vcell.util.gui.TitledBorderBean();
			ivjLocalBorder2.setTitle("Data Retrieval Specification");
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setBorder(ivjLocalBorder2);
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButtonRetrieveData = new java.awt.GridBagConstraints();
			constraintsJButtonRetrieveData.gridx = 2; constraintsJButtonRetrieveData.gridy = 0;
			constraintsJButtonRetrieveData.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonRetrieveData.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJButtonRetrieveData(), constraintsJButtonRetrieveData);

			java.awt.GridBagConstraints constraintsJPanel9 = new java.awt.GridBagConstraints();
			constraintsJPanel9.gridx = 0; constraintsJPanel9.gridy = 0;
			constraintsJPanel9.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJPanel9(), constraintsJPanel9);

			java.awt.GridBagConstraints constraintsJPanel8 = new java.awt.GridBagConstraints();
			constraintsJPanel8.gridx = 1; constraintsJPanel8.gridy = 0;
			constraintsJPanel8.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJPanel8(), constraintsJPanel8);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JPanel10 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel10() {
	if (ivjJPanel10 == null) {
		try {
			ivjJPanel10 = new javax.swing.JPanel();
			ivjJPanel10.setName("JPanel10");
			ivjJPanel10.setBorder(new org.vcell.util.gui.LineBorderBean());
			ivjJPanel10.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJRadioButtonLine = new java.awt.GridBagConstraints();
			constraintsJRadioButtonLine.gridx = 0; constraintsJRadioButtonLine.gridy = 0;
			constraintsJRadioButtonLine.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel10().add(getJRadioButtonLine(), constraintsJRadioButtonLine);

			java.awt.GridBagConstraints constraintsJButtonLineToAOI = new java.awt.GridBagConstraints();
			constraintsJButtonLineToAOI.gridx = 0; constraintsJButtonLineToAOI.gridy = 1;
			constraintsJButtonLineToAOI.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonLineToAOI.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel10().add(getJButtonLineToAOI(), constraintsJButtonLineToAOI);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel10;
}

/**
 * Return the JPanel11 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel11() {
	if (ivjJPanel11 == null) {
		try {
			ivjJPanel11 = new javax.swing.JPanel();
			ivjJPanel11.setName("JPanel11");
			ivjJPanel11.setBorder(new org.vcell.util.gui.LineBorderBean());
			ivjJPanel11.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
			constraintsJPanel3.gridx = 0; constraintsJPanel3.gridy = 0;
			constraintsJPanel3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanel3.weightx = 1.0;
			constraintsJPanel3.weighty = 1.0;
			constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel11().add(getJPanel3(), constraintsJPanel3);

			java.awt.GridBagConstraints constraintsJPanel6 = new java.awt.GridBagConstraints();
			constraintsJPanel6.gridx = 0; constraintsJPanel6.gridy = 1;
			constraintsJPanel6.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel6.weightx = 1.0;
			constraintsJPanel6.weighty = 1.0;
			constraintsJPanel6.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel11().add(getJPanel6(), constraintsJPanel6);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel11;
}

/**
 * Return the JPanel12 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel12() {
	if (ivjJPanel12 == null) {
		try {
			ivjJPanel12 = new javax.swing.JPanel();
			ivjJPanel12.setName("JPanel12");
			ivjJPanel12.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJRadioButtonAOIAnalytic = new java.awt.GridBagConstraints();
			constraintsJRadioButtonAOIAnalytic.gridx = 0; constraintsJRadioButtonAOIAnalytic.gridy = 0;
			constraintsJRadioButtonAOIAnalytic.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJRadioButtonAOIAnalytic.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel12().add(getJRadioButtonAOIAnalytic(), constraintsJRadioButtonAOIAnalytic);

			java.awt.GridBagConstraints constraintsJTextFieldAreaAnalytic = new java.awt.GridBagConstraints();
			constraintsJTextFieldAreaAnalytic.gridx = 0; constraintsJTextFieldAreaAnalytic.gridy = 1;
			constraintsJTextFieldAreaAnalytic.gridwidth = 2;
			constraintsJTextFieldAreaAnalytic.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldAreaAnalytic.weightx = 1.0;
			constraintsJTextFieldAreaAnalytic.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel12().add(getJTextFieldAreaAnalytic(), constraintsJTextFieldAreaAnalytic);

			java.awt.GridBagConstraints constraintsJButtonApplySelectAreaAnalytic = new java.awt.GridBagConstraints();
			constraintsJButtonApplySelectAreaAnalytic.gridx = 1; constraintsJButtonApplySelectAreaAnalytic.gridy = 0;
			constraintsJButtonApplySelectAreaAnalytic.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonApplySelectAreaAnalytic.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel12().add(getJButtonApplySelectAreaAnalytic(), constraintsJButtonApplySelectAreaAnalytic);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel12;
}

/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setBorder(new org.vcell.util.gui.LineBorderBean());
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJRadioButtonSelectAdd = new java.awt.GridBagConstraints();
			constraintsJRadioButtonSelectAdd.gridx = 0; constraintsJRadioButtonSelectAdd.gridy = 0;
			constraintsJRadioButtonSelectAdd.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJRadioButtonSelectAdd.insets = new java.awt.Insets(1, 4, 1, 4);
			getJPanel2().add(getJRadioButtonSelectAdd(), constraintsJRadioButtonSelectAdd);

			java.awt.GridBagConstraints constraintsJRadioButtonSelectRemove = new java.awt.GridBagConstraints();
			constraintsJRadioButtonSelectRemove.gridx = 0; constraintsJRadioButtonSelectRemove.gridy = 1;
			constraintsJRadioButtonSelectRemove.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJRadioButtonSelectRemove.insets = new java.awt.Insets(1, 4, 1, 4);
			getJPanel2().add(getJRadioButtonSelectRemove(), constraintsJRadioButtonSelectRemove);

			java.awt.GridBagConstraints constraintsJButtonSelectClear = new java.awt.GridBagConstraints();
			constraintsJButtonSelectClear.gridx = 0; constraintsJButtonSelectClear.gridy = 2;
			constraintsJButtonSelectClear.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonSelectClear.insets = new java.awt.Insets(1, 4, 1, 4);
			getJPanel2().add(getJButtonSelectClear(), constraintsJButtonSelectClear);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}

/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJRadioButtonAOIRange = new java.awt.GridBagConstraints();
			constraintsJRadioButtonAOIRange.gridx = 0; constraintsJRadioButtonAOIRange.gridy = 0;
			constraintsJRadioButtonAOIRange.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJRadioButtonAOIRange(), constraintsJRadioButtonAOIRange);

			java.awt.GridBagConstraints constraintsJButtonApplySelectAreaRange = new java.awt.GridBagConstraints();
			constraintsJButtonApplySelectAreaRange.gridx = 1; constraintsJButtonApplySelectAreaRange.gridy = 0;
			constraintsJButtonApplySelectAreaRange.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJButtonApplySelectAreaRange(), constraintsJButtonApplySelectAreaRange);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}


/**
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel4() {
	if (ivjJPanel4 == null) {
		try {
			org.vcell.util.gui.LineBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new org.vcell.util.gui.LineBorderBean();
			ivjLocalBorder1.setThickness(2);
			ivjJPanel4 = new javax.swing.JPanel();
			ivjJPanel4.setName("JPanel4");
			ivjJPanel4.setBorder(ivjLocalBorder1);
			ivjJPanel4.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanel5 = new java.awt.GridBagConstraints();
			constraintsJPanel5.gridx = 0; constraintsJPanel5.gridy = 0;
			constraintsJPanel5.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel5.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel4().add(getJPanel5(), constraintsJPanel5);

			java.awt.GridBagConstraints constraintsJPanel7 = new java.awt.GridBagConstraints();
			constraintsJPanel7.gridx = 2; constraintsJPanel7.gridy = 0;
			constraintsJPanel7.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel7.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel4().add(getJPanel7(), constraintsJPanel7);

			java.awt.GridBagConstraints constraintsJPanel10 = new java.awt.GridBagConstraints();
			constraintsJPanel10.gridx = 3; constraintsJPanel10.gridy = 0;
			constraintsJPanel10.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel10.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel4().add(getJPanel10(), constraintsJPanel10);

			java.awt.GridBagConstraints constraintsJPanel11 = new java.awt.GridBagConstraints();
			constraintsJPanel11.gridx = 1; constraintsJPanel11.gridy = 0;
			constraintsJPanel11.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel11.weightx = 1.0;
			constraintsJPanel11.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel4().add(getJPanel11(), constraintsJPanel11);

			java.awt.GridBagConstraints constraintsJPanel12 = new java.awt.GridBagConstraints();
			constraintsJPanel12.gridx = 0; constraintsJPanel12.gridy = 1;
			constraintsJPanel12.gridwidth = 4;
			constraintsJPanel12.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel12.weightx = 1.0;
			constraintsJPanel12.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel4().add(getJPanel12(), constraintsJPanel12);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel4;
}

/**
 * Return the JPanel5 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel5() {
	if (ivjJPanel5 == null) {
		try {
			ivjJPanel5 = new javax.swing.JPanel();
			ivjJPanel5.setName("JPanel5");
			ivjJPanel5.setBorder(new org.vcell.util.gui.LineBorderBean());
			ivjJPanel5.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJRadioButtonAOIManual = new java.awt.GridBagConstraints();
			constraintsJRadioButtonAOIManual.gridx = 0; constraintsJRadioButtonAOIManual.gridy = 0;
			constraintsJRadioButtonAOIManual.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel5().add(getJRadioButtonAOIManual(), constraintsJRadioButtonAOIManual);

			java.awt.GridBagConstraints constraintsJComboBoxAreaRadius = new java.awt.GridBagConstraints();
			constraintsJComboBoxAreaRadius.gridx = 0; constraintsJComboBoxAreaRadius.gridy = 1;
			constraintsJComboBoxAreaRadius.gridwidth = 2;
			constraintsJComboBoxAreaRadius.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxAreaRadius.weightx = 1.0;
			constraintsJComboBoxAreaRadius.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel5().add(getJComboBoxAreaRadius(), constraintsJComboBoxAreaRadius);

			java.awt.GridBagConstraints constraintsJLabelRange = new java.awt.GridBagConstraints();
			constraintsJLabelRange.gridx = 1; constraintsJLabelRange.gridy = 0;
			constraintsJLabelRange.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel5().add(getJLabelRange(), constraintsJLabelRange);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel5;
}

/**
 * Return the JPanel6 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel6() {
	if (ivjJPanel6 == null) {
		try {
			ivjJPanel6 = new javax.swing.JPanel();
			ivjJPanel6.setName("JPanel6");
			ivjJPanel6.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJTextFieldAreaMinRange = new java.awt.GridBagConstraints();
			constraintsJTextFieldAreaMinRange.gridx = 1; constraintsJTextFieldAreaMinRange.gridy = 0;
			constraintsJTextFieldAreaMinRange.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldAreaMinRange.weightx = 1.0;
			constraintsJTextFieldAreaMinRange.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel6().add(getJTextFieldAreaMinRange(), constraintsJTextFieldAreaMinRange);

			java.awt.GridBagConstraints constraintsJLabel8 = new java.awt.GridBagConstraints();
			constraintsJLabel8.gridx = 0; constraintsJLabel8.gridy = 0;
			constraintsJLabel8.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel6().add(getJLabel8(), constraintsJLabel8);

			java.awt.GridBagConstraints constraintsJLabel = new java.awt.GridBagConstraints();
			constraintsJLabel.gridx = 2; constraintsJLabel.gridy = 0;
			constraintsJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel6().add(getJLabel(), constraintsJLabel);

			java.awt.GridBagConstraints constraintsJTextFieldAreaMaxRange = new java.awt.GridBagConstraints();
			constraintsJTextFieldAreaMaxRange.gridx = 3; constraintsJTextFieldAreaMaxRange.gridy = 0;
			constraintsJTextFieldAreaMaxRange.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldAreaMaxRange.weightx = 1.0;
			constraintsJTextFieldAreaMaxRange.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel6().add(getJTextFieldAreaMaxRange(), constraintsJTextFieldAreaMaxRange);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel6;
}

/**
 * Return the JPanel7 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel7() {
	if (ivjJPanel7 == null) {
		try {
			ivjJPanel7 = new javax.swing.JPanel();
			ivjJPanel7.setName("JPanel7");
			ivjJPanel7.setBorder(new org.vcell.util.gui.LineBorderBean());
			ivjJPanel7.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJRadioButtonAOIRegion = new java.awt.GridBagConstraints();
			constraintsJRadioButtonAOIRegion.gridx = 0; constraintsJRadioButtonAOIRegion.gridy = 0;
			constraintsJRadioButtonAOIRegion.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel7().add(getJRadioButtonAOIRegion(), constraintsJRadioButtonAOIRegion);

			java.awt.GridBagConstraints constraintsJComboBoxAreaRegion = new java.awt.GridBagConstraints();
			constraintsJComboBoxAreaRegion.gridx = 0; constraintsJComboBoxAreaRegion.gridy = 1;
			constraintsJComboBoxAreaRegion.gridwidth = 2;
			constraintsJComboBoxAreaRegion.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxAreaRegion.weightx = 1.0;
			constraintsJComboBoxAreaRegion.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel7().add(getJComboBoxAreaRegion(), constraintsJComboBoxAreaRegion);

			java.awt.GridBagConstraints constraintsJButtonApplySelectAreaRegion = new java.awt.GridBagConstraints();
			constraintsJButtonApplySelectAreaRegion.gridx = 1; constraintsJButtonApplySelectAreaRegion.gridy = 0;
			constraintsJButtonApplySelectAreaRegion.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel7().add(getJButtonApplySelectAreaRegion(), constraintsJButtonApplySelectAreaRegion);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel7;
}

/**
 * Return the JPanel8 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel8() {
	if (ivjJPanel8 == null) {
		try {
			ivjJPanel8 = new javax.swing.JPanel();
			ivjJPanel8.setName("JPanel8");
			ivjJPanel8.setBorder(new org.vcell.util.gui.LineBorderBean());
			ivjJPanel8.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxRetrieveSpaceStatistics = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRetrieveSpaceStatistics.gridx = 0; constraintsJCheckBoxRetrieveSpaceStatistics.gridy = 0;
			constraintsJCheckBoxRetrieveSpaceStatistics.gridwidth = 2;
			constraintsJCheckBoxRetrieveSpaceStatistics.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel8().add(getJCheckBoxRetrieveSpaceStatistics(), constraintsJCheckBoxRetrieveSpaceStatistics);

			java.awt.GridBagConstraints constraintsJCheckBoxSpaceStatsMakeGroups = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSpaceStatsMakeGroups.gridx = 1; constraintsJCheckBoxSpaceStatsMakeGroups.gridy = 1;
			constraintsJCheckBoxSpaceStatsMakeGroups.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxSpaceStatsMakeGroups.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel8().add(getJCheckBoxSpaceStatsMakeGroups(), constraintsJCheckBoxSpaceStatsMakeGroups);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel8;
}

/**
 * Return the JPanel9 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel9() {
	if (ivjJPanel9 == null) {
		try {
			ivjJPanel9 = new javax.swing.JPanel();
			ivjJPanel9.setName("JPanel9");
			ivjJPanel9.setBorder(new org.vcell.util.gui.LineBorderBean());
			ivjJPanel9.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxRetrieveOverTime = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRetrieveOverTime.gridx = 0; constraintsJCheckBoxRetrieveOverTime.gridy = 0;
			constraintsJCheckBoxRetrieveOverTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxRetrieveOverTime.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel9().add(getJCheckBoxRetrieveOverTime(), constraintsJCheckBoxRetrieveOverTime);

			java.awt.GridBagConstraints constraintsJCheckBoxRetrieveTimeStatistics = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRetrieveTimeStatistics.gridx = 0; constraintsJCheckBoxRetrieveTimeStatistics.gridy = 1;
			constraintsJCheckBoxRetrieveTimeStatistics.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxRetrieveTimeStatistics.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel9().add(getJCheckBoxRetrieveTimeStatistics(), constraintsJCheckBoxRetrieveTimeStatistics);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel9;
}

/**
 * Return the JPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelSelectionMode() {
	if (ivjJPanelSelectionMode == null) {
		try {
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new org.vcell.util.gui.TitledBorderBean();
			ivjLocalBorder.setTitle("Data Selection Mode ");
			ivjJPanelSelectionMode = new javax.swing.JPanel();
			ivjJPanelSelectionMode.setName("JPanelSelectionMode");
			ivjJPanelSelectionMode.setBorder(ivjLocalBorder);
			ivjJPanelSelectionMode.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanel4 = new java.awt.GridBagConstraints();
			constraintsJPanel4.gridx = 0; constraintsJPanel4.gridy = 0;
			constraintsJPanel4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel4.weightx = 1.0;
			constraintsJPanel4.weighty = 1.0;
			constraintsJPanel4.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSelectionMode().add(getJPanel4(), constraintsJPanel4);

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 1; constraintsJPanel2.gridy = 0;
			constraintsJPanel2.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSelectionMode().add(getJPanel2(), constraintsJPanel2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelSelectionMode;
}

/**
 * Return the JRadioButtonAOIAnalytic property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonAOIAnalytic() {
	if (ivjJRadioButtonAOIAnalytic == null) {
		try {
			ivjJRadioButtonAOIAnalytic = new javax.swing.JRadioButton();
			ivjJRadioButtonAOIAnalytic.setName("JRadioButtonAOIAnalytic");
			ivjJRadioButtonAOIAnalytic.setText("Area (analytic function of x,y,z)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonAOIAnalytic;
}

/**
 * Return the JRadioButtonAOIManual property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonAOIManual() {
	if (ivjJRadioButtonAOIManual == null) {
		try {
			ivjJRadioButtonAOIManual = new javax.swing.JRadioButton();
			ivjJRadioButtonAOIManual.setName("JRadioButtonAOIManual");
			ivjJRadioButtonAOIManual.setSelected(true);
			ivjJRadioButtonAOIManual.setText("AREA (manual)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonAOIManual;
}

/**
 * Return the JRadioButtonAOIRange property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonAOIRange() {
	if (ivjJRadioButtonAOIRange == null) {
		try {
			ivjJRadioButtonAOIRange = new javax.swing.JRadioButton();
			ivjJRadioButtonAOIRange.setName("JRadioButtonAOIRange");
			ivjJRadioButtonAOIRange.setText("AREA (range)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonAOIRange;
}

/**
 * Return the JRadioButtonSpecial property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonAOIRegion() {
	if (ivjJRadioButtonAOIRegion == null) {
		try {
			ivjJRadioButtonAOIRegion = new javax.swing.JRadioButton();
			ivjJRadioButtonAOIRegion.setName("JRadioButtonAOIRegion");
			ivjJRadioButtonAOIRegion.setText("AREA (region)");
			ivjJRadioButtonAOIRegion.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonAOIRegion;
}

/**
 * Return the JRadioButtonLine property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonLine() {
	if (ivjJRadioButtonLine == null) {
		try {
			ivjJRadioButtonLine = new javax.swing.JRadioButton();
			ivjJRadioButtonLine.setName("JRadioButtonLine");
			ivjJRadioButtonLine.setText("LINE");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonLine;
}


/**
 * Return the JRadioButtonSelectAdd property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonSelectAdd() {
	if (ivjJRadioButtonSelectAdd == null) {
		try {
			ivjJRadioButtonSelectAdd = new javax.swing.JRadioButton();
			ivjJRadioButtonSelectAdd.setName("JRadioButtonSelectAdd");
			ivjJRadioButtonSelectAdd.setSelected(true);
			ivjJRadioButtonSelectAdd.setText("Add");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonSelectAdd;
}


/**
 * Return the JRadioButtonSelectRemove property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonSelectRemove() {
	if (ivjJRadioButtonSelectRemove == null) {
		try {
			ivjJRadioButtonSelectRemove = new javax.swing.JRadioButton();
			ivjJRadioButtonSelectRemove.setName("JRadioButtonSelectRemove");
			ivjJRadioButtonSelectRemove.setText("Remove");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonSelectRemove;
}


/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldAreaAnalytic() {
	if (ivjJTextFieldAreaAnalytic == null) {
		try {
			ivjJTextFieldAreaAnalytic = new javax.swing.JTextField();
			ivjJTextFieldAreaAnalytic.setName("JTextFieldAreaAnalytic");
			ivjJTextFieldAreaAnalytic.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldAreaAnalytic;
}

/**
 * Return the JTextFieldAreaMaxRange property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldAreaMaxRange() {
	if (ivjJTextFieldAreaMaxRange == null) {
		try {
			ivjJTextFieldAreaMaxRange = new javax.swing.JTextField();
			ivjJTextFieldAreaMaxRange.setName("JTextFieldAreaMaxRange");
			ivjJTextFieldAreaMaxRange.setMinimumSize(new java.awt.Dimension(40, 20));
			ivjJTextFieldAreaMaxRange.setEnabled(false);
			ivjJTextFieldAreaMaxRange.setColumns(3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldAreaMaxRange;
}

/**
 * Return the JTextFieldAreaMinRange property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldAreaMinRange() {
	if (ivjJTextFieldAreaMinRange == null) {
		try {
			ivjJTextFieldAreaMinRange = new javax.swing.JTextField();
			ivjJTextFieldAreaMinRange.setName("JTextFieldAreaMinRange");
			ivjJTextFieldAreaMinRange.setMinimumSize(new java.awt.Dimension(40, 20));
			ivjJTextFieldAreaMinRange.setEnabled(false);
			ivjJTextFieldAreaMinRange.setColumns(3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldAreaMinRange;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJCheckBoxRetrieveOverTime().addActionListener(ivjEventHandler);
	getJRadioButtonAOIManual().addChangeListener(ivjEventHandler);
	getJRadioButtonAOIRange().addItemListener(ivjEventHandler);
	getJRadioButtonAOIManual().addItemListener(ivjEventHandler);
	getJRadioButtonAOIRegion().addItemListener(ivjEventHandler);
	getJRadioButtonLine().addItemListener(ivjEventHandler);
	getJButtonApplySelectAreaRange().addActionListener(ivjEventHandler);
	getJRadioButtonAOIAnalytic().addItemListener(ivjEventHandler);
	getJCheckBoxRetrieveSpaceStatistics().addActionListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DataSelectRetrieve");
		setLayout(new java.awt.GridBagLayout());
		setSize(719, 303);

		java.awt.GridBagConstraints constraintsJPanelSelectionMode = new java.awt.GridBagConstraints();
		constraintsJPanelSelectionMode.gridx = 0; constraintsJPanelSelectionMode.gridy = 0;
		constraintsJPanelSelectionMode.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelSelectionMode.weightx = 1.0;
		constraintsJPanelSelectionMode.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanelSelectionMode(), constraintsJPanelSelectionMode);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);
		initConnections();
		connEtoC1();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void jButtonApplySelectAreaRange_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	//try{
		//cbit.util.Range rangeTemp =
			//new cbit.util.Range(Double.parseDouble(getJTextFieldAreaMinRange().getText()),Double.parseDouble(getJTextFieldAreaMaxRange().getText()));
	//}catch(Exception e){
		//cbit.vcell.client.PopupGenerator.showErrorDialog(this,"Error parsing Area Range\n"+e.getClass().getName()+"\n"+e.getMessage());
	//}
}


/**
 * Comment
 */
private void jCheckBoxRetrieveOverTime_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	if(!getJCheckBoxRetrieveOverTime().isSelected()){
		
		//getJCheckBoxRetrieveTimeStatistics().setEnabled(true);
		getJCheckBoxRetrieveTimeStatistics().setSelected(false);
		getJCheckBoxRetrieveTimeStatistics().setEnabled(false);
	}else{
		getJCheckBoxRetrieveTimeStatistics().setEnabled(true);
	}
}


/**
 * Comment
 */
private void jCheckBoxRetrieveSpaceStatistics_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if(!getJCheckBoxRetrieveSpaceStatistics().isSelected()){
		
		getJCheckBoxSpaceStatsMakeGroups().setSelected(false);
		getJCheckBoxSpaceStatsMakeGroups().setEnabled(false);
	}else{
		getJCheckBoxSpaceStatsMakeGroups().setEnabled(true);
	}
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DataSelectRetrieve aDataSelectRetrieve;
		aDataSelectRetrieve = new DataSelectRetrieve();
		frame.setContentPane(aDataSelectRetrieve);
		frame.setSize(aDataSelectRetrieve.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


public void removeActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.remove(aActionListener, newListener);
	return;
}


/**
 * Sets the modeInstruction property (java.lang.String) value.
 * @param modeInstruction The new value for the property.
 * @see #getModeInstruction
 */
public void setModeInstruction(java.lang.String modeInstruction) {
	String oldValue = fieldModeInstruction;
	fieldModeInstruction = modeInstruction;

	javax.swing.border.TitledBorder tBorder = (javax.swing.border.TitledBorder)getJPanelSelectionMode().getBorder();
	tBorder.setTitle("Data Selection Mode - "+(modeInstruction != null?modeInstruction:""));
	firePropertyChange("modeInstruction", oldValue, modeInstruction);
}


/**
 * Insert the method's description here.
 * Creation date: (2/19/2006 3:49:13 PM)
 */
public void setSelectAreaRegionNames(String[] regionNames) {

	getJComboBoxAreaRegion().removeAllItems();
	if(regionNames != null){
		for(int i=0;i<regionNames.length;i+= 1){
			getJComboBoxAreaRegion().addItem(regionNames[i]);
		}
		getJRadioButtonAOIRegion().setEnabled(true);
	}else{
		getJRadioButtonAOIRegion().setEnabled(false);
	}
	
}


/**
 * Sets the selectMax property (double) value.
 * @param selectMax The new value for the property.
 * @see #getSelectMax
 */
public void setSelectMax(double selectMax) {
	double oldValue = fieldSelectMax;
	fieldSelectMax = selectMax;

	getJTextFieldAreaMaxRange().setText(""+selectMax);
	firePropertyChange("selectMax", new Double(oldValue), new Double(selectMax));
}


/**
 * Sets the selectMin property (double) value.
 * @param selectMin The new value for the property.
 * @see #getSelectMin
 */
public void setSelectMin(double selectMin) {
	double oldValue = fieldSelectMin;
	fieldSelectMin = selectMin;

	getJTextFieldAreaMinRange().setText(""+selectMin);
	firePropertyChange("selectMin", new Double(oldValue), new Double(selectMin));
}

}
