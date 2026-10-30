package cbit.vcell.client.laf;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Scales the pixel sizes VCell's code sets on components (preferred, minimum and maximum sizes, positions in panels
 * without a layout, table column widths) and its windows' sizes with the text size, so layouts keep the proportions
 * they were designed with as the text grows or shrinks. Sizes a layout works out for itself already follow the text.
 * <p>
 * Each size is treated as written for {@link #DESIGN_SIZE}-point text; its design value is kept, so repeated changes
 * never compound rounding. A size code sets later becomes the new design value.
 */
final class LayoutScaler {
	/** The text size, in points, VCell's hard-coded sizes were laid out for (12pt on Windows; 11pt on the Mac). */
	static final float DESIGN_SIZE = 12;

	private static final String FIXED = "VCellFonts.fixedSize";
	private static final String[] KINDS = {"preferred", "minimum", "maximum"};
	private static final String BOUNDS = "bounds";
	private static final int UNBOUNDED = 10_000; // sizes at least this large mean "no limit"; leave them be

	// Windows and table columns can't hold client properties; their design and last applied sizes are kept here
	private static final Map<Window, WindowSize> windowSizes = new WeakHashMap<>();
	private static final Map<TableColumn, int[]> columnWidths = new WeakHashMap<>();
	private static boolean installed = false;

	private LayoutScaler() {}

	/** How much larger than designed the current text size makes things. */
	static float scale() {
		return VCellFonts.getSize() / DESIGN_SIZE;
	}

	/** Leaves the component's sizes as code sets them (for sizes already worked out from the text, or exact pixels). */
	static void keepSize(JComponent component) {
		component.putClientProperty(FIXED, Boolean.TRUE);
	}

	/** Scales each component and window as it is first displayed; call before any Swing component exists. */
	static void install() {
		if (LayoutScaler.installed) return;
		LayoutScaler.installed = true;
		// Hierarchy events are delivered as a component is made displayable, which pack() does before it measures
		Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
			if (!(event instanceof HierarchyEvent hierarchyEvent) || (hierarchyEvent.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) == 0) return;
			Component component = hierarchyEvent.getComponent();
			if (!component.isDisplayable() || hierarchyEvent.getChanged() != component) return;
			LayoutScaler.scaleComponent(component);
			if (component instanceof Window window) LayoutScaler.scaleShownWindow(window);
		}, AWTEvent.HIERARCHY_EVENT_MASK);
	}

	/** Brings the sizes set on this one component (not its children) to the current scale. */
	static void scaleComponent(Component component) {
		if (component instanceof JComponent jComponent && !Boolean.TRUE.equals(jComponent.getClientProperty(FIXED))) {
			LayoutScaler.scaleSize(jComponent, KINDS[0], jComponent.isPreferredSizeSet(), jComponent::getPreferredSize, size -> jComponent.setPreferredSize((Dimension) size));
			LayoutScaler.scaleSize(jComponent, KINDS[1], jComponent.isMinimumSizeSet(), jComponent::getMinimumSize, size -> jComponent.setMinimumSize((Dimension) size));
			LayoutScaler.scaleSize(jComponent, KINDS[2], jComponent.isMaximumSizeSet(), jComponent::getMaximumSize, size -> jComponent.setMaximumSize((Dimension) size));
			// positions code gave it in a plain panel without a layout (other containers without one place their own)
			Container parent = jComponent.getParent();
			if (parent instanceof JPanel && null == parent.getLayout()) {
				LayoutScaler.scaleSize(jComponent, BOUNDS, true, jComponent::getBounds, bounds -> jComponent.setBounds((Rectangle) bounds));
			}
		}
		if (component instanceof JTable table) LayoutScaler.scaleColumns(table);
		if (component instanceof JSplitPane splitPane) LayoutScaler.scaleDivider(splitPane);
	}

	// A window's size as designed (for DESIGN_SIZE text) and as last set here
	private record WindowSize(Dimension design, Dimension applied) {}

	/**
	 * Notes how every window is sized before the scale changes, and returns what resizes them once the change has
	 * refreshed their contents: a window that fits its contents exactly (packed) is packed again, so it still fits;
	 * any other keeps its proportions, sized from its design size so that a size the screen once limited isn't lost.
	 */
	static Runnable prepareWindowsForScaleChange() {
		float previousScale = LayoutScaler.scale();
		Set<Window> packed = new HashSet<>();
		for (Window window : Window.getWindows()) {
			if (window.isDisplayable() && !LayoutScaler.isMaximized(window) && window.getSize().equals(window.getPreferredSize())) packed.add(window);
		}
		return () -> {
			for (Window window : Window.getWindows()) {
				if (!window.isDisplayable()) continue;
				if (packed.contains(window)) {
					window.pack();
					LayoutScaler.place(window, window.getSize());
					LayoutScaler.windowSizes.put(window, new WindowSize(LayoutScaler.unscaled(window.getSize(), LayoutScaler.scale()), window.getSize()));
				} else {
					LayoutScaler.scaleWindow(window, previousScale);
				}
				window.validate();
			}
		};
	}

	// As a window is first displayed, or displayed again: sizes code set before showing it are design values; a packed
	// window (no size yet) is sized from its contents, which are already scaled
	private static void scaleShownWindow(Window window) {
		if (window.getWidth() <= 0) return;
		WindowSize sizes = LayoutScaler.windowSizes.get(window);
		if (null == sizes || !window.getSize().equals(sizes.applied())) sizes = new WindowSize(window.getSize(), null);
		LayoutScaler.applyWindowSize(window, sizes.design());
	}

	// A window still at the size last set here keeps its design size; one resized since (by its user, or by code while
	// shown) was resized at the previous scale, which gives its new design size. A window its user maximized stays so.
	private static void scaleWindow(Window window, float previousScale) {
		if (window.getWidth() <= 0) return;
		WindowSize sizes = LayoutScaler.windowSizes.get(window);
		boolean resizedSince = null == sizes || !window.getSize().equals(sizes.applied());
		if (resizedSince && LayoutScaler.isMaximized(window)) return;
		LayoutScaler.applyWindowSize(window, resizedSince ? LayoutScaler.unscaled(window.getSize(), previousScale) : sizes.design());
	}

	private static void applyWindowSize(Window window, Dimension design) {
		Dimension target = (Dimension) LayoutScaler.scaled(design, LayoutScaler.scale());
		LayoutScaler.place(window, target);
		LayoutScaler.windowSizes.put(window, new WindowSize(design, window.getSize()));
	}

	// Sizes the window, no larger than the usable part of the screen it is on, and keeps it on that screen
	private static void place(Window window, Dimension size) {
		GraphicsConfiguration screen = window.getGraphicsConfiguration();
		Rectangle bounds = screen.getBounds();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(screen);
		int width = Math.min(size.width, bounds.width - insets.left - insets.right);
		int height = Math.min(size.height, bounds.height - insets.top - insets.bottom);
		if (width != window.getWidth() || height != window.getHeight()) window.setSize(width, height);
		int x = Math.max(bounds.x + insets.left, Math.min(window.getX(), bounds.x + bounds.width - insets.right - width));
		int y = Math.max(bounds.y + insets.top, Math.min(window.getY(), bounds.y + bounds.height - insets.bottom - height));
		if (x != window.getX() || y != window.getY()) window.setLocation(x, y);
	}

	private static boolean isMaximized(Window window) {
		return window instanceof Frame frame && (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0;
	}

	private static Dimension unscaled(Dimension size, float scale) {
		return new Dimension(Math.round(size.width / scale), Math.round(size.height / scale));
	}

	// A size code set is its design value; one that differs from the size last applied here was set since, and is the
	// new design value
	private static void scaleSize(JComponent component, String kind, boolean set, Supplier<Object> getter, Consumer<Object> setter) {
		String designKey = "VCellFonts.design." + kind, appliedKey = "VCellFonts.applied." + kind;
		if (!set) {
			component.putClientProperty(designKey, null);
			component.putClientProperty(appliedKey, null);
			return;
		}
		Object current = getter.get();
		Object design = current.equals(component.getClientProperty(appliedKey)) ? component.getClientProperty(designKey) : current;
		if (null == design) design = current;
		Object target = LayoutScaler.scaled(design, LayoutScaler.scale());
		if (!target.equals(current)) setter.accept(target);
		component.putClientProperty(designKey, design);
		component.putClientProperty(appliedKey, target);
	}

	private static Object scaled(Object size, float scale) {
		if (size instanceof Rectangle bounds) {
			return new Rectangle(Math.round(bounds.x * scale), Math.round(bounds.y * scale), LayoutScaler.scaled(bounds.width, scale), LayoutScaler.scaled(bounds.height, scale));
		}
		Dimension dimension = (Dimension) size;
		return new Dimension(LayoutScaler.scaled(dimension.width, scale), LayoutScaler.scaled(dimension.height, scale));
	}

	private static int scaled(int length, float scale) {
		return length >= UNBOUNDED ? length : Math.round(length * scale);
	}

	// Divider positions change as people drag them, so one that differs from the position last applied is taken as
	// meant at the current scale; one set before the split pane is first shown is a design value
	private static void scaleDivider(JSplitPane splitPane) {
		int location = splitPane.getDividerLocation();
		if (location <= 0) return; // not set; the split pane places it from its sides' sizes
		float scale = LayoutScaler.scale();
		Object design = splitPane.getClientProperty("VCellFonts.design.divider");
		boolean unchanged = Integer.valueOf(location).equals(splitPane.getClientProperty("VCellFonts.applied.divider"));
		int designLocation = unchanged && design instanceof Integer d ? d : null == design ? location : Math.round(location / scale);
		int target = Math.round(designLocation * scale);
		if (target != location) splitPane.setDividerLocation(target);
		splitPane.putClientProperty("VCellFonts.design.divider", designLocation);
		splitPane.putClientProperty("VCellFonts.applied.divider", target);
	}

	// Column widths change as people drag them, so a preferred width that differs from the one last applied is taken
	// as meant at the current scale; minimum and maximum widths only change in code, so a new one is a design value
	private static void scaleColumns(JTable table) {
		float scale = LayoutScaler.scale();
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			int[] widths = LayoutScaler.columnWidths.get(column); // design and applied {preferred, min, max}
			int[] current = {column.getPreferredWidth(), column.getMinWidth(), column.getMaxWidth()};
			int[] design = new int[3], target = new int[3];
			for (int kind = 0; kind < 3; kind++) {
				boolean unchanged = null != widths && widths[3 + kind] == current[kind];
				design[kind] = unchanged ? widths[kind] : 0 == kind && null != widths ? Math.round(current[kind] / scale) : current[kind];
				target[kind] = LayoutScaler.scaled(design[kind], scale);
			}
			// limits first, so the preferred width isn't clamped to the old ones
			if (target[2] != current[2]) column.setMaxWidth(target[2]);
			if (target[1] != current[1]) column.setMinWidth(target[1]);
			if (target[0] != column.getPreferredWidth()) column.setPreferredWidth(target[0]);
			LayoutScaler.columnWidths.put(column, new int[] {design[0], design[1], design[2], target[0], target[1], target[2]});
		}
	}
}
