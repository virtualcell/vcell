package cbit.vcell.client.laf;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.geom.Path2D;

public class NimbusDarkLookAndFeel extends NimbusLookAndFeel {
	public static final String ID = "NimbusDarkCustom";
	public static final String NAME = "Nimbus Dark (Custom)";
	// Colors based on OSX Mojave's dark theme
	private static final Color PRIMARY_BACKGROUND = new ColorUIResource(0x1C1C1E);
	private static final Color PRIMARY_FOIL_BACKGROUND = new ColorUIResource(0x262626);
	private static final Color SECONDARY_BACKGROUND = new ColorUIResource(0x2C2C2E);
	private static final Color TERTIARY_BACKGROUND = new ColorUIResource(0x3A3A3C);
	private static final Color PRIMARY_TEXT = new ColorUIResource(0xF5F5F7);
	private static final Color SECONDARY_TEXT = new ColorUIResource(0xAEAEB2);
	private static final Color ACCENT = new ColorUIResource(0x007AFF);
	private static final Color BORDER = new ColorUIResource(0x48484A);
	private static final Color SELECTED_TEXT = new ColorUIResource(Color.WHITE);

	@Override public String getName() { return NimbusDarkLookAndFeel.NAME; }
	@Override public String getID() { return NimbusDarkLookAndFeel.ID; }
	@Override public String getDescription() { return "A version of Nimbus made as a dark theme. Original concept borrowed from `javathinking.com`"; }

