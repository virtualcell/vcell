package cbit.vcell.client.data;
import java.awt.event.KeyEvent;

import cbit.image.gui.ImagePaneScroller;
import cbit.image.gui.ImagePaneView;
import cbit.image.gui.ImagePlaneManager;
import cbit.image.gui.ZoomEvent;
import cbit.util.Range;
import cbit.vcell.simdata.DisplayAdapterService;

import javax.swing.ImageIcon;
/**
 * Insert the type's description here.
 * Creation date: (12/14/2004 9:38:13 AM)
 * @author: Frank Morgan
 */
public class KymographPanel extends javax.swing.JPanel implements cbit.vcell.geometry.DrawPaneModel {
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
	private cbit.vcell.simdata.DataManager dataManager = null;
	private int[] dataManagerIndices = null;
	private double[] dataManagerAccumDistances = null;
	private boolean bLocalScaling = false;
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
	private DisplayAdapterService ivjDisplayAdapterService1 = null;
	private cbit.image.gui.ImagePaneScroller ivjImagePaneScroller1 = null;
	private cbit.image.gui.ImagePlaneManager ivjImagePlaneManager1 = null;
	private javax.swing.ImageIcon cmapImageIcon = null;
	private javax.swing.JLabel ivjColorMapJLabel = null;
	private javax.swing.JLabel ivjMaxJLabel = null;
	private javax.swing.JLabel ivjMinJLabel = null;
	private javax.swing.JLabel ivjDisplayJLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean ivjConnPtoP1Aligning = false;
	private ImagePaneView ivjimagePaneView1 = null;
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
		new cbit.plot.SingleXPlot2D("Time", new String[]{currentInfo}, timeData, new String[] {"Time Series (d="+valS+") Vert","Time"/*"Time (s)"*/,"Value"});
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
		new cbit.plot.Plot2D(new String[] { currentInfo },new cbit.plot.PlotData[] { plotData }, new String[] {"Line Scan (t="+valS+") Horz","Distance"/*"Distance (\u00b5m)"*/, "Value"});
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
private cbit.vcell.simdata.DisplayAdapterService getDisplayAdapterService1() {
	if (ivjDisplayAdapterService1 == null) {
		try {
			ivjDisplayAdapterService1 = new DisplayAdapterService();
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
private ImagePaneScroller getImagePaneScroller1() {
	if (ivjImagePaneScroller1 == null) {
		try {
			ivjImagePaneScroller1 = new ImagePaneScroller();
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
private ImagePaneView getimagePaneView1() {
	// user code begin {1}
	// user code end
	return ivjimagePaneView1;
}


/**
 * Return the ImagePlaneManager1 property value.
 * @return cbit.image.ImagePlaneManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ImagePlaneManager getImagePlaneManager1() {
	if (ivjImagePlaneManager1 == null) {
		try {
			ivjImagePlaneManager1 = new ImagePlaneManager();
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
private void imagePaneView1_KeyPressed(KeyEvent keyEvent) {

	int dx =
		(keyEvent.getKeyCode() == KeyEvent.VK_LEFT?-1:0) + 
		(keyEvent.getKeyCode() == KeyEvent.VK_RIGHT?1:0);
	int dy =
		(keyEvent.getKeyCode() == KeyEvent.VK_UP?-1:0) + 
		(keyEvent.getKeyCode() == KeyEvent.VK_DOWN?1:0);
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
	cbit.vcell.simdata.DataManager argDataManager,
	String variableName,double initTime,int step,double endTime,int[] indices,
	double[] accumDistances,
	boolean waitOnInitialLoad,
	double argInitialLineScanTime)
				throws cbit.util.DataAccessException{
	
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

	cbit.vcell.math.DataIdentifier[] sortedDataIDs =
		cbit.vcell.math.VariableType.collectSimilarDataTypes(variableName,dataManager.getDataIdentifiers());
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
private void initDataManagerVariable(String variableName) {

	final String finalVarName = variableName;
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
	
	cbit.vcell.simdata.SourceDataInfo sdi =
		new cbit.vcell.simdata.SourceDataInfo(
			cbit.vcell.simdata.SourceDataInfo.RAW_VALUE_TYPE,
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
	
	getImagePaneScroller1().getImagePaneModel().setMode(cbit.image.gui.ImagePaneModel.MESH_MODE);
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
		}catch(cbit.util.UserCancelException e){
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
private void setimagePaneView1(ImagePaneView newValue) {
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
	getImagePaneScroller1().zooming(new cbit.image.gui.ZoomEvent(getimagePaneView1(),0,-1));
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
	getImagePaneScroller1().zooming(new ZoomEvent(getimagePaneView1(),0,1));
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G500171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DFD8BDCD5D576B0B6E4CCD121E9D151C4E6E5C5C58DCE14D4DA18B429E152048D9599A515B5CCE3937F06E68EAF21C041C445C423B2870B8CDF0544D488A698A61FA0514417DC388738FA3977FA6F65A96A3776E36DF36E396772EA1C5F7F77797BAD4F59EB2F57DE7B3D76BE97A96F3D40746414BCC9CACE17783FAF12A549B8DE12768CFCD005B8BFCBE316247A77BE40AAA96107B4F886C2DE413246
	2CDCE97E4ECCC807C03AA121B1EB9D3C2F16966E1CB48EDEBC68B1C35E1F6FFD634050FAA62CC4BD99B43DB8A59B1EAB81C281C71EE9D6227F0D14FC0E0F61F804649440D11AB529859CD78D6996GFF00A0AB238F0767DBD47339E5251C2EF942B4E9596F6DA9AB089C15CC044DB646DB4D1F05529B4989B6226B2B54EC4E638569D0GD278D4A9AF7593F816579D6F782A2D55645C5653A61B4D5B5A4CA659625CE6B4B88DE5E55F75F7D9BB6C86DBE71D41A21BA57389A2EFFCEAA6651724DD10CE2438921968
	8806778800D801B7F791FC399D6A8240EC47F27D6B1F655C6E2613F7CAF19D335F4A71D0AEEFBBABCF10DDA9D75D472D296BE99DAC48A13E0E073CECC7E3D6A1C0950091409BE0832D7B68159F43332255D9E96D6A32DAAAEC363DE6237D2055A81B613D2C8CF2B86E305CE6359BA50955EFD5EA310EBDC38163BBBCEABE6613E9BB312E038E5FAA457C7652148C9D1F44CCBB199260E03E88F1A8BEE13AAF7028BB4A615A96DDF57FA029F5CBAF6C48C85751DD70079D9971DCCE12CAF739135475D45A868A601D
	70118E4470F7D03C178A4F5A656794CF52011017560DED23E9A55AD21BB6C54A5DFE192E9F7C6F77CD696CE6B6F8F53B59E2A132033A194ECF05AD8FD0FCEE0F02E73658E9BBA5E9A7644576B6E631767544AA52CFEB7B9B33BA813CG7C81C28122G627B310D3D1F7359885BD81D416E5C5BEDB29BE53B44F81F48D98BCF394FE9B754D89CCE0325CDEEB458CD06D6336C10B84D33B944872DAEFC71346D37010E7D32C5369B1CA62B85249B3AE427ECF7B01ABF672CA2B4F61966B6183AE4D050E513D06E7F64
	2C06275960F09E31C11717958A161F1CCB6A2447508892C1G3CB33FE42CA67E4A9C00BE87D0B200FE984CA5723E136DB0DC141555DA2D473BED8D24A41257G717394BAF6D441FB73G7AB8B50F6042A19DA3647CB6779D151CAAD39BB15FE06F47BEF6131CC76A8E68B3CFB6E695835482F41E44B674CA9336212A9537C8450FCD56EDC3FE501E3DCF323693F0D25B17C2298EE2E7B8644581248DB6E6ED9E44713FDCEE62F20ACF3A76ADE2973EE7F23BE8FBBE295F87BD0FA5C7040E8CCDFE2A6ABD1D77A36C
	4FA355417CFFE28D294BEAA8179140FB906B60155CC25626779A9CB23662C3F6E80032D12991DE9F16A66E5FF4AEB7035BC26CA2F3D68E25A3F2A5E98D40BA5E5F082E0DG6FC9EC1E7D9060A300FF82EC8578C47862A12AA3G6C8B87D88A10F72AB12B9C4088608B9082908310F69A5A84C08340A040D40039G4BG8A4E409C81E0861884908690F7866B342F2BA5D9E9A769A3AC477A91166353D1544D7F4F40FC3D107A9A5BF17A887D1CB1C2BFEF78DFD04E7F8D407AF25CAA62CF398716F6D532CD369861
	454173DEDAC57C4E671FF62B6079B3455BE03E2A048942A1702F2FA275E2F0BA6D2656EE27DCAFBB1DA6CB07127F8F2A4B515DEAD326BA54357C9DD24F56DE182F84FD56BBE42CEF33DAACB29DD4848B4FDFC1679A59ACF701511556EE0B1361D72FA66B7163F2BF1F6B562DDEAE11394A5CCD66CD36468DB61076E4331B3AE028A2D32F546CC566213BD273D5BE4A869CD92B1740334A8B67105E8FF18E59BCF023B47328ADC5EFCD64FF5D45A9D33D58389E6A254CA14C264CBCFD5BD752B90F639D4C566E75C4
	2FD8F2CB2CEF2C1666D9487A204A6430198D7D95C6038D2677FA595EE3EA1337A34DDB1F929F56F499BA64BAB32C558F9AAC70EA9779FE8344AE934D076CC6135CAB726E5DC272AA2D367E83F5D6DB376D20EC694639612635447EE8A857D20034F1B8B71C1FC772F1FE6613C22555ECE6D588F9F563081F7FC167D074D541B8666715A27F13F6F53E3FAABF01667FA072A3A09D2B4AE77518542E2E4752F3147C16F350565EB53924BD4AA73AE5D8DA114548F7224D1CDE730EA62F11379F16BFFE8DE9BFE655BA
	E66EB9C4EF0E9577EFFDDC279FFFCEBB072FC9FFC5689E92F47910AE81E87895DBC75860398970CC5EA5B6241B4B7363FE4564ED76E672985D8FA7104E6C4D683C3D7568565B11AE0C53C5F13A5CF355F4B1022E6EDCC657F92E9E5D558E241B4A6942F469DA1DC81788785C71D066710C4EB6DECD37259B69C2819F89102022ABB98F66EBCAF72C87692AG57FC1EC2B7F81EDA5E26DE248B87FC0C0AAE49056ED1C1D76D436A23450751797B2869A675A1DD2C8FABEFA227AB7E351A6EC8BF5259G6F83B06D57
	0CAE5E05EE7DG52151C5F18D58D50F9BEEFAF672B691E94F489004F3CC021AB3AC0CDB76124E8FF0077D351853B506D9BC4BA491715A35FD72F5E5EBA05F4CD10EF837063F4113EEA3AF902AE6FC27045051E68461DC63ADA20B1FA245BFD86693C01A6GA06CC2D7BAB6B67EE5AB6967B4D6F14C50E3685B666805B9272C6CG8DCC30384407E9191CBE69C20CBF9C4AA063572E0BE0FFGE03C88477685ABB07EB06978B4A966056D991E62AA0397B1BB82AFD246764E040A65ECFCDC360E680C053C040BF03C
	C9DE2B5E83D5B89C56B6935DFE71B1A5F82DFA6CE73869EB5F06E73B496EF05640645C07634F0C3544DE32BF63E86E0F8BCFEC1154319BBA32130D124EA401FB7CD35F2A4B9C16F3AAA54BC31953AEE6E55DFC31D266C0CBC386537D700E8F74F43BCDBE3837BE5D1EAD313946887282G22AEA678996DE47EA663A1332FEDA75A67B7F01D54B2798A0F76657BB13BCA7D947B0ACFA726B37B166797F8E8ABB83B313631350358E58399BEG01G4C5ED2EBB677F3A8418130F972EA0AB7D9CCCE1341EC9A100B36
	B2FD7B6CE4EDFA9C676D23B5AA0AC66E7BA92B3F61364F3DF50AD4FE6667943DF817CFCEE7CA5184BEBFCED04A56B731B555970059772A957D99GB4D1GECAE4C4EF4D953CCE4FB6CF2F856CD44B2ACE8A76D5CE59DA061BA60140D5407335364E0B63FDA2136B9B443D757664E9B7CD3BAA7B2DB7DA6AAB69B2FE9CD6D3B86ED8E3321CFE301A67F9234674393308C113A2C5D8E7920157411ED7C3DC7B696115A5BE668F5EE236B56ED87C94EBE723AD574031EAF3ED417273E4DCE56A0766DC78FAAE2EABD4A
	79926A075967F5826D2B835BFC04FDCBCE9423FD4C32FDFDA6671026B1F9FB6DA8EFA648CA9472EE34EFF315F798468158DE8FDDD85E2E0FEE57565D7623E459B6CC89319F26BB502612C9B0DFCCC21B4C0ECFDDED2A334B0E61CC62B2A3044CC01097ADE4FE6654482CB41B5A0E0FCC6685CE1419BB9966CAG36D6FC23C33DD6741A4CE2B153A6234ED71DBB60E9208B50ED6CD1A75B5B2D76AEF73D95B45B553FF7171171270D2C0349A0D163143B0ED6E844E8BD2D12832EE6769F6AC67B434036E5427EBE17
	35F07125309684683B946D7FDD77D86DE7735FEB5D9817773D944732CBFA5016F040A5DD0A36A4356B59326BB2BC9BF8FAA5714500555AF544F6E0EF3753E9350C49977BFBD1FFB548763A8C751F2F2BBFD4685F326DAB2E3F8AB60BE333G5B535F048DF3C1FE1ABF7A3B35EF34FD1B59F6D8AE9FE24C61C2066C124C2EFB7A502EF23049A96C5A5CCFE299302F5D866021AF428E06D7BE33E97A4F3B5D47066F94F62DB9482F9E9FCE7723BDC1E0CB1CBF5655472EFB384BD9DDD5DD0E76BE577F4B7A5DA52776
	4073D825352B553A575A37FD54554C6D5FBB0076B701EDFE17237D5D49EA7BA3G1FG10F705981387FE197D736C4C7ECE396D78AF31FF79C9343F88ECEB39826D8F6F549B777C2F403E32F2DF959F37C8743E4A60B47C32716BEEE1C790680893F61CEF52332360CA34C36EC1BBEACD24DB98AC3F4C8E4B20182FC0C76715D8DF0F8D6E54DBB3D4598D9D9DC34FB0CC6E0ED3E24E821951D7E2796A0E6915AFB7G75DEFE6A17351336EC52C79DED8633CC574A64F468DB3B350BB8E924ED642F42764DE0D7F3G
	5A3E76381E6D1382F09E3869B472CD83DC2C600B51654B3F8A79FEBE03FC4500B3DE05FCD34CFAFC81D7212FE61F992B2FD8EC38A8CDFB0E99FC15F2F6922EFAC76067FBF43F97FD15768CF28145A7DE25BD03BC1201E710793FE94C9A7C8D1EC99DFF172C25E3A11D8410F7B56CF581EA814CD763BE2FF29DE907C31E7955D8E09BE6B1186BDD0221F87636AE150D73BC7D66BA62E79A3F14B07E36E88371338F681C8A9086908530CC58601DBF2C8D7B7AF4EDE0FE76EEA7FE4E8B9C6D3954C44AD7BC8ABE3627
	587AEF75FD0634D587D2088F7476F84DDC36B9D059D3B01F1C331178A4887091G31G09G795720CF6667978F6793BCDF654EE0ED64963A9FAC3D0635119C51F66E217896951E351D4FA81E24FDA0AFED8A1EA1FDE55536DD2FA92EFE8A502499DF2D9B5FB6CDFA26060F79A3FA93596FC6C35EB2006CEB9B330AGEA81BA81FC2FC53FECD87F2FE15B8A69048D56CA40D5DB2D47AB35B1F9E1EB221B2DDA50AF63F25BF0FCC1573A723131731FEB0708B92896E138962E4A44D8D07835988BDAC46BAB8952F9D3
	21BFCF453EFE2810782F8A52ADGFDG13GC28122G9227227FAC6B778E673F8ABC94E9300AD79DA7B2DF5C6F660B113513C8B73ECD1AF46EF5FAFC63CF0CCD5FA4B7BE2D3EA2DDFD55763169EBF463536A7BC6D7DF16E3EC7A563B71E975B5696A1BBFC6FDB1436A33696A1B609C1B3E2B5D78347AFC2E536BF39169245D6B4451F0D5477B5A76CE5CF786DE07E360116E31595A66462735B5DA57561C7A757A36321D230467C59F9B3567FEA4C616GB2532E07391D1667B6B9135BD3F0BD16678DEAD7437523
	1DA7AF6F991B9FEEF063537A21D3E38B33FDE7875A6EA5ECBF58BBB69BFEF263535A902AEB432CF634A1D258105CB7B69BF25C7834B6E45E204777409875AD98D6DF212EBE6F7E31691B68462755D7FA03DEFB6FB0FB681B380B66F5E11031EF565D00F5D1BDB046F1590D4FED9C5135752BC6CF36729DB36F1B3E1DFAFDB390E4065C00FDB3A89967753922BC59A747D61EF75D783465C91B26573E7FA6463B02E9E8434CB15AB0FFD89B9AF4ED18E5C29B4C4286676058ECB8470DCFEB4354E9FAFC252746266F
	DBB7BE2D3EB05DB21FF78C4B9CA54A1CB4C69B5EBEBD1C8D59B76A59F070B85A3079C634A158CD5648EC38F7D89B1AF5EDD8EDC69B1C428663193159E0F763535A9024EB43A2E1C3B864333B1DDF537357A4C8675E0438026584378B5235B7212D8FE5107D1B8D52FEGD38142GA2C57E63B41F1C4D13B85DD240675E4CE20EFCDE4AE852582886A2D30B8BD361ECDCA7F90F542165FDE9C89D69BAB8753D4BD4C9392F1925C34B76934EF7BD5F49BA792E7E1D2CF16F6A5F49BA6F5DD58C8FB254B70CB17F62F7
	073E33F5593B1E6FECBD3341331D719B746DFCE5033E1D4BB68CEDE75286CFF6EE24FE7D837D66425EEDF11A3AE4AA1A479E7B48DDC058019D6E36306D99116E506E77A55CB3DF78BEB96B5CF5B36E6DAEA958A1396F6D7699CDCE2B9D05B448867B2176F631312B3749EDE877799BBD7BE842C6FD9FDD36D15FC7D7ED9C5AC757ED7464A356F6FF5BC17633F5D0B6B3009FC088C0B4C092C0E190762387A89D392313496F1010FB9F457C3E86390B4168F6F758F83BA5F2308DA75D6C5EF70602BACEEF5670C52B
	52FE5CBE259FB6885D6A7E35E294FA31BF6A75DBF55FA47AF278BBEBEB8F2D4D217560F0488E9A98A121F51E3744C96E243A5DD97C5E50A5CBF81E5EEDFD0B536CF3DAAB370B7ADB3C49F3DBF9E113FEDB7953A6D2E607355B5EA6EB5B43DFB7650BBCA558C0B6E8449CFEA71372BB8D768E592DBDADFFAFDF64F9624FF94FD31B63673DB6ACEB1D53DA37DD54DFCDA64EAFD5C16E6D25C98747E2EE87BBC9BFB7873118DB3240B7D2FC1F8A4FE2F1CFF760B7CE5E10979F047D7A47C24F3EEEA95477F5FB21E7DF
	F7958E6F6B1EC24F3EBED5B83C2F479712F659EAB21A6C7CB6A573717F58546DE907707DBDA754781D82FF462E466F92783D8EB5FE374027BB55784F85BE42857F05285B4FBB75E2F93537F89E9718BC5FEE35BC928FE6EB0AD6A32E6DDBEE41185DBC7A0D10BF240381A2EE41B1A9C456539D4BF5AF03E7662D2EF63075482A9E2223G720AEF45330A489E35BDFBC479CABB74F414F339F570B4EAF4B0F95E3DEF2B7BFD894AFB30DDCFDE9F17618B4FC0DDF925FDAE72443D5DB7DC71BB90BF235F853F937537
	6B7A8CA164D677FA636DE840C55EAE2C2F70CE2C2F08DB313E829AC8FDC937B5E66583145C0675F5FB321E6E6A5B78DD5E5BBC692EBC6922FBB716C5B26949336948F115773A2B3C4FC55D684A739ED65E4D03AE723EC03C75148B3E9475789E535363BF2C1E1D27DD646D915F9A9E571397B42C3CA5AE72F608B61AEF5613B7F3D8F9D71EF111379D65BD25ABAFEAD8F90DE964AE3F5950EF6DF6429EB818342D2EB40C8F44833F8438A20072E096A7E06B405BFB495859EEB7F44174C497E8A537533858F2648D
	G7AE000C80084A277F69C87BEDA275C21D75F6BC01BF69CA1E5E2B7CE37F35C47BF90FD7B493DBF1641711774A934BBB9AE6AA01997D8C0524129B1AE796B23FA310FBE304D77F63C5F97239B3B1C8A79213763FCB761FD22C34C53AE6B116385A42FD2FD3FC9643DDA360667395DA190B4F51DC863F63EAFE8A61B48378A75620C3B42ECD69171BB08BB57385054563B4B79314B1526C107A62A521566C8151A060D77AD749CA8927C3374F65C13BABAF0CFAA5D0163D3BD795C023759E6557BF39DC36D83FFA9
	B09DFFBF2BBAD8198F52313A8E4A5BA24A5CE15476CD739DCA3F743A43535A2C025E671DF407760C7D9045075C21BDE3DFA83E730D023C525FE2BDBCCCE31381108E923862F62C1BD001BBC9578519A10DD985A158D70BFA487E8D16D15BCBC3884DD7B4DD9402BC07BAD0762040850BFA7791384DB24A0E9638A75B11B7CE60768A5E84C51E81F93F190E38D33208F7CC97718E37F5D240F445374153BD79B9778871DB78F4664FF4015FC07151AABC737314863C2B3194726AFE07B6C55018E9882443856E5394
	34B3D2605619102E70CE618B01AB9638EBE99C2E94525582E7BA0E3886017B4C0C389601FB0D624C109E3C936F18CCCE43353B8F60E65E09E379769617FB5C6446C8D959FE3B49385750D1CBE786368EF912461D88FF621D388E49CAA7B2CBC241C60016D03CE35E37856F1887DDBFC50AFCCE7F5EFFEC4329143ED0D6AF81212ABB4D9F3726B21DBFAEC71D51101FFB970DCFAED75F09A16DF4DCAABB43E779F73964137D5449E4F57ECBAE79445ED9696A7C0D996AFC62236FB8BFAB5B439FE159129E8E164C53
	3DBD7ED6C453DD2CCC037CD9B87F7264F07F3A65BC36FCB4134B8C398B7D79D48B71E77E5D302683283E9B6371F932D8335F0D34DEEDF44D8E69E000083B314F76E692FBECF639DD365BE5E33DE9004CCD79B3880F9F9D27E301F6A940D40ABB3F230A5B5BBCC3FFFD184C1F84ECC0638D3006277DE7A8FBC4265F58C96F374306EF698C727B9E4F94912F31F6F91A0EB1A5E10DD9D5E158D65F94FD39B38C7B72FEFAEFB59452D1G89G197740BA649EF16EC973E37837CA643BA672CDD03948DFA4EB47FAA207
	48A36FC456F063F98227AD5221E5BADEF55311AEA9B11011FC9FBED46C1279F2D93B2BAF5348CFECC82C5EAA5BC53D69587816365E0006409A0E773EC7DBEFF38CE4AD5885033D4982E39B55E7A71F79727C86BA76DB3A4DE67E5BA2327A0ED14949DA0FF70C7C38FD017C697B0F4B13197C670A15724C916DB052034F07AACF1C2ABCCCF6A669327C7D134F94BDBF8ABD89BAFA52075113F62FD64F77A50A6CC9B24A4E3DF7783E3479DEC5B651DB2A124D7AC6ACDDCFD4813E69DE6CAFC70FE1FF993C975B7B5A
	A384B79752718277BE45C977316F6098EEDA03364D4E5531D38F62C7C047F44CF25311BA8279687B61E8D8DBFCBAD9B346ECD76A20AA856B20703E910CE71A7A5DF51F36FE53F6AA6D68DFA43C43ED98A73B5AF061EE05AE78F365FD61978ACFF617AB4FDFC99762FE3B526DAE66487CFD639879960F1A0F2D0B1E24F37EB777E9E3064FD3FC6DFD5A18612AF4DCF7B4C1DE58FDF89FEC519E324FAA1C8963B6C08300994097E05ACC5CE72D7F00F42EA16F02B5D8ED35F20FECD60570D979FEDEBE5A7231F1EDD6
	864E6FA1F75FA4050E2F7278FDDC58CC563EE278B3F46E654964494AE77E1214AFFB964CDBG35GC600BEG3FD9D8BE43877C8EA5B98229F0BA8DED1D74EBC5F120E212FB95B459E873B84AD1ED9F1BBAEB787E992AC3B397F0319ABC2B4FFCA32937E533346B628FA9BE73FE6D3AB82E855745851067F5BF0EB96B5D62E1F177AB7A926F7734AECF246B1B6C87181E95821FC175172870CC7FF8CACF528D10B793007531B748F5CC5E7FBABB8A61E911FB8B456568AA5808DB480F6E946DAAA9A9AE5C393BE4AB
	EB87013D6A792D746DF3F4FF1700C0F1B8AB43B7614A7C4656EDB3BAF05D5687F96FC96CF7B66838CC7FA5FC2D07E3F69B3F16C41BFCA69B5BE4414D57CAD50FD725FC684116E0EE03B4DBD9CF37346DFA0B49F964A7F519F22FB027A4FB10D34A792BD4F2F86C41689EB38A92B1133E59ACE664874F293359F9B29B177344FAA5EC36FB1DE3DB4A60F59589B4F1331539B1F136F63D927A9DB9F378376B37CBBCAF31F4854F935FD5893E8B57137ECC63B3AEBCAFDB48DE2151C0CF179C4A8D9F643B66B00DB555
	9ADAC92C097F2E02D13DD7C8FB10153768C1DC37BB5B317E4B9F542BFF1CA3D6D710F7B6EE4C2C20F34403B8C6CC7F6483EA57B17753D2C012BB4475CE3B4952017162C45166FC9E44BE30251248E779670B7C19A23F26CA491FAD769A093AB6E37BB46F1304EFDE6FF4EF93ABCD58A6CA6630B6D18D4F66B96C3E81EB93BD4730CDB867683789BC63A7CF9F20891823340960B95AB631131E89714F33D9D5093C5F94105829388D359DFF9B237E08368E09FDE1F3308E3BD3509F11F350DF97FFABC9B80FBE1B2E
	3CAFD53D2FCBBF7BB12D12332A031F236E99629CF50F07F354BD7AE76E2F6D997AFE441BFBBC5F0FC8ABD37F268D6BABC777133A4B0CE84C5A8CD08D508CE00B403EB3310C749DF6E3DCBB376EB3F61033C572CBF0CE545F7D2567328EFEB97C6F471D7BA5E9E32DD698D63A08FC44DF70A529AF2755C6118C6777A571115D545169E4249CFF6917B4F6A337D3B4DB73479F45FD00EF846E83DAE9DCAC9852F385EEDA8B6E8DE2856EAF143764A1704FC3B8A7EF227B70E6C88F8A5CD60A7307F418403D586229FD
	913E513627E68D8F5311EA7024E3A47B81ADE8779A4C87A5B27AAF76A1744BCDA2861A76FBAC6F5F5B481855GE9B3008FC0A0C088C00420D9C1673AF8C8E7BE8CE3BDC0B5C0A7004F43C8635B5622320148D05B944153BEDC0F368C8DBAB80490D59E3F436C22CB9BF7D82122557367D0FBB3F5DC02753741EA6233E9D04E7007310FFD7D958992F81A1F6A6CD61BECF776B3AC7D559FEE77CC17F35FB81017787050ED0A5990CAA72B6C3960FB00DAGA3C09F009F40D400D00039GF1GC9G3973E0FCGA885
	2886E886304DC37BEF7CFA6B907673C0A89DA86C869399AA389D1B3E03870EF7DFFF64C9860CB487E02DF92875185C66ECB040B44D572BD1C6F11782EC891B0773CD010CB14FE573B056F70046760B67B7E6D581B44FC75AFDE2FD611CBF5275A52EB9CE57CA12785D17F68F3F7BC227D06D3D012079584E7BDD4E5A63G1FB8FF68BAE47E8A2FA06B8587DF37B0D9EF3A34076CC79A338A9F99DA96633B5805EF9770D40F086F77AE7B12E660318D4347FFCF55A46A8D68439E413AB8ED9275768856DB9025AD0E
	04FA83E80EC45A9B0E09FA0B9CED3DDDF3103C73359C0D7110B9ABA8926BE4A0C5DD2E5C855076970C24CEBA76FB1E7BAC7B071F7BFA77535F5B01B92B215F466F588C6EA775ECEDEFF748E20E9D37BFD760EA9CF5D607097EE6AADDB32EEA41B5E3698236E62C0327F1813B1F4A56F17D864F716D09F4CE9C84FA1F8538463C2F855718A1BAFE50467BA200A6E601925FCED820DDE33E5DC24645AE134554555DC535B35D9F5039B78968B39FC5DB0FD512336EBA3355C94E214919EF3DECB7911F717357CFF3D5
	79F85E4B4E21FFF2B9075E75A873C193BC4D0F62D975392DFAB45E707487E0BE7DD1C61A60C719CFA3609973A83B6F4BFCB4CB6C4D16BD2A3F8ED77BA8F3E1E3D641C245C7A58B5D566179E49EBAE868532C413FEFA172AA2DE62B7D20410619AC6F53E44AE332E8F88256AB7BAE3C8F705A8685370363424B49DA305E653BEFC641567915EEFBB5D27E7205782D756732DEFE1D487F0DAE3FD164BF214B5FA7727DF479FDC57EBADD7E4005B8CF0D2BA6F831F6E6D3A96FD3BE5504D77CE6927D99AC36ABE4EB1D
	47691EAF84E44DCE41B5D18C454D85DCDB8A4A19DB63F98CD8D8B37C9890D3233F7EDDDC633E7EDDD2233F7EFD39C63D7EE538D7EBC8BF6D003938C9503DAEF0BF8ADC9215C9F0BF180C4ECE01CFB970B647D74BC4A162B38F903BFAA92E89FF87D3057B11635EB9C046B1A8F227550E3C6B8E10FDFA3B49EC665F7DD29A138506B5610F428344C6134D20327BA301A3F633B17B8937BB824381630B9FA35F7BC35ED1538396FB3B172EC36297EAE33FF3A8FE59C2ED6C37CE4639D20A023E9205F3548117F5C384
	60E322C6B2E73B5F252073B267CDFB4CDD86EB6BFE6D587E153315496D587E95DCDE27BB5D7ACEF73A6BBB5C69EE6EF027FBC6F627FBDED65331F2156B5E0B9B5E9F874648772BD17331FAAEBCC64F279F53567FD60A5F7C18367E6F90EB2592481B749856FF104B7D3CC440E77FE1C4EB3DA3FAFB45C24E3B0BBF2BB5321AF5E45BG678D602F4AE375B3A1453D4EAECDD15799AB5B1E96ACDB9048087B832E6D463560BA3060F1DC875EC6E99BA0ED8670FE9C63C8895F132E41662F2BDA8AA50F31691D981BE6
	BA92DAF07D987038FEDCF108D8E31D727E797FD1744F9A11FE36DFF8761058AF505F8930418A262C8C57B14C46CB44FEA1CA7861D1830D69C7431A9420BC9AFDF6CFAB52B6C4A36DD63ABF720574D40030E83C47B2CE0CA9D1004BDE04B29E927B262AC5A863F5CABB8869C9GC10276D531EF8ADFB4DA7F490749BB13B3A04A18A4E4F70978692EA790771820B38B5CAAE143D4011BA368E2846EFBA1AF6F0FF8E7473005140B4F77F42AE378023E958A9E27BA5EF74249944B5F67295E9B5D6EB17CA70169301E
	D59D2C0F9AF44678F61DB11E5DC12985DFB67F91E3C14F9A459D14BF629835651862234FD46F2126336F2F85E7D587AB7383475C7D95F14C5DDF3753BDE6A878A5C278EBEDA341C5C1FA2940ADE3FDA5867AF68C62EA681D38D2C8B7885CAD5B042F854E74B1558169C8014B213CF110CEFB926B646B16314DF7874748B7FE54FC7C0C140EF379CFEA6749E20A5F7C24F61E1CAE661292481BA44A7B1A0B7EC440E7BFB512F9525FE51DD428615925A3035977EDAB31237AA92D5D07A83E61A92D5D5186343B8572
	421FC23B6FEFD56BAFFADAF33E7C34A77DD751B13F6EE92D7E6BA93E79E92D7E4FC48C2D937266BE0D7A4F9B757A1671CD9EA35F7CB12E270F533B3CA5315A72BAA93EBCD6DB5EB7455CD78DF9C131D85E57DD5653394FC09FFCE6A46D640C6EFA3A1473566A48605FB36B16370553BBBD72696FF3FCB8FD00C73E353A5F7484F37AF00FFCF3F575C5F37A259E796AF54BA7ADE674F90BBDFDC7B35968FA3F47271E3031984B9BC60C31946970EBE3AC5AFBC9650B35B11608A32E3AEEEE10C45E27748F2BF5997A
	B4B12A1BFFA0E3EAB548EAD80CB12A2CE4322F070D2DE940EAF19A4C8DA09123F298D75B120CEBABAF4157126CB98E57CF690301B6ECB146184237D5C96EBE75D47EF860CBDA2C1C7566BE2BAD3F57477AF1B8FFBA1794GFD5133E86B166455AEE52CB01BBAAC64B41073FCA34A57702CB88FB3C822BD6CD03D4F70F8A6761FG26A36A2C6AE073EB3641FD7EDFE5F01F7FEF23EBA3EF700B7F33B8B7EF93E7E7E142DF352D0A0F0E285E1F72F83E771F73576BE7D587AB7322D674C3F69C7A21DE2C9BCB62508F
	579A153287295EF79B4F3E9F3E7FAF68B8512E14E9C0751E5AFE76F5971CD59D2CCED76A44F6D66B44F6AEB5623A58CB340757C5FB709538DF4BC897A3F0AF0ABB6F718277E7A12F74B9449D96F4D58277A96DFB13A0BDD560A60A7E987C1C6B1E0650C94FA32ECA484B9638E3B2FEBF52ACF00FA8F7721F47FD68A7E29F9A7ABC46AF669FA1754F5EAFA1D543476697686F1BF808A514169497EE5F3595E97F157CBEBCD5470E5B6A497FF8B543312DF25F778D7B8E8BBD3F7BD151B3E55BC8740C6EFCED513F15
	77241F94DDE949A3AF538FA3AE9323FF105E29EB355A69C76C6C5C66C9F17E13G7E4EDC5218358B209A005C51E0F36ABEF1EE533244737DA984A75078ACD14EED8216A8F3AA53195206BA27ADE1BAE782C4ADE1F7C2184EE3E29D335403CE35DEA93EB1ABAFDE51D9942F3DDF7D5E7FA5E4CC76CAF1FFFED73C67BB02A8BBC935C620EBE2B759FBFFD6E4A78BDFB5F8101D21A3FB834F6BF413FD5DD1C536C9586D74A07B2DA16C7609572EED2AB45FFDC434A83AEE94651864C1D76A903A26391563A503A27BA7
	D10E908F323306109D61A6FBE51BA27BF9A1BB4A03EC17759F504447AB3213D432795DB717B5FA668B0DD985AF0C647CBE1D6E216839A550F73E00B135CB8F135F9CD1E21A9D5D2632B2F2A43B17F64B3D3241A2095F279231464097B056F8124A0D06F482C0660BB8EE7608B8DF610B233D833C1D2E9F185D9F37215DCDA047CF48373721AD91AF22AD0114D6FA29B1AB9F2064A5243DD244972BDF9A2DAD8DB4E64864FCAD66822F17F0AE68EDD772F708B2070A7CABBA147CAB84FF02483F27D349FFC27097AD
	457CD813126F2BFC8FA772D30FAB796B843F7F52216E5DEEA1474CFCBE5F2FF3D6D32BF3D613B56CDEBFF2295EBDB777FB70DA507EA6987F6D01B146967EC45B6352255A58C2A245A7AE5546961438FA5A4B50AFDE46756CC9173D77DC40473EBC1258C2C95B587C745530FC7AFE6A9D16CF5FCFF96CBB071735FE5AC87152ABDABFDD5F06FE4A06BC5BAB6827C017D8C3946063DF99091F2ADC6AB789F8F27FB4923EAFDC6AE5B370140E086FEA97FD354053B2A23E27DD74B90147E7C4FC93DC7485GCF700878
	8ADC740583CF74B0FC6CB723E62EE6E7A0E6983AAB59DFED96B2DFD31DFB7CDD75FE5EFF618CA4703FF086B2D11C015C150A384B449948F92962B7B9D57B528F557B5536336F0759E7D587AB731DC6F7BF049955FEE0381FF4467C9617B11F613E57B9DB2A57B9DB7A1D0E3C99BA727E2AA36FEFBA722669483BC5C75ECE9DF925AE72485FCEDBAD4DBCB2834A9E7432146E14B249A7B00595E6336A55E117F2446E2606DEE5096D59D9167A6C3529A9D2DA0D341CDD6D291152487D1C9AA91DDE5F2911D21DD6DB
	9F7989399400B48EA9CEE9B9798487641C10CE53ACCDF68649BE546D74143F22565AE6B09712BBDD30FBF2CAB954CE659A9DD0D08CDE1CF3CA6B687D2FC2FA812C50E9A5E5AAA4BF33621E23702C57642865672B730EB8E4FBE1155C8E1CC67054BB6E7B400A0B96BD9EF434AAAFC5CA81E7F54A4CBB693D64041678A79976A349FE2238AE7F84B6CD8D7FC1AC7EF347A22DB404E5B95C7A62D15188496469145E92B746206ADCBE539942F67C3940BA1D14FE39840DD3CA5D31FB37937635DDCEA9398412878DFD
	64FFF211BB15B4F8F54BF3F7CE1A8A1BB226EA53279DB3967F3E479975F7FC096736F97CB631E599BF13D926FC47476E2FAFE922B1FA404FDD06F78C3FE8A2FFCF4E53BD767AAE830C968673718A0B115CE95F5B6F1415EFF716B691798E246119AC6FAFCD446EE3AA1E7F87D0CB8788A422E3C8D2A7GG0C06GGD0CB818294G94G88G88G500171B4A422E3C8D2A7GG0C06GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0CA7
	GGGG
**end of data**/
}
}