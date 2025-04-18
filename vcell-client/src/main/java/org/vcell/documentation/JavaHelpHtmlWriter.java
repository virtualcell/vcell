package org.vcell.documentation;

import cbit.util.xml.XmlUtil;
import com.sun.java.help.search.Indexer;
import org.jdom2.Document;
import org.jdom2.Element;
import org.vcell.util.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.List;


public class JavaHelpHtmlWriter implements DocumentWriter {
    private final static String tocHTMLFileName = "VCellHelpTOC.html";
    private final static String helpSetFileName = "HelpSet.hs";
    private final static String javaHelp_helpSearchConfigFile = "helpSearchConfig.txt";
    private final static String helpSearchFolderName = "JavaHelpSearch";
    private final static String mapFileName = "Map.jhm";
    private final static String definitionFilePath = "topics/ch_9/Appendix/";


    private final File docSourceDir;
    private final File docTargetDir;
    private final String tocFileName;

    public JavaHelpHtmlWriter(File docSourceDir, File docTargetDir, String tocFileName) {
        this.docSourceDir = docSourceDir;
        this.docTargetDir = docTargetDir;
        this.tocFileName = tocFileName;
    }

    private void writePages(Documentation documentation) {
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

    private void writeHTML(Documentation documentation, DocumentPage documentPage, File htmlFile) throws Exception
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

    private void printComponent(Documentation documentation, DocTextComponent docComp, File directory, PrintWriter pw, File sourceHtmlFile) throws Exception {
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
                    if(docLink.getTarget().equals(DocumentCompiler.definitionTarget))
                    {
                        File workingDefinitionDir= new File(docTargetDir, definitionFilePath);
                        File htmlFile = new File(workingDefinitionDir, DocumentCompiler.definitionTarget+".html");
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
                pw.println("<img style=\"clear: left; float: none\" src=\""+relativePathToTarget+"\""+" width=\"" + targetImage.getDisplayWidth() + "\" height=\"" + targetImage.getDisplayHeight()+"\">");
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

    private void writeDefinitions(Documentation documentation) {
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

    private void copyHelpSet() throws IOException {
        //FileUtils.copyFile(new File(docSourceDir, helpSetFileName),new File(docTargetDir, helpSetFileName));
        FileUtils.copyFile(new File(docSourceDir, helpSetFileName), new File(docTargetDir, helpSetFileName), true, true, 4 * 1024);
    }

    private void copyTableOfContents() throws IOException {
        // copy the Table of Contents unchanged from the source to the target directory.
        FileUtils.copyFile(new File(docSourceDir, tocFileName),new File(docTargetDir, tocFileName));
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

    private void generateHelpMap(Documentation documentation) throws Exception
    {
        File mapFile =  new File(docTargetDir, mapFileName);

        try{
            Element mapElement = new Element(VCellDocTags.map_tag);
            mapElement.setAttribute(VCellDocTags.version_tag, "1.0");
            //add toplevelfolder element
            Element topLevelElement = new Element(VCellDocTags.mapID_tag);
            topLevelElement.setAttribute(VCellDocTags.target_attr, "toplevelfolder");
            topLevelElement.setAttribute(VCellDocTags.url_attr, "topics/image/vcell.gif");
            mapElement.addContent(topLevelElement);
            //add doc html files
            for (DocumentPage documentPage : documentation.getDocumentPages()) {
                String fileNameNoExt = documentPage.getTemplateFile().getName().replace(".xml","");
                Element mapIDElement = new Element(VCellDocTags.mapID_tag);
                mapIDElement.setAttribute(VCellDocTags.target_attr, fileNameNoExt);
                File targetHtmlFile = getTargetFile(documentPage.getTemplateFile());
                targetHtmlFile = new File(targetHtmlFile.getPath().replace(".xml",".html"));
                mapIDElement.setAttribute(VCellDocTags.url_attr, getHelpRelativePath(docTargetDir, targetHtmlFile));
                mapElement.addContent(mapIDElement);
            }
            //add definitions to map
            Element definitionElement = new Element(VCellDocTags.mapID_tag);
            definitionElement.setAttribute(VCellDocTags.target_attr, DocumentCompiler.definitionTarget);
            definitionElement.setAttribute(VCellDocTags.url_attr, definitionFilePath /*+ File.separator*/ + DocumentCompiler.definitionTarget+".html");
            mapElement.addContent(definitionElement);
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


    private void generateWebIndexHtml(Documentation documentation) throws Exception
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

        //System.out.println("Calling buildHtmlIndex");
        File htmlTOCFile=new File(docTargetDir,tocHTMLFileName);
        PrintWriter tocPrintWriter = new PrintWriter(htmlTOCFile);
        tocPrintWriter.println("<h2>VCell Desktop Help</h2>");
        tocPrintWriter.println("<ul>");
        buildIndexHtml(documentation, root,0,tocPrintWriter);
        tocPrintWriter.println("</ul>");
        tocPrintWriter.close();
    }


    private void buildIndexHtml(Documentation documentation, Element element, int level,PrintWriter tocPrintWriter) {
        String indent = " ".repeat(4*level);
        String bigger_indent = " ".repeat(4*level + 2);
        String page_content = null;
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
                    page_content = "<a href=\""+pathString+"\">"+linkText+"</a>";
                }
            }
        }else if (element.getName().equals(VCellDocTags.toc_tag)){
            // do nothing
        }else{
            throw new RuntimeException("unexpecteded element '"+element.getName()+"' in table of contents");
        }

        @SuppressWarnings("unchecked")
        List<Element> children = element.getChildren(VCellDocTags.tocitem_tag);
        if (page_content != null) {
            if (children.isEmpty()) {
                tocPrintWriter.println(indent + "<li>" + page_content + "</li>");
            } else {
                tocPrintWriter.println(indent + "<li>");
                tocPrintWriter.println(bigger_indent + page_content);
                tocPrintWriter.println(bigger_indent + "<ul>");
                for (Element tocItemElement : children){
                    buildIndexHtml(documentation, tocItemElement, level+1,tocPrintWriter);
                }
                tocPrintWriter.println(bigger_indent + "</ul>");
                tocPrintWriter.println(indent + "</li>");
            }
        } else {
            for (Element tocItemElement : children){
                buildIndexHtml(documentation, tocItemElement, level,tocPrintWriter);
            }
        }
    }


    private void generateHelpSearch() throws Exception {
        File helpSearchDir = new File(docTargetDir, helpSearchFolderName);
        File topicsDir = new File(docTargetDir, "topics");
        if (helpSearchDir.exists()) {
            if (!helpSearchDir.isDirectory()) {
                helpSearchDir.delete();
            } else {
                for (File file: helpSearchDir.listFiles()) {
                    file.delete();
                }
            }
        } else {
            helpSearchDir.mkdirs();
        }

        //Write indexer config file, removes path prefix to make indexed items not dependent of original index file locations
        File helpSearchConfigFullPath = new File(docSourceDir,javaHelp_helpSearchConfigFile);
        try (FileWriter fw = new FileWriter(helpSearchConfigFullPath)) {
            fw.write("IndexRemove "+docTargetDir+File.separator);
        }


        Indexer indexer = new Indexer();
//		indexer.compile(new String[]{"-logfile", "indexer.log", "-c", "UserDocumentation/originalXML/helpSearchConfig.txt", "-db", docTargetDir + File.separator + helpSearchFolderName, docTargetDir + File.separator + "topics"});//javahelpsearch generated under vcell root
        indexer.compile(new String[]{"-c", helpSearchConfigFullPath.getAbsolutePath(), "-db", helpSearchDir.toString(), topicsDir.toString()});//javahelpsearch generated under vcell root
    }


    private File getTargetFile(File sourceFile){
        File targetDir = new File(sourceFile.getParentFile().getPath().replace(docSourceDir.getPath(), docTargetDir.getPath()));
        return new File(targetDir, sourceFile.getName());
    }

    private static String getHelpRelativePath(File sourceDir, File targetFile) {
        return Paths.get(sourceDir.getPath()).relativize(Paths.get(targetFile.getPath())).toString();
    }

    @Override
    public void writeFiles(Documentation documentation) throws Exception {
        writeDefinitions(documentation);
        writePages(documentation);

        copyTableOfContents();
        copyHelpSet();

        generateHelpMap(documentation);
        generateHelpSearch();

        generateWebIndexHtml(documentation);
    }
}
