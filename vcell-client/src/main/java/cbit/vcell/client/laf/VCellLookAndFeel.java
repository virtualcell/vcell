/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.laf;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.resource.VCellConfiguration;
import com.jthemedetecor.OsThemeDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.vcell.util.OperatingSystemInfo;
import org.vcell.util.OperatingSystemInfo.OsType;


public class VCellLookAndFeel {
	private static final Logger lg = LogManager.getLogger(VCellLookAndFeel.class);
	private static final OperatingSystemInfo osInfo = OperatingSystemInfo.getInstance();
	private static final OsThemeDetector themeDetector = OsThemeDetector.getDetector();

	private static final Map<OsType, String[]> osLightLookAndFeelLoadOrder = new EnumMap<>(OsType.class);
	private static final Map<OsType, String[]> osDarkLookAndFeelLoadOrder = new EnumMap<>(OsType.class);
	// The theme a user picked from the menu, and whether it is dark, kept in ~/.vcell/vcellconfig.properties;
	// absent means follow the system's light / dark mode
	private static final String SAVED_THEME_KEY = "vcell.client.theme";
	private static final String SAVED_THEME_IS_DARK_KEY = "vcell.client.theme.dark";
	//TODO: Make/generate cross-platform look and feel for VCell using `SynthLookAndFeel` / Synth Architecture system!
	private static final String _DEFAULT_SYSTEM = "_Default System";
	private static final String METAL_BUILT_IN = "MetalLookAndFeel";
	//private static final String FLAT_LAF_LIGHT = "Flat Simple (Light)";
	private static final String FLAT_INTELLIJ_LAF_LIGHT = "Flat Modern (Light)";
	//private static final String FLAT_LAF_DARK = "Flat Simple (Dark)";
	private static final String FLAT_INTELLIJ_LAF_DARK = "Flat Modern (Dark)";
	private static final String NIMBUS_BUILT_IN = "Nimbus";
	private static final String NIMBUS_DARK_LAF = DarkNimbusLookAndFeel.ID;
	private static final String DARK_AQUA_LAF = DarkAquaLookAndFeel.ID;
	// The classes Synth (Nimbus) swaps default table renderers for
	private static final Class<?>[] SYNTH_RENDERED_CLASSES = { Object.class, Number.class, Double.class, Float.class,
			Date.class, Icon.class, ImageIcon.class, Boolean.class };

	static {
		VCellLookAndFeel.setPlatformProperties(); // must precede any UIManager call; installLookAndFeel already starts AWT
		VCellFonts.followThemes();

		//UIManager.installLookAndFeel(FLAT_LAF_LIGHT, com.formdev.flatlaf.FlatLightLaf.class.getName());
		//UIManager.installLookAndFeel(FLAT_LAF_DARK, com.formdev.flatlaf.FlatDarkLaf.class.getName());
		UIManager.installLookAndFeel(FLAT_INTELLIJ_LAF_LIGHT, com.formdev.flatlaf.FlatIntelliJLaf.class.getName());
		UIManager.installLookAndFeel(FLAT_INTELLIJ_LAF_DARK, com.formdev.flatlaf.FlatDarculaLaf.class.getName());
		UIManager.installLookAndFeel(NIMBUS_DARK_LAF, DarkNimbusLookAndFeel.class.getName());
		if (VCellLookAndFeel.osInfo.isMac()) UIManager.installLookAndFeel(DARK_AQUA_LAF, DarkAquaLookAndFeel.class.getName());

		// Windows
		String[] windowsLightThemeOrder = new String[]{_DEFAULT_SYSTEM, NIMBUS_BUILT_IN, FLAT_INTELLIJ_LAF_LIGHT, METAL_BUILT_IN};
		String[] windowsDarkThemeOrder = new String[]{_DEFAULT_SYSTEM, NIMBUS_DARK_LAF, FLAT_INTELLIJ_LAF_DARK, METAL_BUILT_IN};
		// Mac
		String[] macLightThemeOrder = new String[]{_DEFAULT_SYSTEM, NIMBUS_BUILT_IN, FLAT_INTELLIJ_LAF_LIGHT, METAL_BUILT_IN};
		String[] macDarkThemeOrder = new String[]{DARK_AQUA_LAF, NIMBUS_DARK_LAF, FLAT_INTELLIJ_LAF_DARK, METAL_BUILT_IN};
		// Linux (*don't* use system theme automatically!)
		String[] linuxLightThemeOrder = new String[]{FLAT_INTELLIJ_LAF_LIGHT, NIMBUS_BUILT_IN, METAL_BUILT_IN};
		String[] linuxDarkThemeOrder = new String[]{FLAT_INTELLIJ_LAF_DARK, NIMBUS_DARK_LAF, METAL_BUILT_IN};

		VCellLookAndFeel.osLightLookAndFeelLoadOrder.put(OsType.WINDOWS, windowsLightThemeOrder);
		VCellLookAndFeel.osLightLookAndFeelLoadOrder.put(OsType.MAC, macLightThemeOrder);
		VCellLookAndFeel.osLightLookAndFeelLoadOrder.put(OsType.LINUX, linuxLightThemeOrder);
		VCellLookAndFeel.osDarkLookAndFeelLoadOrder.put(OsType.WINDOWS, windowsDarkThemeOrder);
		VCellLookAndFeel.osDarkLookAndFeelLoadOrder.put(OsType.MAC, macDarkThemeOrder);
		VCellLookAndFeel.osDarkLookAndFeelLoadOrder.put(OsType.LINUX, linuxDarkThemeOrder);
	}

