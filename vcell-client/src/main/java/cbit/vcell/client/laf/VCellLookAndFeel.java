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
import com.jthemedetecor.OsThemeDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.vcell.util.OperatingSystemInfo;
import org.vcell.util.OperatingSystemInfo.OsType;

public class VCellLookAndFeel {
	private static final Logger lg = LogManager.getLogger(VCellLookAndFeel.class);
	private static final OperatingSystemInfo osInfo = OperatingSystemInfo.getInstance();
	private static final OsThemeDetector themeDetector = OsThemeDetector.getDetector();

	private static final Map<OsType, String[]> osLookAndFeelLoadOrder = new EnumMap<>(OsType.class);
	//TODO: Make/generate cross-platform look and feel for VCell using `SynthLookAndFeel` / Synth Architecture system!
	private static final String _DEFAULT_SYSTEM = "_Default System";
	private static final String METAL_BUILT_IN = "MetalLookAndFeel";
	private static final String NIMBUS_BUILT_IN = "Nimbus";
	private static final String FLAT_LIGHT_LAF = "Flat Light";
	private static final String FLAT_DARK_LAF = "Flat Dark";
	private static final String FLAT_INTELLIJ_LAF = "Flat IntelliJ";
	private static final String FLAT_DARCULA_LAF = "Flat Darcula";
	private static final String CUSTOM_MAC_DARK_LAF = NimbusDarkLookAndFeel.ID;

	static {
		VCellLookAndFeel.setMacOSWindowProperties(); // must precede any UIManager call; installLookAndFeel already starts AWT
		boolean osIsDarkTheme = themeDetector.isDark();

		UIManager.installLookAndFeel(FLAT_LIGHT_LAF, com.formdev.flatlaf.FlatLightLaf.class.getName());
		UIManager.installLookAndFeel(FLAT_DARK_LAF, com.formdev.flatlaf.FlatDarkLaf.class.getName());
		UIManager.installLookAndFeel(FLAT_INTELLIJ_LAF, com.formdev.flatlaf.FlatIntelliJLaf.class.getName());
		UIManager.installLookAndFeel(FLAT_DARCULA_LAF, com.formdev.flatlaf.FlatDarculaLaf.class.getName());
		if (osInfo.isMac()) UIManager.installLookAndFeel(CUSTOM_MAC_DARK_LAF, NimbusDarkLookAndFeel.class.getName());

		// Windows
		String[] windowsLightThemeOrder = new String[]{_DEFAULT_SYSTEM, NIMBUS_BUILT_IN, METAL_BUILT_IN};
		String[] windowsDarkThemeOrder = new String[]{_DEFAULT_SYSTEM, NIMBUS_BUILT_IN, METAL_BUILT_IN};
		// Mac
		String[] macLightThemeOrder = new String[]{_DEFAULT_SYSTEM, FLAT_LIGHT_LAF, NIMBUS_BUILT_IN, METAL_BUILT_IN};
		String[] macDarkThemeOrder = new String[]{CUSTOM_MAC_DARK_LAF, FLAT_DARK_LAF, NIMBUS_BUILT_IN, METAL_BUILT_IN};
		// Linux (*don't* use system theme automatically!)
		String[] linuxLightThemeOrder = new String[]{ FLAT_LIGHT_LAF, NIMBUS_BUILT_IN, METAL_BUILT_IN};
		String[] linuxDarkThemeOrder = new String[]{ FLAT_DARK_LAF, NIMBUS_BUILT_IN, METAL_BUILT_IN};

		VCellLookAndFeel.osLookAndFeelLoadOrder.put(OsType.WINDOWS, osIsDarkTheme ? windowsDarkThemeOrder : windowsLightThemeOrder);
		VCellLookAndFeel.osLookAndFeelLoadOrder.put(OsType.MAC, osIsDarkTheme ? macDarkThemeOrder : macLightThemeOrder);
		VCellLookAndFeel.osLookAndFeelLoadOrder.put(OsType.LINUX, osIsDarkTheme ? linuxDarkThemeOrder : linuxLightThemeOrder);
	}

	public static Font defaultFont = null;

	/**
	 * macOS reads these once, when AWT starts, so this must run before any Swing/AWT use.
	 * The title bar appearance cannot change afterwards, even if the look and feel does.
	 */
	public static void setMacOSWindowProperties() {
		if (!osInfo.isMac()) return;
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		if (themeDetector.isDark()) System.setProperty("apple.awt.application.appearance", "NSAppearanceNameDarkAqua");
	}

	public static Color getContentBackground() {
		return VCellColors.themeColor(() -> {
			Color listBackground = UIManager.getColor("List.background");
			return null == listBackground ? Color.white : listBackground;
		});
	}

