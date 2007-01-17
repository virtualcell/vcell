package cbit.vcell.math;
/**
 * Stochastic variable definition.
 * Creation date: (7/7/2006 2:25:09 PM)
 * @author: Tracy LI
 */
public class StochVolVariable extends Variable {
/**
 * StochVolVariable constructor comment.
 * @param name java.lang.String
 */
public StochVolVariable(String name) {
	super(name);
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(cbit.util.Matchable obj) 
{
	if (!(obj instanceof StochVolVariable)){
		return false;
	}
	if (!compareEqual0(obj)){
		return false;
	}
	
	return true;
}

public String getVCML() {
	return VCML.StochVolVariable+"\t"+getName()+"\n";
}

}



