package org.vcell.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.StdoutSessionLog;

/**
 * read database to get user emails (with notify="on"), dump to file
 * @author GWeatherby
 */
public class EmailList {
	private static final String NOTIFY_QUERY = "Select email from vcell.vc_userinfo where notify='on' "
			+ "and regexp_like(email,'[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$')";
	private static final String BOUNCE_QUERY = "Select email from vcell.mailbounce";
	private static final String DEFAULT_FILE = "emails.csv";

	static final String JDBC_URL = "jdbc:oracle:thin:@vcell-db.cam.uchc.edu:1521/vcelldborcl.cam.uchc.edu";
	static final String USER_ID = "vcell";
	private final String password;
	private final String filename;

	public static void main(String[] args) {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param password database (for {@link #USER_ID}
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
			List<Address> addrs;
			try (Connection conn = getConnection()) {
				Set<String> bounces = getBounces(conn);

				addrs = getAddresses(conn, bounces);
			} //closes connection
//			dump(addrs);
			writeToFile(addrs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * dump to standard out
	 * @param addrs
	 */
	@SuppressWarnings("unused")
	private void dump(List<Address> addrs) {
		addrs.forEach( address -> System.out.println(address) );
	}

	private void writeToFile(List<Address> addrs) throws FileNotFoundException {
		try (PrintWriter pw = new PrintWriter(new File(filename))) {
			addrs.forEach( address -> pw.println(address) );
		}
	}

	private Set<String> getBounces(Connection conn ) throws SQLException {
		Set<String> bounces = new HashSet<>();
		try (Statement stmt = conn.createStatement()) {
			ResultSet rs = stmt.executeQuery(BOUNCE_QUERY);
			while (rs.next( )) {
				String b= rs.getString(1);
				bounces.add(b);
			}
		}
		return bounces;

	}
	/**
	 * @param conn database connection
	 * @param bounces
	 * @return emails from database
	 * @throws SQLException
	 */
	private List<Address> getAddresses(Connection conn, Set<String> bounces ) throws SQLException {
		ArrayList<Address> addresses = new ArrayList<>();
		try (Statement stmt = conn.createStatement()) {
			ResultSet rs = stmt.executeQuery(NOTIFY_QUERY);
			while (rs.next( )) {
				String email = rs.getString(1);
				boolean b = bounces.contains(email);
				addresses.add(new Address(email,b));
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
		String dbDriverName = PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName);
		ConnectionFactory connectionFactory = DatabaseService.getInstance().createConnectionFactory(
				new StdoutSessionLog("test"),
			dbDriverName,
			EmailList.JDBC_URL,
			USER_ID,
			password);
		Connection conn = connectionFactory.getConnection(new Object());
		return conn;
	}

	private static class Address {
		final String email;
		final boolean bounce;
		Address(String email, boolean bounce) {
			super();
			this.email = email;
			this.bounce = bounce;
		}

		@Override
		public String toString( ) {
			return email + ',' + bounce;
		}

	}
}
