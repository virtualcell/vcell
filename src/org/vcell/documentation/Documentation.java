/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.documentation;

import java.io.File;
import java.util.ArrayList;

public class Documentation {
	private ArrayList<DocumentPage> documentPages = new ArrayList<DocumentPage>();
	private ArrayList<DocumentImage> documentImages = new ArrayList<DocumentImage>();
	
	public DocumentPage getDocumentPage(DocLink docLink) {
		for (DocumentPage docPage : documentPages){
			if (docPage.getTarget().equals(docLink.getTarget())){
				return docPage;
			}
		}
		return null;
	}

	public DocumentImage getDocumentImage(DocImageReference docImageReference) {
		for (DocumentImage docImage : documentImages){
			if (docImage.getSourceFile().getName().equals(docImageReference.getImageTarget())){
				return docImage;
			}
		}
		return null;
	}

	public void add(DocumentPage documentPage) {
		if (!documentPages.contains(documentPage)){
			documentPages.add(documentPage);
		}
	}

	public void add(DocumentImage documentImg) {
		if (!documentImages.contains(documentImg)){
			documentImages.add(documentImg);
		}
	}
	
	public DocumentPage[] getDocumentPages(){
		return documentPages.toArray(new DocumentPage[documentPages.size()]);
	}
}
