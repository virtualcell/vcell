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

import cbit.util.xml.XmlUtil;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlParseException;

public class DocumentCompiler {

	public final static String VCELL_DOC_HTML_FILE_EXT = ".html";
	public final static String xmlFile = "";
	public final static String WorkingDir = "UserDocumentation\\doc\\topic\\chapter_1\\";
	//test have two html pages and they point to each other, in addion, xmlFile1 has an image.
	public static void main(String[] args) {
		try {
			DocumentCompiler docCompiler = new DocumentCompiler();
			
			File xmlFile1 = new File(WorkingDir + "pageTest1.xml");
			File xmlFile2 = new File(WorkingDir + "pageTest2.xml");
//			File xmlFile = new File("C:\\mm.xml");
			DocumentPage documentPage1 = docCompiler.readTemplateFile(xmlFile1);
			Documentation documentation = new Documentation();
			documentation.add(documentPage1);
			
			DocumentPage documentPage2 = docCompiler.readTemplateFile(xmlFile2);
			documentation.add(documentPage2);
			
			DocumentImage documentImg1 = new DocumentImage(new File("vcell.gif"), " ", 64,64,64,64);
			documentation.add(documentImg1);
			
			File htmlFile1 = documentation.getTargetFilename(documentPage1);
			docCompiler.writeHTML(documentation,documentPage1,htmlFile1);
			File htmlFile2 = documentation.getTargetFilename(documentPage2);
			docCompiler.writeHTML(documentation,documentPage2,htmlFile2);
		}catch (Throwable e){
			e.printStackTrace(System.out);
		}
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
					throw new XmlParseException("unexpected element "+childElement.getName());
				}
			}
		}
		return docTextComponents;
	}
			
			
	private void writeHTML(Documentation documentation, DocumentPage documentPage, File file) throws Exception
	{
		String htmlFileName = WorkingDir + file.getPath();
		PrintWriter pw = null;
		try {
			 pw = new PrintWriter(htmlFileName);
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
					 throw new RuntimeException("cannot find the page with target "+docLink.getTarget());
				 }
				 pw.printf("<a href=\""+documentation.getTargetFilename(docPage)+"\">");
				 pw.printf(docLink.getText());
				 pw.printf("</a>");
			 }else if (docComp instanceof DocImageReference){
				 DocImageReference imageReference = (DocImageReference)docComp;
				 DocumentImage docImage = documentation.getDocumentImage(imageReference);
				 if (docImage==null){
					 throw new RuntimeException("cannot find the image with target "+imageReference.getImageTarget());
				 }
				 File imageFile = documentation.getTargetFilename(docImage);
				 pw.printf("<img src=\""+imageFile.getPath()+"\" width=/"+docImage.getDisplayWidth()+"\" height=\""+docImage.getDisplayHeight()+"\">");
			 }
		 }

	}
	
}
