package cbit.vcell.math;
import java.util.*;
/**
 * Jump process is used for stochasitc simulation. A Jump process affects a list
 * of variable by conducting a list of actions on these variables. The chance for
 * the process to happen is denoted by probability rate.
 * Creation date: (6/21/2006 3:15:03 PM)
 * @author: Tracy LI
 */
public class JumpProcess implements cbit.util.Matchable,java.io.Serializable {
	private String processName=null;
	private cbit.vcell.parser.Expression  probabilityRate=null;
	private Vector listOfActions = null;

/**
 * Insert the method's description here.
 * Creation date: (6/30/2006 9:23:27 AM)
 */
public JumpProcess() 
{}


/**
 * JumpProcess constructor comment.
 * @param var cbit.vcell.math.Variable
 * @param initialExp cbit.vcell.parser.Expression
 * @param rateExp cbit.vcell.parser.Expression
 */
public JumpProcess(String name, cbit.vcell.parser.Expression probRate)
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
	if(processName != jumpProc.processName) return false;//processName
	if(!probabilityRate.compareEqual(jumpProc.probabilityRate)) return false; //probabilityRate
	for(int i=0; i<jumpProc.listOfActions.size(); i++)//actions
	{
		Action temAction=((Action)jumpProc.listOfActions.elementAt(i));
		int j=0;
		for(j=0; j<listOfActions.size(); j++)
			if(((Action)listOfActions.elementAt(j)).compareEqual(temAction)) break;
		if(j>=listOfActions.size()) return false;
	}
		
	return true;
}

/**
 * Insert the method's description here.
 * Creation date: (6/21/2006 3:15:03 PM)
 * @param sim cbit.vcell.solver.Simulation
 */
void flatten(cbit.vcell.solver.Simulation sim, boolean bRoundCoefficients) throws cbit.vcell.parser.ExpressionException, MathException {}


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
public cbit.vcell.parser.Expression getProbabilityRate() {
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
public void setProbabilityRate(cbit.vcell.parser.Expression newProbabilityRate) {
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
}