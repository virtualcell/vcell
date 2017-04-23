/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui.generic;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Insert the type's description here.
 * Creation date: (8/29/2003 2:51:26 PM)
 * @author: Fei Gao
 */
@SuppressWarnings("serial")
public class DatePanel extends javax.swing.JPanel {
	private javax.swing.JComboBox ivjDayCombo = null;
	private javax.swing.JComboBox ivjMonthCombo = null;
	private javax.swing.JComboBox ivjYearCombo = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	private class IvjEventHandler implements java.awt.event.ItemListener {
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == DatePanel.this.getMonthCombo()) 
				monthCombo_ItemEvent();
			if (e.getSource() == DatePanel.this.getYearCombo()) 
				yearCombo_ItemEvent();
		}
	};
/**
 * DatePanel constructor comment.
 */
public DatePanel() {
	super();
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (8/29/2003 3:04:18 PM)
 */
private void changeMonth() {
	int month = Integer.parseInt((String)getMonthCombo().getSelectedItem());
	int year = Integer.parseInt((String)getYearCombo().getSelectedItem());
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar(year, month - 1, 1);
	getDayCombo().removeAllItems();

	int maxday = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
	for (int i = 1; i <= maxday; i++){
		getDayCombo().addItem(i + "");	
	}
}
/**
 * Comment
 */
public void reset() {
	updateInterface(new GregorianCalendar());
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/2003 3:17:06 PM)
 * @return java.util.Date
 */
public Date getDate() {
	int month = Integer.parseInt((String)getMonthCombo().getSelectedItem());
	int day  = Integer.parseInt((String)getDayCombo().getSelectedItem());
	int year = Integer.parseInt((String)getYearCombo().getSelectedItem());

	java.util.GregorianCalendar calendar = new java.util.GregorianCalendar(year, month - 1, day);
	return calendar.getTime();
}

/**
 * Return the JComboBox2 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getDayCombo() {
	if (ivjDayCombo == null) {
		try {
			ivjDayCombo = new javax.swing.JComboBox();
			ivjDayCombo.setName("DayCombo");
			ivjDayCombo.setToolTipText("Day");
			JTextField tf = new JTextField(4);
			ivjDayCombo.setPreferredSize(tf.getPreferredSize());
			ivjDayCombo.setEditable(true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDayCombo;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getMonthCombo() {
	if (ivjMonthCombo == null) {
		try {
			ivjMonthCombo = new javax.swing.JComboBox();
			ivjMonthCombo.setName("MonthCombo");
			ivjMonthCombo.setToolTipText("Month");
			ivjMonthCombo.setEditable(true);
			JTextField tf = new JTextField(4);
			ivjMonthCombo.setPreferredSize(tf.getPreferredSize());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMonthCombo;
}
/**
 * Return the JComboBox3 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getYearCombo() {
	if (ivjYearCombo == null) {
		try {
			ivjYearCombo = new javax.swing.JComboBox();
			ivjYearCombo.setName("YearCombo");
			ivjYearCombo.setToolTipText("Year");
			JTextField tf = new JTextField(6);
			ivjYearCombo.setPreferredSize(tf.getPreferredSize());
			ivjYearCombo.setEditable(true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjYearCombo;
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
		setName("DatePanel");
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(getMonthCombo(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(new JLabel("/"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(getDayCombo(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		add(new JLabel("/"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(getYearCombo(), gbc);
		
		Calendar cal = new GregorianCalendar();
		int currYear = cal.get(java.util.Calendar.YEAR);
		for (int i = -20; i <= 0; i ++) {
			getYearCombo().addItem((i + currYear) + "");
		}
		for (int i = 1; i <= 12; i ++) {
			getMonthCombo().addItem(i + "");
		}
		updateInterface(cal);
		getMonthCombo().addItemListener(ivjEventHandler);
		getYearCombo().addItemListener(ivjEventHandler);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
private void monthCombo_ItemEvent() {
	changeMonth();
}
/**
 * Insert the method's description here.
 * Creation date: (9/3/2003 8:02:44 AM)
 */
private void updateInterface(Calendar calendar) {
	for (int i = 1; i <= calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH); i ++) {
		getDayCombo().addItem(i + "");
	}
	getYearCombo().setSelectedItem(calendar.get(java.util.Calendar.YEAR) + "");
	getMonthCombo().setSelectedItem((calendar.get(java.util.Calendar.MONTH) + 1) + "");
	getDayCombo().setSelectedItem(calendar.get(java.util.Calendar.DATE) + "");	
}

@Override
public void setEnabled(boolean enabled) {
	getYearCombo().setEnabled(enabled);
	getMonthCombo().setEnabled(enabled);
	getDayCombo().setEnabled(enabled);
	super.setEnabled(enabled);
}
/**
 * Comment
 */
private void yearCombo_ItemEvent() {
	changeMonth();
	return;
}

public void setCalendar(Calendar cal) {
	updateInterface(cal);
}
}
