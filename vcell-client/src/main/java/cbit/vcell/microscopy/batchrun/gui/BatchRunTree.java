/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

@SuppressWarnings("serial")
public class BatchRunTree extends JTree {
	public final static String TREE_ROOT_STR = "FRAP Batch Run";
	public final static String TREE_DOC_STR = "Documents";
	public final static String TREE_RESULT_STR = "Results";
	public final static DefaultMutableTreeNode FRAP_BATCHRUN_NODE = new DefaultMutableTreeNode(TREE_ROOT_STR); //root
    public final static DefaultMutableTreeNode FRAP_BATCHRUN_DOC_NODE = new DefaultMutableTreeNode(TREE_DOC_STR); //second to root
    public final static DefaultMutableTreeNode FRAP_BATCHRUN_RESULT_NODE = new DefaultMutableTreeNode(TREE_RESULT_STR); //second to root

    protected DefaultTreeModel treeModel;
    
    public BatchRunTree() 
    {
		//set up tree
	    FRAP_BATCHRUN_NODE.add(FRAP_BATCHRUN_DOC_NODE);
	    FRAP_BATCHRUN_NODE.add(FRAP_BATCHRUN_RESULT_NODE);
	    treeModel = new DefaultTreeModel(FRAP_BATCHRUN_NODE);
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

        DefaultMutableTreeNode newNode = addTreeNode(parentNode, child, true);
        return newNode;
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
    
    public DefaultMutableTreeNode orderFRAPDocChildren(DefaultMutableTreeNode newNode) {
    	DefaultMutableTreeNode orderedNode = newNode;
		if(FRAP_BATCHRUN_DOC_NODE.getChildCount() > 1)
		{
			ArrayList<DefaultMutableTreeNode> children = new ArrayList<DefaultMutableTreeNode>();
			for(int i=0; i<FRAP_BATCHRUN_DOC_NODE.getChildCount(); i++)
			{
				children.add(new DefaultMutableTreeNode(((DefaultMutableTreeNode)FRAP_BATCHRUN_DOC_NODE.getChildAt(i)).getUserObject()));
			}
			Collections.sort(children, nodeComparator);
			FRAP_BATCHRUN_DOC_NODE.removeAllChildren();
			children.size();
			for(int i=0; i < children.size(); i++ )
			{
				if(((File)newNode.getUserObject()).getAbsolutePath().equals(((File)children.get(i).getUserObject()).getAbsolutePath()))
				{
					orderedNode = addBatchRunDocNode(children.get(i).getUserObject());
				}
				else
				{
					addBatchRunDocNode(children.get(i).getUserObject());
				}
			}
			treeModel.nodeStructureChanged(FRAP_BATCHRUN_DOC_NODE);
		}
		return orderedNode;
    }

    private Comparator<DefaultMutableTreeNode> nodeComparator = new Comparator<DefaultMutableTreeNode> () {
        //Override
        public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
        	Object o1UserObject = o1.getUserObject();
    		Object o2UserObject = o2.getUserObject();
        	if(o1UserObject instanceof File && o2UserObject  instanceof File)
        	{
        		
        		if(((File)o1UserObject).getName().compareTo(((File)o2UserObject).getName())==0)
        		{
        			return ((File)o1UserObject).getAbsolutePath().compareTo(((File)o2UserObject).getAbsolutePath());
        		}
        		else
        		{
        			return ((File)o1UserObject).getName().compareTo(((File)o2UserObject).getName());
        		}
        	}
            return o1UserObject.toString().compareTo(o2UserObject.toString());
        }
     
        //Override
        public boolean equals(Object obj)    {
            return false;
        }
     
        //Override
        public int hashCode() {
            int hash = 7;
            return hash;
        }
    };
}
