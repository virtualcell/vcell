package cbit.vcell.dictionary;

/**
 * Stores a reference to an enzyme associated with a Compound
 * Creation date: (6/24/2002 11:03:01 AM)
 * @author: Steven Woolley
 */
public class EnzymeRef implements cbit.sql.Cacheable{
    //type indicates how the compound is related to the enzyme. 
    //The type includes R for a reactant, I for an inhibitor, C for a cofactor, and E for an effector.

    public static final char ENZYME_TYPE_R = (char) 'R';
    public static final char ENZYME_TYPE_I = (char) 'I';
    public static final char ENZYME_TYPE_C = (char) 'C';
    public static final char ENZYME_TYPE_E = (char) 'E';
    private final char validEnzymeTypes[] =
        new char[] { ENZYME_TYPE_R, ENZYME_TYPE_I, ENZYME_TYPE_C, ENZYME_TYPE_E };

    private String ecNumber;
    private char enzymeType;

/**
 * Create a new EnzymeRef object that will store the info about this Enzyme.
 * Creation date: (6/24/2002 10:38:43 AM)
 */
public EnzymeRef(String argECNumber, char argEnzymeType) {

	if(argECNumber == null || argECNumber.length() == 0){
		throw new IllegalArgumentException(this.getClass().getName());
	}
	
    boolean bFound = false;
    for (int i = 0; i < validEnzymeTypes.length; i++) {
        if (validEnzymeTypes[i] == argEnzymeType) {
            bFound = true;
        }
    }
    if (!bFound) {
        throw new IllegalArgumentException("unexpected Enzyme type " + argEnzymeType);
    }
    this.ecNumber = argECNumber;
    this.enzymeType = argEnzymeType;
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(cbit.util.Matchable obj) {
	
	if (obj instanceof EnzymeRef){
		EnzymeRef enzymeRef = (EnzymeRef)obj;
		if (!ecNumber.equals(enzymeRef.ecNumber)){
			return false;
		}
		if(enzymeType != enzymeRef.enzymeType){
			return false;
		}
	}else{
		return false;
	}
	
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (6/3/2003 6:01:39 PM)
 * @return java.lang.String
 */
public java.lang.String getEcNumber() {
	return ecNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (6/3/2003 6:01:39 PM)
 * @return char
 */
public char getEnzymeType() {
	return enzymeType;
}
/**
 * Returns a String that represents the value of this object.
 * @return a string representation of the receiver
 */
public String toString() {
	return "E.C. Number = " + ecNumber + " (" + enzymeType + ")";
}
}
