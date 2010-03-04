package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class BatchRunTree extends JTree {
	public final static String TREE_ROOT_STR = "FRAP Batch Run";
	public final static String TREE_DOC_STR = "Documents";
	public final static String TREE_RESULT_STR = "Results";
	public final static DefaultMutableTreeNode FRAP_BATCHRUN_NODE = new DefaultMutableTreeNode(TREE_ROOT_STR); //root
    public final static DefaultMutableTreeNode FRAP_BATCHRUN_DOC_NODE = new DefaultMutableTreeNode(TREE_DOC_STR); //second to root
    public final static DefaultMutableTreeNode FRAP_BATCHRUN_RESULT_NODE = new DefaultMutableTreeNode(TREE_RESULT_STR); //second to root

    protected DefaultTreeModel treeModel;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    
    public BatchRunTree() 
    {
		//set up tree
	    FRAP_BATCHRUN_NODE.add(FRAP_BATCHRUN_DOC_NODE);
	    FRAP_BATCHRUN_NODE.add(FRAP_BATCHRUN_RESULT_NODE);
	    treeModel = new DefaultTreeModel(FRAP_BATCHRUN_NODE);
//	    treeModel.addTreeModelListener(new MyTreeModelListener());
	    setModel(treeModel);
	    //selection model
	    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    setFont(new Font("Tahoma", Font.PLAIN, 11));
 
    }

    // Remove all nodes except the root node. 
    public void clearAll() {
    	FRAP_BATCHRUN_DOC_NODE.removeAllChildren();
    	FRAP_BATCHRUN_RESULT_NODE.removeAllChildren();
        treeModel.nodeStructureChanged(FRAP_BATCHRUN_NODE);
    }
    //clear loaded vfrap documents
    public void clearResults()
    {
    	FRAP_BATCHRUN_RESULT_NODE.removeAllChildren();
    	treeModel.nodeStructureChanged(FRAP_BATCHRUN_RESULT_NODE);
    }
    //clear results if any
    public void clearDocs()
    {
    	FRAP_BATCHRUN_DOC_NODE.removeAllChildren();
    	treeModel.nodeStructureChanged(FRAP_BATCHRUN_DOC_NODE);
    }
    
    /** Remove the currently selected node. */
    public DefaultMutableTreeNode removeCurrentNode() {
        TreePath currentSelection = getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode)(currentNode.getParent());
            if (parent != null && parent != FRAP_BATCHRUN_NODE) 
            {
                treeModel.removeNodeFromParent(currentNode);
            }
            return parent;
        } 
        return null;
    }

    /** Add child to the currently selected node. */
    public DefaultMutableTreeNode addBatchRunDocNode(Object child) {
        DefaultMutableTreeNode parentNode = FRAP_BATCHRUN_DOC_NODE;
        TreePath parentPath = new TreePath(FRAP_BATCHRUN_DOC_NODE.getPath());

        if (parentPath == null) {
            parentNode = FRAP_BATCHRUN_NODE;
        } 

        return addTreeNode(parentNode, child, true);
    }
    
    public DefaultMutableTreeNode addBatchRunResultNode(Object child) {
        DefaultMutableTreeNode parentNode = FRAP_BATCHRUN_RESULT_NODE;
        TreePath parentPath = new TreePath(FRAP_BATCHRUN_RESULT_NODE.getPath());

        if (parentPath == null) {
            parentNode = FRAP_BATCHRUN_NODE;
        } 

        return addTreeNode(parentNode, child, true);
    }

    public DefaultMutableTreeNode addTreeNode(DefaultMutableTreeNode parent, Object child, boolean bVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = FRAP_BATCHRUN_NODE;
        }
	
	   //It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        //Make sure the user can see the new node.
        if (bVisible) {
            scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }
    
	
	
	/*// for initating cellWareViewTree
	public void initiateFrapViewTree(FRAPBatchRunWorkspace brWorkSpace)
	{
		if(true)//TODO: put condition
		{
			batchRunDocNode.removeAllChildren();
//	    	  frapDocTreeNode.add  //TODO: add children

			expendFRAPViewTree();
			frapBatchRunViewTree.updateUI();
		}
	}// end of method initiateCellWareViewTree

	protected void expendFRAPViewTree()
	{
		DefaultMutableTreeNode node=batchRunDocNode.getFirstLeaf();
		while (node != null)
		{
	        DefaultMutableTreeNode parentNode=(DefaultMutableTreeNode)node.getParent();
	         if (parentNode == null) return; 
	        TreeNode[] treeNodeArray = parentNode.getPath();
	        TreePath path = new TreePath(treeNodeArray);

	        if (path != null)
	            frapBatchRunViewTree.expandPath(path);
	        // and get the next sibling
	        node = node.getNextLeaf();
		}
	}

	// for updating cellWareViewTree
	public void updateFRAPViewTree(FRAPBatchRunWorkspace brWorkSpace)
	{
		if(brWorkSpace!=null)
		{
			initiateFrapViewTree(brWorkSpace);

			//TODO: add tree nodes
			
			expendFRAPViewTree();
		}
		else
		{
			batchRunDocNode.removeAllChildren();
		}
		frapBatchRunViewTree.updateUI();
	}// end of method initiateCellWareViewTree
*/

   
}
