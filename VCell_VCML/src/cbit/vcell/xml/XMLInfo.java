package cbit.vcell.xml;

/**
 * Insert the type's description here.
 * Creation date: (6/13/2004 1:37:46 PM)
 * @author: Anuradha Lakshminarayana
 */
public class XMLInfo implements cbit.util.document.VCDocumentInfo{
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
public cbit.util.document.Version getVersion() {
	// throw new RuntimeException("Not yet implemented!!");
	return new cbit.util.document.Version("DummyVersion", new cbit.util.document.User("anu", new cbit.util.document.KeyValue("123")));
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
