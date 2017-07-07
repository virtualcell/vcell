package cbit.vcell.modelopt;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.vcell.util.BeanUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathException;
import cbit.vcell.opt.OptSolverResultSet;
import cbit.vcell.opt.OptSolverResultSet.OptRunResultSet;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationStatus;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
//call parameter est.
//save the values
//take the first parameter remove from list
//drag a parameter from current value in positive direction until error becomes 2x as large
//repeat in negative direction as 2x as large
//record parameter min and max values until this happens
//restore original parameter and go onto next parameter
//repeat for all parameters

public class ProfileLikelihood {
	//this creates the data set and parameter scan that is being used to update the parameters
	public static class ProfilelikelihoodDataset {
		
		final double bestObjectiveFunctionValue;
		final String[] paramNames; 
		final double[] bestParamValues;
		final ParameterEstimationTask originalTask;
		final ArrayList<ParameterScan> parameterScans = new ArrayList<ParameterScan>();
		
		public ProfilelikelihoodDataset(ParameterEstimationTask argTask, String[] paramNames, double[] bestParamValues, double bestObjectiveFunctionValue){
			this.originalTask = argTask;
			this.paramNames = paramNames;
			this.bestParamValues = bestParamValues;
			this.bestObjectiveFunctionValue = bestObjectiveFunctionValue;
		}
		
		//creates the parameter scan which calls the result set and the current objective value and the limit
		public ParameterScan createParameterScan(String parameterName){
			//Double  = set.getOptSolverResultSet().getLeastObjectiveFunctionValue();
			//final double objectiveFunctionLimit = leastObjectiveFunctionValue*1.5;
			final double objectiveFunctionLimit = bestObjectiveFunctionValue * 1.5;
			ParameterScan scan = new ParameterScan(originalTask,parameterName,bestObjectiveFunctionValue, objectiveFunctionLimit,paramNames,bestParamValues);
			parameterScans.add(scan);
			System.out.println(bestObjectiveFunctionValue);
			System.out.println(bestObjectiveFunctionValue*1.5);
			return scan;
		}
		
		//prints out a report for the parameter estimation run
		public String getReport(){
			StringBuffer buffer = new StringBuffer();
			buffer.append("Dataset\n");
			buffer.append("best fit parameter run (starting point of analysis)\n");
			buffer.append(Arrays.asList(paramNames) + " = " + Arrays.asList(bestParamValues) + "\n");
			buffer.append("scans:\n");
			for (ParameterScan scan : parameterScans){
				buffer.append(scan.getReport());
			}
			return buffer.toString();
		}

		//prints out a summary for the end of the run
		public String getSummary(){
			StringBuffer buffer = new StringBuffer();
			buffer.append("Dataset\n");
			buffer.append("best fit parameter run (starting point of analysis)\n");
			buffer.append(Arrays.asList(paramNames) + " = " + Arrays.asList(bestParamValues) + "\n");
			buffer.append("scans:\n");
			for (ParameterScan scan : parameterScans){
				buffer.append(scan.getSummary());
			}
			return buffer.toString();
		}

	}
	
	//creates the parameter scan and array lists for the increasing and decreasing values
	public static class ParameterScan {
		final double bestObjectiveFunction;
		final double objectiveFunctionLimit;
		final String parameterName;
		final String[] paramNames;
		final double[] bestParamValues;
		//private OptSolverResultSet bestResultSet;
		final ParameterEstimationTask origTask;
		
		public final ArrayList<ParestRun> increasingParestRuns = new ArrayList<ParestRun>();
		public final ArrayList<ParestRun> decreasingParestRuns = new ArrayList<ParestRun>();
		public double epsilon = 0.01;
		