	// VCell keeps editor panels and swaps them in and out of its windows, so a theme switch (which updates only
	// what is in a window at the time) misses the ones out of view. Each switch is numbered; a component that
	// comes into view without the current number is brought up to date the same way.
	private static int lookAndFeelGeneration = 0;
	private static final String LOOK_AND_FEEL_GENERATION = "VCellLookAndFeel.generation";
	private static boolean refreshPending = false;


	/**
	 * The JDK reads these once, when AWT starts, so this must run before any Swing/AWT use.
	 * On macOS the title bar appearance set here holds until a theme switch changes it through AppKit.
	 */
	public static void setPlatformProperties() {
		// Linux desktops other than GNOME often give Java no font smoothing hints, leaving text unsmoothed;
		// a -Dawt.useSystemAAFontSettings=... on the command line still wins
		if (VCellLookAndFeel.osInfo.isLinux() && null == System.getProperty("awt.useSystemAAFontSettings")) {
			System.setProperty("awt.useSystemAAFontSettings", "on");
		}
		if (!VCellLookAndFeel.osInfo.isMac()) return;
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		// matched to the theme VCell is about to start in: a remembered one, or the system's mode
		System.setProperty("apple.awt.application.appearance", VCellLookAndFeel.isStartingDark() ? "NSAppearanceNameDarkAqua" : "NSAppearanceNameAqua");
	}

	/** Whether VCell starts in a dark theme: the remembered theme's mode if the user picked one, else the system's. */
	private static boolean isStartingDark() {
		String savedIsDark = VCellLookAndFeel.getSavedValue(SAVED_THEME_IS_DARK_KEY);
		return null != VCellLookAndFeel.getSavedValue(SAVED_THEME_KEY) && null != savedIsDark
				? Boolean.parseBoolean(savedIsDark) : VCellLookAndFeel.themeDetector.isDark();
	}

	private static String getSavedValue(String key) {
		try {
			return VCellConfiguration.getValue(key, null);
		} catch (Exception e) {
			lg.warn("Could not read {} from the VCell configuration", key, e);
			return null;
		}
	}

	private static void saveValue(String key, String value) {
		try {
			VCellConfiguration.putValue(key, value); // null removes it
		} catch (Exception e) {
			lg.warn("Could not save {} to the VCell configuration", key, e);
		}
	}

	public static Color getContentBackground() {
		return VCellColors.themeColor(() -> {
			Color listBackground = UIManager.getColor("List.background");
			return null == listBackground ? Color.white : listBackground;
		});
	}

