/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.client.logicalwindow.LWNamespace;
import org.vcell.util.Issue;
import org.vcell.util.Issue.Severity;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.JTabbedPaneEnhanced;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.DatabaseWindowPanel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderNode;
import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEvent;
import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEventListener;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.mathmodel.MathModelEditor;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.desktop.BioModelMetaDataPanel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.desktop.GeometryMetaDataPanel;
import cbit.vcell.desktop.MathModelMetaDataPanel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 2:55:18 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public abstract class DocumentEditor extends JPanel {
	public static final String TAB_TITLE_PROBLEMS = "Problems";
	private static final String TAB_TITLE_OBJECT_PROPERTIES = "Object Properties";
	private static final String TAB_TITLE_ANNOTATIONS = "Annotations";
	protected static final String generalTreeNodeDescription = "Select only one object (e.g. species, reaction, simulation) to view/edit properties.";
	protected static final Logger LG = LogManager.getLogger(DocumentEditor.class);
	
	protected enum DocumentEditorTabID {
		object_properties,
		annotations,
		problems,
	}
	protected enum DocumentEditorPopupMenuAction {
		add_new,
		add_new_app_deterministic,
		add_new_app_stochastic,
		add_new_app_rulebased,
		copy_app,
		rename,
		delete,
		app_new_biomodel,
		deleteChoose,
	}
	protected static final String DATABASE_PROPERTIES_TAB_TITLE = "Database File Info";
	protected IvjEventHandler eventHandler = new IvjEventHandler();
	
	protected JTree documentEditorTree = null;
	protected SelectionManager selectionManager = new SelectionManager();
	protected IssueManager issueManager = new IssueManager();

	protected JPanel emptyPanel = new JPanel();
	private JPopupMenu popupMenu = null;
	private JMenu addNewAppMenu = null;
	private JMenuItem removeAppsMenu = null;
	private JMenuItem addNewAppDeterministicMenuItem = null;
	private JMenuItem addNewAppStochasticMenuItem = null;
	private JMenuItem addNewAppRulebasedMenuItem = null;
	private JMenuItem expandAllMenuItem = null;
	private JMenuItem collapseAllMenuItem = null;
	private JMenuItem addNewMenuItem;
	private JMenuItem renameMenuItem;
	private JMenuItem deleteMenuItem;
	
	protected DatabaseWindowPanel databaseWindowPanel = null;
	protected JTabbedPane leftBottomTabbedPane = null;
	protected JSplitPane rightSplitPane = null;
	protected BioModelMetaDataPanel bioModelMetaDataPanel = null;
	protected MathModelMetaDataPanel mathModelMetaDataPanel = null;
	protected GeometryMetaDataPanel geometryMetaDataPanel = null;
	
	protected JTabbedPane rightBottomTabbedPane = null;
	protected JPanel rightBottomEmptyPanel = null;
	protected JPanel rightBottomEmptyAnnotationsPanel = null;
	private JSeparator popupMenuSeparator = null;
	private DocumentEditorTreeCellEditor documentEditorTreeCellEditor;
	protected JLabel treeNodeDescriptionLabel;
	protected IssuePanel issuePanel;
	
	private JMenuItem menuItemNewBiomodelFromApp = null;
	private JMenuItem menuItemAppCopy = null;
	private JMenu menuAppCopyAs = null;
	private JMenuItem menuItemNonSpatialCopyStochastic = null;
	private JMenuItem menuItemNonSpatialCopyDeterministic = null;
	private JMenuItem menuItemNonSpatialCopyRulebased = null;
	
	private JMenu menuSpatialCopyAsNonSpatial = null;
	private JMenuItem menuItemSpatialCopyAsNonSpatialStochastic = null;
	private JMenuItem menuItemSpatialCopyAsNonSpatialDeterministic = null;
	private JMenuItem menuItemSpatialCopyAsNonSpatialRulebased = null;
	private JMenu menuSpatialCopyAsSpatial = null;
	private JMenuItem menuItemSpatialCopyAsSpatialStochastic = null;
	private JMenuItem menuItemSpatialCopyAsSpatialDeterministic = null;
	private ProblemSignalling problemSignalling = null;
	private JMenuItem menuItemSpatialCopyAsSpatialRulebased = null;
	
	private class ProblemSignalling {
		private javax.swing.Timer timer = null;
		private int counter = 0;
		private final int MaxBlinks = 5;
		
		private final String title;
		private final int oldNumWarnings;
		private final int oldNumErrors;
		
		public ProblemSignalling(String title, int oldNumErrors, int oldNumWarnings) {
			this.title = title;
			this.oldNumErrors = oldNumErrors;
			this.oldNumWarnings = oldNumWarnings;
		}
		public void start(Timer timer) {
			this.timer = timer;
			timer.start(); 
		}
		private void blink() {
			if(counter == 0) {			// first tick, we set up the icon
				if(oldNumErrors < issueManager.getNumErrors()) {
					rightBottomTabbedPane.setIconAt(DocumentEditorTabID.problems.ordinal(), VCellIcons.issueErrorIcon);
				} else if(oldNumWarnings < issueManager.getNumWarnings()) {
					rightBottomTabbedPane.setIconAt(DocumentEditorTabID.problems.ordinal(), VCellIcons.issueWarningIcon);
				} else {				// nothing to flash if the number of errors or warnings did not increase
					rightBottomTabbedPane.setIconAt(DocumentEditorTabID.problems.ordinal(), null);
					rightBottomTabbedPane.setTitleAt(DocumentEditorTabID.problems.ordinal(), title );
					problemSignalling.timer.stop();
					return;				// clean up and exit
				}
			}
			if(counter >= MaxBlinks) {	// last tick, we clean up and leave
				rightBottomTabbedPane.setIconAt(DocumentEditorTabID.problems.ordinal(), null);
				rightBottomTabbedPane.setTitleAt(DocumentEditorTabID.problems.ordinal(), title);
				problemSignalling.timer.stop();
				return;
			}
			// text
			if(counter%2 == 0) {		// even ticks, display title in colored background
				if(oldNumErrors < issueManager.getNumErrors()) {
					rightBottomTabbedPane.setTitleAt(DocumentEditorTabID.problems.ordinal(), "*" + title + "*");
				} else if(oldNumWarnings < issueManager.getNumWarnings()) {
					rightBottomTabbedPane.setTitleAt(DocumentEditorTabID.problems.ordinal(), "*" + title + "*");
				}
			} else if(counter%2 == 1) {	// off ticks, display title in normal background
				rightBottomTabbedPane.setTitleAt(DocumentEditorTabID.problems.ordinal(), title );
			}
			counter++;
		}
		public Object getTimer() {
			return timer;
		}
	}
	
	private class IvjEventHandler implements ActionListener, PropertyChangeListener,TreeSelectionListener, MouseListener, IssueEventListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == expandAllMenuItem) {
				TreePath[] selectedPaths = documentEditorTree.getSelectionPaths();
				for (TreePath tp : selectedPaths) {
					Object lastSelectedPathComponent = tp.getLastPathComponent();
					if (lastSelectedPathComponent instanceof BioModelNode) {
						GuiUtils.treeExpandAll(documentEditorTree, (BioModelNode)lastSelectedPathComponent, true);
					}
				}
			} else if (e.getSource() == collapseAllMenuItem) {
				TreePath[] selectedPaths = documentEditorTree.getSelectionPaths();
				for (TreePath tp : selectedPaths) {
					Object lastSelectedPathComponent = tp.getLastPathComponent();
					if (lastSelectedPathComponent instanceof BioModelNode) {
						BioModelNode selectedNode = (BioModelNode)lastSelectedPathComponent;
						if (selectedNode.getParent() == null) {// root
							for (int i = 0; i < selectedNode.getChildCount(); i ++) {
								GuiUtils.treeExpandAll(documentEditorTree, (BioModelNode) selectedNode.getChildAt(i), false);
							}
						} else {
							GuiUtils.treeExpandAll(documentEditorTree, selectedNode, false);
						}
					}
				}
			} else if (e.getSource() == addNewMenuItem) {
				popupMenuActionPerformed(DocumentEditorPopupMenuAction.add_new, e.getActionCommand());
			} else if (e.getSource() == removeAppsMenu) {
				popupMenuActionPerformed(DocumentEditorPopupMenuAction.deleteChoose, e.getActionCommand());
			} else if (e.getSource() == renameMenuItem) {
				documentEditorTree.startEditingAtPath(documentEditorTree.getSelectionPath());
			} else if (e.getSource() == deleteMenuItem) {
				popupMenuActionPerformed(DocumentEditorPopupMenuAction.delete, e.getActionCommand());
			} else if (e.getSource() == addNewAppDeterministicMenuItem) {
				popupMenuActionPerformed(DocumentEditorPopupMenuAction.add_new_app_deterministic, e.getActionCommand());
			} else if (e.getSource() == addNewAppStochasticMenuItem) {
				popupMenuActionPerformed(DocumentEditorPopupMenuAction.add_new_app_stochastic, e.getActionCommand());
			} else if (e.getSource() == addNewAppRulebasedMenuItem) {
				popupMenuActionPerformed(DocumentEditorPopupMenuAction.add_new_app_rulebased, e.getActionCommand());
			} else if (e.getSource() == menuItemAppCopy
						|| e.getSource() == menuItemNonSpatialCopyStochastic
						|| e.getSource() == menuItemNonSpatialCopyDeterministic
						|| e.getSource() == menuItemNonSpatialCopyRulebased
						|| e.getSource() == menuItemSpatialCopyAsNonSpatialDeterministic
						|| e.getSource() == menuItemSpatialCopyAsNonSpatialStochastic
						|| e.getSource() == menuItemSpatialCopyAsNonSpatialRulebased
						|| e.getSource() == menuItemSpatialCopyAsSpatialDeterministic
						|| e.getSource() == menuItemSpatialCopyAsSpatialStochastic
						|| e.getSource() == menuItemSpatialCopyAsSpatialRulebased ) {
				popupMenuActionPerformed(DocumentEditorPopupMenuAction.copy_app, e.getActionCommand());	
			} else if (e.getSource() == menuItemNewBiomodelFromApp){
				popupMenuActionPerformed(DocumentEditorPopupMenuAction.app_new_biomodel, e.getActionCommand());
			} else if((problemSignalling != null) && (e.getSource() == problemSignalling.getTimer())) {
				problemSignalling.blink();
			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == selectionManager) {
				if (evt.getPropertyName().equals(SelectionManager.PROPERTY_NAME_SELECTED_OBJECTS)) {			
					onSelectedObjectsChange();
				} else if (evt.getPropertyName().equals(SelectionManager.PROPERTY_NAME_ACTIVE_VIEW)) {
					onActiveViewChange();
				}
			}
		};
		
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() != documentEditorTree) {
				return;
			}
			if (e.getClickCount() == 1) {
			} else if (e.getClickCount() == 2) {
				Object node = documentEditorTree.getLastSelectedPathComponent();
				if (node instanceof LinkNode) {
					String link = ((LinkNode)node).getLink();
					if (link != null) {
						DialogUtils.browserLauncher(documentEditorTree, link, "failed to launch");
					}
				}
			}
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			if (e.getSource() == documentEditorTree) {
				documentEditorTree_tryPopupTrigger(e);
			}
		}
		public void mouseReleased(MouseEvent e) {
			if (e.getSource() == documentEditorTree) {
				documentEditorTree_tryPopupTrigger(e);
			}
		}

		
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == documentEditorTree) {
				treeSelectionChanged0(e);
			}
		}
		public void issueChange(IssueEvent issueEvent) {
			String errString = issueManager.getNumErrors() + " Errors";
			String warnString = issueManager.getNumWarnings() + " Warnings";
			boolean bHtml = false;
			if (issueManager.getNumErrors() > 0) {
				bHtml = true;
//				errString = "<font color=#9B0000>" + errString + "</font>";
			}
			if (issueManager.getNumWarnings() > 0) {
				bHtml = true;
//				warnString = "<font color=#BA5C00>" + warnString + "</font>";	// 0xff8c00
			}
			String title = " Problems (" + errString + ", " + warnString + ") ";
			if(bHtml) {
				rightBottomTabbedPane.setTitleAt(DocumentEditorTabID.problems.ordinal(), "*"+ title +"*" );
				warningBarUpdate(issueEvent, title);
			} else {
				rightBottomTabbedPane.setTitleAt(DocumentEditorTabID.problems.ordinal(), title);
			}
		}
		
		public void warningBarUpdate(IssueEvent issueEvent, String title) {
			int oldNumErrors = 0;
			int oldNumWarnings = 0;
			for(Issue issue : issueEvent.getOldValue()) {
				Severity severity = issue.getSeverity();
				if (severity == Severity.ERROR) {
					oldNumErrors ++;
				} else if (severity == Severity.WARNING) { 
					oldNumWarnings ++;
				}
			}
			problemSignalling = new ProblemSignalling(title, oldNumErrors, oldNumWarnings);
			problemSignalling.start(new Timer(200, this));
		}
	};

