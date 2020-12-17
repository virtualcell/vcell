package org.jlibsedml.validation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jdom.Document;
import org.jlibsedml.SEDMLDocument;
import org.jlibsedml.SedML;
import org.jlibsedml.SedMLError;
import org.jlibsedml.SedMLError.ERROR_SEVERITY;
import org.jlibsedml.XMLException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Validates against cross-references, model IDs and math statements using
 * undeclared variables.
 * 
 * @author radams
 *
 */
public class SchematronValidator extends AbstractDocumentValidator {
    private static final String SVRL_NS_PREFIX = "svrl";
    private static final String SCHEMATRON_NS_URI = "http://purl.oclc.org/dsdl/svrl";
    private SedML sedml;
    XPathFactory xpf = XPathFactory.newInstance();

    public SchematronValidator(Document doc, SedML sedml) {
        super(doc);
        this.sedml = sedml;
    }

    public List<SedMLError> validate() throws XMLException {

        // first of all, get Validation report from Schematron stylesheets.
        SEDMLDocument seddoc = new SEDMLDocument(sedml);
        List<SedMLError> rc = new ArrayList<SedMLError>();
        String docAsString = seddoc.writeDocumentToString();
        String schematron = getSchematronXSL();
        InputStream is2 = SchematronValidator.class.getClassLoader()
                .getResourceAsStream(schematron);
        TransformerFactory tf = TransformerFactory.newInstance();
        StreamSource ss = new StreamSource(is2);
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer(ss);
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        StreamResult target = new StreamResult(baos);
        try {
            transformer.transform(new StreamSource(new StringReader(docAsString)), target);
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // now use XPath to get the failed asserts, and from the location
        // XPaths,
        // get the SEDML element. From there, identify the line number in the
        // document.
        String validationReportXML = new String(baos.toByteArray());
        NodeList nodes = getFailedAssertNodesFromReport(validationReportXML);
        for (int i = 0; i < nodes.getLength(); i++) {
            NamedNodeMap nnm = nodes.item(i).getAttributes();
            String locationInSEDMLXPath = nnm.getNamedItem("location")
                    .getNodeValue();
            NodeList sedNodes = getSedmlNodes(locationInSEDMLXPath, docAsString);
            if (sedNodes.getLength() > 0) {
                Node sedmlNode = sedNodes.item(0);
                String msgText = getMessageFromAssertFailedNode(nodes, i);
                if (sedmlNode.getAttributes().getNamedItem("id") != null) {
                    int num = getLineNumber(sedmlNode);
                    rc.add(new SedMLError(num, msgText, ERROR_SEVERITY.ERROR));
                } else if (sedmlNode.getLocalName().equals("ci")
                        || sedmlNode.getLocalName().equals("cn")) {
                    rc.add(new SedMLError(0, nodes.item(i).getChildNodes()
                            .item(1).getTextContent(), ERROR_SEVERITY.ERROR));
                }
            }
        }
        return rc;
    }

    private String getSchematronXSL() {
        if (sedml.isL1V1()) {
            return "validatorl1v1.xsl";
        } else if (sedml.isL1V2()) {
            return "validatorl1v2.xsl";
        } else {
        	System.out.println("Unsupported version, import may fail");
        	return "validatorl1v2.xsl";
//            throw new UnsupportedOperationException(MessageFormat.format(
//                    "Invalid level and version -  {0}-{1}", sedml.getLevel(),
//                    sedml.getVersion()));
        }
    }

    String getMessageFromAssertFailedNode(NodeList nodes, int i) {
        NodeList nl = nodes.item(i).getChildNodes();
        String msgText = "";
        for (int j = 0; j < nl.getLength(); j++) {
            if (nl.item(j).getNodeType() == Node.ELEMENT_NODE) {
                msgText = nl.item(j).getTextContent();
                break;
            }
        }
        return msgText;
    }

    int getLineNumber(Node sedmlNode) {
        LineFinderUtil util = new LineFinderUtil();
        int num = util.getLineForElement(sedmlNode.getLocalName(), sedmlNode
                .getAttributes().getNamedItem("id").getNodeValue(), getDoc());
        return num;
    }

    private NodeList getSedmlNodes(String locationInSEDMLXPath, String st) {
        org.w3c.dom.Document doc = getXMLDocumentFromModelString(st);

        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new NamespaceContext() {
            public Iterator getPrefixes(String namespaceURI) {
                return Arrays.asList(new String[] { SVRL_NS_PREFIX })
                        .iterator();
            }

            public String getPrefix(String namespaceURI) {
                if (namespaceURI.equals(SCHEMATRON_NS_URI))
                    return SVRL_NS_PREFIX;
                else
                    return "";
            }

            public String getNamespaceURI(String prefix) {
                if (prefix.equals(SVRL_NS_PREFIX))
                    return SCHEMATRON_NS_URI;
                else
                    return "";
            }
        });
        try {
            XPathExpression expr = xpath.compile(locationInSEDMLXPath);
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            return nodes;
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    NodeList getFailedAssertNodesFromReport(String validatorXSL) {
        org.w3c.dom.Document doc = getXMLDocumentFromModelString(validatorXSL);

        String failedAssertIdentifier = "//svrl:failed-assert";

        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new NamespaceContext() {
            public Iterator getPrefixes(String namespaceURI) {
                return Arrays.asList(new String[] { SVRL_NS_PREFIX })
                        .iterator();
            }

            public String getPrefix(String namespaceURI) {
                if (namespaceURI.equals(SCHEMATRON_NS_URI))
                    return SVRL_NS_PREFIX;
                else
                    return "";
            }

            public String getNamespaceURI(String prefix) {
                if (prefix.equals(SVRL_NS_PREFIX))
                    return SCHEMATRON_NS_URI;
                else
                    return "";
            }
        });
        try {
            XPathExpression expr = xpath.compile(failedAssertIdentifier);
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            return nodes;

        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    static org.w3c.dom.Document getXMLDocumentFromModelString(
            final String originalModel) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setNamespaceAware(true); // never forget this!
            DocumentBuilder builder = factory.newDocumentBuilder();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Writer out = new OutputStreamWriter(bos, "UTF8");
            out.write(originalModel);
            out.close();
            bos.close();

            org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(
                    bos.toByteArray()));
            return doc;
        } catch (Exception e) {
            return null;
        }
    }
}
