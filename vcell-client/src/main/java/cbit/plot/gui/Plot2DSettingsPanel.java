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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.vcell.util.Range;

import cbit.plot.Plot2DSettings;
/**
 * Insert the type's description here.
 * Creation date: (2/15/2001 10:12:19 AM)
 * @author: Ion Moraru
 */
public class Plot2DSettingsPanel extends JPanel {
	private JCheckBox ivjJCheckBoxXauto = null;
	private JCheckBox ivjJCheckBoxXstretch = null;
	private JCheckBox ivjJCheckBoxYauto = null;
	private JCheckBox ivjJCheckBoxYstretch = null;
	private JLabel ivjJLabelXAxis = null;
	private JLabel ivjJLabelXmax = null;
	private JLabel ivjJLabelXmin = null;
	private JLabel ivjJLabelYAxis = null;
	private JLabel ivjJLabelYmax = null;
	private JLabel ivjJLabelYmin = null;
	private JTextField ivjJTextFieldXmax = null;
	private JTextField ivjJTextFieldXmin = null;
	private JTextField ivjJTextFieldYmax = null;
	private JTextField ivjJTextFieldYmin = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JCheckBox ivjJCheckBoxCrosshair = null;
	private JCheckBox ivjJCheckBoxNodes = null;
	private JCheckBox ivjJCheckBoxSnap = null;
	private JLabel ivjJLabelPlots = null;
	private Plot2DSettings fieldPlot2DSettings = new Plot2DSettings();
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP5Aligning = false;
	private boolean ivjConnPtoP6Aligning = false;
	private boolean ivjConnPtoP7Aligning = false;
	private boolean ivjConnPtoP8Aligning = false;
	private boolean ivjConnPtoP9Aligning = false;
	private Plot2DSettings ivjplot2DSettings1 = null;
	private Range ivjcurrentXRange = null;
	private Range ivjcurrentYRange = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == Plot2DSettingsPanel.this.getJTextFieldXmin()) 
				connEtoM5(e);
			if (e.getSource() == Plot2DSettingsPanel.this.getJTextFieldXmax()) 
				connEtoM7(e);
			if (e.getSource() == Plot2DSettingsPanel.this.getJTextFieldYmin()) 
				connEtoM9(e);
			if (e.getSource() == Plot2DSettingsPanel.this.getJTextFieldYmax()) 
				connEtoM11(e);
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == Plot2DSettingsPanel.this.getJTextFieldXmin()) 
				connEtoM6(e);
			if (e.getSource() == Plot2DSettingsPanel.this.getJTextFieldXmax()) 
				connEtoM8(e);
			if (e.getSource() == Plot2DSettingsPanel.this.getJTextFieldYmin()) 
				connEtoM10(e);
			if (e.getSource() == Plot2DSettingsPanel.this.getJTextFieldYmax()) 
				connEtoM12(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == Plot2DSettingsPanel.this.getJCheckBoxCrosshair()) 
				connPtoP1SetTarget();
			if (e.getSource() == Plot2DSettingsPanel.this.getJCheckBoxCrosshair()) 
				connPtoP3SetSource();
			if (e.getSource() == Plot2DSettingsPanel.this.getJCheckBoxNodes()) 
				connPtoP4SetSource();
			if (e.getSource() == Plot2DSettingsPanel.this.getJCheckBoxSnap()) 
				connPtoP5SetSource();
			if (e.getSource() == Plot2DSettingsPanel.this.getJCheckBoxXauto()) 
				connPtoP6SetSource();
			if (e.getSource() == Plot2DSettingsPanel.this.getJCheckBoxXstretch()) 
				connPtoP7SetSource();
			if (e.getSource() == Plot2DSettingsPanel.this.getJCheckBoxYauto()) 
				connPtoP8SetSource();
			if (e.getSource() == Plot2DSettingsPanel.this.getJCheckBoxYstretch()) 
				connPtoP9SetSource();
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == Plot2DSettingsPanel.this && (evt.getPropertyName().equals("plot2DSettings"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == Plot2DSettingsPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("showCrosshair"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == Plot2DSettingsPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("showNodes"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == Plot2DSettingsPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("snapToNodes"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == Plot2DSettingsPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("xAuto"))) 
				connPtoP6SetTarget();
			if (evt.getSource() == Plot2DSettingsPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("xStretch"))) 
				connPtoP7SetTarget();
			if (evt.getSource() == Plot2DSettingsPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("yAuto"))) 
				connPtoP8SetTarget();
			if (evt.getSource() == Plot2DSettingsPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("yStretch"))) 
				connPtoP9SetTarget();
			if (evt.getSource() == Plot2DSettingsPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("xAutoRange"))) 
				connEtoC5(evt);
			if (evt.getSource() == Plot2DSettingsPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("yAutoRange"))) 
				connEtoC6(evt);
			if (evt.getSource() == Plot2DSettingsPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("xAuto"))) 
				connEtoC7(evt);
			if (evt.getSource() == Plot2DSettingsPanel.this.getplot2DSettings1() && (evt.getPropertyName().equals("yAuto"))) 
				connEtoC1(evt);
		};
	};

/**
 * Plot2DSettings constructor comment.
 */
public Plot2DSettingsPanel() {
	super();
	initialize();
}

/**
 * Comment
 */
private void checkDouble(JTextField textField, double defaultValue) {
	try {
		double newValue = Double.parseDouble(textField.getText());
	} catch (NumberFormatException e) {
		textField.setText(Double.toString(defaultValue));
	}
}


