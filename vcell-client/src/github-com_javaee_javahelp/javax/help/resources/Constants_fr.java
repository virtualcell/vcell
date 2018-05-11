/*
 * @(#)Constants_fr.java	1.10 06/10/30
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
 * @(#) Constants.java 1.11 - last change made 08/25/99
 */

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * Constants used for localizing JavaHelp.
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values.
 */

public class Constants_fr extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle.
     */
    public Object[][] getContents() {
        return new Object[][] {

	    //  Constant strings and patterns
	    { "helpset.wrongPublicID",
	          "ID Publique inconnue {0}"},
	    { "helpset.wrongTitle",
	          "Essai de r\u00e9glage du titre sur {0} mais valeur {1} existe d\u00e9j\u00e0."},
	    { "helpset.wrongHomeID",
	          "Essai de r\u00e9glage de l''ID domestique sur {0} mais valeur {1} existe d\u00e9j\u00e0."},
	    { "helpset.subHelpSetTrouble",
	          "Probl\u00e8me lors de la cr\u00e9ation de l''ensemble de sous-aide : {0}."},
	    { "helpset.malformedURL",
	          "URL mal form\u00e9 : {0}."},
	    { "helpset.incorrectURL", 
	          "URL incorrect : {0}."},
	    { "helpset.wrongText",
	          "{0} ne peut contenir de texte {1}."},
	    { "helpset.wrongTopLevel",
	          "{0} ne peut pas \u00eatre une \u00e9tiquette de haut niveau."},
	    { "helpset.wrongParent",
	          "L''\u00e9tiquette parent pour {0} ne peut pas \u00eatre {1}."},
	    { "helpset.unbalanced",
	          "\u00c9tiquette non \u00e9quilibr\u00e9e {0}."},
	    { "helpset.wrongLocale",
	          "Attention : xml:lang attribue {0} des conflits avec d\u00e9faut {1} et avec d\u00e9faut {2}"},
	    { "helpset.unknownVersion",
	          "Version inconnue {0}."},

		// IndexView messages
	    { "index.invalidIndexFormat",
	          "Attention : format d'Index invalide"},
	    { "index.unknownVersion",
	          "Version inconnue {0}."},

		// TOCView messages
	    { "toc.wrongPublicID",
	          "ID Publique inconnue {0}"},
	    { "toc.invalidTOCFormat",
	          "Attention : Format TOC invalide"},
	    { "toc.unknownVersion",
	          "Version inconnue {0}."},

            // FavoritesView messages
	    { "favorites.invalidFavoritesFormat",
	          "Attention : Format Favorites invalide"},
	    { "favorites.unknownVersion",
	          "Version inconnue {0}."},

		// Map messages
	    { "map.wrongPublicID",
	          "ID Publique inconnue {0}"},
	    { "map.invalidMapFormat",
	          "Attention : Format Map inconnu"},
	    { "map.unknownVersion",
	          "Version inconnue {0}."},

	    // GUI components
	    // Labels
	    { "index.findLabel", "Trouver : "},

	    { "search.findLabel", "Trouver : "},
	    { "search.hitDesc", "Nombre d'apparitions dans le document"},
	    { "search.qualityDesc", "Valeur de p\u00e9nalit\u00e9 la plus basse du document"
},
	    { "search.high", "Haut"},
	    { "search.midhigh", "Moyen haut"},
	    { "search.mid", "Moyen"},
	    { "search.midlow", "Moyen bas"},
	    { "search.low", "Bas"},

            { "favorites.add", "Ajouter"},
            { "favorites.remove", "Supprimer"},
            { "favorites.folder", "Nouveau dossier"},
            { "favorites.name", "Nom"},
            { "favorites.cut", "Couper"},
            { "favorites.paste", "Coller"},
            { "favorites.copy" , "Copier"},

            { "history.homePage", "Page de d\u00e9marrage"},
            { "history.unknownTitle", "Page inconnue>"},

	    // ToolTips for Actions
	    { "tooltip.BackAction", "Pr\u00e9c\u00e9dent"},
	    { "tooltip.ForwardAction", "Suivant"},
	    { "tooltip.PrintAction", "Imprimer"},
	    { "tooltip.PrintSetupAction", "Mise en page"},
	    { "tooltip.ReloadAction", "Recharger"},
            { "tooltip.HomeAction", "Vers page ded\u00e9marrage"},

	    // Accessibility names
	    { "access.BackAction", "Bouton Pr\00e9c\00e9dent"},
	    { "access.ForwardAction", "Bouton Suivant"},
	    { "access.HistoryAction", "Bouton Historique"},
	    { "access.PrintAction", "Bouton Impression"},
	    { "access.PrintSetupAction", "Bouton Mise en page"},
	    { "access.ReloadAction", " Bouton Actualiser"},
            { "access.HomeAction", "Bouton Page de d\u00e9marrage"},
            { "access.FavoritesAction", "Bouton Ajouter aux favoris"},
            { "access.contentViewer", "Visualiser le contenu"}
       };
    }
}