	public static void initializeVCellLookAndFeel() {
		lg.info("Operating system:  {}", osInfo.getOsType());
		lg.info("About to set the look and feel.  Before setting, we're using: {}", UIManager.getLookAndFeel().getName());
		// a remembered theme first, then the usual ones for the mode; e.g. Dark Aqua falls back to NimbusDarkCustom
		List<String> lookAndFeelTypes = new ArrayList<>();
		String savedTheme = VCellLookAndFeel.getSavedValue(SAVED_THEME_KEY);
		LookAndFeelInfo savedInfo = VCellLookAndFeel.getInstalledLookAndFeels().get(savedTheme);
		if (null != savedInfo) lookAndFeelTypes.add(savedInfo.getClassName());
		else if (null != savedTheme) lg.info("Remembered theme {} is not available here; following the system", savedTheme);
		boolean dark = null != savedInfo ? VCellLookAndFeel.isStartingDark() : VCellLookAndFeel.themeDetector.isDark();
		lookAndFeelTypes.addAll(VCellLookAndFeel.getLookAndFeelClassNamesToTry(dark));
		VCellLookAndFeel.setFirstAvailableLookAndFeel(lookAndFeelTypes);
		// The title bar was set from the remembered theme before any theme could be checked; if that one was
		// missing or failed to load, match it to the theme VCell ended up in
		boolean endedDark = VCellColors.isDarkTheme();
		if (VCellLookAndFeel.osInfo.isMac() && endedDark != VCellLookAndFeel.isStartingDark()) {
			SwingUtilities.invokeLater(() -> MacOSAppearance.setDark(endedDark));
		}

		lg.info("After setting, we're using: {}", UIManager.getLookAndFeel().getName());
	}

	/** Switches to the theme the user picked, and remembers it for the next start. */
	public static void changeVCellLookAndFeel(Component requester, LookAndFeelInfo lafInfo) {
		if (null == lafInfo) return;
		VCellLookAndFeel.switchLookAndFeel(requester, List.of(lafInfo.getClassName()), lafInfo.getName());
	}

	/** Forgets any theme the user picked and switches to the usual one for the system's current light / dark mode. */
	public static void followSystemLookAndFeel(Component requester) {
		VCellLookAndFeel.switchLookAndFeel(requester, VCellLookAndFeel.getLookAndFeelClassNamesToTry(VCellLookAndFeel.themeDetector.isDark()), null);
	}

