/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */


package cbit.vcell.solver;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import cbit.vcell.client.constants.VCellCodeVersion;
import cbit.vcell.math.ProblemRequirements;
import cbit.vcell.resource.VersionedLibrary;


/**
 * Insert the type's description here.
 * Creation date: (4/23/01 3:34:06 PM)
 * @author: Jim Schaff
 * Stochastic description is added on 12th July 2006.
 */
public enum SolverDescription {
	   ForwardEuler(TimeStep.CONSTANT, ErrorTol.NO, TimeSpecCreated.DEFAULT, "Forward Euler 1st","Forward Euler (First Order, Fixed Time Step)","Forward Euler (First Order, Fixed Time Step)",
	      SolverLongDesc.FORWARD_EULER, 1,SupportedTimeSpec.DEFAULT,
	      new SolverFeature[]{SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_FastSystem, SolverFeature.Feature_JVMRequired},
	      null, null, "KISAO:0000030", false),

	   RungeKutta2(TimeStep.CONSTANT, ErrorTol.NO, TimeSpecCreated.DEFAULT, "Runge-Kutta 2nd","Runge-Kutta (Second Order, Fixed Time Step)","Runge-Kutta (Second Order, Fixed Time Step)",
	      SolverLongDesc.RUNGE_KUTTA2, 2,SupportedTimeSpec.DEFAULT,
	      new SolverFeature[]{SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_JVMRequired},
	      null, null, "KISAO:0000064", false),

	   RungeKutta4(TimeStep.CONSTANT, ErrorTol.NO, TimeSpecCreated.DEFAULT, "Runge-Kutta 4th","Runge-Kutta (Fourth Order, Fixed Time Step)","Runge-Kutta (Fourth Order, Fixed Time Step)",
	      SolverLongDesc.RUNGE_KUTTA4, 4,SupportedTimeSpec.DEFAULT,
	      new SolverFeature[]{SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_JVMRequired},
	      null, null, "KISAO:0000032", false),

	   RungeKuttaFehlberg(TimeStep.VARIABLE, ErrorTol.YES, TimeSpecCreated.DEFAULT, "Runge-Kutta-Fehlberg","Runge-Kutta-Fehlberg (Fifth Order, Variable Time Step)","Runge-Kutta-Fehlberg (Fifth Order, Variable Time Step)",
	      SolverLongDesc.RUNGE_KUTTA_FEHLBERG, 4,SupportedTimeSpec.DEFAULT,
	      new SolverFeature[]{SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_JVMRequired},
	      null, null, "KISAO:0000086", false),

	   AdamsMoulton(TimeStep.CONSTANT, ErrorTol.NO, TimeSpecCreated.DEFAULT, "Adams-Moulton 5th","Adams-Moulton (Fifth Order, Fixed Time Step)","Adams-Moulton (Fifth Order, Fixed Time Step)",
	      SolverLongDesc.ADAMS_MOULTON, 5,SupportedTimeSpec.DEFAULT,
	      new SolverFeature[]{SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_JVMRequired},
	      null, null, "KISAO:0000280", false),

	   IDA(TimeStep.VARIABLE, ErrorTol.YES, TimeSpecCreated.DEFAULT, "IDA","IDA (Variable Order, Variable Time Step, ODE/DAE)","IDA (Variable Order, Variable Time Step, ODE/DAE)",
	      SolverLongDesc.IDA, 3,SupportedTimeSpec.DEFAULT_EXPLICIT_UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_FastSystem, SolverFeature.Feature_StopAtTimeDiscontinuities, SolverFeature.Feature_StopAtGeneralDiscontinuities, SolverFeature.Feature_Events},
	      SolverExecutable.SundialsOde, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO:0000283", false),

