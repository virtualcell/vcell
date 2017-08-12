/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;
import java.awt.Component;
import java.awt.Frame;

import javax.swing.SwingUtilities;
/**
 * Insert the type's description here.
 * Creation date: (9/20/2004 4:26:26 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class VCFileChooser extends javax.swing.JFileChooser implements java.awt.event.ActionListener {
	private String localDialogTitle = null;
	javax.swing.JDialog localDialog = null;
	private int localReturnValue = javax.swing.JFileChooser.ERROR_OPTION;

/**
 * VCFileChooser constructor comment.
 */
public VCFileChooser() {
	super();
	addActionListener(this);
}


/**
 * VCFileChooser constructor comment.
 * @param currentDirectory java.io.File
 */
public VCFileChooser(java.io.File currentDirectory) {
	super(currentDirectory);
	addActionListener(this);
}


/**
 * VCFileChooser constructor comment.
 * @param currentDirectory java.io.File
 * @param fsv javax.swing.filechooser.FileSystemView
 */
public VCFileChooser(java.io.File currentDirectory, javax.swing.filechooser.FileSystemView fsv) {
	super(currentDirectory, fsv);
	addActionListener(this);
}


/**
 * VCFileChooser constructor comment.
 * @param currentDirectoryPath java.lang.String
 */
public VCFileChooser(String currentDirectoryPath) {
	super(currentDirectoryPath);
	addActionListener(this);
}


/**
 * VCFileChooser constructor comment.
 * @param currentDirectoryPath java.lang.String
 * @param fsv javax.swing.filechooser.FileSystemView
 */
public VCFileChooser(String currentDirectoryPath, javax.swing.filechooser.FileSystemView fsv) {
	super(currentDirectoryPath, fsv);
	addActionListener(this);
}


/**
 * VCFileChooser constructor comment.
 * @param fsv javax.swing.filechooser.FileSystemView
 */
public VCFileChooser(javax.swing.filechooser.FileSystemView fsv) {
	super(fsv);
	addActionListener(this);
}


	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getActionCommand().equals(CANCEL_SELECTION)){
		localReturnValue = CANCEL_OPTION;
		if (localDialog!=null){
			localDialog.dispose();
		}
	}else if (e.getActionCommand().equals(APPROVE_SELECTION)){
		localReturnValue = APPROVE_OPTION;
		if (localDialog!=null){
			localDialog.dispose();
		}
	}
}


	/**
	 * Sets the string that goes in the FileChooser window's title bar.
	 *
	 * @beaninfo
	 *   preferred: true
	 *       bound: true
	 * description: The title of the FileChooser dialog window
	 *
	 * @see #getDialogTitle
	 *
	 */
	public void setDialogTitle(String dialogTitle) {
		super.setDialogTitle(dialogTitle);
		this.localDialogTitle = dialogTitle;
	}

/**
 * Pops a custom file chooser dialog with a custom ApproveButton.
 *
 * e.g. filechooser.showDialog(parentWindow, "Run Application");
 * would pop up a filechooser with a "Run Application" button
 * (instead of the normal "Save" or "Open" button).
 *
 * Alternatively, the following code will do the same thing:
 *    JFileChooser chooser = new JFileChooser(null);
 *    chooser.setApproveButtonText("Run Application");
 *    chooser.showDialog(this, null);
 * 
 * PENDING(jeff) - the following method should be added to the api:
 *      showDialog(Component parent);
 *
 * @param   approveButtonText the text of the ApproveButton
 * @return  the return state of the filechooser on popdown:
 *             CANCEL_OPTION, APPROVE_OPTION
 */
public int showDialog(final Component parent, String approveButtonText) {
	if (approveButtonText != null) {
		setApproveButtonText(approveButtonText);
		setDialogType(CUSTOM_DIALOG);
	}

	Frame frame = parent instanceof Frame ? (Frame) parent : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

	String title = null;

	if (getDialogTitle() != null) {
		title = localDialogTitle; // dialogTitle
	} else {
		title = getUI().getDialogTitle(this);
	}

	localDialog = new javax.swing.JDialog(frame, title, true);
	java.awt.Container contentPane = localDialog.getContentPane();
	contentPane.setLayout(new java.awt.BorderLayout());
	contentPane.add(this, java.awt.BorderLayout.CENTER);

	localDialog.pack();
	localDialog.setLocationRelativeTo(parent);

	localDialog.setResizable(false);
	try {
		DialogUtils.showModalJDialogOnTop(localDialog, parent);
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		throw new RuntimeException(exc.getMessage());
	} finally {
		localDialog.dispose();
		localDialog = null;
	}

	return localReturnValue;
}
}
