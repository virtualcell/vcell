package org.vcell.util.document;
import java.io.*;

public interface VCDocumentInfo extends VersionInfo {

/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:24:41 PM)
 * @return boolean
 * @param object java.lang.Object
 */
boolean equals(Object object);


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Version
 */
Version getVersion();


/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:28:06 PM)
 * @return int
 */
int hashCode();


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String toString();
}