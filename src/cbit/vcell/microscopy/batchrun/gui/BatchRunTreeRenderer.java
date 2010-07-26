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
public class BatchRunTreeRenderer extends DefaultTreeCellRenderer
{
    protected ImageIcon rootIcon = new ImageIcon(getClass().getResource("/images/project.gif"));
    protected ImageIcon batchRunDocIcon = new ImageIcon(getClass().getResource("/images/batchDocNode.gif"));
    protected ImageIcon batchRunResultIcon = new ImageIcon(getClass().getResource("/images/batchResultNode.gif"));
    protected ImageIcon treeDocLeafIcon = new ImageIcon(getClass().getResource("/images/treeLeaf_doc.gif"));
    protected ImageIcon treeReLeafIcon = new ImageIcon(getClass().getResource("/images/treeLeaf_re.gif"));
    
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