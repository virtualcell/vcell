package cbit.vcell.geometry.surface;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverFactory;
import cbit.vcell.solvers.CartesianMesh;
import cbit.sql.ConnectionFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.document.User;

import cbit.vcell.server.SessionLog;
import cbit.vcell.simdata.gui.MeshDisplayAdapter;

/**
 * Insert the type's description here.
 * Creation date: (8/2/2005 3:08:51 PM)
 * @author: Jim Schaff
 */
public class GeometryRegressionTest {
	SessionLog log = new cbit.vcell.server.StdoutSessionLog("GeometryTest");
	
	String simlistfile = "D:\\VCell\\geometrytest\\simlist.txt";
	
	String comparemeshfile = "D:\\VCell\\geometrytest\\comparemesh.txt";
	
	String new_dataDir = "d:\\vcell\\geometrytest\\new";
	String new_faillistfile = "D:\\VCell\\geometrytest\\faillist_new.txt";				
	
	String old_dataDir = "d:\\vcell\\geometrytest\\old";
	String old_faillistfile = "D:\\VCell\\geometrytest\\faillist_old.txt";

	User adminUser = new User("Administrator", new org.vcell.util.document.KeyValue("1"));	

/**
 * GeometryRegressionTest constructor comment.
 */
public GeometryRegressionTest() {
	super();
	cbit.vcell.modeldb.DatabasePolicySQL.bAllowAdministrativeAccess = true;
}


/**
 * Insert the method's description here.
 * Creation date: (8/4/2005 8:25:37 AM)
 */
public void compareMesh(String startKey) {
	try {		
		File file_old = new File(old_dataDir);
		File file_new = new File(new_dataDir);
		java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(simlistfile));
		int count = 0;
		java.io.PrintWriter pw = null;
		int numEqual = 0;
		int numNotEqual = 0;
		int numInvalid = 0;
		int numOldNotExist = 0;
		int numNewNotExist = 0;
		
		boolean bStart = true;
		if (startKey == null) {
			bStart = true;
			pw = new java.io.PrintWriter(new java.io.FileOutputStream(comparemeshfile), true);
		} else {
			bStart = false;
			pw = new java.io.PrintWriter(new java.io.FileOutputStream(comparemeshfile, true), true);
		}		 
		
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			line = line.trim();
			count ++;
			
			if (!bStart) {
				if (startKey.equals(line)) {
					bStart = true;
				} else {
					continue;
				}
			} 
			
			try {				
				cbit.vcell.solver.VCSimulationIdentifier simid = new cbit.vcell.solver.VCSimulationIdentifier(new org.vcell.util.document.KeyValue(line), adminUser);
				File meshfile_old = new File(file_old, simid.getID() +"_0_.mesh");
				File meshfile_new = new File(file_new, simid.getID() +"_0_.mesh");				
				File meshmetricsfile_old = new File(file_old, simid.getID() +"_0_.meshmetrics");
				File meshmetricsfile_new = new File(file_new, simid.getID() +"_0_.meshmetrics");				

				if (!meshfile_old.exists() && !meshfile_new.exists()) {
					numInvalid ++;
					System.out.println(count + ": Comparing " + line + " ------ Old and new mesh don't exist\n");
					pw.println(count + ", " + line + ", ?D, Old and new mesh don't exist");
					continue;
				} else if (!meshfile_old.exists()) {
					numOldNotExist ++;
					System.out.println(count + ": Comparing " + line + " ------ Old mesh doesn't exist\n");
					pw.println(count + ", " + line + ", ?D, Old mesh doesn't exist");
					continue;
				} else if (!meshfile_new.exists()) {
					numNewNotExist ++;
					System.out.println(count + ": Comparing " + line + " ------ New mesh doesn't exist\n");
					pw.println(count + ", " + line + ", ?D, New mesh doesn't exist");
					continue;
				}
				cbit.vcell.solvers.CartesianMesh mesh_old = loadMesh(meshfile_old, meshmetricsfile_old);
				cbit.vcell.solvers.CartesianMesh mesh_new = loadMesh(meshfile_new, meshmetricsfile_new);
				
				if (CartesianMesh.compareMesh(mesh_old, mesh_new, pw)) {
					numEqual ++;
					System.out.println(count + ": Comparing " + line + " ------ " + mesh_old.getGeometryDimension() + "D, Equal\n");
					//pw.println(count + ", " + line + ", " + mesh_old.getGeometryDimension() + "D, Equal");
				} else {
					numNotEqual ++;
					System.out.println(count + ": Comparing " + line + " ------ " + mesh_old.getGeometryDimension() + "D, NOT Equal, # Of MembraneElements=" + mesh_old.getNumMembraneElements() + "\n");
					pw.println(count + ", " + line + ", " + mesh_old.getGeometryDimension() + "D, NOT Equal, # Of MembraneElements=" + mesh_old.getNumMembraneElements());
					//return;
				}
			} catch (Exception ex) {
				pw.println("--------------------------------------");
				pw.println(line + " failed");
				ex.printStackTrace(pw);
				pw.println("--------------------------------------");				
			}
		}
		br.close();
		pw.close();
		System.out.println("**************DONE********************");
		System.out.println("Total #: " + count);
		System.out.println("# of Equal: " + numEqual);
		System.out.println("# of NOT Equal: " + numNotEqual);
		System.out.println("# of Invalid: " + numInvalid);
		System.out.println("# of OldNotExist: " + numOldNotExist);
		System.out.println("# of NewNotExist: " + numNewNotExist);
		
