package cbit.vcell.client.laf;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

/**
 * VCell's own UI colors. Light themes get the colors VCell has always used; dark themes get a darker
 * equivalent that keeps the hue. Each theme-dependent color re-evaluates itself when the look and feel
 * changes, so a component given one when it was built follows later theme switches too.
 */
public final class VCellColors {
	private static final Color LIGHT_TABLE_HEADER = new Color(0xB9C9FE);
	private static final Color LIGHT_LAVENDER = new Color(166, 166, 255);
	private static final Color LIGHT_SOFT_SELECTION = new Color(214, 234, 248);
	private static final Color LIGHT_UNEDITABLE = new Color(0x964B00);
	private static final Color LIGHT_VALUE_TEXT = Color.blue;
	private static final Color LIGHT_TITLE_TEXT = new Color(0, 0, 128);
	private static final double MIN_TEXT_CONTRAST = 4.5; // WCAG AA for normal text
	private static final Color LIGHT_WARNING_TEXT = new Color(0x8B0000);
	private static final Color LIGHT_ERROR_BACKGROUND = new Color(255, 170, 170);
	private static final Color SHAPE_CANVAS = Color.white;
	private static final Color VIEW_ONLY_SHAPE_CANVAS = new Color(0xE0E0E0);

	private VCellColors() {}

	/** Background of ScrollTable column headers. */
	public static Color getTableHeaderBackground() { return themeColor(() -> tint(LIGHT_TABLE_HEADER, 0.25f)); }

	/** VCell's lavender: StyleTable / BoxPanel borders, plain table headers, and model-name labels. */
	public static Color getLavender() { return themeColor(() -> tint(LIGHT_LAVENDER, 0.35f)); }

	/** A softer-than-usual selection background some table renderers use for their selected rows. */
	public static Color getSoftSelectionBackground() { return themeColor(() -> tint(LIGHT_SOFT_SELECTION, 0.2f)); }

	/** Rows that match the current selection in a related table. */
	public static Color getMatchHighlightBackground() { return themeColor(() -> tint(Color.yellow, 0.3f)); }

	/** Text of read-only values. Always a shade of brown, VCell's read-only marker; lighter on dark themes. */
	public static Color getUneditableForeground() {
		return themeColor(() -> isDarkTheme() ? blend(LIGHT_UNEDITABLE, Color.white, 0.35f) : LIGHT_UNEDITABLE);
	}

	/**
	 * Canvas behind molecule and rule shape drawings. The same in every theme: the shapes (vcell-core's
	 * *LargeShape classes) draw names and outlines in black, so like the plots they need a light background.
	 */
	public static Color getShapeCanvasBackground() { return SHAPE_CANVAS; }

	/** As {@link #getShapeCanvasBackground()}, for view-only shape panels (grey rather than white). */
	public static Color getViewOnlyShapeCanvasBackground() { return VIEW_ONLY_SHAPE_CANVAS; }

	/** Light grey of read-out values, text gutters, and selection in a few small lists. */
	public static Color getSubtleBackground() { return getThemedBackground(Color.lightGray); }

	/** {@code lightThemeColor} as-is on light themes; on dark ones, the theme background tinted toward it. */
	public static Color getThemedBackground(Color lightThemeColor) { return themeColor(() -> tint(lightThemeColor, 0.15f)); }

	/** Blue text for computed values, notes, and link-style labels. */
	public static Color getValueForeground() { return getReadableForeground(LIGHT_VALUE_TEXT); }

	/** Navy text for headings (mostly the FRAP wizards). */
	public static Color getTitleForeground() { return getReadableForeground(LIGHT_TITLE_TEXT); }

	/** Red text for errors, failures, and inconsistent values. */
	public static Color getErrorForeground() { return getReadableForeground(Color.red); }

	/** Dark red text for warnings and side notes (e.g. issue markers in the database trees). */
	public static Color getWarningForeground() { return getReadableForeground(LIGHT_WARNING_TEXT); }

	/** Grey text for de-emphasized items. */
	public static Color getDimForeground() { return getReadableForeground(Color.gray); }

