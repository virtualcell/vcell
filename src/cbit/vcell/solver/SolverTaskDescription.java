package cbit.vcell.solver;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.*;
import cbit.vcell.parser.*;
import cbit.vcell.solver.stoch.StochHybridOptions;
import cbit.vcell.solver.stoch.StochSimOptions;
import cbit.vcell.math.VCML;
import java.beans.PropertyVetoException;

import org.vcell.util.BeanUtils;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;

/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:15 PM)
 * @author: John Wagner
 */
public class SolverTaskDescription implements Matchable, java.beans.PropertyChangeListener, java.beans.VetoableChangeListener, java.io.Serializable {
	//  Or TASK_NONE for use as a default?
	public static final int TASK_UNSTEADY = 0;
	public static final int TASK_STEADY   = 1;
	//
	private int fieldTaskType = TASK_UNSTEADY;
	private cbit.vcell.math.Constant fieldSensitivityParameter = null;
	private Simulation fieldSimulation = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private TimeBounds fieldTimeBounds = new TimeBounds();
	private TimeStep fieldTimeStep = new TimeStep();
	private ErrorTolerance fieldErrorTolerance = new ErrorTolerance();
	private SolverDescription fieldSolverDescription = null;
	private boolean fieldUseSymbolicJacobian = false;
	private OutputTimeSpec fieldOutputTimeSpec = new DefaultOutputTimeSpec();
	private StochSimOptions fieldStochOpt = null; //added Dec 5th, 2006
	private boolean bStopAtSpatiallyUniform = false;

/**
 * One of three ways to construct a SolverTaskDescription.  This constructor
 * is used when creating a SolverTaskDescription from the database.
 */
public SolverTaskDescription(Simulation simulation, CommentStringTokenizer tokenizer) throws DataAccessException {
	super();
	addVetoableChangeListener(this);
	addPropertyChangeListener(this);
	try {
		if (simulation.isSpatial()) 
		{
			setSolverDescription(SolverDescription.getDefaultPDESolverDescription());
		} //amended Sept.27, 2006
		else if (simulation.getMathDescription().isStoch()) 
		{
			setSolverDescription(SolverDescription.getDefaultStochSolverDescription());
		}
		else
		{
			setSolverDescription(SolverDescription.getDefaultODESolverDescription());
		}
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
	}
	setSimulation(simulation);
	readVCML(tokenizer);
}


/**
 * This constructor is for management console only.
 */
public SolverTaskDescription(CommentStringTokenizer tokenizer) throws DataAccessException {
	super();
	readVCML(tokenizer);
}


/**
 * One of three ways to construct a SolverTaskDescription.  This constructor
 * is used when copying a SolverTaskDescription from an existing one.
 */
public SolverTaskDescription(Simulation simulation, SolverTaskDescription solverTaskDescription) {
	super();
	addVetoableChangeListener(this);
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
	bStopAtSpatiallyUniform = solverTaskDescription.bStopAtSpatiallyUniform;
	if (simulation.getMathDescription().isStoch() && (solverTaskDescription.getStochOpt() != null)) 
	{
		setStochOpt(solverTaskDescription.getStochOpt());
	}
	else setStochOpt(null);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}


/**
 * One of three ways to construct a SolverTaskDescription.  This constructor
 * is used when creating a new SolverTaskDescription.
 */
public SolverTaskDescription(Simulation simulation) {
	super();
	addVetoableChangeListener(this);
	addPropertyChangeListener(this);
	try {
		if (simulation.isSpatial()) 
		{
			setSolverDescription(SolverDescription.getDefaultPDESolverDescription());
		} //amended Sept.27, 2006
		else if (simulation.getMathDescription().isStoch()) 
		{
			fieldStochOpt = new StochSimOptions();
			setSolverDescription(SolverDescription.getDefaultStochSolverDescription());
			//setOutputTimeSpec(new DefaultOutputTimeSpec(1,1000000));//amended Feb 20th,2007
		}
		else
		{
			setSolverDescription(SolverDescription.getDefaultODESolverDescription());
		}
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
	}
	setSimulation(simulation);
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
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
		if (getTaskType() != solverTaskDescription.getTaskType()) return (false);
		//
		if (!getTimeBounds().compareEqual(solverTaskDescription.getTimeBounds())) return (false);
		if (!getTimeStep().compareEqual(solverTaskDescription.getTimeStep())) return (false);
		if (!getErrorTolerance().compareEqual(solverTaskDescription.getErrorTolerance())) return (false);
		if  (!Compare.isEqualOrNull(getStochOpt(),solverTaskDescription.getStochOpt())) return false;
		if (getUseSymbolicJacobian() != solverTaskDescription.getUseSymbolicJacobian()) return (false);
		//
		if (!getOutputTimeSpec().compareEqual(solverTaskDescription.getOutputTimeSpec())) return (false);
		if (!Compare.isEqualOrNull(getSensitivityParameter(),solverTaskDescription.getSensitivityParameter())) return (false);
		if (!Compare.isEqual(getSolverDescription(),solverTaskDescription.getSolverDescription())) return (false);
		if (getTaskType() != solverTaskDescription.getTaskType()) return (false);
		if (bStopAtSpatiallyUniform != solverTaskDescription.bStopAtSpatiallyUniform) {
			return false;
		}
		return true;
	}
	return (false);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(evt);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, int oldValue, int newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, boolean oldValue, boolean newValue) throws java.beans.PropertyVetoException {
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
			if (getSolverDescription().isODESolver()) {
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
public cbit.vcell.math.Constant getSensitivityParameter() {
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
public StochSimOptions getStochOpt() {
	return fieldStochOpt;
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
	//		StopAtSpatiallyUniform true
	//		KeepEvery 1
	//		KeepAtMost	1000
	//		SensitivityParameter {
	//			Constant k1 39.0;
	//		}
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

	if (bStopAtSpatiallyUniform) {
		buffer.append(VCML.StopAtSpatiallyUniform + " " + bStopAtSpatiallyUniform + "\n");
	}
	
	buffer.append(VCML.EndBlock+"\n");
		
	return buffer.toString();
}


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
 * Gets the solver property (java.lang.String) value.
 * @return The solver property value.
 * @see #setSolver
 */
private boolean isAllowableSolverDescription(SolverDescription argSolverDescription) {
	if (argSolverDescription == null){
		return false;
	}
	if (getSimulation()==null){
		return true;
	}

	if (getSimulation().isSpatial()==true && argSolverDescription.isPDESolver()){
		return true;
	}else if (getSimulation().isSpatial()==false && argSolverDescription.isODESolver()){
		return true;
	}else if (getSimulation().isSpatial()==false && argSolverDescription.isSTOCHSolver()){
		return true;
	}else{
		return false;
	}
}


/**
 * This method gets called when a bound property is changed.
 * @param evt A PropertyChangeEvent object describing the event source 
 *   and the property that has changed.
 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && (evt.getPropertyName().equals("solverDescription"))) {
		try {
			SolverDescription solverDescription = getSolverDescription();
			if (solverDescription.equals(SolverDescription.SundialsPDE) || solverDescription.isSemiImplicitPdeSolver()) {
				TimeBounds timeBounds = getTimeBounds();
				double outputTime = timeBounds.getEndingTime()/10;
				setOutputTimeSpec(new UniformOutputTimeSpec(outputTime));
				
				if (solverDescription.equals(SolverDescription.SundialsPDE)) {
					setErrorTolerance(ErrorTolerance.getDefaultSundialsErrorTolerance());
					setTimeStep(TimeStep.getDefaultSundialsTimeStep());
				} else {
					setErrorTolerance(ErrorTolerance.getDefaultSemiImplicitErrorTolerance());
				}
			} else if (!solverDescription.supports(getOutputTimeSpec())){
				setOutputTimeSpec(solverDescription.createOutputTimeSpec(this));
			}
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/00 3:45:12 PM)
 * @return java.lang.String
 */
public void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
	//
	// read format as follows:
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

	//		StopAtSpatiallyUniform true
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
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
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
				double dummyMaxTime = Double.parseDouble(token);
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
			if (token.equalsIgnoreCase(VCML.StochSimOptions)) {
				// Amended July 22nd, 2007 
				StochSimOptions temp = null;
				if(sd != null && sd.equals(SolverDescription.StochGibson))
					temp = new StochSimOptions();
				else temp = new StochHybridOptions();
				temp.readVCML(tokens);
				if (getSimulation()!=null && getSimulation().getMathDescription()!=null)
				{
					if(getSimulation().getMathDescription().isStoch()) setStochOpt(temp);
					else setStochOpt(null);					
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
				Expression exp = new Expression(tokens);
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
				bStopAtSpatiallyUniform = Boolean.valueOf(token).booleanValue();
				continue;
			}
			throw new DataAccessException("unexpected identifier " + token);
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
	removeVetoableChangeListener(this);
	removePropertyChangeListener(this);
	addPropertyChangeListener(this);
	addVetoableChangeListener(this);
}

/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}


/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}


/**
 * Sets the errorTolerance property (cbit.vcell.solver.ErrorTolerance) value.
 * @param errorTolerance The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getErrorTolerance
 */
public void setErrorTolerance(ErrorTolerance errorTolerance) throws java.beans.PropertyVetoException {
	ErrorTolerance oldValue = fieldErrorTolerance;
	fireVetoableChange("errorTolerance", oldValue, errorTolerance);
	fieldErrorTolerance = errorTolerance;
	firePropertyChange("errorTolerance", oldValue, errorTolerance);
}


/**
 * Sets the outputTimeSpec property (cbit.vcell.solver.OutputTimeSpec) value.
 * @param outputTimeSpec The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getOutputTimeSpec
 */
public void setOutputTimeSpec(OutputTimeSpec outputTimeSpec) throws java.beans.PropertyVetoException {
	OutputTimeSpec oldValue = fieldOutputTimeSpec;
	fireVetoableChange("outputTimeSpec", oldValue, outputTimeSpec);
	fieldOutputTimeSpec = outputTimeSpec;
	firePropertyChange("outputTimeSpec", oldValue, outputTimeSpec);
}


/**
 * Sets the sensitivityParameter property (cbit.vcell.math.Constant) value.
 * @param sensitivityParameter The new value for the property.
 * @see #getSensitivityParameter
 */
public void setSensitivityParameter(cbit.vcell.math.Constant sensitivityParameter) throws java.beans.PropertyVetoException {
	cbit.vcell.math.Constant oldValue = fieldSensitivityParameter;
	fireVetoableChange("sensitivityParameter", oldValue, sensitivityParameter);
	fieldSensitivityParameter = sensitivityParameter;
	firePropertyChange("sensitivityParameter", oldValue, sensitivityParameter);
}


/**
 * Gets the endingTime property (double) value.
 * @return The endingTime property value.
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

}


/**
 * Sets the solverDescription property (cbit.vcell.solver.SolverDescription) value.
 * @param solverDescription The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSolverDescription
 */
public void setSolverDescription(SolverDescription solverDescription) throws java.beans.PropertyVetoException {
	SolverDescription oldValue = fieldSolverDescription;
	fireVetoableChange("solverDescription", oldValue, solverDescription);
	fieldSolverDescription = solverDescription;
	firePropertyChange("solverDescription", oldValue, solverDescription);
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2006 6:16:42 PM)
 * @param newFieldStochOpt cbit.vcell.solver.StochSimOptions
 */
public void setStochOpt(StochSimOptions newStochOpt) {
	fieldStochOpt = newStochOpt;
}


/**
 * This method was created by a SmartGuide.
 * @return double
 */
public void setTaskType(int taskType) throws java.beans.PropertyVetoException {
	int oldValue = fieldTaskType;
	fireVetoableChange("taskType", new Integer(oldValue), new Integer(taskType));
	fieldTaskType = taskType;
	firePropertyChange("taskType", new Integer(oldValue), new Integer(taskType));
}


/**
 * Sets the timeBounds property (cbit.vcell.solver.TimeBounds) value.
 * @param timeBounds The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getTimeBounds
 */
public void setTimeBounds(TimeBounds timeBounds) throws java.beans.PropertyVetoException {
	//  Only here to ensure it is being used correctly.
	// cbit.util.Assertion.assertNotNull(timeBounds);
	TimeBounds oldValue = fieldTimeBounds;
	fireVetoableChange("timeBounds", oldValue, timeBounds);
	fieldTimeBounds = timeBounds;
	firePropertyChange("timeBounds", oldValue, timeBounds);
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
	TimeStep oldValue = fieldTimeStep;
	fireVetoableChange("timeStep", oldValue, timeStep);
	fieldTimeStep = timeStep;
	firePropertyChange("timeStep", oldValue, timeStep);
}


/**
 * Sets the useSymbolicJacobian property (boolean) value.
 * @param useSymbolicJacobian The new value for the property.
 * @see #getUseSymbolicJacobian
 */
public void setUseSymbolicJacobian(boolean useSymbolicJacobian) {
	boolean oldValue = fieldUseSymbolicJacobian;
	fieldUseSymbolicJacobian = useSymbolicJacobian;
	firePropertyChange("useSymbolicJacobian", new Boolean(oldValue), new Boolean(useSymbolicJacobian));
}


/**
 * This method gets called when a bound property is changed.
 * @param evt A PropertyChangeEvent object describing the event source 
 *   and the property that has changed.
 */
public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	if (evt.getSource() == this && evt.getPropertyName().equals("solverDescription")) {
		SolverDescription newSolverDescription = (SolverDescription)evt.getNewValue();
		if (!isAllowableSolverDescription(newSolverDescription)){
			throw new java.beans.PropertyVetoException("solverDescription '"+newSolverDescription.getDisplayLabel()+"' is not appropriate",evt);
		}
	}
}

public boolean isStopAtSpatiallyUniform() {
	return bStopAtSpatiallyUniform;
}

public void setStopAtSpatiallyUniform(boolean stopAtSpatiallyUniform) {
	bStopAtSpatiallyUniform = stopAtSpatiallyUniform;
}
}