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

public class MathCompareResults {

	public enum Decision {
		MathEquivalent_NATIVE(true,"MathEquivalent:Native"),
		MathEquivalent_FLATTENED(true,"MathEquivalent:Flattened"),
		MathEquivalent_NUMERICALLY(true,"MathEquivalent:Numerically"),
		
		MathDifferent_GEOMETRYSPEC_DIFFERENT(false,"MathDifferent:GeometrySpecDifferent"),
		MathDifferent_DIFFERENT_BC_TYPE(false,"MathsDifferent:DifferentBCType"),

		MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES(false,"MathDifferent:DifferentNumberOfVariables"),
		MathDifferent_VARIABLES_DONT_MATCH(false,"MathDifferent:VariablesDontMatch"),
		MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION(false,"MathDifferent:VariableNotFoundAsFunction"),

		MathDifferent_DIFFERENT_NUMBER_OF_SUBDOMAINS(false,"MathDifferent:DifferentNumberOfSubdomains"),
		MathDifferent_SUBDOMAINS_DONT_MATCH(false,"MathDifferent:SubdomainsDontMatch"),

		MathDifferent_EQUATION_ADDED(false,"MathDifferent:EquationAdded"),
		MathDifferent_EQUATION_REMOVED(false,"MathDifferent:EquationRemoved"),
		MathDifferent_UNKNOWN_DIFFERENCE_IN_EQUATION(false,"MathDifferent:UnknownDifferenceInEquation"),
		MathDifferent_DIFFERENT_PARTICLE_PROPERTIES(false,"MathDifferent:DifferentParticleProperties"),
		MathDifferent_DIFFERENT_NUMBER_OF_PARTICLE_JUMP_PROCESS(false,"MathDifferent:DifferentNumParticleJumpProcs"),
		MathDifferent_DIFFERENT_PARTICLE_JUMP_PROCESS(false,"MathDifferent:DifferentParticleJumpProc"),
		MathDifferent_DIFFERENT_VARIABLE_IN_EQUATION(false,"MathDifferent:DifferentVariableInEquation"),

		MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS(false,"MathDifferent:DifferentNumberOfExpressions"),
		MathDifferent_DIFFERENT_EXPRESSION(false,"MathDifferent:DifferentExpression"),
		MathDifferent_DIFFERENT_FASTRATE_EXPRESSION(false,"MathDifferent:DifferentFastRateExpression"),
		MathDifferent_DIFFERENT_FASTINV_EXPRESSION(false,"MathDifferent:DifferentFastInvExpression"),
		
		MathDifferent_UNKNOWN_DIFFERENCE_IN_MATH(false,"MathDifferent:UnknownDifference"),

		MathDifferent_FAILURE_UNKNOWN(false,"MathDifferent:FailedUnknown"),
		MathDifferent_FAILURE_FLATTENING_UNKNOWN(false,"MathDifferent:FailedFlatteningUnknown"),
		MathDifferent_FAILURE_FLATTENING_DIV_BY_ZERO(false,"MathDifferent:FailedFlatteningDivideByZero"),

		MathDifferent_NOT_SAVED(false,"MathDifferent:NotSaved"),
		MathEquivalent_SAME_MATHDESC_AS_IN_DB(true,"MathEquivalent:SameMathDescAsInDB"),
		
		MathDifferent_DIFFERENT_PostProcessingBlock(false,"MathDifferent:DifferentPostProcessingBlock"), 
		MathDifferent_DIFFERENT_VELOCITY(false,"MathDifferent:MembraneVelocity");

		public final boolean equivalent;
		public final String description;
		
		private Decision(boolean equivalent, String description){
			this.equivalent = equivalent;
			this.description = description;
		}
	}
	
	public final Decision decision;
	public final String details;
	
	public MathCompareResults(Decision decision){
		this(decision,null);
	}
	
	public MathCompareResults(Decision decision, String details){
		this.decision = decision;
		this.details = details;
	}
	
	public boolean isEquivalent(){
		return decision.equivalent;
	}
	
	public String toDatabaseStatus(){
		if (details!=null){
			return decision.description+":"+details;
		}else{
			return decision.description+":";
		}
	}
	
	public String toString(){
		return toDatabaseStatus();
	}
	
}
