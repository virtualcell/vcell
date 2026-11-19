package org.vcell.client.logicalwindow;

import cbit.vcell.client.laf.VCellFonts;

import java.awt.Window;

/**
 * How big a logical window opens, in ems: multiples of the size of ordinary text. A window sized this way grows and
 * shrinks with the text size, stays within the usable part of its screen, and keeps its size in ems as the text size
 * changes until its user resizes it. Prefer it to sizes in pixels, which can only be scaled by guessing what they meant.
 * <p>
 * As a rough guide, a pixel size laid out for 12pt text is that many twelfths of an em: 600 pixels is 50 ems.
 */
public final class LWSize {
	private final boolean fit;
	private final float width, height; // ems; for a fitted window, its limit (0: none)

	private LWSize(boolean fit, float width, float height) {
		this.fit = fit;
		this.width = width;
		this.height = height;
	}

	/** This many ems wide and high. */
	public static LWSize ems(float width, float height) {
		if (width <= 0 || height <= 0) throw new IllegalArgumentException("A window size must be positive: " + width + " x " + height + " ems");
		return new LWSize(false, width, height);
	}

	/** Just big enough for its contents (packed). */
	public static LWSize fit() {
		return new LWSize(true, 0, 0);
	}

	/** Just big enough for its contents (packed), but no more than this many ems wide and high (0: no limit that way). */
	public static LWSize fit(float maxWidth, float maxHeight) {
		if (maxWidth < 0 || maxHeight < 0) throw new IllegalArgumentException("A window's limit can't be negative: " + maxWidth + " x " + maxHeight + " ems");
		return new LWSize(true, maxWidth, maxHeight);
	}

	/** Sizes the window. Call once its contents are in place, before showing it, on the Swing event thread. */
	public void applyTo(Window window) {
		if (this.fit) VCellFonts.fitWindow(window, this.width, this.height);
		else VCellFonts.sizeWindow(window, this.width, this.height);
	}

	@Override
	public String toString() {
		return this.fit ? "fit within " + this.width + " x " + this.height + " ems" : this.width + " x " + this.height + " ems";
	}
}
