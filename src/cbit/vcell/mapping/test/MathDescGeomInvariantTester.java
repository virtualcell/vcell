package cbit.vcell.mapping.test;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.vcell.util.BeanUtils;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;

public class MathDescGeomInvariantTester implements VCDatabaseVisitor {

	public boolean filterBioModel(BioModelInfo bioModelInfo) {
//		return bioModelInfo.getVersion().getVersionKey().equals(new KeyValue("10765031"));//schaff
		return true;
//		return bioModelInfo.getVersion().getName().equals("testVCell3");//frm
	}

	public boolean filterGeometry(GeometryInfo geometryInfo) {
		return false;
	}

	/**
	 *This method tests whether MathDescription will ignore non-invariant elements of
	 *Geometry when testing for equivalency with another MathDescription.  This test is
	 *in support of a bug fix to prevent save operations from throwing away
	 *Simulation results unnecessarily.  GeometrySurfaceDescription information is
	 *stored in a Document but is also regenerated during certain user operations.
	 *The regenerated GeometrySurfaceDescription may not be the same as the stored version
	 *and causes numerical equivalency test to fail.  This test ensures that GeometrySurfaceDescription
	 *differences alone cause "compareEqual(...)" to fail and "compareEquivalent(...)" to succeed.
	 *
	 * @param  bioModel  BioModel to perform test on
	 * @param  logFilePrintStream PrintStream for writing informative information to
	 * @return      void
	 * @see         MathDescription,GeometrySurfaceDescription,VCDatabaseScanner
	 */
	 public void visitBioModel(BioModel bioModel, PrintStream logFilePrintStream) {

		try {
			logFilePrintStream.println(bioModel.getVersion().getName()+"  "+bioModel.getVersion().getDate()+"  "+bioModel.getVersion().getVersionKey());
			SimulationContext[] simContexts = bioModel.getSimulationContexts();
			for (int i = 0; i < simContexts.length; i++) {
				SimulationContext simContext = simContexts[i];
				Geometry geom = simContext.getGeometry();
				logFilePrintStream.print("     '"+simContext.getName()+"'");
				logFilePrintStream.print("  geomKey="+geom.getVersion().getVersionKey());
				GeometrySurfaceDescription geomSurfDescOrig = geom.getGeometrySurfaceDescription();
				try {
					if(geomSurfDescOrig == null){
						logFilePrintStream.println("  No Surface Description");
						continue;
					}
					MathDescription mathDescOrig = simContext.getMathDescription();
					MathDescription mathDescUpdated = (MathDescription)BeanUtils.cloneSerializable(mathDescOrig);
					mathDescUpdated.getGeometry().getGeometrySurfaceDescription().updateAll();
					boolean bCompareEqual = mathDescOrig.compareEqual(mathDescUpdated);
					logFilePrintStream.print("  compareEqual="+bCompareEqual);
					StringBuffer reasonForDecision = new StringBuffer();
					boolean bMathDescEquiv = mathDescOrig.compareEquivalent(mathDescUpdated, reasonForDecision, false);
					logFilePrintStream.print("  compareEquivalent="+bMathDescEquiv);
					logFilePrintStream.print("  reason="+reasonForDecision.toString());
					GeometrySurfaceDescription geomSurfDescUpdated = mathDescUpdated.getGeometry().getGeometrySurfaceDescription();
					logFilePrintStream.print("  geomSurfDesc="+geomSurfDescOrig.compareEqual(geomSurfDescUpdated));
					if(!bCompareEqual){
						logFilePrintStream.println();
						List<GeometricRegion> geomRegionOrigList = new Vector<GeometricRegion>(Arrays.asList(geomSurfDescOrig.getGeometricRegions().clone()));
						List<GeometricRegion> geomRegionUpdatedList = new Vector<GeometricRegion>(Arrays.asList(geomSurfDescUpdated.getGeometricRegions().clone()));
						while(geomRegionOrigList.size() > 0) {
							double sizeOrig = geomRegionOrigList.get(0).getSize();
							double sizeUpdated = -1;
							for (int k = 0; k < geomRegionUpdatedList.size(); k++) {
								if(geomRegionUpdatedList.get(k).getName().equals(geomRegionOrigList.get(0).getName())){
									sizeUpdated = geomRegionUpdatedList.get(k).getSize();
									geomRegionUpdatedList.remove(k);
									break;
								}
							}
							if(sizeUpdated != -1){
								logFilePrintStream.println("         "+(sizeOrig != sizeUpdated?"* ":"  ")+"'"+geomRegionOrigList.get(0).getName()+"' orig="+sizeOrig+" updt="+sizeUpdated);
							}else{
								logFilePrintStream.println("         O '"+geomRegionOrigList.get(0).getName()+"' orig="+sizeOrig+" updt=");
							}
							geomRegionOrigList.remove(0);
						}
						for (int j = 0; j < geomRegionUpdatedList.size(); j++) {
							logFilePrintStream.println("         U '"+geomRegionUpdatedList.get(j).getName()+"' orig= updt="+geomRegionUpdatedList.get(j).getSize());
						}
					}else{
						logFilePrintStream.println();
					}
				} catch (Exception e) {
					logFilePrintStream.println(" failed "+e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void visitGeometry(Geometry geometry, PrintStream logFilePrintStream) {
		// TODO Auto-generated method stub

	}

	// required for interface implementation
	public boolean filterMathModel(MathModelInfo mathModelInfo) {
		return false;
	}
	public void visitMathModel(MathModel mathModel, PrintStream logFilePrintStream) {
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MathDescGeomInvariantTester visitor = new MathDescGeomInvariantTester();
		boolean bAbortOnDataAccessException = false;
		try{
			VCDatabaseScanner.scanBioModels(args, visitor, bAbortOnDataAccessException);
		}catch(Exception e){e.printStackTrace(System.err);}
	}

}
