package cbit.vcell.geometry;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the output of geometry surface generation against committed goldens.
 *
 * Why this exists: VCell's regression suites are math-generation centric and mostly non-spatial, so
 * a change to region finding, surface tessellation, Taubin smoothing or membrane adjacency could
 * alter every spatial model without a single test noticing. The goldens were generated from
 * <b>pre-merge master ({@code f35beaddcd})</b> — the behaviour that was deployed before the #2026 /
 * #2027 memory work — so they record the old behaviour rather than blessing the new one.
 *
 * What a failure means: surface generation now produces a different answer than the deployed
 * implementation did. That is not automatically wrong — an intentional improvement will fail these
 * too — but it must be a decision, with the golden updated deliberately and the diff reviewed.
 *
 * Regenerating (only after deciding the new output is correct):
 * <pre>
 *   mvn -q -pl vcell-core exec:java -Dexec.classpathScope=test \
 *       -Dexec.mainClass=cbit.vcell.geometry.GeometrySurfaceGolden
 * </pre>
 * then read the diff before committing it. See {@link GeometrySurfaceGolden} for what is captured
 * and how floating point is handled.
 */
@Tag("Fast")
public class GeometrySurfaceRegressionTest {

    static final String RESOURCE_DIR = "/cbit/vcell/geometry/surface-golden/";

    @TestFactory
    public List<DynamicTest> surfaceDescriptionsMatchTheDeployedImplementation() {
        return testsFor(GeometrySurfaceGolden.fixtures());
    }

    /** Shared with {@link GeometrySurfaceCorpusRegressionTest}; the comparison is identical. */
    static List<DynamicTest> testsFor(Map<String, GeometrySurfaceGolden.GeometryFactory> fixtures) {
        List<DynamicTest> tests = new ArrayList<>();
        for (Map.Entry<String, GeometrySurfaceGolden.GeometryFactory> entry : fixtures.entrySet()) {
            String fixture = entry.getKey();
            tests.add(DynamicTest.dynamicTest(fixture, () -> {
                String expected = readGolden(fixture);
                String actual = GeometrySurfaceGolden.describe(entry.getValue().create());
                assertEquals(expected, actual, () -> describeDifference(fixture, expected, actual));
            }));
        }
        return tests;
    }

    static void assertEveryFixtureHasAGolden(Map<String, GeometrySurfaceGolden.GeometryFactory> fixtures) {
        List<String> missing = new ArrayList<>();
        for (String fixture : fixtures.keySet()) {
            if (GeometrySurfaceRegressionTest.class.getResourceAsStream(RESOURCE_DIR + fixture + ".txt") == null) {
                missing.add(fixture);
            }
        }
        assertTrue(missing.isEmpty(),
                "fixtures with no committed golden (run GeometrySurfaceGolden.main): " + missing);
        assertFalse(fixtures.isEmpty(), "there must be fixtures to compare");
    }

    /**
     * Guards the guard. If a fixture is added without a golden, or a golden goes missing, the
     * factory above would simply produce fewer tests and the suite would still be green.
     */
    @Test
    public void everyFixtureHasAGolden() {
        assertEveryFixtureHasAGolden(GeometrySurfaceGolden.fixtures());
    }

    static String readGolden(String fixture) throws Exception {
        try (InputStream in = GeometrySurfaceRegressionTest.class
                .getResourceAsStream(RESOURCE_DIR + fixture + ".txt")) {
            assertNotNull(in, "no golden for fixture '" + fixture + "'");
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * A readable report. assertEquals on two multi-line blocks prints both in full and leaves the
     * reader to find the difference; surface descriptions are long enough that this matters.
     */
    static String describeDifference(String fixture, String expected, String actual) {
        String[] want = expected.split("\n", -1);
        String[] got = actual.split("\n", -1);
        StringBuilder sb = new StringBuilder();
        sb.append("geometry surface output changed for fixture '").append(fixture).append("'.\n");
        sb.append("This is a change against pre-merge master f35beaddcd. If it is intentional, ")
                .append("regenerate the golden with GeometrySurfaceGolden.main and review the diff.\n");
        int shown = 0;
        for (int i = 0; i < Math.max(want.length, got.length) && shown < 12; i++) {
            String w = i < want.length ? want[i] : "<missing>";
            String g = i < got.length ? got[i] : "<missing>";
            if (!w.equals(g)) {
                sb.append("  line ").append(i + 1).append('\n')
                        .append("    golden: ").append(w).append('\n')
                        .append("    actual: ").append(g).append('\n');
                shown++;
            }
        }
        if (shown == 0) {
            sb.append("  (no line differs — trailing whitespace or line endings?)\n");
        }
        return sb.toString();
    }
}
