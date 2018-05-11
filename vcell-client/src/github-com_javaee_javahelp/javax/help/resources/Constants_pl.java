/*
 * @(#)Constants_pl.java	1.2 06/10/30
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
 * 
 * Constants used for localizing JavaHelp.
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values.
 * 
 * @author Krystian Nowak
 * @author krystian (at) man (dot) poznan (dot) pl
 * 
 */

//
// Polish diacritical characters in Unicode
//
// A - 0104  a - 0105
// C - 0106  c - 0107
// E - 0118  e - 0119
// L - 0141  l - 0142
// N - 0143  n - 0144
// O - 00D3  o - 00F3
// S - 015A  s - 015B
// X - 0179  x - 017A
// Z - 017B  z - 017C

public class Constants_pl extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle.
     */
    public Object[][] getContents() {
        return new Object[][] {

	    //  Constant strings and patterns
	    { "helpset.wrongPublicID",
		  "Nieznane PublicID {0}"},
	    { "helpset.wrongTitle",
		  "Pr\u00F3ba ustawienia tytu\u0142u na {0} mimo, \u017Ce ma ju \u017C warto\u015B\u0107 {1}."},
	    { "helpset.wrongHomeID",
		  "Pr\u00F3ba ustawienia homeID na {0} mimo, \u017Ce ma ju \u017C warto\u015B\u0107 {1}."},
	    { "helpset.subHelpSetTrouble",
		  "B\u0142\u0105d w trakcie tworzenia podpomocy: {0}."},
	    { "helpset.malformedURL",
		  "\u0179le sformu\u0142owany URL: {0}."},
	    { "helpset.incorrectURL",
		  "Niepoprawny URL: {0}."},
	    { "helpset.wrongText",
		  "{0} nie mo\u017Ce zawiera\u0107 tekstu {1}."},
	    { "helpset.wrongTopLevel",
		  "{0} nie mo\u017Ce by\u0107 znacznikiem najwy\u017Cszego poziomu."},
	    { "helpset.wrongParent",
		  "{1} nie mo\u017Ce by\u0107 nadznacznikiem dla {0}."},
	    { "helpset.unbalanced",
		  "Niezbalansowany znacznik {0}."},
	    { "helpset.wrongLocale",
		  "Uwaga: atrybut xml:lang {0} jest w konflikcie z domy\u015Blnym {1} i domy\u015Blnym {2}"},
	    { "helpset.unknownVersion",
		  "Nieznana wersja {0}."},

		// IndexView messages
	    { "index.invalidIndexFormat",
		  "Uwaga: Index ma nieprawid\u0142owy format"},
	    { "index.unknownVersion",
		  "Nieznana wersja {0}."},

		// TOCView messages
	    { "toc.wrongPublicID",
		  "Nieznany PublicID {0}"},
	    { "toc.invalidTOCFormat",
		  "Uwaga: TOC ma nieprawid\u0142owy format"},
	    { "toc.unknownVersion",
		  "Nieznana wersja {0}."},
                  
            // FavoritesView messages
	    { "favorites.invalidFavoritesFormat",
		  "Uwaga: Plik Favorites.xml ma nieprawid\u0142owy format"},
	    { "favorites.unknownVersion",
		  "Nieznana wersja {0}."},

		// Map messages
	    { "map.wrongPublicID",
		  "Nieznane PublicID {0}"},
	    { "map.invalidMapFormat",
		  "Uwaga: Map ma nieprawid\u0142owy format"},
	    { "map.unknownVersion",
		  "Nieznana wersja {0}."},


	    // GUI components
	    // Labels
	    { "index.findLabel", "Znajd\u017A: "},
            
	    { "search.findLabel", "Znajd\u017A: "},
	    { "search.hitDesc", "Liczba wyst\u0105pie\u0144 w dokumencie"},
	    { "search.qualityDesc", "Miara niedok\u0142adno\u015Bci" },
	    { "search.high", "Najwy\u017Csza"},
	    { "search.midhigh", "Wysoka"},
	    { "search.mid", "\u015Arednia"},
	    { "search.midlow", "Niska"},
	    { "search.low", "Najni\u017Csza"},
            
            { "favorites.add", "Dodaj"},
            { "favorites.remove", "Usu\u0144"},
            { "favorites.folder", "Nowy folder"},
            { "favorites.name", "Nazwa"},
            { "favorites.cut", "Wytnij"},
            { "favorites.paste", "Wklej"},
            { "favorites.copy" , "Kopiuj"},

            { "history.homePage", "Strona domowa"},
            { "history.unknownTitle", "<nieznany tytu\u0142 strony>"},

            // ToolTips for Actions
            { "tooltip.BackAction", "Poprzednia strona"},
	    { "tooltip.ForwardAction", "Nast\u0119pna strona"},
	    { "tooltip.PrintAction", "Drukuj"},
	    { "tooltip.PrintSetupAction", "Ustawienia strony"},
	    { "tooltip.ReloadAction", "Prze\u0142aduj"},
            { "tooltip.FavoritesAction", "Dodaj do ulubionych"},
            { "tooltip.HomeAction", "Id\u017A do strony domowej"},

	    // Accessibility names
	    { "access.BackAction", "Do ty\u0142u"},
	    { "access.ForwardAction", "Nast\u0119pny"},
	    { "access.HistoryAction", "Historia"},
	    { "access.PrintAction", "Drukuj"},
	    { "access.PrintSetupAction", "Ustawienia stron"},
	    { "access.ReloadAction", "Prze\u0142aduj"},
            { "access.HomeAction", "Strona domowa"},
            { "access.FavoritesAction", "Dodaj do ulubionych"},
            { "access.contentViewer", "Przegl\u0105darka zawarto\u015Bci"}

       };
    }
}








