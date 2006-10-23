package cbit.vcell.numericstest;
import cbit.util.document.MathModelInfo;
import cbit.vcell.simulation.SimulationInfo;
import cbit.vcell.solver.test.VariableComparisonSummary;
/**
 * Insert the type's description here.
 * Creation date: (11/11/2004 1:32:38 PM)
 * @author: Frank Morgan
 */
public class TestCriteriaNewMathModel extends TestCriteriaNew {

	private MathModelInfo regrMathModelInfo;

/**
 * TestCriteriaNewMathModel constructor comment.
 * @param argTcritKey java.math.BigDecimal
 * @param argSimInfo cbit.vcell.solver.SimulationInfo
 * @param argMaxRelError java.lang.Double
 * @param argMaxAbsError java.lang.Double
 * @param argVariableComparisonSummary cbit.vcell.solver.test.VariableComparisonSummary[]
 */
public TestCriteriaNewMathModel(
    java.math.BigDecimal argTcritKey,
    SimulationInfo argSimInfo,
    MathModelInfo argregrMMInfo,
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

    regrMathModelInfo = argregrMMInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 1:42:27 PM)
 */
public MathModelInfo getRegressionMathModelInfo() {
	
	return regrMathModelInfo;
	
	}
}