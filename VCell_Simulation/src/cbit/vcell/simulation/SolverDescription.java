package cbit.vcell.simulation;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (4/23/01 3:34:06 PM)
 * @author: Jim Schaff
 * Stochastic description is added on 12th July 2006.
 */
public class SolverDescription implements java.io.Serializable, cbit.util.Matchable {
	private int type;
	
	private static final int NUM_SOLVERS = 8;
	private static final int TYPE_FORWARD_EULER = 0;
	private static final int TYPE_RUNGE_KUTTA2 = 1;
	private static final int TYPE_RUNGE_KUTTA4 = 2;
	private static final int TYPE_RUNGE_KUTTA_FEHLBERG = 3;
	private static final int TYPE_ADAMS_MOULTON = 4;
	private static final int TYPE_LSODA = 5;
	private static final int TYPE_FINITE_VOLUME = 6;
	private static final int TYPE_STOCH_GIBSON = 7;		

	private static final String[] DESCRIPTIONS = {
		"Forward Euler (First Order, Fixed Time Step)",
		"Runge-Kutta (Second Order, Fixed Time Step)",
		"Runge-Kutta (Fourth Order, Fixed Time Step)",
		"Runge-Kutta-Fehlberg (Fifth Order, Variable Time Step)",
		"Adams-Moulton (Fifth Order, Fixed Time Step)",
		"LSODA (Variable Order, Variable Time Step)",
		"Finite Volume, Regular Grid",
		"Gibson (Next Reaction Stochastic Method)"
	};
	private static final boolean[] IS_ODE = {
		true,   // TYPE_FORWARD_EULER
		true,	// TYPE_RUNGE_KUTTA2
		true,	// TYPE_RUNGE_KUTTA4
		true,	// TYPE_RUNGE_KUTTA_FEHLBERG
		true,	// TYPE_ADAMS_MOULTON
		true,	// TYPE_LSODA
		false,	// TYPE_FINITE_VOLUME
		false	// TYPE_STOCH_GIBSON
	};
	private static final boolean[] IS_STOCH = {
		false,  // TYPE_FORWARD_EULER
		false,	// TYPE_RUNGE_KUTTA2
		false,	// TYPE_RUNGE_KUTTA4
		false,	// TYPE_RUNGE_KUTTA_FEHLBERG
		false,	// TYPE_ADAMS_MOULTON
		false,	// TYPE_LSODA
		false,	// TYPE_FINITE_VOLUME
		true	// TYPE_STOCH_GIBSON
	};
	private static final boolean[] IS_INTERPRETED = {
		true,   // TYPE_FORWARD_EULER
		true,	// TYPE_RUNGE_KUTTA2
		true,	// TYPE_RUNGE_KUTTA4
		true,	// TYPE_RUNGE_KUTTA_FEHLBERG
		true,	// TYPE_ADAMS_MOULTON
		false,	// TYPE_LSODA
		false,	// TYPE_FINITE_VOLUME
		false 	// TYPE_STOCH_GIBSON
	};
		
			
	public static final SolverDescription ForwardEuler			= new SolverDescription(TYPE_FORWARD_EULER);
	public static final SolverDescription RungeKutta2			= new SolverDescription(TYPE_RUNGE_KUTTA2);
	public static final SolverDescription RungeKutta4			= new SolverDescription(TYPE_RUNGE_KUTTA4);
	public static final SolverDescription RungeKuttaFehlberg	= new SolverDescription(TYPE_RUNGE_KUTTA_FEHLBERG);
	public static final SolverDescription AdamsMoulton			= new SolverDescription(TYPE_ADAMS_MOULTON);
	public static final SolverDescription LSODA					= new SolverDescription(TYPE_LSODA);
	public static final SolverDescription FiniteVolume			= new SolverDescription(TYPE_FINITE_VOLUME);
	public static final SolverDescription StochGibson			= new SolverDescription(TYPE_STOCH_GIBSON);
/**
 * SolverDescription constructor comment.
 */
private SolverDescription(int argSolverType) {
	this.type = argSolverType;
}
/**
 * Insert the method's description here.
 * Creation date: (5/24/2001 9:27:14 PM)
 * @return boolean
 * @param obj cbit.util.Matchable
 */
public boolean compareEqual(cbit.util.Matchable obj) {
	return equals(obj);
}
/**
 * Insert the method's description here.
 * Creation date: (9/8/2005 11:27:58 AM)
 * @return cbit.vcell.solver.OutputTimeSpec
 * @param solverTaskDescription cbit.vcell.solver.SolverTaskDescription
 */
public OutputTimeSpec createOutputTimeSpec(SolverTaskDescription solverTaskDescription) {
	return new DefaultOutputTimeSpec();
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/01 3:53:20 PM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof SolverDescription){
		if (((SolverDescription)object).type == type){
			return true;
		}
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/01 4:46:28 PM)
 * @return cbit.vcell.solver.SolverDescription
 * @param solverName java.lang.String
 */
public static SolverDescription fromName(String solverName) {
	if (solverName == null) return null;
	for (int i=0;i<NUM_SOLVERS;i++){
		if (DESCRIPTIONS[i].equals(solverName)){
			return new SolverDescription(i);
		}
	}
	throw new IllegalArgumentException("unexpected solver name '"+solverName+"'");
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/01 5:27:13 PM)
 * @return double
 * @param simulation cbit.vcell.solver.Simulation
 */
public double getEstimatedMemoryMB(Simulation simulation) {
	if (type == TYPE_FINITE_VOLUME){
		//
		// calculate number of PDE variables and total number of spatial variables
		//
		int pdeVarCount=0;
		int odeVarCount=0;
		java.util.Enumeration enumSD = simulation.getMathDescription().getSubDomains();
		while (enumSD.hasMoreElements()){
			cbit.vcell.math.SubDomain sd = (cbit.vcell.math.SubDomain)enumSD.nextElement();
			if (sd instanceof cbit.vcell.math.CompartmentSubDomain){
				java.util.Enumeration enumEQ = sd.getEquations();
				while (enumEQ.hasMoreElements()){
					cbit.vcell.math.Equation eq = (cbit.vcell.math.Equation)enumEQ.nextElement();
					if (eq instanceof cbit.vcell.math.PdeEquation){
						pdeVarCount++;
					}else if (eq instanceof cbit.vcell.math.OdeEquation){
						odeVarCount++;
					}
				}
				break;
			}
		}
		cbit.util.ISize samplingSize = simulation.getMeshSpecification().getSamplingSize();
		long numMeshPoints = samplingSize.getX()*samplingSize.getY()*samplingSize.getZ();
		//
		// calculated assuming PCG on Digital DS20 running Tru64 (cxx)
		//
		int dim = simulation.getMathDescription().getGeometry().getDimension();
		switch (dim){
			case 1: {
				//
				// underestimate, but who cares ... it's small
				//
				double memoryMB = (double)((pdeVarCount+odeVarCount)*numMeshPoints*16)/1e6 + 16;
				
				return memoryMB;
			}
			case 2: {
				//
				// contribution of PDE stuff
				//
				double memoryMB = (4e-4 + 1.85e-4*pdeVarCount + 1.8e-6*pdeVarCount*pdeVarCount)*numMeshPoints + 17;
				//
				// contribution of ODE stuff
				//
				memoryMB += (double)(odeVarCount*numMeshPoints*16)/1e6;
				
				return memoryMB;
			}
			case 3: {
				//
				// contribution of PDE stuff
				//
				double memoryMB = (3e-4 + 2.33e-4*pdeVarCount + 4e-7*pdeVarCount*pdeVarCount)*numMeshPoints + 16;
				//
				// contribution of ODE stuff
				//
				memoryMB += (double)(odeVarCount*numMeshPoints*16)/1e6;;
				
				return memoryMB;
			}
			default: {
				throw new RuntimeException("unexpected dimension "+dim);
			}
		}
	}else{
		throw new RuntimeException("SolverDescription.getEstimatedMemoryMB(), not yet implemented for Solver "+getName());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/01 4:34:35 PM)
 * @return java.lang.String
 */
public String getName() {
	return DESCRIPTIONS[type];
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/01 3:54:43 PM)
 * @return int
 */
public int hashCode() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (6/12/2001 11:10:59 AM)
 * @return boolean
 */
public boolean hasVariableTimestep() {
	switch (type) {
		case TYPE_LSODA: {
			return true;
		}
		case TYPE_RUNGE_KUTTA_FEHLBERG: {
			return true;
		}
		default: {
			return false;
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/01 4:49:19 PM)
 * @return boolean
 */
public boolean isInterpretedSolver() {
	return IS_INTERPRETED[type];
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/01 4:49:19 PM)
 * @return boolean
 */
public boolean isODESolver() {
	return IS_ODE[type];
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/01 4:49:19 PM)
 * @return boolean
 */
public boolean isPDESolver() {
	return !(IS_ODE[type] || IS_STOCH[type]);
}

/**
 * Check whether the solver is stochastic solver or not.
 * Creation date: (7/18/2006 5:08:30 PM)
 * @return boolean
 */
public boolean isSTOCHSolver() {
	return IS_STOCH[type];
}

/**
 * Insert the method's description here.
 * Creation date: (9/8/2005 11:23:54 AM)
 * @return boolean
 * @param outputTimeSpec cbit.vcell.solver.OutputTimeSpec
 */
public boolean supports(OutputTimeSpec outputTimeSpec) {
	switch (type) {
		case TYPE_LSODA:{
			return (outputTimeSpec.isDefault() || outputTimeSpec.isExplicit() || outputTimeSpec.isUniform());
		}
		default: {
			return (outputTimeSpec.isDefault());
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/01 3:52:28 PM)
 * @return java.lang.String
 */
public String toString() {
	return "SolverDescription@"+Integer.toHexString(hashCode())+"("+getName()+")";
}
}
