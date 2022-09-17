package cbit.vcell.parser;

import cbit.vcell.units.VCUnitDefinition;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.DecimalFormat;
import java.util.*;

@RunWith(Parameterized.class)
public class ExpressionFlattenTest {

    private final Expression origExpression;
    private final Expression expectedSimplifiedExpression;
    private final Expression expectedFlattenSafeExpression;
    private final Expression expectedFlattenSubstitutedExpression; // where "KMOLE" is bound to a 'constant' SymbolTableEntry

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

    public ExpressionFlattenTest(Expression origExpression, Expression expectedSimplified, Expression expectedFlattenSafe, Expression expectedFlattenSubstituted) {
        this.origExpression = origExpression;
        this.expectedSimplifiedExpression = expectedSimplified;
        this.expectedFlattenSafeExpression = expectedFlattenSafe;
        this.expectedFlattenSubstitutedExpression = expectedFlattenSubstituted;
    }



    @Parameterized.Parameters
    public static Collection<Expression[]> testCases() throws ExpressionException {
        return Arrays.asList(new Expression[][]{
                {new Expression("((kon_m * M) - ((koff_m * m) / (1 - ((1 / 2 * ((3.0 * k_PIP2 * m) " +
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
//                        new Expression("((kon_m * M) - (koff_m * m / (1.0 - (0.5 * (1.0 + (3.0 * k_PIP2 * m) " +
//                                "- (60221.41789999999 * k_PIP2 * pT) - sqrt((1.0 + (9.0 * (k_PIP2 ^ 2.0) * (m ^ 2.0)) " +
//                                "- (361328.50739999994 * (k_PIP2 ^ 2.0) * m * pT) + ((k_PIP2 ^ 2.0) * ((60221.41789999999 * pT) ^ 2.0)) " +
//                                "+ (6.0 * k_PIP2 * m) + (120442.83579999999 * k_PIP2 * pT)))) * (3.0 - (0.5 * (1.0 + (3.0 * k_PIP2 * m) " +
//                                "- (60221.41789999999 * k_PIP2 * pT) - sqrt((1.0 + (9.0 * (k_PIP2 ^ 2.0) * (m ^ 2.0)) " +
//                                "- (361328.50739999994 * (k_PIP2 ^ 2.0) * m * pT) + ((k_PIP2 ^ 2.0) * ((60221.41789999999 * pT) ^ 2.0)) " +
//                                "+ (6.0 * k_PIP2 * m) + (120442.83579999999 * k_PIP2 * pT)))) * (2.5 - (1.5 * k_PIP2 * m) " +
//                                "+ (30110.708949999997 * k_PIP2 * pT) + (0.5 * sqrt((1.0 + (9.0 * (k_PIP2 ^ 2.0) * (m ^ 2.0)) " +
//                                "- (361328.50739999994 * (k_PIP2 ^ 2.0) * m * pT) + ((k_PIP2 ^ 2.0) * ((60221.41789999999 * pT) ^ 2.0)) " +
//                                "+ (6.0 * k_PIP2 * m) + (120442.83579999999 * k_PIP2 * pT)))))))))))"),
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
                                "+ (2.0 * k_PIP2 * ((100.0 * (0.001660538783162726 ^  - 1.0)) * pT)) + 1.0))) + (5.0 / 2.0))))))))"),
                },
                {new Expression("KMOLE/KMOLE"),
                        new Expression("1.0"),
                        new Expression("KMOLE/KMOLE"),
                        new Expression("1.0"),
                },
                {new Expression("KMOLE/KMOLE*KMOLE/KMOLE"),
                        new Expression("1.0"),
                        new Expression("(KMOLE * KMOLE / KMOLE / KMOLE)"),
                        new Expression("1.0"),
                },
                {new Expression("KMOLE/KMOLE*KMOLE/KMOLE2"),
                        new Expression("KMOLE/KMOLE2"),
                        new Expression("(KMOLE * KMOLE / KMOLE / KMOLE2)"),
                        new Expression(new DecimalFormat("#.##################").format(KMOLE_VALUE)+"/KMOLE2"),
                },
                {new Expression("KMOLE*pow(KMOLE,-1)"),
                        new Expression("1.0"),
                        new Expression("(KMOLE * (KMOLE ^ -1.0))"),
                        new Expression("1.0"),
                },
                {new Expression("5*KMOLE*pow(KMOLE,-1)"),
                        new Expression("5.0"),
                        new Expression("5*KMOLE*pow(KMOLE,-1)"),
                        new Expression("5.0"),
                },
                {new Expression("5/KMOLE*KMOLE"),
                        new Expression("5.0"),
                        new Expression("(5.0 * KMOLE / KMOLE)"),
                        new Expression("5.0"),
                },
                {new Expression("5*KMOLE*pow(KMOLE,-2)"),
                        new Expression("5.0 / KMOLE"),
                        new Expression("5*KMOLE*pow(KMOLE,-2)"),
                        //new Expression(5.0 / KMOLE_VALUE),
                        new Expression(3011.0708949999994),
                },
                {new Expression("-((-x)^2)"),
                        new Expression("-(x^2)"),
                        new Expression(" - ( - x ^ 2.0)"),
                        new Expression(" - ( - x ^ 2.0)"),
                },
                {new Expression("((-x)^2)"),
                        new Expression("(x^2)"),
                        new Expression("( - x ^ 2.0)"),
                        new Expression("( - x ^ 2.0)"),
                },
                {new Expression("((Kf_dimerization * pow(EGFR_active,2.0)) - (Kr_dimerization * EGFR_dimer))"),
                        new Expression(" - ( - ((EGFR_active ^ 2.0) * Kf_dimerization) + (EGFR_dimer * Kr_dimerization))"),
                        new Expression("((Kf_dimerization * (EGFR_active ^ 2.0)) - (Kr_dimerization * EGFR_dimer))"),
                        new Expression("((Kf_dimerization * (EGFR_active ^ 2.0)) - (Kr_dimerization * EGFR_dimer))"),
                }
        });
    }