		public ParameterScan(
				ParameterEstimationTask origTask, String paramName, double bestObjectiveFunction, 
				double objectiveFunctionLimit, String[] paramNames, double[] bestParamValues) {
			this.origTask = origTask;
			this.parameterName = paramName;
			this.paramNames = paramNames;
			this.bestParamValues = bestParamValues;
			this.bestObjectiveFunction = bestObjectiveFunction;
			this.objectiveFunctionLimit = objectiveFunctionLimit;
		}
		
		
		//for when the parameter estimation reaches a certain increasing limit, stop running 
		public boolean reachedIncreasingRunErrorLimit(){
			if (increasingParestRuns.size()==0){
				return false;
			}
			ParestRun lastIncreasingParestRun = increasingParestRuns.get(increasingParestRuns.size()-1);
			Double lastIncreasingObjFuncValue = lastIncreasingParestRun.getObjectiveFunctionValue();
System.out.println(lastIncreasingObjFuncValue);
System.out.println(objectiveFunctionLimit);
			
			double highLimit = origTask.getModelOptimizationSpec().getParameterMappingSpec(parameterName).getHigh();
			double current = lastIncreasingParestRun.getFittedParameterValues()[ParestRun.getParamIndex(paramNames, parameterName)];
//			origTask.getModelOptimizationSpec().getParameterMappingSpec(parameterName).getCurrent();

			
			return (current>highLimit) || (lastIncreasingObjFuncValue > objectiveFunctionLimit);
		}
		
		//for when the parameter estimation reaches a certain increasing limit, stop running 
		public boolean reachedDecreasingRunErrorLimit(){
			if (decreasingParestRuns.size()==0){
				return false;
			}
			ParestRun lastDecreasingParestRun = decreasingParestRuns.get(decreasingParestRuns.size()-1);
			Double lastDecreasingObjFuncValue = lastDecreasingParestRun.getObjectiveFunctionValue();
System.out.println(lastDecreasingObjFuncValue);
System.out.println(objectiveFunctionLimit);
			
			double lowLimit = origTask.getModelOptimizationSpec().getParameterMappingSpec(parameterName).getLow();
			double current = lastDecreasingParestRun.getFittedParameterValues()[ParestRun.getParamIndex(paramNames, parameterName)];
			
			return (current<lowLimit) || (lastDecreasingObjFuncValue > objectiveFunctionLimit);
		}
		
		//creates the increasing run
		public ParestRun createIncreasingRun(double factor){
			ParameterEstimationTask clonedTask = ParestRun.cloneParameterEstimationTask(origTask);
			//clonedTask.setOptimizationResultSet(null);

			//ParameterMappingSpec[] clonedSpecs = clonedTask.getModelOptimizationSpec().getParameterMappingSpecs();
			
			//
			// set all parameter values to "best", then change the "scan parameter"
			//
			for (int i=0;i<paramNames.length;i++){
				String name = paramNames[i];
				double value = bestParamValues[i];
				clonedTask.getModelOptimizationSpec().getParameterMappingSpec(name).setCurrent(value);
			}
			ParameterMappingSpec clonedSpec = ParestRun.findByParameterName(clonedTask.getModelOptimizationSpec().getParameterMappingSpecs(), parameterName);
			int N = this.increasingParestRuns.size()+1;
			double currentIncreasingValue = clonedSpec.getCurrent() * Math.pow(factor,N);
			clonedSpec.setCurrent(currentIncreasingValue);
			clonedSpec.setHigh(currentIncreasingValue*(1+epsilon));
			clonedSpec.setLow(currentIncreasingValue*(1-epsilon));

			ParestRun incParestRun = new ParestRun(clonedTask, paramNames, bestParamValues);
			
			this.increasingParestRuns.add(incParestRun);
			
			return incParestRun;
			
		}
		
		
		
