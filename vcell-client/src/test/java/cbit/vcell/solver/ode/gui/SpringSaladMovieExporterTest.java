package cbit.vcell.solver.ode.gui;

import cbit.vcell.simdata.SpringSaladTrajectory;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The movie export must produce a file that actually decodes — a truncated or index-less MP4 still
 * looks like a plausible file on disk.
 */
@Tag("Fast")
public class SpringSaladMovieExporterTest {

	@BeforeAll
	static void headless() { System.setProperty("java.awt.headless", "true"); }

	@Test
	public void writesADecodableMp4() throws Exception {
		File out = new File(outputDir(), "trajectory.mp4");
		int w = 240, h = 176; // both whole macroblocks, so no rounding
		exportDemo(out, SpringSaladMovieExporter.Format.MP4, w, h);

		assertTrue(out.length() > 0, "MP4 is empty");
		// decode it back: proves the stream and the MP4 index were written, not just bytes
		Picture frame = FrameGrab.createFrameGrab(NIOUtils.readableChannel(out)).getNativeFrame();
		assertNotNull(frame, "MP4 decoded no frames");
		assertEquals(w, frame.getWidth());
		assertEquals(h, frame.getHeight());
		System.out.println("wrote " + out.getAbsolutePath() + " (" + out.length() + " bytes)");
	}

	@Test
	public void writesAnAnimatedGif() throws Exception {
		File out = new File(outputDir(), "trajectory.gif");
		exportDemo(out, SpringSaladMovieExporter.Format.ANIMATED_GIF, 200, 160);
		assertTrue(out.length() > 0, "GIF is empty");
		System.out.println("wrote " + out.getAbsolutePath() + " (" + out.length() + " bytes)");
	}

	/**
	 * A frame size that is not a whole number of macroblocks is rounded down, so the decoded frame
	 * matches the encoded one exactly and no player has to apply H.264 cropping to look right.
	 */
	@Test
	public void partialMacroblocksAreRoundedAway() throws Exception {
		File out = new File(outputDir(), "odd.mp4");
		exportDemo(out, SpringSaladMovieExporter.Format.MP4, 201, 161);
		Picture frame = FrameGrab.createFrameGrab(NIOUtils.readableChannel(out)).getNativeFrame();
		assertEquals(192, frame.getWidth());
		assertEquals(160, frame.getHeight());
	}

	private static void exportDemo(File out, SpringSaladMovieExporter.Format format, int w, int h)
			throws Exception {
		// a short trajectory keeps the test quick; the encoder path is the same
		SpringSaladTrajectory traj = SpringSaladViewerPanel.makeDemoTrajectory();
		SpringSaladViewerCanvas canvas = new SpringSaladViewerCanvas();
		canvas.setTrajectory(traj);
		SpringSaladMovieExporter.writeMovie(canvas, out, format, w, h, 10, null);
	}

	private static File outputDir() {
		File dir = new File("target/salad-movie-export");
		assertTrue(dir.mkdirs() || dir.isDirectory());
		return dir;
	}
}
