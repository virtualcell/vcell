package org.vcell.util.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.BeanUtils;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.sorttable.DefaultSortTableModel;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.UserMessage;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.test.VCellClientTest;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.BrowserLauncherRunner;
import edu.stanford.ejalbert.exceptionhandler.BrowserLauncherErrorHandler;
/**
 * Insert the type's description here.
 * Creation date: (5/21/2004 3:16:43 AM)
 * @author: Ion Moraru
 */
public class DialogUtils {
	
	private static abstract class SwingDispatcherSync {

		private Object returnValue = null;
		
		static class SwingDispatchException extends RuntimeException{
			public SwingDispatchException(Throwable e){
				super(e);
			}
		};
		
		public abstract Object runSwing() throws Exception;

		private static Exception handleThrowableAsException(Throwable throwable){
			if (throwable instanceof Exception){
				return (Exception) throwable;
			}
			throw (Error) throwable;
		}
		private static RuntimeException handleThrowableAsRuntimeException(Throwable throwable) {
			if (throwable instanceof RuntimeException){
				return (RuntimeException) throwable;
			}
			if (throwable instanceof Exception){
				return new RuntimeException(throwable.getMessage(),throwable);
			}
			throw (Error) throwable;
		}
		
		private Object dispatch() throws Throwable{
			if (SwingUtilities.isEventDispatchThread()) {
				return runSwing();
			}			
			Runnable runnable =	new Runnable(){
				public void run(){
					try{
						returnValue = runSwing();
					}catch(Throwable e){
						throw new SwingDispatchException(e);
					}
				}
			};
			try {
				SwingUtilities.invokeAndWait(runnable);
				return returnValue;
			} catch(Throwable e) {
				if (e instanceof InvocationTargetException){
					if (e.getCause() instanceof SwingDispatchException){
						throw e.getCause().getCause();
					}
					if (e.getCause() != null){
						throw e.getCause();
					}
				}
				throw e;
			}
		}	

		public Object dispatchWithException() throws Exception{
			try{
				return dispatch();
			} catch(Throwable throwable){
				printStack(throwable);
				throw handleThrowableAsException(throwable);
			}
		}
		public Object dispatchWrapRuntime(){
			try{
				return dispatch();
			}catch(Throwable throwable){
				printStack(throwable);
				throw handleThrowableAsRuntimeException(throwable);
			}
		}
		public Object dispatchConsumeException(){
			try{
				return dispatch();
			}catch(Throwable throwable){
				printStack(throwable);
				return null;
			}
		}
		private void printStack(Throwable throwable){
			if(!(throwable instanceof UserCancelException || throwable instanceof UtilCancelException)){
				throwable.printStackTrace(System.out);
			}
		}
	}
	
