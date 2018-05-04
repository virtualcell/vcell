package cbit.vcell.client;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.vcell.service.VCellServiceHelper;
import org.vcell.service.registration.RegistrationService;
import org.vcell.util.Compare;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.UserInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.LoginManager;
import cbit.vcell.desktop.RegistrationPanel;
import cbit.vcell.server.UserRegistrationOP;

public class UserRegistrationManager {
	
	private static RegistrationPanel registrationPanel = null;

	public static void registrationOperationGUI(final RequestManager requestManager, final DocumentWindowManager currWindowManager, 
				final ClientServerInfo currentClientServerInfo, final String userAction, final ClientServerManager clientServerManager) throws Exception{
			if(!(userAction.equals(LoginManager.USERACTION_REGISTER) ||
					userAction.equals(LoginManager.USERACTION_EDITINFO) ||
					userAction.equals(LoginManager.USERACTION_LOSTPASSWORD))){
				throw new IllegalArgumentException(UserRegistrationOP.class.getName()+".registrationOperationGUI:  Only New registration, Edit UserInfo or Lost Password allowed.");
			}
			if((userAction.equals(LoginManager.USERACTION_REGISTER) || userAction.equals(LoginManager.USERACTION_LOSTPASSWORD)) && clientServerManager != null){
				throw new IllegalArgumentException(UserRegistrationOP.class.getName()+".registrationOperationGUI:  Register New User Info requires clientServerManager null.");			
			}
			if(userAction.equals(LoginManager.USERACTION_EDITINFO) && clientServerManager == null){
				throw new IllegalArgumentException(UserRegistrationOP.class.getName()+".registrationOperationGUI:  Edit User Info requires clientServerManager not null.");			
			}
	
			RegistrationService registrationService = null;
			if(clientServerManager != null){
				registrationService = clientServerManager.getRegistrationProvider();
			} else {
				registrationService = VCellServiceHelper.getInstance().loadService(RegistrationService.class);
			}
			if(userAction.equals(LoginManager.USERACTION_LOSTPASSWORD)){
				if(currentClientServerInfo.getUsername() == null || currentClientServerInfo.getUsername().length() == 0){
					throw new IllegalArgumentException("Lost Password requires a VCell User Name.");
				}
				String result = PopupGenerator.showWarningDialog(currWindowManager, null,
						new UserMessage(
							"Sending Password via email for user '"+currentClientServerInfo.getUsername()+
							"'\nusing currently registered email address.",
							new String[] {"OK","Cancel"},"OK"),
						null);
				if(!result.equals("OK")){
					throw UserCancelException.CANCEL_GENERIC;
				}
				registrationService.sendLostPassword(currentClientServerInfo.getUsername());
				return;
			}
			
			final RegistrationService finalRegistrationProvider = registrationService;
			final String ORIGINAL_USER_INFO_HOLDER = "originalUserInfoHolder";
			//final String DIGESTED_USERIDS_KEY = "DIGESTED_USERIDS_KEY";
			AsynchClientTask gatherInfoTask = new AsynchClientTask("gathering user info for updating", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
	
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					if(userAction.equals(LoginManager.USERACTION_EDITINFO)){
						UserInfo originalUserInfoHolder = finalRegistrationProvider.getUserInfo(clientServerManager.getUser().getID());
						hashTable.put(ORIGINAL_USER_INFO_HOLDER, originalUserInfoHolder);
					}
				}			
			};
			
