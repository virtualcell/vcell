package cbit.vcell.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import cbit.gui.DialogUtils;
import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.KeyValue;
import cbit.sql.UserInfo;
import cbit.util.AsynchProgressPopup;
import cbit.util.Compare;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.task.UserCancelException;
import cbit.vcell.desktop.LoginDialog;
import cbit.vcell.desktop.RegistrationPanel;
import cbit.vcell.modeldb.LocalAdminDbServer;

public class UserRegistrationOP implements Serializable{
	
//	public static final String USERREGOP_NEWREGISTER = "USERREGOP_NEWREGISTER";
	public static final String USERREGOP_UPDATE = "USERREGOP_UPDATE";
	public static final String USERREGOP_GETINFO = "USERREGOP_GETINFO";
	
	private UserInfo userInfo;
	private String operationType;
	private String userid;
	private String password;
	private KeyValue userKey;
	
	public static UserRegistrationOP createGetUserInfoOP(User user){
		UserRegistrationOP userRegistrationOP = new UserRegistrationOP();
		userRegistrationOP.operationType = USERREGOP_GETINFO;
		userRegistrationOP.userid  = user.getName();
		userRegistrationOP.userKey = user.getID();
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

	public static UserInfo registrationOperationGUI(ClientServerInfo currentClientServerInfo,String userAction,ClientServerManager clientServerManager) throws UserCancelException,Exception{
		if(!(userAction.equals(LoginDialog.USERACTION_REGISTER) || userAction.equals(LoginDialog.USERACTION_EDITINFO))){
			throw new IllegalArgumentException(UserRegistrationOP.class.getName()+".registrationOperationGUI:  Only New registration and Edit UserInfo allowed.");
		}
		if(userAction.equals(LoginDialog.USERACTION_REGISTER) && clientServerManager != null){
			throw new IllegalArgumentException(UserRegistrationOP.class.getName()+".registrationOperationGUI:  Edit User Info requires clientServerManager null.");			
		}
		if(userAction.equals(LoginDialog.USERACTION_EDITINFO) && clientServerManager == null){
			throw new IllegalArgumentException(UserRegistrationOP.class.getName()+".registrationOperationGUI:  Edit User Info requires clientServerManager not null.");			
		}
//		new Thread(new Runnable() {
//			public void run() {
//				try {
					PropertyLoader.loadProperties();
//				} catch (Exception e) {
//					PopupGenerator.showErrorDialog("New Registration: Couldn't load VCell properties.\n"+ e.getMessage());
//					return;
//				}
				RegistrationPanel registrationPanel = new RegistrationPanel();
//				LocalAdminDbServer adminDbServer = null;
//				VCellBootstrap vcellBootstrap = null;
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
//								return localAdminDbServer.updateUserInfo(newUserInfo);
							}else{
								return localAdminDbServer.insertUserInfo(newUserInfo);
							}
						}else if(vcellBootstrap != null){
							if(bUpdate){
								throw new IllegalArgumentException("UPDATE User Info: Must use ClientserverManager NOT VCellBootstrap");
//								return vcellBootstrap.updateUserInfo(newUserInfo);
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
//								return 
//									clientServerManager.getUserMetaDbServer().userRegistrationOP(
//										UserRegistrationOP.createNewRegisterOP(newUserInfo)).getUserInfo();
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
				}
				RegistrationProvider registrationProvider = null;
				if(clientServerManager != null){
					registrationProvider = new RegistrationProvider(clientServerManager);
				}
//				do {
						if (registrationProvider == null) {
							if (currentClientServerInfo.getServerType() == ClientServerInfo.SERVER_LOCAL) {
								SessionLog log = new cbit.vcell.server.StdoutSessionLog("Local");
								ConnectionFactory conFactory = new cbit.sql.OraclePoolingConnectionFactory(log);
								KeyFactory keyFactory = new cbit.sql.OracleKeyFactory();
								registrationProvider = new RegistrationProvider(
										new cbit.vcell.modeldb.LocalAdminDbServer(conFactory, keyFactory, log));
//							registeredUserInfo = adminDbServer.insertUserInfo(newUserInfo);
//							registeredUserInfo = adminDbServer.getUserInfo(new KeyValue("227"));
//							registrationPanel.setUserInfo(registeredUserInfo);
//							continue;
							} else {
								registrationProvider = new RegistrationProvider(
									(cbit.vcell.server.VCellBootstrap) java.rmi.Naming.lookup(
											"//"
											+ currentClientServerInfo.getHost()
											+ "/"
											+ RMIVCellConnectionFactory.SERVICE_NAME)
								);
				
//								registeredUserInfo = vcellBootstrap.insertUserInfo(newUserInfo);
	
							}
						}
				AsynchProgressPopup pp = null;
				pp = new AsynchProgressPopup(null/*loginDialog*/,"Registration...",null,false,false);
				UserInfo originalUserInfo = null;
				if(userAction.equals(LoginDialog.USERACTION_EDITINFO)){
					pp.setMessage("Gathering '"+clientServerManager.getUser().getName()+"' information...");
					try{
						pp.start();
						originalUserInfo = registrationProvider.getUserInfo(clientServerManager.getUser().getID());
						registrationPanel.setUserInfo(originalUserInfo,true);
					}catch(Exception e){
						throw e;
					}finally{
						pp.stop();
					}
				}
				do{
					int result =
						DialogUtils.showComponentOKCancelDialog(null/*loginDialog*/, registrationPanel,
								(userAction.equals(LoginDialog.USERACTION_REGISTER)?"Create New User Registration":"Update Registration Information ("+clientServerManager.getUser().getName()+")"));
					if (result != JOptionPane.OK_OPTION) {
						throw UserCancelException.CANCEL_GENERIC;
					}
					try {
						UserInfo newUserInfo = registrationPanel.getUserInfo();
						try {
							if(!checkUserInfo(originalUserInfo,newUserInfo)){
								PopupGenerator.showInfoDialog("No registration information has changed.");
								continue;
							}
						} catch (UserCancelException e) {
							continue;
						}
						pp.start();
						UserInfo registeredUserInfo = null;
//						if(userAction.equals(LoginDialog.USERACTION_REGISTER)){
							registeredUserInfo = registrationProvider.insertUserInfo(newUserInfo,(userAction.equals(LoginDialog.USERACTION_EDITINFO)?true:false));
//						}else if(userAction.equals(LoginDialog.USERACTION_EDITINFO)){
//							
//						}
						return registeredUserInfo;
					}catch (Exception e) {
						if(pp != null){
							pp.stop();
						}
						e.printStackTrace();
						PopupGenerator.showErrorDialog("Error "+
								(userAction.equals(LoginDialog.USERACTION_REGISTER)?"inserting New User Registration":"Updating Registration Information ("+clientServerManager.getUser().getName()+")")
								+"\n"+ e.getMessage());
					}finally{
						if(pp != null){
							pp.stop();
						}
					}
				} while (true);
//			}}).start();

	}

