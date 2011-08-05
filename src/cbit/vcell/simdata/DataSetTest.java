/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

/**
 * This type was created in VisualAge.
 */
public class DataSetTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] argv) {

	if (argv.length!=5){
		System.out.println("cbit.vcell.math.DataSetTest filename.sim out.tif varName minInput maxInput");
		return;
	}
	String simFilename = argv[0];
	String tiffFilename = argv[1];
	String varName = argv[2];

	DataSet dataSet = new DataSet();
	try{
		dataSet.read(new java.io.File(simFilename), null);
	}catch (Exception e){
		System.out.println("exception: " + e.getMessage());
		e.printStackTrace();
	}
	
	double data[] = null;
	try {
		data = dataSet.getData(varName, null);
	}catch (Exception e){
		System.out.println("Error getting variable '"+varName+"' from simulation file");
		e.printStackTrace();
	}		

	double minInput;
	double maxInput;
	double minValue = 1e10;
	double maxValue = -1e10;
	for (int i=0;i<data.length;i++){
		minValue = Math.min(minValue,data[i]);
		maxValue = Math.max(maxValue,data[i]);
	}
		
	if (argv[3].equals("-")){
		minInput = minValue;
	}else{
		minInput = (new Double(argv[3])).doubleValue();
	}
		
	if (argv[4].equals("-")){
		maxInput = maxValue;
	}else{
		maxInput = (new Double(argv[4])).doubleValue();
	}
	
	byte outputArray[] = new byte[data.length];
	for (int i=0;i<data.length;i++){
		double value = Math.min(maxInput,Math.max(minInput,data[i]));
		outputArray[i] = (byte)((int)(255.0*(value - minInput)/(maxInput - minInput)));
	}	
	System.out.println("using input Lo = "+minInput+", Hi = "+maxInput);

	cbit.image.TiffImage img = new cbit.image.TiffImage(dataSet.getSizeX(), dataSet.getSizeY(), dataSet.getSizeZ(), outputArray);
	try {
		img.write(tiffFilename,cbit.image.ByteOrder.Unix);
		System.out.println("generating file "+tiffFilename);
	}catch(Exception e){
		e.printStackTrace();
	}
}
}
