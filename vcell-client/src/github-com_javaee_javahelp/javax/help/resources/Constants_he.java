/*
 * @(#)Constants_he.java	1.2 06/10/30
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
 * @(#) Constants_he.java 1.19 - last change made 10/19/01
 */

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * Constants used for localizing JavaHelp.
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values.
 */

public class Constants_he extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle.
     */
    public Object[][] getContents() {
        return new Object[][] {

	    //  Constant strings and patterns
	    { "helpset.wrongPublicID",
		  "PublicID {0} \u05dc\u05d0 \u05d9\u05d3\u05d5\u05e2"},
	    { "helpset.wrongTitle",
		  "\u05de\u05e0\u05e1\u05d4 \u05dc\u05d4\u05d2\u05d3\u05d9\u05e8 \u05d0\u05ea \u05d4\u05db\u05d5\u05ea\u05e8\u05ea \u05db {0} \u05d0\u05da \u05db\u05d1\u05e8 \u05d4\u05d5\u05d2\u05d3\u05e8\u05d4 \u05db {1}."},
	    { "helpset.wrongHomeID",
		  " homeID \u05db {0} \u05d0\u05da \u05db\u05d1\u05e8 \u05d4\u05d5\u05d2\u05d3\u05e8 \u05db {1}."},
	    { "helpset.subHelpSetTrouble",
		  "\u05d1\u05e2\u05d9\u05d5\u05ea \u05d1\u05d9\u05e6\u05d9\u05e8\u05ea \u05ea\u05ea-\u05e2\u05e8\u05db\u05ea \u05e2\u05d6\u05e8\u05d4:{0}.-"},
	    { "helpset.malformedURL",
		  "\u05db\u05ea\u05d5\u05d1\u05ea URL \u05d0\u05d9\u05e0\u05d4 \u05de\u05e2\u05d5\u05e6\u05d1\u05ea \u05db\u05e8\u05d0\u05d5\u05d9:{0}."},
	    { "helpset.incorrectURL",
		  "\u05db\u05ea\u05d5\u05d1\u05ea URL \u05e9\u05d2\u05d5\u05d9\u05d4"},
	    { "helpset.wrongText",
		  "{0} \u05d0\u05d9\u05e0\u05d4 \u05d9\u05db\u05d5\u05dc\u05d4 \u05dc\u05d4\u05db\u05d9\u05dc \u05db\u05d9\u05ea\u05d5\u05d1 {1}."},
	    { "helpset.wrongTopLevel",
		  "{0} \u05d0\u05d9\u05e0\u05d4 \u05d9\u05db\u05d5\u05dc\u05d4 \u05dc\u05d4\u05d9\u05d5\u05ea \u05ea\u05d2\u05d9\u05ea \u05d1\u05e8\u05de\u05d4 \u05e2\u05dc\u05d9\u05d5\u05e0\u05d4."},
	    { "helpset.wrongParent",
		  "\u05ea\u05d2\u05d9\u05ea \u05d4\u05d0\u05dd \u05e9\u05dc {0} \u05dc\u05d0 \u05d9\u05db\u05d5\u05dc\u05d4 \u05dc\u05d4\u05d9\u05d5\u05ea {1}."},
	    { "helpset.unbalanced",
		  "\u05ea\u05d2\u05d9\u05ea \u05d1\u05dc\u05ea\u05d9 \u05de\u05d0\u05d5\u05d6\u05e0\u05ea {0}."},
	    { "helpset.wrongLocale",
		  "\u05d0\u05d6\u05d4\u05e8\u05d4: \u05de\u05d0\u05e4\u05d9\u05d9\u05df xml:lang {0} \u05de\u05ea\u05e0\u05d2\u05e9 \u05e2\u05dd \u05d1\u05e8\u05d9\u05e8\u05ea \u05de\u05d7\u05d3\u05dc {1} \u05d5\u05e2\u05dd \u05d1\u05e8\u05d9\u05e8\u05ea \u05de\u05d7\u05d3\u05dc {2}."},
	    { "helpset.unknownVersion",
		  " \u05d2\u05d9\u05e8\u05e1\u05d4 \u05d1\u05dc\u05ea\u05d9 \u05d9\u05d3\u05d5\u05e2\u05d4 {0}."},

		// IndexView messages
	    { "index.invalidIndexFormat",
		  "\u05d0\u05d6\u05d4\u05e8\u05d4: \u05e6\u05d5\u05e8\u05ea \u05d0\u05d9\u05e0\u05d3\u05e7\u05e1 \u05dc\u05d0 \u05d7\u05d5\u05e7\u05d9\u05ea"},
	    { "index.unknownVersion",
		  "\u05d2\u05d9\u05e8\u05e1\u05d4 \u05d1\u05dc\u05ea\u05d9 \u05d9\u05d3\u05d5\u05e2\u05d4 {0}."},

		// TOCView messages
	    { "toc.wrongPublicID",
		  "PublicID {0} \u05d1\u05dc\u05ea\u05d9 \u05d9\u05d3\u05d5\u05e2\u05d4"},
	    { "toc.invalidTOCFormat",
		  "\u05d0\u05d6\u05d4\u05e8\u05d4: \u05e6\u05d5\u05e8\u05ea \u05ea\u05d5\u05db\u05df \u05e2\u05e0\u05d9\u05e0\u05d9\u05dd \u05dc\u05d0 \u05d7\u05d5\u05e7\u05d9\u05ea"},
	    { "toc.unknownVersion",
		  "\u05d2\u05d9\u05e8\u05e1\u05d4 \u05d1\u05dc\u05ea\u05d9 \u05d9\u05d3\u05d5\u05e2\u05d4 {0}."},
                  
            // FavoritesView messages
	    { "favorites.invalidFavoritesFormat",
		  "\u05d0\u05d6\u05d4\u05e8\u05d4: \u05e6\u05d5\u05e8\u05ea \u05de\u05d5\u05e2\u05d3\u05e4\u05d9\u05dd \u05dc\u05d0 \u05d7\u05d5\u05e7\u05d9\u05ea"},
	    { "favorites.unknownVersion",
		  "\u05d2\u05d9\u05e8\u05e1\u05d4 \u05d1\u05dc\u05ea\u05d9 \u05d9\u05d3\u05d5\u05e2\u05d4 {0}."},

		// Map messages
	    { "map.wrongPublicID",
		  "PublicID {0} \u05d1\u05dc\u05ea\u05d9 \u05d9\u05d3\u05d5\u05e2\u05d4"},
	    { "map.invalidMapFormat",
		  "\u05d0\u05d6\u05d4\u05e8\u05d4: \u05e6\u05d5\u05e8\u05ea \u05de\u05e4\u05ea \u05dc\u05d0 \u05d7\u05d5\u05e7\u05d9\u05ea"},
	    { "map.unknownVersion",
		  "\u05d2\u05d9\u05e8\u05e1\u05d4 \u05d1\u05dc\u05ea\u05d9 \u05d9\u05d3\u05d5\u05e2\u05d4 {0}."},

	    // GUI components
	    // Labels
	    { "index.findLabel", "\u05d7\u05e4\u05e9: "},
            
	    { "search.findLabel", "\u05d7\u05e4\u05e9: "},
	    { "search.hitDesc", "\u05de\u05e1\u05e4\u05e8 \u05de\u05d5\u05e4\u05e2\u05d9\u05dd \u05d1\u05de\u05e1\u05de\u05da"},
	    { "search.qualityDesc", "\u05e2\u05e8\u05da \u05d4\u05e7\u05e0\u05e1 \u05d4\u05e0\u05de\u05d5\u05da \u05d1\u05d9\u05d5\u05ea\u05e8 \u05d1\u05de\u05e1\u05de\u05da" },
	    { "search.high", "\u05d2\u05d1\u05d5\u05d4"},
	    { "search.midhigh", "\u05d1\u05d9\u05e0\u05d5\u05e0\u05d9 \u05d2\u05d1\u05d5\u05d4"},
	    { "search.mid", "\u05d1\u05d9\u05e0\u05d5\u05e0\u05d9"},
	    { "search.midlow", "\u05d1\u05d9\u05e0\u05d5\u05e0\u05d9 \u05e0\u05de\u05d5\u05da"},
	    { "search.low", "\u05e0\u05de\u05d5\u05da"},
            
            { "favorites.add", "\u05d4\u05d5\u05e1\u05e4\u05d4"},
            { "favorites.remove", "\u05d4\u05e1\u05e8\u05d4"},
            { "favorites.folder", "\u05ea\u05d9\u05e7\u05d9\u05d4 \u05d7\u05d3\u05e9\u05d4"},
            { "favorites.name", "\u05e9\u05dd/\u05de\u05ea\u05df \u05e9\u05dd"},
            { "favorites.cut", "\u05d2\u05d6\u05d9\u05e8\u05d4"},
            { "favorites.paste", "\u05d4\u05d3\u05d1\u05e7\u05d4"},
            { "favorites.copy" , "\u05d4\u05e2\u05ea\u05e7\u05d4"},

            { "history.homePage", "\u05d3\u05e3 \u05d4\u05d1\u05d9\u05ea"},
            { "history.unknownTitle", "<\u05db\u05d5\u05ea\u05e8\u05ea \u05d3\u05e3 \u05d0\u05d9\u05e0\u05d4 \u05d9\u05d3\u05d5\u05e2\u05d4>"},

            // ToolTips for Actions
            { "tooltip.BackAction", "\u05d4\u05d3\u05e3 \u05d4\u05e7\u05d5\u05d3\u05dd"},
	    { "tooltip.ForwardAction", "\u05d4\u05d3\u05e3 \u05d4\u05d1\u05d0"},
	    { "tooltip.PrintAction", "\u05d4\u05d3\u05e4\u05e1\u05d4"},
	    { "tooltip.PrintSetupAction", "\u05d4\u05d2\u05d3\u05e8\u05ea \u05e2\u05de\u05d5\u05d3"},
	    { "tooltip.ReloadAction", "\u05d8\u05e2\u05d9\u05e0\u05d4 \u05de\u05d7\u05d3\u05e9"},
            { "tooltip.FavoritesAction", "\u05d4\u05d5\u05e1\u05e4\u05d4 \u05dc\u05de\u05d5\u05e2\u05d3\u05e4\u05d9\u05dd"},
            { "tooltip.HomeAction", "\u05e2\u05d1\u05d5\u05e8 \u05dc\u05d3\u05e3 \u05d4\u05d1\u05d9\u05ea"},

	    // Accessibility names
	    { "access.BackAction", "\u05d4\u05db\u05e4\u05ea\u05d5\u05e8 \u05d4\u05e7\u05d5\u05d3\u05dd"},
	    { "access.ForwardAction", "\u05d4\u05db\u05e4\u05ea\u05d5\u05e8 \u05d4\u05d1\u05d0"},
	    { "access.HistoryAction", "\u05db\u05e4\u05ea\u05d5\u05e8 \u05d4\u05e1\u05d8\u05d5\u05e8\u05d9\u05d4"},
	    { "access.PrintAction", "\u05db\u05e4\u05ea\u05d5\u05e8 \u05d4\u05d3\u05e4\u05e1\u05d4"},
	    { "access.PrintSetupAction", "\u05db\u05e4\u05ea\u05d5\u05e8 \u05d4\u05d2\u05d3\u05e8\u05ea \u05e2\u05de\u05d5\u05d3"},
	    { "access.ReloadAction", "\u05db\u05e4\u05ea\u05d5\u05e8 \u05d8\u05e2\u05d9\u05e0\u05d4 \u05de\u05d7\u05d3\u05e9"},
            { "access.HomeAction", "\u05db\u05e4\u05ea\u05d5\u05e8 \u05d4\u05d1\u05d9\u05ea"},
            { "access.FavoritesAction", "\u05db\u05e4\u05ea\u05d5\u05e8 \u05d4\u05d5\u05e1\u05e4\u05d4 \u05dc\u05de\u05d5\u05e2\u05d3\u05e4\u05d9\u05dd"},
            { "access.contentViewer", "\u05ea\u05d5\u05db\u05e0\u05ea \u05e6\u05e4\u05d9\u05d9\u05d4 \u05d1\u05ea\u05d5\u05db\u05df"}

       };
    }
}
