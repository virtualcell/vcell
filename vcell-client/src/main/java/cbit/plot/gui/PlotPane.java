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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.vcell.util.Range;
import org.vcell.util.gui.ButtonGroupCivilized;
import org.vcell.util.gui.EnhancedJLabel;
import org.vcell.util.gui.JToolBarToggleButton;
import org.vcell.util.gui.SpecialtyTableRenderer;
import org.vcell.util.gui.VCellIcons;

import cbit.plot.Plot2D;
import cbit.plot.SingleXPlot2D;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.DataSymbolMetadata;
import cbit.vcell.solver.SimulationModelInfo.DataSymbolMetadataResolver;
import cbit.vcell.units.VCUnitDefinition;
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
	private EnhancedJLabel ivjJLabelLeft = null;
	private EnhancedJLabel ivjJLabelRight = null;
	private JLabel ivjJLabelBottom = null;
	private JLabel ivjJLabelTitle = null;
	private Plot2D fieldPlot2D = new Plot2D(null,null,null, null);
	private JPanel ivjJPanel1 = null;
	private JPanel ivjJPanelData = null;
	private JPanel ivjJPanelPlot = null;
	private Plot2DDataPanel ivjPlot2DDataPanel1 = null;
	private ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
	private CardLayout ivjCardLayout1 = null;
	private JToolBarToggleButton ivjDataButton = null;
	private JToolBarToggleButton ivjPlotButton = null;
	private boolean ivjConnPtoP3Aligning = false;
	private ButtonModel ivjselection1 = null;
	private boolean fieldBCompact = false;
	private JPanel ivjJPanelBottom = null;
	private JPanel ivjJPanelLegend = null;
	private boolean ivjConnPtoP4Aligning = false;
	private Plot2D ivjplot2D1 = null;
	private JLabel ivjJLabelLegendTitle = null;
	private JPanel ivjJPanelPlotLegends = null;
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
			if (e.getSource() == PlotPane.this.getplot2D1()) { 
				connEtoC2(e);
			} 
			if (e.getSource() == PlotPane.this.getJCheckBox_stepLike()) {
				jCheckBox_stepLike_ActionPerformed();
			}
			
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
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		this.jCheckBox_stepLike_ActionPerformed();
	} catch (java.lang.Throwable ivjExc) {
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
public void forceXYRange(Range xRange, Range yRange) {

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
private ButtonGroupCivilized getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new ButtonGroupCivilized();
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
private JToolBarToggleButton getDataButton() {
	if (ivjDataButton == null) {
		try {
			ivjDataButton = new JToolBarToggleButton();
			ivjDataButton.setName("DataButton");
			ivjDataButton.setToolTipText("Show data");
			ivjDataButton.setText("");
			ivjDataButton.setMaximumSize(new java.awt.Dimension(28, 28));
			ivjDataButton.setActionCommand("JPanelData");
			ivjDataButton.setIcon(VCellIcons.dataSetsIcon);
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
			ivjJCheckBox_stepLike.setText("Step View");
			ivjJCheckBox_stepLike.setVisible(false);
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
private EnhancedJLabel getJLabelLeft() {
	if (ivjJLabelLeft == null) {
		try {
			ivjJLabelLeft = new EnhancedJLabel();
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
			ivjJLabelLegendTitle = new javax.swing.JLabel();
			ivjJLabelLegendTitle.setName("JLabelLegendTitle");
			ivjJLabelLegendTitle.setBorder(new EmptyBorder(10, 4, 10, 4));
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
private EnhancedJLabel getJLabelRight() {
	if (ivjJLabelRight == null) {
		try {
			ivjJLabelRight = new EnhancedJLabel();
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
			ivjJLabelTitle.setFont(ivjJLabelTitle.getFont().deriveFont(Font.BOLD));
			ivjJLabelTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
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
			ivjPlot2DDataPanel1 = new Plot2DDataPanel();
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
			ivjPlot2DPanel1 = new Plot2DPanel();
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
private JToolBarToggleButton getPlotButton() {
	if (ivjPlotButton == null) {
		try {
			ivjPlotButton = new JToolBarToggleButton();
			ivjPlotButton.setName("PlotButton");
			ivjPlotButton.setToolTipText("Show plot(s)");
			ivjPlotButton.setText("");
			ivjPlotButton.setMaximumSize(new java.awt.Dimension(28, 28));
			ivjPlotButton.setActionCommand("JPanelPlot");
			ivjPlotButton.setSelected(true);
			ivjPlotButton.setPreferredSize(new java.awt.Dimension(28, 28));
			ivjPlotButton.setIcon(VCellIcons.dataExporterIcon);
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
			ivjPlotLegendsScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
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
	getJCheckBox_stepLike().addChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
	connPtoP4SetTarget();
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
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
}

/**
 * Comment
 */
private void jCheckBox_stepLike_ActionPerformed() {
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
private void setCardLayout1(java.awt.CardLayout newValue) {
	if (ivjCardLayout1 != newValue) {
		try {
			ivjCardLayout1 = newValue;
			connPtoP2SetSource();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
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

public void setPlot2D(Plot2D plot2D,Color[] userDefinedColors) {
	getPlot2DPanel1().setUserDefinedColors(userDefinedColors);
	setPlot2D(plot2D);
}


/**
 * Set the plot2D1 to a new value.
 * @param newValue cbit.plot.Plot2D
 */
private void setplot2D1(Plot2D newValue) {
	if (ivjplot2D1 != newValue) {
		try {
			Plot2D oldValue = getplot2D1();
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}


/**
 * Set the selection1 to a new value.
 * @param newValue javax.swing.ButtonModel
 */
private void setselection1(javax.swing.ButtonModel newValue) {
	if (ivjselection1 != newValue) {
		try {
			ivjselection1 = newValue;
			connEtoM5(ivjselection1);
			connPtoP3SetSource();
			connEtoC4(ivjselection1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
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
	SymbolTableEntry[] steList = plot.getSymbolTableEntries();
	DataSymbolMetadataResolver metadataResolver = plot.getDataSymbolMetadataResolver();
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
		line.setBorder(new EmptyBorder(6,0,0,0));
		text.setBorder(new EmptyBorder(0,8,6,0));
		getJPanelPlotLegends().add(line);
		getJPanelPlotLegends().add(text);
		text.addMouseListener(ml);
	}
	legends = getJPanelPlotLegends().getComponents();
	// update labels and show them,use reverse loop to generate non-repeatable colors
	for (int i = (plotIndices.length - 1); i >= 0; i--){
		LineIcon icon = new LineIcon(getPlot2DPanel1().getVisiblePlotPaint(i));
		String plotLabel = null;
		if (plot instanceof SingleXPlot2D) {
			plotLabel = plotLabels[i + 1];
		} else {
			plotLabel = plotLabels[2 * i + 1];
		}
		((JLabel)legends[2 * i]).setIcon(icon);
		final int head = 20;
		final int tail = 0;
		
		String tooltipString = "";
		String unitSymbol = "";
		if (metadataResolver != null) {
			DataSymbolMetadata metaData = metadataResolver.getDataSymbolMetadata(plotLabel);
			if (metaData != null && metaData.unit != null){
				VCUnitDefinition ud = metaData.unit;
				if(ud != null) {
					unitSymbol += ud.getSymbolUnicode();
				}
			}
			if (metaData != null && metaData.tooltipString != null){
				tooltipString = metaData.tooltipString;
			}
		}
		String shortLabel =  plotLabel;
		if(shortLabel.length()>head+3+tail) {
			shortLabel = shortLabel.substring(0, head) + "..." + shortLabel.substring(shortLabel.length()-tail, shortLabel.length());
		}
//		shortLabel = "<html>" + shortLabel + "<font color=\"red\">" + " [" + unitSymbol + "] " + "</font></html>";
		shortLabel = "<html>" + shortLabel + "<font color=\"#8B0000\">" + " [" + unitSymbol + "] " + "</font></html>";
		tooltipString  = "<html>" + plotLabel + "<font color=\"#0000FF\">" + " " + tooltipString + " " + "</font></html>";
		
		((JLabel)legends[2 * i + 1]).setText(shortLabel);
		((JLabel)legends[2 * i + 1]).setToolTipText(tooltipString);
		legends[2 * i].setVisible(true);
		legends[2 * i + 1].setVisible(true);
	}
	// if extra ones, hide them
	for (int i = 2 * plotIndices.length; i < legends.length; i++){
		legends[i].setVisible(false);
	}
}

public void setStepViewVisible(boolean bStoch, boolean bMultiTrialData) {
	getPlot2DPanel1().setIsHistogram(bMultiTrialData);
	getPlot2DPanel1().updateAutoRanges();
	
	if(!bMultiTrialData && bStoch) {
		getJCheckBox_stepLike().setVisible(true);
		getJCheckBox_stepLike().setSelected(true);
	} else {
		getJCheckBox_stepLike().setVisible(false);
	}
}

public void setBackground(Color color)
{
	super.setBackground(color);
	getPlot2DPanel1().setBackground(color);
	getJPanelLegend().setBackground(color);
	getJPanel1().setBackground(color);
	getJPanelData().setBackground(color);
	getJPanelPlotLegends().setBackground(color);
	getJPanelBottom().setBackground(color);
	getJPanelPlot().setBackground(color);
//	this.setBackground(color);
}

/**
 * set renderer on the 2D data panel
 * @param str not null
 */
public void setSpecialityRenderer(SpecialtyTableRenderer str) {
	getPlot2DDataPanel1().setSpecialityRenderer(str);
}
}