	private interface OKEnabler{
		void setJOptionPane(JOptionPane joptionPane);
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
	protected static String showDialog(final Component requester, final UserPreferences preferences, final UserMessage userMessage, final String replacementText, final int jOptionPaneMessageType) {
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
 * Creation date: (8/26/2005 3:26:35 PM)
 */
public static void browserLauncher(final Component requester, final String targetURL,final String messageToUserIfFail,final boolean isApplet) {
	//(isApplet==true)
	//  Do not use BrowserLauncher as it will sometimes destroy VCell Applets
	//  depending on which browser is running the applet and which OS the
	//  browser is running on.
	
	//(messageToUserIfFail)
	//  Should provide the user with the URL they can
	//  manually navigate to for the information requested
	
	try{
		if(isApplet){
			throw new Exception("VCell Applet forbidden to launch local WWW Browser");
		}
		new Thread(
			new BrowserLauncherRunner(
				new BrowserLauncher(null),
				targetURL,
				new BrowserLauncherErrorHandler() {
					public void handleException(Exception e){
						browserLauncherError(requester, e,messageToUserIfFail);
					}
				}
			)
			).start();
	}catch(Throwable e){
		browserLauncherError(requester, e,messageToUserIfFail);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/26/2005 3:31:31 PM)
 * @param e java.lang.Throwable
 * @param userMessage java.lang.String
 */
private static void browserLauncherError(Component requester, Throwable e, String userMessage) {
	showErrorDialog(requester, userMessage + "\n\n" +
			"Sorry, your local WWW Browser could not be automatically launched.\n" + 
			"Error Type=" + e.getClass().getName() + "\nError Info='" + e.getMessage()+"'", e);
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 12:54:40 AM)
 * @param message java.lang.Object
 */

private static JPanel createMessagePanel(final String message) {
	JTextPane textArea = new JTextPane();
	if (message != null && message.contains("<html>")) {
		textArea.setContentType("text/html");
	}
	textArea.setText(message);
	textArea.setCaretPosition(0);
	textArea.setEditable(false);
	textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));
	textArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

	//
	// determine "natural" TextArea prefered size (what it would like if it didn't wrap lines)
	// and try to set size accordingly (within limits ... e.g. 400<=X<=500 and 100<=Y<=400).
	//
	Dimension textAreaPreferredSize = textArea.getPreferredSize();
	Dimension preferredSize = new Dimension((int)Math.min(500,Math.max(400,textAreaPreferredSize.getWidth()+20)),
			(int)Math.min(400,Math.max(100,textAreaPreferredSize.getHeight()+20)));
	
	JScrollPane scroller = new JScrollPane();
	JPanel panel = new JPanel(new BorderLayout());
	scroller.setViewportView(textArea);
	scroller.getViewport().setPreferredSize(preferredSize);
	panel.add(scroller, BorderLayout.CENTER);
	return panel;
}

private static JDialog prepareWarningDialog(final Component requester,final String message) {
	JPanel panel = createMessagePanel(message);
	JOptionPane pane = new JOptionPane(panel, JOptionPane.WARNING_MESSAGE);
	JDialog dialog = pane.createDialog(requester, "Warning:");
	dialog.setResizable(true);
	return dialog;
}

/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 3:01:00 AM)
 * @param message java.lang.Object
 */
private static JDialog prepareInfoDialog(final Component requester,final String message) {
	JPanel panel = createMessagePanel(message);
	JOptionPane pane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE);
	JDialog dialog = pane.createDialog(requester, "");
	dialog.setResizable(true);
	return dialog;
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2005 2:01:06 PM)
 * @param jop javax.swing.JOptionPane
 * @param bEnabled boolean
 */
private static void setInternalOKEnabled(final JOptionPane jop,final  boolean bEnabled) {
  	Component[] componentsArr = jop.getComponents();
  	for(int i=0;i<componentsArr.length;i+= 1){
	  	if(componentsArr[i] instanceof Container){
		  	for(int j=0;j<((Container)componentsArr[i]).getComponentCount();j+= 1){
			  	if(((Container)componentsArr[i]).getComponent(j) instanceof JButton &&
				  	!((JButton)((Container)componentsArr[i]).getComponent(j)).getText().equalsIgnoreCase("Cancel")){
				  	if(bEnabled){
					  	((Container)componentsArr[i]).getComponent(j).setEnabled(true);
				  	}else{
					  	((Container)componentsArr[i]).getComponent(j).setEnabled(false);
				  	}
			  	}
		  	}
	  	}
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
public static String showAnnotationDialog(final Component requester, final String oldAnnotation) throws Exception{
	return (String)
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			JPanel panel = new JPanel(new BorderLayout());
			JScrollPane scroller = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			JTextArea textArea = new JTextArea(oldAnnotation, 8, 60);
			textArea.setEditable(true);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			scroller.setViewportView(textArea);
			scroller.getViewport().setPreferredSize(new Dimension(400, 200));
			panel.add(scroller, BorderLayout.CENTER);

			JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
			final JDialog dialog = pane.createDialog(requester, "Edit Annotation:");
			dialog.setResizable(true);
			try {
				ZEnforcer.showModalDialogOnTop(dialog,requester);
				Object selectedValue = pane.getValue();
				if(selectedValue == null ||
					(((Integer)selectedValue).intValue() == JOptionPane.CLOSED_OPTION) ||
					(((Integer)selectedValue).intValue() == JOptionPane.CANCEL_OPTION)) {
					throw UtilCancelException.CANCEL_GENERIC;
				}else {
					return textArea.getText();
				}
			}finally {
				dialog.dispose();
			}
		}
	}.dispatchWithException();

}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:17:45 AM)
 * @param owner java.awt.Component
 * @param message java.lang.Object
 */
