/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.documentation;

public class VCellDocTags {
	//XML Tags
	public final static String VCellDoc_tag = "vcelldoc";
	public final static String page_tag = "page";
	public final static String page_title_attr = "title";
	public final static String target_attr = "target";//target for imageReference, pagelinks
	public final static String inline_attr = "inline";//inline for imageReference (true means don't <br>)
	public final static String introduction_tag = "introduction";
	public final static String appearance_tag = "appearance";
	public final static String operations_tag = "operations";
	public final static String properties_tag = "properties";
	public final static String link_tag = "link";
	public final static String img_ref_tag = "imgReference";
	public final static String bold_tag = "bold";
	public final static String paragraph_tag = "para";
	public final static String list_tag = "list";
	public final static String listItem_tag = "item";
	public final static String definition_tag = "def";
	public final static String definition_label_attr = "defLabel";
	
	//HTML tags
	public final static String html_tag = "html";
	public final static String html_head_tag = "head";
	public final static String html_body_tag = "body";
	public final static String html_title_tag = "title";
	public final static String html_new_line = "p";
	public final static String html_header_tag = "h2";
	public final static String html_bold_tag = "b";
	public final static String html_italic_tag = "i";
	public final static String html_list_tag = "ul";
	public final static String html_listItem_tag = "li";
	public final static String html_name_tag = "name";
	
	//for writing Map Toc and HelpSet
	public static final String XMLHeader = "<?xml version='1.0' encoding='ISO-8859-1' ?>";
	public static final String HelpMapHeader = "<!DOCTYPE map\n"+
         "\tPUBLIC \"-//Sun Microsystems Inc.//DTD JavaHelp Map Version 1.0//EN\"\n"+
         "\t\t\"http://java.sun.com/products/javahelp/map_1_0.dtd\">";
	public static final String HelpTOCHeader = "<!DOCTYPE toc\n"+
	         "\tPUBLIC \"-//Sun Microsystems Inc.//DTD JavaHelp TOC Version 1.0//EN\"\n"+
	         "\t\t\"http://java.sun.com/products/javahelp/toc_1_0.dtd\">";
	public static final String HelpSetHeader = "<!DOCTYPE helpset\n"+
	         "\tPUBLIC \"-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN\"\n"+
	         "\t\t\"http://java.sun.com/products/javahelp/helpset_1_0.dtd\">";
	
	public final static String map_tag = "map";
	public final static String version_tag = "version";
	public final static String mapID_tag = "mapID";
	public final static String url_attr = "url";
	
	public final static String toc_tag = "toc";
	public final static String tocitem_tag = "tocitem";
	public final static String text_attr = "text";
	public final static String image_attr = "image";
	
	public final static String helpset_tag = "helpset";
	public final static String title_tag = "title";
	public final static String maps_tag = "maps";
	public final static String mapref_tag = "mapref";
	public final static String location_attr = "location";
	public final static String homeID_tag = "homeID";
	public final static String view_tag = "view";
	public final static String name_tag = "name";
	public final static String label_tag = "label";
	public final static String type_tag = "type";
	public final static String data_tag = "data";
	
	public static void main(String[] args) {
		try {
			System.out.println(HelpSetHeader);
		}catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}