		//creates the decreasing run
		public ParestRun createDecreasingRun(double factor) throws Exception{
			ParameterEstimationTask clonedTask = ParestRun.cloneParameterEstimationTask(origTask);
			//clonedTask.setOptimizationResultSet(null);
			
			//
			// set all parameter values to "best", then change the "scan parameter"
			//
			for (int i=0;i<paramNames.length;i++){
				String name = paramNames[i];
				double value = bestParamValues[i];
				clonedTask.getModelOptimizationSpec().getParameterMappingSpec(name).setCurrent(value);
			}
			ParameterMappingSpec clonedSpec = ParestRun.findByParameterName(clonedTask.getModelOptimizationSpec().getParameterMappingSpecs(), parameterName);
			int N = this.decreasingParestRuns.size()+1;
			double currentDecreasingValue = clonedSpec.getCurrent() * Math.pow(1.0/factor,N);
			clonedSpec.setCurrent(currentDecreasingValue);
			clonedSpec.setHigh(currentDecreasingValue*(1+epsilon));
			clonedSpec.setLow(currentDecreasingValue*(1-epsilon));
			
			ParestRun decParestRun = new ParestRun(clonedTask,paramNames,bestParamValues);
			
			this.decreasingParestRuns.add(decParestRun);
			
			return decParestRun;
		}
		
		public void resetRun(ParameterEstimationTask t){
			ParameterEstimationTask clonedTask = ParestRun.cloneParameterEstimationTask(t);
			ParameterMappingSpec[] clonedSpecs = clonedTask.getModelOptimizationSpec().getParameterMappingSpecs();
			ParameterMappingSpec clonedSpec = ParestRun.findByParameterName(clonedSpecs, parameterName);
			double current = clonedSpec.getCurrent();
			double high = clonedSpec.getHigh();
			double low = clonedSpec.getLow();
			clonedSpec.setCurrent(current);
			clonedSpec.setHigh(high);
			clonedSpec.setLow(low);
			return;
		}
//		public ParestRun getInitialParameters(ParameterMappingSpec[] parameterMappingSpecs){
//			ParameterEstimationTask clonedTask = ParestRun.cloneParameterEstimationTask(task);
//			ParameterMappingSpec[] origSpecs = clonedTask.getModelOptimizationSpec().getParameterMappingSpecs();
//			ParameterMappingSpec[] clonedSpecs = ParestRun.cloneParameterMappingSpecs(origSpecs);
//			
//			double current = clonedSpecs[1].getCurrent();
//			double high = clonedSpecs[1].getHigh();
//			double low = clonedSpecs[1].getLow();
//			clonedSpecs[1].setCurrent(current);
//			clonedSpecs[1].setHigh(high);
//			clonedSpecs[1].setLow(low);
//			return null;
//			
//			
//		}
		
		//reports the output
		public String getReport(){
			StringBuffer buffer = new StringBuffer();
			buffer.append("Scan for parameter "+parameterName+"\n");
			buffer.append("increasing runs\n");
			for (ParestRun run : this.increasingParestRuns){
				buffer.append("     "+run.getReport());
			}
			buffer.append("decreasing runs\n");
			for (ParestRun run : this.decreasingParestRuns){
				buffer.append("     "+run.getReport());
			}
			return buffer.toString();
		}
		//reports the output
		public String getSummary(){
			StringBuffer buffer = new StringBuffer();
			buffer.append("Scan for parameter "+parameterName+"\n");
			ParestRun lastIncreasingRun = increasingParestRuns.get(increasingParestRuns.size()-1);
			int paramIndex = ParestRun.getParamIndex(paramNames, parameterName);
			buffer.append("last increasing run (" + parameterName + " = " + lastIncreasingRun.getParameterInitialGuess()[paramIndex] + ") \n");
			buffer.append("     "+lastIncreasingRun.getReport());
			ParestRun lastDecreasingRun = decreasingParestRuns.get(decreasingParestRuns.size()-1);
			buffer.append("last decreasing run (" + parameterName + " = " + lastDecreasingRun.getParameterInitialGuess()[paramIndex] + ") \n");
			buffer.append("     "+lastDecreasingRun.getReport());

			HashMap<Double,Double> map = getParameterScanValuesMap();
			Double[] paramValues = map.keySet().toArray(new Double[0]);
			Arrays.sort(paramValues);
			for (Double paramValue : paramValues){
				double objectiveFunctionValue = map.get(paramValue);
				buffer.append(paramValue + " --> " + objectiveFunctionValue + "\n");
			}
			return buffer.toString();
			
		}

		
		public HashMap<Double,Double> getParameterScanValuesMap(){
			HashMap<Double,Double> parameterToObjectiveFunctionMap = new HashMap<>();
			int paramIndex = ParestRun.getParamIndex(paramNames,parameterName);
			// best fit at nominal value
			parameterToObjectiveFunctionMap.put(bestParamValues[paramIndex],bestObjectiveFunction);
			for (ParestRun decRun : decreasingParestRuns){
				parameterToObjectiveFunctionMap.put(decRun.getParameterInitialGuess()[paramIndex], decRun.getObjectiveFunctionValue());
			}
			for (ParestRun incRun : increasingParestRuns){
				parameterToObjectiveFunctionMap.put(incRun.getParameterInitialGuess()[paramIndex], incRun.getObjectiveFunctionValue());
			}
			return parameterToObjectiveFunctionMap;
		}
	}
	

