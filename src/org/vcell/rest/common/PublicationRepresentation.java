package org.vcell.rest.common;

import java.util.ArrayList;

import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.modeldb.PublicationRep;
import cbit.vcell.parser.ExpressionException;

public class PublicationRepresentation {
	public String pubKey;
	public String title;
	public String[] authors;
	public Integer year;
	public String citation;
	public String pubmedid;
	public String doi;
	public String endnoteid;
	public String url;
	public BiomodelRepresentation[] biomodels;

	public PublicationRepresentation(){
		
	}	
	

	public String getPubKey() {
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

	public BiomodelRepresentation[] getBiomodels() {
		return biomodels;
	}



	public PublicationRepresentation(PublicationRep publicationRep) throws ExpressionException{
		this.pubKey = publicationRep.getPubKey().toString();
		this.title = publicationRep.getTitle();
		this.authors = publicationRep.getAuthors();
		this.year = publicationRep.getYear();
		this.citation = publicationRep.getCitation();
		this.pubmedid = publicationRep.getPubmedid();
		this.doi = publicationRep.getDoi();
		this.endnoteid = publicationRep.getEndnoteid();
		this.url = publicationRep.getUrl();

		ArrayList<BiomodelRepresentation> biomodelList = new ArrayList<BiomodelRepresentation>();
		for (BioModelRep bmRep : publicationRep.getBiomodelRepList()){
			biomodelList.add(new BiomodelRepresentation(bmRep));
		}
		this.biomodels = biomodelList.toArray(new BiomodelRepresentation[biomodelList.size()]);
	}
}
