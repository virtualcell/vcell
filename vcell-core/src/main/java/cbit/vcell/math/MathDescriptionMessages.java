package cbit.vcell.math;

/**
 * Validation messages produced by {@link MathDescription}.
 * <p>
 * These describe math constructs - subdomains, equations, variables - and are phrased in terms of
 * {@link VCML} tokens, so they belong to the math namespace. They previously lived on
 * {@code cbit.vcell.model.common.VCellErrorMessages}, which forced {@code MathDescription} to
 * import from the biological layer purely to describe its own validation failures. Nothing outside
 * {@code MathDescription} referenced them.
 */
public class MathDescriptionMessages {

	private MathDescriptionMessages() {
	}

	private final static String PLACE_HOLDER = "__PLACE_HOLDER__";

	public final static String MATH_DESCRIPTION_GEOMETRY_1 = "no geometry defined";
	public final static String MATH_DESCRIPTION_GEOMETRY_2 = "unable to get region information from geometry";
	public final static String MATH_DESCRIPTION_GEOMETRY_3 = "Geometry '" + PLACE_HOLDER + "' SubDomain name '" + PLACE_HOLDER +"' does not match any "+VCML.CompartmentSubDomain+" name in "+VCML.MathDescription+" -- check spelling/case";
	public final static String MATH_DESCRIPTION_GEOMETRY_4 = "There should be a MembraneSubDomain between compartments '"+PLACE_HOLDER+"' and '"+PLACE_HOLDER+"'";
	public final static String MATH_DESCRIPTION_GEOMETRY_5 = "MembraneSubDomain inside='"+PLACE_HOLDER+"', outside='"+PLACE_HOLDER+"' is not in geometry";
	
	public final static String MATH_DESCRIPTION_CONSTANT = "Constant cannot be evaluated to a number: " + PLACE_HOLDER;	
	
	// non spatial model
	public final static String MATH_DESCRIPTION_COMPARTMENT_MODEL_1 = "Compartmental Model requires exactly one "+ VCML.CompartmentSubDomain;
	public final static String MATH_DESCRIPTION_COMPARTMENT_MODEL_2 = "Compartmental Model requires the subdomain be a " + VCML.CompartmentSubDomain;
	public final static String MATH_DESCRIPTION_COMPARTMENT_MODEL_3 = "Compartmental model, unexpected equation of type " + VCML.PdeEquation + ", must include only " + VCML.OdeEquation;
	public final static String MATH_DESCRIPTION_COMPARTMENT_MODEL_4 = "Compartmental model, expecting at least one " + VCML.OdeEquation;
	public final static String MATH_DESCRIPTION_COMPARTMENT_MODEL_5 = "Compartmental model, must declare an " + VCML.OdeEquation + " for each " + VCML.VolumeVariable;
	public final static String MATH_DESCRIPTION_COMPARTMENT_MODEL_6 = "Compartmental model, must not declare any " + PLACE_HOLDER;
	
