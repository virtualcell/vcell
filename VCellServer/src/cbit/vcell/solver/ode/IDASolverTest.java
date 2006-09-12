package cbit.vcell.solver.ode;
import cbit.gui.PropertyLoader;
import java.io.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (6/21/01 12:25:42 PM)
 * @author: Jim Schaff
 */
public class IDASolverTest {
public static void main(java.lang.String[] args) {
	try {
		new PropertyLoader();
		IDASolverTest test = new IDASolverTest();
		//test.testStandalone("les");
		//test.testStandalone("ion");
		//test.testStandalone("cwc219");
		test.testStandalone("eungdamr");
		//test.testStandalone("skkrishnamurthy");
		/*
		//
		cbit.vcell.math.MathDescription mathDescription = cbit.vcell.math.MathDescriptionTest.getOdeExample();
		cbit.vcell.solver.Simulation simulation = new cbit.vcell.solver.Simulation(mathDescription) {
			public String getSimulationIdentifier() {
				return ("NewSIMULATION");
			}
			public void constantAdded(cbit.vcell.solver.MathOverridesEvent e){
			}
			public void constantRemoved(cbit.vcell.solver.MathOverridesEvent e){
			}
			public void constantChanged(cbit.vcell.solver.MathOverridesEvent e){
			}
			public void clearVersion(){
			}
		};
		//
		IDASolver solver = new IDASolverStandalone(new SimulationJob(simulation, 0), new java.io.File("C:\\temp"), new cbit.vcell.server.StdoutSessionLog("IDASolverTest"));
		//
		solver.startSolver();
		//
		while (solver.getSolverStatus().getStatus() != cbit.vcell.solver.SolverStatus.SOLVER_STOPPED){
			try {
				Thread.sleep(1000);
			}catch(InterruptedException e){
			}
		}
		*/
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/19/2005 11:28:31 AM)
 */
public void testStandalone(String username) {
	try {
		
		java.io.File userdir = new java.io.File("\\\\san2\\raid\\vcell\\users\\" + username);
		char[] oldBuffer = new char[10000];
		char[] newBuffer = new char[10000];
		FileReader oldReader = null;
		FileReader newReader = null;
		
		String[] idaInputFiles = userdir.list(
			new java.io.FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.endsWith(".idaInput")) {
						return true;
					}
					return false;
				}
			}
		);
		String old_idastandalone = "\\\\san2\\raid\\vcell\\deployed\\C++Lib\\VSS_ALPHA\\VCell\\IDAWin\\IDAStandalone.exe";
		String new_idastandalone = "D:\\ClearCaseClient\\fgao_VirtualCell\\VCellVOB\\InitialComponent\\VCell\\IDAWin\\IDAStandalone.exe";		
		String outdir = "h:\\VCell\\IDATest\\";

		PrintWriter pw = new PrintWriter(new FileOutputStream(outdir + "idatest_" + username + ".txt"));
		for (int i = 0; i < idaInputFiles.length; i ++) {
			String inputfile = idaInputFiles[i];
			String prefix = inputfile.substring(0, inputfile.lastIndexOf("."));
			
			File userOutputdir = new File(outdir, username);
			if (!userOutputdir.exists()) {
				userOutputdir.mkdir();
			}


			File oldOutput = new File(userOutputdir, prefix + ".oldida");
			File newOutput = new File(userOutputdir, prefix + ".newida");			
			String inputfilepath = userdir + "\\" + inputfile;
			String cmd_old = old_idastandalone +  " " +  inputfilepath + " " + oldOutput.getAbsolutePath();
			String cmd_new = new_idastandalone +  " " +  inputfilepath + " " + newOutput.getAbsolutePath();
			try {
				cbit.util.Executable exe = new cbit.util.Executable(cmd_old);
				exe.start();
				exe = new cbit.util.Executable(cmd_new);
				exe.start();
			} catch (Exception ex) {
				if (!newOutput.exists()) {
					pw.println(i + ": Something is wrong with " + inputfilepath);
					System.out.println("----------------------" + i + ": Something is wrong with : " + inputfilepath + "------------------------");
					continue;
				}
			}

			// compare old and new results

			oldReader = new FileReader(oldOutput);
			newReader = new FileReader(newOutput);

			boolean bEqual = true;
				
			while (true) {				
				int m = oldReader.read(oldBuffer, 0, 10000);
				int n = newReader.read(newBuffer, 0, 10000);

				if (m != n) {
					bEqual = false;
					break;
				}

				if (m == -1) {
					break;
				}

				if (!java.util.Arrays.equals(oldBuffer, newBuffer)) {
					bEqual = false;
					break;
				}
			}
			oldReader.close();
			newReader.close();
			
			if (bEqual) {
				pw.println(i + ": Same : " + inputfilepath);
				System.out.println("----------------------" + i + ": Same : " + inputfilepath + "------------------------");
			} else {
				pw.println(i + ": Different : " + inputfilepath);
				System.out.println("----------------------" + i + ": Different : " + inputfilepath + "------------------------");
			}

		}
		pw.close();
	} catch (Exception ex) {
	}
	
}
}