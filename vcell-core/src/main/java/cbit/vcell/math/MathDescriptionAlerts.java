package cbit.vcell.math;

import org.vcell.util.Issue;
import org.vcell.util.IssueContext;

/**
 * Validation messages produced by {@link MathDescription}.
 * <br/><br/>
 * <p>
 * These describe math constructs - subdomains, equations, variables - and are phrased in terms of
 * {@link VCML} tokens, so they belong to the math namespace. They previously lived on
 * {@code cbit.vcell.model.common.VCellErrorMessages}, which forced {@code MathDescription} to
 * import from the biological layer purely to describe its own validation failures. Nothing outside
 * {@code MathDescription} referenced them.
 */
public class MathDescriptionAlerts {

	private MathDescriptionAlerts() {}

	private final static String PLACE_HOLDER = "__PLACE_HOLDER__";

	public interface Alert {
		Issue.IssueCategory category();
		Issue.Severity severity();
		String message();

		default Issue generateIssue(Issue.IssueSource issueSource, IssueContext issueContext, Object... fillInValues){
			return new Issue(issueSource, issueContext, this.category(), MathDescriptionAlerts.format(this.message(), fillInValues), this.severity());
		}
	}

	public enum Geometry implements Alert {
		NOT_DEFINED(
				Issue.IssueCategory.MathDescription_NoGeometry,
				Issue.Severity.ERROR,
				"no geometry defined"
		),
		NO_REGION_INFO(
				Issue.IssueCategory.MathDescription_SpatialModel_Geometry,
				Issue.Severity.ERROR,
				"unable to get region information from geometry"
		),
		NO_MATCH(
				Issue.IssueCategory.MathDescription_SpatialModel_Geometry,
				Issue.Severity.ERROR,
				"Geometry '" + PLACE_HOLDER + "' SubDomain name '" + PLACE_HOLDER +"' does not match any "+VCML.CompartmentSubDomain+" name in "+VCML.MathDescription+" -- check spelling/case"
		),
		LACK_OF_MEMBRANE(
				Issue.IssueCategory.MathDescription_SpatialModel_Geometry,
				Issue.Severity.ERROR,
				"There should be a MembraneSubDomain between compartments '"+PLACE_HOLDER+"' and '"+PLACE_HOLDER+"'"
		),
		AWOL_MEMBRANE(
				Issue.IssueCategory.MathDescription_SpatialModel_Geometry,
				Issue.Severity.ERROR,
				"MembraneSubDomain inside='"+PLACE_HOLDER+"', outside='"+PLACE_HOLDER+"' is not in geometry"
		),
		;

		private final Issue.IssueCategory ISSUE_CATEGORY;
		private final Issue.Severity SEVERITY;
		private final String MESSAGE;

		Geometry(Issue.IssueCategory ISSUE_CATEGORY, Issue.Severity SEVERITY, String MESSAGE) {
			this.ISSUE_CATEGORY = ISSUE_CATEGORY;
			this.SEVERITY = SEVERITY;
			this.MESSAGE = MESSAGE;
		}

		@Override
		public Issue.IssueCategory category() {
			return this.ISSUE_CATEGORY;
		}

		@Override
		public Issue.Severity severity() {
			return this.SEVERITY;
		}

		@Override
		public String message() {
			return this.MESSAGE;
		}
	}

	public enum Constant implements Alert {
		EVALUATION_FAILURE(
				Issue.IssueCategory.MathDescription_Constant_NotANumber,
				Issue.Severity.ERROR,
				"Constant cannot be evaluated to a number: " + PLACE_HOLDER
		),
		;

		private final Issue.IssueCategory ISSUE_CATEGORY;
		private final Issue.Severity SEVERITY;
		private final String MESSAGE;

		Constant(Issue.IssueCategory ISSUE_CATEGORY, Issue.Severity SEVERITY, String MESSAGE) {
			this.ISSUE_CATEGORY = ISSUE_CATEGORY;
			this.SEVERITY = SEVERITY;
			this.MESSAGE = MESSAGE;
		}

		@Override
		public Issue.IssueCategory category() {
			return this.ISSUE_CATEGORY;
		}

		@Override
		public Issue.Severity severity() {
			return this.SEVERITY;
		}

		@Override
		public String message() {
			return this.MESSAGE;
		}
	}

