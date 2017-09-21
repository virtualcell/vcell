/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;

public class ParticleJumpProcess implements org.vcell.util.Matchable,java.io.Serializable {
	private String processName = null;
	private List<ParticleVariable> particles = null;
	private JumpProcessRateDefinition  rateDefinition = null;
	private List<Action> actions = null;
//	private ArrayList<ProcessParticleMapping> processParticleMappings = null;
	private ProcessSymmetryFactor processSymmetryFactor = null;
	
public static class ProcessSymmetryFactor implements Matchable, Serializable {
	double factor;
	public ProcessSymmetryFactor(double factor){
		this.factor = factor;
	}
	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof ProcessSymmetryFactor){
			ProcessSymmetryFactor other = (ProcessSymmetryFactor)obj;
			if (!Compare.isEqual(factor, other.factor)){
				return false;
			}
			return true;
		}
		return false;
	}
	public double getFactor(){
		return this.factor;
	}
}

//public abstract static class ProcessParticleMapping implements Matchable {
//		
//	public abstract String getVCML();
//		
//}
//	
//public static class ProcessParticleTypeMapping extends ProcessParticleMapping {
//	private ParticleMolecularTypePattern p1;
//	private ParticleMolecularTypePattern p2;
//
//	@Override
//	public boolean compareEqual(Matchable obj) {
//		if (obj instanceof ProcessParticleTypeMapping){
//			ProcessParticleTypeMapping other = (ProcessParticleTypeMapping)obj;
//			if (!Compare.isEqual(p1, other.p1)){
//				return false;
//			}
//			if (!Compare.isEqual(p2, other.p2)){
//				return false;
//			}
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public String getVCML() {
//		return "\t\t"+VCML.ProcessParticleTypeMapping+"\t"+p1.getMolecularType().getName()+"\t"+p2.getMolecularType().getName()+"\n";
//	}
//	
//}
//
//public static class ProcessParticleComponentMapping extends ProcessParticleMapping {
//	private ParticleMolecularTypePattern p1;
//	private ParticleMolecularComponentPattern c1;
//	
//	private ParticleMolecularTypePattern p2;
//	private ParticleMolecularComponentPattern c2;
//	
//	@Override
//	public boolean compareEqual(Matchable obj) {
//		if (obj instanceof ProcessParticleComponentMapping){
//			ProcessParticleComponentMapping other = (ProcessParticleComponentMapping)obj;
//			if (!Compare.isEqual(c1, other.c1)){
//				return false;
//			}
//			if (!Compare.isEqual(c2, other.c2)){
//				return false;
//			}
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public String getVCML() {
//		return "\t\t"+VCML.ProcessParticleComponentMapping+"\t"+c1.getc1.getMolecularComponent().getName()+"\t"+c2.getVCML()+"\n";
//	}
//	
//}

/**
 * JumpProcess constructor comment.
 * @param var cbit.vcell.math.Variable
 * @param initialExp cbit.vcell.parser.Expression
 * @param rateExp cbit.vcell.parser.Expression
 */
public ParticleJumpProcess(String name, List<ParticleVariable> particles, JumpProcessRateDefinition rateDefinition, List<Action> actions, ProcessSymmetryFactor processSymmetryFactor)
{
	processName = name;
	this.particles = particles;
	this.rateDefinition = rateDefinition;
	this.actions = actions;
	this.processSymmetryFactor = processSymmetryFactor;
}

//public void setProcessParticleMappings(ArrayList<ProcessParticleMapping> particleMappings){	
//	this.processParticleMappings = new ArrayList<ProcessParticleMapping>(particleMappings);
//}
//
//public List<ProcessParticleMapping> getProcessParticleMappings(){	
//	return processParticleMappings;
//}
//
public void setProcessParticleMappings(ProcessSymmetryFactor processSymmetryFactor){	
	this.processSymmetryFactor = processSymmetryFactor;
}

public ProcessSymmetryFactor getProcessSymmetryFactor() {	
	return this.processSymmetryFactor;
}

/**
 * Added by mfenwick
 * 
 * @return array of ParticleVariables
 */
public ParticleVariable [] getParticleVariables() {
	return this.particles.toArray(new ParticleVariable [this.particles.size()]);
}



public void remove(ParticleVariable particleVariable) {
	if (particles.contains(particleVariable)){
		particles.remove(particleVariable);
	}
	
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
	if(!Compare.isEqual(rateDefinition,jumpProc.rateDefinition)) return false; //probabilityRate
	//actions
	if((actions != null) && (jumpProc.actions != null))
	{
		if(!Compare.isEqualOrNull(actions,jumpProc.actions))
		{
			return false;
		}
//		if (!Compare.isEqualOrNull(processParticleMappings, jumpProc.processParticleMappings)){
//			return false;
//		}
		if (!Compare.isEqualOrNull(processSymmetryFactor, jumpProc.processSymmetryFactor)){
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
	return actions;
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
public JumpProcessRateDefinition getParticleRateDefinition() {
	return rateDefinition;
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
		buffer.append("\t\t"+VCML.SelectedParticle+"\t\t"+particleVar.getName()+"\n");
	}
	buffer.append("\t\t"+getParticleRateDefinition().getVCML()+";\n");
	for(Action action : actions){
		buffer.append(action.getVCML());
	}
	if (processSymmetryFactor!=null){
		buffer.append("\t\t"+VCML.ProcessSymmetryFactor+"\t\t"+this.processSymmetryFactor.getFactor()+"\n");
	}
	buffer.append("\t"+" "+VCML.EndBlock+"\n");
//	if (this.processParticleMappings != null){
//		buffer.append("\t"+" "+VCML.ProcessParticleMappings+"{\n");
//		for(ProcessParticleMapping mapping : this.processParticleMappings){
//			buffer.append(mapping.getVCML());
//		}
//		buffer.append("\t"+" "+VCML.EndBlock+"\n");
//	}
	return buffer.toString();	
}

public Expression[] getExpressions()
{
	ArrayList<Expression> expV = new ArrayList<Expression>();
	for (Expression exp : getParticleRateDefinition().getExpressions()){
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
	JumpProcessRateDefinition particleRateDef = null;
	ArrayList<Action> actions = new ArrayList<Action>();
	ProcessSymmetryFactor symmetryFactor = null;
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
			particleRateDef = new MacroscopicRateConstant(exp);
		}else if (token.equals(VCML.InteractionRadius)){
			Expression exp = MathFunctionDefinitions.fixFunctionSyntax(tokens);
			particleRateDef = new InteractionRadius(exp);
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
		} else if (token.equals(VCML.ProcessSymmetryFactor)){
			token = tokens.nextToken();
			symmetryFactor = new ProcessSymmetryFactor(Double.parseDouble(token));
		}
		token = tokens.nextToken();
	}
	ParticleJumpProcess pjp = new ParticleJumpProcess(name,particles,particleRateDef,actions,symmetryFactor);
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


public void flatten(MathSymbolTable mathSymbolTable, boolean bRoundCoefficients) throws ExpressionException, MathException {
	for (int i = 0; i < actions.size(); i++) {
		Action action = actions.get(i);
		Expression oldExp = action.getOperand();
		actions.set(i,new Action(action.getVar(),action.getOperation(),Equation.getFlattenedExpression(mathSymbolTable,oldExp,bRoundCoefficients)));
	}
	
	rateDefinition.flatten(mathSymbolTable,bRoundCoefficients);
}

}
