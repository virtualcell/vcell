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
	D0CB838494G88G88GBCFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DDB8BF0944719D665EC0ECA14C4F6E1AC823EA304A4CA8E5B64C26EE447AE1384A7B8A6F5C4419CD5061C4A26FCF20AF2289CE7D389540DD6EFACC0B6320DEDE20841D8D8C23CA490C220472E1E6831C80B9012CCB412C652225D1DE5F796AD92967777F47F3DB33333DA3CF52E3A3ABA2AFEE6766F6FFF765F0F699989057B97E625DB8A85A1BDCBE07F4E248BC2CD3DA0943F567CA472AABECE4993B4
	7F7E8274067010B3932ECD5016237ECE1B4E076BAE208160116B92389688421D3D0CFF8F63A325DB84C1A9A218776F6CE33C4D0049855EF1A0DB9145774315BAF0EFE58543755F17A4EC79254F3213685168C4FA024926326B81610F69330F90DB36BB79CCE6AF7C36831160B3045DFD6F43354ABEBEFAE1E8502914FADD32D2D9E9077FCF5A6C22C7F289191F907978BB39AADE90D601EC024A7B178C2233896E8700021FA05F1BCB78EB60FE93D0E6B146F17E63AA66E74F54BF8BEBCE3AB3F7C708E3DFB1755F
	D69C0AE329FB0365DDB5679B2E135C46C15BE220E42075C05B007615246411F876889B1D1F4035F6D0314AEE376C2975FA6BDC433EB6F9D8F241FDE5A534B0DE27B4A47B06850176E74B1D9293FFE6A2AA57733965E8CE12CEE15FAE69FCC4D87F4339169C131C2CCF1A32141550DC341604F2C2ED5F9751362BC4DFFBFA5BD7842D6DFD0DEBB332CCECD7142D4D08E3FA66E8EC2FAEA1FDED4DA4FD3D8C6E5319DE4A372B7C4223A1BE2D4367541AA13F0FC35B1C23D89BBD3B5017564CC5C2410987CD73107853
	F84B5623540702D223AFA5BB086E2AD2EA534AFD69D6792BB4FC6A4BCD35CE496F7EE3D03FA7D272E8FD3D64A263F24E29143C25C0AB01D20104B228AF2063E5D8E3C72F9F3B4B9A330BBE25AE60F48DCBBE014A3EF47DED38CAC145A7B6FB7C0A6899123AC51FD39CF4C9FE01E1FEFF1D64E0D0A737C52D7DA13051A8F9A41F28B8E58FE8975D12A2797C94337D7A9B84631354D60753AD018137D7C03DD963FBC8ED08FE6532F7D8D4249002366F9CA7FDF2D36C860D20G6EE9DE76F8C83E6C907F98D01067E1
	362A6F22640369223232D5164783DE8709C4D8D9C6721C244E9D71F03F388CF33CDBA63C7C72143CC36528672971B7B5FAEC4EA1623E683B05E32CB9330054A3606DC043C07714E37D3C520375E35B7930D0725C8326753384EAF9E1B92D1B64F263B86AD675939FD321CDB88D3E1D46F97E790C9E6D18A88F0DAB7463730CFDA4BB1323B63668ECE4E86EF358180945C6963B523CFF66A5FDE80338860062CEE36E7FB8FE0056F21D681730FABBFCD0F852F028A7281FA7F75C43F2CBFCFC9D082CC7E7180F4DC0
	7B853ADE12793D8D68AD208E20F700BEE23E9433FABC815488D48EF49068889099D7309C8BFDC0E7C9AD831D83BA85F49E281A784A73764C8562CFA244329CA88D2860CCCADE8550G50EC2025C06B00040A143CA2A0BBD090E8A150CA204DC0FB4F266455G0D81A5GA583258265D64218876A810A83CA82DA8D3495684039143CA6A0AFD062B92C155B398516D0FE34F3AE66EA37067F964B4B3BAC9F3BB479BB4872350B6525185D9FE3F9BB4572F60665691CA1BF3BD89E9BD93F6CE27D519121D67A8C3F07
	E22C397F27F022351111C7EA0354C51626ED27909A63EFE97824A67EAC50DA40BAA0F3BB19EF0F8A34CE309E8E32FBD2979F33BAA075C2EA0E5487298D2C8BAB43127AA1F50575C86A70FD415C7FF2436F337F8BF27AFF05689C3C59C57AC13AA9F9941B6415BC43F063E7ED2F39C8ED307D42084CE5362BFCAF6CAF2C30207BB9BF43CD6AC4D4941FF3B020C8DD1222B8BD232176575D4416BFB0688DEDCD50561BEED21B72846CAFB87E9DB7D91B07E40FC7D29783AE425A5FD5778612CBF20353D6B960D1A8FF
	3F07BCAF0DCB37585E6420A7DBA0FB8BD7006CF368B348D21154285767F44392C336CAC2C19559B7BC451F2188AD077915BCCBED046B56F3386ECF9C44F57F5064770465FF75DA4C7630095F7E3A25270A2E352E2A503ACFFDAA144DFDFFCF5D23B03E1F7ADA53C8FC55BE9FE52858EF2AF33FD7651FE03C47738BB996BBC015B9C15B33EF6E66B258FE022D537FF40378948F3EAE84CA2E42F5FA2ECC5AF10F4496E82B6CF251EE01362DEA6E96E4963258D7436F34AA1ABB721B6676BF3334392DB9CF5B3D648A
	C4F5BC1B03BA96GEF5979108E6A4B5FF849FE49AF5D88C830FDA59B3E0B3C1F3F6CFD5350564D7A1C365F6FA5F9F4E9760A0E8B4466FB5578CC3C62BC5DAF2EE7F677DD2036A96E379C37708245A5DF20384DBA5C429A4495D45376A2F62D29263844EA02DBDE0738FE562ED453FD60EC38FE4075FD590A380D2CFD9B436757E86D565990D7D29352C3F0B69D6ED50E73A22E46CC5F637508DB49F0298CF7201662EE57925CF483629C35F45F632A8D59AF657A1A9A393EDA1A3F54DA1667BAEDDEDEEFC2DCCF9D
	5D8FFB6BA8EEF61DB6AF9BB8EEA1EBCFE65755BAFD8FB6A3EE935317E9E5F62DDAFD4E9644153176D62B193E175B90B7EC257AEEB3DC02CE5FE36D08DB4C5A1FB055676BC05CEA26AF0D613669745559F9DD59D8DD594C747D09632AEC741942E1E3F934D1FD947774A544453176C426EF198EB72F137B47F0E98CA754EBF163DD085BDBCFF1476BA92EC907BB6FC05CG438599AEDE075BF91977AF43A5B35C2AFADD9DF0DCAAEB5FDAEFD64FF33B91175FC05B8FB568F154EE479544D5B4D03BF6069BEB505A7D
	CB8F4F9FEBCFECB06B0F67B9EEA953371261D2F57A9E3E0A382D2C3D20D12F0F62C6FB91D754C847D18D4345B5EAF145FD08CBE638552638D7B8AE3F09628EB569F1F49D7A0FA3E47ED64F6D2E0BB745E029FF82160F4A4A96F50121E7F6C704DC069FE8423339FD6A79CBA27CDE8A3432895751DF6F443339B963C942564667B2A21DB126B6D1BFB21BC36B686D27A6B2683AA57809CD9B34B91AF14D5AF9C3FBBED06B774BC3CE75E8022D85CF5C502E331477139BBB60BA62747915E6588885F15D78458D62AF
	B93BE0ECF61673B3753C092C2BE3E0F7A1503AE6759CB60B6CBF48DCCE639CBA05F1A6CCFEDB98F8609B1657A344392F0546D755920A3375FCF9864D7F233D8705C867263A351E1D1F9E4D4673D325857BE2EF8E71F9897CDE81349E084672079C0CE58B70CAAEE2AC47F2C9ACE47E20B8EBF5BE6B5FE140ACG227B0A6FD713E7G379C70CBEDB249F3A93B3FA98D9711EAA995A714D2F51FD95AC6DA6A496D91DEDBAF251333D32F6C74A8120FCA35C694BBF7249B4F93976D29E139DB7548A2216A4EB50B5979
	6E6C5DE316B597E9CE37DD8C65F6711A8D16E4A09ADBCE8D4656548A355F0A31F9EACAB0B69AD5FD50294C98960D69CA89598F7BA43F24D8FD325F5FA4BAFDC544F5D6BF4FF0FB49E0EBB337B7370E701D9E276294DD4EC92908697B4DA562B76C07951F6CA25B9ED5995575F29D76DFD1DBCA5EC09B6AFABB0B6CFD69FECFE0FBC4452A56671853CFF37807DAED8E13F362CDF3B8765DC4CBE29B4D5D7236D08E93DE5CE0D97CA2667007D60C29209D667DF67463BD9B5A24317CDD9D991F8175AC30B620481D22
	E7D4D28322FA46ED28A78EF4A437633B030C0E7DDC0ED4FED1AB9F8A25F4A41C63BA7657230E4DA0DF54013E4C2ABFC972B8C6CC167A9550E4D56FE90F32E90DF5AAED41E225FADF64FA87C0E7A2575B52902BDE9A5B8707BF6631F5BB7DCE582112107CB345785D866C775560C72E9DFD5955C872AD7AC6CF50BE3EFD957BB8EE55B2E16F1E2F1A76313AAF37533E9D33077AF85D8B9BAC1BDE40BE3E51486B96B01B394DF50DC77563446AF28E0D5F5D78AFAEA36789DE1FBCA4797DEDDCB84AA4C07DA9EB6235
	FF89EA7F927AB3DF65875BC4CFC0F42985C679CF0771291E6CE60CAB91F42C66FA5C4DC6BC757958011D219A8E526A459AD95E0CBE65F6427A5C093ABEEEA17CDBE1BEDD8A63D3BDBDED68D390F4AC65FA1EEAB3473F590EF653GFB208B71375AD59DE42889B88FAC5271287C8B6DE82F87E49338FCF3C7B8F6FE875ADA89B84101D80B1DE0EF69EC956BF8EC0C5B51D68548FA39FC0A897650A534158438D40E2D4F5DA5981FE751662317F04E21BADE61BA76DE06BD5DE554F139137423FFCC1ED02769B11826
	395FF126EDD45FCFBBD15FEC50351C6BBB521547E45A65E1727849F4F50571291EB9DD28E7AB68B85E0DFA7E54C57689FE0F68F548D40272BFF29879D44F08036FC7C047C22E67D78E328F88F6A9BEC9999A233CD7F5BCAAFF7AB24A2F835902ABA87F50E5023D25137F390E4746CFB7DFBFC1B60E4B3B3B43EDBD453149005B4C31072F046B8DDE61633B8746F78FE27F6D4A8E2CC9F584E01E73FA0CFC2A2725070FEF50311A6B79C184FC66D5BE062F42983E0A78712BBB30BEF5F69F8863D3BDEB2F72B18CBA
	16F2BD67FB4D71893D08CF836C01DE443F56FBCAB059E359FC6268684C33AC1D2FD2CA0E0466D8A295ED0B457CF17471B981FCC9607E7C384FB05F5BE12F630FB65FD31D6F74234E1520CF68C31D537D25FA1D1D30C79723A925675C2502711DF4C5DF689DD92B669E093E2BCDD01FCF867A683B5AF44E1F2772DD9ABE757D438C7C2EA188ED6B7B707D621DGF9BE2A69CF496B87D200620116G2D6A4767128E9F6943995F5FB613AD30C7F4F5698ECA71BD2A1F64E9DC3A45FE67297BA975ECD3606F26B2C99E
	527A6F7E7DA0157BD7D5CE78746E65685E62C82F76DB114CB7DAAC9902795E62B853DD73E9E8EFC1F3F7FC021C7B4581FF8150B220D5C01BBE453CFD4DDF92ADEF78AE1BA50C3DB74C1E699B097CEB11EAA2DEFD9EBAF04DD8938F287C12EB461A78A087EB22825A96DC4377F2DBF3E26B0B6C5C4F5B976CFDF535B1DE612FFA9D7B8C3F295C15B0398F8C3FEB8CF2345FE6854959C18F34A9C071C0C9C02B0052007287307F1E771F0F56FF6AE0F648D660B549723855F86E4FFD5DD993DB0C6F0749159AFE178C
	1849155745E64F9AA6E734E7B335777BB0B9A3195B5B9ED52EFF402C8F375FA2FD28C0DB82D092508A20B420CC911E1BC46C43D225AEDA9F56624B9F074CEFCDBA127A7B18B5363C7EA2CC4E18579A51CC6E06ADB6FBF742640CE32347545EC7753159BB95A6E70C4FEBEA6F65986D3D96555EECD3FB8FB744D627BF0EAA37D0B42B5329A9D22749503686E893D06EE0CADE89D09310EB906B74CB516B14FF3EF4B7F5BA5198DBDE6F8D13B3564DBD03E6F215CD316535A92A5C82D3FB794D314577DE181C31EE16
	195A7BF9185C5D4537A62A5CAAD3FB575AE20B4F95A6E70CEF23293D3D6D3145F7B82A5CB6D3FBE9E1F2F7975F2BE1F246788A074C643E5991DBFC0FC615ABB135F7439E633C9DA6E70C4FE6EA6F13CB3145D791D52E5F545E16986DD9BA2349A92676D6C615B33737B62ADC3C293DEFF445E6AFA92A5CE2D3FBAEC7EC76A6234ABDE1EA2F6AF2EC76DA224A2DB135175F9D63FC9DA6D7EA783D49545E53B15A7BCDD4FB1943E6F253D7E24BE7FCF4B9D3FB761E58623B96A6E70C2F4C545E0E2B314537B72ADC2B
	293D75E1F2F7975F6F42640C718D1B5A1B5B9B1B3D45E1F2467D52EDD3FB3DFD31599B8B13B35A1BE3EA6F4F7D3159AB8E13B35ADBE230C73FE52FD75F292F0036F5C0147715F4425B863F8BA5BCEB18CB0EDD182D3F575C97693E717E1FA6EA23768B35413E798FC47ED6716B8173EF959F8818FF2BB8BF708665038E6DDFCAE07B3782B3FF4B788F8133EF9949F7339942DA8BB98B7285BC0A53AD29E669792B94A45F544253C7E740C39FCD08E53F717CCA40671D47EE1377979592BE57545EBCA904BF57548F
	BB955907CA9C12686B9899618FB5DDDEE988FD2E18081C3F6A89737C55CF18676F62444C796B1A0814BFDA3F4D59D83F8EDE3F776704EAF63E667ED04E97DF3F673FD09B34AE1EF9DB7D769252A8795543AE1B28082C6D5F95728DF658372E6D7014AA70F7D155F98CD32F485693BC578941487D7AE0503CDF9F8A2A6F356400EFC8B2765D6260DE5E96FAA8EED6A4B7F107E29ED6659551B7AA05757DB2D51E36C512FFB29829BEE82C369AED2CA7B95FD5276517F37EE1AB726D0AEC8F65A6E7B2F2EEF6CD1A67
	26F0B2F2EE5E1F0C1E1BC31311F3D3BC99BDB7E513647D5E20F35869E3DF585298B7DBF7E87B7FB4461EE4536664944FC9FF3D8EDF0E7C838DDAFC9947EFECF89DF3B862135D10C56A4F3DD311F398BFE51E437B27A267F05ED4749CAE1A0A1C436FCDC54F61BFCE19677011C6ED6C27F96CB71A3475F306732D4DDAFC083F3DF90786FF62B8FF87582645D7F0FCC73BD6FF486E7E8EAD7EAC672F6B5062C37CA43B2ECF39DD7112969F9AAF451DDAFE28B67ECB478F55402F3B347C50787A16C34B8F45653F2C4B
	43891E37EE1D1F27103FCD4FAFC37E0F2E68741CC47E5F766870DCFF6DD59D1E6B5F532B43F3BD3F52734FA07F3EBE9D3F82790EFE9D7FAC9FD382791E4AA55E12830AA02C1748DA6697703B76A07CCE10689EEE05C43F2524EB7F8FA6C8BD0E78C4B7CCF36A221CBCC2E49725236CA178DD8154839484CA9C41F57F495BBB35F3A5DB93287CC0B64A8B2354EEA13B160D62B7FB7385FCB765GDE7CA87AB47AA1797B9E7ABD216CAEF58E3ACB83B75D256220D2AA4126223232BD60F23532D0857A7D60BAD5578A
	50319EE84BA8FE6398BC0C5FB8AE794EA2E113557CDB4ED447E7167CB16A5F713150BB40956E8D967A4D62E1CBAE13F3C17B62B1757D3325D023037443FD6974D904364F523513F50564A7541E2CEBA77EDE35E85BFF27EB4F524853581E7D88E35B766CD2417523FBA27EAD7CA696D30113DE93B64ECB2F58BD15C1E31B150D31B5B9B136E559C65F67E5E8FDFBA95B685BFD4CF7F69697764E7B6E76BB0FFEEEB97A7E33B51D74FF1C5378DE74324A0FF79A5F0B7E1657DEA234EDF3623BF25FB4295B016BA9F9
	370166GAD81DA8514F69D6B3E7EB3185EE6BE47F7485ED6D818DC1AADA8FB5632FC5E78E8DF7FA4877BFA59635F9712E759A2FE2B3B6DBA6D63FD6374DA74623CF4F22571E529F3C2AB340D814583AD86CA86DABD0E71FD75B376CE1BEC61E147AE8E0D29DFA373878227B491E213C903467615995FE9C76E73CE92863B6F535CEF943E787D7576AF54864D6754F464FD4A17264D77A933274D1FEF3EB6BD73735F5C6948FF4B76503476EF59E8BDDCCCA763F8A3743D60CA49BB84D481546A42DAA83E73A12D85
	13BA289F9EA57BA472977B8A5A5FA5C40E75ADA17A5F79FFA01075EDD0D69459CD74A37F105ADF0A6CD51914F7C49D9BBE676818C2210CFFCCBD17F0C9A3AA1BBD7725C7762BB7BD3ADFC36A3906847A9C373CCC67D8BAD9F36511913F4463F72797F0DE335FAE7B1DE413775FD0CB8788A500A29F1394GGE8C5GGD0CB818294G94G88G88GBCFBB0B6A500A29F1394GGE8C5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4D
	94GGGG
**end of data**/
}
}