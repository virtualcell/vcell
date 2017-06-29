package cbit.vcell.modelopt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.vcell.optimization.gui.ParameterEstimationRunTaskPanel;
import org.vcell.util.BeanUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

public class ProfileLikelihood {
	
	public static void main(String[] args) {
		try {
			ResourceUtil.setNativeLibraryDirectory();
			NativeLib.COPASI.load( );
			File newXML = new File(args[0]);
			XMLSource source = new XMLSource(newXML);
			BioModel biomodel = XmlHelper.XMLToBioModel(source);
			SimulationContext app = biomodel.getSimulationContext("Deterministic");
			AnalysisTask[] task = app.getAnalysisTasks();
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
			Double leastObjectiveFunctionValue = taskCopy1.getOptimizationResultSet().getOptSolverResultSet().getLeastObjectiveFunctionValue();
			//Double  = set.getOptSolverResultSet().getLeastObjectiveFunctionValue();
			ParameterScan scan = new ParameterScan(taskCopy1,parameterName,leastObjectiveFunctionValue, leastObjectiveFunctionValue*2);
			paramterScanMap.put(parameterName, scan);
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
		private double bestfitParamValue;
		private double currentIncreasingValue;
		private double currentDecreasingValue;
		final ParameterEstimationTask task;
		
		public final ArrayList<ParestRun> increasingParestRuns = new ArrayList<ParestRun>();
		public final ArrayList<ParestRun> decreasingParestRuns = new ArrayList<ParestRun>();
		public double epsilon = 0.01;
		
		public ParameterScan(
				ParameterEstimationTask task, String paramName, double bestValue, double objectiveFunctionLimit) {
			this.task = task;
			this.parameterName = paramName;
			this.bestfitParamValue = bestValue;
			this.currentDecreasingValue = bestValue;
			this.currentIncreasingValue = bestValue;
			this.objectiveFunctionLimit = objectiveFunctionLimit;
		}
		
		
		//for when the parameter estimation reaches a certain limit, stop running 
		public boolean reachedIncreasingRunErrorLimit(){
			return true;
//			if (increasingParestRuns.size()==0){
//				return false;
//			}
//			ParestRun lastIncreasingParestRun = increasingParestRuns.get(increasingParestRuns.size()-1);
//			Double lastIncreasingObjFuncValue = lastIncreasingParestRun.resultSet.getOptSolverResultSet().getLeastObjectiveFunctionValue();
//			return (lastIncreasingObjFuncValue > this.objectiveFunctionLimit);
		}
		
		public boolean reachedDecreasingRunErrorLimit(){
			if (decreasingParestRuns.size()==0){
				return false;
			}
			ParestRun lastDecreasingParestRun = decreasingParestRuns.get(decreasingParestRuns.size()-1);
			Double lastDecreasingObjFuncValue = lastDecreasingParestRun.resultSet.getOptSolverResultSet().getLeastObjectiveFunctionValue();
			return (lastDecreasingObjFuncValue > this.objectiveFunctionLimit);
		}
		
		//creates the increasing and decreasing runs
		public ParestRun createIncreasingRun(double factor){
			ParameterEstimationTask clonedTask = ParestRun.cloneParameterEstimationTask(task);
			ParameterMappingSpec[] origSpecs = clonedTask.getModelOptimizationSpec().getParameterMappingSpecs();
			ParameterMappingSpec[] clonedSpecs = ParestRun.cloneParameterMappingSpecs(origSpecs);
			
			ParameterMappingSpec clonedSpec = ParestRun.findByParameterName(clonedSpecs, parameterName);
			this.currentIncreasingValue = this.currentIncreasingValue * factor;
			double current = clonedSpecs[1].getCurrent();
			double newcurr = current * factor;
			double newhigh = newcurr + epsilon;
			double newlow = newcurr - epsilon;
			clonedSpecs[1].setCurrent(newcurr);
			clonedSpecs[1].setHigh(newhigh);
			clonedSpecs[1].setLow(newlow);
			ParestRun incParestRun = new ParestRun(clonedTask);
			this.increasingParestRuns.add(incParestRun);
			return incParestRun;
		}
		
		public ParestRun createDecreasingRun(double factor){
			ParameterEstimationTask clonedTask = ParestRun.cloneParameterEstimationTask(task);
			ParameterMappingSpec[] origSpecs = clonedTask.getModelOptimizationSpec().getParameterMappingSpecs();
			ParameterMappingSpec[] clonedSpecs = ParestRun.cloneParameterMappingSpecs(origSpecs);
			
			ParameterMappingSpec clonedSpec = ParestRun.findByParameterName(clonedSpecs, parameterName);
			this.currentDecreasingValue = this.currentDecreasingValue / factor;
			clonedSpec.setCurrent(currentDecreasingValue);
			clonedSpec.setHigh(currentDecreasingValue + epsilon);
			clonedSpec.setLow(currentDecreasingValue - epsilon);
			ParestRun decParestRun = new ParestRun(clonedTask);
			this.decreasingParestRuns.add(decParestRun);
			return decParestRun;
		}
		
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
		ParameterEstimationTask task;
		OptimizationResultSet resultSet;
		
		public ParestRun(ParameterEstimationTask task){
			// make a snapshot of the current parameter mapping spec values (since the object will be altered later)
			this.task = task;
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
			this.resultSet = OptimizationService.optimize(task);
		}
		
		public static double[] getParameterValues(ParameterEstimationTask task){
			ParameterMappingSpec[] specs = task.getModelOptimizationSpec().getParameterMappingSpecs();
			double[] values = new double[specs.length];
			for (int i=0;i<specs.length;i++){
				values[i] = specs[i].getCurrent();
			}
			return values;
		}
		
		public String getReport(){
			double data[] = getParameterValues(task);

			double leastObjectiveFunctionValue = this.resultSet.getOptSolverResultSet().getLeastObjectiveFunctionValue();
			
			return "params [" + Arrays.asList(data) + "], objectiveValue = "+ leastObjectiveFunctionValue + "\n";
		}
		
	}
	public void run(ParameterEstimationTask taskOrig) throws Exception {
		//ModelOptimizationSpec specOrig = taskOrig.getModelOptimizationSpec();
		//ParameterMappingSpec[] parameterMappingSpecsOrig = specOrig.getParameterMappingSpecs();
		
		//run with current parameters
		
//		double keepCurrent = parameterMappingSpecsOrig[1].getCurrent();
//		double keepHigh = parameterMappingSpecsOrig[1].getHigh();
//		double keepLow = parameterMappingSpecsOrig[1].getLow();
//		OptimizationResultSet objvalueOrig = OptimizationService.optimize(taskOrig);
		
		ParestRun bestFitRun = new ParestRun(ParestRun.cloneParameterEstimationTask(taskOrig));
		bestFitRun.optimize();
		
		ProfilelikelihoodDataset dataset = new ProfilelikelihoodDataset(taskOrig,bestFitRun);
		

//		double objectiveFunctionLimit = 2 * objfuncOrig;
		
		
		//this will set new parameters in the positive direction
		double factor = 1.5;
		

		ParameterMappingSpec[] parameterMappingSpecsCopy = taskOrig.getModelOptimizationSpec().getParameterMappingSpecs();
		for (ParameterMappingSpec parameterMappingSpec : parameterMappingSpecsCopy){
			String parameterName = parameterMappingSpec.getModelParameter().getName();
			ParameterScan scan = dataset.createParameterScan(parameterName);
			
//			while (!scan.reachedDecreasingRunErrorLimit()){
//				ParestRun run = scan.createDecreasingRun(factor);
//				run.optimize();
//			}
			while (!scan.reachedIncreasingRunErrorLimit()){
				ParestRun run = scan.createIncreasingRun(factor);
				run.optimize();
//				System.out.println(parameterMappingSpecsCopy[1].getCurrent());
//				System.out.println(parameterMappingSpecsCopy[1].getHigh());
//				System.out.println(parameterMappingSpecsCopy[1].getLow());
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


	


