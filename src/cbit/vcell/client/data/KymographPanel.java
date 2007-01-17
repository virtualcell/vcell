package cbit.vcell.client.data;
import cbit.image.DisplayAdapterService;
import cbit.util.Range;
import javax.swing.ImageIcon;
/**
 * Insert the type's description here.
 * Creation date: (12/14/2004 9:38:13 AM)
 * @author: Frank Morgan
 */
public class KymographPanel extends javax.swing.JPanel implements cbit.vcell.geometry.gui.DrawPaneModel {
	//
	private class MinMaxMeanHolder {
		private double min;
		private double max;
		private double mean;
		private Range range;;
		public MinMaxMeanHolder(double argMin,double argMax,double argMean){
			min = argMin;
			max = argMax;
			mean = argMean;
			range = new Range(min,max);
		};
		//public void update (MinMaxMeanHolder argMMMH){
			//if(argMMMH.min < min){
				//min = argMMMH.min;
			//}
			//if(argMMMH.max > max){
				//max = argMMMH.max;
			//}
			//mean = (min+max)/2;
		//};
		public Range getRange(){
			return range;
		}
	};
	//
	private MinMaxMeanHolder allDataMMMH;
	private MinMaxMeanHolder localDistDataMMMH;
	private MinMaxMeanHolder localTimeDataMMMH;
	private MinMaxMeanHolder userDefinedMMMH;
	//private double userDefinedMin,userDefinedMax;
	private boolean bBlockInitialLoad = false;
	private static final int SCALE_IMAGE_ALL = 0;
	private static final int SCALE_IMAGE_LINESCAN = 1;
	private static final int SCALE_IMAGE_TIMESERIES = 2;
	private static final int SCALE_IMAGE_USERDEFINED = 3;
	private int scaleImageMode = SCALE_IMAGE_ALL;
	private boolean isInit = false;
	private cbit.plot.Plot2D currentLineScanPlot2D = null;
	private cbit.plot.Plot2D currentTimeSeriesPlot2D = null;
	private String NONE_MESSAGE = "Mouse Click, Arrow Keys Change Graph.  Mouse Menu for Options";
	private cbit.vcell.desktop.controls.DataManager dataManager = null;
	private int[] dataManagerIndices = null;
	private double[] dataManagerAccumDistances = null;
	private boolean bLocalScaling = false;
	private cbit.vcell.parser.SymbolTable symbolTable;
	private cbit.vcell.parser.SymbolTableEntry currentSymbolTablEntry;
	//private double localDistDataMin = 0;
	//private double localDistDataMax = 0;
	//private double localDistDataMean = 0;
	//private double localTimeDataMin = 0;
	//private double localTimeDataMax = 0;
	//private double localTimeDataMean = 0;
	private String currentInfo = null;
	private double[] currentTimes = null;
	private double[] currentDistances = null;
	private double[] rawValues = null;
	//private double allDataMin,allDataMax,allDataMean;
	private int RESAMP_SIZE;
	private java.awt.Point currentSelectionImg = null;
	private java.awt.geom.Point2D currentSelectionUnit = null;
	private int resampleStepOrig = 0;
	private double resampleStartTimeOrig = 0;
	private double resampleEndTimeOrig = 0;
	private double initialLineScanTime = 0;
	private double[][] timeSeriesDataOrig = null;
	private double[] accumDistancesDataOrig = null;
	private cbit.image.DisplayAdapterService ivjDisplayAdapterService1 = null;
	private cbit.image.ImagePaneScroller ivjImagePaneScroller1 = null;
	private cbit.image.ImagePlaneManager ivjImagePlaneManager1 = null;
	private javax.swing.ImageIcon cmapImageIcon = null;
	private javax.swing.JLabel ivjColorMapJLabel = null;
	private javax.swing.JLabel ivjMaxJLabel = null;
	private javax.swing.JLabel ivjMinJLabel = null;
	private javax.swing.JLabel ivjDisplayJLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean ivjConnPtoP1Aligning = false;
	private cbit.image.ImagePaneView ivjimagePaneView1 = null;
	private javax.swing.JMenuItem ivjCopyJMenuItem = null;
	private javax.swing.JPopupMenu ivjCopyJPopupMenu = null;
	private javax.swing.JButton ivjCopyJButton = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JButton ivjZoomDownJButton = null;
	private javax.swing.JButton ivjZoomUpJButton = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JPanel ivjGraphJPanel = null;
	private javax.swing.JPanel ivjButtonsJPanel = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JComboBox ivjVarNamesJComboBox = null;
	private javax.swing.JCheckBox ivjJCheckBox1 = null;
	private javax.swing.JMenuItem ivjCopyLineScanJMenuItem = null;
	private javax.swing.JMenuItem ivjCopyTimeDataJMenuItem = null;
	private cbit.plot.PlotPane ivjPlotPaneLineScan = null;
	private cbit.plot.PlotPane ivjPlotPaneTimeSeries = null;
	private javax.swing.JMenuItem ivjScaleImageAllJMenuItem = null;
	private javax.swing.JMenuItem ivjScaleImageLSJMenuItem = null;
	private javax.swing.JMenuItem ivjScaleImageTSJMenuItem = null;
	private javax.swing.JLabel ivjScaleImageModeJLabel = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.KeyListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == KymographPanel.this.getCopyJMenuItem()) 
				connEtoC7(e);
			if (e.getSource() == KymographPanel.this.getCopyJButton()) 
				connEtoC8(e);
			if (e.getSource() == KymographPanel.this.getZoomUpJButton()) 
				connEtoC9(e);
			if (e.getSource() == KymographPanel.this.getZoomDownJButton()) 
				connEtoC10(e);
			if (e.getSource() == KymographPanel.this.getVarNamesJComboBox()) 
				connEtoC13(e);
			if (e.getSource() == KymographPanel.this.getJCheckBox1()) 
				connEtoC14(e);
			if (e.getSource() == KymographPanel.this.getCopyTimeDataJMenuItem()) 
				connEtoC15(e);
			if (e.getSource() == KymographPanel.this.getCopyLineScanJMenuItem()) 
				connEtoC16(e);
			if (e.getSource() == KymographPanel.this.getScaleImageAllJMenuItem()) 
				connEtoC18(e);
			if (e.getSource() == KymographPanel.this.getScaleImageLSJMenuItem()) 
				connEtoC19(e);
			if (e.getSource() == KymographPanel.this.getScaleImageTSJMenuItem()) 
				connEtoC20(e);
			if (e.getSource() == KymographPanel.this.getScaleImageUDJMenuItem()) 
				connEtoC21(e);
		};
		public void keyPressed(java.awt.event.KeyEvent e) {
			if (e.getSource() == KymographPanel.this.getimagePaneView1()) 
				connEtoC12(e);
		};
		public void keyReleased(java.awt.event.KeyEvent e) {};
		public void keyTyped(java.awt.event.KeyEvent e) {};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == KymographPanel.this.getimagePaneView1()) 
				connEtoC6(e);
		};
		public void mouseDragged(java.awt.event.MouseEvent e) {
			if (e.getSource() == KymographPanel.this.getimagePaneView1()) 
				connEtoC17(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {
			if (e.getSource() == KymographPanel.this.getimagePaneView1()) 
				connEtoC3(e);
			if (e.getSource() == KymographPanel.this.getImagePaneScroller1()) 
				connEtoC11(e);
		};
		public void mouseMoved(java.awt.event.MouseEvent e) {
			if (e.getSource() == KymographPanel.this.getimagePaneView1()) 
				connEtoC2(e);
		};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == KymographPanel.this.getimagePaneView1()) 
				connEtoC5(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == KymographPanel.this.getimagePaneView1()) 
				connEtoC4(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == KymographPanel.this.getImagePaneScroller1() && (evt.getPropertyName().equals("imagePaneView"))) 
				connPtoP1SetTarget();
		};
	};
	private javax.swing.JMenuItem ivjScaleImageUDJMenuItem = null;

/**
 * Kymograph constructor comment.
 */
public KymographPanel() {
	super();
	initialize();

}

/**
 * Insert the method's description here.
 * Creation date: (5/24/2005 8:28:42 AM)
 * @param data double[]
 */
private MinMaxMeanHolder calcMMM(double[] data) {
	//
	//Calculate min,max,mean using only valid numeric values
	//
	double min = Double.POSITIVE_INFINITY;
	double max = Double.NEGATIVE_INFINITY;
	double mean = 0;
	long validCounter = 0;
	for(int j=0;j<data.length;j+= 1){
		//for(int k=0;k<data[j].length;k+= 1){
			double value = data[j];//[k];
			if(!Double.isNaN(value) && !Double.isInfinite(value)){
				if(value < min){
					min = value;
				}
				if(value > max){
					max = value;
				}
				mean+= value;
				validCounter+= 1;
			}
		//}
	}

	if(validCounter != 0){
		mean/= (double)validCounter;
	}else{
		return null;
	}
	
	return new MinMaxMeanHolder(min,max,mean);	
}


/**
 * Insert the method's description here.
 * Creation date: (12/28/2004 10:01:37 AM)
 * @param min double
 * @param max double
 */
private void configureMinMax() {
	
	getScaleImageModeJLabel().setText(
			(scaleImageMode == SCALE_IMAGE_ALL?"All":"")+
			(scaleImageMode == SCALE_IMAGE_LINESCAN?"LS":"")+
			(scaleImageMode == SCALE_IMAGE_TIMESERIES?"TS":"")+
			(scaleImageMode == SCALE_IMAGE_USERDEFINED?"User":"")
		);

	Range minmaxRange = null;
	switch(scaleImageMode){
		case SCALE_IMAGE_ALL:
			minmaxRange = (allDataMMMH != null?allDataMMMH.getRange():null);
		break;
		case SCALE_IMAGE_LINESCAN:
			minmaxRange = (localDistDataMMMH != null?localDistDataMMMH.getRange():null);
		break;
		case SCALE_IMAGE_TIMESERIES:
			minmaxRange = (localTimeDataMMMH != null?localTimeDataMMMH.getRange():null);
		break;
		case SCALE_IMAGE_USERDEFINED:
			minmaxRange = (userDefinedMMMH != null?userDefinedMMMH.getRange():null);
		break;
	}

	if(minmaxRange == null || minmaxRange.getMin() == minmaxRange.getMax()){
		getColorMapJLabel().setIcon(null);
		getColorMapJLabel().setText("No Range");
	}else{
		getColorMapJLabel().setIcon(cmapImageIcon);
		getColorMapJLabel().setText(null);
	}
	
	if(minmaxRange == null){
		getDisplayAdapterService1().setActiveScaleRange(null);
		getDisplayAdapterService1().setValueDomain(null);

		getMinJLabel().setText("?");
		getMinJLabel().setToolTipText("Unknown");
		getMaxJLabel().setText("?");
		getMaxJLabel().setToolTipText("Unknown");
		
		//updateColorMapDisplay();
		getimagePaneView1().getImagePaneModel().updateViewPortImage();
		
	}else if(getDisplayAdapterService1().getActiveScaleRange() == null || 
		getDisplayAdapterService1().getActiveScaleRange().getMin() != minmaxRange.getMin() ||
		getDisplayAdapterService1().getActiveScaleRange().getMax() != minmaxRange.getMax()){
			
		getDisplayAdapterService1().setActiveScaleRange(minmaxRange);
		getDisplayAdapterService1().setValueDomain((allDataMMMH != null?allDataMMMH.getRange():null));

		java.text.DecimalFormat nf = new java.text.DecimalFormat();
		nf.applyPattern("#.###E0");
		getMinJLabel().setText(((""+minmaxRange.getMin()).length() < 7?minmaxRange.getMin()+"":nf.format(minmaxRange.getMin())));
		getMinJLabel().setToolTipText(""+minmaxRange.getMin());
		getMaxJLabel().setText(((""+minmaxRange.getMax()).length() < 7?minmaxRange.getMax()+"":nf.format(minmaxRange.getMax())));
		getMaxJLabel().setToolTipText(""+minmaxRange.getMax());
		
		//updateColorMapDisplay();
		getimagePaneView1().getImagePaneModel().updateViewPortImage();
	}

}


/**
 * Insert the method's description here.
 * Creation date: (12/16/2004 10:46:05 AM)
 * @param imgX int
 * @param imgY int
 */
