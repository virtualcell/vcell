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
	private cbit.gui.EnhancedJLabel ivjJLabelLeft = null;
	private cbit.gui.EnhancedJLabel ivjJLabelRight = null;
	private JLabel ivjJLabelBottom = null;
	private JLabel ivjJLabelTitle = null;
	private Plot2D fieldPlot2D = new Plot2D(null, null);
	private JPanel ivjJPanel1 = null;
	private JPanel ivjJPanelData = null;
	private JPanel ivjJPanelPlot = null;
	private Plot2DDataPanel ivjPlot2DDataPanel1 = null;
	private cbit.gui.ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
	private CardLayout ivjCardLayout1 = null;
	private cbit.gui.JToolBarToggleButton ivjDataButton = null;
	private cbit.gui.JToolBarToggleButton ivjPlotButton = null;
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

class IvjEventHandler implements java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
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
public void forceXYRange(cbit.util.Range xRange,cbit.util.Range yRange) {

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
private cbit.gui.ButtonGroupCivilized getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new cbit.gui.ButtonGroupCivilized();
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
private cbit.gui.JToolBarToggleButton getDataButton() {
	if (ivjDataButton == null) {
		try {
			ivjDataButton = new cbit.gui.JToolBarToggleButton();
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
private cbit.gui.EnhancedJLabel getJLabelLeft() {
	if (ivjJLabelLeft == null) {
		try {
			ivjJLabelLeft = new cbit.gui.EnhancedJLabel();
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
			cbit.gui.EmptyBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.EmptyBorderBean();
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
private cbit.gui.EnhancedJLabel getJLabelRight() {
	if (ivjJLabelRight == null) {
		try {
			ivjJLabelRight = new cbit.gui.EnhancedJLabel();
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
			constraintsJLabel5.gridx = 1; constraintsJLabel5.gridy = 1;
			constraintsJLabel5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabel5.weightx = 1.0;
			constraintsJLabel5.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelBottom().add(getJLabel5(), constraintsJLabel5);

			java.awt.GridBagConstraints constraintsPlotButton = new java.awt.GridBagConstraints();
			constraintsPlotButton.gridx = 2; constraintsPlotButton.gridy = 1;
			constraintsPlotButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelBottom().add(getPlotButton(), constraintsPlotButton);

			java.awt.GridBagConstraints constraintsDataButton = new java.awt.GridBagConstraints();
			constraintsDataButton.gridx = 3; constraintsDataButton.gridy = 1;
			constraintsDataButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelBottom().add(getDataButton(), constraintsDataButton);
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
private cbit.gui.JToolBarToggleButton getPlotButton() {
	if (ivjPlotButton == null) {
		try {
			ivjPlotButton = new cbit.gui.JToolBarToggleButton();
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
			ivjPlotLegendsScrollPane.setBorder(new cbit.gui.EmptyBorderBean());
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
		line.setBorder(new cbit.gui.EmptyBorderBean(6,0,0,0));
		text.setBorder(new cbit.gui.EmptyBorderBean(0,8,6,0));
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
	D0CB838494G88G88G63FC71B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD81DCD4D55638159599DBD45852C6AD9995A59515ED34E1D1D1E9322D969A9915A51AED543A3B366B6E1F7D6F8B91C6C186958585F594B4ACBEC4441554D5C0B0A9D492B2ACCA86668123434CB87390F44D3EF36FFD673E471BB78C32EB3F4FDF27776E39675EFB4E39675EFB6E39778DC24E3A10059F26648842078B85795F0E8F85A178D2C1085D386AD544CDF6259985553F75GCB05978F2742F3
	8664854ECBB3E68BAFFCE20074BCC81FFBAF4D188B6F4B0517BEB9F796DE3C34538CF905BD4542606D3CDF0A6D2453F438BB931ED9G7381C71E35EE52FE0CBBCF469738999E614394C1B0C804668DF7010C73831A90009800F819FEAEBC975116AF5FD9A95335DE97A644BD9F1D3E0454232A9321D1E2E5A579A98887528BFAC9DB86A9D3AE938EF909GC47885424C43044F5D0DA73B76F434DB25D2274DA16D5C59887FEFB45B45A0DA36D1B2D0DAC1E803F451E902BB340C5417G69F9G1927911F3602603B
	61FD84C050E91461DF9F6D16F9EC7977A3421C154E25C6AFB2C41CE63C471FD6E418D0B6B6E59555F7E99961B727AF4DD88950G608488G08G48277D3CFD4B47702CEE1776B9FAFA9C76EA273346E6F19DF4D8C49B3C6F5C89B9B26E085861F0D9840175E516D422434FE0404A95DCF0B9261330ED582F61C76E93229ECACCCF5751C9D458447489FDCC97B37A941D30363F725A3650BF506E86367D3520EEFBC2D7A33587ED5B094D0D469C391E62FE256D6EFE5257CFD19BB8G6F24DC8669F70A1FCC7161AA
	BC3341E60AA769D848ABBB03365112053CB4240E9166164D5255C37FD35353834FB09E424FE8F9C9D9C96A0EBF435AAC643C94D0FC71D9854FF84924F6CA527D1097786FB4A3332F9D65E4CCC61CCBB34682A482A483947CG76865070835A5879ADDB06E8E30DE617D453EB35D9C417404A6EA8DB89CF31DFF2196B6DEE49EC6F900F1ADDD6F33BCDF48BB2CDD59951C17B00F2F55476BB200D4FC43B68B2CBD6079DEAB777081268F2B31AC3E5CB890DCB24394D569E919A68F18AD86F5132657034195D52D7CE
	0BD992958A167FFD996913536623D0A3D4G6FCCAF16EDC4DFB610FFA4C0A05743AB343EAFC417DD346D5C5960F01C6CF5B693C904449F081EEDF46E880377689FD047ED9584D7FCBE4DD8F59E6B495E3ACCD5CF2D350330EFF61D41B1B6E199CC58C2B350F78378830482C41DC79BFA3F85ED28F6493DC26654193AB65472D523F172F9E6BBF34EEB4752B45A8661B37BC730CF00C6G670FB857FF332CE588E33B7B7C40F157AC732B258B15F1D1721335619ED29B8BB56985F2F93D311EA50F39AC5E17AFECA7
	FD99GF20583447E08FD7045D69333699A33DBC4ABBE6C828394ADCA0FB0BE8DABEBE57DAE1079A1EDAD115BB2D29AD227A02CG48155BA66D2C17475DBA006DDC6E8D343EC460A5F3C41A31822095E0A4C0B8C09CC0B2C051A5B08681A400E0009800A4001CCB530C3B81AC17E2DFADDB117D61C07ED09F99B21F037118A573073C7A6A0F6DC320793FGCCB74728EED0AFE8B7C36D3FACD9AFEBE49D79EAF3A87A7B3FGEC8CD4179359455322DD2A951D225D82AFEEB96FC0B95111BCEFF7BAF819C3946F04F9
	FE9FCC2CEE0EEFAEA7BAB4CB124B5A5EAB09CD22A4D96DDDCAFEABED4B5D5B6ED41688EC2B231C740D238F66F9CE5FDDCE66488E075DAE5241480B4879A7689CAD5A449EE0FA1F2357AEB13CF39B71D9CF0AE764B5C25A16A610B95E56CB569B6687C618C93FBBDD569E9862E4499292AEA57377E5D21ECAC77E00A37EEC84BCE3AE4539372F90675E2233F78BA1479FCF5773A57A6FF8AD3D68B2B657D5DE264C3D0CA731C21F7713F42D1071EE466B67C7888EFD548514EE79A6E227B53D126430FF66F274BA77
	D9CFDBED563322E52B40ECFD39302B0948384F6C32B4184FB8FA252DB27E06330BC9BFC0A3D42DDB11FE55E9B2B608C7DCDE3BD51EBB53F790BEDB00FF9B40484BF06EFC3B0264637AA5CF1A7B9CB639CE480B3D0C68F34D729C15CE02AFE37AA46F4DA3C9FEBD4F4F01F449C8ED7E93BCBF985263BC72BBF2B0BF8652892AFC46E75C76ECAA6B29DE915C8E32D0FF416D62556D4BB4F9C7E59BE179B337937D59D4EB3C73F25A66CA5C47A4C1FB2900AB8228GE881D8CBE4390250ED4BC53A284B99DFF1B25D1C
	4BE53F0E52A5F13A36AB58FA50FF85238F380251317A2E49C33A79F2FE761550B64001AB55F45F2FC23A56AB991DF4A5DB637D8750652D667CF921AB71A3F4311CAE6ECA36BEA541B31914017CD2CE770BFC246B77E3EB99218B70D368187E5A8A10AE827093G127C187FA1DC25565F16B5C817FD9573CDC8BE21AB3ECA4D5FCCCEE7390A2D17672EE27592FA255DFB57A21D7FD5CC4ED0D53DEBF9FD6EF5DCAFF2FDF1B2DD22DC9F6B37DA93528923187E7264E749A8F57F7E1D53B50EE275F44BF47DB29D636F
	6175DCAFF27E04D14C9FCB984578E3750D5A406DCF4ECF3DFA203D30F963CF1B79DE77047934393F545D8773714E1D1F13E94446763A6FD1FF150C7B5A2BF1CFBB7EFBB2664FC1BA90A07CEA1C7726AC41BDED6049B0A1347B30515B3EBC66EA59673CDA1977ACFF583B084DA9DFD6D2FF589F76987EB81FFC7FCF35DFDD6DF6BBBA2C5425174762129D6AF91161166DA073D8275565166AE1616A47F11B370370CBFCFE99AD6B634E42C2C13D776F6A356E5C29B7054AFB469F762B658DB6CECA4F70A2EF03BF13
	53662F48BB7F7DD3C63677F2AE67318848CF7447D804A1FF153AFFCA4DFD524E1D4AF4AD605E35E39B7291F0768E21EA749C2FFC947C0C353F7BE78A9FA5677FECE0FC4C26FC1039DE027CF000441F91FCE18ED9AF49D8E0F423F2102E781AB4E35BB5C8779C25A3B649687CD7A2DD9050CC86E0F37E2A0F54F33E61DA988FG0DGEC1D7953CAD20F54EDF533B5F7DB1EFA4D35G1DBFC084C08CAF131FCBE4061D1B546BEEB0370BB6D9B7FF2C2E124B455EB7C688CA1F1E2E97E7B9F0576C7404EB19CED28394
	5DA45CB5B6E5AEG1327B5B7D33615E620718F40361F4EA57E83DB9751161A4A49F8F40BD2A3C597B770E8D2A9436C5AFC947B6F5E35EA9E67343D244B236D1D0753E3E45E12D4BC365CB4B645F6937238A28FF92C388E56646B501676FCC970F6072B47ECBBA23AFBED924387571236FA695E176A4DDD8C0CB13E46ACD773D570E338AEDF5D46DF2507DE4778093DCE61EBEE505814D44E57A31C2F3C6B212F2FC75D2DDD25569D6B6F8FB6287B5B8FE847814483A471F21F8DA8475A38F2955ADB6A8D307F3981
	E95F27F82B5DAAD94DE47C96EFE6321759B74053EC3194B728663F526AF6B7F8349D929B79341FD87D552B7978003A43EFC0BB7F00639389AE905BED4DA7F6ECF6F5EDE5F67F12D5ED774D014C6EC7F27A1179E877CC7F670E217EC7440E9366AFFBD5D77F840282195E2383957D97015DD6F25BFDBA9FF94C9E8D7E40E89C0B37AE5663E944E8146D2A82AC97C84AG30BEFAA2CB6B8713B8C16A0D30465D08724CADA0FDE413A7AB5907DC03B2327ADB5620DD14C1396E9B51DE6BB27E070C1FEEC79F6B9CB279
	156EF358A5339536G3B984EE6066EEA026D043D0BF693463256A24FA1D0DFBC67473D967B0351CD63F459BF871D7C9C69BE4DD9A4287CDFDEEF70BA249F8134111CFE01C92E371C25BFB111F54FCC8B13F01859EE61F57418D05E39D03EB8886B08B311752B9D481DD01069A0771395320D138579F80D1CD97C0DCA56CD6B313EB628AB085777337528E7D657D30BBFD14B3475F8B3E48D34FBE66FB4E4C734496AFFFDBD4ABC996AB65C04753F16239EFF8CA7ED9858378FEDC05E2A20DCBFAF3B3A309C1E9D5D
	50FBE2A9193245FD745DD22CE6076EB9CBD9CE9DF95F0C7B75BF7298EF44CD4C5EE3EED262C9712AF7BEC75158675B4789BFC9B7315867A20E1FC77106DFA8F8F94CD0BF07240BA04F6F97982F336DA2361F84E9434DE9C693C0A5C08BC07F4D682F046C58A6780807565BA5F0E74C3626819BDE0CCBD692DD419ED5CEFFD3C974C8772882462D266FA6E3B7905A0CG18GB083C088C69E5E2C7449C3DD3FAE8FCC4F27178FAF7EBF72024B493166BDC49E93703F9B20994086604F6579645392DF72E05CD89604
	7113C479898F560F7FFB1763EF34DC4C8514E3737666EF5467D30959C16924AF75666DF9F25D2937E8E32F81B9436379561C8B6519153BFE252FBE2B3DE5E01D2CDC084FF25D1AF2725E23534B5E03BAA1EC0D29A952DBE3C6C0FD01G72D95CC7E45EA473ED78AD38F6983F4D97744F704AEB6956C676B37E36CCDBBF5993E65F8A7B1CDB317E13F9F856377BD65C7F3CC8FD9989527EG2137623C704ADE326F0A02F4BC40DC004CDF42FE9420814079CB3463B31F56783263EA8C12B5BB786BBEED208B7B20BD
	D75B87FE3F9C28F37D3EF3FA14DB2BC907EA6AE1F29E2A26F2C2DEBC40DC004C9010932081409902F2CE7AF417AFB969448EB24AEB25D6C046E7FE5E70645B6CD12ED4138E8851136F199AA2DF986445GA4G4C87A8388D66F900565BD03E021D7F9A22FC7BGF74061B8295305B22F6307A963A40FF25ABEBCF71B5E785B32F61D57B1D2EB164C6AF1D2E5529BA701D0EF58EDB8CE7E279047C974EDB8CE2EADA463E4BE243347002FB3864FA2FE36EAF8F34D4DBE4BD50D51AB77596A613577154FF2AD3A6D7D
	3FE13637A85FD7B967983D3E7CEB1ECE5C6273E60743D6E3F6B5BB3A3A48FAC8FCBB017555629386011DE5780D41392B2E1658FC92248D3703CF83309B208D609CC0486DE8730E1DBE67AEFE84BB1439EB22CF1975F575324FF211378FAC47E41F3A58A030331C493723BDDEDBB0BC9EEE71D92EA2D40F073FE499643EEB884531320F5E85988969E000F121485B77EB06B7AF58BC4A15EA523121FA65162D9D1EAECCBE4BA56A3617B04C76FE6733DC32EEFBE3568D2F3D881F65F26E50AB67B68D2F7FAE71A827
	1D57CBF45BABD9BF3C76F6FA145336D72B5BDE22C7392169730FBE4B356A367768066149771CC739D2CDDA5234476EEBAC6AA5E3559F7242G986E5EEF89AE8A5213B9EE54F774FE9D24B36F44317B5EF1F236D4866983G2DG924F7B9B4D733F132D25A466924E73A6FECF6EBC90DABD3D103A4A3C64F15D8D124F5AF80B3631488B8DDBD7866F9F3D7B9588EC2D685D657D6C785CAE7D3363CBF7691F9D7B6DDA4A70D0077A869166876C9A7CECB9E81777336537F6FB67731D5D7AFC7EFE37BE1F9F6C9E1C4F
	3F6F9E1C4F3DFB3C7332FF0FBEAF8DFB74F9B932E7F0DE3E59630D17B5347F272510B54955EB172CBDA22D1A590558CF6EBB40CAFF24574E5CGD23BDB3B8797501F3E2C0E4405A65F09BE449F76148B1EBEC41D45AAB9DCD8C933E8F69D6E6C648EC413D36CC03E5E16C8DC4C635C7F90F89D824622A62EDCA453B4CA0EC6F2664D565E33C53876A65D09EBEF5BA6A2E751DD6073GB45F0563702B0F746E9E596ED24656083BBC47D9A06042B478A8C8CF3ECB4F97A8DF0E7CA45E057C7C0D1E2F94042599ABG
	9A42109F27072F2F86CB18373857FE9A7F6E8FE3712D8FB97EB0450FD461D95CABBE8F4F2382A0EFDE98765B4C3D5EED73F7FB75EDF36EDE32B7F7BBFADD9D22567E7E32B70F67A98EE23DA476102EE4B4C295A9AF19DDDD22077D26D76571BCEF65332B4839D03B55E2F54951DCE6C393D79A6958F03BC5B75D2411CD0FECBBB765226D54C90E03DBF9FBAD555E65BFDE2DAF3FD86DDDFEDB35EF79FB2B3D4BFF2E5A377C17D7FB9B63CC56A3F9AAD977A9327E36463B2C53EA74E5CD2871AE6B4C9A5F32A655F8
	1775CFB53EE5CD2E99DC56F9EAD98F1673B158EF42B158F2B70E41AA9A3B8E00F498C0745DB8864B8B879B0371F7FB9B038B0AE86C79EE6D984C247879F7EB476068C29C03297724993B6F4158F94CAA358C95DC36732B5578ED9CDF1E1FA1639BBBDD0E9E1889199C4693E4EE0B3DE7A03FB36474BC79E9982B649740FB99C06D5801E5DAA0ED5360D8FF9C2E75EEC3C7EB75ED2835563B8DF5547A36A1DB2DF79BF2577A3621F3355A790269D1A8982847F2D90F539793BD0E50489FA8274364E714AAFFB23CA7
	0E75341379004B8C9F08E7EDBFDB20DE4B085D323613333436E8D2152F8877E60B0F30FB6A614C6696F2BC3B3F5E2A42B3DB2CDF0667B8CE480B8BC7DB9C3DC6EDF35BB93F75EB876AEA1B0CDF3ECEAD07E2232F2C9BC8DFA1636FB12967A6053EE33D9A2F0C017C8D6A7A957C5C8DEA7AF20EB77592DBF13A44CE516592ADCD563344FFF05ECBE47E147A7D1570DEFBAF9FFB5F2A4FB3CD77D13AD60C0D0D833AD800F9G067BD82C1A79CB8F749179BADD669E180E28A313CC4B5E769D166D0674B900E000A800
	047B304D299D24AC0B0AB7DBA5702A98FEF7B33181F6D3876F6D459F4C64389A07A4B9FA64333A158A3EC16C0201484EFECB5B3477B0884F357763996FAF0E6B455FDBA05FF6BF7A84D7565133CE1CF3E59F0C59670FC5DA7B9CF93FE213C177FB334FB86A0F055F4F6CB01D63E7D0FC0C8A4F6C331362C9BA81722AA2F0CF56C74F86C2A1BD0E6392A8AE9A52719C77161B6012A0EDF8GF17FEEA23882C817F05C24A65C63D5F1DC6F92028B00F4588338C66C70B87F0AFAC0B57E9F70A6371DFE0311780076FC
	725F94AFBC28BD1F94D66079E481640DF890F93AB28F79CCFC90F9EAA02EB846B5D56F2F678D65FE7DF0013571171F200D9FBC62A5598FA9FAAEFA481B4E4751F53E72A1265B859C7FB8459FD061194E77EF40737096480BFA88F5DEDF08BAB70DC31DAFAAD2743CCC75FEF361455747380B5A8613F9AB0DA3B7003C16F12807A00AF3C2FA84477D1D627CA19D4AF15FE690DCA42463B86EE90A1B8169F99C1719C9F0060741AFFB98F14F0BF4EE0674810E5B13057AEF6538D7AC84978E69980ECBDE0CBC47F35C
	F79948F3A247BD1E01BC4F65387147106754DFA1AE74B872124DF10EC51CE70EFB6CB872D749F1EFAEC47E7CB8EE39116002A19D41F17317E25938DF21ED0DDFC6F042A3E9463CC790B70162485DB8F2DF0A618E536FE7880D373E9486499B1A2DF553B6345F602841D7ACE9B0A8FB04FCF7FA22C190F05D9E732D891EF6314F44EF8655DAFBC4BB79C02B38223C2C44D4D1D636191D450774294F627396CF563D0BCFC0127560A7BF0B36A72731B1EFEEC5DFBC9A7266BF026B7A6CD2FE4E2C5C4F2CF1103B6E72
	8DCD363F383B55A017AF0B447D454B344EEEC80FG880A44BB347D1B70AEE7781DE304A00B7E9D5A6866C96991110C4F38C855FD453231A92C4D673E43B6339F05F172A83D27761DFA3DA73A3CE1810B03327C0F3ED767133D94714B157CE38372893F8BD25579D78F48A7BA3ACEAE4FE41B3491E5CB1E94A1B4BC7C22576FF6839EE5B20D131F118B125302038FADE23209EDC4B66297A5BD0A670FAF9E47BDDC632FD1478F1A090E7DA09D829079EB9CCFB7529850E4126638951BF00C999E4331B31D7A959510
	BEG50467133A91E5C6BCF166F2F4BE7AE1BAEE44E45BA07C2DB415B3E7C025AD0FF4F648B9688B83E2E79085E63024D999BFB238E89BC4FE026F72A9DAE314B6568A5975B183F3C3018682F9F789C79987A2EFFF5FB772F1751FB6521C09B41692FEFCA25F55BA5C693C67DAD42DB8250D4C5D1DF2D29F688B2A0E4C29993408AD99795D14ABE1B35693F045891B866561E5E9E4AA1EBBB147AF5FEC09F9805B6F2E8B94E4D519CB7A1077BDAD158C7528A925BB70C073681AA819A47E35EA5B42FFBBC7B4E438F
	1EE1BCAFED05373EA5F5613BC92ECF1DDFB95E379D31B656F9EDC3BDC763770B5A723E5AC898CF6F146619862119A0734A7497E8C21D96BD0EBA4D5A00BABD70B86A665774EE210D27E3E81AFC5BA27B379BDA647C210E0D8B815606C85B78CF56B25F3AEBAB9C0A6E186D9EB8C1ECD7F2B8E0E768EC0661AA2F15779E96B84D7DA7860E65579A153C1C1354764D7D9A5BAFB9C9ED9FF49978B80ECDD7065E1DD6D627F0C429F3BA751B58BDD351426A7BC3863F9B426BAB4C74BEB76C26BE5FE420CD6074EF0AC4
	8EABEC62994DDF288FE8F8A24D1873844E8DDFE58DB6B760199602C9B5B71079407904F6EE18EB511F9B8C346DF3C06F8740F4D058A4F0FD6CEF9638ACEFD3594781DDB427BD7F15127F9B3AEEA5C15EFCG068B227EE3E3B4783251A85B0B478713AD5D07ECFE510AEC2C0D45E9240DC04007C4230EBFB6FA46B72A0D9883080DE65F5FA542F3FEB4BB37E4B6EBDCB6184DE6BF293659F1E7487A41E291643691622D698A1E0662E51F61375F2A7D3739B77DBA9D655036D3A437D37624628F3173A767D1A22B85
	70A31E4438457D8D64BE74D173894F53A7C0B63A9CCE51A51DE1D87A551FBC87F4358E6FCEEC6FB04B5DF74165587E750B6F080F927224B65E5CCC7161CFEA634D2FF19FB792728A1E42FBC137FDCD7AFF8424038146814482A481981EC6DD4EEFA893FC5C89EAF6B89B445350654AB1A413AF763B617DD648136968CB0E7B75BDC2509557FB7F6669E9791BA7795918121CC61ECC3E0AE3C4BE9B647981048344G44834C6572D59D146F9D93F32816A4F3C7B77D4A0C9FF8DA45BE85CD82FCCC3693EDFAEBEDEA
	0C32C66545682FD7A59AFC151CEED66165FBD047357A6BD6511C53291F594126F6FA3E91230D3115D3FCD80CB64696E94698DBB46495BD03F3463DC51EF3C68C3F376B87F4A1G93G921EE1779C581CD13249B4C89F4FFBC6BDE77CE3A90DF5529868F699F7FBA319AF488CA2DFED1371896B881E4CA008E7F36E26D60157B75DCD6E3E293EAA31B9DC256DE40CB35A7A6F94DAE793398763ED2FD9D1E2AAAF5B0C34CFB4ED50526E87E736F80345EA36B93AB61418E0EB3A35FCB37B1DG42835D4353FFF377FA
	621077086F85EE77EFE6225D970C3DDDA8FA797A74C2AFF6EFF816693065D9E5BE9C31E2D6862BF3F51BA2632F2CC3111175ED5FF26C5B00E7D93CBB8C1ED14F32EF72621EA5B4EF6464697003405A2C2EDD4C7B34C16C646BC0DDDF265C2F72DD6D2D8C7F11D909039F31F6F5CBB27DE77D199C4FE25EAC367D708A75DE3460B9465F0167703B274BD7696537727C51EB3531F1126FFC8E6D7E31BCE2777BC4728D8776CD1F286E1B64355724AF7552B7A31FE3FD92711C52B76449F4BC1E5F37CDFC0E69B8991E
	5993D8DC0769F863865FBAFE2FA31B6B125F695D4A7A7FD233527F0D990374FFF9A574FFA537F1C3A675A7DD3DA22B670EF6251EA7442154434A3DA4AA65BE4ABA28AA075F7F5477183B44FAF0780A49F19D39FDD23C3994DE0F086C8430F843968B88F5DC6CF7BADC508B474B4B7A4B4BCA3B2C1D9BB6EF66E3674F1D4AB85A582D34F949622173FA402A14FB7058504B89A715F24796557F87B23AC5492DAFDF21CDE9A3627850F95B5F236845EAD76A78E1C8F530B541EAC41F2198EC33ED8246E2F217E26CA9
	E88246E26A16925A49101E83A0CC44F1D432CCBD0E581A3C12041D64B35B273E5AA8F87153E0657D9CF8BC5CFE82769A4D66AEDE2677A899837CCC0A39EFF8E762A899577CDBD07A89B97B02102D27DF5239DF7EFDA3557B4C560B9F33CF3E28EDB0796F795A7B197C83DF6B1F49BF72357E7D3271DF8FFE176D192F3D5F390B7DDA7DFBAE2C5F8B1D24EF7240B6AAGDAGECGA3A6229F76CD8BD9DF5957B1DA9F2C4E52C56E8410DF8F13307DFF9D73AEEB6DB15F3FB9F67098D9235A692AC26AC7FC53B152DF
	1243C911725D20E3C4C7AE32B2B1D27C7DB0FAE6E003950E20D9FC431F46AF03C12E7009987378C39B1ECBC6F21C3D0660E2A11D40F1A6FE4EB117630ED1DC4173E9465D4FA36E7D639863EC6438EEB316151E4738C9897577C2A09D89904F7115941F8C6902C9D06FA444BFEE76E52324AEDF369829C323347D324FB606920351BB0FD07C5924A21C3B5AC03620C998535F5AC63E03504474BFF3D9ADB566AE9654E7F3DC45C61C63A6CF42B92EFDA37D0EB496765EG1531B84755EFA2F5B7C159EEEE6B171CA2
	7B79C620318184G0481C48144814C81C886487ECD1A3198208A2099209B609C00BFC08840385F60587860FB32A6F85B4F4A82508162B2DB4990117570CB7E5B939320AE61851C435FDE0E72D53E00722D2077921D10768388F9814FACA6FC270E6B8B1F5C229B570F7FEEC2FA648B2C5F93DED07C9E9B7824CC77C1A5EB840F6FD86A6C5DE4F0DA180BA7601D2D249504BFB23E33FF0BBCC72DC21C44F131EB712CA07E3738273538CD245CE4580FGB482F4831C8388848883081E4CFFF7242DF8903D32439EC5
	2DCC2733CC28538428A7E78A7FC6EABD6A34E18A6A74DB7AFD75C8C887830C1B02E3E0EA76706285B307D92EE06970628C0F52F5BDF60AB64E70944547CF5146993ED80A3AC9043C83AF72EFFB1D5E676AF2276F39FA17137E7E8B4C314DE71C72DD2EAAA771AD9C1D1D60BEA1DD1DB31B636A5D0D8E3715F8G721E32EDE02CB43317D8A80B1719685C998E7CC63E08637A721A016795B75379BA9E7293DF44FD6E0EAC53A0BA4CF9C93D4FF5E6117DDF3743E5BDCBFE6840D6ED33F65949AAA773B0AA8B6D2798
	4A313DC2EE9B6E83DADEE2FB85A7BCC73E443EA7E0BC7CE89E0C076081BCCCADA1EBDE0D4DECBFA98FB806CFAAA0EBBD6E57489DA2F69B8973EB4E11D8A359EB30DCF7D3074BE13311AD07003F75F532DD3B27A2BC063F04FB2ECB52757223DF427BC85FE72877BCF1320CC29C0C31B89CB7FF7438F3B4B47B97B68E2F5C2797DC0E0D0FE9F42DA809530E1BB7A83EB2CEBBEE3AB7623829053C30B89CB723CFF99FB74127FC0F1BBBCE697BB8E3CFF97AB8912774FD1CDF1DD27BB88C7758A9B227F7413A534269
	22B96E9847BDC36BA4382F2D963ABE4A5F8ED33E887E00C8377BB2FE8A652B0F62DA70BE328AF7CC463DFA0A0CFD90990693161DF10A0ED72B4D4652091446EAA73BAB24F90772E8F51AD5FC3F4FF147F83F5FB14C5FC6F8E8186512AE389C33272B681A1DB9D5EBE757D3FC5ED42D1D6567201D95C11E5FD4DCB3E777525F3C0034E19A2C1BG35GADG4EE97CB700DD032D154CFBD256CAA6579FF44FA3C7CED35D93047770E95EFD4AE8B9AFCEFE4E111F4953F0CEF8F215DE9B59AF2B6E0741FB654B5E5BE8
	10735A6427A4BF7DDE46B62668FE0F98AC53C540B386A09EA0C946495F533A3D0F5F53EE5F6377C7377E783D5C6DB9FEC7397547EF005BF37C0E166BF5718FEB987EE6FA264F623D874DF6F3176A9B76F7BD6E608D4D367FBA4CF265BE3FA1549F933F25BE17902F9D9371941F992F9D936675B8A672A06FDCBC767993E4C9445FE3D63DFF397462478ACEDC54B6187F73D49F11092D6F1DB5E4ED7608191BBBCEB25AABFED0E8F3324877E1BAF170EAB0DE5B069253D6B98CAE53B7E71179751043C567FF063B0D
	56210E15B5117F63965FDD3A2F6ED0F35D915EC7FB5B300FE6C07F643D02BE7715ED68B3B53E02BE774314568F52A1G112F20BF97ECA66BC31D59AD6F71463463EF9FCE6675C526939C99C7066938E76C2A637B16013FE5B24087E265EF4C40BA2B26A3BF4FAC215FE7C1BAGA0ECBA5AD742C64526E6295E6D9BAF3EFDDD799334F1ED0EA2D3106AFDFD4E45EFFB67C5EDC37EC634577B5C7E67DE5FF37BBFFA7567760527BD677645277567766C531E712765ABB17E94BD9DE3C352CA0CBF45F25C8B94B78352
	292FA22EB0176072A0DD49F1FD3998D7EA60387BF3BDE3D27194578FB47E1CEE66AA64A50463EAD7A3AF619CF76BEACFDE1EDB0D3CC4F35C72FCFED716633E487764C522B8A181768E89081B3396F9A966387AF5D8DF8547DD3A8E79EB633804F51E7CADB4295B1577756B95BBFFDD755E357E625B7C081F200D3A36612D65CD43AC67FF416558DA3F191EE7CB89DA9F201C62C7A4E8FD0047787A628FF9F39330BF6FEA47761BDFD37C47EE797D5CEB1EBE25FCFFBBDD4DF700CC97AABFA361992BA9ABFFBB1021
	AEB783E86629688C2FFBBB6F7FB40BFED37C3A76BB07FD94DF723A76BB07C9CB70BB075D109772BA5EDD3A7BBC2064F844ADABB1A6127A862E2133D612F9CD09E110EBE998E541F2C2AE2E49C53C5C4EDC62C01D96DD1235C39D7940B24D3CCC739B985738A657B4C8BF0FFCC39D5778BDAD0F7533BA5378EFFB853E0172FDA9A872752CC679C25F403BD9F3D7637A9D437978B8FFB0BE668D60636AFCCFBE3A57A29F29B3108F09080AF782578DD6FF4E8CD6BF2BEB6CBA6439F8865A6896FA56AB4775D56FCF6A
	5ECF796F4E8753AFEA9B72379A54CFBBG72DAE6605CBB055EEF6B07349F477DB39D6772C00E0B2338E8C847F15C8F343E7910B63C493F91EB4732B99C175FC1F0D510EE61388729FFE703F4A047AD21779943A09D45F1E717E0FDB35E44FE5A1BC57C4F4C37B0DDCF53A5BCBDE549FFC31724FE6F7D753B7FCA9B5E4EE7646FD9D7FA5BCB307C5C5C217A7E0C7E6485520F4DA5FA7DDAF4CBAC3DA68F6FB430740D2B8767AFBD5FC7FBB5071B1B8F9F646DFD160F7B8A167EF16D4076FED827BE9FE23838F50373
	B0355D7B7D5977292DB6C2BF373E05736B1C3401316D1F27C0D21EDF8E261B8669CB3F375473573B69A4E6466282F8DFA54500F159E0209D87B083E05EDB6C6C10158BEC9F587EB38B14767F5ABED87B19B3556D17F768FFFF70B99D0BA620AD1909EDCE4CA031ABE5ADE25B319A332B51E133F61C617A1AC547AC79AD3F86DE36A853D34EDD197C6E53CCA6E798BC23E47E18AC57AD99CC16388132D42D23B1F5FE97D60E1C8B78BB58E7BDFEE3191C434E01BA12E762BAB107289547D55AE0F7B7AACACC5B3773
	FBBCA75255B647567C0F8D386667A402AF1F087B5B0CACD22F76DC2E47A91DE167DFB522590E7BDB178157321109383FFD1E7AD4D1100E83C8CAC49D2F6CD06F7B19BC075294F9EAB3869F8349997F540CC3550E3B744061A3755F9CBE54DC5DF03CE95F11438D8DB555C70ED7B79DBFD4D7D7DB47FFB33CA393E5CE1D857A1D057B7C311968EFC833D00ED7A8EDB824E3G92E6219C77ADD15FE1E1659F643F71B60F173F191EFD154CCEB35682344E46F93D4C604B0715E66B7902FBFC16537E0697AB57C74B85
	4C769E6F548273957750BB81213335BE73E7949FB1DB6BB3CFB6234F9C85F92637D1DEEB6670646D73D9CEDF5E881F657465BDC8FD6011EFEB65BDCA7181EFEB65FDA393658D06BC18F2E5F99F71781D864B9CC53E7EB95EFCF6A37D5D857FB95A7673A8BEE80E367D5B7859EAA864995EC9B3325F3A09DECE568A2B5DE6350B7B58DF4B9350F73A060ED79B500EF887792D25F77A587BC1557BB41F77B27E93E0ED7C7EA7E823E909A2D32B6AFDD66645EF7B7FDF54B664388D1DFB7C21BFC35E61F7FF32515F0C
	617D3CE219A27BDA557BDDBFC17CBA7A2236416665BB8C6A73BD363E5023C2B9FFD616675D75E43E664E81BD893F03FD9440EEG6207335F6D78B19BE91A815F8D60879086B061F778F7930A785D48B900B33D0BEB691183833E996631629AC77F00EF06EDC6DCC7DB5E65F1EBDAE7G2443G225F45356A0EECDC2B625F45EFFDDF4A56FE6BBBA5C57D2D6E42255AEFFDC9F9A5FF47D26D373E578D4837AE55FE6B0B65D97FFEEC9CDEBC27F21865CE71F259C9C3DB5764B8D0B651E9D112C79C08624B12BC62C0
	3CDFF7C3DEC8920E25FBAE785E8D9B17FE341FD35FE33E043CDFA7D7D4D09F2A77A70DBF41FE7D223641465DAF0D746FE574DB5D6C6FD03F5030889A8FFFCFD868F6894BBCFD576AEBDEF9B97CF8EDCE0A10D2AF24F553F37EFAE1E19F39B0506F045E88G8815048FCD121059546D68B311DF33AEF6EF06D1AD890B9587DB929651ECF227DA1DC7AE47C353D983DD4D8F6F483DAC7B5AED3DA902E13F1056E936B93B4D50AC7D9B8C8DC2C6BB8D36135BDFB6C94826FFC9E38B145DC24B6EEA90D2BA5C12102F60
	BB881E65B6B94D9DA210AC3218DDA7E98D2704734262EF05C51D2E2EF65A00A424FC3B7FDBE81626C8F6E60330D86AA617DC49AECA92F2152A9B49F93A1C310BE8C4907E817FB969BFB7A9CC7E1181DB82E29A391875F4EA1CF1FA5B4E85B3EFDF10A22402FA59F1C33D10CA4E8CC0DC97537682496124B5272AC1D3B72648954A82CD4AA594F6B8FACA2D6DBD253D27FBCA4D6DD229E8014D84B9DC3670FB705FBC5D9659BDF1450694E1E13D1051E995ED16A6FA933EC1482629DA515D6132BAC9B8D19216B195
	10BF8333C57EB3B08D42D2CA07B768C9750411158D42F2D5F57B2DB6512EE4E529323EBA52A089D92CE63BA8ED01B453690712303134347568BE51E6B3FDA5D9ED2DDE3EBF1004BCEF1FA61076D631AE49762ACCBD7511548A4644FA9DA686FEA0A089B9FA5F8D10BA56B9F5AA76410AA4AC24173DA8F611ECDD02ECDDAEE14900D35EE7EE5BBEBB7675236E85C2DA3D300ADC0EEB92DD6C6FAED948B8137F1072FEE1115381D6CB8297E0ECEE7AD868164E58202BF3BA0F10BF3A6816C48B7F81D098CCABD82774
	0254DB8E185D5D92B9AD039ECF65BD497A293A0A72CEA7EA60EFB9650F14AA652516BFE65C387E73444885C24AFEE1314DE1366CB7F7C88ED73D30C86AF6096EEE074D526F5C4BAAB274619D52D428AE1F0BCB04ABA581EE4E64497EDB7C174CD9329F165AFE173005D212C1D94AA9AC7B3A4D640FCB092EFA097C49EA072B0867DD32D65A741B4FF2DBD3048F878AA5B1857131CB7F6E51FE60DA1A7E5E44B12F6E1E1B84C5764BD393B41C2DD09D6A6DE997DDD7040FDE1391B863EA4217132807F665A7921956
	FA2C36B370D2CB3FD163F8F61B0AA63790ADE046A249E5363BBB9D2E9E604624D185E6F109CAEF18D8B67A6E3FFDC3A5D20ACA42BAE5726A929DBDDB2ABBBBE1F342AB008187552F6155931AD28D4D327E6BD6FCBE759F6DE0E83542021E32B27A7F2D747F65647FDBA9E6AB45ECAD075E1CAB8C7ACF1B5F5FCFE42E681704F4F66F311C3FD570372D12E0B0497FE075A12B0C0945F1F672C13483A0CFF7706060DB2C9AFC64DD7EBBB2BF2A3FFD0E3F7C812F3FDD93BF0F79BCC973345FBECFECA436DB7CC11A31
	76833C27352A117CEDADEF5F54B47518EDB649ECBBD9ED37106FEBEA4EC82272CD478B0D24FEB7524819ACEFEAA3617B042A4C7F82D0CB878861F84EF6BBAAGG4402GGD0CB818294G94G88G88G63FC71B461F84EF6BBAAGG4402GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF5AAGGGG
**end of data**/
}
}