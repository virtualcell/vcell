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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Icon;
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
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.BeanUtils;
import org.vcell.util.ExceptionInterpreter;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.test.VCellClientTest;
/**
 * Insert the type's description here.
 * Creation date: (5/21/2004 3:16:43 AM)
 * @author: Ion Moraru
 */
public class DialogUtils {
	public final static String SHARE_MODEL_TEXT =
		"Sending your model information will improve the ability of the Virtual Cell Support team to diagnose " +
		"and repair transient software bugs which have escaped release testing. We may use the information you send to " +
		"make copies of your models for the sole purpose of reproducing the bugs you've encountered.";
	
	private static abstract class SwingDispatcherSync {

		private Object returnValue = null;
		
		@SuppressWarnings("serial")
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
	
	public interface SelectableTreeVersionJPanel {
		void addJTreeSelectionMouseListener(MouseListener jtreeSelectionMouseListener);
		void removeJTreeSelectionMouseListener(MouseListener jtreeSelectionMouseListener);
		VersionInfo getSelectedVersionInfo();
	}

	/**
	 * Return VersionInfo from JPanels implementing SelectableTreeVersionJPanel
	 * including MathModelDbTreePanel, BioModelDbTreePanel, GeometryTreePanel and ImageDbTreePanel
	 * 
	 * @param requester						parent component for dialog
	 * @param selectableTreeVersionJPanel	component having a JTree user can select to define a VersionInfo
	 * @param okString						label for button user presses when selection complete
	 * @param dialogTitle					string displayed at top of dialog
	 * @return	VersionInfo					identifies a particular VCell document
	 * @throws UserCancelException
	 * @throws IllegalArgumentException
	 */
	public static VersionInfo getDBTreePanelSelection(Component requester,SelectableTreeVersionJPanel selectableTreeVersionJPanel,String okString,String dialogTitle) throws UserCancelException,IllegalArgumentException{
		if(!(selectableTreeVersionJPanel instanceof JPanel)){
			throw new IllegalArgumentException("selectableTreeVersionJPanel must be of type JPanel");
		}
		final MouseListener[] mouseListener = new MouseListener[] {null};
//		final JTree[] listenToJTree = new JTree[] {null};
		try{
			JPanel defaultSizedJPanel = new JPanel();
			defaultSizedJPanel.setLayout(new BorderLayout());
			defaultSizedJPanel.add((JPanel)selectableTreeVersionJPanel,BorderLayout.CENTER);
			Dimension dim = new Dimension(300, 400);
			defaultSizedJPanel.setPreferredSize(dim);
			defaultSizedJPanel.setMinimumSize(dim);
			defaultSizedJPanel.setMaximumSize(dim);
			String result = DialogUtils.showOptionsDialog(requester, defaultSizedJPanel, JOptionPane.PLAIN_MESSAGE, new String[] {okString, getCancelText()}, okString,new DialogUtils.OKEnabler() {
				@Override
				public void setJOptionPane(JOptionPane joptionPane) {
					DialogUtils.setInternalOKEnabled(joptionPane, selectableTreeVersionJPanel.getSelectedVersionInfo() != null, false);				
					mouseListener[0] = new MouseAdapter() {
					    public void mousePressed(MouseEvent e) {
					    	Component parentComponent = (Component)e.getSource();
					    	while(parentComponent != selectableTreeVersionJPanel && parentComponent.getParent() != null){
					    		parentComponent = parentComponent.getParent();
					    	}
					    	if(parentComponent == selectableTreeVersionJPanel){
						        if(selectableTreeVersionJPanel.getSelectedVersionInfo() != null) {
						            if(e.getClickCount() == 2) {
						            	DialogUtils.setInternalOKEnabled(joptionPane, true, true);//enable OK and click OK button
						            }else{
						            	DialogUtils.setInternalOKEnabled(joptionPane, true, false);//enable OK button
						            }
						        }else{
						        	DialogUtils.setInternalOKEnabled(joptionPane, false, false);//disable OK button
						        }
					    	}
					    }
					};
					selectableTreeVersionJPanel.addJTreeSelectionMouseListener(mouseListener[0]);
				}
			}, dialogTitle);
			if(result == null || !result.equals(okString)){
				throw UserCancelException.CANCEL_GENERIC;
			}		
			return selectableTreeVersionJPanel.getSelectedVersionInfo();
		}finally{
			selectableTreeVersionJPanel.removeJTreeSelectionMouseListener(mouseListener[0]);
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
					DialogUtils.showModalJDialogOnTop(dialog,requester);
					if (checkBox!=null){
						preferences.setShowWarning(userMessage.getUserPreferenceWarning(), ! checkBox.isSelected());
					}
					Object selectedValue = pane.getValue();
					if(selectedValue == null || selectedValue.equals(JOptionPane.UNINITIALIZED_VALUE)) {
						return UserMessage.OPTION_CANCEL;
					} else {
						return selectedValue.toString();
					}
				}finally {
					dialog.dispose();
				}
			}
		}.dispatchWrapRuntime();
	}

	
	public static void openURLWithExternalBrowser(String url) throws Exception {
		Desktop.getDesktop().browse(new URL(url).toURI());
	}
	
	
