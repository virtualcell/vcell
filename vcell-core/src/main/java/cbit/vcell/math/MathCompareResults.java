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

import java.util.List;

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
		MathDifferent_DIFFERENT_PARTICLE_MOLECULAR_TYPES(false,"MathDifferent:DifferentParticleMolecularTypes"),
		MathDifferent_DIFFERENT_NUMBER_OF_JUMP_PROCESS(false,"MathDifferent:DifferentNumJumpProcs"),
		MathDifferent_DIFFERENT_JUMP_PROCESS(false,"MathDifferent:DifferentJumpProc"),
		MathDifferent_DIFFERENT_NUMBER_OF_PARTICLE_JUMP_PROCESS(false,"MathDifferent:DifferentNumParticleJumpProcs"),
		MathDifferent_DIFFERENT_PARTICLE_JUMP_PROCESS(false,"MathDifferent:DifferentParticleJumpProc"),
		MathDifferent_LEGACY_RATE_PARTICLE_JUMP_PROCESS(false,"MathDifferent:LegacyRateParticleJumpProc"),
		MathDifferent_LEGACY_SYMMETRY_PARTICLE_JUMP_PROCESS(false,"MathDifferent:LegacySymmetryParticleJumpProc"),
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
	
	/**
	 * One concrete way two maths differ: which object, which attribute, and the two values.
	 * <p>
	 * {@link Decision} says <em>that</em> two maths differ and broadly why; a list of these says
	 * exactly what to go and look at. Both are needed - the decision is what gets persisted (see
	 * {@link MathCompareResults#toDatabaseStatus()}), the differences are for a human or a test.
	 */
	public static class Difference {
		/** Where the difference is, e.g. {@code "molecularType 'A' / site 's0'"}. */
		public final String path;
		/** Which attribute differs, e.g. {@code "radius"}. */
		public final String attribute;
		/** Value in the first math; {@code null} means absent. */
		public final String value1;
		/** Value in the second math; {@code null} means absent. */
		public final String value2;

		public Difference(String path, String attribute, Object value1, Object value2){
			this.path = path;
			this.attribute = attribute;
			this.value1 = value1 == null ? null : value1.toString();
			this.value2 = value2 == null ? null : value2.toString();
		}

		@Override
		public String toString(){
			return path + ": " + attribute + " " + (value1 == null ? "<absent>" : "'" + value1 + "'")
					+ " vs " + (value2 == null ? "<absent>" : "'" + value2 + "'");
		}
	}

	public final Decision decision;
	public final String details;
	public final List<String> varsNotFoundMath1;
	public final List<String> varsNotFoundMath2;

	/**
	 * Every difference found, or empty. Never {@code null}.
	 * <p>
	 * Populated only where a comparison bothered to describe itself; an empty list therefore means
	 * "nothing was recorded", not "the maths are identical" - use {@link #isEquivalent()} for that.
	 */
	private final List<Difference> differences;

	public MathCompareResults(Decision decision){
		this(decision,null,null,null);
	}

	public MathCompareResults(Decision decision, String details){
		this(decision,details,null,null);
	}

	public MathCompareResults(Decision decision, String details, List<String> varsNotFoundMath1, List<String> varsNotFoundMath2){
		this(decision, details, varsNotFoundMath1, varsNotFoundMath2, null);
	}

	public MathCompareResults(Decision decision, List<Difference> differences){
		this(decision, summarize(differences), null, null, differences);
	}

	public MathCompareResults(Decision decision, String details, List<String> varsNotFoundMath1, List<String> varsNotFoundMath2, List<Difference> differences){
		this.decision = decision;
		this.details = details;
		this.varsNotFoundMath1 = varsNotFoundMath1;
		this.varsNotFoundMath2 = varsNotFoundMath2;
		this.differences = differences == null
				? java.util.Collections.<Difference>emptyList()
				: java.util.Collections.unmodifiableList(new java.util.ArrayList<>(differences));
	}

	/** Every recorded difference, in the order found. Never {@code null}. */
	public List<Difference> getDifferences(){
		return differences;
	}

	public boolean hasDifferences(){
		return !differences.isEmpty();
	}

	/**
	 * The differences one per line, for logs, tests and the math debugger. Kept out of
	 * {@link #toDatabaseStatus()}, which is written to a 255-character database column.
	 */
	public String toDifferenceReport(){
		StringBuilder buffer = new StringBuilder();
		for(Difference difference : differences){
			if(buffer.length() > 0){
				buffer.append("\n");
			}
			buffer.append(difference);
		}
		return buffer.toString();
	}

	/** A one-line summary suitable for {@code details}, which is length-limited when persisted. */
	private static String summarize(List<Difference> differences){
		if(differences == null || differences.isEmpty()){
			return null;
		}
		String first = differences.get(0).toString();
		return differences.size() == 1 ? first : first + " (+" + (differences.size() - 1) + " more)";
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

	public String toCause() {
		StringBuffer buffer = new StringBuffer();
		if (details!=null){
			buffer.append(details);
		}
		if (varsNotFoundMath1!=null && varsNotFoundMath1.size()>0){
			buffer.append(" varsNotFound1="+varsNotFoundMath1);
		}
		if (varsNotFoundMath2!=null && varsNotFoundMath2.size()>0){
			buffer.append(" varsNotFound2="+varsNotFoundMath2);
		}
		return buffer.toString();
	}



	public String toString(){
		return toDatabaseStatus();
	}
	
}
