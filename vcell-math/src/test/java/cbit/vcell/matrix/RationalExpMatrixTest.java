package cbit.vcell.matrix;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;

import static cbit.vcell.matrix.RationalExpMatrix.NoSolutionExistsException;
import static cbit.vcell.parser.ExpressionUtils.functionallyEquivalent;
import static cbit.vcell.parser.ExpressionUtils.solveLinear;
import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class RationalExpMatrixTest {
    public record TestCase(
        Expression[] equations,
        String[] vars,
        Expression[] expectedSolutions,
        boolean bSolutionExists
    ) {}


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

    @ParameterizedTest
    @MethodSource("testCases")
    public void solveSystem(TestCase testCase) throws MatrixException, ExpressionException, RationalExpMatrix.NoSolutionExistsException {
        String[] vars = testCase.vars;
        Expression[] equations = testCase.equations;
        Expression[] expectedSolutions = testCase.expectedSolutions;
        if (!testCase.bSolutionExists) {
            assertThrows(NoSolutionExistsException.class, () -> solveLinear(equations, vars));
        } else {
            Expression[] solutions = ExpressionUtils.solveLinear(equations, vars);

            for (int varIndex = 0; varIndex < vars.length; varIndex++) {
                if (expectedSolutions[varIndex] == null) {
                    assertNull(solutions[varIndex], "expecting null solution for var " + vars[varIndex]);
                } else {
                    if (solutions[varIndex] == null) {
                        fail("solution was null for var " + vars[varIndex] + ", but expecting " + expectedSolutions[varIndex].infix());
                    } else {
                        assertTrue(functionallyEquivalent(expectedSolutions[varIndex], solutions[varIndex]), "expected solution for " + vars[varIndex] +
                                " to be " + expectedSolutions[varIndex].infix() + " but is " + solutions[varIndex].infix());
                    }
                }
            }
        }
    }
}
