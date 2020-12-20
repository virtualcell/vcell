package org.jlibsedml.modelsupport;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class that provides access to some constants describing likely model languages supported by SED-ML.<br>
 * This can be used when setting the <em>language</em> attribute of SED-ML model elements.
 * 
 * For example:
 * <pre>
 * Model basic = new Model("modelID", "ModelName",
			        SUPPORTED_LANGUAGE.SBML_GENERIC.getURN(),"modelSource");
 * </pre>
 * 
 * Also clients  can use this  class to query the language to see if they can support the  model format.
 * E.g., 
 * <pre>
 *    if (sedMLModel.getLanguage().equals(SUPPORTED_LANGUAGE.CELLML_1_0.getURN()) {
 *       // do something...
 *    }
 * </pre>
 * @author radams
 * @link http://sed-ml.org
 */
public final class SUPPORTED_LANGUAGE {
	private String language, urn, specURL;
	
public final static String NO_URL="none";
	
public final static String CELLML_GENERIC_ID="CellML (generic)";
public final static SUPPORTED_LANGUAGE CELLML_GENERIC= new SUPPORTED_LANGUAGE(CELLML_GENERIC_ID,
									"urn:sedml:language:cellML",NO_URL);

public final static String CELLML_1_0_ID="CellML 1.0";
public final static SUPPORTED_LANGUAGE CELLML_1_0= new SUPPORTED_LANGUAGE(CELLML_1_0_ID,
		"urn:sedml:language:cellML.1_0","http://www.cellml.org/specifications/cellml_1.0");

public final static String CELLML_1_1_ID="CellML 1.1";
public final static SUPPORTED_LANGUAGE CELLML_1_1= new SUPPORTED_LANGUAGE(CELLML_1_1_ID,
		"urn:sedml:language:cellML.1_1","http://www.cellml.org/specifications/cellml_1.1");

public final static String NEUROML_GENERIC_ID="NeuroML (Generic)";
public final static SUPPORTED_LANGUAGE NEUROML_GENERIC= new SUPPORTED_LANGUAGE(NEUROML_GENERIC_ID,
		"urn:sedml:language:neuroml",NO_URL);

public final static String NEUROML_1_8_1_L1_ID="NeuroML 1.8.1 Level 1";
public final static SUPPORTED_LANGUAGE NEUROML_1_8_1_L1= new SUPPORTED_LANGUAGE(NEUROML_1_8_1_L1_ID,
		"urn:sedml:language:neuroml.version-1_8_1.level-1",NO_URL);

public final static String NEUROML_1_8_1_L2_ID="NeuroML 1.8.1 Level 2";
public final static SUPPORTED_LANGUAGE NEUROML_1_8_1_L2= new SUPPORTED_LANGUAGE(NEUROML_1_8_1_L2_ID,
		"urn:sedml:language:neuroml.version-1_8_1.level-2",NO_URL);

public final static String SBML_GENERIC_ID="SBML (Generic)";
public final static SUPPORTED_LANGUAGE SBML_GENERIC= new SUPPORTED_LANGUAGE(SBML_GENERIC_ID,
		"urn:sedml:language:sbml",NO_URL);

public final static String SBML_L1V1_ID="SBML Level1 Version 1";
public final static SUPPORTED_LANGUAGE SBML_L1V1= new SUPPORTED_LANGUAGE(SBML_L1V1_ID,
		"urn:sedml:language:sbml.level-1.version-1","http://sbml.org/Special/specifications/sbml-level-1/version-1/html/");

public final static String SBML_L1V2_ID="SBML Level1 Version 2";
public final static SUPPORTED_LANGUAGE SBML_L1V2= new SUPPORTED_LANGUAGE(SBML_L1V2_ID,
		"urn:sedml:language:sbml.level-1.version-2","http://sbml.org/Special/specifications/sbml-level-1/version-2/html/");

public final static String SBML_L2V1_ID="SBML Level2 Version 1";
public final static SUPPORTED_LANGUAGE SBML_L2V1= new SUPPORTED_LANGUAGE(SBML_L2V1_ID,
		"urn:sedml:language:sbml.level-2.version-1","http://sbml.org/specifications/sbml-level-2/version-1/html/");

public final static String SBML_L2V2_ID="SBML Level2 Version 2";
public final static SUPPORTED_LANGUAGE SBML_L2V2= new SUPPORTED_LANGUAGE(SBML_L2V2_ID,
		"urn:sedml:language:sbml.level-2.version-2","http://www.sbml.org/specifications/sbml-level-2/version-2/revision-1/sbml-level-2-version-2-rev1.pdf");

public final static String SBML_L2V3Rel1_ID="SBML Level2 Version 3 (Release 1)";
public final static SUPPORTED_LANGUAGE SBML_L2V3Rel1= new SUPPORTED_LANGUAGE(SBML_L2V3Rel1_ID,
		"urn:sedml:language:sbml.level-2.version-3.release-1","http://www.sbml.org/specifications/sbml-level-2/version-3/release-1/sbml-level-2-version-3-rel-1.pdf");

public final static String SBML_L2V3Rel2_ID="SBML Level2 Version 3 (Release 2)";
public final static SUPPORTED_LANGUAGE SBML_L2V3Rel2= new SUPPORTED_LANGUAGE(SBML_L2V3Rel2_ID,
		"urn:sedml:language:sbml.level-2.version-3.release-2","http://precedings.nature.com/documents/58/version/2");

public final static String SBML_L2V4_ID="SBML Level2 Version 4";
public final static SUPPORTED_LANGUAGE SBML_L2V4= new SUPPORTED_LANGUAGE(SBML_L2V4_ID,
		"urn:sedml:language:sbml.level-2.version-4","http://precedings.nature.com/documents/2715/version/1");


public final static String SBML_L3V1_ID="SBML Level3 Version 1";
public final static SUPPORTED_LANGUAGE SBML_L3V1= new SUPPORTED_LANGUAGE(SBML_L3V1_ID,
		"urn:sedml:language:sbml.level-3.version-1","http://precedings.nature.com/documents/4123/version/1");

public final static String VCELL_GENERIC_ID="VCELL (Generic)";
public final static SUPPORTED_LANGUAGE VCELL_GENERIC= new SUPPORTED_LANGUAGE(VCELL_GENERIC_ID,
		"urn:sedml:language:vcml",NO_URL);

/**
 * Convenience list of all supported SBML  languages.
 */
public static final List<SUPPORTED_LANGUAGE> ALL_SBMLS = Arrays.asList(new SUPPORTED_LANGUAGE[]{
		SBML_GENERIC,SBML_L1V1,SBML_L1V2,SBML_L2V1,SBML_L2V2,SBML_L2V3Rel1,SBML_L2V3Rel2, SBML_L2V4,SBML_L3V1});

/**
 * Convenience list of all supported languages
 */
public static final List<SUPPORTED_LANGUAGE> ALL_LANGUAGES = Arrays.asList(new SUPPORTED_LANGUAGE[]{
        CELLML_GENERIC, CELLML_1_0,CELLML_1_1,NEUROML_1_8_1_L1, NEUROML_1_8_1_L2,NEUROML_GENERIC,VCELL_GENERIC,SBML_GENERIC,SBML_L1V1,SBML_L1V2,SBML_L2V1,SBML_L2V2,SBML_L2V3Rel1,SBML_L2V3Rel2, SBML_L2V4,SBML_L3V1});


private SUPPORTED_LANGUAGE(String language, String urn, String specURL) {
	this.language=language;
	this.urn=urn;
	this.specURL=specURL;
}

/**
 * Boolean test for whether a given language URN is indeed an SBML language
 * @param urn A non-null language urn
 * @return <code>true</code> if <code>urn</code> is an SBML language, <code>false</code> otherwise.
 */
public static boolean isSBML(String urn){
	for (SUPPORTED_LANGUAGE lang: ALL_SBMLS){
		if(lang.getURN().equalsIgnoreCase(urn)){
			return true;
		}
	}
	return false;
}

/**
 * Gets a human readable descriptive <code>String</code> of the model language.
 * @return A non-null <code>String</code>
 */
public String getLanguage() {
	return language;
}

/**
 * Returns a URN of the model language as a String.
 * @return A non-null <code>String</code>
 */
public String getURN() {
	return urn;
}

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((urn == null) ? 0 : urn.hashCode());
	return result;
}
/*
 * Equality is based on the value of the object's URN attribute.
 * (non-Javadoc)
 * @see java.lang.Object#equals(java.lang.Object)
 */
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	SUPPORTED_LANGUAGE other = (SUPPORTED_LANGUAGE) obj;
	if (urn == null) {
		if (other.urn != null)
			return false;
	} else if (!urn.equals(other.urn))
		return false;
	return true;
}

/**
 * Returns a URL to the specification documents of the model, or NO_URL if
 *  no such resource exists. 
 * @return A <code>String</code>
 */
public String getSpecificationURL() {
	return specURL;
} 


 
 
  
}
