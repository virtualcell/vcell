package cbit.vcell.math;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import java.io.*;

import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.IExpression;

import cbit.util.*;
/**
 * This type was created in VisualAge.
 */
public abstract class FastSystem implements MathObject, Serializable, Matchable {
	protected MathDescription mathDesc = null;
	protected Vector fastInvariantList = new Vector();
	protected Vector fastRateList = new Vector();
	protected Vector fastVarList = new Vector();
	
	protected Vector independentVarList = new Vector();
	protected Vector dependentVarList = new Vector();
	protected Vector dependencyExpList = new Vector();
	protected Vector fastRateExpList = new Vector();
	protected Vector pseudoConstantList = new Vector();

	protected boolean bNeedsRefresh = false;
/**
 * FastSystem constructor comment.
 */
protected FastSystem(MathDescription mathDesc) {
	super();
	this.mathDesc = mathDesc;
}
/**
 * This method was created in VisualAge.
 * @param fastInvariant cbit.vcell.math.FastInvariant
 */
public void addFastInvariant(FastInvariant fastInvariant) throws MathException {
	if (fastInvariantList.contains(fastInvariant)){
		throw new MathException("fastInvariant "+fastInvariant+" already exists");
	}
	fastInvariantList.addElement(fastInvariant);

	bNeedsRefresh = true;
}
/**
 * This method was created in VisualAge.
 * @param fastInvariant cbit.vcell.math.FastRate
 */
public void addFastRate(FastRate fastRate) throws MathException {
	if (fastRateList.contains(fastRate)){
		throw new MathException("fastRate "+fastRate+" already exists");
	}
	fastRateList.addElement(fastRate);

	bNeedsRefresh = true;
}
/**
 * This method was created in VisualAge.
 */
public void checkValidity() throws MathException, ExpressionException {
	refreshAll();
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(Matchable object) {
	FastSystem fs = null;
	if (object == null){
		return false;
	}
	if (!(object instanceof FastSystem)){
		return false;
	}else{
		fs = (FastSystem)object;
	}
	BoundFunction mArray1 [] = null;
	BoundFunction mArray2 [] = null;
	if (fastInvariantList.size() > 0) {
		mArray1 = (BoundFunction [])fastInvariantList.toArray(new BoundFunction[fastInvariantList.size()]);
	}
	if (fs.fastInvariantList.size() > 0) {
		mArray2 = (BoundFunction [])fs.fastInvariantList.toArray(new BoundFunction[fs.fastInvariantList.size()]);
	}
	if (!Compare.isEqualOrNull(mArray1, mArray2))
		return false;

	mArray1 = null;
	mArray2 = null;
	if (fastRateList.size() > 0) {
		mArray1 = (BoundFunction [])fastRateList.toArray(new BoundFunction[fastRateList.size()]);
	}
	if (fs.fastRateList.size() > 0) {
		mArray2 = (BoundFunction [])fs.fastRateList.toArray(new BoundFunction[fs.fastRateList.size()]);
	}
	if (!Compare.isEqualOrNull(mArray1, mArray2))
		return false;
		
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 11:15:14 AM)
 * @param sim cbit.vcell.solver.Simulation
 */
abstract void flatten(MathDescription mathDesc, boolean bRoundCoefficients) throws ExpressionException, MathException;
/**
 * Insert the method's description here.
 * Creation date: (1/29/2002 4:24:21 PM)
 * @return java.lang.String
 */
protected String getAvailablePseudoConstantName() throws ExpressionBindingException {
	String base = "__C";
	int i = 0;
	while (true){
		if (mathDesc.getEntry(base+i)==null){
			return base+i;
		}
		i++;
	}
}
/**
 * This method was created in VisualAge.
 * @return java.util.Enumeration
 */
public Enumeration getDependencyExps() throws MathException, ExpressionException {
	refreshAll();
	return dependencyExpList.elements();
}
/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 */
public final Enumeration getDependentVariables() throws MathException, ExpressionException {
	refreshAll();
	return dependentVarList.elements();
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2002 1:58:28 AM)
 * @return cbit.vcell.parser.Expression[]
 */
final IExpression[] getExpressions() {
	Vector expList = new Vector();
	for (int i = 0; i < fastInvariantList.size(); i++){
		FastInvariant fi = (FastInvariant)fastInvariantList.elementAt(i);
		expList.add(fi.getFunction());
	}
	for (int i = 0; i < fastRateList.size(); i++){
		FastRate fr = (FastRate)fastRateList.elementAt(i);
		expList.add(fr.getFunction());
	}
	return (IExpression[])BeanUtils.getArray(expList,IExpression.class);
}
/**
 * This method was created in VisualAge.
 * @return java.util.Enumeration
 */
public Enumeration getFastInvariants() {
	return fastInvariantList.elements();
}
/**
 * This method was created in VisualAge.
 * @return java.util.Enumeration
 */
public Enumeration getFastRateExpressions() throws MathException, ExpressionException {
	refreshAll();
	return fastRateExpList.elements();
}
/**
 * This method returns the FastRates list.
 * Creation date: (3/2/2001 5:36:48 PM)
 * @return java.util.Vector
 */
public Enumeration getFastRates() {
	return fastRateList.elements();
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 10:31:03 AM)
 * @param sim cbit.vcell.solver.Simulation
 */
IExpression getFlattenedExpression(MathDescription mathDesc, IExpression exp, boolean bRoundCoefficients) throws ExpressionException, MathException {

	if (exp == null){
		return null;
	}
	
	exp.bindExpression(mathDesc);
	exp = MathUtilities.substituteFunctions(exp,mathDesc);
	exp = exp.flatten();
	if (bRoundCoefficients){
		exp.roundToFloat();
	}

	return exp;
}
/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 */
public final Enumeration getIndependentVariables() throws MathException, ExpressionException {
	refreshAll();
	return independentVarList.elements();
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumDependentVariables() throws MathException, ExpressionException {
	refreshAll();
	return dependentVarList.size();
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumFastRates() throws MathException, ExpressionException {
	refreshAll();
	return fastRateList.size();
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumIndependentVariables() throws MathException, ExpressionException {
	refreshAll();
	return independentVarList.size();
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumPseudoConstants() throws MathException, ExpressionException {
	refreshAll();
	return pseudoConstantList.size();
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.PseudoConstant
 * @param id java.lang.String
 */
protected PseudoConstant getPseudoConstant(String id) throws MathException, ExpressionException {
	refreshAll();
	for (int i=0;i<pseudoConstantList.size();i++){
		PseudoConstant pc = (PseudoConstant)pseudoConstantList.elementAt(i);
		if (pc.getName().equals(id)){
			return pc;
		}
	}
	return null;
}
/**
 * This method was created in VisualAge.
 * @return java.util.Enumeration
 */
public Enumeration getPseudoConstants() throws MathException, ExpressionException {
	refreshAll();
	return pseudoConstantList.elements();
}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public abstract String getVCML();
/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public abstract void read(CommentStringTokenizer tokens) throws MathException, ExpressionException;
/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 12:46:51 PM)
 */
public void rebind() throws ExpressionBindingException {
	for (int i = 0; i < fastInvariantList.size(); i++){
		IExpression fastInvariant = ((FastInvariant)fastInvariantList.elementAt(i)).getFunction();
		fastInvariant.bindExpression(mathDesc);
	}
	for (int i = 0; i < fastRateList.size(); i++){
		IExpression fastRate = ((FastRate)fastRateList.elementAt(i)).getFunction();
		fastRate.bindExpression(mathDesc);
	}
}
/**
 * This method was created in VisualAge.
 */
protected abstract void refreshAll() throws MathException, ExpressionException;
}
