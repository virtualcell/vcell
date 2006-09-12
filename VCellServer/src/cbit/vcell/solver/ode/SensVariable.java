package cbit.vcell.solver.ode;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.*;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 9:00:26 PM)
 * @author: John Wagner
 */
public class SensVariable extends cbit.vcell.math.Variable {
	private VolVariable volVar = null;
	private Constant parameter = null;
/**
 * SensVariable constructor comment.
 * @param name java.lang.String
 */
public SensVariable(VolVariable volVariable, Constant parameter) {
	super(getSensName(volVariable,parameter));
	this.volVar = volVariable;
	this.parameter = parameter;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj Matchable
 */
public boolean compareEqual(cbit.util.Matchable obj) {
	if (!(obj instanceof SensVariable)){
		return false;
	}
	if (!compareEqual0(obj)){
		return false;
	}
	SensVariable v = (SensVariable)obj;
	
	if (!cbit.util.Compare.isEqual(volVar,v.volVar)){
		return false;
	}
	if (!cbit.util.Compare.isEqual(parameter,v.parameter)){
		return false;
	}
	
	return true;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.Constant
 */
public Constant getParameter() {
	return parameter;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param var cbit.vcell.math.VolVariable
 * @param parm cbit.vcell.math.Constant
 */
public static String getSensName(Function function, Constant parm) {
	return "sens_"+function.getName()+"_wrt_"+parm.getName();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param var cbit.vcell.math.VolVariable
 * @param parm cbit.vcell.math.Constant
 */
public static String getSensName(VolVariable var, Constant parm) {
	return "sens_"+var.getName()+"_wrt_"+parm.getName();
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.VolVariable
 */
public VolVariable getVolVariable() {
	return volVar;
}
}