	@Override
	public UIDefaults getDefaults() {
		UIDefaults defaults = super.getDefaults();

		// Nimbus base colors; most component colors are derived from these
		defaults.put("control", PRIMARY_BACKGROUND);
		defaults.put("background", PRIMARY_BACKGROUND);
		defaults.put("nimbusLightBackground", SECONDARY_BACKGROUND); // lists, trees, tables, text components
		defaults.put("textBackground", SECONDARY_BACKGROUND);
		defaults.put("menu", SECONDARY_BACKGROUND);
		defaults.put("info", TERTIARY_BACKGROUND); // tooltips
		defaults.put("nimbusBase", TERTIARY_BACKGROUND); // button / scrollbar shading
		defaults.put("nimbusBlueGrey", TERTIARY_BACKGROUND);
		defaults.put("nimbusBorder", BORDER);
		defaults.put("text", PRIMARY_TEXT);
		defaults.put("textForeground", PRIMARY_TEXT);
		defaults.put("controlText", PRIMARY_TEXT);
		defaults.put("menuText", PRIMARY_TEXT);
		defaults.put("infoText", PRIMARY_TEXT);
		defaults.put("nimbusDisabledText", SECONDARY_TEXT);
		defaults.put("nimbusFocus", ACCENT);
		defaults.put("nimbusSelectionBackground", ACCENT);
		defaults.put("nimbusSelectedText", SELECTED_TEXT);
		defaults.put("textHighlight", ACCENT);
		defaults.put("textHighlightText", SELECTED_TEXT);
		defaults.put("controlHighlight", TERTIARY_BACKGROUND);
		defaults.put("controlLHighlight", TERTIARY_BACKGROUND);
		defaults.put("controlShadow", BORDER);
		defaults.put("controlDkShadow", PRIMARY_FOIL_BACKGROUND);
		defaults.put("scrollbar", SECONDARY_BACKGROUND);
		defaults.put("activeCaption", SECONDARY_BACKGROUND);
		defaults.put("inactiveCaption", PRIMARY_BACKGROUND);

		// Text colors Nimbus hard-codes instead of deriving from the base colors
		defaults.put("Menu[Enabled].textForeground", PRIMARY_TEXT);
		defaults.put("MenuBar:Menu[Enabled].textForeground", PRIMARY_TEXT);
		defaults.put("MenuItem.foreground", PRIMARY_TEXT);
		defaults.put("MenuItem[Enabled].textForeground", PRIMARY_TEXT);
		defaults.put("CheckBoxMenuItem.foreground", PRIMARY_TEXT);
		defaults.put("CheckBoxMenuItem[Enabled].textForeground", PRIMARY_TEXT);
		defaults.put("RadioButtonMenuItem.foreground", PRIMARY_TEXT);
		defaults.put("RadioButtonMenuItem[Enabled].textForeground", PRIMARY_TEXT);
		defaults.put("Table.textForeground", PRIMARY_TEXT);
		// Nimbus derives selected text from nimbusLightBackground, which is now dark
		defaults.put("List[Selected].textForeground", SELECTED_TEXT);
		defaults.put("List:\"List.cellRenderer\"[Selected].textForeground", SELECTED_TEXT);
		defaults.put("Table[Enabled+Selected].textForeground", SELECTED_TEXT);

		// Per-component overrides
		defaults.put("Panel.background", PRIMARY_BACKGROUND);
		defaults.put("Label.foreground", PRIMARY_TEXT);
		defaults.put("Button.background", SECONDARY_BACKGROUND);
		defaults.put("Button.foreground", PRIMARY_TEXT);
		defaults.put("Button.select", TERTIARY_BACKGROUND); // Selected button state
		defaults.put("Button.border", new BorderUIResource.LineBorderUIResource(BORDER, 0));
		defaults.put("Button.borderPainted", false);
		defaults.put("TextField.background", SECONDARY_BACKGROUND);
		defaults.put("TextField.foreground", PRIMARY_TEXT);
		defaults.put("TextField.caretForeground", PRIMARY_TEXT); // Text cursor color
		defaults.put("TextField.border", new BorderUIResource.LineBorderUIResource(BORDER, 1));
		defaults.put("ScrollPane.background", PRIMARY_BACKGROUND);
		defaults.put("ScrollBar.background", PRIMARY_BACKGROUND);
		defaults.put("ScrollBar.track", SECONDARY_BACKGROUND);
		defaults.put("ScrollBar.thumb", TERTIARY_BACKGROUND);
		defaults.put("ScrollBar.width", 8);
		defaults.put("ScrollBar.thumbArc", 4);
		defaults.put("Menu.background", PRIMARY_BACKGROUND);
		defaults.put("Menu.foreground", PRIMARY_TEXT);
		defaults.put("Menu.selectionBackground", ACCENT);
		defaults.put("Menu.selectionForeground", SELECTED_TEXT);
		defaults.put("Table.background", PRIMARY_BACKGROUND);
		defaults.put("Table.foreground", PRIMARY_TEXT);
		defaults.put("Table.gridColor", BORDER);
		defaults.put("Table.alternateRowColor", PRIMARY_FOIL_BACKGROUND);
		defaults.put("Separator.background", BORDER);
		defaults.put("ToolTip.background", TERTIARY_BACKGROUND);
		defaults.put("ToolTip.foreground", PRIMARY_TEXT);

		// Nimbus draws tree arrows from nimbusBlueGrey shifted far darker, which comes out black on this theme
		defaults.put("Tree.expandedIcon", new TreeArrowIcon(true, (Icon) defaults.get("Tree.expandedIcon")));
		defaults.put("Tree.collapsedIcon", new TreeArrowIcon(false, (Icon) defaults.get("Tree.collapsedIcon")));
		return defaults;
	}

	/** A tree expand / collapse arrow in this theme's secondary text color, the same size as Nimbus' own. */
	private static final class TreeArrowIcon implements Icon, UIResource {
		private final boolean expanded;
		private final int width;
		private final int height;

		TreeArrowIcon(boolean expanded, Icon nimbusIcon) {
			this.expanded = expanded;
			this.width = nimbusIcon.getIconWidth();
			this.height = nimbusIcon.getIconHeight();
		}

		@Override public int getIconWidth() { return this.width; }
		@Override public int getIconHeight() { return this.height; }

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(SECONDARY_TEXT);
			// like Nimbus' arrows: a triangle filling a square at the icon's left edge (7x7 in an 18x7 icon)
			float s = Math.min(this.width, this.height);
			Path2D.Float arrow = new Path2D.Float();
			if (this.expanded) { // pointing down
				arrow.moveTo(x, y + s * 0.15f);
				arrow.lineTo(x + s, y + s * 0.15f);
				arrow.lineTo(x + s / 2, y + s * 0.9f);
			} else { // pointing right
				arrow.moveTo(x + s * 0.15f, y);
				arrow.lineTo(x + s * 0.15f, y + s);
				arrow.lineTo(x + s * 0.9f, y + s / 2);
			}
			arrow.closePath();
			g2.fill(arrow);
			g2.dispose();
		}
	}
}
