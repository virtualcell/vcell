package cbit.vcell.client.laf;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * macOS's own look and feel (Aqua) in dark mode. macOS draws Aqua's native controls (buttons, combo boxes, tabs,
 * scroll bars) in the dark appearance; the parts Aqua paints itself in Java, with light colors, are recolored here
 * with macOS's own dark semantic colors, read from AppKit. macOS only.
 * <p>
 * Aqua cannot be subclassed (its package is not exported), so this wraps an instance of it.
 */
public class DarkAquaLookAndFeel extends LookAndFeel {
	public static final String ID = "DarkAqua";
	public static final String NAME = "Dark Aqua";

	// macOS's dark values, for when AppKit can't be read
	private static final Map<String, Color> FALLBACK_COLORS = Map.of(
			"windowBackgroundColor", new Color(0x323232),
			"controlBackgroundColor", new Color(0x1E1E1E),
			"underPageBackgroundColor", new Color(0x282828),
			"labelColor", new Color(255, 255, 255, 217),
			"textColor", Color.white,
			"headerTextColor", Color.white,
			"disabledControlTextColor", new Color(255, 255, 255, 64),
			"selectedContentBackgroundColor", new Color(0x0059D1),
			"unemphasizedSelectedContentBackgroundColor", new Color(0x464646),
			"selectedTextBackgroundColor", new Color(0x3F638B));
	private static final Color FALLBACK_SEPARATOR = new Color(255, 255, 255, 26);
	private static final String CONTENT_COMPONENTS = "(List|Table|Tree|TextField|TextArea|TextPane|EditorPane|FormattedTextField|PasswordField|Viewport|Spinner|ComboBox)";
	private static final String TEXT_COMPONENTS = "(TextField|TextArea|TextPane|EditorPane|FormattedTextField|PasswordField)";

	private final LookAndFeel aqua;

	public DarkAquaLookAndFeel() throws UnsupportedLookAndFeelException {
		this.aqua = UIManager.createLookAndFeel("Mac OS X");
	}

	@Override public String getName() { return DarkAquaLookAndFeel.NAME; }
	@Override public String getID() { return DarkAquaLookAndFeel.ID; }
	@Override public String getDescription() { return "macOS's Aqua look and feel in dark mode, colored by macOS itself"; }
	@Override public boolean isNativeLookAndFeel() { return true; }
	@Override public boolean isSupportedLookAndFeel() { return this.aqua.isSupportedLookAndFeel(); }
	@Override public boolean getSupportsWindowDecorations() { return this.aqua.getSupportsWindowDecorations(); }
	@Override public void initialize() { this.aqua.initialize(); }
	@Override public void uninitialize() { this.aqua.uninitialize(); }
	@Override public LayoutStyle getLayoutStyle() { return this.aqua.getLayoutStyle(); }
	@Override public void provideErrorFeedback(Component component) { this.aqua.provideErrorFeedback(component); }
	@Override public Icon getDisabledIcon(JComponent component, Icon icon) { return this.aqua.getDisabledIcon(component, icon); }
	@Override public Icon getDisabledSelectedIcon(JComponent component, Icon icon) { return this.aqua.getDisabledSelectedIcon(component, icon); }

