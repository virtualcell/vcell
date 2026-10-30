package cbit.vcell.client.laf;

import com.formdev.flatlaf.FlatLaf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.synth.SynthLookAndFeel;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * VCell's text sizes and family. The size is set in points, as the size of ordinary text (labels, fields, tables,
 * trees), and applies live: every font the theme defines grows or shrinks with it, keeping the theme's own differences
 * (a theme whose tooltips are smaller than its labels keeps them smaller). Themes start from different sizes, so
 * internally the size becomes a scale relative to the current theme's own, kept across theme switches.
 * <p>
 * The family, if one is picked, replaces the theme's in all but monospaced text. Families of the same point size can
 * look larger or smaller, so a picked family is sized to match the theme font's lowercase letters: 13pt looks like
 * 13pt whatever the family.
 * <p>
 * VCell's own text asks for a {@link Category} rather than a size, so it follows the size too; tables and trees can
 * have their row heights follow it.
 */
public final class VCellFonts {
	private static final Logger lg = LogManager.getLogger(VCellFonts.class);

	/** The smallest and largest size {@link #setSize} accepts, in points. */
	public static final float MIN_SIZE = 9, MAX_SIZE = 28;
	/** The size VCell starts at until the user picks another, in points. */
	public static final float DEFAULT_SIZE = 13;
	/** The step between the sizes offered (the text size menu, the font dialog), in points. */
	public static final int SIZE_STEP = 1;
	// The size and family a user picked, kept in ~/.vcell/vcellconfig.properties
	private static final String SAVED_SIZE_KEY = "vcell.client.fontSize";
	private static final String SAVED_FAMILY_KEY = "vcell.client.fontFamily";
	// Characters VCell's text needs (letters, digits, units, Greek parameter names); families without them aren't offered
	private static final String NEEDED_CHARACTERS = "AZaz09\u00b5\u00b2\u00b3\u00d7\u00b1\u00b0\u03b1\u03b2\u03b3\u0394";
	private static final Set<String> LOGICAL_FAMILIES = Set.of(Font.DIALOG, Font.DIALOG_INPUT, Font.SANS_SERIF, Font.SERIF, Font.MONOSPACED);
	private static final int BORROWED_GLYPH = 0x1000000; // see hasOwnLetters
	// How far a picked family's size may be adjusted to match the theme font's lowercase letters
	private static final float MIN_FAMILY_ADJUSTMENT = 0.8f, MAX_FAMILY_ADJUSTMENT = 1.25f;

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
	// Table and tree rows are this many lines of ordinary text tall (or the theme's own height, if taller): room for
	// the text plus enough space between rows to read them easily, at any size
	private static final float ROW_SPACING = 1.4f;
	private static final String CATEGORY = "VCellFonts.category";
	private static final String STYLE = "VCellFonts.style";
	private static final String ROW_HEIGHT = "VCellFonts.rowHeight"; // the height last set, to tell if other code changed it since
	private static final FontRenderContext RENDER_CONTEXT = new FontRenderContext(null, true, true);

