package org.vcell.physics.component;
import jscl.plugin.Expression;
import jscl.plugin.ParseException;


/**
 * Insert the type's description here.
 * Creation date: (1/22/2006 9:39:29 PM)
 * @author: Jim Schaff
 */
public class Ground extends OnePortElectricalDevice {
/**
 * Ground constructor comment.
 * @param argName java.lang.String
 */
public Ground(String argName) {
	super(argName);
	try {
		addEquation(Expression.valueOf("V(t)-0"));
		//addEquation(Expression.valueOf("I(t)-0"));
	}catch (ParseException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}