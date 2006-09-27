package cbit.plot;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.*;
import cbit.util.Range;
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
	D0CB838494G88G88G58FC71B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DDDFFDCD4D55A27B75A574AADACACB45A48B0E9351612B6EB2D582232320DCDAAACABABAAEA295856B6F6E36B4A0F81958295919595934D4A9091950D72E72505BA6AE410A48C4CA81383C3B397980D72FD4EBD67B9775E39E7981AFD773F7573F93CF31F73FDFE1C677CBE771E0BD432BCAAF7E6D609A44D4C15583F4DB3A52975DCC99A59F95EFE64A53ACDC512665F8A203952432BF260BA9D529A5F
	B49595CBDF4D4C077B99F01F16E1AADA883F67CB6619E967408FBFF692A06D19F346EF9F584ED38DE8A7CF39BF2F37G2E6FGE5G0FDCF7FB087DD13DE50CDF6E21FC2419D96002CCB0377616B35E9040C481A58225B0FCA6DCE7A9164F59DA47F04D43E224247B62738A099E0DCE24E6194A06302BA46D49DB53C3ECBD2F78C9E4A6C0DABA9049FC36743BEFD742355EFC7244276DED8E392A5B691237EEB5437FB53B8F5BE55951F542E336F6591DD23422272AAFDF1113A4A75CD777925E6CDC22BB956EA538
	AF6BC5FEC79E614B70FBA8D0F4AF666713776A193F0DBF5EA42516F64FAD72131F093DB49FA93DEAFE36FFBAB6EB11927B657BC90CAB7ACCC53B019A017A01A201A682ADD14AFC753E8F603A23CD5E656A6CF4F56D686E5E6934397739ECF6A77C5E3A95D2986F103D5D6536C992AD57345E3582FF86A2AA774FDFACC7E39233814BF85C216B240963D3727284B11998F3EFDED29F0DC5DA1F9A93EAFBC31FBF5B85DEFD9D545B7EC6525A1EF4422C548DDF5BA996F3D18553D36BD5EDF7FBC9D97F2C54818B7C1E
	4974D27E798ABFCE43277531D861137BC4C82BBD05F523719D7425A1E71414DE7D1CB08E5EBB9E4B0BB8C5FD08BD656B4B5A59C47734D354260B7B62D4788927D5BE7525CB292764FE5C0F2622B5E7CCC534FE2DB11376997DBF2622F820E420F4206233CDC5EB01361F0DF54C35EF5DA06B18596A16F776B81CB63BDB2232EB761742556E155D563DDD9E595A55EEBFE2F5BB2CEDCE3BC7E218757BC98C5AF4F21B143A5F8EB63E30F7595DD659616A826D56CE3BECF7FBA8E65B7E39846336AB2996C7279D8CF4
	F6CB28770B7D73616A34FA642F3BEDD6592EA2E87A017D24CCFA2DC7C0A3A8005FB4AEDF9FA471EA057CFB010670B85C27687B4A6E066EE26B5686176BE4CF370564C4CABE1B4479CB256F08045FF1E7E30C8F9EA23C72D028832128E76601F99ABD3B9D6D44FD2B7B943631505CE2D25A00378105GC506E27DF925916B4F6E42EB2502071F95561F462F4DC593C2E93DC98C75EDC743947D44478CC8ABBB47D4D4F78E767725390DCCC788135B932A36ADDB28381DE9A9F2G8C35310E5B48E357EC8DA6B700FE
	EC577E52E85CEFBFCC4A309172A583051F03312FBFD0C16B72CE2B470E3577009BAA1E5D2616847533E676B4664FD7C07B008EGFD82340D68G5A85749950C2E61758D88A34DC2263A999FBD6813D8774BE5007C09F811176349EE88350C6200F018E829D867ADAA2BD16A4B581FD813497288548CA6ABAE9CBC0A7009CC0A701BA010E82FD8784CDCA6A816A83026AA47D88749350992033A45A871C8DF4B150F0DE9EEEA51F63A0C6C9C019C0153FB295B5G49C011C089C0E9C0A57FEBAA2A8732818D850A83
	1A8614B3847A8CA08BD0BFD0B450E42099C0656742788564848A871A881482D4F09E348920E620D020F1C0C9C019C0156703FDA099A892A881A88D28E4A8588732818D850A831A861473EB308FE4816A870A861A8CB483287C822C5BF2DEF776AF2BE77FEE39221E0F7D549BACDFED7D925583AC5FC07540456C852A87E78748B7560FDFB24E7F177ECBA222ED6FF03E366DE58FC2EE01746F35BF54C35A619CA03226A8D2FCA163005A8EF3190FE4CC598C34C55237D7EC07015A2B03598B54DECF864837369D7F
	177ECB7F8E51B9CF0D1954FBFB2F3DCB5EED6F36F75960070725EDB113FA4F666747DDDCE61B426F06797CAE18C0FBB87FB3B3E9BFD6D9F6BB5AFAE4BBAE21F97ABE451627272DDBDD8A20AD0B1934B5D79F4C67B93E49CC66426D2E2EAE3BB27962A2AC7D28B29737BB6D1D6074AED7CF17CC795603E41F62247D94DB8B588F1AA4B217F77610F585DD7B47DAC93B6FF6BBBAE1CAC716A6D27885E41EEE6DAB5344E8B770489EC687DC7BAF40F9F65FCA1CE7D71E3EC60AEA39ADCF34E6740EFEA2EF5A85F43E1B
	FE01BA4F26BE9DB9A4763DD9D993B03E077A7AF2A711556FCB50FEF23E34300D4434DB17361E25EDB9CA7A35769E379BF4B59E32F61D30CBA8B73EC7135604E9F4FE1CE1A13E95DF88F3AD207A8BF1FE1CFC0824635A04CD0CF7391CCEDABC1056FFA109612339A5AC869638F7DEC8E3788ECF6FB7E1FA825CCFB3243F465325B0FDBA75632E43E40D62317F50E307A5A3D9E4FD454BFA7261F9BEE9C7D83953742443245C1D1A75D9C49831B9F5B66EC71541FDB510994886948A348C789497BF87F1A9E1542F4C
	B036D69A26451DD70038B543E87A76E1D4EF338E77D3A1623A99EE88DC230679625267A3AE1E611207D17F7438A9A508CBE7387C0B404F0BFCF137AFC0DC7DC594E701EB0781B736146F53B1DC94DC638C38CB97A2AE09615260AADD2C62BAAEA638334B90D7F1B1DDDF123855416F460BE999D35C3EC5080B5360A69BF07397A3AE6DE23AD6A5381CF0DF5CD40E2B8CD7F1759ADCC4B841DD3184F14D61F4CDA307D37BC360DA45F15F1473FA90CE57BD084B54610A16A22EE0B8DD9B494C3EDE5F1D9CD7B91C2E
	1F90077AE8BE7A1671F29BCE63E6992E46C7933F65088BE738F1C25C0A8A1E0F61B43EE2FDAFF0DCA6430975DD3B82F1A51750F2A07A6A8CB84F3B08DB4BF0630438ADAB91377B92DADEE2FDEFF3DCB343796A4B3804606E2AC41CFC89DD1312B813F6BC946ED300A86EEF2B385DCB693AD50BEB3E94F1256F7176FBA9DD5BEAF1C3B84E435BDB6425F47D2B4545F15C253C3DCD3A142E0F3538699CF78BEFEFB3AE25EBE9AD2EA082F1CF286DAD022E353538BA0E1B495BC7C384DD0FEBF1B66051FA3A0A378FEF
	84DD1B07C528F1D66B732B9C9745F093A254762662EE666DE8D284DD67CF976ABB1B370FF4064B9FA152370F370F3291F48F21FA04C85FC25EBE36B3DC6388355FD0F1CFF3DC47883A9F91A254B716370FB0060B916A6B616DE342883A5711B8C2EDC7AAEE9BEF9F5399AEE3045A7FE97A17AA44650F247BA695A3C57A26F2DCB5436D9949740D5462462C63799DC977E4C2047AFE286679E5381811EABF2E62BEDE4F73BB126E6FA48A756556707CB2DC060F3E664B886EC10E4B3F0C0EB3951751FADAF799ED67
	94F76E864445B0DCBC43A56BF03BEAF93F46F0A511A2FD199C37A6120D6711A2FDB7EEC41CA512665349F07D11DADC67A65EBEA2E9FD724551395F5F5EA773D16599557756DE2B374A5387D33C2DDB3FD49ECC5167D277ABFB4B84BFA1921FBF3DF11A4C615261BE7FF2085165B8F77DF3A1BEFF8ABF99A3C5F79CA87277BC2D6EF2666F656A5CB5E55676D9F40E387308321F8DE963AE477961810BF68FFC074763EAF7A85B6FEC6E7516C5BB37253C6C2FE7437538436D11774262430B7334D9DF93FF497EBCE3
	336795EFA94FCE489CB69E6CE6GD57FC629E7A6B267A7F3B11A4F768D184F305323256D43D373667849277C9B1A3F705F2879DC7B735F73293D2BCC786CF0B22467DC014F8EBFEFAA15344F8EE19DF1423EF5AB1BC8D359C5F9A8DB8FF2B29065E74DA9E065B58EF8A9C00DD14AF34AE3A59A3F4A236833025DF025F376DDCE92BF0F1D4C376DB6AAF3BAD7A493254AA84FCF6CDDE42AEBE34F3CDE5B311D61A7DFB7CA0A48FBACCF748CF24F1867736222E8CC26C52931315CB8B62B8308666545B9D89EEB2F04
	F162CA34FB50240C0942B50BDCC04ACA6EF0F8280E7185980FC810CF62BABEAEA4F55353616A5B65F6F9BC9DD607DBC29D478B353120FA564EC7BD8523A0EE23D0CF5C7CD9CC4FFE170DAC85A49A3B1704716E9F0565342884750581EF9257F7E98929CB1EAEEB3745C5B5D2FCA647C335A92A3B8A71218B884F3B23C7F6F15BD3CF0AECF7DF0536D397202ED060CD643A3ACBC93E3D07E537DDEE6F203A8ADBC43AE6F0DDC92528ABBF9A7A2DE854553C1070CE697CFAD09893EFB46A0ADF083A0682AF016B5AD3
	C67CBA2573ABADC724AB136B9AD706F527F8B40CC723319DDC32D2AB97B21A4A450CC6DB1B97A975476E15E9FD3EF61936BE2736CE9156E7670BB766CD9C4D1E290FD66BF36255E333D22E467A5C3B0857672B21BEDF0DBED53AC4BEC5DE0DF929DF0CF23140CB65F24B3E9349D50C61FD4192142B851EF38C4A9D4B9149C5F339C9DCEE82705247E03FD472A939AAFB9D3C2D6CA8A757CEC797EB7F7DCD982F1049F1D2463C470571D2463A989AA7F30C9A2F483831D931F1982F10F274A38AB049B1D8C67F20B6
	2DDE56B66A1672323E864A7A9A4CC79A6707GEF8267C7ADC3FEBA617D967566AD2F012BD559F0282297335DFD5C656E04DE000C10D556BE39CA594F2A5A21A4FFC6FE3F0FFDF26FF2545B8ABAA3385EA71613F7A50E3B5AFBBCD54A7F8DAE0F6C2B70F312205777E5856ACB84DD79E3517F6295485F8EBCAF67BF42793140CB657C48F711DFB18E6A40B864372DC4FE9870A6F17E1ACA64CB57C2DFF3AD72DF294476598D3C18EB31DE04FD73A5DC3B6CFD4A8875395B5539372B3BC7DE53200EDED5CA52478DCA
	261D703B8ADAB3745B9FFF7B3E91A531F7E25C2B701D18695762587B66D22CDF153B23253299B77B2DDFE55751FA357DBA35FE0DABFCA3BF9E086627FCB56F0381B389085A0DDA0DF6255FE15E3D3C7F2D83DE776F901BD90258980E0D647D6BF4601545A23622943166D85E67717EF3887062B9F677C244E6F06C333CFF2C3E5ED454F1BDE2CF14A1B66AFA441A96A1B699F845B7A0763CC5085DF383E2BFD80C589060CD60586B96A3B61DE377733EE14DF8D3D16BF82C63B7AEA17D2655FDE2BD451571369B81
	18C40EBBD34EF0B594778C47654741789107383DCB757A4671366C85CCAC474DDF2657574B5BF6AAE0AAEE64EDF019DE5FF65ED69B819346F1E3166B754D66EDF092E0245FA36E078A3D3EA40E2B83CCB747FD3CC22F6FB73C0D46GE6BA47653E2B5757415BEC59CD26A273CD08FB78A33D3E3AAA448D81CCBC47DDD1255717358EF19900291E00B847BA54C76F072D43710F4AA5F2398E100962F21B2B757AC7D6A3AE99B045B7A3EE66FA3D3F3675085B8318900E3BFF3DDEDFD58D9F03G134EF143EB747AB2
	EA306E2E39856A63AD08EB59C0667E4A3C084D1BE9DF553B05BC5B6BD15E4B215B563BDCDD325B65742CF97F889B43C64D47BE060C7965E746897B180E225F67C55CC27B1678DB54BE2683467BFEBE665F30014F9B7FG7346BF20FF0BEA09FF278C7EBD20712F49609F55692CC51D7D20AF0E6B1CD22B1D8BD26C2A0D08CD83DC65C4448E5D281D1FD16C139C5B8C38F00E7DEC934E29290F976EDE40FD5CE76D6A31BA95C7958FD157A51BD057E45013F3AB3E8B11E8F8172E7ED6753D9D3366B792FD476C6D9F
	09BE5B2D749D33D51C3F381F70A7E878547E3F14B5AA39AF388D46305B713DA8FB93D9735A62216E81C5G4582A582254763DAF3585764B96E006F1D6D6D12E1A9EAF59E56BDF042773F0E10381D341FE277DF9D514E1BE89CF2CCA48E85FF8C7CDE9392151B2748D57E82B9DA275F7FD67B2EEBCAF1C4DE3620CEABFB8BCC376D0FEA1D2631ABBDCA660D51400F87CA86CA87AA3E836316F8E4ED203861BBF8ACE0B4CF571802FBA77426A065166785A777D110F279F386122B0678D96E5017A7FBA7EEC0B9AF48
	4479485176796991D29E02FD1DDD9D767613BBDDDEB63FDA5A124FECCD3E8367D7BFFD2E5D4309AC3A5F6F5EC64E1D54F6651DEABD69FF7587B6F738BE97F55B6E4436B721196CCBC5427D8420C420B4206284D351DAA0F3825623E55F6C8CD40FF66003E00B0B7F5C657BB09763786A00F1D46A3CB07E7F8AA85711A0123B39E0B076C2B432D46E1E00F251A0B351A0D7B3BFB8FB5B834AE500CC71DD2A9CAD5B953C5EB45C057D7A721260FC78A820DCA858083C4BD7AEEBC1F0760A834A45022DC906BC97BBB1
	4FB9F7E31EDF8952074C00F275E043F2372F8F3BDBF0AFB60C7B70D8E9F0BE2486141B84B6269BFCB07132AF3F87FD38FBE1F0BEA48516839B4EFBFCFDD8190BF10861BE5CD096DC9B0F8FA8B77D9ED19F3BEA8D694B84FD2C85264E1FBB6CCE9B7575011568EB46BD588FFEF40C48D6CFB295B5G5900C20022016201D2A6E1BF587D8DF99FEC40FED059B000BED069760C9DA04DE37F2260E2F3DEC0391CFB75F2B44FC7DD186772FB31FCF6AE8E4E87F3E0B9218FCBDA5187A777A1FFC9F0BE1486140B967AB0
	BC87FD1848FD78D310BECC8BA8B7434787DA572EEDA1F52D60BED83F83ED87EA868A810A840A3F8F6B5A9D0147DCFE8CE2B0E36E2F4A034BE7F8C039647BC4F21FAE8D4EDEC3C039F4213D570234A7AD8BA417FF3FC86E6A00F2E2FBF18165AA04767A178759E784142B935AB3D784E76FD8C0B93350DE690A606CD586146B905A1B96243D17824A05886D0DFAB7B8FB318165A20476BAD686E72FB720DC2C50DED5E5F0766A834ACD925A1BE1101B6E03935B4BDA65ABD765F31FA934572713AB199CF83DCF7B7B
	8F2D243FDF83785DC04DC0DE20C82089C053A6E3FFDF5AC85EC39FECFF3F8B78FBDC2E13026E1E797B6E6A6062D3ED10B344E732C8EE34CE2E6401407121F2BF9B569B0373F328C14E574FB58F0864E66A667A5B876D67A8439AE1F0FE469A64FC7D34887DDC25DB8FB8876D675D06F544607CCCB248797A99A274F32FEE4D90B1E8BFDFB42CB58667679B86B9DFBF47897DF46A56958903763340B0679E1C1F0B8DF23EFEA68B7D3CD0A717BEE8BF2B8D737241797929C14E574F1CBF0964466B644A7FB4D8BFAD
	C14E375B824AD58B7DDC96643CF7EDC039BDC2FB29C15A7BFBC039D6213D9B021C77468714738A6D7587B96FBDAF20DC18505E67C14EFBAD8165E204760A031C77AE8BA897AF3477D81076D2834AA58B6DC587B96F3DA120DC3A501E7CD1F0767EA720DC7E03427E2CAA48F5E7C0398A213D7CF5C16EE184142B935AFBA4C8FBA981654CC2FBD15541593BAE20DC07505E776B0333678DA897A234D7DB931C3D1D8165A204765EB2488DEE1CAE5890E81C0E7531C7579F235A487AE3922425GE58215A51A0A6A01
	9A017A93F17D61E8A467DC87333799F86DB141606B6072F8F740BCC6A60A645C3541598BB148795A0B935A7BF0E3F076369864FC6DCD965ACB89525EEB816D25886DC5EE8A4E5EEF8DF23E76B2FD6C516F253C203C53DE72E7D3519AA04ACBB8C5F88DF0EF7BB36E5566766F016B90380F821A8814841486147F10292892289E488264849A82948D948F148C1481D4B645D4D487E41902BAAB941D5DD3683939B03846CE21E79148F909C4781D8ED41CC44FF710B3A864BCDCF592BDBFC74E3CD5333303649C9F55
	197DE3454C017B86629F7EA67E8F04559271BF7FA1A33ED240939149BB39C6EB70A40E969FF90CF1BD0B5DC0BA078852AD4CCE860B3FEFFA92AF537D81E335C073FB4EA063C408FC9F20D040AF9A247CCAF6DD64D34F677B6048FB9B39CADE683789865239D040DB452E641B86CB846933A4BCA75155647F7CF6CF13787C760FCD62735BE7B54D25FC50217DDA93261F5BB470796EE1CD22735DCB15F8BC12C55E75F177F4490ECE3BE216BE2333FBC91940C8F228270B8EB344324777DD9489472EE75A49FBCE66
	A49C235E7B36C6B20ED11F599C324B0DCAACF62B7B4071637C014861EEFBBB7A3C6C287F782DBAAA0E5F07C74571DBFFF46078EDB96AAFFEB4C695E7C8DD7038FA5C6DF6E5E0BD50EBF73B9DB67BFEEB271DE1DE10C95DB71C09D790783E7736B964DB978E595ED9C57EBBACDBE57B2E8E727A176195693D104EDEE826322DF3E6B17DE659E5DEAF713D4396520FBB13746DB6045D07A7995BB3AD2FA5F672BED28C24A7GCD831A81D472B0FF9F69E8407738BE738A5F6322FE7DBE1778D57B305E7E9EF65F7C30
	51AF76AC2D59FF395F5BACAE77871B8BF97978166DA34DE5BCCD1D851170122221182794F9596ABEE1B7540D6795F91A66CF7E55E6525735B9EC8EB7BBF1CF4BECF501364CEAF8FDFA36EBA157E92CCFD4B6DDA73B0157217C42606B10E33ED6E7AD57F9567CE0F452F2DE2D543FEE1F728CE5779102F226B60F14E8FD594879DDA539688B67EDD92045EE627C038B0277FB011378BD4E473FF8F61F64576FD525DADFB6F37EA725417B3237C534BEC9E5BEE47A75E56EC22DAFF51C7F7E4260FD192378D27C085E
	E6A53B2FFB441FAF7F52793205739714856F4B1ADCD1DC9A188F2DFEFDF9318CFD79CCF66DD37B4A0786FCDEA6033EA1BEBAE97B5EFC4CFF1F7469B1F11F347B187FBE692BE3017BA44BB17FFD5251E3017BA45BB1FFE39A7B4E48A2ED0CEAF80CDADD8345A89262936B93A32A6F54E22D3E8DDCDFC56BC07A92C0D732D05F36A5DAFD35DC5F481C01742501AE69D1113E5C72B9D8BF0F3BDD1DB00252B85D54623FECEFEB9117EDC20B7F323D3FA5F059A63578AF5B47DB8217EDCA0BF83C3955A70FB50C5F33D4
	4F5F007D44B2BD3FD642739E5A582BFD737865DA3E5A8F7FEF05162F76097BD6E879EA7FB47FDDADDF6DAB263EBBDB5BEE2B11FF45CA9DFFBD726D15BAFE8D0E2F7716AA5F5E01A9201D1E8A5BED152DCC6643D53ABC6FDA4F563597A86725CA206ED4BE0AF3200DD67FF5235EAA2E9BBB2D7E6B46DEEB603AF1486A3FEEB4D983570DB66B406D3EE935BE7FB5AC7F9315F3DBF51077BD0F72F3C9A5FA6C8606ADDBC0304D00F3F26C5485FAECAD4356AB6736C2G9746317FAA55E3B7B2EC2BF2EEAB9AF0F19CDB
	D12647EEE2583315F3DB13G174C313B9669311B9976EA655CD69A60B2B97644E2BD360EE16FD34EED95A71B0AAA1291FB7E92FDBB5948E27935CFFB5B44784BFD5A5BE69C17FC78F5582EFC5A6196467FC9B9E72569332AA97F0B8A9F7EFAB69FD86143E767129E7765EF207C319F7970EBD9FFD165435F487A64F5BE7CCD14DFD86D435FCC790F7A726BA87F4A75BE7CAD14FF32C64F2FE1795D38C153DF6C5A485B4338B67F6DF3FC1B38FD5E5C663FFD463785EE1F773679EF1F09ED015B675436015B672B3A
	3C2AFD6CB535DA3E5A4736ED547257F37E12CDDAFE8D67CF517155F545DBFD64DBECCE6BA9D70FACC9D14924ADAC7062B757EA613E81C8869A12CC4FBC53FD026B7BC85DBD6E36F6C2B61485FC7FD4A27B78A914CD85FCA6D0B9706B011A27E23B7AE0A6117D32415AE6F7B66E70BABC4C17F2871163A7AE9A2D4AC1AB1A7636B296B099F2F49A57C5ADDA3E550B7C10C50AAE7EE631D626F325EF1AAA774F052AEFCD9A5FA2A555CE13C6576B1DDA3E2AE74C923D8D2D4CF7965FB4D56E19FADD8C1A34B1981527
	7AC63EBE073EFD7249DCDD5CBC7450BFAFE3739EDDFA13EF7A78B37334691A0FBB50744A55055A7443DD56EEAEFBF23F4EDF76C187FAE63E2DDFFBE6BEE1AA2DBF29D371CC7D5BC7C56919BCFD48FED1FA71E3187E56DCD1FAA5CFBF52AECA2F6369D7E709529BF87A0DB922745647F01E70FCFB2136CD33BD9FBA1EECD15E79A75F2C139F437D721F945E90380F603CA7DBF08FBD1A6BDDE9775F3FFDE09777EF55F6719E58A67B40FBE01F5A7D75CB543F87F2B19FB3384FF3CD180F7C471167B6E1BECAB86F93
	9E839B671D45E360653CE7F88CC2B9EF2D93E513B82F470932291C770893E5E7F0DEAB3731FB9A727E48ED18B96FDD6EDF94679D6732F11CF78F170D673CED3908ABFE82F9D13908AB673C97D7A24F49F98FF4919E790E61C44E6BF3A1AE1173464CC5DC4513485B5C0A38DA4E4B4BC1DCA867DD1F0338884E739EC3DC6A1358970FDE454E86EFA8F80AE07E403F9F1049B1D3D66BB1AF72337E66279033EE019E7319B2D7A4E760DAB966F0299E63A8C50C4CB1FD8B7518DF73B37DC3B8E6E4199E934B4F72C7F0
	4C2D0B7418A90B50D68C47BC3DD80FF9E3B1E2A6BE05E777BF7196B0DEA27072276378373ED5F71605ECE7EC5D7A055BE15BE9BD51200C2674BC43B9DE5439FDBAFE8BEA3C22538B77E1C0B153719C21F7B51EA39CF775A8A942A67E96D43C657E3C7869F42D1B422E4AF700BE9D1BC5ED4EBC0DB6AB1E063E6CE942DBF7DA5B17117CBFB713BE7B2369D6DDBA699326C85A740BF569445F33F472F76952C90C5EE669B4EF772F423CE55E9FABB55C78105F3F9B9375B44DD3823BCE484EB4C55D32FF967B7E56F1
	BF5F5FD2C6C51A7F917DF806A473E97CAE4326E6BC03D57B8C72D6191167643C0D73D0B61A73FE5D0E38E91C77C49672CA1EC5DE9ABF3B52GBCDA275ADA30EE06BE0B75EEF2BFFEAFA07CD972F7FDC6BBC9048AD2887E75165DAC8E494F9A7739E6323249E3BA53GC3E88133AB2550B35D04287D3F6722FD8B24D1FBCFF42A764EB521BDDB0A585E7C816CF98D760E2B79657614CFE4B3FB7F60798B77E32FE8GFBB186FB5FBA51DE9C373732DB357732936DA57831172DA1DFFB499AFBD4377787D5F7B24FCB
	0A9F5D3983684EB06465AB9E3B1C67B0AF1739D5FBCFF2FBA54F096D4D9A405E1A67FC73F21BC755FDD3AE6A2E7523FB4EG3A77E8F4D3FDD2A56A338874E963C22863B93D3E7E678C75388B7B34A110C6EDDC60C29B11FEECE86D447A58C8B058C81C0BB692390D7D2D3C0C7D585046BC4347C641733EB62AF350C65973E8E3D28E5AD87B3C5806B6765B1F575B3098ECDCF90C0F3D4FE31F7310B24E4B6E9EBB6D0F237A345FB6A81B1DA87CE6B6A11979B404DDABB7E61A28CE69BD54C96F1B156F2A9D37BABD
	6A334F10D2A3AFFA21DE6E1EB27D7DCB0B0CB26FAC5172681E70AEEF20773BFC094A9D8CD26E3CDFACC73F37F14EA9B25E47EB4A0972AFD07813B5FCBA6738194FB912A12D7E85BC831BAA1331AF8A6EA782A5812581952442DCA595578EC976EAA94073DB0B2B3B819697CE4DF200664FF47A17660F5625BF6661589EF74BEF2508DFDD64777B8775296CB9B03B1A33B2156F92527C7D287C1D3A21109683B499A885A8832878C54CDF797135B4FF6415029D32ECED6FD03EDE49DFD0F0587BD4B6591820F97BCD
	3FA8EF15AF3267DBAF9A5B9D157B38D978AC0A61DB61AA7B48D2398F4CA239A18C9B8957D8215C56F9A2398406CD766B67C56DA239B40617DE02F559CBA2FB4FE4096456B0ECBDDC4DC2393F8A1F475898567B1251CF763C28C55F8FBDF8D45B8F250F38A58F5B022F7CD026B362A5F54ECB67DD2B793965491036DC22F316FCC97DC7473B716433252C4EBEDD00F536FC6CD5D2652397652D74E3B767AF541E65AF2A5D1C0DD36733BDD09366077AF20B89FD997A979C43171E16F87EFFEA5163BF603EA7F07C11
	9FD57C8FCEBDFED9A71F5BF2FCE8BF415374D9DCDFED9A260F79C9555766E33FC8BD734D71874E287AD279F78322F93AB2A5E1696BABB1FDDA1AA876695B284C70192A8F05AEEDDFCE755C4E4F3F173CBC101E4CACD54F8BF30DFA4E707CB48C28C779E4995373C50ED14FBF78DCBEF4C0BDBBF3D5BD930EE97550BE6DD5A5FE93C1C7724B5807E54B64EF609C31FEEFFCBB8A18E6372B5B6E16CFD12E725792D81BBB1F7CF9A876BBDC737BF6AF7E0D367F84D19B497FD19BB4AEFF157D6FFBE54862FD2F37E471
	3ED7CE4F406F469574787F5BA773FB34FF7B0416E3E19ED95FE6C099163CE2AA2A83EAGEAFD854B744A1EF7A5E5DC920CC91F5914AF12113F28A8237D50DE7FF9BD3FB7705FE18C6FA57B4EEDAED9F6F5927D489F59CB4ACBF6F5ABCC4A3B221744486DB851A1D3A8630F6EA5F553E9BF2E30699E510D7D382F557F8A6E9BA57E04FBC9C3B86FD0B36212B9EFFCB362D2B8EF6DFE5CF32AFD95F9BB4CA83B0773C619D15642F95BD622ECB4679D1C073293B86F06F9A81B40F95734A32EA49DF933ED082B603C83
	D9E8231B737E110532211C77EDB64A06F15EBD4DA8BB1D7372793E608C4EF370FDC1692FB8177BC069CA5873663ED57E7A8328BDDFDAFB2D875A3E37374B966B895E9EBF543DDFE1863D363F8E7C8EB07BFB193348DA438B58F02089C009C0A9C019C0452F190AAA016A004CC09DC07DC0E1C051C0F1C01300263D0675BE437B7EGFD995B90D3AA3F5B6AA0559F7BAC5DF74852C1DF4E5F86761F4A2D53653B8CE4568ECAEEE5276ECCB148D886A5770E4EDE8748748FCA2EC4A79786B2510312FBD9776E538410
	19BCA839CD3A337653C1E646206492F46F7D644FB095154F980CDC28AEFF55A033FBD0F2EF69660D0DA0638CA0C76B590CDC721B7767F4D8E4E9EFAA5FDCF7F4B9E4075569B82DFC621EFEAA1726AF48735F571667856EEBD765A95F4907BE52F22A1BBD4F7DA00FBCB7F59DBF6E3173BEFDDDDEB1676D7518DD9E8771044E935613A5915B9BF961A7DC6F0788720E73DFFFFBE061B3FC77F114BF8F45F45F570C3A23843A33A571FE579226AFEE06E1FFEDBF6EA5444F4079DFDF21EA6FE6B35ACB72E3CF348706
	76D28D76BAD6223D995C5E48F7D4FB774EC3FB792F0BE317B7C06C4ADF770D5D2DC52A6E95B6543DC6201B68936D2FE1DE6ADF774D0BB78B733267F54C4B74E2555E8F59E82F590FBD511E9B5A6BB658CBEFC6FB7D5C5E3AF92A3D903E479766A7F6058344AEDAE30F0EEBBB1539F6AC63D35E978AEF926331F3EB337C373F6ED9015B5F29D962394E19D94639CE68AC71DC673CD95A398E65DD28BCD3B881E3D4A34705F3DE93670DD0F4925EB78E1B5C4179D7A8FE917E9EBBB14867D50ADFFD8A2F1171FE2B61
	B5B15E6FE611BE84325C61F223EC5CAC32B0B96EF0BA697D8485636802AE054763D645C7C737D563779D1C57444B62911331FC9EB7E94B0772BE971463D72DC65C56CEA3EEDB279137CA206F83013E8F8538EA81AEC7100FD902FC6C2BB4628ED59AF10F3B0C3869AEA36EB201FFD1827F96649AF10BF335B83A26AC7B995714A93CFD9EA24BCA167EE6B326177F9D53FB485006FB817B0D6B54174CA8E361B257142AB2E7D69AE57E491FF18D65B213972AFE9C3271BD891E7EEA192A7342AC234ED159FCDF024B
	4CDB2C4A64FDE714391E6FAD543E01B227C9F5E6E306D57956BC9B571526C779655EC2150FCD0F4DAF167E2C7BA68B688DF9E3B0739269BB2DDCB848448CCA6ED55DBE5FC410C99C145C5B3AFD3E94104998145CB7B62DDCC1862CE5B286A3B7A6DBABD78BB2FB86A577FB5DFC3499E43A8348517ADF7B3331CDEC795958F696FEE74415FFE744198E9AF1F38E9AF125730D38A5730D38AE1B91673199F137E59BF1F7E69BF18F887A0007F4FD40C2259DE42E7CAECB12463DAC654A52BB64CF60D434636BD632D4
	C87E06CD056C2A68A22FD14930E0F5DB7B70A6073C8AA6CBF96C6DAD834363160A78490A3D4A00B13DF743566CE72F4A4E12F276CAA6BA006E15F248A838D74AD586493DD2B68C20DE6509A97CA33D8D9954A44F8F524FCA9518AD640A31484D798FF0F304560262C2209BAB2CDE07274253EEF55A37C2A8BB9DDD4A456A05CBE153C029A665553B2DDCB52AD5C84765B4886E7F238609FF3F7C0CF6EFBD791C6B7DBE630BFC13366958B755BDEE3ADFB0D9F957A78D7879197C994CEA72773B7D6DD79C6E34BA1D
	3255F9F2C7170D6CDD6CBCA55B55FD37BF2FA67ABD08E1096C6F592EA6FEFF2F1179BFD0CB8788E949451BC1A6GG0809GGD0CB818294G94G88G88G58FC71B4E949451BC1A6GG0809GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGFBA6GGGG
**end of data**/
}
}