public static void showComponentCloseDialog(final Component requester,final Component stayOnTopComponent,final String title) {
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			JOptionPane inputDialog = new JOptionPane(stayOnTopComponent, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Close"});
			final JDialog d = inputDialog.createDialog(requester, title);
			d.setResizable(true);
			try {
				ZEnforcer.showModalDialogOnTop(d,requester);				
			}finally {
				d.dispose();
			}
			return null;
		}
	}.dispatchWrapRuntime();

}

public static int showComponentOKCancelDialog(final Component requester,final Component stayOnTopComponent, final String title) {
	return (Integer)
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{	
			return showComponentOKCancelDialog(requester,stayOnTopComponent,title,null);
		}
	}.dispatchWrapRuntime();
}

/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:17:45 AM)
 * @param owner java.awt.Component
 * @param message java.lang.Object
 */
private static int showComponentOKCancelDialog(final Component requester,final Component stayOnTopComponent,final String title,final OKEnabler okEnabler) {
	Component newRequester = requester;
	if (requester instanceof JTable) {
		newRequester = BeanUtils.findTypeParentOfComponent(requester, Window.class);
	}
	JOptionPane inputDialog = new JOptionPane(stayOnTopComponent, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
	final JDialog d = inputDialog.createDialog(newRequester, title);
	d.setResizable(true);
	if (okEnabler != null) {
		okEnabler.setJOptionPane(inputDialog);
	}
	try {
		ZEnforcer.showModalDialogOnTop(d,newRequester);
		if(inputDialog.getValue() instanceof Integer){
			return ((Integer)inputDialog.getValue()).intValue();
		}else if(inputDialog.getValue() == null){
			return JOptionPane.CLOSED_OPTION;
		}else if(inputDialog.getValue().equals(JOptionPane.UNINITIALIZED_VALUE)){
			return JOptionPane.CLOSED_OPTION;
		}
		throw new RuntimeException("Unexpected return value="+inputDialog.getValue().toString());
	}finally {
		d.dispose();
	}
}

public static int[] showComponentOKCancelTableList(final Component requester,final String title,
		final String[] columnNames,final Object[][] rowData,final Integer listSelectionModel_SelectMode)
			throws UserCancelException{
	return showComponentOptionsTableList(requester, title, columnNames, rowData, listSelectionModel_SelectMode, null,null,null,null).selectedTableRows;
}
public static class  TableListResult{
	public String selectedOption;
	public int[] selectedTableRows;
}
public static TableListResult showComponentOptionsTableList(final Component requester,final String title,
		final String[] columnNames,final Object[][] rowData,final Integer listSelectionModel_SelectMode,
		final ListSelectionListener listSelectionListener,
		final String[] options,final String initOption,final Comparator<Object> rowSortComparator)
			throws UserCancelException{
	
	return (TableListResult)
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			final Integer[] sortedRowReference = new Integer[rowData.length];
			for (int i = 0; i < sortedRowReference.length; i++) {
				sortedRowReference[i] = i;
			}
			DefaultSortTableModel tableModel = new DefaultSortTableModel(columnNames) {
				@Override
				public boolean isSortable(int col) {
					if (rowSortComparator != null) {
						return true;
					}
					return false;
				}

				public Object getValueAt(int row, int column) {
					return rowData[sortedRowReference[row]][column];
				}

				@Override
				public void sortColumn(final int col, final boolean ascending) {
					Arrays.sort(sortedRowReference, 
							new Comparator<Integer>() {
								public int compare(Integer o1,Integer o2) {
									if(ascending){
										return rowSortComparator.compare(rowData[o1][col], rowData[o2][col]);
									}
									return rowSortComparator.compare(rowData[o2][col],rowData[o1][col]);
								}
							}
						);
				}
			};
			tableModel.setData(Arrays.asList(rowData));
			final JTable table = new JSortTable();
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			table.setModel(tableModel);
			if(listSelectionModel_SelectMode != null){
				table.setSelectionMode(listSelectionModel_SelectMode);
			}else{
				table.setRowSelectionAllowed(false);
				table.setColumnSelectionAllowed(false);
			}
			JScrollPane scrollPane = new JScrollPane(table);
			table.setPreferredScrollableViewportSize(new Dimension(500, 250));
						
			OKEnabler tableListOKEnabler = null;
			if(listSelectionModel_SelectMode != null){
				tableListOKEnabler = new OKEnabler(){
				private JOptionPane jop;
					public void setJOptionPane(JOptionPane joptionPane) {
						jop = joptionPane;
						setInternalOKEnabled(joptionPane, false);
						table.getSelectionModel().addListSelectionListener(
								new ListSelectionListener(){
									public void valueChanged(ListSelectionEvent e) {
										if(!e.getValueIsAdjusting()){
											if(table.getSelectedRowCount() != 0){
												setInternalOKEnabled(jop, true);
											}else{
												setInternalOKEnabled(jop, false);
											}
										}
									}
								}
						);

					}
				};
			}
			if(listSelectionListener != null){
				table.getSelectionModel().addListSelectionListener(listSelectionListener);
			}
			TableListResult tableListResult = new TableListResult();
			if(options == null){
				if(showComponentOKCancelDialog(requester, scrollPane, title,tableListOKEnabler) != JOptionPane.OK_OPTION){
					throw UserCancelException.CANCEL_GENERIC;
				}
				tableListResult.selectedTableRows = table.getSelectedRows();
			}else{
				tableListResult.selectedOption = showOptionsDialog(requester, scrollPane, JOptionPane.QUESTION_MESSAGE, options, initOption,tableListOKEnabler,title);
				tableListResult.selectedTableRows = table.getSelectedRows();
			}
			if(rowSortComparator != null && tableListResult != null){
				for (int i = 0; i < tableListResult.selectedTableRows.length; i++) {
					tableListResult.selectedTableRows[i] = sortedRowReference[tableListResult.selectedTableRows[i]];
				}
			}
			return tableListResult;
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
private static String showDialog(final Component requester, final SimpleUserMessage userMessage, final String replacementText, final int JOptionPaneMessageType) {
	String message = userMessage.getMessage(replacementText);
	JPanel panel = createMessagePanel(message);
	JOptionPane pane = new JOptionPane(panel, JOptionPaneMessageType, 0, null, userMessage.getOptions(), userMessage.getDefaultSelection());
	final JDialog dialog = pane.createDialog(requester, "");
	dialog.setResizable(true);
	dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	try {
		ZEnforcer.showModalDialogOnTop(dialog,requester);
		Object selectedValue = pane.getValue();
		if(selectedValue == null || selectedValue.equals(JOptionPane.UNINITIALIZED_VALUE)) {
			return SimpleUserMessage.OPTION_CANCEL;
		} else {
			return selectedValue.toString();
		}
	}finally {
		dialog.dispose();
	}
}

private static String showOptionsDialog(final Component requester,Component showComponent,final int JOptionPaneMessageType,String[] options,String initOption,OKEnabler okEnabler,String dialogTitle) {

	JOptionPane pane = new JOptionPane(showComponent, JOptionPaneMessageType, 0, null, options, initOption);
	final JDialog dialog = pane.createDialog(requester, "");
	if(dialogTitle != null){
		dialog.setTitle(dialogTitle);
	}
	dialog.setResizable(true);
	dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	if (okEnabler != null) {
		okEnabler.setJOptionPane(pane);
	}
	try {
		ZEnforcer.showModalDialogOnTop(dialog,requester);
		Object selectedValue = pane.getValue();
		if(selectedValue == null || selectedValue.equals(JOptionPane.UNINITIALIZED_VALUE)) {
			return SimpleUserMessage.OPTION_CANCEL;
		} else {
			return selectedValue.toString();
		}
	}finally {
		dialog.dispose();
	}
}

public static void showErrorDialog(final Component requester,final String message) {
	showErrorDialog(requester, message, null);
}

/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:17:45 AM)
 * @param owner java.awt.Component
 * @param message java.lang.Object
 */
public static void showErrorDialog(final Component requester, final String message, final Throwable exception) {
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			String errMsg = message;
			boolean bSER = false;
			if (errMsg == null || errMsg.trim().length() == 0) {
				errMsg = "Virtual Cell has encountered a problem. Please tell Virtual Cell about this problem to help improve Virtual Cell.";
				if (exception != null) {
					bSER = true;
				}
			} 
			if (exception instanceof ClassCastException || exception instanceof ArrayIndexOutOfBoundsException 
					|| exception instanceof NullPointerException
					|| exception instanceof Error) {
				bSER = true;
//				String stackTrace = BeanUtils.getStackTrace(exception);
//
//				errMsg = "<html>" + errMsg +
//				"<p>We have created an error report as follows that you can send to help improve Virtual Cell. " +
//				"<p><u>Error Report</u><p>" + stackTrace + "</html>";
			}
			
			JPanel panel = createMessagePanel(errMsg);
			JOptionPane pane =  new JOptionPane(panel, JOptionPane.ERROR_MESSAGE);
			JDialog dialog = pane.createDialog(requester, "Error");
			dialog.setResizable(true);
			try{
				ZEnforcer.showModalDialogOnTop(dialog,requester);
				if (bSER && VCellClientTest.getVCellClient() != null) {
					VCellClientTest.getVCellClient().getClientServerManager().sendErrorReport(exception);
				}
			} finally{
				dialog.dispose();
			}
			return null;
		}
	}.dispatchConsumeException();
}

