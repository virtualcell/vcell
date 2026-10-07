package cbit.image;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Retained BlueRed default. The 248 data colors stay the historical map.
 * Special-state indicators are checked against the colors they sit beside in the legend
 * (a black gap, and the neighboring swatch) and, for the two range ends, against the
 * data color those pixels are drawn next to.
 */
@Tag("Fast")
public class BlueRedSpecialColorTest {

	private static final double MIN_INDICATOR = 3.0;
	private static final double MIN_TEXT = 4.5;

	@Test
	public void dataGradientEndpointsStayBlueAndRed() {
		int[] model = DisplayAdapterService.createBlueRedColorModel();
		int dataCount = 256 - DisplayAdapterService.NUM_SPECIAL_COLORS;
		assertEquals(new Color(0, 0, 128).getRGB(), model[0]);
		assertEquals(new Color(255, 0, 0).getRGB(), model[dataCount - 1]);
		for (int i = dataCount; i < model.length; i++) {
			assertEquals(0, model[i]);
		}
	}

	@Test
	public void legendIndicatorsClearTheirNeighborsAndCarryText() {
		int[] model = DisplayAdapterService.createBlueRedColorModel();
		int dataCount = 256 - DisplayAdapterService.NUM_SPECIAL_COLORS;
		int low = model[0];
		int high = model[dataCount - 1];
		int[] specials = DisplayAdapterService.createBlueRedSpecialColors();
		int gap = Color.BLACK.getRGB();
		int[] legend = new int[] {
				DisplayAdapterService.BELOW_MIN_COLOR_OFFSET,
				DisplayAdapterService.ABOVE_MAX_COLOR_OFFSET,
				DisplayAdapterService.NAN_COLOR_OFFSET,
				DisplayAdapterService.NOT_IN_DOMAIN_COLOR_OFFSET,
				DisplayAdapterService.NO_RANGE_COLOR_OFFSET
		};
		Set<Integer> colors = new HashSet<Integer>();
		Set<String> labels = new HashSet<String>();

		for (int offset = 0; offset < specials.length; offset++) {
			assertTrue(colors.add(specials[offset]), "duplicate special color");
			String label = DisplayAdapterService.specialStateLabel(offset);
			assertTrue(labels.add(label));
			int text = DisplayAdapterService.contrastingTextRgb(specials[offset]);
			assertTrue(DisplayAdapterService.contrastRatio(specials[offset], text) >= MIN_TEXT, label + " text");
		}
		for (int offset : legend) {
			assertTrue(DisplayAdapterService.contrastRatio(specials[offset], gap) >= MIN_INDICATOR,
					DisplayAdapterService.specialStateLabel(offset) + " vs legend gap");
		}
		for (int i = 0; i < legend.length - 1; i++) {
			assertTrue(DisplayAdapterService.contrastRatio(specials[legend[i]], specials[legend[i + 1]]) >= MIN_INDICATOR,
					"legend neighbors " + i);
		}
		assertTrue(DisplayAdapterService.contrastRatio(specials[DisplayAdapterService.BELOW_MIN_COLOR_OFFSET], low) >= MIN_INDICATOR);
		assertTrue(DisplayAdapterService.contrastRatio(specials[DisplayAdapterService.ABOVE_MAX_COLOR_OFFSET], high) >= MIN_INDICATOR);
	}
}
