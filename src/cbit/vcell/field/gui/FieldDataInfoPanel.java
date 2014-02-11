/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.field.gui;

import javax.swing.JPanel;

import cbit.vcell.client.PopupGenerator;

import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridBagLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.Font;
import javax.swing.BorderFactory;

import org.vcell.util.BeanUtils;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;

import java.awt.Color;

public class FieldDataInfoPanel extends JPanel {
	//
	private boolean bsimMode = false;
	private double[] dataTimes;
	//
	private javax.swing.JLabel ivjAnnotationJLabel = null;
	private javax.swing.JTextArea ivjAnnotationJTextArea = null;
	private javax.swing.JLabel ivjJLabel = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JLabel ivjJLabel4 = null;
	private javax.swing.JPanel ivjJPanel = null;
	private javax.swing.JPanel jPanelOrigin = null;
	private javax.swing.JPanel jPanelSize = null;
	private javax.swing.JPanel ivjJPanel5 = null;
	private javax.swing.JPanel ivjJPanel6 = null;
	private javax.swing.JTextField ivjJTextFieldFieldName = null;
	private javax.swing.JLabel ivjMicronJLabel = null;
	private javax.swing.JLabel ivjMicronJLabel1 = null;
	private javax.swing.JLabel ivjPixelSizeJLabel = null;
	private javax.swing.JLabel ivjPixelSizeXJLabel = null;
	private javax.swing.JLabel ivjPixelSizeYJLabel = null;
	private javax.swing.JLabel ivjPixelSizeZJLabel = null;
	private javax.swing.JLabel ivjXMicronJLabel = null;
	private javax.swing.JLabel ivjXMicronJLabel1 = null;
	private javax.swing.JTextField ivjXMicronJTextField = null;
	private javax.swing.JLabel ivjYMicronJLabel = null;
	private javax.swing.JLabel ivjYMicronJLabel1 = null;
	private javax.swing.JTextField ivjYMicronJTextField = null;
	private javax.swing.JLabel ivjZMicronJLabel = null;
	private javax.swing.JLabel ivjZMicronJLabel1 = null;
	private javax.swing.JTextField ivjZMicronJTextField = null;
	private javax.swing.JTextField ivjXOriginJTextField1 = null;
	private javax.swing.JTextField ivjYOriginJTextField1 = null;
	private javax.swing.JTextField ivjZOriginJTextField1 = null;
	private JLabel jLabel1Time = null;
	private JPanel jPanel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabelTimeCount = null;
	private JLabel jLabel21 = null;
	private JLabel jLabeTimeBeg = null;
	private JLabel jLabel23 = null;
	private JLabel jLabelTimeEnd = null;
	private JPanel jPanel1 = null;
	private JComboBox jComboBoxVarNames = null;
	private JButton jButtonVarNameEdit = null;
	private JButton jButtonEditTimes = null;
	private JPanel jPanel2 = null;
	private JButton jButtonSeqTimes = null;
	/**
 * FieldDataInfoPanel constructor comment.
 */
public FieldDataInfoPanel() {
	super();
	initialize();
}

/**
 * FieldDataInfoPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public FieldDataInfoPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * FieldDataInfoPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public FieldDataInfoPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * FieldDataInfoPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public FieldDataInfoPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * Return the AnnotationJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAnnotationJLabel() {
	if (ivjAnnotationJLabel == null) {
		try {
			ivjAnnotationJLabel = new javax.swing.JLabel();
			ivjAnnotationJLabel.setName("AnnotationJLabel");
			ivjAnnotationJLabel.setText("Field Data Annotation");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnnotationJLabel;
}


/**
 * Return the AnnotationJTextArea property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getAnnotationJTextArea() {
	if (ivjAnnotationJTextArea == null) {
		try {
			ivjAnnotationJTextArea = new javax.swing.JTextArea();
			ivjAnnotationJTextArea.setName("AnnotationJTextArea");
			ivjAnnotationJTextArea.setRows(3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnnotationJTextArea;
}

public String getAnnotation(){
	return getAnnotationJTextArea().getText();
}
public void setAnnotation(String newAnnotation){
	getAnnotationJTextArea().setText(newAnnotation);
	}


public Extent getExtent(){
	return 	new org.vcell.util.Extent(
			Double.parseDouble(getXMicronJTextField().getText()),
			Double.parseDouble(getYMicronJTextField().getText()),
			Double.parseDouble(getZMicronJTextField().getText())
			);

}

public String getFieldName(){
	return getJTextFieldFieldName().getText();
}

public String[] getVariableNames(){
	String[] varNames = new String[jComboBoxVarNames.getModel().getSize()];
	for(int i=0;i<varNames.length;i+= 1){
		varNames[i] = (String)jComboBoxVarNames.getItemAt(i);
	}
	return varNames;
}
/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel() {
	if (ivjJLabel == null) {
		try {
			ivjJLabel = new javax.swing.JLabel();
			ivjJLabel.setName("JLabel");
			ivjJLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			ivjJLabel.setText("X:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setFont(new Font("Dialog", Font.BOLD, 14));
			ivjJLabel1.setText("Y:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}


/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setFont(new Font("Dialog", Font.BOLD, 14));
			ivjJLabel2.setText("Z:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}


/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setFont(new Font("Dialog", Font.PLAIN, 14));
			ivjJLabel3.setText("Field name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}


/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setFont(new Font("Dialog", Font.PLAIN, 14));
			ivjJLabel4.setText("Variable names:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}


/**
 * Return the JPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel() {
	if (ivjJPanel == null) {
		try {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 2;
			gridBagConstraints12.gridy = 2;
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints22.gridy = 1;
			gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints22.weightx = 0.0;
			gridBagConstraints22.gridx = 2;
			GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
			gridBagConstraints71.gridx = 1;
			gridBagConstraints71.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints71.weightx = 1.0;
			gridBagConstraints71.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints71.gridy = 2;
			GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
			gridBagConstraints61.gridx = 1;
			gridBagConstraints61.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints61.weightx = 1.0;
			gridBagConstraints61.gridy = 1;
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.gridx = 1;
			gridBagConstraints51.gridy = 2;
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.gridx = 0;
			gridBagConstraints41.anchor = GridBagConstraints.EAST;
			gridBagConstraints41.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints41.gridy = 2;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 1;
			gridBagConstraints31.gridy = 2;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.anchor = GridBagConstraints.EAST;
			gridBagConstraints21.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints21.gridy = 2;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 2;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 2;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.gridy = 2;
			jLabel1Time = new JLabel();
			jLabel1Time.setText("Times:");
			jLabel1Time.setFont(new Font("Dialog", Font.PLAIN, 14));
			ivjJPanel = new javax.swing.JPanel();
			ivjJPanel.setName("JPanel");
			ivjJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanel4 = new java.awt.GridBagConstraints();
			constraintsJPanel4.gridx = 1;
 	constraintsJPanel4.gridy = 5;
			constraintsJPanel4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel4.weightx = 1.0;
			constraintsJPanel4.gridwidth = 2;
			constraintsJPanel4.insets = new java.awt.Insets(4, 4, 4, 4);
			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 1;
 	constraintsJPanel1.gridy = 4;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.gridwidth = 2;
			constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			java.awt.GridBagConstraints constraintsJPanel5 = new java.awt.GridBagConstraints();
			constraintsJPanel5.gridx = 1;
 	constraintsJPanel5.gridy = 3;
			constraintsJPanel5.fill = GridBagConstraints.HORIZONTAL;
			constraintsJPanel5.weightx = 1.0;
			constraintsJPanel5.gridwidth = 2;
			constraintsJPanel5.anchor = GridBagConstraints.CENTER;
			constraintsJPanel5.insets = new java.awt.Insets(4, 4, 4, 4);
			java.awt.GridBagConstraints constraintsJPanel6 = new java.awt.GridBagConstraints();
			constraintsJPanel6.gridx = 0;
 	constraintsJPanel6.gridy = 6;
			constraintsJPanel6.gridwidth = 3;
			constraintsJPanel6.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel6.weightx = 1.0;
			constraintsJPanel6.weighty = 1.0;
			constraintsJPanel6.insets = new java.awt.Insets(4, 4, 4, 4);
			java.awt.GridBagConstraints constraintsMicronJLabel1 = new java.awt.GridBagConstraints();
			constraintsMicronJLabel1.gridx = 0;
 	constraintsMicronJLabel1.gridy = 4;
			constraintsMicronJLabel1.anchor = java.awt.GridBagConstraints.EAST;
			constraintsMicronJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			java.awt.GridBagConstraints constraintsMicronJLabel = new java.awt.GridBagConstraints();
			constraintsMicronJLabel.gridx = 0;
 	constraintsMicronJLabel.gridy = 5;
			constraintsMicronJLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsMicronJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjJPanel.add(getJPanelSize(), constraintsJPanel4);
			ivjJPanel.add(getJPanelOrigin(), constraintsJPanel1);
			ivjJPanel.add(getJPanel5(), constraintsJPanel5);
			ivjJPanel.add(getJPanel6(), constraintsJPanel6);
			ivjJPanel.add(getMicronJLabel1(), constraintsMicronJLabel1);
			java.awt.GridBagConstraints constraintsPixelSizeJLabel = new java.awt.GridBagConstraints();
			constraintsPixelSizeJLabel.gridx = 0;
 	constraintsPixelSizeJLabel.gridy = 3;
			constraintsPixelSizeJLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsPixelSizeJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 0;
			constraintsJLabel3.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjJPanel.add(getMicronJLabel(), constraintsMicronJLabel);
			ivjJPanel.add(getPixelSizeJLabel(), constraintsPixelSizeJLabel);
			getJPanel().add(getJLabel3(), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJTextFieldFieldName = new java.awt.GridBagConstraints();
			constraintsJTextFieldFieldName.gridx = 1; constraintsJTextFieldFieldName.gridy = 0;
			constraintsJTextFieldFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldFieldName.weightx = 1.0;
			constraintsJTextFieldFieldName.gridwidth = 2;
			constraintsJTextFieldFieldName.insets = new java.awt.Insets(4, 4, 4, 4);
			java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
			constraintsJLabel4.gridx = 0; constraintsJLabel4.gridy = 1;
			constraintsJLabel4.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabel4.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjJPanel.add(getJTextFieldFieldName(), constraintsJTextFieldFieldName);
			getJPanel().add(getJLabel4(), constraintsJLabel4);

 			ivjJPanel.add(jLabel1Time, gridBagConstraints41);

 			ivjJPanel.add(getJPanel12(), gridBagConstraints61);
 			ivjJPanel.add(getJPanel2(), gridBagConstraints71);
			ivjJPanel.add(getJButtonVarNameEdit(), gridBagConstraints22);
			ivjJPanel.add(getJPanel22(), gridBagConstraints12);
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel;
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOrigin() {
	if (jPanelOrigin == null) {
		try {
			jPanelOrigin = new javax.swing.JPanel();
			jPanelOrigin.setName("JPanel1");
			jPanelOrigin.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsXMicronJLabel1 = new java.awt.GridBagConstraints();
			constraintsXMicronJLabel1.gridx = 0; constraintsXMicronJLabel1.gridy = 0;
			constraintsXMicronJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelOrigin().add(getXMicronJLabel1(), constraintsXMicronJLabel1);

			java.awt.GridBagConstraints constraintsXOriginJTextField1 = new java.awt.GridBagConstraints();
			constraintsXOriginJTextField1.gridx = 1; constraintsXOriginJTextField1.gridy = 0;
			constraintsXOriginJTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsXOriginJTextField1.weightx = 1.0;
			constraintsXOriginJTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelOrigin().add(getXOriginJTextField1(), constraintsXOriginJTextField1);

			java.awt.GridBagConstraints constraintsYMicronJLabel1 = new java.awt.GridBagConstraints();
			constraintsYMicronJLabel1.gridx = 2; constraintsYMicronJLabel1.gridy = 0;
			constraintsYMicronJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelOrigin().add(getYMicronJLabel1(), constraintsYMicronJLabel1);

			java.awt.GridBagConstraints constraintsYOriginJTextField1 = new java.awt.GridBagConstraints();
			constraintsYOriginJTextField1.gridx = 3; constraintsYOriginJTextField1.gridy = 0;
			constraintsYOriginJTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsYOriginJTextField1.weightx = 1.0;
			constraintsYOriginJTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelOrigin().add(getYOriginJTextField1(), constraintsYOriginJTextField1);

			java.awt.GridBagConstraints constraintsZMicronJLabel1 = new java.awt.GridBagConstraints();
			constraintsZMicronJLabel1.gridx = 4; constraintsZMicronJLabel1.gridy = 0;
			constraintsZMicronJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelOrigin().add(getZMicronJLabel1(), constraintsZMicronJLabel1);

			java.awt.GridBagConstraints constraintsZOriginJTextField1 = new java.awt.GridBagConstraints();
			constraintsZOriginJTextField1.gridx = 5; constraintsZOriginJTextField1.gridy = 0;
			constraintsZOriginJTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsZOriginJTextField1.weightx = 1.0;
			constraintsZOriginJTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelOrigin().add(getZOriginJTextField1(), constraintsZOriginJTextField1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return jPanelOrigin;
}

/**
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelSize() {
	if (jPanelSize == null) {
		try {
			jPanelSize = new javax.swing.JPanel();
			jPanelSize.setName("JPanel4");
			jPanelSize.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsXMicronJLabel = new java.awt.GridBagConstraints();
			constraintsXMicronJLabel.gridx = 0; constraintsXMicronJLabel.gridy = 0;
			constraintsXMicronJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSize().add(getXMicronJLabel(), constraintsXMicronJLabel);

			java.awt.GridBagConstraints constraintsXMicronJTextField = new java.awt.GridBagConstraints();
			constraintsXMicronJTextField.gridx = 1; constraintsXMicronJTextField.gridy = 0;
			constraintsXMicronJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsXMicronJTextField.weightx = 1.0;
			constraintsXMicronJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSize().add(getXMicronJTextField(), constraintsXMicronJTextField);

			java.awt.GridBagConstraints constraintsYMicronJLabel = new java.awt.GridBagConstraints();
			constraintsYMicronJLabel.gridx = 2; constraintsYMicronJLabel.gridy = 0;
			constraintsYMicronJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSize().add(getYMicronJLabel(), constraintsYMicronJLabel);

			java.awt.GridBagConstraints constraintsYMicronJTextField = new java.awt.GridBagConstraints();
			constraintsYMicronJTextField.gridx = 3; constraintsYMicronJTextField.gridy = 0;
			constraintsYMicronJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsYMicronJTextField.weightx = 1.0;
			constraintsYMicronJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSize().add(getYMicronJTextField(), constraintsYMicronJTextField);

			java.awt.GridBagConstraints constraintsZMicronJLabel = new java.awt.GridBagConstraints();
			constraintsZMicronJLabel.gridx = 4; constraintsZMicronJLabel.gridy = 0;
			constraintsZMicronJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSize().add(getZMicronJLabel(), constraintsZMicronJLabel);

			java.awt.GridBagConstraints constraintsZMicronJTextField = new java.awt.GridBagConstraints();
			constraintsZMicronJTextField.gridx = 5; constraintsZMicronJTextField.gridy = 0;
			constraintsZMicronJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsZMicronJTextField.weightx = 1.0;
			constraintsZMicronJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSize().add(getZMicronJTextField(), constraintsZMicronJTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return jPanelSize;
}


/**
 * Return the JPanel5 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel5() {
	if (ivjJPanel5 == null) {
		try {
			ivjJPanel5 = new javax.swing.JPanel();
			ivjJPanel5.setName("JPanel5");
			ivjJPanel5.setLayout(new java.awt.GridBagLayout());

			ivjJPanel5.setBorder(BorderFactory.createLineBorder(Color.black, 2));
			java.awt.GridBagConstraints constraintsJLabel = new java.awt.GridBagConstraints();
			constraintsJLabel.gridx = 0; constraintsJLabel.gridy = 0;
			constraintsJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel5().add(getJLabel(), constraintsJLabel);

			java.awt.GridBagConstraints constraintsPixelSizeXJLabel = new java.awt.GridBagConstraints();
			constraintsPixelSizeXJLabel.gridx = 1; constraintsPixelSizeXJLabel.gridy = 0;
			constraintsPixelSizeXJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel5().add(getPixelSizeXJLabel(), constraintsPixelSizeXJLabel);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 2; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.insets = new Insets(4, 20, 4, 4);
			java.awt.GridBagConstraints constraintsPixelSizeYJLabel = new java.awt.GridBagConstraints();
			constraintsPixelSizeYJLabel.gridx = 3; constraintsPixelSizeYJLabel.gridy = 0;
			constraintsPixelSizeYJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjJPanel5.add(getJLabel1(), constraintsJLabel1);
			getJPanel5().add(getPixelSizeYJLabel(), constraintsPixelSizeYJLabel);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 4; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.insets = new Insets(4, 20, 4, 4);
			java.awt.GridBagConstraints constraintsPixelSizeZJLabel = new java.awt.GridBagConstraints();
			constraintsPixelSizeZJLabel.gridx = 5; constraintsPixelSizeZJLabel.gridy = 0;
			constraintsPixelSizeZJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjJPanel5.add(getJLabel2(), constraintsJLabel2);
			getJPanel5().add(getPixelSizeZJLabel(), constraintsPixelSizeZJLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel5;
}


/**
 * Return the JPanel6 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel6() {
	if (ivjJPanel6 == null) {
		try {
			ivjJPanel6 = new javax.swing.JPanel();
			ivjJPanel6.setName("JPanel6");
			ivjJPanel6.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsAnnotationJTextArea = new java.awt.GridBagConstraints();
			constraintsAnnotationJTextArea.gridx = 0; constraintsAnnotationJTextArea.gridy = 1;
			constraintsAnnotationJTextArea.fill = java.awt.GridBagConstraints.BOTH;
			constraintsAnnotationJTextArea.weightx = 1.0;
			constraintsAnnotationJTextArea.weighty = 1.0;
			constraintsAnnotationJTextArea.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel6().add(getAnnotationJTextArea(), constraintsAnnotationJTextArea);

			java.awt.GridBagConstraints constraintsAnnotationJLabel = new java.awt.GridBagConstraints();
			constraintsAnnotationJLabel.gridx = 0; constraintsAnnotationJLabel.gridy = 0;
			constraintsAnnotationJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel6().add(getAnnotationJLabel(), constraintsAnnotationJLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel6;
}


/**
 * Return the JTextFieldFieldName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldFieldName() {
	if (ivjJTextFieldFieldName == null) {
		try {
			ivjJTextFieldFieldName = new javax.swing.JTextField();
			ivjJTextFieldFieldName.setName("JTextFieldFieldName");
			ivjJTextFieldFieldName.setToolTipText("Microns for whole X axis");
			ivjJTextFieldFieldName.setFont(new Font("Dialog", Font.PLAIN, 14));
			ivjJTextFieldFieldName.setText("NewFieldName");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldFieldName;
}


/**
 * Return the MicronJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMicronJLabel() {
	if (ivjMicronJLabel == null) {
		try {
			ivjMicronJLabel = new javax.swing.JLabel();
			ivjMicronJLabel.setName("MicronJLabel");
			ivjMicronJLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
			ivjMicronJLabel.setText("Size (microns):");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMicronJLabel;
}


/**
 * Return the MicronJLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMicronJLabel1() {
	if (ivjMicronJLabel1 == null) {
		try {
			ivjMicronJLabel1 = new javax.swing.JLabel();
			ivjMicronJLabel1.setName("MicronJLabel1");
			ivjMicronJLabel1.setFont(new Font("Dialog", Font.PLAIN, 14));
			ivjMicronJLabel1.setText("Origin:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMicronJLabel1;
}


public Origin getOrigin(){
	return new org.vcell.util.Origin(
			Double.parseDouble(getXOriginJTextField1().getText()),
			Double.parseDouble(getYOriginJTextField1().getText()),
			Double.parseDouble(getZOriginJTextField1().getText())
			);

}
/**
 * Return the PixelSizeJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPixelSizeJLabel() {
	if (ivjPixelSizeJLabel == null) {
		try {
			ivjPixelSizeJLabel = new javax.swing.JLabel();
			ivjPixelSizeJLabel.setName("PixelSizeJLabel");
			ivjPixelSizeJLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
			ivjPixelSizeJLabel.setText("Pixel Size:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPixelSizeJLabel;
}


/**
 * Return the PixelSizeXJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPixelSizeXJLabel() {
	if (ivjPixelSizeXJLabel == null) {
		try {
			ivjPixelSizeXJLabel = new javax.swing.JLabel();
			ivjPixelSizeXJLabel.setName("PixelSizeXJLabel");
			ivjPixelSizeXJLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			ivjPixelSizeXJLabel.setText("Pixel X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPixelSizeXJLabel;
}


/**
 * Return the PixelSizeYJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPixelSizeYJLabel() {
	if (ivjPixelSizeYJLabel == null) {
		try {
			ivjPixelSizeYJLabel = new javax.swing.JLabel();
			ivjPixelSizeYJLabel.setName("PixelSizeYJLabel");
			ivjPixelSizeYJLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			ivjPixelSizeYJLabel.setText("Pixel Y");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPixelSizeYJLabel;
}


/**
 * Return the PixelSizeZJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPixelSizeZJLabel() {
	if (ivjPixelSizeZJLabel == null) {
		try {
			ivjPixelSizeZJLabel = new javax.swing.JLabel();
			ivjPixelSizeZJLabel.setName("PixelSizeZJLabel");
			ivjPixelSizeZJLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			ivjPixelSizeZJLabel.setText("Pixel Z");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPixelSizeZJLabel;
}

public double[] getTimes(){
	return dataTimes;
}

/**
 * Return the XMicronJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getXMicronJLabel() {
	if (ivjXMicronJLabel == null) {
		try {
			ivjXMicronJLabel = new javax.swing.JLabel();
			ivjXMicronJLabel.setName("XMicronJLabel");
			ivjXMicronJLabel.setText("X:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXMicronJLabel;
}


/**
 * Return the XMicronJLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getXMicronJLabel1() {
	if (ivjXMicronJLabel1 == null) {
		try {
			ivjXMicronJLabel1 = new javax.swing.JLabel();
			ivjXMicronJLabel1.setName("XMicronJLabel1");
			ivjXMicronJLabel1.setText("X:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXMicronJLabel1;
}


/**
 * Return the XMicronJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getXMicronJTextField() {
	if (ivjXMicronJTextField == null) {
		try {
			ivjXMicronJTextField = new javax.swing.JTextField();
			ivjXMicronJTextField.setName("XMicronJTextField");
			ivjXMicronJTextField.setToolTipText("Microns for whole X axis");
			ivjXMicronJTextField.setFont(new Font("Dialog", Font.PLAIN, 14));
			ivjXMicronJTextField.setText("1.0");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXMicronJTextField;
}


/**
 * Return the XMicronJTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getXOriginJTextField1() {
	if (ivjXOriginJTextField1 == null) {
		try {
			ivjXOriginJTextField1 = new javax.swing.JTextField();
			ivjXOriginJTextField1.setName("XOriginJTextField1");
			ivjXOriginJTextField1.setToolTipText("Microns for whole X axis");
			ivjXOriginJTextField1.setFont(new Font("Dialog", Font.PLAIN, 14));
			ivjXOriginJTextField1.setText("0");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXOriginJTextField1;
}

/**
 * Return the YMicronJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getYMicronJLabel() {
	if (ivjYMicronJLabel == null) {
		try {
			ivjYMicronJLabel = new javax.swing.JLabel();
			ivjYMicronJLabel.setName("YMicronJLabel");
			ivjYMicronJLabel.setText("Y:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYMicronJLabel;
}


/**
 * Return the YMicronJLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getYMicronJLabel1() {
	if (ivjYMicronJLabel1 == null) {
		try {
			ivjYMicronJLabel1 = new javax.swing.JLabel();
			ivjYMicronJLabel1.setName("YMicronJLabel1");
			ivjYMicronJLabel1.setText("Y:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYMicronJLabel1;
}


/**
 * Return the YMicronJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getYMicronJTextField() {
	if (ivjYMicronJTextField == null) {
		try {
			ivjYMicronJTextField = new javax.swing.JTextField();
			ivjYMicronJTextField.setName("YMicronJTextField");
			ivjYMicronJTextField.setToolTipText("Microns for Whole Y axis");
			ivjYMicronJTextField.setFont(new Font("Dialog", Font.PLAIN, 14));
			ivjYMicronJTextField.setText("1.0");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYMicronJTextField;
}


/**
 * Return the YMicronJTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getYOriginJTextField1() {
	if (ivjYOriginJTextField1 == null) {
		try {
			ivjYOriginJTextField1 = new javax.swing.JTextField();
			ivjYOriginJTextField1.setName("YOriginJTextField1");
			ivjYOriginJTextField1.setToolTipText("Microns for Whole Y axis");
			ivjYOriginJTextField1.setFont(new Font("Dialog", Font.PLAIN, 14));
			ivjYOriginJTextField1.setText("0");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYOriginJTextField1;
}

/**
 * Return the ZMicronJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getZMicronJLabel() {
	if (ivjZMicronJLabel == null) {
		try {
			ivjZMicronJLabel = new javax.swing.JLabel();
			ivjZMicronJLabel.setName("ZMicronJLabel");
			ivjZMicronJLabel.setText("Z:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZMicronJLabel;
}


/**
 * Return the ZMicronJLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getZMicronJLabel1() {
	if (ivjZMicronJLabel1 == null) {
		try {
			ivjZMicronJLabel1 = new javax.swing.JLabel();
			ivjZMicronJLabel1.setName("ZMicronJLabel1");
			ivjZMicronJLabel1.setText("Z:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZMicronJLabel1;
}


/**
 * Return the ZMicronJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getZMicronJTextField() {
	if (ivjZMicronJTextField == null) {
		try {
			ivjZMicronJTextField = new javax.swing.JTextField();
			ivjZMicronJTextField.setName("ZMicronJTextField");
			ivjZMicronJTextField.setToolTipText("Microns for Whole Z axis");
			ivjZMicronJTextField.setFont(new Font("Dialog", Font.PLAIN, 14));
			ivjZMicronJTextField.setText("1.0");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZMicronJTextField;
}


/**
 * Return the ZMicronJTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getZOriginJTextField1() {
	if (ivjZOriginJTextField1 == null) {
		try {
			ivjZOriginJTextField1 = new javax.swing.JTextField();
			ivjZOriginJTextField1.setName("ZOriginJTextField1");
			ivjZOriginJTextField1.setToolTipText("Microns for Whole Z axis");
			ivjZOriginJTextField1.setFont(new Font("Dialog", Font.PLAIN, 14));
			ivjZOriginJTextField1.setText("0");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZOriginJTextField1;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("FieldDataInfoPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(570, 344);

		java.awt.GridBagConstraints constraintsJPanel = new java.awt.GridBagConstraints();
		constraintsJPanel.gridx = 0; constraintsJPanel.gridy = 0;
		constraintsJPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel.weightx = 1.0;
		constraintsJPanel.weighty = 1.0;
		add(getJPanel(), constraintsJPanel);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

public void initTimes(double[] times) {
	
	dataTimes = times;
	
	jLabelTimeCount.setText(times.length+"");
	jLabeTimeBeg.setText(times[0]+"");
	jLabelTimeEnd.setText(times[times.length-1]+"");
	
}

public void initNames(String argFieldName,String[] argVarNames){
	getJTextFieldFieldName().setText(argFieldName);
	jComboBoxVarNames.removeAllItems();
	for(int i=0;i<argVarNames.length;i+= 1){
		jComboBoxVarNames.addItem(argVarNames[i]);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/18/2007 2:03:43 PM)
 */
public void initISize(org.vcell.util.ISize isize) {
	
	getPixelSizeXJLabel().setText(isize.getX()+"");
	getPixelSizeYJLabel().setText(isize.getY()+"");
	getPixelSizeZJLabel().setText(isize.getZ()+"");
	if(isize.getZ() == 1){
		getZOriginJTextField1().setEnabled(false);
		getZMicronJTextField().setEnabled(false);
	}
	if(isize.getY() == 1){
		getYOriginJTextField1().setEnabled(false);
		getYMicronJTextField().setEnabled(false);
	}
	
}

public void initIOrigin(Origin origin) {
	
	getXOriginJTextField1().setText(origin.getX()+"");
	getYOriginJTextField1().setText(origin.getY()+"");
	getZOriginJTextField1().setText(origin.getZ()+"");
	
}

public void initIExtent(Extent extent) {
	
	getXMicronJTextField().setText(extent.getX()+"");
	getYMicronJTextField().setText(extent.getY()+"");
	getZMicronJTextField().setText(extent.getZ()+"");
	
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		FieldDataInfoPanel aFieldDataInfoPanel;
		aFieldDataInfoPanel = new FieldDataInfoPanel();
		frame.setContentPane(aFieldDataInfoPanel);
		frame.setSize(aFieldDataInfoPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}

/**
 * This method initializes jPanel	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getJPanel2() {
	if (jPanel == null) {
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 5;
		gridBagConstraints6.weightx = 0.0;
		gridBagConstraints6.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints6.gridy = 0;
		jLabelTimeEnd = new JLabel();
		jLabelTimeEnd.setText("JLabel");
		jLabelTimeEnd.setFont(new Font("Dialog", Font.BOLD, 14));
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 4;
		gridBagConstraints5.insets = new Insets(4, 20, 4, 4);
		gridBagConstraints5.gridy = 0;
		jLabel23 = new JLabel();
		jLabel23.setText("End:");
		jLabel23.setFont(new Font("Dialog", Font.BOLD, 14));
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 3;
		gridBagConstraints4.weightx = 0.0;
		gridBagConstraints4.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints4.gridy = 0;
		jLabeTimeBeg = new JLabel();
		jLabeTimeBeg.setText("JLabel");
		jLabeTimeBeg.setFont(new Font("Dialog", Font.BOLD, 14));
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 2;
		gridBagConstraints3.insets = new Insets(4, 20, 4, 4);
		gridBagConstraints3.ipadx = 0;
		gridBagConstraints3.gridy = 0;
		jLabel21 = new JLabel();
		jLabel21.setText("Begin:");
		jLabel21.setFont(new Font("Dialog", Font.BOLD, 14));
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.weightx = 0.0;
		gridBagConstraints2.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints2.gridy = 0;
		jLabelTimeCount = new JLabel();
		jLabelTimeCount.setText("JLabel");
		jLabelTimeCount.setFont(new Font("Dialog", Font.BOLD, 14));
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints1.gridy = 0;
		jLabel1 = new JLabel();
		jLabel1.setText("Total:");
		jLabel1.setFont(new Font("Dialog", Font.BOLD, 14));
		jPanel = new JPanel();
		jPanel.setLayout(new GridBagLayout());
		jPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		jPanel.add(jLabel1, gridBagConstraints1);
		jPanel.add(jLabelTimeCount, gridBagConstraints2);
		jPanel.add(jLabel21, gridBagConstraints3);
		jPanel.add(jLabeTimeBeg, gridBagConstraints4);
		jPanel.add(jLabel23, gridBagConstraints5);
		jPanel.add(jLabelTimeEnd, gridBagConstraints6);
	}
	return jPanel;
}

/**
 * This method initializes jPanel1	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getJPanel12() {
	if (jPanel1 == null) {
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints8.gridy = 0;
		gridBagConstraints8.weightx = 1.0;
		gridBagConstraints8.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints8.gridx = 0;
		jPanel1 = new JPanel();
		jPanel1.setLayout(new GridBagLayout());
		jPanel1.add(getJComboBoxVarNames(), gridBagConstraints8);
	}
	return jPanel1;
}

/**
 * This method initializes jComboBoxVarNames	
 * 	
 * @return javax.swing.JComboBox	
 */
private JComboBox getJComboBoxVarNames() {
	if (jComboBoxVarNames == null) {
		jComboBoxVarNames = new JComboBox();
	}
	return jComboBoxVarNames;
}

/**
 * This method initializes jButtonVarNameEdit	
 * 	
 * @return javax.swing.JButton	
 */
private JButton getJButtonVarNameEdit() {
	if (jButtonVarNameEdit == null) {
		jButtonVarNameEdit = new JButton();
		jButtonVarNameEdit.setText("Edit Var...");
		jButtonVarNameEdit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				try{
					String newVarName =	inputStrictName(jComboBoxVarNames.getSelectedItem().toString(),
								"Edit Variable Name");

					int selIndex = jComboBoxVarNames.getSelectedIndex();
					((DefaultComboBoxModel)jComboBoxVarNames.getModel()).removeElementAt(selIndex);
					((DefaultComboBoxModel)jComboBoxVarNames.getModel()).insertElementAt(newVarName, selIndex);
					jComboBoxVarNames.setSelectedIndex(selIndex);
				}catch(UserCancelException e2){
					//ignore
				}
			}
		});
	}
	return jButtonVarNameEdit;
}

