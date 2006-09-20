package cbit.image.gui;
import javax.swing.*;

import cbit.util.Range;
import cbit.vcell.simdata.DisplayAdapterService;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (10/11/00 4:21:48 PM)
 * @author: 
 */
public class DisplayAdapterServicePanel extends JPanel implements java.awt.event.ActionListener {
	//
	public static final String HLE_PROPERTY_ID = "DisplayAdapterServicePanel_HLE";
	//
	private ImageIcon cmapImageIcon = null;
	private JLabel ivjMaxLabel = null;
	private JLabel ivjMinLabel = null;
	private JCheckBox ivjAutoScaleCheckbox = null;
	private JPanel ivjColorGridPanel = null;
	private JTextField ivjMaxTextField = null;
	private JTextField ivjMinTextField = null;
	private DisplayAdapterService ivjDisplayAdapterService = null;
	private JLabel ivjMaxRangeJLabel = null;
	private JLabel ivjMinRangeJLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JPanel ivjScalePanel = null;
	private cbit.util.Range ivjactiveScaleRange1 = null;
	private cbit.util.Range ivjvalueDomain1 = null;
	private JPanel ivjJPanel1 = null;
	private JLabel ivjColorMapJLabel = null;
	private JLabel ivjSCAboveMaxJLabel = null;
	private JLabel ivjSCBelowMinJLabel = null;
	private JLabel ivjSCIllegalJLabel = null;
	private JLabel ivjSCNANJLabel = null;
	private JLabel ivjSCNoRangeJLabel = null;
	private JPanel ivjSpecialColorsJPanel = null;
	private cbit.gui.ButtonGroupCivilized ivjColorMapButtonGroup = null;
	private ButtonModel ivjButtonModel1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DisplayAdapterServicePanel.this.getAutoScaleCheckbox()) 
				connEtoM11(e);
			if (e.getSource() == DisplayAdapterServicePanel.this.getAutoScaleCheckbox()) 
				connEtoC12(e);
			if (e.getSource() == DisplayAdapterServicePanel.this.getMaxTextField()) 
				connEtoC9(e);
			if (e.getSource() == DisplayAdapterServicePanel.this.getMinTextField()) 
				connEtoC16(e);
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == DisplayAdapterServicePanel.this.getMaxTextField()) 
				connEtoC15(e);
			if (e.getSource() == DisplayAdapterServicePanel.this.getMinTextField()) 
				connEtoC17(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == DisplayAdapterServicePanel.this.getAutoScaleCheckbox()) 
				connEtoM3(e);
			if (e.getSource() == DisplayAdapterServicePanel.this.getAutoScaleCheckbox()) 
				connEtoM7(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals("colorModelIDs"))) 
				connEtoC1(evt);
			if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals("autoScale"))) 
				connEtoM17(evt);
			if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals("activeColorModelID"))) 
				connEtoC7(evt);
			if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals("activeColorModelID"))) 
				connEtoM10(evt);
			if (evt.getSource() == DisplayAdapterServicePanel.this.getColorMapButtonGroup() && (evt.getPropertyName().equals("selection"))) 
				connEtoM12(evt);
			if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals("valueDomain"))) 
				connEtoM20(evt);
			if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals("activeScaleRange"))) 
				connEtoM22(evt);
			if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals("specialColors"))) 
				connEtoC5(evt);
		};
	};

/**
 * DisplayAdapterServicePanel constructor comment.
 */
public DisplayAdapterServicePanel() {
	super();
	initialize();
}

	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {

	//This is here to get the dynamically created colormap selection button events
	if(e.getSource() instanceof JRadioButton){
		createHighLevelUserEvent(e);
	}
}


/**
 * Comment
 */
private void calculateCustomScaleRange() {

	try{
		double min = Double.parseDouble(getMinTextField().getText());
		double max = Double.parseDouble(getMaxTextField().getText());
		getDisplayAdapterService().setCustomScaleRange(new Range(min,max));
		firePropertyChange(HLE_PROPERTY_ID,true,false);
	}catch (NumberFormatException e){
		ensureMinMaxDisplay();
		//if(getactiveScaleRange1() != null){
			//getMinTextField().setText(getactiveScaleRange1().getMin()+"");
			//getMaxTextField().setText(getactiveScaleRange1().getMax()+"");
		//}
	}
}


/**
 * connEtoC1:  (DisplayAdapterService.activeColorModelID --> DisplayAdapterServicePanel.updateColorModelradioButtons()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateColorModelRadioButtons();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC12:  (AutoScaleCheckbox.action.actionPerformed(java.awt.event.ActionEvent) --> DisplayAdapterServicePanel.fireHighLevelUserEvent(Ljava.awt.AWTEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.createHighLevelUserEvent(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC13:  (activeScaleRange1.this --> DisplayAdapterServicePanel.ensureMinMaxDisplay()V)
 * @param value cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(cbit.util.Range value) {
	try {
		// user code begin {1}
		// user code end
		this.ensureMinMaxDisplay();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC15:  (MaxTextField.focus.focusLost(java.awt.event.FocusEvent) --> DisplayAdapterServicePanel.calculateCustomScaleRange()V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.calculateCustomScaleRange();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC16:  (MinTextField.action.actionPerformed(java.awt.event.ActionEvent) --> DisplayAdapterServicePanel.calculateCustomScaleRange()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.calculateCustomScaleRange();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC17:  (MinTextField.focus.focusLost(java.awt.event.FocusEvent) --> DisplayAdapterServicePanel.calculateCustomScaleRange()V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.calculateCustomScaleRange();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (valueDomain1.this --> DisplayAdapterServicePanel.ensureDomainDisplay()V)
 * @param value cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(cbit.util.Range value) {
	try {
		// user code begin {1}
		// user code end
		this.ensureDomainDisplay();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (DisplayAdapterServicePanel.initialize() --> DisplayAdapterServicePanel.displayAdapterServicePanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4() {
	try {
		// user code begin {1}
		// user code end
		this.displayAdapterServicePanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (DisplayAdapterService.specialColors --> DisplayAdapterServicePanel.specialColorsChanged()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.specialColorsChanged();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (activeScaleRange1.this --> DisplayAdapterServicePanel.updateColorMapDisplay()V)
 * @param value cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(cbit.util.Range value) {
	try {
		// user code begin {1}
		// user code end
		this.updateColorMapDisplay();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC7:  (DisplayAdapterService.activeColorModel --> DisplayAdapterServicePanel.updateColorMapDisplay()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateColorMapDisplay();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (valueDomain1.this --> DisplayAdapterServicePanel.updateColorMapDisplay()V)
 * @param value cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(cbit.util.Range value) {
	try {
		// user code begin {1}
		// user code end
		this.updateColorMapDisplay();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC9:  (MaxTextField.action.actionPerformed(java.awt.event.ActionEvent) --> DisplayAdapterServicePanel.calculateCustomScaleRange()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.calculateCustomScaleRange();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM10:  (DisplayAdapterService.activeColorModelID --> ColorMapButtonGroup.setSelection(Ljava.lang.String;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getColorMapButtonGroup().setSelection(String.valueOf(getDisplayAdapterService().getActiveColorModelID()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM11:  (AutoScaleCheckbox.action.actionPerformed(java.awt.event.ActionEvent) --> DisplayAdapterService.autoScale)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getDisplayAdapterService().setAutoScale(getAutoScaleCheckbox().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM12:  (ColorMapButtonGroup.selection --> ButtonModel1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setButtonModel1(getColorMapButtonGroup().getSelection());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM13:  (ButtonModel1.this --> DisplayAdapterService.activeColorModelID)
 * @param value javax.swing.ButtonModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM13(javax.swing.ButtonModel value) {
	try {
		// user code begin {1}
		// user code end
		if ((getButtonModel1() != null)) {
			getDisplayAdapterService().setActiveColorModelID(getButtonModel1().getActionCommand());
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
 * connEtoM17:  (DisplayAdapterService.autoScale --> AutoScaleCheckbox.selected)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM17(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getAutoScaleCheckbox().setSelected(getDisplayAdapterService().getAutoScale());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM20:  (DisplayAdapterService.valueDomain --> valueDomain1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM20(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setvalueDomain1(getDisplayAdapterService().getValueDomain());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM22:  (DisplayAdapterService.activeScaleRange --> activeScaleRange1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM22(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setactiveScaleRange1(getDisplayAdapterService().getActiveScaleRange());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (AutoScaleCheckbox.item.itemStateChanged(java.awt.event.ItemEvent) --> MinTextField.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getMinTextField().setEnabled(this.notIsAutosclaeSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM7:  (AutoScaleCheckbox.item.itemStateChanged(java.awt.event.ItemEvent) --> MaxTextField.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getMaxTextField().setEnabled(this.notIsAutosclaeSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/6/2003 2:16:28 PM)
 * @param event java.awt.AWTEvent
 */
private void createHighLevelUserEvent(java.awt.AWTEvent event) {

	firePropertyChange(HLE_PROPERTY_ID,true,false);
}


