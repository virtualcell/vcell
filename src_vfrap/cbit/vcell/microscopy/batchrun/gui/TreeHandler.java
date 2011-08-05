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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;

/**
 * The Class handles action events, which is triggered by both
 * tree-selection events and mouse-click events. 
 * @author Tracy Li
 * @version 1.0
 */
public class TreeHandler extends MouseAdapter implements TreeSelectionListener
{
    private Object parentInfo;
    private Object selectedInfo;
    private FRAPBatchRunWorkspace batchRunWorkspace = null;
    
	public void valueChanged(TreeSelectionEvent e)
    {
       JTree batchRunTree = (JTree)e.getSource();
       DefaultMutableTreeNode node = (DefaultMutableTreeNode)batchRunTree.getLastSelectedPathComponent();
       if (node == null)
       {
    	   return;
       }
       parentInfo = ((DefaultMutableTreeNode)node.getParent()).getUserObject();
       selectedInfo = node.getUserObject();
       if(parentInfo != null && parentInfo instanceof String && ((String)parentInfo).equals(BatchRunTree.TREE_DOC_STR))
       {
           if(selectedInfo != null && selectedInfo instanceof File)//selected node is a batch run document
           {
	           getBatchRunWorkspace().updateDisplayForTreeSelection(selectedInfo);
           }
       }
       else if(parentInfo != null && parentInfo instanceof String && ((String)parentInfo).equals(BatchRunTree.TREE_RESULT_STR))
       {
    	    if(selectedInfo != null && selectedInfo instanceof String) //selected node is a batch run result
           {
               getBatchRunWorkspace().updateDisplayForTreeSelection(selectedInfo); 
           }
       }
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
        	e.toString();

        }
    }

    public void mousePressed(MouseEvent e)
    {
      
    }
    public FRAPBatchRunWorkspace getBatchRunWorkspace() {
		return batchRunWorkspace;
	}

	public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace) {
		this.batchRunWorkspace = batchRunWorkspace;
	}
}
