package cbit.vcell.numericstest;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.solver.test.*;
import cbit.vcell.solver.SimulationInfo;
/**
 * Insert the type's description here.
 * Creation date: (11/11/2004 1:32:38 PM)
 * @author: Frank Morgan
 */
public class TestCriteriaNewBioModel extends TestCriteriaNew {

	private BioModelInfo regrBioModelInfo;
	private String regressionApplicationName;

/**
 * TestCriteriaNewMathModel constructor comment.
 * @param argTcritKey java.math.BigDecimal
 * @param argSimInfo cbit.vcell.solver.SimulationInfo
 * @param argMaxRelError java.lang.Double
 * @param argMaxAbsError java.lang.Double
 * @param argVariableComparisonSummary cbit.vcell.solver.test.VariableComparisonSummary[]
 */
public TestCriteriaNewBioModel(
    java.math.BigDecimal argTcritKey,
    SimulationInfo argSimInfo,
    BioModelInfo argregrBMInfo,
    String argregrAppName,
    SimulationInfo argregrSimInfo,
    Double argMaxRelError,
    Double argMaxAbsError,
    VariableComparisonSummary[] argVariableComparisonSummary,
    String argReportStatus,
    String argReportStatusMessage) {
    super(
        argTcritKey,
        argSimInfo,
        argregrSimInfo,
        argMaxRelError,
        argMaxAbsError,
        argVariableComparisonSummary,argReportStatus,argReportStatusMessage);

    regrBioModelInfo = argregrBMInfo;
    regressionApplicationName = argregrAppName;
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/2004 2:53:00 PM)
 * @return java.lang.String
 */
public java.lang.String getRegressionApplicationName() {
	return regressionApplicationName;
}


/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 1:42:27 PM)
 */
public BioModelInfo getRegressionBioModelInfo() {
	
	return regrBioModelInfo;
	
}
}