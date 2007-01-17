package cbit.vcell.modelopt;
/**
 * Insert the type's description here.
 * Creation date: (8/25/2005 10:17:21 AM)
 * @author: Jim Schaff
 */
public class ParameterMapping {
	private cbit.vcell.model.Parameter fieldModelParameter = null;
	private cbit.vcell.opt.Parameter fieldOptParameter = null;
	private cbit.vcell.math.Variable fieldMathVariable = null;

/**
 * ParameterMapping constructor comment.
 */
public ParameterMapping(cbit.vcell.model.Parameter modelParameter, cbit.vcell.opt.Parameter optParameter, cbit.vcell.math.Variable mathVariable) {
	super();
	fieldModelParameter = modelParameter;
	fieldOptParameter = optParameter;
	fieldMathVariable = mathVariable;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2005 10:20:52 AM)
 * @return cbit.vcell.math.Variable
 */
public cbit.vcell.math.Variable getMathVariable() {
	return fieldMathVariable;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2005 10:20:52 AM)
 * @return cbit.vcell.model.Parameter
 */
public cbit.vcell.model.Parameter getModelParameter() {
	return fieldModelParameter;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2005 10:20:52 AM)
 * @return cbit.vcell.opt.Parameter
 */
public cbit.vcell.opt.Parameter getOptParameter() {
	return fieldOptParameter;
}
}