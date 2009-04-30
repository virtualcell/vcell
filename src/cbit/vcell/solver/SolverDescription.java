/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

package cbit.vcell.solver;
/**
 * Insert the type's description here.
 * Creation date: (4/23/01 3:34:06 PM)
 * @author: Jim Schaff
 * Stochastic description is added on 12th July 2006.
 */
public class SolverDescription implements java.io.Serializable, org.vcell.util.Matchable {
	private int type;

	private static final int NUM_SOLVERS = 15;
	private static final int TYPE_FORWARD_EULER = 0;
	private static final int TYPE_RUNGE_KUTTA2 = 1;
	private static final int TYPE_RUNGE_KUTTA4 = 2;
	private static final int TYPE_RUNGE_KUTTA_FEHLBERG = 3;
	private static final int TYPE_ADAMS_MOULTON = 4;
	private static final int TYPE_IDA = 5;
	private static final int TYPE_FINITE_VOLUME = 6;
	private static final int TYPE_STOCH_GIBSON = 7;
	private static final int TYPE_HYBRID_EM = 8;
	private static final int TYPE_HYBRID_MIL = 9;
	private static final int TYPE_HYBRID_MIL_Adaptive = 10; //added adaptive milstein solver on July 12, 2007
	private static final int TYPE_CVODE = 11;
	private static final int TYPE_FINITE_VOLUME_STANDALONE = 12;
	private static final int TYPE_SUNDIALS_STANDALONE = 13;
	private static final int TYPE_SUNDIALS_PDE = 14;
	
	private static final String ALTERNATE_CVODE_Description = "LSODA (Variable Order, Variable Time Step)"; // backward compatibility
	
