package cbit.vcell.bionetgen;
import org.vcell.expression.IExpression;

/**
 * Insert the type's description here.
 * Creation date: (1/13/2006 5:33:12 PM)
 * @author: Jim Schaff
 */
public class BNGSingleStateSpecies extends BNGSpecies {
/**
 * BNGSingleStateSpecies constructor comment.
 * @param argName java.lang.String
 */
public BNGSingleStateSpecies(String argName, IExpression argConc, int argNtwkFileIndx) {
	super(argName, argConc, argNtwkFileIndx);
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 5:33:12 PM)
 * @return boolean
 */
public boolean isWellDefined() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (3/13/2006 2:50:46 PM)
 * @return boolean
 */
public BNGSpecies[] parseBNGSpeciesName() {
	return new BNGSpecies[] {this};
}
}