	      /**
	       * this is the compiled Solver we don't support any more. Left present to load old models that use it, but they
	       * won't execute
	       */
	   @Deprecated
	   FiniteVolume(TimeStep.CONSTANT, ErrorTol.NO, TimeSpecCreated.DEFAULT, "Semi-Implicit Compiled","Semi-Implicit Finite Volume Compiled, Regular Grid (Fixed Time Step) (DEPRECATED)","Finite Volume, Regular Grid",
	      SolverLongDesc.FINITE_VOLUME, 1,SupportedTimeSpec.DEFAULT_UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_Spatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_FastSystem, SolverFeature.Feature_StopAtSpatiallyUniform},
	      SolverExecutable.FiniteVolume, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO:0000285", true),

	   StochGibson(TimeStep.VARIABLE, ErrorTol.NO, TimeSpecCreated.UNIFORM, "Gibson","Gibson (Next Reaction Stochastic Method)","Gibson (Next Reaction Stochastic Method)",
	      SolverLongDesc.STOCH_GIBSON, 1,SupportedTimeSpec.DEFAULT_UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Stochastic},
	      SolverExecutable.Gibson, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO:0000027", false),

	   HybridEuler(TimeStep.CONSTANT, ErrorTol.NO, TimeSpecCreated.UNIFORM, "Gibson + Euler-Maruyama","Hybrid (Gibson + Euler-Maruyama Method)","Hybrid (Gibson + Euler-Maruyama Method)",
	      SolverLongDesc.HYBRID_EULER, 1,SupportedTimeSpec.UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Stochastic},
	      SolverExecutable.Hybrid_EM, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO:0000352", false),

	   HybridMilstein(TimeStep.CONSTANT, ErrorTol.NO, TimeSpecCreated.UNIFORM, "Gibson + Milstein","Hybrid (Gibson + Milstein Method)","Hybrid (Gibson + Milstein Method)",
	      SolverLongDesc.HYBRID_MILSTEIN, 1,SupportedTimeSpec.UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Stochastic},
	      SolverExecutable.Hybrid_Mil, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO:0000352", false),

	   HybridMilAdaptive(TimeStep.VARIABLE, ErrorTol.NO, TimeSpecCreated.UNIFORM, "Adaptive Gibson + Milstein","Hybrid (Adaptive Gibson + Milstein Method)","Hybrid (Adaptive Gibson + Milstein Method)",
	      SolverLongDesc.HYBRID_MIL_ADAPTIVE, 1,SupportedTimeSpec.UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Stochastic},
	      SolverExecutable.Hybrid_Mil_Adaptive, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO:0000352", false),

	   CVODE(TimeStep.VARIABLE, ErrorTol.YES, TimeSpecCreated.DEFAULT, "CVODE","CVODE (Variable Order, Variable Time Step)","CVODE (Variable Order, Variable Time Step)",
	      SolverLongDesc.CVODE, 3,SupportedTimeSpec.DEFAULT_EXPLICIT_UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_StopAtTimeDiscontinuities, SolverFeature.Feature_StopAtGeneralDiscontinuities, SolverFeature.Feature_Events},
	      SolverExecutable.SundialsOde, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO:0000019", false),

	   FiniteVolumeStandalone(TimeStep.CONSTANT, ErrorTol.NO, TimeSpecCreated.DEFAULT, "Semi-Implicit","Semi-Implicit Finite Volume-Particle Hybrid, Regular Grid (Fixed Time Step)","Finite Volume Standalone, Regular Grid",
	      SolverLongDesc.FINITE_VOLUME_STANDALONE, 1,SupportedTimeSpec.DEFAULT_UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_Spatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_Hybrid, SolverFeature.Feature_FastSystem, SolverFeature.Feature_PeriodicBoundaryCondition, SolverFeature.Feature_RandomVariables, SolverFeature.Feature_StopAtSpatiallyUniform, SolverFeature.Feature_DataProcessingInstructions, SolverFeature.Feature_PSF, SolverFeature.Feature_SerialParameterScans, SolverFeature.Feature_VolumeRegionEquations, SolverFeature.Feature_RegionSizeFunctions, SolverFeature.Feature_PostProcessingBlock},
	      SolverExecutable.FiniteVolume, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO:0000285", false),

