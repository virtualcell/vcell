package cbit.vcell.parser;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.test.Fast;

import java.util.Arrays;
import java.util.Collection;

@Category(Fast.class)
@RunWith(Parameterized.class)
public class ExpressionUtilsJSCLFlattenTest {

    private Expression expectedFlattenedExpression;
    private Expression origExpression;
    private Boolean expectTimeout;

    public ExpressionUtilsJSCLFlattenTest(Expression origExpression, Expression expectedFlattenedExpression, Boolean expectTimeout) {
        this.expectedFlattenedExpression = expectedFlattenedExpression;
        this.origExpression = origExpression;
        this.expectTimeout = expectTimeout;
    }



    @Parameterized.Parameters
    public static Collection<Object[]> testCases() throws ExpressionException {
        return Arrays.asList(new Object[][]{

                {new Expression("KMOLE/KMOLE"),
                        new Expression("1.0"),
                        Boolean.FALSE},

                {new Expression("KMOLE/KMOLE*KMOLE/KMOLE"),
                        new Expression("1.0"),
                        Boolean.FALSE},

                {new Expression("KMOLE/KMOLE*KMOLE/KMOLE2"),
                        new Expression("KMOLE/KMOLE2"),
                        Boolean.FALSE},

                {new Expression("KMOLE*pow(KMOLE,-1)"),
                        new Expression("1.0"),
                        Boolean.FALSE},

                {new Expression("5*KMOLE*pow(KMOLE,-1)"),
                        new Expression("5.0"),
                        Boolean.FALSE},

                {new Expression("5/KMOLE*KMOLE"),
                        new Expression("5.0"),
                        Boolean.FALSE},

                {new Expression("((Kf_dimerization * pow(EGFR_active,2.0)) - (Kr_dimerization * EGFR_dimer))"),
                        new Expression(" - ( - ((EGFR_active ^ 2.0) * Kf_dimerization) + (EGFR_dimer * Kr_dimerization))"),
                        Boolean.FALSE},

                {new Expression("(5.0 * KMOLE * ((1.0 / KMOLE) ^ 2.0))"),
                        new Expression("(5.0 / KMOLE)"),
                        Boolean.FALSE},

                {new Expression("(10.0 * pow(KMOLE,-1.0) * Size_c0)"),
                        new Expression("((10.0 * Size_c0) / KMOLE)"),
                        Boolean.FALSE},

                {new Expression("(50.0 + (s1_Count_initCount / (pow(KMOLE,-1.0) * Size_c0)))"),
                        new Expression("(((50.0 * Size_c0) + (KMOLE * s1_Count_initCount)) / Size_c0)"),
                        Boolean.FALSE},

                {new Expression("(10.0 * Size_c0 / KMOLE)"),
                        new Expression("((10.0 * Size_c0) / KMOLE)"),
                        Boolean.FALSE},

                {new Expression("(50.0 + (s1_Count_initCount / (pow(KMOLE,-1.0) * Size_c0)))"),
                        new Expression("(((50.0 * Size_c0) + (KMOLE * s1_Count_initCount)) / Size_c0)"),
                        Boolean.FALSE},

                {new Expression("(((50.0 * Size_c0) + (KMOLE * s1_Count_init_uM / (KMOLE / Size_c0))) / Size_c0)"),
                        new Expression("(50.0 + s1_Count_init_uM)"),
                        Boolean.FALSE},

                {new Expression("((KMOLE * (1.0 * pow(KMOLE,1.0))) * (1.0 * pow(KMOLE, - 1.0)))"),
                        new Expression("KMOLE"),
                        Boolean.FALSE},

                {new Expression("(((2.0 * KMOLE) * (1.0 * pow(KMOLE,1.0))) * (1.0 * pow(KMOLE, - 1.0)))"),
                        new Expression("2.0*KMOLE"),
                        Boolean.FALSE},

                {new Expression("((KMOLE ^ 2.0) / KMOLE)"),
                        new Expression("KMOLE"),
                        Boolean.FALSE},

                //
                // the expressions below are expected to timeout
                //
                {new Expression(       "(((V32 / ((k32 ^ 3.0) + (Ca_nM ^ 3.0))) + b32) * ((Vm32 * (Ca_nM ^ 7.0) / ((km32 ^ 7.0) + (Ca_nM ^ 7.0))) + bm32))"),
                        new Expression("(((V32 + (b32 * (Ca_nM ^ 3.0)) + (b32 * (k32 ^ 3.0))) * ((Vm32 * (Ca_nM ^ 7.0)) + (bm32 * (Ca_nM ^ 7.0)) + (bm32 * (km32 ^ 7.0)))) / (((Ca_nM ^ 3.0) + (k32 ^ 3.0)) * ((Ca_nM ^ 7.0) + (km32 ^ 7.0))))"),
                        Boolean.TRUE},

                {new Expression(       "(((V32 / ((k32 ^ 2.0) + (Ca_nM ^ 2.0))) + b32) * ((Vm32 * (Ca_nM ^ 7.0) / ((km32 ^ 7.0) + (Ca_nM ^ 7.0))) + bm32))"),
                        new Expression("((V32 + ((Ca_nM ^ 2.0) * b32) + (b32 * (k32 ^ 2.0))) * ((Vm32 * (Ca_nM ^ 7.0)) + (bm32 * (Ca_nM ^ 7.0)) + (bm32 * (km32 ^ 7.0))) / (((Ca_nM ^ 2.0) + (k32 ^ 2.0)) * ((Ca_nM ^ 7.0) + (km32 ^ 7.0))))"),
                        Boolean.TRUE},

//                {new Expression("((kf * (1.0 - BrF) * PointedD_Cyt * BarbedD_Cyt / L) - (((k1 * L) + (k2 * (L ^ 2.0) * N)) * N * FAD_Cyt / (FAD_Cyt + FAT_Cyt + FADPi_Cyt)))"),
//                        new Expression("((kf * (1.0 - BrF) * PointedD_Cyt * BarbedD_Cyt / L) - (((k1 * L) + (k2 * (L ^ 2.0) * N)) * N * FAD_Cyt / (FAD_Cyt + FAT_Cyt + FADPi_Cyt)))"),
//                        Boolean.TRUE},

                {new Expression("(0.001 * ((CofFAD2_Cyt * Kf) - (1.0E9 * BarbedD_Cyt * (Cof_Cyt ^ 2.0) * Kr * PointedD_Cyt)))"),
                        new Expression("(0.001 * ((CofFAD2_Cyt * Kf) - (1.0E9 * BarbedD_Cyt * (Cof_Cyt ^ 2.0) * Kr * PointedD_Cyt)))"),
                        Boolean.TRUE},

//                {new Expression("(0.001 * ((CofFAD2_Cyt * Kf) - (1.0E9 * BarbedD_Cyt * ((0.001 * Cof_Cyt) ^ 2.0) * Kr * PointedD_Cyt)))"),
//                        new Expression("(0.001 * ((CofFAD2_Cyt * Kf) - (1.0E9 * BarbedD_Cyt * ((0.001 * Cof_Cyt) ^ 2.0) * Kr * PointedD_Cyt)))"),
//                        Boolean.TRUE},

        });
    }

    @Test
    public void simplifySymbolFactorTest_small() throws ExpressionException {
        try {
            Expression flattenedExp = origExpression.simplifyJSCL(20000, true);
            Assert.assertTrue("expected='" + expectedFlattenedExpression.infix() + "', actual='" + flattenedExp.infix() + "'",
                    expectedFlattenedExpression.compareEqual(flattenedExp));
            Assert.assertTrue("expressions not equivalent: expected='" + expectedFlattenedExpression.infix() + "', actual='" + flattenedExp.infix() + "'",
                    ExpressionUtils.functionallyEquivalent(expectedFlattenedExpression, flattenedExp, false));
            Assert.assertTrue("expressions not equivalent: expected='" + expectedFlattenedExpression.infix() + "', original='" + origExpression.infix() + "'",
                    ExpressionUtils.functionallyEquivalent(expectedFlattenedExpression, origExpression, false));
        } catch (jscl.math.Expression.ExpressionTimeoutException e){
            if (expectTimeout){
                return;
            }else{
                throw e;
            }
        }
   }

}


