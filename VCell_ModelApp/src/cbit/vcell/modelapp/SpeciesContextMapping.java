package cbit.vcell.modelapp;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.expression.IExpression;

import cbit.vcell.math.Variable;
import cbit.vcell.model.SpeciesContext;
/**
 * This type was created in VisualAge.
 */
public class SpeciesContextMapping {
	
	private SpeciesContext speciesContext = null;	
	private Variable       variable = null;    // variable, constant, or function

	//
	// input to structural analysis
	//
	private boolean bDiffusing        = false;
	private boolean bFastParticipant  = false;

	//
	// output from structural analysis of slow reactions
	//
	private IExpression dependencyExp = null;   // if absolute dependent
	private IExpression rateExp = null;

	//
	// output from structural analysis of fast reactions
	//
	private IExpression fastInvariant = null;   // if only dependent on the fast time scale
	private IExpression fastRate      = null;          
/**
 * SpeciesContextMapping constructor comment.
 */
public SpeciesContextMapping(SpeciesContext speciesContext) {
	this.speciesContext = speciesContext;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:55:08 PM)
 * @return cbit.vcell.parser.Expression
 */
public IExpression getDependencyExpression() {
	return this.dependencyExp;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:59:19 PM)
 * @return cbit.vcell.parser.Expression
 */
public IExpression getFastInvariant() {
	return this.fastInvariant;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:50:50 PM)
 * @return cbit.vcell.parser.Expression
 */
public IExpression getFastRate() {
	return this.fastRate;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:57:40 PM)
 * @return cbit.vcell.parser.Expression
 */
public IExpression getRate() {
	return this.rateExp;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.SpeciesContext
 */
public SpeciesContext getSpeciesContext() {
	return speciesContext;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.Variable
 */
public Variable getVariable() {
	return variable;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:56:35 PM)
 * @return boolean
 */
public boolean isDiffusing() {
	return this.bDiffusing;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:49:24 PM)
 * @return boolean
 */
public boolean isFastParticipant() {
	return bFastParticipant;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 4:16:14 PM)
 * @param argDepencencyExpression cbit.vcell.parser.Expression
 */
public void setDependencyExpression(IExpression argDepencencyExpression) {
	this.dependencyExp = argDepencencyExpression;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 4:10:49 PM)
 * @param diffusing boolean
 */
public void setDiffusing(boolean diffusing) {
	this.bDiffusing = diffusing;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:53:28 PM)
 * @param fastInvariantExpression cbit.vcell.parser.Expression
 */
public void setFastInvariant(IExpression fastInvariantExpression) {
	this.fastInvariant = fastInvariantExpression;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 4:12:59 PM)
 * @param isFastParticipant boolean
 */
public void setFastParticipant(boolean isFastParticipant) {
	this.bFastParticipant = isFastParticipant;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:50:13 PM)
 * @param fastRateExpression cbit.vcell.parser.Expression
 */
public void setFastRate(IExpression argFastRateExpression) {
	this.fastRate = argFastRateExpression;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 4:18:22 PM)
 * @param rate cbit.vcell.parser.Expression
 */
public void setRate(IExpression argRate) {
	this.rateExp = argRate;
}
/**
 * This method was created in VisualAge.
 * @param variable cbit.vcell.math.Variable
 */
public void setVariable(Variable variable) {
	this.variable = variable;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Mapping <sc="+speciesContext.getName()+"> to <var="+variable+">";
}
}
