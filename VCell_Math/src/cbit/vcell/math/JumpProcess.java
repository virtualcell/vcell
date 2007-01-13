package cbit.vcell.math;
import java.util.*;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.IExpression;
import org.vcell.expression.SimpleSymbolTable;
import org.vcell.expression.SymbolTable;

import cbit.util.CommentStringTokenizer;
import cbit.util.Compare;
import cbit.util.document.SimulationVersion;
public class JumpProcess implements cbit.util.Matchable,java.io.Serializable {
	private String processName=null;
	private IExpression  probabilityRate=null;
	private Vector listOfActions = null;

/**
 * JumpProcess constructor comment.
 * @param var cbit.vcell.math.Variable
 * @param initialExp cbit.vcell.parser.Expression
 * @param rateExp cbit.vcell.parser.Expression
 */
public JumpProcess(String name, IExpression probRate)
{
	processName = name;
	probabilityRate = probRate;
	listOfActions = new Vector();
}


/**
 * Append a new action to the end of the action list if the variable in the action is not in the list
 * Creation date: (6/21/2006 5:13:17 PM)
 */
public void addAction(Action newAction) throws MathException
{
	if(getAction(newAction.getVar().getName())!=null)
		throw new MathException("Variable: "+newAction.getVar().getName()+" is already exists in action list in jump process"+getName());
	listOfActions.add(newAction);
}


/**
 * Compare two Jump processes.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(cbit.util.Matchable object) 
{
	if (object == null) {
		return false;
	}
	if (!(object instanceof JumpProcess)) {
		return false;
	}
	
	JumpProcess jumpProc = (JumpProcess) object;
	if(processName.compareTo(jumpProc.processName) != 0 ) return false;//processName
	if(!probabilityRate.compareEqual(jumpProc.probabilityRate)) return false; //probabilityRate
	//actions
	if((listOfActions != null) && (jumpProc.listOfActions != null))
	{
		Action actions1[] = (Action[]) listOfActions.toArray(new Action[0]);
		Action actions2[] = (Action[]) jumpProc.listOfActions.toArray(new Action[0]);

		if(!Compare.isEqualOrNull(actions1,actions2))
		{
			return false;
		}
	}
	else return false;
			
	return true;
}

/**
 * Get an action from the list by it's index.
 * Creation date: (6/27/2006 10:10:41 AM)
 * @return cbit.vcell.math.Action
 * @param index int
 */
public Action getAction(int index) 
{
	if(index<listOfActions.size())
 		return (Action)listOfActions.elementAt(index);
 	return null;
}


/**
 * Get an action from action list by it's variable name.
 * Creation date: (6/27/2006 10:12:16 AM)
 * @return cbit.vcell.math.Action
 * @param actionName java.lang.String
 */
public Action getAction(String varName) //again the problem here, do we allow same variables applear in actionlist more than once.
{
	for(int i=0; i<listOfActions.size(); i++)
	{
		if(((Action)listOfActions.elementAt(i)).getVar().getName().compareTo(varName)==0)
			return (Action)listOfActions.elementAt(i);
	}
	return null;
}


/**
 * Return the reference of the action list.
 * Creation date: (6/27/2006 3:02:29 PM)
 * @return java.util.Vector
 */
public java.util.Vector getActions() {
	return listOfActions;
}


/**
 * getDenpendent processes under this process..
 * Creation date: (6/21/2006 5:34:31 PM)
 */
public void getDependencies()
{

}


/**
 * Getthe process name.
 * Creation date: (6/21/2006 5:31:11 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return processName;
}


/**
 * Get probability expression.
 * Creation date: (6/21/2006 5:31:11 PM)
 * @return cbit.vcell.parser.Expression
 */
public IExpression getProbabilityRate() {
	return probabilityRate;
}


/**
 * Write the instance of the class to VCML.
 * @return java.lang.String
 */
public String getVCML()
{
	StringBuffer buffer = new StringBuffer();
	// the jump process will be written inside compartment brackets, therefore a "\t" is needed
	buffer.append("\t"+VCML.JumpProcess+"\t"+getName()+" "+VCML.BeginBlock+"\n");
	buffer.append("\t\t"+VCML.ProbabilityRate+"\t"+getProbabilityRate().infix()+";\n");
	for(int i=0; i<getActions().size(); i++)
	{
		buffer.append(((Action)getActions().elementAt(i)).getVCML());
	}
	buffer.append("\t"+" "+VCML.EndBlock+"\n");
	return buffer.toString();	
}


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public void read(CommentStringTokenizer tokens) throws Exception {}


/**
 * Remove the action from the action list by it's index
 * Creation date: (6/21/2006 5:14:47 PM)
 */
public void removeAction(int index)
{
	if(index<listOfActions.size())
		listOfActions.remove(index);
}


/**
 * Remove the action from action list by it's variable name
 * Creation date: (6/21/2006 5:26:56 PM)
 */
public void removeAction(String varName) //one problem, actions may have the same variable names. If wanna remove all the actions with same names
{				                         //the loop should start from biggest index.																
	for(int i=0; i<listOfActions.size(); i++)
	{
		if(((Variable)listOfActions.elementAt(i)).getName().compareTo(varName)==0)
			listOfActions.remove(i)	;
	}
	
}


/**
 * Assignment the probabilityRate to a new expression.
 * Creation date: (6/21/2006 5:32:45 PM)
 * @param newProbabilityRate cbit.vcell.parser.Expression
 */
public void setProbabilityRate(IExpression newProbabilityRate) {
	probabilityRate = newProbabilityRate;
}


/**
 * Setthe process's name.
 * Creation date: (6/21/2006 5:32:45 PM)
 * @param newProcessName java.lang.String
 */
public void setProcessName(java.lang.String newProcessName) {
	processName = newProcessName;
}

/**
 * Insert the method's description here.
 * Creation date: (10/2/2006 5:20:53 PM)
 * @return double
 * @param names java.lang.String[]
 * @param values double[]
 */
public double evaluateProbabilityRate(String[] names, double[] values) 
{
	double result = 0;
	try
	{
		SymbolTable symTable= new SimpleSymbolTable(names);
		getProbabilityRate().bindExpression(symTable);
		result = getProbabilityRate().evaluateVector(values);
	} catch (ExpressionException e) {e.printStackTrace();}

	return result;	
}


/**
 * Insert the method's description here.
 * Creation date: (9/28/2006 5:15:46 PM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer buffer = new StringBuffer();
	// the jump process will be written inside compartment brackets, therefore a "\t" is needed
	buffer.append(VCML.JumpProcess+"_"+getName());
	
	return buffer.toString();
}
}