private void configurePlotData(int imgX,int imgY) {

	//
	//TimeScan Data
	//
	double[][] timeData = new double[2][currentTimes.length];
	timeData[0] = currentTimes;
	timeData[1] = new double[currentTimes.length];

	for(int i=0;i<currentTimes.length;i+= 1){
		//timeData[1][i] = timeSeriesDataOrig[1+imgX][i];
		timeData[1][i] = rawValues[imgX+(i*RESAMP_SIZE)];
	};
	localTimeDataMMMH = calcMMM(timeData[1]);
	
	
	final int MAX_TITLE_VAL_LENGTH = 9;
	java.text.DecimalFormat nf = new java.text.DecimalFormat();
	String valS = null;
	valS = currentDistances[imgX]+"";
	if(valS.length() > MAX_TITLE_VAL_LENGTH){
		nf.applyPattern("#.###E0");
		valS = nf.format(currentDistances[imgX]);
	}
	
	currentTimeSeriesPlot2D =
		new cbit.plot.SingleXPlot2D(new cbit.vcell.parser.SymbolTableEntry[] {currentSymbolTablEntry},"Time", new String[]{currentInfo}, timeData, new String[] {"Time Series (d="+valS+") Vert","Time"/*"Time (s)"*/,"Value"});
	getPlotPaneTimeSeries().setPlot2D(currentTimeSeriesPlot2D);

	
	//
	//LineScan Data
	//
	double[] lineData = new double[timeSeriesDataOrig.length-1];
	for(int i=1;i<timeSeriesDataOrig.length;i+= 1){
		lineData[i-1] = timeSeriesDataOrig[i][imgY];
	}
	double[] lineScanDistances = accumDistancesDataOrig;

	localDistDataMMMH = calcMMM(lineData);
	
	cbit.plot.PlotData plotData = new cbit.plot.PlotData(lineScanDistances,lineData);	
	valS = currentTimes[imgY]+"";
	if(valS.length() > MAX_TITLE_VAL_LENGTH){
		valS = nf.format(currentTimes[imgY]);
	}

	currentLineScanPlot2D =
		new cbit.plot.Plot2D(new cbit.vcell.parser.SymbolTableEntry[] {currentSymbolTablEntry},new String[] { currentInfo },new cbit.plot.PlotData[] { plotData }, new String[] {"Line Scan (t="+valS+") Horz","Distance"/*"Distance (\u00b5m)"*/, "Value"});
	getPlotPaneLineScan().setPlot2D(currentLineScanPlot2D);


	Range xRangeTime = new Range(currentTimes[0],currentTimes[currentTimes.length-1]);
	Range xRangeDist = new Range(lineScanDistances[0],lineScanDistances[lineScanDistances.length-1]);
	Range yRangeTime = (allDataMMMH != null?allDataMMMH.getRange():null);
	Range yRangeDist = yRangeTime;

	if(bLocalScaling){
		yRangeTime = (localTimeDataMMMH != null?localTimeDataMMMH.getRange():null);
		yRangeDist = (localDistDataMMMH != null?localDistDataMMMH.getRange():null);
	}

	getPlotPaneTimeSeries().forceXYRange(xRangeTime,yRangeTime);
	getPlotPaneLineScan().forceXYRange(xRangeDist,yRangeDist);

	configureMinMax();
	getimagePaneView1().repaint();
	
	
}


/**
 * connEtoC1:  (Kymograph.initialize() --> Kymograph.kymograph_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.kymograph_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC10:  (ZoomDownJButton.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.zoomDownJButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.zoomDownJButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (ImagePaneScroller1.mouse.mouseExited(java.awt.event.MouseEvent) --> Kymograph.imagePaneScroller1_MouseExited(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneScroller1_MouseExited(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC12:  (imagePaneView1.key.keyPressed(java.awt.event.KeyEvent) --> Kymograph.imagePaneView1_KeyPressed(Ljava.awt.event.KeyEvent;)V)
 * @param arg1 java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.KeyEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneView1_KeyPressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (JComboBox1.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.jComboBox1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBox1_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC14:  (JCheckBox1.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.jCheckBox1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBox1_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC15:  (CopyTimeDataJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.copyTimeDataJMenuItem_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.copyTimeDataJMenuItem_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC16:  (CopyLineScanJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.copyLineScanJMenuItem_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.copyLineScanJMenuItem_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC17:  (imagePaneView1.mouseMotion.mouseDragged(java.awt.event.MouseEvent) --> KymographPanel.imagePaneView1_Copy(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneView1_Copy(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC18:  (ScaleImageAllJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> KymographPanel.scaleImageModeFromMenu(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.scaleImageModeFromMenu(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC19:  (ScaleImageLSJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> KymographPanel.scaleImageModeFromMenu(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.scaleImageModeFromMenu(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (imagePaneView1.mouseMotion.mouseMoved(java.awt.event.MouseEvent) --> Kymograph.imagePaneScroller1_MouseMoved(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneScroller1_MouseMoved(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC20:  (ScaleImageTSJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> KymographPanel.scaleImageModeFromMenu(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.scaleImageModeFromMenu(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC21:  (ScaleImageUDJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> KymographPanel.scaleImageModeFromMenu(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC21(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.scaleImageModeFromMenu(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (imagePaneView1.mouse.mouseExited(java.awt.event.MouseEvent) --> Kymograph.imagePaneScroller1_MouseMoved(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneScroller1_MouseMoved(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (imagePaneView1.mouse.mouseReleased(java.awt.event.MouseEvent) --> Kymograph.imagePaneView1_Copy(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneView1_Copy(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (imagePaneView1.mouse.mousePressed(java.awt.event.MouseEvent) --> Kymograph.imagePaneView1_Copy(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneView1_Copy(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (imagePaneView1.mouse.mouseClicked(java.awt.event.MouseEvent) --> Kymograph.imagePaneView1_Copy(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneView1_Copy(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (CopyJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.copyJMenuItem_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.copyJMenuItem_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (CopyJButton.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.copyJMenuItem_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.copyJMenuItem_ActionPerformed(null);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (ZoomUpJButton.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.zoomUpJButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.zoomUpJButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (ImagePaneScroller1.imagePaneView <--> imagePaneView1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setimagePaneView1(getImagePaneScroller1().getImagePaneView());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private void copyJMenuItem_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	StringBuffer sb = new StringBuffer();
	sb.append("LineScan-Time Data ("+currentInfo+")"+
		" Distances(columns) from "+currentDistances[0]+" to "+currentDistances[currentDistances.length-1]+" along sample line "+
		" Times(rows) from "+currentTimes[0]+" to "+currentTimes[currentTimes.length-1]+"\n"+
		"\n");
	sb.append("Min\tMax\tMean\n");
	sb.append((allDataMMMH != null?allDataMMMH.min+"\t"+allDataMMMH.max+"\t"+allDataMMMH.mean:"\"?\"\t\"?\"\t\"?\"")+"\n");
	for(int i=0;i<currentDistances.length;i+= 1){
		sb.append("\t"+currentDistances[i]);
	}
	sb.append("\n\n");
	int counter = 0;
	for(int i=0;i<currentTimes.length;i+= 1){
		sb.append(currentTimes[i]);
		for(int j=0;j<currentDistances.length;j+= 1){
			sb.append("\t"+rawValues[counter]);
			counter+= 1;
		}
		sb.append("\n");
	}
	cbit.vcell.desktop.VCellTransferable.sendToClipboard(sb.toString());
}


/**
 * Comment
 */
private void copyLineScanJMenuItem_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	StringBuffer sb = new StringBuffer();
	sb.append("LineScan Data "+" ("+currentInfo+")"+
		" Distances from "+currentDistances[0]+" to "+currentDistances[currentDistances.length-1]+" along sample line "+
		" at Time="+currentTimes[(int)currentSelectionImg.getY()]+"\n");
	sb.append("Min\tMax\tMean\n");
	//sb.append(localDistDataMin+"\t"+localDistDataMax+"\t"+localDistDataMean+"\n");
	sb.append((localDistDataMMMH != null?localDistDataMMMH.min+"\t"+localDistDataMMMH.max+"\t"+localDistDataMMMH.mean:"\"?\"\t\"?\"\t\"?\"")+"\n");
	sb.append("Dist\tValue\n");

	for(int i=0;i<currentLineScanPlot2D.getPlotDatas()[0].getIndependent().length;i+= 1){
		sb.append(currentLineScanPlot2D.getPlotDatas()[0].getIndependent()[i]+"\t");
		sb.append(currentLineScanPlot2D.getPlotDatas()[0].getDependent()[i]+"\n");
	}
	
	cbit.vcell.desktop.VCellTransferable.sendToClipboard(sb.toString());

}


/**
 * Comment
 */
private void copyTimeDataJMenuItem_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	StringBuffer sb = new StringBuffer();
	sb.append("TimeSeries Data "+" ("+currentInfo+")"+
		" Times from "+currentTimes[0]+" to "+currentTimes[currentTimes.length-1]+
		" at Distance="+ currentDistances[(int)currentSelectionImg.getX()]+" ("+currentDistances[currentDistances.length-1]+")\n");
	sb.append("Min\tMax\tMean\n");
	//sb.append(localTimeDataMin+"\t"+localTimeDataMax+"\t"+localTimeDataMean+"\n");
	sb.append((localTimeDataMMMH != null?localTimeDataMMMH.min+"\t"+localTimeDataMMMH.max+"\t"+localTimeDataMMMH.mean:"\"?\"\t\"?\"\t\"?\"")+"\n");
	sb.append("Time\tValue\n");

	for(int i=0;i<currentTimeSeriesPlot2D.getPlotDatas()[0].getIndependent().length;i+= 1){
		sb.append(currentTimeSeriesPlot2D.getPlotDatas()[0].getIndependent()[i]+"\t");
		sb.append(currentTimeSeriesPlot2D.getPlotDatas()[0].getDependent()[i]+"\n");
	}

	cbit.vcell.desktop.VCellTransferable.sendToClipboard(sb.toString());
}


/**
 * This is called when the canvas repaint.
 */
public void draw(java.awt.Graphics g) {

	g.setColor(java.awt.Color.white);
	if(currentSelectionUnit != null){
		int width = (int)getimagePaneView1().getImagePaneModel().getDimension().getWidth();
		int height = (int)getimagePaneView1().getImagePaneModel().getDimension().getHeight();
		int y = (int)((height-1) * currentSelectionUnit.getY());
		int x = (int)((width-1) * currentSelectionUnit.getX());
		g.drawLine(0,y-1,width-1,y-1);
		g.drawLine(0,y,width-1,y);
		g.drawLine(0,y+1,width-1,y+1);
		
		g.drawLine(x-1,0,x-1,height-1);
		g.drawLine(x,0,x,height-1);
		g.drawLine(x+1,0,x+1,height-1);
	}
}


