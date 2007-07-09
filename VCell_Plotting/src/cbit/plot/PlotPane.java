package cbit.plot;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.*;
import java.awt.*;
/**
 * Insert the type's description here.
 * Creation date: (2/7/2001 4:26:59 AM)
 * @author: Ion Moraru
 */
public class PlotPane extends JPanel {

class LineIcon implements Icon {
		private Paint lineColor;
		private LineIcon(Paint paint) {
			lineColor = paint;
		}
		public int getIconHeight() { return 2; }
		public int getIconWidth() { return 50; }
		public void paintIcon(Component c, Graphics g, int x, int y) {
			 Graphics2D g2 = (Graphics2D)g;
			 g2.setStroke(new BasicStroke(2.0f));
			 g2.setPaint(lineColor);
			 g2.drawLine(x, y, x + getIconWidth(), y);
		}
	}
	private java.awt.event.MouseListener ml = null;
	private Plot2DPanel ivjPlot2DPanel1 = null;
	private JLabel ivjJLabel5 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private org.vcell.util.gui.EnhancedJLabel ivjJLabelLeft = null;
	private org.vcell.util.gui.EnhancedJLabel ivjJLabelRight = null;
	private JLabel ivjJLabelBottom = null;
	private JLabel ivjJLabelTitle = null;
	private Plot2D fieldPlot2D = new Plot2D(null,null, null);
	private JPanel ivjJPanel1 = null;
	private JPanel ivjJPanelData = null;
	private JPanel ivjJPanelPlot = null;
	private Plot2DDataPanel ivjPlot2DDataPanel1 = null;
	private org.vcell.util.gui.ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
	private CardLayout ivjCardLayout1 = null;
	private org.vcell.util.gui.JToolBarToggleButton ivjDataButton = null;
	private org.vcell.util.gui.JToolBarToggleButton ivjPlotButton = null;
	private boolean ivjConnPtoP3Aligning = false;
	private ButtonModel ivjselection1 = null;
	private boolean fieldBCompact = false;
	private JPanel ivjJPanelBottom = null;
	private JPanel ivjJPanelLegend = null;
	private boolean ivjConnPtoP4Aligning = false;
	private Plot2D ivjplot2D1 = null;
	private JLabel ivjJLabelLegendTitle = null;
	private JPanel ivjJPanelPlotLegends = null;
	private BoxLayout ivjJPanelPlotLegendsBoxLayout = null;
	private JScrollPane ivjPlotLegendsScrollPane = null;
	private JLabel ivjBlankLabel = null;
	private JCheckBox ivjJCheckBox_stepLike = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PlotPane.this.getJCheckBox_stepLike()) 
				connEtoC5(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == PlotPane.this && (evt.getPropertyName().equals("plot2D"))) 
				connEtoM1(evt);
			if (evt.getSource() == PlotPane.this && (evt.getPropertyName().equals("plot2D"))) 
				connEtoM2(evt);
			if (evt.getSource() == PlotPane.this.getButtonGroupCivilized1() && (evt.getPropertyName().equals("selection"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == PlotPane.this && (evt.getPropertyName().equals("bCompact"))) 
				connEtoC3(evt);
			if (evt.getSource() == PlotPane.this && (evt.getPropertyName().equals("plot2D"))) 
				connPtoP4SetTarget();
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == PlotPane.this.getplot2D1()) 
				connEtoC2(e);
		};
	};

public PlotPane() {
	super();
	initialize();
}

/**
 * connEtoC1:  ( (PlotPane,plot2DData --> Plot2DPanel1,plot2DData).normalResult --> PlotPane.updateLabels()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.updateLabels();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (plot2D1.change.stateChanged(javax.swing.event.ChangeEvent) --> PlotPane.updateLabels()V)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateLabels();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (PlotPane.bCompact --> PlotPane.plotPane_BCompact(Z)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.plotPane_BCompact(this.getBCompact());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (selection1.this --> PlotPane.selection1_This(Ljavax.swing.ButtonModel;)V)
 * @param value javax.swing.ButtonModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.ButtonModel value) {
	try {
		// user code begin {1}
		// user code end
		this.selection1_This(getselection1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JCheckBox_stepLike.action.actionPerformed(java.awt.event.ActionEvent) --> PlotPane.jCheckBox_stepLike_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBox_stepLike_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (PlotPane.plot2DData --> Plot2DPanel1.plot2DData)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getPlot2DPanel1().setPlot2D(this.getPlot2D());
		connEtoC1();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM2:  (PlotPane.plot2D --> Plot2DDataPanel1.plot2D)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getPlot2DDataPanel1().setPlot2D(this.getPlot2D());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (PlotPane.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getPlotButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (PlotPane.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getDataButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (selection1.this --> CardLayout1.show(Ljava.awt.Container;Ljava.lang.String;)V)
 * @param value javax.swing.ButtonModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(javax.swing.ButtonModel value) {
	try {
		// user code begin {1}
		// user code end
		if ((getselection1() != null)) {
			getCardLayout1().show(getJPanel1(), getselection1().getActionCommand());
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
 * connPtoP1SetTarget:  (JLabel3.this <--> Plot2DPanel1.statusLabel)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getPlot2DPanel1().setStatusLabel(getJLabel5());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetSource:  (JPanel1.layout <--> CardLayout1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if ((getCardLayout1() != null)) {
			getJPanel1().setLayout(getCardLayout1());
		}
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (CardLayout1.this <--> JPanel1.layout)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		setCardLayout1((java.awt.CardLayout)getJPanel1().getLayout());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP3SetSource:  (ButtonGroupCivilized1.selection <--> selection1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getselection1() != null)) {
				getButtonGroupCivilized1().setSelection(getselection1());
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
 * connPtoP3SetTarget:  (ButtonGroupCivilized1.selection <--> selection1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setselection1(getButtonGroupCivilized1().getSelection());
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
 * connPtoP4SetSource:  (PlotPane.plot2D <--> plot2D1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getplot2D1() != null)) {
				this.setPlot2D(getplot2D1());
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
 * connPtoP4SetTarget:  (PlotPane.plot2D <--> plot2D1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			setplot2D1(this.getPlot2D());
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
 * Insert the method's description here.
 * Creation date: (12/16/2004 3:00:42 PM)
 * @param range cbit.util.Range
 */