		pw.println("Total #: " + count);
		pw.println("# of Equal: " + numEqual);
		pw.println("# of NOT Equal: " + numNotEqual);
		pw.println("# of Invalid: " + numInvalid);
		pw.println("# of OldNotExist: " + numOldNotExist);
		pw.println("# of NewNotExist: " + numNewNotExist);		
	} catch (Exception ex) {
		ex.printStackTrace();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/5/2005 7:10:26 AM)
 * @param meshfile java.io.File
 */
public static cbit.vcell.solvers.CartesianMesh loadMesh(File meshFile, File meshmetricsFile) throws Exception {
	CartesianMesh mesh = CartesianMesh.readFromFiles(meshFile,meshmetricsFile);
	return mesh;	
}


/**
 * Insert the method's description here.
 * Creation date: (8/2/2005 3:09:52 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		if (args.length == 0) {
			System.out.println("Usage: GometryRegressionTest old|new|compare [simkey]");
			System.exit(0);		
		}

		GeometryRegressionTest grt = new GeometryRegressionTest();
		if (args[0].equalsIgnoreCase("new")) {	
			System.setProperty(cbit.vcell.server.PropertyLoader.propertyFileProperty, "C:\\Documents and Settings\\fgao\\vcell.properties");
			cbit.vcell.server.PropertyLoader.loadProperties();
			String startKey = null;
			if (args.length > 1) {
				startKey = args[1];
			}
			grt.runSimulations(true, startKey);
		} else if (args[0].equalsIgnoreCase("old")){
			System.setProperty(cbit.vcell.server.PropertyLoader.propertyFileProperty, "\\\\san1\\raid\\vcell\\deployed\\manager\\alpha\\configs\\vcell.properties");
			cbit.vcell.server.PropertyLoader.loadProperties();	
			String startKey = null;
			if (args.length > 1) {
				startKey = args[1];
			}
			grt.runSimulations(false, startKey);
		} else if (args[0].equalsIgnoreCase("compare")) {
			String startKey = null;
			if (args.length > 1) {
				startKey = args[1];
			}
			grt.compareMesh(startKey);
		}
	} catch (Exception ex ){
		ex.printStackTrace();
	} finally {
		System.exit(0);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (8/2/2005 3:09:52 PM)
 * @param args java.lang.String[]
 */
public void runSimulations(boolean bNew, String startKey) {
	try {		
		cbit.vcell.modeldb.DatabasePolicySQL.bAllowAdministrativeAccess = true;
		
		String dataDir = null;
		String faillistfile = null;		
		if (bNew) {
			dataDir = new_dataDir;
			faillistfile = new_faillistfile;
		} else {
			dataDir = old_dataDir;
			faillistfile = old_faillistfile;
		}
		ConnectionFactory conFactory = new cbit.sql.OraclePoolingConnectionFactory(log);
		cbit.sql.KeyFactory	keyFactory = new cbit.sql.OracleKeyFactory();
		cbit.sql.DBCacheTable dbCacheTable = new cbit.sql.DBCacheTable(1000*60*30);
		cbit.vcell.modeldb.DatabaseServerImpl dbServerImpl = new cbit.vcell.modeldb.DatabaseServerImpl(conFactory, keyFactory, dbCacheTable, log);	 
		
		java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(simlistfile));
		int count = 0;

		java.io.PrintWriter pw = null;
		boolean bStart = true;
		if (startKey == null) {
			pw = new java.io.PrintWriter(new java.io.FileOutputStream(faillistfile), true);
			bStart = true;
		} else {
			pw = new java.io.PrintWriter(new java.io.FileOutputStream(faillistfile, true), true);		
			bStart = false;
		}
		while (true) {
			String line = br.readLine();
			if (line == null) {
				System.out.println("**************DONE********************");
				break;
			}
			line = line.trim();
			count ++;

			if (!bStart) {
				if (startKey.equals(line)) {
					bStart = true;
				} else {
					continue;
				}
			} 
			 
			System.out.println("--------------------------" + count + ": Running simulation " + line + "-----------------------------");
			
			try {
				org.vcell.util.BigString simxml = dbServerImpl.getSimulationXML(adminUser, new org.vcell.util.document.KeyValue(line));
				cbit.vcell.solver.Simulation sim = cbit.vcell.xml.XmlHelper.XMLToSim(simxml.toString());
				sim.getSolverTaskDescription().setTimeStep(new cbit.vcell.solver.TimeStep(0.001, 0.001, 0.001));
				sim.getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(0,0.001));
				cbit.vcell.solver.Solver solver = SolverFactory.createSolver(log, new File(dataDir), new SimulationJob(sim,null, 0));		
				solver.startSolver();
				while (true) {
					cbit.vcell.solver.SolverStatus solverStatus = solver.getSolverStatus();
					
					if (solverStatus != null){
						if (solverStatus.getStatus() != cbit.vcell.solver.SolverStatus.SOLVER_STARTING &&
							solverStatus.getStatus() != cbit.vcell.solver.SolverStatus.SOLVER_READY &&
							solverStatus.getStatus() != cbit.vcell.solver.SolverStatus.SOLVER_RUNNING){
							break;
						}
					}				
				}
			} catch (Exception ex) {
				pw.println("--------------------------------------");
				pw.println(line + " failed");
				ex.printStackTrace(pw);
				pw.println("--------------------------------------");				
			}
		}
		br.close();
		pw.close();
	} catch (Exception ex) {
		ex.printStackTrace();
	}
 
 }
}