/*
 * @(#)Constants_ar.java	1.2 06/10/30
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
 * @(#) Constants_ar.java 1.19 - last change made 10/19/01
 */

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * Constants used for localizing JavaHelp.
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values.
 */

public class Constants_ar extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle.
     */
    public Object[][] getContents() {
        return new Object[][] {

	    //  Constant strings and patterns
	    { "helpset.wrongPublicID",
		  "PublicID {0}  \u0645\u062c\u0647\u0648\u0644"},
	    { "helpset.wrongTitle",
		  "\u0645\u062d\u0627\u0648\u0644\u0629 \u0627\u0639\u0637\u0627\u0621 \u0639\u0646\u0648\u0627\u0646 \u0644 {0} \u0644\u0643\u0646 \u0647\u0646\u0627\u0643 \u0639\u0646\u0648\u0627\u0646 {1}."},
	    { "helpset.wrongHomeID",
		  ".homeID \u0642\u064a\u0645\u0629{\u0660}  \u0644 {\u0660}  \u0645\u062d\u0627\u0648\u0644\u0629 \u0627\u0639\u0637\u0627\u0621 \u0644\u0643\u0646 \u0647\u0646\u0627\u0643"},
	    { "helpset.subHelpSetTrouble",
		  "\u0647\u0646\u0627\u0643 \u0645\u0634\u0643\u0644\u0629 \u0627\u0646\u0634\u0627\u0621 subhelpset:{0}."},
	    { "helpset.malformedURL",
		  "\u0645\u0648\u0642\u0639 URL {0). \u063a\u064a\u0631 \u0635\u0627\u0644\u062d"},
	    { "helpset.incorrectURL",
		  "\u0645\u0648\u0642\u0639 URL {0). \u063a\u064a\u0631 \u0635\u062d\u064a\u062d"},
	    { "helpset.wrongText",
		  "{0} \u064a\u062a\u0639\u0630\u0631 \u0627\u062f\u062e\u0627\u0644 \u0646\u0635\u0648\u0635 {1}."},
	    { "helpset.wrongTopLevel",
		  "{0} TAG \u0645\u0646 \u0627\u0644\u0645\u0633\u062a\u0648\u0649 \u0627\u0644\u0623\u0639\u0644\u0649 \u063a\u064a\u0631 \u0645\u0645\u0643\u0646."},
	    { "helpset.wrongParent",
		  "TAG \u0631\u0626\u064a\u0633\u064a \u0645\u0646 {0} \u0644\u0627 \u064a\u0645\u0643\u0646 \u0623\u0646 \u064a\u0643\u0648\u0646 . {1}."},
	    { "helpset.unbalanced",
		  "tag {0} \u063a\u064a\u0631 \u0645\u0648\u0632\u0648\u0646"},
	    { "helpset.wrongLocale",
		  "\u062a\u0646\u0628\u064a\u0647: \u0633\u0645\u0629 xml:lang {0} \u062a\u062a\u0639\u0627\u0631\u0636 \u0645\u0639 \u0627\u0644\u0642\u064a\u0645\u0629 \u0627\u0644\u0627\u0641\u062a\u0631\u0627\u0636\u064a\u0629 {1} \u0648\u0645\u0639 \u0627\u0644\u0642\u064a\u0645\u0629 \u0627\u0644\u0627\u0641\u062a\u0631\u0627\u0636\u064a\u0629 {2}."},
	    { "helpset.unknownVersion",
		  "\u0627\u0635\u062f\u0627\u0631 \u0645\u062c\u0647\u0648\u0644{0}."},

		// IndexView messages
	    { "index.invalidIndexFormat",
		  "\u062a\u0646\u0628\u064a\u0647: \u062a\u0646\u0633\u064a\u0642 \u0627\u0644\u0641\u0647\u0631\u0633 \u063a\u064a\u0631 \u0635\u0627\u0644\u062d"},
	    { "index.unknownVersion",
		  "\u0627\u0635\u062f\u0627\u0631 \u0645\u062c\u0647\u0648\u0644{0}."},

		// TOCView messages
	    { "toc.wrongPublicID",
		  "PublicID {0}  \u0645\u062c\u0647\u0648\u0644"},
	    { "toc.invalidTOCFormat",
		  "\u062a\u0646\u0628\u064a\u0647: \u0642\u0627\u0626\u0645\u0629 \u0627\u0644\u0645\u062d\u062a\u0648\u064a\u0627\u062a \u063a\u064a\u0631 \u0635\u0627\u0644\u062d\u0629"},
	    { "toc.unknownVersion",
		  "\u0627\u0635\u062f\u0627\u0631 \u0645\u062c\u0647\u0648\u0644{0}."},
                  
            // FavoritesView messages
	    { "favorites.invalidFavoritesFormat",
		  "\u062a\u0646\u0628\u064a\u0647: \u062a\u0646\u0633\u064a\u0642 \u0627\u0644\u0645\u0641\u0636\u0644\u0629 \u063a\u064a\u0631 \u0635\u0627\u0644\u062d"},
	    { "favorites.unknownVersion",
		  "\u0627\u0635\u062f\u0627\u0631 \u0645\u062c\u0647\u0648\u0644{0}."},

		// Map messages
	    { "map.wrongPublicID",
		  "PublicID {0}  \u0645\u062c\u0647\u0648\u0644"},
	    { "map.invalidMapFormat",
		  "\u062a\u0646\u0628\u064a\u0647: \u062a\u0646\u0633\u064a\u0642 \u0627\u0644 Map \u063a\u064a\u0631 \u0635\u0627\u0644\u062d"},
	    { "map.unknownVersion",
		  "\u0627\u0635\u062f\u0627\u0631 \u0645\u062c\u0647\u0648\u0644{0}."},

	    // GUI components
	    // Labels
	    { "index.findLabel", "\u0628\u062d\u062b: "},
            
	    { "search.findLabel", "\u0628\u062d\u062b: "},
	    { "search.hitDesc", "\u0639\u062f\u062f \u0645\u0631\u0627\u062a \u0627\u0644\u0638\u0647\u0648\u0631 \u0641\u064a \u0627\u0644\u0645\u0633\u062a\u0646\u062f"},
	    { "search.qualityDesc", "" },
	    { "search.high", "\u0639\u0627\u0644\u064a"},
	    { "search.midhigh", "\u0648\u0633\u0637 \u0627\u0644\u0639\u0627\u0644\u064a"},
	    { "search.mid", "\u0648\u0633\u0637"},
	    { "search.midlow", "\u0648\u0633\u0637 \u0627\u0644\u0645\u0646\u062e\u0641\u0636"},
	    { "search.low", "\u0645\u0646\u062e\u0641\u0636"},
            
            { "favorites.add", "\u0627\u0636\u0627\u0641\u0629"},
            { "favorites.remove", "\u0627\u0632\u0627\u0644\u0629"},
            { "favorites.folder", "\u0645\u062c\u0644\u062f \u062c\u062f\u064a\u062f"},
            { "favorites.name", "\u0627\u0633\u0645"},
            { "favorites.cut", "\u0642\u0635"},
            { "favorites.paste", "\u0644\u0635\u0642"},
            { "favorites.copy" , "\u0646\u0633\u062e"},

            { "history.homePage", "\u0627\u0644\u0635\u0641\u062d\u0629 \u0627\u0644\u0631\u0626\u064a\u0633\u064a\u0629"},
            { "history.unknownTitle", "<\u0639\u0646\u0648\u0627\u0646 \u0635\u0641\u062d\u0629 \u0645\u062c\u0647\u0648\u0644>"},

            // ToolTips for Actions
            { "tooltip.BackAction", "\u0627\u0644\u0635\u0641\u062d\u0629 \u0627\u0644\u0633\u0627\u0628\u0642\u0629"},
	    { "tooltip.ForwardAction", "\u0627\u0644\u0635\u0641\u062d\u0629 \u0627\u0644\u062a\u0627\u0644\u064a\u0629"},
	    { "tooltip.PrintAction", "\u0637\u0628\u0627\u0639\u0629"},
	    { "tooltip.PrintSetupAction", "\u0627\u0639\u062f\u0627\u062f \u0627\u0644\u0635\u0641\u062d\u0629"},
	    { "tooltip.ReloadAction", "\u0627\u0639\u0627\u062f\u0629 \u062a\u062d\u0645\u064a\u0644"},
            { "tooltip.FavoritesAction", "\u0623\u0636\u0641 \u0644\u0644\u0645\u0641\u0636\u0644\u0629"},
            { "tooltip.HomeAction", "\u0631\u062c\u0648\u0639 \u0644\u0644\u0635\u0641\u062d\u0629 \u0627\u0644\u0631\u0626\u064a\u0633\u064a\u0629"},

	    // Accessibility names
	    { "access.BackAction", "\u0627\u0644\u0632\u0631 \u0633\u0627\u0628\u0642"},
	    { "access.ForwardAction", "\u0627\u0644\u0632\u0631 \u0627\u0644\u0627\u062a\u064a"},
	    { "access.HistoryAction", "\u0627\u0644\u0632\u0631 \u0645\u062d\u0641\u0648\u0638\u0627\u062a"},
	    { "access.PrintAction", "\u0627\u0644\u0632\u0631 \u0637\u0628\u0627\u0639\u0629"},
	    { "access.PrintSetupAction", "\u0627\u0644\u0632\u0631 \u0627\u0639\u062f\u0627\u062f \u0627\u0644\u0635\u0641\u062d\u0629"},
	    { "access.ReloadAction", "\u0627\u0644\u0632\u0631 \u0627\u0639\u0627\u062f\u0629 \u062a\u062d\u0645\u064a\u0644"},
            { "access.HomeAction", "\u0627\u0644\u0632\u0631 \u0628\u062f\u0627\u064a\u0629"},
            { "access.FavoritesAction", "\u0627\u0644\u0632\u0631 \u0623\u0636\u0641 \u0644\u0644\u0645\u0641\u0636\u0644\u0629"},
            { "access.contentViewer", "\u0639\u0627\u0631\u0636 \u0627\u0644\u0645\u062d\u062a\u0648\u064a\u0627\u062a"}

       };
    }
}
