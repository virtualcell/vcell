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
	//
	//
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
	}
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
private Line2D[] getLinePlotSegments(PlotData plotData, int index) {
	if (plotData != null && plotData.getSize()>0) {
		nodes[index].setPoints(mapPoints(plotData));
		Line2D[] segments = new Line2D[plotData.getSize()-1];
		for (int i=0;i<plotData.getSize()-1;i++) {
			segments[i] = new Line2D.Double(nodes[index].getPoints()[i], nodes[index].getPoints()[i+1]);
		}
		return segments;
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
	return new Plot2D(new String[] {"plot one", "plot two", "plot three"}, new PlotData[] {plotData1, plotData2, plotData3}, new String[] {"title", "X Data", "Y Data"}, new boolean[] {true, false, true});
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
	D0CB838494G88G88G63FC71B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DDB8BD494D71AA68993710D0A89C9C84698229851E8A409493899E60FB3C3B66E8E13B189BBA1D9E7F592B7636EBA99A74BB9F1769428348F20C1D0093C95D1A70460A30A884A53C78621C5D412189D9A3A00DE9B2A53DDC06318E47FDB7F3D372B2B2BED6D33B9E74F1E711C5F6A7A6F77BF6FDF77D1F591F20AE6ECCACE49910464CD827DF7BCD9906A1A85617D5F1EFD1D71629D29C6C1752F84A8CB
	183EFBA3DCD7C01BC139F7EECB03EB86D0927048354919EA4C9446EF4B257CFCA77299A52788C21ACCB0333665D3DE88E0E6G4581A5D27CBA38EED1ACFF2F3A0A622ECD0992167FDD2CA11D68D169E454A1232C0BDE85611C21F808587A1962A711D9GED2B01C8709B846397F9F02DB1DD6FBB5553ED15AB6CB6C92E2EB6417F071BCC66C151A6C4A8723B52B6ABF8C130407D01E1427BBC1368DC8177C984B34C786F67903E8DFE8783C58C33B8EA7757D0BFBB7F722C30FA27BD4B68A30E18E174BFF1589DC7
	DC655C148F141C3F12C7F21BBB12EA2C81B28149C0E1C0B1C0A41EECA1F1779F61DA5FADB7CA838352E03D5D5EE033B8AEC89651863F2B2B2105725A459E49E191846C4FD736156B78F3ABC23915F7AC07B909BA427AB63AFD3E30E451AA03C1A7A7CB22DEB4AC9B41DC2C9AF16784ED672779323D5165D9FB1E362F8AEA5BF1FDA625A63436D7F5180C39D4CF394BED3B5FC57A7A9F149AE8015F49D4AF72DFD77851AABE56E11742A777F150D6BE4AEA23B303795232F126303672573AF9F07D6DAF8D212368C3
	54285617E7F7925D4BC751E6A37765D78A5FF2434DC7DF12F724517BF5501679F52A916B6B4D8272DC16FF1BEABC87548564828A870A810A7B1655583E3C0FEF3346CCE6075CB0E435D9C4070032EF666D042B68129D667303CE59BC58A3DEB1BB2C66EE1B6894A8666DBC1203EE8F39B5CA6D77000D33622068B04BD6E990341B87C4D9F4B891737B3CAC02F108CAEB07F5C0848383F601697DC3DEB6DCEDE627FC59EEB14B229B016D1B72C91F8C1B2F00C6D0G3FB1AF5B0AC83ED6C37CC29064AB0865A1A81F
	687B13680061223A3AC5122E8F59BBC8A4C2974189C714312394FE5784319C9B0A89AF926E17F0BD4F66EFD769E9327690774D0ED1760CAD4F4C04EBBC60D7832D874A3F0B554F3FF5327AE9CA1FA7243D32D237FEBAAF1B0CD5F7E15D186E52BEC73FD274939FDD50968E3464AEB64E77E6F62A74909F58EF66471DBC7B0CD6D3BDE863DB8F9B9BD43F8D7419894446A6FA453C8F1510BECC84FF1300F26FE639FFB73F98EB39416C94D97536B92070C40B3BA7504F3F5999CCF3CBFC5C86C4662363544773C0C5
	824E17E4FC3FG3483288DE89750BE6A4B07349E8F811D857A84E8AF50C1A072DC41F4ACFC81F40254B250C920A3C035C027092FBCEFEFAB7ED4C2AC1DC0C1C0D1C071C0C9C0457740F38CE4878A870A85DA8D14991CEA2C826A828A81DAG14GA4FC8F66B1A093108BE886D09C50DA205CFBD30DF5C07DC021C00B01D6GED9E13EA2C9C43EA4530A3B3451D9F75184BF2E5D471F7503CFCC07311214A5FDE1A2F8C1A178F696F0FE95E0E503C9D27F9BA2949CF864D63D95AAF9934BF5AFC544A971A7B1EGEB
	6E2F64CDD89B13145AA0F531C95516AE381F719DAABE29098281EB015581995B49F87B1100F5426AE1AF7DCD6AE2BF2D83D2AF2466C8FD105AE0F551C83124FEC8DD31FAA4F518A7687B7F09667E447F011C7EFFA19C03D79710FE900745C139C9340B0396786124ED7FDEC0EA032E97FAA5AE73FB05EF0775C5A3CC68CE4EFF3F005409D9169D566EA1D93CA44A32F5304F5D3E211058F28EF55B5DCB93E6EBEBA129CDE98456979CEFACA4F3F30FB4B8A8AA138197216DD94A5AC034098360F423B4B4A8A3FFD7
	9159AFDD97C7695AA42FA8D5A0EB8B5B90D967609EE4011954285DE19D00A906AC150428B1E45D70E9DA2EAAC71D40A3FB291BF08D9D43667D113DEC5EAF3DB1C7187167978CFAEBD85763EF98560E4139F6E308FB5EC71F520A75FD5F2E2CD1A85F093E561DA53E2A77C79B946C23DB4873EBD77807A86F79349C0EE59D20489C427654E1A317E16D0768BCBDFF8F712994FC2D82B20530F9FAD2B1E9E7EBA4BAC1B7CAB69BF68B340506105C6D4F4A21317743FDD088660E5CE3EE03362BF31BC05BB70F05AB90
	6A381243F49C85DE4BD8378E7465AFA5E43D64943F9A92E179CA96FCFF627DFCF749F6CD5B955A6758BE2E04645126DAAB2E99CBECD66DE2FB62EBE3F13DA8D33BB1E351B66256F3DC55B8441946A1AEE41C9A77C2AE43C55176D8FACD20384A71847766EE06DBC75B534663BA30982E05DCDFD49E435D246DE163919FB9DEED37AB1F61162A7490DC2287AE270061B6CF203889FA7A92B82E0B62DC9437F88262F2A692DCC4A1432D19006B1E0D935D76AB383E5EA22EEFA2664FB51166F922BAAF870B99EE6DC4
	DC8FEF1E043862C96A3C3C4FF1D5345DC42F7D13547A1EAEE13860C928AF0262E2BC740D5943F04BE87BAADDFD06B206DBCF7565CCC65C0149EAFD2FF1DC8DED6F182C27EF5E89066B1F0C7A02A8AE4CC35F57553C2EE8FB2C2E3EE3A799EE79E45CD32C21FAB70722BE4489B58C179B0A6D1521282F4583371C637AA9AE086242BDF033EA99EE814545D15C8A8F5CBD2798AE0962B227A02EF40A9AF769B46F5FA908B3D11CE50ABAAFFB6B984EC55BC3276855F3A247C55276A59A9C5A1DDE4FF071546EEA0A93
	262A6D76B5707CCD45764A29FA7DD15248F06726223EAE0AF3F9687B9D47055276A80DBE444DEBE2385829789CA5D0DC6EB4B56E2BE606B3CDC3DC7FB4BD5C51B3BCAF9437C40343F968F78749782D3C377BEF733059D5619C0169233A7AD3E50241F7F6B14A3E1B60132631F7F3330DE4DE298C03318128AB0C4D23BFCFE76F66422EC789117DEDC6DF6F98DDE168C7C418FB9E357C7674961C371276921B095036A60C4DD91B76285F8F54BB1DD20FD5F9B5C1670218BD6AF996F94F6D598A57DE2B43A91F0705
	100B4D9BBF5CC37CA56FAEA81B3E4BE94DCA93F0DDADCC07B99348BEDDF98F1BC5569FE4AC47B8FB0E30B8C3EFBCAE54CDDFED5866A34E1869985F72696EB84B3FF9EFB3667F494F778A3E5E1BFA4C75747D69626D6C7DE95AFD2CAF9E4BA6BEB7417DB5A09988E379BA0745B281F8CB6FE331BC1FC3E2A16387624C3B52E87F2E874C517BD30D382E085BC576G835210D33CA011BCD7505F432265G29168A7308DC212CB3ABAE1016E67273A02F2D3712493BD33BE49D14C587CA357894BBF9708AFB1FB8B33B
	0E66EE597C19C27E3751863D773B7D46E78C367BB127E1773BF33BB6FCEE4A46876626E0EC7BF2D9ECABG139B4EE21B10DB4EE243281ADDD67916E1E1CCD74B49FA58A1BAC53951A1B91D674CD6478162BA2D1F3739BD93588A6176E2F6933EF550AADB4DB66B8D71G55770B0B44EFD88F4B8E49C616BD0AB254E548E37D978BFA12382EC733485A9757FB82DDA3CE4DA3FDAA77DB1D1843F76B55B98CB77CD2B70767E63FE528FCGF3577900BB07093367262C1D4DF27873BC96D394E0D6BC407C284AE7B6B1
	16C79A4873B9243C8B2C9F1225F673E01F288404FA4666B3BD398F42387FA07BF630212D084B114ABF50429F058AFC92CEF29D3585CCC7884847BE48FC09ACB8CC7258CFCCD6B8E55054287C469E25439A6DD4ECE145CA5F6196B23DC9203372A126373FB0D03D98DBE159FE9E5B952B538AABD4921273D6B17E381075FBBF78B1037BD22E6C014C0E3EC35847B7BFE3FD9C34ACC6C85A7E3AEE9FAB6B7207E85FC6387B38947A370A777164A2DE37008909E0B65FAB7A4873B9E934D9FB2E5F5E737F61D172BE41
	6E10FAC42773829776B38820BF6D453C764117A46E4FF38A5FF541BCB8E436A985067C15DEFC54D3DA42622AFC98664A07191E71A5DABC7A7CF1F13A3B06DDD83D2CC65EACE1BE4DGBD89DCD73332479A7572C97262231E61FD4C274CC7E07EFC0469F9E31FBE3E320C598D826CE20E3F3F0C68F011C7CDE063C02C878F65057D4C5EDA10BD70FDA69FFC429B7B6A89E62B8BF0619CDBDBCD30239E36FABDF8A8BF2B1A590A87594DB318FC7AC9EF6C1FCFB2DBF500F3F1ECFCCE06205D4FB21B6F1CE4E38E6AB8
	D843F4ACG7955DCC7E88D69C7E73FB4228C537DB0CCF33FFF205B067A7E407595BF1AEA6CFC14693354EE21B22D1205ECBF292EC9DEFC7A1D34166989859DF1DC4F31D3E41D609CB45BBBA414C0FE17979F75C41F626B1119309E194974E41FA66BG57A559A14ABD7D482B7160217C4D53CC5E8E32D1DCFE25029D7510CF2BD373D03E250E4F1FA01B7B9813DFD467EDEBD3BD9FC3G97423136FAEF3DAF5473679BF0C99C3B2FE1AB2BC96589E0F9EE7362231E50867EFCC742739D49747C078F7C270D7C9986
	EC8247AFEC5C4A6A53436E6BDEFC54135F441F61476199FE1C693927C99F7FAA478781F6B14757B69F91F456D8CD8EF3DF5F2DC7D99C2FDEAEBF689EE309143FA596756741E6BEA600AF87E6B1FF124FE846FB932CF51C7E46FB5479659926338B7405F31DCB4FD4F86AEC07B53A591FD2FC4F7D1C57B76978D96EEFE42BD43F793AD079D61B27FC9FCE1A055FEA13B93FD461EF1C6D66236F730C6CDCC3A634493359774511B232BFCA007BF5C0E9D1301F85EA8232C431FD49593D248FEF797D76BCD9828F1AED
	17BCDE14326F28FBC91E2E0B2374BE391464D0F937A930BCF4E411BC84C55D7E77C1146BD664426FC08E579687BFD71F95D91599EE58A0682FAD62286E04A8775A82F3E7EDA56F7DF267401E822885488294BC07656D5E52F2FFF9E35F32E94268F7436D37BAA391B947D7CD6CD276C30B67E8EB22D861AF1D232D09B9592CA662C98CCF306FF27764845697D3F26E34AFD02EEF17B65E70A7BCF544E86ED1EE48CB2ED0F31F201143FE7BE19BF9F730965A5266C23D835581D9000200A267327E7BC5E92D3F7ED3
	9E668E2991F867A469FA23763DBF7775435C40E2BC61A5D72139DFBAD7CF6E526E406C193D64347692F56D256779BB5723EFAF4F2F5C3A39FAFDF86CA2694334E8985B016A002E818581C5G45C633BEAC5B57602F8F6B59471F8E097F5469C87477B5AFFFEFAF2F3F76125366B5A1DACFEEF2FEE0769E761253BE9BEBF56DB59684E66F22171CB63E4D7374648C0501595B61A52735D72CEB6FA5AF395B2B53FF74ABD7B5CF2FCEE35AC91D1A204D86948CB483E8A9508A200D73D91D5E70DF277C78526D546974
	2240727A18171C36EE72676B49F596871657DE3FF2C7F56D7D31A430782A3C6434F55322EBEF15175C6D45772EDFB90B2E3D1B7B820BEF1C171CB63E1B3A760E158596DF13DF39B0DDFB297B830BAF47CBCE9BDF342E3D44930145770EDF39253A76222B830B6FF9AFB9EDFC093A76441301456774AB37CE575E47B501592B75AB1776241E5C065A406CE57915AB553577CA0076D67A152B5135775429406C457A156B5035F77769406C057A1533695AEB2F8B6CF96872122B505C876B5A5BD69F183D8F3C643476
	A2F46DAD75123B3DFC3E6CDFCE575EFD8D01459769A5270DEF192EBDD3E3E0717D17DF39D53A7676B48596DF25171CB63E753A76928334770E171CF63D1433C0CFEEC6F3E07666FB49E96D156B5A6BB993183D2F3C6434761AB47670ACFB3C72CD7D9A3459011037B303704260BEFA81FB57209C0DE2EBBA556FC546CE0D1D7FCDC29B4B3ED39B744CFF196F330AE34A744FAACEAC53BF2BB82DAC8B7920C37D17922C7D2132DB1FE5FC2CCC6FACA3B9B73BC1F8B5053C8BF28C8D4A5681D1B10B6FDFC597B9D38B
	3B0F7621C15C1A904BCE6D7BAB016DF75E3DC43EDF44AF607B1A8B07856FFDCD3345AACB8E2624C3B4BB5AFAFB79266612DD6CE1BEFF556ABBFF2ED67D7CFD532A1F3F6056DB67EFC22B2F7CE17DAE5F4E6AF78D2F5F029CF74D1629FEBF197D5D57EF5CF7EA836B62451D4A59CBC82368D4DEF6B519E5B3EDFBDBA6E7303D4E3A3642AED5E05F979E593505E21AE52971904F750BED3E7BB53ECD3FDFDFEDD33EEBC9C30E9ED15BF7EF346572B67726783CAC8E90F7907316A2AF1B9DFD22D75F7F2BA20FED3E64
	5FEB73D59F98EBF72EBA56431CBFEE371A7F8967B765B13EC916CC6E5C54DD741D1B339775F3F361226F5CF4DC741F1BAFAF7A4E0D65227F5C8CDCA45F773A2D962B031E304598B372372A7B7F980B7DA7796A1C9C61B939D160017F0471CF952A71C7B9BE25E89B4BE12FC39A00AC22BFAF347B4E610F5B75F378D23B6F9C3E5C6EBF072F377B4E61BF377B4F61EF5A75F3382CC89D7BB19E7B64E2F57D9C67FCF3099A6F66676D592A429F2AE47CC57B5478AA0E9FA8D36BF75B7D7C049AFF0273332B55F8B7FF
	E535C71FF23B91A755F8777352DB23663BEB233AD64DF757C05AA9B55F7DFC7D0B875F9D57F327BD72F008714756F978F904719B6ABD78C7997F7DFA8FBD07997F6F9BBC70DC7F18C68FBC577FD913871E6B49EE76609FE77C57BC79D50C7F60998F7E8946FFBB0D1C273219C725A199EAF0A1194B7E137F4DC0A65C9FD808EB38EB8B71ACA54E7D8BC7C8BD76BA4C83B04CA9133269A9A27BD18613DD8278F82035C4A734D5BE45667D77AE252B47CABAA7207CEF78197A702750EEB43DAEFB0A1D59ABCBE75F26
	56G2FF4917329EF8F797B9EBCCFA88DD4D83B87AA060687AA4C5DF205880B0A6A6A56A11B2D0506AA6079419EC557B550A183CDD8444EB83A4A58994768D9B305F00B7ED94E580E178C110B503F38C52AB3E415F3D3704C62C2E5DDCB64B646C07EE2886F1FB2F2D4BAC8BFCCC9463D8836E799556DE4DEA179F137B7FA3493FFBFCBD1378FF934EFD249E3ECAF6DE3312DFFE9015072744BBE7F96BEB886E30A2257459B5627C6C434EE41583232D9ECABF8EC2733353EBF30C16D5BD759DA5F26D05FF15DD21E
	6EEFCF283F5E293EE3B97C7E791A527F39CFEB3F0B2ED07825CFEB3F0BDAF96DD5C2DB58536CDB795C4B24EE1360BE6719D4E3B9D0931085A86899D6775FF44070F66B77789D123D85A6A61BEA890A71BD13F12771E1DF7F48407ABA6679A70470FB277ABC2B9B768C76F18C3D1AD2562712AB46F76AAA09EF9534894F42B387D483E4826AFF164577138E7ACD1BAC61E145EE6E69D7CEA3738D01D59CF133492001316D4C8E2C4F5FA0E5CBFF3F257AAD25FF776B6B106F5486BD53FD5977BA6587177557A9BF3A
	2C3F3F796965DB6F7FFEF659775F323DFAD97D37ECD88F411D64B93E89FD9F8E3484A89EE8952F854B15BDD88BBAF550EC69A36BA472977BB233FF762A6FD8DB2F7A7FBB7F2BD7497C56AD4932B4C074B37E17D7C9FF4912DDE1A22F7BAA4911435A57AFA314727B2F12714CA676AAEC3A6F6B746D5734CE7FFEC5F4AA6772C1DF47281D6AFC34134C39D2EF2FD364714F6A4C643C73CE13643412C55E7FGD0CB87880DC076274594GGE8C5GGD0CB818294G94G88G88G63FC71B40DC076274594GGE8
	C5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7F94GGGG
**end of data**/
}
}