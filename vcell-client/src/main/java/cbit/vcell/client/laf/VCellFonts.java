package cbit.vcell.client.laf;

import com.formdev.flatlaf.FlatLaf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.synth.SynthLookAndFeel;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * VCell's text sizes. The size is set in points, as the size of ordinary text (labels, fields, tables, trees), and
 * applies live: every font the theme defines grows or shrinks with it, keeping the theme's own differences (a theme
 * whose tooltips are smaller than its labels keeps them smaller). Themes start from different sizes, so internally
 * the size becomes a scale relative to the current theme's own, kept across theme switches.
 * <p>
 * VCell's own text asks for a {@link Category} rather than a size, so it follows the size too; tables and trees can
 * have their row heights follow it.
 */
public final class VCellFonts {
	private static final Logger lg = LogManager.getLogger(VCellFonts.class);

	/** The smallest and largest size {@link #setSize} accepts, in points. */
	public static final float MIN_SIZE = 9, MAX_SIZE = 28;

	/** What a piece of text is for. Each has a size relative to {@link Category#BODY} and a style. */
	public enum Category {
		/** Panel and dialog headings */
		TITLE(1.25f, Font.BOLD, "h2.font"),
		/** Section titles, titled borders, emphasized labels */
		HEADING(1, Font.BOLD, "h4.font"),
		/** Almost everything: labels, fields, lists, tables, trees, buttons */
		BODY(1, Font.PLAIN, null),
		/** Tooltips, status text, hints, the shortcuts shown in menus */
		CAPTION(0.85f, Font.PLAIN, "small.font"),
		/** VCML, expressions, logs */
		MONOSPACED(1, Font.PLAIN, "monospaced.font");

		private final float relativeSize;
		private final int style;
		private final String themeKey; // the theme's own font for this, where it has one (FlatLaf's typography)

		Category(float relativeSize, int style, String themeKey) {
			this.relativeSize = relativeSize;
			this.style = style;
			this.themeKey = themeKey;
		}
	}

	private static final float FALLBACK_THEME_SIZE = 13;
	private static final int FALLBACK_TABLE_ROW_HEIGHT = 16; // JTable's own
	private static final int FALLBACK_TREE_ROW_HEIGHT = 20; // what VCell's trees have used where the theme sizes rows to fit
	private static final String CATEGORY = "VCellFonts.category";
	private static final String ROW_PADDING = "VCellFonts.rowPadding";
	private static final String ROW_HEIGHT = "VCellFonts.rowHeight"; // the height set from the padding, to tell if other code changed it since
	private static final FontRenderContext RENDER_CONTEXT = new FontRenderContext(null, true, true);

	private static Float size = null; // null: the theme's own size
	private static boolean themeRead = false; // whether a theme has been installed since VCell started following them
	private static boolean followingThemes = false;
	// The current theme's own, unscaled fonts and row heights
	private static final Map<String, Font> themeFonts = new HashMap<>();
	private static float themeSize = FALLBACK_THEME_SIZE;
	private static int themeTableRowHeight = FALLBACK_TABLE_ROW_HEIGHT;
	private static int themeTreeRowHeight = 0; // 0: rows sized to fit their contents
	// What the size gives the current theme
	private static final Map<Category, Font> categoryFonts = new EnumMap<>(Category.class);
	private static int tableRowHeight = FALLBACK_TABLE_ROW_HEIGHT;
	private static int treeRowHeight = FALLBACK_TREE_ROW_HEIGHT;

	private VCellFonts() {}

	/** The size of ordinary text, in points. */
	public static float getSize() {
		return null == VCellFonts.size ? VCellFonts.themeSize : VCellFonts.size;
	}

