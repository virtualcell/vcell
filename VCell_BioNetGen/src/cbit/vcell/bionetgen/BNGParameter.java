package cbit.vcell.bionetgen;
/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 1:19:06 PM)
 * @author: Jim Schaff
 */
public class BNGParameter {
	private String name = null;
	private double value = 0.0;

/**
 * BNGParameter constructor comment.
 */
public BNGParameter(String argName, double argValue) {
	super();
	name = argName;
	value = argValue;
}


/**
 * BNGParameter constructor comment.
 */
public String getName() {
	return name;
}


/**
 * BNGParameter constructor comment.
 */
public double getValue() {
	return value;
}


/**
 * BNGParameter constructor comment.
 */
public void setName(String argName) {
	name = argName;
}


/**
 * BNGParameter constructor comment.
 */
public void setValue(double argValue) {
	value = argValue;
}


/**
 * BNGParameter constructor comment.
 */
public String toString() {
	return new String(getName() + ";\t" + getValue());
}
}