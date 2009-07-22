package cbit.vcell.server;

import java.awt.Component;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.LoginDialog;
import cbit.vcell.desktop.RegistrationPanel;
import cbit.vcell.modeldb.LocalAdminDbServer;

public class UserRegistrationOP implements Serializable{
	
//	public static final String USERREGOP_NEWREGISTER = "USERREGOP_NEWREGISTER";
	public static final String USERREGOP_UPDATE = "USERREGOP_UPDATE";
	public static final String USERREGOP_GETINFO = "USERREGOP_GETINFO";
	public static final String USERREGOP_LOSTPASSWORD = "USERREGOP_LOSTPASSWORD";
	
	private UserInfo userInfo;
	private String operationType;
	private String userid;
	private String password;
	private KeyValue userKey;
	
	private static RegistrationPanel registrationPanel = null;
	
	public static UserRegistrationOP createGetUserInfoOP(User user){
		UserRegistrationOP userRegistrationOP = new UserRegistrationOP();
		userRegistrationOP.operationType = USERREGOP_GETINFO;
		userRegistrationOP.userid  = user.getName();
		userRegistrationOP.userKey = user.getID();
		return userRegistrationOP;
	}
	public static UserRegistrationOP createGetUserInfoOP(String userid){
		UserRegistrationOP userRegistrationOP = new UserRegistrationOP();
		userRegistrationOP.operationType = USERREGOP_LOSTPASSWORD;
		userRegistrationOP.userid  = userid;
		return userRegistrationOP;
	}

//	public static UserRegistrationOP createGetUserInfoOP(String userID,String password){
//		UserRegistrationOP userRegistrationOP = new UserRegistrationOP();
//		userRegistrationOP.operationType = USERREGOP_GETINFO;
//		userRegistrationOP.userid  = userID;
//		userRegistrationOP.password = password;
//		return userRegistrationOP;		
//	}
//	public static UserRegistrationOP createNewRegisterOP(UserInfo userInfo){
//		UserRegistrationOP userRegistrationOP = new UserRegistrationOP();
//		userRegistrationOP.operationType = USERREGOP_NEWREGISTER;
//		userRegistrationOP.userInfo = userInfo;
//		return userRegistrationOP;		
//	}
	public static UserRegistrationOP createUpdateRegisterOP(UserInfo userInfo){
		UserRegistrationOP userRegistrationOP = new UserRegistrationOP();
		userRegistrationOP.operationType = USERREGOP_UPDATE;
		userRegistrationOP.userInfo = userInfo;
		userRegistrationOP.userid  = userInfo.userid;
		userRegistrationOP.userKey = userInfo.id;
		return userRegistrationOP;		
	}