	   CombinedSundials(TimeStep.VARIABLE, ErrorTol.YES, TimeSpecCreated.DEFAULT, "Combined IDA/CVODE","Combined Stiff Solver (IDA/CVODE)","Combined Stiff Solver (IDA/CVODE)",
	      SolverLongDesc.COMBINED_SUNDIALS, 3,SupportedTimeSpec.DEFAULT_EXPLICIT_UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_FastSystem, SolverFeature.Feature_StopAtTimeDiscontinuities, SolverFeature.Feature_StopAtGeneralDiscontinuities, SolverFeature.Feature_Events},
	      SolverExecutable.SundialsOde, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO:0000019", false),

       SundialsPDE(TimeStep.VARIABLE, ErrorTol.YES, TimeSpecCreated.UNIFORM, "Fully-Implicit","Fully-Implicit Finite Volume, Regular Grid (Variable Time Step)","Sundials Stiff PDE Solver (Variable Time Step)",
	      SolverLongDesc.SUNDIALS_PDE, 3,SupportedTimeSpec.DEFAULT_UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_Spatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_StopAtTimeDiscontinuities, SolverFeature.Feature_RandomVariables, SolverFeature.Feature_StopAtSpatiallyUniform, SolverFeature.Feature_DataProcessingInstructions, SolverFeature.Feature_PSF, SolverFeature.Feature_SerialParameterScans, SolverFeature.Feature_VolumeRegionEquations, SolverFeature.Feature_RegionSizeFunctions, SolverFeature.Feature_GradientSourceTerm, SolverFeature.Feature_PostProcessingBlock},
	      SolverExecutable.FiniteVolume, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO:0000285", false),

	   Smoldyn(TimeStep.CONSTANT, ErrorTol.NO, TimeSpecCreated.UNIFORM, "Smoldyn","Smoldyn (Spatial Stochastic Simulator)","Smoldyn (Spatial Stochastic Simulator)",
	      SolverLongDesc.SMOLDYN, 1,SupportedTimeSpec.UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_Spatial, SolverFeature.Feature_Stochastic, SolverFeature.Feature_PeriodicBoundaryCondition, SolverFeature.Feature_DataProcessingInstructions},
	      SolverExecutable.Smoldyn, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO:0000057", false),