	public static boolean hasIllegalCharacters(String apo){
		if((apo.indexOf('\'') != -1) || (apo.indexOf('<') != -1) || (apo.indexOf('>') != -1) || (apo.indexOf('&') != -1) || (apo.indexOf('\"') != -1)){
			return true;
		}
		return false;
	}
	private static boolean checkUserInfo(UserInfo origUserInfo,UserInfo newUserInfo) throws Exception{
		
	
		if(newUserInfo.userid == null || newUserInfo.userid.length() == 0){throw new Exception("Registration Info: userid length must be greater than 0");}
		if(newUserInfo.password == null || newUserInfo.password.length() == 0 || !newUserInfo.password.equals(newUserInfo.password2)){throw new Exception("Registration Info: both passwords must be the same and non-zero length");}
		if(newUserInfo.email == null || newUserInfo.email.length() == 0){throw new Exception("Registration Info: email length must be greater than 0");}
		if(newUserInfo.firstName == null || newUserInfo.firstName.length() == 0){throw new Exception("Registration Info: firstName length must be greater than 0");}
		if(newUserInfo.lastName == null || newUserInfo.lastName.length() == 0){throw new Exception("Registration Info: lastName length must be greater than 0");}
		if(newUserInfo.title == null || newUserInfo.title.length() == 0){throw new Exception("Registration Info: title length must be greater than 0");}
		if(newUserInfo.company == null || newUserInfo.company.length() == 0){throw new Exception("Registration Info: organization length must be greater than 0");}
		if(newUserInfo.address1 == null || newUserInfo.address1.length() == 0){throw new Exception("Registration Info: address1 length must be greater than 0");}
//		if(newUserInfo.address2 == null || newUserInfo.address2.length() == 0){throw new Exception("Registration Info: address2 length must be greater than 0");}
		if(newUserInfo.city == null || newUserInfo.city.length() == 0){throw new Exception("Registration Info: city length must be greater than 0");}
		if(newUserInfo.state == null || newUserInfo.state.length() == 0){throw new Exception("Registration Info: state length must be greater than 0");}
		if(newUserInfo.country == null || newUserInfo.country.length() == 0){throw new Exception("Registration Info: country length must be greater than 0");}
		if(newUserInfo.zip == null || newUserInfo.zip.length() == 0){throw new Exception("Registration Info: zip length must be greater than 0");}
		
		//Check Illegal characters
		if(hasIllegalCharacters(newUserInfo.userid)){throw new Exception("Registration Info: userid has illegal character '<>&\"");}
		if(hasIllegalCharacters(newUserInfo.password)){throw new Exception("Registration Info: password has illegal character '<>&\"");}
		if(hasIllegalCharacters(newUserInfo.email)){throw new Exception("Registration Info: email has illegal character '<>&\"");}
		if(hasIllegalCharacters(newUserInfo.firstName)){throw new Exception("Registration Info: firstName has illegal character '<>&\"");}
		if(hasIllegalCharacters(newUserInfo.lastName)){throw new Exception("Registration Info: lastName has illegal character '<>&\"");}
		if(hasIllegalCharacters(newUserInfo.title)){throw new Exception("Registration Info: title has illegal character '<>&\"");}
		if(hasIllegalCharacters(newUserInfo.company)){throw new Exception("Registration Info: organization has illegal character '<>&\"");}
		if(hasIllegalCharacters(newUserInfo.address1)){throw new Exception("Registration Info: address1 has illegal character '<>&\"");}
		if(newUserInfo.address2 != null && hasIllegalCharacters(newUserInfo.address2)){throw new Exception("Registration Info: address2 has illegal character '<>&\"");}
		if(hasIllegalCharacters(newUserInfo.city)){throw new Exception("Registration Info: city has illegal character '<>&\"");}
		if(hasIllegalCharacters(newUserInfo.state)){throw new Exception("Registration Info: state has illegal character '<>&\"");}
		if(hasIllegalCharacters(newUserInfo.country)){throw new Exception("Registration Info: country has illegal character '<>&\"");}
		if(hasIllegalCharacters(newUserInfo.zip)){throw new Exception("Registration Info: zip has illegal character '<>&\"");}

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
					columnNames, tableData,ListSelectionModel.SINGLE_SELECTION);
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
