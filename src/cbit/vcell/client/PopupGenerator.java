package cbit.vcell.client;
import cbit.vcell.client.server.*;
import javax.swing.*;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.SwingDispatcherSync;
import org.vcell.util.gui.UtilCancelException;
import org.vcell.util.gui.ZEnforcer;

import java.awt.*;
import cbit.gui.*;
/**
 * Insert the type's description here.
 * Creation date: (5/21/2004 3:16:43 AM)
 * @author: Ion Moraru
 */
public class PopupGenerator extends DialogUtils{
/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:23:18 AM)
 * @return int
 * @param owner java.awt.Component
 * @param message java.lang.String
 * @param preferences cbit.vcell.client.UserPreferences
 * @param preferenceName java.lang.String
 */
private static String showDialog(final Component requester, final UserPreferences preferences, final UserMessage userMessage, final String replacementText, final int jOptionPaneMessageType) {
	return (String)
	new SwingDispatcherSync() {
		public Object runSwing() throws Exception{
			//
			// if userMessage is a warning that can be ignored, and the preference is to ignore it, then return default selection.
			//
			if (userMessage.getUserPreferenceWarning() > -1 && preferences!=null && !preferences.getShowWarning(userMessage.getUserPreferenceWarning())){
				return userMessage.getDefaultSelection();
			}

			
			String message = userMessage.getMessage(replacementText);
			JPanel panel = createMessagePanel(message);
			JCheckBox checkBox = null;
			if (userMessage.getUserPreferenceWarning() >= 0){
				checkBox = new JCheckBox("Do not show this warning again");
				panel.add(checkBox, BorderLayout.SOUTH);
			}
			JOptionPane pane = new JOptionPane(panel, jOptionPaneMessageType, 0, null, userMessage.getOptions(), userMessage.getDefaultSelection());
			final JDialog dialog = pane.createDialog(requester, "");
			switch (jOptionPaneMessageType) {
				case JOptionPane.WARNING_MESSAGE: {
					dialog.setTitle("WARNING:");
					break;
				}
				case JOptionPane.ERROR_MESSAGE: {
					dialog.setTitle("ERROR:");
					break;
				}
				case JOptionPane.INFORMATION_MESSAGE: {
					dialog.setTitle("INFO:");
					break;
				}
			}
			dialog.setResizable(true);
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			try {
				ZEnforcer.showModalDialogOnTop(dialog,requester);
				if (checkBox!=null){
					preferences.setShowWarning(userMessage.getUserPreferenceWarning(), ! checkBox.isSelected());
				}
				Object selectedValue = pane.getValue();
				if(selectedValue == null || selectedValue.equals(JOptionPane.UNINITIALIZED_VALUE)) {
					return UserMessage.OPTION_CANCEL;
				} else {
					return (String)selectedValue;
				}
			}finally {
				dialog.dispose();
			}
		}
	}.dispatchWrapRuntime();
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
public static String showErrorDialog(TopLevelWindowManager requester, UserPreferences preferences, UserMessage userMessage, String replacementText) {
	return showDialog(requester.getComponent(), preferences, userMessage, replacementText,JOptionPane.ERROR_MESSAGE);
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
public static void showErrorDialog(TopLevelWindowManager requester, String message) {
	showErrorDialog(requester.getComponent(), message);
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
		return showInputDialog0(requester,message,initialValue);
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
	return showListDialog(requester.getComponent(), names, dialogTitle);
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
	return showDialog(requester.getComponent(), preferences, userMessage, replacementText,JOptionPane.WARNING_MESSAGE);
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
	return showDialog(component, preferences, userMessage, replacementText,JOptionPane.WARNING_MESSAGE);
}
}