package cbit.vcell.xml;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;

public class VCMLSupport {

	public static String getXPathForModel() {
		return "/vcml:vcml/vcml:BioModel/vcml:Model";
	}

	public static String getXPathForModelParameter(String parameterName) {
		return getXPathForModel()+"/vcml:ModelParameters/vcml:Parameter[@name='" + parameterName + "']";
	}

	public static String getXPathForSimContexts() {
		return "/vcml:vcml/vcml:BioModel/vcml:SimulationSpec";
	}

	public static String getXPathForSimContext(String simContextName) {
		return getXPathForSimContexts() + "[@Name='" + simContextName + "']";
	}

	public static String getXPathForOutputFunctions(String simContextName) {
		return getXPathForSimContext(simContextName) + "/vcml:OutputFunctions";
	}

	public static String getXPathForSpecies(String speciesName) {
		return getXPathForModel() + "/vcml:LocalizedCompound[@Name='" + speciesName + "']";
	}

	public static String getXPathForSpeciesContextSpec(String simContextName, String speciesContextName) {
		return getXPathForSimContext(simContextName) + "/vcml:ReactionContext/vcml:LocalizedCompoundSpec[@LocalizedCompoundRef='" + speciesContextName + "']";
	}

	public static String getXPathForOutputFunction(String simContextName, String outputFunctionID) {
		return getXPathForOutputFunctions(simContextName) + "/vcml:AnnotatedFunction[@Name='" + outputFunctionID + "']";
	}

	public static String getXPathForBioModel() {
		return "/vcml:vcml/vcml:BioModel";
	}

	public static String getXPathForFeatureMappingAttribute(String simContextName, String featureName, String attributeName) {
		return getXPathForSimContext(simContextName) + "/vcml:GeometryContext/vcml:FeatureMapping[@Feature='" + featureName +"']/@" + attributeName;
	}

	public static String getXPathForMembraneMappingAttribute(String simContextName, String featureName, String attributeName) {
		return getXPathForSimContext(simContextName) + "/vcml:GeometryContext/vcml:FeatureMapping[@Feature='" + featureName +"']/@" + attributeName;
	}

	public static String getXPathForKineticLawParameter(String reactionName, String parameterName) {
		return getXPathForModel() + "/vcml:SimpleReaction[@Name='" + reactionName + "']/vcml:Kinetics/vcml:Parameter[@Name='" + parameterName + "']"; 
	}

}
