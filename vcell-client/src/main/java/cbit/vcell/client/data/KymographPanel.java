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

import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.vcell.util.DataAccessException;
import org.vcell.util.gui.GeneralGuiUtils;
import org.vcell.util.Range;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.VCDataJobID;

import cbit.image.DisplayAdapterService;
import cbit.image.SourceDataInfo;
import cbit.image.ZoomEvent;
import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.plot.SingleXPlot2D;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.ClientTaskDispatcher.BlockingTimer;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.gui.PdeTimePlotMultipleVariablesPanel.MultiTimePlotHelper;

/**
 * Insert the type's description here.
 * Creation date: (12/14/2004 9:38:13 AM)
 *
 * @author: Frank Morgan
 */
public class KymographPanel extends javax.swing.JPanel implements org.vcell.util.DrawPaneModel {

    private class MinMaxMeanHolder {
        private final double min;
        private final double max;
        private final double mean;
        private final Range range;

        public MinMaxMeanHolder(double argMin, double argMax, double argMean) {
            this.min = argMin;
            this.max = argMax;
            this.mean = argMean;
            this.range = new Range(this.min, this.max);
        }

        //public void update (MinMaxMeanHolder argMMMH){
        //if(argMMMH.min < min){
        //min = argMMMH.min;
        //}
        //if(argMMMH.max > max){
        //max = argMMMH.max;
        //}
        //mean = (min+max)/2;
        //};
        public Range getRange() {
            return this.range;
        }
    }

    //
    private MinMaxMeanHolder allDataMMMH;
    private MinMaxMeanHolder localDistDataMMMH;
    private MinMaxMeanHolder localTimeDataMMMH;
    private MinMaxMeanHolder userDefinedMMMH;
    //private double userDefinedMin,userDefinedMax;
//	private boolean bBlockInitialLoad = false;
    private static final int SCALE_IMAGE_ALL = 0;
    private static final int SCALE_IMAGE_LINESCAN = 1;
    private static final int SCALE_IMAGE_TIMESERIES = 2;
    private static final int SCALE_IMAGE_USERDEFINED = 3;
    private int scaleImageMode = SCALE_IMAGE_ALL;
    private boolean isInit = false;
    private cbit.plot.Plot2D currentLineScanPlot2D = null;
    private cbit.plot.Plot2D currentTimeSeriesPlot2D = null;
    private static final String NORMAL_MESSAGE = "Mouse Click, Arrow Keys Change Graph.  Mouse Menu for Options";
    private String NONE_MESSAGE = NORMAL_MESSAGE;
    private int[] dataManagerIndices = null;
    private int[] crossingMembraneIndices = null;
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
    private DataIdentifier currentDataIdentifier = null;
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
    private cbit.image.DisplayAdapterService ivjDisplayAdapterService1 = null;  //  @jve:decl-index=0:
    private cbit.image.gui.ImagePaneScroller ivjImagePaneScroller1 = null;
    private cbit.image.ImagePlaneManager ivjImagePlaneManager1 = null;
    private javax.swing.ImageIcon cmapImageIcon = null;  //  @jve:decl-index=0:
    private javax.swing.JLabel ivjColorMapJLabel = null;
    private javax.swing.JLabel ivjMaxJLabel = null;
    private javax.swing.JLabel ivjMinJLabel = null;
    private javax.swing.JLabel ivjDisplayJLabel = null;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private boolean ivjConnPtoP1Aligning = false;
    private cbit.image.gui.ImagePaneView ivjimagePaneView1 = null;
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
    private JComboBox<DataIdentifier> ivjVarNamesJComboBox = null;
    private javax.swing.JCheckBox ivjJCheckBox1 = null;
    private javax.swing.JMenuItem ivjCopyLineScanJMenuItem = null;
    private javax.swing.JMenuItem ivjCopyTimeDataJMenuItem = null;
    private cbit.plot.gui.PlotPane ivjPlotPaneLineScan = null;
    private cbit.plot.gui.PlotPane ivjPlotPaneTimeSeries = null;
    private javax.swing.JMenuItem ivjScaleImageAllJMenuItem = null;
    private javax.swing.JMenuItem ivjScaleImageLSJMenuItem = null;
    private javax.swing.JMenuItem ivjScaleImageTSJMenuItem = null;
    private javax.swing.JLabel ivjScaleImageModeJLabel = null;

    private javax.swing.JMenuItem ivjScaleImageUDJMenuItem = null;
    private JCheckBox jCheckBoxColor = null;
    private String title = null;
//	private AsynchClientTask timeSeriesDataRetrievalTask;

    private final MultiTimePlotHelper multiTimePlotHelper;

