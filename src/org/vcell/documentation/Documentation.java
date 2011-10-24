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
import java.util.HashSet;

public class Documentation {
	private ArrayList<DocumentPage> documentPages = new ArrayList<DocumentPage>();
	private ArrayList<DocumentImage> documentImages = new ArrayList<DocumentImage>();
	private ArrayList<DocumentDefinition> documentDefinitions = new ArrayList<DocumentDefinition>();
	private HashSet<Object> referencedTargets = new HashSet<Object>();
	
	public DocumentPage getDocumentPage(DocLink docLink) {
		for (DocumentPage docPage : documentPages){
			if (docPage.getTarget().equals(docLink.getTarget())){
				referencedTargets.add(docPage);
				return docPage;
			}
		}
		return null;
	}

	public DocumentImage getDocumentImage(DocImageReference docImageReference) {
		for (DocumentImage docImage : documentImages){
			if (docImage.getSourceFile().getName().equals(docImageReference.getImageTarget())){
				referencedTargets.add(docImage);
				return docImage;
			}
		}
		return null;
	}

	public DocumentDefinition getDocumentDefinition(DocDefinitionReference docDefReference) {
		for (DocumentDefinition docDef : documentDefinitions){
			if (docDef.getTarget().equals(docDefReference.getDefinitionTarget())){
				referencedTargets.add(docDef);
				return docDef;
			}
		}
		return null;
	}
	
	public boolean isReferenced(DocumentPage docPage){
		return referencedTargets.contains(docPage);
	}
	
	public boolean isReferenced(DocumentImage docImage){
		return referencedTargets.contains(docImage);
	}
	
	public boolean isReferenced(DocumentDefinition docDefinition){
		return referencedTargets.contains(docDefinition);
	}
	
	public DocumentDefinition[] getDocumentDefinitions() {
		return documentDefinitions.toArray(new DocumentDefinition[documentDefinitions.size()]);
	} 
	
	public void add(DocumentPage documentPage) {
		if (!documentPages.contains(documentPage)){
			DocumentPage existingDocumentPage = getDocumentPage(new DocLink(documentPage.getTarget(),documentPage.getTarget()));
			if (existingDocumentPage==null){
				documentPages.add(documentPage);
			}else{
				throw new RuntimeException("document pages "
							+existingDocumentPage.getTemplateFile().getPath()+" and "
							+documentPage.getTemplateFile().getPath()
							+" have same target = '"+documentPage.getTarget());
			}
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
