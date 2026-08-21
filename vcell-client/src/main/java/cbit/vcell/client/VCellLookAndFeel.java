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
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import cbit.vcell.resource.ResourceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.resource.OperatingSystemInfo;

public class VCellLookAndFeel {
	private final static Logger lg = LogManager.getLogger(VCellLookAndFeel.class);

	/**
	 * Multiplies every font the look and feel supplies. 1.0 (the default) leaves the UI exactly
	 * as it was.
	 * <p>
	 * This only reaches fonts obtained <em>implicitly</em>, from the look and feel's defaults. A
	 * component whose font was set explicitly - {@code setFont(new Font("Dialog", PLAIN, 12))} -
	 * keeps that font, because a plain {@code Font} is not a {@link javax.swing.plaf.UIResource}
	 * and the look and feel will not replace it. Those sites have to be converted separately.
	 */
	public static final String PROPERTY_FONT_SCALE = "vcell.ui.fontScale";

	private static final float MIN_FONT_SCALE = 0.5f;
	private static final float MAX_FONT_SCALE = 4.0f;

	/** Resolved once: the property cannot change while the client is running. */
	private static volatile Float cachedFontScale = null;

	/**
	 * @return the requested font scale, or 1.0 if unset, unparseable or out of range - a bad value
	 *         here must never stop the client from starting.
	 */
	public static float getFontScale() {
		Float scale = cachedFontScale;
		if (scale == null) {
			scale = computeFontScale();
			cachedFontScale = scale;
		}
		return scale;
	}

	/**
	 * Scales a hard-coded pixel dimension that exists to fit text - a split-pane divider, a
	 * minimum size, a column width. Such a constant was chosen against the default font, so it
	 * has to move with the font or the text it was sized for no longer fits.
	 * <p>
	 * This is for dimensions that <em>bound text</em>. Do not use it for icon sizes, insets or
	 * borders, which should stay where they are.
	 */
	public static int scaleTextPixels(int pixels) {
		return Math.round(pixels * getFontScale());
	}

	private static float computeFontScale() {
		final String raw = System.getProperty(PROPERTY_FONT_SCALE);
		if (raw == null || raw.trim().isEmpty()) {
			return 1.0f;
		}
		final float scale;
		try {
			scale = Float.parseFloat(raw.trim());
		} catch (NumberFormatException e) {
			lg.warn("ignoring " + PROPERTY_FONT_SCALE + "='" + raw + "': not a number");
			return 1.0f;
		}
		if (scale < MIN_FONT_SCALE || scale > MAX_FONT_SCALE) {
			lg.warn("ignoring " + PROPERTY_FONT_SCALE + "=" + scale + ": outside ["
					+ MIN_FONT_SCALE + ", " + MAX_FONT_SCALE + "]");
			return 1.0f;
		}
		return scale;
	}

	/**
	 * Scales every {@link Font} in the look and feel's defaults, in place, before any window is
	 * built. Must run after {@code setLookAndFeel} (which replaces the whole defaults table) and
	 * before anything reads a font, because Swing components resolve their font once, at
	 * construction.
	 */
	private static void applyFontScale(float scale) {
		if (scale == 1.0f) {
			return;
		}
		final UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		// snapshot the keys: resolving a lazy value can add entries, and we are writing as we go
		final List<Object> keys = new ArrayList<>(defaults.keySet());
		int count = 0;
		for (Object key : keys) {
			final Object value;
			try {
				value = defaults.get(key);      // resolves LazyValue / ActiveValue
			} catch (Exception e) {
				continue;                       // a defaults entry that cannot be resolved is not ours to fix
			}
			if (value instanceof Font) {
				final Font font = (Font) value;
				// FontUIResource, not Font: a plain Font would be treated as a user-set override
				UIManager.put(key, new FontUIResource(font.deriveFont(font.getSize2D() * scale)));
				count++;
			}
		}
		lg.info("scaled " + count + " look-and-feel fonts by " + scale
				+ " (" + PROPERTY_FONT_SCALE + ")");
	}

	public static Font defaultFont = null;
	public static void setVCellLookAndFeel() {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
//		if (!ResourceUtil.bLinux) {
			//changed to see if SystemLookAndFeel on Linux works better than the default CrossPlatformLookAndFeel (aka Metal)
			try {
				lg.info("Operating system:  " + osi.getOsType());
				lg.info("About to set the look and feel.  Before setting, we're using: " + UIManager.getLookAndFeel().getName());
				String lookAndFeelType = OperatingSystemInfo.getInstance().isLinux() ?
						UIManager.getCrossPlatformLookAndFeelClassName() : UIManager.getSystemLookAndFeelClassName();
				UIManager.setLookAndFeel(lookAndFeelType);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
				lg.warn("Error while setting look and feel:", e);
			}
//		}
		// before anything reads a font: setLookAndFeel above replaced the defaults table, and the
		// Mac block below derives from Label.font, so it inherits the scale rather than undoing it.
		applyFontScale(getFontScale());

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
	        System.getProperties().put("swing.component.sizevariant", "regular");
		}
		
		System.out.println("After setting, we're using: "+UIManager.getLookAndFeel().getName());
	}
}