/**
 * Comment
 */
private void displayAdapterServicePanel_Initialize() {
	getDisplayAdapterService().setAutoScale(getAutoScaleCheckbox().isSelected());
}


/**
 * Comment
 */
private void ensureDomainDisplay() {
	if(getvalueDomain1() != null){
		getMinRangeJLabel().setText(cbit.util.NumberUtils.formatNumber(getvalueDomain1().getMin()));
		getMaxRangeJLabel().setText(cbit.util.NumberUtils.formatNumber(getvalueDomain1().getMax()));
	}else{
		getMinRangeJLabel().setText("---");
		getMaxRangeJLabel().setText("---");
	}
}


/**
 * Comment
 */
private void ensureMinMaxDisplay() {
	if(getactiveScaleRange1() != null){
		getMinTextField().setText(cbit.util.NumberUtils.formatNumber(getactiveScaleRange1().getMin()));
		getMaxTextField().setText(cbit.util.NumberUtils.formatNumber(getactiveScaleRange1().getMax()));
	}else{
		getMinTextField().setText("0");
		getMaxTextField().setText("0");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 5:03:23 PM)
 * @return java.lang.String
 * @param number double
 */
private static final String formatNumber(double number) {
	return cbit.util.NumberUtils.formatNumber(number);
}


/**
 * Return the activeScaleRange1 property value.
 * @return cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.util.Range getactiveScaleRange1() {
	// user code begin {1}
	// user code end
	return ivjactiveScaleRange1;
}

/**
 * Return the AutoScaleCheckbox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getAutoScaleCheckbox() {
	if (ivjAutoScaleCheckbox == null) {
		try {
			ivjAutoScaleCheckbox = new javax.swing.JCheckBox();
			ivjAutoScaleCheckbox.setName("AutoScaleCheckbox");
			ivjAutoScaleCheckbox.setSelected(false);
			ivjAutoScaleCheckbox.setText("Auto Range");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAutoScaleCheckbox;
}

/**
 * Return the ButtonModel1 property value.
 * @return javax.swing.ButtonModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonModel getButtonModel1() {
	// user code begin {1}
	// user code end
	return ivjButtonModel1;
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getColorGridPanel() {
	if (ivjColorGridPanel == null) {
		try {
			cbit.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.TitledBorderBean();
			ivjLocalBorder.setBorder(new javax.swing.border.EtchedBorder());
			ivjLocalBorder.setTitleJustification(javax.swing.border.TitledBorder.LEFT);
			ivjLocalBorder.setTitle("Color");
			ivjColorGridPanel = new javax.swing.JPanel();
			ivjColorGridPanel.setName("ColorGridPanel");
			ivjColorGridPanel.setBorder(ivjLocalBorder);
			ivjColorGridPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSpecialColorsJPanel = new java.awt.GridBagConstraints();
			constraintsSpecialColorsJPanel.gridx = 0; constraintsSpecialColorsJPanel.gridy = 0;
			constraintsSpecialColorsJPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSpecialColorsJPanel.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsSpecialColorsJPanel.weightx = 1.0;
			constraintsSpecialColorsJPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getColorGridPanel().add(getSpecialColorsJPanel(), constraintsSpecialColorsJPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorGridPanel;
}

/**
 * Return the ColorMapButtonGroup property value.
 * @return cbit.gui.ButtonGroupCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.ButtonGroupCivilized getColorMapButtonGroup() {
	if (ivjColorMapButtonGroup == null) {
		try {
			ivjColorMapButtonGroup = new cbit.gui.ButtonGroupCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorMapButtonGroup;
}


/**
 * Return the ColorMapJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getColorMapJLabel() {
	if (ivjColorMapJLabel == null) {
		try {
			cbit.gui.LineBorderBean ivjLocalBorder2;
			ivjLocalBorder2 = new cbit.gui.LineBorderBean();
			ivjLocalBorder2.setThickness(2);
			ivjColorMapJLabel = new javax.swing.JLabel();
			ivjColorMapJLabel.setName("ColorMapJLabel");
			ivjColorMapJLabel.setBorder(ivjLocalBorder2);
			ivjColorMapJLabel.setText("cm");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorMapJLabel;
}

/**
 * Return the DisplayAdapterService property value.
 * @return cbit.image.DisplayAdapterService
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DisplayAdapterService getDisplayAdapterService() {
	if (ivjDisplayAdapterService == null) {
		try {
			ivjDisplayAdapterService = new cbit.vcell.simdata.DisplayAdapterService();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayAdapterService;
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
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsAutoScaleCheckbox = new java.awt.GridBagConstraints();
			constraintsAutoScaleCheckbox.gridx = 0; constraintsAutoScaleCheckbox.gridy = 0;
			constraintsAutoScaleCheckbox.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getAutoScaleCheckbox(), constraintsAutoScaleCheckbox);
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
 * Return the MaxLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMaxLabel() {
	if (ivjMaxLabel == null) {
		try {
			ivjMaxLabel = new javax.swing.JLabel();
			ivjMaxLabel.setName("MaxLabel");
			ivjMaxLabel.setText("Max:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaxLabel;
}


/**
 * Return the MaxJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMaxRangeJLabel() {
	if (ivjMaxRangeJLabel == null) {
		try {
			ivjMaxRangeJLabel = new javax.swing.JLabel();
			ivjMaxRangeJLabel.setName("MaxRangeJLabel");
			ivjMaxRangeJLabel.setText("(Max)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaxRangeJLabel;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMaxTextField() {
	if (ivjMaxTextField == null) {
		try {
			ivjMaxTextField = new javax.swing.JTextField();
			ivjMaxTextField.setName("MaxTextField");
			ivjMaxTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaxTextField;
}

/**
 * Return the MinLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMinLabel() {
	if (ivjMinLabel == null) {
		try {
			ivjMinLabel = new javax.swing.JLabel();
			ivjMinLabel.setName("MinLabel");
			ivjMinLabel.setText("Min:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinLabel;
}


/**
 * Return the MinJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMinRangeJLabel() {
	if (ivjMinRangeJLabel == null) {
		try {
			ivjMinRangeJLabel = new javax.swing.JLabel();
			ivjMinRangeJLabel.setName("MinRangeJLabel");
			ivjMinRangeJLabel.setText("(Min)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinRangeJLabel;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMinTextField() {
	if (ivjMinTextField == null) {
		try {
			ivjMinTextField = new javax.swing.JTextField();
			ivjMinTextField.setName("MinTextField");
			ivjMinTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinTextField;
}

/**
 * Return the SCAboveMaxJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSCAboveMaxJLabel() {
	if (ivjSCAboveMaxJLabel == null) {
		try {
			ivjSCAboveMaxJLabel = new javax.swing.JLabel();
			ivjSCAboveMaxJLabel.setName("SCAboveMaxJLabel");
			ivjSCAboveMaxJLabel.setToolTipText("Above Max Color");
			ivjSCAboveMaxJLabel.setOpaque(true);
			ivjSCAboveMaxJLabel.setText("AM");
			ivjSCAboveMaxJLabel.setBackground(java.awt.Color.black);
			ivjSCAboveMaxJLabel.setForeground(java.awt.Color.lightGray);
			ivjSCAboveMaxJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjSCAboveMaxJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSCAboveMaxJLabel;
}

/**
 * Return the ScalePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getScalePanel() {
	if (ivjScalePanel == null) {
		try {
			cbit.gui.TitledBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new cbit.gui.TitledBorderBean();
			ivjLocalBorder1.setBorder(new javax.swing.border.EtchedBorder());
			ivjLocalBorder1.setTitleJustification(javax.swing.border.TitledBorder.LEFT);
			ivjLocalBorder1.setTitle("Scale");
			ivjScalePanel = new javax.swing.JPanel();
			ivjScalePanel.setName("ScalePanel");
			ivjScalePanel.setBorder(ivjLocalBorder1);
			ivjScalePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsMinRangeJLabel = new java.awt.GridBagConstraints();
			constraintsMinRangeJLabel.gridx = 1; constraintsMinRangeJLabel.gridy = 3;
			constraintsMinRangeJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getScalePanel().add(getMinRangeJLabel(), constraintsMinRangeJLabel);

			java.awt.GridBagConstraints constraintsMinLabel = new java.awt.GridBagConstraints();
			constraintsMinLabel.gridx = 0; constraintsMinLabel.gridy = 4;
			constraintsMinLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMinLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getScalePanel().add(getMinLabel(), constraintsMinLabel);

			java.awt.GridBagConstraints constraintsMinTextField = new java.awt.GridBagConstraints();
			constraintsMinTextField.gridx = 1; constraintsMinTextField.gridy = 4;
			constraintsMinTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMinTextField.weightx = 1.0;
			constraintsMinTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getScalePanel().add(getMinTextField(), constraintsMinTextField);

			java.awt.GridBagConstraints constraintsMaxRangeJLabel = new java.awt.GridBagConstraints();
			constraintsMaxRangeJLabel.gridx = 1; constraintsMaxRangeJLabel.gridy = 1;
			constraintsMaxRangeJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getScalePanel().add(getMaxRangeJLabel(), constraintsMaxRangeJLabel);

			java.awt.GridBagConstraints constraintsMaxLabel = new java.awt.GridBagConstraints();
			constraintsMaxLabel.gridx = 0; constraintsMaxLabel.gridy = 2;
			constraintsMaxLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMaxLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getScalePanel().add(getMaxLabel(), constraintsMaxLabel);

			java.awt.GridBagConstraints constraintsMaxTextField = new java.awt.GridBagConstraints();
			constraintsMaxTextField.gridx = 1; constraintsMaxTextField.gridy = 2;
			constraintsMaxTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMaxTextField.weightx = 1.0;
			constraintsMaxTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getScalePanel().add(getMaxTextField(), constraintsMaxTextField);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
			constraintsJPanel1.gridwidth = 2;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getScalePanel().add(getJPanel1(), constraintsJPanel1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScalePanel;
}

/**
 * Return the SCBelowMinJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSCBelowMinJLabel() {
	if (ivjSCBelowMinJLabel == null) {
		try {
			ivjSCBelowMinJLabel = new javax.swing.JLabel();
			ivjSCBelowMinJLabel.setName("SCBelowMinJLabel");
			ivjSCBelowMinJLabel.setToolTipText("Below Min Color");
			ivjSCBelowMinJLabel.setOpaque(true);
			ivjSCBelowMinJLabel.setText("BM");
			ivjSCBelowMinJLabel.setBackground(java.awt.Color.black);
			ivjSCBelowMinJLabel.setForeground(java.awt.Color.lightGray);
			ivjSCBelowMinJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjSCBelowMinJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSCBelowMinJLabel;
}

/**
 * Return the SCIllegalJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSCIllegalJLabel() {
	if (ivjSCIllegalJLabel == null) {
		try {
			ivjSCIllegalJLabel = new javax.swing.JLabel();
			ivjSCIllegalJLabel.setName("SCIllegalJLabel");
			ivjSCIllegalJLabel.setToolTipText("Illegal Value Color");
			ivjSCIllegalJLabel.setOpaque(true);
			ivjSCIllegalJLabel.setText("IL");
			ivjSCIllegalJLabel.setBackground(java.awt.Color.black);
			ivjSCIllegalJLabel.setForeground(java.awt.Color.lightGray);
			ivjSCIllegalJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjSCIllegalJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSCIllegalJLabel;
}

/**
 * Return the SCNANJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSCNANJLabel() {
	if (ivjSCNANJLabel == null) {
		try {
			ivjSCNANJLabel = new javax.swing.JLabel();
			ivjSCNANJLabel.setName("SCNANJLabel");
			ivjSCNANJLabel.setToolTipText("Not A Number Color");
			ivjSCNANJLabel.setOpaque(true);
			ivjSCNANJLabel.setText("NN");
			ivjSCNANJLabel.setBackground(java.awt.Color.black);
			ivjSCNANJLabel.setForeground(java.awt.Color.lightGray);
			ivjSCNANJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjSCNANJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSCNANJLabel;
}

/**
 * Return the SCNoRangeJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSCNoRangeJLabel() {
	if (ivjSCNoRangeJLabel == null) {
		try {
			ivjSCNoRangeJLabel = new javax.swing.JLabel();
			ivjSCNoRangeJLabel.setName("SCNoRangeJLabel");
			ivjSCNoRangeJLabel.setToolTipText("No Range Color");
			ivjSCNoRangeJLabel.setOpaque(true);
			ivjSCNoRangeJLabel.setText("NR");
			ivjSCNoRangeJLabel.setBackground(java.awt.Color.black);
			ivjSCNoRangeJLabel.setForeground(java.awt.Color.lightGray);
			ivjSCNoRangeJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjSCNoRangeJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSCNoRangeJLabel;
}

/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getSpecialColorsJPanel() {
	if (ivjSpecialColorsJPanel == null) {
		try {
			ivjSpecialColorsJPanel = new javax.swing.JPanel();
			ivjSpecialColorsJPanel.setName("SpecialColorsJPanel");
			ivjSpecialColorsJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSCBelowMinJLabel = new java.awt.GridBagConstraints();
			constraintsSCBelowMinJLabel.gridx = 0; constraintsSCBelowMinJLabel.gridy = 0;
			constraintsSCBelowMinJLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSCBelowMinJLabel.weightx = 1.0;
			constraintsSCBelowMinJLabel.insets = new java.awt.Insets(4, 0, 4, 0);
			getSpecialColorsJPanel().add(getSCBelowMinJLabel(), constraintsSCBelowMinJLabel);

			java.awt.GridBagConstraints constraintsSCAboveMaxJLabel = new java.awt.GridBagConstraints();
			constraintsSCAboveMaxJLabel.gridx = 1; constraintsSCAboveMaxJLabel.gridy = 0;
			constraintsSCAboveMaxJLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSCAboveMaxJLabel.weightx = 1.0;
			constraintsSCAboveMaxJLabel.insets = new java.awt.Insets(4, 0, 4, 0);
			getSpecialColorsJPanel().add(getSCAboveMaxJLabel(), constraintsSCAboveMaxJLabel);

			java.awt.GridBagConstraints constraintsSCNANJLabel = new java.awt.GridBagConstraints();
			constraintsSCNANJLabel.gridx = 2; constraintsSCNANJLabel.gridy = 0;
			constraintsSCNANJLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSCNANJLabel.weightx = 1.0;
			constraintsSCNANJLabel.insets = new java.awt.Insets(4, 0, 4, 0);
			getSpecialColorsJPanel().add(getSCNANJLabel(), constraintsSCNANJLabel);

			java.awt.GridBagConstraints constraintsSCIllegalJLabel = new java.awt.GridBagConstraints();
			constraintsSCIllegalJLabel.gridx = 3; constraintsSCIllegalJLabel.gridy = 0;
			constraintsSCIllegalJLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSCIllegalJLabel.weightx = 1.0;
			constraintsSCIllegalJLabel.insets = new java.awt.Insets(4, 0, 4, 0);
			getSpecialColorsJPanel().add(getSCIllegalJLabel(), constraintsSCIllegalJLabel);

			java.awt.GridBagConstraints constraintsSCNoRangeJLabel = new java.awt.GridBagConstraints();
			constraintsSCNoRangeJLabel.gridx = 4; constraintsSCNoRangeJLabel.gridy = 0;
			constraintsSCNoRangeJLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSCNoRangeJLabel.weightx = 1.0;
			constraintsSCNoRangeJLabel.insets = new java.awt.Insets(4, 0, 4, 0);
			getSpecialColorsJPanel().add(getSCNoRangeJLabel(), constraintsSCNoRangeJLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSpecialColorsJPanel;
}

/**
 * Return the dataRange1 property value.
 * @return cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.util.Range getvalueDomain1() {
	// user code begin {1}
	// user code end
	return ivjvalueDomain1;
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
	getDisplayAdapterService().addPropertyChangeListener(ivjEventHandler);
	getAutoScaleCheckbox().addActionListener(ivjEventHandler);
	getColorMapButtonGroup().addPropertyChangeListener(ivjEventHandler);
	getAutoScaleCheckbox().addItemListener(ivjEventHandler);
	getMaxTextField().addActionListener(ivjEventHandler);
	getMaxTextField().addFocusListener(ivjEventHandler);
	getMinTextField().addActionListener(ivjEventHandler);
	getMinTextField().addFocusListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DisplayAdapterServicePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(218, 237);

		java.awt.GridBagConstraints constraintsColorGridPanel = new java.awt.GridBagConstraints();
		constraintsColorGridPanel.gridx = 1; constraintsColorGridPanel.gridy = 1;
		constraintsColorGridPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsColorGridPanel.weightx = 1.0;
		constraintsColorGridPanel.weighty = 1.0;
		constraintsColorGridPanel.insets = new java.awt.Insets(0, 0, 4, 4);
		add(getColorGridPanel(), constraintsColorGridPanel);

		java.awt.GridBagConstraints constraintsScalePanel = new java.awt.GridBagConstraints();
		constraintsScalePanel.gridx = 1; constraintsScalePanel.gridy = 0;
		constraintsScalePanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsScalePanel.weightx = 1.0;
		constraintsScalePanel.insets = new java.awt.Insets(4, 0, 4, 4);
		add(getScalePanel(), constraintsScalePanel);

		java.awt.GridBagConstraints constraintsColorMapJLabel = new java.awt.GridBagConstraints();
		constraintsColorMapJLabel.gridx = 0; constraintsColorMapJLabel.gridy = 0;
constraintsColorMapJLabel.gridheight = 2;
		constraintsColorMapJLabel.insets = new java.awt.Insets(10, 4, 6, 0);
		add(getColorMapJLabel(), constraintsColorMapJLabel);
		initConnections();
		connEtoC4();
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DisplayAdapterServicePanel aDisplayAdapterServicePanel;
		aDisplayAdapterServicePanel = new DisplayAdapterServicePanel();
		frame.setContentPane(aDisplayAdapterServicePanel);
		frame.setSize(aDisplayAdapterServicePanel.getSize());
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
private boolean notIsAutosclaeSelected() {
	return !getAutoScaleCheckbox().isSelected();
}


/**
 * Set the activeScaleRange1 to a new value.
 * @param newValue cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setactiveScaleRange1(cbit.util.Range newValue) {
	if (ivjactiveScaleRange1 != newValue) {
		try {
			ivjactiveScaleRange1 = newValue;
			connEtoC13(ivjactiveScaleRange1);
			connEtoC6(ivjactiveScaleRange1);
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
 * Set the ButtonModel1 to a new value.
 * @param newValue javax.swing.ButtonModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setButtonModel1(javax.swing.ButtonModel newValue) {
	if (ivjButtonModel1 != newValue) {
		try {
			ivjButtonModel1 = newValue;
			connEtoM13(ivjButtonModel1);
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
 * Set the dataRange1 to a new value.
 * @param newValue cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setvalueDomain1(cbit.util.Range newValue) {
	if (ivjvalueDomain1 != newValue) {
		try {
			ivjvalueDomain1 = newValue;
			connEtoC2(ivjvalueDomain1);
			connEtoC8(ivjvalueDomain1);
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
private void specialColorsChanged() {
	updateColorMapDisplay();
	createHighLevelUserEvent(null);
}


/**
 * Comment
 */
private void updateColorMapDisplay() {

	//if(getColorMapJLabel().getSize().height == 0){
		//return;
	//}
	////Create ImageIcon if necessary.  Keep reusing old if possible, just change BufferdImageData
	//java.awt.Insets myInsets = getColorMapJLabel().getInsets();
	//int cmapLength = getColorMapJLabel().getSize().height-myInsets.top-myInsets.bottom;
	//if(cmapLength < 2){
		//return;
	//}
	int cmapLength = 256-(getDisplayAdapterService().getSpecialColors() != null?getDisplayAdapterService().getSpecialColors().length:0);//256;
	int cmapWidth = 12;
	if(cmapImageIcon == null /*|| cmapImageIcon.getIconHeight() != cmapLength*/){
		java.awt.image.BufferedImage cmapImage = new java.awt.image.BufferedImage(cmapWidth,cmapLength,java.awt.image.BufferedImage.TYPE_INT_ARGB);
		cmapImageIcon = new ImageIcon(cmapImage);
		getColorMapJLabel().setIcon(cmapImageIcon);
		getColorMapJLabel().setText(null);
	}
	//Set ColorMap
	if(getvalueDomain1() != null && getDisplayAdapterService() != null){
		java.awt.image.BufferedImage bufferedCmap = (java.awt.image.BufferedImage )cmapImageIcon.getImage();
		int[] row = new int[cmapWidth];
		double value = getvalueDomain1().getMin();
		double inc = (getvalueDomain1().getMax() - getvalueDomain1().getMin())/(double)(cmapLength-1);
		for(int i=0;i<cmapLength;i+=1){
			int colorFromValue = getDisplayAdapterService().getColorFromValue(value);
			if(i == (cmapLength-1)){
				colorFromValue = getDisplayAdapterService().getColorFromValue(getvalueDomain1().getMax());
			}
			java.util.Arrays.fill(row,colorFromValue);
//if(i == 0){java.util.Arrays.fill(row,0xFFFF0000);}
//else if(i == (cmapLength-1)){java.util.Arrays.fill(row,0xFF00FF00);}
//System.out.println(cbit.util.BeanUtils.forceStringSize(cbit.util.NumberUtils.formatNumber(value),15," ",false)+" "+Integer.toHexString(row[0]));
			//bufferedCmap.setRGB(0,i,cmapWidth,1,row,0,cmapWidth);
			bufferedCmap.setRGB(0,cmapLength-1-i,cmapWidth,1,row,0,cmapWidth);
			value+= inc;
		}
	}
	//Set Special Colors
	int[] specialColors = getDisplayAdapterService().getSpecialColors();
	if(specialColors != null){
		for(int i=0;i < specialColors.length;i+= 1){
			java.awt.Color specialColor = new java.awt.Color(specialColors[i]);
			java.awt.Color contrast = java.awt.Color.white;
			//Calculate contrast color for foreground text
			if(specialColor.getRed() == specialColor.getGreen() &&
				specialColor.getRed() == specialColor.getBlue() &&
				specialColor.getBlue() == specialColor.getGreen()){
				contrast = (specialColor.getRed() <= 128?java.awt.Color.white:java.awt.Color.black);
			}else{
				java.awt.color.ICC_Profile iccGrayProfile = java.awt.color.ICC_Profile.getInstance(java.awt.color.ColorSpace.CS_GRAY);
				java.awt.color.ICC_ColorSpace iccGrayColorSpace = new java.awt.color.ICC_ColorSpace(iccGrayProfile);
				float[] fColor = new float[3];
				fColor[0] = (float)specialColor.getRed()/255;
				fColor[1] =(float)specialColor.getGreen()/255;
				fColor[2] =(float)specialColor.getBlue()/255;
				float[] fGray = iccGrayColorSpace.fromRGB(fColor);
				contrast = (fGray[0] < .5f ? java.awt.Color.white:java.awt.Color.black);
			}
			//
			switch(i){
				case DisplayAdapterService.BELOW_MIN_COLOR_OFFSET:
					getSCBelowMinJLabel().setBackground(specialColor);
					getSCBelowMinJLabel().setForeground(contrast);
					break;
				case DisplayAdapterService.ABOVE_MAX_COLOR_OFFSET:
					getSCAboveMaxJLabel().setBackground(specialColor);
					getSCAboveMaxJLabel().setForeground(contrast);
					break;
				case DisplayAdapterService.NAN_COLOR_OFFSET:
					getSCNANJLabel().setBackground(specialColor);
					getSCNANJLabel().setForeground(contrast);
					break;
				case DisplayAdapterService.ILLEGAL_COLOR_OFFSET:
					getSCIllegalJLabel().setBackground(specialColor);
					getSCIllegalJLabel().setForeground(contrast);
					break;
				case DisplayAdapterService.NO_RANGE_COLOR_OFFSET:
					getSCNoRangeJLabel().setBackground(specialColor);
					getSCNoRangeJLabel().setForeground(contrast);
					break;
			}
		}
	}
	//
	getColorMapJLabel().repaint();
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 6:15:25 PM)
 */
private void updateColorModelRadioButtons() {
	String currentSelection = getDisplayAdapterService().getActiveColorModelID();
	JRadioButton toBeSelectedButton = null;
	// remove all from panel and buttongroup
	java.awt.Component[] components = getColorGridPanel().getComponents();
	if (components != null) {
		for (int i = 0; i < components.length; i += 1) {
			if(components[i].getName() == null || !components[i].getName().equals("SpecialColorsJPanel")){
				((java.awt.Container) getColorGridPanel()).remove(components[i]);
			}
		}
	}
	java.util.Enumeration buttons = getColorMapButtonGroup().getElements();
	while (buttons.hasMoreElements()) {
		AbstractButton button = (AbstractButton)buttons.nextElement();
		getColorMapButtonGroup().remove(button);
	}
	// now add new ones
	String[] colorNames = getDisplayAdapterService().getColorModelIDs();
	if (colorNames != null) {
		for (int i = 0; i < colorNames.length; i += 1) {
			JRadioButton jcb = new JRadioButton(colorNames[i]);
			jcb.addActionListener(this);
			if (colorNames[i].equals(currentSelection)) {
				toBeSelectedButton = jcb;
			}
			jcb.setActionCommand(colorNames[i]);
			java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints();
				//constraints.gridy = i;
				constraints.gridy = i+1;
				constraints.insets = new java.awt.Insets(0, 5, 0, 0);
				constraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
			getColorGridPanel().add(jcb, constraints);
			getColorMapButtonGroup().add(jcb);
		}
		if (toBeSelectedButton != null) {
			toBeSelectedButton.setSelected(true);
		}
	}
	getColorGridPanel().revalidate();
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G430171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E165FDFFDCD4D55AB895AA999615ED585AC6C6C6C6ADA5962536D41618E4D4D6ECD131E5E53BE4E46E9B6D3AEFD7C445C0252455ADACCAAB5CDCC223B232D241C2C345A4424D5539B3F367878CB3434CC5C7857BBE671C7B1CFB674E9D86782E7D737A79BC5EFB1E737CBC67B967BC775CF387217C6D04C58B8A4B85E141A2C179775982C1289AAE88DFC7BD3282F1F16E5692C1736F9D00D70528ADC5F0CD
	003A5C97DBCB4A04C32B0A211C8A651C97DACBD6417DCA41322AE2885C0451B3AFDA9016BFFECED15FFA728F211EA534FC68D0A9DC178324810EDC0BBCC47F11C32B95FC0E02C7D8D088B8CAF36E218A855788E593C0BCC0320751CF016BD22AF9C85DE6056E6039C9423CBBFD054B089C0DCC04F5DE465B28DCAB053FAC487792DDB7D1BB89CF941453G08738B058D4BBE046B16668E7BD79633D32EF6F60AF6292E2E4169773A44E35B2C22D716FC7BA55F9127C5EA965D12CBF0F591F905070BA93FA054C239
	00626EF5909DA9F01F81104B71E28741EF71351634G38FC6857179FECD16CEEBBF103B04BFE7B5295E17C0A76B1FF12FD2ADFE97FB4AF7C876D039A93E96B59D0D76AEFAD2984E8G3082C4833CC97B7E33437F046BB633DC6F696C743837F93D5BDDD65FEE0FD5F241FDDD9D54A8386FA50B47E79584563F6907B698585397B03E998366E3ED1274B176F572775788B963FFD5384420CDF212CE940E763336C8752BED42F4AFB99CCE77CCFFF0AC866B7ED1506A961E5934E411016E0A87B6AD192D48192F513D
	D5A6FD3D4BC97A3A826E899F09D306BFC071519ABC0B4BEC9AB3241C84F5655D989BED4B51162622B1C259468B8D5BA17E56584228EEE6C3FC375E16E9AEA2FBCAB753798937A50F62B79FD1714C96BB0DD3D2CE063A36C0EB890B2F4F4DE41C969D075882588C508C60850885C8B80EB196F57823FE46D833681337F7BBDDD649A7B05E4FCDFF072B9410FD62CE37DF965D9629D574B9C533CB728B8A4DF693E983F3905F37B476AD2063DB49AD79C45969F103F431D302F16BE7B44D26D7890DCF2235AD4ECE89
	94F4FA85145BE6DA89D7176817FF70DAC5D9D2A9D83D49C47A6408588A92C1G5C33F631D9C8FB25037F59G33F9BBBCCC65FDA779E03A282BEB72F8BA3A3DAD4493E17389524EEF5139E3BE50171F40B6B6DB892E874AA3CF201C574457B4F29A1C96E23E68BB06E32C59C9FCCF827AAC00F9G25BD2DA59BG1AFAB00E6634E19CB5AC3BDA58F07F8506F19487B1ED6DE1715353239FCF07299EE2EBA254CD811885D0545BDA322E975701C79DA61DCCE28B310958C26CB39AF79BFA0C71DAC85270B2DDAF0568
	DA2A5411F134A80C1C1288FA88AC545C33FE4912C89FB7001FA60028DE6C1B5DE2A50B756D22DF42685E6B0340142CEACFB1FB6FF6B9607AB540BE0093GBFG58814EG6882009097FE8FF08E40DEB28EGEEG3891E0BF404FG3DGA24081G8881618A00F3816A81F6GFC8B30133751DC2AB39E6CCD87188DD0F69246AE0089A086A085A09BC0783935248A2099A0G10G1081108F30BA2A35E4AB0083A08EA08DA09720783456129A00B6002853B086BABB92960436696F7B516E7FAD78DA396A5B846B77
	AAFD30DF690365CAFBEAE5084AD5D27AC82F837B8B4B4714BE6DD57AE939522FC6761D2374D9FF7D39A2C2BD4648C05A0875D536E1DF2D7C857B8A479AD9B3B682FCAC3035876BC94ECA46725B829B5F847701CE46FB02BAD60D46778A6A2BDADE2F783FD6E079AC41AD8BE35F6BG7F9A00BF9FC628A773461A8136911BEB6A4D448F690864169BA42F6436420DDF295BE5A6ED232C1BB68F67E926F8AF2C3375303079B93E55CC5AC314E51F535CADCB7BA5D9F63A6DEA7DA12A4B5FED762ACBB46A124C24BFBC
	C7E11D65746DE632C6D9BCEE37C4A7BD4E22543B689AA93924CEB03A5E536D16995EE7A14F919D52B1E50DBEE2D9AC10B55655CD56FB161B270824CF3DBE4849FD47C84AA0941CC6564FCF8E2F5634D1926048B3C6A6DCF3CF43F56F68DADC77569D3FD2C8BF6CADB44A6562AFBF27F06B69ECFDE9B9DDDD7718CDF62B316D1DF42DD670FEE66B0E05EF40350F47869A6FAB056B2D2F07239310463522945B224153A9BA5D9B997E39FF523E03F6BEA26D3708AE69FB51ED17B6F2BE7FE7E47C56FBDC9E5FEE513B
	3DDB16BD6EEFFD1EEE2FD27FD28F114B70B4AB5B48563825F60217417F98000453F10DFB46CA6AB17FD096B7906FE2DD8DF5F927137E3842D92EE9533453D9FF10FB6F99247EBEDEDF8B6526B37475BB5B31BECDD74F647F2EDDABBFAB047FCF3CDE0806B5ABDA2DE7FEBCA0151118163A3AA5C8ABC9A2769D0F3BC72457F4F52DCA8C327AA7A59203AECD8E1798CDF4EE6944E75706E816979DD4748622196E28A1046EC5CE178F78E240D58DE1740EA10C2EF4A8EDA3B752E58EE1EB762CA10C3EG201A4B0B75
	A0DD5BD0A647BBD425A3F8E6DF1B9769E2943A04214C3E7421DA7B4A3B10EE16C2B7FFA84B6DAA06E96932B8DD53B0D6EF9D46723F1EA03A849F5245EA6AD12E6A2F4D0FF40943D8AEC27CA5741306B1FF199F6F48C817BD0C7919BF0C79D19323557BA427F345B03AE838268364AAF435E752D86AC63AF9B1AC6FA1FA093C52B3357244A3C897F526AAAF7ECC3D3C8F0EA2DD5A19ACFF524BE3FECC63F4D9E7329CFA76190C2EA0C86FE981242B984E5A2DF6B823EF9A2E15376F98529DD46A6561417D4B68169E
	C73A6861ACEFC73D2307EB75CE66F4690A5EEC05BED86F70933C7D14FAE33DDF75A0DD69D92CFE1DF25DF216162E0053F94FE2FAE3943A44A03A713DC817F116528F8ADDE52C162E21026FA945B2BAD32C915D62B5C8979F2B4409213C1BB8DD0AC2271747667F975613B5096EA3350BC744C0357FA82C2BF5F53B686C4F760CAE214FFD04BEA3967706261B495CAB0C00F98B20E6842EDF77AC433D21119DC9C24EB39B17045B73EA9C416CF00DD0572F4C7CED0B595C3E5FC1F4A6C35D2491B82F1BEC5A6753ED
	FE3F4762240F464A1C37422EDD5F982E5C7E8ADCEDCE1FDF5E8989C8G6747B76C44DE726C2C2015769851753E505FFDB55CFF697DC66B7324953D05A5E1FC4ED2FC4D57781C66E9D94276B17E62443D3E1A33E11EBD9B777ADEDC75FA10CD5D32937C270B2800B6D8BEC69B620EDFAE18AE38A82C8D89E7B35D69E72BB6ECB8D930085960EDC79B8A203E669C34E16C5A35C1B6583B1DF5F51A253A5EF94469F29E172C4A1E4CEB9DA84781B292381C215B5798461BE6F597F0DFC76AACD57AA59B784B62DACBD8
	BE1059C9E24AC27385423033414F7B6F46CFC95E5DCD77C36AB94577225569E1AA7CD56BDB15B69B33F22B52A619570C91367CFC2850E89FB3E6052330B1CED9376354B64BBD4F3CF09EG333552DDAA30FCE692504C83E0B940F58EED8E30615C561296004039684BB077A60165C69EF7B53BB4CBBE1B4757A9D92BC88BD50BC765EA1AC3D7EF2355BB487DFA4C1DA63A09AFE2376C2119950F072B5F527AD6AC8EB174AD7699FBE16C394AFE64392AEF0D671B979EBC9FFD1B4FFD1B8FB43567E1DF16FFC52EB4
	5F93501EEE8F9903FE0964DA12155952F3GED094ACC95D62F9CE1E88B1D8B4EE3B60CBCCF35257CD273422ACB5116899E0C09D9C0D3F1BE36E305D75F8E0C7EB4AF5237GEDAC271F6DFDCD401C34DE93C998C72BB62F56451168D5A68188A0A67B4BAE14BD85644E6732933B7A166D7947BB340D647D121212D6B5512EF641102E5E8F0FB8EE7B9790256843CBDCCF6D48569257C854E372AD65ED4D72DA069F6EA34FA8324369E77C53FC58FF09401B834070677891DFFE812C1797205CE2F9990FC932A5AA3A
	2D5C16DDB25A928D7413B84F3572E063183549431F125C096EF5CA0DCE3B2389C8DDBF78A59FA5D35A07085876E38BC55186E2B6AD6FC6BF6601BD9BFE05B6F5F713E7F427ACF5D6137F764B24AF9DE45A8CB1E9A754AB8631F17B01CDBBEE2DA0B396A0054BCE6AA64F63121B2456B8674D6E6671GF473FF0563A4210E2CE9EE0F3C533F8D8254EFF10952FEE504D42D7FB79F33FFDD0363E4DDC32230F9DEC758F1D2FBA19B9F87AFD447C9723217962583B0DB069F41B6C9861AD99762FCE4329B79D5910FB1
	51FC94FD28811C036367F2FC82603263319D8E84B056182F8FAD257B61EEFF37CFF988D4C78D765738G4AAA9B55DA52B88AE53DFA2CC488FAFED462790BE34171FC7C9872C781EF9A677F030EEE53F1244B871A2A0B30BF5EB029B66EF63AF70B01D09B1F623CA6600B3F88F56CBED1A604BE4BB21E71A710A78B680BFF0DEDE76FC17CD64085FE0D32E6D03C536D141DA2D9BB2B56B37B5EAC2FA2FD9376197C70CE8D0BB2973D530BBAD2C07EEC2EA326170CE53FD702D449C5A7A3F52D7C4131CADF0743839A
	02494D61F2ABC73716340DC639E5957FFF637DB67A3C858DE869F611E1596D17BD1DEA03F23FBAABD0FF9C684E607AE7D6107702B60F255BDFCD7FEF7278E53D62EFC8856AE572BEDA0372040BDBCBB6DF0CFD748447FB8117F4B16E8DA7063C134B3DD85D571F2739C7E06F2A3E267BC7453FE16F2A16F07CF794DF2941B37D7750FC1A14B7C35D685F607B95F787D90704CBE00C825482B48138GE2AF413CF80C1D6CC176797EEA279BE248AD3A7687ED1061FBA487E9538E6918D2BE68502E6B4C0E9C9719B7
	12C06794009C00F9G6589E843930E08B66C88985A405AD9E89F5C7B5A658366D35E8D3A09BF35E0FFB30083A08AA0017B73AF6706C87E60FBBE4591E64F11C17A91BDC83E559D0363FBFF10FC9D1DFA3E740460B11023ABB33EE3A1FC553A32F0E9B09F6B27F3683E51EA282B85E882F0G4482A481E4DE0A7DF55C79E54478A393C10B279EF00D9ECFC73DFE6FD7ED9BF72436B17631B6A2DF1E4EC7B697FE55CAFCB0F8862FF7C8160E6D1E0032FFBC54265FBFA6EBF75198F88687E04F6A4F5067E6E2CF4D98
	1CC39653BDB087142381C683248164G94GD4DC06EDB85D79C5BF5BF01B129B6ADB1035C321E7F06DD79611EF4BE5C6B1B2142EA5ADD067850881C88448G1885D01C087E2DE95F9E49BF7E8A9EFC44F78D8601426C1D98E2EF7F464234903E37F4654AC4A33EE15E41693BA004CFBF76B69B6A2B6F9A1C3EFDA1FCFA7F1A8D753DBCC8FD4BFC1174B98C755D9D42573FB8FDA4A2DF14213E8B7C0353F7D9C43EF8C3FD4D72605A73D0881F3EBDD38C7515F78F4E3FF7A372E5A49A4D1B6FD511F1EFB0EF36C881
	799B27643232F9F3FD48FBB7F2A6A497E44ECB44F973108D77B8CBAF475CEFCE08DD7DEB3F79A1FC553AF24365C673D891FD97F0906A82GF1G49GD9GF9GE5E3F19E6BEA5FA674FF9E0B3C5E459D99DC3F7DA6A2DF4D58E0BE5656975A312D9B47E2DB379E9DDCDBDBC37874ED9DB556082FE0107A9687A2690BB75417924257BFFDB70770050CC5C3FDD14786276F6C90BE3DFF99067AB69F9F1C3EEFC278747AF28D75658DD25FDFC278746DD9E0286F46930353B7B504CF6FDF5995C6FC1DBD0353F7A204
	CF2F2F4AD05F873D035377C9881FDE5FD6C3FD3FFDF3F07AA60770697535996A73D78CCE5FE9A1FCFAFDDEC3FD1F2C991C3E7A90BE3D3E98C3FD4F8CD25F0B9175A568743133209369BB41B4284B84E038F811607221DC1C04737A8E3A5F31814A0DGA60E5FC771BDC96C99A601639FB4376974AFD7783565C6DD39BF206A4EB75431D0D78EF79E33AFA072599AFD34A37C5927139D46E71F22BB0C4FBE8D6FF81561C1067684B2561F57517759280BBA421F0D1A658AEF671FDC46F6BE6FB23673C5D75FF63E6C
	6A5B4EED6E7036B43A0DED5963B63625455D37AD875D61ECD9C3E3E15A5FD7D0FE3FDFE2BB088D22ACAAF5CF4B64FDEC485931BD10F189788EE6CC67D205E6076C295F48750E720477B141E36C63D88F5D4375F47BAC125E0F2BBD2BF91D1A61115799449CC6F3BD6517C51FDD8AE90749141F5505633F43932E2D182F7FF13F227A3AFB63C64C73EE66B9F5FA921E9B789BBD33D5G6532ABDBCB2A2E643936034CC3DB2F8C9E57ADCA59F1E568F8E73615F505EF4FD5DD466D3926ABFCFB2E6B0A5C1E7F6A8A5F
	1E35DD115B734B2E3E5B73F3CF09521E4DB61F273379F3C5F61CAF3C2F977A0CFD3D58975E57C4DFE4DF2F760577B55597595749BE620B59E9F57A1437144C47391E2018798CFD3F592B9DB71F71B6B95E95C4DF0378CFFDC178CD08DF6C8F42FF0C78CCFFF05BFE2C6007499A7AFAD54EF75D9AFBF613BDEE965B62E15CE76B3992E37B9DFA66BE954A19G33F8ECDBECFDBDA74DD7E23BEC1C9A63CC77FBC702EC2D5F24347BF2B9FC9C3CAE9B47411BF278B8F8D70E9C879F4A6163E0139CB98E3E107B0E7929
	C7346D7C19007949E8FBDFED37CEE9334DA1EDB762E8F05BE1BFFF94504657A6CE7FF4C09BAF1BF8FC5DF0CCCB3F1953FB0E87458B0F2F35A7026470B84ABF211573A9171358234557F27C21DEAD7E930EFF6ACDAD7EF30E9FD791E44FA77CCC6E1AA07C27082FFCAB884F475F3D0749F9E617F84C53ADC3FEB70E6EA9737D13A6A8DB81E281924631FD943697F3ADEDEF1BCF6C04EE5D6F15AC42482B886FEFC5649520DC8ED08B5082A0DF056F72BE7E8F2927AFA03F75B92DC1670E3FB693BB690B41A0FCDE87
	6759ADFAF7B509E6D24776FB22ADC67BBDB920AF7FAAFC8FBEA968BDF8F1B22359100CE3763610B13BB5D90D3116645075083D5F6B225F45B912597B3D059CFF0262839ABCFB6F3750016F69E320AEAF99F369C5CEB23FB4DF8D6DFDB56276D2DCG4AB19C1762441CFB6455E83BAD681D65AC4097DC2D5ADBFACDB85BAF2773D765B54C46959CBF01629BB5F8E67B8794CF73A4284B3B86EDBAEEC31B9C3FC5DCB15D73C903F2B6470D37A3DD9E47FDE75352B1BD2F1A70AC591694081B940C390D26207DBF7262
	372E0EC450F6515EC4A3184D5587CC458AFFF28A4E5543284CD9D01E8FD0FEAD1EDF8C3C0F676812470E91321EB4BE3F185B52DB3861DA560E4D57EA4EAF7E533C10693CDFC41D31D0BF65DA5A57E6ED4C11BEFFF881FB5EE2751F84551379724220FAFBD0BD31F7C2D07D2820FA52C60B8AD9BD73ED7AFB685B7C69A902EBC2F4586F11CB2FE3BE55A8574A992396D40DEADE4CFC1BE441354FF19D366777743CDF821453G322E43BEFA419666FCA99D31CC5E8BF614B77BBA0C0307E89CD40E0779E2BC628EB8
	B0DE5A46E32C9FF510B39FC93CFC9AAD13F34F2C3C54E1742C978E081C01BE6F3DAA3CBBA09D7A674C7E502F94AA1C5AF3F5B963C9FBFC6C44984B980FF3E9131D1E01018420B215FC87FFA57944CB08CEA574D31D8DFD68D92040C2852C40C3E02D028F243271CF0075C7AF4D01B2811F1B0AFD740C19600AA634162C1E0038B9FC0E5AB2817B65AA090C5DD8A8A781CC821889108FD0F4FDEBC94575C8B7115255C03979FAF60E598B57386B5999E9F21612517DCB72AA72089C62CF7E04603E1CB4A1F25F925D
	A1B1E140D72150B15DCDD4B7E9B37C5ECB7FCD5712FE74335177DCC6FB90AB14AB6B53F3ED5827A9863623CD5827D300A69B60F5C5F65E75EA1F3278F95BCE4ED8BBBCBE67F10FDB96DD5BDCCE3B1B1C2796D84CFD4E776E8B001759F0279DED283C415806C59A9BEAEF88362171865586561E2BCC03BB1B705EA0798E8D18CF79B6DEA4F3DE0B46FE06B7D33CC903E76B58883E0E3920AE6B86FCBF9F5BC366485AB408F1G87C094C0824024B4FC6773288CA9E95F6FFCDABCDEFAEAD2335D417CFBC89C28FFEC
	3D38E5892E9729B70D935206B604BF571E260CB9651A16B9EA8139B27F5EBEC97C2B3C9146BF0089A0G908B10F8A37A77FE37F25EE60755A9EF13E5516220277879E19B27F4D4C513841379F60BC56BDB9A484B3CD10D415C9B4365BAF75075EA5E0D7ABCAD0B620BEE5267E9ED9D7C2CAC5485EE42F927A06879A467A6259D14EB51C4637130D2F85E961AE7975BB1EFDDBD116559B5F0ED184866B9B6563C8E9CEB878DE4E33C61F87382CD74C4B656C8B9FE22FE3CDF3C1164995B705CB4BDF1E27684F87DAE
	A711E593DDFE75ECF942D16D6F1AECFE65F443EF9489A4AAB626A9575817C7ADE0B26E7EC9AB236CA2D76102B0B272955E799355BC6BEDEA9B2C73027A0F2DF7E752FB26638F25983F95D7DDA6B4BC58D038B60C0E1AC9CCF654A4D5C71533F629727DA27D46536C715107DE56D7E379B3D19A70E4814C87A81B4C7633991FE0896D63D57C5924EAB26B632DF0EDD178D89FAFEA47BEF6CDB60E9F6DBA9D85B4A3A72B73E952E4FD9F67FFC778F6BB5D74E8A5BEAB313A8B83452C0EE2996E5113258C475F23B37C
	7CG15A3868C64BC56D34C6AB4F21EAAADE5B81D1C956F1031433EB05A281C371EAB691F47C8FB24CE46673547EDC675993C5E9572BCC76AF3F87DAAC37E7C49787E7FD63ED7D9F4F3EBC9E8FC65FF9DBE66DEA0CD2D34C3331D4CE71AD437DABD0DD05D34631B96E57F682FFC9D5D92C69F133D1608D556294322FA6F505C1FEF9AC87EB9D0E0BA12CF298E3686DC5093FEDFEBF40F713E56189E637D7F241E3E5FB5247404FFA7F2C30F76FBF136064D35107811212F62GD281B2GF2EE4675EBC76F3B82DD3F
	8C562E9DD6BB59D7A33F8EA3237E2A1361FD7D68E4645F1459FC126C0B18BD326C69A472917F55C952DF3247CB114AF778A7C99B791CF6074CC8957C4E13A45EDC120D22D92E1EED411C3E60E64C5FEB9D782E2F62F7083B066774DBFF07EBE1E687F97608636587E8B905178FF5040BA142735F095323E1F58C74394B88186FABE8FB4C841BE7737648266DD11A8EED140E38AF5B310D2A52F1AF23460EB9E0GF0A969B87F846C78ACB7BB9D1F2947D25A4ADBC09EC073ADC8FB2983678E47AD831D2B9EA45D2F
	44F4B41D7392C04624DBB00E9FBAC9FE8F22D5ECD7B2CC120EF9FCB41281596C73F8A51FFC0CE16997670A2C1F49CF0EA8F1D57973FB61E440E85885F363DEF3BBBC473608F6EE4BB81A5764009D85G953742F3BFC0A30089C0860881988D108A1089108BB08F2078B6F8568458G3075B674E3867D4D0CF0FEA81BC4F4D07AC4A79916CA1EB763B066F9ADA0AB76B65C47B894A0ED2A7B7E3145A93BA46BF61A98EC17C437B2377F5D047B99D3EE433E7C141E6F1F8F6572A9E06B940C87761DG6977864075CC
	41BE9EE641F8C81D02B2A6D05ABCA8975D8EEDF4BB52FE5A0E71D0FB7BC063E1E8BB39E7F25673B55045E57F032FD3099CF7994765F05C9E4EDBFE870E7729C1FB28CD00B75D5177B8D73E85F0E879E460090940477A7CAF1670F36A82EB64B97595767B98B097369C73AA6FADD6D8C98E6731597C921F3B4B2DE59C37535F6C71BB497B8E165FAD36E2FEB77A8E16972642B583001CF5E0B47FF1A0CD6E9DAC87AC00EB69D4C643F2405B79DE4D3A2946B92096B683CD53D4B54FB7CD556700C79D247F77EB3F75
	E1891832E73E44255FB3A73638C0CE54D44CE75FF7046641DFF2DF6201AE85A087A0FFAABB5321644FCE5D1ED48709B966E78B4FF50BB342BF4F609EC2855054E4B0BF691849506F9DFC5A677B2270B157B0C83E5E817331674CB7683EFFF306FE6FE0AD459F4C506F9DA473F54381F51999B84646860D151AE9703CB82DBFE365CC9B396779911B4A71BDB34D1579F7E97493FB76D5AA2B3F5991FE1C5D6E08BC4EE6B80CF317190E505C65778E635C65B107B6F7D14E32506F166CB03737F13AE7B86EG474D21
	B2896EC727D5F6F07CBCEA9741B7CAC4A11FD728DDC7A92E8D4F32E8F08794DC11130C8FF0596171A16FD29A7BB6274B454A4BA80D538DD387EF0F5768B33653ABEA6CDE45F187F82C4D8C7B7B1088ED3A3EE7FC0FC764531F01E4FCEF866D0B499A59B186B1B69AF0AC06475135AEE51ABE3647D37C24E97A585EE246584E003A8DF72AE74786B7A6778F12AFF640FC4C2F5F525FED083BD36FEF9A450F3ED36F6FC43E66A7C1DD69F474770B20EF403C000F1E5E1F75325A70FCC13C421BECA003713DE5B8F725
	AB74B34372A599724DD6686707655BEFE8E7D9A623DF17994A477268C7E9EE31B91335A343BDC1F14DC138D50ED03A8A07160E357FACAB66F8AE40A7E6E28E33C1423CADA793F32E53683CDBFE9744A5C043DD18378DF1E09F9E845C483BD046DD8E4C5BB26FC299FF2534C5B3A0E783281D01347F63441C29E9C6FF73B65C9778030B14181C4BBBD1CE748C54F9699EBA5620BC93A0EF865AAD71774DC5F73716EC399B735BDFFD4FFF17C34DEF1B1CEEC9135D223E2B9D789B42725D286FBD9A6B49D01E8210F3
	B77AB8046726736EB672117DCE1EF1EEDAC4D4AA31D369D06F57EB6E2F331F7A3D0F6927D4870B53498631FBEBD06C32330769AB49FBA2275B85DDD34FFEBD1653EF224F9D9B32E09E4E42E7500B68FBE8AB1423B96EB60A0B07F2F2964EBDA9E635CDEF545CEFB7BCAB7B5FED5F1FFE819D931C2ACFB7EB6EEF311CFA5D8F1DD29D2CCF3FEF8F0D1F9F5BC367489D6CFC8574FB860F07F17C3C46CC0E7B440EE7CD565D43E3091F572861385FD1DC941463B8EE9DFFCFBF1A635AEDA8AF0F6326F33D857760BCA4
	3EDFAA705C1326E78CDF91D82662B1BDD35A770F564165C58591790C73220D12162F71DED57641FBC357B06F3D18977D192EA551776A73047F257838FB75F942B01E730F06BA61BE9C2BE7B986175F0C9AA45F3D8366E3FEF9681AE03DCF6FEF00623D77697DDD40FF7F2A876AF2393FFFF5EA75B74CECADE91B591F3CE8DC48B767AE8DDF14018CE65F6BFB099DA3E7B27BCAB8FEAD4527EA704C6EAB68DACB4AB321EE737DE8F7C744F6B30E4BE391790C63723ABE79C65F9F4E5FD1345DD36E577753980A1FF4
	3F3E1FBE647D1481F59B9EC0FF0787652FF100CFFC206FFEE27B83072550FD051FCC7C7D9A484882A8FA3035E47503CC1EF21657C6FEE7C9A61BF03B3A7D32536634301F03D34EAABCE543BC2566C15C0B2834AFC29E81730CAF496B84C566C1DB70DE05099C6DD076AAEE61E7ADCC8F9A6F49AC9154BD19EC4DD98B52D67203EABBB259E7F429322F65E7A8228CE4931E25863271FD664887757BBD8F684E810D75293A3E606F109342783150C0977A119A6247B43FAA7BF15EC669E17CD8E4A09B7D1829114D56
	08C689F31A5C87F15D18E08B4DE9165AB127A94A6673865D13E677C51A1CF144AF10B30E7D85F4F4EB7218131A7BA54ED32F3B72146AE0FD7A1AC1CE334AA0273905BE7F14C13F2F4B46F8586A415C221147431BFB493ED6BC14D38132GE683947D9E6B57537ACAA8EF85B081C481A4707A2B77E8FDA63C641A15EDBC479139713A3A54B034EA3B5E9124E329264E08072CB98379EE0D354DB41A1F2503FD595C37CBB4711324395F788B44522EDFA016AEB5082531C131445636B3CC78FC1C87ED336EA14CCB97
	D512F7DA7AF32C55EC512A2B5BA1DB9C78A6C8406764697C5C71410770B979A5AAFFA414138126BC046B525FEC6B0460F383CCF435769D93BDC040E44F656FDBF2C0861B0773C8CA2C68FE1C3F23A9FF986B679ED16BDD7C9DE2932FFFBD20562F647C513CFEB37913814A7AFCC44275F95243ECFD1E8957598FAB67E7A87DCF47858157236C4E5033B04FF572EF83F258FB1055F05DGC05E99337564C78F2EA78DB9FD3F9FA05086B42E9C76F723686F4365685745079A493B1B7D755BA5176768EE27FB17663C
	4A5F9AD85DB633670834DB8CE86B4ED9D1C26B76EC5B239CFCE178E5747B497D75BBDDAE49AE3A026AEC2BD85D9E0F76408CBB0FF2E82F7EBC8A71B9B6874F2BCC6CB12ACF60759922D1FDAA2F7F54ECD41F416B671A0C6AF3F2B09EF3699E396CF0DABA5C92BDD741F22A829EB779B9B8F71C99717C24F11EFBE1C4BE63BCB7EB1067B5BB6939CC6191FD1EFB0462CB9F516739AF73731A2B212E679174774520FD4CDC404FFB24BF4FA3CF876D1B96BF5ADAD271E8FF783A025F71GCFC33F78D68769EB839ED7
	84BE6677EFA41C832300BE6DD15C4BFBCA4279AC7FD11C4F4AED04366AB1D8B3815A9E43FD46D5EEECBBAF60129EC399075DA8A377B114B102522E4EEDAD2981E84CC55A988F461AA9F7206F35CF52F36FCC4E85FB0B8576BDC5BC17BD3787F173B96EE591F126BFA02E530C38A99C371E53D5BD0E63E50513783409373DDFF3BE2BC7F33FC8BC756B69DB27D487DBBB175BC357D336E68467E67FEBC0375091CA97E3307FB942006EA383F935C1FAD99BA4CA031B132E0B48E7BCA77DA9A21F711C94C347CF6C63
	7AB969DC0A0FFFDCBFA73DE143F11588F545CF601C34A662EF4F99474A070364338E180F7935007EFE336B893D3F2FD0FC60893D3F6773F9A49A6AE6F3FFC7847D06DA53AC4857E775E7CE3CE8AF999BB25046E868C743FD0AD21E82576C88328864A9B44213AA2D32BE74909D65002F5254ED017BE625EC05EB604948BAE2951A04909DC5F44FBC9570991A3A9C384FD74AC5CFC1BE75D4E49D9B941A2DCF69F578280E9640BBB4F5BDF09F271493611A568F9D198ACDEE080EEABAD74D83FC7153EADD855C57A8
	65863836BD9DD907CB2109FADA2F63EC1B406FAF545CD75ACE7DDC7C79AF20E309C77529CCF3BF4AFD6AF5DFF7CAF53079BD5193BA67DF6989DD9BA651F5FEA474FF7253388F3036193E6F00F2A64739A8AE974A799CF78F454D07F279EC443DC94F47D6C1F9AB47A5D33A96A887B8EE41BE5CEB0865389FE8FE118365799C770FDE342F6C997E2DECAF5AD741F1B7D3DC8D1437F25C5A13E8DF93470D7321FDA60E733951BED91567C67BE2B82EDDC47B32B96EFDB35A1747F1434CE8DF8147CD273852BF024DFF
	C4DC0D856D2B623843B634EF8B47CD3720FD4D9C374A0A76D9B9AE1B5B174AF1A339FD999C77E7EE5FCC0E7B1C6A188D65820E7B8D372F68CF081BECC17BD6F35C98AB5A37016366F07B36F25CC8935A9747F14DA25A1748F1570AE8DF8A477D196226C0F9A6477D5B0C7665F25C2B925AB70F63661A503E62BC44FD45FFF7290263269FC27BDCF918235E3717BC434A9E0F2B4569A5DFA5A8397225BDE44EE0B4F54D743C23D76CA2674158B35F3EE65CC70F62324EE88E5EDF1E522B4A185DCC4FF709168E3B4F
	53CD7EF8G13A350F19287B2C6F3B91B68B9F61B47A785536E2271BE8968B2B86DED4D64EC197AEDA2F181CFDFA25F234D78FC111707FB882B77619EC24533117790EA002661D975FB1A36E775FB882E1E3E3F119C5A03B6D81F453D1327CF1276E1751775E2FB0EFC96FD3B3FB738BDC92BA0FD93ED0BC420CD81E0387B68584FA2E50E7BB5FF369266206F773B51771AB9113FA5223E4F5178BEC7751D69B81FFF57EE1D033E9DD8205A1AE2663E4DC15F9EB7877B56D328529F20E3B091E8D366209FB1F45E48
	A2E50E1B474FC38B4F21EF55D66E5BF3213EADB2726DB90DEF4F69FDDBE0663EBD073EDD32D03575AD8B776DB9742D59926C5B0CA215FEAA738DE8D31EC3BF1E2238ACD2663869D66EDBBE7F86CC623E659B1F278A71ADDF63DB3E5E3749FCAF521A0F3E4DEAD3EDFDC8643E6523EF253AFEDB72EF157E8C5AD609C01B120FFE545179AA0B14B96ECD9EAF4273685B8FA2776D79D05F169A79763C463767753E4DBB44FDFB9EEDF730794957ADB15B5F7A0F40476F4A13E46F0B7F2DGFA9A3A5A6C02790A51C69F
	D2E9AFEBDE9DCA6BA2478F3E750947984D1F1B5F910277EE77137F71139FFFF57D0EBDADBB3E97F0BFE267BE5C5798797CC077B51EBE2C5AF6F1CFFF75B27A1BFA035BE437284A9A636B3BCD461BD5DA27BBF21BDC679ED81BAC66FBBD1906ED027EBFE0D16D98A78E4C7F2966E07F37C82A2C9FEDFD7BBF4626525EEA096CFF09F5E07E9F36227FC5F37B727FF63BEA47EA73407C7F44926C7F07CED55684CB5F7E1F5B2E520E32C6767F71817A7F29047EEF69537F548E550E192601797F3C986C7F9B1D2A2C25
	D25F7EC739D55AE7CC117D5F546F58E47E8F9551FF39CF7FAF531CD5A8B95CDF9D6CD9ED4FBE6D1EC9B268C91F5B1FBD1312203D161940B33BDFFC562073D57301276C057E70FD913417368EF8B6770B6F2C205FB9E8829ED384BE16ABCB7BC21FAF5B7705BEDF3EEB709C7A0141F36889EBA8DD14B5146EFBA9146EC7A914EE0C98CAF7052825DBC563E1DBDD8184C6723342A2D9F805FC52DFC95FBB79FD428A9E2CBB69178739C7BE2EDB7844E58B8B05221D42E276A546CE2108FCCE31D3D8C43F3658A9AC14
	BD5E00D7507D73F789A769DADB2405C1E385444AC221255330CAD539D8D3730AACAC22BFD9G572A5DCE77FAD9A802CB9DAD0A81DA9483F57FE5F30BD4F397312F2842542E20AF98AB4122CACA876EED5F8DE87AEE3192AC6368ED00FEED0FC72E5CD639273B53AC79F84D1EBD3230D2F931D8C93F5061D5BB1BE4E1799E2503D5066F212F1787CFCC8BAEDFF5C659B99FF7978A455F880BED224B6B90210F69DF77E992CA4C3433A59F504AC2997DB3EB9F816FC714770BA668943FAC3C296269DFDDE4357B3D22
	C5821225D65157C1A51036287CC9D8EA73594DD481587453B7BF01DADAA2556FB6C14FB9486750A42F1405D52A68E672A126D271858B3C17813C749FED6737A9AEBDBDDD0353C60B407B6B6D7FFB5C6CB7AF567F2C7DDD0D02A116303F4D312EC0796D448275B7AF583738C73FA4F30B8C7838827C1EF66A9672B76543FD533B3FD3F43960C938E31B5BCA3E6F5DFECC1654EF6DFB3EA4727DC823D4323AB33EA4F637EBF87E9FD0CB8788905209CC9DA9GGD00AGGD0CB818294G94G88G88G430171B490
	5209CC9DA9GGD00AGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD7A9GGGG
**end of data**/
}
}