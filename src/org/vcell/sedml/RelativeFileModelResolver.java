package org.vcell.sedml;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jlibsedml.execution.IModelResolver;
import org.w3c.dom.Document;

/**
 * This resolver will resolve relative URIs to a predetermined "current" prefix path.<br/>
 * For example:<br/>
 * <ul>
 * <li>"./Myfile.xml"
 * </ul>
 */
public class RelativeFileModelResolver implements IModelResolver {

    String prefixPath;
    
    @SuppressWarnings("unused")
    private RelativeFileModelResolver() {   // should be never called
        throw new UnsupportedOperationException();
    }
    public RelativeFileModelResolver(String prefixPath) {
        super();
        this.prefixPath = prefixPath;
    }
    /**
     * Builds a full path to the XML based model, reads it and returns it as String. 
     * The current directory must be provided in the constructor.<br/>
     * 
     * @param modelURI
     *            A non-null {@link URI}.
     * @return The model String or <code>null</code> if could not be resolved,
     *         or the file is not XML content.
     */
    public String getModelXMLFor(URI modelURI) {
        
        try {
            String path = modelURI.getPath();
            if(path.startsWith("./")) {
                path = prefixPath + path.substring(2);
            } else {
                return null;
            }
            File f = new File(path);
            if (!f.exists() && !f.canRead()) {
                return null;
            }
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.parse(f);
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance()
                    .newTransformer();
            xformer.transform(source, result);
            return result.getWriter().toString();

        } catch (Exception e) {
            return null;
        }
    }
}