	public static void registrationOperationGUI(final RequestManager requestManager, final DocumentWindowManager currWindowManager, final ClientServerInfo currentClientServerInfo, final String userAction, final ClientServerManager clientServerManager) throws Exception{
		if(!(userAction.equals(LoginDialog.USERACTION_REGISTER) ||
				userAction.equals(LoginDialog.USERACTION_EDITINFO) ||
				userAction.equals(LoginDialog.USERACTION_LOSTPASSWORD))){
			throw new IllegalArgumentException(UserRegistrationOP.class.getName()+".registrationOperationGUI:  Only New registration, Edit UserInfo or Lost Password allowed.");
		}
		if((userAction.equals(LoginDialog.USERACTION_REGISTER) || userAction.equals(LoginDialog.USERACTION_LOSTPASSWORD)) && clientServerManager != null){
			throw new IllegalArgumentException(UserRegistrationOP.class.getName()+".registrationOperationGUI:  Register New User Info requires clientServerManager null.");			
		}
		if(userAction.equals(LoginDialog.USERACTION_EDITINFO) && clientServerManager == null){
			throw new IllegalArgumentException(UserRegistrationOP.class.getName()+".registrationOperationGUI:  Edit User Info requires clientServerManager not null.");			
		}

		class RegistrationProvider {
			private LocalAdminDbServer localAdminDbServer;
			private VCellBootstrap vcellBootstrap;
			private ClientServerManager clientServerManager;
			public RegistrationProvider(ClientServerManager clientServerManager){
				this.clientServerManager = clientServerManager;
			}
			public RegistrationProvider(LocalAdminDbServer localAdminDbServer){
				this.localAdminDbServer = localAdminDbServer;
			}
			public RegistrationProvider(VCellBootstrap vcellBootstrap){
				this.vcellBootstrap = vcellBootstrap;
			}
			public UserInfo insertUserInfo(UserInfo newUserInfo,boolean bUpdate) throws RemoteException,DataAccessException{
				if(localAdminDbServer != null){
					if(bUpdate){
						throw new IllegalArgumentException("UPDATE User Info: Must use ClientserverManager NOT LocalAdminDBServer");
					}else{
						return localAdminDbServer.insertUserInfo(newUserInfo);
					}
				}else if(vcellBootstrap != null){
					if(bUpdate){
						throw new IllegalArgumentException("UPDATE User Info: Must use ClientserverManager NOT VCellBootstrap");
					}else{
						return vcellBootstrap.insertUserInfo(newUserInfo);
					}
				}else{
					if(bUpdate){
						newUserInfo.id = this.clientServerManager.getUser().getID();
						return 
							clientServerManager.getUserMetaDbServer().userRegistrationOP(
								UserRegistrationOP.createUpdateRegisterOP(newUserInfo)).getUserInfo();								
					}else{
						throw new IllegalArgumentException("INSERT User Info: Not allowed to use ClientserverManager");
					}
				}
			}
			public UserInfo getUserInfo(KeyValue userKey) throws DataAccessException,RemoteException{
				if(localAdminDbServer != null){
					return localAdminDbServer.getUserInfo(userKey);
				}else if(vcellBootstrap != null){
					return vcellBootstrap.getUserInfo(userKey);
				}else{
					return 
						clientServerManager.getUserMetaDbServer().userRegistrationOP(
							UserRegistrationOP.createGetUserInfoOP(clientServerManager.getUser())).getUserInfo();
				}
			}					
			public void sendLostPassword(String userid) throws DataAccessException,RemoteException{
				if(localAdminDbServer != null){
					localAdminDbServer.sendLostPassword(userid);
				}else if(vcellBootstrap != null){
					vcellBootstrap.sendLostPassword(userid);
				}else{
					clientServerManager.getUserMetaDbServer().userRegistrationOP(
						UserRegistrationOP.createGetUserInfoOP(clientServerManager.getUser())).getUserInfo();
				}
			}
		}
		RegistrationProvider registrationProvider = null;
		if(clientServerManager != null){
			registrationProvider = new RegistrationProvider(clientServerManager);
		} else {
			if (currentClientServerInfo.getServerType() == ClientServerInfo.SERVER_LOCAL) {
				PropertyLoader.loadProperties();
				SessionLog log = new StdoutSessionLog("Local");
				ConnectionFactory conFactory = new OraclePoolingConnectionFactory(log);
				KeyFactory keyFactory = new OracleKeyFactory();
				registrationProvider = new RegistrationProvider(new LocalAdminDbServer(conFactory, keyFactory, log));
			} else {
				String[] hosts = currentClientServerInfo.getHosts();
				VCellBootstrap vcellBootstrap = null;
				for (int i = 0; i < hosts.length; i ++) {
					try {
						vcellBootstrap = (VCellBootstrap) java.rmi.Naming.lookup("//" + hosts[i]	+ "/" + RMIVCellConnectionFactory.SERVICE_NAME);
						vcellBootstrap.getVCellSoftwareVersion(); // test connection
						break;
					} catch (Exception ex) {
						if (i == hosts.length - 1) {
							throw ex;
						}
					}
				}
				registrationProvider = new RegistrationProvider(vcellBootstrap);
			}
		}
		if(userAction.equals(LoginDialog.USERACTION_LOSTPASSWORD)){
			if(currentClientServerInfo.getUsername() == null || currentClientServerInfo.getUsername().length() == 0){
				throw new IllegalArgumentException("Lost Password requires a VCell User Name.");
			}
			String result = PopupGenerator.showWarningDialog((Component)null, null,
					new UserMessage(
						"Sending Password via email for user '"+currentClientServerInfo.getUsername()+
						"'\nusing currently registered email address.",
						new String[] {"OK","Cancel"},"OK"),
					null);
			if(!result.equals("OK")){
				throw UserCancelException.CANCEL_GENERIC;
			}
			registrationProvider.sendLostPassword(currentClientServerInfo.getUsername());
			return;
		}
		
		final RegistrationProvider finalRegistrationProvider = registrationProvider;
		
		AsynchClientTask gatherInfoTask = new AsynchClientTask("gathering user info for updating", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				if(userAction.equals(LoginDialog.USERACTION_EDITINFO)){
					UserInfo originalUserInfoHolder = finalRegistrationProvider.getUserInfo(clientServerManager.getUser().getID());
					hashTable.put("originalUserInfoHolder", originalUserInfoHolder);
				}
			}			
		};
		
