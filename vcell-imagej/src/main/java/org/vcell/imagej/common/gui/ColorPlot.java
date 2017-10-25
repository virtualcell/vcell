package org.vcell.imagej.common.gui;

import java.awt.Color;

import ij.gui.Plot;

public class ColorPlot extends Plot {
	
	private final Color[] palette = new Color[] {
			Color.RED,
			Color.BLUE,
			Color.GREEN
	};
	
	private int paletteCounter = 0;

	public ColorPlot(String title, String xLabel, String yLabel) {
		super(title, xLabel, yLabel);
	}
	
	@Override
	public void addPoints(double[] x, double[] y, int shape) {
		setColor(palette[paletteCounter % palette.length]);
		super.addPoints(x, y, shape);
		paletteCounter++;
	}
	
	@Override
	public void addLegend(String labels) {
		setColor(Color.BLACK);
		super.addLegend(labels);
	}
}
