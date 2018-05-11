/*
 * @(#)ValidateTag.java	1.3 06/10/30
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

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.Tag;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.InvalidHelpSetContextException;

/**
 * Validates a HelpSet file and an Map.ID thourgh either a Request Parameter
 * or passed as a JSP argument and sets the state in a HelpBroker. Additionally
 * allows a HelpSet to be merged with the current HelpSet in the HelpBroker.
 *
 * @author Roger D. BRinkley
 * @version	1.3	10/30/06
 * @see javax.help.HelpSet
 * @see javax.help.Map.ID
 * @see javax.help.HelpBroker
 */

public class ValidateTag extends TagSupport {
    private HelpBroker helpBroker;
    private String invalidURLPath = "invalidhelp.html";
    private String hsName = null;
    private String id = null;
    private boolean merge = false;

    public void setHelpBroker(HelpBroker helpBroker) {
	this.helpBroker = helpBroker;
    }

    public void setInvalidURL(String relativeURLPath) {
	this.invalidURLPath = relativeURLPath;
    }

    public void setHelpSetName(String hsName) {
	this.hsName = hsName;
    }

    public void setCurrentID(String id) {
	this.id = id;
    }

    public void setMerge(boolean merge) {
	this.merge = merge;
    }

    public int doStartTag() {
	checkRequestParams();
	validateHelpSet();
	validateID();
	return SKIP_BODY;
    }

    private void checkRequestParams() {
	ServletRequest request = pageContext.getRequest();

	if (hsName == null) {
	    hsName = request.getParameter("helpset");
	}

	if (id == null) {
	    id = request.getParameter("id");
	}
    }

    private void validateHelpSet() {
	HelpSet tesths = helpBroker.getHelpSet();

	// If there is already a HelpSet and there isn't a hsName
	// return. Nothing to do
	if (tesths != null && hsName == null) {
	    return;
	}

	// If there isn't a HelpSet and there isn't a hsName
	// then forward to the invalid page
	if (tesths == null && hsName == null) {
	    try {
		pageContext.forward(invalidURLPath);
	    } catch (Exception e) {
		// ignore it
		return;
	    }
	} 


	// If we don't have a helpset and there is a hsName
	// the create one and set the HelpBroker to this page
	if (tesths == null && hsName != null) {
	    helpBroker.setHelpSet(createHelpSet());
	    return;
	} 

	// If we have a helpset and there is a hsname
	// and merging is turned on, merge the helpset
	if (tesths != null && hsName != null && merge) {
	    tesths.add(createHelpSet());
	}
    }

    private HelpSet createHelpSet() {
	HelpSet hs = null;
	ServletRequest request = pageContext.getRequest();
	if (!hsName.startsWith("/")) {
	    hsName = "/" + hsName;
	}
	URL url = null;
	try {
	    if (hsName.startsWith("http")) {
		url = new URL (hsName);
	    } else {
		url = new URL(request.getScheme(),
			      request.getServerName(),
			      request.getServerPort(),
			      hsName);
	    }
	    hs = new HelpSet(null, url);
	} catch (MalformedURLException e) {
	    // ignore
	} catch (HelpSetException hse) {
	    // this is a serious error
	    throw new RuntimeException(hse.getMessage());
	}
	return hs;
    }

    private void validateID() {
	if (id != null) {
	    helpBroker.setCurrentID(id);
	} else if (helpBroker.getCurrentID() == null && 
		   helpBroker.getCurrentURL() == null) {
	    try {
		helpBroker.setCurrentID(helpBroker.getHelpSet().getHomeID());
	    } catch (InvalidHelpSetContextException e) {
		// ignore
	    }
	}
    }
}

