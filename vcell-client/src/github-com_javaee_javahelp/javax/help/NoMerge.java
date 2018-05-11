/*
 * @(#)NoMerge.java	1.3 06/10/30
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

/**
 * No merge type
 *
 * @author Roger Brinkley
 * @version	1.3	10/30/06
 */
public class NoMerge extends Merge{

    /**
     * Constructs NoMerge
     *
     * @param master The master NavigatorView
     * @param slave The slave NavigatorView
     */ 
    public NoMerge(NavigatorView master, NavigatorView slave){
        super(master,slave);

    }
    
    /**
     * Processes no merge
     *
     * @param node The master node
     * @return the master node
     */
    public TreeNode processMerge(TreeNode node){
       debug("start merge");
       DefaultMutableTreeNode masterNode = (DefaultMutableTreeNode)node;
 
       // There is no merging here but we will make sure
       // the master children are handled correctly
       MergeHelpUtilities.mergeNodeChildren("javax.help.NoMerge",
					    masterNode);      
       return masterNode;
    }
    
    /**
     * Merge Nodes. Merge two nodes according to the merging rules of the
     * masterNode. Each Subclass should override this implementation.
     *
     * @param master The master node to merge with 
     * @param slave The node to merge into the master
     */
    public static void mergeNodes(TreeNode master, TreeNode slave) {
	// Doesn't do anything
    }

    /**
     * Merge Node Children. Merge the children of a node according to the
     * merging rules of the parent. Each subclass must implement this method
     *
     * @param node The parent node from which the children are merged
     */
    public static void mergeNodeChildren(TreeNode node) {
	// Doesn't do anything
    }
    
    private static boolean debug = false;
    private static void debug(String msg){
        if (debug) {
            System.out.println("NoMerge :"+msg);
	}
    }
}
    
