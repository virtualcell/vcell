package cbit.vcell.model;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.junit.Test;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;

public class BioModelTestReader implements VCDatabaseVisitor {
	/**
	 * user key to use for tests
	 */
	private final static String USER_KEY = "gerardw" ;
	/**
	 * output file name
	 */
	private final static String OUTPUT = "regen.txt";
	
	private Hashtable<KeyValue, BioModelInfo> bioModelInfoHash = new Hashtable<KeyValue, BioModelInfo>();
	private HashSet<KeyValue> unparsedBioModels = new HashSet<KeyValue>();
	
	public boolean filterBioModel(BioModelInfo bioModelInfo) {
			bioModelInfoHash.put(bioModelInfo.getVersion().getVersionKey(), bioModelInfo);
			unparsedBioModels.add(bioModelInfo.getVersion().getVersionKey());
			return true;
	}

	public void visitBioModel(BioModel bioModel, PrintStream logFilePrintStream) {
		KeyValue currentKey = bioModel.getVersion().getVersionKey();
		BioModelInfo bmInfo = bioModelInfoHash.get(currentKey);
		logFilePrintStream.append(" == SUCEEDED IN READING BIOMODEL " + bmInfo.getVersion().getName() + "; key : " + currentKey.toString() + "\n");
		unparsedBioModels.remove(currentKey);
		
		for (Iterator<KeyValue> iterator = unparsedBioModels.iterator(); iterator.hasNext();) {
			KeyValue key = iterator.next();
			bmInfo = bioModelInfoHash.get(key);
			logFilePrintStream.append(" == FAILED TO READ BIOMODEL : " + bmInfo.getVersion().getName() + "; key : " + key.toString() +"\n");
			iterator.remove();
		}
		return;
	}
	
	public HashSet<KeyValue> getUnParsedBioModelsHash() {
		return unparsedBioModels;
	}

	public boolean filterGeometry(GeometryInfo geometryInfo) {
		return false;
	}

	public boolean filterMathModel(MathModelInfo mathModelInfo) {
		return false;
	}

	public void visitGeometry(Geometry geometry, PrintStream logFilePrintStream) {
	}

	public void visitMathModel(MathModel mathModel,	PrintStream logFilePrintStream) {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BioModelTestReader visitor = new BioModelTestReader();
		boolean bAbortOnDataAccessException = false;
		try{
			VCDatabaseScanner.scanBioModels(args, visitor, bAbortOnDataAccessException);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			System.err.println("out of "+visitor.bioModelInfoHash.size()+" bioModels, "+visitor.unparsedBioModels.size()+" could not be read");
			System.err.flush();
		}
	}
	
	@Test
	public void tryit( ) throws IOException {
		PropertyLoader.loadProperties();
		String args[] = {USER_KEY,OUTPUT};
		BioModelTestReader visitor = new BioModelTestReader();
		boolean bAbortOnDataAccessException = false;
		try{
			VCDatabaseScanner.scanBioModels(args, visitor, bAbortOnDataAccessException);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			System.err.println("out of "+visitor.bioModelInfoHash.size()+" bioModels, "+visitor.unparsedBioModels.size()+" could not be read");
			System.err.flush();
		}
		
	}

}