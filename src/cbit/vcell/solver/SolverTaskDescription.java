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
import java.beans.PropertyVetoException;
import java.util.Collection;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.vcell.chombo.ChomboSolverSpec;
import org.vcell.chombo.TimeInterval;
import org.vcell.util.BeanUtils;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;

import cbit.vcell.math.Constant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.math.VCML;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.SolverDescription.SolverFeature;

/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:15 PM)
 * @author: John Wagner
 */
@SuppressWarnings("serial")
public class SolverTaskDescription implements Matchable, java.beans.PropertyChangeListener, java.io.Serializable {
	public static final String PROPERTY_ERROR_TOLERANCE = "errorTolerance";
	public static final String PROPERTY_USE_SYMBOLIC_JACOBIAN = "useSymbolicJacobian";
	public static final String PROPERTY_OUTPUT_TIME_SPEC = "outputTimeSpec";
	public static final String PROPERTY_SOLVER_DESCRIPTION = "solverDescription";
	public static final String PROPERTY_TIME_BOUNDS = "timeBounds";
	public static final String PROPERTY_TIME_STEP = "timeStep";
	public static final String PROPERTY_STOCH_SIM_OPTIONS = "StochSimOptions";
	public static final String PROPERTY_STOCH_HYBRID_OPTIONS = "StochHybridOptions";
	public static final String PROPERTY_STOP_AT_SPATIALLY_UNIFORM_ERROR_TOLERANCE = "stopAtSpatiallyUniformErrorTolerance";
	public static final String PROPERTY_SMOLDYN_SIMULATION_OPTIONS = "smoldynSimulationOptions";
	public static final String PROPERTY_NFSIM_SIMULATION_OPTIONS = "nfsimSimulationOptions";
	public static final String PROPERTY_SUNDIALS_SOLVER_OPTIONS = "sundialsSolverOptions";

	//  Or TASK_NONE for use as a default?
	public static final int TASK_UNSTEADY = 0;
	public static final int TASK_STEADY   = 1;

	private static Logger lg = Logger.getLogger(SolverTaskDescription.class);
	//
	private int fieldTaskType = TASK_UNSTEADY;
	private Constant fieldSensitivityParameter = null;
	private Simulation fieldSimulation = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private TimeBounds fieldTimeBounds = new TimeBounds();
	private TimeStep fieldTimeStep = new TimeStep();
	private ErrorTolerance fieldErrorTolerance = new ErrorTolerance();
	private SolverDescription fieldSolverDescription;
	private boolean fieldUseSymbolicJacobian = false;
	private OutputTimeSpec fieldOutputTimeSpec = new DefaultOutputTimeSpec();
	private NonspatialStochSimOptions fieldNonspatialStochOpt = null; //added Dec 5th, 2006
	private NonspatialStochHybridOptions fieldNonspatialStochHybridOpt = null; //added June 17, 2016
	private ErrorTolerance stopAtSpatiallyUniformErrorTolerance = null;
	private boolean bSerialParameterScan = false;
	private SmoldynSimulationOptions smoldynSimulationOptions = null;
	private NFsimSimulationOptions nfsimSimulationOptions = null;
	private SundialsPdeSolverOptions sundialsPdeSolverOptions = null;
	private ChomboSolverSpec chomboSolverSpec = null;
	/**
	 * number of parallel processors to use for solution, if supported by
	 * select solver
	 */
	private int numProcessors = 1;

	/**
	 * One of three ways to construct a SolverTaskDescription.  This constructor
	 * is used when creating a SolverTaskDescription from the database.
	 */
	public SolverTaskDescription(Simulation simulation, CommentStringTokenizer tokenizer) throws DataAccessException {
		super();
		addPropertyChangeListener(this);
		setSimulation(simulation);
		readVCML(tokenizer);
		resetSolverTaskDescriptionIfNecessary();
	}

	/**
	 * This constructor is for management console and VCell API only.
	 */
	public SolverTaskDescription(CommentStringTokenizer tokenizer) throws DataAccessException {
		super();
		addPropertyChangeListener(this);
		readVCML(tokenizer);
	}

	/**
	 * One of three ways to construct a SolverTaskDescription.  This constructor
	 * is used when creating a new SolverTaskDescription.
	 */
	public SolverTaskDescription(Simulation simulation) {
		super();
		addPropertyChangeListener(this);
		setSimulation(simulation);
		resetSolverTaskDescriptionIfNecessary();
	}

	/**
	 * One of three ways to construct a SolverTaskDescription.  This constructor
	 * is used when copying a SolverTaskDescription from an existing one.
	 */
	public SolverTaskDescription(Simulation simulation, SolverTaskDescription solverTaskDescription) {
		super();
		addPropertyChangeListener(this);

		setSimulation(simulation);
		//
		fieldTaskType = solverTaskDescription.getTaskType();
		fieldTimeBounds = new TimeBounds(solverTaskDescription.getTimeBounds());
		fieldTimeStep = new TimeStep(solverTaskDescription.getTimeStep());
		fieldErrorTolerance = new ErrorTolerance(solverTaskDescription.getErrorTolerance());
		try {
			fieldOutputTimeSpec = (OutputTimeSpec)BeanUtils.cloneSerializable(solverTaskDescription.getOutputTimeSpec());
		}catch (Exception e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
		fieldSensitivityParameter = solverTaskDescription.getSensitivityParameter();
		fieldSolverDescription = solverTaskDescription.getSolverDescription();
		fieldUseSymbolicJacobian = solverTaskDescription.getUseSymbolicJacobian();
		if (solverTaskDescription.stopAtSpatiallyUniformErrorTolerance != null) {
			stopAtSpatiallyUniformErrorTolerance = new ErrorTolerance(solverTaskDescription.stopAtSpatiallyUniformErrorTolerance);
		}
		bSerialParameterScan = solverTaskDescription.bSerialParameterScan;
		if (simulation.getMathDescription().isNonSpatialStoch() && (solverTaskDescription.getStochOpt() != null))
		{
			setStochOpt(solverTaskDescription.getStochOpt());
		}
		else {
			setStochOpt(null);
		}
		if (simulation.getMathDescription().isNonSpatialStoch() && 
			(!solverTaskDescription.getSolverDescription().isGibsonSolver()) &&
			(solverTaskDescription.getStochHybridOpt() != null))
		{
			setStochHybridOpt(solverTaskDescription.getStochHybridOpt());
		}
		else {
			setStochHybridOpt(null);
		}
		if (simulation.getMathDescription().isSpatialStoch() || simulation.getMathDescription().isSpatialHybrid()) {
			smoldynSimulationOptions = new SmoldynSimulationOptions(solverTaskDescription.smoldynSimulationOptions);
		} else {
			smoldynSimulationOptions = null;
		}
		if (simulation.getMathDescription().isRuleBased()) {
			nfsimSimulationOptions = new NFsimSimulationOptions(solverTaskDescription.nfsimSimulationOptions);
		} else {
			nfsimSimulationOptions = null;
		}
		if (fieldSolverDescription.equals(SolverDescription.SundialsPDE)) {
			sundialsPdeSolverOptions = new SundialsPdeSolverOptions(solverTaskDescription.sundialsPdeSolverOptions);
		} else {
			sundialsPdeSolverOptions = null;
		}
		if (solverTaskDescription.chomboSolverSpec != null) {
			chomboSolverSpec = new ChomboSolverSpec(solverTaskDescription.chomboSolverSpec);
		} else {
			chomboSolverSpec = null;
		}
		numProcessors = solverTaskDescription.numProcessors;
	}
	
	private void resetSolverTaskDescriptionIfNecessary() {
		final MathDescription md = fieldSimulation.getMathDescription();
		if (fieldSolverDescription != null) {
			Collection<SolverDescription> valid = SolverDescription.getSupportingSolverDescriptions(md);
			if (valid.contains(fieldSolverDescription)) {
				if (lg.isTraceEnabled()) {
					lg.trace("resetSolverTaskDescription leaving " + fieldSolverDescription + " in place");
				}
				return; //existing is okay, don't mess with it
			}
			if (lg.isTraceEnabled()) {
				lg.trace("resetSolverTaskDescription replacing existing " + fieldSolverDescription);
			}
		}
		try {
			setSolverDescription(SolverDescription.getDefaultSolverDescription(md));
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to set SolverDescription for simulation "+getSimulation().getName(),e);
		}
	}
	


	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}


