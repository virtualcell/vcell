package cbit.vcell.parser;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static cbit.vcell.parser.ExpressionUtils.functionallyEquivalent;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class ExpressionUtilsJSCLFlattenTest {

    record TestCase(Expression origExpression, Expression expectedFlattenedExpression, Boolean expectTimeout) {}


    public static Collection<TestCase> testCases() throws ExpressionException {
        return Arrays.asList(
                new TestCase(new Expression("KMOLE/KMOLE"),
                        new Expression("1.0"),
                        Boolean.FALSE),

                new TestCase(new Expression("KMOLE/KMOLE*KMOLE/KMOLE"),
                        new Expression("1.0"),
                        Boolean.FALSE),

                new TestCase(new Expression("KMOLE/KMOLE*KMOLE/KMOLE2"),
                        new Expression("KMOLE/KMOLE2"),
                        Boolean.FALSE),

                new TestCase(new Expression("KMOLE*pow(KMOLE,-1)"),
                        new Expression("1.0"),
                        Boolean.FALSE),

                new TestCase(new Expression("5*KMOLE*pow(KMOLE,-1)"),
                        new Expression("5.0"),
                        Boolean.FALSE),

                new TestCase(new Expression("5/KMOLE*KMOLE"),
                        new Expression("5.0"),
                        Boolean.FALSE),

                new TestCase(new Expression("((Kf_dimerization * pow(EGFR_active,2.0)) - (Kr_dimerization * EGFR_dimer))"),
                        new Expression(" - ( - ((EGFR_active ^ 2.0) * Kf_dimerization) + (EGFR_dimer * Kr_dimerization))"),
                        Boolean.FALSE),

                new TestCase(new Expression("(5.0 * KMOLE * ((1.0 / KMOLE) ^ 2.0))"),
                        new Expression("(5.0 / KMOLE)"),
                        Boolean.FALSE),

                new TestCase(new Expression("(10.0 * pow(KMOLE,-1.0) * Size_c0)"),
                        new Expression("((10.0 * Size_c0) / KMOLE)"),
                        Boolean.FALSE),

                new TestCase(new Expression("(50.0 + (s1_Count_initCount / (pow(KMOLE,-1.0) * Size_c0)))"),
                        new Expression("(((50.0 * Size_c0) + (KMOLE * s1_Count_initCount)) / Size_c0)"),
                        Boolean.FALSE),

                new TestCase(new Expression("(10.0 * Size_c0 / KMOLE)"),
                        new Expression("((10.0 * Size_c0) / KMOLE)"),
                        Boolean.FALSE),

                new TestCase(new Expression("(50.0 + (s1_Count_initCount / (pow(KMOLE,-1.0) * Size_c0)))"),
                        new Expression("(((50.0 * Size_c0) + (KMOLE * s1_Count_initCount)) / Size_c0)"),
                        Boolean.FALSE),

                new TestCase(new Expression("(((50.0 * Size_c0) + (KMOLE * s1_Count_init_uM / (KMOLE / Size_c0))) / Size_c0)"),
                        new Expression("(50.0 + s1_Count_init_uM)"),
                        Boolean.FALSE),

                new TestCase(new Expression("((KMOLE * (1.0 * pow(KMOLE,1.0))) * (1.0 * pow(KMOLE, - 1.0)))"),
                        new Expression("KMOLE"),
                        Boolean.FALSE),

                new TestCase(new Expression("(((2.0 * KMOLE) * (1.0 * pow(KMOLE,1.0))) * (1.0 * pow(KMOLE, - 1.0)))"),
                        new Expression("2.0*KMOLE"),
                        Boolean.FALSE),

                new TestCase(new Expression("((KMOLE ^ 2.0) / KMOLE)"),
                        new Expression("KMOLE"),
                        Boolean.FALSE),

                //
                // the expressions below are expected to timeout
                //
                new TestCase(new Expression(       "(((V32 / ((k32 ^ 3.0) + (Ca_nM ^ 3.0))) + b32) * ((Vm32 * (Ca_nM ^ 7.0) / ((km32 ^ 7.0) + (Ca_nM ^ 7.0))) + bm32))"),
                        new Expression("(((V32 + (b32 * (Ca_nM ^ 3.0)) + (b32 * (k32 ^ 3.0))) * ((Vm32 * (Ca_nM ^ 7.0)) + (bm32 * (Ca_nM ^ 7.0)) + (bm32 * (km32 ^ 7.0)))) / (((Ca_nM ^ 3.0) + (k32 ^ 3.0)) * ((Ca_nM ^ 7.0) + (km32 ^ 7.0))))"),
                        Boolean.TRUE),

                new TestCase(new Expression(       "(((V32 / ((k32 ^ 2.0) + (Ca_nM ^ 2.0))) + b32) * ((Vm32 * (Ca_nM ^ 7.0) / ((km32 ^ 7.0) + (Ca_nM ^ 7.0))) + bm32))"),
                        new Expression("((V32 + ((Ca_nM ^ 2.0) * b32) + (b32 * (k32 ^ 2.0))) * ((Vm32 * (Ca_nM ^ 7.0)) + (bm32 * (Ca_nM ^ 7.0)) + (bm32 * (km32 ^ 7.0))) / (((Ca_nM ^ 2.0) + (k32 ^ 2.0)) * ((Ca_nM ^ 7.0) + (km32 ^ 7.0))))"),
                        Boolean.TRUE),

//                new TestCase(new Expression("((kf * (1.0 - BrF) * PointedD_Cyt * BarbedD_Cyt / L) - (((k1 * L) + (k2 * (L ^ 2.0) * N)) * N * FAD_Cyt / (FAD_Cyt + FAT_Cyt + FADPi_Cyt)))"),
//                        new Expression("((kf * (1.0 - BrF) * PointedD_Cyt * BarbedD_Cyt / L) - (((k1 * L) + (k2 * (L ^ 2.0) * N)) * N * FAD_Cyt / (FAD_Cyt + FAT_Cyt + FADPi_Cyt)))"),
//                        Boolean.TRUE),

                new TestCase(new Expression("(0.001 * ((CofFAD2_Cyt * Kf) - (1.0E9 * BarbedD_Cyt * (Cof_Cyt ^ 2.0) * Kr * PointedD_Cyt)))"),
                        new Expression("(0.001 * ((CofFAD2_Cyt * Kf) - (1.0E9 * BarbedD_Cyt * (Cof_Cyt ^ 2.0) * Kr * PointedD_Cyt)))"),
                        Boolean.TRUE)

//                new TestCase(new Expression("(0.001 * ((CofFAD2_Cyt * Kf) - (1.0E9 * BarbedD_Cyt * ((0.001 * Cof_Cyt) ^ 2.0) * Kr * PointedD_Cyt)))"),
//                        new Expression("(0.001 * ((CofFAD2_Cyt * Kf) - (1.0E9 * BarbedD_Cyt * ((0.001 * Cof_Cyt) ^ 2.0) * Kr * PointedD_Cyt)))"),
//                        Boolean.TRUE),

        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    public void simplifySymbolFactorTest_small(TestCase testCase) throws ExpressionException {
        try {
            Expression flattenedExp = testCase.origExpression.simplifyJSCL(20000, true);
            assertTrue(testCase.expectedFlattenedExpression.compareEqual(flattenedExp), "expected='" + testCase.expectedFlattenedExpression.infix() + "', actual='" + flattenedExp.infix() + "'");
            assertTrue(functionallyEquivalent(testCase.expectedFlattenedExpression, flattenedExp, false), "expressions not equivalent: expected='" + testCase.expectedFlattenedExpression.infix() + "', actual='" + flattenedExp.infix() + "'");
            assertTrue(functionallyEquivalent(testCase.expectedFlattenedExpression, testCase.origExpression, false), "expressions not equivalent: expected='" + testCase.expectedFlattenedExpression.infix() + "', original='" + testCase.origExpression.infix() + "'");
        } catch (jscl.math.Expression.ExpressionTimeoutException e){
            if (testCase.expectTimeout){
                return;
            }else{
                throw e;
            }
        }
   }

}


