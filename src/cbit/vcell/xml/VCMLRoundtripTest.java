package cbit.vcell.xml;

import java.io.PrintStream;
import java.util.Vector;

import cbit.sql.KeyValue;
import cbit.sql.SimulationVersion;
import cbit.util.BeanUtils;
import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.MathDescription;
import cbit.vcell.model.Structure;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.IDAFileWriter;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.VariableComparisonSummary;
import cbit.vcell.util.RowColumnResultSet;

public class VCMLRoundtripTest implements VCDatabaseVisitor {

	public VCMLRoundtripTest(){
	}

	public static void main(java.lang.String[] args) {
		try {
			VCMLRoundtripTest vcmlRoundtripTest = new VCMLRoundtripTest();
			boolean bAbortOnDataAccessException = false;
			VCDatabaseScanner.scanBioModels(args, vcmlRoundtripTest, bAbortOnDataAccessException);
		} catch (Throwable exception) {
			System.out.println("Exception occurred in main() of SBVCVerifier");
			exception.printStackTrace(System.out);
		} finally {
			System.exit(0);
		}
	}


public boolean filterBioModel(BioModelInfo bioModelInfo) {
		if (bioModelInfo.getVersion().getOwner().getName().equals("ion")) {  
//				|| bioModelInfo.getVersion().getOwner().getName().equals("anu")  
//				|| bioModelInfo.getVersion().getOwner().getName().equals("schaff")  
//				|| bioModelInfo.getVersion().getOwner().getName().equals("ion")) {
			// System.err.println("BM name : " + bioModelInfo.getVersion().getName() + "\tdate : " + bioModelInfo.getVersion().getDate().toString());
			return true;
		}
		return false;
	}


	public boolean filterGeometry(GeometryInfo geometryInfo) {
		// TODO Auto-generated method stub
		return false;
	}


	public void visitBioModel(BioModel bioModel_1, PrintStream logFilePrintStream) {
		try {
			/* Roundtrip (export -> import) this application to VCML  and back */

			// Export to VCML
			logFilePrintStream.println("\nUser : " + bioModel_1.getVersion().getOwner().getName() + ";\tBiomodel : " + bioModel_1.getName() + ";\tDate : " + bioModel_1.getVersion().getDate().toString());
			String exportedVCMLStr = null;
			BioModel bioModel_2 = null;
			// Import into VCell		
			exportedVCMLStr = XmlHelper.bioModelToXML(bioModel_1);
			XmlUtil.writeXMLString(exportedVCMLStr, "C:\\VCML_Schema\\VCMLRoundtripValidation\\Validation1_28_08\\" + bioModel_1.getName()+".vcml");
			// Import the exported VCML model
			bioModel_2 = (BioModel) cbit.vcell.xml.XmlHelper.XMLToBioModel(exportedVCMLStr);
//			SimulationContext[] simContexts = bioModel_2.getSimulationContexts();
//			for (int i = 0; i < simContexts.length; i++) {
//				simContexts[i].setMathDescription((new MathMapping(simContexts[i])).getMathDescription());
//			}
//			bioModel_2.refreshDependencies();
			// export again to compare the 2 exported strings
			String newExportedString = XmlHelper.bioModelToXML(bioModel_2);
			XmlUtil.writeXMLString(exportedVCMLStr, "C:\\VCML_Schema\\VCMLRoundtripValidation\\Validation1_28_08\\" + bioModel_1.getName()+"__RT.vcml");
			// compare the 2 biomodel objects:
			boolean bModelsEqual = bioModel_1.compareEqual(bioModel_2);
			// compare the 2 strings:
			boolean bXmlsEqual = exportedVCMLStr.equals(newExportedString);
			logFilePrintStream.println("Biomodel : " + bioModel_1.getName() + "\t; ModelCompare : " + bModelsEqual + ";\t XmlCompare : " + bXmlsEqual);
			// System.out.println("Biomodel : " + bioModel_1.getName() + "\t; ModelCompare : " + bModelsEqual + ";\t XmlCompare : " + bXmlsEqual);
		}catch (Throwable e){
	        e.printStackTrace(logFilePrintStream);
			logFilePrintStream.println("\nBiomodel : " + bioModel_1.getName() + "\t roundtrip from VCML FAILED");
			System.out.println("Biomodel : " + bioModel_1.getName() + "\t roundtrip from VCML FAILED");
		}
	}

	public void visitGeometry(Geometry geometry, PrintStream logFilePrintStream) {
		// TODO Auto-generated method stub
		
	}

	}