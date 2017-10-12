/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.plot.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.vcell.util.BeanUtils;
import org.vcell.util.ColorUtil;
import org.vcell.util.NumberUtils;
import org.vcell.util.Range;

import cbit.plot.Plot2D;
import cbit.plot.Plot2DSettings;
import cbit.plot.PlotData;

/**
 * Insert the type's description here.
 * Creation date: (2/7/2001 12:38:12 AM)
 * @author: Ion Moraru
 */
public class Plot2DPanel extends JPanel {
	private static final long serialVersionUID = org.vcell.util.Serial.serialFromSVNRevision("$Rev$");
	//
	//
	private final BasicStroke lineBS_10 = new BasicStroke(1.0f);
	private final BasicStroke lineBS_15 = new BasicStroke(1.5f);
	private final BasicStroke lineBS_20 = new BasicStroke(2.0f);
	private String xCompactMinS = "?";
	private String xCompactMaxS = "?";
	private String yCompactMinS = "?";
	private String yCompactMaxS = "?";
	private final int COMP_TICK_LEN = 5;
	private Rectangle plotRectHolder = null;
	private boolean bPlot2DHasInvalidRange = false;
	private Point dragStartPoint = new Point(-1, -1);
	private Point dragEndPoint = new Point(-1, -1);
	private NumberFormat snf = new DecimalFormat("0.000E00");
	private Point lastPoint = null;
	private boolean drawn = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private Point2DSet[] nodes = null;
	private javax.swing.JLabel fieldStatusLabel = new JLabel();
	private Plot2D fieldPlot2D = null;
	private PlotData[] plotDatas = null;
	private boolean ivjConnPtoP1Aligning = false;
	private Plot2D ivjplot2D1 = null;
	private int lMargin = 10;
	private int tMargin = 10;
	private int rMargin = 10;
	private int bMargin = 10;
	private int tick = 10;
	private boolean fieldAutoColor = true;
	private boolean fieldShowNodes = true;
	private boolean fieldSnapToNodes = true;
	private boolean fieldShowCrosshair = true;
	private boolean fieldXAuto = true;
	private boolean fieldYAuto = true;
	private boolean fieldXStretch = false;
	private boolean fieldYStretch = false;
	private int fieldCurrentPlotIndex = 0;
	private double[] fieldYMajorTicks = null;
	private double[] fieldYMinorTicks = null;
	private double[] fieldXMinorTicks = null;
	private double[] fieldXMajorTicks = null;
	private Plot2DSettingsPanel ivjPlot2DSettingsPanel1 = null;
	private boolean ivjConnPtoP11Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP5Aligning = false;
	private boolean ivjConnPtoP6Aligning = false;
	private boolean ivjConnPtoP8Aligning = false;
	private boolean ivjConnPtoP9Aligning = false;
	private Plot2DSettings ivjplot2DSettings1 = null;
	private Range fieldXAutoRange = null;
	private Range fieldYAutoRange = null;
	private boolean ivjConnPtoP12Aligning = false;
	private boolean ivjConnPtoP13Aligning = false;
	private Range fieldXManualRange = null;
	private Range fieldYManualRange = null;
	private boolean ivjConnPtoP10Aligning = false;
	private boolean ivjConnPtoP7Aligning = false;
	private boolean fieldBCompact = false;
	private boolean fieldBStepMode = false;
	private boolean fieldIsHistogram = false; //added March 30,2007. to indicate this plot if for trajectory or histogram
	
	private Color[] autoContrastColors;
	private Color[] userDefinedColors;

class IvjEventHandler implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseDragged(java.awt.event.MouseEvent e) {
			if (e.getSource() == Plot2DPanel.this){
				processDrag0(e);
			}
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {
			if (e.getSource() == Plot2DPanel.this) 
				connEtoC2(e);
		};
		public void mouseMoved(java.awt.event.MouseEvent e) {
			if (e.getSource() == Plot2DPanel.this) 
				connEtoC1(e);
		};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == Plot2DPanel.this){
				processDrag0(e);//To handle possible drag start action
				processMouseButtonEvent(e);//popup menu, etc...
			}
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == Plot2DPanel.this){
				processDrag0(e);//to handle possible drag end action
				processMouseButtonEvent(e);//popup menu, etc...
			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("plot2D"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == Plot2DPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("xManualRange"))) 
				connPtoP7SetTarget();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("xManualRange"))) 
				connPtoP7SetSource();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("xManualRange"))) 
				connEtoC7(evt);
			if (evt.getSource() == Plot2DPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("yManualRange"))) 
				connPtoP10SetTarget();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("yManualRange"))) 
				connPtoP10SetSource();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("yManualRange"))) 
				connEtoC8(evt);
			if (evt.getSource() == Plot2DPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("xAuto"))) 
				connPtoP6SetTarget();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("xAuto"))) 
				connPtoP6SetSource();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("xAuto"))) 
				connEtoC9(evt);
			if (evt.getSource() == Plot2DPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("yAuto"))) 
				connPtoP9SetTarget();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("yAuto"))) 
				connPtoP9SetSource();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("yAuto"))) 
				connEtoC10(evt);
			if (evt.getSource() == Plot2DPanel.this.getPlot2DSettingsPanel1() && (evt.getPropertyName().equals("plot2DSettings"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == Plot2DPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("showCrosshair"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("showCrosshair"))) 
				connPtoP3SetSource();
			if (evt.getSource() == Plot2DPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("showNodes"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("showNodes"))) 
				connPtoP4SetSource();
			if (evt.getSource() == Plot2DPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("snapToNodes"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("snapToNodes"))) 
				connPtoP5SetSource();
			if (evt.getSource() == Plot2DPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("xStretch"))) 
				connPtoP8SetTarget();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("xStretch"))) 
				connPtoP8SetSource();
			if (evt.getSource() == Plot2DPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("yStretch"))) 
				connPtoP11SetTarget();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("yStretch"))) 
				connPtoP11SetSource();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("xStretch"))) 
				connEtoC13(evt);
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("yStretch"))) 
				connEtoC14(evt);
			if (evt.getSource() == Plot2DPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("xAutoRange"))) 
				connPtoP12SetTarget();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("xAutoRange"))) 
				connPtoP12SetSource();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("xAutoRange"))) 
				connEtoC11(evt);
			if (evt.getSource() == Plot2DPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("yAutoRange"))) 
				connPtoP13SetTarget();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("yAutoRange"))) 
				connPtoP13SetSource();
			if (evt.getSource() == Plot2DPanel.this && (evt.getPropertyName().equals("yAutoRange"))) 
				connEtoC12(evt);
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == Plot2DPanel.this.getplot2D1()) 
				connEtoC15(e);
			if (e.getSource() == Plot2DPanel.this.getplot2D1()) 
				connEtoC4(e);
		};
	};

public Plot2DPanel()
{
	super();
	initialize();
}

