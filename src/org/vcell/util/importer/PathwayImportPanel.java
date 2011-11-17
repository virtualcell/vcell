package org.vcell.util.importer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.Arrays;
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
import javax.swing.JToolBar;
import javax.swing.JTree;

import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.tree.BioPAXTreeMaker;
import org.vcell.sybil.util.JavaUtil;

import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayData;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;

@SuppressWarnings("serial")
public class PathwayImportPanel extends JPanel {

	protected final SelectionManager selectionManager;
	protected final PathwayImporter importer;
	protected final JTabbedPane tabbedPane = new JTabbedPane();
	protected final JFileChooser fileChooser = new JFileChooser();
	protected final WebImportPanel webImportPanel = new WebImportPanel();
	protected final ResourceImportPanel<String> examplesImportPanel = 
		new ResourceImportPanel<String>(VCellPathwayResourceDirectory.getDescriptionList());
	protected final JTextArea textArea = new JTextArea();
	protected final JScrollPane textScrollPane = new JScrollPane(textArea);
	protected final JPanel textPanel = new JPanel();
	protected final JLabel textLabel = new JLabel("[No data loaded yet]");
	protected final JPanel treePanel = new JPanel();
	protected final JLabel treeLabel = new JLabel("[No data loaded yet]");
	protected final JTree biopaxTree = new JTree(BioPAXTreeMaker.makeEmptyTree());
	protected final JScrollPane treeScrollPane = new JScrollPane(biopaxTree);
	protected final JToolBar toolBar = new JToolBar();
	protected JDialog dialog = null;
	
	protected Action showAction = new AbstractAction("Show Data") {
		public void actionPerformed(ActionEvent e) {
			final DataImportSource selectedSource = getSelectedSource();
			if(selectedSource != null) {
				AsynchClientTask readDataTask = 
					new AsynchClientTask("Reading data from " + selectedSource.getLabel(), 
							AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						if(importer.getPreviouslyReadData() == null) {
							importer.readData(getClientTaskStatusSupport());								
						}
					}
				};
				AsynchClientTask showDataTask = new AsynchClientTask("Displaying data from " + selectedSource.getLabel(),
						AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						tabbedPane.setSelectedComponent(textPanel);
						String data = importer.getPreviouslyReadData();
						String dataDescription = "Content of " + selectedSource.getLabel() + " (" + data.length() + " bytes):";
						textLabel.setText(dataDescription);
						textLabel.setToolTipText(dataDescription);
						textArea.setText(data);
						textArea.setToolTipText(dataDescription);
					}
				};
				ClientTaskDispatcher.dispatch(PathwayImportPanel.this, new Hashtable<String, Object>(), 
						new AsynchClientTask[]{ readDataTask, showDataTask}, true, true, null);
				System.out.println(selectedSource.getLabel());				
			} else {
				System.out.println("Selected source is null");
			}
		}
	};
	
	protected Action treeAction = new AbstractAction("Show Tree") {
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
				AsynchClientTask parseDataTask = new AsynchClientTask("Parsing data from " + sourceLabel,
						AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						if(importer.getPreviouslyExtractedPathwayModel() == null) {
							importer.extractPathwayModelFromPreviouslyRead(getClientTaskStatusSupport());							
						}
					}
				};
				AsynchClientTask showTreeTask = new AsynchClientTask("Showing pathway from " + sourceLabel,
						AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						tabbedPane.setSelectedComponent(treePanel);
						String data = importer.getPreviouslyReadData();
						String dataDescription = "Content of " + selectedSource.getLabel() + " (" + data.length() + " bytes):";
						treeLabel.setText(dataDescription);
						treeLabel.setToolTipText(dataDescription);
						PathwayModel pathwayModel = importer.getPreviouslyExtractedPathwayModel();
						biopaxTree.setModel(BioPAXTreeMaker.makeTree(pathwayModel));
						biopaxTree.setToolTipText(dataDescription);
					}
				};
				ClientTaskDispatcher.dispatch(PathwayImportPanel.this, new Hashtable<String, Object>(), 
						new AsynchClientTask[]{ readDataTask, parseDataTask, showTreeTask}, true, true, null);
				System.out.println(sourceLabel);				
			} else {
				System.out.println("Selected source is null");
			}
		}
	};
	
	protected Action importAction = new AbstractAction("Import Pathway") {
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
				ClientTaskDispatcher.dispatch(PathwayImportPanel.this, new Hashtable<String, Object>(), 
						new AsynchClientTask[]{ readDataTask, parseDataTask, showPathwayTask}, true, true, null);
				System.out.println(sourceLabel);				
			} else {
				System.out.println("Selected source is null");
			}
		}
	};
	
	protected Action exportAction = new AbstractAction("Export Pathway") {
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
							System.out.println(importer.getPreviouslyReadData());
							wr.write(importer.getPreviouslyReadData());
							wr.flush();
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
	
	protected Action closeAction = new AbstractAction("Close Dialog") {
		public void actionPerformed(ActionEvent e) {
			PathwayImportPanel.this.disposeDialog();
		}
	};
	
	protected final List<Action> actions = Arrays.asList(showAction, treeAction, importAction, exportAction, closeAction);
	
	public PathwayImportPanel(PathwayImporter importer, SelectionManager selectionManager) {
		this.importer = importer;
		this.selectionManager = selectionManager;
		setLayout(new BorderLayout());
		setSize(400, 300);
		add(tabbedPane);
		add(toolBar, BorderLayout.SOUTH);
		for(Action action : actions) { toolBar.add(new JButton(action)); }
		tabbedPane.addTab("Read from Web", webImportPanel);		
		fileChooser.setControlButtonsAreShown(false);
		tabbedPane.addTab("Read from File", fileChooser);
		tabbedPane.addTab("Use example", examplesImportPanel);
		textPanel.setLayout(new BorderLayout());
		textPanel.add(textScrollPane);
		textPanel.add(textLabel, BorderLayout.NORTH);
		tabbedPane.addTab("Show read data", textPanel);
		treePanel.setLayout(new BorderLayout());
		treePanel.add(treeScrollPane);
		treePanel.add(treeLabel, BorderLayout.NORTH);
		tabbedPane.addTab("Show Pathway Tree", treePanel);
//		add(new JLabel("<html>Once implemented, you will be able to choose a source of pathway data here!</html>"));
	}
	
	public DataImportSource getSelectedSource() {
		Component selectedComponent = tabbedPane.getSelectedComponent();
		if(fileChooser.equals(selectedComponent)) {
			JavaUtil.updateJFileChooser(fileChooser);
			fileChooser.approveSelection();
			File selectedFile = fileChooser.getSelectedFile();
			if(selectedFile != null) {
				importer.selectFile(selectedFile);
			}
		} else if(webImportPanel.equals(selectedComponent)) {
			URL url = webImportPanel.getURL();
			if(url != null) {
				importer.selectURL(url);
			}
		} else if(examplesImportPanel.equals(selectedComponent)) {
			String description = examplesImportPanel.getSelectedOption();
			if(description != null) {
				String path = VCellPathwayResourceDirectory.getPath(description);
				if(path != null) {
					importer.selectResource(path, description);
				}
			}
		}
		return importer.getSource();
	}
	
	public void showDialog(JComponent parent) {
		Container owner = parent.getTopLevelAncestor();
		String title = "Select Pathway Data";
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
