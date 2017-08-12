package org.vcell.api.client.query;

public class BioModelsQuerySpec {
	public String bmName="";
	public String bmId="";
	public String category="all";
	public String owner="";
	public String savedLow="";
	public String savedHigh="";
	public String startRow="";
	public String maxRows="10";
	public String orderBy="date_desc";
	
	public String getQueryString(){
		return "bmName="+bmName+"&"+
				"bmId="+bmId+"&"+
				"category="+category+"&"+
				"owner="+owner+"&"+
				"savedLow="+savedLow+"&"+
				"savedHigh="+savedHigh+"&"+
				"startRow="+startRow+"&"+
				"maxRows="+maxRows+"&"+
				"orderBy="+orderBy;
				
	}
}
