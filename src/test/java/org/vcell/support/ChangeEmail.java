package org.vcell.support;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;

import cbit.vcell.modeldb.UserDbDriverExtended;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.StdoutSessionLog;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class ChangeEmail extends JFrame {
	private JTextField emailField;
	private JLabel userInfo;
	private JPasswordField pwField;
	private Connection dbConn;
	private UserDbDriverExtended dbDriver;
	private List<UserInfo> userInfos;
	private JTextField newEmailField;
	private JLabel status;
	private List<Component> dynamic;
	private Pattern regex;


	public ChangeEmail() {
		this("");
	}

	public ChangeEmail(String password) {
		super.setPreferredSize(new Dimension(500, 400));
		super.setTitle("Notify / email updater");
		dbDriver = new UserDbDriverExtended(new StdoutSessionLog("local"));
		userInfos = new ArrayList<>( );
		dynamic = new ArrayList<>( );
		regex = Pattern.compile(".*<(.*)>.*");

		Container pane = getContentPane();
		pane.setLayout(new MigLayout("","",""));

		pane.add(new JLabel("Db password: "));
		pwField = new JPasswordField(password,20);
		pane.add(pwField,"wrap");


		pane.add(new JLabel("Email: "));
		emailField = new JTextField();
		pane.add(emailField);
		emailField.setColumns(20);

		JButton btnLookup = new JButton("Lookup");
		pane.add(btnLookup);
		btnLookup.addActionListener( (ae) -> lookup( ));

		JButton btnClear = new JButton("Clear");
		pane.add(btnClear, "wrap");
		btnClear.addActionListener( (ae) -> emailField.setText("") );

		userInfo = new JLabel("user info ...");
		pane.add(userInfo, "span 2, wrap");
		JButton unsButton = new JButton("Unsubscribe");
		pane.add(unsButton, "wrap");
		unsButton.addActionListener( (ae) -> unsubscribe() );
		dynamic.add(unsButton);
		//
		pane.add(new JLabel("New email: "));
		newEmailField = new JTextField();
		pane.add(newEmailField);
		newEmailField.setColumns(20);
		dynamic.add(newEmailField);

		JButton btnUpdate = new JButton("Update");
		pane.add(btnUpdate, "wrap");
		btnUpdate.addActionListener( (ae) -> update( ));
		dynamic.add(btnUpdate);

		status = new JLabel( );
		pane.add(status,"wrap");

		pane.add(new JLabel( )); //spacer
		pane.add(new JLabel( )); //spacer
		JButton exitButton = new JButton("Exit");
		exitButton.addActionListener( (ae) -> System.exit(0) );

		pane.add(exitButton);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setDynamic();
		pack( );

	}

	private void setDynamic( ) {
		boolean active = userInfos.size() > 0;
		for ( Component c: dynamic) {
			c.setEnabled(active);
		}
	}

	private void unsubscribe() {
		try {
			LabelBuilder lb = new LabelBuilder( );
			for (UserInfo ui : userInfos) {
				ui.notify = false;
				dbDriver.updateUserInfo(getConnection(), ui);
				lb.append(ui.userid + " notify off");
			}
			setStatus(true,lb.toString());
		} catch (SQLException e) {
			setStatus(e);
		}
	}

	private void update() {
		try {
			String n = newEmailField.getText();
			LabelBuilder lb = new LabelBuilder( );
			for (UserInfo ui : userInfos) {
				ui.email = n;
				dbDriver.updateUserInfo(getConnection(), ui);
				lb.append("set " + ui.userid + " email to " + n);
			}
			setStatus(true,lb.toString());
		} catch (SQLException e) {
			setStatus(e);
		}
	}

	private void lookup( ) {
		try {
			newEmailField.setText("");
			String email = emailField.getText().toLowerCase();
			Matcher m = regex.matcher(email);
			if (m.matches()) {
				email = m.group(1);
			}
			List<User> users = dbDriver.getUserFromEmail(getConnection( ), email);
			userInfos.clear();
			if (users.size() > 0 ) {
				LabelBuilder lb = new LabelBuilder();
				for (User u : users) {
					UserInfo ui = dbDriver.getUserInfo(dbConn, u.getID());
					userInfos.add(ui);
					lb.append(ui.toString());
				}
				userInfo.setText(lb.toString());
				setStatus(true,"found " + users.size() + " users for " + email);
			}
			else {
				setStatus(true,"email " + email + " not found");
			}

		} catch (SQLException | DataAccessException e) {
			setStatus(e);
		}
		setDynamic();
	}

	private void setStatus(boolean good, String msg) {
		status.setText(msg);
		status.setForeground( good ? Color.BLACK : Color.RED);
	}
	private void setStatus(Exception e) {
		setStatus(false,e.getMessage());

	}
	/**
	 * opens the database connection
	 * @return database connection
	 * @throws SQLException
	 */
	private Connection getConnection( ) throws SQLException {
		if (dbConn == null) {
			String dbDriverName = PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName);
			char[] pw = pwField.getPassword();
			ConnectionFactory connectionFactory = DatabaseService.getInstance().createConnectionFactory(
				new StdoutSessionLog("test"),
				dbDriverName,
				EmailList.JDBC_URL,
				EmailList.USER_ID,
				new String(pw));
		
			dbConn = connectionFactory.getConnection(new Object());
		}
		return dbConn;
	}


	public static void main(String[] args) {
		String pw = args.length > 0 ? args[0] : "";
		ChangeEmail ce = new ChangeEmail(pw);
		ce.setVisible(true);
	}

	private static class LabelBuilder {
		StringBuilder sb;
		LabelBuilder( ) {
			sb = new StringBuilder("<html>");
		}
		void append(String s) {
			sb.append(s);
			sb.append("<br>");
		}

		public String toString( ) {
			return sb.toString();
		}
	}

}