public static void showWarningDialog(final Component requester, final String message) {
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			final JDialog dialog = prepareWarningDialog(null, message);
			try{
				ZEnforcer.showModalDialogOnTop(dialog, requester);
			}finally{
				dialog.dispose();
			}
			return null;
		}
	}.dispatchConsumeException();
}

/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:17:45 AM)
 * @param owner java.awt.Component
 * @param message java.lang.Object
 */
public static void showInfoDialog(final Component requester, final String message) {
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			final JDialog dialog = prepareInfoDialog(requester, message);
			try{
				ZEnforcer.showModalDialogOnTop(dialog, requester);
			}finally{
				dialog.dispose();
			}
			return null;
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
public static String showInputDialog0(final Component requester,final  String message,final  String initialValue) throws UtilCancelException{
	try{
		return (String) new SwingDispatcherSync() {
			public Object runSwing() throws Exception{
				JPanel panel = new JPanel(new BorderLayout());
				JOptionPane inputDialog = new JOptionPane(null, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
				if(message.indexOf('\n') == -1){
					panel.add(new JLabel(message), BorderLayout.NORTH);
				}else{
					JPanel msgPanel = createMessagePanel(message);
					panel.add(msgPanel, BorderLayout.NORTH);
				}
				inputDialog.setMessage(panel);
				inputDialog.setWantsInput(true);
				if (initialValue!=null){
					inputDialog.setInitialSelectionValue(initialValue);
				}
				final JDialog d = inputDialog.createDialog(requester, "INPUT:");
				d.setResizable(false);
				String id = Long.toString(System.currentTimeMillis());
				Hashtable<String, Object> choices  = new Hashtable<String, Object>();
				try {
					ZEnforcer.showModalDialogOnTop(d,requester);				
					if(inputDialog.getValue() instanceof Integer && ((Integer)inputDialog.getValue()).intValue() == JOptionPane.CANCEL_OPTION){
						throw UtilCancelException.CANCEL_GENERIC;
					}
					
					choices.put(id, inputDialog.getInputValue());
				} catch (Exception exc) {
					if(exc instanceof UtilCancelException){
						throw (UtilCancelException)exc;
					}
					exc.printStackTrace(System.out);
					choices.put(id, JOptionPane.UNINITIALIZED_VALUE);
				} finally {
					d.dispose();
				}
				String input = (String)choices.get(id);
				return input == JOptionPane.UNINITIALIZED_VALUE ? null : input;
			}
		}.dispatchWithException();
	}catch(Exception e){
		if(e instanceof UtilCancelException){
			throw (UtilCancelException)e;
		}
		if(e instanceof RuntimeException){
			throw (RuntimeException)e;
		}
		throw new RuntimeException(e.getMessage(),e);
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
public static Object showListDialog(final Component requester, Object[] names, String dialogTitle) {
	return showListDialog(requester,names,dialogTitle,null);
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
public static Object showListDialog(final Component requester,final  Object[] names,final  String dialogTitle,final  ListCellRenderer listCellRenderer) {
	try{
	return
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			JPanel panel = new JPanel(new BorderLayout());
			final JOptionPane pane = new JOptionPane(null, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
			JScrollPane scroller = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			final JList list = new JList(names);
			if (listCellRenderer!=null){
				list.setCellRenderer(listCellRenderer);
			}
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			scroller.setViewportView(list);
			Dimension size = new Dimension(list.getPreferredSize());
			size.width = Math.max(80,Math.min(500,size.width+40));
			size.height = Math.max(75,Math.min(500,size.height+40));
			scroller.getViewport().setPreferredSize(size);
			panel.add(scroller, BorderLayout.CENTER);
			pane.setMessage(panel);

			setInternalOKEnabled(pane,false);
			list.addListSelectionListener(
				new javax.swing.event.ListSelectionListener(){
					  public void valueChanged(javax.swing.event.ListSelectionEvent e){
						  if(!e.getValueIsAdjusting()){
						  	DialogUtils.setInternalOKEnabled(pane,list.getSelectedIndex() != -1);
						  }
					  }
				}
			);
			
			final JDialog dialog = pane.createDialog(requester, dialogTitle);
			dialog.setResizable(true);
			try {
				ZEnforcer.showModalDialogOnTop(dialog,requester);
				Object selectedValue = pane.getValue();
				if(selectedValue == null || (((Integer)selectedValue).intValue() == JOptionPane.CLOSED_OPTION) || (((Integer)selectedValue).intValue() == JOptionPane.CANCEL_OPTION)) {
					return null;
				} else {
					int index = list.getSelectedIndex();
					if(index >= 0 && index < names.length){
						return names[index];
					}
					return null;//Should never get here
				}
			}finally {
				dialog.dispose();
			}
		}
	}.dispatchWithException();
	}catch(Exception e){
		return null;
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
protected static void showReportDialog(final Component requester, final String reportText) {
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			JPanel panel = new JPanel(new BorderLayout());
			JScrollPane scroller = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			JTextArea textArea = new JTextArea(reportText);
			scroller.setViewportView(textArea);
			scroller.getViewport().setPreferredSize(new Dimension(600, 800));
			panel.add(scroller, BorderLayout.CENTER);
			JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION); 
			final JDialog dialog = pane.createDialog(requester, "Complete Report");
			try {
				ZEnforcer.showModalDialogOnTop(dialog, requester);
				return null;
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
public static String showWarningDialog(final Component parentComponent,final  String message,final  String[] options,final  String defaultOption) {
	return (String)
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			if (parentComponent==null){
				throw new IllegalArgumentException("PopupGenerator.showWarningDialog() parentComponent cannot be null");
			}
			SimpleUserMessage simpleUserMessage = new SimpleUserMessage(message,options,defaultOption);
			return showDialog(parentComponent, simpleUserMessage, null,JOptionPane.WARNING_MESSAGE);
		}
	}.dispatchWrapRuntime();

}
}