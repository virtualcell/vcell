package org.vcell.cli.run.results;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.Constant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.ConstantArraySpec;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.data.Dimension;
import org.vcell.util.document.Version;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class SedmlDataGeneratorsTest {

    /** A simulation scanning kOn over 3 values and Ka over 2: sorted, Ka (2 values) is outermost. */
    private static Simulation twoParameterScan() throws Exception {
        MathModel mathModel = new MathModel(null);
        MathDescription math = new MathDescription("math");
        math.setGeometry(new Geometry((Version) null, 0));
        math.addVariable(new Constant("kOn", new Expression(1.0)));
        math.addVariable(new Constant("Ka", new Expression(1.0)));
        mathModel.setMathDescription(math);
        Simulation simulation = new Simulation(math, mathModel);
        simulation.getMathOverrides().putConstantArraySpec(ConstantArraySpec.createListSpec("kOn", new String[]{"10", "20", "30"}));
        simulation.getMathOverrides().putConstantArraySpec(ConstantArraySpec.createListSpec("Ka", new String[]{"0.5", "5"}));
        return simulation;
    }

    @Test
    public void scanDimensionsFollowJobOrder() throws Exception {
        MathOverrides overrides = twoParameterScan().getMathOverrides();
        List<Dimension.Numeric> scans = SedmlDataGenerators.scanDimensions(overrides);
        assertEquals(List.of(new Dimension.Numeric("Ka", new double[]{0.5, 5}), new Dimension.Numeric("kOn", new double[]{10, 20, 30})), scans,
                "sorted constant names, each with its own values");

        // job j sits at row-major position j of [Ka, kOn], exactly as MathOverrides numbers the jobs
        for (int ka = 0; ka < 2; ka++)
            for (int kOn = 0; kOn < 3; kOn++) {
                MathOverrides.ScanIndex job = new MathOverrides.ScanIndex(ka * 3 + kOn);
                assertEquals(scans.get(0).values()[ka], overrides.getActualExpression("Ka", job).evaluateConstant());
                assertEquals(scans.get(1).values()[kOn], overrides.getActualExpression("kOn", job).evaluateConstant());
            }
    }
}
