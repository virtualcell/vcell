/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

package cbit.vcell.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.vcell.util.Matchable;

/**
 * Insert the type's description here.
 * Creation date: (4/23/01 3:34:06 PM)
 * @author: Jim Schaff
 * Stochastic description is added on 12th July 2006.
 */
public class SolverDescription implements java.io.Serializable, org.vcell.util.Matchable {
	private int type;
	
	public enum SolverFeature {
		Feature_NonSpatial("NonSpatial"),
		Feature_Spatial("Spatial"),
		Feature_Deterministic("Deterministic"),
		Feature_Stochastic("Stochastic"),
		Feature_FastSystem("Fast System (algebraic constraints)"),
		Feature_PeriodicBoundaryCondition("Periodic Boundary Condition"),
		Feature_StopAtTimeDiscontinuities("Stop at Discontinuities (explicit function of time)"),
		Feature_StopAtGeneralDiscontinuities("Stop at Discontinuities (general)"),
		Feature_Events("Events"),
		Feature_RandomVariables("Random Variables"),
		Feature_StopAtSpatiallyUniform("Stop at Spatially Uniform"),
		Feature_DataProcessingInstructions("Data Processing Instructions"),
		Feature_PSF("Point Spread Function"),
		Feature_JVMRequired("JVM Required"),
		Feature_SerialParameterScans("Serial Parameter Scans"),
		Feature_VolumeRegionEquation("Volume Region Equations");
		
		private String name;
		private SolverFeature(String name) {
			this.name = name;
		}
		public final String getName() {
			return name;
		}
	}
	public static SolverFeature[] OdeFeatureSet = new SolverFeature[] {
		SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Deterministic
	};
	public static SolverFeature[] OdeFastSystemFeatureSet = new SolverFeature[] {
		SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_FastSystem
	};
	public static SolverFeature[] PdeFeatureSet = new SolverFeature[] {
		SolverFeature.Feature_Spatial, SolverFeature.Feature_Deterministic
	};
	public static SolverFeature[] PdeFastSystemFeatureSet = new SolverFeature[] {
		SolverFeature.Feature_Spatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_FastSystem
	};
	public static SolverFeature[] StochasticNonSpatialFeatureSet = new SolverFeature[] {
		SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Stochastic
	};
	public static SolverFeature[] DiscontinutiesFeatureSet = new SolverFeature[] {
		SolverFeature.Feature_StopAtTimeDiscontinuities, SolverFeature.Feature_StopAtGeneralDiscontinuities
	};
	
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
	private static final int TYPE_COMBINED_IDA_CVODE = 13;
	private static final int TYPE_SUNDIALS_PDE = 14;
	
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
	public static final SolverDescription CombinedSundials		= new SolverDescription(TYPE_COMBINED_IDA_CVODE);
	public static final SolverDescription SundialsPDE			= new SolverDescription(TYPE_SUNDIALS_PDE);

	private static SolverDescription[] AllSolverDescriptions = new SolverDescription[] {
		ForwardEuler,
		RungeKutta2,
		RungeKutta4,
		RungeKuttaFehlberg,
		AdamsMoulton,
		IDA,
		FiniteVolume,
		StochGibson,
		HybridEuler,
		HybridMilstein,
		HybridMilAdaptive,
		CVODE,
		FiniteVolumeStandalone,
		CombinedSundials,
		SundialsPDE
	};
	
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
	
	private static String Description_Start_Time = "<b>Starting Time</b>";
	private static String Description_End_Time = "<b>Ending Time</b>";
	private static String Description_TimeStep = "<b>Time Step</b>";
	private static String Description_TimeStep_Default = "<b>Default:</b> the time step to numerically solve ODEs.";
	private static String Description_TimeStep_Min = "<b>Minimum:</b> the minimum time stepsize that the solver should attempt to use.";
	private static String Description_TimeStep_Max = "<b>Maximum:</b> the maxmum time stepsize that the solver should attempt to use.";
	private static String Description_ErrorTolerance = "<b>Error Tolerance</b>";
	private static String Description_ErrorTolerance_Abs = "<b>Absolute:</b> the solver adjusts the stepsize in such a way " +
			"as to keep the absolute error in a step less than absolute tolerance.";
	private static String Description_ErrorTolerance_Rel = "<b>Relative:</b> the solver adjusts the stepsize in such a way " +
			"as to keep the fractional error in a step less than relative tolerance.";
	private static String Description_ErrorTolerance_LinearSolverRel = "<b>Linear Solver Tolerance:</b> the tolerance used to test for " +
			"convergence of the iteration. The iteration is considered to have converged when the size of residual is less than or " +
			"equal to the tolerance.";
	private static String Description_OutputOptions = "<b>Output Options</b>";
	private static String Description_OutputOptions_KeepEvery_ODE = "<b>Keep Every <i>N</i> / At Most <i>M</i>: </b> based on solver's " +
			"internal time step, saves solution at every <i>N</i><sup>th</sup> time step but at most <i>M</i> saved time points. " +
			"If exceeds <i>M</i> saved time points, attempts to remove nearly colinear saved time points.";
	private static String Description_OutputOptions_KeepEvery_PDE = "<b>Keep Every <i>N</i> / At Most <i>M</i>: </b> based on solver's " +
			"internal time step, saves solution at every <i>N</i><sup>th</sup> time step and terminates if exceeds <i>M</i> saved time points.";
	private static String Description_OutputOptions_KeepEvery_Gibson = "<b>Keep Every <i>N</i>:</b> based on solver's " +
			"internal time step, saves solution at every <i>N</i><sup>th</sup> time step.";
	private static String Description_OutputOptions_OutputInterval = "<b>Output Interval:</b> uniformly sampled time points.";
	private static String Description_OutputOptions_OutputTimes = "<b>Output Times:</b> explicit output time points.";
	
