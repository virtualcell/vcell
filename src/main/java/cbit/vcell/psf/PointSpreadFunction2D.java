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

import static cbit.vcell.psf.BesselFunctionsIntegerOrder.J1;

public class PointSpreadFunction2D {

	private double sampleDensity;		// # of pixels per unit distance (pixels/inch)
										// ex: 256x256 pixels per 100x100 micron gives 2.56 pixels/micron
	private double laserWavelength;		// expressed in nanometers (1 inch = 25.4 millimeter)
	private double numericalAperture;
	
	private final int numPixels = 17;	// diameter of PSF in pixels (must be an odd number)
	
	private double width;	// pixel size (expressed in micrometers)  ex: 100/256 = 391 nm
	private double data[];
	
	public PointSpreadFunction2D(double argSampleDensity, double argLaserWavelength, double argNumericalAperture) {
		sampleDensity = argSampleDensity;
		laserWavelength = argLaserWavelength;
		numericalAperture = argNumericalAperture;
		
		// we convert everything to micrometers
		sampleDensity = sampleDensity / 25400;
		laserWavelength = laserWavelength / 1000;
		width = 1/sampleDensity;
		data = new double[numPixels*numPixels];
		
		for(int i=-numPixels/2, index=0; i<numPixels/2+1; i++) {
			for(int j=-numPixels/2; j<numPixels/2+1; j++, index++) {
				double x = width*(double)j;
				double y = width*(double)i;
				double radius = Math.sqrt(x*x + y*y);
				double bessInput = 2*Math.PI*numericalAperture*radius/laserWavelength;
				double bessResult = 2*J1(bessInput);
//				System.out.println("rad : "+radius+" , bessFunc(" + bessInput + ") = " + bessResult);
				if(radius == 0) {		// we solve a 0/0 situation at origin ( radius == 0 )
					data[index] = 1;
				} else {
					data[index] = Math.pow(bessResult/bessInput, 2);
				}
				
				int testInt = (int)(300000*data[index]);
				System.out.print(testInt + "  ");
//				System.out.print((int)radius + "  ");
			}
			System.out.println("");
		}
	}

	public final double[] getRawData() {
		return data;
	}
	public final double getWidth() {
		return width;
	}
	public final double getSampleDensity() {
		return sampleDensity;
	}
	public final double getLaserWavelength() {
		return laserWavelength;
	}
	public final double getNumericalAperture() {
		return numericalAperture;
	}
}
