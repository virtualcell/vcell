package cbit.vcell.mapping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collections;

import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelChildSummary.ApplicationInfo;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.SubDomain;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class MembraneParticleDiffusionVisitor implements VCDatabaseVisitor {

	private static PrintStream mathKeyStream = null;

	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		
		BioModelChildSummary bioModelChildSummary = bioModelInfo.getBioModelChildSummary();
		if (bioModelChildSummary==null){
			return true; // fail safe, look at these models without proper child summaries.
		}
		for (ApplicationInfo appInfo : bioModelChildSummary.getApplicationInfo()){
			if (appInfo.type == MathType.Stochastic && appInfo.dimensions > 0){
				return true;
			}
		}
		//			return bioModelInfo.getVersion().getVersionKey().equals(new KeyValue("10765031"));//schaff
		return false;
		//			return bioModelInfo.getVersion().getName().equals("testVCell3");//frm
	}

	public boolean filterGeometry(GeometryInfo geometryInfo) {
		return false;
	}

	public void visitBioModel(BioModel bioModel, PrintStream logFilePrintStream) {

		try {
			logFilePrintStream.println(bioModel.getVersion().getName()+"  "+bioModel.getVersion().getDate()+"  "+bioModel.getVersion().getVersionKey());
			SimulationContext[] simContexts = bioModel.getSimulationContexts();
			for (int i = 0; i < simContexts.length; i++) {
				SimulationContext simContext = simContexts[i];
				MathDescription mathDescription = simContext.getMathDescription();
				if (simContext.isStoch() && simContext.getGeometry().getDimension()>0) {
					if (hasMembraneDiffusion(mathDescription)){
						logFilePrintStream.println("membraneDiffusion: "+mathDescription.getKey());
						mathKeyStream.println(bioModel.getVersion().getOwner().getName()+",\""+bioModel.getName()+"\","+bioModel.getVersion().getVersionKey()+",BioModel,"+mathDescription.getKey());
						mathKeyStream.flush();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logFilePrintStream.println(" failed "+e.getMessage());
		}
	}

	private boolean hasMembraneDiffusion(MathDescription mathDesc) throws ExpressionException{
		//
		// look inside math description (membrane subdomains) to find ParticleProperties with non-zero diffusion
		//
		for (SubDomain subDomain : Collections.list(mathDesc.getSubDomains())){
			if (subDomain instanceof MembraneSubDomain){
				MembraneSubDomain membraneSubDomain = (MembraneSubDomain)subDomain;
				for (ParticleProperties particleProperties : membraneSubDomain.getParticleProperties()){
					Expression diffusionCoef = particleProperties.getDiffusion();
					diffusionCoef = MathUtilities.substituteFunctions(diffusionCoef, mathDesc);
					diffusionCoef = diffusionCoef.flatten();
					if (!diffusionCoef.isZero()){
						return true;
					}
				}
			}
		}
		return false;
	}

	public void visitGeometry(Geometry geometry, PrintStream logFilePrintStream) {
		// TODO Auto-generated method stub

	}

	// required for interface implementation
	public boolean filterMathModel(MathModelInfo mathModelInfo) {
		return false;
	}
	public void visitMathModel(MathModel mathModel, PrintStream logFilePrintStream) {
		try {
			logFilePrintStream.println(mathModel.getVersion().getName()+"  "+mathModel.getVersion().getDate()+"  "+mathModel.getVersion().getVersionKey());
			MathDescription mathDescription = mathModel.getMathDescription();
			if (hasMembraneDiffusion(mathDescription)){
				logFilePrintStream.println("membraneDiffusion: "+mathDescription.getKey());
				mathKeyStream.println(mathModel.getVersion().getOwner().getName()+",\""+mathModel.getName()+"\","+mathModel.getVersion().getVersionKey()+",MathModel,"+mathDescription.getKey());
				mathKeyStream.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logFilePrintStream.println(" failed "+e.getMessage());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(new File("MemDiffusionMathIDs.txt"));
			mathKeyStream = new PrintStream(outputStream);
			MembraneParticleDiffusionVisitor visitor = new MembraneParticleDiffusionVisitor();
			boolean bAbortOnDataAccessException = false;
			try{
				VCDatabaseScanner.scanBioModels(args, visitor, bAbortOnDataAccessException);
			}catch(Exception e){e.printStackTrace(System.err);}
		} catch (Exception e1) {
			e1.printStackTrace(System.err);
		}finally{
			if (mathKeyStream!=null){
				mathKeyStream.close();
			}
		}
	}

}
