package org.vcell.documentation;

import org.jdom.Element;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.List;


public class HtmlWriter implements DocumentWriter {
    private final static String tocHTMLFileName = "VCellHelpTOC.html";

    private final File docSourceDir;
    private final File docTargetDir;
    private final String definitionXMLFileName;
    private final String definitionFilePath;

    public HtmlWriter(File docSourceDir, File docTargetDir, String definitionXMLFileName, String definitionFilePath) {
        this.docSourceDir = docSourceDir;
        this.docTargetDir = docTargetDir;
        this.definitionXMLFileName = definitionXMLFileName;
        this.definitionFilePath = definitionFilePath;
    }

    public void writePages(Documentation documentation) {
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

    public void writeHTML(Documentation documentation, DocumentPage documentPage, File htmlFile) throws Exception
    {
        try (PrintWriter pw = new PrintWriter(htmlFile)) {
            //start html
            pw.println("<" + VCellDocTags.html_tag + ">");

            //start head
            pw.println("<" + VCellDocTags.html_head_tag + ">");
            //start title
            pw.print("<" + VCellDocTags.html_title_tag + ">");
            pw.print(documentPage.getTitle());                    //html page title, replace when needed
            //end title
            pw.print("</" + VCellDocTags.html_title_tag + ">");
            pw.println();
            //end head
            pw.print("</" + VCellDocTags.html_head_tag + ">");

            //start body
            pw.println("<" + VCellDocTags.html_body_tag + ">");
            //title
            pw.print("<" + VCellDocTags.html_new_line + ">");
            pw.print("<" + VCellDocTags.html_header_tag + ">" + documentPage.getTitle() + "</" + VCellDocTags.html_header_tag + ">");
            pw.print("</" + VCellDocTags.html_new_line + ">");
            pw.println();

            //introduction
            DocSection introSection = documentPage.getIntroduction();
            if (!introSection.getComponents().isEmpty()) {
                pw.print("<" + VCellDocTags.html_new_line + ">");
                printComponent(documentation, introSection, htmlFile.getParentFile(), pw, htmlFile);
                pw.print("</" + VCellDocTags.html_new_line + ">");
                pw.println();
            }
            //appearance
            DocSection appSection = documentPage.getAppearance();
            if (!appSection.getComponents().isEmpty()) {
                pw.print("<" + VCellDocTags.html_new_line + ">");
                printComponent(documentation, appSection, htmlFile.getParentFile(), pw, htmlFile);
                pw.print("</" + VCellDocTags.html_new_line + ">");
                pw.println();
            }
            //operation
            DocSection opSection = documentPage.getOperations();
            if (!opSection.getComponents().isEmpty()) {
                pw.print("<" + VCellDocTags.html_new_line + ">");
                printComponent(documentation, opSection, htmlFile.getParentFile(), pw, htmlFile);
                pw.print("</" + VCellDocTags.html_new_line + ">");
                pw.println();
            }
            //end body
            pw.println("</" + VCellDocTags.html_body_tag + ">");

            //end html
            pw.println("</" + VCellDocTags.html_tag + ">");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new Exception("Exception in " + htmlFile.getAbsolutePath().replace(".html", ".xml") + ".\n" + e.getMessage());
        }
    }

    public void printComponent(Documentation documentation, DocTextComponent docComp, File directory, PrintWriter pw, File sourceHtmlFile) throws Exception {
        if (docComp instanceof DocText text){
            if (text.getBold()) {
                pw.print("<b>" + text.getText() + "</b>");
            } else {
                pw.print(text.getText());
            }
        }else if (docComp instanceof DocLink docLink){
            if(docLink.isWebTarget())
            {
                URL url = new URI(docLink.getTarget()).toURL();
                URLConnection conn = url.openConnection();
                try{
                    conn.connect();
                }catch(Exception e1){
                    System.err.println("Error in xml file:" + sourceHtmlFile.getAbsolutePath().replace(".html", ".xml") + ". Server failed to respond: " + docLink.getTarget()+". It might be a bad URL.");
                }
                pw.print("<a href=\""+docLink.getTarget()+"\">");
            }
            else
            {
                DocumentPage docPage = documentation.getDocumentPage(docLink);
                if (docPage==null){
                    if(docLink.getTarget().equals(definitionXMLFileName.replace(".xml", "")))
                    {
                        File workingDefinitionDir= new File(docTargetDir, definitionFilePath);
                        File htmlFile = new File(workingDefinitionDir, definitionXMLFileName);
                        htmlFile = new File(htmlFile.getPath().replace(".xml",".html"));
                        String relativePathToTarget = getHelpRelativePath(directory, htmlFile);
                        pw.print("<a href=\""+relativePathToTarget+"\">");
                    }
                    else
                    {
                        throw new RuntimeException("reference to document '"+docLink.getTarget()+"' cannot be resolved");
                    }
                }
                else{
                    File htmlFile = getTargetFile(docPage.getTemplateFile());
                    htmlFile = new File(htmlFile.getPath().replace(".xml",".html"));
                    String relativePathToTarget = getHelpRelativePath(directory, htmlFile);
                    pw.print("<a href=\""+relativePathToTarget+"\">");
                }
            }
            pw.print(docLink.getText());
            pw.print("</a>");
        }else if (docComp instanceof DocImageReference imageReference){
            DocumentImage targetImage = documentation.getDocumentImage(imageReference);
            if (targetImage==null){
                throw new RuntimeException("reference to image '"+imageReference+"' cannot be resolved");
            }
            File imageFile = getTargetFile(targetImage.getSourceFile());
            String relativePathToTarget = getHelpRelativePath(directory, imageFile);
            if (!imageReference.isInline()){
                pw.println("<br><br>");
                pw.println("<img align=left src=\""+relativePathToTarget+"\""+" width=\"" + targetImage.getDisplayWidth() + "\" height=\"" + targetImage.getDisplayHeight()+"\">");
            } else {
                pw.println("&nbsp;<img align=left src=\""+relativePathToTarget+"\""+" width=\"" + targetImage.getDisplayWidth() + "\" height=\"" + targetImage.getDisplayHeight()+"\">&nbsp;");
            }
        }else if (docComp instanceof DocDefinitionReference defReference){
            DocumentDefinition targetDef = documentation.getDocumentDefinition(defReference);
            if (targetDef==null){
                throw new RuntimeException("Definition '"+defReference.getDefinitionTarget()+"' does NOT exist.");
            }
            File defXmlFile = getTargetFile(targetDef.getSourceFile());
            File htmlFile = new File(defXmlFile.getPath().replace(".xml",".html"));
            String relativePathToTarget = getHelpRelativePath(directory, htmlFile);
            pw.println("<a href=\""+relativePathToTarget+"#" + targetDef.getTarget()+ "\">");
            pw.print("<i>" + defReference.getText() + "</i></a>");
        }else if (docComp instanceof DocList){
            pw.print("<ul>");
            for (DocTextComponent comp : docComp.getComponents()){
                printComponent(documentation, comp, directory, pw, sourceHtmlFile);
            }
            pw.println("</ul>");
        }else if (docComp instanceof DocParagraph){
            pw.print("<p>");
            for (DocTextComponent comp : docComp.getComponents()){
                printComponent(documentation, comp, directory, pw, sourceHtmlFile);
            }
            pw.println("</p>");
        }else if (docComp instanceof DocListItem){
            pw.print("<li>");
            for (DocTextComponent comp : docComp.getComponents()){
                printComponent(documentation, comp, directory, pw, sourceHtmlFile);
            }
            pw.println("</li>");
        }else if (docComp instanceof DocSection){
            for (DocTextComponent comp : docComp.getComponents()){
                printComponent(documentation, comp, directory, pw, sourceHtmlFile);
            }
        }
    }

    public void buildHtmlIndex(Documentation documentation, Element rootElement) throws IOException {
        File htmlTOCFile=new File(docTargetDir,tocHTMLFileName);
        PrintWriter tocPrintWriter = new PrintWriter(htmlTOCFile);
        buildIndexHtml(documentation, rootElement,0,tocPrintWriter);
        tocPrintWriter.close();
    }

    @Override
    public void writeDefinitions(Documentation documentation) {
        if(documentation.getDocumentDefinitions() != null && documentation.getDocumentDefinitions().length > 0)
        {
            DocumentDefinition[] documentDefinitions = documentation.getDocumentDefinitions();
            File xmlFile = getTargetFile(documentDefinitions[0].getSourceFile());
            File htmlFile = new File(xmlFile.getPath().replace(".xml",".html"));
            try {
                writeDefinitionHTML(documentDefinitions,htmlFile);
            }catch (Exception e){
                e.printStackTrace(System.out);
                throw new RuntimeException("failed to parse document "+documentDefinitions[0].getSourceFile().getPath());
            }
        }

    }

    private void writeDefinitionHTML(DocumentDefinition[] docDefs, File htmlFile) throws Exception
    {
        try (PrintWriter pw = new PrintWriter(htmlFile)) {

            //start html
            pw.println("<" + VCellDocTags.html_tag + ">");
            //start head
            pw.println("<" + VCellDocTags.html_head_tag + ">");
            pw.println("<h2> Virtual Cell Definitions </h2>");
            //start title
            pw.print("<" + VCellDocTags.html_title_tag + ">");
            //end title
            pw.print("</" + VCellDocTags.html_title_tag + ">");
            pw.println();
            //end head
            pw.print("</" + VCellDocTags.html_head_tag + ">");
            //start body
            pw.println("<" + VCellDocTags.html_body_tag + ">");
            for (DocumentDefinition docDef : docDefs) {
                pw.println();
                pw.print("<p>");
                pw.print("<a name = \"" + docDef.getTarget() + "\"><b>" + docDef.getLabel() + "</b></a> <br>");
                pw.println();
                pw.print(docDef.getText());
                pw.print("</p>");
            }
            //end body
            pw.println("</" + VCellDocTags.html_body_tag + ">");
            //end html
            pw.println("</" + VCellDocTags.html_tag + ">");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw e;
        }
    }



    private void buildIndexHtml(Documentation documentation, Element element, int level,PrintWriter tocPrintWriter) {
        if (element.getName().equals(VCellDocTags.tocitem_tag)){
            String target = element.getAttributeValue(VCellDocTags.target_attr);
            if (target!=null){
                // first look for a documentPage as the target, else check if it is a Definition page.
                DocumentPage documentPage = documentation.getDocumentPage(new DocLink(target,target));
                if (documentPage!=null){
                    String linkText = documentPage.getTitle();
                    File targetHtmlFile = getTargetFile(documentPage.getTemplateFile());
                    targetHtmlFile = new File(targetHtmlFile.getPath().replace(".xml",".html"));
                    String pathString = getHelpRelativePath(docTargetDir, targetHtmlFile);
                    System.out.print("<br>\n");
                    tocPrintWriter.print("<br>\n");
                    for (int i=1; i<level; ++i) {
                        System.out.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        tocPrintWriter.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    }
                    System.out.println("<a href=\""+pathString+"\">"+linkText+"</a>");
                    tocPrintWriter.println("<a href=\""+pathString+"\">"+linkText+"</a>");
                }
            }
        }else if (element.getName().equals(VCellDocTags.toc_tag)){
            // do nothing
        }else{
            throw new RuntimeException("unexpecteded element '"+element.getName()+"' in table of contents");
        }
        @SuppressWarnings("unchecked")
        List<Element> children = element.getChildren(VCellDocTags.tocitem_tag);
        for (Element tocItemElement : children){
            buildIndexHtml(documentation, tocItemElement, level+1,tocPrintWriter);
        }
    }



    private File getTargetFile(File sourceFile){
        return new File(getTargetDirectory(sourceFile.getParentFile()), sourceFile.getName());
    }

    private File getTargetDirectory(File sourceDir){
        return new File(sourceDir.getPath().replace(docSourceDir.getPath(),docTargetDir.getPath()));
    }

    private static String getHelpRelativePath(File sourceDir, File targetFile) {
        return Paths.get(sourceDir.getPath()).relativize(Paths.get(targetFile.getPath())).toString();
    }

}
