/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image.gui;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.vcell.util.Range;
import org.vcell.util.gui.ButtonGroupCivilized;

import cbit.image.DisplayAdapterService;
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
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JPanel ivjScalePanel = null;
	private org.vcell.util.Range ivjactiveScaleRange1 = null;
	private org.vcell.util.Range ivjvalueDomain1 = null;
	private JPanel ivjJPanel1 = null;
	private JLabel ivjColorMapJLabel = null;
	private JLabel ivjSCAboveMaxJLabel = null;
	private JLabel ivjSCBelowMinJLabel = null;
	private JLabel ivjSCIllegalJLabel = null;
	private JLabel ivjSCNANJLabel = null;
	private JLabel ivjSCNoRangeJLabel = null;
	private JPanel ivjSpecialColorsJPanel = null;
	private ButtonGroupCivilized ivjColorMapButtonGroup = null;
	private ButtonModel ivjButtonModel1 = null;

	private class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DisplayAdapterServicePanel.this.getAutoScaleCheckbox()) 
				connEtoM11(e);
			if (e.getSource() == DisplayAdapterServicePanel.this.getAutoScaleCheckbox()) 
				connEtoC12(e);
			if (e.getSource() == DisplayAdapterServicePanel.this.getMaxTextField() || e.getSource() == DisplayAdapterServicePanel.this.getMinTextField()) 
				calculateCustomScaleRange();
			if (e.getSource() == DisplayAdapterServicePanel.this.getRdbtnAllTimes() && getRdbtnAllTimes().isSelected())
				getDisplayAdapterService().setAllTimes(getRdbtnAllTimes().isSelected());
			if (e.getSource() == DisplayAdapterServicePanel.this.getRdbtnSingle() && getRdbtnSingle().isSelected())
				getDisplayAdapterService().setAllTimes(getRdbtnAllTimes().isSelected());
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == DisplayAdapterServicePanel.this.getMaxTextField() || e.getSource() == DisplayAdapterServicePanel.this.getMinTextField()) 
				calculateCustomScaleRange();
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == DisplayAdapterServicePanel.this.getAutoScaleCheckbox()) 
				connEtoM3(e);
			if (e.getSource() == DisplayAdapterServicePanel.this.getAutoScaleCheckbox()) 
				connEtoM7(e);
//			if (e.getSource() == DisplayAdapterServicePanel.this.getAutoScaleCheckbox()){
//				getRdbtnSingle().setEnabled(getAutoScaleCheckbox().isSelected());
//				getRdbtnAllTimes().setEnabled(getAutoScaleCheckbox().isSelected() && bEnableAutoAllTimes);
//			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			try {
				if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals("colorModelIDs"))) {
					updateColorModelRadioButtons();
				}
				if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals(DisplayAdapterService.PROP_NAME_AUTOSCALE))) {
					getAutoScaleCheckbox().setSelected(getDisplayAdapterService().getAutoScale());
				}
				if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals("activeColorModelID"))) {
					updateColorMapDisplay();
				}
				if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals("activeColorModelID"))) {
					getColorMapButtonGroup().setSelection(String.valueOf(getDisplayAdapterService().getActiveColorModelID()));
				}
				if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals(DisplayAdapterService.VALUE_DOMAIN_PROP))) { 
					setvalueDomain1(getDisplayAdapterService().getValueDomain());
				}
				if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals("activeScaleRange"))) { 
					setactiveScaleRange1(getDisplayAdapterService().getActiveScaleRange());
				}
				if (evt.getSource() == DisplayAdapterServicePanel.this.getDisplayAdapterService() && (evt.getPropertyName().equals("specialColors"))) { 
					specialColorsChanged();
				}
				if (evt.getSource() == DisplayAdapterServicePanel.this.getColorMapButtonGroup() && (evt.getPropertyName().equals("selection"))) { 
					setButtonModel1(getColorMapButtonGroup().getSelection());
				}
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
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

