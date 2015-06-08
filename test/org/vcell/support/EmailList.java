package org.vcell.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.pool.OracleDataSource;

/**
 * read database to get user emails (with notify="on"), dump to file 
 * @author GWeatherby
 */
public class EmailList {
	private static final String QUERY = "Select email from vcell.vc_userinfo where notify='on'";
	private static final String DEFAULT_FILE = "emails.csv";
	
	static String jdbcUrl = "jdbc:oracle:thin:@dbs6.cam.uchc.edu:1521:orcl";
	static String userId = "vcell";
	private final String password;
	private final String filename;

	public static void main(String[] args) {
		EmailList eml; 
		switch (args.length) {
		case 0:
			System.err.println("Usage: [database password] <output filename>");
			return;
		case 1:
			eml = new EmailList(args[0], DEFAULT_FILE);
			break;
		default:
			eml = new EmailList(args[0], args[1]); 
			break;
		}
		
		eml.exportList( );
	}
	
	/**
	 * 
	 * @param password database (for {@link #userId}
	 * @param filename to write to
	 */
	EmailList(String password, String filename) {
		super();
		this.password = password;
		this.filename = filename;
	}

	/**
	 * does the work
	 */
	private void exportList( ) {
		try {
			List<String> addrs; 
			try (Connection conn = getConnection()) {
				addrs = getAddresses(conn);
			} //closes connection
			//dump(addrs);
			writeToFile(addrs);
		} catch (SQLException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * dump to standard out
	 * @param addrs
	 */
	@SuppressWarnings("unused")
	private void dump(List<String> addrs) {
		addrs.forEach( address -> System.out.println(address) );
	}
	
	private void writeToFile(List<String> addrs) throws FileNotFoundException {
		try (PrintWriter pw = new PrintWriter(new File(filename))) {
			addrs.forEach( address -> pw.println(address) );
		}
	}

	/**
	 * @param conn database connection
	 * @return emails from database
	 * @throws SQLException
	 */
	private List<String> getAddresses(Connection conn ) throws SQLException {
		ArrayList<String> addresses = new ArrayList<>();
		try (Statement stmt = conn.createStatement()) {
			ResultSet rs = stmt.executeQuery(QUERY);
			while (rs.next( )) {
				String email = rs.getString(1);
				addresses.add(email);
			}
			return addresses;
		}
	}

	/**
	 * opens the database connection
	 * @return database connection
	 * @throws SQLException
	 */
	private Connection getConnection( ) throws SQLException {
		OracleDataSource ds = new OracleDataSource();
		ds.setURL(jdbcUrl);
		Connection conn = ds.getConnection(userId,password);
		return conn;
	}
	
	

	
}
