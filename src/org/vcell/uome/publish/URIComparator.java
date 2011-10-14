package org.vcell.uome.publish;

import java.util.Comparator;

import org.openrdf.model.URI;

public class URIComparator implements Comparator<URI> {

	public int compare(URI uri0, URI uri1) {
		return uri0.toString().compareTo(uri1.toString());
	}

}
