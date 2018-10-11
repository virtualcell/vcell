package org.vcell.util.document;

import java.io.Serializable;

import org.vcell.util.document.VCDocument.VCDocumentType;

public class PublicationInfo implements Serializable{
	private KeyValue versionKey;
	private String title;
	private String[] authors;
	private String citation;
	private String pubmedid;
	private String doi;
	private String url;
	private VCDocumentType vcDocumentType;
	private User user;
	private int theHashCode;
	public PublicationInfo(KeyValue versionKey, String title, String[] authors, String citation, String pubmedid,
			String doi, String url, VCDocumentType vcDocumentType, User user) {
		super();
		this.versionKey = versionKey;
		this.title = title;
		this.authors = authors;
		this.citation = citation;
		this.pubmedid = pubmedid;
		this.doi = doi;
		this.url = url;
		this.vcDocumentType = vcDocumentType;
		this.user = user;
		theHashCode = (versionKey.toString()+(doi!=null && doi.length()>0?doi.toString():(pubmedid!=null && pubmedid.length()>0&& !pubmedid.equals("0")?pubmedid.toString():""))).hashCode();
	}
	public KeyValue getVersionKey() {
		return versionKey;
	}
	public String getTitle() {
		return title;
	}
	public String[] getAuthors() {
		return authors;
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
	public String getUrl() {
		return url;
	}
	public VCDocumentType getVcDocumentType() {
		return vcDocumentType;
	}
	public User getUser() {
		return user;
	}
	public int hashCode() {
		return theHashCode;
	}
}