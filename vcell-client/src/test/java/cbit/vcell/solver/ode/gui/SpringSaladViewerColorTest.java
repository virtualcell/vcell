package cbit.vcell.solver.ode.gui;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.springsalad.Colors;
import org.vcell.util.springsalad.NamedColor;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * The viewer must honor every SpringSaLaD color name. An earlier hand-written subset silently
 * rendered half the palette (LIME, LIME_GREEN, PURPLE, TEAL, ...) as gray.
 */
@Tag("Fast")
public class SpringSaladViewerColorTest {

	@Test
	public void everyPaletteNameResolvesToItsOwnColor() {
		for (NamedColor nc : Colors.COLORARRAY) {
			Color resolved = SpringSaladViewerCanvas.colorForName(nc.getName());
			if (nc == Colors.BLACK) {
				continue; // lifted off pure black so it is visible against the black background
			}
			assertEquals(nc.getColor(), resolved, "wrong color for " + nc.getName());
		}
	}

	@Test
	public void limeAndLimeGreenAreNotGray() {
		Color lime = SpringSaladViewerCanvas.colorForName(Colors.LIMESTRING);
		Color limeGreen = SpringSaladViewerCanvas.colorForName(Colors.LIMEGREENSTRING);
		assertNotEquals(Color.LIGHT_GRAY, lime);
		assertNotEquals(Color.LIGHT_GRAY, limeGreen);
		assertNotEquals(lime, limeGreen, "LIME and LIME_GREEN are distinct palette entries");
	}

	@Test
	public void blackIsLiftedSoItRendersAgainstTheBlackBackground() {
		Color black = SpringSaladViewerCanvas.colorForName(Colors.BLACKSTRING);
		assertNotEquals(Color.BLACK, black);
	}

	@Test
	public void unknownAndNullNamesFallBackToGray() {
		assertEquals(Color.LIGHT_GRAY, SpringSaladViewerCanvas.colorForName("CHARTREUSE"));
		assertEquals(Color.LIGHT_GRAY, SpringSaladViewerCanvas.colorForName(null));
	}
}
