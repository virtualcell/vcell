/*
 * @(#)Constants_de.java	1.8 06/10/30
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
 * Constants_de.java 
 *
 * Originaly: Constants.java 1.8 - last change made 01/29/99
 * Translated to German by iXOS Software AG, 03/03/1999, Martin Balin
 */

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * Constants used for localizing JavaHelp
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values.
 */

public class Constants_de extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
	    //  Constant strings and patterns
	    { "helpset.wrongPublicID",
		  "Unbekannte PublicID {0}"},
	    { "helpset.wrongTitle",
		  "Versuche, Titel auf {0} zu setzen, aber Wert {1} ist schon gesetzt."},
	    { "helpset.wrongHomeID",
		  "Versuche HomeID auf {0} zu setzen, aber Wert {1} ist schon gesetzt."},
	    { "helpset.subHelpSetTrouble",
		  "Probleme beim Erzeugen des Subhelpset: {0}."},
	    { "helpset.malformedURL",
		  "Formfehler in URL: {0}."},
	    { "helpset.incorrectURL",
		  "Fehlerhafte URL: {0}."},
	    { "helpset.wrongText",
		  "{0} darf nicht den Text {1} enthalten."},
	    { "helpset.wrongTopLevel",
		  "{0} darf kein Top Level Tag sein."},
	    { "helpset.wrongParent",
		  "Parent Tag f\u00fcr {0} darf nicht {1} sein."},
	    { "helpset.unbalanced",
		  "Einseitiger Tag {0}."},
	    { "helpset.wrongLocale",
		  "Warning: xml:lang-Attribut {0} widerspricht Voreinstellung {1} und Voreinstellung {2}"},
	    { "helpset.unknownVersion",
		  "Unbekannte Version {0}."},

		// IndexView messages
	    { "index.invalidIndexFormat",
		  "Warnung: Ung\u00fcltiges Index-Format"},
	    { "index.unknownVersion",
		  "Unbekannte Version {0}."},

		// TOCView messages
	    { "toc.wrongPublicID",
		  "Unbekannte PublicID {0}"},
	    { "toc.invalidTOCFormat",
		  "Warnung: Ung\u00fcltiges Format f\u00fcr Inhaltsverzeichnis"},
	    { "toc.unknownVersion",
		  "Unbekannte Version {0}."},

            // FavoritesView messages
	    { "favorites.invalidFavoritesFormat",
		  "Warnung: Ung\u00fcltiges Favorites-Format"},
	    { "favorites.unknownVersion",
		  "Unbekannte Version {0}."},

		// Map messages
	    { "map.wrongPublicID",
		  "Unbekannte PublicID {0}"},
	    { "map.invalidMapFormat",
		  "Warnung: Ung\u00fcltiges Map-Format"},
	    { "map.unknownVersion",
		  "Unbekannte Version {0}."},


	    // GUI components
	    // Labels
	    { "index.findLabel", "Suche: "},

	    { "search.findLabel", "Suche: "},
	    { "search.hitDesc", "Number of occurances in document"},
	    { "search.qualityDesc", "Lowest penality value in document" },
	    { "search.high", "High"},
	    { "search.midhigh", "Medium high"},
	    { "search.mid", "Medium"},
	    { "search.midlow", "Medium low"},
	    { "search.low", "Low"},
            
            { "favorites.add", "Hinzuf\u00fcgen"},
            { "favorites.remove", "Entfernem"},
            { "favorites.folder", "Neuer Ordner"},
            { "favorites.name", "Name"},
            { "favorites.cut", "Ausschneiden"},
            { "favorites.paste", "Einf\u00fcgen"},
            { "favorites.copy" , "Kopieren"},

            { "history.homePage", "Startseite"},
            { "history.unknownTitle", "<Unbekannter Titel>"},

	    // ToolTips
	    { "tooltip.BackAction", "Voriger"},
	    { "tooltip.ForwardAction", "N\u00e4chster"},
	    { "tooltip.PrintAction", "Drucken"},
	    { "tooltip.PrintSetupAction", "Seite einrichten"},
	    { "tooltip.ReloadAction", "Neu laden"},
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
