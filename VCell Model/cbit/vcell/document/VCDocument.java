package cbit.vcell.document;

import cbit.sql.*;
import cbit.util.Version;
/**
 * Insert the type's description here.
 * Creation date: (5/6/2004 12:53:51 PM)
 * @author: Ion Moraru
 */
public interface VCDocument extends java.io.Serializable, cbit.util.Matchable {
	// document types
	public static final int BIOMODEL_DOC = 0;
	public static final int MATHMODEL_DOC = 1;
	public static final int GEOMETRY_DOC = 2;
	public static final int XML_DOC = 3;
/**
 * Insert the method's description here.
 * Creation date: (5/17/2004 1:03:55 PM)
 * @return java.lang.String
 */
String getDescription();
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:10:46 PM)
 * @return int
 */
int getDocumentType();
/**
 * Insert the method's description here.
 * Creation date: (5/17/2004 1:03:55 PM)
 * @return java.lang.String
 */
String getName();
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 2:18:55 PM)
 * @return cbit.sql.Version
 */
Version getVersion();
/**
 * Insert the method's description here.
 * Creation date: (9/16/2004 12:33:13 PM)
 */
void refreshDependencies();
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 1:07:43 AM)
 * @param newName java.lang.String
 */
void setDescription(String description) throws java.beans.PropertyVetoException;
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 1:07:43 AM)
 * @param newName java.lang.String
 */
void setName(String newName) throws java.beans.PropertyVetoException;
}
