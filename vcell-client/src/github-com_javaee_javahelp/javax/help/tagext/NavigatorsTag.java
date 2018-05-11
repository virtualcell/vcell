/*
 * @(#)NavigatorsTag.java	1.4 06/10/30
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
import javax.servlet.ServletRequest;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.Map.ID;
import javax.help.NavigatorView;

/**
 * The JSP tag class for a Navigators
 *
 * @author Roger D. Brinkley
 * @version	1.4	10/30/06
 * @see javax.help.HelpSet.getNavigatorView()
 * @see javax.help.NavigatorView
 */

public class NavigatorsTag extends BodyTagSupport {
    private HelpBroker hb;
    private HelpSet hs;
    private String curNav = null;
    private NavigatorView[] views;
    private int i;

    public void setHelpBroker(HelpBroker hb) {
	this.hb = hb;
	hs = hb.getHelpSet();
    }

    public void setCurrentNav(String nav) {
	curNav = nav;
    }

    public void initialize() {
	checkRequestParams();
	initCurNav();
	views = hs.getNavigatorViews();
    }

    public int doStartTag() {
	initialize();
	if(views.length > 0) {
	    i = 0;
	    setNavigatorAttributes(views[i++]);
	    return EVAL_BODY_TAG;
	} else {
	    return SKIP_BODY;
	}
    }

    private void checkRequestParams() {
	ServletRequest request = pageContext.getRequest();

	String nav = request.getParameter("nav");
	if (nav != null) {
	    curNav = nav;
	}

    }

    private void initCurNav() {
	if (curNav != null) {
	    try {
		hb.setCurrentView(curNav);
	    } catch (IllegalArgumentException e) {
		// Ignore
	    }   
	} else {
	    curNav = hb.getCurrentView();
	}
    }


    public int doAfterBody() throws JspException {
	BodyContent body = getBodyContent();
	try {
	    body.writeOut(getPreviousOut());
	} catch (IOException e) {
	    throw new JspTagException("NavigatorsTag: " + e.getMessage());
	}

	// clear up so the next time the body content is empty
	body.clearBody();
	if (i < views.length) {
	    setNavigatorAttributes(views[i++]);
	    return EVAL_BODY_TAG;
	} else {
	    return SKIP_BODY;
	}
    }

    private void setNavigatorAttributes(NavigatorView view) {
	pageContext.setAttribute("className", view.getClass().getName());
	pageContext.setAttribute("name", view.getName());
	pageContext.setAttribute("tip", view.getLabel());
	String icon = getIconURL(view);
	pageContext.setAttribute("iconURL", icon);
	pageContext.setAttribute("isCurrentNav", new Boolean(curNav.compareTo(view.getName()) == 0));
    }

    /**
     * return the icon URL in String form for a given TOCItem
     * 
     * returns empty String if no content exists.
     */
    private String getIconURL(NavigatorView view) {
	URL url = null;
	ID id = view.getImageID();
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

