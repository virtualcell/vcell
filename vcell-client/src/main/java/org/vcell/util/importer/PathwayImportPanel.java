/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.importer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;

import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.tree.BioPAXTreeMaker;
import org.vcell.sybil.util.JavaUtil;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayData;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;

@SuppressWarnings("serial")
public class PathwayImportPanel extends JPanel {

	public enum PathwayImportOption {
		Web_Location,
		File,
		Example,		
	}
	protected final SelectionManager selectionManager;
	protected final PathwayImporter importer;
	protected final JFileChooser fileChooser = new JFileChooser();
	protected final WebImportPanel webImportPanel = new WebImportPanel();
	private ScrollTable exampleTable = new ScrollTable();
	private VCellSortTableModel<String> exampleTableModel = new VCellSortTableModel<String>(new String[]{"Example"}) {
		public Object getValueAt(int rowIndex, int columnIndex) {
			return getValueAt(rowIndex);
		}

		@Override
		protected Comparator<String> getComparator(int col, final boolean ascending) {
			return new Comparator<String>() {

				public int compare(String o1, String o2) {
					int scale = ascending ? 1 : -1;
					return scale * o1.compareTo(o2);
				}
			};
		}
	};
//	protected final ResourceImportPanel<String> examplesImportPanel = 
//		new ResourceImportPanel<String>(VCellPathwayResourceDirectory.getDescriptionList());
	protected final JTextArea textArea = new JTextArea();
	protected final JScrollPane textScrollPane = new JScrollPane(textArea);
	protected final JPanel textPanel = new JPanel();
	protected final JLabel textLabel = new JLabel("[No data loaded yet]");
	protected final JPanel treePanel = new JPanel();
	protected final JLabel treeLabel = new JLabel("[No data loaded yet]");
	protected final JTree biopaxTree = new JTree(BioPAXTreeMaker.makeEmptyTree());
	protected final JScrollPane treeScrollPane = new JScrollPane(biopaxTree);
	protected final JPanel toolBarPanel = new JPanel();
	protected JDialog dialog = null;
	protected JPanel previewPanel = null;
	