private boolean bEnableAutoAllTimes = true;
public void enableAutoAllTimes(boolean bEnable){
	if(bEnable == getRdbtnAllTimes().isEnabled() && bEnable == bEnableAutoAllTimes){
		return;
	}
	bEnableAutoAllTimes = bEnable;
//	getRdbtnAllTimes().removeActionListener(ivjEventHandler);
//	getRdbtnSingle().removeActionListener(ivjEventHandler);
//	try{
		if(!bEnableAutoAllTimes && getRdbtnAllTimes().isSelected()){
			getRdbtnSingle().setSelected(true);
		}
		getDisplayAdapterService().setAllTimes(false);
		getRdbtnAllTimes().setEnabled(bEnableAutoAllTimes);
//	}finally{
//		getRdbtnAllTimes().addActionListener(ivjEventHandler);
//		getRdbtnSingle().addActionListener(ivjEventHandler);
//	}
}
public boolean isEnableAutoAllTimes(){
	return bEnableAutoAllTimes;
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
private void connEtoC13(org.vcell.util.Range value) {
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
 * connEtoC2:  (valueDomain1.this --> DisplayAdapterServicePanel.ensureDomainDisplay()V)
 * @param value cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(org.vcell.util.Range value) {
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
 * connEtoC6:  (activeScaleRange1.this --> DisplayAdapterServicePanel.updateColorMapDisplay()V)
 * @param value cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(org.vcell.util.Range value) {
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
private void connEtoC8(org.vcell.util.Range value) {
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
	getDisplayAdapterService().setAllTimes(getRdbtnAllTimes().isSelected());
}


/**
 * Comment
 */
private void ensureDomainDisplay() {
	if(getvalueDomain1() != null){
		getMinRangeJLabel().setText(getvalueDomain1().getMin()+"");
		getMaxRangeJLabel().setText(getvalueDomain1().getMax()+"");
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
		getMinTextField().setText(getactiveScaleRange1().getMin()+"");
		getMaxTextField().setText(getactiveScaleRange1().getMax()+"");
	}else{
		getMinTextField().setText("0");
		getMaxTextField().setText("0");
	}
}

/**
 * Return the activeScaleRange1 property value.
 * @return cbit.image.Range
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.Range getactiveScaleRange1() {
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
			ivjAutoScaleCheckbox.setText("Auto range");
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
			TitledBorder ivjLocalBorder = new TitledBorder("Color");
			ivjLocalBorder.setBorder(new javax.swing.border.EtchedBorder());
			ivjLocalBorder.setTitleJustification(javax.swing.border.TitledBorder.LEFT);
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
			ivjColorGridPanel.add(getSpecialColorsJPanel(), constraintsSpecialColorsJPanel);
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
private org.vcell.util.gui.ButtonGroupCivilized getColorMapButtonGroup() {
	if (ivjColorMapButtonGroup == null) {
		try {
			ivjColorMapButtonGroup = new org.vcell.util.gui.ButtonGroupCivilized();
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
			org.vcell.util.gui.LineBorderBean ivjLocalBorder2;
			ivjLocalBorder2 = new org.vcell.util.gui.LineBorderBean();
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
			ivjJPanel1.setBorder(new LineBorder(Color.black, 1));

			java.awt.GridBagConstraints constraintsAutoScaleCheckbox = new java.awt.GridBagConstraints();
			constraintsAutoScaleCheckbox.gridwidth = 2;
			constraintsAutoScaleCheckbox.insets = new Insets(0, 0, 0, 5);
			constraintsAutoScaleCheckbox.gridx = 0; constraintsAutoScaleCheckbox.gridy = 0;
			constraintsAutoScaleCheckbox.weightx = 1.0;
			constraintsAutoScaleCheckbox.anchor = GridBagConstraints.WEST;
			
			ivjJPanel1.add(getAutoScaleCheckbox(), constraintsAutoScaleCheckbox);
			
			GridBagConstraints gbc_rdbtnSingle = new GridBagConstraints();
			gbc_rdbtnSingle.weightx = 1.0;
			gbc_rdbtnSingle.insets = new Insets(0, 0, 0, 5);
			gbc_rdbtnSingle.gridx = 0;
			gbc_rdbtnSingle.gridy = 1;
			gbc_rdbtnSingle.anchor = GridBagConstraints.WEST;
			ivjJPanel1.add(getRdbtnSingle(), gbc_rdbtnSingle);
			GridBagConstraints gbc_rdbtnAllTimes = new GridBagConstraints();
			gbc_rdbtnAllTimes.gridx = 1;
			gbc_rdbtnAllTimes.gridy = 1;
			gbc_rdbtnAllTimes.weightx = 1.0;
			gbc_rdbtnAllTimes.anchor = GridBagConstraints.WEST;
			ivjJPanel1.add(getRdbtnAllTimes(), gbc_rdbtnAllTimes);
			
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
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new org.vcell.util.gui.TitledBorderBean();
			ivjLocalBorder1.setBorder(new javax.swing.border.EtchedBorder());
			ivjLocalBorder1.setTitleJustification(javax.swing.border.TitledBorder.LEFT);
			ivjLocalBorder1.setTitle("Data Range (Min-Max)");
			ivjScalePanel = new javax.swing.JPanel();
			ivjScalePanel.setName("ScalePanel");
			ivjScalePanel.setBorder(ivjLocalBorder1);
			final java.awt.GridBagLayout gridBagLayout = new java.awt.GridBagLayout();
			gridBagLayout.columnWidths = new int[] {7,0};
			gridBagLayout.rowHeights = new int[] {0,7,0,0,7,0,0};
			ivjScalePanel.setLayout(gridBagLayout);

			java.awt.GridBagConstraints constraintsMinLabel = new java.awt.GridBagConstraints();
			constraintsMinLabel.anchor = GridBagConstraints.EAST;
			constraintsMinLabel.gridx = 0; constraintsMinLabel.gridy = 3;
			constraintsMinLabel.insets = new java.awt.Insets(4, 4, 0, 0);
			getScalePanel().add(getMinLabel(), constraintsMinLabel);

			java.awt.GridBagConstraints constraintsMinRangeJLabel = new java.awt.GridBagConstraints();
			constraintsMinRangeJLabel.gridx = 1; constraintsMinRangeJLabel.gridy = 3;
			constraintsMinRangeJLabel.insets = new java.awt.Insets(4, 0, 4, 0);
			getScalePanel().add(getMinRangeJLabel(), constraintsMinRangeJLabel);

			java.awt.GridBagConstraints constraintsMinTextField = new java.awt.GridBagConstraints();
			constraintsMinTextField.gridwidth = 2;
			constraintsMinTextField.gridx = 0; constraintsMinTextField.gridy = 4;
			constraintsMinTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMinTextField.weightx = 1.0;
			constraintsMinTextField.insets = new java.awt.Insets(0, 4, 4, 4);
			getScalePanel().add(getMinTextField(), constraintsMinTextField);

			java.awt.GridBagConstraints constraintsMaxLabel = new java.awt.GridBagConstraints();
			constraintsMaxLabel.gridx = 0; constraintsMaxLabel.gridy = 1;
			constraintsMaxLabel.insets = new java.awt.Insets(4, 4, 0, 0);
			getScalePanel().add(getMaxLabel(), constraintsMaxLabel);
			
			java.awt.GridBagConstraints constraintsMaxRangeJLabel = new java.awt.GridBagConstraints();
			constraintsMaxRangeJLabel.gridx = 1; constraintsMaxRangeJLabel.gridy = 1;
			constraintsMaxRangeJLabel.insets = new java.awt.Insets(4, 0, 4, 0);
			getScalePanel().add(getMaxRangeJLabel(), constraintsMaxRangeJLabel);

			java.awt.GridBagConstraints constraintsMaxTextField = new java.awt.GridBagConstraints();
			constraintsMaxTextField.gridwidth = 2;
			constraintsMaxTextField.gridx = 0; constraintsMaxTextField.gridy = 2;
			constraintsMaxTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMaxTextField.weightx = 1.0;
			constraintsMaxTextField.insets = new java.awt.Insets(0, 4, 4, 4);
			getScalePanel().add(getMaxTextField(), constraintsMaxTextField);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridwidth = 2;
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.insets = new Insets(4, 4, 4, 4);
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
			ivjSCIllegalJLabel.setToolTipText("Not In Domain Color");
			ivjSCIllegalJLabel.setOpaque(true);
			ivjSCIllegalJLabel.setText("ND");
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
private org.vcell.util.Range getvalueDomain1() {
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

JPopupMenu specialColorPopup;
private MouseAdapter mouseAdapter = new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		if(e.isPopupTrigger()){
			specialColorPopup.show((JLabel)e.getSource(), e.getX(), e.getY());
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if(e.isPopupTrigger()){
			specialColorPopup.show((JLabel)e.getSource(), e.getX(), e.getY());
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		if(e.isPopupTrigger()){
			specialColorPopup.show((JLabel)e.getSource(), e.getX(), e.getY());
		}
	}
	
};


private void changeSpecialColor(ActionEvent e){
	
	if(e.getActionCommand().equals("DF")){
		if(Arrays.equals(getDisplayAdapterService().getActiveColorModel(), DisplayAdapterService.createBlueRedColorModel())){
			getDisplayAdapterService().addColorModelForValues(
					getDisplayAdapterService().getActiveColorModel(), DisplayAdapterService.createBlueRedSpecialColors(), getDisplayAdapterService().getActiveColorModelID());
		}else{
			getDisplayAdapterService().addColorModelForValues(
					getDisplayAdapterService().getActiveColorModel(), DisplayAdapterService.createGraySpecialColors(), getDisplayAdapterService().getActiveColorModelID());
		}
		return;
	}
	String activeColorModelID = getDisplayAdapterService().getActiveColorModelID();
	int[] activecolorModelArr = getDisplayAdapterService().getActiveColorModel();
	int[] specialColorsArr = getDisplayAdapterService().getSpecialColors().clone();

	if(e.getActionCommand().equals("GS")){
		for (int i = 0; i < specialColorsArr.length; i++) {
			specialColorsArr[i] = Color.black.getRGB();
		}
		specialColorsArr[DisplayAdapterService.ABOVE_MAX_COLOR_OFFSET] = Color.white.getRGB();
		specialColorsArr[DisplayAdapterService.FOREGROUND_NONHIGHLIGHT_COLOR_OFFSET] = Color.gray.getRGB();
		getDisplayAdapterService().addColorModelForValues(activecolorModelArr, specialColorsArr, activeColorModelID);
	}else{
		Color newSpecialColor = JColorChooser.showDialog(this, "Choose Special Color", Color.black);
		if(newSpecialColor != null){
			if(e.getActionCommand().equals("BM")){
				specialColorsArr[DisplayAdapterService.BELOW_MIN_COLOR_OFFSET] = newSpecialColor.getRGB();
			}else if(e.getActionCommand().equals("AM")){
				specialColorsArr[DisplayAdapterService.ABOVE_MAX_COLOR_OFFSET] = newSpecialColor.getRGB();
			}else if(e.getActionCommand().equals("NN")){
				specialColorsArr[DisplayAdapterService.NAN_COLOR_OFFSET] = newSpecialColor.getRGB();
			}else if(e.getActionCommand().equals("ND")){
				specialColorsArr[DisplayAdapterService.NOT_IN_DOMAIN_COLOR_OFFSET] = newSpecialColor.getRGB();
			}else if(e.getActionCommand().equals("NR")){
				specialColorsArr[DisplayAdapterService.NO_RANGE_COLOR_OFFSET] = newSpecialColor.getRGB();
			}else if(e.getActionCommand().equals("FH")){
				specialColorsArr[DisplayAdapterService.FOREGROUND_HIGHLIGHT_COLOR_OFFSET] = newSpecialColor.getRGB();
			}else if(e.getActionCommand().equals("FN")){
				specialColorsArr[DisplayAdapterService.FOREGROUND_NONHIGHLIGHT_COLOR_OFFSET] = newSpecialColor.getRGB();
			}else if(e.getActionCommand().equals("MB")){
				specialColorsArr[DisplayAdapterService.NULL_COLOR_OFFSET] = newSpecialColor.getRGB();
			}
			getDisplayAdapterService().addColorModelForValues(activecolorModelArr, specialColorsArr, activeColorModelID);
		}
	}
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	ButtonGroup timeRangeButtonGroup = new ButtonGroup();
	timeRangeButtonGroup.add(getRdbtnSingle());
	timeRangeButtonGroup.add(getRdbtnAllTimes());
	getRdbtnSingle().setSelected(true);
	getRdbtnAllTimes().addActionListener(ivjEventHandler);
	getRdbtnSingle().addActionListener(ivjEventHandler);
	
	getDisplayAdapterService().addPropertyChangeListener(ivjEventHandler);
	getAutoScaleCheckbox().addActionListener(ivjEventHandler);
	getColorMapButtonGroup().addPropertyChangeListener(ivjEventHandler);
	getAutoScaleCheckbox().addItemListener(ivjEventHandler);
	getMaxTextField().addActionListener(ivjEventHandler);
	getMaxTextField().addFocusListener(ivjEventHandler);
	getMinTextField().addActionListener(ivjEventHandler);
	getMinTextField().addFocusListener(ivjEventHandler);
	
	specialColorPopup = new JPopupMenu();
	JMenuItem changeSpecialColorMenuItem0 = new JMenuItem("Change 'Below Min' Color...");
	changeSpecialColorMenuItem0.setActionCommand("BM");
	changeSpecialColorMenuItem0.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			changeSpecialColor(e);
		}
	});
	JMenuItem changeSpecialColorMenuItem1 = new JMenuItem("Change 'Above Max' Color...");
	changeSpecialColorMenuItem1.setActionCommand("AM");
	changeSpecialColorMenuItem1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			changeSpecialColor(e);
		}
	});
	JMenuItem changeSpecialColorMenuItem2 = new JMenuItem("Change 'NAN' Color...");
	changeSpecialColorMenuItem2.setActionCommand("NN");
	changeSpecialColorMenuItem2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			changeSpecialColor(e);
		}
	});
	JMenuItem changeSpecialColorMenuItem3 = new JMenuItem("Change 'Not In Domain' Color...");
	changeSpecialColorMenuItem3.setActionCommand("ND");
	changeSpecialColorMenuItem3.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			changeSpecialColor(e);
		}
	});
	JMenuItem changeSpecialColorMenuItem4 = new JMenuItem("Change 'No Range' Color...");
	changeSpecialColorMenuItem4.setActionCommand("NR");
	changeSpecialColorMenuItem4.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			changeSpecialColor(e);
		}
	});
	JMenuItem changeSpecialColorMenuItem5 = new JMenuItem("Change 'Foreground Hilite' Color...");
	changeSpecialColorMenuItem5.setActionCommand("FH");
	changeSpecialColorMenuItem5.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			changeSpecialColor(e);
		}
	});
	JMenuItem changeSpecialColorMenuItem6 = new JMenuItem("Change 'Foreground NonHilite' Color...");
	changeSpecialColorMenuItem6.setActionCommand("FN");
	changeSpecialColorMenuItem6.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			changeSpecialColor(e);
		}
	});
	JMenuItem changeSpecialColorMenuItem7 = new JMenuItem("Change 'Membrane Background' Color...");
	changeSpecialColorMenuItem7.setActionCommand("MB");
	changeSpecialColorMenuItem7.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			changeSpecialColor(e);
		}
	});
	JMenuItem changeSpecialColorMenuItemReset = new JMenuItem("Default Colors");
	changeSpecialColorMenuItemReset.setActionCommand("DF");
	changeSpecialColorMenuItemReset.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			changeSpecialColor(e);
		}
	});
	JMenuItem changeSpecialColorMenuItemGray = new JMenuItem("GrayScale Colors");
	changeSpecialColorMenuItemGray.setActionCommand("GS");
	changeSpecialColorMenuItemGray.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			changeSpecialColor(e);
		}
	});

	specialColorPopup.add(changeSpecialColorMenuItemReset);
	specialColorPopup.add(changeSpecialColorMenuItemGray);
	specialColorPopup.add(changeSpecialColorMenuItem0);
	specialColorPopup.add(changeSpecialColorMenuItem1);
	specialColorPopup.add(changeSpecialColorMenuItem2);
	specialColorPopup.add(changeSpecialColorMenuItem3);
	specialColorPopup.add(changeSpecialColorMenuItem4);
	specialColorPopup.add(changeSpecialColorMenuItem5);
	specialColorPopup.add(changeSpecialColorMenuItem6);
	specialColorPopup.add(changeSpecialColorMenuItem7);
	
	getSCBelowMinJLabel().addMouseListener(mouseAdapter);
	getSCAboveMaxJLabel().addMouseListener(mouseAdapter);
	getSCNANJLabel().addMouseListener(mouseAdapter);
	getSCIllegalJLabel().addMouseListener(mouseAdapter);
	getSCNoRangeJLabel().addMouseListener(mouseAdapter);

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
//		setSize(222, 217);

		java.awt.GridBagConstraints constraintsColorGridPanel = new java.awt.GridBagConstraints();
		constraintsColorGridPanel.gridx = 1; constraintsColorGridPanel.gridy = 1;
		constraintsColorGridPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsColorGridPanel.weightx = 1.0;
//		constraintsColorGridPanel.weighty = 1.0;
		constraintsColorGridPanel.insets = new java.awt.Insets(0, 0, 4, 4);
		add(getColorGridPanel(), constraintsColorGridPanel);

		java.awt.GridBagConstraints constraintsScalePanel = new java.awt.GridBagConstraints();
		constraintsScalePanel.gridx = 1; constraintsScalePanel.gridy = 0;
		constraintsScalePanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsScalePanel.weightx = 1.0;
//		constraintsScalePanel.weighty = 1.0;
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

private JRadioButton rdbtnSingle;
private JRadioButton rdbtnAllTimes;

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
		frame.setVisible(true);
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
private void setactiveScaleRange1(org.vcell.util.Range newValue) {
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
private void setvalueDomain1(org.vcell.util.Range newValue) {
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
		for(int i=0;i<cmapLength;i+=1){
			double value = getvalueDomain1().getMin()+(getvalueDomain1().getMax() - getvalueDomain1().getMin())*i/(double)(cmapLength-1);
			int colorFromValue = getDisplayAdapterService().getColorFromValue(value);
			if(i == (cmapLength-1)){
				colorFromValue = getDisplayAdapterService().getColorFromValue(getvalueDomain1().getMax());
			}
			java.util.Arrays.fill(row,colorFromValue);
			bufferedCmap.setRGB(0,cmapLength-1-i,cmapWidth,1,row,0,cmapWidth);
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
				case DisplayAdapterService.NOT_IN_DOMAIN_COLOR_OFFSET:
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
	java.util.Enumeration<AbstractButton> buttons = getColorMapButtonGroup().getElements();
	while (buttons.hasMoreElements()) {
		AbstractButton button = buttons.nextElement();
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

public void setDisplayAdapterService(DisplayAdapterService newValue) {
	if (ivjDisplayAdapterService == newValue) {
		return;
	}
	DisplayAdapterService oldValue = ivjDisplayAdapterService;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(ivjEventHandler);
	}
	ivjDisplayAdapterService = newValue;
	if (newValue != null) {
		newValue.addPropertyChangeListener(ivjEventHandler);
		updateColorModelRadioButtons();
		getAutoScaleCheckbox().setSelected(getDisplayAdapterService().getAutoScale());
		updateColorMapDisplay();
		getColorMapButtonGroup().setSelection(String.valueOf(getDisplayAdapterService().getActiveColorModelID()));
		setvalueDomain1(getDisplayAdapterService().getValueDomain());
		setactiveScaleRange1(getDisplayAdapterService().getActiveScaleRange());
		specialColorsChanged();
	}
}

private static final String SINGLE_TIMES_TEXT = "at time";
public static final String ALL_TIMES__STATE_TEXT = "all times";
public static final String ALL_TIMES__APPROX_TEXT = "all times (approx)";
	private JRadioButton getRdbtnSingle() {
		if (rdbtnSingle == null) {
			rdbtnSingle = new JRadioButton(SINGLE_TIMES_TEXT);
		}
		return rdbtnSingle;
	}
	private JRadioButton getRdbtnAllTimes() {
		if (rdbtnAllTimes == null) {
			rdbtnAllTimes = new JRadioButton(ALL_TIMES__STATE_TEXT);
		}
		return rdbtnAllTimes;
	}
	public void changeAllTimesButtonText(String newText){
		getRdbtnAllTimes().setText(newText);
	}
}
