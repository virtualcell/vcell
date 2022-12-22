package cbit.vcell.matrix;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class RationalExpMatrixTest {
    static class TestCase {
        final Expression[] equations;
        final String[] vars;
        final Expression[] expectedSolutions;
        final boolean bSolutionExists;
        public TestCase(Expression[] equations, String[] vars, Expression[] expectedSolutions, boolean bSolutionExists){
            this.equations = equations;
            this.vars = vars;
            this.expectedSolutions = expectedSolutions;
            this.bSolutionExists = bSolutionExists;
        }
    }

    TestCase testCase;

    public RationalExpMatrixTest(TestCase testCase){
        this.testCase = testCase;
    }

    @Parameterized.Parameters
    public static List<TestCase> testCases() throws ExpressionException {
        return Arrays.asList(
                new TestCase(
                        new Expression[]{ new Expression("x+y+z-1"), new Expression("x+y+2*z-3") },
                        new String[]{"x", "y", "z"},
                        new Expression[]{ new Expression("( - y - 1.0)"), null, new Expression(2.0) },
                        true),
                new TestCase(
                        new Expression[]{ new Expression("a*x-1") },
                        new String[]{"x"},
                        new Expression[]{ new Expression("1/a") },
                        true),
                new TestCase(
                        new Expression[]{ new Expression("a*x-1"), new Expression("a*x-1") },
                        new String[]{"x", "y"},
                        new Expression[]{ new Expression("1/a"), null },
                        true),
                new TestCase(
                        new Expression[]{ new Expression("a*x+b*y+1") },
                        new String[]{"x", "y"},
                        new Expression[]{ new Expression("(-y*b-1)/a"), null },
                        true),
                new TestCase(  // should be inconsistent - no solution
                        new Expression[]{ new Expression("a*x+b*y+1"), new Expression("a*x+b*y-1") },
                        new String[]{"x", "y"},
                        new Expression[]{ null, null },
                        false)
        );
    }

    @Test
    public void solveSystem() throws MatrixException, ExpressionException, RationalExpMatrix.NoSolutionExistsException {
        String[] vars = testCase.vars;
        Expression[] equations = testCase.equations;
        Expression[] expectedSolutions = testCase.expectedSolutions;
        if (!testCase.bSolutionExists) {
            Assert.assertThrows(RationalExpMatrix.NoSolutionExistsException.class,
                    () -> ExpressionUtils.solveLinear(equations, vars));
        } else {
            Expression[] solutions = ExpressionUtils.solveLinear(equations, vars);

            for (int varIndex = 0; varIndex < vars.length; varIndex++) {
                if (expectedSolutions[varIndex] == null) {
                    Assert.assertNull("expecting null solution for var " + vars[varIndex], solutions[varIndex]);
                } else {
                    if (solutions[varIndex] == null) {
                        Assert.fail("solution was null for var " + vars[varIndex] + ", but expecting " + expectedSolutions[varIndex].infix());
                    } else {
                        Assert.assertTrue("expected solution for " + vars[varIndex] +
                                        " to be " + expectedSolutions[varIndex].infix() + " but is " + solutions[varIndex].infix(),
                                ExpressionUtils.functionallyEquivalent(expectedSolutions[varIndex], solutions[varIndex]));
                    }
                }
            }
        }
    }
}
