package cbit.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/*
 * @(#)ImageButton.java
 *
 * Copyright (c) 1997 NAKAGAWA Masami
 *
 * Permission to use, copy, modify, and distribute this software
 * for NON-COMMERCIAL purpose and without fee is hereby granted.
 */

import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.net.*;

class HSBImageFilter extends RGBImageFilter {
	private float[] hsb = new float[3];
	private float hue;
	private float saturation;
	private float brightness;

	public HSBImageFilter(float hue, float saturation, float brightness) {
		canFilterIndexColorModel = true;
		this.hue = hue;
		this.saturation = saturation;
		this.brightness = brightness;
	}
	public int filterRGB(int x, int y, int rgb) {
		Color.RGBtoHSB((rgb & 0xFF0000)>>16, (rgb & 0xFF00)>>8, (rgb & 0xFF), hsb);
		float sat = hsb[1] * saturation;
		if (sat > 1.0f) sat = 1.0f;
		float bri = hsb[2] * brightness;
		if (bri > 1.0f) bri = 1.0f;
		return Color.HSBtoRGB((hue >= 0)?hue:hsb[0], sat, bri);
	}
}
