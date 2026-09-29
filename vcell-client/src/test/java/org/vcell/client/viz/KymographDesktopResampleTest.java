package org.vcell.client.viz;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.vcell.util.Coordinate;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.VCDataJobID;

import cbit.vcell.geometry.CurveSelectionInfo;
import cbit.vcell.geometry.PolyLine;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.SpatialSelectionVolume;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solvers.CartesianMesh;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * The desktop kymograph's numbers, for the cross-check of plan §6.3, which the desktop GUI cannot be driven
 * to give in a test: this builds what the desktop builds for a volume line and shows —
 * <ol>
 * <li>{@code PDEDataViewer.showKymograph}: {@code new SpatialSelectionVolume(new CurveSelectionInfo(new
 * PolyLine(vertices)), VOLUME, mesh).getIndexSamples(0, 1)};</li>
 * <li>{@code KymographPanel.initDataManagerVariable}: one {@link TimeSeriesJobSpec} over those samples with
 * their membrane-crossing indices, every saved time;</li>
 * <li>{@code KymographPanel.initStandAloneTimeSeries_private}: the nearest-neighbour resampling to evenly
 * spaced distances that its image and its Copy output show ({@link #resample}, copied line for line).</li>
 * </ol>
 * and checks the result against the golden files in {@code kymo/}, which the browser test
 * ({@code webapp-viewer/test/test_kymograph.py}) compares with the viewer's {@code Desktop CSV} export. The two
 * together are the desktop cross-check: the viewer's export equals what the desktop would show, value for value.
 * <p>
 * {@code -Dvcell.kymographGolden.write=<dir>} rewrites the golden files into {@code <dir>} instead of checking.
 */
@Tag("Fast")
@ResourceLock("fieldViewerServer")
public class KymographDesktopResampleTest {

	/** The lines of the cross-check, as a viewer user types them: sim, variable, vertices. */
	static final String[][] CASES = {
			{ FieldViewerServerFvTest.SIM_2D, "Dex", "-10,-9;9,8" },
			{ FieldViewerServerFvTest.SIM_2D, "Dex", "-10,0;0,0;3,10" },
			{ FieldViewerServerFvTest.SIM_3D, "s0", "0,0.3,2;4,3.4,2" },
			{ FieldViewerServerFvTest.SIM_3D, "s0", "0,2,2;4,2,2" },
	};

	private Path root;
	private VCDataManager dataManager;

	@BeforeEach
	public void setup() throws Exception {
		root = Files.createTempDirectory("KymographDesktopResampleTest_");
		dataManager = FieldViewerServerFvTest.stageFvFixtures(root);
	}

	@AfterEach
	public void teardown() throws Exception {
		FieldViewerServer.stop();
		try (Stream<Path> walk = Files.walk(root)) {
			walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}
	}

	/** The golden file's name for a case: {@code <sim>-<var>-<n>.csv}. */
	static String goldenName(int c) {
		return CASES[c][0] + "-" + CASES[c][1] + "-" + c + ".csv";
	}

	/**
	 * {@code KymographPanel.initStandAloneTimeSeries_private}'s resampling, copied: {@code timeSeriesOrig[0]}
	 * the times, {@code timeSeriesOrig[1 + i]} sample i's values; returns {@code {distances, row 0, row 1, …}}.
	 */
	static double[][] resample(double[][] timeSeriesDataOrig, double[] accumDistancesDataOrig) {
		double[] currentTimes = timeSeriesDataOrig[0];
		int RESAMP_SIZE = timeSeriesDataOrig.length - 1;
		double[] rawValues = new double[currentTimes.length * RESAMP_SIZE];
		double incr = accumDistancesDataOrig[accumDistancesDataOrig.length - 1] / (double) (RESAMP_SIZE - 1);
		double[] currentDistances = new double[RESAMP_SIZE];
		for (int j = 0; j < currentTimes.length; j += 1) {
			int sourceIndex = 0;
			double currentDistance = 0;
			for (int k = 0; k < RESAMP_SIZE; k += 1) {
				while (currentDistance > accumDistancesDataOrig[sourceIndex + 1]) {
					sourceIndex += 1;
				}
				double subShort = currentDistance - accumDistancesDataOrig[sourceIndex];
				double subLong = accumDistancesDataOrig[sourceIndex + 1] - accumDistancesDataOrig[sourceIndex];
				double proportion = subShort / subLong;
				double value = timeSeriesDataOrig[1 + sourceIndex + (proportion > .5 ? 1 : 0)][j];
				rawValues[(j * RESAMP_SIZE) + (k)] = value;
				currentDistances[k] = currentDistance;
				currentDistance += incr;
				if (currentDistance > accumDistancesDataOrig[accumDistancesDataOrig.length - 1]) {
					currentDistance = accumDistancesDataOrig[accumDistancesDataOrig.length - 1];
				}
			}
		}
		double[][] out = new double[1 + currentTimes.length][];
		out[0] = currentDistances;
		for (int j = 0; j < currentTimes.length; j++) {
			out[1 + j] = java.util.Arrays.copyOfRange(rawValues, j * RESAMP_SIZE, (j + 1) * RESAMP_SIZE);
		}
		return out;
	}

	/** The desktop's kymograph of one case, resampled: {@code Distances,…} then {@code <time>,…} per saved time. */
	private List<String> desktopKymograph(int c) throws Exception {
		VCSimulationDataIdentifier vcdID = FieldViewerServerFvTest.vcdID(CASES[c][0]);
		CartesianMesh mesh = dataManager.getMesh(vcdID);
		String[] entries = CASES[c][2].split(";");
		Coordinate[] coords = new Coordinate[entries.length];
		for (int i = 0; i < entries.length; i++) {
			String[] p = entries[i].split(",");
			coords[i] = new Coordinate(Double.parseDouble(p[0]), Double.parseDouble(p[1]), p.length > 2 ? Double.parseDouble(p[2]) : 0);
		}
		SpatialSelection.SSHelper ssh = new SpatialSelectionVolume(new CurveSelectionInfo(new PolyLine(coords)),
				VariableType.VOLUME, mesh).getIndexSamples(0.0, 1.0);
		double[] times = dataManager.getDataSetTimes(vcdID);
		TimeSeriesJobSpec spec = new TimeSeriesJobSpec(new String[] { CASES[c][1] }, new int[][] { ssh.getSampledIndexes() },
				ssh.getMembraneIndexesInOut() != null ? new int[][] { ssh.getMembraneIndexesInOut() } : null,
				times[0], 1, times[times.length - 1], VCDataJobID.createVCDataJobID(vcdID.getOwner(), true));
		TSJobResultsNoStats results = (TSJobResultsNoStats) dataManager.getTimeSeriesValues(
				new OutputContext(new AnnotatedFunction[0]), vcdID, spec);
		double[][] timeSeries = results.getTimesAndValuesForVariable(CASES[c][1]);
		double[][] r = resample(timeSeries, ssh.getWorldCoordinateLengths());
		List<String> lines = new ArrayList<>();
		lines.add("Distances," + join(r[0]));
		for (int j = 0; j < timeSeries[0].length; j++) {
			lines.add(timeSeries[0][j] + "," + join(r[1 + j]));
		}
		return lines;
	}

	private static String join(double[] values) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			sb.append(i > 0 ? "," : "").append(values[i]);
		}
		return sb.toString();
	}

	@Test
	public void theGoldenFilesAreWhatTheDesktopShows() throws Exception {
		String writeTo = System.getProperty("vcell.kymographGolden.write");
		for (int c = 0; c < CASES.length; c++) {
			List<String> lines = desktopKymograph(c);
			if (writeTo != null) {
				Files.write(Path.of(writeTo, goldenName(c)), (String.join("\n", lines) + "\n").getBytes(StandardCharsets.UTF_8));
				continue;
			}
			try (InputStream in = getClass().getResourceAsStream("kymo/" + goldenName(c))) {
				Assertions.assertNotNull(in, goldenName(c));
				List<String> golden = List.of(new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\n"));
				Assertions.assertEquals(golden.size(), lines.size(), goldenName(c));
				for (int i = 0; i < lines.size(); i++) {
					String[] want = golden.get(i).split(",");
					String[] got = lines.get(i).split(",");
					Assertions.assertEquals(want.length, got.length, goldenName(c) + " line " + i);
					Assertions.assertEquals(want[0], got[0]);
					for (int k = 1; k < want.length; k++) {
						Assertions.assertEquals(Double.parseDouble(want[k]), Double.parseDouble(got[k]), 0.0,
								goldenName(c) + " line " + i + " column " + k);
					}
				}
			}
		}
	}

	/** The resampling on a hand-made line: evenly spaced distances, each the nearer sample, ties to the left. */
	@Test
	public void theResamplingIsNearestNeighbourAtEvenSpacing() {
		double[][] series = { { 0.0 }, { 10 }, { 20 }, { 30 }, { 40 } };
		double[] accum = { 0, 1, 1, 3 }; // a membrane pair at distance 1
		double[][] r = resample(series, accum);
		Assertions.assertArrayEquals(new double[] { 0, 1, 2, 3 }, r[0], 1e-15);
		// d = 0: sample 0. d = 1: still in sample 0's stretch [0, 1] at proportion 1, so the right end, sample 1
		// (the left side of the membrane pair). d = 2: halfway along [1, 3], a tie, so the left end, sample 2.
		// d = 3: sample 3.
		Assertions.assertArrayEquals(new double[] { 10, 20, 30, 40 }, r[1], 0.0);
	}
}
