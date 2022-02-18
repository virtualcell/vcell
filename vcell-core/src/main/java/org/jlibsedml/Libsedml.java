package org.jlibsedml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jlibsedml.mathsymbols.SedMLSymbolFactory;
import org.jlibsedml.validation.RawContentsSchemaValidator;
import org.jmathml.ASTNode;
import org.jmathml.ASTRootNode;
import org.jmathml.MathMLReader;
import org.jmathml.SymbolRegistry;
import org.jmathml.TextToASTNodeMathParser2;

/**
 * Provides an entry point to the SedML object model. To read a SED-ML file,
 * 
 * <pre>
 * SEDMLDocument doc = Libsedml.readDocument(myFile);
 * // get errors
 * List&lt;SedMLError&gt; errors = doc.getErrors();
 * 
 * // Alternatively, if you're creating a SED-ML document from scratch, do
 * doc = LibsedML.createDocument();
 * // get model
 * Sedml model = doc.getSedMLModel();
 * 
 * // apply changes to your model using the SED-ML API
 * 
 * // write document back to file
 * doc.writeDocument(myFile);
 * 
 * // to write a .sedx archive file with the model file included:
 * 
 * List&lt;IModelFile&gt; models = new ArrayList&lt;IModelFile&gt;();
 * models.add(new FileModelContent(myModelFile));
 * ArchiveComponents comps = new ArchiveComponents(models, doc);
 * 
 * byte[] zippedBytes = LibSedml.writeMiaseAchive(comps);
 * 
 * // persist your zipped archive
 * </pre>
 * 
 * @author Richard Adams
 * @since 1.0
 * 
 */
public class Libsedml {

    private Libsedml() {
    }

    /**
     * Reader for creating a SEDML document from a file.
     * 
     * @param file
     *            A non-null, readable file of a SED-ML xml file
     * @return A non null {@link SEDMLDocument}
     * @throws XMLException
     *             for parsing errors, or if file doesn't exist or is not
     *             readable
     */
    public static SEDMLDocument readDocument(File file) throws XMLException {
        String fileContents = null;
        try {
            fileContents = FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new XMLException("Couldn't read file " + file.getName(), e);
        }
        SEDMLDocument sedmlDoc = readDocumentFromString(fileContents);
        return sedmlDoc;
    }
    
    /**
     * Reads SEDML from an input stream
     * @param is
     * @param encoding - the character encoding. Defaults to default encoding if this is <code>null</code>.
     * @return A {@link SEDMLDocument}
     * @throws XMLException
     * @throws IOException if stream cannot be read
     * @since 2.2.3
     */
    public static SEDMLDocument readDocument(InputStream is, String encoding) throws XMLException, IOException {
        if(encoding == null) {
            encoding = Charset.defaultCharset().name();
        }     
        List<String> lines = IOUtils.readLines(is, encoding);
        String content =  StringUtils.join(lines, "\n");
        return readDocumentFromString(content);
    }

    /**
     * Boolean test for whether a file is a SED-ML file or not.
     * 
     * @param file
     *            A non-null, readable file.
     * @return <code>true</code> if this is a SED-ML file (i.e., a readable XML
     *         file with the SED-ML namespace). If the XML cannot be parsed, or
     *         the root element namespace is not that of SED-ML, returns
     *         <code>false</code>.
     */
    public static boolean isSEDML(File file) {
        try {
            return isSEDML(new FileInputStream(file));
        } catch (FileNotFoundException e1) {
            return false;
        }
    }

    /**
     * Boolean test for whether an <code>InputStream</code> is SED-ML or not.
     * 
     * @param inputStream
     *            A non-null, readable InputStream.
     * @return <code>true</code> if the input stream is to a a SED-ML resource
     *         (i.e., a readable XML with the SED-ML namespace). If the XML
     *         cannot be parsed, or the root element namespace is not SED-ML,
     *         returns <code>false</code>.
     */
    public static boolean isSEDML(InputStream inputStream) {
        Document doc = null;
        try {
            doc = createDocument(inputStream);
        } catch (XMLException e) {
            return false;
        }
        return SEDMLTags.SEDML_L1V1_NS.equalsIgnoreCase(doc.getRootElement()
                .getNamespaceURI()) || SEDMLTags.SEDML_L1V2_NS.equalsIgnoreCase(doc.getRootElement()
                        .getNamespaceURI()) || SEDMLTags.SEDML_L1V3_NS.equalsIgnoreCase(doc.getRootElement()
                                .getNamespaceURI());

    }

    private static Document createDocument(InputStream in) throws XMLException {
        SAXBuilder builder = new SAXBuilder();

        Document doc;
        try {
            doc = builder.build(in);
        } catch (JDOMException e) {
            throw new XMLException("Error reading file", e);
        } catch (IOException e) {
            throw new XMLException("Error reading file", e);
        }
        return doc;
    }

