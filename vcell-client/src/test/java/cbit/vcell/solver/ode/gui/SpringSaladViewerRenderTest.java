package cbit.vcell.solver.ode.gui;

import cbit.vcell.simdata.SpringSaladTrajectory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
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
		// nudge to a 3/4 view so depth/shading is visible
		canvas.rotate(0.0, 0.0, 0.30, 0.22);

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
