package cbit.vcell.util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.NumberUtils;
import org.vcell.util.Origin;
import org.vcell.util.Range;

import cbit.vcell.math.VariableType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.solvers.CartesianMesh;

public class FunctionRangeGenerator {
	
	public static class VarStatistics {
		public final String stateVariableName;
		public final double[] minValuesOverTime;
		public final double[] maxValuesOverTime;
		public VarStatistics(String stateVariableName, double[] minValuesOverTime, double[] maxValuesOverTime) {
			super();
			this.stateVariableName = stateVariableName;
			this.minValuesOverTime = minValuesOverTime;
			this.maxValuesOverTime = maxValuesOverTime;
		}
		
	}
	
	public static class FunctionStatistics {
		private final double[] minValueEstimate; // just for info, don't use this for the display scale
		private final double[] maxValueEstimate; // just for info, don't use this for the display scale
		private final Range defaultDisplayRange;
		
		public FunctionStatistics(double[] minValueEstimate, double[] maxValueEstimate){
			this.minValueEstimate = minValueEstimate;
			this.maxValueEstimate = maxValueEstimate;
			this.defaultDisplayRange = NumberUtils.getDecimalRange(getMinOverTime(),getMaxOverTime(),false,false);
		}
		
		public double getMinOverTime(){
			double minValue = minValueEstimate[0];
			for (double value : minValueEstimate){
				minValue = Math.min(minValue, value);
			}
			return minValue;
		}
		
		public double getMaxOverTime(){
			double maxValue = maxValueEstimate[0];
			for (double value : maxValueEstimate){
				maxValue = Math.max(maxValue, value);
			}
			return maxValue;
		}
		
		public Range getDefaultDatasetRange(){
			return defaultDisplayRange;
		}
	}
	
	//
	// note: functionExp should already be flattened to only have symbols for state variables, x, y, z, and t
	//
	public static FunctionStatistics getFunctionStatistics(Expression functionExp, VarStatistics[] varStatistics, double[] times,CartesianMesh cartesianMesh,BitSet inDomainBitSetOrig, VariableType variableType,int numSamplesPerDim) throws Exception{
		ArrayList<Integer> inDomainIndexes =  new ArrayList<>();
		for (int i = inDomainBitSetOrig.nextSetBit(0); i >= 0; i = inDomainBitSetOrig.nextSetBit(i + 1)) {
			// operate on index i here
			if (i == Integer.MAX_VALUE) {
				break;
			}
			inDomainIndexes.add(i);
		}

		if (varStatistics.length == 0){
			double constantValue = functionExp.evaluateConstant();
			double[] minValues = new double[times.length];
			Arrays.fill(minValues, constantValue);
			double[] maxValues = new double[times.length];
			Arrays.fill(maxValues, constantValue);
			return new FunctionStatistics(minValues,maxValues);
		}
		ArrayList<VarStatistics> varStatList = new ArrayList<VarStatistics>();
		varStatList.addAll(Arrays.asList(varStatistics));
		int numVars = varStatistics.length;
		long numSamples = numVars*numSamplesPerDim;
		ArrayList<String> symbols = new ArrayList<>();
		if (functionExp.hasSymbol("x")){
			numSamples *= numSamplesPerDim;
		}
		if (functionExp.hasSymbol("y")){
			numSamples *= numSamplesPerDim;
		}
		if (functionExp.hasSymbol("z")){
			numSamples *= numSamplesPerDim;
		}
		//
		// establishes order of values when evaluating, values={t,x,y,z,var1,var2, ... varN}
		//
		symbols.add("t");
		symbols.add("x");
		symbols.add("y");
		symbols.add("z");
		for (VarStatistics varStat : varStatistics){
			symbols.add(varStat.stateVariableName);
		}
		SimpleSymbolTable symTable = new SimpleSymbolTable(symbols.toArray(new String[0]));
		functionExp.bindExpression(symTable);
		
		// loop through time, at each time sample state variables (and x,y,z if necessary) to estimate
		// the min and max values for that time.
		Random rand = new Random(1);
		double[] values = new double[symbols.size()];
		double[] minFunctionValues = new double[times.length];
		double[] maxFunctionValues = new double[times.length];
		for (int tIndex=0; tIndex<times.length; tIndex++){
			values[0] = times[tIndex];
			double minValue = Double.POSITIVE_INFINITY;
			double maxValue = Double.NEGATIVE_INFINITY;
			BitSet inDomainIndexBitSet = new BitSet(inDomainIndexes.size());
			inDomainIndexBitSet.set(0, inDomainIndexes.size(), true);
			for (int sample=0; sample<numSamples; sample++){
				Coordinate coord = null;
				while(true){
					int rndIndex = rand.nextInt(inDomainIndexes.size());
					int index = inDomainIndexes.get(rndIndex);
					if(inDomainIndexBitSet.get(rndIndex)){
						inDomainIndexBitSet.set(rndIndex,false);
						if(variableType.equals(VariableType.MEMBRANE)){
							coord = cartesianMesh.getCoordinateFromMembraneIndex(index);
						}else if(variableType.equals(VariableType.VOLUME)){
							coord = cartesianMesh.getCoordinateFromVolumeIndex(index);
						}else{
							throw new Exception("Not implemented "+variableType.getTypeName());
						}
						break;
					}
					System.out.println("timeIndx="+tIndex+" sample=("+sample+" of "+numSamples+") rndOODIndex=("+index+" of "+inDomainIndexBitSet.size()+")");
				}
				values[1] = coord.getX();
				values[2] = coord.getY();
				values[3] = coord.getZ();
//				values[1] = origin.getX() + rand.nextDouble()*extent.getX();
//				values[2] = origin.getY() + rand.nextDouble()*extent.getY();
//				values[3] = origin.getZ() + rand.nextDouble()*extent.getZ();
				for (int varIndex=0; varIndex<varStatistics.length; varIndex++){
					double s = rand.nextDouble();
					values[4+varIndex] = s * varStatistics[varIndex].minValuesOverTime[tIndex] +
										(1-s) * varStatistics[varIndex].maxValuesOverTime[tIndex];
				}
				double evaluation = functionExp.evaluateVector(values);
				minValue = Math.min(minValue, evaluation);
				maxValue = Math.max(maxValue, evaluation);
			}
			minFunctionValues[tIndex] = minValue;
			maxFunctionValues[tIndex] = maxValue;
		}
		FunctionStatistics functionStats = new FunctionStatistics(minFunctionValues,maxFunctionValues);
		return functionStats;
	}
	
//	public static void main(String[] args){
//		try {
//			Expression exp = new Expression("a+log(b)+c");
//			VarStatistics[] varStats = new VarStatistics[3];
//			varStats[0] = new VarStatistics("a", new double[] {1.0, 2.0, 3.0}, new double[]{1.0, 2.0, 3.0});
//			varStats[1] = new VarStatistics("b", new double[] {1.0, 2.0, 3.0}, new double[]{1.0, 2.0, 3.0});
//			varStats[2] = new VarStatistics("c", new double[] {1.0, 2.0, 3.0}, new double[]{1.0, 2.0, 3.0});
//			double[] times = new double[] {0.0, 1.0, 2.0};
//			Extent extent = new Extent(5,5,5);
//			Origin origin = new Origin(0,0,0);
//			int numSamplesPerDim = 10;
//			FunctionStatistics results = FunctionRangeGenerator.getFunctionStatistics(exp, varStats, times, extent, origin, numSamplesPerDim);
//
//			System.out.println(results.getDefaultDatasetRange().toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
}
