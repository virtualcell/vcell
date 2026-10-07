package cbit.vcell.client.constants;

import java.awt.Color;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 8.2-d. {@link GuiConstants#ERROR_TEXT_COLOR} and {@link GuiConstants#WARNING_TEXT_COLOR}
 * must keep WCAG 2.1 SC 1.4.3 text contrast (>= 4.5:1) on every rendered row background.
 */
@Tag("Fast")
public class GuiConstantsContrastTest {

	private static final double MIN_TEXT_CONTRAST = 4.5;

	@Test
	public void errorAndWarningTextPassOnRowBackgrounds_8_2_d() {
		assertTextContrast(GuiConstants.ERROR_TEXT_COLOR);
		assertTextContrast(GuiConstants.WARNING_TEXT_COLOR);
	}

	private static void assertTextContrast(Color text) {
		assertContrast(text, Color.WHITE, "white");
		assertContrast(text, new Color(0xE8, 0xED, 0xFF), "#e8edff alternating row");
		assertContrast(text, new Color(0xFD, 0xFC, 0xDC), "#FDFCDC hover row");
	}

	private static void assertContrast(Color text, Color background, String label) {
		double ratio = contrast(text, background);
		assertTrue(ratio >= MIN_TEXT_CONTRAST,
				text.getRed() + "," + text.getGreen() + "," + text.getBlue()
						+ " on " + label + " is " + String.format("%.2f", ratio)
						+ ":1, below " + MIN_TEXT_CONTRAST);
	}

	/** WCAG 2.1 relative luminance and contrast ratio. */
	private static double contrast(Color a, Color b) {
		double la = luminance(a);
		double lb = luminance(b);
		double hi = Math.max(la, lb);
		double lo = Math.min(la, lb);
		return (hi + 0.05) / (lo + 0.05);
	}

	private static double luminance(Color c) {
		double r = linear(c.getRed() / 255.0);
		double g = linear(c.getGreen() / 255.0);
		double b = linear(c.getBlue() / 255.0);
		return 0.2126 * r + 0.7152 * g + 0.0722 * b;
	}

	private static double linear(double channel) {
		return channel <= 0.04045 ? channel / 12.92 : Math.pow((channel + 0.055) / 1.055, 2.4);
	}
}
