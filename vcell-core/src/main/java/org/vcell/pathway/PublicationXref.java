/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class PublicationXref extends Xref {
	private ArrayList<String> author = new ArrayList<String>();
	private ArrayList<String> source = new ArrayList<String>();
	private String title;
	private ArrayList<String> url = new ArrayList<String>();
	private Integer year;
	
	public ArrayList<String> getAuthor() {
		return author;
	}
	public ArrayList<String> getSource() {
		return source;
	}
	public String getTitle() {
		return title;
	}
	public ArrayList<String> getUrl() {
		return url;
	}
	public Integer getYear() {
		return year;
	}
	public void setAuthor(ArrayList<String> author) {
		this.author = author;
	}
	public void setSource(ArrayList<String> source) {
		this.source = source;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setUrl(ArrayList<String> url) {
		this.url = url;
	}
	public void setYear(Integer year) {
		this.year = year;
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printStrings(sb, "author",author,level);
		printStrings(sb, "source",source,level);
		printString(sb, "title",title,level);
		printStrings(sb, "url",url,level);
		printInteger(sb, "year",year,level);
	}

}