	private static Float size = null; // null: the theme's own size
	private static String family = null; // null: the theme's own
	private static List<String> availableFamilies = null;
	private static java.lang.reflect.Method compositeFontMethod = null; // see withFallback
	private static boolean fallbackUnavailableLogged = false;
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
	private static int treeRowHeight = FALLBACK_TABLE_ROW_HEIGHT;

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
		VCellFonts.change(() -> VCellFonts.size = clamped);
	}

	/** The family of VCell's text, or null for the theme's own. */
	public static String getFamily() {
		return VCellFonts.family;
	}

	/**
	 * Sets the family of VCell's text (null for the theme's own), and changes the text of every open window to it. Set
	 * before the theme is installed, it is the family VCell starts in. Call it on the Swing event thread once windows
	 * are open.
	 */
	public static void setFamily(String family) {
		VCellFonts.change(() -> VCellFonts.family = family);
	}

	/**
	 * The installed families VCell's text can be in, sorted: text fonts with their own Latin letters and digits, that
	 * can show the units and Greek letters VCell does, and aren't monospaced. Looked up the first time; kept after.
	 */
	public static List<String> getAvailableFamilies() {
		if (null == VCellFonts.availableFamilies) {
			List<String> families = new ArrayList<>();
			for (String name : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
				if (name.startsWith(".") || LOGICAL_FAMILIES.contains(name)) continue; // hidden system fonts; Java's own aliases
				Font font = new Font(name, Font.PLAIN, 12);
				if (!name.equals(font.getFamily())) continue; // can't be loaded by its name
				if (font.canDisplayUpTo(NEEDED_CHARACTERS) != -1 || !VCellFonts.hasOwnLetters(font)) continue;
				if (VCellFonts.isMonospaced(font) || !VCellFonts.looksLikeText(font)) continue;
				families.add(name);
			}
			VCellFonts.availableFamilies = List.copyOf(families);
		}
		return VCellFonts.availableFamilies;
	}

	// macOS fills in the characters a font lacks from other fonts, marking each borrowed glyph with the font it came
	// from in its high byte; a font that borrows even its letters and digits (emoji, Braille, other scripts) isn't for text
	private static boolean hasOwnLetters(Font font) {
		GlyphVector glyphs = font.createGlyphVector(RENDER_CONTEXT, "AZaz09");
		for (int i = 0; i < glyphs.getNumGlyphs(); i++) {
			if (glyphs.getGlyphCode(i) >= BORROWED_GLYPH) return false;
		}
		return true;
	}

	// Letters shaped like text: a lowercase x shorter than a capital H, a p that hangs below the line, an i narrower than
	// an m. Rules out pictures in letters' places (Wingdings, ornaments) and all-capitals display fonts.
	private static boolean looksLikeText(Font font) {
		Font large = font.deriveFont(100f);
		double xHeight = VCellFonts.xHeight(large), capHeight = large.createGlyphVector(RENDER_CONTEXT, "H").getVisualBounds().getHeight();
		double descent = large.createGlyphVector(RENDER_CONTEXT, "p").getVisualBounds().getMaxY();
		double iWidth = large.getStringBounds("i", RENDER_CONTEXT).getWidth(), mWidth = large.getStringBounds("m", RENDER_CONTEXT).getWidth();
		return xHeight > 0.35 * capHeight && xHeight < 0.9 * capHeight && descent > 0.1 * xHeight && iWidth < 0.75 * mWidth;
	}

	/**
	 * The font ordinary text would have in this family (null: the theme's own) at this size, in points, in the current
	 * theme; for previewing a choice before making it.
	 */
	public static Font getPreviewFont(String family, float points) {
		Font body = VCellFonts.getThemeBodyFont();
		return null == family ? body.deriveFont(points)
				: VCellFonts.withFallback(new FontUIResource(family, Font.PLAIN, Math.round(points * VCellFonts.familyAdjustment(family, body))));
	}

	// Makes a change to the size or family, then gives it to the theme and every open window
	private static void change(Runnable change) {
		boolean windowsOpen = VCellFonts.themeRead && Window.getWindows().length > 0;
		Runnable resizeWindows = windowsOpen ? LayoutScaler.prepareWindowsForScaleChange() : null;
		change.run();
		if (!VCellFonts.themeRead) return; // the theme picks it up when installed
		// Nimbus-family themes keep the fonts they have handed out and don't see later changes; a fresh copy of the
		// theme, which reads the new size as it is installed, does
		if (UIManager.getLookAndFeel() instanceof SynthLookAndFeel) VCellLookAndFeel.reinstallLookAndFeel();
		else VCellFonts.applySize();
		if (!windowsOpen) return;
		VCellLookAndFeel.refreshAllWindows();
		resizeWindows.run();
	}

	/** Every size offered, from {@link #MIN_SIZE} to {@link #MAX_SIZE} by {@link #SIZE_STEP}, in points. */
	public static List<Float> getSizeChoices() {
		List<Float> sizes = new ArrayList<>();
		for (float points = MIN_SIZE; points <= MAX_SIZE; points += SIZE_STEP) sizes.add(points);
		return sizes;
	}

	/**
	 * Starts VCell at the size and family the user last picked, or {@link #DEFAULT_SIZE} and the theme's family. Call
	 * before the theme is installed.
	 */
	public static void restore() {
		String saved = VCellLookAndFeel.getSavedValue(SAVED_SIZE_KEY);
		float points = VCellFonts.DEFAULT_SIZE;
		if (null != saved) {
			try {
				points = Float.parseFloat(saved);
			} catch (NumberFormatException e) {
				lg.warn("Ignoring the saved font size {}: not a number", saved);
			}
		}
		VCellFonts.setSize(points);
		String savedFamily = VCellLookAndFeel.getSavedValue(SAVED_FAMILY_KEY);
		if (null == savedFamily) return;
		// a family checked against what is installed now; it may have been uninstalled since
		if (Font.DIALOG.equals(new Font(savedFamily, Font.PLAIN, 12).getFamily()) && !Font.DIALOG.equals(savedFamily)) {
			lg.warn("The saved font family {} is not installed; using the theme's", savedFamily);
		} else {
			VCellFonts.setFamily(savedFamily);
		}
	}

	/** Changes the size to one the user picked, and remembers it for the next start. Call on the Swing event thread. */
	public static void chooseSize(float points) {
		VCellFonts.setSize(points);
		VCellLookAndFeel.saveValue(SAVED_SIZE_KEY, Float.toString(VCellFonts.getSize()));
	}

	/**
	 * Changes the family to one the user picked (null: the theme's own), and remembers it for the next start. Call on
	 * the Swing event thread.
	 */
	public static void chooseFamily(String family) {
		VCellFonts.setFamily(family);
		VCellLookAndFeel.saveValue(SAVED_FAMILY_KEY, family);
	}

	/** The current font for this kind of text. */
	public static Font getFont(Category category) {
		Font font = VCellFonts.categoryFonts.get(category);
		// a plain Font, so a theme update doesn't take it back off a component it was set on
		return null == font ? new JLabel().getFont() : new PlainFont(font);
	}

	/** The current font for this kind of text, in another style (Font.PLAIN, BOLD, ITALIC, or BOLD | ITALIC). */
	public static Font getFont(Category category, int style) {
		Font font = VCellFonts.getFont(category);
		return font.getStyle() == style ? font : font.deriveFont(style);
	}

	/** Gives the component the font for this kind of text, and keeps it current as the size or theme changes. */
	public static void setCategory(JComponent component, Category category) {
		VCellFonts.setCategory(component, category, VCellFonts.getFont(category).getStyle());
	}

	/** As {@link #setCategory(JComponent, Category)}, in another style (e.g. an italic title). */
	public static void setCategory(JComponent component, Category category, int style) {
		component.putClientProperty(CATEGORY, category);
		component.putClientProperty(STYLE, style);
		component.setFont(VCellFonts.getFont(category, style));
	}

	/**
	 * Gives the component its theme's own font in this style (a bold menu item, say), and keeps it that way as the
	 * size or theme changes. For text that is the component's usual kind, just emphasized; otherwise use a category.
	 */
	public static void setStyle(JComponent component, int style) {
		component.putClientProperty(CATEGORY, null);
		component.putClientProperty(STYLE, style);
		// a UIResource, so a theme update replaces it with the theme's new font, which refresh styles again
		component.setFont(new FontUIResource(component.getFont().deriveFont(style)));
	}

	/**
	 * Leaves the sizes code sets on this component as they are, rather than scaling them with the text: for sizes
	 * already worked out from its text, or that must stay exact pixels (an image shown pixel for pixel, say).
	 */
	public static void keepSize(JComponent component) {
		LayoutScaler.keepSize(component);
	}

	/** Sizes the table's rows to the current text, and keeps them sized to it until other code sets their height. */
	public static void followRowHeight(JTable table) {
		VCellFonts.setRowHeight(table, VCellFonts.tableRowHeight);
	}

	/** Sizes the tree's rows to the current text, and keeps them sized to it until other code sets their height. */
	public static void followRowHeight(JTree tree) {
		VCellFonts.setRowHeight(tree, VCellFonts.treeRowHeight);
	}

	/**
	 * Applies the size to every theme installed from now on. Call before any Swing component exists: the size is
	 * applied as the theme change is announced, and must come before components that update themselves on that
	 * announcement (a TitledBorder's label does), or Nimbus-family themes fix their fonts at the theme's own size.
	 */
	static void followThemes() {
		if (VCellFonts.followingThemes) return;
		VCellFonts.followingThemes = true;
		LayoutScaler.install();
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
			LayoutScaler.scaleComponent(c);
			if (!(c instanceof JComponent component)) return;
			if (component.getClientProperty(CATEGORY) instanceof Category category) {
				component.setFont(component.getClientProperty(STYLE) instanceof Integer style ? VCellFonts.getFont(category, style) : VCellFonts.getFont(category));
			} else if (component.getClientProperty(STYLE) instanceof Integer style) {
				component.setFont(new FontUIResource(component.getFont().deriveFont(style)));
			}
			if (!(component.getClientProperty(ROW_HEIGHT) instanceof Integer lastSet)) return;
			int rowHeight = component instanceof JTable table ? table.getRowHeight() : component instanceof JTree tree ? tree.getRowHeight() : -1;
			if (rowHeight != lastSet) {
				component.putClientProperty(ROW_HEIGHT, null); // other code set its own row height; leave it be
				return;
			}
			VCellFonts.setRowHeight(component, component instanceof JTable ? VCellFonts.tableRowHeight : VCellFonts.treeRowHeight);
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
		Font body = VCellFonts.getThemeBodyFont();
		float adjustment = null == VCellFonts.family ? 1 : VCellFonts.familyAdjustment(VCellFonts.family, body);
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		VCellFonts.themeFonts.forEach((key, font) -> defaults.put(key, VCellFonts.scaled(font, scale, adjustment)));

		VCellFonts.categoryFonts.clear();
		for (Category category : Category.values()) {
			Font themeFont = null == category.themeKey ? null : VCellFonts.themeFonts.get(category.themeKey);
			Font font = null != themeFont ? VCellFonts.scaled(themeFont, scale, adjustment)
					: category == Category.MONOSPACED ? new FontUIResource(Font.MONOSPACED, category.style, Math.round(VCellFonts.getSize()))
					: VCellFonts.scaled(body.deriveFont(category.style, VCellFonts.themeSize * category.relativeSize), scale, adjustment);
			VCellFonts.categoryFonts.put(category, font);
		}

		float lineHeight = VCellFonts.categoryFonts.get(Category.BODY).getLineMetrics("Xg", RENDER_CONTEXT).getHeight();
		int rowHeight = Math.round(lineHeight * ROW_SPACING);
		VCellFonts.tableRowHeight = Math.max(rowHeight, Math.round(VCellFonts.themeTableRowHeight * scale));
		VCellFonts.treeRowHeight = Math.max(rowHeight, Math.round(VCellFonts.themeTreeRowHeight * scale));
		if (UIManager.getLookAndFeel() instanceof FlatLaf) return; // FlatLaf scales its own row heights with its font
		defaults.put("Table.rowHeight", VCellFonts.tableRowHeight);
		if (VCellFonts.themeTreeRowHeight > 0) defaults.put("Tree.rowHeight", VCellFonts.treeRowHeight);
	}

	// The theme font scaled to the size, in the picked family (adjusted to look the size) unless it is monospaced.
	// Whole points: fractional sizes can render unevenly.
	private static FontUIResource scaled(Font font, float scale, float familyAdjustment) {
		float points = font.getSize2D() * scale;
		if (null == VCellFonts.family || VCellFonts.isMonospaced(font)) return new FontUIResource(font.deriveFont((float) Math.round(points)));
		return VCellFonts.withFallback(new FontUIResource(VCellFonts.family, font.getStyle(), Math.round(points * familyAdjustment)));
	}

	/**
	 * The family font, with characters it lacks drawn from the system's fallback fonts, as the theme's own font does.
	 * macOS does this for any font; on Windows and Linux a font named by its family has only its own characters, so text
	 * in other scripts (species names in Chinese, say) would show as boxes. Swing's own Windows and Linux themes wrap
	 * their fonts this way, through a part of Java that isn't public: VCell's launchers allow it
	 * (--add-exports java.desktop/sun.font=ALL-UNNAMED). Without that, the family is used as it is.
	 */
	private static FontUIResource withFallback(FontUIResource font) {
		if (System.getProperty("os.name", "").startsWith("Mac")) return font;
		try {
			if (null == VCellFonts.compositeFontMethod) {
				VCellFonts.compositeFontMethod = Class.forName("sun.font.FontUtilities").getMethod("getCompositeFontUIResource", Font.class);
			}
			return (FontUIResource) VCellFonts.compositeFontMethod.invoke(null, font);
		} catch (ReflectiveOperationException | RuntimeException e) {
			if (!VCellFonts.fallbackUnavailableLogged) lg.info("Font fallback unavailable; {} shows only its own characters: {}", font.getFamily(), e.toString());
			VCellFonts.fallbackUnavailableLogged = true;
			return font;
		}
	}

	// A copy of a font that isn't a UIResource, keeping what it draws with (a plain new Font from its attributes would
	// lose a fallback it has)
	private static final class PlainFont extends Font {
		PlainFont(Font font) {
			super(font);
		}
	}

	private static Font getThemeBodyFont() {
		Font body = VCellFonts.themeFonts.getOrDefault("Label.font", VCellFonts.themeFonts.get("defaultFont"));
		return null == body ? new Font(Font.DIALOG, Font.PLAIN, Math.round(FALLBACK_THEME_SIZE)) : body;
	}

	// How much to scale the family so its lowercase letters are as tall as the theme font's at the same point size
	private static float familyAdjustment(String family, Font themeFont) {
		float themeHeight = VCellFonts.xHeight(themeFont.deriveFont(Font.PLAIN, 100f));
		float familyHeight = VCellFonts.xHeight(new Font(family, Font.PLAIN, 100));
		if (themeHeight <= 0 || familyHeight <= 0) return 1;
		return Math.max(MIN_FAMILY_ADJUSTMENT, Math.min(MAX_FAMILY_ADJUSTMENT, themeHeight / familyHeight));
	}

	private static float xHeight(Font font) {
		return (float) font.createGlyphVector(RENDER_CONTEXT, "x").getVisualBounds().getHeight();
	}

	private static boolean isMonospaced(Font font) {
		return Math.abs(font.getStringBounds("i", RENDER_CONTEXT).getWidth() - font.getStringBounds("W", RENDER_CONTEXT).getWidth()) < 0.01;
	}
}
