package org.vcell.pub;

import java.io.Serializable;
import java.util.Arrays;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;

public class Publication implements Serializable {
	public final KeyValue key;
	public final String title;
	public final String[] authors;
	public final String citation;
	public final String pubmedid;
	public final String doi;
	public final String url;
	
	public Publication(KeyValue key, String title, String[] authors, String citation, String pubmedid, String doi, String url) {
		super();
		this.key = key;
		this.title = title;
		this.authors = authors;
		this.citation = citation;
		this.pubmedid = pubmedid;
		this.doi = doi;
		this.url = url;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof Publication){
			Publication other = (Publication)obj;
			if (!Compare.isEqualOrNull(key, other.key)){
				return false;
			}
			if (!Compare.isEqual(title,other.title)){
				return false;
			}
			if (!Compare.isEqual(authors,other.authors)){
				return false;
			}
			if (!Compare.isEqual(citation,other.citation)){
				return false;
			}
			if (!Compare.isEqualOrNull(pubmedid,other.pubmedid)){
				return false;
			}
			if (!Compare.isEqualOrNull(doi,other.doi)){
				return false;
			}
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * doesn't have to be unique
	 */
	@Override
	public int hashCode(){
		if (key!=null){
			return key.toString().hashCode();
		}else{
			return citation.hashCode();
		}
	}

	@Override
	public String toString() {
		return "Publication [key=" + key + ", title=" + title + ", authors="
				+ Arrays.toString(authors) + ", citation=" + citation
				+ ", pubmedid=" + pubmedid + ", doi=" + doi + "]";
	}
	
}