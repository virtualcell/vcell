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
	}
	private java.awt.event.MouseListener ml = null;
	private Plot2DPanel ivjPlot2DPanel1 = null;
	private JLabel ivjJLabel5 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.gui.EnhancedJLabel ivjJLabelLeft = null;
	private cbit.gui.EnhancedJLabel ivjJLabelRight = null;
	private JLabel ivjJLabelBottom = null;
	private JLabel ivjJLabelTitle = null;
	private Plot2D fieldPlot2D = new Plot2D(null,null, null);
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
	D0CB838494G88G88GE8FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8FFCD44535F02A91220D9AB4C8C054D8D131CDEB5A52163EC77BD14B7BCAAB7D2491DF53A7367449FB9F2F453E34653DAF6DCB6B4DA6C896A3C4099ADFD1A22206928304D05390A4FFB141G01C40892014D6E4D6EA63BFBB7F76FA61BBF106F4C1FB3FBF377EEB624253F179F07FB674C1919F3E64E4C1CB9B3F3D7AA5E113AB157D2ACC9399BA57E77EE2EA44D3FC612C66F8CDCC0DC755B99D6C9
	7777BA4073D232A59F1EAD901733B043DAA4A51DAF00F0B704575E1DE1FD995E37C9334F0F0D40CB14F2BAA1EEF64E6EB31317333B874B59C443578C9742F38B40C54011E75A5B247CF943A59C9F4F71883996C97217911A2F8CEF6738AC20A9GDCB9C0DD99236F02A7E3603A1AEACE57BDEB213461FB2A65B9120FAECF0445BC6DEA7EAC15FE1B9B3F1B1475E34AA7C9D38C61B6GA2FC1E7464C942E7EDFB3F73BDFB0FDB2B70FB942D26269D7EEF3779E4690FA46DDBA38514D61216C33AC40A1B95A479B541
	FBB70076C744370688FEA53C2F831076208C075F2E65BCF60DFECDDAD5694D5F9AC50612BD0C773ABDE199D677BFE1F90556375BCE789D0338D400C5G19G59GA5659956BF50F676777C911E75BDDA236275AA3EFA3F3F4163D00FAB8E59836FB5B59043F127E53B22BAA40935655A61F293FEA683166ED7D71C0E55494283582EE9273FA02D7E72ED16CDA6F532FA6128E5DFB92B0B1672F01D3032770CC4AB5BD5BED16FA616FDD652179DFA70313C0DA6E5A74FFFACEF0C671324ABFBE5B9E96B0D03242D53
	611D24FB1634BB45EF237862F742F82603F3284E10F09544253C033A513585F9E94B7F0CD434FF0EE9BD24BC14E849F90771D0DCE16425196AF7DD85AB738D414B850ACF526199AFA7291E12709A084B591BE1E57A254A24CF166C4F30D6813481B8G6281D281966DC79D7BFA4F5EA96AD83BCD559A02EE0FC3D6A516D6353F84CFB9242936D6DFC033796C72991B6A3675F8640044E9C66D248EFAA6240B233AEF07B25E17FD32EA535C0A8FF237F9E5CDD6830C66FA7B7304C615E9EC275BABC381DE3F04794E
	32EF0327479650BE74BBEC1A9C26E071F36D24CD86EDE7A0C748G5ED93D5C55CB6AEB95483F9EA0C754C331036477012C7AE4CFCDCD1B2274877D1DC492292D1254F3839DBB0A603D2C926B78F6A741A5C1F8E1A56673016385DDBECDEEBBE15F268EE39FFBF69086ECE989502F8458GD0F8A0433A6BG6A50AF3AD0071A1E7B3CD47E58ECD39DAA3863313C6A83CCF75A8F987B52AB348C42E78862D2G9683E49E40317E0EE057947AB661CF9FDE4279B552957272F771A7AB632BD3AAE323A11C47531B7575
	AD3C4FED91EDE9F513364C06F20B2BC04F2B308DE6B8CA19CEB75882B2EA71C99594D0F604DB04717941F0932F5FBC4E8FA96BB9DE56D6CAC37214249781DE66E513F2DE677DEE8740C1A137CC73EB83DEB40079G4BG5683949F4C305682B8G928196812C86487F934C17G1DGE3G8BGD67C895B6A43D0D16EC47E30BE1E65FCCE4663964E9F729A2BBD8ECE0166FF82303A19CF6B866B8575E62A6D3705574B2B3C0EE215B9157A7B1FG2C8F0C49C4F6F9D076E9CD32DF76B960A540632E15C99D71F13B
	D791E92E27F8BF0C730DB03086847EE6195421CD53D4F7CFD013BBE4CDF37B1C6178B9342CC030479F1EA2302CBBE552B64A900C73027E9E190C11F6456713E9E794C9F87C3DF40C16BD329718EED402BE0D61BF57CBEC56FEF9184F918F76BEA311B15E93A4738D338353ED245D7D2A5B8BDD1CCCD9D2533BE47C7EE824C4D7C79B20FF91FB3684624A5F4531F7688D9CFBF70DBCA0AD3D6037185992A9775EECC9FC170DF5296F0647DE46D32A531C777B68DC417181466B31538407B6EA9E255B769651530620
	26A93E77D5A568EFF48F3ABD6E915931DFE23A3ECDBA54C1E4EC34290EB65B30925476F37C2DA31BC9BBC0A134DA77A37DAB0324EF900B3832E9BF9FBB9B7A881FCB017FD5G6B5F4531F3270B44637C45874DC645437304382AEAD21F97699C03F512DF4D6A133CAF217109A2FE8C4249917116A10C4F7FF306F57B1F0D71770978F208EB5245B3BE373A0B282C83C1994C8EB2D1FFA07462A5778B0638B3DCC7D87CEBEED2FF9E5D9C1F79E7DA26D847F4C258831088B09FE0A940EBC4169AC2B7B802F43BEA98
	DF35B50C2E3D065BF507885D5ED124DBDE436603B51CBE1B53317CFEA068AE9EE271A1F8A682249F52535DBD06F44BB85D6AC3EC0E5FB0016E5CA541DF2DB9DDF2AD215BFA9969EAEB597C5489CF87C983D0A1681E90F4EBEA59DCC66832F5F42C7E6E99C73A12439956FDG1D07197D61BA2C2FBFD5C23A50E1E61B38B8DD52E1BDFFEF65A25D0A43EC3EDC4B73A57461F233845D0643CC4E427742793EA6727B2AC5544BFBAC3FDACE57769E4B0F355B4CBC24F33D476AAF0EBF135F5337EFDDBE52AD66792C64
	F4EBB89D63AFDB50159CE1717B0EB0FB2C6988630F657750C621FFBC5EF3E4223E30F163573B45DA374FB6E88BD58406E0BC2E29B9C606918FDB6BFE1B5A2B245F279D41B56DA1EAAF2D05F08EC0F19D0EFBBFF88E5734497D8B254E74D5F951566565F55C662C8B0FFBE509C3F9ECCCC96C2776B044255661F8B2572D372B6B8381456E26A6BD6F0BADFD7AF111610E7711F12C572D8634D61838C258EFBB7A883F446667E8DE9F773D710624DF7BBB036E1A9A33A1142F99AF9D554B3BE46BA54B33D164DD4465
	DC251337BA71BF73585A6B4B034297D18FBAD40F3E0802BF3C22EF1F8A5B10D6D3939E2EA5DC3B5A8FA09FC9A377CA97E74F0B4AC7FCBDABFFC1FD180FEC6BC14E476E21C2090D752BA13E3881F8E9A0785EA1B2DF123E40683EAF6812A0FE39205B9CA2F4C4A7995DA343C8A7B5E6D82B8158187F03DC7D186F87FCB240E2GB64F7CE9046423395C81B6677EDC554F39AB00EE83C0C9934C39CD18667C881199D6EEDAB050E66B8175E5F5732B7ABACC77054FC80BB7A5DA4C7CAC2E7BD3ACCDCD2CCEBCCD613A
	D97253A7ACAB8118BCF70D94F2DDD982B49BC4594F0C927B014D0B28CB9D1524BF86E42D1D224B5A04B72902E18E914F9A6371732F69F9AC30DDE74AE3623FB9AD654D0C374EE61D9EBD7A04256DD16431EB94F91C8FB42B1AD1175EBBC570BEC5755ABC2765C05023B17C7CA6D2D6102EFDE93D854A00B14657E73669792A9DBFEF4AD742D6172570A863276A284E8F117104E5ED867275DB41D79C50ACBE0AF5E79B53579DEB6F4FDF5237F79650EEFFBF43DA875079BE263BE1C2BAD6467746D05FBCC037C050
	6E3FC470EE1FDBF35BC87FAD5B4DE45F675B89CF1B43D156269B7FAA6AFB82E0515AB55673E9BB317C63AF097E81F99737201ED7DFC6FC9B4135E039F7DDA6FAECD31D7B195E4F99506B7D129626776B857DF7AE235E337A9F7B886BBFEE45A229E45B0D2675CFC0EAE575DE569A2E7FFC505B92213B4F0CA30FA120C9EF453E3848EB46533AD614ED05C817C3529C0339063651105BE88793BF018762630E21BC87A452C69EBED8B1DC3704B2327CEF17D0AFD2A05D4AE3282F4D4F3EC37A0FCB99E20DC3863F0A
	C64527595C3084B844F09E9BB4D787ACA7FCCE5ACC684B4AC51E8BBEG3D79G7979EAAE368723FB45A26A84E852855D5810D552593FA25F1F887AF5C0DB5A0674C773F83E15AC6C23731E0DA6A66EB01B4FA172C84FC3F93BA0FD12486365FCB2FF75G399F9232BAF8F94F0BDC474904FC2101C716FD2C13551D0F79AD073C2463185F4AFC2CE71657C31B7768E55AFF219322A66ABD53F76A32A3354972FFFDA34ADC8DF97BC57E25A1FD7FE338340D935B76679B11370510EE0DC8FB7A19CAF85ADD50FAF285
	993265C67A6EA85333C3571C95AC26193C6F46757AF26163ADB941743D7CC4581FD427FBC7E03ECF192ED5BACFB05F27D560D50A776B700C7FFAEA6710F0A244E51DC0FF5D7DBE227B1DA721BEG92GD28116822CB909764A63FD8724987E50D61F86660C4D53B1E1410BFE49FED2D730C66561597D249E699AD5C23F550BFE52F7F3204C12F6308581DAGDC6D4843F6CFCC9E1AC326BC30FA7E7A60747C7F5F3962F45C57CC47478460FF81409200D5G9B04BC3D1E72D872205F188B427859BBC452951FB277
	7FC71763B0CDD7FE856958383D7BE37D7ED47E7347AC24AD4D466DEE1E37671451777A68B4F97E6995734C52BD96F34FA64D10A7CB77E44CF4AB8D69785A23B74A5A039AA1EC0E796DE533B9E69D6417834056A78596B2EE12713678B44E9DDB4F7DC1B25F432BECE2CB9B167F173C467C491AB08479A41E467C5F8860DE5F0253387E39A1C0460555905EGD0580163C2F1002C3BF6C1388E208BC083C881D884105901FA3C485390CB0F6B51C956290857C623238B5B605E08BD2F2C0E09F5EE5EF6DF0EC8771A
	A1DC58B9B19D1373FA0D4A89F1F5GDDG9AC08A40A2004CCE1473996F21D8F252019DE464F325D1C04667795169496709C8D7E188E71B4AA78D92790ABE047585C09340C500F800D400E59F22FCE73D0727A8DFA360DA94255F2489B92F525874E45C92114E5806EBBFB46BFFFBDF5B91350FB459B41B3E1FE4C778BCC9BF4901FC0B4EE0BF7917CB58CF4A4EE0BFF91F5A4597A19C82C8BA03FB91ABA3F81E5AD873631869961EB1CBF7432569151792B35DD253725E3BBC3D725AE2264BBCE356163FA9B171DB
	9C6BD494CF03CD6DD41CCEB29F925BCEE2ED75FF868AA4361711F586472E38A122731DE7412681C8GD8G309CE0ADC0C1976A7CE73DB147AE3185BB15312BF0FCFAF5755FB15315F6CDCC47E47F3937C0E2FBB955DD280F995364E1CD4CF473CDF9F850DD405BEED19776157F72103E329E42799F41FA77A3646DEEE9FA6342039169AA8C612A0F4C52FD18BB3D3A3047CC57E6DA5E8E4B744AFBA7E6BA07E9F97FB24D72FE9DB35D18E9F9DF4D1BDE7BAD0BC8E79C5713CD4BD37227D75E2508F4467252CC4BFB
	FB63746A73E04CF44BCC4B7BE5C43A294977F4C43A8AC3F83521BCF6DE63A7547F3D81620ABE4E30B25C1EDE025B8561EA01ABF05173F59056BE463E5B5FC77616D2A01C8E308CE035088B51388D9F33391478DC0A4FE15C7BEEF26601501A558B49ABA5CA1C283BC962D99997E99956A8B4ECDE193CFD4C4ED7A03039A25D97FD6F78EBBE733D63EF784C770E3F6DFB1E61A18F7D89A20CFF58B7795E72233E68FB4B2F7A2373394BEF4E679F7D66FC6E73CF4EE735FFF2BEC7862273722981F3DEE68E1873F263
	40643C4C9E08464B2B347D7F117A3B5420CFF3FBE51AB5538BB9C44EBB40CCFFBA68E3E6G49BDE0DC03CBE8CFFF136E8BD41FC39B22DC2D14A2ED08E607DBD3D44C24D33629A7FBFB05815161176D48577F5508DFACE25F7F84D89D927A228AC72C1C26DDD35A491EB71BFBAB47F16E6DBC07F3EF897597A6C2F8814092518F1F33181DBDDAF5AE5C37561D0B6CE7B900AB6A1E085F85616AEEB3DB60E7DE64272D9B797912C278090770FC00C55D48CFD9E032B1F3C5F7A4AF4C3F75FB3ADEDD534D7CDB39823F
	C923B6078E4F7CDEC9815C0F4A06386ECF305DDE8BC4574DB783663A39A7C0566681A5285AE5237ED586CAC4DC58C0EC55E4AFE9CAC6F3082657EC2AD30E505F861A1E45C5CB7FC1006C8B75389DEE15FBF3198EFD77252D34EF8482F200AE524822076B4EEA21BB4D1AF2FC3FA86FD6AD3A7C73B4F379D33568725F2F4516BFDD0BAE7F57345872AF552275F1A66B6CD11D2C0DE1D9B78FC6177579C1F3D9DF9E0CAE6BEB0331E5FDFBB03A2C950331E5FDF7F0F2D95F9D5337EB2568037BC6318FAE7D847BE0B6
	5DCB4A06F051798CEB59F96C03BF1BF4CDD8F7BEDA9F7CFB3A966EBCEF6C034B589A793C318F9E3F0CFD5083F1AB4F236FFC53A53D8CD5C236EFCD409F90786065E7B93E3DD7D53CB092F27F62G995B2AAECC643705073B79532F0B0F07779400B4C31A2590DEE540317638E1A83A8E4D9AB25721B9C351F5684E2158BAF47FD0F49D7A42D0EC9D7A5A10F13CE07558B8BE319EABF9BDE6F9C9BD2EB3481F4343C5977919560B6178EAF8EF3B9829A797812799702CEC2B241F4B085E323297390D3A1820CBBF5F
	248C26F30A171ED33F48F4EE23400FD07CB29D1E69E22E9777F1B2A12E56063A18A969F56E2060B7B1F7E2DD9D6078B3963D9CE19D5D1EB7113E0A631F4A530FCDE17A7B727578F09FB83FD11FFF98DF760C1E3ED2607FF304680ADF15FBE5D5159D9D6E91E2BFE45A6819A6BAA62442FB1A103721D73F1F1950C3683E560B3E316D902E8268867077B0DFB533173EC8F52AD735F9E1B822060C43CE526EF0E15A15C03F96A09F703BG1A6CD866E3F61216F945BB5D9AD8D58CDF5BC9F4001D5441F3FB19478B85
	2EC151B4454B70CF3C94463749CE6808EC6FB72D4FF88E03701CE647BD5EC627197FFDA9442F3223CD70D0086EF562184BEDB0261F6FC678C7565B43BAA9B92269E7A235470A9DCC8FB789FC8A45176B70CCBFDF23F892EE0238058EDC136D25FB8305F206F53B0C38E40AAB03F01D40FD7DAC41F5C258AFF0D554CE0C07F03240450F639AEF2140390788AE9B42C53DB8C77C3B57A8772EDEDD7F6F0DA677DBFEA2C7DB2FF1FF72G453BFA0D7B130B955C1F0C0738F53D485311G725966C41E7249F1A15EAE4F
	695EEF8BCC65FC7DF4017B227E86E59C0C7075071C61FACEF4C62BF38FBD53116AE4F51BA77003941F2E4333BA7F3DB8E3329462F639304EF3AFE31DA738304E3FF5B9DC4F5F533DF7DC3E7A7561392AE5B0197F037A1197013CABC4BDF42B84178961F58277AD0A5BG61C2B7626E7391DCA904EB85EE0BC2F0AD906E963833946707F0224059691AA995426982679C407ADFA6F0DE8F419577E5D84B7B9077028FF92E9338008FF9EE9338D5BE6439CB605CB66459A3F06DCE64A5A4F01F75881E856E149379CB
	953827DD48DF1640BDCCFD43797D995612FE4425FBB0EDEDBF6AD61056018B42F18297C7F164EC9CB9AF45F01B28FDC2E8223525EB12382969DAA9AD43F887C78F31FCC913C1CABF39F7FA6F38A4613CDD5ADB8ACF1FBCD4AACE86B5393D320FDC50AA2B2A5CD7DEDA35EF5FEE36971FBA245F0B2F5EFC0D69D9FC822BF9BDE471E74E0B7378DD511F7422ADDE664930DE74603C7E2F95E21FB9FCBE33C1A1E75D7104A6DDCB3CCD5B1E24CF71607A62C3FA3EFFA5045781C8DEBCC39BFA8B4FF2265D779929A54D
	7C8C6D1A4ECB1692AF6333569BBEBB10557F040515596C42B2C3901F6E25FA6E524F7724AEEF4DE3FED096FF6F04F83216A2F6F9B87E079362893F6460D3B85E6A564713BA1A4553B3599EFE93E54BF9B8DD72FCA5BE6A3D5DECAE53F69F3FEB39F6EE6E5A47C77218ECCF52333E44AE6A74617E63289357F00BFDD8476768791DAC8897A89956D2857B5389BA0FD7133040FDDF42BE66D7306F5CC26D0A7990CE87D8AE70F7D2BCB9576F60675799FED76E150C391867D4E8670B328FDFD1997A7BCC31A0CF742F
	23746E38860BB356777E01B4910F1B51CF4FD4AB2A6CD415A0B958466C65EFD03BEA8D7039DE91E7504EC6372F7F0D5ADC05FE98677CC87F41F8BE4D5B27B19A9BEDA742DB9350AC7453730263CDD31081C103B489GAF723A186F8F2F33D919F154B687435C6D8DFAA9072C6C94EA57E581FD0E9FF564999F0E4DE583080BD3042DB500EDD46EA73EFDBF0493GD281960B38BF533815836C1EC796BC0BD40C7B26922DEDC9DE781E40735347278E4456A3D6468F2316219F23717E22B1FD2CB21AD4D2A7E78225
	13506CD3992F2C7E5AC631CE93D52C537FBD06F51AAE6AC63DC4F4E315880F51B0395B426D5BCBDDBCFE2AFD63CA001571AAAD63AF194BE257DD496529549D77F12BC4F7B5C50115213F933AAB1FAB57D9A4C193D0A77665127CF0DCA6CD6F35058C3A1FC56D35ACA8ABC7453E798D1F5919D61667210D61BC5F247D011DB3159DAC3FDAC51C8D89E0FEBEA57A58B01352D783ED13209F7192465D3008E7B4C9545671C37CD8G47064B83130D8D38270510209B9B48F81099B00E8D719E7331E1BE35475682FD96
	G2B037344B5406B63D3C4DDB86D6F681831DD03B1C3C35A2F97046307693C5589F19781986ED11A7FE288AF53D0B617F3B259B64510ADCB8B4B464AA823F6E68E608B02D8477F6C0E74EF7CC61C732E8A327BF7ED703C98E47B16CCE73F6319CCE7C3C13D4EAE9AA6738773C51053C608F7EF8A6329AB1E5B8C0F1C535BEFC573BC961443D8CEA2AFA7A5983607587E53672CC4569500DF97C43F454F4649F968B3363E485DA7C0362B0ADFD635E10625377E707CC06F744E447EFA1A694A1DD71A0E2DDF3FCB6D
	4B02C1233FB903620B870D7E66F3CE34F1CBA1AEFE904F85DDFB0A347FBA88678F413A82208A209340BF04F5F9C05AA745B8935429785B64C1E87270B6A4136F18EBFA5F8A7956A634A5977D5DE72545B31A225FF99A62F71E78336C493939644964FB290348378A6232G0AC330D6842883688A21FC2392BFF7CC5421DE53ECF6973DE5A6B6BC5D72D098CD9CFCCC36F5FDE6F32DA7941E2362C26673D532813F1007176870FC4D97219FABF5B4EBCD72E7FA70887B66C14868E3FB1C620B060DBEB61B9BFDECE5
	9017B80CE306BC9EB9E60C0D63181185F485A3B0D6GB40D30F38EEC4CD88B7DB3FA9BF70F68470C271F273ECE6A83BD48F177341371020CA0F8340D6F5D6DA0F8B202A01E0D398574BBC60C660BE74959B75D2D920F22D67410BE4EE8771C8D536E9EA767E0222DB52B4ACBAB776DC65A5078CEA36DD1B0E64BF6BA5CB60F625CD9DE8ACB537D153B59F782888FF4A220FFD9C7A2F1487BADC40D385E7F349075FE7B676E111AFE58E4F9A30A5E7BF99DAE9D890F07493E52BC16E74EC7E199C78626A2A3EB5B3D
	BEEC5B6C91666FAE9A4D306E9AE5F772EAC7894D9C2544049F84D6E6FD53E65126EDF22F18871A695DC25DD96D7D8C7F36AD6C87BF6DF63AB4CE7FBE7DEE8D43B31FB773EDFB7D7A35E8BC67AFFD946FBD9D564C621709786343C65FB8094F9CC53D8F84085EB74A648E87364D102CEF1B6DBB6AAC4FC7E91B75232CCDCA4642EDC31E2C0E0323D8FFEDE32C0E9D708C0DB13F8E2B63A746E25771D376A2D11762CC6FFE567E0EF361763F5DB7C97BD7D6C37BD78B9D6FD028BD2986E5164F4C6EF0BEDD5E296443
	52FD65C2B8DD7B40F1DDBA3C7F536A35B965D6B0784A48F69DB9FDD2363B82DECF4BEC8730EC67DE8788F5C18E799595DA61C2653ED0653E8A273BF7676E5D226FE4DE8C7723CDBD61B267DC812F876D61F467EDD3CF7714A31CAE59537A97489810350039FCC53D61B20EBA274E5B81E738DE4E3842F93CB525BC785D30BE349912C0B7170F21AF6611FE74BDC9175097733BFEC2DB8D61F6G57A56CC7EBBD7AFE446664BCB7C94A665E5F953CA9C53153E066BD86BC1E6C690335C6274DA9522453BEA5762418
	7906E7A62C24DF0B3B20748AB93BC1486653C7E83D3077CC5D3B4DF975FD76A3D7358CA6FF622968FB72371EB25F131FFB4A7CFC59DD27A6BF4BF67F2968E76E3EF0CA7FBD97566EC5F4BFB28EF4E3BE40D200D5G6BAE219DD6FB1A4C2F6CF60C5186EBF6B84919G7275B08D4B5F5191DD5637BAE2FFF3ECEF871923FA682CC272C77C418E52DE1A6227C8062B69A0F52412190911F2FCDD87698B9E1861881A79B7B6537E11FF996C4D4B6873D07AF1DF32D4600EEFA638AA88B789DC26B8E75AA5F04F5335DC
	BC0497885C9CA77AB8979B68C85A551751EF7298356B8A4621DC003AF1447F14629D900E87D8A070CEF7AC9DA5F9455245D2931AF059F1B14B180A8F46ECBFA2EC4FAE984731EBB914AB4521CFFF7FC5F28F426053FFDFF5BB9AECCE6E542763597AF19C632A63F00C7BEFFA17598F61840054B89C63F26907B5BBA02DCB683A2F1F2C6797834DAA00EC0022CF019D85D08B508E60G8881A481AC84D8823092E0AD40860002EB409E38867B468F3B481C90EDBD4B85209DC43539C9976175F0560775308F72F2DD
	03E378DDBE14AF759A146F7B14B69342D9G8557621E4572CF74FE7D52BDA7CD7D7A591F0CD8CA2FE56D5EF4ED586E09831B1455FDCA792BD244BD16E61F0BF4CE87B371C8F1141785FE429F695F21EB11E7CDC35CEA010B9F463D023AF8DC531E721612F490CE86D88C3092E0ADC08EC051F59956326B30DE6BBEAE1B24DE59E6CF38DAD91D5EB10AF55A84790CDDA76EC80DE21DAE3A8E6BF49B5DCFDE8F617C99501EB3308F5C58BFBDFF416D53CC376A0A53317562G3D37D2B54368E79821783A99C6BFC31E
	876B268D6252E7E0BD1774C59F2BF774459E2B4B5877DFE00C6D9C767333DC6F74915BC2696D8573C91CB925676E992EB5502E845C4482E07D74757E093E525F78083AB1FF591DF4FCAE1E896364CC6C570E4D9377ABBEF9075054C1FC5BCCDC677E3B2BF412BA9C1B29DF671651EF5C389455BDC2BEF4602977381DBEB24BF19E9A5C28BFC9100E2D95160AB3BACBE73235C2A6BC574FE47789988FE56E49F848CF507370D8B919739ABCB6DFBF6FF08C3FFEBB196BF13DC64E903153C8985FB0C6FC0DE42D41E2
	839DF6D571F868C7F371DBDF4591670D880F45893866DAE89ADF160067116E766957BC3589AC5E854F31846CB71F0DF9A74BDC7F97CFB35DAF2FB89D6B9FB75139A2B94158EF66D0FCEA02315F14CA58EF52A02E68FA6CB75774C76FB75777476EB737741B5BB8A97D11B64E9D7D66B64EBD7DFA9B076196526F95B8E15E6992F41F97380F846ECBB4CF02BB6BF65079116197D33E883EC5264BFD0E7FA665EB08623A70EE088E779147AD6FA7FD9FC4066E04E93FCF7BF72F5B63E1619FD09A370F2C2E10669FA9
	0FEE3FCD477763827711E877CFFC5353177EE926DBF0456918BE5553B3E25A75C6BDBBC671F1579B756CD1E18FA4C2DC5675B8E7BECAFDBA1DB7E4D87DG49GE9GCB81B2EFC0DD6C77CCB6D7B26BA9BCD7B2399E701B59FA6BEF501D937CB47473CFC737A94BF8DCADFF3673276353B8A68CEBE6E504F4F9A642FB6AA4E5AC62F14B79F3B5FFE609B22EB77D8EC2FEA2777340339C208E201363D81DF5FB2377DF3BB7F67F75F84D7B2F6A0D6C3FC3DE737EFB499B59FF2F61792A62E28D43A7F809ED477C3D47
	EDBE1BD3F7077D1E08B3F8D3536DAFCEB35D4F2EB89D537D195466F2A59A7BC49245EB0946BE714A987609B808DB1B08EDBEC44DD13EE6513DDBBDD75FD730732A16416C1F878708CC5CAF3B194C4D91BEF31B3D1F5186064234ABDC64FE18099F3C9E145733333CF4BFF70363BE361B0C2FA794150E7F8C77117BF5DE9E7A4ABA487F38448FD4B4B61F68ECBEAD5AE81D583FEA39B1439AF7A35A5C7B44B92845B7224DFD5EC54F85C13860A63067EEC2FB2E59CD6607E6DB002F71BE6843EF9FD65F04797D0462
	28BF64A6DCB3BA1B453AE56237CCA658C0AC7D0FFC1867421B101F0281FABF8B4259GC5B723FEA5CBE11D3AC3777E16F475756B485F200C771530CC6D3A779F792EFE593F3A2AE53031F23592FDECFFD209BD362FD74C4776DFA911E37B7FD34C47765FA9117E27077D68FFAA3B99FDC3658368FF2A923899945782E10F40BDCEF1F190CE95383DAA7A1596891CD30D74C9A5D15C9A2059A068EE57101702A44465E848CB31401D89C672B29EC4DE4A845D43C1F1D6D6607286A3F9A927B897500C893A54E164A5
	C960F2C7B03F7982D7B3027CAD97386411C87E3EB122AF172F6B4742FAFE1B6EFD47585557796A3FC1993F3862738AACDD4EB4539D3C6273837C9C841D83D6A799ED0047A9FEDD125186F038F0FE59GF1DD3330BD5B7B307CA533427663CA7E3EF6D624CD49522D6D53731D4D698AEF61FEB1F8D65DE27A8D1C896B00962069565179EF09365FBFC6671878DB0C779CE6D0FC72AD46FB8EDB86701E438208AB38954FAE5DC6BE174B7D919F8E20CF44F3AB4E21F78C10F1AD6C43A04752504B0269968F601C1CA8
	527D0A5EC59F14D54DED57FBBEB0CD210AE9165C0AFE0D23EA69A46D3C7ED63DDF63FE1A9E73E7F9FED345F3E9B9C23E77C642723D99C4798A1371EC5642A04E5F65494847BF87A76323BBD94FC763E0A49F25434807A79979300D92BE7819401149729F6379333C6CA348F3D2B26AE8A65D5F63FEFD5DFB1F6B6A0F8757DE55B278DCC3ED32F410F7C5B20E3D5954D7368642D98277C99F0E65B98277DF94D7B69B660B59085BC6F197A16C973810C8BBA6F0DF24380590DEAAF07FC16D3BD5904E9138F6BA8797
	5D16E15DF51B189B7C18DF4BED62373386287FD10493E9B8D9040B877E9AF5C9720F5EDE6F7CD54A0836BF437A406501E8EB89967F4840D4EDFFC67F06FAE574F6FAA6762C9C50384FD345B38DACFCA2B8B9FF7FAB98233C06131D1DA70F0B721E8E623A025FC39F1ED85E41917D7E903FC3BBB2B98F177B221F1F1DC375F2B13473325BF0FC5D5AB751379DCFED8FB63EE8FD251334E556ED7A716B856AB3E3FE81BC2F323B9F7D3279F3B22C5B81DAG3A67303DC3166E0F06726F3F94AE7F66C94B5766684BDF
	5EE7FE7FE08D15B981E81367E019058A71DD0567A2369CEB30296D0A47ED9F9675D5C67BA779165FA2115629C44AF9D91CEB4A66F29625C01FCE61FBDAD416D57E49E429CD514BD237037A5445D9D86EB91770BB585F98B05E3FA57B306D1007A3856789F62F101F77F1CFF6F6232A3C7460C1F10EE7D61FDE67581CFF2A9F677CB148FFD18A2EEF1F5DC272B56E4BF97D5AB05B7FEA10EDBE092FEF677663DC36BE8557379769DE722E39B04682F44E45BACE6B532F7B19BC64A4A972B853B7F99FB8227C49508F
	F5AB6E0A161327DBBFBEF922333E6DC2C7636913EDED8D7527AF54F7DCB8515C5C54AC3E99FE1F02B2FB002F85F3F11D7F8B856D0D55F3D10E6D749EEE713C8CEBB9C053BC146317FE7D9996161E5D0DA472F54F4374BF265FDBC906F09A4032F9B82E1F0D397736FA1E19ADA847CCE77C06973F3FC453E54F0B6E6FB4823395D751F6AC3C5DE8B3BFC171A5379BED66DEB75A4C3BA0AE61F614B7CD191E3CFF97B31D393C3F0E194EDC5EA45A5E6BA3641DCB715991726E74233C7973B32C2E79A86FEF870C652F
	189F16EF4D7CE8B67BE97A7B009B669B4BBFC771529D467257EAD8FEA14479815837EE16EEA3F3055B67F17B64C676EBF9925ACEDF217DF595502E3B83797DA7FA7E0A3DBF29FB7744BC177117GAB633A3FC199377943B25D25FBAFB35D0B786B16DDFFD54B6067FC58FDEBE84F02BB515E7C1C78A6C0791D584EDD41306CB65D7B4FCC77CB7E3A7510B7255F751BAEF0BF74047DBDB63FF4686EB2162911E7570F085F9AE907FAF281A481ACGA0F6B87BEE47C38124D9827815GD9GC5F7E5D8775D053F1B60
	D431AF3683AE61AE1CCBCF37CC38B3ACFC458DCAE8421D6187455AF469DD621BC4B44FEC889725E6D84BD2F12EDA9740392AAE956F7A3E24996F7A7E40223F2B5B22996F7A12746178914DF857F7560478B44DF85797533376752A5373670CCEB35DA211AE14BA35F90DFF7F01FE6BACB155B84686A9BEA555B8467EDE346B8208AB389B7B52D313FE1BADFADFFD18363367EEE6CB703B0E4484447A503D677F8D3E23716AD5AD035FB3236BADDB489DE03FC31DB4CEAAB16DA9E9E3C015DE0834DD6BEF7A518F53
	AEB495DBA4CB2B740C0B6E7337CA9B07480101900C5F39C9G8BB4A937D4138ABBDC4AD0A9791AF5D9E0B7F4A6CD5A9CB630B549CA234919EAFD9CB99C8FC5EF19E8EA665E7B7235C5B79F88DA24022352B33DB60F5FE503E269EFB034C94F76D0E7BBB97D655124A27ACB9AFBA16DDE1A76D01BE43187B4698FE13C1D60D9EC075FE61701446A30297DB40781693234791CE46DD51DBD34GCD321CBBFA8E0A25A192DD58A6ED56DC6410ABD9C5E9524B612C5B49FEBA0FB89422B35A53704FCF7F82A4B179A39D
	369C445CAA44EC25C3631A4183B5F9CF5E13E7117221FA59F6C32B14CF768CC0DC1555F61E2678E94E79FAB0640DA1F204F23BA124CAEF5895EF053B47DB919C74D658FA348A59810B8932396C906760BF7E7645452E6F3E3853A2EDEC151E6DF54B9EC787BD895FA69551D0139C302BEEBFF1A7EA528B2C8A484F406C65BF8353A6BDCF697084BD491EB072D21B34CD175DD137C7760523366822BEBC5D26C9DBD84EBED95B8BE1D627391A74E6C5C57719C65963A97DD0F3FB3A235CBF502412E8D793C8F92F30
	A6A90ADA19E655C7C2AFB2A6DEB7E1E2628581CDAAB63BB7C0725861B749B886AB1A34119E7622D8AB57AE09EB17AABDB7E117779F6EBE782FABFEFCA610A7BD53AA3DC28E47F548AA7B5DA58769E77C07140FCAD63F82DACB9C9720EC817A58985006BD5054453D27490FAE86B459A1BEG8A1D69C556A8C110FAEF0BAD605248EE9934F83EE8C956CE75F514F7BAD083FF5BA8FFA4D505C8356D6F373E797A31350B73A44BD1E933C731B90E5A6C1A2236CAD64D254A811762F1047CC7D8C685C3F806B49F327B
	03901788D7C19C5C02497E509D094F2DFB6EA8CC35A1D55ACBA9C9272C90940EC6170D7C38142C36EA64A72B95F517087B54EB5ADB5FFF7F65EE0B14BBD1A80D8CBFDE37E798DE1A68DDB25A94FBA048CEBD09DECDFF9169A848237D53D35F7D4C6335D96BA13323FC5082160A42D4A7025E9ED91D11B67B55E71357FC1AF06CA762E24ED6CD3579823D0A6A054CCB8DB2E314E03D62566F6E1B7D40EFBF262C0713EA520E70A86514956F5E7A5EDED805088C20E7C1762F0A6C89CD05016605502C970FBD76F48F
	E8D41314675D370F7E3F1F7EDFC97E5FCFB17BA9E6FFA5B4DB16B4691FB1BE04B2D3B52401CA22C3A17236B724C91B583947CA71D6A55E76EBD2C1A97F0359074CB2254C0FD3A3BA513BGA5261D87BB5FE6DD67A36F7CF7E44675F71F332F33C77DF6CD5D834C6669FC40F8777953F9C4F713G1F760078EDA28B79ED2DE8F7EABA3CB60FC733F97A6BFD8EF23F26E1D81343F7BA1272C87E812461117C5B0D74FBFDFD3AB47F9FD0CB87886EEA95BA66A9GG4402GGD0CB818294G94G88G88GE8FBB0B6
	6EEA95BA66A9GG4402GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA0AAGGGG
**end of data**/
}
}