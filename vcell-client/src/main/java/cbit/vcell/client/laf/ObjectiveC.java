package cbit.vcell.client.laf;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/** A minimal binding to the Objective-C runtime (macOS only), for the few AppKit calls VCell's themes make. */
final class ObjectiveC {
	// objc_msgSend is declared once per call shape, never as varargs: arm64 passes variadic arguments differently
	interface Runtime extends Library {
		Pointer objc_getClass(String name);
		Pointer sel_registerName(String name);
		Pointer objc_msgSend(Pointer receiver, Pointer selector);
		Pointer objc_msgSend(Pointer receiver, Pointer selector, String argument);
		Pointer objc_msgSend(Pointer receiver, Pointer selector, Pointer argument);
		void objc_msgSend(Pointer receiver, Pointer selector, Pointer selectorArgument, Pointer objectArgument, boolean waitUntilDone);
		void objc_msgSend(Pointer receiver, Pointer selector, Pointer out1, Pointer out2, Pointer out3, Pointer out4);
	}

	private static Runtime runtime = null;

	private ObjectiveC() {}

	/** Loaded on first use, so nothing touches libobjc off macOS. */
	static synchronized Runtime runtime() {
		if (null == runtime) runtime = Native.load("objc", Runtime.class);
		return runtime;
	}

	static Pointer getClass(String name) { return runtime().objc_getClass(name); }

	static Pointer selector(String name) { return runtime().sel_registerName(name); }

	/** [className selectorName] */
	static Pointer send(String className, String selectorName) {
		return runtime().objc_msgSend(getClass(className), selector(selectorName));
	}

	/** The named NSAppearance: dark (DarkAqua) or light (Aqua). */
	static Pointer appearance(boolean dark) {
		Pointer name = runtime().objc_msgSend(getClass("NSString"), selector("stringWithUTF8String:"),
				dark ? "NSAppearanceNameDarkAqua" : "NSAppearanceNameAqua");
		return runtime().objc_msgSend(getClass("NSAppearance"), selector("appearanceNamed:"), name);
	}

	/** Sets the appearance AppKit uses on the calling thread (to resolve colors and draw controls). */
	static void setCurrentAppearance(Pointer appearance) {
		runtime().objc_msgSend(getClass("NSAppearance"), selector("setCurrentAppearance:"), appearance);
	}

	static Pointer currentAppearance() {
		return send("NSAppearance", "currentAppearance");
	}
}