	// themeToRemember null: forget the remembered theme, so later starts follow the system
	private static void switchLookAndFeel(Component requester, List<String> lookAndFeelTypes, String themeToRemember) {
		AsynchClientTask changeTask = new AsynchClientTask("Change LookAndFeel", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				if (!VCellLookAndFeel.setFirstAvailableLookAndFeel(lookAndFeelTypes)) {
					throw new UnsupportedLookAndFeelException("Could not set the theme: " + lookAndFeelTypes);
				}
				VCellLookAndFeel.saveValue(SAVED_THEME_KEY, themeToRemember);
				VCellLookAndFeel.saveValue(SAVED_THEME_IS_DARK_KEY, null == themeToRemember ? null : Boolean.toString(VCellColors.isDarkTheme()));
				// title bars, and the controls Aqua has macOS draw, follow the new theme; before the UI update below,
				// which is what makes those controls pick it up
				if (VCellLookAndFeel.osInfo.isMac()) performAdditionalMacSetup();
				VCellLookAndFeel.refreshAllWindows();
				if (VCellLookAndFeel.osInfo.isMac()) MacOSAppearance.setWindowsDark(VCellColors.isDarkTheme());
			}

			private static void performAdditionalMacSetup(){
				boolean dark = VCellColors.isDarkTheme();
				MacOSAppearance.setDark(dark);
				if (!"Aqua".equals(UIManager.getLookAndFeel().getID())) return;
				// Aqua reads its selected-tab title color from macOS once, for the appearance VCell started in,
				// while the tab itself is drawn by macOS in the current one
				Color tabTitle = new ColorUIResource(dark ? Color.white : Color.black);
				UIDefaults uiDefaults = UIManager.getLookAndFeelDefaults();
				uiDefaults.put("TabbedPane.selectedTabTitleNormalColor", tabTitle);
				uiDefaults.put("TabbedPane.focus", tabTitle);
			}
		};
		ClientTaskDispatcher.dispatch(requester, new Hashtable<>(), new AsynchClientTask[] {changeTask});
	}
	/** Installs a fresh copy of the current theme, which reads the current font size as it is installed. */
	static void reinstallLookAndFeel() {
		String current = UIManager.getLookAndFeel().getClass().getName();
		if (!VCellLookAndFeel.setFirstAvailableLookAndFeel(List.of(current))) lg.warn("Could not reinstall {} for a font size change", current);
	}

	/** Brings every window up to the current theme and font size; panels out of view catch up when next shown. */
	static void refreshAllWindows() {
		if (0 == VCellLookAndFeel.lookAndFeelGeneration++) Toolkit.getDefaultToolkit().addAWTEventListener(VCellLookAndFeel::refreshIfStale, AWTEvent.HIERARCHY_EVENT_MASK);
		for (Window w : Window.getWindows()) {
			updateTreeUI(w);
			markCurrent(w);
		}
	}

	private static void refreshIfStale(AWTEvent event) {
		if (VCellLookAndFeel.refreshPending || !(event instanceof HierarchyEvent hierarchyEvent)) return;
		if ((hierarchyEvent.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) == 0) return;
		if (hierarchyEvent.getComponent() instanceof JComponent component && component.isShowing() && isStale(component)) {
			VCellLookAndFeel.refreshPending = true; // once per burst: a panel coming into view reports every one of its descendants
			SwingUtilities.invokeLater(VCellLookAndFeel::refreshStaleComponents);
		}
	}

	/** For the repaint manager: brings any panel that has come into view since a theme switch up to date. */
	public static void refreshStaleComponentsIfPending() {
		if (VCellLookAndFeel.refreshPending) refreshStaleComponents();
	}

	private static void refreshStaleComponents() {
		VCellLookAndFeel.refreshPending = false;
		for (Window w : Window.getWindows()) {
			if (w.isShowing()) refreshStaleComponents(w);
		}
	}

	private static void refreshStaleComponents(Component component) {
		if (component instanceof JComponent jComponent && isStale(jComponent)) {
			updateTreeUI(jComponent);
			markCurrent(jComponent);
			return;
		}
		if (component instanceof Container container) {
			for (Component child : container.getComponents()) refreshStaleComponents(child);
		}
	}

	private static void updateTreeUI(Component root) {
		// This section fixes a bug here where leaving a "Synth" theme puts back the default renderers a table had
		// (before the Synth theme was installed), thereby dropping VCell's renderer
		Map<JTable, Map<Class<?>, TableCellRenderer>> ownRenderers = new HashMap<>();
		VCellLookAndFeel.forEachComponent(root, c -> {
			if (!(c instanceof JTable table)) return;
			Map<Class<?>, TableCellRenderer> renderers = new HashMap<>();
			for (Class<?> type : SYNTH_RENDERED_CLASSES) {
				TableCellRenderer renderer = table.getDefaultRenderer(type);
				if (null != renderer && !(renderer instanceof UIResource)) renderers.put(type, renderer);
			}
			ownRenderers.put(table, renderers);
		});
		// this fixes a bug where a JTree measures its rows before its renderer has the new font, which cuts its labels short.
		SwingUtilities.updateComponentTreeUI(root);
		ownRenderers.forEach((table, renderers) -> renderers.forEach((type, renderer) -> {
			if (table.getDefaultRenderer(type) != renderer) table.setDefaultRenderer(type, renderer);
		}));
		VCellLookAndFeel.forEachComponent(root, c -> { if (c instanceof JTree tree) tree.updateUI(); });
		VCellFonts.refresh(root);
	}

	static void forEachComponent(Component component, Consumer<Component> action) {
		action.accept(component);
		if (!(component instanceof Container container)) return;
		for (Component child : container.getComponents()) VCellLookAndFeel.forEachComponent(child, action);
	}

	private static boolean isStale(JComponent component) {
		return !Integer.valueOf(lookAndFeelGeneration).equals(component.getClientProperty(LOOK_AND_FEEL_GENERATION));
	}

	private static void markCurrent(Component component) {
		forEachComponent(component, c -> {
			if (c instanceof JComponent jComponent) jComponent.putClientProperty(LOOK_AND_FEEL_GENERATION, lookAndFeelGeneration);
		});
	}

	/** The installed look and feel classes to try for this OS in a light or dark mode, in order of preference. */
	private static List<String> getLookAndFeelClassNamesToTry(boolean dark) {
		Map<String, LookAndFeelInfo> lookAndFeels = VCellLookAndFeel.getInstalledLookAndFeels();
		Map<OsType, String[]> loadOrder = dark ? VCellLookAndFeel.osDarkLookAndFeelLoadOrder : VCellLookAndFeel.osLightLookAndFeelLoadOrder;
		List<String> classNames = new ArrayList<>();
		for (String themeToLoad : loadOrder.get(VCellLookAndFeel.osInfo.getOsType())) {
			if (_DEFAULT_SYSTEM.equals(themeToLoad)) classNames.add(UIManager.getSystemLookAndFeelClassName());
			else if (lookAndFeels.containsKey(themeToLoad)) classNames.add(lookAndFeels.get(themeToLoad).getClassName());
		}
		classNames.add(UIManager.getCrossPlatformLookAndFeelClassName());
		return classNames;
	}

	/** Installed look and feels by the name the theme menu shows (and a remembered theme is saved under). */
	private static Map<String, LookAndFeelInfo> getInstalledLookAndFeels() {
		return Arrays.stream(UIManager.getInstalledLookAndFeels())
				.collect(Collectors.toMap(LookAndFeelInfo::getName, Function.identity(), (first, second) -> first));
	}

	/** Sets the first of these look and feel classes that installs; false if none does. */
	private static boolean setFirstAvailableLookAndFeel(List<String> lookAndFeelTypes) {
		for (String lookAndFeelType : lookAndFeelTypes) {
			try {
				UIManager.setLookAndFeel(lookAndFeelType);
				return true;
			} catch (Exception e) {
				lg.warn("Error while setting look and feel {}, trying the next:", lookAndFeelType, e);
			}
		}
		return false;
	}

	public static class LookAndFeelComparator implements Comparator<UIManager.LookAndFeelInfo> {

		/**
		 * Compares its two arguments for order.  Returns a negative integer,
		 * zero, or a positive integer as the first argument is less than, equal
		 * to, or greater than the second.<p>
		 * <p>
		 * The implementor must ensure that {@link Integer#signum
		 * signum}{@code (compare(x, y)) == -signum(compare(y, x))} for
		 * all {@code x} and {@code y}.  (This implies that {@code
		 * compare(x, y)} must throw an exception if and only if {@code
		 * compare(y, x)} throws an exception.)<p>
		 * <p>
		 * The implementor must also ensure that the relation is transitive:
		 * {@code ((compare(x, y)>0) && (compare(y, z)>0))} implies
		 * {@code compare(x, z)>0}.<p>
		 * <p>
		 * Finally, the implementor must ensure that {@code compare(x,
		 * y)==0} implies that {@code signum(compare(x,
		 * z))==signum(compare(y, z))} for all {@code z}.
		 *
		 * @param o1 the first object to be compared.
		 * @param o2 the second object to be compared.
		 * @return a negative integer, zero, or a positive integer as the
		 * first argument is less than, equal to, or greater than the
		 * second.
		 * @throws NullPointerException if an argument is null and this
		 *                              comparator does not permit null arguments
		 * @throws ClassCastException   if the arguments' types prevent them from
		 *                              being compared by this comparator.
		 * @apiNote It is generally the case, but <i>not</i> strictly required that
		 * {@code (compare(x, y)==0) == (x.equals(y))}.  Generally speaking,
		 * any comparator that violates this condition should clearly indicate
		 * this fact.  The recommended language is "Note: this comparator
		 * imposes orderings that are inconsistent with equals."
		 */
		@Override
		public int compare(LookAndFeelInfo o1, LookAndFeelInfo o2) {
			if (null == o1) throw new NullPointerException("first argument is null");
			if (null == o2) throw new NullPointerException("second argument is null");
			String s1 = o1.getName();
			if (null == s1) throw new NullPointerException("firstArgument::getName is null");
			String s2 = o2.getName();
			if (null == s2) throw new NullPointerException("secondArgument::getName is null");
			return s1.compareTo(s2);
		}
	}
}