	protected Action previewAction = new AbstractAction("Preview") {
		public void actionPerformed(ActionEvent e) {
			final DataImportSource selectedSource = getSelectedSource();
			if(selectedSource == null) {
				DialogUtils.showErrorDialog(PathwayImportPanel.this, "no pathway is selected!");
			} else {
				final String sourceLabel = selectedSource.getLabel();
				AsynchClientTask readDataTask = 
					new AsynchClientTask("Reading data from " + sourceLabel, 
							AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						if(importer.getPreviouslyReadData() == null) {
							importer.readData(getClientTaskStatusSupport());								
						}
					}
				};
				AsynchClientTask parseDataTask = new AsynchClientTask("Parsing data from " + sourceLabel,
						AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						if(importer.getPreviouslyExtractedPathwayModel() == null) {
							importer.extractPathwayModelFromPreviouslyRead(getClientTaskStatusSupport());							
						}
					}
				};
				
				AsynchClientTask showDataTask = new AsynchClientTask("Displaying data from " + sourceLabel,
						AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						String data = importer.getPreviouslyReadData();
						String dataDescription = "Content of " + sourceLabel + " (" + data.length() + " bytes):";
						textLabel.setText(dataDescription);
						textLabel.setToolTipText(dataDescription);
						textArea.setText(data);
						textArea.setToolTipText(dataDescription);
						
						treeLabel.setText(dataDescription);
						treeLabel.setToolTipText(dataDescription);
						PathwayModel pathwayModel = importer.getPreviouslyExtractedPathwayModel();
						biopaxTree.setModel(BioPAXTreeMaker.makeTree(pathwayModel));
						biopaxTree.setToolTipText(dataDescription);
						DialogUtils.showComponentCloseDialog(dialog, getPreviewPanel(), "Preview Pathway " + sourceLabel);
					}
				};
//				ClientTaskDispatcher.dispatch(dialog, new Hashtable<String, Object>(), new AsynchClientTask[]{ readDataTask, parseDataTask, showDataTask}, false);
				ClientTaskDispatcher.dispatch(dialog, new Hashtable<String, Object>(), new AsynchClientTask[]{ readDataTask, parseDataTask, showDataTask}, null, true, false, false, null, true);
				System.out.println(sourceLabel);				
			}
		}
	};
	
	protected Action importAction = new AbstractAction("Import") {
		public void actionPerformed(ActionEvent arg0) {
			final DataImportSource selectedSource = getSelectedSource();
			if(selectedSource == null) {
				DialogUtils.showErrorDialog(PathwayImportPanel.this, "No pathway is selected!");
			} else {
				final String sourceLabel = selectedSource.getLabel();
				AsynchClientTask readDataTask = 
					new AsynchClientTask("Reading data from " + sourceLabel, 
							AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						if(importer.getPreviouslyReadData() == null) {
							importer.readData(getClientTaskStatusSupport());								
						}
					}
				};
				AsynchClientTask parseDataTask = new AsynchClientTask("Parsing data from " + sourceLabel,
						AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						if(importer.getPreviouslyExtractedPathwayModel() == null) {
							importer.extractPathwayModelFromPreviouslyRead(getClientTaskStatusSupport());							
						}
					}
				};
				AsynchClientTask showPathwayTask = new AsynchClientTask("Showing pathway from " + sourceLabel,
						AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						PathwayData pathwayData = new PathwayData(importer.getLabel(), importer.getPreviouslyExtractedPathwayModel());
						selectionManager.setSelectedObjects(new Object[] {pathwayData});
						PathwayImportPanel.this.disposeDialog();
					}
				};
				ClientTaskDispatcher.dispatch(dialog, new Hashtable<String, Object>(), new AsynchClientTask[]{ readDataTask, parseDataTask, showPathwayTask}, null, true, false, false, null, true);
				System.out.println(sourceLabel);				
			}
		}
	};
	
	private JPanel getPreviewPanel() {
		if (previewPanel == null) {
			previewPanel = new JPanel(new BorderLayout());
			previewPanel.setPreferredSize(new Dimension(500,500));
			textPanel.setLayout(new BorderLayout());		
			textPanel.add(textScrollPane, BorderLayout.CENTER);
			textPanel.add(textLabel, BorderLayout.NORTH);
			
			treePanel.setLayout(new BorderLayout());
			treePanel.add(treeScrollPane, BorderLayout.CENTER);
			treePanel.add(treeLabel, BorderLayout.NORTH);
			
			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.addTab("Raw Data", textPanel);
			tabbedPane.addTab("Tree", treePanel);
			
			previewPanel.add(tabbedPane, BorderLayout.CENTER);
		}
		return previewPanel;
	}
	
	protected Action exportAction = new AbstractAction("Export") {
		public void actionPerformed(ActionEvent arg0) {
			final DataImportSource selectedSource = getSelectedSource();
			if(selectedSource != null) {
				final String sourceLabel = selectedSource.getLabel();
				AsynchClientTask readDataTask = 
					new AsynchClientTask("Reading data from " + sourceLabel, 
							AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						if(importer.getPreviouslyReadData() == null) {
							importer.readData(getClientTaskStatusSupport());								
						}
					}
				};
				final String fileHashKey = "Selected file to export pathway";
				AsynchClientTask selectFileTask = new AsynchClientTask("Selecting file to save " + sourceLabel,
						AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
							@Override
							public void run(Hashtable<String, Object> hashTable) throws Exception {
								// TODO Auto-generated method stub
								JFileChooser fileChooser = new JFileChooser();
								int choice = fileChooser.showSaveDialog(PathwayImportPanel.this);
								if(choice == JFileChooser.APPROVE_OPTION) {
									File selectedFile = fileChooser.getSelectedFile();
									if(selectedFile != null) {
										hashTable.put(fileHashKey, selectedFile);
									}
								}
							}
					
				};
				AsynchClientTask savePathwayTask = new AsynchClientTask("Saving pathway from " + sourceLabel + " as file.",
						AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						Object selectedFileAsObject = hashTable.get(fileHashKey);
						if(selectedFileAsObject instanceof File) {
							File selectedFile = (File) selectedFileAsObject;
							FileWriter wr = new FileWriter(selectedFile);
							try {
								System.out.println(importer.getPreviouslyReadData());
								wr.write(importer.getPreviouslyReadData());
								wr.flush();
							} finally{
								wr.close();
							}
						}
					}
				};
				ClientTaskDispatcher.dispatch(PathwayImportPanel.this, new Hashtable<String, Object>(), 
						new AsynchClientTask[]{ readDataTask, selectFileTask, savePathwayTask}, true, true, null);
				System.out.println(sourceLabel);				
			} else {
				System.out.println("Selected source is null");
			}
		}
	};
	
	protected Action closeAction = new AbstractAction("Cancel") {
		public void actionPerformed(ActionEvent e) {
			PathwayImportPanel.this.disposeDialog();
		}
	};
	
	protected final List<Action> actions = Arrays.asList(importAction/*, exportAction*/, previewAction, closeAction);
	private CardLayout choosePathwayCardLayout;
	private JPanel choosePathwayPanel;
	private PathwayImportOption currentOption = null;
	
	public PathwayImportPanel(PathwayImporter importer, SelectionManager selectionManager) {
		this.importer = importer;
		this.selectionManager = selectionManager;
		exampleTable.setModel(exampleTableModel);
		exampleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		exampleTableModel.setData(VCellPathwayResourceDirectory.getDescriptionList());
		setLayout(new BorderLayout());
		setSize(400, 300);
		choosePathwayPanel = new JPanel();
		choosePathwayCardLayout = new CardLayout();
		choosePathwayPanel.setLayout(choosePathwayCardLayout);
		choosePathwayPanel.add(webImportPanel, PathwayImportOption.Web_Location.name());
		choosePathwayPanel.add(fileChooser, PathwayImportOption.File.name());
		choosePathwayPanel.add(exampleTable.getEnclosingScrollPane(), PathwayImportOption.Example.name());
		for(Action action : actions) { 
			toolBarPanel.add(new JButton(action)); 
		}
		
		fileChooser.setControlButtonsAreShown(false);
		add(choosePathwayPanel);
		add(toolBarPanel, BorderLayout.SOUTH);
	}
	
	public DataImportSource getSelectedSource() {
		switch (currentOption) {
		case Web_Location:
			URL url = webImportPanel.getURL();
			if(url != null) {
				importer.selectURL(url);
			} else {
				return null;
			}
			break;
		case File:
			JavaUtil.updateJFileChooser(fileChooser);
			fileChooser.approveSelection();
			File selectedFile = fileChooser.getSelectedFile();
			if(selectedFile != null) {
				importer.selectFile(selectedFile);
			} else {
				return null;
			}
			break;
		case Example:
			int row = exampleTable.getSelectedRow();
			if(row >= 0) {
				String description = exampleTableModel.getValueAt(row);
				String path = VCellPathwayResourceDirectory.getPath(description);
				if(path != null) {
					importer.selectResource(path, description);
				}
			} else {
				return null;
			}
			break;
		}
		return importer.getSource();
	}
	
	public void showDialog(JComponent parent, PathwayImportOption pathwayImportOption) {
		currentOption = pathwayImportOption;
		choosePathwayCardLayout.show(choosePathwayPanel, currentOption.name());

		Container owner = parent.getTopLevelAncestor();
		String title = "Select Pathway";
		boolean modal = true;
		dialog = owner instanceof Frame ? new JDialog((Frame) owner, title, modal) : 
			owner instanceof Dialog ? new JDialog((Dialog) owner, title, modal) : new JDialog((Frame) null, title, modal);
		dialog.add(this);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
	}
	
	public void disposeDialog() { if(dialog != null) { dialog.dispose(); } }
	
}
