package cbit.sql;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.Matchable;
/**
 * This type was created in VisualAge.
 */
public interface Versionable extends Cacheable,Matchable,Cloneable{
/**
 * Insert the method's description here.
 * Creation date: (4/24/2003 3:27:46 PM)
 */
void clearVersion();
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 10:39:30 AM)
 * @return java.lang.String
 */
String getDescription();
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String getName();
/**
 * This method was created in VisualAge.
 * @return Version
 */
Version getVersion();
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 11:02:05 AM)
 * @param description java.lang.String
 */
void setDescription(String description) throws java.beans.PropertyVetoException;
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 11:03:59 AM)
 * @param name java.lang.String
 */
void setName(String name) throws java.beans.PropertyVetoException;
}