/**
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getButtonsJPanel() {
	if (ivjButtonsJPanel == null) {
		try {
			ivjButtonsJPanel = new javax.swing.JPanel();
			ivjButtonsJPanel.setName("ButtonsJPanel");
			ivjButtonsJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsZoomDownJButton = new java.awt.GridBagConstraints();
			constraintsZoomDownJButton.gridx = 2; constraintsZoomDownJButton.gridy = 0;
			constraintsZoomDownJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getButtonsJPanel().add(getZoomDownJButton(), constraintsZoomDownJButton);

			java.awt.GridBagConstraints constraintsZoomUpJButton = new java.awt.GridBagConstraints();
			constraintsZoomUpJButton.gridx = 3; constraintsZoomUpJButton.gridy = 0;
			constraintsZoomUpJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getButtonsJPanel().add(getZoomUpJButton(), constraintsZoomUpJButton);

			java.awt.GridBagConstraints constraintsCopyJButton = new java.awt.GridBagConstraints();
			constraintsCopyJButton.gridx = 4; constraintsCopyJButton.gridy = 0;
			constraintsCopyJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getButtonsJPanel().add(getCopyJButton(), constraintsCopyJButton);

			java.awt.GridBagConstraints constraintsVarNamesJComboBox = new java.awt.GridBagConstraints();
			constraintsVarNamesJComboBox.gridx = 1; constraintsVarNamesJComboBox.gridy = 0;
			constraintsVarNamesJComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsVarNamesJComboBox.weightx = 1.0;
			constraintsVarNamesJComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getButtonsJPanel().add(getVarNamesJComboBox(), constraintsVarNamesJComboBox);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 0;
			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getButtonsJPanel().add(getJLabel3(), constraintsJLabel3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonsJPanel;
}

/**
 * Return the ColorMapJlabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getColorMapJLabel() {
	if (ivjColorMapJLabel == null) {
		try {
			ivjColorMapJLabel = new javax.swing.JLabel();
			ivjColorMapJLabel.setName("ColorMapJLabel");
			ivjColorMapJLabel.setText("cm");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorMapJLabel;
}

/**
 * Return the CopyJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCopyJButton() {
	if (ivjCopyJButton == null) {
		try {
			ivjCopyJButton = new javax.swing.JButton();
			ivjCopyJButton.setName("CopyJButton");
			ivjCopyJButton.setText("Copy All");
			ivjCopyJButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCopyJButton;
}

/**
 * Return the CopyJMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getCopyJMenuItem() {
	if (ivjCopyJMenuItem == null) {
		try {
			ivjCopyJMenuItem = new javax.swing.JMenuItem();
			ivjCopyJMenuItem.setName("CopyJMenuItem");
			ivjCopyJMenuItem.setText("Copy All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCopyJMenuItem;
}

/**
 * Return the CopyJPopupMenu property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getCopyJPopupMenu() {
	if (ivjCopyJPopupMenu == null) {
		try {
			ivjCopyJPopupMenu = new javax.swing.JPopupMenu();
			ivjCopyJPopupMenu.setName("CopyJPopupMenu");
			ivjCopyJPopupMenu.add(getCopyJMenuItem());
			ivjCopyJPopupMenu.add(getCopyTimeDataJMenuItem());
			ivjCopyJPopupMenu.add(getCopyLineScanJMenuItem());
			ivjCopyJPopupMenu.add(getScaleImageAllJMenuItem());
			ivjCopyJPopupMenu.add(getScaleImageLSJMenuItem());
			ivjCopyJPopupMenu.add(getScaleImageTSJMenuItem());
			ivjCopyJPopupMenu.add(getScaleImageUDJMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCopyJPopupMenu;
}

/**
 * Return the CopyLineScanJMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getCopyLineScanJMenuItem() {
	if (ivjCopyLineScanJMenuItem == null) {
		try {
			ivjCopyLineScanJMenuItem = new javax.swing.JMenuItem();
			ivjCopyLineScanJMenuItem.setName("CopyLineScanJMenuItem");
			ivjCopyLineScanJMenuItem.setText("Copy LineScan");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCopyLineScanJMenuItem;
}


/**
 * Return the CopyTimeDataJMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getCopyTimeDataJMenuItem() {
	if (ivjCopyTimeDataJMenuItem == null) {
		try {
			ivjCopyTimeDataJMenuItem = new javax.swing.JMenuItem();
			ivjCopyTimeDataJMenuItem.setName("CopyTimeDataJMenuItem");
			ivjCopyTimeDataJMenuItem.setText("Copy TimeSeries");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCopyTimeDataJMenuItem;
}


/**
 * Return the DisplayAdapterService1 property value.
 * @return cbit.image.DisplayAdapterService
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.image.DisplayAdapterService getDisplayAdapterService1() {
	if (ivjDisplayAdapterService1 == null) {
		try {
			ivjDisplayAdapterService1 = new cbit.image.DisplayAdapterService();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayAdapterService1;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDisplayJLabel() {
	if (ivjDisplayJLabel == null) {
		try {
			ivjDisplayJLabel = new javax.swing.JLabel();
			ivjDisplayJLabel.setName("DisplayJLabel");
			ivjDisplayJLabel.setText("DisplayJLabel");
			ivjDisplayJLabel.setMaximumSize(new java.awt.Dimension(80, 14));
			ivjDisplayJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjDisplayJLabel.setPreferredSize(new java.awt.Dimension(80, 14));
			ivjDisplayJLabel.setMinimumSize(new java.awt.Dimension(80, 14));
			ivjDisplayJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayJLabel;
}

/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getGraphJPanel() {
	if (ivjGraphJPanel == null) {
		try {
			ivjGraphJPanel = new javax.swing.JPanel();
			ivjGraphJPanel.setName("GraphJPanel");
			ivjGraphJPanel.setLayout(new java.awt.GridBagLayout());
			ivjGraphJPanel.setPreferredSize(new java.awt.Dimension(300, 350));
			ivjGraphJPanel.setMinimumSize(new java.awt.Dimension(300, 350));

			java.awt.GridBagConstraints constraintsPlotPaneTimeSeries = new java.awt.GridBagConstraints();
			constraintsPlotPaneTimeSeries.gridx = 0; constraintsPlotPaneTimeSeries.gridy = 1;
			constraintsPlotPaneTimeSeries.fill = java.awt.GridBagConstraints.BOTH;
			constraintsPlotPaneTimeSeries.weightx = 1.0;
			constraintsPlotPaneTimeSeries.weighty = 1.0;
			constraintsPlotPaneTimeSeries.insets = new java.awt.Insets(4, 0, 2, 4);
			getGraphJPanel().add(getPlotPaneTimeSeries(), constraintsPlotPaneTimeSeries);

			java.awt.GridBagConstraints constraintsPlotPaneLineScan = new java.awt.GridBagConstraints();
			constraintsPlotPaneLineScan.gridx = 0; constraintsPlotPaneLineScan.gridy = 0;
			constraintsPlotPaneLineScan.fill = java.awt.GridBagConstraints.BOTH;
			constraintsPlotPaneLineScan.weightx = 1.0;
			constraintsPlotPaneLineScan.weighty = 1.0;
			constraintsPlotPaneLineScan.insets = new java.awt.Insets(2, 0, 4, 4);
			getGraphJPanel().add(getPlotPaneLineScan(), constraintsPlotPaneLineScan);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGraphJPanel;
}

/**
 * Return the ImagePaneScroller1 property value.
 * @return cbit.image.ImagePaneScroller
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.image.ImagePaneScroller getImagePaneScroller1() {
	if (ivjImagePaneScroller1 == null) {
		try {
			ivjImagePaneScroller1 = new cbit.image.ImagePaneScroller();
			ivjImagePaneScroller1.setName("ImagePaneScroller1");
			ivjImagePaneScroller1.setPreferredSize(new java.awt.Dimension(400, 350));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImagePaneScroller1;
}

/**
 * Return the imagePaneView1 property value.
 * @return cbit.image.ImagePaneView
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.image.ImagePaneView getimagePaneView1() {
	// user code begin {1}
	// user code end
	return ivjimagePaneView1;
}


/**
 * Return the ImagePlaneManager1 property value.
 * @return cbit.image.ImagePlaneManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.image.ImagePlaneManager getImagePlaneManager1() {
	if (ivjImagePlaneManager1 == null) {
		try {
			ivjImagePlaneManager1 = new cbit.image.ImagePlaneManager();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImagePlaneManager1;
}


/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBox1() {
	if (ivjJCheckBox1 == null) {
		try {
			ivjJCheckBox1 = new javax.swing.JCheckBox();
			ivjJCheckBox1.setName("JCheckBox1");
			ivjJCheckBox1.setText("Local Scaling");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBox1;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Max");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}


/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("Min");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}


/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("Variables");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDisplayJLabel = new java.awt.GridBagConstraints();
			constraintsDisplayJLabel.gridx = 0; constraintsDisplayJLabel.gridy = 0;
			constraintsDisplayJLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDisplayJLabel.weightx = 1.0;
			constraintsDisplayJLabel.insets = new java.awt.Insets(4, 0, 0, 4);
			getJPanel1().add(getDisplayJLabel(), constraintsDisplayJLabel);

			java.awt.GridBagConstraints constraintsJCheckBox1 = new java.awt.GridBagConstraints();
			constraintsJCheckBox1.gridx = 1; constraintsJCheckBox1.gridy = 0;
			constraintsJCheckBox1.insets = new java.awt.Insets(4, 0, 0, 4);
			getJPanel1().add(getJCheckBox1(), constraintsJCheckBox1);
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

			java.awt.GridBagConstraints constraintsMaxJLabel = new java.awt.GridBagConstraints();
			constraintsMaxJLabel.gridx = 0; constraintsMaxJLabel.gridy = 2;
			getJPanel3().add(getMaxJLabel(), constraintsMaxJLabel);

			java.awt.GridBagConstraints constraintsColorMapJLabel = new java.awt.GridBagConstraints();
			constraintsColorMapJLabel.gridx = 0; constraintsColorMapJLabel.gridy = 3;
			constraintsColorMapJLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsColorMapJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getColorMapJLabel(), constraintsColorMapJLabel);

			java.awt.GridBagConstraints constraintsMinJLabel = new java.awt.GridBagConstraints();
			constraintsMinJLabel.gridx = 0; constraintsMinJLabel.gridy = 4;
			getJPanel3().add(getMinJLabel(), constraintsMinJLabel);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 1;
			getJPanel3().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 5;
			getJPanel3().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsScaleImageModeJLabel = new java.awt.GridBagConstraints();
			constraintsScaleImageModeJLabel.gridx = 0; constraintsScaleImageModeJLabel.gridy = 0;
			getJPanel3().add(getScaleImageModeJLabel(), constraintsScaleImageModeJLabel);
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
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMaxJLabel() {
	if (ivjMaxJLabel == null) {
		try {
			ivjMaxJLabel = new javax.swing.JLabel();
			ivjMaxJLabel.setName("MaxJLabel");
			ivjMaxJLabel.setText("255");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaxJLabel;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMinJLabel() {
	if (ivjMinJLabel == null) {
		try {
			ivjMinJLabel = new javax.swing.JLabel();
			ivjMinJLabel.setName("MinJLabel");
			ivjMinJLabel.setText("0");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinJLabel;
}

/**
 * Return the PlotPane2 property value.
 * @return cbit.plot.PlotPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.plot.PlotPane getPlotPaneLineScan() {
	if (ivjPlotPaneLineScan == null) {
		try {
			ivjPlotPaneLineScan = new cbit.plot.PlotPane();
			ivjPlotPaneLineScan.setName("PlotPaneLineScan");
			ivjPlotPaneLineScan.setBorder(new cbit.gui.LineBorderBean());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlotPaneLineScan;
}

/**
 * Return the PlotPane1 property value.
 * @return cbit.plot.PlotPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.plot.PlotPane getPlotPaneTimeSeries() {
	if (ivjPlotPaneTimeSeries == null) {
		try {
			ivjPlotPaneTimeSeries = new cbit.plot.PlotPane();
			ivjPlotPaneTimeSeries.setName("PlotPaneTimeSeries");
			ivjPlotPaneTimeSeries.setBorder(new cbit.gui.LineBorderBean());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlotPaneTimeSeries;
}

/**
 * Return the ScaleImageAllJMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getScaleImageAllJMenuItem() {
	if (ivjScaleImageAllJMenuItem == null) {
		try {
			ivjScaleImageAllJMenuItem = new javax.swing.JMenuItem();
			ivjScaleImageAllJMenuItem.setName("ScaleImageAllJMenuItem");
			ivjScaleImageAllJMenuItem.setText("Scale Image to All Data");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScaleImageAllJMenuItem;
}


/**
 * Return the ScaleImageLSJMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getScaleImageLSJMenuItem() {
	if (ivjScaleImageLSJMenuItem == null) {
		try {
			ivjScaleImageLSJMenuItem = new javax.swing.JMenuItem();
			ivjScaleImageLSJMenuItem.setName("ScaleImageLSJMenuItem");
			ivjScaleImageLSJMenuItem.setText("Scale Image to LineScan");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScaleImageLSJMenuItem;
}


/**
 * Return the SclaeImageModeJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getScaleImageModeJLabel() {
	if (ivjScaleImageModeJLabel == null) {
		try {
			ivjScaleImageModeJLabel = new javax.swing.JLabel();
			ivjScaleImageModeJLabel.setName("ScaleImageModeJLabel");
			ivjScaleImageModeJLabel.setText("All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScaleImageModeJLabel;
}

/**
 * Return the ScaleImageTSJMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getScaleImageTSJMenuItem() {
	if (ivjScaleImageTSJMenuItem == null) {
		try {
			ivjScaleImageTSJMenuItem = new javax.swing.JMenuItem();
			ivjScaleImageTSJMenuItem.setName("ScaleImageTSJMenuItem");
			ivjScaleImageTSJMenuItem.setText("Scale Image to TimeSeries");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScaleImageTSJMenuItem;
}


/**
 * Return the ScaleImageUDJMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getScaleImageUDJMenuItem() {
	if (ivjScaleImageUDJMenuItem == null) {
		try {
			ivjScaleImageUDJMenuItem = new javax.swing.JMenuItem();
			ivjScaleImageUDJMenuItem.setName("ScaleImageUDJMenuItem");
			ivjScaleImageUDJMenuItem.setText("Scale Image User Defined");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScaleImageUDJMenuItem;
}


/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getVarNamesJComboBox() {
	if (ivjVarNamesJComboBox == null) {
		try {
			ivjVarNamesJComboBox = new javax.swing.JComboBox();
			ivjVarNamesJComboBox.setName("VarNamesJComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVarNamesJComboBox;
}

/**
 * Return the ZoomDownJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getZoomDownJButton() {
	if (ivjZoomDownJButton == null) {
		try {
			ivjZoomDownJButton = new javax.swing.JButton();
			ivjZoomDownJButton.setName("ZoomDownJButton");
			ivjZoomDownJButton.setText("ZoomOut");
			ivjZoomDownJButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZoomDownJButton;
}

/**
 * Return the ZoomUpJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getZoomUpJButton() {
	if (ivjZoomUpJButton == null) {
		try {
			ivjZoomUpJButton = new javax.swing.JButton();
			ivjZoomUpJButton.setName("ZoomUpJButton");
			ivjZoomUpJButton.setText("ZoomIn");
			ivjZoomUpJButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZoomUpJButton;
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
 * Comment
 */