	private static final String[] DATABASE_NAME = {
		"Forward Euler (First Order, Fixed Time Step)",
		"Runge-Kutta (Second Order, Fixed Time Step)",
		"Runge-Kutta (Fourth Order, Fixed Time Step)",
		"Runge-Kutta-Fehlberg (Fifth Order, Variable Time Step)",
		"Adams-Moulton (Fifth Order, Fixed Time Step)",
		"IDA (Variable Order, Variable Time Step, ODE/DAE)",
		"Finite Volume, Regular Grid",
		"Gibson (Next Reaction Stochastic Method)",
		"Hybrid (Gibson + Euler-Maruyama Method)",
		"Hybrid (Gibson + Milstein Method)",
		"Hybrid (Adaptive Gibson + Milstein Method)",
		"CVODE (Variable Order, Variable Time Step)",
		"Finite Volume Standalone, Regular Grid",
		"Combined Stiff Solver (IDA/CVODE)",
		"Sundials Stiff PDE Solver (Variable Time Step)",
	};
	private static final String[] DISPLAY_LABEL = {
		"Forward Euler (First Order, Fixed Time Step)",
		"Runge-Kutta (Second Order, Fixed Time Step)",
		"Runge-Kutta (Fourth Order, Fixed Time Step)",
		"Runge-Kutta-Fehlberg (Fifth Order, Variable Time Step)",
		"Adams-Moulton (Fifth Order, Fixed Time Step)",
		"IDA (Variable Order, Variable Time Step, ODE/DAE)",
		"Semi-Implicit Finite Volume Compiled, Regular Grid (Fixed Time Step)",
		"Gibson (Next Reaction Stochastic Method)",
		"Hybrid (Gibson + Euler-Maruyama Method)",
		"Hybrid (Gibson + Milstein Method)",
		"Hybrid (Adaptive Gibson + Milstein Method)",
		"CVODE (Variable Order, Variable Time Step)",
		"Semi-Implicit Finite Volume, Regular Grid (Fixed Time Step)",
		"Combined Stiff Solver (IDA/CVODE)",
		"Fully-Implicit Finite Volume, Regular Grid (Variable Time Step)",
	};
	private static final String[] FULL_DESCRIPTIONS = {
		// Forward Euler (First Order, Fixed Time Step)
		"Forward Euler method is a fixed time step mehtod to solve ordinary differential equations. \n\n"+
		"Parameters to be set for the method are,\n"+
		"STARTING TIME: the time when simulation starts.\n"+
		"ENDING TIME: the time when simulation ends.\n"+
		"Default TIME STEP: the time step to numerically solve ODEs.\n\n"+
		"Output options: use keep every number of samples among maximum number of time samples.",
		// Runge-Kutta (Second Order, Fixed Time Step)
		"The Runge Kutta methods provide further systematic improvement in the spirit of the modified Euler method. Second order fixed time step method is also called the midpoint method.\n\n"+
		 "Parameters to be set for the method are,\n"+
        "STARTING TIME: the time when simulation starts.\n"+
		 "ENDING TIME: the time when simulation ends.\n"+
	     "Default TIME STEP: the time step to numerically solve ODEs.\n\n"+
	     "Output options: use keep every number of samples among maximum number of time samples.",
	     // Runge-Kutta (Fourth Order, Fixed Time Step)
	     "The Runge Kutta methods provide further systematic improvement in the spirit of the modified Euler method. the fourth order Runge Kutta method does four function evaluations per step to give a method with fourth order accuracy. It is a fixed time step method.\n\n"+
	  	 "Parameters to be set for the method are,\n"+
         "STARTING TIME: the time when simulation starts.\n"+
		 "ENDING TIME: the time when simulation ends.\n"+
	     "Default TIME STEP: the time step to numerically solve ODEs.\n\n"+
	     "Output options: use keep every number of samples among maximum number of time samples.",
	     // Runge-Kutta-Fehlberg (Fifth Order, Variable Time Step)
	     "The Runge-Kutta-Fehlberg integrator is primarily designed to solve non-stiff and mildly stiff differential equations when derivative evaluations are inexpensive. It should generally not be used when the user is demanding high accuracy. It is a variable time step method.\n\n"+
		 "Parameters to be set for the method are,\n"+
         "STARTING TIME: the time when simulation starts.\n"+
		 "ENDING TIME: the time when simulation ends.\n"+
	     "Minimum TIME STEP: the minimum time stepsize that the RKF should attempt to use.\n"+
	     "Maximum TIME STEP: the maxmum time stepsize that the RKF should attempt to use.\n"+
	     "ABSOLUTE TOLERANCE: RKF adjusts the stepsize in such a way as to keep the absolute error in a step less than absolute tolerance.\n"+
	     "RELATIVE TOLERANCE: RKF adjusts the stepsize in such a way as to keep the fractional error in a step less than relative tolerance.\n\n"+
	     "Output options: use keep every number of samples among maximum number of time samples.",
	     // Adams-Moulton (Fifth Order, Fixed Time Step)
	     "The methods such as Foward Euler, Runge-Kutta etc. are called single-step methods because they use only the information from one previous point to compute the successive point. Adams-Moulton methods are explicit linear multistep methods that depend on multiple previous solution points to generate a new approximate solution point. It is a fixed time step method.\n\n"+
		 "Parameters to be set for the method are,\n"+
         "STARTING TIME: the time when simulation starts.\n"+
		 "ENDING TIME: the time when simulation ends.\n"+
	     "Default TIME STEP: the time step to numerically solve ODEs.\n\n"+
	     "Output options: use keep every number of samples among maximum number of time samples.",
	     // IDA (Variable Order, Variable Time Step, ODE/DAE)
	     "IDA addresses systems of differential-algebraic equations (DAEs), and uses Backward Differentiation Formula methods. ODEs are a subset of DAEs, therefore IDA may be used for solving ODEs. \n\n"+
		 "Parameters to be set for the method are,\n"+
         "STARTING TIME: the time when simulation starts.\n"+
		 "ENDING TIME: the time when simulation ends.\n"+
	     "Minimum TIME STEP: the minimum time stepsize that the IDA should attempt to use.\n"+
	     "Maximum TIME STEP: the maxmum time stepsize that the IDA should attempt to use.\n"+
	     "ABSOLUTE TOLERANCE: IDA adjusts the stepsize in such a way as to keep the absolute error in a step less than absolute tolerance.\n"+
	     "RELATIVE TOLERANCE: IDA adjusts the stepsize in such a way as to keep the fractional error in a step less than relative tolerance.\n\n"+
	     "Output options: use keep every number of samples among maximum number of time samples or output time interval or specified output time points.",
	     // Finite Volume, Regular Grid
	     "The finite volume method is a method for representing and evaluating partial differential equations as algebraic discretization equations which exactly preserves conservation laws. Similar to the finite difference method, values are calculated at discrete places on a meshed geometry.\n\n"+
	     "Parameters to be set for the method are,\n"+
         "STARTING TIME: the time when simulation starts.\n"+
		 "ENDING TIME: the time when simulation ends.\n"+
	     "Default TIME STEP: the time step to numerically solve PDEs.\n\n"+
	     "Output options: use keep every number of samples.",
	     // Gibson (Next Reaction Stochastic Method)
	     "Gibson-Bruck is an improved exact stochastic method based on Gllespie's SSA. It uses only a single random number per simulation event and takes time proportional to the logarithm of the number of reactions. Better performance is also acheived by utilizing a dependency graph and an indexed priority queue.\n\n"+
         "Parameters to be set for the method are,\n"+
         "STARTING TIME: the time when simulation starts.\n"+
         "ENDING TIME: the time when simulation ends.\n"+
         "RANDOM SEED: a random number generated by PC time, which is used to produce a series of uniformly distributed random numbers.\n"+
         "CUSTOMIZED SEED: a user specified number, which is used to produce a series of uniformly distributed random numbers. \n"+
         "NUMBER OF TRIALS: the number of multiple trials.\n\n"+
         "Output options: use keep every number of samples or output time interval.\n\n"+
         "Reference: M.A.Gibson and J.Bruck,'Efficient Exact Stochastic Simulation of Chemical Systems with Many Species and Many Channels' ,J. Phys. Chem. 104, 1876(2000).",	     
		 // Hybrid (Gibson + Euler-Maruyama Method)
         "This is a hybrid stochastic method. It partitions the system into subsets of fast and slow reactions and approximates the fast reactions as a continuous Markov process, using a chemical Langevin equation, and accurately describes the slow dynamics using the Gibson algorithm. Fixed time step Euler-Maruyama is used for approximate numerical solution of CLE.\n\n"+
		 "Parameters to be set for the method are,\n"+
         "STARTING TIME: the time when simulation starts.\n"+
         "ENDING TIME: the time when simulation ends.\n"+
         "Default (SDE)TIME STEP: the maximum time step of the SDE numerical integrator.\n"+
         "RANDOM SEED: a random number generated by PC time, which is used to produce a series of uniformly distributed random numbers.\n"+
         "CUSTOMIZED SEED: a user specified number, which is used to produce a series of uniformly distributed random numbers. \n"+
         "NUMBER OF TRIALS: the number of multiple trials.\n"+
         "EPSILON: minimum number of molecules both reactant and product species required for approximation as a continuous Markov process.\n"+
         "LAMBDA: minimum rate of reaction required for approximation to a continuous Markov process.\n"+
         "MSR TOLERANCE: maximum allowed effect of executing multiple slow reactions per numerical integration of the SDEs.\n\n"+
         "Output options: use output time interval.\n\n"+
         "References: \n"+
         "H.Salis and Y.Kaznessis,'Accurate hybrid stochastic simulation of a system of coupled chemical or biochemical reactions' ,J. Chem. Phys. 122, 054103(2005).\n"+
         "H.Salis, V. Sotiropoulos and Y. Kaznessis,'Multiscale Hy3S: Hybrid stochastic simulation for supercomputers' ,BMC Bioinformatics 7:93(2006).",
		 // Hybrid (Gibson + Milstein Method)
         "This is a hybrid stochastic method. It partitions the system into subsets of fast and slow reactions and approximates the fast reactions as a continuous Markov process, using a chemical Langevin equation, and accurately describes the slow dynamics using the Gibson algorithm. Fixed time step Milstein is used for approximate numerical solution of CLE.\n\n"+
		 "Parameters to be set for the method are,\n"+
         "STARTING TIME: the time when simulation starts.\n"+
	     "ENDING TIME: the time when simulation ends.\n"+
	     "Default (SDE)TIME STEP: the maximum time step of the SDE numerical integrator.\n"+
	     "RANDOM SEED: a random number generated by PC time, which is used to produce a series of uniformly distributed random numbers.\n"+
         "CUSTOMIZED SEED: a user specified number, which is used to produce a series of uniformly distributed random numbers. \n"+
         "NUMBER OF TRIALS: the number of multiple trials.\n"+
         "EPSILON: minimum number of molecules both reactant and product species required for approximation as a continuous Markov process.\n"+
         "LAMBDA: minimum rate of reaction required for approximation to a continuous Markov process.\n"+
         "MSR TOLERANCE: maximum allowed effect of executing multiple slow reactions per numerical integration of the SDEs.\n\n"+
         "Output options: use output time interval.\n\n"+
         "References:\n"+
         "H.Salis and Y.Kaznessis,'Accurate hybrid stochastic simulation of a system of coupled chemical or biochemical reactions', J. Chem. Phys. 122, 054103(2005).\n"+
         "H.Salis, V. Sotiropoulos and Y. Kaznessis,'Multiscale Hy3S: Hybrid stochastic simulation for supercomputers',BMC Bioinformatics 7:93(2006).",         
	     // Hybrid (Adaptive Gibson + Milstein Method)
         "This is a hybrid stochastic method. It partitions the system into subsets of fast and slow reactions and approximates the fast reactions as a continuous Markov process, using a chemical Langevin equation, and accurately describes the slow dynamics using the Gibson algorithm. Adaptive time step Milstein is used for approximate numerical solution of CLE.\n\n"+
		 "Parameters to be set for the method are,\n"+
         "STARTING TIME: the time when simulation starts.\n"+
	     "ENDING TIME: the time when simulation ends.\n"+
	     "Default (SDE)TIME STEP: the maximum time step of the SDE numerical integrator, it may be set for\n"+
	     "adaptive methods to decrease memory requirements\n"+
	     "RANDOM SEED: a random number generated by PC time, which is used to produce a series of uniformly distributed random numbers.\n"+
         "CUSTOMIZED SEED: a user specified number, which is used to produce a series of uniformly distributed random numbers. \n"+
         "NUMBER OF TRIALS: the number of multiple trials.\n"+
         "EPSILON: minimum number of molecules both reactant and product species required for approximation as a continuous Markov process.\n"+
         "LAMBDA: minimum rate of reaction required for approximation to a continuous Markov process.\n"+
         "MSR TOLERANCE: maximum allowed effect of executing multiple slow reactions per numerical integration of the SDEs.\n"+
         "SDE tolerance: maximum allowed value of the drift and diffusion errors.\n\n"+
         "Output options: use output time interval.\n\n"+
         "References: \n"+
         "H.Salis and Y.Kaznessis,'Accurate hybrid stochastic simulation of a system of coupled chemical or biochemical reactions' ,J. Chem. Phys. 122, 054103(2005).\n"+
         "H.Salis, V. Sotiropoulos and Y. Kaznessis,'Multiscale Hy3S: Hybrid stochastic simulation for supercomputers' ,BMC Bioinformatics 7:93(2006).",         
	     // CVODE (Variable Order, Variable Time Step)
         "CVODE is used for solving initial value problems for ordinary differential equations. It solves both stiff and nonstiff systems, using variable-coefficient Adams and BDF methods. In the stiff case, options for treating the Jacobian of the system include dense and band matrix solvers, and a preconditioned Krylov (iterative) solver. In the highly modular organization of CVODE, the core integrator module is independent of the linear system solvers, and all operations on N-vectors are isolated in a module of vector kernels.\n\n"+
		 "Parameters to be set for the method are,\n"+
         "STARTING TIME: the time when simulation starts.\n"+
		 "ENDING TIME: the time when simulation ends.\n"+
	     "Minimum TIME STEP: the minimum time stepsize that the CVODE should attempt to use.\n"+
	     "Maximum TIME STEP: the maxmum time stepsize that the CVODE should attempt to use.\n"+
	     "ABSOLUTE TOLERANCE: CVODE adjusts the stepsize in such a way as to keep the absolute error in a step less than absolute tolerance.\n"+
	     "RELATIVE TOLERANCE: CVODE adjusts the stepsize in such a way as to keep the fractional error in a step less than relative tolerance.\n\n"+
	     "Output options: use keep every number of samples among maximum number of time samples or output time interval or specified output time points.",
	     // Finite Volume Standalone, Regular Grid
	     "This is our interpreted standalone version of the finite volume method. It is a little slower but gives better error messages.\n" +
	     "The finite volume method is a method for representing and evaluating partial differential equations as algebraic discretization equations which exactly preserves conservation laws. Similar to the finite difference method, values are calculated at discrete places on a meshed geometry.\n\n"+
	     "Parameters to be set for the method are,\n"+
         "STARTING TIME: the time when simulation starts.\n"+
		 "ENDING TIME: the time when simulation ends.\n"+
	     "Default TIME STEP: the time step to numerically solve PDEs.\n\n"+
	     "Output options: use keep every number of samples.",
	     // Combined Stiff Solver (IDA/CVODE)
	     "This chooses between IDA and CVODE depending on the problem to be solved. \n" 
	     + "CVODE is used for ordinary differential equation (ODE) systems;\n" 
	     + "IDA is used for differential-algebraic equation (DAE) systems.\n\n"
	     + "VCell models with fast reactions (i.e. fast systems) are DAE systems. ",
	     // Stiff PDE Solver (Variable Time Step)
	     "This is our fully implicit, adaptive time step finite volume method. The finite volume method " 
	     + "represents partial differential equations as algebraic discretization equations which exactly preserves conservation laws. " 
	     + "Similar to the finite difference method, values are calculated at discrete places on a meshed geometry.\n\n"
	     + "This method employs Sundials stiff solver CVODE for time stepping (method of lines).\n"
	     + "Parameters to be set for the method are,\n"
	     + "STARTING TIME: the time when simulation starts.\n"
	     + "ENDING TIME: the time when simulation ends.\n"
	     + "ABSOLUTE TOLERANCE: CVODE adjusts the time step size in such a way as to keep the absolute error (due to time discretization) in a step less than absolute tolerance.\n"
	     + "RELATIVE TOLERANCE: CVODE adjusts the time step size in such a way as to keep the fractional error (due to time discretization) in a step less than relative tolerance.\n"
	     + "(Please note that these tolerances affect the accuracy of time descritization only, therefore spatial discritization is the only significant source of solution error.)\n"
	     + "Output TIME STEP: output time interval.\n",
	};
	private static final boolean[] SOLVES_FASTSYSTEM = {
		true,   // TYPE_FORWARD_EULER
		false,	// TYPE_RUNGE_KUTTA2
		false,	// TYPE_RUNGE_KUTTA4
		false,	// TYPE_RUNGE_KUTTA_FEHLBERG
		false,	// TYPE_ADAMS_MOULTON
		true,	// TYPE_IDA
		true,	// TYPE_FINITE_VOLUME
		false,	// TYPE_STOCH_GIBSON
		false,	// TYPE_Hybrid_Euler
		false,	// TYPE_Hybrid_Milstein
		false,	// TYPE_HYBRID_MIL_Adaptive
		false,	// TYPE_CVODE
		true,	// TYPE_FINITE_VOLUME_STANDALONE
		true,	// TYPE_SUNDIALS_STANDALONE
		false,	// TYPE_SUNDIALS_PDE
	};
	private static final boolean[] IS_ODE = {
		true,   // TYPE_FORWARD_EULER
		true,	// TYPE_RUNGE_KUTTA2
		true,	// TYPE_RUNGE_KUTTA4
		true,	// TYPE_RUNGE_KUTTA_FEHLBERG
		true,	// TYPE_ADAMS_MOULTON
		true,	// TYPE_IDA
		false,	// TYPE_FINITE_VOLUME
		false,	// TYPE_STOCH_GIBSON
		false,	// TYPE_Hybrid_Euler
		false,	// TYPE_Hybrid_Milstein
		false,	// TYPE_HYBRID_MIL_Adaptive
		true,	// TYPE_CVODE
		false,	// TYPE_FINITE_VOLUME_STANDALONE
		true,	// TYPE_SUNDIALS_STANDALONE
		false,	// TYPE_SUNDIALS_PDE
	};
	private static final boolean[] IS_STOCH = {
		false,  // TYPE_FORWARD_EULER
		false,	// TYPE_RUNGE_KUTTA2
		false,	// TYPE_RUNGE_KUTTA4
		false,	// TYPE_RUNGE_KUTTA_FEHLBERG
		false,	// TYPE_ADAMS_MOULTON
		false,	// TYPE_IDA
		false,	// TYPE_FINITE_VOLUME
		true,	// TYPE_STOCH_GIBSON
		true,	// TYPE_Hybrid_Euler
		true,	// TYPE_Hybrid_Milstein
		true,	// TYPE_HYBRID_MIL_Adaptive
		false,	// TYPE_CVODE
		false,	// TYPE_FINITE_VOLUME_STANDALONE
		false,	// TYPE_SUNDIALS_STANDALONE
		false,	// TYPE_SUNDIALS_PDE
	};
	private static final boolean[] IS_INTERPRETED = {
		true,   // TYPE_FORWARD_EULER
		true,	// TYPE_RUNGE_KUTTA2
		true,	// TYPE_RUNGE_KUTTA4
		true,	// TYPE_RUNGE_KUTTA_FEHLBERG
		true,	// TYPE_ADAMS_MOULTON
		false,	// TYPE_IDA
		false,	// TYPE_FINITE_VOLUME
		false, 	// TYPE_STOCH_GIBSON
		false, 	// TYPE_Hybrid_Euler
		false, 	// TYPE_Hybrid_Milstein
		false,  // TYPE_HYBRID_MIL_Adaptive
		false,	// TYPE_CVODE
		false,	// TYPE_FINITE_VOLUME_STANDALONE
		false,	// TYPE_SUNDIALS_STANDALONE
		false,	// TYPE_SUNDIALS_PDE
	};
	// for all sundials solvers, the time order is variable from 1 to 5, we choose an intermediate order of 3
	// as a compromise for accuracy during stiff and non stiff time stepping 
	private static final int[] timeOrder = {		
		1,   // TYPE_FORWARD_EULER
		2,	// TYPE_RUNGE_KUTTA2
		4,	// TYPE_RUNGE_KUTTA4
		4,	// TYPE_RUNGE_KUTTA_FEHLBERG
		5,	// TYPE_ADAMS_MOULTON
		3,	// TYPE_IDA
		1,	// TYPE_FINITE_VOLUME
		1,	// TYPE_STOCH_GIBSON
		1,	// TYPE_Hybrid_Euler
		1,	// TYPE_Hybrid_Milstein
		1,	// TYPE_HYBRID_MIL_Adaptive
		3,	// TYPE_CVODE
		1,	// TYPE_FINITE_VOLUME_STANDALONE
		3,	// TYPE_SUNDIALS_STANDALONE
		3,	// TYPE_SUNDIALS_PDE
	};
	private static final boolean[] resolves_discontinuties = {
		false,   // TYPE_FORWARD_EULER
		false,	// TYPE_RUNGE_KUTTA2
		false,	// TYPE_RUNGE_KUTTA4
		false,	// TYPE_RUNGE_KUTTA_FEHLBERG
		false,	// TYPE_ADAMS_MOULTON
		true,	// TYPE_IDA
		false,	// TYPE_FINITE_VOLUME
		false, 	// TYPE_STOCH_GIBSON
		false, 	// TYPE_Hybrid_Euler
		false, 	// TYPE_Hybrid_Milstein
		false,  // TYPE_HYBRID_MIL_Adaptive
		true,	// TYPE_CVODE
		false,	// TYPE_FINITE_VOLUME_STANDALONE
		true,	// TYPE_SUNDIALS_STANDALONE
		true,	// TYPE_SUNDIALS_PDE
	};		
			
