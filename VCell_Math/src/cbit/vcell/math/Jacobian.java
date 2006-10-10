package cbit.vcell.math;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.*;

import java.util.*;
import javax.swing.event.*;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;
/**
 * The Jacobian class represents the jacobian of the nonlinear system C'(t) = f(C,t).
 * For simplicity, only ODE's are considered within a single subDomain.
 * 
 */
public class Jacobian implements ChangeListener {
	private MathDescription mathDesc = null;
	private SubDomain subDomain = null;
	private boolean bMathDirty = true;
	//
	// variables are only Volume Variables with ODE's
	//
	private VolVariable vars[] = null;
	private IExpression rates[] = null;
	private int numVariables = 0;
	
	//
	//   d Ci'
	//   ---- = Jexp[i+numVars*j] 
	//   d Cj
	//
	private IExpression Jexp[] = null;
/**
 * This method was created by a SmartGuide.
 * @param mathDesc cbit.vcell.math.MathDescription
 */
public Jacobian (MathDescription AmathDesc, SubDomain AsubDomain) {
	this.mathDesc = AmathDesc;
	this.subDomain = AsubDomain;
	this.mathDesc.addChangeListener(this);
}
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
private void create() throws ExpressionException {
	Jexp = new IExpression[numVariables*numVariables];
	for (int rateIndex=0;rateIndex<numVariables;rateIndex++){
		for (int varIndex=0;varIndex<numVariables;varIndex++){
			IExpression rateExp = rates[rateIndex];
			VolVariable var = vars[varIndex];
			rateExp.bindExpression(mathDesc);
			rateExp = MathUtilities.substituteFunctions(rateExp, mathDesc);
			IExpression diff = rateExp.differentiate(var.getName());
			diff.bindExpression(null);
			diff = diff.flatten();
			setJ(rateIndex,varIndex,diff);
			diff.bindExpression(mathDesc);
		}
	}
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
 * @param rateIndex int
 * @param varIndex int
 * @exception java.lang.Exception The exception description.
 */
public IExpression getJexp(int rateIndex, int varIndex) throws MathException, ExpressionException, ArrayIndexOutOfBoundsException {
	refresh();
	if (rateIndex<0 || rateIndex>=numVariables){
		throw new ArrayIndexOutOfBoundsException("rateIndex out of range '"+rateIndex+"'");
	}	
	if (varIndex<0 || varIndex>=numVariables){
		throw new ArrayIndexOutOfBoundsException("varIndex out of range '"+varIndex+"'");
	}	
	return Jexp[rateIndex+varIndex*numVariables];
}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getNumRates() throws MathException, ExpressionException {
	refresh();
	return vars.length;
}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @param constant cbit.vcell.math.Constant
 */
public int getRateIndex(Variable variable) throws MathException, ExpressionException {
	refresh();
	for (int i=0;i<vars.length;i++){
		if (vars[i] == variable){
			return i;
		}
	}
	return -1;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.SubDomain
 */
public SubDomain getSubDomain() throws Exception {
	refresh();
	return subDomain;
}
/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 * @exception java.lang.Exception The exception description.
 */
public Enumeration getTotalExpressions(Variable var) throws Exception {
	refresh();
	Vector vector = new Vector();
	
	for (int rateIndex=0;rateIndex<numVariables;rateIndex++){
		for (int varIndex=0;varIndex<numVariables;varIndex++){
			if (var == vars[varIndex] || var == vars[rateIndex]){
				IExpression J = ExpressionFactory.createExpression(getJexp(rateIndex, varIndex));
				IExpression lhs = ExpressionFactory.derivative(vars[varIndex].getName(),ExpressionFactory.createExpression(vars[rateIndex].getName()+"_rate;"));
				IExpression exp = ExpressionFactory.assign(lhs,J);
				vector.addElement(exp);
			}	
		}
	}		
	return vector.elements();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
private void parseMathDesc() throws MathException {
	Vector equationList = new Vector();
	Enumeration enum1 = subDomain.getEquations();
	while (enum1.hasMoreElements()){
		Equation equ = (Equation)enum1.nextElement();
		if (equ instanceof OdeEquation){
			equationList.addElement(equ);
		}else{
			throw new MathException("encountered non-ode equation, unsupported");
		}		
	}	
	Vector variableList = new Vector();
	enum1 = mathDesc.getVariables();
	while (enum1.hasMoreElements()){
		Variable var = (Variable)enum1.nextElement();
		if (var instanceof VolVariable){
			variableList.addElement(var);
		}		
	}
	if (equationList.size() != variableList.size()){
		throw new MathException("there are "+equationList.size()+" equations and "+variableList.size()+" variables");
	}
	numVariables = variableList.size();
	rates = new IExpression[numVariables];
	vars = new VolVariable[numVariables];
	for (int i=0;i<numVariables;i++){
		OdeEquation odeEqu = (OdeEquation)equationList.elementAt(i);
		rates[i] = odeEqu.getRateExpression();
		vars[i] = (VolVariable)odeEqu.getVariable();
	}	
	
}
/**
 * This method was created in VisualAge.
 */
private void refresh() throws MathException, ExpressionException {
	if (bMathDirty){
		parseMathDesc();
		create();
		bMathDirty = false;
	}
}
/**
 * This method was created in VisualAge.
 */
public void refreshDependencies() {
	mathDesc.removeChangeListener(this);
	mathDesc.addChangeListener(this);
}
/**
 * This method was created by a SmartGuide.
 * @param rateIndex int
 * @param varIndex int
 * @param exp cbit.vcell.parser.Expression
 * @exception java.lang.Exception The exception description.
 */
private void setJ(int rateIndex, int varIndex, IExpression exp) throws ArrayIndexOutOfBoundsException {
	if (rateIndex<0 || rateIndex>=numVariables){
		throw new ArrayIndexOutOfBoundsException("rateIndex out of range '"+rateIndex+"'");
	}	
	if (varIndex<0 || varIndex>=numVariables){
		throw new ArrayIndexOutOfBoundsException("varIndex out of range '"+varIndex+"'");
	}	
	Jexp[rateIndex+varIndex*numVariables] = exp;
}
/**
 * This method was created by a SmartGuide.
 */
public void show() throws MathException, ExpressionException {
	refresh();
	for (int rateIndex=0;rateIndex<numVariables;rateIndex++){
		for (int varIndex=0;varIndex<numVariables;varIndex++){
			IExpression rateExp = rates[rateIndex];
			VolVariable var = vars[varIndex];
			IExpression J = getJexp(rateIndex,varIndex);
			System.out.println("Jacobian of "+vars[rateIndex].getName()+"' wrt "+vars[varIndex].getName()+" is "+J);
		}
	}
}
	/**
	 * Invoked when the target of the listener has changed its state.
	 *
	 * @param e  a ChangeEvent object
	 */
public void stateChanged(javax.swing.event.ChangeEvent e) {
	if (e.getSource() instanceof MathDescription) {
		bMathDirty = true;
	}
}
}
