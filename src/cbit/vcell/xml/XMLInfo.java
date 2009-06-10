package cbit.vcell.xml;

import org.vcell.util.document.Version;
import org.vcell.util.document.VersionableType;

/**
 * Insert the type's description here.
 * Creation date: (6/13/2004 1:37:46 PM)
 * @author: Anuradha Lakshminarayana
 */
public class XMLInfo implements org.vcell.util.document.VCDocumentInfo{
	private String xmlString = null;
/**
 * XMLInfo constructor comment.
 */
public XMLInfo(String newXMLString) {
	super();
	xmlString = newXMLString;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.Version
 */
public Version getVersion() {
	// throw new RuntimeException("Not yet implemented!!");
	return new Version("DummyVersion", new org.vcell.util.document.User("anu", new org.vcell.util.document.KeyValue("123")));
}
/**
 * Insert the method's description here.
 * Creation date: (6/13/2004 1:50:07 PM)
 * @return java.lang.String
 */
public String getXmlString() {
	return xmlString;
}
public VersionableType getVersionType() {	
	return null;
}
}
