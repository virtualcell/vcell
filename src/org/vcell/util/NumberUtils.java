/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;
import java.math.BigDecimal;
/**
 * Insert the type's description here.
 * Creation date: (6/1/00 12:48:50 PM)
 * @author: 
 */
public class NumberUtils {

/**
 * Insert the method's description here.
 * Creation date: (10/11/00 5:03:23 PM)
 * @return java.lang.String
 * @param number double
 */
public static final String formatNumber(double number) {
	return formatNumber(number, 8);
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 5:03:23 PM)
 * @return java.lang.String
 * @param number double
 */
public static final String formatNumber(double number, int numDigits) {
	try { 
		if (numDigits < 1) {
			throw new RuntimeException("NumberUtils.formatNumber() -- Argument 'numDigits' (desired number of digits) must be greater than 0");
		}
		if(number == 0){
			return "0";
		}

		java.text.NumberFormat scinf = new java.text.DecimalFormat("0.000000000000000000000E0", new java.text.DecimalFormatSymbols(java.util.Locale.US));
		scinf.setMaximumFractionDigits(numDigits - 1);
		scinf.setMinimumIntegerDigits(1);
		scinf.setMaximumIntegerDigits(1);
		String scistr = scinf.format(number);	

		java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.US);
		nf.setGroupingUsed(false);
		String str = null;
		for (int i = 1; i <= numDigits; i ++) {
			nf.setMaximumFractionDigits(i);
			nf.setMaximumIntegerDigits(numDigits - i);
			nf.setMinimumIntegerDigits(1);
			str = nf.format(number);
			if (Double.parseDouble(str) == Double.parseDouble(scistr)) {
				return str;
			}
		}

		return scistr;
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}

	return number + "";
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 5:03:23 PM)
 * @return java.lang.String
 * @param number double
 */
