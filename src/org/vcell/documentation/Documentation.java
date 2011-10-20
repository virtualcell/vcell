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

import java.util.ArrayList;

public class Documentation {
	private ArrayList<DocumentPage> documentPages = new ArrayList<DocumentPage>();
	private ArrayList<DocumentImage> documentImages = new ArrayList<DocumentImage>();
	private ArrayList<DocumentDefinition> documentDefinitions = new ArrayList<DocumentDefinition>();
	
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

	public DocumentDefinition getDocumentDefinition(DocDefinitionReference docDefReference) {
		for (DocumentDefinition docDef : documentDefinitions){
			if (docDef.getTarget().equals(docDefReference.getDefinitionTarget())){
				return docDef;
			}
		}
		return null;
	}
	
	public DocumentDefinition[] getDocumentDefinitions() {
		return documentDefinitions.toArray(new DocumentDefinition[documentDefinitions.size()]);
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
	
	public void add(DocumentDefinition documentDef) {
		if (!documentDefinitions.contains(documentDef)){
			documentDefinitions.add(documentDef);
		}
	}
	
	public void add(ArrayList<DocumentDefinition> documentDefs)
	{
		documentDefinitions = documentDefs;
	}
	
	public DocumentPage[] getDocumentPages(){
		return documentPages.toArray(new DocumentPage[documentPages.size()]);
	}
}