	private static final String Description_Stochastic_MSR_TOLERANCE = "<b>MSR Tolerance:</b> maximum allowed effect of executing multiple " +
			"slow reactions per numerical integration of the SDEs.";
	private static final String Description_Stochastic_LAMBDA = "<b>Lambda:</b> minimum rate of reaction required for approximation to a " +
			"continuous Markov process.";
	private static final String Description_Stochastic_EPSILON = "<b>Epsilon:</b> minimum number of molecules both reactant and product " +
			"species required for approximation as a continuous Markov process.";
	private static final String Description_Stochastic_NUMBER_OF_TRIALS = "<b>Number of Trials:</b> the number of multiple trials.";
	private static final String Description_Stochastic_CUSTOMIZED_SEED = "<b>Customized Seed:</b> a user specified number, which is used to " +
			"produce a series of uniformly distributed random numbers.";
	private static final String Description_Stochastic_RANDOM_SEED = "<b>Random Seed:</b> a random number generated by PC time, which is used to " +
			"produce a series of uniformly distributed random numbers.";
	private static final String Description_Stochastic_SDE_TOLERANCE = "<b>SDE Tolerance:</b> maximum allowed value of the drift and diffusion errors.";
	private static final String Description_Stochastic_DEFAULT_TIME_STEP = "<b>Default:</b> the maximum time step of the SDE numerical integrator.";
	private static final String Description_Stochastic_DEFAULT_TIME_STEP_Adaptive = "<b>Default:</b> the initial time step of the SDE numerical integrator. " +
			"It may be set for adaptive methods to decrease memory requirements.";
	private static String Description_StochasticOptions = "<b>Stochastic Options</b>";
	
	private static final String Description_PARAMETERS_TO_BE_SET = "<p><u><b>Input Parameters:<b></u>";
	private static final String Description_REFERENCES = "<u><b>References:<b></u>";
	
