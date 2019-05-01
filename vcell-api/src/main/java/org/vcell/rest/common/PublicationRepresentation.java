package org.vcell.rest.common;

import java.util.Date;

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
	public String wittid;
	public BiomodelReferenceRepresentation[] biomodelReferences;
	public MathmodelReferenceRepresentation[] mathmodelReferences;
	public Date date;
	public boolean bcurated;
	
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

	public BiomodelReferenceRepresentation[] getBiomodelReferences() {
		return biomodelReferences;
	}

	public MathmodelReferenceRepresentation[] getMathmodelReferences() {
		return mathmodelReferences;
	}

	public String getWittid() {
		return wittid;
	}

	public Date getDate() {
		return date;
	}
	public boolean isCurated() {
		return bcurated;
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
		this.wittid = publicationRep.getWittid();
		this.date = publicationRep.getDate();
		this.bcurated = publicationRep.isCurated();
		
		this.biomodelReferences = new BiomodelReferenceRepresentation[publicationRep.getBiomodelReferenceReps().length];
		for (int i=0;i<biomodelReferences.length;i++){
			this.biomodelReferences[i] = new BiomodelReferenceRepresentation(publicationRep.getBiomodelReferenceReps()[i]);
		}

		this.mathmodelReferences = new MathmodelReferenceRepresentation[publicationRep.getMathmodelReferenceReps().length];
		for (int i=0;i<mathmodelReferences.length;i++){
			this.mathmodelReferences[i] = new MathmodelReferenceRepresentation(publicationRep.getMathmodelReferenceReps()[i]);
		}
	}
}