			final String NEW_USER_INFO_KEY = "NEW_USER_INFO_KEY";
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
					UserInfo originalUserInfoHolder = (UserInfo)hashTable.get(ORIGINAL_USER_INFO_HOLDER);;
					if(userAction.equals(LoginManager.USERACTION_EDITINFO) && originalUserInfoHolder != null) {
						registrationPanel.setUserInfo(originalUserInfoHolder,true);					
					}
					do {
						int result = DialogUtils.showComponentOKCancelDialog(currWindowManager.getComponent(), registrationPanel,
									(userAction.equals(LoginManager.USERACTION_REGISTER)?"Create New User Registration":"Update Registration Information ("+clientServerManager.getUser().getName()+")"));
						if (result != JOptionPane.OK_OPTION) {
							throw UserCancelException.CANCEL_GENERIC;
						}
						UserRegistrationOP.NewPasswordUserInfo newUserInfo = registrationPanel.getUserInfo();
						
						if(userAction.equals(LoginManager.USERACTION_EDITINFO)){
							//User editing registration info but did not enter new clear text password in gui,
							//set existing digestPassword
							if(newUserInfo.digestedPassword0 == null && originalUserInfoHolder.digestedPassword0 != null){
								newUserInfo.digestedPassword0 = originalUserInfoHolder.digestedPassword0;
							}
							if(newUserInfo.otherDigestedPassword == null && originalUserInfoHolder.digestedPassword0 != null){
								newUserInfo.otherDigestedPassword = originalUserInfoHolder.digestedPassword0;
							}						
						}
				
						try {
							if(!checkUserInfo(currWindowManager, originalUserInfoHolder,newUserInfo,userAction)){
								PopupGenerator.showInfoDialog(currWindowManager, "No registration information has changed.");
								continue;
							}
						} catch (UserCancelException ex) {
							continue;
						} catch (Exception ex) {
							PopupGenerator.showErrorDialog(currWindowManager, ex.getMessage());
							continue;
						}
						hashTable.put(NEW_USER_INFO_KEY, newUserInfo);
						break;
					} while (true);
				}
			};
			
	//		final String USERID_NOT_UNIQUE = "USERID_NOT_UNIQUE";
			AsynchClientTask updateDbTask = new AsynchClientTask(userAction.equals(LoginManager.USERACTION_REGISTER)?"registering new user":"updating user info", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
	
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					UserInfo newUserInfo = (UserInfo)hashTable.get(NEW_USER_INFO_KEY);
	//				if(userAction.equals(LoginManager.USERACTION_REGISTER)){
	//					//Check userid already exists
	//					if(!finalRegistrationProvider.isUserIdUnique(newUserInfo.userid)){
	//						throw UserCancelException.createCustomUserCancelException(USERID_NOT_UNIQUE);
	//					}
	//				}
					try {					
						UserInfo registeredUserInfo = finalRegistrationProvider.insertUserInfo(newUserInfo,(userAction.equals(LoginManager.USERACTION_EDITINFO)?true:false));
						hashTable.put("registeredUserInfo", registeredUserInfo);
					}catch (UseridIDExistsException e) {
						throw e;
					}catch (Exception e) {
						e.printStackTrace();
						throw new Exception("Error " 
								+ (userAction.equals(LoginManager.USERACTION_REGISTER)?"registering new user" :"updating user info ") +  " ("+newUserInfo.userid+"), "
								+ e.getMessage());
					}
				}			
			};
			
			AsynchClientTask connectTask = new AsynchClientTask("user logging in", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
	
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					UserInfo registeredUserInfo = (UserInfo)hashTable.get("registeredUserInfo");
					try {					
						if (userAction.equals(LoginManager.USERACTION_REGISTER)) {
							try{
								ClientServerInfo newClientServerInfo = VCellClient.createClientServerInfo(currentClientServerInfo, registeredUserInfo.userid, registeredUserInfo.digestedPassword0);
								requestManager.connectToServer(currWindowManager, newClientServerInfo);
							}finally{
								ConnectionStatus connectionStatus = requestManager.getConnectionStatus();
								if(connectionStatus.getStatus() != ConnectionStatus.CONNECTED){
									PopupGenerator.showErrorDialog(currWindowManager, "Automatic login of New user '"+registeredUserInfo.userid+"' failed.\n"+
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
			
			AsynchClientTask useridErrorTask = new AsynchClientTask("re-enter userid...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING,false,false) {
				@Override
				public void run(final Hashtable<String, Object> hashTable) throws Exception {
					if(hashTable.containsKey(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR)){
						//retry if requested
						if(hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR) instanceof IllegalArgumentException){
							//Exception handled here, suppress ClientTaskDispatcher error dialog.
							hashTable.remove(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR);
							UserInfo newUserInfo = (UserInfo)hashTable.get(NEW_USER_INFO_KEY);
							PopupGenerator.showErrorDialog(currWindowManager, "Login ID '"+newUserInfo.userid+"' cannot be used, enter a different one.");
							//Use thread to restart registration process again
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										registrationOperationGUI(requestManager, currWindowManager, currentClientServerInfo, userAction, clientServerManager);
									} catch (Exception e) {
										e.printStackTrace();
										DialogUtils.showErrorDialog(currWindowManager.getComponent(), e.getMessage());
									}	
								}
							}).start();
						}									
					}
				}
			};
			ClientTaskDispatcher.dispatch(currWindowManager.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {gatherInfoTask, showPanelTask, updateDbTask, connectTask,useridErrorTask}, false);
		}

	private static boolean checkUserInfo(DocumentWindowManager currWindowManager, UserInfo origUserInfo,UserRegistrationOP.NewPasswordUserInfo newUserInfo,String userAction) throws Exception{
		TokenMangler.checkLoginID(newUserInfo.userid);
		boolean bEditing = userAction.equals(LoginManager.USERACTION_EDITINFO);
		String emptyMessge = " can not be empty";	
		if(newUserInfo.userid == null || newUserInfo.userid.length() == 0){throw new Exception("Registration Info: userid" + emptyMessge);}
		if(newUserInfo.digestedPassword0 == null || !newUserInfo.digestedPassword0.equals(newUserInfo.otherDigestedPassword)){
			if(bEditing){
				throw new Exception("Registration Info: password fields don't match, clear both to keep existing password or enter same text in both to create new password");
			}else{
				throw new Exception("Registration Info: both passwords must be the same and" + emptyMessge);
			}
		}
		if(newUserInfo.email == null || newUserInfo.email.length() == 0 || newUserInfo.email.indexOf("@") < 0){throw new Exception("please type in a valid email address.");}
		if(newUserInfo.wholeName == null || newUserInfo.wholeName.length() == 0){throw new Exception("Registration Info: Name" + emptyMessge);}
		
		if(newUserInfo.title != null && newUserInfo.title.length() == 0){
			newUserInfo.title = null;
		}
		if(newUserInfo.company != null && newUserInfo.company.length() == 0){
			newUserInfo.company = null;
		}
		String hasIllegalMessage = " has illegal character '<>&\"";
		//Check Illegal characters
		if(UserRegistrationOP.hasIllegalCharacters(newUserInfo.userid)){throw new Exception("Registration Info: userid" + hasIllegalMessage);}
		if(UserRegistrationOP.hasIllegalCharacters(newUserInfo.email)){throw new Exception("Registration Info: email" + hasIllegalMessage);}
		if(UserRegistrationOP.hasIllegalCharacters(newUserInfo.wholeName)){throw new Exception("Registration Info: firstName" + hasIllegalMessage);}
		
		if(newUserInfo.title!=null && UserRegistrationOP.hasIllegalCharacters(newUserInfo.title)){throw new Exception("Registration Info: title" + hasIllegalMessage);}
		if(newUserInfo.company!=null && UserRegistrationOP.hasIllegalCharacters(newUserInfo.company)){throw new Exception("Registration Info: organization" + hasIllegalMessage);}
		if(newUserInfo.country!=null && UserRegistrationOP.hasIllegalCharacters(newUserInfo.country)){throw new Exception("Registration Info: country" + hasIllegalMessage);}
	
		if(origUserInfo != null){
			String[] columnNames = new String[] {"Field","Original","New Value"};
			Vector<String[]> tableRow = new Vector<String[]>();
			if(!newUserInfo.userid.equals(origUserInfo.userid)){tableRow.add(new String[] {"userid",origUserInfo.userid,newUserInfo.userid});}
			if(!newUserInfo.digestedPassword0.equals(origUserInfo.digestedPassword0)){tableRow.add(new String[] {"password","---","changed"});}
			if(!newUserInfo.email.equals(origUserInfo.email)){tableRow.add(new String[] {"email",origUserInfo.email,newUserInfo.email});}
			if(!newUserInfo.wholeName.equals(origUserInfo.wholeName)){tableRow.add(new String[] {"firstName",origUserInfo.wholeName,newUserInfo.wholeName});}
			
			if(!Compare.isEqualOrNull(newUserInfo.title, origUserInfo.title)){tableRow.add(new String[] {"title",origUserInfo.title,newUserInfo.title});}
			if(!Compare.isEqualOrNull(newUserInfo.company, origUserInfo.company)){tableRow.add(new String[] {"organization",origUserInfo.company,newUserInfo.company});}
			if(!Compare.isEqualOrNull(newUserInfo.country, origUserInfo.country)){tableRow.add(new String[] {"country",origUserInfo.country,newUserInfo.country});}
			if(!(newUserInfo.notify == origUserInfo.notify)){tableRow.add(new String[] {"notify",origUserInfo.notify+"",newUserInfo.notify+""});}
	
			if(tableRow.size() > 0){
				String[][] tableData = new String[tableRow.size()][];
				tableRow.copyInto(tableData);
				DialogUtils.showComponentOKCancelTableList(
					currWindowManager.getComponent(), "Confirm Registration Info Changes",
					columnNames, tableData,null);
				return true;
			}
			return false;
		}else{
			return true;
		}
	}


}
