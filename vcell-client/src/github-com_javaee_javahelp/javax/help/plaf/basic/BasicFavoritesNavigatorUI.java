/*
 * @(#)BasicFavoritesNavigatorUI.java	1.17 06/10/30
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
import javax.help.event.HelpModelListener;
import javax.help.event.HelpModelEvent;
import java.util.EventObject;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Locale;
import java.util.HashMap;
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
import java.awt.event.ComponentEvent;
import javax.help.Map.ID;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.net.MalformedURLException;
import java.util.HashSet;
import javax.help.event.HelpSetListener;
import javax.help.event.HelpSetEvent;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.dnd.peer.*;
import java.io.IOException;

/**
 * The default UI for JHelpNavigator of type Favorites
 *
 * @author Richard Gregor
 * @version	1.17	10/30/06
 */

public class BasicFavoritesNavigatorUI extends HelpNavigatorUI
implements HelpModelListener, TreeSelectionListener, HelpSetListener,
PropertyChangeListener, TreeModelListener, Serializable {
    
    protected JHelpFavoritesNavigator favorites;
    protected JScrollPane sp;
    protected FavoritesNode topNode;
    protected JTree tree;
    private   String contentTitle;
    protected RuleBasedCollator rbc;
    protected String oldText;
    protected DefaultMutableTreeNode currentFindNode;
    protected Action addAction;
    protected Action removeAction;
    protected Action cutAction;
    protected Action pasteAction;
    protected Action copyAction;
    protected Action folderAction;
    protected JPopupMenu popup;
    
    private   HashMap dataMap = new HashMap();
    // node wich will contain parsed favorites data
    private   FavoritesNode favNode = null;
    // node which contents will be saved
    private   FavoritesNode rootNode = null;
    private   Locale locale = null;
    private   FavoritesNode selectedNode = null;
    private   FavoritesItem selectedItem = null;
    private   TreePath selectedTreePath = null;
    private   Map.ID selectedID = null;
    private   JMenuItem newFolderMI = null;
    private   JMenuItem addMI = null;
    private   JSeparator separatorMI = null;
    private   JMenuItem cutMI = null;
    private   JMenuItem copyMI = null;
    private   JMenuItem pasteMI = null;
    private   JMenuItem removeMI = null;
    private   HashMap hsMap = null;
    //HasMap with visible node from favNode as key and its related node from rootNode as a value
    private   HashMap connections = new HashMap();
    /**
     * Vector of nodes used in CutAction
     */
    private   Vector nodeClipboard = new Vector();

    static private boolean on1dot3 = false;

    static {
        try {
            // Test if method introduced in 1.3 or greater is available.
            Class klass = Class.forName("javax.swing.InputVerifier");
            on1dot3 = (klass != null);
        } catch (ClassNotFoundException e) {
            on1dot3 = false;
        }
    }

    
    public static ComponentUI createUI(JComponent x) {
        return new BasicFavoritesNavigatorUI((JHelpFavoritesNavigator) x);
    }
    /**
     * Creates BasicFavoritesUI for JHelpFavoritesNavigator
     */
    public BasicFavoritesNavigatorUI(JHelpFavoritesNavigator b) {
        ImageIcon icon = getImageIcon(b.getNavigatorView());
        if (icon != null) {
            setIcon(icon);
	} else {
            setIcon(UIManager.getIcon("FavoritesNav.icon")); 
	}
    }
    
    public void installUI(JComponent c) {
        debug("installUI");
        
        locale = HelpUtilities.getLocale(c);
        addAction = new AddAction();
        removeAction = new RemoveAction();
        folderAction = new FolderAction();
        favorites = (JHelpFavoritesNavigator)c;
        HelpModel model = favorites.getModel();
        
        favorites.setLayout(new BorderLayout());
        favorites.addPropertyChangeListener(this);
        if (model != null) {
            model.addHelpModelListener(this); // for our own changes
            model.addPropertyChangeListener(this); // for HelpSet change
            HelpSet helpSet = model.getHelpSet();
            if(helpSet != null)
                helpSet.addHelpSetListener(this);
        }
        
	topNode = new FavoritesNode(new FavoritesItem("Favorites"));
	if (on1dot3) {
	    // Use drag and drop if available
	    tree = new FavoritesTree(topNode);
	} else {
	    tree = new JTree(topNode);
	}
	tree.setEditable(true);
	tree.addMouseListener(new PopupListener());
	
	cutAction = new CutAction();
	copyAction = new CopyAction();
	pasteAction = new PasteAction();
	
	popup = new JPopupMenu();
	
	newFolderMI = new JMenuItem((String)folderAction.getValue(folderAction.NAME));
	newFolderMI.addActionListener(folderAction);
	popup.add(newFolderMI);
	
	addMI = new JMenuItem((String)addAction.getValue(addAction.NAME));
	addMI.addActionListener(addAction);
	popup.add(addMI);

	separatorMI = new JSeparator();
	popup.add(separatorMI);
	
	cutMI = new JMenuItem((String)cutAction.getValue(cutAction.NAME));
	cutMI.addActionListener(cutAction);
	cutMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,java.awt.Event.CTRL_MASK));
	popup.add(cutMI);
	
	copyMI = new JMenuItem((String)copyAction.getValue(copyAction.NAME));
	copyMI.addActionListener(copyAction);
	copyMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,java.awt.Event.CTRL_MASK));
	popup.add(copyMI);
	
	pasteMI = new JMenuItem((String)pasteAction.getValue(pasteAction.NAME));
	pasteMI.addActionListener(pasteAction);
	pasteMI.setEnabled(false);
	pasteMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,java.awt.Event.CTRL_MASK));
	popup.add(pasteMI);
	
	removeMI = new JMenuItem((String) removeAction.getValue(removeAction.NAME));
	removeMI.addActionListener(removeAction);
	
	popup.add(removeMI);
        
        tree.getModel().addTreeModelListener(this);
        tree.addTreeSelectionListener(this);
        
        TreeSelectionModel tsm = tree.getSelectionModel();
        tsm.addTreeSelectionListener(this);
        
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        
        setCellRenderer(favorites.getNavigatorView(), tree);
 
        sp = new JScrollPane();
        sp.getViewport().add(tree);
        
        favorites.add("Center", sp);
        
        reloadData();
    }
    
    /**
     * Sets the desired cell renderer on this tree.  This is exposed for redefinition
     * by subclases.
     */
    protected void setCellRenderer(NavigatorView view, JTree tree) {
        tree.setCellRenderer(new BasicFavoritesCellRenderer());
    }
    
    public void uninstallUI(JComponent c) {
        debug("uninstallUI");
        HelpModel model = favorites.getModel();
        
        favorites.removePropertyChangeListener(this);
        TreeSelectionModel tsm = tree.getSelectionModel();
        tsm.removeTreeSelectionListener(this);
        favorites.setLayout(null);
        favorites.removeAll();
        
        if (model != null) {
            model.removeHelpModelListener(this);
        }
        
        favorites = null;
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
        
        //needs refactoring - sholudn't parse each time!!! - only once
        
        if (favorites.getModel() == null) {
            return;
        }
        
        // remove all children
        topNode.removeAllChildren();
        
        FavoritesView view = (FavoritesView) favorites.getNavigatorView();
        
        if (view == null) {
            return;
        }

        favNode = view.getDataAsTree();
        rootNode = favNode.getDeepCopy();

        classifyNode(favNode);
        while (favNode.getChildCount() > 0) {
	    // this looks strange but as you add a child to the top node
	    // it removes the child from the favNode so the getFirstChild
	    // will always get the first child
            topNode.add((DefaultMutableTreeNode)favNode.getFirstChild());
        }
        
        // reload the tree data
        ((DefaultTreeModel)tree.getModel()).reload();
        
        setVisibility(topNode);        
        
    }
    
    /**
     * Classifies the node. Determines wheter child will be visible or not according to node's HelpSet.
     * Hides node without children when its HelpSet is not loaded
     *
     * @param node The FavoritesNode
     */
    private void classifyNode(FavoritesNode node){
        debug("classifyNode");
        
        if(node == null)
            return;
        
        HelpModel model = favorites.getModel();
        if(model == null){
            node.removeAllChildren();
            return;
        }
        HelpSet masterHelpSet = model.getHelpSet();
        if(masterHelpSet == null){
            node.removeAllChildren();
            return;
        }
        hsMap = new HashMap();
        hsMap.put(masterHelpSet.getTitle(), masterHelpSet);
        
        fillHelpSetTitles(masterHelpSet);
        
        // tags node's children wheter they are visible or not
        classifyChildren(node);
        
    }
    
    /**
     * Fills HashMap with actual HelpSets and their titles
     *
     * @param masterHelpSet The master HelpSet
     */
    private void fillHelpSetTitles(HelpSet masterHelpSet){
        for(Enumeration helpSets = masterHelpSet.getHelpSets();helpSets.hasMoreElements();){
            HelpSet hs = (HelpSet) helpSets.nextElement();
            if(hs != null){
                hsMap.put(hs.getTitle(), hs);
                debug(" fill title: "+hs.getTitle());
                fillHelpSetTitles(hs);
            }
        }
    }
    
    /**
     * Classifies children of node
     *
     * @param node The FavoritesNode
     */
    private void classifyChildren(FavoritesNode node){
        debug("classifyChildren: "+node);
        
        if(node == null)
            return;
        
	boolean skipChild = true;

        for(Enumeration children = node.preorderEnumeration();children.hasMoreElements();){
            FavoritesNode chnode = (FavoritesNode)children.nextElement();

	    // Skip the first entry because it is node and we don't need
	    // to classify the node only it's children
	    if (skipChild) {
		skipChild = false;
		continue;
	    }

	    // Add this child node to the root node. 

	    // Make a copy of the child node for the rootNode 
            FavoritesNode copy = chnode.getDeepCopy();

	    // establish the connection between the chnode and the copy
            connections.put(chnode, copy);
	    // properly connect it to the childs corresponding parent in the 
	    // rootnode. 
	    FavoritesNode rootParent = (FavoritesNode)connections.get(chnode.getParent());
	    if (rootParent == null) {
		rootParent = rootNode;
	    }
	    rootParent.add(copy);

	    // Now on to seeing if the item
            FavoritesItem item = (FavoritesItem)chnode.getUserObject();
            debug("classify item: "+ item);
            //shouldn't happen
            if(item == null){
                debug("item is null : fillDataMap");
                continue;
            }
            String target = item.getTarget();
            String hsTitle = item.getHelpSetTitle();
            if(!hsMap.containsKey(hsTitle)){
                if(chnode.getVisibleChildCount() == 0){
                    if(item.emptyInitState() && item.isFolder()){
                        debug("empty init state");
                        continue;
                    }
                    //chnode.removeFromParent();
                    item.setVisible(false);
                    continue;
                }
            }
            
            if(target == null){
                debug("target is null:fillDataMap");
                continue;
            }
            
            Map.ID id = null;
            try{
                id = Map.ID.create(target, (HelpSet)hsMap.get(hsTitle));
            }catch(BadIDException ep){
                debug(ep.getMessage());
                continue;
            }
            
            debug("put to the dataMap: "+item);
            dataMap.put(item,id);
        }
        
        //clear node
	Vector toRemove = new Vector();
	Enumeration nodesToRem = node.breadthFirstEnumeration();
	while(nodesToRem.hasMoreElements()) {
	    FavoritesNode fn = (FavoritesNode)nodesToRem.nextElement();
            if(!fn.isVisible()){
		debug ("remove node:" +(FavoritesItem)fn.getUserObject());
		toRemove.addElement(fn);
            }
	}
	for (int i=0; i < toRemove.size(); i++) {
	    debug("removing " +  toRemove.elementAt(i));
	    try {
		node.remove((DefaultMutableTreeNode) toRemove.elementAt(i));
	    } catch (IllegalArgumentException iae) {
		// ignore - supernode is already removed
	    }
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
                FavoritesItem favoritesItem = (FavoritesItem)node.getUserObject();
                if(favoritesItem == null)
                    debug("favoritesItem is null");
                else{
                    //Map.ID id = favoritesItem.getID();
                    Map.ID id = (Map.ID)dataMap.get(favoritesItem);
                    if(id != null){
                        debug("id name :"+id.id);
                        debug("target :"+target);
                        Map.ID itemID = null;
                        try{
                            itemID = Map.ID.create(target,favorites.getModel().getHelpSet());
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
    
    // Make all nodes visible
    
    private void setVisibility(DefaultMutableTreeNode node) {
        tree.expandPath(new TreePath(node.getPath()));
        if (! node.isLeaf()) {
            int max = node.getChildCount();
            for (int i=0; i<max; i++) {
                setVisibility((DefaultMutableTreeNode)node.getChildAt(i));
            }
        }
    }
    
    /**
     * Processes and idChanged event. In this navigator works with url too.
     *
     * @param e The HelpModelEvent
     */
    
    public void idChanged(HelpModelEvent e) {
        debug("idChanged("+e+")");
        
        //next values
        ID id = e.getID();
        contentTitle = e.getHistoryName();
        URL nurl = e.getURL();
        String nURL = null;
        if (nurl != null)
            nURL = nurl.toExternalForm();
        
        //current values
        ID currentID = null;
        String currentURL = null;
        String currentName = null;
	FavoritesItem item = null;
        
        HelpModel helpModel = favorites.getModel();
        
        
        if (e.getSource() != helpModel) {
            debug("Internal inconsistency!");
            debug("  "+e.getSource()+" != "+helpModel);
            throw new Error("Internal error");
        }
        
        TreePath s = tree.getSelectionPath();
        if (s != null) {
            Object o = s.getLastPathComponent();
            // should require only a TreeNode
            if (o instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode tn = (DefaultMutableTreeNode) o;
                item = (FavoritesItem) tn.getUserObject();
                if (item != null){
                    currentID = (Map.ID) dataMap.get(item);
                    currentURL = item.getURLSpec();
                    currentName = item.getName();
                }
                if((currentName != null) && (currentName.equals(contentTitle))){
                    if(currentID != null)
                        if(currentID.equals(id))
                            return;
                    if(currentURL != null)
                        if(currentURL.equals(nURL))
                            return;
                }
            }
        }
        
        DefaultMutableTreeNode node = null;        

        node = findID(topNode, id);
        if(node == null) {
            node = findURL(topNode, nURL);
	}

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
    
    private DefaultMutableTreeNode findID(DefaultMutableTreeNode node, ID id) {
        debug("findID: ("+id+")");
        debug("  node: "+node);
        
        // check on the id
        if (id == null) {
            return null;
        }
        FavoritesItem item = (FavoritesItem) node.getUserObject();
        Map.ID  itemID = (Map.ID) dataMap.get(item);
        if (itemID != null) {
            ID testID = itemID;
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
     * Finds the subnode with certain url and name
     */
    private DefaultMutableTreeNode findURL(DefaultMutableTreeNode node, String urlSpec){
        debug(" findURL: "+ urlSpec);
        
        if(urlSpec == null)
            return null;
        
        for(Enumeration children = node.children(); children.hasMoreElements();){
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)children.nextElement();
            FavoritesItem childItem = (FavoritesItem) child.getUserObject();
            String childName = childItem.getName();
            String childURL = childItem.getURLSpec();
            if(urlSpec.equals(childURL))
                return child;
            else
                findURL(child,urlSpec);
        }
        return null;
    }
    
    /**
     * Finds the subnode with certain id and name
     */
    
    public void valueChanged(TreeSelectionEvent e) {
        
        selectedTreePath = e.getNewLeadSelectionPath();
        if (selectedTreePath == null) {
            selectedNode = null;
            return;
        }
        selectedNode =
        (FavoritesNode)selectedTreePath.getLastPathComponent();
        if (selectedNode != null){
            selectedItem = (FavoritesItem)selectedNode.getUserObject();
            selectedID = (Map.ID)dataMap.get(selectedItem);
        }
        
        HelpModel helpModel = favorites.getModel();
	HelpSet hs = helpModel.getHelpSet();
        
        debug("ValueChanged: "+e);
        debug("  model: "+helpModel);
        if(helpModel == null)
            return;
        // send selected items into navigator
        TreeItem[] items = null;
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            removeAction.setEnabled(true);
            items = new TreeItem[paths.length];
            for (int i = 0; i < paths.length; i++) {
                if (paths[i] != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
		    FavoritesItem favItem = (FavoritesItem) node.getUserObject();
		    // need to normalize this item so it looks like a real TreeItem
		    try {
			String target = favItem.getTarget();
			if (target != null) {
			    ID id = Map.ID.create(favItem.getTarget(), hs);
			    favItem.setID(id);
			}
		    } catch (BadIDException ee) {
		    }
		    items[i] = (TreeItem) favItem;
                }
            }
        }else{
            removeAction.setEnabled(false);
            pasteAction.setEnabled(false);
            pasteMI.setEnabled(false);
            return;
        }
        favorites.setSelectedItems(items);
        
        // change current id only if one item is selected
        if (items != null && items.length == 1) {
            FavoritesItem item = (FavoritesItem) items[0];
            if (item != null) {
                Map.ID itemID = (Map.ID) dataMap.get(item);
                if(itemID != null){
                    debug("itemID: "+itemID);
                    try{
                        // helpmodel.setCurrentID(item.getID());
                        helpModel.setCurrentID(itemID, item.getName(), (JHelpNavigator)favorites);
                    } catch (InvalidHelpSetContextException ex) {
                        System.err.println("BadID: "+item.getID());
                        return;
                    }
                }else{
                    if(item.getURLSpec() != null){
                        try{
                            URL url = new URL(item.getURLSpec());
                            helpModel.setCurrentURL(url,item.getName(),(JHelpNavigator)favorites);
                        }catch(MalformedURLException ep){
                            System.err.println(ep);
                        }
                    }
                }
            }
        }
    }
    
    public void propertyChange(PropertyChangeEvent event) {
        debug("propertyChange: " + event.getSource() + " "  +
        event.getPropertyName());
        
        if (event.getSource() == favorites) {
            String changeName = event.getPropertyName();
            if (changeName.equals("helpModel")) {
                debug("model changed");
                reloadData();
                
            } else  if (changeName.equals("font")) {
                debug("Font change");
                Font newFont = (Font)event.getNewValue();
                tree.setFont(newFont);
                RepaintManager.currentManager(tree).markCompletelyDirty(tree);
            } else if(changeName.equals("expand")){
                debug("Expand change");
                expand((String)event.getNewValue());
            } else if(changeName.equals("collapse")){
                debug("Collapse change");
                collapse((String)event.getNewValue());
            } else if(changeName.equals("navigatorChange")){
                debug("Navigator change");
                tree.clearSelection();
            }
            
            // changes to UI property?
            
        }else if (favorites != null){
            if (event.getSource() == favorites.getModel()) {
                String changeName = event.getPropertyName();
                if( changeName.equals("helpSet")) {
                    reloadData();
                }
            }
        }
    }
    
    public void helpSetAdded(HelpSetEvent ev){
        debug("HelpSet added");
        reloadData();
    }
    
    public void helpSetRemoved(HelpSetEvent ev){
        debug("HelpSet removed");
        reloadData();
    }
    
    /**
     * Saves favorites to the file
     */
    public void saveFavorites(){
        FavoritesView view = (FavoritesView)favorites.getNavigatorView();
        view.saveFavorites(rootNode);
    }
    
    public void treeStructureChanged(javax.swing.event.TreeModelEvent treeModelEvent) {
        debug("tree structure changed");
    }
    
    public void treeNodesInserted(javax.swing.event.TreeModelEvent treeModelEvent) {
        debug("node inserted");
        int place = -1;
        FavoritesNode parent = (FavoritesNode) treeModelEvent.getTreePath().getLastPathComponent();
        Object nodes[] = treeModelEvent.getChildren();
        int[] indices = treeModelEvent.getChildIndices();
        int firstIndex = indices[0];
        debug("index first "+firstIndex);
        int lastIndex = (indices.length + firstIndex) - 1;
        FavoritesNode rootParent = (FavoritesNode)connections.get(parent);
        if(rootParent == null)
            rootParent = rootNode;
        
        debug("root parent "+rootParent);
        //nodes were inserted into first place in parent
        if(firstIndex == 0){
            if(rootParent.getChildCount() == 0)
                place = 0;
            else{
                for(Enumeration en = rootParent.children(); en.hasMoreElements();){
                    FavoritesNode node = (FavoritesNode)en.nextElement();
                    if(node.isVisible()){
                        debug("is visible : "+node);
                        place = rootParent.getIndex(node);
                        break;
                    }
                }
            }
            //insert nodes in node place
            if(place >= 0){
                for(int j = nodes.length - 1; j >= 0; j--){
                    FavoritesNode copy = ((FavoritesNode) nodes[j]).getDeepCopy();
                    rootParent.insert((DefaultMutableTreeNode)copy,place);
                    connections.put((FavoritesNode)nodes[j],copy);
                }
            }
            
        }else if (firstIndex > 0){
            //find what is before
            FavoritesNode nodeBefore = (FavoritesNode)parent.getChildAt(firstIndex - 1);
            FavoritesNode rootNode = (FavoritesNode)connections.get(nodeBefore);
            place = rootParent.getIndex(rootNode)+1;
            for(int k = nodes.length - 1; k >= 0; k--){
                FavoritesNode copyNode = ((FavoritesNode)nodes[k]).getDeepCopy();
                rootParent.insert((DefaultMutableTreeNode)copyNode,place);
                connections.put((FavoritesNode)nodes[k],copyNode);
            }
        }
    }
    
    public void treeNodesRemoved(javax.swing.event.TreeModelEvent treeModelEvent) {
        debug("nodes removed");
        Object nodes[] = treeModelEvent.getChildren();
        for(int i = 0; i < nodes.length; i++){
            FavoritesNode originalNode = (FavoritesNode)nodes[i];
            FavoritesNode remNode = (FavoritesNode)connections.get(originalNode);
            if(remNode != null)
                remNode.removeFromParent();
        }
    }
    
    public void treeNodesChanged(javax.swing.event.TreeModelEvent treeModelEvent) {
        debug("node changed");
        TreeCellEditor editor = tree.getCellEditor();
        Object newName = editor.getCellEditorValue();
        
        if((newName instanceof String) && selectedItem != null){
            debug("new name");
            //data of old item
            Map.ID itemID = (Map.ID)dataMap.get(selectedItem);
            //remove old data from dataMap
            dataMap.remove(selectedItem);
            FavoritesNode fromRootNode = (FavoritesNode)connections.get(getSelectedNode());
            //change name of old Item
            selectedItem.setName((String)newName);
            selectedNode.setUserObject(selectedItem);       
            if(fromRootNode != null){
                FavoritesItem fromRootItem = (FavoritesItem) fromRootNode.getUserObject();
                fromRootItem.setName((String)newName);
            }
            //put data to the dataMap
            dataMap.put(selectedItem,itemID);
            saveFavorites();
        }
    }
    
    /**
     * Returns the selected node
     *
     */
    public FavoritesNode getSelectedNode() {
        return selectedNode;
    }
    
    /**
     * AddAction class.
     *
     */
    public class AddAction extends AbstractAction{
        
        public AddAction(){
            super(HelpUtilities.getString(locale, "favorites.add"), SwingHelpUtilities.getImageIcon(BasicFavoritesNavigatorUI.class,"images/addToFav.gif"));
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("add");

            String target = null;
            String hstitle = null;
            FavoritesItem favorite = null;
	    HelpModel helpModel = favorites.getModel();
	    HelpSet hs = helpModel.getHelpSet();

            Map.ID ID = helpModel.getCurrentID();
            URL url = helpModel.getCurrentURL();
            if(ID != null){
                target = ID.id;
                hstitle = ID.hs.getTitle();
            }
            if(hstitle == null){
		// need to determine the helpset title by looking at the
		// starting URL of the 
		hstitle = getHelpSetTitle(hs, url);
		if (hstitle == null) {
		    hstitle=  hs.getTitle();
		}
            }
            String urlSpec = null;
            if(target == null) {
                urlSpec = url.toExternalForm();
	    }
            favorite = new FavoritesItem(contentTitle, target, urlSpec,
					 hstitle, Locale.getDefault());
            dataMap.put(favorite,ID);
 
            FavoritesNode node = new FavoritesNode(favorite);
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            model.insertNodeInto(node, topNode, topNode.getChildCount());
            
            TreePath path = new TreePath(node.getPath());
            tree.expandPath(path);
            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);

            saveFavorites();
        }

	/**
	 * retuns a helpset title for a given url
	 */
	private String getHelpSetTitle(HelpSet hs, URL url) {
	    URL baseURL = hs.getHelpSetURL();
	    String urlExternal = url.toExternalForm();
	    String baseURLExternal = baseURL.toExternalForm();
	    if (urlExternal.startsWith(baseURLExternal)) {
		return hs.getTitle();
	    }
	    Enumeration helpsets = hs.getHelpSets();
	    String title = null;
	    while (helpsets.hasMoreElements()) {
		HelpSet testHS = (HelpSet) helpsets.nextElement();
		title = getHelpSetTitle(testHS, url);
		if (title != null) {
		    break;
		}
	    }
	    return title;
	}

    }
    
    /**
     * Returns the Add action
     *
     */
    public Action getAddAction(){
        return addAction;
    }
    
    /**
     * RemoveAction class.
     *
     */
    public class RemoveAction extends AbstractAction{
        
        public RemoveAction(){
            super(HelpUtilities.getString(locale, "favorites.remove"), SwingHelpUtilities.getImageIcon(BasicFavoritesNavigatorUI.class,"images/remove.gif"));
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("remove");
            DefaultMutableTreeNode node = null;
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            TreePath[] paths = tree.getSelectionPaths();
            for(int i = 0 ; i < paths.length; i++){
                if(paths[i] != null){
                    node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
                    model.removeNodeFromParent(node);
                    if(node != null){
                        FavoritesItem item = (FavoritesItem)node.getUserObject();
                        dataMap.remove(item);
                    }
                }
                saveFavorites();
            }
        }
    }
    
    /**
     * Returns the RemoveAction object
     */
    public Action getRemoveAction(){
        return removeAction;
    }
    
    /**
     * FolderAction class.
     *
     */
    public class FolderAction extends AbstractAction{
        
        public FolderAction(){
            super(HelpUtilities.getString(locale, "favorites.folder"), SwingHelpUtilities.getImageIcon(BasicFavoritesNavigatorUI.class,"images/folder.gif"));
        }
        
        public void actionPerformed(ActionEvent ev){
            FavoritesItem favoriteFolder = new FavoritesItem(HelpUtilities.getString(locale, "favorites.folder"));
            favoriteFolder.setAsFolder();
            FavoritesNode node = new FavoritesNode(favoriteFolder);
            TreePath nodePath = tree.getSelectionPath();
            TreeNode parent = null;
            if(nodePath == null)
                parent = topNode;
            else{
                FavoritesNode selNode = (FavoritesNode)nodePath.getLastPathComponent();
                parent = selNode.getParent();
            }
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            model.insertNodeInto(node,(DefaultMutableTreeNode)parent,parent.getChildCount());
            TreePath path = new TreePath(node.getPath());
            tree.expandPath(path);
            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);
            saveFavorites();
        }
    }
    
    /**
     * Returns the FolderAction object
     */
    public Action getFolderAction(){
        return folderAction;
    }
    
    /**
     * CutAction class.
     */
    public class CutAction extends AbstractAction{
        
        public CutAction(){
            super(HelpUtilities.getString(locale,"favorites.cut"));
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("cut");
            DefaultMutableTreeNode node = null;
            nodeClipboard.removeAllElements();
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            TreePath[] paths = tree.getSelectionPaths();
            for(int i = 0 ; i < paths.length; i++){
                if(paths[i] != null){
                    node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
                    if(node != null){
                        FavoritesItem item = (FavoritesItem)node.getUserObject();
                        nodeClipboard.add(node);
                    }
                    model.removeNodeFromParent(node);
                }
            }
            saveFavorites();
            pasteMI.setEnabled(true);
        }
    }
    
    /**
     * Returns the CutAction object
     */
    public Action getCutAction(){
        return cutAction;
    }
    
    /**
     * PasteAction class.
     */
    public class PasteAction extends AbstractAction{
        
        public PasteAction(){
            super(HelpUtilities.getString(locale,"favorites.paste"));
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("paste");
            
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            TreePath path = tree.getSelectionPath();
            FavoritesNode node = (FavoritesNode)path.getLastPathComponent();
            if(node != null){
                if(node.getAllowsChildren()){
                    for(Enumeration nodes = nodeClipboard.elements(); nodes.hasMoreElements();){
                        model.insertNodeInto((DefaultMutableTreeNode)nodes.nextElement(),(DefaultMutableTreeNode)node,node.getChildCount());
                    }
                }
                else{
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
                    if(parent == null)
                        return;
                    int index = parent.getIndex(node);
                    for(Enumeration en = nodeClipboard.elements(); en.hasMoreElements();index++){
                        model.insertNodeInto((DefaultMutableTreeNode)en.nextElement(),parent,index);
                    }
                }
                saveFavorites();
            }
            
        }
    }
    
    /**
     * Returns the PasteAction object
     */
    public Action getPasteAction(){
        return pasteAction;
    }
    
    /**
     * CopyAction class.
     */
    public class CopyAction extends AbstractAction{
        
        public CopyAction(){
            super(HelpUtilities.getString(locale,"favorites.copy"));
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("paste");
            
            DefaultMutableTreeNode node = null;
            nodeClipboard.removeAllElements();
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            TreePath[] paths = tree.getSelectionPaths();
            for(int i = 0 ; i < paths.length; i++){
                if(paths[i] != null){
                    node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
                    if(node != null){
                        FavoritesItem item = (FavoritesItem)node.getUserObject();
                        FavoritesNode copy = ((FavoritesNode)node).getDeepCopy();
                        nodeClipboard.add(copy);
                    }
                }
            }
            saveFavorites();
            pasteMI.setEnabled(true);
        }
    }
    
    /**
     * Returns the CopyAction Object
     */
    public Action getCopyAction(){
        return copyAction;
    }
    
    /**
     * PopupListener class
     */
    public class PopupListener extends MouseAdapter{
        
        public void mousePressed(MouseEvent e){
	    maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e){
	    maybeShowPopup(e);
        }

	private void maybeShowPopup(MouseEvent e) {
            TreePath path = tree.getSelectionPath();
            TreePath clickPath = tree.getPathForLocation(e.getX(),e.getY());
            if (e.isPopupTrigger()) { 
		if (path != null && path.equals(clickPath)) {
		    separatorMI.setVisible(true);
		    cutMI.setVisible(true);
		    copyMI.setVisible(true);
		    pasteMI.setVisible(true);
		    removeMI.setVisible(true);
		} else {
		    separatorMI.setVisible(false);
		    cutMI.setVisible(false);
		    copyMI.setVisible(false);
		    pasteMI.setVisible(false);
		    removeMI.setVisible(false);
		}
                popup.show(e.getComponent(), e.getX(), e.getY());
	    }
	}
    }
    
    /**
     * Class for JTree supported D&D features.
     */
    public class FavoritesTree extends JTree implements DragGestureListener, DropTargetListener, DragSourceListener {
        
        protected Map.ID selectedID = null;
        
        private DragSource dragSource = null;
        private DragSourceContext dragSourceContext = null;
        private Point cursorLocation = null;
        private TreePath pathSource;
        private BufferedImage ghostImage;
        private Point offset = new Point();
        private Point ptLast = new Point();
        private Rectangle2D ghostRect = new Rectangle2D.Float();
        
        /**
         * Data object of selected FavoritesNode
         */
        private Map.ID hashCandidate;
        /**
         * Custom cursor
         */
        private Cursor dndCursor;
        
        /**
         * Creates FavoritesTree
         *
         * @param root The root node of the tree
         */
        public FavoritesTree(FavoritesNode root) {
            super(root);
            
            setEditable(true);
            
            dragSource = DragSource.getDefaultDragSource();
            
            DragGestureRecognizer dgr =
            dragSource.createDefaultDragGestureRecognizer(this,DnDConstants.ACTION_COPY_OR_MOVE,this);
            
            dgr.setSourceActions((dgr.getSourceActions()) &~ (InputEvent.BUTTON3_MASK));
            
            DropTarget dropTarget = new DropTarget(this, this);
            
            Toolkit tk = this.getToolkit();
            if(tk.getBestCursorSize(16,16).equals(new Dimension(64,64)))
                dndCursor = (Cursor) UIManager.get("HelpDnDCursor");
            if(dndCursor == null)
                debug("cursor is null");
            putClientProperty("JTree.lineStyle", "None");
        }
        
        public void dragGestureRecognized(DragGestureEvent e) {
            FavoritesNode dragNode = getSelectedNode();
            if (dragNode != null) {
                
                ghostImage = createGhostImage(e);
                Transferable transferable = (Transferable) dragNode.getUserObject();
                hashCandidate = (Map.ID)dataMap.get((FavoritesItem)transferable);
                
                Cursor cursor = DragSource.DefaultCopyDrop;
                int action = e.getDragAction();
                if(action == DnDConstants.ACTION_MOVE){
                    debug("action move");
                    cursor = DragSource.DefaultMoveDrop;
                }
                
                dragSource.startDrag(e,dndCursor,ghostImage,new Point(5,5), transferable,this);
            }
        }
        
        private BufferedImage createGhostImage(DragGestureEvent e){
            debug("createGhostImage");
            
            BufferedImage ghostImage = null;
            Point ptDragOrigin = e.getDragOrigin();
            TreePath path = getPathForLocation(ptDragOrigin.x, ptDragOrigin.y);
            if (path == null)
                return ghostImage;
            
            Rectangle raPath = getPathBounds(path);
            offset.setLocation(ptDragOrigin.x-raPath.x, ptDragOrigin.y-raPath.y);
            
            JLabel lbl = (JLabel) getCellRenderer().getTreeCellRendererComponent
            (this,path.getLastPathComponent(),false, isExpanded(path),getModel().isLeaf(path.getLastPathComponent()),0,false);
            lbl.setSize((int)raPath.getWidth(), (int)raPath.getHeight()); 
            
            ghostImage = new BufferedImage((int)raPath.getWidth(), (int)raPath.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics2D g2 = ghostImage.createGraphics();
            
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f));
            lbl.paint(g2);
            
            Icon icon = lbl.getIcon();
            int nStartOfText = (icon == null) ? 0 : icon.getIconWidth()+lbl.getIconTextGap();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, 0.5f));
            g2.setPaint(new GradientPaint(nStartOfText,	0, SystemColor.controlShadow,
            getWidth(),	0, new Color(255,255,255,0)));
            g2.fillRect(nStartOfText, 0, getWidth(), ghostImage.getHeight());
            
            g2.dispose();
            
            return ghostImage;
        }
        
        public void dragDropEnd(DragSourceDropEvent dsde) {
            debug("dragDropEnd");
        }
        
        public void dragEnter(DragSourceDragEvent dsde) {
            debug("dragEnter");
            setCursor(dsde);
        }
        
        public void dragOver(DragSourceDragEvent dsde) {
            debug("drag over");
            setCursor(dsde);
        }
        
        public void dropActionChanged(DragSourceDragEvent dsde) {
            debug("dropActionChanged");
            setCursor(dsde);
        }
        
        public void dragExit(DragSourceEvent dsde) {
            debug("dragExit");
        }
        
        /**
         * Sets appropriate cursor type according to event
         *
         * @param dsde DrageSourceDrageEvent
         */
        private void setCursor(DragSourceDragEvent dsde) {
            
            if (cursorLocation == null) return;
            
            TreePath destinationPath =
            getPathForLocation(cursorLocation.x, cursorLocation.y);
            
            DragSourceContext dsc = dsde.getDragSourceContext();
            
            if (testDropTarget(destinationPath, selectedTreePath) == null){
                dsc.setCursor(DragSource.DefaultCopyDrop);
                
            }
            else {
                dsc.setCursor(DragSource.DefaultCopyNoDrop);
            }
        }
        
        public void drop(DropTargetDropEvent e) {
            debug("drop");
            try {
                Transferable tr = e.getTransferable();
                
                if (!tr.isDataFlavorSupported( FavoritesItem.FAVORITES_FLAVOR)){
                    debug("drop rejected not data flavor");
                    e.rejectDrop();
                }
                
                FavoritesItem childInfo = (FavoritesItem) tr.getTransferData( FavoritesItem.FAVORITES_FLAVOR );
                Point loc = e.getLocation();
                TreePath destinationPath = getPathForLocation(loc.x, loc.y);
                
                final String msg = testDropTarget(destinationPath, selectedTreePath);
                if (msg != null) {
                    e.rejectDrop();
                    debug("Error : "+msg);
                    return;
                }
                
                FavoritesNode newParent =
                (FavoritesNode) destinationPath.getLastPathComponent();
                
                debug("new parent: "+newParent);
                //get old parent node
                FavoritesNode oldParent = (FavoritesNode) getSelectedNode().getParent();
                
                FavoritesNode selNode = (FavoritesNode) getSelectedNode();
                FavoritesItem selItem = (FavoritesItem) selNode.getUserObject();
                
                FavoritesNode newNode = selNode.getDeepCopy();
                
                int action = e.getDropAction();
                boolean copyAction = (action == DnDConstants.ACTION_COPY);
                debug("copy action: "+ copyAction);
                //make new child node
                FavoritesNode newChild = new FavoritesNode(childInfo);
                debug("new child: "+newChild);
                try {
                    if (!copyAction){
                        FavoritesNode fromRootNode = (FavoritesNode)connections.get(getSelectedNode());
                        if(fromRootNode != null)
                            fromRootNode.removeFromParent();
                        oldParent.remove(getSelectedNode());
                    }
                    DefaultTreeModel model = (DefaultTreeModel)getModel();
                    
                    if (!newParent.getAllowsChildren()){
                        TreeNode parent = newParent.getParent();
                        if(parent != null){
                            int index = parent.getIndex(newParent);
                            model.insertNodeInto(newNode,(DefaultMutableTreeNode)parent, index+1);
                        }
                    }else
                        model.insertNodeInto(newNode,newParent,newParent.getChildCount());
                    
                    if (copyAction)
                        e.acceptDrop(DnDConstants.ACTION_COPY);
                    else e.acceptDrop(DnDConstants.ACTION_MOVE);
                }
                catch (java.lang.IllegalStateException ils) {
                    debug("drop ejected");
                    e.rejectDrop();
                }
                
                e.getDropTargetContext().dropComplete(true);
                
                FavoritesItem newItem = (FavoritesItem) newNode.getUserObject();
                dataMap.put(newItem, hashCandidate);
                DefaultTreeModel model = (DefaultTreeModel) getModel();
                model.reload(oldParent);
                model.reload(newParent);
                TreePath parentPath = new TreePath(newParent.getPath());
                expandPath(parentPath);
                saveFavorites();
            }
            catch (IOException io) {
                e.rejectDrop();
                debug("drop rejected" + io);
            }
            catch (UnsupportedFlavorException ufe) {
                e.rejectDrop();
                debug("drop rejected: "+ ufe);
            }
        }
        
        public void dragEnter(DropTargetDragEvent e) {
        }
        
        public void dragExit(DropTargetEvent e) {
            if (!DragSource.isDragImageSupported()) {
                repaint(ghostRect.getBounds());
            }
        }
        
        public void dragOver(DropTargetDragEvent e) {
            
            Point pt = e.getLocation();
            if (pt.equals(ptLast))
                return;
            
            ptLast = pt;
            
            Point cursorLocationBis = e.getLocation();
            TreePath destinationPath = getPathForLocation(cursorLocationBis.x, cursorLocationBis.y);
            
            Graphics2D g2 = (Graphics2D) getGraphics();
            
            if(testDropTarget(destinationPath, selectedTreePath) == null){
                e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
                if (!DragSource.isDragImageSupported()) {
                    paintImmediately(ghostRect.getBounds());
                    ghostRect.setRect(pt.x - offset.x, pt.y - offset.y, ghostImage.getWidth(), ghostImage.getHeight());
                    g2.drawImage(ghostImage, AffineTransform.getTranslateInstance(ghostRect.getX(), ghostRect.getY()), null);
                }
            }else
                e.rejectDrag();
        }
        
        public void dropActionChanged(DropTargetDragEvent e) {
        }
        
        /**
         * Tests whether drop location is valid or not
         *
         * @param destination The destination path
         * @param dropper The path for the node to be dropped
         * @return Null if no problems, otherwise an explanation
         */
        private String testDropTarget(TreePath destination, TreePath dropper) {
            
            boolean destinationPathIsNull = destination == null;
            if (destinationPathIsNull){
                return "Invalid drop location.";
                //remove ghostLikeImage
            }
            
            FavoritesNode node = (FavoritesNode) destination.getLastPathComponent();
            
            if (destination.equals(dropper))
                return "Destination cannot be same as source";
            
            
            if ( dropper.isDescendant(destination))
                return "Destination node cannot be a descendant.";
            
            
            if ( dropper.getParentPath().equals(destination))
                return "Destination node cannot be a parent.";
            
            return null;
        }
    }
    
    /**
     * For printf debugging.
     */
    private static boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("BasicFavoritesNavigatorUI: " + str);
        }
    }
}