	private static final String[] FULL_DESCRIPTIONS = {
		// Forward Euler (First Order, Fixed Time Step)
		"<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_FORWARD_EULER] + "</h3></center>" +
		"Forward Euler method is a fixed time step mehtod to solve ordinary differential equations. "+
		Description_PARAMETERS_TO_BE_SET +
		"<ul>"+
		"<li>" + Description_Start_Time + "</li>"+
		"<li>" + Description_End_Time + "</li>"+
		"<li>" + Description_TimeStep +
			"<ul>"+
			"<li>" + Description_TimeStep_Default + "</li>" +
			"</ul></li>"+
		"<li>" + Description_OutputOptions +
 			"<ul>" +
 			"<li>" + Description_OutputOptions_KeepEvery_ODE + "</li>" +
 			"</ul></li>"
		+ "</ul>"
	     +"</html>",
		// Runge-Kutta (Second Order, Fixed Time Step)
	     "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_RUNGE_KUTTA2] + "</h3></center>" +
		"The Runge Kutta methods provide further systematic improvement in the spirit of the modified Euler method. " +
		"Second order fixed time step method is also called the midpoint method."+
		 Description_PARAMETERS_TO_BE_SET+
			"<ul>"+
			"<li>" + Description_Start_Time + "</li>"+
			"<li>" + Description_End_Time + "</li>"+
			"<li>" + Description_TimeStep +
				"<ul>"+
				"<li>" + Description_TimeStep_Default + "</li>" +
				"</ul></li>"+
			"<li>" + Description_OutputOptions +
	 			"<ul>" +
	 			"<li>" + Description_OutputOptions_KeepEvery_ODE + "</li>" +
	 			"</ul></li>"
			+ "</ul>"
	     +"</html>",
	     // Runge-Kutta (Fourth Order, Fixed Time Step)
	     "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_RUNGE_KUTTA4] + "</h3></center>" +
	     "The Runge Kutta methods provide further systematic improvement in the spirit of the modified Euler method. " +
	     "the fourth order Runge Kutta method does four function evaluations per step to give a method with fourth order accuracy. It is a fixed time step method."+
			Description_PARAMETERS_TO_BE_SET +
			"<ul>"+
			"<li>" + Description_Start_Time + "</li>"+
			"<li>" + Description_End_Time + "</li>"+
			"<li>" + Description_TimeStep +
				"<ul>"+
				"<li>" + Description_TimeStep_Default + "</li>" +
				"</ul></li>"+
			"<li>" + Description_OutputOptions +
	 			"<ul>" +
	 			"<li>" + Description_OutputOptions_KeepEvery_ODE + "</li>" +
	 			"</ul></li>"
			+ "</ul>"
	     +"</html>",
	     // Runge-Kutta-Fehlberg (Fifth Order, Variable Time Step)
	     "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_RUNGE_KUTTA_FEHLBERG] + "</h3></center>" +
	     "The Runge-Kutta-Fehlberg integrator is primarily designed to solve non-stiff and " +
	     "mildly stiff differential equations when derivative evaluations are inexpensive. It should generally " +
	     "not be used when the user is demanding high accuracy. It is a variable time step method."+
		 Description_PARAMETERS_TO_BE_SET+
		 "<ul>"+
		"<li>" + Description_Start_Time + "</li>"+
		"<li>" + Description_End_Time + "</li>"+
		"<li>" + Description_TimeStep +
			"<ul>"+
			"<li>" + Description_TimeStep_Min + "</li>"+
			"<li>" + Description_TimeStep_Max + "</li>"+
			"</ul></li>"+
		"<li>" + Description_ErrorTolerance +
			"<ul>"+
			"<li>" + Description_ErrorTolerance_Abs + "</li>"+
			"<li>" + Description_ErrorTolerance_Rel + "</li>"+
			"</ul></li>"+
		"<li>" + Description_OutputOptions +
			"<ul>" +
			"<li>" + Description_OutputOptions_KeepEvery_ODE + "</li>" +
			"</ul></li>"
			+ "</ul>"
	     +"</html>",
	     // Adams-Moulton (Fifth Order, Fixed Time Step)
	     "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_ADAMS_MOULTON] + "</h3></center>" +
	     "The methods such as Foward Euler, Runge-Kutta etc. are called single-step methods because they use only the information from one previous point to compute the successive point. Adams-Moulton methods are explicit linear multistep methods that depend on multiple previous solution points to generate a new approximate solution point. It is a fixed time step method.\n\n"+
		 Description_PARAMETERS_TO_BE_SET+
		 "<ul>"+
			"<li>" + Description_Start_Time + "</li>"+
			"<li>" + Description_End_Time + "</li>"+
			"<li>" + Description_TimeStep +
				"<ul>"+
				"<li>" + Description_TimeStep_Default + "</li>" +
				"</ul></li>"+
			"<li>" + Description_OutputOptions +
	 			"<ul>" +
	 			"<li>" + Description_OutputOptions_KeepEvery_ODE + "</li>" +
	 			"</ul></li>"
			+ "</ul>"
	     +"</html>",
	     // IDA (Variable Order, Variable Time Step, ODE/DAE)
	     "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_IDA] + "</h3></center>" +
	     "IDA addresses systems of differential-algebraic equations (DAEs), and uses Backward Differentiation Formula methods. ODEs are a subset of DAEs, therefore IDA may be used for solving ODEs. \n\n"+
	     Description_PARAMETERS_TO_BE_SET +
		 "<ul>"+
		"<li>" + Description_Start_Time + "</li>"+
		"<li>" + Description_End_Time + "</li>"+
		"<li>" + Description_TimeStep +
			"<ul>"+
			"<li>" + Description_TimeStep_Max + "</li>" +
			"</ul></li>"+
		"<li>" + Description_ErrorTolerance +
			"<ul>"+
			"<li>" + Description_ErrorTolerance_Abs + "</li>"+
			"<li>" + Description_ErrorTolerance_Rel + "</li>"+
			"</ul></li>"+
		"<li>" + Description_OutputOptions +
 			"<ul>" +
 			"<li>" + Description_OutputOptions_KeepEvery_ODE + "</li>" +
 			"<li>" + Description_OutputOptions_OutputInterval + "</li>" +
 			"<li>" + Description_OutputOptions_OutputTimes + "</li>" +
 			"</ul></li>"
	     +"</ul>" +
	     "</html>",
	     // Finite Volume, Regular Grid
	     "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_FINITE_VOLUME] + "</h3></center>" +
	     "The finite volume method is a method for representing and evaluating partial differential equations as algebraic discretization equations which exactly preserves conservation laws. Similar to the finite difference method, values are calculated at discrete places on a meshed geometry.\n\n"+
	     Description_PARAMETERS_TO_BE_SET +
	     "<li>" + Description_Start_Time + "</li>"+
			"<li>" + Description_End_Time + "</li>"+
			"<li>" + Description_TimeStep +
				"<ul>"+
				"<li>" + Description_TimeStep_Default + "</li>" +
				"</ul></li>"+
		     "<li>" + Description_ErrorTolerance_LinearSolverRel + "</li>" +
		     "<li>" + Description_OutputOptions +
		     	"<ul>" +
	  			"<li>" + Description_OutputOptions_OutputInterval + "</li>" +
	  			"</ul></li>"
	  		 + "</ul>"
	     +"</html>",
	     // Gibson (Next Reaction Stochastic Method)
	     "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_STOCH_GIBSON] + "</h3></center>" +
	     "Gibson-Bruck is an improved exact stochastic method based on Gllespie's SSA. It uses only a single random " +
	     "number per simulation event and takes time proportional to the logarithm of the number of reactions. Better " +
	     "performance is also acheived by utilizing a dependency graph and an indexed priority queue."+
	     Description_PARAMETERS_TO_BE_SET +
	     "<ul>" +
		"<li>" + Description_Start_Time + "</li>"+
		"<li>" + Description_End_Time + "</li>"+
		"<li>" + Description_StochasticOptions + 
			"<ul>" +
			"<li>" + Description_Stochastic_RANDOM_SEED+ "</li>"+
			"<li>" + Description_Stochastic_CUSTOMIZED_SEED+ "</li>"+
			"<li>" + Description_Stochastic_NUMBER_OF_TRIALS+ "</li>"+
			"</ul></li>" +
		"<li>" + Description_OutputOptions +
			"<ul>" +
			"<li>" + Description_OutputOptions_KeepEvery_Gibson + "</li>" +
			"<li>" + Description_OutputOptions_OutputInterval + "</li>" +
			"</ul></li>" +
         "</ul>" +
         Description_REFERENCES+
         "<ul>" +         
         "<li>M.A.Gibson and J.Bruck,'Efficient Exact Stochastic Simulation of Chemical Systems with Many Species and " +
         "Many Channels', J. Phys. Chem. 104, 1876(2000).</li>" +
         "</ul>"
	     +"</html>",	     
		 // Hybrid (Gibson + Euler-Maruyama Method)
         "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_HYBRID_EM] + "</h3></center>" +
         "This is a hybrid stochastic method. It partitions the system into subsets of fast and slow reactions and " +
         "approximates the fast reactions as a continuous Markov process, using a chemical Langevin equation, and " +
         "accurately describes the slow dynamics using the Gibson algorithm. Fixed time step Euler-Maruyama is used " +
         "for approximate numerical solution of CLE."+
         Description_PARAMETERS_TO_BE_SET +
 		"<li>" + Description_Start_Time + "</li>"+
		"<li>" + Description_End_Time + "</li>"+
		"<li>" + Description_TimeStep +
			"<ul>"+
			"<li>" + Description_Stochastic_DEFAULT_TIME_STEP + "</li>" +
			"</ul></li>"+
		"<li>" + Description_StochasticOptions + 
			"<ul>" +			
			"<li>" + Description_Stochastic_RANDOM_SEED+"</li>" +
			"<li>" + Description_Stochastic_CUSTOMIZED_SEED+"</li>" +
			"<li>" + Description_Stochastic_NUMBER_OF_TRIALS+"</li>" +
			"<li>" + Description_Stochastic_EPSILON+"</li>" +
			"<li>" + Description_Stochastic_LAMBDA+"</li>" +
			"<li>" + Description_Stochastic_MSR_TOLERANCE+"</li>" +
			"</ul></li>" +
		"<li>" + Description_OutputOptions +
			"<ul>" +
			"<li>" + Description_OutputOptions_OutputInterval + "</li>" +
			"</ul></li>" +
		"</ul>" +
         Description_REFERENCES+
         "<ul>" +         
         "<li>H.Salis and Y.Kaznessis,'Accurate hybrid stochastic simulation of a system of coupled chemical or biochemical reactions', " +
         	"J. Chem. Phys. 122, 054103(2005).</li>"+
         "<li>H.Salis, V. Sotiropoulos and Y. Kaznessis,'Multiscale Hy3S: Hybrid stochastic simulation for supercomputers', " +
         "	BMC Bioinformatics 7:93(2006).</li>"
         +"</ul>"
	     +"</html>",
		 // Hybrid (Gibson + Milstein Method)
         "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_HYBRID_MIL] + "</h3></center>" +
         "This is a hybrid stochastic method. It partitions the system into subsets of fast and slow reactions and " +
         "approximates the fast reactions as a continuous Markov process, using a chemical Langevin equation, and accurately describes " +
         "the slow dynamics using the Gibson algorithm. Fixed time step Milstein is used for approximate numerical solution of CLE.\n\n"+
         Description_PARAMETERS_TO_BE_SET +
 		"<li>" + Description_Start_Time + "</li>"+
		"<li>" + Description_End_Time + "</li>"+
		"<li>" + Description_TimeStep +
			"<ul>"+
			"<li>" + Description_Stochastic_DEFAULT_TIME_STEP + "</li>" +
			"</ul></li>"+
		"<li>" + Description_StochasticOptions + 
			"<ul>" +			
			"<li>" + Description_Stochastic_RANDOM_SEED + "</li>" +
			"<li>" + Description_Stochastic_CUSTOMIZED_SEED+ "</li>" +
			"<li>" + Description_Stochastic_NUMBER_OF_TRIALS+"</li>" +
			"<li>" + Description_Stochastic_EPSILON+"</li>" +
			"<li>" + Description_Stochastic_LAMBDA+"</li>" +
			"<li>" + Description_Stochastic_MSR_TOLERANCE+"</li>" +
			"</ul></li>" +
	     "<li>" + Description_OutputOptions +
			"<ul>" +
			"<li>" + Description_OutputOptions_OutputInterval + "</li>" +
			"</ul></li>" +
		"</ul>" +			
		Description_REFERENCES+
		"<ul>" +
         "<li>H.Salis and Y.Kaznessis,'Accurate hybrid stochastic simulation of a system of coupled " +
         	"chemical or biochemical reactions', J. Chem. Phys. 122, 054103(2005).</li>"+
         "<li>H.Salis, V. Sotiropoulos and Y. Kaznessis,'Multiscale Hy3S: Hybrid stochastic simulation for supercomputers', BMC Bioinformatics 7:93(2006).</li>"
	     +"</html>",         
	     // Hybrid (Adaptive Gibson + Milstein Method)
         "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_HYBRID_MIL_Adaptive] + "</h3></center>" +
         "This is a hybrid stochastic method. It partitions the system into subsets of fast and slow reactions and approximates " +
         "the fast reactions as a continuous Markov process, using a chemical Langevin equation, and accurately describes " +
         "the slow dynamics using the Gibson algorithm. Adaptive time step Milstein is used for approximate numerical solution of CLE."+
         Description_PARAMETERS_TO_BE_SET +
 		"<li>" + Description_Start_Time + "</li>"+
		"<li>" + Description_End_Time + "</li>"+
		"<li>" + Description_TimeStep +
			"<ul>"+
			"<li>" + Description_Stochastic_DEFAULT_TIME_STEP_Adaptive + "</li>" +
			"</ul></li>"+
		"<li>" + Description_StochasticOptions + 
			"<ul>" +
			"<li>" + Description_Stochastic_RANDOM_SEED+"</li>" +
			"<li>" + Description_Stochastic_CUSTOMIZED_SEED+"</li>" +
			"<li>" + Description_Stochastic_NUMBER_OF_TRIALS+"</li>" +
			"<li>" + Description_Stochastic_EPSILON+"</li>" +
			"<li>" + Description_Stochastic_LAMBDA+"</li>" +
			"<li>" + Description_Stochastic_MSR_TOLERANCE+"</li>" +
			"<li>" + Description_Stochastic_SDE_TOLERANCE+"</li>" +
			"</ul></li>" +
	     "<li>" + Description_OutputOptions +
			"<ul>" +
			"<li>" + Description_OutputOptions_OutputInterval + "</li>" +
			"</ul></li>" +
		"</ul>" +						
		Description_REFERENCES+
		"<ul>" +
         "<li>H.Salis and Y.Kaznessis,'Accurate hybrid stochastic simulation of a system of coupled chemical or biochemical " +
         	"reactions', J. Chem. Phys. 122, 054103(2005).</li>"+
         "<li>H.Salis, V. Sotiropoulos and Y. Kaznessis,'Multiscale Hy3S: Hybrid stochastic simulation for supercomputers', " +
         	"BMC Bioinformatics 7:93(2006).</li>" +
         "</ul>"
	     +"</html>",         
	     // CVODE (Variable Order, Variable Time Step)
         "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_CVODE] + "</h3></center>" +
         "CVODE is used for solving initial value problems for ordinary differential equations. It solves both stiff and nonstiff " +
         "systems, using variable-coefficient Adams and BDF methods. In the stiff case, options for treating the Jacobian of the system " +
         "include dense and band matrix solvers, and a preconditioned Krylov (iterative) solver. In the highly modular organization of CVODE, " +
         "the core integrator module is independent of the linear system solvers, and all operations on N-vectors are isolated in a module of vector kernels."+
         Description_PARAMETERS_TO_BE_SET +
 		"<li>" + Description_Start_Time + "</li>"+
		"<li>" + Description_End_Time + "</li>"+
		"<li>" + Description_TimeStep +
			"<ul>"+
			"<li>" + Description_TimeStep_Max + "</li>" +
			"</ul></li>"+
		"<li>" + Description_ErrorTolerance +
			"<ul>"+
			"<li>" + Description_ErrorTolerance_Abs + "</li>"+
			"<li>" + Description_ErrorTolerance_Rel + "</li>"+
			"</ul></li>"+
	     "<li>" + Description_OutputOptions +
			"<ul>" +
			"<li>" + Description_OutputOptions_KeepEvery_ODE + "</li>" +
			"<li>" + Description_OutputOptions_OutputInterval + "</li>" +
			"<li>" + Description_OutputOptions_OutputTimes + "</li>" +
			"</ul></li>"
		 + "</ul>"
	     +"</html>",
	     // Finite Volume Standalone, Regular Grid
	     "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_FINITE_VOLUME_STANDALONE] + "</h3></center>" +
	     "This is our interpreted standalone version of the finite volume method. It is a little slower but gives better error messages." +
	     "The finite volume method is a method for representing and evaluating partial differential equations as algebraic discretization " +
	     "equations which exactly preserves conservation laws. Similar to the finite difference method, values are calculated at discrete " +
	     "places on a meshed geometry."+
	     Description_PARAMETERS_TO_BE_SET +
		"<li>" + Description_Start_Time + "</li>"+
		"<li>" + Description_End_Time + "</li>"+
		"<li>" + Description_TimeStep +
			"<ul>"+
			"<li>" + Description_TimeStep_Default + "</li>" +
			"</ul></li>"+
	     "<li>" + Description_ErrorTolerance_LinearSolverRel + "</li>" +
	     "<li>" + Description_OutputOptions +
	     	"<ul>" +
  			"<li>" + Description_OutputOptions_OutputInterval + "</li>" +
  			"</ul></li>"
  		 + "</ul>"	
	     +"</html>",
	     // Combined Stiff Solver (IDA/CVODE)
	     "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_COMBINED_IDA_CVODE] + "</h3></center>"
	     + "This chooses between IDA and CVODE depending on the problem to be solved. <br>" 
	     + "<ul>" 
	     + "<li><b>CVODE</b> is used for ordinary differential equation (ODE) systems;</li>" 
	     + "<li><b>IDA</b> is used for differential-algebraic equation (DAE) systems.</li>" 
	     + "</ul>" 
	     + "VCell models with fast reactions (i.e. fast systems) are DAE systems. "
	     +"</html>",
	     // Stiff PDE Solver (Variable Time Step)
	     "<html>"
	     + "<center><h3>" + DISPLAY_LABEL[TYPE_SUNDIALS_PDE] + "</h3></center>"
	     + "This is our fully implicit, adaptive time step finite volume method. The finite volume method " 
	     + "represents partial differential equations as algebraic discretization equations which exactly preserves conservation laws. " 
	     + "Similar to the finite difference method, values are calculated at discrete places on a meshed geometry.\n\n"
	     + "This method employs Sundials stiff solver CVODE for time stepping (method of lines). " 
	     + "Please note that relative and absolute tolerances affect the accuracy of time descritization only, therefore spatial discritization " 
	     + "is the only significant source of solution error." +
	     Description_PARAMETERS_TO_BE_SET +
	     "<ul>" +
		"<li>" + Description_Start_Time + "</li>"+
		"<li>" + Description_End_Time + "</li>"+
		"<li>" + Description_ErrorTolerance +
			"<ul>"+
			"<li>" + Description_ErrorTolerance_Abs + "</li>"+
			"<li>" + Description_ErrorTolerance_Rel + "</li>"+
			"</ul></li>" +
		"<li>" + Description_OutputOptions +
	     		"<ul>" +
	     		"<li>" + Description_OutputOptions_KeepEvery_PDE + "</li>" +
	     		"<li>" + Description_OutputOptions_OutputInterval + "</li>" +
	     		"</ul></li>"
	     + "</ul>"
	     +"</html>",
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
		3,	// TYPE_COMBINED_IDA_CVODE
		3,	// TYPE_SUNDIALS_PDE
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
public boolean compareEqual(Matchable obj) {
	return equals(obj);
}


/**
 * Gets the solver property (java.lang.String) value.
 * @return The solver property value.
 * @see #setSolver
 */
public static SolverDescription getDefaultODESolverDescription() {
	return CombinedSundials;
}


/**
 * Gets the solver property (java.lang.String) value.
 * @return The solver property value.
 * @see #setSolver
 */
public static SolverDescription getDefaultPDESolverDescription() {
	return FiniteVolumeStandalone;
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
		case TYPE_COMBINED_IDA_CVODE:
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
		case TYPE_COMBINED_IDA_CVODE:
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
public boolean isJavaSolver() {
	Set<SolverFeature> set = getSupportedFeatures();
	return set.contains(SolverFeature.Feature_JVMRequired);
}

/**
 * Check whether the solver is stochastic solver or not.
 * Creation date: (7/18/2006 5:08:30 PM)
 * @return boolean
 */
public boolean isStochasticNonSpatialSolver() {
	return getSupportedFeatures().containsAll(Arrays.asList(StochasticNonSpatialFeatureSet));
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
		case TYPE_COMBINED_IDA_CVODE: {
			return (outputTimeSpec.isDefault() || outputTimeSpec.isExplicit() || outputTimeSpec.isUniform());
		}
		case TYPE_STOCH_GIBSON:
		case TYPE_FINITE_VOLUME:
		case TYPE_FINITE_VOLUME_STANDALONE: 
		case TYPE_SUNDIALS_PDE: {
			return (outputTimeSpec.isDefault() || outputTimeSpec.isUniform());
		}
		case TYPE_HYBRID_EM:
		case TYPE_HYBRID_MIL:
		case TYPE_HYBRID_MIL_Adaptive: {
			return outputTimeSpec.isUniform();
		}
		default: {
			return (outputTimeSpec.isDefault());
		}
	}
}

public boolean isSundialsSolver() {
	return type == TYPE_CVODE || type == TYPE_IDA || type == TYPE_COMBINED_IDA_CVODE || type == TYPE_SUNDIALS_PDE;
}

public boolean isSemiImplicitPdeSolver() {
	return type == TYPE_FINITE_VOLUME || type == TYPE_FINITE_VOLUME_STANDALONE;
}

public String getFullDescription() {
	return FULL_DESCRIPTIONS[type];
}

/**
 * getODESolverDescriptions method comment.
 */
public static SolverDescription[] getODESolverDescriptions() {
	return getSolverDescriptions(OdeFeatureSet);
}

public static SolverDescription[] getODEWithFastSystemSolverDescriptions() {
	return getSolverDescriptions(OdeFastSystemFeatureSet);
}

public static SolverDescription[] getPDEWithFastSystemSolverDescriptions() {
	return getSolverDescriptions(PdeFastSystemFeatureSet);
}

/**
 * getPDESolverDescriptions method comment.
 */
public static SolverDescription[] getPDESolverDescriptions() {
	return getSolverDescriptions(PdeFeatureSet);
}


/**
 * Get stochasic solver(s)' description(s)
 * Creation date: (9/27/2006 9:37:49 AM)
 * @return java.lang.String[]
 */
public static SolverDescription[] getStochasticNonSpatialSolverDescriptions() {
	return getSolverDescriptions(StochasticNonSpatialFeatureSet);
}

public static SolverDescription[] getSolverDescriptions(SolverFeature[] desiredSolverFeatures){
	ArrayList<SolverDescription> solvers = new ArrayList<SolverDescription>();
	for (SolverDescription sd : AllSolverDescriptions){
		Set<SolverFeature> features = sd.getSupportedFeatures();
		boolean bContainsAll = true;
		for (SolverFeature feature : desiredSolverFeatures) {
			if (!features.contains(feature)){
				bContainsAll = false;
				break;
			}
		}
		if (bContainsAll) {
			solvers.add(sd);
		}
	}
	return solvers.toArray(new SolverDescription[0]);
}

public int getTimeOrder() {
	return timeOrder[type];
}

public boolean supports(SolverFeature[] features) {
	Set<SolverFeature> set = getSupportedFeatures();
	return set.containsAll(Arrays.asList(features));
}

/**
 * Insert the method's description here.
 * Creation date: (4/23/01 3:52:28 PM)
 * @return java.lang.String
 */
public String toString() {
	return "SolverDescription@" + Integer.toHexString(hashCode()) + "(" + getDisplayLabel() + ")";
}

public Set<SolverFeature> getSupportedFeatures() {
	Set<SolverFeature> featureSet = new HashSet<SolverFeature>();
	
	switch (type) {
	case TYPE_FORWARD_EULER:
		featureSet.add(SolverFeature.Feature_NonSpatial);
		featureSet.add(SolverFeature.Feature_Deterministic);
		featureSet.add(SolverFeature.Feature_FastSystem);
		featureSet.add(SolverFeature.Feature_JVMRequired);
		break;
		
	case TYPE_RUNGE_KUTTA2:
	case TYPE_RUNGE_KUTTA4:
	case TYPE_RUNGE_KUTTA_FEHLBERG:
	case TYPE_ADAMS_MOULTON:
		featureSet.add(SolverFeature.Feature_NonSpatial);
		featureSet.add(SolverFeature.Feature_Deterministic);
		featureSet.add(SolverFeature.Feature_JVMRequired);
		break;
	
	case TYPE_CVODE:
		featureSet.add(SolverFeature.Feature_NonSpatial);
		featureSet.add(SolverFeature.Feature_Deterministic);
		featureSet.add(SolverFeature.Feature_StopAtTimeDiscontinuities);
		featureSet.add(SolverFeature.Feature_StopAtGeneralDiscontinuities);
		featureSet.add(SolverFeature.Feature_Events);
		break;
		
	case TYPE_IDA:
	case TYPE_COMBINED_IDA_CVODE:
		featureSet.add(SolverFeature.Feature_NonSpatial);
		featureSet.add(SolverFeature.Feature_Deterministic);
		featureSet.add(SolverFeature.Feature_FastSystem);
		featureSet.add(SolverFeature.Feature_StopAtTimeDiscontinuities);
		featureSet.add(SolverFeature.Feature_StopAtGeneralDiscontinuities);
		featureSet.add(SolverFeature.Feature_Events);
		break;

	case TYPE_STOCH_GIBSON:
	case TYPE_HYBRID_EM:
	case TYPE_HYBRID_MIL:
	case TYPE_HYBRID_MIL_Adaptive:
		featureSet.add(SolverFeature.Feature_NonSpatial);
		featureSet.add(SolverFeature.Feature_Stochastic);
		break;
		
	case TYPE_FINITE_VOLUME:
		featureSet.add(SolverFeature.Feature_Spatial);
		featureSet.add(SolverFeature.Feature_Deterministic);
		featureSet.add(SolverFeature.Feature_FastSystem);
		featureSet.add(SolverFeature.Feature_StopAtSpatiallyUniform);
		featureSet.add(SolverFeature.Feature_PeriodicBoundaryCondition);
		break;
		
	case TYPE_FINITE_VOLUME_STANDALONE:
		featureSet.add(SolverFeature.Feature_Spatial);
		featureSet.add(SolverFeature.Feature_Deterministic);
		featureSet.add(SolverFeature.Feature_FastSystem);
		featureSet.add(SolverFeature.Feature_RandomVariables);
		featureSet.add(SolverFeature.Feature_StopAtSpatiallyUniform);
		featureSet.add(SolverFeature.Feature_DataProcessingInstructions);
		featureSet.add(SolverFeature.Feature_PSF);
		featureSet.add(SolverFeature.Feature_PeriodicBoundaryCondition);
		featureSet.add(SolverFeature.Feature_SerialParameterScans);
		featureSet.add(SolverFeature.Feature_VolumeRegionEquation);
		break;
		
	case TYPE_SUNDIALS_PDE:
		featureSet.add(SolverFeature.Feature_Spatial);
		featureSet.add(SolverFeature.Feature_Deterministic);
		featureSet.add(SolverFeature.Feature_StopAtTimeDiscontinuities);
		featureSet.add(SolverFeature.Feature_RandomVariables);
		featureSet.add(SolverFeature.Feature_StopAtSpatiallyUniform);
		featureSet.add(SolverFeature.Feature_DataProcessingInstructions);
		featureSet.add(SolverFeature.Feature_PSF);
		featureSet.add(SolverFeature.Feature_SerialParameterScans);
		featureSet.add(SolverFeature.Feature_VolumeRegionEquation);
		break;
	}
	
	return featureSet;
}

//public static void main(String[] args) {
//	for (int i = 0; i < NUM_SOLVERS; i ++) {
//		org.vcell.util.gui.DialogUtils.showInfoDialog(null, FULL_DESCRIPTIONS[i]);
//	}
//}
}