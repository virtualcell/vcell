package cbit.vcell.solver.stoch;
import org.vcell.util.CommentStringTokenizer;

import cbit.vcell.server.DataAccessException;
/**
 * This class is used to store some stochastic simulation
 * required information, including  use custom seed or not,
 * custom seed and number of trials. This data structure is
 * stored in SolverTaskDescription.
 * Creation date: (12/6/2006 9:52:07 AM)
 * @author: Tracy LI
 * @version: 1.0 Beta
 */
import cbit.vcell.math.*;

public class StochSimOptions implements java.io.Serializable, org.vcell.util.Matchable {
	private boolean useCustomSeed = false;
	private int customSeed = 0;
	private long numOfTrials = 1;

/**
 * StochSimOptions constructor comment.
 * @param status int
 */
public StochSimOptions()
{
	useCustomSeed = false;
	customSeed = 0;
	numOfTrials = 1;
}


/**
 * StochSimOptions constructor comment.
 * @param status int
 * @param message java.lang.String
 */
public StochSimOptions(boolean arg_useCustomSeed, int arg_customSeed, long arg_numOfTrials) 
{
	useCustomSeed = arg_useCustomSeed;
	customSeed = arg_customSeed;
	numOfTrials = arg_numOfTrials;
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
	if (obj != null && obj instanceof StochSimOptions) {
		StochSimOptions stochOpt = (StochSimOptions) obj;
		if (isUseCustomSeed() != stochOpt.isUseCustomSeed()) return false;
		if (isUseCustomSeed() == true)
		{
			if (getCustomSeed() != stochOpt.getCustomSeed()) return false;
		}
		if (getNumOfTrials() != stochOpt.getNumOfTrials()) return false;
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2006 10:51:09 AM)
 * @return int
 */
public int getCustomSeed() {
	return customSeed;
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2006 10:51:09 AM)
 * @return long
 */
public long getNumOfTrials() {
	return numOfTrials;
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2006 11:32:10 AM)
 * @return java.lang.String
 */
public String getVCML() 
{
	//
	// write format as follows:
	//
	//   StochSimOptions {
	//		UseCustomSeed	false
	//		CustomSeed	0
	//		NumOfTrials	1
	//   }
	// if useCustomSeed == false, customSeed wont be compared
	// ODE/PDE applications don't need to take care of this
	//
	StringBuffer buffer = new StringBuffer();
	
	buffer.append(VCML.StochSimOptions+" "+VCML.BeginBlock+"\n");
	
	buffer.append("   " + VCML.UseCustomSeed + " " + isUseCustomSeed() + "\n");
	buffer.append("   " + VCML.CustomSeed + " " + getCustomSeed() + "\n");
	buffer.append("   " + VCML.NumOfTrials + " " + getNumOfTrials() + "\n");

	buffer.append(VCML.EndBlock+"\n");

	return buffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2006 10:51:09 AM)
 * @return boolean
 */
public boolean isUseCustomSeed() {
	return useCustomSeed;
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2006 1:03:54 PM)
 */
public void readVCML(CommentStringTokenizer tokens) throws DataAccessException
{
	//
	// read format as follows:
	//
	//   StochSimOptions {
	//		UseCustomSeed		false
	//		CustomSeed		0
	//		NumOfTrials		1
	//   }
	//
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
			throw new DataAccessException("unexpected identifier " + token);
		}
	} catch (Throwable e) {
		throw new DataAccessException(
			"line #" + (tokens.lineIndex()+1) + " Exception: " + e.getMessage()); 
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2006 10:51:09 AM)
 * @param newCustomSeed int
 */
public void setCustomSeed(int newCustomSeed) {
	customSeed = newCustomSeed;
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2006 10:51:09 AM)
 * @param newNumOfTrials long
 */
public void setNumOfTrials(long newNumOfTrials) {
	numOfTrials = newNumOfTrials;
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2006 10:51:09 AM)
 * @param newUseCustomSeed boolean
 */
public void setUseCustomSeed(boolean newUseCustomSeed) {
	useCustomSeed = newUseCustomSeed;
}
}