	// spatial model
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_1 = "Spatial model, can't find a matching geometry subdomain for math subdomain '" + PLACE_HOLDER + "'. Math subdomain names must match geometry subdomain names .";	
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_2 = "Spatial model, unexpected subdomain type for subdomain " + PLACE_HOLDER;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_3 = "Spatial model, there are " + PLACE_HOLDER + " subdomains in geometry, but "+PLACE_HOLDER+" "+VCML.CompartmentSubDomain+"s in math description. They must match.";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_4 = "Spatial model, there are no " + VCML.FilamentSubDomain + "s defined, cannot define "+VCML.FilamentVariable + " or " + VCML.FilamentRegionVariable;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_5 = "Spatial model, there are no " + VCML.MembraneSubDomain + "s defined, cannot define " + VCML.MembraneVariable + " or " + VCML.MembraneRegionVariable;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_6 = "Duplicate subDomains "+PLACE_HOLDER+" and "+PLACE_HOLDER;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_7 = "Duplicate membrane subdomains between compartments "+PLACE_HOLDER+" and "+PLACE_HOLDER;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_8 = "CompartmentSubDomain priorities must be unique see '"+PLACE_HOLDER+"' and '"+PLACE_HOLDER+"'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_9 = PLACE_HOLDER + " and " + PLACE_HOLDER + " must both have periodic boundary condition for math subdomain '" + PLACE_HOLDER + "'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_10 = "Spatial Model, PDE for '" + PLACE_HOLDER + "' in '"+PLACE_HOLDER + "' must have a boundary condition at membrane '"+ PLACE_HOLDER + "'. Specify EITHER BoundaryValue in the PDE OR a JumpCondition on the membrane.";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_10A = "Spatial Model, PDE for '" + PLACE_HOLDER + "' in '"+PLACE_HOLDER + "' has a boundary value for membrane '"+ PLACE_HOLDER + "'. It requires \"Boundary " + PLACE_HOLDER + " [Flux OR Value]\" in '" + PLACE_HOLDER + "'.";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_11 = "Spatial Model, Jump condition for '" + PLACE_HOLDER + "' on membrane '" + PLACE_HOLDER+"' has no matching PDE in either '"+PLACE_HOLDER+"' or '"+PLACE_HOLDER+"'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_12 = "Spatial Model, Jump condition for '" + PLACE_HOLDER + "' on membrane '" + PLACE_HOLDER+"' has a non-zero inward flux but no PDE in compartment '" + PLACE_HOLDER+"'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_13 = "Spatial Model, Jump condition for '" + PLACE_HOLDER + "' on membrane '" + PLACE_HOLDER+"' has a non-zero outward flux but no PDE in compartment '"+PLACE_HOLDER+"'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_14 = "Spatial Model, cannot mix " + VCML.OdeEquation + " and " + VCML.PdeEquation +" for variable '"+PLACE_HOLDER+ "'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_15 = "Spatial Model, cannot mix " + VCML.Steady + " and " + VCML.Unsteady + " " + VCML.PdeEquation + "s for variable '" +PLACE_HOLDER+ "'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_16 = "Spatial Model, there is neither a "+VCML.PdeEquation+" nor an " + VCML.OdeEquation+" for variable '"+PLACE_HOLDER + "'";	
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_17 = "Spatial Model, VolumeRegionEquation for '" + PLACE_HOLDER+"' in '" + PLACE_HOLDER+"' does not have a jump condition on membrane '" + PLACE_HOLDER + "'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_18 = "There should be at least one " + VCML.VolumeRegionEquation+" defined for volume region variable '" + PLACE_HOLDER + "'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_19 = "There should be at least one " + VCML.MembraneRegionEquation+" defined for membrane region variable '" + PLACE_HOLDER + "'";
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_20 = "There should be a " + VCML.FilamentRegionEquation +" defined for filament region variable "+ PLACE_HOLDER + " in " + VCML.FilamentSubDomain + " " + PLACE_HOLDER;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_21 = "There should be a "+ VCML.OdeEquation+" defined for variable "+PLACE_HOLDER+" for " + VCML.FilamentSubDomain + " " + PLACE_HOLDER;
	public final static String MATH_DESCRIPTION_SPATIAL_MODEL_22 = "Events are not supported in spatial models.";
	
	// non spatial stochastic
	public final static String MATH_DESCRIPTION_COMPARTMENT_STOCHASTIC_MODEL_1 = "stochastic model requires at least one stochastic volume variable";
	public final static String MATH_DESCRIPTION_COMPARTMENT_STOCHASTIC_MODEL_2 = "stochastic model requires at least one jump process";

	/**
	 * Substitute {@code objects} into the placeholders of {@code baseErrorMessage}, in order.
	 * <p>
	 * Behaviourally identical to {@code VCellErrorMessages.getErrorMessage}, including throwing when
	 * there are fewer arguments than placeholders. Duplicated rather than imported so the math
	 * namespace does not depend on the biological one to format its own messages.
	 */
	public static final String getErrorMessage(String baseErrorMessage, Object... objects) {
		String returnMessage = baseErrorMessage;
		if (baseErrorMessage.contains(PLACE_HOLDER)) {
			int counter = 0;
			while (returnMessage.contains(PLACE_HOLDER)) {
				if (counter >= objects.length) {
					throw new RuntimeException("MathDescriptionMessages.getErrorMessage(), not enough arguments for place holders");
				}
				returnMessage = returnMessage.replaceFirst(PLACE_HOLDER, objects[counter].toString());
				counter ++;
			}
		}
		if (returnMessage.contains(PLACE_HOLDER)) {
			throw new RuntimeException("MathDescriptionMessages.getErrorMessage(), not enough arguments for place holders, the message is " + returnMessage);
		}
		return returnMessage;
	}
}