/**
 * connEtoC1:  (Plot2DPanel.mouseMotion.mouseMoved(java.awt.event.MouseEvent) --> Plot2DPanel.drawCrossHair(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.pointerMoved(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC10:  (Plot2DPanel.yAuto --> Plot2DPanel.updateAxes()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateAxes();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (Plot2DPanel.xStretch --> Plot2DPanel.updateAxes()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateAxes();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC12:  (Plot2DPanel.yStretch --> Plot2DPanel.updateAxes()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateAxes();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (Plot2DPanel.xStretch --> Plot2DPanel.updateAutoRanges()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateAutoRanges();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC14:  (Plot2DPanel.yStretch --> Plot2DPanel.updateAutoRanges()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateAutoRanges();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC15:  (plot2D1.change.stateChanged(javax.swing.event.ChangeEvent) --> Plot2DPanel.updateAutoRanges()V)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateAutoRanges();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC16:  (plot2D1.this --> Plot2DPanel.updateAutoRanges()V)
 * @param value cbit.plot.Plot2D
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(Plot2D value) {
	try {
		// user code begin {1}
		// user code end
		this.updateAutoRanges();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}




/**
 * connEtoC2:  (Plot2DPanel.mouse.mouseExited(java.awt.event.MouseEvent) --> Plot2DPanel.repaint()V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.resetCrossHair();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (plot2D1.this --> Plot2DPanel.updatePlot(Lcbit.plot.Plot2D;)V)
 * @param value cbit.plot.Plot2D
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(Plot2D value) {
	try {
		// user code begin {1}
		// user code end
		this.updatePlot(getplot2D1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (plot2D1.change.stateChanged(javax.swing.event.ChangeEvent) --> Plot2DPanel.updatePlot(Lcbit.plot.Plot2D;)V)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getplot2D1() != null)) {
			this.updateVisiblePlots(getplot2D1());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (Plot2DPanel.initialize() --> Plot2DPanel.plot2DPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5() {
	try {
		// user code begin {1}
		// user code end
		this.controlKeys();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC7:  (Plot2DPanel.xManualRange --> Plot2DPanel.updateAxes()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateAxes();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (Plot2DPanel.yManualRange --> Plot2DPanel.updateAxes()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateAxes();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (Plot2DPanel.xAuto --> Plot2DPanel.updateAxes()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateAxes();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP10SetSource:  (plot2DSettings1.yManualRange <--> Plot2DPanel.yManualRange)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP10SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP10Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP10Aligning = true;
			if ((getplot2DSettings1() != null)) {
				getplot2DSettings1().setYManualRange(this.getYManualRange());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP10Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP10Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP10SetTarget:  (plot2DSettings1.yManualRange <--> Plot2DPanel.yManualRange)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP10SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP10Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP10Aligning = true;
			if ((getplot2DSettings1() != null)) {
				this.setYManualRange(getplot2DSettings1().getYManualRange());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP10Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP10Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP11SetSource:  (plot2DSettings1.yStretch <--> Plot2DPanel.yStretch)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP11SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP11Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP11Aligning = true;
			if ((getplot2DSettings1() != null)) {
				getplot2DSettings1().setYStretch(this.getYStretch());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP11Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP11Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP11SetTarget:  (plot2DSettings1.yStretch <--> Plot2DPanel.yStretch)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP11SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP11Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP11Aligning = true;
			if ((getplot2DSettings1() != null)) {
				this.setYStretch(getplot2DSettings1().getYStretch());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP11Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP11Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP12SetSource:  (plot2DSettings1.xAutoRange <--> Plot2DPanel.xAutoRange)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP12SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP12Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP12Aligning = true;
			if ((getplot2DSettings1() != null)) {
				getplot2DSettings1().setXAutoRange(this.getXAutoRange());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP12Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP12Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP12SetTarget:  (plot2DSettings1.xAutoRange <--> Plot2DPanel.xAutoRange)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP12SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP12Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP12Aligning = true;
			if ((getplot2DSettings1() != null)) {
				this.setXAutoRange(getplot2DSettings1().getXAutoRange());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP12Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP12Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP13SetSource:  (plot2DSettings1.yAutoRange <--> Plot2DPanel.yAutoRange)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP13SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP13Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP13Aligning = true;
			if ((getplot2DSettings1() != null)) {
				getplot2DSettings1().setYAutoRange(this.getYAutoRange());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP13Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP13Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP13SetTarget:  (plot2DSettings1.yAutoRange <--> Plot2DPanel.yAutoRange)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP13SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP13Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP13Aligning = true;
			if ((getplot2DSettings1() != null)) {
				this.setYAutoRange(getplot2DSettings1().getYAutoRange());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP13Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP13Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (Plot2DPanel.plot2D <--> plot2D1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getplot2D1() != null)) {
				this.setPlot2D(getplot2D1());
			}
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
 * connPtoP1SetTarget:  (Plot2DPanel.plot2D <--> plot2D1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setplot2D1(this.getPlot2D());
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
 * connPtoP2SetSource:  (Plot2DSettingsPanel1.plot2DSettings <--> plot2DSettings1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getplot2DSettings1() != null)) {
				getPlot2DSettingsPanel1().setPlot2DSettings(getplot2DSettings1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (Plot2DSettingsPanel1.plot2DSettings <--> plot2DSettings1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setplot2DSettings1(getPlot2DSettingsPanel1().getPlot2DSettings());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (plot2DSettings1.showCrosshair <--> Plot2DPanel.showCrosshair)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getplot2DSettings1() != null)) {
				getplot2DSettings1().setShowCrosshair(this.getShowCrosshair());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (plot2DSettings1.showCrosshair <--> Plot2DPanel.showCrosshair)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getplot2DSettings1() != null)) {
				this.setShowCrosshair(getplot2DSettings1().getShowCrosshair());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetSource:  (plot2DSettings1.showNodes <--> Plot2DPanel.showNodes)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getplot2DSettings1() != null)) {
				getplot2DSettings1().setShowNodes(this.getShowNodes());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetTarget:  (plot2DSettings1.showNodes <--> Plot2DPanel.showNodes)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getplot2DSettings1() != null)) {
				this.setShowNodes(getplot2DSettings1().getShowNodes());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP5SetSource:  (plot2DSettings1.snapToNodes <--> Plot2DPanel.snapToNodes)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getplot2DSettings1() != null)) {
				getplot2DSettings1().setSnapToNodes(this.getSnapToNodes());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP5SetTarget:  (plot2DSettings1.snapToNodes <--> Plot2DPanel.snapToNodes)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getplot2DSettings1() != null)) {
				this.setSnapToNodes(getplot2DSettings1().getSnapToNodes());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP6SetSource:  (plot2DSettings1.xAuto <--> Plot2DPanel.xAuto)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			if ((getplot2DSettings1() != null)) {
				getplot2DSettings1().setXAuto(this.getXAuto());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP6Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP6Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP6SetTarget:  (plot2DSettings1.xAuto <--> Plot2DPanel.xAuto)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			if ((getplot2DSettings1() != null)) {
				this.setXAuto(getplot2DSettings1().getXAuto());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP6Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP6Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP7SetSource:  (plot2DSettings1.xManualRange <--> Plot2DPanel.xManualRange)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP7Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP7Aligning = true;
			if ((getplot2DSettings1() != null)) {
				getplot2DSettings1().setXManualRange(this.getXManualRange());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP7Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP7Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP7SetTarget:  (plot2DSettings1.xManualRange <--> Plot2DPanel.xManualRange)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP7Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP7Aligning = true;
			if ((getplot2DSettings1() != null)) {
				this.setXManualRange(getplot2DSettings1().getXManualRange());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP7Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP7Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP8SetSource:  (plot2DSettings1.xStretch <--> Plot2DPanel.xStretch)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP8SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP8Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP8Aligning = true;
			if ((getplot2DSettings1() != null)) {
				getplot2DSettings1().setXStretch(this.getXStretch());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP8Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP8Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP8SetTarget:  (plot2DSettings1.xStretch <--> Plot2DPanel.xStretch)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP8SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP8Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP8Aligning = true;
			if ((getplot2DSettings1() != null)) {
				this.setXStretch(getplot2DSettings1().getXStretch());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP8Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP8Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP9SetSource:  (plot2DSettings1.yAuto <--> Plot2DPanel.yAuto)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP9SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP9Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP9Aligning = true;
			if ((getplot2DSettings1() != null)) {
				getplot2DSettings1().setYAuto(this.getYAuto());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP9Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP9Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP9SetTarget:  (plot2DSettings1.yAuto <--> Plot2DPanel.yAuto)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP9SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP9Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP9Aligning = true;
			if ((getplot2DSettings1() != null)) {
				this.setYAuto(getplot2DSettings1().getYAuto());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP9Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP9Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private void controlKeys() {
	registerKeyboardAction(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			cycleCurrentPlot(true);
		}
	}, KeyStroke.getKeyStroke("ctrl N"), WHEN_IN_FOCUSED_WINDOW);
	registerKeyboardAction(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			cycleCurrentPlot(false);
		}
	}, KeyStroke.getKeyStroke("ctrl P"), WHEN_IN_FOCUSED_WINDOW);
}


/**
 * Comment
 */
private synchronized void cycleCurrentPlot(boolean up) {
	if (getPlot2D() == null) return;
	if (getPlot2D().getNumberOfVisiblePlots() > 1) {
		int ix = getCurrentPlotIndex();
		do {
			if (up) {
				ix++;
			} else {
				ix--;
			}
			if (ix == - 1) ix = getPlot2D().getNumberOfPlots() - 1;
			if (ix == getPlot2D().getNumberOfPlots()) ix = 0;
		} while (! getPlot2D().isVisiblePlot(ix));
		setCurrentPlotIndex(ix);
	} else if (getPlot2D().getNumberOfVisiblePlots() == 1) {
		return;
	} else {
		setCurrentPlotIndex(-1);
	}
}


/**
 * Comment
 */
