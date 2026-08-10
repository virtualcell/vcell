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
import cbit.vcell.parser.ExpressionUtils;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;

public class LangevinParticleJumpProcess extends ParticleJumpProcess {

	/**
	 * Math-namespace equivalent of the biological {@code ReactionRuleSpec.Subtype}.
	 * <p>
	 * Duplicated rather than imported, following {@code ParticleMolecularComponentPattern.ParticleBondType}:
	 * a math description must be describable without reference to the application that generated it.
	 * {@code columnName} is the VCML wire name and must not change.
	 */
	public enum ParticleSubtype {
		INCOMPATIBLE("Not Compatible"),
		CREATION("Creation"),
		DECAY("Decay"),
		TRANSITION("Transition"),
		ALLOSTERIC("Allosteric"),
		BINDING("Binding");

		final public String columnName;
		private ParticleSubtype(String columnName) {
			this.columnName = columnName;
		}
		public static ParticleSubtype fromName(String nameCandidate) {
			for(ParticleSubtype st : ParticleSubtype.values()) {
				if(st.columnName.equals(nameCandidate)) {
					return st;
				}
			}
			return null;
		}
	}

	/**
	 * Math-namespace equivalent of the biological {@code ReactionRuleSpec.TransitionCondition}.
	 * <p>
	 * {@code vcellName} is the VCML wire name; {@code lngvName} is what the Langevin solver input uses.
	 * Neither may change. The terminology is genuinely confusing across layers - see the biological
	 * {@code MolecularComponentPattern.BondType} comments for the correspondence.
	 */
	public enum ParticleTransitionCondition {
		NONE("Any", "None"),
		FREE("Unbound", "Free"),
		BOUND("Bound", "Bound");

		final public String vcellName;
		final public String lngvName;
		private ParticleTransitionCondition(String vcellName, String lngvName) {
			this.vcellName = vcellName;
			this.lngvName = lngvName;
		}
		public static ParticleTransitionCondition fromVcellName(String nameCandidate) {
			for(ParticleTransitionCondition tc : ParticleTransitionCondition.values()) {
				if(tc.vcellName.equals(nameCandidate)) {
					return tc;
				}
			}
			return null;
		}
		public static ParticleTransitionCondition fromLngvName(String nameCandidate) {
			for(ParticleTransitionCondition tc : ParticleTransitionCondition.values()) {
				if(tc.lngvName.equals(nameCandidate)) {
					return tc;
				}
			}
			return null;
		}
	}

	private ParticleSubtype subtype = null;
	private ParticleTransitionCondition transitionCondition = null;
	/**
	 * Bond length for BINDING reactions.
	 * <p>
	 * An {@link Expression} rather than a double because every scalar quantity in a math description
	 * is an expression, even where math generation only ever produces a literal today - it may later
	 * be linked to a {@link Constant} symbol, and comparison then follows the same functional
	 * equivalence rules as the rest of the math layer rather than exact bit equality.
	 */
	private Expression bondLength = new Expression(0.0);
	


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
	// exact class, not instanceof: the superclass accepts any ParticleJumpProcess, so an instanceof
	// test here would make comparison depend on which side it is called from.
	if (object == null || !getClass().equals(object.getClass())) {
		return false;
	}
	LangevinParticleJumpProcess other = (LangevinParticleJumpProcess)object;
	if(subtype != other.subtype) {
		return false;
	}
	if(transitionCondition != other.transitionCondition) {	// null unless the subtype is TRANSITION
		return false;
	}
	// Exact comparison: this is the "identical" tier. Tolerance belongs to the equivalence tier,
	// as it does for expressions (ExpressionUtils.functionallyEquivalent).
	if(!Compare.isEqualOrNull(bondLength, other.bondLength, new ExpressionUtils.ExpressionEquivalencePredicate())) {
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
	buffer.append("\t"+VCML.LangevinParticleJumpProcess+"\t"+getName()+" "+VCML.BeginBlock+"\n");
	buffer.append("\t\t" + VCML.Subtype + "\t\t\t" + subtype.columnName+"\n");
	if(ParticleSubtype.TRANSITION == subtype) {
		buffer.append("\t\t" + VCML.TransitionCondition + "\t\t" + transitionCondition.vcellName + "\n");
	} else {
		buffer.append("\t\t" + VCML.TransitionCondition + "\t\t" + " - " + "\n");
	}
	if(ParticleSubtype.BINDING == subtype) {
		buffer.append("\t\t" + VCML.BondLength + "\t\t\t" + bondLength.infix() + "\n");
	} else {
		buffer.append("\t\t" + VCML.BondLength + "\t\t\t" + " - " + "\n");
	}
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
	
	ParticleSubtype subtype = null;
	ParticleTransitionCondition transitionCondition = null;		// may be null if ParticleSubtype is not TRANSITION
	Expression bondLength = null;
	ArrayList<ParticleVariable> particles = new ArrayList<ParticleVariable>();
	JumpProcessRateDefinition particleRateDef = null;
	ArrayList<Action> actions = new ArrayList<Action>();
	ProcessSymmetryFactor symmetryFactor = null;
	
	token = tokens.nextToken();
	while(!token.equals(VCML.EndBlock)) {
		if(token.equals(VCML.Subtype)) {
			token = tokens.nextToken();
			String subtypeName = token;
			subtype = ParticleSubtype.fromName(subtypeName);
			if(subtype == null) {
				throw new IllegalArgumentException("Invalid ParticleSubtype: " + subtypeName);
			}
		} else if(token.equals(VCML.TransitionCondition)) {
			if(ParticleSubtype.TRANSITION == subtype) {
				token = tokens.nextToken();
				String transitionConditionName = token;
				transitionCondition = ParticleTransitionCondition.fromVcellName(transitionConditionName);
				if(subtype == null) {
					throw new IllegalArgumentException("Invalid ParticleTransitionCondition: " + transitionConditionName);
				}
			}
		} else if(token.equals(VCML.BondLength)) {
			token = tokens.nextToken();
			// only BINDING reactions carry a bond length; the writer emits '-' for the rest, so guard on the
			// subtype rather than parsing and swallowing the failure - a genuinely malformed value should be
			// reported, not silently ignored.
			if(ParticleSubtype.BINDING == subtype) {
				bondLength = new Expression(token);
			}
		} else if (token.equals(VCML.SelectedParticle)){
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
	pjp.setSubtype(subtype);
	pjp.setTransitionCondition(transitionCondition);
	pjp.setBondLength(bondLength);
	return pjp;
}

public void setSubtype(ParticleSubtype subtype) {
	this.subtype = subtype;
}
public ParticleSubtype getSubtype() {
	return subtype;
}
public void setTransitionCondition(ParticleTransitionCondition transitionCondition) {
	this.transitionCondition = transitionCondition;
}
public ParticleTransitionCondition getTransitionCondition() {
	return transitionCondition;
}
public void setBondLength(Expression bondLength) {
	this.bondLength  = bondLength;
}
public Expression getBondLength() {
	return bondLength;
}

public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append(VCML.LangevinParticleJumpProcess+"_"+getName());
	
	return buffer.toString();
}

}