private String inputStrictName(String initalValue,String message)throws UserCancelException{
	String strictName = initalValue;
	while(true){
		strictName =
			PopupGenerator.showInputDialog(this, message, strictName);
		String fixedVarName = TokenMangler.fixTokenStrict(strictName);
		if(!strictName.equals(fixedVarName)){
			int result =
				PopupGenerator.showComponentOKCancelDialog(this, null,
					"Special characters were removed.\n"+
					"Is the value "+fixedVarName+" alright?");
			if(result == JOptionPane.OK_OPTION){
				strictName = fixedVarName;
			}else{
				continue;
			}
		}
		return strictName;
	}
	
}

public void setSimulationMode(boolean bSim){
	bsimMode = bSim;
	getJButtonSeqTimes().setEnabled(!bsimMode);
	getJButtonEditTimes().setEnabled(!bsimMode);
	getJButtonVarNameEdit().setEnabled(!bsimMode);
	BeanUtils.enableComponents(getJPanelOrigin(), !bSim);
	BeanUtils.enableComponents(getJPanelSize(), !bSim);
}

/**
 * This method initializes jButtonEditTimes	
 * 	
 * @return javax.swing.JButton	
 */
private JButton getJButtonEditTimes() {
	if (jButtonEditTimes == null) {
		jButtonEditTimes = new JButton();
		jButtonEditTimes.setText("Edit Times...");
		jButtonEditTimes.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				String[] colNames = new String[] {"time","value"};
				String[][] rows = new String[dataTimes.length][2];
				for(int i=0;i<rows.length;i+= 1){
					rows[i][0] = i+"";
					rows[i][1] = dataTimes[i]+"";
				}
				DefaultTableModel tableMode = new DefaultTableModel(){
				    public boolean isCellEditable(int row, int column) {
				        return column==1;
				    }
				};
				tableMode.setDataVector(rows, colNames);
				JTable table = new JTable(tableMode);
				
				
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			    TableColumn col = table.getColumnModel().getColumn(0);
			    col.setPreferredWidth(50);

			    
				JScrollPane scrollPane = new JScrollPane(table);
				table.setPreferredScrollableViewportSize(new Dimension(250, 250));
				while(true){
					int result =
						PopupGenerator.showComponentOKCancelDialog(
								FieldDataInfoPanel.this, scrollPane, "Edit Times");
					if(result == JOptionPane.OK_OPTION){
						if(table.isEditing()){
							table.getCellEditor().stopCellEditing();
						}
						double[] newTimes = new double[dataTimes.length];
						try{
							for(int i=0;i<dataTimes.length;i+= 1){
								newTimes[i] =
									Double.parseDouble(
											tableMode.getValueAt(i, 1).toString());
							}
						}catch(Exception e2){
							PopupGenerator.showErrorDialog(FieldDataInfoPanel.this, 
									"Error parsing new times. Try again.\n"+e2.getMessage(), e2);
							continue;
						}
						initTimes(newTimes);
						break;
					}else{
						break;
					}
				}
			}
		});
	}
	return jButtonEditTimes;
}

