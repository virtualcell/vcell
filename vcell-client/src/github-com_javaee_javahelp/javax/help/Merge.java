/*
 * @(#)Merge.java	1.6 06/10/30
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

import java.util.Locale;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import java.lang.reflect.Constructor;

/**
 * Common superclass for all merge types
 *
 * @author  Richard Gregor
 * @version	1.6	10/30/06 
 */
public abstract class Merge {

    /**
     * Slave node
     */
    protected DefaultMutableTreeNode slaveTopNode;

    /**
     * HelpSet's locale which is used in sorting
     */
    protected Locale locale;
    
    /**
     * Constructs Merge for master and slave NavigatorViews
     *
     * @param master The master NavigatorView
     * @param slave The slave NavigatorView
     */
    protected Merge(NavigatorView master, NavigatorView slave){
        
        try {
            Class clss = Class.forName("javax.help.TOCView");
            if (clss.isInstance(slave)) {
                this.slaveTopNode = ((TOCView)slave).getDataAsTree();
	    }
            clss = Class.forName("javax.help.IndexView");
            if (clss.isInstance(slave)) {
                this.slaveTopNode = ((IndexView)slave).getDataAsTree();
	    }
        } catch(ClassNotFoundException exp) {
            System.err.println(exp);
        }
                       
        locale = master.getHelpSet().getLocale();
        if(locale == null)
            locale = Locale.getDefault();
    }
    
    /**
     * Processes merge. Changes master node according merge rules using slave node.
     *
     * @param node The master node 
     * @return The changed master node
     */
    public abstract TreeNode processMerge(TreeNode node);

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
    
    /**
     * Default Merge factory which creates concrete Merge objects
     */ 
    public static class DefaultMergeFactory {

        /**
         * Returns suitable Merge object
         *
         * @param masterView The master NavigatorView
         * @param slaveView The slave NavigatorView
         * @return The Merge object
         */
        public static Merge getMerge(NavigatorView masterView, NavigatorView slaveView) {
            
            Merge mergeObject = null;
	    // throw an NPE early
	    if (masterView == null || slaveView == null) {
		throw new NullPointerException("masterView and/or slaveView are null");

	    }
            String mergeType = masterView.getMergeType();
	    HelpSet hs = masterView.getHelpSet();
            Locale locale = hs.getLocale();
	    ClassLoader loader = hs.getLoader();
	    Class klass;
	    Constructor konstructor;
            
	    if (mergeType != null) {
		try {
		    Class types[] = { NavigatorView.class, 
				      NavigatorView.class };
		    Object args[] = { masterView, slaveView };
		    if (loader == null) {
			klass = Class.forName(mergeType);
		    } else {
			klass = loader.loadClass(mergeType);
		    }
		    konstructor = klass.getConstructor(types);
		    mergeObject = (Merge) konstructor.newInstance(args);
		} catch (Exception ex) {
		    ex.printStackTrace();
		    throw new RuntimeException("Could not create Merge type " +
					       mergeType);
		}
	    } else {
		mergeObject = new AppendMerge(masterView, slaveView);
	    }
            return mergeObject;
        }
    }
}
