package org.vcell.smoldyn.readingoutput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class ReadLocations {

	public static void main(String [] args) {
		getNumbersAsDoubles();
	}
	
	public static Vector<Vector<Double>> readfile() {
//		String path = "srcSmoldyn\\org\\vcell\\smoldyn\\matts_project\\readingoutput\\data.txt";
		String path = "SmoldynTest\\filehandle.txt";
		File file = new File(path);
		System.out.println("the absolute path to the file is: " + file.getAbsolutePath());
		BufferedReader br;
		Vector<Vector<Double>> doubles = new Vector<Vector<Double>>(4);
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while((line = br.readLine()) != null) {
				//if (line.matches("[0-9]")) {
				
					String [] numbers = line.split(" ");
					Vector<Double> numline = new Vector<Double>();
					for(String s : numbers) {
						try {
							numline.addElement(Double.parseDouble(s));
						} catch (NumberFormatException n) {
							continue;
						}
					}
					doubles.addElement(numline);
				//}
			}
//			for(int i = 0; i < integers.size(); i++) {
//				for(int j = 0; j < integers.elementAt(i).size(); j++) {
//					System.out.print(integers.elementAt(i).elementAt(j) + " ");
//				}
//				System.out.println();
//			}
			return doubles;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("couldn't get file data");
		}
	}
	
	public static double [] [] getNumbersAsDoubles() {
		Vector<Vector<Double>> nums = ReadLocations.readfile();
		double [] d1 = new double [nums.size()-1];
		double [] d2 = new double [nums.size()-1];
		double [] d3 = new double [nums.size()-1];
		double [] d4 = new double [nums.size()-1];
		double [] d5 = new double [nums.size()-1];
		int j = 0;
		for(int i = 0; i < nums.size(); i++) {
			if (nums.elementAt(i).size() > 1) {
				d1[j] = nums.elementAt(i).elementAt(0);
				d2[j] = nums.elementAt(i).elementAt(1);
				d3[j] = nums.elementAt(i).elementAt(2);
				d4[j] = nums.elementAt(i).elementAt(3);
				d5[j] = nums.elementAt(i).elementAt(4);
				j++;
			}
		}
		double [] [] out = {d1, d2, d3, d4, d5};
		return out;
	}
}
