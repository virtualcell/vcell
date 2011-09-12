package org.vcell.documentation;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.vcell.sybil.rdf.NameSpace;
import org.vcell.util.FileUtils;

import cbit.util.xml.XmlUtil;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlParseException;

public class DocumentCompiler {

	public final static String VCELL_DOC_HTML_FILE_EXT = ".html";
	public final static String xmlFile = "";
	public final static String WorkingParentDir = "UserDocumentation\\doc\\topic\\";
	public final static String OriginalDocParentDir = "UserDocumentation\\originalXML\\topic\\";
	public final static String[] DocGenerationDirs = new String[]{"chapter_2"};
	//test have two html pages and they point to each other, in addion, xmlFile1 has an image.
	public static void main(String[] args) {
		try {
			DocumentCompiler docCompiler = new DocumentCompiler();
			docCompiler.batchRun();
//			
//			File xmlFile1 = new File(WorkingParentDir + "pageTest1.xml");
//			File xmlFile2 = new File(WorkingParentDir + "pageTest2.xml");
//			
////			File xmlFile = new File("C:\\mm.xml");
//			DocumentPage documentPage1 = docCompiler.readTemplateFile(xmlFile1);
//			Documentation documentation = new Documentation();
//			documentation.add(documentPage1);
//			
//			DocumentPage documentPage2 = docCompiler.readTemplateFile(xmlFile2);
//			documentation.add(documentPage2);
//			
//			DocumentImage documentImg1 = new DocumentImage(new File("vcell.gif"), " ", 64,64,64,64);
//			documentation.add(documentImg1);
//			
//			File htmlFile1 = documentation.getTargetFilename(documentPage1);
//			docCompiler.writeHTML(documentation,documentPage1,htmlFile1);
//			File htmlFile2 = documentation.getTargetFilename(documentPage2);
//			docCompiler.writeHTML(documentation,documentPage2,htmlFile2);
			
		}catch (Throwable e){
			e.printStackTrace(System.out);
		}
	}
	
	public void batchRun() throws Exception
	{
		Documentation documentation = new Documentation();
		//generate document images
		File origImgDir = new File(OriginalDocParentDir, "image");
		if(origImgDir.exists())
		{
			File workingImgDir= new File(WorkingParentDir, "image");
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
						DocumentImage tempImg = new DocumentImage(workingImgFile, " ", 80,80,80,80);//TODO: how to set size?
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
						DocumentPage documentPage = readTemplateFile(xmlFile);
						documentation.add(documentPage);
						File htmlFile = documentation.getTargetFilename(documentPage);
						writeHTML(documentation,documentPage,htmlFile, workingDir);
					}
				}
			}
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
	
	private DocumentPage readTemplateFile(File file) throws XmlParseException {
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
			ArrayList<DocTextComponent> appearance = getSection(pageElement,VCellDocTags.appearance_tag);
			ArrayList<DocTextComponent> introduction = getSection(pageElement,VCellDocTags.introduction_tag);
			ArrayList<DocTextComponent> operations = getSection(pageElement,VCellDocTags.operations_tag);
			ArrayList<DocTextComponent> properties = getSection(pageElement,VCellDocTags.properties_tag);
			DocumentPage documentTemplate = new DocumentPage(filename, title, target, introduction, appearance, operations, properties);
			return documentTemplate;
		}
		return null;
	}
	
	private ArrayList<DocTextComponent> getSection(Element root, String tagName) throws XmlParseException{
		ArrayList<DocTextComponent> docTextComponents = new ArrayList<DocTextComponent>();
		Element sectionElement = root.getChild(tagName);
		if(sectionElement != null)
		{
			List children = sectionElement.getContent();
			for (Object child : children){
				if (child instanceof Text){
					Text childText = (Text)child;
					docTextComponents.add(new DocText(childText.getText()));
				}else if (child instanceof Element){
					Element childElement = (Element)child;
					if (childElement.getName().equals(VCellDocTags.link_tag)){
						String linkText = childElement.getText();
						String linkTarget = childElement.getAttributeValue(VCellDocTags.target_attr);
						docTextComponents.add(new DocLink(linkTarget,linkText));
					}else if (childElement.getName().equals(VCellDocTags.img_ref_tag)){
						String linkTarget = childElement.getAttributeValue(VCellDocTags.target_attr);
						docTextComponents.add(new DocImageReference(linkTarget));
					}else{
						//TODO: we may need to add more tags such as : Definition
						//comment this statement since our tags are not complete yet. once everything is settled, we'll uncomment this statement.
//						throw new XmlParseException("unexpected element "+childElement.getName());
					}
				}
			}
		}
		return docTextComponents;
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
			 pw.print("<" + VCellDocTags.html_new_line + ">");
			 printSection(documentation,documentPage.getIntroduction(), pw);
			 pw.print("</" + VCellDocTags.html_new_line + ">");
			 pw.println();

			 //appearance
			 pw.print("<" + VCellDocTags.html_new_line + ">");
			 printSection(documentation,documentPage.getAppearance(), pw);
			 pw.print("</" + VCellDocTags.html_new_line + ">");
			 pw.println();

			 //operation
			 pw.print("<" + VCellDocTags.html_new_line + ">");
			 printSection(documentation,documentPage.getOperations(), pw);
			 pw.print("</" + VCellDocTags.html_new_line + ">");
			 pw.println();

			 //properties
			 pw.print("<" + VCellDocTags.html_new_line + ">");
			 printSection(documentation,documentPage.getProperties(), pw);
			 pw.print("</" + VCellDocTags.html_new_line + ">");
			 pw.println();

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

	private void printSection(Documentation documentation, ArrayList<DocTextComponent> docTextComponents, PrintWriter pw){
		 for (DocTextComponent docComp : docTextComponents){
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
				 if(docImage != null && documentation.getTargetFilename(docImage) != null)
				 {
					 File imageFile = documentation.getTargetFilename(docImage);
					 pw.printf("<img src=\""+imageFile.getPath()+"\" width=/"+docImage.getDisplayWidth()+"\" height=\""+docImage.getDisplayHeight()+"\">");
				 }
			 }
		 }

	}
	
}