private void imagePaneScroller1_MouseExited(java.awt.event.MouseEvent mouseEvent) {
	getDisplayJLabel().setText(NONE_MESSAGE);
}


/**
 * Comment
 */
private void imagePaneScroller1_MouseMoved(java.awt.event.MouseEvent mouseEvent) {
	java.awt.Point point = getimagePaneView1().getImagePoint(mouseEvent.getPoint());
	if(point != null){
		int imgX = point.x;
		int imgY = point.y;
		getDisplayJLabel().setText(
			"Time = "+currentTimes[imgY]+
			"  Dist = "+currentDistances[imgX]+
			"  Value = "+rawValues[(imgY*RESAMP_SIZE) + imgX]);
		return;
	}
	
	getDisplayJLabel().setText(NONE_MESSAGE);
}


/**
 * Comment
 */
private void imagePaneView1_Copy(java.awt.event.MouseEvent mouseEvent) {
	
	if(mouseEvent.isPopupTrigger()){
		getCopyJPopupMenu().show(getImagePaneScroller1().getImagePaneView(),mouseEvent.getX(),mouseEvent.getY());
	}else if(mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_RELEASED
		//mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_CLICKED
		/*|| mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_DRAGGED*/){
		java.awt.Point point = getimagePaneView1().getImagePoint(mouseEvent.getPoint());
		if(point != null){
			currentSelectionImg = point;
			currentSelectionUnit = getimagePaneView1().getImagePointUnitized(mouseEvent.getPoint());
			configurePlotData(currentSelectionImg.x,currentSelectionImg.y);
		}
	}
}


/**
 * Comment
 */
private void imagePaneView1_KeyPressed(java.awt.event.KeyEvent keyEvent) {

	int dx =
		(keyEvent.getKeyCode() == keyEvent.VK_LEFT?-1:0) + 
		(keyEvent.getKeyCode() == keyEvent.VK_RIGHT?1:0);
	int dy =
		(keyEvent.getKeyCode() == keyEvent.VK_UP?-1:0) + 
		(keyEvent.getKeyCode() == keyEvent.VK_DOWN?1:0);
	if(dx != 0 ||  dy != 0){
		int imgWidth = getimagePaneView1().getImagePaneModel().getSourceData().getXSize();
		int imgHeight = getimagePaneView1().getImagePaneModel().getSourceData().getYSize();
		int newImgX = currentSelectionImg.x+dx;
		int newImgY = currentSelectionImg.y+dy;
		double newImgXUnit = (double)(newImgX)/(double)(imgWidth-1);
		double newImgYUnit = (double)(newImgY)/(double)(imgHeight-1);
		if(newImgXUnit >= 0 && newImgXUnit <= 1 && newImgYUnit >= 0 && newImgYUnit <= 1){
			currentSelectionUnit = new java.awt.geom.Point2D.Double(newImgXUnit,newImgYUnit);
			currentSelectionImg = new java.awt.Point(newImgX,newImgY);
			configurePlotData(newImgX,newImgY);
		}
	}
	//if(keyEvent.getKeyCode() == keyEvent.VK_UP || 
		//keyEvent.getKeyCode() == keyEvent.VK_DOWN || 
		//keyEvent.getKeyCode() == keyEvent.VK_LEFT || 
		//keyEvent.getKeyCode() == keyEvent.VK_RIGHT){
			
	//}
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getImagePaneScroller1().addPropertyChangeListener(ivjEventHandler);
	getCopyJMenuItem().addActionListener(ivjEventHandler);
	getCopyJButton().addActionListener(ivjEventHandler);
	getZoomUpJButton().addActionListener(ivjEventHandler);
	getZoomDownJButton().addActionListener(ivjEventHandler);
	getImagePaneScroller1().addMouseListener(ivjEventHandler);
	getVarNamesJComboBox().addActionListener(ivjEventHandler);
	getJCheckBox1().addActionListener(ivjEventHandler);
	getCopyTimeDataJMenuItem().addActionListener(ivjEventHandler);
	getCopyLineScanJMenuItem().addActionListener(ivjEventHandler);
	getScaleImageAllJMenuItem().addActionListener(ivjEventHandler);
	getScaleImageLSJMenuItem().addActionListener(ivjEventHandler);
	getScaleImageTSJMenuItem().addActionListener(ivjEventHandler);
	getScaleImageUDJMenuItem().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
}

/**
 * Insert the method's description here.
 * Creation date: (12/14/2004 9:47:38 AM)
 * @param timeSeries double[][]
 * @param distances double[]
 */
public void initDataManager(
	cbit.vcell.desktop.controls.DataManager argDataManager,
	String variableName,double initTime,int step,double endTime,int[] indices,
	double[] accumDistances,
	boolean waitOnInitialLoad,
	double argInitialLineScanTime,
	cbit.vcell.parser.SymbolTable argSymbolTable)
				throws cbit.vcell.server.DataAccessException{

	symbolTable = argSymbolTable;
	currentSymbolTablEntry = null;
	bBlockInitialLoad = waitOnInitialLoad;
	resampleStepOrig = step;
	resampleStartTimeOrig = initTime;
	resampleEndTimeOrig = endTime;
	isInit = true;
	initialLineScanTime = argInitialLineScanTime;
		
	dataManager = argDataManager;
	dataManagerAccumDistances = accumDistances;
	dataManagerIndices = indices;
	currentSelectionImg = new java.awt.Point(0,0);
	currentSelectionUnit = new java.awt.geom.Point2D.Double(0,0);;

	cbit.vcell.simdata.DataIdentifier[] sortedDataIDs =
		cbit.vcell.simdata.VariableType.collectSimilarDataTypes(variableName,dataManager.getDataIdentifiers());
	//Add to combobox
	getVarNamesJComboBox().setEnabled(true);
	getVarNamesJComboBox().removeAllItems();
	getVarNamesJComboBox().removeActionListener(ivjEventHandler);	
	for(int i=0;i<sortedDataIDs.length;i+= 1){
		getVarNamesJComboBox().addItem(sortedDataIDs[i].getName());
	}
	getVarNamesJComboBox().addActionListener(ivjEventHandler);

	getVarNamesJComboBox().setSelectedItem(variableName);
}


/**
 * Insert the method's description here.
 * Creation date: (12/14/2004 9:47:38 AM)
 * @param timeSeries double[][]
 * @param distances double[]
 */
private void initDataManagerVariable(final String finalVarName) {

	//Create SymbolTableEntry for Copy/Paste functionality
	try{
		currentSymbolTablEntry = (symbolTable != null?symbolTable.getEntry(finalVarName):null);
	}catch(cbit.vcell.parser.ExpressionBindingException e){
		e.printStackTrace();
	}
	
	Thread fetchThread = new Thread(
		new Runnable(){
			cbit.util.AsynchProgressPopup pp =
				new cbit.util.AsynchProgressPopup(null,"Time Series Data ("+finalVarName+")","Fetching Data...",false,false);
			public void run(){
				pp.start();
				try{
					cbit.util.TimeSeriesJobSpec timeSeriesJobSpec =
						new cbit.util.TimeSeriesJobSpec(new String[] {finalVarName},new int[][] {dataManagerIndices},resampleStartTimeOrig,resampleStepOrig,resampleEndTimeOrig);
					cbit.util.TSJobResultsNoStats timeSeriesJobResults = (cbit.util.TSJobResultsNoStats)dataManager.getTimeSeriesValues(timeSeriesJobSpec);
					final double[][] timeSeries = timeSeriesJobResults.getTimesAndValuesForVariable(finalVarName);
					currentInfo = finalVarName;
					if(javax.swing.SwingUtilities.isEventDispatchThread()){
						initStandAloneTimeSeries_private(timeSeries,dataManagerAccumDistances);
					}else{
						javax.swing.SwingUtilities.invokeAndWait(
							new Runnable(){
								public void run(){
									initStandAloneTimeSeries_private(timeSeries,dataManagerAccumDistances);
								}
							}
						);
					}
						if(isInit){// set crosshair to init time
						double initTime = initialLineScanTime;//resampleStartTimeOrig;
						isInit = false;
						int closestTimeIndex = 0;
						double closestDiff = Double.MAX_VALUE;
						for(int i=0;i<currentTimes.length;i+= 1){
							double diff = Math.abs(initTime-currentTimes[i]);
							if( diff < closestDiff){
								closestTimeIndex = i;
								closestDiff = diff;
							}
						}
						currentSelectionImg = new java.awt.Point(0,closestTimeIndex);
						currentSelectionUnit = new java.awt.geom.Point2D.Double(0,(double)closestTimeIndex/(double)(currentTimes.length-1));
						if(javax.swing.SwingUtilities.isEventDispatchThread()){
							configurePlotData((int)currentSelectionImg.getX(),(int)currentSelectionImg.getY());
						}else{
							javax.swing.SwingUtilities.invokeAndWait(
								new Runnable(){
									public void run(){
										configurePlotData((int)currentSelectionImg.getX(),(int)currentSelectionImg.getY());
									}
								}
							);
						}
					}
				}catch(Throwable e){
					pp.stop();
					final Throwable finale = e;
								cbit.vcell.client.PopupGenerator.showErrorDialog(
									"Error init variable "+finalVarName+" "+finale.getClass().getName()+" "+finale.getMessage());
				}finally{
					pp.stop();
				}
			}
		}
	);
	
	if(isInit && bBlockInitialLoad){
		fetchThread.run();
	}else{
		fetchThread.start();
	}
	
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("Kymograph");
		setLayout(new java.awt.GridBagLayout());
		setSize(736, 448);

		java.awt.GridBagConstraints constraintsImagePaneScroller1 = new java.awt.GridBagConstraints();
		constraintsImagePaneScroller1.gridx = 1; constraintsImagePaneScroller1.gridy = 0;
		constraintsImagePaneScroller1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsImagePaneScroller1.weightx = 1.0;
		constraintsImagePaneScroller1.weighty = 1.0;
		add(getImagePaneScroller1(), constraintsImagePaneScroller1);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.gridwidth = 3;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanel1.weightx = 1.0;
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsGraphJPanel = new java.awt.GridBagConstraints();
		constraintsGraphJPanel.gridx = 2; constraintsGraphJPanel.gridy = 0;
		constraintsGraphJPanel.fill = java.awt.GridBagConstraints.BOTH;
		add(getGraphJPanel(), constraintsGraphJPanel);

		java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
		constraintsJPanel3.gridx = 0; constraintsJPanel3.gridy = 0;
		constraintsJPanel3.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 0);
		add(getJPanel3(), constraintsJPanel3);

		java.awt.GridBagConstraints constraintsButtonsJPanel = new java.awt.GridBagConstraints();
		constraintsButtonsJPanel.gridx = 0; constraintsButtonsJPanel.gridy = 2;
		constraintsButtonsJPanel.gridwidth = 3;
		constraintsButtonsJPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsButtonsJPanel.weightx = 1.0;
		constraintsButtonsJPanel.insets = new java.awt.Insets(0, 4, 0, 4);
		add(getButtonsJPanel(), constraintsButtonsJPanel);
		initConnections();
		connEtoC1();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Insert the method's description here.
 * Creation date: (12/14/2004 9:47:38 AM)
 * @param timeSeries double[][]
 * @param distances double[]
 */
