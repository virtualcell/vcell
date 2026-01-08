package org.jlibsedml.modelsupport;

import java.util.HashMap;

import org.jlibsedml.SedMLTags;

/** List of constants useful in this package (model type, simulator type, etc.)
 * 
 */
public class ToolSupport {
	
    private ToolSupport(){}
	
	public static final String MODEL_VCML = "VCML";
	

	/** Simulator types constants : for now VCell and Copasi simulators */
	public static final String SIMULATOR_VCELL = "vcell";
	public static final String SIMULATOR_COPASI = "copasi";

	// use all namespaces? or try to get it from sbml doc? But sbml doc does not come with prefix .... :(
	public static HashMap<String, String> nameSpaces_PrefixesHashMap = new HashMap<String, String>();
	
	static {
		nameSpaces_PrefixesHashMap.put(SedMLTags.SBML_NS_PREFIX, SedMLTags.SBML_NS);
		nameSpaces_PrefixesHashMap.put(SedMLTags.SBML_NS_PREFIX, SedMLTags.SBML_NS_L2V4);
		nameSpaces_PrefixesHashMap.put(SedMLTags.MATHML_NS_PREFIX, SedMLTags.MATHML_NS);
	}

}