/**
 * BioModelEditor constructor comment.
 */
public DocumentEditor() {
	super();
	initialize();
}

protected abstract void popupMenuActionPerformed(DocumentEditorPopupMenuAction action, String actionCommand);

public void onSelectedObjectsChange() {
	Object[] selectedObjects = selectionManager.getSelectedObjects();
	setRightBottomPanelOnSelection(selectedObjects);
//	DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(this, DocumentWindow.class);
//	if (documentWindow!=null){
//		documentWindow.getWarningBar().setText(selectionManager.getStatusText());
//	}
}

private void onActiveViewChange() {
//	DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(this, DocumentWindow.class);
//	if (documentWindow!=null){
//		documentWindow.getWarningBar().setText(selectionManager.getStatusText());
//	}
}

private void selectClickPath(MouseEvent e) {
	Point mousePoint = e.getPoint();
	TreePath clickPath = documentEditorTree.getPathForLocation(mousePoint.x, mousePoint.y);
    if (clickPath == null) {
    	return; 
    }
	Object rightClickNode = clickPath.getLastPathComponent();
	if (rightClickNode == null || !(rightClickNode instanceof BioModelNode)) {
		return;
	}
	TreePath[] selectedPaths = documentEditorTree.getSelectionPaths();
	if (selectedPaths == null || selectedPaths.length == 0) {
		return;
	} 
	boolean bFound = false;
	for (TreePath tp : selectedPaths) {
		if (tp.equals(clickPath)) {
			bFound = true;
			break;
		}
	}
	if (!bFound) {
		documentEditorTree.setSelectionPath(clickPath);
	}
}
private void documentEditorTree_tryPopupTrigger(MouseEvent e) {
	if (e.isPopupTrigger()) {
		selectClickPath(e);
		Point mousePoint = e.getPoint();
		getPopupMenu().show(documentEditorTree, mousePoint.x, mousePoint.y);
	}
}	

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}

