package cbit.vcell.mapping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;

import org.vcell.util.document.BioModelChildSummary.ApplicationInfo;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ClientTaskManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;

public class NFSimMathGenerationHashVisitor implements VCDatabaseVisitor {

	private static PrintStream mathGenHashStream = null;

	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		//
		// check for applicability of the model
		//
		// does this model have either
		//  1) a nonspatial stochastic application which can be copied as a rule-based model 
		//  2) a rulebased application
		//
		boolean bHasNonspatialStochastic = false;
		boolean bHasRulebased = false;
		for (ApplicationInfo appInfo : bioModelInfo.getBioModelChildSummary().getApplicationInfo()){
			if (appInfo.type == MathType.Stochastic && appInfo.dimensions == 0){
				bHasNonspatialStochastic = true;
			}
			if (appInfo.type == MathType.RuleBased){
				bHasRulebased = true;
			}
		}
		
		//
		// we would like to filter out models with more than one compartmnet ... but can't know this from the bioModelInfo.
		//
		
		// skip
		if (bioModelInfo.getVersion().getVersionKey().equals(new KeyValue("93037435"))){
			return false;
		}
		
		// candidate models
		if (bHasNonspatialStochastic || bHasRulebased){
			//
			// filter by user
			//
//			if (!bioModelInfo.getVersion().getOwner().getName().equals("schaff")){
//				return false;
//			}
			return true;
		}
		return false;
	}

	public void visitBioModel(BioModel bioModel, PrintStream logFilePrintStream) {
		try {
			logFilePrintStream.println(bioModel.getVersion().getName()+"  "+bioModel.getVersion().getDate()+"  "+bioModel.getVersion().getVersionKey());
			//
			// we only support a single compartment now.
			//
			if (bioModel.getModel().getStructures().length!=1){
				return;
			}
			
			ArrayList<SimulationContext> origNonspatialStochSimContexts = new ArrayList<SimulationContext>();
			ArrayList<SimulationContext> origRulebasedSimContexts = new ArrayList<SimulationContext>();
			
			SimulationContext[] simContexts = bioModel.getSimulationContexts();
			//
			// find applications which are nonspatial stoch or rulebased.
			//
			for (int i = 0; i < simContexts.length; i++) {
				SimulationContext simContext = simContexts[i];
				if (simContext.getApplicationType() == Application.NETWORK_STOCHASTIC && simContext.getGeometry().getDimension()==0){
					origNonspatialStochSimContexts.add(simContext);
				}else if (simContext.getApplicationType() == Application.RULE_BASED_STOCHASTIC){
					origRulebasedSimContexts.add(simContext);
				}
			}
			//
			// for each original nonspatial stochastic application, try to copy as Rulebased
			//
			for (SimulationContext origSsaSimContext : origNonspatialStochSimContexts) {
				boolean bSpatial = false;
				SimulationContext rulebasedSimContext = ClientTaskManager.copySimulationContext(origSsaSimContext, origSsaSimContext.getName()+"_rbmCopy", bSpatial, Application.RULE_BASED_STOCHASTIC);
				try {
					MathMapping mathMapping = rulebasedSimContext.createNewMathMapping();
					MathDescription generatedMathDesc = mathMapping.getMathDescription();
					mathGenHashStream.println(bioModel.getVersion().getOwner().getName()+",\""+bioModel.getName()+"\","+bioModel.getVersion().getVersionKey()+",RuleCopyOfSSA "+origSsaSimContext.getVersion().getVersionKey()+","+"good"+","+generateHashCode(generatedMathDesc));
					mathGenHashStream.flush();
				} catch (Exception e) {
					String errorMessage = e.getMessage().replace(',', '_').replace("\n","...");
					if (errorMessage.length()>100){
						errorMessage = errorMessage.substring(0, 100);
					}
					mathGenHashStream.println(bioModel.getVersion().getOwner().getName()+",\""+bioModel.getName()+"\","+bioModel.getVersion().getVersionKey()+",RuleCopyOfSSA "+origSsaSimContext.getVersion().getVersionKey()+","+errorMessage+","+0);
					e.printStackTrace();
				}
			}
			//
			// for each original rulebased application, try to copy as Nonspatial Stochastic
			//
			for (SimulationContext origRuleSimContext : origRulebasedSimContexts) {
				boolean bSpatial = false;
				SimulationContext ssaSimContext = ClientTaskManager.copySimulationContext(origRuleSimContext, origRuleSimContext.getName()+"_ssaCopy", bSpatial, Application.NETWORK_STOCHASTIC);
				try {
					MathMapping mathMapping = ssaSimContext.createNewMathMapping();
					MathDescription generatedMathDesc = mathMapping.getMathDescription();
					mathGenHashStream.println(bioModel.getVersion().getOwner().getName()+",\""+bioModel.getName()+"\","+bioModel.getVersion().getVersionKey()+",SsaCopyOfRule "+origRuleSimContext.getVersion().getVersionKey()+","+"good"+","+generateHashCode(generatedMathDesc));
					mathGenHashStream.flush();
				} catch (Exception e) {
					String errorMessage = e.getMessage().replace(',', '_').replace("\n","...");
					if (errorMessage.length()>100){
						errorMessage = errorMessage.substring(0, 100);
					}
					mathGenHashStream.println(bioModel.getVersion().getOwner().getName()+",\""+bioModel.getName()+"\","+bioModel.getVersion().getVersionKey()+",SsaCopyOfRule "+origRuleSimContext.getVersion().getVersionKey()+","+errorMessage+","+0);
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logFilePrintStream.println(" failed "+e.getMessage());
		}
	}

	private String generateHashCode(MathDescription generatedMathDesc) throws MathException {
		String vcml = generatedMathDesc.getVCML_database();
		MessageDigest md = null;
	    try {
	        md = MessageDigest.getInstance("SHA-1");
	    }
	    catch(NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } 
	    return byteToHex(md.digest(vcml.getBytes()));
	}

	private static String byteToHex(final byte[] hash)
	{
	    Formatter formatter = new Formatter();
	    for (byte b : hash)
	    {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}

	public boolean filterGeometry(GeometryInfo geometryInfo) {
		return false;
	}

	public void visitGeometry(Geometry geometry, PrintStream logFilePrintStream) {
	}

	public boolean filterMathModel(MathModelInfo mathModelInfo) {
		return false;
	}

	public void visitMathModel(MathModel mathModel, PrintStream logFilePrintStream) {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(new File("NFSimMathGenerationHash.txt"));
			mathGenHashStream = new PrintStream(outputStream);
			NFSimMathGenerationHashVisitor visitor = new NFSimMathGenerationHashVisitor();
			boolean bAbortOnDataAccessException = false;
			try{
				VCDatabaseScanner.scanBioModels(args, visitor, bAbortOnDataAccessException);
			}catch(Exception e){e.printStackTrace(System.err);}
		} catch (Exception e1) {
			e1.printStackTrace(System.err);
		}finally{
			if (mathGenHashStream!=null){
				mathGenHashStream.close();
			}
		}
	}

}
