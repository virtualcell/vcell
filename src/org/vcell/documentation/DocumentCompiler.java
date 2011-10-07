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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.vcell.util.FileUtils;

import cbit.util.xml.XmlUtil;
import cbit.vcell.xml.XmlParseException;

public class DocumentCompiler {

	public final static String VCELL_DOC_HTML_FILE_EXT = ".html";
	public final static String xmlFile = "";
	public final static String htmlRelativeParentDir = "topics\\";
	public final static String WorkingParentDir = "resources\\vcellDoc\\topics\\";
	public final static String OriginalDocParentDir = "UserDocumentation\\originalXML\\topics\\";
	public final static String[] DocGenerationDirs = new String[]{"PropertiesPanes","chapter_1", "chapter_2", "chapter_3", "chapter_4", "chapter_5"};
	public final static String ImageDir = "image";
	public final static String mapFileName = "Map.jhm";
	public final static String tocFileName = "TOC.xml";
		
	//test have two html pages and they point to each other, in addion, xmlFile1 has an image.
	public static void main(String[] args) {
		try {
			DocumentCompiler docCompiler = new DocumentCompiler();
			docCompiler.batchRun();
			docCompiler.generateHelpMap();
			docCompiler.generateTOC();
		}catch (Throwable e){
			e.printStackTrace(System.out);
		}
	}
	
	public void batchRun() throws Exception
	{
		Documentation documentation = new Documentation();
		//generate document images
		File origImgDir = new File(OriginalDocParentDir, ImageDir);
		if(origImgDir.exists())
		{
			File workingImgDir= new File(WorkingParentDir, ImageDir);
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
			//get images from the original image directory
			File[] imgFiles = origImgDir.listFiles();
			if(imgFiles != null)
			{
				for(File imgFile: imgFiles)
				{
					String fileNameStr = imgFile.getName(); 
					if(!fileNameStr.contains(".svn") && imgFile.isFile())//dont' move over svn file
					{
						File workingImgFile = new File(workingImgDir, fileNameStr);
						FileUtils.copyFile(imgFile, workingImgFile);
						BufferedImage img = ImageIO.read(imgFile);
						int imgWidth = img.getWidth();
						int imgHeight = img.getHeight();
						DocumentImage tempImg = new DocumentImage(new File(fileNameStr), ImageDir, " ", imgWidth, imgHeight, imgWidth, imgHeight);//TODO: how to set size?
						documentation.add(tempImg);
					}
				}
			}
		}
		
		//generate document pages
		for(String dirStr : DocGenerationDirs)
		{
			File workingDir = new File(WorkingParentDir, dirStr);
			File origDir = new File(OriginalDocParentDir, dirStr);
			if(origDir.exists())
			{
				if(!workingDir.exists()){
					workingDir.mkdirs();	
				}
				//delete all working directory files
				File[] oldXmlFiles = workingDir.listFiles();
				for(File file : oldXmlFiles)
				{
					if(!file.getName().contains(".svn"))//dont' delete svn file
					{
						file.delete();
					}
				}
				//get all xml files from the original xml directory.
				File[] xmlFiles = getXmlFiles(origDir);
				if(xmlFiles != null)
				{
					for(File xmlFile: xmlFiles)
					{
						DocumentPage documentPage = readTemplateFile(xmlFile, dirStr);
						documentation.add(documentPage);
						File htmlFile = documentation.getTargetFilename(documentPage);
						writeHTML(documentation,documentPage,htmlFile, workingDir);
					}
				}
			}
		}
	}
	
