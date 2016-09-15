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
	private BioModelReferenceRep[] bioModelReferenceReps = new BioModelReferenceRep[0];
	private MathModelReferenceRep[] mathModelReferenceReps = new MathModelReferenceRep[0];
	private final String wittid;
	
	public PublicationRep(KeyValue pubKey, String title, String[] authors, Integer year, String citation, String pubmedid, String doi, String endnoteid,
			String url, BioModelReferenceRep[] bioModelReferenceReps, MathModelReferenceRep[] mathModelReferenceReps, String wittid) {
		
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
		this.bioModelReferenceReps = bioModelReferenceReps;
		this.mathModelReferenceReps = mathModelReferenceReps;
		this.wittid = wittid;
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

	public BioModelReferenceRep[] getBiomodelReferenceReps() {
		return bioModelReferenceReps;
	}

	public MathModelReferenceRep[] getMathmodelReferenceReps() {
		return mathModelReferenceReps;
	}

	public String getWittid() {
		return wittid;
	}
	
}