	@Override
	public UIDefaults getDefaults() {
		UIDefaults defaults = this.aqua.getDefaults();
		Map<String, Color> macOS = MacOSSemanticColors.read(true, "windowBackgroundColor", "controlBackgroundColor",
				"underPageBackgroundColor", "labelColor", "textColor", "headerTextColor", "disabledControlTextColor", "separatorColor",
				"selectedContentBackgroundColor", "unemphasizedSelectedContentBackgroundColor", "selectedTextBackgroundColor");
		Color window = macOS(macOS, "windowBackgroundColor");
		Color content = macOS(macOS, "controlBackgroundColor");
		Color label = over(macOS(macOS, "labelColor"), window);
		Color text = over(macOS(macOS, "textColor"), content);
		Color header = over(macOS(macOS, "headerTextColor"), window);
		Color disabled = over(macOS(macOS, "disabledControlTextColor"), window);
		Color grid = over(macOS.getOrDefault("separatorColor", FALLBACK_SEPARATOR), content);
		Color selection = macOS(macOS, "selectedContentBackgroundColor");
		Color unemphasizedSelection = macOS(macOS, "unemphasizedSelectedContentBackgroundColor");
		Color textSelection = macOS(macOS, "selectedTextBackgroundColor");

		// Raw entries: get() would build Aqua's lazily created values before the look and feel is installed
		Map<String, Color> aquaColors = new TreeMap<>();
		for (Map.Entry<Object, Object> entry : defaults.entrySet()) {
			if (entry.getKey() instanceof String key && entry.getValue() instanceof Color color) aquaColors.put(key, color);
		}
		for (String key : aquaColors.keySet()) {
			Color color = this.getDarkColor(key, window, content, label, text, header, disabled, grid, selection, unemphasizedSelection, textSelection);
			if (null != color) defaults.put(key, new ColorUIResource(color));
		}

		defaults.put("ToolTip.background", new ColorUIResource(over(macOS(macOS, "underPageBackgroundColor"), window)));
		// Aqua keeps these tab title colors in keys the rules above don't match
		defaults.put("TabbedPane.selectedTabTitleNormalColor", new ColorUIResource(label));
		defaults.put("TabbedPane.nonSelectedTabTitleNormalColor", new ColorUIResource(label));
		defaults.put("TabbedPane.selectedTabTitleShadowNormalColor", new ColorUIResource(window));
		return defaults;
	}

	// Maps one of Aqua's color keys to its dark equivalent by the role the key's name gives it; null leaves it as is
	private Color getDarkColor(String key, Color window, Color content, Color label, Color text, Color header, Color disabled,
							   Color grid, Color selection, Color unemphasizedSelection, Color textSelection) {
		if (key.matches(TEXT_COMPONENTS + "\\.selectionBackground")) return textSelection;
		if (key.matches(TEXT_COMPONENTS + "\\.selectionForeground")) return Color.white;
		if (key.endsWith("selectionInactiveBackground") || key.endsWith("selectionBackgroundInactive")) return unemphasizedSelection;
		if (key.endsWith("selectionBackground") || key.equals("textHighlight") || key.equals("Table.focusCellBackground")) return selection;
		if (key.endsWith("selectionForeground") || key.endsWith("selectionInactiveForeground") || key.equals("textHighlightText")) return Color.white;
		if (key.matches(CONTENT_COMPONENTS + "\\.(background|textBackground)") || key.equals("text") || key.equals("window")) return content;
		// read-only (non-editable) text: Aqua gives it its own background key
		if (key.matches(TEXT_COMPONENTS + "\\.inactiveBackground") || key.equals("ComboBox.disabledBackground")) return content;
		if (key.endsWith(".background") || key.equals("control") || key.equals("menu")) return window;
		// disabled, toolbar, internal frame, and header focus backgrounds
		if (key.endsWith("Background") && !key.contains("election") && !key.startsWith("DesktopIcon.")) return window;
		if (key.endsWith("inactiveForeground") || key.endsWith("disabledText") || key.endsWith("disabledForeground") || key.equals("textInactiveText")) return disabled;
		if (key.matches(TEXT_COMPONENTS + "\\.(foreground|caretForeground)")) return text;
		if (key.startsWith("TableHeader.") && key.endsWith("oreground")) return header;
		if (key.endsWith("foreground") || key.endsWith("Foreground") || key.endsWith("titleColor")
				|| key.matches("(textText|controlText|windowText|infoText|menuText)")) return label;
		if (key.endsWith("gridColor")) return grid;
		return null;
	}

	private static Color macOS(Map<String, Color> colors, String name) {
		return colors.getOrDefault(name, FALLBACK_COLORS.get(name));
	}

	// macOS's text colors are translucent; Swing text reads better composited onto the background it sits on
	private static Color over(Color color, Color background) {
		float alpha = color.getAlpha() / 255f;
		return new Color(Math.round(color.getRed() * alpha + background.getRed() * (1 - alpha)),
				Math.round(color.getGreen() * alpha + background.getGreen() * (1 - alpha)),
				Math.round(color.getBlue() * alpha + background.getBlue() * (1 - alpha)));
	}
}
