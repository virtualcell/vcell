package cbit.vcell.graph;

import cbit.image.DisplayAdapterService;
import cbit.vcell.client.constants.GuiConstants;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class MolecularTypeErrorMarkTest {

	@Test
	public void errorMarkPaintsTheWordInErrorInkOnWhite() {
		BufferedImage image = new BufferedImage(80, 28, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		MolecularTypeLargeShape.paintErrorMark(graphics, 2, 2);
		graphics.dispose();

		int errorRgb = GuiConstants.ERROR_TEXT_COLOR.getRGB();
		int hits = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				if (image.getRGB(x, y) == errorRgb) {
					hits++;
				}
			}
		}
		assertTrue(hits > 8, "error ink pixels: " + hits);
		double ratio = DisplayAdapterService.contrastRatio(errorRgb, 0xFFFFFF);
		assertTrue(ratio >= 4.5, ratio + ":1");
	}
}
