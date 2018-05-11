/*
 * @(#)UniteAppendMerge.java	1.8 06/10/30
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package javax.help;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Locale;
/**
 * Unite-append merge type
 *
 * @author Richard Gregor
 * @version 	1.8	10/30/06
 */

public class UniteAppendMerge extends Merge{

    /**
     * Constructs UniteAppendMerge
     *
     * @param master The master NavigatorView
     * @param slave The slave NavigatorView
     */
    public UniteAppendMerge(NavigatorView master, NavigatorView slave) {
        super(master, slave);
    }
    
    /**
     * Processes unite-append merge 
     *
     * @param node The master node
     * @return Merged master node
     */
    public TreeNode processMerge(TreeNode node) {
        
	DefaultMutableTreeNode masterNode = (DefaultMutableTreeNode) node;
        
        //if master and slave are the same object return the
	// masterNode 
        if (masterNode.equals(slaveTopNode)) {
            return masterNode;
        }
        
	// If there are not children in slaveTopNode return the
	// masterNode
        if (slaveTopNode.getChildCount() == 0) { 
            return masterNode;
        }
        
	mergeNodes(masterNode, slaveTopNode);
        return masterNode;
    }
    
    
    /**
     * Merge Nodes. Merge two nodes according to the UniteAppend merging rules 
     *
     * @param masterNode The master node to merge with 
     * @param slaveNode The node to merge into the master
     */
    public static void mergeNodes(TreeNode master, TreeNode slave) {
       DefaultMutableTreeNode masterNode = (DefaultMutableTreeNode)master;
       DefaultMutableTreeNode slaveNode = (DefaultMutableTreeNode)slave;
       debug("mergeNodes master=" + MergeHelpUtilities.getNodeName(masterNode) + 
	     " slave=" + MergeHelpUtilities.getNodeName(slaveNode));       


       int masterCnt = masterNode.getChildCount();

       // loop thru the slaves
       while (slaveNode.getChildCount() > 0) {
	    DefaultMutableTreeNode slaveNodeChild = 
		(DefaultMutableTreeNode)slaveNode.getFirstChild();
	    
	    //loop thru the master children
	    for (int m=0; m < masterCnt; m++) {
		DefaultMutableTreeNode masterAtM = 
		    (DefaultMutableTreeNode)masterNode.getChildAt(m);
		
		// see if the names are the same
		if (MergeHelpUtilities.compareNames(masterAtM, 
						    slaveNodeChild) == 0) {
		    // See if the IDs are the same
		    if (MergeHelpUtilities.haveEqualID(masterAtM,
							  slaveNodeChild)) {
			// ID and name the same merge the slave node in
			MergeHelpUtilities.mergeNodes("javax.help.UniteAppendMerge",
						      masterAtM,
						      slaveNodeChild);
			// Need to remove the slaveNodeChild from the list
			slaveNodeChild.removeFromParent();
			slaveNodeChild = null;
			break;
		    } else {
			// Names are the same but the ID are not
			// Mark the nodes and add the slaveChild
			MergeHelpUtilities.markNodes(masterAtM, 
						     slaveNodeChild);
			masterNode.add(slaveNodeChild);
			MergeHelpUtilities.mergeNodeChildren("javax.help.UniteAppendMerge",
							     slaveNodeChild);
			slaveNodeChild = null;
			break;
		    }
		}
	    }
	    if (slaveNodeChild != null) {
		masterNode.add(slaveNodeChild);
		MergeHelpUtilities.mergeNodeChildren("javax.help.UniteAppendMerge",
						     slaveNodeChild);
	    }
       }
       // There are no more children.
       // Remove slaveNode from it's parent
       slaveNode.removeFromParent();
       slaveNode = null;
    }

    /**
     * Merge Node Children. Merge the children of a node according to the
     * UniteAppend merging.
     *
     * @param node The parent node from which the children are merged
     */
    public static void mergeNodeChildren(TreeNode node) {
	DefaultMutableTreeNode masterNode = (DefaultMutableTreeNode)node;
	debug("mergeNodes master=" + MergeHelpUtilities.getNodeName(masterNode));
	
	
	// The rules are there are no rules. Nothing else needs to be done
	// except for merging through the children
	for (int i=0; i < masterNode.getChildCount(); i++) {
	    DefaultMutableTreeNode child = 
		(DefaultMutableTreeNode)masterNode.getChildAt(i);
	    if (!child.isLeaf()) {
		MergeHelpUtilities.mergeNodeChildren("javax.help.UniteAppendMerge", child);
	    }
	}
    }

    private static boolean debug = false;
    private static void debug(String msg) {
        if (debug) {
            System.out.println("UniteAppendMerge :"+msg);
	}
    }
     
}
