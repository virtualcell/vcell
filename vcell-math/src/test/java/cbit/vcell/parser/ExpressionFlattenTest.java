package cbit.vcell.parser;

import cbit.vcell.units.VCUnitDefinition;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static cbit.vcell.parser.ExpressionUtils.functionallyEquivalent;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class ExpressionFlattenTest {

    public record TestCase(
            Expression origExpression,
            Expression expectedSimplifiedExpression,
            Expression expectedFlattenSafeExpression,
            Expression expectedFlattenSubstitutedExpression // where "KMOLE" is bound to a 'constant' SymbolTableEntry
    ) {}

    private final static double KMOLE_VALUE = 1.0/602.214179;
    private final static SymbolTableEntry constantKMOLE = new SymbolTableEntry() {
        @Override
        public double getConstantValue() throws ExpressionException { return KMOLE_VALUE; }
        @Override
        public Expression getExpression() { return new Expression(KMOLE_VALUE);}
        @Override
        public int getIndex() { return 0;}
        @Override
        public String getName() { return "KMOLE";}
        @Override
        public NameScope getNameScope() { return null;}
        @Override
        public VCUnitDefinition getUnitDefinition() {return null;}
        @Override
        public boolean isConstant() throws ExpressionException {return true;} // important - this allows substitution by flatten()
    };

    private final static SymbolTable symbolTableKMOLE = new SymbolTable() {
        @Override
        public SymbolTableEntry getEntry(String identifierString) {
            if (identifierString.equals(constantKMOLE.getName())){
                return constantKMOLE;
            }
            return new SimpleSymbolTable.SimpleSymbolTableEntry(identifierString,0, null);
        }
        @Override
        public void getEntries(Map<String, SymbolTableEntry> entryMap) {
            entryMap.put(constantKMOLE.getName(), constantKMOLE);
        }
    };



    public static Collection<TestCase> testCases() throws ExpressionException {
        return Arrays.asList(
                new TestCase(new Expression("((kon_m * M) - ((koff_m * m) / (1 - ((1 / 2 * ((3.0 * k_PIP2 * m) " +
                        "- (k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) - sqrt(((9.0 * (k_PIP2 ^ 2) * (m ^ 2)) " +
                        "- (6.0 * (k_PIP2 ^ 2) * m * ((100.0 * (KMOLE ^ (-1))) * pT)) + ((k_PIP2 ^ 2) * (((100.0 * (KMOLE ^ (-1))) * pT) ^ 2)) " +
                        "+ (6.0 * k_PIP2 * m) + (2 * k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) + 1)) + 1))" +
                        " * (3.0 - ((1 / 2 * ((3.0 * k_PIP2 * m) - (k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) " +
                        "- sqrt(((9.0 * (k_PIP2 ^ 2) * (m ^ 2)) - (6.0 * (k_PIP2 ^ 2) * m * ((100.0 * (KMOLE ^ (-1))) * pT)) " +
                        "+ ((k_PIP2 ^ 2) * (((100.0 * (KMOLE ^ (-1))) * pT) ^ 2)) + (6.0 * k_PIP2 * m) " +
                        "+ (2 * k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) + 1)) + 1)) * (( - (3.0 / 2) * k_PIP2 * m) " +
                        "+ ((1 / 2) * k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) + ((1 / 2) * sqrt(((9.0 * (k_PIP2 ^ 2) * (m ^ 2)) " +
                        "- (6.0 * (k_PIP2 ^ 2) * m * ((100.0 * (KMOLE ^ (-1))) * pT)) + ((k_PIP2 ^ 2) " +
                        "* (((100.0 * (KMOLE ^ (-1))) * pT) ^ 2)) + (6.0 * k_PIP2 * m) " +
                        "+ (2 * k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) + 1))) + (5.0 / 2))))))))"),
                        new Expression("((kon_m * M) - ((koff_m * m) / (1 - ((1 / 2 * ((3.0 * k_PIP2 * m) " +
                                "- (k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) - sqrt(((9.0 * (k_PIP2 ^ 2) * (m ^ 2)) " +
                                "- (6.0 * (k_PIP2 ^ 2) * m * ((100.0 * (KMOLE ^ (-1))) * pT)) + ((k_PIP2 ^ 2) * (((100.0 * (KMOLE ^ (-1))) * pT) ^ 2)) " +
                                "+ (6.0 * k_PIP2 * m) + (2 * k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) + 1)) + 1))" +
                                " * (3.0 - ((1 / 2 * ((3.0 * k_PIP2 * m) - (k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) " +
                                "- sqrt(((9.0 * (k_PIP2 ^ 2) * (m ^ 2)) - (6.0 * (k_PIP2 ^ 2) * m * ((100.0 * (KMOLE ^ (-1))) * pT)) " +
                                "+ ((k_PIP2 ^ 2) * (((100.0 * (KMOLE ^ (-1))) * pT) ^ 2)) + (6.0 * k_PIP2 * m) " +
                                "+ (2 * k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) + 1)) + 1)) * (( - (3.0 / 2) * k_PIP2 * m) " +
                                "+ ((1 / 2) * k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) + ((1 / 2) * sqrt(((9.0 * (k_PIP2 ^ 2) * (m ^ 2)) " +
                                "- (6.0 * (k_PIP2 ^ 2) * m * ((100.0 * (KMOLE ^ (-1))) * pT)) + ((k_PIP2 ^ 2) " +
                                "* (((100.0 * (KMOLE ^ (-1))) * pT) ^ 2)) + (6.0 * k_PIP2 * m) " +
                                "+ (2 * k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) + 1))) + (5.0 / 2))))))))"),
                        new Expression("((kon_m * M) - ((koff_m * m) / (1 - ((1 / 2 * ((3.0 * k_PIP2 * m) " +
                                "- (k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) - sqrt(((9.0 * (k_PIP2 ^ 2) * (m ^ 2)) " +
                                "- (6.0 * (k_PIP2 ^ 2) * m * ((100.0 * (KMOLE ^ (-1))) * pT)) + ((k_PIP2 ^ 2) * (((100.0 * (KMOLE ^ (-1))) * pT) ^ 2)) " +
                                "+ (6.0 * k_PIP2 * m) + (2 * k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) + 1)) + 1))" +
                                " * (3.0 - ((1 / 2 * ((3.0 * k_PIP2 * m) - (k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) " +
                                "- sqrt(((9.0 * (k_PIP2 ^ 2) * (m ^ 2)) - (6.0 * (k_PIP2 ^ 2) * m * ((100.0 * (KMOLE ^ (-1))) * pT)) " +
                                "+ ((k_PIP2 ^ 2) * (((100.0 * (KMOLE ^ (-1))) * pT) ^ 2)) + (6.0 * k_PIP2 * m) " +
                                "+ (2 * k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) + 1)) + 1)) * (( - (3.0 / 2) * k_PIP2 * m) " +
                                "+ ((1 / 2) * k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) + ((1 / 2) * sqrt(((9.0 * (k_PIP2 ^ 2) * (m ^ 2)) " +
                                "- (6.0 * (k_PIP2 ^ 2) * m * ((100.0 * (KMOLE ^ (-1))) * pT)) + ((k_PIP2 ^ 2) " +
                                "* (((100.0 * (KMOLE ^ (-1))) * pT) ^ 2)) + (6.0 * k_PIP2 * m) " +
                                "+ (2 * k_PIP2 * ((100.0 * (KMOLE ^ (-1))) * pT)) + 1))) + (5.0 / 2))))))))"),
                        new Expression("((kon_m * M) - ((koff_m * m) / (1.0 - ((1.0 / 2.0 * ((3.0 * k_PIP2 * m) " +
                                "- (k_PIP2 * ((100.0 * (0.001660538783162726 ^  - 1.0)) * pT)) - sqrt(((9.0 * (k_PIP2 ^ 2.0) * (m ^ 2.0)) " +
                                "- (6.0 * (k_PIP2 ^ 2.0) * m * ((100.0 * (0.001660538783162726 ^  - 1.0)) * pT)) " +
                                "+ ((k_PIP2 ^ 2.0) * (((100.0 * (0.001660538783162726 ^  - 1.0)) * pT) ^ 2.0)) + (6.0 * k_PIP2 * m) " +
                                "+ (2.0 * k_PIP2 * ((100.0 * (0.001660538783162726 ^  - 1.0)) * pT)) + 1.0)) + 1.0)) * (3.0 - ((1.0 / 2.0 " +
                                "* ((3.0 * k_PIP2 * m) - (k_PIP2 * ((100.0 * (0.001660538783162726 ^  - 1.0)) * pT)) - sqrt(((9.0 * (k_PIP2 ^ 2.0) " +
                                "* (m ^ 2.0)) - (6.0 * (k_PIP2 ^ 2.0) * m * ((100.0 * (0.001660538783162726 ^  - 1.0)) * pT)) " +
                                "+ ((k_PIP2 ^ 2.0) * (((100.0 * (0.001660538783162726 ^  - 1.0)) * pT) ^ 2.0)) + (6.0 * k_PIP2 * m) " +
                                "+ (2.0 * k_PIP2 * ((100.0 * (0.001660538783162726 ^  - 1.0)) * pT)) + 1.0)) + 1.0)) * (( - (3.0 / 2.0) " +
                                "* k_PIP2 * m) + ((1.0 / 2.0) * k_PIP2 * ((100.0 * (0.001660538783162726 ^  - 1.0)) * pT)) + ((1.0 / 2.0) " +
                                "* sqrt(((9.0 * (k_PIP2 ^ 2.0) * (m ^ 2.0)) - (6.0 * (k_PIP2 ^ 2.0) * m * ((100.0 * (0.001660538783162726 ^  - 1.0)) * pT)) " +
                                "+ ((k_PIP2 ^ 2.0) * (((100.0 * (0.001660538783162726 ^  - 1.0)) * pT) ^ 2.0)) + (6.0 * k_PIP2 * m) " +
                                "+ (2.0 * k_PIP2 * ((100.0 * (0.001660538783162726 ^  - 1.0)) * pT)) + 1.0))) + (5.0 / 2.0))))))))")
                        ),
                new TestCase(new Expression("KMOLE/KMOLE"),
                        new Expression("1.0"),
                        new Expression("KMOLE/KMOLE"),
                        new Expression("1.0")
                ),
                new TestCase(new Expression("KMOLE/KMOLE*KMOLE/KMOLE"),
                        new Expression("1.0"),
                        new Expression("(KMOLE * KMOLE / KMOLE / KMOLE)"),
                        new Expression("1.0")
                ),
                new TestCase(new Expression("KMOLE/KMOLE*KMOLE/KMOLE2"),
                        new Expression("KMOLE/KMOLE2"),
                        new Expression("(KMOLE * KMOLE / KMOLE / KMOLE2)"),
                        new Expression(new DecimalFormat("#.##################").format(KMOLE_VALUE)+"/KMOLE2")
                ),
                new TestCase(new Expression("KMOLE*pow(KMOLE,-1)"),
                        new Expression("1.0"),
                        new Expression("(KMOLE * (KMOLE ^ -1.0))"),
                        new Expression("1.0")
                ),
                new TestCase(new Expression("5*KMOLE*pow(KMOLE,-1)"),
                        new Expression("5.0"),
                        new Expression("5*KMOLE*pow(KMOLE,-1)"),
                        new Expression("5.0")
                ),
                new TestCase(new Expression("5/KMOLE*KMOLE"),
                        new Expression("5.0"),
                        new Expression("(5.0 * KMOLE / KMOLE)"),
                        new Expression("5.0")
                ),
                new TestCase(new Expression("5*KMOLE*pow(KMOLE,-2)"),
                        new Expression("5.0 / KMOLE"),
                        new Expression("5*KMOLE*pow(KMOLE,-2)"),
                        //new Expression(5.0 / KMOLE_VALUE),
                        new Expression(3011.0708949999994)
                ),
                new TestCase(new Expression("-((-x)^2)"),
                        new Expression("-(x^2)"),
                        new Expression(" - ( - x ^ 2.0)"),
                        new Expression(" - ( - x ^ 2.0)")
                        ),
                new TestCase(new Expression("((-x)^2)"),
                        new Expression("(x^2)"),
                        new Expression("( - x ^ 2.0)"),
                        new Expression("( - x ^ 2.0)")
                ),
                new TestCase(new Expression("((Kf_dimerization * pow(EGFR_active,2.0)) - (Kr_dimerization * EGFR_dimer))"),
                        new Expression(" - ( - ((EGFR_active ^ 2.0) * Kf_dimerization) + (EGFR_dimer * Kr_dimerization))"),
                        new Expression("((Kf_dimerization * (EGFR_active ^ 2.0)) - (Kr_dimerization * EGFR_dimer))"),
                        new Expression("((Kf_dimerization * (EGFR_active ^ 2.0)) - (Kr_dimerization * EGFR_dimer))")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    public void testCorrectnessOfTestCases(TestCase testCase) throws ExpressionException {
        //
        // test correctness of expected expressions for Simplification (without constant substitution)
        //
        assertTrue(functionallyEquivalent(testCase.expectedSimplifiedExpression, testCase.origExpression, false), "expected safe simplified expression not equivalent," +
                " expected='" + testCase.expectedSimplifiedExpression.infix() + "', original='" + testCase.origExpression.infix() + "'");

        //
        // test correctness of expected expression for Flattened (without constant substitution)
        //
        assertTrue(functionallyEquivalent(testCase.expectedFlattenSafeExpression, testCase.origExpression, false), "expected flattened expression without simplification not equivalent," +
                " expected='" + testCase.expectedFlattenSafeExpression.infix() + "', substitutedOriginal='" + testCase.origExpression.infix() + "'");

        //
        // test correctness of expected expression for Flattened with Substitution
        //
        Expression substitutedOrigExpression = new Expression(testCase.origExpression);
        substitutedOrigExpression.substituteInPlace(new Expression(constantKMOLE.getName()),new Expression(KMOLE_VALUE));
        assertTrue(functionallyEquivalent(testCase.expectedFlattenSubstitutedExpression, substitutedOrigExpression, false), "expected flattened expression with substitution not equivalent to manually substituted original," +
                " expected='" + testCase.expectedFlattenSubstitutedExpression.infix() + "', substitutedOriginal='" + substitutedOrigExpression.infix() + "'");
    }

    @ParameterizedTest
    @MethodSource("testCases")
    public void testSimplifyJSCL(TestCase testCase) throws ExpressionException {
        Expression simplifiedUnboundExp = testCase.origExpression.simplifyJSCL(20000, false);
        assertTrue(testCase.expectedSimplifiedExpression.compareEqual(simplifiedUnboundExp), "didn't compare equal for unbound, expected='" + testCase.expectedSimplifiedExpression.infix() + "', actual='" + simplifiedUnboundExp.infix() + "'");

        // simplifyJSCL works the same (is safe) for bound or unbound expressions
        Expression boundOrigExpression = getBoundExpression(testCase.origExpression);
        Expression simplifiedBoundExp = boundOrigExpression.simplifyJSCL(20000, false);
        assertTrue(testCase.expectedSimplifiedExpression.compareEqual(simplifiedBoundExp), "didn't compare equal for bound, expected='" + testCase.expectedSimplifiedExpression.infix() + "', actual='" + simplifiedBoundExp.infix() + "'");
    }

    @ParameterizedTest
    @MethodSource("testCases")
    public void testFlatten(TestCase testCase) throws ExpressionException {
        Expression flattenedUnboundExp = testCase.origExpression.flatten();
        assertTrue(testCase.expectedFlattenSafeExpression.compareEqual(flattenedUnboundExp), "didn't compare equal for unbound, expected='" + testCase.expectedFlattenSafeExpression.infix() + "', actual='" + flattenedUnboundExp.infix() + "'");

        // flatten should substitute numeric value for KMOLE for bound expressions
        Expression boundOrigExpression = getBoundExpression(testCase.origExpression);
        Expression flattenedBoundExp = boundOrigExpression.flatten();
        assertTrue(testCase.expectedFlattenSubstitutedExpression.compareEqual(flattenedBoundExp), "didn't compare equal for bound, expected='" + testCase.expectedFlattenSubstitutedExpression.infix() + "', actual='" + flattenedBoundExp.infix() + "'");
    }

    @ParameterizedTest
    @MethodSource("testCases")
    public void testFlattenSafe(TestCase testCase) throws ExpressionException {
        Expression flattenedUnboundExp = testCase.origExpression.flattenSafe();
        assertTrue(testCase.expectedFlattenSafeExpression.compareEqual(flattenedUnboundExp), "didn't compare equal for unbound, expected='" + testCase.expectedFlattenSafeExpression.infix() + "', actual='" + flattenedUnboundExp.infix() + "'");

        // flattenSafe works the same (is safe) for bound or unbound expressions - no substitution
        Expression boundOrigExpression = getBoundExpression(testCase.origExpression);
        Expression flattenedBoundExp = boundOrigExpression.flattenSafe();
        assertTrue(testCase.expectedFlattenSafeExpression.compareEqual(flattenedBoundExp), "didn't compare equal for bound, expected='" + testCase.expectedFlattenSafeExpression.infix() + "', actual='" + flattenedBoundExp.infix() + "'");
    }

    private Expression getBoundExpression(Expression exp) throws ExpressionBindingException {
        Expression boundExp = new Expression(exp);
        boundExp.bindExpression(symbolTableKMOLE);
        return boundExp;
    }

}


