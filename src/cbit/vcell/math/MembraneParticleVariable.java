package cbit.vcell.math;
/**
 * Stochastic variable definition.
 * Creation date: (7/7/2006 2:25:09 PM)
 * @author: Tracy LI
 */
public class MembraneParticleVariable extends ParticleVariable {
/**
 * StochVolVariable constructor comment.
 * @param name java.lang.String
 */
public MembraneParticleVariable(String name, Domain domain) {
	super(name, domain);
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) 
{
	if (!(obj instanceof MembraneParticleVariable)){
		return false;
	}
	if (!compareEqual0(obj)){
		return false;
	}
	
	return true;
}

public String getVCML() {
	return VCML.MembraneParticleVariable+"    "+getQualifiedName()+"\n";
}

}



