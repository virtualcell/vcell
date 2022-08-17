package cbit.vcell.parser;

import jscl.text.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ExpressionFlattenTest {

    private Expression expectedFlattenedExpression;
    private Expression origExpression;

    public ExpressionFlattenTest(Expression origExpression, Expression expectedFlattenedExpression) {
        this.expectedFlattenedExpression = expectedFlattenedExpression;
        this.origExpression = origExpression;
    }



    @Parameterized.Parameters
    public static Collection<Expression[]> testCases() throws ExpressionException {
        return Arrays.asList(new Expression[][]{
                {new Expression("KMOLE/KMOLE"),
                        new Expression("1.0")},
                {new Expression("KMOLE/KMOLE*KMOLE/KMOLE"),
                        new Expression("1.0")},
                {new Expression("KMOLE/KMOLE*KMOLE/KMOLE2"),
                        new Expression("KMOLE/KMOLE2")},
                {new Expression("KMOLE*pow(KMOLE,-1)"),
                        new Expression("1.0")},
                {new Expression("5*KMOLE*pow(KMOLE,-1)"),
                        new Expression("5.0")},
                {new Expression("5/KMOLE*KMOLE"),
                        new Expression("5.0")},
                {new Expression("5*KMOLE*pow(KMOLE,-2)"),
                        new Expression("5*KMOLE*pow(KMOLE,-2)")},
                {new Expression("- (SCAfoldchange * KMOLE * pow(KMOLE,-1.0) * ERDensity_ER_SM * Jmax2_IP3R_flux * (1.0 - (Ca_spine / Ca_ER)) * pow((KMOLE * pow(KMOLE,-1.0) * h_ER_SM * IP3_spine * Ca_spine / (IP3_spine + dIP3_IP3R_flux) / (Ca_spine + Kact_IP3R_flux)),3.0))"),
                 new Expression("- (SCAfoldchange                           * ERDensity_ER_SM * Jmax2_IP3R_flux * (1.0 - (Ca_spine / Ca_ER)) * pow((                          h_ER_SM * IP3_spine * Ca_spine / (IP3_spine + dIP3_IP3R_flux) / (Ca_spine + Kact_IP3R_flux)),3.0))")},
        });
    }

    @Test
    public void simplifySymbolFactorTest() throws ExpressionException, ParseException {
        Expression flattenedExp = origExpression.flattenFactors("KMOLE");
        Assert.assertTrue("expected='"+expectedFlattenedExpression.infix()+"', actual='"+flattenedExp.infix()+"'", expectedFlattenedExpression.compareEqual(flattenedExp));
    }

}