private Frame jframeParent;
private void documentEditor_eventDispatched(AWTEvent event) {
	try {
		if (jframeParent == null) {
			jframeParent = JOptionPane.getFrameForComponent(DocumentEditor.this);
		}
		Object source = event.getSource();
		if (source instanceof Component) {
			for (Component component = (Component)source; component != null; component = component.getParent()) {
				if (component == DocumentEditor.this || component == jframeParent) {	
					issueManager.setDirty();
					break;
				}
			}			
		}
	}catch (Exception e){
		e.printStackTrace();
		// consume any exception ... don't screw up the swing event queue.
	}	
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setLayout(new BorderLayout());
		getToolkit().addAWTEventListener(new AWTEventListener() {
			
			public void eventDispatched(AWTEvent event) {
				try {
					switch (event.getID()) {		
					case KeyEvent.KEY_RELEASED:	
						documentEditor_eventDispatched(event);
						break;
					case MouseEvent.MOUSE_RELEASED:
						documentEditor_eventDispatched(event);
						break;
					}
				}catch (Exception e){
					e.printStackTrace();
					// consume any exception ... don't screw up the swing event queue.
				}
				
			}
		}, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
				
		documentEditorTree = new javax.swing.JTree() {

			@Override
			public boolean isPathEditable(TreePath path) {
				Object object = path.getLastPathComponent();
				return (object instanceof BioModelNode) && (((BioModelNode)object).getUserObject() instanceof SimulationContext);
			}
			
		};
		documentEditorTree.setEditable(true);
		documentEditorTree.setLargeModel(true);
		documentEditorTreeCellEditor = new DocumentEditorTreeCellEditor(documentEditorTree);
		documentEditorTree.setCellEditor(documentEditorTreeCellEditor);
		documentEditorTree.setName("bioModelEditorTree");
		ToolTipManager.sharedInstance().registerComponent(documentEditorTree);
		int rowHeight = documentEditorTree.getRowHeight();
		if(rowHeight < 10) { 
			rowHeight = 20; 
		}
		documentEditorTree.setRowHeight(rowHeight + 2);
		documentEditorTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		databaseWindowPanel = new DatabaseWindowPanel(false, false);
		leftBottomTabbedPane  = new JTabbedPaneEnhanced();
		leftBottomTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		leftBottomTabbedPane.addTab("VCell DB", databaseWindowPanel);
		
		JScrollPane treePanel = new javax.swing.JScrollPane(documentEditorTree);	
		leftSplitPane.setTopComponent(treePanel);
		leftBottomTabbedPane.setMinimumSize(new java.awt.Dimension(198, 148));
		leftSplitPane.setBottomComponent(leftBottomTabbedPane);
		leftSplitPane.setResizeWeight(0.5);
		leftSplitPane.setDividerLocation(300);
		leftSplitPane.setDividerSize(8);
		leftSplitPane.setOneTouchExpandable(true);

		rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightSplitPane.setResizeWeight(0.7);
		rightSplitPane.setDividerLocation(400);
		rightSplitPane.setDividerSize(8);
		rightSplitPane.setOneTouchExpandable(true);
		
		rightBottomEmptyPanel = new JPanel(new GridBagLayout());
		rightBottomEmptyPanel.setBackground(Color.white);
		rightBottomEmptyPanel.setName("ObjectProperties");
		treeNodeDescriptionLabel = new JLabel(generalTreeNodeDescription);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(10,10,4,4);
		gbc.gridy = 0;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.PAGE_START;
		rightBottomEmptyPanel.add(treeNodeDescriptionLabel, gbc);

		rightBottomEmptyAnnotationsPanel = new JPanel(new GridBagLayout());
		rightBottomEmptyAnnotationsPanel.setBackground(Color.white);
		rightBottomEmptyAnnotationsPanel.setName("Annotations");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(10,10,4,4);
		gbc.gridy = 0;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.PAGE_START;
		rightBottomEmptyAnnotationsPanel.add(treeNodeDescriptionLabel, gbc);

		issuePanel = new IssuePanel();		
		rightBottomTabbedPane = new JTabbedPaneEnhanced();
		rightBottomTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		rightBottomEmptyPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		rightBottomEmptyAnnotationsPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		issuePanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		rightBottomTabbedPane.addTab(TAB_TITLE_OBJECT_PROPERTIES, rightBottomEmptyPanel);		
		rightBottomTabbedPane.addTab(TAB_TITLE_ANNOTATIONS, rightBottomEmptyAnnotationsPanel);		
		rightBottomTabbedPane.addTab(TAB_TITLE_PROBLEMS, issuePanel);		
		rightBottomTabbedPane.setMinimumSize(new java.awt.Dimension(198, 148));		
		rightSplitPane.setBottomComponent(rightBottomTabbedPane);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(270);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.3);
		splitPane.setDividerSize(8);
		splitPane.setLeftComponent(leftSplitPane);
		splitPane.setRightComponent(rightSplitPane);
		
		add(splitPane, BorderLayout.CENTER);
		
		issueManager.addIssueEventListener(eventHandler);
		selectionManager.addPropertyChangeListener(eventHandler);
		
		databaseWindowPanel.setSelectionManager(selectionManager);
		documentEditorTree.addTreeSelectionListener(eventHandler);
		documentEditorTree.addMouseListener(eventHandler);
		
		bioModelMetaDataPanel = new BioModelMetaDataPanel();
		bioModelMetaDataPanel.setSelectionManager(selectionManager);
		mathModelMetaDataPanel = new MathModelMetaDataPanel();
		mathModelMetaDataPanel.setSelectionManager(selectionManager);
		geometryMetaDataPanel = new GeometryMetaDataPanel();
		geometryMetaDataPanel.setSelectionManager(selectionManager);
		issuePanel.setSelectionManager(selectionManager);
		issuePanel.setIssueManager(issueManager);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void treeSelectionChanged0(TreeSelectionEvent treeSelectionEvent) {
	try {
		treeSelectionChanged();
		Object selectedNode = documentEditorTree.getLastSelectedPathComponent();
		if (selectedNode != null && (selectedNode instanceof BioModelNode)) {
			Object selectedObject = ((BioModelNode)selectedNode).getUserObject();
			
			DocumentEditorTreeFolderClass folderClass = null;
			if (selectedObject instanceof DocumentEditorTreeFolderNode) {
				folderClass = ((DocumentEditorTreeFolderNode) selectedObject).getFolderClass();
			}
			ActiveView activeView = new ActiveView(getSelectedSimulationContext(), folderClass, null);
			selectionManager.setActiveView(activeView);
			if (/*selectedObject instanceof SimulationContext 
					|| */ selectedObject instanceof BioModel
					|| selectedObject instanceof MathModel) { 
				selectionManager.setSelectedObjects(new Object[]{selectedObject});
			}
		}
	}catch (Exception ex){
		ex.printStackTrace(System.out);
	}
}

protected abstract void setRightBottomPanelOnSelection(Object[] selections);
protected abstract void treeSelectionChanged();

protected SimulationContext getSelectedSimulationContext() {
	if (this instanceof MathModelEditor) {
		return null;
	}
	
	// make sure only one simulation context is selected
	TreePath[] selectedPaths = documentEditorTree.getSelectionPaths();
	SimulationContext simulationContext = null;
	for (TreePath path : selectedPaths) {
		Object node = path.getLastPathComponent();
		if (!(node instanceof BioModelNode)) {
			return null;
		}
		BioModelNode n = (BioModelNode)node;
		while (true) {
			Object userObject = n.getUserObject();
			if (userObject instanceof SimulationContext) {
				if (simulationContext == null) {
					simulationContext = (SimulationContext)userObject;
					break;
				} else if (simulationContext != userObject) {
					return null;
				}
			}
			TreeNode parent = n.getParent();
			if (parent == null || !(parent instanceof BioModelNode)) {
				break;
			}
			n = (BioModelNode)parent;
		}
	}
	return simulationContext;
}

private void construcutPopupMenu() {
	popupMenu.removeAll();
	TreePath[] selectedPaths = documentEditorTree.getSelectionPaths();
	boolean bRename = false;
	boolean bExpand = true;
	boolean bAddNew = false;
	boolean bAddNewApp = false;
	boolean bCopyApp = false;
	boolean bDelete = false;
	boolean bNewBiomodel = false;
	boolean bRemoveApps = false;
	DocumentEditorTreeFolderClass folderClass = null;
	for (TreePath tp : selectedPaths) {
		Object obj = tp.getLastPathComponent();
		if (obj == null || !(obj instanceof BioModelNode)) {
			continue;
		}
		if (documentEditorTree.getModel().isLeaf(obj)) {
			bExpand = false;
		}
		
		BioModelNode selectedNode = (BioModelNode) obj;
		Object userObject = selectedNode.getUserObject();
		if (userObject instanceof DocumentEditorTreeFolderNode) {
			folderClass = ((DocumentEditorTreeFolderNode) userObject).getFolderClass();
			if (folderClass == DocumentEditorTreeFolderClass.APPLICATIONS_NODE) {
				bAddNewApp = true;
				if(selectedNode.getChildCount() > 0){
					bRemoveApps = true;
				}
			} else if (folderClass == DocumentEditorTreeFolderClass.REACTIONS_NODE
					|| folderClass == DocumentEditorTreeFolderClass.STRUCTURES_NODE
					|| folderClass == DocumentEditorTreeFolderClass.SPECIES_NODE
					|| folderClass == DocumentEditorTreeFolderClass.MOLECULAR_TYPES_NODE
					|| folderClass == DocumentEditorTreeFolderClass.OBSERVABLES_NODE
					|| folderClass == DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE
				) {
				bAddNew = (selectedPaths.length == 1);
				bRename = false;
			}
		} else if (userObject instanceof SimulationContext) {			
			bRename = true;
			bCopyApp = true;
			bDelete = true;
			bNewBiomodel = true;
		}
	}
	if (selectedPaths.length != 1) {
		bRename = false;
	}
	if (bAddNewApp) {
		if (addNewAppMenu == null) {
			addNewAppMenu = new JMenu("New Application");
			addNewAppDeterministicMenuItem = new JMenuItem(GuiConstants.MENU_TEXT_DETERMINISTIC_APPLICATION);
			addNewAppDeterministicMenuItem.addActionListener(eventHandler);
			addNewAppStochasticMenuItem = new JMenuItem(GuiConstants.MENU_TEXT_STOCHASTIC_APPLICATION);
			addNewAppStochasticMenuItem.addActionListener(eventHandler);
			addNewAppRulebasedMenuItem = new JMenuItem(GuiConstants.MENU_TEXT_RULEBASED_APPLICATION);
			addNewAppRulebasedMenuItem.addActionListener(eventHandler);
			addNewAppMenu.add(addNewAppDeterministicMenuItem);
			addNewAppMenu.add(addNewAppStochasticMenuItem);
			addNewAppMenu.add(addNewAppRulebasedMenuItem);
		}
		popupMenu.add(addNewAppMenu);
	}
	if(bRemoveApps){
		if (removeAppsMenu == null) {
			removeAppsMenu = new JMenuItem("Remove Apps...");
			removeAppsMenu.addActionListener(eventHandler);
		}
		popupMenu.add(removeAppsMenu);		
	}
	if (bAddNew) {
		String addText = "New";
		if (folderClass == DocumentEditorTreeFolderClass.REACTIONS_NODE) {
			addText += " Reaction";
		} else if(folderClass == DocumentEditorTreeFolderClass.STRUCTURES_NODE) {
			addText += " Compartment";
		} else if(folderClass == DocumentEditorTreeFolderClass.SPECIES_NODE) {
			addText += " Species";
		} else if(folderClass == DocumentEditorTreeFolderClass.MOLECULAR_TYPES_NODE) {
			addText += " Molecule";
		} else if(folderClass == DocumentEditorTreeFolderClass.OBSERVABLES_NODE) {
			addText += " Observable";
		} else if(folderClass == DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE) {
			addText += " Simulation";
		}
		addNewMenuItem = new javax.swing.JMenuItem(addText);
		addNewMenuItem.addActionListener(eventHandler);
		popupMenu.add(addNewMenuItem);
	}
	if (bRename) {
		if (renameMenuItem == null) {
			renameMenuItem = new javax.swing.JMenuItem("Rename");
			renameMenuItem.addActionListener(eventHandler);
		}
		popupMenu.add(renameMenuItem);
	}
	if (bDelete) {
		if (deleteMenuItem == null) {
			deleteMenuItem = new javax.swing.JMenuItem("Delete");
			deleteMenuItem.addActionListener(eventHandler);
		}
		popupMenu.add(deleteMenuItem);
	}
	if (bCopyApp) {
		if (menuItemAppCopy == null) {
			menuItemAppCopy = new JMenuItem(GuiConstants.MENU_TEXT_APP_COPY);
			menuItemAppCopy.addActionListener(eventHandler);
			menuItemAppCopy.setActionCommand(GuiConstants.ACTIONCMD_COPY_APPLICATION);
		}		
		if (menuAppCopyAs == null) {
			menuAppCopyAs = new JMenu(GuiConstants.MENU_TEXT_APP_COPYAS);
		}
		menuAppCopyAs.removeAll();
		SimulationContext selectedSimContext = getSelectedSimulationContext();
		if (selectedSimContext != null) {
			if (selectedSimContext.getGeometry().getDimension() == 0) {		
				if (menuItemNonSpatialCopyDeterministic == null) {
					menuItemNonSpatialCopyStochastic=new JMenuItem(GuiConstants.MENU_TEXT_STOCHASTIC_APPLICATION);
					menuItemNonSpatialCopyStochastic.setActionCommand(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_STOCHASTIC_APPLICATION);
					menuItemNonSpatialCopyStochastic.addActionListener(eventHandler);
					
					menuItemNonSpatialCopyDeterministic = new javax.swing.JMenuItem(GuiConstants.MENU_TEXT_DETERMINISTIC_APPLICATION);
					menuItemNonSpatialCopyDeterministic.setActionCommand(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_DETERMINISTIC_APPLICATION);
					menuItemNonSpatialCopyDeterministic.addActionListener(eventHandler); 

					menuItemNonSpatialCopyRulebased = new javax.swing.JMenuItem(GuiConstants.MENU_TEXT_RULEBASED_APPLICATION);
					menuItemNonSpatialCopyRulebased.setActionCommand(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_RULEBASED_APPLICATION);
					menuItemNonSpatialCopyRulebased.addActionListener(eventHandler); 
				}
				menuAppCopyAs.add(menuItemNonSpatialCopyDeterministic);
				menuAppCopyAs.add(menuItemNonSpatialCopyStochastic);
				menuAppCopyAs.add(menuItemNonSpatialCopyRulebased);
			} else {
				if (menuSpatialCopyAsNonSpatial == null) {
					menuSpatialCopyAsNonSpatial = new JMenu(GuiConstants.MENU_TEXT_NON_SPATIAL_APPLICATION);
					menuItemSpatialCopyAsNonSpatialDeterministic = new JMenuItem(GuiConstants.MENU_TEXT_DETERMINISTIC_APPLICATION);
					menuItemSpatialCopyAsNonSpatialDeterministic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_DETERMINISTIC_APPLICATION);
					menuItemSpatialCopyAsNonSpatialDeterministic.addActionListener(eventHandler);
					menuItemSpatialCopyAsNonSpatialStochastic = new JMenuItem(GuiConstants.MENU_TEXT_STOCHASTIC_APPLICATION);
					menuItemSpatialCopyAsNonSpatialStochastic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_STOCHASTIC_APPLICATION);
					menuItemSpatialCopyAsNonSpatialStochastic.addActionListener(eventHandler);
					menuItemSpatialCopyAsNonSpatialRulebased = new JMenuItem(GuiConstants.MENU_TEXT_RULEBASED_APPLICATION);
					menuItemSpatialCopyAsNonSpatialRulebased.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_RULEBASED_APPLICATION);
					menuItemSpatialCopyAsNonSpatialRulebased.addActionListener(eventHandler);
					menuSpatialCopyAsNonSpatial.add(menuItemSpatialCopyAsNonSpatialDeterministic);
					menuSpatialCopyAsNonSpatial.add(menuItemSpatialCopyAsNonSpatialStochastic);
					menuSpatialCopyAsNonSpatial.add(menuItemSpatialCopyAsNonSpatialRulebased);
					
					menuSpatialCopyAsSpatial = new JMenu(GuiConstants.MENU_TEXT_SPATIAL_APPLICATION);
					menuItemSpatialCopyAsSpatialDeterministic = new JMenuItem(GuiConstants.MENU_TEXT_DETERMINISTIC_APPLICATION);
					menuItemSpatialCopyAsSpatialDeterministic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_DETERMINISTIC_APPLICATION);
					menuItemSpatialCopyAsSpatialDeterministic.addActionListener(eventHandler);
					menuItemSpatialCopyAsSpatialStochastic = new JMenuItem(GuiConstants.MENU_TEXT_STOCHASTIC_APPLICATION);
					menuItemSpatialCopyAsSpatialStochastic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_STOCHASTIC_APPLICATION);
					menuItemSpatialCopyAsSpatialStochastic.addActionListener(eventHandler);
					menuItemSpatialCopyAsSpatialRulebased = new JMenuItem(GuiConstants.MENU_TEXT_RULEBASED_APPLICATION);
					menuItemSpatialCopyAsSpatialRulebased.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_RULEBASED_APPLICATION);
					menuItemSpatialCopyAsSpatialRulebased.addActionListener(eventHandler);
					menuSpatialCopyAsSpatial.add(menuItemSpatialCopyAsSpatialDeterministic);
					menuSpatialCopyAsSpatial.add(menuItemSpatialCopyAsSpatialStochastic);
					//menuSpatialCopyAsSpatial.add(menuItemSpatialCopyAsSpatialRulebased);		// not supported yet, uncomment when time comes
				}
				menuAppCopyAs.add(menuSpatialCopyAsNonSpatial);
				menuAppCopyAs.add(menuSpatialCopyAsSpatial);
			}
		}
		if (popupMenu.getComponents().length > 0) {
			popupMenu.add(new JSeparator());
		}
		popupMenu.add(menuItemAppCopy);
		popupMenu.add(menuAppCopyAs);
	}
	if(bNewBiomodel){
		menuItemNewBiomodelFromApp = new JMenuItem(GuiConstants.MENU_TEXT_APP_NEWBIOMODEL);
		menuItemNewBiomodelFromApp.addActionListener(eventHandler);
		popupMenu.add(menuItemNewBiomodelFromApp);
	}
	if (bExpand) {
		if (expandAllMenuItem == null) {
			popupMenuSeparator = new JSeparator();
			expandAllMenuItem = new javax.swing.JMenuItem("Expand All");
			collapseAllMenuItem = new javax.swing.JMenuItem("Collapse All");
			expandAllMenuItem.addActionListener(eventHandler);
			collapseAllMenuItem.addActionListener(eventHandler);
		}
		if (popupMenu.getComponents().length > 0) {
			popupMenu.add(popupMenuSeparator);
		}
		popupMenu.add(expandAllMenuItem);
		popupMenu.add(collapseAllMenuItem);
	}
}

