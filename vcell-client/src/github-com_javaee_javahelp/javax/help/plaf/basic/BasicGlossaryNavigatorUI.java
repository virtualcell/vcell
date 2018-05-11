/*
 * @(#)BasicGlossaryNavigatorUI.java	1.8 06/10/30
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

package javax.help.plaf.basic;

import javax.help.*;
import javax.help.plaf.HelpNavigatorUI;
import javax.help.plaf.HelpUI;
import javax.help.plaf.basic.BasicHelpUI;
import javax.help.plaf.basic.BasicIndexCellRenderer;
import javax.help.event.HelpModelListener;
import javax.help.event.HelpModelEvent;
import java.util.EventObject;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.help.Map.ID;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Locale;
import java.util.Stack;

/**
 * The default UI for JHelpNavigator of type Glossary.
 *
 * @author Roger D. Brinkley
 * @author Richard Gregor
 * @version   1.8     10/30/06
 */

public class BasicGlossaryNavigatorUI extends HelpNavigatorUI
implements HelpModelListener, TreeSelectionListener,
PropertyChangeListener, ActionListener, Serializable {
    protected JHelpGlossaryNavigator glossary;
    protected JScrollPane sp;
    protected DefaultMutableTreeNode topNode;
    protected JTree tree;
    protected JTextField searchField;
    protected RuleBasedCollator rbc;
    protected String oldText;
    protected DefaultMutableTreeNode currentFindNode;
    protected JHelpContentViewer viewer;
    
    /**
     * Creates UI
     */
    public static ComponentUI createUI(JComponent x) {
        return new BasicGlossaryNavigatorUI((JHelpGlossaryNavigator) x);
    }
    
    public BasicGlossaryNavigatorUI(JHelpGlossaryNavigator b) {
        ImageIcon icon = getImageIcon(b.getNavigatorView());
        if (icon != null) {
            setIcon(icon);
        } else {
	    setIcon(UIManager.getIcon("GlossaryNav.icon"));
	}
    }
    
    public void installUI(JComponent c) {
        debug("installUI");
        
        glossary = (JHelpGlossaryNavigator)c;
        HelpModel model = glossary.getModel();
        
        glossary.setLayout(new BorderLayout());
        glossary.addPropertyChangeListener(this);
        if (model != null) {
            //model.addHelpModelListener(this); // for our own changes
        }
        
        topNode = new DefaultMutableTreeNode();
        
        JLabel search = new JLabel(HelpUtilities.getString(HelpUtilities.getLocale(c),
        "index.findLabel"));
        searchField= new JTextField();
        search.setLabelFor(searchField);
        
        searchField.addActionListener(this);
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
        box.add(search);
        box.add(searchField);
        
        glossary.add("North", box);
        
        tree = new JTree(topNode);
        TreeSelectionModel tsm = tree.getSelectionModel();
        tsm.addTreeSelectionListener(this);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        
        setCellRenderer(glossary.getNavigatorView(), tree);
        
        sp = new JScrollPane();
        sp.getViewport().add(tree);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
        splitPane.setOneTouchExpandable(true);
        
        splitPane.setTopComponent(sp);
        
        viewer = new JHelpContentViewer(model.getHelpSet());
        viewer.setSynch(false);
        splitPane.setBottomComponent(viewer);
        
        glossary.add("Center",splitPane);
        splitPane.setDividerLocation(180);
        
        reloadData();
    }
    
    /**
     * Sets the desired cell renderer on this tree.
     * This is exposed for redefinition
     * by subclases.
     */
    protected void setCellRenderer(NavigatorView view, JTree tree) {
        tree.setCellRenderer(new BasicIndexCellRenderer());
    }
    
    /**
     * Uninstalls UI
     */
    public void uninstallUI(JComponent c) {
        debug("uninstallUI");
        HelpModel model = glossary.getModel();
        
        glossary.removePropertyChangeListener(this);
        TreeSelectionModel tsm = tree.getSelectionModel();
        tsm.removeTreeSelectionListener(this);
        glossary.setLayout(null);
        glossary.removeAll();
        
        if (model != null) {
            model.removeHelpModelListener(this);
        }
        
        glossary = null;
    }
    
 
    public Dimension getPreferredSize(JComponent c) {
        
        return new Dimension(200,100);
        
    }
    
    public Dimension getMinimumSize(JComponent c) {
        return new Dimension(100,100);
    }
 
    public Dimension getMaximumSize(JComponent c) {
        return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
    }
    
    private void reloadData() {
        debug("reloadData");
                
        // parse the glossary data into topNode
        GlossaryView view = (GlossaryView) glossary.getNavigatorView();
	loadData(view);
    }

    private void loadData(GlossaryView view) {

       if (view == null) {
            return;
        }
        
        // remove all children
        topNode.removeAllChildren();
        
        String mergeType = view.getMergeType();
        
        Locale locale = view.getHelpSet().getLocale();
        
        DefaultMutableTreeNode node = view.getDataAsTree();
        
	// Make sure the children are all handled correctly
	MergeHelpUtilities.mergeNodeChildren(mergeType, node);
        
        // This is a tricky one. As you remove the entries from one node to
        // another the list shrinks. So you can't use an Enumated list to do
        // the move.
        while (node.getChildCount() > 0) {
            topNode.add((DefaultMutableTreeNode) node.getFirstChild());
        }
        
        // add all subhelpsets
        addSubHelpSets(view.getHelpSet());        

        // reload the tree data
        ((DefaultTreeModel)tree.getModel()).reload();
        
        setVisibility(topNode);
    }
    
    /**
     * Reloads the presentation data using new help model. Changes the navigator if new model contains
     * view with the same name as former view
     **/
    private void reloadData(HelpModel model) {
        debug("reloadData in using new model");
        GlossaryView glossaryView = null;
        
        HelpSet newHelpSet = model.getHelpSet();
        GlossaryView oldView = (GlossaryView) glossary.getNavigatorView();
        String oldName = oldView.getName();
        NavigatorView[] navViews = newHelpSet.getNavigatorViews();
        for(int i = 0 ; i < navViews.length; i++){
            if((navViews[i].getName()).equals(oldName)){
                NavigatorView tempView = navViews[i];
                if(tempView instanceof GlossaryView){
                    glossaryView = (GlossaryView) tempView;
                    break;
                }
            }
        }
 
	loadData(glossaryView);
    }

    /** Adds subhelpsets
     *
     * @param hs The HelpSet which subhelpsets will be added
     */
    protected void addSubHelpSets(HelpSet hs){
	debug ("addSubHelpSets");
        for( Enumeration e = hs.getHelpSets(); e.hasMoreElements(); ) {
            HelpSet ehs = (HelpSet) e.nextElement();
            // merge views
            NavigatorView[] views = ehs.getNavigatorViews();
            for(int i = 0; i < views.length; i++){
                if(glossary.canMerge(views[i]))
                    merge(views[i]);
            }
            addSubHelpSets( ehs );
        }
    }

    /**
     * Expands entry path and entry itself( when entry is not empty) for specific id
     *
     * @param target The target of entry
     */
    
    private void expand(String target){
        debug("expand called");
        //find all nodes with certain id
        Enumeration nodes = findNodes(target).elements();
        DefaultMutableTreeNode node = null;
        
        while(nodes.hasMoreElements()){
            node = (DefaultMutableTreeNode)nodes.nextElement();
            debug("expandPath :"+node);
            if(node.getChildCount() > 0){
                DefaultMutableTreeNode child =(DefaultMutableTreeNode) node.getFirstChild();
                TreePath path = new TreePath(child.getPath());
                tree.makeVisible(path);
            }
            else{
                TreeNode[] treeNode = node.getPath();
                TreePath path = new TreePath(treeNode);
                //tree.scrollPathToVisible(path);
                tree.makeVisible(path);
            }
        }
    }    
    
    /**
     * Returns all nodes with certain id
     *
     * @param target The target of entry
     *
     */
    private Vector findNodes(String target){
        Enumeration nodes = topNode.preorderEnumeration();
        DefaultMutableTreeNode node = null;
        Vector nodeFound = new Vector();
        
        while(nodes.hasMoreElements()){
            node = (DefaultMutableTreeNode)nodes.nextElement();
            debug(" node :"+ node.toString());
            if(node != null){
                IndexItem indexItem = (IndexItem)node.getUserObject();
                if(indexItem == null)
                    debug("indexItem is null");
                else{
                    Map.ID id = indexItem.getID();
                    if(id != null){
                        debug("id name :"+id.id);
                        debug("target :"+target);
                        Map.ID itemID = null;
                        try{
                            itemID = Map.ID.create(target,glossary.getModel().getHelpSet());
                        }
                        catch(BadIDException exp){
                            System.err.println("Not valid ID :"+target );
                            break;
                        }
                        if(id.equals(itemID))
                            nodeFound.addElement(node);
                    }
                }
            }
        }
        
        return nodeFound;
    }
    
    /**
     * Collapses entry specified by id. If entry is empty collapses it's parent.
     *
     * @param target The target of entry
     */
    
    private void collapse(String target){
        Enumeration nodes = findNodes(target).elements();
        DefaultMutableTreeNode node = null;
        debug("collapse called");
        
        while(nodes.hasMoreElements()){
            node = (DefaultMutableTreeNode)nodes.nextElement();
            if(node.getChildCount() > 0){
                TreeNode[] treeNode = node.getPath();
                TreePath path = new TreePath(treeNode);
                tree.collapsePath(path);
                tree.collapseRow(tree.getRowForPath(path));
            }
            else{
                DefaultMutableTreeNode parent =(DefaultMutableTreeNode) node.getParent();
                TreePath path = new TreePath(parent.getPath());
                tree.collapseRow(tree.getRowForPath(path));
            }
        }
    }
    
    
    /**
     * Merges in the navigational data from another IndexView.
     */

    public void doMerge(NavigatorView view) {
	debug("merging data");
       
        Merge mergeObject = Merge.DefaultMergeFactory.getMerge(glossary.getNavigatorView(),view);
        if(mergeObject != null) {
            mergeObject.processMerge(topNode);
	}
        
    }

    /**
     * Merges in the navigational data from another TOCView.
     *
     * @param view A GlossaryView.  Note the actual argument is a NavigatorView type
     * so it replaces the correct NavigatorUI method.
     */
    
    public void merge(NavigatorView view) {
        debug("merging data");
	doMerge(view);

        //reload the tree data
	((DefaultTreeModel)tree.getModel()).reload(); 
	setVisibility(topNode);
    }

    /**
     * Removes the navigational data from another GlossaryView.
     *
     * @param view An GlossaryView.  Note the actual argument is a NavigatorView type
     * so it replaces the correct NavigatorUI method.
     */
    
    public void remove(NavigatorView view) {
        debug("removing "+view);
        
	remove(topNode, view.getHelpSet());
        
        // reload the tree data
        ((DefaultTreeModel)tree.getModel()).reload();
        setVisibility(topNode);
    }
    
    /**
     * Recursively removes all children of the node that have either hs or a HelpSet that
     * is included in hs as their HelpSet data.
     *
     * Recursion is stopped when a node is removed.  This is because of the
     * property of the merge mechanism.
     *
     * @param node The node from which to remove children.
     * @param hs The non-null HelpSet to use.
     */
    
    private void remove(DefaultMutableTreeNode node,
			HelpSet hs) {
	debug("remove("+node+", "+hs+")");

	// a simple node.children() does not work because the
	// enumeration is voided when a child is removed

	// getNextSibling() has a linear search, so we won't do that either

	// Collect all to be removed
	Vector toRemove = new Vector();
	
	for (Enumeration e = node.children();
	     e.hasMoreElements(); ) {
	    DefaultMutableTreeNode child
		= (DefaultMutableTreeNode) e.nextElement();
	    debug("  considering "+child);
	    IndexItem item = (IndexItem) child.getUserObject();
	    HelpSet chs = item.getHelpSet();
	    debug ("chs=" + chs + " hs.contains(chs)=" + hs.contains(chs));
	    if (chs != null &&
		hs.contains(chs)) {
		if (child.isLeaf()) {
		    // if the child has no children then just remove it
		    debug("  tagging for removal: "+child);
		    toRemove.addElement(child); // tag to be removed...
		} else {
		    // be carefull here. While the child hs is one to be
		    // removed it is possible that there are children that
		    // are not of this hs. Attempt to remove the 
		    // child's children first. If they're are any children left
		    // the change the hs to be the hs of the first child
		    remove(child, hs);
		    if (child.isLeaf()) {
			// no more children remove the child as well
			debug("  tagging for removal: "+child);
			toRemove.addElement(child); // tag to be removed...
		    } else {
			// nuts! There are children from different hs
			// change the hs of the IndexItem to be the hs of the
			// first child
			DefaultMutableTreeNode childOne = 
			    (DefaultMutableTreeNode) child.getFirstChild();
			IndexItem itemOne =  (IndexItem) childOne.getUserObject();
			item.setHelpSet(itemOne.getHelpSet());
			debug("  orphaned children - changing hs: "+child);
		    }
		}
	    } else {
		// the child doesn't need to be removed but possibly it's
		// children will
		remove(child, hs);
	    }
	}
	    
	// Now remove them
	for (int i=0; i<toRemove.size(); i++) {
	    debug("  removing "+toRemove.elementAt(i));
	    node.remove((DefaultMutableTreeNode) toRemove.elementAt(i));
	}
    }

    // Make all nodes visible
    
    private void setVisibility (DefaultMutableTreeNode node) {
	IndexItem item = (IndexItem)node.getUserObject();
	if (node == topNode || 
	    (item != null && item.getExpansionType() != TreeItem.COLLAPSE)) {
	    tree.expandPath(new TreePath(node.getPath()));
	    if (! node.isLeaf()) {
		int max = node.getChildCount();
		for (int i=0; i<max; i++) {
		    setVisibility((DefaultMutableTreeNode)node.getChildAt(i));
		}
	    }
	}
    }

    // Process and idChanged event
    
    public void idChanged(HelpModelEvent e) {
        ID id = e.getID();
        HelpModel helpModel = glossary.getModel();
        debug("idChanged("+e+")");
        
        if (e.getSource() != helpModel) {
            System.err.println("Internal inconsistency!");
            System.err.println("  "+e.getSource()+" != "+helpModel);
            throw new Error("Internal error");
        }
        
        if (id == null) {
            //return;
        }
        TreePath s = tree.getSelectionPath();
        if (s != null) {
            Object o = s.getLastPathComponent();
            // should require only a TreeNode
            if (o instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode tn = (DefaultMutableTreeNode) o;
                IndexItem item = (IndexItem) tn.getUserObject();
                if (item != null) {
                    ID nId = item.getID();
                    if (nId != null && nId.equals(id)) {
                        return;
                    }
                }
            }
        }
        
        DefaultMutableTreeNode node = findID(topNode, id);
 	selectNode(node);
    }

    // Note - this recursive implementation may need tuning for very large Index - epll

    private DefaultMutableTreeNode findID(DefaultMutableTreeNode node, ID id) {
        debug("findID: ("+id+")");
        debug("  node: "+node);
        
        // check on the id
        if (id == null) {
            return null;
        }
        IndexItem item = (IndexItem) node.getUserObject();
        if (item != null) {
            ID testID = item.getID();
            debug("  testID: "+testID);
            if (testID != null && testID.equals(id)) {
                return node;
            }
        }
        int size = node.getChildCount();
        for (int i=0; i<size ; i++) {
            DefaultMutableTreeNode tmp =
            (DefaultMutableTreeNode) node.getChildAt(i);
            DefaultMutableTreeNode test = findID(tmp, id);
            if (test != null) {
                return test;
            }
        }
        return null;
    }

    protected JHelpContentViewer getContentViewer() {
        return viewer;
    }

    /**
     * Select a certian node
     */
    private void selectNode(DefaultMutableTreeNode node) {
	if (node == null) {
	    // node doesn't exist. Need to clear the selection.
	    tree.clearSelection();
	    return;
	}
	TreePath path = new TreePath(node.getPath());
	tree.expandPath(path);
	tree.setSelectionPath(path);
	tree.scrollPathToVisible(path);
    }

    protected JHelpNavigator getHelpNavigator() {
        return glossary;
    }

    public void valueChanged(TreeSelectionEvent e) {

        JHelpNavigator navigator = getHelpNavigator();
        HelpModel helpmodel = navigator.getModel();

        debug("ValueChanged: "+e);
	debug("  model: "+helpmodel);

        // send selected items into navigator
        TreeItem[] items = null;
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            items = new TreeItem[paths.length];
            for (int i = 0; i < paths.length; i++) {
                if (paths[i] != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
                    items[i] = (TreeItem) node.getUserObject();
                }
            }
        }
        navigator.setSelectedItems(items);

        // change current id only if one items is selected
        if (items != null && items.length == 1) {
            IndexItem item = (IndexItem) items[0];
            if (item != null && item.getID() != null) {
                try {
                    getContentViewer().getModel().setCurrentID(item.getID(), item.getName(), navigator);
                } catch (InvalidHelpSetContextException ex) {
                    System.err.println("BadID: "+item.getID());
                    return;
                }
            }
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        debug("propertyChange: " + event.getSource() + " "  +
        event.getPropertyName());
        
        if (event.getSource() == glossary) {
            String changeName = event.getPropertyName();
            if (changeName.equals("helpModel")) {
                debug("model changed");
                reloadData((HelpModel)event.getNewValue());
            } else  if (changeName.equals("font")) {
                debug("Font change");
                Font newFont = (Font)event.getNewValue();
                tree.setFont(newFont);
                RepaintManager.currentManager(tree).markCompletelyDirty(tree);
            }else if(changeName.equals("expand")){
                debug("Expand change");
                expand((String)event.getNewValue());
            } else if(changeName.equals("collapse")){
                debug("Collapse change");
                collapse((String)event.getNewValue());
            }
            // changes to UI property?
        }
    }
    
    /**
     *  Handles Action from the JTextField component for searching.
     */
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource()==searchField) {
            
            // get a Collator based on the component locale
            rbc = (RuleBasedCollator) Collator.getInstance(glossary.getLocale());
            
            String text = searchField.getText();
            if (text != null) {
                text = text.toLowerCase();
            }
            if (oldText != null && text.compareTo(oldText) != 0) {
                currentFindNode = null;
            }
            oldText = text;
            
            // find the node in the tree
            DefaultMutableTreeNode node = searchName(topNode, text);
            if (node == null) {
                currentFindNode = null;
                glossary.getToolkit().beep();
                return;
            }
            currentFindNode = node;
            
            //display it
            TreePath path = new TreePath(node.getPath());
            tree.scrollPathToVisible(path);
            tree.expandPath(path);
            tree.setSelectionPath(path);
            
        }
    }
    
    /**
     *  Searches in the tree for an index item with a name that starts with String name.
     *  Returns the node that contains the name,
     *  returns null if no node is found.
     */
    private DefaultMutableTreeNode searchName(DefaultMutableTreeNode node,
    String name) {
        if (currentFindNode == null) {
            IndexItem item = (IndexItem) node.getUserObject();
            if (item!=null) {
                String itemName = item.getName();
                if (itemName !=null) {
                    itemName = itemName.toLowerCase();
                    // compare the Node with the Name
                    //if (HelpUtilities.isStringInString(rbc, name, itemName)) {
                    if(itemName.startsWith(name)){
                        return node;
                    }
                }
            }
        } else {
            if (currentFindNode == node) {
                currentFindNode = null;
            }
        }
        
        // travel the the rest of the tree
        int size = node.getChildCount();
        for (int i=0; i<size ; i++) {
            DefaultMutableTreeNode tmp =
            (DefaultMutableTreeNode) node.getChildAt(i);
            DefaultMutableTreeNode test = searchName(tmp, name);
            if (test != null) {
                return test;
            }
        }
        return null;
    }
    
    /**
     * For printf debugging.
     */
    private static boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("BasicGlossaryNavigatorUI: " + str);
        }
    }
    
}
