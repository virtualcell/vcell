package cbit.vcell.client.laf;

import com.formdev.flatlaf.ui.FlatNativeMacLibrary;
import com.sun.jna.Pointer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * Sets the light / dark appearance of the running application on macOS: window title bars, and the controls the
 * Aqua look and feel has macOS draw (buttons, text fields, segmented tabs, scroll bars). The
 * apple.awt.application.appearance system property only takes effect if it is set before AWT starts, which a launcher
 * showing a splash screen (install4j's) has already done by the time VCell runs, so VCell sets it through AppKit itself.
 */
final class MacOSAppearance {
	private static final Logger lg = LogManager.getLogger(MacOSAppearance.class);
	private static Boolean dark = null; // as last set; null until then
	private static Thread setOnThread = null; // the event thread it was last set on
	private static boolean followingEventThreads = false;

	private MacOSAppearance() {}

	/**
	 * Switches to the dark (DarkAqua) or light (Aqua) appearance; logs and carries on if AppKit can't be reached.
	 * Call it on the Swing event thread: that is where Aqua has macOS draw its controls.
	 */
	static void setDark(boolean dark) {
		MacOSAppearance.dark = dark;
		MacOSAppearance.followEventThreads();
		try {
			Pointer appearance = ObjectiveC.appearance(dark);
			// The appearance controls are drawn with is kept per thread and does not follow the application's
			ObjectiveC.setCurrentAppearance(appearance);
			MacOSAppearance.setOnThread = Thread.currentThread();
			Pointer application = ObjectiveC.send("NSApplication", "sharedApplication");
			// AppKit must be changed on its main thread, which is not Swing's; don't wait, AppKit may be waiting on Swing
			ObjectiveC.runtime().objc_msgSend(application, ObjectiveC.selector("performSelectorOnMainThread:withObject:waitUntilDone:"),
					ObjectiveC.selector("setAppearance:"), appearance, false);
		} catch (Throwable e) {
			lg.warn("Could not change the macOS appearance", e);
		}
	}

	/**
	 * Sets it on the event thread, waiting for that; for the theme VCell starts in, before the theme is installed (it reads
	 * some of macOS's colors as it installs).
	 */
	static void setDarkOnEventThread(boolean dark) {
		if (EventQueue.isDispatchThread()) {
			MacOSAppearance.setDark(dark);
			return;
		}
		try {
			EventQueue.invokeAndWait(() -> MacOSAppearance.setDark(dark));
		} catch (Exception e) {
			lg.warn("Could not set the macOS appearance on the event thread", e);
		}
	}

	// AWT replaces its event thread when no window is left open (before VCell's first one, say), and the appearance set
	// on the old one is lost with it: set it again on the new one, as a window opens there
	private static synchronized void followEventThreads() {
		if (MacOSAppearance.followingEventThreads) return;
		MacOSAppearance.followingEventThreads = true;
		Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
			if (WindowEvent.WINDOW_OPENED != event.getID() || null == MacOSAppearance.dark) return;
			if (Thread.currentThread() == MacOSAppearance.setOnThread) return;
			try {
				ObjectiveC.setCurrentAppearance(ObjectiveC.appearance(MacOSAppearance.dark));
				MacOSAppearance.setOnThread = Thread.currentThread();
			} catch (Throwable e) {
				lg.warn("Could not set the macOS appearance on a new event thread", e);
			}
		}, AWTEvent.WINDOW_EVENT_MASK);
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