/**
 * connEtoC1:  (plot2DSettings1.yAuto --> Plot2DSettingsPanel.updateYRangeControls()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateYRangeControls();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (currentXRange.this --> Plot2DSettingsPanel.updateManualRanges()V)
 * @param value cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(Range value) {
	try {
		// user code begin {1}
		// user code end
		this.updateManualRanges();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (currentYRange.this --> Plot2DSettingsPanel.updateManualRanges()V)
 * @param value cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(Range value) {
	try {
		// user code begin {1}
		// user code end
		this.updateManualRanges();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (plot2DSettings1.xAutoRange --> Plot2DSettingsPanel.updateXRangeControls()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateXRangeControls();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (plot2DSettings1.yAutoRange --> Plot2DSettingsPanel.updateYRangeControls()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateYRangeControls();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (plot2DSettings1.xAuto --> Plot2DSettingsPanel.updateXRangeControls()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateXRangeControls();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (currentXRange.this --> JTextFieldXmin.text)
 * @param value cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(Range value) {
	try {
		// user code begin {1}
		// user code end
		if ((getcurrentXRange() != null)) {
			getJTextFieldXmin().setText(String.valueOf(getcurrentXRange().getMin()));
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
 * connEtoM10:  (JTextFieldYmin.focus.focusLost(java.awt.event.FocusEvent) --> currentYRange.this)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setcurrentYRange(this.newRangeFromInput(getcurrentYRange(), getJTextFieldYmin().getText(), true));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM11:  (JTextFieldYmax.action.actionPerformed(java.awt.event.ActionEvent) --> currentYRange.this)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setcurrentYRange(this.newRangeFromInput(getcurrentYRange(), getJTextFieldYmax().getText(), false));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM12:  (JTextFieldYmax.focus.focusLost(java.awt.event.FocusEvent) --> currentYRange.this)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setcurrentYRange(this.newRangeFromInput(getcurrentYRange(), getJTextFieldYmax().getText(), false));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM2:  (currentXRange.this --> JTextFieldXmax.text)
 * @param value cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(Range value) {
	try {
		// user code begin {1}
		// user code end
		if ((getcurrentXRange() != null)) {
			getJTextFieldXmax().setText(String.valueOf(getcurrentXRange().getMax()));
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
 * connEtoM3:  (currentYRange.this --> JTextFieldYmin.text)
 * @param value cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(Range value) {
	try {
		// user code begin {1}
		// user code end
		if ((getcurrentYRange() != null)) {
			getJTextFieldYmin().setText(String.valueOf(getcurrentYRange().getMin()));
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
 * connEtoM4:  (currentYRange.this --> JTextFieldYmax.text)
 * @param value cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(Range value) {
	try {
		// user code begin {1}
		// user code end
		if ((getcurrentYRange() != null)) {
			getJTextFieldYmax().setText(String.valueOf(getcurrentYRange().getMax()));
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
 * connEtoM5:  (JTextFieldXmin.action.actionPerformed(java.awt.event.ActionEvent) --> currentXRange.this)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setcurrentXRange(this.newRangeFromInput(getcurrentXRange(), getJTextFieldXmin().getText(), true));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (JTextFieldXmin.focus.focusLost(java.awt.event.FocusEvent) --> currentXRange.this)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setcurrentXRange(this.newRangeFromInput(getcurrentXRange(), getJTextFieldXmin().getText(), true));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM7:  (JTextFieldXmax.action.actionPerformed(java.awt.event.ActionEvent) --> currentXRange.this)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setcurrentXRange(this.newRangeFromInput(getcurrentXRange(), getJTextFieldXmax().getText(), false));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM8:  (JTextFieldXmax.focus.focusLost(java.awt.event.FocusEvent) --> currentXRange.this)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setcurrentXRange(this.newRangeFromInput(getcurrentXRange(), getJTextFieldXmax().getText(), false));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM9:  (JTextFieldYmin.action.actionPerformed(java.awt.event.ActionEvent) --> currentYRange.this)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setcurrentYRange(this.newRangeFromInput(getcurrentYRange(), getJTextFieldYmin().getText(), true));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetTarget:  (JCheckBoxCrosshairShow.selected <--> JCheckBoxCrosshairSnap.enabled)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getJCheckBoxSnap().setEnabled(getJCheckBoxCrosshair().isSelected());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetSource:  (Plot2DSettingsPanel.plot2DSettings <--> plot2DSettings1.this)
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
				this.setPlot2DSettings(getplot2DSettings1());
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
 * connPtoP2SetTarget:  (Plot2DSettingsPanel.plot2DSettings <--> plot2DSettings1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setplot2DSettings1(this.getPlot2DSettings());
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
 * connPtoP3SetSource:  (plot2DSettings1.showCrosshair <--> JCheckBoxCrosshair.selected)
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
				getplot2DSettings1().setShowCrosshair(getJCheckBoxCrosshair().isSelected());
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
 * connPtoP3SetTarget:  (plot2DSettings1.showCrosshair <--> JCheckBoxCrosshair.selected)
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
				getJCheckBoxCrosshair().setSelected(getplot2DSettings1().getShowCrosshair());
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
 * connPtoP4SetSource:  (plot2DSettings1.showNodes <--> JCheckBoxNodes.selected)
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
				getplot2DSettings1().setShowNodes(getJCheckBoxNodes().isSelected());
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
 * connPtoP4SetTarget:  (plot2DSettings1.showNodes <--> JCheckBoxNodes.selected)
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
				getJCheckBoxNodes().setSelected(getplot2DSettings1().getShowNodes());
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
 * connPtoP5SetSource:  (plot2DSettings1.snapToNodes <--> JCheckBoxSnap.selected)
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
				getplot2DSettings1().setSnapToNodes(getJCheckBoxSnap().isSelected());
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
 * connPtoP5SetTarget:  (plot2DSettings1.snapToNodes <--> JCheckBoxSnap.selected)
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
				getJCheckBoxSnap().setSelected(getplot2DSettings1().getSnapToNodes());
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
 * connPtoP6SetSource:  (plot2DSettings1.xAuto <--> JCheckBoxXauto.selected)
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
				getplot2DSettings1().setXAuto(getJCheckBoxXauto().isSelected());
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
 * connPtoP6SetTarget:  (plot2DSettings1.xAuto <--> JCheckBoxXauto.selected)
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
				getJCheckBoxXauto().setSelected(getplot2DSettings1().getXAuto());
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
 * connPtoP7SetSource:  (plot2DSettings1.xStretch <--> JCheckBoxXstretch.selected)
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
				getplot2DSettings1().setXStretch(getJCheckBoxXstretch().isSelected());
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
 * connPtoP7SetTarget:  (plot2DSettings1.xStretch <--> JCheckBoxXstretch.selected)
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
				getJCheckBoxXstretch().setSelected(getplot2DSettings1().getXStretch());
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
 * connPtoP8SetSource:  (plot2DSettings1.yAuto <--> JCheckBoxYauto.selected)
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
				getplot2DSettings1().setYAuto(getJCheckBoxYauto().isSelected());
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
 * connPtoP8SetTarget:  (plot2DSettings1.yAuto <--> JCheckBoxYauto.selected)
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
				getJCheckBoxYauto().setSelected(getplot2DSettings1().getYAuto());
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
 * connPtoP9SetSource:  (plot2DSettings1.yStretch <--> JCheckBoxYstretch.selected)
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
				getplot2DSettings1().setYStretch(getJCheckBoxYstretch().isSelected());
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
 * connPtoP9SetTarget:  (plot2DSettings1.yStretch <--> JCheckBoxYstretch.selected)
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
				getJCheckBoxYstretch().setSelected(getplot2DSettings1().getYStretch());
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
 * Return the currentXRange property value.
 * @return cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Range getcurrentXRange() {
	// user code begin {1}
	// user code end
	return ivjcurrentXRange;
}


/**
 * Return the currentYRange property value.
 * @return cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Range getcurrentYRange() {
	// user code begin {1}
	// user code end
	return ivjcurrentYRange;
}


/**
 * Return the JCheckBox5 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxCrosshair() {
	if (ivjJCheckBoxCrosshair == null) {
		try {
			ivjJCheckBoxCrosshair = new javax.swing.JCheckBox();
			ivjJCheckBoxCrosshair.setName("JCheckBoxCrosshair");
			ivjJCheckBoxCrosshair.setSelected(true);
			ivjJCheckBoxCrosshair.setText("show crosshair");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxCrosshair;
}

/**
 * Return the JCheckBoxNodes property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxNodes() {
	if (ivjJCheckBoxNodes == null) {
		try {
			ivjJCheckBoxNodes = new javax.swing.JCheckBox();
			ivjJCheckBoxNodes.setName("JCheckBoxNodes");
			ivjJCheckBoxNodes.setSelected(true);
			ivjJCheckBoxNodes.setText("draw nodes");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxNodes;
}


/**
 * Return the JCheckBox6 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSnap() {
	if (ivjJCheckBoxSnap == null) {
		try {
			ivjJCheckBoxSnap = new javax.swing.JCheckBox();
			ivjJCheckBoxSnap.setName("JCheckBoxSnap");
			ivjJCheckBoxSnap.setSelected(true);
			ivjJCheckBoxSnap.setText("snap to nodes");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSnap;
}

/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxXauto() {
	if (ivjJCheckBoxXauto == null) {
		try {
			ivjJCheckBoxXauto = new javax.swing.JCheckBox();
			ivjJCheckBoxXauto.setName("JCheckBoxXauto");
			ivjJCheckBoxXauto.setSelected(true);
			ivjJCheckBoxXauto.setText("auto");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxXauto;
}

/**
 * Return the JCheckBox3 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxXstretch() {
	if (ivjJCheckBoxXstretch == null) {
		try {
			ivjJCheckBoxXstretch = new javax.swing.JCheckBox();
			ivjJCheckBoxXstretch.setName("JCheckBoxXstretch");
			ivjJCheckBoxXstretch.setText("stretch");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxXstretch;
}

/**
 * Return the JCheckBox2 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxYauto() {
	if (ivjJCheckBoxYauto == null) {
		try {
			ivjJCheckBoxYauto = new javax.swing.JCheckBox();
			ivjJCheckBoxYauto.setName("JCheckBoxYauto");
			ivjJCheckBoxYauto.setSelected(true);
			ivjJCheckBoxYauto.setText("auto");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxYauto;
}

/**
 * Return the JCheckBox4 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxYstretch() {
	if (ivjJCheckBoxYstretch == null) {
		try {
			ivjJCheckBoxYstretch = new javax.swing.JCheckBox();
			ivjJCheckBoxYstretch.setName("JCheckBoxYstretch");
			ivjJCheckBoxYstretch.setText("stretch");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxYstretch;
}

/**
 * Return the JLabel7 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPlots() {
	if (ivjJLabelPlots == null) {
		try {
			ivjJLabelPlots = new javax.swing.JLabel();
			ivjJLabelPlots.setName("JLabelPlots");
			ivjJLabelPlots.setText("Plots:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPlots;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelXAxis() {
	if (ivjJLabelXAxis == null) {
		try {
			ivjJLabelXAxis = new javax.swing.JLabel();
			ivjJLabelXAxis.setName("JLabelXAxis");
			ivjJLabelXAxis.setText("X axis scale:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelXAxis;
}

/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelXmax() {
	if (ivjJLabelXmax == null) {
		try {
			ivjJLabelXmax = new javax.swing.JLabel();
			ivjJLabelXmax.setName("JLabelXmax");
			ivjJLabelXmax.setText("max:");
			ivjJLabelXmax.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelXmax;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelXmin() {
	if (ivjJLabelXmin == null) {
		try {
			ivjJLabelXmin = new javax.swing.JLabel();
			ivjJLabelXmin.setName("JLabelXmin");
			ivjJLabelXmin.setText("min:");
			ivjJLabelXmin.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelXmin;
}

/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelYAxis() {
	if (ivjJLabelYAxis == null) {
		try {
			ivjJLabelYAxis = new javax.swing.JLabel();
			ivjJLabelYAxis.setName("JLabelYAxis");
			ivjJLabelYAxis.setText("Y axis scale:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelYAxis;
}

/**
 * Return the JLabel6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelYmax() {
	if (ivjJLabelYmax == null) {
		try {
			ivjJLabelYmax = new javax.swing.JLabel();
			ivjJLabelYmax.setName("JLabelYmax");
			ivjJLabelYmax.setText("max:");
			ivjJLabelYmax.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelYmax;
}

/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelYmin() {
	if (ivjJLabelYmin == null) {
		try {
			ivjJLabelYmin = new javax.swing.JLabel();
			ivjJLabelYmin.setName("JLabelYmin");
			ivjJLabelYmin.setText("min:");
			ivjJLabelYmin.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelYmin;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldXmax() {
	if (ivjJTextFieldXmax == null) {
		try {
			ivjJTextFieldXmax = new javax.swing.JTextField();
			ivjJTextFieldXmax.setName("JTextFieldXmax");
			ivjJTextFieldXmax.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldXmax;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldXmin() {
	if (ivjJTextFieldXmin == null) {
		try {
			ivjJTextFieldXmin = new javax.swing.JTextField();
			ivjJTextFieldXmin.setName("JTextFieldXmin");
			ivjJTextFieldXmin.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldXmin;
}

/**
 * Return the JTextField4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldYmax() {
	if (ivjJTextFieldYmax == null) {
		try {
			ivjJTextFieldYmax = new javax.swing.JTextField();
			ivjJTextFieldYmax.setName("JTextFieldYmax");
			ivjJTextFieldYmax.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldYmax;
}

/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldYmin() {
	if (ivjJTextFieldYmin == null) {
		try {
			ivjJTextFieldYmin = new javax.swing.JTextField();
			ivjJTextFieldYmin.setName("JTextFieldYmin");
			ivjJTextFieldYmin.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldYmin;
}

/**
 * Gets the plot2DSettings property (cbit.plot.Plot2DSettings) value.
 * @return The plot2DSettings property value.
 * @see #setPlot2DSettings
 */
