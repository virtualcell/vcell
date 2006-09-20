package cbit.util.xml;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Generic implementation for handling errors generated when validating an XML file against its schema.
 * Creation date: (10/9/2003 4:14:38 PM)
 * @author: Rashad Badrawi
 */
public class GenericXMLErrorHandler implements ErrorHandler {

	private StringBuffer logBuffer = new StringBuffer();

/**
 * GenericXMLErrorHandler constructor comment.
 */
protected GenericXMLErrorHandler() {
	super();
}


    public void error(SAXParseException e) {

       logBuffer.append("SAXParseException: Error: "+e.getMessage());
       logBuffer.append("\n");
       logBuffer.append("\tCol number: " + e.getColumnNumber() + " Line number: " + e.getLineNumber() +
	       				  " PublicID: " + e.getPublicId() + " SystemID: " + e.getSystemId());
       logBuffer.append("\n");
    }


    public void fatalError(SAXParseException e) {

       logBuffer.append("SAXParseException: FatalError: "+e.getMessage());
       logBuffer.append("\n");
       logBuffer.append("\tCol number: " + e.getColumnNumber() + " Line number: " + e.getLineNumber() +
	       				  " PublicID: " + e.getPublicId() + " SystemID: " + e.getSystemId());
       logBuffer.append("\n");
    }


	protected String getErrorLog() {

		return logBuffer.toString();
	}


    public void warning(SAXParseException e) {

       logBuffer.append("SAXParseException: Warning: "+e.getMessage());
       logBuffer.append("\n");
       logBuffer.append("\tCol number: " + e.getColumnNumber() + " Line number: " + e.getLineNumber() +
	       				  " PublicID: " + e.getPublicId() + " SystemID: " + e.getSystemId());
       logBuffer.append("\n");
    }
}