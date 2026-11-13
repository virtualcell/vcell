package cbit.vcell.client.laf;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * macOS's semantic colors (NSColor.windowBackgroundColor, labelColor, ...), read from AppKit as sRGB for a given
 * appearance. They follow the user's own settings, such as the accent color.
 */
final class MacOSSemanticColors {
	private static final Logger lg = LogManager.getLogger(MacOSSemanticColors.class);

	private MacOSSemanticColors() {}

	/**
	 * The named colors (NSColor class methods, e.g. "labelColor"), resolved for the dark or light appearance.
	 * Leaves the calling thread's appearance as it was. Returns an empty map if AppKit can't be reached, and
	 * omits any color that can't be expressed in sRGB.
	 */
	static Map<String, Color> read(boolean dark, String... names) {
		Map<String, Color> colors = new LinkedHashMap<>();
		try {
			Pointer previous = ObjectiveC.currentAppearance();
			ObjectiveC.setCurrentAppearance(ObjectiveC.appearance(dark));
			try {
				Pointer srgb = ObjectiveC.send("NSColorSpace", "sRGBColorSpace");
				for (String name : names) {
					Color color = toColor(ObjectiveC.send("NSColor", name), srgb);
					if (null != color) colors.put(name, color);
				}
			} finally {
				ObjectiveC.setCurrentAppearance(previous);
			}
		} catch (Throwable e) {
			lg.warn("Could not read macOS colors", e);
		}
		return colors;
	}

	private static Color toColor(Pointer nsColor, Pointer srgbColorSpace) {
		if (null == nsColor) return null;
		ObjectiveC.Runtime runtime = ObjectiveC.runtime();
		Pointer srgb = runtime.objc_msgSend(nsColor, ObjectiveC.selector("colorUsingColorSpace:"), srgbColorSpace);
		if (null == srgb) return null;
		Memory red = new Memory(8), green = new Memory(8), blue = new Memory(8), alpha = new Memory(8); // CGFloat
		runtime.objc_msgSend(srgb, ObjectiveC.selector("getRed:green:blue:alpha:"), red, green, blue, alpha);
		return new Color(clamp(red.getDouble(0)), clamp(green.getDouble(0)), clamp(blue.getDouble(0)), clamp(alpha.getDouble(0)));
	}

	private static float clamp(double component) {
		return (float) Math.max(0, Math.min(1, component));
	}
}
