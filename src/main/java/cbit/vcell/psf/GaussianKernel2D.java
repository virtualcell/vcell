/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.psf;

import org.vcell.util.Range;

public class GaussianKernel2D {

	private int width = 0;
	private Range range = null;
	private double sigma;
	private double[] data;
	
	public GaussianKernel2D(double argSigma) {

		if(argSigma < 0.5) {
			sigma = 0.5;
		} else {
			sigma = argSigma;
		}
		int sixSigma = (int)(6 * sigma);
		if( sixSigma % 2 == 0) {
			width = sixSigma + 1;
		} else {
			width = sixSigma;
		}
		data = new double[width*width];
		
		double max = 0;
		double min = Double.MAX_VALUE;
		for(int x=-width/2, i=0; i<width; x++, i++) {
			for(int y=-width/2, j=0; j<width; y++, j++) {
				double twoSigmaCube = 2*sigma*sigma;
				double exponent = (x*x + y*y) / twoSigmaCube;
				double val = (Math.exp(-exponent)) / (Math.PI*twoSigmaCube);
				if (val > max) max = val;
				if (val < min) min = val;
				data[i*width+j] = val;
//				System.out.print((int)(10000*data[i*width+j]) + " ");
			}
			System.out.println("");
		}
		range = new Range(min, max);
//		System.out.println("width=" + width + ", sigma=" + sigma + ", 6sigma=" + sixSigma );
		System.out.println("min: " + min + " max: " + max);
	}
	
	static final double[] getNormalizedData(GaussianKernel2D kernel) {
		int width = kernel.getWidth();
		double min = kernel.getRange().getMin();
		double max = kernel.getRange().getMax();
		double[] rawData = kernel.getRawData();
		double[] normalized = new double[width * width];
		for (int i = 0; i < rawData.length; i++) {
			normalized[i] = (rawData[i]-min)/(max - min);
		}
		return normalized;
	}
	public static final int[] getMaxContrast(GaussianKernel2D kernel) {
		double[] normalized = GaussianKernel2D.getNormalizedData(kernel);
		int width = kernel.getWidth();
		int[] contrasted = new int[width * width];
		for(int i=0; i<normalized.length; i++) {
			contrasted[i] = (int)(normalized[i]*127);
			if(contrasted[i] < 0) contrasted[i] = 0;
			if(contrasted[i] > 127) contrasted[i] = 127;
			System.out.println("    " + contrasted[i]);
		}
		return contrasted;
	}
		
	public final int getWidth() {
		return width;
	}
	public final double getSigma() {
		return sigma;
	}
	public final double[] getRawData() {
		return data;
	}
	public final Range getRange() {
		return range;
	}
}
