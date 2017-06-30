package cbit.vcell.modelopt;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.opt.OptSolverResultSet;
import cbit.vcell.opt.OptSolverResultSet.OptRunResultSet;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationStatus;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

public class ProfileLikelihood {
	//this creates the data set and parameter scan that is being used to update the parameters
	public static class ProfilelikelihoodDataset {
		
		final ParestRun bestFitParestRun;
		final ParameterEstimationTask originalTask;
		HashMap<String,ParameterScan> paramterScanMap = new HashMap<String,ParameterScan>(); 
		
		public ProfilelikelihoodDataset(ParameterEstimationTask argTask, ParestRun argBestFitParestRun){
			this.originalTask = argTask;
			this.bestFitParestRun = argBestFitParestRun;
		}
		
		public ParameterScan createParameterScan(String parameterName){
			ParameterEstimationTask taskCopy1 = ParestRun.cloneParameterEstimationTask(originalTask);
			OptSolverResultSet optSolverResultSet = taskCopy1.getOptimizationResultSet().getOptSolverResultSet();
			Double leastObjectiveFunctionValue = optSolverResultSet.getLeastObjectiveFunctionValue();
			//Double  = set.getOptSolverResultSet().getLeastObjectiveFunctionValue();
			ParameterScan scan = new ParameterScan(taskCopy1,parameterName,leastObjectiveFunctionValue, leastObjectiveFunctionValue*1.5,optSolverResultSet);
			paramterScanMap.put(parameterName, scan);
			System.out.println(leastObjectiveFunctionValue);
			System.out.println(leastObjectiveFunctionValue*1.5);
			return scan;
		}
		
		public String getReport(){
			StringBuffer buffer = new StringBuffer();
			buffer.append("Dataset\n");
			buffer.append("best fit parameter run (starting point of analysis)\n");
			buffer.append(bestFitParestRun.getReport());
			buffer.append("scans:\n");
			for (String paramName : paramterScanMap.keySet()){
				ParameterScan scan = paramterScanMap.get(paramName);
				buffer.append(scan.getReport());
			}
			return buffer.toString();
		}

	}
	
	//creates the parameter scan and array lists for the increasing and decreasing values
	public static class ParameterScan {
		final double objectiveFunctionLimit;
		final String parameterName;
		private OptSolverResultSet bestResultSet;
		final ParameterEstimationTask task;
		
		public final ArrayList<ParestRun> increasingParestRuns = new ArrayList<ParestRun>();
		public final ArrayList<ParestRun> decreasingParestRuns = new ArrayList<ParestRun>();
		public double epsilon = 0.01;
		
		public ParameterScan(
				ParameterEstimationTask task, String paramName, double bestValue, 
				double objectiveFunctionLimit, OptSolverResultSet bestFit) {
			this.task = task;
			this.parameterName = paramName;
			this.bestResultSet = bestFit;
			this.objectiveFunctionLimit = objectiveFunctionLimit;
		}
		
		
		//for when the parameter estimation reaches a certain limit, stop running 
		public boolean reachedIncreasingRunErrorLimit(){
			if (increasingParestRuns.size()==0){
				return false;
			}
			ParestRun lastIncreasingParestRun = increasingParestRuns.get(increasingParestRuns.size()-1);
			Double lastIncreasingObjFuncValue = lastIncreasingParestRun.resultSet.getOptSolverResultSet().getLeastObjectiveFunctionValue();
			System.out.println(lastIncreasingObjFuncValue);
			System.out.println(objectiveFunctionLimit);
			
			return lastIncreasingObjFuncValue > 2;
//			return (lastIncreasingObjFuncValue > this.objectiveFunctionLimit);
//				System.out.println("exceeded upper limit");
//				return false;
			
		}
		
		public boolean reachedDecreasingRunErrorLimit(){
			if (decreasingParestRuns.size()==0){
				return false;
			}
			ParestRun lastDecreasingParestRun = decreasingParestRuns.get(decreasingParestRuns.size()-1);
			Double lastDecreasingObjFuncValue = lastDecreasingParestRun.resultSet.getOptSolverResultSet().getLeastObjectiveFunctionValue();
			System.out.println(lastDecreasingObjFuncValue);
			System.out.println(objectiveFunctionLimit);
			
			return lastDecreasingObjFuncValue > 1.2;
			//return (lastDecreasingObjFuncValue > this.objectiveFunctionLimit);
		}
		
