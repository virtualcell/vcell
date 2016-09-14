package cbit.vcell.modeldb;

import java.util.ArrayList;

import org.vcell.util.document.KeyValue;

public class PublicationRep {
	private final KeyValue pubKey;
	private final String title;
	private final String[] authors;
	private final Integer year;
	private final String citation;
	private final String pubmedid;
	private final String doi;
	private final String endnoteid;
	private final String url;
	private final KeyValue[] biomodelKeyList;
	private ArrayList<BioModelRep> biomodelRepList = new ArrayList<BioModelRep>();
	
	public PublicationRep(KeyValue pubKey, String title, String[] authors, Integer year, String citation, String pubmedid, String doi, String endnoteid,
			String url, KeyValue[] biomodelKeyList) {
		
		super();
		this.pubKey = pubKey;
		this.title = title;
		this.authors = authors;
		this.year = year;
		this.citation = citation;
		this.pubmedid = pubmedid;
		this.doi = doi;
		this.endnoteid = endnoteid;
		this.url = url;
		this.biomodelKeyList = biomodelKeyList;
	}

	public KeyValue getPubKey() {
		return pubKey;
	}

	public String getTitle() {
		return title;
	}

	public String[] getAuthors() {
		return authors;
	}

	public Integer getYear() {
		return year;
	}

	public String getCitation() {
		return citation;
	}

	public String getPubmedid() {
		return pubmedid;
	}

	public String getDoi() {
		return doi;
	}

	public String getEndnoteid() {
		return endnoteid;
	}

	public String getUrl() {
		return url;
	}

	public KeyValue[] getBiomodelKeyList() {
		return biomodelKeyList;
	}


	public BioModelRep[] getBiomodelRepList() {
		return biomodelRepList.toArray(new BioModelRep[biomodelRepList.size()]);
	}

	public void addBioModelRep(BioModelRep bmRep) {
		if (!biomodelRepList.contains(bmRep)){
			biomodelRepList.add(bmRep);
		}
	}
	
}