	/**
	 * Non-Spatial Model Alerts
	 */
	public enum Compartmental implements Alert {
		SINGLE_COMPARTMENT_VIOLATION(
				Issue.IssueCategory.MathDescription_CompartmentalModel,
				Issue.Severity.ERROR,
				"Compartmental Model requires exactly one "+ VCML.CompartmentSubDomain
		),
		NON_COMPARTMENT_SUBDOMAIN_VIOLATION(
				Issue.IssueCategory.MathDescription_CompartmentalModel,
				Issue.Severity.ERROR,
				"Compartmental Model requires the subdomain be a " + VCML.CompartmentSubDomain
		),
		UNKNOWN_EQUATION_TYPE(
				Issue.IssueCategory.MathDescription_CompartmentalModel,
				Issue.Severity.ERROR,
				"Unexpected equation of type " + VCML.PdeEquation + ", must include only " + VCML.OdeEquation
		),
		ODE_EQUATION_REQUIRED(
				Issue.IssueCategory.MathDescription_CompartmentalModel,
				Issue.Severity.WARNING,
				"Expecting at least one " + VCML.OdeEquation
		),
		UNMATCHED_VOL_VARIABLE(
				Issue.IssueCategory.MathDescription_CompartmentalModel,
				Issue.Severity.ERROR,
				"Model must declare an " + VCML.OdeEquation + " for each " + VCML.VolumeVariable
		),
		DECLARATION_FORBIDDEN(
				Issue.IssueCategory.MathDescription_CompartmentalModel,
				Issue.Severity.ERROR,
				"Model must not declare any " + PLACE_HOLDER
		),
		;

		private final Issue.IssueCategory ISSUE_CATEGORY;
		private final Issue.Severity SEVERITY;
		private final String MESSAGE;

		Compartmental(Issue.IssueCategory ISSUE_CATEGORY, Issue.Severity SEVERITY, String MESSAGE) {
			this.ISSUE_CATEGORY = ISSUE_CATEGORY;
			this.SEVERITY = SEVERITY;
			this.MESSAGE = MESSAGE;
		}

		@Override
		public Issue.IssueCategory category() {
			return this.ISSUE_CATEGORY;
		}

		@Override
		public Issue.Severity severity() {
			return this.SEVERITY;
		}

		@Override
		public String message() {
			return this.MESSAGE;
		}
	}

