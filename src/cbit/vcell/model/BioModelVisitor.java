package cbit.vcell.model;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import com.icl.saxon.functions.UnparsedEntityURI;

import cbit.sql.ConnectionFactory;
import cbit.util.BigString;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.client.test.ClientTester;
import cbit.vcell.clientdb.ClientDocumentManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.test.LumpedKineticsTester;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.modeldb.DatabasePolicySQL;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;
import cbit.vcell.server.SessionLog;
import cbit.vcell.solver.Simulation;

public class BioModelVisitor implements VCDatabaseVisitor {
	
	private Hashtable<KeyValue, BioModelInfo> bioModelInfoHash = new Hashtable<KeyValue, BioModelInfo>();
	private HashSet<KeyValue> unparsedBioModels = new HashSet<KeyValue>();
	
	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		if (true){
			bioModelInfoHash.put(bioModelInfo.getVersion().getVersionKey(), bioModelInfo);
			unparsedBioModels.add(bioModelInfo.getVersion().getVersionKey());
			return true;
		}else{
			return false;
		}
	}

	public void visitBioModel(BioModel bioModel, PrintStream logFilePrintStream) {
		KeyValue currentKey = bioModel.getVersion().getVersionKey();
		logFilePrintStream.append("======SUCEEDED IN READING BIOMODEL with key = "+currentKey.toString()+"\n");
		unparsedBioModels.remove(currentKey);
		
		for (Iterator<KeyValue> iterator = unparsedBioModels.iterator(); iterator.hasNext();) {
			KeyValue key = iterator.next();
			logFilePrintStream.append("======FAILED TO READ BIOMODEL : " + bioModelInfoHash.get(key)+"\n");
			iterator.remove();
		}
		return;
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
		BioModelVisitor visitor = new BioModelVisitor();
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