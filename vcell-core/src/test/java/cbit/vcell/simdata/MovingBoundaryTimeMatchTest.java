package cbit.vcell.simdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * A MovingBoundary simulation's saved times reach the reader by two routes that do not agree.
 *
 * The .log file, which is where the caller's requested time comes from, is written by the solver
 * with C's "%g" -- six significant digits. The time attributes inside the HDF5 file are full
 * doubles. So a request for "0.122269" has to find a group whose stored time is 0.1222685102809653.
 *
 * The reader used to require the two to agree within 1e-8 absolute. On the 386-timepoint simulation
 * this was diagnosed on, that matched 20 of 386 times: only the earliest, where six digits happen to
 * be enough. Every other time failed with "No time group found", which reads like missing data
 * rather than a rounding problem, and it took reading the .log and the .h5 side by side to see why.
 *
 * The values below are real: taken from SimID_109369415, whose logged times were compared against
 * the time attributes in its own HDF5 file.
 */
@Tag("Fast")
public class MovingBoundaryTimeMatchTest {

	/** {logged (what the caller asks for), stored (what the file holds)} */
	private static final double[][] LOGGED_VS_STORED = {
			{0.0,        0.0},
			{0.00260146, 0.002601457665552453},
			{0.0104058,  0.010405830662209813},
			{0.122269,   0.1222685102809653},
			{0.127471,   0.1274714256120702},
			{0.280957,   0.28095742787966493},
			{0.28616,    0.28616034321076983},
			{0.312175,   0.31217491986629436},
			{0.702394,   0.7023935696991623},
	};

	/**
	 * The point of the fix: every one of these is a time a user can select in the viewer.
	 */
	@Test
	public void everyLoggedTimeMatchesTheTimeStoredInTheFile() {
		for (double[] pair : LOGGED_VS_STORED) {
			double logged = pair[0], stored = pair[1];
			double delta = Math.abs(stored - logged);
			assertTrue(delta <= MovingBoundarySimDataReader.timeMatchTolerance(logged),
					"logged " + logged + " should match stored " + stored + " (differ by " + delta
							+ ", tolerance " + MovingBoundarySimDataReader.timeMatchTolerance(logged) + ")");
		}
	}

	/**
	 * The regression this replaces, kept as a statement of what was wrong: under the old fixed 1e-8
	 * bound all but the two earliest of these were unreadable.
	 */
	@Test
	public void theOldFixedToleranceWouldHaveRejectedMostOfThem() {
		int rejected = 0;
		for (double[] pair : LOGGED_VS_STORED) {
			if (Math.abs(pair[1] - pair[0]) >= 1e-8) {
				rejected++;
			}
		}
		assertEquals(7, rejected, "7 of these 9 real times were unreadable before the fix");
	}

	/**
	 * The tolerance still has to be small enough that it cannot reach a neighbouring saved time.
	 * Saved times in this simulation are ~1.04e-3 apart.
	 */
	@Test
	public void theToleranceStaysWellInsideTheSpacingOfSavedTimes() {
		double smallestGapBetweenSavedTimes = 1.04e-3;
		for (double[] pair : LOGGED_VS_STORED) {
			assertTrue(MovingBoundarySimDataReader.timeMatchTolerance(pair[0]) < smallestGapBetweenSavedTimes / 10,
					"tolerance at t=" + pair[0] + " must stay an order of magnitude inside the gap"
							+ " between saved times, or it could select the wrong one");
		}
	}

	/** t=0 is exact in both routes, and must not be handed a tolerance of zero. */
	@Test
	public void zeroIsMatchedExactlyAndKeepsAPositiveTolerance() {
		assertTrue(MovingBoundarySimDataReader.timeMatchTolerance(0.0) > 0.0);
		assertTrue(Math.abs(0.0 - 0.0) <= MovingBoundarySimDataReader.timeMatchTolerance(0.0));
	}

	/** A time that belongs to no saved point must still be rejected. */
	@Test
	public void aTimeBetweenTwoSavedPointsIsStillRejected() {
		double savedTime = 0.1222685102809653;
		double halfwayToTheNext = savedTime + 5.2e-4;
		assertTrue(Math.abs(savedTime - halfwayToTheNext) > MovingBoundarySimDataReader.timeMatchTolerance(halfwayToTheNext),
				"nearest-match must not silently return a different time's data");
	}
}
