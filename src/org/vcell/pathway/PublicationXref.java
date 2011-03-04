package org.vcell.pathway;

import java.util.ArrayList;

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

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printStrings(sb, "author",author,level);
		printStrings(sb, "source",source,level);
		printString(sb, "title",title,level);
		printStrings(sb, "url",url,level);
		printInteger(sb, "year",year,level);
	}

}
