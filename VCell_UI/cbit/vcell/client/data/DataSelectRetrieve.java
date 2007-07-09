package cbit.vcell.client.data;

import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;


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
		public final org.vcell.util.Range selectAreaRange;
		public final IExpression selectAreaAnalytic;
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

			org.vcell.util.Range rangeTemp = null;
				try{
					rangeTemp = new org.vcell.util.Range(Double.parseDouble(getJTextFieldAreaMinRange().getText()),Double.parseDouble(getJTextFieldAreaMaxRange().getText()));
				}catch(Throwable e){
					//Do nothing
				}
			selectAreaRange = rangeTemp;

			IExpression expTemp = null;
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
	private java.util.Hashtable refireEvents = new java.util.Hashtable();
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

				cbit.vcell.client.PopupGenerator.showErrorDialog("Any use of X,Y,Z in Analytic must be lower case (x,y,z)");
				return;
			}
		}
		DSRState dsrState = getDSRState();
		if(dsrState.selectAreaAnalytic == null){
			try{
				getAreaAnalyticExpression();
			}catch(Throwable exc){
				cbit.vcell.client.PopupGenerator.showErrorDialog("Error parsing 'Select Area Analytic' expression\n"+exc.getMessage());
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
					cbit.vcell.client.PopupGenerator.showInfoDialog("Select Area Range min,max had to be swapped, 'Apply' again");
					return;
				}
			}catch(Exception exc){
				//Do Nothing
			}
		}else{
			cbit.vcell.client.PopupGenerator.showErrorDialog("Error parsing 'Select Area Range' min,max");
			return;
		}
	}
	
	if(e.getSource() == getJButtonApplySelectAreaRange() ||
		e.getSource() == getJButtonRetrieveData() ||
		e.getSource() == getJButtonSelectClear() ||
		e.getSource() == getJButtonApplySelectAreaRegion() ||
		e.getSource() == getJButtonLineToAOI() ||
		e.getSource() == getJButtonApplySelectAreaAnalytic()){
			
		fireActionPerformed((java.awt.event.ActionEvent)refireEvents.get(e.getSource()));
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
public IExpression getAreaAnalyticExpression() throws org.vcell.expression.ExpressionException{
	
	IExpression expTemp = ExpressionFactory.createExpression(getJTextFieldAreaAnalytic().getText());
	String symbols[] =
		new String[]{
			cbit.vcell.math.ReservedVariable.X.getName(),
			cbit.vcell.math.ReservedVariable.Y.getName(),
			cbit.vcell.math.ReservedVariable.Z.getName()
		};
	expTemp.bindExpression(new org.vcell.expression.SimpleSymbolTable(symbols));
	
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
		frame.show();
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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G610171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8DD8D4D57A3895952DD454D2D14B96EBD4D4D4D434D2CB45969595EB34CB05492F28452242242216D62A4B0709C2C9CAC609BAFE14D808202864D221E2E1CBC9CAC8C5AB83F3E1C60719F1E6D0D44C7FFB4E396F39F76E3D4340EC7A7CFF4F7363F9DE6E1C773C9F67FD4FFBBE6F3D678A65CB42E76697148BC27ECCC17A5B18AF886EE4C1F8B9EE612B088B36F514880A3F25G6F88C39F9641B59C
	722ABF5FDDD2A6143B0AA19D836961065DA56F416F79424F3638FB610797BDA11077546BEF0C9FD94F9DDC4FAC1A3E647DD2383E8D908BB8F2AD32F7007E096FD7C878B4890F10DFA0886914662F6FD7CA3896C8F7830481C45B99FDA2DCE7D34D27EEEA10683A4E0994F26EF3963CC564A8E4A2D4B998EF2BF4B5887F4C4FF190DDB3E8B989CFG2463G08710542051F2F01EBE35B3E7E4FFBFBAC6E1A833D2255DA53EB3508B6F70D4968B6EE5A548C7FF709D631573DD3F4BBAD6281D1F06CA7B237FED0CCE5
	88C2832473A8EE3E036809015FC9G999CEF5ACF70CD4E0E12CEG0793ED7BECE523D4764EA393050C7E3BE64F75E2DB201359946D14EDB3FC5CD378BE2D070215445FD910D7666AA8D9815082E0868884D8C86BFF76C79F43F5F30FFB2BFDE040EE5B6CF0EC311A1C5F58CD2295FEEF5A84B992EE2758EBF71A840155F17E8755BA659989985F5CB173B11FC42E437A0E5AF92D10F663798533F4FC1296F924A05C45FC916B12FD42F437F8551D6A720CC7CF5D3B8525EE61193533E66A682EFCF86D2CACC90E60
	16F5B73AC9DD1FCEE340G3F73A5398C9FC2716394F8961B6FD3BCC907C11EB008315179B6162535E802D0D6F7012E9FC26F88AAB08D32B28C8F2A4B1261A432A387194ECFF9D9E63B88FE4581994F4A321746A9C907C31EF528230445D749AA52D69B8EF7143483D88182GA2G6281D28EE30C6D7CE84DA8E32C4D68F4EF9934D8CD22D3E03CA5AB9740D59CF2BB0D5BEDAE375156ABF6981D96E30FD5F48992CD79CA62039E8F3E77E96C77020E2FC41B68B43AADF69BC8B78E08EE5169E2B44BD63EC3E81CA2
	4DED378C0820E040A1205C0FD74E072B5568F2FF6F00E6AF4A94ACFF6DCAD2A7870C9DA0918440EF6617060F093FF2407E52A39DA515C7508F57D0F97F961DB6513AE9D32B5D3EEF5051CEAC912CC7081F77503E2396FE379CC19F57FFC2F05110CE64F2B2D64ED348E9367412629B1D07300D1D69A8A3ED816873G8C43A0EF98636779CE0C1F66372E912A9FBACFB7FE02A1165D43ACEE0206556D68A2AA1F14B18672D2GF206316FDF626874511E8D437AED0D14D519EE11685294F8262341270ED9DE70B3F5
	5AB73996AAF04C6F932AC99D169F6DA82985E8BB0A3EDFD2E5E0313C456892B1FAF7B8A170C413DC932C1C4FBA9D12EF09FCB2BE4D25F8A2C7905E85F8CF52C7E4AF82D8A230B1F5B94007GAB81D681FC82309A003423BA00F5G6B81B6CA6D72DFG1F111860BE1A6BA23A4DD06EE000D800F40062DF409E00CE00F1G11G49G39G06E390A7G8E00D000F800CC0032F19DA58DG5DG01G51G29G42C99DA5D5GEDGC3G61A7E18CB87647674BF62B6B60C47841DBDC7C5BC75C7C9FF83621F7043F53
	3AA47528EEBBC8C35AD049F12CD7EF657B747F839F7DEF8156F73D3B0A548F4CD5ED6EE651A15ACC7043A565D52EA275A80D33FDF64E330C629DB0AEEF0501504571D52BC8BD9B5DB0796D99F40B3BC4375BE26B1773EB29AE57E00FC39E52D15746D5A4166C87E1DC66740D2B481856EB3759C45A09F296A97FF3BA26420CFBG8A3D55BEE8F3B3FC7347E46D31CFBCA40D69AD9F3FA910B159BAC86687ECBE9FEDA47169F0DA8620EBA6D38C2162E4B25E9E7820C26123C4683F483AA48B2E4249B8DE9EDC0E63
	650A43D78A71FB9C85FAF33F504B4EAE68BE190DD36E13657112156953CF744B7E859D5BA53C0B1575488E62871D6585ED0373056B73C93B5AB268F65BEDDFB96D038EC67665202DD7C01A70F99F6851ECB619F60A83F6D9D629FB090A4E81B3A123F5175B68F6D184D429B49ECED9CDEC88815BA28162CF467170CF1F10FC1C0BC88361D63B554A2A91720ACEA13ECEF714AB7C15FAB273755BBC3FF47F48798DDE73D9B9A22B491C44A56E9F94E10AC8A6D57F66B1F1DD75BCD5DE07949FACFFE2B5098F2BE2BE
	96C9F57E644235E86BA9CC1FC93A8E1F42F4B33AF75DC817836952GD8EF87B0BAEBG23331ECA68A6F33AF84027826482B4C0DE67290CAE1752DDB708F431273231BB79D4C61F81579AAE2F77G52B51F56D15285B08C90FB1ADADE43C124CBBD0D4D8108BCC26FA9AF0B53E51F467491F945272B655DB004F4D527333904FE797A8EA1DD5369F279BAB5722A8FA35D5069521CC437FC691CAED8A1AFD2A36F72A3C817F8BA1B5B684B7BF19869529572F2B5728CC7112EA2104D11746525F03A5AC0D9DECB20DA
	5EC53FA01DB9104D357465FDFB8C6946913AC029FEB5724A85DE3F01EC4EC66491FACFF9091CAEDBA12F788C353C3372F97D1E4166FE7A723694707AA5B24EE07206B4720A8B11AE748CB607A47288BD4AE36DA80E53C51F4162A4718C560E0AFF23ECC7F7ACC63AEE408F81048224G647CC6297702A5C8D7BC1E4DD709DEC2EF982F34234D00F47563E5F92D63557296ACC5BA6BF8B66FD54BE3F65C476982G9F8690B71E5911B71E49E3FD7DCB2B4878C37719769A8F9807EADC8721435D34696BB6A37426EC
	BFA91D2E8989FDE7906E9B15AEA6FDF1B024A3816203F02CFA70AD5CB78A5997A924BDD3B74B5B1ED8EA90ABC7DE10BCD645CC5F7CA66B6BBFDCC3F4B61E89BEB8937B79352B15EB574DAE173D57C2174DD29F7A44EA65D846F0CF2D1E8357BE0B53655E8E130DA16CEF33D6137212F53504167C71DA4DA2DDFFA8C6A88177E40EFE2934B5EE6E510292AF36F61F49EC9CF726EC6B0A0176D9ECEFA3006EF9907D71101F8B40464667A85EE233108151F2D82CDA454A78753F8DF0B5E94667BD5B95249D2C0C9366
	B7C9E5C83AF60250F86CBF85FA7BF801F34D8535E73132F51EA51731655C1E42AEGD64E4F5DD84E8820C983E063627DB66538D8FEF6C7C9A3C07759E847E56E35F0B552413806DD5AC4E71F5DB9A01A2A0817EB0C875DB5F4CED83319E6EFA33FD7E11D187ADA603AF72BD96C5D3745BE04666EB8A0BA49F6481E4DAA112D5EE57EEBD5873325E090EDF1C3B9234EC6DB4E5B2734A58370E5419DA57541E80BE5105852EBB65A7A459A97CCD2442D74B7B3C48A97C9AF4BC1DB187CC10BD2FECBB0136F607217B9
	49FAC63411A900C90A33EB96AB6B3058F82AEE9D86BD53DF90904C6AAEAAD82E43E8283F84DE072954EE22AB87E82A4F417D66724F49154ED9851C1B3D310F34A7970DB151444AB27C8316E5DCD20CD0B17FCC5D3290B01DA36D5D1CA317E5B83C27B0B89C4B92F4G4B1286B46567E29D0896F59D34C21E63DC7451CB87489A4D629687EA483FDD6ADAD0D47DF64817AA0049FE55C3F6G480C82C886E0E5B29E44B2E5G2E723708FFF788716D008B7AAD16354F29141770DBD61F191CEF8A674B83DCFD88624F
	BE04F8AB60A2C2D05E6B7DCAF9E9A14C762273106F7B4348D78138B60E1FFD847101000BBF8F655DE55633B71773BD4079CA4F6FA8E9BE9F71278CA3FE98F0B167233CFA335ADEA2AF0B73EDBF0AFC4285B0EEDF007857B85E8138488BD05EC03F1E3CF44EF767AFBCDE81D79D0A78034790EF82DCD8A84A8B6DD347CE8A646572FC132876AF49AF3D9065FEA620DC8360BAB97E850E8F86DC6205A8EFC51F3ABD93F942C548B7B19F794A81574A717B8A909FG38380BD0DE00C9DD7E9C482B789D663F2DA9BF49
	2F7E9D4A2DABC439CD009B6278150B313F0B86DC666F30AD1D35783F6B1B1FFC6BEB52B73361F13343E1BDA4AD741C22F1A7E912E36C1DF13F7F39453C9E42209E42507E1AEE35FF02A1AFA68C6D5939447F3E215428EEABC4F6A2177D1FEEE55F4C4A993F0477E1C0D77EFB247D54206CBB996DA183523682DD406F5126CB3B55F5CA74467D9E6B6E0B2548178C38620B917F52D22C53A6408DDD0C727EB9209E0F23A1AF65E2AC5B0F4B7E3BBA5F78738EC1E7BC0615FB2FC89CEEF139AD3D2E31D6BC5B6B1B2A
	39BF16F9313C771E27780D406E9B0D2371D2F6893BEFB4136347D37C8A851E79EE9EEF8F8D1097F6895E6B0828A373E1A19C7AFE00FA00D6GABC0D0B84EC35DB5E4EFF044FBC95BEDEE18269A2D3BBCB61F701E4E9AD28F7B44C3D23AE60DF26CE56518370E54E1A468CCGC8834881A87F83166126B5BE4B30EDC8378C4C4F3FF1297DDC7F87CF5F3629520CAFD443D723CA3BD5FC4C9EF3BDD90F84C1DEA4C0BCC09A00B0817C8C50B4816DAA2A7D4C27DDA4E25A6DDB8157E2376F5B2A5E2F63E5EDF67BE763
	CE8D5FE2D53AFB02A79FEB971FF7909BF456D258D224BD3F34FD6ABD3FA1109786908D4056DC85F6DCF3A5CE40FB9A45037E5933C043272EB361D2BDFB96ECD92CEF4FCE2349E2E7DD3EA059F40AD9EDD38548EC84E83B94EDCA2273C9D2A67325B86F6C37FA7319FD2047CEFC46F823AC680F70CB312DBE3E1E44D4AA24F3GCAA3BACA2AGDA814CGC19198D35F577EEB14B1453AA4EDC4B1BFBE2671239A747D1F6513AFAAC22F4D6C59C86CCB003CF400DC00724BE0CE85508EB0F4995AF7C56D96DF76715B
	4EE0A36E176BB49CD65EF38F78E7E738CF3E604BBC79D86C6C5544CEA450A58124DF063193466BBF77B20C1D8F2BAA75E327DD9CF2FFE9912DA6A67F3EFEBD79351743FC77F214DF3B1748A7EB1C2E4B313D759F744F8F03BE79C2AE5763DBB8641F3ED5BE7922AE57EB5FAF75F9597BE9B576605E4F6F1D6877846E17A77CAC67F41FFCF93AFEB96F10FF7AAE7549D7FE051EDF666EFADF5FAF5865C9FD52FAB346CC7595681B1F8E7BD7D60BCFBE5395FAFCF30F7827EF11CF3EE115BEE6F39E37B9045B7C101F
	E5783BCF3EA4DD1B4F9876CF5F85BE79B2F4ED5EE746F1AB0F5B3C6328FFE57859A7DFFD249EDF61AF7E69FB47A7DFEB241E4D917DE833A992ED3E574FB2BC6213AFCC576623477C53B75EA7DFCC245EB8345129B707C9825A74C89CA79E68C33F64F25C6FC4EC9335D7222F368A7E157DBB1FFC66AB75787E61273E19793E7846DD29979FBF756138904AED3E5527AC7DB25C6F13AFD957667D857E69BB6613AF73CA3D78382DD7AFBE042BE0DEF69546421FCC6817EA0E5B466343F4957AEAE321FFE55F6613AF
	6CAABD3E550B7DDBBB7CCB4327DE8B45686AFBC143B7BA7B5E704917F415DE5DDC582DD7B799C01B4B6BC16C463AA93B9AF1DB0D08EB399A6B666EA57E15BD45A75F382BF56351601F3EB3FD7205DE2D57F69F6D46F80C66B6EFDB6ADF99F679644B543579D58D5F6862F156B2DF71A8C4694547A21B5E1A3B82E82B23B0966AEC988B4D00E34F436651FB1F43108E66384915749D86C8A7C5217FCEDBCCF05910AE3B86F167D05C0AEB18AE062BD85CA95169790A700E64CBDF40F454D19DFA4FBD3EAD68BFC3B7
	96101EA32E737E9CD7CC1D7EF3DC375469BF4795DF778E43038C655357181FD8B772F3DE49F55E1F737AE01D77F2AEDD27DF4E156B744B39FA5D4865DC3F4EDBB997517A38F741DC4A6FF209AE3A61C09E3112721EF613E72AB44F1EFD8B2BF6816F477D59B5DB22596636EF2D63FA1D755EEDBCD42FEF6331FAF2DF5EE59FF4760AEABBCE2B2F60F972A68159AEA745E1B4E7D3FE375159AFEA7CF0BE65E7F95E78AF297766ABE66B91375256353C9DCFB22A5BB1F9863C855ACD57B5580E4B2DB8C7F3DF034FAB
	145B48FEEF942493G52F89B1DE5576B3FF224F6D8FC2D36BD3272FDB128A85FB76BF879CE935565A36D3F926454DE4B6788FCBD51F2AD16AFEDAFFD87835221G5157E2799E5CC74A1728AAC71A144E56A99F633B5EB2D23FDCF49D74FD57F97232FAF9E63D77D84ADE2F9FCB2F2C779ECB2F2D779DCBEFEE709ECBEFED709DCB8BB60C9CCB6F9EA8116A2A2D4FE99FE8DBA74936EC74EE2BE3233E2D87B6FA377568C65F361E36513B2DE7EE74ED6B799B09ADBD961345A9BD5D48EC3C7700B29E6BF9BC6E5344
	A31997D5C35DB7DD0771B8414C1F6F398E6371DAB3FD3E875211G7157E1BC7EC1B72ED225D84A3ACE9B0F52FEB657A1C4230E4FADC4C783245BG4C5128E37D5E11E2F79C5005GC4C6B3DDF151F8DF6B62031E75DCAF79E6671052B76BF99B1960545313AC494D08765676AB8EF96AD9AF61138EA975EC60FAB619757464CA72CB2F6FA8B1G54DFAF6BE3724EBD6C29E703047F7208D24FC62EA7FF44B65F82723BG9C121E006B51EF25431EFAB6CAFA620715FA9A389E072EBD21125CA83846DF6F69BBA66F
	34231EFA9AA47C26DF14FABE65FA2E6C575313A2494E04EB1E2E9E6118271ECFA5FC4C31B98ABDF535981BF6A77647E5B7E0ECBEAE1258EC01F4B740508D981BA9FDFAE58A3E0115A3822E31B7686E0D0BFAFCC992EDBADCF3G0AEFD44740E17A5C01ECCBDDAD435764FB58D207FA3AF54B07D08972EBEF5473D9CE3ECACF9D43C796F868D90BBEFB2597FD56FCA37A6CAF3DC446B024C3G22EEC41F451B744A14A015A375C6EFE358573ABE4B12680598D8F783D44528FDE6ABD459321661CD0B15F30AF51C3E
	EA09070D6B50468779FA31B186ED9C5CC3E43BA19D84909103B66E6D56ABEB1CD4366498EFB63698C72A2F8C604B0D512B2F571628ECDC4770FFB4F858D20FFA9E57ADDF69CD5233D9F02D87E839C96D4B6125AABD758CDF3BCC69CBB25EB01FFDEF4338683A89FD36F500BE378F69A800041B50E79BECFAE5CA134A10FD13377EF77EC272FE0555F848BE68863DFFA4BABEDE0863DC9124AB811A81BA7F486607ECDF7FFABABE74B90D83B0BD586590FB05560904B73B92F9E301BE89A08B2094722AA7E29F79D5
	9DF97614BD954B1EF00131175CE2134AF4E849AC1EDFA70D3DF7AE561B2B06004C4809B8763E59037E0A1F087EFAE39F71D7AEA9DBAC44F7AC7A6BFD2B52DFCD00EF0F15FDE40E557A0BBD8391C8673E43316AE7A3FECB7141316AE7A3BE307167BCA12F78E6DC77AE2677242D109E6638E3F6DC8B875E0CB65DA622CD29B723CDF7775367B3EE81FBG1AEE61FD0347D85289F86BAD329D63EE71E6D39B75D148AD2C6C33B83E0B622394F8E65313F4FECD5289248CFF4272E752FB52649970A00E2B236B07F0C8
	C7F35C79D634B30E6392AD08AB0DC3DC899DCB483367DDF1680FCB39BF9CF1680F9B29BFA2A19D8F109A077E38C2B336E64749BEA83A551BBF165139D4452D6ABADECD7155372A6BB8464C1F9D023C08DB317C315426625BE01EF89B62966DC5BBD75C4657CD1AF23AEF134B96F81B37F25EC04B93F61B7A9D745BA9BE76B675BB684D94CF52A91057F0BB16291517A90C630E52FB96D910AE0AC7DCC8BF56C78547CD2638EAC8B7F15C4D345E5AA1ED62380CC1546160386F68BCB69E52499C77C7B35225F1DC96
	45D57F99F47C99F1AEDE16D60E2B60B1544DF157F5239D519C776A9E64CD60384FA9DDAA2433B86EF4B35AA15C41E31C172F1463967032F4F1DCBC45B9A0BD0E63BE643CC19CE7B3A3EF5A9D988FEE0F39F4B6600B6E14E32062CEEF71302B0F46671D6AF63C07621B6FD4376329A23663AE480B3F934B749C3D9F31A281463184443575E3591BB8AE113645E0C8C7F05CC40A0B05F4A247D576A1EF8A4719685E7E0A3BA08E6FC2DCFFAF7A2219638EF25ECE0EFBCBC43A483B50E7B3FA15BE0B83FC72DD321FB2
	6E7266334DA6BABE5C25EE6B2D94DFFA373A2D7F2D975B3A8172826E46B2BDC26D4904F4AE47B50AD87622FB90E79451376D9CF787276B66381F293CE8C8A7F0DCB937BB799E347BE14DDCA4639E59565CFB3C597D8C1DFB15A62AE365E50ADF11280E15A3FBB0D69AA1AFBC914BF492EDB7C577427C71DE44BD550D7698B8EE0F1137FF0E7B0A5211F7F262B96E95AEAF0563F6F35EF40E3B1752555E8731F29F627277203C367B50BFBFFA4C0BCC009F3ACF76C950FD5E7CB3114E33426FD347459D949FF31FBA
	AE368FE0DCA4C2DE757DD826EFECB84E87F35C35B65E5F5E0F4F1F6EDCC878094CAC402DC842F9D6DD3747F3B964114FCD1B3EF2DACCDB0C7D2DF4564646450B9696CB7CDDC9B8AEA6D219A1100E82C8C842770A06BE42F7ED22AE1FA0A4CF55FF2FA83D7DE8C1DA1234BE780B7C5EC17D47BD05CC6702C5283391725DFF21E34CA2657332E4BE372700BDA347728FF864133D261F721579119E79243C174CD466BF6A11CFFC7408407219ED77FF0836655D9FADD8EF8A70FA0ED01CE4D3BA5AB669427C325036B7
	19EDB39663BC23E2927A73815AA734C15A8CB0EE9246D8C90FB246C2819FB5C90E2B78C95EE24CE5A572D2A6295B60B10A4F1A24EE03B32D58860B9E00B2BC0071D4CB67D3C910CE67382B86E8BBCA0631BA99F1AB787C32B1994BFE960D45A0C847F05C0594C75EFBCD67B803ED24B3BA105FDB3EAFE0BABE9DD10737B38BC68B52BBC183647DF237B864969EA4E7DF3D7D85ED918F52F9293DD922EDFED0EBCB3EA01F01C12093E888AC17705687D9FDE11D3DA35C6931660E077C94055CCC9D9D2C7EB75179E4
	1EA42F0463DBA83E722FB21E45C5921D7FD2BA488B7FAB9F13685C2268EF9083FF63F3AEBA57EB04F49B475D3F8F63427DB72C6F8EBA77CF00F4A6C0F18A62FBA8BED5FA4F193CCB4C7019FBFD1D612286146DAD1F68740C0F7CFDE35561ABE6E6A9FE572650BE4D0A67FD99D2F03D5AE2750C19AF1B641839460AB163CE719DB301C0C360DD899F16A2478C1377388565C5FA1107B611EB9C50A4CB7288AEC3A10F597329936D4966F59869911742C3B0F6BFA46B2AFA48DBDC2E2471577810FACCDCCB71ED8F29
	47441B7BF1CCB4C1DE62C398EF3731797C43B017FC98F18DFC8DE2FA984BD945667310CE6138F59457A53DF74EF037183D4585619DEBBF112262E1BA7E66D50737FB43A301BAA6D91CBDE0710CB34BE6B94E6A77E2DC646958240E3332C7006791B94ED6BCA24705347FE046FE2076912C03D03EFEEAFF84FD9BEBA167B0C4C0BA91A08DA01B675DC573E225F76C49FB71113CCE3E360C54D6099CDFBEC21DCADC3614E6BAFAC754B14B07FCEF7D831EA1457C5E4B7DDE1E2A3F072674FBD52AE7FBECCC157D4E6A
	B8241F745383961BE5E0F0E0176530A83553A83A3EEA817AF6G063B359F67D763D231AF4A6EA7F6755A2D0383B61744BB0B5F838D61F449CE4FD83AFE0B9CCBB386512608D158142C68E388BED6E313596C296B092D322E3F18D1D742A8F42963B6D5232B50C276AB870C4EFE0B8DFED0FD5B3791FD4C97E15CE799404B4A3006772939BAE51825AAC369E4D837CC166B30FE323ACFCD25EB0FA64037CE463ADA560DFA07A6E39DFC5F2D2DABEBB756D590503172DB3F147D758677D758E45F6DDCDDB7511A32FE
	417D91474B75C83FE7FDC5FCA56BEF647A13F5744FD4694FG1A0C49327EDC0D7E8B4C283F68D16CE746BBF13EDA41F1F35CB8DFED6438B3781ED17723582F7CC077B222A11D82104371A24513B3AD48799764CC8D06FFCE9C6D1C0148A4571CC73D77CDA90F6A0D8DC5FD2355D128609B5B58D0202A33FBDA64BA6B6F47BA2B1CB27AF9AB56D96D94F59BFBDCA46543F7B3D9BD3EA272355E940C23C922E719042F65B22D6B43B2356914C95D6ECDC0C3G4BB424A9539113365DAFA7473FC9658B6E457285CC41
	F6FF96AFC328973FA867D2D1D3E43F5073A32628E3F98377C152940C51F9F41FA69D52B99CF70E4857DEE9FCAF1B6F1FB526E1ECFEC5F903A09D49F1BBA90E1C25C24EDDE138493D98DF04D6E9C3E41A7FEBAE4F98FE116BD04FE77E5BE75C6498FE5E241AC7374A7132CB6463040ECD3EE2B8B5CD2EABE96E740DACFBB2171D61C5F6219759749D29B4F5AC3AF5E2B168DBB996BF63F1D274BFD87F53E83FDF8169EA0E3376E0CC347D8F5675F43A4F968E698400CC0E1FC171C35239B864AC9B06BFE90F373EA7
	D31261CFFD057F0FF21EF511D79D337E0B78A8D4DC678B215D1E31D15FA6575FEB5DFC8E76186F5828829A8238AEEFFCCC3D46BAE0C4F9ADDE64A97B05AEC99E460363B1F53FB048477E6147F0CC7F5DF7F279EDDCDF300EBE4D9CD2D57E08475471175E2D0D3F5BF64A7177719EACCF74E398EB7BEDB866A6F05CF3F66C27B2B9CE5C4F6FEFBC4E5FC520FBC9DD108E6438C40AA3E7A145F05C0E813DB8C13EDF271FB26B6A78EF77069024796BAE396E6E32E15D25685800750231226E17B29FD76F89F5AE7475
	0C373E5DFDFE72DDB2E6BE36263F281274B7B90A72B37C2594AF244BF836561F44772FCBA14F110E6F95A734137B76A9104E86A8FE826656GCDGDDCF60BB379B9BEA859F6F5C365B9D2D6281512AF8D4185937F051D86DE37B44FF1E057B44B137DC85EB58E62F67CDB91EE0759EACDDE313AE4CA7D7E65F6E8EE2DFBC64258188CF42188FD08D507CA45AF72C211A59374DE4F1EFF63B0D3DE6FA2A969A57E1918F4AE872B08533CD70F887229364D91FD45C4FFFD29B0F2C0E36527BFAA1CF2A77E93E257848
	A7557BB48F19F01FA68E728CFF473EE0999D1F82A09D4AF133687CA48E52499C976440BEA3676F58979C22FB554D99B02F83984AC07C29949F14414E35A2E7E9C8FB68D6E53BA6BC5E6AEFE804BC04668C3DF97A8A8F9D235D9774866AF9926B1BA7B8C95F5CE3F75207D15973B4BFD8715915A8A8D782C0B6C051D466834637542DFD8E67EF8348D7B915BD07D38F5796090F75CF7F62FB565DD3FD0F0DEE20891C2A58FF1C2A9EDB2E1FCF627AEBEF6703F13A9FFFDA22C7C7E864E3CB2433E0E66A1EE996B995
	6FC7BD7013725B8FE597D98B30CCEAFB62A49B93274A7723587D1AFB78998FF910370416D1900A85459F6D5B571390107A00A7CA318F283C7AD2217911660265DE7456BFA5BD6779142CB7202BFEB60B7FC7C55CE70FFB8A5BC41185775752B96E973E47D974B446FD93BD832A9D52E600E100E000881E7FAD4DAF134EC2A2679311776D4919B6AC7F2F7D9853E609177C9EFE5AB396A21E765EE65AC748135650DC47CCC15E9FD3371112B13495ED3B29D922EDB70205BFCB76B4EBB749F04DGA863E73C5D3BD4
	5BEE8E6FC53E3C27D93BA94F04B9D5A663E36D6649FD58EE1AB2FD371BF620B1E732F6C35243196AF6536AA2F94A53BCB66F587E0D51B6E834F21A55867A5C9B1E46410EF5B2D986DD38B73815AE22690981743CA764E3779F1C03A20B5B70034A7652B067E45DB30C8984CA3605C95720B7AE145A4BE3D6ECAFA919A3351727FEA0BF99CF59DE64A9FDC60FA77B8B6FF2E67FA4F0BF34B9519E56AEEE276D2799E4F6BE431F476947FB84439C37155FB78FFF8663FFB55DDF4A06F4513308EB2038D669FCB6063B
	1D46B02171552FA390192325E5BAD2290E314CC347F26FC05AD36C57467B537C4C0F0AE7D93C57423579D9F65E9D0B779DFC9F3BCB47876AF8F7GCD4033729AA474D9F53C3FCCBEAE240EF79A34AC7F741F485C67EB6FE719C9B1A572180AF8F624B82C6E22F1C86D7D0A5B1BA6591B83576267D8FDB3FB2F63FB3E0667FC2F796B012665B939FDF7BD273637230BFC37C4BE8D0558710D4546CC11E832BF234F2D52765B00677E9CF9136310EE6BF29DD946A1C99673CB88770B79B97D36467C3263E759C7176E
	91F8F94F68A76FF25A440386FED2D8B30C4DB672E1132A7A5AEA435A5AD5C81B52CF7474992DAEF9FCDFECA63613673AD9F92A8FE0F9421F43B56E5D5D824FFF1D3F13124A731FB14A653951C387636966675014E50D35BF296815FF7F4BA46BF9FE4F686D7E34DBEB77A95D1EF65772B236E421DD7F91E5FD9DBAF68DF0DF0C53358B4BFDD21FAC67DA33079C9A67B773B394E23324718F2E99D9ECAE40E89A5CFC2C4B12463AE9B056CDE3B42CAD04737BBDCD53FC6F6937830DF91AE22C1B26EE8B1B3E53996B
	F60A7D6CFCF8297DAF520EF50C02650FDBC91EEB7252BFD0C2664B50FE74E560B49ED77D325F5E6A537A5F444FD3081FB6127FCBAD321CABFA35F2EE67F2F2F565E09BDC33D716730D280D29D5A66D5C26490475D67BBC2B5B9638F6BD4F66F80C66967E3E197BF9D637C1F08D17E8D85DAE63FB7F314F7B3E371584B4694F4BF51B733C3AEE2F5ED7262A5BD60BCD6A53677DD2A65799C1375B2162192F82C47415B09D6BE967BE017BF28E77E5834F9F344AFEEB5771DB11D1BB56AD646F2CBA26B31F8442B5EC
	BA1B93B31F1C5203BE09196E3D6F1FADDD9301A6ED3A6C136C69EA1F1C1D7F1EB65EB75B0C56C3304811E8DE526953B909C4736A1145DEE3DE26E57E2A61E7A964CD9FA906C387E49FDF41F96A33C70A5799B6596FC15D5A383F044B31E6E3DDADF648BC1B77E87B2A0B7819G9159AC1E63601A1C4D56780C265C01B419592C6E0497203FFA0151303A9B5E0FF5D77D026F3D52A620E9FFC1DE4F1ADFD0575DAC17C150D9272A4E57E4349B0796A9E9B90D6B9B63BE119ED7CE5E03E5EB5A9C4D1A165860FE815F
	D3F8051FFF9078827AF073FEC1606F30D54A3F2FD47CCED9B856E71C4682CC4734632A036DF97D255D7B7B40A96D7A6F83BF5A2EFFD6C0FA7B48E796E436FBBF33E0FABB29837CB68D5B33FB071EB39D8F7512C66A66C508C1002A97F13F6E68774B843ADF2733D7374D54CF5EC7A6DF2EF3237E6E8E6F3676F7787E5E1D23033CE756E307EEE10048676FC7F750776E6D8E0AE438A374ACFA27255F6CE624927E248E7A1C3158C751EC3D745E925C17E8FE9157D0B7AC45FD89B34755AD676FA53C086B256D34BF
	CF03F4B64735D39CF9673968A544CDB10DB616089CEFF9E9DE72180E1CD16B98E96D65ED2DA54D85FB34E3F4DB8F76D7D5E0EF930095E05CCB6C8C92E91EEC520ED16B79589E7A926B6722611AA07131FE6ED28776F3E9AF79DE1FE483CDD10EFC8F2AA2C75D4FCD315379969BDB301723E7E2307CAFDB64F76A709D5E0CBE9957A075EB31D6757949244CABF270B97513687EAD61EFG1CA3871FD3BFFFA77F56837D46EA7F20E553A6B2C758C2DD3AC5B412C107750D77DA717ED4E48EBEDB7D8FAAB79D5239G
	E5AF233E9983F8DF218AF05DAF733306ECA8A374E514311F3EEB178C69CCG61952435723D410AD77447D26F63EB8469E624F2B77277B15BDE417B9B5F39C9BEEB036F6C7E902E9D463D529D8C325DEFF7E24F71B52CC7F6746C05713E5D584FE3BB49634C0CC0109B764A487B8ECC6FB01D53C783ED92C0B6C071BFA056G6A81DAG3AG9CG01G61GB1G09GE9GD9GC2AE74FB3958679D6EDC652D6C0014DEB8209D1F53E8A1DD1F946B05FD3C0D6432F13F892E6D396CEC84C6331A6F63D9F3D9FB8800
	EB28C4435A43F9FCFFA2BA57777EC4825024664A6DA1ABD7339F37CD0EEDBCD360664DB28E5F4BDF7EE1B147617B601F6FD76024F737674CD7605632582C32E0DB48857D352FE23CB9AC981B66D7B1B6AF263461100E85C8FE9563FB42DE0C6FCC40997E09B21E5D0BB2BA7F09B2AA68335741100E840867347379DEDE6ABF479A5F1D87859ED3B307C8FE590C0E126A999897FF6E7CE40438E8F35A9D2253FD08E169D73870BCCA0F77EBDBC1DE770C11631A713D6BF1DE059BF882FD70317AF9D854765F53787A
	A9EC860B4D9838A64EE0FB680C66C5FEEFA6FD860B4DDC381666B19A961BBF72FB40AB72FC4FC99B0026B5CFDECFF46729E3F3E00D9C076BA45CADD69937C13AAFB7570231E1859991F9D85F3BF9FC2565E1EC1CCAE94BDF033A83E8FE8DE94771FD242E57C63B47026B04830784DEAFFBBD6AA584E4C53EB6F23DB09B6CE634A18E68F3F839A67267822BDFC79B26D2DA93240781C2DEC75AF43E8793757AD8ED387030EC43059EB6E4002C5C57C79313F59EE7B1173E816D740D517095FA1CA1D28FBCAD236293
	BD783A0047B1AA3E759EFC814093BAAABE27477B47D140936F030F556D93CE6C7FD2003ED458AD5557AA9756EDB360D85D6E265FDF9A06F488C094G13516D4678C8A034DCC6249BE55467230C07A92D955281GE179C87B60A046C78C6046567F459D116363779EE724E401AC21E077A87CE771B8A7249CF82AC6451760415788BCEDBE78184F4CA6743B8968438A508F51A674D9CA817A6CB1769EEB616E1295G0D056877A7C574FBBB60028AD14647FDA8A32190E5F452E7EB73A0DDDE343B243A88E9F7713D
	396622317A7D71E1596FE7F83C0FB28C32020BC6633F399E7E0BG1ED89FFC4C762DC634BD89680BE622BD630D7C995719E87B4D14568D69A00088CE9B530336474D5433FD243D6B02DFE45B57F91CD91283320A5F9C0D6DD19EFC1540D3BBAA3E888F3EE66069744147FCF6078DE34E8A7491EF229F665879D874A67A2C41CE4722D990ABG4D33B0669A9D6877AE40054CC299818E7EDC6DAC14F18325ADAA5EDDD289D0DF0C34576CC73F37960FB566BEBCC6FEB2B91F58F1AE9FGF224E7ACBD5E950E83FCF2
	71483E113ECD6F743E7EB7B8FD2F7FD7516761C5D837379FF2C8E713555067C86CFDFDAE116FB32CF316F15CF6D71B5DE5A1872730F94C36A5B80F4900B21335EA9EDC4BE66F26E766B11A7425FC2E8EF8B257E902EB3BC44366BAAD4BF12EE31D2D35DDBD8F9F87B4A1GB88F0F045F1EF39DF339BC2F41B34466FCA56356CA673BF46E51BB6BAF8664A54E4678996A4198CC87DC451C5D525A73BFC3244ED5EB4FF60B5BAA1A942BCFD66F71345D9299EDF3B0565A69BB7D01108E87081D037AFEB6E13CA6832E
	3884E370EA9E73CDA5A8E3B20D79A1C88783C4F25A94873F97D1B256FE621B82B9DE2773F398F3CBB0DECF7718E36BC1ED29E79DB23E73FC72294F01E5FC13FC722901BDF7360F2E61DBCBF72B1EA5F4D3FC2782AFFD5F4C0AFEB7C1DEE2A95A1B2FFB4651A84689BF79DA464C476C1ACA7B4A6A375476BECF718DEF296DBDE8C3FB1BA1AF72AD34F73F475EC0695B3BCB8CEF0F260F5F2CDB6EFA0937852EDDAAB9EC9F706FB42E9DEF3372C9671DD0DC2004E37DD07020777E6E02C15F7DDD78207EFE6765035A
	7D4E2B877577BB2F9FD46EF7B25C9F87C9BB6BF7DA68FE2174BDEB0E7B01636229CC025BEDB1394D9CFFB7AD97413708C4A16213E8398ED2DC2704FBD0017BC142BDBCC87AECB059ECF7A26F14C1722CCB1F45EAE56947A90D4586DDB877C786AD2345E1D4147B990E7B014749A7FD587FC5C0FD243D0DFD0C2B8F7B347239589FFDC757D22D10B6818C4F45BE6DFB3E268C81DC52DC1471FB3E26ACAAC39977D35AC6C83783D84B1076EE3E268CA89BED1F066BB187A9269427B5A31E31683D7FEA7449275FFF8D
	7A6453EF4F73689ECDDC193ABDAF247844B2F5FB0E60BECE013C06F730BDC70EF8962777767CC7BF79260F190F5995C47719425ED15BFB9E45C73E2336F771BE34B78672AA66213D959E6345B06003670DE6BEB5E7087C6677FB5836926E318E117DE97E9DDF7A4962DDC0E86BE77D55BF2D5ABE6C8D2B328FE3386A3DDA3A3A3DCABA69DB25F21B0332274D43B670B3DFA7154F4776B21E5236C25A84B0BC9F5B5C87FC8FBB84F0C973D1C64FGEFF365A8A31852B6C23A9D40DA4E57A67CFEE2C079D867912F12
	A99D6E2F0D398DB03E65FE72F5795966DE25EDA82EDC9D03852C4D152BE3F03C5C66A02F61DD0C41D3BD5AC09860235F9DB98625F30DF46263B40F78107A945D73DBFD7B6594BF7962464C477C768375CF423BEAFF76D0FC723BEAFFBE47776D52A02FE9817AF32AC7FDC6GBEF641E87C992D634F1B76EA5BE54B81AD5DB78734F49FD834F4CBACDA3AE19D3A13F468DAF5683E5321BB432C25BB4B2C0D0B27F54FC3D6027E587632CFBE7D312DC673AD495145457DF4DFAEE901BAAED2A8BEF501BAAEFEF6E1DC
	E4C0DE4BFB9897FD1A6FBC0EAE1E5DFE72DDB3E6BEE65757741B5DD16F296D5DC571316F296DCD1377A7A12F2A826DFD5863BB04C1008F2F98CDBB086957464F4D7D5AB89B572725BB35CFCBB7C507AE3DCF9B0FB65DF303FD476330CFBE7DF83C49A71FFE7D6C247B12B1956A7A79096263AB547513415768C910D77BBE56CF266EF9473E636A95BF79560CF8D634F7FB13681CA164FD353D8FD1FC787BEAFB7F530776C6C1DE5987E8EF27475E359B70019F0CA69E43C5ED7CDCA6EA636CA31DB87BC4A74E360F
	F88633F77F6D7613AFF84CFC52D92EF4AFA96C83355FB7D0FC6487EA3F5F4E770CE3A02FE2A17A7DE60F335F06819F3CF0B4FE7F5A2875678E23566FF76A50A56A507DC907AE5E282D1FC41FFB9A7A7D4043BE79747B01393A5F8471DDBFD1F4BFADE2213AFEEE227868056A7A795C4E6FB3C21E21926BE70B433F38EA73136F0CB173B13BBE5ACF4A9FD02936F78D4587D72A6D0D65FB69E110A7ACC2FBEF582F54EF827C5022516C99556A56D310449B8E5718C5FAFBC673F4760C5E7358B3E265DD4E3F571E88
	F20445382E59E7403D0B06453886FA0352BAA09D88903E9857D1454B5066D840E5F399ED4BF09DD53584E57CC2E93BA1BD84903C84E98FAD47F5D46412312E23AA69B9BDCCCE709E5C0FCD67BABBE89FD1E100F188204500F572CB0FFFEDAE48A71FFE1B3B4BA71F923A211CAC569269BE385B200E4187A9FE1CC19D03BF76E2FD84C1DEB63757B266B64F788E7A49976DE79F738DEDC36DCB5576F6D0FC77D2353D0F73BE468AF949CB515E499EED3EFE99547D325134399C5D7D74AE09578157G159C76FD2B78
	7964CCD30B4DEA3109644B6DDF09B6AEF3BFFD9E1C7DBE2C78DD28FBEE422F85CCC7658950711CCD36A9C7717B6808E7077EBA3A4FBF2EBAD85F9928531F1EE9570E771DF4BF081CA39D8AB1A22DA3787B11919CF7B75DE3A2E7C667AC437BE5460FCA853E07C63740993EF268AD990FDB60788C0C4F792DFE9F7605CFBE7DBEECE8C43E62655A76455A6EDD6C0C54656AB6BD09622B162B5B74EE3E4FD18FF9214B31CD7F6467FE1745CF3E89FE6E13FDC1774372B47636D0FC718A353D8F72FD32F2489BDA0176
	5E6F31CF1686786C95236943AA683BCF6C77D2456F2BC6BC1B7157E90FF71EG9D676E13ED7A1D62770AB11FC5BBF65D1F9DD79D2CFFD82333DFDEEF552E9B967A597ED779597EFB7CDCCF3FC15747C59F2A5BC3B17BE64A076A76F08EDFCFD7C25E388F31BD98867CEB47B5FE72750F190F59D5C46F9DE7E86CFD0B6233B5761E4F6FC564C1DE77C7E8EF30477AAE8170299F0D267D3FC15F21E43F0B956D7F1C9350B60FEF9F435AC2184E3E759FF46E836CD178412C787D0257F303FFBDBF4CB981FE4853599F
	4F57714303AE5976C7953FFBDD475F8F43A7C0C75B20EC538F0A5FCF3A0F3F6E9947D507F4CE6120361EDF9C5456F327A85BEED47CFEF6546FEA796F0742D11F696A3F0E3F7649B6CDD17CB60D7A2CD77FF59FBD2EBAD81D5AF56EC3387B35753C2EC7363DD1717BAE63717743E347D58733B939C76B0707FA34FE3841AE5B7EA7456FE67B7177434FA7C0C735625D64750A5F37B90E3F6EC70E2B8E69FBC17B3575BCE93FF6DD7D95DDEBE7415CA76FA3DCC3C7716F26942FC45CC40A6BA46915B807DAE570EF0E
	3C51A71F7E9C596113CF8963D662BE1F0BFE07B2E425FA4E483ECF993ED2BDE71C39145FF3023C32AA34776DE57E4DF5AB7D647BFE4CFC4C2E9769DE303BCAED6F8C0A9FD72536B7006FB387C1DEB637771765CA7D6D2BF61718D70DE60EFC5E9E0FFBC440932C600B5011414AF79F5DCF0EDDA577C38C7FB045A7AB702C5CDD94CF521910577AB146EA9A6D4F22A11D70B15AB2091E1FA335BF456F6E5E635F5E8F1DG9D3B9C32CDDD0A5FD3CFC0DF7A7AF155416AB45B216D4FDEF6E87B33923A7FC73EBF164C
	6341B1007DD99A4755D39C795EDC75A73B2573AA3E780A780D3D97104C5F01EC78C47F3DG657B6A6AEFA335FEA237B1F6C671F47E6CDB67A7D80633697DE4AB2483BE617BD2E6396ED2943FFFBE816B2A83A7C047B6451A79DB456FC7CE407AB97B842C9B1F54D9BFBF2D33EE1C59AF5B7E36627785A7E03E9DFD82F44C3149B64DD77C8EBB8177CCEEBA2EBAD81DDEE15356F314CDD94F6C5D1F7F58BD4FF96FE9A13EE0FD4C1D7CAC5BD01DBE26D085D1C0C3G7B18F8C59F43F4CD32937BBD3FE3B1153F3B13
	447B1D5369BD68F4C86770FE6750D2390ECED67CAED9FA7C6BEB458950113BCC36A9FF397C7BB74B0E3F6ECB0F2B8ED62767AD574663C54B3563E3943D0FCF4E45972AB19EBE224F4D10F371D7F0DCB87FD6C3CBB546C8B21DEF05C2BA8EA01D6353A8DED83D3B44G50349A71BDF4BDCB6814650D2B9EF98E29045091E8190EA3D447ACD55E2FF5A629F456D98F1167A6AF5891BBE9BBBEF53B40FF47A93E83F37AGBF1FFA35366C6A33875DC0C3G5BF460EA39CDCB6F8D53EF3E30E7B0727939AEA12BF14E52
	CD3F7B414A7926CDFB265C65FD645D9F3759523B4FA652B326D83FF09BBF1FAEE9B5BEAB124365272D56FF571469BC65DBD9E71EDD2B73E20356DF176F20F65077B29F626FBE16576C2667D4D4433589001C3344E8F670F36FBAEB583B1B8E3886C8B44C57937939F7E1B57A7D27525751C4D78DFBF71344CBEA0D3A7F5C5C2DBE67D1BA2F4EE462B4066A8ADD1A1D62007D00F4EE602A76F982BF1B08E5EF350AC6A71E25B637C7F966D046596CFC298CE29B4DFF34DBAFBF1767BFEED41FD9C472CB57E0BD468E
	E0BD9A560CD40F7752EF0E30F6742647B3A51D40E7DDB3F2BBE536BE54C77C37164BF955C34EB8109162C30E748E649EFCDEAA92685339AD5B7837C6AAEA7139A3D7B721ED033499E0DCADBEAFF510BF33988A3864DA14F1BBFF4F3D388EE5CC2334CD106E84F054A1EDA6FF4FBD30EE2C6FDBE751EF20B0B9DF766073D271DC76BB7C1C41DC0EDB4069EA57E23FF92B473DB1B36007570E66994BC5BA4FD2AC53F91622CCE7AD752EC3BBC7AD54FCEBDB8D7A7BBB6F7864535F5F795AA71F9A587E4053B49E0257
	2A77BB32A9BEEC2DFA3F63D0B746C9A464152EC33F9F7573195C71FE72A57879AC2F11BE336BD82736F7AF458F2FD35B7B92EF97011017456D1D66715CDCDB7D6E92D37DE876F77ECD7906D434C1D2BADCC78663DB20EBEF0CC41F6815EF216E73FDE992FDB6DC0B566BBD2B19B2200D7349835AB81F56AD705F2FA8FE9F6BBE7EF34350932023DFF19F4321787D6A893827B12F67784FFD0BFA3575BC3BD75B6F75503E3682E225FABD4EF32F263876780E12A60EAB6D4539EFAB472D26FDF7982423B86EDCA352
	45F25CDAAE2FF88362FE4B6752659C97CB47B5932407G58D8FA0852396804C1B4C95F796367B784EC40B95FDC45771A1677629CA8E4036F73AB547BD4119B54F320E62367FAF5E23B2C6BCA7EDD2F98AF3AC65A93CB5C201E5BBE7A3DAC7BAABE17CB71A2FB260EECBC07A3D3E34783BA5F8CFB2DC36079D77460B7999C521998B8B667F0BF9B6837D3D87E23FC6C2E5E0879DB955F0A1842678DA61E5F7723405B418F3D4ABEAC8CE822B70E262FFD5C234FCCG1E54D17125FB7419D924CC8D2319C3B8F45A52
	20CEDBFAC5677D0B99BA6FE97CC367FEE31E477D4677E8FDCCDA7687F0DE54B4E126DB18C74E71B4FCE9F79AB6DB2D8672099D17DBF8EB734EED1B8DD5837493A32B5CC2A9CBBB49C784A079A67B3208DB18C98F1AF6B905397CC07C6D74740174836BB6957E7D52428221E83B70A6BB0DE13BD0C40ED458AE4C24A7AEEC978A5DF6479059BEAFD002DBA87C069C82D9780DF1081744C9CFEDDF45B56E97FE21752F62ACEA5D7E6DB6A8DF3B5D30F947F6EFD4CB49195806AA23F4AA3621EF5056CBDE75B6587B8C
	C355072A8F7B54D33955E86D9D349A5D22E11743C81EE8B54867BDF326A2A59CB72CD318277D9A416633A6BF923527393CC0A8G7F1BC5667019874973385465E4D1D375AB152962D71677FF587FF5D837703E5CA4C8A722E88FEE98C3C50751C93258C1675000E82FA39DA5CCDA0FC36C3574D9FA69118B506A7110E1832153EF05E3ABDEB9D5487687C8D3A79FGB40CCDC461E6130974FDF47341AD4C265B8C5E7B990E4DD708499F8BD640EF092DBA267CA6D36E293DDE3F4964781C0DBB815215FCCB0C1D3F
	EA7F080C837100CF83E067DD5C7E61EA417B19423B860CD62B5BE85D3759E6A2678BEFB9649665F3515D9F9179AE2411B225F344BFA2655E2B6079FFD0CB8788630DE8GD2B8GG1C50GGD0CB818294G94G88G88G610171B4630DE8GD2B8GG1C50GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0CB8GGGG
**end of data**/
}
}