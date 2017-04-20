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

import java.awt.Component;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * The class MyTreeRenderer is written for customize the tree cell renderers.
 */
@SuppressWarnings("serial")
public class BatchRunTreeRenderer extends DefaultTreeCellRenderer
{
    private final ImageIcon rootIcon = new ImageIcon(getClass().getResource("/images/project.gif"));
    private final ImageIcon batchRunDocIcon = new ImageIcon(getClass().getResource("/images/batchDocNode.gif"));
    private final ImageIcon batchRunResultIcon = new ImageIcon(getClass().getResource("/images/batchResultNode.gif"));
    private final ImageIcon treeDocLeafIcon = new ImageIcon(getClass().getResource("/images/treeLeaf_doc.gif"));
    private final ImageIcon treeReLeafIcon = new ImageIcon(getClass().getResource("/images/treeLeaf_re.gif"));
    
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus)
    {
        super.getTreeCellRendererComponent( tree, value, sel,expanded, leaf, row,hasFocus);
        if (((DefaultMutableTreeNode)value).isRoot())
        {
            setIcon(rootIcon);
        }
        else if(((DefaultMutableTreeNode)value).equals(BatchRunTree.FRAP_BATCHRUN_DOC_NODE))
        {
        	setIcon(batchRunDocIcon);
        }
        else if(((DefaultMutableTreeNode)value).equals(BatchRunTree.FRAP_BATCHRUN_RESULT_NODE))
        {
        	setIcon(batchRunResultIcon);
        }
        else if(((DefaultMutableTreeNode)value).isLeaf())
        {
        	if(((DefaultMutableTreeNode)value).getParent().equals(BatchRunTree.FRAP_BATCHRUN_DOC_NODE))
        	{
        		setIcon(treeDocLeafIcon);
        		setText(((File)(((DefaultMutableTreeNode)value).getUserObject())).getName());
        		setToolTipText(((File)((DefaultMutableTreeNode)value).getUserObject()).getAbsolutePath());
        	}
        	else
        	{
        		setIcon(treeReLeafIcon);
        		setText(((DefaultMutableTreeNode)value).toString());
        	}	
        }
        else
        {
        	setIcon(null);
        }
        
        return this;
    }
}