    /**
     * Converts a string representation of a MathML XML structure to a C-style
     * string.
     * 
     * @param mathml
     *            A <code>string</code> of MathML
     * @return A <code>String</code> of C style math syntax, or an error string
     *         if it could not be converted.
     */
    public static String MathMLXMLToText(String mathml) {
        SymbolRegistry.getInstance().addSymbolFactory(new SedMLSymbolFactory());
        String text = MathMLReader.MathMLXMLToText(mathml);
        if (hasExtraSurroundingParentheses(text)) {
            return text.substring(1, text.length() - 1);
        } else {
            return text;
        }
    }

    private static boolean hasExtraSurroundingParentheses(String text) {
        return text.trim().lastIndexOf("(") == 0
                && text.trim().indexOf(")") == (text.length() - 1);
    }

    private static SEDMLDocument buildDocumentFromXMLTree(Document doc,
            List<SedMLError> errs) throws XMLException {
        Element sedRoot = doc.getRootElement();
        SEDMLReader reader = new SEDMLReader();
        try {
            SEDMLElementFactory.getInstance().setStrictCreation(false);
            SedML sedML = reader.getSedDocument(sedRoot);
            SEDMLDocument sedmlDoc = new SEDMLDocument(sedML, errs);
            sedmlDoc.validate();
            return sedmlDoc;
        } finally {
            SEDMLElementFactory.getInstance().setStrictCreation(true);
        }
    }

    /**
     * Creates a new, empty SED-ML document
     * 
     * @return A non-null, empty {@link SEDMLDocument}. This document is not
     *         free of errors after creation, as a valid document contains at
     *         least one task, for example.
     */
    public static SEDMLDocument createDocument() {
        return new SEDMLDocument();
    }

    /**
     * Generates a sedx archive of a SED-ML document and its constituent model
     * files. Within the archive, the SED-ML document will called 'sedml.xml'
     * 
     * @param components
     *            An {@link ArchiveComponents} object.
     * @param sedmlName
     *            A name for the SEDML component of the archive. This should be
     *            the same as the root of the name of the file to which the
     *            archive will be saved. E.g., if <br/>
     *            <code>sedmlName</code> is mysedml, then the entry in the
     *            archive will be : <br/>
     *            <em> mysedml.xml</em> <br/>
     *            and the final .sedx archive should be called
     *            <em>mysedml.sedx</em>
     * @return A byte[] of the zipped contents, or null.
     * @throws XMLException
     *             if any aspect of zipping could not be performed.
     * @deprecated - use OMEX /Combine archive format from now on - the sedx
     *             archive is obsolete.
     */
//    public static byte[] writeSEDMLArchive(final ArchiveComponents components,
//            final String sedmlName) throws XMLException {
//        ZipOutputStream out = null;
//        ByteArrayOutputStream baos = null;
//        try {
//            baos = new ByteArrayOutputStream();
//            out = new ZipOutputStream(baos);
//            for (IModelContent modelFile : components.getModelFiles()) {
//                out.putNextEntry(new ZipEntry(modelFile.getName()));
//                byte[] data = modelFile.getContents().getBytes();
//                out.write(data);
//            }
//            out.putNextEntry(new ZipEntry(sedmlName + ".xml"));
//            String sedmlString = components.getSedmlDocument()
//                    .writeDocumentToString();
//
//            byte[] array = sedmlString.getBytes("UTF-8");
//            ByteArrayInputStream bais = new ByteArrayInputStream(
//                    sedmlString.getBytes("UTF-8"));
//            int bytesRead;
//            while ((bytesRead = bais.read(array)) != -1) {
//                out.write(array, 0, bytesRead);
//            }
//            out.close();
//
//        } catch (Exception e) {
//            throw new XMLException("Error generating SED-ML archive file:", e);
//        } finally {
//            try {
//                if (out != null) {
//                    out.close();
//                }
//            } catch (IOException e) {// ignore
//            }
//        }
//        return baos.toByteArray();
//    }

    /**
     * Reads an archive, which should be in sedx format - i.e., a zip archive
     * containing a SED-ML file and possibly one or more model files. <br/>
     * This method is kept for reading legacy archives, but the SEDML archive
     * format is now superceded by the Combine archive format
     * 
     * @param archive
     *            An {@link InputStream} onto the archive
     * @return A non-null {@link ArchiveComponents} objects.
     * @throws XMLException
     * @throws {@link IllegalArgumentException} if parameter File is null,
     *         unreadable, or does not end in ".sedx".
     * @link http://co.mbine.org/documents/archive
     */
    public static ArchiveComponents readSEDMLArchive(final InputStream archive)
            throws XMLException {
        if (archive == null) {
            throw new IllegalArgumentException();
        }

        ZipInputStream zis = new ZipInputStream(archive);
        ZipEntry entry = null;

        int read;
        List<IModelContent> contents = new ArrayList<IModelContent>();
        List<SEDMLDocument> docs = new ArrayList<SEDMLDocument>();
        try {
            while ((entry = zis.getNextEntry()) != null) {
            	if(entry.getName().endsWith(".rdf")) {
            		continue;		// we skip rdf files, otherwise isSEDML() below fails
            	}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                while ((read = zis.read(buf)) != -1) {
                    baos.write(buf, 0, read);
                }
                if (isSEDML(new ByteArrayInputStream(baos.toByteArray()))) {
                    SEDMLDocument doc = readDocumentFromString(baos.toString());
                    String fullPath = entry.getName();
                    int lastSeparator = Math.max(fullPath.lastIndexOf("\\"), fullPath.lastIndexOf("/")) + 1;
                    String path = fullPath.substring(0, lastSeparator);
                    String name = fullPath.substring(lastSeparator);
                    doc.getSedMLModel().setPathForURI(path);
                    doc.getSedMLModel().setFileName(name);
                    docs.add(doc);
                } else {
                    IModelContent imc = new BaseModelContent(baos.toString(),
                            entry.getName());
                    contents.add(imc);
                }
            }
            return new ArchiveComponents(contents, docs);
        } catch (Exception e) {
            throw new XMLException("Error reading archive", e);
        } finally {
            try {
                zis.close();
            } catch (IOException e) {}// ignore
        }
    }

