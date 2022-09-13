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
                        new Expression("5.0 / KMOLE")},
                {new Expression("-((-x)^2)"),
                        new Expression("-(x^2)")},
                {new Expression("((-x)^2)"),
                        new Expression("(x^2)")},
                {new Expression("((Kf_dimerization * pow(EGFR_active,2.0)) - (Kr_dimerization * EGFR_dimer))"),
                        new Expression(" - ( - ((EGFR_active ^ 2.0) * Kf_dimerization) + (EGFR_dimer * Kr_dimerization))")}
        });
    }

    @Test
    public void simplifySymbolFactorMatchesExpected() throws ExpressionException, ParseException {
        Expression flattenedExp = origExpression.flattenFactors("KMOLE");
        Assert.assertTrue("didn't compare equal, expected='"+expectedFlattenedExpression.infix()+"', actual='"+flattenedExp.infix()+"'",
                expectedFlattenedExpression.compareEqual(flattenedExp));
        Assert.assertTrue("expected flattened not equivalent, expected='"+expectedFlattenedExpression.infix()+"', original='"+origExpression.infix()+"'",
                ExpressionUtils.functionallyEquivalent(expectedFlattenedExpression,origExpression,false));
    }

    @Test
    public void simplifySymbolFactorIsEquivalent() throws ExpressionException, ParseException {
        Expression flattenedExp = origExpression.flattenFactors("KMOLE");
        Assert.assertTrue("not equivalent, expected='"+origExpression.infix()+"', actual='"+flattenedExp.infix()+"'",
                ExpressionUtils.functionallyEquivalent(origExpression,flattenedExp,false));
    }

}


