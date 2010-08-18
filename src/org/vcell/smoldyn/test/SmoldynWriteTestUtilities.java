package org.vcell.smoldyn.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.smoldynwriters.SimulationWriter;
import org.vcell.smoldyn.simulation.Simulation;


/**
 * 
 * 
 * @author mfenwick
 *
 */
public class SmoldynWriteTestUtilities {
	
	
	/**
	 * @param name filename (without extension)
	 * @param base should be an already-existing directory
	 * @param simulation the filled-out {@link Simulation}
	 */
	public static void findFilesAndPrintConfigFile(String name, String base, Simulation simulation) {
		//path = "Smoldyn\\output.txt";
		System.out.println("value of base: " + base);
		String path = base.concat(name + ".smol");
		PrintWriter printwriter;		
		try {
			File f = new File(path);
			printwriter = new PrintWriter(f);
//			printwriter = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			System.out.println("writing a file at: " + f.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		writeSmoldynRun(simulation, printwriter);
		Runtime r = Runtime.getRuntime();
		try {
			r.exec("cmd /c " + path);
			BufferedWriter bw = new BufferedWriter(new FileWriter(base.concat(name + ".bat")));
			bw.write("smoldyn2.15.exe " + name + ".smol");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void writeSmoldynRun(Simulation simulation, PrintWriter printwriter) {
		SimulationWriter.write(simulation, printwriter);
		if (printwriter.checkError() == true) {
			System.err.println("my printwriter encountered an error");
		}
		printwriter.flush();
		printwriter.close();
	}
}