    @Test
    public void testCorrectnessOfTestCases() throws ExpressionException {
        //
        // test correctness of expected expressions for Simplification (without constant substitution)
        //
        Assert.assertTrue("expected safe simplified expression not equivalent," +
                        " expected='"+expectedSimplifiedExpression.infix()+"', original='"+origExpression.infix()+"'",
                ExpressionUtils.functionallyEquivalent(expectedSimplifiedExpression,origExpression,false));

        //
        // test correctness of expected expression for Flattened (without constant substitution)
        //
        Assert.assertTrue("expected flattened expression without simplification not equivalent," +
                        " expected='"+expectedFlattenSafeExpression.infix()+"', substitutedOriginal='"+origExpression.infix()+"'",
                ExpressionUtils.functionallyEquivalent(expectedFlattenSafeExpression,origExpression,false));

        //
        // test correctness of expected expression for Flattened with Substitution
        //
        Expression substitutedOrigExpression = new Expression(origExpression);
        substitutedOrigExpression.substituteInPlace(new Expression(constantKMOLE.getName()),new Expression(KMOLE_VALUE));
        Assert.assertTrue("expected flattened expression with substitution not equivalent to manually substituted original," +
                        " expected='"+expectedFlattenSubstitutedExpression.infix()+"', substitutedOriginal='"+substitutedOrigExpression.infix()+"'",
                ExpressionUtils.functionallyEquivalent(expectedFlattenSubstitutedExpression,substitutedOrigExpression,false));
    }

    @Test
    public void testSimplifyJSCL() throws ExpressionException {
        Expression simplifiedUnboundExp = origExpression.simplifyJSCL(1000, true);
        Assert.assertTrue("didn't compare equal for unbound, expected='"+expectedSimplifiedExpression.infix()+"', actual='"+simplifiedUnboundExp.infix()+"'",
                expectedSimplifiedExpression.compareEqual(simplifiedUnboundExp));

        // simplifyJSCL works the same (is safe) for bound or unbound expressions
        Expression boundOrigExpression = getBoundExpression(origExpression);
        Expression simplifiedBoundExp = boundOrigExpression.simplifyJSCL(1000, true);
        Assert.assertTrue("didn't compare equal for bound, expected='"+expectedSimplifiedExpression.infix()+"', actual='"+simplifiedBoundExp.infix()+"'",
                expectedSimplifiedExpression.compareEqual(simplifiedBoundExp));
    }

    @Test
    public void testFlatten() throws ExpressionException {
        Expression flattenedUnboundExp = origExpression.flatten();
        Assert.assertTrue("didn't compare equal for unbound, expected='"+expectedFlattenSafeExpression.infix()+"', actual='"+flattenedUnboundExp.infix()+"'",
                expectedFlattenSafeExpression.compareEqual(flattenedUnboundExp));

        // flatten should substitute numeric value for KMOLE for bound expressions
        Expression boundOrigExpression = getBoundExpression(origExpression);
        Expression flattenedBoundExp = boundOrigExpression.flatten();
        Assert.assertTrue("didn't compare equal for bound, expected='"+expectedFlattenSubstitutedExpression.infix()+"', actual='"+flattenedBoundExp.infix()+"'",
                expectedFlattenSubstitutedExpression.compareEqual(flattenedBoundExp));
    }

    @Test
    public void testFlattenSafe() throws ExpressionException {
        Expression flattenedUnboundExp = origExpression.flattenSafe();
        Assert.assertTrue("didn't compare equal for unbound, expected='"+expectedFlattenSafeExpression.infix()+"', actual='"+flattenedUnboundExp.infix()+"'",
                expectedFlattenSafeExpression.compareEqual(flattenedUnboundExp));

        // flattenSafe works the same (is safe) for bound or unbound expressions - no substitution
        Expression boundOrigExpression = getBoundExpression(origExpression);
        Expression flattenedBoundExp = boundOrigExpression.flattenSafe();
        Assert.assertTrue("didn't compare equal for bound, expected='"+expectedFlattenSafeExpression.infix()+"', actual='"+flattenedBoundExp.infix()+"'",
                expectedFlattenSafeExpression.compareEqual(flattenedBoundExp));
    }

    private Expression getBoundExpression(Expression exp) throws ExpressionBindingException {
        Expression boundExp = new Expression(exp);
        boundExp.bindExpression(symbolTableKMOLE);
        return boundExp;
    }

}


