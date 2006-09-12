package cbit.vcell.matlab;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.solver.*;
import cbit.vcell.geometry.*;
import java.util.*;
import cbit.vcell.model.*;
import java.io.*;
import cbit.vcell.math.*;
import cbit.vcell.math.gui.*;
import cbit.vcell.mapping.*;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 11:10:26 PM)
 * @author: 
 */
public class MatlabOdeFileCoderTest extends cbit.vcell.client.server.ClientTester {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	MathDescription mathDesc = null;
	try {
		cbit.vcell.server.VCellConnection vcConn = VCellConnectionFactoryInit(args, "MatlabOdeFileCoderTest").createVCellConnection();
		//MathDbDialog aMathDbDialog = new MathDbDialog();
		//aMathDbDialog.setModal(true);
		//new cbit.gui.WindowCloser(aMathDbDialog, false);
		//aMathDbDialog.init(vcConn.getUserMetaDbServer(), vcConn.getUser(), new MathDescription("emptyMathDesc"));
		//aMathDbDialog.setVisible(true);
		//mathDesc = aMathDbDialog.getMathDescription();

		mathDesc = MathDescriptionTest.getOdeExampleWagner();
		Simulation simulation = new cbit.vcell.solver.Simulation(mathDesc);

		// SimulationContext simContext = SimulationContextTest.getExample(2);
		// TimeScaleAnalyzer timeScaleAnalyzer = new TimeScaleAnalyzer(simContext);
		// MathDescription mathDesc = timeScaleAnalyzer.getMathDescription();
		cbit.vcell.matrix.RationalMatrixFast stoichMatrix = null; // timeScaleAnalyzer.getReducedStoichMatrix();
		
		MatlabOdeFileCoder odeFileCoder = new MatlabOdeFileCoder(simulation, stoichMatrix);

		//java.awt.Frame fileDialogFrame = new java.awt.Frame();
		//java.awt.FileDialog fileDialog = new java.awt.FileDialog(fileDialogFrame);
		//fileDialog.setTitle("enter matlab odefile name");
		//fileDialog.setFile(mathDesc.getName()+".ode");
		//fileDialog.setVisible(true);
		//String odeFileName = fileDialog.getFile();
		//if (odeFileName == null){
			//System.out.println("required output filename not specified");
			//System.exit(1);
		//}
		//FileOutputStream fos = new FileOutputStream(odeFileName);
		//PrintWriter pw = new PrintWriter(fos);
		
		java.io.StringWriter sw = new java.io.StringWriter();
		java.io.PrintWriter pw = new java.io.PrintWriter(sw);
		odeFileCoder.write_V5_OdeFile(pw,"odefile");
		pw.flush();
		pw.close();
		System.out.println("\n\n\n-------------------------Matlab 5.0 (old)---------------------");
		System.out.println(sw.getBuffer().toString());
		System.out.println("\n");
		
		java.io.StringWriter sw1 = new java.io.StringWriter();
		java.io.PrintWriter pw1 = new java.io.PrintWriter(sw1);
		odeFileCoder.write_V6_MFile(pw1,"odefunc");
		pw1.flush();
		pw1.close();
		System.out.println("\n\n\n-------------------------Matlab 6.0 (new)---------------------");
		System.out.println(sw1.getBuffer().toString());

		
	}catch (Throwable e){
		System.out.println("uncaught exception in MatlabOdeFileCoderTest.main()");
		e.printStackTrace(System.out);
	}finally{
		System.exit(1);
	}
}
}