public void initStandAloneTimeSeries(double[][] timeSeries,double[] accumDistances,String info /*,double initTime,double endTime*/) {

	//timeseries is in the format returned by pdeDatacontext.getTimeSeries
	//timeSeries[0][0...numTimePoints-1] = timePointArray
	//timeSeries[1 + 0...numSamplePoints-1][0...numTimePoints-1] = dataValueArrays

	isInit = true;
	resampleStartTimeOrig = timeSeries[0][0];
	resampleStepOrig = 1;
	resampleEndTimeOrig = timeSeries[0][timeSeries[0].length-1];
	currentInfo = info;
	currentSelectionImg = new java.awt.Point(0,0);
	currentSelectionUnit = new java.awt.geom.Point2D.Double(0,0);;
	getVarNamesJComboBox().removeAllItems();
	getVarNamesJComboBox().removeActionListener(ivjEventHandler);	
	getVarNamesJComboBox().addItem(info);
	getVarNamesJComboBox().setEnabled(false);
	getVarNamesJComboBox().addActionListener(ivjEventHandler);
	initStandAloneTimeSeries_private(timeSeries,accumDistances);
}


/**
 * Insert the method's description here.
 * Creation date: (12/14/2004 9:47:38 AM)
 * @param timeSeries double[][]
 * @param distances double[]
 */
private void initStandAloneTimeSeries_private(double[][] timeSeriesOrig,double[] accumDistancesOrig) {

	//timesereis is in the format returned by pdeDatacontext.getTimeSeries
	//timeSeries[0][0...numTimePoints-1] = timePointArray
	//timeSeries[1 + 0...numSamplePoints-1][0...numTimePoints-1] = dataValueArrays

	if(accumDistancesOrig.length != (timeSeriesOrig.length-1)){
		throw new IllegalArgumentException(this.getClass().getName()+" accumDistances.length != numSamplePoints");
	}


	timeSeriesDataOrig = timeSeriesOrig;
	accumDistancesDataOrig = accumDistancesOrig;
	
	//Resample for even distances
	RESAMP_SIZE = timeSeriesDataOrig.length-1;
	int rvSize = (timeSeriesDataOrig[0].length)*RESAMP_SIZE;
	rawValues = new double[rvSize];
	double incr = accumDistancesDataOrig[accumDistancesDataOrig.length-1]/(double)(RESAMP_SIZE-1);
	currentTimes = timeSeriesDataOrig[0];
	currentDistances = new double[RESAMP_SIZE];
	for(int j=0;j<timeSeriesDataOrig[0].length;j+= 1){
		int sourceIndex = 0;
		double currentDistance = 0;
		for(int k=0;k<RESAMP_SIZE;k+= 1){
			while(currentDistance > accumDistancesDataOrig[sourceIndex+1]){
				sourceIndex+= 1;
			}
			double subShort = currentDistance-accumDistancesDataOrig[sourceIndex];
			double subLong  = accumDistancesDataOrig[sourceIndex+1]-accumDistancesDataOrig[sourceIndex];
			double proportion = subShort/subLong;
//System.out.println("prop="+proportion+" j="+j+" k="+k+" sourceIndex="+sourceIndex+"sourcedist="+accumDistancesDataOrig[sourceIndex]+" currentDistance="+currentDistance);
			double value = timeSeriesDataOrig[1+sourceIndex+(proportion > .5?1:0)][j];
			//double value = timeSeriesDataOrig[1+sourceIndex][j] + ((timeSeriesDataOrig[1+sourceIndex+1][j]-timeSeriesDataOrig[1+sourceIndex][j])*proportion);
			//if(k == (RESAMP_SIZE-1)){
				//value = timeSeriesDataOrig[timeSeriesDataOrig.length-1][j];
			//}
			rawValues[(j*RESAMP_SIZE)+(k)] = value;
			currentDistances[k] = currentDistance;
			currentDistance+= incr;
			if(currentDistance > accumDistancesDataOrig[accumDistancesDataOrig.length-1]){
				currentDistance = accumDistancesDataOrig[accumDistancesDataOrig.length-1];
			}
		}
	}

	Range minmaxRange = null;
	allDataMMMH = calcMMM(rawValues);
	if(allDataMMMH != null){
		minmaxRange = allDataMMMH.getRange();
	}
	
	cbit.image.SourceDataInfo sdi =
		new cbit.image.SourceDataInfo(
			cbit.image.SourceDataInfo.RAW_VALUE_TYPE,
			rawValues,
			minmaxRange,
			0,
			RESAMP_SIZE,1,
			0,accumDistancesDataOrig[accumDistancesDataOrig.length-1],
			timeSeriesDataOrig[0].length,RESAMP_SIZE,
			timeSeriesDataOrig[0][0],timeSeriesDataOrig[0][timeSeriesDataOrig[0].length-1]-timeSeriesDataOrig[0][0]
		);

	getImagePaneScroller1().getImagePaneModel().setSourceData(sdi);
	getImagePlaneManager1().setSourceDataInfo(sdi);
	if(isInit){
		zoomUpJButton_ActionPerformed(null);
		zoomDownJButton_ActionPerformed(null);
		//getImagePaneScroller1().getImagePaneModel().changeZoomToFillViewport();
	}


	getDisplayJLabel().setText(NONE_MESSAGE);
	configurePlotData(currentSelectionImg.x,currentSelectionImg.y);

}


/**
 * Comment
 */
private void jCheckBox1_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	bLocalScaling = getJCheckBox1().isSelected();
	configurePlotData((int)currentSelectionImg.getX(),(int)currentSelectionImg.getY());
}


/**
 * Comment
 */
private void jComboBox1_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	initDataManagerVariable((String)getVarNamesJComboBox().getSelectedItem());
}


/**
 * Comment
 */
private void kymograph_Initialize() {
	
	//getDisplayAdapterService1().addColorModelForValues(DisplayAdapterService.createGrayColorModel(), DisplayAdapterService.createGraySpecialColors(), "Gray");
	getDisplayAdapterService1().addColorModelForValues(DisplayAdapterService.createBlueRedColorModel(), DisplayAdapterService.createBlueRedSpecialColors(), "BlueRed");
	getDisplayAdapterService1().setActiveColorModelID("BlueRed");

	//int[] sc = getDisplayAdapterService1().fetchSpecialColors("BlueRed");
	//sc[DisplayAdapterService.BELOW_MIN_COLOR_OFFSET] = java.awt.Color.darkGray.getRGB();
	//sc[DisplayAdapterService.ABOVE_MAX_COLOR_OFFSET] = java.awt.Color.lightGray.getRGB();
	//getDisplayAdapterService1().updateSpecialColors("BlueRed",sc);
	updateColorMapDisplay();
	
	getImagePaneScroller1().getImagePaneModel().setMode(cbit.image.ImagePaneModel.MESH_MODE);
	getImagePaneScroller1().initRowColumnDescriptions("simulation Time","Distance Along Sample Line");
	getimagePaneView1().setDrawPaneModel(this);
	getDisplayJLabel().setText(NONE_MESSAGE);
	getPlotPaneTimeSeries().setBCompact(true);
	getPlotPaneLineScan().setBCompact(true);
	getImagePaneScroller1().getImagePaneModel().setDisplayAdapterService(getDisplayAdapterService1());
	getImagePaneScroller1().getImagePaneModel().setBackgroundColor(new java.awt.Color(32,32,32));
	getImagePaneScroller1().setImagePlaneManager(getImagePlaneManager1());

}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		final int numTP = 27;
		final int numDP = 100;
		double[][] timeSeries = new double[numDP+1][numTP];
		double[] accumArr = new double[numDP];
		timeSeries[0] = new double[numTP];
		for(int i=0;i<numTP;i+= 1){
			timeSeries[0][i] = i+10;
		}
		java.util.Random rand = new java.util.Random();
		for(int i=0;i<numDP;i+= 1){
			accumArr[i] = (i==0?0:accumArr[i-1]+rand.nextDouble()*10);
			for(int j=0;j<numTP;j+= 1){
				//timeSeries[i+1][j] = rand.nextDouble()*10000;
				timeSeries[i+1][j] = j*numTP + i;
			}
			if(args.length > 0 && args[0].equals("allNAN")){
				java.util.Arrays.fill(timeSeries[i+1],Double.NaN);
			}
		}
		timeSeries[1][0] = Double.NaN;
		timeSeries[1][1] = Double.NaN;
		timeSeries[2][0] = Double.NaN;
		timeSeries[2][1] = Double.NaN;
			
		javax.swing.JFrame frame = new javax.swing.JFrame();
		KymographPanel aKymograph;
		aKymograph = new KymographPanel();
		frame.setContentPane(aKymograph);
		frame.setSize(aKymograph.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		aKymograph.initStandAloneTimeSeries(timeSeries,accumArr,"Test Data"/*,timeSeries[0][0],timeSeries[0][timeSeries[0].length-1]*/);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private void scaleImageModeFromMenu(java.awt.event.ActionEvent actionEvent) {
	
	if(actionEvent.getSource() == getScaleImageAllJMenuItem()){
		scaleImageMode = SCALE_IMAGE_ALL;
	}else if(actionEvent.getSource() == getScaleImageLSJMenuItem()){
		scaleImageMode = SCALE_IMAGE_LINESCAN;
	}else if(actionEvent.getSource() == getScaleImageTSJMenuItem()){
		scaleImageMode = SCALE_IMAGE_TIMESERIES;
	}else if(actionEvent.getSource() == getScaleImageUDJMenuItem()){
		try{
			String userMinMaxS =
				cbit.vcell.client.PopupGenerator.showInputDialog(this,
				"Enter min and max scaling value separated by comma (e.g. 0,100)",
				(allDataMMMH != null?allDataMMMH.getRange().getMin()+","+allDataMMMH.getRange().getMax():"?,?"));
			int commaIndex = userMinMaxS.indexOf(",");
			if(commaIndex == -1){
				cbit.vcell.client.PopupGenerator.showErrorDialog("Min and Max values must be separted by a comma");
				return;
			}
			try{
				double min = Double.parseDouble(userMinMaxS.substring(0,commaIndex));
				double max = Double.parseDouble(userMinMaxS.substring(commaIndex+1,userMinMaxS.length()));
				userDefinedMMMH = new MinMaxMeanHolder(min,max,(min+max)/2);
			}catch(NumberFormatException e){
				cbit.vcell.client.PopupGenerator.showErrorDialog("Min or Max value cannot be parsed to a number");
				return;
			}
			scaleImageMode = SCALE_IMAGE_USERDEFINED;
		}catch(cbit.vcell.client.task.UserCancelException e){
			//getimagePaneView1().getImagePaneModel().updateViewPortImage();
			getimagePaneView1().repaint();
			return;
		}
	}

	configureMinMax();
}


/**
 * Set the imagePaneView1 to a new value.
 * @param newValue cbit.image.ImagePaneView
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setimagePaneView1(cbit.image.ImagePaneView newValue) {
	if (ivjimagePaneView1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjimagePaneView1 != null) {
				ivjimagePaneView1.removeMouseMotionListener(ivjEventHandler);
				ivjimagePaneView1.removeMouseListener(ivjEventHandler);
				ivjimagePaneView1.removeKeyListener(ivjEventHandler);
			}
			ivjimagePaneView1 = newValue;

			/* Listen for events from the new object */
			if (ivjimagePaneView1 != null) {
				ivjimagePaneView1.addMouseMotionListener(ivjEventHandler);
				ivjimagePaneView1.addMouseListener(ivjEventHandler);
				ivjimagePaneView1.addKeyListener(ivjEventHandler);
			}
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Comment
 */
private void updateColorMapDisplay() {

	if(cmapImageIcon == null && getDisplayAdapterService1().getActiveColorModel() != null){
		int[] cmap = getDisplayAdapterService1().getActiveColorModel();
		int cmapLength = cmap.length - getDisplayAdapterService1().getSpecialColors().length;
		int cmapWidth = 12;
		java.awt.image.BufferedImage cmapImage = new java.awt.image.BufferedImage(cmapWidth,cmapLength,java.awt.image.BufferedImage.TYPE_INT_ARGB);
		int[] row = new int[cmapWidth];
		for(int i=0;i<cmapLength;i+=1){
			java.util.Arrays.fill(row,cmap[i]);
			cmapImage.setRGB(0,cmapLength-1-i,cmapWidth,1,row,0,cmapWidth);
		}
		cmapImageIcon = new ImageIcon(cmapImage);
		getColorMapJLabel().setIcon(cmapImageIcon);
		getColorMapJLabel().setText(null);
		getColorMapJLabel().repaint();
	}
	////Set ColorMap
	//int[] row = new int[cmapWidth];
	//java.awt.image.BufferedImage bufferedCmap = (java.awt.image.BufferedImage )cmapImageIcon.getImage();
	//if(getDisplayAdapterService1() != null &&
		//getDisplayAdapterService1().getActiveScaleRange() != null){
		//double value = getDisplayAdapterService1().getActiveScaleRange().getMin();
		//double inc = (getDisplayAdapterService1().getActiveScaleRange().getMax() - getDisplayAdapterService1().getActiveScaleRange().getMin())/(double)(cmapLength-1);
		//for(int i=0;i<cmapLength;i+=1){
			//int colorFromValue = getDisplayAdapterService1().getColorFromValue(value);
			//if(i == (cmapLength-1)){
				//colorFromValue = getDisplayAdapterService1().getColorFromValue(getDisplayAdapterService1().getActiveScaleRange().getMax());
			//}
////System.out.println("i="+i+" colorValue="+cbit.util.Hex.toString(colorFromValue));
			//java.util.Arrays.fill(row,colorFromValue);
			//bufferedCmap.setRGB(0,cmapLength-1-i,cmapWidth,1,row,0,cmapWidth);
			//value+= inc;
		//}
	//}else{
		//java.util.Arrays.fill(row,java.awt.Color.white.getRGB());
		//for(int i=0;i<cmapLength;i+=1){
			//bufferedCmap.setRGB(0,cmapLength-1-i,cmapWidth,1,row,0,cmapWidth);
		//}
	//}
	//getColorMapJLabel().repaint();
}


/**
 * Comment
 */
private void zoomDownJButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getImagePaneScroller1().zooming(new cbit.image.ZoomEvent(getimagePaneView1(),0,-1));
}