	/**
	 * Sets the size of ordinary text, in points (limited to {@link #MIN_SIZE}..{@link #MAX_SIZE}), and resizes the
	 * text of every open window to match. Set before the theme is installed, it is the size VCell starts at. Call it
	 * on the Swing event thread once windows are open.
	 */
	public static void setSize(float points) {
		float clamped = Math.max(MIN_SIZE, Math.min(MAX_SIZE, points));
		if (clamped != points) lg.warn("Font size {} is outside {}..{}; using {}", points, MIN_SIZE, MAX_SIZE, clamped);
		VCellFonts.size = clamped;
		if (!VCellFonts.themeRead) return; // the theme picks it up when installed
		// Nimbus-family themes keep the fonts they have handed out and don't see later changes; a fresh copy of the
		// theme, which reads the new size as it is installed, does
		if (UIManager.getLookAndFeel() instanceof SynthLookAndFeel) VCellLookAndFeel.reinstallLookAndFeel();
		else VCellFonts.applySize();
		if (Window.getWindows().length > 0) VCellLookAndFeel.refreshAllWindows();
	}

	/** The current font for this kind of text. */
	public static Font getFont(Category category) {
		Font font = VCellFonts.categoryFonts.get(category);
		// a plain Font, so a theme update doesn't take it back off a component it was set on
		return null == font ? new JLabel().getFont() : new Font(font.getAttributes());
	}

	/** Gives the component the font for this kind of text, and keeps it current as the size or theme changes. */
	public static void setCategory(JComponent component, Category category) {
		component.putClientProperty(CATEGORY, category);
		component.setFont(VCellFonts.getFont(category));
	}

	/** Row height of a table at the current size, before any padding of its own. */
	public static int getTableRowHeight() { return VCellFonts.tableRowHeight; }

	/** Row height of a tree at the current size, before any padding of its own. */
	public static int getTreeRowHeight() { return VCellFonts.treeRowHeight; }

	/**
	 * Keeps the table's rows {@code padding} pixels taller than the current size gives them, until other code sets
	 * the table's row height itself.
	 */
	public static void setRowPadding(JTable table, int padding) {
		table.putClientProperty(ROW_PADDING, padding);
		VCellFonts.setRowHeight(table, VCellFonts.tableRowHeight + padding);
	}

	/**
	 * Keeps the tree's rows {@code padding} pixels taller than the current size gives them, until other code sets the
	 * tree's row height itself.
	 */
	public static void setRowPadding(JTree tree, int padding) {
		tree.putClientProperty(ROW_PADDING, padding);
		VCellFonts.setRowHeight(tree, VCellFonts.treeRowHeight + padding);
	}

	/**
	 * Applies the size to every theme installed from now on. Call before any Swing component exists: the size is
	 * applied as the theme change is announced, and must come before components that update themselves on that
	 * announcement (a TitledBorder's label does), or Nimbus-family themes fix their fonts at the theme's own size.
	 */
	static void followThemes() {
		if (VCellFonts.followingThemes) return;
		VCellFonts.followingThemes = true;
		UIManager.addPropertyChangeListener(event -> {
			if ("lookAndFeel".equals(event.getPropertyName())) VCellFonts.themeChanged();
		});
	}

	// Reads the newly installed theme's own fonts and gives it the current size
	private static void themeChanged() {
		VCellFonts.themeRead = true;
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		VCellFonts.themeFonts.clear();
		for (Object key : new ArrayList<>(defaults.keySet())) {
			if (key instanceof String name && (name.endsWith("font") || name.endsWith("Font")) && defaults.get(name) instanceof Font font) {
				VCellFonts.themeFonts.put(name, font);
			}
		}
		Font themeFont = VCellFonts.themeFonts.getOrDefault("Label.font", VCellFonts.themeFonts.get("defaultFont"));
		VCellFonts.themeSize = null == themeFont ? FALLBACK_THEME_SIZE : themeFont.getSize2D();
		int tableRowHeight = defaults.getInt("Table.rowHeight");
		VCellFonts.themeTableRowHeight = tableRowHeight > 0 ? tableRowHeight : FALLBACK_TABLE_ROW_HEIGHT;
		VCellFonts.themeTreeRowHeight = Math.max(0, defaults.getInt("Tree.rowHeight"));
		VCellFonts.applySize();
	}

