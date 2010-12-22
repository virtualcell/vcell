package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.desktop.DatabaseWindowPanel;
import cbit.vcell.desktop.BioModelMetaDataPanel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.desktop.GeometryMetaDataPanel;
import cbit.vcell.desktop.MathModelMetaDataPanel;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 2:55:18 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public abstract class DocumentEditor extends JPanel {
	protected static final double DEFAULT_DIVIDER_LOCATION = 0.7;
	protected IvjEventHandler ivjEventHandler = new IvjEventHandler();
	
	protected JTree documentEditorTree = null;
	protected SelectionManager selectionManager = new SelectionManager();

	protected JPanel emptyPanel = new JPanel();
	private JPopupMenu popupMenu = null;
	private JMenuItem expandAllMenuItem = null;
	private JMenuItem collapseAllMenuItem = null;
	protected DatabaseWindowPanel databaseWindowPanel = null;
	protected JTabbedPane leftTabbedPaneBottom = null;
	protected JSplitPane rightSplitPane = null;
	protected BioModelMetaDataPanel bioModelMetaDataPanel = null;
	protected MathModelMetaDataPanel mathModelMetaDataPanel = null;
	protected GeometryMetaDataPanel geometryMetaDataPanel = null;
	
	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.TreeSelectionListener, MouseListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == expandAllMenuItem || e.getSource() == collapseAllMenuItem) {
				Object lastSelectedPathComponent = documentEditorTree.getLastSelectedPathComponent();
				if (lastSelectedPathComponent instanceof BioModelNode) {
					expandAll((BioModelNode)lastSelectedPathComponent, e.getSource() == expandAllMenuItem);
				}
			} 
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == selectionManager) {
				onSelectedObjectsChange();
			}
		};
		
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == documentEditorTree) {
				if (SwingUtilities.isRightMouseButton(e)) {	// right click		
					Point mousePoint = e.getPoint();
					TreePath path = documentEditorTree.getPathForLocation(mousePoint.x, mousePoint.y);
                    if (path == null) {
                    	return; 
                    }
					Object node = documentEditorTree.getLastSelectedPathComponent();
					if (node == null || !(node instanceof BioModelNode) || path.getLastPathComponent() != node) {
						return;
					}
					getPopupMenu().show(documentEditorTree, mousePoint.x, mousePoint.y);
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
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}	
		
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == documentEditorTree)
				treeSelectionChanged0();
		}
	};

/**
 * BioModelEditor constructor comment.
 */
public DocumentEditor() {
	super();
	initialize();
}

public void onSelectedObjectsChange() {
	Object[] selectedObjects = selectionManager.getSelectedObjects();
	setRightBottomPanelOnSelection(selectedObjects);
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
		documentEditorTree.setName("bioModelEditorTree");
		ToolTipManager.sharedInstance().registerComponent(documentEditorTree);			
		documentEditorTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		int rowHeight = documentEditorTree.getRowHeight();
		if(rowHeight < 10) { 
			rowHeight = 20; 
		}
		documentEditorTree.setRowHeight(rowHeight + 2);
		
		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		databaseWindowPanel = new DatabaseWindowPanel(false, false);
		leftTabbedPaneBottom  = new JTabbedPane();
		leftTabbedPaneBottom.addTab("VCell Database", databaseWindowPanel);
		
		JScrollPane treePanel = new javax.swing.JScrollPane(documentEditorTree);		
		treePanel.setMinimumSize(new java.awt.Dimension(198, 148));
		leftSplitPane.setTopComponent(treePanel);
		leftTabbedPaneBottom.setMinimumSize(new java.awt.Dimension(198, 148));
		leftSplitPane.setBottomComponent(leftTabbedPaneBottom);
		leftSplitPane.setResizeWeight(0.5);
		leftSplitPane.setDividerLocation(300);
		leftSplitPane.setDividerSize(8);
		leftSplitPane.setOneTouchExpandable(true);

		rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightSplitPane.setResizeWeight(0.7);
		rightSplitPane.setDividerLocation(550);
		rightSplitPane.setDividerSize(8);
		rightSplitPane.setOneTouchExpandable(true);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(270);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.3);
		splitPane.setDividerSize(8);
		splitPane.setLeftComponent(leftSplitPane);
		splitPane.setRightComponent(rightSplitPane);
		
		add(splitPane, BorderLayout.CENTER);
		
		
		selectionManager.addPropertyChangeListener(ivjEventHandler);
		
		databaseWindowPanel.setSelectionManager(selectionManager);
		
		documentEditorTree.addTreeSelectionListener(ivjEventHandler);
		documentEditorTree.addMouseListener(ivjEventHandler);
		
		getMenuItemExpandAll().addActionListener(ivjEventHandler);
		getMenuItemCollapseAll().addActionListener(ivjEventHandler);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void treeSelectionChanged0() {
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
		selectionManager.setSelectedObjects(selectedObjects.toArray());
	}catch (Exception ex){
		ex.printStackTrace(System.out);
	}
}

protected abstract void setRightBottomPanelOnSelection(Object[] selections);
protected abstract void treeSelectionChanged();

private JPopupMenu getPopupMenu() {
	if (popupMenu == null) {
		try {
			popupMenu = new javax.swing.JPopupMenu();
			popupMenu.add(getMenuItemExpandAll());
			popupMenu.add(getMenuItemCollapseAll());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return popupMenu;
}

private JMenuItem getMenuItemExpandAll() {
	if (expandAllMenuItem == null) {
		try {
			expandAllMenuItem = new javax.swing.JMenuItem("Expand All");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return expandAllMenuItem;
}
private JMenuItem getMenuItemCollapseAll() {
	if (collapseAllMenuItem == null) {
		try {
			collapseAllMenuItem = new javax.swing.JMenuItem("Collapse All");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return collapseAllMenuItem;
}

private void expandAll(BioModelNode treeNode, boolean bExpand) {
	int childCount = treeNode.getChildCount();
	if (childCount > 0) {
		for (int i = 0; i < childCount; i++) {
			TreeNode n = treeNode.getChildAt(i);
			if (n instanceof BioModelNode) {
				expandAll((BioModelNode)n, bExpand);
			}
		}
		if (!bExpand) {
			documentEditorTree.collapsePath(new TreePath(treeNode.getPath()));
		}
	} else {
		TreePath path = new TreePath(treeNode.getPath());
		if (bExpand && !documentEditorTree.isExpanded(path)) {
			documentEditorTree.expandPath(path.getParentPath());
		} 
	}
}

}