	private void generateHelpMap() throws Exception
	{
		String dir = new File(WorkingParentDir).getParent();
		File mapFile =  new File(dir, mapFileName);
		
		try{
			Element mapElement = new Element(VCellDocTags.map_tag);
			mapElement.setAttribute(VCellDocTags.version_tag, "1.0");
			//add toplevelfolder element
			Element topLevelElement = new Element(VCellDocTags.mapID_tag);
			topLevelElement.setAttribute(VCellDocTags.target_attr, "toplevelfolder");
			topLevelElement.setAttribute(VCellDocTags.url_attr, new File(WorkingParentDir, "toplevel.gif").getPath());
			mapElement.addContent(topLevelElement);
						
			//add mapID elements to mapElement
			for(String dirStr : DocGenerationDirs)//loop through all the folders
			{
				File workingDir = new File(WorkingParentDir, dirStr);
				File htmlRelativeWorkingDir = new File(htmlRelativeParentDir, dirStr);
				//get all html files
				File[] htmlFiles = workingDir.listFiles();
				if(htmlFiles != null && htmlFiles.length > 0)
				{
					boolean bFirstHtmlFile = true;
					for(int i=0; i<htmlFiles.length; i++)
					{
						File onefile = htmlFiles[i];
						if(onefile.getName().contains(".html"))
						{
							if(bFirstHtmlFile)
							{
								Element chapterMapIDElement = new Element(VCellDocTags.mapID_tag);
								chapterMapIDElement.setAttribute(VCellDocTags.target_attr, dirStr);
								chapterMapIDElement.setAttribute(VCellDocTags.url_attr, new File(htmlRelativeWorkingDir,onefile.getName()).getPath());
								mapElement.addContent(chapterMapIDElement);
								bFirstHtmlFile = false;
							}
							int extIdx = onefile.getName().indexOf(".html");
							String fileNameNoExt = onefile.getName().substring(0,extIdx);
							Element mapIDElement = new Element(VCellDocTags.mapID_tag);
							mapIDElement.setAttribute(VCellDocTags.target_attr, fileNameNoExt);
							mapIDElement.setAttribute(VCellDocTags.url_attr, new File(htmlRelativeWorkingDir,onefile.getName()).getPath());
							mapElement.addContent(mapIDElement);
						}
						else
						{
							continue;
						}
					}
				}
				
			}//end of for loop
			//convert mapdocument to string
			Document mapDoc = new Document();
			Comment docComment = new Comment(VCellDocTags.HelpMapHeader); 
			mapDoc.addContent(docComment);
			mapDoc.setRootElement(mapElement);
			String mapString = XmlUtil.xmlToString(mapDoc, false);
			
			XmlUtil.writeXMLStringToFile(mapString, mapFile.getPath(), true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw e;
		} 
			
	}
	
	private void generateTOC() throws Exception
	{
		String dir = new File(WorkingParentDir).getParent();
		File tocFile =  new File(dir, tocFileName);
		try{
			Element tocElement = new Element(VCellDocTags.toc_tag);
			tocElement.setAttribute(VCellDocTags.version_tag, "1.0");
			//add toplevelfolder element
			Element topLevelElement = new Element(VCellDocTags.tocitem_tag);
			topLevelElement.setAttribute(VCellDocTags.image_attr, "toplevelfolder");
			topLevelElement.setAttribute(VCellDocTags.text_attr, "The Virtual Cell Help");
			tocElement.addContent(topLevelElement);
						
			//add mapID elements to mapElement
			for(String dirStr : DocGenerationDirs)//loop through all the folders
			{
				if(dirStr.equals("PropertiesPanes"))
				{
					continue;
				}
				File workingDir = new File(WorkingParentDir, dirStr);
				//chapters are under topLevelElement, chapter has files and childern, clicking on chapter points to the first file.
				Element chapterElement = new Element(VCellDocTags.tocitem_tag);
				chapterElement.setAttribute(VCellDocTags.target_attr, dirStr);
				chapterElement.setAttribute(VCellDocTags.text_attr, dirStr);
				topLevelElement.addContent(chapterElement);
				//get all html files
				File[] htmlFiles = workingDir.listFiles();
				if(htmlFiles != null && htmlFiles.length > 0)
				{
					for(int i=0; i<htmlFiles.length; i++)
					{
						File onefile = htmlFiles[i];
						if(onefile.getName().contains(".html"))
						{
							int extIdx = onefile.getName().indexOf(".html");
							String fileNameNoExt = onefile.getName().substring(0,extIdx);
							Element fileElement = new Element(VCellDocTags.tocitem_tag);
							fileElement.setAttribute(VCellDocTags.target_attr, fileNameNoExt);
							fileElement.setAttribute(VCellDocTags.text_attr, fileNameNoExt);
							chapterElement.addContent(fileElement);
						}
						else
						{
							continue;
						}
					}
				}
				
			}//end of for loop
			//convert toc to string
			Document tocDoc = new Document();
			Comment tocComment = new Comment(VCellDocTags.HelpTOCHeader); 
			tocDoc.addContent(tocComment);
			tocDoc.setRootElement(tocElement);
			String tocString = XmlUtil.xmlToString(tocDoc, false);
			
			XmlUtil.writeXMLStringToFile(tocString, tocFile.getPath(), true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw e;
		} 
			
	}
	
	private File[] getXmlFiles(File workingDir)
	{
		File[] files = workingDir.listFiles();
		ArrayList<File> resultFiles = new ArrayList<File>();
		int len = 0;
		for(File file : files)
		{
			if((file.getName().endsWith(".xml")))
			{
				resultFiles.add(file);
				len ++;
			}
		}
		return resultFiles.toArray(new File[len]);
	}
	
	private DocumentPage readTemplateFile(File file, String parentDir) throws XmlParseException {
		Document doc = XmlUtil.readXML(file);
		Element root = doc.getRootElement();
		if (!root.getName().equals(VCellDocTags.VCellDoc_tag)){
			throw new RuntimeException("expecting ...");
		}
//		Namespace ns = Namespace.getNamespace("http://www.copasi.org/static/schema");	// default - blank namespace
//		List abc = root.getChildren();
		Element pageElement = root.getChild(VCellDocTags.page_tag);
		if (pageElement!=null){
			String filename = file.getName();
			String title = pageElement.getAttributeValue(VCellDocTags.page_title_attr);
			String target = pageElement.getAttributeValue(VCellDocTags.target_attr);
			DocSection appearance = getSection(pageElement,VCellDocTags.appearance_tag);
			DocSection introduction = getSection(pageElement,VCellDocTags.introduction_tag);
			DocSection operations = getSection(pageElement,VCellDocTags.operations_tag);
			DocumentPage documentTemplate = new DocumentPage(filename, parentDir, title, target, introduction, appearance, operations);
			return documentTemplate;
		}
		return null;
	}
	
	// top level section parse method.
	private DocSection getSection(Element root, String tagName) throws XmlParseException{
		DocSection docSection = new DocSection();
		readBlock(docSection,root.getChild(tagName));
		
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
			
	private void writeHTML(Documentation documentation, DocumentPage documentPage, File file, File directory) throws Exception
	{
		File htmlFile =  new File(directory, file.getName());
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
				 printComponent(documentation,introSection, pw);
				 pw.print("</" + VCellDocTags.html_new_line + ">");
				 pw.println();
			 }
			 //appearance
			 DocSection appSection = documentPage.getAppearance();
			 if(appSection.getComponents().size() > 0)
			 {
				 pw.print("<" + VCellDocTags.html_new_line + ">");
				 printComponent(documentation,appSection, pw);
				 pw.print("</" + VCellDocTags.html_new_line + ">");
				 pw.println();
			 }
			 //operation
			 DocSection opSection = documentPage.getOperations();
			 if(opSection.getComponents().size() > 0)
			 {
				 pw.print("<" + VCellDocTags.html_new_line + ">");
				 printComponent(documentation,opSection, pw);
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
	
	private void printComponent(Documentation documentation, DocTextComponent docComp, PrintWriter pw){
		 if (docComp instanceof DocText){
			 DocText text = (DocText)docComp;
			 pw.print(text.getText());
		 }else if (docComp instanceof DocLink){
			 DocLink docLink = (DocLink)docComp;
			 DocumentPage docPage = documentation.getDocumentPage(docLink);
			 if (docPage==null){
				 //temporarily commented, becase we are in the half way of putting docs together, it is reasonable that some pages/links are not available/ready. just leave the link blank.
				 //TODO:once everything is ready, we should uncomment this statement.
//					 throw new RuntimeException("cannot find the page with target "+docLink.getTarget());
			 }
			 if(docPage != null && documentation.getTargetFilename(docPage) != null)
			 {
				 pw.printf("<a href=\""+documentation.getTargetFilename(docPage)+"\">");
				 pw.printf(docLink.getText());
				 pw.printf("</a>");
			 }
		 }else if (docComp instanceof DocImageReference){
			 DocImageReference imageReference = (DocImageReference)docComp;
			 DocumentImage docImage = documentation.getDocumentImage(imageReference);
			 if (docImage==null){
				//temporarily commented, becase we are in the half way of putting docs together, it is reasonable that some pages/links are not available/ready. just leave the link blank.
				 //TODO:once everything is ready, we should uncomment this statement.
//					 throw new RuntimeException("cannot find the image with target "+imageReference.getImageTarget());
			 }
			 if (docImage != null && documentation.getTargetFilename(docImage) != null){
				 File imageFile = documentation.getTargetFilename(docImage);
				 pw.printf("<img src=\""+imageFile.getPath()+"\""+" width=\"" + docImage.getDisplayWidth()+ "\" height=\"" +docImage.getDisplayHeight()+"\">");
			 }
		 }else if (docComp instanceof DocList){
			 pw.printf("<ul>");
			 for (DocTextComponent comp : docComp.getComponents()){
				 printComponent(documentation, comp, pw);
			 }
			 pw.printf("</ul>");
		 }else if (docComp instanceof DocParagraph){
			 pw.printf("<p>");
			 for (DocTextComponent comp : docComp.getComponents()){
				 printComponent(documentation, comp, pw);
			 }
			 pw.printf("</p>");
		 }else if (docComp instanceof DocListItem){
			 pw.printf("<li>");
			 for (DocTextComponent comp : docComp.getComponents()){
				 printComponent(documentation, comp, pw);
			 }
			 pw.printf("</li>");
		 }else if (docComp instanceof DocSection){
			 for (DocTextComponent comp : docComp.getComponents()){
				 printComponent(documentation, comp, pw);
			 }
		 }
	}
}
