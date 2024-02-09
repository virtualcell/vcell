package org.vcell.solver.langevin;


import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.vcell.solver.langevin.LangevinPostprocessor.*;

@Tag("Fast")
public class LangevinPostprocessorTest {

    private final static String FullBondData_Content = """
        Time, r0,
        0.0,0,
        0.1,0,
        0.2,0,
        0.30000000000000004,0,
        0.4,0,
        0.5,0,
        0.6,0,
        0.7,0,
        0.7999999999999999,0,
        0.8999999999999999,0,
        0.9999999999999999,0,
        1.0999999999999999,0,
        0.0,0,
        0.0,0,
        0.0,0,
        0.0,0,
        0.0,0,
        0.0,0,
        0.0,0,
        0.0,0,
        0.0,0,
        """;

    private final static String FullCountData_Content = """
            Time, TOTAL MT0,FREE MT0,BOUND MT0,TOTAL MT1,FREE MT1,BOUND MT1,
            0.0,0,0,0,0,0,0,
            0.1,0,0,0,0,0,0,
            0.2,0,0,0,0,0,0,
            0.30000000000000004,0,0,0,0,0,0,
            0.4,0,0,0,0,0,0,
            0.5,0,0,0,0,0,0,
            0.6,0,0,0,0,0,0,
            0.7,0,0,0,0,0,0,
            0.7999999999999999,0,0,0,0,0,0,
            0.8999999999999999,0,0,0,0,0,0,
            0.9999999999999999,0,0,0,0,0,0,
            1.0999999999999999,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            """;

    private final static String FullStateCountData_Content = """
            Time, TOTAL MT0 : Site0 : state0,FREE MT0 : Site0 : state0,BOUND MT0 : Site0 : state0,TOTAL MT1 : Site0 : state0,FREE MT1 : Site0 : state0,BOUND MT1 : Site0 : state0,
            0.0,0,0,0,0,0,0,
            0.1,0,0,0,0,0,0,
            0.2,0,0,0,0,0,0,
            0.30000000000000004,0,0,0,0,0,0,
            0.4,0,0,0,0,0,0,
            0.5,0,0,0,0,0,0,
            0.6,0,0,0,0,0,0,
            0.7,0,0,0,0,0,0,
            0.7999999999999999,0,0,0,0,0,0,
            0.8999999999999999,0,0,0,0,0,0,
            0.9999999999999999,0,0,0,0,0,0,
            1.0999999999999999,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            0.0,0,0,0,0,0,0,
            """;

    private final static String expected_ida_content = """
            t:r0:TOTAL_MT0:FREE_MT0:BOUND_MT0:TOTAL_MT1:FREE_MT1:BOUND_MT1:TOTAL_MT0__Site0__state0:FREE_MT0__Site0__state0:BOUND_MT0__Site0__state0:TOTAL_MT1__Site0__state0:FREE_MT1__Site0__state0:BOUND_MT1__Site0__state0 0.0 0 0 0 0 0 0 0 0 0 0 0 0 0
            0.1 0 0 0 0 0 0 0 0 0 0 0 0 0
            0.2 0 0 0 0 0 0 0 0 0 0 0 0 0
            0.30000000000000004 0 0 0 0 0 0 0 0 0 0 0 0 0
            0.4 0 0 0 0 0 0 0 0 0 0 0 0 0
            0.5 0 0 0 0 0 0 0 0 0 0 0 0 0
            0.6 0 0 0 0 0 0 0 0 0 0 0 0 0
            0.7 0 0 0 0 0 0 0 0 0 0 0 0 0
            0.7999999999999999 0 0 0 0 0 0 0 0 0 0 0 0 0
            0.8999999999999999 0 0 0 0 0 0 0 0 0 0 0 0 0
            0.9999999999999999 0 0 0 0 0 0 0 0 0 0 0 0 0
            1.0999999999999999 0 0 0 0 0 0 0 0 0 0 0 0 0
            """;
    static Path tempDirectory;

    @BeforeAll
    public static void setUp() throws IOException {
        tempDirectory = Files.createTempDirectory("LangevinPostprocessorTestDir");

        // write FullBondData_Content to a file named FullBondData.csv in tempDirectory
        Path file1 = tempDirectory.resolve(FULL_BOND_DATA_CSV);
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file1))) {
            writer.println(FullBondData_Content);
        }
        // write FullCountData_Content to a file named FullCountData.csv in tempDirectory
        Path file2 = tempDirectory.resolve(FULL_COUNT_DATA_CSV);
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file2))) {
            writer.println(FullCountData_Content);
        }
        // write FullStateCountData_Content to a file named FullStateCountData.csv in tempDirectory
        File file3 = new File(tempDirectory.toFile().getAbsolutePath(), FULL_STATE_COUNT_DATA_CSV);
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file3.toPath()))) {
            writer.println(FullStateCountData_Content);
        }
    }

    @AfterAll
    public static void tearDown() throws IOException {
        // remove tempDirectory and its contents
        Files.walk(tempDirectory).map(Path::toFile).forEach(File::delete);
        Files.delete(tempDirectory);
    }

    private static String normalizeLineEnds(String s) {
        return s.replace("\r\n", "\n").replace('\r', '\n');
    }

    @Test
    void test_writeIdaFile() throws IOException {
        File idaFile = new File(tempDirectory.toFile().getAbsolutePath(), "output.ida");
        LangevinPostprocessor.writeIdaFile(tempDirectory, idaFile.toPath());

        String idaFileContent = Files.readString(idaFile.toPath());
        Assertions.assertEquals(normalizeLineEnds(expected_ida_content), normalizeLineEnds(idaFileContent));
    }

}
