package cbit.vcell.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.util.NullSessionLog;
import org.vcell.util.PropertyLoader;
import org.vcell.util.VCAssert;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.logging.Logging;

import cbit.vcell.modeldb.VCDatabaseScanner;

/**
 * provide implementations for some {@link BioModelVisitor} and / or {@link VCMultiBioVisitor} methods
 */
public abstract class VisitorAdapter {
	
	/**
	 * @return minimum major version of model to process
	 */
	protected abstract int minimumModelVersion( );
	
	/**
	 * type specfic scan
	 * @param scanner not null
	 * @param users not null
	 * @throws Exception
	 */
	protected abstract void scan(VCDatabaseScanner scanner, User[] users) throws Exception;
	
	protected Logger lg = Logger.getLogger(VisitorAdapter.class);
	protected Level level = Level.DEBUG;
	
	/**
	 * optional method to specify different logger than default
	 * @param lg
	 * @param level
	 */
	protected void setupLogging(Logger lg, Level level) {
		VCAssert.assertValid(lg);
		VCAssert.assertValid(level);
		this.lg = lg;
		this.level = level;
	}
	
	/**
	 * initialize
	 * @throws IOException
	 */
	protected void init() throws IOException {
		init(null);
	}
	
	/**
	 * initialize
	 * @param additionalRequired required properties
	 * @throws IOException
	 */
	protected void init(String[] additionalRequired) throws IOException {
		Logging.init();
		String[] required;
		if (additionalRequired == null) {
			required = REQUIRED_PROPERTIES;
		}
		else {
			ArrayList<String> al = new ArrayList<String>( );
			al.addAll(Arrays.asList(REQUIRED_PROPERTIES));
			al.addAll(Arrays.asList(additionalRequired));
			required = al.toArray(new String[al.size( )]);
		}
		PropertyLoader.loadProperties(required);
		initCalled = true;
	}
	
	/**
	 * commences scan for all users
	 * @throws IOException
	 */
	protected void setupScan() throws IOException {
		setupScan(VCDatabaseScanner.ALL_USERS);
	}
	
	/**
	 * commences scan for specified user
	 * @param user
	 * @throws IOException
	 */
	protected void setupScan(String user) throws IOException {
		if (!initCalled) {
			throw new IllegalStateException("Call init( )");
		}
		try{
			VCDatabaseScanner scanner = new VCDatabaseScanner(new NullSessionLog()); 
			User[] users = null; 
			if (!VCDatabaseScanner.ALL_USERS.equals(user)) { 
				User u = scanner.getUser(user);
				if (u == null) {
					throw new IllegalArgumentException("Can't find user " + user + " in database");
				}
				users =  new User[1];
				users[0] = u;
			}
			else {
				users = scanner.getAllUsers();
			}
			scan(scanner,users);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			System.err.flush();
		}
	}


	/**
	 * return true if major version >= {@link #minimumModelVersion()} 
	 * @param bioModelInfo not null
	 */
	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		VCellSoftwareVersion sv = bioModelInfo.getSoftwareVersion();
		final boolean recentEnough = ( sv.getMajorVersion() >= minimumModelVersion() ); 
		if (!recentEnough && lg.isEnabledFor(level)) {
			lg.log(level,"skipping old (v" + sv.getMajorVersion() + ")" + bioModelInfo.toString());
		}
		return recentEnough;
	}
	
	/**
	 * flag to ensure sublcass calls {@link #init()} before {@link #setupScan()} 
	 */
	private boolean initCalled = false;

	private static final String[] REQUIRED_PROPERTIES = {
			PropertyLoader.dbDriverName,
			PropertyLoader.dbPassword,
			PropertyLoader.dbUserid,
			PropertyLoader.dbConnectURL
	};
	
	
	

}
