package cbit.vcell.math;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;

import java.util.*;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;

public class ParticleJumpProcess implements org.vcell.util.Matchable,java.io.Serializable {
	private String processName = null;
	private List<ParticleVariable> particles = null;
	private ParticleProbabilityRate  particleProbabilityRate=null;
	private List<Action> actions = null;

/**
 * JumpProcess constructor comment.
 * @param var cbit.vcell.math.Variable
 * @param initialExp cbit.vcell.parser.Expression
 * @param rateExp cbit.vcell.parser.Expression
 */
public ParticleJumpProcess(String name, List<ParticleVariable> particles, ParticleProbabilityRate partProbRate, List<Action> actions)
{
	processName = name;
	this.particles = particles;
	this.particleProbabilityRate = partProbRate;
	this.actions = actions;
}


/**
 * Added by mfenwick
 * 
 * @return array of ParticleVariables
 */
public ParticleVariable [] getParticleVariables() {
	return this.particles.toArray(new ParticleVariable [this.particles.size()]);
}

/**
 * Compare two Jump processes.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable object) 
{
	if (!(object instanceof ParticleJumpProcess)) {
		return false;
	}
	
	ParticleJumpProcess jumpProc = (ParticleJumpProcess) object;
	if(!Compare.isEqual(processName,jumpProc.processName)) return false;//processName
	if(!Compare.isEqual(particleProbabilityRate,jumpProc.particleProbabilityRate)) return false; //probabilityRate
	//actions
	if((actions != null) && (jumpProc.actions != null))
	{
		if(!Compare.isEqualOrNull(actions,jumpProc.actions))
		{
			return false;
		}
	}
	else return false;
			
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
	if(index<actions.size()){
 		return (Action)actions.get(index);
	}
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
	for(int i=0; i<actions.size(); i++)
	{
		if(((Action)actions.get(i)).getVar().getName().compareTo(varName)==0)
			return (Action)actions.get(i);
	}
	return null;
}


/**
 * Return the reference of the action list.
 * Creation date: (6/27/2006 3:02:29 PM)
 * @return java.util.Vector
 */
public List<Action> getActions() {
	return Collections.unmodifiableList(actions);
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
public ParticleProbabilityRate getParticleProbabilityRate() {
	return particleProbabilityRate;
}


/**
 * Write the instance of the class to VCML.
 * @return java.lang.String
 */
public String getVCML()
{
	StringBuffer buffer = new StringBuffer();
	// the jump process will be written inside compartment brackets, therefore a "\t" is needed
	buffer.append("\t"+VCML.ParticleJumpProcess+"\t"+getName()+" "+VCML.BeginBlock+"\n");
	for (ParticleVariable particleVar : particles){
		buffer.append("\t\t"+VCML.SelectedParticle+"\t"+particleVar.getName()+"\n");
	}
	buffer.append("\t\t"+getParticleProbabilityRate().getVCML()+";\n");
	for(Action action : actions){
		buffer.append(action.getVCML());
	}
	buffer.append("\t"+" "+VCML.EndBlock+"\n");
	return buffer.toString();	
}

public Expression[] getExpressions()
{
	ArrayList<Expression> expV = new ArrayList<Expression>();
	for (Expression exp : getParticleProbabilityRate().getExpressions()){
		expV.add(exp);
	}
	for(Action action : actions)
	{
		Expression operand = action.getOperand();
		if (operand != null) {
			expV.add(operand);
		}
	}
	return expV.toArray(new Expression[expV.size()]);	
}


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public static ParticleJumpProcess fromVCML(MathDescription mathDesc, CommentStringTokenizer tokens) throws MathException, ExpressionException {
	String token = tokens.nextToken();
	String name = token;
	token = tokens.nextToken();
	if (!token.equals(VCML.BeginBlock)){
		throw new MathFormatException("expecting "+VCML.BeginBlock+", found "+token);
	}
	token = tokens.nextToken();
	ArrayList<ParticleVariable> particles = new ArrayList<ParticleVariable>();
	ParticleProbabilityRate particleProbRate = null;
	ArrayList<Action> actions = new ArrayList<Action>();
	while(!token.equals(VCML.EndBlock)){
		if (token.equals(VCML.SelectedParticle)){
			token = tokens.nextToken();
			String varName = token;
			Variable var = mathDesc.getVariable(varName);
			if (var instanceof ParticleVariable){
				particles.add((ParticleVariable)var);
			}else{
				throw new MathFormatException("variable "+varName+" not a "+VCML.VolumeParticleVariable+" or "+VCML.MembraneParticleVariable);
			}
		} else if (token.equals(VCML.MacroscopicRateConstant)){
			Expression exp = MathFunctionDefinitions.fixFunctionSyntax(tokens);
			particleProbRate = new MacroscopicRateConstant(exp);
		} else if (token.equals(VCML.Action)){
			token = tokens.nextToken();
			String varName = token;
			Variable var = mathDesc.getVariable(varName);
			ParticleVariable particleVar = null;
			if (var instanceof ParticleVariable){
				particleVar = (ParticleVariable)var;
			}else{
				throw new MathFormatException("variable "+varName+" not a "+VCML.VolumeParticleVariable+" or "+VCML.MembraneParticleVariable);
			}
			token = tokens.nextToken();
			if (token.equals(VCML.CreateParticle)){
				actions.add(Action.createCreateAction(particleVar));
			}else if (token.equals(VCML.DestroyParticle)){
				actions.add(Action.createDestroyAction(particleVar));	
			}else{
				throw new MathFormatException("unexpected command "+token+" within "+VCML.ParticleJumpProcess+" "+name);
			}
		}
		token = tokens.nextToken();
	}
	ParticleJumpProcess pjp = new ParticleJumpProcess(name,particles,particleProbRate,actions);
	return pjp;
}


/**
 * Setthe process's name.
 * Creation date: (6/21/2006 5:32:45 PM)
 * @param newProcessName java.lang.String
 */
public void setName(java.lang.String newProcessName) {
	processName = newProcessName;
}


/**
 * Insert the method's description here.
 * Creation date: (9/28/2006 5:15:46 PM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer buffer = new StringBuffer();
	// the jump process will be written inside compartment brackets, therefore a "\t" is needed
	buffer.append(VCML.ParticleJumpProcess+"_"+getName());
	
	return buffer.toString();
}


public void bind(MathDescription mathDescription) throws ExpressionBindingException {
	for (Expression exp : getExpressions()) {
		exp.bindExpression(mathDescription);
	}
}
}