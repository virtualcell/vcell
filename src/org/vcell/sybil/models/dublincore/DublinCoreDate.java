package org.vcell.sybil.models.dublincore;

public class DublinCoreDate {
	private String dateString = null;
	
	public DublinCoreDate(String argDateString){
		this.dateString = argDateString;
	}
	
	public String getDateString(){
		return dateString;
	}
}
