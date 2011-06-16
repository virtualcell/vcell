/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.surface;

/**
 * Insert the type's description here.
 * Creation date: (12/3/2003 2:49:20 PM)
 * @author: Jim Schaff
 */
public class TaubinSmoothingSpecification {
	private final double fieldLambda;
	private final double fieldMu;
	private final int fieldIterations;
	private final FilterSpecification fieldFilterSpecification;

	private static TaubinSmoothingSpecification precomputedTaubinSpecs[] = {
		new TaubinSmoothingSpecification(0.987,-0.996838798945593,98,new FilterSpecification(0.01,0.18000000000000005,0.05,0.05)),
		new TaubinSmoothingSpecification(0.976,-0.9954308093994778,94,new FilterSpecification(0.02,0.19000000000000006,0.05,0.05)),
		new TaubinSmoothingSpecification(0.967,-0.9958907918722129,90,new FilterSpecification(0.03,0.20000000000000007,0.05,0.05)),
		new TaubinSmoothingSpecification(0.961,-0.9994176130454677,96,new FilterSpecification(0.04,0.20000000000000007,0.05,0.05)),
		new TaubinSmoothingSpecification(0.95,-0.9973753280839894,75,new FilterSpecification(0.05,0.2300000000000001,0.05,0.05)),
		new TaubinSmoothingSpecification(0.94,-0.9961848240779991,55,new FilterSpecification(0.06,0.2700000000000001,0.05,0.05)),
		new TaubinSmoothingSpecification(0.931,-0.9959029984061273,42,new FilterSpecification(0.07,0.3100000000000001,0.05,0.05)),
		new TaubinSmoothingSpecification(0.923,-0.9965880625377905,33,new FilterSpecification(0.08,0.35000000000000014,0.05,0.05)),
		new TaubinSmoothingSpecification(0.915,-0.9971121887429849,25,new FilterSpecification(0.09,0.4000000000000002,0.05,0.05)),
		new TaubinSmoothingSpecification(0.9,-0.989010989010989,21,new FilterSpecification(0.1,0.4400000000000002,0.05,0.05)),
		new TaubinSmoothingSpecification(0.883,-0.9779924020069335,18,new FilterSpecification(0.11,0.48000000000000026,0.05,0.05)),
		new TaubinSmoothingSpecification(0.883,-0.9876515592143529,15,new FilterSpecification(0.12,0.5200000000000002,0.05,0.05)),
		new TaubinSmoothingSpecification(0.871,-0.982216358243964,13,new FilterSpecification(0.13,0.5600000000000003,0.05,0.05)),
		new TaubinSmoothingSpecification(0.871,-0.9919595471835638,11,new FilterSpecification(0.14,0.6000000000000003,0.05,0.05)),
		new TaubinSmoothingSpecification(0.864,-0.9926470588235293,10,new FilterSpecification(0.15,0.6300000000000003,0.05,0.05)),
		new TaubinSmoothingSpecification(0.847,-0.9797797519896353,9,new FilterSpecification(0.16,0.6700000000000004,0.05,0.05)),
		new TaubinSmoothingSpecification(0.849,-0.9922049388198722,8,new FilterSpecification(0.17,0.7000000000000004,0.05,0.05)),
		new TaubinSmoothingSpecification(0.845,-0.9965797853520462,7,new FilterSpecification(0.18,0.7400000000000004,0.05,0.05)),
		new TaubinSmoothingSpecification(0.801,-0.9447871574999116,7,new FilterSpecification(0.19,0.7800000000000005,0.05,0.05)),
		new TaubinSmoothingSpecification(0.816,-0.9751434034416826,6,new FilterSpecification(0.2,0.8100000000000005,0.05,0.05)),
		new TaubinSmoothingSpecification(0.778,-0.9299323468241256,6,new FilterSpecification(0.21,0.8500000000000004,0.05,0.05)),
		new TaubinSmoothingSpecification(0.81,-0.9856412752494526,5,new FilterSpecification(0.22,0.8700000000000004,0.05,0.05)),
		new TaubinSmoothingSpecification(0.774,-0.9416287500912432,5,new FilterSpecification(0.23,0.9100000000000005,0.05,0.05)),
		new TaubinSmoothingSpecification(0.805,-0.9977689638076352,4,new FilterSpecification(0.24,0.9400000000000005,0.05,0.05)),
		new TaubinSmoothingSpecification(0.789,-0.9828713796325134,4,new FilterSpecification(0.25,0.9600000000000005,0.05,0.05)),
		new TaubinSmoothingSpecification(0.757,-0.9425035483951294,4,new FilterSpecification(0.26,1.0000000000000004,0.05,0.05)),
		new TaubinSmoothingSpecification(0.728,-0.9061037538584089,4,new FilterSpecification(0.27,1.0400000000000005,0.05,0.05)),
		new TaubinSmoothingSpecification(0.774,-0.9881523848432234,3,new FilterSpecification(0.28,1.0600000000000005,0.05,0.05)),
		new TaubinSmoothingSpecification(0.775,-0.9996775233795551,3,new FilterSpecification(0.29,1.0600000000000005,0.05,0.05)),
		new TaubinSmoothingSpecification(0.747,-0.9627529320788761,3,new FilterSpecification(0.3,1.1000000000000005,0.05,0.05)),
		new TaubinSmoothingSpecification(0.727,-0.9385125801995791,3,new FilterSpecification(0.31,1.1300000000000006,0.05,0.05)),
		new TaubinSmoothingSpecification(0.702,-0.9053858852661988,3,new FilterSpecification(0.32,1.1700000000000006,0.05,0.05)),
		new TaubinSmoothingSpecification(0.749,-0.9949125300532656,2,new FilterSpecification(0.33,1.2000000000000006,0.05,0.05)),
		new TaubinSmoothingSpecification(0.743,-0.9941395274157725,2,new FilterSpecification(0.34,1.2100000000000006,0.05,0.05)),
		new TaubinSmoothingSpecification(0.737,-0.9931945286705746,2,new FilterSpecification(0.35,1.2200000000000006,0.05,0.05)),
		new TaubinSmoothingSpecification(0.732,-0.9939170106452313,2,new FilterSpecification(0.36,1.2300000000000006,0.05,0.05)),
		new TaubinSmoothingSpecification(0.726,-0.9926440427684651,2,new FilterSpecification(0.37,1.2400000000000007,0.05,0.05)),
		new TaubinSmoothingSpecification(0.703,-0.9592555194716589,2,new FilterSpecification(0.38,1.2800000000000007,0.05,0.05)),
		new TaubinSmoothingSpecification(0.687,-0.9384348491264497,2,new FilterSpecification(0.39,1.3100000000000007,0.05,0.05)),
		new TaubinSmoothingSpecification(0.667,-0.9097108565193672,2,new FilterSpecification(0.4,1.3500000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.652,-0.8898837145820823,2,new FilterSpecification(0.41,1.3800000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.7,-0.9915014164305949,1,new FilterSpecification(0.42,1.4000000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.695,-0.9912286957141837,1,new FilterSpecification(0.43,1.4100000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.69,-0.9908098793796668,1,new FilterSpecification(0.44,1.4200000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.685,-0.990242139501265,1,new FilterSpecification(0.45,1.4300000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.681,-0.9916416693362847,1,new FilterSpecification(0.46,1.4400000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.676,-0.9907955678020754,1,new FilterSpecification(0.47,1.4500000000000006,0.05,0.05)),
		new TaubinSmoothingSpecification(0.671,-0.9897923058767998,1,new FilterSpecification(0.48,1.4600000000000006,0.05,0.05)),
		new TaubinSmoothingSpecification(0.667,-0.9908344103272576,1,new FilterSpecification(0.49,1.4700000000000006,0.05,0.05)),
		new TaubinSmoothingSpecification(0.662,-0.9895366218236173,1,new FilterSpecification(0.5,1.4800000000000006,0.05,0.05)),
		new TaubinSmoothingSpecification(0.658,-0.9903374371632402,1,new FilterSpecification(0.51,1.4900000000000007,0.05,0.05)),
		new TaubinSmoothingSpecification(0.654,-0.9910292156625047,1,new FilterSpecification(0.52,1.5000000000000007,0.05,0.05)),
		new TaubinSmoothingSpecification(0.649,-0.9892840266451232,1,new FilterSpecification(0.53,1.5100000000000007,0.05,0.05)),
		new TaubinSmoothingSpecification(0.645,-0.9897191959490564,1,new FilterSpecification(0.54,1.5200000000000007,0.05,0.05)),
		new TaubinSmoothingSpecification(0.645,-0.9996125532739251,1,new FilterSpecification(0.55,1.5200000000000007,0.05,0.05)),
		new TaubinSmoothingSpecification(0.637,-0.9902375326451934,1,new FilterSpecification(0.56,1.5400000000000007,0.05,0.05)),
		new TaubinSmoothingSpecification(0.625,-0.9708737864077669,1,new FilterSpecification(0.57,1.5700000000000007,0.05,0.05)),
		new TaubinSmoothingSpecification(0.617,-0.9608496589528762,1,new FilterSpecification(0.58,1.5900000000000007,0.05,0.05)),
		new TaubinSmoothingSpecification(0.606,-0.9432493851757308,1,new FilterSpecification(0.59,1.6200000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.595,-0.9253499222395023,1,new FilterSpecification(0.6,1.6500000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.587,-0.9144299222656674,1,new FilterSpecification(0.61,1.6700000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.577,-0.8983900601002708,1,new FilterSpecification(0.62,1.7000000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.567,-0.8820921296224272,1,new FilterSpecification(0.63,1.7300000000000009,0.05,0.05)),
		new TaubinSmoothingSpecification(0.557,-0.865551964196917,1,new FilterSpecification(0.64,1.760000000000001,0.05,0.05)),
		new TaubinSmoothingSpecification(0.551,-0.8584560255511413,1,new FilterSpecification(0.65,1.780000000000001,0.05,0.05)),
		new TaubinSmoothingSpecification(0.542,-0.843868717693218,1,new FilterSpecification(0.66,1.810000000000001,0.05,0.05)),
		new TaubinSmoothingSpecification(0.533,-0.8290687364868019,1,new FilterSpecification(0.67,1.840000000000001,0.05,0.05)),
		new TaubinSmoothingSpecification(0.525,-0.8164852255054432,1,new FilterSpecification(0.68,1.870000000000001,0.05,0.05)),
		new TaubinSmoothingSpecification(0.519,-0.808549751515057,1,new FilterSpecification(0.69,1.8900000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.511,-0.7955783901603611,1,new FilterSpecification(0.7,1.9200000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.503,-0.782428795868527,1,new FilterSpecification(0.71,1.9500000000000008,0.05,0.05)),
		new TaubinSmoothingSpecification(0.496,-0.7715281234444997,1,new FilterSpecification(0.72,1.9800000000000009,0.05,0.05)),
	};
	public TaubinSmoothingSpecification(double lambda, double mu, int iterations, FilterSpecification argFilterSpecification) {
		fieldLambda = lambda;
		fieldMu = mu;
		fieldIterations = iterations;
		fieldFilterSpecification = argFilterSpecification;
	}
/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 5:38:57 PM)
 * @return java.lang.String
 */
public static void createLookupTableCode() {
	System.out.println("\t\tprivate static TaubinSmoothingSpecification precomputedTaubinSpecs[] = {"); 
	for (int i = 1; i < 200; i++){
		try {
			cbit.vcell.geometry.surface.TaubinSmoothingSpecification spec = cbit.vcell.geometry.surface.TaubinSmoothingSpecification.fromFilterSpecification(i/100.0);
			System.out.println("\t\t\t\tnew TaubinSmoothingSpecification("+
													spec.getLambda()+","+
													spec.getMu()+","+
													spec.getIterations()+","+
													"new FilterSpecification("+
															spec.getFilterSpecification().getKpb()+","+
															spec.getFilterSpecification().getKsb()+","+
															spec.getFilterSpecification().getDpb()+","+
															spec.getFilterSpecification().getDsb()+
													")"+
											"),");
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	System.out.println("\t\t};"); 
}
//
// from Taubin (1995) "Curve and Surface Smoothing without Shrinkage"
//
//  Filter design:
//
//      passband frequency Kpb
//      stopband frequency Ksb
//      passband ripple    Dpb
//      stopband ripple    Dpb
//
//   1)   0 < Kpb < Ksb < 2,
//   2)   0 < Dpb,
//   3)   0 < Dsb < 1
//
//  Finding Taubin filter coeffients
//
//      lambda (forward constant)
//      mu     (reverse constant)
//      N      (num iterations)
//
//   4)   0 < N,   
//   5)   0 < lambda < -mu,
//   6)   lambda < 1 / Ksb,
//   7)   1/lambda + 1/mu = Kpb
//   8)   [((lamdba - mu)^2)/(-4*lambda*mu)]^N  <  1 + Dpb
//   9)   [(1 - lambda*Ksb)*(1 - mu*Ksb)]^N  <  Dsb
//
//
//  from (7):
//
//  10)   lambda = 1/(Kpb - 1/mu)
//
//


private static TaubinSmoothingSpecification fromFilterSpecification(double cutoffFrequency) throws SurfaceGeneratorException {
	if (cutoffFrequency<=0.0 || cutoffFrequency>2.0) {
		throw new SurfaceGeneratorException("cutoffFrequency = '"+cutoffFrequency+"', should be 0.0 < cutoffFrequency < 0.5");
	}

	//
	// initialize to frequency at ratio=0.25 and then squeeze down as necessary
	//
	final double STANDARD_PASS_BAND_FREQUENCY	= 0.3;
	final double STANDARD_STOP_BAND_FREQUENCY	= 1.1;
	final double STANDARD_PASS_BAND_RIPPLE		= 0.05;
	final double STANDARD_STOP_BAND_RIPPLE		= 0.05;
	
	//
	// try to find best filter possible (try
	//
	double passBandFrequency = cutoffFrequency;
	double passBandRipple = STANDARD_PASS_BAND_RIPPLE;
	double stopBandRipple = STANDARD_STOP_BAND_RIPPLE;
	double stopBandFrequency = cutoffFrequency+0.1;
	while (stopBandFrequency<2.0){
		try {
			FilterSpecification filterSpec = new FilterSpecification(passBandFrequency,stopBandFrequency,passBandRipple,stopBandRipple);
			TaubinSmoothingSpecification spec = fromFilterSpecification(filterSpec);
			return spec;
		}catch (IllegalArgumentException e){
		}catch (SurfaceGeneratorException e){
		}
		stopBandFrequency+=0.01;
	}
	throw new SurfaceGeneratorException("unable to find filter coefficients for cutoffFrequency="+cutoffFrequency);
}
public static TaubinSmoothingSpecification fromFilterSpecification(FilterSpecification filterSpec) throws SurfaceGeneratorException {
	//
	// Make sure 0 < n...
	for (int n = 1; n < 100; n++) {
		int I = 1000; // for (int I = 100; I <= 100000; I *= 10) {
		for (int i = 1; i < I; i++){
			double lambda = ((double) i)/((double) I);
			// Make sure lambda < 1/stopBandFrequency
			if (lambda < 1.0/filterSpec.getKsb()) {
				// Make sure 1/lambda + 1/mu = passBandFrequency...
				double mu = -lambda/(1.0 - lambda*filterSpec.getKpb());
				// Make sure 0 < lambda < -mu < 1...
				if (0.0 < lambda && lambda < -mu && -mu < 1.0) {
					// Make sure ((lambda - mu)^2/(-4*lambda*mu))^n < 1 + passBandRipple...
					// Make sure ((1 - lambda*stopBandFrequency)(1 - mu*stopBandFrequency))^n < stopBandRipple...
					double A = (lambda - mu)*(lambda - mu)/(-4.0*lambda*mu);
					double B = (1 - lambda*filterSpec.getKsb())*(1 - mu*filterSpec.getKsb());
					if (Math.pow(A, n) < 1.0 + filterSpec.getDpb() && Math.pow(B, n) < filterSpec.getDsb()) {
						return new TaubinSmoothingSpecification(lambda, mu, n, filterSpec);
					}
				}
			}
		}
	}
	throw new SurfaceGeneratorException("unable to find algorithm parameters");
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 11:42:57 AM)
 * @return cbit.vcell.geometry.surface.FilterSpecification
 */
public final FilterSpecification getFilterSpecification() {
	return fieldFilterSpecification;
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 1:45:55 PM)
 * @return cbit.vcell.geometry.surface.TaubinSmoothingSpecification
 * @param cutoffFrequency double
 */
public static TaubinSmoothingSpecification getInstance(double cutoffFrequency) {

	if (cutoffFrequency <= 0 || cutoffFrequency >= 2.0){
		throw new IllegalArgumentException("cutoff frequency="+cutoffFrequency+" doesn't satisfy:  (0 < cutoff frequency < 2)");
	}
	TaubinSmoothingSpecification bestSpec = precomputedTaubinSpecs[0];
	double bestError = Math.abs(cutoffFrequency - bestSpec.getFilterSpecification().getKpb());
	
	for (int i = 1; i < precomputedTaubinSpecs.length; i++){
		double currError = Math.abs(cutoffFrequency - precomputedTaubinSpecs[i].getFilterSpecification().getKpb());
		if (currError < bestError){
			bestSpec = precomputedTaubinSpecs[i];
			bestError = currError;
		}
	}
	return bestSpec;
}
/**
 * Insert the method's description here.
 * Creation date: (12/3/2003 3:08:37 PM)
 * @return int
 */
public int getIterations() {
	return fieldIterations;
}
/**
 * Insert the method's description here.
 * Creation date: (12/3/2003 3:08:37 PM)
 * @return double
 */
public double getLambda() {
	return fieldLambda;
}
/**
 * Insert the method's description here.
 * Creation date: (12/3/2003 3:08:37 PM)
 * @return double
 */
public double getMu() {
	return fieldMu;
}
}