public static final String formatNumBytes(long numBytes) {
	if (numBytes < 1000){ 
		// < 1KB
		return Long.toString(numBytes)+" bytes";
	}else if (numBytes < 1000000){
		// < 1MB
		return numBytes/1000 + " KB";
	}else if (numBytes < 1000000000){
		// < 1GB
		return (numBytes/1000)/1000.0 + " MB";
	}else{
		// >= 1GB
		return (numBytes/1000000)/1000.0 + " GB";
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 7:10:37 PM)
 * @return cbit.image.Range
 * @param d1 double
 * @param d2 double
 * @param includeZero boolean
 * @param wide boolean
 */
public static Range getDecimalRange(double d1, double d2, boolean includeZero, boolean wide) {
	if (d1 == 0 && d2 == 0) {
		return new Range(-1, 1);
	}
	double low = Math.min(d1, d2);
	double high = Math.max(d1, d2);
	if (includeZero) {
		low = (low > 0) ? 0 : low;
		high = (high < 0) ? 0 : high;
	}
	if (wide) {
		if (high > 0 && low >= 0) {
			return getPositiveWideRange(low, high, includeZero);
		} else if (high <= 0) {
			Range range = getPositiveWideRange(-high, -low, includeZero);
			return new Range(-range.getMax(), -range.getMin());
		} else {
			return getMixedWideRange(low, high);
		}
	} else {
		return getNarrowRange(low, high);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 7:11:33 PM)
 * @return cbit.image.Range
 * @param range cbit.image.Range
 * @param includeZero boolean
 * @param wide boolean
 */
public static Range getDecimalRange(Range range, boolean includeZero, boolean wide) {
	return getDecimalRange(range.getMin(), range.getMax(), includeZero, wide);
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 8:56:08 PM)
 * @return double[]
 * @param range cbit.image.Range
 */
public static double[] getMajorDecimalTicks(Range range) {
	if (range.getMax() == 0 && range.getMin() == 0) {
		return new double[] {-1.0, 0.0, 1.0};
	} else {
		int power = (int)Math.floor(Math.log(range.getMax() - range.getMin()) / Math.log(10));
		BigDecimal spacing = new BigDecimal("1");
		if (power < 0) spacing = spacing.setScale(-power);
		while (power > 0) {
			spacing = spacing.multiply(new BigDecimal("10"));
			power--;
		}
		while (power < 0) {
			spacing = spacing.divide(new BigDecimal("10"), BigDecimal.ROUND_UNNECESSARY);
			power++;
		}
		spacing.movePointLeft(power);
		int i = 0;
		BigDecimal first = spacing.multiply(new BigDecimal(Double.toString(Math.floor(range.getMin() / spacing.doubleValue()))));
		double d = first.doubleValue();
		do {
			i++;
		} while ((d += spacing.doubleValue()) < range.getMax());
		double[] ticks = new double[i+1];
		for (int j=0;j<ticks.length;j++) {
			ticks[j] = first.add(spacing.multiply(new BigDecimal((double)j))).doubleValue();
		}
		return ticks;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 8:56:08 PM)
 * @return double[]
 * @param range cbit.image.Range
 */
public static double[] getMinorDecimalTicks(Range range) {
	if (range.getMax() == 0 && range.getMin() == 0) {
		return new double[] {-0.5, 0.5};
	} else {
		double[] majors = getMajorDecimalTicks(range);
		if (majors.length > 1) {
			double spacing = majors[1] - majors[0];
			int n = majors.length - 1;
			int s = 1;
			if ((majors[0] - spacing / 2) > range.getMin()) {
				n++;
			}
			if ((majors[majors.length - 1] + spacing / 2) < range.getMax()) {
				n++;
			}
			double[] minors = new double[n];
			try {
				for (int i=0;i<n;i++) {
					minors[i] = majors[i + s] - spacing / 2;
				}
			} catch (IndexOutOfBoundsException exc) {
				minors[n - 1] = minors[n - 2] + spacing;
			}
			return minors;
		} else {
			return majors;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 7:10:37 PM)
 * @return cbit.image.Range
 * @param d1 double
 * @param d2 double
 */
private static Range getMixedWideRange(double low, double high) {
	
	/* asumes mixed-sign, non-zero, and properly ranked numbers */
	
	double ceil = Math.pow(10, Math.ceil(Math.log(high) / Math.log(10)));
	double lowCeil = Math.pow(10, Math.ceil(Math.log(-low) / Math.log(10)));
	if (ceil > lowCeil) {
		if (ceil / 2 > lowCeil) {
			lowCeil = ceil / 2;
		}
		if (ceil / 2 >= high) {
			ceil = ceil / 2;
		}
	} else {
		if (lowCeil / 2 > ceil) {
			ceil = lowCeil / 2;
		}
		if (lowCeil / 2 >= -low) {
			lowCeil = lowCeil / 2;
		}
	}		
	return new Range(-lowCeil, ceil);
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 7:10:37 PM)
 * @return cbit.image.Range
 * @param d1 double
 * @param d2 double
 */
private static Range getNarrowRange(double low, double high) {
	Range range = new Range(low, high);
	double[] ticks = getMajorDecimalTicks(range);
	return new Range(ticks[0], ticks[ticks.length - 1]);
}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 7:10:37 PM)
 * @return cbit.image.Range
 * @param d1 double
 * @param d2 double
 * @param includeZero boolean
 */
private static Range getPositiveWideRange(double low, double high, boolean includeZero) {
	
	/* asumes non-negative and properly ranked numbers */
	
	double ceil = Math.pow(10, Math.ceil(Math.log(high) / Math.log(10)));
	if (ceil / 2 >= high) {
		ceil = ceil / 2;
	}
	double floor = 0;
	if (!(includeZero || low == 0)) {
		double diff = high - low;
		double diffCeil = Math.pow(10, Math.ceil(Math.log(diff) / Math.log(10)));
		if (diffCeil / 2 >= diff) {
			diffCeil = diffCeil / 2;
		}
		floor = ceil - diffCeil;
	}
	return new Range(floor, ceil);
}


/**
 * Computes a bounding range used to min and max values while plotting.
 * @return double[] - array of length 3 (array[0] = min, array[1] = max, array[2] = rounding_precision
 * @param num1 double
 * @param num2 double
 */
public static double[] roundNumberRange(double num1, double num2) {
//System.out.print("rounding ("+num1+","+num2+")");
	double rounded[] = new double[3];
	
	double low = Math.min(num1,num2);
	double high = Math.max(num1,num2);
	double diff = high - low;
	
	if (diff < 10e-8){
		//
		// if there is no range, create one
		//

		//
		// if ~equal to zero, make -1,1
		//
		if (Math.abs(low) < 10e-8){
			low = -1;
			high = 1;
			diff = high - low;
		}else{
			double epsilon = Math.abs(low/999);
			low = low - epsilon;
			high = high + epsilon;
			diff = high - low;
		}
	}
	
	double logDiff = Math.log(diff)/Math.log(10);
	int power = (int)(logDiff-0.3);
	double orderDiff = Math.pow(10,power);
	if (orderDiff > diff){
		orderDiff /= 10.0;
		power--;
	}			
	int newLowInt = (int)Math.floor(low / orderDiff);
	int newHighInt = (int)Math.ceil(high / orderDiff);
	BigDecimal bigLow = (new BigDecimal(newLowInt)).movePointRight(power);
	BigDecimal bigHigh = (new BigDecimal(newHighInt)).movePointRight(power);
		
	rounded[0] = bigLow.doubleValue();
	rounded[1] = bigHigh.doubleValue();	
	rounded[2] = orderDiff;
//System.out.println(" into ("+rounded[0]+","+rounded[1]+")");

	return rounded;	
}


/**
 * This method was created in VisualAge.
 * @return double
 * @param extent double
 * @param value double
 */
public static double roundToExtent(double extent, double value) {
	extent = Math.abs(extent);
	if (extent<10E-5){
		extent = 1;
	}
	double logDiff = Math.log(extent)/Math.log(10);
	int power = (int)(logDiff-4);
	double orderScale = Math.pow(10,power);
	int newValueInt = (int)Math.floor(value / orderScale);
	java.math.BigDecimal bigValue = (new java.math.BigDecimal(newValueInt)).movePointLeft(-power);
	return bigValue.doubleValue();
}
}