		//creates the increasing and decreasing runs
		public ParestRun createIncreasingRun(double factor){
			ParameterEstimationTask clonedTask = ParestRun.cloneParameterEstimationTask(task);
			ParameterMappingSpec[] clonedSpecs = clonedTask.getModelOptimizationSpec().getParameterMappingSpecs();
			
			//
			// set all parameter values to "best", then change the "scan parameter"
			//
			for (int i=0;i<bestResultSet.getParameterNames().length;i++){
				String name = bestResultSet.getParameterNames()[i];
				double value = bestResultSet.getBestEstimates()[i];
				clonedTask.getModelOptimizationSpec().getParameterMappingSpec(name).setCurrent(value);
			}
			ParameterMappingSpec clonedSpec = ParestRun.findByParameterName(clonedSpecs, parameterName);
			int N = this.increasingParestRuns.size()+1;
			double currentIncreasingValue = clonedSpec.getCurrent() * Math.pow(factor,N);
			clonedSpec.setCurrent(currentIncreasingValue);
			clonedSpec.setHigh(currentIncreasingValue+epsilon);
			clonedSpec.setLow(currentIncreasingValue-epsilon);

			ParestRun incParestRun = new ParestRun(clonedTask, bestResultSet);
			
			this.increasingParestRuns.add(incParestRun);
			return incParestRun;
		}
		
		public ParestRun createDecreasingRun(double factor) throws Exception{
			ParameterEstimationTask clonedTask = ParestRun.cloneParameterEstimationTask(task);
			ParameterMappingSpec[] clonedSpecs = clonedTask.getModelOptimizationSpec().getParameterMappingSpecs();
			
			//
			// set all parameter values to "best", then change the "scan parameter"
			//
			for (int i=0;i<bestResultSet.getParameterNames().length;i++){
				String name = bestResultSet.getParameterNames()[i];
				double value = bestResultSet.getBestEstimates()[i];
				clonedTask.getModelOptimizationSpec().getParameterMappingSpec(name).setCurrent(value);
			}
			ParameterMappingSpec clonedSpec = ParestRun.findByParameterName(clonedSpecs, parameterName);
			int N = this.decreasingParestRuns.size()+1;
			double currentDecreasingValue = clonedSpec.getCurrent() * Math.pow(1.0/factor,N);
			clonedSpec.setCurrent(currentDecreasingValue);
			clonedSpec.setHigh(currentDecreasingValue+epsilon);
			clonedSpec.setLow(currentDecreasingValue-epsilon);
			
			ParestRun decParestRun = new ParestRun(clonedTask,bestResultSet);
			
			this.decreasingParestRuns.add(decParestRun);
			return decParestRun;
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
	}

	
	public static class ParestRun {
		static boolean bFakeOptimization = false;
		ParameterEstimationTask task;
		final OptSolverResultSet bestFit;
		OptimizationResultSet resultSet;
		
		public ParestRun(ParameterEstimationTask task, OptSolverResultSet bestFit){
			// make a snapshot of the current parameter mapping spec values (since the object will be altered later)
			this.task = task;
			this.bestFit = bestFit;
		}
		
		public static ParameterMappingSpec findByParameterName(ParameterMappingSpec[] specs, String name){
			for (ParameterMappingSpec spec : specs){
				if (spec.getModelParameter().getName().equals(name)){
					return spec;
				}
			}
			throw new RuntimeException("couldn't find parameterMappingSpec with name " + name);
		}

		public static ParameterMappingSpec[] cloneParameterMappingSpecs(ParameterMappingSpec[] argParameterMappingSpecs) {
			ParameterMappingSpec[] specs = new ParameterMappingSpec[argParameterMappingSpecs.length];
			for (int i=0; i<specs.length;i++){
				specs[i] = (argParameterMappingSpecs[i]);
			}
			return specs;
		}
		
		public static ParameterEstimationTask cloneParameterEstimationTask(ParameterEstimationTask originalTask) {
			BioModel clonedBioModel = originalTask.getSimulationContext().getBioModel();
			clonedBioModel.refreshDependencies();
			SimulationContext clonedSimContext = clonedBioModel.getSimulationContext(originalTask.getSimulationContext().getName());
			ParameterEstimationTask taskCopy1 = (ParameterEstimationTask)clonedSimContext.getAnalysisTasks(0);
			return taskCopy1;
		}

