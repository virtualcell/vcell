package org.vcell.documentation;

import java.io.File;
import java.util.ArrayList;

public class Documentation {
	private ArrayList<DocumentPage> documentPages = new ArrayList<DocumentPage>();
	private ArrayList<DocumentImage> documentImages = new ArrayList<DocumentImage>();
	
	public File getTargetFilename(DocumentPage documentPage){
		String xmlFileName = documentPage.getTemplateFilename();
		String xmlFileDir = documentPage.getTemplateFileDir();
		int extIdx = xmlFileName.indexOf(".");
		String baseFileName = xmlFileName.substring(0, extIdx);
		String htmlFilename = ".." + System.getProperty("file.separator") + xmlFileDir + System.getProperty("file.separator") + baseFileName + DocumentCompiler.VCELL_DOC_HTML_FILE_EXT;
		return new File(htmlFilename);
	}	
	
	public File getTargetFilename(DocumentImage documentImage) {
		String sourceFileDir = documentImage.getSourceFileDir();
		String imgFilename = ".." + System.getProperty("file.separator") + sourceFileDir + System.getProperty("file.separator") + documentImage.getSourceFile();
		return new File(imgFilename);
	}
	
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
}