	/**
	 * Spatial Model Alerts
	 */
	public enum Spatial implements Alert {
		MYSTERIOUS_SUBDOMAIN_DETECTED(
				Issue.IssueCategory.MathDescription_SpatialModel_Subdomain,
				Issue.Severity.ERROR,
				"Can't find a matching geometry subdomain for math subdomain '" + PLACE_HOLDER + "'. Math subdomain names must match geometry subdomain names ."
		),
		UNKNOWN_DOMAIN_TYPE(
				Issue.IssueCategory.MathDescription_SpatialModel_Subdomain,
				Issue.Severity.ERROR,
				"Unexpected subdomain type for subdomain " + PLACE_HOLDER
		),
		GEOMETRY_DOMAIN_NUMBER_MISMATCH(
				Issue.IssueCategory.MathDescription_SpatialModel,
				Issue.Severity.ERROR,
				"There are " + PLACE_HOLDER + " subdomains in geometry, but "+PLACE_HOLDER+" "+VCML.CompartmentSubDomain+"s in math description. They must match."
		),
		FILAMENT_DOMAIN_NEEDED(
				Issue.IssueCategory.MathDescription_SpatialModel,
				Issue.Severity.ERROR,
				"There are no " + VCML.FilamentSubDomain + "s defined, cannot define " + VCML.FilamentVariable + " or " + VCML.FilamentRegionVariable
		),
		MEMBRANE_DOMAIN_NEEDED(
				Issue.IssueCategory.MathDescription_SpatialModel,
				Issue.Severity.ERROR,
				"There are no " + VCML.MembraneSubDomain + "s defined, cannot define " + VCML.MembraneVariable + " or " + VCML.MembraneRegionVariable
		),
		DUPLICATE_SUBDOMAINS_FORBIDDEN(
				Issue.IssueCategory.MathDescription_SpatialModel_Subdomain,
				Issue.Severity.ERROR,
				"Duplicate subDomains " + PLACE_HOLDER + " and " + PLACE_HOLDER
		),
		DUPLICATE_INBETWEEN_DOMAINS_FORBIDDEN(
				Issue.IssueCategory.MathDescription_SpatialModel_Subdomain,
				Issue.Severity.ERROR,
				"Duplicate membrane subdomains between compartments " + PLACE_HOLDER+" and " + PLACE_HOLDER
		),
		NON_UNIQUE_PRIORITIES_FORBIDDEN(
				Issue.IssueCategory.MathDescription_SpatialModel_Subdomain,
				Issue.Severity.ERROR,
				"CompartmentSubDomain priorities must be unique see '" + PLACE_HOLDER + "' and '" + PLACE_HOLDER + "'"
		),
		PERIODIC_BOUNDARIES_MISSING(
				Issue.IssueCategory.MathDescription_SpatialModel_Subdomain,
				Issue.Severity.ERROR,
				PLACE_HOLDER + " and " + PLACE_HOLDER + " must both have periodic boundary condition for math subdomain '" + PLACE_HOLDER + "'"
		),
		MISSING_BOUNDARY_CONDITION(
				Issue.IssueCategory.MathDescription_SpatialModel_Equation,
				Issue.Severity.ERROR,
				"PDE for '" + PLACE_HOLDER + "' in '" + PLACE_HOLDER + "' must have a boundary condition at membrane '" + PLACE_HOLDER + "'. Specify EITHER BoundaryValue in the PDE OR a JumpCondition on the membrane."
		),
		UNMATCHED_BOUNDARY_CONDITION(
				Issue.IssueCategory.MathDescription_SpatialModel_Equation,
				Issue.Severity.ERROR,
				"PDE for '" + PLACE_HOLDER + "' in '" + PLACE_HOLDER + "' has a boundary value for membrane '" + PLACE_HOLDER + "'. It requires \"Boundary " + PLACE_HOLDER + " [Flux OR Value]\" in '" + PLACE_HOLDER + "'."
		),
		ORPHANED_JUMP_CONDITION(
				Issue.IssueCategory.MathDescription_SpatialModel_Equation,
				Issue.Severity.ERROR,
				"Jump condition for '" + PLACE_HOLDER + "' on membrane '" + PLACE_HOLDER+"' has no matching PDE in either '" + PLACE_HOLDER+"' or '" + PLACE_HOLDER + "'"
		),
		AMBIGUOUS_INWARD_FLUX(
				Issue.IssueCategory.MathDescription_SpatialModel_Equation,
				Issue.Severity.ERROR,
				"Jump condition for '" + PLACE_HOLDER + "' on membrane '" + PLACE_HOLDER+"' has a non-zero inward flux but no PDE in compartment '" + PLACE_HOLDER + "'"
		),
		AMBIGUOUS_OUTWARD_FLUX(
				Issue.IssueCategory.MathDescription_SpatialModel_Equation,
				Issue.Severity.ERROR,
				"Jump condition for '" + PLACE_HOLDER + "' on membrane '" + PLACE_HOLDER + "' has a non-zero outward flux but no PDE in compartment '" + PLACE_HOLDER + "'"
		),
		VOLUME_MIXED_EQUATIONS_FORBIDDEN(
				Issue.IssueCategory.MathDescription_SpatialModel,
				Issue.Severity.ERROR,
				"Cannot mix " + VCML.OdeEquation + " and " + VCML.PdeEquation +" for volume variable '"+PLACE_HOLDER+ "'"
		),
		MEMBRANE_MIXED_EQUATIONS_FORBIDDEN(
				Issue.IssueCategory.MathDescription_SpatialModel_Variable,
				Issue.Severity.ERROR,
				"Cannot mix " + VCML.OdeEquation + " and " + VCML.PdeEquation +" for membrane variable '"+PLACE_HOLDER+ "'"
		),
		VOLUME_PDE_MIXED_STABILITY_FORBIDDEN(
				Issue.IssueCategory.MathDescription_SpatialModel,
				Issue.Severity.ERROR,
				"Cannot mix " + VCML.Steady + " and " + VCML.Unsteady + " " + VCML.PdeEquation + "s for variable '" +PLACE_HOLDER+ "'"
		),
		MEMBRANE_PDE_MIXED_STABILITY_FORBIDDEN(
				Issue.IssueCategory.MathDescription_SpatialModel_Variable,
				Issue.Severity.ERROR,
				"Cannot mix " + VCML.Steady + " and " + VCML.Unsteady + " " + VCML.PdeEquation + "s for variable '" +PLACE_HOLDER+ "'"
		),
		VOLUME_MISSING_CORRESPONDING_EQUATION(
				Issue.IssueCategory.MathDescription_SpatialModel,
				Issue.Severity.ERROR,
				"There is neither a " + VCML.PdeEquation + " nor an " + VCML.OdeEquation+" for variable '"+PLACE_HOLDER + "'"
		),
		MEMBRANE_MISSING_CORRESPONDING_EQUATION(
				Issue.IssueCategory.MathDescription_SpatialModel_Variable,
				Issue.Severity.ERROR,
				"There is neither a " + VCML.PdeEquation + " nor an " + VCML.OdeEquation+" for variable '"+PLACE_HOLDER + "'"
		),
		MISSING_JUMP_CONDITION(
				Issue.IssueCategory.MathDescription_SpatialModel_Variable,
				Issue.Severity.ERROR,
				"VolumeRegionEquation for '" + PLACE_HOLDER + "' in '" + PLACE_HOLDER+"' does not have a jump condition on membrane '" + PLACE_HOLDER + "'"
		),
		VOLUME_REGION_EQUATION_MISSING(
				Issue.IssueCategory.MathDescription_SpatialModel_Variable,
				Issue.Severity.ERROR,
				"There should be at least one " + VCML.VolumeRegionEquation+" defined for volume region variable '" + PLACE_HOLDER + "'"
		),
		MEMBRANE_REGION_EQUATION_MISSING(
				Issue.IssueCategory.MathDescription_SpatialModel_Variable,
				Issue.Severity.ERROR,
				"There should be at least one " + VCML.MembraneRegionEquation+" defined for membrane region variable '" + PLACE_HOLDER + "'"
		),
		FILAMENT_REGION_EQUATION_MISSING(
				Issue.IssueCategory.MathDescription_SpatialModel_Variable,
				Issue.Severity.ERROR,
				"There should be a " + VCML.FilamentRegionEquation + " defined for filament region variable "+ PLACE_HOLDER + " in " + VCML.FilamentSubDomain + " " + PLACE_HOLDER
		),
		ODE_EQUATION_MISSING(
				Issue.IssueCategory.MathDescription_SpatialModel_Variable,
				Issue.Severity.ERROR,
				"There should be a "+ VCML.OdeEquation+" defined for variable " + PLACE_HOLDER + " for " + VCML.FilamentSubDomain + " " + PLACE_HOLDER
		),
		EVENTS_FORBIDDEN(
				Issue.IssueCategory.MathDescription_SpatialModel,
				Issue.Severity.ERROR,
				"Events are not supported in spatial models."
		),
		;

