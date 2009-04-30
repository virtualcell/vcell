package cbit.vcell.xml;

/**
 * Insert the type's description here.
 * Creation date: (6/13/2004 1:37:46 PM)
 * @author: Anuradha Lakshminarayana
 */
public class XMLInfo implements cbit.vcell.document.VCDocumentInfo{
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
public cbit.sql.Version getVersion() {
	// throw new RuntimeException("Not yet implemented!!");
	return new cbit.sql.Version("DummyVersion", new org.vcell.util.document.User("anu", new org.vcell.util.document.KeyValue("123")));
}
/**
 * Insert the method's description here.
 * Creation date: (6/13/2004 1:50:07 PM)
 * @return java.lang.String
 */
public java.lang.String getXmlString() {
	return xmlString;
}
}
