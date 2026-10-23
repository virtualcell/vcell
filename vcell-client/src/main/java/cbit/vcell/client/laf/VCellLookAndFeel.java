/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;

import java.awt.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import com.jthemedetecor.OsThemeDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.vcell.util.OperatingSystemInfo;
import org.vcell.util.OperatingSystemInfo.OsType;

public class VCellLookAndFeel {
	private static final Logger lg = LogManager.getLogger(VCellLookAndFeel.class);

	private static final Map<OsType, String[]> osLookAndFeelLoadOrder = new EnumMap<>(OsType.class);
	//TODO: Make/generate cross-platform look and feel for VCell using `SynthLookAndFeel` / Synth Architecture system!
	private static final String _DEFAULT_SYSTEM = "_Default System";
	private static final String METAL_BUILT_IN = "MetalLookAndFeel";
	private static final String NIMBUS_BUILT_IN = "Nimbus";
	private static final String FLAT_LIGHT_LAF = "Flat Light";
	private static final String FLAT_DARK_LAF = "Flat Dark";
	private static final String FLAT_INTELLIJ_LAF = "Flat IntelliJ";
	private static final String FLAT_DARCULA_LAF = "Flat Darcula";
	private static final String FLAT_MAC_LIGHT_LAF = "Flat Mac Light";
	private static final String FLAT_MAC_DARK_LAF = "Flat Mac Dark";

	static {
//		OperatingSystemInfo osInfo = OperatingSystemInfo.getInstance();
		OsThemeDetector themeDetector = OsThemeDetector.getDetector();
		boolean osIsDarkTheme = themeDetector.isDark();

		UIManager.installLookAndFeel(FLAT_LIGHT_LAF, com.formdev.flatlaf.FlatLightLaf.class.getName());
		UIManager.installLookAndFeel(FLAT_DARK_LAF, com.formdev.flatlaf.FlatDarkLaf.class.getName());
		UIManager.installLookAndFeel(FLAT_INTELLIJ_LAF, com.formdev.flatlaf.FlatIntelliJLaf.class.getName());
		UIManager.installLookAndFeel(FLAT_DARCULA_LAF, com.formdev.flatlaf.FlatDarculaLaf.class.getName());

		// Windows
		String[] windowsLightThemeOrder = new String[]{_DEFAULT_SYSTEM, NIMBUS_BUILT_IN, METAL_BUILT_IN};
		String[] windowsDarkThemeOrder = new String[]{_DEFAULT_SYSTEM, NIMBUS_BUILT_IN, METAL_BUILT_IN};
		// Mac
		String[] macLightThemeOrder = new String[]{_DEFAULT_SYSTEM, FLAT_LIGHT_LAF, NIMBUS_BUILT_IN, METAL_BUILT_IN};
		String[] macDarkThemeOrder = new String[]{_DEFAULT_SYSTEM, FLAT_DARK_LAF, NIMBUS_BUILT_IN, METAL_BUILT_IN};
		// Linux (*don't* use system theme automatically!)
		String[] linuxLightThemeOrder = new String[]{ FLAT_LIGHT_LAF, NIMBUS_BUILT_IN, METAL_BUILT_IN};
		String[] linuxDarkThemeOrder = new String[]{ FLAT_DARK_LAF, NIMBUS_BUILT_IN, METAL_BUILT_IN};

		VCellLookAndFeel.osLookAndFeelLoadOrder.put(OsType.WINDOWS, osIsDarkTheme ? windowsDarkThemeOrder : windowsLightThemeOrder);
		VCellLookAndFeel.osLookAndFeelLoadOrder.put(OsType.MAC, osIsDarkTheme ? macDarkThemeOrder : macLightThemeOrder);
		VCellLookAndFeel.osLookAndFeelLoadOrder.put(OsType.LINUX, osIsDarkTheme ? linuxDarkThemeOrder : linuxLightThemeOrder);
	}

	public static Font defaultFont = null;

	public static void initializeVCellLookAndFeel() {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		try {
			lg.info("Operating system:  {}", osi.getOsType());
			lg.info("About to set the look and feel.  Before setting, we're using: {}", UIManager.getLookAndFeel().getName());
			String lookAndFeelType = VCellLookAndFeel.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lookAndFeelType);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			lg.warn("Error while setting look and feel:", e);
		}
		defaultFont = calibrateFont(defaultFont, osi.isMac());
		
		System.out.println("After setting, we're using: "+UIManager.getLookAndFeel().getName());
	}

	public static void changeVCellLookAndFeel(Component requester, LookAndFeelInfo lafInfo) {
		if (null == lafInfo) return;
		AsynchClientTask changeTask = new AsynchClientTask("Change LookAndFeel", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				UIManager.setLookAndFeel(lafInfo.getClassName());
				for (Window w : Window.getWindows()) {
					SwingUtilities.updateComponentTreeUI(w);
				}
			}
		};
		ClientTaskDispatcher.dispatch(requester, new Hashtable<>(), new AsynchClientTask[] {changeTask});
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

	private static Font calibrateFont(Font fontToDeriveFrom, boolean isMac){
		if (null != fontToDeriveFrom) return fontToDeriveFrom;
		Font labelFont = UIManager.getFont("Label.font");
		Font newFont = isMac ? labelFont.deriveFont(labelFont.getSize2D() - 2) : labelFont;
		if (!isMac) return newFont;
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

	private 
}
