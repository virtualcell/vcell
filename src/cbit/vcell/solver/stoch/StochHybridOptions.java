package cbit.vcell.solver.stoch;

import cbit.util.CommentStringTokenizer;
import cbit.vcell.math.VCML;
import cbit.vcell.server.DataAccessException;

public class StochHybridOptions extends StochSimOptions {

	private double epsilon = 100;
	private double lambda = 10;
	private double MSRTolerance = 1/epsilon;
	private double SDETolerance = 1e-4;
	public StochHybridOptions() {
		super();
	}

	public StochHybridOptions(boolean arg_useCustomSeed, int arg_customSeed, long arg_numOfTrials,
			double arg_epsilon, double arg_lambda, double arg_MSRTolerance, double arg_SDETolerance)
	{
		super(arg_useCustomSeed, arg_customSeed, arg_numOfTrials);
		epsilon = arg_epsilon;
		lambda = arg_lambda;
		MSRTolerance = arg_MSRTolerance;
		SDETolerance = arg_SDETolerance;
	}

	public StochHybridOptions(boolean arg_useCustomSeed, int arg_customSeed, long arg_numOfTrials)
	{
		super(arg_useCustomSeed, arg_customSeed, arg_numOfTrials);
	}

	
	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public double getMSRTolerance() {
		return MSRTolerance;
	}

	public void setMSRTolerance(double tolerance) {
		MSRTolerance = tolerance;
	}

	public double getSDETolerance() {
		return SDETolerance;
	}

	public void setSDETolerance(double tolerance) {
		SDETolerance = tolerance;
	}
	
	/**
	 * get VCML string for stochHybridOption
	 */
	public String getVCML() 
	{
		//
		// write format as follows:
		//
		//   StochSimOptions {
		//		UseCustomSeed false
		//		CustomSeed 0
		//		NumOfTrials	1
		//		Epsilon 100
		//		Lambda 10
		//		MSRTolerance 0.01
		//		SDETolerance 1e-4
		//   }
		// if useCustomSeed == false, customSeed shouldn't be compared
		// ODE/PDE applications don't need to take care of this
		//
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(VCML.StochSimOptions+" "+VCML.BeginBlock+"\n");
		
		buffer.append("   " + VCML.UseCustomSeed + " " + isUseCustomSeed() + "\n");
		buffer.append("   " + VCML.CustomSeed + " " + getCustomSeed() + "\n");
		buffer.append("   " + VCML.NumOfTrials + " " + getNumOfTrials() + "\n");
		buffer.append("   " + VCML.Epsilon + " " + getEpsilon() + "\n");
		buffer.append("   " + VCML.Lambda + " " + getLambda() + "\n");
		buffer.append("   " + VCML.MSRTolerance + " " + getMSRTolerance() + "\n");
		buffer.append("   " + VCML.SDETolerance + " " + getSDETolerance() + "\n");
		
		buffer.append(VCML.EndBlock+"\n");

		return buffer.toString();
	}
	
	/**
	 * Read VCML to feed data into the class
	 */
	public void readVCML(CommentStringTokenizer tokens) throws DataAccessException
	{
		//
		// read format as follows:
		//
		//   StochSimOptions {
		//		UseCustomSeed false
		//		CustomSeed 0
		//		NumOfTrials 1
		//		Epsilon 100
		//		Lambda 10
		//		MSRTolerance 0.01
		//		SDETolerance 1e-4
		//   }
		//
		try {
			String token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.StochSimOptions)) {
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
				if (token.equalsIgnoreCase(VCML.UseCustomSeed)) {
					token = tokens.nextToken();
					setUseCustomSeed(Boolean.parseBoolean(token));
					continue;
				}
				if (token.equalsIgnoreCase(VCML.CustomSeed)) {
					token = tokens.nextToken();
					int val1 = Integer.parseInt(token);
					if(val1 < 0)
						throw new DataAccessException("unexpected token " + token + ", seed is required to be an unsigned interger. ");
					else 
						setCustomSeed(val1);
					continue;
				}
				if (token.equalsIgnoreCase(VCML.NumOfTrials)) {
					token = tokens.nextToken();
					int val2 = Integer.parseInt(token);
					if(val2 < 1 )
						throw new DataAccessException("unexpected token " + token + ", num of trials is requied to be at least 1. ");
					else
						setNumOfTrials(val2);
					continue;
				}
				if (token.equalsIgnoreCase(VCML.Epsilon)) {
					token = tokens.nextToken();
					double val3 = Double.parseDouble(token);
					if(val3 < 1 )
						throw new DataAccessException("unexpected token " + token + ", Minimum number of molecue is requied to be greater than or equal to 1. ");
					else
						setEpsilon(val3);
					continue;
				}
				if (token.equalsIgnoreCase(VCML.Lambda)) {
					token = tokens.nextToken();
					double val4 = Double.parseDouble(token);
					if(val4 <= 0 )
						throw new DataAccessException("unexpected token " + token + ", num of trials is requied to be greater than 0. ");
					else
						setLambda(val4);
					continue;
				}
				if (token.equalsIgnoreCase(VCML.MSRTolerance)) {
					token = tokens.nextToken();
					double val5 = Double.parseDouble(token);
					if(val5 <= 0 )
						throw new DataAccessException("unexpected token " + token + ", Maximum allowed effect of slow reactions is requied to be greater than 0. ");
					else
						setMSRTolerance(val5);
					continue;
				}
				if (token.equalsIgnoreCase(VCML.SDETolerance)) {
					token = tokens.nextToken();
					double val6 = Double.parseDouble(token);
					if(val6 <= 0 )
						throw new DataAccessException("unexpected token " + token + ", SDE allowed value of drift and diffusion errors is requied to be greater than 0. ");
					else
						setSDETolerance(val6);
					continue;
				}
				throw new DataAccessException("unexpected identifier " + token);
			}
		} catch (Throwable e) {
			throw new DataAccessException(
				"line #" + (tokens.lineIndex()+1) + " Exception: " + e.getMessage()); 
		}
	}
	
	/**
	 * Checks for internal representation of objects
	 * @return boolean
	 * @param obj java.lang.Object
	 */
	public boolean compareEqual(org.vcell.util.Matchable obj) 
	{
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof StochHybridOptions) {
			StochHybridOptions hybridOpt = (StochHybridOptions) obj;
			if (isUseCustomSeed() != hybridOpt.isUseCustomSeed()) return false;
			if (isUseCustomSeed() == true)
			{
				if (getCustomSeed() != hybridOpt.getCustomSeed()) return false;
			}
			if (getNumOfTrials() != hybridOpt.getNumOfTrials()) return false;
			if (getEpsilon() != hybridOpt.getEpsilon()) return false;
			if (getLambda() != hybridOpt.getLambda()) return false;
			if (getMSRTolerance() != hybridOpt.getMSRTolerance()) return false;
			if (getSDETolerance() != hybridOpt.getSDETolerance()) return false;
			return true;
		}
		return false;
	}
	
}
