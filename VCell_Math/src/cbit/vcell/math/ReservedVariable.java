package cbit.vcell.math;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.expression.ExpressionBindingException;

import edu.uchc.vcell.expression.internal.*;
public class ReservedVariable extends Variable
{
   public final static ReservedVariable TIME = new ReservedVariable("t",0);
   public final static ReservedVariable X    = new ReservedVariable("x",1);
   public final static ReservedVariable Y    = new ReservedVariable("y",2);
   public final static ReservedVariable Z    = new ReservedVariable("z",3);
private ReservedVariable(String name, int defaultIndex){
	super(name);
	setIndex(defaultIndex);
}         
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj Matchable
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (!(obj instanceof ReservedVariable)){
		return false;
	}
	if (!compareEqual0(obj)){
		return false;
	}
	
	return true;
}
public static ReservedVariable fromString(String symbolName) {
	if (symbolName==null){
		return null;
	}else if (symbolName.equals(TIME.getName())){
		return TIME;
	}else if (symbolName.equals(X.getName())){
		return X;
	}else if (symbolName.equals(Y.getName())){
		return Y;
	}else if (symbolName.equals(Z.getName())){
		return Z;
	}else{
		return null;
	}
}         
public String getSyntax() throws ExpressionBindingException {
	if (isTIME()){
		return "t";
	}else if (isX()){
		return "x";
	}else if (isY()){
		return "y";
	}else if (isZ()){
		return "z";
	}else{
		throw new ExpressionBindingException("unimplemented reserved symbol: '"+getName()+"'");			
	}	
}	        
/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isConstant() {
	return false;
}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isTIME() {
	if (getName().equals(TIME.getName())){
		return true;
	}else{
		return false;
	}		
}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isX() {
	if (getName().equals(X.getName())){
		return true;
	}else{
		return false;
	}		
}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isY() {
	if (getName().equals(Y.getName())){
		return true;
	}else{
		return false;
	}		
}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isZ() {
	if (getName().equals(Z.getName())){
		return true;
	}else{
		return false;
	}		
}
   public String toString()
   {
	   return getName();
   }         
}