	   Chombo(TimeStep.CONSTANT, ErrorTol.NO, TimeSpecCreated.UNIFORM, "EBChombo","EBChombo, Semi-Implicit (Fixed Time Step), Experimental","Chombo Standalone",
	      SolverLongDesc.CHOMBO, 1,SupportedTimeSpec.UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_Spatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_RegionSizeFunctions,
			   SolverFeature.Feature_DirichletAtMembraneBoundary, SolverFeature.Feature_Parallel,  SolverFeature.Feature_FastSystem},
	      SolverExecutable.VCellChombo, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO:0000285",
	      VCellCodeVersion.CURRENT.compare(5,4) < 0),

	   NFSim(TimeStep.VARIABLE, ErrorTol.NO, TimeSpecCreated.UNIFORM, "NFSim","NFSim (Network Free Simulator)","NFSim",
	      SolverLongDesc.NFSIM, 1,SupportedTimeSpec.UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Rulebased},
	      SolverExecutable.NFSIM, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO:0000263", false),

	   Comsol(TimeStep.VARIABLE,ErrorTol.NO,TimeSpecCreated.DEFAULT,"Comsol","Comsol Multiphysics","Comsol",
		  SolverLongDesc.COMSOL,1,SupportedTimeSpec.DEFAULT_UNIFORM,
		  new SolverFeature[]{SolverFeature.Feature_Spatial, /*  SolverFeature.Feature_Moving  ,*/ SolverFeature.Feature_Deterministic},
		  null, VersionedLibrary.NONE,"KISAO",false),

	   MovingBoundary(TimeStep.VARIABLE,ErrorTol.NO,TimeSpecCreated.DEFAULT,"Moving","Moving Boundary","MovingB",
		  SolverLongDesc.MB,1,SupportedTimeSpec.DEFAULT_UNIFORM,
		  new SolverFeature[]{SolverFeature.Feature_Spatial, SolverFeature.Feature_Moving, SolverFeature.Feature_Deterministic},
		  SolverExecutable.MOVING_B, VersionedLibrary.DEPENDENT_LIBS_VCELL,"KISAO",false),
	   
       VCellPetsc(TimeStep.CONSTANT, ErrorTol.NO, TimeSpecCreated.DEFAULT, "Petsc","Petsc, Regular Grid","VCellPetsc",
	      SolverLongDesc.VCellPetsc, 3,SupportedTimeSpec.DEFAULT_UNIFORM,
	      new SolverFeature[]{SolverFeature.Feature_Spatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_Hybrid, SolverFeature.Feature_SerialParameterScans, SolverFeature.Feature_RandomVariables, SolverFeature.Feature_StopAtSpatiallyUniform, SolverFeature.Feature_VolumeRegionEquations, SolverFeature.Feature_RegionSizeFunctions, SolverFeature.Feature_PostProcessingBlock},
	      SolverExecutable.FiniteVolume, VersionedLibrary.DEPENDENT_LIBS_VCELL, "KISAO", false),
	   
   ;

	public interface SupportedProblemRequirements {
		boolean supports(ProblemRequirements desc);
	}
	
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
		Feature_VolumeRegionEquations("Volume Region Equations"),
		Feature_RegionSizeFunctions("Region Size Functions"),
		Feature_GradientSourceTerm("Gradient Source Term"),
		Feature_PostProcessingBlock("Post Processing"),
		Feature_DirichletAtMembraneBoundary("Dirichlet (Value) Boundary Condition at Membrane"),
		Feature_Rulebased("Rule based"),
		Feature_ServerOnly("Server only execution"),
		Feature_Parallel("Parallel execution"),
		Feature_Hybrid("Hybrid: both Deterministic and Stochastic"),
		Feature_Moving("Moving Membrane"),
		;

		private final String name;
		private SolverFeature(String name) {
			this.name = name;
		}
		public final String getName() {
			return name;
		}
	}

	/*
	 * Spatial solvers
	 */
	public static final SolverFeatureSet SpatialHybridFeatureSet = new SolverFeatureSet (
		new SolverFeature[] { SolverFeature.Feature_Spatial, SolverFeature.Feature_Hybrid, SolverFeature.Feature_Deterministic },
		new SupportedProblemRequirements() { public boolean supports(ProblemRequirements desc) { 
			return desc.isSpatialHybrid() && !desc.isMovingMembrane(); }},
		FiniteVolumeStandalone,50);

	public static final SolverFeatureSet SpatialStochasticFeatureSet = new SolverFeatureSet (
		new SolverFeature[] { SolverFeature.Feature_Spatial, SolverFeature.Feature_Stochastic },
		new SupportedProblemRequirements() { public boolean supports(ProblemRequirements desc) { 
			return desc.isSpatialStoch() && !desc.isSpatialHybrid(); }},
		Smoldyn,40);

	public static final SolverFeatureSet PdeFastSystemFeatureSet = new SolverFeatureSet (
		new SolverFeature[] { SolverFeature.Feature_Spatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_FastSystem },
		new SupportedProblemRequirements() { public boolean supports(ProblemRequirements desc) {
			return desc.isSpatial() && !desc.isSpatialHybrid() && desc.hasFastSystems( ) && !desc.isSpatialStoch(); }},
		FiniteVolumeStandalone,30);

	public static final SolverFeatureSet PdeFeatureSetWithDirichletAtMembrane =  new SolverFeatureSet(
		new SolverFeature[] { SolverFeature.Feature_DirichletAtMembraneBoundary },
		new SupportedProblemRequirements() { public boolean supports(ProblemRequirements s) {
			return s.isSpatial() &&  !s.isSpatialHybrid( )  && s.hasDirichletAtMembrane() && !s.hasFastSystems() && !s.isSpatialStoch(); }},
		Chombo,20);

	public static final SolverFeatureSet PdeFeatureSetWithoutDirichletAtMembrane = new SolverFeatureSet(
		new SolverFeature[] { SolverFeature.Feature_Spatial, SolverFeature.Feature_Deterministic },
		new SupportedProblemRequirements() { public boolean supports(ProblemRequirements s) {
			return s.isSpatial() && !s.isSpatialHybrid()  && !s.isMovingMembrane() && !s.hasDirichletAtMembrane() && !s.hasFastSystems() && !s.isSpatialStoch(); }},
		SundialsPDE,10);

