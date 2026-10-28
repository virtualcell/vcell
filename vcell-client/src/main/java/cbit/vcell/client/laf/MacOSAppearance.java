package cbit.vcell.client.laf;

import com.formdev.flatlaf.ui.FlatNativeMacLibrary;
import com.sun.jna.Pointer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

/**
 * Sets the light / dark appearance of the running application on macOS: window title bars, and the controls the
 * Aqua look and feel has macOS draw (split pane dividers, segmented tabs). The apple.awt.application.appearance
 * system property only takes effect when AWT starts, so a theme chosen later needs AppKit itself.
 */
final class MacOSAppearance {
	private static final Logger lg = LogManager.getLogger(MacOSAppearance.class);

	private MacOSAppearance() {}

	/**
	 * Switches to the dark (DarkAqua) or light (Aqua) appearance; logs and carries on if AppKit can't be reached.
	 * Call it on the Swing event thread: that is where Aqua has macOS draw its controls.
	 */
	static void setDark(boolean dark) {
		try {
			Pointer appearance = ObjectiveC.appearance(dark);
			// The appearance controls are drawn with is kept per thread and does not follow the application's
			ObjectiveC.setCurrentAppearance(appearance);
			Pointer application = ObjectiveC.send("NSApplication", "sharedApplication");
			// AppKit must be changed on its main thread, which is not Swing's; don't wait, AppKit may be waiting on Swing
			ObjectiveC.runtime().objc_msgSend(application, ObjectiveC.selector("performSelectorOnMainThread:withObject:waitUntilDone:"),
					ObjectiveC.selector("setAppearance:"), appearance, false);
		} catch (Throwable e) {
			lg.warn("Could not change the macOS appearance", e);
		}
	}

	/**
	 * FlatLaf gives each window its own appearance on macOS, which overrides the application's, and leaves it in
	 * place when another theme takes over; this sets every window's to dark or light instead. Call it after the
	 * windows' UI has been updated for the new theme.
	 */
	static void setWindowsDark(boolean dark) {
		try {
			if (!FlatNativeMacLibrary.isLoaded()) return; // only FlatLaf's native library sets per-window appearances
			String appearanceName = dark ? "NSAppearanceNameDarkAqua" : "NSAppearanceNameAqua";
			for (Window window : Window.getWindows()) {
				if (window.isDisplayable()) FlatNativeMacLibrary.setWindowAppearance(window, appearanceName);
			}
		} catch (Throwable e) {
			lg.warn("Could not change the macOS window appearances", e);
		}
	}
}
