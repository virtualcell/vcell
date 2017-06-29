package cbit.vcell.mapping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;

public class MathGenerationHashVisitor implements VCDatabaseVisitor {

	private static PrintStream mathGenHashStream = null;

	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		if (bioModelInfo.getVersion().getOwner().getName().equals("schaff")){
			return true;
		}
		return false;
	}

	public void visitBioModel(BioModel bioModel, PrintStream logFilePrintStream) {
		try {
			logFilePrintStream.println(bioModel.getVersion().getName()+"  "+bioModel.getVersion().getDate()+"  "+bioModel.getVersion().getVersionKey());
			SimulationContext[] simContexts = bioModel.getSimulationContexts();
			for (int i = 0; i < simContexts.length; i++) {
				SimulationContext simContext = simContexts[i];
				try {
					MathMapping mathMapping = simContext.createNewMathMapping();
					MathDescription generatedMathDesc = mathMapping.getMathDescription();
					mathGenHashStream.println(bioModel.getVersion().getOwner().getName()+",\""+bioModel.getName()+"\","+bioModel.getVersion().getVersionKey()+","+simContext.getVersion().getVersionKey()+","+"good"+","+generateHashCode(generatedMathDesc));
					mathGenHashStream.flush();
				} catch (Exception e) {
					String errorMessage = e.getMessage().replace(',', '_').replace("\n","...");
					if (errorMessage.length()>100){
						errorMessage = errorMessage.substring(0, 100);
					}
					mathGenHashStream.println(bioModel.getVersion().getOwner().getName()+",\""+bioModel.getName()+"\","+bioModel.getVersion().getVersionKey()+","+simContext.getVersion().getVersionKey()+","+errorMessage+","+0);
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
			outputStream = new FileOutputStream(new File("MathGenerationHash.txt"));
			mathGenHashStream = new PrintStream(outputStream);
			MathGenerationHashVisitor visitor = new MathGenerationHashVisitor();
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