/**
 * This method initializes jPanel2	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getJPanel22() {
	if (jPanel2 == null) {
		GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		gridBagConstraints10.gridx = 0;
		gridBagConstraints10.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints10.gridy = 1;
		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		gridBagConstraints9.gridx = 0;
		gridBagConstraints9.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints9.gridy = 0;
		jPanel2 = new JPanel();
		jPanel2.setLayout(new GridBagLayout());
		jPanel2.add(getJButtonEditTimes(), gridBagConstraints9);
		jPanel2.add(getJButtonSeqTimes(), gridBagConstraints10);
	}
	return jPanel2;
}

/**
 * This method initializes jButtonSeqTimes	
 * 	
 * @return javax.swing.JButton	
 */
private JButton getJButtonSeqTimes() {
	if (jButtonSeqTimes == null) {
		jButtonSeqTimes = new JButton();
		jButtonSeqTimes.setText("Sequence...");
		jButtonSeqTimes.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				try{
				String incrS = PopupGenerator.showInputDialog(FieldDataInfoPanel.this,
						"Enter a uniform Time Step value", "1.0");
				double incrVal = Double.parseDouble(incrS);
				if(incrVal == 0){
					throw new Exception("Time Step value cannot be 0");
				}
				double[] newTimes = new double[dataTimes.length];
				for(int i=0;i<dataTimes.length;i+= 1){
					newTimes[i] = i*incrVal;
				}
				initTimes(newTimes);
				}catch(UserCancelException e2){
					//ignore
				}catch(Exception e2){
					PopupGenerator.showErrorDialog(FieldDataInfoPanel.this, "Error generating sequence\n"+e2.getMessage(), e2);
				}
			}
		});
	}
	return jButtonSeqTimes;
}

}
