package cbit.vcell.solver.test;
/**
 * Insert the type's description here.
 * Creation date: (1/20/2003 11:07:17 AM)
 * @author: Jim Schaff
 */
public class VariableComparisonSummary implements java.io.Serializable {
	
	private Double maxRef;
	private Double minRef;
	private Double absError;
	private Double relError;
	private String varName = null;
	private Double mse;
	private Double timeAbsError;
	private Integer indexAbsError;

public VariableComparisonSummary(	String argVarName,
									double argMinRef,
									double argMaxRef,
									double argAbsoluteError,
									double argRelativeError,
									double argMeanSqErr,
									double argTimeAbsError,
									int    argIndexAbsError){

	this.varName = argVarName;
	this.minRef = 		new Double(argMinRef);
	this.maxRef = 		new Double(argMaxRef);
	this.absError = 	new Double(argAbsoluteError);
	this.relError = 	new Double(argRelativeError);
	this.mse = 			new Double(argMeanSqErr);
	this.timeAbsError =	new Double(argTimeAbsError);
	this.indexAbsError= new Integer(argIndexAbsError);
}


public Double getAbsoluteError(){
	return absError;
}


/**
 * Insert the method's description here.
 * Creation date: (11/23/2004 7:44:17 PM)
 * @return java.lang.Double
 */
public Integer getIndexAbsoluteError() {
	return indexAbsError;
}


public Double getMaxRef(){
	return maxRef;
}


/**
 * Gets the meanSqError property (double) value.
 * @return The meanSqError property value.
 */
public Double getMeanSqError() {
	return mse;
}


public Double getMinRef(){
	return minRef;
}


/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 11:22:01 AM)
 * @return java.lang.String
 */
public String getName() {
	return varName;
}


public Double getRelativeError(){
	return relError;
}


/**
 * Insert the method's description here.
 * Creation date: (11/23/2004 7:44:17 PM)
 * @return java.lang.Double
 */
public Double getTimeAbsoluteError() {
	return timeAbsError;
}


/**
 * Insert the method's description here.
 * Creation date: (3/7/2004 2:28:06 PM)
 * @return java.lang.String
 */
public String toShortString() {
	return "var="+getName()+":MSE="+mse+",MAE="+absError+",MRE="+relError+",MnR="+minRef+",MxR="+maxRef+",@t="+timeAbsError+",@index="+indexAbsError;
}


/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 11:30:12 AM)
 * @return java.lang.String
 */
public String toString() {
	return "VariableComparisonSummary@"+Integer.toHexString(hashCode())+": "+toShortString();
}
}