	//this is where the parameter estimation run is continued and updated
	public static class ParestRun {
		
		//bFakeOptiimization is for a "fake" run to test how it runs
		static boolean bFakeOptimization = false;
		ParameterEstimationTask task;
		final String[] paramNames;
		final double[] bestParamValues;
//		final OptSolverResultSet bestFit;
		OptimizationResultSet resultSet;
		
		public ParestRun(ParameterEstimationTask task, String[] paramNames, double[] bestParamValues){
			// make a snapshot of the current parameter mapping spec values (since the object will be altered later)
			this.task = task;
			this.paramNames = paramNames;
			this.bestParamValues = bestParamValues;
		}
		
		public static ParameterMappingSpec findByParameterName(ParameterMappingSpec[] specs, String name){
			for (ParameterMappingSpec spec : specs){
				if (spec.getModelParameter().getName().equals(name)){
					return spec;
				}
			}
			throw new RuntimeException("couldn't find parameterMappingSpec with name " + name);
		}

		public static ParameterEstimationTask cloneParameterEstimationTask(ParameterEstimationTask originalTask) {
			BioModel clonedBioModel;
			try {
				clonedBioModel = XmlHelper.XMLToBioModel(new XMLSource(XmlHelper.bioModelToXML(originalTask.getSimulationContext().getBioModel())));
				clonedBioModel.refreshDependencies();
				SimulationContext clonedSimContext = clonedBioModel.getSimulationContext(originalTask.getSimulationContext().getName());
				ParameterEstimationTask taskCopy1 = (ParameterEstimationTask)clonedSimContext.getAnalysisTasks(0);
				taskCopy1.refreshMappings();
				return taskCopy1;
			} catch (XmlParseException | MappingException | MathException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to clone BioModel");
			}
		}
		
		static int getParamIndex(String[] paramNames, String parameterName) {
			int paramIndex = 0;
			for (;paramIndex<paramNames.length;paramIndex++){
				if (paramNames[paramIndex].equals(parameterName)){
					break;
				}
			}
			return paramIndex;
		}

