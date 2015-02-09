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

import cbit.vcell.resource.OperatingSystemInfo;

public class VCellLookAndFeel {
	
	public static Font defaultFont = null;
	public static void setVCellLookAndFeel() {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
//		if (!ResourceUtil.bLinux) {
			//changed to see if SystemLookAndFeel on Linux works better than the default CrossPlatformLookAndFeel (aka Metal)
			try {
				System.out.println("Operating system:  " + osi.getOsType());
				System.out.println("About to set the look and feel.  Before setting, we're using: "+UIManager.getLookAndFeel().getName());
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
//		}
		final boolean isMac = osi.isMac();
			
		if (defaultFont == null) {			
			defaultFont = UIManager.getFont("Label.font");		
			if (isMac) {
				defaultFont = defaultFont.deriveFont(defaultFont.getSize2D() - 2);		
			}
		}
		if (isMac) {
	        UIManager.put("Button.font",defaultFont);
	        UIManager.put("CheckBox.font",defaultFont);
	        UIManager.put("CheckBoxMenuItem.font",defaultFont);
	        UIManager.put("ColorChooser.font",defaultFont);
	        UIManager.put("ComboBox.font",defaultFont);
	        UIManager.put("DesktopIcon.font",defaultFont);
	        UIManager.put("EditorPane.font",defaultFont);
	        UIManager.put("FileChooser.font", defaultFont);
	        UIManager.put("FormattedTextField.font", defaultFont);
	        UIManager.put("Label.font",defaultFont);
	        UIManager.put("List.font",defaultFont);
	        UIManager.put("Menu.font", defaultFont);
	        UIManager.put("MenuBar.font", defaultFont);
	        UIManager.put("MenuItem.font", defaultFont);
	        UIManager.put("OptionPane.font",defaultFont);
	        UIManager.put("Panel.font",defaultFont);
	        UIManager.put("PasswordField.font",defaultFont);
	        UIManager.put("PopupMenu.font",defaultFont);
	        UIManager.put("ProgressBar.font",defaultFont);
	        UIManager.put("RadioButton.font",defaultFont);
	        UIManager.put("RadioButtonMenuItem.font",defaultFont);
	        UIManager.put("TabbedPane.font",defaultFont);
	        UIManager.put("Table.font",defaultFont);
	        UIManager.put("TableHeader.font", defaultFont);
	        UIManager.put("TextArea.font",defaultFont);
	        UIManager.put("TextField.font",defaultFont);
	        UIManager.put("TextPane.font",defaultFont);
	        UIManager.put("TitledBorder.font",defaultFont);
	        UIManager.put("ToggleButton.font",defaultFont);
	        UIManager.put("ToolBar.font", defaultFont);
	        UIManager.put("ToolTip.font", defaultFont);
	        UIManager.put("Tree.font", defaultFont);
	        UIManager.put("Slider.font", defaultFont);
	        UIManager.put("ScrollPane.font", defaultFont);
	        UIManager.put("Viewport.font", defaultFont);
	        
	        UIManager.put("CheckBoxMenuItem.acceleratorFont", defaultFont);
	        UIManager.put("InternalFrame.optionDialogTitleFont", defaultFont);
	        UIManager.put("InternalFrame.paletteTitleFont", defaultFont);
	        UIManager.put("InternalFrame.titleFont", defaultFont);
	        UIManager.put("Menu.acceleratorFont", defaultFont);
	        UIManager.put("MenuItem.acceleratorFont", defaultFont);
	        UIManager.put("OptionPane.buttonFont", defaultFont);
	        UIManager.put("OptionPane.messageFont", defaultFont);
	        UIManager.put("RadioButtonMenuItem.acceleratorFont", defaultFont);
	        UIManager.put("TabbedPane.useSmallLayout", Boolean.TRUE);
	        
//	        System.setProperty("apple.laf.useScreenMenuBar", "true");
	        System.getProperties().put("swing.component.sizevariant", "small");
		}
		
		System.out.println("After setting, we're using: "+UIManager.getLookAndFeel().getName());
	}
}
