/*
 * @(#)Constants_sk.java	1.10 06/10/30
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

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * Constants used for localizing JavaHelp.
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values.
 *
 * @author Richard Gregor 
 * @version	1.10	10/30/06
 *
 */

public class Constants_sk extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle.
     */
    public Object[][] getContents() {
        return new Object[][] {

	    //  Constant strings and patterns
	    { "helpset.wrongPublicID",
		  "Nezn\u00e1me PublicID {0}"},
	    { "helpset.wrongTitle",
		  "Pokus o nastavenie Title na {0} Title u\u017E m\u00e1 hodnotu {1}."},
	    { "helpset.wrongHomeID",
		  "Pokus o nastavenie homeID na {0} homeID u\u017E m\u00e1 hodnotu {1}."},
	    { "helpset.subHelpSetTrouble",
		  "Probl\u00e9m pri vytv\u00e1ran\u00ed subhelpsetu: {0}."},
	    { "helpset.malformedURL",
		  "Chybn\u00fd form\u00e1t URL: {0}."},
	    { "helpset.incorrectURL",
		  "Nespr\u00e1vne URL: {0}."},
	    { "helpset.wrongText",
		  "{0} nem\u00f4\u017Ee obsahova\u0165 text {1}."},
	    { "helpset.wrongTopLevel",
		  "{0} Nespr\u00e1vny top level tag."},
	    { "helpset.wrongParent",
		  "Rodi\u010Dovsk\u00fd tag {0} nem\u00f4\u017Ee by\u0165 {1}."},
	    { "helpset.unbalanced",
		  "Neukon\u010Den\u00fd tag {0}."},
	    { "helpset.wrongLocale",
		  "Pozor!: xml:lang atrib\u00fat {0} je v konflikte s {1} a s {2}"},
	    { "helpset.unknownVersion",
		  "Nezn\u00e1ma verzia {0}."},

		// IndexView messages
	    { "index.invalidIndexFormat",
		  "Pozor!: Index m\u00e1 chybn\u00fd form\u00e1t"},
	    { "index.unknownVersion",
		  "Nezn\u00e1ma verzia {0}."},

		// TOCView messages
	    { "toc.wrongPublicID",
		  "Nezn\u00e1me PublicID {0}"},
	    { "toc.invalidTOCFormat",
		  "Pozor!: TOC m\u00e1 chybn\u00fd form\u00e1t"},
	    { "toc.unknownVersion",
		  "Nezn\u00e1ma verzia {0}."},
                  
                //FavoritesView messages
	    { "favorites.invalidFavoritesFormat",
		  "Pozor!: Nespr\u00e1vny form\u00e1t s\u00faboru Favorites.xml"},
	    { "favorites.unknownVersion",
		  "Nespr\u00e1vna verzia {0}."},

		// Map messages
	    { "map.wrongPublicID",
		  "Nezn\u00e1me PublicID {0}"},
	    { "map.invalidMapFormat",
		  "Pozor!: Map m\u00e1 nespr\u00e1vny form\u00e1t"},
	    { "map.unknownVersion",
		  "Nezn\u00e1ma verzia {0}."},

	    // GUI components
	    // Labels
	   { "index.findLabel", "Vyh\u013Eada\u0165 : "},

	    { "search.findLabel", "Vyh\u013Eada\u0165 : "},
	    { "search.hitDesc", "Po\u010Det v\u00fdskytov v dokumente"},
	    { "search.qualityDesc", "Miera nepresnosti" },
	    { "search.high", "Najvy\u0161\u0161ia"},
	    { "search.midhigh", "Vysok\u00e1"},
	    { "search.mid", "Stredn\u00e1"},
	    { "search.midlow", "N\u00edzka"},
	    { "search.low", "Najni\u0161\u0161ia"},

            { "favorites.add", "Pridaj"},
            { "favorites.remove", "Zma\u017e"},
            { "favorites.folder", "Zlo\u017eka"},
            { "favorites.name", "N\u00e1zov"},
            { "favorites.cut", "Vystrihni"},
            { "favorites.paste", "Vlo\u017e"},
            { "favorites.copy" ,"Kop\u00edruj" },

            { "history.homePage", "Domovsk\u00e1 str\u00e1nka"},
            { "history.unknownTitle", "<nezn\u00e1my titul str\u00e1nky>"},

	    // ToolTips for Actions
	    { "tooltip.BackAction", "Predch\u00e1dzaj\u00face"},
	    { "tooltip.ForwardAction", "\u010Eal\u0161ie"},
	    { "tooltip.PrintAction", "Tla\u010D"},
	    { "tooltip.PrintSetupAction", "Nastavenie str\u00e1nky"},
	    { "tooltip.ReloadAction", "Obnovi\u0165"},
            { "tooltip.FavoritesAction", "Pridaj k ob\u013E\u00faben\u00fdm polo\u017Ek\u00e1m"},
            { "tooltip.HomeAction", "Zobraz domovsk\u00fa str\u00e1nku"},

	    // Accessibility names
	    { "access.BackAction", "Spiatky"},
	    { "access.ForwardAction", "Vpred"},
	    { "access.HistoryAction", "Hist\u00f3ria"},
	    { "access.PrintAction", "Tla\u010D"},
	    { "access.PrintSetupAction", "Nastavenie Tla\u010De"},
	    { "access.ReloadAction", "Obnovenie"},
            { "access.HomeAction", "Domovsk\u00e1 Str\u00e1nka"},
            { "access.FavoritesAction", "Pridaj k ob\u013E\u00faben\u00fdm polo\u017ek\u00e1m"},
            { "access.contentViewer", "Prehliada\u010D obsahu"}

	};
    
    }

    
}
