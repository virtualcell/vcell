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

import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.mapping.ReactionRuleSpec.Subtype;
import cbit.vcell.mapping.ReactionRuleSpec.TransitionCondition;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;

public class LangevinParticleJumpProcess extends ParticleJumpProcess {
	private ReactionRuleSpec rrr = null;
	private Subtype subtype = null;
	private TransitionCondition transitionCondition = null;
	private double bondLength = 0;
	


/**
 * JumpProcess constructor comment.
 * @param var cbit.vcell.math.Variable
 * @param initialExp cbit.vcell.parser.Expression
 * @param rateExp cbit.vcell.parser.Expression
 */
public LangevinParticleJumpProcess(String name, List<ParticleVariable> particles, JumpProcessRateDefinition rateDefinition, List<Action> actions, ProcessSymmetryFactor processSymmetryFactor)
{
	super(name, particles, rateDefinition, actions, processSymmetryFactor);
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
	if(false) {
		return false;
	}
	return super.compareEqual(object);
}


/*
 * Write the instance of the class to VCML.
 * @return java.lang.String
 */
public String getVCML()
{
	StringBuffer buffer = new StringBuffer();
	// the jump process will be written inside compartment brackets, therefore a "\t" is needed
	buffer.append("\t"+VCML.ParticleJumpProcess+"\t"+getName()+" "+VCML.BeginBlock+"\n");
	buffer.append("\t\t" + VCML.Subtype + "\t\t\t" + subtype.columnName+"\n");
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


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public static LangevinParticleJumpProcess fromVCML(MathDescription mathDesc, CommentStringTokenizer tokens) throws MathException, ExpressionException {
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
	LangevinParticleJumpProcess pjp = new LangevinParticleJumpProcess(name,particles,particleRateDef,actions,symmetryFactor);
	return pjp;
}



public void setSubtype(Subtype subtype) {
	this.subtype = subtype;
}



public void setTransitionCondition(TransitionCondition transitionCondition) {
	this.transitionCondition = transitionCondition;
}



public void setBondLength(double bondLength) {
	this.bondLength  = bondLength;
}

}