private JPopupMenu getPopupMenu() {
	if (popupMenu == null) {
		popupMenu = new javax.swing.JPopupMenu();	
	}
	construcutPopupMenu();
	return popupMenu;
}

private ConnectionStatus connectionStatus = null;
public void updateConnectionStatus(ConnectionStatus newValue) {
	if (connectionStatus == newValue 
			|| connectionStatus != null && connectionStatus.equals(newValue)) {
		return;
	}
	connectionStatus = newValue;
	databaseWindowPanel.updateConnectionStatus(connectionStatus);
	if (connectionStatus.getStatus() == ConnectionStatus.CONNECTED) {
		databaseWindowPanel.getDatabaseWindowManager().initializeAll();
	}
}
public SelectionManager getSelectionManager() { return selectionManager; }

public final JTabbedPane getRightBottomTabbedPane() {
	return rightBottomTabbedPane;
}

public void setWindowFocus( ) {
	if (issueManager.getNumErrors() > 0) {
		rightBottomTabbedPane.setSelectedIndex(DocumentEditorTabID.problems.ordinal( ));
	}
	Window w = LWNamespace.findOwnerOfType(Window.class, getParent( ));
	if (w != null) {
		w.toFront( );
	}
	if (LG.isWarnEnabled()) {
		LG.warn("can't find window owner");
	}
}

}