//	public static final SolverFeatureSet ComsolFeatureSet = new SolverFeatureSet(
//			new SolverFeature[] { SolverFeature.Feature_Spatial,SolverFeature.Feature_Deterministic },
//			new SupportedProblemRequirements() { public boolean supports(ProblemRequirements s) {
//				return s.isSpatial() && !s.isSpatialHybrid() && !s.hasFastSystems() && !s.isSpatialStoch(); }},
//			Comsol,30);
//
	public static final SolverFeatureSet MovingBoundaryFeatureSet = new SolverFeatureSet(
			new SolverFeature[] { SolverFeature.Feature_Moving,SolverFeature.Feature_Spatial,SolverFeature.Feature_Deterministic },
			new SupportedProblemRequirements() { public boolean supports(ProblemRequirements s) {
				return s.isSpatial() && !s.isSpatialHybrid()  && s.isMovingMembrane() && !s.hasFastSystems() && !s.isSpatialStoch(); }},
			MovingBoundary,30);

	/*
	 * rule-based solvers
	 */
	public static final SolverFeatureSet RulebasedFeatureSet = new SolverFeatureSet (
		new SolverFeature[] { SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Rulebased },
		new SupportedProblemRequirements() { public boolean supports(ProblemRequirements desc) { return desc.isRuleBased(); }},
		NFSim,100);

	/*
	 * Non-spatial solvers
	 */
	public static final SolverFeatureSet NonSpatialStochasticFeatureSet = new SolverFeatureSet (
		new SolverFeature[] { SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Stochastic},
		new SupportedProblemRequirements() { public boolean supports(ProblemRequirements desc) { return desc.isNonSpatialStoch() && !desc.isRuleBased(); }},
		StochGibson,100);

	public static final SolverFeatureSet OdeFeatureSet =  new SolverFeatureSet(
		new SolverFeature[] { SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Deterministic},
		new SupportedProblemRequirements() { public boolean supports(ProblemRequirements desc) {
			return !desc.isSpatial() && !desc.hasFastSystems() && !desc.isNonSpatialStoch() && !desc.isRuleBased(); }},
		CombinedSundials,10);

	public static final SolverFeatureSet OdeFastSystemFeatureSet =  new SolverFeatureSet(
		new SolverFeature[] { SolverFeature.Feature_NonSpatial, SolverFeature.Feature_Deterministic, SolverFeature.Feature_FastSystem},
		new SupportedProblemRequirements() { public boolean supports(ProblemRequirements desc) {
			return !desc.isSpatial() && desc.hasFastSystems() && !desc.isNonSpatialStoch() && !desc.isRuleBased(); }},
		CombinedSundials,10);

	//this one is not like the others
	public static final Collection<SolverFeature> DiscontinutiesFeatures = Arrays.asList(new SolverFeature[]{
		SolverFeature.Feature_StopAtTimeDiscontinuities, SolverFeature.Feature_StopAtGeneralDiscontinuities
	});
	
	/**
	 * explicit boolean for readability
	 */
	private static enum TimeSpecCreated {
		UNIFORM, DEFAULT
	}

	/**
	 * explicit boolean for readability
	 */
	private static enum TimeStep {
		CONSTANT,
		VARIABLE
	}

	/**
	 * explicit boolean for readability
	 */
	private static enum ErrorTol {
		NO,
		YES
	}
	/**
	 * implements {@link #supports(OutputTimeSpec)}
	 */
	private enum SupportedTimeSpec {
		DEFAULT_EXPLICIT_UNIFORM(true,true,true),
		DEFAULT_UNIFORM(true,false,true),
		UNIFORM(false,false,true),
		DEFAULT(true,false,false),
		;
		final boolean def;
		final boolean explicit;
		final boolean uniform;
		SupportedTimeSpec(boolean def, boolean explicit, boolean uniform) {
			this.def = def;
			this.explicit = explicit;
			this.uniform = uniform;
		}

		boolean supports(OutputTimeSpec outputTimeSpec) {
			return (def && outputTimeSpec.isDefault() )
				|| (explicit && outputTimeSpec.isExplicit() )
				|| (uniform  && outputTimeSpec.isUniform( ) );
		}
	}

	private final boolean variableTimeStep;
	private final boolean errorTolerance;
	private final TimeSpecCreated timeSpecType;
	private final String shortDisplayLabel;
	private final String displayLabel;
	private final String databaseName;
	private final String fullDescription;
	private final URL fullDescriptionUrl;
	private final int timeOrder;
	private final SupportedTimeSpec supportedTimeSpec;
	private final Set<SolverFeature> supportedFeatures;
	private final SolverExecutable solverExecutable;
	/**
	 * will not be null, may be placeholder
	 */
	public final VersionedLibrary versionedLibrary;
	public final String kisao;
	public final boolean deprecated;

	private SolverDescription(TimeStep ts, ErrorTol et,TimeSpecCreated tst,
			String shortDisplayLabel,
			String displayLabel, String databaseName,
			String fullDescription, int timeOrder, SupportedTimeSpec sts,
			SolverFeature[] fset,
			SolverExecutable se, VersionedLibrary versionedLibrary, String kisao, boolean deprecated) {

		variableTimeStep = (ts == TimeStep.VARIABLE);
		errorTolerance = ( et == ErrorTol.YES);
		timeSpecType = tst;
		this.shortDisplayLabel = shortDisplayLabel;
		this.displayLabel = displayLabel;
		this.databaseName = databaseName;
		this.fullDescription = subFullDescription(fullDescription,displayLabel);
		this.fullDescriptionUrl = null;
		this.timeOrder = timeOrder;
		supportedTimeSpec = sts;
		this.supportedFeatures = new HashSet<SolverFeature>(Arrays.asList(fset));
		solverExecutable = se;
		this.versionedLibrary = versionedLibrary != null ? versionedLibrary : VersionedLibrary.NONE;
		this.kisao = kisao;
		this.deprecated = deprecated;
	}
	
	private SolverDescription(TimeStep ts, ErrorTol et,TimeSpecCreated tst,
			String shortDisplayLabel,
			String displayLabel, String databaseName,
			URL fullDescriptionUrl, int timeOrder, SupportedTimeSpec sts,
			SolverFeature[] fset,
			SolverExecutable se, VersionedLibrary versionedLibrary, String kisao, boolean deprecated) {
		
		variableTimeStep = (ts == TimeStep.VARIABLE);
		errorTolerance = ( et == ErrorTol.YES);
		timeSpecType = tst;
		this.shortDisplayLabel = shortDisplayLabel;
		this.displayLabel = displayLabel;
		this.databaseName = databaseName;
		this.fullDescription = null;
		this.fullDescriptionUrl = fullDescriptionUrl;
		this.timeOrder = timeOrder;
		supportedTimeSpec = sts;
		this.supportedFeatures = new HashSet<SolverFeature>(Arrays.asList(fset));
		solverExecutable = se;
		this.versionedLibrary = versionedLibrary != null ? versionedLibrary : VersionedLibrary.NONE;
		this.kisao = kisao;
		this.deprecated = deprecated;
	}
	
	public SolverExecutable getSolverExecutable() {
		return solverExecutable;
	}

	public String getShortDisplayLabel() {
		return shortDisplayLabel;
	}

	public String getDisplayLabel() {
		return displayLabel;
	}

	public String getKisao() {
		return kisao;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getFullDescription() {
		String fullDesc = fullDescription;
		if (fullDesc == null && fullDescriptionUrl != null)
		{
			try
			{
				fullDesc = IOUtils.toString(fullDescriptionUrl.openStream());
				fullDesc = subFullDescription(fullDesc, displayLabel);
			}
			catch (IOException ex)
			{
				// ignore
			}
		}
		return fullDesc;
	}

	/**
	 * does this require a special license?
	 * @param sl license to test for
	 * @return true if it does
	 */
	/*
	public boolean requiresLicense(LicenseLibrary sl) {
		return licensedLibrary == sl;
	}
	*/

	/**
	 * replace DISPLAY_LABEL_TOKEN with displayName
	 * @param full not null
	 * @param displayName null
	 * @throws AssertionError
	 */
	private static String subFullDescription(String full, String displayName) {
		assert full != null;
		assert full != displayName;
		return full.replace("DISPLAY_LABEL_TOKEN", displayName);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/8/2005 11:27:58 AM)
	 * @return cbit.vcell.solver.OutputTimeSpec
	 * @param solverTaskDescription cbit.vcell.solver.SolverTaskDescription
	 */
	public OutputTimeSpec createOutputTimeSpec(SolverTaskDescription solverTaskDescription)
	{
		switch (timeSpecType) {
		case UNIFORM:
			return new UniformOutputTimeSpec(0.05);
		case DEFAULT:
			return new DefaultOutputTimeSpec();
		default:
			throw new IllegalStateException("no time spec for " + timeSpecType);
		}
	}

	//package access for JUnit testing
	static final String ALTERNATE_CVODE_Description = "LSODA (Variable Order, Variable Time Step)"; // backward compatibility
	private static final Map<String,SolverDescription> displayNameMap = new HashMap<String, SolverDescription>();
	private static final Map<String,SolverDescription> dbNameMap = new HashMap<String, SolverDescription>();
	//load up the maps
	static {
		for (SolverDescription sd : SolverDescription.values()) {
			displayNameMap.put(sd.displayLabel, sd);
			dbNameMap.put(sd.databaseName, sd);
		}
		//backward compatibility special case
		dbNameMap.put(ALTERNATE_CVODE_Description,CVODE);
	}

	/**
	 * lookup name in given map
	 * @param map not null
	 * @param name may be null
	 * @return null if name null, SolverDescription otherwise
	 * @throws IllegalArgumentException if invalid name
	 */
	private static SolverDescription mapLookup(Map<String,SolverDescription> map, String name) {
		if (name != null) {
			SolverDescription sd = map.get(name);
			if (sd != null) {
				return sd;
			}
			throw new IllegalArgumentException("unexpected solver name '"+name+"'");
		}
		return null;
	}

	/**
	 * lookup by database name
	 * @return null if solverNamename null, SolverDescription otherwise
	 * @throws IllegalArgumentException if invalid name
	*/
	public static SolverDescription fromDatabaseName(String solverName) {
		return mapLookup(dbNameMap,solverName);
	}

	/**
	 * lookup by display name
	 * @return null if solverNamename null, SolverDescription otherwise
	 * @throws IllegalArgumentException if invalid name
	*/
	public static SolverDescription fromDisplayLabel(String label) {
		return mapLookup(displayNameMap,label);
	}

	public boolean hasVariableTimestep() {
		return variableTimeStep;
	}

	public boolean hasErrorTolerance() {
		return errorTolerance;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/01 4:49:19 PM)
	 * @return boolean
	 */
	public boolean isJavaSolver() {
		return supportedFeatures.contains(SolverFeature.Feature_JVMRequired);
	}

	/**
	 * Check whether the solver is stochastic solver or not.
	 * Creation date: (7/18/2006 5:08:30 PM)
	 * @return boolean
	 */
	public boolean isNonSpatialStochasticSolver() {
		return supportedFeatures.containsAll(NonSpatialStochasticFeatureSet.getSolverFeatures());
	}

	public boolean isGibsonSolver(){
		return this == StochGibson;
	}
	
	public boolean isMovingBoundarySolver(){
		return this == MovingBoundary;
	}
	
	public boolean isComsolSolver(){
		return this == Comsol;
	}

	public boolean isSpatialStochasticSolver() {
		return supportedFeatures.containsAll(SpatialStochasticFeatureSet.getSolverFeatures());
	}

	public boolean supports(OutputTimeSpec outputTimeSpec) {
		return supportedTimeSpec.supports(outputTimeSpec);
	}

	public boolean hasSundialsTimeStepping() {
		switch (this) {
		case CVODE:
		case IDA:
		case CombinedSundials:
		case SundialsPDE:
			return true;
		default:
			return false;
		}
	}

	public boolean isSemiImplicitPdeSolver() {
		switch (this) {
		case FiniteVolume:
		case FiniteVolumeStandalone:
			return true;
		default:
			return false;
		}
	}

	public static Collection<SolverDescription> getSolverDescriptions(Collection<SolverFeature> solverFeatures){
		ArrayList<SolverDescription> solvers = new ArrayList<SolverDescription>();
		for (SolverDescription sd : values( )) {
			if (sd.supportsAll(solverFeatures)) {
				solvers.add(sd);
			}
		}
		return solvers;
	}

	public int getTimeOrder() {
		return timeOrder;
	}

	/**
	 * contains all features in collection? XXXX
	 */
	public boolean supportsAll(Collection<SolverFeature> features) {
		return supportedFeatures.containsAll(features);
	}

	/**
	 * contains this particular feature?
	 */
	public boolean supports(SolverFeature feature) {
		return supportedFeatures.contains(feature);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/01 3:52:28 PM)
	 * @return java.lang.String
	 */
	/*
	public String toString() {
		return "SolverDescription@" + Integer.toHexString(ordinal()) + "(" + getDisplayLabel() + ")";
	}
	*/

	/**
	 * @return read-only set of supported features
	 */
	public Set<SolverFeature> getSupportedFeatures() {
		return Collections.unmodifiableSet(supportedFeatures);
	}

	/**
	 * get solvers which support math description
	 * @param mathDescription
	 * @return non-empty collection
	 * @throws IllegalStateException if mathDescription has invalid state
	 * @throws UnsupportedOperationException if state not supported
	 */
	public static Collection<SolverDescription> getSupportingSolverDescriptions(ProblemRequirements mathDescription) {
		ProblemRequirements.Checker.validate(mathDescription);
		Collection<SolverDescription> solvers = new HashSet<SolverDescription>( );
		for (SolverFeatureSet sfs : SolverFeatureSet.getSets()) {
			if (sfs.supports(mathDescription)) {
				solvers.addAll(getSolverDescriptions(sfs.getSolverFeatures()));
			}
		}
		if (!solvers.isEmpty( )) {
			return solvers;
		}
		throw new UnsupportedOperationException("Can't get descriptions for " + ProblemRequirements.Explain.describe(mathDescription) );
	}

	/**
	 * default solver for math description
	 * @param mathDescription
	 * @return non null SolverDescription
	 * @throws IllegalStateException if mathDescription has invalid state
	 */
	public static SolverDescription getDefaultSolverDescription(ProblemRequirements mathDescription) {
		ProblemRequirements.Checker.validate(mathDescription);
		SolverFeatureSet best = null;
		for (SolverFeatureSet sfs : SolverFeatureSet.getSets()) {
			if (sfs.supports(mathDescription)) {
				best = SolverFeatureSet.getHigherSolverPriority(best, sfs);
			}
		}
		if (best != null) {
			return best.getDefaultSolver();
		}
		throw new UnsupportedOperationException("Can't get default solver for " + mathDescription);
	}

	public boolean isChomboSolver()
	{
		return this == Chombo;
	}

	public boolean isNFSimSolver()
	{
		return this == NFSim;
	}

	/**
	 * backward compatiblity
	 * @param other
	 * @return true if they'[re the same
	 */
	public boolean compareEqual(SolverDescription other) {
		return this == other;
	}

	public boolean hasCellCenteredMesh()
	{
		return isChomboSolver() || isMovingBoundarySolver();
	}
}