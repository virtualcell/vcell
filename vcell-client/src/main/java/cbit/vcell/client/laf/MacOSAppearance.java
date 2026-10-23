package cbit.vcell.client.laf;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Sets the light / dark appearance of the running application on macOS: window title bars, and the controls the
 * Aqua look and feel has macOS draw (split pane dividers, segmented tabs). The apple.awt.application.appearance
 * system property only takes effect when AWT starts, so a theme chosen later needs AppKit itself.
 */
final class MacOSAppearance {
	private static final Logger lg = LogManager.getLogger(MacOSAppearance.class);

	// objc_msgSend is declared once per call shape, never as varargs: arm64 passes variadic arguments differently
	private interface ObjC extends Library {
		Pointer objc_getClass(String name);
		Pointer sel_registerName(String name);
		Pointer objc_msgSend(Pointer receiver, Pointer selector);
		Pointer objc_msgSend(Pointer receiver, Pointer selector, String argument);
		Pointer objc_msgSend(Pointer receiver, Pointer selector, Pointer argument);
		void objc_msgSend(Pointer receiver, Pointer selector, Pointer selectorArgument, Pointer objectArgument, boolean waitUntilDone);
	}

	private MacOSAppearance() {}

	/**
	 * Switches to the dark (DarkAqua) or light (Aqua) appearance; logs and carries on if AppKit can't be reached.
	 * Call it on the Swing event thread: that is where Aqua has macOS draw its controls.
	 */
	static void setDark(boolean dark) {
		try {
			ObjC objc = Native.load("objc", ObjC.class);
			Pointer appearanceName = objc.objc_msgSend(objc.objc_getClass("NSString"), objc.sel_registerName("stringWithUTF8String:"),
					dark ? "NSAppearanceNameDarkAqua" : "NSAppearanceNameAqua");
			Pointer appearance = objc.objc_msgSend(objc.objc_getClass("NSAppearance"), objc.sel_registerName("appearanceNamed:"), appearanceName);
			// The appearance controls are drawn with is kept per thread and does not follow the application's
			objc.objc_msgSend(objc.objc_getClass("NSAppearance"), objc.sel_registerName("setCurrentAppearance:"), appearance);
			Pointer application = objc.objc_msgSend(objc.objc_getClass("NSApplication"), objc.sel_registerName("sharedApplication"));
			// AppKit must be changed on its main thread, which is not Swing's; don't wait, AppKit may be waiting on Swing
			objc.objc_msgSend(application, objc.sel_registerName("performSelectorOnMainThread:withObject:waitUntilDone:"),
					objc.sel_registerName("setAppearance:"), appearance, false);
		} catch (Throwable e) {
			lg.warn("Could not change the macOS appearance", e);
		}
	}
}
