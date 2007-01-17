package cbit.vcell.client.data;
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
		public final cbit.util.Range selectAreaRange;
		public final cbit.vcell.parser.Expression selectAreaAnalytic;
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

			cbit.util.Range rangeTemp = null;
				try{
					rangeTemp = new cbit.util.Range(Double.parseDouble(getJTextFieldAreaMinRange().getText()),Double.parseDouble(getJTextFieldAreaMaxRange().getText()));
				}catch(Throwable e){
					//Do nothing
				}
			selectAreaRange = rangeTemp;

			cbit.vcell.parser.Expression expTemp = null;
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
			cbit.gui.TitledBorderBean ivjLocalBorder2;
			ivjLocalBorder2 = new cbit.gui.TitledBorderBean();
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
			ivjJPanel10.setBorder(new cbit.gui.LineBorderBean());
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
			ivjJPanel11.setBorder(new cbit.gui.LineBorderBean());
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
			ivjJPanel2.setBorder(new cbit.gui.LineBorderBean());
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
			cbit.gui.LineBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new cbit.gui.LineBorderBean();
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
			ivjJPanel5.setBorder(new cbit.gui.LineBorderBean());
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
			ivjJPanel7.setBorder(new cbit.gui.LineBorderBean());
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
			ivjJPanel8.setBorder(new cbit.gui.LineBorderBean());
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
			ivjJPanel9.setBorder(new cbit.gui.LineBorderBean());
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
			cbit.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.TitledBorderBean();
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
	D0CB838494G88G88GDDFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8FD8D4D55AB895953554D21152C6C5C9C5C5AD35EC19D1D1D9E1D19121510A0549A69595DF796D3A3BFCF585D4A8CAD4AAB4AAAAACCCB2D4B4D4AAD0CCB4CCD2D2CCD2C6E6E086661FB3830C027AFB4FB977BD774E3DF79818DDFDFE5F73FCBC4F4B1D7B1E774FF95F731E3F775CF305120FA3664E49AD9104B9F385716F1BB90260CA9204632D53EEC1DC6D974905026C6F930005C2581779F0ED02
	34143FA597968B935C85F05F8E77957F15DC78BE7CDEAC9CF346CD029FDE7404C25A2178C7CD436BF9166B19CF6F530B0B607A8EC0876048B57A8B22FFF6F129088F947188F3F285A1281250ACDED8A662320126GF00DGED150C5E8ED71601B3B7540AF46D97C489339EF2643ECD6448E4A2A4093C3344EB39704FB9614B092E3A05C5A2CF955C6B810871F942A55FFD8D573A165E6E6FBA8F19DCAB7BBB8DE6734ACE3349E0F52D546BDC3A8D9B9A617FEE035950695AE5F0B9CC06FE03D07AA511993830004A
	9004F41097C9F1BB1DC4CFBB7CF68184FF0978889741E742EFE1C5F2E1698A346D5BAF6A443C378D5EA6242C3664AF70E2DB758AE6D35B8A4936345EE939CBE8B9FCDAC57C9D8AE96381928132810A2B128B2B81BE20653FFC4517F0EDB8645AEC33D8EC56863BFD13D96F58E1539B4C70FB4386C891F13B8C1DB607DE90D899EFDAD825111F610071350C1A0F79A4EA8D16F7742E1B0434BF0D491D2F61133428415C26AA660B0EAA49A7CC77B9DEF587D4F94623276E3D02DC377B0F137366EA688E8D1F1C97AA
	4A0912694E28A2E5FD9F0D018478BDC7144B70C994DF791504E731798B45137BC6C80B7A8AE3236D9D4CCBF37ED5C2712A311AFE883BB7B837E8A54BC345CAE5DE4A7B096C1615CC67FA1E17AD941FA843333CDCC363145CB7FD1DDCD832AA391045570A15242E262FCEAE4C81A8D913DCD885508CE08498DA03B166D8717588E32CC567F0ED6AB31975860740F8D7D43D87D7035B6550ED33BADDBAEB27E10F4EE1529DB29B1C02C8D3D3C5FCF04803EFBD0D7DCE503155E0B5B8F4AE134D8A52F596034B60F0B2
	1ACDD58B890D43C0D3DBCD9683A8305885143B3DEAB1DC4DBA276B97BBD4FB03C44152F7D511B269576D8109A0GFEB33F34FDCD7C958E764782CC64FEF8044A7B5160309A4C9BB6B45BEC3DFD76D6E209D0D2C35B835AF62482FD769A74F1EBB54135C13A3D866564FF35C8A6275154C9322FF39C41BAB649D98C57C0200F84C8G482E4178F9398D6327716D9B052A4997EB46CF41E513734A56323829D92B2CC7D328FC1247F6C89B8288DF0BED7FFEE71B0F7ACC7223050FD46033457BC0991E69F07954B15F
	8BFE2EC67DA657BC990E797D5ED5248C63402ED40059EB51775FFFD54EE2F9134EE940685D690040B3682512E0797C3C5FAE7A1648A77D53820AA7F28461DD0077C5FDC47607G9F8B2CCF7D8CE0994097G4B81D6GFC85C06A51AA00B5GEB813E916B65C600EFC9CCF09F7DC8F5972FCBAE2C8568G88820881C885903EC9AE2C84E881F083C4GA4GE4831456A69756839881C2G46832483942C07FE83208DA0GA08AA089E0B6C079066442268D9883512E78B9125D4AB2B895FE709697BF7A081B7F834F
	BA34D078380F14A5A9C7E55DC19AD2075EBA09656AADFF6B7FBF70517F96E0ED575A1524FCE02CEAF5B59A6C862B9EFEB845343A15249C45FE364B46F9B6D13C9D7A654D5091BAB93EE9A5A9E71D8B863F077ADC065D86174BE46D1652DB28AEE75FA13B5425232E3DABC9AC598620DF66746DABC91F56E933DA8D349165ACE27A815A2742085B82195EEC6B333A98DE7FB519FB749A0E08FD3A716BF98269134DFDE4FC404673B1BA921FF6074982CDB399E288719BC8FFFB5B42D2190F6C00A37312D038C6ED40
	7EF260B36CAFAB0EDEA7449F3467EA0D7D422E7EFDEE41C656CF15ED147ACB1627FD555AF9BFC87BF6916FE4F99D5CC97C20B13D20F5F031F0739CD22FB67539DCB66BD6072D4F4E48FE6833F68AC89331E82996CD03DE3F4BE031C9320ADC9FAB686CB0925219F73BF4AEA7C5C0110A7D613FD6939B6A403696G63C66C8F27D613F49C0B089D61E61B594C0A915222B792DF1739CAE47E8A5848FC7D8ECFEF72116E721A4E7211300A0CC91C0643FD8698A212C1550FBCA69EDB35C81136C70C8F167E64AA929F
	E659F82C250E689C5B0FF351D9223E22BAF62D28E33A995D4F83C8978E7731GA9A2DD49370C2E64BBC2772E9B690C008FG08GC887903EE3F491146EC5CE57719D6B3B5D5FB17AE0382E64722EB802F4D9F01F7FBD648B2063FB25BC57D1248B781E0D815CA23D273C4AC1248B7B1E69A372E2D472DE66F4C95F333104F67E2E9AC23A4C6F257C8975CAF946E3C8D7DEAF0EC9B473D7F69C69EA6BA5F9ADAAF94FF2BAFBBD9B5BE84B9BF3826902E472A2D4725A85240B2FE7E3A4EDF945F310AED5A6AFDBA5EF
	8A27ABEEE0E3ADEDF96766A2DDA521EB904B37C1A92FA50F17EF839B3391F9045ED3DEFEBEAFDF193C98153C07B8DDD2839B7BE94BBBBE1717AF11310949AB5F241457B88F696AB731B1A411C768D19E2BC733B9DD5BA696A776CD2C9E45EC1257233879C8D73099748154838C8104EF166B3D20G69E2B63371AA51CB6893B64B6DC82AC03AB4193CD9AAF9E316A1DDC9A39B77AA65B1BB36FF0EF4D5C0578820EFE4F6C4B6B2F92C2DFFEDB969FF68BAD30F2EDF67DE699C0086F743066DADBAE8CD59FA52A70B
	8AC4FAE18B2E9B6DF817344535F05F82E05C02FD5563EF633AD1E8EF14509A131267EDCDACE08B4BC76496292F2A8C9E48E3ED7D169A22B38352E6ED41F6FE57EA795C35416934F51A6834D9ECC3672C1177E58CB7EF4D1BF06DB2B91C2EEDB058F0E3FBDB340664174C2BC534680F7FD9792126BFE4BD1400EBB247FE105B9A3760D86EDBDEECAD780159D87903E4EBED705F72585AC6BCDD73A07A0D109E81407A46AF86885EE4B5118E51F454D0391C65F17B0F65F0552B7A67035BE424FBD89E2FDADCAF66A1
	71262B043A9387F2355671029698F3D3453C89DB25BCA6A5CF4B4DGE079BCBA0079EC861AC0G56AFF6FB740BF1004FGA8E8C2BB268F2C062B0EF646AB592545606832B9AC86FDA57172CA5D00EBA59D932EEC2049DB486F65D8A67A2EA63876ECB69ABAFBB7595CE86E4EFE0303AC079CECD008EC76AEF36372BD4C166F5CE8CB9964333589EDD9EA135B928C7871GE95C162B5D4416CE234E5AEDD86904C10AE1B37D4D8C9143C5544BD25096A63F5FA2171FAD4AAF5D06726F9AA0739903158C8174E21C5D
	78113C8C8BF4E7EA16E170735D39D55BD85935EE1345599353F21B1F40B2ACB9D2A43031D7B85024EC437566126F48150ED9859C1B855AC8FDF25298B368D9DE0676E1DE8292E305524567E96605C051F6F16DE63B14177C6953F2CB27E3DE92F8DE8201A6EEBB1641E2D5994C0234529F51C7DF9FA5F3B4134BE0D9C97E6DD616022C6837C13AD8GCC76999E322BC0E6AB009B00656932231827E040CD64781F87911F83381AE64CEB2EDBAE4F5C4C4AB32499795E9FC23EC84025F17C439CDF32A33930F9874A
	7B48A3BE82F7B05B23F7A05F854710AF9EF033B9FE4BF144D77F846D71CFA84FE65532B762A764FB6B84724582AE0B636F66780A1D49056DBBD15ECC0B52DEA2AFF4A7721DA9A0DF946052B9FE7D9C441736C01F5102724EB3EB498BEAC13EB7B8DFB860D2B87ECF3908AF7A997A371FD15EAA33B2F606A0AD02273F5C2374AFC90F7D99659E49C339890093F6A17E2BFC4457824E3E8B65AD6FD556E7A2AF0A733D467962GB70B632F1D0B782A5D50B76FC6F9E1A6E57E43A1AD1E27DF234ABFC9CF590DF2AD73
	D0EEA6604ADB917F49FCEC6F5AG97520AF5A9E87E3F57B6BF7B76F652B6336E3141EEB79F91A7FA8E03EE97291223EC1DF13D7F2F73F9B9C0DE05DF507E1F8CCA7F54C2DA7BAFE8CFF3017FEDC3D417322E9059F6AE5BE910374DAC1F938AF89B86F4F11C76768AF95B49E87FD1013433002EEA8F5AB45E20ACD322D73F075745E5486786DC8C47BBBF47B24D84DC79DE147716D3599F37C15A50DE4C5BDFBE7F774A7C1B5FF68A9A7DB14C5CBB8D4461A6274B54699CED410B6B550B144F47C276CAEB6F11325F
	886C3951BDF4BCBAFEAFFBEEB4176393A9BED106E73E5B7DAE7AAE9D529A5B70D94744F5E4BC9C8577938152GE681146C03B6FF9F0EC3C357103541E11FA5ED333AE0182AB36F76D8FC42E7BAB5249CFA8DC7447B1FEA64FDAF4B476ADAD206AD20538C9088908E904773B03946E79E3638B573407C7CC03F524FE97BBCFDBBDBF14F78D2D4FCAB9577E53FFA72B1FB02561379C88D2435G9881828122GA682E47E0AF6D52C7D5627DDA4E2DAED1B8157E43375EED62E57713C868C78E76379AA3E0F9477857B
	BD79D83D78EE8F31C1E3AE05B5C5DC73DBF0D83966D78E729A815AG581C6BF2974E396C7B7119C6235BBFFBF6297814E59625E94FFB1BBE5236E717CEEF3231A6DF90ED3A412234A99EE4E6G4C66B67DCC4713A4CF456DB86E6CB6FB73194DF248C6FC46F8EF3420BF1A5A312E3E310144D4G5C07834482A4814C82A87E8D6A6BEF98D3FDEBB70EB026D813240EA8664725C7FC6D9B5076FF25CF3E565F346A4C09BAE21F995202GA2G62G5281F2G4A8F20FD8F2E5D644BBE7E5899EC4475F20D0A43727B
	281FF62679642BBD604947E267BCD56C34G1D8BA068G464EF69B16FF44810C1DE515E55A3153EAF03BFEB0994CFAA67FD533167CD400ACAEFF8215CF66B87987313E0DBB6A1F9FEE7649D7F7D00BEF6F20FF7A8CBE79DA8FEA556F573A3C2C7DB46B8E615A4FFAB77A5D4C7D726110FF79DC6113AFD253AF0F7B296F991FFCF11AFED930FB0936DF3049935B24DFAC98B3A95CB7E30F7917572BFD7295F5E8717DF85CBFFD7BFC72D5A874B11B2F31224DF59DE87342937E6561E31FFCAECD1B6F77D35F93BE79
	02B5ED7E480A7DD6A43779F702FFF93844A7DF1A264D5F4F71CF5F8E1FFC33B4ED1EE8C61B0BF4E8735CDC7F723058A7DF23CE0B6FF6BF75BD68132FDD27558F1D66549A433800B6C8077D04117BA502631675E21DC8653EBA1167DF5E4F7349D7FCC80BEFCD3EFF7A9AFC72D59E520A0F0EDE6C976A8F214D7F70B38F73677A62F3EB5AFC23CFBEEDFDF778648BB924959F87CCDA71918534F107B0969EB321DFD2B8EEDA8F46C7D1A77A4AB94F3F3C1F66132F31D30B6F0B797E4D9D562A7814F321F6CDFDAF2A
	78C6E65FBFFC7239BA354AE6F317D6598483EDC4A716431986AC1B719C77CC976232F9594497781777C7FD72D56A35785E2870CF5F5B4BFC71556B356A6EAB860C47B6BD5AFC3FCFD9DE7AD41FFCA11AB69F795C3FF8BCDB45270C47A83DD6FCCCD0356DE44E9D8F34A9FA0C05CDCE0C05AC40317D305F52E71F95066442DA8362161650F798605EE5C07FBD78AE4105417DF80ECB2638C48353A50E3B5EED936934FCB55E8BFE2440F4B8288E2DFD0F6F885AFB68C68362BE62F55E77F1252C535E477517F55A7B
	385257ADE4F810A15FFD0D69196B065F67153D4E7BBE2F8D355E7359D02B1D4F2D355A795CD1BBFCBEFF2975164F8FE9F9CCFAEF8165F7BA8DCE3A60C036D809E9B3DDE4CF15EA6F59CFB0EB9770F95C7F74970AB4DBDC364D2B385E8B57FB377112755AB6DE311EBC17F75A7A9C1D86259D572CAF65E952A281D9AEA759E1B4FF247CAE1D235B2072432D141F25F9633FFB3DB7DFB1DBEF9B105B3A1A57635D5D4AFACC7600E7C33D496F42FA3C75B00E514A3AF03FC28CFD1721956E4DG01DDFC0E6F52EA3F42
	3BD8BD0C69D257C7163FC137ACFFBB56707CDD5E234C9F297F93C1CEAA4F5F8C8B66AF1B67CFE7256FE0F4A797568334F5E37E7658C87E6C5D1E7988946F433A5579E3FC37D806EB172301AFDE414B4A25F02377D8AA5E289DCB6FED749ECB9FEE749DCB1FEF749ECBDFED749DCB6BB68E9FCBBF9FF9CBAC2B16AE074D5232C614FDCE1DF7DBC36A34ED9DD3675D564B6AFC5BFACD1DF7DBEF2C73ED6B2DF54416C3A63D49A16EEEE4B64EBDAA0F479A9E0F33D471C846C5A9D076195D980F777335D541087158CE
	6331966EDBG0CC60C471DB62D388AB032D88AB52A63116978126B0862BA92280EF4381F8DD0ECC29DF7EB6AC028843ABA0096935325B77167DAC7BD4B39C6744D7903F25F2C65F564BB553AB88137A8B758642D6E378EF96AD9AB628B0E49752C63FAFE552C0B91227CD838A6G244974B1F90F9E735433CE441FFEDC2E679B2E67866BF0FE4B867979BDD066BDCCCFD58F7A2D6904271EEFC4BDB9A764FAEA391E4FB475540BF2DB61EA6C71749D13F72F602927D644774F116BD94F75CCB2EB699992E50740B5
	D2D3CFDD2E271E75A2BEBB77CD191ED555981B37BB31BD9E5F0331696EA531198D77853DB0FE6B455834E8662936176523992E9D3DDA6356AFFB3578DCA2ED90DC4381E2FA15B1F0AB5D0FA059322A1A61FB72BDECD9455702B575A0CC8479293DDABE2B1E2B50330A61B366FA68D90DBE9BE3C21FE575224F269B090C8AB354CF00D6B37AAC452415A7331965A3406C2D8FFB36C70BAFD4240F02EB9CC012D969336B67A9ECD94D7087664B4794EBB87D27859EB62EC19B87795CB0430CB6DE2CA7324BAC4905B5
	G4D963471DC03D6DE759616B737451B0D99DD4315D7B070C5D8344A6BD8054246B58C3FEE1907ADB528E716E67EE2C55989F0CD834836A8FD1933CC21270661E3BE177B1274B74CE72F723A14EFC51F35BA683EFD38EF85B0DB51E7DB1DDAF98A340AE39B2B5776F791F93F422CBBE26BF301DE9B51513B887B39E8601B881085A0585878102D6B5FBCC05A05AE074E8243035DF6C327B00B722EAAC15E8E38F781048244822458300D5C3A0A6CBDE53BE2598E9768FB49A3B6B1CFC7BE1E4F53D709FDEF7EFBDA
	E355BABB74DDF66CFBFF570D7EB25A51DF0B8F93FFC5407DF800A4BB7A2B79305CDF19004F31CBBEAABE6CEDEFC485FDF7366230F2EF445794DFFBD839B762D6A75F6789E9B107F15EBB01BE13AEF1A497D6B89077C19F4E052B9DE853149E34A94001B63DEC267BB360BE89A05301B6E5FA740D029364BAA5BBAA1D5EEC1ACC478EF5CE1677799CBF1D62DBE5F8E6D3B74513FBB324A5B9B17F967A5C0F6C812FF1A16ED60AEB027BB60E7B400EF66AB9EE1285F1319CF76DG41113D6779FD680F2ADE74C7E99F
	7AE38A75C78B5C9B81827A509F8F29468AE1FD128F227B3C79A3095A975F27AC6329941F5227AC63C3D63EF788521A7BB17FDD547698380F673807EDE8E7E2BF6673EBD5BE4B86243CD58FF84BE71D0D68ED9CD03E033E0D62BB86146F20BFCA73C96E07A0ADFDG73B4056729510D38E83AB69F8A77519C37550265914FF14194178277199C778BFD4E198377C5C79097B008BACAB96EB065B5423D1B632670B88864385369B32394384F6438CB4D1817D99CF72F95F98B0EA26EAF8634230D63469910574CF119
	9497G77219C37410AF6C4F15C25DCC7AC472D63F9499FC4DCBA451542FDA547ED613655F0DC2995F98387B19E6E72E84B43G9FBDA845C07C2037F84822632C14C1E5BD1ECD71D9034AFAFC3C976BF17E90541DA14CD3A04DFBA25C27F15C73E64CFBA647BDC25B265AE39073479057C06BE7875C5BB96E1ADE649D6238336918A7916E53B9EE3E89FD1145F1C31CD7B80E3831BDC857F29CFDF6294738C88FF877F149CF41473D796C97936D9F0EAB6BFA8745479ED7567519C62C6B8910D6F5827374A24DFB88
	5CC7F0DCCB8F66BD1A637E51033E4D6138DF390D858108F3D0F9EDF0EF66384B385D6EG34BBC0B566898E10EC0D8870E6F721015A97200C15F7A9BEB1C099AB176AB1D6B2A02D69B44C5315345EC443FD9C474D6BC2FB92B86EDF9CD7FABA625AA9AFF9A74748F1EFF5213CA10EB3F35EA00EB3D0DCAA5CE7F15CA2BD4A1BFDBA7A676F9EF529688C18FF1CA17924668CEF7EC920632C26B314F1718445371F210C0BDF9D9897F6C8CBB98373F440097DFCEDA0626EF67276B690771FDE34087093192100CB8C44
	F1562A8E0FFDB9E44B67068DDB9DA67DA6DDF7B39D3531FEF1AADDDBA77C79E7E23FD83818766BF05F8AE0BE935FABF2FF0E6F5AC4DFF31550942D7DDED1FA6B315C40B34579411952FB87593D53F2194E3DA528B383524B4E224F05CA647BE549F86EE0AE5BA34752EF73C8A7EBCD7B6748531F77C8A7793DE22EBC7D03F7656944C7CF8AAC1D597670B234AD676198417C67C02F6788694F92670B62B5A46312B999D30F66B15B6A5E43F1C67CD9684F1934CD18CD72F1B6346BE7E30C05F44BE32C9E702DE7CB
	F1E5BC5BDB0C1DCD4726C3E7AB6B60C5949FFA36328EAEB90CF5B09A520A03B01EBA699A2D8B6E03B8EE1A0356A3380F67383DAE0C3B0CA04C7B1794D7F3CEF2E17BB908DBCBF1643D57A00E0B9B760C8E6477166E8B180E49436A70F6E641C8C11C53B9487B65AE035B95F58EB97BEAE5A3091A72F309FFD677B50A34D99A364C9124B3B0888840C360B391DFF2AEABAFAC330542D40FB9379152074E1564061C6BADAE8ED9C8B9C70A725E62F8BB45CF1461D9DC4C2263DFCA87E9CD3F4372FE030EB72261BE1E
	630E5031DE865C4F66385778F83CAC984B1B0DF54DF09F82904371B3A9BEC0FC4F193CCBAC2E65D8FD1D612284146DADBDC61646F41CB5EA9D3EE2E63E6CF7EAB06D67ECF85ED7C2B04ED79F31F946CC5796D94C58B1E64A4E739DB355C0C360DD915FF81E94B3CCDE3A856535F81107B6112B9EE85C22BC028B1649E376CC9EC0FB424E4332DA65312E958578B8192EA48D3DAC7E3653784BB8CF59A77EC271334FD37609E9E66C930B4E0771607998EF53591C816EB3B9EEBF1F03947D9E73B9050D3561FE0863
	26D3DC3E785EB943993D4685619DEDBBB1246091770AF855616D5970F0200CC9F19DCA91E7677CA0455933B60C0BC88D1B14F1B69EE888E01CA57EDE0A8B666F9FAC588E247E9E4BA01E4FEDF2383FB75948B98C4DA1D0FEG01GE1A152738B1256A13EE3CF5E0BEF61E512B2ECDDA5F2FC7988F54AF1E162BD53715230BA667B106F2DFD40B324185FDF62FE0F5348F31E426FC9A11E75B1A3C472BBAB637B4C241D36182CA6CB1FE5376928C12C27D3697CAA9B68F3G984EEE4171D5658558960D33903BBAED
	66BE0B55A972CE604FC06BB81D596D99CB0FEF15E2E96CA05A54FC01EF1B5C32B60660BBAED05A94E47154B5E71B246BA55E1E19C720CB993781AADD17D849FA25C56768B6D9216D227ACC6435B95197EC2FA5218F86DE1607272C1807880DBC4CD764A196E892AE104AB06D82E51B1AC7679E19001FF58116D522817516DF08E57006C1DDD66FF5E1D955G9D4BFF700F123FEE61F9ED3C50F7BDD716CD5B054A3C9E647E5073FC7D5D6CD9DE931BA57D6D3C3C5C9A7A67AA7487838D81549F21525FE0C57D5197
	E2BBB359096355F80EEB6D4371EA8647BD514F674597E13B72AADD33E8037BA100F00EFF1D62491996647C8BF226864387760EF44CC0E412EB78C55E5B26210B347A064BC62CA3CF46B73A3E61D2C5194D5CA11519404BEC22C65E3D0DDB314CD2AFD256310C9E12BFFCB71315633FFA78DC6FA20C2336DE4FBC156F1472F4E7AF66E936C61E14753EA8947A7DD0A9CF65214ABCDDEED2577B4DAD820F33EA93662FAA946B7DC5BC8F75213E47D22D2112DF687991214AD80E67BEF005E20C5EC2758641FDB8472D
	6A65F3AF0E3B012F1FE504723395A9EF4D4550EFDE0C38AE368636091D3BA276F3C60CAFC2AB372165E27F67DC1EB1BC4F04BA146319FFF70F1B944339BD0AF1744FD23CF47770FEC243A6DFB19CF031D4D66258E917A47BB00F03E0AF32733C48266FC8DD2C0C45F3B4E2B1FA37940B8F70B8093E182FD952F6BF9E6ED3B86EBDA34644ECDE7E8B687ADB531864C2B3C0489844FFC071656239B864AC9B063FD26F2D6D8991E578D3DECDE364632C89DEF54C7FB762A3CFF6DDAC0CB7F846C66BAFD279FD584547
	E09A76A8E3A389E888603CBCE30CF20E35289B65E5FB11A7EF977247B2F9988F25E3156D42393C6F2F980BFD7A9DFB247C1F4173DFBBF684E3C8C57E1B47AA63EF28CB9DFFCF6F1562EF239E7353B696E36D9D977639E60E8B62FDEE8847658E70679BE3B1469E25EBC979E1B0F78DC35CD394C74EC2EA67388E07D61CA05FFF261DBA2229635FDD9BC290472F7B24321B6244329B5230814B85E3C559AE0504A95704AED13D13BFB23B2F7513AFFD54FC6233CE3AF69CAE4BBF43E7D0FC148C4F667A8B96637AF5
	AC2415DE026F95E76CA34F6D0760BE8CA086A081A093A07F8F784E6D5EEF2B859F6F5C365A6C4D06FE03D936D5185957DEB2DA7B58BA71BD73F11DB8F642754278339A3D1EB7D57A87D66E356235F246A5F3481559B7301F58E70434C000A800F8009400ACEE5F955FD6B17B3668CD2E8617CB57E92427EA21F1FBCC068189CDB6D3086F933D6771BC7FD22823174A1E67DF2A0EC7D6C67BFB683A7F254AF51AAE0AEF39D439CE73128957E9741016F0A9FF7ECE77D3D405A797560773673DF4FC2207FBB747AD64
	637470F0EC8B4E23EB55D9F0DFF4990CE5AEC37CA594DFF399BB570A1C2541709F9F1657EB42632D7C4A07C9C3A0FA55F52156C347C857853D01F21C445A66ACA7E91B8F599CF4B3AA5BCF934C4FA8E8053C1B8142G22C59FB03E5EBE75BE1CF97CBD0B091731FDB8E9F04D9679D87BE4F5E17BD4F0396F3E318CE82AAF172DBFDE2E6CDBEEDECC62FA3B377341B85D2F7BBF5622A3B45231A50237B3D4C87EDBAE4767D10F6D17FF7B21788F66DC4C13529E3DE8237DF269F994FBDE5359077B4BA2A16DE31AC7
	7009A07D31367D175F48EF26F7FA913681E5B70C939A1FEC4C7D4C0B5EB4D1DF5595125ED0EBF99E0B7FD7FBF01DDDFF855609B2AB2E2F85F15C983E4699FD85DF3B24E7C06540FDF1C4F2E185C0ADC0F3842633B34E460BE7A1117309487B7664CC9B167ECFB346F43148CBFED7C4F846C2F30477BA13F30577B426E33185F54C952475B1E59DF9EB94F5C5DDEFEC6F296BCD25156FA50BE075468D576088F6AE9863FBF3093A5EDCE5C33E4888D6EF62601AA47231FA7366E12CB7199A3ED1561B9C20A93E1255
	9BF2DFF125325EB4BBC91A7CB40F061D5BF6682CFDBAB327792A1C6EFB4353B85831CEFAD31FD3CC8F5DCCFC48CE8C2067BD0978DB583A30234F4062B6E2C0DEDFEA5FBCDD738CE38255D7B25B9A45EB4E3BFF407AE2477AB2F465F075E55EC1721B71AC34A2CF2CA6CF76775E65AC6F90389F3286509ED6AF1A68FCB98BE48AD7E1DDF9500C4F88AAB8AE11BFB7EF3A8A633F052EFD0641FDB4476D27B8F2569BB97FCC5C7FCA6B892171552EA3040D021669789755B11AF168E81E9DB05FBDEDD6477B1B3C1E44
	DF4562BD952ED9D731736ED83CBB78BAF67EB85F71DE8AB4D56324B9C87DB8E53C7FB7793810B25EE9503274337713314FF66FE71909B1351847E17338616230D9C763105AEB63E71C840EE3760643B5E69CABEFE66F13FC9DB5410B3D72B9FF9A50E40F136AF7FE24525EBD6D643BA552E9A8440E9DA6ABB3857744FFCB772D527ADB0B677E8C4E63B8245B7C1906AC1DDB14A51E13407DD29C29DD5718DFEC07A49F5D2797F8FE6BAC64DDCE2BE1201C1F945688FD3315FC582432263A2AFCF575F224BD5BC274
	F4694CCE29FFFF5CC2ECA77B3AD9FE9286B1BFCD11B847FD4EA0881E67BA1074G1EDE58AD65E72AD92E03713C494F21996F45C66F71D0EB14FE6FB7C9FA428CA337FB06C1ED77D7DD1EF64F60F6E5F33B0E77C87A7AB56CBA0F3F1FD3F975F0B1FDE52FA4274D6AA10746F91E0B77FFD70B7D9FDC032FE6E381C6F3FA9F6F6B2E967BBA38A609B42CAE3C504F7B3A2BFD2F6967GCD71B5323E6E9AE5DD587033C6DF374B504D4E0797E36AC3F5DF47A8D8FA4097E4DF1317760192B2DF2E623E2C3E064715C572
	5B22DE357FDF6667A9982F994E7FD5D6C94ED613DA4EC4AEA7C2D38E56411DB6C94E13BD6A181AE4D20FED52797987295730324D06EB7E35EC0C47E8FE6DC51A32EBD959564035C924E1E5FB2F994B3663DA5F4F36DCC093F42DD4366157AA4B76065EE2C559B61B2CE21B3E68F831D4E6845DEA0302E73E8A6BC1DFC5DD0B6564A047BB0B3E3C0A7BB21D27DFFCD8725BB48D3F5D54256E6B6E67E79314C6B11FD44335B10A0D0919CF3EB222CF5A233C377D0562558EB401D112CF422214BE797D1C7755715EE0
	55190F40A4C724F9CD23CD67A4A24DBF86BF729A7392AD7357CF7CFD19482861E2F83CC3727166EE64C9536441F87D40A5797DCA03BA6EE3396E126B302C3E6B13F8F66955ED55B73C2DEA3E0E45339E2E6E6B589C1F515C4C5B2A106BD859C541B5CE24E1E537E4G4BAE65BA5FEB2519C013F31DB41FAD3EDED9F6731D650246BCD5F13EA6237D467D211C1653B8F768FA8D7438F2729E2CF876346A1CEEE2C35975781E42FCFE66E0757568C3DD3FA060D8F17AE269778B325F6FAE9E6D9E2751G533172246A
	E0EBDEAF6D737EBE702CFD5A6F837FF31F76D981422F431FD9D0702B77B38B967CCA4AG3FCD43566CD68D1032B3C239841232810883C83A9E576BAE7B75D3012E57E92C55ED51F713771149176BDC28BFE03FF7DB4F5E6F7BFBF7A17B49FBE607EC50ACD808FC7EFE74FED2DEAE1B1DA2996E527D44C78ED3375145C8C57C157BC93C198DDD944D66CBED6F613AC45675FC2FDFA92ECB945F40771AAF656FA55C0073A59DED4F8361BE0C634C94C75EF90E663857CCA30DA5A247DBDA2017B4262328E724BA061B
	FBF91BEB313AFFD7373A0F1E4C4FC7C8027CE592FFC5A797D6C633B3C8447DB8A6F59FB501776D75512C1DEB032BD964E36DDCA59F1385C67B1E1F0481CDF434748CAABEDA594ECD355171966BDB301523E7E230749F1A24F76A709D5E0CAE89D7AB36EB09071567A713BCA7C663BE75D83AFECB785381D7FAA36ED39F330BFF6B01FEE3353B4F34E18399A3EC22AE5DE450114196EB9BF38E637329169BF1EF7517F47FFC905CC7G0C3F91752DF462F305A440955C046DEB3F93E5545F04B2A2A92D9B6EC3G22
	B86D953CAD0E3FC93BAF755E3FCEA41F339064E75A901F4D3E891FEF9CBEC252D99DDC553E8C2EFBF4BD62938C325CEFF3E04B319D66A3BB8F75C0FF5F2A6B6631ED6C132F71D77F9146B2FF9CFE5D01698DBFCA62278DE8DDGE1GB1G938152G328172E340FE00EA00A600F6GBBC0A0C0A8C094C0DC8C36F9FF78ED39373C83D2FC6100B6FC8E1D09B4FDE22CFF404728C9B12C5F4F04EBCE8CBB9B0151EC616B93A5B7337AD085577A1B998D2B8FB7727509361BFD2FCF1801A660E629BE045E2CDA0F5BA2
	45B61EA9F0FB0304437772BFDBD640F178BE78F707E5B8715D6DB7974BF02BD9EC36D831AEC4007E541BB15E42781E2762DBB0B69F2034CDF05F81603E85637BA19B46F7886092EEC1990B6CA8C378934A283753F7CC613E854078A7245DF8184F3D7FB45A78F68F89BC26169DA36963C1C64A1FB0AE267F36E21838E8F1586C86076B8843522FF0E1DC5B65F1BD8B6495448E9F570C6FBD0F73904A00275A879FAB1F033D6A763B1F47E6E3AC0B4DF6385AE3599ABA2399F4A0CDD0AC0B4D8838460AB4ACB6E770
	E740099AF9D10EC95301E6D62CB41FA87833B2B6ADDFCBF138C644CDB0CB38F562F33955D60C0D9210517CE7AC6F23BC3E827F0C3191C5E96360BE85A00B530E63EBAC79370EF40D856789E30E8B3CDCCABD4A338EE4355CBAFC39B09B46DA5086BD50075F4A4F0A63FBDBD3EEC59B5E26B6945D16DCD881D0F79B5296F09BDAEF9B2D8D371F10EC784C4306E01095F15BC8E272910FB301E301A7E1C4FC1EE7FB2481CF76087896F81C5912BF9E7CB0FEA4FC199E7A2A0027FEC4FC97FA3CFF5C8ABCC69FFC2CEC
	D738307D9B827A5871D8DECE9716ED56F8AC5BAB59FB7337C33982345E0E6D5F75FD989FE640C55E0EB2DE6DC399E937238C761D6B128950FFGB4CEC05A8F86B0BE5AA70C367DCB932478D8676107D01095B5E1A47EFB4D63FD0AB860C99A915FA48F3E8C60196D030F792C20877DDEF48778618E74C38B5FF7B6F4877A6CB8250D057BC4000CBB506F2777225FF3GD79307B29E6AC59966B814415E9F0E047BB80094CE7B1A997D1E95B7DA3F3FB1C7727BE59E6FC9D55C19DCD8FB67C87CF72D077F1A012743
	871F385F218BEDF781FD741DE84F5AEE3E4775CE343D331B1ED1F29778882079AE24BDE0C45B75F70DF66D7A53BC49765D9E6F6F06032C183BC6E2FB1347BB079301A7F5C4FC37FB684B829E616E11786CA02F6BA5C05FFCB77A61DADE57836FC61FCD2634F1F01F8210F5B7465CD33C2E6747C3BB900FB2BE69677BEA63D146CEDA572361FEA2C09A275D4E6B7A2C7851465C967258913F01604231FC55BDB817AD72B83FCE8FF877BD437BC67CB67D007779FF7D006F797FB6EA1381666D2DC76C6259E4BF8D10
	710C2D2B4BE9606B8C3B870AB9EE1B334566B41103D358B866ACFEBECA70BDEC2E9A8957717730B373984DA7CB78D87D9EB6564904EB0EC84346BA67AC4531CE493D3E476115C0D3F72FB48EEF39D7B956B116C8639ABCC36C4D2D92EE35F83E0BC92FF556DFBB48335F0B7173BF5D9803C1000B3F97671E875C244C95F34FD6134BEC504BE61F2C5C3750FACBE44C3E97E36DA97ACEFF75FDB0CF8268388F75BDEB42F8F583AE66BE0C41EA5EB7E55E07B28E5058AE1F88ED97C04BC4245D574F1FC5CC9CEDBBC1
	DF7591731D6740F80D180871BA41638C9BADC81D28358FAA41A71F729CD846B747615FDE424B695CFE56C465DE42EBA9DE38DF3917305A01FEAF02B47B7DE8EF0D6699C73E5B3CCDFE728D0E1A0F59D5CA5B3F147B1576FEC27169AAFB2F67EB9CD9105672G5A9B69519E4582BE61011134715AE7C125093C59F04DFF50D38EDB87BCC1633A74C116BF71AC900A2B96F12C9D0AB96A3D3D3B6D286F76AE7E2876FA6783C7556B1D8F9F55DE6FFC7C28FC3D1361269CA5752C5BE1227B55456FD9F35CBE0ECB27B2
	89EE2FC96FB2F27CF3B4DF845FE4A08A911FC573B5C0F1EDA26E3FE438FDA26EEFC7C91B8DA69BED8E647D57D15269F4194CE6F67F8625B1D92189677E489FA4F9B459F532FC97F25CBE9EA71BFB317DEB067288FC101FFB550BEDDA5C03589E19683CEB965C97A540FCAA01FF530E4FA96BG67CAC099FF165E53CAC099B3A8ED865C67G14CCC25A2779FC2CEA52E867E3A1944562F44F30E7ACFAEF3F74BE79345B2F4BFC72E957678DF40DC6BFC9D91FB7D33CFD1232BEA7F29F8FC1DA7AA42C4FC9431E4569
	3DBEBF6DA75F7BF67F6C0D256BCC0D8FA96D3D1362DB9ED25A7B039D6DED07347807505EFA0FFE2662E1689B9F9E49F86A4BE364B7FF5E431615F00D7598D91F665F71251FAC5E8D0456EE318D33EB34E1F6F99B46F0DFD955F42B2DF2BAE65B65D21D033C87BE0CF560B9BECF0AFB986BCB89FD5FFD965C97A5C21DCB44BAB70E77E7F500F3A5220C979D3C4EA5220CA534AF48007B9C0012C71036042FE1D7BDB25AF1C484FDBFD65C83BD6ABA4078FE70136F041FF52E0C5539C714B1380C5539C714B1F813D4
	67A0AD7D910C41A80F3A5378E8F2E15B234347A00B052FB5622846A30E44B6E55833213D7B6586BF79B2C64D477CF69ABB4F63D125BF4F25F87723CAFFAE667E8C04344CC7511FEFFB783379B198AFBFB692FFEE507067779A75B2E3D0CD777C201AEE03C67D7D5E2A267B5C2626DBE1D3533D21A1AFDF036EBA0B1A6EA60BBAAED6E91E05AE876D3E6DFB1FFC5AFD1B5BA71FF6DC3CCB67AD2E4714F171914587BC260C0BEBDD98974110167D9846C5CC1FFF71FC171FFCFF9FB59F33AB142EAB34A6A96D3D0C62
	BB121476FE5E4F57A7A1ADA9896DAD77782EE54D63B0F7FCFCA475E007C67C6C3228636CCD331AEE01D9CD175E2B267BEB2FBA9E5F51BC7F57F7BC167964530E47DFFC72E9174F2BF43D327DF1E5797C0B620D0FAB4BA730072F65C1DA6A63D8BEC1431E5D6CBD2E4678497758287918DDE6BAE628CBD65A5BC771CD49CAFBDF676B362D10B6BE996D1D6DF116F65993490555CF0CA49EE3FB5471F3FB0FBA4E6E5208337BB4626C35E14FE0766E3FB95D7E71ED98B59F736F13F4AD297189255F53A93E6589255F
	CD5DFC0C8BE971CF205FBB0D9EE35C949863260C446FBDDDEAFF5A3A54FEBF582D2653F72B6912B464CD6ED2170F414FFE4969E73F946DE73F340D76BF4DA94A7259C571EDA94A72194A7BA5BD24A524E079BC63E77F72121FFC1F2B3E69BCB2FB6F237DCC55E4253D0994DFBBD9E96FAF3CDFEA043428497CCCA60FFE29E8CAF2E17914112C995D22D9CEB5A2EF93DC5B27E82D99FD66D247DD25CBBD0F523F0F73A8BB48091A02731A6877F16DA2FD8A5F67D0CAE8CB1F04B68C2069C91CC7EDDB02B6F7GAE6C
	C914F141921CC7A5BD09B26E26348529E0BFC0EDAA7FB66DD21CC735240EF69EF59FBD27074939D20F6B31C1292873D57ADCA99E6ED38132D331CC720C7E553905BE79346B5CF61FFCF2A8F8AA3910455A8E3A5EDD7614B28677D0FC65D34A98FC460865D183E9E1CF213DAF0F3A4EB33E9CBF79567AB996CC26EDC90E4A5E2994DF7097253DFA7E7C2A84525CFFC1FB8FF85479B440E77FE5A4F52EC0F3BDBDFFAA63AD05EB55D4CFB96C7BD6710B491926A62B59E4B510AF37EFB5D8394CC83A9F1C7D3ED176FB
	1D6639897FA9905B1ED32063A317E45397325FB1431E9D7A1F51BD69246AE0ED679D9A6DE93CCB5D5F33F30549B95275D355678AB7F3DCA74511B32343276273B25D67C582DFC323CB608CDF66FEDB42639238E87BB61F63DB6DB6EC2FCFBE6DB6ACE4D83E18296A7A456A6EB4F6C66AD4E51DFE0E621326AA6B340D6FCFCA03347AB47ECDD1BF573BCE7313EF021F6BE4FB69FAD8E41A525E8E0A0FC9D35A7BBCDF5709033472E9E86F8C0FF51DC04007CD9BC99BD6CF5FFD92E3C476BBF954E7350E5EE7AF1E82
	9D936C12CD7749FEB75BCF3EEE43C955A11E89FDD85D5E349FD64F9BA27D2C7F3778D97FDF74F3BE9DC4674D515314756182764D14E94A7A30540C75E1A224D5BE0D75613AD1BF6BE6FC377949176D67337573687302602715760E217830271576FE4C1FB5C7C2DA41B3E86F920F31179970814F0C247EFFAA2B73ABE43FE3CFC15DFC64246A907BD40D756309B6F57DB85BAA599EA27B3D649478E175A970C305069F16EB786103BE49768A596F9BDDA75F8F8F1C829DFF103D43B7CE763B2A7F646B5EF2D2F530
	B27DE6C0DD4EF5836AF21E5DA359BE27D77AFD6E085F55725F8F63CE018E07D932E9D076BB5FFC72F5FFF8D2F5084F92B41EC314E8BC471A56AD597E1C6C372D7B647BA168246AE0B69FB52A7DF042287643CED9DB56A67BBD7D9434B97FBC85BAA6495A32E4596F7D2720DDF31D02F62DCB23DD6B9DD04F2BA75139F6A80CFDA21F41B974CA7EDD3E980EFB0D6E5D905261BE9D47D0BD6F7BB7C6F67B64539EA35F6C13CF8E1569384E97CB57D76B52D57BFBA83EA9DDB9E67C2114BFF30234715C5E1DCB7C9B6B
	7E66A75F18D173B13B56AC256B1A53157656D1FC65F4253D7773F5669AC88B1B4E3F253CD42EBF8770454F0EE40CFC0D5E63999170544A781AB5E40867B552E7CE9D4FCA6D9043BBA85EAD4333FC4F266B4F64BE8452E6BD0B312A236DD95B0C64C2738C34E5B7BDB3077DEE177D7E2B7164577757CF010ED4F99BAD7B5D53F772F51FF1D2F530B26D6BD737E7C77B556D5947341DA25F9FF34F605F77F3E0FB9648F14FD09C795EDC4A8CFE3ED3B3711B729C5C7499A3BFC39B4F8F1BB5C3F9E6F2B0FFFFDB4860
	670D526749A5F0DF15415725ACD259E549FE77D9CEFEB91EFF8AF47CA61BB3F749FE3FF28A660D8536139F277F5418B73E21B1EFBCDFB6469EAB7BDDFE8A465B6BCF010E0B1C12CD61325F4BCE41F30B6FCE41F30BEA0D6796EBBD1EDB30F7FF52DD1E673C977EC4FC415A18AD7C7BEB75995A6FD94B2195E888E09BE34CD03EFB742613586F799D0BCFF83B63626DCE82FD869D8477613C5D19D0AA1551FD325F4D2527E05D779468D83FD432E9136C7743CBCE3E6E0C132A03156914256AF81C3AD45DBF5EC91F
	6313F37123F8BCFC433EC18A38C40E3B399B7B4CEC9EA3E6BA8E2B7FEBF2219EA0682F08F7D3FC945CA7GE4F27C6BDD44EEC2A74F2F7E2F430FA165D0BF8CAD5371BE1DB34FD7247D274EA4E5754ACD1F973A488BF682BFCB0FFE7B057D6E245FE5E1F57AD287BF1F7AB95FE78F16818D812C53554FC9F51A49BE0BFEB3C4F946D65DF3B8E619CF3F43A11E4D63D41FA9F7D7AFF97747E5B4F576DA8D740CA956AECC616753391E63E74C70B325831F9B6E8C36AFDBA51D359ABA1FF9DF6D2F9D747B966C3D4C67
	783B0FF14F31F3AAD2601A799CBBE70951F470F36F04196C5D4DD238D64DE4B44C57897C3B960DB3FD1FD35186B466196C5DCD92AF81B3156DE7C307721CC7713CBA3D1E5314D715EA526CB2D8EC7D6239014BDB97897CECA2163C59EC50B970AC359B3A65E78E85B7B11B0209EDB4FDDE17D6FA84CFB7F7AB4FACA26931B3319C939DD80E89B307ABC7BB7DE6872BC71E6F828B19508EE40E649C1217FA097FD6F3B9039EFBD3AAC1C61D8FB9ECAE770E8177CB35GFDD0A65A3207FFEBA4BE93779D85D25A59F0
	DF7CFCF2E1657338DFAA083F67DE8FB87773A8036D95A4781867D1466B14B6936E0597006E85247D87BFD3307A05513EEFFD917DA6891373ECB76E17B2F2595F72F386A3B8EE9C27CBFD815B4D47BD1E0D953F18DCD87162C876D87A5E1B46F097EA3C77B836CFBDC6ADB178373E7329CFBE6D751D7DBE7914A0FE6F1D46C36D0B4A750EB9340C9BDFD42EF71CEB40B8E901345897516F677B3917B75CCF3E492366132F0714E6A96D9D24780AAC253D79FCAFEFB52405E6717E59E3AF71EC40973DB412751D1914
	2FDCC1DBA35EB7E9489047DD1A76360B74F62FFCD11AFB1E83C57AB038C63F2435D7B353210E73179C6AB8FF1D1E6B4CFE4F6F12FE0798CE7E58703AD32043ADFB0EF11A6C771B27601946E7276019463B9A4FB4169A556D5E6B342D0D07D8C9F9894739DBA9DD0E9B469F9C97453FD3B60B637651363B71E518033D0C383B3A102E03636E6EC6F9B19CD74E6545F19C7BFEE651ABB016F9854FD6E4F6B869004120E76334AAFEFEC355ABB8663B1538D49CEF5E4D4F7E2DFBE5645F114371E64BAB4AB1103D4BF3
	3EFA60D7C957E47EDD2FF6AF3A06DB93333F229C5B1E36DF127D8D37E3480B6C399A32719C0E90159DB38C6AEF06C5368BBCFD33913F49407A61ECFE3ECDB8777303745BA9ACFD9E9F97247074978EC869C64EDF140D69EFF7883C9E7C37479E65C620E94B9EC9DB7B16C75BE7861E009171D9BC740512BC7960E3F1D9E4D2572505A6F5DD7AD463BD0DAFB45E538854281B67F8544D77E9F93C7C4515603C68170439AEE191B947337C871B233C41ECAEA71F58F13A04379BF6EDE9A82F3450CF0CACF789C56C5E
	C1BEA2G37735817C5DC42DCFA503453A1AC608762EF23278F24772F5910B7E3DCDE2E103FCD1847CEE358A66413A3953689F369098B5B04BC174D6EA65B5EF265609272F610C3A073F6685CBCA78EFAEA7BF22E7130F01C163F02B33FF95BCFDBA0FF2D3672061D5B3CD1FDC24E44AE2F540927E217F775D9BB492B5E65362EF2F755112A23BE7514ED5619BB7B4CBA1721FC37DDC7F63416CB67BDF326FCB91CB42CC3D8247E9AC14379CFBD99FD3031A4D748857F9B8D4C61F38748FEDC6AF2B229297C8F6529
	74BFAC6F7F307F6B30AEE109D4A5C8A3A22B8FAEE141EE03DD67A0C96C20F328C03455910F92A63547EE68B4F519BA69118BD06B711061F2C227DD8BC7173D922A102D8F102ACEBEGD8BEBA91F98DFABDE97B686203CBA8244B8C5E5B990E1DA393B3E7B4D8813FA5367C047C1BCC334F6C747ACD26523F0B5F927B3B74ADB1F67E6A6FD710FE408878403F63792B4FFC7915607DCC615D961D596C5219FB9B2CFAF23E7026A3AE03F4AEFA688AA25F09B4E222F80E788A126F9E994F7F83D0CB878831D5E8638D
	B8GG1C50GGD0CB818294G94G88G88GDDFBB0B631D5E8638DB8GG1C50GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC7B8GGGG
**end of data**/
}
}