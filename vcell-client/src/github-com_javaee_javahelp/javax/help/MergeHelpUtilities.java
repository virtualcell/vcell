/*
 * @(#)MergeHelpUtilities.java	1.9 06/10/30
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

import javax.swing.tree.*;
import java.util.*;
import java.text.*;
import java.lang.reflect.Method;

/**
 * Common utilities for merge types
 *
 * @author  Richard Gregor
 * @version	1.9	10/30/06
 */

public class MergeHelpUtilities extends Object {


    /**
     * Merge the nodes according the merging rules of the masterNode's
     * merge type
     * 
     * @param masterMergeType The fallback mergeType if masterNode does not have a mergeType
     * @param masterNode The master node to merge into
     * @param slaveNode A secondary node that will merge into the master node
     */
    public static void mergeNodes(String masterMergeType,
				  DefaultMutableTreeNode masterNode,
				  DefaultMutableTreeNode slaveNode) {
	if (slaveNode.isLeaf()) {
	    return;
	}
	String mergeType = getMergeType(masterNode);
	if (mergeType == null) {
	    mergeType = masterMergeType;
	}

	Class klass;
	Class types[] =  { TreeNode.class, 
			   TreeNode.class };
	Method m;
	Object args[] = { masterNode, slaveNode };
	try {
	    klass = Class.forName(mergeType);
	    m = klass.getDeclaredMethod("mergeNodes", types);
	    m.invoke(null, args);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    throw new RuntimeException
		("Could not find or execute mergeNodes for " +
		 mergeType);
	}
    }

    /**
     * Merge a nodes children according the merging rules of the node's
     * merge type
     *
     * @param masterMergeType The fallback mergeType if masterNode does not have a mergeType
     * @param node The node
     */
    public static void mergeNodeChildren(String masterMergeType, DefaultMutableTreeNode node) {
	if (node.isLeaf()) {
	    return;
	}
	String mergeType = getMergeType(node);
	if (mergeType == null) {
	    mergeType = masterMergeType;
	}
	Class klass;
	Class types[] =  { TreeNode.class };
	Method m;
	Object args[] = { node };
	try {
	    klass = Class.forName(mergeType);
	    m = klass.getDeclaredMethod("mergeNodeChildren", types);
	    m.invoke(null, args);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    throw new RuntimeException
		("Could not find or execute mergeNodeChildren for " +
		 mergeType);
	}
    }

    /**
     * Returns node's merge type
     *
     * @param node The node
     * @return The node's merge type
     */
    private static String getMergeType(DefaultMutableTreeNode node) { 
        TreeItem item = (TreeItem)node.getUserObject();
        if (item == null) {
            return null;
        } else {
            return item.getMergeType();
	}
    }
    
    /**
     * Returns name of node
     *
     * @param node The node
     * @return The name of node 
     */
    public static String getNodeName(DefaultMutableTreeNode node) {
	if (node == null) {
	    return null;
	}
        TreeItem item = (TreeItem)node.getUserObject();
	if (item == null) {
	    return null;
	}
        return item.getName();
    }
    
    /**
     * Returns node with given name
     *
     * @param parent The parent node 
     * @param name The name of child
     * @return The child with given name
     */
    public static DefaultMutableTreeNode getChildWithName(DefaultMutableTreeNode parent,String name) {
        DefaultMutableTreeNode childAtI = null;
        DefaultMutableTreeNode result = null;
        
        for (int i = 0; i < parent.getChildCount(); i++) {
            childAtI = (DefaultMutableTreeNode)parent.getChildAt(i);
            if (getNodeName(childAtI).equals(name)) {
                result = childAtI;
                break;
            }
        }
        return result;
        
    }
    
    /**
     * Returns locale of node
     *
     * @param node The node
     * @return The locale object
     */    
    public static Locale getLocale(DefaultMutableTreeNode node) {
	Locale locale = null;
        TreeItem item = (TreeItem)node.getUserObject();
        if (item != null) {
            locale = item.getLocale();
	} 
	if (locale == null) {
            locale = Locale.getDefault();
	}
	return locale;
    }
    
    /**
     * Compares name of nodes
     *
     * @param master The master node
     * @param slave The slave node
     * @return negative is master is lexically lower than slave;
     * positive if master is lexically higher than slave and zero if lexically
     * identical.
     */
    public static int compareNames(DefaultMutableTreeNode master, DefaultMutableTreeNode slave) {

        TreeItem masterItem = (TreeItem)master.getUserObject();
        debug("haveEqualName - master:"+masterItem);
        TreeItem slaveItem = (TreeItem)slave.getUserObject();
        debug("haveEqualName - slave:"+slaveItem);
        
	String masterName = masterItem.getName();
        if (masterName == null) {
            masterName = " ";
	}

	String slaveName = slaveItem.getName();
        if (slaveName == null) {
            slaveName = " ";
	}

	Collator collator = Collator.getInstance(getLocale(master));
        return collator.compare(masterName, slaveName);
    }
        
    /**
     * Method for comparing ID of nodes
     *
     * @param master The master node
     * @param slave The slave node
     * @return True if ID is the same
     */
    public static boolean haveEqualID(DefaultMutableTreeNode master, DefaultMutableTreeNode slave) {
        TreeItem masterItem = (TreeItem)master.getUserObject();
        TreeItem slaveItem = (TreeItem)slave.getUserObject();
        
        if (masterItem.getID() == null) {
            if (slaveItem.getID() == null) {
                return true;
            } else {
                return false;
	    }
        } else {
            if (slaveItem.getID() == null) {
                return false;
	    }
	}
        
        //items are not null
        
        Map.ID masterMapID = masterItem.getID();
        Map.ID slaveMapID = slaveItem.getID();
        
        if (masterMapID.id == null) {
            if (slaveMapID.id == null) {
                return true;
            } else {
                return false;
	    }
        } else {
            if (slaveMapID.id == null) {
                return false;
	    }
	}
        
        return masterMapID.id.equals(slaveMapID.id);
    }
    
        
    /**
     * Marks nodes with the same name but diferent IDs with their HelpSet title
     *
     * @param master The master node to mark
     * @param slave The slave node to mark
     */
    
    public static void markNodes(DefaultMutableTreeNode master, DefaultMutableTreeNode slave) {
        debug("MarkNodes");
        TreeItem masterItem = (TreeItem)master.getUserObject();
        TreeItem slaveItem = (TreeItem)slave.getUserObject();
        HelpSet masterHS = masterItem.getHelpSet();
        HelpSet slaveHS = slaveItem.getHelpSet();

	if (masterItem.getName() != null) {
	    masterItem.setName(masterItem.getName()+"("+masterHS.getTitle()+")");
	} else { 
	    masterItem.setName(masterItem.getName()+"("+masterHS.getTitle()+")");
	}
	if (slaveItem.getName() != null) {
	    slaveItem.setName(slaveItem.getName()+"("+slaveHS.getTitle()+")");
	} else {
	    slaveItem.setName(slaveItem.getName()+"("+slaveHS.getTitle()+")");
	}
    }

    private static boolean debug = false;
    private static void debug(String msg) {
        if (debug) {
            System.out.println("MergeHelpUtilities :"+msg);
	}
    }
}