    /**
     * Builds a SEDMLDocument from an XML string representation of such a
     * document, validating against the appropriate XML schema.
     * 
     * @param sedmlString
     *            An XML string of the SEDML Document.
     * @return A validated SEDMLDocument.
     * @throws XMLException
     *             if the XML was malformed or could not otherwise be parsed.
     */
    public static SEDMLDocument readDocumentFromString(String sedmlString)
            throws XMLException {
        SAXBuilder builder = new SAXBuilder();
        List<SedMLError> errs = new RawContentsSchemaValidator(sedmlString)
                .validate();
        Document doc;
        try {
            doc = builder.build(new StringReader(sedmlString));
        } catch (JDOMException e) {
            throw new XMLException("Error reading file", e);
        } catch (IOException e) {
            throw new XMLException("Error reading file", e);
        }
        SEDMLDocument sedmlDoc = buildDocumentFromXMLTree(doc, errs);
        return sedmlDoc;
    }

    /**
     * Writes out an XML string to a file whose name is specified. Optionally, a
     * UTF-8 character set can be specified
     * @param xmlString
     *            A string of XML to write.
     * @param filename
     *            A relative or absolute file name, that must be writeable to.
     *            It is up to the client to ensure the directories exist.
     * @param bUseUTF8
     *            If <code>true</code>, will write to UTF-8, else will write
     *            using the default character set.
     * @return A <code>File</code> object representing the written-to file
     * @throws IOException
     *             If writing to file did not complete.
     */
    public static File writeXMLStringToFile(String xmlString, String filename,
            boolean bUseUTF8) throws IOException {
        File outputFile = new File(filename);
        OutputStreamWriter fileOSWriter = null;
        if (bUseUTF8) {
            fileOSWriter = new OutputStreamWriter(new FileOutputStream(
                    outputFile), "UTF-8");
        } else {
            fileOSWriter = new OutputStreamWriter(new FileOutputStream(
                    outputFile));
        }
        fileOSWriter.write(xmlString);
        fileOSWriter.flush();
        fileOSWriter.close();
        return outputFile;
    }

    /**
     * Convenience method to parse free text math into an ASTNode. The style of
     * math is a C-style syntax, e.g.,<br/>
     * . The parsing supports standard arithmetical operations and function
     * calls. E.g.,
     * 
     * <ul>
     * <li>4 + 2
     * <li>sin(pi / 2) // or any other trig function
     * <li>log(9,81) // = 2
     * <li>root(4,81) //=3
     * <li>exp(t)
     * </ul>
     * <p>
     * In addition, the strings <code>pi</code>, <code>NaN</code>,
     * <code>infinity</code> and <code>exponentiale</code> are translated into
     * constants.
     * </p>
     * 
     * Functions not supported by this library ( see ASTFunctionType for
     * supported functions ) will be parsed into a function call, which will not
     * be evaluable. E.g., <br/>
     * 
     * <pre>
     * myfunction(a, b)
     * </pre>
     * 
     * can be turned into an ASTNode, but will not be evaluable, unless a symbol
     * definition class is supplied. (See the
     * <code>org.jlibsedml.mathsymbols</code> package for how the SED-ML
     * aggregate functions are supported).
     * 
     * @param math
     *            A <code>String</code> of math.
     * @return An {@link ASTNode}. This will be an {@link ASTRootNode} with the
     *         math nodes as children. E.g., the string
     * 
     *         <pre>
     * 2 * (4 / 5)
     * </pre>
     *         will return the structure:
     *         <pre>
     *  ASTRootNode
     *       ASTimes
     *            ASTCn(2)
     *            ASTDivide
     *                 ASTCn(4)
     *                 ASTCn(5)
     * </pre>
     * 
     *         If <code>math</code> is null or empty, returns <code>null</code>.
     * @throws RuntimeException
     *             if mathString cannot be parsed due to incorrect syntax.
     */
    public static ASTNode parseFormulaString(String math) {
        if (math == null || math.length() == 0) {
            return null;
        }
        ASTRootNode rc = new ASTRootNode();
        new TextToASTNodeMathParser2().parseString(math, rc);
        return rc;
    }
}
