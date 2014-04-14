package cbit.vcell.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import javax.help.UnsupportedOperationException;

import org.junit.Test;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.User;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.modeldb.VCDatabaseScanner;

public class HybridBioModelVisitor implements VCMultiBioVisitor { 
	/**
	 * user key to use for tests
	 */
	private final static String USER_KEY = "gerardwNot" ;
	/**
	 * output file name
	 */
	private final static String OUTPUT = "regen.txt";
	
	private BioModel currentModel = null;
	
	
	public boolean filterBioModel(BioModelInfo bioModelInfo) {
			return true;
	}

	public void setBioModel(BioModel bioModel, PrintWriter pw) {
		currentModel = bioModel;
		return;
	}
	
	

	@Override
	public Iterator<BioModel> iterator() {
		return new Changer(); 
	}
	
	private class Changer implements Iterator<BioModel> {
		boolean first = true;

		@Override
		public boolean hasNext() {
			return first;
		}

		@Override
		public BioModel next() {
			first = false;
			return currentModel; 
		}

		/**
		 * @throws UnsupportedOperationException
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		
	}

	@SuppressWarnings("unused")
	@Test
	public void tryit( ) throws IOException {
		PropertyLoader.loadProperties();
		//String args[] = {USER_KEY,OUTPUT};
		HybridBioModelVisitor visitor = new HybridBioModelVisitor();
		boolean bAbortOnDataAccessException = false;
		try{
			VCDatabaseScanner scanner = new VCDatabaseScanner(); 
			Writer w = new FileWriter(OUTPUT);
			User[] users = null; 
			if (USER_KEY != VCDatabaseScanner.ALL_USERS) { 
				User u = scanner.getUser(USER_KEY);
				if (u == null) {
					throw new IllegalArgumentException("Can't find user " + USER_KEY + " in database");
				}
				users =  new User[1];
				users[0] = u;
			}
			else {
				users = scanner.getAllUsers();
			}
			scanner.multiScanBioModels(visitor, w, users, bAbortOnDataAccessException);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			System.err.flush();
		}
		
	}

}