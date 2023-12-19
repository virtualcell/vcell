package cbit.vcell.solver;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;

import java.util.List;

import static cbit.vcell.solver.ConstantArraySpec.TYPE_INTERVAL;
import static cbit.vcell.solver.ConstantArraySpec.TYPE_LIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class MathOverridesTest {

    @Test
    public void test_constantArraySpec_interval_simple_linear(){
        ConstantArraySpec expected = ConstantArraySpec.createIntervalSpec(
                "name",
                "4",
                "8",
                3, false);

        String description = "4 to 8, 3 values";
        String expected_description = "4.0 to 8.0, 3 values";
        ConstantArraySpec cas = ConstantArraySpec.createFromString("name", description, ConstantArraySpec.TYPE_INTERVAL);
        assertEquals(expected.toString(), cas.toString());
        assertEquals(expected_description, cas.toDatabaseString());
    }

    @Test
    public void test_constantArraySpec_interval_complex_linear(){
        ConstantArraySpec expected = ConstantArraySpec.createIntervalSpec(
                "name",
                "(KMOLE * (1.0 * pow(KMOLE,1.0)))",
                "((2.0 * KMOLE) * (1.0 * pow(KMOLE,1.0)))",
                2, false);

        String description = "(KMOLE * (1.0 * pow(KMOLE,1.0))) to ((2.0 * KMOLE) * (1.0 * pow(KMOLE,1.0))), 2 values";
        ConstantArraySpec cas = ConstantArraySpec.createFromString("name", description, ConstantArraySpec.TYPE_INTERVAL);
        assertEquals(expected.toString(), cas.toString());
        assertEquals(description, cas.toDatabaseString());
    }

    @Test
    public void test_constantArraySpec_interval_simple_log(){
        ConstantArraySpec expected = ConstantArraySpec.createIntervalSpec(
                "name",
                "4",
                "8",
                3, true);

        String description = "4 to 8, log, 3 values";
        String expected_description = "4.0 to 8.0, log, 3 values";
        ConstantArraySpec cas = ConstantArraySpec.createFromString("name", description, ConstantArraySpec.TYPE_INTERVAL);
        assertEquals(expected.toString(), cas.toString());
        assertEquals(expected_description, cas.toDatabaseString());
    }

    @Test
    public void test_constantArraySpec_interval_complex_log(){
        ConstantArraySpec expected = ConstantArraySpec.createIntervalSpec(
                "name",
                "(KMOLE * (1.0 * pow(KMOLE,1.0)))",
                "((2.0 * KMOLE) * (1.0 * pow(KMOLE,1.0)))",
                2, true);

        String description = "(KMOLE * (1.0 * pow(KMOLE,1.0))) to ((2.0 * KMOLE) * (1.0 * pow(KMOLE,1.0))), log, 2 values";
        ConstantArraySpec cas = ConstantArraySpec.createFromString("name", description, ConstantArraySpec.TYPE_INTERVAL);
        assertEquals(expected.toString(), cas.toString());
        assertEquals(description, cas.toDatabaseString());
    }


    @Test
    public void test_constantArraySpec_list_numbers_old() throws ExpressionException {
        ConstantArraySpec expected = ConstantArraySpec.createListSpec("name",
                new String[] {
                        "4",
                        "6",
                        "8" });

        String old_style_description = "4, 6, 8";
        String new_style_description = "\"4.0\", \"6.0\", \"8.0\"";
        ConstantArraySpec cas = ConstantArraySpec.createFromString("name", old_style_description, ConstantArraySpec.TYPE_LIST);
        assertEquals(expected.toString(), cas.toString());
        assertEquals(new_style_description, cas.toDatabaseString());
    }

    @Test
    public void test_constantArraySpec_list_numbers_quoted() throws ExpressionException {
        ConstantArraySpec expected = ConstantArraySpec.createListSpec("name",
                new String[] {
                        "4",
                        "6",
                        "8" });

        String description = "\"4\", \"6\", \"8\"";
        String expected_description = "\"4.0\", \"6.0\", \"8.0\"";
        ConstantArraySpec cas = ConstantArraySpec.createFromString("name", description, ConstantArraySpec.TYPE_LIST);
        assertEquals(expected.toString(), cas.toString());
        assertEquals(expected_description, cas.toDatabaseString());
    }

    @Test
    public void test_constantArraySpec_list_expressions_quoted() throws ExpressionException {
        ConstantArraySpec expected = ConstantArraySpec.createListSpec("name",
                new String[] {
                    "(KMOLE * (1.0 * pow(KMOLE,1.0)))",
                    "((2.0 * KMOLE) * (1.0 * pow(KMOLE,1.0)))",
                    "((3.0 * KMOLE) * (1.0 * pow(KMOLE,1.0)))"});

        String description = "\"(KMOLE * (1.0 * pow(KMOLE,1.0)))\", \"((2.0 * KMOLE) * (1.0 * pow(KMOLE,1.0)))\", \"((3.0 * KMOLE) * (1.0 * pow(KMOLE,1.0)))\"";
        ConstantArraySpec cas = ConstantArraySpec.createFromString("name", description, ConstantArraySpec.TYPE_LIST);
        assertEquals(expected.toString(), cas.toString());
        assertEquals(description, cas.toDatabaseString());
    }

    @Test
    public void test_database_serialization() throws DataAccessException, ExpressionException {
        final String database_string = "MathOverrides {\n" +
                "   ConstantArraySpec s1_Count_initCount 1001 (Size_c0 / KMOLE) to (5.0 * Size_c0 / KMOLE), 3 values;" +
                "   ConstantArraySpec s0_Count_initCount 1000 \"KMOLE\", \"(2.0 * KMOLE)\";\n" +
                "}\n";
        CommentStringTokenizer mathOverridesTokens = new CommentStringTokenizer(database_string);
        List<MathOverrides.Element> elements = MathOverrides.parseOverrideElementsFromVCML(mathOverridesTokens);
        assertEquals(2, elements.size());
        {
            // make sure interval override is correct (for s1_Count_initCount)
            ConstantArraySpec intervalConstantArraySpec = elements.get(0).getSpec();
            assertEquals(TYPE_INTERVAL, intervalConstantArraySpec.getType());
            Expression expectedMinExp = new Expression("(Size_c0 / KMOLE)");
            Expression expectedMaxExp = new Expression("(5.0 * Size_c0 / KMOLE)");
            int numValues = 3;
            boolean logInterval = false;
            ConstantArraySpec expected = ConstantArraySpec.createIntervalSpec(
                    "s1_Count_initCount", expectedMinExp.infix(), expectedMaxExp.infix(), numValues, logInterval);
            assertTrue(expected.compareEqual(intervalConstantArraySpec), "interval specs didn't match");
            String expectedDescription = "(Size_c0 / KMOLE) to (5.0 * Size_c0 / KMOLE), 3 values";
            assertEquals(expectedDescription, intervalConstantArraySpec.toDatabaseString());
        }
        {
            // make sure list override is correct (for s0_Count_initCount)
            ConstantArraySpec listConstantArraySpec = elements.get(1).getSpec();
            assertEquals(TYPE_LIST, listConstantArraySpec.getType());
            Expression expectedItem1 = new Expression("KMOLE");
            Expression expectedItem2 = new Expression("(2.0 * KMOLE)");
            ConstantArraySpec expected = ConstantArraySpec.createListSpec("s0_Count_initCount", new String[] { expectedItem1.infix(), expectedItem2.infix() });
            assertTrue(expected.compareEqual(listConstantArraySpec), "list specs didn't match");
            String expectedDescription = "\"KMOLE\", \"(2.0 * KMOLE)\"";
            assertEquals(expectedDescription, listConstantArraySpec.toDatabaseString());
        }
        // make sure generated database text is same as above (for multiple overrides)
        MathOverrides mathOverrides = new MathOverrides(null, new CommentStringTokenizer(database_string));
        String generated_database_string = mathOverrides.getVCML();
        assertEquals(database_string, generated_database_string);
    }

 }
