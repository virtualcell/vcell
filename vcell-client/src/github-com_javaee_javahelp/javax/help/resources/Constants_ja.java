/*
 * @(#)Constants_ja.java	1.10 06/10/30
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
 * @(#) Constants_ja.java 1.10 - last change made 10/30/06
 */

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * \u5b9a\u6570\u306f\u3001JavaHelp \u3092\u30ed\u30fc\u30ab\u30e9\u30a4\u30ba\u3059\u308b\u306e\u306b\u4f7f\u7528\u3055\u308c\u307e\u3059\u3002
 *
 * \u5b9a\u6570\u306f\u3001\u30ad\u30fc\u3001\u5024\u306e\u5f62\u3067\u793a\u3055\u308c\u307e\u3059\u3002
 * \u5024\u3060\u3051\u3092\u7ffb\u8a33\u3057\u307e\u3059\u3002
 */

public class Constants_ja extends ListResourceBundle {
    /**
     * ListResourceBundle \u3092\u4e0a\u66f8\u304d\u3057\u307e\u3059\u3002
     */
    public Object[][] getContents() {
        return new Object[][] {

	    // \u5b9a\u6570\u6587\u5b57\u5217\u3068\u30d1\u30bf\u30fc\u30f3 
	    { "helpset.wrongTitle",
		  "\u30bf\u30a4\u30c8\u30eb\u3092 {0} \u306b\u8a2d\u5b9a\u3057\u3088\u3046\u3068\u3057\u307e\u3057\u305f\u304c\u3001\u3059\u3067\u306b\u5024 {1} \u304c\u5b58\u5728\u3057\u3066\u3044\u307e\u3059\u3002"},
	    { "helpset.wrongHomeID",
		  "homeID \u3092 {0} \u306b\u8a2d\u5b9a\u3057\u3088\u3046\u3068\u3057\u307e\u3057\u305f\u304c\u3001\u3059\u3067\u306b\u5024 {1} \u304c\u5b58\u5728\u3057\u3066\u3044\u307e\u3059\u3002"},
	    { "helpset.subHelpSetTrouble",
		  "\u30b5\u30d6\u30d8\u30eb\u30d7\u30bb\u30c3\u30c8: {0} \u306e\u4f5c\u6210\u306b\u554f\u984c\u304c\u3042\u308a\u307e\u3059\u3002"},
	    { "helpset.malformedURL",
		  "URL: {0} \u306e\u5f62\u5f0f\u304c\u6b63\u3057\u304f\u3042\u308a\u307e\u305b\u3093\u3002"},
	    { "helpset.incorrectURL",
		  "URL: {0} \u306f\u9593\u9055\u3063\u3066\u3044\u307e\u3059\u3002"},
	    { "helpset.wrongText",
		  "{0} \u306b\u306f\u30c6\u30ad\u30b9\u30c8 {1} \u3092\u542b\u3080\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093\u3002"},
	    { "helpset.wrongTopLevel",
		  "{0} \u306f\u6700\u4e0a\u4f4d\u30ec\u30d9\u30eb\u306e\u30bf\u30b0\u306b\u306f\u306a\u308c\u307e\u305b\u3093\u3002"},
	    { "helpset.wrongParent",
		  "{0} \u306e\u89aa\u30bf\u30b0\u306f {1} \u306b\u306f\u306a\u308c\u307e\u305b\u3093\u3002"},
	    { "helpset.unbalanced",
		  "\u30bf\u30b0 {0} \u306f\u9589\u3058\u3066\u3044\u307e\u305b\u3093\u3002"},
	    { "helpset.wrongLocale",
		  "\u8b66\u544a: xml: \u8a00\u8a9e\u5c5e\u6027 {0} \u306f\u3001\u30c7\u30d5\u30a9\u30eb\u30c8\u306e {1} \u304a\u3088\u3073 {2} \u3068\u91cd\u8907\u3057\u307e\u3059\u3002"},

	    { "index.invalidIndexFormat",
		  "\u8b66\u544a: \u7d22\u5f15\u306e\u30d5\u30a9\u30fc\u30de\u30c3\u30c8\u304c\u6b63\u3057\u304f\u3042\u308a\u307e\u305b\u3093\u3002"},

	    { "toc.invalidTOCFormat",
		  "\u8b66\u544a: \u76ee\u6b21\u306e\u30d5\u30a9\u30fc\u30de\u30c3\u30c8\u304c\u304c\u6b63\u3057\u304f\u3042\u308a\u307e\u305b\u3093\u3002"},

	    // GUI \u30b3\u30f3\u30dd\u30fc\u30cd\u30f3\u30c8 
	    // \u30e9\u30d9\u30eb 
	    { "index.findLabel", "\u691c\u7d22 : "},

	    { "search.findLabel", "\u691c\u7d22 : "},
	    { "search.hitDesc", "Number of occurances in document"},
	    { "search.qualityDesc", "Lowest penality value in document" },
	    { "search.high", "High"},
	    { "search.midhigh", "Medium high"},
	    { "search.mid", "Medium"},
	    { "search.midlow", "Medium low"},
	    { "search.low", "Low"},
            
            { "favorites.add", "\u8ffd\u52a0"},
            { "favorites.remove", "\u524a\u9664"},
            { "favorites.folder", "\u65b0\u898f\u30df=u30a9\u30eb\u30c0\u30fc\u306e\u4f5c\u6210"},
            { "favorites.name", "\u540d]u524d"},
            { "favorites.cut", "u5207\u308a\u53d6\u308a"},
            { "favorites.paste", "\u8cbc\u308a\u4ed8\u3051"},
            { "favorites.copy" , "u30b3\u30d4\u30fc"},

	    // ToolTips for Action
	    { "tooltip.BactionAction", "\u524d\u306e\u30da\u30fc\u30b8"},
	    { "tooltip.ForwardAction", "\u6b21\u306e\u30da\u30fc\u30b8"},
	    { "tooltip.PrintAction", "\u5370\u5237"},
	    { "tooltip.PrintSetupAction", "\u30da\u30fc\u30b8\u8a2d\u5b9a"},
	    { "tooltip.ReloadAction", "\u518d\u8aad\u307f\u8fbc\u307f"},
            { "tooltip.FavoritesAction", "\u304a\u6c17\u306b\u5165\u308a\u306b\u8ffd\u52a0"},
            { "tooltip.HomeAction", "\u30db\u30fc\u30e0\u30da\u30fc\u30b8\u3078\u79fb\u52d5"},

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