		public void optimize() throws Exception{
			if (bFakeOptimization && bestFit!=null){
				double objFunction = 1; // bestFit.getLeastObjectiveFunctionValue();
				String[] paramNames = bestFit.getParameterNames();
				double[] bestParamValues = bestFit.getBestEstimates();
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
			}else{
				this.resultSet = OptimizationService.optimize(task);
			}
			System.out.println("ParestRun.optimize():  bFake="+bFakeOptimization+", report="+getReport());
		}
		
		public static double[] getParameterValues(ParameterEstimationTask task){
			ParameterMappingSpec[] specs = task.getModelOptimizationSpec().getParameterMappingSpecs();
			double[] values = new double[task.getModelOptimizationSpec().getNumberSelectedParameters()];
			int index = 0;
			for (int i=0;i<specs.length;i++){
				if (specs[i].isSelected()){
					values[index++] = specs[i].getCurrent();
				}
			}
			return values;
		}
		
		public String getReport(){
			double data[] = getParameterValues(task);

			double leastObjectiveFunctionValue = this.resultSet.getOptSolverResultSet().getLeastObjectiveFunctionValue();
			
			return "params2 [" + Arrays.toString(data) + "], objectiveValue = "+ leastObjectiveFunctionValue + "\n";
		}
		
	}

	
	public static void main(String[] args) {
		try {
			ResourceUtil.setNativeLibraryDirectory();
			NativeLib.COPASI.load( );
			File newXML = new File(args[0]);
			XMLSource source = new XMLSource(newXML);
			BioModel biomodel = XmlHelper.XMLToBioModel(source);
			SimulationContext app = biomodel.getSimulationContext("Deterministic");
			AnalysisTask[] task = app.getAnalysisTasks();
			
			ParestRun.bFakeOptimization = true;

		
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
		//ModelOptimizationSpec specOrig = taskOrig.getModelOptimizationSpec();
		//ParameterMappingSpec[] parameterMappingSpecsOrig = specOrig.getParameterMappingSpecs();
		
		//run with current parameters
		
//		double keepCurrent = parameterMappingSpecsOrig[1].getCurrent();
//		double keepHigh = parameterMappingSpecsOrig[1].getHigh();
//		double keepLow = parameterMappingSpecsOrig[1].getLow();
//		OptimizationResultSet objvalueOrig = OptimizationService.optimize(taskOrig);
		ParestRun bestFitRun = new ParestRun(ParestRun.cloneParameterEstimationTask(taskOrig),null);
		bestFitRun.optimize();
		
		ProfilelikelihoodDataset dataset = new ProfilelikelihoodDataset(taskOrig,bestFitRun);
		

//		double objectiveFunctionLimit = 2 * objfuncOrig;
		
		
		//this will set new parameters in the positive direction
		double factor = 1.2;
		

		ParameterMappingSpec[] parameterMappingSpecsCopy = taskOrig.getModelOptimizationSpec().getParameterMappingSpecs();
		for (ParameterMappingSpec parameterMappingSpec : parameterMappingSpecsCopy){
			if (!parameterMappingSpec.isSelected()){
				continue;
			}
			String parameterName = parameterMappingSpec.getModelParameter().getName();
			ParameterScan scan = dataset.createParameterScan(parameterName);
			double keepcurrent = parameterMappingSpecsCopy[1].getCurrent();
			double keephigh = parameterMappingSpecsCopy[1].getHigh();
			double keeplow = parameterMappingSpecsCopy[1].getLow();
			while (!scan.reachedDecreasingRunErrorLimit()){
				ParestRun run = scan.createDecreasingRun(factor);
				run.optimize();
//				System.out.println(parameterMappingSpecsCopy[1].getCurrent());
//				System.out.println(parameterMappingSpecsCopy[1].getHigh());
//				System.out.println(parameterMappingSpecsCopy[1].getLow());
				System.out.println(run.getReport());
			}
			parameterMappingSpecsCopy[1].setCurrent(keepcurrent);
			parameterMappingSpecsCopy[1].setHigh(keephigh);
			parameterMappingSpecsCopy[1].setLow(keeplow);
			while (!scan.reachedIncreasingRunErrorLimit()){
				ParestRun run = scan.createIncreasingRun(factor);
				run.optimize();
				System.out.println(parameterMappingSpecsCopy[1].getCurrent());
				System.out.println(parameterMappingSpecsCopy[1].getHigh());
				System.out.println(parameterMappingSpecsCopy[1].getLow());
				System.out.println(run.getReport());
			}
		}
		//System.out.println(dataset.getReport());
	}
	//call parameter est.
			//save the values
			//take the first parameter remove from list
			//drag a parameter from current value in positive direction until error becomes 2x as large
			//repeat in negative direction as 2x as large
			//record parameter min and max values until this happens
			//restore original parameter and go onto next parameter
			//repeat for all parameters
	
}
			//serializable didn't work, this clones the biomodel to get a copy
			
			
			
