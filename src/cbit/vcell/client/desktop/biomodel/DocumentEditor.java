package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.DatabaseWindowPanel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderNode;
import cbit.vcell.client.desktop.mathmodel.MathModelEditor;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.desktop.BioModelMetaDataPanel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.desktop.GeometryMetaDataPanel;
import cbit.vcell.desktop.MathModelMetaDataPanel;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 2:55:18 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public abstract class DocumentEditor extends JPanel {
	protected static final String generalTreeNodeDescription = "Select only one object (e.g. species, reaction, simulation) to view/edit properties.";
	
	protected enum DocumentEditorPopupMenuAction {
		add_new,
		delete,
		add_new_app_deterministic,
		add_new_app_stochastic,
		copy_app,
		rename,
	}
	protected static final double DEFAULT_DIVIDER_LOCATION = 0.68;
	protected final static int RIGHT_BOTTOM_TAB_PROPERTIES_INDEX = 0;
	protected static final String DATABASE_PROPERTIES_TAB_TITLE = "Database File Info";
	protected IvjEventHandler eventHandler = new IvjEventHandler();
	
	protected JTree documentEditorTree = null;
	protected SelectionManager selectionManager = new SelectionManager();
	protected IssueManager issueManager = new IssueManager();

	protected JPanel emptyPanel = new JPanel();
	private JPopupMenu popupMenu = null;
	private JMenu addNewAppMenu = null;
	private JMenuItem addNewAppDeterministicMenuItem = null;
	private JMenuItem addNewAppStochasticMenuItem = null;
	private JMenuItem expandAllMenuItem = null;
	private JMenuItem collapseAllMenuItem = null;
	private JMenuItem addNewMenuItem;
	private JMenuItem deleteMenuItem;
	private JMenuItem renameMenuItem;
	
	protected DatabaseWindowPanel databaseWindowPanel = null;
	protected JTabbedPane leftBottomTabbedPane = null;
	protected JSplitPane rightSplitPane = null;
	protected BioModelMetaDataPanel bioModelMetaDataPanel = null;
	protected MathModelMetaDataPanel mathModelMetaDataPanel = null;
	protected GeometryMetaDataPanel geometryMetaDataPanel = null;
	
	protected JTabbedPane rightBottomTabbedPane = null;
	protected JPanel rightBottomEmptyPanel = null;
	private JSeparator popupMenuSeparator = null;
	private DocumentEditorTreeCellEditor documentEditorTreeCellEditor;
	protected JLabel treeNodeDescriptionLabel;
	protected IssuePanel issuePanel;
	
	private JMenuItem menuItemAppCopy = null;
	private JMenu menuAppCopyAs = null;
	private JMenuItem menuItemNonSpatialCopyStochastic = null;
	private JMenuItem menuItemNonSpatialCopyDeterministic = null;
	
	private JMenu menuSpatialCopyAsNonSpatial = null;
	private JMenuItem menuItemSpatialCopyAsNonSpatialStochastic = null;
	private JMenuItem menuItemSpatialCopyAsNonSpatialDeterministic = null;
	private JMenu menuSpatialCopyAsSpatial = null;
	private JMenuItem menuItemSpatialCopyAsSpatialStochastic = null;
	private JMenuItem menuItemSpatialCopyAsSpatialDeterministic = null;

	private class IvjEventHandler implements ActionListener, PropertyChangeListener,TreeSelectionListener, MouseListener {
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
			} else if (e.getSource() == deleteMenuItem) {
				popupMenuActionPerformed(DocumentEditorPopupMenuAction.delete, e.getActionCommand());
			} else if (e.getSource() == renameMenuItem) {
				documentEditorTree.startEditingAtPath(documentEditorTree.getSelectionPath());
			} else if (e.getSource() == addNewAppDeterministicMenuItem) {
				popupMenuActionPerformed(DocumentEditorPopupMenuAction.add_new_app_deterministic, e.getActionCommand());
			} else if (e.getSource() == addNewAppStochasticMenuItem) {
				popupMenuActionPerformed(DocumentEditorPopupMenuAction.add_new_app_stochastic, e.getActionCommand());
			} else if (e.getSource() == menuItemAppCopy
						|| e.getSource() == menuItemNonSpatialCopyStochastic
						|| e.getSource() == menuItemNonSpatialCopyDeterministic
						|| e.getSource() == menuItemSpatialCopyAsNonSpatialDeterministic
						|| e.getSource() == menuItemSpatialCopyAsNonSpatialStochastic
						|| e.getSource() == menuItemSpatialCopyAsSpatialDeterministic
						|| e.getSource() == menuItemSpatialCopyAsSpatialStochastic) {
				popupMenuActionPerformed(DocumentEditorPopupMenuAction.copy_app, e.getActionCommand());	
			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == selectionManager) {
				onSelectedObjectsChange();
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
						DialogUtils.browserLauncher(documentEditorTree, link, "failed to launch", false);
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

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setLayout(new BorderLayout());
				
		documentEditorTree = new javax.swing.JTree();
		documentEditorTree.setEditable(true);
		documentEditorTreeCellEditor = new DocumentEditorTreeCellEditor(documentEditorTree);
		documentEditorTree.setCellEditor(documentEditorTreeCellEditor);
		documentEditorTree.setName("bioModelEditorTree");
		ToolTipManager.sharedInstance().registerComponent(documentEditorTree);
		int rowHeight = documentEditorTree.getRowHeight();
		if(rowHeight < 10) { 
			rowHeight = 20; 
		}
		documentEditorTree.setRowHeight(rowHeight + 2);
		
		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		databaseWindowPanel = new DatabaseWindowPanel(false, false);
		leftBottomTabbedPane  = new JTabbedPane();
		leftBottomTabbedPane.addTab("VCell Database", databaseWindowPanel);
		
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
		
		issuePanel = new IssuePanel();		
		rightBottomTabbedPane = new JTabbedPane();
		rightBottomEmptyPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		issuePanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		rightBottomTabbedPane.addTab("Object Properties", rightBottomEmptyPanel);		
		rightBottomTabbedPane.addTab("Problems", issuePanel);		
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
		TreePath[] paths = documentEditorTree.getSelectionModel().getSelectionPaths();
		List<Object> selectedObjects = new ArrayList<Object>();
		if (paths != null) {
			for (TreePath path : paths) {
				Object node = path.getLastPathComponent();
				if (node != null && (node instanceof BioModelNode)) {
					selectedObjects.add(((BioModelNode)node).getUserObject());
				}
			}
		}
		if (selectedObjects.size() > 0) {
			selectionManager.setSelectedObjects(selectedObjects.toArray());
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
	boolean bDelete = false;
	boolean bRename = false;
	boolean bExpand = true;
	boolean bAddNew = false;
	boolean bAddNewApp = false;
	boolean bCopyApp = false;
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
			DocumentEditorTreeFolderClass folderClass = ((DocumentEditorTreeFolderNode) userObject).getFolderClass();
			if (folderClass == DocumentEditorTreeFolderClass.APPLICATTIONS_NODE) {
				bAddNewApp = true;
			} else if (folderClass == DocumentEditorTreeFolderClass.REACTIONS_NODE
					|| folderClass == DocumentEditorTreeFolderClass.STRUCTURES_NODE
					|| folderClass == DocumentEditorTreeFolderClass.SPECIES_NODE
					|| folderClass == DocumentEditorTreeFolderClass.SIMULATIONS_NODE
					|| folderClass == DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE
				) {
				bAddNew = (selectedPaths.length == 1);
				bDelete = false;
				bRename = false;
			}
		} else if (userObject instanceof ReactionStep
				|| userObject instanceof Feature
				|| userObject instanceof SpeciesContext
				|| userObject instanceof ModelParameter
				|| userObject instanceof Simulation
			) {			
			bDelete = true;
			bRename = true;
		} else if (userObject instanceof SimulationContext) {			
			bDelete = true;
			bRename = true;
			bCopyApp = true;
		} else if (userObject instanceof Membrane) {
			bDelete = false;
			bRename = true;
		} else {
			SimulationContext selectedSimulationContext = getSelectedSimulationContext();
			if (userObject instanceof BioEvent) {
				bDelete = selectedSimulationContext != null;
				bRename = true;			
			} else if (userObject instanceof AnnotatedFunction) {			
				bDelete = selectedSimulationContext != null || this instanceof MathModelEditor;
				bRename = false;
			}
		}
	}
	if (selectedPaths.length != 1) {
		bRename = false;
	}
	if (bAddNewApp) {
		if (addNewAppMenu == null) {
			addNewAppMenu = new JMenu("Add New");
			addNewAppDeterministicMenuItem = new JMenuItem(GuiConstants.MENU_TEXT_DETERMINISTIC_APPLICATION);
			addNewAppDeterministicMenuItem.addActionListener(eventHandler);
			addNewAppStochasticMenuItem = new JMenuItem(GuiConstants.MENU_TEXT_STOCHASTIC_APPLICATION);
			addNewAppStochasticMenuItem.addActionListener(eventHandler);
			addNewAppMenu.add(addNewAppDeterministicMenuItem);
			addNewAppMenu.add(addNewAppStochasticMenuItem);
		}
		popupMenu.add(addNewAppMenu);
	}
	if (bAddNew) {
		if (addNewMenuItem == null) {
			addNewMenuItem = new javax.swing.JMenuItem("Add New");
			addNewMenuItem.addActionListener(eventHandler);
		}
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
				}
				menuAppCopyAs.add(menuItemNonSpatialCopyDeterministic);
				menuAppCopyAs.add(menuItemNonSpatialCopyStochastic);
			} else {
				if (menuSpatialCopyAsNonSpatial == null) {
					menuSpatialCopyAsNonSpatial = new JMenu(GuiConstants.MENU_TEXT_NON_SPATIAL_APPLICATION);
					menuItemSpatialCopyAsNonSpatialDeterministic = new JMenuItem(GuiConstants.MENU_TEXT_DETERMINISTIC_APPLICATION);
					menuItemSpatialCopyAsNonSpatialDeterministic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_DETERMINISTIC_APPLICATION);
					menuItemSpatialCopyAsNonSpatialDeterministic.addActionListener(eventHandler);
					menuItemSpatialCopyAsNonSpatialStochastic = new JMenuItem(GuiConstants.MENU_TEXT_STOCHASTIC_APPLICATION);
					menuItemSpatialCopyAsNonSpatialStochastic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_STOCHASTIC_APPLICATION);
					menuItemSpatialCopyAsNonSpatialStochastic.addActionListener(eventHandler);
					menuSpatialCopyAsNonSpatial.add(menuItemSpatialCopyAsNonSpatialDeterministic);
					menuSpatialCopyAsNonSpatial.add(menuItemSpatialCopyAsNonSpatialStochastic);
					
					menuSpatialCopyAsSpatial = new JMenu(GuiConstants.MENU_TEXT_SPATIAL_APPLICATION);
					menuItemSpatialCopyAsSpatialDeterministic = new JMenuItem(GuiConstants.MENU_TEXT_DETERMINISTIC_APPLICATION);
					menuItemSpatialCopyAsSpatialDeterministic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_DETERMINISTIC_APPLICATION);
					menuItemSpatialCopyAsSpatialDeterministic.addActionListener(eventHandler);
					menuItemSpatialCopyAsSpatialStochastic = new JMenuItem(GuiConstants.MENU_TEXT_STOCHASTIC_APPLICATION);
					menuItemSpatialCopyAsSpatialStochastic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_STOCHASTIC_APPLICATION);
					menuItemSpatialCopyAsSpatialStochastic.addActionListener(eventHandler);
					menuSpatialCopyAsSpatial.add(menuItemSpatialCopyAsSpatialDeterministic);
					menuSpatialCopyAsSpatial.add(menuItemSpatialCopyAsSpatialStochastic);
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

public void updateConnectionStatus(ConnectionStatus connStatus) {
	databaseWindowPanel.updateConnectionStatus(connStatus);
}
}