private void drawAxes(Graphics2D g) {
	g.setColor(Color.black);
	g.setStroke(lineBS_10);
	// bottom X axis
	g.drawLine(getTick() + getLMargin(), getHeight() - getTick() - getBMargin(), getWidth() - getTick() - getRMargin(), getHeight() - getTick() - getBMargin());
	// left Y axis
	g.drawLine(getTick() + getLMargin(), getHeight() - getTick() - getBMargin(), getTick() + getLMargin(), getTick() + getTMargin());

	if(getBCompact()){
		drawCompactAxesValues(g);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/18/2004 3:26:59 PM)
 */
private void drawCompactAxesValues(Graphics2D g) {
	
	//Draw first and last ticks with text if compact
	if(getBCompact()){
		// bottom X axis
		g.drawLine(
			getTick() + getLMargin(),getHeight() - getTick() - getBMargin(),
			getTick() + getLMargin(),getHeight() - getTick() - getBMargin()+COMP_TICK_LEN);
		g.drawString(xCompactMinS,getTick() + getLMargin()-COMP_TICK_LEN,getHeight() - getTick() +COMP_TICK_LEN+2);
		g.drawLine(
			getWidth() - getTick() - getRMargin(),getHeight() - getTick() - getBMargin(),
			getWidth() - getTick() - getRMargin(),getHeight() - getTick() - getBMargin()+COMP_TICK_LEN);
		g.drawString(xCompactMaxS,
			getWidth() - getTick() - getRMargin() -(int)(g.getFontMetrics().getStringBounds(xCompactMaxS,g).getWidth()),
			getHeight() - getTick() +COMP_TICK_LEN+2);

		// left Y axis
		g.drawLine(
			getTick() + getLMargin(), getHeight() - getTick() - getBMargin(),
			getTick() + getLMargin()-COMP_TICK_LEN, getHeight() - getTick() - getBMargin());
		g.drawString(yCompactMinS,
			getTick() + getLMargin()-(int)(g.getFontMetrics().getStringBounds(yCompactMinS,g).getWidth())-COMP_TICK_LEN-2,
			getHeight() - getTick() - getBMargin());
		g.drawLine(
			getTick() + getLMargin(), getTick() + getTMargin(),
			getTick() + getLMargin()-COMP_TICK_LEN, getTick() + getTMargin());
		g.drawString(yCompactMaxS,
			getTick() + getLMargin()-(int)(g.getFontMetrics().getStringBounds(yCompactMaxS,g).getWidth())-COMP_TICK_LEN-2,
			getTick() + getTMargin()+5);
	}
}


/**
 * Comment
 */
private void drawCrossHair(Graphics2D g, Point p) {
	if (p != null) {
		g.drawLine(p.x, 0, p.x, getHeight());
		g.drawLine(0, p.y, getWidth(), p.y);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 2:22:29 AM)
 * @param plotData cbit.plot.PlotData
 */
private void drawLinePlot(PlotData plotData, int index, Graphics2D g, int renderHints) {
	if (plotData != null) {
		g.setStroke(lineBS_15);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if( plotRectHolder == null ||
			plotRectHolder.x != getLMargin() + getTick() ||
			plotRectHolder.y != getTMargin() + getTick() - (getBCompact()?2:0) ||
			plotRectHolder.width != getWidth() - 2 * getTick() - getLMargin() - getRMargin() ||
			plotRectHolder.height != getHeight() - 2 * getTick() - getTMargin() - getBMargin() + (getBCompact()?4:0)){
				
			plotRectHolder = new Rectangle(
				getLMargin() + getTick(),
				getTMargin() + getTick() - (getBCompact()?2:0),
				getWidth() - 2 * getTick() - getLMargin() - getRMargin(),
				getHeight() - 2 * getTick() - getTMargin() - getBMargin() + (getBCompact()?4:0));
		}
		g.setClip(plotRectHolder);
		//Area plotArea = new Area(plotRect);

		Line2D[] segments = getLinePlotSegments(plotData, index);
		if ((renderHints & Plot2D.RENDERHINT_DRAWLINE) == Plot2D.RENDERHINT_DRAWLINE){
			for (int i=0;i<segments.length;i++){
				if (segments[i].intersects(plotRectHolder)) {
					Line2D line = BeanUtils.clipLine(segments[i], plotRectHolder);
					g.draw(line);
				}
			}
		}
		if (getShowNodes() && ((renderHints & Plot2D.RENDERHINT_DRAWPOINT) == Plot2D.RENDERHINT_DRAWPOINT)) {
//			nodes[index].setPoints(mapPoints(plotData));
			Ellipse2D.Double circle = new Ellipse2D.Double();
			int diameter = 2;
			if (renderHints == Plot2D.RENDERHINT_DRAWPOINT) { // only draw points, make them larger
				diameter = 3;	// 4 is too large when we may draw a mix of points and lines, like in param estimation (expected vs estimated)
			}
			for (int i=0;i<plotData.getSize();i++) {
				circle.setFrameFromCenter(nodes[index].getPoints()[i].getX(), nodes[index].getPoints()[i].getY(), nodes[index].getPoints()[i].getX() + diameter, nodes[index].getPoints()[i].getY() + diameter);
				if (circle.intersects(plotRectHolder)) {
					g.fill(circle);
				}
			}
		}
	}
}

//added March 30 to display histogram
private void drawHistogram(PlotData plotData, int index, Graphics2D g, int renderHints, int width) {
	if (plotData != null) {
		g.setStroke(lineBS_15);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if( plotRectHolder == null ||
			plotRectHolder.x != getLMargin() + getTick() ||
			plotRectHolder.y != getTMargin() + getTick() - (getBCompact()?2:0) ||
			plotRectHolder.width != getWidth() - 2 * getTick() - getLMargin() - getRMargin() ||
			plotRectHolder.height != getHeight() - 2 * getTick() - getTMargin() - getBMargin() + (getBCompact()?4:0)){
				
			plotRectHolder = new Rectangle(
				getLMargin() + getTick(),
				getTMargin() + getTick() - (getBCompact()?2:0),
				getWidth() - 2 * getTick() - getLMargin() - getRMargin(),
				getHeight() - 2 * getTick() - getTMargin() - getBMargin() + (getBCompact()?4:0));
		}
		g.setClip(plotRectHolder);
		
		
		if (plotData != null && plotData.getSize()>0) 
		{
			nodes[index].setPoints(mapPoints(plotData));
			Point2D[] points =nodes[index].getPoints();
			int w = width;
			if (width % 2 != 0) w=w+1;
			for(int k=0;k<points.length;k++)
			{
				double x=points[k].getX()-w/2;
				double y=points[k].getY();
				//to get transformed coordinate for y0=0, h=y0-y
				Point2D[] transH = new Point2D.Double[1];
				Point2D temp = new Point2D.Double(plotData.getPoints()[k].getX(),0);
				Point2D[] origH = new Point2D.Double[1];
				origH[0] = temp;
				getTransform().transform(origH, 0, transH, 0, 1);
				double h = transH[0].getY()-y;
				Rectangle2D reac=new Rectangle2D.Double(x,y,w,h);
				g.fill(reac);
			}
		}
		if (getShowNodes() && ((renderHints & Plot2D.RENDERHINT_DRAWPOINT) == Plot2D.RENDERHINT_DRAWPOINT)) {
//			nodes[index].setPoints(mapPoints(plotData));
			Ellipse2D.Double circle = new Ellipse2D.Double();
			int diameter = 2;
			if (renderHints == Plot2D.RENDERHINT_DRAWPOINT) { // only draw points, make them larger
				diameter = 4;
			}
			for (int i=0;i<plotData.getSize();i++) {
				circle.setFrameFromCenter(nodes[index].getPoints()[i].getX(), nodes[index].getPoints()[i].getY(), nodes[index].getPoints()[i].getX() + diameter, nodes[index].getPoints()[i].getY() + diameter);
				if (circle.intersects(plotRectHolder)) {
					g.setColor(Color.gray);
					g.fill(circle);
				}
			}
		}
		
	}
}
/**
 * Comment
 */
private void drawSelectionRectangle() {
	//int x = Math.min(getDragStartPoint().x, getDragEndPoint().x);
	//int y = Math.min(getDragStartPoint().y, getDragEndPoint().y);
	//int w = Math.abs(getDragStartPoint().x - getDragEndPoint().x);
	//int h = Math.abs(getDragStartPoint().y - getDragEndPoint().y);
	Graphics2D g = (Graphics2D)getGraphics();
	g.setColor(Color.white);
	g.setStroke(lineBS_20);
	//g.drawRect(x, y, w, h);
	g.drawRect(
		Math.min(getDragStartPoint().x, getDragEndPoint().x),
		Math.min(getDragStartPoint().y, getDragEndPoint().y),
		Math.abs(getDragStartPoint().x - getDragEndPoint().x),
		Math.abs(getDragStartPoint().y - getDragEndPoint().y));
}


/**
 * Comment
 */
private void drawTicks(Graphics2D g) {
	if(!getBCompact()){
		g.setColor(Color.black);
		g.setStroke(lineBS_10);
		Range xR = getXAuto() ? getXAutoRange() : getXManualRange();
		Range yR = getYAuto() ? getYAutoRange() : getYManualRange();
		xR = xR == null ? new Range(-1, 1) : xR;
		yR = yR == null ? new Range(-1, 1) : yR;
		double[] xM = getXMajorTicks();
		double[] xm = getXMinorTicks();
		double[] yM = getYMajorTicks();
		double[] ym = getYMinorTicks();
		int xLabelMaxHeight = 0;
		int yLabelMaxWidth = 0;
		for (int i = 0; i < yM.length; i++){
			String label = Double.toString(yM[i]);
			Rectangle2D labelBounds = g.getFontMetrics().getStringBounds(label,g);
			yLabelMaxWidth = Math.max(yLabelMaxWidth, (int)labelBounds.getWidth());
		}
		for (int i = 0; i < xM.length; i++){
			String label = Double.toString(xM[i]);
			Rectangle2D labelBounds = g.getFontMetrics().getStringBounds(label,g);
			xLabelMaxHeight = Math.max(xLabelMaxHeight, (int)labelBounds.getHeight());
		}
		setBMargin(xLabelMaxHeight * 4 / 3);
		setLMargin(yLabelMaxWidth + xLabelMaxHeight * 2 / 3);
		double ymin = yR.getMin();
		double xmin = xR.getMin();
		Point2D[] xP = new Point2D[xM.length];
		Point2D[] xp = new Point2D[xm.length];
		Point2D[] yP = new Point2D[yM.length];
		Point2D[] yp = new Point2D[ym.length];
		for (int i=0;i<xM.length;i++) {
			xP[i] = new Point2D.Double(xM[i], ymin);
		}
		for (int i=0;i<xm.length;i++) {
			xp[i] = new Point2D.Double(xm[i], ymin);
		}
		for (int i=0;i<yM.length;i++) {
			yP[i] = new Point2D.Double(xmin, yM[i]);
		}
		for (int i=0;i<ym.length;i++) {
			yp[i] = new Point2D.Double(xmin, ym[i]);
		}
		Point2D[] xP1 = new Point2D[xM.length];
		getTransform().transform(yP, 0, yP, 0, yP.length);
		getTransform().transform(yp, 0, yp, 0, yp.length);
		getTransform().transform(xP, 0, xP1, 0, xP1.length);
		getTransform().transform(xP, 0, xP, 0, xP.length);
		getTransform().transform(xp, 0, xp, 0, xp.length);
		for (int i=0;i<xP.length;i++) {
			g.drawLine((int)xP[i].getX(), (int)xP[i].getY(), (int)xP[i].getX(), (int)xP[i].getY() + getTick());
			String label = Double.toString(xM[i]);
			Rectangle2D labelBounds = g.getFontMetrics().getStringBounds(label,g);
			int labelX = (int)xP[i].getX() - (int)labelBounds.getWidth() / 2;
			int labelY = (int)xP[i].getY() + getTick() + xLabelMaxHeight;
			g.drawString(label,labelX,labelY);
		}
		for (int i=0;i<xp.length;i++) {
			g.drawLine((int)xp[i].getX(), (int)xp[i].getY(), (int)xp[i].getX(), (int)xp[i].getY() + getTick() / 2);
		}
		for (int i=0;i<yP.length;i++) {
			g.drawLine((int)yP[i].getX(), (int)yP[i].getY(), (int)yP[i].getX() - getTick(), (int)yP[i].getY());
			String label = Double.toString(yM[i]);
			Rectangle2D labelBounds = g.getFontMetrics().getStringBounds(label,g);
			int labelX = (int)yP[i].getX() - getTick() - (int)labelBounds.getWidth() - xLabelMaxHeight / 3;
			int labelY = (int)yP[i].getY() + (int)labelBounds.getHeight() / 3;
			g.drawString(label,labelX,labelY);
		}
		for (int i=0;i<yp.length;i++) {
			g.drawLine((int)yp[i].getX(), (int)yp[i].getY(), (int)yp[i].getX() - getTick() / 2, (int)yp[i].getY());
		}
	}else{
		int maxLeftMargin =
			Math.max(
				(int)(g.getFontMetrics().getStringBounds(yCompactMinS,g).getWidth()),
				(int)(g.getFontMetrics().getStringBounds(yCompactMaxS,g).getWidth())
			)+COMP_TICK_LEN;
		if(getLMargin() != maxLeftMargin){
			setLMargin(maxLeftMargin);
		}
	}
}

/**
 * Gets the autoColor property (boolean) value.
 * @return The autoColor property value.
 * @see #setAutoColor
 */
public boolean getAutoColor() {
	return fieldAutoColor;
}


/**
 * Gets the bCompact property (boolean) value.
 * @return The bCompact property value.
 * @see #setBCompact
 */
public boolean getBCompact() {
	return fieldBCompact;
}


/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 10:09:17 AM)
 * @return int
 */
private int getBMargin() {
	return bMargin;
}


/**
 * Gets the bStepMode property (boolean) value.
 * @return The bStepMode property value.
 * @see #setBStepMode
 */
public boolean getBStepMode() {
	return fieldBStepMode;
}


/**
 * Gets the currentPlotIndex property (int) value.
 * @return The currentPlotIndex property value.
 * @see #setCurrentPlotIndex
 */
public int getCurrentPlotIndex() {
	return fieldCurrentPlotIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 11:22:38 AM)
 * @return java.lang.String
 */
public String getCurrentPlotName() {
	if (getCurrentPlotIndex() == -1) {
		return null;
	} else {
		return getPlot2D().getPlotNames()[getCurrentPlotIndex()];
	}
}

/**
 * Insert the method's description here.
 * Creation date: (6/11/2002 11:42:37 AM)
 * @return java.awt.Point
 */
private java.awt.Point getDragEndPoint() {
	return dragEndPoint;
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2002 10:28:35 PM)
 * @return java.awt.Point
 */
private java.awt.Point getDragStartPoint() {
	return dragStartPoint;
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 2:18:22 AM)
 * @return java.awt.Point
 */
private java.awt.Point getLastPoint() {
	return lastPoint;
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 2:22:29 AM)
 * @param plotData cbit.plot.PlotData
 * @param index int
 * @return java.awt.geom.GeneralPath
 */
private GeneralPath getLinePlot(PlotData plotData, int index) {
	if (plotData != null) {
		nodes[index].setPoints(mapPoints(plotData));
		GeneralPath path = new GeneralPath();
		for (int i=0;i<plotData.getSize()-1;i++) {
			Line2D.Double segment = new Line2D.Double(nodes[index].getPoints()[i], nodes[index].getPoints()[i+1]);
			path.append(segment, true);
		}
		return path;
	} else {
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 2:22:29 AM)
 * @param plotData cbit.plot.PlotData
 * @param index int
 * @return java.awt.geom.Line2D[]
 */
private Line2D[] getLinePlotSegments(PlotData plotData, int index) { //index means which plot(for a spacific variable), The array 'nodes' stores all the plots. comment added 4th Oct, 2006 
	if (plotData != null && plotData.getSize()>0) {
		nodes[index].setPoints(mapPoints(plotData));
		if(!getBStepMode())
		{
			Line2D[] segments = new Line2D[plotData.getSize()-1];
			for (int i=0;i<plotData.getSize()-1;i++) {
				segments[i] = new Line2D.Double(nodes[index].getPoints()[i], nodes[index].getPoints()[i+1]);
			}
			return segments;
		}
		else
		{
			Line2D[] segments = new Line2D[(plotData.getSize()-1)*2];
			for (int i=0;i<plotData.getSize()-1;i++) {

				Point2D transientNode = new Point2D.Double(nodes[index].getPoints()[i+1].getX(),nodes[index].getPoints()[i].getY());
				segments[2*i] = new Line2D.Double(nodes[index].getPoints()[i], transientNode);
				segments[2*i+1] = new Line2D.Double(transientNode, nodes[index].getPoints()[i+1]);
			}
			return segments;
		}
		
	} else {
		return null;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 10:09:17 AM)
 * @return int
 */
private int getLMargin() {
	return lMargin;
}


/**
 * Gets the plot2D property (cbit.plot.Plot2D) value.
 * @return The plot2D property value.
 * @see #setPlot2D
 */
public Plot2D getPlot2D() {
	return fieldPlot2D;
}


/**
 * Return the plot2D1 property value.
 * @return cbit.plot.Plot2D
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Plot2D getplot2D1() {
	// user code begin {1}
	// user code end
	return ivjplot2D1;
}


/**
 * Return the plot2DSettings1 property value.
 * @return cbit.plot.Plot2DSettings
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Plot2DSettings getplot2DSettings1() {
	// user code begin {1}
	// user code end
	return ivjplot2DSettings1;
}


/**
 * Return the Plot2DSettingsPanel1 property value.
 * @return cbit.plot.Plot2DSettingsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Plot2DSettingsPanel getPlot2DSettingsPanel1() {
	if (ivjPlot2DSettingsPanel1 == null) {
		try {
			ivjPlot2DSettingsPanel1 = new cbit.plot.gui.Plot2DSettingsPanel();
			ivjPlot2DSettingsPanel1.setName("Plot2DSettingsPanel1");
			ivjPlot2DSettingsPanel1.setLocation(328, 460);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlot2DSettingsPanel1;
}


/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 11:05:16 AM)
 * @return java.awt.Paint
 * @param visiblePlotIndex int
 */
public Paint getVisiblePlotPaint(int visiblePlotIndex) {
//	if (getAutoColor()) {
//		return Color.getHSBColor((float) plotIndex / plotDatas.length, 1.0f, 1.0f);
//	} else {
//		return Color.black;
//	}

	if(userDefinedColors != null && userDefinedColors.length > visiblePlotIndex){
		return userDefinedColors[visiblePlotIndex];
	}
	if (getAutoColor()) {
		if(autoContrastColors == null || visiblePlotIndex >= autoContrastColors.length){
			autoContrastColors = ColorUtil.generateAutoColor(getPlot2D().getNumberOfVisiblePlots(),getBackground(),new Integer(0));
		}
		return autoContrastColors[visiblePlotIndex];
	} else {
		return Color.black;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 10:09:17 AM)
 * @return int
 */
private int getRMargin() {
	return rMargin;
}


public static Plot2D getSamplePlot2D() {
	PlotData plotData1 = getSamplePlotData();
	int size = 30;
	double offset = 20;
	double[] xArray = new double[size];
	double[] yArray = new double[size];
	double w = 4 * Math.PI / size;
	for (int i=0;i<size;i++){
		xArray[i] = w * i;
		yArray[i] = 5 * i - offset;
	}
	PlotData plotData2 = new PlotData(xArray,yArray);
	xArray = new double[size];
	yArray = new double[size];
	for (int i=0;i<size;i++){
		xArray[i] = w * i;
		yArray[i] = 300 - Math.pow(i - offset, 2);
	}
	PlotData plotData3 = new PlotData(xArray,yArray);
	return new Plot2D(null,null,new String[] {"plot one", "plot two", "plot three"}, new PlotData[] {plotData1, plotData2, plotData3}, new String[] {"title", "X Data", "Y Data"}, new boolean[] {true, false, true});
}


public static PlotData getSamplePlotData() {
	int size = 60;
	int scale = 100;
	double[] xArray = new double[size];
	double[] yArray = new double[size];
	double w = 4 * Math.PI / size;
	for (int i=0;i<size;i++){
		xArray[i] = w * i;
		yArray[i] = scale * Math.sin(w * i);
	}
	PlotData plotData = new PlotData(xArray,yArray);
	return plotData;
}


/**
 * Gets the showCrosshair property (boolean) value.
 * @return The showCrosshair property value.
 * @see #setShowCrosshair
 */
public boolean getShowCrosshair() {
	return fieldShowCrosshair;
}


/**
 * Gets the showNodes property (boolean) value.
 * @return The showNodes property value.
 * @see #setShowNodes
 */
public boolean getShowNodes() {
	return fieldShowNodes;
}


/**
 * Gets the snapToNodes property (boolean) value.
 * @return The snapToNodes property value.
 * @see #setSnapToNodes
 */
public boolean getSnapToNodes() {
	return fieldSnapToNodes;
}


/**
 * Gets the statusLabel property (javax.swing.JLabel) value.
 * @return The statusLabel property value.
 * @see #setStatusLabel
 */
public javax.swing.JLabel getStatusLabel() {
	return fieldStatusLabel;
}


/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 10:09:17 AM)
 * @return int
 */
private int getTick() {
	return tick;
}


/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 10:09:17 AM)
 * @return int
 */
private int getTMargin() {
	return tMargin;
}


/**
 * Returns an AffineTrasform that maps a cartesian rectangular area bounded by
 * the stored X axis and Y axis values to the current plotting area of this panel.
 * Creation date: (2/8/2001 11:02:15 PM)
 * @return java.awt.geom.AffineTransform
 */
private AffineTransform getTransform() {
	Range xR = getXAuto() ? getXAutoRange() : getXManualRange();
	Range yR = getYAuto() ? getYAutoRange() : getYManualRange();
	xR = xR == null ? new Range(-1, 1) : xR;
	yR = yR == null ? new Range(-1, 1) : yR;
	// these go in reverse order !
	AffineTransform transform = AffineTransform.getTranslateInstance(getTick() + getLMargin(), getTick() + getTMargin());
	transform.concatenate(AffineTransform.getScaleInstance((getWidth() - 2 * getTick() - getLMargin() - getRMargin()) / (xR.getMax() - xR.getMin()),  (getHeight() - 2 * getTick() - getTMargin() - getBMargin()) / (yR.getMin() - yR.getMax())));
	transform.concatenate(AffineTransform.getTranslateInstance(-xR.getMin(), -yR.getMax()));
	return transform;
}


/**
 * Gets the xAuto property (boolean) value.
 * @return The xAuto property value.
 * @see #setXAuto
 */
public boolean getXAuto() {
	return fieldXAuto;
}


/**
 * Gets the xAutoRange property (cbit.image.Range) value.
 * @return The xAutoRange property value.
 * @see #setXAutoRange
 */
public Range getXAutoRange() {
	return fieldXAutoRange;
}

/**
 * Gets the xMajorTicks property (double[]) value.
 * @return The xMajorTicks property value.
 * @see #setXMajorTicks
 */
public double[] getXMajorTicks() {
	return fieldXMajorTicks;
}


/**
 * Gets the xManualRange property (cbit.image.Range) value.
 * @return The xManualRange property value.
 * @see #setXManualRange
 */
public Range getXManualRange() {
	return fieldXManualRange;
}


/**
 * Gets the xMinorTicks property (double[]) value.
 * @return The xMinorTicks property value.
 * @see #setXMinorTicks
 */
public double[] getXMinorTicks() {
	return fieldXMinorTicks;
}


/**
 * Gets the xStretch property (boolean) value.
 * @return The xStretch property value.
 * @see #setXStretch
 */
public boolean getXStretch() {
	return fieldXStretch;
}


/**
 * Gets the yAuto property (boolean) value.
 * @return The yAuto property value.
 * @see #setYAuto
 */
public boolean getYAuto() {
	return fieldYAuto;
}


/**
 * Gets the yAutoRange property (cbit.image.Range) value.
 * @return The yAutoRange property value.
 * @see #setYAutoRange
 */
public Range getYAutoRange() {
	return fieldYAutoRange;
}

/**
 * Gets the yMajorTicks property (double[]) value.
 * @return The yMajorTicks property value.
 * @see #setYMajorTicks
 */
public double[] getYMajorTicks() {
	return fieldYMajorTicks;
}


/**
 * Gets the yManualRange property (cbit.image.Range) value.
 * @return The yManualRange property value.
 * @see #setYManualRange
 */
public Range getYManualRange() {
	return fieldYManualRange;
}


/**
 * Gets the yMinorTicks property (double[]) value.
 * @return The yMinorTicks property value.
 * @see #setYMinorTicks
 */
public double[] getYMinorTicks() {
	return fieldYMinorTicks;
}


/**
 * Gets the yStretch property (boolean) value.
 * @return The yStretch property value.
 * @see #setYStretch
 */
public boolean getYStretch() {
	return fieldYStretch;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addMouseMotionListener(ivjEventHandler);
	this.addMouseListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getPlot2DSettingsPanel1().addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP7SetTarget();
	connPtoP10SetTarget();
	connPtoP6SetTarget();
	connPtoP9SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
	connPtoP8SetTarget();
	connPtoP11SetTarget();
	connPtoP12SetTarget();
	connPtoP13SetTarget();
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("Plot2DPanel");
		setLayout(null);
		setSize(300, 200);
		initConnections();
		connEtoC5();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		Plot2DPanel aPlot2DPanel;
		aPlot2DPanel = new Plot2DPanel();
		frame.setContentPane(aPlot2DPanel);
		frame.setSize(aPlot2DPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		aPlot2DPanel.setPlot2D(getSamplePlot2D());
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 10:13:28 AM)
 * @return java.awt.geom.Point2D.Double[]
 * @param plotData cbit.plot.PlotData
 */
private Point2D.Double[] mapPoints(PlotData plotData) {
	Point2D.Double[] mappedPoints = new Point2D.Double[plotData.getSize()];
	getTransform().transform(plotData.getPoints(), 0, mappedPoints, 0, plotData.getSize());
	return mappedPoints;
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 2:12:00 AM)
 * @param g java.awt.Graphics
 */
public void paintComponent(Graphics g) {
	super.paintComponent(g);
	// IIM: paint should never throw, but fail gracefully so parents do show up even if a child is not rendered properly...
	// reason why we must catch here is g2D implementation on Windows has some bugs that occasionally throw stuff
	try {
		if (getPlot2D() == null) {
			g.drawString("NO DATA",5,this.getSize().height/2);
			return;
		}
		boolean bVisiblePlotHasInvalidData = getPlot2D().visiblePlotsInvalid();
		if(bVisiblePlotHasInvalidData || bPlot2DHasInvalidRange){
			g.drawString("PLOT DISABLED",5,this.getSize().height/2);
			g.drawString("Invalid Numeric "+
				(bVisiblePlotHasInvalidData?"Data":"")+(bPlot2DHasInvalidRange && !bVisiblePlotHasInvalidData?"Range":"")+
				" (NaN or Infinity)",5,g.getFontMetrics().getHeight() + this.getSize().height/2);
			return;
		}
		
		Graphics2D g2D = (Graphics2D)g;
		drawTicks(g2D);
		drawAxes(g2D);
		
		int visiblePlotIndex = 0;
		for (int i=0;i<plotDatas.length;i++) {
			if (getPlot2D().isVisiblePlot(i)) {
				g2D.setPaint(getVisiblePlotPaint(visiblePlotIndex ++));
				//amended March 30,2007. draw trajectory or histogram
				if(getIsHistogram())
					drawHistogram(plotDatas[i], i, g2D,getPlot2D().getRenderHints()[i],10);
				else 
					drawLinePlot(plotDatas[i], i, g2D,getPlot2D().getRenderHints()[i]);
			}
		}
	} catch (Throwable exc) {
		// normally, this should never happen (see above)
		// if it does, we can have burst of paint() calls that do, so don't cripple by asking for stack trace...
		System.out.println("*** PAINT failed *** " + exc.getMessage());
	}
}


/**
 * Comment
 */
private void pointerMoved(java.awt.event.MouseEvent mouseEvent) {
	if (! getShowCrosshair() || plotDatas==null || plotDatas.length==0) {
		return;
	}
	Point point = mouseEvent.getPoint();
	Point nodePoint = null;
	Graphics2D g = (Graphics2D)getGraphics();
	g.setColor(Color.white);
	g.setXORMode(getBackground());
	g.setStroke(lineBS_20);
	int index = getCurrentPlotIndex();
	PlotData plotData = null;
	try {
		if (index>=0){
			plotData = plotDatas[index];
		}
	} catch (IndexOutOfBoundsException exc) {
		// ignore - we probably don't have any visible plot, so plotData should stay null;
	}
	if (plotData != null) {
		int i = 0;
		while (i < plotData.getSize() && nodes[index].getPoints()[i].getX() < point.getX()) i++;
		if (i == plotData.getSize()) {
			i--;
		} else {
			if (i > 0) {
				if (nodes[index].getPoints()[i].getX() - point.getX() > point.getX() - nodes[index].getPoints()[i-1].getX()) {
					i--;
				}
			}
		}
		nodePoint = new Point((int)nodes[index].getPoints()[i].getX(), (int)nodes[index].getPoints()[i].getY());
		getStatusLabel().setText(snf.format(plotData.getIndependent()[i]) + ", " + snf.format(plotData.getDependent()[i]));
		if (getSnapToNodes()) {
			point = nodePoint;
		}
	} else {
		getStatusLabel().setText(" ");
	}
	if (! point.equals(getLastPoint())) {
		if (drawn) {
			drawCrossHair(g, getLastPoint());
		}
		drawCrossHair(g, point);
		setLastPoint(point);
		drawn = true;
	}
}


/**
 * Comment
 */
private void processDrag0(java.awt.event.MouseEvent mouseEvent) {
	try{
		if (mouseEvent.isShiftDown()) {
			Point point = mouseEvent.getPoint();
			switch (mouseEvent.getID()) {
				case MouseEvent.MOUSE_PRESSED: {
					setDragStartPoint(point);
					break;
				}
				case MouseEvent.MOUSE_DRAGGED: {
					resetCrossHair();
					setDragEndPoint(point);
					drawSelectionRectangle();
					break;
				}
				case MouseEvent.MOUSE_RELEASED: {
					setDragEndPoint(point);
					drawSelectionRectangle();
					Point2D.Double p1 = new Point2D.Double();
					Point2D.Double p2 = new Point2D.Double();
					try {
						getTransform().inverseTransform(getDragStartPoint(), p1);
						getTransform().inverseTransform(getDragEndPoint(), p2);
					} catch (NoninvertibleTransformException exc) {
						// no worry, this is always an invertible transform...
					}
					setXManualRange(new Range(p1.x, p2.x));
					setXAuto(false);
					setYManualRange(new Range(p1.y, p2.y));
					setYAuto(false);
					break;
				}
			}
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private void processMouseButtonEvent(java.awt.event.MouseEvent mouseEvent) {
	try{
		if (getPlot2D() == null){
			return;
		} else if (mouseEvent.isPopupTrigger()) {
			// save current settings before showing panel; plot will update live
			getplot2DSettings1().saveSettings();
			int newSettings = org.vcell.util.gui.DialogUtils.showComponentOKCancelDialog(this, getPlot2DSettingsPanel1(), "Plot Settings");
			if (newSettings != JOptionPane.OK_OPTION) {
				// user didn't ok, put back old settings
				getplot2DSettings1().restoreSavedSettings();
			}
//			paintComponent(getGraphics());//this shouldn't be needed
		} else if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED && mouseEvent.getClickCount() == 1) {
			if(mouseEvent.isAltDown()){
				// zoom out x2 on manual scale(s)
				if (! getXAuto()) {
					setXManualRange(Range.multiplyRange(getXManualRange(), 2));
				}
				if (! getYAuto()) {
					setYManualRange(Range.multiplyRange(getYManualRange(), 2));
				}
			}else{//try to select plot curve
				GeneralPath path = null;
				for (int i=0;i<plotDatas.length;i++) {
					if (getPlot2D().isVisiblePlot(i)) {
						path = getLinePlot(plotDatas[i], i);
						if (path.intersects(new Rectangle(mouseEvent.getPoint().x - 1, mouseEvent.getPoint().y - 1, 3, 3))) {
							setCurrentPlotIndex(i);
						}
					}
				}

			}
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}

}


/**
 * Comment
 */
public void resetCrossHair() {
	setLastPoint(null);
	drawn = false;
	getStatusLabel().setText(" ");
	repaint();
}


/**
 * Sets the autoColor property (boolean) value.
 * @param autoColor The new value for the property.
 * @see #getAutoColor
 */
public void setAutoColor(boolean autoColor) {
	boolean oldValue = fieldAutoColor;
	fieldAutoColor = autoColor;
	firePropertyChange("autoColor", new Boolean(oldValue), new Boolean(autoColor));
}


/**
 * Sets the bCompact property (boolean) value.
 * @param bCompact The new value for the property.
 * @see #getBCompact
 */
public void setBCompact(boolean bCompact) {
	setRMargin((bCompact?0:10));
	setTMargin((bCompact?0:10));
	boolean oldValue = fieldBCompact;
	fieldBCompact = bCompact;
	firePropertyChange("bCompact", new Boolean(oldValue), new Boolean(bCompact));
}


/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 10:09:17 AM)
 * @param newBMargin int
 */
private void setBMargin(int newBMargin) {
	bMargin = newBMargin;
}


/**
 * Sets the bStepMode property (boolean) value.
 * @param bStepMode The new value for the property.
 * @see #getBStepMode
 */
public void setBStepMode(boolean bStepMode) {
	boolean oldValue = fieldBStepMode;
	fieldBStepMode = bStepMode;
	firePropertyChange("bStepMode", new Boolean(oldValue), new Boolean(bStepMode));
}


/**
 * Insert the method's description here.
 * Creation date: (10/27/2005 10:37:21 AM)
 * @param plotName java.lang.String
 */
public void setCurrentPlot(String plotName) {
	String[] plotNames = getPlot2D().getPlotNames();
	if (plotName == null || plotNames == null) {
		return;
	}
	int index = -1;
	for (int i = 0; i < plotNames.length; i++){
		if (plotName.equals(plotNames[i])) {
			index = i;
			break;
		}
	}
	if (index > -1) {
		if (getPlot2D().isVisiblePlot(index)) {
			setCurrentPlotIndex(index);
		}
	}
}
		
/**
 * Sets the currentPlotIndex property (int) value.
 * @param currentPlotIndex The new value for the property.
 * @see #getCurrentPlotIndex
 */
private void setCurrentPlotIndex(int currentPlotIndex) {
	int oldValue = fieldCurrentPlotIndex;
	fieldCurrentPlotIndex = currentPlotIndex;
	firePropertyChange("currentPlotIndex", new Integer(oldValue), new Integer(currentPlotIndex));
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2002 11:42:37 AM)
 * @param newDragEndPoint java.awt.Point
 */
private void setDragEndPoint(java.awt.Point newDragEndPoint) {
	dragEndPoint = newDragEndPoint;
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2002 10:28:35 PM)
 * @param newDragStartPoint java.awt.Point
 */
private void setDragStartPoint(java.awt.Point newDragStartPoint) {
	dragStartPoint = newDragStartPoint;
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 2:18:22 AM)
 * @param newLastPoint java.awt.Point
 */
private void setLastPoint(java.awt.Point newLastPoint) {
	lastPoint = newLastPoint;
}


/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 10:09:17 AM)
 * @param newLMargin int
 */
private void setLMargin(int newLMargin) {
	lMargin = newLMargin;
}


/**
 * Sets the plot2D property (cbit.plot.Plot2D) value.
 * @param plot2D The new value for the property.
 * @see #getPlot2D
 */
public void setPlot2D(Plot2D plot2D) {
	Plot2D oldValue = fieldPlot2D;
	fieldPlot2D = plot2D;

	firePropertyChange("plot2D", oldValue, plot2D);
}


/**
 * Set the plot2D1 to a new value.
 * @param newValue cbit.plot.Plot2D
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setplot2D1(Plot2D newValue) {
	if (ivjplot2D1 != newValue) {
		try {
			cbit.plot.Plot2D oldValue = getplot2D1();
			/* Stop listening for events from the current object */
			if (ivjplot2D1 != null) {
				ivjplot2D1.removeChangeListener(ivjEventHandler);
			}
			ivjplot2D1 = newValue;

			/* Listen for events from the new object */
			if (ivjplot2D1 != null) {
				ivjplot2D1.addChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connEtoC16(ivjplot2D1);
			connEtoC3(ivjplot2D1);
			firePropertyChange("plot2D", oldValue, newValue);
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
 * Set the plot2DSettings1 to a new value.
 * @param newValue cbit.plot.Plot2DSettings
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setplot2DSettings1(Plot2DSettings newValue) {
	if (ivjplot2DSettings1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjplot2DSettings1 != null) {
				ivjplot2DSettings1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjplot2DSettings1 = newValue;

			/* Listen for events from the new object */
			if (ivjplot2DSettings1 != null) {
				ivjplot2DSettings1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP7SetTarget();
			connPtoP10SetTarget();
			connPtoP6SetTarget();
			connPtoP9SetTarget();
			connPtoP2SetSource();
			connPtoP3SetTarget();
			connPtoP4SetTarget();
			connPtoP5SetTarget();
			connPtoP8SetTarget();
			connPtoP11SetTarget();
			connPtoP12SetTarget();
			connPtoP13SetTarget();
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
 * Insert the method's description here.
 * Creation date: (2/15/2001 10:09:17 AM)
 * @param newRMargin int
 */
private void setRMargin(int newRMargin) {
	rMargin = newRMargin;
}


/**
 * Sets the showCrosshair property (boolean) value.
 * @param showCrosshair The new value for the property.
 * @see #getShowCrosshair
 */
public void setShowCrosshair(boolean showCrosshair) {
	boolean oldValue = fieldShowCrosshair;
	fieldShowCrosshair = showCrosshair;
	firePropertyChange("showCrosshair", new Boolean(oldValue), new Boolean(showCrosshair));
}


/**
 * Sets the showNodes property (boolean) value.
 * @param showNodes The new value for the property.
 * @see #getShowNodes
 */
public void setShowNodes(boolean showNodes) {
	boolean oldValue = fieldShowNodes;
	fieldShowNodes = showNodes;
	firePropertyChange("showNodes", new Boolean(oldValue), new Boolean(showNodes));
}


/**
 * Sets the snapToNodes property (boolean) value.
 * @param snapToNodes The new value for the property.
 * @see #getSnapToNodes
 */
public void setSnapToNodes(boolean snapToNodes) {
	boolean oldValue = fieldSnapToNodes;
	fieldSnapToNodes = snapToNodes;
	firePropertyChange("snapToNodes", new Boolean(oldValue), new Boolean(snapToNodes));
}


/**
 * Sets the statusLabel property (javax.swing.JLabel) value.
 * @param statusLabel The new value for the property.
 * @see #getStatusLabel
 */
public void setStatusLabel(javax.swing.JLabel statusLabel) {
	JLabel oldValue = fieldStatusLabel;
	fieldStatusLabel = statusLabel;
	firePropertyChange("statusLabel", oldValue, statusLabel);
}

/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 10:09:17 AM)
 * @param newTMargin int
 */
private void setTMargin(int newTMargin) {
	tMargin = newTMargin;
}


/**
 * Sets the xAuto property (boolean) value.
 * @param xAuto The new value for the property.
 * @see #getXAuto
 */
public void setXAuto(boolean xAuto) {
	boolean oldValue = fieldXAuto;
	fieldXAuto = xAuto;
	firePropertyChange("xAuto", new Boolean(oldValue), new Boolean(xAuto));
}


/**
 * Sets the xAutoRange property (cbit.image.Range) value.
 * @param xAutoRange The new value for the property.
 * @see #getXAutoRange
 */
public void setXAutoRange(Range xAutoRange) {
	Range oldValue = fieldXAutoRange;
	fieldXAutoRange = xAutoRange;
	firePropertyChange("xAutoRange", oldValue, xAutoRange);
}


/**
 * Sets the xMajorTicks property (double[]) value.
 * @param xMajorTicks The new value for the property.
 * @see #getXMajorTicks
 */
private void setXMajorTicks(double[] xMajorTicks) {
	fieldXMajorTicks = xMajorTicks;
}


/**
 * Sets the xManualRange property (cbit.image.Range) value.
 * @param xManualRange The new value for the property.
 * @see #getXManualRange
 */
public void setXManualRange(Range xManualRange) {
	
	if(xManualRange != null){
		if(xManualRange.getMin() == xManualRange.getMax()){
			xManualRange = new Range(xManualRange.getMin()-1,xManualRange.getMin()+1);
		}
	}
	if(getBCompact()){
		if(xManualRange != null){
			final int COMP_XVAL_MAX_LEN = 9;
			xCompactMinS = xManualRange.getMin()+"";
			if(xCompactMinS.length() > COMP_XVAL_MAX_LEN){
				DecimalFormat df = new DecimalFormat();
				df.applyPattern("#.###E0");
				xCompactMinS = df.format(xManualRange.getMin());
			}
			xCompactMaxS = xManualRange.getMax()+"";
			if(xCompactMaxS.length() > COMP_XVAL_MAX_LEN){
				DecimalFormat df = new DecimalFormat();
				df.applyPattern("#.###E0");
				xCompactMaxS = df.format(xManualRange.getMax());
			}
		}else{
			xCompactMaxS = "?";
			xCompactMinS = "?";
		}
	}
		
	Range oldValue = fieldXManualRange;
	fieldXManualRange = xManualRange;
	firePropertyChange("xManualRange", oldValue, xManualRange);
}


/**
 * Sets the xMinorTicks property (double[]) value.
 * @param xMinorTicks The new value for the property.
 * @see #getXMinorTicks
 */
private void setXMinorTicks(double[] xMinorTicks) {
	fieldXMinorTicks = xMinorTicks;
}


/**
 * Sets the xStretch property (boolean) value.
 * @param xStretch The new value for the property.
 * @see #getXStretch
 */
public void setXStretch(boolean xStretch) {
	boolean oldValue = fieldXStretch;
	fieldXStretch = xStretch;
	firePropertyChange("xStretch", new Boolean(oldValue), new Boolean(xStretch));
}


/**
 * Sets the yAuto property (boolean) value.
 * @param yAuto The new value for the property.
 * @see #getYAuto
 */
public void setYAuto(boolean yAuto) {
	boolean oldValue = fieldYAuto;
	fieldYAuto = yAuto;
	firePropertyChange("yAuto", new Boolean(oldValue), new Boolean(yAuto));
}


/**
 * Sets the yAutoRange property (cbit.image.Range) value.
 * @param yAutoRange The new value for the property.
 * @see #getYAutoRange
 */
public void setYAutoRange(Range yAutoRange) {
	Range oldValue = fieldYAutoRange;
	fieldYAutoRange = yAutoRange;
	firePropertyChange("yAutoRange", oldValue, yAutoRange);
}


/**
 * Sets the yMajorTicks property (double[]) value.
 * @param yMajorTicks The new value for the property.
 * @see #getYMajorTicks
 */
private void setYMajorTicks(double[] yMajorTicks) {
	fieldYMajorTicks = yMajorTicks;
}


/**
 * Sets the yManualRange property (cbit.image.Range) value.
 * @param yManualRange The new value for the property.
 * @see #getYManualRange
 */
public void setYManualRange(Range yManualRange) {
	
	if(yManualRange != null && (yManualRange.getMin() == yManualRange.getMax())){
		yManualRange = new Range(yManualRange.getMin()-1,yManualRange.getMin()+1);
	}
	
	if(getBCompact()){
		setLMargin(20);
		if(yManualRange != null){
			final int COMP_YVAL_MAX_LEN = 5;
			yCompactMinS = yManualRange.getMin()+"";
			if(yCompactMinS.length() > COMP_YVAL_MAX_LEN){
				DecimalFormat df = new DecimalFormat();
				df.applyPattern("#.##E0");
				yCompactMinS = df.format(yManualRange.getMin());
			}
			yCompactMaxS = yManualRange.getMax()+"";
			if(yCompactMaxS.length() > COMP_YVAL_MAX_LEN){
				DecimalFormat df = new DecimalFormat();
				df.applyPattern("#.##E0");
				yCompactMaxS = df.format(yManualRange.getMax());
			}
		}else{
			yCompactMaxS = "?";
			yCompactMinS = "?";
		}
	}

	Range oldValue = fieldYManualRange;
	fieldYManualRange = yManualRange;
	firePropertyChange("yManualRange", oldValue, yManualRange);
}


/**
 * Sets the yMinorTicks property (double[]) value.
 * @param yMinorTicks The new value for the property.
 * @see #getYMinorTicks
 */
private void setYMinorTicks(double[] yMinorTicks) {
	fieldYMinorTicks = yMinorTicks;
}


/**
 * Sets the yStretch property (boolean) value.
 * @param yStretch The new value for the property.
 * @see #getYStretch
 */
public void setYStretch(boolean yStretch) {
	boolean oldValue = fieldYStretch;
	fieldYStretch = yStretch;
	firePropertyChange("yStretch", new Boolean(oldValue), new Boolean(yStretch));
}


protected void updateAutoRanges() {
	if (getPlot2D() == null) return;
	if(getPlot2D().visiblePlotsInvalid()){
		setXAutoRange(null);
		setYAutoRange(null);
		return;
	}
	
	if (getXStretch()) {
		setXAutoRange(getPlot2D().getXDataRange());
	} else {
		setXAutoRange(NumberUtils.getDecimalRange(getPlot2D().getXDataRange(), false, false));
	}
	if (getYStretch()) {
		if(getIsHistogram())
		{
			Range temp = getPlot2D().getYDataRange();
			Range yRange = new Range(temp.getMin()*2, temp.getMax()*2);
			setYAutoRange(yRange);
		}
		else setYAutoRange(getPlot2D().getYDataRange());
	} else {
		if(getIsHistogram())
		{
			Range temp = getPlot2D().getYDataRange();
			Range yRange = new Range(temp.getMin()*2, temp.getMax()*2);
			setYAutoRange(NumberUtils.getDecimalRange(yRange, false, false));
		}
		else setYAutoRange(NumberUtils.getDecimalRange(getPlot2D().getYDataRange(), false, false));
	}
}


/**
 * Comment
 */
private void updateAxes() {
	
	Range xR = getXAuto() ? getXAutoRange() : getXManualRange();
	Range yR = getYAuto() ? getYAutoRange() : getYManualRange();
	bPlot2DHasInvalidRange = false;
	if (xR != null){
		bPlot2DHasInvalidRange = bPlot2DHasInvalidRange || !xR.isValid();
	}
	if (yR != null){
		bPlot2DHasInvalidRange = bPlot2DHasInvalidRange || !yR.isValid();
	}
	
	if(getPlot2D() == null || getPlot2D().visiblePlotsInvalid() || bPlot2DHasInvalidRange){
		setXMajorTicks(null);
		setYMajorTicks(null);
		setXMinorTicks(null);
		setYMinorTicks(null);
		repaint();
		return;
	}
	
	xR = xR == null ? new Range(-1, 1) : xR;
	yR = yR == null ? new Range(-1, 1) : yR;
	double[] xM = NumberUtils.getMajorDecimalTicks(xR);
	double[] yM = NumberUtils.getMajorDecimalTicks(yR);
	int xl = xM.length;
	int xs = 0;
	if (xM[0] < xR.getMin()) {
		xl--;
		xs++;
	}
	if (xM[xM.length - 1] > xR.getMax()) {
		xl--;
	}
	double[] xMajorTicks = new double[xl];
	System.arraycopy(xM, xs, xMajorTicks, 0, xl);
	int yl = yM.length;
	int ys = 0;
	if (yM[0] < yR.getMin()) {
		yl--;
		ys++;
	}
	if (yM[yM.length - 1] > yR.getMax()) {
		yl--;
	}
	double[] yMajorTicks = new double[yl];
	System.arraycopy(yM, ys, yMajorTicks, 0, yl);
	setXMajorTicks(xMajorTicks);
	setYMajorTicks(yMajorTicks);
	setXMinorTicks(NumberUtils.getMinorDecimalTicks(xR));
	setYMinorTicks(NumberUtils.getMinorDecimalTicks(yR));
	repaint();
}


/**
 * Comment
 */
private void updatePlot(cbit.plot.Plot2D plot2D) {
	if (plot2D == null) {
		plotDatas = null;
		repaint();
		return;
	}
	plotDatas = plot2D.getPlotDatas();	
	nodes = new Point2DSet[plot2D.getNumberOfPlots()];
	for (int i=0;i<plot2D.getNumberOfPlots();i++) {
		nodes[i] = new Point2DSet(null);
	}
	updateVisiblePlots(plot2D);
	updateAxes();
}


/**
 * Comment
 */
private void updateVisiblePlots(Plot2D plot2D) {
	int index = -1;
	if (plot2D.getNumberOfVisiblePlots() > 0) {
		for (int i = 0; i < plot2D.getNumberOfPlots(); i++){
			if (plot2D.isVisiblePlot(i)) {
				index = i;
				break;
			}
		}
	}
	setCurrentPlotIndex(index);
	repaint();
}

public boolean getIsHistogram() {
	return fieldIsHistogram;
}
public void setIsHistogram(boolean fieldIsHistogram) {
	this.fieldIsHistogram = fieldIsHistogram;
}

public void setUserDefinedColors(Color[] userDefinedColors) {
	this.userDefinedColors = userDefinedColors;
}

}