//
//how to obtain the main frame from anywhere in the code 
//
//		Container previousParent = null;
//		Container parent = getParent();
//		while(parent != null) {
//			previousParent = parent;
//			parent = parent.getParent();
//		}
//		if(previousParent != null && previousParent instanceof DocumentWindow) {
//			DocumentWindow mainFrame = (DocumentWindow)previousParent;
//			JLabel warningBar = mainFrame.getWarningBar();	// a label on the Status Bar of the application window
//			warningBar.setText(text);
//			warningBar.setIcon(icon);
//			// the thread will change the icon and the text after a few seconds
//			Runnable r = new CleanupThread(mainFrame);
//			new Thread(r).start();
//		}
//		Runnable r = new CleanupThread(text);
//		new Thread(r).start();
//
//	public class CleanupThread implements Runnable {
//		private String title;
//		public CleanupThread(String title) {
//			this.title = title;
//		}
//		public void run() {
//			try {
//				System.out.println("in thread");
//				Thread.sleep(2000);
//				rightBottomTabbedPane.setIconAt(DocumentEditorTabID.problems.ordinal(), null);
//				rightBottomTabbedPane.setTitleAt(DocumentEditorTabID.problems.ordinal(), title);
//			} catch ( Throwable th ) {
//				throw new RuntimeException(th);
//			}
//		}
//	}