	/** Pink background of cells flagged as bad or out of range. */
	public static Color getErrorBackground() { return getThemedBackground(LIGHT_ERROR_BACKGROUND); }

	/** {@code color} as "#RRGGBB", for HTML text in labels and renderers. A snapshot: HTML text keeps it. */
	public static String toHtmlColor(Color color) {
		return String.format("#%06X", color.getRGB() & 0xFFFFFF);
	}

	/**
	 * {@code lightThemeColor} as-is on light themes; on dark ones, lightened toward white (keeping its hue)
	 * just enough to read against both the panel background and the (often lighter) content background.
	 */
	public static Color getReadableForeground(Color lightThemeColor) {
		return themeColor(() -> computeReadableForeground(lightThemeColor));
	}

	public static boolean isDarkTheme() {
		return isDark(getThemeBackground());
	}

	public static boolean isDark(Color color) {
		return null != color && 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue() < 128;
	}

	/** Linear mix: fraction 0 is {@code from}, 1 is {@code to}. */
	public static Color blend(Color from, Color to, float fraction) {
		return new Color(
				Math.round(from.getRed() + (to.getRed() - from.getRed()) * fraction),
				Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * fraction),
				Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * fraction));
	}

	/** WCAG contrast ratio, from 1 (identical) to 21 (black on white). */
	public static double getContrastRatio(Color a, Color b) {
		double lighter = Math.max(getRelativeLuminance(a), getRelativeLuminance(b));
		double darker = Math.min(getRelativeLuminance(a), getRelativeLuminance(b));
		return (lighter + 0.05) / (darker + 0.05);
	}

	/**
	 * A color that re-evaluates {@code supplier} whenever the look and feel changes. It is a plain Color, not a
	 * UIResource, so a theme switch keeps it on the component (and Synth themes paint it as given), while its
	 * value follows the new theme.
	 */
	static Color themeColor(Supplier<Color> supplier) {
		return new ThemeColor(supplier);
	}

	private static Color computeReadableForeground(Color lightThemeColor) {
		Color background = getThemeBackground();
		if (!isDark(background)) return lightThemeColor;
		Color contentBackground = UIManager.getColor("List.background");
		for (int step = 0; step <= 20; step++) {
			Color candidate = blend(lightThemeColor, Color.white, step / 20f);
			if (getContrastRatio(candidate, background) >= MIN_TEXT_CONTRAST
					&& (null == contentBackground || getContrastRatio(candidate, contentBackground) >= MIN_TEXT_CONTRAST)) return candidate;
		}
		return Color.white;
	}

	private static double getRelativeLuminance(Color color) {
		return 0.2126 * linearize(color.getRed()) + 0.7152 * linearize(color.getGreen()) + 0.0722 * linearize(color.getBlue());
	}

	private static double linearize(int channel) {
		double c = channel / 255.0;
		return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
	}

	// On a dark theme, mix some of the light-theme color into the background rather than using it as-is
	private static Color tint(Color lightThemeColor, float amount) {
		Color background = getThemeBackground();
		return isDark(background) ? blend(background, lightThemeColor, amount) : lightThemeColor;
	}

	private static Color getThemeBackground() {
		Color tableBackground = UIManager.getColor("Table.background");
		return null != tableBackground ? tableBackground : UIManager.getColor("Panel.background");
	}

	// Color derives getRed/Green/Blue/Alpha, equals, and what Graphics paints from getRGB()
	@SuppressWarnings("serial")
	private static final class ThemeColor extends Color {
		private final transient Supplier<Color> supplier;
		private transient LookAndFeel evaluatedFor;
		private transient int rgb;

		ThemeColor(Supplier<Color> supplier) {
			super(supplier.get().getRGB(), true);
			this.supplier = supplier;
			this.evaluatedFor = UIManager.getLookAndFeel();
			this.rgb = super.getRGB();
		}

		@Override
		public int getRGB() {
			LookAndFeel current = UIManager.getLookAndFeel();
			if (current != this.evaluatedFor) {
				this.evaluatedFor = current;
				this.rgb = this.supplier.get().getRGB();
			}
			return this.rgb;
		}
	}
}