public void forceXYRange(org.vcell.util.Range xRange,org.vcell.util.Range yRange) {

	if(xRange == null){
		getPlot2DPanel1().setXAuto(true);
		getPlot2DPanel1().setXStretch(false);
	}else{
		getPlot2DPanel1().setXAuto(false);
		getPlot2DPanel1().setXManualRange(xRange);
		getPlot2DPanel1().setXStretch(true);
	}

	if(yRange == null){
		getPlot2DPanel1().setYAuto(true);
		getPlot2DPanel1().setYStretch(false);
	}else{
		getPlot2DPanel1().setYAuto(false);
		getPlot2DPanel1().setYManualRange(yRange);
		getPlot2DPanel1().setYStretch(true);
	}
	
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
 * Return the BlankLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBlankLabel() {
	if (ivjBlankLabel == null) {
		try {
			ivjBlankLabel = new javax.swing.JLabel();
			ivjBlankLabel.setName("BlankLabel");
			ivjBlankLabel.setPreferredSize(new java.awt.Dimension(24, 55));
			ivjBlankLabel.setText("        ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBlankLabel;
}

/**
 * Return the ButtonGroupCivilized1 property value.
 * @return cbit.gui.ButtonGroupCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.ButtonGroupCivilized getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new org.vcell.util.gui.ButtonGroupCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupCivilized1;
}


/**
 * Return the CardLayout1 property value.
 * @return java.awt.CardLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.CardLayout getCardLayout1() {
	// user code begin {1}
	// user code end
	return ivjCardLayout1;
}

/**
 * Return the DataButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JToolBarToggleButton getDataButton() {
	if (ivjDataButton == null) {
		try {
			ivjDataButton = new org.vcell.util.gui.JToolBarToggleButton();
			ivjDataButton.setName("DataButton");
			ivjDataButton.setToolTipText("Show data");
			ivjDataButton.setText("");
			ivjDataButton.setMaximumSize(new java.awt.Dimension(28, 28));
			ivjDataButton.setActionCommand("JPanelData");
			ivjDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/data_sets_20x20.gif")));
			ivjDataButton.setPreferredSize(new java.awt.Dimension(28, 28));
			ivjDataButton.setMinimumSize(new java.awt.Dimension(28, 28));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDataButton;
}

/**
 * Return the JCheckBox_stepLike property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBox_stepLike() {
	if (ivjJCheckBox_stepLike == null) {
		try {
			ivjJCheckBox_stepLike = new javax.swing.JCheckBox();
			ivjJCheckBox_stepLike.setName("JCheckBox_stepLike");
			ivjJCheckBox_stepLike.setText("Step Mode");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBox_stepLike;
}


/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel5() {
	if (ivjJLabel5 == null) {
		try {
			ivjJLabel5 = new javax.swing.JLabel();
			ivjJLabel5.setName("JLabel5");
			ivjJLabel5.setText(" ");
			ivjJLabel5.setForeground(java.awt.Color.blue);
			ivjJLabel5.setPreferredSize(new java.awt.Dimension(44, 20));
			ivjJLabel5.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabel5.setMinimumSize(new java.awt.Dimension(44, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel5;
}

/**
 * Return the JLabelBottom property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelBottom() {
	if (ivjJLabelBottom == null) {
		try {
			ivjJLabelBottom = new javax.swing.JLabel();
			ivjJLabelBottom.setName("JLabelBottom");
			ivjJLabelBottom.setText("JLabel1");
			ivjJLabelBottom.setForeground(java.awt.Color.black);
			ivjJLabelBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelBottom.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelBottom;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.EnhancedJLabel getJLabelLeft() {
	if (ivjJLabelLeft == null) {
		try {
			ivjJLabelLeft = new org.vcell.util.gui.EnhancedJLabel();
			ivjJLabelLeft.setName("JLabelLeft");
			ivjJLabelLeft.setText("JLabel2");
			ivjJLabelLeft.setForeground(java.awt.Color.black);
			ivjJLabelLeft.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelLeft.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabelLeft.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabelLeft.setVertical(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLeft;
}

/**
 * Return the JLabelPlots property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLegendTitle() {
	if (ivjJLabelLegendTitle == null) {
		try {
			org.vcell.util.gui.EmptyBorderBean ivjLocalBorder;
			ivjLocalBorder = new org.vcell.util.gui.EmptyBorderBean();
			ivjLocalBorder.setInsets(new java.awt.Insets(10, 4, 10, 4));
			ivjJLabelLegendTitle = new javax.swing.JLabel();
			ivjJLabelLegendTitle.setName("JLabelLegendTitle");
			ivjJLabelLegendTitle.setBorder(ivjLocalBorder);
			ivjJLabelLegendTitle.setText("Plot Legend:");
			ivjJLabelLegendTitle.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLegendTitle;
}

/**
 * Return the JLabelRight property value.
 * @return cbit.gui.EnhancedJLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.EnhancedJLabel getJLabelRight() {
	if (ivjJLabelRight == null) {
		try {
			ivjJLabelRight = new org.vcell.util.gui.EnhancedJLabel();
			ivjJLabelRight.setName("JLabelRight");
			ivjJLabelRight.setText("JLabel4");
			ivjJLabelRight.setForeground(java.awt.Color.black);
			ivjJLabelRight.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelRight.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
			ivjJLabelRight.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabelRight.setVertical(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRight;
}

/**
 * Return the JTextFieldTitle property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTitle() {
	if (ivjJLabelTitle == null) {
		try {
			ivjJLabelTitle = new javax.swing.JLabel();
			ivjJLabelTitle.setName("JLabelTitle");
			ivjJLabelTitle.setText("Title");
			ivjJLabelTitle.setForeground(java.awt.Color.black);
			ivjJLabelTitle.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTitle;
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
			ivjJPanel1.setLayout(new java.awt.CardLayout());
			getJPanel1().add(getJPanelPlot(), getJPanelPlot().getName());
			getJPanel1().add(getJPanelData(), getJPanelData().getName());
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
 * Return the JPanelBottom property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelBottom() {
	if (ivjJPanelBottom == null) {
		try {
			ivjJPanelBottom = new javax.swing.JPanel();
			ivjJPanelBottom.setName("JPanelBottom");
			ivjJPanelBottom.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
			constraintsJLabel5.gridx = 1; constraintsJLabel5.gridy = 0;
			constraintsJLabel5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabel5.weightx = 1.0;
			constraintsJLabel5.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelBottom().add(getJLabel5(), constraintsJLabel5);

			java.awt.GridBagConstraints constraintsPlotButton = new java.awt.GridBagConstraints();
			constraintsPlotButton.gridx = 2; constraintsPlotButton.gridy = 0;
			constraintsPlotButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelBottom().add(getPlotButton(), constraintsPlotButton);

			java.awt.GridBagConstraints constraintsDataButton = new java.awt.GridBagConstraints();
			constraintsDataButton.gridx = 3; constraintsDataButton.gridy = 0;
			constraintsDataButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelBottom().add(getDataButton(), constraintsDataButton);

			java.awt.GridBagConstraints constraintsJCheckBox_stepLike = new java.awt.GridBagConstraints();
			constraintsJCheckBox_stepLike.gridx = 0; constraintsJCheckBox_stepLike.gridy = 0;
			constraintsJCheckBox_stepLike.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelBottom().add(getJCheckBox_stepLike(), constraintsJCheckBox_stepLike);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelBottom;
}

/**
 * Return the JPanelData property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelData() {
	if (ivjJPanelData == null) {
		try {
			ivjJPanelData = new javax.swing.JPanel();
			ivjJPanelData.setName("JPanelData");
			ivjJPanelData.setLayout(new java.awt.BorderLayout());
			getJPanelData().add(getPlot2DDataPanel1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelData;
}


/**
 * Return the JPanelLegend property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelLegend() {
	if (ivjJPanelLegend == null) {
		try {
			ivjJPanelLegend = new javax.swing.JPanel();
			ivjJPanelLegend.setName("JPanelLegend");
			ivjJPanelLegend.setLayout(new java.awt.BorderLayout());
			getJPanelLegend().add(getBlankLabel(), "South");
			getJPanelLegend().add(getJLabelLegendTitle(), "North");
			getJPanelLegend().add(getPlotLegendsScrollPane(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelLegend;
}

/**
 * Return the JPanelPlot property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelPlot() {
	if (ivjJPanelPlot == null) {
		try {
			ivjJPanelPlot = new javax.swing.JPanel();
			ivjJPanelPlot.setName("JPanelPlot");
			ivjJPanelPlot.setLayout(new java.awt.BorderLayout());
			getJPanelPlot().add(getJLabelLeft(), "West");
			getJPanelPlot().add(getPlot2DPanel1(), "Center");
			getJPanelPlot().add(getJLabelRight(), "East");
			getJPanelPlot().add(getJLabelBottom(), "South");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelPlot;
}


/**
 * Return the JPanelPlotLegends property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelPlotLegends() {
	if (ivjJPanelPlotLegends == null) {
		try {
			ivjJPanelPlotLegends = new javax.swing.JPanel();
			ivjJPanelPlotLegends.setName("JPanelPlotLegends");
			ivjJPanelPlotLegends.setLayout(getJPanelPlotLegendsBoxLayout());
			ivjJPanelPlotLegends.setBounds(0, 0, 72, 360);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelPlotLegends;
}

/**
 * Return the JPanelPlotLegendsBoxLayout property value.
 * @return javax.swing.BoxLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.BoxLayout getJPanelPlotLegendsBoxLayout() {
	javax.swing.BoxLayout ivjJPanelPlotLegendsBoxLayout = null;
	try {
		/* Create part */
		ivjJPanelPlotLegendsBoxLayout = new javax.swing.BoxLayout(getJPanelPlotLegends(), javax.swing.BoxLayout.Y_AXIS);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelPlotLegendsBoxLayout;
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
 * Return the Plot2DDataPanel1 property value.
 * @return cbit.plot.Plot2DDataPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Plot2DDataPanel getPlot2DDataPanel1() {
	if (ivjPlot2DDataPanel1 == null) {
		try {
			ivjPlot2DDataPanel1 = new cbit.plot.Plot2DDataPanel();
			ivjPlot2DDataPanel1.setName("Plot2DDataPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlot2DDataPanel1;
}


/**
 * Return the Plot2DPanel1 property value.
 * @return cbit.plot.Plot2DPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Plot2DPanel getPlot2DPanel1() {
	if (ivjPlot2DPanel1 == null) {
		try {
			ivjPlot2DPanel1 = new cbit.plot.Plot2DPanel();
			ivjPlot2DPanel1.setName("Plot2DPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlot2DPanel1;
}

/**
 * Return the PlotButton property value.
 * @return cbit.gui.JToolBarToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JToolBarToggleButton getPlotButton() {
	if (ivjPlotButton == null) {
		try {
			ivjPlotButton = new org.vcell.util.gui.JToolBarToggleButton();
			ivjPlotButton.setName("PlotButton");
			ivjPlotButton.setToolTipText("Show plot(s)");
			ivjPlotButton.setText("");
			ivjPlotButton.setMaximumSize(new java.awt.Dimension(28, 28));
			ivjPlotButton.setActionCommand("JPanelPlot");
			ivjPlotButton.setSelected(true);
			ivjPlotButton.setPreferredSize(new java.awt.Dimension(28, 28));
			ivjPlotButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/data_exporter_20x20.gif")));
			ivjPlotButton.setMinimumSize(new java.awt.Dimension(28, 28));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlotButton;
}

/**
 * Return the PlotLegendsScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getPlotLegendsScrollPane() {
	if (ivjPlotLegendsScrollPane == null) {
		try {
			ivjPlotLegendsScrollPane = new javax.swing.JScrollPane();
			ivjPlotLegendsScrollPane.setName("PlotLegendsScrollPane");
			ivjPlotLegendsScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ivjPlotLegendsScrollPane.setBorder(new org.vcell.util.gui.EmptyBorderBean());
			getPlotLegendsScrollPane().setViewportView(getJPanelPlotLegends());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlotLegendsScrollPane;
}

/**
 * Return the selection1 property value.
 * @return javax.swing.ButtonModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonModel getselection1() {
	// user code begin {1}
	// user code end
	return ivjselection1;
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
	this.addPropertyChangeListener(ivjEventHandler);
	getButtonGroupCivilized1().addPropertyChangeListener(ivjEventHandler);
	getJCheckBox_stepLike().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
	connPtoP4SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PlotPane");
		setPreferredSize(new java.awt.Dimension(420, 400));
		setLayout(new java.awt.BorderLayout());
		setSize(513, 457);
		add(getJLabelTitle(), "North");
		add(getJPanel1(), "Center");
		add(getJPanelBottom(), "South");
		add(getJPanelLegend(), "East");
		initConnections();
		connEtoM4();
		connEtoM3();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void jCheckBox_stepLike_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getPlot2DPanel1().setBStepMode(getJCheckBox_stepLike().isSelected());
	getPlot2DPanel1().repaint();
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		PlotPane aPlotPane;
		aPlotPane = new PlotPane();
		frame.setContentPane(aPlotPane);
		frame.setSize(aPlotPane.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		aPlotPane.setPlot2D(Plot2DPanel.getSamplePlot2D());
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private void plotPane_BCompact(boolean arg1) {
	getJPanelLegend().setVisible(!getBCompact());
	getPlot2DPanel1().setBCompact(getBCompact());
}


/**
 * Comment
 */
private void selection1_This(javax.swing.ButtonModel arg1) {
	getJPanelLegend().setVisible(getJPanelPlot().isVisible() && !getBCompact());
}


/**
 * Sets the bCompact property (boolean) value.
 * @param bCompact The new value for the property.
 * @see #getBCompact
 */
public void setBCompact(boolean bCompact) {
	getJLabelRight().setVisible(!bCompact);
	boolean oldValue = fieldBCompact;
	fieldBCompact = bCompact;
	firePropertyChange("bCompact", new Boolean(oldValue), new Boolean(bCompact));
}


/**
 * Set the CardLayout1 to a new value.
 * @param newValue java.awt.CardLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setCardLayout1(java.awt.CardLayout newValue) {
	if (ivjCardLayout1 != newValue) {
		try {
			ivjCardLayout1 = newValue;
			connPtoP2SetSource();
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
			connPtoP4SetSource();
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
 * Set the selection1 to a new value.
 * @param newValue javax.swing.ButtonModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselection1(javax.swing.ButtonModel newValue) {
	if (ivjselection1 != newValue) {
		try {
			ivjselection1 = newValue;
			connEtoM5(ivjselection1);
			connPtoP3SetSource();
			connEtoC4(ivjselection1);
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
private void updateLabels() {
	if (getPlot2D() == null) return;
	getJLabelTitle().setText(getPlot2D().getTitle());
	getJLabelRight().setText(" ");
	getJLabelLeft().setText(getPlot2D().getYLabel());
	getJLabelBottom().setText(getPlot2D().getXLabel());
	updateLegend();
}


/**
 * Comment
 */
private void updateLegend() {
	Plot2D plot = getPlot2DPanel1().getPlot2D();
	String[] plotLabels = plot.getVisiblePlotColumnTitles();
	int[] plotIndices = plot.getVisiblePlotIndices();
	Component[] legends = getJPanelPlotLegends().getComponents();
	// add legends if necessarry
	if (ml == null) {
		ml = new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				String name = ((JLabel)evt.getSource()).getText();
				getPlot2DPanel1().setCurrentPlot(name);
			}
		};
	}
	for (int i = 0; i < plotIndices.length - legends.length / 2; i++){
		JLabel line = new JLabel();
		JLabel text = new JLabel();
		line.setBorder(new org.vcell.util.gui.EmptyBorderBean(6,0,0,0));
		text.setBorder(new org.vcell.util.gui.EmptyBorderBean(0,8,6,0));
		getJPanelPlotLegends().add(line);
		getJPanelPlotLegends().add(text);
		text.addMouseListener(ml);
	}
	legends = getJPanelPlotLegends().getComponents();
	// update labels and show them
	for (int i = 0; i < plotIndices.length; i++){
		LineIcon icon = new LineIcon(getPlot2DPanel1().getPlotPaint(plotIndices[i]));
		String plotLabel = null;
		if (plot instanceof SingleXPlot2D) {
			plotLabel = plotLabels[i + 1];
		} else {
			plotLabel = plotLabels[2 * i + 1];
		}
		((JLabel)legends[2 * i]).setIcon(icon);
		((JLabel)legends[2 * i + 1]).setText(plotLabel);
		legends[2 * i].setVisible(true);
		legends[2 * i + 1].setVisible(true);
	}
	// if extra ones, hide them
	for (int i = 2 * plotIndices.length; i < legends.length; i++){
		legends[i].setVisible(false);
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G42DAB1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8FDCD5D536B0159615950EB411D1111A03C50DCD78E74AA6B3AAADEAEC9EFDB9246F5944B44EB7B44F6F653C674CB8F538000210E162C80AF6D5D2ECF88808AFB2CB91D4B0D492B2B4D2AE5C837742655E6B3D87387CD13F357F2CFD8F671E4BC5E61C5F73576A1C3D765AFB2F35765AFB2F3D4E5E97A97B230854A4C336A4A525CA7C5F27C912D4FF3DA4F9ACF55B90377C6B78F4C975EFAB40DAE9
	7977A970CCBB951F2E3C9A1F1EA53DD3168669DCC86F1F9F1F3E815E57C9491FE5DF0297BF6DA4DE1424D5AF07FEBDF4BBF356E1BB2BE87AC2F5A6BC57G9881C71E36D3247D9B2BF3B83E126391128C12D4D4C7E8A6D567F25CD420198B1088A055B17A9CF8B286C66FAB63F40DE3A325E54F3B8C6F11FAD4F5A2F8F859F07EB4CAFFCA2A2CA7ED3DC879A4E562A0AF93F0C478E4690D13044F7D359DEDDF36B4DB15C2274D216C5BD78B7F2FB5596559346C7AE3E914D61202A1DD738D419994D2DF8A3C678214
	FE0378473A89BE845EA381E6FF03B2FC31F3BF673121FF06B4B0EDEF467BFEE4D8708D63DDBA6D15E156DD8B8D9FD2FD9B56D3FEA12F89C0818883188930GE0936D67746CCF60D95E2CD4B8BABB9D76F22773104D6CBA6EB04BB6F85F378FF2B86E145C62F019215FE95D7FD2DD20434FD0404A3DFE456518CEA277E03FC61DFAD8DA78688F8D2BF4F432B0325F30146BA26DDB2FCED85B9B0F79EBBB7F5B41F6B7386DEFA5F55B2583F14929BAED57DA63127773FAEAD5ED07FCCB7A3A1B5A00935EC9398C526F
	94BF0A6263D4F8E603E994CF524BA02F2181ED23E18D72D213B2C14AAA3EC3D78FE1F34698E6B5B09E629A343C4C69A1F5CBE7D81B9F8BDE96D1FC3D8A4FF819CD6D1424A3A1AF6EECFCBA332F0C8DE4CCCA0DE057G25GB5GB600E00070C63431D3593B07E9E335A617F2284BEAB34BAE09154DD87FD7F84A9E45E52A363B95133DC5BEEDF2D9CD4DB659ADF11A6C75C4874D034AFDC8ED3F855AB8AA5BE517C931BA6CD03B29D3D6E4171B51ECDB3F165038E41ADBEF6D142101CE270475FE32FE9DBCEDA637
	720D53ECD2E4AF854BAFD9CF7A245BF49AEA048A601D6925AC0768EB96489F8710A07470902D6FAB59E517ED7B7655B89C9DDD4EFAA209D47ABD51F3B11DBB1683FD56772863528F894E8369106F311E04759F286A2934369076CD2EDE9CE3838ACC5852A4201F893090E099C066B934215FB7208DD53E7710D4704A6D3AB6948376ECBC47EC276C1CF6AC5DC45BA0FCB6C1DE90C084406CF3B8572FD19A06B13689FF6A74A44E2F16EE9947E5F2BED89BBB0755C62AA61D4C4B6B0D75B5FC4C2D91FDB9E1A3694B
	45506E8A005C7358879F7D5548EC7A1049AD23951FF401814AE6EF0FB0BE6776D4F17DA6F3FEC8DB6F71365E27B424CEC9DA8F300137CD5AA16B68F6000FG7682FCAAE4FF0356D98A7CB4820C8208820883D881105784E391C08188870881C88448B6419A8FE0869883908D305004FD76EC37B1E9B01F28178C4E6FD03C2E61FCA24F017A856539123560FF83B0DD2D6DA13AC2BD21BD8D37DF57F0BDED66BA8B54A65167477F8BE43FD2E0E3E57D86228B39DB36AB1532D3361B61454D73F2B7901D71793D55A1
	4AEC23F8A72C8795B0813B85BEFF835129C9D1DC5666AEC52E139545EAEF73669751365CDD4DCE6FD202EDFD3A01741523875683C13FFF8319CBDB9CF63BCC872DA842733F24F339EC13BB01698AC717DDE1784A9C625BF6483DFCAD294AD9A911354056C556A566AFCEB591BBF03A2C1DB0951025CD2AEFA673FC55319C150ED2GC77C5EFCF86EEF46B93AE7BB4E51F9FDD32459671D86BD1FA36C7E5B8C614DECCE1C5A6C1D2399CF1FFD284F7B813A26F03C1B71FA6C944121AF1BCC69566DA0F6FB28CBD19C
	7623AEC71733425AED35D97BE4F33144ECFF1D74F99D11314264B25718FA9DDDCAB1470F6BDBCD7A819A21EAADC67A8F3B49D8A11EF3C9E5B11FE3DF5BC4781C8F7CA7G2CE846B976718FC9BE2EF3FCF22DF058F81D10D759C27479B2DD0BD0A759ADCC1F64BD16667FC5640F0174A41FFCF79766E71B63538B4C5A7C8D5D183F9F726AD5790C4F480DD9D4568BDDB238A7E4C17FCA58450FB7FE2049BB4DED04654F58C874E7D37982CB4C244D0FBD385FB1C3FAGA09CA08AE0BE4096A20BCC687EA068CAE546
	D70D4C681AE446E3DEAB21FB3897699648EC4DD84A6953DA999D2B2F368F691C2DACBF981E61G338751997B11AE0E53A536B2DFA025CDCD97AF68CA5B7469A635913ADB87102E260D2DE1E6F8AAG21G05022E71A252ADEDE36B9D21CB33F869187E36DDC23AFC48BB88E036B0BF45E3D16B6F7F893AE08B73E1BC1CAE4222666F7E4BC83750425654E53CDEC26FED57A1A1DD0A154919EB75563BC55437B7C968454A6A2B61F40DD6D69F6B3715862473D8197EC278F312D55D3FCF8A3A393C1E451CEEA927E3
	7C75A48B3D34337C036D4CEF2BEFE77C317A4AD3047D717C017641764268D2D311EE96644781AC63F4259D0C0E4DAFEF6F92FB67F6D33749D3686E01F9FB5F3EE3E43A31313D73CF287FCB6607D19D38C7FE56C2FDE9C84781A4F6607C7872FB38C78E6D081432AE7ECB323FFDFE0A0D7111EF734E0F8935DD49EC6E79F8B3E95382F99E9B4EBBA51B54FEFA395B6DE83152AD829F332FEFD24F1F8C7766A6B25F35DADDEE259A96B88F0E6F44CD04DF320760E82E0F495B37CB6AD8C2DB17F55FBE3D29166FC1AF
	9ED14BBB6B7D0B068CBF720E61F2C62964B5277DA1196D65723AB036118879F91D985BC85B74213AFF8ACDBD4A3EFD5EE9DD423DF04B9E64A3246FFE29697671FE79E86CE46D8FF4FA7998F5639E4E47F84A87D99322A1BF91A04FCE702FF611F5150C99C677E5B752D9A0BF420EF47D5D040E58AE23BB61C13A85A45F81F8BAF79F4DD02F8DD500378104G3075E81A075423D82CEE36B61F9FD02F4D1300AE96E0A9C00A934BAC729019E1A728F439EBCC4D320D6B66BF4A8F72F2318FCF10E62E9AE3500B5BD8
	FE94E648F5B21DD4BA3D3A89DF3B48B09500493325B7135BCAA850440A36C77792BF032D1FE8CBF5A5E45C3AE525162273EBC4F42A10E1BE5FF59A7B6F21ADEA9E53CC23F5F99C735BB6C34A8546DB4185AF0FB9490B8C854948632B02C785E822AE20ADFD79B5415B9D2ECE136D146C6E32A98C9FDEC95A6A22FBE92AB7F7BEB046781A30CE4D577E4B67F4798AFE5FE2C8607CE43A3CFCCDB5ACB24498102F5EBE642B81E8C2DC283B45FDEA5D317E5E3ACA5D5FF301F699C00E9B7CGB716CB6FD717E3EDD477
	233DD5825D00201DCA71D63BD5311A48784D5F45E4AF3213202C49EC4E2FD14DFF05654DEE70FCDB94B672E9BF317A13874478003A935DE867B384BECF0135C241F637DCA4F6ECF23595B33BFF7DD3355D07AA4C6EE3847D110BE877CC7F83E7D07FC13151D24E3ADBF475CF7D8705479C3BD473E956A24372AC547F680B48E3AD5084F761D81C532947536CAE146D2BCBD8AE8EF0195D30A651BE7A1DCB6BAF1338C3A564B7F423BCBF39C47A4846A7AB06FB6D924A486A5FF8996D428965A23B51DE8FE77CB799
	BF96C78F6B9CB27995D6B86C0A498ADB054F994EE6026E2A03ED073D0DF6936F2717AEA34F0B213E1C9E64E72FA47A035205C9C2A7C093AC685E6ACA17D4FE322877DC92524F865A65027E19A4DEEF89AF1FC456BD93ADCC42EBA63BD954315B007296F96253AD9E2C63078632FEB58339938AB29DEC785BFAEE63E4C1BEFF08E7661FD5493AC4549781F5AD90751DCCC6BD333A662C7E1BDA2662737510B5586E193D5390A051A62BBFA299E54E6A0DCF2F6A457A47F62B479F437DADE5F05F3627A0EFC1D0EE26
	A87BF2CA89BCDBAC50FBF2A11932658A7AEE4ED733C3772605AC67B0F95F05B29726E23DCB214E7CBE34652F5A54365C5447ECB9388F5B3DBD35C474CBA1FB544A2ED61893F9535448D83B65B49B5BE5BAFC57DE8E4F768A0B5C52F14861B96FD6E4E70D35C3BEDF2E292B46FFE54CCED93CE20608FD0777717DE21FB74E9623FAC7E0B16125D6A27F02BE9693CE97783FD07CB2951E69AB167AEBA41D558F7EFBBF46B18F9A4998DEG69E5G1983300E83D4821887506FD2F277C881624455F6855CB2132DEE50
	869F63351B096EE1CF4E53051B09AE691EDC42F85E43DBC93F85C11B61G51G31G090207E91B8372F058234B835373A7DDA37BAED2F64565F88CFEBB11A773A258A640C10086G4FC514675D4D85016441F8BA97047193C179893B287FDD443F9C8F53F2D32F209CDBFFF61DD5FF37CBD9FB4CC07AD2EF7DD94C6BDEFAD19B132E6C9E994F273A2F14E7D62E5E93284F0AAE8D2E1315EB8ED82EDED30E6F21DA7D6C2128B34556CAFB1A5EDAE9037A02G58BE6BD286197F493A91F6895740773F5BA469FF5BAC
	2924DBB46EAF6D685156CF7640AB201E2C4BD87F9D83788DB47FB26E635E21FECB9324BDGA117F1DEF8A80F6C9FA3A19D833090E0B9C0EED0FCFA99C0E3905A7171AD078259F1B986856B9D6235C29B5843BE486B55760192B4D8677AFDD762D3EE0BA69D2229074939ED87159372E2G9682AC87483D866484E83C86653CF74B67016424F3BB484857FC2D000C4FC5A3146FF7BE658AB56901EB74643BF3A711AF947226824C85C8GC83B9666F9002AEBD13E25DB3E9826FC95002BF2B8BAF43A1073FA39EFE4
	B25E62D3CE5B0716EB75465F6EAD9F799DA315A645249EA736943DF192847506DE0B63644CAA9CA7D15762B8F9FC9599A78910DE8110FD9DFE1BB959BF3239667B00658A2E53AB17BEB03276B684ACD7295B5E93A3EC6F0500659A2F536B4BBF666844DF0E55BB9C36C3A6D73D232D0D2C0744C715D8DF6550B3B2A4AE24DC07F357073B084DAF0074B2004CD130868354GD8G460CC21B2F78A8605CA5BECD8FE76E3A796248F4F5E740F213C68DAE47E47FE3E71A443EE54D9A05767875251171F0BEE0B963E8
	BD9E363B53F85F150D4631622031A1A7240381A2C6A3EF464BA31B978AFD4A95EA52B3C76B150B73A9B7BCDD7CAAE039793A6D5DA30D2C3D280065166836E7CE9AD9FB2E0065246B754A6DB50C2C7FAAFC4AE967759C5D76FEBF4276D684EC2FC8373D4949A353E7F440F2D53A6DB9D3C6A65FA51FF2051AF413EEFB2529A3EB6F10CFB92DBEBD576B4D65DF1E963E6BE03F98F74FFC8E39368D67109028071D39F909FEF3188B6984011BAE53F3B8104E3C81F163E859A2C8D75D00734CFFDA49F7BF9B24C78104
	81CC95F9496DA4EF6E8DEC5DA73101A5A26F9796F2EE05506A75A1294B66A78FA1EC08FC6E4751B6527D50B03D8DEDCBFAE7E490583AD6ED747FDD3F56287FDD7F34D17F3BFE23F1AD43C39D6AD3E018EFB68E7D5D5FE6747F5DFF7AD67FFCBE31D51F4F27376A73B9EF6B50FC3E3CF5E8BE57ED774F4B466D7A3C98376B7332E37B503C94EE77474BE65A7F7F074673DCDDF6455AA9532A19DD489EF2B6853C12D3DDF66632105A5D5AF801043E7F371F10D8DCE6B07ABB4FECAF11FC7D1D43E62B62F0E1A57532
	49F53235D5B8BBF5CE3985797A0DC2E211BEE7B2CE0007A4E17C6FE6CFBA2729D59C3564BC829B631353F00C6787231FB0E6B55DFBC25A8390920C63703A8C3D73E311415E31B5AB58F71C4D87DC02863F9C5219B76A79AD67FA109F630D484F8E7A5D248152CE00609B111F70A1FD59089BFDF9E131B8970D37C75F48E2F1C9825FCF7133D5F89623CB39085FGE3A12F68A66C3799F97EED73E7F97A369913C76288EEC7172BC5565A5F8BF9B9A24F6B4CD6ABF2A769CAC693C74BABA6D71B6CE33F2F5172AC4F
	DF7904BC72AD2E59EA363AF8841D59503C3F3ECF47065BAD3B690612EC50386DFC570B36F3D8F19CAF966D956C70AF7F1E9D7A72FF36433F7C87F784163FFA07FF79EBF784167F4C8EFFE31C491A5D27123542AB6B441D7EE5FDE0273E2CD3F77A17F5464E40324E5E69DF5667F68616F57E4E21E51D2E1675F809980313D2F08C065E04E35036120C01D8C8A7G2C3889476039D5C30D416C1B7D0D415DE9243E7C1B35E3300C62CBEF560E4127D66198BC88F913EE466F8657772BE5A895329D9CD06377887C32
	018C0E2FEDF5B9BAE1A6E4F2ECAEA3F3DB5A18417C6672F491FFD62A729B605D869024A9938A69C88D0E754776DD7EED6813DD7AB6D4344B3F8DFD3AAB308D9D5C655F060E6E8AECC3353B34738553631C0B0375D8427578CEA75163AC0D7C73F9BA01BF172B72B3EF09CFB75E62EBA725002B5260D95B411754EB9931DB56F63EDBEB0B0D2A72CE1DB6184D3DC1774523EEE1B617AA703F2778B0951E59E2D7B7FE630A023C2CDB51963F3D2C36393D025F8CE9302E76F07CAB12DA8E2F0D5E15B4183E1463ED86
	755C64257FB8D90D770E01C5496A7A3D789F252869CB843EA3D50D5FAB74FAC31B5E98D7EEE57A8C3E55D72F2C3EDF9FA33667F4492D324BA51B6B2CFD449F093B15463769BEA29C5EA3057EDE14555F24C75DC6688E35E2DCB08752C5G8DG365BD81C1E79DF0F7490FD353ACC1DB03DD14728A90414FD208D4B46827DE2G8970C60003A15866ABAD24AC7FA2E0D540CBE3787D75C4F6F65A8A4FE8AEB81EA9F0079C0A626864FA7E2B97DFA3374140E65FBCBFED571E25A1BCC70660F77AEE2B5E3707D910BF
	BF84FD0C6FBE215F2BF18E67BE9D6B178753347D32A4445B97ABC27C593B14C1784D9A2B7DC6799E45970C55FE237C41EA7CC6D985F9D1E3F10FF7BF7DAE12758350698F9037BA13608AA0DDA9F04A9902EB0034A2F0AF51EF6A41108E9338AD4938E70C9238FF7B0C60D6C0BAFB9C2EB9ADBDDA397347F9E55DBF4E1F5C63299FD9BBCEAB770FA85EB6CEABF7C1AF4A9D8CF909631027C787104F5AD06449F3D1927DF20D6AFD7545615C3998A930B6367F935A1862935B9888756AB9A4541F4E97D33FE1D2A853
	ED32407FDF0A0FD661194EDDAB71CCC38C64655F0EBA6FDA05BA9FF3BB6AFCEF1AD74FDF2A5E1FDBF5757531782A3641E4BEC0E368B3C15E79C28F13A9EEA1249385AE030E1165104E7A2150D7B14165C17A2040AD22389AC8B7895C0E9202D3A09DA2F0AF52BD61A4C8C78B5C4FAF207E678A5C859B41655C8173729D08B35A1167CA013B460EBC578A5C2F6C48F32340F9CC4833D3604E37A1AF838277D31B60D9604E08F853A401FB57027CAD933883B4AE1E96961F1E9B06383CEEACFBB08CED6B3794E70374
	A801DBC6F164FCA3B97346F07D5D448E880D3F3E348D11B7BCDB8B25EDE86FE529A1D0ECAA302D5DC375978E3CC6893DA4DBD0AFB38117F6A739337CA55992717570C15988CF3B5CE394E7EAAA2D1D321DDC6E4BAFADA9AAB01696956DE26795A2FA5467954AD6DF2BFBBF03C04D1DCCDFCD7C39E27DF8FE4F383D9577GE110B77FCEDC7F5FAC9C944FE4E7F18FB9487D87FE9A171F17EECD63655346633EE6B12D73A0249BG1C63713C34E7871E5B0D1ABCC1CA6955BFAF3D387E22A1F8BCBFBFBC5EFB3EE27E
	DD8B8D2C4D79ED58669248B75EC5FD1FC1E72108AE47A533D8B14BAF322873499E0E6C873C794D0372893F64C019B7BFF4D0BE5151D8DE1E49764247A85B0A9726CA36E9237C5E79365C456FA670E7C8421DC9193D1EE4A61B3D1D48C67C27183B709B6D43D691CF8AC79D5F5AC163C91076G040423FD4D2173CCA424E789DC79EA34394561B846D6E61058F0665D20BF00B2005A3BB1EF9D4DA377BD94FE2F011C29E7F9E12B834D51249EFC473A07B3E6B2C57B0F84ECC39DD3D65FFD8B8449E21C4D4A20E777
	E0F3484660B9627A733C359D749C3D43A537399CDD64B0A3732F77D0BF4C89FC8E5C0D3E6E4DE77C7B6313280F968E3411027E570694DA37DDE1B4FF26BE9B61AD0E686A9E025B1BDC99C006A415ACA9D0A687E0BD57C541BD5EFDBEEB73BFBFA376840E3C35332B13F2485ADE330F349D8474E36EC1DB696B4139FC2640AD73207DAC3C877BA822175808F4AF348DD084D0F5AF66BDC4731A6EE5F700026099A172CAFA7D75AD298B5FF3F8FD6A7C22FB835B91EB63AB3FED2867F43C832BAD9F280D38FB09CE1E
	9FB08EC1B31B734A74B7BBC52C0F912853FDAB457A98013AF9F3A5318D33C8AF25E9F26F097B43AB9BF87EF05721AB815646D85A465F337685565D18D543519D335D53E544F6950783F6124EFA982E929B2F57102DB727F922EC70D8FEE42FB7EF9B33FD13C7E37B7B28FF97843A9C9301E3F3CC315EB9E6D6E75C7FF86BDCC6FDBEF636D8B6337AB2CA709C4DCCD15F97A57E67065314FEAE5046897A04B4A2073545E1E7B49BB6D0877C3E78744C7BF0EEF87342D0F3832E7FB8B76428668602375C271D9B02ED
	7AF3C384738701BE68BE74D37E0BDC15667A587819A4E449F0905AA8201BA9E8CF6D7366875375BA81721682B05C2FE87DD593E05FBE81E5F3368DA55B2AG3285CD704A465A48276B66984007CDC09D1FF67B46C3BA5D98B30819406EE6AE046752896C3BA933594F3B0732593409EA1B0D6EA56B870BDD1013D908372E72626927G0E1F56B658FE9FA3476FA426130D9654C96EC45FF12F6ECB82C5C0C3GF5D2B9512B9366277C7CBB354F18B55EE640FCED5D353C3D4609DE9F0CFDEB0B5ACF749A82782809
	98D3392E181C37BFEDEA777D528648DA1743A93B14DE062537CF71DCC06B484EAADF9AE13999D7DC0E6D2D6B5BE82CFE22B6367EBD45AF18280D2D3FA97C6A84482B1A04673596FDC1ECAD8A52F3819683AC8748399F56057BD1173145C5D20033DA758EE70D5C8D66657D644A64FBC5B72EB894B03BF8EA957A2F510FBDA04D3C3E526F1D3A2A7B19BDD878B37F0DBB1348134967BCC0648B033CE80085GCB81D2A6435AB799654BAE61674109B914AB0A2945C2EFB10A0F3BD639470BA641C7A61B3BDDEFDDAF
	1A6C9D971513755746868D5E4653C1AABCEB63D12BD6FF21AA1AC81D7A199D98EC740E68E4E6872B84FE8D454729704CBEC2297FCE520910D77BA31C1FFE1966BBBFAD9567292301AE96E099C0DAA4BB53416627065546A17A5898291E1F5EDDCB6330B4BE3B1763A6561239094CD67C48A147AF7C086049ECB598FF27015CA9BF66F3B78457075F102BA398E3BFC34EAC2AEEB5599C2E42E6B287B05A9395DE5A278D643C0D3F7DEFE9013124E89752FE103CCDCBFB849C6B7CEDE62B4966E85BD6E0046DF2F149
	AE767B9604873A1E52FFCB8F786210F73A4472F171FAA60E0B5C87A7CA152FD69A367B999725DC4723A23D73E5DE12B11955795AE12F0CB11F8EC7C656779345D977D911ACD69F874FA5116CCE28B4052E3F3DB9BA7CA030B64BABD70BBE2F11DB451AF4187E5E126A0CFDB1436FB4F9E378272CED9605539F25F7DC991E456B79DD3F5EC1F179A90CBFE58A5ED5FA6422DEFE3048FF4A200D6B137C70A9B8AEFEB3C046C505CC6E5EE05F74486A3E497D6820E12D1F3E19BA05754942A95E3E21FDC5F57CDBF1CE
	BA6B812663FCF86EFF004524180E3FDE99D847EF35E489DD0A3358452C7FAB0EFA7B3F3DF8087EAFA9037EAF93B63E2D047A862EAE195553DA652DE7CE5AF06AE165EA3E72161BF961382A9C5EBF2B6EB435495560FC6613CF1764A4CE7E2EC2F8BDA5332F21795BF61BC12873324769F0C1AF1CAFA97214949536D9DB376D5AA546CEFF0DF79C5DF35C5B667D1D43677529935EF21D2661176B2D75161BE82B7EBBE4F44B0ADBDF3E702F3DED1CE89BBEEFCF1E726A65D7F55EBA8A07D587DBB3EEF723CFD18F36
	B966810C8FD5F4E1BCAC66810C8FDD20F75BD7C0BA7B4178740287F19CB5F42B4791AB7F158563CB15C067FFDECC7D063C33F57EA7741D2D676FFCCEFE97C8577703557C9848F532399D76CA7526B6D166117DE45C08EFF04CA564F9B3771339C05CDF26BFFB40EE3D32B57AAC29062F57AD2A7744562B7F0DA2356DEA3641FF47618B7FE79A5E7AC27FCC435BDF681F4F7B6397C31F85CC7A527F194534AF553FD5447A7D495324EF8640CE42GE6814C87C8F890FD3B9FFEC956E4F693CA6B579DB63711B39564
	977494EC3F7700FFD9038E847E5D3D9B8F10F52D192EC424FE445FF600741762F0D2A443059EA0BAF211550C11F27C7883743B842C0A844D62B33753B82C94856FD1983379D3BBFE074D95381F9F25F19CC89F94389A9746F66A85EEB44585C1BAC2601E37E22CB6DA60AABB306C02A80C7B24D19FB265A1E897E07FC3084F2678A6C88781C4887C780EC0B6CA6A8AE40B39BAB45E369F8D5846F0E2C8FA5FDF3CBEF2CEBA4EF7B1506E0A07705BC4F19339732279B6F154E5B59FB235310F93ECDEEBCA47F93174
	E191B727F55AA0BD8AA07CE11C973BD6133A6B202CC5587A66866288C6834DFC00E5G19BF0ECFB782148154GB481A8GE3GA6814C840885D88C3094A0E5AA6C6D26625818DEC95691FFFBE4AEG9DA0AE13158C912E0797FAD08F85D017F9AA4E7B6BBCA8DF78D4142F1C7AE7F110CE84C8F9843F3D4C7DDE7DFD427837133A5FA716FF5FE748FD04757B41C73C3ED2967831CC77E1851BA51FBBCB076D96B2B84D4CAD14F0DD4969A37C11712DBC02BCAF3908388582375C005FBC767F84774952D9A3A98769
	9000E800D80045G4B81B29F0DCF4FFBD47C8E62117CA1744ABEDAF9554AF43AB6D51CFD03FABC0F0A7BF0292853290F22CEEF2034CBA0ADC5C3FFC66398C89A61BD6B77C7D82E6A0A4B31BD682F29AFD0942D0DDD7C1E6277C7EBE397BD6277F92AA0AFCA48DBEBD1371FB58D7AE05A507388BFD7F95A7F9C7F526940F37C2B2769EF9D415CDC5F6B64E768961EA6FE0C233595DCB524FB7DF41640D53BEB9DEEAB719C58780EEA9F9CE3DB729D9995ACCE380B4E39E5A0CB65B41C8F5EBCBA78BB4D8ABA4F1BA1
	5FB98D775CADAE639032C7CCD36F3943DD442732B8DC56BE72239E36F21B354DCED6C74E43F4975ADDB414E37B123745D92604E9ECDF32821ED95359BD8E4643F79DC37110BFDD4D43AB85E42DBCE4B359BB78C0E57844DC62A3605E101C35E22736B07F50G09A712FD8D4BF55735389CB69B595EC8787BF7C7ED5A7D9B6131ECBA6E6FC22DFA79B553715C1654295EDFB5F1994740B3E2BA5A5F1674110D1B029116132F389C0F855133DDB327EB47DB864547CC570E37D0314E4D073C5299B82F95B7789FB7E5
	8D0147CDF9033EEFF4344157B72AE950770DCEB528FDA3067B36012C85ED30DEB5883AC601BBA3F0A6DAA741FDEBB553F515615BA8DF84DFA553508247F7D23EFAA82E0163DCAA5C990E6BE9A0E39FC4066104E5879A68F8355AEC5CF724B4D6BB5949A14DE84A2355E9D271FD13401D917D7E324FD93861594B3F0D30DCCE405FE2503733E9F42D8F1B2135331FD17C2499DABBE3319B3A36C1DE76CCDCEB67B412316701F4A840CC0079G89GABE622ADEEBCB3549A4B3CAE6F9A4B4FFF77697908D9BF752E99
	F970DE76D37FEBC88D4FEB62CF05BF03FF0AF342DB3A677442D5F5CE057718A15AD840739279D3FA0CBD339F43B67E62F347124E2D1CEEBFBCEB814CG9E0EE3BA4BB86BFF7CFEF0B670785DF4D6FF7CEEBD6BBBFEF71C559F3F85E7FD47EFB12F57A5AEB471336FE709CF48E24F47CDF6D31B6AF78EB6785C0B9C1EEDFFBC42F2672E389C337DBFD3DFED4CE35AB111C24FC006BD269D933788BFF0926425BD0EFD7E3BAEC97032CC75FE316B6A4798FEF8D55BE07B4CB69A7331BD4E7441F0B60A9B45F1BC06F7
	0E484E7C00670E12B55CA70EEFEA69E0349B4CDE5A03AEF2FFCFA7B6DF8EC6EE5BD6E0AC6621F9CE9F64A2737089070B2E937C6CBA2DC39D3F2BA37F4790023B306270097A4327C4DFEEEA473E0CG9996BC0EBEFDCBBB7AD6D93350279FCBE92BA05D84601905FE1F27032CA307CDEE3E073CE4435F838D1905753DC0776DE43C4D1D05FB523643E2DFB4787E6FA0DF0915AF6B44BA25A7101FDE8761278C5235G16A7445EFF355776562A5E4727DFFDBBFC741F5046639E2FCCB12A77631E2B5FF66BD5ED035F
	BFEC743F867C22B1709A70DA237E9A70FA236F9A70E6237E9A1058689B5FBA5007712D20A7B076945107712DB18277A70A0B00F40C401D6CA7388510DEAEF0937BB1EE1579B3443D566F9B73CA21677C4B0026C6503DFF89F9E992B867A56445A6F04FDF7265656DCB48CB1020BBF099F98995381E4B3E3CC4D05CEC200993F45949484B92016B92F7A816895C4349481F71C94425257872D71C22EE17478DD6FA6DFC356AFDDC6A553779282B5A861B8BCDBB48DC4663775475E778DC4FFBDEBC3A7E1C2F2D6DA3
	7393F60F301C650A4BB1BFA215FE37AFFDD26BDF2C217803CFEA7D0BBB441AD483F9D31F94773EED58FE76EC2FEF1A4F5F774F7675D7D93979034EA754F2BAB3FFFA60B9C6D316157B22D3DDAE82E822D5F4F3F55AE3F23DC55725053335F7CD7E0B6293E7EB6F1ADCF660DD1395105774941E875BC726A39E6B18570771161827F05DDD5BCF66C2EFFC049C7543888E16AB6847F5BCC114EB6DA7310BEE5925D8DB54D195AC93A44AE44F411849E37D46A17A39F20EBAE632094ED7D8BF2B33C47C36DC7D9C146F
	7BD62FFCF7DEC2794CF3703CDB6EE5DC73C789BECEDF9E0A0F68C1FC4C386C4BC72881790891FC84516FE17C1CE572D07547717AD9DD2FA6A34FCB66200DE6F4C842FE32D56FF76B1E4379474ED3532FEA9BECEE9ECFFD3B34276353731E4679BA017A40E5102E9238EA2B781EA3F03F2138A0C8078A5CC29AC71C89693982D7EE4332F18257C7F1D2CCFCFACE8C626A29CFD8G69FA017BB145D9A0BDA0F0058E2CAFA2867B69ED8771D9E3C57AAFB41DA852239D7F88DD127A7D7757BD7F10B67CFDB3E2E360BF
	7A7C6DBFD8FEC57FF0778B0C7E0EAB24FF351F6875DB592D30F475G1E4D6067B1AE8D4D5F1E4B815ABBF4323E7E64F1511E7BB26EC5785DA043607626A42B3FD9B11C14B2B48F0F587C1FC9FE1E5AE556B37169394F607C7AE74B603879C452941FDF1A2C46A17A326A99757C750E15B8BAAC660067EE5637E34C3791E89500880068E75877CCD66E6D0E416DBFEB75365FE09B2A7D58C16D0F35695F6918C2E5DE8C3409C2660ECE9297732EC5EC8BF748642AF5582CAD3DC2DFB75071C9FECBB273D9AC7BEC27
	2F1C0BC4EC3B76D9A627851E834F32EF36CC16430E21E489FDD6AD4B410FE83CDE1CAF66D1F989FFCF3E50293D83CD5A0904BAE6BD0B6B44EF5BA5D15FAFADC31DC1A9ADB06E5DAB4EA3DD342AED0E2D79DF34631A9F8775E7BE07FB620CB524DE6D3742CE27524B3E499D12CDF65C930FEE47352C72B95C936F22BE55G24C381221E935FEAED6AD8811367244DAB4F1B36214740BD1D7F2399072ADDFAE15549D355E7CF1E28AF2FB9DFD7F16AE4CD4D2172D3674B6B4E1FB8FC387230786D7DD43B38D38BFC2D
	F88EE383476D621B73DC14231F525AA0BD8AA0FCAE4A516C70BD0B536A4433B851227CC9FA6EA79152A973E0EC4E43F93DA2208FDBBACF4F977CBAE0B96DEF1E31F2B751F23573067E66278666AB5ECD7B51BCCF6BB3CF26F867BC2D4F3C3B83FD668148DBAC644D300FCC5E8F8316531777D440F27A727EBB6D6F4A673572AE27785A673572DEE3C7F99BA1EF7673A8EF07D35BFE5E8BDE794ADE706733BFCD6D256685ED7B31945F7002367DEF4439B2B3644D85E03FDFB4FB9D5B2F592CF639027D95CB89FD27
	7B58D9185878740331486FC4150F9825FA5F9B702C485F832C0D937F04B67EE0774A740E6A7D867B55EFBB622A36417A74DABA7754C0FFB645223F19AB4EB20D927DBC4F6D157D972A776F5CD7DF8F4AD5ED031F0FEDD7FFBBE46B0B2BD39279F7BAFD4F68CF95EBEEA468E9B640920095G448FE73F1D32C77C8D116C97635373812AGACG41AF625F9FD960443198893845AF62DAFA2ACA7F77A58FB9BC036EE39BAF60BA1A73922E9F568BF45E013485A068A5F156C51CB988FB896FD15F60525E23FE59203E
	877DD317769EB5A96F4D7F1DCBFB0FFA6C207C4DEE6DBDEAAC4F7A77DFFD663C6159C562884BFDFC814B4DFFE9F86B9A7FBB45D42789AFE96758B7A9FE69CB5AB956F1817BF58564B57DDC7C56176BCA79E663F2BF6D67181FB3DF0255754685492B8F55FB57052BBFAEEF3D2AED30F1D7F7017E5D990F554D7EBEFC62593FC163D1EFC929EE1774012F6FDAFE6BAFDF0DBADF19ED108C5552CA8B8DB8D6CB29BDA4A269F1C2EF04GCCD224A423A2E556D99CBDC672EB6A796EDDB02A95E9355741D624F41ACD4E
	062B73AAE9BA3DCE111DC6F2BD8B58D8B3586DCC3AFF43F5D9376D69B2C8E9C724152DA61B53E28296685F832911B21AE9301E1CCE33A9D2967DB3AA3B216CEEDA76739A495062D624CDDEFC8B41335CBA2729C58612F4334955C1EB38A0DD12B23E1352DBDDED4D3481C5B2FCF764BBE81626C876FBB552EA45C28E61129D15A2ED70D6DDCB3E5B730C4F09F6A469DD784FC97F39C9E1720F8C5E829073FDA1E6B51DA697F76F59177C4644E40314822AE61FAB2A2594724D8144F5B14DA7AB8EA72DB9C58D1A3A
	B1C50EF866EAD2AEE9FB0B2333505A5CD958555DD9E8EAD68AE5B3ECAC48C7EC33B85BFF7669261916F96B37992454EAA92355AA5B4CF574F4FF0D14C5D315323B45E5F5125022A2FD40D4C07E3452EE7E2715EA2435148EEF85106A89A39BEA24F52A6A0ED8ED325D1B35C615754D299AC5DA43EA364B4AEEC8B31DA6A9524705050D27ABE41B4D780DE235B57A39D321C8B97E2EDB1076B632AED96BD719FA6AA3293FB2A6366AB0B1785203A2E56B5D05A0F5981DBA958F07959F9AC5CA25C74DE86EEAEEF392
	37B917745E20EF474F5C37774D58FFBD6DCE16D6D6CB9F1223F9F5320B7D05B3B399097CCF209F11521D8E30E5925AG93F453C72ADB69350181E4371EA2FF86552D48E671332FB04456332E6A82DD6C2EB239AD8A798687F610A27A1775DE79C1AA911D4A013FF514BFD22AD014DA77787B9FEFBD16B0B3D9B29C11D65B9CA67391D30B62F0D5CB690A45A53BAD8E1B5963BC40AACA6B41132FA9D05DA6A1AE912E10044085139D1E3B473C37643DA33098FBDC52EECAC906EA2120B0D7D8CC644F38492EEA057C
	31F907ABCF64DD33C5597162518D0D86A9E930D08A1914BA2D36DEF829243766E8D77CB7A45919ABB156695F9EBB8272A871EF4D1B705A7E2509D05991BE1581CBD9DE2A93DD1D4D326B7A285BB7E704AE3E19F06CA462E24D690A4BE4F713BF9D85159BB5B2E316E03DF05C3C225B277C69ACE55DDBD411BE724EDDED3223F3F7F9EBAB6CD3C485B05E207A4D22FAC2D328217940B3F67D31D75EED860B2A1412BB0B0A687F0B697FCB487F0BA92618E20ACB205B16CAC37E5366FBD0E6EA06B4D1CAEC48C35E0A
	BD0A340A1D3AAC91EF25622DD811520C7C9F2CCFE49DB232C84FBEB13448057C9C5D410343ED35EA382D463FF3F4D9FD87FC7968963F3F9BB473D56693453E2A3D837E6F67E86CFCC1FCFA5E82BCA3F6609C799BF67E6E8155F51AECB645E46BA8371B491D20C33D0A6C3D07326C9C295F0DB4BC13657D799C613BDDD5667F83D0CB8788D3CC376BCEABGGFC06GGD0CB818294G94G88G88G42DAB1B6D3CC376BCEABGGFC06GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1
	F4E1D0CB8586GGGG81G81GBAGGG08ABGGGG
**end of data**/
}
}