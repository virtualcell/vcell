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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.vcell.util.FileUtils;

import cbit.util.xml.XmlUtil;
import cbit.vcell.xml.XmlParseException;

import com.sun.java.help.search.Indexer;

public class DocumentCompiler {

	public final static String VCELL_DOC_HTML_FILE_EXT = ".html";
	public static File docTargetDir;
	public static File docSourceDir;
	public final static String ImageDir = "topics\\image";
	public final static String mapFileName = "Map.jhm";
	public final static String tocFileName = "TOC.xml";
	public final static String helpSetFileName = "HelpSet.hs";
	
	private Documentation documentation = new Documentation();

	
	FileFilter directoryFileFilter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};
	FileFilter xmlFileFilter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".xml");
		}
	};
		
	//test have two html pages and they point to each other, in addion, xmlFile1 has an image.
	public static void main(String[] args) {
		try {
			if (args.length==2){
				docSourceDir = new File(args[0]);
				if (!docSourceDir.exists() || !docSourceDir.isDirectory()){
					throw new RuntimeException("document source directory "+docSourceDir.getPath()+" doesn't exist or isn't a directory");
				}
				docTargetDir = new File(args[1]);
				if (!docTargetDir.exists()){
					docTargetDir.mkdirs();
				}else if (!docTargetDir.isDirectory()){
					throw new RuntimeException("document target directory "+docTargetDir.getPath()+" isn't a directory");
				}
			}else{
				throw new RuntimeException("expecting xmlSource and resource directories for documentation");
			}
			DocumentCompiler docCompiler = new DocumentCompiler();
			docCompiler.batchRun();
			docCompiler.generateHelpMap();
			docCompiler.validateTOC();
			docCompiler.copyHelpSet();
			docCompiler.generateHelpSearch();
		}catch (Throwable e){
			e.printStackTrace(System.out);
		}
	}
	
	private void generateHelpSearch() 
	{
		Indexer indexer = new Indexer();
		try {
			indexer.compile(new String[]{"-db","C:\\Developer\\eclipse\\workspace\\VCell\\resources\\vcellDoc\\JavaHelpSearch","C:\\Developer\\eclipse\\workspace\\VCell\\resources\\vcellDoc\\topics"});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void batchRun() throws Exception
	{
		//generate document images
		File imgSourceDir = new File(docSourceDir, ImageDir);
		if (!imgSourceDir.exists() || !imgSourceDir.isDirectory()){
			throw new RuntimeException("cannot find source image directory "+imgSourceDir.getPath());
		}

		File workingImgDir= new File(docTargetDir, ImageDir);
		if(!workingImgDir.exists()){
			workingImgDir.mkdirs();	
		}
		//delete all images
		File[] oldImgFiles = workingImgDir.listFiles();
		for(File file : oldImgFiles)
		{
			if(!file.getName().contains(".svn"))//dont' delete svn file
			{
				file.delete();
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
			for(File htmlFile : oldHtmlFiles)
			{
				if(!htmlFile.getName().contains(".svn"))//dont' delete svn file
				{
					htmlFile.delete();
				}
			}
			//get all xml files from the original xml directory.
			for(File xmlFile : sourceDir.listFiles(xmlFileFilter)) {
				if (xmlFile.getName().equals("Definitions.xml")){
					System.out.println("IGNORING DEFINITIONS.XML for now");
					continue;
				}
				DocumentPage documentPage = readTemplateFile(xmlFile);
				documentation.add(documentPage);
			}
		}
		
		//get images from the original image directory
		File[] imgFiles = imgSourceDir.listFiles();
		for(File imgFile: imgFiles) {
			if(!imgFile.getName().contains(".svn") && imgFile.isFile())//dont' move over svn file
			{
				File workingImgFile = getTargetFile(imgFile);
				FileUtils.copyFile(imgFile, workingImgFile);
				BufferedImage img = ImageIO.read(imgFile);
				int imgWidth = img.getWidth();
				int imgHeight = img.getHeight();
				DocumentImage tempImg = new DocumentImage(workingImgFile, imgWidth, imgHeight, imgWidth, imgHeight);//TODO: how to set size?
				documentation.add(tempImg);
			}
		}
		
		for(DocumentPage documentPage : documentation.getDocumentPages()) {
			File htmlFile = getTargetFile(documentPage.getTemplateFile());
			htmlFile = new File(htmlFile.getPath().replace(".xml",".html"));
			try {
				writeHTML(documentation,documentPage,htmlFile);
			}catch (Exception e){
				e.printStackTrace(System.out);
				throw new RuntimeException("failed to parse document "+documentPage.getTemplateFile().getPath());
			}
		}
	}
	
	private File getTargetFile(File sourceFile){
		return new File(getTargetDirectory(sourceFile.getParentFile()), sourceFile.getName());
	}
	
	private File getTargetDirectory(File sourceDir){
		return new File(sourceDir.getPath().replace(docSourceDir.getPath(),docTargetDir.getPath()));
	}
	
	private void generateHelpMap() throws Exception
	{
		File mapFile =  new File(docTargetDir, mapFileName);
		
		try{
			Element mapElement = new Element(VCellDocTags.map_tag);
			mapElement.setAttribute(VCellDocTags.version_tag, "1.0");
			//add toplevelfolder element
			Element topLevelElement = new Element(VCellDocTags.mapID_tag);
			topLevelElement.setAttribute(VCellDocTags.target_attr, "toplevelfolder");
			topLevelElement.setAttribute(VCellDocTags.url_attr, ".\\topics\\image\\vcell.gif");
			mapElement.addContent(topLevelElement);	
			//add doc html files
			for (DocumentPage documentPage : documentation.getDocumentPages()) {
				String fileNameNoExt = documentPage.getTemplateFile().getName().replace(".xml","");
				Element mapIDElement = new Element(VCellDocTags.mapID_tag);
				mapIDElement.setAttribute(VCellDocTags.target_attr, fileNameNoExt);
				File targetHtmlFile = getTargetFile(documentPage.getTemplateFile());
				targetHtmlFile = new File(targetHtmlFile.getPath().replace(".xml",".html"));
				mapIDElement.setAttribute(VCellDocTags.url_attr, FileUtils.getRelativePath(docTargetDir, targetHtmlFile, false).getPath());
				mapElement.addContent(mapIDElement);	
			}
			//convert mapdocument to string
			Document mapDoc = new Document();
			mapDoc.setRootElement(mapElement);
			
			String mapString = XmlUtil.xmlToString(mapDoc, false);
			XmlUtil.writeXMLStringToFile(mapString, mapFile.getPath(), true);
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw e;
		} 
			
	}
	
	private void validateTOC() throws Exception
	{
		File tocSourceFile =  new File(docSourceDir, tocFileName);
		//
		// read in existing TOC file and validate it's contents
		//
		Document doc = XmlUtil.readXML(tocSourceFile);
		Element root = doc.getRootElement();
		if (!root.getName().equals(VCellDocTags.toc_tag)){
			throw new RuntimeException("expecting "+VCellDocTags.toc_tag+" in file "+tocSourceFile.getPath());
		}
//		Namespace ns = Namespace.getNamespace("http://www.copasi.org/static/schema");	// default - blank namespace
//		List abc = root.getChildren();
		HashSet<DocumentPage> pagesNotYetReferenced = new HashSet<DocumentPage>(Arrays.asList(documentation.getDocumentPages()));
		readTOCItem(pagesNotYetReferenced, root);
		if (pagesNotYetReferenced.size()>0){
			for (DocumentPage docPage : pagesNotYetReferenced){
				System.err.println("Document page '"+docPage.getTarget()+"' not referenced in table of contents");
			}
		}
		
		// copy the Table of Contents to the target directory.
		FileUtils.copyFile(new File(docSourceDir, tocFileName),new File(docTargetDir, tocFileName));			
	}
	
	private void copyHelpSet() throws Exception
	{
		FileUtils.copyFile(new File(docSourceDir, helpSetFileName),new File(docTargetDir, helpSetFileName));
	}
	
	private void readTOCItem(HashSet<DocumentPage> pagesNotYetReferenced, Element element){
		if (element.getName().equals(VCellDocTags.tocitem_tag)){
			String target = element.getAttributeValue(VCellDocTags.target_attr);
			String text = element.getAttributeValue(VCellDocTags.text_attr);
			if (target!=null){
				DocumentPage targetDocPage = documentation.getDocumentPage(new DocLink(target,target));
				if (targetDocPage==null){
					throw new RuntimeException("table of contents referencing nonexistant target '"+target+"'");
				}else{
					pagesNotYetReferenced.remove(targetDocPage);
				}
			}
		}else if (element.getName().equals(VCellDocTags.toc_tag)){
			// do nothing
		}else{
			throw new RuntimeException("unexpecteded element '"+element.getName()+"' in table of contents");
		}
		List<Element> children = element.getChildren(VCellDocTags.tocitem_tag);
		for (Element tocItemElement : children){
			readTOCItem(pagesNotYetReferenced, tocItemElement);
		}
	}
	
	private DocumentPage readTemplateFile(File file) throws XmlParseException {
		Document doc = XmlUtil.readXML(file);
		Element root = doc.getRootElement();
		if (!root.getName().equals(VCellDocTags.VCellDoc_tag)){
			throw new RuntimeException("expecting ...");
		}
		Element pageElement = root.getChild(VCellDocTags.page_tag);
		if (pageElement!=null){
			String title = pageElement.getAttributeValue(VCellDocTags.page_title_attr);
			DocSection appearance = getSection(pageElement,VCellDocTags.appearance_tag);
			DocSection introduction = getSection(pageElement,VCellDocTags.introduction_tag);
			DocSection operations = getSection(pageElement,VCellDocTags.operations_tag);
			DocumentPage documentTemplate = new DocumentPage(file, title, introduction, appearance, operations);
			return documentTemplate;
		}
		return null;
	}
	
	// top level section parse method.
	private DocSection getSection(Element root, String tagName) throws XmlParseException{
		DocSection docSection = new DocSection();
		Element sectionRootElement = root.getChild(tagName);
		if (sectionRootElement!=null){
			readBlock(docSection,sectionRootElement);
		}
		return docSection;
	}
	
	private void readBlock(DocTextComponent docComponent, Element element) {
		List children = element.getContent();
		for (Object child : children){
			if (child instanceof Text){
				Text childText = (Text)child;
				if (childText.getText().trim().length()>0){
					docComponent.add(new DocText(childText.getText(),false));
				}
			}else if (child instanceof Element){
				Element childElement = (Element)child;
				if (childElement.getName().equals(VCellDocTags.link_tag)){
					String linkText = childElement.getText();
					String linkTarget = childElement.getAttributeValue(VCellDocTags.target_attr);
					docComponent.add(new DocLink(linkTarget,linkText));
				}else if (childElement.getName().equals(VCellDocTags.img_ref_tag)){
					String linkTarget = childElement.getAttributeValue(VCellDocTags.target_attr);
					docComponent.add(new DocImageReference(linkTarget));
				}else if (childElement.getName().equals(VCellDocTags.bold_tag)){
					List boldChildren = childElement.getContent();
					for (Object boldChild : boldChildren){
						if (boldChild instanceof Text){
							Text childText = (Text)boldChild;
							docComponent.add(new DocText(childText.getText(),true));
						}else if (boldChild instanceof Element){
							throw new RuntimeException("only plain text is allowed within <bold></bold> elements");
						}
					}
				}else if (childElement.getName().equals(VCellDocTags.list_tag)){
					DocList docList = new DocList();
					docComponent.add(docList);
					readBlock(docList, childElement);
				}else if (childElement.getName().equals(VCellDocTags.listItem_tag)){
					DocListItem docListItem = new DocListItem();
					docComponent.add(docListItem);
					readBlock(docListItem, childElement);
				}else if (childElement.getName().equals(VCellDocTags.paragraph_tag)){
					DocParagraph docParagraph = new DocParagraph();
					docComponent.add(docParagraph);
					readBlock(docParagraph, childElement);
				}
			}
		}
	}
			
	private void writeHTML(Documentation documentation, DocumentPage documentPage, File htmlFile) throws Exception
	{
		PrintWriter pw = null;
		try {
			 pw = new PrintWriter(htmlFile);
			 //start html
			 pw.println("<" + VCellDocTags.html_tag + ">");
			 
			 //start head
			 pw.println("<" + VCellDocTags.html_head_tag + ">");
			 //start title
			 pw.print("<" + VCellDocTags.html_title_tag + ">");
			 pw.print("VCell Documentaion");                    //html page title, replace when needed
			 //end title
			 pw.print("</" + VCellDocTags.html_title_tag + ">");
			 pw.println();
			 //end head
			 pw.print("</" + VCellDocTags.html_head_tag + ">");
			 
			 //start body
			 pw.println("<" + VCellDocTags.html_body_tag + ">");
			 //title
			 pw.print("<" + VCellDocTags.html_new_line + ">");
			 pw.print(documentPage.getTitle());
			 pw.print("</" + VCellDocTags.html_new_line + ">");
			 pw.println();
			 
			 //introduction
			 DocSection introSection = documentPage.getIntroduction();
			 if(introSection.getComponents().size() > 0)
			 {
				 pw.print("<" + VCellDocTags.html_new_line + ">");
				 printComponent(documentation,introSection, htmlFile.getParentFile(), pw);
				 pw.print("</" + VCellDocTags.html_new_line + ">");
				 pw.println();
			 }
			 //appearance
			 DocSection appSection = documentPage.getAppearance();
			 if(appSection.getComponents().size() > 0)
			 {
				 pw.print("<" + VCellDocTags.html_new_line + ">");
				 printComponent(documentation,appSection, htmlFile.getParentFile(), pw);
				 pw.print("</" + VCellDocTags.html_new_line + ">");
				 pw.println();
			 }
			 //operation
			 DocSection opSection = documentPage.getOperations();
			 if(opSection.getComponents().size() > 0)
			 {
				 pw.print("<" + VCellDocTags.html_new_line + ">");
				 printComponent(documentation,opSection, htmlFile.getParentFile(), pw);
				 pw.print("</" + VCellDocTags.html_new_line + ">");
				 pw.println();
			 }
			 //end body
			 pw.println("</" + VCellDocTags.html_body_tag + ">");
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw e;
		} finally {
			if (pw != null) {
				pw.close();	
			}
		}
	}
	
	private void printComponent(Documentation documentation, DocTextComponent docComp, File directory, PrintWriter pw) throws IOException {
		 if (docComp instanceof DocText){
			 DocText text = (DocText)docComp;
			 pw.print(text.getText());
		 }else if (docComp instanceof DocLink){
			 DocLink docLink = (DocLink)docComp;
			 DocumentPage docPage = documentation.getDocumentPage(docLink);
			 if (docPage==null){
				 throw new RuntimeException("reference to document '"+docLink.getTarget()+"' cannot be resolved");
			 }
			 File htmlFile = getTargetFile(docPage.getTemplateFile());
			 htmlFile = new File(htmlFile.getPath().replace(".xml",".html"));
			 String relativePathToTarget = FileUtils.getRelativePath(directory, htmlFile, false).getPath();
			 pw.printf("<a href=\""+relativePathToTarget+"\">");
			 pw.printf(docLink.getText());
			 pw.printf("</a>");
		 }else if (docComp instanceof DocImageReference){
			 DocImageReference imageReference = (DocImageReference)docComp;
			 DocumentImage docImage = documentation.getDocumentImage(imageReference);
			 if (docImage==null){
				throw new RuntimeException("reference to image '"+imageReference+"' cannot be resolved");
			 }
			 File imageFile = getTargetFile(docImage.getSourceFile());
			 String relativePathToTarget = FileUtils.getRelativePath(directory, imageFile, false).getPath();
			 pw.printf("<img src=\""+relativePathToTarget+"\""+" width=\"" + docImage.getDisplayWidth()+ "\" height=\"" +docImage.getDisplayHeight()+"\">");
		 }else if (docComp instanceof DocList){
			 pw.printf("<ul>");
			 for (DocTextComponent comp : docComp.getComponents()){
				 printComponent(documentation, comp, directory, pw);
			 }
			 pw.printf("</ul>");
		 }else if (docComp instanceof DocParagraph){
			 pw.printf("<p>");
			 for (DocTextComponent comp : docComp.getComponents()){
				 printComponent(documentation, comp, directory, pw);
			 }
			 pw.printf("</p>");
		 }else if (docComp instanceof DocListItem){
			 pw.printf("<li>");
			 for (DocTextComponent comp : docComp.getComponents()){
				 printComponent(documentation, comp, directory, pw);
			 }
			 pw.printf("</li>");
		 }else if (docComp instanceof DocSection){
			 for (DocTextComponent comp : docComp.getComponents()){
				 printComponent(documentation, comp, directory, pw);
			 }
		 }
	}
}