    class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.KeyListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.beans.PropertyChangeListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == KymographPanel.this.getCopyJMenuItem())
                KymographPanel.this.connEtoC7(e);
            if (e.getSource() == KymographPanel.this.getCopyJButton())
                KymographPanel.this.connEtoC8(e);
            if (e.getSource() == KymographPanel.this.getZoomUpJButton())
                KymographPanel.this.connEtoC9(e);
            if (e.getSource() == KymographPanel.this.getZoomDownJButton())
                KymographPanel.this.connEtoC10(e);
            if (e.getSource() == KymographPanel.this.getVarNamesJComboBox())
                KymographPanel.this.connEtoC13(e);
            if (e.getSource() == KymographPanel.this.getJCheckBox1())
                KymographPanel.this.connEtoC14(e);
            if (e.getSource() == KymographPanel.this.getCopyTimeDataJMenuItem())
                KymographPanel.this.connEtoC15(e);
            if (e.getSource() == KymographPanel.this.getCopyLineScanJMenuItem())
                KymographPanel.this.connEtoC16(e);
            if (e.getSource() == KymographPanel.this.getScaleImageAllJMenuItem())
                KymographPanel.this.connEtoC18(e);
            if (e.getSource() == KymographPanel.this.getScaleImageLSJMenuItem())
                KymographPanel.this.connEtoC19(e);
            if (e.getSource() == KymographPanel.this.getScaleImageTSJMenuItem())
                KymographPanel.this.connEtoC20(e);
            if (e.getSource() == KymographPanel.this.getScaleImageUDJMenuItem())
                KymographPanel.this.connEtoC21(e);
        }

        public void keyPressed(java.awt.event.KeyEvent e) {
            if (e.getSource() == KymographPanel.this.getimagePaneView1())
                KymographPanel.this.connEtoC12(e);
        }

        public void keyReleased(java.awt.event.KeyEvent e) {
        }

        public void keyTyped(java.awt.event.KeyEvent e) {
        }

        public void mouseClicked(java.awt.event.MouseEvent e) {
            if (e.getSource() == KymographPanel.this.getimagePaneView1())
                KymographPanel.this.connEtoC6(e);
        }

        public void mouseDragged(java.awt.event.MouseEvent e) {
            if (e.getSource() == KymographPanel.this.getimagePaneView1())
                KymographPanel.this.connEtoC17(e);
        }

        public void mouseEntered(java.awt.event.MouseEvent e) {
        }

        public void mouseExited(java.awt.event.MouseEvent e) {
            if (e.getSource() == KymographPanel.this.getimagePaneView1())
                KymographPanel.this.connEtoC3(e);
            if (e.getSource() == KymographPanel.this.getImagePaneScroller1())
                KymographPanel.this.connEtoC11(e);
        }

        public void mouseMoved(java.awt.event.MouseEvent e) {
            if (e.getSource() == KymographPanel.this.getimagePaneView1())
                KymographPanel.this.connEtoC2(e);
        }

        public void mousePressed(java.awt.event.MouseEvent e) {
            if (e.getSource() == KymographPanel.this.getimagePaneView1())
                KymographPanel.this.connEtoC5(e);
        }

        public void mouseReleased(java.awt.event.MouseEvent e) {
            if (e.getSource() == KymographPanel.this.getimagePaneView1())
                KymographPanel.this.connEtoC4(e);
        }

        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if (evt.getSource() == KymographPanel.this.getImagePaneScroller1() && (evt.getPropertyName().equals("imagePaneView"))) {
                KymographPanel.this.connPtoP1SetTarget();
            }
            if (evt.getSource() == KymographPanel.this.multiTimePlotHelper && evt.getPropertyName().equals(SimDataConstants.PROPERTY_NAME_DATAIDENTIFIERS)) {
                KymographPanel.this.updateTheVariable(null/*false*/);
            }

        }

    }

    /**
     * Kymograph constructor comment.
     */
    public KymographPanel(PDEDataViewer pdeDataViewer, String title,/*AsynchClientTask timeSeriesDataRetrievalTask,*/MultiTimePlotHelper multiTimePlotHelper) {
        super();
        //	this.timeSeriesDataRetrievalTask=timeSeriesDataRetrievalTask;
        this.title = title;
        this.multiTimePlotHelper = multiTimePlotHelper;

        this.initialize();

    }

    private void updateTheVariable(DataIdentifier override) {
        try {
            DataIdentifier selected = (override != null ? override : (DataIdentifier) this.ivjVarNamesJComboBox.getSelectedItem());//(DataIdentifier)ivjVarNamesJComboBox.getSelectedItem();
            this.ivjVarNamesJComboBox.removeActionListener(this.ivjEventHandler);
            DataIdentifier[] newData = this.multiTimePlotHelper.getCopyOfDisplayedDataIdentifiers();
            ((DefaultComboBoxModel<DataIdentifier>) this.ivjVarNamesJComboBox.getModel()).removeAllElements();  //setListData(newData);
            for (int i = 0; i < newData.length; i++) {
                ((DefaultComboBoxModel<DataIdentifier>) this.ivjVarNamesJComboBox.getModel()).addElement(newData[i]);
            }
            initVariableListSelected(this.ivjVarNamesJComboBox, selected);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.ivjVarNamesJComboBox.addActionListener(this.ivjEventHandler);
        }
        this.initDataManagerVariable();

    }

    private static void initVariableListSelected(JComboBox<DataIdentifier> myContainer, DataIdentifier selected) {
        boolean bHasdataIdentifier = false;
        for (int i = 0; i < myContainer.getModel().getSize(); i++) {
            if (myContainer.getModel().getElementAt(i).equals(selected)) {
                bHasdataIdentifier = true;
                break;
            }
        }
        if (selected != null && bHasdataIdentifier) {
            myContainer.setSelectedItem(selected);
        }
        if (myContainer.getSelectedIndex() == -1) {
            myContainer.setSelectedIndex(0);
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (5/24/2005 8:28:42 AM)
     *
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
        for (int j = 0; j < data.length; j += 1) {
            double value = data[j];
            if (!Double.isNaN(value) && !Double.isInfinite(value)) {
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
                mean += value;
                validCounter += 1;
            }
        }

        if (validCounter != 0) {
            mean /= (double) validCounter;
        } else {
            return null;
        }

        return new MinMaxMeanHolder(min, max, mean);
    }

    private void configureMinMax() {

        this.getScaleImageModeJLabel().setText(
                (this.scaleImageMode == SCALE_IMAGE_ALL ? "All" : "") +
                        (this.scaleImageMode == SCALE_IMAGE_LINESCAN ? "LS" : "") +
                        (this.scaleImageMode == SCALE_IMAGE_TIMESERIES ? "TS" : "") +
                        (this.scaleImageMode == SCALE_IMAGE_USERDEFINED ? "User" : "")
        );

        Range minmaxRange = null;
        switch (this.scaleImageMode) {
            case SCALE_IMAGE_ALL:
                minmaxRange = (this.allDataMMMH != null ? this.allDataMMMH.getRange() : null);
                break;
            case SCALE_IMAGE_LINESCAN:
                minmaxRange = (this.localDistDataMMMH != null ? this.localDistDataMMMH.getRange() : null);
                break;
            case SCALE_IMAGE_TIMESERIES:
                minmaxRange = (this.localTimeDataMMMH != null ? this.localTimeDataMMMH.getRange() : null);
                break;
            case SCALE_IMAGE_USERDEFINED:
                minmaxRange = (this.userDefinedMMMH != null ? this.userDefinedMMMH.getRange() : null);
                break;
        }

        if (minmaxRange == null || minmaxRange.getMin() == minmaxRange.getMax()) {
            this.getColorMapJLabel().setIcon(null);
            this.getColorMapJLabel().setText("No Range");
        } else {
            this.getColorMapJLabel().setIcon(this.cmapImageIcon);
            this.getColorMapJLabel().setText(null);
        }

        if (minmaxRange == null) {
            this.getDisplayAdapterService1().setActiveScaleRange(null);
            this.getDisplayAdapterService1().setValueDomain(null);

            this.getMinJLabel().setText("?");
            this.getMinJLabel().setToolTipText("Unknown");
            this.getMaxJLabel().setText("?");
            this.getMaxJLabel().setToolTipText("Unknown");

            //updateColorMapDisplay();
            this.getimagePaneView1().getImagePaneModel().updateViewPortImage();

        } else if (this.getDisplayAdapterService1().getActiveScaleRange() == null ||
                this.getDisplayAdapterService1().getActiveScaleRange().getMin() != minmaxRange.getMin() ||
                this.getDisplayAdapterService1().getActiveScaleRange().getMax() != minmaxRange.getMax()) {

            this.getDisplayAdapterService1().setValueDomain((this.allDataMMMH != null ? this.allDataMMMH.getRange() : null));
            this.getDisplayAdapterService1().setActiveScaleRange(minmaxRange);

            java.text.DecimalFormat nf = new java.text.DecimalFormat();
            nf.applyPattern("#.###E0");
            this.getMinJLabel().setText((("" + minmaxRange.getMin()).length() < 7 ? minmaxRange.getMin() + "" : nf.format(minmaxRange.getMin())));
            this.getMinJLabel().setToolTipText("" + minmaxRange.getMin());
            this.getMaxJLabel().setText((("" + minmaxRange.getMax()).length() < 7 ? minmaxRange.getMax() + "" : nf.format(minmaxRange.getMax())));
            this.getMaxJLabel().setToolTipText("" + minmaxRange.getMax());

            //updateColorMapDisplay();
            this.getimagePaneView1().getImagePaneModel().updateViewPortImage();
        }

    }


    /**
     * Insert the method's description here.
     * Creation date: (12/16/2004 10:46:05 AM)
     *
     * @param imgX int
     * @param imgY int
     */
    private void configurePlotData(int imgX, int imgY) {

        //
        //TimeScan Data
        //
        double[][] timeData = new double[2][this.currentTimes.length];
        timeData[0] = this.currentTimes;
        timeData[1] = new double[this.currentTimes.length];

        for (int i = 0; i < this.currentTimes.length; i += 1) {
            //timeData[1][i] = timeSeriesDataOrig[1+imgX][i];
            timeData[1][i] = this.rawValues[imgX + (i * this.RESAMP_SIZE)];
        }
        this.localTimeDataMMMH = this.calcMMM(timeData[1]);


        final int MAX_TITLE_VAL_LENGTH = 9;
        DecimalFormat nf = new DecimalFormat();
        String valS = null;
        valS = this.currentDistances[imgX] + "";
        if (valS.length() > MAX_TITLE_VAL_LENGTH) {
            nf.applyPattern("#.###E0");
            valS = nf.format(this.currentDistances[imgX]);
        }

        this.currentTimeSeriesPlot2D =
                new SingleXPlot2D(new SymbolTableEntry[]{this.currentSymbolTablEntry}, this.multiTimePlotHelper.getDataSymbolMetadataResolver(), "Time", new String[]{(this.currentDataIdentifier == null ? "Error" : this.currentDataIdentifier.getName())}, timeData, new String[]{"Time Series (d=" + valS + ") Vert", "Time"/*"Time (s)"*/, "Value"});
        this.getPlotPaneTimeSeries().setPlot2D(this.currentTimeSeriesPlot2D);


        //
        //LineScan Data
        //
        double[] lineData = new double[this.timeSeriesDataOrig.length - 1];
        for (int i = 1; i < this.timeSeriesDataOrig.length; i += 1) {
            lineData[i - 1] = this.timeSeriesDataOrig[i][imgY];
        }
        double[] lineScanDistances = this.accumDistancesDataOrig;

        this.localDistDataMMMH = this.calcMMM(lineData);

        PlotData plotData = new PlotData(lineScanDistances, lineData);
        valS = this.currentTimes[imgY] + "";
        if (valS.length() > MAX_TITLE_VAL_LENGTH) {
            valS = nf.format(this.currentTimes[imgY]);
        }

        this.currentLineScanPlot2D =
                new Plot2D(new SymbolTableEntry[]{this.currentSymbolTablEntry}, this.multiTimePlotHelper.getDataSymbolMetadataResolver(), new String[]{(this.currentDataIdentifier == null ? "Error" : this.currentDataIdentifier.getName())}, new PlotData[]{plotData}, new String[]{"Line Scan (t=" + valS + ") Horz", "Distance"/*"Distance (\u00b5m)"*/, "Value"});
        this.getPlotPaneLineScan().setPlot2D(this.currentLineScanPlot2D);


        Range xRangeTime = new Range(this.currentTimes[0], this.currentTimes[this.currentTimes.length - 1]);
        Range xRangeDist = new Range(lineScanDistances[0], lineScanDistances[lineScanDistances.length - 1]);
        Range yRangeTime = (this.allDataMMMH != null ? this.allDataMMMH.getRange() : null);
        Range yRangeDist = yRangeTime;

        if (this.bLocalScaling) {
            yRangeTime = (this.localTimeDataMMMH != null ? this.localTimeDataMMMH.getRange() : null);
            yRangeDist = (this.localDistDataMMMH != null ? this.localDistDataMMMH.getRange() : null);
        }

        this.getPlotPaneTimeSeries().forceXYRange(xRangeTime, yRangeTime);
        this.getPlotPaneLineScan().forceXYRange(xRangeDist, yRangeDist);

        this.configureMinMax();
        this.getimagePaneView1().repaint();


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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC10:  (ZoomDownJButton.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.zoomDownJButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC11:  (ImagePaneScroller1.mouse.mouseExited(java.awt.event.MouseEvent) --> Kymograph.imagePaneScroller1_MouseExited(Ljava.awt.event.MouseEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC12:  (imagePaneView1.key.keyPressed(java.awt.event.KeyEvent) --> Kymograph.imagePaneView1_KeyPressed(Ljava.awt.event.KeyEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC13:  (JComboBox1.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.jComboBox1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC14:  (JCheckBox1.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.jCheckBox1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC15:  (CopyTimeDataJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.copyTimeDataJMenuItem_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC16:  (CopyLineScanJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.copyLineScanJMenuItem_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC17:  (imagePaneView1.mouseMotion.mouseDragged(java.awt.event.MouseEvent) --> KymographPanel.imagePaneView1_Copy(Ljava.awt.event.MouseEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC18:  (ScaleImageAllJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> KymographPanel.scaleImageModeFromMenu(Ljava.awt.event.ActionEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC19:  (ScaleImageLSJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> KymographPanel.scaleImageModeFromMenu(Ljava.awt.event.ActionEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC2:  (imagePaneView1.mouseMotion.mouseMoved(java.awt.event.MouseEvent) --> Kymograph.imagePaneScroller1_MouseMoved(Ljava.awt.event.MouseEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC20:  (ScaleImageTSJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> KymographPanel.scaleImageModeFromMenu(Ljava.awt.event.ActionEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC21:  (ScaleImageUDJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> KymographPanel.scaleImageModeFromMenu(Ljava.awt.event.ActionEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC3:  (imagePaneView1.mouse.mouseExited(java.awt.event.MouseEvent) --> Kymograph.imagePaneScroller1_MouseMoved(Ljava.awt.event.MouseEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC4:  (imagePaneView1.mouse.mouseReleased(java.awt.event.MouseEvent) --> Kymograph.imagePaneView1_Copy(Ljava.awt.event.MouseEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC5:  (imagePaneView1.mouse.mousePressed(java.awt.event.MouseEvent) --> Kymograph.imagePaneView1_Copy(Ljava.awt.event.MouseEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC6:  (imagePaneView1.mouse.mouseClicked(java.awt.event.MouseEvent) --> Kymograph.imagePaneView1_Copy(Ljava.awt.event.MouseEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC7:  (CopyJMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.copyJMenuItem_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC8:  (CopyJButton.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.copyJMenuItem_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connEtoC9:  (ZoomUpJButton.action.actionPerformed(java.awt.event.ActionEvent) --> Kymograph.zoomUpJButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
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
            this.handleException(ivjExc);
        }
    }


    /**
     * connPtoP1SetTarget:  (ImagePaneScroller1.imagePaneView <--> imagePaneView1.this)
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connPtoP1SetTarget() {
        /* Set the target from the source */
        try {
            if (!ivjConnPtoP1Aligning) {
                // user code begin {1}
                // user code end
                this.ivjConnPtoP1Aligning = true;
                this.setimagePaneView1(this.getImagePaneScroller1().getImagePaneView());
                // user code begin {2}
                // user code end
                this.ivjConnPtoP1Aligning = false;
            }
        } catch (java.lang.Throwable ivjExc) {
            this.ivjConnPtoP1Aligning = false;
            // user code begin {3}
            // user code end
            this.handleException(ivjExc);
        }
    }


    /**
     * Comment
     */
    private void copyJMenuItem_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        StringBuffer sb = new StringBuffer();
        sb.append("LineScan-Time Data (" + this.currentDataIdentifier.getName() + ")" +
                " Distances(columns) from " + this.currentDistances[0] + " to " + this.currentDistances[this.currentDistances.length - 1] + " along sample line " +
                " Times(rows) from " + this.currentTimes[0] + " to " + this.currentTimes[this.currentTimes.length - 1] + "\n" +
                "\n");
        sb.append("Min(All)\tMax(All)\tMean(All)\n");
        sb.append((this.allDataMMMH != null ? this.allDataMMMH.min + "\t" + this.allDataMMMH.max + "\t" + this.allDataMMMH.mean : "\"?\"\t\"?\"\t\"?\"") + "\n");
        sb.append("Distances");
        for (int i = 0; i < this.currentDistances.length; i += 1) {
            sb.append("\t" + this.currentDistances[i]);
        }
        sb.append("\nTimes\n");
        int counter = 0;
        for (int i = 0; i < this.currentTimes.length; i += 1) {
            sb.append(this.currentTimes[i]);
            for (int j = 0; j < this.currentDistances.length; j += 1) {
                sb.append("\t" + this.rawValues[counter]);
                counter += 1;
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
        sb.append("LineScan Data " + " (" + this.currentDataIdentifier.getName() + ")" +
                " Distances from " + this.currentDistances[0] + " to " + this.currentDistances[this.currentDistances.length - 1] + " along sample line " +
                " at Time=" + this.currentTimes[(int) this.currentSelectionImg.getY()] + "\n");
        sb.append("Min\tMax\tMean\n");
        //sb.append(localDistDataMin+"\t"+localDistDataMax+"\t"+localDistDataMean+"\n");
        sb.append((this.localDistDataMMMH != null ? this.localDistDataMMMH.min + "\t" + this.localDistDataMMMH.max + "\t" + this.localDistDataMMMH.mean : "\"?\"\t\"?\"\t\"?\"") + "\n");
        sb.append("Dist\tValue\n");

        for (int i = 0; i < this.currentLineScanPlot2D.getPlotDatas()[0].getIndependent().length; i += 1) {
            sb.append(this.currentLineScanPlot2D.getPlotDatas()[0].getIndependent()[i] + "\t");
            sb.append(this.currentLineScanPlot2D.getPlotDatas()[0].getDependent()[i] + "\n");
        }

        VCellTransferable.sendToClipboard(sb.toString());

    }


    /**
     * Comment
     */
    private void copyTimeDataJMenuItem_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        StringBuffer sb = new StringBuffer();
        sb.append("TimeSeries Data " + " (" + this.currentDataIdentifier.getName() + ")" +
                " Times from " + this.currentTimes[0] + " to " + this.currentTimes[this.currentTimes.length - 1] +
                " at Distance=" + this.currentDistances[(int) this.currentSelectionImg.getX()] + " (" + this.currentDistances[this.currentDistances.length - 1] + ")\n");
        sb.append("Min\tMax\tMean\n");
        //sb.append(localTimeDataMin+"\t"+localTimeDataMax+"\t"+localTimeDataMean+"\n");
        sb.append((this.localTimeDataMMMH != null ? this.localTimeDataMMMH.min + "\t" + this.localTimeDataMMMH.max + "\t" + this.localTimeDataMMMH.mean : "\"?\"\t\"?\"\t\"?\"") + "\n");
        sb.append("Time\tValue\n");

        for (int i = 0; i < this.currentTimeSeriesPlot2D.getPlotDatas()[0].getIndependent().length; i += 1) {
            sb.append(this.currentTimeSeriesPlot2D.getPlotDatas()[0].getIndependent()[i] + "\t");
            sb.append(this.currentTimeSeriesPlot2D.getPlotDatas()[0].getDependent()[i] + "\n");
        }

        VCellTransferable.sendToClipboard(sb.toString());
    }


    /**
     * This is called when the canvas repaint.
     */
    public void draw(java.awt.Graphics g) {

        g.setColor((!this.getJCheckBoxColor().isSelected() ? java.awt.Color.white : java.awt.Color.red));
        if (this.currentSelectionUnit != null) {
            int width = (int) this.getimagePaneView1().getImagePaneModel().getDimension().getWidth();
            int height = (int) this.getimagePaneView1().getImagePaneModel().getDimension().getHeight();
            int y = (int) ((height - 1) * this.currentSelectionUnit.getY());
            int x = (int) ((width - 1) * this.currentSelectionUnit.getX());
            g.drawLine(0, y - 1, width - 1, y - 1);
            g.drawLine(0, y, width - 1, y);
            g.drawLine(0, y + 1, width - 1, y + 1);

            g.drawLine(x - 1, 0, x - 1, height - 1);
            g.drawLine(x, 0, x, height - 1);
            g.drawLine(x + 1, 0, x + 1, height - 1);
        }
    }


    /**
     * Return the JPanel4 property value.
     *
     * @return javax.swing.JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getButtonsJPanel() {
        if (this.ivjButtonsJPanel == null) {
            try {
                this.ivjButtonsJPanel = new javax.swing.JPanel();
                this.ivjButtonsJPanel.setName("ButtonsJPanel");
                this.ivjButtonsJPanel.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsZoomDownJButton = new java.awt.GridBagConstraints();
                constraintsZoomDownJButton.gridx = 2;
                constraintsZoomDownJButton.gridy = 0;
                constraintsZoomDownJButton.insets = new java.awt.Insets(4, 4, 4, 4);
                this.getButtonsJPanel().add(this.getZoomDownJButton(), constraintsZoomDownJButton);

                java.awt.GridBagConstraints constraintsZoomUpJButton = new java.awt.GridBagConstraints();
                constraintsZoomUpJButton.gridx = 3;
                constraintsZoomUpJButton.gridy = 0;
                constraintsZoomUpJButton.insets = new java.awt.Insets(4, 4, 4, 4);
                this.getButtonsJPanel().add(this.getZoomUpJButton(), constraintsZoomUpJButton);

                java.awt.GridBagConstraints constraintsCopyJButton = new java.awt.GridBagConstraints();
                constraintsCopyJButton.gridx = 4;
                constraintsCopyJButton.gridy = 0;
                constraintsCopyJButton.insets = new java.awt.Insets(4, 4, 4, 4);
                this.getButtonsJPanel().add(this.getCopyJButton(), constraintsCopyJButton);

                java.awt.GridBagConstraints constraintsVarNamesJComboBox = new java.awt.GridBagConstraints();
                constraintsVarNamesJComboBox.gridx = 1;
                constraintsVarNamesJComboBox.gridy = 0;
                constraintsVarNamesJComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsVarNamesJComboBox.weightx = 1.0;
                constraintsVarNamesJComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
                this.getButtonsJPanel().add(this.getVarNamesJComboBox(), constraintsVarNamesJComboBox);

                java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
                constraintsJLabel3.gridx = 0;
                constraintsJLabel3.gridy = 0;
                constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
                this.getButtonsJPanel().add(this.getJLabel3(), constraintsJLabel3);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjButtonsJPanel;
    }

    /**
     * Return the ColorMapJlabel property value.
     *
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getColorMapJLabel() {
        if (this.ivjColorMapJLabel == null) {
            try {
                this.ivjColorMapJLabel = new javax.swing.JLabel();
                this.ivjColorMapJLabel.setName("ColorMapJLabel");
                this.ivjColorMapJLabel.setText("cm");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjColorMapJLabel;
    }

    /**
     * Return the CopyJButton property value.
     *
     * @return javax.swing.JButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getCopyJButton() {
        if (this.ivjCopyJButton == null) {
            try {
                this.ivjCopyJButton = new javax.swing.JButton();
                this.ivjCopyJButton.setName("CopyJButton");
                this.ivjCopyJButton.setText("Copy All");
                this.ivjCopyJButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjCopyJButton;
    }

    /**
     * Return the CopyJMenuItem property value.
     *
     * @return javax.swing.JMenuItem
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getCopyJMenuItem() {
        if (this.ivjCopyJMenuItem == null) {
            try {
                this.ivjCopyJMenuItem = new javax.swing.JMenuItem();
                this.ivjCopyJMenuItem.setName("CopyJMenuItem");
                this.ivjCopyJMenuItem.setText("Copy All");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjCopyJMenuItem;
    }

    /**
     * Return the CopyJPopupMenu property value.
     *
     * @return javax.swing.JPopupMenu
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPopupMenu getCopyJPopupMenu() {
        if (this.ivjCopyJPopupMenu == null) {
            try {
                this.ivjCopyJPopupMenu = new javax.swing.JPopupMenu();
                this.ivjCopyJPopupMenu.setName("CopyJPopupMenu");
                this.ivjCopyJPopupMenu.add(this.getCopyJMenuItem());
                this.ivjCopyJPopupMenu.add(this.getCopyTimeDataJMenuItem());
                this.ivjCopyJPopupMenu.add(this.getCopyLineScanJMenuItem());
                this.ivjCopyJPopupMenu.add(this.getScaleImageAllJMenuItem());
                this.ivjCopyJPopupMenu.add(this.getScaleImageLSJMenuItem());
                this.ivjCopyJPopupMenu.add(this.getScaleImageTSJMenuItem());
                this.ivjCopyJPopupMenu.add(this.getScaleImageUDJMenuItem());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjCopyJPopupMenu;
    }

    /**
     * Return the CopyLineScanJMenuItem property value.
     *
     * @return javax.swing.JMenuItem
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getCopyLineScanJMenuItem() {
        if (this.ivjCopyLineScanJMenuItem == null) {
            try {
                this.ivjCopyLineScanJMenuItem = new javax.swing.JMenuItem();
                this.ivjCopyLineScanJMenuItem.setName("CopyLineScanJMenuItem");
                this.ivjCopyLineScanJMenuItem.setText("Copy LineScan");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjCopyLineScanJMenuItem;
    }


    /**
     * Return the CopyTimeDataJMenuItem property value.
     *
     * @return javax.swing.JMenuItem
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getCopyTimeDataJMenuItem() {
        if (this.ivjCopyTimeDataJMenuItem == null) {
            try {
                this.ivjCopyTimeDataJMenuItem = new javax.swing.JMenuItem();
                this.ivjCopyTimeDataJMenuItem.setName("CopyTimeDataJMenuItem");
                this.ivjCopyTimeDataJMenuItem.setText("Copy TimeSeries");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjCopyTimeDataJMenuItem;
    }


    /**
     * Return the DisplayAdapterService1 property value.
     *
     * @return cbit.image.DisplayAdapterService
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private cbit.image.DisplayAdapterService getDisplayAdapterService1() {
        if (this.ivjDisplayAdapterService1 == null) {
            try {
                this.ivjDisplayAdapterService1 = new cbit.image.DisplayAdapterService();
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjDisplayAdapterService1;
    }


    /**
     * Return the JLabel1 property value.
     *
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getDisplayJLabel() {
        if (this.ivjDisplayJLabel == null) {
            try {
                this.ivjDisplayJLabel = new javax.swing.JLabel();
                this.ivjDisplayJLabel.setName("DisplayJLabel");
                this.ivjDisplayJLabel.setText("DisplayJLabel");
                this.ivjDisplayJLabel.setMaximumSize(new java.awt.Dimension(80, 14));
                this.ivjDisplayJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                this.ivjDisplayJLabel.setPreferredSize(new java.awt.Dimension(80, 14));
                this.ivjDisplayJLabel.setMinimumSize(new java.awt.Dimension(80, 14));
                this.ivjDisplayJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjDisplayJLabel;
    }

    /**
     * Return the JPanel2 property value.
     *
     * @return javax.swing.JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getGraphJPanel() {
        if (this.ivjGraphJPanel == null) {
            try {
                this.ivjGraphJPanel = new javax.swing.JPanel();
                this.ivjGraphJPanel.setName("GraphJPanel");
                this.ivjGraphJPanel.setLayout(new java.awt.GridBagLayout());
                this.ivjGraphJPanel.setPreferredSize(new java.awt.Dimension(300, 350));
                this.ivjGraphJPanel.setMinimumSize(new java.awt.Dimension(300, 350));

                java.awt.GridBagConstraints constraintsPlotPaneTimeSeries = new java.awt.GridBagConstraints();
                constraintsPlotPaneTimeSeries.gridx = 0;
                constraintsPlotPaneTimeSeries.gridy = 1;
                constraintsPlotPaneTimeSeries.fill = java.awt.GridBagConstraints.BOTH;
                constraintsPlotPaneTimeSeries.weightx = 1.0;
                constraintsPlotPaneTimeSeries.weighty = 1.0;
                constraintsPlotPaneTimeSeries.insets = new java.awt.Insets(4, 0, 2, 4);
                this.getGraphJPanel().add(this.getPlotPaneTimeSeries(), constraintsPlotPaneTimeSeries);

                java.awt.GridBagConstraints constraintsPlotPaneLineScan = new java.awt.GridBagConstraints();
                constraintsPlotPaneLineScan.gridx = 0;
                constraintsPlotPaneLineScan.gridy = 0;
                constraintsPlotPaneLineScan.fill = java.awt.GridBagConstraints.BOTH;
                constraintsPlotPaneLineScan.weightx = 1.0;
                constraintsPlotPaneLineScan.weighty = 1.0;
                constraintsPlotPaneLineScan.insets = new java.awt.Insets(2, 0, 4, 4);
                this.getGraphJPanel().add(this.getPlotPaneLineScan(), constraintsPlotPaneLineScan);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjGraphJPanel;
    }

    /**
     * Return the ImagePaneScroller1 property value.
     *
     * @return cbit.image.ImagePaneScroller
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private cbit.image.gui.ImagePaneScroller getImagePaneScroller1() {
        if (this.ivjImagePaneScroller1 == null) {
            try {
                this.ivjImagePaneScroller1 = new cbit.image.gui.ImagePaneScroller();
                this.ivjImagePaneScroller1.setName("ImagePaneScroller1");
                this.ivjImagePaneScroller1.setPreferredSize(new java.awt.Dimension(400, 350));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjImagePaneScroller1;
    }

    /**
     * Return the imagePaneView1 property value.
     *
     * @return cbit.image.ImagePaneView
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private cbit.image.gui.ImagePaneView getimagePaneView1() {
        // user code begin {1}
        // user code end
        return this.ivjimagePaneView1;
    }


    /**
     * Return the ImagePlaneManager1 property value.
     *
     * @return cbit.image.ImagePlaneManager
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private cbit.image.ImagePlaneManager getImagePlaneManager1() {
        if (this.ivjImagePlaneManager1 == null) {
            try {
                this.ivjImagePlaneManager1 = new cbit.image.ImagePlaneManager();
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjImagePlaneManager1;
    }


    /**
     * Return the JCheckBox1 property value.
     *
     * @return javax.swing.JCheckBox
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JCheckBox getJCheckBox1() {
        if (this.ivjJCheckBox1 == null) {
            try {
                this.ivjJCheckBox1 = new javax.swing.JCheckBox();
                this.ivjJCheckBox1.setName("JCheckBox1");
                this.ivjJCheckBox1.setText("Local Scaling");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjJCheckBox1;
    }


    /**
     * Return the JLabel1 property value.
     *
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel1() {
        if (this.ivjJLabel1 == null) {
            try {
                this.ivjJLabel1 = new javax.swing.JLabel();
                this.ivjJLabel1.setName("JLabel1");
                this.ivjJLabel1.setText("Max");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjJLabel1;
    }


    /**
     * Return the JLabel2 property value.
     *
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel2() {
        if (this.ivjJLabel2 == null) {
            try {
                this.ivjJLabel2 = new javax.swing.JLabel();
                this.ivjJLabel2.setName("JLabel2");
                this.ivjJLabel2.setText("Min");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjJLabel2;
    }


    /**
     * Return the JLabel3 property value.
     *
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel3() {
        if (this.ivjJLabel3 == null) {
            try {
                this.ivjJLabel3 = new javax.swing.JLabel();
                this.ivjJLabel3.setName("JLabel3");
                this.ivjJLabel3.setText("Variables");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjJLabel3;
    }


    /**
     * Return the JPanel1 property value.
     *
     * @return javax.swing.JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJPanel1() {
        if (this.ivjJPanel1 == null) {
            try {
                this.ivjJPanel1 = new javax.swing.JPanel();
                this.ivjJPanel1.setName("JPanel1");
                this.ivjJPanel1.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsDisplayJLabel = new java.awt.GridBagConstraints();
                constraintsDisplayJLabel.gridx = 0;
                constraintsDisplayJLabel.gridy = 0;
                constraintsDisplayJLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsDisplayJLabel.weightx = 1.0;
                constraintsDisplayJLabel.insets = new java.awt.Insets(4, 0, 0, 4);
                this.getJPanel1().add(this.getDisplayJLabel(), constraintsDisplayJLabel);

                java.awt.GridBagConstraints constraintsJCheckBox1 = new java.awt.GridBagConstraints();
                constraintsJCheckBox1.gridx = 1;
                constraintsJCheckBox1.gridy = 0;
                constraintsJCheckBox1.insets = new java.awt.Insets(4, 0, 0, 4);
                this.getJPanel1().add(this.getJCheckBox1(), constraintsJCheckBox1);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjJPanel1;
    }

    /**
     * Return the JPanel3 property value.
     *
     * @return javax.swing.JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJPanel3() {
        if (this.ivjJPanel3 == null) {
            try {
                GridBagConstraints gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 0;
                this.ivjJPanel3 = new javax.swing.JPanel();
                this.ivjJPanel3.setName("JPanel3");
                this.ivjJPanel3.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsMaxJLabel = new java.awt.GridBagConstraints();
                constraintsMaxJLabel.gridx = 0;
                constraintsMaxJLabel.gridy = 3;
                java.awt.GridBagConstraints constraintsColorMapJLabel = new java.awt.GridBagConstraints();
                constraintsColorMapJLabel.gridx = 0;
                constraintsColorMapJLabel.gridy = 4;
                constraintsColorMapJLabel.anchor = java.awt.GridBagConstraints.EAST;
                constraintsColorMapJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                this.ivjJPanel3.add(this.getMaxJLabel(), constraintsMaxJLabel);
                java.awt.GridBagConstraints constraintsMinJLabel = new java.awt.GridBagConstraints();
                constraintsMinJLabel.gridx = 0;
                constraintsMinJLabel.gridy = 5;
                this.ivjJPanel3.add(this.getColorMapJLabel(), constraintsColorMapJLabel);
                java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
                constraintsJLabel1.gridx = 0;
                constraintsJLabel1.gridy = 2;
                java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
                constraintsJLabel2.gridx = 0;
                constraintsJLabel2.gridy = 6;
                this.ivjJPanel3.add(this.getMinJLabel(), constraintsMinJLabel);
                this.ivjJPanel3.add(this.getJLabel1(), constraintsJLabel1);
                java.awt.GridBagConstraints constraintsScaleImageModeJLabel = new java.awt.GridBagConstraints();
                constraintsScaleImageModeJLabel.gridx = 0;
                constraintsScaleImageModeJLabel.gridy = 1;
                this.ivjJPanel3.add(this.getJLabel2(), constraintsJLabel2);
                this.ivjJPanel3.add(this.getScaleImageModeJLabel(), constraintsScaleImageModeJLabel);
                this.ivjJPanel3.add(this.getJCheckBoxColor(), gridBagConstraints);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjJPanel3;
    }

    /**
     * Return the JLabel1 property value.
     *
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getMaxJLabel() {
        if (this.ivjMaxJLabel == null) {
            try {
                this.ivjMaxJLabel = new javax.swing.JLabel();
                this.ivjMaxJLabel.setName("MaxJLabel");
                this.ivjMaxJLabel.setText("255");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjMaxJLabel;
    }

    /**
     * Return the JLabel2 property value.
     *
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getMinJLabel() {
        if (this.ivjMinJLabel == null) {
            try {
                this.ivjMinJLabel = new javax.swing.JLabel();
                this.ivjMinJLabel.setName("MinJLabel");
                this.ivjMinJLabel.setText("0");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjMinJLabel;
    }

    /**
     * Return the PlotPane2 property value.
     *
     * @return cbit.plot.PlotPane
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private cbit.plot.gui.PlotPane getPlotPaneLineScan() {
        if (this.ivjPlotPaneLineScan == null) {
            try {
                this.ivjPlotPaneLineScan = new cbit.plot.gui.PlotPane();
                this.ivjPlotPaneLineScan.setName("PlotPaneLineScan");
                this.ivjPlotPaneLineScan.setBorder(new org.vcell.util.gui.LineBorderBean());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjPlotPaneLineScan;
    }

    /**
     * Return the PlotPane1 property value.
     *
     * @return cbit.plot.PlotPane
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private cbit.plot.gui.PlotPane getPlotPaneTimeSeries() {
        if (this.ivjPlotPaneTimeSeries == null) {
            try {
                this.ivjPlotPaneTimeSeries = new cbit.plot.gui.PlotPane();
                this.ivjPlotPaneTimeSeries.setName("PlotPaneTimeSeries");
                this.ivjPlotPaneTimeSeries.setBorder(new org.vcell.util.gui.LineBorderBean());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjPlotPaneTimeSeries;
    }

    /**
     * Return the ScaleImageAllJMenuItem property value.
     *
     * @return javax.swing.JMenuItem
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getScaleImageAllJMenuItem() {
        if (this.ivjScaleImageAllJMenuItem == null) {
            try {
                this.ivjScaleImageAllJMenuItem = new javax.swing.JMenuItem();
                this.ivjScaleImageAllJMenuItem.setName("ScaleImageAllJMenuItem");
                this.ivjScaleImageAllJMenuItem.setText("Scale Image to All Data");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjScaleImageAllJMenuItem;
    }


    /**
     * Return the ScaleImageLSJMenuItem property value.
     *
     * @return javax.swing.JMenuItem
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getScaleImageLSJMenuItem() {
        if (this.ivjScaleImageLSJMenuItem == null) {
            try {
                this.ivjScaleImageLSJMenuItem = new javax.swing.JMenuItem();
                this.ivjScaleImageLSJMenuItem.setName("ScaleImageLSJMenuItem");
                this.ivjScaleImageLSJMenuItem.setText("Scale Image to LineScan");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjScaleImageLSJMenuItem;
    }


    /**
     * Return the SclaeImageModeJLabel property value.
     *
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getScaleImageModeJLabel() {
        if (this.ivjScaleImageModeJLabel == null) {
            try {
                this.ivjScaleImageModeJLabel = new javax.swing.JLabel();
                this.ivjScaleImageModeJLabel.setName("ScaleImageModeJLabel");
                this.ivjScaleImageModeJLabel.setText("All");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjScaleImageModeJLabel;
    }

    /**
     * Return the ScaleImageTSJMenuItem property value.
     *
     * @return javax.swing.JMenuItem
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getScaleImageTSJMenuItem() {
        if (this.ivjScaleImageTSJMenuItem == null) {
            try {
                this.ivjScaleImageTSJMenuItem = new javax.swing.JMenuItem();
                this.ivjScaleImageTSJMenuItem.setName("ScaleImageTSJMenuItem");
                this.ivjScaleImageTSJMenuItem.setText("Scale Image to TimeSeries");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjScaleImageTSJMenuItem;
    }


    /**
     * Return the ScaleImageUDJMenuItem property value.
     *
     * @return javax.swing.JMenuItem
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JMenuItem getScaleImageUDJMenuItem() {
        if (this.ivjScaleImageUDJMenuItem == null) {
            try {
                this.ivjScaleImageUDJMenuItem = new javax.swing.JMenuItem();
                this.ivjScaleImageUDJMenuItem.setName("ScaleImageUDJMenuItem");
                this.ivjScaleImageUDJMenuItem.setText("Scale Image User Defined");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjScaleImageUDJMenuItem;
    }


    /**
     * Return the JComboBox1 property value.
     *
     * @return javax.swing.JComboBox
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JComboBox<DataIdentifier> getVarNamesJComboBox() {
        if (this.ivjVarNamesJComboBox == null) {
            try {
                this.ivjVarNamesJComboBox = new JComboBox();
                this.ivjVarNamesJComboBox.setName("VarNamesJComboBox");
                this.ivjVarNamesJComboBox.setRenderer(this.multiTimePlotHelper.getListCellRenderer());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjVarNamesJComboBox;
    }

    /**
     * Return the ZoomDownJButton property value.
     *
     * @return javax.swing.JButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getZoomDownJButton() {
        if (this.ivjZoomDownJButton == null) {
            try {
                this.ivjZoomDownJButton = new javax.swing.JButton();
                this.ivjZoomDownJButton.setName("ZoomDownJButton");
                this.ivjZoomDownJButton.setText("ZoomOut");
                this.ivjZoomDownJButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjZoomDownJButton;
    }

    /**
     * Return the ZoomUpJButton property value.
     *
     * @return javax.swing.JButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getZoomUpJButton() {
        if (this.ivjZoomUpJButton == null) {
            try {
                this.ivjZoomUpJButton = new javax.swing.JButton();
                this.ivjZoomUpJButton.setName("ZoomUpJButton");
                this.ivjZoomUpJButton.setText("ZoomIn");
                this.ivjZoomUpJButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        return this.ivjZoomUpJButton;
    }

    /**
     * Called whenever the part throws an exception.
     *
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
        this.getDisplayJLabel().setText(this.NONE_MESSAGE);
    }

    private boolean isErrorMode() {
        return this.NONE_MESSAGE.startsWith("ERROR");
    }

    /**
     * Comment
     */
    private void imagePaneScroller1_MouseMoved(java.awt.event.MouseEvent mouseEvent) {
        if (this.isErrorMode()) {
            return;
        }
        java.awt.Point point = this.getimagePaneView1().getImagePoint(mouseEvent.getPoint());
        if (point != null) {
            int imgX = point.x;
            int imgY = point.y;
            this.getDisplayJLabel().setText(
                    "Time = " + this.currentTimes[imgY] +
                            "  Dist = " + this.currentDistances[imgX] +
                            "  Value = " + this.rawValues[(imgY * this.RESAMP_SIZE) + imgX]);
            return;
        }

        this.getDisplayJLabel().setText(this.NONE_MESSAGE);
    }


    /**
     * Comment
     */
    private void imagePaneView1_Copy(java.awt.event.MouseEvent mouseEvent) {

        if (mouseEvent.isPopupTrigger()) {
            this.getCopyJPopupMenu().show(this.getImagePaneScroller1().getImagePaneView(), mouseEvent.getX(), mouseEvent.getY());
        } else if (mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_RELEASED
            //mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_CLICKED
            /*|| mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_DRAGGED*/) {
            if (this.isErrorMode()) {
                return;
            }
            java.awt.Point point = this.getimagePaneView1().getImagePoint(mouseEvent.getPoint());
            if (point != null) {
                this.currentSelectionImg = point;
                this.currentSelectionUnit = this.getimagePaneView1().getImagePointUnitized(mouseEvent.getPoint());
                this.configurePlotData(this.currentSelectionImg.x, this.currentSelectionImg.y);
            }
        }
    }


    /**
     * Comment
     */
    private void imagePaneView1_KeyPressed(java.awt.event.KeyEvent keyEvent) {
        if (this.isErrorMode()) {
            return;
        }
        int dx =
                (keyEvent.getKeyCode() == KeyEvent.VK_LEFT ? -1 : 0) +
                        (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT ? 1 : 0);
        int dy =
                (keyEvent.getKeyCode() == KeyEvent.VK_UP ? -1 : 0) +
                        (keyEvent.getKeyCode() == KeyEvent.VK_DOWN ? 1 : 0);
        if (dx != 0 || dy != 0) {
            int imgWidth = this.getimagePaneView1().getImagePaneModel().getSourceData().getXSize();
            int imgHeight = this.getimagePaneView1().getImagePaneModel().getSourceData().getYSize();
            int newImgX = this.currentSelectionImg.x + dx;
            int newImgY = this.currentSelectionImg.y + dy;
            double newImgXUnit = (double) (newImgX) / (double) (imgWidth - 1);
            double newImgYUnit = (double) (newImgY) / (double) (imgHeight - 1);
            if (newImgXUnit >= 0 && newImgXUnit <= 1 && newImgYUnit >= 0 && newImgYUnit <= 1) {
                this.currentSelectionUnit = new java.awt.geom.Point2D.Double(newImgXUnit, newImgYUnit);
                this.currentSelectionImg = new java.awt.Point(newImgX, newImgY);
                this.configurePlotData(newImgX, newImgY);
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
     *
     * @throws java.lang.Exception The exception description.
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initConnections() throws java.lang.Exception {
        // user code begin {1}
        // user code end
        this.multiTimePlotHelper.addPropertyChangeListener(this.ivjEventHandler);
        this.getImagePaneScroller1().addPropertyChangeListener(this.ivjEventHandler);
        this.getCopyJMenuItem().addActionListener(this.ivjEventHandler);
        this.getCopyJButton().addActionListener(this.ivjEventHandler);
        this.getZoomUpJButton().addActionListener(this.ivjEventHandler);
        this.getZoomDownJButton().addActionListener(this.ivjEventHandler);
        this.getImagePaneScroller1().addMouseListener(this.ivjEventHandler);
        this.getVarNamesJComboBox().addActionListener(this.ivjEventHandler);
        this.getJCheckBox1().addActionListener(this.ivjEventHandler);
        this.getCopyTimeDataJMenuItem().addActionListener(this.ivjEventHandler);
        this.getCopyLineScanJMenuItem().addActionListener(this.ivjEventHandler);
        this.getScaleImageAllJMenuItem().addActionListener(this.ivjEventHandler);
        this.getScaleImageLSJMenuItem().addActionListener(this.ivjEventHandler);
        this.getScaleImageTSJMenuItem().addActionListener(this.ivjEventHandler);
        this.getScaleImageUDJMenuItem().addActionListener(this.ivjEventHandler);
        this.connPtoP1SetTarget();
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/14/2004 9:47:38 AM)
     */
    public void initDataManager(
            DataIdentifier initVar, double initTime, int step, double endTime,
            int[] indices, int[] argCrossingMembraneIndices,
            double[] accumDistances,
            boolean waitOnInitialLoad,
            double argInitialLineScanTime,
            SymbolTable argSymbolTable) throws DataAccessException {

        this.symbolTable = argSymbolTable;
        this.currentSymbolTablEntry = null;
        this.resampleStepOrig = step;
        this.resampleStartTimeOrig = initTime;
        this.resampleEndTimeOrig = endTime;
        this.isInit = true;
        this.initialLineScanTime = argInitialLineScanTime;

        this.dataManagerAccumDistances = accumDistances;
        this.dataManagerIndices = indices;
        this.crossingMembraneIndices = argCrossingMembraneIndices;
        this.currentSelectionImg = new java.awt.Point(0, 0);
        this.currentSelectionUnit = new java.awt.geom.Point2D.Double(0, 0);

        Hashtable<String, Object> hash = new Hashtable<String, Object>();

        ////	AsynchClientTask task1  = new AsynchClientTask("Retrieving list of variables", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
        ////		public void run(Hashtable<String, Object> hashTable) throws Exception {
        ////			DataIdentifier[] sortedDataIDs = KymographPanel.this.multiTimePlotHelper.getCopyOfDisplayedDataIdentifiers();
        ////			hashTable.put("sortedDataIDs", sortedDataIDs);
        ////		}
        ////	};
        //	AsynchClientTask task2 = new AsynchClientTask("Setting list of variables", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
        //		public void run(Hashtable<String, Object> hashTable) throws Exception {
        //			ivjEventHandler.propertyChange(new PropertyChangeEvent(KymographPanel.this.multiTimePlotHelper,PDEDataContext.PROPERTY_NAME_DATAIDENTIFIERS, null, null));
        ////			DataIdentifier[] sortedDataIDs = (DataIdentifier[])hashTable.get("sortedDataIDs");
        ////
        ////			//Add to combobox
        ////			getVarNamesJComboBox().setEnabled(true);
        ////			getVarNamesJComboBox().removeAllItems();
        ////			getVarNamesJComboBox().removeActionListener(ivjEventHandler);
        ////			for(int i=0;i<sortedDataIDs.length;i+= 1){
        ////				getVarNamesJComboBox().addItem(sortedDataIDs[i]);
        ////			}
        ////			getVarNamesJComboBox().addActionListener(ivjEventHandler);
        ////			DataIdentifier selected = KymographPanel.this.multiTimePlotHelper.getPdeDatacontext().getDataIdentifier();
        ////			getVarNamesJComboBox().setSelectedItem((selected==null?getVarNamesJComboBox().getModel().getElementAt(0):selected));
        //		}
        //	};
        //	AsynchClientTask[] taskArray = new AsynchClientTask[]{/*task1,*/ task2};
        //	ClientTaskDispatcher.dispatch(this, hash, taskArray, false);

        this.updateTheVariable(initVar/*true*/);
    }


    private boolean failMethod(final Throwable timeSeriesJobFailed, final DataIdentifier dataIdentifier) {
        if (timeSeriesJobFailed != null) {
            this.NONE_MESSAGE = "ERROR (" + dataIdentifier.getName() + ") -- " + timeSeriesJobFailed.getMessage();
            this.getDisplayJLabel().setText(this.NONE_MESSAGE);
            this.currentSelectionImg.x = 0;
            this.currentSelectionImg.y = 0;
            this.currentSelectionUnit = new Point2D.Double(0, 0);
            try {
                this.initStandAloneTimeSeries_private(new double[][]{{0, 1}, {0, 0}, {0, 0}}, new double[]{0, 1});
            } catch (DataAccessException e) {
                this.failMethod(e, dataIdentifier);
                e.printStackTrace();
            }

            this.getPlotPaneLineScan().setPlot2D(null);
            this.getPlotPaneTimeSeries().setPlot2D(null);
            this.getZoomUpJButton().setEnabled(false);
            this.getZoomDownJButton().setEnabled(false);
            this.getJCheckBox1().setEnabled(false);
            this.getJCheckBoxColor().setEnabled(false);
            this.getCopyJButton().setEnabled(false);
            GeneralGuiUtils.enableComponents(this.getCopyJPopupMenu(), false);
            return true;
        } else {
            this.NONE_MESSAGE = NORMAL_MESSAGE;
            this.getZoomUpJButton().setEnabled(true);
            this.getZoomDownJButton().setEnabled(true);
            this.getJCheckBox1().setEnabled(true);
            this.getJCheckBoxColor().setEnabled(true);
            this.getCopyJButton().setEnabled(true);
            this.getCopyJPopupMenu().setEnabled(true);
            GeneralGuiUtils.enableComponents(this.getCopyJPopupMenu(), true);
            return false;
        }
    }

    private BlockingTimer initVariableTimer;

    /**
     * Insert the method's description here.
     * Creation date: (12/14/2004 9:47:38 AM)
     */
    private void initDataManagerVariable(/*final DataIdentifier dataIdentifer,*//*boolean bFromGUI*/) {
        final DataIdentifier dataIdentifer = (DataIdentifier) this.getVarNamesJComboBox().getSelectedItem();
        //	System.out.println("-----initDataManagerVariable-----"+dataIdentifer+" "+bFromGUI);
        //	Thread.dumpStack();
        if ((this.initVariableTimer = ClientTaskDispatcher.getBlockingTimer(this, this.multiTimePlotHelper.getPdeDatacontext(),
                null, this.initVariableTimer,
                e -> this.initDataManagerVariable(), "KymographPanel get '" + dataIdentifer.getName() + "'")) != null) {
            return;
        }

        //Create SymbolTableEntry for Copy/Paste functionality
        this.currentSymbolTablEntry = (this.symbolTable != null ? this.symbolTable.getEntry(dataIdentifer.getName()) : null);

        String taskName = "Retrieving data for variable '" + dataIdentifer.getName() + "'";
        AsynchClientTask task1 = new AsynchClientTask(taskName, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
            public void run(final Hashtable<String, Object> hashTable) throws Exception {
                double[] timeValues = KymographPanel.this.multiTimePlotHelper.getPdeDatacontext().getTimePoints();
                final TimeSeriesJobSpec timeSeriesJobSpec = new TimeSeriesJobSpec(
                        new String[]{dataIdentifer.getName()}, new int[][]{KymographPanel.this.dataManagerIndices},
                        (KymographPanel.this.crossingMembraneIndices != null ? new int[][]{KymographPanel.this.crossingMembraneIndices} : null),
                        KymographPanel.this.resampleStartTimeOrig, KymographPanel.this.resampleStepOrig, timeValues[timeValues.length - 1],
                        VCDataJobID.createVCDataJobID(KymographPanel.this.multiTimePlotHelper.getUser(), true));
                hashTable.put(PDEDataViewer.StringKey_timeSeriesJobSpec, timeSeriesJobSpec);
            }
        };
        AsynchClientTask task2 = new PDEDataViewer.TimeSeriesDataRetrievalTask("Retrieving Data", this.multiTimePlotHelper, this.multiTimePlotHelper.getPdeDatacontext());//new TimeSeriesDataRetrievalTask(title, PDEDataViewer.this, PDEDataViewer.this.getPdeDataContext());//timeSeriesDataRetrievalTask;

        AsynchClientTask task3 = new AsynchClientTask("Showing kymograph", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {
            public void run(Hashtable<String, Object> hashTable) throws Exception {

                Throwable timeSeriesJobFailed = (Throwable) hashTable.get(PDEDataViewer.StringKey_timeSeriesJobException);
                if (timeSeriesJobFailed == null) {
                    timeSeriesJobFailed = (Throwable) hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_USER);
                }
                if (KymographPanel.this.failMethod(timeSeriesJobFailed, dataIdentifer)) {
                    return;
                }

                TSJobResultsNoStats tsJobResultsNoStats = (TSJobResultsNoStats) hashTable.get(PDEDataViewer.StringKey_timeSeriesJobResults);

                KymographPanel.this.currentDataIdentifier = dataIdentifer;

                final double[][] timeSeries = tsJobResultsNoStats.getTimesAndValuesForVariable(KymographPanel.this.currentDataIdentifier.getName());
                try {
                    KymographPanel.this.initStandAloneTimeSeries_private(timeSeries, KymographPanel.this.dataManagerAccumDistances);
                } catch (Exception e) {
                    KymographPanel.this.failMethod(e, dataIdentifer);
                    throw e;
                }
                if (KymographPanel.this.isInit) {// set crosshair to init time
                    double initTime = KymographPanel.this.initialLineScanTime;//resampleStartTimeOrig;
                    KymographPanel.this.isInit = false;
                    int closestTimeIndex = 0;
                    double closestDiff = Double.MAX_VALUE;
                    for (int i = 0; i < KymographPanel.this.currentTimes.length; i += 1) {
                        double diff = Math.abs(initTime - KymographPanel.this.currentTimes[i]);
                        if (diff < closestDiff) {
                            closestTimeIndex = i;
                            closestDiff = diff;
                        }
                    }
                    KymographPanel.this.currentSelectionImg = new Point(0, closestTimeIndex);
                    KymographPanel.this.currentSelectionUnit = new Point2D.Double(0, (double) closestTimeIndex / (double) (KymographPanel.this.currentTimes.length - 1));
                    KymographPanel.this.configurePlotData((int) KymographPanel.this.currentSelectionImg.getX(), (int) KymographPanel.this.currentSelectionImg.getY());

                    //				ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(pdeDataViewer);
                    ////				final ChildWindow childWindow = childWindowManager.addChildWindow(new javax.swing.JPanel(),this,title);
                    //				final ChildWindow childWindow = childWindowManager.addChildWindow(KymographPanel.this,KymographPanel.this,title);
                    //				childWindow.setIsCenteredOnParent();
                    //				childWindow.pack();
                    //				childWindow.show();
                    //				Timer timer = new Timer(1000,new ActionListener() {
                    //
                    //					@Override
                    //					public void actionPerformed(ActionEvent e) {
                    //						childWindow.toFront();
                    //					}
                    //				});
                    //System.out.println("Kymograph panel ChildWindow requesting focus.  Answer is: "+childWindow.requestFocusInWindow());
                    //				zoomToFill();
                } else {
                    KymographPanel.this.getImagePaneScroller1().zooming(new ZoomEvent(KymographPanel.this.getimagePaneView1(), 0, 0));
                }
            }
        };
        AsynchClientTask[] tasks = (task2 == null ? new AsynchClientTask[]{task1, task3} : new AsynchClientTask[]{task1, task2, task3});
        ClientTaskDispatcher.dispatch(KymographPanel.this, new Hashtable<String, Object>(), tasks, false, true, true, null, false);
        //	if(bFromGUI){
        //		ClientTaskDispatcher.dispatch(KymographPanel.this,  new Hashtable<String, Object>(), tasks, false, true, true, null, false);
        //		System.out.println("Waiting here");
        //	}else{
        //		multiTimePlotHelper.addExtraTasks(tasks);
        //	}
    }


    /**
     * Initialize the class.
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            this.setName("Kymograph");
            this.setLayout(new java.awt.GridBagLayout());
            this.setSize(736, 448);

            java.awt.GridBagConstraints constraintsImagePaneScroller1 = new java.awt.GridBagConstraints();
            constraintsImagePaneScroller1.gridx = 1;
            constraintsImagePaneScroller1.gridy = 0;
            constraintsImagePaneScroller1.fill = java.awt.GridBagConstraints.BOTH;
            constraintsImagePaneScroller1.weightx = 1.0;
            constraintsImagePaneScroller1.weighty = 1.0;
            this.add(this.getImagePaneScroller1(), constraintsImagePaneScroller1);

            java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
            constraintsJPanel1.gridx = 0;
            constraintsJPanel1.gridy = 1;
            constraintsJPanel1.gridwidth = 3;
            constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJPanel1.weightx = 1.0;
            this.add(this.getJPanel1(), constraintsJPanel1);

            java.awt.GridBagConstraints constraintsGraphJPanel = new java.awt.GridBagConstraints();
            constraintsGraphJPanel.gridx = 2;
            constraintsGraphJPanel.gridy = 0;
            constraintsGraphJPanel.fill = java.awt.GridBagConstraints.BOTH;
            this.add(this.getGraphJPanel(), constraintsGraphJPanel);

            java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
            constraintsJPanel3.gridx = 0;
            constraintsJPanel3.gridy = 0;
            constraintsJPanel3.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 0);
            this.add(this.getJPanel3(), constraintsJPanel3);

            java.awt.GridBagConstraints constraintsButtonsJPanel = new java.awt.GridBagConstraints();
            constraintsButtonsJPanel.gridx = 0;
            constraintsButtonsJPanel.gridy = 2;
            constraintsButtonsJPanel.gridwidth = 3;
            constraintsButtonsJPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsButtonsJPanel.weightx = 1.0;
            constraintsButtonsJPanel.insets = new java.awt.Insets(0, 4, 0, 4);
            this.add(this.getButtonsJPanel(), constraintsButtonsJPanel);
            this.initConnections();
            this.connEtoC1();

        } catch (Throwable ivjExc) {
            this.handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
    }

    public void initStandAloneTimeSeries(double[][] timeSeries, double[] accumDistances, DataIdentifier dataIdentifier) throws DataAccessException {

        //timeseries is in the format returned by pdeDatacontext.getTimeSeries
        //timeSeries[0][0...numTimePoints-1] = timePointArray
        //timeSeries[1...numSamplePoints][0...numTimePoints-1] = dataValueArrays

        this.isInit = true;
        this.resampleStartTimeOrig = timeSeries[0][0];
        this.resampleStepOrig = 1;
        this.resampleEndTimeOrig = timeSeries[0][timeSeries[0].length - 1];
        this.currentDataIdentifier = dataIdentifier;
        this.currentSelectionImg = new java.awt.Point(0, 0);
        this.currentSelectionUnit = new java.awt.geom.Point2D.Double(0, 0);
        this.getVarNamesJComboBox().removeAllItems();
        this.getVarNamesJComboBox().removeActionListener(this.ivjEventHandler);
        this.getVarNamesJComboBox().addItem(dataIdentifier);
        this.getVarNamesJComboBox().setEnabled(false);
        this.getVarNamesJComboBox().addActionListener(this.ivjEventHandler);
        this.initStandAloneTimeSeries_private(timeSeries, accumDistances);
    }


    /**
     * Insert the method's description here.
     * Creation date: (12/14/2004 9:47:38 AM)
     */
    private void initStandAloneTimeSeries_private(double[][] timeSeriesOrig, double[] accumDistancesOrig) throws DataAccessException {

        //timesereis is in the format returned by pdeDatacontext.getTimeSeries
        //timeSeries[0][0...numTimePoints-1] = timePointArray
        //timeSeries[1...numSamplePoints][0...numTimePoints-1] = dataValueArrays

        if (accumDistancesOrig.length != (timeSeriesOrig.length - 1)) {
            throw new IllegalArgumentException(this.getClass().getName() + " accumDistances.length != numSamplePoints");
        }


        this.timeSeriesDataOrig = timeSeriesOrig;
        this.accumDistancesDataOrig = accumDistancesOrig;

        this.currentTimes = this.timeSeriesDataOrig[0];
        //Resample for even distances
        this.RESAMP_SIZE = this.timeSeriesDataOrig.length - 1;
        int rvSize = (this.currentTimes.length) * this.RESAMP_SIZE;
        this.rawValues = new double[rvSize];
        double incr = this.accumDistancesDataOrig[this.accumDistancesDataOrig.length - 1] / (double) (this.RESAMP_SIZE - 1);
        this.currentDistances = new double[this.RESAMP_SIZE];
        for (int j = 0; j < this.currentTimes.length; j += 1) {
            int sourceIndex = 0;
            double currentDistance = 0;
            for (int k = 0; k < this.RESAMP_SIZE; k += 1) {
                while (currentDistance > this.accumDistancesDataOrig[sourceIndex + 1]) {
                    sourceIndex += 1;
                }
                double subShort = currentDistance - this.accumDistancesDataOrig[sourceIndex];
                double subLong = this.accumDistancesDataOrig[sourceIndex + 1] - this.accumDistancesDataOrig[sourceIndex];
                double proportion = subShort / subLong;
                //System.out.println("prop="+proportion+" j="+j+" k="+k+" sourceIndex="+sourceIndex+"sourcedist="+accumDistancesDataOrig[sourceIndex]+" currentDistance="+currentDistance);
                double value = this.timeSeriesDataOrig[1 + sourceIndex + (proportion > .5 ? 1 : 0)][j];
                //double value = timeSeriesDataOrig[1+sourceIndex][j] + ((timeSeriesDataOrig[1+sourceIndex+1][j]-timeSeriesDataOrig[1+sourceIndex][j])*proportion);
                //if(k == (RESAMP_SIZE-1)){
                //value = timeSeriesDataOrig[timeSeriesDataOrig.length-1][j];
                //}
                this.rawValues[(j * this.RESAMP_SIZE) + (k)] = value;
                this.currentDistances[k] = currentDistance;
                currentDistance += incr;
                if (currentDistance > this.accumDistancesDataOrig[this.accumDistancesDataOrig.length - 1]) {
                    currentDistance = this.accumDistancesDataOrig[this.accumDistancesDataOrig.length - 1];
                }
            }
        }

        Range minmaxRange = null;
        this.allDataMMMH = this.calcMMM(this.rawValues);
        if (this.allDataMMMH != null) {
            minmaxRange = this.allDataMMMH.getRange();
        }

        SourceDataInfo sdi =
                new SourceDataInfo(
                        SourceDataInfo.RAW_VALUE_TYPE,
                        this.rawValues,
                        minmaxRange,
                        0,
                        this.RESAMP_SIZE, 1,
                        0, this.accumDistancesDataOrig[this.accumDistancesDataOrig.length - 1],
                        this.currentTimes.length, this.RESAMP_SIZE,
                        this.currentTimes[0], this.currentTimes[this.currentTimes.length - 1] - this.currentTimes[0]
                );

        this.getImagePaneScroller1().getImagePaneModel().setSourceData(sdi);
        this.getImagePlaneManager1().setSourceDataInfo(sdi);
        if (this.isInit) {
            this.zoomUpJButton_ActionPerformed(null);
            this.zoomDownJButton_ActionPerformed(null);
            //getImagePaneScroller1().getImagePaneModel().changeZoomToFillViewport();
        }


        this.getDisplayJLabel().setText(this.NONE_MESSAGE);
        this.configurePlotData(this.currentSelectionImg.x, this.currentSelectionImg.y);

    }


    /**
     * Comment
     */
    private void jCheckBox1_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

        this.bLocalScaling = this.getJCheckBox1().isSelected();
        this.configurePlotData((int) this.currentSelectionImg.getX(), (int) this.currentSelectionImg.getY());
    }


    /**
     * Comment
     */
    private void jComboBox1_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        this.initDataManagerVariable();
        this.getimagePaneView1().requestFocusInWindow();
    }


    /**
     * Comment
     */
    private void kymograph_Initialize() {

        this.getDisplayAdapterService1().addColorModelForValues(DisplayAdapterService.createGrayColorModel(), DisplayAdapterService.createGraySpecialColors(), DisplayAdapterService.GRAY);
        this.getDisplayAdapterService1().addColorModelForValues(DisplayAdapterService.createBlueRedColorModel(), DisplayAdapterService.createBlueRedSpecialColors(), DisplayAdapterService.BLUERED);
        this.getDisplayAdapterService1().setActiveColorModelID(DisplayAdapterService.BLUERED);

        this.updateColorMapDisplay();

        this.getImagePaneScroller1().getImagePaneModel().setMode(cbit.image.ImagePaneModel.MESH_MODE);
        this.getImagePaneScroller1().initRowColumnDescriptions("simulation Time", "Distance Along Sample Line");
        this.getimagePaneView1().setDrawPaneModel(this);
        this.getDisplayJLabel().setText(this.NONE_MESSAGE);
        this.getPlotPaneTimeSeries().setBCompact(true);
        this.getPlotPaneLineScan().setBCompact(true);
        this.getImagePaneScroller1().getImagePaneModel().setDisplayAdapterService(this.getDisplayAdapterService1());
        this.getImagePaneScroller1().getImagePaneModel().setBackgroundColor(new java.awt.Color(32, 32, 32));
        this.getImagePaneScroller1().setImagePlaneManager(this.getImagePlaneManager1());

    }


    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            final int numTP = 27;
            final int numDP = 100;
            double[][] timeSeries = new double[numDP + 1][numTP];
            double[] accumArr = new double[numDP];
            timeSeries[0] = new double[numTP];
            for (int i = 0; i < numTP; i += 1) {
                timeSeries[0][i] = i + 10;
            }
            java.util.Random rand = new java.util.Random();
            for (int i = 0; i < numDP; i += 1) {
                accumArr[i] = (i == 0 ? 0 : accumArr[i - 1] + rand.nextDouble() * 10);
                for (int j = 0; j < numTP; j += 1) {
                    //timeSeries[i+1][j] = rand.nextDouble()*10000;
                    timeSeries[i + 1][j] = j * numTP + i;
                }
                if (args.length > 0 && args[0].equals("allNAN")) {
                    java.util.Arrays.fill(timeSeries[i + 1], Double.NaN);
                }
            }
            timeSeries[1][0] = Double.NaN;
            timeSeries[1][1] = Double.NaN;
            timeSeries[2][0] = Double.NaN;
            timeSeries[2][1] = Double.NaN;

            javax.swing.JFrame frame = new javax.swing.JFrame();
            KymographPanel aKymograph;
            aKymograph = new KymographPanel(null, "Kymograph", null);
            frame.setContentPane(aKymograph);
            frame.setSize(aKymograph.getSize());
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }

            });
            java.awt.Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
            aKymograph.initStandAloneTimeSeries(timeSeries, accumArr, new DataIdentifier("Test", VariableType.VOLUME, new Variable.Domain("test"), false, "test"));
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JPanel");
            exception.printStackTrace(System.out);
        }
    }


    /**
     * Comment
     */
    private void scaleImageModeFromMenu(java.awt.event.ActionEvent actionEvent) {

        if (actionEvent.getSource() == this.getScaleImageAllJMenuItem()) {
            this.scaleImageMode = SCALE_IMAGE_ALL;
        } else if (actionEvent.getSource() == this.getScaleImageLSJMenuItem()) {
            this.scaleImageMode = SCALE_IMAGE_LINESCAN;
        } else if (actionEvent.getSource() == this.getScaleImageTSJMenuItem()) {
            this.scaleImageMode = SCALE_IMAGE_TIMESERIES;
        } else if (actionEvent.getSource() == this.getScaleImageUDJMenuItem()) {
            try {
                String userMinMaxS = PopupGenerator.showInputDialog(this,
                        "Enter min and max scaling value separated by comma (e.g. 0,100)",
                        (this.allDataMMMH != null ? this.allDataMMMH.getRange().getMin() + "," + this.allDataMMMH.getRange().getMax() : "?,?"));
                int commaIndex = userMinMaxS.indexOf(",");
                if (commaIndex == -1) {
                    PopupGenerator.showErrorDialog(this, "Min and Max values must be separted by a comma");
                    return;
                }
                try {
                    double min = Double.parseDouble(userMinMaxS.substring(0, commaIndex));
                    double max = Double.parseDouble(userMinMaxS.substring(commaIndex + 1));
                    this.userDefinedMMMH = new MinMaxMeanHolder(min, max, (min + max) / 2);
                } catch (NumberFormatException e) {
                    PopupGenerator.showErrorDialog(this, "Min or Max value cannot be parsed to a number");
                    return;
                }
                this.scaleImageMode = SCALE_IMAGE_USERDEFINED;
            } catch (org.vcell.util.UserCancelException e) {
                //getimagePaneView1().getImagePaneModel().updateViewPortImage();
                this.getimagePaneView1().repaint();
                return;
            }
        }

        this.configureMinMax();
    }


    /**
     * Set the imagePaneView1 to a new value.
     *
     * @param newValue cbit.image.ImagePaneView
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void setimagePaneView1(cbit.image.gui.ImagePaneView newValue) {
        if (this.ivjimagePaneView1 != newValue) {
            try {
                /* Stop listening for events from the current object */
                if (this.ivjimagePaneView1 != null) {
                    this.ivjimagePaneView1.removeMouseMotionListener(this.ivjEventHandler);
                    this.ivjimagePaneView1.removeMouseListener(this.ivjEventHandler);
                    this.ivjimagePaneView1.removeKeyListener(this.ivjEventHandler);
                }
                this.ivjimagePaneView1 = newValue;

                /* Listen for events from the new object */
                if (this.ivjimagePaneView1 != null) {
                    this.ivjimagePaneView1.addMouseMotionListener(this.ivjEventHandler);
                    this.ivjimagePaneView1.addMouseListener(this.ivjEventHandler);
                    this.ivjimagePaneView1.addKeyListener(this.ivjEventHandler);
                }
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                this.handleException(ivjExc);
            }
        }
        // user code begin {3}
        // user code end
    }

    /**
     * Comment
     */
    private void updateColorMapDisplay() {

        if (this.cmapImageIcon == null && this.getDisplayAdapterService1().getActiveColorModel() != null) {
            int[] cmap = this.getDisplayAdapterService1().getActiveColorModel();
            int cmapLength = cmap.length - this.getDisplayAdapterService1().getSpecialColors().length;
            int cmapWidth = 12;
            java.awt.image.BufferedImage cmapImage = new java.awt.image.BufferedImage(cmapWidth, cmapLength, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            int[] row = new int[cmapWidth];
            for (int i = 0; i < cmapLength; i += 1) {
                java.util.Arrays.fill(row, cmap[i]);
                cmapImage.setRGB(0, cmapLength - 1 - i, cmapWidth, 1, row, 0, cmapWidth);
            }
            this.cmapImageIcon = new ImageIcon(cmapImage);
            this.getColorMapJLabel().setIcon(this.cmapImageIcon);
            this.getColorMapJLabel().setText(null);
            this.getColorMapJLabel().repaint();
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
        this.getImagePaneScroller1().zooming(new ZoomEvent(this.getimagePaneView1(), 0, -1));
    }


    /**
     * Insert the method's description here.
     * Creation date: (12/29/2004 12:57:18 PM)
     */
    public void zoomToFill() {

        this.getimagePaneView1().getImagePaneModel().changeZoomToFillViewport();
        this.getImagePaneScroller1().zooming(new ZoomEvent(this.getimagePaneView1(), 0, 0));
    }


    /**
     * Comment
     */
    private void zoomUpJButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        this.getImagePaneScroller1().zooming(new ZoomEvent(this.getimagePaneView1(), 0, 1));
    }

    /**
     * This method initializes jCheckBoxColor
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJCheckBoxColor() {
        if (this.jCheckBoxColor == null) {
            this.jCheckBoxColor = new JCheckBox();
            this.jCheckBoxColor.setText("B/W");
            this.jCheckBoxColor.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JCheckBox jCheckBox = (JCheckBox) e.getSource();
                    if (jCheckBox.isSelected()) {
                        KymographPanel.this.getDisplayAdapterService1().setActiveColorModelID(DisplayAdapterService.GRAY);
                    } else {
                        KymographPanel.this.getDisplayAdapterService1().setActiveColorModelID(DisplayAdapterService.BLUERED);
                    }
                    KymographPanel.this.cmapImageIcon = null;
                    KymographPanel.this.updateColorMapDisplay();
                    KymographPanel.this.getimagePaneView1().getImagePaneModel().updateViewPortImage();
                }
            });
        }
        return this.jCheckBoxColor;
    }
}
