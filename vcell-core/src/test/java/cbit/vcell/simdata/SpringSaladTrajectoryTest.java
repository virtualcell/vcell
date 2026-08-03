package cbit.vcell.simdata;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class SpringSaladTrajectoryTest {

	// A minimal SpringSaLaD viewer file: header + two SCENE frames. Tabs are significant.
	private static final String VIEWER =
			"TotalTime\t0.01\n" +
			"dtimage\t0.005\n" +
			"xsize\t0.1\n" +
			"ysize\t0.1\n" +
			"z_outside\t0.01\n" +
			"z_inside\t0.09\n" +
			"\n" +
			"SCENE\n" +
			"SceneNumber\t0\tCurrentTime\t0.0\n" +
			"ID\t0\t2.000000\tYELLOW\t0.000000\t4.000000\t4.000000\n" +
			"ID\t1\t1.500000\tGREEN\t1.000000\t2.000000\t3.000000\n" +
			"Link\t0\t:\t1\n" +
			"\n" +
			"SCENE\n" +
			"SceneNumber\t1\tCurrentTime\t0.005\n" +
			"ID\t0\t2.000000\tRED\t0.100000\t4.100000\t4.100000\n" +
			"ID\t1\t1.500000\tGREEN\t1.100000\t2.100000\t3.100000\n" +
			"Link\t0\t:\t1\n" +
			"\n";

	@Test
	public void parsesHeaderFramesSitesAndLinks() throws IOException {
		SpringSaladTrajectory traj = SpringSaladTrajectory.parse(new StringReader(VIEWER));

		// header
		assertEquals(0.01, traj.getTotalTime(), 0.0);
		assertEquals(0.005, traj.getDtImage(), 0.0);
		assertEquals(0.1, traj.getXSize(), 0.0);
		assertEquals(0.1, traj.getYSize(), 0.0);
		assertEquals(0.01, traj.getZOutside(), 0.0);
		assertEquals(0.09, traj.getZInside(), 0.0);

		// two frames
		assertEquals(2, traj.getFrameCount());

		SpringSaladTrajectory.Frame f0 = traj.getFrames().get(0);
		assertEquals(0, f0.getSceneNumber());
		assertEquals(0.0, f0.getTime(), 0.0);
		assertEquals(2, f0.getSites().size());

		SpringSaladTrajectory.Site s0 = f0.getSites().get(0);
		assertEquals(0, s0.getId());
		assertEquals(2.0, s0.getRadius(), 0.0);
		assertEquals("YELLOW", s0.getColor());
		assertEquals(0.0, s0.getX(), 0.0);
		assertEquals(4.0, s0.getY(), 0.0);
		assertEquals(4.0, s0.getZ(), 0.0);

		assertEquals(1, f0.getLinks().size());
		assertArrayEquals(new int[] { 0, 1 }, f0.getLinks().get(0));

		// second frame: color/position change over time (state transition -> color change)
		SpringSaladTrajectory.Frame f1 = traj.getFrames().get(1);
		assertEquals(1, f1.getSceneNumber());
		assertEquals(0.005, f1.getTime(), 0.0);
		assertEquals("RED", f1.getSites().get(0).getColor());
		assertEquals(0.1, f1.getSites().get(0).getX(), 1e-9);
	}

	@Test
	public void rejectsNonViewerContent() {
		String notViewer = "some random\nfile contents\n";
		assertThrows(IOException.class, () -> SpringSaladTrajectory.parse(new StringReader(notViewer)));
	}

	@Test
	public void handlesTrailingSceneWithoutTrailingBlankLine() throws IOException {
		String oneFrameNoTrailingBlank =
				"TotalTime\t1.0\n" +
				"dtimage\t1.0\n" +
				"xsize\t1\nysize\t1\nz_outside\t1\nz_inside\t1\n" +
				"\n" +
				"SCENE\n" +
				"SceneNumber\t0\tCurrentTime\t0.0\n" +
				"ID\t0\t1.0\tBLUE\t0.0\t0.0\t0.0\n"; // no trailing blank line
		SpringSaladTrajectory traj = SpringSaladTrajectory.parse(new StringReader(oneFrameNoTrailingBlank));
		assertEquals(1, traj.getFrameCount());
		assertEquals(1, traj.getFrames().get(0).getSites().size());
		assertTrue(traj.getFrames().get(0).getLinks().isEmpty());
	}
}
