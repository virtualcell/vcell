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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.client.logicalwindow.LWContainerHandle;
import org.vcell.client.logicalwindow.LWDialog;
import org.vcell.client.logicalwindow.LWNamespace;
import org.vcell.client.logicalwindow.LWTitledOptionPaneDialog;
import org.vcell.client.logicalwindow.transition.LWJDialogDecorator;
import org.vcell.util.BeanUtils;
import org.vcell.util.ExceptionInterpreter;
import org.vcell.util.UserCancelException;
import org.vcell.util.UtilCancelException;
import org.vcell.util.VCAssert;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.gui.MessagePanelFactory.DialogMessagePanel;
import org.vcell.util.gui.VCSwingFunction.Doer;
import org.vcell.util.gui.VCSwingFunction.Producer;
import org.vcell.util.gui.sorttable.JSortTable;

import com.centerkey.utils.BareBonesBrowserLaunch;

import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.test.VCellClientTest;
import cbit.vcell.resource.OperatingSystemInfo;

/**
 * Insert the type's description here.
 * Creation date: (5/21/2004 3:16:43 AM)
 * @author: Ion Moraru
 */
public class DialogUtils {

	/**
	 * Virtual Cell variant of {@link JOptionPane#createDialog(Component, String)} 
	 * @param parent null acceptable but discouraged
	 * @param pane not null
	 * @param title null okay
	 * @return new dialog
	 */
	public static LWDialog createDialog(LWContainerHandle parent, JOptionPane pane, String title) {
		return new LWTitledOptionPaneDialog(parent, title, pane);
	}
	
