package cbit.vcell.geometry.gui;

import java.util.List;

/**
 * Selection cues for the image-geometry ROI editor. The region pixels stay in the ROI image.
 * The name and the black/white edge are drawn on top of that image.
 */
public final class RoiSelectionCue {

	private RoiSelectionCue() {}

	/**
	 * A highlighted pixel whose 4-neighbor is outside the highlight, including the image border.
	 * Reads {@code mask} and does not write it.
	 */
	public static boolean isBoundary(byte[] mask, int width, int height, int x, int y) {
		if (mask == null || x < 0 || y < 0 || x >= width || y >= height) {
			return false;
		}
		int index = y * width + x;
		if (mask[index] == 0) {
			return false;
		}
		if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
			return true;
		}
		return mask[index - 1] == 0 || mask[index + 1] == 0
				|| mask[index - width] == 0 || mask[index + width] == 0;
	}

	/** Alternating black and white, so the edge is a pattern rather than the region hue. */
	public static int boundaryRgb(int x, int y) {
		return ((x + y) & 1) == 0 ? 0x000000 : 0xFFFFFF;
	}

	public static String selectedRegionSentence(List<String> names) {
		if (names == null || names.isEmpty()) {
			return "Selected: none";
		}
		StringBuilder sentence = new StringBuilder("Selected: ");
		for (int i = 0; i < names.size(); i++) {
			if (i > 0) {
				sentence.append(", ");
			}
			sentence.append(names.get(i));
		}
		return sentence.toString();
	}

	public static String withListTitle(String title, List<String> names) {
		return title + ". " + selectedRegionSentence(names);
	}
}