	public static void initializeVCellLookAndFeel() {
		try {
			lg.info("Operating system:  {}", osInfo.getOsType());
			lg.info("About to set the look and feel.  Before setting, we're using: {}", UIManager.getLookAndFeel().getName());
			String lookAndFeelType = VCellLookAndFeel.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lookAndFeelType);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			lg.warn("Error while setting look and feel:", e);
		}
		defaultFont = calibrateFont(defaultFont);
		
		System.out.println("After setting, we're using: "+UIManager.getLookAndFeel().getName());
	}

	public static void changeVCellLookAndFeel(Component requester, LookAndFeelInfo lafInfo) {
		if (null == lafInfo) return;
		AsynchClientTask changeTask = new AsynchClientTask("Change LookAndFeel", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				UIManager.setLookAndFeel(lafInfo.getClassName());
				// title bars, and the controls Aqua has macOS draw, follow the new theme; before the UI update below,
				// which is what makes those controls pick it up
				if (osInfo.isMac()) {
					boolean dark = VCellColors.isDarkTheme();
					MacOSAppearance.setDark(dark);
					if (UIManager.getLookAndFeel().isNativeLookAndFeel()) {
						// Aqua reads its selected-tab title color from macOS once, for the appearance VCell started in,
						// while the tab itself is drawn by macOS in the current one
						Color tabTitle = new ColorUIResource(dark ? Color.white : Color.black);
						UIManager.getLookAndFeelDefaults().put("TabbedPane.selectedTabTitleNormalColor", tabTitle);
						UIManager.getLookAndFeelDefaults().put("TabbedPane.focus", tabTitle);
					}
				}
				if (0 == lookAndFeelGeneration++) {
					Toolkit.getDefaultToolkit().addAWTEventListener(VCellLookAndFeel::refreshIfStale, AWTEvent.HIERARCHY_EVENT_MASK);
				}
				for (Window w : Window.getWindows()) {
					updateTreeUI(w);
					markCurrent(w);
				}
			}
		};
		ClientTaskDispatcher.dispatch(requester, new Hashtable<>(), new AsynchClientTask[] {changeTask});
	}

	// VCell keeps editor panels and swaps them in and out of its windows, so a theme switch (which updates only
	// what is in a window at the time) misses the ones out of view. Each switch is numbered; a component that
	// comes into view without the current number is brought up to date the same way.
	private static int lookAndFeelGeneration = 0;
	private static final String LOOK_AND_FEEL_GENERATION = "VCellLookAndFeel.generation";
	private static boolean refreshPending = false;

	private static void refreshIfStale(AWTEvent event) {
		if (refreshPending || !(event instanceof HierarchyEvent hierarchyEvent)) return;
		if ((hierarchyEvent.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) == 0) return;
		if (hierarchyEvent.getComponent() instanceof JComponent component && component.isShowing() && isStale(component)) {
			refreshPending = true; // once per burst: a panel coming into view reports every one of its descendants
			SwingUtilities.invokeLater(VCellLookAndFeel::refreshStaleComponents);
		}
	}

	private static void refreshStaleComponents() {
		refreshPending = false;
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

	// The classes Synth (Nimbus) swaps default table renderers for
	private static final Class<?>[] SYNTH_RENDERED_CLASSES = { Object.class, Number.class, Double.class, Float.class,
			Date.class, Icon.class, ImageIcon.class, Boolean.class };

	/**
	 * SwingUtilities.updateComponentTreeUI, plus two fixes for gaps in Swing itself: leaving a Synth theme puts back
	 * the default renderers a table had before Synth was installed, dropping VCell's own; and a JTree measures its
	 * rows before its renderer has the new font, which cuts its labels short.
	 */
	private static void updateTreeUI(Component root) {
		Map<JTable, Map<Class<?>, TableCellRenderer>> ownRenderers = new HashMap<>();
		forEachComponent(root, c -> {
			if (!(c instanceof JTable table)) return;
			Map<Class<?>, TableCellRenderer> renderers = new HashMap<>();
			for (Class<?> type : SYNTH_RENDERED_CLASSES) {
				TableCellRenderer renderer = table.getDefaultRenderer(type);
				if (null != renderer && !(renderer instanceof UIResource)) renderers.put(type, renderer);
			}
			ownRenderers.put(table, renderers);
		});
		SwingUtilities.updateComponentTreeUI(root);
		ownRenderers.forEach((table, renderers) -> renderers.forEach((type, renderer) -> {
			if (table.getDefaultRenderer(type) != renderer) table.setDefaultRenderer(type, renderer);
		}));
		forEachComponent(root, c -> { if (c instanceof JTree tree) tree.updateUI(); });
	}

	private static void forEachComponent(Component component, Consumer<Component> action) {
		action.accept(component);
		if (component instanceof Container container) {
			for (Component child : container.getComponents()) forEachComponent(child, action);
		}
	}

	private static boolean isStale(JComponent component) {
		return !Integer.valueOf(lookAndFeelGeneration).equals(component.getClientProperty(LOOK_AND_FEEL_GENERATION));
	}

	private static void markCurrent(Component component) {
		forEachComponent(component, c -> {
			if (c instanceof JComponent jComponent) jComponent.putClientProperty(LOOK_AND_FEEL_GENERATION, lookAndFeelGeneration);
		});
	}

	private static String getCrossPlatformLookAndFeelClassName() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Map<String, LookAndFeelInfo> lookAndFeels = Arrays.stream(UIManager.getInstalledLookAndFeels())
				.collect(Collectors.toMap(LookAndFeelInfo::getName, Function.identity()));
		OsType osType = OperatingSystemInfo.getInstance().getOsType();
		for (String themeToLoad : VCellLookAndFeel.osLookAndFeelLoadOrder.get(osType)) {
			if (_DEFAULT_SYSTEM.equals(themeToLoad)) return UIManager.getSystemLookAndFeelClassName();
			if (!lookAndFeels.containsKey(themeToLoad)) continue;
			LookAndFeelInfo info = lookAndFeels.get(themeToLoad);
			return info.getClassName();
		}
		return UIManager.getCrossPlatformLookAndFeelClassName();
	}

	private static Font calibrateFont(Font fontToDeriveFrom){
		if (null != fontToDeriveFrom) return fontToDeriveFrom;
		Font labelFont = UIManager.getFont("Label.font");
		Font newFont = osInfo.isMac() ? labelFont.deriveFont(labelFont.getSize2D() - 2) : labelFont;
		if (!osInfo.isMac()) return newFont;
		UIManager.put("Button.font",newFont);
		UIManager.put("CheckBox.font",newFont);
		UIManager.put("CheckBoxMenuItem.font",newFont);
		UIManager.put("ColorChooser.font",newFont);
		UIManager.put("ComboBox.font",newFont);
		UIManager.put("DesktopIcon.font",newFont);
		UIManager.put("EditorPane.font",newFont);
		UIManager.put("FileChooser.font", newFont);
		UIManager.put("FormattedTextField.font", newFont);
		UIManager.put("Label.font",newFont);
		UIManager.put("List.font",newFont);
		UIManager.put("Menu.font", newFont);
		UIManager.put("MenuBar.font", newFont);
		UIManager.put("MenuItem.font", newFont);
		UIManager.put("OptionPane.font",newFont);
		UIManager.put("Panel.font",newFont);
		UIManager.put("PasswordField.font",newFont);
		UIManager.put("PopupMenu.font",newFont);
		UIManager.put("ProgressBar.font",newFont);
		UIManager.put("RadioButton.font",newFont);
		UIManager.put("RadioButtonMenuItem.font",newFont);
		UIManager.put("TabbedPane.font",newFont);
		UIManager.put("Table.font",newFont);
		UIManager.put("TableHeader.font", newFont);
		UIManager.put("TextArea.font",newFont);
		UIManager.put("TextField.font",newFont);
		UIManager.put("TextPane.font",newFont);
		UIManager.put("TitledBorder.font",newFont);
		UIManager.put("ToggleButton.font",newFont);
		UIManager.put("ToolBar.font", newFont);
		UIManager.put("ToolTip.font", newFont);
		UIManager.put("Tree.font", newFont);
		UIManager.put("Slider.font", newFont);
		UIManager.put("ScrollPane.font", newFont);
		UIManager.put("Viewport.font", newFont);

		UIManager.put("CheckBoxMenuItem.acceleratorFont", newFont);
		UIManager.put("InternalFrame.optionDialogTitleFont", newFont);
		UIManager.put("InternalFrame.paletteTitleFont", newFont);
		UIManager.put("InternalFrame.titleFont", newFont);
		UIManager.put("Menu.acceleratorFont", newFont);
		UIManager.put("MenuItem.acceleratorFont", newFont);
		UIManager.put("OptionPane.buttonFont", newFont);
		UIManager.put("OptionPane.messageFont", newFont);
		UIManager.put("RadioButtonMenuItem.acceleratorFont", newFont);
		UIManager.put("TabbedPane.useSmallLayout", Boolean.TRUE);
		System.getProperties().put("swing.component.sizevariant", "regular");
		return newFont;
	}
}