		AsynchClientTask showPanelTask = new AsynchClientTask("please fill the user registration form", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				if (registrationPanel == null) {
					registrationPanel = new RegistrationPanel();
				} else {
					if (currentClientServerInfo.getUsername() != null) { // another user already connected
						registrationPanel.reset();
					}
				}
				UserInfo originalUserInfoHolder = (UserInfo)hashTable.get("originalUserInfoHolder");;
				if(userAction.equals(LoginDialog.USERACTION_EDITINFO) && originalUserInfoHolder != null) {
					registrationPanel.setUserInfo(originalUserInfoHolder,true);					
				}
				do {
					int result = DialogUtils.showComponentOKCancelDialog(null, registrationPanel,
								(userAction.equals(LoginDialog.USERACTION_REGISTER)?"Create New User Registration":"Update Registration Information ("+clientServerManager.getUser().getName()+")"));
					if (result != JOptionPane.OK_OPTION) {
						throw UserCancelException.CANCEL_GENERIC;
					}
					UserInfo newUserInfo = registrationPanel.getUserInfo();
			
					try {
						if(!checkUserInfo(originalUserInfoHolder,newUserInfo)){
							PopupGenerator.showInfoDialog(currWindowManager, "No registration information has changed.");
							continue;
						}
					} catch (Exception ex) {
						PopupGenerator.showErrorDialog(currWindowManager, ex.getMessage());
						continue;
					}
					hashTable.put("newUserInfo", newUserInfo);
					break;
				} while (true);
			}
		};
		
		AsynchClientTask updateDbTask = new AsynchClientTask(userAction.equals(LoginDialog.USERACTION_REGISTER)?"registering new user":"updating user info", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				UserInfo newUserInfo = (UserInfo)hashTable.get("newUserInfo");
				try {					
					UserInfo registeredUserInfo = finalRegistrationProvider.insertUserInfo(newUserInfo,(userAction.equals(LoginDialog.USERACTION_EDITINFO)?true:false));
					hashTable.put("registeredUserInfo", registeredUserInfo);
				}catch (Exception e) {
					e.printStackTrace();
					throw new Exception("Error " 
							+ (userAction.equals(LoginDialog.USERACTION_REGISTER)?"registering new user" :"updating user info ") +  " ("+newUserInfo.userid+"), "
							+ e.getMessage());
				}
			}			
		};
		
		AsynchClientTask connectTask = new AsynchClientTask("user logging in", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				UserInfo registeredUserInfo = (UserInfo)hashTable.get("registeredUserInfo");
				try {					
					if (userAction.equals(LoginDialog.USERACTION_REGISTER)) {
						try{
							ClientServerInfo newClientServerInfo = VCellClient.createClientServerInfo(currentClientServerInfo, registeredUserInfo.userid, registeredUserInfo.password);
							requestManager.connectToServer(currWindowManager, newClientServerInfo);
						}finally{
							ConnectionStatus connectionStatus = requestManager.getConnectionStatus();
							if(connectionStatus.getStatus() != ConnectionStatus.CONNECTED){
								PopupGenerator.showErrorDialog((Component)null, "Automatic login of New user '"+registeredUserInfo.userid+"' failed.\n"+
									"Restart VCell and login as '"+registeredUserInfo.userid+"' to use new VCell account."
								);
						}
					}
				}					
				}catch (Exception e) {
					e.printStackTrace();
					throw new Exception("Error logging in user " + " ("+registeredUserInfo.userid+"), "	+ e.getMessage());
				}
			}			
		};
		
		ClientTaskDispatcher.dispatch(null, new Hashtable<String, Object>(), new AsynchClientTask[] {gatherInfoTask, showPanelTask, updateDbTask, connectTask}, false);
	}

	public static boolean hasIllegalCharacters(String apo){
		if((apo.indexOf('\'') != -1) || (apo.indexOf('<') != -1) || (apo.indexOf('>') != -1) || (apo.indexOf('&') != -1) || (apo.indexOf('\"') != -1)){
			return true;
		}
		return false;
	}
	private static boolean checkUserInfo(UserInfo origUserInfo,UserInfo newUserInfo) throws Exception{
		TokenMangler.checkLoginID(newUserInfo.userid);
		
		String emptyMessge = " can not be empty";	
		if(newUserInfo.userid == null || newUserInfo.userid.length() == 0){throw new Exception("Registration Info: userid" + emptyMessge);}
		if(newUserInfo.password == null || newUserInfo.password.length() == 0 || !newUserInfo.password.equals(newUserInfo.password2)){throw new Exception("Registration Info: both passwords must be the same and" + emptyMessge);}
		if(newUserInfo.email == null || newUserInfo.email.length() == 0 || newUserInfo.email.indexOf("@") < 0){throw new Exception("please type in a valid email address.");}
		if(newUserInfo.firstName == null || newUserInfo.firstName.length() == 0){throw new Exception("Registration Info: firstName" + emptyMessge);}
		if(newUserInfo.lastName == null || newUserInfo.lastName.length() == 0){throw new Exception("Registration Info: lastName" + emptyMessge);}
		if(newUserInfo.title == null || newUserInfo.title.length() == 0){throw new Exception("Registration Info: title" + emptyMessge);}
		if(newUserInfo.company == null || newUserInfo.company.length() == 0){throw new Exception("Registration Info: organization" + emptyMessge);}
		if(newUserInfo.address1 == null || newUserInfo.address1.length() == 0){throw new Exception("Registration Info: address1" + emptyMessge);}
//		if(newUserInfo.address2 == null || newUserInfo.address2.length() == 0){throw new Exception("Registration Info: address2 length must be greater than 0");}
		if(newUserInfo.city == null || newUserInfo.city.length() == 0){throw new Exception("Registration Info: city" + emptyMessge);}
		if(newUserInfo.state == null || newUserInfo.state.length() == 0){throw new Exception("Registration Info: state" + emptyMessge);}
		if(newUserInfo.country == null || newUserInfo.country.length() == 0){throw new Exception("Registration Info: country" + emptyMessge);}
		if(newUserInfo.zip == null || newUserInfo.zip.length() == 0){throw new Exception("Registration Info: zip" + emptyMessge);}
		
		String hasIllegalMessage = " has illegal character '<>&\"";
		//Check Illegal characters
		if(hasIllegalCharacters(newUserInfo.userid)){throw new Exception("Registration Info: userid" + hasIllegalMessage);}
		if(hasIllegalCharacters(newUserInfo.password)){throw new Exception("Registration Info: password" + hasIllegalMessage);}
		if(hasIllegalCharacters(newUserInfo.email)){throw new Exception("Registration Info: email" + hasIllegalMessage);}
		if(hasIllegalCharacters(newUserInfo.firstName)){throw new Exception("Registration Info: firstName" + hasIllegalMessage);}
		if(hasIllegalCharacters(newUserInfo.lastName)){throw new Exception("Registration Info: lastName" + hasIllegalMessage);}
		if(hasIllegalCharacters(newUserInfo.title)){throw new Exception("Registration Info: title" + hasIllegalMessage);}
		if(hasIllegalCharacters(newUserInfo.company)){throw new Exception("Registration Info: organization" + hasIllegalMessage);}
		if(hasIllegalCharacters(newUserInfo.address1)){throw new Exception("Registration Info: address1" + hasIllegalMessage);}
		if(newUserInfo.address2 != null && hasIllegalCharacters(newUserInfo.address2)){throw new Exception("Registration Info: address2" + hasIllegalMessage);}
		if(hasIllegalCharacters(newUserInfo.city)){throw new Exception("Registration Info: city" + hasIllegalMessage);}
		if(hasIllegalCharacters(newUserInfo.state)){throw new Exception("Registration Info: state" + hasIllegalMessage);}
		if(hasIllegalCharacters(newUserInfo.country)){throw new Exception("Registration Info: country" + hasIllegalMessage);}
		if(hasIllegalCharacters(newUserInfo.zip)){throw new Exception("Registration Info: zip" + hasIllegalMessage);}

		if(origUserInfo != null){
			String[] columnNames = new String[] {"Field","Original","New Value"};
			Vector<String[]> tableRow = new Vector<String[]>();
			if(!newUserInfo.userid.equals(origUserInfo.userid)){tableRow.add(new String[] {"userid",origUserInfo.userid,newUserInfo.userid});}
			if(!newUserInfo.password.equals(origUserInfo.password)){tableRow.add(new String[] {"password","---","changed"});}//{origVal.add("password");newVal.add("password changed");}
			if(!newUserInfo.email.equals(origUserInfo.email)){tableRow.add(new String[] {"email",origUserInfo.email,newUserInfo.email});}
			if(!newUserInfo.firstName.equals(origUserInfo.firstName)){tableRow.add(new String[] {"firstName",origUserInfo.firstName,newUserInfo.firstName});}
			if(!newUserInfo.lastName.equals(origUserInfo.lastName)){tableRow.add(new String[] {"lastName",origUserInfo.lastName,newUserInfo.lastName});}
			if(!newUserInfo.title.equals(origUserInfo.title)){tableRow.add(new String[] {"title",origUserInfo.title,newUserInfo.title});}
			if(!newUserInfo.company.equals(origUserInfo.company)){tableRow.add(new String[] {"company",origUserInfo.company,newUserInfo.company});}
			if(!newUserInfo.address1.equals(origUserInfo.address1)){tableRow.add(new String[] {"address1",origUserInfo.address1,newUserInfo.address1});}
			if(!Compare.isEqualOrNull(newUserInfo.address2, origUserInfo.address2)){tableRow.add(new String[] {"address2",(origUserInfo.address2 == null?"":"'"+origUserInfo.address2+"'"),(newUserInfo.address2 == null?"":"'"+newUserInfo.address2+"'")});}
			if(!newUserInfo.city.equals(origUserInfo.city)){tableRow.add(new String[] {"city",origUserInfo.city,newUserInfo.city});}
			if(!newUserInfo.state.equals(origUserInfo.state)){tableRow.add(new String[] {"state",origUserInfo.state,newUserInfo.state});}
			if(!newUserInfo.country.equals(origUserInfo.country)){tableRow.add(new String[] {"country",origUserInfo.country,newUserInfo.country});}
			if(!newUserInfo.zip.equals(origUserInfo.zip)){tableRow.add(new String[] {"postal code",origUserInfo.zip,newUserInfo.zip});}
			if(!(newUserInfo.notify == origUserInfo.notify)){tableRow.add(new String[] {"notify",origUserInfo.notify+"",newUserInfo.notify+""});}
	
			if(tableRow.size() > 0){
				String[][] tableData = new String[tableRow.size()][];
				tableRow.copyInto(tableData);
				DialogUtils.showComponentOKCancelTableList(
					null, "Confirm Registration Info Changes",
					columnNames, tableData,null);
				return true;
			}
			return false;
		}else{
			return true;
		}
	}
	
	public UserInfo getUserInfo(){
		return userInfo;
	}
	
	public String getOperationType(){
		return operationType;
	}
	public String getUserid(){
		return userid;
	}
	public String getPassword(){
		return password;
	}
	public KeyValue getUserKey(){
		return userKey;
	}
}
