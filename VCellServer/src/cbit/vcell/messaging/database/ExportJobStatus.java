package cbit.vcell.messaging.database;
import cbit.util.*;
import java.io.*;
/**
 * Insert the type's description here.
 * Creation date: (6/4/2004 3:34:42 PM)
 * @author: Ion Moraru
 */
public class ExportJobStatus implements Matchable, Serializable {
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	return false;
}
}