/**
 * Insert the method's description here.
 * Creation date: (8/26/2005 3:26:35 PM)
 */
public static void browserLauncher(final Component requester, final String targetURL,final String messageToUserIfFail,final boolean isApplet) {
	
	
	//(messageToUserIfFail)
	//  Should provide the user with the URL they can
	//  manually navigate to for the information requested

	try {
		openURLWithExternalBrowser(targetURL);
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


@SuppressWarnings("serial")
private static class JPanelWithCb extends JPanel {
	JPanelWithCb( ) {
		super(new BorderLayout());
	}
	JCheckBox allowSendCb;
}

private static JPanelWithCb createMessagePanel(final String message) {
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
	JPanelWithCb panel = new JPanelWithCb();
	scroller.setViewportView(textArea);
	scroller.getViewport().setPreferredSize(preferredSize);
	panel.add(scroller, BorderLayout.CENTER);
	return panel;
}

/**
 * add send model info to VCellSupport panel to JPanelWithCb (see {@link #createMessagePanel(String)}
 * @param panel
 * @param initialCheckboxState
 */
private static void addModelSendInformation(JPanelWithCb panel, boolean initialCheckboxState) {
		JPanel buttonPanel = new JPanel();
		panel.add(buttonPanel, BorderLayout.SOUTH);
		JPanel sendModelPanel = new JPanel();
		buttonPanel.add(sendModelPanel);
		
		panel.allowSendCb = new JCheckBox("Send model info to VCell support team");
		sendModelPanel.add(panel.allowSendCb);
		
		JButton helpBtn = new JButton(DialogUtils.swingIcon(JOptionPane.QUESTION_MESSAGE));
		helpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Component c = (Component) e.getSource();
				DialogUtils.showInfoDialog(c,SHARE_MODEL_TEXT);
			}
		});
		sendModelPanel.add(helpBtn);
		panel.allowSendCb.setSelected(initialCheckboxState);
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
private static JDialog prepareInfoDialog(final Component requester,final String title, final String message) {
	JPanel panel = createMessagePanel(message);
	JOptionPane pane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE);
	JDialog dialog = pane.createDialog(requester, title);
	dialog.setResizable(true);
	return dialog;
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2005 2:01:06 PM)
 * @param jop javax.swing.JOptionPane
 * @param bEnabled boolean
 */
