/*
 * @(#)Constants_es.java	1.3 06/10/30
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
 * @(#) Constants_es.java 1.3 - last change made 10/30/06
 */

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * Constants used for localizing JavaHelp.
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values.
 */

public class Constants_es extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle.
     */
    public Object[][] getContents() {
        return new Object[][] {

	    //  Constant strings and patterns
	    { "helpset.wrongPublicID",
		  "PublicID {0} desconocido"},
	    { "helpset.wrongTitle",
		  "Intentando asignar el t\u00EDtulo a {0} pero ya tiene el valor {1}."},
	    { "helpset.wrongHomeID",
		  "Intentando asignar homeID a {0} pero ya tiene el valor {1}."},
	    { "helpset.subHelpSetTrouble",
		  "Problemas creando subhelpset: {0}."},
	    { "helpset.malformedURL",
		  "URL mal formada: {0}."},
	    { "helpset.incorrectURL",
		  "URL incorrecta: {0}."},
	    { "helpset.wrongText",
		  "{0} no puede contener el texto {1}."},
	    { "helpset.wrongTopLevel",
		  "{0} no puede ser una etiqueta del nivel superior."},
	    { "helpset.wrongParent",
		  "La etiqueta padre de {0} no puede ser {1}."},
	    { "helpset.unbalanced",
		  "Etiqueta sin cerrar {0}."},
	    { "helpset.wrongLocale",
  "Aviso: atributo xml:lang {0} est\u00E1 en conflicto con el por defecto{1} y con {2}"},
	    { "helpset.unknownVersion",
		  "Versi\u00F3n desconocida {0}."},

	    { "index.invalidIndexFormat",
		  "Aviso: Formato de \u00EDndice incorrecto"},
	    { "index.unknownVersion",
		  "Versi\u00F3n desconocida {0}."},

		// TOCView messages
	    { "toc.wrongPublicID",
		  "PublicID {0} desconocido"},
	    { "toc.invalidTOCFormat",
		  "Aviso: Formato de TOC incorrecto"},
	    { "toc.unknownVersion",
		  "Versi\u00F3n desconocida {0}."},

            // FavoritesView messages
	    { "favorites.invalidFavoritesFormat",
		  "Aviso: Formato de Favoritos incorrecto"},
	    { "favorites.unknownVersion",
		  "Versi\u00F3n desconocida {0}."},

		// Map messages
	    { "map.wrongPublicID",
		  "PublicID {0} desconocido"},
	    { "map.invalidMapFormat",
		  "Aviso: Formato de Map incorrecto"},
	    { "map.unknownVersion",
		  "Versi\u00F3n desconocida {0}."},

	    // GUI components
	    // Labels
	    { "index.findLabel", "Buscar: "},
	    { "search.findLabel", "Buscar: "},
	    { "search.hitDesc",
		  "N\u00FAmero de apariciones en el documento"},
	    { "search.qualityDesc",
		  "El m\u00E1s bajo valor de penalizaci\u00F3n en el documento" },
	    { "search.high", "Alto"},
	    { "search.midhigh", "Medio alto"},
	    { "search.mid", "Medio"},
	    { "search.midlow", "Medio bajo"},
	    { "search.low", "Bajo"},

            { "favorites.add", "Anadir"},
            { "favorites.remove", "Quitar"},
            { "favorites.folder", "Nueva carpeta"},
            { "favorites.name", "Nombre"},
            { "favorites.cut", "Cortar"},
            { "favorites.paste", "Pegar"},
            { "favorites.copy", "Copiar"},

            { "history.homePage", "P\u00E1gina de inicio"},
            { "history.unknownTitle",
		  "<T\u00EDtulo de p\u00E1gina desconocido>"},

            // ToolTips for Actions
            { "tooltip.BackAction", "P\u00E1gina anterior"},
	    { "tooltip.ForwardAction", "P\u00E1gina siguiente"},
	    { "tooltip.PrintAction", "Imprimir"},
	    { "tooltip.PrintSetupAction",
		  "Configuraci\u00F3n de p\u00E1gina"},
	    { "tooltip.ReloadAction", "Recargar"},
            { "tooltip.FavoritesAction", "Anadir a favoritos"},
            { "tooltip.HomeAction", "Ir a la p\u00E1gina de inicio"},

	    // Accessibility names
	    { "access.BackAction", "Bot\u00F3n de anterior"},
	    { "access.ForwardAction", "Bot\u00F3n de siguiente"},
	    { "access.HistoryAction", "Bot\u00F3n de historia"},
	    { "access.PrintAction", "Bot\u00F3n de imprimir"},
	    { "access.PrintSetupAction",
		  "Bot\u00F3n de configuraci\u00F3n de p\u00E1gina"},
	    { "access.ReloadAction", "Bot\u00F3n de recarga"},
            { "access.HomeAction", "Bot\u00F3n de inicio"},
            { "access.FavoritesAction", "Bot\u00F3n de anadir a favoritos"},
            { "access.contentViewer", "Visualizador del contenido"}

       };
    }
}