	public static final SolverDescription ForwardEuler			= new SolverDescription(TYPE_FORWARD_EULER);
	public static final SolverDescription RungeKutta2			= new SolverDescription(TYPE_RUNGE_KUTTA2);
	public static final SolverDescription RungeKutta4			= new SolverDescription(TYPE_RUNGE_KUTTA4);
	public static final SolverDescription RungeKuttaFehlberg	= new SolverDescription(TYPE_RUNGE_KUTTA_FEHLBERG);
	public static final SolverDescription AdamsMoulton			= new SolverDescription(TYPE_ADAMS_MOULTON);
	public static final SolverDescription IDA					= new SolverDescription(TYPE_IDA);
	public static final SolverDescription FiniteVolume			= new SolverDescription(TYPE_FINITE_VOLUME);
	public static final SolverDescription StochGibson			= new SolverDescription(TYPE_STOCH_GIBSON);
	public static final SolverDescription HybridEuler			= new SolverDescription(TYPE_HYBRID_EM);
	public static final SolverDescription HybridMilstein		= new SolverDescription(TYPE_HYBRID_MIL);
	public static final SolverDescription HybridMilAdaptive     = new SolverDescription(TYPE_HYBRID_MIL_Adaptive);
	public static final SolverDescription CVODE					= new SolverDescription(TYPE_CVODE);
	public static final SolverDescription FiniteVolumeStandalone = new SolverDescription(TYPE_FINITE_VOLUME_STANDALONE);
	public static final SolverDescription SUNDIALS				= new SolverDescription(TYPE_SUNDIALS_STANDALONE);
	public static final SolverDescription SundialsPDE			= new SolverDescription(TYPE_SUNDIALS_PDE);

