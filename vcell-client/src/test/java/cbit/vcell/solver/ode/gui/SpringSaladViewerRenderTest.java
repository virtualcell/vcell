package cbit.vcell.solver.ode.gui;

import GIFUtils.GIFImage;
import GIFUtils.GIFOutputStream;
import cbit.vcell.simdata.SpringSaladTrajectory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Renders sample frames of the demo trajectory to PNG (headless) so the glyph renderer can be
 * eyeballed without launching the Swing app. Output: {@code target/salad-viewer-samples/*.png}.
 */
@Tag("Fast")
public class SpringSaladViewerRenderTest {

	@BeforeAll
	static void headless() {
		System.setProperty("java.awt.headless", "true");
	}

	@Test
	public void rendersSampleFramesToPng() throws IOException {
		SpringSaladTrajectory traj = SpringSaladViewerPanel.makeDemoTrajectory();
		SpringSaladViewerCanvas canvas = new SpringSaladViewerCanvas();
		canvas.setTrajectory(traj);
		// no nudge: exercise the default oblique view the user actually sees

		File dir = new File("target/salad-viewer-samples");
		assertTrue(dir.mkdirs() || dir.isDirectory());

		int w = 760, h = 680;
		int[] frames = { 0, 30, 60, 90 };
		boolean anyNonBlank = false;
		for (int f : frames) {
			canvas.setFrameIndex(f);
			BufferedImage img = canvas.renderToImage(w, h);
			File out = new File(dir, String.format("frame_%03d.png", f));
			ImageIO.write(img, "png", out);
			anyNonBlank |= hasContent(img);
		}
		assertTrue(anyNonBlank, "rendered frames should not be entirely blank");
		System.out.println("Wrote sample frames to " + dir.getAbsolutePath());
	}

	@Test
	public void writesAnimatedGif() throws Exception {
		SpringSaladTrajectory traj = SpringSaladViewerPanel.makeDemoTrajectory();
		SpringSaladViewerCanvas canvas = new SpringSaladViewerCanvas();
		canvas.setTrajectory(traj);

		File dir = new File("target/salad-viewer-samples");
		assertTrue(dir.mkdirs() || dir.isDirectory());
		File gifFile = new File(dir, "demo.gif");

		int w = 480, h = 430, step = 3, delayCentis = 7; // ~14 fps, every 3rd frame
		GIFImage gif = null;
		int n = traj.getFrameCount();
		for (int f = 0; f < n; f += step) {
			canvas.setFrameIndex(f);
			canvas.rotate(0.0, 0.0, 0.06, 0.0); // gentle spin about vertical
			BufferedImage img = canvas.renderToImage(w, h);
			// GIFImage requires <=256 colors: quantize via an indexed image (auto palette + dither)
			BufferedImage indexed = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED);
			java.awt.Graphics ig = indexed.getGraphics();
			ig.drawImage(img, 0, 0, null);
			ig.dispose();
			int[] px = indexed.getRGB(0, 0, w, h, null, 0, w);
			if (gif == null) {
				gif = new GIFImage(px, w);
				gif.setDelay(delayCentis);
			} else {
				gif.addImage(px, w, true);
				gif.setDelay(gif.countImages() - 1, delayCentis);
			}
		}
		assertTrue(gif != null && gif.countImages() > 1, "expected multiple GIF frames");
		gif.setIterationCount(0); // loop forever
		try (GIFOutputStream out = new GIFOutputStream(new BufferedOutputStream(new FileOutputStream(gifFile)))) {
			gif.write(out);
		}
		assertTrue(gifFile.length() > 0);
		System.out.println("Wrote animated demo to " + gifFile.getAbsolutePath() + " (" + gif.countImages() + " frames)");
	}

	/** True if any pixel is meaningfully brighter than the black background (i.e. glyphs drew). */
	private static boolean hasContent(BufferedImage img) {
		for (int y = 0; y < img.getHeight(); y += 4) {
			for (int x = 0; x < img.getWidth(); x += 4) {
				int rgb = img.getRGB(x, y) & 0xFFFFFF;
				int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
				if (r + g + b > 60) return true;
			}
		}
		return false;
	}
}
