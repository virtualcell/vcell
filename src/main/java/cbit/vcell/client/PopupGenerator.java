/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;
import java.awt.Component;

import cbit.vcell.client.server.*;
import javax.swing.*;

import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.Version;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.UtilCancelException;
/**
 * Insert the type's description here.
 * Creation date: (5/21/2004 3:16:43 AM)
 * @author: Ion Moraru
 */
public class PopupGenerator extends DialogUtils {

/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:23:18 AM)
 * @return int
 * @param owner java.awt.Component
 * @param message java.lang.String
 * @param preferences cbit.vcell.client.UserPreferences
 * @param preferenceName java.lang.String
 */
public static void showErrorDialog(TopLevelWindowManager requester, String message) {
	showErrorDialog(requester, message,null);
}

/**
 * showErrorDialog. If requester is of type DocumentWindowManager, offer to send context information
 * to VCellSupport 
 * @param requester
 * @param message to display
 * @param ex may be null
 */
public static void showErrorDialog(TopLevelWindowManager requester, String message, Throwable ex) {
	DialogUtils.ErrorContext errorContext = null;
	if (requester instanceof DocumentWindowManager) {
		DocumentWindowManager dwm = (DocumentWindowManager) requester;
		VCDocument doc = dwm.getVCDocument();
		String contextString = doc.getName();
		Version v = doc.getVersion();
		if (v != null) {
			contextString += " " + v.identificationString();
		}
		errorContext = new DialogUtils.ErrorContext(contextString, requester.getUserPreferences()); 
	}
	showErrorDialog(requester.getComponent(), message, ex, errorContext);
}

public static void showInfoDialog(TopLevelWindowManager requester, String message) {
	showInfoDialog(requester.getComponent(), message);
}

/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:23:18 AM)
 * @return int
 * @param owner java.awt.Component
 * @param message java.lang.String
 * @param preferences cbit.vcell.client.UserPreferences
 * @param preferenceName java.lang.String
 */
public static String showInfoDialog(TopLevelWindowManager requester, UserPreferences preferences, UserMessage userMessage, String replacementText) {
	return showDialog(requester.getComponent(), preferences, userMessage, replacementText,JOptionPane.INFORMATION_MESSAGE);
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:23:18 AM)
 * @return int
 * @param owner java.awt.Component
 * @param message java.lang.String
 * @param preferences cbit.vcell.client.UserPreferences
 * @param preferenceName java.lang.String
 */
public static String showInputDialog(TopLevelWindowManager requester, String message) throws UserCancelException{
	return showInputDialog(requester, message, null);
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:23:18 AM)
 * @return int
 * @param owner java.awt.Component
 * @param message java.lang.String
 * @param preferences cbit.vcell.client.UserPreferences
 * @param preferenceName java.lang.String
 */
public static String showInputDialog(TopLevelWindowManager requester, String message, String initialValue) throws UserCancelException{
	return showInputDialog(requester.getComponent(), message, null);
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:23:18 AM)
 * @return int
 * @param owner java.awt.Component
 * @param message java.lang.String
 * @param preferences cbit.vcell.client.UserPreferences
 * @param preferenceName java.lang.String
 */
public static String showInputDialog(final Component requester, String message, String initialValue) throws UserCancelException{

	try{
		return DialogUtils.showInputDialog0(requester,message,initialValue);
	}catch(UtilCancelException e){
		throw UserCancelException.CANCEL_GENERIC;
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:23:18 AM)
 * @return int
 * @param owner java.awt.Component
 * @param message java.lang.String
 * @param preferences cbit.vcell.client.UserPreferences
 * @param preferenceName java.lang.String
 */
public static Object showListDialog(TopLevelWindowManager requester, Object[] names, String dialogTitle) {
	return DialogUtils.showListDialog(requester.getComponent(), names, dialogTitle);
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:23:18 AM)
 * @return int
 * @param owner java.awt.Component
 * @param message java.lang.String
 * @param preferences cbit.vcell.client.UserPreferences
 * @param preferenceName java.lang.String
 */
public static void showReportDialog(TopLevelWindowManager requester, String reportText) {
	DialogUtils.showReportDialog(requester.getComponent(), reportText);
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:23:18 AM)
 * @return int
 * @param owner java.awt.Component
 * @param message java.lang.String
 * @param preferences cbit.vcell.client.UserPreferences
 * @param preferenceName java.lang.String
 */
public static String showWarningDialog(TopLevelWindowManager requester, UserPreferences preferences, UserMessage userMessage, String replacementText) {
	return DialogUtils.showDialog(requester.getComponent(), preferences, userMessage, replacementText,JOptionPane.WARNING_MESSAGE);
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:23:18 AM)
 * @return int
 * @param owner java.awt.Component
 * @param message java.lang.String
 * @param preferences cbit.vcell.client.UserPreferences
 * @param preferenceName java.lang.String
 */
public static String showWarningDialog(Component component, UserPreferences preferences, UserMessage userMessage, String replacementText) {
	return DialogUtils.showDialog(component, preferences, userMessage, replacementText,JOptionPane.WARNING_MESSAGE);
}
}