	private static SolverDescription[] fieldODESolverDescriptions = new SolverDescription[] {
		ForwardEuler,
		RungeKutta2,
		RungeKutta4,
		AdamsMoulton,
		RungeKuttaFehlberg,
		IDA,
		CVODE,
		SUNDIALS,
	};
	private static SolverDescription[] fieldODEWithFastSolverDescriptions = new SolverDescription[] {
		ForwardEuler,
		IDA,
		SUNDIALS,
	};	
	private static SolverDescription[] fieldPDEWithFastSolverDescriptions = new SolverDescription[] {
		FiniteVolume,
		FiniteVolumeStandalone
	};
	private static SolverDescription[] fieldPDESolverDescriptions = new SolverDescription[] {
		FiniteVolume,
		FiniteVolumeStandalone,
		SundialsPDE,
	};
	private static SolverDescription[] fieldStochSolverDescriptions = new SolverDescription[] {
		StochGibson,
		HybridEuler,
		HybridMilstein,
		HybridMilAdaptive 
	};	
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
public boolean compareEqual(org.vcell.util.Matchable obj) {
	return equals(obj);
}


/**
 * Gets the solver property (java.lang.String) value.
 * @return The solver property value.
 * @see #setSolver
 */
public static SolverDescription getDefaultODESolverDescription() {
	return SUNDIALS;
}


/**
 * Gets the solver property (java.lang.String) value.
 * @return The solver property value.
 * @see #setSolver
 */
public static SolverDescription getDefaultPDESolverDescription() {
	return FiniteVolume;
}


/**
 * Get the default non-spatial stochastic solver which is Gibson.
 * Creation date: (9/27/2006 2:43:55 PM)
 * @return cbit.vcell.solver.SolverDescription
 */
public static SolverDescription getDefaultStochSolverDescription() {
	return StochGibson;
}

/**
 * Insert the method's description here.
 * Creation date: (9/8/2005 11:27:58 AM)
 * @return cbit.vcell.solver.OutputTimeSpec
 * @param solverTaskDescription cbit.vcell.solver.SolverTaskDescription
 */
public OutputTimeSpec createOutputTimeSpec(SolverTaskDescription solverTaskDescription) 
{
	switch (type) {
		case TYPE_HYBRID_EM:
		case TYPE_HYBRID_MIL:
		case TYPE_HYBRID_MIL_Adaptive:
		case TYPE_SUNDIALS_PDE:	{
			return new UniformOutputTimeSpec(0.1);
		}
		default:	
			return new DefaultOutputTimeSpec();
	}
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
public static SolverDescription fromDatabaseName(String solverName) {
	if (solverName == null) return null;
	if (solverName.equals(ALTERNATE_CVODE_Description)) {
		return CVODE;
	}
	for (int i=0;i<NUM_SOLVERS;i++){
		if (DATABASE_NAME[i].equals(solverName)){
			return new SolverDescription(i);
		}
	}
	throw new IllegalArgumentException("unexpected solver name '"+solverName+"'");
}

public static SolverDescription fromDisplayLabel(String label) {
	if (label == null) return null;
	for (int i=0;i<NUM_SOLVERS;i++){
		if (DISPLAY_LABEL[i].equals(label)){
			return new SolverDescription(i);
		}
	}
	throw new IllegalArgumentException("unexpected solver '"+label+"'");
}


/**
 * Insert the method's description here.
 * Creation date: (4/23/01 4:34:35 PM)
 * @return java.lang.String
 */
public String getDatabaseName() {
	return DATABASE_NAME[type];
}

public String getDisplayLabel() {
	return DISPLAY_LABEL[type];
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
		case TYPE_IDA:
		case TYPE_RUNGE_KUTTA_FEHLBERG:
		case TYPE_CVODE:
		case TYPE_SUNDIALS_STANDALONE:
		case TYPE_STOCH_GIBSON:
		case TYPE_HYBRID_MIL_Adaptive:
		case TYPE_SUNDIALS_PDE: {
			return true;
		}
		default: {
			return false;
		}
	}
}

public boolean hasErrorTolerance() {
	switch (type) {
		case TYPE_IDA:
		case TYPE_RUNGE_KUTTA_FEHLBERG:
		case TYPE_CVODE:
		case TYPE_SUNDIALS_STANDALONE:
		case TYPE_SUNDIALS_PDE: {
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

public boolean solvesFastSystem() {
	return SOLVES_FASTSYSTEM[type];
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
		case TYPE_IDA: 
		case TYPE_CVODE:
		case TYPE_SUNDIALS_STANDALONE: {
			return (outputTimeSpec.isDefault() || outputTimeSpec.isExplicit() || outputTimeSpec.isUniform());
		}
		case TYPE_STOCH_GIBSON:{
			return (outputTimeSpec.isDefault() || outputTimeSpec.isUniform());
		}
		case TYPE_HYBRID_EM:
		case TYPE_HYBRID_MIL:
		case TYPE_HYBRID_MIL_Adaptive:
		case TYPE_SUNDIALS_PDE: {
			return outputTimeSpec.isUniform();
		}
		default: {
			return (outputTimeSpec.isDefault());
		}
	}
}

public boolean supportsUniformExplicitOutput() {
	switch (type) {
	case TYPE_IDA: 
	case TYPE_CVODE:
	case TYPE_SUNDIALS_STANDALONE: {			
		return true;
	}
	default: {
		return false;
	}
}	
}

public static String getFullDescription(SolverDescription sd) {
	return FULL_DESCRIPTIONS[sd.type];
}

/**
 * getODESolverDescriptions method comment.
 */
public static SolverDescription[] getODESolverDescriptions() {
	return (fieldODESolverDescriptions);
}

public static SolverDescription[] getODEWithFastSystemSolverDescriptions() {
	return (fieldODEWithFastSolverDescriptions);
}

public static SolverDescription[] getPDEWithFastSystemSolverDescriptions() {
	return (fieldPDEWithFastSolverDescriptions);
}

/**
 * getPDESolverDescriptions method comment.
 */
public static SolverDescription[] getPDESolverDescriptions() {
	return (fieldPDESolverDescriptions);
}


/**
 * Get stochasic solver(s)' description(s)
 * Creation date: (9/27/2006 9:37:49 AM)
 * @return java.lang.String[]
 */
public static SolverDescription[] getStochSolverDescriptions() {
	return fieldStochSolverDescriptions;
}

public int getTimeOrder() {
	return timeOrder[type];
}

public boolean resolvesDiscontinuties() {
	return resolves_discontinuties[type];
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/01 3:52:28 PM)
 * @return java.lang.String
 */
public String toString() {
	return "SolverDescription@" + Integer.toHexString(hashCode()) + "(" + getDisplayLabel() + ")";
}
}