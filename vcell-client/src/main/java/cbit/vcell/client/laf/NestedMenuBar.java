package cbit.vcell.client.laf;

import javax.swing.*;
import javax.swing.plaf.MenuBarUI;

/**
 * A menu bar placed inside a window's content, rather than installed with {@code setJMenuBar}.
 * <p>
 * With {@code apple.laf.useScreenMenuBar} on (see {@link VCellLookAndFeel#setMacOSWindowProperties()}), the JDK gives
 * every JMenuBar Aqua's MenuBarUI, whatever the look and feel, so that window menu bars can move to the macOS menu
 * bar. A menu bar nested in content is painted in place instead, and Aqua's painter throws a NullPointerException
 * under any other look and feel. This one always takes the current look and feel's own UI.
 */
@SuppressWarnings("serial")
public class NestedMenuBar extends JMenuBar {
	private static LookAndFeel cachedLookAndFeel = null;
	private static UIDefaults cachedDefaults = null;

	@Override
	public void updateUI() {
		setUI((MenuBarUI) getLookAndFeelDefaults().getUI(this));
	}

	// The look and feel's own defaults, without the screen-menu-bar override; cached because getDefaults() may rebuild them
	private static synchronized UIDefaults getLookAndFeelDefaults() {
		LookAndFeel current = UIManager.getLookAndFeel();
		if (current != cachedLookAndFeel) {
			cachedLookAndFeel = current;
			cachedDefaults = current.getDefaults();
		}
		return cachedDefaults;
	}
}