		//this runs the parameter estimation and updates results
		public void optimize() throws Exception{
			if (bFakeOptimization && bestParamValues!=null){
				double objFunction = 1; // bestFit.getLeastObjectiveFunctionValue();
				ParameterMappingSpec[] currentParmMappingSpecs = task.getModelOptimizationSpec().getParameterMappingSpecs();
				double[] currParamValues = new double[bestParamValues.length];
				for (int i=0; i<paramNames.length;i++){
					double bestParamValue = bestParamValues[i];
					ParameterMappingSpec currSpec = ParestRun.findByParameterName(currentParmMappingSpecs, paramNames[i]);
					double currParamValue = currSpec.getCurrent();
					currParamValues[i] = currParamValue;
					objFunction = objFunction + Math.pow((bestParamValue-currParamValue)/bestParamValue,2);
				}
				OptimizationStatus status = new OptimizationStatus(OptimizationStatus.NORMAL_TERMINATION,"worked");
				OptRunResultSet runResults = new OptRunResultSet(currParamValues, objFunction, 100L, status);
				this.resultSet = new OptimizationResultSet(new OptSolverResultSet(paramNames, runResults),null);
				task.setOptimizationResultSet(this.resultSet);
			}else{
				this.resultSet = OptimizationService.optimize(task);
				task.setOptimizationResultSet(this.resultSet);
				if (bFakeOptimization){
					ParameterMappingSpec[] currentParmMappingSpecs = task.getModelOptimizationSpec().getParameterMappingSpecs();
					double[] currParamValues = new double[paramNames.length];
					for (int i=0; i<paramNames.length;i++){
						ParameterMappingSpec currSpec = ParestRun.findByParameterName(currentParmMappingSpecs, paramNames[i]);
						double currParamValue = currSpec.getCurrent();
						currParamValues[i] = currParamValue;
					}
					double objFunction = 1; // bestFit.getLeastObjectiveFunctionValue();
					OptimizationStatus status = new OptimizationStatus(OptimizationStatus.NORMAL_TERMINATION,"worked");
					OptRunResultSet runResults = new OptRunResultSet(currParamValues, objFunction, 100L, status);
					this.resultSet = new OptimizationResultSet(new OptSolverResultSet(paramNames, runResults),null);
					task.setOptimizationResultSet(this.resultSet);
				}
			}
			try {
				System.out.println("ParestRun.optimize():  bFake="+bFakeOptimization+", report="+getReport());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public String[] getParameterNames(){
			return getParameterNames(this.task);
		}
		
		public static String[] getParameterNames(ParameterEstimationTask task){
			ParameterMappingSpec[] specs = task.getModelOptimizationSpec().getParameterMappingSpecs();
			String[] names = new String[task.getModelOptimizationSpec().getNumberSelectedParameters()];
			int index = 0;
			for (int i=0;i<specs.length;i++){
				if (specs[i].isSelected()){
					names[index++] = specs[i].getModelParameter().getName();
				}
			}
			return names;
		}
		
		public double[] getParameterLimitsLow(){
			return getParameterLimitsLow(this.task);
		}
		public double[] getParameterLimitsHigh(){
			return getParameterLimitsHigh(this.task);
		}
		public double[] getParameterInitialGuess(){
			return getParameterInitialGuess(this.task);
		}
		
		public static double[] getParameterLimitsLow(ParameterEstimationTask task){
			ParameterMappingSpec[] specs = task.getModelOptimizationSpec().getParameterMappingSpecs();
			double[] lowLimits = new double[task.getModelOptimizationSpec().getNumberSelectedParameters()];
			int index = 0;
			for (int i=0;i<specs.length;i++){
				if (specs[i].isSelected()){
					lowLimits[index++] = specs[i].getLow();
				}
			}
			return lowLimits;
		}
		
		public static double[] getParameterLimitsHigh(ParameterEstimationTask task){
			ParameterMappingSpec[] specs = task.getModelOptimizationSpec().getParameterMappingSpecs();
			double[] highLimits = new double[task.getModelOptimizationSpec().getNumberSelectedParameters()];
			int index = 0;
			for (int i=0;i<specs.length;i++){
				if (specs[i].isSelected()){
					highLimits[index++] = specs[i].getHigh();
				}
			}
			return highLimits;
		}
		
		public static double[] getParameterInitialGuess(ParameterEstimationTask task){
			ParameterMappingSpec[] specs = task.getModelOptimizationSpec().getParameterMappingSpecs();
			double[] current = new double[task.getModelOptimizationSpec().getNumberSelectedParameters()];
			int index = 0;
			for (int i=0;i<specs.length;i++){
				if (specs[i].isSelected()){
					current[index++] = specs[i].getCurrent();
				}
			}
			return current;
		}
		
		public double[] getFittedParameterValues(){
			return getFittedParameterValues(this.task);
		}
		
		public static double[] getFittedParameterValues(ParameterEstimationTask task){
			ParameterMappingSpec[] specs = task.getModelOptimizationSpec().getParameterMappingSpecs();
			double[] values = new double[task.getModelOptimizationSpec().getNumberSelectedParameters()];
			int index = 0;
			for (int i=0;i<specs.length;i++){
				if (specs[i].isSelected()){
					values[index++] = task.getCurrentSolution(specs[i]);
				}
			}
			return values;
		}		

		public String getReport(){
			if (bestParamValues!=null){
				return "Run:  objectiveValue = "+ getObjectiveFunctionValue() + "\n" +
						"params  [" + Arrays.toString(paramNames) + "]" + "\n" +
						"best**  [" + Arrays.toString(bestParamValues) + "]" + "\n" +
						"fitted  [" + Arrays.toString(getFittedParameterValues()) + "]" + "\n" +
						"low     [" + Arrays.toString(getParameterLimitsLow()) +"]\n" +
						"initial [" + Arrays.toString(getParameterInitialGuess()) +"]\n" +
						"high    [" + Arrays.toString(getParameterLimitsHigh()) +"]\n\n";
			}else{
				return "Run:  objectiveValue = "+ getObjectiveFunctionValue() + "\n" +
						"params  [" + Arrays.toString(paramNames) + "]" + "\n" +
						"best**  []" + "\n" +
						"fitted  [" + Arrays.toString(getFittedParameterValues()) + "]" + "\n" +
						"low     [" + Arrays.toString(getParameterLimitsLow()) +"]\n" +
						"initial [" + Arrays.toString(getParameterInitialGuess()) +"]\n" +
						"high    [" + Arrays.toString(getParameterLimitsHigh()) +"]\n\n";
			}
		}
		
		public double getObjectiveFunctionValue() {
			return task.getOptimizationResultSet().getOptSolverResultSet().getLeastObjectiveFunctionValue();
			//lastDecreasingParestRun.resultSet.getOptSolverResultSet().getLeastObjectiveFunctionValue();
		}
		
	}

	//main
	public static void main(String[] args) {
		try {
			ResourceUtil.setNativeLibraryDirectory();
			NativeLib.COPASI.load( );
			File newXML = new File(args[0]);
			XMLSource source = new XMLSource(newXML);
			BioModel biomodel = XmlHelper.XMLToBioModel(source);
			SimulationContext app = biomodel.getSimulationContext("Deterministic");
			AnalysisTask[] task = app.getAnalysisTasks();
			
			//ParestRun.bFakeOptimization = true;

		
			final ProfileLikelihoodCallback callback = new ProfileLikelihoodCallback() {
				
				@Override
				public void report(String msg) {
					System.out.println(msg);
					
				}
			};
			ProfileLikelihood pl = new ProfileLikelihood(callback);
			pl.run((ParameterEstimationTask) task[0]);
		} catch (XmlParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	final ProfileLikelihoodCallback callback0;

	public ProfileLikelihood (ProfileLikelihoodCallback callback) {
		callback0 = callback;
	}

	
	public void run(ParameterEstimationTask taskOrig) throws Exception {
		String[] parameterNames = ParestRun.getParameterNames(taskOrig);
		ParestRun bestFitRun = new ParestRun(ParestRun.cloneParameterEstimationTask(taskOrig),parameterNames,null);
		bestFitRun.optimize();
		
		ProfilelikelihoodDataset dataset = new ProfilelikelihoodDataset(taskOrig,bestFitRun.getParameterNames(),bestFitRun.getFittedParameterValues(),bestFitRun.getObjectiveFunctionValue());

		for (String parameterName : parameterNames){
			double factor = 1.2;
			ParameterScan scan = dataset.createParameterScan(parameterName);
			while (!scan.reachedDecreasingRunErrorLimit()){
				ParestRun run = scan.createDecreasingRun(factor);
				run.optimize();
				System.out.println(run.getReport());
			}
			while (!scan.reachedIncreasingRunErrorLimit()){
				ParestRun run = scan.createIncreasingRun(factor);
				run.optimize();
				System.out.println(run.getReport());
			}
		}
		System.out.println(dataset.getSummary());
		//System.out.println(dataset.getReport());
	}
	
	
}
			//serializable didn't work, this clones the biomodel to get a copy