			//should be operating on the copy of the model/parameter specs
			
				//have j be iterations until 2x obj value is goal
//		int j = 0;
//			while (j < 3){
//				
//				ParestRun parestRun = scan.createIncreasingRun(factor);
//				parestRun.optimize();
//				System.out.println(parestRun.getReport());
				
				
				
						//System.out.println("Check2");
				//int n=1;
				
				//forward
				
				
						
//				double Current = parameterMappingSpecsCopy[n].getCurrent();
//				//multiply by 2 to increase parameters 
//				double newCurrent = Current * 1.1; 
//				parameterMappingSpecsCopy[n].setCurrent(newCurrent);
//				//parameterMappingSpec.setCurrent(newCurrent);
//						
//				double High = parameterMappingSpecsCopy[n].getHigh();
//				double newHigh = High * 1.1;
//				parameterMappingSpecsCopy[n].setHigh(newHigh);
//				//parameterMappingSpec.setHigh(newHigh);
//						
//				double Low = parameterMappingSpecsCopy[n].getLow();
//				double newLow = Low * 1.1;
//				parameterMappingSpecsCopy[n].setLow(newLow);
//				//parameterMappingSpec.setLow(newLow);
//				
//				OptimizationResultSet objvalueCopy = OptimizationService.optimize(taskCopy1);
//				double objfuncCopy = objvalueCopy.getOptSolverResultSet().getLeastObjectiveFunctionValue();
//				data[j] = (int) objfuncCopy;
//				System.out.println(data);
//				System.out.println("This is the new current guess: " + newCurrent);
//				System.out.println("This is the new current high: " + newHigh);
//				System.out.println("This is the new current low: " + newLow);
//				System.out.println(objfuncOrig + "," + objfuncCopy);
//					
//				//System.out.println("Check3");
//				System.out.println("Initial current guess: " + keepCurrent);
//				System.out.println("Initial current high: " + keepHigh);
//				System.out.println("Initial current low: " + keepLow);
				//double newobj1 = objvalue.getOptSolverResultSet().getLeastObjectiveFunctionValue();
				//System.out.println(newobj1);
				//j++;
//				if (objfuncCopy < obj2){
//				//specOrig = specCopy;
//				j++;
//				}
//				else{
//					System.out.println("TEST");
//				}
				
				//negative direction
//				while (j < 3){
//					OptimizationResultSet objvalueCopy2 = OptimizationService.optimize(taskCopy);
//							
//					double Current2 = parameterMappingSpecsCopy[n].getCurrent();
//					//multiply by 2 to increase parameters 
//					double newCurrent2 = Current / 1.1; 
//					parameterMappingSpecsCopy[n].setCurrent(newCurrent);
//					parameterMappingSpec.setCurrent(newCurrent);
//							
//					double High2 = parameterMappingSpecsCopy[n].getHigh();
//					double newHigh2 = High / 1.1;
//					parameterMappingSpecsCopy[n].setHigh(newHigh);
//					//parameterMappingSpec.setHigh(newHigh);
//							
//					double Low2 = parameterMappingSpecsCopy[n].getLow();
//					double newLow2 = Low / 1.1;
//					parameterMappingSpecsCopy[n].setLow(newLow);
//					//parameterMappingSpec.setLow(newLow);
//							
//					double objfuncCopy2 = objvalueCopy.getOptSolverResultSet().getLeastObjectiveFunctionValue();





					

	
			//System.out.println(keepCurrent);
			//parameterMappingSpecs[n].setCurrent(keepCurrent);
			//System.out.println(keepHigh);
			//parameterMappingSpecs[n].setHigh(keepHigh);
			//System.out.println(keepLow);
			//parameterMappingSpecs[n].setLow(keepLow);
			
		
		










//	public static OptimizationResultSet calculateFittingError(ParameterEstimationTask t2) throws Exception {
//		OptimizationResultSet objvalue = OptimizationService.optimize(t2);
//		double objfunc = objvalue.getOptSolverResultSet().getLeastObjectiveFunctionValue();
//		System.out.println("The best value for this set of parameters is: " + objfunc);	
//		return objvalue;
//	}


	


