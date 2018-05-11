/*
 * @(#)BasicTOCNavigatorUI.java	1.99 06/10/30
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
/*
 * @(#) BasicTOCNavigatorUI.java 1.99 - last change made 10/30/06
 */

package javax.help.plaf.basic;

import javax.help.*;
import javax.help.plaf.HelpNavigatorUI;
import javax.help.plaf.HelpUI;
import javax.help.event.HelpModelListener;
import javax.help.event.HelpModelEvent;
import com.sun.java.help.impl.SwingWorker;
import java.util.EventObject;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.RepaintManager;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.help.Map.ID;
import java.util.Locale;
import java.lang.reflect.Method;

/**
 * The default UI for JHelpNavigator of type TOC.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @author Richard Gregor
 * @version   1.99     10/30/06
 */
public class BasicTOCNavigatorUI extends HelpNavigatorUI
             implements HelpModelListener, TreeSelectionListener,
                        PropertyChangeListener, ComponentListener,
                        Serializable
{
    protected JHelpTOCNavigator toc;
    protected JScrollPane sp;
    protected DefaultMutableTreeNode topNode;
    protected JTree tree;
    private boolean inInstallUI = false;
    private SwingWorker worker = null;

    public static ComponentUI createUI(JComponent x) {
        return new BasicTOCNavigatorUI((JHelpTOCNavigator) x);
    }
    
    public BasicTOCNavigatorUI(JHelpTOCNavigator b) {
	debug (this + " " + "CreateUI - sort of");
        ImageIcon icon = getImageIcon(b.getNavigatorView());
        if (icon != null) {
            setIcon(icon);
        } else {
	    setIcon(UIManager.getIcon("TOCNav.icon"));
	}
    }

    public void installUI(JComponent c) {
	debug (this + " " + "installUI");

	inInstallUI = true;
	toc = (JHelpTOCNavigator)c;
	HelpModel model = toc.getModel();

	toc.setLayout(new BorderLayout());
	toc.addPropertyChangeListener(this);
        toc.addComponentListener(this);
	if (model != null) {
	    model.addHelpModelListener(this); // for our own changes            
	}

	topNode = new DefaultMutableTreeNode();

	tree = new JTree(topNode);	
	TreeSelectionModel tsm = tree.getSelectionModel();
        // tsm.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	tsm.addTreeSelectionListener(this);
        
	tree.setShowsRootHandles(false);
	tree.setRootVisible(false);

	setCellRenderer(toc.getNavigatorView(), tree);

	sp = new JScrollPane();
	sp.getViewport().add(tree);
        
	toc.add("Center", sp);
	reloadData();
	inInstallUI = false;
    }

    /**
     * Sets the desired cell renderer on this tree.  This is exposed for redefinition
     * by subclases.
     */
    protected void setCellRenderer(NavigatorView view, JTree tree) {
	// Use the combined map to drive the TOC tree actions
	Map map = view.getHelpSet().getCombinedMap();
	tree.setCellRenderer(new BasicTOCCellRenderer(map, (TOCView)view));
    }

    public void uninstallUI(JComponent c) {
	debug (this + " " + "unistallUI");
	HelpModel model = toc.getModel();

        toc.removeComponentListener(this);
	toc.removePropertyChangeListener(this);
	TreeSelectionModel tsm = tree.getSelectionModel();
	tsm.removeTreeSelectionListener(this);
	toc.setLayout(null);
	toc.removeAll();

	if (model != null) {
	    model.removeHelpModelListener(this);
	}

	toc = null;
    }

    public Dimension getPreferredSize(JComponent c) {
	/*
	if (sp != null) {
	    return ((ScrollPaneLayout)sp.getLayout()).preferredLayoutSize(sp);
	} else {
	    return new Dimension(200,100);
	}
	*/
	return new Dimension (200,100);
    }

    public Dimension getMinimumSize(JComponent c) {
	return new Dimension(100,100);
    }

    public Dimension getMaximumSize(JComponent c) {
	return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
    }

    /**
     * Reloads the presentation data.
     */
    private void reloadData() {
	debug("reloadData");

	// parse the TOC data into topNode
	TOCView view = (TOCView) toc.getNavigatorView();

	if (worker != null) {
	    // Something is still going on. Stop it and start over
	    worker.interrupt();
	}
	worker = new NavSwingWorker(view);
	worker.start(Thread.MIN_PRIORITY);
    }

    /**
     * load the data for the navigator. Will be run on a separate
     * thread.
     */
    private synchronized Object loadData(TOCView view) {
	if (view == null) {
	    return Boolean.FALSE;
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
	    
	// Add all of the subhelpset's data
	addSubHelpSets(view.getHelpSet());
	return Boolean.TRUE;
    }


    /*
     * Presents the data loaded in loadData. Will be run on the
     * Swing event thread.
     */
    private void presentData() {
	// reload the tree data
	((DefaultTreeModel)tree.getModel()).reload(); 
	    
	setVisibility(topNode);

	    
	if (toc.getModel() != null) {
	    ID id = toc.getModel().getCurrentID();
	    if (id != null) {
		DefaultMutableTreeNode selectedNode = findID(topNode, id);
		selectNode(selectedNode);
	    }
	}
    }

    private class NavSwingWorker extends SwingWorker {
	TOCView view;

	public NavSwingWorker (TOCView view) {
	    super();
	    this.view = view;
	}

	public Object construct() {
	    return loadData(view);
	}

	public void finished() {
	    if ((Boolean)get() == Boolean.TRUE) {
		presentData();
	    }
	}
    }


    /**
     * Reloads the presentation data using new help model. Changes the navigator if new model contains
     * view with the same name as former view
     **/
    private void reloadData(HelpModel model) {
	debug("reloadData using new model");
        
        TOCView view = null;          
        
        HelpSet newHelpSet = model.getHelpSet();
        TOCView oldView = (TOCView) toc.getNavigatorView();
        String oldName = oldView.getName();
        NavigatorView[] navViews = newHelpSet.getNavigatorViews();
        for(int i = 0 ; i < navViews.length; i++){
            if((navViews[i].getName()).equals(oldName)){
                NavigatorView tempView = navViews[i];
                if(tempView instanceof TOCView){
                    view = (TOCView) tempView;
                    break;
                }
            }
        }
        
	if (worker != null) {
	    // Something is still going on. Stop it and start over
	    worker.interrupt();
	}
	worker = new NavSwingWorker(view);
	worker.start(Thread.MIN_PRIORITY);
    }

    /** Adds subhelpsets
     *
     * @param hs The HelpSet which subhelpsets will be added
     */
    protected void addSubHelpSets(HelpSet hs){
	debug("addSubHelpSets");
        for( Enumeration e = hs.getHelpSets(); e.hasMoreElements(); ) {
	    HelpSet ehs = (HelpSet) e.nextElement();
            // merge views
            NavigatorView[] views = ehs.getNavigatorViews();
            for(int i = 0; i < views.length; i++){
                if(toc.canMerge(views[i]))
                    doMerge(views[i]);
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
                    TOCItem tocItem = (TOCItem)node.getUserObject();
                    if(tocItem == null)
                        debug("tocItem is null");
                    else{
                        Map.ID id = tocItem.getID();
                        if(id != null){
                            debug("id name :"+id.id);
                            debug("target :"+target);
                            Map.ID itemID = null;
                            try{
                                itemID = Map.ID.create(target,toc.getModel().getHelpSet());
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
     * Collapses entry specified by id. If entry is empty collapses its parent.
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
       
        Merge mergeObject = Merge.DefaultMergeFactory.getMerge(toc.getNavigatorView(),view);
        if(mergeObject != null) {
            mergeObject.processMerge(topNode);
	}
        
    }

    /**
     * Merges in the navigational data from another TOCView.
     *
     * @param view A TOCView.  Note the actual argument is of a NavigatorView type
     * so it replaces the correct NavigatorUI method.
     */

    public void merge(NavigatorView view) {
	debug("merging "+view);
        doMerge(view);

        //reload the tree data
	((DefaultTreeModel)tree.getModel()).reload(); 
	setVisibility(topNode);
    }

    /**
     * Removes the navigational data.
     *
     * @param view A TOCView.  Note the actual argument is of a NavigatorView type
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
     * @param node The node from where to remove its children
     * @param hs The non-null helpset to use
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
	    TOCItem item = (TOCItem) child.getUserObject();
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
			// change the hs of the TOCItem to be the hs of the
			// first child
			DefaultMutableTreeNode childOne = 
			    (DefaultMutableTreeNode) child.getFirstChild();
			TOCItem itemOne =  (TOCItem) childOne.getUserObject();
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


    // In the TOC only set the visibility at the next to the top node
    // unless the node is set for expansion. Always expand the first row
    private void setVisibility (DefaultMutableTreeNode node) {
	TOCItem item = (TOCItem)node.getUserObject();
	int expandType = TreeItem.DEFAULT_EXPANSION;
	if (item != null) {
	    expandType = item.getExpansionType();
	}
	TreePath path = new TreePath(node.getPath());
	int pathcount = path.getPathCount();
	if (node == topNode || 
	    (pathcount <= 2 && expandType == TreeItem.DEFAULT_EXPANSION) ||
	    (expandType == TreeItem.EXPAND)) {
	    tree.expandPath(new TreePath(node.getPath()));
	    if (! node.isLeaf()) {
		int max = node.getChildCount();
		for (int i=0; i<max; i++) {
		    setVisibility((DefaultMutableTreeNode)node.getChildAt(i));
		}
	    }
	}
    }

    /**
     * Processes an idChanged event.
     */

    public synchronized void idChanged(HelpModelEvent e) {
 	ID id = e.getID();
	HelpModel helpModel = toc.getModel();
	debug("idChanged("+e+")");

	if (e.getSource() != helpModel) {
	    debug("Internal inconsistency!");
	    debug("  "+e.getSource()+" != "+helpModel);
	    throw new Error("Internal error");
	}

	if (id == null) {
	    id = helpModel.getHelpSet().getCombinedMap().getClosestID(e.getURL());
	}
	TreePath s = tree.getSelectionPath();
	if (s != null) {
	    Object o = s.getLastPathComponent();
	    // should require only a TreeNode
	    if (o instanceof DefaultMutableTreeNode) {
		DefaultMutableTreeNode tn = (DefaultMutableTreeNode) o;
		TOCItem item = (TOCItem) tn.getUserObject();
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

    // Note - this recursive implementation may need tuning for very large TOC - epll

    private DefaultMutableTreeNode findID(DefaultMutableTreeNode node, ID id) {
	debug("findID: ("+id+")");
	debug("  node: "+node);

	// check on the ID
	if (id == null) {
	    return null;
	}
	TOCItem item = (TOCItem) node.getUserObject();
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
        return toc;
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
            TOCItem item = (TOCItem) items[0];
            if (item != null && item.getID() != null) {
		String presentation = item.getPresentation();
		if (presentation == null) {
		    // This is the old way of doing things 
		    // set the currentID in the helpModel
		    try {
			helpmodel.setCurrentID(item.getID(), item.getName(), navigator);
		    } catch (InvalidHelpSetContextException ex) {
			System.err.println("BadID: "+item.getID());
			return;
		    }
		} else {
		    // Tricky situation here
		    // Don't select a Tree item if we're installing
		    // and that TreeItem displays in different presentation
		    // This can lead to infinate loops
		    if (inInstallUI) {
			tree.clearSelection();
			return;
		    }

		    // using a Presentation
		    // Get a Presentation
		    Presentation pres;
		    HelpSet hs = helpmodel.getHelpSet();
		    ClassLoader loader;
		    Class klass;
		    Class types[] = { HelpSet.class,
				      String.class};
		    Object args[] = { hs,
				      item.getPresentationName()};		
		    try {
			loader = hs.getLoader();
			if (loader == null) {
			    klass = Class.forName(presentation);
			} else {
			    klass = loader.loadClass(presentation);
			}
			Method m = klass.getMethod("getPresentation", types);
			pres = (Presentation)m.invoke(null, args);
		    } catch (Exception ex) {
			throw new RuntimeException("error invoking presentation" );
		    }

		    if (pres == null) {
			return;
		    }

		    // Minor adjustments for Windows and Popups
		    if (pres instanceof WindowPresentation) {
			((WindowPresentation)pres).setActivationObject(tree);
		    }
		    if (pres instanceof Popup) {
			((Popup)pres).setInvokerInternalBounds(tree.getPathBounds(paths[0]));
			((Popup)pres).setInvoker((Component)tree);
		    }

		    // set the id to be displayed
		    try {
			pres.setCurrentID(item.getID());
		    } catch (InvalidHelpSetContextException ex) {
			System.err.println("BadID: "+item.getID());
			return;
		    }

		    // diplay the presentation
		    pres.setDisplayed(true);
		}
            }
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
	debug(this + " " + "propertyChange: " + event.getSource() + " "  +
	      event.getPropertyName());

	if (event.getSource() == toc) {
	    String changeName = event.getPropertyName();
	    if (changeName.equals("helpModel")) {
                reloadData((HelpModel)event.getNewValue());             
      	    } else  if (changeName.equals("font")) {
		debug ("Font change");
		Font newFont = (Font)event.getNewValue();
		tree.setFont(newFont);
		RepaintManager.currentManager(tree).markCompletelyDirty(tree);
            } else if(changeName.equals("expand")){
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
     * Invoked when the component's size changes.
     */
    public void componentResized(ComponentEvent e) {
    }
    
    /**
     * Invoked when the component's position changes.
     */
    public void componentMoved(ComponentEvent e) {
    }
    
    /**
     * Invoked when the component has been made visible.
     */
    public void componentShown(ComponentEvent e) {
        tree.requestFocus();
    }
    
    /**
     * Invoked when the component has been made invisible.
     */
    public void componentHidden(ComponentEvent e) {
    }
    
    /**
     * For printf debugging.
     */
    protected static final boolean debug = false;
    protected static void debug(String str) {
        if (debug) {
            System.out.println("BasicTOCNavigatorUI: " + str);
        }
    }
}
