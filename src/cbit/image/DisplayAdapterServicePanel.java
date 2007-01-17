package cbit.image;
import javax.swing.*;
import cbit.util.Range;
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
			ivjDisplayAdapterService = new cbit.image.DisplayAdapterService();
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
	D0CB838494G88G88GB3FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E165FDFFD89C5539B09AD454E830A50A1A2BDC4504D86C25AD7D1AFECDEF31723431CDBFD32496DB543426165654263D5C4F5C1ABEF78244309193E250D0C38CC90892C58C89B1147C62D7A209A4E24008869396F658DD185D596C4E42C2C8723D671CF94F4C4E4E3240ED7A4F1767F9B3F35E737EBC67BD673CF3664CA214ECC9D835B22FC490D62E9254FF7BD68AC2658D0270D04D45FA44553D15E191F4
	7F36822C97E6B5944035956AE2D2B2AC45426826C2A8F7C3B9BAB94332916EB78817B715DD83B7E17464C68BC2471B6BBB465793A7221E5534AC3FDE845735GBD00A3576437087E6BDEAFD57151AA9EE1E59E60AA894DBD2F172938E5C0D3883886004ECAC6AF411599F0CDFD1DCA57FDCB12107BA3EF5E4BC40ECEA642C215F719FAAD977E32B2FEA75175E3EAA7612904F28FGF1BEDF28FA799D389EE89F349F6A6BF5AA55CE1755AE565737B8FD9E49BA52E833FA94515BA1FA071CFDE23B55ADCAC2495BC4
	5E865A3E79G59A0AB01627EA4919DDDF06F8108F99B71D1B241AF017B95GA56F20DF875FBA205A5DF561DBC266EED7413AB0FED53D437C69FCC7732B457DC45E5FE89FE47613360E053AD40085GCB810A2AB2ACD5G1BE85F7FD2FC9B2E0D3DCA336CF2496EC60F27C932F9CF48B6D1027B7AFA28D1F12744BE59EB9384563FBFFD3D4A441E710071BDB5E9BE56A6C9FB302F13CFFDD5487E462DF92BCD5AA4BB69C2DECB95EB0B6EAA2DCD186E52CD61F40FD58547E2306ECF853D6E007B317CD5A63A634EBC
	169F2B4AC954694E29A2FD6DF4113ECE07FB42C76214618394DF752E06E7F179BC0D99D2EE073A796FE2ECF42DC5DB5A8A6E910AEBEEB3ED07786FCF4F2B2CE6B6B4D49BED49719059D2B55339175B7252F902DF2C43B3DB2E25F1CA4A1D6F41582F49303078FA42C146E972EE08AD0045G4B81CA77E4D8EA81DA77E00CD5743FB74198EB37FA15A63FD33209DE0171BE517F9ADC450062359EF37B942B3BCFBCED75BA2D3D126893D41AE77AC99B748671BDCBE33F8FF4FCA03AC52FD5F14AEE10EEF509B0EEFD
	0CA637FFBD21710A3436536992C1014BA3205C957D9BC8BBD9FD4AA79E1BD591B58AD6DFE0A7FDB2E4BD8D92C1G5C33F6D9B3C05A4B817EC7814472F638ECA772BE943DB0DD5457374972205F53C9BC91966FA16D7C9E1DBB9261FE7E9EEC639787892E22B643F22096657CAF7BABBAB9AD4EBEE23E55BB02E3CCF6915F5B01BEGB08BA095A093A03796636839AE0C2316179F902AFEB653B40E8A3B9F4BAF5A4B6227E22FF1BCDD22FA082DEDD0A781CC87C886D8389757013FCABD8619292ACD392AFDE663AE
	B38CDE8F6DBA9AF5FC3B0C3AACEA9D99C72B4248F9A9029E82793AFB56AF8A9D1FCB414F4277B3AC156FE35F7C49DE4EE23D496A93B13ABF72C2E00AB62D27183D4FF99CF0BD8C70B1408500CFG6CG53G4E83C0088B0F834CG78080C8300EF81FC9B00AC481781AE82D881BE8300909066G5C82508CF084608300E33C0D561FA7BA9B40DE87C0AC40BC0045G057BB2AC3BG3AG22G12G9681AC87A82F03718C60810887C8834881A87EFB06258E2087A086A085A08BC02807B629479818AEA72C8CED53
	47A7506E7FA8B82CDE0DED02759F29FD50217641DA35BD75B22C6AD5D47B4828837B8B4BA3EA1FDED47BE92D5A2FE6764DD07BEC227E4C09D00FB1B219B6E2FD753CE9DFED78A776950EB532E654G6C91585A0375A4E7A5E3F90B4046B7413DE51031C350462A5978DEC7FD554ABBD57F378BAC1FA53817435877AA403B1370671D8875E45E58BC49B6E2F34DEF9D448FF1C8F4ABAD22C7F45B60462756BD67A0ED232E1B7DB2674925F88F2C334D303079B87EAF8E529ED6C571BAFB7D0A58A1AA0A53ED576A73
	1CC4174F5F6B5116E854E5F1127E1007E11D6574EB1DE40D6A135DEE11CEFA1CC52DAF26EB24A809AEB03AD9763B9506FFED00BCC78C0AA36A9A7D7A400B82D9E3A5BFD96FD9EE1EE2A5FD6A71C2CE6E9DA1A90310D0CF564F231BCAF5ED548EB8720C2140B5269E573D616D386ED50C4E95524EF9724CF239787BE66465543375E5C53D366EB11BDE9AB437FDBDDD2BD53C0F59FAA47FF5380E7358C0639D56F45B2B61688424115695E1DB3448AE2B53DD4370FFFC1B769D3473105851E71544D3D637DD2C61FC
	3EFDE47CB64B126CBDE175B47995C5F6FF60157D9E357E3F4608DC0627D9D98DDB639E2039E959FED897GDA77639AF753A029477CC3DD5CC03C443A9A6A6276137E78012BC45726BD7BD9FF107B5283247E4F3CBE9B6AF2C36A9DEE2C6FB990DC4F64BF69564B8F9CB0722F1131BE894AE93AFA664728D4CCE2DABC6F97A12DA4095807BC6E2E12DEB1541DD6E31055DFAF119814F4B9DC5BC122734CF9FCF6DDFA0065C58587195E7203CCF765A1C2771A976962811F823068A023AFBE4468D2A95D1FB91DF208
	2D59538FB17A8438D6F3F9DF71A11DF01849A9BD2C5191BC33EFD8C13ADDAADD6BE1E61F63305E3EBDFE241B2E52A59EE639DDFA905D9FB9DD2EDADF5440723F0A86BD5D7706102ED6D70FF2B5FF2F99C63A3686160B90FF893D2D01794B7CE888A0DDD48373B33E017911952437E08469CA9A99DD95DC9DGB10D0CAE3B114692271B5548729E221748CBED544B0B9AC53A4AA6CDDEC313D15E518BC857534472A723BC66C78E278BB4319CBA360951A5B46975A60CA1DDFA93EB37EC15BE37C9AF4FF391698A1A
	D9FDD9F3F07FB23A4ACBC8D7554C72F6545B522C571B4D699C4DCCEF14CA9F2CF76EE55EFEEA3D395EAF8424CBD56B972A57A54DFA3A0DAB112E3405695D5542685ADA74F4991C4E53227603CA37A008AEE79B5FD3D2698A0F18515D3F9D699A0E28F1E2AA6FEC8552F529F4C6F9EC7E7F0F1DE4CD227BC88356A1EB205AB78C6BEAFD7DF1BA7B33BD23947A5CC768BDC7F0EFA87B8DB277A6C1B98DA06B882EDFBFFD99770662861304CE79E77961763C169DE1F6149C55562F02054379ECEEFF5ECDF4F6C21D6D
	A84E6B85AE7D73E9234FA777B9692331BA67FD4325DF5F98EE1EEB8DDC7B1DDE1FF28C9210G4E0F7F6EA27612E7E7952D3647CD675F94A63A2F067BAF970F6AFD1E3F6EE25ECBE1FC8E283E466BFCEE7B7079FC360F71B7B76E75E5C1FD71873857777C46D703EC72ABCE701FAE22825A503787ED089D3DCF6819F9C7D89BDABFE03A9D9FE8B6E49E5B2B5AB0035B10G75D95C06595B378759E077BB6B6BF5CBF533F348A9B9C7C51B3AA7D3A3231C62D61843DBD14E35CD1BCD63CD373A8B382F93CD77FB88DF
	94704F83E079403367C9CC7551FC01B09CEB71717E7B767BA46F76537D10E6CEF14AEAF34ACC052FF267E9354D6E5950203649422F5EA39C38FCB64FEC9FB3E61DA3EFD92B3AEE9F535A2C6EE7CF643581B0DB8FFB0B84164F5800E696G4B813EA769F300CC402FGA8BF0E3E4C766E96D8EEA43B2B5925DD74764BDE17E82BA4ADD4ED9DD62AE98EDD5DC82B0F107B1D18BB7D42CBFC317A9519E6D6BC9E9EF8C36FDB21759AD35F267F4E1ED7FBDC5D0FBC2E7916GFE25F25FB6795037C42049BE0EFDD9F208
	DCE93EA720BDF1BEB286FDA24935C49B33E56CB334A5EAE12AD03A61A6D3DB68DC70A1336140071AAD5253CF64C5BD0D36E47AB0A62683CD7A07580E7B95E3BBB07A848569D7GEDED9B522F763FA2E0CE5A2C0BA40C230DF52506B832FA54C9G8208496E7623EC8964A6F2598FC510AD7FEDABEDA325C3D4D3524AB65A55928C696A8EF844F15B77C314228F25C328A79BF4141CC0BD1707AC3C2DD9DE4B70F30748B30A62F07A98FF4E9076DF9B70C683B07CD70791BF9FF039DC6EDB0117F9CC12ADD12B5B46
	EDF18650162A13B0B71FC41EC5012946B1EB13DF3ECFF2A73A57A936BA6D0EB6A015BE7109DECA26368F915178E9A7C55186E2B63DBB02FE4C82FBB239CDF1A364995D29082EEA72DF07C27A52C1264D90130EC13DEA909B37E9C1633668A3089D002E0FD0768F699E1968A629B54EF92BC7F9BCGDD62C7B8CE926A491A661615E33EC688D0DF1FE495BB5491D23973F3BEE6DF580C632422A5D1284B9D8CBBCE32BFE2632320DD9BA71D0F6F4DF7BC0E73785CD1EC13CE20195E0E7351D503E6FE2537E3CC4897
	5007AC4095FF0C7875E308EF85DC5447588E01B10CB5666BAFACF4BF5C6D73FB5507C0ED54E0FFBD4CE54D83B94B382CDD97DF92021E9F55F87E7CE2F0BC5FFE9179ABCFE5D8FACEA13FF0A9186E63CBC8978FB40BCEE1FF3C5E2359F84269BEE18D045A386AB27296F6C0BE5601BA244B45C26833AC634960BC8120CF6940363BD6C0FC8E604ABBD1569FA85E69F6AACEABD9BBABF7B27BB615D442559676197C5CB19D0BBA97B52CC49DDDA0BF166B1833120CE51FC704D4C922131136D6FE6258E82C4361818D
	41643E4065AEG1942A7A8B7F55B7FEC3CBFC41F9B21817B7C929916FE1FA23B348665FE2D5F0E7A6BC037076B8FECA76F857B65BE3F2F1A7E5FA67B94236223248275B2F90FF2F9C9A0EB71A758C757D5A03E74F406257DB46E8DFFB3641DDC4CE9EDDFFF166E9E013D2BEA5FC264251CE66F2AD6F3FCB745AF5061197EDF53FC1A1497C3DD4B2778FE6595AFD90712201C8E108D108BD0528563368B736223EE3287B96E7B2BE3EE08A137D56A885AA04377C8EE5226036208DA96E47D3A4E6C087211F9239DF4
	CAG51G33G66F39B2E13A35AF0A4E0EA83EB67AD6E293D2FFDF752FC6A3BC10578138D76AF87A87EAC43D289507A997A73D339AA12BF781ECFF50459B3F30AFE5CBBC53EFD7254780ECC116F0673C6BE47E741E3A05AD0E6FC71A1FC5506F212010F75539E3AAF1386F559G39G451FC36C833483A81FE3FF3D61B998B17E48C450A9B7832ED516871B0DFB3FDA5BF8A3350D390F9D917962BE8F66E3F3612153448713E770E60758B758A487547D6377DC467DE332F6A7034CB4G762C7EFF25C2551E2C4FF1
	8E391F6ED9951F4930D48134G74GC483A4G241F41B614BD7BA758060DEAEEE8ECC1568EDBFDD3EB3F77A272ADB9E396A3EF533D3195D0D75A1DE1598550866081188E10520D7EBD68E90A649FFF858FBE623B8613C0E1767EA2445E090D051C903EB78C65855DE6FC33152969CB8D61B30E3D45267AFA7DD353678961B37A375CD45F9BC3D353D79542E754D77C05995FF3A1FC930B53959179AACD75258ECFCD5F77A372B5186A1383D3EB4F0BA1FC46766CB25537E7E4EA7EB5C66473FCE1B6EF6E28A4635E
	E45E6C94834AD127A859583CF92A4FB8EF12B3A1B1A0F35697B8EFBEAF619EE76A97187B3DBCBA35765B9442D7EDA8AF7D42EC9ECB22FB7785E7619981208E2093A0G9087B06FAC4EE32F1EEF94A6BE0FC5DE6F7EAD445E09755BF7A372E51D8D66E3EDBDEF905BFA59D9ECEB7F052935753490BEE3DBD71EB3635BB8B6B5FDBBC2780C7A9ACC75BDB2C5FDCF0470050CC5D3FDF7DF1C1A3E87C2780C7EF9CC75FDFAE9EA7A4490BE233E98D3FDAFDD1E1A3E3F0570995BB341D4DF569475BD9342E774EF1E293E
	694254745D9142E7543748D45F5115D353579942E75417E32A2FE35B5474D9C3780C7A049E53713EFD0A63BD044F282F54D45F63D354775B087AF6196A3B29E2EA7A6E8C61B36AEBB568E3E7C19752F702BDD027GB05C5C4D84978F65149E1C57CF537D0ECCA8AF83A834A23E1762AB2C6C99261563F35E68B268DF2B726B4B4B8C65090026FB6596B39D7906F23873186381114F5668CD5E70E71F36F94D4FBED5FA4D4FBED5FB57B3BC48501FC0467A773D631F0DBA648DFFB6AA46975E4E99BEF3BBE77A4C6D
	3C53B73E1D777A463773B7CAF8DB16AA66367CD9B137E539B23EAD02BF1CAD1BE9ACBC7C5ABA4A6F7309EC8731452AD8553A5FAA64FDEC4859311310F189788E66417396156608A2B757F03D8707427B58B2E46E6371A13A07AB7B3DFD22510F0F07CAF91D1661115799449CC673B965D72CDE3B98528ED64A4F6A42718F8C05EBAB666B18F70D666B091A9A4C736CAE4C739CD6BCB730061ECBCE0072BC00C5FCCC3EA011F9A8479ABC2ED72865625E50714EECCB9D8E5F1E5F99B6EF4F87074337670261486D79
	5361706D19B59C39BDFFB5BCFEFBF679DED25B333D5FAB3B5A7F2E4A2E9D896F6B7E91F3DF9BC7427B5ABA9259570FC7427B5AB59259D76B88712557E9F3FA5537144C4775CAD04C6CC35F1FD07463E69FEF135B7DC1743B907F49D090FEB762F78C876177A07E596160365D2362E787F4744D1A1C01911DBDA7489EB70B6D9B7AF01F2D2297E3FB9F0D6DEEA8ABG53FBB136D7C863BDA7A52A31BDCF9763CCF74BE8102D4D3B55F67F7AE878B8787628F99CFCEFB4FC9C7CE0B4F29CBCB29ABE8E9E9B0D9C87CF
	0D0E9F734F0C6A5BF91F401FB18747EB3B05EA1BAD8EE93B648B41ED077DFCF2CC9FDF3BB97D0B977571321B4757E3977574F51C7E66CBC17142632B69F2109C9EC76B843D1C77391C070270359CFFF1259E3F1763EF5826477F1D63CFEE8F32E7AF620BAB02706FA3BEBD984F475F531B48F9E649BAA27B95486F7AC8DC7F051FDB4E05F6AE82DCADC0EB9F5BC7E1FBB1DF23632A5FEBF5C137F6F844BE61000D3EC75F0C3CC9049EA09BE085C0198D5F656D7902545397109FF81D3620F34707FB091D7445E090
	FE69A067B9E1759CEF337612BA365FB3F30B59FECFB4680B3761FB704F034E66265898CD268D472C64B246DD0ECD0B3195365075083D5F3BC46700E2113D5F4B6778980AAF576159FB3FE2895F536F023AB891F369D7685A399C4AC57D086B2638F2A86F62386F3AB067BE500F364F68535BBE9D70897D1A3D297D61EC7F8E35E5C1BF33F19D477F18621769704C760394CF4A25F6305D0EB6ED60B695BB90D7CE77FCFA209C45F11903C89747F139121E0E6959D606E74916G2E4C09B1D75393347FC7DE7C5657
	1388EA325A5BE8843339FA202CD0656FF462DC9DCFE5CE07F2A240FCA71EDF8C3C09676812E75FA3348E1A1FDFDC5CF9B1AF53495AF139D3BB035165FEA20F69FCE6B36A2C9D4830C88384D77206BE66C81F7FF2A5FB5EE275C7036A49FCF9DBD0FDA0281E587B4D207A7B036AC99B2D4AE3754C379F6FC05FD67CB8C5103E999D76FB64548166D316FA0DDDF27B4A34CD977219EFBF5902EBDF71A0366723742CDEAB14FBG820358C77F519F66FCA99D31CC5EADEE1497BB08F1B00D465082A8E7F3DC1E0C71A2
	C8986B5192B973514E4BB753B2B9774C4A2FC9E64FFA610048196C735EFA613DC969B0BEE7CE04FE03305325BFD797AD51B39FAE0CB10F04F36931C1FA868692008592798E7EC49E6979AA979DEF2E16F1742CD4A1DF05A260A130DD45173B5878A7407AE3B64D0194404738783CE5A738E4A827F15CC6B7765B9297764B0392993B356E8CCBBB008DE08CA09EA099A05D0DF4BF25F4D9D0DE6EE66718CB658CCB1D4C4EC81333140C2ED9722872089C62CF3CBB38AFED6E48FD1BECC253EE02CBD7F16ADE8655CD
	5A8C3F77B2FE5335FA827DEC76BD17599E44BA754A7A74ED5E27DDF2281DE893762984B4D1G2F2A3263E42DCFD97C6C9BA4E72C9D3257B9AA3B952B54A8B96DEEF21EDAE0B177B15F3BCFGDEE6433303E84302B0B62C525910ED30E1194E86561E15E5D3BB1B707E947964C97331F5F1E0B31973D6686CE7F89F4597FAB4BCDB476E626BD8895485BC78FEFED7BE19A333213C9C2078FC062592209540F69E5F79DCFD81D252715F79F44A9EFAEAD2375D417CFBF673E47DE36B45032BF13DC87D5F778B732EED
	89FF2E7D3CBA6654EB6553372FA4D7665FC385443F85D0378420501BE1A9872885E87322FFBF3A201E37B9E2F3AA0D0AE26DF350D37C7C300DD39C5650A441E43EE5EC517B568372942F9603B15EF0394E87743C76AC2FB1CF7B1862133D46BC6D71737C2CAC315F07734E2DC167F622FDEABB2857E41F79F858A05C618A4D333F464FF72479D81E1D855725BEB64F313176B79947DA0192AA9B638D47DBA950D4A9EC2C11F203E29C6F7FD2C3720CC6BCB7CDCF1C744A81DE7F290B486A37CABE6DECF942307EF7
	CD6A56DCED7A0DA201B655469E753A6255BBD6B2990F1C514BA83EC34ADB99C6C63C4A1B28E8F95696EA9B0CB1C17B47563BF7C93728F67E2A8863376CAB778AADBFEF495B9EC6C716AA3B522F69D862AB4FD74F1516133E6A153D7421177555A37C1DF28F7084G9281667959FE363AE65BC37B38410EFC0B7C2C0FF3603AC265E3FDDC6D46BEAE99B20F9F7DBADD89B48706347934FD4858474BBEA4FCA71CEEFA34921F15D85DED01C2D6C7318C77A4BDB78BB87E9E1D61D784289CEB40C44ED3E3052CCEA767
	1922A206B348D93715F4927B4228C6BDEFDD68B2BE0F1176689E42673527864C6ABD3CBEFA502CBE1A577FC4B22B0F9F42777FE778DEE5B260C263EB596170B1F79BC5B15F9C03E4BE53253A555AE9046A36A3C7BB557D23B2FE5EE0C998FDCC768FE9A82B67EAB6EB77A33A7B443249641F1385F59F660A6AE0EB405F73436FEBB5641B6FEB9D4DB75F7FEF4B9F7FDDC3E7FE78F7A2E772755F0B33B56CD587091F32E148BD813A81BCG514338FE7DAEFF1BC057AF13356B084DCE7655482F43A828FFE1C1F8DF
	9FAD087C1BB25985E4DF24D7D69459C564A37E5785243F9459C3116AF77885240D3CCE3BC3E124AA7E8F85A45EA4311F22D92E7E3BAD1853A78CE37EFE24825F7525F35C77F6E0CE1FB30CEB6189AFF976288BE079D3DA6E6265274E070BA142730F095365E1F5CC76394B8C186F8F506719B130B9B6006DB164A138D4A827F35CAFBD58C60B82381771388BF34072118CCB57884EBFDB5D78AC97BB024F549F51EF7196C0F9B140F2CEFBDC463923F8F432F355DF1C820F69B983243E95E458C6B10EAF2DA23F87
	F15ABA20E618A49D133DB41281596E15BD22D799E1D87A4539AA6B1EC10147D57A2A9D61E440E8B88EF363C73D83709C5BE935F3DBFEA211B5AF9A6CC8GC8874886D886D0F8A143D286308B2085209BC0810881188510821086108910F3817D702E5EB90E9F6AA6919D14DE2B138CCB7CDD2BBE4C73D6002C5AB15C47B89BA0EDEA787E315329C8222D09A686CD22552D4E6D07ED381FA10DE1DF1E6BA7F29321BC9FA0F38C6341E147F8D88A380A0B5847A9F60C076E0BA863C9CA9B8765E400F4CEFB4E0D7110
	FDF13271B057CD6ED53B799AD8F289E53F461F775AB86EBB9C974DF1851CF77EA59C6FBDC1FB283900AF3CBC7EB8D74F8F873DAFA9831EDD9178D81FEF9E6777C6AA9C116754F79D747BE1188BBBC7BC6AFB0B5D8E1243497D7DBE114F5D7B9C459CF74C57AE7B1C64FD874B6FB616E3FE57F21965055DF075G10338E0C46DD01B4B117D98E18G57D4150665000F6D409CF021096F7986D88CB4391735BC3FB04A18838EBBC87FF7683F75E189183AE79E63B56E1993DBCAC0CEE5946633696E50BC7817EE64EB
	G3AAE00E80078A8F62603715D64895E13DAC816EFF57F624F9E74B3A5AA7C738C6EA12483CDD69473130E19A8635E41D3A17B7693DBB77EB0C53EB7A77D5D86FB4EFC084E6D4B230CFB878BA93E60AA635E41A77C3B6CE228D32E42B176D150F79CD900DFFA55C446CA72G396779911B4A71BBFA1AEB736F5268A7766C2BD4757D3594FE1C65C81147591FA4735C65F9A9B4F7D9A11967AEF9AEFD6E42F02B69332A9D6676AECED744F11FF15CFA7A6DBF41FD6A34A98E0E5FC8FFAB0060DBC522104FABAEE257
	B045F561999E9D6EB3957796FD2687179D3297F92BDCA4767B1D12444A3BA80D538DD387EF0F3D54C627472A333B1E63BE633176573EC831A6DCEDFAD65296094FF88612713584711569E46F3ABAB446DAG47E278D13A56F5DDED0C6DEC0A37DDED0CED319FE35B83F519D7E3EC3B6DD39B13E3D3647B66247918DFD9B4272B1BE67477D7945FB24D686F923E6637C3DD6AB4745796B41714C643DA90BD11753245F46EEAD0F9BBCDE4286754CD5F2DBBD47A3130FCE926FC31AAFDE2D8BE0B291D73D47A05A6FC
	AC0F9E26E3FDF1B4EBC7069BE36FC703F0F5D2A85DC1C9CF475A7FBF4BB147AB39A64352F68D66B08737E25E96FD8D66DCDF225F0B4E07F2A64052EBB0EF3B35827B30605A8C4B01EBD14613951837A957220C229DF4CF914A69G591CF6751B18B365DEBB513C8D77858A484F7929F95B1B8A4A293A8EF54EBFCB479A1447G622EC33B73E574BD99F0CB2E437C7656D37CF7B9347C364D6996F559AD6A7BD0465F90AE0BC1FD8FD1391DD016G22E350476D675136D9B1E6BE325F49B34FCD5FA20F5FEA6CBC6C
	566EB3F477E75CD7FE6FC33922BAD81C764B2131BBA86BE3171DBDCC5BC05E93B95D92F4CDB37B75D8CEFF06BEF7E4C2BB674460B368C3A24195DD8FF3497508DBCAF18DD06E3C9E671E477B35B67D356EFE307F4A37EF74BFC14713AE4D275F6A6EFFEF3F723A739DD7BEFEAE38C363A74A9DBAC732FD4E9E68F78F0F07B4FEDEE30C63F68F62D913059CF79BBF2F1145F1BF27384A9BB2ACF5B7A06E0004F4AD9CD74E4F2E44F15CD5DCEF428DB88FD95FAC92F86EC953B306AF8B3C2C61B1BDD35B7705722965
	C5162DD34B0B1A0278165D2049AE38B1F48DAB3D91732297691AD1F523B1CFD8C771F5B79A7304786D384634C0DD520DB8D6E7051C591FD89C5EBFC53E27A74D477C0A226BDE51F4233F57D3FC69F4233F2F6CC0FFAB20AEE6BA7ADB7426DE7FD2408BB7CDA4AFFAA7643B57929DDF25098CE65F2EF3440E83B7B17BDE6278C3945F2D43B33B3FCF57DAD29E033A45B7215D16085F581B47654608FC66F159BEAEDF4B4D617C5DC7FFEF3D6BE6E3BFB5D03C6DE6E3BFBD4CFF5B4B83F519B7233F15C15F78574D00
	5CEA46787D447687BCDBC3779522ED7C7D9A4888GA4832429725433425B486FACA9E4936E385F27B87B1DFD6C676054338A4FEF43BCA5EB866EC554ECDF05BC826699BD643500AA536B8A5E2B18C62699F52F62677C2CC5E12C791E4CEAC15B1309521DF3A0EDD5962B35A313BD5B2F497EAABFC3D1E9A21B70D8CCE4637B4C8331467D1E678C67009E9E52F4F5C8282BB50C9F79A63A500F6E90BF168EEB3263F99BB94278314AC4B67AB126134D56084F36E2CE93930B6B468F360566B42FEF471CA6B9164F9B
	95829F9B2F6A6E6F5AFE65577CEF7D93F4DC3BC373E9066E3EF4471557DDF3C5F530BE5D7EE6E8CED379E6E8CE53C41FFF66C13FAF64B172209FF30BE5BC9E5EBFC776359AEE4930F48384GE28112EF417A96DA3FG4AB9G05DF4A30D48234FE896B971C557BCCF849B5F00B799CC764B6986A3A4350EA6D7A2BA09D96DD1D998FD9F3A6735D9AEB9BBBFD2EF000DFD15C37039EADFE0E6A6E9F112FFCAC65DCD19D4C674367C3E32965FC685EC0328D1F0F6320ED96FE897352D565E400994F31D633C52B3E7E
	08526740B7C182BEA7675AF0AF20604B781C3CCD24398514BB8124AF633A34F6BB1920C2C5D76B5FB1518384CCF6A1FF67938DB258BC3C0E7C243F2A7BA97E0EE6BE2FFF7502D69F4D4F9F64727A3AB12D7EE17EBE27AA8E6B4F10BF95202E4F57ED45755996475667B13846463178E67417AE89822EC71FA921E7E1BC8A7FB6A00E3D87C903EBA6GF9E74C5613ACBF2EA7CB6346FFBFC0C01819E1A919497EEE947DFD381946F571972D645DCDC7F313A84943A71C6E633A73AAFFEDE1F50D3D7210F8429A5057
	4DD877922DBB59F8D2BD784270AF533D700E66E312A45A2DD2D0DD7FC6D6F7D2569F18E167D1C64E994F23901FEBE762F91574DA337AD6DE3F28502C3E1B579F6BB72B77707A1B6CE67551B3B19EAF49E46ED19C4E3EC137C84FD5301C6AD67E6EB9FEA64E9DDF0F383FEC1E6726CDF1DF7A3FA2729901653337523117B45318675ECD7129B30DF9EE2508F9EE9A54D55C4AFF97A4E81FB6867033EE1D4873481220FD539460C91F905F75C16FC33300E76904787ECF50FE2BF09B0C0D5B4667E3FE7F5BD61C83AB
	013E67B67E9BA9DBF1BE0B3F8D67339D5B886DA2A867G8871384F982FE05B15822EBD9EE5ACD6D0C6CCBC4AD8CBFFC7B58D4AD9G4BB82DE588E32D7076493E577ED772A7CD5439AC65DC21403E27E8389DE53FD00B38C40EDBEDC1DC619D080B6AC71C44F169F644AD3A83474B83B271E9B7EF7BD96471C23D3FCFF7DFADDE79757403AB2A032D1D95A67BCDBBCD761BDE988C259324D03A84133D7B7BCC68FEE372AE6077C16F8254F7B99177F64C6724072638A734A2A21F791CF4BB9DBF35F79A6724FBA83E
	61CE631C744EB69CD7EDD017F2A74EC9F7056CF5CCACD6122748771BC973B13F2E26E33DE416515F9BA93EFC16515F92BF7ADB85F53133505F35C13F41158B7842FF19481C784DF3E4EC14816DAE9DFD8B5CF729E5892ED191E49108D3E912F434CC07301768188F78C53A3AA5F03FDCAD975D15E1A93FAB320EDA152675AE230EA28B51518DF80F2EAE9A6E6355F2B2DC53A620A3D325498951F18D5D17DF8178623B353A8A382FD34BEDF06D39BB328EC52509395B2863E13A7FBE8B70A93A3AF4384FD24BCB61
	AAA4C456D12252D4A698F55CCD0EE62A77C93A7B1AEDD7FEAEBE7ECF50916357FC3AC5F77F3A7F4A6B5EF3C5F53079FD47D0681C7F76D0685A70F33A4E9F007E6FCC40FD00C3BD74FD8714950E3B0E62E2209C4FF1CBA9AE914A73B96E1D8E02DB84659C0E4B22BAD6C0397CDF9177EE8F6EB554F25CD91ADFC4C3B91163366DC57B66F15C343DE8DFBA477D106232201C43F1D56F23FD399C17D60776955E03386CDDE8DF9947D55620FD3BB84EDB0876C5F1DC35856D0B6338999634AF01639E2538D4A827F3DC
	5D9A34EF91477DE0G6DDB42F1B78C20FD4BB96E3181342F68DE44BDAB22FD5D9CB7279F6D73F0DC219D6D9B6338B60A0B05F28247FD4D0E76A5F35CD9EEDF9A477D510176E5F25CFEA7BF6B4CF16F59513E3AC4EE339D6DEB6338B6875A5745F13194A7C1F90C637E53017645F05CDEA75AB7ABB138CD09FDA99C778AFFAF174EF1393DE8DF49FD1823BEF70EBC43AA32ACF5BABD642B84B5D7CEA7D3867E1EC68FBD6F68311EA7674158B31F54037B6815DC56DDBD417B4B3FDD294948375273DD563EC13BD776
	13BF9E4064EC3292B9F5A023054BE96A20FF1FC5760A41341F5331E783BA8F277D63B9F236CC7BB611380027AF11EFCD8FBEDF445D07FB88ADBD380710FEDF64BD04AC20D9FA1F76BD0DB05B3807602F9D7F9B499935E8C351EC5CBBD9C337A7D8FD72DEEC4F83335137A7778637673B859A7DA7F46C3781ED97G437D0E0E7DGA9F35C2A9A541BB49BFDBFDC033EE74D0E7CAD9175FD364E77B91A6FCC471D055C37B9685B58AA4D56F9966E5B9C746D7716E05F6EFED1233752F9238DE83B66209FFF24B19EA0
	E50E7B46G77ED8E7A7607816E5B1CD05FD61979B6C767DB12513712FE6EDB927A767D551A2DB5F66EDB927A56E78F76ED76D90D7E97143E8DE83B12500F6B18EF244CF125CE6EDB927AF64A49FDCBB2BFCF9562DB124E3739C65F3A78F9722239685B4367B4DB7D8E6E5BDC7E37839C413EAD6D51685F20B63781ED57DC7463AC4585C8F9EEF0DFD25F6622EFBB793B2E2C39213ED94CFC1B2B736DFE23EF1B7B38EF77236D53587C64750B4C76FC72A7CC54713B3D036CFD713F95C0CFC3D777CAB0DFB1DA2728
	51BEF22EB414D6A2478FBE70DAC7984D561E2DC2705EED87799FBF7971D5B79FB959F96414007B917E9E5C57B8F07FE477B59676EB364D2E1D28DEC67F505E60B6596D50E44D2B9B3FCD621D9A6D233BA237C9D94D645A44D303ED221836897A7F1D814D0E2F97CE4E7FC7AC417EEF15B4D97FBEB03E7F5739B45A2B86A27B7F230149797F865F2FCF9E577F7B5D1A9D6F74CF4E7FD6FB307F459ECD5669887E8F1D57E81FF1C4767FBD67647C1F66C47F170C6B7F6DDE4D8E59BEB97F27BB027D7FEF5DFB7FF71D
	637BAF7AB55A6FCE207FDF9C1C1C7F1F737EAF7B4AF87E478CE9F65476CDD487FBD69B6D516F19F4029E47D7A632E7622F51730D81CF6C047836876DF7A7824F3C897145B875FC8B01E77104784E85694B851E42647179D82EFCCBCF6873652DBD214F1753EBC269EE2989252BB75973BCBC98CAF737B314AE5199CAB7E4A26FC2103C0DB49E3EB8349C82A3798F42AAC5D8C3BE69AF276F1DFCDEE19D8F56E3744B0345C3FB6A731F3EB7BFCFA8B8A63C483E44B8A694104FA90E892B6857964704FCC57684BC02
	611F6F3CF0093D535443143182E295A12F4FE5DA2529FCD1D733C691D6511FAC00EB658927FB27A29440251E962D81DA34866A7F416696E8662EE2DFD10529DDC7DFB01603C565148E5CEBBA81E87AEE319CAC6368C6C03FF2D2D64A9B4BCF7ADD3D2217571CBC29889B54970B65748B9DDEF52CCD91561ED4BBD8E3B885FD3DB6F8E2DAF95F46E945B3767873044223420B7DD6496330C29F533F6E53A63C54CBBBDB74822DA29453BF33769E703EC7F97737C1277894E1130627FFF51155F6F82CFDA210D8ECD6
	6FA015C05A227C0CE069775AFB298230694C51B3201616C87536B668B987799C1A64150A30D1935DCEBE44D4AB7633407BEFG8F7DC75BF98B4525252569F07AE891F8FFED797F0F1B7D66454E4B7A5F55D8FECDDF585F66D818227EF6E20A761B976CDB5CAFB71239256CEB99163A2F617734CFB7103FA99F6E1B5E8E17D51260C9F83051EDA35F77B60DA822762DFDFCA3116FC39A3512555D5DC86C9E50717CBFD0CB87880104036F98A9GGD00AGGD0CB818294G94G88G88GB3FBB0B60104036F98A9
	GGD00AGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD2A9GGGG
**end of data**/
}
}