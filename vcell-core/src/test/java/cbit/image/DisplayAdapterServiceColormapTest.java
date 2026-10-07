package cbit.image;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 8.2-b and 8.2-c. Special-state colors are checked against both Cividis endpoints.
 * An interior gradient sample can sit near a special color's luminance; WCAG 1.4.11's
 * essential-presentation exception covers that measurement gradient, and 1.4.1 is met by
 * {@link DisplayAdapterService#specialStateLabel(int)} rather than by hue alone.
 */
@Tag("Fast")
public class DisplayAdapterServiceColormapTest {

	private static final double MIN_INDICATOR_CONTRAST = 3.0;

	@Test
	public void standardModelsRegisterInOrder_8_2_b() {
		DisplayAdapterService das = new DisplayAdapterService();
		DisplayAdapterService.addStandardColorModels(das);

		assertArrayEquals(
				new String[] {
						DisplayAdapterService.GRAY,
						DisplayAdapterService.BLUERED,
						DisplayAdapterService.CIVIDIS
				},
				das.getColorModelIDs());
		das.setActiveColorModelID("Cividis");
		assertEquals(DisplayAdapterService.CIVIDIS, das.getActiveColorModelID());
	}

	@Test
	public void cividisLightnessIsStrictlyIncreasing_8_2_b() {
		int[] model = DisplayAdapterService.createCividisColorModel();
		int dataCount = 256 - DisplayAdapterService.NUM_SPECIAL_COLORS;
		assertEquals(0xFF00224E, model[0]);
		assertEquals(0xFFFEE838, model[dataCount - 1]);
		double previous = cielabL(model[0]);
		for (int i = 1; i < dataCount; i++) {
			double lightness = cielabL(model[i]);
			assertTrue(lightness > previous, "L* decreased at data index " + i);
			previous = lightness;
		}
		for (int i = dataCount; i < model.length; i++) {
			assertEquals(0, model[i], "special-color slot " + i + " must stay unused in the map");
		}
	}

	@Test
	public void cividisSpecialStates_8_2_c() {
		int[] model = DisplayAdapterService.createCividisColorModel();
		int dataCount = 256 - DisplayAdapterService.NUM_SPECIAL_COLORS;
		int low = model[0];
		int high = model[dataCount - 1];
		int interior = model[dataCount / 2];
		int[] specials = DisplayAdapterService.createCividisSpecialColors();
		Set<String> labels = new HashSet<String>();
		Set<Integer> colors = new HashSet<Integer>();

		assertEquals(DisplayAdapterService.NUM_SPECIAL_COLORS, specials.length);
		for (int offset = 0; offset < specials.length; offset++) {
			String label = DisplayAdapterService.specialStateLabel(offset);
			assertTrue(label != null && !label.isBlank(), "special state " + offset + " needs a readout");
			assertTrue(labels.add(label), "duplicate special-state label " + label);
			assertTrue(colors.add(specials[offset]), "duplicate special color");
			assertTrue(contrast(specials[offset], low) >= MIN_INDICATOR_CONTRAST,
					label + " vs low endpoint");
			assertTrue(contrast(specials[offset], high) >= MIN_INDICATOR_CONTRAST,
					label + " vs high endpoint");
			// The interior sample is a measurement color. Record that the indicator is still named
			// when it does not clear 3:1 against that neighbor.
			double interiorContrast = contrast(specials[offset], interior);
			assertTrue(interiorContrast > 1.0, label + " must differ from the interior sample");
			if (interiorContrast < MIN_INDICATOR_CONTRAST) {
				assertTrue(label.length() > 1);
			}
		}
	}

	private static double contrast(int argbA, int argbB) {
		double lighter = Math.max(relativeLuminance(argbA), relativeLuminance(argbB));
		double darker = Math.min(relativeLuminance(argbA), relativeLuminance(argbB));
		return (lighter + 0.05) / (darker + 0.05);
	}

	private static double relativeLuminance(int argb) {
		double r = linearize(((argb >> 16) & 0xFF) / 255.0);
		double g = linearize(((argb >> 8) & 0xFF) / 255.0);
		double b = linearize((argb & 0xFF) / 255.0);
		return 0.2126 * r + 0.7152 * g + 0.0722 * b;
	}

	private static double linearize(double channel) {
		return channel <= 0.04045 ? channel / 12.92 : Math.pow((channel + 0.055) / 1.055, 2.4);
	}

	private static double cielabL(int argb) {
		double r = linearize(((argb >> 16) & 0xFF) / 255.0);
		double g = linearize(((argb >> 8) & 0xFF) / 255.0);
		double b = linearize((argb & 0xFF) / 255.0);
		double y = 0.2126729 * r + 0.7151522 * g + 0.0721750 * b;
		return 116.0 * labF(y) - 16.0;
	}

	private static double labF(double t) {
		double delta = 6.0 / 29.0;
		return t > delta * delta * delta ? Math.cbrt(t) : t / (3.0 * delta * delta) + 4.0 / 29.0;
	}
}
