package cbit.vcell.numericstest;

import cbit.vcell.mathmodel.MathModelInfo;
import java.math.BigDecimal;
import cbit.sql.Version;
/**
 * Insert the type's description here.
 * Creation date: (10/16/2004 2:01:25 PM)
 * @author: Frank Morgan
 */
public abstract class TestCaseNew implements java.io.Serializable {

	private BigDecimal tcKey;
	private Version version;
	//private MathModelInfo mathModelInfo;
	private String type;
	private TestCriteriaNew[] testCriterias;
	private String annotation;
	
	public static final String EXACT = 			"EXACT";
	public static final String CONSTRUCTED = 	"CONSTRUCTED";
	public static final String REGRESSION = 	"REGRESSION";
/**
 * TestCaseNew constructor comment.
 */
public TestCaseNew(BigDecimal argTCKey,Version argVersion,String argType,String argAnnot,TestCriteriaNew[] argTestCriterias) {
	if (! (argType.equals(EXACT) || argType.equals(CONSTRUCTED) || argType.equals(REGRESSION)) ) {
		throw new IllegalArgumentException("Unsupported ty[e="+argType);
	}

	version = argVersion;
	type = argType;
	testCriterias = argTestCriterias;
	annotation = argAnnot;
	tcKey = argTCKey;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2004 2:34:11 PM)
 * @return java.lang.String
 */
public java.lang.String getAnnotation() {
	return annotation;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2004 2:34:11 PM)
 * @return java.lang.String
 */
public BigDecimal getTCKey() {
	return tcKey;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2004 2:34:11 PM)
 * @return cbit.vcell.numericstest.TestCriteriaNew[]
 */
public cbit.vcell.numericstest.TestCriteriaNew[] getTestCriterias() {
	return testCriterias;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2004 2:34:11 PM)
 * @return java.lang.String
 */
public java.lang.String getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (11/10/2004 9:56:29 AM)
 */
public Version getVersion() {

	return version;	
}
}
