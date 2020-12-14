package org.jlibsedml.validation;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jlibsedml.SedMLError;
import org.jlibsedml.SedMLError.ERROR_SEVERITY;
import org.jlibsedml.XMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class SchemaValidatorImpl {
    Logger log = LoggerFactory.getLogger(SchemaValidatorImpl.class);
    static final String SEDML_L1_V1_SCHEMA = "schema.xsd";
    static final String SEDML_L1_V2_SCHEMA = "sed-ml-L1-V2.xsd";

    static final String SBML_MATHML_SCHEMA = "sedml-mathml.xsd";

    public void validateSedMLString(final List<SedMLError> errors,
            String xmlAsString) throws XMLException {

        SchemaFactory factory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        InputStream math = loadSchema(SBML_MATHML_SCHEMA);
        InputStream sed = null;
        try {
            String sedmlSchema = getSchema(xmlAsString);
            sed = loadSchema(sedmlSchema);

            StreamSource s1 = new StreamSource(math);
            StreamSource s2 = new StreamSource(sed);

            Schema schema = null;
            try {
                schema = factory.newSchema(new Source[] { s1, s2 });
            } catch (SAXException e1) {
                log.error(e1.getMessage());
                throw new XMLException("Error parsing schema files", e1);
            }
            Validator xmlValidator = schema.newValidator();
            xmlValidator.setErrorHandler(new DefaultXMLParsingErrorHandler(
                    errors));
            // at last perform validation:
            try {
                xmlValidator.validate(new StreamSource(new StringReader(
                        xmlAsString)));
            } catch (SAXException e) {
                throw new XMLException("Error parsing XML", e);
            } catch (IOException e) {
                throw new XMLException("Error parsing XML", e);
            }
        } catch (JDOMException | IOException e2) {
            throw new XMLException("Couldn't load schema:", e2);
        } finally {
            closeQuietly(sed);
            closeQuietly(math);
        }
    }

    private String getSchema(String xmlAsString) throws JDOMException,
            IOException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xmlAsString));
        Element sedRoot = doc.getRootElement();
        // TODO handle null values of these and throw IllArgException
        String level = sedRoot.getAttribute("level").getValue();
        String version = sedRoot.getAttribute("version").getValue();
        if (level.equals("1") && version.equals("1")) {
            return SEDML_L1_V1_SCHEMA;
        } else if (level.equals("1") && version.equals("2")) {
            return SEDML_L1_V2_SCHEMA;
        } else {
            throw new IllegalArgumentException(
                    "Invalid level/version combingation - must be 1-1 or 1-2 but was "
                            + level + "," + version);
        }
    }

    class DefaultXMLParsingErrorHandler implements ErrorHandler {
        private List<SedMLError> errors;

        public DefaultXMLParsingErrorHandler(List<SedMLError> errors) {
            this.errors = errors;
        }

        public void warning(SAXParseException exception) throws SAXException {
            errors.add(new SedMLError(exception.getLineNumber(), exception
                    .getMessage(), ERROR_SEVERITY.WARNING));
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            errors.add(new SedMLError(exception.getLineNumber(), exception
                    .getMessage(), ERROR_SEVERITY.FATAL));
        }

        public void error(SAXParseException exception) throws SAXException {
            errors.add(new SedMLError(exception.getLineNumber(), exception
                    .getMessage(), ERROR_SEVERITY.ERROR));
        }
    }

    private InputStream loadSchema(final String schema) {
        InputStream is2 = SchemaValidatorImpl.class.getClassLoader()
                .getResourceAsStream(schema);
        return is2;
    }
}
