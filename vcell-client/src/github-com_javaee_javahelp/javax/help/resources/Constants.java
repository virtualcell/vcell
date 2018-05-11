/*
 * @(#)Constants.java	1.22 06/10/30
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
 * @(#) Constants.java 1.22 - last change made 10/30/06
 */

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * Constants used for localizing JavaHelp.
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values.
 */

public class Constants extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle.
     */
    public Object[][] getContents() {
        return new Object[][] {

	    //  Constant strings and patterns
	    { "helpset.wrongPublicID",
		  "Unknown PublicID {0}"},
	    { "helpset.wrongTitle",
		  "Attempting to set title to {0} but already has value {1}."},
	    { "helpset.wrongHomeID",
		  "Attempting to set homeID to {0} but already has value {1}."},
	    { "helpset.subHelpSetTrouble",
		  "Trouble creating subhelpset: {0}."},
	    { "helpset.malformedURL",
		  "Malformed URL: {0}."},
	    { "helpset.incorrectURL",
		  "Incorrect URL: {0}."},
	    { "helpset.wrongText",
		  "{0} cannot contain text {1}."},
	    { "helpset.wrongTopLevel",
		  "{0} cannot be a top level tag."},
	    { "helpset.wrongParent",
		  "The parent tag for {0} cannot be {1}."},
	    { "helpset.unbalanced",
		  "Unbalanced tag {0}."},
	    { "helpset.wrongLocale",
		  "Warning: xml:lang attribute {0} conflicts with default {1} and with default {2}"},
	    { "helpset.unknownVersion",
		  "Unknown Version {0}."},

		// IndexView messages
	    { "index.invalidIndexFormat",
		  "Warning: Invalid Index format"},
	    { "index.unknownVersion",
		  "Unknown Version {0}."},

		// TOCView messages
	    { "toc.wrongPublicID",
		  "Unknown PublicID {0}"},
	    { "toc.invalidTOCFormat",
		  "Warning: Invalid TOC format"},
	    { "toc.unknownVersion",
		  "Unknown Version {0}."},
                  
            // FavoritesView messages
	    { "favorites.invalidFavoritesFormat",
		  "Warning: Invalid Favorites format"},
	    { "favorites.unknownVersion",
		  "Unknown Version {0}."},

		// Map messages
	    { "map.wrongPublicID",
		  "Unknown PublicID {0}"},
	    { "map.invalidMapFormat",
		  "Warning: Invalid Map format"},
	    { "map.unknownVersion",
		  "Unknown Version {0}."},

	    // GUI components
	    // Labels
	    { "index.findLabel", "Find: "},
            
	    { "search.findLabel", "Find: "},
	    { "search.hitDesc", "Number of occurances in document"},
	    { "search.qualityDesc", "Lowest penality value in document" },
	    { "search.high", "High"},
	    { "search.midhigh", "Medium high"},
	    { "search.mid", "Medium"},
	    { "search.midlow", "Medium low"},
	    { "search.low", "Low"},
            
            { "favorites.add", "Add"},
            { "favorites.remove", "Remove"},
            { "favorites.folder", "New Folder"},
            { "favorites.name", "Name"},
            { "favorites.cut", "Cut"},
            { "favorites.paste", "Paste"},
            { "favorites.copy" , "Copy"},

            { "history.homePage", "Home page"},
            { "history.unknownTitle", "<unknown page title>"},

            // ToolTips for Actions
            { "tooltip.BackAction", "Previous Page"},
	    { "tooltip.ForwardAction", "Next Page"},
	    { "tooltip.PrintAction", "Print"},
	    { "tooltip.PrintSetupAction", "Page Setup"},
	    { "tooltip.ReloadAction", "Reload"},
            { "tooltip.FavoritesAction", "Add to Favorites"},
            { "tooltip.HomeAction", "Go to home page"},

	    // Accessibility names
	    { "access.BackAction", "Previous Button"},
	    { "access.ForwardAction", "Next Button"},
	    { "access.HistoryAction", "History Button"},
	    { "access.PrintAction", "Print Button"},
	    { "access.PrintSetupAction", "Page Setup Button"},
	    { "access.ReloadAction", "Reload Button"},
            { "access.HomeAction", "Home Button"},
            { "access.FavoritesAction", "Add to Favorites Button"},
            { "access.contentViewer", "Content Viewer"}

       };
    }
}
