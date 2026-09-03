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

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.resource.OperatingSystemInfo;

public class VCellLookAndFeel {
	private final static Logger lg = LogManager.getLogger(VCellLookAndFeel.class);
	private static final Font DEFAULT_FONT = new Font(Font.DIALOG, Font.PLAIN, UIManager.getFont("Label.font").getSize() - (OperatingSystemInfo.getInstance().isMac() ? 2 : 0));

	private static String defaultLookAndFeelTypeString;
	private static OperatingSystemInfo osi;

	static {
		VCellLookAndFeel.osi = OperatingSystemInfo.getInstance();
		if (osi.isLinux()) System.setProperty("awt.useSystemAAFontSettings", "on");
		VCellLookAndFeel.defaultLookAndFeelTypeString = VCellLookAndFeel.osi.isLinux() ? UIManager.getCrossPlatformLookAndFeelClassName() : UIManager.getSystemLookAndFeelClassName();
		System.getProperties().put("swing.component.sizevariant", "regular");
	}

	public static void useCalculatedDefaultVCellLookAndFeel(){
		VCellLookAndFeel.useCalculatedDefaultVCellLookAndFeel(VCellLookAndFeel.defaultLookAndFeelTypeString, VCellLookAndFeel.DEFAULT_FONT);
	}

	public static void useCalculatedDefaultVCellLookAndFeel(String lookAndFeelTypeString, Font font){
		lg.info("Operating system:  {}", VCellLookAndFeel.osi.getOsType());
		lg.info("About to set the look and feel.  Before setting, we're using: {}", UIManager.getLookAndFeel().getName());

		// General Look and Feel
		try {
			UIManager.setLookAndFeel(lookAndFeelTypeString);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			lg.warn("Error while setting look and feel:", e);
		}

		// Font
		VCellLookAndFeel.setVCellFontToDefault(font);

		lg.info("After setting, we're using: {}", UIManager.getLookAndFeel().getName());
	}

	public static void setVCellFontToDefault(){
		VCellLookAndFeel.setVCellFontToDefault(VCellLookAndFeel.DEFAULT_FONT);
	}

	public static void setVCellFontToDefault(Font fontToUse) {
		UIManager.put("Button.font", fontToUse);
		UIManager.put("CheckBox.font", fontToUse);
		UIManager.put("CheckBoxMenuItem.font", fontToUse);
		UIManager.put("ColorChooser.font", fontToUse);
		UIManager.put("ComboBox.font", fontToUse);
		UIManager.put("DesktopIcon.font", fontToUse);
		UIManager.put("EditorPane.font", fontToUse);
		UIManager.put("FileChooser.font", fontToUse);
		UIManager.put("FormattedTextField.font", fontToUse);
		UIManager.put("Label.font", fontToUse);
		UIManager.put("List.font", fontToUse);
		UIManager.put("Menu.font", fontToUse);
		UIManager.put("MenuBar.font", fontToUse);
		UIManager.put("MenuItem.font", fontToUse);
		UIManager.put("OptionPane.font", fontToUse);
		UIManager.put("Panel.font", fontToUse);
		UIManager.put("PasswordField.font", fontToUse);
		UIManager.put("PopupMenu.font", fontToUse);
		UIManager.put("ProgressBar.font", fontToUse);
		UIManager.put("RadioButton.font", fontToUse);
		UIManager.put("RadioButtonMenuItem.font", fontToUse);
		UIManager.put("TabbedPane.font", fontToUse);
		UIManager.put("Table.font", fontToUse);
		UIManager.put("TableHeader.font", fontToUse);
		UIManager.put("TextArea.font", fontToUse);
		UIManager.put("TextField.font", fontToUse);
		UIManager.put("TextPane.font", fontToUse);
		UIManager.put("TitledBorder.font", fontToUse);
		UIManager.put("ToggleButton.font", fontToUse);
		UIManager.put("ToolBar.font", fontToUse);
		UIManager.put("ToolTip.font", fontToUse);
		UIManager.put("Tree.font", fontToUse);
		UIManager.put("Slider.font", fontToUse);
		UIManager.put("ScrollPane.font", fontToUse);
		UIManager.put("Viewport.font", fontToUse);

		UIManager.put("CheckBoxMenuItem.acceleratorFont", fontToUse);
		UIManager.put("InternalFrame.optionDialogTitleFont", fontToUse);
		UIManager.put("InternalFrame.paletteTitleFont", fontToUse);
		UIManager.put("InternalFrame.titleFont", fontToUse);
		UIManager.put("Menu.acceleratorFont", fontToUse);
		UIManager.put("MenuItem.acceleratorFont", fontToUse);
		UIManager.put("OptionPane.buttonFont", fontToUse);
		UIManager.put("OptionPane.messageFont", fontToUse);
		UIManager.put("RadioButtonMenuItem.acceleratorFont", fontToUse);
		UIManager.put("TabbedPane.useSmallLayout", Boolean.TRUE);
	}

	public static Font getDefaultFont() {
		return new Font(
				VCellLookAndFeel.DEFAULT_FONT.getFontName(),
				VCellLookAndFeel.DEFAULT_FONT.getStyle(),
				VCellLookAndFeel.DEFAULT_FONT.getSize()
		);
	}
}
