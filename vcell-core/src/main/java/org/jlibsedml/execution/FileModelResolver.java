package org.jlibsedml.execution;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * This resolver will resolve relative URIs with no scheme, i.e., relative URIs
 * to files.<br/>
 * For example:<br/>
 * <ul>
 * <li>"Myfile.xml"
 * <li>a/b/c/d.xml
 * </ul>
 * URIs specified with the 'file' schema will not be resolved unless the path is
 * absolute.
 * 
 * @author radams
 *
 */
public class FileModelResolver implements IModelResolver {

    /**
     * Makes a best effort to read an XML based model and return it as a String
     * based upon the URI. It is up to the client to ensure that the model
     * returned is the correct model for simulation or change operations.
     * 
     * @param modelURI
     *            A non-null {@link URI}.
     * @return The model String or <code>null</code> if could not be resolved,
     *         or the file is not XML content.
     * @throws IllegalArgumentException
     *             if <code>modelURI</code> is null.
     */
    public String getModelXMLFor(URI modelURI) {
        String path = null;
        if (modelURI.getAuthority() == null && modelURI.getScheme() == null
                && modelURI.getHost() == null) {
            path = modelURI.getPath();

        } else if (modelURI.getAuthority() == null
                && "file".equals(modelURI.getScheme())
                && modelURI.getHost() == null) {
            path = modelURI.getPath();

        } else {
            return null;
        }
        File f = new File(path);
        if (!f.exists() && !f.canRead()) {
            return null;
        }
        try {
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