	/**
	 * Virtual Cell variant of {@link JOptionPane#createDialog(Component, String)} 
	 * @param parent null acceptable but discouraged
	 * @param pane not null
	 * @param title null okay
	 * @return new dialog
	 */
	public static LWDialog createDialog(Component parent, JOptionPane pane, String title) {
		LWContainerHandle lwParent = LWNamespace.findLWOwner(parent);
		return new LWTitledOptionPaneDialog(lwParent, title, pane);
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
					DialogUtils.setInternalNotCancelEnabled(joptionPane, selectableTreeVersionJPanel.getSelectedVersionInfo() != null, false);				
					mouseListener[0] = new MouseAdapter() {
					    public void mousePressed(MouseEvent e) {
					    	Component parentComponent = (Component)e.getSource();
					    	while(parentComponent != selectableTreeVersionJPanel && parentComponent.getParent() != null){
					    		parentComponent = parentComponent.getParent();
					    	}
					    	if(parentComponent == selectableTreeVersionJPanel){
						        if(selectableTreeVersionJPanel.getSelectedVersionInfo() != null) {
						            if(e.getClickCount() == 2) {
						            	DialogUtils.setInternalNotCancelEnabled(joptionPane, true, true);//enable OK and click OK button
						            }else{
						            	DialogUtils.setInternalNotCancelEnabled(joptionPane, true, false);//enable OK button
						            }
						        }else{
						        	DialogUtils.setInternalNotCancelEnabled(joptionPane, false, false);//disable OK button
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
	 * safely convert any object to String; equivalent to
	 * cast to String for String objects or call {@link #toString()}
	 * for non String objects
	 * @param obj input object of any type, may be null
	 * @return String or null
	 */
	private static String objectToString(Object obj) {
		if (obj != null) {
			return obj.toString();
		}
		return null;
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
		checkForNull(requester);
		LWContainerHandle lwParent = LWNamespace.findLWOwner(requester);
		Producer<String> prod = ( ) ->  {
			//
			// if userMessage is a warning that can be ignored, and the preference is to ignore it, then return default selection.
			//
			if (userMessage.getUserPreferenceWarning() > -1 && preferences!=null && !preferences.getShowWarning(userMessage.getUserPreferenceWarning())){
				return userMessage.getDefaultSelection();
			}

			String message = userMessage.getMessage(replacementText);
			JPanel panel = MessagePanelFactory.createSimple(message);
			JCheckBox checkBox = null;
			if (userMessage.getUserPreferenceWarning() >= 0){
				checkBox = new JCheckBox("Do not show this warning again");
				panel.add(checkBox, BorderLayout.SOUTH);
			}
			JOptionPane pane = new JOptionPane(panel, jOptionPaneMessageType, 0, null, userMessage.getOptions(), userMessage.getDefaultSelection());
			String title;
			switch (jOptionPaneMessageType) {
			case JOptionPane.WARNING_MESSAGE: 
				title = "WARNING:";
				break;
			case JOptionPane.ERROR_MESSAGE: 
				title = "ERROR:";
				break;
			case JOptionPane.INFORMATION_MESSAGE: 
				title = "INFO:";
				break;
			default:
					title = null;
			}
			LWDialog dialog = new LWTitledOptionPaneDialog(lwParent, title,pane);
			if(OperatingSystemInfo.getInstance().isMac()) {
				dialog.setAlwaysOnTop(true);
			}
			dialog.setResizable(true);
			try {
				dialog.setVisible(true);
				if (checkBox!=null){
					preferences.setShowWarning(userMessage.getUserPreferenceWarning(), ! checkBox.isSelected());
				}
				Object selectedValue = pane.getValue();
				if(selectedValue == null || selectedValue.equals(JOptionPane.UNINITIALIZED_VALUE)) {
					return UserMessage.OPTION_CANCEL;
				} else {
					return objectToString(selectedValue);
				}
			}finally {
				dialog.dispose();
			}
		};
		return VCSwingFunction.executeAsRuntimeException(prod);
	}

	/**
	 * @param requester parent for error dialog if required, may be null
	 * @param url to open
	 * @param messageToUserIfFail message for error dialog if required, may be null
	 */
	public static void browserLauncher(final Component requester, final String url,final String messageToUserIfFail) {
		assert url != null;
		try {
			boolean opened = false;
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
				try {
					Desktop.getDesktop().browse(java.net.URI.create(url));
					opened = true;
				}
				catch (Exception e) {}
			}
			if (!opened) {
				BareBonesBrowserLaunch.openURL(url);
			}
		}
		catch (Exception e) {
			String msg = "Sorry, your local WWW Browser could not be automatically launched.\n" + 
					"Error Type=" + e.getClass().getName() + "\nError Info='" + e.getMessage()+"'";
			if (messageToUserIfFail != null) {
				msg = messageToUserIfFail + "\n\n" + msg;
			}
			showErrorDialog(requester, msg,e); 
		}
	}


	/**
	 * get screensize including multi monitor environment 
	 * @return
	 */
	public static Dimension getScreenSize( ) {
		//http://stackoverflow.com/questions/3680221/how-can-i-get-the-monitor-size-in-java
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();	
		return new Dimension(width, height);
	}

	/**
	 * @param jop non null
	 * @param bEnabled set non-cancel button to this state
	 * @param bClickOK execute click (bEnabled must be true)
	 */
	private static void setInternalNotCancelEnabled(final JOptionPane jop,final  boolean bEnabled, boolean bClickOK) {
		VCAssert.assertTrue(!bClickOK || bEnabled, "bEnabled must be true if bClickOK is true");
	  	for(Component topLevel : jop.getComponents() ) {
	  		Container cntr = BeanUtils.downcast(Container.class, topLevel); 
	  		if (cntr != null) {
	  			for (Component cmpt : cntr.getComponents()) {
	  				JButton btn = BeanUtils.downcast(JButton.class, cmpt); 
	  				if (btn != null && !btn.getText().equalsIgnoreCase(getCancelText())) {
				  		btn.setEnabled(bEnabled);
					  	if(bEnabled && bClickOK) {
					  			btn.doClick();
					  		}
					  	}
	  				}
	  			}
	  		}
	}

	public static String showAnnotationDialog(final Component requester, final String oldAnnotation) throws Exception{
		checkForNull(requester);
		LWContainerHandle lwParent = LWNamespace.findLWOwner(requester);
		Producer<String> prod = ( ) -> {
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
			final LWDialog dialog = new LWTitledOptionPaneDialog(lwParent,"Edit Annotation:", pane);
			dialog.setResizable(true);
			try {
				dialog.setVisible(true);
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
		};

		return VCSwingFunction.execute( prod );
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (5/21/2004 3:17:45 AM)
	 * @param owner java.awt.Component
	 * @param message java.lang.Object
	 */
	public static void showComponentCloseDialog(final Component requester,final Component stayOnTopComponent,final String title) {
		checkForNull(requester);
		LWContainerHandle lwParent = LWNamespace.findLWOwner(requester);
		Doer doer = ( ) -> {
			JOptionPane inputDialog = new JOptionPane(stayOnTopComponent, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Close"});
			final JDialog d = new LWTitledOptionPaneDialog(lwParent, title, inputDialog);
			d.setResizable(true);
			try {
				d.setVisible(true);
			}finally {
				d.dispose();
			}
		}; 
		VCSwingFunction.executeAsRuntimeException(doer);
	}

	public static int showComponentOKCancelDialogx(final Component requester,final Component stayOnTopComponent, final String title) {
		return 0;

	}
	public static int showComponentOKCancelDialog(final Component requester,final Component stayOnTopComponent, final String title) {
		return showComponentOKCancelDialog(requester,stayOnTopComponent,title,null);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (5/21/2004 3:17:45 AM)
	 * @param owner java.awt.Component
	 * @param message java.lang.Object
	 */
	public static int showComponentOKCancelDialog(final Component requester,final Component stayOnTopComponent,final String title,final OKEnabler okEnabler) {
		return showComponentOKCancelDialog(requester,stayOnTopComponent,title,okEnabler, true);
	}

	public static int showComponentOKCancelDialog(final Component requester,final Component stayOnTopComponent,final String title,final OKEnabler okEnabler, boolean isResizeable) {
		checkForNull(requester);
		LWContainerHandle lwParent = LWNamespace.findLWOwner(requester);

		Producer<Integer> prod = ( ) -> {
			JOptionPane inputDialog = new JOptionPane(stayOnTopComponent, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,null,new String[] {getOKText(),getCancelText()});
			JDialog d = new LWTitledOptionPaneDialog(lwParent,title,inputDialog);
			if(OperatingSystemInfo.getInstance().isMac()) {
				d.setAlwaysOnTop(true);				
			}
			d.setResizable(isResizeable);
			if (okEnabler != null) {
				okEnabler.setJOptionPane(inputDialog);
			}
			try {
				d.setVisible(true);
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
		};

		return VCSwingFunction.executeAsRuntimeException( prod ); 
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
		Producer<TableListResult> prod = ( ) -> {
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
			@SuppressWarnings("serial")
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
						setInternalNotCancelEnabled(joptionPane, false,false);
						table.getSelectionModel().addListSelectionListener(
								new ListSelectionListener(){
									public void valueChanged(ListSelectionEvent e) {
										if(!e.getValueIsAdjusting()){
											if(table.getSelectedRowCount() != 0){
												setInternalNotCancelEnabled(jop, true,false);
											}else{
												setInternalNotCancelEnabled(jop, false,false);
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
		};

		return VCSwingFunction.executeAsRuntimeException(prod);
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
		VCellThreadChecker.checkSwingInvocation();
		checkForNull(requester);
		LWContainerHandle lwParent = LWNamespace.findLWOwner(requester);
		
		String message = userMessage.getMessage(replacementText);
		JPanel panel = MessagePanelFactory.createSimple(message);
		JOptionPane pane = new JOptionPane(panel, JOptionPaneMessageType, 0, null, userMessage.getOptions(), userMessage.getDefaultSelection());
		final JDialog dialog = new LWTitledOptionPaneDialog(lwParent, title, pane);
		if(OperatingSystemInfo.getInstance().isMac()) {
			dialog.setAlwaysOnTop(true);
		}
		dialog.setResizable(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.pack();
		try {
			dialog.setVisible(true);
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

	public static String showOptionsDialog(final Component requester,Component showComponent,final int JOptionPaneMessageType,String[] options,String initOption,OKEnabler okEnabler,String title) {
		VCellThreadChecker.checkSwingInvocation();
		checkForNull(requester);
		LWContainerHandle lwParent = LWNamespace.findLWOwner(requester);
		
		JOptionPane pane = new JOptionPane(showComponent, JOptionPaneMessageType, 0, null, options, initOption);
		final JDialog dialog =  new LWTitledOptionPaneDialog(lwParent, title, pane);
		dialog.setResizable(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		if (okEnabler != null) {
			okEnabler.setJOptionPane(pane);
		}
		try {
			dialog.setVisible(true);
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

		final String modelInfo;
		final UserPreferences userPreferences;
	}
	
	/**
	 * set visible to true, then dispose (implies modal dialog)
	 * @param c not null
	 */
	private static void showOnce(Dialog c) {
		try {
			c.setVisible(true);
		}
		finally {
			c.dispose( );
		}
	}

	/**
	 * show error dialog in standard way. If modelInfo is not null user is prompted to allow sending of context information
	 * @param requester parent Component, may be null
	 * @param message message to display
	 * @param exception exception to dialog and possibility email to VCellSupport; may be null
	 * @param modelInfo information to include in email to VCellSupport; may be null 
	 */
	public static void showErrorDialog(final Component requester, final String message, final Throwable exception, final ErrorContext errorContext) {
		checkForNull(requester);
		LWContainerHandle lwParent = LWNamespace.findLWOwner(requester);
		
		Doer doer = ( ) -> {
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

			final boolean goingToEmail = sendErrorReport && VCellClientTest.getVCellClient() != null;
			DialogMessagePanel dialogMessagePanel;
			if (goingToEmail) {
				dialogMessagePanel = MessagePanelFactory.createExtended(errMsg, errorContext);
			}
			else {
				dialogMessagePanel = MessagePanelFactory.createNonSending(errMsg);
			}
				
			
			Collection<String> suggestions = ExceptionInterpreter.instance().suggestions(message);
			if (suggestions != null) {
				SuggestionPanel sp = new SuggestionPanel();
				sp.setSuggestedSolution(suggestions);
				dialogMessagePanel.add(sp,BorderLayout.NORTH);
			}

			JOptionPane pane =  new JOptionPane(dialogMessagePanel, JOptionPane.ERROR_MESSAGE,dialogMessagePanel.optionType( ) );
			JDialog dialog = new LWTitledOptionPaneDialog(lwParent, "Error", pane);
			dialog.setResizable(true);
			try{
				dialog.setVisible(true);
				if (goingToEmail ) {
					Object ro = pane.getValue( );
					Integer reply = BeanUtils.downcast(Integer.class, ro);
					boolean userSaidYes = reply != null ? reply == JOptionPane.YES_OPTION : false;
					if (userSaidYes) {
						Throwable throwableToSend = exception; 
						String extra = dialogMessagePanel.getSupplemental();
						extra = BeanUtils.PLAINTEXT_EMAIL_NEWLINE+(extra==null?"":extra)+collectRecordedUserEvents();
						throwableToSend = new RuntimeException(extra, exception);
						VCellClientTest.getVCellClient().getClientServerManager().sendErrorReport(throwableToSend);
					}
				}
			} finally{
				dialog.dispose();
			}
		};

		VCSwingFunction.executeConsumeException(doer);
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


	public static void showWarningDialog(final Component requester, final String message) {
		checkForNull(requester);
		LWContainerHandle lwParent = LWNamespace.findLWOwner(requester);
		Doer doer = ( ) -> {
			JPanel panel = MessagePanelFactory.createSimple(message);
			JOptionPane pane = new JOptionPane(panel, JOptionPane.WARNING_MESSAGE);
			LWDialog dialog = new LWTitledOptionPaneDialog(lwParent, "Warning", pane);
			if(OperatingSystemInfo.getInstance().isMac()) {
				dialog.setAlwaysOnTop(true);
			}
			dialog.setResizable(true);
			showOnce(dialog);
		};
		VCSwingFunction.executeAsRuntimeException(doer);
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
		checkForNull(requester);
		LWContainerHandle lwParent = LWNamespace.findLWOwner(requester);
		Doer doer = ( ) -> {
			JPanel panel = MessagePanelFactory.createSimple(message);
			JOptionPane pane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE);
			if(OperatingSystemInfo.getInstance().isMac()) {
				//On Mac: problem when one keepOnTop,Modal dialogue shows another keepOnTop,Modal dialogue, second one may go behind initially
				final JDialog dialog = new JDialog(JOptionPane.getRootFrame(),true);
				pane.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent event) {
						Object nvalue = event.getNewValue();
						if (event.getPropertyName().equals("value") && nvalue != null && nvalue != JOptionPane.UNINITIALIZED_VALUE) {
							dialog.setVisible(false);
						}
					}
				});
				dialog.getContentPane().add(pane);
				dialog.pack();
				dialog.setAlwaysOnTop(true);
				dialog.setResizable(true);
				BeanUtils.centerOnComponent(dialog, requester);
				showOnce(dialog);
			}else {
				LWDialog dialog = new LWTitledOptionPaneDialog(lwParent,title,pane);
				dialog.setResizable(true);
				showOnce(dialog);
			}
		};

		VCSwingFunction.executeAsRuntimeException(doer);
	}

	/**
	 * use showInfoDialog
	 * @param requester
	 * @param title
	 * @param message
	 */
	@Deprecated
	public static void showInfoDialogAndResize(final Component requester, final String title, final String message) {
		showInfoDialog(requester,title,message);
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
		//TODO: rename showInputDialog after trunk merge
		checkForNull(requester);
		LWContainerHandle lwParent = LWNamespace.findLWOwner(requester);
		Producer<String> prod = ( ) -> {
			JPanel panel = new JPanel(new BorderLayout());
			JOptionPane inputDialog = new JOptionPane(null, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
			if(message.indexOf('\n') == -1){
				panel.add(new JLabel(message), BorderLayout.NORTH);
			}else{
				JPanel msgPanel = MessagePanelFactory.createSimple(message);
				panel.add(msgPanel, BorderLayout.NORTH);
			}
			inputDialog.setMessage(panel);
			inputDialog.setWantsInput(true);
			if (initialValue!=null){
				inputDialog.setInitialSelectionValue(initialValue);
			}
			final JDialog d = new LWTitledOptionPaneDialog(lwParent, "INPUT:", inputDialog);
			BeanUtils.centerOnComponent(d, requester);
			if(OperatingSystemInfo.getInstance().isMac()) {
				d.setAlwaysOnTop(true);
			}
			d.setResizable(false);
			String id = Long.toString(System.currentTimeMillis());
			Hashtable<String, Object> choices  = new Hashtable<String, Object>();
			try {
				d.setVisible(true);
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
		};
		try {
			return VCSwingFunction.execute(prod);
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

	public static Object showListDialog(final Component requester, final Object[] names, final String dialogTitle, final ListCellRenderer<Object> listCellRenderer) {
		return showListDialog(requester, names, dialogTitle, listCellRenderer, null);
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
	public static Object showListDialog(final Component requester,final  Object[] names,final  String dialogTitle,final  ListCellRenderer<Object> listCellRenderer, String message) {
		checkForNull(requester);
		LWContainerHandle lwParent = LWNamespace.findLWOwner(requester);
		Producer<Object> prod = ( ) -> {
			JPanel panel = new JPanel(new BorderLayout(1,2));
			if(message == null) {
				panel = new JPanel(new BorderLayout());
			} else {
				panel = new JPanel(new BorderLayout(1,2));
				JButton button = new JButton();
				Color initialBackground = button.getBackground();
				JTextPane textPane = new JTextPane();
				textPane.setText(message);
				textPane.setEditable(false);
				textPane.setBackground(initialBackground);
				panel.add(textPane, BorderLayout.NORTH);
			}
			final JOptionPane pane = new JOptionPane(null, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
			JScrollPane scroller = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			final JList<Object> list = new JList<>(names);
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

			setInternalNotCancelEnabled(pane,false,false);
			list.addListSelectionListener(
				new javax.swing.event.ListSelectionListener(){
					public void valueChanged(javax.swing.event.ListSelectionEvent e){
						if(!e.getValueIsAdjusting()){
							DialogUtils.setInternalNotCancelEnabled(pane,list.getSelectedIndex() != -1,false);
						}
					}
				}
			);

			final JDialog dialog = new LWTitledOptionPaneDialog(lwParent, dialogTitle,pane);
			dialog.setResizable(true);
			try {
				dialog.setVisible(true);
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
		};
		return VCSwingFunction.executeAsRuntimeException(prod);
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
		checkForNull(requester);
		LWContainerHandle lwParent = LWNamespace.findLWOwner(requester);
		Doer doer = ( ) -> {
			JPanel panel = new JPanel(new BorderLayout());
			JScrollPane scroller = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			JTextArea textArea = new JTextArea(reportText);
			scroller.setViewportView(textArea);
			scroller.getViewport().setPreferredSize(new Dimension(600, 400));
			panel.add(scroller, BorderLayout.CENTER);
			JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION); 
			final JDialog dialog = new LWTitledOptionPaneDialog(lwParent, "Complete Report",pane);
			showOnce(dialog);
		};
		VCSwingFunction.executeAsRuntimeException(doer);
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
		Producer<String> prod = ( ) -> {
			if (parentComponent==null){
				throw new IllegalArgumentException("PopupGenerator.showWarningDialog() parentComponent cannot be null");
			}
			SimpleUserMessage simpleUserMessage = new SimpleUserMessage(message,options,defaultOption);
			return showDialog(parentComponent, title, simpleUserMessage, null,JOptionPane.WARNING_MESSAGE);
		};
		return VCSwingFunction.executeConsumeException(prod);
	}

	public static String showOKCancelWarningDialog(final Component parentComponent, final String title, final String message) {
		Producer<String> prod = ( ) -> {
			if (parentComponent==null){
				throw new IllegalArgumentException("PopupGenerator.showWarningDialog() parentComponent cannot be null");
			}
			SimpleUserMessage simpleUserMessage = new SimpleUserMessage(message, new String[] {SimpleUserMessage.OPTION_OK, SimpleUserMessage.OPTION_CANCEL}, SimpleUserMessage.OPTION_OK);
			return showDialog(parentComponent, title, simpleUserMessage, null, JOptionPane.WARNING_MESSAGE);
		};
		return VCSwingFunction.executeAsRuntimeException(prod);
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

	/**
	 * @deprecated -- use setVisible on LWDialog
	 * @param jdialog
	 * @param component 
	 */
	public static void showModalJDialogOnTop(JDialog jdialog, Component component) {
		if (jdialog instanceof LWDialog) {
			jdialog.setVisible(true);
			return;
		}
		LWContainerHandle lwParent = LWNamespace.findLWOwner(component);
		LWJDialogDecorator deco = LWJDialogDecorator.decoratorFor(jdialog);
		lwParent.manage(deco);
		deco.getWindow().setVisible(true);
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

	public static int findScreen(JDialog dialog) {
		GraphicsConfiguration config = dialog.getGraphicsConfiguration();
		GraphicsDevice myScreen = config.getDevice();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] allScreens = env.getScreenDevices();
		int myScreenIndex = -1;
		for (int i = 0; i < allScreens.length; i++) {
			if (allScreens[i].equals(myScreen)) {
				myScreenIndex = i;
				break;
			}
		}
		System.out.println("Dialog is on screen " + myScreenIndex);
		return myScreenIndex;
	}
	public static Rectangle getCurrentScreenBounds(Component component) {
		return component.getGraphicsConfiguration().getBounds();
	}
	
	/**
	 * temporarily print error message for null input Components -- should eventually be an Exception
	 * @param c
	 */
	private static void checkForNull(Component c) {
		if (c == null) {
			StringBuilder sb = new StringBuilder("Null component passed to DialogUtils");
			for (StackTraceElement ste :Thread.currentThread().getStackTrace()) {
				sb.append(ste);
				sb.append('\n');
			}
			System.err.println(sb.toString());
		}
	}

}