public Plot2DSettings getPlot2DSettings() {
	return fieldPlot2DSettings;
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
	getJCheckBoxCrosshair().addItemListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getJCheckBoxNodes().addItemListener(ivjEventHandler);
	getJCheckBoxSnap().addItemListener(ivjEventHandler);
	getJCheckBoxXauto().addItemListener(ivjEventHandler);
	getJCheckBoxXstretch().addItemListener(ivjEventHandler);
	getJCheckBoxYauto().addItemListener(ivjEventHandler);
	getJCheckBoxYstretch().addItemListener(ivjEventHandler);
	getJTextFieldXmin().addActionListener(ivjEventHandler);
	getJTextFieldXmin().addFocusListener(ivjEventHandler);
	getJTextFieldXmax().addActionListener(ivjEventHandler);
	getJTextFieldXmax().addFocusListener(ivjEventHandler);
	getJTextFieldYmin().addActionListener(ivjEventHandler);
	getJTextFieldYmin().addFocusListener(ivjEventHandler);
	getJTextFieldYmax().addActionListener(ivjEventHandler);
	getJTextFieldYmax().addFocusListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
	connPtoP6SetTarget();
	connPtoP7SetTarget();
	connPtoP8SetTarget();
	connPtoP9SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("Plot2DSettings");
		setLayout(new java.awt.GridBagLayout());
		setSize(192, 242);

		java.awt.GridBagConstraints constraintsJLabelXAxis = new java.awt.GridBagConstraints();
		constraintsJLabelXAxis.gridx = 0; constraintsJLabelXAxis.gridy = 0;
		constraintsJLabelXAxis.gridwidth = 3;
		constraintsJLabelXAxis.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelXAxis.insets = new java.awt.Insets(10, 10, 4, 10);
		add(getJLabelXAxis(), constraintsJLabelXAxis);

		java.awt.GridBagConstraints constraintsJCheckBoxXauto = new java.awt.GridBagConstraints();
		constraintsJCheckBoxXauto.gridx = 0; constraintsJCheckBoxXauto.gridy = 1;
		constraintsJCheckBoxXauto.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCheckBoxXauto.insets = new java.awt.Insets(0, 20, 0, 4);
		add(getJCheckBoxXauto(), constraintsJCheckBoxXauto);

		java.awt.GridBagConstraints constraintsJLabelXmin = new java.awt.GridBagConstraints();
		constraintsJLabelXmin.gridx = 1; constraintsJLabelXmin.gridy = 1;
		constraintsJLabelXmin.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabelXmin.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelXmin(), constraintsJLabelXmin);

		java.awt.GridBagConstraints constraintsJLabelXmax = new java.awt.GridBagConstraints();
		constraintsJLabelXmax.gridx = 1; constraintsJLabelXmax.gridy = 2;
		constraintsJLabelXmax.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabelXmax.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelXmax(), constraintsJLabelXmax);

		java.awt.GridBagConstraints constraintsJTextFieldXmin = new java.awt.GridBagConstraints();
		constraintsJTextFieldXmin.gridx = 2; constraintsJTextFieldXmin.gridy = 1;
		constraintsJTextFieldXmin.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldXmin.weightx = 1.0;
		constraintsJTextFieldXmin.insets = new java.awt.Insets(0, 4, 0, 10);
		add(getJTextFieldXmin(), constraintsJTextFieldXmin);

		java.awt.GridBagConstraints constraintsJTextFieldXmax = new java.awt.GridBagConstraints();
		constraintsJTextFieldXmax.gridx = 2; constraintsJTextFieldXmax.gridy = 2;
		constraintsJTextFieldXmax.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldXmax.weightx = 1.0;
		constraintsJTextFieldXmax.insets = new java.awt.Insets(0, 4, 0, 10);
		add(getJTextFieldXmax(), constraintsJTextFieldXmax);

		java.awt.GridBagConstraints constraintsJLabelYAxis = new java.awt.GridBagConstraints();
		constraintsJLabelYAxis.gridx = 0; constraintsJLabelYAxis.gridy = 3;
		constraintsJLabelYAxis.gridwidth = 3;
		constraintsJLabelYAxis.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelYAxis.insets = new java.awt.Insets(10, 10, 4, 10);
		add(getJLabelYAxis(), constraintsJLabelYAxis);

		java.awt.GridBagConstraints constraintsJLabelYmin = new java.awt.GridBagConstraints();
		constraintsJLabelYmin.gridx = 1; constraintsJLabelYmin.gridy = 4;
		constraintsJLabelYmin.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabelYmin.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelYmin(), constraintsJLabelYmin);

		java.awt.GridBagConstraints constraintsJLabelYmax = new java.awt.GridBagConstraints();
		constraintsJLabelYmax.gridx = 1; constraintsJLabelYmax.gridy = 5;
		constraintsJLabelYmax.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabelYmax.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelYmax(), constraintsJLabelYmax);

		java.awt.GridBagConstraints constraintsJTextFieldYmin = new java.awt.GridBagConstraints();
		constraintsJTextFieldYmin.gridx = 2; constraintsJTextFieldYmin.gridy = 4;
		constraintsJTextFieldYmin.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldYmin.weightx = 1.0;
		constraintsJTextFieldYmin.insets = new java.awt.Insets(0, 4, 0, 10);
		add(getJTextFieldYmin(), constraintsJTextFieldYmin);

		java.awt.GridBagConstraints constraintsJTextFieldYmax = new java.awt.GridBagConstraints();
		constraintsJTextFieldYmax.gridx = 2; constraintsJTextFieldYmax.gridy = 5;
		constraintsJTextFieldYmax.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldYmax.weightx = 1.0;
		constraintsJTextFieldYmax.insets = new java.awt.Insets(0, 4, 0, 10);
		add(getJTextFieldYmax(), constraintsJTextFieldYmax);

		java.awt.GridBagConstraints constraintsJCheckBoxYauto = new java.awt.GridBagConstraints();
		constraintsJCheckBoxYauto.gridx = 0; constraintsJCheckBoxYauto.gridy = 4;
		constraintsJCheckBoxYauto.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCheckBoxYauto.insets = new java.awt.Insets(0, 20, 0, 4);
		add(getJCheckBoxYauto(), constraintsJCheckBoxYauto);

		java.awt.GridBagConstraints constraintsJLabelPlots = new java.awt.GridBagConstraints();
		constraintsJLabelPlots.gridx = 0; constraintsJLabelPlots.gridy = 6;
		constraintsJLabelPlots.gridwidth = 3;
		constraintsJLabelPlots.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelPlots.insets = new java.awt.Insets(10, 10, 4, 10);
		add(getJLabelPlots(), constraintsJLabelPlots);

		java.awt.GridBagConstraints constraintsJCheckBoxXstretch = new java.awt.GridBagConstraints();
		constraintsJCheckBoxXstretch.gridx = 0; constraintsJCheckBoxXstretch.gridy = 2;
		constraintsJCheckBoxXstretch.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCheckBoxXstretch.insets = new java.awt.Insets(0, 20, 0, 4);
		add(getJCheckBoxXstretch(), constraintsJCheckBoxXstretch);

		java.awt.GridBagConstraints constraintsJCheckBoxYstretch = new java.awt.GridBagConstraints();
		constraintsJCheckBoxYstretch.gridx = 0; constraintsJCheckBoxYstretch.gridy = 5;
		constraintsJCheckBoxYstretch.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCheckBoxYstretch.insets = new java.awt.Insets(0, 20, 0, 4);
		add(getJCheckBoxYstretch(), constraintsJCheckBoxYstretch);

		java.awt.GridBagConstraints constraintsJCheckBoxCrosshair = new java.awt.GridBagConstraints();
		constraintsJCheckBoxCrosshair.gridx = 0; constraintsJCheckBoxCrosshair.gridy = 8;
		constraintsJCheckBoxCrosshair.gridwidth = 3;
		constraintsJCheckBoxCrosshair.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCheckBoxCrosshair.insets = new java.awt.Insets(0, 20, 0, 10);
		add(getJCheckBoxCrosshair(), constraintsJCheckBoxCrosshair);

		java.awt.GridBagConstraints constraintsJCheckBoxSnap = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSnap.gridx = 0; constraintsJCheckBoxSnap.gridy = 9;
		constraintsJCheckBoxSnap.gridwidth = 3;
		constraintsJCheckBoxSnap.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCheckBoxSnap.insets = new java.awt.Insets(0, 20, 10, 10);
		add(getJCheckBoxSnap(), constraintsJCheckBoxSnap);

		java.awt.GridBagConstraints constraintsJCheckBoxNodes = new java.awt.GridBagConstraints();
		constraintsJCheckBoxNodes.gridx = 0; constraintsJCheckBoxNodes.gridy = 7;
		constraintsJCheckBoxNodes.gridwidth = 3;
		constraintsJCheckBoxNodes.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCheckBoxNodes.insets = new java.awt.Insets(0, 20, 0, 10);
		add(getJCheckBoxNodes(), constraintsJCheckBoxNodes);
		initConnections();
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
		Plot2DSettingsPanel aPlot2DSettingsPanel;
		aPlot2DSettingsPanel = new Plot2DSettingsPanel();
		frame.setContentPane(aPlot2DSettingsPanel);
		frame.setSize(aPlot2DSettingsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private Range newRangeFromInput(Range currentRange, String typedValue, boolean isMin) {
	double newValue = 0;
	Range newRange = null;
	try {
		newValue = Double.parseDouble(typedValue);
	} catch (NumberFormatException exc) {
		// user typed crap, put back old values
		return (Range)currentRange.clone();
	}
	if (isMin) {
		newRange = new Range(newValue, currentRange.getMax());
	} else {
		newRange = new Range(currentRange.getMin(), newValue);
	}
	if (newRange.getMin() != newRange.getMax()) {
		return newRange;
	} else {
		// can't scale properly on a zero-length interval...
		return (Range)currentRange.clone();
	}
}


/**
 * Set the currentXRange to a new value.
 * @param newValue cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setcurrentXRange(Range newValue) {
	if (ivjcurrentXRange != newValue) {
		try {
			ivjcurrentXRange = newValue;
			connEtoM1(ivjcurrentXRange);
			connEtoM2(ivjcurrentXRange);
			connEtoC2(ivjcurrentXRange);
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
 * Set the currentYRange to a new value.
 * @param newValue cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setcurrentYRange(Range newValue) {
	if (ivjcurrentYRange != newValue) {
		try {
			ivjcurrentYRange = newValue;
			connEtoM3(ivjcurrentYRange);
			connEtoM4(ivjcurrentYRange);
			connEtoC3(ivjcurrentYRange);
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
 * Sets the plot2DSettings property (cbit.plot.Plot2DSettings) value.
 * @param plot2DSettings The new value for the property.
 * @see #getPlot2DSettings
 */
public void setPlot2DSettings(Plot2DSettings plot2DSettings) {
	Plot2DSettings oldValue = fieldPlot2DSettings;
	fieldPlot2DSettings = plot2DSettings;
	firePropertyChange("plot2DSettings", oldValue, plot2DSettings);
}


/**
 * Set the plot2DSettings1 to a new value.
 * @param newValue cbit.plot.Plot2DSettings
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setplot2DSettings1(Plot2DSettings newValue) {
	if (ivjplot2DSettings1 != newValue) {
		try {
			cbit.plot.Plot2DSettings oldValue = getplot2DSettings1();
			/* Stop listening for events from the current object */
			if (ivjplot2DSettings1 != null) {
				ivjplot2DSettings1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjplot2DSettings1 = newValue;

			/* Listen for events from the new object */
			if (ivjplot2DSettings1 != null) {
				ivjplot2DSettings1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP2SetSource();
			connPtoP3SetTarget();
			connPtoP4SetTarget();
			connPtoP5SetTarget();
			connPtoP6SetTarget();
			connPtoP7SetTarget();
			connPtoP8SetTarget();
			connPtoP9SetTarget();
			firePropertyChange("plot2DSettings", oldValue, newValue);
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
private void updateManualRanges() {
	if (! getPlot2DSettings().getXAuto()) {
		getPlot2DSettings().setXManualRange(getcurrentXRange());
	}
	if (! getPlot2DSettings().getYAuto()) {
		getPlot2DSettings().setYManualRange(getcurrentYRange());
	}
}


/**
 * Comment
 */
private void updateXRangeControls() {
	boolean b = getJCheckBoxXauto().isSelected();
	getJCheckBoxXstretch().setEnabled(b);
	getJLabelXmin().setEnabled(! b);
	getJLabelXmax().setEnabled(! b);
	getJTextFieldXmin().setEnabled(! b);
	getJTextFieldXmax().setEnabled(! b);
	if (b || getPlot2DSettings().getXManualRange() == null) {
		setcurrentXRange(getPlot2DSettings().getXAutoRange());
	} else {
		setcurrentXRange(getPlot2DSettings().getXManualRange());
	}
	updateManualRanges();
}


/**
 * Comment
 */
private void updateYRangeControls() {
	boolean b = getJCheckBoxYauto().isSelected();
	getJCheckBoxYstretch().setEnabled(b);
	getJLabelYmin().setEnabled(! b);
	getJLabelYmax().setEnabled(! b);
	getJTextFieldYmin().setEnabled(! b);
	getJTextFieldYmax().setEnabled(! b);
	if (b || getPlot2DSettings().getYManualRange() == null) {
		setcurrentYRange(getPlot2DSettings().getYAutoRange());
	} else {
		setcurrentYRange(getPlot2DSettings().getYManualRange());
	}
	updateManualRanges();
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF4FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFDFFDCD4D55E3F95956DD2391B959A95EBD8D4EC4BD336715A68F97C6E6373E4AD5B9A512EEDD83451AE6D3A4F0BDAF7F34B1EAE3F848C15524AB6D3D4CCD4C2C4141FAA4820E0A824E8E8D80C4CB0B3B2B0634CG0322BE1FF34F791CFB6F5CB38CBB7B5D7F56576B631D7BB96F4F0F73B93F4F3D67A22DDA9719131139CA12B2F2A4762FAAC312FA2E1324D7BA27DA10D73DB9B1DFD27DDB8F741EF4
	B9B79B2EAD10B66BC7097905521F4F6441FDB75CF77EB0B17FA3783DD2FA734C022B61079FBB33A1AD73E06FFF0FEF6726B23413AB5FD7DDAE006BF2209E6011EB4CE6E23F7D72EA468FE1FC240CCCC99AABA118331757B0DEBAE07200D78D54D6C271A638D2872E2E2DE6386E6FC5CB8B1FF4E5AEA3FAD4BA11E6B359B4F6AD125E4C183C05583AC97613489441FD979049FC167423D39B61DA5F51EF595BFB4666A9F35A9D1E5A5A8E783F42F04C6C715886AD6E8E632059AE95ECA5FA1E3F1CA74BC9D2926888
	17F96F74915D9D705B8AB4E9AB72CF5B88BF99FEAF84AA5806795933391E795BF56191A929E2A0FB051F7C94EF2379E85B2664A7616C7C4C0F6558BF331D44B89452E200E6812582E517A6669783FDA217792F4B364235710C2749B1B06098ECF4BA775BCD2E438E13598E3FEBEBA105710E1AFB9DAE13A451F21DFA39D4604FF8C4656E793B65E8CC22F7E299479CFDC0CAFE68164CDCC1CC1223AFE4561752D8F416AAB12136D37C5A7614EA6B205676D7125A761CF34FE66588EC27FE73ECD6A853932132BD2F
	1414355BC24ABA9EFEE7B03D14BFC9662F79DC61537A18A37349FDB524C5FC0EF523EBB97A5216BDDDAA5CF12BB08E61BF8D4B144A288F2B4BFCFDD9EFA73A8DE55426037BE21479255B95BE7525D72E2764FED5F9E27E641D097934FEA56FA06D3320AAB13F842881288B488BB485A83A8A6B58466D5BA7D847BA0CAE4F7EA11B5DE4F6C9D4B6F97B07F0B5FBBDAEE36B205BE39C6CB51FB03AEC46B3F633DBE218D73613981C51487DDE2E7B3DE06320F9506CB2FAEC0EC150EE9CB0FB4CAEB7457CE97BFB8463
	B24B291D3681B39898F0CA28772D6DAB61EAB73ABD471DA623472CA0E8FAD6B9A913E1638950888A60B70DCB7ECE922FB9107F9420F49E07CB65C45F97E697F4973535ED8EC77F10331364C4EA28A6F1FEC36EBB3261771AEA0CF1CEA5610540FDC4B56AF9387CFD159E032D1738EFF40DE29B9B359442B5967089C0E9C059B5D8FF7E278B6B0FE1598F2552E7EF96561F2EEF1F4DAA2A2175263A46379DDDA36BA7BE76C05AA420489A6C6F5F35F4B19D446EF266876ACD289637B3B5E5577847D09B455CC6AE3B
	E629B0B98174E33B7617C6636E5EC54AF09678128C343886E31FDEDEC46B72FE235B0C35375D85954FECD2CA027A39591E4C7C798268B0D0BB509E20FDC487D093D0B350C74CAE3171A950BA090E27E46C59843499E88B50B6204F01C8FB5A813493E897D09D5017C047000ECB2447122413C08701DA013E85B2123ACE5A929019AC58007A0186003E86BA8D84CDCA9A829A81026AA4DDG3A88F499688A097681D7815D84F4B3AF8F0B1C4FD535097975C0A620B02019C049C059F5097925C01DC0E3C0D1C089C0
	8B015654A7669B006CC0D3006201D2018A76A466D782F58305G45GA581AD86AA5E8BF3GA08FD08450EC2085C02B7601FDA093D09850CC20E4206C86308F5489B486948514G3490E8CDA358873283CD810A87CA85AA588F76013A01C276E35D36591CD9FFDFBD7BC74B9575547929B7D83E6A7AA52A87D83E016A0103598BD48F2E8A10EF2C9FFF4FB87FAF7A970908363D16336A3617B581398F24FF2C7D219E528E5F85A2EB0A9532AFE49CD05AE18E73110CA9D5C0B512363DE2BB8C54DEED4CDE20765A9F
	A05F6AF67CAF7A977DA3C467BC3F5DC16A3DF958BC68B1181D66C1937CF03334BF6EA0751E4D4F7B9CDC66CFB25F897379A618C03BB97F2FBBC87BB1FABCAE5B19A10F991750BCBD230258F28F1DF1AACB813415D7C15A1AE38466739C3F2C024C05FB9D0303E6F97245C5D87A8AF9AEEE361B874069A64750200772BF58C976A97A4D23ECAD70714EA592194B5B07483A022E7DE30D245DBBDD3681185211250934F8BF1927AF17D62BE294C366881B93736742B5F5BF4E33C7B662BC3B787C3D522CEF1D1922B5
	E3780C9BB38DCDF43E5B55244C3329CFCBAB453E974AEB8246F7D3DF7FB8C0E4357B92341FDCA9FDF406445429C95B4152EA3EA67DDA6F104B853A3A0E9A87ADE689659E9AD2251D44B4BABF0E59CDFC73024FE1C0D1CDB8BF8E2BA46938B6E19363A6075DCE0B8752D21BC88C93AC2BD88C6261BE290946F0B9CFBFE74574D2834C518C3E693F6369D61FF46A4768AE32C6F11B4F8D19E149C896D9DF70321E346BFD1F34932C5CE9FA68AED26EF6557ACCEAA6B61F3263FE54A430978E948714G34GE8ADA9F6
	997770G625A1A29DF26E636D6EAD663AE8FA2EEB2CB0FEE26FAE7EBF0AE876266B1DCBADC738E7862DE99C2DC49810AEBB8C07D5362668CA32E0B611CF08D51691BB90238A8060B076BDC9DAE524B776998AE6FA04C2D8F7A6242C691D7FF1062BA61EAD561669EA4B86FF944059E246BCB9237C878BD6BA0ADE30A5BFF81F1EBDA94DCE50BAFAEEF8CF11DADF42DCAF0F69D6EA90E8BD36122D4B829156026DCC45C6C963A26C9E6765361DA46F1ED17F8BDE8256B9E4419DA5538EC3E4F69E925EB23E4E6DF2B
	AF0E6342DA697A89F1280F6643AE71F2EB25F10BEFD562230ADF866296B35C2AC3A25C0719BC9F07E8FC457A12B94E44F0E2FD533390B7F608168351972943D93391B7056108BEBD2EAC87F1B107E8F90975AD6438598C672B2F678B027B499244A59F22EBD292E7520E9742FD9BE0A8EEFEAE377B85DD372AF133B9EE41273C7DFEC15736EADCBA47952EC3DCF69BDD7F2AF1EB5A90D7D9043872B63ABED663DAB86E2B75086BEE23EBE9B54E43F167B7707656C6575AEADCA4475D3191F1B35B68FADC0DCBG9E
	2D27BFADC6DCCA9BDD1BAFEAD3622C546730CD084BBBCCF1C50715762660CEFC4673FB182E73DB8E0B7495ED667D9A43B9057AFE4FF1138E53BD04F0213E19A5080BE638D90715FEC341DD5A0238390769FEC41AD0DF4BD644ADE23842A3A2FD6FEFC3DC51913A57D1FDC4E9C78AAE1963DA982E6708527FA93818AA44B90F50FD13D0213E61EA4405B3DCAC6ABB224699EAF8FE0F50BD19B4213E6535BC3F8CD7582E74638AEE9E479535537D1D6AF6113E69F5BC3F8C576323EFF63B5C7F55737C3653F1A6341D
	565348F65A4EA96E33BD08AB6C2038128E0AEB68D063D2B84E44F0E39DA2FDD37772711C6122057A0E6EC3DCFC874DE7924325EAF0CB9BF87B781256A7DF9C1D7B7DE98B190F4A4F284E9A070D5EB27788CC71EAEB8F498F2668F3A92F144770C5DF62732728ACB2076B02FBA7D06817B8F7FDFA99BEFF1A529FADC53912327CBDCF0B7C12797B25B2F7AD6D994922F3443FD4497B59C721AD9E4579E156EE759EF8235B6D6835495B6FEC6E7560EE755C16729E593D942EFDB6175B538A0B8FAF4E539E5BCD7CA5
	7B730C4D1ED77CC9FEF6C26630A5E03787A87CA86117DB491C1F4C45E8BEFBF7E2BEA71F1FA1755CBCAD73DDBF79CCBECA73377828124F4297AA33283DF0ABBEBB2CBC866D7998BEBBBCF072C3C97D6C90569196F3EDAD1BC8D35965B6140D8239E4A04AFFC3FE96C3742C6A04368DB42BD31EB372F9AF2110CE7A2CA0862EF44EEE731278394DE43EEDB6D119259611CC5EF11489B9C576AF4C03E42AEBE24F3CDEEFECE078048726CBF139E11922E71056FB42B3579C27B1B19CD7E29313B2BFF396904D4B8B83
	D89ED3G13406D9E304AEDD438E6318D123272D8EDEE2AA3FA906311FDA2B13F7E846AA8F7103A6936BAC61ADC8E375BEA3439A4547135C39D8B2AE77D906A71000E982E675E21FC26670843C41682920D5D4D42F8271E40F2DABE0C7A96812F7CAB5497B6CC6A12FB50686CF4D00D947F9A47DB819B497123A30467ED9C72B8386DF7C6C43667FD0536670F202E8540AB6EC2DDE6398EF80FF9DCE6CF2F156A5AA14CC7F7976AFA420B3A1C400B663A3E9CA53CD12DDF427A1442F5BDBC0A3A96822F74A46A5AF3
	1E78B52A716B76BE11AE53C954750373D8F73C400BBD096D6065E3EA393413D42E70945AAA3DA0579F3357C36B738F572A6BF31E71EAE1FD8EFB5512D9FC0ABDD3BF255467124479197509D81F7BAF707A8C1804D36853E20348276C2FB1AFBB46D0EEB570BA3EC639FF6B934905F239B738DC84701238DC2ED02E209B659E3B08F2C5406B6A46FEE955DEF21577BAF8DB59FD09DC87EC032C7D0F1D44F8CDCA18A92DDEF93DB0DEF25F59CD6394572D44EB610B73B373F87B9F3A04FE641D063170B416517FD01B
	C6AFEB9B1FDF66E58D185853180F17B8BF8DF8C55FA07FE68979DD04778D6ADDACD54055A8EFB81451CB07595567F08D18CDA5E404ACB30EF84A647D2C32C6B939197C5E02FDF2FF866A1D83BA25EFD1EFE286F9D7224F51BB64AE137FEFF338BD3E8A8F10842D3EFD19282F9AF4B93FC57FF332109F8D3C944E7F99672F6E01FA52037C6FE5A3BF94F833B87F2B9C64AF82DE3991791FAEC13E95F8111C7F7292EC1F7300D7F8866B45642F8E41F550BCA20FD087DC0E0156C167102724CD993D4A64243AB6B953
	F678DD862D997A6D3AD3DB74A8093D93E34945F7E2DA4E60587B5627D83F0A8DD1D27542FE3F75EB5299DA2F224F285E599958159585C47333A40F774100A96F05B6A05B7DCE9E5A3576E25EBFD80B58C8604D635829EB91DBE8C2EC4DBA4436GEF1289310F2DC3EC9C471EAEC2ECBA70CA4C08FD298831BDE644CEDA0F58F0604D65586C8D084D6BC36CDD9B905BGBCEF9FE2B7EFC4ECAC47BE3E9131E940AB32A0760BE244F6D9907B07E22C9B1301B74702F57C68A652EF9ADD169D2CAF5F04B8498A73F2AB
	6256FC46F09594F779B344B981934DF13F7DCC2B2FF5B362D2G335A0638584DDAFDABCB90578118D00EBBDF2255B71F63E681E691479936E8755D3385F165E793732DE79137FC2BD65F392D080B844CBC0E1B37D52B2FEE9B628A7BE1BD5A0F38695B347AF2CA91B789B0F19C672A526AFB368AF16900A931A32E3EDA2B6F4EEA4475GA61C63165420BEFADFD603639F159B28C13939A013B700F2C9B5DA7DD53508EBG0C17636E28557A1BD10738D84024F11C35CE2B6FE79CD7B488E350A062AA6A357AEE2E
	473ABB99B0F3B8EEF1BD197B4B73A2B6EF26FD55F08DF936B7A43F17C3372D1B9C039E17436EAE59F2020DE153D7E29FC3467C7A4B5F887B18509556CC49C17B16920752474CFAFAFEE65253B806356F6173C64044B8503F9F6FA17E0D6A7C7B394A3F13BA7F284E7C3D28B395742DF122CE67DE75DC10E2FF4A311D008B6358AD7B5473B30A3D308F3133813718E3FF338F675454479B8D9FF09F8F9B87070CF659D159C354353381F5D51E03354DB9FC976222647BAEDD54B9653D1DB855EFA47A0ED90CBC06A5
	1C236F18ED627C47E5FE11CB61D37B7749E3A13977C0DA249B5F0B7A380E2CF993BCD0770024A168CB002A013A06F02D59DCC51E630E7B5ED96B20871622C67BB14D83A7FC7F2B0A442D5FBC4A6E5F29D24F1BE89C7EEAA1F1708C85FE2F89094A65DA09DC585FA1C76B7416D36AF7DD335FBB1419A52853725E82531DB02454E99A3BA76B493C31E0987A99208620AEA06FB046AD243AB4D05C709DBC96B01A27BB2C413D939A93245CAADBF0F29B0214FB5BBE1EDCB844AFFED8DB1E6C1D38017164D2C0E64D08
	D60E364F3DA7C8F98876F51A2C665E7E7D8EAF1BDF7D581A47ECD50E607C6A6281759ECE7C0A0BFE77B66C4CF65808EAEF6348DF32286EC68B6ACE984136E7177739323DB047G2A866A847282CD810A73E2BDCA2C5E9F289EB5620360CE877F59647BB0976318BAEE9C65BAAF0C7F6B816566FAC5F28F8CCE44DE1ACA164A458714AB9805757A282F5C6621606C6D8AA85783363CAAFB34EC93F83D19B90A7D7A2A6160FC5890D0EE815848BE6FAB77C61076F2C682492D86DB6567FD733C5B0BF9361F47BC3F9C
	D017580757824AC5010DF81D8FD6AB6E45AE62BE7C429B1C8F73834A15DF00796985DD9CF85907DCC09F9E9D8D4E07A7824B010DA41D8FC9FD980742B174611E7341357107834A350C097A58CD72B39EC19F5B89D36783B6335DC4FD7DDBA77A5AB3067D60CB72BEFEB85C4F84CAGCA83AA388863AAD05BC56C870BEB487BE06376037206817401F237276FGE99E4FDD88AEB617834A59AFEA65E81EF7B6E31EC3AE7273BCE34179509CD8CE684383B674A1097B70F610BE3CFBB1E03FFCC96443DEDECF0BAF21
	8F7F99D017580727834AF57B78C06B1A311154B58F24CD860A861A8D148614FD996A5BE52CEB43B58147DCFE8CE2A2E36E05CB4165733A00F28D17C5F2BB831E8F925BEB8CA857A534773BA06D7DB9201CD3E86FB65D3AE9E2766E8DA897AA34F7AEA348BEA320DC2450DEF3E6F0760E85140B935AFBB7ABB8FB9F87141BAB3477F41076DE8CA817A634F7EBF6F076E6841413A60964CE6784E72FBF205CEA213D0DCB023337A320DC39505E2BBA39969F1C585E9BBA39B21FFB13501EBDD7ADB7660391916D6FDF
	5CCF7A7B49000F811A8D148214FD852C39008CD7E0FF7FE48DF98FFD227DFD9370DB9C0EFEC1F74F7CAD488BAEBEFF53496962F305C86EC60D5C58950163C365BED99B1C1FDBF4F23EFECE967AF907C6AEFA42FE6EDB971C1F07F5F23EFE468B7D7C29C6AEE942FE7696856767B91D1C2F1FE9C2BFFF2B11132E1C281F572C8F4E4FA9BAB9DFBFD7DDA9124B5F2016AB1D301F77EE884E4FC7F4F23EFEB6887DAC5D28166B1A301FBF5F981C1F4F6B64FC7D348B7D6CA8D64B05CC584F570A039BA75E89A897AE74
	7351CD4159FBA220DC2C505E551F85E76F2600F2F304765AB787E76FF4C03994213D0FCB023337B9205CA2213D17033477DAC039422BC4F2BF5C921C3DC7834A15886D0DEC8D4E5E5581659A04769A3785E72FBD20DC17505EFBD54159DB97D04EA93477FC1076DE8DA897AA34B723BAB8FB8F85140B945AF35784E76F0A00F2F1C2FBFBEA033377C5C03939C2FB39F541597BA020DC1A505E53C15AFBB1201C94A25CB7288FEE1C3ED7A7D766F33F5A479EDDFF1CB01075C7B92435811900A681C5814D82CA8D41
	75472735641C6BC476B6832FBD0E6F89AE0F3DBAB95FBCE6DFAD12DB31B7B8FB1F6A64FC6D2D915A1B93243DDF8634D7A934F7453E606C5D2813733557A634D753901C3D03BAB9DFFBA69FFB74FBA9D364F75A47A0EDB290651D4BA43C19F01FF0B56E55BE185D8257F4384F3BA6B13F98289E2893488994869485948F1484148ED4F0EDE2FE89D083D08F50A420C82078EBD1674FE51D732E2567669641F5F5A8BD0BC04ECBD4436FAEA0EFA8BD5FC14E201073F06121747C9CB973964E4E8E12F3FCD4E7ECD6D1
	4678FD83718FFFF7DE230D49F8C47CF78A70E19354C172CE2E8557AA78E296B3B58EE3CC6259F06D783A5385366319CC8F0B3FEFBAA9B79A2B3782466A83556FF7A798A7C2647BG4B847C9593145F482E9F7B547315BEB8725EC60E1C977AED0271F4FEA460EDE2D772CD03CF846979921E1358D0677F7C7616BA71796D6DF562735B3B6A5E23FC50217EDA93266F299B7FFCF7F31D68FC7727F2BCFEA93FA76B9A9A7458864C32D97A0C4E6CA5E582A3495121C1BA4C904BEE5FF7D1A49C3BEEE9A66FB9450562
	9875FCDD05249F231ACDB60F4305CABA4DC6D7FBDF9FFFA0F24CE96EC51F936B7D476F39FAF17CDE2C97476F157A71637747FAFF7123B1AA3ACC6A025BB1646AB54B83EB7B30596532194CC70C83E606791D0754FD5D19F899016FFBD78E10EFDD58BC6601B2725FB10F51E3EE3212573FF42FC837C2BAFB21194A9E9F48E77ABBBC0E0E9D92DF4B486F71A405EA5BED9A3BDF9C2AEF4F343CFEF1003C0FD4F8DDE2FEA91081289BE86CBA7EBE521E006FF1B5FB056FF1D13F8E486FE8C5DC27359F4B6EE7DF2777
	0BBDCB5B6B3F5CAF6E95177BD5FB17717271AD5B6F6CDD4D5314D99089AFA99A0A39C9167798DD96332EEECC1365E91ABF793B76123E6E0C4DE4F3319377344CBE99D417D9852FCFAF8FFE44F56A6B9315FDC5A33B1357213787032FC35F8C29F5D6F21D43C34168E4F3F2397E4D73A94F856CDE7A0E3E1C294DAF06553E6C627C5E619C7405734AC654585D1CFFE0A4F83F6B64F7B8D67978D7426E6B7D7A7DB72F5A17AA4E2F70866F4B0070DD3C8E6603492FAFCBC6553ED4F37E5A51FFA0AE32AFDE9F1BE16C
	BE522FAF2FEBFC29617C256703776539BED1DCE6B29F66787565057368CB3347F1D869AB57F50E378ECC86FD69BEBAE97B7E43BE7FFD526B7B44FD5222FD7E7B24F776856E13729A7C77C9AB9A8277C92B9B7C0DE9B4C6519754B12A60B12AEE9EAFC6595F05B977F735B1227A8647547AF6F2FD8F0D7B5EE7A968EA906A2B3C2856D74975B5896B84D2A768328A753DF971DD2C1FFDAE47G0C20B4CE7D0D7E4B5655A8AEDBEF237F323D5C9838ECC39B7D176D0D0D014BB63CD1BC5EBCF8C91B478A46BFFBD94B
	5F097D042465D7B2FE22240E3D52B7C7E72879CABFBC162966ABFD627EACB5DF691F723255FC252FF8AAFB293A5D16A3FFCA0E063F83792716E87895B83EBE71217C6D9D18821A6929B00351E3E4B2C5391ABCB76DE06B5A2B6473D2E3D0F7423E0BF320D7777B2F9B697B45F563AF7B7D570D3777872E9BCB1A7C570DE5CD016B4607CD63377B26BCED7EABD87E6F164FEDC5C25EE33F4B4FA52D55E2F7B26C4372392D5900CB625861F5DAECA543BEA71F5BCA835CA20E1DD624456EE2583765F3DB85E130EE8F
	C36CE36B35585D8CDBA41F5BAA87DC834726EC50E22B9836C9BE375589B89347E697EB31558CEB114FEDF981974A31A51B346DE4970EF9BE7C5D0C9F7F1916DF457857EC56722B993F23C44B2F413906FCFECA55E715D3FE2AAFFF8765BF3055074F4EA5F9377970F7D2FECB158F3F12725F2F76616F227CE4DF7EEE4A3F2F4607DFC5791EDA9FFEB5656F2D73615730FE21DE4B2FE079CD2CD77597CD3BF8FB68B178EF1F960338FD8E987C37CF0FA1F07B3CE8705FBEC38C015B677506715BE7589EF5DED5F35D
	3DEA3E52473E23616F607C38FDEAFE8567BB9B54FCE5DDF13FC43E45E6B70EBA06BC1214F73DFC7E09FFF3AD825A42CC20E42074B0FA66196E937C5B88293BFDAE63GE4C3DE40274A322119A85B8177A6209020A8205957E33B5A1AC1E48F35994F186DDD0DDE1B1B793246C66478090BAE23FC500A65F763D2C5069C1D46B165DBB55F68C57E240FE5DD7C4DE2354C40C65FB4C56E4D0F945FCE2AFC0B1094BBA7D53A7EBC2066ABFA6E79C4EBC3ADF33A53B7CD117BCD3DA686A755B1181E2B78C63EBE073E6D
	59731EA6EEEEFA681F17F1C70BA67D24EF7AC3175FD7272BBE6EC0530BBFDB26CEBFB6E8F4F2597EA39AFF59879D68197995B96AB37325B7507A53F1831E291FF7D614EE6269CF1C9225FBF97ACA03A8BD0C275F39DD149E4953DF4D95254F6469477AC469F3EE40F9423466E56AB64D76FC68F812A83F73CF3ED917FC836E17FFA83FDF1D8E77520D489B31619EFA410D2877D7877C77EFAF9C9077EF298744FBE03FBFB07E9E586B877C75CB543F53964CC7B7777987964C071373B22D180FB14ECB63B1C8603C
	D29E03944E3B1E47E081679D7522EC7DE4645D66C5598E4EDB6CC559EE4E8B31A2AE0673DE617E45F15ED16EDF5E7710F78B17DD43F9EFF059924ECB6FC31C17733E34A0AE0473F6F7A2AF0973E698880FFC4730787B48EBEFC6DCB56745B7A1AE14731E64310A603CD8B36296F05E29BE44C9B7A1AF5A02380E1B781CAC171D8D5E6939C956453F9FE0621847723418E778D97FB80EC9D82B457CC61EAB12B3F0F3B8A6FD1D96332488B1499C33EA3D9633F5BD5ACA67182A8DDA4CE1FE16DF1A0218D39B359867
	C634D548B1170A3518A94508A91E02E777FFAFFF7702702A01671C02635F0E9E4DD996321DD1DBFB50E5B36DB7DA5A6451141EE72848C01D51D370DBD0BD72B7AFD260FE91D0614DF80E507B991EA30C39FB3A94BEA87E96D4CA67454C121B69DA370DDD49B942143373B359B90ECC34998A6933EEA63C64CCF5DFC6727FCA86FD76C75317E72953C91F700C24CEEF502493FF2F50483BB469A4C66F30F41A371FED423CAD7ED92CE4FFB8446F5F0D493B05662914DDA727CE4DC87B5179AC767D2DBEBF5F5F12C7
	C51A7F83B9F80644F48BFE17E1C5BF1E410A603C3EEE64A5F15EA1834A965C0A3CDFEEC71C01730A7210B746F95F61E7D7E6820F5629372CD8B7975C4A3FBF14035F8BD8FCAB793BBEA70F1088F942897E861B014521A1DC3F4F15414AA617696C848C218F18DDEBB8BD53CD085A3F46027663A10D5A7BDE3BE26FE3AB5ACB70E3EF65B876D2F4765E33717CF2FBB3BB94FBD1BCFF0B7D58DBB10E3D42293E76A6F9515E1A29E86FD7DFAA76D60E223D5229E2FBD9AA7235572032C7F5179ED3F4AF6631EB7323BB
	E79C5DBD3A3CDC60F5454E73525E2958DB4EE3B76647DE7EB876A66B7262BA2E6836DAD0F7049F5D6F0E23BBD61D97D95F61CE5497AF50270E8B2139D3357AD2F531F94A00FDDABA24D19BFD4DE8A3FB1A58065A4E6AE9DA9B2553FCED3C5204B62A27210D17F84C5B7C58D0473C474706C7E763B9B35A1844ED1C6BC39BD37C58D047BE5A47C63C4E465396BE76CE43BE67F1F91C7738064C34BF0E9CD1FF5B20FA691542EFE693CAE6BE25336B628F26E5D01D3F7C94F5527B3F2EA5BA7A0CF637726C7353A2BD
	2FF13DD64E34C1FBFF6DC63D4C7D45EA9E5D93CE8FF8CE4B17285C7F86A9D795701C16AF516FEDD4484F5ACBEED34A09726BE4FE250ACF679CFD99B867E80034285B708CEC4489B27665C5403C91289E28934883B4B982578E21874B25G4FEFBB9D4EB6D8DC58D54B8136E79F70DC18AF513A747FF2F1EC1F79137B2438EB8CFE3FFF9095411E8333EB494BD3B3481566EFFB97495FC2C8AB3CBDB13F92288D288748FBBB66EF6E41D21ABF72CAC12347E36C354ADF2F64AFA8584CA38A1BECCC30F79D05E72642
	EEE74F37EE5737BBAA372ADF78AC0A616740B559C7161D516A964925B3EC769D09792B6F903E43EB904915B2EC439D7E7CFCFE3BC82E13612DF09D935A5B24BBCBC4E8B243C641B5CEA8F72370F9CC824326887CE4FB0CB6EDBF740B2F557DD061B4FBA63685DF7905CC27F427B26725732E7F606716ABA1ED1DC467ACF912720F0EF716D3922F33AF95E01DDDF37FDD1261D7064C0DFE6C5A193DF8155D040122AC1A1F5BAD189FB6FF645F9BD8F8A70E61CF13BFB14272DFE253621F653E17C6A2FE69E985FF2B
	D70BB70C7239AD47FF7E8D4153744F393E881E5E712D226F7BD62D3EFD4A19EF0EDF5423681BEA6173619FE07AA7E71474CB7C2C3E61872258276F23B2FBFB959FDEB1287BF22A67D6FE7EFDECDCBDB3CC0A1E45CDFABD6F1F65F370F1754CB1ABFA524DFABD5379DCFE4138FA7E502768C933287550BEADDC0EDF71F4D803CF47BE6C116364EF601CB01E553F9D854C8E1743E9F6F9C6A9D77EEB89780CB61358E2E784D43F6D9978B75A7E99444EB27CD3ED50384CB861FF5FAB6604F85F6B419362FD2F389363
	3F9BB76B047F3FFD727889755FBE216578E3791B2EBDD006E3C011C0B3016670B2BD7E5586C99E1784E3D233C97EA299790B0A9E34DF5D65BF2F7B3A827F9D4616AE326FFC466171B88608FE649F69A26565F1B8E5A6659D6BA2B1F259ACD68F05B27E49AED2B76D66BE19CD77084E64603ED66AF45CB73A3884771252B9EFDBBF629A6EC2DED8BF625AB8EF60946EB9C5F05E155DA89B4BF9AFF723EC3C226F984A96C4A12F4F0032C51C773881E5CBB96F756D089B633CE365088B653C6F67220DF91CD71F0732
	8BB86F76BC14DD94255DE3A332ADB310F7B75F976C663CDC3EAFE81D01F339E7481F20E26570EA57A6FF7D81541EC346E1E37B193366DECF2751425B63EC4D7B95F120B7E146786FGD33B7FFB162CB5D2G3B9828686E447CEA20B6A0931097A88CA892A88EE8AED0AA50A22002FB927357G1583996E41FA9FF3EA4BB8FD995B9013AB3F4BE8A3559F67B91AF71F3AC01F7D1E717D27F23F5664FB9248CC1910DC2B663BB951A093BFA1398A0D3D39A013BAA1B903C6EE914894C4CFC46E8E0BDA2E88E4AAA7A4
	F715669D279610691E10DC04663D9FA74804CCC86E2333EA39F0100919105CBD1AF963AC10C98AA0C76B596D96721B7767F4D8E4E9D172B732ED03B60F4DE8371D17BFF1CFBF15CB537F5D6A3F2F7DAFEB603E76A9F9CEE706BE32F354491E67BEE3A54FCD9DFDFDEEB36F5367D98BB92F555D61F05B08A7F41E70EBBBA4313D11A9CBF03D1FA648BB4EFF7D6D01AD0E765D47F9FBC051FDD7BF6A4E3BD7BC87976DF7FD42742D3957F7AF6136D33817D0F2AF4E7F0A9C0A3DB73B51DE3D9FFB22BDB03457213377
	70B134574D6DB5B995FBAF98501ED3E08F6366AFF6A1777A462E6F1C227BECB96A1E6CA7AF227DB54CCB14AEAF0F66E2DEE2F9DE2EF3A976E666213D59FE6C09765C505EBC1D3D2B7A50DEAA37B74F2D58DB4B77789679095D32F1E2D7F01FE20F0EEB4F4BEB07550CCFF9A9B22F1C71E83BF83F5FFF7B7B38BFF07B5B50AF1E6B14746B67BA1F770B67BABB7B55F39DB6876BA7793240985545F1FBB86FA4676D17F5925ED7B613474A79AD32DF045FE2A686799CCC76EBC466F5619CCC45BB49F8DD7D248F01AC
	DB9DAE14BD5DCF56E7FDB63B1D5E9BE50CED903A949E0FBE59C71B5328723B1F73CE723268336A4B275F2AAE9F4AFBCDD00EFF3169F1BD23FA1CF9D40FEB9568BBA2507735G57A340BDEC5163FEE2516326F56AF1F7F66AF18FB56BF1F14DFADC59D9BDEE67D9BD6E5F7B74387F6CD363681A723F16601A326DBEEC1FEF11A991FE232F9F53C3F87A5AE1C94275650327746B54ED27D1A61E4B9C75AAB2894774B2AB78DAFBA117999AD57C382D1C6FC95C0F696167951D0939FA1D1695FCDF024BBCFAC1111956
	2717E967FB8B91DCE6A3795347EC4CB04A5FB9E7633A5C74A8FF0DF719424726476697CFEA3E9D1484FA536E1F483C64B94DBE5FE210A90C1908DC79E935DCB148D4CFC8EE15E65E5586B2BD93120BACD74BF9C0A6F4C2F27695EA398810091D10DC27E6BEBA9BE466851023757F25A57AB6715BA57A36135E2F477D39DF0FBBF8DA0FEBBB2D47ED93346DF2C15BFE24DC0F0BAF57632EDA2147052E50637A85FD14D353C7FDA437033BF3CEE7CAD24C9F259C0F341C7C891C22DEFC5D4AA3ADA3FF43264863A89A
	A42FD1F92425A617F184EF32492BE09EA9173D3D25E338DD528AFE3222D59EB0D206F756E63DFCD7D626145DAAAD2183E82B14CDC641D6A9C79EA4DB25AC98C03D728C8B7E1109B49954A477B96912DC85168A39E2ACF2337F895CEC2135203890682EA2235766AEF2779A6D66DA886500EDD03E983DF0D9F6F23C54A5722BF735DCB52A15C94365B4816E7FC78D927FFE79E5755E7A222BFB7DBE634B0E25EDFAF52C3247CD778B0665FD0FCE60BBE3F14D7F5F39646FF77B5B2FB8B6E0345BBDC6FBFF6320096C
	DD6C9F751815FD378BF9C43F9BB1AC1126DD19C77CBE2B1279BFD0CB87889E7D1ACEB8A6GG0809GGD0CB818294G94G88G88GF4FBB0B69E7D1ACEB8A6GG0809GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF2A6GGGG
**end of data**/
}
}