private static void setInternalOKEnabled(final JOptionPane jop,final  boolean bEnabled, boolean bClickOK) {
  	Component[] componentsArr = jop.getComponents();
  	for(int i=0;i<componentsArr.length;i+= 1){
	  	if(componentsArr[i] instanceof Container){
		  	for(int j=0;j<((Container)componentsArr[i]).getComponentCount();j+= 1){
			  	if(((Container)componentsArr[i]).getComponent(j) instanceof JButton &&
				  	!((JButton)((Container)componentsArr[i]).getComponent(j)).getText().equalsIgnoreCase(getCancelText())){
				  	if(bEnabled){
					  	((Container)componentsArr[i]).getComponent(j).setEnabled(true);
					  	if(bClickOK){
					  		((JButton)((Container)componentsArr[i]).getComponent(j)).doClick();
					  	}
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
				DialogUtils.showModalJDialogOnTop(dialog,requester);
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
				DialogUtils.showModalJDialogOnTop(d, requester);				
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
public static int showComponentOKCancelDialog(final Component requester,final Component stayOnTopComponent,final String title,final OKEnabler okEnabler) {
	Component newRequester = requester;
	if (requester instanceof JTable) {
		newRequester = BeanUtils.findTypeParentOfComponent(requester, Window.class);
	}
	JOptionPane inputDialog = new JOptionPane(stayOnTopComponent, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,null,new String[] {getOKText(),getCancelText()});
	JDialog d = inputDialog.createDialog(newRequester, title);
	d.setResizable(true);
	if (okEnabler != null) {
		okEnabler.setJOptionPane(inputDialog);
	}
	try {
		DialogUtils.showModalJDialogOnTop(d, newRequester);
		if(inputDialog.getValue() instanceof String){
			if(inputDialog.getValue().equals(getOKText())){
				return JOptionPane.OK_OPTION;
			}if(inputDialog.getValue().equals(getCancelText())){
				return JOptionPane.CANCEL_OPTION;
			}
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

private static String getOKText(){
	return UIManager.getString("OptionPane.okButtonText",Locale.getDefault());
}
private static String getCancelText(){
	return UIManager.getString("OptionPane.cancelButtonText",Locale.getDefault());
}

public static int[] showComponentOKCancelTableList(final Component requester,final String title,
		final String[] columnNames,final Object[][] rowData,final Integer listSelectionModel_SelectMode)
			throws UserCancelException{
	return showComponentOptionsTableList(requester, title, columnNames, rowData, listSelectionModel_SelectMode, null,null,null,null).selectedTableRows;
}
public static TableListResult showComponentOptionsTableList(final Component requester,final String title,
		final String[] columnNames,final Object[][] rowDataOrig,final Integer listSelectionModel_SelectMode,
		final ListSelectionListener listSelectionListener,
		final String[] options,final String initOption,final Comparator<Object> rowSortComparator)
			throws UserCancelException{
	return showComponentOptionsTableList(requester, title, columnNames, rowDataOrig, listSelectionModel_SelectMode, listSelectionListener,options,initOption,rowSortComparator,true);
}
public static class  TableListResult{
	public String selectedOption;
	public int[] selectedTableRows;
}

public static TableListResult showComponentOptionsTableList(final Component requester,final String title,
		final String[] columnNames,final Object[][] rowDataOrig,final Integer listSelectionModel_SelectMode,
		final ListSelectionListener listSelectionListener,
		final String[] options,final String initOption,final Comparator<Object> rowSortComparator,final boolean bModal)
			throws UserCancelException{
	
//	//Create hidden column with original row index so original row index can
//	//be returned for user selections even if rows are sorted
//	final int hiddenColumnIndex = rowDataOrig[0].length;
//	final Object[][] rowDataHiddenIndex = new Object[rowDataOrig.length][hiddenColumnIndex+1];
//	for (int i = 0; i < rowDataHiddenIndex.length; i++) {
//		for (int j = 0; j < rowDataOrig[i].length; j++) {
//			rowDataHiddenIndex[i][j] = rowDataOrig[i][j];
//		}
//		rowDataHiddenIndex[i][hiddenColumnIndex] = i;
//	}
	return (TableListResult)
	new SwingDispatcherSync (){
		@SuppressWarnings("serial")
		public Object runSwing() throws Exception{
			//Create hidden column with original row index so original row index can
			//be returned for user selections even if rows are sorted
			int hiddenColumnIndex = rowDataOrig[0].length;
			Object[][] rowDataHiddenIndex = rowDataOrig;
			if(rowSortComparator != null){
				rowDataHiddenIndex = new Object[rowDataOrig.length][hiddenColumnIndex+1];
				for (int i = 0; i < rowDataHiddenIndex.length; i++) {
					for (int j = 0; j < rowDataOrig[i].length; j++) {
						rowDataHiddenIndex[i][j] = rowDataOrig[i][j];
					}
					rowDataHiddenIndex[i][hiddenColumnIndex] = i;
				}
			}
			VCellSortTableModel<Object[]> tableModel = new VCellSortTableModel<Object[]>(columnNames, rowDataOrig.length) {
				@Override
				public boolean isSortable(int col) {
					if (rowSortComparator != null) {
						return true;
					}
					return false;
				}

				public Object getValueAt(int row, int column) {
					return getValueAt(row)[column];
				}

				@Override
				public Comparator<Object[]> getComparator(final int col, final boolean ascending) {
					return new Comparator<Object[]>() {
						public int compare(Object[] o1,Object[] o2) {
							if(ascending){
								return rowSortComparator.compare(o1[col], o2[col]);
							}
							return rowSortComparator.compare(o2[col],o1[col]);
						}
					};
				}
			};
			final JSortTable table = new JSortTable();
//			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			table.setModel(tableModel);
			tableModel.setData(Arrays.asList(rowDataHiddenIndex));
			if(listSelectionModel_SelectMode != null){
				table.setSelectionMode(listSelectionModel_SelectMode);
			}else{
				table.setRowSelectionAllowed(false);
				table.setColumnSelectionAllowed(false);
			}
			table.setPreferredScrollableViewportSize(new Dimension(500, 250));
			table.disableUneditableForeground();
						
			OKEnabler tableListOKEnabler = null;
			if(listSelectionModel_SelectMode != null){
				tableListOKEnabler = new OKEnabler(){
				private JOptionPane jop;
					public void setJOptionPane(JOptionPane joptionPane) {
						jop = joptionPane;
						setInternalOKEnabled(joptionPane, false, false);
						table.getSelectionModel().addListSelectionListener(
								new ListSelectionListener(){
									public void valueChanged(ListSelectionEvent e) {
										if(!e.getValueIsAdjusting()){
											if(table.getSelectedRowCount() != 0){
												setInternalOKEnabled(jop, true, false);
											}else{
												setInternalOKEnabled(jop, false, false);
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
			
			
			// HACK to fix horizontal scrollbar not showing for large horizontal tables
			// workaround code from: http://bugs.sun.com/view_bug.do?bug_id=4127936
			final JScrollPane[] jScrollPaneArr = new JScrollPane[1];
			Component[] components = table.getEnclosingScrollPane().getComponents();
			for (int i = 0; i < components.length; i++) {
				if(components[i] instanceof JScrollPane){
					jScrollPaneArr[0] = (JScrollPane)components[i];
					break;
				}
			}
			if(jScrollPaneArr[0] != null){
				jScrollPaneArr[0].addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					super.componentResized(e);
					if (table.getPreferredSize().width <= jScrollPaneArr[0].getViewport().getExtentSize().width){
						table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
					} else {
						table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					}
				}
				});
			}
			
			if(options == null){
				if(bModal){
					if(showComponentOKCancelDialog(requester, table.getEnclosingScrollPane(), title,tableListOKEnabler) != JOptionPane.OK_OPTION){
						throw UserCancelException.CANCEL_GENERIC;
					}
				}else{
					//display non-modal
					JOptionPane jOptionPane = new JOptionPane(table.getEnclosingScrollPane(),JOptionPane.INFORMATION_MESSAGE);
					JDialog jDialog = jOptionPane.createDialog(requester, title);
					jDialog.setResizable(true);
					jDialog.setModal(false);
					jDialog.pack();
					jDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					jDialog.setVisible(true);					
				}
				tableListResult.selectedTableRows = table.getSelectedRows();
			}else{
				tableListResult.selectedOption = showOptionsDialog(requester, table.getEnclosingScrollPane(), JOptionPane.QUESTION_MESSAGE, options, initOption,tableListOKEnabler,title);
				tableListResult.selectedTableRows = table.getSelectedRows();
			}
			if(rowSortComparator != null){
				//return the index of the original unsorted object array that corresponds to the user row selections
				for (int i = 0; i < tableListResult.selectedTableRows.length; i++) {
					tableListResult.selectedTableRows[i] = (Integer)(tableModel.getValueAt(tableListResult.selectedTableRows[i])[hiddenColumnIndex]);
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
private static String showDialog(final Component requester, String title, final SimpleUserMessage userMessage, final String replacementText, final int JOptionPaneMessageType) {
	String message = userMessage.getMessage(replacementText);
	JPanel panel = createMessagePanel(message);
	JOptionPane pane = new JOptionPane(panel, JOptionPaneMessageType, 0, null, userMessage.getOptions(), userMessage.getDefaultSelection());
	final JDialog dialog = pane.createDialog(requester, title);
	dialog.setResizable(true);
	dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	try {
		DialogUtils.showModalJDialogOnTop(dialog, requester);
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
		DialogUtils.showModalJDialogOnTop(dialog, requester);
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
 * show error dialog in standard way
 * @param requester parent Component, may be null
 * @param message message to display
 * @param exception exception to dialog and possibility email to VCellSupport; may be null
 */
public static void showErrorDialog(final Component requester, final String message, final Throwable exception) {
	showErrorDialog(requester,message,exception,null);
}

public static class ErrorContext {
	/**
	 * information for enhanced ErrorDialog 
	 * @param modelInfo may not be null
	 * @param userPreferences may be null, will use {@link UserPreferences#getLastUserPreferences()}
	 */
	public ErrorContext(String modelInfo, UserPreferences userPreferences) {
		if (modelInfo == null) {
			throw new IllegalArgumentException("null point passed to ErrorContextInformation");
		}
		this.modelInfo = modelInfo;
		if (userPreferences != null) {
			this.userPreferences = userPreferences;
		}
		else {
			this.userPreferences = UserPreferences.getLastUserPreferences();
		}
	}
	/**
	 * information for enhanced ErrorDialog 
	 * @param modelInfo may not be null
	 */
	public ErrorContext(String modelInfo) {
		this(modelInfo,null);
	}
	
	/**
	 * can this ErrorContext be used? 
	 * @return true if it can
	 */
	public boolean canUse( ) {
		return userPreferences != null;
	}
	
	private final String modelInfo;
	private final UserPreferences userPreferences;
}


/**
 * show error dialog in standard way. If modelInfo is not null user is prompted to allow sending of context information
 * @param requester parent Component, may be null
 * @param message message to display
 * @param exception exception to dialog and possibility email to VCellSupport; may be null
 * @param modelInfo information to include in email to VCellSupport; may be null 
 */
public static void showErrorDialog(final Component requester, final String message, final Throwable exception, 
		final ErrorContext errorContext) {
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			final boolean haveContext = errorContext != null && errorContext.canUse();
			String errMsg = message;
			boolean sendErrorReport = false;
			if (errMsg == null || errMsg.trim().length() == 0) {
				errMsg = "Virtual Cell has encountered a problem. Please tell Virtual Cell about this problem to help improve Virtual Cell.";
				if (exception != null) {
					sendErrorReport = true;
				}
			} 
			if (exception instanceof ClassCastException || exception instanceof ArrayIndexOutOfBoundsException 
					|| exception instanceof NullPointerException
					|| exception instanceof Error) {
				sendErrorReport = true;
			}
			
			boolean initialCheckState = false;
			final boolean goingToEmail = sendErrorReport && VCellClientTest.getVCellClient() != null;
			JPanelWithCb panel = createMessagePanel(errMsg);
			if (goingToEmail && haveContext) {
				initialCheckState = errorContext.userPreferences.getGenPrefBoolean(UserPreferences.SEND_MODEL_INFO_IN_ERROR_REPORT);
				addModelSendInformation(panel, initialCheckState);
			}
			Collection<String> suggestions = ExceptionInterpreter.instance().suggestions(message);
			if (suggestions != null) {
				SuggestionPanel sp = new SuggestionPanel();
				sp.setSuggestedSolution(suggestions);
				panel.add(sp,BorderLayout.NORTH);
			}
				
			JOptionPane pane =  new JOptionPane(panel, JOptionPane.ERROR_MESSAGE);
			JDialog dialog = pane.createDialog(requester, "Error");
			dialog.setResizable(true);
			try{
				DialogUtils.showModalJDialogOnTop(dialog, requester);
				if (goingToEmail) {
					Throwable throwableToSend = null; 
					if (haveContext) {
						final boolean userSelectedSendModel = panel.allowSendCb.isSelected();
						if (userSelectedSendModel) {
							assert(errorContext != null);
							throwableToSend = new RuntimeException(errorContext.modelInfo + BeanUtils.PLAINTEXT_EMAIL_NEWLINE + collectRecordedUserEvents(),exception);
						}
						else {
							throwableToSend = new RuntimeException("User declined to share model" + BeanUtils.PLAINTEXT_EMAIL_NEWLINE + collectRecordedUserEvents(),exception);
						}
						VCellClientTest.getVCellClient().getClientServerManager().sendErrorReport(throwableToSend);
						if (userSelectedSendModel != initialCheckState) {
							errorContext.userPreferences.setGenPref(UserPreferences.SEND_MODEL_INFO_IN_ERROR_REPORT,userSelectedSendModel);
						}
					}
					else {
						assert(haveContext == false);
						throwableToSend = new RuntimeException("No context provided" + BeanUtils.PLAINTEXT_EMAIL_NEWLINE + collectRecordedUserEvents(),exception);
						VCellClientTest.getVCellClient().getClientServerManager().sendErrorReport(throwableToSend);
					}
					
				}
			} finally{
				dialog.dispose();
			}
			return null;
		}
	}.dispatchConsumeException();
}

public static String collectRecordedUserEvents(){
	String content = "";
	if(VCellClientTest.recordedUserEvents != null && VCellClientTest.recordedUserEvents.size() > 0){
		content+= "-----Recorded User Events-----"+BeanUtils.PLAINTEXT_EMAIL_NEWLINE;
		Iterator<String> iter = VCellClientTest.recordedUserEvents.iterator();
		while(iter.hasNext()){
			content+= iter.next()+BeanUtils.PLAINTEXT_EMAIL_NEWLINE;
		}
		content+= "------------------------------"+BeanUtils.PLAINTEXT_EMAIL_NEWLINE;;
	}
	return content;
}


/**
 * subclass to carry supplemental information about context of exception
 * currently unused until deployed in postprocessors
 */
@SuppressWarnings({ "serial", "unused" })
private static class ContextCarrierException extends RuntimeException {
	ContextCarrierException(String msg, Throwable payload) {
		super(msg,payload);
	}
}

public static void showWarningDialog(final Component requester, final String message) {
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			final JDialog dialog = prepareWarningDialog(requester, message);
			try{
				DialogUtils.showModalJDialogOnTop(dialog, requester);
			}finally{
				dialog.dispose();
			}
			return null;
		}
	}.dispatchConsumeException();
}

public static void showInfoDialog(final Component requester, final String message) {
	showInfoDialog(requester, "Info", message);
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:17:45 AM)
 * @param owner java.awt.Component
 * @param message java.lang.Object
 */
public static void showInfoDialog(final Component requester, final String title, final String message) {
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			final JDialog dialog = prepareInfoDialog(requester, title, message);
			try{
				DialogUtils.showModalJDialogOnTop(dialog, requester);
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
					DialogUtils.showModalJDialogOnTop(d,requester);				
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
@SuppressWarnings("rawtypes")
public static Object showListDialog(final Component requester,final  Object[] names,final  String dialogTitle,final  ListCellRenderer listCellRenderer) {
	try{
	return
	new SwingDispatcherSync (){
		@SuppressWarnings({ "unchecked" })
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

			setInternalOKEnabled(pane,false,false);
			list.addListSelectionListener(
				new javax.swing.event.ListSelectionListener(){
					  public void valueChanged(javax.swing.event.ListSelectionEvent e){
						  if(!e.getValueIsAdjusting()){
						  	DialogUtils.setInternalOKEnabled(pane,list.getSelectedIndex() != -1,false);
						  }
					  }
				}
			);
			
			final JDialog dialog = pane.createDialog(requester, dialogTitle);
			dialog.setResizable(true);
			try {
				DialogUtils.showModalJDialogOnTop(dialog,requester);
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
			scroller.getViewport().setPreferredSize(new Dimension(600, 400));
			panel.add(scroller, BorderLayout.CENTER);
			JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION); 
			final JDialog dialog = pane.createDialog(requester, "Complete Report");
			try {
				DialogUtils.showModalJDialogOnTop(dialog, requester);
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
	return showWarningDialog(parentComponent, "", message, options, defaultOption);
}

public static String showWarningDialog(final Component parentComponent,final String title, final String message, final  String[] options,final  String defaultOption) {
	return (String)
	new SwingDispatcherSync (){
		public Object runSwing() throws Exception{
			if (parentComponent==null){
				throw new IllegalArgumentException("PopupGenerator.showWarningDialog() parentComponent cannot be null");
			}
			SimpleUserMessage simpleUserMessage = new SimpleUserMessage(message,options,defaultOption);
			return showDialog(parentComponent, title, simpleUserMessage, null,JOptionPane.WARNING_MESSAGE);
		}
	}.dispatchWrapRuntime();

}

public static String showOKCancelWarningDialog(final Component parentComponent, final String title, final String message) {
	if (parentComponent==null){
		throw new IllegalArgumentException("PopupGenerator.showWarningDialog() parentComponent cannot be null");
	}
	SimpleUserMessage simpleUserMessage = new SimpleUserMessage(message, new String[] {SimpleUserMessage.OPTION_OK, SimpleUserMessage.OPTION_CANCEL}, SimpleUserMessage.OPTION_OK);
	if (SwingUtilities.isEventDispatchThread()) {
		return showDialog(parentComponent, title, simpleUserMessage, null, JOptionPane.WARNING_MESSAGE);
	}
	else {
		return (String)
				new SwingDispatcherSync (){
			public Object runSwing() throws Exception{
				return showDialog(parentComponent, title, simpleUserMessage, null, JOptionPane.WARNING_MESSAGE);
			}
		}.dispatchWrapRuntime();	
	}
}
/**
 * call {@link #showOKCancelWarningDialog(Component, String, String)}, return true if user presses okay
 * @param parentComponent
 * @param title
 * @param message
 * @return true if user says OK
 */
public static boolean queryOKCancelWarningDialog(final Component parentComponent, final String title, final String message) {
	String ret = showOKCancelWarningDialog(parentComponent, title, message);
	return ret == SimpleUserMessage.OPTION_OK;
}

public static void showModalJDialogOnTop(JDialog jdialog, Component component) {
	jdialog.setModal(true);
	jdialog.setVisible(true);
}

public static Icon swingIcon(int optionPaneValue){
	String key;
	switch (optionPaneValue) {
	case JOptionPane.QUESTION_MESSAGE:
		key= "questionIcon";
		break;
	case JOptionPane.WARNING_MESSAGE:
		key= "warningIcon";
		break;
	case JOptionPane.ERROR_MESSAGE:
		key= "errorIcon";
		break;
	case JOptionPane.INFORMATION_MESSAGE:
		key= "informationIcon";
		break;
		default:
			throw new UnsupportedOperationException("DialogUtils.swingIcon value " + optionPaneValue);
	}
	Icon i = UIManager.getIcon("OptionPane." + key);
	return i;
}

}