/**
 * Insert the method's description here.
 * Creation date: (12/29/2004 12:57:18 PM)
 */
public void zoomToFill() {

	getimagePaneView1().getImagePaneModel().changeZoomToFillViewport();
}


/**
 * Comment
 */
private void zoomUpJButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getImagePaneScroller1().zooming(new cbit.image.ZoomEvent(getimagePaneView1(),0,1));
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC8FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DFD8DD8D5D53AB0B51624D4E4DAE44EC406C58DCD4EB7F647A96FFD1C724EB8B7FAE2981BE13ECBDD6E64B4FDF3ED062FCB13B317193BB9C0C22122C262C805098A0A840A0504A902090AC2C206C54A113301837B7CF04E66CFD13FF77D3CEB6F334FBE7CB54EF31F4F67F95DFB3D6B7DDB6F7AFF575A87A9675D707494CB0EA42524CB7C5F07A912E43BD1125E3CBE3584F115BBE2B3A55D3FADG6B24
	2C2F5260D98FF9CB22E2B333255FD6E6C03A8D52491131199B61FD03747B4A195741CBGBD8A64FD30A4B7F5F4BD3BF3D04FAA1A5E181D854FB5G6D00A34F289DC47F7B59F99CBF056391D2AC00ABA2B4473373B9AE91E8B2GF78820250851AB70E4865CD0D549695AEE0B14127E45E3D9CD6468E4A2C4F35EC47EAC107E1492D6CCF4F9291D042788526DG247029D2CE6AAEF8D6B775F6FD5CF1412E168EF4480AD25A2158E527DAEA332A562A2A93438ED717476A6EEE32BAE5C54A59C964BD1E1DC179A5A9
	9EE405D3DCCC8F51518A6FEE00601D084F72927CF2F8CF8648590565BA30231A5B5DFA697B5232BD0E3435814AD5320B1527E517D62EA047F316CD348E9C394457211037GE0A9408A002C12584C92004D346E551C1D702C3920563A9C8E1733466DBE2C58BCA7DDB6D901772AAA48613853F2074BE313A4D63F4359A5A6760C860CEF6A047918CFA277E2DDC71D7E0E947F3DBBAC2BCCFC929FF949D2D742FC51D622790469DE9CD077C809EFDB74557D3924571D367A672969A63A8B7E74ECEAA8179321533D3C
	0454F5DDAF296BA570CE785EA475CE7127A93EE83706E76D7267344D10F4BD64C56D4636513A86EDE9C81BABE5175FE96A0730A7C2AC59254C0622D2BFDBD4A23B2514695CADEC69207805BABC33E5BDED27A41D7FBE345F3258CC563E86B612FE1AD48EB4FBE2B30BG8E8134830CG046E41B6760FB96F0F330DB5D9BD6A61FE3BE213BD92639D48FD8B1E72106A319EF3FAD52B33C3BEE375582D979459ABF11A2B39448797FC782650365F81BA0E4ACE59E3D56DAEA7C837BAE4D576F8994D4D396B880DC726
	39ADF6078C8A9CEE89654E4A5DGCF456AD5BFF3C31717B58A167F4DDCD2A78356B3A0918440BB734BFDF944DF73217CCB8162049F725EA272CE489E98AE2A2A9ADC2E5EFEF78BA909D43417787997F46CD88174E9FB514777ECA2B88552D376221C4637566B645459BB0879D64FB07631633DB9246E00FEBE40B200A4A23F825B506FDA318D552DFED8AAF9F616E99B1A896D392402351D3A8AE3DFEA25BA081D8A6405GC481C4D760789F5F534A65907DBEEDD3171E4F6DA26FC9FB4D7BE0603164D521E315A1
	BFD5771E4E7B917667716AE07EDF3D1954E5A25818358F464DFDD887535EAAE0ED7A3055ABE3ABEE74C08314EDDA0D707AD0DB39FF53395CD55C96E297193356D2BAA2D712F281B6727EC6F43D8D702E4466592DG5BGF6G94836C923E30D39DF5E01F82B093E09140F2002C8FE2B3ABG5AG26GC481AC83C886A87CB0B63381C08518833084E085C0CEE5ECE6B5008DA024926BB44F5B1E22351374F116E353B84B31FB82F5737FB3B0DFFB292F319D270F534F2B4669674D7F834A79BF81D8DF8E5AC87CA9
	8F40522ECEF64BCE9B3CF8F95E540D446FFC7E69F4891E1BA95E8D73D5ADCC90DE013FFDA329972B2AFA6C977AD539D9D6D53B33CB4B3F1B6A7276DFF0EBD39D6A1A3B115433EB9066ABC17F40C6B256F7381CCE198EAA020567BFC8679AD9119DE0F42D2B5F29B27CA3F9E4BD5EAB8F73396EFBF92FCBE42ED27A493C495638732D24BD39BDF6878CD5E46A15527613F968F7E20DCEE08174FF32D60F03670ACA1CC38637629CD2F871C1E971F93745ECCD94B66FD6CB43FEB62E376F576690E6D344A6F35B9F22
	F39E47FB192D7D1B08DE316416D85F58A0BD6DA46B03BA3B572DD807EBECD6B7CC6F4D32E7405EA117A3CD66EE6243E38EEB175C2440DA7D2455892F9E119FFA1158E5277910FD46AE8F0A3CFF5ACE72EADD6E6163CDAEF73F7B246C6C47396113FCE2FF9014EBA6C054FE1C9B367F1564637C4CA705DA172230AA043C15544F7750B994FD35E4BF73739A117F14CF7E21AAADBF2C0A643FA6725D108E566533FA1C23686BB1C1171F71915A3AE1F3B6E90FF2DF3F8CCBAB3298B9A15A4CDFB72FB7641D616D0765
	3F3719349FC5370ED1BFA2FA9FD1F17FD6709153D9491FCD9F71B5E9B5213377A35DE2404781A4FF445691F9107F8E60193C3D83C827D6337C10EAA6AF3A1A49E3F42F893A1C830C2E6400995D8F86114E4669C6B85D4283FA3A11A124DB496932BFB623BBB80CF48D9FB3BA1BA91D65A25205C37EC200B8CE17F7D0CF77A3C1578E78A10030039ADD9CG23FBE38469D6GAE6D10C6D7F8C8AF6F1F841D8278A9BA3A889FBA4FE5316FBE446AA32306519D2A5153D5DCC13A609AD65EF0CE374C076ED5C117F7B8
	B6338C2069B0238BBD6CD39FD711AE8E7009G59353C3D546A69AECBC897867879359ADD0C8FDDF90AE8FFF550766AB43A56BABD5D2F85DDE49DAB4762BA33FAFB5002F452912883004AA30CEE68081E4E110AF40BG3FACA05D16B424CB82FC56A701681E95F4A5C0D387E07B44170E0D0D7FD9CC7AB90DD574D8872CC3255EC118F32A2A0E5340840BCB342E4B6074911FE07CE1CA8E993F62A11D8410F59447761F2E4678434C5EC8A9E17533290162AA05C7199D75C73531BD7E1141D4B6BE063ECDF486C3DE
	58D19CEF3E39D93F872A71FADD9DF63A7D62E3CAC33EFE6CE738CF735F00E7275D63D50F4164BC04634F677944DE32BF63E86E0F1B7B36CB7A588D9D5949C649E41240BD7E65CF74E5DE347632654DGE50E62E50D5615F9497F79C3AA537D541E774CF47BCDBE3837FEC7491258DC13D58FF58CB0D2CF70DFAAE47EA663A1332FE3AF5A97FAF11E54BEEBF6C07B9657B33B926AB57B22F66F63763D1E179F202D606C465A46CF28DDF947205F825483B0FB63E80C0048EAA7B8GB6CF9E6CA7F83B532E5A2D0A7D
	22DCD44C749D711035E9AF4E5B670F69A84EF05F4F5DF0085B9E7D1D39D27555F39633F8D7705AEECB4CB1BEBF9E534A26AEFB4E928C406C1B510F7E2CBB8E3E84E0F3E1CD2E4F1A66B85BE3AF07674A63D806BE0534F31FF500046B003787C8FD285DF6AF3379159A3D4D8BD705185A5C7DC018A57B8433357204E6F37B8B4FD954975066368174E9B050ACBE0176BCB9G4B9049616A774AA7DD240F1472778159D6C4EA2F54BA281652F5EB69C912F3043C960BFEB07061C77A72B4F7F8489A44D3FE7E24A626
	A1201C0F20FE18FD8503E85FCA302D2C816D1BBAD80276B14B0E8C5955D1CDE372FEA564F5032CF0A16F58D0292F3C53B08E4076FA744272F6FD3E5CD8F765674932ED0C92E2BF1CB704B64501BD59A751262C615D3EB6B5F9E46FD8A6F1195D43A8339E648589194F98E556AA760E5E71495CF591E5AE84F909GEC2DB85B21DFAB961DE2311826D328F34645BD70345285E8A9FBB4491ECE1747612F3706E67B7A7731AAB27EF410F5B099A40E293263FC0DC10C5153BAB960EAE67F6F057DB630ED4EA934FF03
	4FDAF7996013818A9A517EB317A6EBBF1B7FE6DD4238FCC5A30EE5FBC7509685F0910DE84BCC454C1678C6BC9B7837B5449797DDAE47E76E63077BD555651C14AFFEA374A7026C22A654DFEC2A3F3D8975EFAF7D186B2F034D6264AC4076F477E534C18579D1CD68EF4B150976EDE65BE939FA14B1078B9935CBB23B3E3A02F6AD871B72BFC53B16DCA1318C5857168284680B300361154FECFA745CBE7F31610446EEB48779756343BBD7511E9630A564D32C2BA7FBFC76F01F323ADAA16C3D616A576BF737DF5E
	8F4F1EDA1763026B30EB28FC42554C6D7F1530DFBA8D6B4D53E8FFC1315EFEB76043G961D96E33274756CFF5A436C6F16BBFA3F0E7D77CBE8FF8C5816510C765B1CE6635E21E66CABEB0E5471F10BC46F6B2C2A756B0DDFDF2408F6GBAC2049DBBDDE6F6AC91F64883E8C7031DF48B2B736B593146A266AB50115D027555EF59EB36E62873D83B3AC61FE1185CD8A1379EE48635E07992CD4B37D0685D1F7A755AC9C79669235E8E2BA25335B2B99D7A4463F290A70D370D5C110A36C703DDE91F216D7F60B633
	3D7AB31C870E27A1DF936002855FC81F995FE241779741378CF0D9E7106F30470C2F6E8C7ACACE1F2C2FD8EC782BF546F34C56B35A590922FBC7E067FBAB69FEAF680C718C7275DC028FBFE3BC031C1603E7100BA12F70F3BC13DAD5C0565241100EGD884308CA089A0279577F97F16CF5A6128E7FE471C308DF3DA15E61FE0A81E3D3D1D4A46F91E3E6BED62E79A3F14B07E563F0578398CF4B6G58GC6G66889B8A5E995306A3C326B6B0BF97AA444F0BDAA7FAAE350772AD1BG9F5BD394FF213F4F10366E
	38057840EC0F17F616494EB92B6DA918CF5E59CAFC5282F8B7C0B0C0B840623368131EF7CA4672891E2FF2E730B6F252C15AC242D956C6560A36739545E7FC2161D95BF9066E9FC93A8C72223E40B32467D5E35BAD7A5257CFF506B463DB6147D7EAC8F79B7818BF54C2325F8D023CB9G8BGE2G12G325BE2B3AB5A50AF5FFF674018ED05F44296D7AD606ADD2E5EDAE3CCDE589A56BF569D8773B25EBFA6DFCB1BAF9F9BBBF73CB5CA4CC13788433570F0AE460214B60C851DF6117A0A0074A200E5ED58577F
	E39B715F8AC8E7FC856BF100EA00F6009100702F50FFEF3CFBF8AC7F556021C80BCB3C1AB81179624240645A095B0F6F9DC3FA61D7E6FC5B87A7272F420F4F28AF46D45FCB13547707B1753DE42A6F5BC313537783BFBE23BE691C991FF3F8F27AC67C780C7A72CC7515DF1C1C3E03FEFCC6FDE5674C7A5C52F4526ECD62E8382A63FD6D278E5CF7561F43B17055C95A1AFAE9ACDB03CCEDDD5B3C495CD636F314703C680C5DF86EC7E2E4E1A0B38A001567F8EF8637E709A84FECEAD77239094E137BC7A6670723
	FEFCC6BFE41F7765E336BF25206DC567517617A7E943FF0DE9C33B298D6D4206A1E1433D17A7E743A3FEFCC69B661FB7636B38B2B9FDFDFEFCC6FDCBCD7595DE1D1C3EBDFEFCC6FD89674D5AFB179220EF62AE1A57451FFA30EF2E94F57152A4ED7D43183616351B597A7119C036729DB36F1B69EE333ED98FB25B5A31EFFEDE0C733A5A0E650912A6D71E477C780C6509EAB7EB5FF7BB5127CB048D6DA91333416947E734A15954862B8BED4831228DB9164959701E9F1F5106862B99DF5CA4753DB826BE1B552C
	4CBB5CD86691D166B929133361A1BFBE238D8BCCEDF8D95890ADECE8CA1B1C8D677C780CB624DDB03361FBFDE8C37E8534A1ADFDF2B62C7763B35A50E2EA433087EDD0A01F5D6DD46979EB8424978A5C279B88AE9E52C942563F64107DDBDEC7ECE6A5C083008DE028837333F2C9BEB91BA7F13A1900DF5841E20EACBFB42755E0239E08CCA34E264365F11D64FD48041669F8F0D49D69A6B87D3D4BD4C93B2F792689AD5BCFE497843E133531407CCE565B8566F73236962CE3F810213FE10C793B8AC63F333537
	A0701D2D4BDB825BB9E50B391D5336185B993AE5F4BB4336843273ED6A571F53581D275F295A9DB2954DE30FC364AEA06C40CE77BB59760CC8779A777B926E190BF710334E788E5C5B3D7F5E9E49FFEFF744E6D7DD9E9452A2DBBD0D1D1DE2E3576C16BB506E6DDB837BE877D6F39F6D5DEA6E237DDBC777D14D56C0BEE2ED3759C17633AB21ECB9B668E3GEDGC1G91GCBED580FBAA89D3923C36E1910BBA4F97CCEC7963F0B41681ED55C3C5D92B95806A3EC7EFDE7B4D8497BDA0E01AFC8172E6476E97DB0
	C96856772F5593500B7D512C5F6A7BA65137163F33367613375652FA70FAE5AF8D0C1050BA4F7B0DCA6E247A5DD9BCE5F548921E276776E7F21AA32A2B36DC545F154240ED6506C27336B23D1014596B6A77F4484676B023B0CF64E9418632C1A36670BB1914DF35FA3AE43F76B41772333CC07C5FAE8C546678F92FA8EB136AEAAA9775F7A597671795A66DC5128345F1FFC4E3B5B9B20B3925887CB394DF204333D8DC2B8B3FF1AA013CD0997B752B5B827BBAF91B392FAD5B837BBAF37B583EDE3FBD302FB7ED
	9F5B576FEDA76D72025DE677705B144C47F78D685B539E617B2F8675783D827F6E109EDFA1703F72416F9378F943FA7C87825FFDD10F7FD0546D4F9DE6313CA413BA5577438DD249A53DBC928FE6EB0ADB6C38364F6844181DC23F9DBA8469D6GF7A70EC90D3DE63A03BB196EB9701C5F69EB87DB0FFC171E11AF013CE51DF8D6619E515B33DF14AFC6B153311C4BDD894F2CAEDF9DCCDE6165B7747D3E8C654926728A380C8AF8561B4A0B7315A76E6D4E3E62035F0378AF2F7A6077227E7F6E99AD765A5665DF
	EF3C9DC9BE72AA303E8E38313E5CDDD8DF4D67C8FDC5C2FAB1C0DC9756D7DB31196EC42EB32DBB106E97FCF56F433204BB4D646519487115F7E70A0F3C8FD0DE274BCCDE491872CED8FC64FD08783529BE78CA54D362B653F3E8CCBDBF7315379F653DE2AA2FE5CCF9D35374727608B63A304FCCDE771872BECA7711D7AE46FF0F193C11B165CDDFC76E72AB56E1D73F8AFB60EE5236662E4378C0A824A381E2G16F733B881DB87FEF7100C1D1D9E2B8326A73AC00B3353FBCA9B10378E522DGC3GE1G8B6DB8
	8EEC5B285D21575F6BC01B76FCC64A44EE1C16F35C4E4F093E23645E9F4B60785B07B45A3D9C376CA41997D8C0524B29B1AED912E2967BA868094D2C68417BFD769EB31A8648EF6F4179EE778E22C34C53BE6B115EFC12D72B3F5FA472DE294A65F9FEF78884CDD3B752781D6F8B1AD9F6722DC233B8632ED1949D912379695EDC9F1A06E6FFB9E79D3EB4ADA6B44BEAFDE9BE2B53533071FE96BD879A827F4C6C41BDE92893772411BDB8BEC513691F3759E7F56F67DC236D833FAEB09D0357D487AB734BB4C63F
	924A1B510BE53E5DEE6C1BB93DDA3FAC6A8D34B6CB20F3F2F52F710C7DF7945F56EBBCE3777462997B8864A574E2BDF452B8FB1D921B5922A06E473DD8B76D8277AD3AAE188F69A58A76552517487E8D16D165898A21F91E26E3844FEF9414DD68C0DC3D83E51789DCC38F4A8E923891419BA2F07F2EA0EF1840BD51093CAF895CDB427EA4877A79305F98DD6754FC5B6A8C64679F5339D4F1B2FF268BFCAC458769704C4FE74F61DD0D1910375209B6F9E94C344D951B29389097D70CF68E895C771DC837D460AE
	3B9037CC60EA693C1FG69C4014B6CC3DC3240BDA3F099EE444D22F1239CC8973A710E493F2C4335FB99603A5DB81617377B5C63A6B7C62A2A0EFA6C3643562E86BAB330F5487A75991CBF5C0D6B10C36B094CB8C8A782E474619D73216DF847BC6A7E395232CC73FB7F89AD17AD85FD2CDE6A7A347B87B39D4FD978DAF5836A8C027C05FD8437E8037ECE8CE927572532B3FC167FBB1FFC321F3A14224FAF48516793FBFF182E4FBF6D13CFFCF402733332BD358D4B167C54FCC9F9F4CA405F0A10BC2CCC057C99
	3A7C2E14307F3E184A4AE6CB4960B25BBC684F60CE624F45100E83C874E0BC7E5E9E31E677A26D45EE3AE607F4AB005B0BFDB6AA17586376481D3247A35B1A6D97495C3458CBF87E190E534170BE9320C1A538871CF55C5EB455FCFD18421F8472002684A017278F295A9E1169CFF3527B6D30611B29125F7738CCDC47474707689893877895AA3655E25117337B31AF3FC86F7036C3FA84A08CE0BEC00C48CF2279647BA3722D92792E09FC9334DC648F759A47FAA20748A36F737B470E2904F11A9893DA2663B6
	BF9D69129683994F77612345AE797AC67175E5B879798309555B733D225E867CED4CB4569B50904865781281E33D7D32132C859DB0585B1DB036D1FDEBC8080167A751315F592FA87C37C5E47D9D2332CB8D816F98D5F27B6A79B3B9F7F68A13FF5D9E2DBC679DD81EA113722C12C6AFCF082EBCCCF684592273779FD7E8FAD6AA28A74CC4CF7A98FA227C747CF61FA6FB37281305A6320DFDA9DAA71B68CD5049E6FD63AC1D83D6GDE9A94F3CA9F761742C1EC6FCF1FA3B89552218297C7F111036CBBB806BBFF
	4E58E6894FF856B721632063FD4FCFC76AB864236F4722E1EDF1DA31E10C79D02B035F97E39DACB53159EFBCB354EF7C2031FE43AB35F674CB923660B6EC6B75352178A30D2E21DAFBF79D50F822BC3EBCB34875FB0C03785D459C1F3F1F1FA45F46759365E36B22759B483C735220B1E638196213860DB143CF56633AC39A0A4D348D61FD307E03E41F3594524B811281F206E19F8650B40C7B2CC7F74016F9743BE0ADAEF703BCA0AB3A90BEDFF7EF18E87958387643D5B83FAFF86CA1E9610DF5813F0F338D33
	76B505BF0BDE38AB05BCD979B2EAC87996C0DE8CC092C056C558EB82D4DE44727DF9873FC3C90EC0EAD4555A51CD3FD69487AAF6F9D0C3130DB67F0D0E94E39FEB38B8F67FECB721D18197EC40337AFC42CE6AED4EC5633A78E90A1FFF5138AE3E25935745CBA02F68920EB9777B4543C2AEE97A42AF85DA173FC257B78BAEB1BD2B85BE056A0F5361197EA0CACF52491057BD82EB9C5A476E28A5354A3F21F0964053A98F96084B51F53091F7129F5DA92AA8ABABA9583B2F2C18350370C17D7CD6794675263FCB
	C0E05988AB43CBA35A7C4656EDF1CEDC3795DC0E4DFCD7E23F3311A1613F4403E6B8E637F4C492ED725F3330CD66FF7BBE296E97F5162D81ECE93D4CEC083C2C2D27637A8AD2191CE73E5417A9FB36E2C989A0A701732F5049E1BE7C215DBFE694A76EFB95DCE1B123CAF8B6DCE16749ECDCBEA466C65B957FBA4736340A5755905004DC51664670AB46754A4293644C618B5FEF17F85E4A4A55BCCFFCD7A578EE5EC47AB30D4F78703C6CA4FB05B3D6FA3A6455EE78A05F3D27E92C2941FA01441A587D7C5DF67D
	DEA10A17B7668A2E5B57AAD87F4B2F1855BF4E910FFCC25E593851F6144C9179D7F10C78347CBDEAD70F7FE9A9A0499D62E655E3F7F6E13CB85401BA4B2EE29FF8221E48E77945A23FDB647F7A1816FF2E9773432F1A37D94613F1C2923EF13B7D5B44D4F14FA86EAAEB930970CC8BE2778D78DC5D07EDA2BF483CCD6099BFF91681CDDD1056A6DA030CEDE2EF8129F77EF9B62BAA1177AD7A6D37388DD50E3F0D7146F9E39D927BECC1D80785BB519FC3C1682FDD8D120473E85E866DFD0B6E3DE17D350FE94957
	D487BFC7BDB84AB96A41G6728874D4F5CEFBBB87A7D083B8E863E9F91FED07F1BB62C2F7CD783293B79D0AF51G09GE95741FA6EBA6CBB2507C85FE1B7460DF36B91DB97B9DBA43F8427227EF5B5014B1AD7B3766F47ED29A1ED6C028B069587110F789DB5243ED4171BA2992E24067848E36F6AD699A9476F29213F53A6F7D2B4DB73CFCF41FDC045F5388FC8CFA538D6C82B82575C09FB03E0014B17691E9B520957611C3C146E43532E07BD4475087B99459D0234CD60E6F686EADF04EF226DA94D4043F4BC
	9CD047F87683C6B06EB5188F7E31877D97FCBD7A65C391EB0C9265DD59CD462CE4C867FCA3B6338C209E208D407D8D24D9C5673AD0C84F870881C884481E8274D31066466EF61D8DC4065EA6B7CF9379B95F7077E372A83EED535173BB3FBE3A0CF10755BADAB3FF0E36B75347A5D8FF4BBBC5FC568465D426E09F7B57DA72DBE0016627A60F4BADFB54E106253F7A436DFE4E67ECB1846405CF993DCDB19BDA9A491C31GE8E3G12G32EE00F5B5C0A5C083C0BB008A908290813090A09AA081A091A06DC698A3
	EEC47B0F9CA99E45FE9E8825830547EAA7C385376331A6F860F8F7E4DBA099B0529C0735E663059E39C3ED3142B44D572BD20F38CB813658EE4479263E87E31EF3EE44D85FG25DD866995GE9D31136CF4C5579D3473B3E44B547DFCFCB12785D174EG3F7BC227D09E2F947786DA260A3B30BEE76DA1008F1FBAFA9DB2FF359FA56B85AFDF37B0D933FC6EB9ACGB9CB47104578CAFD786201A7F1DCFC9DBEF79B520221CD840F4E476A6D913728B7203785E3DD3C669675960C75362D0F569B24D7G245DA456
	949ED1EFB7CD345E8EB413F7A62766BC49A7F3D64BCDD8A75BF66A4B35907051B70D27CED2CF861E7B5EB8B9765C3761A47D3D9D1833DA065D7C0ECD5EC9D24F2E4ECE2FAC665837CFE68B5CB1EF134BEBA717EF581AF101B81FCD38092D99D742B3EB9A3B1F4A56F1792901635B7B681CD88874E553F00D595309EB4C36E9E3477B5CC0B3E51A965F8E1B6637461449386830BB6D0EFE87554EF41F2073EC84504F1F0636765412336EA6452512F3E8F2665BACFB6C44E77C7CF5F736AE9F4FFB79BDFD1FF368F8
	6E83E9BA342169F8D6BD2F4B0C2684728F81B01F3EA86434CEE7BEF543F34AF4F65F1779681408094E19EE3E8E577BE8BE50AC192E79A8EE3A1F0F7248BCF452BAE4D8031FEAA772EADD0A4BF3526A46CC1637BB0572581D861EB91B34FD975E87F8F533065B43F1CB2A49DA3059673BEFC6415679877C76EA247C4B27633756A93DE679ABC57E63267CD9A118BFE04ADFA0722BFB4C72ABC47E63267975A1B8CF3DFD0A60455A19CD253CCF951EA23C62B713684FE031DDA1DB6BA431B3C710754F96DC937D1162
	D440F5D8D0CEF7E360B14055B87698B054E83E7E3D52683F7E7DC623797A7726C67D7A1761EEE9A47D348B6662D6C1F73B401D9538B0AA1360BE375B54EE017F96350B606BE522907191542EC10AEB455F4154614EF25CF79A49B886C56EF6F91077FB0DE41F5EE9D7947E5DAF2531BBE1D8937ED8C4ED343B2DBA3BFFA8F044EEB6E63FBE6A1D3140F3D84EA479CECE180F455EFECB57A121A146586FFFD27C1C90E36CF7C42CF1A2A1AF6BE61C2386FC66DEB760275CBC1EB95B6EF717024E4B1CB74AC486EB6B
	3B94EC7F5A59CA19026DDF43C5B97C693E6B7027BBE2C2F75C046EE22FBF5D55DEBD9DAB572326774B467647131364CB1EB09F2B673B68FAE6714D467A1FCB7151B79B6BFFD79F56FF9C64D55FA256A8BE775542813F601671547FA7674D760ACBB9EFBCFFA69AE4255D6AAFBB8FF0A5G07F4F92CFE2AF77A57D94DCEFD1D31324527E059DAC0C6482D3836AB3460BAF0492D621BF87ABDC5B22189853D2198C78AE981B41F3F6ABB8B240031693D981B665F46F5627A31AE54BC2EB8CA2C71B36DBD760C263FF7
	DC7A59FE6105C631DF203F93600695CCD5952EE3783DD5F1E7E1A4947D708ABDA3D8G69E80065216833673A10B6D9507E830D73D75C86EB94G5BEDF80FE5CDAF7AFD84F08BEEC399E7FA516FABEE933FCBC9E98BE7C05B83E8190134D7C5DCD619B1D17F5123973E7ED6451EA8C2482E94FB32F801FBC550655C0E380785DD0340F9054F26885CAB823768F63C33E35DCE4A4567FBBA55B1FC7E50EA8D0FD39D6FBB09BD12F87F236E7D7A1EEB9F273C7B1A6AE0FD74E61331F6C62F7F189CC46BA289FC19B693
	E3C13F360BBBA8B3F10C3A27CF73D1246E3D3C6F5A7B6B78B5554147B90F49B8677177D7B75DE33603DF5C42DFCFD35C8824E78A5CEDF45D968969E801FB116292A01DACF04F9C943E1E05386777D39D109E9238470A88AE8452D13330CEFE65779D6F787C19B8C93EE23FEFE847827EDD8D9D6F974F727B2E066223E7996749D60B18A7A12F7A8EACEFF8274FBC897885F70CE71EBC6C333EDBEA6009B711416C6B6CA2F6A45EE1345BC57149F7986DDE5909F6E75C991B295CA96A29CB2FBF66CE4379721D0174
	6F246B4515F79A756F257834B0237EC5F6541F8DF9EA1808CB19FE37B2365F5E1BA45F179366633F23C5672738B0E3F9D368EF9EAD77AB6FB4B177A5C2DE4BDDD85EA9BEF13805000F3EEBBC6DE458F4BD1D40F913CCE4B03E8326654D184D6873E78762FB4DD4DF99272F8B48773FCC7535F2FAA5A05F1069BEA708534F8CEC27A9DFA427DFE442473FFB317B5E6FA9A4DBBCFEAFA9590EB1169893FEE30C45F8AFE979ECE30C656AF9DFDDC73F10C45E7273B4C6E59DB24428AED849181A883212E7E30CAA126E
	6BE1E3EB3F68F22AD62585A4E2D48E63EACFB6625A2A68EE647BF3E360B85CDBCD74EC8AE8EDF7E30C694503F5123FCF8315BF9478A26E564EFA975EEDAC7F2B7B4D63F02B2B6837E7C09FA3EC7DE951861FB256A876AEA7B98D64BC7FD10465CB3EDBFC2BE011C4FBF8D677FE4E72F778D6611A6A607750BB7D677FC51D7E737FD93A7EAC1983FB25B9628E223833EB1B037ED23AB49F756B5E7F9270FC6FEF672F0DDD575EDFFF6CC2BFAC90FE700AF5E31C70C319EEED3DDF777E047D5A7B61DF7F8EBA7E522B
	15E915223D077A5D077E5B6BFE701A6AE0F5FA37C92CA85CA4D694EA47F5F151B731BD7CDF519EAA842E25976926885C23227F048A5C1A9E24CB90B8D78F522D90381FD05E6AEF41387AAD44150A336CD6013B358769A285AED148DBA0F0EBFA717B1134FBC43B566E645F037B505DE29F5AFE8F46AFFA3EA4754F5E5FBFC75E59F8FBE3A379FD13G31044A321202720AE2247D5F0DDB6029BBF6ACEDA67F63558CEFE96D11D3ADC7CE8BBD274FEBFA169C9C0F1E091D2F7537EB6FE1D6CD57A3C563AF1325F83C
	E5627727FAC86C78024BC3BFE26777274439CD9878FBBEC0BCC0A2G3923416FCF09BBF59961016FCFA16483CDD938F6EED3972E4D29CC67FB5D2833A91C696C0667C8B83B9342F4CE936B181981F46A75C6824DA21D4E981DCE666347AF909FB35999BDA8BBBE006CF41D6C885D9A012E097DE4BF5F21490E963ECA8EA0FB15096C4DBCAF7BDE236C9AD913FD2330BB7FDEF359192358DDF62FF1ED13E038DBEE6B54F49D91E7F55581F4250E222B492F9CD33BB4592B040F5A8248FEF39459EEBF598FF5EB3227
	8959A381E47B2C7F0006G4A0E5049E6F3419E1FB57AFC48DFE2A2D78F7C7C1E6EA56939A550E74F45185A9D2749EF0EE8B14D2EFEFBD595B912BDCC3B65E1596A14B06E771A3823D1BFD71C75533DD9902443G664F95E77DA25638F46EC46FGBFED17045D473B51EE693E584C4A7BD07E0CEE3445FD9F5A321852C6C2FAB1C01C20FDDC44A1936F1B28ADACFE48646C97F3C1D1844E859B94AD7F4FA23659AE72BFF0E8795F977CE1A27F2CD34B9F90BE0B9179A3AEADDF0B31E64C437CFB5CDA7E63A27F503C
	516E5DBEC163FAEC4EBB5663BF6F1F6AB1B92B99736CECE81E59BD37A74764B37EA698BF2B1964D95DDD34BD4E1C67F7D6C37161737C4EEAC4ECA18A72326F97E7B5BE3185957041770FA7365054BDB9BF1D9B134F5CCFF70E49E76E278A761D437DC6BF9D2078487B0DFE7AD1B77AE9816465BD00FEDA6C935B9881FC688363715347BE759B81BC8B4745E7712917E860C998975FA29FFDC9401391B99E3ECBBE7A720127ECDCFC3E3122BA60E99D975FE29FBE85F8024660E33F9935F883BB83D1E0682EE5FF35
	D9483CC737F709543D177F9D76AE477F8EFB17EA31F779C12A08690B3D4B54D434254A2E153DD6779E5DFD6D7D706BEB2A037F7DEB3B3F9F626DFABFB05CE7A6E37E97BEE3BE43EDB259933EE332A77C19093CB893F9B3CC645DE9A22F4564CC6B2C491956AFCC6862FD68485FCE5BA045FF71B814BD6AE5A9DD15B248A7B085B50A22FB75FA2435E2F7F30CDEE5C99858DB157A42FD2996A96D1874BA3B5AF3CCCAA377F30EC969747A4EB1A9D5F53907487CCB62E4E99CAC2A74BA7984876474C9D7E816A1FB95
	49EE6CD783652FEEF0F5D81582F2278BF6CF2A34165A29DD23838A0A410BF32A34115E7FAA2097408AD497A9D3817919957F9C0DE713A1C7AFBFCF1F7719D7769454491D40E983CF2D775F87565C724BDFC41D2F4B31C896F0D6374C3C13BEC8CEE809FFD2E00F1C92AA0A6B73CFE0537470B74462BFFFAC52CA23D83616DBDFB2A19AA119BCD5A9D35C98032A7379CCE7945B7167821BCCD2666592B42A143AE75FBED532F4B8D4A9258C12A72DC364FFF211BB15B4F8FD4B73F7CE1A8EEB19D4351953CE9A0BFF
	5F632A7EBB3E15B7F4847CB6B163E1FEA67330769D9F3B3F1ECA3F8FEF853C7AB05EB1EC7E0A7CBD39C077581B9DD698AD2CCAEF0D53C66E349F9ED6E56D5B1D0CF3C43E97E9F8A64BDBFB0E585D236379FFD0CB878802B2463BB8A7GG0C06GGD0CB818294G94G88G88GC8FBB0B602B2463BB8A7GG0C06GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF2A7GGGG
**end of data**/
}
}