	/**
	 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
	 */
	public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
		getVetoPropertyChange().addVetoableChangeListener(listener);
	}

	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 * @param object java.lang.Object
	 */
	public boolean compareEqual(Matchable object) {
		if (this == object) {
			return (true);
		}
		if (object != null && object instanceof SolverTaskDescription) {
			SolverTaskDescription solverTaskDescription = (SolverTaskDescription) object;
			if (getTaskType() != solverTaskDescription.getTaskType()) {
				return false;
			}
			//
			if (!getTimeBounds().compareEqual(solverTaskDescription.getTimeBounds())) {
				return false;
			}
			if (!getTimeStep().compareEqual(solverTaskDescription.getTimeStep())) {
				return false;
			}
			if (!getErrorTolerance().compareEqual(solverTaskDescription.getErrorTolerance())) {
				return false;
			}
			if  (!Compare.isEqualOrNull(getStochOpt(),solverTaskDescription.getStochOpt())) {
				return false;
			}
			if (getUseSymbolicJacobian() != solverTaskDescription.getUseSymbolicJacobian()) {
				return false;
			}
			if (!getOutputTimeSpec().compareEqual(solverTaskDescription.getOutputTimeSpec())) {
				return false;
			}
			if (!Compare.isEqualOrNull(getSensitivityParameter(),solverTaskDescription.getSensitivityParameter())) {
				return false;
			}
			if (!Compare.isEqual(getSolverDescription(),solverTaskDescription.getSolverDescription())) {
				return false;
			}
			if (getTaskType() != solverTaskDescription.getTaskType()) {
				return false;
			}
			if (!Compare.isEqualOrNull(stopAtSpatiallyUniformErrorTolerance, solverTaskDescription.stopAtSpatiallyUniformErrorTolerance)) {
				return false;
			}
			if (bSerialParameterScan != solverTaskDescription.bSerialParameterScan) {
				return false;
			}
			if  (!Compare.isEqualOrNull(smoldynSimulationOptions,solverTaskDescription.smoldynSimulationOptions)) {
				return false;
			}
			if  (!Compare.isEqualOrNull(sundialsPdeSolverOptions,solverTaskDescription.sundialsPdeSolverOptions)) {
				return false;
			}
			if (!Compare.isEqualOrNull(chomboSolverSpec,solverTaskDescription.chomboSolverSpec)) {
				return false;
			}
			if (!Compare.isEqualOrNull(nfsimSimulationOptions,solverTaskDescription.nfsimSimulationOptions)) {
				return false;
			}
			return numProcessors == solverTaskDescription.numProcessors;
		}
		return false;
	}

	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
	 */
	public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
		getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
	}

	/**
	 * Gets the errorTolerance property (cbit.vcell.solver.ErrorTolerance) value.
	 * @return The errorTolerance property value.
	 * @see #setErrorTolerance
	 */
	public ErrorTolerance getErrorTolerance() {
		return fieldErrorTolerance;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (9/7/2005 3:26:06 PM)
	 * @return int
	 */
	public long getExpectedNumTimePoints() {
		long numTimepoints = 0;
		double endingTime = getTimeBounds().getEndingTime();
		double startingTime = getTimeBounds().getStartingTime();

		if (fieldOutputTimeSpec.isDefault()) {
			int keepEvery = ((DefaultOutputTimeSpec)fieldOutputTimeSpec).getKeepEvery();
			int keepAtMost = ((DefaultOutputTimeSpec)fieldOutputTimeSpec).getKeepAtMost();

			if (getSolverDescription().hasVariableTimestep()){
				numTimepoints = keepAtMost;

			}else{ // fixed time step
				double timeStep = getTimeStep().getDefaultTimeStep();
				double saveInterval = timeStep * keepEvery;
				numTimepoints = (long)((endingTime - startingTime) / saveInterval);

				//
				// keepAtMost will limit the number of points for ODEs.
				// for PDEs, keepAtMost is ignored.
				//
				if (getSolverDescription().supports(SolverDescription.OdeFeatureSet)) {
					numTimepoints = Math.min(numTimepoints, keepAtMost);
				}
			}
		} else if (fieldOutputTimeSpec.isExplicit()) {
			numTimepoints = ((ExplicitOutputTimeSpec)fieldOutputTimeSpec).getNumTimePoints();
		} else if (fieldOutputTimeSpec.isUniform()) {
			double outputTimeStep = ((UniformOutputTimeSpec)fieldOutputTimeSpec).getOutputTimeStep();
			numTimepoints = (long)((endingTime - startingTime)/outputTimeStep);
		}
		return numTimepoints;
	}


	/**
	 * Gets the outputTimeSpec property (cbit.vcell.solver.OutputTimeSpec) value.
	 * @return The outputTimeSpec property value.
	 * @see #setOutputTimeSpec
	 */
	public OutputTimeSpec getOutputTimeSpec() {
		return fieldOutputTimeSpec;
	}


	/**
	 * Accessor for the propertyChange field.
	 */
	protected java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}


	/**
	 * Gets the sensitivityParameter property (cbit.vcell.math.Constant) value.
	 * @return The sensitivityParameter property value.
	 * @see #setSensitivityParameter
	 */
	public Constant getSensitivityParameter() {
		return fieldSensitivityParameter;
	}


	/**
	 * Gets the endingTime property (double) value.
	 * @return The endingTime property value.
	 * @see #setEndingTime
	 */
	public Simulation getSimulation() {
		return fieldSimulation;
	}


	/**
	 * Gets the solverDescription property (cbit.vcell.solver.SolverDescription) value.
	 * @return The solverDescription property value.
	 * @see #setSolverDescription
	 */
	public SolverDescription getSolverDescription() {
		return fieldSolverDescription;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return boolean
	 */
	public boolean getSteady() {
		return (getTaskType() == TASK_STEADY);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/6/2006 6:16:42 PM)
	 * @return cbit.vcell.solver.StochSimOptions
	 */
	public NonspatialStochSimOptions getStochOpt() {
		return fieldNonspatialStochOpt;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return double
	 */
	public int getTaskType() {
		return fieldTaskType;
	}


	/**
	 * Gets the timeBounds property (cbit.vcell.solver.TimeBounds) value.
	 * @return The timeBounds property value.
	 * @see #setTimeBounds
	 */
	public TimeBounds getTimeBounds() {
		//  Only here to ensure it is being used correctly.
		// cbit.util.Assertion.assertNotNull(fieldTimeBounds);
		return fieldTimeBounds;
	}


	/**
	 * Gets the timeStep property (cbit.vcell.solver.TimeStep) value.
	 * @return The timeStep property value.
	 * @see #setTimeStep
	 */
	public TimeStep getTimeStep() {
		//  Only here to ensure it is being used correctly.
		// cbit.util.Assertion.assertNotNull(fieldTimeStep);
		return fieldTimeStep;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return boolean
	 */
	public boolean getUnsteady() {
		return (getTaskType() == TASK_UNSTEADY);
	}


	/**
	 * Gets the useSymbolicJacobian property (boolean) value.
	 * @return The useSymbolicJacobian property value.
	 * @see #setUseSymbolicJacobian
	 */
	public boolean getUseSymbolicJacobian() {
		return fieldUseSymbolicJacobian;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/00 3:45:12 PM)
	 * @return java.lang.String
	 */
	public String getVCML() {

		//
		// write format as follows:
		//
		//   SolverTaskDescription {
		//		TaskType Unsteady
		//		MaxTime 1
		//		SolverDescription "Runga-Kutta Fehlberg"
		//		UseSymbolicJacobian false
		//		TimeBounds {
		//			StartingTime	0.0
		//			EndingTime		0.0
		//		}
		//		TimeStep {
		//			DefaultTimeStep		0.0
		//			MinimumTimeStep		0.0
		//			MaximumTimeStep		0.0
		//		}
		//		ErrorTolerance {
		//			AbsoluteErrorTolerance 1e-8
		//			RelativeErrorTolerance 1e-4
		//		}
		//   	StochSimOptions {
		//			UseCustomSeed	false
		//			CustomSeed	0
		//			NumOfTrials	1
		//          //if Hybrid, we have following four more
		//          Epsilon 100
		//          Lambda 10
		//          MSRTolerance 0.01
		//          SDETolerance 1e-4
		//   	}
		//		StopAtSpatiallyUniform {
		//			AbsoluteErrorTolerance	1e-8
		//			RelativeErrorTolerance 1e-4
		//		}
		//      RunParameterScanSerially false
		//		KeepEvery 1
		//		KeepAtMost	1000
		//		SensitivityParameter {
		//			Constant k1 39.0;
		//		}
		//		NumProcessors 1
		//   }
		//
		//
		StringBuffer buffer = new StringBuffer();

		buffer.append(VCML.SolverTaskDescription+" "+VCML.BeginBlock+"\n");

		if (getTaskType() == TASK_UNSTEADY){
			buffer.append(VCML.TaskType+" "+VCML.TaskType_Unsteady+"\n");
		}else if (getTaskType() == TASK_STEADY){
			buffer.append(VCML.TaskType+" "+VCML.TaskType_Steady+"\n");
		}else{
			throw new RuntimeException("unexpected task type");
		}

		buffer.append(VCML.SolverDescription+" \""+getSolverDescription().getDatabaseName()+"\""+"\n");

		buffer.append(VCML.UseSymbolicJacobian+" "+getUseSymbolicJacobian()+"\n");

		buffer.append(getTimeBounds().getVCML()+"\n");

		buffer.append(getTimeStep().getVCML()+"\n");

		buffer.append(getErrorTolerance().getVCML()+"\n");

		if(getStochOpt() != null)
			buffer.append(getStochOpt().getVCML()+"\n");

		buffer.append(fieldOutputTimeSpec.getVCML() + "\n");

		if (getSensitivityParameter()!=null){
			buffer.append(VCML.SensitivityParameter+" "+VCML.BeginBlock+"\n");
			buffer.append(getSensitivityParameter().getVCML()+"\n");
			buffer.append(VCML.EndBlock+"\n");
		}

		if (stopAtSpatiallyUniformErrorTolerance != null) {
			buffer.append(VCML.StopAtSpatiallyUniform + " " + stopAtSpatiallyUniformErrorTolerance.getVCML() + "\n");
		}

		if (bSerialParameterScan) {
			buffer.append(VCML.RunParameterScanSerially + " " + bSerialParameterScan + "\n");
		}
		if (nfsimSimulationOptions != null) {
			buffer.append(nfsimSimulationOptions.getVCML());
		}
		if (smoldynSimulationOptions != null) {
			buffer.append(smoldynSimulationOptions.getVCML());
		}
		if (sundialsPdeSolverOptions != null) {
			buffer.append(sundialsPdeSolverOptions.getVCML());
		}

		if (chomboSolverSpec != null) {
			buffer.append(chomboSolverSpec.getVCML()+"\n");
		}
		buffer.append("\t" + VCML.NUM_PROCESSORS + " " + numProcessors + "\n");

		buffer.append(VCML.EndBlock+"\n");

		return buffer.toString();
	}

	//if (token.equalsIgnoreCase(VCML.NFSimSimulationOptions)) {
	//	getNFSimSimulationOptions().readVCML(tokens);
	//	continue;
	//}


	/**
	 * Accessor for the vetoPropertyChange field.
	 */
	protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
		if (vetoPropertyChange == null) {
			vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
		};
		return vetoPropertyChange;
	}


	/**
	 * The hasListeners method was generated to support the propertyChange field.
	 */
	public synchronized boolean hasListeners(java.lang.String propertyName) {
		return getPropertyChange().hasListeners(propertyName);
	}

	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source
	 *   and the property that has changed.
	 */
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		try {
			if (evt.getSource() == this && (evt.getPropertyName().equals(PROPERTY_SOLVER_DESCRIPTION))) {
				SolverDescription solverDescription = getSolverDescription();
				if (solverDescription.equals(SolverDescription.SundialsPDE) || solverDescription.isSemiImplicitPdeSolver() || solverDescription.isGibsonSolver()) {
					TimeBounds timeBounds = getTimeBounds();
					if (!(getOutputTimeSpec() instanceof UniformOutputTimeSpec)) {
						// set to uniform output if it is not.
						double outputTime = (timeBounds.getEndingTime()-timeBounds.getStartingTime())/20;
						setOutputTimeSpec(new UniformOutputTimeSpec(outputTime));
					}
					if (solverDescription.equals(SolverDescription.SundialsPDE)) {
						setErrorTolerance(ErrorTolerance.getDefaultSundialsErrorTolerance());
						setDefaultTimeStep(TimeStep.getDefaultSundialsTimeStep());
						if (sundialsPdeSolverOptions == null) {
							sundialsPdeSolverOptions = new SundialsPdeSolverOptions();
						}
					} else {
						sundialsPdeSolverOptions = null;
						setErrorTolerance(ErrorTolerance.getDefaultSemiImplicitErrorTolerance());
					}
				} else if (!solverDescription.supports(getOutputTimeSpec())){
					setOutputTimeSpec(solverDescription.createOutputTimeSpec(this));
				}
				if (solverDescription.isNonSpatialStochasticSolver()) {
					if (fieldNonspatialStochOpt == null) {
						setStochOpt(new NonspatialStochSimOptions());
					} else {
						setStochOpt(new NonspatialStochSimOptions(fieldNonspatialStochOpt));
					}
					if (!solverDescription.equals(SolverDescription.StochGibson)) {
						if (fieldNonspatialStochHybridOpt == null) {
							setStochHybridOpt(new NonspatialStochHybridOptions());
						}
					}
				} else if (solverDescription.isSpatialStochasticSolver()) {
					setTimeStep(TimeStep.getDefaultSmoldynTimeStep());
					if (smoldynSimulationOptions == null) {
						smoldynSimulationOptions = new SmoldynSimulationOptions();
					}
					//				setSmoldynDefaultTimeStep();
				}
				if (solverDescription.isNFSimSolver()){
					if (nfsimSimulationOptions == null){
						nfsimSimulationOptions = new NFsimSimulationOptions();
					}
				}
				if (solverDescription.isChomboSolver()) {
					if (chomboSolverSpec == null && getSimulation() != null) {
						chomboSolverSpec = new ChomboSolverSpec(ChomboSolverSpec.getDefaultMaxBoxSize(getSimulation().getMathDescription().getGeometry().getDimension()));
					}
					if (getOutputTimeSpec() == null || !(getOutputTimeSpec() instanceof UniformOutputTimeSpec)) {
						double outputTime = getTimeBounds().getEndingTime()/10;
						setOutputTimeSpec(new UniformOutputTimeSpec(outputTime));
					}
					if (chomboSolverSpec.getTimeIntervalList().size() == 0)
					{
						chomboSolverSpec.addTimeInterval(TimeInterval.getDefaultTimeInterval());
					}
				} else {
					chomboSolverSpec = null;
				}
			}
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}


	//private void setSmoldynDefaultTimeStep() throws PropertyVetoException {
	//	if (fieldSimulation != null) {
	//		SmoldynTimeStepVars smoldynTimeStepVars = RunSims.computeMaxSmoldynTimeStep(fieldSimulation);
	//		double maxDt = smoldynTimeStepVars.getMaxDt();
	//		setTimeStep(new TimeStep(maxDt, maxDt, maxDt));
	//	}
	//}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/00 3:45:12 PM)
	 * @return java.lang.String
	 */
	public void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		//
		// read format as follows: (OUT OF DATE)
		//
		//   SolverTaskDescription {
		//		TaskType Unsteady
		//		MaxTime 1
		//		SolverDescription "Runga-Kutta Fehlberg"
		//		UseSymbolicJacobian false
		//		TimeBounds {
		//			StartingTime	0.0
		//			EndingTime		0.0
		//		}
		//		TimeStep {
		//			DefaultTimeStep		0.0
		//			MinimumTimeStep		0.0
		//			MaximumTimeStep		0.0
		//		}
		//		ErrorTolerance {
		//			AbsoluteErrorTolerance 1e-8
		//			RelativeErrorTolerance 1e-4
		//		}

		
		//		StochSimOptions {
		//			UseCustomSeed	false
		//			CustomSeed	0
		//			NumOfTrials	1
		//          if Hybrid, we have following four more
		//          Epsilon 100
		//          Lambda 10
		//          MSRTolerance 0.01
		//          SDETolerance 1e-4
		//   	}
		//
		//  or
		//
		//		StochSimOptions {
		//			UseCustomSeed	false
		//			CustomSeed	0
		//			NumOfTrials	1
		//   	}
		//		StochHybridOptions {
		//          Epsilon 100
		//          Lambda 10
		//          MSRTolerance 0.01
		//          SDETolerance 1e-4
		//   	}

		//		StopAtSpatiallyUniform {
		//			AbsoluteErrorTolerance	1e-8
		//			RelativeErrorTolerance 1e-4
		//		}
		//		KeepEvery 1
		//		KeepAtMost	1000
		//      OR
		//      OutputOptions {
		//			KeepEvery 1
		//			KeepAtMost 1000
		//		}
		//      OR
		//      OutputOptions {
		//			OutputTimeStep 0.5
		//		}
		//		OR
		//  	OutputOptions {
		//			OutputTimes 0.1,0.3,0.4,... (comma separated list, no spaces or linefeeds between numbers in list)
		//   	}
		//
		//		SensitivityParameter {
		//			Constant k1 39.0;
		//		}
		//      RunParameterScanSerially false
		//   }
		//
		//
		int keepEvery = -1;
		int keepAtMost = -1;
		SolverDescription sd = null;
		try {
			String token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.SolverTaskDescription)) {
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
					throw new DataAccessException(
							"unexpected token " + token + " expecting " + VCML.BeginBlock);
				}
			}

			while (tokens.hasMoreTokens()) {
				token = tokens.nextToken();
				lg.debug(token);
				if (token.equalsIgnoreCase(VCML.EndBlock)) {
					lg.debug("end block");
					break;
				}
				if (token.equalsIgnoreCase(VCML.TaskType)) {
					token = tokens.nextToken();
					if (token.equals(VCML.TaskType_Unsteady)){
						setTaskType(TASK_UNSTEADY);
					}else if (token.equals(VCML.TaskType_Steady)){
						setTaskType(TASK_STEADY);
					}else{
						throw new DataAccessException("unexpected "+VCML.TaskType+" = "+token);
					}
					continue;
				}
				if (token.equalsIgnoreCase(VCML.SolverDescription)) {
					token = tokens.nextToken();
					//
					// catch property veto exception to correct inappropriate solver selection in database
					//
					try {
						sd = SolverDescription.fromDatabaseName(token);
						setSolverDescription(sd);
					}catch (java.beans.PropertyVetoException e){
						e.printStackTrace(System.out);
					}
					continue;
				}
				if (token.equalsIgnoreCase(VCML.UseSymbolicJacobian)) {
					token = tokens.nextToken();
					setUseSymbolicJacobian((new Boolean(token)).booleanValue());
					continue;
				}
				//  JMW 01/25/2001 We have removed the maxTime property
				//  from SolverTaskDescription, this little piece of
				//  code allows us to read the old format.
				if (token.equalsIgnoreCase(VCML.MaxTime)) {
					token = tokens.nextToken();
					//				double dummyMaxTime = Double.parseDouble(token);
					continue;
				}
				if (token.equalsIgnoreCase(VCML.TimeBounds)) {
					getTimeBounds().readVCML(tokens);
					continue;
				}
				if (token.equalsIgnoreCase(VCML.TimeStep)) {
					getTimeStep().readVCML(tokens);
					continue;
				}
				if (token.equalsIgnoreCase(VCML.ErrorTolerance)) {
					getErrorTolerance().readVCML(tokens);
					continue;
				}
				if (token.equalsIgnoreCase(VCML.NFSimSimulationOptions)) {
					getNFSimSimulationOptions().readVCML(tokens);
					continue;
				}
				if (token.equalsIgnoreCase(VCML.StochSimOptions)) {
					boolean useCustomSeed = false;
					int customSeed = -1;
					long numOfTrials = 1;
					Double epsilon = null;
					Double lambda = null;
					Double MSRTolerance = null;
					Double SDETolerance = null;
					
					token = tokens.nextToken();
					if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
						throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock); 
					}
					while (tokens.hasMoreTokens()) {
						token = tokens.nextToken();
						if (token.equalsIgnoreCase(VCML.EndBlock)) {
							break;
						}
						if (token.equalsIgnoreCase(VCML.UseCustomSeed)) {
							token = tokens.nextToken();
							useCustomSeed = Boolean.parseBoolean(token);
							continue;
						}
						if (token.equalsIgnoreCase(VCML.CustomSeed)) {
							token = tokens.nextToken();
							int val1 = Integer.parseInt(token);
							if(val1 < 0) {
								throw new DataAccessException("unexpected token " + token + ", seed is required to be an unsigned interger. ");
							} else {
								customSeed = val1;
							}
							continue;
						}
						if (token.equalsIgnoreCase(VCML.NumOfTrials)) {
							token = tokens.nextToken();
							int val2 = Integer.parseInt(token);
							if(val2 < 1 ) {
								throw new DataAccessException("unexpected token " + token + ", num of trials is requied to be at least 1. ");
							} else {
								numOfTrials = val2;
							}
							continue;
						}
						
						//
						// for backward compatibility, try to read NonspatialStochHybridOptions also
						//
						if (token.equalsIgnoreCase(VCML.Epsilon)) {
							token = tokens.nextToken();
							double val3 = Double.parseDouble(token);
							if(val3 < 1 )
								throw new DataAccessException("unexpected token " + token + ", Minimum number of molecue is requied to be greater than or equal to 1. ");
							else
								epsilon = val3;
							continue;
						}
						if (token.equalsIgnoreCase(VCML.Lambda)) {
							token = tokens.nextToken();
							double val4 = Double.parseDouble(token);
							if(val4 <= 0 )
								throw new DataAccessException("unexpected token " + token + ", num of trials is requied to be greater than 0. ");
							else
								lambda = val4;
							continue;
						}
						if (token.equalsIgnoreCase(VCML.MSRTolerance)) {
							token = tokens.nextToken();
							double val5 = Double.parseDouble(token);
							if(val5 <= 0 )
								throw new DataAccessException("unexpected token " + token + ", Maximum allowed effect of slow reactions is requied to be greater than 0. ");
							else
								MSRTolerance = val5;
							continue;
						}
						if (token.equalsIgnoreCase(VCML.SDETolerance)) {
							token = tokens.nextToken();
							double val6 = Double.parseDouble(token);
							if(val6 <= 0 )
								throw new DataAccessException("unexpected token " + token + ", SDE allowed value of drift and diffusion errors is requied to be greater than 0. ");
							else
								SDETolerance = val6;
							continue;
						}
						throw new DataAccessException("unexpected identifier " + token);
					}
					if (getSimulation()!=null && getSimulation().getMathDescription()!=null)
					{
						if(getSimulation().getMathDescription().isNonSpatialStoch()){
							setStochOpt(new NonspatialStochSimOptions(useCustomSeed, customSeed, numOfTrials));
							if (epsilon!=null && lambda!=null && MSRTolerance!=null && SDETolerance!=null){
								setStochHybridOpt(new NonspatialStochHybridOptions(epsilon, lambda, MSRTolerance, SDETolerance));
							}
						}else{
							setStochOpt(null);
						}
					}
					continue;
				}
				if (token.equalsIgnoreCase(VCML.StochHybridOptions)) {
					Double epsilon = null;
					Double lambda = null;
					Double MSRTolerance = null;
					Double SDETolerance = null;
					token = tokens.nextToken();
					if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
						throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock); 
					}
					while (tokens.hasMoreTokens()) {
						token = tokens.nextToken();
						if (token.equalsIgnoreCase(VCML.EndBlock)) {
							break;
						}
						if (token.equalsIgnoreCase(VCML.Epsilon)) {
							token = tokens.nextToken();
							double val3 = Double.parseDouble(token);
							if(val3 < 1 )
								throw new DataAccessException("unexpected token " + token + ", Minimum number of molecue is requied to be greater than or equal to 1. ");
							else
								epsilon = val3;
							continue;
						}
						if (token.equalsIgnoreCase(VCML.Lambda)) {
							token = tokens.nextToken();
							double val4 = Double.parseDouble(token);
							if(val4 <= 0 )
								throw new DataAccessException("unexpected token " + token + ", num of trials is requied to be greater than 0. ");
							else
								lambda = val4;
							continue;
						}
						if (token.equalsIgnoreCase(VCML.MSRTolerance)) {
							token = tokens.nextToken();
							double val5 = Double.parseDouble(token);
							if(val5 <= 0 )
								throw new DataAccessException("unexpected token " + token + ", Maximum allowed effect of slow reactions is requied to be greater than 0. ");
							else
								MSRTolerance = val5;
							continue;
						}
						if (token.equalsIgnoreCase(VCML.SDETolerance)) {
							token = tokens.nextToken();
							double val6 = Double.parseDouble(token);
							if(val6 <= 0 )
								throw new DataAccessException("unexpected token " + token + ", SDE allowed value of drift and diffusion errors is requied to be greater than 0. ");
							else
								SDETolerance = val6;
							continue;
						}
						throw new DataAccessException("unexpected identifier " + token);
					}
					if (getSimulation()!=null && getSimulation().getMathDescription()!=null)
					{
						if(getSimulation().getMathDescription().isNonSpatialStoch()){
							if (epsilon!=null && lambda!=null && MSRTolerance!=null && SDETolerance!=null){
								setStochHybridOpt(new NonspatialStochHybridOptions(epsilon, lambda, MSRTolerance, SDETolerance));
							}
						}
					}
					continue;
				}
				if (token.equalsIgnoreCase(VCML.OutputOptions)) {
					fieldOutputTimeSpec = OutputTimeSpec.readVCML(tokens);
					continue;
				}

				if (token.equalsIgnoreCase(VCML.KeepEvery)) {
					token = tokens.nextToken();
					keepEvery = Integer.parseInt(token);
					continue;
				}
				if (token.equalsIgnoreCase(VCML.KeepAtMost)) {
					token = tokens.nextToken();
					keepAtMost = Integer.parseInt(token);
					continue;
				}
				if (token.equalsIgnoreCase(VCML.SensitivityParameter)) {
					token = tokens.nextToken();
					if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
						throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock);
					}
					token = tokens.nextToken();
					if (!token.equalsIgnoreCase(VCML.Constant)) {
						throw new DataAccessException("unexpected token " + token + " expecting " + VCML.Constant);
					}
					String name = tokens.nextToken();
					Expression exp = MathFunctionDefinitions.fixFunctionSyntax(tokens);
					Constant constant = new Constant(name,exp);
					setSensitivityParameter(constant);
					token = tokens.nextToken();
					if (!token.equalsIgnoreCase(VCML.EndBlock)) {
						throw new DataAccessException("unexpected token " + token + " expecting " + VCML.EndBlock);
					}
					continue;
				}
				if (token.equalsIgnoreCase(VCML.StopAtSpatiallyUniform)) {
					token = tokens.nextToken();
					stopAtSpatiallyUniformErrorTolerance = ErrorTolerance.getDefaultSpatiallyUniformErrorTolerance();
					stopAtSpatiallyUniformErrorTolerance.readVCML(tokens);
				} else if (token.equalsIgnoreCase(VCML.RunParameterScanSerially)) {
					token = tokens.nextToken();
					setSerialParameterScan((new Boolean(token)).booleanValue());
				} else if (token.equalsIgnoreCase(VCML.SmoldynSimulationOptions)) {
					setSmoldynSimulationOptions(new SmoldynSimulationOptions(tokens));
				} else if (token.equalsIgnoreCase(VCML.SundialsSolverOptions)) {
					setSundialsPdeSolverOptions(new SundialsPdeSolverOptions(tokens));
				} else	if (token.equalsIgnoreCase(VCML.ChomboSolverSpec)) {
					chomboSolverSpec = new ChomboSolverSpec(tokens);
				}
				else if (token.equalsIgnoreCase(VCML.NUM_PROCESSORS))
				{
					token = tokens.nextToken();
					numProcessors = Integer.parseInt(token);
				} else {
					throw new DataAccessException("unexpected identifier " + token);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			throw new DataAccessException("line #" + (tokens.lineIndex()+1) + " Exception: " + e.getMessage());
		}

		if (keepEvery != -1) {
			fieldOutputTimeSpec = new DefaultOutputTimeSpec(keepEvery, keepAtMost);
		}
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (9/8/2005 11:15:57 AM)
	 */
	public void refreshDependencies() {
		removePropertyChangeListener(this);
		addPropertyChangeListener(this);
		if(smoldynSimulationOptions != null) {
			smoldynSimulationOptions.refreshDependencies();
		}
		if(nfsimSimulationOptions != null) {
			nfsimSimulationOptions.refreshDependencies();
		}
	}

	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	/**
	 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
	 */
	public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
		getVetoPropertyChange().removeVetoableChangeListener(listener);
	}


	/**
	 * Sets the errorTolerance property (cbit.vcell.solver.ErrorTolerance) value.
	 * @param errorTolerance The new value for the property.
	 * @exception java.beans.PropertyVetoException The exception description.
	 * @see #getErrorTolerance
	 */
	public void setErrorTolerance(ErrorTolerance errorTolerance) throws java.beans.PropertyVetoException {
		if (!Matchable.areEqual(fieldErrorTolerance,errorTolerance) ) {
			ErrorTolerance oldValue = fieldErrorTolerance;
			fireVetoableChange(PROPERTY_ERROR_TOLERANCE, oldValue, errorTolerance);
			fieldErrorTolerance = errorTolerance;
			firePropertyChange(PROPERTY_ERROR_TOLERANCE, oldValue, errorTolerance);
		}
	}


	/**
	 * Sets the outputTimeSpec property (cbit.vcell.solver.OutputTimeSpec) value.
	 * @param outputTimeSpec The new value for the property.
	 * @exception java.beans.PropertyVetoException The exception description.
	 * @see #getOutputTimeSpec
	 */
	public void setOutputTimeSpec(OutputTimeSpec outputTimeSpec) throws java.beans.PropertyVetoException {
		if (!Matchable.areEqual(fieldOutputTimeSpec,outputTimeSpec)) {
			OutputTimeSpec oldValue = fieldOutputTimeSpec;
			fireVetoableChange(PROPERTY_OUTPUT_TIME_SPEC, oldValue, outputTimeSpec);
			fieldOutputTimeSpec = outputTimeSpec;
			firePropertyChange(PROPERTY_OUTPUT_TIME_SPEC, oldValue, outputTimeSpec);
		}
	}


	/**
	 * Sets the sensitivityParameter property (cbit.vcell.math.Constant) value.
	 * @param sensitivityParameter The new value for the property.
	 * @see #getSensitivityParameter
	 */
	public void setSensitivityParameter(Constant sensitivityParameter) throws java.beans.PropertyVetoException {
		if (!Matchable.areEqual(fieldSensitivityParameter,sensitivityParameter)) {
			Constant oldValue = fieldSensitivityParameter;
			fireVetoableChange("sensitivityParameter", oldValue, sensitivityParameter);
			fieldSensitivityParameter = sensitivityParameter;
			firePropertyChange("sensitivityParameter", oldValue, sensitivityParameter);
		}
	}


	/**
	 * Gets the endingTime property (double) value.
	 * @return The endingTime property value.
	 * @throws PropertyVetoException
	 * @see #setEndingTime
	 */
	private void setSimulation(Simulation simulation) {
		if (getSimulation() != null) {
			getSimulation().removePropertyChangeListener(this);
		}
		fieldSimulation = simulation;
		if (getSimulation() != null) {
			getSimulation().addPropertyChangeListener(this);
		}
		//	try {
		//		setSmoldynDefaultTimeStep();
		//	} catch (Exception ex) {
		//		ex.printStackTrace();
		//	}
	}


	/**
	 * Sets the solverDescription property (cbit.vcell.solver.SolverDescription) value.
	 * @param solverDescription The new value for the property.
	 * @exception java.beans.PropertyVetoException The exception description.
	 * @see #getSolverDescription
	 */
	public void setSolverDescription(SolverDescription solverDescription) throws java.beans.PropertyVetoException {
		if (fieldSolverDescription != solverDescription) {
			SolverDescription oldValue = fieldSolverDescription;
			fireVetoableChange(PROPERTY_SOLVER_DESCRIPTION, oldValue, solverDescription);
			fieldSolverDescription = solverDescription;
			if (numProcessors > 1 &&  !fieldSolverDescription.supports(SolverFeature.Feature_Parallel)) {
				numProcessors = 1;
			}
			if (solverDescription.isNonSpatialStochasticSolver() && !solverDescription.isGibsonSolver()){
				if (fieldNonspatialStochHybridOpt == null){
					fieldNonspatialStochHybridOpt = new NonspatialStochHybridOptions();
				}
			}
			firePropertyChange(PROPERTY_SOLVER_DESCRIPTION, oldValue, solverDescription);
		}
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (12/6/2006 6:16:42 PM)
	 * @param newFieldStochOpt cbit.vcell.solver.StochSimOptions
	 */
	public void setStochOpt(NonspatialStochSimOptions newStochOpt) {
		if (lg.isTraceEnabled()) {
			lg.trace("setStochOption " + Objects.hashCode(newStochOpt) +  ' ' + Objects.toString(newStochOpt));
		}
		
		if (!Matchable.areEqual(fieldNonspatialStochOpt,newStochOpt)) {
			NonspatialStochSimOptions oldValue = fieldNonspatialStochOpt;
			fieldNonspatialStochOpt = newStochOpt;
			firePropertyChange(PROPERTY_STOCH_SIM_OPTIONS, oldValue, newStochOpt);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/6/2006 6:16:42 PM)
	 * @param newFieldStochOpt cbit.vcell.solver.StochSimOptions
	 */
	public void setStochHybridOpt(NonspatialStochHybridOptions newStochHybridOpt) {
		if (lg.isTraceEnabled()) {
			lg.trace("setStochOption " + Objects.hashCode(newStochHybridOpt) +  ' ' + Objects.toString(newStochHybridOpt));
		}
		
		if (!Matchable.areEqual(fieldNonspatialStochHybridOpt,newStochHybridOpt)) {
			NonspatialStochHybridOptions oldValue = fieldNonspatialStochHybridOpt;
			fieldNonspatialStochHybridOpt = newStochHybridOpt;
			firePropertyChange(PROPERTY_STOCH_HYBRID_OPTIONS, oldValue, newStochHybridOpt);
		}
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return double
	 */
	public void setTaskType(int taskType) throws java.beans.PropertyVetoException {
		if (fieldTaskType != taskType) {
			int oldValue = fieldTaskType;
			fireVetoableChange("taskType", new Integer(oldValue), new Integer(taskType));
			fieldTaskType = taskType;
			firePropertyChange("taskType", new Integer(oldValue), new Integer(taskType));
		}
	}


	/**
	 * Sets the timeBounds property (cbit.vcell.solver.TimeBounds) value.
	 * @param timeBounds The new value for the property.
	 * @exception java.beans.PropertyVetoException The exception description.
	 * @see #getTimeBounds
	 */
	public void setTimeBounds(TimeBounds timeBounds) throws java.beans.PropertyVetoException {
		if (!Matchable.areEqual(fieldTimeBounds,timeBounds) ) {
			//  Only here to ensure it is being used correctly.
			// cbit.util.Assertion.assertNotNull(timeBounds);
			TimeBounds oldValue = fieldTimeBounds;
			fireVetoableChange(PROPERTY_TIME_BOUNDS, oldValue, timeBounds);
			fieldTimeBounds = timeBounds;
			firePropertyChange(PROPERTY_TIME_BOUNDS, oldValue, timeBounds);
		}
	}


	/**
	 * Sets the timeStep property (cbit.vcell.solver.TimeStep) value.
	 * @param timeStep The new value for the property.
	 * @exception java.beans.PropertyVetoException The exception description.
	 * @see #getTimeStep
	 */
	public void setTimeStep(TimeStep timeStep) throws java.beans.PropertyVetoException {
		//  Only here to ensure it is being used correctly.
		// cbit.util.Assertion.assertNotNull(timeStep);
		if (!Matchable.areEqual(timeStep, fieldTimeStep)) {
			TimeStep oldValue = fieldTimeStep;
			fireVetoableChange(PROPERTY_TIME_STEP, oldValue, timeStep);
			fieldTimeStep = timeStep;
			firePropertyChange(PROPERTY_TIME_STEP, oldValue, timeStep);
		}
	}

	/**
	 * set a default time, adjusting if necessary because the current {@link #getOutputTimeSpec()} is smaller
	 * @param timeStep
	 * @throws java.beans.PropertyVetoException
	 */
	public void setDefaultTimeStep(TimeStep timeStep) throws java.beans.PropertyVetoException {
		TimeStep adjusted = timeStep;
		UniformOutputTimeSpec uots = BeanUtils.downcast(UniformOutputTimeSpec.class, fieldOutputTimeSpec);
		if (uots != null && timeStep.getDefaultTimeStep() > uots.getOutputTimeStep()) {
			adjusted = timeStep.withDefault(uots.getOutputTimeStep());
		}
		setTimeStep(adjusted);
	}

	/**
	 * Sets the useSymbolicJacobian property (boolean) value.
	 * @param useSymbolicJacobian The new value for the property.
	 * @see #getUseSymbolicJacobian
	 */
	public void setUseSymbolicJacobian(boolean useSymbolicJacobian) {
		if (useSymbolicJacobian != fieldUseSymbolicJacobian) {
			boolean oldValue = fieldUseSymbolicJacobian;
			fieldUseSymbolicJacobian = useSymbolicJacobian;
			firePropertyChange(PROPERTY_USE_SYMBOLIC_JACOBIAN, new Boolean(oldValue), new Boolean(useSymbolicJacobian));
		}
	}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source
	 *   and the property that has changed.
	 */
	public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	}

	public final ErrorTolerance getStopAtSpatiallyUniformErrorTolerance() {
		return stopAtSpatiallyUniformErrorTolerance;
	}

	/**
	 * Sets the errorTolerance property (cbit.vcell.solver.ErrorTolerance) value.
	 * @param errorTolerance The new value for the property.
	 * @exception java.beans.PropertyVetoException The exception description.
	 * @see #getErrorTolerance
	 */
	public void setStopAtSpatiallyUniformErrorTolerance(ErrorTolerance errorTolerance) {
		if (!Matchable.areEqual(stopAtSpatiallyUniformErrorTolerance, errorTolerance)) {
			ErrorTolerance oldValue = stopAtSpatiallyUniformErrorTolerance;
			stopAtSpatiallyUniformErrorTolerance = errorTolerance;
			firePropertyChange(PROPERTY_STOP_AT_SPATIALLY_UNIFORM_ERROR_TOLERANCE, oldValue, errorTolerance);
		}
	}

	public final boolean isSerialParameterScan() {
		return bSerialParameterScan;
	}


	public final void setSerialParameterScan(boolean arg_bSerialParameterScan) {
		this.bSerialParameterScan = arg_bSerialParameterScan;
	}


	public final SmoldynSimulationOptions getSmoldynSimulationOptions() {
		return smoldynSimulationOptions;
	}

	public final NFsimSimulationOptions getNFSimSimulationOptions() {
		return nfsimSimulationOptions;
	}

	public final SundialsPdeSolverOptions getSundialsPdeSolverOptions() {
		return sundialsPdeSolverOptions;
	}

	public final void setSmoldynSimulationOptions(SmoldynSimulationOptions smoldynSimulationOptions) {
		if (!Matchable.areEqual(smoldynSimulationOptions,this.smoldynSimulationOptions)) {
			SmoldynSimulationOptions oldValue = this.smoldynSimulationOptions;
			this.smoldynSimulationOptions = smoldynSimulationOptions;
			firePropertyChange(PROPERTY_SMOLDYN_SIMULATION_OPTIONS, oldValue, smoldynSimulationOptions);
		}
	}

	public final void setNFSimSimulationOptions(NFsimSimulationOptions nfsimSimulationOptions) {
		if (!Matchable.areEqual(this.nfsimSimulationOptions,nfsimSimulationOptions)) {
			NFsimSimulationOptions oldValue = this.nfsimSimulationOptions;
			this.nfsimSimulationOptions = nfsimSimulationOptions;
			firePropertyChange(PROPERTY_NFSIM_SIMULATION_OPTIONS, oldValue, nfsimSimulationOptions);
		}
	}

	public final void setSundialsPdeSolverOptions(SundialsPdeSolverOptions sundialsPdeSolverOptions) {
		if (!Matchable.areEqual(this.sundialsPdeSolverOptions,sundialsPdeSolverOptions) ) {
			SundialsPdeSolverOptions oldValue = this.sundialsPdeSolverOptions;
			this.sundialsPdeSolverOptions = sundialsPdeSolverOptions;
			firePropertyChange(PROPERTY_SUNDIALS_SOLVER_OPTIONS, oldValue, sundialsPdeSolverOptions);
		}
	}

	public final ChomboSolverSpec getChomboSolverSpec()
	{
		return chomboSolverSpec;
	}

	public final void setChomboSolverSpec(ChomboSolverSpec chomboSolverSpec)
	{
		this.chomboSolverSpec = chomboSolverSpec;
	}

	public int getNumProcessors() {
		return numProcessors;
	}

	/**
	 * return true if set for parallel processing ({@link #numProcessors} > 1)
	 * (note selected solver may not support parallel processing)
	 * @return true if is
	 */
	public boolean isParallel( ) {
		return numProcessors > 1;
	}

	/**
	 * set num of processors; < 1 treated as 1
	 * @param numProcessors
	 * @throws IllegalArgumentException if > 1 and solver does not support parallel
	 */
	public void setNumProcessors(int numProcessors) {
		numProcessors = Math.max(numProcessors, 1); //must be >= 1
		if (numProcessors == 1 || fieldSolverDescription.supports(SolverFeature.Feature_Parallel) ) {
			this.numProcessors = numProcessors;
		}
		else { //problem here
			this.numProcessors = 1;
			throw new IllegalArgumentException(fieldSolverDescription.getDisplayLabel() + " only supports 1 processor, "
					+ numProcessors + " passed");
		}
	}

	/**
	 * does this simulation require use of Vtk?
	 * @return true if does (currently chombo solver)
	 */
	public boolean isVtkUser( ) {
		return chomboSolverSpec != null;
	}

	public NonspatialStochHybridOptions getStochHybridOpt() {
		return fieldNonspatialStochHybridOpt;
	}

	//double calculateBindingRadius(ParticleJumpProcess pjp, SubDomain subDomain) throws Exception
	//{
	//	double minRadius = 1e8;
	//	MathDescription mathDesc = fieldSimulation.getMathDescription();
	//	for (int jobIndex = 0; jobIndex < fieldSimulation.getScanCount(); jobIndex ++) {
	//		double kon = 0;
	//		SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(fieldSimulation, jobIndex);
	//		if(pjp.getParticleProbabilityRate() instanceof MacroscopicRateConstant) {
	//			Expression newExp = new Expression(((MacroscopicRateConstant) pjp.getParticleProbabilityRate()).getExpression());
	//			newExp.bindExpression(simSymbolTable);
	//			newExp = simSymbolTable.substituteFunctions(newExp).flatten();
	//			kon = newExp.evaluateConstant();
	//		}
	//		double sumD = 0;
	//		double maxConc = 0;
	//		//List<ParticleProperties> particleProperties = subDomain.getParticleProperties();
	//		//reactants
	//		ParticleVariable[] particleVars = pjp.getParticleVariables();
	//		//
	//		for(ParticleVariable pv : particleVars)
	//		{
	//			SubDomain particleSubDomain = mathDesc.getSubDomain(pv.getDomain().getName());
	//			ParticleProperties particleProperties = particleSubDomain.getParticleProperties(pv);
	//			Expression diffExp = new Expression(particleProperties.getDiffusion());
	//			diffExp.bindExpression(simSymbolTable);
	//			diffExp = simSymbolTable.substituteFunctions(diffExp).flatten();
	//			double diffConstant = diffExp.evaluateConstant();
	//
	//			for (ParticleInitialCondition pic : particleProperties.getParticleInitialConditions()){
	//				if (pic instanceof ParticleInitialCondition){
	//					Expression concExp = new Expression(particleProperties.get());
	//					diffExp.bindExpression(simSymbolTable);
	//					diffExp = simSymbolTable.substituteFunctions(diffExp).flatten();
	//					double diffConstant = diffExp.evaluateConstant();
	//					sumD += diffConstant;
	//					maxConc = Math.max(maxConc,conc);
	//				}else if (pic instanceof ParticleInitialConditionCount){
	//
	//				}
	//			}
	//		}
	//		if(subDomain instanceof CompartmentSubDomain)
	//		{
	//			double calculatedRadius = kon/(4*Math.PI*sumD);
	//			minRadius = Math.min(minRadius, calculatedRadius);
	//		}
	//		else if(subDomain instanceof MembraneSubDomain)
	//		{
	//			double b = 0;
	//			double calculatedRadius = Math.exp(2*Math.PI*sumD/kon - Math.log(b)) ;
	//			minRadius = Math.min(minRadius, calculatedRadius);
	//		}
	//
	//	}
	//	return 0;
	//}

}
