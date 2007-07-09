package cbit.vcell.server.bionetgen;

import org.vcell.util.BigString;
/**
 * Insert the type's description here.
 * Creation date: (6/27/2005 2:59:28 PM)
 * @author: Anuradha Lakshminarayana
 */
public class BNGInput implements java.io.Serializable {
	private org.vcell.util.BigString inputString;
/**
 * BNGInput constructor comment.
 */
public BNGInput(String argInputString) {
	super();
	inputString = new BigString(argInputString);
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/2005 3:04:42 PM)
 * @return cbit.util.BigString
 */
public String getInputString() {
	return inputString.toString();
}
}
