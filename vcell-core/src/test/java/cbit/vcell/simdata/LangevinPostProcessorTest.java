package cbit.vcell.simdata;

import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.solver.ode.ODESolverResultSet;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class LangevinPostProcessorTest {

    // the .IDA files are in vcell-core/src/test/resources  /cbit/vcell/simdata
    static File ida_0_File;
    static File ida_1_File;
    static File ida_2_File;

    @BeforeAll
    public static void setUp() throws IOException {
        ida_0_File = File.createTempFile("SimID_284673710_0_", ".ida");
        Resources.asByteSource(Resources.getResource("cbit/vcell/simdata/SimID_284673710_0_.ida"))
                .copyTo(Files.asByteSink(ida_0_File));
        ida_1_File = File.createTempFile("SimID_284673710_1_", ".ida");
        Resources.asByteSource(Resources.getResource("cbit/vcell/simdata/SimID_284673710_1_.ida"))
                .copyTo(Files.asByteSink(ida_1_File));
        ida_2_File = File.createTempFile("SimID_284673710_2_", ".ida");
        Resources.asByteSource(Resources.getResource("cbit/vcell/simdata/SimID_284673710_2_.ida"))
                .copyTo(Files.asByteSink(ida_2_File));
    }

    @AfterAll
    public static void tearDown() {

        ida_0_File.delete();
        ida_1_File.delete();
        ida_2_File.delete();
//                if (inputStream != null) {
//                    inputStream.close();
    }

    @Test
    public void testRead() throws IOException {

        // read the input data (3 .IDA files)
        ODESolverResultSet osrs_0 = getOdeSolverResultSet(ida_0_File);
        ODESolverResultSet osrs_1 = getOdeSolverResultSet(ida_1_File);
        ODESolverResultSet osrs_2 = getOdeSolverResultSet(ida_2_File);

        Map<Integer, ODESolverResultSet> odeSolverResultSetMap = new LinkedHashMap<>();
        odeSolverResultSetMap.put(0, osrs_0);
        odeSolverResultSetMap.put(1, osrs_1);
        odeSolverResultSetMap.put(2, osrs_2);

        LangevinPostProcessorInput lppInput = new LangevinPostProcessorInput(null, null);
        lppInput.setFailed(false);
        lppInput.setOdeSolverResultSetMap(odeSolverResultSetMap);

        // compute primary statistics
        LangevinPostProcessor lpp = new LangevinPostProcessor();
        LangevinPostProcessorOutput lppOutput = lpp.postProcessLangevinResults(lppInput);

        assertFalse(lppOutput.isFailed(), "expected to not fail");
        assertTrue(lppOutput.isMultiTrial(), "expected to be multi-trial");

        // get some timepoint for some variable
        String name = osrs_0.getColumnDescriptions()[7].getName();
        double anAverage = lppOutput.getAveragesResultSet().getRow(10)[7];      // TOTAL_MT0__Site1__state0
        double aStd = lppOutput.getStdResultSet().getRow(10)[7];
        double aMin = lppOutput.getMinResultSet().getRow(10)[7];
        double aMax = lppOutput.getMaxResultSet().getRow(10)[7];

        // compare to what's expected
        assertTrue("TOTAL_MT0__Site1__state0".contentEquals(name), "expecting column name 'TOTAL_MT0__Site1__state0', found: '" + name + "'");
        assertTrue(anAverage == 21.0 ? true : false, "expecting 21.0, found " + anAverage);
        assertTrue(aStd == 0.816496580927726 ? true : false, "expecting 0.816496580927726, found " + aStd);
        assertTrue(aMin == 20.0 ? true : false, "expecting 20.0, found " + aMin);
        assertTrue(aMax == 22.0 ? true : false, "expecting 22.0, found " + aMax);
    }

    //
    // ------- IDA file parsing / initializing ODESolverResultSet -----------------------------------------------------
    //
    private static ODESolverResultSet getOdeSolverResultSet(File idaFile) throws IOException {
        ODESolverResultSet odeSolverResultSet = new ODESolverResultSet();
        FileInputStream inputStream = null;
        inputStream = new FileInputStream(idaFile);
        if(readIDA(odeSolverResultSet, inputStream) == null) {
            return null;
        }
        return (odeSolverResultSet);
    }
    private static ODESolverResultSet readIDA(ODESolverResultSet odeSolverResultSet, FileInputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        //  read header
        String line = bufferedReader.readLine();
        if (line == null) {
            return null;
        }
        while (line.indexOf(':') > 0) {
            String name = line.substring(0, line.indexOf(':'));
            odeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(name));
            line = line.substring(line.indexOf(':') + 1);
        }
        //  read data
        while ((line = bufferedReader.readLine()) != null) {
            line = line + " ";
            double[] values = new double[odeSolverResultSet.getDataColumnCount()];
            boolean bCompleteRow = true;
            for (int i = 0; i < odeSolverResultSet.getDataColumnCount(); i++) {
                if (line.indexOf(' ')==-1) {    // here and below we assume separator is ' ', in other cases might also be '\t'
                    bCompleteRow = false;
                    break;
                }else{
                    String value = line.substring(0, line.indexOf(' ')).trim();
                    values[i] = Double.valueOf(value).doubleValue();
                    line = line.substring(line.indexOf(' ') + 1);
                }
            }
            if (bCompleteRow){
                odeSolverResultSet.addRow(values);
            }else{
                break;
            }
        }
        return odeSolverResultSet;
    }
}
