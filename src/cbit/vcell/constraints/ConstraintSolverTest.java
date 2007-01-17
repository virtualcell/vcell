package cbit.vcell.constraints;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.Expression;
import net.sourceforge.interval.ia_math.RealInterval;
/**
 * Insert the type's description here.
 * Creation date: (6/25/2001 10:00:31 PM)
 * @author: Jim Schaff
 */
public class ConstraintSolverTest {
/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 10:00:56 PM)
 * @param args java.lang.String]
 */
public static void main(String[] args) {
	try {
//		SimulationContext simContext = cbit.vcell.mapping.SimulationContextTest.getExample(0);
//		ConstraintContainerImpl ccImpl = ConstraintContainerImplTest.steadyStateFromApplication(simContext,1.1);
//		ConstraintContainerImpl ccImpl = ConstraintContainerImplTest.fromApplication(simContext);
		ConstraintContainerImpl ccImpl = ConstraintContainerImplTest.getExample();
//		ConstraintContainerImpl ccImpl = ConstraintContainerImplTest.getMichaelisMentenExample();
		ConstraintSolver cSolver = new ConstraintSolver(ccImpl);
		showSystem(ccImpl);
		cSolver.resetIntervals();
		showResults(cSolver);
		if (!cSolver.narrow()){
			System.out.println("narrow failed");
		}else{
			System.out.println("narrow succeeded");
		}
		showResults(cSolver);
		
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 10:11:00 PM)
 * @param constraintSolver cbit.vcell.constraints.ConstraintSolver
 */
public static void showResults(ConstraintSolver constraintSolver) {
	RealInterval intervals[] = constraintSolver.getIntervals();
	cbit.vcell.parser.SymbolTableEntry stes[] = constraintSolver.getSymbolTableEntries();
	System.out.println("Values:");
	for (int i = 0; i < stes.length; i++){
		System.out.println("    "+stes[i].getName()+" = "+intervals[stes[i].getIndex()].toString());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 10:11:00 PM)
 * @param constraintSolver cbit.vcell.constraints.ConstraintSolver
 */
public static void showSystem(ConstraintContainerImpl ccImpl) {
	SimpleBounds simpleBounds[] = ccImpl.getSimpleBounds();
	for (int i = 0; i < simpleBounds.length; i++){
		String constraintTypeName = simpleBounds[i].getTypeName();
		while (constraintTypeName.length()<20){	constraintTypeName += " "; }
		RealInterval interval = simpleBounds[i].getBounds();
		String identifier = simpleBounds[i].getIdentifier();
		System.out.println("   "+constraintTypeName+"    "+interval.lo()+" <= "+identifier+" <= "+interval.hi());
	}
	GeneralConstraint generalConstraints[] = ccImpl.getGeneralConstraints();
	for (int i = 0; i < generalConstraints.length; i++){
		String constraintTypeName = generalConstraints[i].getTypeName();
		while (constraintTypeName.length()<20){	constraintTypeName += " "; }
		System.out.println("   "+constraintTypeName+"    "+generalConstraints[i].getExpression());
	}
}
}