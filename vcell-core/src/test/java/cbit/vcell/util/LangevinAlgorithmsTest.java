package cbit.vcell.util;

import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.simdata.LangevinPostProcessor;
import cbit.vcell.simdata.LangevinPostProcessorInput;
import cbit.vcell.simdata.LangevinPostProcessorOutput;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.ode.ODESolverResultSet;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.junit.jupiter.api.*;

import java.io.*;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class LangevinAlgorithmsTest {


    private static final double D = 1e-12;       // diffusion coefficient in m^2/s
    private static final double deltaT = 1e-3;   // time interval in seconds
    private static final double P = 0.95;        // probability
    private static final String expectedDisplacement = "0.11";

//    // the .IDA files are in vcell-core/src/test/java  /cbit/vcell/simdata
//    static File ida_0_File;
//
//    @BeforeAll
//    public static void setUp() throws IOException {
//        ida_0_File = File.createTempFile("SimID_284673710_0_", ".ida");
//        Resources.asByteSource(Resources.getResource("cbit/vcell/simdata/SimID_284673710_0_.ida"))
//                .copyTo(Files.asByteSink(ida_0_File));
//    }
//
//    @AfterAll
//    public static void tearDown() {
//
//        ida_0_File.delete();
//                if (inputStream != null) {
//                    inputStream.close();
//    }

    // given diffusion rate and time step, we compute what's the max distance a particle
    // can travel with a probability P
    // in other words, a particle has a P chance to stay within the calculated max displacement
    @Test
    public void testParticleDisplacement() throws IOException {

        double displacement = Simulation.computeDisplacement(D, deltaT, P);
        displacement = displacement * 1000000;      // m -> nm
        String calculatedDisplacement = adjust(displacement);
        System.out.println("Maximum displacement for P=" + P + ": " + calculatedDisplacement + " nanometers");
        Assertions.assertEquals(expectedDisplacement, calculatedDisplacement);
    }


    private static String adjust(double number) {
        DecimalFormat standardFormat = new DecimalFormat("0.00");
        DecimalFormat exponentialFormat = new DecimalFormat("0.00E0");

        int exponent = (int) Math.floor(Math.log10(Math.abs(number)));
        String formattedNumber;
        if (exponent < -2 || exponent > 3) {
            formattedNumber = exponentialFormat.format(number);
        } else {
            formattedNumber = standardFormat.format(number);
        }
        return(formattedNumber);
    }


}
