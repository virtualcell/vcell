package cbit.vcell.microscopy.batchrun.gui; 

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.*;

import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.io.File;

/**
 * The Class handles action events, which is triggered by both
 * tree-selection events and mouse-click events. 
 * @author Tracy Li
 * @version 1.0
 */
public class TreeHandler extends MouseAdapter implements TreeSelectionListener
{
    private DefaultMutableTreeNode parent;
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
	           System.out.println("batch run files selected:" + ((File)selectedInfo).getAbsolutePath());
	           getBatchRunWorkspace().updateDisplayForTreeSelection(selectedInfo);
           }
       }
       else if(parentInfo != null && parentInfo instanceof String && ((String)parentInfo).equals(BatchRunTree.TREE_RESULT_STR))
       {
    	    if(selectedInfo != null && selectedInfo instanceof String) //selected node is a batch run result
           {
               System.out.println("batch run results selected:" + (String)selectedInfo);
               getBatchRunWorkspace().updateDisplayForTreeSelection(selectedInfo); //TODO: shift to result panel.
           }
       }
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
        	e.toString();
        	boolean f = e.getSource() instanceof String;
//            TreePath path = detailsFrame.cellWareViewTree.getSelectionPath();
//            Object node = path.getLastPathComponent();
//            // DefaultMutableTreeNode node = (DefaultMutableTreeNode)detailsFrame.cellWareViewTree.getLastSelectedPathComponent();
//            if (node instanceof Experiment) {
//                Experiment exp = (Experiment) node;
//                StandardInternalFrame report = exp.getReprot();
//                MainFrame.mf.workingFrame.desktop.moveToFront(report);
//                try{
//                    if (report.isIcon()) report.setIcon(false);
//                    report.setSelected(true);
//                }catch (java.beans.PropertyVetoException ex) {ex.printStackTrace();}
//                // MainFrame.mf.workingFrame.desktop.setSelectedFrame(report); // TODO: XXX doesn't work?
//
//            }

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