		private final Issue.IssueCategory ISSUE_CATEGORY;
		private final Issue.Severity SEVERITY;
		private final String MESSAGE;

		Spatial(Issue.IssueCategory ISSUE_CATEGORY, Issue.Severity SEVERITY, String MESSAGE) {
			this.ISSUE_CATEGORY = ISSUE_CATEGORY;
			this.SEVERITY = SEVERITY;
			this.MESSAGE = MESSAGE;
		}

		@Override
		public Issue.IssueCategory category() {
			return this.ISSUE_CATEGORY;
		}

		@Override
		public Issue.Severity severity() {
			return this.SEVERITY;
		}

		@Override
		public String message() {
			return this.MESSAGE;
		}
	}

	/**
	 * non-spatial stochastic
	 */
	public enum CompartmentalStochastic implements Alert {
		STOCHASTIC_VARIABLE_NEEDED(
				Issue.IssueCategory.MathDescription_StochasticModel,
				Issue.Severity.ERROR,
				"stochastic model requires at least one stochastic volume variable"
		),
		JUMP_PROCESS_NEEDED(
				Issue.IssueCategory.MathDescription_StochasticModel,
				Issue.Severity.ERROR,
				"stochastic model requires at least one jump process"
		),
		;

		private final Issue.IssueCategory ISSUE_CATEGORY;
		private final Issue.Severity SEVERITY;
		private final String MESSAGE;

		CompartmentalStochastic(Issue.IssueCategory ISSUE_CATEGORY, Issue.Severity SEVERITY, String MESSAGE) {
			this.ISSUE_CATEGORY = ISSUE_CATEGORY;
			this.SEVERITY = SEVERITY;
			this.MESSAGE = MESSAGE;
		}

		@Override
		public Issue.IssueCategory category() {
			return this.ISSUE_CATEGORY;
		}

		@Override
		public Issue.Severity severity() {
			return this.SEVERITY;
		}

		@Override
		public String message() {
			return this.MESSAGE;
		}
	}

	/**
	 * Substitute {@code objects} into the placeholders of {@code baseErrorMessage}, in order.
	 * <p>
	 * Behaviourally identical to {@code VCellErrorMessages.getErrorMessage}, including throwing when
	 * there are fewer arguments than placeholders. Duplicated rather than imported so the math
	 * namespace does not depend on the biological one to format its own messages.
	 */
	public static String format(String baseErrorMessage, Object... objects) {
		if (!baseErrorMessage.contains(PLACE_HOLDER) && objects.length == 0) return baseErrorMessage;
		String returnMessage = baseErrorMessage;
		int counter;
		for (counter = 0; returnMessage.contains(PLACE_HOLDER) && counter < objects.length; counter++) {
			returnMessage = returnMessage.replaceFirst(PLACE_HOLDER, objects[counter].toString());
		}
		if (counter == objects.length && returnMessage.contains(PLACE_HOLDER)) throw new RuntimeException("Not enough arguments for place holders");
		else if (counter < objects.length) throw new RuntimeException("Too many arguments compared to expected place holders");
		return returnMessage;
	}

	public static String format(Alert alert, Object... objects) {
		return MathDescriptionAlerts.format(alert.message(), objects);
	}
}
