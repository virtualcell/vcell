package cbit.plot;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import cbit.util.*;
import cbit.util.Range;
/**
 * Insert the type's description here.
 * Creation date: (2/7/2001 12:38:12 AM)
 * @author: Ion Moraru
 */
public class Plot2DPanel extends JPanel {
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
	private boolean bPlot2DHasInvalidData = false;
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

class IvjEventHandler implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == Plot2DPanel.this) 
				connEtoC6(e);
		};
		public void mouseDragged(java.awt.event.MouseEvent e) {
			if (e.getSource() == Plot2DPanel.this) 
				connEtoC17(e);
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
			if (e.getSource() == Plot2DPanel.this) 
				connEtoC18(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == Plot2DPanel.this) 
				connEtoC19(e);
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

public Plot2DPanel() {
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
 * connEtoC17:  (Plot2DPanel.mouseMotion.mouseDragged(java.awt.event.MouseEvent) --> Plot2DPanel.processMouseDragged(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.processDrag(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC18:  (Plot2DPanel.mouse.mousePressed(java.awt.event.MouseEvent) --> Plot2DPanel.processDrag(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.processDrag(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC19:  (Plot2DPanel.mouse.mouseReleased(java.awt.event.MouseEvent) --> Plot2DPanel.processDrag(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.processDrag(arg1);
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
 * connEtoC6:  (Plot2DPanel.mouse.mouseClicked(java.awt.event.MouseEvent) --> Plot2DPanel.showSettings(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.processMouseClick(arg1);
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
				diameter = 4;
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
 * Creation date: (3/2/2001 11:12:03 AM)
 * @return java.awt.Paint
 */
public Paint getCurrentPlotPaint() {
	if (getCurrentPlotIndex() == -1) {
		return null;
	} else {
		return getPlotPaint(getCurrentPlotIndex());
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
			ivjPlot2DSettingsPanel1 = new cbit.plot.Plot2DSettingsPanel();
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
 * @param plotIndex int
 */
public Paint getPlotPaint(int plotIndex) {
	if (getAutoColor()) {
		return Color.getHSBColor((float) plotIndex / plotDatas.length, 1.0f, 1.0f);
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
	return new Plot2D(null,new String[] {"plot one", "plot two", "plot three"}, new PlotData[] {plotData1, plotData2, plotData3}, new String[] {"title", "X Data", "Y Data"}, new boolean[] {true, false, true});
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


private Range getXAutoRange0() {
	if (getXStretch()) {
		return getPlot2D().getXDataRange();
	} else {
		return NumberUtils.getDecimalRange(getPlot2D().getXDataRange(), false, false);
	}
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


private Range getYAutoRange0() {
	if (getYStretch()) {
		return getPlot2D().getYDataRange();
	} else {
		return NumberUtils.getDecimalRange(getPlot2D().getYDataRange(), false, false);
	}
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
		frame.show();
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
		if(bPlot2DHasInvalidData ||bPlot2DHasInvalidRange){
			g.drawString("PLOT DISABLED",5,this.getSize().height/2);
			g.drawString("Invalid Numeric "+
				(bPlot2DHasInvalidData?"Data":"")+(bPlot2DHasInvalidRange && !bPlot2DHasInvalidData?"Range":"")+
				" (NaN or Infinity)",5,g.getFontMetrics().getHeight() + this.getSize().height/2);
			return;
		}
		
		Graphics2D g2D = (Graphics2D)g;
		drawTicks(g2D);
		drawAxes(g2D);
		for (int i=0;i<plotDatas.length;i++) {
			if (getPlot2D().isVisiblePlot(i)) {
				g2D.setPaint(getPlotPaint(i));
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
private void processDrag(java.awt.event.MouseEvent mouseEvent) {
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
}


/**
 * Comment
 */
private void processMouseClick(java.awt.event.MouseEvent mouseEvent) {
	if (getPlot2D() == null) return;
	if (mouseEvent.isAltDown()) {
		// zoom out x2 on manual scale(s)
		if (! getXAuto()) {
			setXManualRange(Range.multiplyRange(getXManualRange(), 2));
		}
		if (! getYAuto()) {
			setYManualRange(Range.multiplyRange(getYManualRange(), 2));
		}
		return;
	}
	if (SwingUtilities.isRightMouseButton(mouseEvent)) { // right-click shows Settings panel
		// save current settings before showing panel; plot will update live
		getplot2DSettings1().saveSettings();
//		int newSettings = JOptionPane.showOptionDialog(this, getPlot2DSettingsPanel1(), "Plot Settings", 0, JOptionPane.PLAIN_MESSAGE, null, new String[] {"OK", "Cancel"}, null);
		int newSettings = cbit.gui.DialogUtils.showComponentOKCancelDialog(this, getPlot2DSettingsPanel1(), "Plot Settings");
		if (newSettings != JOptionPane.OK_OPTION) {
			// user didn't ok, put back old settings
			getplot2DSettings1().restoreSavedSettings();
		}
		paintComponent(getGraphics());
	} else { // regular click tries to select an active plot	
		GeneralPath path = null;
		for (int i=0;i<plotDatas.length;i++) {
			if (getPlot2D().isVisiblePlot(i)) {
				path = getLinePlot(plotDatas[i], i);
				if (path.intersects(new Rectangle(mouseEvent.getPoint().x - 1, mouseEvent.getPoint().y - 1, 3, 3))) {
					setCurrentPlotIndex(i);
					return;
				}
			}
		}
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

	if (plot2D != null) {
		bPlot2DHasInvalidData = false;
		for(int i=0;i<plot2D.getPlotDatas().length;i+= 1){
			if(plot2D.getPlotDatas()[i].hasInvalidNumericValues()){
				bPlot2DHasInvalidData = true;
				break;
			}
		}
	}
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
 * @param newTick int
 */
private void setTick(int newTick) {
	tick = newTick;
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


private void updateAutoRanges() {
	if (getPlot2D() == null) return;
	if(bPlot2DHasInvalidData){
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
		setYAutoRange(getPlot2D().getYDataRange());
	} else {
		setYAutoRange(NumberUtils.getDecimalRange(getPlot2D().getYDataRange(), false, false));
	}
}


/**
 * Comment
 */
private void updateAxes() {
	
	Range xR = getXAuto() ? getXAutoRange() : getXManualRange();
	Range yR = getYAuto() ? getYAutoRange() : getYManualRange();

	if(xR != null &&
			(
			Double.isNaN(xR.getMin()) ||
			Double.isInfinite(xR.getMin()) ||
			Double.isNaN(xR.getMax()) ||
			Double.isInfinite(xR.getMax())
			)
		){
		bPlot2DHasInvalidRange = true;
	}else{
		bPlot2DHasInvalidRange = false;
	}

	if(!bPlot2DHasInvalidRange){
		if(yR != null &&
				(
				Double.isNaN(yR.getMin()) ||
				Double.isInfinite(yR.getMin()) ||
				Double.isNaN(yR.getMax()) ||
				Double.isInfinite(yR.getMax())
				)
			){
			bPlot2DHasInvalidRange = true;
		}else{
			bPlot2DHasInvalidRange = false;
		}
	}
	
	if(bPlot2DHasInvalidData || bPlot2DHasInvalidRange){
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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G41DAB1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DDB8FD094D71AB7D9E3C802AB9ACCF4636D32D94D9217CD580BD94D065DA5A9F3E792ABCB32E457E2C91DF7EBAE561D37676DDAF56CED762E9986E59090908C8AAA89A8A0B882E2C4E2EED130548DA6C4C79D93AFB2C083A3B35363CC8F8C20713E576FFDEFFAFAFA9C1D3AD4DDDD9DD59F5D7D3D5F7777FD7D5E6B57BDC2596E045CECC319A0E4678A6C6FF036A0F41E90044533FB93111775E906C9D0
	7D3D8BD4A2BC6BB542B16FEC06E913F2BDE8AA00E391D0B57048518147E2613A2902713B999FA95BA088ED678866E1D39563A583E68550FAA063B90A2F02A3F5601E0EF6063BBCBBD158701247D0C87428F4A2DDE732896CD8AD7CDB76A98B31152A78C9E4D6915B40A3416788DBBE2F0463515E51617F9C68374BE637C312BBBAFA61FFEBF72F4DA5BA04650AFCDEFE1E029704D8386EBDCFF89F95911DF9F0DE8D54FE9E796BCA89BF9E4E1300161F47B8BE2CBB4A7C34CEAD952EBFFDE87316B0F1E41E277E9B
	AF8462C8F9A45330DD49799FE4125BCEE89B847283ADGCA814A84A271EC958C72BEB89E6B173BA427D3F29DF33B0FBB86BDE724C15181679D9D5042F8E7458149B3A88834BF7F4A5424634F2D084A255F319C4DC962C16C5B2433CF8819CFBDE4582413134C44A94386160B020B011CD05B057961ECB7DD8C2E3DE05B9785356D366B69B939BA36FB6D69B91DCC0FC5E5BB6EA2696B1FAAB5A043F9B653CB79AB95FE3A0ACF6B70D305CF2E57C31B450A35E1ADC2DFFA0C0F8A45AD8F6B66E15E733106E5D66AC3
	1AD56BCBE299517D36155A6C623E3C2670E3AF857854175FAAF5CA2E2BBE037A3D1CE12275E59CA77725650B8C139BA886A881A895A893E87D97D8E39F4987EE3346FAED9E79384F6E9894BD8215B57A3641D1744B9E5BE917D7363986448BB60F5D566F903D8243947AC88E7A0364CAFD2476874046C951A5FAEC32DDF201F61BD314C50F17E2AAFDA584639115D60B5DA9028127DBC03DB53E2DF0F4583C72F97720CD9683885A5E68A3FDB2E63BG9AC1811C533C3478C93E0A2FE4186A015A2FE09E9ED374FD
	A4FAE0386868681124D11F5BC2A291E22E10BC57AAE3C78F1C8FDE419C6F1FA03C15F03D066BF943D72A5253ED9FA06E5BBC93F80F3DD0D48C47AC2233AF43D48B545D0775739BAB56CFF76177052657676A564FF228653EBEDAB77EBE6DFD741A221F7818GED29C0EB7AF01C3FD8E4D569A1BE60B97AF1A777BED2B11323B65CC1B6F2D4671B58BD930D0DDCF624F9FFE41274215186E3B6D0278DF3DFB1D6CDEB79384DABE275FE620142938783BDC17DFC22ECBA4BAD71F1B390190F8EB39FCF836D9668FCC9
	4677B3C065C01FG3D8334077952486A318568A450A1209A2086A0F2DF41F4ACFC8EF40454B25087C08701BE847A93711567ED15620F95E219861488148614C5E26C4FB01D82F2834D83CA85DA8BD4BCG758B54879483148C348AC898045A866A857283A5G2DGDA8FD4A1C23E00C600620016812D864A9B4AB0B583D90730D632CA0B8D017C2847DC4C5596953F1C65E5BB4BC711AAFFB5ACDFC5ACAF0D6C7CG4B5BC1163743ACCF9FE872D34472F81275CB916B0FCF42544A671A6B01A8EB6E7FA914E8ED5C
	5CCAEA0354C52E2A2DD0885C6365AABE29091D822D852C83B2361371F63FC06B846B21061D133A28E3F5C06A05549C298FD29BD897DD8CCB6A07549556A32943CAC15F7FC31A6BA37F8BF27AFF056898DCB4CE7AC19C93DDF2376896DD03F062E5ED656324B658FAE1C862B2158A5F8D6B0BAE18503D1C7F6EB829931BACFB6C7DBED9BCA74A325DB59CE8EFD0ECF9FD7D6E405284EDB50F135A1446E1FD41710746495CBCA039DC22B299F09156FED8D99B088E5189CEF7C9BE17CC799F7A49735228B84156A647
	7C9B853236F07848BA07BE03A45BC80D3ABDF6A7CCB1E429A4248D11F5438779952A9CCD839EF916DA8847E5C3B86F0F57603CDFBB793810FA45ED50DB434EDBB44BD0B1CC675A2661403CCFFDEA1D50773DDDD923B03E177A5AF912782AFEBE4AD1305FD646FE3742EFE13C9F9514F1ACF600A253C25BB70EED61B2585E42666974A962D38F785A87649F46F9FA4984E947B5921B203BA40703F68B34AD9BA6392B58D246E20F036B44E11ABBF2CDF3FB2DD81DDBE10436B713A39055F13294F5DC861EBC9250C1
	FD7976A4D9AFF945EBBE91162FE44177916F674449D2CD5B85566734BDF912645121DAAB1659094D06F2FCA61EBEC2570B8B185D4C91EA1B627E09637A6C946737D3DC32DD0DFBF29B6252D87BDAF6942ED21C75AA417D2C82F1D5D7E9FB5BD53A8EBC8547DDDC5F7C6D08DB485AD398FE65D5355DCFF7A0EE1DCA8F4199C755385CCA44B50FB25C281E3E97B9AE06619298EEB553F754C1F0B32B90D766206B1EA6C7403E196B3B38136BF3507CA5B8D81E9D6A3C6C5805388AA7DD8FB7BBA96E14D31D17F59C57
	475A7D6C9867D46BFBECB76212183E658C1799244FDF0D388D2C3D402527AF2B86F135AE2A6FA843753A547AD654A2EE1035DF5755F7579E4445B1FD098C1792246F533D3C2ED87BDADDFD79F508FB5BC51FA94AA416C7096A2338F40E6BE46DD6096A1303F009755CBF06CBE43854A05CE48362D6B15CFA064BF32BF1BD7B90D76D2638F6B7457584612A9AF97FB21C1F61E25DC1F540F1892CFD19DB2F1E97B5A1EEA5EBDF2341D13BDF6CC7DC9633DBFC0D626A2F296DB61BF97ED83B751ADEFF7C8347392FD1
	FDB18C179024AF65G62163176B40DBE0A9BEBC65C5AEB74BE92BC945769D163BADA906777D0DC1CAE6E0F9C37126156E8F0F49E7ADD83993F15FD3B2B36B11B5F6C9D07692323634F4A84C3776C96A94F5D84DF6D453D39F905E4DE3142359BA8460B73682B0538B797BF1AA8945F78F9CE38BD4684AF75E339B7B00F2E69756550F9EB578DE253A8C31D4BB8E7B5CE2977870EF93D5200DD591AE0F3C14614FA1E253C3F194A0763105D6315CF43C2480F73461BD344DF32F74158ECAF67CFDB8A843A2E2E873B
	FDC073E4E59FF68BD9FF10311C46B9F090630C1BDCA4744D7D06E1F318B8B3E59A5F5BF2A04E69779D4C2179FF72339AA15C3EE950DC4F76CF9FAF467D53B69F76C5FC8971598157530196G51D8DC25984B92602D73E1AC5FABA531107103624E1697307E2D9D4BB0DD86226B0AE74A49B300D372F945B3924933191D0F09037524DA4C36F1592C2CB34DE7C84B89F25A40EB6B6F33495E29DB323BE451C325FA420AFD50F08177939F5D5A09357B442342511BDF987476F7E3360C986247E8CED3468239CDB63C
	E1D88EC4E35B368DE34B9B075A9F4758EEECEB4258E8D4A77CF67916E15118AEB6117530C7740AF217C772FACF596C1EFA62BA2B1FD55C1E9FECA5F3FB0BAA885F6E324BF61B43BEA956B3FDBF7F14788D6BE159A3B94832C7D1C6F5E5D5E07F2D85BD55FE54B5E78BD97B52751E405608DF5BCE7AD49E31FBE98EFFFBCC1D43E51BE2F5F3B8725DF9862B1F66EE1ABF104385A5EF9812CBB0074FEF471852G13B701FEB46CC01BB416EF9EA77727CF598BBC66132533B657302884C475CC6EC0BD1D2043BF016F
	8EF2BE594D65C865577770DB41CC6F048F388EF3A56AC88679355C1739152DA40FA3442459AB0326AE651C76A89B56D8275296ACD636074B75D6CF4218BF09FAAFD5C52B174636EBEF9D0F6D025DEB0795AA8949FB2B98FFD8057D9E87FE2C603E6C5CC972ED738C3750BE3EFE897BF85A4AA5C24556193AFD2C2C4B27E85F56CF29465A62B78CD945584753F6723A85CC72945AFCEB677E607B244BE19F983D3D7B3F310D6CA738BD520068751E61429186816ACF57AEDE7B60CB75F574E73142771F3139FCB607
	D2E0141F9E4227FA4AF7E3DCD650914775DC5F2D45D31F8FD49786EA58CF2B97EB64E73B51279520C738013A5E2FA67C09901F7AC278D44FF08D7A548EBA5CDC4FABB57A78F7EB51EEA2E0D7F3FCCC2D52DF64D693F09C78C1900F4A7FCBAD5A2B7892660CAFD15E3AA7943BF88F5A0A81DCAA47166CA55809A0DB9DC1BCF60F6FC5DBD9A05BFC93657FD687FB308EED0DGAE01E31FABAD92344F33E873A7F5B866D09DFF64BAD601FC71B454B1D8CF7A51BBA20DAB4374888C535C6FD93AEDD45F2B75286F9468
	1A467535B718184C4752A0F97CE43A7AC378D44FF79AD04FB250311E6B495FC75689DE174DED11288465370570299E5FBE3E9E398B56A3F7211E4CFDE49D60BFA7FBC4F9E00472F29B55BCAAFF2291656701EC9A17CFD23093C1722F8771587D5344674F3BE17E3C1B5FBFCD2136DE61D8BF6012B976507ED03D776E6777B7602A3F06585F6C4F471AD46EG4CF305D94B27FAAE19797D8DBA62381E54B0782D8778BD8CD8E1BA62A78F64E3FD8659DD984227FA5EBC406FE1506166FAFA1A75715FEAC6FCA2E0D7
	F37C461603024E9A2B5BE39B9E3E75A8CB472B571A9A82E3AC110A3444E27E8C3470B1619E98936EC1FFDEEA510C773D305671C69A6F294E3D2D28B3867425F21DB3DB4D41BA4F429A5D96C9A95D67DE9C72CEBA6B1E40BB320299216F4B683B5A82657DF0758C7A2EB61B734B95FE130ACFFD7FE6A1FE57508EED8BE6607B453F38C91E0F04FBB3CCD5C0EDC0BDC08E2058FB713944F91D7461AD5F5F1EA6CBE0174DF1AEE823945F235EA0F99A95A758F55D8D12C3E5EFD340BC1C58C27210F86F6D3F9F24F267
	943954BB1023EB0B064F545F0A98CB7EEC48917457966B19EEA1A6303620397BB81B6C7BF5827FB2108C948B149403F9CB3A5194A9EF78AE1BA50C3DB7AC3E55B792ABE34255C41E72BC34BAC6DB93A58AFFDD0C36A63ED102B511C5E2388F5F4B49255175455D25F75A97D44EDA2E0DB7753EE09D191AEBAAB794A237CBF3AD5C9FACC77BED21016C9DD4C0DB9BD08F5088D0ACD0A2504A7B317FFEFF6343C87D275C4C96298BF827A4E934CB3B6F4FFD2D5A96DD0C7BC2644C1A6BF5776B49F5D7C4E76FEC081C
	561E7181BD39FF8F1153123E3D026D11642A9E506B43CAA36943B6E86B859A811A8E1488349CE86D835807BDDF9E0F54074770650FC56227BA9DC97DDD91626F6D657597A1F25A3C8A31FAF253F6C4E76F6BA1F25AFB23C257DEDBE5F4760E0748E963EB56351795253D0DD5116C1D52351792A2F7FBF57AD2C4393ED83DBA1D1FCB6A548FED71C0C9C0AB005681654D4CB0B54D44BACD08DC277C73255B295399BB234BEBFC081C36EE3AE76A491D5E95DDDEAFC6143B2CEB2FF2F7F471B50448E96BC6563577FA
	085C6D45776B08F2313A761CB55145B795A2270DEF212E3D3A5A6862BB94D1AEC5575E3F06485DDEFC39FBA245172EEBAFB9C46E76627BC9C439F53A76266D0DF25C8E115346E77C3A1E5C113A6862BB99D12ECA57DECEFDF476CAA24A35695A7BE514765E0CA85723EB6F7106686CAD0DA8B728EBEFEADFF4766E0BA8F7DD575EC70D51593B94D1AEDE57DEE5D31463F5081CD9F31D24EBAFBDCAFBFF97515EF2DDFBB377C7174FC7A24B695A3BE20EAEBEE7081CB63E8D3A76369F08AE3E3A08F2853374647EB1
	C46E76627BFD081CB63EDADDFB0B1A233377D4081CF63DF4D4571E3DA5BAFB63A1F2DAFB96DDFBCD2D51596B8811535AF3E86C51EF591FD35E29CF0736F9C01437290070D260BAFD966EB5A8DB61B8C6284E9796DAB5F67EA7095A7871D7EA03FD73FFB37C370A5F3F297F2D62D3B7753FD5FC66E68965038E75AFA5307D391B377E1671451BFA5FB2126FE6F304DF9848DE104767126DCED1B1CB77DFC5BF7926961EBE4E7ADC745104D876EA772F84FC5E7965C6727EA2EB96BE57DC91DA0550671A9303F6D972
	20920BE873FCB2B4449FEA4E3945817479C2F6787C7DD736FE7E86327573E74F3EF57E2EE5074B9F2D5F970A31FE4B6230FE0BCA83B57B0E6ABC21642B2F5FE73ED29B34AEDE5C26FCFB89E9943D4AE6D737CD36313637E4728DF648372E9F43D32A405FC5151B986604ACF53570DCBFE1885F2FCF9B747B75D96577FCDE4967999035FD773C210237859E0ACF4B221338C3B1AFAB72324DB3AC06747DEB0ABCED8BA77FABC3387A20311E5B260E35157327AA547CC31CFFE8BB72FBE529B7101BBD467039E9B26A
	67265598BEB7C70C11F3F35C98BEB7270D11F3F356C85E6F755B876D9E7605AD0D71BF36672B7B7FFD0CFD698EF5CE8E721C0CD686618FA17FC0159A5F46713F2B5A0CB99C72C8CE48A27567BB3961F338B8D7BF07493961F338B4B7F28ED3F34367702FF3A367B0ADD7BF07BF5E290E7DFD9E7B34DD6A7AB94C79E7F72B7181FEC1F53E8A5F524C5F815628716D9C7FC52DDAFF402EF90F9AFF04735F5A23468778CB778675A9376B2ED3638377CBC73D1A9F280D2286B5BFD0832B0378017B6BAF77297901386E
	EE8C4AC38B4FDBD3101F87113FF9FF903F8D794B778769E9C57EC366A0BC575FFBA0884F753F579C0467FAB2037907111F509244EFC77EC0EB907F8872D765136F299C368949A78B4202B8B2177DB37F4DC0BBDC774651B55C74B87AADA51D7BFFB0CE6AF148E3F342B027CC4A7E59C4F6D7814A2E81FC96D085705B012C33F15E7F5546C275D8496684AA1F493F29CF1DCD6D2633630659784D5EBB1B715DD4591C8CD34F9C74E978BD727B9E7ABD2164B45B7B1DE65F1853EC6B174DA2ACAABABABE76B99CBDAC
	D401FEBFE8D1F4CD879D8B00164C41EF9C7DFB719B4724479E950C937A5FF22E365CB02C1CC37DDBBFA7708EB06D11CC837DE671D1E5DDCB641A9E047CBDC8F8E985E5AA9D249FE6E753E7915AEEA8D43713F90564A7505E9654CE7C3DE4D0378F8535672A64E9ECAF6F41585EFEB9D9F0BCBDBD6CEF61139E24B12531E35C1A795985937E9C9A1B2184E34B0B47581ACB343E4F4FD17B56DF2275EDB6731D2EDBF6EE0E74CC283F5EE93CE3B97A7EF305527F1D715A77222FAA7C1EF86DFB514FF86DD921ADA59E
	5F154765113A2D1E1BE1BA8AE481F2G4582A54E453A9F5884435B2D7771AD123B87A6A607EA89CA637BEE411D46C77B7A39CD5857CBFE34D8D8B623BB6C373AA9F3E99FE732E37D2F67E713A30D6FB18B092F60218CD3BDD08F50A0109FA86EA10C6FFB1B58BBED320407953BEDE0C4799A19BF90584571G1B8C9AB436CDA5517579ABA4945C83D41D77ED7E6A575793DF298D1A4F79F96157A989F97A6B144772741FEF1272EE7D7C37A4AF7CEF591E4DD37F160D56433A7364BEDE88FD1F8A3486A88B2860E1
	2C0515675E233520D387A7860749BA097CE2DFC67BCFDA42477AC3CB645F797F54C26637FEC916A5A7510F7C97AC243FE4492DB0A96FA58B491147BEBCA2D3A86327D948F866900794B665EDB89F5E2FBF1C0F6C17710272DDBE6833CC3819CE5385B267CAC3C3DE1147DFF4211873CEFBFBA52F1DAC727E9BD0CB878804E047271F94GGE8C5GGD0CB818294G94G88G88G41DAB1B604E047271F94GGE8C5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586G
	GGG81G81GBAGGG5994GGGG
**end of data**/
}
}