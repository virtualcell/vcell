package org.vcell.pathway;

import java.util.ArrayList;

public class PublicationXref extends Xref {
	private ArrayList<String> author;
	private ArrayList<String> comment;
	private ArrayList<String> source;
	private String title;
	private ArrayList<String> url;
	private Integer year;
	public ArrayList<String> getAuthor() {
		return author;
	}
	public ArrayList<String> getComment() {
		return comment;
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
	public void setComment(ArrayList<String> comment) {
		this.comment = comment;
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
}
