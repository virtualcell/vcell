/*
 * @(#)IndexItemTag.java	1.4 06/10/30
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

package javax.help.tagext;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.Map.ID;
import javax.help.Merge;
import javax.help.MergeHelpUtilities;
import javax.help.NavigatorView;
import javax.help.IndexItem;
import javax.help.IndexView;
import javax.help.SortMerge;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * The JSP tag class for an IndexItem
 *
 * @author Roger D. Brinkley
 * @version	1.4	10/30/06
 * @see javax.help.IndexItem
 */

public class IndexItemTag extends BodyTagSupport {
    private Enumeration treeEnum;
    private DefaultMutableTreeNode topNode;
    private String baseID="root";
    private HelpBroker hb;
    private IndexView view=null;

    public void setIndexView(IndexView view) {
	this.view = view;
    }

    public void setBaseID(String baseID) {
	this.baseID = baseID;
    }

    public void setHelpBroker(HelpBroker hb) {
	this.hb = hb;
    }

    private void initialize() {
	if (view == null) {
	    return;
	}
	topNode = view.getDataAsTree();
	treeEnum = topNode.preorderEnumeration();

	String mergeType = view.getMergeType();
	HelpSet hs = view.getHelpSet();
        Locale locale = hs.getLocale();


	// Make sure the children are all handled correctly
	MergeHelpUtilities.mergeNodeChildren(mergeType, topNode);
        
	// add all the subhelpsets
        addSubHelpSets(hs);
    }

    /** Adds subhelpsets
     *
     * @param hs The HelpSet which subhelpsets will be added
     */
    private void addSubHelpSets(HelpSet hs){
        for( Enumeration e = hs.getHelpSets(); e.hasMoreElements(); ) {
	    HelpSet ehs = (HelpSet) e.nextElement();
	    if (ehs == null) {
		continue;
	    }
            // merge views
            NavigatorView[] views = ehs.getNavigatorViews();
            for(int i = 0; i < views.length; i++){
		if (views[i] instanceof IndexView) {
		    Merge mergeObject = 
			Merge.DefaultMergeFactory.getMerge(view, views[i]);
		    if (mergeObject != null) {
			mergeObject.processMerge(topNode);
		    }
		}
            }
            addSubHelpSets( ehs );
	}
    }    

    public int doStartTag() {
	initialize();
	if(treeEnum.hasMoreElements()) {
	    DefaultMutableTreeNode node = 
		(DefaultMutableTreeNode) treeEnum.nextElement();
	    // never use the top node. It is just the container node
 	    if (node == topNode) {
		try {
		    node = (DefaultMutableTreeNode) treeEnum.nextElement();
		} catch (NoSuchElementException e) {
		    return SKIP_BODY;
		}
	    }
	    setNodeAttributes(node);
	    return EVAL_BODY_TAG;
	}
	else {
	    return SKIP_BODY;
	}
    }

    public int doAfterBody() throws JspException {
	BodyContent body = getBodyContent();
	try {
	    body.writeOut(getPreviousOut());
	} catch (IOException e) {
	    throw new JspTagException("IndexItemTag: " + e.getMessage());
	}
	// clear up so the next time the body content is empty
	body.clearBody();
	if (treeEnum.hasMoreElements()) {
	    DefaultMutableTreeNode node = 
		(DefaultMutableTreeNode) treeEnum.nextElement();
	    setNodeAttributes(node);
	    return EVAL_BODY_TAG;
	} else {
	    return SKIP_BODY;
	}
    }

    private void setNodeAttributes(DefaultMutableTreeNode node) {
	    IndexItem item = (IndexItem) node.getUserObject();
	    pageContext.setAttribute("name", item.getName());
	    String helpID = "";
	    if (item.getID() != null) {
		helpID = item.getID().id;
	    }
	    pageContext.setAttribute("helpID", helpID);
	    pageContext.setAttribute("parent", Integer.toHexString(node.getParent().hashCode()));
	    String id = getID(node.getParent());
	    pageContext.setAttribute("parentID", id);
	    pageContext.setAttribute("node", Integer.toHexString(node.hashCode()));
	    id = getID(node);
	    pageContext.setAttribute("nodeID", id);
	    String content = getContentURL(item);
	    pageContext.setAttribute("contentURL", content);
	    String expansionType = Integer.toString(item.getExpansionType());
	    pageContext.setAttribute("expansionType", expansionType);
    }

    private String getID(TreeNode node) {
	if (node == topNode) {
	    return baseID;
	}
	TreeNode parent = node.getParent();
	if (parent == null) {
	    return "";
	}
	String id = getID(parent);
        id = id.concat("." + Integer.toString(parent.getIndex(node)));
	return id;
    }

    /**
     * return the content URL in String form for a given IndexItem
     * 
     * returns an empty String if no content exists.
     */
    private String getContentURL(IndexItem item) {
	URL url = null;
	ID id = item.getID();
	if (id != null) {
	    HelpSet hs = id.hs;
	    Map map = hs.getLocalMap();
	    try {
		url = map.getURLFromID(id);
	    } catch (MalformedURLException e) {
		// just ignore
	    }
	}
	if (url == null) {
	    return "";
	}
	return url.toExternalForm();
    }

}

