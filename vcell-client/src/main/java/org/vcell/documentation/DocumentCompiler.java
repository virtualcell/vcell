/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  https://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.documentation;

import cbit.util.xml.XmlUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.vcell.util.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class DocumentCompiler {
	private final static int MAX_IMG_FILE_SIZE = 50000;
	private final static int WARN_IMG_FILE_SIZE = 25000;
	private final static int maxImgWidth = 600;
	private final static int maxImgHeight = 600;
	private final static String imageFilePath = "topics/image/";
	private final static String tocFileName = "TOC.xml";
	private final static String definitionXMLFileName = "Definitions.xml";
	public final static String definitionTarget = "Definitions";

	private final File docTargetDir;
	private final File docSourceDir;
	private final TreeSet<String> referencedImageFiles = new TreeSet<>();


	public DocumentCompiler(File docSourceDir, File docTargetDir) {
		this.docSourceDir = docSourceDir;
		this.docTargetDir = docTargetDir;
	}


	public static void main(String[] args) {
		try {
			if (args.length!=2) {
				System.out.println("Usage: java "+DocumentCompiler.class.getTypeName()+" input-xml-dir  output-javahelp-dir");
				System.exit(-1);
			}
			File docSourceDir = new File(args[0]); // see vcell-client/pom.xml
			File docTargetDir = new File(args[1]); // see vcell-client/pom.xml
			System.out.println("docSourceDir="+docSourceDir);
			System.out.println("docTargetDir="+docTargetDir);
			System.out.flush();
			if (!docSourceDir.exists() || !docSourceDir.isDirectory()){
				throw new RuntimeException("document source directory "+docSourceDir.getPath()+" doesn't exist or isn't a directory");
			}
			if (!docTargetDir.exists()){
				docTargetDir.mkdirs();
			}else if (!docTargetDir.isDirectory()){
				throw new RuntimeException("document target directory "+docTargetDir.getPath()+" isn't a directory");
			}
			DocumentCompiler docCompiler = new DocumentCompiler(docSourceDir, docTargetDir);
			Documentation documentation = docCompiler.parseDocumentation();

			DocumentWriter documentWriter = new JavaHelpHtmlWriter(docSourceDir, docTargetDir, tocFileName);
			documentWriter.writeFiles(documentation);
			//write document definitions
		}catch (Throwable e){
			e.printStackTrace(System.out);
		}
	}


	public Documentation parseDocumentation() throws Exception
	{
		Documentation documentation = new Documentation();
		//generate document images
		File imgSourceDir = new File(docSourceDir, imageFilePath);
		if (!imgSourceDir.exists() || !imgSourceDir.isDirectory()){
			throw new RuntimeException("cannot find source image directory "+imgSourceDir.getPath());
		}

		File workingImgDir= new File(docTargetDir, imageFilePath);
		if(!workingImgDir.exists()){
			workingImgDir.mkdirs();	
		}
		//delete all files in the working image directory
		File[] oldImgFiles = workingImgDir.listFiles();
        assert oldImgFiles != null;
        for(File file : oldImgFiles) {
			if (!file.delete()) {
				throw new RuntimeException("failed to delete file "+file.getPath());
			}
		}

		File[] sourceDirectories = FileUtils.getAllDirectories(docSourceDir);
		for (File sourceDir : sourceDirectories){
			//
			// the targetDirectory is the source directory with the source/target root directories replaced.
			//
			File targetDir = getTargetDirectory(sourceDir);
			if(!targetDir.exists()){
				targetDir.mkdirs();
			}
			//delete all working directory files
			File[] oldHtmlFiles = targetDir.listFiles();
			for(File htmlFile : oldHtmlFiles) {
				htmlFile.delete();
			}
			//get all xml files from the original xml directory.
			File[] xmlFiles = sourceDir.listFiles(pathname -> pathname.getName().endsWith(".xml"));
			for(File xmlFile : xmlFiles) {
				if (xmlFile.getName().equals(definitionXMLFileName)){
					ArrayList<DocumentDefinition> docDefs = readDefinitionFile(xmlFile);
					documentation.add(docDefs);
				}
				else
				{
					DocumentPage documentPage = readTemplateFile(xmlFile);
					documentation.add(documentPage);
				}
			}
		}
		
		//get images from the original image directory
		Vector<File> unreferencedImageFiles = new Vector<>();
		File[] imgFiles = imgSourceDir.listFiles();
		for(File imgFile: imgFiles) {
			if(imgFile.isFile()) {
				String name = imgFile.getName();
				if(!referencedImageFiles.remove(name)){
					unreferencedImageFiles.add(imgFile);
				}
				File workingImgFile = getTargetFile(imgFile);
				FileUtils.copyFile(imgFile, workingImgFile);
				BufferedImage img = ImageIO.read(imgFile);
				int imgWidth = img.getWidth();
				int imgHeight = img.getHeight();
				String imgChkStr = "";
				if(imgWidth > maxImgWidth || imgHeight > maxImgHeight)
				{
					imgChkStr = "ERROR: (" + imgFile.getName() + ") image dimension ("+imgWidth+","+imgHeight+") exceeds the maximum allowed image dimension("+ maxImgWidth + ","+maxImgHeight+") in VCell Help.";
				}
				if(imgFile.length() > MAX_IMG_FILE_SIZE){
					imgChkStr = (!imgChkStr.isEmpty() ? imgChkStr+"\n" : "") +
							"ERROR: (" + imgFile.getName() + ") ERROR file size ("+imgFile.length()+") greater than maximum allowed file size ("+MAX_IMG_FILE_SIZE+") in VCell Help.";
				}
				if(imgFile.length() > WARN_IMG_FILE_SIZE && imgFile.length() <= MAX_IMG_FILE_SIZE){
					System.out.println("WARNING: (" + imgFile.getName() + ") file size ("+imgFile.length()+") greater than preferred file size ("+WARN_IMG_FILE_SIZE+") in VCell Help.");
				}
				if(!imgChkStr.isEmpty()){
					System.err.println(imgChkStr);
				}
				DocumentImage tempImg = new DocumentImage(workingImgFile, imgWidth, imgHeight, imgWidth, imgHeight);
				documentation.add(tempImg);
			}
		}
		if (!unreferencedImageFiles.isEmpty()){
			System.out.println("-----Unreferenced Image Files");
			for (int i = 0; i < unreferencedImageFiles.size(); i++) {
				System.out.println("WARNING: Unreferenced image: "+unreferencedImageFiles.elementAt(i).getName());
			}
			System.out.println("-----");
		}

		validateTOC(documentation);

		return documentation;
	}

	private File getTargetFile(File sourceFile){
		return new File(getTargetDirectory(sourceFile.getParentFile()), sourceFile.getName());
	}
	
	private File getTargetDirectory(File sourceDir){
		return new File(sourceDir.getPath().replace(docSourceDir.getPath(),docTargetDir.getPath()));
	}
	

	
	private void validateTOC(Documentation documentation) throws Exception
	{
		File tocSourceFile =  new File(docSourceDir, tocFileName);
		//
		// read in existing TOC file and validate its contents
		//
		Document doc = XmlUtil.readXML(tocSourceFile);
		Element root = doc.getRootElement();
		if (!root.getName().equals(VCellDocTags.toc_tag)){
			throw new RuntimeException("expecting "+VCellDocTags.toc_tag+" in file "+tocSourceFile.getPath());
		}

		HashSet<DocumentPage> pagesNotYetReferenced = new HashSet<>(Arrays.asList(documentation.getDocumentPages()));
		readTOCItem(documentation, pagesNotYetReferenced, root);
		for (DocumentPage docPage : pagesNotYetReferenced){
			if (!documentation.isReferenced(docPage)){
				System.err.println("ERROR: Document page '"+docPage.getTarget()+"' not referenced in either table of contents or from another document");
			}
		}
	}
	
	private void readTOCItem(Documentation documentation, HashSet<DocumentPage> pagesNotYetReferenced, Element element){
		if (element.getName().equals(VCellDocTags.tocitem_tag)){
			String target = element.getAttributeValue(VCellDocTags.target_attr);
			if (target!=null){
				// first look for a documentPage as the target, else check if it is a Definition page.
				DocumentPage targetDocPage = documentation.getDocumentPage(new DocLink(target,target));
				if (targetDocPage==null){
					if (!target.equals(definitionTarget)){
						throw new RuntimeException("table of contents referencing nonexistant target '"+target+"'");
					}
				}else{
					pagesNotYetReferenced.remove(targetDocPage);
				}
			}
		}else if (!element.getName().equals(VCellDocTags.toc_tag)) {
			throw new RuntimeException("unexpecteded element '"+element.getName()+"' in table of contents");
		}
        @SuppressWarnings("unchecked")
		List<Element> children = element.getChildren(VCellDocTags.tocitem_tag);
		for (Element tocItemElement : children){
			readTOCItem(documentation, pagesNotYetReferenced, tocItemElement);
		}
	}

	private ArrayList<DocumentDefinition> readDefinitionFile(File file) {
		ArrayList<DocumentDefinition> documentDefs = new ArrayList<>();
		
		Document doc = XmlUtil.readXML(file);
		Element root = doc.getRootElement();
		if (!root.getName().equals(VCellDocTags.VCellDoc_tag)){
			throw new RuntimeException("expecting ...");
		}
		//get all definition elements
		@SuppressWarnings("unchecked")
		List<Object> pageElements = root.getContent();
		
		if (pageElements!=null){
			for(Object pageElement : pageElements)
			{
				if(pageElement instanceof Element && ((Element)pageElement).getName().equals(VCellDocTags.definition_tag))
				{
					String target = ((Element)pageElement).getAttributeValue(VCellDocTags.target_attr);
					String label = ((Element)pageElement).getAttributeValue(VCellDocTags.definition_label_attr);
					String text = ((Element)pageElement).getText();
					DocumentDefinition documentDefinition = new DocumentDefinition(file, target, label, text);
					documentDefs.add(documentDefinition);
				}
			}
		}
		
		return documentDefs;
	}
	
	
	private DocumentPage readTemplateFile(File file) {
		Document doc = XmlUtil.readXML(file);
		Element root = doc.getRootElement();
		if (!root.getName().equals(VCellDocTags.VCellDoc_tag)){
			throw new RuntimeException("expecting ...");
		}
		Element pageElement = root.getChild(VCellDocTags.page_tag);
		if (pageElement!=null){
			String title = pageElement.getAttributeValue(VCellDocTags.page_title_attr);
			DocSection appearance = getSection(pageElement,VCellDocTags.appearance_tag, file);
			DocSection introduction = getSection(pageElement,VCellDocTags.introduction_tag, file);
			DocSection operations = getSection(pageElement,VCellDocTags.operations_tag, file);
            return new DocumentPage(file, title, introduction, appearance, operations);
		}
		return null;
	}
	
	// top level section parse method.
	private DocSection getSection(Element root, String tagName, File xmlFile) {
		DocSection docSection = new DocSection();
		Element sectionRootElement = root.getChild(tagName);
		if (sectionRootElement!=null){
			readBlock(docSection,sectionRootElement, xmlFile);
		}
		return docSection;
	}
	
	private void readBlock(DocTextComponent docComponent, Element element, File xmlFile) {
		@SuppressWarnings("rawtypes")
		List children = element.getContent();
		for (Object child : children){
			if (child instanceof Text){
				Text childText = (Text)child;
				if (!childText.getText().trim().isEmpty()){
					docComponent.add(new DocText(childText.getText(),false));
				}
			}else if (child instanceof Element childElement){
				if (childElement.getName().equals(VCellDocTags.link_tag)){
					String linkText = childElement.getText();
					String linkTarget = childElement.getAttributeValue(VCellDocTags.target_attr);
					docComponent.add(new DocLink(linkTarget,linkText));
				}else if (childElement.getName().equals(VCellDocTags.img_ref_tag)){
					String linkTarget = childElement.getAttributeValue(VCellDocTags.target_attr);
					boolean bInline = Boolean.parseBoolean(childElement.getAttributeValue(VCellDocTags.inline_attr));
					docComponent.add(new DocImageReference(linkTarget,bInline));
					referencedImageFiles.add(linkTarget);
				}else if (childElement.getName().equals(VCellDocTags.definition_tag)){ //definition link
					String linkText = childElement.getText();
					String linkTarget = childElement.getAttributeValue(VCellDocTags.target_attr);
					docComponent.add(new DocDefinitionReference(linkTarget, linkText));	
				}else if (childElement.getName().equals(VCellDocTags.bold_tag)){
					@SuppressWarnings("rawtypes")
					List boldChildren = childElement.getContent();
					for (Object boldChild : boldChildren){
						if (boldChild instanceof Text childText){
							docComponent.add(new DocText(childText.getText(),true));
						}else if (boldChild instanceof Element){
							throw new RuntimeException("only plain text is allowed within <bold></bold> elements");
						}
					}
				}else if (childElement.getName().equals(VCellDocTags.list_tag)){
					DocList docList = new DocList();
					docComponent.add(docList);
					readBlock(docList, childElement, xmlFile);
				}else if (childElement.getName().equals(VCellDocTags.listItem_tag)){
					DocListItem docListItem = new DocListItem();
					docComponent.add(docListItem);
					readBlock(docListItem, childElement, xmlFile);
				}else if (childElement.getName().equals(VCellDocTags.paragraph_tag)){
					DocParagraph docParagraph = new DocParagraph();
					docComponent.add(docParagraph);
					readBlock(docParagraph, childElement, xmlFile);
				}
				else
				{
					System.err.println("Error: Unsupported element " + childElement.getName() + " in the file: " + xmlFile.getAbsolutePath());
//					throw new RuntimeException("Unsupported element " + childElement.getName());
				}
			}
		}
	}
}
