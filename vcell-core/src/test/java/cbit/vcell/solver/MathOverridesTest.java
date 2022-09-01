package cbit.vcell.solver;

import cbit.vcell.parser.ExpressionException;
import org.junit.Assert;
import org.junit.Test;

public class MathOverridesTest {

    @Test
    public void test_constantArraySpec_interval_simple_linear(){
        ConstantArraySpec expected = ConstantArraySpec.createIntervalSpec(
                "name",
                "4",
                "8",
                3, false);

        String description = "4 to 8, 3 values";
        ConstantArraySpec cas = ConstantArraySpec.createFromString("name", description, ConstantArraySpec.TYPE_INTERVAL);
        Assert.assertEquals(expected.toString(), cas.toString());
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
        Assert.assertEquals(expected.toString(), cas.toString());
    }

    @Test
    public void test_constantArraySpec_interval_simple_log(){
        ConstantArraySpec expected = ConstantArraySpec.createIntervalSpec(
                "name",
                "4",
                "8",
                3, true);

        String description = "4 to 8, log, 3 values";
        ConstantArraySpec cas = ConstantArraySpec.createFromString("name", description, ConstantArraySpec.TYPE_INTERVAL);
        Assert.assertEquals(expected.toString(), cas.toString());
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
        Assert.assertEquals(expected.toString(), cas.toString());
    }


    @Test
    public void test_constantArraySpec_list_simple_linear() throws ExpressionException {
        ConstantArraySpec expected = ConstantArraySpec.createListSpec("name",
                new String[] {
                        "4",
                        "6",
                        "8" });

        String description = "4, 6, 8";
        ConstantArraySpec cas = ConstantArraySpec.createFromString("name", description, ConstantArraySpec.TYPE_LIST);
        Assert.assertEquals(expected.toString(), cas.toString());
    }

    @Test
    public void test_constantArraySpec_list_complex_linear() throws ExpressionException {
        ConstantArraySpec expected = ConstantArraySpec.createListSpec("name",
                new String[] {
                    "(KMOLE * (1.0 * pow(KMOLE,1.0)))",
                    "((2.0 * KMOLE) * (1.0 * pow(KMOLE,1.0)))",
                    "((3.0 * KMOLE) * (1.0 * pow(KMOLE,1.0)))"});

        String description = "(KMOLE * (1.0 * pow(KMOLE,1.0))); ((2.0 * KMOLE) * (1.0 * pow(KMOLE,1.0))); ((3.0 * KMOLE) * (1.0 * pow(KMOLE,1.0)))";
        ConstantArraySpec cas = ConstantArraySpec.createFromString("name", description, ConstantArraySpec.TYPE_LIST);
        Assert.assertEquals(expected.toString(), cas.toString());
    }

 }
