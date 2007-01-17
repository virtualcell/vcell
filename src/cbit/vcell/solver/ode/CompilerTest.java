package cbit.vcell.solver.ode;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (8/6/2001 11:06:04 PM)
 * @author: Jim Schaff
 */
import cbit.vcell.server.PropertyLoader;
public class CompilerTest {
public static void main(java.lang.String[] args) {
	try {
		new PropertyLoader();
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
		Compiler compiler = new Compiler(new cbit.vcell.server.StdoutSessionLog("TEST"));
		compiler.setDefines(System.getProperty(PropertyLoader.definesProperty));
		compiler.setIncludes(System.getProperty(PropertyLoader.idaIncludeProperty));
		compiler.compile("E:\\Bullshit\\NewSIMULATION.cpp", "E:\\Bullshit\\CompilerTest.obj");
		compiler.setLibraries(System.getProperty(PropertyLoader.idaLibraryProperty));
		compiler.link(new String[] {"E:\\Bullshit\\CompilerTest.obj"}, "E:\\Bullshit\\CompilerTest.exe");
		//
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}