	/** Brings fonts and row heights that VCell set on components under {@code root} up to the current size. */
	static void refresh(Component root) {
		VCellLookAndFeel.forEachComponent(root, c -> {
			if (!(c instanceof JComponent component)) return;
			if (component.getClientProperty(CATEGORY) instanceof Category category) component.setFont(VCellFonts.getFont(category));
			if (!(component.getClientProperty(ROW_PADDING) instanceof Integer padding)) return;
			int rowHeight = component instanceof JTable table ? table.getRowHeight() : component instanceof JTree tree ? tree.getRowHeight() : -1;
			if (!Integer.valueOf(rowHeight).equals(component.getClientProperty(ROW_HEIGHT))) {
				component.putClientProperty(ROW_PADDING, null); // other code set its own row height; leave it be
				return;
			}
			VCellFonts.setRowHeight(component, (component instanceof JTable ? VCellFonts.tableRowHeight : VCellFonts.treeRowHeight) + padding);
		});
	}

	private static void setRowHeight(JComponent component, int rowHeight) {
		if (component instanceof JTable table) table.setRowHeight(rowHeight);
		else if (component instanceof JTree tree) tree.setRowHeight(rowHeight);
		component.putClientProperty(ROW_HEIGHT, rowHeight);
	}

	// Scales the theme's fonts and row heights to the size, in the theme's own defaults: a theme switch replaces them
	// with the new theme's, and components only take fonts from the theme that the theme gave them (UIResources)
	private static void applySize() {
		float scale = VCellFonts.getSize() / VCellFonts.themeSize;
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		VCellFonts.themeFonts.forEach((key, font) -> defaults.put(key, VCellFonts.scaled(font, scale)));

		Font body = VCellFonts.themeFonts.getOrDefault("Label.font", VCellFonts.themeFonts.get("defaultFont"));
		if (null == body) body = new Font(Font.DIALOG, Font.PLAIN, Math.round(FALLBACK_THEME_SIZE));
		VCellFonts.categoryFonts.clear();
		for (Category category : Category.values()) {
			Font themeFont = null == category.themeKey ? null : VCellFonts.themeFonts.get(category.themeKey);
			Font font = null != themeFont ? VCellFonts.scaled(themeFont, scale)
					: category == Category.MONOSPACED ? new FontUIResource(Font.MONOSPACED, category.style, Math.round(VCellFonts.getSize()))
					: new FontUIResource(body.deriveFont(category.style, Math.round(VCellFonts.getSize() * category.relativeSize)));
			VCellFonts.categoryFonts.put(category, font);
		}

		// rows at least as tall as a line of ordinary text
		int lineHeight = (int) Math.ceil(VCellFonts.categoryFonts.get(Category.BODY).getLineMetrics("Xg", RENDER_CONTEXT).getHeight()) + 2;
		VCellFonts.tableRowHeight = Math.max(lineHeight, Math.round(VCellFonts.themeTableRowHeight * scale));
		int treeBase = VCellFonts.themeTreeRowHeight >= 10 ? VCellFonts.themeTreeRowHeight : FALLBACK_TREE_ROW_HEIGHT;
		VCellFonts.treeRowHeight = Math.max(lineHeight, Math.round(treeBase * scale));
		if (UIManager.getLookAndFeel() instanceof FlatLaf) return; // FlatLaf scales its own row heights with its font
		defaults.put("Table.rowHeight", VCellFonts.tableRowHeight);
		if (VCellFonts.themeTreeRowHeight > 0) defaults.put("Tree.rowHeight", VCellFonts.treeRowHeight);
	}

	// Whole points: fractional sizes can render unevenly
	private static FontUIResource scaled(Font font, float scale) {
		return new FontUIResource(font.deriveFont((float) Math.round(font.getSize2D() * scale)));
	}
}
