package cbit.vcell.export.server;

import cbit.image.DisplayAdapterService;
import cbit.image.DisplayPreferences;

/**
 * Chooses the colormap an export request is allowed to name.
 * An old server has no Cividis registration and no way to advertise it, so a request
 * that still says Cividis would be drawn with another palette or rejected.
 */
public final class ExportColorModeNegotiation {

	private ExportColorModeNegotiation() {
	}

	public static String[] legacyModes() {
		return new String[] { DisplayAdapterService.GRAY, DisplayAdapterService.BLUERED };
	}

	public static String[] currentModes() {
		return new String[] {
				DisplayAdapterService.GRAY,
				DisplayAdapterService.BLUERED,
				DisplayAdapterService.CIVIDIS
		};
	}

	/**
	 * The mode that will be written. Never returns an id that {@code supported} does not list,
	 * except when that list is empty, in which case BlueRed is the fallback.
	 */
	public static String modeThatWillBeWritten(String requested, String[] supported) {
		if (contains(supported, requested)) {
			return requested;
		}
		if (contains(supported, DisplayAdapterService.BLUERED)) {
			return DisplayAdapterService.BLUERED;
		}
		if (contains(supported, DisplayAdapterService.GRAY)) {
			return DisplayAdapterService.GRAY;
		}
		if (supported != null) {
			for (String mode : supported) {
				if (mode != null && !mode.isEmpty()) {
					return mode;
				}
			}
		}
		return DisplayAdapterService.BLUERED;
	}

	/**
	 * Preferences whose color mode is one the server can draw. A supported request is returned
	 * unchanged, including any custom special colors. A substitution uses that mode's own special colors.
	 */
	public static DisplayPreferences preferencesForServer(DisplayPreferences requested, String[] supported) {
		if (requested == null) {
			return null;
		}
		String written = modeThatWillBeWritten(requested.getColorMode(), supported);
		if (written.equals(requested.getColorMode())) {
			return requested;
		}
		return new DisplayPreferences(
				written,
				requested.getScaleSettings(),
				specialsFor(written),
				requested.isAuto(),
				requested.isAlltimes());
	}

	/**
	 * Sentence shown when the file will not use the colormap the user had selected.
	 * Returns null when the request already names a supported mode.
	 */
	public static String notice(String requested, String written) {
		if (requested == null || requested.equals(written)) {
			return null;
		}
		return "This server will write " + written + ". " + requested + " is not available on the export server.";
	}

	public static boolean isUnsupportedColorModeQuery(Throwable error) {
		Throwable current = error;
		while (current != null) {
			String message = current.getMessage();
			if (message != null && message.contains("No such method") && message.contains("getSupportedExportColorModes")) {
				return true;
			}
			current = current.getCause();
		}
		return false;
	}

	private static int[] specialsFor(String mode) {
		if (DisplayAdapterService.CIVIDIS.equals(mode)) {
			return DisplayAdapterService.createCividisSpecialColors();
		}
		if (DisplayAdapterService.GRAY.equals(mode)) {
			return DisplayAdapterService.createGraySpecialColors();
		}
		return DisplayAdapterService.createBlueRedSpecialColors();
	}

	private static boolean contains(String[] supported, String mode) {
		if (mode == null || supported == null) {
			return false;
		}
		for (String candidate : supported) {
			if (mode.equals(candidate)) {
				return true;
			}
		}
		return false;
	}
}
